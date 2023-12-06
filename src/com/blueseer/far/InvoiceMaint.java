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
package com.blueseer.far;


import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import static com.blueseer.fgl.fglData.AcctBalEntry;
import com.blueseer.ord.ordData;
import com.blueseer.shp.shpData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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


/**
 *
 * @author vaughnte
 */
public class InvoiceMaint extends javax.swing.JPanel {

     // global variable declarations
                boolean isLoad = false;
                int ordercount = 0;
                String status = "";
               
             
            
                
      // global datatablemodel declarations     
    javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("amounttype"), 
                getGlobalColumnTag("amount")
            });
    
    javax.swing.table.DefaultTableModel myshipdetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                 getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("quantity"), 
                getGlobalColumnTag("price"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("tax")
            });           
                
    
  
    public InvoiceMaint() {
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
        ordercount = 0;
        
        
        tbkey.setText("");
        tbuserid.setText("");
        tbtrailer.setText("");
        tbremarks.setText("");
        tbref.setText("");
        tbcust.setText("");
        tbship.setText("");
        tbopenamt.setText("");
        tbbank.setText("");
        tbaracct.setText("");
        tbarcc.setText("");
        tbtaxcode.setText("");
        tbartaxamt.setText("");
        tbaramt.setText("");
        lbmessage.setText("");
        lbcust.setText("");
        tbterms.setText("");
        tbordnbr.setText("");
        tbpo.setText("");
        lbladdr.setText("");
        tbtotdollars.setText("");
        totlines.setText("");
        tbtotqty.setText("");
        
        cbisvoid.setSelected(false);
        cbispaid.setSelected(false);
       
       
        ddcurr.removeAllItems();
        ArrayList<String> curr = fglData.getCurrlist();
        for (int i = 0; i < curr.size(); i++) {
            ddcurr.addItem(curr.get(i));
        }
        ddcurr.setSelectedItem(OVData.getDefaultCurrency());
        
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        
        tabledetail.setModel(myshipdetmodel);
        myshipdetmodel.setRowCount(0);
        sactable.setModel(sacmodel);
        sacmodel.setRowCount(0);
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btclear.setEnabled(false);
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
                   btvoid.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   
                   // custom set
                   if (status.equals("c")) {
                       lbmessage.setText(getGlobalProgTag("closed"));
                       lbmessage.setForeground(Color.red);
                       btvoid.setEnabled(false);
                       btupdate.setEnabled(false);
                       cbispaid.setSelected(true);
                   } else {
                       lbmessage.setText(getGlobalProgTag("open"));
                       lbmessage.setForeground(Color.blue);
                       btvoid.setEnabled(true);
                       btupdate.setEnabled(false);
                       cbispaid.setSelected(false);
                   }
                   
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
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
                    return b;
                }
        
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    ddsite.requestFocus();
                    return b;
                }
               
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btclear.setEnabled(true);
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
     String[] m = new String[2];
     
     
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     
     try {
            boolean proceed = true;
            String uniqpo = "";
            int i = 0;
            int d = 0;
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
               proceed = validateInput("updateRecord");
                if (proceed) {
                    // lets collect single or multiple PO status
                       for (int j = 0; j < tabledetail.getRowCount(); j++) {
                         if (d > 0) {
                           if ( uniqpo.compareTo(tabledetail.getValueAt(j, 3).toString()) != 0) {
                           uniqpo = "multi-PO";
                           break;
                           }
                         }
                         d++;
                         uniqpo = tabledetail.getValueAt(j, 3).toString();
                       }
                    
                    st.executeUpdate("update ship_mstr set " 
                        + " sh_shipdate = " + "'" + bsmf.MainFrame.dfdate.format(dcshipdate.getDate()) + "'" + ","
                        + " sh_ref = " + "'" + tbref.getText() + "'" + ","
                        + " sh_rmks = " + "'" + tbremarks.getText() + "'" + ","
                        + " sh_userid = " + "'" + tbuserid.getText() + "'" + ","
                        + " sh_site = " + "'" + ddsite.getSelectedItem().toString() + "'"   
                        + " where sh_id = " + "'" + tbkey.getText().toString() + "'"
                        + ";");
                    // delete the sod_det records and add back.
                    //  "Line", "Part", "SO", "PO", "Qty", "Price", "Desc"
                    st.executeUpdate("delete from ship_det where shd_id = " + "'" + tbkey.getText() + "'"  );
                    for (int j = 0; j < tabledetail.getRowCount(); j++) {
                       st.executeUpdate("insert into ship_det "
                            + "(shd_id, shd_soline, shd_item, shd_so, shd_date, shd_po, shd_qty,"
                            + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, shd_loc, shd_site ) "
                            + " values ( " + "'" + tbkey.getText() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + bsmf.MainFrame.dfdate.format(dcshipdate.getDate()) + "'" + ","        
                            + "'" + tabledetail.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 10).toString() + "'" + ","        
                            + "'" + tabledetail.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");
                    }
                    
                     // now update shs_det
                    shpData.updateShipperSAC(tbkey.getText());
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};  
            } finally {
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
        }
     
     return m;
     }
     
     public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        bsmf.MainFrame.show(getMessageTag(1131));
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
                        
            Statement st = con.createStatement();
            ResultSet res = null;
            int i = 0;
            try {
                
                // rules:  if payment already applied, you cannot undo
                 int c = 0;
                  res = st.executeQuery("select ard_id from ard_mstr inner join ar_mstr on ar_nbr = ard_id and ar_type = 'P' where ard_ref = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                      c++;
                }
                
                
                if (c > 0) {
                  m = new String[]{BlueSeerUtils.ErrorBit, "Payment already applied, unable to void"};   
                } else {
                   // restore the original order
                   String _order = "";
                   String _line = "";
                   String _orderstatus = "";
                   String _detailstatus = "";
                   String _cumshippedqty = "";
                   String _qty = "";
                   double _adjustedqty = 0.00;
                   ArrayList<String[]> orderlines = new ArrayList<String[]>();
                     res = st.executeQuery("select sod_nbr, sod_line, so_status, sod_status, sod_ord_qty, sod_shipped_qty, shd_qty " +
                              " from so_mstr inner join sod_det on sod_nbr = so_nbr " +
                              " inner join ship_det on shd_so = sod_nbr and shd_soline = sod_line " +
                              " where ship_det.shd_id =  " + "'" + x[0] + "'" + ";");
                     while (res.next()) {
                      _order = res.getString("sod_nbr");
                      _line = res.getString("sod_line");
                      _orderstatus = res.getString("so_status");
                      _detailstatus = res.getString("sod_status");
                      _cumshippedqty = res.getString("sod_shipped_qty");
                      _qty = res.getString("shd_qty");
                      orderlines.add(new String[]{_order, _line, _orderstatus, _detailstatus, _cumshippedqty,_qty});
                     }
                     // now loop through orderlines and adjust so_mstr and sod_det
                     String finalstatus = "open";
                     int z = 0;
                     for (String[] s : orderlines) {
                         z++;
                         if (! s[0].isEmpty() && ! s[1].isEmpty()) {
                             _adjustedqty = bsParseDouble(s[4]) - bsParseDouble(s[5]);
                             if (_adjustedqty > 0) {
                                 finalstatus = getGlobalProgTag("backorder");
                             }
                             st.executeUpdate("update sod_det set " +
                             " sod_shipped_qty = " + "'" + _adjustedqty + "'" + "," +
                             " sod_status = 'open' "  +
                             " where sod_nbr = " + "'" + s[0] + "'" + 
                             " and sod_line = " + "'" + s[1] + "'" + ";");
                         }
                         if ( orderlines.size() == z && ! s[0].isEmpty()) {  // last line
                              st.executeUpdate("update so_mstr set " +
                             " so_status = " + "'" + finalstatus + "'" + 
                             " where so_nbr = " + "'" + s[0] + "'" + ";");
                         }
                     }
                     
                     // now update Inventory
                     OVData.UpdateInventoryFromShipperRV(x[0]);
                     
                     // now update TRHIST
                     OVData.TRHistIssSalesRV(x[0], new java.util.Date());
                    
                   // delete shipper
                   i = st.executeUpdate("delete from ship_det where shd_id = " + "'" + x[0] + "'" + ";");
                   i = st.executeUpdate("delete from ship_mstr where sh_id = " + "'" + x[0] + "'" + ";");
                   
                   // delete ar_mstr
                   i = st.executeUpdate("delete from ar_mstr where ar_nbr = " + "'" + x[0] + "'" + ";");
                   
                   // now determine if still in gl_tran or in gl_hist
                   int glt = 0;
                   ArrayList<String[]> glvals_hist = new ArrayList<String[]>();
                    res = st.executeQuery("select * from gl_tran where glt_type = 'ISS-SALES' and glt_ref = " + "'" + x[0] + "'" + ";");
                    while (res.next()) {
                          glt++;
                    }
                    if (glt == 0) {
                            res = st.executeQuery("select * from gl_hist where glh_type = 'ISS-SALES' and glh_ref = " + "'" + x[0] + "'" + ";");
                            while (res.next()) {
                                  glvals_hist.add(new String[]{
                                    res.getString("glh_site"),
                                    res.getString("glh_acct"),
                                    res.getString("glh_cc"),
                                    res.getString("glh_amt"),
                                    res.getString("glh_effdate")});
                            }
                    }
                    
                   // delete gl_tran
                   if (glt > 0) {
                   i = st.executeUpdate("delete from gl_tran where glt_type = 'ISS-SALES' and glt_ref = " + "'" + x[0] + "'" + ";");
                   }
                   
                   // delete from gl_hist as well as rebalance the acb_mstr
                   if (! glvals_hist.isEmpty()) {
                      for (String[] s : glvals_hist) {
                          double d = (-1 * Double.valueOf(s[3])); // reverse amt entry
                          AcctBalEntry(s[0], s[1], s[2], d, s[4]);
                      }
                      i = st.executeUpdate("delete from gl_hist where glh_type = 'ISS-SALES' and glh_ref = " + "'" + x[0] + "'" + ";");
                   }
                  
                   
                    // need to bundle above actions into one transaction
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    
                }       
                    
            } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
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
                String order = "";
                String po = "";
                
                 res = st.executeQuery("select shd_soline, shd_item, shd_so, shd_po, sum(shd_qty) as sumqty, shd_netprice, shd_desc, " +
                         " shd_disc, shd_listprice, shd_taxamt " +
                         " from ship_det where shd_id = " + "'" + x[0] + "'" +
                                       " group by shd_so, shd_soline, shd_item, shd_po, shd_netprice, shd_desc, shd_disc, shd_listprice, shd_taxamt " + ";");
                while (res.next()) {
                  myshipdetmodel.addRow(new Object[]{res.getString("shd_soline"), res.getString("shd_item"), 
                      res.getString("shd_so"), 
                      res.getString("shd_po"), 
                      res.getString("sumqty"), 
                      res.getString("shd_netprice"),
                      res.getString("shd_desc"),
                      res.getString("shd_disc"),
                      res.getString("shd_listprice"),
                      res.getString("shd_taxamt")
                  });
                  
                  // lets determine if single or multi PO / SO
                  if (d > 0) {
                           if ( po.compareTo(res.getString("shd_po")) != 0) {
                           po = "multi-PO";
                           order = "multi-order";
                           break;
                           }
                         }
                         d++;
                         po = res.getString("shd_po");
                         order = res.getString("shd_so");
                }
                
                res = st.executeQuery("select * from ship_mstr inner join cms_det on cms_shipto = sh_ship " +
                        " inner join cm_mstr on cm_code = sh_cust " + 
                        " inner join ar_mstr on ar_nbr = sh_id and ar_type = 'I' " +
                         " where sh_id = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                    i++;
                    
                    tbkey.setText(res.getString("sh_id"));
                    tbcust.setText(res.getString("sh_cust"));
                    lbcust.setText(res.getString("cm_name"));
                    tbship.setText(res.getString("sh_ship"));
                    tbordnbr.setText(order);
                    tbpo.setText(po);
                    ddcurr.setSelectedItem(res.getString("sh_curr"));
                    tbref.setText(res.getString("sh_ref"));
                    tbuserid.setText(res.getString("sh_userid"));
                    tbremarks.setText(res.getString("sh_rmks"));
                   // ddpo.setSelectedItem(res.getString("sh_po"));
                    dcshipdate.setDate(parseDate(res.getString("sh_shipdate")));
                    dcinvduedate.setDate(parseDate(res.getString("ar_duedate")));
                    tbterms.setText(res.getString("ar_terms"));
                    tbtaxcode.setText(res.getString("ar_tax_code"));
                    tbaramt.setText(bsFormatDouble(res.getDouble("ar_amt")));
                    tbartaxamt.setText(bsFormatDouble(res.getDouble("ar_amt_tax")));
                    tbopenamt.setText(bsFormatDouble(res.getDouble("ar_open_amt")));
                    tbbank.setText(res.getString("ar_bank"));
                    ddshipvia.setSelectedItem(res.getString("sh_shipvia"));
                    ddsite.setSelectedItem(res.getString("sh_site"));
                    status = res.getString("ar_status");
                    tbaracct.setText(res.getString("sh_ar_acct"));
                    tbarcc.setText(res.getString("sh_ar_cc"));
                    lbladdr.setText(res.getString("cms_name") + "  " + res.getString("cms_line1") + "..." + res.getString("cms_city") +
                                    ", " + res.getString("cms_state") + " " + res.getString("cms_zip"));
                }
                
                    // now get sac table
                    ArrayList<String[]> sac = shpData.getShipperSAC(x[0]);
                 //write to shs_det
                     for (String[] s : sac) {
                         sacmodel.addRow(new Object[]{
                         s[0], s[1], s[2], s[3], s[4]
                         });
                     }
                   sactable.setModel(sacmodel);
                
                
                retotal();
               
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError};  
        }
      return m;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getInvoiceBrowseUtil(luinput.getText(),0, "sh_id");
        } else {
         luModel = DTData.getInvoiceBrowseUtil(luinput.getText(),0, "sh_cust");   
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
        double qty = 0;
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             qty = qty + bsParseDouble(tabledetail.getValueAt(j, 4).toString()); 
         }
         tbtotqty.setText(String.valueOf(qty));
    }
    
    public void sumdollars() {
        
        double dol = 0;
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             dol = dol + ( bsParseDouble(tabledetail.getValueAt(j, 4).toString()) * bsParseDouble(tabledetail.getValueAt(j, 5).toString()) ); 
         }
         // now add trailer/summary charges if any
         for (int j = 0; j < sactable.getRowCount(); j++) {
            if (sactable.getValueAt(j,2).toString().equals("charge") || sactable.getValueAt(j,2).toString().equals("shipping ADD"))
            dol += bsParseDouble(sactable.getValueAt(j,4).toString());
        }
         tbtotdollars.setText(bsFormatDouble(dol));
    }
     
    public void retotal() {
         sumqty();
         sumdollars();
         sumlinecount();
    }
        
    public void setLabelByShipTo(String shipto) {
        try {
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int i = 0;
            try {
                res = st.executeQuery("select * from cms_det where cms_shipto = " + "'" + shipto + "'" + ";");
                while (res.next()) {
                    i++;
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel5 = new javax.swing.JLabel();
        panelMain = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btvoid = new javax.swing.JButton();
        btPrintShp = new javax.swing.JButton();
        btPrintInv = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        dcshipdate = new com.toedter.calendar.JDateChooser();
        tbtrailer = new javax.swing.JTextField();
        ddshipvia = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        tbuserid = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        lbladdr = new javax.swing.JLabel();
        tbremarks = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        dcinvduedate = new com.toedter.calendar.JDateChooser();
        jLabel36 = new javax.swing.JLabel();
        tbaracct = new javax.swing.JTextField();
        tbarcc = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        tbtaxcode = new javax.swing.JTextField();
        tbordnbr = new javax.swing.JTextField();
        tbpo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tbterms = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cbispaid = new javax.swing.JCheckBox();
        cbisvoid = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tbopenamt = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        tbbank = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        tbaramt = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        tbartaxamt = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        tbship = new javax.swing.JTextField();
        tbcust = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lbcust = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        jScrollPane8 = new javax.swing.JScrollPane();
        sactable = new javax.swing.JTable();
        lbmessage = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();

        jLabel5.setText("jLabel5");

        setBackground(new java.awt.Color(0, 102, 204));

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Invoice View"));
        panelMain.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("Invoice Nbr:");
        jLabel24.setName("lblid"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btvoid.setText("Void");
        btvoid.setName("btvoid"); // NOI18N
        btvoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btvoidActionPerformed(evt);
            }
        });

        btPrintShp.setText("Print Shipper");
        btPrintShp.setName("btprintshipper"); // NOI18N
        btPrintShp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintShpActionPerformed(evt);
            }
        });

        btPrintInv.setText("Print Invoice");
        btPrintInv.setName("btprintinvoice"); // NOI18N
        btPrintInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintInvActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Header"));
        jPanel3.setName("panelheader"); // NOI18N

        jLabel25.setText("Site");
        jLabel25.setName("lblsite"); // NOI18N

        jLabel35.setText("Shipdate");
        jLabel35.setName("lblshipdate"); // NOI18N

        jLabel26.setText("UserID");
        jLabel26.setName("lbluserid"); // NOI18N

        dcshipdate.setDateFormatString("yyyy-MM-dd");

        jLabel39.setText("ShipVia");
        jLabel39.setName("lblshipvia"); // NOI18N

        jLabel40.setText("Trailer");
        jLabel40.setName("lbltrailer"); // NOI18N

        jLabel27.setText("Ref");
        jLabel27.setName("lblref"); // NOI18N

        lbladdr.setBackground(java.awt.Color.lightGray);
        lbladdr.setBorder(javax.swing.BorderFactory.createTitledBorder("ShipTo Addr"));
        lbladdr.setName("panelshipto"); // NOI18N

        jLabel41.setText("Remarks");

        dcinvduedate.setDateFormatString("yyyy-MM-dd");

        jLabel36.setText("InvDue");
        jLabel36.setName("lblduedate"); // NOI18N

        jLabel2.setText("ARAcct");
        jLabel2.setName("lblaracct"); // NOI18N

        jLabel6.setText("ARcc");
        jLabel6.setName("lbarcc"); // NOI18N

        jLabel8.setText("Currency");
        jLabel8.setName("lblcurrency"); // NOI18N

        jLabel9.setText("OrdNbr");
        jLabel9.setName("lblorder"); // NOI18N

        jLabel10.setText("PONbr");
        jLabel10.setName("lblpo"); // NOI18N

        jLabel11.setText("ShipCode:");
        jLabel11.setName("lblship"); // NOI18N

        cbispaid.setText("Paid?");
        cbispaid.setName("cbpaid"); // NOI18N

        cbisvoid.setText("Void?");
        cbisvoid.setName("cbvoid"); // NOI18N

        jLabel12.setText("Terms");
        jLabel12.setName("lblterms"); // NOI18N

        jLabel13.setText("TaxCode");
        jLabel13.setName("lbltaxcode"); // NOI18N

        jLabel14.setText("OpenAmt");
        jLabel14.setName("lblopenamt"); // NOI18N

        jLabel15.setText("Bank");
        jLabel15.setName("lblbank"); // NOI18N

        jLabel16.setText("AR Amt");
        jLabel16.setName("lblaramt"); // NOI18N

        jLabel17.setText("Tax Amt");
        jLabel17.setName("lbltaxamt"); // NOI18N

        jLabel7.setText("CustCode:");
        jLabel7.setName("lblcust"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbbank, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbarcc, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel36)
                            .addComponent(jLabel35)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbtaxcode)
                            .addComponent(tbterms)
                            .addComponent(ddcurr, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dcshipdate, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(dcinvduedate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbartaxamt))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbopenamt)
                            .addComponent(tbaramt)
                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbispaid)
                                    .addComponent(cbisvoid)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tbcust, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                    .addComponent(tbship))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel41)
                        .addGap(5, 5, 5)
                        .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel25))
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel35))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(dcshipdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel27))
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbladdr))
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel26))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbtaxcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel40))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(tbship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel39)
                                .addGap(16, 16, 16)
                                .addComponent(jLabel2))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(tbaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbterms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12))))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(dcinvduedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel6))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(tbarcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel14))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(tbopenamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(13, 13, 13)
                                        .addComponent(jLabel36)))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel15))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbbank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tbartaxamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel17))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbaramt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel16))))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(cbispaid))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbisvoid))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(lbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel41))
                    .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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
        jScrollPane7.setViewportView(tabledetail);

        jLabel3.setText("Total Lines:");
        jLabel3.setName("lbllines"); // NOI18N

        jLabel1.setText("Total Qty:");
        jLabel1.setName("lblqty"); // NOI18N

        jLabel4.setText("Total $");
        jLabel4.setName("lblamt"); // NOI18N

        sactable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(sactable);

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
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
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel24)
                        .addGap(5, 5, 5)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(btclear)
                        .addGap(78, 78, 78)
                        .addComponent(lbmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btPrintInv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btPrintShp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btvoid)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate))
                    .addComponent(jScrollPane7)
                    .addComponent(jScrollPane8))
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24))
                            .addComponent(btclear)
                            .addComponent(btlookup))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addComponent(lbmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btvoid)
                    .addComponent(btupdate)
                    .addComponent(btPrintShp)
                    .addComponent(btPrintInv)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)))
        );

        add(panelMain);
    }// </editor-fold>//GEN-END:initComponents

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btvoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btvoidActionPerformed
         if (! validateInput("deleteRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("delete", new String[]{tbkey.getText()});    
    }//GEN-LAST:event_btvoidActionPerformed

    private void btPrintShpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintShpActionPerformed
        OVData.printShipper(tbkey.getText());

    }//GEN-LAST:event_btPrintShpActionPerformed

    private void btPrintInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintInvActionPerformed
       OVData.printInvoice(tbkey.getText(), true);
    }//GEN-LAST:event_btPrintInvActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       bsmf.MainFrame.show(getMessageTag(1122));
    }//GEN-LAST:event_btupdateActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btPrintInv;
    private javax.swing.JButton btPrintShp;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btvoid;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbispaid;
    private javax.swing.JCheckBox cbisvoid;
    private com.toedter.calendar.JDateChooser dcinvduedate;
    private com.toedter.calendar.JDateChooser dcshipdate;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbcust;
    private javax.swing.JLabel lbladdr;
    private javax.swing.JLabel lbmessage;
    private javax.swing.JPanel panelMain;
    private javax.swing.JTable sactable;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTextField tbaracct;
    private javax.swing.JTextField tbaramt;
    private javax.swing.JTextField tbarcc;
    private javax.swing.JTextField tbartaxamt;
    private javax.swing.JTextField tbbank;
    private javax.swing.JTextField tbcust;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbopenamt;
    private javax.swing.JTextField tbordnbr;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbremarks;
    private javax.swing.JTextField tbship;
    private javax.swing.JTextField tbtaxcode;
    private javax.swing.JTextField tbterms;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField tbtrailer;
    private javax.swing.JTextField tbuserid;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
