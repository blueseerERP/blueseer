/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.blueseer.shp;

import com.blueseer.far.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags; 
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData.getCustInfo;
import static com.blueseer.far.farData.addArTransaction;
import com.blueseer.far.farData.ar_mstr;
import com.blueseer.far.farData.ard_mstr;
import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import static com.blueseer.shp.shpData.addShipperTransaction;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author vaughnte
 */
public class ShipperMaintSerial extends javax.swing.JPanel implements IBlueSeer {

    // global variable declarations
                boolean isLoad = false;
                String terms = "";
                String aracct = "";
                String arcc = "";
                String arbank = "";
                double actamt = 0.00;
                double baseamt = 0.00;
                double rcvamt = 0.00;
                String curr = "";
                String basecurr = "";
                int j = 0;
                
    
    // global datatablemodel declarations 
    javax.swing.table.DefaultTableModel inventorymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("serial"), 
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("expiredate"),
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("uom"),
                getGlobalColumnTag("price")});
    ShipTableModel shipmodel = new ShipTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("serial"), 
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("uom"),
                getGlobalColumnTag("price"),
                getGlobalColumnTag("bom")
            });
    
    class ShipTableModel extends DefaultTableModel {  
      
        public ShipTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }
        public boolean isCellEditable(int rowIndex, int columnIndex) {
             boolean[] canEdit = new boolean[]{false, false, false, false, false, false, true, false, false, false}; 
            return canEdit[columnIndex];
        }
   
        /*
        public Class getColumnClass(int column) {
               if (column == 6 || column == 7)       
                return Double.class; 
            else return String.class;  //other columns accept String values 
        }
       
        */
        
   }    
    
    
    javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if ((tme.getType() == TableModelEvent.UPDATE) && (tme.getColumn() == 6 )) {
                            sumdollars();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };            
  
    public ShipperMaintSerial() {
        initComponents();
        setLanguageTags(this);     
    }
   
    // interface functions implemented
    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(String type, String[] key) { 
              this.type = type;
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "add":
                    message = addRecord(key);
                    break;
                case "update":
                    message = updateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
                    break;
                case "get":
                    message = getRecord(key);    
                    break;    
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
           if (this.type.equals("delete")) {
             initvars(null);  
           } else if (this.type.equals("get") && message[0].equals("1")) {
             tbkey.requestFocus();
           } else if (this.type.equals("get") && message[0].equals("0")) {
             tbkey.requestFocus();
           } else if (this.type.equals("add") && message[0].equals("0")) {
             initvars(key);
           } else {
             initvars(null);  
           }
           
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
   
    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                if (component instanceof JScrollPane) {
                    setPanelComponentState((JScrollPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    
                    component.setEnabled(b);
                    
                }
            }
            if (scrollpane != null) {
                scrollpane.setEnabled(b);
                JViewport viewport = scrollpane.getViewport();
                Component[] componentspane = viewport.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    component.setEnabled(b);
                }
            }
    } 
    
    public void setLanguageTags(Object myobj) {
       JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
       Component[] components = panel.getComponents();
       for (Component component : components) {
           if (component instanceof JPanel) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".panel." + component.getName())) {
                       ((JPanel) component).setBorder(BorderFactory.createTitledBorder(tags.getString(this.getClass().getSimpleName() +".panel." + component.getName())));
                    } 
                    setLanguageTags((JPanel) component);
                }
                if (component instanceof JLabel ) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JLabel) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
       }
    }
     
    public void setComponentDefaultValues() {
       isLoad = true;
        tbkey.setText("");
         terms = "";
         aracct = "";
         arcc = "";
         arbank = "";
         actamt = 0.00;
         rcvamt = 0.00;
        
        lbcust.setText("");
        lbmessage.setText("");
        lbmessage.setForeground(Color.blue);
                
        tbrmks.setText("");
      
        tbref.setText("");
        tbtotal.setText("0");
        tbtotal.setBackground(Color.white);
        tbtotal.setEditable(false);
        inventorymodel.setRowCount(0);
        shipmodel.setRowCount(0);
        shipmodel.addTableModelListener(ml);
        inventorydet.setModel(inventorymodel);
        shipdet.setModel(shipmodel);
        
       
        
        java.util.Date now = new java.util.Date();
        dcdate.setDate(now);
              
        ddcust.removeAllItems();
        ArrayList mycust = cusData.getcustmstrlist();
        for (int i = 0; i < mycust.size(); i++) {
            ddcust.addItem(mycust.get(i));
        }
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        ddship.removeAllItems();
        
        
        ddsite.removeAllItems();
        ArrayList mylist = OVData.getSiteList();
        for (int i = 0; i < mylist.size(); i++) {
            ddsite.addItem(mylist.get(i));
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
      
        dditem.removeAllItems();
        ArrayList<String> items = invData.getItemMasterListBySite(ddsite.getSelectedItem().toString()); 
        for (String item : items) {
        dditem.addItem(item);
        }  
        dditem.insertItemAt("", 0);
        dditem.setSelectedIndex(0);
        
         
        
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btPrintInv.setEnabled(false);
        btPrintShp.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public String[] setAction(int i) {
        String[] m = new String[2];
        if (i > 0) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   
                   tbtotal.setText(currformatDouble(actamt));
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
        return m;
    }
    
    public boolean validateInput(String x) {
        boolean b = true;
                String line = checkqty();
                if (! line.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1169,line));
                    return b;  
                }
        
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    ddsite.requestFocus();
                    return b;
                }
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
                    return b;
                }
                
                if (arbank.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1128));
                    return b;
                }
                if (arcc.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1129));
                    return b;
                }
                if (aracct.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1130));
                    return b;
                }
                
                
                
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask("get",arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
        String[] m = addShipperTransaction(createDetRecord(), createRecord());
        shpData.updateShipperSAC(tbkey.getText());
        confirmShipperTransaction("", tbkey.getText(), dcdate.getDate());
        return m;
    }
     
    public String[] updateRecord(String[] x) {
     String[] m = new String[]{BlueSeerUtils.ErrorBit, "This update functionality is not implemented at this time"};
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[]{BlueSeerUtils.ErrorBit, "This delete functionality is not implemented at this time"};
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
        
       try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                int d = 0;
                res = st.executeQuery("select * from ship_mstr where sh_id = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                  i++;
                     tbkey.setText(res.getString("sh_id"));
                     dcdate.setDate(parseDate(res.getString("sh_shipdate")));
                     tbref.setText(res.getString("sh_ref"));
                     tbrmks.setText(res.getString("sh_rmks"));
                     ddcust.setSelectedItem(res.getString("sh_cust"));
                     ddship.setSelectedItem(res.getString("sh_ship"));
                     ddsite.setSelectedItem(res.getString("sh_site"));
                }
                
                res = st.executeQuery("select * from ship_det where shd_id = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                // line, item, desc, serial, warehouse, loc, qty, price, bom
                     shipmodel.addRow(new Object[] { res.getString("shd_line"),
                                              res.getString("shd_item"),
                                              res.getString("shd_desc"),
                                              res.getString("shd_serial"),
                                              res.getString("shd_wh"),
                                              res.getString("shd_loc"),
                                              res.getString("shd_qty"),
                                              res.getString("shd_netprice"),
                                              res.getString("shd_bom")
                                              });
                 
                  
                  actamt += (res.getDouble("shd_qty") * res.getDouble("shd_netprice"));
                d++;
                }
               
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError};  
        }
      return m;
    }
   
    public shpData.ship_mstr createRecord() {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       
        
        shpData.ship_mstr x = new shpData.ship_mstr(null, 
                tbkey.getText(),
                ddcust.getSelectedItem().toString(),
                ddship.getSelectedItem().toString(),
                "0", // pallets
                "0", // boxes
                "", // shipvia  
                dfdate.format(dcdate.getDate()),
                null, // po date
                tbref.getText().replace("'", ""),
                "", // po number
                tbrmks.getText(),
                bsmf.MainFrame.userid,
                ddsite.getSelectedItem().toString(),
                curr,
                "", // wh
                terms,
                "", // taxcode
                aracct,
                arcc,
                "S", // type
                "", // sh_so 
                ddsite.getSelectedItem().toString());
                
        return x;        
    }
    
    public ArrayList<shpData.ship_det> createDetRecord() {
        ArrayList<shpData.ship_det> list = new ArrayList<shpData.ship_det>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // line, item, order, orderline, po, qty, netprice, desc, wh, loc, disc, listprice, tax, cont, serial
        for (int j = 0; j < shipdet.getRowCount(); j++) { 
            shpData.ship_det x = new shpData.ship_det(null, 
                tbkey.getText(), // shipper
                shipdet.getValueAt(j, 0).toString(), //shline
                shipdet.getValueAt(j, 1).toString(), // item
                shipdet.getValueAt(j, 1).toString(), // custimtem
                "",  // order
                String.valueOf(j + 1), //soline    
                dfdate.format(dcdate.getDate()),
                "", // po
                shipdet.getValueAt(j, 6).toString().replace(defaultDecimalSeparator, '.'), // qty
                shipdet.getValueAt(j, 7).toString(), //uom
                curr, //currency
                shipdet.getValueAt(j, 8).toString().replace(defaultDecimalSeparator, '.'), // net price
                "0", // disc
                shipdet.getValueAt(j, 8).toString().replace(defaultDecimalSeparator, '.'), // list price
                shipdet.getValueAt(j, 2).toString(), // desc
                shipdet.getValueAt(j, 4).toString(), // wh
                shipdet.getValueAt(j, 5).toString(), // loc
                "0", // taxamt
                "0", // cont
                "", // ref
                shipdet.getValueAt(j, 3).toString(), // serial   
                ddsite.getSelectedItem().toString(),
                shipdet.getValueAt(j, 9).toString() // bom
                );
        list.add(x);
        }      
        return list;        
    }
    
     public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getShipperBrowseUtil(luinput.getText(),0, "sh_id");
        } else {
         luModel = DTData.getShipperBrowseUtil(luinput.getText(),0, "sh_cust");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), 
                getClassLabelTag("lblcust", this.getClass().getSimpleName())); 
        
    }

    
    // custom funcs      
    public void setcustvariables(String cust) {
       
        // aracct, arcc, currency, bank, terms, carrier, onhold, site
            String[] custinfo = getCustInfo(cust);
            aracct = custinfo[0];
            arcc = custinfo[1];
            terms = custinfo[4];
            arbank = custinfo[3];
            curr = custinfo[2];
            ddship.removeAllItems();
            ArrayList<String> shiptos = cusData.getcustshipmstrlist(ddcust.getSelectedItem().toString());
            for (int i = 0; i < shiptos.size(); i++) {
                ddship.addItem(shiptos.get(i));
            }
       
    }
      
    public void setstatus(javax.swing.JTable mytable) {
            try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
             
             String sonbr = null;
             int i = 0;
             int qty = 0;
             boolean iscomplete = true;
             String sodstatus = "";
             
        // find the sod record for each line / So pair
        for (int j = 0; j < mytable.getRowCount(); j++) {
               //    mytable.getModel().getValueAt(j, 0);  
             i = 0;
             String thispart = mytable.getModel().getValueAt(j, 0).toString();
             String thisorder = mytable.getModel().getValueAt(j, 1).toString();
             String thispo = mytable.getModel().getValueAt(j, 2).toString();
             double thisrecvqty = Double.valueOf(mytable.getModel().getValueAt(j, 3).toString());
             String thislinestatus = "";
             double thisrecvpedtotal = 0;
            
             try {
            
                 /* ok....let's get the current state of this line item on the sales order */
                 res = st.executeQuery("select * from sod_det where sod_nbr = " + "'" + thisorder + "'" + 
                                     " AND sod_item = " + "'" + thispart + "'" + ";");
               while (res.next()) {     
                 i++;
                   if (Double.valueOf(res.getString("sod_recvped_qty") + thisrecvqty) < Double.valueOf(res.getString("sod_ord_qty")) ) {
                   thislinestatus = "Partial"; 
                   }
                   if (Double.valueOf(res.getString("sod_recvped_qty") + thisrecvqty) >= Double.valueOf(res.getString("sod_ord_qty")) ) {
                   thislinestatus = "Shipped"; 
                   }
                   thisrecvpedtotal = thisrecvqty + Double.valueOf(res.getString("sod_recvped_qty"));
                   
                }
                 
                 /* ok...now lets update the status of this sod_det line item */
                 st.executeUpdate("update sod_det set sod_recvped_qty = " + "'" + thisrecvpedtotal + "'" + 
                                  "," + " sod_status = " + "'" + thislinestatus + "'" +
                                  " where sod_nbr = " + "'" + thisorder + "'" + 
                                  " and sod_item = " + "'" + thispart + "'" + 
                                  " and sod_po = " + "'" + thispo + "'" +
                     ";");
                 
             } catch (SQLException s) {
                 MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
             } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
             }
         }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void sumdollars() {
       
       
        actamt = 0;
         for (int j = 0; j < shipdet.getRowCount(); j++) {
             actamt += ( bsParseDouble(shipdet.getModel().getValueAt(j,6).toString()) * bsParseDouble(shipdet.getModel().getValueAt(j,8).toString()) );
         }
        
        tbtotal.setText(currformatDouble(actamt));
        
    }
    
    public String checkqty() {
        String x = "";
        for (int j = 0; j < shipdet.getRowCount(); j++) {
            if (bsParseDouble(shipdet.getModel().getValueAt(j,6).toString()) > invData.getItemQOHBySerial(shipdet.getModel().getValueAt(j,1).toString(), ddsite.getSelectedItem().toString(), shipdet.getModel().getValueAt(j,3).toString()) ) {
             return shipdet.getModel().getValueAt(j,0).toString();   
            }
         }
        return x;
    }
    
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < shipdet.getRowCount(); j++) {
            current = Integer.valueOf(shipdet.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
        
    public void getItemInfo(String item) {
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               int i = 0;
                    
                res = st.executeQuery("select * from in_mstr inner join item_mstr on it_item = in_item where in_item = " + "'" + item + "'" 
                        + ";");
                while (res.next()) {
                    i++;
                    inventorymodel.addRow(new Object[]{
                      res.getString("in_item"), 
                      res.getString("it_desc"),  
                      res.getString("in_serial"), 
                      res.getString("in_wh"), 
                      res.getString("in_loc"), 
                      res.getString("in_expire"),
                      currformatDouble(res.getDouble("in_qoh")), 
                      res.getString("it_uom"), 
                      res.getString("it_sell_price")});
                    if (i == 1) {
                        lbitemdesc.setText(res.getString("it_desc"));
                    }
                    
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        shipdet = new javax.swing.JTable();
        ddcust = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        inventorydet = new javax.swing.JTable();
        tbref = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbtotal = new javax.swing.JTextField();
        tbrmks = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        lbmessage = new javax.swing.JLabel();
        lbcust = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        dditem = new javax.swing.JComboBox<>();
        lbitemdesc = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ddship = new javax.swing.JComboBox<>();
        lbship = new javax.swing.JLabel();
        btPrintInv = new javax.swing.JButton();
        btPrintShp = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Shipper Maintenance (Serialized Inventory)"));
        jPanel1.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("Number");
        jLabel24.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel36.setText("Billto");
        jLabel36.setName("lblbillto"); // NOI18N

        btadditem.setText("Add Item");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        shipdet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane7.setViewportView(shipdet);

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Del Item");
        btdeleteitem.setName("btdeleteitem"); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel35.setText("EffDate");
        jLabel35.setName("lbleffdate"); // NOI18N

        inventorydet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane8.setViewportView(inventorydet);

        jLabel2.setText("reference");
        jLabel2.setName("lblref"); // NOI18N

        jLabel4.setText("Rmks");
        jLabel4.setName("lblremarks"); // NOI18N

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        jLabel37.setText("Shipto");
        jLabel37.setName("lblshipto"); // NOI18N

        jLabel38.setText("Site");
        jLabel38.setName("lblsite"); // NOI18N

        jLabel5.setText("Total");
        jLabel5.setName("lbltotal"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        dditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemActionPerformed(evt);
            }
        });

        jLabel3.setText("Item");
        jLabel3.setName("lblitem"); // NOI18N

        ddship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddshipActionPerformed(evt);
            }
        });

        btPrintInv.setText("Print Invoice");
        btPrintInv.setName("btprintinvoice"); // NOI18N
        btPrintInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintInvActionPerformed(evt);
            }
        });

        btPrintShp.setText("Print Shipper");
        btPrintShp.setName("btprintshipper"); // NOI18N
        btPrintShp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintShpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeleteitem))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addGap(9, 9, 9)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel24)
                                        .addComponent(jLabel36)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel37)
                                        .addComponent(jLabel38)
                                        .addComponent(jLabel3))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(tbrmks)
                                            .addGap(215, 215, 215))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(33, 33, 33)
                                                    .addComponent(btnew)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(btclear))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(lbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(ddship, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(ddsite, javax.swing.GroupLayout.Alignment.LEADING, 0, 119, Short.MAX_VALUE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(lbship, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                            .addGap(8, 8, 8)
                                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)))
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(tbtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addGap(149, 149, 149))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lbmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(0, 105, Short.MAX_VALUE))))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(lbitemdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, Short.MAX_VALUE)))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(192, 192, 192)
                        .addComponent(btPrintInv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btPrintShp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnew)
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel24)
                                        .addComponent(btclear))
                                    .addComponent(btlookup))
                                .addGap(8, 8, 8)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel36)
                                    .addComponent(lbcust, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel37)
                                    .addComponent(ddship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lbship, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lbmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(tbtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35))
                        .addGap(9, 9, 9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbitemdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(25, 25, 25)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdeleteitem)
                    .addComponent(btadditem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete)
                    .addComponent(btPrintInv)
                    .addComponent(btPrintShp))
                .addGap(35, 35, 35))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("shipper");
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
     
        int[] rows = inventorydet.getSelectedRows();
        int line = 0;
        line = getmaxline();
        for (int i : rows) {
           line++;   
          
                        
           shipmodel.addRow(new Object[] { 
                String.valueOf(line),
                inventorydet.getModel().getValueAt(i, 0),
                inventorydet.getModel().getValueAt(i, 1), 
                inventorydet.getModel().getValueAt(i, 2),
                inventorydet.getModel().getValueAt(i, 3),
                inventorydet.getModel().getValueAt(i, 4),
                inventorydet.getModel().getValueAt(i, 6),
                inventorydet.getModel().getValueAt(i, 7),
                inventorydet.getModel().getValueAt(i, 8),
                "" // bom
                });
        }
        
        sumdollars();
        
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed
        // clean slate
        inventorymodel.setRowCount(0);
        shipmodel.setRowCount(0);
        lbcust.setText("");
        if ( ddcust.getSelectedItem() != null && ! ddcust.getSelectedItem().toString().isEmpty()  && ! isLoad) {
        lbcust.setText(cusData.getCustName(ddcust.getSelectedItem().toString()));
        setcustvariables(ddcust.getSelectedItem().toString());
        }
    }//GEN-LAST:event_ddcustActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = shipdet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
             actamt -= bsParseDouble(shipdet.getModel().getValueAt(i,1).toString());
            ((javax.swing.table.DefaultTableModel) shipdet.getModel()).removeRow(i);
        }
        tbtotal.setText(String.valueOf(actamt));
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddsiteActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         if (! validateInput("deleteRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("delete", new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void dditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemActionPerformed
         if (dditem.getSelectedItem() != null && ! isLoad)
        getItemInfo(dditem.getSelectedItem().toString());
    }//GEN-LAST:event_dditemActionPerformed

    private void ddshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshipActionPerformed
       if (! isLoad && ddship != null && ddship.getItemCount() > 0)  {
        lbship.setText(cusData.getShipName(ddcust.getSelectedItem().toString(), ddship.getSelectedItem().toString()));
       }
    }//GEN-LAST:event_ddshipActionPerformed

    private void btPrintInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintInvActionPerformed
        OVData.printInvoice(tbkey.getText(), true);
    }//GEN-LAST:event_btPrintInvActionPerformed

    private void btPrintShpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintShpActionPerformed
        OVData.printShipper(tbkey.getText());
        // OVData.printJTableToJasper("Shipper Report", tabledetail );
    }//GEN-LAST:event_btPrintShpActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btPrintInv;
    private javax.swing.JButton btPrintShp;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox<String> dditem;
    private javax.swing.JComboBox<String> ddship;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JTable inventorydet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbcust;
    private javax.swing.JLabel lbitemdesc;
    private javax.swing.JLabel lbmessage;
    private javax.swing.JLabel lbship;
    private javax.swing.JTable shipdet;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrmks;
    private javax.swing.JTextField tbtotal;
    // End of variables declaration//GEN-END:variables
}
