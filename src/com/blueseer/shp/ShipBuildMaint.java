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
import com.blueseer.adm.admData;
import com.blueseer.ctr.cusData;
import com.blueseer.ctr.cusData.cm_mstr;
import com.blueseer.ctr.cusData.cms_det;
import static com.blueseer.ctr.cusData.getCMSDet;
import static com.blueseer.ctr.cusData.getCustMstr;
import static com.blueseer.lbl.lblData.getLabelSerialDisplay;
import static com.blueseer.lbl.lblData.getLabelTableRecs;
import static com.blueseer.lbl.lblData.updateLabelStatus;
import static com.blueseer.shp.shpData.addShipperTransaction;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import static com.blueseer.shp.shpData.getShipperMstrSet;
import com.blueseer.shp.shpData.ship_det;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.DTData;
import com.blueseer.utl.OVData;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
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
public class ShipBuildMaint extends javax.swing.JPanel {

     // global variable declarations
        boolean isLoad = false;
        boolean canUpdate = false;
        boolean isAutoPost = false;
        ArrayList<String[]> initDataSets = null;
        String defaultSite = "";
        String defaultCurrency = "";
        String defaultCC = "";
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
        HashSet<String> assignedlabels = new HashSet<String>();
        boolean autoconfirm = false;
        boolean autonumber = true;
        public static shpData.ship_mstr sh = null;
        public static ArrayList<shpData.ship_det> shdlist = null;
    
