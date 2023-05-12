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
package com.blueseer.fap;

import com.blueseer.utl.OVData;
import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.dfdate;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.fap.fapData.VoucherTransaction;
import static com.blueseer.fap.fapData.updateAPVoucherStatus;
import com.blueseer.fgl.fglData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
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
import static com.blueseer.utl.BlueSeerUtils.setDateFormat;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.blueseer.vdr.venData;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
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
public class VoucherMaint extends javax.swing.JPanel implements IBlueSeerT {

                boolean isLoad = false;
                String terms = "";
                String apacct = "";
                String apcc = "";
                String apbank = "";
                double actamt = 0;
                double baseamt = 0;
                double rcvamt = 0;
                int voucherline = 0;
                String curr = "";
                String basecurr = "";
                
                 javax.swing.table.DefaultTableModel receivermodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("quantity"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("netprice"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("serial"), 
                getGlobalColumnTag("lot"), 
                getGlobalColumnTag("receiver"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("account"), 
                getGlobalColumnTag("costcenter")
            });
    javax.swing.table.DefaultTableModel vouchermodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("quantity"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("receiver"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("account"), 
                getGlobalColumnTag("costcenter")
            });
                
                
    /**
     * Creates new form ShipMaintPanel
     */
    public VoucherMaint() {
        initComponents();
        setLanguageTags(this);
    }
   
    
    public void setComponentDefaultValues() {
        
        
       isLoad = true; 
       java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdate.setDate(now);
         basecurr = OVData.getDefaultCurrency();
         terms = "";
         apacct = "";
         apcc = "";
         apbank = "";
         actamt = 0;
         rcvamt = 0;
         tbkey.setText("");
         tbinvoice.setText("");
        tbrmks.setText("");
        tbcontrolamt.setText("");
        tbrecvamt.setText("");
        tbactualamt.setText("");
        tbqty.setText("");
        lbvendor.setText("");
        lbacct.setText("");
        tbprice.setDisabledTextColor(Color.black);
        tbprice.setText("");
        
        lblstatus.setText("");
        lblstatus.setForeground(Color.black);
        
        ddsite.setForeground(Color.black);
        ddpo.removeAllItems();
        ddreceiver.removeAllItems();
       
         receivermodel.setRowCount(0);
        vouchermodel.setRowCount(0);
        receiverdet.setModel(receivermodel);
        voucherdet.setModel(vouchermodel);
        receiverdet.getTableHeader().setReorderingAllowed(false);
        voucherdet.getTableHeader().setReorderingAllowed(false);
        
        ddacct.removeAllItems();
        ArrayList<String> myaccts = fglData.getGLAcctListByType("E");
        for (String code : myaccts) {
            ddacct.addItem(code);
        }
        
            ddcc.removeAllItems();
        ArrayList<String> mycc = fglData.getGLCCList();
        for (String code : mycc) {
            ddcc.addItem(code);
        }
        ddcc.setSelectedItem(OVData.getDefaultCC());
        
        
        ddvend.removeAllItems();
        ArrayList myvend = venData.getVendMstrList();
        for (int i = 0; i < myvend.size(); i++) {
            ddvend.addItem(myvend.get(i));
        }
            ddvend.insertItemAt("", 0);
            ddvend.setSelectedIndex(0);
        
          ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        
       isLoad = false;
    }
         
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   tbactualamt.setText(currformatDouble(actamt));
                   String status = fapData.getVoucherStatus(tbkey.getText());
                   if (status.equals("x")) {
                       lblstatus.setText(getMessageTag(1083));
                       lblstatus.setForeground(Color.blue);
                       btvoid.setEnabled(false);
                       btadd.setEnabled(false);
                   }
                   if (status.equals("c")) {
                       lblstatus.setText(getMessageTag(1097));
                       lblstatus.setForeground(Color.blue);
                       btvoid.setEnabled(false);
                       btadd.setEnabled(false);
                   }
                   if (status.equals("o") || status.isBlank()) {
                       lblstatus.setText("");
                       lblstatus.setForeground(Color.black);
                   }
                   
        } else {
                   tbkey.setForeground(Color.red); 
        }
       
    }
    
    public boolean validateInput(BlueSeerUtils.dbaction x) {
        boolean b = true;
                
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "ID"));
                    tbkey.requestFocus();
                    return b;
                }
                
                if (ddvend.getSelectedItem() == null || ddvend.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "Vendor"));
                    return b;
                }
                
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "Site"));
                    return b;
                }
                
                if ( OVData.isGLPeriodClosed(dfdate.format(dcdate.getDate()))) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1035));
                    return b;
                }
                
                 if (apbank.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "bank"));
                    return b;
                }
                if (apcc.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "CC"));
                    return b;
                }
                if (apacct.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "APAccount"));
                    return b;
                }
                 if ( actamt == 0.00 ) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1036));
                    return b;
                }
                
                
                
               
        return b;
    }
    
    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
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
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask(BlueSeerUtils.dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    
    public String[] addRecord(String[] x) {
     String[] m = VoucherTransaction(OVData.getNextNbr("batch"), ddtype.getSelectedItem().toString() , createDetRecord(), createRecord(), false);
     updateReceivers(); 
     return m;
     }
    
    public String[] updateRecord(String[] x) {
     return null;  // no update available...only add and void (reverse)
     }
     
    public String[] deleteRecord(String[] x) {
     // same function used for add...but with 'true' for void as last parameter
     String[] m = VoucherTransaction(OVData.getNextNbr("batch"), ddtype.getSelectedItem().toString() , createDetRecord(), createRecord(), true);
     updateReceiversVoid(); 
     updateAPVoucherStatus(tbkey.getText(), "x");
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
                actamt = 0.00;
                res = st.executeQuery("select * from ap_mstr where ap_nbr = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                  i++;
                     tbkey.setText(res.getString("ap_nbr"));
                     dcdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("ap_effdate")));
                     tbinvoice.setText(res.getString("ap_ref"));
                     tbrmks.setText(res.getString("ap_rmks"));
                     ddvend.setSelectedItem(res.getString("ap_vend"));
                     ddsite.setSelectedItem(res.getString("ap_site"));
                     ddtype.setSelectedItem(res.getString("ap_subtype"));
                }
                
                res = st.executeQuery("select * from vod_mstr where vod_id = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                //  "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
                     vouchermodel.addRow(new Object[] { res.getString("vod_id"),
                                              res.getString("vod_rvdline"),
                                              res.getString("vod_item"),
                                              res.getString("vod_qty").replace('.',defaultDecimalSeparator),
                                              res.getString("vod_voprice").replace('.',defaultDecimalSeparator),
                                              res.getString("vod_rvdid"),
                                              res.getString("vod_rvdline"),
                                              res.getString("vod_expense_acct"),
                                              res.getString("vod_expense_cc")
                                              });
                 
                  
                  actamt += res.getDouble("vod_voprice");
               
                }
               
                if (i > 0)
                m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                setAction(m);
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
    
    public fapData.ap_mstr createRecord() {
        fapData.ap_mstr x = new fapData.ap_mstr(null, 
                "", //ap_id
                ddvend.getSelectedItem().toString(), // ap_vend, 
                tbkey.getText(), // ap_nbr
                currformatDouble(actamt).replace(defaultDecimalSeparator, '.'), // ap_amt
                currformatDouble(actamt).replace(defaultDecimalSeparator, '.'), // ap_base_amt
                setDateFormat(dcdate.getDate()), // ap_effdate
                setDateFormat(dcdate.getDate()), // ap_entdate
                setDateFormat(OVData.getDueDateFromTerms(dcdate.getDate(), terms)), // ap_duedate         
                "V", // ap_type
                tbrmks.getText(), //ap_rmks
                tbinvoice.getText(), //ap_ref
                terms, //ap_terms
                apacct, //ap_acct
                apcc, //ap_cc
                "0", //ap_applied
                "o", //ap_status
                apbank, //ap_bank
                curr, //ap_curr
                basecurr, //ap_base_curr
                tbkey.getText(), //ap_check // in this case voucher number is reference field
                "", //ap_batch
                ddsite.getSelectedItem().toString(), //ap_site
                ddtype.getSelectedItem().toString()); 
        return x;  
    }
    
    public ArrayList<fapData.vod_mstr> createDetRecord() {
        ArrayList<fapData.vod_mstr> list = new ArrayList<fapData.vod_mstr>();
         for (int j = 0; j < voucherdet.getRowCount(); j++) {
             fapData.vod_mstr x = new fapData.vod_mstr(null, 
                tbkey.getText(),
                voucherdet.getValueAt(j, 5).toString(),
                voucherdet.getValueAt(j, 6).toString(),
                voucherdet.getValueAt(j, 2).toString(),
                voucherdet.getValueAt(j, 3).toString().replace(defaultDecimalSeparator, '.'),
                voucherdet.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.'),
                dfdate.format(dcdate.getDate()),
                ddvend.getSelectedItem().toString(),
                tbinvoice.getText(), // ap_check 
                voucherdet.getValueAt(j, 7).toString(),
                voucherdet.getValueAt(j, 8).toString()
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
         luModel = DTData.getVoucherBrowseUtil(luinput.getText(),0, "vod_id");
        } else {
         luModel = DTData.getVoucherBrowseUtil(luinput.getText(),0, "ap_vend");   
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
                getClassLabelTag("lblvendor", this.getClass().getSimpleName())); 
        
        
    }

    
    // misc functions
    public void reinitreceivervariables(String myreceiver) {
       
        tbkey.setText(myreceiver);
        if (myreceiver.compareTo("") == 0) {
            btadd.setEnabled(true);

        } else {
            btadd.setEnabled(false);
        }


        tbkey.setEnabled(true);
        tbkey.setText(myreceiver);
      
        tbcontrolamt.setText("");
        receiverdet.setModel(receivermodel);
        voucherdet.setModel(vouchermodel);
        receivermodel.setRowCount(0);
        vouchermodel.setRowCount(0);
       
       

     //   cobCustNameSM.removeAllItems();
        
    }
      
    public void setvendorvariables(String vendor) {
        
        try {
     
            int i = 0;
            int d = 0;
            String uniqpo = null;
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {


                res = st.executeQuery("select vd_ap_acct, vd_ap_cc, vd_terms, vd_bank, vd_curr from vd_mstr where vd_addr = " + "'" + vendor + "'" + ";");
                while (res.next()) {
                    i++;
                   apacct = res.getString("vd_ap_acct");
                   apcc = res.getString("vd_ap_cc");
                   terms = res.getString("vd_terms");
                   apbank = res.getString("vd_bank");
                   curr = res.getString("vd_curr");
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
      
    public void getreceiverinfo(String myreceiver) {
        
        try {
             int i = 0;
            int d = 0;
            String uniqpo = null;
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                rcvamt = 0;
                res = st.executeQuery("select * from recv_det inner join recv_mstr on rv_id = rvd_id where rvd_id = " + "'" + myreceiver + "'" + ";");
                while (res.next()) {
                    // "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "serial", "lot", "RecvID", "RecvLine", "Acct", "CC"
                  receivermodel.addRow(new Object[]{res.getString("rvd_item"), res.getString("rvd_po"), 
                      res.getString("rvd_poline"), bsFormatDouble((res.getDouble("rvd_qty") - res.getDouble("rvd_voqty"))).replace('.',defaultDecimalSeparator), res.getString("rvd_listprice").replace('.',defaultDecimalSeparator),
                   res.getString("rvd_disc").replace('.',defaultDecimalSeparator), res.getString("rvd_netprice").replace('.',defaultDecimalSeparator), res.getString("rvd_loc"),
                  res.getString("rvd_serial"), res.getString("rvd_lot"), res.getString("rvd_id"), res.getString("rvd_rline"),
                  res.getString("rv_ap_acct"), res.getString("rv_ap_cc")});
                  rcvamt += res.getDouble("rvd_netprice") * res.getDouble("rvd_qty");
               
                d++;
                }
                tbrecvamt.setText(currformatDouble(rcvamt).replace('.',defaultDecimalSeparator));
                receiverdet.setModel(receivermodel);

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

    public void getvoucherinfo(String voucher) {
        
        try {
             int i = 0;
            int d = 0;
            String uniqpo = null;
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                rcvamt = 0;
                res = st.executeQuery("select * from vod_mstr inner join ap_mstr on ap_nbr = vod_id inner join recv_det on rvd_id = vod_rvdid and rvd_poline = vod_rvdline " +
                        " where vod_id = " + "'" + voucher + "'" + ";");
                while (res.next()) {
                  vouchermodel.addRow(new Object[]{res.getString("rvd_po"), 
                      res.getString("vod_rvdline"), res.getString("vod_item"), res.getString("vod_qty").replace('.',defaultDecimalSeparator), res.getString("vod_voprice").replace('.',defaultDecimalSeparator),
                   res.getString("vod_rvdid"), res.getString("vod_rvdline"), res.getString("vod_expense_acct"),
                  res.getString("vod_expense_cc") }) ;
                d++;
                }
                if (d > 0) {
                   tbkey.setEnabled(false);
                    btnew.setEnabled(false);
                }
                tbkey.setText(voucher);
                tbrecvamt.setText(currformatDouble(rcvamt));
                receiverdet.setModel(receivermodel);

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
      
    public void updateReceivers() {
        
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            double amt = 0;
            try {
                for (int j = 0; j < voucherdet.getRowCount(); j++) {
                amt = bsParseDouble(voucherdet.getValueAt(j, 3).toString());   
                double voqty = 0;
                double rvqty = 0;
                double rvdvoqty = 0;
                String status = "0";
                
                res = st.executeQuery("select rvd_voqty, rvd_qty from recv_det " 
                         + " where rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                        + " AND rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                        );
                while (res.next()) {
                    voqty = res.getDouble("rvd_voqty");
                    rvqty = res.getDouble("rvd_qty");
                    if ((voqty + amt) >= rvqty) {
                        status = "1";
                    }     
                }
                res.close();        
                        
                   rvdvoqty = voqty + amt;
                   
                        if (ddtype.getSelectedItem().toString().equals("Receipt")) {
                           if (bsmf.MainFrame.dbtype.equals("sqlite")) { 
                            st.executeUpdate("update recv_det  "
                            + " set rvd_voqty =  " + "'" + currformatDouble(rvdvoqty).replace(defaultDecimalSeparator, '.') + "'" + ","
                            + " rvd_status = " + "'" + status + "'"
                            + " where rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                            + " AND rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                            );
                           } else {
                            st.executeUpdate("update recv_det as r1 inner join recv_det as r2 "
                            + " set r1.rvd_voqty = r2.rvd_voqty + " +  "'" + currformatDouble(amt).replace(defaultDecimalSeparator, '.') + "'" + ","
                            + " r1.rvd_status = case when r1.rvd_qty <= ( r2.rvd_voqty + " + "'" + currformatDouble(amt).replace(defaultDecimalSeparator, '.') + "'" +  ") then '1' else '0' end " 
                            + " where r1.rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                            + " AND r1.rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                            + " AND r2.rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                            + " AND r2.rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                            );   
                           }
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
    
    public void updateReceiversVoid() {
        
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            double amt = 0;
            try {
                for (int j = 0; j < voucherdet.getRowCount(); j++) {
                amt = bsParseDouble(voucherdet.getValueAt(j, 3).toString());   
                double voqty = 0;
                double rvqty = 0;
                double rvdvoqty = 0;
                String status = "0";
                
                res = st.executeQuery("select rvd_voqty, rvd_qty from recv_det " 
                         + " where rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                        + " AND rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                        );
                while (res.next()) {
                    voqty = res.getDouble("rvd_voqty");
                    rvqty = res.getDouble("rvd_qty");
                   // if ((voqty + amt) >= rvqty) {
                   //     status = "1";
                    //}     
                }
                res.close();        
                        
                   rvdvoqty = voqty - amt; // back it out with subtraction
                   
                        if (ddtype.getSelectedItem().toString().equals("Receipt")) {
                          st.executeUpdate("update recv_det  "
                            + " set rvd_voqty =  " + "'" + currformatDouble(rvdvoqty).replace(defaultDecimalSeparator, '.') + "'" + ","
                            + " rvd_status = " + "'" + status + "'"
                            + " where rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                            + " AND rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                            );
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
    
      
    public void setType(String type) {
          if (type.equals("Receipt")) {
              ddpo.setEnabled(true);
              ddreceiver.setEnabled(true);
              tbitemservice.setEnabled(false);
              tbprice.setEnabled(false);
              btaddall.setEnabled(true);
              tbrecvamt.setEnabled(true);
          } else {
              btaddall.setEnabled(false);
              tbrecvamt.setEnabled(false);
              ddpo.setEnabled(false);
              ddreceiver.setEnabled(false);
              tbitemservice.setEnabled(true);
              tbprice.setEnabled(true);
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
        tbcontrolamt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        lblreceiver = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        voucherdet = new javax.swing.JTable();
        ddvend = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        ddreceiver = new javax.swing.JComboBox();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        receiverdet = new javax.swing.JTable();
        ddpo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        tbinvoice = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        btaddall = new javax.swing.JButton();
        tbrecvamt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbitemservice = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jLabel37 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        lbvendor = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        ddacct = new javax.swing.JComboBox<>();
        lbacct = new javax.swing.JLabel();
        btvoid = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        ddcc = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Voucher Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("Voucher Nbr");
        jLabel24.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel36.setText("Vendor");
        jLabel36.setName("lblvendor"); // NOI18N

        lblreceiver.setText("Receivers");
        lblreceiver.setName("lblreceivers"); // NOI18N

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

        voucherdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(voucherdet);

        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Del Item");
        btdeleteitem.setName("btdeleteitem"); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        ddreceiver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddreceiverActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("Control Amt");
        jLabel27.setName("lblcontrol"); // NOI18N

        jLabel35.setText("VoucherDate");
        jLabel35.setName("lblvoucherdate"); // NOI18N

        receiverdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(receiverdet);

        ddpo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpoActionPerformed(evt);
            }
        });

        jLabel1.setText("PO");
        jLabel1.setName("lblpo"); // NOI18N

        jLabel2.setText("Invoice");
        jLabel2.setName("lblinvoice"); // NOI18N

        jLabel28.setText("Actual Amt");
        jLabel28.setName("lblactual"); // NOI18N

        btaddall.setText("Add All");
        btaddall.setName("btaddall"); // NOI18N
        btaddall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddallActionPerformed(evt);
            }
        });

        jLabel3.setText("Receiver Total");
        jLabel3.setName("lblreceivertotal"); // NOI18N

        jLabel4.setText("Rmks");
        jLabel4.setName("lblremarks"); // NOI18N

        jLabel5.setText("Item/Service");
        jLabel5.setName("lblitem"); // NOI18N

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel6.setText("Price");
        jLabel6.setName("lblprice"); // NOI18N

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Receipt", "Expense" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        jLabel37.setText("Type");
        jLabel37.setName("lbltype"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Qty");
        jLabel7.setName("lblqty"); // NOI18N

        jLabel8.setText("CC");
        jLabel8.setName("lblcc"); // NOI18N

        jLabel9.setText("ExpenseAcct");
        jLabel9.setName("lblacct"); // NOI18N

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        jLabel10.setText("Site");
        jLabel10.setName("lblsite"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        ddacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddacctActionPerformed(evt);
            }
        });

        btvoid.setText("Void");
        btvoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btvoidActionPerformed(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(btvoid)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btadd))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(9, 9, 9)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jLabel35)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(tbinvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel10)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(121, 121, 121)
                                                        .addComponent(jLabel4)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(0, 2, Short.MAX_VALUE))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(lbvendor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(jLabel1)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(lblreceiver)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(ddreceiver, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jLabel28)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(5, 5, 5))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel24)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(13, 13, 13)
                                                .addComponent(btnew)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(btclear)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel27)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 348, Short.MAX_VALUE))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(0, 0, Short.MAX_VALUE))))))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbrecvamt, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btaddall))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeleteitem))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(250, 250, 250)))))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24)
                        .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)
                        .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28)
                        .addComponent(btclear))
                    .addComponent(btlookup))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(ddreceiver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(lblreceiver)
                            .addComponent(jLabel36)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbvendor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel35)
                    .addComponent(tbinvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddall)
                    .addComponent(tbrecvamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdeleteitem)
                    .addComponent(btadditem)
                    .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btvoid))
                .addGap(0, 0, 0))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("voucher"); 
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        // Pattern p = Pattern.compile("\\d\\.\\d\\d");
      //  Matcher m = p.matcher(tbprice.getText());
       // receiverdet  "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "serial", "lot", "RecvID", "RecvLine", "Acct", "CC"
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
        if (ddtype.getSelectedItem().toString().equals(("Receipt"))) {
        int[] rows = receiverdet.getSelectedRows();
            for (int i : rows) {
                actamt += bsParseDouble(receiverdet.getModel().getValueAt(i,3).toString()) * 
                          bsParseDouble(receiverdet.getModel().getValueAt(i,6).toString());

               vouchermodel.addRow(new Object[] { receiverdet.getModel().getValueAt(i, 1),
                                                  receiverdet.getModel().getValueAt(i, 2),
                                                  receiverdet.getModel().getValueAt(i, 0),
                                                  receiverdet.getModel().getValueAt(i, 3),
                                                  receiverdet.getModel().getValueAt(i, 6),
                                                  receiverdet.getModel().getValueAt(i, 10),
                                                  receiverdet.getModel().getValueAt(i, 11),
                                                  receiverdet.getModel().getValueAt(i, 12),
                                                  receiverdet.getModel().getValueAt(i, 13)
                                                  });
            }
        } else {
            voucherline++;
            actamt += bsParseDouble(tbqty.getText()) * 
                          bsParseDouble(tbprice.getText());
            vouchermodel.addRow(new Object[] { "", voucherline,
                                                  tbitemservice.getText(),
                                                  tbqty.getText(),
                                                  tbprice.getText(),
                                                  "expense",
                                                  "0",
                                                  ddacct.getSelectedItem().toString(),
                                                  ddcc.getSelectedItem().toString()
                                                  });
        }
        
        tbactualamt.setText(currformatDouble(actamt));
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(BlueSeerUtils.dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
       
      if (! isLoad) { 
           ddreceiver.removeAllItems();
           ddpo.removeAllItems();
           receivermodel.setRowCount(0);
        
        if (ddvend.getSelectedItem() != null && ! ddvend.getSelectedItem().toString().isEmpty() )
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
                res = st.executeQuery("select vd_name from vd_mstr where vd_addr = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbvendor.setText(res.getString("vd_name"));
                }
                res = st.executeQuery("select po_nbr from po_mstr where po_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    ddpo.addItem(res.getString("po_nbr"));
                }
                ddpo.insertItemAt("", 0);
                ddpo.setSelectedIndex(0);
                setvendorvariables(ddvend.getSelectedItem().toString());
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
      }// not isLoad
    }//GEN-LAST:event_ddvendActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = voucherdet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
             actamt -= bsParseDouble(voucherdet.getModel().getValueAt(i,3).toString()) * bsParseDouble(voucherdet.getModel().getValueAt(i,4).toString());
            ((javax.swing.table.DefaultTableModel) voucherdet.getModel()).removeRow(i);
           voucherline--;
        }
        tbactualamt.setText(currformatDouble(actamt));
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void ddreceiverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddreceiverActionPerformed
       if (ddreceiver.getSelectedItem() == null)
           return;
       
        receivermodel.setRowCount(0);
       if ( ddreceiver.getItemCount() != 0 && ! ddreceiver.getSelectedItem().toString().isEmpty())
       getreceiverinfo(ddreceiver.getSelectedItem().toString());
    }//GEN-LAST:event_ddreceiverActionPerformed

    private void ddpoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpoActionPerformed
       if (ddpo.getSelectedItem() == null)
           return;
        ddreceiver.removeAllItems();
        receivermodel.setRowCount(0);
   //    if (! ddvend.getSelectedItem().toString().isEmpty() && ! ddpo.getSelectedItem().toString().isEmpty() && ! ddpo.equals(null) && ! ddvend.equals(null))
        if ( ddpo.getSelectedItem() != null)
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

                res = st.executeQuery("select distinct(rvd_id) from recv_det inner join recv_mstr on " +
                        " rvd_id = rv_id where rv_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + 
                        " and rvd_po = " + "'" + ddpo.getSelectedItem().toString() + "'" + " and rvd_status = '0' " + ";");
                while (res.next()) {
                    ddreceiver.addItem(res.getString("rvd_id"));
                }
                ddreceiver.insertItemAt("", 0);
                ddreceiver.setSelectedIndex(0);
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
    }//GEN-LAST:event_ddpoActionPerformed

    private void btaddallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddallActionPerformed
         for (int i = 0; i < receiverdet.getRowCount(); i++) {
            actamt += bsParseDouble(receiverdet.getModel().getValueAt(i,3).toString()) * bsParseDouble(receiverdet.getModel().getValueAt(i,6).toString());
            
           vouchermodel.addRow(new Object[] { receiverdet.getModel().getValueAt(i, 1),
                                              receiverdet.getModel().getValueAt(i, 2),
                                              receiverdet.getModel().getValueAt(i, 0),
                                              receiverdet.getModel().getValueAt(i, 3),
                                              receiverdet.getModel().getValueAt(i, 6),
                                              receiverdet.getModel().getValueAt(i, 10),
                                              receiverdet.getModel().getValueAt(i, 11),
                                              receiverdet.getModel().getValueAt(i, 12),
                                              receiverdet.getModel().getValueAt(i, 13)
                                              });
        }
        tbactualamt.setText(currformatDouble(actamt));
    }//GEN-LAST:event_btaddallActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
       setType(ddtype.getSelectedItem().toString());
    }//GEN-LAST:event_ddtypeActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddsiteActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
         if (! btadd.isEnabled())
        executeTask(BlueSeerUtils.dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
         BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
        if (ddacct.getSelectedItem() != null && ! isLoad )
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

                res = st.executeQuery("select ac_desc from ac_mstr where ac_id = " + "'" + ddacct.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbacct.setText(res.getString("ac_desc"));
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
    }//GEN-LAST:event_ddacctActionPerformed

    private void tbpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusGained
        if (tbprice.getText().equals("0")) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
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
    }//GEN-LAST:event_tbpriceFocusLost

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
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
    }//GEN-LAST:event_tbqtyFocusLost

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
        if (tbqty.getText().equals("0")) {
            tbqty.setText("");
        }
    }//GEN-LAST:event_tbqtyFocusGained

    private void btvoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btvoidActionPerformed
         if (! validateInput(BlueSeerUtils.dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.delete, new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btvoidActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddall;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btvoid;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddacct;
    private javax.swing.JComboBox<String> ddcc;
    private javax.swing.JComboBox ddpo;
    private javax.swing.JComboBox ddreceiver;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbacct;
    private javax.swing.JLabel lblreceiver;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lbvendor;
    private javax.swing.JTable receiverdet;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbcontrolamt;
    private javax.swing.JTextField tbinvoice;
    private javax.swing.JTextField tbitemservice;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbrecvamt;
    private javax.swing.JTextField tbrmks;
    private javax.swing.JTable voucherdet;
    // End of variables declaration//GEN-END:variables
}
