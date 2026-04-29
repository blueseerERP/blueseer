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

import bsmf.MainFrame;
import static bsmf.MainFrame.defaultDecimalSeparator;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.tags;
import com.blueseer.ctr.cusData;
import com.blueseer.ctr.cusData.CustShipSet;
import com.blueseer.ctr.cusData.cms_det;
import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.getItemMstr;
import com.blueseer.inv.invData.item_mstr;
import static com.blueseer.ord.ordData.getOrderDet;
import static com.blueseer.ord.ordData.getOrderMstr;
import static com.blueseer.ord.ordData.getOrderMstrSet;
import com.blueseer.ord.ordData.salesOrder;
import com.blueseer.ord.ordData.so_mstr;
import com.blueseer.ord.ordData.sod_det;
import com.blueseer.ord.ordData.sos_det;
import com.blueseer.shp.shpData.Shipper;
import static com.blueseer.shp.shpData.addShipperTransaction;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import static com.blueseer.shp.shpData.deleteShipMstr;
import static com.blueseer.shp.shpData.getShipperLineNumbers;
import static com.blueseer.shp.shpData.getShipperMstrSet;
import com.blueseer.shp.shpData.sh_meta;
import com.blueseer.shp.shpData.ship_det;
import com.blueseer.shp.shpData.ship_mstr;
import com.blueseer.shp.shpData.ship_tree;


import com.blueseer.shp.shpData.shs_det;
import static com.blueseer.shp.shpData.updateShipTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import com.blueseer.utl.BlueSeerUtils.dbaction;
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
import static com.blueseer.utl.BlueSeerUtils.lurb2;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author vaughnte
 */
public class ShipperMaint extends javax.swing.JPanel implements IBlueSeerT {

