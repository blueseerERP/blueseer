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
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.fgl.fglData.addUpdateGLICMeta;
import static com.blueseer.fgl.fglData.addUpdateGLICTransaction;
import static com.blueseer.fgl.fglData.deleteGLIC;
import static com.blueseer.fgl.fglData.deleteGLICMeta;
import static com.blueseer.fgl.fglData.getGLICAcctlist;
import static com.blueseer.fgl.fglData.getGLICDefElements;
import static com.blueseer.fgl.fglData.getGLICMetaValue;
import static com.blueseer.fgl.fglData.getGLIClist;
import com.blueseer.fgl.fglData.glic_accts;
import com.blueseer.fgl.fglData.glic_def;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ConvertBoolToYesNo;
import static com.blueseer.utl.BlueSeerUtils.boolToInt;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
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
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author vaughnte
 */
public class GLIncStmtDef extends javax.swing.JPanel  {

    DefaultListModel mymodel = new DefaultListModel() ;
    DefaultListModel mymodelex = new DefaultListModel() ;
    ArrayList<String[]> initDataSets = new ArrayList<>();
    String defaultSite = "";
    String defaultCurrency = "";
    boolean canUpdate = false;
    public static ArrayList<glic_def> xlist = null;
    public static ArrayList<glic_accts> ylist = null;
    public static LinkedHashMap<String, ArrayList<String>> acctsIn = new  LinkedHashMap<>();
    public static LinkedHashMap<String, ArrayList<String>> acctsOut = new  LinkedHashMap<>();
    String jasper = "";
    boolean isLoad = false;
    
