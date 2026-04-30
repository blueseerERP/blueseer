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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.dfdate;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import com.blueseer.ctr.cusData;
import com.blueseer.ctr.cusData.cm_mstr;
import static com.blueseer.ctr.cusData.getCustMstr;
import static com.blueseer.fap.fapData.addExpMstr;
import static com.blueseer.fap.fapData.cashBuy;
import static com.blueseer.fap.fapData.cashExpense;
import static com.blueseer.fap.fapData.cashExpenseRecurring;
import static com.blueseer.fap.fapData.cashIncome;
import static com.blueseer.fap.fapData.cashSell;
import com.blueseer.fap.fapData.exp_mstr;
import static com.blueseer.fap.fapData.getExpMstr;
import static com.blueseer.fap.fapData.getRecurringExpenseHistory;
import static com.blueseer.fap.fapData.getRecurringExpenseRecords;
import static com.blueseer.fap.fapData.getRecurringIncomeTotal;
import static com.blueseer.fap.fapData.updateExpActive;
import static com.blueseer.fap.fapData.updateRecurExp_Income;
import com.blueseer.fgl.fglData.AcctMstr;
import static com.blueseer.fgl.fglData.addAcctMstr;
import static com.blueseer.fgl.fglData.getAcctMstr;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.getItemMstr;
import com.blueseer.inv.invData.item_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
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
import static com.blueseer.utl.BlueSeerUtils.lurb2;
import com.blueseer.utl.DTData;
import com.blueseer.utl.OVData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import com.blueseer.vdr.venData;
import static com.blueseer.vdr.venData.getVendMstr;
import com.blueseer.vdr.venData.vd_mstr;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.io.FilenameUtils;


/**
 *
 * @author vaughnte
 */
public class CashTran extends javax.swing.JPanel {

        boolean canUpdate = false;
        boolean isAutoPost = false;
        ArrayList<String[]> initDataSets = null;
        String defaultSite = "";
        String defaultCurrency = "";
        String defaultCC = "";
               
        /*
                String terms = "";
                String apacct = "";
                String apcc = "";
                String apbank = "";
                String curr = "";
                double actamt = 0;
                double actqty = 0;
               */
                int voucherline = 0;
                int incomeline = 0;
                boolean isLoad = false;
                String partnumber = "";
                String newFileName = "";
                
                public static javax.swing.table.DefaultTableModel lookUpModel = null;
                public static JTable lookUpTable = new JTable();
                public static MouseListener mllu = null;
                public static JDialog dialog = new JDialog();
                
                public static ButtonGroup bg = null;
                public static JRadioButton rb1 = null;
                public static JRadioButton rb2 = null;
                