                String terms = "";
                String taxcode = "";
                String aracct = "";
                String arcc = "";
                String podate = "";
                int ordercount = 0;
                String status = "";
                String curr = "";
                boolean isLoad = false;
                boolean autonumber = true;
                boolean canUpdate = false;
                boolean autopost = false;
                boolean canconfirm = false;
                public static ship_mstr sh = null;
                public static ArrayList<ship_det> shdlist = null;
                public static ArrayList<shs_det> shslist = null;
                public static ArrayList<ship_tree> shtlist = null;
                public static ArrayList<sh_meta> shmlist = null;
                public static cms_det cms = null;
                public ArrayList<String[]> rtabledetail = new ArrayList<>();
                public ArrayList<String> rwhaction = new ArrayList<>();
                public ArrayList<String> rbilltoaction = new ArrayList<>();
                public static CustShipSet css = null;
                public static salesOrder so = null;
                
    
    /**
     * Creates new form ShipMaintPanel
     */
    public ShipperMaint() {
        initComponents();
        setLanguageTags(this);
    }
   
    
   // shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt 
    javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("amounttype"), 
                getGlobalColumnTag("amount")
            });
    
    ShipperMaint.MyTableModel myshipdetmodel = new ShipperMaint.MyTableModel(new Object[][]{},
            new String[]{
                 getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("orderline"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("price"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("tax"), 
                getGlobalColumnTag("serial"), 
                getGlobalColumnTag("cont"),
                getGlobalColumnTag("bom"),
                getGlobalColumnTag("contqty"),
                getGlobalColumnTag("uom"),
                getGlobalColumnTag("custitem")
            });
    javax.swing.table.DefaultTableModel attachmentmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), 
                getGlobalColumnTag("file")})
            {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class; 
                else return String.class;  //other columns accept String values  
              }  
            };
    
    javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 5 )) {
                            retotal();
                            refreshDisplayTotals();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };
    
    class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
       boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}; 
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
    
    public boolean validateInput(BlueSeerUtils.dbaction x) {
        
         if (! canUpdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"ship_mstr"});
        int fc;

        fc = checkLength(f,"sh_id");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }  
        
       
        
        fc = checkLength(f,"sh_rmks");
        if (tbremarks.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbremarks.requestFocus();
            return false;
        }
        
        if (tabledetail.getRowCount() == 0) {
            bsmf.MainFrame.show(getMessageTag(1089));
            tbkey.requestFocus();
            return false;
        }


        if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            ddsite.requestFocus();
            return false;
        }

        if (lbladdr.getText().isBlank()) {
            
            if (rbnonorder.isSelected()) {
            bsmf.MainFrame.show("ship to has not been set");
            } else {
            bsmf.MainFrame.show("single order ship to has not been set");    
            }
            
            return false;
        }
              
        return true;
    }
    
    public void executeTask(dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(dbaction type, String[] key) { 
              this.type = type.name();
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
                case "run":
                    if (this.key[0].equals("commit")) {
                      message = run_autoInvoice();
                    }
                    if (this.key[0].equals("tabledetailMouseClicked")) {
                      message = run_tabledetailMouseClicked(this.key[1]);
                    } 
                    if (this.key[0].equals("ddwhActionPerformed")) {
                      message = run_ddwhActionPerformed(this.key[1]);
                    }
                    if (this.key[0].equals("ddbilltoActionPerformed")) {
                      message = run_ddbilltoActionPerformed(this.key[1]);
                    }
                    if (this.key[0].equals("getCustShipSet")) {
                      message = run_getCustShipSet(this.key[1], this.key[2]);
                    }
                    if (this.key[0].equals("getOrderMstrSet")) {
                      message = run_getOrderMstrSet(this.key[1]);
                    }
                    
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
             done_updateForm();
             tbkey.requestFocus();
           } else if (this.type.equals("get") && message[0].equals("0")) {
             done_updateForm();
             tbkey.requestFocus();
           } else if (this.type.equals("add") && message[0].equals("0")) {
             initvars(key);
           } else if (this.type.equals("update") && message[0].equals("0")) {
             initvars(key);    
           } else if (this.type.equals("run")) {
                if (this.key[0].equals("commit")) {
                      initvars(null);
                }
                if (this.key[0].equals("tabledetailMouseClicked")) {
                      done_tabledetailMouseClicked();
                }
                if (this.key[0].equals("ddwhActionPerformed")) {
                      done_ddwhActionPerformed();
                }
                if (this.key[0].equals("ddbilltoActionPerformed")) {
                      done_ddbilltoActionPerformed();
                }
                if (this.key[0].equals("getCustShipSet")) {
                      done_getCustShipSet();
                }
                if (this.key[0].equals("getOrderMstrSet")) {
                      done_getOrderMstrSet();
                }
                
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
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
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
        
        ArrayList<String[]> initDataSets = shpData.getShipperInit(this.getClass().getName(), bsmf.MainFrame.userid);
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Lines", panelDetail);
        jTabbedPane1.add("Attachments", panelAttachment);
                
        attachmentmodel.setNumRows(0);
        tableattachment.setModel(attachmentmodel);
        tableattachment.getTableHeader().setReorderingAllowed(false);
        tableattachment.getColumnModel().getColumn(0).setMaxWidth(100);
        
        buttonGroup1.add(rborder);
        buttonGroup1.add(rbnonorder);
        
        sacmodel.setRowCount(0);
        
        rborder.setEnabled(false);
        rbnonorder.setEnabled(false);
        
        ordercount = 0;
        
        tbkey.setText("");
        cbcomplete.setSelected(false);
        
        java.util.Date now = new java.util.Date();
        dcshipdate.setDate(now);
        
        String defaultsite = null;
         ddwh.removeAllItems();
         ddshipfrom.removeAllItems();
         ddloc.removeAllItems();
         dduom.removeAllItems();
         ddcont.removeAllItems();
         ddshipto.removeAllItems();
         ddcust.removeAllItems();
         ddorder.removeAllItems();
         ddshipvia.removeAllItems();
         ddsite.removeAllItems();
         
        for (String[] s : initDataSets) {
           
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }  
            if (s[0].equals("autopost")) {
              autopost = BlueSeerUtils.ConvertStringToBool(s[1]);  
            } 
            if (s[0].equals("canconfirm")) {
              canconfirm = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("warehouses")) {
              ddwh.addItem(s[1]); 
              ddshipfrom.addItem(s[1]); 
            }
            if (s[0].equals("locations")) {
              ddloc.addItem(s[1]); 
            }           
            if (s[0].equals("uoms")) {
              dduom.addItem(s[1]); 
            }           
            if (s[0].equals("customers")) {
              ddcust.addItem(s[1]); 
            }
            if (s[0].equals("carriers")) {
              ddshipvia.addItem(s[1]); 
            }
            if (s[0].equals("items")) {
              ddcont.addItem(s[1]); 
            }
            if (s[0].equals("orders")) {
              ddorder.addItem(s[1]); 
            }
            if (s[0].equals("autonumber")) {
                autonumber = BlueSeerUtils.ConvertStringToBool(s[1]);
            }
        }
        
             
        ddwh.insertItemAt("", 0);
        ddwh.setSelectedIndex(0);
        ddshipfrom.insertItemAt("", 0);
        ddshipfrom.setSelectedIndex(0);
        
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
                
        dduom.insertItemAt("", 0);
        dduom.setSelectedIndex(0);
        
        ddcont.insertItemAt("", 0);
        ddcont.setSelectedIndex(0);
        
                 
        ddshipto.insertItemAt("", 0);
        ddshipto.setSelectedIndex(0);
          
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        
        ddorder.insertItemAt("", 0);
        ddorder.insertItemAt("none",1);
        ddorder.setSelectedIndex(0);
               
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        
        ddsite.setSelectedItem(defaultsite);
        
        
        
        btshipto.setEnabled(false);
        btorder.setEnabled(false);
        ddorder.setEnabled(false);
        
        tbtotdollars.setText("");
        tbtotqty.setText("");
        totlines.setText("");
        
        tbitem.setText("");
        tbtrailer.setText("");
        tbremarks.setText("");
        tbref.setText("");
        tbpallets.setText("0");
        tbboxes.setText("0");
        tbcontqty.setText("");
        
        lbladdr.setText("");
                
        
        tbqty.setText("");
        tbdesc.setText("");
        tbprice.setText("");
        tbcustitem.setText("");
         btlookup.setEnabled(true);
         btnew.setEnabled(true);
         btupdate.setEnabled(true);
         btadd.setEnabled(true);
         btprintshipper.setEnabled(false);
         btprintinvoice.setEnabled(false);
         btcommit.setEnabled(false);
         
         lblstatus.setText("");
        
        myshipdetmodel.addTableModelListener(ml); 
        tabledetail.setModel(myshipdetmodel);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        myshipdetmodel.setRowCount(0);
        tabledetail.getColumnModel().getColumn(10).setMaxWidth(0);
        tabledetail.getColumnModel().getColumn(10).setMinWidth(0);
        tabledetail.getColumnModel().getColumn(10).setPreferredWidth(0);
        tabledetail.getColumnModel().getColumn(11).setMaxWidth(0);
        tabledetail.getColumnModel().getColumn(11).setMinWidth(0);
        tabledetail.getColumnModel().getColumn(11).setPreferredWidth(0);
        
        if (autonumber) {
            tbkey.setBackground(bsmf.MainFrame.nonEditableColor);
        }
        
    }
    
    public void setAction(String[] x) {
        
        if (x[0].equals("0")) {
           
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                    // custom set
                    
                    // refreshDisplayTotals();
                   
                    if (status.equals("1")) {
                    btadd.setEnabled(false);
                    btupdate.setEnabled(false);
                    btcommit.setEnabled(false);
                    btupdateitem.setEnabled(false);
                    btadditem.setEnabled(false);
                    btdelitem.setEnabled(false);
                    lblstatus.setText(getMessageTag(1148));
                    lblstatus.setForeground(Color.blue);
                    btprintshipper.setEnabled(true);
                    btprintinvoice.setEnabled(true);
                   
                } else {
                    btadd.setEnabled(false);
                    btupdate.setEnabled(true);
                    btupdateitem.setEnabled(true);
                    btadditem.setEnabled(true);
                    btdelitem.setEnabled(true);
                     if (canconfirm) {
                        btcommit.setEnabled(true);
                    }
                    lblstatus.setText(getMessageTag(1149));
                    lblstatus.setForeground(Color.red);
                    btprintshipper.setEnabled(true);
                    btprintinvoice.setEnabled(false);
                }  
                   
        } else {
                   tbkey.setForeground(Color.red); 
        }
        
       
    }
    
    
    public void newAction(String x) {
        isLoad = true;
        setPanelComponentState(this, true);        
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btcommit.setEnabled(false);
        btprintinvoice.setEnabled(false);
        btprintshipper.setEnabled(false);
        btupdate.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        rborder.setEnabled(true);
        rbnonorder.setEnabled(true);
        rbnonorder.setSelected(true);
        btshipto.setEnabled(true);
        tbkey.setForeground(Color.blue);
        
        jTabbedPane1.setEnabledAt(1, false);
        
        if (autonumber) {  
              tbkey.setText(bsNumber(OVData.getNextNbr(x)));
              tbkey.setEditable(false);
              tbkey.setEnabled(false);
        } else {
              tbkey.setText("");
              tbkey.setEditable(true);
              tbkey.setEnabled(true);
        }
          
       
        tbkey.requestFocus();
        isLoad = false;
    }
    
     public String[] addRecord(String[] x) {
     String[] m;
     m = addShipperTransaction(createDetRecord(), createRecord(), createTreeRecord());
     shpData.updateShipperSAC(x[0]);
     return m;
     }
     
    public String[] updateRecord(String[] x) {
        String[] m;
        // first delete any ship_det line records that have been
        // disposed from the current shipdet table
        ArrayList<String> badlines = getBadLines(tbkey.getText());
        
        // now update
        m = updateShipTransaction(badlines, createDetRecord(), createRecord());
        shpData.updateShipperSAC(x[0]);
        
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
        String[] m;
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
            m = deleteShipMstr(x[0]);
        } else {
            m = new String[]{"0", getMessageTag(1192)};
        }
     return m;
     
    }
      
    public String[] getRecord(String[] key) {
       
      Shipper z = getShipperMstrSet(key);
      sh = z.sh();
      shdlist = z.shd();
      shslist = z.shs();
      shtlist = z.sht();
      shmlist = z.shmeta();
      cms = z.cms();
      getAttachments(key[0]);
      
      return z.m();
    }
        
    public void initvars(String[] arg) {
        
        isLoad = true;
        setPanelComponentState(this, false); 
        setComponentDefaultValues();
        
      
     
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        isLoad = false;
        
         if (arg != null && arg.length > 0) {
           // getshipperinfo(arg[0]);
           executeTask(dbaction.get, arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
     
    }
    
    public String[] run_autoInvoice() {
        
        String[] m = confirmShipperTransaction("order", tbkey.getText(), dcshipdate.getDate());
        // autopost
        if (autopost) {
            fglData.PostGL();
        }
        return m;
    }
    
    public String[] run_tabledetailMouseClicked(String item) {
        rtabledetail = invData.getBOMsByItemSite_mg(item);
        if (rtabledetail.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }
    
    public String[] run_ddwhActionPerformed(String wh) {
        rwhaction = invData.getLocationListByWarehouse(wh);
        if (rwhaction.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }
    
    public String[] run_ddbilltoActionPerformed(String billto) {
        rbilltoaction = cusData.getcustshipmstrlist(billto);
        if (rbilltoaction.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }
    
    public String[] run_getCustShipSet(String billto, String shipto) {
        css = cusData.getCustShipSet(new String[]{billto, shipto}); 
        if (css == null) {
           bsmf.MainFrame.show(getMessageTag(1034,billto + "/" + shipto));
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }
    
    public String[] run_getOrderMstrSet(String order) {
        so = getOrderMstrSet(new String[]{order});
        if (so == null) {
           bsmf.MainFrame.show(getMessageTag(1034,order));   
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }
    
    public void done_updateForm() {
        isLoad = true;
        tbkey.setText(sh.sh_id());
        cbcomplete.setSelected(BlueSeerUtils.ConvertStringToBool(sh.sh_char2())); 
        ddcust.setSelectedItem(sh.sh_cust());
        if (ddshipto.getItemCount() <= 1) {
            ddshipto.addItem(sh.sh_ship());
            ddshipto.setSelectedItem(sh.sh_ship());
        }
        tbref.setText(sh.sh_ref());
        tbboxes.setText(bsNumber(sh.sh_boxes()));
        tbpallets.setText(bsNumber(sh.sh_pallets()));
        tbremarks.setText(sh.sh_rmks());
        tbtrailer.setText(sh.sh_trailer());
        dcshipdate.setDate(parseDate(sh.sh_shipdate()));
        ddshipvia.setSelectedItem(sh.sh_shipvia());
        ddsite.setSelectedItem(sh.sh_site());
        ddshipfrom.setSelectedItem(sh.sh_shipfrom());
        status = sh.sh_status();
        lbladdr.setText(cms.cms_name() + "  " + cms.cms_line1() + "..." + cms.cms_city() +
                        ", " + cms.cms_state() + " " + cms.cms_zip());
        
        for (ship_det shd : shdlist) {
                    myshipdetmodel.addRow(new Object[]{shd.shd_line(), shd.shd_item(), 
                      shd.shd_so(),
                      shd.shd_soline(),
                      shd.shd_po(), 
                      bsNumber(shd.shd_qty()), 
                      bsNumber(shd.shd_netprice()),
                      shd.shd_desc(),
                      shd.shd_wh(),
                      shd.shd_loc(),
                      bsNumber(shd.shd_disc()),
                      bsNumber(shd.shd_listprice()),
                      bsNumber(shd.shd_taxamt()),
                      shd.shd_serial(),
                      shd.shd_cont(),
                      shd.shd_bom(),
                      bsNumber(shd.shd_qty()), // cont qty
                      shd.shd_uom(),
                      shd.shd_custitem()
                      
                  });
                }
        
           
        for (shs_det s : shslist) {
             sacmodel.addRow(new Object[]{
             s.shs_so(), s.shs_desc(), s.shs_type(), s.shs_amttype(), s.shs_amt()
             });
        }
        sactable.setModel(sacmodel);
        
        
        refreshDisplayTotals();
        
        setAction(sh.m());
        
        sh = null;
        shdlist = null;
        shslist = null;
        shmlist = null;
        cms = null;
        
        isLoad = false;
    }
    
    public void done_tabledetailMouseClicked() {
        ddbom.removeAllItems();
        ddbom.insertItemAt("", 0);
        
        for (String[] bom : rtabledetail) {
            if (bom[0].equals("boms")) {
                ddbom.addItem(bom[1]);
            }
            if (bom[0].equals("bomprimary")) {
                ddbom.setSelectedItem(bom[1]);
            }
        }
    }   
    
    public void done_ddwhActionPerformed() {
        ddloc.removeAllItems();
             for (String lc : rwhaction) {
                ddloc.addItem(lc);
             }
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
    }
        
    public void done_ddbilltoActionPerformed() {
        ddshipto.removeAllItems();
        for (String s : rbilltoaction) {
            ddshipto.addItem(s);
        }

        if (ddcust.getSelectedItem().toString() != null && ! ddcust.getSelectedItem().toString().isEmpty() && ddshipto.getItemCount() <= 0) {
       bsmf.MainFrame.show(getMessageTag(1108));
       ddcust.requestFocus();
        }
    }
    
    public void done_getCustShipSet() {
        disableradiobuttons();
        
        if (css == null) {
            return; 
        } 
        
        curr = css.cm().cm_curr();
        lbladdr.setText(css.cms().cms_name() + "  " + css.cms().cms_line1() + "..." + css.cms().cms_city() +
                        ", " + css.cms().cms_state() + " " + css.cms().cms_zip());
        ddshipvia.setSelectedItem(css.cm().cm_carrier());
        ddsite.setSelectedItem(css.cm().cm_site());
        ddshipfrom.setSelectedItem(css.cm().cm_site());
        terms = css.cm().cm_terms();
        taxcode = css.cm().cm_tax_code();
        aracct = css.cm().cm_ar_acct();
        arcc = css.cm().cm_ar_cc();
        
        
        if (terms.isEmpty() || aracct.isEmpty() || arcc.isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1090));
        }

        disablechoices();

        
        jTabbedPane1.setEnabledAt(1, true);
        btadd.setEnabled(true);
        btupdate.setEnabled(false);
    }
    
    public void done_getOrderMstrSet() {
        disableradiobuttons();
        double qtyavailable = 0.0;
       
        
        if (so == null) {
            ddshipto.setSelectedIndex(0);
            ddorder.requestFocus(); 
            return;
        } 
        
        if (so.so().so_status().equals(getGlobalProgTag("closed"))) {
            bsmf.MainFrame.show(getMessageTag(1097));
            ddshipto.setSelectedIndex(0);
            ddorder.requestFocus(); 
            return;
        }
        
        curr = so.so().so_curr();
        podate = so.so().so_ord_date();
        lbladdr.setText(so.cms().cms_name() + "  " + so.cms().cms_line1() + "..." + so.cms().cms_city() +
                        ", " + so.cms().cms_state() + " " + so.cms().cms_zip());
        ddshipvia.setSelectedItem(so.so().so_shipvia());
        ddsite.setSelectedItem(so.so().so_site());
        ddshipfrom.setSelectedItem(so.so().so_site());
        terms = so.cm().cm_terms();
        taxcode = so.so().so_taxcode();
        aracct = so.cm().cm_ar_acct();
        arcc = so.cm().cm_ar_cc();
        
        
        if (terms.isEmpty() || aracct.isEmpty() || arcc.isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1090));
        }

        // add line item info to lines table
        for (sod_det sod : so.sod()) {
               qtyavailable = sod.sod_ord_qty() - sod.sod_shipped_qty();
                 myshipdetmodel.addRow(new Object[]{sod.sod_line(), 
                     sod.sod_item(), 
                     sod.sod_nbr(),
                     sod.sod_line(),
                     sod.sod_po(), 
                     String.valueOf(qtyavailable), 
                     sod.sod_netprice(),
                     sod.sod_desc(),
                     sod.sod_wh(),
                     sod.sod_loc(),
                     sod.sod_disc(),
                     sod.sod_listprice(),
                     sod.sod_taxamt(),
                     "", // cont
                     "",  // serialno
                     sod.sod_bom(),
                     String.valueOf(qtyavailable), // cont qty
                     sod.sod_uom(),
                     sod.sod_custitem()
                     }); 
        }
        tabledetail.setModel(myshipdetmodel);
       
        for (sos_det sos : so.sos() ) {
             sacmodel.addRow(new Object[]{
             sos.sos_nbr(), sos.sos_desc(), sos.sos_type(), sos.sos_amttype(), sos.sos_amt()
             });
        }
       sactable.setModel(sacmodel);
        
        
        refreshDisplayTotals();
            
           
        disablechoices();

        
        jTabbedPane1.setEnabledAt(1, true);
        btadd.setEnabled(true);
        btupdate.setEnabled(false);
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
                getClassLabelTag("lblbillto", this.getClass().getSimpleName())); 
        
    }

    public void lookUpFrameOrderLine() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
         
        if (lurb1.isSelected()) {  
         luModel = DTData.getOrderDetailBrowseUtil(luinput.getText(), "sod_item", ddcust.getSelectedItem().toString(), ddshipto.getSelectedItem().toString() );
        } else {
         luModel = DTData.getOrderDetailBrowseUtil(luinput.getText(), "sod_desc", ddcust.getSelectedItem().toString(), ddshipto.getSelectedItem().toString() );
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
                tbordernbr.setText(target.getValueAt(row,1).toString());
                tborderline.setText(target.getValueAt(row,2).toString());
                setDetail(tbordernbr.getText(), tborderline.getText());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName()));
        
        
    }

    public void lookUpFrameBillTo() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_name");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_code");   
        } else {
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_zip");   
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
                ddcust.setSelectedItem(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblname", this.getClass().getSimpleName()), 
                getClassLabelTag("lblcode", this.getClass().getSimpleName()),
                getClassLabelTag("lblzip", this.getClass().getSimpleName())); 
        
        
    }
 
    public ship_mstr createRecord() {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String uniqwh = getUniqueWH();
        String uniqpo = getUniquePO();
        
        int pallets = tbpallets.getText().isBlank() ? 0 : bsParseInt(tbpallets.getText());
        int boxes = tbboxes.getText().isBlank() ? 0 : bsParseInt(tbboxes.getText());
              
        
        ship_mstr x = new ship_mstr(null,  
                tbkey.getText(),
                ddcust.getSelectedItem().toString(),
                ddshipto.getSelectedItem().toString(),
                pallets,
                boxes,
                ddshipvia.getSelectedItem().toString(),  
                setDateDB(dcshipdate.getDate()),
                podate,
                tbref.getText().replace("'", ""),
                uniqpo,
                tbremarks.getText(),
                bsmf.MainFrame.userid,
                ddsite.getSelectedItem().toString(),
                curr,
                uniqwh,
                terms,
                taxcode,
                aracct,
                arcc,
                "S", // type
                "", // sh_so 
                ddshipfrom.getSelectedItem().toString(),
                tbtrailer.getText(),
                "", // status 
                "", // sh_char1
                String.valueOf(BlueSeerUtils.boolToInt(cbcomplete.isSelected())), // sh_char2 
                "" // sh_char3
        );
                
        return x;        
    }
    
    public ArrayList<ship_det> createDetRecord() {
        ArrayList<ship_det> list = new ArrayList<ship_det>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String uniqwh = getUniqueWH();
        String uniqpo = getUniquePO();
        // line, item, order, orderline, po, qty, netprice, desc, wh, loc, disc, listprice, tax, cont, serial
        for (int j = 0; j < tabledetail.getRowCount(); j++) { 
            ship_det x = new ship_det(null, 
                tbkey.getText(), // shipper
                bsParseInt(tabledetail.getValueAt(j, 0).toString()), //shline
                tabledetail.getValueAt(j, 1).toString(), // item
                tabledetail.getValueAt(j, 18).toString(), // custimtem
                tabledetail.getValueAt(j, 2).toString(),  // order
                bsParseInt(tabledetail.getValueAt(j, 3).toString()), //soline    
                setDateDB(dcshipdate.getDate()),
                tabledetail.getValueAt(j, 4).toString(),
                bsParseDouble(tabledetail.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.')), // qty
                tabledetail.getValueAt(j, 17).toString(), //uom
                "", //currency
                bsParseDouble(tabledetail.getValueAt(j, 6).toString().replace(defaultDecimalSeparator, '.')), // netprice
                bsParseDouble(tabledetail.getValueAt(j, 10).toString().replace(defaultDecimalSeparator, '.')), // disc
                bsParseDouble(tabledetail.getValueAt(j, 11).toString().replace(defaultDecimalSeparator, '.')), // listprice
                tabledetail.getValueAt(j, 7).toString(), // desc
                tabledetail.getValueAt(j, 8).toString(), // wh
                tabledetail.getValueAt(j, 9).toString(), // loc
                bsParseDouble(tabledetail.getValueAt(j, 12).toString().replace(defaultDecimalSeparator, '.')), // taxamt
                tabledetail.getValueAt(j, 14).toString(), // cont
                "", // ref
                tabledetail.getValueAt(j, 13).toString(),  // serial   
                ddsite.getSelectedItem().toString(),
                tabledetail.getValueAt(j, 15).toString(), // bom
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
        
            shpData.ship_tree x = new shpData.ship_tree(null,
            tbkey.getText(),
            "", // ... no labels
            ddsite.getSelectedItem().toString(),
            "f", // flat ...no labels
            tbkey.getText(),
            "",
            "",
            "",
            "",
            "",
            1.0,
            "" // get display serial
            );
            
            list.add(x);
            // now items of container
            for (int j = 0; j < tabledetail.getRowCount(); j++) { 
                
                    shpData.ship_tree y = new shpData.ship_tree(null,
                    tbkey.getText(), 
                    tabledetail.getValueAt(j, 2).toString() + "," + tabledetail.getValueAt(j, 1).toString() + "," + tabledetail.getValueAt(j, 0).toString(),
                    ddsite.getSelectedItem().toString(),
                    "i",
                    tbkey.getText(),
                    tabledetail.getValueAt(j, 0).toString(),
                    tabledetail.getValueAt(j, 2).toString(),
                    tabledetail.getValueAt(j, 0).toString(),
                    tabledetail.getValueAt(j, 4).toString(),
                    tabledetail.getValueAt(j, 1).toString(),
                    bsParseDouble(tabledetail.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.')),
                    "" // get display serial
                    );
                    list.add(y);
                
            }
       
       
        return list;        
    }
    
    
    // misc methods
    public ArrayList<String> getBadLines(String key) {
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        
        lines = getShipperLineNumbers(key);
       for (String line : lines) {
          goodLine = false;
          for (int j = 0; j < tabledetail.getRowCount(); j++) {
             if (tabledetail.getValueAt(j, 0).toString().equals(line)) {
                 goodLine = true;
             }
          }
          if (! goodLine) {
              badlines.add(line);
          }
        }
       return badlines;
    }
        
    public void getAttachments(String id) {
        attachmentmodel.setNumRows(0);
        ArrayList<String> list = OVData.getSysMetaData(id, this.getClass().getSimpleName(), "attachments");
        for (String file : list) {
        attachmentmodel.addRow(new Object[]{BlueSeerUtils.clickflag,  
                               file
            });
        }
    }
    
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < tabledetail.getRowCount(); j++) {
            current = Integer.valueOf(tabledetail.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
    
    public void sumlinecount() {
         totlines.setText(String.valueOf(tabledetail.getRowCount()));
    }
     
    public void sumqty() {
        double qty = 0.00;
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             qty = qty + Double.valueOf(tabledetail.getValueAt(j, 5).toString()); 
         }
         tbtotqty.setText(String.valueOf(qty));
    }
    
    public void sumdollars() {
        
        double dol = 0;
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             dol = dol + ( bsParseDouble(tabledetail.getValueAt(j, 5).toString()) * bsParseDouble(tabledetail.getValueAt(j, 11).toString()) ); 
         }
         // now add trailer/summary discounts/charges if any
         for (int j = 0; j < sactable.getRowCount(); j++) {
            dol += bsParseDouble(sactable.getValueAt(j,4).toString());
        }
         tbtotdollars.setText(bsFormatDouble(dol));
    }
     
    public void retotal() {
         
    }
        
    public void disableshipto() {
        if (! isLoad) {
        btshipto.setEnabled(false);
        ddshipto.setEnabled(false);
        ddcust.setEnabled(false);
         btorder.setEnabled(true);
         ddorder.setEnabled(true);
        }
    }
     
    public void enableshipto() {
        if (! isLoad) {
         ddshipto.setEnabled(true);
         ddcust.setEnabled(true);
         btshipto.setEnabled(true);
         btorder.setEnabled(false);
         ddorder.setEnabled(false);
         
         
         if (ddcust.getItemCount() > 0) {
             ddcust.setSelectedIndex(0);
         }
        }
    }
    
    public void disablechoices() {
        btorder.setEnabled(false);
        btshipto.setEnabled(false);
        ddorder.setEnabled(false);
        ddshipto.setEnabled(false);
        ddcust.setEnabled(false);
        
    }
    
    public void disableradiobuttons() {
        rborder.setEnabled(false);
        rbnonorder.setEnabled(false);
    }
    
    public void reinitshippervariables(String myshipper) {
       
        tbkey.setText(bsNumber(myshipper));
        if (myshipper.compareTo("") == 0) {
            btadd.setEnabled(true);

        } else {
            btadd.setEnabled(false);
        }


       
        tbkey.setText(bsNumber(myshipper));
        
        tbqty.setText("");
        tbdesc.setText("");
        tbcustitem.setText("");
        tbref.setText("");
        tbboxes.setText("");
        tbpallets.setText("");
        
        
        
        tabledetail.setModel(myshipdetmodel);
        myshipdetmodel.setRowCount(0);
        btlookup.setEnabled(true);
        btnew.setEnabled(true);
        btprintshipper.setEnabled(true);

     
    }
        
    public String getUniquePO() {
        int d = 0;
        String uniqpo = "";
        for (int j = 0; j < tabledetail.getRowCount(); j++) {
         if (d > 0) {
           if ( uniqpo.compareTo(tabledetail.getValueAt(j, 4).toString()) != 0) {
           uniqpo = "multi-PO";
           break;
           }
         }
         d++;
         uniqpo = tabledetail.getValueAt(j, 4).toString();
       }
        return uniqpo;
    }  
    
    public String getUniqueWH() {
           // lets collect single or multiple Warehouse status
        int d = 0;
        String uniqwh = "";
       for (int j = 0; j < tabledetail.getRowCount(); j++) {
         if (d > 0) {
           if ( uniqwh.compareTo(tabledetail.getValueAt(j, 8).toString()) != 0) {
           uniqwh = "multi-WH";
           break;
           }
         }
         d++;
         uniqwh = tabledetail.getValueAt(j, 8).toString();
       }
       return uniqwh;
    }
         
    public void setDetail(String nbr, String line) {
        
        
        // reset fields
        tbitem.setText("");
        tbpo.setText("");
        tbserial.setText("");
        tbdesc.setText("");
        tbcustitem.setText("");
        tbqty.setText("");
        tbprice.setText("");
        dduom.setSelectedIndex(0);
        ddcont.setSelectedIndex(0);
        ddbom.removeAllItems();
        
        // get order detail data
        sod_det sod = getOrderDet(nbr, line);
        item_mstr it = getItemMstr(sod.sod_item()); 
        
        // set fields
        tbitem.setText(sod.sod_item());
        tbpo.setText(sod.sod_po());
        tbdesc.setText(sod.sod_desc());
        tbcustitem.setText(sod.sod_custitem());
        tbqty.setText(String.valueOf(sod.sod_ord_qty() - sod.sod_shipped_qty()));
        tbprice.setText(bsNumber(sod.sod_netprice()));
        dduom.setSelectedItem(sod.sod_uom());
        ddcont.setSelectedItem(it.it_cont());
        tbcontqty.setText(bsNumber(it.it_contqty()));
        podate = sod.sod_ord_date();
        tbcustitem.setText(sod.sod_custitem());
        ddbom.insertItemAt("", 0);
        ddbom.setSelectedIndex(0);
        ArrayList<String[]> boms = invData.getBOMsByItemSite_mg(sod.sod_item());
        if (boms != null) {
            for (String[] wh : boms) {
            ddbom.addItem(wh[0]);
            }
            if (! sod.sod_bom().isEmpty()) {
            ddbom.setSelectedItem(sod.sod_bom());
            }
        }
    }
    
   
    public boolean validateDetail() {
       // if user clicks on 'additem' before focuslost on each field
       // has time to fire, focuslost will have effectively set these fields to empty upon
       // seeing an error before this function is called
       // ...so we check for empty to prevent lines from being added
        
        if (tbqty.getText().isEmpty()) {
            return false;
        }
        if (tbprice.getText().isEmpty()) {
            return false;
        }
      
        
        boolean isvalid = OVData.isValidItem(tbitem.getText());
        
       
        
        if (isvalid && ! OVData.isValidUOMConversion(tbitem.getText(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString())) {
                bsmf.MainFrame.show(getMessageTag(1093));
                dduom.requestFocus();
                return false;
                
        }
        if (isvalid && ! OVData.isBaseUOMOfItem(tbitem.getText(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString())) {
                bsmf.MainFrame.show(getMessageTag(1094));
                dduom.requestFocus();
                return false;
        }
      return true;   
    }
    
    public void refreshDisplayTotals() {
        sumqty();
        sumdollars();
        sumlinecount();
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel5 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelMain = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btprintshipper = new javax.swing.JButton();
        btprintinvoice = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        HeaderPanel = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        dcshipdate = new com.toedter.calendar.JDateChooser();
        tbtrailer = new javax.swing.JTextField();
        ddshipvia = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        tbremarks = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        tbpallets = new javax.swing.JTextField();
        tbboxes = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ddshipfrom = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        ChoicePanel = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        btorder = new javax.swing.JButton();
        btshipto = new javax.swing.JButton();
        jLabel104 = new javax.swing.JLabel();
        ddshipto = new javax.swing.JComboBox<>();
        ddorder = new javax.swing.JComboBox<>();
        ddcust = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        lbbilltoname = new javax.swing.JLabel();
        lbladdr = new javax.swing.JLabel();
        btLookUpBillTo = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        rborder = new javax.swing.JRadioButton();
        rbnonorder = new javax.swing.JRadioButton();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        cbcomplete = new javax.swing.JCheckBox();
        panelDetail = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btadditem = new javax.swing.JButton();
        btdelitem = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lblwhqty = new javax.swing.JLabel();
        lbllocqty = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ddcont = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        tbpo = new javax.swing.JTextField();
        tbitem = new javax.swing.JTextField();
        tbcontqty = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tbordernbr = new javax.swing.JTextField();
        tborderline = new javax.swing.JTextField();
        btlookupOrderLine = new javax.swing.JButton();
        tbcustitem = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        dduom = new javax.swing.JComboBox<>();
        tbqty = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        lbqtyshipped = new javax.swing.JLabel();
        cbexplode = new javax.swing.JCheckBox();
        tbserial = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        ddwh = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        ddloc = new javax.swing.JComboBox<>();
        ddbom = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        btupdateitem = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        sactable = new javax.swing.JTable();
        panelAttachment = new javax.swing.JPanel();
        labelmessage = new javax.swing.JLabel();
        btaddattachment = new javax.swing.JButton();
        btdeleteattachment = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableattachment = new javax.swing.JTable();

        jLabel5.setText("jLabel5");

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Shipper Maintenance"));
        panelMain.setName("panelmain"); // NOI18N
        panelMain.setPreferredSize(new java.awt.Dimension(912, 600));

        jLabel24.setText("Shipper#");
        jLabel24.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btprintshipper.setText("Print Shipper");
        btprintshipper.setName("btprintshipper"); // NOI18N
        btprintshipper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintshipperActionPerformed(evt);
            }
        });

        btprintinvoice.setText("Print Invoice");
        btprintinvoice.setName("btprintinvoice"); // NOI18N
        btprintinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintinvoiceActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        HeaderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Header"));
        HeaderPanel.setName("panelheader"); // NOI18N

        jLabel25.setText("Site:");
        jLabel25.setName("lblsite"); // NOI18N

        jLabel35.setText("Shipdate:");
        jLabel35.setName("lblshipdate"); // NOI18N

        dcshipdate.setDateFormatString("yyyy-MM-dd");

        jLabel39.setText("ShipVia:");
        jLabel39.setName("lblshipvia"); // NOI18N

        jLabel40.setText("Tracking Number:");
        jLabel40.setName("lbltrack"); // NOI18N

        jLabel27.setText("Ref:");
        jLabel27.setName("lblref"); // NOI18N

        jLabel41.setText("Remarks:");
        jLabel41.setName("lblremarks"); // NOI18N

        tbpallets.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpalletsFocusLost(evt);
            }
        });

        tbboxes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbboxesFocusLost(evt);
            }
        });

        jLabel7.setText("Pallets:");
        jLabel7.setName("lblpallets"); // NOI18N

        jLabel8.setText("Boxes:");
        jLabel8.setName("lblboxes"); // NOI18N

        jLabel13.setText("ShipFrom:");

        javax.swing.GroupLayout HeaderPanelLayout = new javax.swing.GroupLayout(HeaderPanel);
        HeaderPanel.setLayout(HeaderPanelLayout);
        HeaderPanelLayout.setHorizontalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel39)
                    .addComponent(jLabel41)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel25)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbboxes, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(HeaderPanelLayout.createSequentialGroup()
                                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(62, 62, 62)
                                        .addComponent(jLabel35))
                                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                                        .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel40))
                                    .addComponent(tbpallets, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dcshipdate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddshipfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        HeaderPanelLayout.setVerticalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel35))
                    .addComponent(dcshipdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpallets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbboxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addContainerGap(178, Short.MAX_VALUE))
        );

        jLabel36.setText("ShipTo:");
        jLabel36.setName("lblshipto"); // NOI18N

        btorder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btorderActionPerformed(evt);
            }
        });

        btshipto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshiptoActionPerformed(evt);
            }
        });

        jLabel104.setText("Order:");
        jLabel104.setName("lblorder"); // NOI18N

        ddorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddorderActionPerformed(evt);
            }
        });

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        jLabel2.setText("Bill To:");
        jLabel2.setName("lblbillto"); // NOI18N

        lbladdr.setBackground(java.awt.Color.lightGray);

        btLookUpBillTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpBillTo.setToolTipText("lookup");
        btLookUpBillTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpBillToActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ChoicePanelLayout = new javax.swing.GroupLayout(ChoicePanel);
        ChoicePanel.setLayout(ChoicePanelLayout);
        ChoicePanelLayout.setHorizontalGroup(
            ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel104, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddorder, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ChoicePanelLayout.createSequentialGroup()
                        .addComponent(btLookUpBillTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lbbilltoname, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ChoicePanelLayout.createSequentialGroup()
                        .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btshipto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btorder, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                        .addGap(9, 9, 9)
                        .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        ChoicePanelLayout.setVerticalGroup(
            ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ChoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbbilltoname, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btLookUpBillTo)
                        .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel36)
                        .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btshipto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel104))
                    .addComponent(btorder))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblstatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        rborder.setText("Single Order");
        rborder.setName("cbsingle"); // NOI18N
        rborder.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rborderStateChanged(evt);
            }
        });
        rborder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rborderActionPerformed(evt);
            }
        });

        rbnonorder.setText("Multi Order");
        rbnonorder.setName("cbmulti"); // NOI18N
        rbnonorder.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbnonorderStateChanged(evt);
            }
        });
        rbnonorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnonorderActionPerformed(evt);
            }
        });

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

        cbcomplete.setText("Complete");

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel24)
                .addGap(5, 5, 5)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbnonorder)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rborder)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbcomplete)))
                .addGap(66, 66, 66)
                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(panelMainLayout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addComponent(btcommit)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(btprintinvoice)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btprintshipper)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btadd)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btupdate)
                    .addContainerGap())
                .addComponent(HeaderPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelMainLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ChoicePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnew)
                                    .addComponent(btclear)
                                    .addComponent(cbcomplete))
                                .addGap(9, 9, 9))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel24))
                                    .addComponent(btlookup))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rborder)
                            .addComponent(rbnonorder)))
                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ChoicePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(HeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btprintshipper)
                    .addComponent(btprintinvoice)
                    .addComponent(btcommit)))
        );

        add(panelMain);

        panelDetail.setMinimumSize(new java.awt.Dimension(958, 477));
        panelDetail.setPreferredSize(new java.awt.Dimension(958, 600));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        jPanel2.setName("paneldetail"); // NOI18N

        btadditem.setText("Add Line");
        btadditem.setName("btaddline"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdelitem.setText("Delete Line");
        btdelitem.setName("btdeleteline"); // NOI18N
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        jPanel5.setPreferredSize(new java.awt.Dimension(762, 229));

        jLabel42.setText("PONbr");
        jLabel42.setName("lblponbr"); // NOI18N

        jLabel30.setText("Item");
        jLabel30.setName("lblitem"); // NOI18N

        jLabel38.setText("Desc");
        jLabel38.setName("lbldesc"); // NOI18N

        jLabel48.setText("Order");
        jLabel48.setName("lblorder"); // NOI18N

        jLabel6.setText("OrderLine");
        jLabel6.setName("lblorderline"); // NOI18N

        jLabel11.setText("Cont");
        jLabel11.setName("lblcont"); // NOI18N

        jLabel9.setText("ContQty");
        jLabel9.setName("lblcontqty"); // NOI18N

        btlookupOrderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupOrderLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupOrderLineActionPerformed(evt);
            }
        });

        jLabel14.setText("CustItem");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbcustitem, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookupOrderLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tborderline, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddcont, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbcontqty, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btlookupOrderLine)))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tborderline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcustitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcontqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(16, 16, 16))
        );

        jLabel47.setText("UOM");
        jLabel47.setName("lbluom"); // NOI18N

        jLabel44.setText("NetPrice");
        jLabel44.setName("lblnetprice"); // NOI18N

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel43.setText("Qty");
        jLabel43.setName("lblqty"); // NOI18N

        cbexplode.setText("Explode");
        cbexplode.setName("cbexplode"); // NOI18N
        cbexplode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbexplodeActionPerformed(evt);
            }
        });

        jLabel10.setText("Serial");
        jLabel10.setName("lblserial"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbprice)
                    .addComponent(dduom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbqtyshipped, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addComponent(cbexplode)
                .addGap(10, 10, 10))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbqty, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbqtyshipped, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbexplode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap())
        );

        jLabel46.setText("Location");
        jLabel46.setName("lblloc"); // NOI18N

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel45.setText("Warehouse");
        jLabel45.setName("lblwh"); // NOI18N

        jLabel12.setText("BOM");
        jLabel12.setName("lblbom"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddwh, 0, 76, Short.MAX_VALUE)
                    .addComponent(ddloc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddbom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddbom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblwhqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbllocqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(lblwhqty, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbllocqty, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );

        btupdateitem.setText("Update Line");
        btupdateitem.setName("btupdateline"); // NOI18N
        btupdateitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateitemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 756, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btupdateitem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btdelitem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btadditem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelitem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdateitem))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane7.setBorder(javax.swing.BorderFactory.createTitledBorder("Line Detail"));

        tabledetail.setModel(new javax.swing.table.DefaultTableModel(
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
        tabledetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabledetailMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(tabledetail);

        jLabel3.setText("Total Lines:");
        jLabel3.setName("lbltotallines"); // NOI18N

        jLabel1.setText("Total Qty:");
        jLabel1.setName("lbltotalqty"); // NOI18N

        jLabel4.setText("Total $");
        jLabel4.setName("lbltotalamt"); // NOI18N

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Summary Charges/Discounts"));

        sactable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sactable.setEnabled(false);
        jScrollPane2.setViewportView(sactable);

        javax.swing.GroupLayout panelDetailLayout = new javax.swing.GroupLayout(panelDetail);
        panelDetail.setLayout(panelDetailLayout);
        panelDetailLayout.setHorizontalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7)
                    .addComponent(jScrollPane2))
                .addContainerGap())
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(154, 154, 154)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(178, 178, 178)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDetailLayout.setVerticalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(0, 0, 0))
        );

        add(panelDetail);

        panelAttachment.setBorder(javax.swing.BorderFactory.createTitledBorder("Attachment Panel"));
        panelAttachment.setName("panelAttachment"); // NOI18N
        panelAttachment.setPreferredSize(new java.awt.Dimension(974, 560));

        btaddattachment.setText("Add Attachment");
        btaddattachment.setName("btaddattachment"); // NOI18N
        btaddattachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddattachmentActionPerformed(evt);
            }
        });

        btdeleteattachment.setText("Delete Attachment");
        btdeleteattachment.setName("btdeleteattachment"); // NOI18N
        btdeleteattachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteattachmentActionPerformed(evt);
            }
        });

        tableattachment.setModel(new javax.swing.table.DefaultTableModel(
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
        tableattachment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableattachmentMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableattachment);

        javax.swing.GroupLayout panelAttachmentLayout = new javax.swing.GroupLayout(panelAttachment);
        panelAttachment.setLayout(panelAttachmentLayout);
        panelAttachmentLayout.setHorizontalGroup(
            panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttachmentLayout.createSequentialGroup()
                .addGroup(panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAttachmentLayout.createSequentialGroup()
                        .addComponent(btaddattachment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteattachment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 439, Short.MAX_VALUE)
                        .addComponent(labelmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        panelAttachmentLayout.setVerticalGroup(
            panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttachmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btaddattachment)
                        .addComponent(btdeleteattachment)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE))
        );

        add(panelAttachment);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("shipper");  
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        tabledetail.setModel(myshipdetmodel);
        String part = "";
        
        int line = 0;
        line = getmaxline();
        
        
        int nbrOfContainers = 0;
        int remainder = 0;
        if (cbexplode.isSelected() && ! tbcontqty.getText().isEmpty()) {
            nbrOfContainers = Integer.valueOf(tbqty.getText()) / Integer.valueOf(tbcontqty.getText());
            remainder = Integer.valueOf(tbqty.getText()) % Integer.valueOf(tbcontqty.getText());
        }
                
        if (canproceed) {
            if (nbrOfContainers == 0) {
            line++;    
            myshipdetmodel.addRow(new Object[]{line, tbitem.getText(), 
                tbordernbr.getText(), 
                tborderline.getText(),
                tbpo.getText(), 
                tbqty.getText(), 
                tbprice.getText(),
                tbdesc.getText(),
                ddwh.getSelectedItem().toString(),
                ddloc.getSelectedItem().toString(),
                0,
                tbprice.getText(), 
                "0",  // matltax 
                tbserial.getText(),
                ddcont.getSelectedItem().toString(),
                ddbom.getSelectedItem().toString(),
                "0", // cont
                dduom.getSelectedItem().toString(),
                tbcustitem.getText()
            });
            } else {
                   for (int x = 0; x < nbrOfContainers; x++) {
                   line++;
                   myshipdetmodel.addRow(new Object[]{line, tbitem.getText(), 
                    tbordernbr.getText(), 
                    tborderline.getText(),
                    tbpo.getText(), 
                    tbcontqty.getText(), 
                    tbprice.getText(),
                    tbdesc.getText(),
                    ddwh.getSelectedItem().toString(),
                    ddloc.getSelectedItem().toString(),
                    0,
                    tbprice.getText(), 
                    "0",  // matltax 
                    ddcont.getSelectedItem().toString(),
                    tbserial.getText(),
                    ddbom.getSelectedItem().toString(),
                    tbcontqty.getText(), // cont
                    dduom.getSelectedItem().toString(),
                    tbcustitem.getText()
                }); 
                }
                   // now add extra if remainder greater than zero
                   if (remainder > 0) {
                    line++;   
                    myshipdetmodel.addRow(new Object[]{line, tbitem.getText(), 
                    tbordernbr.getText(), 
                    tborderline.getText(), 
                    tbpo.getText(), 
                    remainder, 
                    tbprice.getText(),
                    tbdesc.getText(),
                    ddwh.getSelectedItem().toString(),
                    ddloc.getSelectedItem().toString(),
                    0,
                    tbprice.getText(), 
                    "0",  // matltax 
                    ddcont.getSelectedItem().toString(),
                    tbserial.getText(),
                    ddbom.getSelectedItem().toString(),
                    remainder, // cont
                    dduom.getSelectedItem().toString(),
                    tbcustitem.getText()
                });    
                   }
            }
        }
        
        refreshDisplayTotals();
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(dbaction.add)) {
           return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});  
        
    }//GEN-LAST:event_btaddActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tabledetail.getModel()).removeRow(i);
        }
       refreshDisplayTotals();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btprintshipperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintshipperActionPerformed
         OVData.printShipperRemote(tbkey.getText(), "shipper");
       // OVData.printJTableToJasper("Shipper Report", tabledetail ); 

    }//GEN-LAST:event_btprintshipperActionPerformed

    private void btprintinvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintinvoiceActionPerformed
       OVData.printInvoiceRemote(tbkey.getText(), "shipper", true);
    }//GEN-LAST:event_btprintinvoiceActionPerformed

    private void btorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btorderActionPerformed
        if (ddorder.getSelectedItem() != null && ! ddorder.getSelectedItem().toString().isBlank()) {
        executeTask(dbaction.run, new String[]{"getOrderMstrSet", ddorder.getSelectedItem().toString()});  
        }
    }//GEN-LAST:event_btorderActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
         if (! validateInput(dbaction.update)) {
           return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshiptoActionPerformed
        if (ddcust.getSelectedItem() != null && ddshipto.getSelectedItem() != null && ! ddcust.getSelectedItem().toString().isBlank() && ! ddshipto.getSelectedItem().toString().isBlank()) {
          executeTask(dbaction.run, new String[]{"getCustShipSet", ddcust.getSelectedItem().toString(), ddshipto.getSelectedItem().toString()});
        }
    }//GEN-LAST:event_btshiptoActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        if (dcshipdate.getDate() != null) {
        setPanelComponentState(this, false);
        executeTask(dbaction.run, new String[]{"commit", tbkey.getText()}); 
        }
    }//GEN-LAST:event_btcommitActionPerformed

    private void ddorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddorderActionPerformed
              if (! isLoad && ddorder.getItemCount() > 0 && ddorder.getSelectedItem() != null && ! ddorder.getSelectedItem().toString().isBlank()) {
                  so_mstr so = getOrderMstr(ddorder.getSelectedItem().toString());
                  ddcust.setSelectedItem(so.so_cust());
                  ddshipto.setSelectedItem(so.so_ship());
              }
    }//GEN-LAST:event_ddorderActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed
         if (ddcust.getItemCount() > 0 && ! isLoad) {
           executeTask(dbaction.run, new String[]{"ddbilltoActionPerformed", ddcust.getSelectedItem().toString()});
        } 
    }//GEN-LAST:event_ddcustActionPerformed

    private void rborderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rborderActionPerformed
        if (rborder.isSelected()) {
            disableshipto();
        }
        if (rbnonorder.isSelected()) {
            enableshipto();
        }
    }//GEN-LAST:event_rborderActionPerformed

    private void rbnonorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbnonorderActionPerformed
       if (rborder.isSelected()) {
            disableshipto();
        }
        if (rbnonorder.isSelected()) {
            enableshipto();
        }
    }//GEN-LAST:event_rbnonorderActionPerformed

    private void rborderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rborderStateChanged
        /*
        if (rborder.isSelected()) {
            disableshipto();
        }
        if (rbnonorder.isSelected()) {
            enableshipto();
        }
      */
    }//GEN-LAST:event_rborderStateChanged

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
         if (ddwh.getSelectedItem() != null && ! isLoad) {
             executeTask(dbaction.run, new String[]{"ddwhActionPerformed", ddwh.getSelectedItem().toString()});
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        
    }//GEN-LAST:event_dduomActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        if (! tbqty.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbqty.getText(), "0");
            if (x.equals("error")) {
                tbqty.setText("");
                tbqty.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbqty.requestFocus();
            } else {
                tbqty.setText(x);
                tbqty.setBackground(Color.white);
            }
        } else {
             tbqty.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbqtyFocusLost

    private void btlookupOrderLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupOrderLineActionPerformed
        lookUpFrameOrderLine();
    }//GEN-LAST:event_btlookupOrderLineActionPerformed

    private void tbpalletsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpalletsFocusLost
        String x = BlueSeerUtils.bsformat("", tbpallets.getText(), "0");
        if (x.equals("error")) {
            tbpallets.setText("");
            tbpallets.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbpallets.requestFocus();
        } else {
            tbpallets.setText(x);
            tbpallets.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbpalletsFocusLost

    private void tbboxesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbboxesFocusLost
         String x = BlueSeerUtils.bsformat("", tbboxes.getText(), "0");
        if (x.equals("error")) {
            tbboxes.setText("");
            tbboxes.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbboxes.requestFocus();
        } else {
            tbboxes.setText(x);
            tbboxes.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbboxesFocusLost

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
        //   line, item, order, orderline, po, qty, price, desc, wh, loc, disc, listprice, tax, serial, cont, bom, contqty, uom
        
        executeTask(dbaction.run, new String[]{"tabledetailMouseClicked", tabledetail.getValueAt(row, 1).toString()}); 
        
        isLoad = true;  
        tbitem.setText(tabledetail.getValueAt(row, 1).toString());
        tbordernbr.setText(tabledetail.getValueAt(row, 2).toString());
        tborderline.setText(tabledetail.getValueAt(row, 3).toString());
        tbpo.setText(tabledetail.getValueAt(row, 4).toString());
        tbqty.setText(tabledetail.getValueAt(row, 5).toString());
        tbprice.setText(tabledetail.getValueAt(row, 6).toString());
        tbdesc.setText(tabledetail.getValueAt(row, 7).toString());
        ddwh.setSelectedItem(tabledetail.getValueAt(row, 8).toString());
        ddloc.setSelectedItem(tabledetail.getValueAt(row, 9).toString());
        ddcont.setSelectedItem(tabledetail.getValueAt(row, 14).toString());
        tbserial.setText(tabledetail.getValueAt(row, 13).toString());
        ddbom.setSelectedItem(tabledetail.getValueAt(row, 15).toString());
        dduom.setSelectedItem(tabledetail.getValueAt(row, 17).toString());
        tbcustitem.setText(tabledetail.getValueAt(row, 18).toString());
       
        isLoad = false;
    }//GEN-LAST:event_tabledetailMouseClicked

    private void btupdateitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateitemActionPerformed
        int line = 0;
        String bom = "";
        if (ddbom.getSelectedItem() != null) {
            bom = ddbom.getSelectedItem().toString();
        }
        String cont = "";
        if (ddcont.getSelectedItem() != null && ! ddcont.getSelectedItem().toString().isBlank()) {
            cont = ddcont.getSelectedItem().toString();
        }
        line = getmaxline();
        line++;
        
        int[] rows = tabledetail.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
                return;
        }
        for (int i : rows) {
            if (tabledetail.getValueAt(i, 11).toString().equals(getGlobalProgTag("closed")) || tabledetail.getValueAt(i, 11).toString().equals(getGlobalProgTag("partial"))) {
                bsmf.MainFrame.show(getMessageTag(1088));
                return;
            } else if (! tabledetail.getValueAt(i, 1).toString().equals(tbitem.getText())) {
                bsmf.MainFrame.show(getMessageTag(1096));
                return;
            }else {
                boolean canproceed = validateDetail();
                if (canproceed) {
                   
                    //   line, item, order, orderline, po, qty, price, desc, wh, loc, disc, listprice, tax, serial, cont, bom, contqty, uom
        
                tabledetail.setValueAt(tbqty.getText(), i, 5);
                tabledetail.setValueAt(dduom.getSelectedItem().toString(), i, 17);
                tabledetail.setValueAt(tbserial.getText(), i, 13);
                tabledetail.setValueAt(cont, i, 14);
                tabledetail.setValueAt(tbprice.getText(), i, 6);
                tabledetail.setValueAt(tbdesc.getText(), i, 7);
                tabledetail.setValueAt(tbcustitem.getText(), i, 18);
                tabledetail.setValueAt(ddwh.getSelectedItem().toString(), i, 8);
                tabledetail.setValueAt(ddloc.getSelectedItem().toString(), i, 9);
                tabledetail.setValueAt(bom, i, 15);
                
                refreshDisplayTotals();         
                tbprice.setText("");
                tbqty.setText("");
                tbordernbr.requestFocus();
                
                }
            }
        }
        
    }//GEN-LAST:event_btupdateitemActionPerformed

    private void cbexplodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbexplodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbexplodeActionPerformed

    private void btaddattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddattachmentActionPerformed
        if (! validateInput(BlueSeerUtils.dbaction.add)) {
            return;
        }
        OVData.addFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), this );
        getAttachments(tbkey.getText());
    }//GEN-LAST:event_btaddattachmentActionPerformed

    private void btdeleteattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteattachmentActionPerformed
        if (! validateInput(BlueSeerUtils.dbaction.delete)) {
            return;
        }
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
            int[] rows = tableattachment.getSelectedRows();
            String filename = null;
            for (int i : rows) {
                filename = tableattachment.getValueAt(i, 1).toString();
            }
            OVData.deleteFileAttachment(tbkey.getText(),this.getClass().getSimpleName(),filename);
            getAttachments(tbkey.getText());
        }
    }//GEN-LAST:event_btdeleteattachmentActionPerformed

    private void tableattachmentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableattachmentMouseClicked
        int row = tableattachment.rowAtPoint(evt.getPoint());
        int col = tableattachment.columnAtPoint(evt.getPoint());
        if ( col == 0) {
            OVData.openFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), tableattachment.getValueAt(row, 1).toString());
        }
    }//GEN-LAST:event_tableattachmentMouseClicked

    private void rbnonorderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbnonorderStateChanged
        /*
        if (rborder.isSelected()) {
            disableshipto();
        }
        if (rbnonorder.isSelected()) {
            enableshipto();
        }
        */
    }//GEN-LAST:event_rbnonorderStateChanged

    private void btLookUpBillToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpBillToActionPerformed
        lookUpFrameBillTo();
    }//GEN-LAST:event_btLookUpBillToActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ChoicePanel;
    private javax.swing.JPanel HeaderPanel;
    private javax.swing.JButton btLookUpBillTo;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattachment;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdeleteattachment;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btlookupOrderLine;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btorder;
    private javax.swing.JButton btprintinvoice;
    private javax.swing.JButton btprintshipper;
    private javax.swing.JButton btshipto;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdateitem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbcomplete;
    private javax.swing.JCheckBox cbexplode;
    private com.toedter.calendar.JDateChooser dcshipdate;
    private javax.swing.JComboBox<String> ddbom;
    private javax.swing.JComboBox<String> ddcont;
    private javax.swing.JComboBox<String> ddcust;
    private javax.swing.JComboBox<String> ddloc;
    private javax.swing.JComboBox<String> ddorder;
    private javax.swing.JComboBox<String> ddshipfrom;
    private javax.swing.JComboBox<String> ddshipto;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelmessage;
    private javax.swing.JLabel lbbilltoname;
    private javax.swing.JLabel lbladdr;
    private javax.swing.JLabel lbllocqty;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblwhqty;
    private javax.swing.JLabel lbqtyshipped;
    private javax.swing.JPanel panelAttachment;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelMain;
    private javax.swing.JRadioButton rbnonorder;
    private javax.swing.JRadioButton rborder;
    private javax.swing.JTable sactable;
    private javax.swing.JTable tableattachment;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTextField tbboxes;
    private javax.swing.JTextField tbcontqty;
    private javax.swing.JTextField tbcustitem;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbitem;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tborderline;
    private javax.swing.JTextField tbordernbr;
    private javax.swing.JTextField tbpallets;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbremarks;
    private javax.swing.JTextField tbserial;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField tbtrailer;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
