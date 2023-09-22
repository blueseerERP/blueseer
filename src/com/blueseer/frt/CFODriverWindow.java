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

package com.blueseer.frt;

import com.blueseer.pur.*;
import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.frt.CFOMaint.fc;
import static com.blueseer.frt.frtData.getCFOCtrl;
import static com.blueseer.frt.frtData.getDriverInfo;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.vdr.venData;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author vaughnte
 */
public class CFODriverWindow extends javax.swing.JPanel {
 
     public Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
     
     boolean carrierPOV = true;                     
     String startdate = "";
     String enddate = "";
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), 
                            getGlobalColumnTag("number"),  
                            getGlobalColumnTag("status"), 
                            getGlobalColumnTag("name"),
                            getGlobalColumnTag("driverid"),
                            getGlobalColumnTag("type"),
                            getGlobalColumnTag("miles"),
                            getGlobalColumnTag("cost")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    javax.swing.table.DefaultTableModel modeldriver = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("driverid"),
                            "DATE1", 
                            "DATE2", 
                            "DATE3", 
                            "DATE4", 
                            "DATE5", 
                            "DATE6", 
                            "DATE7"});
    
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
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), column);  
        
         if ("scheduled".equals(status)) {
            c.setBackground(Color.BLUE);
            c.setForeground(Color.WHITE);
        } else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }       
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       /*
            if (column == 3)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       */
        return c;
    }
    }

    
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public CFODriverWindow() {
        initComponents();
        setLanguageTags(this);
    }

    public void getdetail(String cfo, String revision) {
      
         modeldriver.setNumRows(0);
         double total = 0;
        
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
                String blanket = "";
                res = st.executeQuery("select cfod_nbr, cfod_stopline, cfod_type, cfod_date, cfod_datetype, cfod_name, cfod_line1, cfod_city, cfod_state, cfod_zip from cfo_det " +
                        " where cfod_nbr = " + "'" + cfo + "'" +
                        " and cfod_revision = " + "'" + revision + "'" +  ";");
                while (res.next()) {
                   modeldriver.addRow(new Object[]{ 
                      res.getString("cfod_stopline"),
                      res.getString("cfod_type"),
                      res.getString("cfod_datetype"),
                      res.getString("cfod_date"),
                      res.getString("cfod_name"),
                      res.getString("cfod_line1"),
                      res.getString("cfod_city"),
                      res.getString("cfod_state"),
                      res.getString("cfod_zip")});
                }
               
              
                tabledetail.setModel(modeldriver);
              //   tabledetail.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                this.repaint();

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
        lblamttot.setText("0");
        lbllines.setText("0");
        labeldettotal.setText("");
        
        
        fc = getCFOCtrl(null);
       // note:  fc.frtc_function() = 1 for Trucking POV...and 0 for Customer POV
       carrierPOV = (fc.frtc_function().equals("1"));
       if (carrierPOV) {
           fromkeypartner.setText("From Customer");
           tokeypartner.setText("To Customer");
       } else {
           fromkeypartner.setText("From Carrier");
           tokeypartner.setText("To Carrier");
       }
        
        java.util.Date now = new java.util.Date();
        dcFrom.setDate(now);
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        DateFormat dfperiod = new SimpleDateFormat("M");
        
        mymodel.setNumRows(0);
        modeldriver.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldriver);
        
         
         
       
          
         
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
        
        
        
        ddstatus.removeAllItems();
        ArrayList sites = OVData.getSiteList();
        for (Object site : sites) {
            ddstatus.addItem(site);
        }
        
        ddcustfrom.removeAllItems();
        ddcustto.removeAllItems();
        ArrayList<String> partners = new ArrayList<String>();
        if (carrierPOV) {
            partners = cusData.getcustmstrlist();
        } else {
            partners = OVData.getfreightlist();
        }
        for (Object p : partners) {
            ddcustfrom.addItem(p);
        }
        ddcustto.removeAllItems();
        for (Object p : partners) {
            ddcustto.addItem(p);
        }
        
        if (ddcustfrom.getItemCount() > 0)
        ddcustfrom.setSelectedIndex(0);
        
        if (ddcustto.getItemCount() > 0)
        ddcustto.setSelectedIndex(ddcustto.getItemCount() - 1);
      
        ddstatus.removeAllItems();
        ddstatus.addItem("");
        ddstatus.addItem("pending");
        ddstatus.addItem("scheduled");
        ddstatus.addItem("delivered");
        ddstatus.addItem("declined");
        ddstatus.addItem("cancelled");
        ddstatus.addItem("closed");
        ddstatus.setSelectedIndex(0);
        
          
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
        tokeypartner = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        fromkeypartner = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ddcustto = new javax.swing.JComboBox();
        ddcustfrom = new javax.swing.JComboBox();
        ddstatus = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        tbfromnbr = new javax.swing.JTextField();
        tbtonbr = new javax.swing.JTextField();
        dcFrom = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        ddweek = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lbllines = new javax.swing.JLabel();
        lblamttot = new javax.swing.JLabel();
        EndBal = new javax.swing.JLabel();
        labeldettotal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Freight Order / Driver Schedule Window"));
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
        jScrollPane2.setViewportView(tabledetail);

        detailpanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tablepanel.add(detailpanel);

        tokeypartner.setText("To Customer");
        tokeypartner.setName("lbltovend"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("Status");
        jLabel5.setName("lblsite"); // NOI18N

        fromkeypartner.setText("From Customer");
        fromkeypartner.setName("lblfromvend"); // NOI18N

        jLabel3.setText("To Number");
        jLabel3.setName("lbltopo"); // NOI18N

        jLabel6.setText("From Number");
        jLabel6.setName("lblfrompo"); // NOI18N

        dcFrom.setDateFormatString("yyyy-MM-dd");

        jLabel1.setText("Date");

        ddweek.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "week1", "week2", "week3", "week4" }));

        jLabel2.setText("Week");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddweek, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tbtonbr)
                    .addComponent(tbfromnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tokeypartner)
                            .addComponent(fromkeypartner))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddcustfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ddcustto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(4, 4, 4)
                .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btRun)
                .addGap(111, 111, 111))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbfromnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(tbtonbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddweek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fromkeypartner)
                            .addComponent(ddcustfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btRun)
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tokeypartner)
                            .addComponent(ddcustto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setText("Total Qty");
        jLabel8.setName("lbltotalqty"); // NOI18N

        lbllines.setText("0");

        lblamttot.setBackground(new java.awt.Color(195, 129, 129));
        lblamttot.setText("0");

        EndBal.setText("Total Amt");
        EndBal.setName("lbltotalamt"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(EndBal)
                    .addComponent(jLabel8))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblamttot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbllines, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbllines, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EndBal)
                    .addComponent(lblamttot, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labeldettotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addComponent(labeldettotal, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
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

    
try {
                if (dcFrom.getDate() == null) {
                    bsmf.MainFrame.show("choose a starting date");
                    return;
                }
                
                String[] days = new String[7];
                String[] daysh = new String[7];
                String[] daysv = new String[7];
                
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat hf = new SimpleDateFormat("EEE-MM-dd");
                Calendar cal = Calendar.getInstance();
                
                Date date = dcFrom.getDate();
                cal.setTime(date);
                if (ddweek.getSelectedItem().toString().equals("week2")) {
                  cal.add(Calendar.DATE,7);  
                }
                if (ddweek.getSelectedItem().toString().equals("week3")) {
                  cal.add(Calendar.DATE,14);  
                }
                if (ddweek.getSelectedItem().toString().equals("week4")) {
                  cal.add(Calendar.DATE,21);  
                }
                
                startdate = dfdate.format(cal.getTime());
                for (int v = 0; v < days.length; v++) {
                    if (v == 0) {
                      days[v] = dfdate.format(cal.getTime()); // startdate
                      daysh[v] = hf.format(cal.getTime());
                      daysv[v] = "open";
                      continue;
                    }
                    cal.add(Calendar.DATE,1);
                    days[v] = dfdate.format(cal.getTime());
                    daysh[v] = hf.format(cal.getTime());
                    daysv[v] = "open";
                }
                
                enddate = dfdate.format(cal.getTime());
                
                
                
                tabledetail.getColumnModel().getColumn(1).setHeaderValue(daysh[0]);
                tabledetail.getColumnModel().getColumn(2).setHeaderValue(daysh[1]);
                tabledetail.getColumnModel().getColumn(3).setHeaderValue(daysh[2]);
                tabledetail.getColumnModel().getColumn(4).setHeaderValue(daysh[3]);
                tabledetail.getColumnModel().getColumn(5).setHeaderValue(daysh[4]);
                tabledetail.getColumnModel().getColumn(6).setHeaderValue(daysh[5]);
                tabledetail.getColumnModel().getColumn(7).setHeaderValue(daysh[6]);
                
                tabledetail.getTableHeader().repaint();
    
    
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
             
                double qty = 0;
                double dol = 0;
                double total = 0;
                double tax = 0;
                double disc = 0;
                double charge = 0;
                int i = 0;
               
               mymodel.setNumRows(0);
        
              tablereport.setModel(mymodel);
              tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
              
              modeldriver.setNumRows(0);
              tabledetail.setModel(modeldriver);
              
                
                 double totqty = 0;
                 double totamt = 0;
                 
                 String nbrfrom = tbfromnbr.getText();
                 String nbrto = tbtonbr.getText();
                 
                 if (nbrfrom.isEmpty()) {
                     nbrfrom = bsmf.MainFrame.lowchar;
                 }
                  if (nbrto.isEmpty()) {
                     nbrto = bsmf.MainFrame.hichar;
                 }
                  
                
                 
                 String custfrom = "";
                 String custto = "";
                 
                 if (ddcustfrom.getSelectedItem() != null)
                     custfrom = ddcustfrom.getSelectedItem().toString();
                 
                 if (ddcustto.getSelectedItem() != null)
                     custto = ddcustto.getSelectedItem().toString();
                 
                 
                 
                 Enumeration<TableColumn> en = tabledetail.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (modeldriver.getColumnClass(tc.getModelIndex()).getSimpleName().equals("ImageIcon")) {
                         continue;
                     }
                     tc.setCellRenderer(new CFODriverWindow.SomeRenderer());
                 }
                 tablereport.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
              
             if (! ddstatus.getSelectedItem().toString().isBlank()) {    
             
                if (carrierPOV) { 
                res = st.executeQuery("select cfo_nbr, cfo_revision, cfo_orderstatus, cfo_cust, cfo_mileage, " +
                      " cfo_truckid, cfo_driver, cfo_ratetype, cfo_cost, cm_name " +
                         " from cfo_mstr inner join cm_mstr on cm_code = cfo_cust where " +
                        " cfo_cust >= " + "'" + custfrom + "'" + " AND " +
                        " cfo_cust <= " + "'" + custto + "'" + " AND " +
                        " cfo_nbr >= " + "'" + nbrfrom + "'" + " AND " +
                        " cfo_nbr <= " + "'" + nbrto + "'" + " AND " +
                        " cfo_orderstatus = " + "'" + ddstatus.getSelectedItem().toString() + "'" +
                        " order by cfo_nbr ;");
                } else {
                    res = st.executeQuery("select cfo_nbr, cfo_revision, cfo_orderstatus, cfo_cust, cfo_mileage, " +
                      " cfo_truckid, cfo_driver, cfo_ratetype, cfo_cost, car_name " +
                         " from cfo_mstr inner join car_mstr on car_id = cfo_cust where " +
                        " cfo_cust >= " + "'" + custfrom + "'" + " AND " +
                        " cfo_cust <= " + "'" + custto + "'" + " AND " +
                        " cfo_nbr >= " + "'" + nbrfrom + "'" + " AND " +
                        " cfo_nbr <= " + "'" + nbrto + "'" + " AND " +
                        " cfo_orderstatus = " + "'" + ddstatus.getSelectedItem().toString() + "'" +
                        " order by cfo_nbr ;");
                }
             } else {
                 
                if (carrierPOV) {  
                res = st.executeQuery("select cfo_nbr, cfo_revision, cfo_orderstatus, cfo_cust, cfo_mileage, " +
                      " cfo_truckid, cfo_driver, cfo_ratetype, cfo_cost, cm_name " +
                         " from cfo_mstr inner join cm_mstr on cm_code = cfo_cust where " +
                        " cfo_cust >= " + "'" + custfrom + "'" + " AND " +
                        " cfo_cust <= " + "'" + custto + "'" + " AND " +
                        " cfo_nbr >= " + "'" + nbrfrom + "'" + " AND " +
                        " cfo_nbr <= " + "'" + nbrto + "'" + 
                        " order by cfo_nbr ;"); 
                } else {
                    res = st.executeQuery("select cfo_nbr, cfo_revision, cfo_orderstatus, cfo_cust, cfo_mileage, " +
                      " cfo_truckid, cfo_driver, cfo_ratetype, cfo_cost, car_name " +
                         " from cfo_mstr inner join car_mstr on car_id = cfo_cust where " +
                        " cfo_cust >= " + "'" + custfrom + "'" + " AND " +
                        " cfo_cust <= " + "'" + custto + "'" + " AND " +
                        " cfo_nbr >= " + "'" + nbrfrom + "'" + " AND " +
                        " cfo_nbr <= " + "'" + nbrto + "'" + 
                        " order by cfo_nbr ;"); 
                }
             }
                       while (res.next()) {
                       
                        total = res.getDouble("cfo_cost"); 
                        dol = dol + total;
                        i++;      
                        if (carrierPOV) {  
                        mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, 
                                res.getString("cfo_nbr"),
                                res.getString("cfo_orderstatus"),
                                res.getString("cm_name"),
                                res.getString("cfo_driver"),
                                res.getString("cfo_ratetype"),
                                res.getString("cfo_mileage"),
                                bsParseDouble(currformatDouble(total))
                            });
                        } else {
                            mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, 
                                res.getString("cfo_nbr"),
                                res.getString("cfo_orderstatus"),
                                res.getString("car_name"),
                                res.getString("cfo_driver"),
                                res.getString("cfo_ratetype"),
                                res.getString("cfo_mileage"),
                                bsParseDouble(currformatDouble(total))
                                });
                        }
                    } // while   
                    
                       // now driver scheduling info
            res = st.executeQuery("select drv_id, drv_lname, drv_fname, cfo_nbr, cfo_orderstatus, date1, date2 from drv_mstr left outer join " +
                    " (select cfo_driver, cfo_nbr, cfo_orderstatus, (select cfod_date from cfo_det where cfod_nbr = cfo_nbr and " +
                    " cfod_datetype like '%Pickup%') as date1, (select cfod_date from cfo_det where cfod_nbr = cfo_nbr and cfod_datetype like '%Delivery%') as date2 " +
                    " from cfo_mstr inner join cfo_det on cfod_nbr = cfo_nbr and cfod_revision = cfo_revision " +
                    " where cfod_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                    " and cfod_date <= " + "'" + enddate + "'" +
                    " and cfod_datetype like '%Pickup%' ) x  " +
                    " on x.cfo_driver = drv_id; " ); 
            Date date1 = null;
            Date date2 = null;
            long diffInMillies = 0;
            long diff = 0;
            LinkedHashMap<String,ArrayList<String>> lhm = new LinkedHashMap<String,ArrayList<String>>();
            
            while (res.next()) {
                date1 = null;
                date2 = null;
                diffInMillies = 0;
                diff = 0;
                if (res.getString("date1") != null && ! res.getString("date1").isBlank()) {
                 date1 = dfdate.parse(res.getString("date1"));
                }
                if (res.getString("date2") != null && ! res.getString("date2").isBlank()) {
                 date2 = dfdate.parse(res.getString("date2"));
                }
                if (date1 != null && date2 != null) {
                 diffInMillies = Math.abs(date2.getTime() - date1.getTime());
                 diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                }
                
                ArrayList<String> temp = lhm.get(res.getString("drv_id"));
                if (temp != null && res.getString("date1") != null) {
                          if (! temp.contains(dfdate.format(date1))) { // put pickup date first...if not already there
                                temp.add(dfdate.format(date1));
                          }
                          if (diff > 0) { // now add dates in between pickup and delivery dates if exist
                          Calendar c = Calendar.getInstance();
                          c.setTime(date1);
                            for (int k = 1; k <= diff; k++) {
                            c.add(Calendar.DATE, 1);
                            Date newdate = c.getTime();
                                if (! temp.contains(dfdate.format(newdate))) {
                                temp.add(dfdate.format(newdate));
                                }
                            }
                          }
                          lhm.put(res.getString("drv_id"), temp);
                          
                } else {
                          ArrayList<String> al = new ArrayList<String>();
                          al.add(res.getString("date1")); // if blank...let it ride
                          if (diff > 0) { // now add dates in between pickup and delivery dates if exist
                          Calendar c = Calendar.getInstance();
                          c.setTime(date1);
                            for (int k = 1; k <= diff; k++) {
                            c.add(Calendar.DATE, 1);
                            Date newdate = c.getTime();
                                if (! al.contains(dfdate.format(newdate))) {
                                al.add(dfdate.format(newdate));
                                }
                            }
                          }
                          lhm.put(res.getString("drv_id"), al);
                }
                
            }
            
            // now enter into model table
            String driverconcat = "";
            for (Map.Entry<String, ArrayList<String>> val : lhm.entrySet()) {
                
                String[] drv = getDriverInfo(val.getKey());
                
	    	ArrayList<String> f = val.getValue();
                daysv[0] = (f.contains(days[0])) ? "scheduled" : "open";
                daysv[1] = (f.contains(days[1])) ? "scheduled" : "open";
                daysv[2] = (f.contains(days[2])) ? "scheduled" : "open";
                daysv[3] = (f.contains(days[3])) ? "scheduled" : "open";
                daysv[4] = (f.contains(days[4])) ? "scheduled" : "open";
                daysv[5] = (f.contains(days[5])) ? "scheduled" : "open";
                daysv[6] = (f.contains(days[6])) ? "scheduled" : "open";
                
                driverconcat = val.getKey() + " - " + drv[0] + ", " + drv[1];
                modeldriver.addRow(new Object[]{ 
                      driverconcat,
                      daysv[0],
                      daysv[1],
                      daysv[2],
                      daysv[3],
                      daysv[4],
                      daysv[5],
                      daysv[6]
                });
               
	    }
                      
            
                lblamttot.setText(String.valueOf(currformatDouble(dol)));
                lbllines.setText(bsNumber(i));
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
       
    }//GEN-LAST:event_btRunActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
                String mypanel = "CFOMaint";
               if (! checkperms(mypanel)) { return; }
               String[] args = new String[]{tablereport.getValueAt(row, 1).toString(), ""};
               reinitpanels(mypanel, true, args);
              
        }
    }//GEN-LAST:event_tablereportMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EndBal;
    private javax.swing.JButton btRun;
    private com.toedter.calendar.JDateChooser dcFrom;
    private javax.swing.JComboBox ddcustfrom;
    private javax.swing.JComboBox ddcustto;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> ddweek;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel fromkeypartner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labeldettotal;
    private javax.swing.JLabel lblamttot;
    private javax.swing.JLabel lbllines;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbfromnbr;
    private javax.swing.JTextField tbtonbr;
    private javax.swing.JLabel tokeypartner;
    // End of variables declaration//GEN-END:variables
}