     GLIncStmtDef.MyTableModel tablemodel = new GLIncStmtDef.MyTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("code"),
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("sequence"), 
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("start"),
                getGlobalColumnTag("end"),
                "summarize",
                "flip sign",
                "enabled",
                "suppress zeros DET",
                "suppress zeros SUM",
                "passive",
                "begbal",
                "activity",
                "endbal",
                "expression"
            });
    
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
       boolean[] canEdit = new boolean[]{
                false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
               canEdit = new boolean[]{false, false, false, false, false}; 
            return canEdit[columnIndex];
        }
   
        /*
        public Class getColumnClass(int column) {
               if (column == 6 || column == 7)       
                return Double.class; 
            else return String.class;  //other columns accept String values 
        }
       
        */
        
   }    
    
    
    /**
     * Creates new form GLIncStmtDef
     */
    public GLIncStmtDef() {
        initComponents();
        setLanguageTags(this);
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
    
    public void setComponentDefaultValues() {
       isLoad = true;
       initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "accounts");
       acctsIn.clear();
       acctsOut.clear();
       assignlist.setModel(mymodel);
       excludelist.setModel(mymodelex);
       tablereport.setModel(tablemodel);
       tablemodel.setNumRows(0);
       
        tbkey.setText("");
        tbdesc.setText("");
        tbjasper.setText("");
        ddcategory.removeAllItems();
        ddtype.setSelectedIndex(0);
        tbsequence.setText("");
        tbfrom.setText("");
        tbto.setText("");
        cbsummarize.setSelected(false);
        cbflipsign.setSelected(false);
        cbpassive.setSelected(false);
        cbbegbal.setSelected(false);
        cbactivity.setSelected(false);
        cbendbal.setSelected(false);
        tbexpression.setText("");
        assignlist.removeAll();
        excludelist.removeAll(); 
        ddacct.removeAllItems();
        
        
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("accounts")) {
              ddacct.addItem(s[1]); 
            }
        }
        
        
       isLoad = false;
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
           } else if (this.type.equals("get")) {
             updateForm();
             tbkey.requestFocus();
           } else {
             initvars(null);  
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
   
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        tbjasper.setText("genericstatement.jasper");
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
        } else {
                   tbkey.setForeground(Color.red); 
        }
    }
    
    public boolean validateInput(dbaction x) {
        if (! canUpdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"glic_def", "glic_accts"});
        int fc;

        fc = checkLength(f,"glic_profile");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }
        
        /*
        fc = checkLength(f,"glic_desc");
        if (tbdesc.getText().length() > fc || tbdesc.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbdesc.requestFocus();
            return false;
        }
         */       
        if (tablereport.getRowCount() < 1) {
            bsmf.MainFrame.show(getMessageTag(1062));
            ddcategory.requestFocus();
            return false;
        }
                
                
               
        return true;
    }
        
    public void initvars(String[] arg) {
       setPanelComponentState(this, false); 
       if (initDataSets != null) {
        setComponentDefaultValues();
       }
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
    
       
    public String[] addRecord(String[] key) {
         String[] m = addUpdateGLICTransaction(key[0], createRecord(), createRecordAccts());
         addUpdateGLICMeta("header", "jasper", key[0], tbjasper.getText());
         return m;
    }
        
    public String[] updateRecord(String[] key) {
         String[] m = addUpdateGLICTransaction(key[0], createRecord(), createRecordAccts());
         addUpdateGLICMeta("header", "jasper", key[0], tbjasper.getText());
         return m;
    }
    
    public String[] deleteRecord(String[] key) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteGLIC(key[0]); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
    }
    
    public String[] getRecord(String[] key) {
        ArrayList<glic_def> z = getGLIClist(key); 
        ArrayList<glic_accts> y = getGLICAcctlist(key);
        jasper = getGLICMetaValue("header", "jasper", key[0]);
        xlist = z;
        ylist = y;
        return xlist.get(0).m();
    }
    
    public void updateForm() {
        int i = 0;
        setAction(xlist.get(0).m());
        
        acctsIn.clear();
        acctsOut.clear();
        
        tbjasper.setText(jasper);
        
        for (glic_def glic : xlist) {
            if (i == 0) {
            tbkey.setText(glic.glic_profile());
            //tbfrom.setText(glic.glic_start());
           // tbto.setText(glic.glic_end());
           // tbdesc.setText(glic.glic_desc());
           // tbsequence.setText(String.valueOf(glic.glic_seq()));
           // ddtype.setSelectedItem(glic.glic_type());
           // cbsummarize.setSelected(BlueSeerUtils.ConvertStringToBool(glic.glic_summarize()));
           // cbflipsign.setSelected(BlueSeerUtils.ConvertStringToBool(glic.glic_flipsign()));
            }
            ddcategory.addItem(glic.glic_name());
            tablemodel.addRow(new Object[]{glic.glic_name(), 
                glic.glic_desc(),
                glic.glic_seq(),
                glic.glic_type(),
                glic.glic_start(),
                glic.glic_end(),
                glic.glic_summarize(),
                glic.glic_flipsign(),
                glic.glic_enabled(),
                glic.glic_suppzerodet(),
                glic.glic_suppzerosum(),
                glic.glic_passive(),
                glic.glic_begbal(),
                glic.glic_activity(),
                glic.glic_endbal(),
                glic.glic_expression()});
            tablereport.setModel(tablemodel);
        }
        
        for (glic_accts acct : ylist) {
            if (acct.glicd_type().equals("in")) {
                if (acctsIn.containsKey(acct.glicd_name())) {
                    ArrayList<String> lm = acctsIn.get(acct.glicd_name());
                    if (! lm.contains(acct.glicd_acct())) {
                        lm.add(acct.glicd_acct());
                    }
                    acctsIn.replace(acct.glicd_name(), lm);
                } else {
                    ArrayList<String> lm = new ArrayList<>();
                    lm.add(acct.glicd_acct());
                    acctsIn.put(acct.glicd_name(), lm);
                }
            }
            if (acct.glicd_type().equals("out")) {
                if (acctsOut.containsKey(acct.glicd_name())) {
                    ArrayList<String> lm = acctsOut.get(acct.glicd_name());
                    if (! lm.contains(acct.glicd_acct())) {
                        lm.add(acct.glicd_acct());
                    }
                    acctsOut.replace(acct.glicd_name(), lm);
                } else {
                    ArrayList<String> lm = new ArrayList<>();
                    lm.add(acct.glicd_acct());
                    acctsOut.put(acct.glicd_name(), lm);
                }
            }
        }
    }
    
    public ArrayList<glic_def> createRecord() { 
        ArrayList<glic_def> list = new ArrayList<>();
        for (int j = 0; j < tablereport.getRowCount(); j++) {
        glic_def x = new glic_def(null, 
                tbkey.getText(),
                tablereport.getValueAt( j, 0).toString(),
                tablereport.getValueAt( j, 1).toString(),
                bsParseInt(tablereport.getValueAt( j, 2).toString()), 
                tablereport.getValueAt( j, 3).toString(),
                tablereport.getValueAt( j, 4).toString(),
                tablereport.getValueAt( j, 5).toString(),
                tablereport.getValueAt( j, 6).toString(),
                tablereport.getValueAt( j, 7).toString(),
                tablereport.getValueAt( j, 8).toString(),
                tablereport.getValueAt( j, 9).toString(),
                tablereport.getValueAt( j, 10).toString(),
                tablereport.getValueAt( j, 11).toString(),
                tablereport.getValueAt( j, 12).toString(),
                tablereport.getValueAt( j, 13).toString(),
                tablereport.getValueAt( j, 14).toString(),
                tablereport.getValueAt( j, 15).toString()
                );
        list.add(x);
        }
        /* potential validation mechanism...would need association between record field and input field
        for(Field f : x.getClass().getDeclaredFields()){
        System.out.println(f.getName());
        }
        */
        return list;
    }
    
    public ArrayList<glic_accts> createRecordAccts() { 
        ArrayList<glic_accts> list = new ArrayList<>();
        
        for (Map.Entry<String, ArrayList<String>> val : acctsIn.entrySet()) {
            ArrayList<String> lm = val.getValue();
            for (String s : lm) {
                glic_accts x = new glic_accts(null, 
                    tbkey.getText(),
                    val.getKey(),
                    s,
                    0,
                    "in"
                    );
                list.add(x);
            }
        }
        
        for (Map.Entry<String, ArrayList<String>> val : acctsOut.entrySet()) {
            ArrayList<String> lm = val.getValue();
            for (String s : lm) {
                glic_accts x = new glic_accts(null, 
                    tbkey.getText(),
                    val.getKey(),
                    s,
                    0,
                    "out"
                    );
                list.add(x);
            }
        }
        
        /*
        for (int j = 0; j < mymodel.getSize(); j++) {
        glic_accts x = new glic_accts(null, 
                tbkey.getText(),
                ddcategory.getSelectedItem().toString(),
                mymodel.getElementAt(j).toString(),
                0,
                "in"
                );
        list.add(x);
        }
       for (int j = 0; j < mymodelex.getSize(); j++) {
        glic_accts x = new glic_accts(null, 
                tbkey.getText(),
                ddcategory.getSelectedItem().toString(),
                mymodelex.getElementAt(j).toString(),
                0,
                "out"
                );
        list.add(x);
        }
       */
       
        return list;
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getGLICBrowseUtil(luinput.getText(),0, "glic_profile");
        } else {
         luModel = DTData.getGLICBrowseUtil(luinput.getText(),0, "glic_name");   
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
                tbkey.setText(target.getValueAt(row,1).toString());
                /*
                ArrayList<String> cats = fglData.getGLICCategoryList(target.getValueAt(row,1).toString());
                isLoad = true;
                for (String cat : cats) {
                ddcategory.addItem(cat);
                }
                if (ddcategory.getItemCount() > 0) {
                    ddcategory.setSelectedIndex(0);
                }
                isLoad = false;
                */
                initvars(new String[]{target.getValueAt(row,1).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog("profile", "category"); 
        
    }

    public void lookUpFrameAcctDesc() {
        
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
                  ddacct.setSelectedItem(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
         
        callDialog(getGlobalColumnTag("id"), 
                getGlobalColumnTag("description")); 
        
    }
    
    public void clearAll() {
        tbkey.setText("");
        tbdesc.setText("");
        ddcategory.setSelectedIndex(0);
        ddtype.setSelectedIndex(0);
        tbsequence.setText("");
        tbfrom.setText("");
        tbto.setText("");
        cbsummarize.setSelected(false);
        cbflipsign.setSelected(false);
        assignlist.removeAll();
        excludelist.removeAll();
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
        btupdate = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btaddassign = new javax.swing.JButton();
        btdeleteexclude = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btaddexclude = new javax.swing.JButton();
        btdeleteassigned = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        excludelist = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        assignlist = new javax.swing.JList();
        acctname = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        ddacct = new javax.swing.JComboBox();
        btLookUpAccountFrom = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        tbdesc = new javax.swing.JTextField();
        cbflipsign = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        tbto = new javax.swing.JTextField();
        tbfrom = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tbsequence = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btaddcat = new javax.swing.JButton();
        ddcategory = new javax.swing.JComboBox();
        ddtype = new javax.swing.JComboBox<>();
        cbsummarize = new javax.swing.JCheckBox();
        cbsuppzerodet = new javax.swing.JCheckBox();
        cbsuppzerosum = new javax.swing.JCheckBox();
        cbenabled = new javax.swing.JCheckBox();
        cbpassive = new javax.swing.JCheckBox();
        cbbegbal = new javax.swing.JCheckBox();
        cbactivity = new javax.swing.JCheckBox();
        cbendbal = new javax.swing.JCheckBox();
        tbexpression = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        btnew = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btaddcategory = new javax.swing.JButton();
        btdeletecategory = new javax.swing.JButton();
        btupdatecategory = new javax.swing.JButton();
        tbjasper = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        btcopy = new javax.swing.JButton();
        btsequence = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel2.setText("Profile");

        jLabel3.setText("Account:");
        jLabel3.setName("lblaccounts"); // NOI18N

        btaddassign.setText("Add");
        btaddassign.setName("btadd"); // NOI18N
        btaddassign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddassignActionPerformed(evt);
            }
        });

        btdeleteexclude.setText("Delete");
        btdeleteexclude.setName("btdelete"); // NOI18N
        btdeleteexclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteexcludeActionPerformed(evt);
            }
        });

        jLabel4.setText("Excluded:");
        jLabel4.setName("lblexcluded"); // NOI18N

        btaddexclude.setText("Add");
        btaddexclude.setName("btadd"); // NOI18N
        btaddexclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddexcludeActionPerformed(evt);
            }
        });

        btdeleteassigned.setText("Delete");
        btdeleteassigned.setName("btdelete"); // NOI18N
        btdeleteassigned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteassignedActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(excludelist);

        jScrollPane2.setViewportView(assignlist);

        jLabel7.setText("Assigned:");
        jLabel7.setName("lblassigned"); // NOI18N

        ddacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddacctActionPerformed(evt);
            }
        });

        btLookUpAccountFrom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpAccountFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpAccountFromActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btdeleteexclude)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaddexclude))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btdeleteassigned)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddassign)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btLookUpAccountFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(acctname, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(acctname, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btLookUpAccountFrom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddassign)
                    .addComponent(btdeleteassigned))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddexclude)
                    .addComponent(btdeleteexclude))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        btclear.setText("Clear");

        cbflipsign.setText("Flip Sign");

        jLabel5.setText("From");
        jLabel5.setName("lblfrom"); // NOI18N

        jLabel1.setText("Code");
        jLabel1.setName("lblcode"); // NOI18N

        jLabel6.setText("To");
        jLabel6.setName("lblto"); // NOI18N

        jLabel9.setText("Sequence");

        jLabel10.setText("Type");

        jLabel8.setText("Description");

        btaddcat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddcat.setToolTipText("Add Category");
        btaddcat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddcatActionPerformed(evt);
            }
        });

        ddcategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcategoryActionPerformed(evt);
            }
        });

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "header", "detail", "expression", "summation", "spacer", "dashline", "groupstart", "groupend", "variable" }));

        cbsummarize.setText("Summarize");

        cbsuppzerodet.setText("Suppress Detail Zeros");

        cbsuppzerosum.setText("Suppress Summation Zero");

        cbenabled.setText("Enabled");

        cbpassive.setText("Passive");

        cbbegbal.setText("BegBal");

        cbactivity.setText("Activity");

        cbendbal.setText("EndBal");

        jLabel11.setText("Expression");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbbegbal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbactivity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbendbal))
                    .addComponent(cbsuppzerosum)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ddcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaddcat, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tbfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cbsummarize)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbflipsign))
                            .addComponent(cbsuppzerodet))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbpassive)
                            .addComponent(cbenabled)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(tbexpression, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbdesc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)))
                .addGap(25, 25, 25))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddcategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(btaddcat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbexpression, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbsummarize)
                    .addComponent(cbflipsign)
                    .addComponent(cbenabled))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbsuppzerodet)
                    .addComponent(cbpassive))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbsuppzerosum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbbegbal)
                    .addComponent(cbactivity)
                    .addComponent(cbendbal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btadd.setText("add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btdelete.setText("delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btaddcategory.setText("Add Category");
        btaddcategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddcategoryActionPerformed(evt);
            }
        });

        btdeletecategory.setText("Delete Category");
        btdeletecategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletecategoryActionPerformed(evt);
            }
        });

        btupdatecategory.setText("Update Category");
        btupdatecategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdatecategoryActionPerformed(evt);
            }
        });

        jLabel12.setText("Jasper File");

        btcopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addfile.png"))); // NOI18N
        btcopy.setToolTipText("Copy");
        btcopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcopyActionPerformed(evt);
            }
        });

        btsequence.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map.png"))); // NOI18N
        btsequence.setToolTipText("Copy");
        btsequence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsequenceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(47, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbjasper))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btcopy)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(152, 152, 152)
                .addComponent(btdeletecategory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btaddcategory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdatecategory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btsequence)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addComponent(btlookup)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btclear)
                                .addComponent(btnew)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbjasper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)))
                    .addComponent(btcopy))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btaddcategory)
                            .addComponent(btdeletecategory)
                            .addComponent(btupdatecategory))
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btupdate)
                            .addComponent(btadd)
                            .addComponent(btdelete)))
                    .addComponent(btsequence))
                .addGap(24, 24, 24))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void ddcategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcategoryActionPerformed
       /*
        if (! isLoad && ddcategory.getItemCount() > 0) {
        mymodelex.removeAllElements();
        mymodel.removeAllElements();
        String[] x = getGLICDefElements(tbkey.getText(), ddcategory.getSelectedItem().toString());
        if (x != null) {
        tbfrom.setText(x[5]);
        tbto.setText(x[6]);
        tbdesc.setText(x[2]);
        tbsequence.setText(x[3]);
        ddtype.setSelectedItem(x[4]);
        cbsummarize.setSelected(BlueSeerUtils.ConvertStringToBool(x[7]));
        cbflipsign.setSelected(BlueSeerUtils.ConvertStringToBool(x[8]));
        }
        ArrayList mylistin = fglData.getGLICAccts(ddcategory.getSelectedItem().toString(),"in");
        for (int i = 0; i < mylistin.size(); i++) {
            mymodel.addElement(mylistin.get(i));
        }
        
        ArrayList mylist = fglData.getGLICAccts(ddcategory.getSelectedItem().toString(),"out");
        for (int i = 0; i < mylist.size(); i++) {
            mymodelex.addElement(mylist.get(i));
        }
       }
       */
    }//GEN-LAST:event_ddcategoryActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
       if (! isLoad) {
        acctname.setText(fglData.getGLAcctDesc(ddacct.getSelectedItem().toString()));
       }
    }//GEN-LAST:event_ddacctActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()}); 
    }//GEN-LAST:event_btupdateActionPerformed

    private void btaddassignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddassignActionPerformed
        mymodel.addElement(ddacct.getSelectedItem().toString());
    }//GEN-LAST:event_btaddassignActionPerformed

    private void btdeleteassignedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteassignedActionPerformed
         mymodel.removeElement(assignlist.getSelectedValue());
    }//GEN-LAST:event_btdeleteassignedActionPerformed

    private void btaddexcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddexcludeActionPerformed
       mymodelex.addElement(ddacct.getSelectedItem().toString());
    }//GEN-LAST:event_btaddexcludeActionPerformed

    private void btdeleteexcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteexcludeActionPerformed
        mymodelex.removeElement(excludelist.getSelectedValue());
    }//GEN-LAST:event_btdeleteexcludeActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btaddcatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddcatActionPerformed
        if (! tbkey.getText().isBlank()) {
            String input = bsmf.MainFrame.input("Enter new category");
            boolean proceed = true;
            for (int i = 0; i < ddcategory.getItemCount(); i++) {
                  if (ddcategory.getItemAt(i).toString().toLowerCase().equals(input.toLowerCase())) {
                     proceed = false;
                   }
            }
            if (proceed) {
            addUpdateGLICMeta("glic", "category", tbkey.getText(), input);
            ddcategory.addItem(input);
            ddcategory.setSelectedItem(input);
            ddcategory.requestFocus();
            }
        }
    }//GEN-LAST:event_btaddcatActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(dbaction.add)) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput(dbaction.delete)) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});

    }//GEN-LAST:event_btdeleteActionPerformed

    private void btaddcategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddcategoryActionPerformed
        tablemodel.addRow(new Object[]{ddcategory.getSelectedItem().toString(), 
                tbdesc.getText(),
                tbsequence.getText(),
                ddtype.getSelectedItem().toString(),
                tbfrom.getText(),
                tbto.getText(),
                BlueSeerUtils.boolToInt(cbsummarize.isSelected()),
                BlueSeerUtils.boolToInt(cbflipsign.isSelected()),
                BlueSeerUtils.boolToInt(cbenabled.isSelected()),
                BlueSeerUtils.boolToInt(cbsuppzerodet.isSelected()),
                BlueSeerUtils.boolToInt(cbsuppzerosum.isSelected()),
                BlueSeerUtils.boolToInt(cbpassive.isSelected()),
                BlueSeerUtils.boolToInt(cbbegbal.isSelected()),
                BlueSeerUtils.boolToInt(cbactivity.isSelected()),
                BlueSeerUtils.boolToInt(cbendbal.isSelected()),
                tbexpression.getText()});
        
               
        
        tbdesc.setText("");
        tbsequence.setText("");
        ddtype.setSelectedIndex(0);
        tbfrom.setText("");
        tbto.setText("");
        tbexpression.setText("");
        cbsummarize.setSelected(false);
        cbflipsign.setSelected(false);
        cbenabled.setSelected(false);
        cbsuppzerodet.setSelected(false);
        cbsuppzerosum.setSelected(false);
        cbpassive.setSelected(false);
        cbbegbal.setSelected(false);
        cbactivity.setSelected(false);
        cbendbal.setSelected(false);
        
        ArrayList<String> lm = new ArrayList<>();
          for (int j = 0; j < mymodel.getSize(); j++) {
              lm.add(mymodel.getElementAt(j).toString());
          }
          acctsIn.put(ddcategory.getSelectedItem().toString(), lm);
          
          lm = new ArrayList<>();
          for (int j = 0; j < mymodelex.getSize(); j++) {
              lm.add(mymodelex.getElementAt(j).toString());
          }
          acctsOut.put(ddcategory.getSelectedItem().toString(), lm);
        
    }//GEN-LAST:event_btaddcategoryActionPerformed

    private void btdeletecategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletecategoryActionPerformed
        int[] rows = tablereport.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031, String.valueOf(i)));
            String category = tablereport.getValueAt(i, 0).toString();
            if (acctsIn.containsKey(category)) {
              acctsIn.remove(category);
            }
            if (acctsOut.containsKey(category)) {
                acctsOut.remove(category);
            }
            ((javax.swing.table.DefaultTableModel) tablereport.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btdeletecategoryActionPerformed

    private void btupdatecategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdatecategoryActionPerformed
        int[] rows = tablereport.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
                return;
        }
        int trow = 0;
        for (int i : rows) {
                trow = i;
                tablereport.setValueAt(tbdesc.getText(), i, 1);
                tablereport.setValueAt(tbsequence.getText(), i, 2);
                tablereport.setValueAt(ddtype.getSelectedItem().toString(), i, 3);
                tablereport.setValueAt(tbfrom.getText(), i, 4);
                tablereport.setValueAt(tbto.getText(), i, 5);
                tablereport.setValueAt(boolToInt(cbsummarize.isSelected()), i, 6);
                tablereport.setValueAt(boolToInt(cbflipsign.isSelected()), i, 7);
                tablereport.setValueAt(boolToInt(cbenabled.isSelected()), i, 8);
                tablereport.setValueAt(boolToInt(cbsuppzerodet.isSelected()), i, 9);
                tablereport.setValueAt(boolToInt(cbsuppzerosum.isSelected()), i, 10);
                tablereport.setValueAt(boolToInt(cbpassive.isSelected()), i, 11);
                tablereport.setValueAt(boolToInt(cbbegbal.isSelected()), i, 12);
                tablereport.setValueAt(boolToInt(cbactivity.isSelected()), i, 13);
                tablereport.setValueAt(boolToInt(cbendbal.isSelected()), i, 14);
                tablereport.setValueAt(tbexpression.getText(), i, 15);
        }
        
        // loop through model and adjust sequence line if new sequence is inserted
        for (int j = 0; j < tablereport.getRowCount(); j++) {
            if (j > trow && bsParseInt(tablereport.getValueAt(j, 2).toString()) >= bsParseInt(tbsequence.getText())) {
              int k = bsParseInt(tablereport.getValueAt(j, 2).toString());
              tablereport.setValueAt(k + 1, j, 2);  
            }
        }
        
        // update in/out list of accounts per ddcategory
          // first delete from in/out list in linkedhashmap
          if (acctsIn.containsKey(ddcategory.getSelectedItem().toString())) {
              acctsIn.remove(ddcategory.getSelectedItem().toString());
          }
          if (acctsOut.containsKey(ddcategory.getSelectedItem().toString())) {
              acctsOut.remove(ddcategory.getSelectedItem().toString());
          }
          
          ArrayList<String> lm = new ArrayList<>();
          for (int j = 0; j < mymodel.getSize(); j++) {
              lm.add(mymodel.getElementAt(j).toString());
          }
          acctsIn.put(ddcategory.getSelectedItem().toString(), lm);
          
          
          ArrayList<String> lmex = new ArrayList<>();
          for (int j = 0; j < mymodelex.getSize(); j++) {
              lmex.add(mymodelex.getElementAt(j).toString());
          }
          acctsOut.put(ddcategory.getSelectedItem().toString(), lmex);
          
        
        
    }//GEN-LAST:event_btupdatecategoryActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        // element, percent, type, enabled
        ddcategory.setSelectedItem(tablereport.getValueAt(row, 0).toString());
        tbdesc.setText(tablereport.getValueAt(row, 1).toString());
        tbsequence.setText(tablereport.getValueAt(row, 2).toString());
        ddtype.setSelectedItem(tablereport.getValueAt(row, 3).toString());
        tbfrom.setText(tablereport.getValueAt(row, 4).toString());
        tbto.setText(tablereport.getValueAt(row, 5).toString());
        cbsummarize.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 6).toString()));
        cbflipsign.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 7).toString()));
        cbenabled.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 8).toString()));
        cbsuppzerodet.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 9).toString()));
        cbsuppzerosum.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 10).toString()));
        cbpassive.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 11).toString()));
        cbbegbal.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 12).toString()));
        cbactivity.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 13).toString()));
        cbendbal.setSelected(BlueSeerUtils.ConvertStringToBool(tablereport.getValueAt(row, 14).toString()));
        tbexpression.setText(tablereport.getValueAt(row, 15).toString());
        mymodel.removeAllElements();
        mymodelex.removeAllElements();
        ArrayList<String> lm = acctsIn.get(ddcategory.getSelectedItem().toString());
        if (lm != null) {
            for (String s : lm) {
                mymodel.addElement(s);
            }
        }
        lm = acctsOut.get(ddcategory.getSelectedItem().toString());
        if (lm != null) {
            for (String s : lm) {
                mymodelex.addElement(s);
            }
        }
        
    }//GEN-LAST:event_tablereportMouseClicked

    private void btLookUpAccountFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAccountFromActionPerformed
        lookUpFrameAcctDesc();
    }//GEN-LAST:event_btLookUpAccountFromActionPerformed

    private void btcopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcopyActionPerformed
        if (! isLoad) {
            tbkey.setText("");
            tbkey.setBackground(Color.yellow);
            //bsmf.MainFrame.show("choose new parent key and adjust ISA/GS IDs accordingly");
            tbkey.requestFocus();
            btadd.setEnabled(true);
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            btdelete.setEnabled(false);
            btupdate.setEnabled(false);
            btcopy.setEnabled(false);
          //  for (int j = 0; j < tablereport.getRowCount(); j++) {
          //      tablereport.setValueAt(0, j, 10);
          //  }

        }
    }//GEN-LAST:event_btcopyActionPerformed

    private void btsequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsequenceActionPerformed
            for (int j = 0; j < tablereport.getRowCount(); j++) {
                tablereport.setValueAt(j, j, 2);
            }
    }//GEN-LAST:event_btsequenceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel acctname;
    private javax.swing.JList assignlist;
    private javax.swing.JButton btLookUpAccountFrom;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddassign;
    private javax.swing.JButton btaddcat;
    private javax.swing.JButton btaddcategory;
    private javax.swing.JButton btaddexclude;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcopy;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteassigned;
    private javax.swing.JButton btdeletecategory;
    private javax.swing.JButton btdeleteexclude;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btsequence;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdatecategory;
    private javax.swing.JCheckBox cbactivity;
    private javax.swing.JCheckBox cbbegbal;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JCheckBox cbendbal;
    private javax.swing.JCheckBox cbflipsign;
    private javax.swing.JCheckBox cbpassive;
    private javax.swing.JCheckBox cbsummarize;
    private javax.swing.JCheckBox cbsuppzerodet;
    private javax.swing.JCheckBox cbsuppzerosum;
    private javax.swing.JComboBox ddacct;
    private javax.swing.JComboBox ddcategory;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JList excludelist;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbexpression;
    private javax.swing.JTextField tbfrom;
    private javax.swing.JTextField tbjasper;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbsequence;
    private javax.swing.JTextField tbto;
    // End of variables declaration//GEN-END:variables
}
