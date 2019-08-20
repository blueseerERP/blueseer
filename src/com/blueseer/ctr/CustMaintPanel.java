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
package com.blueseer.ctr;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.ord.OrderMaintPanel;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;


/**
 *
 * @author vaughnte
 */
public class CustMaintPanel extends javax.swing.JPanel {

    
    javax.swing.table.DefaultTableModel contactmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
               "ID", "Type", "Name", "Phone", "Fax", "Email"
            });
    /**
     * Creates new form CustMaintPanel
     */
    public CustMaintPanel() {
        initComponents();
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
                case "add":
                    message = addRecord();
                    break;
                case "edit":
                    message = updateRecord();
                    break;
                case "delete":
                    message = deleteRecord();    
                    break;
                default:
                    message = new String[]{"1", "unknown action"};
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
           if (this.type.equals("delete")) {
             initvars(null);  
           }  else {
             initvars(new String[]{tbcustcode.getText()});  
           }
           
            
            } catch (Exception e) {
               MainFrame.bslog(e);
            } 
           
        }
    }  
     
     public boolean validateInput(String action) {
         boolean r = true;
          if ( tbcustcode.getText().isEmpty()) {
                  r = false;
                  bsmf.MainFrame.show("Must provide valid CustCode");
              }  
              if (action.equals("add") && OVData.isValidCustomer(tbcustcode.getText())) {
                  r = false;
                  bsmf.MainFrame.show("CustCode already in use");
                 
              }  
              
              if ( ! OVData.isValidGLAcct(ddaccount.getSelectedItem().toString())) {
                  r = false;
                  bsmf.MainFrame.show("Invalid Account Code");
                  
              }
              
              if ( ! OVData.isValidGLcc(ddcc.getSelectedItem().toString())) {
                  r = false;
                  bsmf.MainFrame.show("Invalid CC / Dept Code");
                  
              }
                
              if ( ! tbcreditlimit.getText().toString().matches("\\d+") ) {
              r = false;
             bsmf.MainFrame.show("Invalid credit limit...must be integer");
            
             }
              return r;
     }
     
     public String[] addRecord() {
     String[] message = new String[2];
     try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
               
                    st.executeUpdate("insert into cm_mstr "
                        + "(cm_code, cm_name, cm_line1, cm_line2, "
                        + "cm_line3, cm_city, cm_state, cm_zip, "
                        + "cm_country, cm_dateadd, cm_datemod, cm_usermod, "
                        + "cm_group, cm_market, cm_creditlimit, cm_onhold, "
                        + "cm_carrier, cm_terms, cm_freight_type, cm_price_code, "
                        + "cm_disc_code, cm_tax_code, cm_salesperson, "
                        + "cm_ar_acct, cm_ar_cc, cm_bank, cm_curr, cm_remarks, cm_label, cm_ps_jasper, cm_iv_jasper "
                        + " ) "
                        + " values ( " + "'" + tbcustcode.getText() + "'" + ","
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
                            + "'" + tbcreditlimit.getText() + "'" + ","
                           + "'" + BlueSeerUtils.boolToInt(cbonhold.isSelected()) + "'" + ","
                           + "'" + ddcarrier.getSelectedItem().toString() + "'" + ","
                           + "'" + ddterms.getSelectedItem().toString() + "'" + ","
                           + "'" + ddfreightterms.getSelectedItem().toString() + "'" + ","
                            + "'" + tbpricecode.getText() + "'" + ","
                            + "'" + tbdisccode.getText() + "'" + ","
                            + "'" + ddtax.getSelectedItem().toString() + "'" + ","
                            + "'" + tbsalesrep.getText().replace("'", "") + "'" + ","
                            + "'" + ddaccount.getSelectedItem().toString() + "'" + ","
                            + "'" + ddcc.getSelectedItem().toString() + "'" + ","
                            + "'" + ddbank.getSelectedItem().toString() + "'" + ","
                            + "'" + ddcurr.getSelectedItem().toString() + "'" + ","        
                            + "'" + tbremarks.getText().replace("'", "") + "'" + ","  
                           + "'" + ddlabel.getSelectedItem().toString() + "'" + ","
                           + "'" + tbshpformat.getText().replace("'", "") + "'" + ","
                           + "'" + tbinvformat.getText().replace("'", "") + "'"
                        + ")"
                        + ";");

                    for (int j = 0; j < contacttable.getRowCount(); j++) {
                        st.executeUpdate("insert into cmc_det "
                            + "(cmc_code, cmc_type, cmc_name, cmc_phone, cmc_fax, "
                            + "cmc_email ) "
                            + " values ( " + "'" + tbcustcode.getText() + "'" + ","
                            + "'" + contacttable.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + contacttable.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + "'" + contacttable.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                            + "'" + contacttable.getValueAt(j, 3).toString().replace("'", "") + "'" + ","
                            + "'" + contacttable.getValueAt(j, 4).toString().replace("'", "") + "'"                           
                            + ")"
                            + ";");

                    }
                    if (cbshipto.isSelected())        
                    addShipTo(tbcustcode.getText());
                     message = new String[]{"0", "Customer has been added"};   
                    initvars(null);
                    // btQualProbAdd.setEnabled(false);
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                 message = new String[]{"1", "Customer cannot be added"};   
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     return message;
     }
    
     public String[] updateRecord() {
        String[] message = new String[2];
           
           try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
               st.executeUpdate("update cm_mstr set " + 
                       " cm_name = " + "'" + tbname.getText() + "'" + "," +
                       " cm_line1 = " + "'" + tbline1.getText() + "'" + "," +
                       " cm_line2 = " + "'" + tbline2.getText() + "'" + "," +
                       " cm_line3 = " + "'" + tbline3.getText() + "'" + "," +
                       " cm_city = " + "'" + tbcity.getText() + "'" + "," +
                       " cm_state = " + "'" + ddstate.getSelectedItem().toString() + "'" + "," +
                       " cm_zip = " + "'" + tbzip.getText() + "'" + "," +
                       " cm_country = " + "'" + ddcountry.getSelectedItem().toString() + "'" + "," +
                       " cm_datemod = " + "'" + tbdatemod.getText() + "'" + "," +
                       " cm_usermod = " + "'" + bsmf.MainFrame.userid + "'" + "," +
                       " cm_group = "  + "'" + tbgroup.getText() + "'" + "," +
                       " cm_market = " + "'" + tbmarket.getText() + "'" + "," +
                       " cm_creditlimit = " + "'" + tbcreditlimit.getText() + "'" + "," +
                       " cm_onhold = " + "'" + BlueSeerUtils.boolToInt(cbonhold.isSelected()) + "'" + "," +
                       " cm_carrier = " + "'" + ddcarrier.getSelectedItem().toString() + "'" + "," +
                       " cm_terms = " + "'" + ddterms.getSelectedItem().toString() + "'" + "," +
                       " cm_freight_type = " + "'" + ddfreightterms.getSelectedItem().toString() + "'" + "," +
                       " cm_price_code = " + "'" + tbpricecode.getText() + "'" + "," +
                       " cm_disc_code = " + "'" + tbdisccode.getText() + "'" + "," +
                       " cm_tax_code = " + "'" + ddtax.getSelectedItem().toString() + "'" + "," +
                       " cm_salesperson = " + "'" + tbsalesrep.getText().replace("'", "") + "'" + "," +
                       " cm_ar_acct = " + "'" + ddaccount.getSelectedItem().toString() + "'" + "," +
                       " cm_ar_cc = " + "'" + ddcc.getSelectedItem().toString() + "'" + "," +
                       " cm_bank = " + "'" + ddbank.getSelectedItem().toString() + "'" + "," +
                        " cm_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'" + "," +        
                       " cm_label = " + "'" + ddlabel.getSelectedItem().toString() + "'" + "," +
                       " cm_ps_jasper = " + "'" + tbshpformat.getText() + "'" + "," +
                       " cm_iv_jasper = " + "'" + tbinvformat.getText() + "'" + "," +
                       " cm_remarks = " + "'" + tbremarks.getText() + "'" +                      
                       " where " + 
                        " cm_code = " + "'" + tbcustcode.getText() + "'" +  ";");

                              
               message = new String[]{"0", "Customer has been updated"};   
              
               initvars(null);
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                 message = new String[]{"1", "Unable to update Customer"};   
            }
              } catch (Exception e) {
            MainFrame.bslog(e);
        }   
           
      
        return message;
     }
     
     public String[] deleteRecord() {
        String[] message = new String[2];
          
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from cm_mstr where cm_code = " + "'" + tbcustcode.getText() + "'" + ";");
                   st.executeUpdate("delete from cpr_mstr where cpr_cust = " + "'" + tbcustcode.getText() + "'" + ";");
                   st.executeUpdate("delete from cup_mstr where cup_cust = " + "'" + tbcustcode.getText() + "'" + ";");
                   st.executeUpdate("delete from cms_det where cms_code = " + "'" + tbcustcode.getText() + "'" + ";");
                   st.executeUpdate("delete from cmc_det where cmc_code = " + "'" + tbcustcode.getText() + "'" + ";");
                    if (i > 0) {
                        message = new String[]{"0", "Deleted code: " + tbcustcode.getText() };
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    message = new String[]{"1", "Unable to delete code: " + tbcustcode.getText() };
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
        return message;
     }
     
    public void addShipTo() {
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (proceed) {
                    st.executeUpdate("insert into cms_det "
                        + "(cms_code, cms_shipto, cms_name, cms_line1, cms_line2, "
                        + "cms_line3, cms_city, cms_state, cms_zip, "
                        + "cms_country "
                        + " ) "
                        + " values ( " + "'" + tbcustcode.getText() + "'" + ","
                        + "'" + tbshipcode.getText() + "'" + ","
                        + "'" + tbshipname.getText().replace("'", "") + "'" + ","
                        + "'" + tbshipline1.getText().replace("'", "") + "'" + ","
                        + "'" + tbshipline2.getText().replace("'", "") + "'" + ","
                        + "'" + tbshipline3.getText().replace("'", "") + "'" + ","
                        + "'" + tbshipcity.getText() + "'" + ","
                        + "'" + ddshipstate.getSelectedItem() + "'" + ","
                        + "'" + tbshipzip.getText() + "'" + ","
                        + "'" + ddshipcountry.getSelectedItem() + "'"                           
                        + ")"
                        + ";");
        
                    if (! cbshipto.isSelected())
                    bsmf.MainFrame.show("Added Customer Shipto Record");
                    
                  
                    
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Add Customer Shipto");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void addShipTo(String cust) {
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                
                       
                
                if (proceed) {
                    st.executeUpdate("insert into cms_det "
                        + "(cms_code, cms_shipto, cms_name, cms_line1, cms_line2, "
                        + "cms_line3, cms_city, cms_state, cms_zip, "
                        + "cms_country "
                        + " ) "
                        + " values ( " + "'" + cust + "'" + ","
                        + "'" + cust + "'" + ","
                        + "'" + tbname.getText().replace("'", "") + "'" + ","
                        + "'" + tbline1.getText().replace("'", "") + "'" + ","
                        + "'" + tbline2.getText().replace("'", "") + "'" + ","
                        + "'" + tbline3.getText().replace("'", "") + "'" + ","
                        + "'" + tbcity.getText() + "'" + ","
                        + "'" + ddstate.getSelectedItem() + "'" + ","
                        + "'" + tbzip.getText() + "'" + ","
                        + "'" + ddcountry.getSelectedItem() + "'"                           
                        + ")"
                        + ";");
        
                    if (! cbshipto.isSelected())
                    bsmf.MainFrame.show("Added Customer Shipto Record");
                    
                  
                    
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Add Customer Shipto");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void addContact(String cust) {
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (proceed) {
                    st.executeUpdate("insert into cmc_det "
                        + "(cmc_code, cmc_type, cmc_name, cmc_phone, cmc_fax, "
                            + "cmc_email ) "
                            + " values ( " + "'" + cust + "'" + ","
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
            MainFrame.bslog(e);
        }
    }
    
    public void editContact(String cust, String z) {
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (proceed) {
                    st.executeUpdate("update cmc_det set "
                            + "cmc_type = " + "'" + ddcontacttype.getSelectedItem().toString() + "'" + ","
                            + "cmc_name = " + "'" + tbcontactname.getText().replace("'", "") + "'" + ","
                            + "cmc_phone = " + "'" + tbphone.getText().replace("'", "") + "'" + ","
                            + "cmc_fax = " +  "'" + tbfax.getText().replace("'", "") + "'" + ","
                            + "cmc_email = " + "'" + tbemail.getText().replace("'", "") + "'"                           
                            + " where cmc_code = " + "'" + cust + "'"
                            + " and cmc_id = " + "'" + z + "'" 
                            + ";");
        
                   
                    bsmf.MainFrame.show("Updated Contact Info");
                    
                  
                    
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Update Contact Info");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void deleteContact(String cust, String z) {
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (proceed) {
                    st.executeUpdate("delete from cmc_det where cmc_id = " + "'" + z + "'"
                            + " AND cmc_code = " + "'" + cust + "'"
                            + ";");
                    bsmf.MainFrame.show("Deleted Contact Info");
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Delete Contact Info");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void initvars(String[] arg) {
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", mainPanel);
        jTabbedPane1.add("ShipTo", shiptoPanel);
        jTabbedPane1.add("Contact", contactPanel);
        
        
        clearCust();
        clearShipTo();
        disableCust();
        disableShipTo();
        disableContact();
        
         contactmodel.setRowCount(0);
        contacttable.setModel(contactmodel);
       
     
      if (arg != null && arg.length > 0) {
            if (arg.length > 1) {
              getbillto(arg[0]);
              getshipto(arg[0], arg[1]);
              jTabbedPane1.setSelectedIndex(1); 
              enableShipTo();
              btshipadd.setEnabled(false);
              tbshipcode.setEnabled(false);
            } else {
              getbillto(arg[0]);  
              disableShipTo();
              btshipnew.setEnabled(true);
              btshiptobrowse.setEnabled(true);
            }
        } else {
              disableCust();
              disableShipTo();
              btnew.setEnabled(true);
              btcustcodebrowse.setEnabled(true);
          }
       
    }
    
    public boolean getbillto(String arg) {
        boolean gotIt = false;
        try {
            
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
                res = st.executeQuery("select * from cm_mstr where cm_code = " + "'" + arg + "'"  + ";");
                while (res.next()) {
                 gotIt = true;
                 tbcustcode.setForeground(Color.blue);
                 tbcustcode.setEditable(false);
                tbcustcode.setText(res.getString("cm_code"));
                tbname.setText(res.getString("cm_name"));
                tbline1.setText(res.getString("cm_line1"));
                tbline2.setText(res.getString("cm_line2"));
                tbline3.setText(res.getString("cm_line3"));
                tbcity.setText(res.getString("cm_city"));
                ddstate.setSelectedItem(res.getString("cm_state"));
                ddcountry.setSelectedItem(res.getString("cm_country"));
                if (res.getString("cm_country").equals("US")) {
                    ddcountry.setSelectedItem("USA");
                } 
                if (res.getString("cm_country").equals("United States")) {
                    ddcountry.setSelectedItem("USA");
                } 
                 if (res.getString("cm_country").equals("CA")) {
                    ddcountry.setSelectedItem("Canada");
                } 
                tbzip.setText(res.getString("cm_zip"));
                
                tbdateadded.setText(res.getString("cm_dateadd"));
                tbdatemod.setText(res.getString("cm_datemod"));
                tbgroup.setText(res.getString("cm_group"));
                tbmarket.setText(res.getString("cm_market"));
                tbcreditlimit.setText(res.getString("cm_creditlimit"));
                //if (res.getString("cm_onhold") != null) {
                cbonhold.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("cm_onhold")));
               // }
                ddcarrier.setSelectedItem(res.getString("cm_carrier"));
                ddbank.setSelectedItem(res.getString("cm_bank"));
                ddcurr.setSelectedItem(res.getString("cm_curr"));
                ddlabel.setSelectedItem(res.getString("cm_label"));
                tbinvformat.setText(res.getString("cm_iv_jasper"));
                tbshpformat.setText(res.getString("cm_ps_jasper"));
                ddfreightterms.setSelectedItem(res.getString("cm_freight_type"));
                ddterms.setSelectedItem(res.getString("cm_terms"));
                
                tbpricecode.setText(res.getString("cm_price_code"));
                tbdisccode.setText(res.getString("cm_disc_code"));
                ddtax.setSelectedItem(res.getString("cm_tax_code"));
                tbsalesrep.setText(res.getString("cm_salesperson"));
                ddaccount.setSelectedItem(res.getString("cm_ar_acct"));
                ddcc.setSelectedItem(res.getString("cm_ar_cc"));
                tbremarks.setText(res.getString("cm_remarks"));
                
                
                                    
                }
            
                
            if (gotIt) {
                enableCust();
                enableContact();
                refreshContactTable(tbcustcode.getText());    
                btedit.setEnabled(true);
                btdelete.setEnabled(true);
                btadd.setEnabled(false);
            }    
            
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem retrieving customer list");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       return gotIt; 
    }
    
     public void getshipto(String cust, String ship) {
        
        try {
            
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
                res = st.executeQuery("select * from cms_det where cms_code = " + "'" + cust + "'"  + 
                        " AND cms_shipto = " + "'" + ship + "'" + ";");
                while (res.next()) {
                tbshipcode.setText(res.getString("cms_shipto"));
                tbshipname.setText(res.getString("cms_name"));
                tbshipline1.setText(res.getString("cms_line1"));
                tbshipline2.setText(res.getString("cms_line2"));
                tbshipline3.setText(res.getString("cms_line3"));
                tbshipcity.setText(res.getString("cms_city"));
                ddshipstate.setSelectedItem(res.getString("cms_state"));
                ddshipcountry.setSelectedItem(res.getString("cms_country"));
                if (res.getString("cms_country").equals("US")) {
                    ddshipcountry.setSelectedItem("USA");
                } 
                if (res.getString("cms_country").equals("United States")) {
                    ddshipcountry.setSelectedItem("USA");
                } 
                 if (res.getString("cms_country").equals("CA")) {
                    ddshipcountry.setSelectedItem("Canada");
                } 
                tbshipzip.setText(res.getString("cms_zip"));
              
                }
            btshipedit.setEnabled(true);
           
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem retrieving shipto list");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
     public void refreshContactTable(String cust) {
      contactmodel.setRowCount(0);
       try {
            
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                res = st.executeQuery("select * from cmc_det where cmc_code = " + "'" + cust + "'" + ";");
                while (res.next()) {
                    contactmodel.addRow(new Object[]{res.getString("cmc_id"), res.getString("cmc_type"), res.getString("cmc_name"), res.getString("cmc_phone"), res.getString("cmc_fax"), res.getString("cmc_email") }); 
                }
                contacttable.setModel(contactmodel);
                
                 } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem retrieving contact list");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
     }
     
     
     public void clearShipTo() {
         tbshipname.setText("");
       tbshipline1.setText("");
       tbshipline2.setText("");
       tbshipline3.setText("");
       tbshipcity.setText("");
       tbshipzip.setText("");
       tbshipcode.setText("");
       
    ddshipstate.removeAllItems();
        ArrayList states = OVData.getCodeMstrKeyList("state");
        for (int i = 0; i < states.size(); i++) {
            ddshipstate.addItem(states.get(i).toString());
        }
        if (ddshipstate.getItemCount() > 0) {
           ddshipstate.setSelectedIndex(0); 
        }
       
       if (ddshipcountry.getItemCount() == 0)
       for (int i = 0; i < OVData.countries.length; i++) {
            ddshipcountry.addItem(OVData.countries[i]);
        }
       ddshipcountry.setSelectedItem("USA");
       
     }
     
    public void clearCust() {
        
        java.util.Date now = new java.util.Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        tbdateadded.setText(df.format(now));
        tbdatemod.setText(df.format(now));
        if (ddcountry.getItemCount() > 0)
        ddcountry.setSelectedItem("USA");
        if (ddstate.getItemCount() > 0)
        ddstate.setSelectedIndex(0);
        if (ddterms.getItemCount() > 0)
        ddterms.setSelectedIndex(0);
        if (ddfreightterms.getItemCount() > 0)
        ddfreightterms.setSelectedIndex(0);
        if (ddcarrier.getItemCount() > 0)
        ddcarrier.setSelectedIndex(0);
        if (ddtax.getItemCount() > 0)
        ddtax.setSelectedIndex(0);
        
       
        tbname.setText("");
        tbline1.setText("");
        tbline2.setText("");
        tbline3.setText("");
        tbcity.setText("");
        tbzip.setText("");
        tbpricecode.setText("");
        tbmarket.setText("");
        tbgroup.setText("");
        tbdisccode.setText("");
        tbcreditlimit.setText("0");
        tbcontactname.setText("");
        tbphone.setText("");
        tbfax.setText("");
        tbemail.setText("");
        tbremarks.setText("");
        tbsalesrep.setText("");
        
        tbcustcode.setText("");
        tbcustcode.setEditable(true);
        tbcustcode.setForeground(Color.black);
        
        /* cant add shipcode until billcode has been committed */
        btshipadd.setEnabled(false);
        btshipedit.setEnabled(false);
        
        
        contactmodel.setRowCount(0);
        
        ddcarrier.removeAllItems();
        ArrayList myscac = OVData.getScacCarrierOnly();   
        for (int i = 0; i < myscac.size(); i++) {
            ddcarrier.addItem(myscac.get(i));
        }
        
        ddfreightterms.removeAllItems();
        ArrayList freightterms = OVData.getfreighttermslist();
        for (int i = 0; i < freightterms.size(); i++) {
            ddfreightterms.addItem(freightterms.get(i));
        }
        
        ddterms.removeAllItems();
        ArrayList custterms = OVData.getcusttermslist();
        for (int i = 0; i < custterms.size(); i++) {
            ddterms.addItem(custterms.get(i));
        }
        
        ddtax.removeAllItems();
        ArrayList<String> tax = OVData.gettaxcodelist();
        for (int i = 0; i < tax.size(); i++) {
            ddtax.addItem(tax.get(i));
        }
        ddtax.insertItemAt("", 0);
        ddtax.setSelectedIndex(0);
        
        ddcurr.removeAllItems();
        ArrayList<String> curr = OVData.getCurrlist();
        for (int i = 0; i < curr.size(); i++) {
            ddcurr.addItem(curr.get(i));
        }
        ddcurr.setSelectedItem(OVData.getDefaultCurrency());
        
        
        ddbank.removeAllItems();
        ArrayList bank = OVData.getbanklist();
        for (int i = 0; i < bank.size(); i++) {
            ddbank.addItem(bank.get(i));
        }
        ddbank.setSelectedItem(OVData.getDefaultARBank());
        
        ddlabel.removeAllItems();
        ArrayList label = OVData.getLabelFileList("cont");
        for (int i = 0; i < label.size(); i++) {
            ddlabel.addItem(label.get(i));
        }
        
        ddaccount.removeAllItems();
        ArrayList accounts = OVData.getGLAcctList();
        for (int i = 0; i < accounts.size(); i++) {
            ddaccount.addItem(accounts.get(i).toString());
        }
        ddaccount.setSelectedItem(OVData.getDefaultARAcct());
       
        ddcc.removeAllItems();
        ArrayList ccs = OVData.getGLCCList();
        for (int i = 0; i < ccs.size(); i++) {
            ddcc.addItem(ccs.get(i).toString());
        }
        
        ddstate.removeAllItems();
        ArrayList states = OVData.getCodeMstrKeyList("state");
        for (int i = 0; i < states.size(); i++) {
            ddstate.addItem(states.get(i).toString());
        }
        if (ddstate.getItemCount() > 0) {
           ddstate.setSelectedIndex(0); 
        }
        
       /*
       if (ddstate.getItemCount() == 0)
       for (int i = 0; i < OVData.states.length; i++) {
            ddstate.addItem(OVData.states[i]);
        } else {
           ddstate.setSelectedIndex(0);
       }
        */
        
       if (ddcountry.getItemCount() == 0)
       for (int i = 0; i < OVData.countries.length; i++) {
            ddcountry.addItem(OVData.countries[i]);
        }
       ddcountry.setSelectedItem("USA");
       
       
        
        
        
    }
    
    public void clearAllContacts() {
         tbcontactname.setText("");
        tbphone.setText("");
        tbfax.setText("");
        tbemail.setText("");
        
         if (ddcontacttype.getItemCount() > 0)
        ddcontacttype.setSelectedIndex(0);
    }
    
    public void disableAll() {
        disableCust();
        disableShipTo();
        disableContact();
    }
    
     public void enableAll() {
        enableCust();
        enableShipTo();
        enableContact();
    }
     
    public void disableCust() {
        // tbcustcode.setEnabled(false);
        tbdateadded.setEnabled(false);
        tbdatemod.setEnabled(false);
        ddcountry.setEnabled(false);
        ddstate.setEnabled(false);
        ddterms.setEnabled(false);
        ddfreightterms.setEnabled(false);
        ddcarrier.setEnabled(false);
        tbname.setEnabled(false);
        tbline1.setEnabled(false);
        tbline2.setEnabled(false);
        tbline3.setEnabled(false);
        tbcity.setEnabled(false);
        tbzip.setEnabled(false);
        ddtax.setEnabled(false);
        tbpricecode.setEnabled(false);
        tbmarket.setEnabled(false);
        tbgroup.setEnabled(false);
        tbdisccode.setEnabled(false);
        tbcreditlimit.setEnabled(false);
        
        tbremarks.setEnabled(false);
        ddaccount.setEnabled(false);
        ddcc.setEnabled(false);
        tbsalesrep.setEnabled(false);
        ddbank.setEnabled(false);
        ddlabel.setEnabled(false);
        cbonhold.setEnabled(false);
        tbshpformat.setEnabled(false);
        tbinvformat.setEnabled(false);
        ddcurr.setEnabled(false);
       
        cbshipto.setEnabled(false);
        
        btadd.setEnabled(false);
        btedit.setEnabled(false);  
        btdelete.setEnabled(false);
        btcustcodebrowse.setEnabled(false);
         btcustnamebrowse.setEnabled(true);
       btcustzipbrowse.setEnabled(false);
       
       
    }
    
    public void disableShipTo() {
            tbshipname.setEnabled(false);
       tbshipline1.setEnabled(false);
       tbshipline2.setEnabled(false);
       tbshipline3.setEnabled(false);
       tbshipcity.setEnabled(false);
       tbshipzip.setEnabled(false);
        btshipadd.setEnabled(false);
        btshipedit.setEnabled(false);
        ddshipstate.setEnabled(false);
        ddshipcountry.setEnabled(false);
        tbshipcode.setEnabled(false);
        btshiptobrowse.setEnabled(false);
        btshipnew.setEnabled(false);
        
     
    }
    
    public void enableContact() {
        tbcontactname.setEnabled(true);
        tbphone.setEnabled(true);
        tbfax.setEnabled(true);
        tbemail.setEnabled(true); 
        ddcontacttype.setEnabled(true);
        btaddcontact.setEnabled(true);
        btdeletecontact.setEnabled(true);
        bteditcontact.setEnabled(true);
    }
    
    public void disableContact() {
        tbcontactname.setEnabled(false);
        ddcontacttype.setEnabled(false);
        btaddcontact.setEnabled(false);
        btdeletecontact.setEnabled(false);
        tbphone.setEnabled(false);
        tbfax.setEnabled(false);
        tbemail.setEnabled(false);
        bteditcontact.setEnabled(false);
    }
    
    
    public void enableCust() {
        tbcustcode.setEnabled(true);
        ddcountry.setEnabled(true);
        ddstate.setEnabled(true);
        ddterms.setEnabled(true);
        ddfreightterms.setEnabled(true);
        ddcarrier.setEnabled(true);
        tbname.setEnabled(true);
        tbline1.setEnabled(true);
        tbline2.setEnabled(true);
        tbline3.setEnabled(true);
        tbcity.setEnabled(true);
        tbzip.setEnabled(true);
        ddtax.setEnabled(true);
        tbpricecode.setEnabled(true);
        tbmarket.setEnabled(true);
        tbgroup.setEnabled(true);
        tbdisccode.setEnabled(true);
        tbcreditlimit.setEnabled(true);
        
        tbremarks.setEnabled(true);
        ddaccount.setEnabled(true);
        ddcc.setEnabled(true);
        tbsalesrep.setEnabled(true);
        
        ddbank.setEnabled(true);
        ddlabel.setEnabled(true);
        cbonhold.setEnabled(true);
        tbshpformat.setEnabled(true);
        tbinvformat.setEnabled(true);
        ddcurr.setEnabled(true);
       
       
       
       btadd.setEnabled(true);
       btedit.setEnabled(true);
       btdelete.setEnabled(true);
       btcustnamebrowse.setEnabled(true);
       btcustzipbrowse.setEnabled(true);
       btcustcodebrowse.setEnabled(true);
      
       
    }
    
  
    public void enableShipTo() {
       tbshipname.setEnabled(true);
       tbshipline1.setEnabled(true);
       tbshipline2.setEnabled(true);
       tbshipline3.setEnabled(true);
       tbshipcity.setEnabled(true);
       tbshipzip.setEnabled(true);
        btshipadd.setEnabled(true);
       btshipedit.setEnabled(true);
       ddshipstate.setEnabled(true);
        ddshipcountry.setEnabled(true);
        tbshipcode.setEnabled(true);
       
      
        btshiptobrowse.setEnabled(true);
        btshipnew.setEnabled(true);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        btcustcodebrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        tbcustcode = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbremarks = new javax.swing.JTextArea();
        jLabel29 = new javax.swing.JLabel();
        btcustnamebrowse = new javax.swing.JButton();
        btcustzipbrowse = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        tbdatemod = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbmarket = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cbonhold = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        tbdateadded = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        tbgroup = new javax.swing.JTextField();
        tbcreditlimit = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        ddterms = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        ddcarrier = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        ddfreightterms = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        tbdisccode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        tbpricecode = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tbsalesrep = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        ddbank = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        tbshpformat = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        tbinvformat = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        ddlabel = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        btedit = new javax.swing.JButton();
        cbshipto = new javax.swing.JCheckBox();
        ddaccount = new javax.swing.JComboBox<>();
        ddcc = new javax.swing.JComboBox<>();
        ddtax = new javax.swing.JComboBox<>();
        btdelete = new javax.swing.JButton();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        shiptoPanel = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        tbshipcity = new javax.swing.JTextField();
        tbshipzip = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        ddshipcountry = new javax.swing.JComboBox();
        ddshipstate = new javax.swing.JComboBox();
        tbshipline2 = new javax.swing.JTextField();
        tbshipline3 = new javax.swing.JTextField();
        tbshipname = new javax.swing.JTextField();
        tbshipline1 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        btshipadd = new javax.swing.JButton();
        btshipedit = new javax.swing.JButton();
        btshiptobrowse = new javax.swing.JButton();
        btshipnew = new javax.swing.JButton();
        tbshipcode = new javax.swing.JTextField();
        contactPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        contacttable = new javax.swing.JTable();
        btdeletecontact = new javax.swing.JButton();
        tbcontactname = new javax.swing.JTextField();
        btaddcontact = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        tbemail = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        ddcontacttype = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        tbfax = new javax.swing.JTextField();
        bteditcontact = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Master Maintenance"));

        jLabel7.setText("State");

        jLabel5.setText("Line3");

        jLabel8.setText("Zip/PostCode");

        jLabel3.setText("Line1");

        jLabel9.setText("Country");

        jLabel4.setText("Line2");

        jLabel1.setText("CustCode");

        jLabel2.setText("Name");

        jLabel6.setText("City");

        btcustcodebrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btcustcodebrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcustcodebrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        tbcustcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcustcodeActionPerformed(evt);
            }
        });

        tbremarks.setColumns(20);
        tbremarks.setRows(5);
        jScrollPane2.setViewportView(tbremarks);

        jLabel29.setText("Remarks");

        btcustnamebrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btcustnamebrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcustnamebrowseActionPerformed(evt);
            }
        });

        btcustzipbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btcustzipbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcustzipbrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btcustnamebrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(tbcustcode, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btcustcodebrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnew))
                                    .addComponent(tbline1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbline2, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbline3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btcustzipbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddcountry, 0, 233, Short.MAX_VALUE)
                            .addComponent(ddstate, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btcustcodebrowse)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(tbcustcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnew))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(btcustnamebrowse))
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
                    .addComponent(btcustzipbrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addContainerGap(149, Short.MAX_VALUE))
        );

        tbdatemod.setEnabled(false);

        jLabel10.setText("DateAdded");

        jLabel13.setText("Group");

        cbonhold.setText("On Hold");

        jLabel12.setText("Market");

        jLabel11.setText("LastMod");

        jLabel14.setText("CreditLimit");

        jLabel15.setText("Terms");

        jLabel16.setText("Carrier");

        jLabel17.setText("Freight Type");

        jLabel18.setText("Disc Code");

        jLabel19.setText("Price Code");

        jLabel20.setText("Tax Code");

        jLabel26.setText("SalesRep");

        jLabel27.setText("AR Account");

        jLabel28.setText("CostCenter");

        jLabel39.setText("Bank");

        jLabel40.setText("Label Format");

        jLabel42.setText("Shp Format");

        jLabel43.setText("Inv Format");

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

        cbshipto.setText("Create Shipto Same as Billto");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel41.setText("Currency");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbgroup)
                            .addComponent(tbshpformat)
                            .addComponent(ddcurr, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddcarrier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddlabel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddaccount, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddcc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbpricecode)
                            .addComponent(tbsalesrep)
                            .addComponent(ddbank, 0, 133, Short.MAX_VALUE)
                            .addComponent(tbdateadded, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbcreditlimit)
                            .addComponent(ddterms, 0, 170, Short.MAX_VALUE)
                            .addComponent(tbinvformat)
                            .addComponent(ddfreightterms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbdisccode)
                            .addComponent(ddtax, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbdatemod)
                            .addComponent(tbmarket)
                            .addComponent(cbonhold)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(cbshipto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)))
                .addGap(35, 35, 35))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdateadded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(tbdatemod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbgroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(tbmarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbcreditlimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddterms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel15))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddfreightterms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel17)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbsalesrep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel26))
                            .addGap(6, 6, 6)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcarrier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addGap(51, 51, 51)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdisccode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel27)
                    .addComponent(ddaccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel28)
                    .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddbank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(cbonhold))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(tbinvformat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(ddlabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshpformat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btedit)
                    .addComponent(cbshipto)
                    .addComponent(btdelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(mainPanel);

        shiptoPanel.setBackground(new java.awt.Color(220, 220, 220));

        jLabel30.setText("State");

        jLabel31.setText("Line3");

        jLabel32.setText("Zip");

        jLabel33.setText("Line1");

        jLabel34.setText("Country");

        jLabel35.setText("Line2");

        jLabel36.setText("ShipCode");

        jLabel37.setText("Name");

        jLabel38.setText("City");

        btshipadd.setText("Add");
        btshipadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshipaddActionPerformed(evt);
            }
        });

        btshipedit.setText("Edit");
        btshipedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshipeditActionPerformed(evt);
            }
        });

        btshiptobrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btshiptobrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshiptobrowseActionPerformed(evt);
            }
        });

        btshipnew.setText("New");
        btshipnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshipnewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout shiptoPanelLayout = new javax.swing.GroupLayout(shiptoPanel);
        shiptoPanel.setLayout(shiptoPanelLayout);
        shiptoPanelLayout.setHorizontalGroup(
            shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shiptoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(shiptoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbshipcode, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btshiptobrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btshipnew))
                    .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(shiptoPanelLayout.createSequentialGroup()
                            .addComponent(btshipedit)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btshipadd))
                        .addGroup(shiptoPanelLayout.createSequentialGroup()
                            .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel37)
                                        .addComponent(jLabel33)
                                        .addComponent(jLabel35)
                                        .addComponent(jLabel31)
                                        .addComponent(jLabel38)
                                        .addComponent(jLabel30))
                                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(shiptoPanelLayout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(ddshipcountry, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(tbshipline3, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(tbshipline2, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(tbshipline1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(ddshipstate, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tbshipcity, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(tbshipzip, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbshipname)))))
                .addContainerGap(565, Short.MAX_VALUE))
        );
        shiptoPanelLayout.setVerticalGroup(
            shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shiptoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addComponent(btshiptobrowse)
                    .addComponent(btshipnew)
                    .addComponent(tbshipcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btshipadd)
                    .addComponent(btshipedit))
                .addContainerGap(231, Short.MAX_VALUE))
        );

        add(shiptoPanel);

        contactPanel.setBackground(new java.awt.Color(220, 220, 220));

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

        btdeletecontact.setText("DeleteContact");
        btdeletecontact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletecontactActionPerformed(evt);
            }
        });

        btaddcontact.setText("AddContact");
        btaddcontact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddcontactActionPerformed(evt);
            }
        });

        jLabel24.setText("ContactType");

        jLabel21.setText("ContactName");

        ddcontacttype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sales", "Finance", "IT", "Admin", "Shipping", "Engineering", "Quality" }));

        jLabel23.setText("Email");

        jLabel22.setText("Phone");

        jLabel25.setText("Fax");

        bteditcontact.setText("EditContact");
        bteditcontact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditcontactActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contactPanelLayout = new javax.swing.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                .addComponent(tbfax, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bteditcontact)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddcontact)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeletecontact))
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1))
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
                    .addComponent(btdeletecontact)
                    .addComponent(btaddcontact)
                    .addComponent(bteditcontact))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(tbcontactname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE))
        );

        add(contactPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
           if (! validateInput("add")) {
             return;
         }
        BlueSeerUtils.startTask(new String[]{"","Committing..."});
        disableAll();
        Task task = new Task("add");
        task.execute();   
    }//GEN-LAST:event_btaddActionPerformed

    private void btaddcontactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddcontactActionPerformed
                         addContact(tbcustcode.getText());
                         refreshContactTable(tbcustcode.getText());
    }//GEN-LAST:event_btaddcontactActionPerformed

    private void btdeletecontactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletecontactActionPerformed
         int[] rows = contacttable.getSelectedRows();
        for (int i : rows) {
           deleteContact(tbcustcode.getText(), contacttable.getValueAt(i, 0).toString());
        }
       refreshContactTable(tbcustcode.getText());
       clearAllContacts();
    }//GEN-LAST:event_btdeletecontactActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
          if (!validateInput("edit")) {
             return;
         }
        BlueSeerUtils.startTask(new String[]{"","Committing..."});
        disableAll();
        Task task = new Task("edit");
        task.execute();      
    }//GEN-LAST:event_bteditActionPerformed

    private void btshipaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshipaddActionPerformed
       if (OVData.isValidCustShipTo(tbcustcode.getText(),tbshipcode.getText())) {
                  bsmf.MainFrame.show("ShipCode already in use");
                  return;
              } 
        addShipTo();
    }//GEN-LAST:event_btshipaddActionPerformed

    private void btshipeditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshipeditActionPerformed
        boolean proceed = true;
        java.util.Date now = new java.util.Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
       if (proceed) {
           
           try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
               st.executeUpdate("update cms_det set " + 
                       " cms_name = " + "'" + tbshipname.getText() + "'" + "," +
                       " cms_line1 = " + "'" + tbshipline1.getText() + "'" + "," +
                       " cms_line2 = " + "'" + tbshipline2.getText() + "'" + "," +
                       " cms_line3 = " + "'" + tbshipline3.getText() + "'" + "," +
                       " cms_city = " + "'" + tbshipcity.getText() + "'" + "," +
                       " cms_state = " + "'" + ddshipstate.getSelectedItem().toString() + "'" + "," +
                       " cms_zip = " + "'" + tbshipzip.getText() + "'" + "," +
                       " cms_country = " + "'" + ddshipcountry.getSelectedItem().toString() + "'" + 
                       " where cms_code = " + "'" + tbcustcode.getText() + "'" +
                       " AND cms_shipto = " + "'" + tbshipcode.getText() + "'" +
                       ";");
               
               bsmf.MainFrame.show("Updated Customer Shipto Successfully");
                          
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to get selected cust code");
            }
              } catch (Exception e) {
            MainFrame.bslog(e);
        }   
       } else {
           bsmf.MainFrame.show("Cannot Proceed...Data Field flagged");
       }
       
    }//GEN-LAST:event_btshipeditActionPerformed

    private void btcustcodebrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcustcodebrowseActionPerformed
        bsmf.MainFrame.reinitpanels("BrowseUtil", true, new String[]{"custmaint","cm_code"});
    }//GEN-LAST:event_btcustcodebrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        enableCust();
        clearCust();
        disableShipTo();
        disableContact();
        btedit.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        btcustcodebrowse.setEnabled(false);
        btcustnamebrowse.setEnabled(false);
       btcustzipbrowse.setEnabled(false);
        cbshipto.setEnabled(true);
        cbshipto.setSelected(true);
       
        
         if (OVData.isAutoCust()) {
        tbcustcode.setText(String.valueOf(OVData.getNextNbr("customer")));
        tbcustcode.setEditable(false);
        tbcustcode.setForeground(Color.blue);
        } else {
              tbcustcode.requestFocus();
         }
        
    }//GEN-LAST:event_btnewActionPerformed

    private void btshiptobrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshiptobrowseActionPerformed
        bsmf.MainFrame.reinitpanels("BrowseUtil", true, new String[]{"shiptomaint","cms_code",tbcustcode.getText()}); 
    }//GEN-LAST:event_btshiptobrowseActionPerformed

    private void btshipnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshipnewActionPerformed
       enableShipTo();
        clearShipTo();
         if (OVData.isAutoCust()) {
        tbshipcode.setText(String.valueOf(OVData.getNextNbr("shipto")));
        tbshipcode.setEnabled(false);
        } else {
              tbshipcode.requestFocus();
         }
      
    }//GEN-LAST:event_btshipnewActionPerformed

    private void bteditcontactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditcontactActionPerformed
        int[] rows = contacttable.getSelectedRows();
        for (int i : rows) {
           editContact(tbcustcode.getText(), contacttable.getValueAt(i, 0).toString());
        }
       refreshContactTable(tbcustcode.getText());
       clearAllContacts();
    }//GEN-LAST:event_bteditcontactActionPerformed

    private void contacttableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contacttableMouseClicked
        int row = contacttable.rowAtPoint(evt.getPoint());
        ddcontacttype.setSelectedItem(contacttable.getValueAt(row, 1).toString());
        tbcontactname.setText(contacttable.getValueAt(row, 2).toString());
        tbphone.setText(contacttable.getValueAt(row, 3).toString());
        tbfax.setText(contacttable.getValueAt(row, 4).toString());
        tbemail.setText(contacttable.getValueAt(row, 5).toString());
        
        
    }//GEN-LAST:event_contacttableMouseClicked

    private void btcustnamebrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcustnamebrowseActionPerformed
       bsmf.MainFrame.reinitpanels("BrowseUtil", true, new String[]{"custmaint","cm_name"});
    }//GEN-LAST:event_btcustnamebrowseActionPerformed

    private void btcustzipbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcustzipbrowseActionPerformed
       bsmf.MainFrame.reinitpanels("BrowseUtil", true, new String[]{"custmaint","cm_zip"});
    }//GEN-LAST:event_btcustzipbrowseActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
          boolean proceed = false;
          bsmf.MainFrame.show("This is not advised.  Deleting Customers with historical transactions may cause issues.");
           proceed = bsmf.MainFrame.warn("Are you sure?");
           if (proceed) {
             BlueSeerUtils.startTask(new String[]{"","Committing..."});
             disableAll();
             Task task = new Task("delete");
             task.execute();
           }
        
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbcustcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcustcodeActionPerformed
        boolean gotIt = getbillto(tbcustcode.getText());
        if (gotIt) {
          tbcustcode.setEditable(false);
          tbcustcode.setForeground(Color.blue);
        } else {
           tbcustcode.setForeground(Color.red); 
        }
    }//GEN-LAST:event_tbcustcodeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddcontact;
    private javax.swing.JButton btcustcodebrowse;
    private javax.swing.JButton btcustnamebrowse;
    private javax.swing.JButton btcustzipbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeletecontact;
    private javax.swing.JButton btedit;
    private javax.swing.JButton bteditcontact;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btshipadd;
    private javax.swing.JButton btshipedit;
    private javax.swing.JButton btshipnew;
    private javax.swing.JButton btshiptobrowse;
    private javax.swing.JCheckBox cbonhold;
    private javax.swing.JCheckBox cbshipto;
    private javax.swing.JPanel contactPanel;
    private javax.swing.JTable contacttable;
    private javax.swing.JComboBox<String> ddaccount;
    private javax.swing.JComboBox ddbank;
    private javax.swing.JComboBox ddcarrier;
    private javax.swing.JComboBox<String> ddcc;
    private javax.swing.JComboBox ddcontacttype;
    private javax.swing.JComboBox ddcountry;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddfreightterms;
    private javax.swing.JComboBox ddlabel;
    private javax.swing.JComboBox ddshipcountry;
    private javax.swing.JComboBox ddshipstate;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox<String> ddtax;
    private javax.swing.JComboBox ddterms;
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
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
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
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel shiptoPanel;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontactname;
    private javax.swing.JTextField tbcreditlimit;
    private javax.swing.JTextField tbcustcode;
    private javax.swing.JTextField tbdateadded;
    private javax.swing.JTextField tbdatemod;
    private javax.swing.JTextField tbdisccode;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbfax;
    private javax.swing.JTextField tbgroup;
    private javax.swing.JTextField tbinvformat;
    private javax.swing.JTextField tbline1;
    private javax.swing.JTextField tbline2;
    private javax.swing.JTextField tbline3;
    private javax.swing.JTextField tbmarket;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbpricecode;
    private javax.swing.JTextArea tbremarks;
    private javax.swing.JTextField tbsalesrep;
    private javax.swing.JTextField tbshipcity;
    private javax.swing.JTextField tbshipcode;
    private javax.swing.JTextField tbshipline1;
    private javax.swing.JTextField tbshipline2;
    private javax.swing.JTextField tbshipline3;
    private javax.swing.JTextField tbshipname;
    private javax.swing.JTextField tbshipzip;
    private javax.swing.JTextField tbshpformat;
    private javax.swing.JTextField tbzip;
    // End of variables declaration//GEN-END:variables
}
