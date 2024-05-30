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
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData.getDiscCodeByCust;
import static com.blueseer.ctr.cusData.getShipAddressInfo;
import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.getItemQOHTotal;
import static com.blueseer.ord.ordData.addOrderTransaction;
import static com.blueseer.ord.ordData.getOrderItemAllocatedQty;
import static com.blueseer.ord.ordData.getOrderLines;
import static com.blueseer.ord.ordData.getOrderMstrSet;
import com.blueseer.ord.ordData.salesOrder;
import com.blueseer.ord.ordData.sod_det;
import com.blueseer.ord.ordData.so_mstr;
import com.blueseer.ord.ordData.so_tax;
import com.blueseer.ord.ordData.sod_tax;
import com.blueseer.ord.ordData.sos_det;
import static com.blueseer.ord.ordData.updateOrderTransaction;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.shp.shpData.ship_mstr;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleWithSymbol;
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
import static com.blueseer.utl.BlueSeerUtils.priceformat;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import static com.blueseer.utl.BlueSeerUtils.xZero;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import static com.blueseer.utl.OVData.canUpdate;
import static com.blueseer.utl.OVData.isVoucherShippingSO;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
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
public class OrderMaint extends javax.swing.JPanel implements IBlueSeerT {

    // global variable declarations
                boolean isLoad = false;
                String terms = "";
                String aracct = "";
                String arcc = "";
                String status = "";
                String curr = "";
                String basecurr = "";
                boolean custitemonly = true;
                boolean autoallocate = false;
                String allocationStatus = "";
                public static so_mstr so = null;
                public static ArrayList<sod_det> sodlist = null;
                public static ArrayList<sos_det> soslist = null;
                public static ArrayList<sod_tax> sodtaxlist = null;
                public static ArrayList<so_tax> sotaxlist = null;
                
                Map<Integer, ArrayList<String[]>> linetax = new HashMap<Integer, ArrayList<String[]>>();
                ArrayList<String[]> headertax = new ArrayList<String[]>();
     
               
    
