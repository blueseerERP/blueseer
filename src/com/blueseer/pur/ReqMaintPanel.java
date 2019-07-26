/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.pur;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
public class ReqMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form PurchReqMaint
     */
    
    
     javax.swing.table.DefaultTableModel reqtask = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                   "id", "owner", "status", "Sequence", "Email"
                   });
     javax.swing.table.DefaultTableModel itemmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                   "item", "qty", "price"
                   });
    
     
  

     
     
     
     public String admins[];
    
      class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            
            
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            if (jTable1.getModel().getValueAt(row, 2).toString().compareTo("approved") == 0) {
            setBackground(Color.green);
            //setEnabled(false);
            setText("complete");
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        protected JButton button;
        private String label;
        private String columnname;
        private int myrow;
        private int mycol;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            columnname = String.valueOf(column);
            button.setText(label);
            //button.setText("approve");
            
            isPushed = true;
           
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // 
                // 
               //JOptionPane.showMessageDialog(button, columnname + ":" + label );
                // System.out.println(label + ": Ouch!");
              //  if ((jTable1.getColumnName(0).toString().compareTo("Shipper")) == 0) {
                    // approvereq(label);
              //  }
                myrow = jTable1.getSelectedRow();
                if (jTable1.getModel().getValueAt(myrow, 2).toString().compareTo("pending") == 0) {
                    if (jTable1.getModel().getValueAt(myrow, 1).toString().compareTo(bsmf.MainFrame.userid) == 0) {
                 approvereq(jTable1.getValueAt(myrow, 0).toString(), jTable1.getValueAt(myrow, 3).toString());
                    } else {
                      bsmf.MainFrame.show("you do not have access to approve this action");  
                    }
                    //bsmf.MainFrame.show(jTable1.getValueAt(myrow, 1).toString());
                }
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    
    class SomeRenderer extends DefaultTableCellRenderer {
        public SomeRenderer() {
        setOpaque(true);
        }
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

        if (isSelected) {
                c.setForeground(table.getSelectionForeground());
                c.setBackground(table.getSelectionBackground());
            } else {
                c.setForeground(table.getForeground());
                c.setBackground(UIManager.getColor("Button.background"));
            }
            //Integer percentage = (Integer) table.getValueAt(row, 3);
            //if (percentage > 30)
            if (table.getValueAt(row, 2).toString().compareTo("approved") == 0)   
            {c.setBackground(Color.green);
             c.setEnabled(false);
            }
            else {
                c.setBackground(table.getBackground());
                c.setEnabled(true);
            }
        return c;
    }
    }
    
     
     public ReqMaintPanel() {
        initComponents();
        
       
    }

    public void initddvend() {
        ddvend.removeAllItems();
        ArrayList myvend = OVData.getvendnamelist();
        for (int i = 0; i < myvend.size(); i++) {
            ddvend.addItem(myvend.get(i));
        }
         
    } 
    
    public void initdddept() {
       dddept.removeAllItems();
        ArrayList mydept = OVData.getdeptidlist();
        for (int i = 0; i < mydept.size(); i++) {
            dddept.addItem(mydept.get(i));
        }
         
    } 
    
    public void initddtype(String user) {
        ddtype.removeAllItems();
        
        ddtype.addItem("Expense");
        ddtype.addItem("Inventory");
        ddtype.addItem("AR");
        ddtype.addItem("Sorting");
        
        if (admins != null) {
        for (String admin : admins) {
          if (admin.compareTo(user) == 0) {
              ddtype.addItem("MRO");
          }  
        }
        }
    } 
   
    public void disableall() {
        
        tbrequestor.setEnabled(false);
        tbamt.setEnabled(false);
        tbAR.setEnabled(false);
        tbcc.setEnabled(false);
        tbacct.setEnabled(false);
        tbproject.setEnabled(false);
        tbdesc.setEnabled(false);
        tbpo.setEnabled(false);
        tbstatus.setEnabled(false);
        btPurchReqNew.setEnabled(true);
        btPurchReqAdd.setEnabled(false);
        btPurchReqEdit.setEnabled(false);
        btPurchReqGet.setEnabled(true);
        tbsite.setEnabled(false);
        itemmodel.setRowCount(0);
        itemtable.setModel(itemmodel);
        tbqty.setEnabled(false);
        tbitem.setEnabled(false);
        tbprice.setEnabled(false);
        tbcc.setEnabled(false);
        tbacct.setEnabled(false);
        override.setEnabled(false);
        tbsite.setEnabled(false);
        dddept.setEnabled(false);
        ddvend.setEnabled(false);
        dddept.setEnabled(false);
        
        caldate.setEnabled(false);
        tbvendnbr.setEnabled(false);
        btAddItem.setEnabled(false);
        btdelitem.setEnabled(false);
        ddtype.setEnabled(false);
        btprint.setEnabled(false);
    }
    
    public void enableall_onNew() {
        tbrequestor.setEnabled(false);
        tbamt.setEnabled(true);
        tbAR.setEnabled(true);
        tbcc.setEnabled(true);
        tbacct.setEnabled(true);
        tbproject.setEnabled(true);
        tbdesc.setEnabled(true);
        tbpo.setEnabled(true);
        tbstatus.setEnabled(true);
        btPurchReqNew.setEnabled(false);
        btPurchReqAdd.setEnabled(true);
        btPurchReqEdit.setEnabled(false);
        btPurchReqGet.setEnabled(false);
        tbsite.setEnabled(false);
        itemmodel.setRowCount(0);
        itemtable.setModel(itemmodel);
        tbqty.setEnabled(true);
        tbitem.setEnabled(true);
        tbprice.setEnabled(true);
        tbcc.setEnabled(true);
        tbacct.setEnabled(true);
        override.setEnabled(true);
        
        dddept.setEnabled(true);
        ddvend.setEnabled(true);
        dddept.setEnabled(true);
         caldate.setEnabled(true);
        tbvendnbr.setEnabled(true);
        btAddItem.setEnabled(true);
        btdelitem.setEnabled(true);
         ddtype.setEnabled(true);
        btprint.setEnabled(true);
    }
    
     public void enableall_onGet() {
        tbrequestor.setEnabled(false);
        tbamt.setEnabled(true);
        tbAR.setEnabled(true);
        tbcc.setEnabled(true);
        tbacct.setEnabled(true);
        tbproject.setEnabled(true);
        tbdesc.setEnabled(true);
        tbpo.setEnabled(true);
        tbstatus.setEnabled(true);
        btPurchReqNew.setEnabled(false);
        btPurchReqAdd.setEnabled(false);
        btPurchReqEdit.setEnabled(true);
        btPurchReqGet.setEnabled(false);
        tbsite.setEnabled(true);
        itemmodel.setRowCount(0);
        itemtable.setModel(itemmodel);
        tbqty.setEnabled(true);
        tbitem.setEnabled(true);
        tbprice.setEnabled(true);
        tbcc.setEnabled(true);
        tbacct.setEnabled(true);
        override.setEnabled(true);
        
        dddept.setEnabled(true);
        ddvend.setEnabled(true);
        dddept.setEnabled(true);
         caldate.setEnabled(true);
        tbvendnbr.setEnabled(true);
        btAddItem.setEnabled(true);
        btdelitem.setEnabled(true);
         ddtype.setEnabled(true);
        btprint.setEnabled(true);
    }
    
    public void reinitvariables(String myid) {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        tbnbr.setEnabled(true);
        tbnbr.setText(myid);
        tbrequestor.setText("");
        tbamt.setText("");
        tbAR.setText("");
        tbcc.setText("");
        tbacct.setText("");
        tbproject.setText("");
        tbdesc.setText("");
        tbpo.setText("");
        tbstatus.setText("");
        caldate.setDate(now);
        btPurchReqNew.setEnabled(true);
        btPurchReqAdd.setEnabled(false);
        btPurchReqEdit.setEnabled(false);
        btPurchReqGet.setEnabled(true);
        tbsite.setText(OVData.getDefaultSite());
        itemmodel.setRowCount(0);
        itemtable.setModel(itemmodel);
        tbqty.setText("");
        tbitem.setText("");
        tbprice.setText("");
        tbcc.setEnabled(false);
        tbacct.setEnabled(false);
        override.setEnabled(false);
        override.setSelected(false);
        tbsite.setEnabled(false);
        
        
        
        set_admins();
       
        itemtable.setModel(itemmodel);
        initddtype(bsmf.MainFrame.userid);
        initddvend();
        initdddept();
        ddvend.setSelectedIndex(0);
        dddept.setSelectedIndex(0);
        jScrollPane2.setVisible(false);

    }
    
    public void initvars(String myid) {
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        tbrequestor.setText("");
        tbamt.setText("");
        tbAR.setText("");
        tbcc.setText("");
        tbacct.setText("");
        tbproject.setText("");
        tbdesc.setText("");
        tbpo.setText("");
        tbstatus.setText("");
        caldate.setDate(now);
        btPurchReqNew.setEnabled(true);
        btPurchReqAdd.setEnabled(false);
        btPurchReqEdit.setEnabled(false);
        btPurchReqGet.setEnabled(true);
        tbsite.setText(OVData.getDefaultSite());
        itemmodel.setRowCount(0);
        itemtable.setModel(itemmodel);
        tbqty.setText("");
        tbitem.setText("");
        tbprice.setText("");
        tbcc.setEnabled(false);
        tbacct.setEnabled(false);
        override.setEnabled(false);
        override.setSelected(false);
        tbsite.setEnabled(false);
        tbprice.setBackground(Color.white);
        tbqty.setBackground(Color.white);
        
        
        
        set_admins();
       
        itemtable.setModel(itemmodel);
        initddtype(bsmf.MainFrame.userid);
        initddvend();
        initdddept();
        if (ddvend.getItemCount() > 0)
        ddvend.setSelectedIndex(0);
        
        if (dddept.getItemCount() > 0)
        dddept.setSelectedIndex(0);
        
        jScrollPane2.setVisible(false);
        
        
        
        if (! myid.isEmpty()) {
           enableall_onGet();
        getreqmstrinfo(myid.toString());
        btPurchReqAdd.setEnabled(false);
        jScrollPane2.setVisible(true);
        } else {
            disableall();
        tbnbr.setEnabled(true);
        tbnbr.setText("");
        }
        

    }
    
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
                 bsmf.MainFrame.show("Cannot send email to requestor");
            }
            bsmf.MainFrame.con.close();
        }
        catch (Exception e){
            e.printStackTrace();
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
                                tbcc.setEnabled(true);
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
                 bsmf.MainFrame.show("Cannot set admins");
            }
            bsmf.MainFrame.con.close();
        }
        catch (Exception e){
            e.printStackTrace();
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
                 bsmf.MainFrame.show("Cannot set all tasks approved with override");
            }
            bsmf.MainFrame.con.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
       
    }
    
    public void settotal() {
        double totamt = 0.00;
        double myqty = 0;
        double myprice = 0.00;
        DecimalFormat df = new DecimalFormat("###.##");
        
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
    
    
    public void getreqmstrinfo(String myid) {
        
        try {
            
            /* set the admins from the req_ctrl file */
            
           btPurchReqEdit.setEnabled(true);
           
            reqtask.setRowCount(0);
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select * from req_mstr where req_id = " + "'" + myid + "'" + ";");
                while (res.next()) {
                    i++;
                    tbdesc.setText(res.getString("req_desc"));
                    ddvend.setSelectedItem(res.getString("req_vend"));
                    ddtype.setSelectedItem(res.getString("req_type"));
                    dddept.setSelectedItem(res.getString("req_dept"));
                    tbrequestor.setText(res.getString("req_name"));
                    caldate.setDate(Date.valueOf(res.getString("req_date")));
                    tbpo.setText(res.getString("req_po"));
                    tbstatus.setText(res.getString("req_status"));
                    tbamt.setText(res.getString("req_amt"));
                    tbAR.setText(res.getString("req_AR"));
                    tbproject.setText(res.getString("req_proj"));
                    tbacct.setText(res.getString("req_acct"));
                    tbcc.setText(res.getString("req_cc"));
                    tbsite.setText(res.getString("req_site"));
                    tbsite.setEnabled(false);
                    if (!myid.isEmpty()) {
                        tbnbr.setText(myid);
                        jScrollPane2.setVisible(true);
                    }
                    tbnbr.setEnabled(false);
                }

                
               itemmodel.setRowCount(0);
                res = st.executeQuery("select * from req_det where reqd_id = " + "'" + myid + "'" + ";");
                while (res.next()) {
                    itemmodel.addRow(new Object[]{res.getString("reqd_item"), res.getString("reqd_qty"), res.getString("reqd_price")}); 
                }
                itemtable.setModel(itemmodel);
                
                
                res = st.executeQuery("select * from req_task where reqt_id = " + "'" + myid + "'" + " order by reqt_sequence ;" );
                while (res.next()) {
                reqtask.addRow(new Object[]{res.getString("reqt_id"), res.getString("reqt_owner"), res.getString("reqt_status"), res.getString("reqt_sequence")});
                }
                jTable1.setModel(reqtask);
                jTable1.getColumn("Sequence").setCellRenderer(new ButtonRenderer());
                jTable1.getColumn("Sequence").setCellEditor(
                        new ButtonEditor(new JCheckBox()));
               
                
                 for (int j = 0; j < jTable1.getRowCount(); j++) {
                    if (jTable1.getModel().getValueAt(j, 2).toString().compareTo("approved") == 0) {
                   // jTable1.getColumn("Sequence").setCellEditor(null);
                        if (! isadmin())
                        btPurchReqEdit.setEnabled(false);
                    }
                 }
                
                
                if (i > 0) {
                    btPurchReqNew.setEnabled(false);
                    btPurchReqAdd.setEnabled(false);
                    btPurchReqGet.setEnabled(false);
                     if (tbstatus.getText().compareTo("approved") == 0 ) {
                         tbstatus.setForeground(Color.green);
                         btPurchReqEdit.setEnabled(false);
                     }
                    if (tbstatus.getText().compareTo("pending") == 0 ) {
                    tbstatus.setForeground(Color.red);
                    }
                }
               

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("unable to retrieve requisition record");
               
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void noApprovalUpdate(String myid) {
        int mypo = OVData.getNextNbr("PO");
                         bsmf.MainFrame.show("Req is approved....PO Number = " + String.valueOf(mypo));
                         tbpo.setText(String.valueOf(mypo));
                          tbstatus.setForeground(Color.green);
                         tbstatus.setText("approved");
                        // sendEmailToRequestor(tbrequestor.getText(), String.valueOf(mypo));
          
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
                bsmf.MainFrame.show("unable to update req_mstr for no approval scenario");
            }
              } catch (Exception e) {
            e.printStackTrace();
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
                         tbstatus.setText("approved");
                       
                         if (emailrequestor)
                         sendEmailToRequestor(myid, String.valueOf(mypo));
                 }
                 
                 if (islast)
                 bsmf.MainFrame.show("Req is approved....PO Number = " + String.valueOf(mypo));
                 
                 if (! islast)
                 bsmf.MainFrame.show("You have Successfully Approved");  
                 
                 // reinit the task list
                res = st.executeQuery("select * from req_task where reqt_id = " + "'" + myid + "'" + " order by reqt_sequence ;");
                while (res.next()) {
                reqtask.addRow(new Object[]{res.getString("reqt_id"), res.getString("reqt_owner"), res.getString("reqt_status"), res.getString("reqt_sequence")});
                }
                jTable1.setModel(reqtask);
                jTable1.getColumn("Sequence").setCellRenderer(new ButtonRenderer());
                jTable1.getColumn("Sequence").setCellEditor(
                        new ButtonEditor(new JCheckBox()));
               
                

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to approve Req");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
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
        tbnbr = new javax.swing.JTextField();
        btPurchReqNew = new javax.swing.JButton();
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
        btPurchReqAdd = new javax.swing.JButton();
        btPurchReqEdit = new javax.swing.JButton();
        btPurchReqGet = new javax.swing.JButton();
        tbacct = new javax.swing.JTextField();
        tbcc = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        itemtable = new javax.swing.JTable();
        tbitem = new javax.swing.JTextField();
        tbqty = new javax.swing.JTextField();
        tbprice = new javax.swing.JTextField();
        btAddItem = new javax.swing.JButton();
        btdelitem = new javax.swing.JButton();
        tbpo = new javax.swing.JTextField();
        tbstatus = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbdesc = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        tbvendnbr = new javax.swing.JTextField();
        tbAR = new javax.swing.JTextField();
        tbproject = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        override = new javax.swing.JCheckBox();
        btprint = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        tbsite = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Requisition Maintenance"));
        jPanel1.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N

        jLabel1.setText("Requested By:");

        btPurchReqNew.setText("New");
        btPurchReqNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPurchReqNewActionPerformed(evt);
            }
        });

        jLabel2.setText("Req Number:");

        jLabel3.setText("Req Type:");

        jLabel4.setText("Req Date:");

        caldate.setDateFormatString("yyyy-MM-dd");

        jLabel5.setText("Total:");

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

        jLabel6.setText("Vendor:");

        jLabel7.setText("Dept:");

        btPurchReqAdd.setText("Submit");
        btPurchReqAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPurchReqAddActionPerformed(evt);
            }
        });

        btPurchReqEdit.setText("Edit");
        btPurchReqEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPurchReqEditActionPerformed(evt);
            }
        });

        btPurchReqGet.setText("Get");
        btPurchReqGet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPurchReqGetActionPerformed(evt);
            }
        });

        jLabel11.setText("Acct:");

        jLabel12.setText("CC:");

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

        tbitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbitemActionPerformed(evt);
            }
        });

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        btAddItem.setText("AddItem");
        btAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddItemActionPerformed(evt);
            }
        });

        btdelitem.setText("DelItem");
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        tbpo.setEditable(false);

        tbstatus.setEditable(false);
        tbstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbstatusActionPerformed(evt);
            }
        });

        jLabel8.setText("PO:");

        jLabel9.setText("Status");

        tbdesc.setColumns(20);
        tbdesc.setLineWrap(true);
        tbdesc.setRows(5);
        tbdesc.setWrapStyleWord(true);
        jScrollPane1.setViewportView(tbdesc);

        jLabel10.setText("Remarks");

        jLabel13.setText("AR#");

        jLabel14.setText("Project");

        jLabel15.setText("Item");

        jLabel16.setText("Qty");

        jLabel17.setText("Price");

        override.setText("Override");

        btprint.setText("Print");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable1);

        jLabel18.setText("Site");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(4, 4, 4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbAR)
                            .addComponent(tbpo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbacct, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel9)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbcc, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbstatus, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                            .addComponent(tbproject))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(override)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btprint)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btPurchReqEdit)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btPurchReqAdd)))
                                .addContainerGap())))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(btPurchReqNew)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(btPurchReqGet)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel18)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(tbsite, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(28, 28, 28))
                                        .addComponent(jLabel10)
                                        .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel15)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(btAddItem)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btdelitem))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                    .addGap(35, 35, 35)
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                            .addComponent(jLabel7)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(dddept, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                            .addComponent(jLabel6)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(tbvendnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(ddvend, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(caldate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                            .addComponent(jLabel1)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(tbrequestor, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                            .addComponent(jLabel2)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(tbnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                            .addComponent(jLabel3)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(66, 66, 66)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel16)
                                                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel17))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(241, 241, 241)
                                                        .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addGap(11, 11, 11)))))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 3, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btPurchReqNew)
                            .addComponent(btPurchReqGet))
                        .addGap(90, 90, 90)
                        .addComponent(jLabel10)
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(tbsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbrequestor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(tbvendnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dddept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(caldate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btAddItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btPurchReqEdit)
                            .addComponent(btPurchReqAdd)
                            .addComponent(btprint))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(tbcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbproject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(override))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btPurchReqGetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPurchReqGetActionPerformed
        enableall_onGet();
        getreqmstrinfo(tbnbr.getText().toString());
        btPurchReqAdd.setEnabled(false);
        jScrollPane2.setVisible(true);
       this.revalidate();
    }//GEN-LAST:event_btPurchReqGetActionPerformed

    private void btPurchReqNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPurchReqNewActionPerformed
        
                tbnbr.setText(String.valueOf(OVData.getNextNbr("purchreq")));
                tbnbr.setEnabled(false);
                java.util.Date now = new java.util.Date();
                caldate.setDate(now);
               
                tbrequestor.setText(bsmf.MainFrame.userid.toString());
          

                enableall_onNew();

              //  this.revalidate();

          
    }//GEN-LAST:event_btPurchReqNewActionPerformed

    private void btPurchReqAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPurchReqAddActionPerformed
       try {
           
           boolean canproceed = true;
           boolean preapproved = false;
           String mystatus = "";
           String firstapprover = "";
           boolean sendemail = false;
           
            for (int i = 0; i < itemtable.getRowCount(); i++) {
             if (itemtable.getModel().getValueAt(i, 0) == null || itemtable.getModel().getValueAt(i, 0).toString().isEmpty()) {
            bsmf.MainFrame.show("If any cell is occupied...all must be occupied");
            canproceed = false;    
            break;    
            }
            }
           
           
           if (canproceed) {
               
               
            javax.swing.JTable mytable = new javax.swing.JTable();
                    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                   "id", "owner", "status", "Sequence", "Email"
                   });
           
           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                double threshold = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                
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
                
                if (proceed) {
                    st.executeUpdate("insert into req_mstr "
                        + "(req_id, req_name, req_date, req_vend, "
                        + "req_type, req_dept, req_amt,  req_desc, req_acct, req_cc, req_po, req_status, req_site, req_vendnbr )"
                        + " values ( " + "'" + tbnbr.getText().toString() + "'" + ","
                        + "'" + tbrequestor.getText() + "'" + ","
                        + "'" + dfdate.format(caldate.getDate()).toString() + "'" + ","
                        + "'" + ddvend.getSelectedItem() + "'" + ","
                        + "'" + ddtype.getSelectedItem() + "'" + ","
                        + "'" + dddept.getSelectedItem() + "'" + ","
                        + "'" + tbamt.getText() + "'" + ","
                        + "'" + rmks + "'" + ","
                            + "''" + ","
                            + "''" + ","
                            + "''" + ","
                        + "'" + status.toString() + "'"  + ","
                        + "'" + tbsite.getText() + "'" + ","
                        + "'" + tbvendnbr.getText() + "'"
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
                        + " values ( " + "'" + tbnbr.getText().toString() + "'" + ","
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
                         mymodel.addRow(new Object[]{tbnbr.getText().toString(), res.getString("reqa_owner"), "queued", res.getString("reqa_sequence"), res.getString("reqa_email") }); 
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
                        bsmf.MainFrame.show("cannot insert req_task");
                        }
                        mystatus = "queued";
                      }
                      
                 
                    }
                     /* let send email to first approver */
                     if (sendemail) {
                     String subject = "Requisition Approval Alert";
                     String body = "Requisition number " + tbnbr.getText() + " requires your approval";
                     String requestor = "Requestor = " + tbrequestor.getText();
                     String amount = "Amount = " + tbamt.getText();
                     body = body + "\n" + requestor + "\n" + amount;
                     OVData.sendEmailByID(firstapprover, subject, body, "");
                     }
                  bsmf.MainFrame.show("Added Requisition Record");    
                  } else {
                      noApprovalUpdate(tbnbr.getText());
                    
                  }
                    
                    
                    itemmodel.setNumRows(0);
                    initvars("");
                    disableall();
                    //OVData.sendEmail("terry.vaughn@avmind.com", "test", "test", "");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Sql Cannot Add ReqMstr");
            }
             bsmf.MainFrame.con.close();
             
             
             
           } // if can proceed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btPurchReqAddActionPerformed

    private void tbitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbitemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbitemActionPerformed

    private void btAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddItemActionPerformed
       boolean canproceed = true;
        itemtable.setModel(itemmodel);
       
        if (tbitem.getText().length() > 80) {
            bsmf.MainFrame.show("item length cannot exceed 80 chars");
            canproceed = false;
            return;
        }
        
        if (tbprice.getText().isEmpty()) {
           bsmf.MainFrame.show("Invalid Price");
           canproceed = false;
           tbprice.setBackground(Color.yellow);
           tbprice.requestFocus();
           return;
        }
        if (tbqty.getText().isEmpty()) {
           bsmf.MainFrame.show("Invalid Qty");
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
            JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Removing row " + i);
            ((javax.swing.table.DefaultTableModel) itemtable.getModel()).removeRow(i);
        }
        settotal();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void tbstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbstatusActionPerformed
       if (tbstatus.getText().compareTo("approved") == 0 ) {
           tbstatus.setForeground(Color.green);
       }
       if (tbstatus.getText().compareTo("pending") == 0 ) {
           tbstatus.setForeground(Color.yellow);
       }
    }//GEN-LAST:event_tbstatusActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
         
       
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
                tbvendnbr.setText(res.getString("vd_addr"));
                    
                }

            } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Unable to get selected vd_name");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }//GEN-LAST:event_ddvendItemStateChanged

    private void btPurchReqEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPurchReqEditActionPerformed
       boolean canproceed = true;
       boolean isadmin = false;
       
       for (String admin : admins) {
          if (admin.compareTo(bsmf.MainFrame.userid) == 0) {
              isadmin = true;
          }  
        }
       for (int i = 0; i < reqtask.getRowCount(); i++) {
           if (reqtask.getValueAt(i, 2).toString().compareTo("approved") == 0) 
             canproceed = false;
       }
       
      
       if (canproceed || isadmin) {
           
           try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
               st.executeUpdate("update req_mstr set " + 
                       " req_desc = " + "'" + tbdesc.getText() + "'" + "," +
                       " req_vend = " + "'" + ddvend.getSelectedItem() + "'" + "," +
                       " req_type = " + "'" + ddtype.getSelectedItem() + "'" + "," +
                       " req_dept = " + "'" + dddept.getSelectedItem() + "'" + "," +
                       " req_amt = " + "'" + tbamt.getText() + "'" + "," +
                       " req_acct = " + "'" + tbacct.getText() + "'" + "," +
                       " req_cc = " + "'" + tbcc.getText() + "'" + "," +
                       " req_AR = " + "'" + tbAR.getText() + "'" + "," +
                       " req_proj = " + "'" + tbproject.getText() + "'" +                      
                       " where " + 
                        " req_id = " + "'" + tbnbr.getText() + "'" +  ";");

                 // let's remove all items associated with this req id
                       st.executeUpdate("delete from req_det where reqd_id = " + "'" + tbnbr.getText() + "'" +  ";");
                    //  now add the new ones just updated
                       for (int i = 0; i < itemtable.getRowCount(); i++) {
                        
                        if (itemtable.getModel().getValueAt(i, 0) == null &&
                        itemtable.getModel().getValueAt(i, 1) == null &&
                        itemtable.getModel().getValueAt(i, 2) == null) {
                        continue;
                        }        
                        
                    st.executeUpdate("insert into req_det "
                        + "(reqd_id, reqd_item, reqd_qty, reqd_price )"
                        + " values ( " + "'" + tbnbr.getText().toString() + "'" + ","
                        + "'" + itemtable.getValueAt(i, 0) + "'" + ","
                        + "'" + itemtable.getValueAt(i, 1) + "'" + ","
                        + "'" + itemtable.getValueAt(i, 2) + "'"
                        + ")"
                        + ";");
                        
                    }
               
               
               bsmf.MainFrame.show("Updated Requisition Successfully");
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Unable to get selected vd_name");
            }
              } catch (Exception e) {
            e.printStackTrace();
        }   
                
           
       } else {
           bsmf.MainFrame.show("Partially or Completely approved...cannot edit");
       }
       
       
    }//GEN-LAST:event_btPurchReqEditActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "SHIPPER");
                hm.put("myid",  tbnbr.getText().toString());
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
                bsmf.MainFrame.show("Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
      
    }//GEN-LAST:event_btprintActionPerformed

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
        String x = BlueSeerUtils.bsformat("", tbprice.getText(), "2");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
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
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddItem;
    private javax.swing.JButton btPurchReqAdd;
    private javax.swing.JButton btPurchReqEdit;
    private javax.swing.JButton btPurchReqGet;
    private javax.swing.JButton btPurchReqNew;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btprint;
    private com.toedter.calendar.JDateChooser caldate;
    private javax.swing.JComboBox dddept;
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
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JTable jTable1;
    private javax.swing.JCheckBox override;
    private javax.swing.JTextField tbAR;
    private javax.swing.JTextField tbacct;
    private javax.swing.JTextField tbamt;
    private javax.swing.JTextField tbcc;
    private javax.swing.JTextArea tbdesc;
    private javax.swing.JTextField tbitem;
    private javax.swing.JTextField tbnbr;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbproject;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbrequestor;
    private javax.swing.JTextField tbsite;
    private javax.swing.JTextField tbstatus;
    private javax.swing.JTextField tbvendnbr;
    // End of variables declaration//GEN-END:variables
}
