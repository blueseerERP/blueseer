/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author vaughnte
 */
public class OrderMaintPanel extends javax.swing.JPanel implements IBlueSeer {

    // global variable declarations
                boolean isLoad = false;
                String terms = "";
                String aracct = "";
                String arcc = "";
                String status = "";
                String curr = "";
                String basecurr = OVData.getDefaultCurrency();
                boolean custitemonly = true;
                boolean autoallocate = false;
                
                DecimalFormat df = new DecimalFormat("#0.0000", new DecimalFormatSymbols(Locale.US));
                Map<Integer, ArrayList<String[]>> linetax = new HashMap<Integer, ArrayList<String[]>>();
                ArrayList<String[]> headertax = new ArrayList<String[]>();
     
                public static JFrame frame = new JFrame("Choose Item:");
                public static javax.swing.table.DefaultTableModel lookUpModel = null;
                public static JTable lookUpTable = new JTable();
                public static MouseListener mllu = null;
    
    // global datatablemodel declarations
    OrderMaintPanel.MyTableModel myorddetmodel = new OrderMaintPanel.MyTableModel(new Object[][]{},
            new String[]{
               "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc", "Tax"
            }
    );
    
    javax.swing.table.DefaultTableModel modelsched = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Date", "Ref", "Qty", "Type"
            });
    javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Type", "Desc", "Value", "Amt"
            });
    
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
    
     
    public OrderMaintPanel() {
        initComponents();
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
        
    public void setComponentDefaultValues() {
        
        isLoad = true;
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", jPanelMain);
        jTabbedPane1.add("Lines", jPanelLines);
        jTabbedPane1.add("Schedule", jPanelSched);
        
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(2, false);
        
        tbkey.setText("");
        tbkey.setEditable(true);
        tbkey.setForeground(Color.black);
        
       
        cbisallocated.setText("Allocation?");
        
        
        ArrayList<String> mylist = new ArrayList<String>();
         jPanelSched.setVisible(false);
        java.util.Date now = new java.util.Date();
       
        lblstatus.setText("");
        lblstatus.setForeground(Color.black);
       
        cbissourced.setSelected(false);
        cbisallocated.setSelected(false);
        
        listprice.setText("0.00");
        netprice.setText("0.00");
        qtyshipped.setText("0");
        discount.setText("0.00");
        ponbr.setText("");
        lblcustname.setText("");
        lblshiptoaddr.setText("");
        lblcurr.setText("");
        ddsactype.setSelectedIndex(0);
        
        duedate.setDate(now);
        orddate.setDate(now);
        
        
        myorddetmodel.setRowCount(0);
        myorddetmodel.addTableModelListener(ml);
        orddet.setModel(myorddetmodel);
        
        //hide columns
        orddet.getColumnModel().getColumn(2).setMaxWidth(0);
        orddet.getColumnModel().getColumn(2).setMinWidth(0);
        orddet.getColumnModel().getColumn(3).setMaxWidth(0);
        orddet.getColumnModel().getColumn(3).setMinWidth(0);
        orddet.getColumnModel().getColumn(4).setMaxWidth(0);
        orddet.getColumnModel().getColumn(4).setMinWidth(0);
        
        
        sacmodel.setRowCount(0);
        sactable.setModel(sacmodel);
        modelsched.setRowCount(0);
        tablesched.setModel(modelsched);
        
        tbhdrwh.setText("");
        lblIsSourced.setIcon(null);
        remarks.setText("");
        tbtotqty.setText("");
        tbtotdollars.setText("");
        tbtottax.setText("");
        totlines.setText("");
        custnumber.setText("");
        
        ddpart.setForeground(Color.black);
        custnumber.setForeground(Color.black);
        custnumber.setEditable(false);
        tbdesc.setForeground(Color.black);
        tbdesc.setEditable(false);
        
        autoallocate = OVData.isOrderAutoAllocate();
        cbisallocated.setSelected(autoallocate);
        
        
        ddsite.removeAllItems();
        mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        // lets check order control for custitemonly versus any item from item master to be filled into ddpart
        custitemonly = OVData.isCustItemOnly();
        if (! custitemonly) {
            ddpart.removeAllItems();
            ArrayList<String> items = OVData.getItemMasterListBySite(ddsite.getSelectedItem().toString()); 
            for (String item : items) {
            ddpart.addItem(item);
            }  
        } 
        
         ddwh.removeAllItems();
         ddwh.insertItemAt("", 0);
         ddwh.setSelectedIndex(0);
        ArrayList<String> whs = OVData.getWareHouseList();
        for (String wh : whs) {
            ddwh.addItem(wh);
        }
        
        
         ddloc.removeAllItems();
         ddloc.insertItemAt("", 0);
         ddloc.setSelectedIndex(0);
        ArrayList<String> loc = OVData.getLocationList();
        for (String lc : loc) {
            ddloc.addItem(lc);
        }
        
        
        ddcurr.removeAllItems();
         ddcurr.insertItemAt("", 0);
         ddcurr.setSelectedIndex(0);
        mylist = OVData.getCurrlist();
        for (String code : mylist) {
            ddcurr.addItem(code);
        }
        
        dduom.removeAllItems();
        dduom.insertItemAt("", 0);
         dduom.setSelectedIndex(0);
        mylist = OVData.getUOMList();
        for (String code : mylist) {
            dduom.addItem(code);
        }
        
        ddcust.removeAllItems();
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        ArrayList mycusts = OVData.getcustmstrlist();
        for (int i = 0; i < mycusts.size(); i++) {
            ddcust.addItem(mycusts.get(i));
        }
        ddship.removeAllItems();
        
        
        ddtax.removeAllItems();
        ArrayList<String> tax = OVData.gettaxcodelist();
        for (int i = 0; i < tax.size(); i++) {
            ddtax.addItem(tax.get(i));
        }
        ddtax.insertItemAt("", 0);
        ddtax.setSelectedIndex(0);
        
        
        ddstate.removeAllItems();
        ArrayList states = OVData.getCodeMstrKeyList("state");
        for (int i = 0; i < states.size(); i++) {
            ddstate.addItem(states.get(i).toString());
        }
        if (ddstate.getItemCount() > 0) {
           ddstate.setSelectedIndex(0); 
        }
       
        
        ddshipvia.removeAllItems();
        mylist = OVData.getScacCarrierOnly();  
        for (int i = 0; i < mylist.size(); i++) {
            ddshipvia.addItem(mylist.get(i));
        }
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        
        ddstatus.removeAllItems();
        mylist = OVData.getCodeMstr("orderstatus");
        for (int i = 0; i < mylist.size(); i++) {
            ddstatus.addItem(mylist.get(i));
        }
        ddstatus.setSelectedItem("open");
        
        
        
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
                   
                    // custom set
                    
                    refreshDisplayTotals();
                    
                    if (ddstatus.getSelectedItem().toString().compareTo("close") == 0) {
                             lblstatus.setText("Order has been invoiced and is now closed.");
                             lblstatus.setForeground(Color.blue);
                             setPanelComponentState(this, false);
                             btnew.setEnabled(true);
                             btordnbrbrowse.setEnabled(true);
                             btclear.setEnabled(true);
                             btprintinvoice.setEnabled(true);
                             btprintps.setEnabled(true);
                             btprintorder.setEnabled(true);
                    } else {
                             
                             lblstatus.setText("Order has not been shipped.");
                             lblstatus.setForeground(Color.red);
                              setPanelComponentState(this, true);
                              btadd.setEnabled(false);
                              btprintinvoice.setEnabled(false);
                              btprintps.setEnabled(false);
                    }
                    
                    if (cbblanket.isSelected())
                    jPanelSched.setVisible(true);
                    else
                    jPanelSched.setVisible(false);
                   
                   
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
        return m;
    }
    
    public boolean validateInput(String x) {
        boolean b = true;
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must enter a code");
                    tbkey.requestFocus();
                    return b;
                }
                
                if (orddet.getRowCount() == 0) {
                    b = false;
                    bsmf.MainFrame.show("No Line Items");
                    ddship.requestFocus();
                    return b;
                }
                
        
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must choose a site");
                    ddsite.requestFocus();
                    return b;
                }
                
                if ( ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty() ) {
                    b = false;
                    bsmf.MainFrame.show("Must choose a customer code");
                    ddcust.requestFocus();
                    return b;
                }
                if ( ddship.getSelectedItem() == null || ddship.getSelectedItem().toString().isEmpty() || ddship.getSelectedItem().toString().equals("<new>")) {
                    b = false;
                    bsmf.MainFrame.show("Must choose a valid shipto code");
                    ddship.requestFocus();
                    return b;
                }
                
               
                if (ddcurr.getSelectedItem() == null || ddcurr.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must choose a currency");
                    return b;
                }
                                
                
                
                terms = OVData.getCustTerms(ddcust.getSelectedItem().toString());
                arcc = OVData.getCustSalesCC(ddcust.getSelectedItem().toString());
                aracct = OVData.getCustSalesAcct(ddcust.getSelectedItem().toString());
                curr = ddcurr.getSelectedItem().toString();
                if (terms == null   || aracct == null   || arcc == null || curr == null ||
                        terms.isEmpty() || aracct.isEmpty() || arcc.isEmpty() || curr.isEmpty()
                         ) {
                        b = false;
                        bsmf.MainFrame.show("Terms|ARacct|ARcc|Currency is not defined for this customer");
                        return b;
                    }   
                
                
                
                
                 // lets check for foreign currency with no exchange rate
            if (! curr.toUpperCase().equals(basecurr.toUpperCase())) {
            if (OVData.getExchangeRate(basecurr, curr).isEmpty()) {
                bsmf.MainFrame.show("Foreign currency has no exchange rate " + curr + "/" + basecurr);
                b = false;
            }
            }
                
                
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
     
      setPanelComponentState(jPanelMain, false); 
       setPanelComponentState(jPanelLines, false); 
       setPanelComponentState(jPanelSched, false); 
       setPanelComponentState(this, false); 
       
       
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btordnbrbrowse.setEnabled(true);
        btordpobrowse.setEnabled(true);
        btordduebrowse.setEnabled(true);
        btorddatebrowse.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask("get", arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }

    }
    
    public String[] addRecord(String[] x) {
     String[] m = new String[2];
     
     try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                proceed = validateInput("addRecord");
                
                if (proceed) {
                       
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
                       
                       
                       
                        
                        
                         int invqty = 0;
                         int allqty = 0;
                         int qohunall = 0;
                         int allocationvalue = 0;
                         boolean completeAllocation = true;
                        for (int j = 0; j < orddet.getRowCount(); j++) {
                            // get total inventory for line item
                            // get allocated on current 'open' orders
                             // now get QOH
                                    res = st.executeQuery("SELECT  sum(in_qoh) as totqty  " +
                                    " FROM  in_mstr  " +
                                    " where in_part = " + "'" + orddet.getValueAt(j, 1).toString() + "'" + 
                                    " group by in_part ;");

                                    while (res.next()) {
                                    invqty = res.getInt("totqty");
                                    }
                                    
                                    res = st.executeQuery("SELECT  sum(case when sod_all_qty = '' then 0 else (sod_all_qty - sod_shipped_qty) end) as allqty  " +
                                    " FROM  sod_det inner join so_mstr on so_nbr = sod_nbr  " +
                                    " where sod_part = " + "'" + orddet.getValueAt(j, 1).toString() + "'" + 
                                    " AND so_status <> 'close' " + 
                                    " AND so_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +          
                                    " group by sod_part ;");

                                    while (res.next()) {
                                    allqty = res.getInt("allqty");
                                    }
                                    
                                 qohunall = invqty - allqty; 
                                 
                                 if (Integer.valueOf(orddet.getValueAt(j,5).toString()) <= qohunall) {
                                     allocationvalue = Integer.valueOf(orddet.getValueAt(j,5).toString());
                                 } else {
                                     allocationvalue = qohunall;
                                     completeAllocation = false;
                                 }
                            
                            st.executeUpdate("insert into sod_det "
                                + "(sod_line, sod_part, sod_custpart, sod_nbr, sod_po, sod_ord_qty, sod_uom, sod_all_qty, sod_listprice, sod_disc, sod_netprice, sod_ord_date, sod_due_date, "
                                + "sod_shipped_qty, sod_status, sod_wh, sod_loc, sod_desc, sod_taxamt, sod_site) "
                                + " values ( " 
                                + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                                + "'" + orddet.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                                + "'" + orddet.getValueAt(j, 3).toString().replace("'", "") + "'" + ","
                                + "'" + orddet.getValueAt(j, 4).toString().replace("'", "") + "'" + ","
                                + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","        
                                + "'" + allocationvalue + "'" + ","  // sod_all_qty is allocated
                                + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 8).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 9).toString() + "'" + ","
                                + "'" + bsmf.MainFrame.dfdate.format(orddate.getDate()).toString() + "'" + ","
                                + "'" + bsmf.MainFrame.dfdate.format(duedate.getDate()).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 10).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 11).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 12).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 13).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 14).toString() + "'" + ","
                                + "'" + orddet.getValueAt(j, 15).toString() + "'" + ","        
                                + "'" + ddsite.getSelectedItem().toString() + "'" 
                                + ")"
                                + ";");

                              if (linetax.containsKey(orddet.getValueAt(j,0))) {
                                  for (String[] s : (ArrayList<String[]>)linetax.get(orddet.getValueAt(j,0))) {
                                          st.executeUpdate("insert into sod_tax "
                                    + "(sodt_nbr, sodt_line, sodt_desc, sodt_percent, sodt_type ) "
                                    + " values ( " 
                                    + "'" + tbkey.getText().toString() + "'" + ","
                                    + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                                    + "'" + s[0].toString() + "'" + ","
                                    + "'" + s[1].toString() + "'" + ","
                                    + "'" + s[2].toString() + "'"  
                                    + ")"
                                    + ";");
                                  }
                              }
                        } 
                        
                        
                        
                        
                        
                        
                        // now add applied discounts and summary charges
                     for (int j = 0; j < sactable.getRowCount(); j++) {
                        st.executeUpdate("insert into sos_det "
                            + "(sos_nbr, sos_desc, sos_type, sos_amttype, sos_amt ) "
                            + " values ( " 
                            + "'" + tbkey.getText().toString() + "'" + ","
                            + "'" + sactable.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + "'" + sactable.getValueAt(j, 0).toString().replace("'", "") + "'" + ","
                            + "'" + sactable.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                            + "'" + sactable.getValueAt(j, 3).toString().replace("'", "") + "'" 
                            + ")"
                            + ";");
                    }
                     
                     // now add headertax if any
                     if (! headertax.isEmpty()) {
                              for (String[] s : headertax) {
                                      st.executeUpdate("insert into so_tax "
                                + "(sot_nbr, sot_desc, sot_percent, sot_type ) "
                                + " values ( " 
                                + "'" + tbkey.getText().toString() + "'" + ","
                                + "'" + s[0].toString() + "'" + ","
                                + "'" + s[1].toString() + "'" + ","
                                + "'" + s[2].toString() + "'"  
                                + ")"
                                + ";");
                                      
                              }
                          }
                     
                      String isallocated = "";
                        if (cbisallocated.isSelected()) {
                          isallocated = "c";
                          if (! completeAllocation) {
                              isallocated = "p";
                          }
                        }
                        
                      // insert order header
                         st.executeUpdate("insert into so_mstr "
                        + "(so_nbr, so_cust, so_ship, so_site, so_curr, so_shipvia, so_wh, so_po, so_due_date, so_ord_date, "
                        + " so_create_date, so_userid, so_status, so_isallocated, "
                        + " so_terms, so_ar_acct, so_ar_cc, so_rmks, so_type, so_taxcode ) "
                        + " values ( " + "'" + tbkey.getText().toString() + "'" + ","
                        + "'" + ddcust.getSelectedItem() + "'" + ","
                        + "'" + ddship.getSelectedItem() + "'" + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + ddcurr.getSelectedItem().toString() + "'" + ","        
                        + "'" + ddshipvia.getSelectedItem().toString() + "'" + ","
                        + "'" + uniqwh + "'" + ","
                        + "'" + ponbr.getText().replace("'", "") + "'" + ","
                        + "'" + bsmf.MainFrame.dfdate.format(duedate.getDate()).toString() + "'" + ","
                        + "'" + bsmf.MainFrame.dfdate.format(orddate.getDate()).toString() + "'" + ","
                        + "'" + bsmf.MainFrame.dfdate.format(new Date()) + "'" + ","
                        + "'" + bsmf.MainFrame.userid + "'" + ","
                        + "'" + ddstatus.getSelectedItem().toString() + "'" + ","
                        + "'" + isallocated + "'" + ","        
                        + "'" + terms + "'" + ","
                        + "'" + aracct + "'" + ","
                        + "'" + arcc + "'" + ","
                        + "'" + remarks.getText().replace("'", "") + "'" + "," 
                        + "'" + ordertype + "'" + ","
                        + "'" + ddtax.getSelectedItem().toString() + "'"
                        + ")"
                        + ";");
                     
                     
                     
                  m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                   
                // if autoinvoice
                if (OVData.isAutoInvoice()) {
                 
                boolean sure = bsmf.MainFrame.warn("This is an auto-invoice order...Are you sure you want to auto-invoice?");     
                    if (sure) {     
                       m = autoInvoiceOrder();
                    }
                } // if autoinvoice
                  
                  
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                 m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
             m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordConnError};
        }
     
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     
     try {
            boolean proceed = true;
            int i = 0;
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;   
               proceed = validateInput("updateRecord");
                
                if (proceed) {
                      int invqty = 0;
                         int allqty = 0;
                         int qohunall = 0;
                         int allocationvalue = 0;
                         boolean completeAllocation = true;   
                     
                    // first delete any sod_det line records that have been
                    // disposed from the current orddet table
                    ArrayList<String> deletes = new ArrayList<String>();
                    boolean goodLine = false;
                    res = st.executeQuery("Select sod_line from sod_det where sod_nbr = " + "'" + tbkey.getText() + "'"  + ";" );
                    while (res.next()) {
                      goodLine = false;
                      for (int j = 0; j < orddet.getRowCount(); j++) {
                         if (orddet.getValueAt(j, 0).toString().equals(res.getString("sod_line"))) {
                             goodLine = true;
                         }
                      }
                      if (! goodLine) {
                          deletes.add(res.getString("sod_line"));
                      }
                    }
                    for (String d : deletes) {
                       st.executeUpdate("delete from sod_det " + 
                               " where sod_nbr = " + "'" + tbkey.getText() + "'" +
                               " and sod_line = " + "'" + d + "'" +
                               ";");
                    }
                    
                    
                    
                    for (int j = 0; j < orddet.getRowCount(); j++) {
                         i = 0;
                        // skip closed lines
                        if (orddet.getValueAt(j, 11).toString().equals("close"))
                            continue;
                        
                        
                        
                           // get total inventory for line item
                            // get allocated on current 'open' orders
                             // now get QOH
                        
                                invqty = 0;
                                allqty = 0;
                                qohunall = 0;
                                allocationvalue = 0;
                                
                                    res = st.executeQuery("SELECT  sum(in_qoh) as totqty  " +
                                    " FROM  in_mstr  " +
                                    " where in_part = " + "'" + orddet.getValueAt(j, 1).toString() + "'" + 
                                    " group by in_part ;");

                                    while (res.next()) {
                                    invqty = res.getInt("totqty");
                                    }
                                    
                                    res = st.executeQuery("SELECT  sum(case when sod_all_qty = '' then 0 else (sod_all_qty - sod_shipped_qty) end) as allqty  " +
                                    " FROM  sod_det inner join so_mstr on so_nbr = sod_nbr  " +
                                    " where sod_part = " + "'" + orddet.getValueAt(j, 1).toString() + "'" + 
                                    " AND so_status <> 'close' " + 
                                    " AND so_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +   
                                    " AND so_nbr <> " + "'" + tbkey.getText() + "'" +
                                    " group by sod_part ;");

                                    while (res.next()) {
                                    allqty = res.getInt("allqty");
                                    }
                                    
                                 qohunall = invqty - allqty; 
                                 
                                 if (Integer.valueOf(orddet.getValueAt(j,5).toString()) <= qohunall) {
                                     allocationvalue = Integer.valueOf(orddet.getValueAt(j,5).toString());
                                 } else {
                                     allocationvalue = qohunall;
                                     completeAllocation = false;
                                 }  
                        
                        
                        
                            res = st.executeQuery("Select sod_line from sod_det where sod_nbr = " + "'" + tbkey.getText() + "'" +
                                " and sod_line = " + "'" + orddet.getValueAt(j, 0).toString() + "'" + ";" );
                            while (res.next()) {
                            i++;
                            }
                            if (i > 0) {                                
                              st.executeUpdate("update sod_det set "
                            + " sod_part = " + "'" + orddet.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + " sod_custpart = " + "'" + orddet.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                            + " sod_po = " + "'" + ponbr.getText().replace("'", "") + "'" + ","
                            + " sod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + ","        
                            + " sod_ord_qty = " + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + " sod_uom = " + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","        
                            + " sod_all_qty = " + "'" + allocationvalue + "'" + ","        
                            + " sod_listprice = " + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","
                            + " sod_disc = " + "'" + orddet.getValueAt(j, 8).toString() + "'" + ","
                            + " sod_wh = " + "'" + orddet.getValueAt(j, 12).toString() + "'" + ","
                            + " sod_loc = " + "'" + orddet.getValueAt(j, 13).toString() + "'" + ","
                            + " sod_due_date = " + "'" + bsmf.MainFrame.dfdate.format(duedate.getDate()).toString() + "'" + ","
                            + " sod_ord_date = " + "'" + bsmf.MainFrame.dfdate.format(orddate.getDate()).toString() + "'" + ","        
                            + " sod_netprice = " + "'" + orddet.getValueAt(j, 9).toString() + "'"
                            + " where sod_nbr = " + "'" + tbkey.getText() + "'" 
                            + " AND sod_line = " + "'" + orddet.getValueAt(j, 0).toString() + "'"
                            + ";");
                            } else {
                             st.executeUpdate("insert into sod_det "
                            + "(sod_line, sod_part, sod_custpart, sod_nbr, sod_po, sod_ord_qty, sod_uom, sod_all_qty, sod_listprice, sod_disc, sod_netprice, sod_ord_date, sod_due_date, "
                            + "sod_shipped_qty, sod_status, sod_wh, sod_loc, sod_site) "
                            + " values ( " 
                            + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","        
                            + "'" + allocationvalue + "'" + ","  // sod_all_qty is allocated
                            + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + bsmf.MainFrame.dfdate.format(orddate.getDate()).toString() + "'" + ","
                            + "'" + bsmf.MainFrame.dfdate.format(duedate.getDate()).toString() + "'" + ","        
                            + "'" + orddet.getValueAt(j, 10).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 11).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 12).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 13).toString() + "'" + ","                
                            + "'" + ddsite.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");   
                            }

                    }
                    
                    // now delete all applicable sos_det and re-add applied discounts and summary charges
                     st.executeUpdate("delete from sos_det where sos_nbr = " + "'" + tbkey.getText().toString() + "'" + ";");
                     for (int j = 0; j < sactable.getRowCount(); j++) {
                        st.executeUpdate("insert into sos_det "
                            + "(sos_nbr, sos_desc, sos_type, sos_amttype, sos_amt ) "
                            + " values ( " 
                            + "'" + tbkey.getText().toString() + "'" + ","
                            + "'" + sactable.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + "'" + sactable.getValueAt(j, 0).toString().replace("'", "") + "'" + ","
                            + "'" + sactable.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                            + "'" + sactable.getValueAt(j, 3).toString().replace("'", "") + "'" 
                            + ")"
                            + ";");

                    }
                     
                     
                     // now update the order
                     String isallocated = "";
                        if (cbisallocated.isSelected()) {
                          isallocated = "c";
                          if (! completeAllocation) {
                              isallocated = "p";
                          }
                        }
                      st.executeUpdate("update so_mstr "
                        + " set so_ship = " + "'" + ddship.getSelectedItem() + "'" + ","
                        + " so_po = " + "'" + ponbr.getText().replace("'", "") + "'" + ","
                        + " so_isallocated = " + "'" + isallocated + "'" + ","        
                        + " so_rmks = " + "'" + remarks.getText().replace("'", "") + "'" + ","        
                        + " so_status = " + "'" + ddstatus.getSelectedItem() + "'" + ","
                        + " so_site = " + "'" + ddsite.getSelectedItem() + "'" + ","  
                        + " so_curr = " + "'" + ddcurr.getSelectedItem() + "'" + ","        
                        + " so_taxcode = " + "'" + ddtax.getSelectedItem() + "'" + ","
                        + " so_shipvia = " + "'" + ddshipvia.getSelectedItem() + "'" + ","        
                        + " so_due_date = " + "'" + bsmf.MainFrame.dfdate.format(duedate.getDate()).toString() + "'" + ","
                        + " so_ord_date = " + "'" + bsmf.MainFrame.dfdate.format(orddate.getDate()).toString() + "'" 
                        + " where so_nbr = " + "'" + tbkey.getText().toString() + "'"
                        + ";");
                     
                     
                    
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
        }
     
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                        st.executeUpdate("delete from sod_det where sod_nbr = " + "'" + tbkey.getText() + "'" + ";");   
                int i = st.executeUpdate("delete from so_mstr where so_nbr = " + "'" + tbkey.getText() + "'" + ";");
                    if (i > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    initvars(null);
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordConnError};
        }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
       
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from so_mstr where so_nbr = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                    i++;
                    tbkey.setText(x[0]);
                    tbkey.setEditable(false);
                    ddcust.setSelectedItem(res.getString("so_cust"));
                    ddcust.setEnabled(false);
                    cbissourced.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("so_issourced")));
                    ddstatus.setSelectedItem(res.getString("so_status"));
                    ddcurr.setSelectedItem(res.getString("so_curr"));
                    ddshipvia.setSelectedItem(res.getString("so_shipvia"));
                    ddtax.setSelectedItem(res.getString("so_taxcode"));
                    ddsite.setSelectedItem(res.getString("so_site"));
                    if (ddship.getItemCount() > 0)
                    ddship.setSelectedItem(res.getString("so_ship"));
                    ddship.setEditable(true);
                    ponbr.setText(res.getString("so_po"));
                    remarks.setText(res.getString("so_rmks"));
                    tbhdrwh.setText(res.getString("so_wh"));
                    duedate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("so_due_date")));
                    orddate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("so_ord_date")));
                    
                    if (res.getString("so_isallocated").equals("c")) {
                        cbisallocated.setSelected(true);
                        cbisallocated.setText("Allocation?");
                    } 
                    else if (res.getString("so_isallocated").equals("p")) {
                        cbisallocated.setSelected(true);
                        cbisallocated.setText("Allocation? (partial)");
                    } else {
                        cbisallocated.setSelected(false);
                        cbisallocated.setText("Allocation?");
                    }
                    
                    if (res.getString("so_type").compareTo("BLANKET") == 0) {
                    cbblanket.setSelected(true);
                    cbblanket.setEnabled(false);
                    } else {
                    cbblanket.setSelected(false);
                    cbblanket.setEnabled(false);
                    }
                }
               
                 // myorddetmodel  "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status"
                res = st.executeQuery("select * from sod_det where sod_nbr = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                  myorddetmodel.addRow(new Object[]{res.getString("sod_line"), res.getString("sod_part"),
                      res.getString("sod_custpart"), res.getString("sod_nbr"), 
                      res.getString("sod_po"), res.getString("sod_ord_qty"), res.getString("sod_uom"), BlueSeerUtils.priceformat(res.getString("sod_listprice")),
                      BlueSeerUtils.priceformat(res.getString("sod_disc")), BlueSeerUtils.priceformat(res.getString("sod_netprice")), res.getString("sod_shipped_qty"), res.getString("sod_status"),
                      res.getString("sod_wh"), res.getString("sod_loc"), res.getString("sod_desc"), res.getString("sod_taxamt")
                  });
                }
                
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError};  
        }
      return m;
    }
    
     
    // custom funcs 
    public String[] autoInvoiceOrder() {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
         int shipperid = OVData.getNextNbr("shipper");     
                     boolean error = OVData.CreateShipperHdr(String.valueOf(shipperid), ddsite.getSelectedItem().toString(),
                             String.valueOf(shipperid), 
                              ddcust.getSelectedItem().toString(),
                              ddship.getSelectedItem().toString(),
                              tbkey.getText(),
                              ponbr.getText().replace("'", ""),  // po
                              ponbr.getText().replace("'", ""),  // ref
                              dfdate.format(duedate.getDate()).toString(),
                              dfdate.format(orddate.getDate()).toString(),
                              remarks.getText().replace("'", ""),
                              ddshipvia.getSelectedItem().toString(),
                              "S" ); 

                     if (! error) {
                         OVData.CreateShipperDetFromTable(orddet, String.valueOf(shipperid), dfdate.format(duedate.getDate()).toString(), ddsite.getSelectedItem().toString());
                     }
                     
                     if (! error) {
                       OVData.updateShipperSAC(String.valueOf(shipperid));
                     }

                     // now confirm shipment
                     String[] message = OVData.confirmShipment(String.valueOf(shipperid), now);
                    
                 return message;
    }
    
    public void custChangeEvent(String mykey) {
            clearShipAddress();
            ddship.removeAllItems();
            
            
           if (ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty() ) {
               ddcust.setBackground(Color.red);
           } else {
               ddcust.setBackground(null);
           }
            
           
            
            ArrayList mycusts = OVData.getcustshipmstrlist(ddcust.getSelectedItem().toString());
            for (int i = 0; i < mycusts.size(); i++) {
                ddship.addItem(mycusts.get(i));
            }
            ddship.insertItemAt("",0);
            ddship.insertItemAt("<new>",1);
            
            if (ddship.getItemCount() == 2) {
              ddship.setBackground(Color.red); 
           }
            
            
            
            
        try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                res = st.executeQuery("select cm_name, cm_carrier, cm_tax_code, cm_curr from cm_mstr where cm_code = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    lblcustname.setText(res.getString("cm_name"));
                    ddshipvia.setSelectedItem((res.getString("cm_carrier")));
                    ddtax.setSelectedItem((res.getString("cm_tax_code")));
                    ddcurr.setSelectedItem((res.getString("cm_curr")));
                }
                
                if (custitemonly) {
                    ddpart.removeAllItems();
                    res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + mykey + "'" + ";");
                    while (res.next()) {
                        ddpart.addItem(res.getString("cup_item"));
                    }
                    res = st.executeQuery("select cpr_disc, cpr_item from cpr_mstr where cpr_cust = " + "'" + mykey + "'" + 
                                          " AND cpr_type = " + "'" + "DISCOUNT" + "'" + ";");
                    while (res.next()) {
                      sacmodel.addRow(new Object[]{ "discount", res.getString("cpr_item"), "percent", res.getString("cpr_disc")
                      });
                    }
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("SQL Code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
  
    public void reinitCustandShip(String mykey, String shipto) {
            clearShipAddress();
            ddship.removeAllItems();
            
            ArrayList mycusts = OVData.getcustshipmstrlist(mykey);
            for (int i = 0; i < mycusts.size(); i++) {
                ddship.addItem(mycusts.get(i));
            }
            ddship.insertItemAt("",0);
            ddship.insertItemAt("<new>",1);
            
            ddship.setSelectedItem(shipto);
                    
     
            
     
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
        btaddshipto.setEnabled(false);
    }
        
    public void getSchedRecords(String order, String po, String part, String line) {
        modelsched.setNumRows(0);
        
        try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                res = st.executeQuery("select * from srl_mstr where " +
                        " srl_so = " + "'" + order + "'" + 
                        " AND srl_po = " + "'" + po + "'" + 
                        " AND srl_part = " + "'" + part + "'" + 
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
                bsmf.MainFrame.show("unable to select srl_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    } 
     
    public void getparts(String part) {
        try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
               // partnbr.removeAllItems();
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
                    
                    discount.setText("0.0000");
                    listprice.setText("0.0000");
                    listprice.setBackground(Color.white);
                    
                    netprice.setText("0.0000");
                    qtyshipped.setText("0");
                }
                
                }
                
             if (k >= 0) {   
            discount.setText("0.0000");
            listprice.setText("0.0000");
            netprice.setText("0.0000");
            qtyshipped.setText("0");
            tbdesc.setText(OVData.getItemDesc(ddpart.getSelectedItem().toString()));
            custnumber.setText(OVData.getCustPartFromPart(ddcust.getSelectedItem().toString(),ddpart.getSelectedItem().toString()));
            dduom.setSelectedItem(OVData.getUOMFromItemSite(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString()));
            ddpart.setForeground(Color.blue);
            custnumber.setForeground(Color.blue);
            custnumber.setEditable(false);
            tbdesc.setForeground(Color.blue);
            tbdesc.setEditable(false);
            
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
                bsmf.MainFrame.show("SQL Code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void setPrice() {
        listprice.setText("0.00");
        netprice.setText("0.00");
        discount.setText("0");
        String[] TypeAndPrice = new String[]{"","0"};
        if (dduom.getItemCount() > 0 && ddpart.getItemCount() > 0 && ddcust.getItemCount() > 0) {
                DecimalFormat df = new DecimalFormat("#0.0000", new DecimalFormatSymbols(Locale.US));
                
                TypeAndPrice = OVData.getItemPrice("c", ddcust.getSelectedItem().toString(), ddpart.getSelectedItem().toString(), 
                        dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString());
                String pricetype = TypeAndPrice[0].toString();
                Double price = Double.valueOf(TypeAndPrice[1]);
              //  
                listprice.setText(df.format(price));
                if (pricetype.equals("cust")) {
                    listprice.setBackground(Color.green);
                }
                if (pricetype.equals("item")) {
                    listprice.setBackground(Color.white);
                }
                discount.setText(df.format(OVData.getPartDiscFromCust(ddcust.getSelectedItem().toString())));
                // custnumber.setText(OVData.getCustPartFromPart(ddcust.getSelectedItem().toString(), ddpart.getSelectedItem().toString()));  
                setNetPrice();
        }
    }
    
    public void setNetPrice() {
        Double disc = 0.00;
        Double list = 0.00;
        Double net = 0.00;
        
        
        if (discount.getText().isEmpty() || Double.parseDouble(discount.getText().toString()) == 0) {
            netprice.setText(listprice.getText());
        } else {
           
           if (listprice.getText().isEmpty() || Double.parseDouble(listprice.getText().toString()) == 0) {
             listprice.setText("0");
             netprice.setText("0");
           } else {               
           disc = Double.parseDouble(discount.getText().toString());
           list = Double.parseDouble(listprice.getText().toString());
            
           net = list - ((disc / 100) * list);
           netprice.setText(df.format(net));
           }
        }
    }
     
    public void enableAllButAction() {
        //tbhdrwh.setEnabled(true);
        //cbissourced.setEnabled(true);
        lblIsSourced.setEnabled(true);
        lbqtyavailable.setEnabled(true);
        lbqtyavailable.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        tbkey.setEnabled(true);
        ddcust.setEnabled(true);
        ddship.setEnabled(true);
        ddshipvia.setEnabled(true);
        ddtax.setEnabled(true);
        duedate.setEnabled(true);
        orddate.setEnabled(true);
        ponbr.setEnabled(true);
        ddsite.setEnabled(true);
        ddwh.setEnabled(true);
        ddloc.setEnabled(true);
        ddstatus.setEnabled(true);
        cbblanket.setEnabled(true);
        remarks.setEnabled(true);
        ddpart.setEnabled(true);
        dduom.setEnabled(true);
        custnumber.setEnabled(true);
        tbdesc.setEnabled(true);
        qtyshipped.setEnabled(true);
        listprice.setEnabled(true);
       // netprice.setEnabled(true);   // leave disabled
        discount.setEnabled(true);
        tbtottax.setEnabled(true);
        tbmisc.setEnabled(true);
        orddet.setEnabled(true);
       // ddcurr.setEnabled(true);  leave disabled
        /*
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
        */
        
        
         ddsactype.setEnabled(true);
        tbsacdesc.setEnabled(true);
        tbsacamt.setEnabled(true);
        btsacadd.setEnabled(false);
        btsacdelete.setEnabled(false);
        
        
        totlines.setEnabled(true);
        tbtotqty.setEnabled(true);
        tbtotdollars.setEnabled(true);
        
        btordnbrbrowse.setEnabled(false);
        btordduebrowse.setEnabled(false);
        btordpobrowse.setEnabled(false);
        btordcustbrowse.setEnabled(false);
        btorddatebrowse.setEnabled(false);
        
        btnew.setEnabled(true);
        btupdate.setEnabled(false);
        btprintorder.setEnabled(true);
        btinvoice.setEnabled(false);
        btadd.setEnabled(false);
        btadditem.setEnabled(false);
        btdelitem.setEnabled(false);
        btdelete.setEnabled(false);
    }
     
    public void sumlinecount() {
         totlines.setText(String.valueOf(orddet.getRowCount()));
    }
            
    public void sumqty() {
        int qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + Integer.valueOf(orddet.getValueAt(j, 5).toString()); 
         }
         tbtotqty.setText(String.valueOf(qty));
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
        DecimalFormat df = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.US));
        double dol = 0;
        double summaryTaxPercent = 0;
        double headertax = 0;
        double matltax = 0;
        double totaltax = 0;
        
         for (int j = 0; j < orddet.getRowCount(); j++) {
             dol = dol + ( Double.valueOf(orddet.getValueAt(j, 5).toString()) * Double.valueOf(orddet.getValueAt(j, 9).toString()) ); 
             matltax += Double.valueOf(orddet.getValueAt(j, 15).toString()); // now get material tax at the line level
         }
         
         // now lets get summary tax
         // now add trailer/summary charges if any
         for (int j = 0; j < sactable.getRowCount(); j++) {
            if (sactable.getValueAt(j,0).toString().equals("charge")) {
            dol += Double.valueOf(sactable.getValueAt(j,3).toString());  // add charges to total net charge
            }
            if (sactable.getValueAt(j,0).toString().equals("tax")) {
            summaryTaxPercent += Double.valueOf(sactable.getValueAt(j,3).toString());
            }
        }
         
         if (summaryTaxPercent > 0) {
              headertax = (dol * (summaryTaxPercent / 100) );
         }
         totaltax = headertax + matltax;  // combine header tax and matl tax
         
         
         // add tax to total
         dol += totaltax;
         
         /*
         if (! tbtottax.getText().isEmpty()) {
         dol += Double.valueOf(tbtottax.getText()); // add total tax from summation of matl tax + customer specific order tax
         }
         */
         tbtottax.setText(df.format(totaltax));
         tbtotdollars.setText(df.format(dol));
         lblcurr.setText(ddcurr.getSelectedItem().toString());
    }
      
    public void refreshDisplayTotals() {
        sumqty();
        sumdollars();
        sumlinecount();
    }
    
    public void retotal() {
        
        DecimalFormat df = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.US));
        double dol = 0;
        double newdisc = 0;
        double newprice = 0;
        double newtax = 0;
        double listprice = 0;
        
        
         //"Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc"
        for (int j = 0; j < sactable.getRowCount(); j++) {
            if (sactable.getValueAt(j,0).toString().equals("discount")) {
            newdisc += Double.valueOf(sactable.getValueAt(j,3).toString());
            
            }
        }
        
       
        
         for (int j = 0; j < orddet.getRowCount(); j++) {
             listprice = Double.valueOf(orddet.getValueAt(j, 7).toString());
             orddet.setValueAt(BlueSeerUtils.priceformat(String.valueOf(newdisc)), j, 8);
             if (newdisc > 0) {
             newprice = listprice - (listprice * (newdisc / 100));
             } else {
             newprice = listprice;    
             }
             orddet.setValueAt(BlueSeerUtils.priceformat(String.valueOf(newprice)), j, 9);
              
             
         }
                
         
    }
    
    public boolean validateDetail() {
        boolean canproceed = true;
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(listprice.getText());
        if (!m.find() || listprice.getText() == null) {
            bsmf.MainFrame.show("Invalid List Price format");
            canproceed = false;
        }
        
        p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        if (! discount.getText().isEmpty()) {
            m = p.matcher(discount.getText());
            if (!m.find()) {
                bsmf.MainFrame.show("Invalid Discount format");
                canproceed = false;
            }
        }
        
        p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        m = p.matcher(netprice.getText());
        if (!m.find() || netprice.getText() == null) {
            bsmf.MainFrame.show("Invalid Net Price format");
            canproceed = false;
        }
        
        p = Pattern.compile("^[1-9]\\d*$");
        m = p.matcher(qtyshipped.getText());
        if (!m.find() || qtyshipped.getText() == null) {
            bsmf.MainFrame.show("Invalid Qty");
            canproceed = false;
        }
        
         
        
        // check unallocated qty
        if (! OVData.isOrderExceedQOHU() && Integer.valueOf(qtyshipped.getText()) > OVData.getItemQOHUnallocated(ddpart.getSelectedItem().toString(),ddsite.getSelectedItem().toString(),tbkey.getText())) {
             bsmf.MainFrame.show("Quantity exceeds QOH Unallocated");
            canproceed = false;
        }
        
        if (OVData.isValidItem(ddpart.getSelectedItem().toString()) && ! OVData.isValidUOMConversion(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString())) {
                bsmf.MainFrame.show("no base uom conversion");
                canproceed = false;
                dduom.requestFocus();
                
        }
        if (OVData.isValidItem(ddpart.getSelectedItem().toString()) && ! OVData.isBaseUOMOfItem(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString()) && ! OVData.isValidCustPriceRecordExists(ddcust.getSelectedItem().toString(),ddpart.getSelectedItem().toString(),dduom.getSelectedItem().toString(),ddcurr.getSelectedItem().toString())) {
                bsmf.MainFrame.show("no price record for conversion uom");
                canproceed = false;
                dduom.requestFocus();
                
        }
      return canproceed;   
    }
    
    public static void lookUpFrameCustItem() {
        if (frame != null) {
            frame.dispose();
        }
        if (lookUpModel != null && lookUpModel.getRowCount() > 0) {
        lookUpModel.setRowCount(0);
        }
        //MouseListener[] mllist = lookUpTable.getMouseListeners();
       // for (MouseListener ml : mllist) {  
       //     System.out.println(ml.toString());
            // lookUpTable.removeMouseListener(ml);
       // }
       lookUpTable.removeMouseListener(mllu);
        final JTextField input = new JTextField(20);
        
        input.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        lookUpModel = DTData.getCustXrefBrowseUtil(input.getText(), 0, "cup_citem");
        lookUpTable.setModel(lookUpModel);
        lookUpTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (lookUpModel.getRowCount() < 1) {
            frame.setTitle("No Records Found!");
        } else {
            frame.setTitle(lookUpModel.getRowCount() + " Records Found!");
        }
        }
        });
        
       
        lookUpTable.setPreferredScrollableViewportSize(new Dimension(400,100));
        JScrollPane scrollPane = new JScrollPane(lookUpTable);
        mllu = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            JTable target = (JTable)e.getSource();
            int row = target.getSelectedRow();
            int column = target.getSelectedColumn();
            if ( column == 0) {
            ddpart.setSelectedItem(target.getValueAt(row,3).toString());
            frame.dispose();
            }
        }
        };
       lookUpTable.addMouseListener(mllu);
        frame = new JFrame("Enter Search Text:");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(input, BorderLayout.NORTH);
        frame.add( scrollPane );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }

    public static void lookUpFrameItemDesc() {
        if (frame != null) {
            frame.dispose();
        }
        if (lookUpModel != null && lookUpModel.getRowCount() > 0) {
        lookUpModel.setRowCount(0);
        }
        // MouseListener[] mllist = lookUpTable.getMouseListeners();
       // for (MouseListener ml : mllist) {
        //    System.out.println(ml.toString());
            //lookUpTable.removeMouseListener(ml);
       // }
       lookUpTable.removeMouseListener(mllu);
        final JTextField input = new JTextField(20);
        input.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        lookUpModel = DTData.getItemDescBrowse(input.getText(), "it_desc");
        lookUpTable.setModel(lookUpModel);
        lookUpTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (lookUpModel.getRowCount() < 1) {
            frame.setTitle("No Records Found!");
        } else {
            frame.setTitle(lookUpModel.getRowCount() + " Records Found!");
        }
        }
        });
        
       
        lookUpTable.setPreferredScrollableViewportSize(new Dimension(400,100));
        JScrollPane scrollPane = new JScrollPane(lookUpTable);
        mllu = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ddpart.setSelectedItem(target.getValueAt(row,1).toString());
                frame.dispose();
                }
            }
        };
        lookUpTable.addMouseListener(mllu);
      
        frame = new JFrame("Enter Search Text:");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(input, BorderLayout.NORTH);
        frame.add( scrollPane );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
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
        btordduebrowse = new javax.swing.JButton();
        btorddatebrowse = new javax.swing.JButton();
        btordpobrowse = new javax.swing.JButton();
        ddtax = new javax.swing.JComboBox<>();
        jLabel93 = new javax.swing.JLabel();
        tbhdrwh = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        lblIsSourced = new javax.swing.JLabel();
        cbissourced = new javax.swing.JCheckBox();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel98 = new javax.swing.JLabel();
        cbisallocated = new javax.swing.JCheckBox();
        btdelete = new javax.swing.JButton();
        btordnbrbrowse = new javax.swing.JButton();
        btordcustbrowse = new javax.swing.JButton();
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
        btinvoice = new javax.swing.JButton();
        btprintorder = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btprintinvoice = new javax.swing.JButton();
        btprintps = new javax.swing.JButton();
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
        btupdateitem = new javax.swing.JButton();
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
        jPanelSched.setPreferredSize(new java.awt.Dimension(821, 627));

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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE))
        );
        jPanelSchedLayout.setVerticalGroup(
            jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSchedLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
        );

        add(jPanelSched);

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Order Maintenance"));

        jLabel76.setText("Key");

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel82.setText("bill-to");

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
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

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel81.setText("Due Date");

        jLabel90.setText("ShipVia");

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "cancel", "error", "hold", "open", "backorder", "close", "" }));

        jLabel83.setText("PO Number");

        jLabel85.setText("Status");

        duedate.setDateFormatString("yyyy-MM-dd");

        jLabel86.setText("Remarks");

        orddate.setDateFormatString("yyyy-MM-dd");

        jLabel87.setText("Ord Date");

        cbblanket.setText("Blanket?");

        jLabel92.setText("Site:");

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        btordduebrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btordduebrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btordduebrowseActionPerformed(evt);
            }
        });

        btorddatebrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btorddatebrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btorddatebrowseActionPerformed(evt);
            }
        });

        btordpobrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btordpobrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btordpobrowseActionPerformed(evt);
            }
        });

        ddtax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtaxActionPerformed(evt);
            }
        });

        jLabel93.setText("Tax Code");

        jLabel97.setText("WH");

        lblIsSourced.setText("   ");

        cbissourced.setText("is Sourced?");

        jLabel98.setText("Currency");

        cbisallocated.setText("Allocation?");

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
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbblanket)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(ddshipvia, 0, 162, Short.MAX_VALUE)
                                            .addComponent(ponbr))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btordpobrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel93, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(orddate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(duedate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btordduebrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btorddatebrowse, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(cbissourced))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbblanket)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ponbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel83))
                                    .addComponent(btordpobrowse))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel90)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel92)
                                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel85)))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btorddatebrowse)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel81)
                                        .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btordduebrowse, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel87)
                                        .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                            .addComponent(cbissourced))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addContainerGap())
        );

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btordnbrbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btordnbrbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btordnbrbrowseActionPerformed(evt);
            }
        });

        btordcustbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btordcustbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btordcustbrowseActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setText("Zip");

        jLabel99.setText("City");

        jLabel100.setText("Misc");

        jLabel101.setText("Name");

        jLabel102.setText("Addr2");

        jLabel104.setText("Phone");

        jLabel106.setText("Email");

        jLabel77.setText("Code");

        jLabel109.setText("Contact");

        jLabel110.setText("Addr1");

        jLabel111.setText("State");

        btaddshipto.setText("Add ShipTo");
        btaddshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddshiptoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel77)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(24, 24, 24)
                                    .addComponent(jLabel101))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(23, 23, 23)
                                    .addComponent(jLabel110))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(23, 23, 23)
                                    .addComponent(jLabel102))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(36, 36, 36)
                                    .addComponent(jLabel99))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(27, 27, 27)
                                    .addComponent(jLabel111))))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbaddr1)
                            .addComponent(tbaddr2)
                            .addComponent(tbcity, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbname, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addComponent(tbshiptocode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel104)
                            .addComponent(jLabel106)
                            .addComponent(jLabel109)
                            .addComponent(jLabel100))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbmisc1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btaddshipto)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(tbshiptocode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel101))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel106)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jLabel110))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel111))
                            .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
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
                            .addComponent(jLabel100))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btaddshipto)
                .addContainerGap())
        );

        btinvoice.setText("Invoice");
        btinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btinvoiceActionPerformed(evt);
            }
        });

        btprintorder.setText("Print Order");
        btprintorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintorderActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btprintinvoice.setText("Print Invoice");
        btprintinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintinvoiceActionPerformed(evt);
            }
        });

        btprintps.setText("Print PackingSlip");
        btprintps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintpsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                .addComponent(btordcustbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblcustname, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblshiptoaddr, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(41, 41, 41)
                                .addComponent(lblshiptoname, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(btordnbrbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addGap(27, 27, 27)
                                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(btinvoice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btprintinvoice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btprintps)
                                .addGap(247, 247, 247)
                                .addComponent(btprintorder)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd)))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(lblshiptoname, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel76))
                            .addComponent(btordnbrbrowse)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnew)
                                .addComponent(btclear)))
                        .addGap(10, 10, 10)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblcustname, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btordcustbrowse)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel82))))
                        .addGap(6, 6, 6)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel91))
                            .addComponent(lblshiptoaddr, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btinvoice)
                    .addComponent(btprintorder)
                    .addComponent(btprintinvoice)
                    .addComponent(btprintps))
                .addGap(13, 13, 13))
        );

        add(jPanelMain);

        jPanelLines.setBorder(javax.swing.BorderFactory.createTitledBorder("Lines"));
        jPanelLines.setPreferredSize(new java.awt.Dimension(821, 627));

        btadditem.setText("Insert");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdelitem.setText("Remove");
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        jLabel84.setText("Qty Ship");

        lblCustItemAndDesc.setText("CustNumber");

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

        jLabel5.setText("uom");

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel9.setText("Description");

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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblpart1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCustItemAndDesc, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(custnumber)
                            .addComponent(tbdesc)
                            .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel5))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(btLookUpCustItem, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dduom, 0, 65, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(lbqtyavailable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(lblpart1)
                    .addComponent(jLabel5)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel84))
                    .addComponent(lbqtyavailable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jLabel89.setText("NetPrice");

        jLabel80.setText("ListPrice");

        discount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                discountFocusLost(evt);
            }
        });

        jLabel88.setText("Disc%");

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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel88)
                            .addComponent(jLabel89))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(discount, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(netprice)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel80)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(listprice, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addGap(34, 34, 34)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel88)
                            .addComponent(discount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(netprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addContainerGap())
        );

        jLabel94.setText("Misc");

        jLabel95.setText("Plant/WH");

        jLabel96.setText("Location");

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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel96, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel94, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel95, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddwh, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbmisc, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    .addComponent(ddloc, 0, 94, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel95)
                        .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel96)
                            .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel94))
                .addContainerGap())
        );

        btupdateitem.setText("Update");
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
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(541, 541, 541)
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdateitem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelitem)))
                .addGap(0, 37, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btadditem)
                    .addComponent(btupdateitem)))
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

        sactable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(sactable);

        percentlabel.setText("Percent/Amount");

        jLabel8.setText("Desc");

        btsacadd.setText("add");
        btsacadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacaddActionPerformed(evt);
            }
        });

        btsacdelete.setText("delete");
        btsacdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacdeleteActionPerformed(evt);
            }
        });

        ddsactype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "discount", "charge" }));
        ddsactype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsactypeActionPerformed(evt);
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
                    .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jLabel3.setText("Total Lines:");

        totlines.setEditable(false);

        jLabel1.setText("Total Qty:");

        tbtotqty.setEditable(false);

        jLabel4.setText("Total Tax:");

        tbtottax.setEditable(false);

        jLabel2.setText("Total $");

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
                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtottax, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
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
                .addGap(0, 23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelLinesLayout = new javax.swing.GroupLayout(jPanelLines);
        jPanelLines.setLayout(jPanelLinesLayout);
        jPanelLinesLayout.setHorizontalGroup(
            jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelLinesLayout.setVerticalGroup(
            jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinesLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        add(jPanelLines);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      newAction("order");
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        
        int line = 0;
        
        line = getmaxline();
        line++;
        
        boolean canproceed = validateDetail();
        
        //    "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice"
        if (canproceed) {
            myorddetmodel.addRow(new Object[]{line, ddpart.getSelectedItem().toString(), custnumber.getText(), tbkey.getText(), ponbr.getText(), 
                qtyshipped.getText(), dduom.getSelectedItem().toString(), listprice.getText(), 
                discount.getText(), netprice.getText(), 
                "0", "open",
                ddwh.getSelectedItem().toString(), ddloc.getSelectedItem().toString(), tbdesc.getText(), 
                String.valueOf(OVData.getTaxAmtApplicableByItem(ddpart.getSelectedItem().toString(), (Double.valueOf(netprice.getText()) * Double.valueOf(qtyshipped.getText())) ))
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
        if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btaddActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed

        if (ddcust.getItemCount() > 0) {
           custChangeEvent(ddcust.getSelectedItem().toString());
           
           
        } // if ddcust has a list
        
    }//GEN-LAST:event_ddcustActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals("close") || orddet.getValueAt(i, 11).toString().equals("partial")) {
                bsmf.MainFrame.show("Cannot Delete Closed or Partial Item");
                return;
            } else {
            bsmf.MainFrame.show("Removing row " + i);
            linetax.remove(orddet.getValueAt(i, 0));
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
        
        if (getmaxline() > 0) {
            btadd.setEnabled(true);
        } else {
             btadd.setEnabled(false);
        }
       
         refreshDisplayTotals();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
          if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});
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

                         Class.forName(bsmf.MainFrame.driver).newInstance();
                        bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);

                        try {
                            Statement st = bsmf.MainFrame.con.createStatement();
                            ResultSet res = null;

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
                            }

                        } catch (SQLException s) {
                            MainFrame.bslog(s);
                        }
                        bsmf.MainFrame.con.close();
                    } catch (Exception e) {
                        MainFrame.bslog(e);
                    }
              disableShipAddress();
              }
            
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
        getparts(ddpart.getSelectedItem().toString());
    }//GEN-LAST:event_ddpartActionPerformed

    private void qtyshippedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusGained
        if (qtyshipped.getText().equals("0")) {
            qtyshipped.setText("");
        }
    }//GEN-LAST:event_qtyshippedFocusGained

    private void qtyshippedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusLost
       
          String x = BlueSeerUtils.bsformat("", qtyshipped.getText(), "0");
        if (x.equals("error")) {
            qtyshipped.setText("");
            qtyshipped.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            qtyshipped.requestFocus();
        } else {
            qtyshipped.setText(x);
            qtyshipped.setBackground(Color.white);
        }
        
       
        if (! qtyshipped.getText().isEmpty()) {
               if (Double.valueOf(qtyshipped.getText()) > OVData.getItemQtyByWarehouseAndLocation(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), ddwh.getSelectedItem().toString(), ddloc.getSelectedItem().toString())) {
                   lbqtyavailable.setBackground(Color.red);
               } else {
                   lbqtyavailable.setBackground(Color.green);
               }
           }
    }//GEN-LAST:event_qtyshippedFocusLost

    private void listpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusLost
        String x = BlueSeerUtils.bsformat("", listprice.getText(), "4");
        if (x.equals("error")) {
            listprice.setText("");
            listprice.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            listprice.requestFocus();
        } else {
            listprice.setText(x);
            listprice.setBackground(Color.white);
        }
        setNetPrice();
    }//GEN-LAST:event_listpriceFocusLost

    private void discountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_discountFocusLost
           String x = BlueSeerUtils.bsformat("", discount.getText(), "4");
        if (x.equals("error")) {
            discount.setText("");
            discount.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            discount.requestFocus();
        } else {
            discount.setText(x);
            discount.setBackground(Color.white);
        }
        if (discount.getText().isEmpty())
            discount.setText("0.00");
        setNetPrice();
    }//GEN-LAST:event_discountFocusLost

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       executeTask("delete", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btordnbrbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btordnbrbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"ordermaint","so_nbr"});
    }//GEN-LAST:event_btordnbrbrowseActionPerformed

    private void btordcustbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btordcustbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"ordermaint","so_cust"});
    }//GEN-LAST:event_btordcustbrowseActionPerformed

    private void btordduebrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btordduebrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"ordermaint","so_due_date"});
    }//GEN-LAST:event_btordduebrowseActionPerformed

    private void btorddatebrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btorddatebrowseActionPerformed
       reinitpanels("BrowseUtil", true, new String[]{"ordermaint","so_ord_date"});
    }//GEN-LAST:event_btorddatebrowseActionPerformed

    private void btordpobrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btordpobrowseActionPerformed
       reinitpanels("BrowseUtil", true, new String[]{"ordermaint","so_po"});
    }//GEN-LAST:event_btordpobrowseActionPerformed

    private void tbmiscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbmiscActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbmiscActionPerformed

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
        if (ddwh.getSelectedItem() != null) {
             ddloc.removeAllItems();
             ddloc.insertItemAt("", 0);
             ddloc.setSelectedIndex(0);
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
            setPrice();
    }//GEN-LAST:event_dduomActionPerformed

    private void ddlocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddlocActionPerformed
       if (ddwh.getSelectedItem() != null && ddloc.getSelectedItem() != null && ddpart.getSelectedItem() != null && ! isLoad) {
           
           double qty = 0.0;
           String prefix = "";
           if (cbisallocated.isSelected()) {
               prefix = "QOH Unallocated=";
           qty = OVData.getItemQOHUnallocated(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), tbkey.getText());
           
           } else {
            prefix = "QOH Available=";
           qty = OVData.getItemQtyByWarehouseAndLocation(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), ddwh.getSelectedItem().toString(), ddloc.getSelectedItem().toString());
          
           }
           String sqty = String.valueOf(qty);
           lbqtyavailable.setText(prefix + sqty);
           if (! qtyshipped.getText().isEmpty()) {
               if (Double.valueOf(qtyshipped.getText()) > qty || qty == 0 ) {
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
            bsmf.MainFrame.show("Must enter a legitimate value in the code field");
            return;
        }
        if (tbshiptocode.getText().length() > 10) {
            bsmf.MainFrame.show("ship to code cannot be larger than 10 chars");
            return;
        }
        if (tbzip.getText().length() > 10) {
            bsmf.MainFrame.show("zip code cannot be larger than 10 chars");
            return;
        }
        if (tbname.getText().length() > 50) {
            bsmf.MainFrame.show("ship to name cannot be larger than 10 chars");
            return;
        }
        
        if (OVData.isValidCustShipTo(ddcust.getSelectedItem().toString(), tbshiptocode.getText())) {
            bsmf.MainFrame.show("Shipto Code already used...choose another");
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
                   "" + ":" + 
                   "" + ":" + 
                   tbcontact.getText().replace("'", "") + ":" + 
                   tbphone.getText().replace("'", "") + ":" + 
                   tbemail.getText().replace("'", "") + ":" + 
                   tbmisc1.getText().replace("'", "");
            
            st.add(list);
           
           error = OVData.addCustShipToMstr(st);
           if (! error) {
            bsmf.MainFrame.show("Added shipto code");
            reinitCustandShip(ddcust.getSelectedItem().toString(), tbshiptocode.getText()); 
           }
        }
    }//GEN-LAST:event_btaddshiptoActionPerformed

    private void btsacdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacdeleteActionPerformed
         int[] rows = sactable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) sactable.getModel()).removeRow(i);
        }
       
        retotal();
        refreshDisplayTotals();
         
    }//GEN-LAST:event_btsacdeleteActionPerformed

    private void btsacaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacaddActionPerformed
        boolean proceed = true;
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbsacamt.getText());
        if (!m.find() || tbsacamt.getText() == null) {
            bsmf.MainFrame.show("Invalid amount format");
            proceed = false;
            tbsacamt.requestFocus();
            return;
        }
        
        if (tbsacdesc.getText().isEmpty()) {
            bsmf.MainFrame.show("Description cannot be blank");
            proceed = false;
            tbsacdesc.requestFocus();
            return;
        }
        
        if (proceed)
        sacmodel.addRow(new Object[]{ ddsactype.getSelectedItem().toString(), tbsacdesc.getText(), percentlabel.getText(), tbsacamt.getText()});
        retotal();
        refreshDisplayTotals();
    }//GEN-LAST:event_btsacaddActionPerformed

    private void ddsactypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsactypeActionPerformed
        if (ddsactype.getSelectedItem().toString().equals("discount")) {
            percentlabel.setText("percent");
        } else {
            percentlabel.setText("amount");
        }
    }//GEN-LAST:event_ddsactypeActionPerformed

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
        String[] message = autoInvoiceOrder();
         if (message[0].equals("1")) { // if error
           bsmf.MainFrame.show(message[1]);
         } else {
           executeTask("get", new String[]{tbkey.getText()});
         }
    }//GEN-LAST:event_btinvoiceActionPerformed

    private void btprintorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintorderActionPerformed
        OVData.printCustomerOrder(tbkey.getText());
    }//GEN-LAST:event_btprintorderActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
       executeTask("get", new String[]{tbkey.getText()});
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
            ArrayList<String> items = OVData.getItemMasterListBySite(ddsite.getSelectedItem().toString()); 
            for (String item : items) {
            ddpart.addItem(item);
            }  
            }
    }//GEN-LAST:event_ddsiteActionPerformed

    private void btupdateitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateitemActionPerformed
        int line = 0;
        
        line = getmaxline();
        line++;
        
        int[] rows = orddet.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show("Only 1 row must be selected");
                return;
        }
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals("close") || orddet.getValueAt(i, 11).toString().equals("partial")) {
                bsmf.MainFrame.show("Cannot Update Closed or Partial Item");
                return;
            } else if (! orddet.getValueAt(i, 1).toString().equals(ddpart.getSelectedItem().toString())) {
                bsmf.MainFrame.show("The item field value cannot be different for this table record");
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpCustItem;
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btaddshipto;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btinvoice;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btordcustbrowse;
    private javax.swing.JButton btorddatebrowse;
    private javax.swing.JButton btordduebrowse;
    private javax.swing.JButton btordnbrbrowse;
    private javax.swing.JButton btordpobrowse;
    private javax.swing.JButton btprintinvoice;
    private javax.swing.JButton btprintorder;
    private javax.swing.JButton btprintps;
    private javax.swing.JButton btsacadd;
    private javax.swing.JButton btsacdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdateitem;
    private javax.swing.JCheckBox cbblanket;
    private javax.swing.JCheckBox cbisallocated;
    private javax.swing.JCheckBox cbissourced;
    private javax.swing.JTextField custnumber;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox<String> ddloc;
    private static javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox<String> ddsactype;
    private javax.swing.JComboBox ddship;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> ddtax;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JTextField discount;
    private com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
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
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblCustItemAndDesc;
    private javax.swing.JLabel lblIsSourced;
    private javax.swing.JLabel lblcurr;
    private javax.swing.JLabel lblcustname;
    private javax.swing.JLabel lblpart1;
    private javax.swing.JLabel lblshiptoaddr;
    private javax.swing.JLabel lblshiptoname;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lbqtyavailable;
    private javax.swing.JTextField listprice;
    private javax.swing.JTextField netprice;
    private com.toedter.calendar.JDateChooser orddate;
    private javax.swing.JTable orddet;
    private javax.swing.JLabel percentlabel;
    private javax.swing.JTextField ponbr;
    private javax.swing.JTextField qtyshipped;
    private javax.swing.JTextField remarks;
    private javax.swing.JTable sactable;
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
