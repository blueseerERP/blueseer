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
package com.blueseer.pur;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import com.blueseer.utl.IBlueSeer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


/**
 *
 * @author vaughnte
 */
public class POMaintPanel extends javax.swing.JPanel implements IBlueSeer {

    // global variable declarations
     boolean editmode = false;
     boolean isLoad = false;
     String curr = "";
     String basecurr = OVData.getDefaultCurrency();
     String terms = "";
     String acct = "";
     String cc = "";
     String blanket = "";
     String status = "";
      boolean venditemonly = true;  
     DecimalFormat df = new DecimalFormat("#0.0000", new DecimalFormatSymbols(Locale.US));
     
    public static javax.swing.table.DefaultTableModel lookUpModel = null;
    public static JTable lookUpTable = new JTable();
    public static MouseListener mllu = null;
    public static JDialog dialog = new JDialog();
    
    public static ButtonGroup bg = null;
    public static JRadioButton rb1 = null;
    public static JRadioButton rb2 = null;
     
     // global datatablemodel declarations  
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "line", "Part", "Desc" ,"VendPart", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
            })
    {
        boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
               canEdit = new boolean[]{false, false, false, false, true, false, true, false, false, false, false}; 
            return canEdit[columnIndex];
        }
    };
    javax.swing.table.DefaultTableModel modelsched = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Date", "Ref", "Qty", "Type"
            });
    
     javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 4 || tme.getColumn() == 6 )) {
                            retotal();
                            refreshDisplayTotals();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };  
      
    
    public POMaintPanel() {
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
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Lines", panelDetail);
        jTabbedPane1.add("Schedule", panelSchedule);
        
        
        jTabbedPane1.setEnabledAt(2, false);
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(0, true);
        
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       
        tbkey.setText("");
        tbkey.setEditable(true);
        tbkey.setForeground(Color.black);
        
        listprice.setText("0.00");
        netprice.setText("0.00");
        netprice.setEditable(false);
        qtyshipped.setText("0");
        discount.setText("0.00");
        
        lbvend.setText("");
       
        userid.setText(bsmf.MainFrame.userid);
        duedate.setDate(now);
        orddate.setDate(now);
        
        orddate.setEnabled(false);
       
        
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
        myorddetmodel.addTableModelListener(ml);
        
        modelsched.setRowCount(0);
        tablesched.setModel(modelsched);
        
        lblcurr.setText("");
        remarks.setText("");
        tbtotqty.setText("");
        tbtotdollars.setText("");
        totlines.setText("");
        vendnumber.setText("");
        cbblanket.setEnabled(true);
        
        
        
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddshipvia.removeAllItems();
        mylist = OVData.getScacCarrierOnly();   
        for (int i = 0; i < mylist.size(); i++) {
            ddshipvia.addItem(mylist.get(i));
        }
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        
        
        ddcurr.removeAllItems();
         ddcurr.insertItemAt("", 0);
         ddcurr.setSelectedIndex(0);
        mylist = OVData.getCurrlist();
        for (String code : mylist) {
            ddcurr.addItem(code);
        }
        
        
         venditemonly = OVData.isVendItemOnly();
        if (! venditemonly) {
            ddpart.removeAllItems();
            ArrayList<String> items = OVData.getItemMasterAlllist();
            for (String item : items) {
            ddpart.addItem(item);
            }  
        }
        
        
        
        dduom.removeAllItems();
        mylist = OVData.getUOMList();
        for (String code : mylist) {
            dduom.addItem(code);
        }
        
         ddpart.setForeground(Color.black);
        vendnumber.setForeground(Color.black);
        vendnumber.setEditable(false);
        
        
        ddvend.removeAllItems();
          ArrayList myvends = OVData.getvendmstrlist();
          for (int i = 0; i < myvends.size(); i++) {
            ddvend.addItem(myvends.get(i));
          }
          ddvend.insertItemAt("", 0);
          ddvend.setSelectedIndex(0);
        isLoad = false;
        
        
    }
    
    public void initvars(String[] arg) {
       
       
       setPanelComponentState(panelMain, false); 
       setPanelComponentState(panelDetail, false); 
       setPanelComponentState(panelSchedule, false); 
       setPanelComponentState(this, false); 
       
       
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask("get", arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
          
    }
  
    public void newAction(String x) {
        setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btpoprint.setEnabled(false);
        btnew.setEnabled(false);
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
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
        return m;
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
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
               
                
               
                    st.executeUpdate("insert into po_mstr "
                        + "(po_nbr, po_vend, po_site, po_type, po_curr, po_buyer, po_due_date, "
                        + " po_ord_date, po_userid, po_status,"
                        + " po_terms, po_ap_acct, po_ap_cc, po_rmks ) "
                        + " values ( " + "'" + tbkey.getText().toString() + "'" + ","
                        + "'" + ddvend.getSelectedItem().toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbblanket.isSelected()) + "'" + ","
                        + "'" + curr + "'" + ","
                        + "'" + tbbuyer.getText() + "'" + ","        
                        + "'" + dfdate.format(duedate.getDate()).toString() + "'" + ","
                        + "'" + dfdate.format(orddate.getDate()).toString() + "'" + ","
                        + "'" + userid.getText() + "'" + ","
                        + "'" + ddstatus.getSelectedItem() + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + remarks.getText() + "'"
                        + ")"
                        + ";");

                  
                  //"line", "Part", "Desc" ,"VendPart", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
            
                    for (int j = 0; j < orddet.getRowCount(); j++) {
                        st.executeUpdate("insert into pod_mstr "
                            + "(pod_line, pod_part, pod_vendpart, pod_nbr, pod_ord_qty, pod_uom, pod_listprice, pod_disc, pod_netprice, pod_due_date, "
                            + "pod_rcvd_qty, pod_status, pod_site) "
                            + " values ( " 
                            + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + dfdate.format(duedate.getDate()).toString() + "'" + ","
                            + '0' + "," 
                            + "''" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");

                    }
                   m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                   
               
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
      
    public String[] deleteRecord(String[] x) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
                   try {

                    Class.forName(bsmf.MainFrame.driver).newInstance();
                    bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
                    try {
                        Statement st = bsmf.MainFrame.con.createStatement();
                        ResultSet res = null;
 
                        // if this PO has 'any' line items already received then bale...cannot delete
                        res = st.executeQuery("select pod_nbr from pod_mstr where pod_nbr = " + "'" + x[0] + "'" + 
                                              " and pod_rcvd_qty > 0 " + ";");
                        int z = 0;
                        while (res.next()) {
                            z++;
                        }
                        if (z > 0) {
                           return m = new String[] {"1","cannot delete PO...some lines already received"};
                        }
                        
                        
                        
                        st.executeUpdate("delete from pod_mstr where pod_nbr = " + "'" + tbkey.getText() + "'" + ";");   
                        int i = st.executeUpdate("delete from po_mstr where po_nbr = " + "'" + tbkey.getText() + "'" + ";");
                            if (i > 0) {
                                m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                            }
                        } catch (SQLException s) {
                            MainFrame.bslog(s);
                        m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError}; 
                    }
                    bsmf.MainFrame.con.close();
                } catch (Exception e) {
                    MainFrame.bslog(e);
                }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled};  
        }
           return m;
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
       try {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
               
               
               
                    st.executeUpdate("update po_mstr "
                       + " set po_status = " + "'" + ddstatus.getSelectedItem().toString() + "'" + ","
                        + " po_rmks = " + "'" + remarks.getText().replace("'", "''") + "'" + "," 
                        + " po_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + ","         
                        + " po_buyer = " + "'" + tbbuyer.getText().replace("'", "''") + "'" + "," 
                        + " po_due_date = " + "'" + dfdate.format(duedate.getDate()).toString() + "'" + "," 
                       + " po_shipvia = " + "'" + ddshipvia.getSelectedItem().toString() + "'"          
                       + " where po_nbr = " + "'" + x[0] + "'"
                        + ";");

                 
                   
                    
                    // if available pod_mstr line item...then update....else insert
                    for (int j = 0; j < orddet.getRowCount(); j++) {
                         i = 0;
                        // skip closed lines
                        if (orddet.getValueAt(j, 11).toString().equals("close"))
                            continue;
                        res = st.executeQuery("Select pod_line from pod_mstr where pod_nbr = " + "'" + x[0] + "'" +
                                " and pod_line = " + "'" + orddet.getValueAt(j, 0).toString() + "'" + ";" );
                            while (res.next()) {
                            i++;
                            }
                            
                             //   "line", "Part", "Desc", "VendPart", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
                            if (i > 0) {
                              st.executeUpdate("update pod_mstr set "
                            + " pod_part = " + "'" + orddet.getValueAt(j, 1).toString().replace("'", "''") + "'" + ","
                            + " pod_vendpart = " + "'" + orddet.getValueAt(j, 3).toString().replace("'", "''") + "'" + ","
                            + " pod_ord_qty = " + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + " pod_uom = " + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","        
                            + " pod_listprice = " + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","
                            + " pod_disc = " + "'" + orddet.getValueAt(j, 8).toString() + "'" + ","
                            + " pod_netprice = " + "'" + orddet.getValueAt(j, 9).toString() + "'" + ","
                            + " pod_due_date = " + "'" + dfdate.format(duedate.getDate()).toString() + "'"  + ","
                            + " pod_status = " + "'" + ddstatus.getSelectedItem().toString() + "'" + ","
                            + " pod_site = " + "'" + ddsite.getSelectedItem().toString() + "'"
                            + " where pod_nbr = " + "'" + x[0] + "'" 
                            + " AND pod_line = " + "'" + orddet.getValueAt(j, 0).toString() + "'"
                            + ";");
                            } else {
                             st.executeUpdate("insert into pod_mstr "
                            + "(pod_line, pod_part, pod_vendpart, pod_nbr, pod_ord_qty, pod_uom, pod_listprice, pod_disc, pod_netprice, pod_ord_date, pod_due_date, "
                            + "pod_rcvd_qty, pod_status, pod_site) "
                            + " values ( " 
                            + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","        
                            + "'" + orddet.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + dfdate.format(orddate.getDate()).toString() + "'" + ","
                            + "'" + dfdate.format(duedate.getDate()).toString() + "'" + ","        
                            + "'" + "0" + "'" + ","
                            + "'" + "" + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");   
                            }

                    }
                    
                   m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};    
                    
                    
                    // btQualProbAdd.setEnabled(false);
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};   
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
            MainFrame.bslog(e);
        }
     return m;
    }
         
    public String[] getRecord(String[] x) {
        String[] m = new String[2];
         myorddetmodel.setRowCount(0);
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                res = st.executeQuery("select * from po_mstr where po_nbr = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                    i++;
                    tbkey.setText(x[0]);
                    ddvend.setSelectedItem(res.getString("po_vend"));
                    ddvend.setEnabled(false);
                    ddstatus.setSelectedItem(res.getString("po_status"));
                    ddcurr.setSelectedItem(res.getString("po_curr"));
                    ddshipvia.setSelectedItem(res.getString("po_shipvia"));
                    remarks.setText(res.getString("po_rmks"));
                    duedate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("po_due_date")));
                    blanket = res.getString("po_type");
                    if (blanket != null && blanket.compareTo("BLANKET") == 0)
                    cbblanket.setSelected(true);
                    else
                    cbblanket.setSelected(false);
                    cbblanket.setEnabled(false);
                }
                
                 // myorddetmodel  "Line", "Part", "CustPart",  "PO", "Qty", UOM", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status"
                res = st.executeQuery("select * from pod_mstr left outer join item_mstr on it_item = pod_part where pod_nbr = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                  myorddetmodel.addRow(new Object[]{res.getString("pod_line"), 
                      res.getString("pod_part"),
                      res.getString("it_desc"), 
                      res.getString("pod_vendpart"), 
                      res.getString("pod_nbr"), 
                      res.getString("pod_ord_qty"), 
                      res.getString("pod_uom"), 
                      res.getString("pod_listprice"),
                      res.getDouble("pod_disc"), 
                      res.getString("pod_netprice"), 
                      res.getString("pod_rcvd_qty"), 
                      res.getString("pod_status")});
                }
               
                 
               // set Action if Record found (i > 0)
                m = setAction(i);
                
                sumqty();
                sumdollars();
                sumlinecount();
               
               

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
    
    public boolean validateInput(String x) {
        boolean proceed = true;
            
            
         if (ddstatus.getSelectedItem().toString().isEmpty()) {
                    status = "open";
                } else {
                    status = ddstatus.getSelectedItem().toString();
                }
                
                if (cbblanket.isSelected())
                    blanket = "BLANKET";
                else
                    blanket = "DISCRETE";
                
                if ( ddvend.getSelectedItem() == null || ddvend.getSelectedItem().toString().isEmpty() ) {
                   bsmf.MainFrame.show("must choose a vendor");
                   proceed = false;
                    ddvend.requestFocus();
                    return false;
                }
               
                if ( duedate.getDate() == null) {
                   bsmf.MainFrame.show("must choose a valid due date");
                   proceed = false;
                    duedate.requestFocus();
                    return false;
                }
                
                terms = OVData.getVendTerms(ddvend.getSelectedItem().toString()); 
                cc = OVData.getVendAPCC(ddvend.getSelectedItem().toString());
                acct = OVData.getVendAPAcct(ddvend.getSelectedItem().toString());
                curr = ddcurr.getSelectedItem().toString();
                
                if (terms == null   || acct == null   || cc == null || curr == null ||
                        terms.isEmpty() || acct.isEmpty() || cc.isEmpty() || curr.isEmpty()
                         ) {
                        bsmf.MainFrame.show("Terms|ARacct|ARcc|Currency is not defined for this customer");
                        proceed = false;
                        return false;
                    }   
                
                if (orddet.getRowCount() == 0) {
                    bsmf.MainFrame.show("No Line Items");
                    proceed = false;
                    return false;
                }
                
                
                 // lets check for foreign currency with no exchange rate
            if (! curr.toUpperCase().equals(basecurr.toUpperCase())) {
            if (OVData.getExchangeRate(basecurr, curr).isEmpty()) {
                bsmf.MainFrame.show("Foreign currency has no exchange rate " + curr + "/" + basecurr);
                proceed = false;
                return false;
            }
            }
                
                    
        return proceed;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getPOBrowseUtil(luinput.getText(),0, "po_nbr");
        } else {
         luModel = DTData.getPOBrowseUtil(luinput.getText(),0, "po_vend");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle("No Records Found!");
        } else {
            ludialog.setTitle(luModel.getRowCount() + " Records Found!");
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
      
        callDialog("PONbr", "Vendor"); 
        
        
    }

       
     // additional functions
    public void setPrice() {
         DecimalFormat df = new DecimalFormat("#0.0000", new DecimalFormatSymbols(Locale.US));
         if (dduom.getItemCount() > 0 && ddpart.getItemCount() > 0 && ddvend.getItemCount() > 0 && ! ddcurr.getSelectedItem().toString().isEmpty()) {
                String[] TypeAndPrice = OVData.getItemPrice("v", ddvend.getSelectedItem().toString(), ddpart.getSelectedItem().toString(), 
                        dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString());
                String pricetype = TypeAndPrice[0].toString();
                Double price = Double.valueOf(TypeAndPrice[1]);
             //   
                listprice.setText(df.format(price));
                if (pricetype.equals("vend")) {
                    listprice.setBackground(Color.green);
                }
                if (pricetype.equals("item")) {
                    listprice.setBackground(Color.white);
                }
               // discount.setText(df.format(OVData.getPartDiscFromVend(ddvend.getSelectedItem().toString())));
                setnetprice();
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
        
         
        
        if (OVData.isValidItem(ddpart.getSelectedItem().toString()) && ! OVData.isValidUOMConversion(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString())) {
                bsmf.MainFrame.show("no base uom conversion");
                dduom.requestFocus();
                return false;
        }
        if (OVData.isValidItem(ddpart.getSelectedItem().toString()) && ! OVData.isBaseUOMOfItem(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString()) && ! OVData.isValidVendPriceRecordExists(ddvend.getSelectedItem().toString(),ddpart.getSelectedItem().toString(),dduom.getSelectedItem().toString(),ddcurr.getSelectedItem().toString())) {
                bsmf.MainFrame.show("no price record for conversion uom"); 
                dduom.requestFocus();
                return false;
        }
      return canproceed;   
    }
   
    
    public void getparts(String part) {
        try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
               // if part is not already in list
                int k = ddpart.getSelectedIndex();
              
                
                // lets first try as vend part...i.e....lets look up the item based on entering a vendor part number.
                if (k < 0) {
                    
                res = st.executeQuery("select vdp_item, vdp_vitem from vdp_mstr where vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" 
                        + " AND vdp_vitem = " + "'" + part + "'" 
                        + ";");
                int i = 0;
                while (res.next()) {
                    i++;
                   // ddpart.setSelectedItem(res.getString("vdp_item"));
                    vendnumber.setText(part);
                    ddpart.setForeground(Color.blue);
                    vendnumber.setForeground(Color.blue);
                    vendnumber.setEditable(false);
                }
                
                // if i is still 0...then must be a misc item
                if (i == 0) {
                  //  partnbr.addItem(part);
                  //  partnbr.setSelectedItem(part);
                    vendnumber.setText("");
                    ddpart.setForeground(Color.red);
                    vendnumber.setForeground(Color.red);
                   // custnumber.setBorder(BorderFactory.createLineBorder(Color.red));
                    vendnumber.setEditable(true);
                    
                    discount.setText("0.00");
                    listprice.setText("0.00");
                    listprice.setBackground(Color.white);
                    
                    netprice.setText("0.00");
                    qtyshipped.setText("0");
                }
                
                }
                
             if (k >= 0) {   
            discount.setText("0.00");
            listprice.setText("0.00");
            netprice.setText("0.00");
            qtyshipped.setText("0");
            tbdesc.setText(OVData.getItemDesc(ddpart.getSelectedItem().toString()));
            vendnumber.setText(OVData.getVendPartFromPart(ddvend.getSelectedItem().toString(),ddpart.getSelectedItem().toString()));
            dduom.setSelectedItem(OVData.getUOMFromItemSite(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString()));
            ddpart.setForeground(Color.blue);
            vendnumber.setForeground(Color.blue);
            vendnumber.setEditable(false);
            
            if (ddpart.getItemCount() > 0) {
                ddpart.setForeground(Color.blue);
                vendnumber.setForeground(Color.blue);
                setPrice();
                
            } // if part selected
             }  
                
                
                
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem with getparts function");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void setnetprice() {
        Double disc = 0.00;
        Double list = 0.00;
        Double net = 0.00;
        DecimalFormat df = new DecimalFormat("#0.0000", new DecimalFormatSymbols(Locale.US));
        
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
         for (int j = 0; j < orddet.getRowCount(); j++) {
             dol = dol + ( Double.valueOf(orddet.getValueAt(j, 5).toString()) * Double.valueOf(orddet.getValueAt(j, 9).toString()) ); 
         }
         tbtotdollars.setText(df.format(dol));
         lblcurr.setText(ddcurr.getSelectedItem().toString());
    }
   
     public void refreshDisplayTotals() {
        sumqty();
        sumdollars();
        sumlinecount();
    }
    
    public void vendChangeEvent(String mykey) {
        
        // reset
        lbvend.setText("");
        ddshipvia.setSelectedIndex(0);
        ddcurr.setSelectedIndex(0);
      try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
               
                if (venditemonly) {
                    ddpart.removeAllItems();
                    res = st.executeQuery("select vdp_item from vdp_mstr where vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                        ddpart.addItem(res.getString("vdp_item"));
                    }
                }
                res = st.executeQuery("select * from vd_mstr where vd_addr = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbvend.setText(res.getString("vd_name"));
                    ddshipvia.setSelectedItem(res.getString("vd_shipvia"));
                    ddcurr.setSelectedItem(res.getString("vd_curr"));
                    curr = ddcurr.getSelectedItem().toString();
                }
                
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem with vendchange event");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
     public void retotal() {
        
        DecimalFormat df = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.US));
        double dol = 0;
        double newdisc = 0;
        double newprice = 0;
        double newtax = 0;
        double listprice = 0;
        
         for (int j = 0; j < orddet.getRowCount(); j++) {
             listprice = Double.valueOf(orddet.getValueAt(j, 7).toString());
             orddet.setValueAt(String.valueOf(newdisc), j, 8);
             if (newdisc > 0) {
             newprice = listprice - (listprice * (newdisc / 100));
             } else {
             newprice = listprice;    
             }
             orddet.setValueAt(String.valueOf(newprice), j, 9);
              
             
         }
                
         
    }
   
    
    public static void lookUpFrameVendItem() {
        if (dialog != null) {
            dialog.dispose();
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
        lookUpModel = DTData.getVendXrefBrowseUtil(input.getText(), 0, "vdp_vitem");
        lookUpTable.setModel(lookUpModel);
        lookUpTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (lookUpModel.getRowCount() < 1) {
            dialog.setTitle("No Records Found!");
        } else {
            dialog.setTitle(lookUpModel.getRowCount() + " Records Found!");
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
            dialog.dispose();
            }
        }
        };
       lookUpTable.addMouseListener(mllu);
        dialog = new JDialog();
        dialog.setTitle("Enter Search Text:");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(input, BorderLayout.NORTH);
        dialog.add( scrollPane );
        dialog.pack();
        dialog.setLocationRelativeTo( null );
        dialog.setVisible(true);
    }

    public static void lookUpFrameItemDesc() {
        if (dialog != null) {
            dialog.dispose();
        }
        if (lookUpModel != null && lookUpModel.getRowCount() > 0) {
        lookUpModel.setRowCount(0);
        lookUpModel.setColumnCount(0);
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
        if (rb1.isSelected()) {  
         lookUpModel = DTData.getItemDescBrowse(input.getText(), "it_item");
        } else {
         lookUpModel = DTData.getItemDescBrowse(input.getText(), "it_desc");   
        }
        lookUpTable.setModel(lookUpModel);
        lookUpTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (lookUpModel.getRowCount() < 1) {
            dialog.setTitle("No Records Found!");
        } else {
            dialog.setTitle(lookUpModel.getRowCount() + " Records Found!");
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
                dialog.dispose();
                }
            }
        };
        lookUpTable.addMouseListener(mllu);
      
        
        JPanel rbpanel = new JPanel();
        bg = new ButtonGroup();
        rb1 = new JRadioButton("item");
        rb2 = new JRadioButton("description");
        rb1.setSelected(true);
        rb2.setSelected(false);
        BoxLayout radiobuttonpanellayout = new BoxLayout(rbpanel, BoxLayout.X_AXIS);
        rbpanel.setLayout(radiobuttonpanellayout);
        rbpanel.add(rb1);
        JLabel spacer = new JLabel("   ");
        rbpanel.add(spacer);
        rbpanel.add(rb2);
        bg.add(rb1);
        bg.add(rb2);
        
        
        dialog = new JDialog();
        dialog.setTitle("Search By Text:");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(input, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        dialog.add(panel);
        
        dialog.pack();
        dialog.setLocationRelativeTo( null );
        dialog.setVisible(true);
    }

   public void lookUpFrameVendor() {
        if (dialog != null) {
            dialog.dispose();
        }
        if (lookUpModel != null && lookUpModel.getRowCount() > 0) {
        lookUpModel.setRowCount(0);
        lookUpModel.setColumnCount(0);
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
        if (rb1.isSelected()) {  
         lookUpModel = DTData.getVendBrowseUtil(input.getText(), 0, "vd_name"); 
        } else {
         lookUpModel = DTData.getVendBrowseUtil(input.getText(), 0, "vd_zip");   
        }
        lookUpTable.setModel(lookUpModel);
        lookUpTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (lookUpModel.getRowCount() < 1) {
            dialog.setTitle("No Records Found!");
        } else {
            dialog.setTitle(lookUpModel.getRowCount() + " Records Found!");
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
                ddvend.setSelectedItem(target.getValueAt(row,1).toString());
                dialog.dispose();
                }
            }
        };
        lookUpTable.addMouseListener(mllu);
      
        
        JPanel rbpanel = new JPanel();
        bg = new ButtonGroup();
        rb1 = new JRadioButton("Name");
        rb2 = new JRadioButton("Zip");
        rb1.setSelected(true);
        rb2.setSelected(false);
        BoxLayout radiobuttonpanellayout = new BoxLayout(rbpanel, BoxLayout.X_AXIS);
        rbpanel.setLayout(radiobuttonpanellayout);
        rbpanel.add(rb1);
        JLabel spacer = new JLabel("   ");
        rbpanel.add(spacer);
        rbpanel.add(rb2);
        bg.add(rb1);
        bg.add(rb2);
        
        
        dialog = new JDialog();
        dialog.setTitle("Search By Text:");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(input, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        dialog.add(panel);
        
        dialog.pack();
        dialog.setLocationRelativeTo( null );
        dialog.setVisible(true);
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
        panelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        duedate = new com.toedter.calendar.JDateChooser();
        jLabel77 = new javax.swing.JLabel();
        remarks = new javax.swing.JTextField();
        userid = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        jLabel85 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        ddstatus = new javax.swing.JComboBox();
        ddvend = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        jLabel81 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        ddshipvia = new javax.swing.JComboBox();
        jLabel90 = new javax.swing.JLabel();
        cbblanket = new javax.swing.JCheckBox();
        ddsite = new javax.swing.JComboBox();
        jLabel91 = new javax.swing.JLabel();
        btpoprint = new javax.swing.JButton();
        orddate = new com.toedter.calendar.JDateChooser();
        tbbuyer = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        ddcurr = new javax.swing.JComboBox<>();
        lbvend = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btLookUpVendor = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelSchedule = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablesched = new javax.swing.JTable();
        panelDetail = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        qtyshipped = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        ddpart = new javax.swing.JComboBox();
        vendnumber = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dduom = new javax.swing.JComboBox<>();
        tbdesc = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btLookUpCustItem = new javax.swing.JButton();
        btLookUpItemDesc = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        netprice = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        listprice = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        discount = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        btdelitem = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        btupdateitem = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        lblcurr = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        setBackground(new java.awt.Color(0, 102, 204));

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Purchase Order Maintenance"));
        panelMain.setPreferredSize(new java.awt.Dimension(715, 550));

        jLabel76.setText("OrderNbr");

        jLabel86.setText("Remarks");

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        duedate.setDateFormatString("yyyy-MM-dd");

        jLabel77.setText("DateCreated");

        jLabel78.setText("UserID");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel85.setText("Status");

        jLabel82.setText("Vendor");

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "open", "closed", "partial", "hold", "void" }));

        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel81.setText("Due Date");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel90.setText("ShipVia");

        cbblanket.setText("Blanket");

        jLabel91.setText("Site");

        btpoprint.setText("Print");
        btpoprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpoprintActionPerformed(evt);
            }
        });

        orddate.setDateFormatString("yyyy-MM-dd");

        jLabel92.setText("Buyer");

        jLabel83.setText("Currency");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btLookUpVendor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpVendor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpVendorActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel92, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel90, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel76, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(5, 5, 5)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbblanket)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btLookUpVendor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE)))
                        .addGap(33, 33, 33)
                        .addComponent(lbvend, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)))
                .addGap(7, 7, 7)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel77, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel83, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel78, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userid, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(jLabel86)
                        .addGap(4, 4, 4)
                        .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(408, 408, 408)
                        .addComponent(btpoprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addGap(6, 6, 6)
                        .addComponent(btupdate)
                        .addGap(10, 10, 10)
                        .addComponent(btadd)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnew)
                            .addComponent(btclear))
                        .addGap(4, 4, 4))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btlookup)
                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelMainLayout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jLabel76))
                                .addGroup(panelMainLayout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(panelMainLayout.createSequentialGroup()
                                    .addGap(5, 5, 5)
                                    .addComponent(jLabel81))
                                .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(2, 2, 2)))
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel91)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel77))
                .addGap(6, 6, 6)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel82))
                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbvend, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(userid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel78))))
                        .addGap(9, 9, 9)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel90))
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel83)))))
                    .addComponent(btLookUpVendor))
                .addGap(5, 5, 5)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel92))
                    .addComponent(tbbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel85))
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(cbblanket)
                .addGap(2, 2, 2)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel86))
                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btdelete)
                        .addComponent(btpoprint))
                    .addComponent(btupdate)
                    .addComponent(btadd)))
        );

        add(panelMain);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        panelSchedule.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule Releases"));
        panelSchedule.setMinimumSize(new java.awt.Dimension(715, 550));
        panelSchedule.setPreferredSize(new java.awt.Dimension(640, 550));
        panelSchedule.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        panelSchedule.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 27, 608, 440));

        add(panelSchedule);

        panelDetail.setPreferredSize(new java.awt.Dimension(715, 550));

        qtyshipped.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                qtyshippedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                qtyshippedFocusLost(evt);
            }
        });

        jLabel79.setText("PartNumber");

        ddpart.setEditable(true);
        ddpart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpartActionPerformed(evt);
            }
        });

        jLabel87.setText("VendNumber");

        jLabel84.setText("Order Qty");

        jLabel5.setText("uom");

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel6.setText("Description");

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel79, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(vendnumber, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                    .addComponent(ddpart, 0, 203, Short.MAX_VALUE)
                    .addComponent(qtyshipped)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btLookUpCustItem, javax.swing.GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(73, 73, 73))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(vendnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel87))
                    .addComponent(btLookUpCustItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(btLookUpItemDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addContainerGap(73, Short.MAX_VALUE))
        );

        netprice.setEditable(false);
        netprice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netpriceActionPerformed(evt);
            }
        });

        jLabel80.setText("ListPrice");

        listprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                listpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                listpriceFocusLost(evt);
            }
        });

        jLabel88.setText("Disc%");

        discount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                discountFocusLost(evt);
            }
        });

        jLabel89.setText("NetPrice");

        btdelitem.setText("Delete");
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        btadditem.setText("Add");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

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
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel80, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel88, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel89, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdateitem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelitem))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(discount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addComponent(listprice, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(netprice)))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(netprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btadditem)
                    .addComponent(btupdateitem))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel1.setText("Total Lines");

        jLabel2.setText("Total Qty");

        jLabel3.setText("Total $");

        javax.swing.GroupLayout panelDetailLayout = new javax.swing.GroupLayout(panelDetail);
        panelDetail.setLayout(panelDetailLayout);
        panelDetailLayout.setHorizontalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(3, 3, 3)
                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelDetailLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        panelDetailLayout.setVerticalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        add(panelDetail);
    }// </editor-fold>//GEN-END:initComponents

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        
        int line = 0;
        
        line = getmaxline();
        line++;
        
           if (line == 1 && ! editmode) {
            btadd.setEnabled(true);
        } 
        
         boolean canproceed = validateDetail();
        // "line", "Part", "Desc", "VendPart", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
       
        if (canproceed) {
         myorddetmodel.addRow(new Object[]{line, 
             ddpart.getSelectedItem().toString(), 
             tbdesc.getText(),  
             vendnumber.getText(), 
             tbkey.getText(),  
             qtyshipped.getText(), 
             dduom.getSelectedItem().toString(), 
             listprice.getText(), 
             discount.getText(), 
             netprice.getText(), 
             "0", 
             "open"});
         sumqty();
         sumdollars();
         sumlinecount();
         listprice.setText("");
         netprice.setText("");
         discount.setText("");
         qtyshipped.setText("");
         ddpart.requestFocus();
        }
       
    }//GEN-LAST:event_btadditemActionPerformed

    private void ddpartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpartActionPerformed
         if (ddpart.getSelectedItem() != null && ! isLoad)
        getparts(ddpart.getSelectedItem().toString());
    }//GEN-LAST:event_ddpartActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
         int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals("close") || orddet.getValueAt(i, 11).toString().equals("partial")) {
                bsmf.MainFrame.show("Cannot Delete Closed or Partial Item");
                return;
                            } else {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
       
         sumqty();
         sumdollars();
         sumlinecount();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void netpriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netpriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_netpriceActionPerformed

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
        setnetprice();
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
        setnetprice();
    }//GEN-LAST:event_discountFocusLost

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
       setPrice();
    }//GEN-LAST:event_dduomActionPerformed

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
        
    }//GEN-LAST:event_qtyshippedFocusLost

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
         JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if (index == 1 && ddpart != null && ddpart.getItemCount() > 0) {
            ddpart.setSelectedIndex(0);
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void listpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusGained
         if (listprice.getText().equals("0")) {
            listprice.setText("");
        }
    }//GEN-LAST:event_listpriceFocusGained

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
         //"line", "Part", "Desc", "VendPart", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
                orddet.setValueAt(qtyshipped.getText(), i, 5);
                orddet.setValueAt(dduom.getSelectedItem().toString(), i, 6);
                orddet.setValueAt(listprice.getText(), i, 7);
                orddet.setValueAt(discount.getText(), i, 8);
                orddet.setValueAt(netprice.getText(), i, 9);
                orddet.setValueAt(tbdesc.getText(), i, 2);
                
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

    private void orddetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orddetMouseClicked
        int row = orddet.rowAtPoint(evt.getPoint());
        int col = orddet.columnAtPoint(evt.getPoint());
         //"line", "Part", "Desc", "VendPart", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
         isLoad = true;  
        ddpart.setSelectedItem(orddet.getValueAt(row, 1).toString());
        dduom.setSelectedItem(orddet.getValueAt(row, 6).toString());
        qtyshipped.setText(orddet.getValueAt(row, 5).toString());
        vendnumber.setText(orddet.getValueAt(row, 3).toString());
        listprice.setText(orddet.getValueAt(row, 7).toString());
        netprice.setText(orddet.getValueAt(row, 9).toString());
        discount.setText(orddet.getValueAt(row, 8).toString());
        tbdesc.setText(orddet.getValueAt(row, 2).toString());
       
        isLoad = false;
    }//GEN-LAST:event_orddetMouseClicked

    private void btLookUpCustItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpCustItemActionPerformed
        lookUpFrameVendItem();
    }//GEN-LAST:event_btLookUpCustItemActionPerformed

    private void btLookUpItemDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemDescActionPerformed
        lookUpFrameItemDesc();
    }//GEN-LAST:event_btLookUpItemDescActionPerformed

    private void btLookUpVendorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpVendorActionPerformed
        lookUpFrameVendor();
    }//GEN-LAST:event_btLookUpVendorActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput("deleteRecord")) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask("delete", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btpoprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpoprintActionPerformed
        OVData.printPurchaseOrder(tbkey.getText());
    }//GEN-LAST:event_btpoprintActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput("updateRecord")) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput("addRecord")) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
        if (! isLoad) {
            if (ddvend.getItemCount() > 0) {
                jTabbedPane1.setEnabledAt(1, true);
                vendChangeEvent(ddvend.getSelectedItem().toString());
            } // if ddvend has a list
        }
    }//GEN-LAST:event_ddvendActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("po");
    }//GEN-LAST:event_btnewActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpCustItem;
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btLookUpVendor;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btpoprint;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdateitem;
    private javax.swing.JCheckBox cbblanket;
    private javax.swing.JComboBox<String> ddcurr;
    private static javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JTextField discount;
    private com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
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
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblcurr;
    private javax.swing.JLabel lbvend;
    private javax.swing.JTextField listprice;
    private javax.swing.JTextField netprice;
    private com.toedter.calendar.JDateChooser orddate;
    private javax.swing.JTable orddet;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelSchedule;
    private javax.swing.JTextField qtyshipped;
    private javax.swing.JTextField remarks;
    private javax.swing.JTable tablesched;
    private javax.swing.JTextField tbbuyer;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField totlines;
    private javax.swing.JTextField userid;
    private javax.swing.JTextField vendnumber;
    // End of variables declaration//GEN-END:variables
}
