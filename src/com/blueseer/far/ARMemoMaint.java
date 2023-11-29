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
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.tags;
import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData.getCustInfo;
import com.blueseer.far.farData.ARSet;
import static com.blueseer.far.farData.addArTransaction;
import com.blueseer.far.farData.ar_mstr;
import com.blueseer.far.farData.ard_mstr;
import static com.blueseer.far.farData.getARMstrSet;
import com.blueseer.fgl.fglData;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.BlueSeerUtils.dbaction;
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
import com.blueseer.utl.IBlueSeerT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;

/**
 *
 * @author vaughnte
 */
public class ARMemoMaint extends javax.swing.JPanel implements IBlueSeerT {

     // global variable declarations
    public static ar_mstr ar = null;
    public static ArrayList<ard_mstr> ardlist = null;
    
                boolean isLoad = false;
                String terms = "";
                String aracct = "";
                String arcc = "";
                String arbank = "";
                double actamt = 0.00;
                double control = 0.00;
                double baseamt = 0.00;
                double rcvamt = 0.00;
                String curr = "";
                String basecurr = "";
                String type = "";
    
    /**
     * Creates new form UserMaintPanel
     */
    
    
    DefaultTableCellRenderer tableRender = new DefaultTableCellRenderer();
    javax.swing.table.DefaultTableModel armodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("account"), 
                getGlobalColumnTag("cc"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("amount")
            })    {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
    
                
    javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 1 )) {
                            sumdollars();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };            
  
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
    
    public ARMemoMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void executeTask(dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(dbaction type, String[] key) { 
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
           } else if (this.type.equals("get")) {
             updateForm();
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
        actamt = 0;
       java.util.Date now = new java.util.Date();
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       dcdate.setDate(now);
       tbactualamt.setText("0");
       tbkey.setText("");
       tbkey.setEnabled(false);
       tbrmks.setText("");
       tbref.setText("");
       tbhdracct.setText("");
       tbhdrcc.setText("");
       armodel.setNumRows(0);
       tbamt.setText("");
       tbcc.setText("");
       tbdesc.setText("");
       tbacct.setText("");
       btnew.setEnabled(true);
       
       basecurr = OVData.getDefaultCurrency();
       ardet.setModel(armodel);
      
       type = "";
       
        ddsite.removeAllItems();
        ArrayList mylist = OVData.getSiteList();
        for (int i = 0; i < mylist.size(); i++) {
            ddsite.addItem(mylist.get(i));
        }
        
        ddterms.removeAllItems();
        ArrayList custterms = cusData.gettermsmstrlist();
        for (int i = 0; i < custterms.size(); i++) {
            ddterms.addItem(custterms.get(i));
        }
       
       
       ddcust.setEnabled(true);
       ddcust.removeAllItems();
        ArrayList mycusts = cusData.getcustmstrlist();
        for (int i = 0; i < mycusts.size(); i++) {
            ddcust.addItem(mycusts.get(i));
        }
        
       ddcurr.removeAllItems();
        ArrayList<String> curr = fglData.getCurrlist();
        for (int i = 0; i < curr.size(); i++) {
            ddcurr.addItem(curr.get(i));
        }
        ddcurr.setSelectedItem(OVData.getDefaultCurrency());
        
        
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
        } else { 
                   tbkey.setForeground(Color.red); 
        }
    }
    
    public boolean validateInput(dbaction x) {
        boolean b = true;
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    ddsite.requestFocus();
                    return b;
                }
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
                    return b;
                }
                
                if (tbhdracct.getText().isEmpty() || ! OVData.isValidGLAcct(tbhdracct.getText())) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    tbhdracct.requestFocus();
                    return b;
                }
                
                if (tbhdrcc.getText().isEmpty() || ! OVData.isValidGLcc(tbhdrcc.getText())) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    tbhdrcc.requestFocus();
                    return b;
                }
                
                if (arbank.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1128));
                    return b;
                }
                if (arcc.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1129));
                    return b;
                }
                if (aracct.isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1130));
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
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
        String[] m = addArTransaction(createDetRecord(), createRecord());
        boolean error = OVData.ARUpdate(tbkey.getText());
        if (! error) {
            error = fglData.glEntryFromARMemo(tbkey.getText(), dcdate.getDate());
        }
        return m;
    }
     
    public String[] updateRecord(String[] x) {
     String[] m = new String[]{BlueSeerUtils.ErrorBit, "This update functionality is not implemented at this time"};
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[]{BlueSeerUtils.ErrorBit, "This delete functionality is not implemented at this time"};
     return m;
     }
      
    public String[] getRecord(String[] key) {
      ARSet z = getARMstrSet(key);
      ar = z.ar();
      ardlist = z.ard();
      return z.m();
    }
    
    public farData.ar_mstr createRecord() { 
        java.util.Date now = new java.util.Date(); 
        
                if (basecurr.toUpperCase().equals(ddcurr.getSelectedItem().toString().toUpperCase())) {
                  baseamt = actamt;  
                } else {
                  baseamt = OVData.getExchangeBaseValue(basecurr, ddcurr.getSelectedItem().toString(), actamt);
                } 
        farData.ar_mstr x = new farData.ar_mstr(null, 
                null, // ar_id auto-generated
                tbkey.getText(),
                ddcust.getSelectedItem().toString(),
                currformatDouble(actamt).replace(defaultDecimalSeparator, '.'),
                currformatDouble(baseamt).replace(defaultDecimalSeparator, '.'),
                ddtype.getSelectedItem().toString().substring(0,1),
                ddcurr.getSelectedItem().toString(),
                basecurr,
                tbref.getText(),
                tbrmks.getText(),
                BlueSeerUtils.setDateFormat(now),
                BlueSeerUtils.setDateFormat(dcdate.getDate()),
                BlueSeerUtils.setDateFormat(now),
                tbhdracct.getText(),
                tbhdrcc.getText(),
                "c",
                arbank,
                ddsite.getSelectedItem().toString(),
                "", //ar_amt_tax
                "", //ar_base_amt_tax
                "", //ar_amt_disc
                "", //ar_base_amt_disc
                "", //ar_open_amt
                "", //ar_applied
                "", //ar_terms
                "", //ar_tax_code
                BlueSeerUtils.setDateFormatNull(null), //ar_invdate
                BlueSeerUtils.setDateFormatNull(null), //ar_duedate
                BlueSeerUtils.setDateFormatNull(null), //ar_discdate
                "0" //ar_reverse
                );
        return x;
    }
   
    public ArrayList<farData.ard_mstr> createDetRecord() {
        ArrayList<farData.ard_mstr> list = new ArrayList<farData.ard_mstr>();
        
            
            double amt_d = 0;
            double taxamt_d = 0;
            double baseamt_d = 0;
            double basetaxamt_d = 0;
            boolean completeAllocation = true;
            for (int j = 0; j < ardet.getRowCount(); j++) {
                        amt_d = bsParseDouble(ardet.getValueAt(j, 4).toString());
                         if (basecurr.toUpperCase().equals(ddcurr.getSelectedItem().toString().toUpperCase())) {
                         baseamt_d = amt_d;
                         } else {
                         baseamt_d = OVData.getExchangeBaseValue(basecurr, ddcurr.getSelectedItem().toString(), amt_d);
                         }
                        farData.ard_mstr x = new farData.ard_mstr(null,  
                            tbkey.getText(), 
                            String.valueOf(j + 1),    
                            ddcust.getSelectedItem().toString(),
                            tbref.getText(),
                            BlueSeerUtils.setDateFormat(dcdate.getDate()),
                            currformatDouble(amt_d).replace(defaultDecimalSeparator, '.'),
                            currformatDouble(taxamt_d).replace(defaultDecimalSeparator, '.'),
                            currformatDouble(baseamt_d).replace(defaultDecimalSeparator, '.'),     
                            currformatDouble(basetaxamt_d).replace(defaultDecimalSeparator, '.'),
                            ddcurr.getSelectedItem().toString(),
                            basecurr,
                            ardet.getValueAt(j, 1).toString(),
                            ardet.getValueAt(j, 2).toString()
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
         luModel = DTData.getARMemoBrowseUtil(luinput.getText(),0, "ar_nbr");
        } else {
         luModel = DTData.getARMemoBrowseUtil(luinput.getText(),0, "ar_cust");   
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
                getClassLabelTag("lblbillto", this.getClass().getSimpleName()));  
        
        
    }

    public void updateForm() {
        armodel.setNumRows(0);
        tbkey.setText(ar.ar_nbr());
        tbref.setText(ar.ar_ref());
        tbrmks.setText(ar.ar_rmks());
        ddsite.setSelectedItem(ar.ar_site());
        ddcust.setSelectedItem(ar.ar_cust());
        ddcurr.setSelectedItem(ar.ar_curr());
        
         for (ard_mstr ard : ardlist) {
        armodel.addRow(new Object[]{ard.ard_line(), 
                      ard.ard_acct(),
                      ard.ard_cc(), 
                      ard.ard_ref(),
                      ard.ard_amt()});
        }
        
        setAction(ar.m());
    }
    
    // custom
    public void getBilltoInfo(String cust) {
        
         // aracct, arcc, currency, bank, terms, carrier, onhold, site
            String[] custinfo = getCustInfo(cust);
            aracct = custinfo[0];
            arcc = custinfo[1];
            terms = custinfo[4];
            arbank = custinfo[3];
            tbhdracct.setText(custinfo[0]);
            tbhdrcc.setText(custinfo[1]);
            ddterms.setSelectedItem(custinfo[4]);
            
        
    }
    
   
    
     public void sumdollars() {
       double amt = 0;
        for (int i = 0; i < ardet.getRowCount(); i++) {
             amt += bsParseDouble(ardet.getValueAt(i, 4).toString());
          }
        actamt = amt;
        tbactualamt.setText(String.valueOf(amt));  
        
    }
    
    
    public void clearinput() {
        tbamt.setText("");
        tbcc.setText("");
        tbdesc.setText("");
        tbacct.setText("");
        tbamt.requestFocus();
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
        jScrollPane7 = new javax.swing.JScrollPane();
        taUMperms1 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ardet = new javax.swing.JTable();
        btdeleteitem = new javax.swing.JButton();
        jLabel49 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        tbcc = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        tbacct = new javax.swing.JTextField();
        btadd = new javax.swing.JButton();
        tbamt = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbhdrcc = new javax.swing.JTextField();
        tbhdracct = new javax.swing.JTextField();
        ddcust = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        ddsite = new javax.swing.JComboBox();
        ddterms = new javax.swing.JComboBox();
        btlookup = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        tbref = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("CR/DR Memo Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        taUMperms1.setColumns(20);
        taUMperms1.setRows(5);
        jScrollPane7.setViewportView(taUMperms1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        jPanel2.setName("paneldetail"); // NOI18N

        ardet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(ardet);

        btdeleteitem.setText("DeleteItem");
        btdeleteitem.setName("btdeleteitem"); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        jLabel49.setText("Acct");
        jLabel49.setName("lblacct"); // NOI18N

        btadditem.setText("AddItem");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        jLabel52.setText("Desc");
        jLabel52.setName("lbldesc"); // NOI18N

        jLabel51.setText("Amt");
        jLabel51.setName("lblamt"); // NOI18N

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        tbamt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbamtFocusLost(evt);
            }
        });

        jLabel48.setText("cc");
        jLabel48.setName("lblcc"); // NOI18N

        jLabel8.setText("Total:");
        jLabel8.setName("lbltotal"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel48)
                            .addComponent(tbcc, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(0, 76, Short.MAX_VALUE)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(btadditem)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btdeleteitem))
                                    .addComponent(btadd, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel51)))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel48)
                    .addComponent(jLabel52)
                    .addComponent(jLabel51))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadditem)
                    .addComponent(btdeleteitem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btadd)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 21, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Header"));
        jPanel3.setName("panelheader"); // NOI18N

        jLabel53.setText("Billto");
        jLabel53.setName("lblbillto"); // NOI18N

        jLabel50.setText("EffectiveDate");
        jLabel50.setName("lbleffdate"); // NOI18N

        jLabel5.setText("Acct");
        jLabel5.setName("lblacct"); // NOI18N

        jLabel3.setText("Terms");
        jLabel3.setName("lblterms"); // NOI18N

        jLabel46.setText("Key");
        jLabel46.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        jLabel6.setText("cc");
        jLabel6.setName("lblcc"); // NOI18N

        jLabel1.setText("Site");
        jLabel1.setName("lblsite"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel7.setText("Remarks");
        jLabel7.setName("lblremarks"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        jLabel2.setText("Reference");

        jLabel4.setText("Currency");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CM", "DM" }));

        jLabel9.setText("Type");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(46, 46, 46)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel3)
                                .addComponent(jLabel6)))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(39, 39, 39)
                            .addComponent(jLabel7))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel46)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddterms, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear))
                            .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbhdracct, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbhdrcc, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ddtype, javax.swing.GroupLayout.Alignment.LEADING, 0, 80, Short.MAX_VALUE)
                    .addComponent(ddcurr, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(184, 184, 184))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnew)
                            .addComponent(btclear)))
                    .addComponent(btlookup))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel50))))
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ddterms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbhdracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbhdrcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(0, 2, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        String prodline = "";
        String status = "";
        String op = "";
        
         double amt = 0;
      
        if (! BlueSeerUtils.isMoneyFormat(tbamt.getText())) {
             bsmf.MainFrame.show(getMessageTag(1023,"2"));
            canproceed = false;
            tbacct.requestFocus();
        }
        
        if (! BlueSeerUtils.isParsableToDouble(tbamt.getText()) ) {
            bsmf.MainFrame.show(getMessageTag(1028));
            canproceed = false;
            tbacct.requestFocus();
        }

        if (tbdesc.getText().isEmpty()) {
             bsmf.MainFrame.show(getMessageTag(1024));
            tbdesc.requestFocus();
            canproceed = false;
        }
        if (! OVData.isAcctNumberValid(tbacct.getText()) ) {
            bsmf.MainFrame.show(getMessageTag(1026));
            canproceed = false;
        }
        if (! OVData.isCostCenterValid(tbcc.getText()) ) {
            bsmf.MainFrame.show(getMessageTag(1026));
            canproceed = false;
        }
        
        if (dcdate.getDate() == null || ! BlueSeerUtils.isValidDateStr(BlueSeerUtils.mysqlDateFormat.format(dcdate.getDate())) ) {
            bsmf.MainFrame.show(getMessageTag(1123));
            canproceed = false;
        }
        
        
        if (canproceed) {
            amt = bsParseDouble(tbamt.getText());
            armodel.addRow(new Object[]{armodel.getRowCount() + 1,
                tbacct.getText(),
                tbcc.getText(),
                tbdesc.getText(),
                currformatDouble(amt)
            });
            sumdollars();
            clearinput();
        }
    }//GEN-LAST:event_btadditemActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
       int[] rows = ardet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) ardet.getModel()).removeRow(i);
        }
        
        // clean up line count....column 1
          for (int i = 0; i < ardet.getRowCount(); i++) {
             ardet.setValueAt(i + 1, i, 0);
          }
          
          sumdollars();
        
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
       newAction("ar");
    }//GEN-LAST:event_btnewActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed
       if (ddcust.getSelectedItem() != null)
        getBilltoInfo(ddcust.getSelectedItem().toString());
    }//GEN-LAST:event_ddcustActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
       executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void tbamtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbamtFocusLost
        if (! tbamt.getText().isEmpty()) {
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
        }
    }//GEN-LAST:event_tbamtFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable ardet;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddterms;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea taUMperms1;
    private javax.swing.JTextField tbacct;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbamt;
    private javax.swing.JTextField tbcc;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbhdracct;
    private javax.swing.JTextField tbhdrcc;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrmks;
    // End of variables declaration//GEN-END:variables
}
