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

import com.blueseer.shp.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import com.blueseer.fgl.fglData;
import static com.blueseer.fgl.fglData.exportInvoiceCSV;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.OVData.getSystemJasperDirectory;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.ListOfArrayDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author vaughnte
 */
public class InvoiceBrowse extends javax.swing.JPanel {
 
    boolean sending = false;
    Object[][] rData;
    ArrayList<String[]> initDataSets = new ArrayList<>();
    String defaultcurrency = "";
   
    javax.swing.table.DefaultTableModel modeltable = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), 
                            getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("invoice"), 
                            getGlobalColumnTag("po"),
                            getGlobalColumnTag("site"),
                            getGlobalColumnTag("code"),
                            getGlobalColumnTag("name"),
                            getGlobalColumnTag("shipdate"), 
                            getGlobalColumnTag("invdate"), 
                            getGlobalColumnTag("status"), 
                            getGlobalColumnTag("amount"), 
                            getGlobalColumnTag("open"), 
                            getGlobalColumnTag("print"), 
                            getGlobalColumnTag("email")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 1 || col == 12 || col == 13)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                      @Override
                      public boolean isCellEditable(int row, int column) {
                            return false;
                            //Only the first column
                            // return column == 1;
                      }
                        };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("shipper"), 
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("custitem"), 
                            getGlobalColumnTag("order"), 
                            getGlobalColumnTag("line"), 
                            getGlobalColumnTag("po"), 
                            getGlobalColumnTag("qty"), 
                            getGlobalColumnTag("price")});
    
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
    
   
 class SomeRenderer extends DefaultTableCellRenderer {
         
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
            boolean issched = (Boolean) tablereport.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
            if (( column == 5 || column == 6) && ! issched ) {
            c.setBackground(Color.green);
            c.setForeground(Color.BLACK);
            
            }
            else {
                c.setBackground(table.getBackground());
            }
            return c;
    }
    }
    
    
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public InvoiceBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
          String type = "";
          String[] key = null; 
          /*
          String key = "";
          int row = 0;
          String site = "";
          */
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            
            switch(this.type) {
                case "init":
                    message = getInitialization();
                    break;
                    
                case "run":
                    if (this.key[0].equals("getInvoiceBrowseView")) {
                      message = getInvoiceBrowseView();
                    } 
                    if (this.key[0].equals("getInvoiceBrowseDetail")) {
                       message = getDetail(this.key[1]);  
                    }
                    if (this.key[0].equals("sendInvoice")) {
                       String rfile = OVData.printInvoiceRemote(this.key[1], "shipper", false);
                        if (rfile != null && !rfile.isBlank()) {
                        message = OVData.sendInvoice(this.key[1], this.key[3], rfile);
                        }
                    }
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
            if (this.type.equals("init")) {
                    done_Initialization();
            }
            if (this.type.equals("run")) {
                    if (this.key != null && this.key[0].equals("getInvoiceBrowseView")) {
                      done_getInvoiceBrowseView();
                    } 
                    if (this.key != null && this.key[0].equals("getInvoiceBrowseDetail")) {
                      done_getDetail(); 
                    }
            } 
            if (this.key != null && this.key[0].equals("sendInvoice")) {
                sending = false;
                tablereport.getModel().setValueAt(BlueSeerUtils.clickmail,Integer.parseInt(this.key[2]),11);  
            }
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"", getMessageTag(1189)});
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
    
    public String[] getInitialization() {
        initDataSets = shpData.getShipperInit(this.getClass().getName(), bsmf.MainFrame.userid);
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
        
    }    
    
    public String[] getInvoiceBrowseView() {
       
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getInvoiceBrowseView"});
            list.add(new String[]{"param1", tbfromshipper.getText()});
            list.add(new String[]{"param2", tbtoshipper.getText()});
            list.add(new String[]{"param3", tbfromcust.getText()});
            list.add(new String[]{"param4", tbtocust.getText()});
            list.add(new String[]{"param5", dfdate.format(dcfrom.getDate())});
            list.add(new String[]{"param6", dfdate.format(dcto.getDate())});
            try {
                jsonString = sendServerPost(list, "", null, "dataServFIN"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getInvoiceBrowseView")};
            }
        } else {
            jsonString = fglData.getInvoiceBrowseView(tbfromshipper.getText(), tbtoshipper.getText(), tbfromcust.getText(), tbtocust.getText(), dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate()));
        }
         rData = jsonToData(jsonString);
       
      return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
   }
    
    public String[] getDetail(String shipper) {
      
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getInvoiceBrowseDetail"});
            list.add(new String[]{"param1", shipper});
            try {
                jsonString = sendServerPost(list, "", null, "dataServFIN"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getDetail")};
            }
        } else {
            jsonString = fglData.getInvoiceBrowseDetail(shipper); 
        }        
        rData = jsonToData(jsonString);
        
        return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
      
    }
   
    public void done_Initialization() {
        setPanelComponentState(this, true);
        ddsite.removeAllItems();
        Calendar calfrom = Calendar.getInstance();
        String defaultsite = "";
        for (String[] s : initDataSets) {
            if (s[0].equals("site")) {
              defaultsite = s[1];  
            }
                      
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            
            if (s[0].equals("currency")) {
              defaultcurrency = s[1];  
            }
        }
        
        if (ddsite.getItemCount() > 0) {
            ddsite.setSelectedItem(defaultsite);
        }
        
        calfrom.add(Calendar.DATE, -365);
        dcfrom.setDate(calfrom.getTime()); 
        
        modeltable.setNumRows(0);
        modeldetail.setNumRows(0);
        
        tabledetail.setModel(modeldetail);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        tabledetail.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultcurrency)));
               
        tablereport.setModel(modeltable);
        tablereport.getTableHeader().setReorderingAllowed(false);
        tablereport.getColumnModel().getColumn(10).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultcurrency)));
        tablereport.getColumnModel().getColumn(10).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultcurrency)));
      
        tablereport.getColumnModel().getColumn(11).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultcurrency)));
        tablereport.getColumnModel().getColumn(11).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultcurrency)));
      
        tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
        tablereport.getColumnModel().getColumn(1).setMaxWidth(100);
        tablereport.getColumnModel().getColumn(12).setMaxWidth(100);
        tablereport.getColumnModel().getColumn(13).setMaxWidth(100);
       
    }

    public void done_getInvoiceBrowseView() {
        setPanelComponentState(this, true);
        double totsales = 0;
        double totopen = 0;
        modeltable.setNumRows(0);
        tablereport.setModel(modeltable);
        if (rData != null) {
            for (int j = 0; j < rData.length; j++) { // 
                if (rData[j][12].equals("print")) { 
                    rData[j][12] = BlueSeerUtils.clickprint;
                }
                if (rData[j][13].equals("mail")) { 
                    rData[j][13] = BlueSeerUtils.clickmail;
                }
            }
        
            int i = 0;
            if (rData.length > 0) {
                for (Object[] rowData : rData) {
                 totsales = totsales + bsParseDouble(rowData[11].toString()); 
                 totopen = totopen + bsParseDouble(rowData[10].toString());
                 modeltable.addRow(rowData); 
                 i++;
                } 
            }
            tbtotopen.setText(currformatDouble(totopen));
            tbtotsales.setText(currformatDouble(totsales));
        }
        rData = null;
    }   
     
    public void done_getDetail() {
      modeldetail.setNumRows(0);
         double totalsales = 0;
         double totalqty = 0;
         
       if (rData != null) {
        if (rData.length > 0) {
            for (Object[] rowData : rData) {
                totalsales = totalsales + (bsParseDouble(rowData[6].toString()) * bsParseDouble(rowData[7].toString()));
                totalqty = totalqty + bsParseDouble(rowData[6].toString());
                modeldetail.addRow(rowData);
            } 
            tbdetsales.setText(currformatDouble(totalsales));
            tbdetqty.setText(currformatDouble(totalqty));
        }
       }
       rData = null;
    }
    
    
    public void initvars(String[] arg) {
        tbdetqty.setText("0");
        tbtotopen.setText("0");
        tbtotsales.setText("0");
        tbdetsales.setText("0");
        
        
        java.util.Date now = new java.util.Date();
        dcfrom.setDate(now);
        dcto.setDate(now);
               
        modeltable.setNumRows(0);
        modeldetail.setNumRows(0);
        
        
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
                
        btdetail.setEnabled(false);
        detailpanel.setVisible(false);
        
        executeTask(BlueSeerUtils.dbaction.init, null);
          
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
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        datelabel = new javax.swing.JLabel();
        tbfromshipper = new javax.swing.JTextField();
        tbtoshipper = new javax.swing.JTextField();
        tbfromcust = new javax.swing.JTextField();
        tbtocust = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        tbcsv = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tbtotsales = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tbtotopen = new javax.swing.JLabel();
        tbdetqty = new javax.swing.JLabel();
        EndBal = new javax.swing.JLabel();
        tbdetsales = new javax.swing.JLabel();
        EndBal1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

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

        jLabel4.setText("To Billto:");
        jLabel4.setName("lbltocust"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Billto:");
        jLabel1.setName("lblfromcust"); // NOI18N

        jLabel3.setText("To Invoice:");
        jLabel3.setName("lbltoinv"); // NOI18N

        jLabel2.setText("From Invoice:");
        jLabel2.setName("lblfrominv"); // NOI18N

        jLabel5.setText("From InvDate:");
        jLabel5.setName("lblfromdate"); // NOI18N

        jLabel6.setText("To InvDate:");
        jLabel6.setName("lbltodate"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        tbcsv.setText("CSV");
        tbcsv.setName("btcsv"); // NOI18N
        tbcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcsvActionPerformed(evt);
            }
        });

        btprint.setText("Print/PDF");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        jLabel9.setText("Site:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbtoshipper, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbfromshipper, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel4))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(datelabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(95, 95, 95)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbfromcust, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(tbtocust, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btRun)
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
                        .addComponent(jLabel2)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(tbfromshipper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbfromcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(tbcsv)
                        .addComponent(btprint))
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(tbtoshipper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtocust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addGap(13, 13, 13))
        );

        jLabel8.setText("Total Amt:");
        jLabel8.setName("lbltotamt"); // NOI18N

        tbtotsales.setText("0");

        jLabel7.setText("Total Open Amt:");
        jLabel7.setName("lblopen"); // NOI18N

        tbtotopen.setText("0");

        tbdetqty.setBackground(new java.awt.Color(195, 129, 129));
        tbdetqty.setText("0");

        EndBal.setText("Detail Qty:");
        EndBal.setName("lblqty"); // NOI18N

        tbdetsales.setBackground(new java.awt.Color(195, 129, 129));
        tbdetsales.setText("0");

        EndBal1.setText("Detail Amt:");
        EndBal1.setName("lblamt"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(EndBal1)
                    .addComponent(EndBal)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbdetqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbtotsales, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbtotopen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tbdetsales, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotopen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotsales, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EndBal)
                    .addComponent(tbdetqty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdetsales, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EndBal1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
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
        modeltable.setNumRows(0);
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.run, new String[]{"getInvoiceBrowseView",""});
    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       tbdetqty.setText("");
       tbdetsales.setText("");
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 1) {
                executeTask(BlueSeerUtils.dbaction.run, new String[]{"getInvoiceBrowseDetail",tablereport.getValueAt(row, 2).toString()});
               // getdetail(tablereport.getValueAt(row, 2).toString());
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
        }
        if ( col == 0) {
                String mypanel = "InvoiceMaint";
               if (! checkperms(mypanel)) { return; }
               String[] args = new String[]{tablereport.getValueAt(row, 2).toString()};
               reinitpanels(mypanel, true, args);
        }
        if (col == 12) {
            OVData.printInvoiceRemote(tablereport.getValueAt(row, 2).toString(), "shipper", true);
           // OVData.printInvoice(tablereport.getValueAt(row, 2).toString(), true); 
        }
        if (col == 13) {
            if (sending) {
                return;
            }
            String[] x = OVData.isSMTPServer();
            if (x[0].equals("0")) {
               // tablereport.setEnabled(false);
                tablereport.getModel().setValueAt(BlueSeerUtils.clicklock,row,11);
                sending = true; 
              //  executeTask(tablereport.getValueAt(row, 2).toString(), row, bsNumberToUS(tablereport.getValueAt(row, 3).toString()));
                executeTask(BlueSeerUtils.dbaction.run, new String[]{"getInvoiceBrowseDetail",tablereport.getValueAt(row, 2).toString(), String.valueOf(row), bsNumberToUS(tablereport.getValueAt(row, 3).toString())});
            } else {
                bsmf.MainFrame.show(x[1]);
            }
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void tbcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcsvActionPerformed
      if (tablereport != null) {
          // OVData.exportCSV(tablereport);
          
        ArrayList<String> list = new ArrayList<String>();
        for (int j = 0; j < tablereport.getRowCount(); j++) {
           list.add(tablereport.getValueAt(j, 2).toString());  
        }          
        exportInvoiceCSV(list);
      }
    }//GEN-LAST:event_tbcsvActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        // OVData.printJTableToJasper("Invoice Report", tablereport, "genericJTableL10.jasper" );
        if (tablereport != null && modeltable.getRowCount() > 0) {
            String[] rec;
            String[] columnnames = new String[10];
            List<Object[]> list = new ArrayList<>();
            for (int j = 0; j < tablereport.getRowCount(); j++) {
                 rec = new String[]{tablereport.getValueAt(j, 2).toString(),
                   tablereport.getValueAt(j, 3).toString(),
                   tablereport.getValueAt(j, 4).toString(),
                   tablereport.getValueAt(j, 5).toString(),
                   tablereport.getValueAt(j, 6).toString(),
                   tablereport.getValueAt(j, 7).toString(),
                   tablereport.getValueAt(j, 8).toString(),
                   tablereport.getValueAt(j, 9).toString(),
                   tablereport.getValueAt(j, 10).toString(),
                   tablereport.getValueAt(j, 11).toString()};
                 list.add(rec);
             }
            HashMap hm = new HashMap();
            hm.put("REPORT_TITLE", "Invoice Report");
            hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
            for (int j = 2; j < tablereport.getColumnCount() - 2; j++) {
               hm.put("d" + (j - 2),  tablereport.getColumnName(j));
               columnnames[j - 2] = "COLUMN_" + (j - 2);
            }
            JRDataSource datasource = new ListOfArrayDataSource(list, columnnames);
            // assumes explicit jasper file name is larger than 3 chars.....if 3 chars or less...then must be key based L8, L8C, etc
            // type = "L8C";  ...or type = genericJTableL8.jasper
            // String jasperfile = (type.length() > 3) ? jasperfile = type  : OVData.getCodeValueByCodeKey("jasper", type)  ;
            Path template = FileSystems.getDefault().getPath(cleanDirString(getSystemJasperDirectory()) + "genericJTableL8.jasper");
            JasperPrint jasperPrint; 
            try {
             jasperPrint = JasperFillManager.fillReport(template.toString(), hm, datasource );
             JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
               jasperViewer.setVisible(true);
                    jasperViewer.setTitle("Viewer");
                    jasperViewer.setIconImage(null);
                    jasperViewer.setFitPageZoomRatio();
               //  JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ivprt.pdf");
           } catch (JRException ex) {
               MainFrame.bslog(ex);
           }
        }
    }//GEN-LAST:event_btprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EndBal;
    private javax.swing.JLabel EndBal1;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btprint;
    private javax.swing.JLabel datelabel;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JButton tbcsv;
    private javax.swing.JLabel tbdetqty;
    private javax.swing.JLabel tbdetsales;
    private javax.swing.JTextField tbfromcust;
    private javax.swing.JTextField tbfromshipper;
    private javax.swing.JTextField tbtocust;
    private javax.swing.JTextField tbtoshipper;
    private javax.swing.JLabel tbtotopen;
    private javax.swing.JLabel tbtotsales;
    // End of variables declaration//GEN-END:variables
}
