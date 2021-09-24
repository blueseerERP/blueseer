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
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import com.blueseer.utl.BlueSeerUtils;
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
import static com.blueseer.utl.BlueSeerUtils.lurb2;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author vaughnte
 */
public class ReqMaint extends javax.swing.JPanel implements IBlueSeer {

    /**
     * Creates new form PurchReqMaint
     */
    
    
    // global variable declarations
     boolean isLoad = false;
    
     javax.swing.table.DefaultTableModel reqtask = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                   getGlobalColumnTag("number"), 
                   getGlobalColumnTag("approver"), 
                   getGlobalColumnTag("status"), 
                   getGlobalColumnTag("sequence"), 
                   getGlobalColumnTag("email")
                   }){
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
     javax.swing.table.DefaultTableModel itemmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                   getGlobalColumnTag("item"), getGlobalColumnTag("quantity"), getGlobalColumnTag("price")
                   });
         
     public String admins[];
   
    
    class SomeRenderer extends DefaultTableCellRenderer {
        public SomeRenderer() {
        setOpaque(true);
        }
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

        
            //Integer percentage = (Integer) table.getValueAt(row, 3);
            //if (percentage > 30)
            if (table.getValueAt(row, 2).toString().compareTo("approved") == 0){
             c.setBackground(Color.green);
            } else {
             c.setBackground(table.getBackground());
             c.setForeground(table.getForeground());
            }
            
        return c;
    }
    }
    
     
    public ReqMaint() {
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
        approverPanel.setVisible(false);
         
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       
        tbkey.setText("");
        tbkey.setEditable(true);
        tbkey.setForeground(Color.black);
        tbrequestor.setText(bsmf.MainFrame.userid);
        
        tbamt.setText("");
        tbAR.setText("");
       
        tbacct.setText("");
        tbproject.setText("");
        tbdesc.setText("");
        tbpo.setText("");
        tbstatus.setText("");
        caldate.setDate(now);
        itemmodel.setRowCount(0);
        itemtable.setModel(itemmodel);
        tbqty.setText("");
        tbitem.setText("");
        tbprice.setText("");
        
        approverTable.setModel(reqtask);
        approverTable.getColumnModel().getColumn(2).setCellRenderer(new ReqMaint.SomeRenderer());
        set_admins();
        itemtable.setModel(itemmodel);
        
        
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddvend.removeAllItems();
        ArrayList myvends = OVData.getvendnamelist();
        for (int i = 0; i < myvends.size(); i++) {
         ddvend.addItem(myvends.get(i));
        }
        ddvend.insertItemAt("", 0);
        ddvend.setSelectedIndex(0);
          
        dddept.removeAllItems();
        ArrayList mydept = OVData.getdeptidlist();
        for (int i = 0; i < mydept.size(); i++) {
            dddept.addItem(mydept.get(i));
        }  
        
        ddtype.removeAllItems();
        ArrayList mytype = OVData.getCodeMstrKeyList("REQTYPE");
        for (int i = 0; i < mytype.size(); i++) {
            ddtype.addItem(mytype.get(i));
        }  
          
        isLoad = false;
        
        
    }
    
    public void initvars(String[] arg) {
       
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
        btprint.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setForeground(Color.blue);
        tbrequestor.setEnabled(false);
        tbvendcode.setEnabled(false);
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
                   tbrequestor.setEnabled(false);
                   tbvendcode.setEnabled(false);
                   
                   if (tbstatus.getText().compareTo(getGlobalProgTag("approved")) == 0 ) {
                         tbstatus.setForeground(Color.green);
                         btupdate.setEnabled(false);
                   }
                   if (tbstatus.getText().compareTo(getGlobalProgTag("pending")) == 0 ) {
                    tbstatus.setForeground(Color.red);
                   }
                   
                   
                   
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
                double threshold = 0;
                boolean preapproved = false;
                String mystatus = "";
                String firstapprover = "";
                boolean sendemail = false;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                
                 javax.swing.JTable mytable = new javax.swing.JTable();
                    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                   "id", "owner", "status", "Sequence", "Email"
                   });
                
                 for (i = 0; i < itemtable.getRowCount(); i++) {
                        if (itemtable.getModel().getValueAt(i, 0) == null &&
                        itemtable.getModel().getValueAt(i, 1) == null &&
                        itemtable.getModel().getValueAt(i, 2) == null) {
                       ((javax.swing.table.DefaultTableModel) itemtable.getModel()).removeRow(i);
                        }      
                 }
                
                
                String status = "pending";
                if (isadmin()) {
                    preapproved = true;
                    status = "approved";
                }
                
                
                // lets trim remarks/desc to 300 chars only
                String rmks = "";
                if (tbdesc.getText().length() > 300) {
                   rmks = tbdesc.getText().substring(0, 300).replace("'", "");
                } else {
                    rmks = tbdesc.getText().replace("'", "");
                }
                
                 st.executeUpdate("insert into req_mstr "
                        + "(req_id, req_name, req_date, req_vendnbr, "
                        + "req_type, req_dept, req_amt,  req_desc, req_acct, req_cc, req_po, req_status, req_site, req_vend )"
                        + " values ( " + "'" + tbkey.getText() + "'" + ","
                        + "'" + tbrequestor.getText() + "'" + ","
                        + "'" + dfdate.format(caldate.getDate()) + "'" + ","
                        + "'" + tbvendcode.getText() + "'" + ","
                        + "'" + ddtype.getSelectedItem() + "'" + ","
                        + "'" + dddept.getSelectedItem() + "'" + ","
                        + "'" + tbamt.getText() + "'" + ","
                        + "'" + rmks + "'" + ","
                            + "''" + ","
                            + "''" + ","
                            + "''" + ","
                        + "'" + status + "'"  + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + ddvend.getSelectedItem().toString() + "'"
                        + ")"
                        + ";");
                    
                    for (i = 0; i < itemtable.getRowCount(); i++) {
                        
                        if (itemtable.getModel().getValueAt(i, 0) == null &&
                        itemtable.getModel().getValueAt(i, 1) == null &&
                        itemtable.getModel().getValueAt(i, 2) == null) {
                        continue;
                        }        
                        
                    st.executeUpdate("insert into req_det "
                        + "(reqd_id, reqd_item, reqd_qty, reqd_price )"
                        + " values ( " + "'" + tbkey.getText().toString() + "'" + ","
                        + "'" + itemtable.getValueAt(i, 0).toString().replace("'", "") + "'" + ","
                        + "'" + itemtable.getValueAt(i, 1) + "'" + ","
                        + "'" + itemtable.getValueAt(i, 2) + "'"
                        + ")"
                        + ";");
                        
                    }
                    
                     if (! preapproved) {
                    res = st.executeQuery("select reqa_owner, reqa_sequence, req_amt_threshold, reqa_email from req_auth inner join req_ctrl where reqa_enabled = '1' order by reqa_sequence ;");
                   
                    while (res.next()) {
                        threshold = res.getDouble("req_amt_threshold");
                         mymodel.addRow(new Object[]{tbkey.getText().toString(), res.getString("reqa_owner"), getGlobalProgTag("queued"), res.getString("reqa_sequence"), res.getString("reqa_email") }); 
                    }
                    mytable.setModel(mymodel);
                    
                    if (mytable.getRowCount() > 0 ) {
                     mystatus = "pending";
                     
                     for (int j = 0; j < mytable.getRowCount(); j++) {
                        try {
                         
                            // we skip the first sequence authorization if the dollar amount is less than the threshold
                            // first sequence owner must always be the person making the ultimate decision
                        if (Double.valueOf(tbamt.getText().toString()) < threshold &&
                            mytable.getValueAt(j, 3).toString().compareTo("1") == 0) {
                            continue;
                        } 
                        
                        // this will ensure that the first approver on the list gets the initial email
                        if (firstapprover.isEmpty()) {
                        firstapprover = mytable.getValueAt(j,1).toString();
                        sendemail = Boolean.valueOf(mytable.getValueAt(j, 4).toString());
                        }
                        
                        st.executeUpdate("insert into req_task "
                        + "(reqt_id, reqt_owner, reqt_status, reqt_sequence, reqt_email "
                        + ") "
                        + " values ( " + "'" + mytable.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + mytable.getValueAt(j, 1).toString() + "'" + ","
                        + "'" + mystatus + "'" + ","
                        + "'" + mytable.getValueAt(j, 3).toString() + "'" + ","
                        + "'" + mytable.getValueAt(j, 4).toString() + "'"
                        + ")"
                        + ";");
                        
                        } catch (SQLException s) {
                            MainFrame.bslog(s);
                        bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                        }
                        mystatus = getGlobalProgTag("queued");
                      }
                      
                 
                    }
                     /* let send email to first approver */
                     if (sendemail) {
                     String subject = "Requisition Approval Alert";
                     String body = "Requisition number " + tbkey.getText() + " requires your approval";
                     String requestor = "Requestor = " + tbrequestor.getText();
                     String amount = "Amount = " + tbamt.getText();
                     body = body + "\n" + requestor + "\n" + amount;
                     OVData.sendEmailByID(firstapprover, subject, body, "");
                     }
                  
                  } else {
                      noApprovalUpdate(tbkey.getText());
                    
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
 
                       
                        st.executeUpdate("delete from req_det where reqd_id = " + "'" + x[0] + "'" + ";"); 
                        st.executeUpdate("delete from req_task where reqt_id = " + "'" + x[0] + "'" + ";"); 
                        
                        
                        int i = st.executeUpdate("delete from req_mstr where req_id = " + "'" + x[0] + "'" + ";");   
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
               
                boolean isadmin = false;
       
       for (String admin : admins) {
          if (admin.compareTo(bsmf.MainFrame.userid) == 0) {
              isadmin = true;
          }  
        }
       for (int i = 0; i < reqtask.getRowCount(); i++) {
           if (reqtask.getValueAt(i, 2).toString().compareTo("approved") == 0) 
             proceed = false;
       }
       
      
       if (proceed || isadmin) {
                
                 st.executeUpdate("update req_mstr set " + 
                       " req_desc = " + "'" + tbdesc.getText() + "'" + "," +
                       " req_vend = " + "'" + ddvend.getSelectedItem() + "'" + "," +
                       " req_vendnbr = " + "'" + tbvendcode.getText() + "'" + "," +        
                       " req_type = " + "'" + ddtype.getSelectedItem() + "'" + "," +
                       " req_dept = " + "'" + dddept.getSelectedItem() + "'" + "," +
                       " req_amt = " + "'" + tbamt.getText() + "'" + "," +
                       " req_acct = " + "'" + tbacct.getText() + "'" + "," +
                       " req_cc = " + "'" + "" + "'" + "," +
                       " req_AR = " + "'" + tbAR.getText() + "'" + "," +
                       " req_proj = " + "'" + tbproject.getText() + "'" +                      
                       " where " + 
                        " req_id = " + "'" + tbkey.getText() + "'" +  ";");

                 // let's remove all items associated with this req id
                       st.executeUpdate("delete from req_det where reqd_id = " + "'" + tbkey.getText() + "'" +  ";");
                    //  now add the new ones just updated
                       for (int i = 0; i < itemtable.getRowCount(); i++) {
                        
                        if (itemtable.getModel().getValueAt(i, 0) == null &&
                        itemtable.getModel().getValueAt(i, 1) == null &&
                        itemtable.getModel().getValueAt(i, 2) == null) {
                        continue;
                        }        
                        
                    st.executeUpdate("insert into req_det "
                        + "(reqd_id, reqd_item, reqd_qty, reqd_price )"
                        + " values ( " + "'" + tbkey.getText().toString() + "'" + ","
                        + "'" + itemtable.getValueAt(i, 0) + "'" + ","
                        + "'" + itemtable.getValueAt(i, 1) + "'" + ","
                        + "'" + itemtable.getValueAt(i, 2) + "'"
                        + ")"
                        + ";");
                        
                    }
       } // proceed    
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
         reqtask.setRowCount(0);
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                
                res = st.executeQuery("select * from req_mstr where req_id = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                    i++;
                    tbdesc.setText(res.getString("req_desc"));
                    ddvend.setSelectedItem(res.getString("req_vend"));
                    tbvendcode.setText(res.getString("req_vendnbr"));
                    ddtype.setSelectedItem(res.getString("req_type"));
                    dddept.setSelectedItem(res.getString("req_dept"));
                    tbrequestor.setText(res.getString("req_name"));
                    caldate.setDate(Date.valueOf(res.getString("req_date")));
                    tbpo.setText(res.getString("req_po"));
                    tbstatus.setText(res.getString("req_status"));
                    tbamt.setText(BlueSeerUtils.currformat(res.getString("req_amt")));
                    tbAR.setText(res.getString("req_AR"));
                    tbproject.setText(res.getString("req_proj"));
                    tbacct.setText(res.getString("req_acct"));
                    ddsite.setSelectedItem(res.getString("req_site"));
                    ddsite.setEnabled(false);
                    if (! x[0].isEmpty()) {
                        tbkey.setText(x[0]);
                        approverPanel.setVisible(true);
                    }
                    tbkey.setEnabled(false);
                }

                
               itemmodel.setRowCount(0);
                res = st.executeQuery("select * from req_det where reqd_id = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                    itemmodel.addRow(new Object[]{res.getString("reqd_item"), res.getString("reqd_qty"), BlueSeerUtils.currformat(res.getString("reqd_price"))}); 
                }
                itemtable.setModel(itemmodel);
                
                
                res = st.executeQuery("select * from req_task where reqt_id = " + "'" + x[0] + "'" + " order by reqt_sequence ;" );
                while (res.next()) {
                reqtask.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("reqt_owner"), res.getString("reqt_status"), res.getString("reqt_sequence")});
                }
                
             //   jTable1.getColumn("Sequence").setCellRenderer(new ButtonRenderer());
             //   jTable1.getColumn("Sequence").setCellEditor(
             //           new ButtonEditor(new JCheckBox()));
               
                
                 for (int j = 0; j < approverTable.getRowCount(); j++) {
                    if (approverTable.getModel().getValueAt(j, 2).toString().compareTo("approved") == 0) {
                   // jTable1.getColumn("Sequence").setCellEditor(null);
                        if (! isadmin())
                        btupdate.setEnabled(false);
                    }
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
    
    public boolean validateInput(String x) {
        boolean proceed = true;
                
        if ( ddvend.getSelectedItem() == null || ddvend.getSelectedItem().toString().isEmpty() ) {
           bsmf.MainFrame.show(getMessageTag(1026));
           proceed = false;
            ddvend.requestFocus();
            return false;
        }
         
                    
        return proceed;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getReqBrowseUtil(luinput.getText(),0, "req_id");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getReqBrowseUtil(luinput.getText(),0, "req_name");   
        } else {
         luModel = DTData.getReqBrowseUtil(luinput.getText(),0, "req_vend");   
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
                getClassLabelTag("lblrequestor", this.getClass().getSimpleName()),
                getClassLabelTag("lblvendor", this.getClass().getSimpleName())); 
        
    }

    
     // additional functions        
    public void sendEmailToRequestor(String myid, String mypo) {
        
       int i = 0;
        try{
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try{
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                res = st.executeQuery("select * from req_mstr inner join user_mstr on " +
                          " user_id = req_name " +
                          "where " +
                          "req_id = " + "'" + myid + "'" + 
                           ";");
                 while (res.next()) {
                     String subject = "Requisition is Approved";
                     String body = "Requisition number " + myid + " is approved. \n";
                     body += "Your PO number is " + mypo;
                     OVData.sendEmail(res.getString("user_email"), subject, body, "");
                 }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
       
    }
         
    public boolean isadmin() {
        boolean myreturn = false;
        for (String thisuser : admins) {
                           if (thisuser.compareTo(bsmf.MainFrame.userid) == 0) {
                                myreturn = true;
                           }    
                           
                       }
        
        return myreturn;
    }
      
    public void set_admins() {
        
       int i = 0;
        try{
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try{
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                res = st.executeQuery("SELECT req_admins from req_ctrl;");
                    while (res.next()) {
                        i++;
                       admins = res.getString("req_admins").split(",");
                       for (String thisuser : admins) {
                           if (thisuser.compareTo(bsmf.MainFrame.userid) == 0) {
                                tbacct.setEnabled(true);
                                override.setEnabled(true);
                           }    
                           
                       }
                    }
                    if (i == 0) {
                        admins = new String[1];
                        admins[0] = "admin";
                    }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
       
    }
      
    public void setAllTasksApproved(String myid) {
        
       int i = 0;
        try{
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try{
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                 // now...lets update task and set to approved 
                 st.executeUpdate("update req_task set reqt_status = 'approved' where " + 
                        " reqt_id = " + "'" + myid + "'" +  ";");
                 
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
       
    }
    
    public void settotal() {
        double totamt = 0.00;
        double myqty = 0;
        double myprice = 0.00;
        DecimalFormat df = new DecimalFormat("##0.00", new DecimalFormatSymbols(Locale.US));
        
        for (int i = 0; i < itemtable.getRowCount(); i++) {
            myqty = 0;
            myprice = 0;
           
            /* logic for blank row */
            if (itemtable.getModel().getValueAt(i, 0) == null &&
                itemtable.getModel().getValueAt(i, 1) == null &&
                itemtable.getModel().getValueAt(i, 2) == null) {
                continue;
            }    
            
          
            
            
            if (itemtable.getModel().getValueAt(i, 1) != null && ! itemtable.getModel().getValueAt(i, 1).toString().isEmpty() ) {
                myqty = Double.valueOf(itemtable.getModel().getValueAt(i, 1).toString());
            }
             if (itemtable.getModel().getValueAt(i, 2) != null && ! itemtable.getModel().getValueAt(i, 2).toString().isEmpty()) {
                myprice = Double.valueOf(itemtable.getModel().getValueAt(i, 2).toString());
            }
                  
             
	 
            totamt += (myqty * myprice);
        }
        tbamt.setText(String.valueOf(df.format(totamt)));
    }
    
    public void noApprovalUpdate(String myid) {
        int mypo = OVData.getNextNbr("PO");
                         bsmf.MainFrame.show(getMessageTag(1110, String.valueOf(mypo)));
                         tbpo.setText(String.valueOf(mypo));
                          tbstatus.setForeground(Color.green);
                         tbstatus.setText(getGlobalProgTag("approved"));
                         sendEmailToRequestor(myid, String.valueOf(mypo));
          
                          try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
               st.executeUpdate("update req_mstr set " + 
                       " req_status = " + "'" + "approved" + "'" + "," +
                       " req_po = " + "'" + String.valueOf(mypo) + "'" +                      
                       " where " + 
                        " req_id = " + "'" + myid + "'" +  ";");
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
              } catch (Exception e) {
            MainFrame.bslog(e);
        }
                         
                         
    }
    
    public void approvereq(String myid, String thissequence) {
        
        try {
            
            
            int mypo = 0;
            int mysequence = Integer.valueOf(thissequence);
            reqtask.setRowCount(0);
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int nextsequence = 0;
            boolean islast = false;
            boolean emailrequestor = false;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                // OK...lets determine if last sequence
                
               res = st.executeQuery("select * from req_task inner join req_ctrl where reqt_id = "
                     + "'" + myid + "'" +  " order by reqt_sequence desc ;"); 
                 while (res.next()) {
                   i++;
                   emailrequestor = res.getBoolean("req_requestor_email");
                   if (i == 1) {
                      if (mysequence == Integer.valueOf(res.getString("reqt_sequence"))) {
                         islast = true;
                         
                      }
                   }
                   
                   if (! islast && mysequence == i) {
                       nextsequence = i + 1;
                       break;
                   }
                 }
                i = 0;
                
                // check for override of remaining approvals.  Any approver can override subsequent approvals.
                if (override.isSelected()) {
                    islast = true;
                    setAllTasksApproved(myid);
                }
                
                
                // now...lets update task and set to approved 
                 st.executeUpdate("update req_task set reqt_status = 'approved' where " + 
                        " reqt_id = " + "'" + myid + "'" + " AND " + 
                         "reqt_sequence = " + "'" + mysequence + "'" + ";");
                 
                
                // let's get the next sequence userid for email purposes
                 if (! islast) {
                 res = st.executeQuery("select * from req_task inner join user_mstr on " +
                          " user_id = reqt_owner " + " inner join req_mstr on " +
                          " reqt_id = req_id " +
                          "where " +
                          "reqt_id = " + "'" + myid + "'" + " AND " +
                          "reqt_sequence = " + "'" + nextsequence + "'" + ";");
                 while (res.next()) {
                     if (res.getBoolean("reqt_email")) {
                     String subject = "Requisition Approval Alert";
                     String body = "Requisition number " + myid + " requires your approval";
                     String requestor = "Requestor = " + res.getString("req_name");
                     String amount = "Amount = " + res.getString("req_amt");
                     body = body + "\n" + requestor + "\n" + amount;
                     OVData.sendEmail(res.getString("user_email"), subject, body, "");
                     }
                 }
                 
                 }
                 
                 // now...lets set next sequence to pending....if there is one
                 if (! islast) {
                 st.executeUpdate("update req_task set reqt_status = 'pending' where " + 
                        " reqt_id = " + "'" + myid + "'" + " AND " + 
                         "reqt_sequence = " + "'" + nextsequence + "'" + ";");
                 }
                 
                 //finally....if is last sequence...then set entire Req to 'approved'
                 if (islast) {
                
                 mypo = OVData.getNextNbr("PO");
                  st.executeUpdate("update req_mstr set req_status = 'approved', req_po = " + "'" + String.valueOf(mypo) + "'" + " where " + 
                        " req_id = " + "'" + myid + "'" +  ";");
                         tbpo.setText(String.valueOf(mypo));
                         tbstatus.setText(getGlobalProgTag("approved"));
                       
                         if (emailrequestor) {
                         sendEmailToRequestor(myid, String.valueOf(mypo));
                         } 
                 }
                 
                 if (islast)
                 bsmf.MainFrame.show(getMessageTag(1110, String.valueOf(mypo)));
                 
                 if (! islast) {
                 bsmf.MainFrame.show(getMessageTag(1111));  
                 }
                 // reinit the task list
                res = st.executeQuery("select * from req_task where reqt_id = " + "'" + myid + "'" + " order by reqt_sequence ;");
                while (res.next()) {
                reqtask.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("reqt_owner"), res.getString("reqt_status"), res.getString("reqt_sequence")});
                }
                
              //  jTable1.getColumn("Sequence").setCellRenderer(new ButtonRenderer());
               // jTable1.getColumn("Sequence").setCellEditor(
               //         new ButtonEditor(new JCheckBox()));
               
                

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void vendChangeEvent(String mykey) {
      try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                tbvendcode.setText("");
                res = st.executeQuery("select vd_addr from vd_mstr where vd_name = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    tbvendcode.setText(res.getString("vd_addr"));
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
        jLabel1 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        tbrequestor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        caldate = new com.toedter.calendar.JDateChooser();
        tbamt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        ddvend = new javax.swing.JComboBox();
        dddept = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        itemtable = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbdesc = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        tbvendcode = new javax.swing.JTextField();
        btprint = new javax.swing.JButton();
        approverPanel = new javax.swing.JScrollPane();
        approverTable = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        btAddItem = new javax.swing.JButton();
        tbitem = new javax.swing.JTextField();
        btdelitem = new javax.swing.JButton();
        tbprice = new javax.swing.JTextField();
        tbqty = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        tbpo = new javax.swing.JTextField();
        tbacct = new javax.swing.JTextField();
        tbproject = new javax.swing.JTextField();
        override = new javax.swing.JCheckBox();
        tbAR = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tbstatus = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Requisition Maintenance"));
        jPanel1.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N
        jPanel1.setName("panelmain"); // NOI18N

        jLabel1.setText("Req By:");
        jLabel1.setName("lblreqby"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel2.setText("Req Nbr:");
        jLabel2.setName("lblid"); // NOI18N

        jLabel3.setText("Req Type:");
        jLabel3.setName("lblreqtype"); // NOI18N

        jLabel4.setText("Req Date:");
        jLabel4.setName("lblreqdate"); // NOI18N

        caldate.setDateFormatString("yyyy-MM-dd");

        jLabel5.setText("Total:");
        jLabel5.setName("lbltotal"); // NOI18N

        ddvend.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddvendItemStateChanged(evt);
            }
        });
        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        jLabel6.setText("VendName:");
        jLabel6.setName("lblvendname"); // NOI18N

        jLabel7.setText("Dept:");
        jLabel7.setName("lbldept"); // NOI18N

        btadd.setText("Submit");
        btadd.setName("btsubmit"); // NOI18N
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

        itemtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Item", "Qty", "UnitPrice"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(itemtable);

        tbdesc.setColumns(20);
        tbdesc.setLineWrap(true);
        tbdesc.setRows(5);
        tbdesc.setWrapStyleWord(true);
        jScrollPane1.setViewportView(tbdesc);

        jLabel10.setText("Remarks");
        jLabel10.setName("lblremarks"); // NOI18N

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        approverTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Owner", "Status", "Commit"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        approverTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                approverTableMouseClicked(evt);
            }
        });
        approverPanel.setViewportView(approverTable);

        jLabel18.setText("Site");
        jLabel18.setName("lblsite"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        ddsite.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btAddItem.setText("AddItem");
        btAddItem.setName("btadditem"); // NOI18N
        btAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddItemActionPerformed(evt);
            }
        });

        tbitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbitemActionPerformed(evt);
            }
        });

        btdelitem.setText("DelItem");
        btdelitem.setName("btdeleteitem"); // NOI18N
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel15.setText("Item");
        jLabel15.setName("lblitem"); // NOI18N

        jLabel16.setText("Qty");
        jLabel16.setName("lblqty"); // NOI18N

        jLabel17.setText("Price");
        jLabel17.setName("lblprice"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15)
                        .addGap(5, 5, 5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addComponent(btAddItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelitem))
                    .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btAddItem)
                    .addComponent(btdelitem)
                    .addComponent(jLabel17))
                .addContainerGap())
        );

        jLabel14.setText("Project");
        jLabel14.setName("lblproject"); // NOI18N

        tbpo.setEditable(false);

        override.setText("Override");
        override.setName("cboverride"); // NOI18N

        jLabel11.setText("Acct:");
        jLabel11.setName("lblacct"); // NOI18N

        jLabel8.setText("PO:");
        jLabel8.setName("lblpo"); // NOI18N

        jLabel13.setText("Auth:");
        jLabel13.setName("lblauth"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel11))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel8)
                        .addComponent(jLabel13)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(jLabel14))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(tbAR, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                        .addComponent(tbpo, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(override)
                    .addComponent(tbproject, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(tbproject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(override))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addContainerGap())
        );

        tbstatus.setEditable(false);
        tbstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbstatusActionPerformed(evt);
            }
        });

        jLabel9.setText("Status");
        jLabel9.setName("lblstatus"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N

        jLabel12.setText("VendCode:");
        jLabel12.setName("lblvend"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tbrequestor, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(9, 9, 9)
                                                .addComponent(btnew)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btclear))
                                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(dddept, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tbvendcode, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel10)
                                            .addComponent(jLabel18))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(caldate, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(34, 34, 34)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jScrollPane1)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(10, 10, 10))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(approverPanel)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(79, 79, 79)
                                .addComponent(btprint)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel18)
                        .addComponent(btnew)
                        .addComponent(btclear)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbrequestor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1))
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbvendcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dddept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(caldate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)))))
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(btprint)
                    .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(btdelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(approverPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("purchreq");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void tbitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbitemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbitemActionPerformed

    private void btAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddItemActionPerformed
       boolean canproceed = true;
        itemtable.setModel(itemmodel);
       
        if (tbitem.getText().length() > 80) {
            bsmf.MainFrame.show(getMessageTag(1080,"80"));
            canproceed = false;
            return;
        }
        
        if (tbprice.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1028));
           canproceed = false;
           tbprice.setBackground(Color.yellow);
           tbprice.requestFocus();
           return;
        }
        if (tbqty.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1028));
           canproceed = false;
           tbqty.setBackground(Color.yellow);
           tbqty.requestFocus();
           return;
        }
       
         
        if (canproceed) {
               itemmodel.addRow(new Object[]{tbitem.getText(), tbqty.getText(), tbprice.getText()}); 
        }
        settotal();
        tbitem.setText("");
        tbqty.setText("");
        tbprice.setText("");
    }//GEN-LAST:event_btAddItemActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
         int[] rows = itemtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) itemtable.getModel()).removeRow(i);
        }
        settotal();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void tbstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbstatusActionPerformed
       if (tbstatus.getText().compareTo(getGlobalProgTag("approved")) == 0 ) {
           tbstatus.setForeground(Color.green);
       }
       if (tbstatus.getText().compareTo(getGlobalProgTag("pending")) == 0 ) {
           tbstatus.setForeground(Color.yellow);
       }
    }//GEN-LAST:event_tbstatusActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
        if (! isLoad) {
         if (ddvend.getItemCount() > 0) {
            vendChangeEvent(ddvend.getSelectedItem().toString());
         } // if ddvend has a list
        }
    }//GEN-LAST:event_ddvendActionPerformed

    private void ddvendItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddvendItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED)
    {
       try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
                res = st.executeQuery("select vd_addr from vd_mstr where vd_name = " + "'" + ddvend.getSelectedItem().toString().replace("'", "") + "'"  + ";");
                while (res.next()) {
                tbvendcode.setText(res.getString("vd_addr"));
                    
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    }//GEN-LAST:event_ddvendItemStateChanged

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
          if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "SHIPPER");
                hm.put("myid",  tbkey.getText().toString());
                hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_part, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/req_print.jasper");
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, bsmf.MainFrame.con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/b1.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
      
    }//GEN-LAST:event_btprintActionPerformed

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

    private void approverTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approverTableMouseClicked
        int row = approverTable.rowAtPoint(evt.getPoint());
        int col = approverTable.columnAtPoint(evt.getPoint());
        if ( col == 0) {
            if (approverTable.getModel().getValueAt(row, 2).toString().compareTo("pending") == 0) {
               if (approverTable.getModel().getValueAt(row, 1).toString().compareTo(bsmf.MainFrame.userid) == 0) {
                approvereq(tbkey.getText(), approverTable.getValueAt(row, 3).toString());
               } else {
                bsmf.MainFrame.show(getMessageTag(1112));  
               }
            }
        }
    }//GEN-LAST:event_approverTableMouseClicked

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JScrollPane approverPanel;
    public javax.swing.JTable approverTable;
    private javax.swing.JButton btAddItem;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btupdate;
    private com.toedter.calendar.JDateChooser caldate;
    private javax.swing.JComboBox dddept;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JTable itemtable;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JCheckBox override;
    private javax.swing.JTextField tbAR;
    private javax.swing.JTextField tbacct;
    private javax.swing.JTextField tbamt;
    private javax.swing.JTextArea tbdesc;
    private javax.swing.JTextField tbitem;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbproject;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbrequestor;
    private javax.swing.JTextField tbstatus;
    private javax.swing.JTextField tbvendcode;
    // End of variables declaration//GEN-END:variables
}
