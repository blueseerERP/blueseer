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

package com.blueseer.prd;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
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
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.inv.ItemBrowse;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.getTranMstr;
import static com.blueseer.inv.invData.getTranMstrBySerial;
import com.blueseer.inv.invData.tran_mstr;
import static com.blueseer.prd.prdData.getSerialBrowseViewDet;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.blueseer.vdr.venData;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;

import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author vaughnte
 */
public class SerialBrowse extends javax.swing.JPanel {
    public String rsData; 
    Object[][] roData;
    ArrayList<String[]> initDataSets = new ArrayList<>();
    String defaultSite = "";
    String defaultCurrency = "";
    
     public Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
     
    javax.swing.table.DefaultTableModel mymodel = new SerialBrowse.MyTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("select"),
                            getGlobalColumnTag("detail"),
                            getGlobalColumnTag("id"), 
                            getGlobalColumnTag("serial"),
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("qty"), 
                            getGlobalColumnTag("uom"), 
                            getGlobalColumnTag("effectivedate"), 
                            getGlobalColumnTag("timestamp"), 
                            getGlobalColumnTag("lot") 
                            })
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 1)       
                            return ImageIcon.class;  
                        else if (col == 6)
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
    
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("select"),
                            getGlobalColumnTag("id"), 
                            getGlobalColumnTag("serial"),
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("qty"), 
                            getGlobalColumnTag("uom"), 
                            getGlobalColumnTag("effectivedate"), 
                            getGlobalColumnTag("timestamp")})
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else if (col == 5)
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };;
    
    class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
          //  if (col == 2 || col == 3 || col == 4)       
          //      return Double.class;  
          //  else return String.class;  //other columns accept String values  
              return String.class;
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
        }    
    
    
    class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
               
        return c;
    }
    }
    

    
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public SerialBrowse() {
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
                    message = getBrowseViewDet(key[0]);
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
     
    public void showTranMstr(String key, String bytype) {
        javax.swing.JTextArea ta = new javax.swing.JTextArea();
        
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tran_mstr tm = null;
        if (bytype.equals("id")) {
         tm = getTranMstr(key); // lot should be blank for specific tran_mstr other than association
        } else {
         tm = getTranMstrBySerial(key, ""); // lot should be blank for specific tran_mstr other than association   
        }
        if (tm == null) {
            return;
        }
        Field[] xfs = tm.getClass().getDeclaredFields();
        
        LinkedHashMap<String, String> hm = new LinkedHashMap<String, String>();
        for (Field f : xfs) {
            f.setAccessible(true);
            if (f.getName().equals("m")) {
                continue;
            }
            try {
                if (f.get(tm) != null) {
                    hm.put(f.getName(), f.get(tm).toString());
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                bslog(ex);
            }
        }
        ta.setText("  " + "\n\n");
       
        ta.append("Serial:\t\t" + hm.get("tr_serial") + "  \n");
        ta.append("Item:\t\t" + hm.get("tr_item") + "  \n");
        ta.append("Quantity:\t\t" + hm.get("tr_qty") + "  \n");
        ta.append("UOM:\t\t" + hm.get("tr_uom") + "  \n");
        ta.append("Type:\t\t" + hm.get("tr_type") + "  \n");
        ta.append("Date:\t\t" + hm.get("tr_eff_date") + "  \n");
        ta.append("Time:\t\t" + hm.get("tr_timestamp") + "  \n");
        ta.append("Warehouse:\t\t" + hm.get("tr_wh") + "  \n");
        ta.append("Location:\t\t" + hm.get("tr_loc") + "  \n");
        ta.append("user:\t\t" + hm.get("tr_userid") + "  \n");
        ta.append("Entity:\t\t" + hm.get("tr_addrcode") + "  \n");
        ta.append("KeyNumber:\t\t" + hm.get("tr_nbr") + "  \n");
        ta.append("Order:\t\t" + hm.get("tr_order") + "  \n");
        ta.append("Line:\t\t" + hm.get("tr_line") + "  \n");
        ta.append("Program:\t\t" + hm.get("tr_program") + "  \n");
        
        ta.setCaretPosition(0);
        ta.setEditable(false);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Source Transaction View by " + bytype + ": " + key);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints panelGBC = new GridBagConstraints();

        panelGBC.weightx = 1;                    //I want to fill whole panel with JTextArea
        panelGBC.weighty = 1;                    //so both weights =1
        panelGBC.fill = GridBagConstraints.BOTH; //and fill is set to BOTH
        
        panel.add(scroll, panelGBC);
        dialog.add(panel);
        dialog.setPreferredSize(new Dimension(500, 400));
        dialog.pack();
        dialog.setLocationRelativeTo( null );
        dialog.setResizable(false);
        dialog.setVisible(true);
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
                 // start reset background colors
                if (component instanceof JTextField) {
                    if (((JTextField) component).isEditable()) {
                     component.setBackground(Color.WHITE);
                    } else {
                     component.setBackground(bsmf.MainFrame.nonEditableColor);   
                    }
                }
                if (component instanceof JComboBox) {
                     component.setBackground(bsmf.MainFrame.ddbgcolor);
                }
                // end reset background colors
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
    
    public void initvars(String[] arg) {
     executeTask("dataInit", null);
    }
    
    public String[] getInitialization() {
        initDataSets = invData.getInvMaintInit_min(this.getClass().getName(), bsmf.MainFrame.userid);
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }  
    
    public void done_Initialization() {
        setPanelComponentState(this, true);
        java.util.Date now = new java.util.Date();
        dcfrom.setDate(now);
        dcto.setDate(now);
        tbserialfrom.setText("");
        tbserialto.setText("");
        
        
        mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldetail);
        
        tablereport.getColumnModel().getColumn(0).setMaxWidth(50);
        tablereport.getColumnModel().getColumn(1).setMaxWidth(50);
        btdetail.setEnabled(false);
        detailpanel.setVisible(false);
        
        ddsite.removeAllItems();
        ddtype.removeAllItems();
       
        
        
        ddfromitem.removeAllItems();
        ddtoitem.removeAllItems();
        
        
        for (String[] s : initDataSets) {
            
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultSite = s[1]; 
            }
            if (s[0].equals("trantype")) {
              ddtype.addItem(s[1]); 
            }
            if (s[0].equals("currency")) {
              defaultCurrency = s[1]; 
            }
            if (s[0].equals("items")) {
              ddfromitem.addItem(s[1]); 
              ddtoitem.addItem(s[1]);
            }
        }
        if (ddsite.getItemCount() > 0) {
            ddsite.setSelectedItem(defaultSite);
        }
        ddfromitem.insertItemAt("", 0);
        ddtoitem.insertItemAt("", 0);
        ddfromitem.setSelectedIndex(0);
        ddtoitem.setSelectedIndex(ddtoitem.getItemCount() - 1);
        ddtype.insertItemAt("ALL", 0);
        ddtype.setSelectedIndex(0);
    }
    
    public String[] getBrowseView() {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String fromitem;
        String toitem;
        String fromserial = "";
        String toserial = "";
         if (! tbserialfrom.getText().isBlank()) {
             fromserial = tbserialfrom.getText();
         } else {
             fromserial = bsmf.MainFrame.lowchar;
         }
         if (! tbserialto.getText().isBlank()) {
             toserial = tbserialto.getText();
         } else {
             toserial = bsmf.MainFrame.hichar;
         }
        if (ddfromitem.getSelectedItem() == null || ddfromitem.getSelectedItem().toString().isEmpty()) {
                    fromitem = bsmf.MainFrame.lowchar;
        } else {
            fromitem = ddfromitem.getSelectedItem().toString();
        }
         if (ddtoitem.getSelectedItem() == null || ddtoitem.getSelectedItem().toString().isEmpty()) {
            toitem = bsmf.MainFrame.hichar;
        } else {
            toitem = ddtoitem.getSelectedItem().toString();
        }
        
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getSerialBrowseView"});
        list.add(new String[]{"fromdate",dfdate.format(dcfrom.getDate())});
        list.add(new String[]{"todate",dfdate.format(dcto.getDate())});
        list.add(new String[]{"fromserial",fromserial});
        list.add(new String[]{"toserial",toserial});
        list.add(new String[]{"fromitem",fromitem});
        list.add(new String[]{"toitem",toitem});
        list.add(new String[]{"type",ddtype.getSelectedItem().toString()});
        
        try {
                jsonString = sendServerPost(list, "", null, "dataServPRD"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getSerialBrowseView")};
            }
        } else {
            jsonString = prdData.getSerialBrowseView(new String[]{
                dfdate.format(dcfrom.getDate()),
                dfdate.format(dcto.getDate()),
                fromserial, 
                toserial, 
                fromitem,
                toitem,
                ddtype.getSelectedItem().toString()
            });
        }
      
      if (jsonString == null) {
          return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getSerialBrowseView return jsonString is null")};
      }
        
      roData = jsonToData(jsonString);
       
      return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
    }

    public void done_getBrowseView() {
        setPanelComponentState(this, true);
        int i = 0;
        mymodel.setNumRows(0);
        if (roData != null) {
        for (Object[] rowData : roData) {
            if (roData[i][3].toString().isBlank()) {
                        continue;
            }
            roData[i][6] = bsParseDouble(rowData[6].toString());
            i++;
            mymodel.addRow(rowData);
        }
        
        }          
        roData = null;
    }   
    
    public String[] getBrowseViewDet(String serial) {
      
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getSerialBrowseViewDet"});
            list.add(new String[]{"param1", serial});
            try {
                jsonString = sendServerPost(list, "", null, "dataServPRD"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getDetail")};
            }
        } else {
            jsonString = getSerialBrowseViewDet(serial); 
        }        
        roData = jsonToData(jsonString);
        
        return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
      
    }
   
    public void done_getBrowseViewDet() {
      modeldetail.setNumRows(0);
       //  double totalsales = 0;
      //   double totalqty = 0;
         
       if (roData != null) {
        if (roData.length > 0) {
            for (Object[] rowData : roData) {
               // totalsales = totalsales + (bsParseDouble(rowData[6].toString()) * bsParseDouble(rowData[7].toString()));
               // totalqty = totalqty + bsParseDouble(rowData[6].toString());
                modeldetail.addRow(rowData);
            } 
        }
       }
       roData = null;
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
        jLabel3 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        tbserialfrom = new javax.swing.JTextField();
        tbserialto = new javax.swing.JTextField();
        btprint = new javax.swing.JButton();
        tbcsv = new javax.swing.JButton();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        ddfromitem = new javax.swing.JComboBox<>();
        ddtoitem = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Serial Transaction Browse"));
        jPanel1.setName("panelmain"); // NOI18N

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
        tabledetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabledetailMouseClicked(evt);
            }
        });
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

        jLabel4.setText("To Date");
        jLabel4.setName("lbltodate"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("Site");
        jLabel5.setName("lblsite"); // NOI18N

        jLabel1.setText("From Date");
        jLabel1.setName("lblfromdate"); // NOI18N

        jLabel3.setText("To Serial");
        jLabel3.setName("lbltoserial"); // NOI18N

        jLabel6.setText("From Serial");
        jLabel6.setName("lblfromserial"); // NOI18N

        btprint.setText("Print/PDF");
        btprint.setName("btprintpdf"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        tbcsv.setText("CSV");
        tbcsv.setName("btcsv"); // NOI18N
        tbcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcsvActionPerformed(evt);
            }
        });

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel2.setText("From Item");

        jLabel7.setText("To Item");

        jLabel8.setText("Type");

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tbserialto, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    .addComponent(tbserialfrom))
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ddtoitem, 0, 137, Short.MAX_VALUE)
                    .addComponent(ddfromitem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8)))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddsite, 0, 95, Short.MAX_VALUE)
                    .addComponent(ddtype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btclear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdetail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbcsv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprint)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(tbserialfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(btprint)
                        .addComponent(tbcsv)
                        .addComponent(jLabel2)
                        .addComponent(btclear)
                        .addComponent(ddfromitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(tbserialto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(ddtoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 277, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
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
        mymodel.setNumRows(0);
        setPanelComponentState(this, false);
        executeTask("getBrowseView", null);
    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 1) {
                executeTask("getBrowseViewDet", new String[]{tablereport.getValueAt(row, 3).toString()});
                //getdetail(tablereport.getValueAt(row, 3).toString()); // serialnumber
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
              
        }
        
        if ( col == 0) {
               showTranMstr(tablereport.getValueAt(row, 2).toString(), "id"); // serialnumber
              
        }
        
    }//GEN-LAST:event_tablereportMouseClicked

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        OVData.printJTableToJasper("Receiver Report", tablereport, "genericJTableL7.jasper", null );
    }//GEN-LAST:event_btprintActionPerformed

    private void tbcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcsvActionPerformed
        if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_tbcsvActionPerformed

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
        if ( col == 0) {
                showTranMstr(tabledetail.getValueAt(row, 2).toString(), "serial"); // serialnumber
        }
    }//GEN-LAST:event_tabledetailMouseClicked

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        java.util.Date now = new java.util.Date();
        dcfrom.setDate(now);
        dcto.setDate(now);
        tbserialfrom.setText("");
        tbserialto.setText("");
        ddtype.setSelectedIndex(0);
        ddfromitem.setSelectedIndex(0);
        ddtoitem.setSelectedIndex(ddtoitem.getItemCount() - 1);
        mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
    }//GEN-LAST:event_btclearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btprint;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddfromitem;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox<String> ddtoitem;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JButton tbcsv;
    private javax.swing.JTextField tbserialfrom;
    private javax.swing.JTextField tbserialto;
    // End of variables declaration//GEN-END:variables
}
