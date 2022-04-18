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

package com.blueseer.inv;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
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
import com.blueseer.vdr.venData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author vaughnte
 */
public class QPRMaint extends javax.swing.JPanel implements IBlueSeer {

    // global variable declarations
            boolean isLoad = false;
            String sitename = "";
            String siteaddr = "";
            String sitephone = "";
            String sitecitystatezip = "";
            String sqename = "";
            String sqephone = "";
            String sqefax = "";
            String sqeemail = "";
    
    // global datatablemodel declarations       

    public QPRMaint() {
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
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dccreate.setDate(now);
        dcupdate.setDate(now);
        dcclose.setDate(null);
        
        tbkey.setText("");
        tbQtyRejected.setText("0");
        tbNumSuspectCont.setText("0");
        tbTotalQty.setText("0");
        tbChargeBack.setText("0.00");
        tbkey.setText("");
        tbOriginator.setText(bsmf.MainFrame.userid);
        tbContact.setText("");
        cbQPR.setSelected(false);
        cbInforOnly.setSelected(false);
        cbSendSupp.setSelected(false);
        cbSort.setSelected(false);
        cbRework.setSelected(false);
        cbScrapped.setSelected(false);
        cbDeviation.setSelected(false);
        cbLine.setSelected(false);
        cbReceiving.setSelected(false);
        cbCustomer.setSelected(false);
        cbEngineering.setSelected(false);
        cbOther.setSelected(false);
        tbOtherReason.setText("");
        cbInternal.setSelected(false);
        cbExternal.setSelected(false);
        tbDept.setText("");
        tbDeviationNbr.setText("");
        
        tbPartDesc.setText("");
        taIssue.setText("");
        taHistory.setText("");
        taComments.setText(""); 
       
         lbstatus.setText("");
        lbstatus.setForeground(Color.black);
        
        lbvendname.setText("");
        ddvend.removeAllItems();
          ArrayList<String> myvends = venData.getVendMstrList();
          for (int i = 0; i < myvends.size(); i++) {
            ddvend.addItem(myvends.get(i));
          }
          ddvend.insertItemAt("", 0);
          ddvend.setSelectedIndex(0);
        
        ArrayList<String> items = invData.getItemMasterAlllist();
        dditem.removeAllItems();
        for (int i = 0; i < items.size(); i++) {
            dditem.addItem(items.get(i));
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
                    if (dcclose.getDate() != null) {
                             lbstatus.setText("This QPR is closed.");
                             lbstatus.setForeground(Color.blue);
                            // setPanelComponentState(this, false);
                             btnew.setEnabled(true);
                             btlookup.setEnabled(true);
                             btprint.setEnabled(true);
                             btadd.setEnabled(false);
                             btupdate.setEnabled(false);
                             btdelete.setEnabled(false);
                            
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
                
                if (taComments.getText().length() > 400) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1075));
                    taComments.requestFocus();
                    return b;
                }
                
