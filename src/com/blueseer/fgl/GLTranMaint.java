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
package com.blueseer.fgl;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.fgl.fglData.addGL;
import static com.blueseer.fgl.fglData.deleteGL;
import com.blueseer.fgl.fglData.gl_tran;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsFormatInt;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalLabelTag;
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
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.DTData;
import static com.blueseer.utl.OVData.canUpdate;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */
public class GLTranMaint extends javax.swing.JPanel {

    /**
     * Creates new form UserMaintPanel
     */
    
    double positiveamt = 0;
    String type = "";
    boolean isLoad = false;
    
    public static javax.swing.table.DefaultTableModel lookUpModel = null;
                public static JTable lookUpTable = new JTable();
                public static MouseListener mllu = null;
                public static JDialog dialog = new JDialog();
                
                public static ButtonGroup bg = null;
                public static JRadioButton rb1 = null;
                public static JRadioButton rb2 = null;
    
           DefaultTableCellRenderer tableRender = new DefaultTableCellRenderer();
    javax.swing.table.DefaultTableModel transmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("account"), 
                getGlobalColumnTag("costcenter"),
                getGlobalColumnTag("description"),
                getGlobalColumnTag("amount"), 
                getGlobalColumnTag("number"),
                getGlobalColumnTag("reference"),
                getGlobalColumnTag("type")
            })    {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
    
    public void tallyamount() {
        
        double amt = 0;
        positiveamt = 0;
        for (int i = 0; i < transtable.getRowCount(); i++) {
             amt += bsParseDouble(transtable.getValueAt(i, 4).toString());
             if (bsParseDouble(transtable.getValueAt(i, 4).toString()) > 0) {
                 positiveamt += bsParseDouble(transtable.getValueAt(i, 4).toString());
             }
          }
        
        labeltotal.setText(bsFormatDouble(amt));
    }
    
    public void clearinput() {
       ddacct.setSelectedIndex(0);
       ddacct2.setSelectedIndex(0);
       ddcc.setSelectedIndex(0);
       // tbdesc.setText("");
        tbamt.setText("");
        ddacct.requestFocus();
    }
    
    class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            //Integer percentage = (Integer) table.getValueAt(row, 3);
            //if (percentage > 30)
           // if (table.getValueAt(row, 0).toString().compareTo("1923") == 0)   
            if (column == 0)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
    
    public GLTranMaint() {
        initComponents();
        setLanguageTags(this);
        

    }

    
    public void clearAll() {
        isLoad = true;
        java.util.Date now = new java.util.Date();
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       effdate.setDate(now);
       dateentered.setText(dfdate.format(now));
       tbuserid.setText("");
       tbref.setText("");
       tbcontrolamt.setText("");
       tbamt.setText("");
       tbcontrolamt.setBackground(Color.white);
       tbamt.setBackground(Color.white);
       tbdesc.setBackground(Color.white);
       tbdesc.setText("");
       labeltotal.setText("0.00");
        type = ""; 
       
        transtable.setModel(transmodel);
       transmodel.setNumRows(0);
       transtable.getColumnModel().getColumn(4).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));
       transtable.getTableHeader().setReorderingAllowed(false);
       
       ddsite.removeAllItems();
        ArrayList sites = OVData.getSiteList();
        for (Object site : sites) {
            ddsite.addItem(site);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddacct.removeAllItems();
        ddacct2.removeAllItems();
        ArrayList<String> myacct = fglData.getGLAcctList();
        for (int i = 0; i < myacct.size(); i++) {
            ddacct.addItem(myacct.get(i));
            ddacct2.addItem(myacct.get(i));
        }
            ddacct.setSelectedIndex(0);
            ddacct2.setSelectedIndex(0);
        
        ddcc.removeAllItems();
        fglData.getGLCCList().stream().forEach((s) -> ddcc.addItem(s));
        ddcc.setSelectedItem(OVData.getDefaultCC());
       
        
        ddcurr.removeAllItems();
        fglData.getCurrlist().stream().forEach((s) -> ddcurr.addItem(s));
        ddcurr.setSelectedItem(OVData.getDefaultCurrency());
        
        
       ddsite.setSelectedItem(OVData.getDefaultSite());
       tbuserid.setText(bsmf.MainFrame.userid);
       isLoad = false;
       
    }
    
    public void enableAll() {
       isLoad = true;
       btnew.setEnabled(true);
       btlookup.setEnabled(true);
       btLookUpAccount.setEnabled(true);
       btLookUpAccount2.setEnabled(true);
       btdeleteALL.setEnabled(true);
       btsubmit.setEnabled(true);
        btadd.setEnabled(true);
       btdelete.setEnabled(true);
       ddtype.setSelectedIndex(0);
       ddtype.setEnabled(true); 
       ddsite.setEnabled(true);
       ddcurr.setEnabled(true);
       ddacct.setEnabled(true);
       ddacct2.setEnabled(true);
       ddcc.setEnabled(true);
       tbamt.setEnabled(true);
       tbdesc.setEnabled(true);
       effdate.setEnabled(true);
       transtable.setEnabled(true);
       tbcontrolamt.setEnabled(true);
       isLoad = false;
      
    }
    
    public void disableAll() {
       isLoad = true;
       btnew.setEnabled(false);
       btlookup.setEnabled(false);
       btLookUpAccount.setEnabled(false);
       btLookUpAccount2.setEnabled(false);
       btdeleteALL.setEnabled(false);
       btsubmit.setEnabled(false);
        btadd.setEnabled(false);
       btdelete.setEnabled(false);
       ddtype.setSelectedIndex(0);
       ddtype.setEnabled(false);
       dateentered.setEnabled(false);
       tbuserid.setEnabled(false);
       tbref.setEnabled(false);
       ddsite.setEnabled(false);
       ddcurr.setEnabled(false);
       ddacct.setEnabled(false);
       ddacct2.setEnabled(false);
       ddcc.setEnabled(false);
       tbamt.setEnabled(false);
       tbdesc.setEnabled(false);
       effdate.setEnabled(false);
       transtable.setEnabled(false);
       tbcontrolamt.setEnabled(false);
       isLoad = false;
       
    }
    
    public void getGLTranHist(String docid) {
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
                res = st.executeQuery("SELECT * " +
                        " FROM  gl_hist where glh_doc = " + "'" + docid + "'" +
                        "  ;");
                
                while (res.next()) {
                    i++;
                    tbref.setText(res.getString("glh_ref"));
                    dateentered.setText(res.getString("glh_entdate"));
                    
                    transmodel.addRow(new Object[]{transmodel.getRowCount() + 1,
                    res.getString("glh_acct"),
                    res.getString("glh_cc"),
                    res.getString("glh_desc"),
                    currformat(res.getString("glh_amt")),
                    res.getString("glh_doc"),
                    res.getString("glh_ref"),
                    res.getString("glh_type")
                    });
                }
                
                if (i > 0) {
                    
                    transtable.setModel(transmodel);
                    tallyamount();
                    clearinput();
                    disableAll();
                    btdeleteALL.setEnabled(true);
                    transtable.setEnabled(true);
                    btnew.setEnabled(true);
                    btlookup.setEnabled(true);
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
    
    public void getGLTran(String docid) {
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
                res = st.executeQuery("SELECT * " +
                        " FROM  gl_tran where glt_doc = " + "'" + docid + "'" +
                        "  ;");
                
                while (res.next()) {
                    i++;
                    tbref.setText(res.getString("glt_ref"));
                    dateentered.setText(res.getString("glt_entdate"));
                    
                    transmodel.addRow(new Object[]{transmodel.getRowCount() + 1,
                    res.getString("glt_acct"),
                    res.getString("glt_cc"),
                    res.getString("glt_desc"),
                    currformat(res.getString("glt_amt")),
                    res.getString("glt_doc"),
                    res.getString("glt_ref"),
                    res.getString("glt_type")
                    });
                }
                
                if (i > 0) {
                    
                    transtable.setModel(transmodel);
                    tallyamount();
                    clearinput();
                    disableAll();
                    btdeleteALL.setEnabled(true);
                    transtable.setEnabled(true);
                    btnew.setEnabled(true);
                    btlookup.setEnabled(true);
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
    
    
    public void setLanguageTags(Object myobj) {
      // lblaccount.setText(labels.getString("LedgerAcctMstrPanel.labels.lblaccount"));
      
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
    
     // functions implemented
    
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
                case "delete":
                    message = deleteRecord(key);    
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
            initvars(null);
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
    
    public boolean validateInput(BlueSeerUtils.dbaction x) {
               
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"gl_tran"});
        int fc;
        
        fc = checkLength(f,"glt_ref");        
        if (tbref.getText().length() > fc || tbref.getText().isEmpty()) { 
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbref.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"glt_site");
        if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().length() > fc) { 
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            ddsite.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"glt_curr");
        if (ddcurr.getSelectedItem() == null || ddcurr.getSelectedItem().toString().length() > fc) { 
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            ddcurr.requestFocus();
            return false;
        }
               
        if (! ddtype.getSelectedItem().toString().equals("simple") &&  ! currformatDouble(positiveamt).equals(currformatDouble(bsParseDouble(tbcontrolamt.getText()))) ) {
            String s_positiveamt = bsFormatDouble(positiveamt);
            bsmf.MainFrame.show(getMessageTag(1039, currformat(tbcontrolamt.getText()) + "/" + s_positiveamt));
            return false;
        }

        if (! ddtype.getSelectedItem().toString().equals("simple") && bsParseDouble(labeltotal.getText().toString()) != 0) {
            bsmf.MainFrame.show(getMessageTag(1040));
            return false;
        } 
        
        String[] caldate = fglData.getGLCalForDate(effdate.getDate());
        if (caldate == null || caldate[0].isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1038));
            return false;
        }

        if ( OVData.isGLPeriodClosed(setDateDB(effdate.getDate()))) {
            bsmf.MainFrame.show(getMessageTag(1035));
            return false;
        }
                    
        // if reversing...check and make sure next immediate period is open
        String nextstartdate = "";
        if (! caldate[0].isEmpty() && type.equals("RV")) {
            int nextperiod = Integer.valueOf(caldate[1]) + 1;
            int thisyear = Integer.valueOf(caldate[0]);
              if (nextperiod > 12) {
                  thisyear++;
                  nextperiod = 1;
              }
              ArrayList<String> nextcal = fglData.getGLCalByYearAndPeriod(String.valueOf(thisyear), String.valueOf(nextperiod));
              if (nextcal.isEmpty()) {
               bsmf.MainFrame.show(getMessageTag(1042));
               return false;
              }
              if (! nextcal.isEmpty() && nextcal.get(4).toString().equals("closed")) {
               bsmf.MainFrame.show(getMessageTag(1041));
               return false;
              }
              nextstartdate = String.valueOf(nextcal.get(2));
        }

        if (type.equals("RV") && nextstartdate.isEmpty()) {
             bsmf.MainFrame.show(getMessageTag(1043));
             return false;
        }
        
       
               
        return true;
    }
    
    
    public String[] addRecord(String[] x) {
     String[] m = addGL(createRecord());
      // autopost
        if (OVData.isAutoPost()) {
            fglData.PostGL();
        } 
         return m;
     }
        
    public String[] deleteRecord(String[] x) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteGL(tbref.getText()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
       
        
        return m;  
     }
        
    public void initvars(String[] arg) {
       
       clearAll();
       disableAll();
       
       btnew.setEnabled(true);
       btlookup.setEnabled(true);
       
        if (arg != null && arg.length > 1) {
             if (arg[1].equals("gl_tran")) {
              getGLTran(arg[0]);
             } else {
              getGLTranHist(arg[0]);    
             }
         }  
    }
    
    public ArrayList<gl_tran> createRecord() {
        ArrayList<gl_tran> glv = new ArrayList<gl_tran>();
        String curr = OVData.getDefaultCurrency();
        String basecurr = curr;
        double amt = 0.00;
        // for reversing type
        String[] caldate = fglData.getGLCalForDate(effdate.getDate());
        String nextstartdate = "";
        if (! caldate[0].isEmpty() && type.equals("RV")) {
            int nextperiod = Integer.valueOf(caldate[1]) + 1;
            int thisyear = Integer.valueOf(caldate[0]);
              if (nextperiod > 12) {
                  thisyear++;
                  nextperiod = 1;
              }
              ArrayList<String> nextcal = fglData.getGLCalByYearAndPeriod(String.valueOf(thisyear), String.valueOf(nextperiod));
              nextstartdate = String.valueOf(nextcal.get(2));
        }
        
        for (int i = 0; i < transtable.getRowCount(); i++) {
            amt = bsParseDouble(transtable.getValueAt(i, 4).toString());
            gl_tran gv = new gl_tran(null,
                    "", // id DB assigned
                    tbref.getText(), // ref
                    setDateDB(effdate.getDate()), // effdate
                    setDateDB(parseDate(dateentered.getText())), // entdate
                    "0", // timestamp DB assigned
                    transtable.getValueAt(i, 1).toString(), // acct
                    transtable.getValueAt(i, 2).toString(), // cc
                    bsParseDouble(transtable.getValueAt(i, 4).toString().replace(defaultDecimalSeparator, '.')), //amt
                    bsParseDouble(transtable.getValueAt(i, 4).toString().replace(defaultDecimalSeparator, '.')), // baseamt
                    ddsite.getSelectedItem().toString(), //site 
                    tbref.getText(), // doc
                    transtable.getValueAt(i, 0).toString(), // line
                    type, // type
                    curr, // currency
                    basecurr, // base currency
                    transtable.getValueAt(i, 3).toString(), // desc
                    tbuserid.getText() // userid
            );
        glv.add(gv);
        
        if (type.equals("RV")) {
            gl_tran gr = new gl_tran(null,
                    "", // id DB assigned
                    tbref.getText(), // ref
                    nextstartdate, // effdate
                    dateentered.getText(), // entdate
                    "0", // timestamp DB assigned
                    transtable.getValueAt(i, 1).toString(), // acct
                    transtable.getValueAt(i, 2).toString(), // cc
                    -1 * bsParseDouble(transtable.getValueAt(i, 4).toString().replace(defaultDecimalSeparator, '.')), //amt
                    -1 * bsParseDouble(transtable.getValueAt(i, 4).toString().replace(defaultDecimalSeparator, '.')), // baseamt
                    ddsite.getSelectedItem().toString(), //site 
                    tbref.getText(), // doc
                    transtable.getValueAt(i, 0).toString(), // line
                    type, // type
                    curr, // currency
                    basecurr, // base currency
                    transtable.getValueAt(i, 3).toString(), // desc
                    tbuserid.getText() // userid
            );
        glv.add(gr);
        }
        
        }
        return glv;
    }
    
    public static void lookUpFrameAcctDesc(String box) {
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
         lookUpModel = DTData.getAcctBrowseUtil(input.getText(), 0, "ac_id");
        } else {
         lookUpModel = DTData.getAcctBrowseUtil(input.getText(), 0, "ac_desc");   
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
        
       
        lookUpTable.setPreferredScrollableViewportSize(new Dimension(500,100));
        JScrollPane scrollPane = new JScrollPane(lookUpTable);
        mllu = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                if (box.equals("1")) {    
                  ddacct.setSelectedItem(target.getValueAt(row,1).toString());
                } else {
                  ddacct2.setSelectedItem(target.getValueAt(row,1).toString());   
                }
                dialog.dispose();
                }
            }
        };
        lookUpTable.addMouseListener(mllu);
      
        
        JPanel rbpanel = new JPanel();
        bg = new ButtonGroup();
        rb1 = new JRadioButton(getGlobalLabelTag("lblaccount"));
        rb2 = new JRadioButton(getGlobalLabelTag("lbldesc"));
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
        dialog.setTitle(getMessageTag(1049));
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
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getGLTranBrowseUtil2(luinput.getText(),0, "glt_doc");
         } else if (lurb2.isSelected()) {
         luModel = DTData.getGLTranBrowseUtil2(luinput.getText(),0, "glt_acct");  
        } else {
         luModel = DTData.getGLTranBrowseUtil2(luinput.getText(),0, "glt_effdate");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        luTable.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));
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
                initvars(new String[]{target.getValueAt(row,2).toString(), "gl_tran"});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()),
                getClassLabelTag("lblacct", this.getClass().getSimpleName()),
                getClassLabelTag("lbleffdate", this.getClass().getSimpleName())); 
        
        
        
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
        btsubmit = new javax.swing.JButton();
        btdeleteALL = new javax.swing.JButton();
        tbref = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        tbuserid = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        dateentered = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        effdate = new com.toedter.calendar.JDateChooser();
        btnew = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        transtable = new javax.swing.JTable();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        lbacct1 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        tbamt = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        labeltotal = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        ddacct = new javax.swing.JComboBox();
        ddcc = new javax.swing.JComboBox();
        ddcurr = new javax.swing.JComboBox();
        tbcontrolamt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lbacctname = new javax.swing.JLabel();
        lbccname = new javax.swing.JLabel();
        btLookUpAccount = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        ddacct2 = new javax.swing.JComboBox<>();
        btLookUpAccount2 = new javax.swing.JButton();
        lbacctname2 = new javax.swing.JLabel();
        lbacct2 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        btsubmit.setText("Commit");
        btsubmit.setName("btcommit"); // NOI18N
        btsubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsubmitActionPerformed(evt);
            }
        });

        btdeleteALL.setText("Delete");
        btdeleteALL.setName("btdelete"); // NOI18N
        btdeleteALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteALLActionPerformed(evt);
            }
        });

        jLabel46.setText("Reference");
        jLabel46.setName("lblid"); // NOI18N

        jLabel47.setText("Userid");
        jLabel47.setName("lbluserid"); // NOI18N

        jLabel50.setText("EffectiveDate");
        jLabel50.setName("lbleffectivedate"); // NOI18N

        jLabel2.setText("Entered Date");
        jLabel2.setName("lblenterdate"); // NOI18N

        effdate.setDateFormatString("yyyy-MM-dd");

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        transtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Line", "Acct", "CC", "Description", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(transtable);

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel48.setText("cc");
        jLabel48.setName("lblcc"); // NOI18N

        lbacct1.setText("Acct");
        lbacct1.setName("lblacct"); // NOI18N

        tbdesc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdescFocusLost(evt);
            }
        });

        jLabel51.setText("Amt");
        jLabel51.setName("lblamt"); // NOI18N

        tbamt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbamtFocusLost(evt);
            }
        });

        jLabel52.setText("Desc");
        jLabel52.setName("lbldesc"); // NOI18N

        labeltotal.setBackground(new java.awt.Color(217, 235, 191));

        jLabel1.setText("Site");
        jLabel1.setName("lblsite"); // NOI18N

        jLabel3.setText("Currency");
        jLabel3.setName("lblcurrency"); // NOI18N

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "simple", "custom", "reversing" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        jLabel4.setText("Entries Summed:");
        jLabel4.setName("lbltotalamt"); // NOI18N

        ddacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddacctActionPerformed(evt);
            }
        });

        ddcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddccActionPerformed(evt);
            }
        });

        tbcontrolamt.setName(""); // NOI18N
        tbcontrolamt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcontrolamtFocusLost(evt);
            }
        });

        jLabel5.setText("Control Amt");
        jLabel5.setName("lblcontrolamt"); // NOI18N

        lbacctname.setName("lblacctname"); // NOI18N

        lbccname.setName("lblccname"); // NOI18N

        btLookUpAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpAccountActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        ddacct2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddacct2ActionPerformed(evt);
            }
        });

        btLookUpAccount2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpAccount2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpAccount2ActionPerformed(evt);
            }
        });

        lbacct2.setText("Acct");
        lbacct2.setName("lblacct"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbacct1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbacct2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ddcurr, javax.swing.GroupLayout.Alignment.LEADING, 0, 102, Short.MAX_VALUE)
                                .addComponent(ddsite, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateentered, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(effdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ddacct2, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddacct, javax.swing.GroupLayout.Alignment.LEADING, 0, 138, Short.MAX_VALUE))
                            .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbccname, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btLookUpAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btLookUpAccount2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lbacctname, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                    .addComponent(lbacctname2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(jLabel4)
                                .addGap(7, 7, 7)
                                .addComponent(labeltotal, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(197, 197, 197)
                                .addComponent(btsubmit)
                                .addGap(12, 12, 12)
                                .addComponent(btdeleteALL))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btadd)
                                .addGap(12, 12, 12)
                                .addComponent(btdelete))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(202, 202, 202)
                                        .addComponent(btnew))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(91, 91, 91)
                                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(98, 98, 98)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnew)
                                    .addComponent(btclear)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btlookup)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel46)))))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel47))
                            .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(dateentered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel50))
                            .addComponent(effdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btLookUpAccount)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbacct1))
                    .addComponent(lbacctname, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbacctname2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddacct2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lbacct2))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btLookUpAccount2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel48))
                    .addComponent(lbccname, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btadd)
                        .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel51))
                    .addComponent(btdelete))
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(labeltotal, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btsubmit)
                    .addComponent(btdeleteALL)))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btsubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsubmitActionPerformed
        if (! validateInput(BlueSeerUtils.dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.add, new String[]{tbref.getText()});
    }//GEN-LAST:event_btsubmitActionPerformed

    private void btdeleteALLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteALLActionPerformed
            boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
            /*
            if (! tbref.getText().toString().startsWith("JL")) {
                proceed = false;
                bsmf.MainFrame.show(getMessageTag(1044));
            }
            */
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
              
                   int i = st.executeUpdate("delete from gl_tran where glt_ref = " + "'" + tbref.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show(getMessageTag(1045, String.valueOf(i)));
                    initvars(null);
                    } else {
                     bsmf.MainFrame.show(getMessageTag(1047));   
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteALLActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        
        String prodline = "";
        String status = "";
        String op = "";
       transtable.setModel(transmodel);
        
        if (tbdesc.getText().isEmpty()) {
             bsmf.MainFrame.show(getMessageTag(1024, "description"));
             tbdesc.setBackground(Color.yellow);
            tbdesc.requestFocus();
            return;
         }
        
        
        
        if (tbamt.getText().isEmpty()) {
             bsmf.MainFrame.show(getMessageTag(1024, "amount"));
            tbamt.setBackground(Color.yellow);
            tbamt.requestFocus();
            return;
         }
       
        if (! OVData.isAcctNumberValid(ddacct.getSelectedItem().toString()) ) {
            bsmf.MainFrame.show(getMessageTag(1026));
            return;
        }
        if (! OVData.isCostCenterValid(ddcc.getSelectedItem().toString()) ) {
            bsmf.MainFrame.show(getMessageTag(1048));
            return;
        }
        
        if (effdate.getDate() == null || ! BlueSeerUtils.isValidDateStr(BlueSeerUtils.mysqlDateFormat.format(effdate.getDate())) ) {
            bsmf.MainFrame.show(getMessageTag(1033, "effective date"));
            return;
        }
        
        
       if (ddtype.getSelectedItem().toString().equals("simple")) {
           // add double entry...ddacct is credit (-) ddacct2 is debit (+)
            transmodel.addRow(new Object[]{transmodel.getRowCount() + 1,
                ddacct.getSelectedItem().toString(),
                ddcc.getSelectedItem().toString(),
                tbdesc.getText(),
                "-" + tbamt.getText()
            });
            transmodel.addRow(new Object[]{transmodel.getRowCount() + 1,
                ddacct2.getSelectedItem().toString(),
                ddcc.getSelectedItem().toString(),
                tbdesc.getText(),
                tbamt.getText()
            });
        } else {
           // allow custom entry...sign is determined by entry in amount field
           transmodel.addRow(new Object[]{transmodel.getRowCount() + 1,
                ddacct.getSelectedItem().toString(),
                ddcc.getSelectedItem().toString(),
                tbdesc.getText(),
                tbamt.getText()
            });
       }   
            
            
            tallyamount();
            clearinput();
       
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       int[] rows = transtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) transtable.getModel()).removeRow(i);
        }
        
        // clean up line count....column 1
          for (int i = 0; i < transtable.getRowCount(); i++) {
             transtable.setValueAt(i + 1, i, 0);
          }
          
          tallyamount();
        
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        clearAll();
        enableAll();
        /*
        
       if (! ddtype.getSelectedItem().toString().equals("reversing")) {
       type = "JL";
       tbref.setText(fglData.setGLRecNbr("JL"));
       } else {
       type = "RV";
       tbref.setText(fglData.setGLRecNbr("RV"));    
       }
       */
       ddtype.setSelectedIndex(0);
       tbref.setEnabled(false);
       tbuserid.setEnabled(false);
       dateentered.setEnabled(false);
       btdeleteALL.setEnabled(false);
       
    }//GEN-LAST:event_btnewActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
        if (! isLoad && ddacct.getSelectedItem() != null ) {
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
                    lbacctname.setText(res.getString("ac_desc"));
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
    }//GEN-LAST:event_ddacctActionPerformed

    private void ddccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddccActionPerformed
       if (ddcc.getSelectedItem() != null )
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

                res = st.executeQuery("select dept_desc from dept_mstr where dept_id = " + "'" + ddcc.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbccname.setText(res.getString("dept_desc"));
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
    }//GEN-LAST:event_ddccActionPerformed

    private void tbamtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbamtFocusLost
        String x = BlueSeerUtils.bsformat("", tbamt.getText(), "2");
        if (x.equals("error")) {
            tbamt.setText("");
            tbamt.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbamt.requestFocus();
        } else {
            tbamt.setText(x);
            tbamt.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbamtFocusLost

    private void tbcontrolamtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcontrolamtFocusLost
        String x = BlueSeerUtils.bsformat("", tbcontrolamt.getText(), "2");
        if (x.equals("error")) {
            tbcontrolamt.setText("");
            tbcontrolamt.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbcontrolamt.requestFocus();
        } else {
            tbcontrolamt.setText(x);
            tbcontrolamt.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbcontrolamtFocusLost

    private void btLookUpAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAccountActionPerformed
        lookUpFrameAcctDesc("1");
    }//GEN-LAST:event_btLookUpAccountActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btLookUpAccount2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAccount2ActionPerformed
        lookUpFrameAcctDesc("2");
    }//GEN-LAST:event_btLookUpAccount2ActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
       if (! isLoad) {
        if (ddtype.getSelectedItem().toString().equals("simple")) {
            type = "JL";
            tbref.setText(fglData.setGLRecNbr("JL"));
            lbacct1.setText("Credit Account (-)");
            lbacct2.setText("Debit Account (+)");
            tbcontrolamt.setEnabled(false);
            ddacct2.setEnabled(true);
            btLookUpAccount2.setEnabled(true);
        } else if (ddtype.getSelectedItem().toString().equals("custom")) {
            type = "JL";
            tbref.setText(fglData.setGLRecNbr("JL"));
            lbacct1.setText("General Account");
            lbacct2.setText("N/A");
            tbcontrolamt.setEnabled(true);
            ddacct2.setEnabled(false);
            btLookUpAccount2.setEnabled(false);
        } else { // reversing
            type = "RV";
            tbref.setText(fglData.setGLRecNbr("RV"));    
            lbacct1.setText("General Account");
            lbacct2.setText("N/A");
            tbcontrolamt.setEnabled(true);
            ddacct2.setEnabled(false);
            btLookUpAccount2.setEnabled(false);
        }
       }
    }//GEN-LAST:event_ddtypeActionPerformed

    private void ddacct2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacct2ActionPerformed
         if (! isLoad && ddacct2.getSelectedItem() != null ) {
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
                res = st.executeQuery("select ac_desc from ac_mstr where ac_id = " + "'" + ddacct2.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbacctname2.setText(res.getString("ac_desc"));
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
    }//GEN-LAST:event_ddacct2ActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        clearAll();
       disableAll();
       
       btnew.setEnabled(true);
       btlookup.setEnabled(true);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbdescFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdescFocusLost
       if (! tbdesc.getText().isBlank()) {
           tbdesc.setBackground(Color.white);
       }
    }//GEN-LAST:event_tbdescFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpAccount;
    private javax.swing.JButton btLookUpAccount2;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteALL;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btsubmit;
    private javax.swing.JTextField dateentered;
    private static javax.swing.JComboBox ddacct;
    private static javax.swing.JComboBox<String> ddacct2;
    private javax.swing.JComboBox ddcc;
    private javax.swing.JComboBox ddcurr;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtype;
    private com.toedter.calendar.JDateChooser effdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labeltotal;
    private javax.swing.JLabel lbacct1;
    private javax.swing.JLabel lbacct2;
    private javax.swing.JLabel lbacctname;
    private javax.swing.JLabel lbacctname2;
    private javax.swing.JLabel lbccname;
    private javax.swing.JTextField tbamt;
    private javax.swing.JTextField tbcontrolamt;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbuserid;
    private javax.swing.JTable transtable;
    // End of variables declaration//GEN-END:variables
}
