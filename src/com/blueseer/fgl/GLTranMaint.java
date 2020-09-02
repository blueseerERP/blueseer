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
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author vaughnte
 */
public class GLTranMaint extends javax.swing.JPanel {

    /**
     * Creates new form UserMaintPanel
     */
    
    Double positiveamt = 0.00;
    String type = "";
           DefaultTableCellRenderer tableRender = new DefaultTableCellRenderer();
    javax.swing.table.DefaultTableModel transmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Line", "Acct", "CC", "Desc", "Amt"
            })    {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
    
    public void tallyamount() {
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        double amt = 0.00;
        positiveamt = 0.00;
        for (int i = 0; i < transtable.getRowCount(); i++) {
             amt += Double.valueOf(transtable.getValueAt(i, 4).toString());
             if (Double.valueOf(transtable.getValueAt(i, 4).toString()) > 0) {
                 positiveamt += Double.valueOf(transtable.getValueAt(i, 4).toString());
             }
          }
        labeltotal.setText(df.format(amt));
    }
    
    public void clearinput() {
       ddacct.setSelectedIndex(0);
       ddcc.setSelectedIndex(0);
        tbdesc.setText("");
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
        

    }

    
    public void clearAll() {
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
       labeltotal.setText("0.00");
        type = ""; 
       transtable.setModel(transmodel);
       transmodel.setNumRows(0);
       transtable.getColumnModel().getColumn(4).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
       ddsite.removeAllItems();
        ArrayList sites = OVData.getSiteList();
        for (Object site : sites) {
            ddsite.addItem(site);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddacct.removeAllItems();
        ArrayList myacct = OVData.getGLAcctList();
        for (int i = 0; i < myacct.size(); i++) {
            ddacct.addItem(myacct.get(i));
        }
            ddacct.setSelectedIndex(0);
        
        ddcc.removeAllItems();
        ArrayList cc = OVData.getGLCCList();
        for (int i = 0; i < cc.size(); i++) {
            ddcc.addItem(cc.get(i));
        }
        ddcc.setSelectedItem(OVData.getDefaultCC());
       
        ddcurr.removeAllItems();
        ArrayList cur = OVData.getCurrlist();
        for (int i = 0; i < cur.size(); i++) {
            ddcurr.addItem(cur.get(i));
        }
            ddcurr.setSelectedIndex(0);    
      
       ddsite.setSelectedItem(OVData.getDefaultSite());
       ddcurr.setSelectedItem(OVData.getDefaultCurrency());
       tbuserid.setText(bsmf.MainFrame.userid);
    }
    
    public void enableAll() {
        btnew.setEnabled(true);
       btbrowse.setEnabled(true);
       btdeleteALL.setEnabled(true);
       btsubmit.setEnabled(true);
        btadd.setEnabled(true);
       btdelete.setEnabled(true);
       ddtype.setSelectedIndex(0);
       ddtype.setEnabled(true); 
       ddsite.setEnabled(true);
       ddcurr.setEnabled(true);
       ddacct.setEnabled(true);
       ddcc.setEnabled(true);
       tbamt.setEnabled(true);
       tbdesc.setEnabled(true);
       effdate.setEnabled(true);
       transtable.setEnabled(true);
       tbcontrolamt.setEnabled(true);
      
    }
    
    public void disableAll() {
         btnew.setEnabled(false);
       btbrowse.setEnabled(false);
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
       ddcc.setEnabled(false);
       tbamt.setEnabled(false);
       tbdesc.setEnabled(false);
       effdate.setEnabled(false);
       transtable.setEnabled(false);
       tbcontrolamt.setEnabled(false);
       
    }
    
    public void getGLTran(String refid) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_entdate, glt_desc, glt_amt, glt_userid " +
                        " FROM  gl_tran where glt_ref = " + "'" + refid + "'" +
                        "  ;");
                
                while (res.next()) {
                    i++;
                    tbref.setText(res.getString("glt_ref"));
                    dateentered.setText(res.getString("glt_entdate"));
                    
                    transmodel.addRow(new Object[]{transmodel.getRowCount() + 1,
                    res.getString("glt_acct"),
                    res.getString("glt_cc"),
                    res.getString("glt_desc"),
                    Double.valueOf(df.format(res.getDouble("glt_amt")))
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
                    btbrowse.setEnabled(true);
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve gl_tran record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void initvars(String[] arg) {
       
       clearAll();
       disableAll();
       
       btnew.setEnabled(true);
       btbrowse.setEnabled(true);
       
        if (arg != null && arg.length > 0) {
             getGLTran(arg[0]);
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
        jLabel49 = new javax.swing.JLabel();
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
        btbrowse = new javax.swing.JButton();
        tbcontrolamt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lbacct = new javax.swing.JLabel();
        lbcc = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Maintenance"));

        btsubmit.setText("Submit");
        btsubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsubmitActionPerformed(evt);
            }
        });

        btdeleteALL.setText("Delete");
        btdeleteALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteALLActionPerformed(evt);
            }
        });

        jLabel46.setText("Reference");

        jLabel47.setText("Userid");

        jLabel50.setText("EffectiveDate");

        jLabel2.setText("Entered Date");

        effdate.setDateFormatString("yyyy-MM-dd");

        btnew.setText("New");
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
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel48.setText("cc");

        jLabel49.setText("Acct");

        tbdesc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdescFocusLost(evt);
            }
        });

        jLabel51.setText("Amt");

        tbamt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbamtFocusLost(evt);
            }
        });

        jLabel52.setText("Desc");

        labeltotal.setBackground(new java.awt.Color(217, 235, 191));

        jLabel1.setText("Site");

        jLabel3.setText("Currency");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "standard", "reversing" }));

        jLabel4.setText("Total:");

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

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        tbcontrolamt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcontrolamtFocusLost(evt);
            }
        });

        jLabel5.setText("Control Amt");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel46))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(202, 202, 202)
                                .addComponent(btnew)))
                        .addGap(7, 7, 7)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(132, 132, 132)
                        .addComponent(jLabel47)
                        .addGap(5, 5, 5)
                        .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76)
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(dateentered, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel3)
                        .addGap(5, 5, 5)
                        .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(204, 204, 204)
                        .addComponent(jLabel50)
                        .addGap(5, 5, 5)
                        .addComponent(effdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel52)
                        .addGap(5, 5, 5)
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jLabel51)
                        .addGap(5, 5, 5)
                        .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(btadd)
                        .addGap(12, 12, 12)
                        .addComponent(btdelete))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGap(62, 62, 62)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel49)
                                .addGap(5, 5, 5)
                                .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lbcc, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(6, 6, 6))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel46))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jLabel5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(btnew))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(btbrowse))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel47))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateentered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel50))
                    .addComponent(effdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel49))
                            .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel48))
                            .addComponent(lbcc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel52))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel51))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btadd)
                    .addComponent(btdelete))
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
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
        try {
            
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                String nextstartdate = "";
                Double amt = 0.00;
                DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
                
                if (  Double.compare(Double.valueOf(tbcontrolamt.getText().toString()), positiveamt) != 0 ) {
                    proceed = false;
                    bsmf.MainFrame.show("Control Amount not met " + df.format(Double.valueOf(tbcontrolamt.getText().toString())) + "/" + String.valueOf(df.format(positiveamt)) );
                    return;
                }
                
                if (Double.valueOf(labeltotal.getText().toString()) != 0) {
                    proceed = false;
                    bsmf.MainFrame.show("Not Balanced...cannot submit");
                    return;
                }
                   
                ArrayList<String> caldate = OVData.getGLCalForDate(dfdate.format(effdate.getDate()));
                if (caldate.isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("No calendar period for effective date");
                    return;
                }
                
                if ( OVData.isGLPeriodClosed(dfdate.format(effdate.getDate()))) {
                    proceed = false;
                    bsmf.MainFrame.show("Period is closed");
                    return;
                }
                    
                // if reversing...check and make sure next immediate period is open
                if (! caldate.isEmpty() && type.equals("RV")) {
                    int nextperiod = Integer.valueOf(caldate.get(1)) + 1;
                    int thisyear = Integer.valueOf(caldate.get(0));
                      if (nextperiod > 12) {
                          thisyear++;
                          nextperiod = 1;
                      }
                      ArrayList<String> nextcal = OVData.getGLCalByYearAndPeriod(String.valueOf(thisyear), String.valueOf(nextperiod));
                      if (nextcal.isEmpty()) {
                       proceed = false;
                       bsmf.MainFrame.show("No Calendar for Reversing Period");
                       return;
                      }
                      if (! nextcal.isEmpty() && nextcal.get(4).toString().equals("closed")) {
                       proceed = false;
                       bsmf.MainFrame.show("Reversing Period is closed");
                       return;
                      }
                      nextstartdate = String.valueOf(nextcal.get(2));
                }
                
                if (type.equals("RV") && nextstartdate.isEmpty()) {
                     proceed = false;
                     bsmf.MainFrame.show("Reversing Period start date is blank");
                     return;
                }
                
                if (proceed) {
                    for (int i = 0; i < transtable.getRowCount(); i++) {
                        amt = Double.valueOf(transtable.getValueAt(i, 4).toString());
                        
                        st.executeUpdate("insert into gl_tran "
                        + "(glt_line, glt_acct, glt_cc, glt_effdate, glt_amt, glt_ref, glt_site, glt_type, glt_desc, glt_userid, glt_entdate )"
                        + " values ( " 
                        + "'" + transtable.getValueAt(i, 0).toString() + "'" + ","
                        + "'" + transtable.getValueAt(i, 1).toString() + "'" + ","
                        + "'" + transtable.getValueAt(i, 2).toString() + "'" + ","
                        + "'" + dfdate.format(effdate.getDate()) + "'" + ","
                        + "'" + transtable.getValueAt(i, 4).toString() + "'" + ","
                        + "'" + tbref.getText().toString() + "'" + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + type + "'" + ","
                        + "'" + transtable.getValueAt(i, 3).toString() + "'" + ","
                        + "'" + tbuserid.getText().toString() + "'" + ","
                         + "'" + dateentered.getText().toString() + "'"
                                + ")"
                        + ";" );
                        
                        //if reversing...add entries to next immediate period start date
                        if (type.equals("RV")) {
                            st.executeUpdate("insert into gl_tran "
                        + "(glt_line, glt_acct, glt_cc, glt_effdate, glt_amt, glt_ref, glt_site, glt_type, glt_desc, glt_userid, glt_entdate )"
                        + " values ( " 
                        + "'" + transtable.getValueAt(i, 0).toString() + "'" + ","
                        + "'" + transtable.getValueAt(i, 1).toString() + "'" + ","
                        + "'" + transtable.getValueAt(i, 2).toString() + "'" + ","
                        + "'" + nextstartdate + "'" + ","
                        + "'" + String.valueOf(-1 * amt) + "'" + ","
                        + "'" + tbref.getText().toString() + "'" + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + type + "'" + ","
                        + "'" + transtable.getValueAt(i, 3).toString() + "'" + ","
                        + "'" + tbuserid.getText().toString() + "'" + ","
                         + "'" + dateentered.getText().toString() + "'"
                                + ")"
                        + ";" );
                        }
                            
                        
                    }
                    bsmf.MainFrame.show("Transactions added successfully");
                    initvars(null);
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot Add GL Transactions via SQL statement");
            }
            bsmf.MainFrame.con.close();
           
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btsubmitActionPerformed

    private void btdeleteALLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteALLActionPerformed
            boolean proceed = bsmf.MainFrame.warn("Are you sure?");
            
            if (! tbref.getText().toString().startsWith("JL")) {
                proceed = false;
                bsmf.MainFrame.show("You can only delete 'JL' type transactions created with GL Maintenance Menu");
            }
            
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from gl_tran where glt_ref = " + "'" + tbref.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted " + String.valueOf(i) + " gl_tran records for ref= " + tbref.getText());
                    initvars(null);
                    } else {
                     bsmf.MainFrame.show("unable to delete...may have been posted" + tbref.getText());   
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete GL_TRAN Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteALLActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        
        String prodline = "";
        String status = "";
        String op = "";
        DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        transtable.setModel(transmodel);
        
         if (tbdesc.getText().isEmpty()) {
             bsmf.MainFrame.show("Desc cannot be blank");
             tbdesc.setBackground(Color.yellow);
            tbdesc.requestFocus();
            return;
         }
       
        if (tbamt.getText().isEmpty()) {
             bsmf.MainFrame.show("Amount cannot be blank");
            tbamt.setBackground(Color.yellow);
            tbamt.requestFocus();
            return;
        }
       
        if (! OVData.isAcctNumberValid(ddacct.getSelectedItem().toString()) ) {
            bsmf.MainFrame.show("Acct is not valid");
            return;
        }
        if (! OVData.isCostCenterValid(ddcc.getSelectedItem().toString()) ) {
            bsmf.MainFrame.show("CostCenter is not valid");
            return;
        }
        
        if (effdate.getDate() == null || ! BlueSeerUtils.isValidDateStr(BlueSeerUtils.mysqlDateFormat.format(effdate.getDate())) ) {
            bsmf.MainFrame.show("Date not properly formatted");
            return;
        }
        
        
       
            transmodel.addRow(new Object[]{transmodel.getRowCount() + 1,
                ddacct.getSelectedItem().toString(),
                ddcc.getSelectedItem().toString(),
                tbdesc.getText(),
                Double.valueOf(df.format(Double.valueOf(tbamt.getText())))
            });
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
        
       if (ddtype.getSelectedItem().toString().equals("standard")) {
       type = "JL";
       tbref.setText(OVData.setGLRecNbr("JL"));
       } else {
       type = "RV";
       tbref.setText(OVData.setGLRecNbr("RV"));    
       }
       
       tbref.setEnabled(false);
       tbuserid.setEnabled(false);
       dateentered.setEnabled(false);
       btdeleteALL.setEnabled(false);
       
    }//GEN-LAST:event_btnewActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"glmaint","glt_ref"});
    }//GEN-LAST:event_btbrowseActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
        if (ddacct.getSelectedItem() != null )
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
                bsmf.MainFrame.show("unable to select from ac_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_ddacctActionPerformed

    private void ddccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddccActionPerformed
       if (ddcc.getSelectedItem() != null )
        try {
            
        
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select dept_desc from dept_mstr where dept_id = " + "'" + ddcc.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbcc.setText(res.getString("dept_desc"));
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("unable to select from dept_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_ddccActionPerformed

    private void tbamtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbamtFocusLost
        String x = BlueSeerUtils.bsformat("", tbamt.getText(), "2");
        if (x.equals("error")) {
            tbamt.setText("");
            tbamt.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
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
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbcontrolamt.requestFocus();
        } else {
            tbcontrolamt.setText(x);
            tbcontrolamt.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbcontrolamtFocusLost

    private void tbdescFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdescFocusLost
       if (tbdesc.getText().isEmpty()) {
           tbdesc.setBackground(Color.yellow);
       } else {
           tbdesc.setBackground(Color.white);
       }
    }//GEN-LAST:event_tbdescFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteALL;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btsubmit;
    private javax.swing.JTextField dateentered;
    private javax.swing.JComboBox ddacct;
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
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labeltotal;
    private javax.swing.JLabel lbacct;
    private javax.swing.JLabel lbcc;
    private javax.swing.JTextField tbamt;
    private javax.swing.JTextField tbcontrolamt;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbuserid;
    private javax.swing.JTable transtable;
    // End of variables declaration//GEN-END:variables
}
