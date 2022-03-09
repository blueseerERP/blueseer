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
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
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
                "Line", "Acct", "CC", "Desc", "Amt"
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
        setLanguageTags(this);
        

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
        ArrayList myacct = fglData.getGLAcctList();
        for (int i = 0; i < myacct.size(); i++) {
            ddacct.addItem(myacct.get(i));
        }
            ddacct.setSelectedIndex(0);
        
        ddcc.removeAllItems();
        ArrayList cc = fglData.getGLCCList();
        for (int i = 0; i < cc.size(); i++) {
            ddcc.addItem(cc.get(i));
        }
        ddcc.setSelectedItem(OVData.getDefaultCC());
       
        ddcurr.removeAllItems();
        ArrayList cur = fglData.getCurrlist();
        for (int i = 0; i < cur.size(); i++) {
            ddcurr.addItem(cur.get(i));
        } 
        ddcurr.setSelectedItem(OVData.getDefaultCurrency());
        
       ddsite.setSelectedItem(OVData.getDefaultSite());
       tbuserid.setText(bsmf.MainFrame.userid);
    }
    
    public void enableAll() {
        btnew.setEnabled(true);
       btlookup.setEnabled(true);
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
       btlookup.setEnabled(false);
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
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
                    currformat(res.getString("glt_amt"))
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
    
    
    public void initvars(String[] arg) {
       
       clearAll();
       disableAll();
       
       btnew.setEnabled(true);
       btlookup.setEnabled(true);
       
        if (arg != null && arg.length > 0) {
             getGLTran(arg[0]);
         }
        
            
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
                ddacct.setSelectedItem(target.getValueAt(row,1).toString());
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
        dialog.setVisible(true);
    }

    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getGLTranBrowseUtil(luinput.getText(),0, "glt_ref");
        } else {
         luModel = DTData.getGLTranBrowseUtil(luinput.getText(),0, "glt_acct");   
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
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), getClassLabelTag("lblacct", this.getClass().getSimpleName())); 
        
        
        
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
        tbcontrolamt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lbacctname = new javax.swing.JLabel();
        lbccname = new javax.swing.JLabel();
        btLookUpAccount = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();

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

        jLabel49.setText("Acct");
        jLabel49.setName("lblacct"); // NOI18N

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

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "standard", "reversing" }));

        jLabel4.setText("Total:");
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
                    .addComponent(jLabel49, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.TRAILING))
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
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btLookUpAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbccname, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbacctname, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(202, 202, 202)
                        .addComponent(btnew))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGap(74, 74, 74)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(397, 397, 397)
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbamt, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btadd)
                        .addGap(12, 12, 12)
                        .addComponent(btdelete)))
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
                                .addComponent(btnew))
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
                        .addComponent(jLabel49))
                    .addComponent(lbacctname, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48))
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbccname, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                boolean proceed = true;
                String nextstartdate = "";
                Double amt = 0.00;
                
                if (  Double.compare(bsParseDouble(tbcontrolamt.getText()), positiveamt) != 0 ) {
                    proceed = false;
                    String s_positiveamt = bsFormatDouble(positiveamt);
                    bsmf.MainFrame.show(getMessageTag(1039, currformat(tbcontrolamt.getText()) + "/" + s_positiveamt));
                    return;
                }
                
                if (bsParseDouble(labeltotal.getText().toString()) != 0) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1040));
                    return;
                }
                   
                String[] caldate = fglData.getGLCalForDate(dfdate.format(effdate.getDate()));
                if (caldate == null || caldate[0].isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1038));
                    return;
                }
                
                if ( OVData.isGLPeriodClosed(dfdate.format(effdate.getDate()))) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1035));
                    return;
                }
                    
                // if reversing...check and make sure next immediate period is open
                if (! caldate[0].isEmpty() && type.equals("RV")) {
                    int nextperiod = Integer.valueOf(caldate[1]) + 1;
                    int thisyear = Integer.valueOf(caldate[0]);
                      if (nextperiod > 12) {
                          thisyear++;
                          nextperiod = 1;
                      }
                      ArrayList<String> nextcal = OVData.getGLCalByYearAndPeriod(String.valueOf(thisyear), String.valueOf(nextperiod));
                      if (nextcal.isEmpty()) {
                       proceed = false;
                       bsmf.MainFrame.show(getMessageTag(1042));
                       return;
                      }
                      if (! nextcal.isEmpty() && nextcal.get(4).toString().equals("closed")) {
                       proceed = false;
                       bsmf.MainFrame.show(getMessageTag(1041));
                       return;
                      }
                      nextstartdate = String.valueOf(nextcal.get(2));
                }
                
                if (type.equals("RV") && nextstartdate.isEmpty()) {
                     proceed = false;
                     bsmf.MainFrame.show(getMessageTag(1043));
                     return;
                }
                
                String curr = OVData.getDefaultCurrency();
                String basecurr = curr;
                
                if (proceed) {
                    for (int i = 0; i < transtable.getRowCount(); i++) {
                        amt = bsParseDouble(transtable.getValueAt(i, 4).toString());
                        
                        st.executeUpdate("insert into gl_tran "
                        + "(glt_line, glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc, glt_userid, glt_entdate )"
                        + " values ( " 
                        + "'" + transtable.getValueAt(i, 0).toString() + "'" + ","
                        + "'" + transtable.getValueAt(i, 1).toString() + "'" + ","
                        + "'" + transtable.getValueAt(i, 2).toString() + "'" + ","
                        + "'" + dfdate.format(effdate.getDate()) + "'" + ","
                        + "'" + transtable.getValueAt(i, 4).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + transtable.getValueAt(i, 4).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + curr + "'" + ","
                        + "'" + basecurr + "'" + ","        
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
                        + "'" + String.valueOf(-1 * amt).replace(defaultDecimalSeparator, '.') + "'" + ","
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
                    bsmf.MainFrame.show(getMessageTag(1007));
                    initvars(null);
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
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
    }//GEN-LAST:event_btsubmitActionPerformed

    private void btdeleteALLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteALLActionPerformed
            boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
            
            if (! tbref.getText().toString().startsWith("JL")) {
                proceed = false;
                bsmf.MainFrame.show(getMessageTag(1044));
            }
            
        if (proceed) {
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
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
        
        
       
            transmodel.addRow(new Object[]{transmodel.getRowCount() + 1,
                ddacct.getSelectedItem().toString(),
                ddcc.getSelectedItem().toString(),
                tbdesc.getText(),
                tbamt.getText()
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
       tbref.setText(fglData.setGLRecNbr("JL"));
       } else {
       type = "RV";
       tbref.setText(fglData.setGLRecNbr("RV"));    
       }
       
       tbref.setEnabled(false);
       tbuserid.setEnabled(false);
       dateentered.setEnabled(false);
       btdeleteALL.setEnabled(false);
       
    }//GEN-LAST:event_btnewActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
        if (ddacct.getSelectedItem() != null )
        try {
            
        
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
    }//GEN-LAST:event_ddacctActionPerformed

    private void ddccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddccActionPerformed
       if (ddcc.getSelectedItem() != null )
        try {
            
        
            Connection con = DriverManager.getConnection(url + db, user, pass);
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

    private void tbdescFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdescFocusLost
       if (tbdesc.getText().isEmpty()) {
           tbdesc.setBackground(Color.yellow);
       } else {
           tbdesc.setBackground(Color.white);
       }
    }//GEN-LAST:event_tbdescFocusLost

    private void btLookUpAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAccountActionPerformed
        lookUpFrameAcctDesc();
    }//GEN-LAST:event_btLookUpAccountActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpAccount;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteALL;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btsubmit;
    private javax.swing.JTextField dateentered;
    private static javax.swing.JComboBox ddacct;
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
    private javax.swing.JLabel lbacctname;
    private javax.swing.JLabel lbccname;
    private javax.swing.JTextField tbamt;
    private javax.swing.JTextField tbcontrolamt;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbuserid;
    private javax.swing.JTable transtable;
    // End of variables declaration//GEN-END:variables
}