    // global datatablemodel declarations 
    javax.swing.table.DefaultTableModel serialmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("label"),
                getGlobalColumnTag("order"),
                getGlobalColumnTag("line"),
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("custitem"),
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location"),
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("uom"),
                getGlobalColumnTag("listprice"),
                getGlobalColumnTag("discount"),
                getGlobalColumnTag("price"),
                getGlobalColumnTag("po")});
    ShipTableModel shipmodel = new ShipTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("label"), // label serial
                getGlobalColumnTag("order"),
                getGlobalColumnTag("line"),
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("custitem"), 
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("uom"),
                getGlobalColumnTag("listprice"),
                getGlobalColumnTag("discount"),
                getGlobalColumnTag("price"),
                getGlobalColumnTag("po")
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
  
    public ShipBuildMaint() {
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
     
    public void setComponentDefaultValues(boolean init) {
       isLoad = true;
        
       if (init) {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "customers,autoshipconfirm,autoshipnumber");
        }
       
        cbcomplete.setSelected(false);
         tbkey.setText("");
         terms = "";
         aracct = "";
         arcc = "";
         arbank = "";
         actamt = 0.00;
         rcvamt = 0.00;
        
        assignedlabels.clear();
         
        lbcust.setText("");
        lbmessage.setText("");
        lbmessage.setForeground(Color.blue);
                
        tbrmks.setText("");
      
        tbref.setText("");
        tbtotal.setText("0");
        tbtotal.setBackground(Color.white);
        tbtotal.setEditable(false);
        serialmodel.setRowCount(0);
        shipmodel.setRowCount(0);
        shipmodel.addTableModelListener(ml);
        serialdet.setModel(serialmodel);
        shipdet.setModel(shipmodel);
        
       
        
        java.util.Date now = new java.util.Date();
        dcdate.setDate(now);
              
        ddcust.removeAllItems();
        ddsite.removeAllItems();
       
        
       for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("site")) {
              defaultSite = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("autopost")) {
              isAutoPost = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("autoshipnumber")) {
              autonumber = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("autoshipconfirm")) {
              autoconfirm = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("customers")) {
              ddcust.addItem(s[1]); 
            }
        }
       
        ddsite.setSelectedItem(defaultSite);
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        ddship.removeAllItems();
        
        
         
        
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues(false);
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btPrintInv.setEnabled(false);
        btPrintShp.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          if (autonumber) {  
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
          } else {
              tbkey.setText("");
          }
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
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
        
    }
    
    public boolean validateInput(String x) {
        boolean b = true;
           
        
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
       setComponentDefaultValues(initDataSets == null);
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
        String[] m = addShipperTransaction(createDetRecord(), createRecord(), createTreeRecord());
        for (String label : assignedlabels) {
            updateLabelStatus(label, "1");
        }
        shpData.updateShipperSAC(tbkey.getText());
        if (autoconfirm) {
        confirmShipperTransaction("", tbkey.getText(), dcdate.getDate());
        }
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
      shpData.Shipper z = getShipperMstrSet(x);
      sh = z.sh();
      shdlist = z.shd();
      return z.m();
    }
   
    public shpData.ship_mstr createRecord() {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       
        
        shpData.ship_mstr x = new shpData.ship_mstr(null, 
                tbkey.getText(),
                ddcust.getSelectedItem().toString(),
                ddship.getSelectedItem().toString(),
                0, // pallets
                0, // boxes
                "", // shipvia  
                setDateDB(dcdate.getDate()),
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
                ddsite.getSelectedItem().toString(),
                tbtracking.getText(),
                "", // status 
                "", // sh_char1
                String.valueOf(BlueSeerUtils.boolToInt(cbcomplete.isSelected())), // sh_char2 
                "" // sh_char3
        );
                
        return x;        
    }
    
    public ArrayList<shpData.ship_det> createDetRecord() {
        ArrayList<shpData.ship_det> list = new ArrayList<shpData.ship_det>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // line, item, order, orderline, po, qty, netprice, desc, wh, loc, disc, listprice, tax, cont, serial
        for (int j = 0; j < shipdet.getRowCount(); j++) { 
            shpData.ship_det x = new shpData.ship_det(null, 
                tbkey.getText(), // shipper
                j + 1, //shline
                shipdet.getValueAt(j, 3).toString(), // item
                shipdet.getValueAt(j, 5).toString(), // custimtem
                shipdet.getValueAt(j, 1).toString(),  // order
                bsParseInt(shipdet.getValueAt(j, 2).toString()), //soline    
                setDateDB(dcdate.getDate()),
                shipdet.getValueAt(j, 13).toString(), // po
                bsParseDouble(shipdet.getValueAt(j, 8).toString().replace(defaultDecimalSeparator, '.')), // qty
                shipdet.getValueAt(j, 9).toString(), //uom
                curr, //currency
                bsParseDouble(shipdet.getValueAt(j, 12).toString().replace(defaultDecimalSeparator, '.')), // net price
                bsParseDouble(shipdet.getValueAt(j, 11).toString().replace(defaultDecimalSeparator, '.')), // disc
                bsParseDouble(shipdet.getValueAt(j, 10).toString().replace(defaultDecimalSeparator, '.')), // list price
                shipdet.getValueAt(j, 4).toString(), // desc
                shipdet.getValueAt(j, 6).toString(), // wh
                shipdet.getValueAt(j, 7).toString(), // loc
                0, // taxamt
                "0", // cont
                tbref.getText(), // ref
                shipdet.getValueAt(j, 5).toString(), // serial   
                ddsite.getSelectedItem().toString(),
                "", // bom
                0,  // packqty
                "" // kvpair    
                );
        list.add(x);
        }      
        return list;        
    }
    
    public ArrayList<shpData.ship_tree> createTreeRecord() {
        ArrayList<shpData.ship_tree> list = new ArrayList<shpData.ship_tree>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // create shipper parent node with child containers
        for (String s : assignedlabels) {
            shpData.ship_tree x = new shpData.ship_tree(null,
            tbkey.getText(),
            s,
            ddsite.getSelectedItem().toString(),
            "c",
            tbkey.getText(),
            "",
            "",
            "",
            "",
            "container",
            1.0,
            getLabelSerialDisplay(s) // get display serial
            );
            
            list.add(x);
            // now items of container
            for (int j = 0; j < shipdet.getRowCount(); j++) { 
                if (shipdet.getValueAt(j, 0).toString().equals(s)) {
                    shpData.ship_tree y = new shpData.ship_tree(null,
                    s,
                    shipdet.getValueAt(j, 1).toString() + "," + shipdet.getValueAt(j, 2).toString() + "," + shipdet.getValueAt(j, 3).toString(),
                    ddsite.getSelectedItem().toString(),
                    "i",
                    tbkey.getText(),
                    String.valueOf(j + 1),
                    shipdet.getValueAt(j, 1).toString(),
                    shipdet.getValueAt(j, 2).toString(),
                    shipdet.getValueAt(j, 11).toString(),
                    shipdet.getValueAt(j, 3).toString(),
                    bsParseDouble(shipdet.getValueAt(j, 8).toString().replace(defaultDecimalSeparator, '.')),
                    "" // get display serial
                    );
                    list.add(y);
                }
            }
        }
       
        return list;        
    }
    
    public void updateForm() throws ParseException {
        
        shipmodel.setRowCount(0);
        
        tbkey.setText(sh.sh_id());
        cbcomplete.setSelected(BlueSeerUtils.ConvertStringToBool(sh.sh_char2()));        
         dcdate.setDate(parseDate(sh.sh_shipdate()));
         tbref.setText(sh.sh_ref());
         tbrmks.setText(sh.sh_rmks());
         ddcust.setSelectedItem(sh.sh_cust());
         ddship.setSelectedItem(sh.sh_ship());
         ddsite.setSelectedItem(sh.sh_site());
         tbtracking.setText(sh.sh_trailer());
        
        
        
        for (ship_det shd : shdlist) {
                      
        int nbrOfContainers = 0;
        int remainder = 0;
        if (shd.shd_packqty() > 0) {
            nbrOfContainers = ( (int) shd.shd_qty() / (int) shd.shd_packqty());
            remainder = ( (int) shd.shd_qty() % (int) shd.shd_packqty());
        } 
       
            
           
            
             shipmodel.addRow(new Object[] { shd.shd_line(),
                                              shd.shd_item(),
                                              shd.shd_desc(),
                                              shd.shd_serial(),
                                              shd.shd_wh(),
                                              shd.shd_loc(),
                                              shd.shd_qty(),
                                              shd.shd_netprice(),
                                              shd.shd_bom()
                                              });
                 
                  
                  actamt += (shd.shd_qty() * shd.shd_netprice());
            
        }
        
       // getAttachments(tbkey.getText());
        
        setAction(sh.m()); 
        
        //sh = null;
        //shdlist = null;

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
    public void setcustvariables(cm_mstr cm) {
       
        // aracct, arcc, currency, bank, terms, carrier, onhold, site
           
            aracct = cm.cm_ar_acct();
            arcc = cm.cm_ar_cc();
            terms = cm.cm_terms();
            arbank = cm.cm_bank();
            curr = cm.cm_curr();
            ddship.removeAllItems();
            ArrayList<String> shiptos = cusData.getcustshipmstrlist(ddcust.getSelectedItem().toString());
            for (int i = 0; i < shiptos.size(); i++) {
                ddship.addItem(shiptos.get(i));
            }
       
    }
     
    
    public void sumdollars() {
       
       
        actamt = 0;
         for (int j = 0; j < shipdet.getRowCount(); j++) {
             actamt += ( bsParseDouble(shipdet.getModel().getValueAt(j,8).toString()) * bsParseDouble(shipdet.getModel().getValueAt(j,10).toString()) );
         }
        
        tbtotal.setText(currformatDouble(actamt));
        
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
        
    public void refreshList() {
        serialmodel.setRowCount(0);
        ArrayList<String[]> list = getLabelTableRecs(ddcust.getSelectedItem().toString());
        for (String[] s : list) {
            if (! assignedlabels.contains(s[0])) {
            serialmodel.addRow(new Object[] { 
                s[0], // serial
                s[1], // order
                s[2], // line
                s[3], // item
                s[4], // desc
                s[5], // custitem
                s[6], //warehouse
                s[7], // location
                s[8], // qty
                s[9], // uom
                s[10], // listprice
                s[11], // disc
                s[12], // netprice
                s[13] // po
                });
            }
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
        serialdet = new javax.swing.JTable();
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
        ddship = new javax.swing.JComboBox<>();
        lbship = new javax.swing.JLabel();
        btPrintInv = new javax.swing.JButton();
        btPrintShp = new javax.swing.JButton();
        tbtracking = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btlist = new javax.swing.JButton();
        cbcomplete = new javax.swing.JCheckBox();

        jLabel1.setText("jLabel1");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Shipper Build"));
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

        serialdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(serialdet);

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

        jLabel6.setText("Tracking");

        btlist.setText("List");
        btlist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlistActionPerformed(evt);
            }
        });

        cbcomplete.setText("complete");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                        .addGap(26, 26, 26)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel24)
                                            .addComponent(jLabel36)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel37)
                                            .addComponent(jLabel38))
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
                                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                    .addComponent(tbtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel6)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(tbtracking, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))))
                                                    .addComponent(lbmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(192, 192, 192)
                            .addComponent(btPrintInv)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btPrintShp)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                            .addComponent(cbcomplete)
                            .addGap(49, 49, 49)
                            .addComponent(btdelete)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btupdate)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btadd)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btlist)))
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
                            .addComponent(jLabel2)
                            .addComponent(tbtracking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
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
                .addComponent(btlist)
                .addGap(4, 4, 4)
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
                    .addComponent(btPrintShp)
                    .addComponent(cbcomplete))
                .addGap(35, 35, 35))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("shipper");
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        
        int[] rows = serialdet.getSelectedRows();
        String targetlabel = "";
        for (int i : rows) {
            targetlabel = serialdet.getModel().getValueAt(i, 0).toString();
        }
        
        
        for (int i = 0; i < serialdet.getRowCount(); i++) {
           if (! assignedlabels.contains(targetlabel) && serialdet.getModel().getValueAt(i, 0).toString().equals(targetlabel)) {
                shipmodel.addRow(new Object[] { 
                serialdet.getModel().getValueAt(i, 0), // serial
                serialdet.getModel().getValueAt(i, 1), // order
                serialdet.getModel().getValueAt(i, 2), // orderline
                serialdet.getModel().getValueAt(i, 3), // item
                serialdet.getModel().getValueAt(i, 4), // desc
                serialdet.getModel().getValueAt(i, 5), // custitem
                serialdet.getModel().getValueAt(i, 6), // wh
                serialdet.getModel().getValueAt(i, 7), // loc
                serialdet.getModel().getValueAt(i, 8), // qty
                serialdet.getModel().getValueAt(i, 9), // uom
                serialdet.getModel().getValueAt(i, 10), // listprice
                serialdet.getModel().getValueAt(i, 11), // disc
                serialdet.getModel().getValueAt(i, 12), // price
                serialdet.getModel().getValueAt(i, 13) // po
                });
                            
           }
        }
      
        
        assignedlabels.add(targetlabel);
        refreshList();
        
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
        serialmodel.setRowCount(0);
        shipmodel.setRowCount(0);
        lbcust.setText("");
        if ( ddcust.getSelectedItem() != null && ! ddcust.getSelectedItem().toString().isEmpty()  && ! isLoad) {
        cm_mstr cm = getCustMstr(new String[]{ddcust.getSelectedItem().toString()});
        lbcust.setText(cm.cm_name());
        setcustvariables(cm);
        }
    }//GEN-LAST:event_ddcustActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = shipdet.getSelectedRows();
        String targetlabel = "";
        for (int i : rows) {
            targetlabel = shipdet.getModel().getValueAt(i, 0).toString();
        }
        
       
        ArrayList<Integer> rowsToDelete = new ArrayList<Integer>();
        for (int i = 0; i < shipdet.getRowCount(); i++) {
           if (shipdet.getModel().getValueAt(i, 0).toString().equals(targetlabel)) {
                 rowsToDelete.add(i);
                               
           }
        }
        Collections.reverse(rowsToDelete);
        for (int j : rowsToDelete) {
            ((javax.swing.table.DefaultTableModel) shipdet.getModel()).removeRow(j); 
        }
        
        assignedlabels.remove(targetlabel);
        refreshList();
        sumdollars();
       
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
        initDataSets = null;
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

    private void ddshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshipActionPerformed
       if (! isLoad && ddship != null && ddship.getItemCount() > 0)  {
        cms_det cms = getCMSDet(ddship.getSelectedItem().toString(),ddcust.getSelectedItem().toString());
        lbship.setText(cms.cms_name());
       }
    }//GEN-LAST:event_ddshipActionPerformed

    private void btPrintInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintInvActionPerformed
        OVData.printInvoice(tbkey.getText(), true);
    }//GEN-LAST:event_btPrintInvActionPerformed

    private void btPrintShpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintShpActionPerformed
        OVData.printShipper(tbkey.getText());
        // OVData.printJTableToJasper("Shipper Report", tabledetail );
    }//GEN-LAST:event_btPrintShpActionPerformed

    private void btlistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlistActionPerformed
        refreshList();
    }//GEN-LAST:event_btlistActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btPrintInv;
    private javax.swing.JButton btPrintShp;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlist;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbcomplete;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox<String> ddship;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbcust;
    private javax.swing.JLabel lbmessage;
    private javax.swing.JLabel lbship;
    private javax.swing.JTable serialdet;
    private javax.swing.JTable shipdet;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrmks;
    private javax.swing.JTextField tbtotal;
    private javax.swing.JTextField tbtracking;
    // End of variables declaration//GEN-END:variables
}
