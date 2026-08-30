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
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.fgl.fglData.getAccountBalanceDetView;
import static com.blueseer.fgl.fglData.getGLCalYearsRange;
import static com.blueseer.utl.BlueSeerUtils.bsFormatInt;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.dropColumn;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.sendServerRequest;
import com.blueseer.utl.DTData;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author vaughnte
 */
public class BalanceSheetReport extends javax.swing.JPanel {
 
     public Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
     public ArrayList<String[]> accounts;
     public String data = null;
     ArrayList<String[]> initDataSets = new ArrayList<>();
    String defaultSite = "";
    String defaultCurrency = "";
    String[] glCalDateArray;
    ArrayList<String> datelabels = new ArrayList<>();
    public String rsData; 
     Object[][] roData;
     boolean isLoad = false;
     
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("account"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("currency"), 
                            getGlobalColumnTag("description"), 
                            getGlobalColumnTag("site"), 
                            getGlobalColumnTag("beginbalance"), 
                            getGlobalColumnTag("activity"), 
                            getGlobalColumnTag("endbalance")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  )       
                            return ImageIcon.class; 
                        else if (col == 6 || col == 7 || col == 8) 
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
    
    
    javax.swing.table.DefaultTableModel mymodelCC = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("account"),
                            getGlobalColumnTag("costcenter"),
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("currency"), 
                            getGlobalColumnTag("description"), 
                            getGlobalColumnTag("site"), 
                            getGlobalColumnTag("beginbalance"), 
                            getGlobalColumnTag("activity"), 
                            getGlobalColumnTag("endbalance")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  )       
                            return ImageIcon.class; 
                        else if (col == 7 || col == 8 || col == 9) 
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("account"), 
                            getGlobalColumnTag("costcenter"), 
                            getGlobalColumnTag("site"), 
                            getGlobalColumnTag("reference"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("effectivedate"), 
                            getGlobalColumnTag("description"), 
                            getGlobalColumnTag("amount")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 7 )   
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
    
    
   
    
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
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
   
    private static class myHeaderRenderer implements TableCellRenderer {
      DefaultTableCellRenderer renderer;
      int horAlignment;
      public myHeaderRenderer(JTable table, int horizontalAlignment) {
        horAlignment = horizontalAlignment;
        renderer = (DefaultTableCellRenderer)table.getTableHeader()
            .getDefaultRenderer();
      }
      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = renderer.getTableCellRendererComponent(table, value,
          isSelected, hasFocus, row, col);
        JLabel label = (JLabel)c;
        label.setHorizontalAlignment(horAlignment);
        return label;
      }
}

    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public BalanceSheetReport() {
        initComponents();
        setLanguageTags(this);
    }

    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
         
          String action = "";
          String[] key = null;
          
          public Task(String action, String[] key) { 
              this.action = action;
              this.key = key;
          }     
            
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            rsData = "";
            
            
            switch(this.action) {
                case "dataInit":
                    message = getInitialization();
                    break;
                    
                case "getBrowseView":
                    message = getBrowseView();
                    break;
    
                case "getBrowseViewDet":
                    message = getBrowseViewDet(key);
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
            
            
            if (this.action.equals("dataInit")) {
                    done_Initialization();
            }
            
            if (this.action.equals("getBrowseView")) {
                done_getBrowseView();
            }
            
            if (this.action.equals("getBrowseViewDet")) {
                done_getBrowseViewDet();
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
    
    public void disableAll() {
       btdetail.setEnabled(false);
       btRun.setEnabled(false);
       btprint.setEnabled(false);
       ddyear.setEnabled(false);
       ddperiod.setEnabled(false);
       ddendperiod.setEnabled(false);
       ddacctfrom.setEnabled(false);
       ddacctto.setEnabled(false);
       tablepanel.setEnabled(false);
    }
    
    public void enableAll() {
      btdetail.setEnabled(true);
       btRun.setEnabled(true);
       btprint.setEnabled(true);
       ddyear.setEnabled(true);
       ddperiod.setEnabled(true);
       ddendperiod.setEnabled(true);
       ddacctfrom.setEnabled(true);
       ddacctto.setEnabled(true);
       tablepanel.setEnabled(true); 
    }
     
    public String[] getInitialization() {
        java.util.Date now = new java.util.Date();
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "accounts");
        
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }  
    
    public void done_Initialization() {
        lblendbal.setText("0");
        lblbegbal.setText("0");
        lblactbal.setText("0");
        lblnet.setText("");
        lblfromacct.setText("");
        lbltoacct.setText("");
        
        
        
        mymodel.setNumRows(0);
         mymodelCC.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldetail);
        
        tablereport.getTableHeader().setReorderingAllowed(false);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        btdetail.setEnabled(false);
        detailpanel.setVisible(false);
        
        tabledetail.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency))); 
       
          tablereport.setModel(mymodel);
          tablereport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
          tablereport.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
          tablereport.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));    
        //  tablereport.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
        
        
        for (int i = 0 ; i < tablereport.getColumnCount(); i++) { 
              
                   if (i == 6 || i == 7 || i == 8) {
                 tablereport.getTableHeader().getColumnModel().getColumn(i)
                 .setHeaderRenderer(new myHeaderRenderer(tablereport, JLabel.RIGHT));
                  } else {
                  tablereport.getTableHeader().getColumnModel().getColumn(i)
                 .setHeaderRenderer(new myHeaderRenderer(tablereport, JLabel.LEFT));    
                  }
              
        }

        
        ddsite.removeAllItems();
        
        
       
        
        
        
        
        for (String[] s : initDataSets) {
            
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultSite = s[1]; 
            }
            if (s[0].equals("accounts")) {
              ddacctfrom.addItem(s[1]); 
              ddacctto.addItem(s[1]);
            }
            if (s[0].equals("currency")) {
              defaultCurrency = s[1]; 
            }
        }
        
        
        if (ddsite.getItemCount() > 0) {
            ddsite.setSelectedItem(defaultSite);
        }
        
        ddacctfrom.setSelectedIndex(0);
        ddacctto.setSelectedIndex(ddacctto.getItemCount() - 1);
        
       // accounts = fglData.getGLAcctListRangeWCurrTypeDesc(ddacctfrom.getSelectedItem().toString(), ddacctto.getSelectedItem().toString());
        
    }
    
    public void initvars(String[] arg) {
        isLoad = true;
        java.util.Date now = new java.util.Date();
        glCalDateArray = fglData.getGLCalForDate(now);
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        ddyear.removeAllItems();
        ddperiod.removeAllItems();
        ddendperiod.removeAllItems();
        ArrayList<String> years = getGLCalYearsRange();
        for (String y : years) {
            ddyear.addItem(y);
        }
        ddyear.setSelectedItem(bsNumber(dfyear.format(now)));
        for (int i = 1 ; i <= 12; i++) {
            ddperiod.addItem(bsFormatInt(i));
            ddendperiod.addItem(bsFormatInt(i));
        }
        ddperiod.setSelectedItem(bsNumber(glCalDateArray[1]));
        ddendperiod.setSelectedItem(bsNumber(glCalDateArray[1]));
        datelabels = fglData.getGLCalForPeriodRange(bsParseInt(ddyear.getSelectedItem().toString()), bsParseInt(ddperiod.getSelectedItem().toString()), bsParseInt(ddendperiod.getSelectedItem().toString()));
        datelabel.setText(datelabels.get(0) + " To " + datelabels.get(1));
        isLoad = false;
        executeTask("dataInit", null);
    }
    
    public String[] getBrowseView() {
        
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getBalanceSheetView"});
        list.add(new String[]{"param1",ddyear.getSelectedItem().toString()});
        list.add(new String[]{"param2",ddperiod.getSelectedItem().toString()});
        list.add(new String[]{"param3",ddendperiod.getSelectedItem().toString()});
        list.add(new String[]{"param4",ddsite.getSelectedItem().toString()});
        
        try {
                jsonString = sendServerPost(list, "", null, "dataServFIN"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getAccountBalanceView")};
            }
        } else {
            jsonString = fglData.getBalanceSheetView(new String[]{
                ddyear.getSelectedItem().toString(),
                ddperiod.getSelectedItem().toString(),
                ddendperiod.getSelectedItem().toString(),
                ddsite.getSelectedItem().toString()
            });
        }
      
      if (jsonString == null) {
          return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getBalanceSheetView return jsonString is null")};
      }
        
      roData = jsonToData(jsonString);
       
      return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
    }

    public void done_getBrowseView() {
       
          tablereport.setModel(mymodel);
          tablereport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
          tablereport.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
          tablereport.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));    
        //  tablereport.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
      
        
        for (int i = 0 ; i < tablereport.getColumnCount(); i++) { 
             
                   if (i == 6 || i == 7 || i == 8) {
                 tablereport.getTableHeader().getColumnModel().getColumn(i)
                 .setHeaderRenderer(new myHeaderRenderer(tablereport, JLabel.RIGHT));
                  } else {
                  tablereport.getTableHeader().getColumnModel().getColumn(i)
                 .setHeaderRenderer(new myHeaderRenderer(tablereport, JLabel.LEFT));    
                  }
              
        }
        
        int i = 0;
        double totendbal = 0;
        double totbegbal = 0;
        double totactivity = 0;
        double totassets = 0;
        double totliables = 0;
        double totaloe = 0;
        mymodel.setNumRows(0);
        mymodelCC.setNumRows(0);
        if (roData != null) {
            
             // drop column 16
      Object[][] newdata = null;
      
          newdata = roData;
      
        
       if (newdata != null) {
        for (Object[] rowData : newdata) {
            
            
            totendbal = totendbal + bsParseDouble(rowData[8].toString());
            totbegbal = totbegbal + bsParseDouble(rowData[6].toString());
            totactivity = totactivity + bsParseDouble(rowData[7].toString());
            rowData[6] = bsParseDouble(rowData[6].toString());
            rowData[7] = bsParseDouble(rowData[7].toString());
            rowData[8] = bsParseDouble(rowData[8].toString());
            if (rowData[2].toString().equals("A")) {
                totassets = totassets + bsParseDouble(rowData[8].toString());
            }
            if (rowData[2].toString().equals("L")) {
                totliables = totliables + bsParseDouble(rowData[8].toString());
            }
            if (rowData[2].toString().equals("O")) {
                totaloe = totaloe + bsParseDouble(rowData[8].toString());
            }
            if (cbzero.isSelected() && bsParseDouble(rowData[6].toString()) == 0 && bsParseDouble(rowData[7].toString()) == 0 && bsParseDouble(rowData[8].toString()) == 0) {
                     continue;
            }
            mymodel.addRow(rowData);    
            
        }
       }
        lblbegbal.setText(currformatDouble(totassets));
        lblactbal.setText(currformatDouble(totliables));
        lblendbal.setText(currformatDouble(totaloe));
        lblnet.setText(currformatDouble(totassets + totliables + totaloe ));
        
        }          
        roData = null;
    }   
    
    public String[] getBrowseViewDet(String[] x) {
      
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getAccountBalanceDetView"});
            list.add(new String[]{"param1", x[0]});
            list.add(new String[]{"param2", x[1]});
            list.add(new String[]{"param3", x[2]});
            list.add(new String[]{"param4", x[3]});
            list.add(new String[]{"param5", x[4]});
            list.add(new String[]{"param6", x[5]});
            try {
                jsonString = sendServerPost(list, "", null, "dataServFIN"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getDetail")};
            }
        } else {
            jsonString = getAccountBalanceDetView(x[0], x[1], x[2], bsParseInt(x[3]), bsParseInt(x[4])); 
        }        
        roData = jsonToData(jsonString);
        
        return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
      
    }
   
    public void done_getBrowseViewDet() {
      modeldetail.setNumRows(0);
      double total = 0.00;
         
       if (roData != null) {
        if (roData.length > 0) {
            for (Object[] rowData : roData) {
               total = total + bsParseDouble(rowData[7].toString());
               rowData[7] = bsParseDouble(rowData[7].toString());
                modeldetail.addRow(rowData);
            } 
            lblnet.setText(currformatDouble(total));
            this.repaint();
        }
       }
       
       roData = null;
    }
    
    public void lookUpFrameAcctDesc(String ddfield) {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getAcctBrowseUtil(luinput.getText(),0, "ac_id");
        } else {
         luModel = DTData.getAcctBrowseUtil(luinput.getText(), 0, "ac_desc");   
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
                if (ddfield.equals("from")) {
                    ddacctfrom.setSelectedItem(target.getValueAt(row,1).toString());
                    lblfromacct.setText(target.getValueAt(row,2).toString());
                } else {
                    ddacctto.setSelectedItem(target.getValueAt(row,1).toString());
                    lbltoacct.setText(target.getValueAt(row,2).toString()); 
                }
                    
                }
            }
        };
        luTable.addMouseListener(luml);
      
         
        callDialog(getGlobalColumnTag("id"), 
                getGlobalColumnTag("description")); 
        
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
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btdetail = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cbzero = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ddperiod = new javax.swing.JComboBox();
        ddyear = new javax.swing.JComboBox();
        ddacctto = new javax.swing.JComboBox();
        datelabel = new javax.swing.JLabel();
        ddacctfrom = new javax.swing.JComboBox();
        ddsite = new javax.swing.JComboBox();
        btprint = new javax.swing.JButton();
        ddendperiod = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        btLookUpAccountFrom = new javax.swing.JButton();
        lblfromacct = new javax.swing.JLabel();
        btLookUpAccountTo = new javax.swing.JButton();
        lbltoacct = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblactbal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblbegbal = new javax.swing.JLabel();
        lblendbal = new javax.swing.JLabel();
        EndBal = new javax.swing.JLabel();
        lblnet = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmaint"); // NOI18N

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        summarypanel.setLayout(new java.awt.BorderLayout());

        tablereport.setAutoCreateRowSorter(true);
        tablereport.setModel(new javax.swing.table.DefaultTableModel(
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
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

        summarypanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tablepanel.add(summarypanel);

        detailpanel.setLayout(new java.awt.BorderLayout());

        tabledetail.setAutoCreateRowSorter(true);
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
        jScrollPane2.setViewportView(tabledetail);

        detailpanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tablepanel.add(detailpanel);

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        jLabel4.setText("To Acct");
        jLabel4.setName("lbltoacct"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("Site");
        jLabel5.setName("lblsite"); // NOI18N

        jLabel1.setText("From Acct");
        jLabel1.setName("lblfromacct"); // NOI18N

        cbzero.setText("Supress Zeros");
        cbzero.setName("cbsuppresszeros"); // NOI18N

        jLabel3.setText("From Period");
        jLabel3.setName("lblfromperiod"); // NOI18N

        jLabel2.setText("Year");
        jLabel2.setName("lblyear"); // NOI18N

        ddperiod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        ddperiod.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddperiodItemStateChanged(evt);
            }
        });

        ddyear.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddyearItemStateChanged(evt);
            }
        });

        btprint.setText("Print/PDF");
        btprint.setName("btprintpdf"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        ddendperiod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        ddendperiod.setName("lbltoperiod"); // NOI18N

        jLabel10.setText("To Period");

        btLookUpAccountFrom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpAccountFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpAccountFromActionPerformed(evt);
            }
        });

        btLookUpAccountTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpAccountTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpAccountToActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddperiod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddendperiod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddacctfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(ddacctto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btLookUpAccountFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btLookUpAccountTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblfromacct, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5))
                            .addComponent(lbltoacct, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btRun)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdetail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btprint))
                            .addComponent(cbzero))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(datelabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(524, 524, 524))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblfromacct, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(ddacctfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(btRun)
                                .addComponent(btdetail)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(btprint)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddacctto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ddperiod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addComponent(lbltoacct, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbzero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btLookUpAccountFrom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btLookUpAccountTo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddendperiod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel8.setText("Total Liabilities");
        jLabel8.setName("lblactivity"); // NOI18N

        lblactbal.setText("0");

        jLabel7.setText("Total Assets");
        jLabel7.setName("lblbegbalance"); // NOI18N

        lblbegbal.setText("0");

        lblendbal.setBackground(new java.awt.Color(195, 129, 129));
        lblendbal.setText("0");

        EndBal.setText("Total Owner Equity");
        EndBal.setName("lblendbalance"); // NOI18N

        lblnet.setText("0");

        jLabel6.setText("Net Income / Loss");
        jLabel6.setName("lblaccountbalance"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(EndBal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblbegbal, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblactbal, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblendbal, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblnet, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblbegbal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblactbal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EndBal)
                    .addComponent(lblendbal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblnet, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
       executeTask("getBrowseView", null);
    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       lblnet.setText("");
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
           
                executeTask("getBrowseViewDet", new String[]{tablereport.getValueAt(row, 1).toString(),
                "", // cc
                tablereport.getValueAt(row, 5).toString(),
                ddyear.getSelectedItem().toString(),
                ddperiod.getSelectedItem().toString(),
                "false"});
           
            
            btdetail.setEnabled(true);
            detailpanel.setVisible(true);
              
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void ddyearItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddyearItemStateChanged
       if (! isLoad) {
        if (ddyear.getItemCount() > 0 && ddperiod.getItemCount() > 0) {
        ArrayList fromdatearray = fglData.getGLCalForPeriod(bsParseInt(ddyear.getSelectedItem().toString()), bsParseInt(ddperiod.getSelectedItem().toString()));
        if (fromdatearray.size() == 2)
        datelabel.setText(fromdatearray.get(0).toString() + " To " + fromdatearray.get(1).toString());
       }
       }
    }//GEN-LAST:event_ddyearItemStateChanged

    private void ddperiodItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddperiodItemStateChanged
        if (! isLoad) {
        if (ddperiod.getItemCount() > 0 && ddyear.getItemCount() > 0) {
        ArrayList fromdatearray = fglData.getGLCalForPeriod(bsParseInt(ddyear.getSelectedItem().toString()), bsParseInt(ddperiod.getSelectedItem().toString()));
        if (fromdatearray.size() == 2)
        datelabel.setText(fromdatearray.get(0).toString() + " To " + fromdatearray.get(1).toString());
        }
        }
    }//GEN-LAST:event_ddperiodItemStateChanged

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        OVData.printJTableToJasper("Account Balances Report", tablereport, "acctbalance.jasper", null );
    }//GEN-LAST:event_btprintActionPerformed

    private void btLookUpAccountFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAccountFromActionPerformed
        lookUpFrameAcctDesc("from");
    }//GEN-LAST:event_btLookUpAccountFromActionPerformed

    private void btLookUpAccountToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAccountToActionPerformed
        lookUpFrameAcctDesc("to");
    }//GEN-LAST:event_btLookUpAccountToActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EndBal;
    private javax.swing.JButton btLookUpAccountFrom;
    private javax.swing.JButton btLookUpAccountTo;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btprint;
    private javax.swing.JCheckBox cbzero;
    private javax.swing.JLabel datelabel;
    private javax.swing.JComboBox ddacctfrom;
    private javax.swing.JComboBox ddacctto;
    private javax.swing.JComboBox<String> ddendperiod;
    private javax.swing.JComboBox ddperiod;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddyear;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblactbal;
    private javax.swing.JLabel lblbegbal;
    private javax.swing.JLabel lblendbal;
    private javax.swing.JLabel lblfromacct;
    private javax.swing.JLabel lblnet;
    private javax.swing.JLabel lbltoacct;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    // End of variables declaration//GEN-END:variables
}
