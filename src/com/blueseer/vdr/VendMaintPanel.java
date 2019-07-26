/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.vdr;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author vaughnte
 */
public class VendMaintPanel extends javax.swing.JPanel {

    public String newcode = "";
    javax.swing.table.DefaultTableModel contactmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "ID", "Type", "Name", "Phone", "Fax", "Email"
            });
    
    
    /**
     * Creates new form VendMaintPanel
     */
    public VendMaintPanel() {
        initComponents();
    }

     public void addContact(String vend) {
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (proceed) {
                    st.executeUpdate("insert into vdc_det "
                        + "(vdc_code, vdc_type, vdc_name, vdc_phone, vdc_fax, "
                            + "vdc_email ) "
                            + " values ( " + "'" + vend + "'" + ","
                            + "'" + ddcontacttype.getSelectedItem().toString() + "'" + ","
                            + "'" + tbcontactname.getText().replace("'", "") + "'" + ","
                            + "'" + tbphone.getText().replace("'", "") + "'" + ","
                            + "'" + tbfax.getText().replace("'", "") + "'" + ","
                            + "'" + tbemail.getText().replace("'", "") + "'"                           
                            + ")"
                            + ";");
        
                   
                    bsmf.MainFrame.show("Added Contact Info");
                    
                  
                    
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Add Contact Info");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void editContact(String vend, String z) {
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (proceed) {
                    st.executeUpdate("update vdc_det set "
                            + "vdc_type = " + "'" + ddcontacttype.getSelectedItem().toString() + "'" + ","
                            + "vdc_name = " + "'" + tbcontactname.getText().replace("'", "") + "'" + ","
                            + "vdc_phone = " + "'" + tbphone.getText().replace("'", "") + "'" + ","
                            + "vdc_fax = " +  "'" + tbfax.getText().replace("'", "") + "'" + ","
                            + "vdc_email = " + "'" + tbemail.getText().replace("'", "") + "'"                           
                            + " where vdc_code = " + "'" + vend + "'"
                            + " and vdc_id = " + "'" + z + "'" 
                            + ";");
        
                   
                    bsmf.MainFrame.show("Updated Contact Info");
                    
                  
                    
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Update Contact Info");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deleteContact(String vend, String z) {
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (proceed) {
                    st.executeUpdate("delete from vdc_det where vdc_id = " + "'" + z + "'"
                            + " AND vdc_code = " + "'" + vend + "'"
                            + ";");
                    bsmf.MainFrame.show("Deleted Contact Info");
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Delete Contact Info");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
     public void refreshContactTable(String vend) {
      contactmodel.setRowCount(0);
       try {
            
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                res = st.executeQuery("select * from vdc_det where vdc_code = " + "'" + vend + "'" + ";");
                while (res.next()) {
                    contactmodel.addRow(new Object[]{res.getString("vdc_id"), res.getString("vdc_type"), res.getString("vdc_name"), res.getString("vdc_phone"), res.getString("vdc_fax"), res.getString("vdc_email") }); 
                }
                contacttable.setModel(contactmodel);
                
                 } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem retrieving contact list");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
     }
    
    public void initvars(String arg) {
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", mainPanel);
        jTabbedPane1.add("Contact", contactPanel);
        
        
       clearVend();
        disableVend();
        disableContact();
        
         contactmodel.setRowCount(0);
        contacttable.setModel(contactmodel);
       
      String[] args = null; 
      if (! arg.isEmpty()) {
            boolean gotIt = getVend(arg);  
            if (gotIt) {
              tbcode.setEditable(false);
              tbcode.setForeground(Color.blue);
             } 
        } else {
              disableVend();
              btnew.setEnabled(true);
              btvendcodebrowse.setEnabled(true);
              btvendzipbrowse.setEnabled(true);
              btvendnamebrowse.setEnabled(true);
              
          }
       
        
    }
    
    
     public boolean getVend(String vend) {
         boolean gotIt = false; 
         try {
           
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                tbname.setText("");
                
                
                res = st.executeQuery("select * from vd_mstr where vd_addr = " + "'" + vend + "'"  + ";");
                while (res.next()) {
                gotIt = true;
                
                tbcode.setText(res.getString("vd_addr"));
                tbname.setText(res.getString("vd_name"));
                tbline1.setText(res.getString("vd_line1"));
                tbline2.setText(res.getString("vd_line2"));
                tbline3.setText(res.getString("vd_line3"));
                tbcity.setText(res.getString("vd_city"));
                ddstate.setSelectedItem(res.getString("vd_state"));
               ddcountry.setSelectedItem(res.getString("vd_country"));
                if (res.getString("vd_country").equals("US")) {
                    ddcountry.setSelectedItem("USA");
                } 
                if (res.getString("vd_country").equals("United States")) {
                    ddcountry.setSelectedItem("USA");
                } 
                 if (res.getString("vd_country").equals("CA")) {
                    ddcountry.setSelectedItem("Canada");
                } 
                tbzip.setText(res.getString("vd_zip"));
                
                tbdateadded.setText(res.getString("vd_dateadd"));
                tbdatemod.setText(res.getString("vd_datemod"));
                tbgroup.setText(res.getString("vd_group"));
                tbmarket.setText(res.getString("vd_market"));
                tbbuyer.setText(res.getString("vd_buyer"));
                ddcarrier.setSelectedItem(res.getString("vd_shipvia"));
                tbmisc.setText(res.getString("vd_misc"));
                ddterms.setSelectedItem(res.getString("vd_terms"));
                tbpricecode.setText(res.getString("vd_price_code"));
                tbdisccode.setText(res.getString("vd_disc_code"));
                tbtaxcode.setText(res.getString("vd_tax_code"));
                ddaccount.setSelectedItem(res.getString("vd_ap_acct"));
                ddcc.setSelectedItem(res.getString("vd_ap_cc"));
                tbremarks.setText(res.getString("vd_remarks"));
                ddbank.setSelectedItem(res.getString("vd_bank"));
                 ddcurr.setSelectedItem(res.getString("vd_curr"));
                
                 contactmodel.setRowCount(0);
            //    res = st.executeQuery("select * from vdc_det where vdc_code = " + "'" + ddvendcode.getSelectedItem() + "'" + ";");
            //    while (res.next()) {
           //         contactmodel.addRow(new Object[]{res.getString("vdc_type"), res.getString("vdc_name"), res.getString("vdc_phone")}); 
            //    }
           //     contacttable.setModel(contactmodel);
                                    
                }
               
               if (gotIt) {
               refreshContactTable(tbcode.getText()); 
               enableVend();
               enableContact();
               btadd.setEnabled(false);
               }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to get selected vendor");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
         
         return gotIt;
     }
    
     public void clearAllContacts() {
        tbcontactname.setText("");
        tbphone.setText("");
        tbfax.setText("");
        tbemail.setText("");
       
     }
     
     public void clearVend() {
         
          java.util.Date now = new java.util.Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        tbdateadded.setText(df.format(now));
        tbdatemod.setText(df.format(now));
        tbcode.setText("");
        tbcode.setForeground(Color.black);
        tbcode.setEditable(true);
        tbname.setText("");
        tbline1.setText("");
        tbline2.setText("");
        tbline3.setText("");
        tbcity.setText("");
        tbzip.setText("");
        tbtaxcode.setText("");
        tbpricecode.setText("");
        tbmarket.setText("");
        tbdisccode.setText("");
        tbbuyer.setText("0");
        
        tbremarks.setText("");
       
        tbsalesrep.setText("");
        tbgroup.setText("");
        tbmarket.setText("");
        tbbuyer.setText("");
        tbpricecode.setText("");
        tbdisccode.setText("");
        tbtaxcode.setText("");
       
        
        ddstate.removeAllItems();
        ArrayList states = OVData.getCodeMstrKeyList("state");
        for (int i = 0; i < states.size(); i++) {
            ddstate.addItem(states.get(i).toString());
        }
        if (ddstate.getItemCount() > 0) {
           ddstate.setSelectedIndex(0); 
        }
        
    
       if (ddcountry.getItemCount() == 0)
       for (int i = 0; i < OVData.countries.length; i++) {
            ddcountry.addItem(OVData.countries[i]);
        }
       
       ddcarrier.removeAllItems();
        ArrayList myscac = OVData.getScacCarrierOnly();   
        for (int i = 0; i < myscac.size(); i++) {
            ddcarrier.addItem(myscac.get(i));
        }
        
       ddterms.removeAllItems();
        ArrayList custterms = OVData.getcusttermslist();
        for (int i = 0; i < custterms.size(); i++) {
            ddterms.addItem(custterms.get(i));
        }
        
        ddcurr.removeAllItems();
        ArrayList<String> curr = OVData.getCurrlist();
        for (int i = 0; i < curr.size(); i++) {
            ddcurr.addItem(curr.get(i));
        }
        
        
        ddbank.removeAllItems();
        ArrayList bank = OVData.getbanklist();
        for (int i = 0; i < bank.size(); i++) {
            ddbank.addItem(bank.get(i));
        }
        
         ddaccount.removeAllItems();
        ArrayList accounts = OVData.getGLAcctList();
        for (int i = 0; i < accounts.size(); i++) {
            ddaccount.addItem(accounts.get(i).toString());
        }
        ddaccount.setSelectedItem(OVData.getDefaultAPAcct());
        
        ddcc.removeAllItems();
        ArrayList ccs = OVData.getGLCCList();
        for (int i = 0; i < ccs.size(); i++) {
            ddcc.addItem(ccs.get(i).toString());
        }
        
         if (ddcurr.getItemCount() > 0)
        ddcurr.setSelectedIndex(0);
         if (ddbank.getItemCount() > 0)
        ddbank.setSelectedIndex(0);
         if (ddcarrier.getItemCount() > 0)
        ddcarrier.setSelectedIndex(0);
        if (ddterms.getItemCount() > 0)
        ddterms.setSelectedIndex(0);
        if (ddcountry.getItemCount() > 0)
        ddcountry.setSelectedItem("USA");
        if (ddstate.getItemCount() > 0)
        ddstate.setSelectedIndex(0);
       
       
        
    }
    
     public void enableVend() {
         tbname.setEnabled(true);
        tbline1.setEnabled(true);
        tbline2.setEnabled(true);
        tbline3.setEnabled(true);
        tbcity.setEnabled(true);
        tbzip.setEnabled(true);
        tbtaxcode.setEnabled(true);
        tbpricecode.setEnabled(true);
        tbmarket.setEnabled(true);
        tbdisccode.setEnabled(true);
        tbbuyer.setEnabled(true);
        
        ddcurr.setEnabled(true);
        ddbank.setEnabled(true);
        ddcarrier.setEnabled(true);
        ddterms.setEnabled(true);
        tbmisc.setEnabled(true);
       
        tbcode.setEnabled(true);
        ddstate.setEnabled(true);
        ddcountry.setEnabled(true);
        btadd.setEnabled(true);
        btedit.setEnabled(true);
        btdelete.setEnabled(true);
        
        btvendcodebrowse.setEnabled(true);
        btvendnamebrowse.setEnabled(true);
        btvendzipbrowse.setEnabled(true);
        
        tbremarks.setEnabled(true);
        ddaccount.setEnabled(true);
        ddcc.setEnabled(true);
        tbsalesrep.setEnabled(true);
        tbgroup.setEnabled(true);
        tbmarket.setEnabled(true);
        tbbuyer.setEnabled(true);
        tbpricecode.setEnabled(true);
        tbdisccode.setEnabled(true);
        tbtaxcode.setEnabled(true);
     }
     
     public void disableVend() {
           tbname.setEnabled(false);
        tbline1.setEnabled(false);
        tbline2.setEnabled(false);
        tbline3.setEnabled(false);
        tbcity.setEnabled(false);
        tbzip.setEnabled(false);
        tbtaxcode.setEnabled(false);
        tbpricecode.setEnabled(false);
        tbmarket.setEnabled(false);
        tbdisccode.setEnabled(false);
        tbbuyer.setEnabled(false);
        
        tbremarks.setEnabled(false);
        ddaccount.setEnabled(false);
        ddcc.setEnabled(false);
        
        ddcurr.setEnabled(false);
        ddbank.setEnabled(false);
        ddcarrier.setEnabled(false);
        ddterms.setEnabled(false);
        tbmisc.setEnabled(false);
        tbdateadded.setEnabled(false);
        tbdatemod.setEnabled(false);
       // tbcode.setEnabled(false);
        ddstate.setEnabled(false);
        ddcountry.setEnabled(false);
        btadd.setEnabled(false);
        btedit.setEnabled(false);
        btdelete.setEnabled(false);
        btvendcodebrowse.setEnabled(false);
        btvendnamebrowse.setEnabled(false);
        btvendzipbrowse.setEnabled(false);
        
        tbsalesrep.setEnabled(false);
        tbgroup.setEnabled(false);
        tbmarket.setEnabled(false);
        tbbuyer.setEnabled(false);
        tbpricecode.setEnabled(false);
        tbdisccode.setEnabled(false);
        tbtaxcode.setEnabled(false);
     }
     
     public void enableContact() {
            tbcontactname.setEnabled(true);
        tbphone.setEnabled(true);
        tbfax.setEnabled(true);
        tbemail.setEnabled(true);
        ddcontacttype.setEnabled(true);
        btAddContact.setEnabled(true);
        btDeleteContact.setEnabled(true);
        btEditContact.setEnabled(true);
        contacttable.setEnabled(true);
     }
     
     public void disableContact() {
             tbcontactname.setEnabled(false);
        tbphone.setEnabled(false);
        tbfax.setEnabled(false);
        tbemail.setEnabled(false);
        
        ddcontacttype.setEnabled(false);
        btAddContact.setEnabled(false);
        btDeleteContact.setEnabled(false);
        btEditContact.setEnabled(false);
        contacttable.setEnabled(false);
     }
     
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ddcountry = new javax.swing.JComboBox();
        ddstate = new javax.swing.JComboBox();
        tbline2 = new javax.swing.JTextField();
        tbline3 = new javax.swing.JTextField();
        tbname = new javax.swing.JTextField();
        tbline1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbcode = new javax.swing.JTextField();
        btvendcodebrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        btvendnamebrowse = new javax.swing.JButton();
        btvendzipbrowse = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        tbdatemod = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbmarket = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tbdateadded = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        tbgroup = new javax.swing.JTextField();
        tbbuyer = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        ddterms = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        ddcarrier = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        tbdisccode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        tbpricecode = new javax.swing.JTextField();
        tbtaxcode = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        tbsalesrep = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbremarks = new javax.swing.JTextArea();
        ddbank = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        ddaccount = new javax.swing.JComboBox<>();
        ddcc = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        tbmisc = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btedit = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        contactPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        contacttable = new javax.swing.JTable();
        btDeleteContact = new javax.swing.JButton();
        tbcontactname = new javax.swing.JTextField();
        btAddContact = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        tbemail = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        ddcontacttype = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        tbfax = new javax.swing.JTextField();
        btEditContact = new javax.swing.JButton();

        jTextField1.setText("jTextField1");

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Vendor Master Maintenance"));

        jLabel7.setText("State");

        jLabel5.setText("Line3");

        jLabel8.setText("Zip/Post Code");

        jLabel3.setText("Line1");

        jLabel9.setText("Country");

        jLabel4.setText("Line2");

        jLabel1.setText("VendCode");

        jLabel2.setText("Name");

        jLabel6.setText("City");

        tbcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcodeActionPerformed(evt);
            }
        });

        btvendcodebrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btvendcodebrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btvendcodebrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btvendnamebrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btvendnamebrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btvendnamebrowseActionPerformed(evt);
            }
        });

        btvendzipbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btvendzipbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btvendzipbrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tbline3, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbline2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbline1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btvendzipbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btvendnamebrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btvendcodebrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(btvendcodebrowse)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(btvendnamebrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbline1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbline2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbline3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addComponent(btvendzipbrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbdatemod.setEnabled(false);

        jLabel10.setText("DateAdded");

        jLabel13.setText("Group");

        jLabel12.setText("Market");

        jLabel11.setText("LastMod");

        jLabel14.setText("Buyer");

        jLabel15.setText("Terms");

        jLabel16.setText("Carrier");

        jLabel18.setText("Disc Code");

        jLabel20.setText("Tax Code");

        jLabel26.setText("SalesRep");

        jLabel27.setText("AP Account");

        jLabel28.setText("CostCenter");

        jLabel29.setText("Remarks");

        tbremarks.setColumns(20);
        tbremarks.setRows(5);
        jScrollPane2.setViewportView(tbremarks);

        jLabel30.setText("Bank");

        jLabel31.setText("Price Code");

        jLabel32.setText("Misc");

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel33.setText("Currency");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel29)
                            .addComponent(jLabel13)
                            .addComponent(jLabel30)
                            .addComponent(jLabel31)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27)
                            .addComponent(jLabel32)
                            .addComponent(jLabel10)
                            .addComponent(jLabel16)
                            .addComponent(jLabel33))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ddcurr, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbpricecode, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbsalesrep, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddaccount, 0, 120, Short.MAX_VALUE)
                                    .addComponent(tbmisc, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbdateadded, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbgroup, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddbank, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddcarrier, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(68, 68, 68)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tbdatemod)
                                    .addComponent(tbmarket)
                                    .addComponent(tbbuyer)
                                    .addComponent(tbtaxcode)
                                    .addComponent(tbdisccode)
                                    .addComponent(ddterms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(62, 62, 62))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tbdatemod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(tbdateadded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbmarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(tbbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(19, 19, 19)
                        .addComponent(jLabel14))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(tbgroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(ddbank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcarrier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(ddterms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(tbdisccode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbtaxcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbsalesrep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddaccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btedit)
                    .addComponent(btdelete))
                .addGap(7, 7, 7))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        add(mainPanel);

        contactPanel.setPreferredSize(new java.awt.Dimension(938, 421));

        contacttable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Type", "Name", "Phone", "Fax", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        contacttable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contacttableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(contacttable);

        btDeleteContact.setText("DeleteContact");
        btDeleteContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDeleteContactActionPerformed(evt);
            }
        });

        btAddContact.setText("AddContact");
        btAddContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddContactActionPerformed(evt);
            }
        });

        jLabel24.setText("ContactType");

        jLabel21.setText("ContactName");

        ddcontacttype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sales", "Finance", "IT", "Admin", "Shipping", "Engineering", "Quality" }));

        jLabel23.setText("Email");

        jLabel22.setText("Phone");

        jLabel25.setText("Fax");

        btEditContact.setText("EditContact");
        btEditContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEditContactActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contactPanelLayout = new javax.swing.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(contactPanelLayout.createSequentialGroup()
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                .addComponent(ddcontacttype, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbcontactname))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                .addComponent(tbfax, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btEditContact)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btAddContact)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btDeleteContact))
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        contactPanelLayout.setVerticalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(ddcontacttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(tbfax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btDeleteContact)
                    .addComponent(btAddContact)
                    .addComponent(btEditContact))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(tbcontactname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(257, Short.MAX_VALUE))
        );

        add(contactPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                
                
           //   if ( ! tbbuyer.getText().toString().matches("\\d+") ) {
           //   proceed = false;
           //  bsmf.MainFrame.show("Invalid credit limit...must be integer");
          //   }
              if (OVData.isValidVendor(tbcode.getText())) {
                  bsmf.MainFrame.show("Vendor Code already in use");
                  return;
              } 
                
                if (ddaccount.getSelectedItem().toString().isEmpty()) {
                    bsmf.MainFrame.show("Must assign Account");
                    return;
                }
                if (ddcc.getSelectedItem().toString().isEmpty()) {
                    bsmf.MainFrame.show("Must assign CostCenter");
                    return;
                }

                if (proceed) {
                    st.executeUpdate("insert into vd_mstr "
                        + "(vd_addr, vd_name, vd_line1, vd_line2, "
                        + "vd_line3, vd_city, vd_state, vd_zip, "
                        + "vd_country, vd_dateadd, vd_datemod, vd_usermod, "
                        + "vd_group, vd_market, vd_buyer, "
                        + "vd_shipvia, vd_terms, vd_misc, vd_price_code, "
                        + "vd_disc_code, vd_tax_code,  "
                        + "vd_ap_acct, vd_ap_cc, vd_bank, vd_curr, vd_remarks "
                        + " ) "
                        + " values ( " + "'" + tbcode.getText() + "'" + ","
                        + "'" + tbname.getText().replace("'", "") + "'" + ","
                        + "'" + tbline1.getText().replace("'", "") + "'" + ","
                        + "'" + tbline2.getText().replace("'", "") + "'" + ","
                        + "'" + tbline3.getText().replace("'", "") + "'" + ","
                        + "'" + tbcity.getText() + "'" + ","
                        + "'" + ddstate.getSelectedItem().toString() + "'" + ","
                        + "'" + tbzip.getText() + "'" + ","
                        + "'" + ddcountry.getSelectedItem().toString() + "'" + ","
                        + "'" + tbdateadded.getText() + "'" + ","
                        + "'" + tbdatemod.getText() + "'" + ","
                            + "'" + bsmf.MainFrame.userid + "'" + ","
                            + "'" + tbgroup.getText() + "'" + ","
                            + "'" + tbmarket.getText() + "'" + ","
                            + "'" + tbbuyer.getText() + "'" + ","
                           + "'" + ddcarrier.getSelectedItem().toString() + "'" + ","
                           + "'" + ddterms.getSelectedItem() + "'" + ","
                           + "'" + tbmisc.getText() + "'" + ","
                            + "'" + tbpricecode.getText() + "'" + ","
                            + "'" + tbdisccode.getText() + "'" + ","
                            + "'" + tbtaxcode.getText() + "'" + ","
                            + "'" + ddaccount.getSelectedItem().toString() + "'" + ","
                            + "'" + ddcc.getSelectedItem().toString() + "'" + ","
                            + "'" + ddbank.getSelectedItem().toString() + "'" + ","
                            + "'" + ddcurr.getSelectedItem().toString() + "'" + ","        
                            + "'" + tbremarks.getText().replace("'", "") + "'"                            
                        + ")"
                        + ";");
        
                    bsmf.MainFrame.show("Added Vendor Record");
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Add Vendor");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btAddContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddContactActionPerformed
                         addContact(tbcode.getText());
                         refreshContactTable(tbcode.getText());
    }//GEN-LAST:event_btAddContactActionPerformed

    private void btDeleteContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDeleteContactActionPerformed
          int[] rows = contacttable.getSelectedRows();
        for (int i : rows) {
           deleteContact(tbcode.getText(), contacttable.getValueAt(i, 0).toString());
        }
       refreshContactTable(tbcode.getText());
       clearAllContacts();
    }//GEN-LAST:event_btDeleteContactActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        boolean canproceed = true;
        java.util.Date now = new java.util.Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
       
        
        if (canproceed) {
           
           try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
               st.executeUpdate("update vd_mstr set " + 
                       " vd_name = " + "'" + tbname.getText() + "'" + "," +
                       " vd_line1 = " + "'" + tbline1.getText() + "'" + "," +
                       " vd_line2 = " + "'" + tbline2.getText() + "'" + "," +
                       " vd_line3 = " + "'" + tbline3.getText() + "'" + "," +
                       " vd_city = " + "'" + tbcity.getText() + "'" + "," +
                       " vd_state = " + "'" + ddstate.getSelectedItem().toString() + "'" + "," +
                       " vd_zip = " + "'" + tbzip.getText() + "'" + "," +
                       " vd_country = " + "'" + ddcountry.getSelectedItem().toString() + "'" + "," +
                       " vd_datemod = " + "'" + df.format(now) + "'" + "," +
                       " vd_usermod = " + "'" + bsmf.MainFrame.userid + "'" + "," +
                       " vd_group = "  + "'" + tbgroup.getText() + "'" + "," +
                       " vd_market = " + "'" + tbmarket.getText() + "'" + "," +
                       " vd_buyer = " + "'" + tbbuyer.getText() + "'" + "," +
                       " vd_shipvia = " + "'" + ddcarrier.getSelectedItem() + "'" + "," +
                       " vd_terms = " + "'" + ddterms.getSelectedItem() + "'" + "," +
                       " vd_misc = " + "'" + tbmisc.getText() + "'" + "," +
                       " vd_price_code = " + "'" + tbpricecode.getText() + "'" + "," +
                       " vd_disc_code = " + "'" + tbdisccode.getText() + "'" + "," +
                       " vd_tax_code = " + "'" + tbtaxcode.getText() + "'" + "," +
                       " vd_ap_acct = " + "'" + ddaccount.getSelectedItem().toString() + "'" + "," +
                       " vd_ap_cc = " + "'" + ddcc.getSelectedItem().toString() + "'" + "," +
                       " vd_bank = " + "'" + ddbank.getSelectedItem() + "'" + "," +
                       " vd_curr = " + "'" + ddcurr.getSelectedItem() + "'" + "," +        
                       " vd_remarks = " + "'" + tbremarks.getText() + "'" +                      
                       " where " + 
                        " vd_addr = " + "'" + tbcode.getText() + "'" +  ";");

               
               
               bsmf.MainFrame.show("Updated Vendor Successfully");
              
               initvars("");
               
            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to get selected vend code");
            }
              } catch (Exception e) {
            e.printStackTrace();
        }   
                
           
       } else {
           bsmf.MainFrame.show("Partially or Completely approved...cannot edit");
       }
       
    }//GEN-LAST:event_bteditActionPerformed

    private void btvendcodebrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btvendcodebrowseActionPerformed
        reinitpanels("BrowseUtil", true, "vendmaint,vd_addr");
    }//GEN-LAST:event_btvendcodebrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        enableVend();
        clearVend();
        disableContact();
        btedit.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        btvendcodebrowse.setEnabled(false);
        btvendnamebrowse.setEnabled(false);
        btvendzipbrowse.setEnabled(false);
        
          if (OVData.isAutoVend()) {
        tbcode.setText(String.valueOf(OVData.getNextNbr("vendor")));
        tbcode.setEditable(false);
        } else {
              tbcode.requestFocus();
         }
    }//GEN-LAST:event_btnewActionPerformed

    private void btEditContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditContactActionPerformed
        int[] rows = contacttable.getSelectedRows();
        for (int i : rows) {
            editContact(tbcode.getText(), contacttable.getValueAt(i, 0).toString());
        }
        refreshContactTable(tbcode.getText());
        clearAllContacts();
    }//GEN-LAST:event_btEditContactActionPerformed

    private void contacttableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contacttableMouseClicked
        int row = contacttable.rowAtPoint(evt.getPoint());
        ddcontacttype.setSelectedItem(contacttable.getValueAt(row, 1).toString());
        tbcontactname.setText(contacttable.getValueAt(row, 2).toString());
        tbphone.setText(contacttable.getValueAt(row, 3).toString());
        tbfax.setText(contacttable.getValueAt(row, 4).toString());
        tbemail.setText(contacttable.getValueAt(row, 5).toString());
    }//GEN-LAST:event_contacttableMouseClicked

    private void btvendnamebrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btvendnamebrowseActionPerformed
       reinitpanels("BrowseUtil", true, "vendmaint,vd_name");
    }//GEN-LAST:event_btvendnamebrowseActionPerformed

    private void btvendzipbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btvendzipbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "vendmaint,vd_zip");
    }//GEN-LAST:event_btvendzipbrowseActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
           boolean proceed = bsmf.MainFrame.warn("This is not advised.  Deleting Vendors with historical transactions may cause issues.");
           proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from vd_mstr where vd_addr = " + "'" + tbcode.getText() + "'" + ";");
                   st.executeUpdate("delete from vpr_mstr where vpr_vend = " + "'" + tbcode.getText() + "'" + ";");
                   st.executeUpdate("delete from vdp_mstr where vdp_vend = " + "'" + tbcode.getText() + "'" + ";");
                   st.executeUpdate("delete from vdc_det where vdc_code = " + "'" + tbcode.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + tbcode.getText());
                    initvars("");
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete Vendor Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcodeActionPerformed
         boolean gotIt = getVend(tbcode.getText());
        if (gotIt) {
          tbcode.setEditable(false);
          tbcode.setForeground(Color.blue);
        } else {
           tbcode.setForeground(Color.red); 
        }
    }//GEN-LAST:event_tbcodeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddContact;
    private javax.swing.JButton btDeleteContact;
    private javax.swing.JButton btEditContact;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btvendcodebrowse;
    private javax.swing.JButton btvendnamebrowse;
    private javax.swing.JButton btvendzipbrowse;
    private javax.swing.JPanel contactPanel;
    private javax.swing.JTable contacttable;
    private javax.swing.JComboBox<String> ddaccount;
    private javax.swing.JComboBox ddbank;
    private javax.swing.JComboBox ddcarrier;
    private javax.swing.JComboBox<String> ddcc;
    private javax.swing.JComboBox ddcontacttype;
    private javax.swing.JComboBox ddcountry;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox ddterms;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField tbbuyer;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcode;
    private javax.swing.JTextField tbcontactname;
    private javax.swing.JTextField tbdateadded;
    private javax.swing.JTextField tbdatemod;
    private javax.swing.JTextField tbdisccode;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbfax;
    private javax.swing.JTextField tbgroup;
    private javax.swing.JTextField tbline1;
    private javax.swing.JTextField tbline2;
    private javax.swing.JTextField tbline3;
    private javax.swing.JTextField tbmarket;
    private javax.swing.JTextField tbmisc;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbpricecode;
    private javax.swing.JTextArea tbremarks;
    private javax.swing.JTextField tbsalesrep;
    private javax.swing.JTextField tbtaxcode;
    private javax.swing.JTextField tbzip;
    // End of variables declaration//GEN-END:variables
}