                if (taIssue.getText().length() > 400) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1076));
                    taIssue.requestFocus();
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
     String[] m = new String[2];
     
     try {

            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
               
                int i = 0;

                    res = st.executeQuery("SELECT qual_id FROM  qual_mstr where qual_id = " + "'" + x[0] + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into qual_mstr "
                        + "(qual_id, qual_userid, qual_date_crt,"
                        + "qual_date_upd, qual_date_cls, qual_originator,"
                        + "qual_vend, qual_vend_name, qual_vend_contact, "
                        + "qual_qpr, qual_infor, qual_sendsupp, qual_sort,"
                        + "qual_rework, qual_scrap, qual_dev, qual_dev_nbr,"
                        + "qual_src_line, qual_line_dept, qual_src_recv,"  
                        + "qual_src_cust, qual_src_eng, qual_src_oth,"
                        + "qual_src_oth_desc, qual_int_sup, qual_ext_sup,"
                        + "qual_part, qual_part_desc, qual_qty_rej, qual_qty_susp,"
                        + "qual_qty_tot_def, qual_desc_iss, qual_desc_fin_hist,"
                        + "qual_desc_sqe_comt, qual_tot_charge, qual_dec1, qual_date1, qual_int1) "
                        + " values ( " + "'" + tbkey.getText().toString() + "'" + ","
                        + "'" + tbOriginator.getText() + "'" + ","
                        + "'" + dfdate.format(dccreate.getDate()) + "'" + ","
                        + "'" + dfdate.format(dccreate.getDate()) + "'" + ","
                        + "'" + "" + "'" + "," // close date
                        + "'" + tbOriginator.getText() + "'" + ","
                        + "'" + ddvend.getSelectedItem().toString() + "'" + ","
                        + "'" + lbvendname.getText().replace("'", "''") + "'" + ","
                        + "'" + tbContact.getText().replace("'", "''") + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbQPR.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbInforOnly.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbSendSupp.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbSort.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbRework.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbScrapped.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbDeviation.isSelected()) + "'" + ","
                        + "'" + tbDeviationNbr.getText().replace("'", "\\'") + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbLine.isSelected()) + "'" + ","
                        + "'" + tbDept.getText().replace("'", "''") + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbReceiving.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbCustomer.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbEngineering.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbOther.isSelected()) + "'" + ","
                        + "'" + tbOtherReason.getText().replace("'", "''") + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbInternal.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbExternal.isSelected()) + "'" + ","
                        + "'" + dditem.getSelectedItem().toString() + "'" + ","
                        + "'" + tbPartDesc.getText().replace("'", "''") + "'" + ","
                        + "'" + Integer.parseInt(tbQtyRejected.getText().toString()) + "'" + ","
                        + "'" + Integer.parseInt(tbNumSuspectCont.getText().toString()) + "'" + ","
                        + "'" + Integer.parseInt(tbTotalQty.getText().toString()) + "'" + ","
                        + "'" + taIssue.getText().replace("'", "''") + "'" + ","
                        + "'" + taHistory.getText().replace("'", "''") + "'" + ","
                        + "'" + taComments.getText().replace("'", "''") + "'" + ","
                        + "'" + Float.parseFloat(tbChargeBack.getText().replace("'", "''").toString()) + "'" + ","
                        + null + "," + null + "," + null
                        + ")"
                        + ";");
                        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                    } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists}; 
                    }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                 m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
             m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1020, Thread.currentThread().getStackTrace()[1].getMethodName())};
        }
     
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     
     try {
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            boolean proceed = true;
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            String closedate = "";
            if (dcclose.getDate() != null) {
                closedate = dfdate.format(dcclose.getDate());
            }
            try {
                    st.executeUpdate("update qual_mstr "
                        + "set qual_qpr = " + "'" + BlueSeerUtils.boolToInt(cbQPR.isSelected()) + "'" + ","
                        + "qual_infor = " + "'" + BlueSeerUtils.boolToInt(cbInforOnly.isSelected()) + "'" + ","
                        + "qual_date_upd = " + "'" + dfdate.format(dcupdate.getDate()) + "'" + ","
                        + "qual_date_cls = " + "'" + closedate + "'" + ","
                        + "qual_sendsupp = " + "'" + BlueSeerUtils.boolToInt(cbSendSupp.isSelected()) + "'" + ","
                        + "qual_sort = " + "'" + BlueSeerUtils.boolToInt(cbSort.isSelected()) + "'" + ","
                        + "qual_rework = " + "'" + BlueSeerUtils.boolToInt(cbRework.isSelected()) + "'" + ","
                        + "qual_scrap = " + "'" + BlueSeerUtils.boolToInt(cbScrapped.isSelected()) + "'" + ","
                        + "qual_dev = " + "'" + BlueSeerUtils.boolToInt(cbDeviation.isSelected()) + "'" + ","
                        + "qual_src_line = " + "'" + BlueSeerUtils.boolToInt(cbLine.isSelected()) + "'" + ","
                        + "qual_src_recv = " + "'" + BlueSeerUtils.boolToInt(cbReceiving.isSelected()) + "'" + ","
                        + "qual_src_cust = " + "'" + BlueSeerUtils.boolToInt(cbCustomer.isSelected()) + "'" + ","
                        + "qual_src_eng = " + "'" + BlueSeerUtils.boolToInt(cbEngineering.isSelected()) + "'" + ","
                        + "qual_src_oth = " + "'" + BlueSeerUtils.boolToInt(cbOther.isSelected()) + "'" + ","
                        + "qual_src_oth_desc = " + "'" + tbOtherReason.getText() + "'" + ","
                        + "qual_int_sup = " + "'" + BlueSeerUtils.boolToInt(cbInternal.isSelected()) + "'" + ","
                        + "qual_ext_sup = " + "'" + BlueSeerUtils.boolToInt(cbExternal.isSelected()) + "'" + ","
                        + "qual_vend_name = " + "'" + lbvendname.getText().replace("'", "''") + "'" + ","
                        + "qual_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + ","
                        + "qual_vend_contact = " + "'" + tbContact.getText().replace("'", "''") + "'" + ","
                        + "qual_line_dept = " + "'" + tbDept.getText().replace("'", "\\'") + "'" + ","
                        + "qual_dev_nbr = " + "'" + tbDeviationNbr.getText().replace("'", "\\'") + "'" + ","
                        + "qual_part = " + "'" + dditem.getSelectedItem().toString() + "'" + ","
                        + "qual_part_desc = " + "'" + tbPartDesc.getText().replace("'", "\\'") + "'" + ","
                        + "qual_qty_rej = " + "'" + Integer.parseInt(tbQtyRejected.getText().toString()) + "'" + ","
                        + "qual_qty_susp = " + "'" + Integer.parseInt(tbNumSuspectCont.getText().toString()) + "'" + ","
                        + "qual_qty_tot_def = " + "'" + Integer.parseInt(tbTotalQty.getText().toString()) + "'" + ","
                        + "qual_tot_charge = " + "'" + Float.parseFloat(tbChargeBack.getText().toString()) + "'" + ","
                        + "qual_desc_iss = " + "'" + taIssue.getText().replace("'", "\\'") + "'" + ","
                        + "qual_desc_fin_hist = " + "'" + taHistory.getText().replace("'", "\\'") + "'" + ","
                        + "qual_desc_sqe_comt = " + "'" + taComments.getText().replace("'", "\\'") + "'"
                        + " where qual_id = " + "'" + tbkey.getText().replace("'", "\\'").toString() + "'"
                        + " ;");
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
               
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};  
            } finally {
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1020, Thread.currentThread().getStackTrace()[1].getMethodName())};
        }
     
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            try {
                   int i = st.executeUpdate("delete from bk_mstr where bk_id = " + "'" + x[0] + "'" + ";");
                    if (i > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    } else {
                    m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};    
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};  
            } finally {
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1020, Thread.currentThread().getStackTrace()[1].getMethodName())};
        }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
       
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                res = st.executeQuery("select * from qual_mstr where qual_id = " + "'" + x[0] + "'");
                while (res.next()) {
                    i++;
                    dccreate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("qual_date_crt")));
                    dcupdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("qual_date_upd")));
                    if (! res.getString("qual_date_cls").isEmpty()) {
                    dcclose.setDate(bsmf.MainFrame.dfdate.parse(res.getString("qual_date_cls")));
                    } else {
                        dcclose.setDate(null);
                    }
                    tbQtyRejected.setText(res.getString("qual_qty_rej"));
                    tbNumSuspectCont.setText(res.getString("qual_qty_susp"));
                    tbTotalQty.setText(res.getString("qual_qty_tot_def"));
                    tbChargeBack.setText(res.getString("qual_tot_charge"));
                    tbOriginator.setText(res.getString("qual_originator"));
                    tbContact.setText(res.getString("qual_vend_contact"));
                    ddvend.setSelectedItem(res.getString("qual_vend"));
                    cbQPR.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_qpr")));
                    cbInforOnly.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_infor")));
                    cbSendSupp.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_sendsupp")));
                    cbSort.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_sort")));
                    cbRework.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_rework")));
                    cbScrapped.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_scrap")));
                    cbDeviation.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_dev")));
                    cbLine.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_line")));
                    cbReceiving.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_recv")));
                    cbCustomer.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_cust")));
                    cbEngineering.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_eng")));
                    cbOther.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_oth")));
                    tbOtherReason.setText(res.getString("qual_src_oth_desc"));
                    cbInternal.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_int_sup")));
                    cbExternal.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_ext_sup")));
                    tbDept.setText(res.getString("qual_line_dept"));
                    tbDeviationNbr.setText(res.getString("qual_dev_nbr"));
                    dditem.setSelectedItem(res.getString("qual_part"));
                    tbPartDesc.setText(res.getString("qual_part_desc"));
                    taIssue.setText(res.getString("qual_desc_iss"));
                    taHistory.setText(res.getString("qual_desc_fin_hist"));
                    taComments.setText(res.getString("qual_desc_sqe_comt"));
                    tbkey.setText(x[0]);
                    // jcbsupervisor_empmast.setSelectedIndex(0);
                }
               
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1020, Thread.currentThread().getStackTrace()[1].getMethodName())};  
        }
      return m;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getQPRBrowseUtil(luinput.getText(),0, "qual_part");
        } else {
         luModel = DTData.getQPRBrowseUtil(luinput.getText(),0, "qual_part_desc");   
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
      
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), getClassLabelTag("lblitemdesc", this.getClass().getSimpleName())); 
         
        
        
    }

   
    
    
    // custom funcs
    public void printPDF() {
         getSiteAddress(OVData.getDefaultSite());
        try {
            final PrinterJob pjob = PrinterJob.getPrinterJob();
            pjob.setJobName("Graphics Demo Printout");
            pjob.setCopies(1);
            pjob.setPrintable(new Printable() {
                private boolean rootPaneCheckingEnabled;
                public int print(Graphics pg, PageFormat pf, int pageNum) {
                    if (pageNum > 0) // we only print one page
                    {
                        return Printable.NO_SUCH_PAGE; // ie., end of job
                    }
                    pg.setFont(new Font("TimesRoman", Font.PLAIN, 12));

                    
                    
                    pg.drawString(sitename, 50, 50);
                    pg.drawString(siteaddr, 50, 60);
                    pg.drawString(sitecitystatezip, 50, 70);

                    pg.drawString("Complaint #: ", 500, 50);
                    pg.drawString(tbkey.getText(), 505, 70);
                    pg.draw3DRect(500, 55, 60, 20, rootPaneCheckingEnabled);

                    pg.setFont(new Font("TimesRoman", Font.BOLD, 18));
                    pg.drawString("Quality Problem Report", 200, 90);

                    pg.setFont(new Font("TimesRoman", Font.PLAIN, 12));
                    pg.drawString("Date", 50, 110);
                    pg.drawString(bsmf.MainFrame.dfdate.format(dccreate.getDate()), 55, 138);
                    pg.drawString("Originator", 140, 110);
                    pg.drawString(tbOriginator.getText(), 145, 138);
                    pg.drawString("Supplier", 260, 110);
                    pg.drawString(ddvend.getSelectedItem().toString(), 265, 138);
                    pg.drawString("Supplier Contact", 400, 110);
                    pg.drawString(tbContact.getText(), 405, 138);
                    pg.draw3DRect(50, 120, 60, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(140, 120, 110, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(260, 120, 110, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(400, 120, 110, 20, rootPaneCheckingEnabled);

                    pg.setFont(new Font("TimesRoman", Font.PLAIN, 8));
                    pg.draw3DRect(50, 160, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbQPR.isSelected()), 52, 168);
                    pg.drawString("QPR (8-D Required)", 80, 170);
                    pg.draw3DRect(50, 180, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbInforOnly.isSelected()), 52, 188);
                    pg.drawString("Infor Only (No 8-D Required)", 80, 190);
                    pg.drawString("Disposition of Nonbsmf.MainFrame.conformance", 50, 230);
                    pg.draw3DRect(50, 240, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbSendSupp.isSelected()), 52, 248);
                    pg.drawString("Send Back to Supplier", 80, 250);
                    pg.draw3DRect(50, 260, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbSort.isSelected()), 52, 268);
                    pg.drawString("Sort", 80, 270);
                    pg.draw3DRect(50, 280, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbRework.isSelected()), 52, 288);
                    pg.drawString("Rework", 80, 290);
                    pg.draw3DRect(50, 300, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbScrapped.isSelected()), 52, 308);
                    pg.drawString("Scrapped", 80, 310);
                    pg.draw3DRect(50, 320, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbDeviation.isSelected()), 52, 328);
                    pg.drawString("Deviation # ", 80, 330);
                    pg.draw3DRect(130, 320, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbDeviationNbr.getText(), 132, 328);
                    pg.drawString("Description of Issue", 50, 350);
                    pg.draw3DRect(50, 365, 500, 50, rootPaneCheckingEnabled);
                    
                    int count = taIssue.getText().length();
                    int loopnbr = count / 100;
                    int pp = 0;
                                     
                    if (count > 100) {
                        for (int p = 0; p < loopnbr; p++) {
                        pg.drawString(taIssue.getText().substring((p * 100), ((p + 1) * 100)), 54, 380 + (p * 10));
                        pp = p;
                       // pg.drawString(taComments.getText().substring(100,count), 54, 660);
                        }
                        pp = pp + 1;
                        pg.drawString(taIssue.getText().substring((pp * 100)), 54, 380 + (pp * 10));
                    } else
                        pg.drawString(taIssue.getText(), 54, 380); 
                    
                    
                    pg.draw3DRect(200, 160, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbLine.isSelected()), 202, 168);
                    pg.drawString("Line", 230, 170);
                    pg.drawString("Dept#", 250, 170);
                    pg.drawString(tbDept.getText(), 285, 170);
                    pg.draw3DRect(280, 160, 60, 15, rootPaneCheckingEnabled);
                    pg.draw3DRect(200, 180, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbReceiving.isSelected()), 202, 188);
                    pg.drawString("Receiving Inspection", 230, 190);
                    pg.draw3DRect(200, 200, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbCustomer.isSelected()), 202, 208);
                    pg.drawString("Customer", 230, 210);
                    pg.draw3DRect(200, 220, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbEngineering.isSelected()), 202, 228);
                    pg.drawString("Engineering", 230, 230);
                    pg.draw3DRect(200, 240, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbOther.isSelected()), 202, 248);
                    pg.drawString("Other", 230, 250);
                    pg.draw3DRect(255, 240, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbOtherReason.getText(), 265, 250);

                    pg.draw3DRect(400, 160, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbInternal.isSelected()), 402, 168);
                    pg.drawString("Internal Supplier", 430, 170);
                    pg.draw3DRect(400, 180, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbExternal.isSelected()), 402, 188);
                    pg.drawString("External Supplier", 430, 190);

                    pg.drawString("Part Number", 430, 210);
                    pg.draw3DRect(430, 212, 100, 15, rootPaneCheckingEnabled);
                    pg.drawString(dditem.getSelectedItem().toString(), 435, 225);
                    pg.drawString("Part Desc", 430, 235);
                    pg.draw3DRect(430, 237, 100, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbPartDesc.getText(), 435, 251);

                    pg.drawString("Quantity Rejected", 380, 270);
                    pg.draw3DRect(460, 260, 80, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbQtyRejected.getText(), 470, 270);
                    pg.drawString("Number of Suspect Containers", 340, 290);
                    pg.draw3DRect(460, 280, 80, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbNumSuspectCont.getText(), 470, 290);
                    pg.drawString("Total Qty Found Defective", 350, 310);
                    pg.draw3DRect(460, 300, 80, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbTotalQty.getText(), 470, 310);

                    pg.drawString("Quality Problem Report requiring 8-D submission must be submitted per the following Timeline:", 50, 435);
                    pg.drawString("1. Initial Response including bsmf.MainFrame.containment due within 24 hours.", 80, 445);
                    pg.drawString("2. Root cause analysis due within 5 work days.", 80, 455);
                    pg.drawString("3. Corrective action plan due within 10 work days from QPR issue date with projected implementation dates.", 80, 465);
                    pg.drawString("4. Final 8-D submission including actual correction action implementation dates and validation methods identified within 30 days", 80, 475);
                    pg.drawString("   of QPR issue date.", 80, 485);

                    pg.drawString("Please contact either of the following if you require additional information:", 50, 495);
                    pg.drawString("SQE: " + sqename + "  PH:  " + sqephone + "  Fax:  " + sqefax + "  Email:  " + sqeemail , 50, 505);
                    pg.drawString("                                                                                          ", 50, 515);

                    pg.drawString("ChargeBack/Debit Memo History", 50, 535);
                    pg.draw3DRect(50, 550, 500, 50, rootPaneCheckingEnabled);
                    pg.drawString(taHistory.getText(), 54, 565);
                    pg.drawString("Total Chargeback for this QPR: ", 350, 620);
                    pg.draw3DRect(470, 610, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbChargeBack.getText(), 490, 618);

                    pg.drawString("SQE comments", 50, 620);
                    pg.draw3DRect(50, 635, 500, 50, rootPaneCheckingEnabled);
                    
                    count = taComments.getText().length();
                    loopnbr = count / 100;
                    pp = 0;
                                     
                    if (count > 100) {
                        for (int p = 0; p < loopnbr; p++) {
                        pg.drawString(taComments.getText().substring((p * 100), ((p + 1) * 100)), 54, 650 + (p * 10));
                        pp = p;
                       // pg.drawString(taComments.getText().substring(100,count), 54, 660);
                        }
                        pp = pp + 1;
                        pg.drawString(taComments.getText().substring((pp * 100)), 54, 650 + (pp * 10));
                    } else
                        pg.drawString(taComments.getText(), 54, 650); 
                    
                    String closedate = "";
                    if (dcclose.getDate() != null) {
                        closedate = bsmf.MainFrame.dfdate.format(dcclose.getDate());
                    }
                    pg.drawString("Date Last Updated: ", 350, 700);
                    pg.draw3DRect(350, 705, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(bsmf.MainFrame.dfdate.format(dcupdate.getDate()), 355, 717);
                    pg.drawString("Date Closed: ", 450, 700);
                    pg.draw3DRect(450, 705, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(closedate, 455, 717);

                    pg.drawString("14.1-1       Revised: 11/29/2012 ", 50, 725);

                    /*
                    pg.drawString("Complaint #: " + tbComplaintNbr.getText(), 50, 50);
                    pg.drawString("QPR: " + bsmf.MainFrame.convertToX(cbQPR.isSelected()) , 50, 60);
                    pg.draw3DRect(80, 50, 10, 10, rootPaneCheckingEnabled);
                    */
                    //Graphics2D g2d = (Graphics2D)pg;
                    //g2d.translate(pf.getImageableX(), pf.getImageableY());
                    // NewJFrame.this.printAll(pg);

                    return Printable.PAGE_EXISTS;
                }
            });

            if (pjob.printDialog() == false) // choose printer
            {
                return;
            }
            pjob.print();
        } catch (PrinterException pe) {
            MainFrame.bslog(pe);
        }// TODO add your handling code here:
    }
    
    public void getSiteAddress(String site) {
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               
                int i = 0;
                                
                res = st.executeQuery("select * from site_mstr where site_site = " + "'" + site + "'" +";");
                while (res.next()) {
                    i++;
                   sitename = res.getString("site_desc");
                   siteaddr = res.getString("site_line1");
                   sitephone = res.getString("site_phone");
                   sitecitystatezip = res.getString("site_city") + ", " + res.getString("site_state") + " " + res.getString("site_zip");
                   sqename = res.getString("site_sqename");
                   sqephone = res.getString("site_sqephone");
                   sqefax = res.getString("site_sqefax");
                   sqeemail = res.getString("site_sqeemail");
                  
                }
               
                if (i == 0)
                    bsmf.MainFrame.show(getMessageTag(1001));

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
     * This method is called from within the bsmf.MainFrame.constructor to initialize the form.
     * WARNING: Do NOT modify this code. The bsmf.MainFrame.content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tbOriginator = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbQPR = new javax.swing.JCheckBox();
        cbInforOnly = new javax.swing.JCheckBox();
        cbSendSupp = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        cbSort = new javax.swing.JCheckBox();
        cbRework = new javax.swing.JCheckBox();
        cbScrapped = new javax.swing.JCheckBox();
        cbDeviation = new javax.swing.JCheckBox();
        tbDeviationNbr = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cbLine = new javax.swing.JCheckBox();
        cbReceiving = new javax.swing.JCheckBox();
        cbCustomer = new javax.swing.JCheckBox();
        cbEngineering = new javax.swing.JCheckBox();
        cbOther = new javax.swing.JCheckBox();
        tbOtherReason = new javax.swing.JTextField();
        tbDept = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbInternal = new javax.swing.JCheckBox();
        cbExternal = new javax.swing.JCheckBox();
        tbPartDesc = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tbQtyRejected = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tbNumSuspectCont = new javax.swing.JTextField();
        tbTotalQty = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        tbContact = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taIssue = new javax.swing.JTextArea();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taHistory = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        taComments = new javax.swing.JTextArea();
        tbChargeBack = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        dccreate = new com.toedter.calendar.JDateChooser();
        dcupdate = new com.toedter.calendar.JDateChooser();
        dcclose = new com.toedter.calendar.JDateChooser();
        lbstatus = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();
        ddvend = new javax.swing.JComboBox<>();
        lbvendname = new javax.swing.JLabel();
        dditem = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Quality Problem Report (QPR)"));
        jPanel1.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel1.setText("Complaint#");
        jLabel1.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel2.setText("DateCreated");
        jLabel2.setName("lblcreatedate"); // NOI18N

        jLabel3.setText("CreatedBy");
        jLabel3.setName("lbluserid"); // NOI18N

        cbQPR.setText("QPR 8d req");
        cbQPR.setName("cbqpr"); // NOI18N

        cbInforOnly.setText("Infor Only (no 8d req)");
        cbInforOnly.setName("cbifoonly"); // NOI18N

        cbSendSupp.setText("Send Back To Supplier");
        cbSendSupp.setName("cbsendback"); // NOI18N

        jLabel4.setText("Disposition of Nonconformance");
        jLabel4.setName("lbldisposition"); // NOI18N

        cbSort.setText("Sort");
        cbSort.setName("cbsort"); // NOI18N

        cbRework.setText("Rework");
        cbRework.setName("cbrework"); // NOI18N

        cbScrapped.setText("Scrapped");
        cbScrapped.setName("cbscrap"); // NOI18N

        cbDeviation.setText("Deviation#");
        cbDeviation.setName("cbdeviation"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Source Of Reject");
        jLabel5.setName("lblsourceofreject"); // NOI18N

        cbLine.setText("Line");
        cbLine.setName("cbline"); // NOI18N

        cbReceiving.setText("Receiving Inspection");
        cbReceiving.setName("cbreceiving"); // NOI18N

        cbCustomer.setText("Customer");
        cbCustomer.setName("cbcustomer"); // NOI18N

        cbEngineering.setText("Engineering");
        cbEngineering.setName("cbengineer"); // NOI18N

        cbOther.setText("Other");
        cbOther.setName("cbother"); // NOI18N

        jLabel6.setText("Dept#");
        jLabel6.setName("lbldept"); // NOI18N

        cbInternal.setText("Internal Supplier");
        cbInternal.setName("cbinternalsupplier"); // NOI18N

        cbExternal.setText("External Supplier");
        cbExternal.setName("cbexternalsupplier"); // NOI18N

        jLabel7.setText("PartNumber");
        jLabel7.setName("lblitem"); // NOI18N

        jLabel8.setText("Part Desc");
        jLabel8.setName("lblitemdesc"); // NOI18N

        jLabel9.setText("Qty Rejected");
        jLabel9.setName("lblqty"); // NOI18N

        jLabel10.setText("Number of Suspect Containers");
        jLabel10.setName("lblsuspect"); // NOI18N

        jLabel11.setText("Total Qty Found Defective");
        jLabel11.setName("lbldefective"); // NOI18N

        jLabel12.setText("DateUpdated");
        jLabel12.setName("lblupdatedate"); // NOI18N

        jLabel13.setText("Supplier");
        jLabel13.setName("lblsupplier"); // NOI18N

        jLabel14.setText("DateClosed");
        jLabel14.setName("lblcloseddate"); // NOI18N

        jLabel15.setText("SupplierContact");
        jLabel15.setName("lblcontact"); // NOI18N

        taIssue.setColumns(20);
        taIssue.setRows(5);
        jScrollPane1.setViewportView(taIssue);

        jLabel16.setText("Description of Issue:");
        jLabel16.setName("lblissue"); // NOI18N

        jLabel17.setText("Description of History (chargeback/debit memo):");
        jLabel17.setName("lblhistory"); // NOI18N

        taHistory.setColumns(20);
        taHistory.setRows(5);
        jScrollPane2.setViewportView(taHistory);

        jLabel18.setText("SQE Comments:");
        jLabel18.setName("lblcomment"); // NOI18N

        taComments.setColumns(20);
        taComments.setRows(5);
        jScrollPane3.setViewportView(taComments);

        jLabel19.setText("Total ChargeBack for QPR");
        jLabel19.setName("lblchargeback"); // NOI18N

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

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        dccreate.setDateFormatString("yyyy-MM-dd");

        dcupdate.setDateFormatString("yyyy-MM-dd");

        dcclose.setDateFormatString("yyyy-MM-dd");

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(cbInternal)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel1)
                                                        .addGap(60, 60, 60))
                                                    .addComponent(tbkey, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(13, 13, 13)
                                                .addComponent(btnew)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btclear)
                                                .addGap(29, 29, 29)))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel12))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dccreate, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(dcupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(23, 23, 23)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbOriginator, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel14)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(dcclose, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cbQPR)
                                            .addComponent(cbSendSupp)
                                            .addComponent(jLabel4)
                                            .addComponent(cbInforOnly)
                                            .addComponent(cbScrapped)
                                            .addComponent(cbSort)
                                            .addComponent(cbRework))
                                        .addGap(8, 8, 8)
                                        .addComponent(lbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(34, 39, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel15)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbContact, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jLabel5)
                                                        .addComponent(cbCustomer)
                                                        .addComponent(cbEngineering)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                            .addComponent(cbOther)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(tbOtherReason))
                                                        .addComponent(cbReceiving)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                            .addComponent(cbLine)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(jLabel6)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(tbDept, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(tbDeviationNbr, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cbDeviation)))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel9)
                                                            .addComponent(jLabel10)
                                                            .addComponent(jLabel11)
                                                            .addComponent(jLabel19))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(tbNumSuspectCont, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(tbQtyRejected, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(tbTotalQty, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(tbChargeBack, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jLabel8)
                                                                .addGap(15, 15, 15)
                                                                .addComponent(tbPartDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(1, 1, 1))
                                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jLabel7)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(221, 221, 221)
                                                .addComponent(jLabel13))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(cbExternal)
                                                .addGap(183, 183, 183)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lbvendname, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(1, 1, 1))
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btclear)
                    .addComponent(dccreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnew)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbOriginator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(btlookup))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbInternal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbExternal)
                        .addGap(1, 1, 1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel12))
                                    .addComponent(dcclose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(dcupdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbvendname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(cbQPR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInforOnly)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbSendSupp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbSort)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbRework)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbScrapped))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(lbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addGap(1, 1, 1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cbLine)
                                        .addComponent(jLabel6)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbReceiving)
                        .addGap(3, 3, 3)
                        .addComponent(cbCustomer)
                        .addGap(6, 6, 6)
                        .addComponent(cbEngineering)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbOther)
                            .addComponent(tbOtherReason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, Short.MAX_VALUE)
                        .addComponent(cbDeviation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbDeviationNbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbPartDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbQtyRejected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbNumSuspectCont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbTotalQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbChargeBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btprint)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("quality");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
    if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed
    
    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
                try {
                    OVData.printQPR(tbkey.getText());
                    // printPDF();
                } catch (SQLException ex) {
                    bslog(ex);
                } catch (JRException ex) {
                    bslog(ex);
                }
        
    }//GEN-LAST:event_btprintActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput("deleteRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("delete", new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
        if (! isLoad) {
            if (ddvend.getItemCount() > 0) {
                lbvendname.setText(venData.getVendName(ddvend.getSelectedItem().toString()));
            } // if ddvend has a list
        }
    }//GEN-LAST:event_ddvendActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbCustomer;
    private javax.swing.JCheckBox cbDeviation;
    private javax.swing.JCheckBox cbEngineering;
    private javax.swing.JCheckBox cbExternal;
    private javax.swing.JCheckBox cbInforOnly;
    private javax.swing.JCheckBox cbInternal;
    private javax.swing.JCheckBox cbLine;
    private javax.swing.JCheckBox cbOther;
    private javax.swing.JCheckBox cbQPR;
    private javax.swing.JCheckBox cbReceiving;
    private javax.swing.JCheckBox cbRework;
    private javax.swing.JCheckBox cbScrapped;
    private javax.swing.JCheckBox cbSendSupp;
    private javax.swing.JCheckBox cbSort;
    private com.toedter.calendar.JDateChooser dcclose;
    private com.toedter.calendar.JDateChooser dccreate;
    private com.toedter.calendar.JDateChooser dcupdate;
    private javax.swing.JComboBox<String> dditem;
    private javax.swing.JComboBox<String> ddvend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbstatus;
    private javax.swing.JLabel lbvendname;
    private javax.swing.JTextArea taComments;
    private javax.swing.JTextArea taHistory;
    private javax.swing.JTextArea taIssue;
    private javax.swing.JTextField tbChargeBack;
    private javax.swing.JTextField tbContact;
    private javax.swing.JTextField tbDept;
    private javax.swing.JTextField tbDeviationNbr;
    private javax.swing.JTextField tbNumSuspectCont;
    private javax.swing.JTextField tbOriginator;
    private javax.swing.JTextField tbOtherReason;
    private javax.swing.JTextField tbPartDesc;
    private javax.swing.JTextField tbQtyRejected;
    private javax.swing.JTextField tbTotalQty;
    private javax.swing.JTextField tbkey;
    // End of variables declaration//GEN-END:variables
}
