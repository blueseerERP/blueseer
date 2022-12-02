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
package com.blueseer.ord;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import static com.blueseer.ord.ordData.addServiceOrderTransaction;
import static com.blueseer.ord.ordData.getServiceOrderLines;
import com.blueseer.ord.ordData.sv_mstr;
import com.blueseer.ord.ordData.svd_det;
import static com.blueseer.ord.ordData.updateServiceOrderTransaction;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
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
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeer;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
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


/**
 *
 * @author vaughnte
 */
public class ServiceOrderMaint extends javax.swing.JPanel implements IBlueSeer {

     // global variable declarations
                boolean isLoad = false;
    
   // global datatablemodel declarations    
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("price"), 
                getGlobalColumnTag("uom")
            })
            {
                    boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, true, true, true
                };

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                   canEdit = new boolean[]{false, false, false, false, false, true, true, true}; 
                return canEdit[columnIndex];
                }
            };
    
     javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 5 || tme.getColumn() == 6 )) {
                            setTotalHours();
                            setTotalPrice();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };  
    
    public ServiceOrderMaint() {
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
           } else if (this.type.equals("update") && message[0].equals("0")) {
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
       
        buttonGroup1.add(rbservice);
        buttonGroup1.add(rbinventory);
       
        cbpaid.setSelected(false);
     //   rbinventory.setEnabled(false);
       
        tbkey.setText("");
        tbhours.setText("0");
        tbprice.setText("0.00");  
        lblcustname.setText("");
        lblshipname.setText("");
        duedate.setDate(new java.util.Date());
        createdate.setDate(new java.util.Date());
         remarks.setText("");
        tbtotqty.setText("");
        totlines.setText("");
        tbtotdollars.setText("");
        
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
        myorddetmodel.addTableModelListener(ml);
         
        ddtype.setSelectedIndex(0);
        
        ddsite.removeAllItems();
        ArrayList<String> site = OVData.getSiteList();
        for (int i = 0; i < site.size(); i++) {
            ddsite.addItem(site.get(i));
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddcust.removeAllItems();
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        ArrayList mycusts = cusData.getcustmstrlist();
        for (int i = 0; i < mycusts.size(); i++) {
            ddcust.addItem(mycusts.get(i));
        }
        ddship.removeAllItems();
        
        
         dduom.removeAllItems();
        dduom.insertItemAt("", 0);
         dduom.setSelectedIndex(0);
        ArrayList<String> uoms = OVData.getUOMList();
        for (String code : uoms) {
            dduom.addItem(code);
        }
        
        
         dditem.removeAllItems();
         ArrayList<String> items = invData.getItemMasterAlllist();
         for (String item : items) {
         dditem.addItem(item);
         }
          dditem.insertItemAt("", 0);
         dditem.setSelectedIndex(0);
         
         
        ddstatus.removeAllItems();
        ArrayList<String> mylist = OVData.getCodeMstr("orderstatus");
        for (int i = 0; i < mylist.size(); i++) {
            ddstatus.addItem(mylist.get(i));
        }
        ddstatus.setSelectedItem(getGlobalProgTag("open"));
         
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btinvoice.setEnabled(false);
        btquotetoorder.setEnabled(false);
        btprint.setEnabled(false);
        btnew.setEnabled(false);
        btadd.setEnabled(false); // set disabled until line item in detail
        ddstatus.setSelectedItem("open");
        ddstatus.setEnabled(false);
        if (OVData.isSRVMQuoteType()) {
            ddtype.setSelectedItem("quote");
        } else {
            ddtype.setSelectedItem("order");
        }
        if (OVData.isSRVMItemType()) {
            rbservice.setSelected(true);
             rbinventory.setSelected(false);
        } else {
            rbservice.setSelected(false);
             rbinventory.setSelected(true);
        }
        
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        
    }
    
    public String[] setAction(int i) {
        String[] m = new String[2];
        if (i > 0) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   
                   
                setTotalHours();
                setTotalPrice();
                sumlinecount();
                        if (ddstatus.getSelectedItem().toString().compareTo(getGlobalProgTag("closed")) == 0 || ddstatus.getSelectedItem().toString().compareTo("void") == 0) {
                             setPanelComponentState(this, false);
                             btnew.setEnabled(true);
                             btlookup.setEnabled(true);
                             btclear.setEnabled(true);
                             btprint.setEnabled(true);
                             m = new String[]{BlueSeerUtils.SuccessBit, getMessageTag(1097)};  
                         } else {
                             setPanelComponentState(this, true);
                              btadd.setEnabled(false);
                         }
                         
                         ddtype.setEnabled(false);
                         
                         if (ddtype.getSelectedItem().toString().compareTo("order") == 0) {
                             btquotetoorder.setEnabled(false);
                             btquotetoorder.setBackground(null);
                         } else {
                             btquotetoorder.setBackground(Color.yellow);
                         }
                 rbservice.setSelected(true); // set this to toggle item versus service
                         
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
        return m;
    }
    
    public boolean validateInput(String key) {
        
        Map<String,Integer> f = OVData.getTableInfo("sv_mstr");
        int fc;

        fc = checkLength(f,"sv_po");
        if (tbpo.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbpo.requestFocus();
            return false;
        }  
        
        fc = checkLength(f,"sv_rmks");
        if (remarks.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            remarks.requestFocus();
            return false;
        }  
        
        if (ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024,""));
            return false;
        } 
        if (ddship.getSelectedItem() == null || ddship.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024,""));
            return false;
        } 
        return true;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setPanelComponentState(this, false); // having to run twice to go deeper into components
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
    
    public String[] getRecord(String[] x) {
        String[] m = new String[2];
        myorddetmodel.setRowCount(0);
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
                res = st.executeQuery("select * from sv_mstr where sv_nbr = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                    i++;
                    tbkey.setText(x[0]);
                    tbkey.setEnabled(false);
                    remarks.setText(res.getString("sv_rmks"));
                    ddcust.setSelectedItem(res.getString("sv_cust"));
                    ddship.setSelectedItem(res.getString("sv_ship"));
                    ddtype.setSelectedItem(res.getString("sv_type"));
                    ddstatus.setSelectedItem(res.getString("sv_status"));
                    ddcrew.setSelectedItem(res.getString("sv_crew"));
                    duedate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sv_due_date")));
                    createdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sv_create_date")));
                }
               
                
                 // get detail
                res = st.executeQuery("select * from svd_det where svd_nbr = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                  myorddetmodel.addRow(new Object[]{res.getString("svd_line"), res.getString("svd_item"),
                      res.getString("svd_type"), res.getString("svd_desc"), res.getString("svd_nbr"),
                      res.getString("svd_qty").replace('.', defaultDecimalSeparator), res.getString("svd_netprice").replace('.', defaultDecimalSeparator), res.getString("svd_uom")});
                }
               
               
                 // set Action if Record found (i > 0)
                m = setAction(i);
               

            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
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
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError};  
        }
      return m;

    }
    
    public String[] addRecord(String[] x) {
     String[] m = addServiceOrderTransaction(createDetRecord(), createRecord());
     return m;
    } 
    
    public String[] updateRecord(String[] x) {
               String[] m = new String[2];
        // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        
        lines = getServiceOrderLines(tbkey.getText());
       for (String line : lines) {
          goodLine = false;
          for (int j = 0; j < orddet.getRowCount(); j++) {
             if (orddet.getValueAt(j, 0).toString().equals(line)) {
                 goodLine = true;
             }
          }
          if (! goodLine) {
              badlines.add(line);
          }
        }
        
        // now update
        m = updateServiceOrderTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord());
     
     return m;
    }
    
    public String[] deleteRecord(String[] x) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
              
                   int i = st.executeUpdate("delete from sv_mstr where sv_nbr = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from svd_det where svd_nbr = " + "'" + x[0] + "'" + ";");
                    if (i > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    initvars(null);
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordConnError};
        }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
    } 
    
    public sv_mstr createRecord() {
        cusData.cm_mstr cm = cusData.getCustMstr(new String[]{ddcust.getSelectedItem().toString()});
        String acct = cm.cm_ar_acct();
        String cc = cm.cm_ar_cc();
        String terms = cm.cm_terms();
        String carrier = cm.cm_carrier();
        String onhold = cm.cm_onhold();
        String taxcode = cm.cm_tax_code();
        String curr = cm.cm_curr();
        sv_mstr x = new sv_mstr(null, tbkey.getText().toString(),
                 ddcust.getSelectedItem().toString(),
                 ddship.getSelectedItem().toString(),
                 "", // po
                 "", // crew
                bsmf.MainFrame.dfdate.format(createdate.getDate()).toString(),
                bsmf.MainFrame.dfdate.format(duedate.getDate()).toString(),
                remarks.getText(),
                ddstatus.getSelectedItem().toString(),
                "", // isched
                bsmf.MainFrame.userid,
                ddtype.getSelectedItem().toString(),
                "", // char1
                "", // char2
                "", // char3
                terms,
                curr,
                acct,
                cc,
                onhold, // onhold
                taxcode, // taxcode
                 ddsite.getSelectedItem().toString()
                );
        return x;
    }
   
    public ArrayList<svd_det> createDetRecord() {
        ArrayList<svd_det> list = new ArrayList<svd_det>();
        
           
            for (int j = 0; j < orddet.getRowCount(); j++) {
                // line, item, type, desc, order, qty, price, uom
                svd_det x = new svd_det(null, 
                tbkey.getText(),
                orddet.getValueAt(j, 0).toString(), // line
                orddet.getValueAt(j, 7).toString(), // uom
                orddet.getValueAt(j, 1).toString(), // item
                orddet.getValueAt(j, 3).toString(), //desc
                ddtype.getSelectedItem().toString(), // type
                "", // custitem
                orddet.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.'), // qty
                "", // completed hours        
                "", // po
                bsmf.MainFrame.dfdate.format(createdate.getDate()).toString(), // orddate
                bsmf.MainFrame.dfdate.format(duedate.getDate()).toString(),  // duedate
                bsmf.MainFrame.dfdate.format(createdate.getDate()).toString(), // createdate       
                "", // char1
                "", // char2
                "", // char3
                ddstatus.getSelectedItem().toString(),
                orddet.getValueAt(j, 6).toString().replace(defaultDecimalSeparator, '.'), // listprice
                orddet.getValueAt(j, 6).toString().replace(defaultDecimalSeparator, '.'), // netprice
                "0", // disc 
                "0", // taxamt  
                "", // taxcode        
                ddsite.getSelectedItem().toString()        
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
         luModel = DTData.getSVBrowseUtil(luinput.getText(),0, "sv_nbr");
        } else {
         luModel = DTData.getSVBrowseUtil(luinput.getText(),0, "sv_cust");   
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


    // custom functions
    public void itemChangeEvent(String myitem) {
          
         lbdesc.setText(invData.getItemDesc(dditem.getSelectedItem().toString()));
         tbprice.setText(BlueSeerUtils.bsformat("",String.valueOf(invData.getItemPrice(dditem.getSelectedItem().toString())).replace('.', defaultDecimalSeparator),"2"));
         dduom.setSelectedItem(OVData.getUOMFromItemSite(myitem, ddsite.getSelectedItem().toString()));
     }
     
    public void custChangeEvent(String mykey) {
           
            ddship.removeAllItems();
            
            
           if (ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty() ) {
               ddcust.setBackground(Color.red);
           } else {
               ddcust.setBackground(null);
           }
            
           
            
            ArrayList mycusts = cusData.getcustshipmstrlist(ddcust.getSelectedItem().toString());
            for (int i = 0; i < mycusts.size(); i++) {
                ddship.addItem(mycusts.get(i));
            }
            ddship.insertItemAt("",0);
            
            if (ddship.getItemCount() == 1) {
              ddship.setBackground(Color.red); 
           }
            
            
            
            
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
                
                res = st.executeQuery("select cm_name, cm_carrier, cm_tax_code, cm_curr from cm_mstr where cm_code = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    lblcustname.setText(res.getString("cm_name"));
                   
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
  
    public void jobsiteChangeEvent(String mycust, String myship) {
           
            
            
           if (ddship.getSelectedItem() == null || ddship.getSelectedItem().toString().isEmpty() ) {
               ddship.setBackground(Color.red);
           } else {
               ddship.setBackground(null);
           }
            
            
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
                String namestring = "";
                res = st.executeQuery("select cms_name, cms_line1, cms_city, cms_state, cms_zip from cms_det where cms_code = " + "'" + mycust + "'" + " and cms_shipto = " + "'" + myship + "'"  + ";");
                while (res.next()) {
                    namestring = res.getString("cms_name") + " " + res.getString("cms_line1") + " " + res.getString("cms_city") + "," + res.getString("cms_state") + " " + res.getString("cms_zip");
                    lblshipname.setText(namestring);
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
  
    public String[] autoInvoiceOrder() {
        
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        int shipperid = OVData.getNextNbr("shipper");   
         
        shpData.ship_mstr sh = shpData.createShipMstrJRT(String.valueOf(shipperid), ddsite.getSelectedItem().toString(),
                             String.valueOf(shipperid), 
                              ddcust.getSelectedItem().toString(),
                              ddship.getSelectedItem().toString(),
                              tbkey.getText(),
                              tbpo.getText(),  // po
                              tbpo.getText(),  // ref
                              dfdate.format(duedate.getDate()).toString(),
                              dfdate.format(createdate.getDate()).toString(),
                              remarks.getText().replace("'", ""),
                              "",
                              "S", "" ); 
        ArrayList<String[]> detail = tableToArrayList();
        ArrayList<shpData.ship_det> shd = shpData.createShipDetJRTmin(detail, String.valueOf(shipperid), dfdate.format(createdate.getDate()).toString(), ddsite.getSelectedItem().toString());
        shpData.addShipperTransaction(shd, sh);
        shpData.updateShipperSAC(String.valueOf(shipperid));
        // now confirm shipment
        String[] message = confirmShipperTransaction("serviceorder", String.valueOf(shipperid), now);
        message = new String[]{"0", "Service Order has been invoiced"};    
        // now auto payment
        if (cbpaid.isSelected()) {
          OVData.AREntry("P", String.valueOf(shipperid), now);
        }
        // autopost
        if (OVData.isAutoPost()) {
            fglData.PostGL();
        }        
        return message;
    }
        
    public void sumlinecount() {
         totlines.setText(String.valueOf(orddet.getRowCount()));
    }
    
    public void setTotalHours() {
        double qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             if (!orddet.getValueAt(j, 5).toString().isEmpty())
             qty = qty + bsParseDouble(orddet.getValueAt(j, 5).toString()); 
         }
         tbtotqty.setText(currformatDouble(qty));
    }
    
    public void setTotalPrice() {
        double qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             if (!orddet.getValueAt(j, 6).toString().isEmpty() && !orddet.getValueAt(j, 5).toString().isEmpty())
             qty = qty + ( bsParseDouble(orddet.getValueAt(j, 5).toString()) * bsParseDouble(orddet.getValueAt(j, 6).toString())); 
         }
         tbtotdollars.setText(currformatDouble(qty));
    }
    
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < orddet.getRowCount(); j++) {
            current = Integer.valueOf(orddet.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
   
    public ArrayList<String[]> tableToArrayList() {
        ArrayList<String[]> list = new ArrayList<String[]>();
         for (int j = 0; j < orddet.getRowCount(); j++) {
             String[] s = new String[]{
                 orddet.getValueAt(j, 0).toString(),
                 orddet.getValueAt(j, 1).toString(),
                 orddet.getValueAt(j, 2).toString(),
                 orddet.getValueAt(j, 3).toString(),
                 orddet.getValueAt(j, 4).toString(),
                 orddet.getValueAt(j, 5).toString(),
                 orddet.getValueAt(j, 6).toString(),
                 orddet.getValueAt(j, 7).toString()};
             list.add(s);
         }
        
        return list;
    }
   
    
    
   
 
   
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        duedate = new com.toedter.calendar.JDateChooser();
        btnew = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        jLabel85 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        btdelitem = new javax.swing.JButton();
        ddtype = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        jLabel81 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        panelService = new javax.swing.JPanel();
        jLabel79 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        serviceitem = new javax.swing.JTextArea();
        tbhours = new javax.swing.JTextField();
        tbserviceprice = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        panelItem = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dditem = new javax.swing.JComboBox<>();
        tbprice = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        lbdesc = new javax.swing.JLabel();
        dduom = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        tbtotqty = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        btprint = new javax.swing.JButton();
        tbpo = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        remarks = new javax.swing.JTextArea();
        btquotetoorder = new javax.swing.JButton();
        ddcrew = new javax.swing.JComboBox<>();
        jLabel93 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        ddcust = new javax.swing.JComboBox();
        ddship = new javax.swing.JComboBox();
        lblcustname = new javax.swing.JLabel();
        lblshipname = new javax.swing.JLabel();
        btnewcust = new javax.swing.JButton();
        btnewsite = new javax.swing.JButton();
        ddstatus = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        createdate = new com.toedter.calendar.JDateChooser();
        jLabel83 = new javax.swing.JLabel();
        btinvoice = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        rbinventory = new javax.swing.JRadioButton();
        rbservice = new javax.swing.JRadioButton();
        cbpaid = new javax.swing.JCheckBox();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();

        jLabel4.setText("jLabel4");

        jLabel9.setText("jLabel9");

        setBackground(new java.awt.Color(0, 102, 204));
        add(jScrollPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Service Order Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel76.setText("Key");
        jLabel76.setName("lblid"); // NOI18N

        jLabel86.setText("Remarks");
        jLabel86.setName("lblremarks"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        duedate.setDateFormatString("yyyy-MM-dd");

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btadditem.setText("Add Item");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        jLabel85.setText("Type");
        jLabel85.setName("lbltype"); // NOI18N

        jLabel82.setText("Customer");
        jLabel82.setName("lblcust"); // NOI18N

        orddet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane8.setViewportView(orddet);

        btdelitem.setText("Del Item");
        btdelitem.setName("btdeleteitem"); // NOI18N
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "quote", "order" }));

        btadd.setText("Add");
        btadd.setName("bdadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel81.setText("Due Date");
        jLabel81.setName("lblduedate"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel79.setText("Service Item");
        jLabel79.setName("lblserviceitem"); // NOI18N

        serviceitem.setColumns(20);
        serviceitem.setRows(5);
        jScrollPane2.setViewportView(serviceitem);

        tbhours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbhoursFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbhoursFocusLost(evt);
            }
        });

        tbserviceprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbservicepriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbservicepriceFocusLost(evt);
            }
        });

        jLabel84.setText("Unit/Hour");
        jLabel84.setName("lblunithour"); // NOI18N

        jLabel11.setText("Price");

        jLabel12.setFont(new java.awt.Font("Noto Sans", 2, 12)); // NOI18N
        jLabel12.setText("Price is per uom");
        jLabel12.setName("lblperuom"); // NOI18N

        javax.swing.GroupLayout panelServiceLayout = new javax.swing.GroupLayout(panelService);
        panelService.setLayout(panelServiceLayout);
        panelServiceLayout.setHorizontalGroup(
            panelServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelServiceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel79)
                    .addComponent(jLabel84)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelServiceLayout.createSequentialGroup()
                        .addComponent(tbhours, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelServiceLayout.createSequentialGroup()
                        .addGroup(panelServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelServiceLayout.createSequentialGroup()
                                .addComponent(tbserviceprice, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        panelServiceLayout.setVerticalGroup(
            panelServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelServiceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel79)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbhours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbserviceprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setText("Item");
        jLabel8.setName("lblitem"); // NOI18N

        jLabel3.setText("Price");
        jLabel3.setName("lblprice"); // NOI18N

        dditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemActionPerformed(evt);
            }
        });

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel10.setText("Qty");
        jLabel10.setName("lblqty"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel13.setText("UOM");
        jLabel13.setName("lbluom"); // NOI18N

        javax.swing.GroupLayout panelItemLayout = new javax.swing.GroupLayout(panelItem);
        panelItem.setLayout(panelItemLayout);
        panelItemLayout.setHorizontalGroup(
            panelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelItemLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelItemLayout.createSequentialGroup()
                        .addComponent(lbdesc, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelItemLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelItemLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelItemLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelItemLayout.setVerticalGroup(
            panelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelItemLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(lbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(panelService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Total Lines");
        jLabel1.setName("lbltotallines"); // NOI18N

        jLabel2.setText("Total Hrs");
        jLabel2.setName("lbltotalhours"); // NOI18N

        jLabel91.setText("Job Site");
        jLabel91.setName("lbljobsite"); // NOI18N

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        jLabel92.setText("PO");
        jLabel92.setName("lblpo"); // NOI18N

        remarks.setColumns(20);
        remarks.setRows(5);
        jScrollPane3.setViewportView(remarks);

        btquotetoorder.setText("QuoteToOrder");
        btquotetoorder.setName("btquotetoorder"); // NOI18N
        btquotetoorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btquotetoorderActionPerformed(evt);
            }
        });

        jLabel93.setText("Crew");
        jLabel93.setName("lblcrew"); // NOI18N

        jLabel5.setText("Total");
        jLabel5.setName("lbltotal"); // NOI18N

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        ddship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddshipActionPerformed(evt);
            }
        });

        btnewcust.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnewcust.setToolTipText("");
        btnewcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewcustActionPerformed(evt);
            }
        });

        btnewsite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnewsite.setToolTipText("");
        btnewsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewsiteActionPerformed(evt);
            }
        });

        jLabel6.setText("Status");
        jLabel6.setName("lblstatus"); // NOI18N

        createdate.setDateFormatString("yyyy-MM-dd");

        jLabel83.setText("Crt Date");
        jLabel83.setName("lblcreatedate"); // NOI18N

        btinvoice.setText("Invoice");
        btinvoice.setName("btinvoice"); // NOI18N
        btinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btinvoiceActionPerformed(evt);
            }
        });

        jLabel7.setText("Site");
        jLabel7.setName("lblsite"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        rbinventory.setText("Inventory");
        rbinventory.setName("cbinventory"); // NOI18N
        rbinventory.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbinventoryStateChanged(evt);
            }
        });
        rbinventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbinventoryActionPerformed(evt);
            }
        });

        rbservice.setText("Service");
        rbservice.setName("cbservice"); // NOI18N
        rbservice.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbserviceStateChanged(evt);
            }
        });
        rbservice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbserviceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbservice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbinventory)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbservice)
                    .addComponent(rbinventory)))
        );

        cbpaid.setText("PaidInFull?");
        cbpaid.setName("cbpaidinfull"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelitem))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(63, 63, 63))
                                    .addComponent(totlines, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addGap(3, 3, 3)
                                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(btprint)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btadd))))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel76)
                            .addComponent(jLabel86)
                            .addComponent(jLabel85)
                            .addComponent(jLabel82)
                            .addComponent(jLabel91)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(ddcust, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(ddship, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(ddtype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(ddstatus, 0, 133, Short.MAX_VALUE))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(85, 85, 85)
                                                        .addComponent(jLabel93))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(btnewcust, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel81))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(btnewsite, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel92))))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(lblcustname, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                                                .addComponent(jLabel83)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(ddcrew, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 303, Short.MAX_VALUE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel7)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(createdate, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                                                        .addComponent(duedate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cbpaid))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnew)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btclear))
                                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblshipname, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 288, Short.MAX_VALUE)
                                        .addComponent(btquotetoorder)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btinvoice)))
                                .addGap(13, 13, 13)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnew)
                                    .addComponent(btquotetoorder)
                                    .addComponent(btinvoice)
                                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)
                                    .addComponent(btclear))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(5, 5, 5)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel76))))
                            .addComponent(btlookup))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblcustname, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(createdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel83)))
                    .addComponent(cbpaid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel82)
                        .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel81))
                    .addComponent(duedate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnewcust, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblshipname, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel91)
                        .addComponent(ddship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel92))
                    .addComponent(btnewsite, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85)
                    .addComponent(ddcrew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel93))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel86)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btadditem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btprint)
                    .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel2);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
     newAction("srvm");
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
         boolean canproceed = true;
        int line = 0;
        double qty = 0;
        double price = 0;
        orddet.setModel(myorddetmodel);
        line = getmaxline();
        line++;
        
        if (rbservice.isSelected()) {
            if (tbhours.getText().isEmpty()) { 
                qty = 0; 
            } else {
                qty = bsParseDouble(tbhours.getText());
            }
            if (tbserviceprice.getText().isEmpty()) { 
                price = 0; 
            } else {
                price = bsParseDouble(tbserviceprice.getText()); 
            }
        } else {
            
            if (dditem.getSelectedItem() == null || dditem.getSelectedItem().toString().isEmpty()) {
                bsmf.MainFrame.show(getMessageTag(1099));
                return;
            }
            if (tbqty.getText().isEmpty()) { 
                qty = 0; 
            } else {
                qty = bsParseDouble(tbqty.getText());
            }
            if (tbprice.getText().isEmpty()) { 
                price = 0; 
            } else {
                price = bsParseDouble(tbprice.getText()); 
            }
        }
        
        if (qty == 0) {
            bsmf.MainFrame.show(getMessageTag(1100));
        }
        
        if (canproceed) {
         if (rbservice.isSelected()) {
           myorddetmodel.addRow(new Object[]{line, serviceitem.getText(), 
               "S", "", tbkey.getText(),  String.valueOf(qty), 
              String.valueOf(price).replace('.', defaultDecimalSeparator), "EA"}); 
         } else {
          myorddetmodel.addRow(new Object[]{line, dditem.getSelectedItem().toString(), 
             "I", lbdesc.getText(), tbkey.getText(),  String.valueOf(qty).replace('.', defaultDecimalSeparator), 
             String.valueOf(price).replace('.', defaultDecimalSeparator), dduom.getSelectedItem().toString()});
         }
         
         setTotalHours();
         sumlinecount();
         setTotalPrice();
         
         if (myorddetmodel.getRowCount() > 0 && ! btupdate.isEnabled()) {
             btadd.setEnabled(true);
         }
         
         serviceitem.setText("");
         tbqty.setText("0");
         tbhours.setText("0");
         tbprice.setText("0");
         tbserviceprice.setText("0");
         dditem.setSelectedIndex(0);
         serviceitem.requestFocus();
        }
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
      if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});   
       
    }//GEN-LAST:event_btaddActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed

  int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
        }
        
        if (getmaxline() > 0) {
            btadd.setEnabled(true);
        } else {
             btadd.setEnabled(false);
        }
       
         setTotalHours();
         sumlinecount();
         setTotalPrice();


        /*
        int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 10).toString().equals("close") || orddet.getValueAt(i, 10).toString().equals("partial")) {
                bsmf.MainFrame.show("Cannot Delete Closed or Partial Item");
                return;
                            } else {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
       
         sumqty();
         sumlinecount();
         */
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       OVData.printServiceOrder(tbkey.getText());
    }//GEN-LAST:event_btprintActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed

        if (ddcust.getItemCount() > 0) {
            custChangeEvent(ddcust.getSelectedItem().toString());

        } // if ddcust has a list

    }//GEN-LAST:event_ddcustActionPerformed

    private void ddshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshipActionPerformed
        if (ddship.getItemCount() > 0) {
            jobsiteChangeEvent(ddcust.getSelectedItem().toString(), ddship.getSelectedItem().toString());

        } // if ddcust has a list
    }//GEN-LAST:event_ddshipActionPerformed

    private void btnewcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewcustActionPerformed
        reinitpanels("CustMaint", true, new String[]{});
    }//GEN-LAST:event_btnewcustActionPerformed

    private void btnewsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewsiteActionPerformed
       reinitpanels("CustMaint", true, new String[]{ddcust.getSelectedItem().toString()});
    }//GEN-LAST:event_btnewsiteActionPerformed

    private void tbpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusGained
         if (tbprice.getText().equals("0")) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
        if (! tbprice.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbprice.getText(), "2");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbprice.requestFocus();
        } else {
            tbprice.setText(x);
            tbprice.setBackground(Color.white);
        }
        setTotalPrice();
        }
    }//GEN-LAST:event_tbpriceFocusLost

    private void tbhoursFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbhoursFocusGained
          if (tbhours.getText().equals("0")) {
            tbhours.setText("");
        }
    }//GEN-LAST:event_tbhoursFocusGained

    private void tbhoursFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbhoursFocusLost
        if (! tbhours.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbhours.getText(), "2");
        if (x.equals("error")) {
            tbhours.setText("");
            tbhours.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbhours.requestFocus();
        } else {
            tbhours.setText(x);
            tbhours.setBackground(Color.white);
        }
        setTotalHours();
        setTotalPrice();  
        }
    }//GEN-LAST:event_tbhoursFocusLost

    private void btquotetoorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btquotetoorderActionPerformed
        try {

           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                    st.executeUpdate("update sv_mstr set sv_type = 'order' where sv_nbr = " + "'" + tbkey.getText() + "'" );
                    bsmf.MainFrame.show(getMessageTag(1101));
                   initvars(new String[]{tbkey.getText()});
                    // btQualProbAdd.setEnabled(false);
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btquotetoorderActionPerformed

    private void btinvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btinvoiceActionPerformed
          String[] message = autoInvoiceOrder();
         if (message[0].equals("1")) { // if error
           bsmf.MainFrame.show(message[1]);
         } else {
           executeTask("get", new String[]{tbkey.getText()});
         }
    }//GEN-LAST:event_btinvoiceActionPerformed

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
         if (tbqty.getText().equals("0")) {
            tbqty.setText("");
        }
    }//GEN-LAST:event_tbqtyFocusGained

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbqtyFocusLost

    private void dditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemActionPerformed
        if (dditem.getSelectedItem() != null && ! isLoad)
        itemChangeEvent(dditem.getSelectedItem().toString());
    }//GEN-LAST:event_dditemActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        executeTask("delete", new String[]{tbkey.getText()});
      
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbservicepriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbservicepriceFocusGained
         if (tbserviceprice.getText().equals("0")) {
            tbserviceprice.setText("");
        }
    }//GEN-LAST:event_tbservicepriceFocusGained

    private void tbservicepriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbservicepriceFocusLost
        if (! tbserviceprice.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbserviceprice.getText(), "2");
        if (x.equals("error")) {
            tbserviceprice.setText("");
            tbserviceprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbserviceprice.requestFocus();
        } else {
            tbserviceprice.setText(x);
            tbserviceprice.setBackground(Color.white);
        }
        setTotalPrice();
        }
    }//GEN-LAST:event_tbservicepriceFocusLost

    private void rbserviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbserviceActionPerformed
       if (! isLoad) {
        if (rbservice.isSelected()) {
            setPanelComponentState(panelService,true);
            setPanelComponentState(panelItem,false);
        } else {
            setPanelComponentState(panelService,false);
            setPanelComponentState(panelItem,true); 
        }
       }
       
    }//GEN-LAST:event_rbserviceActionPerformed

    private void rbinventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbinventoryActionPerformed
        if (! isLoad) {
        if (rbinventory.isSelected()) {
            setPanelComponentState(panelService,false);
            setPanelComponentState(panelItem,true);
        } else {
            setPanelComponentState(panelService,true);
            setPanelComponentState(panelItem,false); 
        }
        }
    }//GEN-LAST:event_rbinventoryActionPerformed

    private void rbserviceStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbserviceStateChanged
       
        if (! isLoad) {
            if (rbservice.isSelected()) {
                setPanelComponentState(panelService,true);
                setPanelComponentState(panelItem,false);
            } else {
                setPanelComponentState(panelService,false);
                setPanelComponentState(panelItem,true);
            } 
       }
       
    }//GEN-LAST:event_rbserviceStateChanged

    private void rbinventoryStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbinventoryStateChanged
        
    }//GEN-LAST:event_rbinventoryStateChanged

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset(); 
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btinvoice;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btnewcust;
    private javax.swing.JButton btnewsite;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btquotetoorder;
    private javax.swing.JButton btupdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbpaid;
    private com.toedter.calendar.JDateChooser createdate;
    private javax.swing.JComboBox<String> ddcrew;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox<String> dditem;
    private javax.swing.JComboBox ddship;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox<String> ddstatus;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox<String> dduom;
    private com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbdesc;
    private javax.swing.JLabel lblcustname;
    private javax.swing.JLabel lblshipname;
    private javax.swing.JTable orddet;
    private javax.swing.JPanel panelItem;
    private javax.swing.JPanel panelService;
    private javax.swing.JRadioButton rbinventory;
    private javax.swing.JRadioButton rbservice;
    private javax.swing.JTextArea remarks;
    private javax.swing.JTextArea serviceitem;
    private javax.swing.JTextField tbhours;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbserviceprice;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
