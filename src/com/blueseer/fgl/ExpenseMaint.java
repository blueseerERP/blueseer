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
package com.blueseer.fgl;

import bsmf.MainFrame;
import static bsmf.MainFrame.dfdate;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
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
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import static com.blueseer.utl.OVData.getDueDateFromTerms;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;


/**
 *
 * @author vaughnte
 */
public class ExpenseMaint extends javax.swing.JPanel implements IBlueSeer {
                
                 // global variable declarations
                boolean isLoad = false;
                String terms = "";
                String apacct = "";
                String apcc = "";
                String apbank = "";
                double actamt = 0.00;
                double control = 0.00;
                double rcvamt = 0.00;
                int voucherline = 0;
               
    
    // global datatablemodel declarations       
                
                
                 javax.swing.table.DefaultTableModel expensemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("price"),
                getGlobalColumnTag("recvid"), 
                getGlobalColumnTag("recvline"),
                getGlobalColumnTag("acct"),
                getGlobalColumnTag("cc")
            });
                
    /**
     * Creates new form ShipMaintPanel
     */
    public ExpenseMaint() {
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
        tbkey.setText("");
      
         terms = "";
         apacct = "";
         apcc = "";
         apbank = "";
         actamt = 0.00;
         control = 0.00;
         rcvamt = 0.00;
        
        lbvendor.setText("");
        lbmessage.setText("");
        lbmessage.setForeground(Color.blue);
        
        tbrmks.setText("");
        tbcheck.setText("");
        tbitemservice.setText("");
        tbqty.setText("");
        tbprice.setText("");
        tbactualamt.setText("0");
        tbactualamt.setBackground(Color.white);
        tbactualamt.setEditable(false);
       
        expensemodel.setRowCount(0);
        expensedet.setModel(expensemodel);
        
       
        
        java.util.Date now = new java.util.Date();
        dcdate.setEnabled(true);
        dcdate.setDate(now);
        
       
        ddvend.removeAllItems();
        ArrayList myvend = OVData.getvendmstrlist();
        for (int i = 0; i < myvend.size(); i++) {
            ddvend.addItem(myvend.get(i));
        }
        ddvend.insertItemAt("", 0);
        ddvend.setSelectedIndex(0);
          
        ddsite.removeAllItems();
        ArrayList mylist = OVData.getSiteList();
        for (int i = 0; i < mylist.size(); i++) {
            ddsite.addItem(mylist.get(i));
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
           ddacct.removeAllItems();
        ArrayList<String> myaccts = OVData.getGLAcctListByType("E");
        for (String code : myaccts) {
            ddacct.addItem(code);
        }
        
           ddcc.removeAllItems();
        ArrayList<String> mycc = OVData.getGLCCList();
        for (String code : mycc) {
            ddcc.addItem(code);
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
    
    public String[] setAction(int i) {
        String[] m = new String[2];
        DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        if (i > 0) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   tbactualamt.setText(df.format(actamt));
                   lbmessage.setText("Batch has been committed");
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

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            Statement st = bsmf.MainFrame.con.createStatement();
            ResultSet res = null;
            boolean error = false;
            try {
                boolean proceed = true;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US)); 
                Date duedate = OVData.getDueDateFromTerms(dcdate.getDate(), terms);
                
                proceed = validateInput("addRecord");
                
                if (proceed) {

                    res = st.executeQuery("SELECT cur_id FROM  cur_mstr where cur_id = " + "'" + x[0] + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    
                       st.executeUpdate("insert into ap_mstr "
                        + "(ap_vend, ap_site, ap_nbr, ap_amt, ap_type, ap_check, ap_rmks, "
                        + "ap_entdate, ap_effdate, ap_duedate, ap_acct, ap_cc, "
                        + "ap_terms, ap_status, ap_bank ) "
                        + " values ( " + "'" + ddvend.getSelectedItem() + "'" + ","
                              + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + tbkey.getText() + "'" + ","
                        + "'" + df.format(actamt) + "'" + ","
                        + "'" + "V" + "'" + ","
                        + "'" + tbcheck.getText().replace("'", "''") + "'" + ","        
                        + "'" + tbrmks.getText().replace("'", "''") + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + dfdate.format(duedate) + "'" + ","
                        + "'" + apacct + "'" + ","
                        + "'" + apcc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + "o" + "'"  + ","
                        + "'" + apbank + "'"
                        + ")"
                        + ";");
                       
                       for (int j = 0; j < expensedet.getRowCount(); j++) {
                        st.executeUpdate("insert into vod_mstr "
                            + "(vod_id, vod_vend, vod_rvdid, vod_rvdline, vod_part, vod_qty, "
                            + " vod_voprice, vod_date, vod_invoice, vod_expense_acct, vod_expense_cc )  "
                            + " values ( " + "'" + tbkey.getText() + "'" + ","
                                + "'" + ddvend.getSelectedItem().toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 2).toString().replace("'", "''") + "'" + ","
                            + "'" + expensedet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                            + "'" + tbcheck.getText() + "'" + ","        
                            + "'" + expensedet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 8).toString() + "'"
                            + ")"
                            + ";");
                  
                     }
                      
                     /* create gl_tran records */
                        if (! error)
                        error = OVData.glEntryFromVoucherExpense(tbkey.getText(), dcdate.getDate());
                         
                        if (! error)
                        error = OVData.APExpense(dcdate.getDate(), OVData.getNextNbr("expensenumber"), tbkey.getText(), tbrmks.getText(), ddvend.getSelectedItem().toString(), actamt, "AP-Expense");
                           
                  if (! error) {        
                   m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                   initvars(null);
                  } else {
                   m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
                  }
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                 m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (bsmf.MainFrame.con != null) bsmf.MainFrame.con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
             m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordConnError};
        }
     
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     m = new String[]{BlueSeerUtils.ErrorBit, "This update functionality is not implemented at this time"};
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        m = new String[]{BlueSeerUtils.ErrorBit, "This delete functionality is not implemented at this time"};
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
       
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            Statement st = bsmf.MainFrame.con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                actamt = 0.00;
                res = st.executeQuery("select * from ap_mstr where ap_nbr = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                  i++;
                     tbkey.setText(res.getString("ap_nbr"));
                     dcdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("ap_effdate")));
                     tbcheck.setText(res.getString("ap_check"));
                     tbrmks.setText(res.getString("ap_rmks"));
                     ddvend.setSelectedItem(res.getString("ap_vend"));
                     ddsite.setSelectedItem(res.getString("ap_site"));
                }
                
                res = st.executeQuery("select * from vod_mstr where vod_id = " + "'" + x[0] + "'" + ";");
                while (res.next()) {
                //  "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
                     expensemodel.addRow(new Object[] { res.getString("vod_id"),
                                              res.getString("vod_rvdline"),
                                              res.getString("vod_part"),
                                              res.getString("vod_qty"),
                                              res.getString("vod_voprice"),
                                              res.getString("vod_rvdid"),
                                              res.getString("vod_rvdline"),
                                              res.getString("vod_expense_acct"),
                                              res.getString("vod_expense_cc")
                                              });
                 
                  
                  actamt += res.getDouble("vod_voprice");
               
                }
               
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (bsmf.MainFrame.con != null) bsmf.MainFrame.con.close();
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
         luModel = DTData.getExpenseBrowseUtil(luinput.getText(),0, "ap_nbr");
        } else {
         luModel = DTData.getExpenseBrowseUtil(luinput.getText(),0, "ap_vend");   
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
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), getClassLabelTag("lblvendor", this.getClass().getSimpleName())); 
        
        
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


                res = st.executeQuery("select vd_name, vd_ap_acct, vd_ap_cc, vd_terms, vd_bank from vd_mstr where vd_addr = " + "'" + vendor + "'" + ";");
                while (res.next()) {
                    i++;
                   apacct = res.getString("vd_ap_acct");
                   apcc = res.getString("vd_ap_cc");
                   terms = res.getString("vd_terms");
                   apbank = res.getString("vd_bank");
                   lbvendor.setText(res.getString("vd_name"));
                   
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, this.getClass().getEnclosingMethod().getName()));
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
        jLabel36 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        expensedet = new javax.swing.JTable();
        ddvend = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel35 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbitemservice = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        ddacct = new javax.swing.JComboBox<>();
        ddcc = new javax.swing.JComboBox<>();
        lbacct = new javax.swing.JLabel();
        lbvendor = new javax.swing.JLabel();
        tbcheck = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lbmessage = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("Expense Nbr");
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

        btadditem.setText("Add Item");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btadd.setText("Commit");
        btadd.setName("btcommit"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        expensedet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(expensedet);

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

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel35.setText("ExpenseDate");
        jLabel35.setName("btdate"); // NOI18N

        jLabel28.setText("Total Amount");
        jLabel28.setName("lbltotalamt"); // NOI18N

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
        jLabel9.setName("lblexpenseacct"); // NOI18N

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        jLabel10.setText("Site");
        jLabel10.setName("lblsite"); // NOI18N

        ddacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddacctActionPerformed(evt);
            }
        });

        jLabel1.setText("CheckNbr");
        jLabel1.setName("lblchecknbr"); // NOI18N

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9)
                            .addComponent(jLabel24)
                            .addComponent(jLabel36)
                            .addComponent(jLabel10)
                            .addComponent(jLabel8)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(13, 13, 13)
                                                .addComponent(btnew)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btclear))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel35)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lbmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap(20, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbcheck, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(361, 361, 361)
                                .addComponent(btadd))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 775, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(644, 644, 644)
                .addComponent(btadditem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdeleteitem)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lbmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(42, 42, 42))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnew)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24)
                                .addComponent(btclear))
                            .addComponent(btlookup))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10)
                                .addComponent(jLabel35))
                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9))
                    .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadditem)
                    .addComponent(btdeleteitem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addGap(18, 18, 18))
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
       // receiverdet  "Part", "PO", "line", "Qty",  listprice, disc, netprice, loc, serial, lot, recvID, recvLine
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
      DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));   
            voucherline++;
            actamt += Double.valueOf(tbqty.getText()) * 
                          Double.valueOf(tbprice.getText());
            expensemodel.addRow(new Object[] { "", voucherline,
                                                  tbitemservice.getText(),
                                                  tbqty.getText(),
                                                  tbprice.getText(),
                                                  "expense",
                                                  "0",
                                                  ddacct.getSelectedItem().toString(),
                                                  ddcc.getSelectedItem().toString()
                                                  });
       
        tbitemservice.setText("");
        tbqty.setText("");
        tbprice.setText("");
        tbitemservice.requestFocus();
        tbactualamt.setText(df.format(actamt));
        
        
        
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
         if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
       if ( ddvend.getSelectedItem() != null && ! ddvend.getSelectedItem().toString().isEmpty()  && ! isLoad) {
            setvendorvariables(ddvend.getSelectedItem().toString());
        } // ddvend
    }//GEN-LAST:event_ddvendActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = expensedet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031, String.valueOf(i)));
             actamt -= Double.valueOf(expensedet.getModel().getValueAt(i,3).toString()) * Double.valueOf(expensedet.getModel().getValueAt(i,4).toString());
            ((javax.swing.table.DefaultTableModel) expensedet.getModel()).removeRow(i);
           voucherline--;
        }
        tbactualamt.setText(String.valueOf(actamt));
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddsiteActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
        if (ddacct.getSelectedItem() != null && ! isLoad )
        try {
            
        
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select ac_desc from ac_mstr where ac_id = " + "'" + ddacct.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbacct.setText(res.getString("ac_desc"));
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, this.getClass().getEnclosingMethod().getName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_ddacctActionPerformed

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

    private void tbpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusGained
         if (tbprice.getText().equals("0")) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

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

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddacct;
    private javax.swing.JComboBox<String> ddcc;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JTable expensedet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel lbacct;
    private javax.swing.JLabel lbmessage;
    private javax.swing.JLabel lbvendor;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbcheck;
    private javax.swing.JTextField tbitemservice;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbrmks;
    // End of variables declaration//GEN-END:variables
}