    // global datatablemodel declarations
    OrderMaint.MyTableModel myorddetmodel = new OrderMaint.MyTableModel(new Object[][]{},
            new String[]{
               getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("custitem"), 
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("uom"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("netprice"), 
                getGlobalColumnTag("shipqty"), 
                getGlobalColumnTag("status"), 
                getGlobalColumnTag("warehouse"),
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("tax"),
                getGlobalColumnTag("bom"),
                getGlobalColumnTag("shipcode")
            }
    );
    
    javax.swing.table.DefaultTableModel modelsched = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("date"), 
                getGlobalColumnTag("reference"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("type")
            });
    javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("value"), 
                getGlobalColumnTag("amount")
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
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 5 || tme.getColumn() == 7 )) {
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
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}; 
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
    
     
    public OrderMaint() {
        initComponents();
        setLanguageTags(this);
    }
   
     // interface functions implemented  
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
             updateForm();
             tbkey.requestFocus();
           } else if (this.type.equals("get") && message[0].equals("0")) {
             updateForm();
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
        
        isLoad = true;
        
        ArrayList<String[]> initDataSets = ordData.getSalesOrderInit();
        
       jTabbedPane1.removeAll();
       jTabbedPane1.add(getClassLabelTag("main", this.getClass().getSimpleName()), jPanelMain);
       jTabbedPane1.add(getClassLabelTag("lines", this.getClass().getSimpleName()), jPanelLines);
       jTabbedPane1.add(getClassLabelTag("schedule", this.getClass().getSimpleName()), jPanelSched);
       jTabbedPane1.add(getClassLabelTag("attachments", this.getClass().getSimpleName()), panelAttachment);
        
        
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(2, false);
        
        
        tbkey.setText("");
        tbkey.setEditable(true);
        tbkey.setForeground(Color.black);
        
       
        attachmentmodel.setNumRows(0);
        tableattachment.setModel(attachmentmodel);
        tableattachment.getTableHeader().setReorderingAllowed(false);
        tableattachment.getColumnModel().getColumn(0).setMaxWidth(100);
        
        cbisallocated.setText("Allocation?");
        
        
        ArrayList<String> mylist = new ArrayList<String>();
         jPanelSched.setVisible(false);
        java.util.Date now = new java.util.Date();
       
        lblstatus.setText("");
        lblstatus.setForeground(Color.black);
       
        cbissourced.setSelected(false);
        cbisallocated.setSelected(false);
        cbconfirm.setSelected(false);
        cbplan.setSelected(true);
        cbedi.setSelected(false);
        
        listprice.setText("0");
        netprice.setText("0");
        qtyshipped.setText("0");
        discount.setText("0");
        ponbr.setText("");
        lblcustname.setText("");
        lblshiptoaddr.setText("");
        lblcurr.setText("");
        
        tbsacdesc.setText("");
        tbsacamt.setText("");
        duedate.setDate(now);
        
        
        orddate.setDate(now);
        
        
        myorddetmodel.setRowCount(0);
        myorddetmodel.addTableModelListener(ml);
        orddet.setModel(myorddetmodel);
        orddet.getTableHeader().setReorderingAllowed(false);
        
        //hide columns
        orddet.getColumnModel().getColumn(2).setMaxWidth(0);
        orddet.getColumnModel().getColumn(2).setMinWidth(0);
        orddet.getColumnModel().getColumn(3).setMaxWidth(0);
        orddet.getColumnModel().getColumn(3).setMinWidth(0);
        orddet.getColumnModel().getColumn(4).setMaxWidth(0);
        orddet.getColumnModel().getColumn(4).setMinWidth(0);
        
        
        sacmodel.setRowCount(0);
        sactable.setModel(sacmodel);
        sactable.getTableHeader().setReorderingAllowed(false);
        modelsched.setRowCount(0);
        tablesched.setModel(modelsched);
        
        tbhdrwh.setText("");
        lblIsSourced.setIcon(null);
        remarks.setText("");
        tbtotqty.setText("");
        tbtotdollars.setText("");
        lbltotdollars.setText("0.00");
        lbltotdollars.setForeground(Color.blue);
        tbtottax.setText("");
        totlines.setText("");
        custnumber.setText("");
        
        ddpart.setForeground(Color.black);
        custnumber.setForeground(Color.black);
        custnumber.setEditable(false);
        tbdesc.setForeground(Color.black);
        tbdesc.setEditable(false);
        
        String defaultsite = null;
        
        ddsite.removeAllItems();
        ddwh.removeAllItems();
        ddloc.removeAllItems();
        ddcurr.removeAllItems();
        dduom.removeAllItems();
        ddtax.removeAllItems();
        ddcust.removeAllItems();
        ddship.removeAllItems();
        dditemship.removeAllItems();
        ddshipvia.removeAllItems();
        ddstatus.removeAllItems();
        ddstate.removeAllItems();
        ddcountry.removeAllItems();
        ddpart.removeAllItems();
        
        
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              basecurr = s[1];  
            }
            if (s[0].equals("allocate")) {
              autoallocate = bsmf.MainFrame.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("custitemonly")) {
              custitemonly = bsmf.MainFrame.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("warehouses")) {
              ddwh.addItem(s[1]); 
            }
            if (s[0].equals("locations")) {
              ddloc.addItem(s[1]); 
            }
            if (s[0].equals("currencies")) {
              ddcurr.addItem(s[1]); 
            }
            if (s[0].equals("uoms")) {
              dduom.addItem(s[1]); 
            }
            if (s[0].equals("taxcodes")) {
              ddtax.addItem(s[1]); 
            }
            if (s[0].equals("customers")) {
              ddcust.addItem(s[1]); 
            }
            if (s[0].equals("carriers")) {
              ddshipvia.addItem(s[1]); 
            }
            if (s[0].equals("statuses")) {
              ddstatus.addItem(s[1]); 
            }
            if (s[0].equals("states")) {
              ddstate.addItem(s[1]); 
            }
            if (s[0].equals("countries")) {
              ddcountry.addItem(s[1]); 
            }
            if (s[0].equals("items")) {
              ddpart.addItem(s[1]); 
            }
            
        }
        
        cbisallocated.setSelected(autoallocate);
        ddsite.setSelectedItem(defaultsite);
        ddwh.insertItemAt("", 0);
        ddwh.setSelectedIndex(0);
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
        ddcurr.insertItemAt("", 0);
        ddcurr.setSelectedIndex(0);
        dduom.insertItemAt("", 0);
        dduom.setSelectedIndex(0);
        ddtax.insertItemAt("", 0);
        ddtax.setSelectedIndex(0);
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        ddstate.insertItemAt("", 0);
        ddstate.setSelectedIndex(0);
        ddcountry.insertItemAt("", 0);
        ddcountry.setSelectedIndex(0);
      
        ddstatus.setSelectedItem(getGlobalProgTag("open"));
        
        
        ddsactype.removeAllItems();
        ddsactype.addItem("discount");
        ddsactype.addItem("charge");
        ddsactype.addItem("passive");
        if (isVoucherShippingSO()) {
            ddsactype.addItem("shipping ADD");
            ddsactype.addItem("shipping PPD");
            ddsactype.addItem("shipping BIL");
        }
        ddsactype.setSelectedIndex(0);
        
        
        
        lbqtyavailable.setBackground(Color.gray);
        lbqtyavailable.setText("");
        
        isLoad = false;
        
        
    }
        
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btinvoice.setEnabled(false);
        btprintinvoice.setEnabled(false);
        btprintorder.setEnabled(false);
        btprintps.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        cbedi.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        disableShipAddress();
        
        if (! x.isEmpty()) {
          tbkey.setText(bsNumber(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        
        if (x[0].equals("0")) {
           
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   ddcust.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   
                    // custom set
                    
                    refreshDisplayTotals();
                    
                    if (ddstatus.getSelectedItem().toString().compareTo(getGlobalProgTag("closed")) == 0) {
                             lblstatus.setText(getMessageTag(1097));
                             lblstatus.setForeground(Color.blue);
                             btnew.setEnabled(true);
                             btlookup.setEnabled(true);
                             btclear.setEnabled(true);
                             btprintinvoice.setEnabled(true);
                             btprintps.setEnabled(true);
                             btprintorder.setEnabled(true);
                             btadd.setEnabled(false);
                             btupdate.setEnabled(false);
                             btdelete.setEnabled(false);
                             btinvoice.setEnabled(false);
                    } else {
                             
                             lblstatus.setText(getMessageTag(1098));
                             lblstatus.setForeground(Color.red);
                              btadd.setEnabled(false);
                              btprintinvoice.setEnabled(false);
                              btprintps.setEnabled(false);
                    }
                    
                    if (ddstatus.getSelectedItem().toString().equals(getGlobalProgTag("hold"))) {
                      ddstatus.setBackground(Color.red); 
                    }
           
                    
                    if (cbblanket.isSelected())
                    jPanelSched.setVisible(true);
                    else
                    jPanelSched.setVisible(false);
                   
                   
        } else {
                   tbkey.setForeground(Color.red); 
        }
        
        cbedi.setEnabled(false);
       
    }
    
    public boolean validateInput(dbaction x) {
        
        if (! canUpdate(this.getClass().getName())) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"so_mstr"});
        int fc;

        fc = checkLength(f,"so_nbr");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }  
        
        fc = checkLength(f,"so_po");
        if (ponbr.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            ponbr.requestFocus();
            return false;
        } 
        
        fc = checkLength(f,"so_rmks");
        if (remarks.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            remarks.requestFocus();
            return false;
        }
        
        if (orddet.getRowCount() == 0) {
            bsmf.MainFrame.show(getMessageTag(1089));
            ddship.requestFocus();
            return false;
        }


        if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            ddsite.requestFocus();
            return false;
        }

        if ( ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty() ) {
            bsmf.MainFrame.show(getMessageTag(1024));
            ddcust.requestFocus();
            return false;
        }
        if ( ddship.getSelectedItem() == null || ddship.getSelectedItem().toString().isEmpty() || ddship.getSelectedItem().toString().equals("<new>")) {
            bsmf.MainFrame.show(getMessageTag(1024));
            ddship.requestFocus();
            return false;
        }


        if (ddcurr.getSelectedItem() == null || ddcurr.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            return false;
        }



        terms = cusData.getCustTerms(ddcust.getSelectedItem().toString());
        arcc = cusData.getCustSalesCC(ddcust.getSelectedItem().toString());
        aracct = cusData.getCustSalesAcct(ddcust.getSelectedItem().toString());
        curr = ddcurr.getSelectedItem().toString();
        if (terms == null   || aracct == null   || arcc == null || curr == null ||
                terms.isEmpty() || aracct.isEmpty() || arcc.isEmpty() || curr.isEmpty()
                 ) {
                bsmf.MainFrame.show(getMessageTag(1090));
                return false;
            }   
                
                
                
                
             // lets check for foreign currency with no exchange rate
        if (! curr.toUpperCase().equals(basecurr.toUpperCase())) {
            if (OVData.getExchangeRate(basecurr, curr).isEmpty()) {
                bsmf.MainFrame.show(getMessageTag(1091, curr + "/" + basecurr));
                return false;
            }
        }
              
        return true;
    }
    
    public void initvars(String[] arg) {
     
      setPanelComponentState(jPanelMain, false); 
       setPanelComponentState(jPanelLines, false); 
       setPanelComponentState(jPanelSched, false); 
       setPanelComponentState(this, false); 
       
       
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get, arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }

    }
    
    public String[] addRecord(String[] x) {
     String[] m = new String[2];
     m = addOrderTransaction(createDetRecord(), createRecord(), createTaxRecord(), createTaxDetRecord(), createSOSRecord());
      // if autoinvoice
        if (OVData.isAutoInvoice()) {
        boolean sure = bsmf.MainFrame.warn("This is an auto-invoice order...Are you sure you want to auto-invoice?");     
            if (sure) {     
               m = autoInvoiceOrder();
            }
        } // if autoinvoice
     return m;
     }
     
    public String[] updateRecord(String[] x) {
        String[] m = new String[2];
        // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        
        lines = getOrderLines(tbkey.getText());
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
        m = updateOrderTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord(), createTaxRecord(), createTaxDetRecord(), createSOSRecord());
     
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
                        st.executeUpdate("delete from sod_det where sod_nbr = " + "'" + tbkey.getText() + "'" + ";");  
                        st.executeUpdate("delete from so_tax where sot_nbr = " + "'" + tbkey.getText() + "'" + ";");
                        st.executeUpdate("delete from sod_tax where sodt_nbr = " + "'" + tbkey.getText() + "'" + ";");
                        st.executeUpdate("delete from sos_det where sos_nbr = " + "'" + tbkey.getText() + "'" + ";");
                int i = st.executeUpdate("delete from so_mstr where so_nbr = " + "'" + tbkey.getText() + "'" + ";");
                    if (i > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
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
      
    public String[] getRecord(String[] key) {
      salesOrder z = getOrderMstrSet(key);
      so = z.so();
      sodlist = z.sod();
      soslist = z.sos();
      sodtaxlist = z.sodtax();
      getAttachments(key[0]);
      return z.m();
    }
    
    public so_mstr createRecord() { 
        // lets collect single or multiple Warehouse status
        int d = 0;
        String uniqwh = "";
        for (int j = 0; j < orddet.getRowCount(); j++) {
         if (d > 0) {
           if ( uniqwh.compareTo(orddet.getValueAt(j, 12).toString()) != 0) {
           uniqwh = "multi-WH";
           break;
           }
         }
         d++;
         uniqwh = orddet.getValueAt(j, 12).toString();
        }
        
        String ordertype = "";
        if (cbblanket.isSelected()) {
           ordertype = "BLANKET";
        } else {
           ordertype = "DISCRETE";
        }
                
        so_mstr x = new so_mstr(null, 
                 bsNumberToUS(tbkey.getText()),
                 ddcust.getSelectedItem().toString(),
                 ddship.getSelectedItem().toString(),
                 ddsite.getSelectedItem().toString(),
                 ddcurr.getSelectedItem().toString(),   
                 ddshipvia.getSelectedItem().toString(),
                 uniqwh,
                 ponbr.getText(),
                 setDateDB(duedate.getDate()),
                 setDateDB(orddate.getDate()),
                 setDateDB(new Date()),
                 bsmf.MainFrame.userid,
                 ddstatus.getSelectedItem().toString(),
                 allocationStatus,   // order level allocation status (global variable) set by createDetRecord 
                 terms,
                 aracct,
                 arcc,
                 remarks.getText(),
                 ordertype,
                 ddtax.getSelectedItem().toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbconfirm.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbissourced.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbplan.isSelected())),
                "" // entrytype...blank for manual; 'edi' for EDI entry
                );
        return x;
    }
   
    public ArrayList<sod_det> createDetRecord() {
        ArrayList<sod_det> list = new ArrayList<sod_det>();
       
            double invqty = 0;
            double allqty = 0;
            double qohunall = 0;
            double allocationvalue = 0;
            boolean completeAllocation = true;
            for (int j = 0; j < orddet.getRowCount(); j++) {
                // get total inventory for line item
                // get allocated on current 'open' orders
                 // now get QOH
                invqty = getItemQOHTotal(orddet.getValueAt(j, 1).toString(), ddsite.getSelectedItem().toString());
                allqty = getOrderItemAllocatedQty(orddet.getValueAt(j, 1).toString(), ddsite.getSelectedItem().toString());        


                 qohunall = invqty - allqty; 

                 if (bsParseDouble(orddet.getValueAt(j,5).toString()) <= qohunall) {
                     allocationvalue = bsParseDouble(orddet.getValueAt(j,5).toString());
                 } else {
                     allocationvalue = qohunall;
                     completeAllocation = false;
                 }
                            
                sod_det x = new sod_det(null, 
                bsNumberToUS(tbkey.getText()),
                bsParseInt(orddet.getValueAt(j, 0).toString()),
                orddet.getValueAt(j, 1).toString(),
                orddet.getValueAt(j, 2).toString(),
                orddet.getValueAt(j, 4).toString(),
                bsParseDouble(xZero(orddet.getValueAt(j, 5).toString())), // qty
                orddet.getValueAt(j, 6).toString(),
                allocationvalue,
                bsParseDouble(xZero(orddet.getValueAt(j, 7).toString())), // list
                bsParseDouble(xZero(orddet.getValueAt(j, 8).toString())), // disc
                bsParseDouble(xZero(orddet.getValueAt(j, 9).toString())), // net
                setDateDB(orddate.getDate()),
                setDateDB(duedate.getDate()),   
                bsParseDouble(xZero(orddet.getValueAt(j, 10).toString())),
                orddet.getValueAt(j, 11).toString(),
                orddet.getValueAt(j, 12).toString(),
                orddet.getValueAt(j, 13).toString(),
                orddet.getValueAt(j, 14).toString(),  
                bsParseDouble(xZero(orddet.getValueAt(j, 15).toString())), 
                ddsite.getSelectedItem().toString(),  
                orddet.getValueAt(j, 16).toString(),
                orddet.getValueAt(j, 17).toString()
                );  
                list.add(x);
            }    
            // set global variable status of total order allocation
            if (cbisallocated.isSelected()) {
              allocationStatus = "c";
              if (! completeAllocation) {
                  allocationStatus = "p";
              }
            }
            
        return list;
    }
    
    public ArrayList<sos_det> createSOSRecord() {
         ArrayList<sos_det> list = new ArrayList<sos_det>();
         for (int j = 0; j < sactable.getRowCount(); j++) {
             sos_det x = new sos_det(null, bsNumberToUS(tbkey.getText()),
                sactable.getValueAt(j, 1).toString(),
                sactable.getValueAt(j, 0).toString(),
                sactable.getValueAt(j, 2).toString(),
                bsParseDouble(xZero(sactable.getValueAt(j, 3).toString())));     
                list.add(x);
         }
       
        return list;
    }
    
    public ArrayList<so_tax> createTaxRecord() {
         ArrayList<so_tax> list = new ArrayList<so_tax>();
         if (! headertax.isEmpty()) {
          for (String[] s : headertax) {
              so_tax x = new so_tax(null, bsNumberToUS(tbkey.getText()),
                s[0].toString(),
                bsParseDouble(xZero(s[1])),
                xZero(s[2]));   
                list.add(x);
          }
         }
        return list;
    }
    
    public ArrayList<sod_tax> createTaxDetRecord() {
         ArrayList<sod_tax> list = new ArrayList<sod_tax>();
         for (int j = 0; j < orddet.getRowCount(); j++) {
             if (linetax.containsKey(orddet.getValueAt(j,0))) {
                  for (String[] s : (ArrayList<String[]>)linetax.get(orddet.getValueAt(j,0))) {
                      sod_tax x = new sod_tax(null, bsNumberToUS(tbkey.getText()),
                        xZero(orddet.getValueAt(j, 0).toString()),
                        s[0],
                        bsParseDouble(xZero(s[1])),
                        xZero(s[2]));     
                        list.add(x);
                  }
            }
        }
       
        return list;
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getOrderBrowseUtil(luinput.getText(),0, "so_nbr");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getOrderBrowseUtil(luinput.getText(),0, "so_po");   
        } else {
         luModel = DTData.getOrderBrowseUtil(luinput.getText(),0, "so_cust");   
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
                getClassLabelTag("lblponbr", this.getClass().getSimpleName()),
                getClassLabelTag("lblbillto", this.getClass().getSimpleName())); 
        
    }
 
    public void lookUpFrameBillTo() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_code");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_name");   
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
      
        
        callDialog(getClassLabelTag("lblcust", this.getClass().getSimpleName()), 
                getClassLabelTag("lblname", this.getClass().getSimpleName()),
                getClassLabelTag("lblzip", this.getClass().getSimpleName())); 
        
        
    }
 
    public void lookUpFrameItemDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        
         luModel = DTData.getItemBrowseUtil(luinput.getText(),0, "it_desc");
       
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
                ddpart.setSelectedItem(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
         
        callDialog(getClassLabelTag("lbldesc", this.getClass().getSimpleName())); 
        
    }
    
    public void lookUpFrameCustItem() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        luModel = DTData.getCustXrefBrowseUtil(luinput.getText(), 0, "cup_citem", ddcust.getSelectedItem().toString());
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
                ddpart.setSelectedItem(target.getValueAt(row,3).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblcustitem", this.getClass().getSimpleName())); 
        
        
    }
    
    public void updateForm() throws ParseException {
        
        boolean canInvoice = true;
        
        tbkey.setText(bsNumber(so.so_nbr()));
        tbkey.setEditable(false);
        ddcust.setSelectedItem(so.so_cust());
        ddcust.setEnabled(false);
        cbissourced.setSelected(BlueSeerUtils.ConvertStringToBool(so.so_issourced()));
        cbconfirm.setSelected(BlueSeerUtils.ConvertStringToBool(so.so_confirm()));
        cbedi.setSelected((so.so_entrytype().equals("edi") ? true : false));
        cbplan.setSelected(BlueSeerUtils.ConvertStringToBool(so.so_plan()));
        ddstatus.setSelectedItem(so.so_status());
        ddcurr.setSelectedItem(so.so_curr());
        ddshipvia.setSelectedItem(so.so_shipvia());
        ddtax.setSelectedItem(so.so_taxcode());
        ddsite.setSelectedItem(so.so_site());
        if (ddship.getItemCount() > 0)
        ddship.setSelectedItem(so.so_ship());
        ddship.setEditable(true);
        ponbr.setText(so.so_po());
        remarks.setText(so.so_rmks());
        tbhdrwh.setText(so.so_wh());
        duedate.setDate(parseDate(so.so_due_date()));
        orddate.setDate(parseDate(so.so_ord_date()));

        if (so.so_isallocated().equals("c")) {
            cbisallocated.setSelected(true);
            cbisallocated.setText(tags.getString(this.getClass().getSimpleName() +".label.cballocation"));
        } 
        else if (so.so_isallocated().equals("p")) {
            cbisallocated.setSelected(true);
            cbisallocated.setText(tags.getString(this.getClass().getSimpleName() +".label.cballocationpartial"));
        } else {
            cbisallocated.setSelected(false);
            cbisallocated.setText(tags.getString(this.getClass().getSimpleName() +".label.cballocation"));
        }

        if (so.so_type().compareTo("BLANKET") == 0) {
        cbblanket.setSelected(true);
        cbblanket.setEnabled(false);
        } else {
        cbblanket.setSelected(false);
        cbblanket.setEnabled(false);
        } 
        
        // now detail
        myorddetmodel.setRowCount(0);
        for (sod_det sod : sodlist) {
                    myorddetmodel.addRow(new Object[]{
                      sod.sod_line(), 
                      sod.sod_item(),
                      sod.sod_custitem(), 
                      sod.sod_nbr(), 
                      sod.sod_po(), 
                      bsNumber(sod.sod_ord_qty()), 
                      sod.sod_uom(), 
                      bsFormatDouble(sod.sod_listprice()),
                      bsFormatDouble(sod.sod_disc()), 
                      bsFormatDouble(sod.sod_netprice()), 
                      bsNumber(sod.sod_shipped_qty()), 
                      sod.sod_status(),
                      sod.sod_wh(), 
                      sod.sod_loc(), 
                      sod.sod_desc(), 
                      bsNumber(sod.sod_taxamt()),
                      sod.sod_bom(),
                      sod.sod_ship()
                  });
                    if (! sod.sod_status().equals(getGlobalProgTag("open"))) {
                        canInvoice = false;
                    }
                }
        
        // summary charges and discounts
        if (soslist != null) {
        for (sos_det sos : soslist) {
            if (! sos.sos_type().equals("tax")) {  // don't show header tax again...
            sacmodel.addRow(new Object[]{
                      sos.sos_type(), 
                      sos.sos_desc(),
                      sos.sos_amttype(),
                      sos.sos_amt()});
            }
        }
        }
        
        // line tax
        linetax.clear();
        if (sodtaxlist != null) {
        for (sod_tax sodt : sodtaxlist) {
           ArrayList<String[]> list = OVData.getTaxPercentElementsApplicableByItem(ordData.getOrderItem(sodt.sodt_nbr(), sodt.sodt_line()));
           if (list != null) {
               if (! linetax.containsKey(Integer.valueOf(sodt.sodt_line()))) {
                   linetax.put(Integer.valueOf(sodt.sodt_line()), list); 
               }
           }
        }
        }
        
        // header tax
        /* done by ddtax change event when ddtax is assigned above
        headertax = OVData.getTaxPercentElementsApplicableByTaxCode(ddtax.getSelectedItem().toString());
        */
        
        
        setAction(so.m()); 
        btinvoice.setEnabled(canInvoice);
              
        so = null;
        sodlist = null;
        soslist = null;
        sodtaxlist = null;
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
    
    // custom funcs 
    public String[] autoInvoiceOrder() {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        int shipperid = OVData.getNextNbr("shipper");   
         
        ship_mstr sh = shpData.createShipMstrJRT(String.valueOf(shipperid), ddsite.getSelectedItem().toString(),
                             String.valueOf(shipperid), 
                              ddcust.getSelectedItem().toString(),
                              ddship.getSelectedItem().toString(),
                              bsNumberToUS(tbkey.getText()),
                              ponbr.getText().replace("'", ""),  // po
                              ponbr.getText().replace("'", ""),  // ref
                              setDateDB(duedate.getDate()),
                              setDateDB(orddate.getDate()),
                              remarks.getText().replace("'", ""),
                              ddshipvia.getSelectedItem().toString(),
                              "S", 
                              ddtax.getSelectedItem().toString(),
                              ddsite.getSelectedItem().toString()); 
        ArrayList<String[]> detail = tableToArrayList();
        ArrayList<shpData.ship_det> shd = shpData.createShipDetJRT(detail, String.valueOf(shipperid), setDateDB(orddate.getDate()), ddsite.getSelectedItem().toString());
        shpData.addShipperTransaction(shd, sh);
        shpData.updateShipperSAC(String.valueOf(shipperid));
        // now confirm shipment
        String[] message = confirmShipperTransaction("order", String.valueOf(shipperid), now);
        
         // autopost
        if (OVData.isAutoPost()) {
            fglData.PostGL();
        } 
        
        return message;
    }
    
    public void custChangeEvent(String mykey) {
        
        if (! isLoad) {
        
        clearShipAddress();
        ddship.removeAllItems();
        dditemship.removeAllItems();
            
            
           if (ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty() ) {
               ddcust.setBackground(Color.red);
           } else {
               ddcust.setBackground(null);
           }
            
           String disckey = getDiscCodeByCust(ddcust.getSelectedItem().toString());
            
            ArrayList mycusts = cusData.getcustshipmstrlist(ddcust.getSelectedItem().toString());
            for (int i = 0; i < mycusts.size(); i++) {
                ddship.addItem(mycusts.get(i));
                dditemship.addItem(mycusts.get(i));
            }
            ddship.insertItemAt("",0);
            dditemship.insertItemAt("",0);
            ddship.insertItemAt("<new>",1);
            
            if (ddship.getItemCount() == 2) {
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
                
                res = st.executeQuery("select cm_name, cm_carrier, cm_tax_code, cm_curr, cm_remarks from cm_mstr where cm_code = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    lblcustname.setText(res.getString("cm_name"));
                    ddshipvia.setSelectedItem((res.getString("cm_carrier")));
                    ddtax.setSelectedItem((res.getString("cm_tax_code")));
                    ddcurr.setSelectedItem((res.getString("cm_curr")));
                    remarks.setText(res.getString("cm_remarks"));
                }
                
                if (custitemonly) {
                    ddpart.removeAllItems();
                    res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + mykey + "'" + ";");
                    while (res.next()) {
                        ddpart.addItem(res.getString("cup_item"));
                    }
                }
                
                // discounts...first check if generic disc code applied to cust master...else use cust code
                if (! disckey.isBlank()) {
                  mykey = disckey;  
                }
                res = st.executeQuery("select cpr_disc, cpr_item from cpr_mstr where cpr_cust = " + "'" + mykey + "'" + 
                                      " AND cpr_type = " + "'" + "DISCOUNT" + "'" + ";");
                while (res.next()) {
                  sacmodel.addRow(new Object[]{ "discount", res.getString("cpr_item"), "percent", res.getString("cpr_disc")
                  });
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
        
        } // if ! isLoad
    }
  
    public void reinitCustandShip(String mykey, String shipto) {
            clearShipAddress();
            ddship.removeAllItems();
            dditemship.removeAllItems();
            
            ArrayList mycusts = cusData.getcustshipmstrlist(mykey);
            for (int i = 0; i < mycusts.size(); i++) {
                ddship.addItem(mycusts.get(i));
                dditemship.addItem(mycusts.get(i));
            }
            ddship.insertItemAt("",0);
            dditemship.insertItemAt("",0);
            ddship.insertItemAt("<new>",1);
            
            ddship.setSelectedItem(shipto);
            dditemship.setSelectedItem(shipto);
                    
     
            
     
    }
    
    public void clearShipAddress() {
        tbshiptocode.setText("");
        tbname.setText("");
        tbaddr1.setText("");
        tbaddr2.setText("");
        tbcity.setText("");
        tbzip.setText("");
        tbphone.setText("");
        tbemail.setText("");
        tbcontact.setText("");
        tbmisc1.setText("");
        if (ddstate.getItemCount() > 0) {
        ddstate.setSelectedIndex(0);
        }
        if (ddcountry.getItemCount() > 0) {
        ddcountry.setSelectedIndex(0);
        }
    }
    
    public void enableShipAddress() {
          tbshiptocode.setEnabled(true);
        tbname.setEnabled(true);
        tbaddr1.setEnabled(true);
        tbaddr2.setEnabled(true);
        tbcity.setEnabled(true);
        tbzip.setEnabled(true);
        tbphone.setEnabled(true);
        tbemail.setEnabled(true);
        tbcontact.setEnabled(true);
        tbmisc1.setEnabled(true);
        ddstate.setEnabled(true);
        ddcountry.setEnabled(true);
        btaddshipto.setEnabled(true);
    }
    
    public void disableShipAddress() {
        tbshiptocode.setEnabled(false);
        tbname.setEnabled(false);
        tbaddr1.setEnabled(false);
        tbaddr2.setEnabled(false);
        tbcity.setEnabled(false);
        tbzip.setEnabled(false);
        tbphone.setEnabled(false);
        tbemail.setEnabled(false);
        tbcontact.setEnabled(false);
        tbmisc1.setEnabled(false);
        ddstate.setEnabled(false);
        ddcountry.setEnabled(false);
        btaddshipto.setEnabled(false);
    }
        
    public void getSchedRecords(String order, String po, String part, String line) {
        modelsched.setNumRows(0);
        
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
                res = st.executeQuery("select * from srl_mstr where " +
                        " srl_so = " + "'" + order + "'" + 
                        " AND srl_po = " + "'" + po + "'" + 
                        " AND srl_item = " + "'" + part + "'" + 
                        " AND srl_line = " + "'" + line + "'" + 
                         ";");
                 while (res.next()) {
                  modelsched.addRow(new Object[]{ 
                      res.getString("srl_duedate"), 
                      res.getString("srl_ref"), res.getString("srl_qtyord"), res.getString("srl_type")});
                }
                tablesched.setModel(modelsched);
               
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
     
    public void getItemInfo(String part) {
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
                
               
               // if part is not already in list
                int k = ddpart.getSelectedIndex();
              
                // lets first try as cust part...i.e....lets look up the item based on entering a customer part number.
                if (k < 0) {
                    
                res = st.executeQuery("select cup_item, cup_citem from cup_mstr where cup_cust = " + "'" + ddcust.getSelectedItem().toString() + "'" 
                        + " AND cup_citem = " + "'" + part + "'" 
                        + ";");
                while (res.next()) {
                    i++;
                    ddpart.setSelectedItem(res.getString("cup_item"));
                    custnumber.setText(res.getString("cup_citem"));
                    ddpart.setForeground(Color.blue);
                    custnumber.setForeground(Color.blue);
                    custnumber.setEditable(false);
                }
                
                // if i is still 0...then must be a misc item
                if (i == 0) {
                  //  partnbr.addItem(part);
                  //  partnbr.setSelectedItem(part);
                    custnumber.setText("");
                    ddpart.setForeground(Color.red);
                    custnumber.setForeground(Color.red);
                    custnumber.setEditable(true);
                    tbdesc.setForeground(Color.red);
                    tbdesc.setEditable(true);
                    
                    discount.setText("0");
                    listprice.setText("0");
                    listprice.setBackground(Color.white);
                    
                    netprice.setText("0");
                    qtyshipped.setText("0");
                }
                
                }
                
             if (k >= 0) {  
            String[] det = invData.getItemDetail(ddpart.getSelectedItem().toString());
            discount.setText("0");
            listprice.setText("0");
            netprice.setText("0");
            qtyshipped.setText("0");
            tbdesc.setText(det[1]);
            custnumber.setText(cusData.getCustPartFromPart(ddcust.getSelectedItem().toString(),ddpart.getSelectedItem().toString()));
            dduom.setSelectedItem(det[2]);
            ddpart.setForeground(Color.blue);
            custnumber.setForeground(Color.blue);
            custnumber.setEditable(false);
            tbdesc.setForeground(Color.blue);
            tbdesc.setEditable(false);
            // do BOM alternates
            ddbom.removeAllItems();
            ddbom.insertItemAt("", 0);
            //ddbom.setSelectedIndex(0);
            ArrayList<String[]> boms = invData.getBOMsByItemSite(ddpart.getSelectedItem().toString());
            for (String[] wh : boms) {
                ddbom.addItem(wh[0]);
            }
            ddbom.setSelectedItem(OVData.getDefaultBomID(ddpart.getSelectedItem().toString()));
            
            if (ddpart.getItemCount() > 0) {
                String[] arr = OVData.getTopLocationAndWHByQTY(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString());
                ddwh.setSelectedItem(arr[0]);
                ddloc.setSelectedItem(arr[1]);
                ddpart.setForeground(Color.blue);
                custnumber.setForeground(Color.blue);
                setPrice();
                
            } // if part selected
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
    
    public void setPrice() {
        
        // save current price if this is a line item update
        String cur_listprice = listprice.getText();
        String cur_discount = discount.getText();
                
        
        listprice.setText("0");
        netprice.setText("0");
        discount.setText("0");
        String pricetype = "";
        double price = 0.00;
        String[] TypeAndPrice = new String[]{"","0"};
        if (dduom.getItemCount() > 0 && ddpart.getItemCount() > 0 && ddcust.getItemCount() > 0) {
                TypeAndPrice = invData.getItemPrice("c", ddcust.getSelectedItem().toString(), ddpart.getSelectedItem().toString(), 
                        dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString(), qtyshipped.getText());
                
                if (TypeAndPrice[0] != null)
                pricetype = TypeAndPrice[0];
                
                if (TypeAndPrice[1] != null) {
                    price = bsParseDouble(TypeAndPrice[1]);
                }
                listprice.setText(bsFormatDouble(price));
                if (pricetype.equals("cust")) {
                    listprice.setBackground(Color.green);
                }
                if (pricetype.equals("item")) {
                    listprice.setBackground(Color.white);
                }
                double disc = invData.getItemDiscFromCust(ddcust.getSelectedItem().toString());
                discount.setText(bsFormatDouble(disc));
                
                // override if line item update and price not found...line update due to qty change
                if (disc == 0) {
                    discount.setText(cur_discount);
                }
                if (price == 0) {
                    listprice.setText(cur_listprice);
                }
                
                
                setNetPrice();
        }
    }
    
    public void setNetPrice() {
        double disc = 0;
        double list = 0;
        double net = 0;
         if (! discount.getText().isEmpty()) {
            disc = bsParseDouble(discount.getText());
        }
        if (! listprice.getText().isEmpty()) {
            list = bsParseDouble(listprice.getText());
        }
        
        if (disc == 0) {
            netprice.setText(listprice.getText());
        } else {
           if (list == 0) {
             listprice.setText("0");
             netprice.setText("0");
           } else {  
           net = list - ((disc / 100) * list);
           netprice.setText(bsFormatDouble(net));
           }
        }

    }
         
    public void sumlinecount() {
         totlines.setText(String.valueOf(orddet.getRowCount()));
         if (orddet.getRowCount() > 0) {
             ddcurr.setEnabled(false);
             ddcust.setEnabled(false);
         } else {
             ddcurr.setEnabled(true);
             ddcust.setEnabled(true);
         }
    }
            
    public void sumqty() {
        double qty = 0.00;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + bsParseDouble(orddet.getValueAt(j, 5).toString()); 
         }
         tbtotqty.setText(bsNumber(qty));
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
    
    public void sumdollars() {
        double dol = 0;
        double summaryTaxPercent = 0;
        double summaryTaxAmount = 0;
        double headertaxamt = 0;
        double matltax = 0;
        double totaltax = 0;
        
        
         for (int j = 0; j < orddet.getRowCount(); j++) {
             dol = dol + ( bsParseDouble(orddet.getValueAt(j, 5).toString()) * bsParseDouble(orddet.getValueAt(j, 9).toString()) );  
             matltax += bsParseDouble(orddet.getValueAt(j, 15).toString()); // now get material tax at the line level
         }
         
         // now lets get summary tax
         // now add trailer/summary charges if any
         for (int j = 0; j < sactable.getRowCount(); j++) {
            if (sactable.getValueAt(j,0).toString().equals("passive")) { // skip passive (info only)
            continue;
            } 
            
            if (! sactable.getValueAt(j,0).toString().equals("tax") &&
                    ! sactable.getValueAt(j,0).toString().equals("shipping PPD") &&
                    ! sactable.getValueAt(j,0).toString().equals("shipping BIL") && ! sactable.getValueAt(j,2).toString().equals("percent") ) {
            
            dol += bsParseDouble(sactable.getValueAt(j,3).toString());  // add charges to total net charge
            }
            if (sactable.getValueAt(j,0).toString().equals("tax") && sactable.getValueAt(j,2).toString().equals("percent")) {
            summaryTaxPercent += bsParseDouble(sactable.getValueAt(j,3).toString());
            }
            if (sactable.getValueAt(j,0).toString().equals("tax") && sactable.getValueAt(j,2).toString().equals("amount")) {
            summaryTaxAmount += bsParseDouble(sactable.getValueAt(j,3).toString());
            }
        }
         
         if (summaryTaxPercent > 0) {
              headertaxamt = (dol * (summaryTaxPercent / 100) );
         }
         headertaxamt += summaryTaxAmount; // header tax amount is percent tax plus non-percent fixed amount
         
         totaltax = headertaxamt + matltax;  // combine header tax and matl tax
         
         
         // add tax to total
         dol += totaltax;
         
        
         tbtottax.setText(currformatDouble(totaltax));
         tbtotdollars.setText(currformatDouble(dol));
         lbltotdollars.setText(currformatDoubleWithSymbol(dol, ddcurr.getSelectedItem().toString()));
         lblcurr.setText(ddcurr.getSelectedItem().toString());
    }
      
    public void refreshDisplayTotals() {
        sumqty();
        sumdollars();
        sumlinecount();
    }
    
    public void retotal() {
        
        double dol = 0;
        double newdisc = 0;
        double newprice = 0;
        double newtax = 0;
        double listprice = 0;
         //"Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc"
        
         for (int j = 0; j < sactable.getRowCount(); j++) {
            if (sactable.getValueAt(j,0).toString().equals("discount") &&
                sactable.getValueAt(j,2).toString().equals("percent")) {
            newdisc += bsParseDouble(sactable.getValueAt(j,3).toString());
            }
         }
         
         // check for customer specific discounts
         newdisc += invData.getItemDiscFromCust(ddcust.getSelectedItem().toString());
        
         for (int j = 0; j < orddet.getRowCount(); j++) {
             listprice = bsParseDouble(orddet.getValueAt(j, 7).toString());
             orddet.setValueAt(currformatDouble(newdisc), j, 8);
             if (newdisc > 0) {
             newprice = listprice - (listprice * (newdisc / 100));
             } else {
             newprice = listprice;    
             }
             orddet.setValueAt(currformatDouble(newprice), j, 9);
         }
               
         
    }
    
    public boolean validateDetail() {
       // if user clicks on 'additem' before focuslost on each field
       // has time to fire, focuslost will have effectively set these fields to empty upon
       // seeing an error before this function is called
       // ...so we check for empty to prevent lines from being added
        
        if (qtyshipped.getText().isEmpty()) {
            return false;
        }
        if (listprice.getText().isEmpty()) {
            return false;
        }
        if (discount.getText().isEmpty()) {
            return false;
        }
        
        if (ddpart.getSelectedItem() == null || ddpart.getSelectedItem().toString().isBlank()) {
            bsmf.MainFrame.show(getMessageTag(1081));
            ddpart.requestFocus();
            return false;
        }
        boolean isvalid = OVData.isValidItem(ddpart.getSelectedItem().toString());
        
        // check unallocated qty
        if (! OVData.isOrderExceedQOHU() && bsParseDouble(qtyshipped.getText()) > invData.getItemQOHUnallocated(ddpart.getSelectedItem().toString(),ddsite.getSelectedItem().toString(),tbkey.getText())) {
             bsmf.MainFrame.show(getMessageTag(1092));
             qtyshipped.requestFocus();
             return false;
        }
        
        if (isvalid && ! OVData.isValidUOMConversion(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString())) {
                bsmf.MainFrame.show(getMessageTag(1093));
                dduom.requestFocus();
                return false;
                
        }
        if (isvalid && ! OVData.isBaseUOMOfItem(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString()) && ! OVData.isValidCustPriceRecordExists(ddcust.getSelectedItem().toString(),ddpart.getSelectedItem().toString(),dduom.getSelectedItem().toString(),ddcurr.getSelectedItem().toString())) {
                bsmf.MainFrame.show(getMessageTag(1094));
                dduom.requestFocus();
                return false;
                
        }
      return true;   
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
                 orddet.getValueAt(j, 7).toString(),
                 orddet.getValueAt(j, 8).toString(),
                 orddet.getValueAt(j, 9).toString(),
                 orddet.getValueAt(j, 10).toString(),
                 orddet.getValueAt(j, 11).toString(),
                 orddet.getValueAt(j, 12).toString(),
                 orddet.getValueAt(j, 13).toString(),
                 orddet.getValueAt(j, 14).toString(),
                 orddet.getValueAt(j, 15).toString(),
                 orddet.getValueAt(j, 16).toString()};
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

        jTextField1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelSched = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablesched = new javax.swing.JTable();
        jPanelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        jLabel82 = new javax.swing.JLabel();
        ddcust = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        ddship = new javax.swing.JComboBox();
        jLabel91 = new javax.swing.JLabel();
        lblcustname = new javax.swing.JLabel();
        lblshiptoaddr = new javax.swing.JLabel();
        lblshiptoname = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        ponbr = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        ddshipvia = new javax.swing.JComboBox();
        remarks = new javax.swing.JTextField();
        ddstatus = new javax.swing.JComboBox();
        jLabel83 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        duedate = new com.toedter.calendar.JDateChooser();
        jLabel86 = new javax.swing.JLabel();
        orddate = new com.toedter.calendar.JDateChooser();
        jLabel87 = new javax.swing.JLabel();
        cbblanket = new javax.swing.JCheckBox();
        jLabel92 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        ddtax = new javax.swing.JComboBox<>();
        jLabel93 = new javax.swing.JLabel();
        tbhdrwh = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        lblIsSourced = new javax.swing.JLabel();
        cbissourced = new javax.swing.JCheckBox();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel98 = new javax.swing.JLabel();
        cbisallocated = new javax.swing.JCheckBox();
        cbconfirm = new javax.swing.JCheckBox();
        cbplan = new javax.swing.JCheckBox();
        cbedi = new javax.swing.JCheckBox();
        btdelete = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        tbemail = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        tbcontact = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        jLabel104 = new javax.swing.JLabel();
        tbname = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        jLabel106 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        ddstate = new javax.swing.JComboBox();
        tbaddr1 = new javax.swing.JTextField();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        tbmisc1 = new javax.swing.JTextField();
        jLabel111 = new javax.swing.JLabel();
        tbaddr2 = new javax.swing.JTextField();
        tbshiptocode = new javax.swing.JTextField();
        btaddshipto = new javax.swing.JButton();
        ddcountry = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        btinvoice = new javax.swing.JButton();
        btprintorder = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btprintinvoice = new javax.swing.JButton();
        btprintps = new javax.swing.JButton();
        btLookUpBillTo = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        lbltotdollars = new javax.swing.JLabel();
        lbldollars = new javax.swing.JLabel();
        jPanelLines = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btadditem = new javax.swing.JButton();
        btdelitem = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel84 = new javax.swing.JLabel();
        lblCustItemAndDesc = new javax.swing.JLabel();
        qtyshipped = new javax.swing.JTextField();
        custnumber = new javax.swing.JTextField();
        ddpart = new javax.swing.JComboBox();
        lblpart1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dduom = new javax.swing.JComboBox<>();
        lbqtyavailable = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        btLookUpCustItem = new javax.swing.JButton();
        btLookUpItemDesc = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        discount = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        netprice = new javax.swing.JTextField();
        listprice = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        tbmisc = new javax.swing.JTextField();
        ddwh = new javax.swing.JComboBox<>();
        ddloc = new javax.swing.JComboBox<>();
        ddbom = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        dditemship = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        btupdateitem = new javax.swing.JButton();
        itemshipaddrlbl = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sactable = new javax.swing.JTable();
        tbsacamt = new javax.swing.JTextField();
        tbsacdesc = new javax.swing.JTextField();
        percentlabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btsacadd = new javax.swing.JButton();
        btsacdelete = new javax.swing.JButton();
        ddsactype = new javax.swing.JComboBox<>();
        ddsacamttype = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbtottax = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        lblcurr = new javax.swing.JLabel();
        panelAttachment = new javax.swing.JPanel();
        labelmessage = new javax.swing.JLabel();
        btaddattachment = new javax.swing.JButton();
        btdeleteattachment = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableattachment = new javax.swing.JTable();

        jTextField1.setText("jTextField1");

        jLabel6.setText("jLabel6");

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        jPanelSched.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule Releases"));
        jPanelSched.setPreferredSize(new java.awt.Dimension(940, 627));

        tablesched.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tablesched);

        javax.swing.GroupLayout jPanelSchedLayout = new javax.swing.GroupLayout(jPanelSched);
        jPanelSched.setLayout(jPanelSchedLayout);
        jPanelSchedLayout.setHorizontalGroup(
            jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSchedLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 918, Short.MAX_VALUE))
        );
        jPanelSchedLayout.setVerticalGroup(
            jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSchedLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
        );

        add(jPanelSched);

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Order Maintenance"));
        jPanelMain.setName("panelmain"); // NOI18N
        jPanelMain.setPreferredSize(new java.awt.Dimension(940, 627));

        jLabel76.setText("Key");
        jLabel76.setName("lblid"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel82.setText("bill-to");
        jLabel82.setName("lblbillto"); // NOI18N

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        ddship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddshipActionPerformed(evt);
            }
        });

        jLabel91.setText("ship-to");
        jLabel91.setName("lblshipto"); // NOI18N

        lblcustname.setName("lblbilltoaddress"); // NOI18N

        lblshiptoaddr.setName("lblshiptoaddress"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel81.setText("Due Date");
        jLabel81.setName("lblduedate"); // NOI18N

        jLabel90.setText("ShipVia");
        jLabel90.setName("lblshipvia"); // NOI18N

        ddstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddstatusActionPerformed(evt);
            }
        });

        jLabel83.setText("PO Number");
        jLabel83.setName("lblponbr"); // NOI18N

        jLabel85.setText("Status");
        jLabel85.setName("lblstatus"); // NOI18N

        duedate.setDateFormatString("yyyy-MM-dd");

        jLabel86.setText("Remarks");
        jLabel86.setName("lblremarks"); // NOI18N

        orddate.setDateFormatString("yyyy-MM-dd");

        jLabel87.setText("Ord Date");
        jLabel87.setName("lblorddate"); // NOI18N

        cbblanket.setText("Blanket?");
        cbblanket.setName("cbblanket"); // NOI18N

        jLabel92.setText("Site:");
        jLabel92.setName("lblsite"); // NOI18N

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        ddtax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtaxActionPerformed(evt);
            }
        });

        jLabel93.setText("Tax Code");
        jLabel93.setName("lbltaxcode"); // NOI18N

        jLabel97.setText("WH");
        jLabel97.setName("lblwh"); // NOI18N

        lblIsSourced.setText("   ");

        cbissourced.setText("is Sourced?");
        cbissourced.setName("cbissourced"); // NOI18N

        jLabel98.setText("Currency");
        jLabel98.setName("lblcurrency"); // NOI18N

        cbisallocated.setText("Allocation?");
        cbisallocated.setName("cballocation"); // NOI18N

        cbconfirm.setText("Confirmed");
        cbconfirm.setName("cbconfirm"); // NOI18N

        cbplan.setText("Plan");
        cbplan.setName("cbplan"); // NOI18N

        cbedi.setText("EDI");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel83)
                    .addComponent(jLabel90)
                    .addComponent(jLabel86)
                    .addComponent(jLabel98))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(cbblanket)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cbedi))
                                    .addComponent(ddshipvia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ponbr, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(48, 48, 48)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel97, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel92, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ddstatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(tbhdrwh, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblIsSourced, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cbisallocated))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel93, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(orddate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(duedate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cbissourced)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbconfirm)))
                        .addGap(18, 18, 18)
                        .addComponent(cbplan)))
                .addContainerGap(148, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbblanket)
                                .addComponent(cbedi))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ponbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel83))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel90)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel92)
                                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel81))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel85)
                                    .addComponent(jLabel87)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel93))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbhdrwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel97)
                                .addComponent(lblIsSourced)))))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel98))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbisallocated)
                            .addComponent(cbissourced)
                            .addComponent(cbconfirm)
                            .addComponent(cbplan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addContainerGap())
        );

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(758, 181));

        jLabel11.setText("Zip");
        jLabel11.setName("lblzip"); // NOI18N

        jLabel99.setText("City");
        jLabel99.setName("lblcity"); // NOI18N

        jLabel100.setText("Misc");
        jLabel100.setName("lblmisc"); // NOI18N

        jLabel101.setText("Name");
        jLabel101.setName("lblname"); // NOI18N

        jLabel102.setText("Addr2");
        jLabel102.setName("lbladdr2"); // NOI18N

        jLabel104.setText("Phone");
        jLabel104.setName("lblphone"); // NOI18N

        jLabel106.setText("Email");
        jLabel106.setName("lblemail"); // NOI18N

        jLabel77.setText("Code");
        jLabel77.setName("lblcode"); // NOI18N

        jLabel109.setText("Contact");
        jLabel109.setName("lblcontact"); // NOI18N

        jLabel110.setText("Addr1");
        jLabel110.setName("lbladdr1"); // NOI18N

        jLabel111.setText("State");
        jLabel111.setName("lblstate"); // NOI18N

        btaddshipto.setText("Add ShipTo");
        btaddshipto.setName("btaddshipto"); // NOI18N
        btaddshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddshiptoActionPerformed(evt);
            }
        });

        jLabel12.setText("Country");
        jLabel12.setName("lblcountry"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel77)
                    .addComponent(jLabel101)
                    .addComponent(jLabel110)
                    .addComponent(jLabel102)
                    .addComponent(jLabel99)
                    .addComponent(jLabel111))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshiptocode, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)))
                .addGap(47, 47, 47)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel104)
                            .addComponent(jLabel106)
                            .addComponent(jLabel109)
                            .addComponent(jLabel100))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbmisc1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(207, 207, 207)
                        .addComponent(btaddshipto)))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel77))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tbshiptocode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel106))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel109))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel104))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbmisc1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel100)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel101))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel110))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel102))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel99))
                            .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel111))
                            .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)
                                .addComponent(btaddshipto)
                                .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btinvoice.setText("Invoice");
        btinvoice.setName("btinvoice"); // NOI18N
        btinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btinvoiceActionPerformed(evt);
            }
        });

        btprintorder.setText("Print Order");
        btprintorder.setName("btprintorder"); // NOI18N
        btprintorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintorderActionPerformed(evt);
            }
        });

        lblstatus.setName("lblmessage"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btprintinvoice.setText("Print Invoice");
        btprintinvoice.setName("btprintinvoice"); // NOI18N
        btprintinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintinvoiceActionPerformed(evt);
            }
        });

        btprintps.setText("Print PackingSlip");
        btprintps.setName("btprintpackingslip"); // NOI18N
        btprintps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintpsActionPerformed(evt);
            }
        });

        btLookUpBillTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpBillTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpBillToActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        lbldollars.setText("Total $");
        lbldollars.setName("lbltotalamount"); // NOI18N

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMainLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel76, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbkey)
                            .addComponent(ddcust, 0, 133, Short.MAX_VALUE)
                            .addComponent(ddship, 0, 133, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(btLookUpBillTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblshiptoaddr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblcustname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(41, 41, 41)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblshiptoname, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanelMainLayout.createSequentialGroup()
                                        .addComponent(lbldollars)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lbltotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addGap(27, 27, 27)
                                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btinvoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btprintinvoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btprintps)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(87, 87, 87))
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMainLayout.createSequentialGroup()
                        .addGap(474, 474, 474)
                        .addComponent(btprintorder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel76))
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnew)
                                    .addComponent(btclear)))
                            .addComponent(btlookup)))
                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblcustname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btLookUpBillTo)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel82))))
                    .addComponent(lbltotdollars, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbldollars, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel91))
                            .addComponent(lblshiptoaddr, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblshiptoname, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btinvoice)
                    .addComponent(btprintorder)
                    .addComponent(btprintinvoice)
                    .addComponent(btprintps))
                .addGap(16, 16, 16))
        );

        add(jPanelMain);

        jPanelLines.setBorder(javax.swing.BorderFactory.createTitledBorder("Lines"));
        jPanelLines.setName("panellines"); // NOI18N
        jPanelLines.setPreferredSize(new java.awt.Dimension(940, 627));

        btadditem.setText("Insert");
        btadditem.setName("btinsert"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdelitem.setText("Remove");
        btdelitem.setName("btremove"); // NOI18N
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        jLabel84.setText("Qty Ship");
        jLabel84.setName("lblqtyship"); // NOI18N

        lblCustItemAndDesc.setText("CustNumber");
        lblCustItemAndDesc.setName("lblcustnumber"); // NOI18N

        qtyshipped.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                qtyshippedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                qtyshippedFocusLost(evt);
            }
        });

        ddpart.setEditable(true);
        ddpart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpartActionPerformed(evt);
            }
        });

        lblpart1.setText("Item Number");
        lblpart1.setName("lblitem"); // NOI18N

        jLabel5.setText("uom");
        jLabel5.setName("lbluom"); // NOI18N

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel9.setText("Description");
        jLabel9.setName("lbldesc"); // NOI18N

        btLookUpCustItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpCustItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpCustItemActionPerformed(evt);
            }
        });

        btLookUpItemDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpItemDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpItemDescActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblpart1)
                    .addComponent(lblCustItemAndDesc)
                    .addComponent(jLabel84)
                    .addComponent(jLabel9)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(custnumber, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(btLookUpCustItem, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addGap(71, 71, 71))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(lbqtyavailable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(custnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustItemAndDesc)
                    .addComponent(btLookUpCustItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btLookUpItemDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblpart1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel84))
                    .addComponent(lbqtyavailable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel89.setText("NetPrice");
        jLabel89.setName("lblnetprice"); // NOI18N

        jLabel80.setText("ListPrice");
        jLabel80.setName("lbllistprice"); // NOI18N

        discount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                discountFocusLost(evt);
            }
        });

        jLabel88.setText("Disc%");
        jLabel88.setName("lbldiscount"); // NOI18N

        netprice.setEditable(false);
        netprice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netpriceActionPerformed(evt);
            }
        });

        listprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                listpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                listpriceFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel80, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel88, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel89, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(discount, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addComponent(netprice))
                    .addComponent(listprice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(listprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel80))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel88)
                            .addComponent(discount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(netprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addContainerGap())
        );

        jLabel94.setText("Misc");
        jLabel94.setName("lblmisc"); // NOI18N

        jLabel95.setText("Plant/WH");
        jLabel95.setName("lblwh"); // NOI18N

        jLabel96.setText("Location");
        jLabel96.setName("lblloc"); // NOI18N

        tbmisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbmiscActionPerformed(evt);
            }
        });

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        ddloc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddlocActionPerformed(evt);
            }
        });

        ddbom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddbomActionPerformed(evt);
            }
        });

        jLabel7.setText("BOM");
        jLabel7.setName("lblbom"); // NOI18N

        dditemship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemshipActionPerformed(evt);
            }
        });

        jLabel10.setText("ShipTo");
        jLabel10.setName("lblshipto"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(118, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel95, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel96, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel94, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(ddbom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddwh, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbmisc)
                        .addComponent(ddloc, 0, 94, Short.MAX_VALUE))
                    .addComponent(dditemship, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel95)
                    .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel96))
                .addGap(8, 8, 8)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel94))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddbom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dditemship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        btupdateitem.setText("Update");
        btupdateitem.setName("btupdate"); // NOI18N
        btupdateitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateitemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(itemshipaddrlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdateitem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelitem)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(itemshipaddrlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btdelitem)
                        .addComponent(btadditem)
                        .addComponent(btupdateitem))))
        );

        orddet.setModel(new javax.swing.table.DefaultTableModel(
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
        orddet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                orddetMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(orddet);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Summary Discounts, Charges, and Taxes"));
        jPanel5.setName("panelsummary"); // NOI18N

        sactable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(sactable);

        percentlabel.setText("Percent/Amount");
        percentlabel.setName("lblpercent"); // NOI18N

        jLabel8.setText("Desc");
        jLabel8.setName("lbldesc"); // NOI18N

        btsacadd.setText("add");
        btsacadd.setName("btadd"); // NOI18N
        btsacadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacaddActionPerformed(evt);
            }
        });

        btsacdelete.setText("delete");
        btsacdelete.setName("btdelete"); // NOI18N
        btsacdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacdeleteActionPerformed(evt);
            }
        });

        ddsacamttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "amount", "percent" }));
        ddsacamttype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsacamttypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(percentlabel)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbsacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tbsacamt, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(btsacadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btsacdelete))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddsacamttype, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsacamttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btsacadd)
                    .addComponent(btsacdelete)
                    .addComponent(tbsacamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percentlabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel3.setText("Total Lines:");
        jLabel3.setName("lbltotallines"); // NOI18N

        totlines.setEditable(false);

        jLabel1.setText("Total Qty:");
        jLabel1.setName("lbltotalquantity"); // NOI18N

        tbtotqty.setEditable(false);

        jLabel4.setText("Total Tax:");
        jLabel4.setName("lbltotaltax"); // NOI18N

        tbtottax.setEditable(false);

        jLabel2.setText("Total $");
        jLabel2.setName("lbltotalamount"); // NOI18N

        tbtotdollars.setEditable(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtottax, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(250, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbtottax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelLinesLayout = new javax.swing.GroupLayout(jPanelLines);
        jPanelLines.setLayout(jPanelLinesLayout);
        jPanelLinesLayout.setHorizontalGroup(
            jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanelLinesLayout.setVerticalGroup(
            jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinesLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );

        add(jPanelLines);

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
        jScrollPane4.setViewportView(tableattachment);

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 446, Short.MAX_VALUE)
                        .addComponent(labelmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAttachmentLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(157, 157, 157))
        );

        add(panelAttachment);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      newAction("order");
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        double np = 0;
        double qty = 0;
        np = bsParseDouble(netprice.getText());
        qty = bsParseDouble(qtyshipped.getText());
        int line = 0;
        line = getmaxline();
        line++;
        
        boolean canproceed = validateDetail();
        String bom = "";
        if (ddbom.getSelectedItem() != null) {
            bom = ddbom.getSelectedItem().toString();
        }
        String uom = "";
        if (dduom.getSelectedItem() != null) {
            uom = dduom.getSelectedItem().toString();
        }
        //    "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice"
        if (canproceed) {
            myorddetmodel.addRow(new Object[]{line, ddpart.getSelectedItem().toString(), custnumber.getText(), tbkey.getText(), ponbr.getText(), 
                qtyshipped.getText(), uom, listprice.getText(), 
                discount.getText(), netprice.getText(), 
                "0", getGlobalProgTag("open"),
                ddwh.getSelectedItem().toString(), ddloc.getSelectedItem().toString(), tbdesc.getText(), 
                String.valueOf(bsFormatDouble(OVData.getTaxAmtApplicableByItem(ddpart.getSelectedItem().toString(), (np * qty) ))),
                bom, dditemship.getSelectedItem().toString()
            });
            
            // lets collect tax elements for each item
            ArrayList<String[]> list = OVData.getTaxPercentElementsApplicableByItem(ddpart.getSelectedItem().toString());
            if (list != null) {
            linetax.put(line, list);
            } 
            
            
         refreshDisplayTotals();
         
          listprice.setText("");
         netprice.setText("");
         discount.setText("");
         qtyshipped.setText("");
         
         ddpart.requestFocus();
        }
        
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btaddActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed

        if (ddcust.getItemCount() > 0) {
           custChangeEvent(ddcust.getSelectedItem().toString());
           
           
        } // if ddcust has a list
        
    }//GEN-LAST:event_ddcustActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("closed")) || orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("partial"))) {
                bsmf.MainFrame.show(getMessageTag(1088));
                return;
            } else {
            bsmf.MainFrame.show(getMessageTag(1031, String.valueOf(i)));
            linetax.remove(orddet.getValueAt(i, 0));
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
        
       
         refreshDisplayTotals();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
          if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void netpriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netpriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_netpriceActionPerformed

    private void ddshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshipActionPerformed
        if (ddship.getItemCount() > 0)  {
            clearShipAddress();
            
              if (ddship.getSelectedItem().toString().equals("<new>")) {
              enableShipAddress();
              } else {
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

                            res = st.executeQuery("select * from cms_det where cms_code = " + "'" + ddcust.getSelectedItem().toString() + "'" +
                                    " AND cms_shipto = " + "'" + ddship.getSelectedItem().toString() + "'" + ";");
                            while (res.next()) {
                                tbname.setText(res.getString("cms_name"));
                                tbaddr1.setText(res.getString("cms_line1"));
                                tbaddr2.setText(res.getString("cms_line2"));
                                tbcity.setText(res.getString("cms_city"));
                                tbzip.setText(res.getString("cms_zip"));
                                tbcontact.setText(res.getString("cms_contact"));
                                tbphone.setText(res.getString("cms_phone"));
                                tbemail.setText(res.getString("cms_email"));
                                tbmisc1.setText(res.getString("cms_misc"));
                                ddstate.setSelectedItem(res.getString("cms_state"));
                                ddcountry.setSelectedItem(res.getString("cms_country"));
                            }

                        } catch (SQLException s) {
                            MainFrame.bslog(s);
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
              disableShipAddress();
              }
           dditemship.setSelectedItem(ddship.getSelectedItem().toString());
           if (ddship.getSelectedItem() == null || ddship.getSelectedItem().toString().isEmpty() || ddship.getSelectedItem().toString().equals("<new>")) {
               jTabbedPane1.setEnabledAt(1, false);
               ddship.setBackground(Color.red);
           } else {
               jTabbedPane1.setEnabledAt(1, true);
               ddship.setBackground(null);
           }
            
       
        }
    }//GEN-LAST:event_ddshipActionPerformed

    private void ddpartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpartActionPerformed
        if (ddpart.getSelectedItem() != null && ! isLoad)
        getItemInfo(ddpart.getSelectedItem().toString());
    }//GEN-LAST:event_ddpartActionPerformed

    private void qtyshippedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusGained
        if (qtyshipped.getText().equals("0")) {
            qtyshipped.setText("");
        }
    }//GEN-LAST:event_qtyshippedFocusGained

    private void qtyshippedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusLost
       
        if (isLoad) {
            return;
        }
        
        String x = BlueSeerUtils.bsformat("", qtyshipped.getText(), "5");
        if (x.equals("error")) {
            qtyshipped.setText("");
            qtyshipped.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            qtyshipped.requestFocus();
        } else {
            qtyshipped.setText(x);
            qtyshipped.setBackground(Color.white);
            setPrice();
        }
        
       
        if (ddpart.getSelectedItem() != null && ! qtyshipped.getText().isEmpty()) {
               if (bsParseDouble(qtyshipped.getText()) > invData.getItemQtyByWarehouseAndLocation(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), ddwh.getSelectedItem().toString(), ddloc.getSelectedItem().toString())) {
                   lbqtyavailable.setBackground(Color.red);
               } else {
                   lbqtyavailable.setBackground(Color.green);
               }
           }
    }//GEN-LAST:event_qtyshippedFocusLost

    private void listpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusLost
       
        if (isLoad) {
            return;
        }
        
        if (! listprice.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", listprice.getText(), "4");
        if (x.equals("error")) {
            listprice.setText("");
            listprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            listprice.requestFocus();
        } else {
            listprice.setText(x);
            listprice.setBackground(Color.white);
        }
        setNetPrice();
        }
    }//GEN-LAST:event_listpriceFocusLost

    private void discountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_discountFocusLost
        
        if (isLoad) {
            return;
        }
        
        if (! discount.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", discount.getText(), "4");
        if (x.equals("error")) {
            discount.setText("");
            discount.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            discount.requestFocus();
        } else {
            discount.setText(x);
            discount.setBackground(Color.white);
        }
        if (discount.getText().isEmpty())
            discount.setText("0.00");
        setNetPrice();
        }
    }//GEN-LAST:event_discountFocusLost

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       executeTask(dbaction.delete, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbmiscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbmiscActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbmiscActionPerformed

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
        if (ddwh.getSelectedItem() != null) {
             ddloc.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
             ddloc.insertItemAt("", 0);
             ddloc.setSelectedIndex(0);
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        if (! isLoad) {
             setPrice();
        }    
    }//GEN-LAST:event_dduomActionPerformed

    private void ddlocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddlocActionPerformed
       if (ddwh.getSelectedItem() != null && ddloc.getSelectedItem() != null && ddpart.getSelectedItem() != null && ! isLoad) {
           
           double qty = 0.0;
           String prefix = "";
           if (cbisallocated.isSelected()) {
               prefix = "QOH Unallocated=";
           qty = invData.getItemQOHUnallocated(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), tbkey.getText());
           
           } else {
            prefix = "QOH Available=";
           qty = invData.getItemQtyByWarehouseAndLocation(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), ddwh.getSelectedItem().toString(), ddloc.getSelectedItem().toString());
          
           }
           String sqty = String.valueOf(qty);
           lbqtyavailable.setText(prefix + sqty);
           if (! qtyshipped.getText().isEmpty()) {
               if (bsParseDouble(qtyshipped.getText()) > qty || qty == 0 ) {
                   lbqtyavailable.setBackground(Color.red);
               } else {
                   lbqtyavailable.setBackground(Color.green);
               }
           }
       }
    }//GEN-LAST:event_ddlocActionPerformed

    private void btaddshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddshiptoActionPerformed
        // + "(cms_code, cms_shipto, cms_name, cms_line1, cms_line2, cms_line3, cms_city, cms_state, cms_zip, cms_country, cms_plantcode, cms_contact, cms_phone, cms_email, cms_misc ) " 
       
        boolean error = false;
        
        if (tbshiptocode.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbshiptocode.requestFocus();
            return;
        }
        if (tbshiptocode.getText().length() > 10) {
            bsmf.MainFrame.show(getMessageTag(1080, "10"));
            tbshiptocode.requestFocus();
            return;
        }
        if (tbzip.getText().length() > 10) {
            bsmf.MainFrame.show(getMessageTag(1080, "10"));
            tbzip.requestFocus();
            return;
        }
        if (tbname.getText().length() > 50) {
            bsmf.MainFrame.show(getMessageTag(1080, "50"));
            tbname.requestFocus();
            return;
        }
        
        if (OVData.isValidCustShipTo(ddcust.getSelectedItem().toString(), tbshiptocode.getText())) {
            bsmf.MainFrame.show(getMessageTag(1022));
        } else {
            ArrayList<String> st = new ArrayList<String>();
            String list = "";
            
            list = ddcust.getSelectedItem().toString() + ":" +
                   tbshiptocode.getText() + ":" + 
                   tbname.getText().replace("'", "") + ":" + 
                   tbaddr1.getText().replace("'", "") + ":" + 
                   tbaddr2.getText().replace("'", "") + ":" + 
                   "" + ":" + 
                   tbcity.getText().replace("'", "") + ":" + 
                   ddstate.getSelectedItem().toString() + ":" + 
                   tbzip.getText() + ":" + 
                   ddcountry.getSelectedItem().toString() + ":" + 
                   "" + ":" + 
                   tbcontact.getText().replace("'", "") + ":" + 
                   tbphone.getText().replace("'", "") + ":" + 
                   tbemail.getText().replace("'", "") + ":" + 
                   tbmisc1.getText().replace("'", "");
            
            st.add(list);
           
           error = OVData.addCustShipToMstr(st, ":");
           if (! error) {
            bsmf.MainFrame.show(getMessageTag(1007));
            reinitCustandShip(ddcust.getSelectedItem().toString(), tbshiptocode.getText()); 
           }
        }
    }//GEN-LAST:event_btaddshiptoActionPerformed

    private void btsacdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacdeleteActionPerformed
         int[] rows = sactable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) sactable.getModel()).removeRow(i);
        }
        retotal(); 
        refreshDisplayTotals();
         
    }//GEN-LAST:event_btsacdeleteActionPerformed

    private void btsacaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacaddActionPerformed
        boolean proceed = true;
        double amount = 0;
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbsacamt.getText());
        if (!m.find() || tbsacamt.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1033));
            proceed = false;
            tbsacamt.requestFocus();
            return;
        }
        
        if (tbsacdesc.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            proceed = false;
            tbsacdesc.requestFocus();
            return;
        }
        
        if (ddsactype.getSelectedItem().toString().startsWith("shipping") &&
                (ddshipvia.getSelectedItem() == null || ddshipvia.getSelectedItem().toString().isBlank()) ) {
            bsmf.MainFrame.show(getMessageTag(1181));
            proceed = false;
            return;
        } 
        
        if (ddsactype.getSelectedItem().toString().equals("discount") &&
                ddsacamttype.getSelectedItem().toString().equals("amount")) {
            amount = -1 * bsParseDouble(tbsacamt.getText());
        } else {
            amount = bsParseDouble(tbsacamt.getText());
        }
        
        if (proceed)
        sacmodel.addRow(new Object[]{ ddsactype.getSelectedItem().toString(), tbsacdesc.getText(), ddsacamttype.getSelectedItem().toString(), String.valueOf(amount)});
        retotal();
        refreshDisplayTotals();
    }//GEN-LAST:event_btsacaddActionPerformed

    private void orddetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orddetMouseClicked
        int row = orddet.rowAtPoint(evt.getPoint());
        int col = orddet.columnAtPoint(evt.getPoint());
        //   "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc", "Tax"
        isLoad = true;  
        ddpart.setSelectedItem(orddet.getValueAt(row, 1).toString());
        dduom.setSelectedItem(orddet.getValueAt(row, 6).toString());
        ddwh.setSelectedItem(orddet.getValueAt(row, 12).toString());
        ddloc.setSelectedItem(orddet.getValueAt(row, 13).toString());
        qtyshipped.setText(orddet.getValueAt(row, 5).toString());
        custnumber.setText(orddet.getValueAt(row, 2).toString());
        tbdesc.setText(orddet.getValueAt(row, 14).toString());
        listprice.setText(orddet.getValueAt(row, 7).toString());
        netprice.setText(orddet.getValueAt(row, 9).toString());
        discount.setText(orddet.getValueAt(row, 8).toString());
        
        
        // ddbom.setSelectedIndex(0);
        
        ddbom.removeAllItems();
        ddbom.insertItemAt("", 0);
        ArrayList<String[]> boms = invData.getBOMsByItemSite(ddpart.getSelectedItem().toString());
        for (String[] wh : boms) {
            ddbom.addItem(wh[0]);
        }
        ddbom.setSelectedItem(OVData.getDefaultBomID(ddpart.getSelectedItem().toString()));
        
        
        if(orddet.getValueAt(row, 17).toString().isBlank() || ((DefaultComboBoxModel)dditemship.getModel()).getIndexOf(orddet.getValueAt(row, 17).toString()) == -1) {
          dditemship.setSelectedItem(ddship.getSelectedItem().toString());
        } else {
          dditemship.setSelectedItem(orddet.getValueAt(row, 17).toString());  
        }
        
        
       
        isLoad = false;
        //tbmisc.setText(orddet.getValueAt(row, 5).toString());
        
       /* blanket stuff not implemented yet
        String order = "";
        String po = "";
        String part = "";
        String line = "";
        
        if ( col == 0 && cbblanket.isSelected()) {
            line = orddet.getValueAt(row, 0).toString();  // line
            order = orddet.getValueAt(row, 3).toString();  // order
            po = orddet.getValueAt(row, 4).toString();  // po
            part = orddet.getValueAt(row, 1).toString();  // part
            this.getSchedRecords(order, po, part, line);
           
        }
       */
    }//GEN-LAST:event_orddetMouseClicked

    private void ddtaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtaxActionPerformed
        if (! isLoad) {
            headertax = OVData.getTaxPercentElementsApplicableByTaxCode(ddtax.getSelectedItem().toString());
            // remove all 'tax' records and refresh
            for (int j = 0; j < sactable.getRowCount(); j++) {
                if (sactable.getValueAt(j, 0).toString().equals("tax"))
               ((javax.swing.table.DefaultTableModel) sactable.getModel()).removeRow(j); 
            }
            //refresh tax records
            for (String[] t : headertax) {
            sacmodel.addRow(new Object[]{ "tax", t[0], "percent", t[1]});
            }
        }
    }//GEN-LAST:event_ddtaxActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if (index == 1 && ddpart != null && ddpart.getItemCount() > 0) {
            ddpart.setSelectedIndex(0);
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void btinvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btinvoiceActionPerformed
        
        if (! canUpdate(this.getClass().getName())) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return;
        }
        
        if (! ddstatus.getSelectedItem().toString().equals(getGlobalProgTag("hold"))) {
        // check for multiple ship destinations...autoinvoicing requires single destination orders
        Set<String> shiptos = new LinkedHashSet<String>();
        for (int j = 0; j < orddet.getRowCount(); j++) {
            shiptos.add(orddet.getValueAt(j, 17).toString());
        } 
        if (shiptos.size() > 1) {
           bsmf.MainFrame.show(getMessageTag(1177));
           return;
        }
        
         String[] message = autoInvoiceOrder();
         if (message[0].equals("1")) { // if error
           bsmf.MainFrame.show(message[1]);
         } else {
           executeTask(dbaction.get, new String[]{tbkey.getText()});
         }
        } else {
          bsmf.MainFrame.show(getMessageTag(1184));  
        }
    }//GEN-LAST:event_btinvoiceActionPerformed

    private void btprintorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintorderActionPerformed
        Set<String> shiptos = new LinkedHashSet<String>();
        boolean isMultiShip = false;
        for (int j = 0; j < orddet.getRowCount(); j++) {
            if (orddet.getValueAt(j, 17).toString().isBlank()) {
                continue;
            }
            shiptos.add(orddet.getValueAt(j, 17).toString());
        } 
        if (shiptos.size() > 1) {
           isMultiShip = true;
        }
        OVData.printCustomerOrder(tbkey.getText(), isMultiShip); 
    }//GEN-LAST:event_btprintorderActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
       executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void listpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusGained
        if (listprice.getText().equals("0")) {
            listprice.setText("");
        }
    }//GEN-LAST:event_listpriceFocusGained

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset(); 
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btprintinvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintinvoiceActionPerformed
        OVData.printInvoiceByOrder(tbkey.getText());
      
    }//GEN-LAST:event_btprintinvoiceActionPerformed

    private void btprintpsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintpsActionPerformed
        OVData.printShipperByOrder(tbkey.getText());
    }//GEN-LAST:event_btprintpsActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
            if (! isLoad) {
            ddpart.removeAllItems();
            ArrayList<String> items = invData.getItemMasterListBySite(ddsite.getSelectedItem().toString()); 
            for (String item : items) {
            ddpart.addItem(item);
            }  
            }
    }//GEN-LAST:event_ddsiteActionPerformed

    private void btupdateitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateitemActionPerformed
        int line = 0;
        String bom = "";
        if (ddbom.getSelectedItem() != null) {
            bom = ddbom.getSelectedItem().toString();
        }
        
        String shipto = "";
        if (dditemship.getSelectedItem() != null) {
            shipto = dditemship.getSelectedItem().toString();
        }
        
        line = getmaxline();
        line++;
        
        int[] rows = orddet.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
                return;
        }
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("closed")) || orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("partial"))) {
                bsmf.MainFrame.show(getMessageTag(1088));
                return;
            } else if (! orddet.getValueAt(i, 1).toString().equals(ddpart.getSelectedItem().toString())) {
                bsmf.MainFrame.show(getMessageTag(1096));
                return;
            }else {
                boolean canproceed = validateDetail();
                if (canproceed) {
                orddet.setValueAt(qtyshipped.getText(), i, 5);
                orddet.setValueAt(dduom.getSelectedItem().toString(), i, 6);
                orddet.setValueAt(listprice.getText(), i, 7);
                orddet.setValueAt(discount.getText(), i, 8);
                orddet.setValueAt(netprice.getText(), i, 9);
                orddet.setValueAt(custnumber.getText(), i, 2);
                orddet.setValueAt(tbdesc.getText(), i, 14);
                orddet.setValueAt(ddwh.getSelectedItem().toString(), i, 12);
                orddet.setValueAt(ddloc.getSelectedItem().toString(), i, 13);
                orddet.setValueAt(bom, i, 16);
                orddet.setValueAt(shipto, i, 17);
                
                refreshDisplayTotals();         
                listprice.setText("");
                netprice.setText("");
                discount.setText("");
                qtyshipped.setText("");
                ddpart.requestFocus();
                
                }
            }
        }
        
        
    }//GEN-LAST:event_btupdateitemActionPerformed

    private void btLookUpCustItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpCustItemActionPerformed
        lookUpFrameCustItem();
    }//GEN-LAST:event_btLookUpCustItemActionPerformed

    private void btLookUpItemDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemDescActionPerformed
        lookUpFrameItemDesc();
    }//GEN-LAST:event_btLookUpItemDescActionPerformed

    private void btLookUpBillToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpBillToActionPerformed
       lookUpFrameBillTo();
    }//GEN-LAST:event_btLookUpBillToActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void ddbomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddbomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddbomActionPerformed

    private void ddsacamttypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsacamttypeActionPerformed
         if (ddsacamttype.getSelectedItem().toString().equals("percent")) {
            percentlabel.setText("percent");
        } else {
            percentlabel.setText("amount");
        }
    }//GEN-LAST:event_ddsacamttypeActionPerformed

    private void dditemshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemshipActionPerformed
       if (! isLoad && dditemship.getSelectedItem() != null) {
           String[] addr = getShipAddressInfo(ddcust.getSelectedItem().toString(), dditemship.getSelectedItem().toString());
           itemshipaddrlbl.setText(addr[1] + "..." + addr[2] + "..." + addr[5] + ", " + addr[6] + " " + addr[7]);
       }
    }//GEN-LAST:event_dditemshipActionPerformed

    private void ddstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddstatusActionPerformed
        if (! isLoad && ddstatus.getSelectedItem() != null) {
            if (ddstatus.getSelectedItem().toString().equals(getGlobalProgTag("hold"))) {
                ddstatus.setBackground(Color.red); 
            } else {
                ddstatus.setBackground(null);  
            }
        }
    }//GEN-LAST:event_ddstatusActionPerformed

    private void btaddattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddattachmentActionPerformed
        OVData.addFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), this );
        getAttachments(tbkey.getText());
    }//GEN-LAST:event_btaddattachmentActionPerformed

    private void btdeleteattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteattachmentActionPerformed
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpBillTo;
    private javax.swing.JButton btLookUpCustItem;
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattachment;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btaddshipto;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteattachment;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btinvoice;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btprintinvoice;
    private javax.swing.JButton btprintorder;
    private javax.swing.JButton btprintps;
    private javax.swing.JButton btsacadd;
    private javax.swing.JButton btsacdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdateitem;
    private javax.swing.JCheckBox cbblanket;
    private javax.swing.JCheckBox cbconfirm;
    private javax.swing.JCheckBox cbedi;
    private javax.swing.JCheckBox cbisallocated;
    private javax.swing.JCheckBox cbissourced;
    private javax.swing.JCheckBox cbplan;
    private javax.swing.JTextField custnumber;
    private javax.swing.JComboBox<String> ddbom;
    private javax.swing.JComboBox<String> ddcountry;
    private javax.swing.JComboBox<String> ddcurr;
    private static javax.swing.JComboBox ddcust;
    private static javax.swing.JComboBox dditemship;
    private javax.swing.JComboBox<String> ddloc;
    private static javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox<String> ddsacamttype;
    private javax.swing.JComboBox<String> ddsactype;
    private static javax.swing.JComboBox ddship;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> ddtax;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JTextField discount;
    private com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JLabel itemshipaddrlbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelLines;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSched;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelmessage;
    private javax.swing.JLabel lblCustItemAndDesc;
    private javax.swing.JLabel lblIsSourced;
    private javax.swing.JLabel lblcurr;
    private javax.swing.JLabel lblcustname;
    private javax.swing.JLabel lbldollars;
    private javax.swing.JLabel lblpart1;
    private javax.swing.JLabel lblshiptoaddr;
    private javax.swing.JLabel lblshiptoname;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lbltotdollars;
    private javax.swing.JLabel lbqtyavailable;
    private javax.swing.JTextField listprice;
    private javax.swing.JTextField netprice;
    private com.toedter.calendar.JDateChooser orddate;
    private javax.swing.JTable orddet;
    private javax.swing.JPanel panelAttachment;
    private javax.swing.JLabel percentlabel;
    private javax.swing.JTextField ponbr;
    private javax.swing.JTextField qtyshipped;
    private javax.swing.JTextField remarks;
    private javax.swing.JTable sactable;
    private javax.swing.JTable tableattachment;
    private javax.swing.JTable tablesched;
    private javax.swing.JTextField tbaddr1;
    private javax.swing.JTextField tbaddr2;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontact;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbhdrwh;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbmisc;
    private javax.swing.JTextField tbmisc1;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbsacamt;
    private javax.swing.JTextField tbsacdesc;
    private javax.swing.JTextField tbshiptocode;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField tbtottax;
    private javax.swing.JTextField tbzip;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
