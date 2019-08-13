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
package com.blueseer.rcv;

import bsmf.MainFrame;
import static bsmf.MainFrame.dfdate;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.BlueSeer;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.DBTask;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author vaughnte
 */
public class RecvMaintPanel extends javax.swing.JPanel implements BlueSeer {

    // global variable declarations
                boolean isLoad = false;
                String terms = "";
                String apacct = "";
                String apcc = "";
                
    // global datatablemodel declarations            
                javax.swing.table.DefaultTableModel myrecvdetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "WH", "serial", "lot", "cost"
            })
                        {
               boolean[] canEdit = new boolean[]{
               false, false, false, true, false, false, false, true, true, true, true, true
               };       
           @Override  
           public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
           }
                        };
    
   
    public RecvMaintPanel() {
        initComponents();
    }
   
    

    // interface functions implemented
    public void executeTask(String x, String y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String key = "";
          
          public Task(String type, String key) { 
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
             initvars("");  
           } else if (this.type.equals("get") && message[0].equals("1")) {
             tbkey.requestFocus();
           } else {
             initvars(key);  
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
   
    public void setPanelComponentState(JPanel panel, boolean b) {
         panel.setEnabled(b);
        Component[] components = panel.getComponents();
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                component.setEnabled(b);
            }
    } 
    
    public void setComponentDefaultValues() {
        
        isLoad = true;
        rvdet.setModel(myrecvdetmodel);
        
         java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdate.setDate(now);
        
        tbpackingslip.setBackground(Color.white);
        tbpackingslip.setText("");
        tbqty.setText("0");
        tbserial.setText("");
        tblot.setText("");
        tbkey.setText("");
        tbkey.setForeground(Color.black);
       
        
        
        tbcost.setDisabledTextColor(Color.black);
        tbcost.setText("");
        
        ddsite.setForeground(Color.black);
       
        cbautovoucher.setSelected(OVData.isAutoVoucher());
        
        duedate.setDisabledTextColor(Color.black);
        duedate.setText("");
        
        tbqtyrcvd.setEditable(false);
        tbqtyrcvd.setForeground(Color.blue);
        tbqtyrcvd.setText("");
        
        tbqtyord.setEditable(false);
        tbqtyord.setForeground(Color.blue);
        tbqtyord.setText("");
        
        tbline.setEditable(false);
        tbline.setForeground(Color.blue);
        tbline.setText("");
       
        tbprice.setEditable(false);
        tbprice.setForeground(Color.blue);
        tbprice.setText("");
        
        orddate.setDisabledTextColor(Color.black);
        orddate.setText("");
        
        ddpo.removeAllItems();
        ddpart.removeAllItems();
        
        ddwh.removeAllItems();
        ArrayList<String> mywh = OVData.getWareHouseList();
        for (String code : mywh) {
            ddwh.addItem(code);
        }
        ddwh.insertItemAt("", 0);
         ddwh.setSelectedIndex(0);
         
         ddloc.removeAllItems();
        ArrayList<String> myloc = OVData.getLocationList();
        for (String code : myloc) {
            ddloc.addItem(code);
        }
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
         
         
        
        ddsite.removeAllItems();
        ArrayList<String>mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
         ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddvend.removeAllItems();
        ArrayList myvend = OVData.getvendmstrlist();
        for (int i = 0; i < myvend.size(); i++) {
            ddvend.addItem(myvend.get(i));
        }
            ddvend.insertItemAt("", 0);
            ddvend.setSelectedIndex(0);
       
        
        
        myrecvdetmodel.setRowCount(0);
        
        isLoad = false;
    }
    
    public void initvars(String arg) {
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btbrowse.setEnabled(true);
        
        if (! arg.isEmpty()) {
            getRecord(arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
        }
        
    }
    
    public String[] getRecord(String x) {
       String[] m = new String[2];
       
       myrecvdetmodel.setRowCount(0);
       
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                String tvend = "";
                String tdate = "";
                String tps = "";
                
                res = st.executeQuery("select rv_vend, rv_recvdate, rv_packingslip from recv_mstr where rv_id = " + "'" + x + "'" + ";");
                while (res.next()) {
                    i++;
                    tvend = res.getString("rv_vend");
                    tdate = res.getString("rv_recvdate");
                    tps = res.getString("rv_packingslip");
                }
                res.close();
                if (i > 0) {
                    ddvend.setSelectedItem(tvend);
                    dcdate.setDate(bsmf.MainFrame.dfdate.parse(tdate));
                    tbpackingslip.setText(tps);
                    tbkey.setText(x);
                }
               
                res = st.executeQuery("select * from recv_det where rvd_id = " + "'" + x + "'" + ";");
                
                while (res.next()) {
                    
                  //  "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "WH", "serial", "lot", "cost"
                  myrecvdetmodel.addRow(new Object[]{res.getString("rvd_part"), res.getString("rvd_po"), 
                      res.getString("rvd_poline"), res.getString("rvd_qty"), res.getString("rvd_listprice"),
                   res.getString("rvd_disc"), res.getString("rvd_netprice"), res.getString("rvd_loc"), res.getString("rvd_wh"),
                  res.getString("rvd_serial"), res.getString("rvd_lot"), res.getString("rvd_cost")});
                }
                
                if (i > 0) {
                    m = new String[]{"0", "found record"};  
                    setPanelComponentState(this, true);
                    btnew.setEnabled(false);
                    btadd.setEnabled(false);
                    tbkey.setEditable(false);
                    tbkey.setForeground(Color.blue);
                } else {
                   m = new String[]{"1", "no record found"}; 
                   tbkey.setForeground(Color.red);
                }
               

            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{"1", "Cannot retrieve receiver"};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
        return m;
    }

    public String[] addRecord(String x) {
     String[] m = new String[2];
         try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                boolean error = false;
                String receiver = tbkey.getText().toString();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                
                javax.swing.table.DefaultTableModel tempmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                new String[]{
                "RecvID", "RecvLine", "Part", "Qty", "Price"
                });
                JTable temptable = new JTable(tempmodel); 
                
                boolean proceed = validateInput("addRecord");
                
                if (proceed) {
                    setvendorvariables(ddvend.getSelectedItem().toString());
                    st.executeUpdate("insert into recv_mstr "
                        + "(rv_id, rv_vend, "
                        + " rv_recvdate, rv_packingslip, rv_userid, rv_site, rv_terms, rv_ap_acct, rv_ap_cc) "
                        + " values ( " + "'" + tbkey.getText().toString() + "'" + ","
                        + "'" + ddvend.getSelectedItem() + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + tbpackingslip.getText() + "'" + ","
                        + "'" + bsmf.MainFrame.userid.toString() + "'" + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + apacct + "'" + ","
                        + "'" + apcc + "'"
                        + ")"
                        + ";");
           
           
               
           
                 //  "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "WH", "serial", "lot", "cost"
                    for (int j = 0; j < rvdet.getRowCount(); j++) {
                        st.executeUpdate("insert into recv_det "
                            + "(rvd_id, rvd_rline, rvd_part, rvd_po, rvd_poline, rvd_qty,"
                            + "rvd_listprice, rvd_disc, rvd_netprice,  "
                            + " rvd_loc, rvd_wh, rvd_serial, rvd_lot, rvd_cost, rvd_site, rvd_packingslip, rvd_date ) "
                            + " values ( " + "'" + tbkey.getText() + "'" + ","
                            + "'" + String.valueOf(j + 1) + "'" + ","
                            + "'" + rvdet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 10).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 11).toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                            + "'" + tbpackingslip.getText() + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" 
                            + ")"
                            + ";");
                        
                        tempmodel.addRow(new Object[] {tbkey.getText(), String.valueOf(j+ 1),
                           rvdet.getValueAt(j, 0).toString(),
                           rvdet.getValueAt(j, 3).toString(),
                           rvdet.getValueAt(j, 6).toString()
                        });
                        
                    } 
                    
                    
                    /* update PO from receiver */
                    OVData.updatePOFromReceiver(tbkey.getText());
                    
                    /* create tran_mstr records */
                        if (! error)
                        error = OVData.TRHistRctPurch(receiver, dcdate.getDate());
                       
                        /* adjust inventory */
                        if (! error)
                        error = OVData.UpdateInventoryFromReceiver(receiver);
                        
                        /* create gl_tran records */
                        if (! error)
                        error = OVData.glEntryFromReceiver(receiver, dcdate.getDate());
                       
                
                  if (! error) {        
                  m = new String[] {"0","receiver has been added"};
                   
                       /* create auto-voucher from temptable if autovoucher is on */
                    if (cbautovoucher.isSelected()) {
                    String messg = OVData.CreateVoucher(temptable, ddsite.getSelectedItem().toString(), ddvend.getSelectedItem().toString(), tbpackingslip.getText(), dcdate.getDate(), tbserial.getText()); 
                     if (! messg.isEmpty()) {
                         m = new String[] {"1","unable to add voucher"};
                     } else {
                         m = new String[] {"0","vouchering complete"};
                     }
                    }
                  
                  
                  initvars("");
                  } else {
                  m = new String[] {"1","unable to add receiver"};
                  }
                    
               
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[] {"1","unable to add receiver"};
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     return m;
    }
    
    public String[] updateRecord(String x) {
     String[] m = new String[2];
     
      try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                
                // first validate input
                boolean proceed = validateInput("updateRecord");
                
                // if this receiver has 'any' line items already vouchered then bale...cannot update
                res = st.executeQuery("select rvd_id, rvd_rline, rvd_part from recv_det where rvd_id = " + "'" + x + "'" + 
                                      " and rvd_voqty > 0 " + ";");
                int i = 0;
                while (res.next()) {
                    i++;
                }
                if (i > 0) {
                   return m = new String[] {"1","cannot update receiver...some lines already vouchered"};
                }
                
                
                if (proceed) {
                    st.executeUpdate("update recv_mstr set "
                        + "rv_packingslip = " + "'" + tbpackingslip.getText() + "'" + ","
                        + "rv_recvdate = " + "'" + dfdate.format(dcdate.getDate()) + "'"
                        + " where rv_id = " + "'" + tbkey.getText().toString() + "'"
                        + ";");
                    // delete the recv_det records and add back.
                    st.executeUpdate("delete from recv_det where rvd_id = " + "'" + tbkey.getText() + "'"  );
                    
                   // "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "WH", "serial", "lot", "cost"
                    for (int j = 0; j < rvdet.getRowCount(); j++) {
                        st.executeUpdate("insert into recv_det "
                            + "(rvd_id, rvd_rline, rvd_part, rvd_po, rvd_poline, rvd_qty,"
                            + "rvd_listprice, rvd_disc, rvd_netprice,  "
                            + " rvd_loc, rvd_wh, rvd_serial, rvd_lot, rvd_cost, rvd_site, rvd_packingslip, rvd_date ) "
                            + " values ( " + "'" + tbkey.getText() + "'" + ","
                            + "'" + String.valueOf(j + 1) + "'" + ","
                            + "'" + rvdet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 10).toString() + "'" + ","
                            + "'" + rvdet.getValueAt(j, 11).toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                            + "'" + tbpackingslip.getText() + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" 
                            + ")"
                            + ";");
                    }
                   m = new String[] {"0","receiver has been updated"};
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
               m = new String[] {"1","unable to update receiver"};
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[] {"1","unable to update receiver"};
        }
     
     return m;
    }
    
    public String[] deleteRecord(String x) {
     String[] m = new String[2];
     return m;
    }
    
    public boolean validateInput(String x) {
        
        boolean b = true;
                if (ddvend.getSelectedItem() == null || ddvend.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("Must Have a Vendor");
                    return b;
                }
                if (! x.equals("updateRecord")) {  // if adjusting fields in the table....this validation is not necessary as they cannot change the PO
                    if (ddpo.getSelectedItem() == null || ddpo.getSelectedItem().toString().isEmpty()) {
                        b = false;
                        bsmf.MainFrame.show("Must Have a legitimate PO to receive against");
                        return b;
                    }
                }
                
                if (tbpackingslip.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("Must enter a packing slip number");
                    tbpackingslip.requestFocus();
                    return b;
                }
                
                if (! x.equals("addItem")) {
                    if (rvdet.getRowCount() <= 0) {
                        b = false;
                        bsmf.MainFrame.show("There are no rows received");
                        return b;
                    }
                }
                
                if (x.equals("addItem")) {
                    if (tbqty.getText().isEmpty()) {
                        b = false;
                        bsmf.MainFrame.show("Must enter a quantity");
                        tbqty.requestFocus();
                        return b;
                    }
                }
               
                
                if ( OVData.isGLPeriodClosed(dfdate.format(dcdate.getDate()))) {
                    b = false;
                    bsmf.MainFrame.show("Period is closed");
                    return b;
                }
        return b;
    }
    

    // additional functions
    public void setvendorvariables(String vendor) {
        
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select vd_terms, poc_rcpt_acct, poc_rcpt_cc from vd_mstr inner join po_ctrl where vd_addr = " + "'" + vendor + "'" + ";");
                while (res.next()) {
                    i++;
                   apacct = res.getString("poc_rcpt_acct");
                   apcc = res.getString("poc_rcpt_cc");
                   terms = res.getString("vd_terms");
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql code does not execute");
            }
            bsmf.MainFrame.con.close();
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

        jPanel1 = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbpackingslip = new javax.swing.JTextField();
        orddate = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        ddpart = new javax.swing.JComboBox();
        jLabel43 = new javax.swing.JLabel();
        tbqtyrcvd = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        rvdet = new javax.swing.JTable();
        ddvend = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        btedit = new javax.swing.JButton();
        ddpo = new javax.swing.JComboBox();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        tbline = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        tbqtyord = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        duedate = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        tbserial = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        tblot = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        tbcost = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox<>();
        lblvendpart = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        ddwh = new javax.swing.JComboBox<>();
        ddloc = new javax.swing.JComboBox<>();
        jLabel42 = new javax.swing.JLabel();
        cbautovoucher = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Receiver Maintenance"));

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("Receiver#");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel26.setText("DueDate");

        jLabel30.setText("PartNumber");

        jLabel32.setText("Price");

        jLabel36.setText("Vendor");

        jLabel38.setText("PO Number");

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        ddpart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpartActionPerformed(evt);
            }
        });

        jLabel43.setText("Qty Recvd");

        rvdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(rvdet);

        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Del Item");
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        ddpo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpoActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("PackingSlip");

        jLabel33.setText("Line");

        jLabel34.setText("Qty Ordered");

        jLabel28.setText("OrdDate");

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel29.setText("Qty");

        jLabel31.setText("WareHouse");

        jLabel35.setText("DateRecvd");

        jLabel37.setText("Site");

        jLabel39.setText("Serial");

        jLabel40.setText("Lot");

        jLabel41.setText("Cost");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel42.setText("Location");

        cbautovoucher.setText("AutoVoucher?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel33))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbqtyrcvd, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbqtyord, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(tbline))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel26)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeleteitem)))
                        .addGap(11, 11, 11))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(ddvend, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(125, 125, 125))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(ddloc, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(ddpart, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(ddpo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(ddwh, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(61, 61, 61)))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblvendpart, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel39)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ddsite, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addGap(110, 110, 110)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbqty)
                            .addComponent(tbserial)
                            .addComponent(tblot)
                            .addComponent(tbcost)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbautovoucher)
                                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbpackingslip, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel24)
                .addGap(536, 536, 536))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane7)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addGap(536, 536, 536))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(536, 536, 536))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)
                        .addGap(23, 23, 23))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btbrowse)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(cbautovoucher))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpackingslip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel30)
                                .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel29))
                            .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblvendpart, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tblot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel40))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel41)
                                    .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel42))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel32))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel43)
                                            .addComponent(tbqtyrcvd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel33)
                                            .addComponent(jLabel26)
                                            .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(tbqtyord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel34)
                                            .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel28))))
                                .addGap(33, 33, 33)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btdeleteitem)
                                    .addComponent(btadditem))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btadd)
                                    .addComponent(btedit)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel31))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      
        tbkey.setText(String.valueOf(OVData.getNextNbr("receiver")));
        setPanelComponentState(this, true);
        btedit.setEnabled(false);
        tbkey.setEditable(false);
        tbkey.setForeground(Color.blue);
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
          
          boolean proceed = validateInput("addItem"); 
                
       //   "Part", "PO", "line", "Qty",  listprice, disc, netprice, loc, serial, lot, cost
            if (proceed)
            myrecvdetmodel.addRow(new Object[]{ddpart.getSelectedItem(), ddpo.getSelectedItem(), 
                tbline.getText(), tbqty.getText(), tbprice.getText(), "0", 
                tbprice.getText(), ddloc.getSelectedItem().toString(), ddwh.getSelectedItem().toString(), 
                tbserial.getText(), tblot.getText(), tbcost.getText()});
       
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", tbkey.getText());
    }//GEN-LAST:event_btaddActionPerformed

    private void ddpartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpartActionPerformed

        try {
            String mypart = "";
            String mypo = "";
            
                    tbline.setText("");
                    tbprice.setText("");
                    ddwh.setSelectedIndex(0);
                    ddloc.setSelectedIndex(0);
                    ddsite.setSelectedIndex(0);
                    lblvendpart.setText("");
                    duedate.setText("");
                    tbqtyrcvd.setText("");
                    tbqtyord.setText("");
                    orddate.setText("");
                    tbqty.setText("0");
                    tbserial.setText("");
                    tblot.setText("");
                    tbcost.setText("");
            
            if (ddpart.getItemCount() > 0 && ddpo.getItemCount() > 0) {
                mypart = ddpart.getSelectedItem().toString();
                mypo = ddpo.getSelectedItem().toString();
            }
            if (! mypo.toString().isEmpty() || ! mypart.toString().isEmpty()  ) {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            
            if (bsmf.MainFrame.con.isClosed())
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                    // at this time you cannot have the same item on the PO more than once
                
                res = st.executeQuery("select itc_total, it_loc, it_wh, pod_nbr, pod_line, pod_vendpart, pod_netprice, pod_rcvd_qty, pod_ord_qty, pod_ord_date, pod_due_date, pod_status, pod_site from pod_mstr " +
                       " inner join po_mstr on po_nbr = pod_nbr " +
                       " left outer join item_mstr on it_item = pod_part " +
                       " left outer join item_cost on itc_item = pod_part and itc_set = 'standard' and itc_site = po_site " + 
                       " where pod_part = " + "'" + mypart + "'" + 
                        " AND pod_nbr = " + "'" + mypo + "'" + ";");
                while (res.next()) {
                    tbline.setText(res.getString("pod_line"));
                    tbprice.setText(res.getString("pod_netprice"));
                    if (res.getString("itc_total") != null)
                    tbcost.setText(res.getString("itc_total"));
                    if (res.getString("it_loc") != null)
                    ddwh.setSelectedItem(res.getString("it_loc"));
                    if (res.getString("it_wh") != null)
                    ddloc.setSelectedItem(res.getString("it_wh"));
                    ddsite.setSelectedItem(res.getString("pod_site"));
                    lblvendpart.setText(res.getString("pod_vendpart"));
                    duedate.setText(res.getString("pod_due_date"));
                    tbqtyrcvd.setText(res.getString("pod_rcvd_qty"));
                    tbqtyord.setText(res.getString("pod_ord_qty"));
                    orddate.setText(res.getString("pod_ord_date"));
                }
                if (tbcost.getText().isEmpty()) {
                    tbcost.setText("0");
                }
             res.close();
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot find po line item");
            }
            
        //    bsmf.MainFrame.con.close();
            } // if mypart and mypo
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_ddpartActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
        ddpo.removeAllItems();
        ddpart.removeAllItems();
       if (ddvend.getSelectedItem() != null && ! ddvend.getSelectedItem().toString().isEmpty()) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            
            if (bsmf.MainFrame.con.isClosed())
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select po_nbr from po_mstr where po_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    ddpo.addItem(res.getString("po_nbr"));
                }
                ddpo.insertItemAt("", 0);
                ddpo.setSelectedIndex(0);
                res.close();
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot get PO list for this Vendor");
            }
       //     bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       }
    }//GEN-LAST:event_ddvendActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = rvdet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) rvdet.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", tbkey.getText());
    }//GEN-LAST:event_bteditActionPerformed

    private void ddpoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpoActionPerformed
       ddpart.removeAllItems();
        try {
            String mypo = "";
            if (ddpo.getItemCount() > 0) {
                mypo = ddpo.getSelectedItem().toString();
            }
            if (! mypo.toString().isEmpty()) {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            
            if (bsmf.MainFrame.con.isClosed())
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                    // at this time you cannot have the same item on the PO more than once
                res = st.executeQuery("select pod_part, pod_site from pod_mstr " +
                       " inner join po_mstr on po_nbr = pod_nbr where pod_nbr = " + "'" + mypo + "'" + ";");
                while (res.next()) {
                   ddpart.addItem(res.getString("pod_part"));
                   ddsite.setSelectedItem(res.getString("pod_site"));
                }
                res.close();
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot find po line items for this PO");
            }
            } // if mypo is not empty
            
           // bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }//GEN-LAST:event_ddpoActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "recvmaint,rv_id");
    }//GEN-LAST:event_btbrowseActionPerformed

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

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        if (! isLoad) {
        String x = BlueSeerUtils.bsformat("", tbqty.getText(), "0");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
        if (tbqty.getText().equals("0") && ! isLoad) {
            tbqty.setText("");
        }
    }//GEN-LAST:event_tbqtyFocusGained

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", tbkey.getText());
    }//GEN-LAST:event_tbkeyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnew;
    private javax.swing.JCheckBox cbautovoucher;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddloc;
    private javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox ddpo;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JTextField duedate;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel lblvendpart;
    private javax.swing.JTextField orddate;
    private javax.swing.JTable rvdet;
    private javax.swing.JTextField tbcost;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbline;
    private javax.swing.JTextField tblot;
    private javax.swing.JTextField tbpackingslip;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbqtyord;
    private javax.swing.JTextField tbqtyrcvd;
    private javax.swing.JTextField tbserial;
    // End of variables declaration//GEN-END:variables
}