                javax.swing.table.DefaultTableModel sellmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
               "Line", "Item", "Qty", "Price", "Desc", "Ref"
            });
                
                 javax.swing.table.DefaultTableModel buymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Line", "Item", "Qty", "Price", "Desc", "Ref"
            });
                 javax.swing.table.DefaultTableModel expensemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Line", "Item", "Qty", "Price", "Ref", "Acct"
            });
                        javax.swing.table.DefaultTableModel incomemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Line", "Item", "Qty", "Price", "Ref", "Acct"
            });
                 CashTran.MyTableModel rexpensemodel = new CashTran.MyTableModel(new Object[][]{},
            new String[]{
                "History", "ID", "Site", "Entity", "Name", "Desc", "Acct", "Amt", "ThisMonth?", "ExactAmt", "Pay?", "dummyyesno"
            })
                         {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 8)       
                            return ImageIcon.class;  
                        else if (col == 10 || col == 11)
                            return Boolean.class;
                        else if (col == 7 || col == 9)
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
                  javax.swing.table.DefaultTableModel rexpenseHistoryModel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "ID", "TranNbr", "Vendor", "Name", "EffDate", "Acct", "Amount"
            });     
    
     
         class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
       boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false, false, true, true
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            
            if ((boolean)recurexpensetable.getModel().getValueAt(rowIndex, 11) == true) {
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false};
            } else {
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, true, true}; 
            }
            
            return canEdit[columnIndex];
        }
    
        
        public Class getColumnClass(int column) {
            
            
         //      if (column == 7 || column == 9)       
        //        return Double.class; 
        //       else if (column == 9) 
       //            return Boolean.class;
       //     else return String.class;  //other columns accept String values 
            return String.class;
       /*     
      if (column >= 0 && column < getColumnCount()) {
          
          
           if (getRowCount() > 0) {
             // you need to check 
             Object value = getValueAt(0, column);
             // a line for robustness (in real code you probably would loop all rows until
             // finding a not-null value 
             if (value != null) {
                return value.getClass();
             }

        }
          
          
          
      }  
              
        return Object.class;
*/
               }
       
        
        
   }    
    
     
     
                  
        public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

          CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            
          }

          public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              //super.setBackground(table.getSelectionBackground());
              setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            setSelected((value != null && ((Boolean) value).booleanValue()));
            return this;
          }
} 
      
                  
       class Task extends SwingWorker<String[], Void> {
        /*
         * Main task. Executed in background thread.
         */
          String type = "";
          
          public Task(String type) {
              this.type = type;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            switch(this.type) {
                case "buy":
                    message = addBuyNew();
                    break;
                case "sell":
                    message = addSellNew();
                    break;
                case "expense":
                    message = addExpenseNew();
                    break;
                case "income":
                    message = addIncomeNew();
                    break;    
                case "recurexpense":
                    message = addRecurExpenseNew();
                    break;
                default:
                    MainFrame.bslog("unkown switch selection " + Thread.currentThread().getStackTrace()[1].getMethodName());
            }
            return message;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
            try {
            String[] message = get();
            
           
            BlueSeerUtils.endTask(message);
             
            switch(this.type) {
                case "expense":
                    initvars(new String[]{"0"});
                    break;
                case "income":
                    initvars(new String[]{"1"});
                    break;    
                case "buy":
                    initvars(new String[]{"2"});
                    break;
                case "sell":
                    initvars(new String[]{"3"});
                    break;
                case "recurexpense":
                    initvars(new String[]{"4"});
                    break;
                default:
                    initvars(new String[]{"0"});
            }
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
                 
                 
                 
                
    /**
     * Creates new form ShipMaintPanel
     */
    public CashTran() {
        initComponents();
        setLanguageTags(this);
    }
   
    public void calcdiff() {
        double diff = 0;
        double totincome = 0;
        double totexpense = 0;
        
        
        if (! tbrexpincome.getText().isEmpty()) {
            totincome = bsParseDouble(tbrexpincome.getText());
        }
        if (! tbrexptotamt.getText().isEmpty()) {
            totexpense = bsParseDouble(tbrexptotamt.getText());
        }
        diff = totincome - totexpense;
        tbrexpdiff.setText(currformatDouble(diff));
        if (diff < 0) {
            tbrexpdiff.setBackground(Color.red);
        } else {
            tbrexpdiff.setBackground(Color.green);
        }
    }
    
    public void getRecurringExpense(boolean showall) {
         
        rexpensemodel.setRowCount(0);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
         double totexpense = 0;
         double totincome = 0;
         ImageIcon haspaid = null;
         double paidamt = 0;
        boolean paid = false; 
         
        totincome = getRecurringIncomeTotal(ddrexpsite.getSelectedItem().toString());
        ArrayList<String[]> list = getRecurringExpenseRecords(ddrexpsite.getSelectedItem().toString(), BlueSeerUtils.boolToString(showall));
        for (String[] s : list) {
            totexpense += bsParseDouble(s[6]);
            paidamt = bsParseDouble(s[7]);
            if (paidamt > 0) {
                haspaid = BlueSeerUtils.clickcheck;
                paid = true;
            } else {
                haspaid = BlueSeerUtils.clicknocheck;
                paid = false;
            }
            // "ID", "Site", "Entity", "Name", "Desc", "Acct", "Amt"
            rexpensemodel.addRow(new Object[]{BlueSeerUtils.clickflag, s[0], s[1],
              s[2], s[3], 
              s[4], s[5], bsParseDouble(s[6]), haspaid, paidamt, false, paid
          });
            
        }
        
        tbrexptotamt.setText(String.valueOf(totexpense)); 
        tbrexpincome.setText(String.valueOf(totincome));        

    }
    
    public void getRecurringExpenseRecord(String id) {
        isLoad = true;
        
        exp_mstr em = getExpMstr(new String[]{id});
        tbrexpensedesc.setText(em.exp_desc());
        tbrexprice.setText(bsNumber(em.exp_amt()));
        ddrexpacct.setSelectedItem(em.exp_acct());
        ddrexpentity.setSelectedItem(em.exp_entity());
        ddrexpsite.setSelectedItem(em.exp_site());
        cbrexpenabled.setSelected(BlueSeerUtils.ConvertStringToBool(em.exp_active()));
        tbrexpid.setText(em.exp_id());                  
             
        isLoad = false;
    }
    
    public void getHistory(String key) {
         
        rexpenseHistoryModel.setRowCount(0);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        ArrayList<String[]> list = getRecurringExpenseHistory(key);
        for (String[] s : list) {
            rexpenseHistoryModel.addRow(new Object[]{s[0], s[1], s[2], s[3], s[4], s[5], bsParseDouble(s[6])});
        }
       
    }
 
    public String[] addBuyNew() {
       ArrayList<String[]> details = new ArrayList<>();
       String[] headers = new String[]{ddentity.getSelectedItem().toString(),
           expensenbr.getText(), 
           dfdate.format(dcdate.getDate()), 
           tbref.getText(), 
           tbpo.getText(), 
           defaultSite, 
           defaultCurrency};       
       for (int j = 0; j < detailtable.getRowCount(); j++) {
           details.add(new String[]{detailtable.getValueAt(j, 1).toString(), detailtable.getValueAt(j, 2).toString(), detailtable.getValueAt(j, 3).toString()});
       }
       String[] m = cashBuy(details, headers);
       return m;
    }
   
    public String[] addSellNew() {
         // headers = cust, transnbr, po, site, currency, remarks
        // details = "Line", "Item", "Qty", "Price", "Desc", "Ref"
       ArrayList<String[]> details = new ArrayList<>();
       String[] headers = new String[]{tbcust.getText(),
           expensenbr1.getText(),            
           tbpo1.getText(),
           defaultSite, 
           defaultCurrency,
           tbrmks1.getText()};       
       for (int j = 0; j < detailtable1.getRowCount(); j++) {
           details.add(new String[]{detailtable1.getValueAt(j, 0).toString(), 
               detailtable1.getValueAt(j, 1).toString(), 
               detailtable1.getValueAt(j, 2).toString(), 
               detailtable1.getValueAt(j, 3).toString(),
               detailtable1.getValueAt(j, 4).toString(),
               detailtable1.getValueAt(j, 5).toString()});
       }
       String[] m = cashSell(details, headers);
       return m;
    }
   
    public String[] addExpenseNew() {
       ArrayList<String[]> details = new ArrayList<>();
       String[] headers = new String[]{ddentityExpense.getSelectedItem().toString(),
           tbKeyExpense.getText(), 
           dfdate.format(dcdateExpense.getDate()), 
           tbexpenseLOT.getText(), 
           tbexpensePO.getText(), 
           defaultSite, 
           defaultCurrency,
           tbexpenseRemarks.getText()};       
       for (int j = 0; j < detailtable.getRowCount(); j++) {
           details.add(new String[]{expenseTable.getValueAt(j, 0).toString(), 
               expenseTable.getValueAt(j, 1).toString(), 
               expenseTable.getValueAt(j, 2).toString(), 
               expenseTable.getValueAt(j, 3).toString(),
                expenseTable.getValueAt(j, 4).toString(),
                expenseTable.getValueAt(j, 5).toString()});
       }
       String[] m = cashExpense(details, headers);
       return m;
    }
   
    public String[] addIncomeNew() {
         // headers = cust, transnbr, po, site, currency, remarks
        // details = "Line", "Item", "Qty", "Price", "Desc", "Ref"
       ArrayList<String[]> details = new ArrayList<>();
       String[] headers = new String[]{ddaccountincome.getSelectedItem().toString(),
           tbKeyIncome.getText(),   
           dfdate.format(dcdateIncome.getDate()),
           tbincomeDesc.getText(),
           defaultSite, 
           defaultCurrency,
           tbincomeRef.getText()};       
       for (int j = 0; j < incomeTable.getRowCount(); j++) {
           details.add(new String[]{incomeTable.getValueAt(j, 0).toString(), 
               incomeTable.getValueAt(j, 1).toString(), 
               incomeTable.getValueAt(j, 2).toString(), 
               incomeTable.getValueAt(j, 3).toString(),
               incomeTable.getValueAt(j, 4).toString(),
               incomeTable.getValueAt(j, 5).toString()});
       }
       String[] m = cashIncome(details, headers);
       return m;
    }
    
    public String[] addRecurExpenseNew() {
          // headers = site
        // details = "History", "ID", "Site", "Entity", "Name", "Desc", "Acct", "Amt", "ThisMonth?", "ExactAmt", "Pay?", "dummyyesno"
       ArrayList<String[]> details = new ArrayList<>();
       String[] headers = new String[]{ddrexpsite.getSelectedItem().toString()};       
       for (int j = 0; j < incomeTable.getRowCount(); j++) {
           details.add(new String[]{incomeTable.getValueAt(j, 0).toString(), 
               incomeTable.getValueAt(j, 1).toString(), 
               incomeTable.getValueAt(j, 2).toString(), 
               incomeTable.getValueAt(j, 3).toString(),
               incomeTable.getValueAt(j, 4).toString(),
               incomeTable.getValueAt(j, 5).toString(),
           incomeTable.getValueAt(j, 6).toString(),
           incomeTable.getValueAt(j, 7).toString(),
           incomeTable.getValueAt(j, 8).toString(),
           incomeTable.getValueAt(j, 9).toString(),
           incomeTable.getValueAt(j, 10).toString(),
           incomeTable.getValueAt(j, 11).toString()});
       }
       String[] m = cashExpenseRecurring(details, headers);
       return m;
    }
        
    
    
    
    public void addExpenseAccount(String desc) {
      //  ddrexpacct.removeAllItems();
      //  ddaccountexpense.removeAllItems();
       boolean proceed = false;
       int i = 0;
        int acctnbr = OVData.getNextNbr("expenseaccount");
        if (acctnbr >= 99000000 && acctnbr <= 99999999) {
            proceed = true;
        } else {
            bsmf.MainFrame.show(getMessageTag(1027));
            return;
        }  
      
        AcctMstr am = new AcctMstr(null,
                String.valueOf(acctnbr),
                desc.replace("'", "").toUpperCase(),
                "E",
                defaultCurrency,
                "1"
        );
        
        String[] m = addAcctMstr(am);
        if (m[0].equals("0")) {
            ddaccountexpense.addItem(String.valueOf(acctnbr));
            ddaccountexpense.setSelectedItem(String.valueOf(acctnbr));
            ddrexpacct.addItem(String.valueOf(acctnbr));
            ddrexpacct.setSelectedItem(String.valueOf(acctnbr));
        } else {
           bsmf.MainFrame.show(getMessageTag(1014)); 
        }
     
    }
    
    public void addIncomeAccount(String desc) {
      //  ddrexpacct.removeAllItems();
      //  ddaccountexpense.removeAllItems();
      
      boolean proceed = false;
       int i = 0;
        int acctnbr = OVData.getNextNbr("incomeaccount");
        if (acctnbr >= 59000000 && acctnbr <= 59999999) {
            proceed = true;
        } else {
            bsmf.MainFrame.show("income account generated number is beyond limits of 59000000 to 59999999");
            return;
        }
      
        AcctMstr am = new AcctMstr(null,
                String.valueOf(acctnbr),
                desc.replace("'", "").toUpperCase(),
                "I",
                defaultCurrency,
                "1"
        );
        String[] m = addAcctMstr(am);
        if (m[0].equals("0")) {
            ddaccountincome.addItem(String.valueOf(acctnbr));
            ddaccountincome.setSelectedItem(String.valueOf(acctnbr));
        } else {
           bsmf.MainFrame.show(getMessageTag(1014)); 
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
           //bsmf.MainFrame.show(component.getClass().getTypeName() + "/" + component.getAccessibleContext().getAccessibleName() + "/" + component.getName());
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
        String v = "0";
        if (arg != null && arg.length > 0) {
        v = arg[0];
        } 
        isLoad = true;
        if (jTabbedPane1.getTabCount() == 0) {
        jTabbedPane1.add(getClassLabelTag("tab1",this.getClass().getSimpleName()), expensePanel);
        jTabbedPane1.add(getClassLabelTag("tab2",this.getClass().getSimpleName()), incomePanel);
        jTabbedPane1.add(getClassLabelTag("tab3",this.getClass().getSimpleName()), buyPanel);
        jTabbedPane1.add(getClassLabelTag("tab4",this.getClass().getSimpleName()), sellPanel);
        jTabbedPane1.add(getClassLabelTag("tab5",this.getClass().getSimpleName()), expenseRecurPanel);
        } else {
            jTabbedPane1.setSelectedIndex(Integer.parseInt(v));
        }
     
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "accounts,vendors");
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            
            if (s[0].equals("autopost")) {
              isAutoPost = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            
            if (s[0].equals("site")) {
              defaultSite = s[1];  
            }
            
            if (s[0].equals("sites")) {
              ddrexpsite.addItem(s[1]);
            }
           
            if (s[0].equals("cc")) {
              defaultCC = s[1];  
            }            
            
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            
            if (s[0].equals("vendors")) {
                ddentityExpense.addItem(s[1]);
                ddrexpentity.addItem(s[1]);
                ddentity.addItem(s[1]);
            }
                        
            if (s[0].equals("accounts")) {
                ddaccountexpense.addItem(s[1]);
                ddaccountincome.addItem(s[1]);
                ddrexpacct.addItem(s[1]);
            }
        }
        ddrexpsite.setSelectedItem(defaultSite);
       clearAll(); 
       disableAll();
        
       btnewbuy.setEnabled(true);
       
       isLoad = false;
       
    }
    
    public exp_mstr createExpMstrRecord() {
        String uniqueID = String.valueOf(OVData.getNextNbr("rexpense"));
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        exp_mstr em = new exp_mstr(null, 
                uniqueID,
                ddrexpsite.getSelectedItem().toString(),
                ddrexpentity.getSelectedItem().toString(),
                lbname3.getText(),
                ddrexpacct.getSelectedItem().toString(),
                "9999",
                dfdate.format(now),
                dfdate.format(now),
                bsmf.MainFrame.userid,
                tbrexpensedesc.getText().replace("'",""),
                "",
                bsParseDouble(tbrexprice.getText()),
                "1"
        );
        return em;
    }
    
    public void disableBuy() {
       
        tbactualamt.setEnabled(false);
        ddentity.setEnabled(false);
        dcdate.setEnabled(false);
        tbrmks.setEnabled(false);
        tbpo.setEnabled(false);
        tbitemservice.setEnabled(false);
        tbprice.setEnabled(false);
        tbref.setEnabled(false);
        tbqty.setEnabled(false);
        btadditem.setEnabled(false);
        btdeleteitem.setEnabled(false);
        btadd.setEnabled(false);        
        btaddentity.setEnabled(false);
        detailtable.setEnabled(false);
        expensenbr.setEnabled(false);
        
       
    }
        
    public void disableSell() {
        tbactualamt1.setEnabled(false);
        tbcust.setEnabled(false);
        dcdate1.setEnabled(false);
        tbrmks1.setEnabled(false);
        tbpo1.setEnabled(false);
        tbitemSell.setEnabled(false);
        tbprice1.setEnabled(false);
        tbref1.setEnabled(false);
        tbqty1.setEnabled(false);
        btadditem1.setEnabled(false);
        btdeleteitem1.setEnabled(false);
        btadd1.setEnabled(false);        
        btaddentity1.setEnabled(false);
        detailtable1.setEnabled(false);
        expensenbr1.setEnabled(false);
    }
    
    public void disableExpense() {
        tbexpensetotal.setEnabled(false);
        ddentityExpense.setEnabled(false);
        dcdateExpense.setEnabled(false);
        tbexpenseRemarks.setEnabled(false);
        tbexpensePO.setEnabled(false);
        ddaccountexpense.setEnabled(false);
        tbexpenseDesc.setEnabled(false);
        tbexpensePrice.setEnabled(false);
        tbexpenseLOT.setEnabled(false);
        tbexpenseQty.setEnabled(false);
        btaddItemExpense.setEnabled(false);
        btdeleteItemExpense.setEnabled(false);
        btaddexpense.setEnabled(false);        
        btexpenseAddEntity.setEnabled(false);
        expenseTable.setEnabled(false);
        tbKeyExpense.setEnabled(false);
        btexpenseAddAccount.setEnabled(false);
    }
    
    public void disableIncome() {
         tbincometotal.setEnabled(false);
        dcdateIncome.setEnabled(false);
        tbincomeRef.setEnabled(false);
        ddaccountincome.setEnabled(false);
        tbincomeDesc.setEnabled(false);
        tbincomeAmount.setEnabled(false);
        btaddItemIncome.setEnabled(false);
        btdeleteItemIncome.setEnabled(false);
        btaddincome.setEnabled(false);
        btincomeAddAccount.setEnabled(false);
        tbKeyIncome.setEnabled(false);
        
        incomeTable.setEnabled(false);
    }
       
    public void disableRecurExpense() {
        tbrexptotamt.setEnabled(false);
        ddrexpentity.setEnabled(false);
        ddrexpacct.setEnabled(false);
        tbrexpensedesc.setEnabled(false);
        tbrexprice.setEnabled(false);
        btrexpadditem.setEnabled(false);
        btpayselected.setEnabled(false);        
        btaddentity3.setEnabled(false);
        recurhisttable.setEnabled(false);
        btexpaddacct.setEnabled(false);
        cbrexpenabled.setEnabled(false);
        cbrexpshowall.setEnabled(false);
        tbrexpid.setEnabled(false);
    }
    
    public void disableAll() {
        disableBuy();
        disableSell();
        disableExpense();
      //  disableRecurExpense();
    }
    
    public void enableBuy() {
        tbactualamt.setEnabled(true);
        ddentity.setEnabled(true);
        dcdate.setEnabled(true);
        tbrmks.setEnabled(true);
        tbpo.setEnabled(true);
        tbitemservice.setEnabled(true);
        tbprice.setEnabled(true);
        tbref.setEnabled(true);
        tbqty.setEnabled(true);
        btadditem.setEnabled(true);
        btaddentity.setEnabled(true);
        btdeleteitem.setEnabled(true);
        btadd.setEnabled(true);
        detailtable.setEnabled(true);
    }
    
    public void enableSell() {
          tbactualamt1.setEnabled(true);
        tbcust.setEnabled(true);
      
        dcdate1.setEnabled(true);
        tbrmks1.setEnabled(true);
        tbpo1.setEnabled(true);
        tbitemSell.setEnabled(true);
        tbprice1.setEnabled(true);
        tbref1.setEnabled(true);
        tbqty1.setEnabled(true);
        btadditem1.setEnabled(true);
        btaddentity1.setEnabled(true);
        btdeleteitem1.setEnabled(true);
        btadd1.setEnabled(true);
        detailtable1.setEnabled(true);
    }
    
    public void enableExpense() {
          tbexpensetotal.setEnabled(true);
        ddentityExpense.setEnabled(true);
        dcdateExpense.setEnabled(true);
        tbexpenseRemarks.setEnabled(true);
        tbexpensePO.setEnabled(true);
        ddaccountexpense.setEnabled(true);
        tbexpenseDesc.setEnabled(true);
        tbexpensePrice.setEnabled(true);
        tbexpenseLOT.setEnabled(true);
        tbexpenseQty.setEnabled(true);
        btaddItemExpense.setEnabled(true);
        btexpenseAddEntity.setEnabled(true);
        btdeleteItemExpense.setEnabled(true);
        btaddexpense.setEnabled(true);
        btexpenseAddAccount.setEnabled(true);
        
        expenseTable.setEnabled(true);
    }
    
    public void enableIncome() {
          tbincometotal.setEnabled(true);
        dcdateIncome.setEnabled(true);
        tbincomeRef.setEnabled(true);
        ddaccountincome.setEnabled(true);
        tbincomeDesc.setEnabled(true);
        tbincomeAmount.setEnabled(true);
        btaddItemIncome.setEnabled(true);
        btdeleteItemIncome.setEnabled(true);
        btaddincome.setEnabled(true);
        btincomeAddAccount.setEnabled(true);
        
        incomeTable.setEnabled(true);
    }
    
    
    public void enableRecurExpense() {
          tbrexptotamt.setEnabled(true);
        ddrexpentity.setEnabled(true);
        ddrexpacct.setEnabled(true);
        tbrexpensedesc.setEnabled(true);
        tbrexprice.setEnabled(true);
        btrexpadditem.setEnabled(true);
        btaddentity3.setEnabled(true);
        btpayselected.setEnabled(true);
        btexpaddacct.setEnabled(true);
        cbrexpenabled.setEnabled(true);
        cbrexpshowall.setEnabled(true);
       // tbrexpid.setEnabled(true);  // disabled by design
        
        recurhisttable.setEnabled(true);
    }
    
    public void enableAll() {
       enableBuy();
       enableSell();
       enableExpense();
       enableRecurExpense();
    }
        
    public void clearBuy() {
         tbqty.setText("1");
         tbprice.setText("");
        
         expensenbr.setText("");
         tbpo.setText("");
        tbrmks.setText("");
        tbref.setText("");
      
        tbactualamt.setText("");
        tbactualamt.setEditable(false);
        lbtitle.setText("");
        lbname.setText("");
        buymodel.setRowCount(0);
        sellmodel.setRowCount(0);
        expensemodel.setRowCount(0);
        detailtable.setModel(buymodel);
        ddentity.removeAllItems();
        
    }
    
    public void clearExpense() {
         tbexpenseQty.setText("1");
         tbexpensePrice.setText("");
        
         tbKeyExpense.setText("");
         tbexpenseDesc.setText("");
         tbexpensePO.setText("");
        tbexpenseRemarks.setText("");
        tbexpenseLOT.setText("");
        lbacct2.setText("");
        tbexpensetotal.setText("");
        tbexpensetotal.setEditable(false);
        lbtitle2.setText("");
        lbexpenseEntityName.setText("");
        buymodel.setRowCount(0);
        sellmodel.setRowCount(0);
        expensemodel.setRowCount(0);
        expenseTable.setModel(expensemodel);
        ddentityExpense.removeAllItems();
          
    }
    
     public void clearIncome() {
         tbincomeAmount.setText("");
        
         tbKeyIncome.setText("");
         tbincomeDesc.setText("");
        tbincomeRef.setText("");
        lbincomeacct.setText("");
        tbincometotal.setText("");
        tbincometotal.setEditable(false);
        lbtitleIncome.setText("");
        incomemodel.setRowCount(0);
        incomeTable.setModel(incomemodel);
            
    }
    
    
    public void clearRecurExpense() {
         
         tbrexpid.setEnabled(false);
         tbrexprice.setText("");
         
         tbrexpensedesc.setText("");
         tbrexpincome.setText("");
         cbrexpenabled.setSelected(true);
         cbrexpshowall.setSelected(false);
         tbrexpid.setText("");
         
        
        lbrexpacctname.setText("");
        tbrexptotamt.setText("");
        tbrexptotamt.setEditable(false);
        lbname3.setText("");
        buymodel.setRowCount(0);
        sellmodel.setRowCount(0);
        rexpensemodel.setRowCount(0);
        recurhisttable.setModel(rexpenseHistoryModel);
        recurexpensetable.setModel(rexpensemodel);
        
        
        
        CashTran.CheckBoxRenderer checkBoxRenderer = new CashTran.CheckBoxRenderer();
        recurexpensetable.getColumnModel().getColumn(10).setCellRenderer(checkBoxRenderer); 
        
        //hide columns
        recurexpensetable.getColumn("dummyyesno").setMaxWidth(0);
        recurexpensetable.getColumn("dummyyesno").setPreferredWidth(0);
        recurexpensetable.getColumn("dummyyesno").setMinWidth(0);
        
        recurexpensetable.getColumn("Entity").setMaxWidth(0);
        recurexpensetable.getColumn("Entity").setPreferredWidth(0);
        recurexpensetable.getColumn("Entity").setMinWidth(0);
        
        recurexpensetable.getColumn("Site").setMaxWidth(0);
        recurexpensetable.getColumn("Site").setPreferredWidth(0);
        recurexpensetable.getColumn("Site").setMinWidth(0);
        
        recurexpensetable.getColumn("ID").setMaxWidth(0);
        recurexpensetable.getColumn("ID").setPreferredWidth(0);
        recurexpensetable.getColumn("ID").setMinWidth(0);
        
        recurexpensetable.getColumn("Name").setMaxWidth(0);
        recurexpensetable.getColumn("Name").setPreferredWidth(0);
        recurexpensetable.getColumn("Name").setMinWidth(0);
        
        isLoad = true;
        
            
        getRecurringExpense(cbrexpshowall.isSelected());   
        calcdiff();
        isLoad = false;
        
       
    }
    
    
    public void clearSell() {
         tbqty1.setText("1");
         tbprice1.setText("");
         
         expensenbr1.setText("");
         tbpo1.setText("");
        tbrmks1.setText("");
        tbref1.setText("");
       
        tbactualamt1.setText("");
        tbactualamt1.setEditable(false);
        lbtitle1.setText("");
        lbname1.setText("");
        buymodel.setRowCount(0);
        sellmodel.setRowCount(0);
        expensemodel.setRowCount(0);
        detailtable1.setModel(sellmodel);
        
    }
    
    
    public void clearAll() {
        clearBuy();
        clearSell();
        clearExpense();
       clearRecurExpense();
       clearIncome();
    }
  
    public void loadImage() {
         DateFormat dfdate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        File file = null;
        String ImageDir = OVData.getSystemImageDirectory();
        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            String suffix = FilenameUtils.getExtension(file.getName()); 
            newFileName = tbitemSell.getText() + "_" + dfdate.format(now) + "." + suffix;
            // insert image filename into database
          //  OVData.addItemImage(dditem.getSelectedItem().toString(), newFileName);
            
            // now lets copy the file over to the appropriate directory  
            file = new File(SourceDir);
            
       //     java.nio.file.Files.copy(file.toPath(), new File("images/" + newFileName).toPath(), 
                 java.nio.file.Files.copy(file.toPath(), new File(ImageDir + newFileName).toPath(), 
                 java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                 java.nio.file.StandardCopyOption.COPY_ATTRIBUTES,
                 java.nio.file.LinkOption.NOFOLLOW_LINKS);
                 
       // now update lblpic with new image
            ImageIcon imageIcon = new ImageIcon(ImageDir + newFileName);
          
            
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
    }   
     
    public void sumExpenseTotal() throws ParseException {
         double total = 0;
         for (int j = 0; j < expenseTable.getRowCount(); j++) {
              total += ( bsParseDouble(expenseTable.getValueAt(j, 2).toString()) * bsParseDouble(expenseTable.getValueAt(j, 3).toString()));
         } 
         tbexpensetotal.setText(currformatDouble(total));
    }
    
    public void sumIncomeTotal() {
         double total = 0;
         for (int j = 0; j < incomeTable.getRowCount(); j++) {
             total += ( bsParseDouble(incomeTable.getValueAt(j, 2).toString()) * bsParseDouble(incomeTable.getValueAt(j, 3).toString()));
         } 
         tbincometotal.setText(currformatDouble(total));
    }
    
      
   public static void lookUpFrameAcctDesc() {
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
        
       
        lookUpTable.setPreferredScrollableViewportSize(new Dimension(400,100));
        JScrollPane scrollPane = new JScrollPane(lookUpTable);
        mllu = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ddaccountexpense.setSelectedItem(target.getValueAt(row,1).toString());
                dialog.dispose();
                }
            }
        };
        lookUpTable.addMouseListener(mllu);
      
        
        JPanel rbpanel = new JPanel();
        bg = new ButtonGroup();
        rb1 = new JRadioButton("AcctNbr");
        rb2 = new JRadioButton("Description");
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

   public void lookUpFrameItemDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getItemDescBrowseBySite(luinput.getText(), "it_item", defaultSite);
        } else {
         luModel = DTData.getItemDescBrowseBySite(luinput.getText(), "it_desc", defaultSite);   
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
                tbitemSell.setText(target.getValueAt(row,1).toString());
                lbitemdesc.setText(target.getValueAt(row,2).toString());
                // getiteminfo(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), getGlobalColumnTag("description")); 
        
        
        
    }

   public void lookUpFrameBillTo() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_name");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_code");   
        } else {
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_zip");   
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
                tbcust.setText(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblname", this.getClass().getSimpleName()), 
                getClassLabelTag("lblcode", this.getClass().getSimpleName()),
                getClassLabelTag("lblzip", this.getClass().getSimpleName())); 
        
        
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
        fc = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        sellPanel = new javax.swing.JPanel();
        btadd1 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        detailtable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        dcdate1 = new com.toedter.calendar.JDateChooser();
        expensenbr1 = new javax.swing.JTextField();
        lblentity1 = new javax.swing.JLabel();
        lbname1 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        btnewsell = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        lbtitle1 = new javax.swing.JLabel();
        btaddentity1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lbitem1 = new javax.swing.JLabel();
        tbprice1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btdeleteitem1 = new javax.swing.JButton();
        btadditem1 = new javax.swing.JButton();
        tbref1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbqty1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        lbitemdesc = new javax.swing.JLabel();
        tbpo1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tbrmks1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btLookUpItemDesc = new javax.swing.JButton();
        tbitemSell = new javax.swing.JTextField();
        btLookUpBillTo = new javax.swing.JButton();
        tbcust = new javax.swing.JTextField();
        tbactualamt1 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        buyPanel = new javax.swing.JPanel();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        detailtable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        dcdate = new com.toedter.calendar.JDateChooser();
        expensenbr = new javax.swing.JTextField();
        lblentity = new javax.swing.JLabel();
        ddentity = new javax.swing.JComboBox();
        lbname = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        btnewbuy = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        lbtitle = new javax.swing.JLabel();
        btaddentity = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tbprice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbitemservice = new javax.swing.JTextField();
        btdeleteitem = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        tbref = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lbacct = new javax.swing.JLabel();
        tbpo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        expensePanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        dcdateExpense = new com.toedter.calendar.JDateChooser();
        tbKeyExpense = new javax.swing.JTextField();
        lblentity2 = new javax.swing.JLabel();
        ddentityExpense = new javax.swing.JComboBox();
        lbexpenseEntityName = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        btnewexpense = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        lbtitle2 = new javax.swing.JLabel();
        btexpenseAddEntity = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        lbitem2 = new javax.swing.JLabel();
        tbexpensePrice = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        tbexpenseDesc = new javax.swing.JTextField();
        ddaccountexpense = new javax.swing.JComboBox<>();
        btdeleteItemExpense = new javax.swing.JButton();
        btaddItemExpense = new javax.swing.JButton();
        tbexpenseLOT = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        tbexpenseQty = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        lbacct2 = new javax.swing.JLabel();
        btexpenseAddAccount = new javax.swing.JButton();
        tbexpenseRemarks = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        tbexpensePO = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        btLookUpExpAccount = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        expenseTable = new javax.swing.JTable();
        tbexpensetotal = new javax.swing.JTextField();
        btaddexpense = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        expenseRecurPanel = new javax.swing.JPanel();
        btpayselected = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        recurhisttable = new javax.swing.JTable();
        tbrexptotamt = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        recurexpensetable = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lbitem3 = new javax.swing.JLabel();
        tbrexprice = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tbrexpensedesc = new javax.swing.JTextField();
        ddrexpacct = new javax.swing.JComboBox<>();
        btrexpadditem = new javax.swing.JButton();
        btexpaddacct = new javax.swing.JButton();
        lbrexpacctname = new javax.swing.JLabel();
        cbrexpenabled = new javax.swing.JCheckBox();
        tbrexpid = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        ddrexpentity = new javax.swing.JComboBox();
        lblentity3 = new javax.swing.JLabel();
        btaddentity3 = new javax.swing.JButton();
        lbname3 = new javax.swing.JLabel();
        ddrexpsite = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        btrexpclear = new javax.swing.JButton();
        cbrexpshowall = new javax.swing.JCheckBox();
        lbrexpsitename = new javax.swing.JLabel();
        tbrexpdiff = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        tbrexpincome = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        btupdateincome = new javax.swing.JButton();
        incomePanel = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        dcdateIncome = new com.toedter.calendar.JDateChooser();
        tbKeyIncome = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        btnewincome = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        lbtitleIncome = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lbitem4 = new javax.swing.JLabel();
        tbincomeAmount = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        tbincomeDesc = new javax.swing.JTextField();
        ddaccountincome = new javax.swing.JComboBox<>();
        btdeleteItemIncome = new javax.swing.JButton();
        btaddItemIncome = new javax.swing.JButton();
        lbincomeacct = new javax.swing.JLabel();
        btincomeAddAccount = new javax.swing.JButton();
        tbincomeRef = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        incomeTable = new javax.swing.JTable();
        tbincometotal = new javax.swing.JTextField();
        btaddincome = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });
        add(jTabbedPane1);

        sellPanel.setName("panelmaintsell"); // NOI18N
        sellPanel.setPreferredSize(new java.awt.Dimension(785, 561));

        btadd1.setText("Commit");
        btadd1.setName("btcommit"); // NOI18N
        btadd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadd1ActionPerformed(evt);
            }
        });

        detailtable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(detailtable1);

        jPanel4.setName("panelsellsub"); // NOI18N

        dcdate1.setDateFormatString("yyyy-MM-dd");

        expensenbr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expensenbr1ActionPerformed(evt);
            }
        });

        lblentity1.setText("Entity");
        lblentity1.setName("sell_lblentity"); // NOI18N

        jLabel25.setText("TransNbr");
        jLabel25.setName("sell_lblnumber"); // NOI18N

        btnewsell.setText("New");
        btnewsell.setName("btnew"); // NOI18N
        btnewsell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewsellActionPerformed(evt);
            }
        });

        jLabel36.setText("Date");
        jLabel36.setName("sell_lbldate"); // NOI18N

        lbtitle1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N

        btaddentity1.setText("add new customer");
        btaddentity1.setName("btaddcustomer"); // NOI18N
        btaddentity1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddentity1ActionPerformed(evt);
            }
        });

        lbitem1.setText("Item Nbr");
        lbitem1.setName("sell_lblitem"); // NOI18N

        tbprice1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbprice1FocusLost(evt);
            }
        });

        jLabel10.setText("Price");
        jLabel10.setName("sell_lblprice"); // NOI18N

        btdeleteitem1.setText("Del Item");
        btdeleteitem1.setName("btdeleteitem"); // NOI18N
        btdeleteitem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitem1ActionPerformed(evt);
            }
        });

        btadditem1.setText("Add Item");
        btadditem1.setName("btadditem"); // NOI18N
        btadditem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditem1ActionPerformed(evt);
            }
        });

        jLabel12.setText("Lot# (optional)");
        jLabel12.setName("sell_lbllot"); // NOI18N

        tbqty1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqty1FocusLost(evt);
            }
        });

        jLabel13.setText("Qty");
        jLabel13.setName("sell_lblqty"); // NOI18N

        jLabel8.setText("PO# (optional)");
        jLabel8.setName("sell_lblpo"); // NOI18N

        jLabel9.setText("Rmks (optional)");
        jLabel9.setName("sell_lblremarks"); // NOI18N

        btLookUpItemDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpItemDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpItemDescActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbitem1)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbrmks1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpo1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbref1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btadditem1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteitem1))
                    .addComponent(tbprice1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tbitemSell, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbitemdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(210, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lbitemdesc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbitem1)
                            .addComponent(tbitemSell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btLookUpItemDesc))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(tbrmks1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadditem1)
                    .addComponent(btdeleteitem1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btLookUpBillTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpBillTo.setToolTipText("lookup");
        btLookUpBillTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpBillToActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblentity1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(tbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btLookUpBillTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(btaddentity1))
                            .addComponent(dcdate1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(lbname1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(expensenbr1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnewsell)))
                                .addGap(18, 18, 18)
                                .addComponent(lbtitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(expensenbr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnewsell))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbname1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbtitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblentity1)
                        .addComponent(btaddentity1)
                        .addComponent(tbcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btLookUpBillTo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcdate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tbactualamt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbactualamt1ActionPerformed(evt);
            }
        });

        jLabel28.setText("Total Amt");
        jLabel28.setName("lbltotal"); // NOI18N

        javax.swing.GroupLayout sellPanelLayout = new javax.swing.GroupLayout(sellPanel);
        sellPanel.setLayout(sellPanelLayout);
        sellPanelLayout.setHorizontalGroup(
            sellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sellPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(sellPanelLayout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbactualamt1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd1))
                    .addComponent(jScrollPane8))
                .addGap(0, 0, 0))
        );
        sellPanelLayout.setVerticalGroup(
            sellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sellPanelLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd1)
                    .addComponent(tbactualamt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addContainerGap())
        );

        add(sellPanel);

        buyPanel.setName("panelmaintbuy"); // NOI18N
        buyPanel.setPreferredSize(new java.awt.Dimension(798, 561));

        btadd.setText("Commit");
        btadd.setName("btcommit"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        detailtable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(detailtable);

        jPanel3.setName("panelbuysub"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(761, 410));

        dcdate.setDateFormatString("yyyy-MM-dd");

        lblentity.setText("Vendor");
        lblentity.setName("buy_lblvendor"); // NOI18N

        ddentity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddentityActionPerformed(evt);
            }
        });

        jLabel24.setText("TransNbr");
        jLabel24.setName("buy_lblnumber"); // NOI18N

        btnewbuy.setText("New");
        btnewbuy.setName("btnew"); // NOI18N
        btnewbuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewbuyActionPerformed(evt);
            }
        });

        jLabel35.setText("Date");
        jLabel35.setName("buy_lbldate"); // NOI18N

        lbtitle.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N

        btaddentity.setText("add new vendor");
        btaddentity.setName("btaddvendor"); // NOI18N
        btaddentity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddentityActionPerformed(evt);
            }
        });

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel6.setText("Price");
        jLabel6.setName("buy_lblprice"); // NOI18N

        jLabel5.setText("Item Description");
        jLabel5.setName("buy_lblitem"); // NOI18N

        btdeleteitem.setText("Del Item");
        btdeleteitem.setName("btdeleteitem"); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btadditem.setText("Add Item");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        jLabel3.setText("Lot# (optional)");
        jLabel3.setName("buy_lbllot"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Qty");
        jLabel7.setName("buy_lblqty"); // NOI18N

        jLabel2.setText("PO# (optional)");
        jLabel2.setName("buy_lblpo"); // NOI18N

        jLabel4.setText("Rmks (optional)");
        jLabel4.setName("buy_lblremarks"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteitem))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(144, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btadditem)
                            .addComponent(btdeleteitem))))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblentity, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ddentity, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaddentity))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lbname, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(expensenbr, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnewbuy)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(expensenbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnewbuy))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbname, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddentity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblentity)
                    .addComponent(btaddentity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );

        tbactualamt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbactualamtActionPerformed(evt);
            }
        });

        jLabel27.setText("Total Amt");
        jLabel27.setName("lbltotal"); // NOI18N

        javax.swing.GroupLayout buyPanelLayout = new javax.swing.GroupLayout(buyPanel);
        buyPanel.setLayout(buyPanelLayout);
        buyPanelLayout.setHorizontalGroup(
            buyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buyPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
            .addComponent(jScrollPane7)
        );
        buyPanelLayout.setVerticalGroup(
            buyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buyPanelLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addContainerGap())
        );

        add(buyPanel);

        expensePanel.setName("panelmaintexpense"); // NOI18N
        expensePanel.setPreferredSize(new java.awt.Dimension(730, 561));

        jPanel6.setName("panelexpensesub"); // NOI18N

        dcdateExpense.setDateFormatString("yyyy-MM-dd");

        lblentity2.setText("Entity");
        lblentity2.setName("expense_lblentity"); // NOI18N

        ddentityExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddentityExpenseActionPerformed(evt);
            }
        });

        jLabel26.setText("TransNbr");
        jLabel26.setName("expense_lblnumber"); // NOI18N

        btnewexpense.setText("New");
        btnewexpense.setName("btnew"); // NOI18N
        btnewexpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewexpenseActionPerformed(evt);
            }
        });

        jLabel37.setText("Date");
        jLabel37.setName("expense_lbldate"); // NOI18N

        lbtitle2.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N

        btexpenseAddEntity.setText("add new vendor");
        btexpenseAddEntity.setName("btaddvendor"); // NOI18N
        btexpenseAddEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexpenseAddEntityActionPerformed(evt);
            }
        });

        jPanel7.setName(""); // NOI18N

        lbitem2.setText("Description:");
        lbitem2.setName("expense_lblacctdesc"); // NOI18N

        tbexpensePrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbexpensePriceFocusLost(evt);
            }
        });

        jLabel16.setText("Price");
        jLabel16.setName("expense_lblprice"); // NOI18N

        jLabel17.setText("Expense Account:");
        jLabel17.setName("expense_lblaccount"); // NOI18N

        ddaccountexpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddaccountexpenseActionPerformed(evt);
            }
        });

        btdeleteItemExpense.setText("Del Item");
        btdeleteItemExpense.setName("btdeleteitem"); // NOI18N
        btdeleteItemExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteItemExpenseActionPerformed(evt);
            }
        });

        btaddItemExpense.setText("Add Item");
        btaddItemExpense.setName("btadditem"); // NOI18N
        btaddItemExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddItemExpenseActionPerformed(evt);
            }
        });

        jLabel18.setText("Lot# (optional)");
        jLabel18.setName("expense_lbllot"); // NOI18N

        tbexpenseQty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbexpenseQtyFocusLost(evt);
            }
        });

        jLabel19.setText("Qty");

        btexpenseAddAccount.setText("Add Account");
        btexpenseAddAccount.setName("btaddaccount"); // NOI18N
        btexpenseAddAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexpenseAddAccountActionPerformed(evt);
            }
        });

        jLabel15.setText("Rmks (optional)");
        jLabel15.setName("expense_lblremarks"); // NOI18N

        jLabel14.setText("PO# (optional)");
        jLabel14.setName("expense_lblpo"); // NOI18N

        btLookUpExpAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpExpAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpExpAccountActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbitem2)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(ddaccountexpense, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btLookUpExpAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbacct2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btexpenseAddAccount))
                    .addComponent(tbexpenseRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbexpenseQty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbexpensePO, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(tbexpenseLOT, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(btaddItemExpense)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteItemExpense))
                    .addComponent(tbexpensePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbexpenseDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(ddaccountexpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbacct2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btexpenseAddAccount)
                    .addComponent(btLookUpExpAccount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbitem2)
                    .addComponent(tbexpenseDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbexpensePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbexpenseQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbexpenseRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbexpensePO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btaddItemExpense)
                        .addComponent(btdeleteItemExpense))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbexpenseLOT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        expenseTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane9.setViewportView(expenseTable);

        tbexpensetotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbexpensetotalActionPerformed(evt);
            }
        });

        btaddexpense.setText("Commit");
        btaddexpense.setName("btcommit"); // NOI18N
        btaddexpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddexpenseActionPerformed(evt);
            }
        });

        jLabel29.setText("Total:");
        jLabel29.setName("lbltotal"); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbexpensetotal, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btaddexpense)
                .addContainerGap())
            .addComponent(jScrollPane9)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddexpense)
                    .addComponent(tbexpensetotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblentity2)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(tbKeyExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnewexpense))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddentityExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbexpenseEntityName, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcdateExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbtitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel26)
                        .addGap(185, 185, 185)
                        .addComponent(btexpenseAddEntity))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbtitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(tbKeyExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnewexpense))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbexpenseEntityName, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddentityExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblentity2)
                            .addComponent(btexpenseAddEntity))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcdateExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout expensePanelLayout = new javax.swing.GroupLayout(expensePanel);
        expensePanel.setLayout(expensePanelLayout);
        expensePanelLayout.setHorizontalGroup(
            expensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expensePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        expensePanelLayout.setVerticalGroup(
            expensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expensePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(expensePanel);

        expenseRecurPanel.setName("panelmaintrecurexpense"); // NOI18N

        btpayselected.setText("Pay Selected Items");
        btpayselected.setName("btpayselecteditems"); // NOI18N
        btpayselected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpayselectedActionPerformed(evt);
            }
        });

        jScrollPane10.setBorder(null);
        jScrollPane10.setName("panelrecurexpensehistory"); // NOI18N

        recurhisttable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane10.setViewportView(recurhisttable);

        tbrexptotamt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbrexptotamtActionPerformed(evt);
            }
        });

        jLabel33.setText("Total Recurring Amount:");
        jLabel33.setName("recurexpense_lbltotalamount"); // NOI18N

        jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane11.setBorder(null);
        jScrollPane11.setName("panelrecurexpensetable"); // NOI18N

        recurexpensetable.setModel(new javax.swing.table.DefaultTableModel(
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
        recurexpensetable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                recurexpensetableMouseClicked(evt);
            }
        });
        jScrollPane11.setViewportView(recurexpensetable);

        jPanel8.setName("panelrecurexpensesub"); // NOI18N

        lbitem3.setText("Expense Acct");
        lbitem3.setName("recurexpense_lblacct"); // NOI18N

        tbrexprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrexpriceFocusLost(evt);
            }
        });

        jLabel22.setText("Price");
        jLabel22.setName("recurexpense_lblprice"); // NOI18N

        jLabel23.setText("Expense Desc");
        jLabel23.setName("recurexpense_lbldescription"); // NOI18N

        ddrexpacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddrexpacctActionPerformed(evt);
            }
        });

        btrexpadditem.setText("Add Recur Expense");
        btrexpadditem.setName("btaddrecurexpense"); // NOI18N
        btrexpadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrexpadditemActionPerformed(evt);
            }
        });

        btexpaddacct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btexpaddacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexpaddacctActionPerformed(evt);
            }
        });

        cbrexpenabled.setText("Visble");
        cbrexpenabled.setName("recurexpense_cbvisible"); // NOI18N
        cbrexpenabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbrexpenabledActionPerformed(evt);
            }
        });

        jLabel32.setText("expID");
        jLabel32.setName("recurexpense_lblexpid"); // NOI18N

        ddrexpentity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddrexpentityActionPerformed(evt);
            }
        });

        lblentity3.setText("VendorCode");
        lblentity3.setName("recurexpense_lblvendor"); // NOI18N

        btaddentity3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddentity3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddentity3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbitem3)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22)
                    .addComponent(jLabel32)
                    .addComponent(lblentity3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(ddrexpacct, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btexpaddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbrexpacctname, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btrexpadditem)
                            .addComponent(tbrexprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbrexpensedesc, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(tbrexpid, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbrexpenabled))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(ddrexpentity, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddentity3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbname3, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddrexpentity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblentity3))
                        .addComponent(btaddentity3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbname3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrexpid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(cbrexpenabled))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbrexpensedesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddrexpacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lbitem3))
                            .addComponent(btexpaddacct, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lbrexpacctname, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrexprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btrexpadditem)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        ddrexpsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddrexpsiteActionPerformed(evt);
            }
        });

        jLabel1.setText("Site");
        jLabel1.setName("recurexpense_lblsite"); // NOI18N

        btrexpclear.setText("Clear");
        btrexpclear.setName("btclear"); // NOI18N
        btrexpclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrexpclearActionPerformed(evt);
            }
        });

        cbrexpshowall.setText("Show All");
        cbrexpshowall.setName("recurexpense_cbshowall"); // NOI18N
        cbrexpshowall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbrexpshowallActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddrexpsite, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbrexpsitename, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btrexpclear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbrexpshowall))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddrexpsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btrexpclear)
                    .addComponent(cbrexpshowall)
                    .addComponent(lbrexpsitename, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel10.add(jPanel1);

        jLabel11.setText("Difference:");
        jLabel11.setName("recurexpense_lbldifference"); // NOI18N

        tbrexpincome.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrexpincomeFocusLost(evt);
            }
        });

        jLabel21.setText("Enter Monthly Net Income:");
        jLabel21.setName("recurexpense_lblnetincome"); // NOI18N

        btupdateincome.setText("Update Income");
        btupdateincome.setName("btupdateincome"); // NOI18N
        btupdateincome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateincomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout expenseRecurPanelLayout = new javax.swing.GroupLayout(expenseRecurPanel);
        expenseRecurPanel.setLayout(expenseRecurPanelLayout);
        expenseRecurPanelLayout.setHorizontalGroup(
            expenseRecurPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expenseRecurPanelLayout.createSequentialGroup()
                .addGroup(expenseRecurPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(expenseRecurPanelLayout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbrexpincome, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdateincome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel33)
                        .addGap(4, 4, 4)
                        .addComponent(tbrexptotamt, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addGap(4, 4, 4)
                        .addComponent(tbrexpdiff, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(expenseRecurPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btpayselected)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        expenseRecurPanelLayout.setVerticalGroup(
            expenseRecurPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expenseRecurPanelLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btpayselected)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(expenseRecurPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel33)
                    .addComponent(tbrexpdiff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btupdateincome)
                    .addComponent(jLabel11)
                    .addComponent(tbrexpincome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(tbrexptotamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        add(expenseRecurPanel);

        incomePanel.setName("panelmaintincome"); // NOI18N
        incomePanel.setPreferredSize(new java.awt.Dimension(730, 561));

        jPanel12.setName("panelincomesub"); // NOI18N

        dcdateIncome.setDateFormatString("yyyy-MM-dd");

        jLabel30.setText("TransNbr");
        jLabel30.setName("income_lblnumber"); // NOI18N

        btnewincome.setText("New");
        btnewincome.setName("btnew"); // NOI18N
        btnewincome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewincomeActionPerformed(evt);
            }
        });

        jLabel39.setText("Date");
        jLabel39.setName("income_lbldate"); // NOI18N

        lbtitleIncome.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N

        lbitem4.setText("Description:");
        lbitem4.setName("income_lbldesc"); // NOI18N

        tbincomeAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbincomeAmountFocusLost(evt);
            }
        });

        jLabel20.setText("Amount");
        jLabel20.setName("income_lblamount"); // NOI18N

        jLabel31.setText("Income Account:");
        jLabel31.setName("income_lblacct"); // NOI18N

        ddaccountincome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddaccountincomeActionPerformed(evt);
            }
        });

        btdeleteItemIncome.setText("Del Item");
        btdeleteItemIncome.setName("btdeleteitem"); // NOI18N
        btdeleteItemIncome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteItemIncomeActionPerformed(evt);
            }
        });

        btaddItemIncome.setText("Add Item");
        btaddItemIncome.setName("btadditem"); // NOI18N
        btaddItemIncome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddItemIncomeActionPerformed(evt);
            }
        });

        btincomeAddAccount.setText("Add Account");
        btincomeAddAccount.setName("btaddaccount"); // NOI18N
        btincomeAddAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btincomeAddAccountActionPerformed(evt);
            }
        });

        jLabel40.setText("Ref (optional)");
        jLabel40.setName("income_lblref"); // NOI18N

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbitem4)
                    .addComponent(jLabel31)
                    .addComponent(jLabel20)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(ddaccountincome, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbincomeacct, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btincomeAddAccount))
                    .addComponent(tbincomeRef, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(btaddItemIncome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteItemIncome))
                    .addComponent(tbincomeAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbincomeDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel31)
                        .addComponent(ddaccountincome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbincomeacct, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btincomeAddAccount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbitem4)
                    .addComponent(tbincomeDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbincomeAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(36, 36, 36)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbincomeRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addGap(30, 30, 30)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddItemIncome)
                    .addComponent(btdeleteItemIncome))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        incomeTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane12.setViewportView(incomeTable);

        tbincometotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbincometotalActionPerformed(evt);
            }
        });

        btaddincome.setText("Commit");
        btaddincome.setName("btcommit"); // NOI18N
        btaddincome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddincomeActionPerformed(evt);
            }
        });

        jLabel42.setText("Total:");
        jLabel42.setName("income_lbltotal"); // NOI18N

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbincometotal, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btaddincome)
                .addContainerGap())
            .addComponent(jScrollPane12)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddincome)
                    .addComponent(tbincometotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(tbKeyIncome, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnewincome))
                    .addComponent(dcdateIncome, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbtitleIncome, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel30))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbtitleIncome, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(tbKeyIncome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnewincome))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcdateIncome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39))))
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout incomePanelLayout = new javax.swing.GroupLayout(incomePanel);
        incomePanel.setLayout(incomePanelLayout);
        incomePanelLayout.setHorizontalGroup(
            incomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(incomePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        incomePanelLayout.setVerticalGroup(
            incomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(incomePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(incomePanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewbuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewbuyActionPerformed
        expensenbr.setText(String.valueOf(OVData.getNextNbr("voucher")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdate.setDate(now);
                lbtitle.setText("Buy");
                enableBuy();
               btnewbuy.setEnabled(false);
               tbqty.setText("1");         
               voucherline = 0;
               
               BlueSeerUtils.messagereset();
        
    }//GEN-LAST:event_btnewbuyActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
       
       if (tbprice.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbprice.requestFocus();
           return;
       }
       
       if (tbitemservice.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbitemservice.requestFocus();
           return;
       }
       
       if (tbqty.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbqty.requestFocus();
           return;
       }
        
       partnumber = String.valueOf(OVData.getNextNbr("item"));
              
       
        
       // Pattern p = Pattern.compile("\\d\\.\\d\\d");
      //  Matcher m = p.matcher(tbprice.getText());
       // receiverdet  "Part", "PO", "line", "Qty",  listprice, disc, netprice, loc, serial, lot, recvID, recvLine
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
       // shipperdet   "Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "shippedqty", "status", "WH", "LOC", "Desc"
            voucherline++;
                        
            
            buymodel.addRow(new Object[] { voucherline,
                                                  partnumber,
                                                  tbqty.getText(),
                                                  tbprice.getText(),
                                                  tbitemservice.getText().replace("'",""),
                                                  tbref.getText()
                                                  });
            
            double actamt = 0.00;
            for (int j = 0; j < detailtable.getRowCount(); j++) {
                actamt += (bsParseDouble(detailtable.getValueAt(j, 2).toString()) * bsParseDouble(detailtable.getValueAt(j, 3).toString()));
            }
        tbitemservice.setText("");
        tbprice.setText("");
        tbactualamt.setText(currformatDouble(actamt));
        tbitemservice.requestFocus();
        
        
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        BlueSeerUtils.startTask(new String[]{"","Committing..."});
        disableBuy();
        btnewbuy.setEnabled(true);
        Task task = new Task("buy");
        task.execute();   
       
    }//GEN-LAST:event_btaddActionPerformed

    private void ddentityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddentityActionPerformed
       
        if (ddentity.getSelectedItem() != null ) {
            vd_mstr vd = getVendMstr(new String[]{ddentity.getSelectedItem().toString()});
               lbname.setText(vd.vd_name());
        }
        
    }//GEN-LAST:event_ddentityActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = detailtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031, String.valueOf(i)));            
            ((javax.swing.table.DefaultTableModel) detailtable.getModel()).removeRow(i);
           voucherline--;
        }
        
        double actamt = 0.00;
            for (int j = 0; j < detailtable.getRowCount(); j++) {
                actamt += (bsParseDouble(detailtable.getValueAt(j, 2).toString()) * bsParseDouble(detailtable.getValueAt(j, 3).toString()));
            }
        tbactualamt.setText(currformatDouble(actamt));
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void tbactualamtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbactualamtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbactualamtActionPerformed

    private void btaddentityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddentityActionPerformed
                reinitpanels("VendMaint", true, new String[]{});
    }//GEN-LAST:event_btaddentityActionPerformed

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
        String x = BlueSeerUtils.bsformat("", tbprice.getText(), "2");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1028));
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
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void btadd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadd1ActionPerformed
        BlueSeerUtils.startTask(new String[]{"","Committing..."});
        disableSell();
        btnewsell.setEnabled(true);
        Task task = new Task("sell");
        task.execute();   
    }//GEN-LAST:event_btadd1ActionPerformed

    private void btnewsellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewsellActionPerformed
         expensenbr1.setText(String.valueOf(OVData.getNextNbr("voucher")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdate1.setDate(now);
                
                enableSell();
               voucherline = 0;
               btnewsell.setEnabled(false);
               tbqty1.setText("1");               
               lbtitle1.setText("Sell");
               
               BlueSeerUtils.messagereset();
        
    }//GEN-LAST:event_btnewsellActionPerformed

    private void btaddentity1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddentity1ActionPerformed
        reinitpanels("CustMaint", true, new String[]{});
    }//GEN-LAST:event_btaddentity1ActionPerformed

    private void tbprice1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbprice1FocusLost
        String x = BlueSeerUtils.bsformat("", tbprice1.getText(), "2");
        if (x.equals("error")) {
            tbprice1.setText("");
            tbprice1.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1028));
            tbprice1.requestFocus();
        } else {
            tbprice1.setText(x);
            tbprice1.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbprice1FocusLost

    private void btdeleteitem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btdeleteitem1ActionPerformed

    private void btadditem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditem1ActionPerformed
         if (tbprice1.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbprice1.requestFocus();
           return;
       }
           if (tbqty1.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbqty1.requestFocus();
           return;
       }
       
      
        
       partnumber = String.valueOf(OVData.getNextNbr("item"));
              
       
        
       // Pattern p = Pattern.compile("\\d\\.\\d\\d");
      //  Matcher m = p.matcher(tbprice.getText());
       // receiverdet  "Part", "PO", "line", "Qty",  listprice, disc, netprice, loc, serial, lot, recvID, recvLine
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
       // shipperdet   "Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "shippedqty", "status", "WH", "LOC", "Desc"
            voucherline++;
           
           
            sellmodel.addRow(new Object[] { voucherline, 
                                            tbitemSell.getText(),
                                            tbqty1.getText(),
                                            tbprice1.getText(),
                                            lbitemdesc.getText().replace("'",""),
                                            tbref1.getText()
                                          });
            
            double actamt = 0.00;
            for (int j = 0; j < detailtable.getRowCount(); j++) {
                actamt += (bsParseDouble(detailtable.getValueAt(j, 2).toString()) * bsParseDouble(detailtable.getValueAt(j, 3).toString()));
            }
            
       
        tbprice1.setText("");
        tbactualamt1.setText(currformatDouble(actamt));
        tbitemSell.setText("");
        tbitemSell.requestFocus();
    }//GEN-LAST:event_btadditem1ActionPerformed

    private void tbqty1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqty1FocusLost
          String x = BlueSeerUtils.bsformat("", tbqty1.getText(), "0");
        if (x.equals("error")) {
            tbqty1.setText("");
            tbqty1.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty1.requestFocus();
        } else {
            tbqty1.setText(x);
            tbqty1.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqty1FocusLost

    private void tbactualamt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbactualamt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbactualamt1ActionPerformed

    private void btaddexpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddexpenseActionPerformed
        BlueSeerUtils.startTask(new String[]{"","Committing..."});
        disableExpense();
        btnewexpense.setEnabled(true);
        Task task = new Task("expense");
        task.execute();  
    }//GEN-LAST:event_btaddexpenseActionPerformed

    private void ddentityExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddentityExpenseActionPerformed
           if (ddentityExpense.getSelectedItem() != null ) {
               vd_mstr vd = getVendMstr(new String[]{ddentityExpense.getSelectedItem().toString()});
               lbexpenseEntityName.setText(vd.vd_name());
           }
       
    }//GEN-LAST:event_ddentityExpenseActionPerformed

    private void btnewexpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewexpenseActionPerformed
         tbKeyExpense.setText(String.valueOf(OVData.getNextNbr("voucher")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdateExpense.setDate(now);
                lbtitle2.setText("Expense");
                enableExpense();
               btnewexpense.setEnabled(false);
               btaddexpense.setEnabled(false);
               tbexpenseQty.setText("1");               
               voucherline = 0;
               
               BlueSeerUtils.messagereset();
    }//GEN-LAST:event_btnewexpenseActionPerformed

    private void btexpenseAddEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexpenseAddEntityActionPerformed
         reinitpanels("VendMaint", true, new String[]{});
    }//GEN-LAST:event_btexpenseAddEntityActionPerformed

    private void tbexpensePriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbexpensePriceFocusLost
        String x = BlueSeerUtils.bsformat("", tbexpensePrice.getText(), "2");
        if (x.equals("error")) {
            tbexpensePrice.setText("");
            tbexpensePrice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1028));
            tbexpensePrice.requestFocus();
        } else {
            tbexpensePrice.setText(x);
            tbexpensePrice.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbexpensePriceFocusLost

    private void ddaccountexpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddaccountexpenseActionPerformed
         if (ddaccountexpense.getSelectedItem() != null && ! isLoad ) {
             AcctMstr ac = getAcctMstr(new String[]{ddaccountexpense.getSelectedItem().toString()});
           lbacct2.setText(ac.desc());
         }
       
    }//GEN-LAST:event_ddaccountexpenseActionPerformed

    private void btdeleteItemExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteItemExpenseActionPerformed
         
        int[] rows = expenseTable.getSelectedRows();
        for (int i : rows) {
            ((javax.swing.table.DefaultTableModel) expenseTable.getModel()).removeRow(i);
        }
        
         if (expensemodel.getRowCount() > 0) {
                btaddexpense.setEnabled(true);
            }
         
                    try {
                        sumExpenseTotal();
                    } catch (ParseException ex) {
                        bslog(ex);
                    }
    }//GEN-LAST:event_btdeleteItemExpenseActionPerformed

    private void btaddItemExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddItemExpenseActionPerformed
        if (tbexpensePrice.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbexpensePrice.requestFocus();
           return;
       }
       
        if (tbexpenseQty.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbexpenseQty.requestFocus();
           return;
       }
        
       if (tbexpenseDesc.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbexpenseDesc.requestFocus();
           return;
       }
        
       partnumber = String.valueOf(OVData.getNextNbr("item"));
         voucherline++;
            
           
                 //"Line", "Item", "Qty", "Price", "Ref", "Acct"
            expensemodel.addRow(new Object[] { voucherline, 
                                            tbexpenseDesc.getText().replace("'",""),
                                            tbexpenseQty.getText(),
                                            tbexpensePrice.getText(),
                                            tbexpenseLOT.getText(),
                                            ddaccountexpense.getSelectedItem().toString()
                                          });
         
            if (expensemodel.getRowCount() > 0) {
                btaddexpense.setEnabled(true);
            }
            
        tbexpenseDesc.setText("");
        tbexpensePrice.setText("");
        
        ddaccountexpense.setSelectedIndex(0);
        
                    try {
                        sumExpenseTotal();
                    } catch (ParseException ex) {
                        bslog(ex);
                    }
        
        tbexpenseDesc.requestFocus();
    }//GEN-LAST:event_btaddItemExpenseActionPerformed

    private void tbexpenseQtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbexpenseQtyFocusLost
          String x = BlueSeerUtils.bsformat("", tbexpenseQty.getText(), "0");
        if (x.equals("error")) {
            tbexpenseQty.setText("");
            tbexpenseQty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1028));
            tbexpenseQty.requestFocus();
        } else {
            tbexpenseQty.setText(x);
            tbexpenseQty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbexpenseQtyFocusLost

    private void btexpenseAddAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexpenseAddAccountActionPerformed
         String s = bsmf.MainFrame.input("Please input the account description: ");
       addExpenseAccount(s);
    }//GEN-LAST:event_btexpenseAddAccountActionPerformed

    private void tbexpensetotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbexpensetotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbexpensetotalActionPerformed

    private void btpayselectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpayselectedActionPerformed
        BlueSeerUtils.startTask(new String[]{"","Committing..."});
        // disableRecurExpense();
        Task task = new Task("recurexpense");
        task.execute(); 
    }//GEN-LAST:event_btpayselectedActionPerformed

    private void ddrexpentityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddrexpentityActionPerformed
            if (ddrexpentity.getSelectedItem() != null ) {
             vd_mstr vd = getVendMstr(new String[]{ddrexpentity.getSelectedItem().toString()});
             lbname3.setText(vd.vd_name());
            }
    }//GEN-LAST:event_ddrexpentityActionPerformed

    private void btaddentity3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddentity3ActionPerformed
         reinitpanels("VendMaint", true, new String[]{}); 
    }//GEN-LAST:event_btaddentity3ActionPerformed

    private void tbrexpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrexpriceFocusLost
        String x = BlueSeerUtils.bsformat("", tbrexprice.getText(), "2");
        if (x.equals("error")) {
            tbrexprice.setText("");
            tbrexprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1028));
            tbrexprice.requestFocus();
        } else {
            tbrexprice.setText(x);
            tbrexprice.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbrexpriceFocusLost

    private void ddrexpacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddrexpacctActionPerformed
       if (ddrexpacct.getSelectedItem() != null && ! isLoad ) {
           AcctMstr ac = getAcctMstr(new String[]{ddrexpacct.getSelectedItem().toString()});
           lbrexpacctname.setText(ac.desc());
       }       
    }//GEN-LAST:event_ddrexpacctActionPerformed

    private void btrexpadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrexpadditemActionPerformed
        if (tbrexprice.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbrexprice.requestFocus();
           return;
       }
       
       if (tbrexpensedesc.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbrexpensedesc.requestFocus();
           return;
       }
       
       if (ddrexpentity.getSelectedItem() == null) {
          bsmf.MainFrame.show(getMessageTag(1029));
           ddrexpentity.requestFocus();
           return; 
       }
       
       String[] m = addExpMstr(createExpMstrRecord());
       if (m[0].equals("0")) {
           bsmf.MainFrame.show(getMessageTag(1030));
       }
       
        getRecurringExpense(cbrexpshowall.isSelected());
        calcdiff(); 
        
        tbrexpensedesc.setText("");
        tbrexprice.setText("");
        
        ddrexpacct.setSelectedIndex(0);
        tbrexpensedesc.requestFocus();
    }//GEN-LAST:event_btrexpadditemActionPerformed

    private void btexpaddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexpaddacctActionPerformed
        String s = bsmf.MainFrame.input("Please input the account description: ");
       addExpenseAccount(s);
    }//GEN-LAST:event_btexpaddacctActionPerformed

    private void tbrexptotamtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbrexptotamtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbrexptotamtActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
       if (! isLoad) {
        JTabbedPane tabSource = (JTabbedPane) evt.getSource();
       String tab = tabSource.getTitleAt(tabSource.getSelectedIndex());
    //   if (tab.equals("buy")) {
    //       bsmf.MainFrame.show("mustbe:" + tab);
    //   }
      
       switch(tab) {
                case "buy asset":
                    clearBuy();
                    disableBuy();
                    btnewbuy.setEnabled(true);
                    break;
                    
                case "sell asset":
                    clearSell();
                    disableSell();
                    btnewsell.setEnabled(true);
                    break;
                    
                case "misc expense":
                    clearExpense();
                    disableExpense();
                    btnewexpense.setEnabled(true);
                    break;
                
                case "misc income":
                    clearIncome();
                    disableIncome();
                    btnewincome.setEnabled(true);
                    break;
                    
                case "recurring expense":
                    clearRecurExpense();
                   // disableRecurExpense();
                    break;
                    
                default:
                    disableBuy();
            }
      
       }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
        
    }//GEN-LAST:event_jTabbedPane1ComponentShown

    private void tbrexpincomeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrexpincomeFocusLost
        String x = BlueSeerUtils.bsformat("", tbrexpincome.getText(), "2");
        if (x.equals("error")) {
            tbrexpincome.setText("");
            tbrexpincome.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1028));
            tbrexpincome.requestFocus();
        } else {
            tbrexpincome.setText(x);
            tbrexpincome.setBackground(Color.white);
        }
        calcdiff();
    }//GEN-LAST:event_tbrexpincomeFocusLost

    private void recurexpensetableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recurexpensetableMouseClicked
         int row = recurexpensetable.rowAtPoint(evt.getPoint());
        int col = recurexpensetable.columnAtPoint(evt.getPoint());
        if ( col == 0) {
              getHistory(recurexpensetable.getValueAt(row, 1).toString());
        }
        getRecurringExpenseRecord(recurexpensetable.getValueAt(row, 1).toString());
    //    if ( col == 9) {
    //          recurexpensetable.setValueAt(null, row, 9);
    //    }
    }//GEN-LAST:event_recurexpensetableMouseClicked

    private void btupdateincomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateincomeActionPerformed
         
        String r = updateRecurExp_Income(ddrexpsite.getSelectedItem().toString(), tbrexpincome.getText());
        bsmf.MainFrame.show(r);       
    }//GEN-LAST:event_btupdateincomeActionPerformed

    private void btnewincomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewincomeActionPerformed
         tbKeyIncome.setText(String.valueOf(OVData.getNextNbr("gl")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdateIncome.setDate(now);
                lbtitleIncome.setText("Income");
                enableIncome();
               btnewincome.setEnabled(false);
               btaddincome.setEnabled(false);  
               incomeline = 0;
               BlueSeerUtils.messagereset();
    }//GEN-LAST:event_btnewincomeActionPerformed

    private void tbincomeAmountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbincomeAmountFocusLost
        String x = BlueSeerUtils.bsformat("", tbincomeAmount.getText(), "2");
        if (x.equals("error")) {
            tbincomeAmount.setText("");
            tbincomeAmount.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1028));
            tbincomeAmount.requestFocus();
        } else {
            tbincomeAmount.setText(x);
            tbincomeAmount.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbincomeAmountFocusLost

    private void ddaccountincomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddaccountincomeActionPerformed
        if (ddaccountincome.getSelectedItem() != null && ! isLoad ) {
            AcctMstr ac = getAcctMstr(new String[]{ddaccountincome.getSelectedItem().toString()});
           lbincomeacct.setText(ac.desc());
        }        
    }//GEN-LAST:event_ddaccountincomeActionPerformed

    private void btdeleteItemIncomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteItemIncomeActionPerformed
          int[] rows = incomeTable.getSelectedRows();
        for (int i : rows) {
            ((javax.swing.table.DefaultTableModel) incomeTable.getModel()).removeRow(i);
        }
        
         if (incomemodel.getRowCount() > 0) {
                btaddincome.setEnabled(true);
            }
         
         sumIncomeTotal();
    }//GEN-LAST:event_btdeleteItemIncomeActionPerformed

    private void btaddItemIncomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddItemIncomeActionPerformed
         if (tbincomeAmount.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbincomeAmount.requestFocus();
           return;
       }
               
       if (tbincomeDesc.getText().isEmpty()) {
           bsmf.MainFrame.show(getMessageTag(1024));
           tbincomeDesc.requestFocus();
           return;
       }
       
       String ref = "misc income";
       if (! tbincomeRef.getText().isEmpty()) {
           ref = ref + "/" + tbincomeRef.getText();
       }
       incomeline++;
                 //"Line", "Item", "Qty", "Price", "Ref", "Acct"
           incomemodel.addRow(new Object[] { incomeline, 
                                            tbincomeDesc.getText().replace("'",""),
                                            '1',
                                            tbincomeAmount.getText(),
                                            ref,
                                            ddaccountincome.getSelectedItem().toString()
                                          });
         
            if (incomemodel.getRowCount() > 0) {
                btaddincome.setEnabled(true);
            }
            
        tbincomeDesc.setText("");
        tbincomeAmount.setText("");
        
        ddaccountincome.setSelectedIndex(0);
        
        sumIncomeTotal();
        
        tbincomeDesc.requestFocus();
    }//GEN-LAST:event_btaddItemIncomeActionPerformed

    private void btincomeAddAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btincomeAddAccountActionPerformed
           String s = bsmf.MainFrame.input("Please input the account description: ");
       addIncomeAccount(s);
    }//GEN-LAST:event_btincomeAddAccountActionPerformed

    private void tbincometotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbincometotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbincometotalActionPerformed

    private void btaddincomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddincomeActionPerformed
         BlueSeerUtils.startTask(new String[]{"","Committing..."});
        disableIncome();
        btnewincome.setEnabled(true);
        Task task = new Task("income");
        task.execute();  
    }//GEN-LAST:event_btaddincomeActionPerformed

    private void cbrexpenabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbrexpenabledActionPerformed
        if (! tbrexpid.getText().isEmpty()) { 
            updateExpActive(tbrexpid.getText(), BlueSeerUtils.boolToString(cbrexpenabled.isSelected()));         
        }
    }//GEN-LAST:event_cbrexpenabledActionPerformed

    private void cbrexpshowallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbrexpshowallActionPerformed
        if (! isLoad) {
        getRecurringExpense(cbrexpshowall.isSelected());
        calcdiff();
        }
    }//GEN-LAST:event_cbrexpshowallActionPerformed

    private void btrexpclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrexpclearActionPerformed
        clearRecurExpense();
    }//GEN-LAST:event_btrexpclearActionPerformed

    private void ddrexpsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddrexpsiteActionPerformed
        if (! isLoad) {
        String[] siteinfo = admData.getSiteAddressInfo(ddrexpsite.getSelectedItem().toString());
        lbrexpsitename.setText(siteinfo[1]);
        getRecurringExpense(cbrexpshowall.isSelected());
        calcdiff();
        }
    }//GEN-LAST:event_ddrexpsiteActionPerformed

    private void btLookUpExpAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpExpAccountActionPerformed
        lookUpFrameAcctDesc();
    }//GEN-LAST:event_btLookUpExpAccountActionPerformed

    private void expensenbr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expensenbr1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expensenbr1ActionPerformed

    private void btLookUpItemDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemDescActionPerformed
        lookUpFrameItemDesc();
    }//GEN-LAST:event_btLookUpItemDescActionPerformed

    private void btLookUpBillToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpBillToActionPerformed
        lookUpFrameBillTo();
    }//GEN-LAST:event_btLookUpBillToActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpBillTo;
    private javax.swing.JButton btLookUpExpAccount;
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadd1;
    private javax.swing.JButton btaddItemExpense;
    private javax.swing.JButton btaddItemIncome;
    private javax.swing.JButton btaddentity;
    private javax.swing.JButton btaddentity1;
    private javax.swing.JButton btaddentity3;
    private javax.swing.JButton btaddexpense;
    private javax.swing.JButton btaddincome;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btadditem1;
    private javax.swing.JButton btdeleteItemExpense;
    private javax.swing.JButton btdeleteItemIncome;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btdeleteitem1;
    private javax.swing.JButton btexpaddacct;
    private javax.swing.JButton btexpenseAddAccount;
    private javax.swing.JButton btexpenseAddEntity;
    private javax.swing.JButton btincomeAddAccount;
    private javax.swing.JButton btnewbuy;
    private javax.swing.JButton btnewexpense;
    private javax.swing.JButton btnewincome;
    private javax.swing.JButton btnewsell;
    private javax.swing.JButton btpayselected;
    private javax.swing.JButton btrexpadditem;
    private javax.swing.JButton btrexpclear;
    private javax.swing.JButton btupdateincome;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel buyPanel;
    private javax.swing.JCheckBox cbrexpenabled;
    private javax.swing.JCheckBox cbrexpshowall;
    private com.toedter.calendar.JDateChooser dcdate;
    private com.toedter.calendar.JDateChooser dcdate1;
    private com.toedter.calendar.JDateChooser dcdateExpense;
    private com.toedter.calendar.JDateChooser dcdateIncome;
    private static javax.swing.JComboBox<String> ddaccountexpense;
    private javax.swing.JComboBox<String> ddaccountincome;
    private javax.swing.JComboBox ddentity;
    private javax.swing.JComboBox ddentityExpense;
    private javax.swing.JComboBox<String> ddrexpacct;
    private javax.swing.JComboBox ddrexpentity;
    private javax.swing.JComboBox<String> ddrexpsite;
    private javax.swing.JTable detailtable;
    private javax.swing.JTable detailtable1;
    private javax.swing.JPanel expensePanel;
    private javax.swing.JPanel expenseRecurPanel;
    private javax.swing.JTable expenseTable;
    private javax.swing.JTextField expensenbr;
    private javax.swing.JTextField expensenbr1;
    private javax.swing.JFileChooser fc;
    private javax.swing.JPanel incomePanel;
    private javax.swing.JTable incomeTable;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbacct;
    private javax.swing.JLabel lbacct2;
    private javax.swing.JLabel lbexpenseEntityName;
    private javax.swing.JLabel lbincomeacct;
    private javax.swing.JLabel lbitem1;
    private javax.swing.JLabel lbitem2;
    private javax.swing.JLabel lbitem3;
    private javax.swing.JLabel lbitem4;
    private javax.swing.JLabel lbitemdesc;
    private javax.swing.JLabel lblentity;
    private javax.swing.JLabel lblentity1;
    private javax.swing.JLabel lblentity2;
    private javax.swing.JLabel lblentity3;
    private javax.swing.JLabel lbname;
    private javax.swing.JLabel lbname1;
    private javax.swing.JLabel lbname3;
    private javax.swing.JLabel lbrexpacctname;
    private javax.swing.JLabel lbrexpsitename;
    private javax.swing.JLabel lbtitle;
    private javax.swing.JLabel lbtitle1;
    private javax.swing.JLabel lbtitle2;
    private javax.swing.JLabel lbtitleIncome;
    private javax.swing.JTable recurexpensetable;
    private javax.swing.JTable recurhisttable;
    private javax.swing.JPanel sellPanel;
    private javax.swing.JTextField tbKeyExpense;
    private javax.swing.JTextField tbKeyIncome;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbactualamt1;
    private javax.swing.JTextField tbcust;
    private javax.swing.JTextField tbexpenseDesc;
    private javax.swing.JTextField tbexpenseLOT;
    private javax.swing.JTextField tbexpensePO;
    private javax.swing.JTextField tbexpensePrice;
    private javax.swing.JTextField tbexpenseQty;
    private javax.swing.JTextField tbexpenseRemarks;
    private javax.swing.JTextField tbexpensetotal;
    private javax.swing.JTextField tbincomeAmount;
    private javax.swing.JTextField tbincomeDesc;
    private javax.swing.JTextField tbincomeRef;
    private javax.swing.JTextField tbincometotal;
    private javax.swing.JTextField tbitemSell;
    private javax.swing.JTextField tbitemservice;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbpo1;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbprice1;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbqty1;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbref1;
    private javax.swing.JTextField tbrexpdiff;
    private javax.swing.JTextField tbrexpensedesc;
    private javax.swing.JTextField tbrexpid;
    private javax.swing.JTextField tbrexpincome;
    private javax.swing.JTextField tbrexprice;
    private javax.swing.JTextField tbrexptotamt;
    private javax.swing.JTextField tbrmks;
    private javax.swing.JTextField tbrmks1;
    // End of variables declaration//GEN-END:variables
}
