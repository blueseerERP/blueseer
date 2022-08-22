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

package com.blueseer.utl;



import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import com.blueseer.edi.EDI;

import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import com.blueseer.fgl.fglData;
import com.blueseer.hrm.hrmData;
import com.blueseer.inv.calcCost;
import com.blueseer.inv.invData;
import com.blueseer.inv.invData.item_mstr;
import com.blueseer.ord.ordData;
import static com.blueseer.ord.ordData.getOrderTotalTax;
import com.blueseer.sch.schData;
import com.blueseer.shp.shpData;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble5;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble; 
import static com.blueseer.utl.BlueSeerUtils.bsParseDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.bsformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.currformatUS;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.swing.JOptionPane;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager; 
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import static java.lang.Math.abs;
import java.math.RoundingMode;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import jcifs.smb.SmbFileInputStream;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.type.OrientationEnum;





import org.apache.commons.lang3.time.DateUtils;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.json.JSONArray;
import org.json.JSONObject;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author vaughnte
 */
public class OVData { 
    
   
    
   public static String[] states = {"AB","AL","AK","AZ","AR","BC","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN","IA","KS","KY","LA","MB","ME","MD","MA","MI","MN","MS","MO","MT","NE","NL","NV","NH","NJ","NL","NM","NY","NC","ND","NS","OH","OK","ON","OR","PA","PE","QC","RI","SC","SD","SE","TN","TX","UT","VT","VA","WA","WV","WI","WY" };
   public static String[] countries = {"Afghanistan","Albania","Algeria","Andorra","Angola","Antigua & Deps","Argentina","Armenia","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bhutan","Bolivia","Bosnia Herzegovina","Botswana","Brazil","Brunei","Bulgaria","Burkina","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Central African Rep","Chad","Chile","China","Colombia","Comoros","Congo","Congo {Democratic Rep}","Costa Rica","Croatia","Cuba","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","East Timor","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Fiji","Finland","France","Gabon","Gambia","Georgia","Germany","Ghana","Greece","Grenada","Guatemala","Guinea","Guinea-Bissau","Guyana","Haiti","Honduras","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland {Republic}","Israel","Italy","Ivory Coast","Jamaica","Japan","Jordan","Kazakhstan","Kenya","Kiribati","Korea North","Korea South","Kosovo","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Morocco","Mozambique","Myanmar, {Burma}","Namibia","Nauru","Nepal","Netherlands","New Zealand","Nicaragua","Niger","Nigeria","Norway","Oman","Pakistan","Palau","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Qatar","Romania","Russian Federation","Rwanda","St Kitts & Nevis","St Lucia","Saint Vincent & the Grenadines","Samoa","San Marino","Sao Tome & Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","Spain","Sri Lanka","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Togo","Tonga","Trinidad & Tobago","Tunisia","Turkey","Turkmenistan","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom", "USA","Uruguay","Uzbekistan","Vanuatu","Vatican City","Venezuela","Vietnam","Yemen","Zambia","Zimbabwe" }; 
  
  
   
    public static void updatecounter(String countername, int counterid) {
        try{
            
                Connection con = null;
                if (ds != null) {
                  con = ds.getConnection();
                } else {
                  con = DriverManager.getConnection(url + db, user, pass);  
                }
            Statement st = con.createStatement();
       try {
            st.executeUpdate("update counter c set c.counter_id = " + "'" + counterid + "'" + "where c.counter_name = " + "'" + countername.toString() + "';");
        } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
    }


   
    public static boolean isUserDefined(String myuser) {
       boolean r = false;
       try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            PreparedStatement ps = null;
            ResultSet res = null;
            try{
                String sql = "SELECT user_id FROM  user_mstr where user_id = ? ;";
                ps = con.prepareStatement(sql);
                ps.setString(1, myuser);
                res = ps.executeQuery();
                if (res.isBeforeFirst()) {
                     r = true;
                }
           }
            catch (SQLException s){
               MainFrame.bslog(s);
               bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) res.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            }
        }
       
        catch (Exception e){
            MainFrame.bslog(e);
        }


       return r;
    }

    public static boolean isAcctNumberValid(String acct) {
       boolean r = false;
       try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            PreparedStatement ps = null;
            ResultSet res = null;
            try{
                String sql = "SELECT ac_id FROM ac_mstr where ac_id = ? ;";
                ps = con.prepareStatement(sql);
                ps.setString(1, acct);
                res = ps.executeQuery();
                if (res.isBeforeFirst()) {
                     r = true;
                }
           }
            catch (SQLException s){
               MainFrame.bslog(s);
               bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) res.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
       return r;
    }     

    public static boolean isCostCenterValid(String cc) {
       boolean r = false;
       try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            PreparedStatement ps = null;
            ResultSet res = null;
            try{
                String sql = "SELECT dept_id FROM dept_mstr where dept_id = ? ;";
                ps = con.prepareStatement(sql);
                ps.setString(1, cc);
                res = ps.executeQuery();
                if (res.isBeforeFirst()) {
                     r = true;
                }
           }
            catch (SQLException s){
               MainFrame.bslog(s);
               bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) res.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
       return r;
    }     
      
    public static ArrayList getusermstrlist() {
        ArrayList myarray = new ArrayList();
      
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
            
                res = st.executeQuery("select user_id from user_mstr order by user_id;");
               while (res.next()) {
                    myarray.add(res.getString("user_id"));
                }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            }
            finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
     
    public static int getNextNbr(String countername) {
       int nbr = 0;
        try{
           
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
              //  if (! dbtype.equals("sqlite")) {
                con.setAutoCommit(false);
               // }
                
                if (dbtype.equals("sqlite")) {
               res = st.executeQuery("select max(counter_id) as 'num' from counter where " +
                       " counter_name = " + "'" + countername + "'" + ";");
                } else {
                res = st.executeQuery("select max(counter_id) as 'num' from counter where " +
                       " counter_name = " + "'" + countername + "'" + " for update;");    
                }
                while (res.next()) {
                   nbr = res.getInt("num") + 1;
                }
                st.executeUpdate(
                       " update counter set counter_id = " + "'" + nbr + "'" +
                       " where counter_name = " + "'" + countername + "'" + ";" );
                
              //  if (! dbtype.equals("sqlite")) {
                con.setAutoCommit(true);
              //  }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            }
            finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return nbr;
        
    }
    
    public static int getNextNbr(String countername, int fwdnbr) {
       int nbr = 0;
        try{
           
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                con.setAutoCommit(false);
                if (dbtype.equals("sqlite")) {
               res = st.executeQuery("select max(counter_id) as 'num' from counter where " +
                       " counter_name = " + "'" + countername + "'" + ";");
                } else {
                res = st.executeQuery("select max(counter_id) as 'num' from counter where " +
                       " counter_name = " + "'" + countername + "'" + " for update;");    
                }
                while (res.next()) {
                   nbr = res.getInt("num") + 1;
                }
                if (fwdnbr > 0) {
                st.executeUpdate(
                       " update counter set counter_id = " + "'" + (nbr + fwdnbr - 1) + "'" +
                       " where counter_name = " + "'" + countername + "'" + ";" );
                } else {
                st.executeUpdate(
                       " update counter set counter_id = " + "'" + nbr + "'" +
                       " where counter_name = " + "'" + countername + "'" + ";" );    
                }
                con.setAutoCommit(true);
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            }
            finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return nbr;
        
    }
    
    
    public static int setNextNbr(String countername, int nbr) {
        try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                con.setAutoCommit(false);
                st.executeUpdate(
                       " update counter set counter_id = " + "'" + nbr + "'" +
                       " where counter_name = " + "'" + countername + "'" + ";" );
                con.setAutoCommit(true);
               
            } catch (SQLException s){
                 MainFrame.bslog(s);
            }
            finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return nbr;
        
    }
    
    
    public static void copyUserPerms(String fromuser, String touser) {
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
                
                boolean proceed = true;
                int i = 0;
                boolean canadd = false;
                ArrayList<String> perms = new ArrayList();
                
                
                if (touser.compareTo("Admin") == 0) {
                    bsmf.MainFrame.show("Cannot overwrite Admin permissions");
                    proceed = false;
                }

                if (proceed) {
                
                 res = st.executeQuery("SELECT perm_menu FROM  perm_mstr where perm_user = " + "'" + fromuser + "'" + ";");
                    while (res.next()) {
                        perms.add(res.getString("perm_menu"));
                    }
                    
                   for (String menu : perms) { 
                       canadd = true;
                            res = st.executeQuery("SELECT perm_menu FROM  perm_mstr where perm_user = " + 
                                    "'" + touser + "'" + 
                                    " and perm_menu = " + "'" + menu + "'" + ";");
                            while (res.next()) {
                               canadd = false;
                            }
                            if (canadd) {
                                 st.executeUpdate("insert into perm_mstr values ( "
                                +  "'" + touser + "'" + ","
                                +  "'" + menu + "'" + ")"
                                + ";");
                            }
                   } 

                } else {
                    bsmf.MainFrame.show("Copy user From does not exist");
                } // if proceed
            } 
            catch (SQLException s) {
                MainFrame.bslog(s);
            }
            finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
       }
       
    public static boolean copySite(String fromsite, String tosite) {
         boolean r = true;
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
                
                boolean proceed = true;
                int i = 0;
                boolean canadd = false;
                ArrayList<String> perms = new ArrayList();
                
                
                if (tosite.compareTo(fromsite) == 0) {
                    bsmf.MainFrame.show("Cannot overwrite site with same site");
                    proceed = false;
                }

                if (proceed) {
                 i = 0;
                 
                 // first check that it does not already exist
                 res = st.executeQuery("SELECT * FROM  site_mstr where site_site = " + "'" + tosite + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                  if (i > 0) {
                      bsmf.MainFrame.show("Site already exists");
                      return r = false;
                  }  
                  res = st.executeQuery("SELECT * FROM  site_mstr where site_site = " + "'" + fromsite + "'" + ";");
                    while (res.next()) {
                        st.executeUpdate("insert into site_mstr (site_site, site_logo, site_iv_jasper, site_sh_jasper, site_po_jasper, site_or_jasper, site_pos_jasper ) values ( " + 
                                     "'" + tosite + "'" + "," +
                                    "'" + res.getString("site_logo") + "'" + "," +
                                    "'" + res.getString("site_iv_jasper") + "'" + "," +
                                    "'" +  res.getString("site_sh_jasper") + "'" + "," +
                                    "'" +  res.getString("site_po_jasper") + "'" + "," +
                                    "'" +  res.getString("site_or_jasper") + "'" + "," +
                                    "'" +  res.getString("site_pos_jasper") + "'" +        
                                ");"
                                );
                    }
                    
                  
                 /* st.executeUpdate("insert into site_mstr (site_site, site_logo, site_iv_jasper, site_sh_jasper, site_po_jasper, site_or_jasper, site_pos_jasper ) " +
                          " values ( select " + "'" + tosite + "'" + ", f.site_logo, f.site_iv_jasper, f.site_sh_jasper, f.site_po_jasper, f.site_or_jasper, f.site_pos_jasper " +
                          " from site_mstr f where f.site_site = " + "'" + fromsite + "'" + ")"  );
                  */
                }  // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                r = false;
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            r = false;
        }  
           return r; 
       }
        
    public static String addMenuToUser(String menu, String thisuser) {
            String mystring = "";  // 0 = assigned; 1 = already assigned; 2 = error
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
               
                boolean canadd = true;
                       
                       res = st.executeQuery("SELECT perm_menu FROM  perm_mstr where perm_user = " + "'" + thisuser + "'" + 
                                             " AND perm_menu = " + "'" + menu + "'" + ";");
                            while (res.next()) {
                                canadd = false;
                            }
                            
                            if (canadd) {
                                 st.executeUpdate("insert into perm_mstr values ( "
                                + "'" + thisuser + "'" + "," 
                                + "'" + menu + "'" + ") " 
                                + ";");
                                
                                  mystring = "0";
                            } else {
                                 mystring = "1";
                            }
                    

            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                 mystring = "2";
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
             mystring = "2";
        }  
         return mystring;
       }
     
    public static String sampleStringJoin(String menu, String thisuser) {
        String mystring = "";  // 0 = unassigned; 1 = already unassigned; 2 = error
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
                String[] permlist = null;
                String newperms = "";
                boolean candelete = false;

                ArrayList<String> users = new ArrayList();
                ArrayList<String> perms = new ArrayList();

                res = st.executeQuery("SELECT user_perms FROM  user_mstr where user_id = " + "'" + thisuser + "'" + ";");
                while (res.next()) {
                    permlist = res.getString("user_perms").split(",");
                }
                for (String myperm : permlist) {
                    if (myperm.toLowerCase().equals(menu.toLowerCase())) {
                        candelete = true;
                    } else {
                        newperms += myperm + ",";
                    }
                }
                if (candelete) {
                    st.executeUpdate("update user_mstr set "
                            + "user_perms = " + "'" + newperms.substring(0, newperms.length() - 1) + "'"
                            + " where user_id = " + "'" + thisuser + "'"
                            + ";");

                    mystring = "0";
                } else {

                    mystring = "1";
                }

            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                mystring = "2";
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            mystring = "2";
        }
        return mystring;
    }

    public static String deleteMenuToUser(String menu, String thisuser) {
        String mystring = "";  // 0 = unassigned; 1 = already unassigned; 2 = error
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

                boolean candelete = false;

                ArrayList<String> users = new ArrayList();
                ArrayList<String> perms = new ArrayList();

                res = st.executeQuery("SELECT perm_menu FROM  perm_mstr where perm_user = " + "'" + thisuser + "'"
                        + " AND perm_menu = " + "'" + menu + "'" + ";");
                while (res.next()) {
                    candelete = true;
                }

                if (candelete) {
                    st.executeUpdate("delete from perm_mstr where "
                            + " perm_menu = " + "'" + menu + "'"
                            + " and perm_user = " + "'" + thisuser + "'"
                            + ";");

                    mystring = "0";
                } else {

                    mystring = "1";
                }

            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                mystring = "2";
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            mystring = "2";
        }
        return mystring;
    }

    public static void deleteMenuToAllUsers(String menu) {
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
                String[] permlist = null;

                ArrayList<String> users = new ArrayList();
                ArrayList<String> perms = new ArrayList();

                st.executeUpdate("delete from perm_mstr where "
                        + " perm_menu = " + "'" + menu + "'"
                        + ";");

            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

    public static void addMenuToAllUsers(String menu) {
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
                String[] permlist = null;
                String newperms = "";
                boolean canadd = true;

                ArrayList<String> users = new ArrayList();

                res = st.executeQuery("SELECT user_id FROM  user_mstr where user_id <> 'admin' ;");
                while (res.next()) {
                    users.add(res.getString("user_id"));
                }

                for (String user : users) {
                    canadd = true;
                    res = st.executeQuery("SELECT perm_user FROM  perm_mstr where perm_user = "
                            + "'" + user + "'"
                            + " and perm_menu = " + "'" + menu + "'" + ";");
                    while (res.next()) {
                        canadd = false;
                    }
                    if (canadd) {
                        st.executeUpdate("insert into perm_mstr values ( "
                                + "'" + user + "'" + ","
                                + "'" + menu + "'" + ")"
                                + ";");
                    }
                }

            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

    public static void addMenu(String menu, String desc) {
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
                res = st.executeQuery("SELECT menu_id FROM menu_mstr where menu_id = " + "'" + menu + "'" + ";");
                while (res.next()) {
                    i++;
                }

                if (i > 0) {
                    bsmf.MainFrame.show("Menu already exists");
                } else {
                    st.executeUpdate("insert into menu_mstr values ( "
                            + "'" + menu + "'" + "," + "'" + desc + "'" + ")"
                            + ";");
                    bsmf.MainFrame.show("Added Menu");
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

    public static void addItemImage(String item, String file) {
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
                res = st.executeQuery("SELECT iti_file FROM item_image where iti_item = " + "'" + item + "'"
                        + " AND iti_file = " + "'" + file + "'"
                        + " ;");
                while (res.next()) {
                    i++;
                }

                if (i > 0) {
                    bsmf.MainFrame.show("Filename already exists");
                } else {
                    st.executeUpdate("insert into item_image (iti_item, iti_site, iti_order, iti_file) values ( "
                            + "'" + item + "'" + ","
                            + "'" + "" + "'" + ","
                            + "'" + "0" + "'" + ","
                            + "'" + file + "'" + ")"
                            + ";");
                    bsmf.MainFrame.show("Added Item Image");
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

    public static void addItemCostRec(String item, String site, String set, Double mtl, Double ovh, Double out, Double tot) {
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
                res = st.executeQuery("SELECT itc_item FROM item_cost where itc_item = " + "'" + item + "'"
                        + " AND itc_set = " + "'" + set + "'"
                        + " AND itc_site = " + "'" + site + "'" + ";");
                while (res.next()) {
                    i++;
                }

                if (i == 0) {
                    st.executeUpdate("insert into item_cost (itc_item, itc_set, itc_site, itc_mtl_top, itc_ovh_top, itc_out_top, itc_total) values ( "
                            + "'" + item + "'" + ","
                            + "'" + set + "'" + ","
                            + "'" + site + "'" + ","
                            + "'" + mtl + "'" + ","
                            + "'" + ovh + "'" + ","
                            + "'" + out + "'" + ","
                            + "'" + tot + "'"
                            + ")"
                            + ";");
                } else {
                    st.executeUpdate("update item_cost set " 
                            + "itc_mtl_top = " + "'" + mtl + "'" + ","
                            + "itc_ovh_top = " + "'" + ovh + "'" + ","
                            + "itc_out_top = " + "'" + out + "'" + ","
                            + "itc_total = " + "'" + tot + "'"
                            + " where "        
                            + "itc_item = " + "'" + item + "'" + " AND "
                            + "itc_set = " + "'" + set + "'" + " AND "
                            + "itc_site = " + "'" + site + "'" 
                            + ";");
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

    public static void updateItemCostRec(String item, String site, String set, Double mtl, Double ovh, Double out, Double tot) {
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
            st.executeUpdate("update item_cost set " +
                  " itc_mtl_top = " + "'" + mtl + "'" + "," +
                  " itc_ovh_top = " + "'" + ovh + "'" + "," +
                  " itc_out_top = " + "'" + out + "'" + "," +
                  " itc_total = " + "'" + tot + "'" +
                  " where itc_item = " + "'" + item + "'" +
                  " and itc_set = " + "'" + set + "'" +
                  " and itc_site = " + "'" + site + "'" +
                  ";");
               
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

    public static void updateItemCostRec(String item, String site, String set, Double mtl, Double lbr, Double bdn, Double ovh, Double out, Double tot) {
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
            st.executeUpdate("update item_cost set " +
                  " itc_mtl_top = " + "'" + mtl + "'" + "," +
                  " itc_ovh_top = " + "'" + ovh + "'" + "," +
                  " itc_out_top = " + "'" + out + "'" + "," +
                  " itc_lbr_top = " + "'" + lbr + "'" + "," +
                  " itc_bdn_top = " + "'" + bdn + "'" + "," +        
                  " itc_total = " + "'" + tot + "'" +
                  " where itc_item = " + "'" + item + "'" +
                  " and itc_set = " + "'" + set + "'" +
                  " and itc_site = " + "'" + site + "'" +
                  ";");
               
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

    
    public static ArrayList getmenulist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select menu_id from menu_mstr order by menu_id ;");
                while (res.next()) {
                    myarray.add(res.getString("menu_id"));

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static void disablemenu(String child) {
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

                st.executeUpdate(
                        " update menu_tree set mt_visible = '0' "
                        + " where mt_child = " + "'" + child + "'" + ";");

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }

    public static void enablemenu(String child) {
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

                st.executeUpdate(
                        " update menu_tree set mt_visible = '1' "
                        + " where mt_child = " + "'" + child + "'" + ";");

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }

    public static void enablemenutree(String menu) {

        enablemenu(menu);
        ArrayList<String> mychildren = new ArrayList<String>();
        mychildren = OVData.getMenuTreeAll(menu);

        for (String myvalue : mychildren) {
            String[] recs = myvalue.split(",", -1);
            if (recs[1].toString().compareTo("JMenuItem") == 0) {
                enablemenu(recs[0].toString());
            } else {
                enablemenutree(recs[0].toString());
            }

        }

    }

    public static void disablemenutree(String menu) {

        disablemenu(menu);
        ArrayList<String> mychildren = new ArrayList<String>();
        mychildren = OVData.getMenuTreeAll(menu);

        for (String myvalue : mychildren) {
            String[] recs = myvalue.split(",", -1);
            if (recs[1].toString().compareTo("JMenuItem") == 0) {
                disablemenu(recs[0].toString());
            } else {
                disablemenutree(recs[0].toString());
            }

        }

    }

    public static void indexMenuChildren(String parent, String child, int newindex, int oldindex) {

        HashMap<String, Integer> mymap = new HashMap<String, Integer>();
        TreeMap<Integer, String> newmap = new TreeMap<Integer, String>();
        boolean addone = false;
        newmap.put(newindex, child);

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

                res = st.executeQuery("select mt_child, mt_index from menu_tree where mt_par = " + "'" + parent + "'"
                        + "  " + " order by mt_index ;");
                while (res.next()) {
                    mymap.put(res.getString("mt_child"), res.getInt("mt_index"));
                }

                // if new index is less than old
                if (newindex < oldindex) {
                    JLOOP:
                    for (int j = 1; j <= mymap.size(); j++) {
                        // for any indexes less than childindex..maintain index
                        MYMAPLOOP:
                        for (Entry<String, Integer> entry : mymap.entrySet()) {
                            String key = entry.getKey();
                            int myint = entry.getValue();
                            if (key.compareTo(child) == 0) {
                                continue MYMAPLOOP;
                            }
                            if (myint >= newindex && myint <= oldindex) {
                                addone = true;
                            }
                            if (myint > oldindex) {
                                addone = false;
                            }
                            if (myint < newindex && myint == j) {
                                newmap.put(j, key);
                                continue JLOOP;
                            }
                            if (myint >= newindex && myint == j) {
                                if (addone) {
                                    newmap.put(j + 1, key);
                                } else {
                                    newmap.put(j, key);
                                }
                                continue JLOOP;
                            }
                        }
                    }
                }
                boolean subtractone = false;
                // if new index is greater than old
                if (newindex > oldindex) {
                    JLOOP:
                    for (int j = 1; j <= mymap.size(); j++) {
                        // for any indexes less than childindex..maintain index
                        MYMAPLOOP:
                        for (Entry<String, Integer> entry : mymap.entrySet()) {
                            String key = entry.getKey();
                            int myint = entry.getValue();
                            if (key.compareTo(child) == 0) {
                                continue MYMAPLOOP;
                            }
                            if (myint <= newindex && myint >= oldindex) {
                                subtractone = true;
                            }
                            if (myint > newindex) {
                                subtractone = false;
                            }
                            if (myint < oldindex && myint == j) {
                                newmap.put(j, key);
                                continue JLOOP;
                            }
                            if (myint >= oldindex && myint == j) {
                                if (subtractone) {
                                    newmap.put(j - 1, key);
                                } else {
                                    newmap.put(j, key);
                                }
                                continue JLOOP;
                            }
                        }
                    }
                }

                // OK ...now we have newmap assigned with appropriate indexing
                for (Entry<Integer, String> entry : newmap.entrySet()) {
                    String value = entry.getValue();
                    int myint = entry.getKey();
                    st.executeUpdate(
                            " update menu_tree set mt_index = " + "'" + myint + "'"
                            + " where mt_par = " + "'" + parent + "'"
                            + " and mt_child = " + "'" + value + "'" + ";");
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }

    public static ArrayList getmenutree(String parent) {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select mt_child, mt_type, mt_label, mt_icon, mt_initvar, mt_func, mt_visible, mt_enable from menu_tree where mt_par = " + "'" + parent + "'"
                        + " and mt_visible = '1' " + " order by mt_index ;");
                while (res.next()) {
                    myarray.add(res.getString("mt_child") + ","
                            + res.getString("mt_type") + ","
                            + res.getString("mt_label") + ","
                            + res.getString("mt_icon") + ","
                            + res.getString("mt_initvar") + ","
                            + res.getString("mt_func") + ","
                            + res.getString("mt_visible") + ","
                            + res.getString("mt_enable")
                    );

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getMenuTreeAll(String parent) {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select mt_child, mt_type, mt_label, mt_icon, mt_initvar, mt_func, mt_visible, mt_enable from menu_tree where mt_par = " + "'" + parent + "'"
                        + " order by mt_index ;");
                while (res.next()) {
                    myarray.add(res.getString("mt_child") + ","
                            + res.getString("mt_type") + ","
                            + res.getString("mt_label") + ","
                            + res.getString("mt_icon") + ","
                            + res.getString("mt_initvar") + ","
                            + res.getString("mt_func") + ","
                            + res.getString("mt_visible") + ","
                            + res.getString("mt_enable")
                    );

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getpanellist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select panel_id, panel_core, ov_custom from panel_mstr inner join ov_ctrl;");
                while (res.next()) {
                    if (res.getInt("panel_core") == 1) {
                        myarray.add(res.getString("panel_id"));
                    }
                    if (res.getInt("ov_custom") == 1 && res.getInt("panel_core") == 0) {
                        myarray.add(res.getString("panel_id"));
                    }
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getUsersOfMenusList(String menu) {
        ArrayList myarray = new ArrayList();
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

                ArrayList<String> users = new ArrayList();

                String[] permlist = null;
                res = st.executeQuery("SELECT perm_user FROM  perm_mstr where "
                        + " perm_menu = " + "'" + menu + "'" + ";");
                while (res.next()) {
                    myarray.add(res.getString("perm_user"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getMenusOfUsersList(String myuser) {
        ArrayList myarray = new ArrayList();
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

                ArrayList<String> users = new ArrayList();

                String[] menulist = null;

                res = st.executeQuery("SELECT perm_menu FROM  perm_mstr where perm_user = " + "'" + myuser + "'" + " order by perm_menu ;");
                while (res.next()) {
                    myarray.add(res.getString("perm_menu"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }
    
    public static ArrayList getbanklist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select bk_id from bk_mstr order by bk_id;");
                while (res.next()) {
                    myarray.add(res.getString("bk_id"));

                }

            } catch (SQLException s) {
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

  
    /* Customer Related functions */
    
    
    public static boolean addCustMstr(ArrayList<String> list, String delim) {
        boolean myreturn = false;
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
                String[] ld = null;

                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);

                    res = st.executeQuery("select cm_code from cm_mstr where "
                            + " cm_code = " + "'" + ld[0] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }

                    if (j == 0) {
                        st.executeUpdate(" insert into cm_mstr "
                                + "(cm_code, cm_site, cm_name, cm_line1, cm_line2, cm_line3, cm_city, cm_state, cm_zip, cm_country, cm_dateadd, cm_datemod, "
                                + " cm_usermod, cm_group, cm_market, cm_creditlimit, cm_onhold, cm_carrier, cm_terms, cm_freight_type, cm_price_code, "
                                + " cm_disc_code, cm_tax_code, cm_salesperson, cm_ar_acct, cm_ar_cc, cm_remarks, cm_misc1, cm_bank, cm_curr, "
                                + " cm_logo, cm_ps_jasper, cm_iv_jasper, cm_label ) "
                                + " values ( "
                                + "'" + ld[0] + "'" + ","
                                + "'" + ld[1] + "'" + ","
                                + "'" + ld[2] + "'" + ","
                                + "'" + ld[3] + "'" + ","
                                + "'" + ld[4] + "'" + ","
                                + "'" + ld[5] + "'" + ","
                                + "'" + ld[6] + "'" + ","
                                + "'" + ld[7] + "'" + ","
                                + "'" + ld[8] + "'" + ","
                                + "'" + ld[9] + "'" + ","        
                                + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" + ","
                                + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" + ","
                                + "'" + bsmf.MainFrame.userid + "'" + ","
                                + "'" + ld[10] + "'" + ","
                                + "'" + ld[11] + "'" + ","
                                + "'" + ld[12] + "'" + ","
                                + "'" + ld[13] + "'" + ","
                                + "'" + ld[14] + "'" + ","
                                + "'" + ld[15] + "'" + ","
                                + "'" + ld[16] + "'" + ","
                                + "'" + ld[17] + "'" + ","
                                + "'" + ld[18] + "'" + ","
                                + "'" + ld[19] + "'" + ","
                                + "'" + ld[20] + "'" + ","
                                + "'" + ld[21] + "'" + ","
                                + "'" + ld[22] + "'" + ","
                                + "'" + ld[23] + "'" + ","
                                + "'" + ld[24] + "'" + ","
                                + "'" + ld[25] + "'" + ","
                                + "'" + ld[26] + "'" + ","
                                + "'" + ld[27] + "'" + ","
                                + "'" + ld[28] + "'" + ","
                                + "'" + ld[29] + "'" + ","
                                + "'" + ld[30] + "'" + ","
                                + "'" + ld[31] + "'" + ","
                                + "'" + ld[32] + "'"
                                + " );"
                        );
                    }
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                myreturn = true;
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myreturn;
    }
    
    public static String[] addCustMstrWShipToMinimal(String[] list) {
       // list[0] will have newly assigned custcode from getNextNbr
       // return array:  aracct, arcc, currency, bank, terms, carrier, onhold, site
       String[] x = new String[]{"","","","","","","", ""}; 
        
        try {
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
                        
            x[0] = OVData.getDefaultARAcct();
            x[1] = OVData.getDefaultARCC();
            x[2] = OVData.getDefaultCurrency();
            x[3] = OVData.getDefaultARBank();
            x[4] = "N00";
            x[5] = "";
            x[6] = "0";
            x[7] = OVData.getDefaultSite();
            
            try {
                int i = 0;
               
                    res = st.executeQuery("select cm_code from cm_mstr where "
                            + " cm_code = " + "'" + list[0] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }

                    if (j == 0) {
                        st.executeUpdate(" insert into cm_mstr "
                                + "(cm_code, cm_name, cm_line1, cm_line2, cm_line3, cm_city, cm_state, cm_zip, cm_country, cm_phone, cm_email, cm_dateadd, cm_datemod, "
                                + " cm_ar_acct, cm_ar_cc, cm_bank, cm_curr, cm_terms ) "
                                + " values ( "
                                + "'" + list[0] + "'" + ","
                                + "'" + list[1] + "'" + ","
                                + "'" + list[2] + "'" + ","
                                + "'" + list[3] + "'" + ","
                                + "'" + list[4] + "'" + ","
                                + "'" + list[5] + "'" + ","
                                + "'" + list[6] + "'" + ","
                                + "'" + list[7] + "'" + ","
                                + "'" + list[8] + "'" + ","
                                + "'" + list[9] + "'" + ","
                                + "'" + list[10] + "'" + ","
                                + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" + ","
                                + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" + ","
                                + "'" + x[0] + "'" + ","
                                + "'" + x[1] + "'" + ","
                                + "'" + x[3] + "'" + ","
                                + "'" + x[2] + "'" + ","
                                + "'" + x[4] + "'"                                       
                                + " );"
                        );

                        // now add default shipto with same shipcode as billcode
                        st.executeUpdate(" insert into cms_det "
                                + "(cms_code, cms_shipto, cms_name, cms_line1, cms_line2, cms_line3, cms_city, cms_state, cms_zip, cms_country ) "
                                + " values ( "
                                + "'" + list[0] + "'" + ","
                                + "'" + list[0] + "'" + ","
                                + "'" + list[11] + "'" + ","
                                + "'" + list[12] + "'" + ","
                                + "'" + list[13] + "'" + ","
                                + "'" + list[14] + "'" + ","
                                + "'" + list[15] + "'" + ","
                                + "'" + list[16] + "'" + ","
                                + "'" + list[17] + "'" + ","
                                + "'" + list[18] + "'"
                                + " );"
                        );

                    }
                
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;
    }
    
    public static boolean addCustMstrWShipTo(ArrayList<String> list, String delim) {
        boolean myreturn = false;
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
                String[] ld = null;

                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);

                    res = st.executeQuery("select cm_code from cm_mstr where "
                            + " cm_code = " + "'" + ld[0] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }

                    if (j == 0) {
                        st.executeUpdate(" insert into cm_mstr "
                                + "(cm_code, cm_site, cm_name, cm_line1, cm_line2, cm_line3, cm_city, cm_state, cm_zip, cm_country, " +
                                  " cm_dateadd, cm_datemod, cm_usermod, "
                                + " cm_group, cm_market, cm_creditlimit, cm_onhold, cm_carrier, cm_terms, cm_freight_type, cm_price_code, "
                                + " cm_disc_code, cm_tax_code, cm_salesperson, cm_ar_acct, cm_ar_cc, cm_remarks, cm_misc1, cm_bank, cm_curr, "
                                + " cm_logo, cm_ps_jasper, cm_iv_jasper, cm_label, cm_phone, cm_email ) "
                                + " values ( "
                                + "'" + ld[0] + "'" + ","
                                + "'" + ld[1] + "'" + ","
                                + "'" + ld[2] + "'" + ","
                                + "'" + ld[3] + "'" + ","
                                + "'" + ld[4] + "'" + ","
                                + "'" + ld[5] + "'" + ","
                                + "'" + ld[6] + "'" + ","
                                + "'" + ld[7] + "'" + ","
                                + "'" + ld[8] + "'" + ","
                                + "'" + ld[9] + "'" + ","        
                                + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" + ","
                                + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" + ","
                                + "'" + bsmf.MainFrame.userid + "'" + ","
                                + "'" + ld[10] + "'" + ","
                                + "'" + ld[11] + "'" + ","
                                + "'" + ld[12] + "'" + ","
                                + "'" + ld[13] + "'" + ","
                                + "'" + ld[14] + "'" + ","
                                + "'" + ld[15] + "'" + ","
                                + "'" + ld[16] + "'" + ","
                                + "'" + ld[17] + "'" + ","
                                + "'" + ld[18] + "'" + ","
                                + "'" + ld[19] + "'" + ","
                                + "'" + ld[20] + "'" + ","
                                + "'" + ld[21] + "'" + ","
                                + "'" + ld[22] + "'" + ","
                                + "'" + ld[23] + "'" + ","
                                + "'" + ld[24] + "'" + ","
                                + "'" + ld[25] + "'" + ","
                                + "'" + ld[26] + "'" + ","
                                + "'" + ld[27] + "'" + ","
                                + "'" + ld[28] + "'" + ","
                                + "'" + ld[29] + "'" + ","
                                + "'" + ld[30] + "'" + ","
                                + "'" + ld[31] + "'" + ","
                                + "'" + ld[32] + "'"
                                + " );"
                        );

                        // now add default shipto with same shipcode as billcode
                        st.executeUpdate(" insert into cms_det "
                                + "(cms_code, cms_shipto, cms_name, cms_line1, cms_line2, cms_line3, cms_city, cms_state, cms_zip, cms_country ) "
                                + " values ( "
                                + "'" + ld[0] + "'" + ","
                                + "'" + ld[0] + "'" + ","
                                + "'" + ld[2] + "'" + ","
                                + "'" + ld[3] + "'" + ","
                                + "'" + ld[4] + "'" + ","
                                + "'" + ld[5] + "'" + ","
                                + "'" + ld[6] + "'" + ","
                                + "'" + ld[7] + "'" + ","
                                + "'" + ld[8] + "'" + ","
                                + "'" + ld[9] + "'"
                                + " );"
                        );

                    }
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myreturn;
    }

    public static boolean addCustShipToMstr(ArrayList<String> list, String delim) {
        boolean myreturn = false;
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
                String[] ld = null;

                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);

                    res = st.executeQuery("select cms_code from cms_det where "
                            + " cms_code = " + "'" + ld[0] + "'" + " AND cms_shipto = " + "'" + ld[1] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }

                    if (j == 0) {
                        st.executeUpdate(" insert into cms_det "
                                + "(cms_code, cms_shipto, cms_name, cms_line1, cms_line2, cms_line3, cms_city, cms_state, cms_zip, cms_country, cms_plantcode, cms_contact, cms_phone, cms_email, cms_misc ) "
                                + " values ( "
                                + "'" + ld[0] + "'" + ","
                                + "'" + ld[1] + "'" + ","
                                + "'" + ld[2] + "'" + ","
                                + "'" + ld[3] + "'" + ","
                                + "'" + ld[4] + "'" + ","
                                + "'" + ld[5] + "'" + ","
                                + "'" + ld[6] + "'" + ","
                                + "'" + ld[7] + "'" + ","
                                + "'" + ld[8] + "'" + ","
                                + "'" + ld[9] + "'" + ","
                                + "'" + ld[10] + "'" + ","
                                + "'" + ld[11] + "'" + ","
                                + "'" + ld[12] + "'" + ","
                                + "'" + ld[13] + "'" + ","
                                + "'" + ld[14] + "'"
                                + " );"
                        );
                    }
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myreturn;
    }

    
    
    
    
    public static ArrayList getpanelslist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select panel_id from panel_mstr ;");
                while (res.next()) {
                    myarray.add(res.getString("panel_id"));

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getScacAll() {
        ArrayList myarray = new ArrayList();
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
                res = st.executeQuery("select car_code from car_mstr order by car_code;");
                while (res.next()) {
                    myarray.add(res.getString("car_code"));

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList<String> getScacCarrierOnly() {
        ArrayList<String> myarray = new ArrayList<String>();
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

                res = st.executeQuery("select car_code from car_mstr where car_type = 'carrier' order by car_code;");
                while (res.next()) {
                    myarray.add(res.getString("car_code"));

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getScacGroupOnly() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select car_code from car_mstr where car_type = 'group' order by car_code;");
                while (res.next()) {
                    myarray.add(res.getString("car_code"));

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        return myarray;

    }

    public static ArrayList getScacsOfGroup(String code) {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select card_carrier from car_det where card_code = " + "'" + code + "'" + " order by card_carrier;");
                while (res.next()) {
                    myarray.add(res.getString("card_carrier"));

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getfreighttermslist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select frt_code from frt_mstr order by frt_code;");
                while (res.next()) {
                    myarray.add(res.getString("frt_code"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList gettaxcodelist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select tax_code from tax_mstr order by tax_code  ;");
                while (res.next()) {
                    myarray.add(res.getString("tax_code"));

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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList<String[]> getTaxPercentElementsApplicableByCust(String cust) {

        ArrayList<String[]> myarray = new ArrayList<String[]>();
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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent, taxd_type from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " inner join cm_mstr on cm_tax_code = tax_code and cm_code = " + "'" + cust + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    String[] elements = {res.getString("taxd_desc"), res.getString("taxd_percent"), res.getString("taxd_type")};
                    myarray.add(elements);
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList<String[]> getTaxPercentElementsApplicableByTaxCode(String taxcode) {

        ArrayList<String[]> myarray = new ArrayList<String[]>();
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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent, taxd_type from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " where tax_code = " + "'" + taxcode + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    String[] elements = {res.getString("taxd_desc"), res.getString("taxd_percent"), res.getString("taxd_type")};
                    myarray.add(elements);
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList<String[]> getTaxPercentElementsApplicableByItem(String item) {

        ArrayList<String[]> myarray = new ArrayList();

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

                res = st.executeQuery("select taxd_id, taxd_parentcode, taxd_desc, taxd_percent, taxd_type from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " inner join item_mstr on it_taxcode = tax_code "
                        + " where it_item = " + "'" + item + "'"
                        + " order by taxd_id ;");
                while (res.next()) {
                    String[] elements = {res.getString("taxd_desc"), res.getString("taxd_percent"), res.getString("taxd_type")};
                    myarray.add(elements);
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static double getTaxPercentApplicableByCust(String cust) {
        double taxpercent = 0.00;

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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " inner join cm_mstr on cm_tax_code = tax_code and cm_code = " + "'" + cust + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    taxpercent += res.getDouble("taxd_percent");
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return taxpercent;

    }

    public static double getTaxPercentApplicableByTaxCode(String taxcode) {
        double taxpercent = 0.00;

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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " where tax_code = " + "'" + taxcode + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    taxpercent += res.getDouble("taxd_percent");
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return taxpercent;

    }
    
    public static double getTaxAmtApplicableByItem(String item, double amt) {
        double taxamt = 0.00;
        double taxpercent = 0.00;

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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " inner join item_mstr on it_taxcode = tax_code and it_item = " + "'" + item + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    taxpercent += res.getDouble("taxd_percent");
                }

                if (taxpercent > 0) {
                    taxamt = (amt * (taxpercent / 100));
                } else {
                    taxamt = 0;
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return taxamt;

    }

    public static double getTaxAmtApplicableByCust(String cust, double amt) {
        double taxamt = 0.00;
        double taxpercent = 0.00;

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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " inner join cm_mstr on cm_tax_code = tax_code and cm_code = " + "'" + cust + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    taxpercent += res.getDouble("taxd_percent");
                }

                if (taxpercent > 0) {
                    taxamt = (amt * (taxpercent / 100));
                } else {
                    taxamt = 0;
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return taxamt;

    }

    public static double getTaxAmtApplicableByTaxCode(String taxcode, double amt) {
        double taxamt = 0.00;
        double taxpercent = 0.00;

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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " where tax_code  = " + "'" + taxcode + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    taxpercent += res.getDouble("taxd_percent");
                }

                if (taxpercent > 0) {
                    taxamt = (amt * (taxpercent / 100));
                } else {
                    taxamt = 0;
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return taxamt;

    }

    public static double getTaxAmtApplicableByOrder(String order, double amt) {
        double taxamt = 0.00;
        double taxpercent = 0.00;

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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " inner join so_mstr on so_taxcode = tax_code and so_nbr = " + "'" + order + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    taxpercent += res.getDouble("taxd_percent");
                }

                if (taxpercent > 0) {
                    taxamt = (amt * (taxpercent / 100));
                } else {
                    taxamt = 0;
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return taxamt;

    }

   
  
   
    
   
    public static ArrayList getWeekNbrByDateTimeClock(String mydate) {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select week(indate) as 'myweek', date_add(indate, interval (8 - dayofweek(indate)) % 7 day) as 'sun' from time_clock where indate >= "
                        + "'" + mydate + "'" + " group by week(indate), indate ;");
                while (res.next()) {
                    myarray.add(res.getString("myweek") + " = " + res.getString("sun"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getWeekNbrByDate(String mydate, String days) {
        ArrayList myarray = new ArrayList();
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

                if (dbtype.equals("sqlite")) {
                    res = st.executeQuery("select c.myweek as 'myweek', date(c.mydate,'-6 days') as 'sun' from (select strftime('%W',date(" + "'" + mydate + "'" + ",'+' || mock_nbr || ' days')) as 'myweek',"
                            + " date(" + "'" + mydate + "'" + ",'+' || mock_nbr || ' days') as 'mydate' from mock_mstr where mock_nbr <= " + "'" + days + "'"
                            + " group by myweek) as 'c' ;");
                } else {
                    res = st.executeQuery("select week(tr_eff_date) as 'myweek', date_add(tr_eff_date, interval (8 - dayofweek(tr_eff_date)) % 7 day) as 'sun' from tran_mstr where tr_eff_date >= "
                            + "'" + mydate + "'" + " group by week(tr_eff_date), tr_eff_date ;");
                }
                while (res.next()) {
                    myarray.add(res.getString("myweek") + " = " + res.getString("sun"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getWeekNbrByDate(String mydate) {
        ArrayList myarray = new ArrayList();
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

                if (dbtype.equals("sqlite")) {

                } else {
                    res = st.executeQuery("select week(tr_eff_date) as 'myweek', date_add(tr_eff_date, interval (8 - dayofweek(tr_eff_date)) % 7 day) as 'sun' from tran_mstr where tr_eff_date >= "
                            + "'" + mydate + "'" + " group by week(tr_eff_date), tr_eff_date ;");
                }
                while (res.next()) {
                    myarray.add(res.getString("myweek") + " = " + res.getString("sun"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getWeekNbrByFromDateToDate(String fromdate, String todate) {
        ArrayList myarray = new ArrayList();
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
                if (dbtype.equals("sqlite")) {
                    res = st.executeQuery("with recursive dates(date) as ( values ( "
                            + "'" + fromdate + "'" + " ) union all select date(date, '+7 day') from dates "
                            + " where date < date( " + "'" + todate + "'" + ") ) select date, strftime('%W',date) + 1 as 'myweek' from dates ; ");
                } else {
                    res = st.executeQuery("select week(tr_eff_date) as 'myweek', date_add(tr_eff_date, interval (8 - dayofweek(tr_eff_date)) % 7 day) as 'sun' from tran_mstr where tr_eff_date >= "
                            + "'" + fromdate + "'" + " group by week(tr_eff_date), tr_eff_date ;");
                }
                while (res.next()) {
                    myarray.add(res.getString("myweek"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getdeptanddesclist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select dept_id, dept_desc from dept_mstr ;");
                while (res.next()) {
                    myarray.add(res.getString("dept_id") + " = " + res.getString("dept_desc"));

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getpsmstrcomp(String item) {
        ArrayList myarray = new ArrayList();

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

                res = st.executeQuery("select ps_child from pbm_mstr "
                        + " inner join bom_mstr on bom_item = ps_parent and bom_primary = '1' "
                        + " where ps_parent = " + "'" + item + "'"
                        + " AND ps_op <> '0' ;");
                while (res.next()) {
                    myarray.add(res.getString("ps_child"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getzerolevelpsmstr() {
        ArrayList<String> myarray = new ArrayList<String>();
        ArrayList<String> mylist = new ArrayList<String>();

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
                res = st.executeQuery("select it_item from item_mstr where it_item <> '' and it_code = 'M';");
                while (res.next()) {
                    myarray.add(res.getString("it_item"));
                }
                for (String myitem : myarray) {
                    i = 0;
                    res = st.executeQuery("select ps_child from pbm_mstr "
                            + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                            + " where ps_child = " + "'" + myitem.toString() + "'" + " limit 1 ;");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        mylist.add(myitem);
                    }
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return mylist;

    }

    public static void setzerolevelpsmstr() {
        ArrayList<String> myarray = new ArrayList<String>();
        ArrayList<String> mylist = new ArrayList<String>();

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
                // let's first reset all levels to -1
                st.executeUpdate(" update item_mstr set it_level = '-1'; ");

                res = st.executeQuery("select it_item from item_mstr where it_item <> '' and (it_code = 'M' or it_code = 'A') ;");
                while (res.next()) {
                    myarray.add(res.getString("it_item"));
                }
                for (String myitem : myarray) {
                    i = 0;
                    res = st.executeQuery("select ps_child from pbm_mstr "
                            + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                            + " where ps_child = " + "'" + myitem.toString() + "'" + " limit 1 ;");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        mylist.add(myitem);
                        st.executeUpdate(
                                " update item_mstr set it_level = " + "'" + '0' + "'"
                                + " where it_item = " + "'" + myitem + "'" + ";");
                    }
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }

    public static ArrayList getNextLevelpsmstr(int level) {
        ArrayList<String> myarray = new ArrayList<String>();
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
                res = st.executeQuery("select ps_child from pbm_mstr " +
                        " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "  +
                        " inner join item_mstr on it_item = ps_parent where it_level = " + "'" + level + "'" + ";");
                while (res.next()) {
                    myarray.add(res.getString("ps_child"));
                }
                // convert to hashset to remove duplicates and then convert back to ArrayList
                HashSet s = new HashSet();
                s.addAll(myarray);
                myarray.clear();
                myarray.addAll(s);
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

        return myarray;
    }

    public static void updateItemlevel(ArrayList list, int level) {

        try {
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {

                for (Object myitem : list) {
                    st.executeUpdate(
                            " update item_mstr set it_level = " + "'" + level + "'"
                            + " where it_item = " + "'" + myitem + "'" + ";");
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }

    public static int createMRPByLevel(int level, String site, String fromitem, String toitem) {
        int rows = 0;
        String sql = "";
        if (dbtype.equals("sqlite")) {
                    sql =  "insert into mrp_mstr (mrp_item, mrp_qty, mrp_date, mrp_type, mrp_site) "
                        + " select ps_child, sum(mrp_qty * ps_qty_per) as sum, " 
                        + " date(mrp_date, ('-' || it_leadtime || ' day')), 'derived', ? from mrp_mstr "
                        + " inner join pbm_mstr on ps_parent = mrp_item "
                        + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "        
                        + " inner join item_mstr on it_item = ps_parent "
                        + " where it_mrp = '1' and it_level = ? "
                        + " AND ps_child >= ? " 
                        + " AND ps_child <= ? " 
                        + " group by ps_child, mrp_date order by ps_child, mrp_date; ";
                    } else  {
                    sql = "insert into mrp_mstr (mrp_item, mrp_qty, mrp_date, mrp_type, mrp_site) "
                        + " select ps_child, sum(mrp_qty * ps_qty_per) as sum, " 
                        + " date_add(mrp_date, " + "interval -" + "cast(it_leadtime as char)" + " day), " 
                        + " 'derived', ? from mrp_mstr "
                        + " inner join pbm_mstr on ps_parent = mrp_item "
                        + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "        
                        + " inner join item_mstr on it_item = ps_parent "
                        + " where it_mrp = '1' and it_level = ? " 
                        + " AND ps_child >= ? " 
                        + " AND ps_child <= ? "
                        + " group by ps_child, mrp_date, it_leadtime order by ps_child, mrp_date; ";
                    } 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, site);
        ps.setString(2, String.valueOf(level));
        ps.setString(3, fromitem);
        ps.setString(4, toitem);
        rows = ps.executeUpdate();
        } catch (SQLException s) {
	       MainFrame.bslog(s);
        }
        return rows;
    }

   
    public static int createMRPZeroLevel(String site) {
        int rows = 0;
        String sql = " insert into mrp_mstr (mrp_item, mrp_qty, mrp_date, mrp_ref, mrp_type, mrp_line, mrp_site ) "
                        + " select sod_item, (sod_ord_qty - sod_shipped_qty), sod_due_date, sod_nbr, 'demand', sod_line, sod_site from sod_det "
                        + " inner join  item_mstr on sod_item = it_item and it_level = '0' where it_mrp = '1' " 
                        + " AND sod_status <> " + "'" + getGlobalProgTag("closed") + "'"
                        + " and sod_site = ? " + ";";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, site);
        rows = ps.executeUpdate();
        } catch (SQLException s) {
	       MainFrame.bslog(s);
        }
        return rows;
    }

    public static int deleteAllMRP(String site, String fromitem, String toitem) {
        int rows = 0;
        String sql = " delete from mrp_mstr where mrp_site = ? " 
                        + " AND mrp_item >= ? "
                        + " AND mrp_item <= ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, site);
        ps.setString(2, fromitem);
        ps.setString(3, toitem);
        rows = ps.executeUpdate();
        } catch (SQLException s) {
	       MainFrame.bslog(s);
        }
        return rows;
    }

    public static void deleteNonMasterTransactionData() {
       
       try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(" delete from gl_hist;");
           st.executeUpdate(" delete from gl_tran;");
           st.executeUpdate(" delete from acb_mstr;");
           st.executeUpdate(" delete from so_mstr;");
           st.executeUpdate(" delete from so_tax;");
           st.executeUpdate(" delete from sod_det;");
           st.executeUpdate(" delete from sod_tax;");
           st.executeUpdate(" delete from sos_det;");
           st.executeUpdate(" delete from po_mstr;");
           st.executeUpdate(" delete from pod_mstr;");
           st.executeUpdate(" delete from ar_mstr;");
           st.executeUpdate(" delete from ard_mstr;");
           st.executeUpdate(" delete from art_tax;");
           st.executeUpdate(" delete from ap_mstr;");
           st.executeUpdate(" delete from apd_mstr;");
           st.executeUpdate(" delete from ar_mstr;");
           st.executeUpdate(" delete from do_mstr;");
           st.executeUpdate(" delete from dod_mstr;");
           st.executeUpdate(" delete from po_mstr;");
           st.executeUpdate(" delete from in_mstr;");
           st.executeUpdate(" delete from po_mstr;");
           st.executeUpdate(" delete from label_mstr;");
           st.executeUpdate(" delete from pay_det;");
           st.executeUpdate(" delete from pay_line;");
           st.executeUpdate(" delete from pay_mstr;");
           st.executeUpdate(" delete from pos_mstr;");
           st.executeUpdate(" delete from pos_det;");
           st.executeUpdate(" delete from qual_mstr;");
           st.executeUpdate(" delete from ship_mstr;");
           st.executeUpdate(" delete from ship_det;");
           st.executeUpdate(" delete from ship_log;");
           st.executeUpdate(" delete from ship_tree;");
           st.executeUpdate(" delete from shs_det;");
           st.executeUpdate(" delete from srl_mstr;");
           st.executeUpdate(" delete from sv_mstr;");
           st.executeUpdate(" delete from svd_det;");
           st.executeUpdate(" delete from time_clock;");
           st.executeUpdate(" delete from tran_mstr;");
           st.executeUpdate(" delete from vod_mstr;");
           
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }

  

    }


        
        
    // BOM Tree by Operation 
    public static class MenuInfo {
        public String menuname;
        public String menuvisible;
 
        public MenuInfo(String name, String visible) {
            menuname = name;
            menuvisible = visible;
           
        }
 
        public String toString() {
            return menuname;
        }
    }
        
    public static DefaultMutableTreeNode getMenusAsTree(String mymenu, String myvisible)  {  
      MenuInfo parent = new MenuInfo(mymenu,myvisible);
      DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(parent);
      ArrayList<String> mychildren = new ArrayList<String>();
        mychildren = OVData.getMenuTreeAll(mymenu);
        
        for ( String myvalue : mychildren) {
             String[] recs = myvalue.split(",",-1);
             MenuInfo leaf = new MenuInfo(recs[0].toString(), recs[6].toString());
                //     if (recs[6].toString().compareTo("0") == 0) {
               //       leaf = recs[0] + "-";
                //    }  else {
                //         leaf = recs[0];
                //     }
             if (recs[1].toString().compareTo("JMenuItem") == 0) {
                DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(leaf); 
                mynode.add(childnode);
             } else {
                  DefaultMutableTreeNode menunode = getMenusAsTree(leaf.toString(), leaf.menuvisible); 
                  mynode.add(menunode);
             }
          
        }
       return mynode;
      }
    
    public static DefaultMutableTreeNode get_op_nodes_new(String item, String bomid)  {  
       DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(item);
       ArrayList<String> myops = new ArrayList<String>();
        //myops = OVData.getItemRoutingOPs(mypart);  //based on itr_cost
         myops = invData.getItemWFOPs(item);   // based on it_wf and wf_mstr
        for ( String myvalue : myops) {
          //  DefaultMutableTreeNode thisop = new DefaultMutableTreeNode(myvalue);
          //  mynode.add(thisop);
            DefaultMutableTreeNode opnode = new DefaultMutableTreeNode(myvalue);
            
            opnode = OVData.get_nodes_by_op_detail_new(item, item, myvalue, bomid);
            mynode.add(opnode);
        }
       return mynode;
      }
    
    public static DefaultMutableTreeNode get_nodes_by_op_detail_new(String root, String item, String myop, String rootbom)  {
        //  bsmf.MainFrame.show(root + "/" + mypart + "/" + myop);
        String myroot = "";
            if (root.toLowerCase().equals(item.toLowerCase()))
            myroot = myop;
        else
            myroot = item;
         DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(myroot);
         
        
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlistbyopnew(item, myop, rootbom);
     //   mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(item.toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                    mfgnode = get_op_nodes(value[1]);
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1]);   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     } 
     
    
    public static DefaultMutableTreeNode get_op_nodes(String item)  {  
       DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(item);
       ArrayList<String> myops = new ArrayList<String>();
        //myops = OVData.getItemRoutingOPs(mypart);  //based on itr_cost
         myops = invData.getItemWFOPs(item);   // based on it_wf and wf_mstr
        for ( String myvalue : myops) {
          //  DefaultMutableTreeNode thisop = new DefaultMutableTreeNode(myvalue);
          //  mynode.add(thisop);
            DefaultMutableTreeNode opnode = new DefaultMutableTreeNode(myvalue);
            
            opnode = OVData.get_nodes_by_op_detail(item, item, myvalue);
            mynode.add(opnode);
        }
       return mynode;
      }
      
    public static DefaultMutableTreeNode get_nodes_by_op_detail(String root, String item, String myop)  {
        //  bsmf.MainFrame.show(root + "/" + mypart + "/" + myop);
        String myroot = "";
            if (root.toLowerCase().equals(item.toLowerCase()))
            myroot = myop;
        else
            myroot = item;
         DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(myroot);
         
        
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlistbyop(item, myop);
     //   mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(item.toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                    mfgnode = get_op_nodes(value[1]);
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1]);   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     } 
      
    public static DefaultMutableTreeNode get_nodes_by_op(String item, String myop)  {
       DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(myop);
        String[] newpart = item.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlistbyopWCost(newpart[0], myop);
     //   mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                   mfgnode = get_nodes_without_op(value[1] + "___" + value[4] + "___" + bsFormatDouble5(bsParseDouble(value[3])) + "___" + bsFormatDouble5(bsParseDouble(value[5])));
                   
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1] + "___" + value[4] + "___" + bsFormatDouble5(bsParseDouble(value[3])) + "___" + bsFormatDouble5(bsParseDouble(value[5])));   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     }
      
    public static DefaultMutableTreeNode get_nodes_without_op(String item)  {
        DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(item);
        ArrayList<String> mylist = new ArrayList<String>();
        ArrayList<String> myops = new ArrayList<String>();
        myops = OVData.getpsmstrlist(item);
        mylist = OVData.getpsmstrlist(item);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(item.toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                    mfgnode = get_nodes_without_op(value[1]);
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1]);   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     }
          
    public static DefaultMutableTreeNode get_nodes_by_op_stripped(String root, String item, String myop)  {
        String myroot = "";
            if (root.toLowerCase().equals(item.toLowerCase()))
            myroot = myop;
        else
            myroot = item;
         DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(myroot);
         
        
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlistbyop(item, myop);
     //   mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(item.toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                    mfgnode = get_nodes_by_op_stripped(root, value[1] ,myop);
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1]);   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     }
    // END of BOM Tree by Operation 
    
    public static ArrayList getpsmstrlist(String item) {
        ArrayList myarray = new ArrayList();
        String mystring = "";
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

                res = st.executeQuery("select ps_parent, ps_child, ps_type, ps_qty_per, it_desc, itc_total, ps_op from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                        + " inner join item_mstr on it_item = ps_child "
                        + " inner join  item_cost on itc_item = it_item and itc_set = 'standard' "
                        + " where ps_parent = " + "'" + item + "'" + ";");
                while (res.next()) {
                    mystring = res.getString("ps_parent") + ","
                            + res.getString("ps_child") + ","
                            + res.getString("ps_type") + ","
                            + res.getString("ps_qty_per") + ","
                            + res.getString("it_desc") + ","
                            + res.getString("itc_total") + ","
                            + res.getString("ps_op");

                    myarray.add(mystring);

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        return myarray;

    }

    public static ArrayList getpsmstrlist(String item, String bom) {
        ArrayList myarray = new ArrayList();
        String mystring = "";
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

                res = st.executeQuery("select ps_parent, ps_child, ps_type, ps_qty_per, it_desc, itc_total, ps_op from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom  "
                        + " inner join item_mstr on it_item = ps_child "
                        + " inner join  item_cost on itc_item = it_item and itc_set = 'standard' "
                        + " where ps_parent = " + "'" + item + "'"
                        + " and ps_bom = " + "'" + bom + "'" + ";");
                while (res.next()) {
                    mystring = res.getString("ps_parent") + ","
                            + res.getString("ps_child") + ","
                            + res.getString("ps_type") + ","
                            + res.getString("ps_qty_per") + ","
                            + res.getString("it_desc") + ","
                            + res.getString("itc_total") + ","
                            + res.getString("ps_op");

                    myarray.add(mystring);

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        return myarray;

    }

    
    public static ArrayList getPsMstrCompOpOnly(String item) {
        ArrayList myarray = new ArrayList();
        String mystring = "";
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

                res = st.executeQuery("select ps_child, ps_type from pbm_mstr " +
                        " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' " +
                        " where ps_parent = " + "'" + item + "'" + ";");
                while (res.next()) {
                    myarray.add(res.getString("ps_child") + "," + res.getString("ps_type"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getpsmstrlistwithOp(String item) {
        ArrayList myarray = new ArrayList();
        String mystring = "";
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

                res = st.executeQuery("select ps_parent, ps_child, ps_type, ps_op from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                        + " where ps_parent = " + "'" + item + "';");
                while (res.next()) {
                    mystring = res.getString("ps_parent") + ","
                            + res.getString("ps_child") + ","
                            + res.getString("ps_type") + ","
                            + res.getString("ps_op");
                    myarray.add(mystring);

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getpsmstrlistbyop(String item, String myop) {
        ArrayList myarray = new ArrayList();
        String mystring = "";
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

                res = st.executeQuery("select ps_parent, ps_child, ps_type, ps_qty_per, it_desc from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                        + " inner join item_mstr on it_item = ps_child "
                        + " where ps_parent = " + "'" + item + "'"
                        + " and ps_op = " + "'" + myop + "'"       
                        + ";");
                while (res.next()) {
                    mystring = res.getString("ps_parent") + ","
                            + res.getString("ps_child") + ","
                            + res.getString("ps_type") + ","
                            + res.getString("ps_qty_per") + ","
                            + res.getString("it_desc").replace(",","");

                    myarray.add(mystring);

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getpsmstrlistbyopnew(String item, String myop, String bomid) {
        ArrayList myarray = new ArrayList();
        String mystring = "";
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

                res = st.executeQuery("select ps_parent, ps_child, ps_type, ps_qty_per, it_desc from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom "
                        + " inner join item_mstr on it_item = ps_child "
                        + " where ps_parent = " + "'" + item + "'"
                        + " and ps_op = " + "'" + myop + "'"  
                        + " and ps_bom = " + "'" + bomid + "'"        
                        + ";");
                while (res.next()) {
                    mystring = res.getString("ps_parent") + ","
                            + res.getString("ps_child") + ","
                            + res.getString("ps_type") + ","
                            + res.getString("ps_qty_per") + ","
                            + res.getString("it_desc").replace(",","");

                    myarray.add(mystring);

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    
    public static ArrayList getpsmstrlistbyopWCost(String item, String myop) {
        ArrayList myarray = new ArrayList();
        String mystring = "";
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
                res = st.executeQuery("select ps_parent, ps_child, ps_type, ps_qty_per, it_desc, itc_total from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                        + " inner join item_mstr on it_item = ps_child "
                        + " inner join  item_cost on itc_item = it_item and itc_set = 'standard' "
                        + " where ps_parent = " + "'" + item + "'"
                        + " and ps_op = " + "'" + myop + "'" + ";");
                while (res.next()) {
                    mystring = res.getString("ps_parent") + ","
                            + res.getString("ps_child") + ","
                            + res.getString("ps_type") + ","
                            + res.getString("ps_qty_per") + ","
                            + res.getString("it_desc") + ","
                            + res.getString("itc_total");

                    myarray.add(mystring);

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }
 
    public static Set<String> getpsmstrparents(String item) {
        Set<String> myarray = new LinkedHashSet<String>();
        String mystring = "";
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

                res = st.executeQuery("select ps_parent from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                        + " where ps_child = " + "'" + item + "';");
                // res = st.executeQuery("select ps_parent from pbm_mstr " +
                //            " where ps_child = " + "'" + mypart.toString() + "';" );
                while (res.next()) {
                    mystring = res.getString("ps_parent");
                    myarray.add(mystring);
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static Set<String> getpsmstrparents2(String item) {

        ArrayList<String> myarray1 = new ArrayList<String>();
        ArrayList<String> myarray2 = new ArrayList<String>();
        ArrayList<String> myarray3 = new ArrayList<String>();
        ArrayList<String> myarray4 = new ArrayList<String>();
        ArrayList<String> myarray5 = new ArrayList<String>();

       // ArrayList fg = new ArrayList();
        Set<String> fg = new LinkedHashSet<String>();
        String mystring = "";
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

                res = st.executeQuery("select ps_parent, it_type from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                        + " inner join item_mstr on it_item = ps_parent "
                        + " where ps_child = " + "'" + item + "';");
                // res = st.executeQuery("select ps_parent from pbm_mstr " +
                //            " where ps_child = " + "'" + mypart.toString() + "';" );
                while (res.next()) {
                    if (res.getString("it_type").compareTo("FG") == 0) {
                        fg.add(res.getString("ps_parent").toString());
                    } else {
                        myarray1.add(res.getString("ps_parent").toString());
                    }
                }
                //    bsmf.MainFrame.show("firstpass=" + String.valueOf(myarray1.size()));
                // let's loop through first parent level and see if more parents
                for (String myvalue : myarray1) {
                    res = st.executeQuery("select ps_parent, it_type from pbm_mstr "
                            + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                            + " inner join item_mstr on it_item = ps_parent "
                            + " where ps_child = " + "'" + myvalue.toString() + "';");
                    // res = st.executeQuery("select ps_parent from pbm_mstr " +
                    //            " where ps_child = " + "'" + mypart.toString() + "';" );
                    while (res.next()) {
                        if (res.getString("it_type").compareTo("FG") == 0) {
                            fg.add(res.getString("ps_parent").toString());
                        } else {
                            myarray2.add(res.getString("ps_parent").toString());
                        }
                    }
                }
                //    bsmf.MainFrame.show("array1" + String.valueOf(myarray1.size()));
                myarray1.clear();
                // let's loop through second parent level and see if more parents
                for (String myvalue : myarray2) {
                    res = st.executeQuery("select ps_parent, it_type from pbm_mstr "
                            + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                            + " inner join item_mstr on it_item = ps_parent "
                            + " where ps_child = " + "'" + myvalue.toString() + "';");
                    // res = st.executeQuery("select ps_parent from pbm_mstr " +
                    //            " where ps_child = " + "'" + mypart.toString() + "';" );
                    while (res.next()) {
                        if (res.getString("it_type").compareTo("FG") == 0) {
                            fg.add(res.getString("ps_parent").toString());
                        } else {
                            myarray3.add(res.getString("ps_parent").toString());
                        }
                    }
                }
                //   bsmf.MainFrame.show("array2" + String.valueOf(myarray2.size()));
                myarray2.clear();
                // let's loop through third parent level and see if more parents
                for (String myvalue : myarray3) {
                    res = st.executeQuery("select ps_parent, it_type from pbm_mstr "
                            + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                            + " inner join item_mstr on it_item = ps_parent "
                            + " where ps_child = " + "'" + myvalue.toString() + "';");
                    // res = st.executeQuery("select ps_parent from pbm_mstr " +
                    //            " where ps_child = " + "'" + mypart.toString() + "';" );
                    while (res.next()) {
                        if (res.getString("it_type").compareTo("FG") == 0) {
                            fg.add(res.getString("ps_parent").toString());
                        } else {
                            myarray4.add(res.getString("ps_parent").toString());
                        }
                    }
                }
                //      bsmf.MainFrame.show("array3" + String.valueOf(myarray3.size()));
                myarray3.clear();
                // let's loop through fourth parent level and see if more parents
                for (String myvalue : myarray4) {
                    res = st.executeQuery("select ps_parent, it_type from pbm_mstr "
                            + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                            + " inner join item_mstr on it_item = ps_parent "
                            + " where ps_child = " + "'" + myvalue.toString() + "';");
                    // res = st.executeQuery("select ps_parent from pbm_mstr " +
                    //            " where ps_child = " + "'" + mypart.toString() + "';" );
                    while (res.next()) {
                        if (res.getString("it_type").compareTo("FG") == 0) {
                            fg.add(res.getString("ps_parent").toString());
                        } else {
                            myarray5.add(res.getString("ps_parent").toString());
                        }
                    }
                }
                //          bsmf.MainFrame.show("array4" + String.valueOf(myarray4.size()));
                myarray4.clear();
                // let's loop through fifth parent level and see if more parents
                for (String myvalue : myarray5) {
                    res = st.executeQuery("select ps_parent, it_type from pbm_mstr "
                            + " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' "
                            + " inner join item_mstr on it_item = ps_parent "
                            + " where ps_child = " + "'" + myvalue.toString() + "';");
                    // res = st.executeQuery("select ps_parent from pbm_mstr " +
                    //            " where ps_child = " + "'" + mypart.toString() + "';" );
                    while (res.next()) {
                        if (res.getString("it_type").compareTo("FG") == 0) {
                            fg.add(res.getString("ps_parent").toString());
                        }
                    }
                }
                myarray5.clear();

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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return fg;

    }
    
    public static String getDefaultBomID(String item) {
       String x = "";
       int i = 0;
       ArrayList<String> boms = new ArrayList<String>();
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

                res = st.executeQuery("select distinct ps_bom from pbm_mstr "
                        + " where ps_parent = " + "'" + item + "';");
                while (res.next()) {
                    i++;
                  boms.add(res.getString("ps_bom")); 
                }
                res.close();
                if (i == 0) {
                    return "";
                } else {
                    for (String s : boms) {
                        res = st.executeQuery("select bom_id from bom_mstr "
                        + " where bom_id = " + "'" + s + "'" +
                          " and bom_primary = '1' " + ";");
                        while (res.next()) {
                           x = res.getString("bom_id");
                        } 
                    }
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
        return x;

    }

    public static int getBomPbmCount(String bomid) {
       int x = 0;
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
                res = st.executeQuery("select * from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom "
                        + " where ps_bom = " + "'" + bomid + "';");
                while (res.next()) {
                    x++;
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;

    }

    public static ArrayList<String> getBOM(String item, String bomid) {
        ArrayList myarray = new ArrayList();
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
                res = st.executeQuery("select ps_child from pbm_mstr " +
                        " inner join bom_mstr on bom_id = ps_bom " +
                        " where ps_parent = " + "'" + item + "'" + 
                        " and ps_bom = " + "'" + bomid + "'" +
                        ";");
                while (res.next()) {
                    myarray.add(res.getString("ps_child"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

            
    public static boolean addItemMaster(ArrayList<String> list, String delim) {
                 boolean myreturn = false;
                 
                 java.util.Date now = new java.util.Date();
              DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                 
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
                String[] ld = null;
                 
                // now loop through comma delimited list and insert into item master table
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                    
                    
                    res = st.executeQuery("select it_item from item_mstr where " +
                                    " it_item = " + "'" + ld[0] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into item_mstr " 
                      + "(it_item, it_desc, it_site, it_code, it_prodline, it_loc, it_wh, it_lotsize, it_createdate, "
                            + "it_sell_price, it_pur_price, it_mtl_cost, it_ovh_cost, it_out_cost, it_type, it_group, "
                            + "it_drawing, it_rev, it_custrev, it_comments, "
                            + "it_uom, it_net_wt, it_ship_wt, "
                            + "it_leadtime, it_safestock, it_minordqty, it_mrp, it_sched, it_plan, it_wf, it_status ) "  
                            + " values ( " + 
                            "'" +  ld[0] + "'" + "," + 
                            "'" +  ld[1] + "'" + "," +
                            "'" +  ld[2] + "'" + "," +  
                            "'" +  ld[3] + "'" + "," +  
                            "'" +  ld[4] + "'" + "," +  
                            "'" +  ld[5] + "'" + "," +  
                            "'" +  ld[6] + "'" + "," +          
                            "'" +  ld[7].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  dfdate.format(now) + "'" + "," + 
                            "'" +  ld[8].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[9].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[10].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[11].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[12].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[13] + "'" + "," +  
                            "'" +  ld[14] + "'" + "," +  
                            "'" +  ld[15] + "'" + "," +  
                            "'" +  ld[16] + "'" + "," +  
                            "'" +  ld[17] + "'" + "," +  
                            "'" +  ld[18] + "'" + "," +  
                            "'" +  ld[19] + "'" + "," +  
                            "'" +  ld[20].replace(defaultDecimalSeparator, '.') + "'" + "," +
                            "'" +  ld[21].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[22].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[23].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[24].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  ld[25] + "'" + "," +  
                            "'" +  ld[26] + "'" + "," +  
                            "'" +  ld[27] + "'" + "," + 
                            "'" +  ld[28] + "'" + "," + "'" + "ACTIVE" + "'" + ");"
                           );
                    
                    } else {
                        st.executeUpdate(" update item_mstr set " +                      
                            " it_desc = " + "'" + ld[1] + "'" + "," +
                            " it_site = " + "'" +  ld[2] + "'" + "," +  
                            " it_code = " + "'" +  ld[3] + "'" + "," +  
                            " it_prodline = " +"'" +  ld[4] + "'" + "," +  
                            " it_loc = " + "'" +  ld[5] + "'" + "," +  
                            " it_wh = " + "'" +  ld[6] + "'" + "," +          
                            " it_lotsize = " + "'" +  ld[7].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_createdate = " + "'" +  dfdate.format(now) + "'" + "," + 
                            " it_sell_price = " + "'" +  ld[8].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_pur_price = " + "'" +  ld[9].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_mtl_cost = " + "'" +  ld[10].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_ovh_cost = " + "'" +  ld[11].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_out_cost = " + "'" +  ld[12].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_type = " + "'" +  ld[13] + "'" + "," +  
                            " it_group = " + "'" +  ld[14] + "'" + "," +  
                            " it_drawing = " + "'" +  ld[15] + "'" + "," +  
                            " it_rev = " + "'" +  ld[16] + "'" + "," +  
                            " it_custrev = " + "'" +  ld[17] + "'" + "," +  
                            " it_comments = " + "'" +  ld[18] + "'" + "," +  
                            " it_uom = " + "'" +  ld[19] + "'" + "," +  
                            " it_net_wt = " + "'" +  ld[20].replace(defaultDecimalSeparator, '.') + "'" + "," +
                            " it_ship_wt = " + "'" +  ld[21].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_leadtime = " + "'" +  ld[22].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_safestock = " + "'" +  ld[23].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_minordqty = " + "'" +  ld[24].replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            " it_mrp = " + "'" +  ld[25] + "'" + "," +  
                            " it_sched = " + "'" +  ld[26] + "'" + "," +  
                            " it_plan = " + "'" +  ld[27] + "'" + "," + 
                            " it_wf = " + "'" +  ld[28] + "'" + "," +
                            " it_status = " + "'ACTIVE'" +
                            " where it_item = " + "'" + ld[0] + "'"
                           );
                    }
                    OVData.addItemCostRec(ld[0], ld[2], "standard", 
                            bsParseDouble(ld[10]), bsParseDouble(ld[11]), bsParseDouble(ld[12]), 
                            (bsParseDouble(ld[10]) + bsParseDouble(ld[11]) + bsParseDouble(ld[12])));
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
          } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             }
             
             
    public static boolean addItemMasterMinimum(String item, String site, String desc, String type, String cost, String date) {
                 boolean myreturn = false;
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
                String[] ld = null;
                
                    
                    res = st.executeQuery("select it_item from item_mstr where " +
                                    " it_item = " + "'" + item + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into item_mstr " 
                      + "(it_item, it_desc, it_site, it_code, it_prodline, it_loc, it_lotsize, it_createdate, "
                            + "it_sell_price, it_pur_price, it_mtl_cost, it_ovh_cost, it_out_cost, it_type, it_group, "
                            + "it_drawing, it_rev, it_custrev, it_comments, "
                            + "it_uom, it_net_wt, it_ship_wt, "
                            + "it_leadtime, it_safestock, it_minordqty, it_mrp, it_sched, it_plan, it_wf, it_status ) "  
                   + " values ( " + 
                    "'" +  item + "'" + "," + 
                    "'" +  desc + "'" + "," +
                    "'" +  site + "'" + "," +  
                            "'" +  type + "'" + "," +  
                            "'" +  "9999" + "'" + "," +  
                            "'" +  "" + "'" + "," +  
                            "'" +  "1" + "'" + "," +  
                            "'" +  date + "'" + "," +          
                            "'" +  bsFormatDouble5(bsParseDouble(cost)).replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  bsFormatDouble5(bsParseDouble(cost)).replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  bsFormatDouble5(bsParseDouble(cost)).replace(defaultDecimalSeparator, '.') + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "ASSET" + "'" + "," +  
                            "'" +  "" + "'" + "," +  
                            "'" +  "" + "'" + "," +  
                            "'" +  "" + "'" + "," +  
                            "'" +  "" + "'" + "," +  
                            "'" +  "" + "'" + "," +  
                            "'" +  "EA" + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "0" + "'" + "," +  
                            "'" +  "0" + "'" + "," + 
                            "'" +  "" + "'" + ",'ACTIVE'" + ");"
                           );
                    
                    OVData.addItemCostRec(item, site, "standard", 
                            bsParseDouble(cost), bsParseDouble("0"), bsParseDouble("0"), 
                            (bsParseDouble(cost) + bsParseDouble("0") + bsParseDouble("0")));
                    }
                 
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             }
             
    public static boolean addGenericCode(ArrayList<String> list, String delim) {
                 boolean myreturn = false;
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
                String[] ld = null;
                             
                               
              
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                   res =  st.executeQuery("select code_code from code_mstr where " +
                                    " code_code = " + "'" + ld[0] + "'" +
                                    " and code_key = " + "'" + ld[1] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into code_mstr " 
                      + "(code_code, code_key, code_value ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" +  ");"
                           );     
                   }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
             
    
    public static boolean addCarrier(ArrayList<String> list, String delim) {
                 boolean myreturn = false;
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
                String[] ld = null;
                             
                               
                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                   res =  st.executeQuery("select car_code from car_mstr where " +
                                           " car_code = " + "'" + ld[0] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into car_mstr " 
                      + "(car_code, car_desc, car_scac, car_phone, car_email, car_contact, car_type, car_acct  ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                    "'" +  ld[3] + "'" + "," + 
                    "'" +  ld[4] + "'" + "," + 
                    "'" +  ld[5] + "'" + "," + 
                    "'" +  ld[6] + "'" + "," +         
                    "'" +  ld[7] + "'"
                             +  ");"
                           );     
                   }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
   
    public static boolean addBOMMstrRecord(ArrayList<String> list, String delim) {
        boolean myreturn = false;
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
                String[] ld = null;

             
                for (String rec : list) {
                    ld = rec.split(delim, -1);

                    // do bom_mstr first
                    res = st.executeQuery("select bom_id from bom_mstr where "
                            + " bom_id = " + "'" + ld[0] + "'"
                            + " AND bom_item = " + "'" + ld[2] + "'"     
                            + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }

                    if (j == 0) {
                        st.executeUpdate(" insert into bom_mstr "
                                + "(bom_id, bom_desc, bom_item, bom_enabled, bom_primary ) "
                                + " values ( "
                                + "'" + ld[0] + "'" + ","
                                + "'" + ld[1] + "'" + ","
                                + "'" + ld[2] + "'" + ","
                                + "'" + ld[3] + "'" + ","
                                + "'" + ld[4] + "'"       
                                + ");"
                        );
                    }
                    
                    // Now do pbm_mstr
                    res = st.executeQuery("select ps_parent from pbm_mstr where "
                            + " ps_parent = " + "'" + ld[5] + "'"
                            + " AND ps_child = " + "'" + ld[6] + "'"
                            + " AND ps_op = " + "'" + ld[10] + "'"
                            + " AND ps_bom = " + "'" + ld[15] + "'"        
                            + ";");
                    j = 0;
                    while (res.next()) {
                        j++;
                    }

                    if (j == 0) {
                        st.executeUpdate(" insert into pbm_mstr "
                                + "(ps_parent, ps_child, ps_type, ps_qty_per, ps_desc, ps_op, ps_sequence, ps_userid, ps_misc1, ps_ref, ps_bom ) "
                                + " values ( "
                                + "'" + ld[5] + "'" + ","
                                + "'" + ld[6] + "'" + ","
                                + "'" + ld[7] + "'" + ","
                                + "'" + ld[8] + "'" + ","
                                + "'" + ld[9] + "'" + ","
                                + "'" + ld[10] + "'" + ","
                                + "'" + ld[11] + "'" + ","
                                + "'" + ld[12] + "'" + ","
                                + "'" + ld[13] + "'" + ","
                                + "'" + ld[14] + "'" + ","
                                + "'" + ld[15] + "'"        
                                + ");"
                        );
                    }
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myreturn;
    }


    public static boolean addCustXref(ArrayList<String> list, String delim) {
                 boolean myreturn = false;
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
                String[] ld = null;
                             
                               
                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                   res =  st.executeQuery("select cup_item from cup_mstr where " +
                                    " cup_cust = " + "'" + ld[0] + "'" +
                                    " and cup_item = " + "'" + ld[1] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into cup_mstr " 
                      + "(cup_cust, cup_item, cup_citem, cup_citem2, cup_upc, cup_sku, cup_misc, cup_userid ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                            "'" +  ld[3] + "'" + "," +  
                            "'" +  ld[4] + "'" + "," +  
                            "'" +  ld[5] + "'" + "," +  
                            "'" +  ld[6] + "'" + "," +  
                            "'" +  bsmf.MainFrame.userid + "'" +  ");"
                           );     
                   } else {
                     st.executeUpdate(" update cup_mstr " 
                      + "set " +
                        " cup_citem = " + "'" + ld[2] + "'" + "," +        
                        " cup_citem2 = " + "'" + ld[3] + "'" + "," +
                        " cup_upc = " + "'" + ld[4] + "'" + "," +
                        " cup_sku = " + "'" + ld[5] + "'" + "," +
                        " cup_misc = " + "'" + ld[6] + "'" + "," +
                        " cup_userid = " + "'" + bsmf.MainFrame.userid + "'" +
                        " where cup_cust = " + "'" + ld[0] + "'" +
                        " and cup_item = " + "'" + ld[1] + "'"  + ";");
                    }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
             
    public static boolean addVendXref(ArrayList<String> list, String delim) {
                   boolean myreturn = false;
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
                String[] ld = null;
                             
                               
                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                   res =  st.executeQuery("select vdp_item from vdp_mstr where " +
                                    " vdp_vend = " + "'" + ld[0] + "'" +
                                    " and vdp_vitem = " + "'" + ld[2] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into vdp_mstr " 
                      + "(vdp_vend, vdp_item, vdp_vitem, vdp_upc, vdp_sku, vdp_misc, vdp_userid ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                            "'" +  ld[3] + "'" + "," +  
                            "'" +  ld[4] + "'" + "," +  
                            "'" +  ld[5] + "'" + "," +  
                            "'" +  ld[6] + "'" + "," +  
                            "'" +  bsmf.MainFrame.userid + "'" +  ");"
                           );     
                   }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
           
    public static boolean addGLAcctBalances(ArrayList<String> list, String delim) {
            boolean myreturn = false;
                  
            
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
              
            String[] calarray = fglData.getGLCalForDate(dfdate.format(now));
                    
                  try {
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement(); 
            try {
                
                String[] ld = null;
                               
             // order of delimited elements in file:   acct, cc, site, amount
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                      st.executeUpdate(" insert into acb_mstr " 
                      + "(acb_acct, acb_cc, acb_year, acb_per, acb_site, acb_amt ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  calarray[0] + "'" + "," +  
                    "'" +  calarray[1] + "'" + "," +  
                    "'" +  ld[2] + "'" + "," +  
                    "'" +  bsFormatDouble(bsParseDouble(ld[3])).replace(defaultDecimalSeparator, '.') + "'" + 
                    " );"
                    );     
                }    // end loop
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
       } 
               
    public static boolean addInvAdjustments(ArrayList<String> list, String delim) {
                   boolean myreturn = false;
                   boolean isError = false;
                   
                   String op = "";
                   String type = "";
                   DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
          
            try {
                
                int i = 0;
                double qty = 0;
                String[] ld = null;
                
                // base currency use only...for this type of transaction
                String curr = OVData.getDefaultCurrency();
                String basecurr = curr;
                               
             // 0=issue/receipt,1=part,2=site,3=location,4=acct,5=cc,6=qty,7=date,8=serial,9=ref,10=remarks,11=warehouse,12=expire
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                    if (ld[0].toString().toLowerCase().equals("issue")) {
                        type = "ISS-MISC";
                        qty = (-1 * bsParseDouble(ld[6]));
                    } else {
                        type = "RCT-MISC";
                        qty = bsParseDouble(ld[6]);
                    }
                    
                // get cost of part ...previously validated as non-zero when massload program verified
                double cost = invData.getItemCost(ld[1], "standard", ld[2]);
                 
               
                 // lets get the productline of the part being adjusted
                 String prodline = OVData.getProdLineFromItem(ld[1]);
        
                // get inventory account
                 String invacct = OVData.getProdLineInvAcct(prodline);
        
           // now lets do the tran_hist write
           isError = OVData.TRHistIssDiscrete(dfdate.parse(ld[7]), ld[1], qty, op, type, 0.00, cost, 
                ld[2], ld[3], ld[11], ld[12], 
                "", "", "", 0, "", "", ld[8], ld[10], ld[9], 
                ld[4], ld[5], "", "", "MassLoad", bsmf.MainFrame.userid);
        
        if (! isError) {
            isError = OVData.UpdateInventoryDiscrete(ld[1], ld[2], ld[3], ld[11], ld[12], "", qty); 
        } else {
            bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        }
        
       
        if (! isError) {
            if (ld[0].toString().toLowerCase().equals("receipt")) {
                fglData.glEntry(invacct, prodline, ld[4], ld[5],  
                        ld[7], (cost * bsParseDouble(ld[6])), (cost * bsParseDouble(ld[6])), curr, basecurr, ld[9] , ld[2], type, ld[10] + "-" + ld[1]);
            } else {
                fglData.glEntry(ld[4], ld[5], invacct, prodline, 
                        ld[7], (cost * bsParseDouble(ld[6])), (cost * bsParseDouble(ld[6])), curr, basecurr, ld[9] , ld[2], type, ld[10] + "-" + ld[1]);
            }
        } else {
          bsmf.MainFrame.show("Error during UpdateInventoryDiscrete of MassLoad");  
        }
        if (isError)
            bsmf.MainFrame.show("Error during glentry of MassLoad Inv Adjustment");
        
                }    // end loop
          
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
             
    public static boolean addCustPriceList(ArrayList<String> list, String delim) {
                  boolean myreturn = false;
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
                String[] ld = null;
                             
                               
                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                   res =  st.executeQuery("select cpr_item from cpr_mstr where " +
                                    " cpr_cust = " + "'" + ld[0] + "'" +
                                    " and cpr_uom = " + "'" + ld[8] + "'" +
                                    " and cpr_curr = " + "'" + ld[9] + "'" +        
                                    " and cpr_item = " + "'" + ld[1] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into cpr_mstr " 
                      + "(cpr_cust, cpr_item, cpr_desc, cpr_type, cpr_price, cpr_volqty, cpr_volprice, cpr_disc, cpr_uom, cpr_curr, cpr_userid, cpr_mod_date ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                            "'" +  ld[3] + "'" + "," +  
                            "'" +  ld[4] + "'" + "," +  
                            "'" +  ld[5] + "'" + "," +  
                            "'" +  ld[6] + "'" + "," +  
                            "'" +  ld[7] + "'" + "," +
                            "'" +  ld[8] + "'" + "," +     
                            "'" +  ld[9] + "'" + "," +        
                            "'" +  bsmf.MainFrame.userid + "'" + "," +
                            "'" +  BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" +
                            " );"
                           );     
                   } else {
                     st.executeUpdate(" update cpr_mstr " 
                      + "set " +
                        " cpr_price = " + "'" + ld[4] + "'" + "," +        
                        " cpr_volqty = " + "'" + ld[5] + "'" + "," +
                        " cpr_volprice = " + "'" + ld[6] + "'" + "," +
                        " cpr_disc = " + "'" + ld[7] + "'" + "," +        
                        " cpr_userid = " + "'" + bsmf.MainFrame.userid + "'" + "," +  
                        " cpr_mod_date = " + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" +        
                        " where cpr_cust = " + "'" + ld[0] + "'" +
                        " and cpr_uom = " + "'" + ld[8] + "'" +
                        " and cpr_curr = " + "'" + ld[9] + "'" +        
                        " and cpr_item = " + "'" + ld[1] + "'" + ";");
                    }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
              
    public static boolean addVendPriceList(ArrayList<String> list, String delim) {
                   boolean myreturn = false;
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
                String[] ld = null;
                             
                               
                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                   res =  st.executeQuery("select vpr_item from vpr_mstr where " +
                                    " vpr_vend = " + "'" + ld[0] + "'" +
                                    " and vpr_item = " + "'" + ld[1] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into vpr_mstr " 
                      + "(vpr_vend, vpr_item, vpr_desc, vpr_type, vpr_price, vpr_volqty, vpr_volprice, vpr_disc, vpr_userid, vpr_mod_date ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                            "'" +  ld[3] + "'" + "," +  
                            "'" +  ld[4] + "'" + "," +  
                            "'" +  ld[5] + "'" + "," +  
                            "'" +  ld[6] + "'" + "," +  
                            "'" +  ld[7] + "'" + "," +
                            "'" +  bsmf.MainFrame.userid + "'" + "," +
                            "'" +  BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" +
                            " );"
                           );     
                   }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
              
    public static boolean addVendMstr(ArrayList<String> list, String delim) {
        boolean myreturn = false;
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
                String[] ld = null;

                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(delim, -1);

                    res = st.executeQuery("select vd_addr from vd_mstr where "
                            + " vd_addr = " + "'" + ld[0] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }

                    if (j == 0) {
                        st.executeUpdate(" insert into vd_mstr "
                                + "(vd_addr, vd_site, vd_name, vd_line1, vd_line2, vd_line3, vd_city, vd_state, vd_zip, vd_country, vd_dateadd, vd_datemod, "
                                + " vd_usermod, vd_group, vd_market, vd_buyer, vd_terms, vd_shipvia, vd_price_code, vd_disc_code, vd_tax_code, "
                                + " vd_ap_acct, vd_ap_cc, vd_remarks, vd_freight_type, vd_bank, vd_curr, vd_misc, vd_phone, vd_email, vd_is850export ) "
                                + " values ( "
                                + "'" + ld[0] + "'" + ","
                                + "'" + ld[1] + "'" + ","
                                + "'" + ld[2] + "'" + ","
                                + "'" + ld[3] + "'" + ","
                                + "'" + ld[4] + "'" + ","
                                + "'" + ld[5] + "'" + ","
                                + "'" + ld[6] + "'" + ","
                                + "'" + ld[7] + "'" + ","
                                + "'" + ld[8] + "'" + ","
                                + "'" + ld[9] + "'" + ","
                                + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" + ","
                                + "'" + BlueSeerUtils.setDateFormat(new java.util.Date()) + "'" + ","
                                + "'" + bsmf.MainFrame.userid + "'" + ","
                                + "'" + ld[10] + "'" + ","
                                + "'" + ld[11] + "'" + ","
                                + "'" + ld[12] + "'" + ","
                                + "'" + ld[13] + "'" + ","
                                + "'" + ld[14] + "'" + ","
                                + "'" + ld[15] + "'" + ","
                                + "'" + ld[16] + "'" + ","
                                + "'" + ld[17] + "'" + ","
                                + "'" + ld[18] + "'" + ","
                                + "'" + ld[19] + "'" + ","
                                + "'" + ld[20] + "'" + ","
                                + "'" + ld[21] + "'" + ","
                                + "'" + ld[22] + "'" + ","
                                + "'" + ld[23] + "'" + ","
                                + "'" + ld[24] + "'" + ","
                                + "'" + ld[25] + "'" + ","
                                + "'" + ld[26] + "'" + ","
                                + "'" + ld[27] + "'"        
                                + " );"
                        );
                    }
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
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
        return myreturn;
    }

    public static boolean addRoutingMaster(ArrayList<String> list, String delim) {
                 boolean myreturn = false;
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
                String[] ld = null;
                             
                               
                // now loop through colon delimited list and insert into routing master table
                // skip if already in table.....keys are wf_id and wf_op
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                   res =  st.executeQuery("select wf_id from wf_mstr where " +
                                    " wf_id = " + "'" + ld[0] + "'" +
                                    " and wf_op = " + "'" + ld[3] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into wf_mstr " 
                      + "(wf_id, wf_desc, wf_site, wf_op, wf_assert, wf_op_desc, wf_cell, wf_setup_hours, wf_run_hours ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                    "'" +  ld[3] + "'" + "," +  
                    "'" +  ld[4] + "'" + "," +  
                    "'" +  ld[5] + "'" + "," +  
                    "'" +  ld[6] + "'" + "," +  
                    "'" +  ld[7] + "'" + "," +
                    "'" +  ld[8] + "'" +  ");"
                   );     
                   }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
   
    public static boolean addWorkCenterMaster(ArrayList<String> list, String delim) {
                 boolean myreturn = false;
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
                String[] ld = null;
                             
                               
                // now loop through colon delimited list and insert into workcenter master table
                // skip if already in table.....keys are wf_id and wf_op
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    
                   res =  st.executeQuery("select wc_cell from wc_mstr where " +
                                    " wc_cell = " + "'" + ld[0] + "'" + ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into wc_mstr " 
                      + "(wc_cell, wc_desc, wc_site, wc_cc, wc_run_rate, wc_run_crew, wc_setup_rate, wc_setup, wc_bdn_rate, wc_remarks) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                    "'" +  ld[3] + "'" + "," +  
                    "'" +  ld[4] + "'" + "," +  
                    "'" +  ld[5] + "'" + "," +  
                    "'" +  ld[6] + "'" + "," +  
                    "'" +  ld[7] + "'" + "," +
                    "'" +  ld[8] + "'" + "," +        
                    "'" +  ld[9] + "'" +  ");"
                   );     
                   }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
   
    public static boolean addShopifyOrderCSV(ArrayList<String> list, String delim) {
         boolean myreturn = false;
         Map<String, ArrayList<String[]>> orders = new  HashMap<String, ArrayList<String[]>>();
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
                String[] ld = null;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();             
                // loop through list and separate unique orders with item(s) and place in a HashMap
                // with order id as key ...column 2 ...order id will be used for Purchase Order Number
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    ArrayList<String[]> x = new ArrayList<String[]>();
                    if (orders.containsKey(ld[1])) {
                       x = orders.get(ld[1]);
                       x.add(ld);
                       orders.put(ld[1], x);
                    } else {
                       x.add(ld);
                       orders.put(ld[1], x);
                    }
                }
               
                for (Map.Entry<String, ArrayList<String[]>> z : orders.entrySet()) {
                 ArrayList<String[]> k = z.getValue();
                 int m = 0;
                 String BillToCode;
                 String nbr = "";
                 String[] custinfo = new String[]{"","","","","","","", ""};
                 for (String[] g : k) {
                     // create sales order header from first element of ArrayList
                     if (m == 0) {
                        // create billto/shipto on the fly and assign return to custcode
                        BillToCode = String.valueOf(OVData.getNextNbr("customer"));
                        // set String array of address info for creation of billto/shipto
                        String[] addr = new String[]{BillToCode, g[25], g[27], g[28], g[26], g[30], g[32], g[31], g[33], g[2], g[34], g[35], g[37], g[38], g[36], g[40], g[42], g[41], g[43]};  
                        custinfo = OVData.addCustMstrWShipToMinimal(addr);

                        nbr = String.valueOf(OVData.getNextNbr("order")); 
                    
                       // bsmf.MainFrame.show(g[48] + "/" + g[49] + "/" + g[50]);
                        st.executeUpdate("insert into so_mstr "
                    + "(so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, "
                    + "so_create_date, so_userid, so_status,"
                    + "so_rmks, so_terms, so_ar_acct, so_ar_cc, so_shipvia, so_type, so_site, so_onhold ) "
                    + " values ( " + "'" + nbr + "'" + ","
                    + "'" + BillToCode + "'" + ","
                    + "'" + BillToCode + "'" + ","
                    + "'" + g[1] + "'" + ","
                    + "'" + dfdate.format(now) + "'" + ","
                    + "'" + dfdate.format(now) + "'" + ","
                    + "'" + dfdate.format(now) + "'" + ","
                    + "'" + "shopify" + "'" + ","
                    + "'" + getGlobalProgTag("open") + "'" + ","
                    + "'" + g[48] + "'" + ","
                    + "'" + custinfo[4] + "'" + ","
                    + "'" + custinfo[0] + "'" + ","
                    + "'" + custinfo[1] + "'" + ","
                    + "'" + custinfo[5] + "'" + ","
                    + "'DISCRETE'" + ","
                    + "'" + custinfo[7] + "'" + ","
                    + "'" + custinfo[6] + "'"
                    + ")"
                    + ";");
                        
                     // now discount info ...only applicable at header level
                    if (! g[5].isBlank() && ! g[5].isBlank() && ! g[5].equals("0") && ! g[5].equals("0.00")) {
                    st.executeUpdate("insert into sos_det (sos_nbr, sos_desc, sos_type, " 
                        + "sos_amttype, sos_amt ) "
                        + " values ( " + "'" + nbr + "'" + ","
                    + "'" + "discount summary" + "'" + ","
                    + "'" + "discount" + "'" + ","
                    + "'" + "amount" + "'" + ","
                    + "'" + g[5].replace(defaultDecimalSeparator, '.') + "'"
                            + ")"
                    + ";");
                    }    
                    
                    // now tax info ...can only do 3 of the 5 shopify provides
                    if (! g[51].isBlank() && ! g[52].isBlank() && ! g[52].equals("0")) {
                    st.executeUpdate("insert into sos_det (sos_nbr, sos_desc, sos_type, " 
                        + "sos_amttype, sos_amt ) "
                        + " values ( " + "'" + nbr + "'" + ","
                    + "'" + g[51] + "'" + ","
                    + "'" + "tax" + "'" + ","
                    + "'" + "amount" + "'" + ","
                    + "'" + g[52].replace(defaultDecimalSeparator, '.') + "'"
                            + ")"
                    + ";");
                    }
                    
                    if (! g[53].isBlank() && ! g[54].isBlank() && ! g[54].equals("0")) {
                    st.executeUpdate("insert into sos_det (sos_nbr, sos_desc, sos_type, " 
                        + "sos_amttype, sos_amt ) "
                        + " values ( " + "'" + nbr + "'" + ","
                    + "'" + g[53] + "'" + ","
                    + "'" + "tax" + "'" + ","
                    + "'" + "amount" + "'" + ","
                    + "'" + g[54].replace(defaultDecimalSeparator, '.') + "'"
                            + ")"
                    + ";");
                    }
                    
                    if (! g[55].isBlank() && ! g[56].isBlank() && ! g[56].equals("0")) {
                    st.executeUpdate("insert into sos_det (sos_nbr, sos_desc, sos_type, " 
                        + "sos_amttype, sos_amt ) "
                        + " values ( " + "'" + nbr + "'" + ","
                    + "'" + g[55] + "'" + ","
                    + "'" + "tax" + "'" + ","
                    + "'" + "amount" + "'" + ","
                    + "'" + g[56].replace(defaultDecimalSeparator, '.') + "'"
                            + ")"
                    + ";");
                    }
                        
                    } // if m == 0 ...first line creates Sales Order Header
                     
                    // now do detail for m == 0 and all subsequent values of m
                    
                    st.executeUpdate("insert into sod_det "
                        + "(sod_nbr, sod_item, sod_site, sod_po, sod_ord_qty, sod_netprice, sod_listprice, sod_disc, sod_due_date, "
                        + "sod_shipped_qty, sod_custitem, sod_status, sod_line) "
                        + " values ( " + "'" + nbr + "'" + ","
                        + "'" + g[16] + "'" + ","
                        + "'" + custinfo[7] + "'" + ","
                        + "'" + g[1] + "'" + ","
                        + "'" + g[15].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + g[19].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + g[17].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + g[18].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + now + "'" + ","
                        + '0' + "," + "'" + g[20] + "'" +  "," 
                        + "'" + getGlobalProgTag("open") + "'" + ","
                        + "'" + String.valueOf(m + 1) + "'"
                        + ")"
                        + ";");
                    
                    m++;     
                 }
    			 
    		 
    	        }
               
               
               
                
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
   
    
    public static boolean createTestDataSO() {
            boolean myreturn = true;
            ArrayList<String[]> items = invData.getItemsAndPriceByType("FG");
            ArrayList<String> custs = cusData.getcustmstrlist();
            String curr = OVData.getDefaultCurrency();
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
                LocalDate lt = LocalDate.now();
                int year = lt.getYear(); 
                LocalDate date = lt.ofYearDay(year, 1);
               // LocalDate date = LocalDate.parse("2022-01-01");
                LocalDate[] duedate = new LocalDate[53];
                String now = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                int qty = 0;
                int count = 0;
                int itempos = 0;
                int custpos = 0;
                for (int k = 0; k < 53; k++) {
                    duedate[k] = date.plusDays(k * 7);
                }
                String sduedate = duedate[0].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); ;
                int j = 0;
                int indexnbr = OVData.getNextNbr("order", 366); // one more than the max ...when <= ...else the max
                for (int i = 0; i <= 365; i++) {
                    if ((i % 7) == 0) {
                      sduedate = duedate[i / 7].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); 
                    } 
                    custpos = (int) (Math.random() * (5 - 0)) + 0;
                    j = 0;
                     
                    res =  st.executeQuery("select so_nbr from so_mstr where " +
                                    " so_nbr = " + "'" + String.valueOf(indexnbr) + "'" + ";");
                    
                    while (res.next()) {
                        j++;
                    }
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into so_mstr " 
                    + "(so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, " 
                    + "so_create_date, so_userid, so_status, so_curr, "
                    + "so_rmks, so_terms, so_ar_acct, so_ar_cc, so_shipvia, so_type, so_site, so_onhold ) "
                    + " values ( " + 
                    "'" +  String.valueOf(indexnbr) + "'" + "," + 
                    "'" +  custs.get(custpos) + "'" + "," +   
                    "'" +  custs.get(custpos) + "'" + "," + 
                    "'" +  "testpo" + String.valueOf(indexnbr) + "'" + "," +  
                    "'" +  sduedate + "'" + "," +  
                    "'" +  sduedate + "'" + "," +  
                    "'" +  now + "'" + "," +  
                    "'" +  "admin" + "'" + "," +
                    "'" +  "open" + "'" + "," +  
                    "'" +  curr + "'" + "," +        
                    "'" +  "" + "'" + "," + 
                    "'" +  "N30" + "'" + "," + 
                    "'" +  "20000000" + "'" + "," + 
                    "'" +  "9999" + "'" + "," + 
                    "'" +  "" + "'" + "," + 
                    "'" +  "DISCRETE" + "'" + "," + 
                    "'" +  "1000" + "'" + "," +       
                    "'" +  "0" + "'" +  ");"
                   );  
                count = (int) (Math.random() * (5 - 1)) + 1;
                
                for (int z = 0; z <= count; z++ ) {
                qty = (int) (Math.random() * (20 - 1)) + 1;   
                itempos = (int) (Math.random() * (5 - 0)) + 0;
                st.executeUpdate("insert into sod_det "
                            + "(sod_nbr, sod_item, sod_site, sod_po, sod_ord_qty, sod_netprice, "
                            + " sod_listprice, sod_disc, sod_due_date, sod_ord_date, sod_uom, sod_desc, "
                            + "sod_shipped_qty, sod_custitem, sod_status, sod_line) "
                    + " values ( " + 
                    "'" +  String.valueOf(indexnbr) + "'" + "," + 
                    "'" +  items.get(itempos)[0] + "'" + "," +
                    "'" +  "1000" + "'" + "," +  
                    "'" +  "testpo" + String.valueOf(indexnbr) + "'" + "," +  
                    "'" +  qty + "'" + "," +  
                    "'" +  items.get(itempos)[1] + "'" + "," +  
                    "'" +  items.get(itempos)[1] + "'" + "," +  
                    "'" +  "0" + "'" + "," +
                    "'" +  sduedate + "'" + "," +  
                    "'" +  sduedate + "'" + "," +        
                    "'" +  items.get(itempos)[2] + "'" + "," +
                    "'" +  items.get(itempos)[3] + "'" + "," +        
                    "'" +  "0" + "'" + "," + 
                    "'" +  "" + "'" + "," + 
                    "'" +  "open" + "'" + "," +    
                    "'" +  String.valueOf(z + 1) + "'" +  ");"
                   );    
                } // for each sales order det random z
                } // if j == 0
                    indexnbr++;  // next order number
                } // for each sales order 
             
               
                
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = false;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
            myreturn = false;
        }  
                  return myreturn;
             } 
   
    public static boolean createTestDataPO() {
            boolean myreturn = true;
            ArrayList<String[]> items = invData.getItemsAndPriceByType("FG");
            String curr = OVData.getDefaultCurrency();
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
                LocalDate lt = LocalDate.now();
                int year = lt.getYear(); 
                LocalDate date = lt.ofYearDay(year, 1);
               // LocalDate date = LocalDate.parse("2022-01-01");
                LocalDate[] duedate = new LocalDate[53];
                String now = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                int qty = 0;
                int count = 0;
                int itempos = 0;
                int custpos = 0;
                for (int k = 0; k < 53; k++) {
                    duedate[k] = date.plusDays(k * 7);
                }
                String sduedate = duedate[0].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); ;
               
                
                // Now lets do Purchase Orders
                items = invData.getItemsAndPriceByType("RAW");
                int j = 0;
                int indexnbr = OVData.getNextNbr("po", 366); // one more than the max ...when <= ...else the max
                for (int i = 0; i <= 365; i++) {
                    if ((i % 7) == 0) {
                      sduedate = duedate[i / 7].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); 
                    } 
                    j = 0;
                    
                    res =  st.executeQuery("select po_nbr from po_mstr where " +
                                    " po_nbr = " + "'" + String.valueOf(indexnbr) + "'" + ";");
                    
                    while (res.next()) {
                        j++;
                    }
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into po_mstr " 
                    + "(po_nbr, po_vend, po_site, po_type, po_ship, " 
                        + " po_curr, po_buyer, po_due_date, "
                        + " po_ord_date, po_userid, po_status,"
                        + " po_terms, po_ap_acct, po_ap_cc, po_rmks ) "
                    + " values ( " + 
                    "'" +  String.valueOf(indexnbr) + "'" + "," + 
                    "'" +  "cash" + "'" + "," +
                    "'" +  "1000" + "'" + "," +  
                    "'" +  "site" + "'" + "," + 
                    "'" +  "1000" + "'" + "," +        
                    "'" +  curr + "'" + "," +   
                    "'" +  "admin" + "'" + "," +  
                    "'" +  sduedate + "'" + "," +  
                    "'" +  now + "'" + "," +  
                    "'" +  "admin" + "'" + "," +
                    "'" +  "open" + "'" + "," + 
                    "'" +  "N30" + "'" + "," + 
                    "'" +  "30000000" + "'" + "," + 
                    "'" +  "9999" + "'" + "," + 
                    "'" +  "someremarks" + "'" +  ");"
                   );  
                    st.executeUpdate(" insert into po_addr (poa_code, poa_shipto, " +
                            "poa_name, poa_line1, poa_city, poa_state, poa_zip, poa_country ) " +
                            " values ( " +
                            "'" +  String.valueOf(indexnbr) + "'" + "," +
                            "'" +  "1000" + "'" + "," +
                            "'" +  "MFG Company" + "'" + "," +
                            "'" +  "Dusty Way Canyon Road" + "'" + "," +
                            "'" +  "Dodge City" + "'" + "," +
                            "'" +  "KS" + "'" + "," +
                            "'" +  "65455" + "'" + "," +
                            "'" +  "US" + "'" +  ");"
                            );
                    
                count = (int) (Math.random() * (5 - 1)) + 1;
                for (int z = 0; z <= count; z++ ) {
                qty = (int) (Math.random() * (200 - 10)) + 10;   
                itempos = (int) (Math.random() * (10 - 0)) + 0;
                st.executeUpdate("insert into pod_mstr "
                            + "(pod_nbr, pod_line, pod_item, pod_venditem, "
                            + " pod_ord_qty, pod_uom, pod_listprice, pod_disc, "
                            + " pod_netprice, pod_ord_date, pod_due_date, "
                            + "pod_rcvd_qty, pod_status, pod_site, pod_desc) "
                    + " values ( " + 
                    "'" +  String.valueOf(indexnbr) + "'" + "," + 
                    "'" +  String.valueOf(z + 1) + "'" + "," + 
                    "'" +  items.get(itempos)[0] + "'" + "," +
                    "'" +  "" + "'" + "," + 
                    "'" +  qty + "'" + "," +  
                    "'" +  items.get(itempos)[2] + "'" + "," + 
                    "'" +  items.get(itempos)[1] + "'" + "," + 
                    "'" +  "0" + "'" + "," +        
                    "'" +  items.get(itempos)[1] + "'" + "," +  
                    "'" +  now + "'" + "," +
                    "'" +  sduedate + "'" + "," +        
                    "'" +  "0" + "'" + "," + 
                    "'" +  "open" + "'" + "," +   
                    "'" +  "1000" + "'" + "," +        
                    "'" +  items.get(itempos)[3] + "'" +  ");"
                   );    
                } // for each purchase order det random z
                } // j == 0
                indexnbr++;    
                } // for each purchase order 
              
             
                
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = false;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
            myreturn = false;
        }  
                  return myreturn;
             } 
   
    public static boolean createTestDataTC() {
        boolean myreturn = true;
        LocalDate today = LocalDate.now();
        LocalDate[] days = new LocalDate[7];
        for (int k = 0; k < 7; k++) {
            days[k] = today.plusDays(0 - k);
        }
        ArrayList<String> emps = hrmData.getempmstrlist(); 
        
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
                   st.executeUpdate("delete from time_clock;");
                   
                   for (String e : emps) {
                       for (int m = 0; m < days.length; m++) {
                   st.executeUpdate(" insert into time_clock " 
                      + "(emp_nbr, indate, intime, intime_adj, outdate, outtime, outtime_adj, tothrs, dept, code_id, code_orig, login ) "
                   + " values ( " + 
                    "'" +  e + "'" + "," + 
                    "'" +  days[m].format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))  + "'" + "," +
                    "'" +  "08:00:00" + "'" + "," +  
                    "'" +  "08:00:00" + "'" + "," +  
                    "'" +  days[m].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'" + "," +  
                    "'" +  "16:00:00" + "'" + "," +  
                    "'" +  "16:00:00" + "'" + "," +  
                    "'" +  "8" + "'" + "," +
                    "'" +  "9999" + "'" + "," +
                    "'" +  "00" + "'" + "," +
                    "'" +  "00" + "'" + "," +
                    "'" +  bsmf.MainFrame.userid + "'" +        
                    ");"
                   );  
                   }
                   }
            } catch (Exception s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = false;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
   
    
    public static boolean addSalesOrderDetail(ArrayList<String> list, String delim) { 
                 boolean myreturn = false;
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
                String[] ld = null;
               
                for (String rec : list) {
                    ld = rec.split(delim, -1);
                    st.executeUpdate("insert into sod_det "
                            + "(sod_nbr, sod_item, sod_site, sod_po, sod_ord_qty, sod_netprice, "
                            + " sod_listprice, sod_disc, sod_due_date, "
                            + "sod_shipped_qty, sod_custitem, sod_status, sod_line) "
                    + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                    "'" +  ld[3] + "'" + "," +  
                    "'" +  ld[4] + "'" + "," +  
                    "'" +  ld[5] + "'" + "," +  
                    "'" +  ld[6] + "'" + "," +  
                    "'" +  ld[7] + "'" + "," +
                    "'" +  ld[8] + "'" + "," +      
                    "'" +  ld[9] + "'" + "," + 
                    "'" +  ld[10] + "'" + "," + 
                    "'" +  ld[11] + "'" + "," +    
                    "'" +  ld[12] + "'" +  ");"
                   );     
                 
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
   
        
              
               
    public static void wip_to_fg(String item, String site, Double cost, String date, String ref, String type, String desc) {
        try{


                String acct_dr = "";
                String cc_dr = "";
                String acct_cr = "";
                String cc_cr = "";

                //inventory transactions....base currency only
                String curr = OVData.getDefaultCurrency();
                String basecurr = curr;


        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            

            /* lets get the 'wip' and 'inventory' acct and cc from product line info for the item */
            res = st.executeQuery("select pl_wip, pl_line, pl_inventory " +
                   " from item_mstr " +
                   " inner join pl_mstr on pl_line = it_prodline " +
                   " where it_item = " + "'" + item + "'" + ";");
             while (res.next()) {
                 acct_dr = res.getString("pl_inventory");
                 cc_dr = res.getString("pl_line");
                 acct_cr = res.getString("pl_wip");
                 cc_cr = res.getString("pl_line");
             }

                // process GL transactions
               fglData.glEntry(acct_cr, cc_cr, acct_dr, cc_dr, date, cost, cost, curr, basecurr, ref, site, type, desc); // post bdn entry

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    }        

    public static void wip_iss_mtl_gl(String item, String op, String csite, Double qty, String date, String cref, String ctype, String cdesc, String serial, String userid, String program, String bom) {

    try{

         // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                ArrayList acct_cr = new ArrayList();
                ArrayList ref =  new ArrayList();
                ArrayList desc =   new ArrayList();
                ArrayList type =   new ArrayList();
                ArrayList cc_cr =   new ArrayList();
                ArrayList acct_dr =   new ArrayList();
                ArrayList cc_dr =   new ArrayList();
                ArrayList site =   new ArrayList();
                ArrayList cost =  new ArrayList();   


                ArrayList child = new ArrayList();
                ArrayList loc = new ArrayList();
                ArrayList wh = new ArrayList();
                ArrayList qtyper = new ArrayList();

                String thistype = ctype;
                String thisdesc = cdesc;  
                String thissite = csite;
                String thisref = cref;

                String par_acct_dr = "";
                String par_cc_dr = "";

                //inventory transactions....base currency only
                String curr = OVData.getDefaultCurrency();
                String basecurr = curr;


        String pmcode = "";
        String tranhisttype = "";
        String expire = ""; // should be blank for component issues

        boolean isReportable = false;
        
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            
            res = st.executeQuery("select wf_assert " +
                   " from wf_mstr " +
                   " inner join item_mstr on it_wf = wf_id " +
                   " where it_item = " + "'" + item + "'" +
                   " and wf_op = " + "'" + op + "'" + 
                   " and wf_site = " + "'" + csite + "'" +        
                   ";");
             while (res.next()) {
               if (res.getBoolean("wf_assert")) {
                   isReportable = true;
               }
             }
            

            /* lets get the 'wip' acct and cc from product line info for the item */
            res = st.executeQuery("select pl_wip, pl_line, it_code " +
                   " from item_mstr " +
                   " inner join pl_mstr on pl_line = it_prodline " +
                   " where it_item = " + "'" + item + "'" + ";");
             while (res.next()) {
               par_acct_dr = res.getString("pl_wip");
               par_cc_dr = res.getString("pl_line"); 
               pmcode = res.getString("it_code");
             }
            /*  NOTE:  we will be debiting the backflushed part WIP account and
            crediting the component Inventory account  */


           if (pmcode.equalsIgnoreCase("P")) {
                res = st.executeQuery("select  itc_total, pl_scrap, pl_line, pl_inventory " +
                   " from item_mstr  " + 
                   " inner join pl_mstr on pl_line = it_prodline " +
                   " inner join item_cost on itc_item = it_item and itc_set = 'standard' where it_item = " + "'" + item + "'" + ";"
                    );
                while (res.next()) {
                acct_cr.add(res.getString("pl_inventory"));
                acct_dr.add(res.getString("pl_scrap"));
                cc_cr.add(res.getString("pl_line"));
                cc_dr.add(res.getString("pl_line"));
                cost.add((res.getDouble("itc_total") * qty));
                site.add(thissite);
                ref.add(thisref);
                type.add(thistype);
                desc.add(thisdesc);
                }
                res.close();
                // now process into GL
                  for (int j = 0; j < acct_cr.size(); j++) {
                  fglData.glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), date, bsParseDouble(cost.get(j).toString()), bsParseDouble(cost.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
                }
           } else {
           res = st.executeQuery("select ps_child, ps_qty_per, it_loc, it_wh, itc_total, pl_inventory, pl_line " +
                   " from pbm_mstr " +
                   " inner join bom_mstr on bom_id = ps_bom  " +
                   " inner join item_mstr on it_item = ps_child " + 
                   " inner join pl_mstr on pl_line = it_prodline " +
                   " inner join item_cost on itc_item = ps_child and itc_set = 'standard' where ps_parent = " + "'" + item + "'" +
                   " AND ps_op = " + "'" + op + "'" +
                   " AND ps_bom = " + "'" + bom + "'" );
           while (res.next()) {
               acct_cr.add(res.getString("pl_inventory"));
                acct_dr.add(par_acct_dr);
                cc_cr.add(res.getString("pl_line"));
                cc_dr.add(par_cc_dr);
                cost.add((res.getDouble("ps_qty_per") * res.getDouble("itc_total") * qty));
                site.add(thissite);
                ref.add(thisref);
                type.add("ISS-SUB");
                desc.add(thisdesc);
                child.add(res.getString("ps_child"));
                loc.add(res.getString("it_loc"));
                wh.add(res.getString("it_wh"));
                qtyper.add(res.getDouble("ps_qty_per"));
           }
           res.close();

           for (int j = 0; j < acct_cr.size(); j++) {

               // now process to GL
               fglData.glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), date, bsParseDouble(cost.get(j).toString()), bsParseDouble(cost.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), "ISS-WIP", desc.get(j).toString());  

               // process tran_hist
               if (ctype.equals("ISS-SCRAP")) {
                   tranhisttype = "ISS-WIP";
               } else {
                   tranhisttype = ctype;
               }
               OVData.TRHistIssDiscrete(BlueSeerUtils.mysqlDateFormat.parse(date), child.get(j).toString(), (-1 * qty), op, tranhisttype, 0, 0, csite, 
                       loc.get(j).toString(), wh.get(j).toString(), expire, "", "", item + ":" + op, 0, "", "", "", cref, "", "", "", "", serial, program, userid);

               // update inventory
               OVData.UpdateInventoryDiscrete(child.get(j).toString(), csite, loc.get(j).toString(), wh.get(j).toString(), "", "", bsParseDouble(qtyper.get(j).toString()) * qty * -1);    

           }

             /* if current op is reportable, go get all the unreport operations back to the last reported op 
              exclusive of this reported op  */
           if (isReportable) { 
            ArrayList<String> myops = new ArrayList<String>();
           res = st.executeQuery("select wf_op " +
                   " from wf_mstr inner join item_mstr on wf_id = it_wf " + 
                   " where it_item = " + "'" + item + "'" +
                   " AND wf_op < " + "'" + op + "'" + " AND " +
                   " wf_assert = 0 order by wf_op desc ");
            while (res.next()) {
               myops.add(res.getString("wf_op"));
            }
            res.close();

            for ( String myvalue : myops) {
                wip_iss_mtl_gl_unreported(item, myvalue, csite, qty, date, cref, ctype, cdesc, serial, userid, program, bom);
            }
           } /* if Reportable Op */
           } // if pmcode "M"
       }
        catch (SQLException s){ 
            MainFrame.bslog(s);
            bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }


    }

    public static void wip_iss_mtl_gl_unreported(String item, String op, String csite, Double qty, String date, String cref, String ctype, String cdesc, String serial, String userid, String program, String bom) {


    try{



                ArrayList acct_cr = new ArrayList();
                ArrayList ref =  new ArrayList();
                ArrayList desc =   new ArrayList();
                ArrayList type =   new ArrayList();
                ArrayList cc_cr =   new ArrayList();
                ArrayList acct_dr =   new ArrayList();
                ArrayList cc_dr =   new ArrayList();
                ArrayList site =   new ArrayList();
                ArrayList cost =  new ArrayList();   
                ArrayList child = new ArrayList();
                ArrayList loc = new ArrayList();
                ArrayList wh = new ArrayList();
                ArrayList qtyper = new ArrayList();

                String thistype = ctype;
                String thisdesc = cdesc;  
                String thissite = csite;
                String thisref = cref;

                String par_acct_dr = "";
                String par_cc_dr = "";

                //inventory transactions....base currency only
                String curr = OVData.getDefaultCurrency();
                String basecurr = curr;

        String expire = "";  //should be "" for components

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            /* lets get the 'wip' acct and cc from product line info for the item */
            res = st.executeQuery("select pl_wip, pl_line " +
                   " from item_mstr " +
                   " inner join pl_mstr on pl_line = it_prodline " +
                   " where it_item = " + "'" + item + "'" + ";");
             while (res.next()) {
                 par_acct_dr = res.getString("pl_wip");
                 par_cc_dr = res.getString("pl_line");
             }
            /*  NOTE:  we will be debiting the backflushed part WIP account and
            crediting the component Inventory account  */

           /* now lets get the components of this item and write cost to GL */
           res = st.executeQuery("select ps_child, it_loc, it_wh, ps_qty_per, itc_total, pl_inventory, pl_line " +
                   " from pbm_mstr " +
                   " inner join bom_mstr on bom_id = ps_bom  " +
                   " inner join item_mstr on it_item = ps_child " + 
                   " inner join pl_mstr on pl_line = it_prodline " +
                   " inner join item_cost on itc_item = ps_child and itc_set = 'standard' where ps_parent = " + "'" + item + "'" +
                   " AND ps_op = " + "'" + op + "'" +
                   " AND ps_bom = " + "'" + bom + "'" );
           while (res.next()) {
               acct_cr.add(res.getString("pl_inventory"));
               cc_cr.add(res.getString("pl_line"));
                acct_dr.add(par_acct_dr);
                cc_dr.add(par_cc_dr);
               cost.add((res.getDouble("ps_qty_per") * res.getDouble("itc_total") * qty));
               site.add(thissite);
                ref.add(thisref);
                type.add(thistype);
                desc.add(thisdesc);
                child.add(res.getString("ps_child"));
                loc.add(res.getString("it_loc"));
                wh.add(res.getString("it_wh"));
                qtyper.add(res.getDouble("ps_qty_per"));
            }


            for (int j = 0; j < acct_cr.size(); j++) {

                // process GL transactions
                fglData.glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), date, bsParseDouble(cost.get(j).toString()), bsParseDouble(cost.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), "ISS-WIP", desc.get(j).toString());  

               // process tran_hist
               OVData.TRHistIssDiscrete(BlueSeerUtils.mysqlDateFormat.parse(date), child.get(j).toString(), (-1 * qty), op, "ISS-WIP", 0, 0, csite, 
                       loc.get(j).toString(), wh.get(j).toString(), expire, "", "", item + ":" + op, 0, "", "", "", cref, "", "", "", "", serial, program, userid);

               // update inventory
               OVData.UpdateInventoryDiscrete(child.get(j).toString(), csite, loc.get(j).toString(), wh.get(j).toString(), "", "", bsParseDouble(qtyper.get(j).toString()) * qty * -1);    
            }



       }
        catch (SQLException s){
            MainFrame.bslog(s);
            bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }


    }

    public static void wip_iss_op_cost_gl(String item, String cop, String site, Double qty, String date, String ref, String type, String desc) {

    ArrayList myarray = new ArrayList();
    double cost = 0.00;
    double actcost = 0.00;
    try{
        String acct_cr = "";
        String cc_cr = "";
        String acct_dr = "";
        String cc_dr = "";

        String lbracct = "";
        String bdnacct = "";
        String lbrvaracct = "";
        String bdnvaracct = "";
        String cc = "";

        //inventory transactions....base currency only
                String curr = OVData.getDefaultCurrency();
                String basecurr = curr;


        boolean isReportable = false;

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            /* lets check if this is a reportable operation */
             /* lets get the 'wip' acct and cc from product line info for the item */

            res = st.executeQuery("select wf_assert " +
                   " from wf_mstr " +
                   " inner join item_mstr on it_wf = wf_id " +
                   " where it_item = " + "'" + item + "'" +
                   " and wf_op = " + "'" + cop + "'" + 
                   " and wf_site = " + "'" + site + "'" +        
                   ";");
             while (res.next()) {
               if (res.getBoolean("wf_assert")) {
                   isReportable = true;
               }
             }

            /* lets get the 'wip' acct and cc from product line info for the item */
            res = st.executeQuery("select pl_wip, pl_line " +
                   " from item_mstr " +
                   " inner join pl_mstr on pl_line = it_prodline " +
                   " where it_item = " + "'" + item + "'" + ";");
             while (res.next()) {
               acct_dr = res.getString("pl_wip");
               cc_dr = res.getString("pl_line"); 
             }
            /*  NOTE:  we will be debiting the backflushed part WIP account and
            crediting the lbr and bdn accounts of the dept where the operation at hand occured */

           res = st.executeQuery("select wc_cc, dept_lbr_acct, dept_bdn_acct, dept_lbr_usg_acct, dept_bdn_usg_acct " +
                   " from item_mstr inner join wf_mstr on it_wf = wf_id " + 
                   " inner join wc_mstr on wc_cell = wf_cell " +
                   " inner join dept_mstr on dept_id = wc_cc " +
                   " where it_item = " + "'" + item + "'" +
                   " AND wf_op = " + "'" + cop + "'" );
           while (res.next()) {
               lbracct = res.getString("dept_lbr_acct");
               bdnacct = res.getString("dept_bdn_acct");
               lbrvaracct = res.getString("dept_lbr_usg_acct");
               bdnvaracct = res.getString("dept_bdn_usg_acct");
               cc = res.getString("wc_cc");
            }
           res.close();
           /* Lets do Labor */
          actcost = (getLaborWithOutSetup(item, cop) * qty);  // used to use actual cost
          cost = invData.getItemLbrCost(item, cop, site, "standard") * qty;  // let's use standard cost to hit the GL

           acct_cr = lbracct;
           cc_cr = cc;
           desc = item + " - lbr op " + cop;
         //  bsmf.MainFrame.show(desc + "/" + String.valueOf(cost) + "/" + String.valueOf(actcost));
           fglData.glEntry(acct_cr, cc_cr, acct_dr, cc_dr, date, cost, cost, curr, basecurr, ref, site, type, desc);  // post lbr entry
           fglData.glEntry(lbrvaracct, cc_cr, acct_dr, cc_dr, date, (cost - actcost), (cost - actcost), curr, basecurr, ref, site, type, desc);  // post lbr variance entry
           /* Lets do Burden */
           actcost = (getBurdenWithOutSetup(item, cop) * qty);
           cost = invData.getItemBdnCost(item, cop, site, "standard") * qty;  // let's use standard cost to hit the GL
           acct_cr = bdnacct;
           cc_cr = cc;
           desc = item + " - bdn op " + cop;
           fglData.glEntry(acct_cr, cc_cr, acct_dr, cc_dr, date, cost, cost, curr, basecurr, ref, site, type, desc); // post bdn entry
           fglData.glEntry(bdnvaracct, cc_cr, acct_dr, cc_dr, date, (cost - actcost), (cost - actcost), curr, basecurr, ref, site, type, desc); // post bdn variance entry





           /* if current op is reportable, go get all the unreport operations back to the last reported op 
              exclusive of this reported op  */
           if (isReportable) { 
           ArrayList op = new ArrayList();
           res = st.executeQuery("select wf_op " +
                   " from wf_mstr inner join item_mstr on wf_id = it_wf " + 
                   " where it_item = " + "'" + item + "'" +
                   " AND wf_op < " + "'" + cop + "'" + " AND " +
                   " wf_assert = 0 order by wf_op desc ");
            while (res.next()) {
                op.add(res.getString("wf_op"));
            }
             for (int j = 0; j < op.size(); j++) {
                 wip_iss_op_cost_gl_unreported(item, op.get(j).toString(), site, qty, date, ref, type, desc);
             }
        } // if reportable

       }
        catch (SQLException s){
            MainFrame.bslog(s);
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }


    }

    public static void wip_iss_op_cost_gl_unreported(String item, String op, String csite, Double qty, String date, String cref, String ctype, String cdesc) {
    try{
                ArrayList acct_cr_lbr = new ArrayList();
                ArrayList acct_cr_lbrvar = new ArrayList();
                ArrayList acct_cr_bdn = new ArrayList();
                ArrayList acct_cr_bdnvar = new ArrayList();
                ArrayList ref =  new ArrayList();
                ArrayList desc_lbr =   new ArrayList();
                ArrayList desc_bdn =   new ArrayList();
                ArrayList type =   new ArrayList();
                ArrayList cc_cr =   new ArrayList();
                ArrayList acct_dr =   new ArrayList();
                ArrayList cc_dr =   new ArrayList();
                ArrayList site =   new ArrayList();
                ArrayList cost_lbr =  new ArrayList(); 
                ArrayList cost_bdn =  new ArrayList();
                ArrayList actcost_lbr =  new ArrayList(); 
                ArrayList actcost_bdn =  new ArrayList();
                ArrayList child = new ArrayList();
                ArrayList loc = new ArrayList();
                ArrayList qtyper = new ArrayList();

                String thistype = ctype;
                String thisdesc = cdesc;  
                String thissite = csite;
                String thisref = cref;

                String par_acct_dr = "";
                String par_cc_dr = "";


                 //inventory transactions....base currency only
                String curr = OVData.getDefaultCurrency();
                String basecurr = curr;


        boolean isReportable = false;

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            /* lets check if this is a reportable operation */
             /* lets get the 'wip' acct and cc from product line info for the item */

            res = st.executeQuery("select wf_assert " +
                   " from wf_mstr " +
                   " inner join item_mstr on it_wf = wf_id " +
                   " where it_item = " + "'" + item + "'" + ";");
             while (res.next()) {
               if (res.getBoolean("wf_assert")) {
                   isReportable = true;
               }
             }

            /* lets get the 'wip' acct and cc from product line info for the item */
            res = st.executeQuery("select pl_wip, pl_line " +
                   " from item_mstr " +
                   " inner join pl_mstr on pl_line = it_prodline " +
                   " where it_item = " + "'" + item + "'" + ";");
             while (res.next()) {
               par_acct_dr = res.getString("pl_wip");
               par_cc_dr = res.getString("pl_line"); 
             }
            /*  NOTE:  we will be debiting the backflushed part WIP account and
            crediting the lbr and bdn accounts of the dept where the operation at hand occured */
           /* Lets do Labor */
           res = st.executeQuery("select wc_cc, dept_lbr_acct, dept_bdn_acct, dept_lbr_usg_acct, dept_bdn_usg_acct " +
                   " from item_mstr inner join wf_mstr on it_wf = wf_id " + 
                   " inner join wc_mstr on wc_cell = wf_cell " +
                   " inner join dept_mstr on dept_id = wc_cc " +
                   " where it_item = " + "'" + item + "'" +
                   " AND wf_op = " + "'" + op + "'" );
           while (res.next()) {

                acct_cr_lbr.add(res.getString("dept_lbr_acct"));
                acct_cr_bdn.add(res.getString("dept_bdn_acct"));
                acct_cr_lbrvar.add(res.getString("dept_lbr_usg_acct"));
                acct_cr_bdnvar.add(res.getString("dept_bdn_usg_acct"));
               cc_cr.add(res.getString("wc_cc"));
                acct_dr.add(par_acct_dr);
                cc_dr.add(par_cc_dr);
              actcost_lbr.add(getLaborWithOutSetup(item, op) * qty);
              cost_lbr.add(invData.getItemLbrCost(item, op, thissite, "standard") * qty);
              actcost_bdn.add(getBurdenWithOutSetup(item, op) * qty);
              cost_bdn.add(invData.getItemBdnCost(item, op, thissite, "standard") * qty); 
             site.add(thissite);
                ref.add(thisref);
                type.add(thistype);
                desc_lbr.add(item + " - lbr op " + op);
                desc_bdn.add(item + " - bdn op " + op);
               // child.add(res.getString("ps_child"));
              //  loc.add(res.getString("it_loc"));
            //   qtyper.add(res.getDouble("ps_qty_per"));

            }

            for (int j = 0; j < acct_cr_lbr.size(); j++) {

                /* Lets do Labor */
                fglData.glEntry(acct_cr_lbr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), date, bsParseDouble(cost_lbr.get(j).toString()), bsParseDouble(cost_lbr.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc_lbr.get(j).toString());  
                fglData.glEntry(acct_cr_lbrvar.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), date, (bsParseDouble(cost_lbr.get(j).toString()) - bsParseDouble(actcost_lbr.get(j).toString())), (bsParseDouble(cost_lbr.get(j).toString()) - bsParseDouble(actcost_lbr.get(j).toString())), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc_lbr.get(j).toString());  

                /* Lets do Burden */
                fglData.glEntry(acct_cr_bdn.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), date, bsParseDouble(cost_bdn.get(j).toString()), bsParseDouble(cost_bdn.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc_bdn.get(j).toString());  
                fglData.glEntry(acct_cr_bdnvar.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), date, (bsParseDouble(cost_bdn.get(j).toString()) - bsParseDouble(actcost_bdn.get(j).toString())), (bsParseDouble(cost_bdn.get(j).toString()) - bsParseDouble(actcost_bdn.get(j).toString())), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc_bdn.get(j).toString());  


            }




       }
        catch (SQLException s){
            MainFrame.bslog(s);
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    }
    
    public static ArrayList getCodeMstr(String type) {
       ArrayList myarray = new ArrayList();
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_key from code_mstr where code_code = " + "'" + type + "'" + " order by code_key ;");
               while (res.next()) {
                    myarray.add(res.getString("code_key"));
                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
     
    public static ArrayList<String> getCodeMstrKeyList(String code) {
       ArrayList myarray = new ArrayList();
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_key from code_mstr where code_code = " + "'" + code + "'" + " order by code_key ;");
               while (res.next()) {
                    myarray.add(res.getString("code_key"));
                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
    
    public static ArrayList getCodeMstrValueList(String code) {
       ArrayList myarray = new ArrayList();
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_value from code_mstr where code_code = " + "'" + code + "'" + " order by code_key ;");
               while (res.next()) {
                    myarray.add(res.getString("code_value"));
                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
    
    public static String getCodeMstrKeyFromCodeValue(String code, String value) {
       String x = "";
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_key from code_mstr where code_code = " + "'" + code + "'" + 
                          " and code_value = " + "'" + value + "'" + ";");
               while (res.next()) {
                    x = res.getString("code_key");
                }
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
    
    
    public static ArrayList getPriceGroupList() {
       ArrayList myarray = new ArrayList();
        try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_key from code_mstr where code_code = " + "'PRICEGROUP'" + " order by code_key ;");
               while (res.next()) {
                    myarray.add(res.getString("code_key"));

                }

           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static String getPriceGroupCodeFromCust(String custcode) {
       String myreturn = "";
        try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select cm_price_code from cm_mstr where cm_code = " + "'" + custcode + "'" + ";");
               while (res.next()) {
                    myreturn = res.getString("cm_price_code");
                }

           }
            catch (SQLException s){
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myreturn;

    }

    public static String getPriceGroupCodeFromVend(String vend) {
       String myreturn = "";
        try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select vd_price_code from vd_mstr where vd_addr = " + "'" + vend + "'" + ";");
               while (res.next()) {
                    myreturn = res.getString("vd_price_code");
                }

           }
            catch (SQLException s){
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myreturn;

    }

    public static ArrayList<String[]> getCodeAndValueMstr(String code) {
       ArrayList<String[]> myarray = new ArrayList<String[]>();
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_key, code_value from code_mstr where code_code = " + "'" + code + "'" + " order by code_key ;");
               while (res.next()) {
                    myarray.add(new String[]{res.getString("code_key"),res.getString("code_value")});
                }
               
          } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
      
    public static ArrayList getJasperByGroup(String group) {
       ArrayList<String[]> myarray = new ArrayList();
       
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select * from jasp_mstr " +
                        " where jasp_group = " + "'" + group + "'" + 
                        " order by cast(jasp_sequence as decimal) ;");
               while (res.next()) {
                    myarray.add(new String[]{res.getString("jasp_desc"), res.getString("jasp_func"), res.getString("jasp_format")});
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
      
    public static String getJasperFuncByTitle(String group, String title) {
       String x = "";
        try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            PreparedStatement ps = null;
            ResultSet res = null;
            try{
                String sql = "select * from jasp_mstr where jasp_group = ? and jasp_desc = ? ;";
                ps = con.prepareStatement(sql);
                ps.setString(1, group);
                ps.setString(2, title);
                res = ps.executeQuery();
                while (res.next()) {
                    x = res.getString("jasp_func");
                }
           }
            catch (SQLException s){
               MainFrame.bslog(s);
            } finally {
                if (res != null) res.close();
                if (ps != null) ps.close();
                if (con != null) con.close(); 
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
    
    public static ArrayList getCodeAndDescMstrOrderByDesc(String type) {
   ArrayList<String[]> myarray = new ArrayList();

    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
            res = st.executeQuery("select code_key, code_value from code_mstr " +
                    " where code_code = " + "'" + type + "'" + 
                    " order by cast(code_key as decimal) ;");
           while (res.next()) {
                myarray.add(new String[]{res.getString("code_key"), res.getString("code_value")});

            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    
    public static Boolean isValidCustPriceRecordExists(String entity, String item, String uom, String curr) {
       
       // type is either 'c' for customer price or 'v' for vendor price      
             
       boolean isValid = false;
       String pricecode = "";
      
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                    res = st.executeQuery("select cm_price_code from cm_mstr where cm_code = " + "'" + entity + "'" + ";");
                     while (res.next()) {
                       pricecode = res.getString("cm_price_code");
                    }     
                      // if there is no pricecode....it defaults to billto
                     if (! pricecode.isEmpty()) {
                         entity = pricecode;
                     }

                    res = st.executeQuery("select cpr_price from cpr_mstr where cpr_cust = " + "'" + entity + "'" + 
                                          " AND cpr_item = " + "'" + item + "'" +
                                          " AND cpr_uom = " + "'" + uom + "'" +
                                          " AND cpr_curr = " + "'" + curr + "'" +
                                          " AND cpr_type = 'LIST' "+ ";");
                   while (res.next()) {
                       isValid = true;
                   }
                
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isValid;
        
    }
    
    public static Boolean isValidVendPriceRecordExists(String entity, String item, String uom, String curr) {
       
       // type is either 'c' for customer price or 'v' for vendor price      
             
       boolean isValid = false;
       String pricecode = "";
      
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                    res = st.executeQuery("select vd_price_code from vd_mstr where vd_addr = " + "'" + entity + "'" + ";");
                 while (res.next()) {
                   pricecode = res.getString("vd_price_code");
                }       
                      // if there is no pricecode....it defaults to billto
                     if (! pricecode.isEmpty()) {
                         entity = pricecode;
                     }

                    res = st.executeQuery("select vpr_price from vpr_mstr where vpr_vend = " + "'" + entity + "'" + 
                                      " AND vpr_item = " + "'" + item + "'" +
                                      " AND vpr_uom = " + "'" + uom + "'" +
                                      " AND vpr_curr = " + "'" + curr + "'" +        
                                      " AND vpr_type = 'LIST' "+ ";");
                   while (res.next()) {
                       isValid = true;
                   }
                
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isValid;
        
    }
    
    public static Double getNetPriceFromListAndDisc(Double listprice, Double discount) {
               Double netprice = 0.00;
               
                if (discount != 0)
              netprice = listprice - (listprice * (discount / 100));
                else
              netprice = listprice;
                
               return netprice;
           }
     
    public static String getCodeDescByCode(String key) {
       String mystring = "";
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_value from code_mstr where code_key = " + "'" + key + "'" + " order by code_key ;");
               while (res.next()) {
                   mystring = res.getString("code_value");
                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
    public static String getCodeKeyByCode(String code) {
       String mystring = "";
        try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_key from code_mstr where code_code = " + "'" + code + "'" + " order by code_key ;");
               while (res.next()) {
                   mystring = res.getString("code_key");
                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
    public static String getCodeValueByCodeKey(String code, String key) {
       String mystring = "";
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_value from code_mstr where " +
                        " code_code = " + "'" + code + "'" + 
                        " AND code_key = " + "'" + key + "'" + " ;");
               while (res.next()) {
                   mystring = res.getString("code_value");
                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
     
    public static String getSystemImageDirectory() {
 String myreturn = "";
 try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        res = st.executeQuery("select ov_image_directory from ov_ctrl;" );
       while (res.next()) {
        myreturn = res.getString("ov_image_directory");                    
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myreturn;

}

    public static String getSystemTempDirectory() {
 String myreturn = "";
 try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        res = st.executeQuery("select ov_temp_directory from ov_ctrl;" );
       while (res.next()) {
        myreturn = res.getString("ov_temp_directory");                    
        }
   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myreturn;

}
    
    public static String getSystemLabelDirectory() {
 String myreturn = "";
 try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        res = st.executeQuery("select ov_label_directory from ov_ctrl;" );
       while (res.next()) {
        myreturn = res.getString("ov_label_directory");                    
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myreturn;

}
    
    public static String getSystemJasperDirectory() {
 String myreturn = "";
 try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        res = st.executeQuery("select ov_jasper_directory from ov_ctrl;" );
       while (res.next()) {
        myreturn = res.getString("ov_jasper_directory");                    
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myreturn;

}
    
    public static String getSystemEDIDirectory() {
 String myreturn = "";
 try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        res = st.executeQuery("select ov_edi_directory from ov_ctrl;" );
       while (res.next()) {
        myreturn = res.getString("ov_edi_directory");                    
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myreturn;

}
    
    public static String getSystemFileServerType() {
    String myreturn = "";
    try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        res = st.executeQuery("select ov_fileservertype from ov_ctrl;" );
       while (res.next()) {
        myreturn = res.getString("ov_fileservertype");                    
        }

    }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}   
  
    public static String getVersion() {
    String x = "";
    try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        res = st.executeQuery("select ov_version from ov_ctrl;" );
       while (res.next()) {
        x = res.getString("ov_version");                    
        }

    }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return x;

}   
  
    
    public static String getDefaultSite() {
           String myitem = null;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select ov_site from ov_mstr;" );
               while (res.next()) {
                myitem = res.getString("ov_site");                    
                }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
              
    public static String getDefaultWH() {
           String myitem = null;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select ov_wh from ov_mstr;" );
               while (res.next()) {
                myitem = res.getString("ov_wh");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
        
    public static String getDefaultCC() {
           String myitem = null;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                res = st.executeQuery("select ov_cc from ov_mstr;" );
               while (res.next()) {
                myitem = res.getString("ov_cc");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }    
            
    public static String getDefaultSiteName() {
           String myitem = null;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select site_desc from site_mstr inner join ov_mstr on ov_site = site_site ;" );
               while (res.next()) {
                myitem = res.getString("site_desc");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
              
    public static String getDefaultCurrency() {
           String myitem = null;
         try{
            
          Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select ov_currency from ov_mstr;" );
               while (res.next()) {
                myitem = res.getString("ov_currency");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
       
    public static String getDefaultLabelPrinter() {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select ov_labelprinter from ov_mstr;" );
               while (res.next()) {
                myitem = res.getString("ov_labelprinter");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
         
    public static String getDefaultARBank() {
           String myitem = null;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select arc_bank from ar_ctrl;" );
               while (res.next()) {
                myitem = res.getString("arc_bank");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
               
    public static String getDefaultARAcct() {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select arc_default_acct from ar_ctrl;" );
               while (res.next()) {
                myitem = res.getString("arc_default_acct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
         
    public static String getDefaultPayWithHoldAcct() {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select payc_withhold_acct from pay_ctrl;" );
               while (res.next()) {
                myitem = res.getString("payc_withhold_acct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
    
    public static String getDefaultPayLaborAcct() {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select payc_labor_acct from pay_ctrl;" );
               while (res.next()) {
                myitem = res.getString("payc_labor_acct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
    
    public static String getDefaultPayLaborCC() {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select payc_labor_cc from pay_ctrl;" );
               while (res.next()) {
                myitem = res.getString("payc_labor_cc");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
    
    public static String getDefaultPaySalariedAcct() {
           String myitem = null;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select payc_salaried_acct from pay_ctrl;" );
               while (res.next()) {
                myitem = res.getString("payc_salaried_acct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
      
    public static String getDefaultPaySalariedCC() {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select payc_salaried_cc from pay_ctrl;" );
               while (res.next()) {
                myitem = res.getString("payc_salaried_cc");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }  
         
    public static String getDefaultPayTaxAcct() {
           String myitem = null;
         try{
            
          Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select payc_payrolltax_acct from pay_ctrl;" );
               while (res.next()) {
                myitem = res.getString("payc_payrolltax_acct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
      
    public static String getDefaultSalesAcct() {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select arc_sales_acct from ar_ctrl;" );
               while (res.next()) {
                myitem = res.getString("arc_sales_acct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
         
    public static String getDefaultAssetAcctAR() {
           String myitem = null;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select arc_asset_acct from ar_ctrl;" );
               while (res.next()) {
                myitem = res.getString("arc_asset_acct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
         
    public static String getDefaultAssetAcctAP() {
           String myitem = null;
         try{
            
          Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                res = st.executeQuery("select apc_assetacct from ap_ctrl;" );
               while (res.next()) {
                myitem = res.getString("apc_assetacct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
          
    public static String getDefaultAssetCC() {
           String myitem = null;
         try{
            
          Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select arc_asset_cc from ar_ctrl;" );
               while (res.next()) {
                myitem = res.getString("arc_asset_cc");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
         
    public static String getDefaultARCC() {
       String myitem = "";
     try{

       Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select arc_default_cc from ar_ctrl;" );
           while (res.next()) {
            myitem = res.getString("arc_default_cc");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}

    public static String getDefaultSalesCC() {
       String myitem = "";
     try{

       Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select arc_sales_cc from ar_ctrl;" );
           while (res.next()) {
            myitem = res.getString("arc_sales_cc");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}

    public static String getDefaultAPBank() {
       String myitem = "";
     try{

      Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select apc_bank from ap_ctrl;" );
           while (res.next()) {
            myitem = res.getString("apc_bank");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}

    public static String getDefaultBankAcct(String bank) {
       String myitem = "";
     try{

       Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select bk_acct from bk_mstr where bk_id = " + "'" + bank + "'" + ";" );
           while (res.next()) {
            myitem = res.getString("bk_acct");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}     

    public static String getDefaultAPAcct() {
       String myitem = "";
     try{

      Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select apc_apacct from ap_ctrl;" );
           while (res.next()) {
            myitem = res.getString("apc_apacct");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}

    public static String getDefaultSiteForUserid(String myuser) {
       String myitem = null;
     try{

      Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
            res = st.executeQuery("select siteu_site from siteu_mstr where siteu_userid = " + "'" + myuser.toString() + "';" );
           while (res.next()) {
            myitem = res.getString("siteu_site");                    
            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}

    public static String getDefaultItemImageFile(String item) {
          
           String myimage = "";
         try{
            
          Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select iti_file from item_image where iti_default = '1' and iti_item = " + "'" + item + "'" + "  ;" );
               while (res.next()) {
                myimage = res.getString("iti_file");                    
                }
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myimage;
        
    }
     
    public static String getDefaultShipperJasper(String site) {
       String myitem = "";
     try{

       Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select site_sh_jasper from site_mstr where site_site = " + "'" + site + "';" );
           while (res.next()) {
            myitem = res.getString("site_sh_jasper");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}    

    public static String getDefaultPOPrintJasper(String site) {
       String myitem = "";
     try{

       Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select site_po_jasper from site_mstr where site_site = " + "'" + site + "';" );
           while (res.next()) {
            myitem = res.getString("site_po_jasper");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem; 

}   

    public static String getDefaultInvoiceJasper(String site) {
       String myitem = "";
     try{

       Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select site_iv_jasper from site_mstr where site_site = " + "'" + site + "';" );
           while (res.next()) {
            myitem = res.getString("site_iv_jasper");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}      

    public static String getDefaultOrderJasper(String site) {
       String myitem = "";
     try{

      Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select site_or_jasper from site_mstr where site_site = " + "'" + site + "';" );
           while (res.next()) {
            myitem = res.getString("site_or_jasper");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}      
    
    public static String getDefaultPOSJasper(String site) {
       String myitem = "";
     try{

       Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select site_pos_jasper from site_mstr where site_site = " + "'" + site + "';" );
           while (res.next()) {
            myitem = res.getString("site_pos_jasper");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}      

    public static String getDefaultRetainedEarningsAcct() {
              // function returns a 2 items from the gl_cal record where a period matches
              // first element = startdate
              // second element = enddate
              
      String account = "";
        try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select gl_earnings from gl_ctrl;"); 
               while (res.next()) {
                       account = res.getString("gl_earnings");
               }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return account;
        
    }
      
    public static String getDefaultForeignCurrRealAcct() {
              // function returns a 2 items from the gl_cal record where a period matches
              // first element = startdate
              // second element = enddate
              
      String account = "";
        try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select gl_foreignreal from gl_ctrl;"); 
               while (res.next()) {
                       account = res.getString("gl_foreignreal");
               }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return account;
        
    }
    
    public static String getDefaultTaxAcctByType(String type) {
        String acct = "";
        String other = "";

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

                // function will try to assign appropriate tax account.  If fed,state,local are undefined, it will attempt to assign
                // to 'other'
                // if 'other' is undefined....it will return a blank.
                res = st.executeQuery("select * from ar_ctrl;");
                while (res.next()) {
                    other = res.getString("arc_othertax_acct");

                    if (type.toUpperCase().equals("FEDERAL") && !res.getString("arc_fedtax_acct").isEmpty()) {
                        acct = res.getString("arc_fedtax_acct");
                    }
                    if (type.toUpperCase().equals("STATE") && !res.getString("arc_statetax_acct").isEmpty()) {
                        acct = res.getString("arc_statetax_acct");
                    }
                    if (type.toUpperCase().equals("LOCAL") && !res.getString("arc_localtax_acct").isEmpty()) {
                        acct = res.getString("arc_localtax_acct");
                    }
                    if (type.toUpperCase().equals("OTHER") && !res.getString("arc_othertax_acct").isEmpty()) {
                        acct = res.getString("arc_othertax_acct");
                    }
                    // need to move this post Version 6.0 to inside the TaxMaint
                    // for now...material and other are synonymous
                    if (type.toUpperCase().equals("MATERIAL") && !res.getString("arc_othertax_acct").isEmpty()) {
                        acct = res.getString("arc_othertax_acct");
                    }
                }

                // default to 'other' if no account is defined
                if (acct.isEmpty()) {
                    acct = other;
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return acct;

    }

    public static String getDefaultTaxCCByType(String type) {
        String cc = "";
        String other = "";

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

                // function will try to assign appropriate tax account.  If fed,state,local are undefined, it will attempt to assign
                // to 'other'
                // if 'other' is undefined....it will return a blank.
                res = st.executeQuery("select * from ar_ctrl;");
                while (res.next()) {
                    other = res.getString("arc_othertax_cc");

                    if (type.toUpperCase().equals("FEDERAL") && !res.getString("arc_fedtax_cc").isEmpty()) {
                        cc = res.getString("arc_fedtax_cc");
                    }
                    if (type.toUpperCase().equals("STATE") && !res.getString("arc_statetax_cc").isEmpty()) {
                        cc = res.getString("arc_statetax_cc");
                    }
                    if (type.toUpperCase().equals("LOCAL") && !res.getString("arc_localtax_cc").isEmpty()) {
                        cc = res.getString("arc_localtax_cc");
                    }
                    if (type.toUpperCase().equals("OTHER") && !res.getString("arc_othertax_cc").isEmpty()) {
                        cc = res.getString("arc_othertax_cc");
                    }
                }

                // default to 'other' if no account is defined
                if (cc.isEmpty()) {
                    cc = other;
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return cc;

    }
 
    public static String getPayProfileDetAcct(String profile, String line) {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select paypd_acct from pay_profdet where paypd_id = " + "'" + line + "'" +
                        " and paypd_parentcode = " + "'" + profile + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("paypd_acct");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }  
      
    public static String getPayProfileDetCC(String profile, String line) {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select paypd_cc from pay_profdet where paypd_id = " + "'" + line + "'" +
                        " and paypd_parentcode = " + "'" + profile + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("paypd_cc");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }  
    
    public static String getPayProfileAcctPayTaxCC() {
           String myitem = null;
         try{
            
           Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select payc_payrolltax_cc from pay_ctrl;" );
               while (res.next()) {
                myitem = res.getString("payc_payrolltax_cc");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
    
    
    public static String getExchangeRate(String base, String foreign) {
   String myitem = "";
 try{

  Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

        res = st.executeQuery("select exc_rate from exc_mstr where exc_base = " + "'" + base + "'" + 
                " and exc_foreign = " + "'" + foreign + "'" + 
                ";" );
       while (res.next()) {
        myitem = res.getString("exc_rate");                    
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myitem;

}

    public static double getExchangeBaseValue(String base, String foreign, double invalue) {
   double outvalue = invalue;
 try{

   Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

        res = st.executeQuery("select exc_rate from exc_mstr where exc_base = " + "'" + base + "'" + 
                " and exc_foreign = " + "'" + foreign + "'" + 
                ";" );
       while (res.next()) {
           if (invalue > 0) {
           outvalue = invalue / res.getDouble("exc_rate");            
           } 
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return outvalue;

}

    public static String getProdLineFromItem(String item) {
       String myitem = null;
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select it_prodline from item_mstr inner join pl_mstr on pl_line = it_prodline where it_item = " + "'" + item + "';" );
           while (res.next()) {
            myitem = res.getString("it_prodline");                    
            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}  

    public static String getUOMFromItemSite(String item, String mysite) {
       String myitem = null;
     try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            res = st.executeQuery("select it_uom from item_mstr where it_item = " + "'" + item + "'" +
                                  " AND it_site = " + "'" + mysite + "'" + ";" ); 
           while (res.next()) {
            myitem = res.getString("it_uom");                    
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                    con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}  

    public static Boolean isBaseUOMOfItem(String item, String mysite, String uom) {
       boolean isBase = false;
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select it_uom from item_mstr where it_item = " + "'" + item + "'" +
                                  " AND it_site = " + "'" + mysite + "'" + ";" ); 
           while (res.next()) {
               if (res.getString("it_uom").toUpperCase().equals(uom.toUpperCase())) {
                   isBase = true;
               }                
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isBase; 

}      

    public static Double getUOMBaseQty(String item, String site, String order_uom, Double order_qty) {
       Double baseqty = order_qty;  // initialize with inbound qty
       String baseuom = "";
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
            // if order_uom is base qty...then return same...else convert qty to order_uom..assuming a legitimate conversion table entry for both uoms
            // lets first make sure there's a conversion table entry for the order_uom...if not bail...and return inbound qty

            res = st.executeQuery("select it_uom from item_mstr where it_item = " + "'" + item + "'" +
                                  " AND it_site = " + "'" + site + "'" + ";" ); 
           while (res.next()) {
            baseuom = res.getString("it_uom");                    
            }
            int z = 0;
            if (! baseuom.equals(order_uom)) {
                res = st.executeQuery("select conv_id, conv_fromamt, conv_toamt from conv_mstr where " +
                        " conv_fromcode = " + "'" + baseuom + "'" +
                        " AND conv_tocode = " + "'" + order_uom + "'" + ";" ); 
                while (res.next()) {
                    z++;
                    baseqty = order_qty * ( res.getDouble("conv_fromamt") / res.getDouble("conv_toamt"));                    
                }
                    if (z == 0) {
                        // try reverse
                        res = st.executeQuery("select conv_id, conv_fromamt, conv_toamt from conv_mstr where " +
                            " conv_fromcode = " + "'" + order_uom + "'" +
                            " AND conv_tocode = " + "'" + baseuom + "'" + ";" ); 
                        while (res.next()) {
                        baseqty = order_qty * ( res.getDouble("conv_toamt") / res.getDouble("conv_fromamt"));                    
                        }
                    }
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return baseqty;

}  
      
    public static Boolean isValidUOMConversion(String item, String site, String order_uom) {
           boolean isValid = false;
           String baseuom = "";
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                // if order_uom is base qty...then return same...else convert qty to order_uom..assuming a legitimate conversion table entry for both uoms
                // lets first make sure there's a conversion table entry for the order_uom...if not bail...and return inbound qty
               
                res = st.executeQuery("select it_uom from item_mstr where it_item = " + "'" + item + "'" +
                                      " AND it_site = " + "'" + site + "'" + ";" ); 
               while (res.next()) {
                baseuom = res.getString("it_uom");                    
                }
                int z = 0;
                if (! baseuom.equals(order_uom)) {
                    res = st.executeQuery("select conv_id, conv_fromamt, conv_toamt from conv_mstr where " +
                            " conv_fromcode = " + "'" + baseuom + "'" +
                            " AND conv_tocode = " + "'" + order_uom + "'" + ";" ); 
                    while (res.next()) {
                        z++;
                        isValid = true;
                    }
                    if (z == 0) {
                        // try reverse
                        res = st.executeQuery("select conv_id, conv_fromamt, conv_toamt from conv_mstr where " +
                            " conv_fromcode = " + "'" + order_uom + "'" +
                            " AND conv_tocode = " + "'" + baseuom + "'" + ";" ); 
                        while (res.next()) {
                        isValid = true;              
                        }
                    }
                } else {
                    isValid = true;
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isValid;
        
    }        
        
    public static String getProdLineInvAcct(String prodline) {
           String myitem = null;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select pl_inventory from pl_mstr where pl_line = " + "'" + prodline.toString() + "';" );
               while (res.next()) {
                myitem = res.getString("pl_inventory");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }  
    
    public static String[] getWorkCellElements(String wc) {
           String[] myarray = new String[9];
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select * from wc_mstr " +
                        " where wc_cell = " + "'" + wc + "'" + ";" );
               
                while (res.next()) {
                myarray = new String[]{res.getString("wc_cell"),
                    res.getString("wc_desc"),
                    res.getString("wc_site"),
                    res.getString("wc_cc"),
                    res.getString("wc_run_rate"),
                    res.getString("wc_run_crew"),
                    res.getString("wc_setup_rate"),
                    res.getString("wc_setup"),
                    res.getString("wc_bd_rate")
                    };                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray; 
        
    }
       
    public static String[] getRoutingElements(String routing) {
           String[] myarray = new String[7];
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select * from wf_mstr " +
                        " where wf_id = " + "'" + routing + "'" + ";" );
               
                while (res.next()) {
                myarray = new String[]{res.getString("wf_id"),
                    res.getString("wf_desc"),
                    res.getString("wf_site"),
                    res.getString("wf_op"),
                    res.getString("wf_cell"),
                    res.getString("wf_setup_hours"),
                    res.getString("wf_run_hours")
                    };                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray; 
        
    }
    
    public static String[] getBOMParentOpElements(String parent, String op) {
           String[] myarray = new String[10];
         try{
            
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select wf_setup_hours, wf_run_hours, " +
                        " wc_run_rate, wc_run_crew, wc_setup_rate, wc_setup, " +
                        " wc_bdn_rate, wc_cc, wf_desc, wc_desc " +
                        " from wf_mstr " +
                        " inner join wc_mstr on wf_cell = wc_cell " +
                        " inner join item_mstr on it_wf = wf_id " +
                        " where it_item = " + "'" + parent + "'" + 
                        " and wf_op = " + "'" + op + "'" +  ";" );
               
                while (res.next()) {
                myarray = new String[]{
                    res.getString("wc_run_rate"),
                    res.getString("wc_setup_rate"),
                    res.getString("wc_bdn_rate"),
                    res.getString("wc_run_crew"),
                    res.getString("wc_setup"),
                    res.getString("wf_run_hours"),
                    res.getString("wf_setup_hours"),
                    res.getString("wc_cc"),
                    res.getString("wf_desc"),
                    res.getString("wc_desc")
                    };                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray; 
        
    }
    
    public static ArrayList getWorkCellList() {
           ArrayList myarray = new ArrayList();
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                res = st.executeQuery("select wc_cell from wc_mstr;" );
               while (res.next()) {
                myarray.add(res.getString("wc_cell"));                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
        
    public static ArrayList getRoutingList() {
           ArrayList myarray = new ArrayList();
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select wf_id from wf_mstr;" );
               while (res.next()) {
                myarray.add(res.getString("wf_id"));                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
          
    public static ArrayList getSiteList() {
           ArrayList myarray = new ArrayList();
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select site_site from site_mstr;" );
               while (res.next()) {
                myarray.add(res.getString("site_site"));                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
                
    public static ArrayList getUOMList() {
           ArrayList myarray = new ArrayList();
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select uom_id from uom_mstr order by uom_id;" );
               while (res.next()) {
                myarray.add(res.getString("uom_id"));                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
                    
    public static Double getPOSSalesTaxPct() {
           Double percent = 0.00;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select posc_taxpercent from pos_ctrl;" );
               while (res.next()) {
                percent = res.getDouble("posc_taxpercent");                    
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return percent;
        
    }
           
    public static String getLocationByItem(String item) {
      String myloc = "";
     try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select it_loc from item_mstr where it_item = " + "'" + item + "'" + ";" );
           while (res.next()) {
            myloc = res.getString("it_loc");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myloc;

}

    public static String[] getTopLocationAndWHByQTY(String item, String site) {
      String[] myloc = new String[2];
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            // first get the default wh and loc for this item and site in case below optimum item and site return nothing.
             res = st.executeQuery("select it_loc, it_wh from item_mstr where it_item = " + "'" + item + "'" + 
                    " AND it_site = " + "'" + site + "'" +
                    " ;" );
           while (res.next()) {
            myloc[0] = res.getString("it_wh");  
            myloc[1] = res.getString("it_loc");
            }

            // now overwrite with optimum wh and loc with highest qoh
            res = st.executeQuery("select in_loc, in_wh from in_mstr where in_item = " + "'" + item + "'" + 
                    " AND in_site = " + "'" + site + "'" +
                    " order by in_qoh desc limit 1;" );
           while (res.next()) {
            myloc[0] = res.getString("in_wh");  
            myloc[1] = res.getString("in_loc");
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myloc;

}

    public static Double getTopQtyLocationByItem(String item, String site) {
      Double qty = 0.00;
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select in_qoh from in_mstr where in_item = " + "'" + item + "'" + 
                    " AND in_site = " + "'" + site + "'" +
                    " order by in_qoh desc limit 1;" );
           while (res.next()) {
            qty = res.getDouble("in_qoh");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return qty;
}
      
    public static String getUOMByItem(String item) {
      String myuom = "";
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select it_uom from item_mstr where it_item = " + "'" + item + "'" + ";" );
           while (res.next()) {
            myuom = res.getString("it_uom");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myuom;

}

    public static String getWarehouseByItem(String item) {
      String myloc = "";
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select it_wh from item_mstr where it_item = " + "'" + item + "'" + ";" );
           while (res.next()) {
            myloc = res.getString("it_wh");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myloc;

}

    public static ArrayList getLocationList() {
       ArrayList myarray = new ArrayList();
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select loc_loc from loc_mstr order by loc_loc;" );
           while (res.next()) {
            myarray.add(res.getString("loc_loc"));                    
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static ArrayList getLocationListByWarehouse(String wh) {
       ArrayList myarray = new ArrayList();
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select loc_loc from loc_mstr  " + 
                    "where loc_wh = " + "'" + wh + "'" +
                    " order by loc_loc ;" );
           while (res.next()) {
            myarray.add(res.getString("loc_loc"));                    
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static ArrayList getWareHouseList() {
           ArrayList myarray = new ArrayList();
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select wh_id from wh_mstr order by wh_id;" );
               while (res.next()) {
                myarray.add(res.getString("wh_id"));                    
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }   
       
    public static String[] getWareHouseAddressElements(String wh) {
           
                    
             String[] mystring = new String[9];
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                   
                      res = st.executeQuery("select * from wh_mstr where wh_id = " + "'" + wh + "'" + ";");
                    while (res.next()) {
                       mystring[0] = res.getString("wh_id");
                        mystring[1] = res.getString("wh_name");
                        mystring[2] = res.getString("wh_addr1");
                        mystring[3] = res.getString("wh_addr2") ;
                        mystring[4] = ""; // only two addr lines
                        mystring[5] = res.getString("wh_city");
                        mystring[6] = res.getString("wh_state");
                        mystring[7] = res.getString("wh_zip") ;
                        mystring[8] = res.getString("wh_country") ;
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mystring;
        
         }    
    
    public static ArrayList getProdCodeList() {
           ArrayList myarray = new ArrayList();
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select pl_line from pl_mstr order by pl_line ;" );
               while (res.next()) {
                myarray.add(res.getString("pl_line"));                    
                }
               
           }
            catch (SQLException s){
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
      
    public static String getPMCodeByItem(String item) {
       String myitem = "";
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select it_code from item_mstr where it_item = " + "'" + item + "';" );
           while (res.next()) {
            myitem = (res.getString("it_code"));                    
            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}

    public static String getBankCodeOfCust(String cust) {
   String myitem = "";
 try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

        res = st.executeQuery("select cm_bank from cm_mstr where cm_code = " + "'" + cust + "';" );
       while (res.next()) {
        myitem = res.getString("cm_bank");                    
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myitem;

}

    public static String getPOSBank() {
        String bank = "";
         try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

        res = st.executeQuery("select posc_bank from pos_ctrl;" );
       while (res.next()) {
        bank = res.getString("posc_bank");                    
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);
}
        return bank;
    }

    public static Integer getGLTranCount() {
   int mycount = 0;
 try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

        res = st.executeQuery("select count(*) as mycount from gl_tran;" );
       while (res.next()) {
        mycount = res.getInt("mycount");                    
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return mycount;

}
         
    public static String[] getBillToAddressArray(String cust) {
        String[] address = new String[9];
         try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select cm_aliascode, cm_name, cm_line1, cm_line2, cm_line3, cm_city, cm_state, cm_zip, cm_country from cm_mstr where cm_code = " + "'" + cust + "';" );
           while (res.next()) {
            address[0] = res.getString("cm_aliascode"); 
            address[1] = res.getString("cm_name");
            address[2] = res.getString("cm_line1"); 
            address[3] = res.getString("cm_line2");
            address[4] = res.getString("cm_line3");
            address[5] = res.getString("cm_city"); 
            address[6] = res.getString("cm_state"); 
            address[7] = res.getString("cm_zip"); 
            address[8] = res.getString("cm_country"); 
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
        return address;
    }       

    public static String[] getShipToAddressArray(String cust, String ship) {
        String[] address = new String[9];
         try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select cms_plantcode, cms_name, cms_line1, cms_line2, cms_line3, cms_city, cms_state, cms_zip, cms_country from cms_det where cms_code = " + "'" + cust + "'" +
                                 " AND cms_shipto = " + "'" + ship + "'" +
                                 ";" );
           while (res.next()) {
            address[0] = res.getString("cms_plantcode"); 
            address[1] = res.getString("cms_name");
            address[2] = res.getString("cms_line1"); 
            address[3] = res.getString("cms_line2");
            address[4] = res.getString("cms_line3");
            address[5] = res.getString("cms_city"); 
            address[6] = res.getString("cms_state"); 
            address[7] = res.getString("cms_zip"); 
            address[8] = res.getString("cms_country"); 
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }

        return address;
    }  

    public static String[] getWarehouseAddressArray(String site, String wh) {
        String[] address = new String[9];
         try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select wh_id, wh_name, wh_line1, wh_line2, wh_line3, wh_city, wh_state, wh_zip, wh_country from wh_mstr where wh_site = " + "'" + site + 
                                 " AND wh_id = " + "'" + wh + "'" +
                                 "';" );
           while (res.next()) {
            address[0] = res.getString("wh_id"); 
            address[1] = res.getString("wh_name");
            address[2] = res.getString("wh_line1"); 
            address[3] = res.getString("wh_line2");
            address[4] = res.getString("wh_line3");
            address[5] = res.getString("wh_city"); 
            address[6] = res.getString("wh_state"); 
            address[7] = res.getString("wh_zip"); 
            address[8] = res.getString("wh_country"); 
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }

        return address;
    }  

    public static String[] getSiteAddressArray(String site) {
       String[] address = new String[9];
         try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select site_site, site_desc, site_line1, site_line2, site_line3, site_city, site_state, site_zip, site_country from site_mstr where site_site = " + "'" + site + 
                                  "';" );
           while (res.next()) {
            address[0] = res.getString("site_site"); 
            address[1] = res.getString("site_desc");
            address[2] = res.getString("site_line1"); 
            address[3] = res.getString("site_line2");
            address[4] = res.getString("site_line3");
            address[5] = res.getString("site_city"); 
            address[6] = res.getString("site_state"); 
            address[7] = res.getString("site_zip"); 
            address[8] = res.getString("site_country"); 
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }

        return address;
    }  

    public static String getSiteLogo(String site) {
       String myitem = "";
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select site_logo from site_mstr where site_site = " + "'" + site + "';" );
           while (res.next()) {
            myitem = res.getString("site_logo");                    
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myitem;

}

    public static boolean isLastOperation(String item, String op) {
       boolean isLast = false;
       String lastopcheck = "";
       
       if (op.equals("0")) {
           // must be a nonmfg item with no operations
           isLast = true;
       } else {
       
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                  res = st.executeQuery("select wf_op from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + item + "'" + 
                          " order by wf_op asc " + ";" ); 
             
                int i = 0;
                while (res.next()) {
                    i++;
                    lastopcheck = res.getString("wf_op");
                }
                
                if (lastopcheck.equals(op))
                    isLast = true;
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s); 
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
       } //else
        
        return isLast;
        
    }     
             
    public static boolean isValidOperation(String item, String op) {
       boolean isGood = false;
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                  res = st.executeQuery("select wf_op from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + item + "'" + 
                          " and wf_op = " + "'" + op + "'" + ";" ); 
             
                int i = 0;
                while (res.next()) {
                    i++;
                }
                if (i > 0)
                    isGood = true;
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isGood;
        
    }    
        
    public static boolean isVendItemOnly() {

   boolean venditemonly = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            
            res = st.executeQuery("select poc_venditem from po_ctrl;");
           while (res.next()) {
                venditemonly = res.getBoolean("poc_venditem");
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return venditemonly;

}   

    public static boolean isCustItemOnly() {

   boolean custitemonly = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            res = st.executeQuery("select orc_custitem from order_ctrl;");
           while (res.next()) {
                custitemonly = res.getBoolean("orc_custitem");
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return custitemonly;

}   

    public static boolean isSRVMQuoteType() {

   boolean srvmtype = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            res = st.executeQuery("select orc_srvm_type from order_ctrl;");
           while (res.next()) {
                srvmtype = res.getBoolean("orc_srvm_type");
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return srvmtype;

}   

    public static boolean isSRVMItemType() {

   boolean srvmtype = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            res = st.executeQuery("select orc_srvm_item_default from order_ctrl;");
           while (res.next()) {
                srvmtype = res.getBoolean("orc_srvm_item_default");
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return srvmtype;

}   

    public static boolean isCustItemOnlySHIP() {

   boolean custitemonly = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            res = st.executeQuery("select shc_custitemonly from ship_ctrl;");
           while (res.next()) {
                custitemonly = res.getBoolean("shc_custitemonly");
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return custitemonly;

}   

    public static boolean isAutoSource() {

boolean autosource = false;
try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        

        res = st.executeQuery("select orc_autosource from order_ctrl;");
       while (res.next()) {
            autosource = res.getBoolean("orc_autosource");
        }

   }
    catch (SQLException s){
        MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return autosource;

}   

    public static boolean isAutoItem() {
             
       boolean autoitem = false;
        try{
           
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

                res = st.executeQuery("select autoitem from inv_ctrl;");
               while (res.next()) {
                    autoitem = res.getBoolean("autoitem");
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return autoitem;
        
    }   
          
    public static boolean isAutoInvoice() {
             
       boolean autoinvoice = false;
        try{
           
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
                res = st.executeQuery("select orc_autoinvoice from order_ctrl;");
               while (res.next()) {
                    autoinvoice = res.getBoolean("orc_autoinvoice");
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return autoinvoice;
        
    }  
         
    public static boolean isOrderAutoAllocate() {
             
       boolean autoallocate = false;
        try{
           
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

                res = st.executeQuery("select orc_autoallocate from order_ctrl;");
               while (res.next()) {
                    autoallocate = res.getBoolean("orc_autoallocate");
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return autoallocate;
        
    }       
     
    public static boolean isOrderExceedQOHU() {
             
       boolean exceedqohu = false;
        try{
           
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

                res = st.executeQuery("select orc_exceedqohu from order_ctrl;");
               while (res.next()) {
                    exceedqohu = res.getBoolean("orc_exceedqohu");
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return exceedqohu;
        
    }       
        
    public static boolean isAutoCust() {
             
       boolean autocust = false;
        try{
           
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
                res = st.executeQuery("select cmc_autocust from cm_ctrl;");
               while (res.next()) {
                    autocust = res.getBoolean("cmc_autocust");
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return autocust;
        
    }  
       
    public static boolean isAutoVend() {
             
       boolean autovend = false;
        try{
           
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

                res = st.executeQuery("select vdc_autovend from vd_ctrl;");
               while (res.next()) {
                    autovend = res.getBoolean("vdc_autovend");
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return autovend;
        
    }  
       
    public static boolean isAutoPost() {
             
       boolean autopost = false;
        try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

                res = st.executeQuery("select gl_autopost from gl_ctrl;");
               while (res.next()) {
                    autopost = res.getBoolean("gl_autopost");
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return autopost;
        
    }  
       
    public static boolean isAutoVoucher() {
             
       boolean autovoucher = false;
        try{
           
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
                res = st.executeQuery("select apc_autovoucher from ap_ctrl;");
               while (res.next()) {
                    autovoucher = res.getBoolean("apc_autovoucher");
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return autovoucher;
        
    }  
        
    public static boolean isValidItem(String myitem) {
             
       boolean isgood = false;
        try{
           
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

                res = st.executeQuery("select it_item from item_mstr where it_item = " + "'" + myitem + "'" + ";");
               while (res.next()) {
                    isgood = true;
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isgood;
        
    }
         
    public static boolean isValidShipperByCustAndBOL(String bol, String cust) {
             
       boolean isgood = false;
        try{
           
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select sh_bol from ship_mstr where sh_bol = " + "'" + bol + "'" +
                        " AND sh_cust = " + "'" + cust + "'" + 
                        ";");
               while (res.next()) {
                    isgood = true;
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isgood;
        
    }
         
    public static boolean isValidFreightOrderNbr(String nbr) {
             
       boolean isgood = false;
        try{
           
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

                res = st.executeQuery("select fo_nbr from fo_mstr where fo_nbr = " + "'" + nbr + "'" +
                        ";");
               while (res.next()) {
                    isgood = true;
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isgood;
        
    }  
      
    public static boolean isValidSite(String key) {

   boolean isgood = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

            res = st.executeQuery("select site_site from site_mstr where site_site = " + "'" + key + "'" + ";");
           while (res.next()) {
                isgood = true;
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isgood;

}

    public static boolean isValidCustomer(String key) {

   boolean isgood = false;
    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

            res = st.executeQuery("select cm_code from cm_mstr where cm_code = " + "'" + key + "'" + ";");
           while (res.next()) {
                isgood = true;
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isgood;

}

    public static boolean isValidCustShipTo(String cust, String ship) {

   boolean isgood = false;
    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

            res = st.executeQuery("select cms_shipto from cms_det where cms_code = " + "'" + cust + "'" 
                    + " and cms_shipto = " + "'" + ship + "'" 
                    + ";");
           while (res.next()) {
                isgood = true;
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isgood;

}

    public static boolean isValidVendor(String key) {

   boolean isgood = false;
    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

            res = st.executeQuery("select vd_addr from vd_mstr where vd_addr = " + "'" + key + "'" + ";");
           while (res.next()) {
                isgood = true;
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isgood;

}

    public static boolean isValidLocation(String key) {

   boolean isgood = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            res = st.executeQuery("select loc_loc from loc_mstr where loc_loc = " + "'" + key + "'" + ";");
           while (res.next()) {
                isgood = true;
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isgood;

}

    public static boolean isValidWarehouse(String key) {

   boolean isgood = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            res = st.executeQuery("select wh_id from wh_mstr where wh_id = " + "'" + key + "'" + ";");
           while (res.next()) {
                isgood = true;
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isgood;

}

    public static boolean isValidProdLine(String key) {

   boolean isgood = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            res = st.executeQuery("select pl_line from pl_mstr where pl_line = " + "'" + key + "'" + ";");
           while (res.next()) {
                isgood = true;
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isgood;

}

    public static boolean isValidPanel(String panel) {

   boolean isgood = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

            res = st.executeQuery("select panel_id from panel_mstr where panel_id = " + "'" + panel + "'" + ";");
           while (res.next()) {
                isgood = true;
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isgood;

}

    public static boolean isValidRouting(String key) {
   boolean isGood = false;
    try{

        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

              res = st.executeQuery("select wf_id from wf_mstr where wf_id = " + "'" + key + "'" + ";" ); 
            int i = 0;
            while (res.next()) {
                i++;
            }
            if (i > 0)
                isGood = true;

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isGood;

}  

    public static boolean isValidWorkCenter(String key) {
       boolean isGood = false;
        try{
            
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

                  res = st.executeQuery("select wc_cell from wc_mstr where wc_cell = " + "'" + key + "'" + ";" ); 
                int i = 0;
                while (res.next()) {
                    i++;
                }
                if (i > 0)
                    isGood = true;
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isGood;
        
    }  
          
    public static boolean isValidShift(String code) {

boolean isgood = false;
try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

        res = st.executeQuery("select shf_id from shift_mstr where shf_id = " + "'" + code + "'" + ";");
       while (res.next()) {
            isgood = true;
        }

   }
    catch (SQLException s){
         MainFrame.bslog(s);
    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return isgood;

}

    public static ArrayList getShiftCodes() {
    ArrayList myarray = new ArrayList();
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

        res = st.executeQuery("select shf_id from shift_mstr;");
       while (res.next()) {
            myarray.add(res.getString("shf_id"));
        }

   }
    catch (SQLException s){
         bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
    } finally {
       if (res != null) res.close();
       if (st != null) st.close();
       con.close();
    }
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myarray;

}

    public static String getShiftSpecific(String a, String b, String c) throws ParseException {
     // a, b, c = specific time as string...example "06:00", "14:00", "22:00"
     String myshift = "";
     Calendar now = Calendar.getInstance();

     int hour = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
     int minute = now.get(Calendar.MINUTE);
     Date currenttime = new SimpleDateFormat("HH:mm").parse(hour + ":" + minute);
     Date firstshift = new SimpleDateFormat("HH:mm").parse(a);  // a = "06:00"
     Date secondshift = new SimpleDateFormat("HH:mm").parse(b);
     Date thirdshift = new SimpleDateFormat("HH:mm").parse(c);


     if (firstshift.before( currenttime ) && secondshift.after(currenttime)) {
        myshift = "1";
     }
     if (secondshift.before( currenttime ) && thirdshift.after(currenttime)) {
        myshift = "2";
     }
     if (thirdshift.before( currenttime ) || currenttime.before(firstshift)) {
        myshift = "3";
     }
     if (firstshift.equals(currenttime))
         myshift = "1";
     if (secondshift.equals(currenttime))
         myshift = "2";
     if (thirdshift.equals(currenttime))
         myshift = "3";
      return myshift;
  }

    /* stop here */
    
    public static ArrayList getOperationsByItem(String item) {
   ArrayList myarray = new ArrayList();
    try{
        Connection con = null;
        if (ds != null) {
              con = ds.getConnection();
        } else {
              con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            res = st.executeQuery("select wf_op from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + item + "'" + " order by wf_op ;");
           while (res.next()) {
                myarray.add(res.getString("wf_op"));
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static int getFirstOpByItem(String item) {
       int myreturn = -1;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            res = st.executeQuery("select min(wf_op) as op from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + item + "'" + 
                    " and wf_assert = '1' group by wf_assert order by wf_op desc;");

           while (res.next()) {
                myreturn = res.getInt("op");
            }

       } catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static int getLastOpByItem(String item) {
       int myreturn = -1;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            

            res = st.executeQuery("select max(wf_op) from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + item + "'" + 
                    " and wf_assert = '1' group by wf_assert order by wf_op desc;");

           while (res.next()) {
                myreturn = res.getInt("wf_op");
            }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}


    public static Double getLaborWithOutSetup(String item, String op) {

         Double labor = 0.0;

         try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            

            res = st.executeQuery("select it_lotsize, wc_run_crew, wf_run_hours, wf_setup_hours, wc_run_rate, wc_setup_rate, wc_bdn_rate from wf_mstr " + 
                    " inner join item_mstr on it_wf = wf_id " + 
                    " inner join wc_mstr on wc_cell = wf_cell " +
                    " where it_item = " + "'" + item + "'" +
                    " AND wf_op = " + "'" + op + "'" + ";");
           while (res.next()) {
                labor += (res.getDouble("wc_run_rate") * res.getDouble("wf_run_hours") * res.getDouble("wc_run_crew"));
            }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
         return labor;

     }

    public static Double getLaborAllOps(String item) {

         Double labor = 0.0;
         try{

        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            

            res = st.executeQuery("select it_lotsize, wf_run_hours, wc_setup_rate, wf_setup_hours, wc_run_rate, wc_run_crew from wf_mstr " + 
                    " inner join item_mstr on it_wf = wf_id " + 
                    " inner join wc_mstr on wc_cell = wf_cell  " +
                    " where it_item = " + "'" + item + "'" + ";");
           while (res.next()) {

            if (res.getDouble("it_lotsize") == 0) {
                labor += ( ((res.getDouble("wc_setup_rate") * res.getDouble("wf_setup_hours")) ) +
                        (res.getDouble("wc_run_rate") * res.getDouble("wf_run_hours") * res.getDouble("wc_run_crew")));
               } else {
                 labor += ( ((res.getDouble("wc_setup_rate") * res.getDouble("wf_setup_hours")) / res.getDouble("it_lotsize") ) +
                        (res.getDouble("wc_run_rate") * res.getDouble("wf_run_hours") * res.getDouble("wc_run_crew")));
               }




           }

       }
        catch (SQLException s){
          MainFrame.bslog(s);
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
         return labor;

     }

    public static Double getBurdenAllOps(String item) {

         Double burden = 0.0;

         try{

        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            

            res = st.executeQuery("select it_lotsize, wf_run_hours, wc_setup_rate, wf_setup_hours, wc_bdn_rate, wc_run_crew from wf_mstr " + 
                    " inner join item_mstr on it_wf = wf_id " + 
                    " inner join wc_mstr on wc_cell = wf_cell  " +
                    " where it_item = " + "'" + item + "'" + ";");
           while (res.next()) {
            if (res.getDouble("it_lotsize") == 0) {
                burden += ( ((res.getDouble("wc_bdn_rate") * res.getDouble("wf_setup_hours"))  ) +
                        (res.getDouble("wc_bdn_rate") * res.getDouble("wf_run_hours")  )  );
               } else {
                burden += ( ((res.getDouble("wc_bdn_rate") * res.getDouble("wf_setup_hours"))) / res.getDouble("it_lotsize") ) +
                        (res.getDouble("wc_bdn_rate") * res.getDouble("wf_run_hours") ) ;  
               }

           }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
         return burden;

     }

    public static Double getBurdenWithOutSetup(String item, String op) {

         Double burden = 0.0;
          try{

        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            

            res = st.executeQuery("select it_lotsize, wc_run_crew, wf_run_hours, wf_setup_hours, wc_run_rate, wc_setup_rate, wc_bdn_rate from wf_mstr " + 
                    " inner join item_mstr on it_wf = wf_id " + 
                    " inner join wc_mstr on wc_cell = wf_cell " +
                    " where it_item = " + "'" + item + "'" +
                    " AND wf_op = " + "'" + op + "'" + ";");
           while (res.next()) {
                 burden += (res.getDouble("wc_bdn_rate") * res.getDouble("wf_run_hours") );
            }

       }
        catch (SQLException s){
        MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
         return burden;

     }

    public static void setStandardCosts(String site, String item) {
        calcCost cur = new calcCost();
         ArrayList<Double> costcur = new ArrayList<Double>();
        costcur = cur.getTotalCostElements(item, ""); // assume default bom
        Double totalcost = 0.00;
        for (Double d : costcur) {
           totalcost += d;
        }
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
            
            boolean proceed = true;

            int i = 0;
            String perms = "";
            double itrcost = 0.00;
            String routing = invData.getItemRouting(item);
            ArrayList<String> ops = OVData.getOperationsByItem(item);
            // lets do item_cost first 
            res = st.executeQuery("SELECT itc_item FROM  item_cost where itc_item = " + "'" + item + "'" + ";");
                while (res.next()) {
                    i++;
                }

                if (i > 0) {
                    st.executeUpdate("update item_cost set "
                            + "itc_mtl_low = " + "'" + costcur.get(0).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_mtl_top = " + "'" + costcur.get(5).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_lbr_low = " + "'" + costcur.get(1).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_lbr_top = " + "'" + costcur.get(6).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_bdn_low = " + "'" + costcur.get(2).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_bdn_top = " + "'" + costcur.get(7).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_ovh_low = " + "'" + costcur.get(3).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_ovh_top = " + "'" + costcur.get(8).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_out_low = " + "'" + costcur.get(4).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_out_top = " + "'" + costcur.get(9).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "itc_total = " + "'" +  totalcost.toString().replace(defaultDecimalSeparator, '.') + "'" 
                            + " where itc_item = " + "'" + item + "'"
                            + " AND itc_set = 'standard' "
                            + " AND itc_site = " + "'" + site + "'"
                            + ";");

                } else {
                    proceed = false;
                }
                // ok now lets do itemr_cost ...routing based costing
               if (proceed) { 
                i = -1;
                for (String op : ops) {
                // bsmf.MainFrame.show(op);
                i++;
                // delete original itemr_cost records for this item, op, routing, standard
                st.executeUpdate(" delete FROM  itemr_cost where itr_item = " + "'" + item + "'" 
                                     +  " AND itr_op = " + "'" + op + "'"
                                     + " AND itr_set = 'standard' "
                                     + " AND itr_site = " + "'" + site + "'"
                                     + " AND itr_routing = " + "'" + routing + "'" + ";");

                }
                 ArrayList<String> costs = new ArrayList<String>();
                 costs = OVData.rollCost(item);

                for (String cost : costs) {
                 String[] elements = cost.split(",", -1);

                        st.executeUpdate("insert into itemr_cost (itr_item, itr_site, itr_set, itr_routing, itr_op, " +
                             " itr_total, itr_lbr_top, itr_bdn_top, itr_mtl_top,  itr_ovh_top, itr_out_top, " +
                             " itr_mtl_low, itr_lbr_low, itr_bdn_low, itr_ovh_low, itr_out_low ) values ( "
                            + "'" + item + "'" + ","
                            + "'" + site + "'" + ","
                            + "'" + "standard" + "'" + ","
                            + "'" + routing + "'" + ","
                            + "'" + elements[1].toString() + "'" + ","
                            + "'" + bsformat("",elements[17].toString(),"5").replace(defaultDecimalSeparator, '.') + "'" + "," 
                            + "'" + bsformat("",elements[6].toString(),"5").replace(defaultDecimalSeparator, '.') + "'" + ","   
                            + "'" + bsformat("",elements[7].toString(),"5").replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "'" + bsformat("",elements[8].toString(),"5").replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "'" + bsformat("",elements[9].toString(),"5").replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "'" + bsformat("",elements[10].toString(),"5").replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "'" + "0" + "'" + ","
                            + "'" + "0" + "'" + ","
                            + "'" + "0" + "'" + ","
                            + "'" + "0" + "'" + ","
                            + "'" + "0" + "'" + " ) ;");
                }  

               }
        } // if proceed
        catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }  
    }

    public static ArrayList rollCost(String item) {
         ArrayList<String> myarray = new ArrayList<String>();
         String mystring = "";
         Double labor = 0.0;
         Double burden = 0.0;
         Double material = 0.0;
         Double ovh = 0.0;
         Double outside = 0.0;
         Double total = 0.0;
         Double prevcost = 0.0;
         Double stdopcost = 0.0;
         String op = "";
         String cell = "";
         String cc = "";
         String desc = "";
         String stdcost = "";



         try{

        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet res = null;
            ResultSet res2 = null;
        try{
            

            // lets first get standard cost of this item for comparison
            res = st.executeQuery("select itc_total from item_cost where itc_set = 'standard' and itc_item = " + "'" + item + "';" );
             while (res.next()) {
             stdcost = res.getString("itc_total");
             }

             // now lets get all operational costs for this parent item and affiliated variables ...and grab matl cost for each operation
            res = st.executeQuery("select it_lotsize, itr_total, wf_cell, wf_op, wc_run_crew, wf_run_hours, wf_setup_hours, " +
                    " wc_desc, wc_cc, wc_run_rate, wc_setup_rate, wc_bdn_rate " +
                    " from wf_mstr inner join item_mstr on it_wf = wf_id " + 
                    " inner join wc_mstr on wc_cell = wf_cell  " + 
                    " left outer join itemr_cost on itr_item = it_item and itr_site = it_site and itr_routing = item_mstr.it_wf and itr_op = wf_op " +
                    " where it_item = " + "'" + item + "'" + 
                    " order by wf_op; " );
           int i = 0;         
           while (res.next()) {
               i++;

               if (i == 1) {
                   stdopcost = res.getDouble("itr_total");
               } else {
                   stdopcost = res.getDouble("itr_total") - prevcost;
               }
               prevcost = res.getDouble("itr_total");

               op = res.getString("wf_op");
               cell = res.getString("wf_cell");
               desc = res.getString("wc_desc");
               cc = res.getString("wc_cc");


               material = 0.0;
               labor = 0.0;
               burden = 0.0;
               if (res.getDouble("it_lotsize") == 0) {
                labor += ( ((res.getDouble("wc_setup_rate") * res.getDouble("wf_setup_hours")) ) +
                        (res.getDouble("wc_run_rate")) * res.getDouble("wf_run_hours") * res.getDouble("wc_run_crew"));
                burden += ( ((res.getDouble("wc_bdn_rate") * res.getDouble("wf_setup_hours"))  ) +
                        (res.getDouble("wc_bdn_rate") * res.getDouble("wf_run_hours") )  );
               } else {
                 labor += ( ((res.getDouble("wc_setup_rate") * res.getDouble("wf_setup_hours")) / res.getDouble("it_lotsize") ) +
                         (res.getDouble("wc_run_rate") * res.getDouble("wf_run_hours") * res.getDouble("wc_run_crew") )  );
                burden += ( ((res.getDouble("wc_bdn_rate") * res.getDouble("wf_setup_hours")) / res.getDouble("it_lotsize") ) +
                        (res.getDouble("wc_bdn_rate") * res.getDouble("wf_run_hours") )  );  
               }

               // now do the matl for this operation
                       res2 = st2.executeQuery("select ps_qty_per, itc_total from pbm_mstr " + 
                            " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' " +
                            " inner join item_cost on ps_child = itc_item and itc_set = 'standard' " +
                            " where ps_parent = " + "'" + item + "'" +
                            " AND ps_op = " + "'" + op + "'" + ";");
                        while (res2.next()) {
                        material += ( res2.getDouble("itc_total") * res2.getDouble("ps_qty_per") );
                        //ovh += ( res2.getDouble("itc_total") * res2.getDouble("ps_qty_per") );
                        //outside += ( res2.getDouble("itc_total") * res2.getDouble("ps_qty_per") );
                       }
                        total = labor + burden + material + ovh + outside;
           mystring = 
                   item + "," +
                   op + "," +
                   cell + "," +
                   "" + "," +   // used to be machine
                   desc + "," +
                   cc + "," +
                   String.valueOf(labor) + "," + 
                   String.valueOf(burden) + "," + 
                   String.valueOf(material) + "," + 
                   String.valueOf(ovh) + "," + 
                   String.valueOf(outside) + "," +
                   res.getString("wc_setup_rate") + "," +
                   res.getString("wc_run_rate") + "," +
                   res.getString("wc_bdn_rate") + "," +
                   res.getString("wf_setup_hours") + "," +
                   res.getString("wf_run_hours") + "," +
                   res.getString("it_lotsize") + "," +
                   String.valueOf(total) + "," +
                   String.valueOf(stdopcost) + "," +
                   stdcost
                   ;
           myarray.add(mystring);

           } 

       }
        catch (SQLException s){
            MainFrame.bslog(s);
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
            if (res != null) res.close();
            if (res2 != null) res2.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
         return myarray;
     }

    public static Double simulateCost(String site, String item, String opvar, 
                                      double runratevar,
                                      double setupratevar,
                                      double burdenratevar,
                                      double runhoursvar,
                                      double setuphoursvar,
                                      double runcrewvar,
                                      double setupcrewvar,
                                      double lotsizevar,
                                      boolean doMatl) {
        double x = 0.00;
        Double labor = 0.0;
         Double burden = 0.0;
         Double material = 0.0;
         Double ovh = 0.0;
         Double outside = 0.0;
         Double total = 0.0;
         String op = "";
              try{

        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet res = null;
            ResultSet res2 = null;
        try{
            


             // now lets get all operational costs for this parent item and affiliated variables ...and grab matl cost for each operation
            res = st.executeQuery("select it_lotsize, itr_total, wf_cell, wf_op, wc_run_crew, wf_run_hours, wf_setup_hours, " +
                    " wc_desc, wc_cc, wc_run_rate, wc_setup_rate, wc_bdn_rate " +
                    " from wf_mstr inner join item_mstr on it_wf = wf_id " + 
                    " inner join wc_mstr on wc_cell = wf_cell  " + 
                    " left outer join itemr_cost on itr_item = it_item and itr_routing = item_mstr.it_wf and itr_op = wf_op " +
                    " where it_item = " + "'" + item + "'" + 
                    " order by wf_op; " );
           int i = 0;         
           while (res.next()) {
               i++;

               op = res.getString("wf_op");


               material = 0.0;
               labor = 0.0;
               burden = 0.0;

               if (op.equals(opvar)) {
                   // simulation
                   if (lotsizevar == 0) {
                    labor += ( ((setupratevar * setuphoursvar) ) +
                            (runratevar * runhoursvar * runcrewvar )  );
                    burden += ( ((burdenratevar * setuphoursvar)  ) +
                            (burdenratevar * runhoursvar )  );
                   } else {
                     labor += ( ((setupratevar * setuphoursvar) / lotsizevar ) +
                             (runratevar * runhoursvar * runcrewvar )  );
                    burden += ( ((burdenratevar * setuphoursvar) / lotsizevar ) +
                            (burdenratevar * runhoursvar )  );  
                   }


               } else {
                   if (res.getDouble("it_lotsize") == 0) {
                    labor += ( ((res.getDouble("wc_setup_rate") * res.getDouble("wf_setup_hours")) ) +
                            (res.getDouble("wc_run_rate") * res.getDouble("wf_run_hours") * res.getDouble("wc_run_crew"))   );
                    burden += ( ((res.getDouble("wc_bdn_rate") * res.getDouble("wf_setup_hours"))  ) +
                            (res.getDouble("wc_bdn_rate") * res.getDouble("wf_run_hours") )  );
                   } else {
                     labor += ( ((res.getDouble("wc_setup_rate") * res.getDouble("wf_setup_hours")) / res.getDouble("it_lotsize") ) +
                             (res.getDouble("wc_run_rate") * res.getDouble("wf_run_hours") * res.getDouble("wc_run_crew"))   );
                    burden += ( ((res.getDouble("wc_bdn_rate") * res.getDouble("wf_setup_hours")) / res.getDouble("it_lotsize") ) +
                            (res.getDouble("wc_bdn_rate") * res.getDouble("wf_run_hours") )  );  
                   }
               }

               if (doMatl) {
               // now do the matl for this operation
               res2 = st2.executeQuery("select ps_qty_per, itc_total from pbm_mstr " + 
                    " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' " +   
                    " inner join item_cost on ps_child = itc_item and itc_set = 'standard' " +
                    " where ps_parent = " + "'" + item + "'" +
                    " AND ps_op = " + "'" + op + "'" + ";");
                while (res2.next()) {
                material += ( res2.getDouble("itc_total") * res2.getDouble("ps_qty_per") );
                //ovh += ( res2.getDouble("itc_total") * res2.getDouble("ps_qty_per") );
                //outside += ( res2.getDouble("itc_total") * res2.getDouble("ps_qty_per") );
               }
               } // if calling program wants this routine to include material
            total = labor + burden + material + ovh + outside;
           } 

       }
        catch (SQLException s){
            MainFrame.bslog(s);
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
            if (res != null) res.close();
            if (res2 != null) res2.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
        return total;
    }

    
    public static String getOrderBillto(String order) {
         String billto = "";
          try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);



                  res = st.executeQuery("select so_cust from so_mstr where so_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    billto = res.getString("so_cust");
                }
       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
         return billto;
     }

    public static ArrayList getOrderWHSource(String order) {
         ArrayList<String> wh = new ArrayList<String>();
          try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            
                  // select statement will return all 'unique' warehouses assigned to source the order
                  res = st.executeQuery("select sod_wh from sod_det where sod_nbr = " + "'" + order + "'" +" group by sod_wh ;");
                while (res.next()) {
                    wh.add(res.getString("sod_wh"));
                }
       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
         return wh;
     }

    public static String getFreightOrderCarrierAssigned(String order) {
        String carrier = "";
          try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            

                  // select statement will return all 'unique' warehouses assigned to source the order
                  res = st.executeQuery("select fo_carrier_assigned from fo_mstr where fo_nbr = " + "'" + order + "'" +";");
                  while (res.next()) {
                      carrier = res.getString("fo_carrier_assigned"); 
                  }
       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
         return carrier;
   }

    public static String getFreightOrderNbrFromCustFO(String custfo) {
        String fo = "";
          try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            

                  res = st.executeQuery("select fo_nbr from fo_mstr where fo_custfo = " + "'" + custfo + "'" +";");
                  while (res.next()) {
                      fo = res.getString("fo_nbr"); 
                  }
       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
         return fo;
   }   
    
    public static ArrayList getFreightOrderCarrierList(String order) {
         ArrayList<String> carriers = new ArrayList<String>();
          try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
            String cartype = "";
            String carrier = "";
                  // select statement will return all 'unique' warehouses assigned to source the order
                  res = st.executeQuery("select fo_carrier, car_type from fo_mstr inner join car_mstr on car_code = fo_carrier where fo_nbr = " + "'" + order + "'" +";");
                  while (res.next()) {
                      cartype = res.getString("car_type");
                      carrier = res.getString("fo_carrier"); 
                  }

                  if (cartype.equals("group")) {
                      res = st.executeQuery("select card_carrier from car_det where card_code = " + "'" + carrier + "'" +";");
                      while (res.next()) {
                      carriers.add(res.getString("card_carrier"));
                      }
                  } else {
                      carriers.add(carrier);
                  }


       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
         return carriers;
     }

    
    public static String[] getFreightOrderHeaderArray(String order) {
          String header[] = new String[12];
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try {
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);


                res = st.executeQuery("select * from fo_mstr where fo_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    header[0] = res.getString("fo_nbr");
                    header[1] = res.getString("fo_ref");
                    header[2] = res.getString("fo_site");
                    header[3] = res.getString("fo_wh");
                    header[4] = res.getString("fo_date");
                    header[5] = res.getString("fo_rmks");
                    header[6] = res.getString("fo_carrier");
                    header[7] = res.getString("fo_carrier_assigned");
                    header[8] = res.getString("fo_reasoncode");
                    header[9] = res.getString("fo_custfo");
                    header[10] = res.getString("fo_type");
                    header[11] = res.getString("fo_status");
                }
       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
    return header;

     }

    public static ArrayList getFreightOrderDetailArray(String order) {
          ArrayList<String[]> detail = new ArrayList<String[]>();
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try {
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);



                res = st.executeQuery("select fod_line, fod_type, fod_shipper, fod_ref, fod_shipdate, fod_shiptime, fod_delvdate, fod_delvtime, fod_code, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip, " +
                        " fod_phone, fod_contact, fod_remarks, fod_units, fod_boxes, fod_weight, fod_wt_uom from fod_det " +
                        "  where fod_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    detail.add(new String[]{res.getString("fod_line").toUpperCase(), 
                        res.getString("fod_type").toUpperCase(), 
                        res.getString("fod_shipper").toUpperCase(), 
                        res.getString("fod_ref").toUpperCase(), 
                        res.getString("fod_shipdate").toUpperCase(), 
                        res.getString("fod_shiptime").toUpperCase(), 
                        res.getString("fod_delvdate").toUpperCase(), 
                        res.getString("fod_delvtime").toUpperCase(), 
                        res.getString("fod_code").toUpperCase(), 
                        res.getString("fod_name").toUpperCase(), 
                        res.getString("fod_addr1").toUpperCase(), 
                        res.getString("fod_addr2").toUpperCase(), 
                        res.getString("fod_city").toUpperCase(),
                        res.getString("fod_state").toUpperCase(),
                        res.getString("fod_zip").toUpperCase(),
                        res.getString("fod_phone").toUpperCase(),
                        res.getString("fod_contact").toUpperCase(),
                        res.getString("fod_remarks").toUpperCase(),
                        res.getString("fod_units").toUpperCase(),
                        res.getString("fod_boxes").toUpperCase(),
                        res.getString("fod_weight").toUpperCase(),
                        res.getString("fod_wt_uom").toUpperCase()
                    });
                }
       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
    return detail;

     }

    public static String[] getOrderHeaderArray(String order) {
          String header[] = new String[7];
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try {
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);

                // so_nbr, so_po, so_cust, so_ship, so_due_date, so_shipvia
                res = st.executeQuery("select * from so_mstr where so_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    header[0] = res.getString("so_nbr").toUpperCase();
                    header[1] = res.getString("so_po").toUpperCase();
                    header[2] = res.getString("so_cust").toUpperCase();
                    header[3] = res.getString("so_ship").toUpperCase();
                    header[4] = res.getString("so_due_date").toUpperCase();
                    header[5] = res.getString("so_shipvia");
                    header[6] = res.getString("so_rmks").toUpperCase();
                }
       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
    return header;

     }

    public static ArrayList getOrderDetailArray(String order) {
      ArrayList<String[]> detail = new ArrayList<String[]>();
try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
    ResultSet res = null;
    try {
       java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
        String mydate = dfdate.format(now);


            // sod_line, sod_item, sod_custitem, sod_ord_qty, sod_uom, sod_desc, it_net_wt, sod_listprice, sod_disc, sod_netprice, sod_site, sod_wh, sod_loc
            res = st.executeQuery("select sod_line, sod_item, sod_custitem, sod_ord_qty, sod_uom, sod_desc, it_net_wt, sod_listprice, sod_disc, sod_netprice, sod_site, sod_wh, sod_loc from sod_det " +
                    " left outer join item_mstr on it_item = sod_item where sod_nbr = " + "'" + order + "'" +";");
            while (res.next()) {
                detail.add(new String[]{res.getString("sod_line").toUpperCase(), 
                    res.getString("sod_item").toUpperCase(), 
                    res.getString("sod_custitem").toUpperCase(), 
                    res.getString("sod_ord_qty").toUpperCase(), 
                    res.getString("sod_uom").toUpperCase(), 
                    res.getString("sod_desc").toUpperCase(), 
                    res.getString("it_net_wt").toUpperCase(),
                    res.getString("sod_listprice").toUpperCase(),
                    res.getString("sod_disc").toUpperCase(),
                    res.getString("sod_netprice").toUpperCase(),
                    res.getString("sod_site").toUpperCase(),
                    res.getString("sod_wh").toUpperCase(),
                    res.getString("sod_loc").toUpperCase()
                });
            }
   }
    catch (SQLException s){
         MainFrame.bslog(s);

    } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);

}
return detail;

 }

    public static String getOrderHeader(String order) {
      String mystring = "";
try{

    Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
    Statement st = con.createStatement();
        ResultSet res = null;
    try{
        
       java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
        String mydate = dfdate.format(now);


            res = st.executeQuery("select * from so_mstr where so_nbr = " + "'" + order + "'" +";");
            while (res.next()) {
                mystring = res.getString("so_cust") + "," +
                           res.getString("so_ship") + "," +
                        res.getString("so_nbr") + "," +
                        res.getString("so_po") + "," +
                        res.getString("so_ord_date") + "," +
                        res.getString("so_due_date") + "," +
                        res.getString("so_rmks") + "," +
                        res.getString("so_char1") + "," +
                        res.getString("so_char2") + "," +
                        res.getString("so_char3") + "," +
                        res.getString("so_shipvia") ;
            }
   }
    catch (SQLException s){
         MainFrame.bslog(s);

    } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            con.close();
        }
}
catch (Exception e){
    MainFrame.bslog(e);

}
return mystring;

 }

    
  
    /* start of inventory related functions */
                
    public static boolean UpdateInventoryDiscrete(String item, String site, String loc, String wh, String serial, String expire, Double qty) {
          boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            Statement st3 = con.createStatement();
            ResultSet res = null;
            ResultSet nres = null;
        try{
            

            java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);

            // Skip item code "S" when adjusting inventory -- service item
            String itemcode = invData.getItemCode(item);
            if (itemcode.equals("S")) {  
                return false;
            }

            // check for serialized inventory flag...if not...prevent serial from entry into in_mstr
            if (! OVData.isInvCtrlSerialize()) {
                serial = "";
                expire = "";
            }

                    double sum = 0.00;

                    // check if in_mstr record exists for this part, loc, wh, site, serial, expire combo
                    // if not add it
                    nres = st2.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"      
                            + ";");
                    int z = 0;
                     double qoh = 0.00;
                      while (nres.next()) {
                        z++;
                        qoh = bsParseDouble(nres.getString("in_qoh"));
                    }
                    nres.close();


                    if (z == 0) {
                     sum = qty;
                     st3.executeUpdate("insert into in_mstr "
                            + "(in_site, in_item, in_loc, in_wh, in_serial, in_expire, in_qoh, in_date ) "
                            + " values ( " 
                            + "'" + site + "'" + ","
                            + "'" + item + "'" + ","
                            + "'" + loc + "'" + ","
                            + "'" + wh + "'" + ","
                            + "'" + serial + "'" + ","
                            + "'" + expire + "'" + ","        
                            + "'" + sum + "'" + ","
                            + "'" + mydate + "'"
                            + ")"
                            + ";");

                    }  else {
                       // nres.first();
                        sum = qoh + qty;
                         st3.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + sum + "'" + "," +
                              " in_date = " + "'" + mydate + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"       
                            + ";");
                    }




       }
        catch (SQLException s){
             MainFrame.bslog(s);
             myerror = true;
        } finally {
            if (res != null) res.close();
            if (nres != null) nres.close();
            if (st != null) st.close();
            if (st2 != null) st2.close();
            if (st3 != null) st3.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
        myerror = true;
    }
    return myerror;

     }

    public static void UpdateInventoryFromPOS(String nbr, boolean isVoid, Connection bscon) throws SQLException {
    Statement st = bscon.createStatement();
    Statement st2 = bscon.createStatement();
    Statement st3 = bscon.createStatement();
    Statement st4 = bscon.createStatement();
    ResultSet res = null;
    ResultSet res2 = null;
    ResultSet nres = null;

           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);


                String item = "";
                double qty = 0;
                String loc = "";
                String wh = "";
                String site = OVData.getDefaultSite();
                double sum = 0;
                int i = 0;
                  res = st.executeQuery("select posd_item, posd_qty from pos_det " +
                          " where posd_nbr = " + "'" + nbr + "'" +";");
                while (res.next()) {
                    i = 0;
                    item = res.getString("posd_item");

                    if (isVoid) {
                    qty = (-1 * res.getDouble("posd_qty"));
                    } else {
                     qty = res.getDouble("posd_qty");   
                    }


                    // lets determine if this is a legitimate item or a misc item...do not inventory misc items
                    res2 = st4.executeQuery("select it_item, it_loc, it_code " +
                          " from  item_mstr  " +
                          " where it_item = " + "'" + item + "'" +";");

                    while (res2.next()) {
                        // continue if service item
                        if (res2.getString("it_code").equals("S")) {
                        continue;
                        }
                        i++;
                        // if no loc in shipper then grab the item default loc
                        if (loc.isEmpty())
                           loc = res2.getString("it_loc");
                    }
                    // if no item_mstr then continue loop...must be miscellaneous item
                    if (i == 0) {
                        continue;
                    }
                    
           
                    
                    // check if in_mstr record exists for this part,loc,site combo
                    // if not add it
                    nres = st2.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + ";");

                      int z = 0;
                     double qoh = 0.00;
                      while (nres.next()) {
                        z++;
                        qoh = bsParseDouble(nres.getString("in_qoh"));
                    }
                    nres.close();


                    if (z == 0) {
                     sum = (-1 * qty);
                     st3.executeUpdate("insert into in_mstr "
                            + "(in_site, in_item, in_loc, in_wh, in_qoh, in_date ) "
                            + " values ( " 
                            + "'" + site + "'" + ","
                            + "'" + item + "'" + ","
                            + "'" + loc + "'" + ","
                            + "'" + wh + "'" + ","
                            + "'" + sum + "'" + ","
                            + "'" + mydate + "'"
                            + ")"
                            + ";");

                    }  else {
                       // nres.first();
                        sum = qoh - qty;
                         st3.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + sum + "'" + "," +
                              " in_date = " + "'" + mydate + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + ";");
                    }
                }
       if (st != null) {st.close();}
       if (st2 != null) {st2.close();}
       if (st3 != null) {st3.close();}
       if (st4 != null) {st4.close();}
       if (res != null) {res.close();}
       if (res2 != null) {res2.close();}
       if (nres != null) {nres.close();}

     }
   
    public static boolean UpdateInventoryFromShipperRV(String shipper) {
          boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            Statement st3 = con.createStatement();
            Statement st4 = con.createStatement();
            ResultSet res = null;
            ResultSet res2 = null;
            ResultSet nres = null;
        try{
            

           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);


                String item = "";
                double qty = 0;
                double baseqty = 0;
                String uom = "";
                String loc = "";
                String wh = "";
                String site = "";
                String serial = "";
                String expire = "";
                double sum = 0;
                int i = 0;



                  res = st.executeQuery("select sh_site, shd_item, shd_qty, shd_loc, shd_wh, shd_uom, shd_serial " +
                          " from ship_det inner join ship_mstr on sh_id = shd_id  " +
                          " where shd_id = " + "'" + shipper + "'" +";");


                while (res.next()) {

                    i = 0;
                    item = res.getString("shd_item");
                    qty = res.getDouble("shd_qty");
                    loc = res.getString("shd_loc");
                    wh = res.getString("shd_wh");
                    site = res.getString("sh_site");
                    uom = res.getString("shd_uom");
                    serial = res.getString("shd_serial");
                    baseqty = OVData.getUOMBaseQty(item, site, uom, qty);
                    // check for serialized inventory flag...if not...prevent serial from entry into in_mstr
                    if (! OVData.isInvCtrlSerialize()) {
                        serial = "";
                        expire = "";
                    }

                    // reverse quantity
                    baseqty = -1 * baseqty;

                    // lets determine if this is a legitimate item or a misc item...do not inventory misc items
                    res2 = st4.executeQuery("select it_item, it_loc, it_wh " +
                          " from  item_mstr  " +
                          " where it_item = " + "'" + item + "'" +";");

                    while (res2.next()) {
                        i++;
                        // if no loc in shipper then grab the item default loc
                        if (loc.isEmpty())
                           loc = res2.getString("it_loc");
                        // if no loc in shipper then grab the item default loc
                        if (wh.isEmpty())
                           wh = res2.getString("it_wh");
                    }
                    // if no item_mstr then continue loop...must be miscellaneous item
                    if (i == 0) {
                        continue;
                    }

                    

                    // check if in_mstr record exists for this part,loc,site combo
                    // if not add it
                    nres = st2.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"     
                            + ";");

                     int z = 0;
                     double qoh = 0.00;
                      while (nres.next()) {
                        z++;
                        qoh = bsParseDouble(nres.getString("in_qoh"));
                    }
                    nres.close();


                    if (z == 0) {
                     sum = (-1 * baseqty);
                     st3.executeUpdate("insert into in_mstr "
                            + "(in_site, in_item, in_loc, in_wh, in_serial, in_expire, in_qoh, in_date ) "
                            + " values ( " 
                            + "'" + site + "'" + ","
                            + "'" + item + "'" + ","
                            + "'" + loc + "'" + ","
                            + "'" + wh + "'" + ","
                            + "'" + serial + "'" + ","
                            + "'" + expire + "'" + ","        
                            + "'" + sum + "'" + ","
                            + "'" + mydate + "'"
                            + ")"
                            + ";");

                    }  else {
                       // nres.first();
                        sum = qoh - baseqty;
                         st3.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + sum + "'" + "," +
                              " in_date = " + "'" + mydate + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"    
                            + ";");
                    }

                }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
             myerror = true;
        } finally {
            if (res != null) res.close();
            if (res2 != null) res2.close();
            if (nres != null) nres.close();
            if (st != null) st.close();
            if (st2 != null) st2.close();
            if (st3 != null) st3.close();
            if (st4 != null) st4.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
        myerror = true;
    }
    return myerror;

     }

    public static boolean UpdateInventoryFromReceiver(String receiver) {
          boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            Statement st3 = con.createStatement();
            ResultSet res = null;
            ResultSet nres = null;
        try{
            

           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);


                String item = "";
                double qty = 0.00;
                double baseqty = 0.00;
                String loc = "";
                String wh = "";
                String site = "";
                String serial = "";
                String expire = mydate;
                double sum = 0.00;
                String uom = "";



                  res = st.executeQuery("select * from recv_det where rvd_id = " + "'" + receiver + "'" +";");
                while (res.next()) {
                    item = res.getString("rvd_item");
                    qty = res.getDouble("rvd_qty");
                    loc = res.getString("rvd_loc");
                    wh = res.getString("rvd_wh");
                    site = res.getString("rvd_site");
                    uom = res.getString("rvd_uom"); 
                    serial = res.getString("rvd_serial");
                    baseqty = OVData.getUOMBaseQty(item, site, uom, qty);

                    // check for serialized inventory flag...if not...prevent serial from entry into in_mstr
                    if (! OVData.isInvCtrlSerialize()) {
                        serial = "";
                        expire = "";
                    }
                    
                    // check if in_mstr record exists for this part,loc,site combo
                    // if not add it
                    nres = st2.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"      
                            + ";");
                     int z = 0;
                     double qoh = 0.00;
                      while (nres.next()) {
                        z++;
                        qoh = bsParseDouble(nres.getString("in_qoh"));
                    }
                    nres.close();


                    if (z == 0) {
                     sum = baseqty;
                     st3.executeUpdate("insert into in_mstr "
                            + "(in_site, in_item, in_loc, in_wh, in_serial, in_expire, in_qoh, in_date ) "
                            + " values ( " 
                            + "'" + site + "'" + ","
                            + "'" + item + "'" + ","
                            + "'" + loc + "'" + ","
                            + "'" + wh + "'" + ","
                            + "'" + serial + "'" + ","
                            + "'" + expire + "'" + ","        
                            + "'" + sum + "'" + ","
                            + "'" + mydate + "'"
                            + ")"
                            + ";");

                    }  else {
                       // nres.first();
                        sum = qoh + baseqty;
                         st3.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + sum + "'" + "," +
                              " in_date = " + "'" + mydate + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"      
                            + ";");
                    }


                }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
             myerror = true;
        } finally {
            if (res != null) res.close();
            if (nres != null) nres.close();
            if (st != null) st.close();
            if (st2 != null) st2.close();
            if (st3 != null) st3.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
        myerror = true;
    }
    return myerror;

     }
    
    public static void _updateNonSerializedInventory(Connection bscon, String item, String site, String wh, String loc, double baseqty, String date) throws SQLException {
       
            Statement st = bscon.createStatement();
            Statement st2 = bscon.createStatement();
            ResultSet res = null;
            int z = 0;
            double qoh = 0;
            double sum = 0;
          
                res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"    
                            + ";");

                    while (res.next()) {
                        z++;
                        qoh = bsParseDouble(res.getString("in_qoh"));
                    }
                    res.close();
                    st.close();
                    if (z == 0) {
                     st2.executeUpdate("insert into in_mstr "
                            + "(in_site, in_item, in_loc, in_wh, in_serial, in_expire, in_qoh, in_date ) "
                            + " values ( " 
                            + "'" + site + "'" + ","
                            + "'" + item + "'" + ","
                            + "'" + loc + "'" + ","
                            + "'" + wh + "'" + ","
                            + "''" + "," // serial = ''   
                            + "''" + ","   // expire = ''
                            + "'" + baseqty + "'" + ","
                            + "'" + date + "'"
                            + ")"
                            + ";");

                    }  else {
                         st2.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + (qoh + baseqty) + "'" + "," +
                              " in_date = " + "'" + date + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"        
                            + ";");
                    }
                    st2.close();
         
    }
    /* end of inventory related functions */


    /* start of production scheduling */

    public static int CommitSchedules(JTable mytable, String datesched) {
        int count = 0;
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();



        try {

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try {
            

            for (int i = 0 ; i < mytable.getRowCount(); i++) {
                count++;

                String status = mytable.getValueAt(i,12).toString();
                 if (status.equals(getGlobalProgTag("open"))) { status = "0"; }
                 if (status.equals(getGlobalProgTag("closed"))) { status = "1"; }
                 if (status.equals(getGlobalProgTag("void"))) { status = "-1"; }

            st.executeUpdate(" update plan_mstr " +
                   " set plan_cell = "  + "'" + mytable.getValueAt(i,5).toString() + "'" + ","
                   + " plan_qty_sched = " + "'" + mytable.getValueAt(i,6).toString() + "'" + ","
                    + " plan_status = " + "'" + status + "'" + ","
                    + " plan_is_sched = '1' " + ","
                    + " plan_date_sched = " + "'" + datesched + "'" 
                    + " where plan_nbr = " + "'" + mytable.getValueAt(i,0) + "'" + ";" );
       }      
          } catch (SQLException s) {
           MainFrame.bslog(s);
           count = 0;
        } finally {
            if (st != null) st.close();
            con.close();
        }
    } catch (Exception e) {
           MainFrame.bslog(e);
           count = 0;
    }
      return count;
   }

    /* end of production scheduling */

    /* start tran_mstr related functions */

    public static boolean TRHistRctPurch(String receiver, Date effdate) {
    boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet res = null;
        try{
            


           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);

                String vend = "";
                String ref = "";
                String rmks = "";
                String acct = "";
                String cc = "";
                String type = "";
                String jobnbr = "";
                String serial = "";
                String item = "";
                String uom = "";
                double qty = 0.00;
                double baseqty = 0.00;
                double price = 0.00;
                double cost = 0.00;
                String loc = "";
                int line = 0;
                String order = "";
                String po = "";
                String site = "";
                String lot = "";
                String terms = "";


                res = st.executeQuery("select * from recv_mstr where rv_id = " + "'" + receiver + "'" +";");
                while (res.next()) {
                 vend = res.getString("rv_vend");
                 ref = res.getString("rv_ref");
                 rmks = res.getString("rv_rmks");
                 acct = res.getString("rv_ap_acct");
                 cc = res.getString("rv_ap_cc");
                 site = res.getString("rv_site");
                 terms = res.getString("rv_terms");
                 type = "RCT-PURCH";
                }

                res = st.executeQuery("select * from recv_det left outer join item_cost on itc_set = 'standard' and itc_item = rvd_item where rvd_id = " + "'" + receiver + "'" +";");
                while (res.next()) {
                    item = res.getString("rvd_item");
                    qty = res.getDouble("rvd_qty");
                    order = res.getString("rvd_po");
                    po = res.getString("rvd_po");
                    line = res.getInt("rvd_poline");
                    lot = res.getString("rvd_lot");
                    loc = res.getString("rvd_loc");
                    jobnbr = res.getString("rvd_jobnbr");
                    serial = res.getString("rvd_serial");
                    price = res.getDouble("rvd_netprice");
                    cost = res.getDouble("itc_total");
                    uom = res.getString("rvd_uom");
                    baseqty = OVData.getUOMBaseQty(item, site, uom, qty);

            st2.executeUpdate("insert into tran_mstr "
                            + "(tr_site, tr_item, tr_qty, tr_base_qty, tr_uom, tr_ent_date, tr_eff_date, "
                            + " tr_userid, tr_ref, tr_addrcode, tr_type, tr_rmks, tr_nbr, "
                            + " tr_acct, tr_cc, tr_lot, tr_serial, tr_program, tr_loc, "
                            + " tr_order, tr_line, tr_po, tr_price, tr_cost, tr_terms ) "
                            + " values ( " 
                            + "'" + site + "'" + ","
                            + "'" + item + "'" + ","
                            + "'" + qty + "'" + ","
                            + "'" + baseqty + "'" + ","
                            + "'" + uom + "'" + ","        
                            + "'" + mydate + "'" + ","
                            + "'" + dfdate.format(effdate) + "'" + ","
                            + "'" + "" + "'" + ","
                            + "'" + ref + "'" + ","
                            + "'" + vend + "'" + ","
                            + "'" + type + "'" + ","
                            + "'" + rmks + "'" + ","
                            + "'" + receiver + "'" + ","
                            + "'" + acct + "'" + ","
                            + "'" + cc + "'" + ","
                            + "'" + lot + "'" + ","
                            + "'" + serial + "'" + ","
                            + "'" + "RecvMstr" + "'" + ","
                            + "'" + loc + "'" + ","
                            + "'" + order + "'" + ","
                            + "'" + line + "'" + ","
                            + "'" + po + "'" + ","
                            + "'" + price + "'" + ","
                            + "'" + cost + "'" + ","
                            + "'" + terms + "'"
                            + ")"
                            + ";");

           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
             myerror = true;
        } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (st2 != null) st2.close();
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
        myerror = true;
    }
    return myerror;

}

      
    public static void TRHistIssSalesPOS(String nbr, Date effdate, boolean isVoid, Connection bscon) throws SQLException {
        
                Statement st = bscon.createStatement();
                Statement st2 = bscon.createStatement();
                ResultSet res;
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                    String cust = "";
                    String ref = nbr;
                    String rmks = "POS Sales";
                    String acct = "";
                    String cc = "";
                    String type = "ISS-SALES";
                    String jobnbr = "";
                    String serial = "";
                    String item = "";
                    double qty = 0;
                    double baseqty = 0;  // UOM conversion not applicable 
                    double price = 0.00;
                    double cost = 0.00;
                    String loc = "";
                    int line = 0;
                    String order = "POS";
                    String po = "";
                    String site = OVData.getDefaultSite();
                    String lot = "";
                    String terms = "";
                    String uom = "";
                   
                    res = st.executeQuery("select * from pos_det where posd_nbr = " + "'" + nbr + "'" +";");
                    while (res.next()) {
                        item = res.getString("posd_item");
                        if (isVoid) {
                        qty = (-1 * res.getDouble("posd_qty"));
                        } else {
                        qty = res.getDouble("posd_qty");    
                        }
                        baseqty = qty;
                        uom = OVData.getUOMFromItemSite(item, site);  //always base uom for POS program
                        
                st2.executeUpdate("insert into tran_mstr "
                                + "(tr_site, tr_item, tr_qty, tr_base_qty, tr_uom, tr_ent_date, tr_eff_date, "
                                + " tr_userid, tr_ref, tr_addrcode, tr_type, tr_rmks, tr_nbr, "
                                + " tr_acct, tr_cc, tr_lot, tr_serial, tr_program, tr_loc, "
                                + " tr_order, tr_line, tr_po, tr_price, tr_cost, tr_terms ) "
                                + " values ( " 
                                + "'" + site + "'" + ","
                                + "'" + item + "'" + ","
                                + "'" + qty + "'" + ","
                                + "'" + baseqty + "'" + ","
                                + "'" + uom + "'" + ","        
                                + "'" + mydate + "'" + ","
                                + "'" + dfdate.format(effdate) + "'" + ","
                                + "'" + bsmf.MainFrame.userid.toString() + "'" + ","
                                + "'" + ref + "'" + ","
                                + "'" + cust + "'" + ","
                                + "'" + type + "'" + ","
                                + "'" + rmks + "'" + ","
                                + "'" + nbr + "'" + ","
                                + "'" + acct + "'" + ","
                                + "'" + cc + "'" + ","
                                + "'" + lot + "'" + ","
                                + "'" + serial + "'" + ","
                                + "'" + "POSMaint" + "'" + ","
                                + "'" + loc + "'" + ","
                                + "'" + order + "'" + ","
                                + "'" + line + "'" + ","
                                + "'" + po + "'" + ","
                                + "'" + price + "'" + ","
                                + "'" + cost + "'" + ","
                                + "'" + terms + "'"
                                + ")"
                                + ";");
               }
                   
                    res.close();
                    st.close();
                    if (st2 != null) {
                    st2.close();
                    }
    }
    
    public static boolean TRHistIssSalesRV(String shipper, Date effdate) {
        boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                Statement st2 = con.createStatement();
                ResultSet res = null;
            try{
                
                
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                    String cust = "";
                    String ref = "";
                    String rmks = "";
                    String acct = "";
                    String cc = "";
                    String type = "";
                    String jobnbr = "";
                    String serial = "";
                    String item = "";
                    String uom = "";
                    double qty = 0.0;
                    double baseqty = 0.0;
                    double price = 0.00;
                    double cost = 0.00;
                    String loc = "";
                    int line = 0;
                    String order = "";
                    String po = "";
                    String site = "";
                    String lot = "";
                    String terms = "";
                    
                    
                    res = st.executeQuery("select * from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                     cust = res.getString("sh_cust");
                     ref = res.getString("sh_ref");
                     rmks = res.getString("sh_rmks");
                     acct = res.getString("sh_ar_acct");
                     cc = res.getString("sh_ar_cc");
                     site = res.getString("sh_site");
                     terms = res.getString("sh_cust_terms");
                     type = "ISS-SALES";
                    }
                   
                    res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                        item = res.getString("shd_item");
                        uom = res.getString("shd_uom");
                        qty = res.getDouble("shd_qty");
                        order = res.getString("shd_so");
                        po = res.getString("shd_po");
                        line = res.getInt("shd_soline");
                        lot = res.getString("shd_lot");
                        loc = res.getString("shd_loc");
                        jobnbr = res.getString("shd_jobnbr");
                        serial = res.getString("shd_serial");
                        // reverse qty
                        baseqty = -1 * OVData.getUOMBaseQty(item, site, uom, qty);
                        qty = -1 * qty;
                        
                        
                st2.executeUpdate("insert into tran_mstr "
                                + "(tr_site, tr_item, tr_qty, tr_base_qty, tr_uom, tr_ent_date, tr_eff_date, "
                                + " tr_userid, tr_ref, tr_addrcode, tr_type, tr_rmks, tr_nbr, "
                                + " tr_acct, tr_cc, tr_lot, tr_serial, tr_program, tr_loc, "
                                + " tr_order, tr_line, tr_po, tr_price, tr_cost, tr_terms ) "
                                + " values ( " 
                                + "'" + site + "'" + ","
                                + "'" + item + "'" + ","
                                + "'" + qty + "'" + ","
                                + "'" + baseqty + "'" + ","
                                + "'" + uom + "'" + ","        
                                + "'" + mydate + "'" + ","
                                + "'" + dfdate.format(effdate) + "'" + ","
                                + "'" + bsmf.MainFrame.userid.toString() + "'" + ","
                                + "'" + ref + "'" + ","
                                + "'" + cust + "'" + ","
                                + "'" + type + "'" + ","
                                + "'" + rmks + "'" + ","
                                + "'" + shipper + "'" + ","
                                + "'" + acct + "'" + ","
                                + "'" + cc + "'" + ","
                                + "'" + lot + "'" + ","
                                + "'" + serial + "'" + ","
                                + "'" + "void" + "'" + ","
                                + "'" + loc + "'" + ","
                                + "'" + order + "'" + ","
                                + "'" + line + "'" + ","
                                + "'" + po + "'" + ","
                                + "'" + price + "'" + ","
                                + "'" + cost + "'" + ","
                                + "'" + terms + "'"
                                + ")"
                                + ";");
                
               }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            } finally {
            if (res != null) res.close();
            if (st != null) st.close();
            if (st2 != null) st2.close();
            con.close();
        }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
    }
      
      
    public static boolean loadTranHistByTable(JTable mytable) {
          
          /*
          Field count must be 20 fields...and must be in this exact order:
          0=part
          1=type 
          2=operation
          3=qty
          4=effdate
          5=location
          6=serialno
          7=reference
          8=site
          9=userid
          10=prodline
          11=AssyCell
          12=remarks
          13=PackCell
          14=packdate
          15=assydate
          16=expiredate
          17=program
          18=warehouse
          19=bom
          
          */
          
          if (mytable == null || mytable.getRowCount() == 0) {
              return false;
          }
          
          boolean didLoad = false;
          String mytrkey = "";
          boolean islastop = false;
          String temptype = "";
       
          java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                String today = dfdate.format(now);
          
          
          // try block for updating tran_mstr
       try {
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                String _part = "";
                String _type = "";
                String _op = "";
                Double _qty = 0.00;
                String _date = "";
                String _loc = "";
                String _wh = "";
                String _serial = "";
                String _ref = "";
                String _site = "";
                String _userid = "";
                String _prodline = "";
                String _assycell = "";
                String _remarks = "";
                String _packcell = "";
                String _packdate = "";
                String _assydate = "";
                String _expiredate = "";
                String _program = "";
                String _bom = "";
                
                // added later
                Double _baseqty = 0.00;
                String _uom = "";
             
          for (int i = 0; i < mytable.getRowCount(); i++) {
          
              _part = mytable.getValueAt(i, 0).toString();
              _type = mytable.getValueAt(i, 1).toString();
              _op = mytable.getValueAt(i, 2).toString();
              _qty = bsParseDouble(mytable.getValueAt(i, 3).toString());
              _date = mytable.getValueAt(i, 4).toString();
              _loc = mytable.getValueAt(i, 5).toString();
              _wh = mytable.getValueAt(i, 18).toString();
              _serial = mytable.getValueAt(i, 6).toString();
              _ref = mytable.getValueAt(i, 7).toString();
              _site = mytable.getValueAt(i, 8).toString();
              _userid = mytable.getValueAt(i, 9).toString();
              _prodline = mytable.getValueAt(i, 10).toString();
              _assycell = mytable.getValueAt(i, 11).toString();
              _remarks = mytable.getValueAt(i, 12).toString();
              _packcell = mytable.getValueAt(i, 13).toString();
               _packdate = mytable.getValueAt(i, 14).toString();
               _assydate = mytable.getValueAt(i, 15).toString();
               _expiredate = mytable.getValueAt(i, 16).toString();
               _program = mytable.getValueAt(i,17).toString();
               _bom = mytable.getValueAt(i,19).toString();
               // defaults to baseqty
               _baseqty = _qty;
               _uom = OVData.getUOMFromItemSite(_part, _site);
              
              if (_remarks.length() > 200) {
                  _remarks = _remarks.substring(1,200);
              }
              if (_program.length() > 50) {
                  _program = _program.substring(1,50);
              }
              
              if (_packdate.isEmpty())
               _packdate = null;
           else
               _packdate = "'" + _packdate + "'";
              
              if (_assydate.isEmpty())
               _assydate = null;
           else
               _assydate = "'" + _assydate + "'";
              
              islastop = OVData.isLastOperation(_part, _op);
              double opcost = invData.getItemCostUpToOp(_part, "standard", _site, _op) ;
              if (invData.getItemCode(_part).toString().equals("P")) {
                  opcost = invData.getItemCost(_part, "standard", _site);
              }
              
                          
            if (_bom.isEmpty()) {
                _bom = OVData.getDefaultBomID(_part);
            }  
              /* now lets filter the type and hit the appropriate conditional followup */
           if (_type == "ISS-WIP") {
                  /* let's first load the tran_hist */
                  
                  // if lastop convert type to RCT-FG else leave type as iss-wip
                 if (islastop) {
                     temptype = "RCT-FG";
                     _wh = OVData.getWarehouseByItem(_part);
                     _loc = OVData.getLocationByItem(_part);
                 } else {
                     temptype = _type;
                 }
                 
              if (dbtype.equals("sqlite")) {    
              st.executeUpdate("insert into tran_mstr "
                        + "(tr_item, tr_type, tr_op, tr_qty, tr_base_qty, tr_uom, tr_cost, tr_eff_date, tr_loc, tr_wh, "
                        + "tr_serial, tr_ref, tr_site, tr_userid, tr_prodline, tr_export, tr_expire, tr_actcell, tr_rmks, tr_pack, tr_assy_date, tr_pack_date, tr_ent_date, tr_program, tr_bom )"
                        + " values ( " + "'" + _part + "'" + ","
                        + "'" + temptype + "'" + ","
                        + "'" + _op + "'" + ","
                        + "'" + _qty + "'" + ","
                        + "'" + _baseqty + "'" + ","
                        + "'" + _uom + "'" + ","        
                        + "'" + opcost + "'" + ","
                        + "'" + _date + "'" + ","
                        + "'" + _loc + "'" + ","
                        + "'" + _wh + "'" + ","
                        + "'" + _serial + "'" + ","
                        + "'" + _ref + "'" + ","
                        + "'" + _site + "'" + ","
                        + "'" + _userid + "'" + ","
                        + "'" + _prodline + "'" + ","
                        + "'0'" + ","
                        + "'" + _expiredate + "'" + ","
                        + "'" + _assycell + "'" + ","
                        + "'" + _remarks + "'" + ","
                        + "'" + _packcell + "'" + ","
                        +  _assydate + ","
                        +  _packdate + ","
                        + "'" + today + "'" + ","
                      + "'" + _program + "'" + ","
                      + "'" + _bom + "'"        
                        + ")"
                        + ";");
              } else {
                  st.executeUpdate("insert into tran_mstr "
                        + "(tr_item, tr_type, tr_op, tr_qty, tr_base_qty, tr_uom, tr_cost, tr_eff_date, tr_loc, tr_wh, "
                        + "tr_serial, tr_ref, tr_site, tr_userid, tr_prodline, tr_export, tr_expire, tr_actcell, tr_rmks, tr_pack, tr_assy_date, tr_pack_date, tr_ent_date, tr_program, tr_bom )"
                        + " values ( " + "'" + _part + "'" + ","
                        + "'" + temptype + "'" + ","
                        + "'" + _op + "'" + ","
                        + "'" + _qty + "'" + ","
                        + "'" + _baseqty + "'" + ","
                        + "'" + _uom + "'" + ","        
                        + "'" + opcost + "'" + ","
                        + "'" + _date + "'" + ","
                        + "'" + _loc + "'" + ","
                        + "'" + _wh + "'" + ","
                        + "'" + _serial + "'" + ","
                        + "'" + _ref + "'" + ","
                        + "'" + _site + "'" + ","
                        + "'" + _userid + "'" + ","
                        + "'" + _prodline + "'" + ","
                        + "'0'" + ","
                        + "'" + _expiredate + "'" + ","
                        + "'" + _assycell + "'" + ","         
                        + "'" + _remarks + "'" + ","
                        + "'" + _packcell + "'" + ","
                        +  _assydate + ","
                        +  _packdate + ","
                        + "'" + today + "'" + ","
                        + "'" + _program + "'" + ","
                        + "'" + _bom + "'"
                        + ")"
                        + ";", Statement.RETURN_GENERATED_KEYS);
              }
              ResultSet res = st.getGeneratedKeys();
               while (res.next()) {
                    mytrkey = String.valueOf(res.getInt(1));
                }
              res.close();
                  /* we need to consume material component inventory
                   and gl cost of this item through all unreported operations since last 
                  reported Operation */
                  wip_iss_mtl_gl(_part, _op, _site, _qty, _date, mytrkey, _type, mytrkey, _serial, _userid, _program, _bom);
                  
                  wip_iss_op_cost_gl(_part, _op, _site, _qty, _date, mytrkey, _type, mytrkey);
                  
                  /* if this is last op... */
                  /* adjust inventory for this part FG being produced ...if last OP */
                  if (islastop) {
                     OVData.UpdateInventoryDiscrete(_part, _site, _loc, _wh, _serial, _expiredate, _qty);
                     double cost = (invData.getItemCost(_part, "standard", _site) * _qty);
                     OVData.wip_to_fg(_part, _site, cost, _date, mytrkey, "RCT-FG", _remarks);
                  }
              }
           
           
           
           
           
            if (_type == "ISS-SCRAP") {
                  /* let's first load the tran_hist */
              if (dbtype.equals("sqlite")) {         
              st.executeUpdate("insert into tran_mstr "
                        + "(tr_item, tr_type, tr_op, tr_qty, tr_base_qty, tr_uom, tr_cost, tr_eff_date, tr_loc, "
                        + "tr_serial, tr_ref, tr_site, tr_userid, tr_prodline, tr_export, tr_actcell, tr_rmks, tr_pack, tr_assy_date, tr_pack_date, tr_ent_date, tr_program, tr_bom )"
                        + " values ( " + "'" + _part + "'" + ","
                        + "'" + _type + "'" + ","
                        + "'" + _op + "'" + ","
                        + "'" + _qty + "'" + ","
                        + "'" + _baseqty + "'" + ","
                        + "'" + _uom + "'" + ","                
                        + "'" + opcost + "'" + ","
                        + "'" + _date + "'" + ","
                        + "'" + _loc + "'" + ","
                        + "'" + _serial + "'" + ","
                        + "'" + _ref + "'" + ","
                        + "'" + _site + "'" + ","
                        + "'" + _userid + "'" + ","
                        + "'" + _prodline + "'" + ","
                        + "'0'" + ","
                        + "'" + _assycell + "'" + ","
                        + "'" + _remarks + "'" + ","
                        + "'" + _packcell + "'" + ","
                        +  _assydate  + ","
                        +  _packdate  + ","
                        + "'" + today + "'" + ","
                        + "'" + _program + "'" + ","
                        + "'" + _bom + "'"
                        + ")"
                        + ";");
              } else {
                 st.executeUpdate("insert into tran_mstr "
                        + "(tr_item, tr_type, tr_op, tr_qty, tr_base_qty, tr_uom, tr_cost, tr_eff_date, tr_loc, "
                        + "tr_serial, tr_ref, tr_site, tr_userid, tr_prodline, tr_export, tr_actcell, tr_rmks, tr_pack, tr_assy_date, tr_pack_date, tr_ent_date, tr_program, tr_bom )"
                        + " values ( " + "'" + _part + "'" + ","
                        + "'" + _type + "'" + ","
                        + "'" + _op + "'" + ","
                        + "'" + _qty + "'" + ","
                        + "'" + _baseqty + "'" + ","
                        + "'" + _uom + "'" + ","                
                        + "'" + opcost + "'" + ","
                        + "'" + _date + "'" + ","
                        + "'" + _loc + "'" + ","
                        + "'" + _serial + "'" + ","
                        + "'" + _ref + "'" + ","
                        + "'" + _site + "'" + ","
                        + "'" + _userid + "'" + ","
                        + "'" + _prodline + "'" + ","
                        + "'0'" + ","
                        + "'" + _assycell + "'" + ","
                        + "'" + _remarks + "'" + ","
                        + "'" + _packcell + "'" + ","
                        +  _assydate  + ","
                        +  _packdate  + ","
                        + "'" + today + "'" + ","
                        + "'" + _program + "'" + ","
                        + "'" + _bom + "'"
                        + ")"
                        + ";", Statement.RETURN_GENERATED_KEYS); 
              }
              ResultSet res = st.getGeneratedKeys();
               while (res.next()) {
                    mytrkey = String.valueOf(res.getInt(1));
                }
                res.close();
                  /* we need to consume material component inventory
                   and gl cost of this item through all unreported operations since last 
                  reported Operation */
                  wip_iss_mtl_gl(_part, _op, _site, _qty, _date, mytrkey, _type, mytrkey, _serial, _userid, _program, _bom);
                  wip_iss_op_cost_gl(_part, _op, _site, _qty, _date, mytrkey, _type, mytrkey);
                  
                  
              }
            
            if (_type == "ISS-REWK") {
                  /* let's first load the tran_hist */
                  
              st.executeUpdate("insert into tran_mstr "
                        + "(tr_item, tr_type, tr_op, tr_qty, tr_base_qty, tr_uom, tr_cost, tr_eff_date, tr_loc, "
                        + "tr_serial, tr_ref, tr_site, tr_userid, tr_prodline, tr_export, tr_actcell, tr_rmks, tr_pack, tr_assy_date, tr_pack_date, tr_ent_date, tr_program, tr_bom )"
                        + " values ( " + "'" + _part + "'" + ","
                        + "'" + _type + "'" + ","
                        + "'" + _op + "'" + ","
                        + "'" + opcost + "'" + ","
                        + "'" + _qty + "'" + ","
                        + "'" + _baseqty + "'" + ","
                        + "'" + _uom + "'" + ","                
                        + "'" + _date + "'" + ","
                        + "'" + _loc + "'" + ","
                        + "'" + _serial + "'" + ","
                        + "'" + _ref + "'" + ","
                        + "'" + _site + "'" + ","
                        + "'" + _userid + "'" + ","
                        + "'" + _prodline + "'" + ","
                        + "'1'" + ","
                        + "'" + _assycell + "'" + ","
                        + "'" + _remarks + "'" + ","
                        + "'" + _packcell + "'" + ","
                        +  _assydate  + ","
                        +  _packdate  + ","
                        + "'" + today + "'" + ","
                        + "'" + _program + "'" + ","
                        + "'" + _bom + "'"
                        + ")"
                        + ";", Statement.RETURN_GENERATED_KEYS);
              ResultSet res = st.getGeneratedKeys();
               while (res.next()) {
                    mytrkey = String.valueOf(res.getInt(1));
                }
              res.close();
                  /* we need to consume material component inventory
                   and gl cost of this item through all unreported operations since last 
                  reported Operation */
                  wip_iss_mtl_gl(_part, _op, _site, _qty, _date, mytrkey, _type, mytrkey, _serial, _userid, _program, _bom);
                  wip_iss_op_cost_gl(_part, _op, _site, _qty, _date, mytrkey, _type, mytrkey);
                  
                  
              }
              
              
          }
          didLoad = true;
          
             } catch (SQLException s) {
                // con.rollback();
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
            if (st != null) st.close();
            con.close();
        }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
       
          
          return didLoad;
      }
      
    public static boolean TRHistIssDiscrete(Date effdate, String item, double qty, String op, String type, double price, double cost, String site, 
              String loc, String wh, String expire, String cust, String nbr, String order, int line, String po, String terms, String lot, String rmks, 
              String ref, String acct, String cc, String jobnbr, String serial, String program, String userid) {
        boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try{
                
                
                // NOTE:  all inventory transactions done at base uom level
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                String uom = OVData.getUOMFromItemSite(item, site);
                                           
                st.executeUpdate("insert into tran_mstr "
                                + "(tr_site, tr_item, tr_qty, tr_base_qty, tr_uom, tr_op, tr_ent_date, tr_eff_date, "
                                + " tr_userid, tr_ref, tr_addrcode, tr_type, tr_rmks, tr_nbr, "
                                + " tr_acct, tr_cc, tr_lot, tr_serial, tr_program, tr_loc, tr_wh, tr_expire, "
                                + " tr_order, tr_line, tr_po, tr_price, tr_cost, tr_terms ) "
                                + " values ( " 
                                + "'" + site + "'" + ","
                                + "'" + item + "'" + ","
                                + "'" + qty + "'" + ","
                                + "'" + qty + "'" + "," // baseqty = qty for inventory movement
                                + "'" + uom + "'" + "," // always base uom for inventory movement 
                                + "'" + op + "'" + ","
                                + "'" + mydate + "'" + ","
                                + "'" + dfdate.format(effdate) + "'" + ","
                                + "'" + userid + "'" + ","
                                + "'" + ref + "'" + ","
                                + "'" + cust + "'" + ","
                                + "'" + type + "'" + ","
                                + "'" + rmks + "'" + ","
                                + "'" + nbr + "'" + ","
                                + "'" + acct + "'" + ","
                                + "'" + cc + "'" + ","
                                + "'" + lot + "'" + ","
                                + "'" + serial + "'" + ","
                                + "'" + program + "'" + ","
                                + "'" + loc + "'" + ","
                                + "'" + wh + "'" + ","
                                + "'" + expire + "'" + ","        
                                + "'" + order + "'" + ","
                                + "'" + line + "'" + ","
                                + "'" + po + "'" + ","
                                + "'" + price + "'" + ","
                                + "'" + cost + "'" + ","
                                + "'" + terms + "'"
                                + ")"
                                + ";");
                
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
    }
    
      /* end tran_mstr related functions */
     
      /* start gl related functions */
   
    public static boolean isValidTerms(String code) {
       boolean myreturn = false;
        try{
           
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
 
                res = st.executeQuery("select cut_code from cust_term where cut_code = " + "'" + code + "'" + ";");
               while (res.next()) {
                   myreturn = true;
               }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myreturn;
        
    }
       
    public static boolean isDuplicateNavCode(String code, String callingmenu) {
   boolean myreturn = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select menu_navcode from menu_mstr where menu_id <> " + "'" + callingmenu + "'" +
                    " and menu_navcode = " + "'" + code + "'" + ";");
           while (res.next()) {
               myreturn = true;
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static boolean isValidProfile(String code) {
   boolean myreturn = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select payp_code from pay_profile where payp_code = " + "'" + code + "'" + ";");
           while (res.next()) {
               myreturn = true;
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}   

    public static boolean isCurrSameAsDefault(String curr) {
   boolean myreturn = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select ov_currency from ov_mstr;");
           while (res.next()) {
               if (res.getString("ov_currency").toUpperCase().equals(curr.toUpperCase())) {
                myreturn = true;
               }
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static boolean isValidBank(String code) {
   boolean myreturn = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select bk_id from bk_mstr where bk_id = " + "'" + code + "'" + ";");
           while (res.next()) {
               myreturn = true;
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static boolean isValidCurrency(String code) {
   boolean myreturn = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select cur_id from cur_mstr where cur_id = " + "'" + code + "'" + ";");
           while (res.next()) {
               myreturn = true;
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static boolean isValidGLAcct(String acct) {
   boolean myreturn = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select ac_id from ac_mstr where ac_id = " + "'" + acct + "'" + ";");
           while (res.next()) {
               myreturn = true;
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static boolean isValidGLcc(String cc) {
   boolean myreturn = false;
    try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select dept_id from dept_mstr where dept_id = " + "'" + cc + "'" + ";");
           while (res.next()) {
               myreturn = true;
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}
   public static boolean isGLPeriodClosed(String EffDate) {
          // function returns a 5 items from the gl_cal record where a date matches
          // first element = year  as int
          // second element = period as int
          // third element = startdate as string
          // fourth element = enddate as string
          // fifth element = status as string

     boolean isclosed = false;
    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select * from gl_cal where glc_start <= " +
                    "'" + EffDate.toString() + "'" + 
                    " AND glc_end >= " +
                    "'" + EffDate.toString() + "'" + ";");
           while (res.next()) {
               if (res.getString("glc_status").equals(getGlobalProgTag("closed"))) {
                   isclosed = true;
               }
           }

       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return isclosed;

}

    /* end gl related functions */

       
   
    
       /* end ap related functions */
       
       
       
    /* start ar related functions */
    public static Date getDueDateFromTerms(Date effdate, String terms) {
           Date duedate = new Date();
           duedate = null;
           
           try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select * from cust_term where cut_code = " + "'" + terms + "'" + " ;");
               while (res.next()) {
                    duedate = DateUtils.addDays(effdate,res.getInt("cut_days"));
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
            if (res != null) {
                res.close();
            }
            if (st != null) {
                st.close();
            }
            con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
                   
           return duedate;           
       }
    
    public static void AREntry(String type, String shipper, Date effdate, Connection bscon) throws SQLException {
           
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();
            Statement st = bscon.createStatement();
            ResultSet res;
                    String cust = "";
                    String ref = "";
                    String rmks = "";
                    String acct = "";
                    String cc = "";
                    String terms = "";
                    String site = "";
                    String taxcode = "";
                    String curr = "";
                    String basecurr = "";
                    String bank = "";
                    Date duedate = new Date();
                    double amt = 0.00;
                    double baseamt = 0.00;
                    double taxamt = 0.00;
                    double basetaxamt = 0.00;
                    double matltax = 0.00;
                   
                    res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                    amt += (res.getDouble("shd_qty") * res.getDouble("shd_netprice"));
                    matltax += OVData.getTaxAmtApplicableByItem(res.getString("shd_item"),res.getDouble("shd_qty") * res.getDouble("shd_netprice")); // line level matl tax
                    }
                    res.close();
                    
                    taxamt = matltax;
                    // lets retrieve any summary charges from orders associated with this shipment.
                    res = st.executeQuery("select * from shs_det where shs_nbr = " + "'" + shipper + "'" + 
                            " and shs_type = 'charge' " +               
                            ";");
                    while (res.next()) {
                    amt += res.getDouble("shs_amt");
                    }
                    res.close();
                    
                    res = st.executeQuery("select * from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                     cust = res.getString("sh_cust");
                     ref = res.getString("sh_ref");
                     rmks = res.getString("sh_rmks");
                     acct = res.getString("sh_ar_acct");
                     cc = res.getString("sh_ar_cc");
                     terms = res.getString("sh_cust_terms");
                     taxcode = res.getString("sh_taxcode");
                     site = res.getString("sh_site");
                     curr = res.getString("sh_curr");
                    }
                    res.close();
                    
                    // line matl tax versus order level tax....material Tax will be at line level..
                    //..summary tax will ONLY be at summary level...it will not be baked into line level
                    // ...summary level tax cannot be reported at line level
                    taxamt += shpData.getTaxAmtApplicableByShipper(shipper, amt); 
                    duedate = getDueDateFromTerms(effdate, terms);
                    
                    bank = OVData.getBankCodeOfCust(cust);
                    if (bank.isEmpty()) {
                        bank = OVData.getDefaultARBank();
                    }
                    
                    
                    // let's handle the currency exchange...if any
                    basecurr = OVData.getDefaultCurrency();
                    
                    
                    if (curr.toUpperCase().equals(basecurr.toUpperCase())) {
                        baseamt = amt;
                        basetaxamt = taxamt;
                    } else {
                        baseamt = OVData.getExchangeBaseValue(basecurr, curr, amt);
                        basetaxamt = OVData.getExchangeBaseValue(basecurr, curr, taxamt);
                    }
                    if (type.equals("I")) {
                         st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_base_amt, ar_curr, ar_base_curr, ar_amt_tax, ar_base_amt_tax, ar_open_amt, ar_type, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_duedate, ar_acct, ar_cc, "
                        + "ar_terms, ar_tax_code, ar_bank, ar_site, ar_status) "
                        + " values ( " + "'" + cust + "'" + ","
                        + "'" + shipper + "'" + ","
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(baseamt + basetaxamt) + "'" + ","   
                        + "'" + curr + "'" + ","   
                        + "'" + basecurr + "'" + ","        
                        + "'" + currformatDoubleUS(taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(basetaxamt) + "'" + ","        
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + "," // open_amount.....should this be base or foreign ?  currently it's foreign
                        + "'" + "I" + "'" + ","
                        + "'" + ref + "'" + ","
                        + "'" + rmks + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + dfdate.format(duedate) + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + taxcode + "'" + ","
                        + "'" + bank + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + "o" + "'" 
                        + ")"
                        + ";");
                    if (taxamt > 0) {
                      ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(taxcode);
                          for (String[] elements : taxelements) {
                                  st.executeUpdate("insert into art_tax "
                                + "(art_nbr, art_desc, art_type, art_amt, art_percent ) "
                                + " values ( " + "'" + shipper + "'" + ","
                                + "'" + elements[0] + "'" + ","
                                + "'" + elements[2] + "'" + ","
                                + "'" + currformatDoubleUS(amt * ( bsParseDoubleUS(elements[1]) / 100 )) + "'" + ","   // amount is currently 'foreign' ...not base
                                + "'" + elements[1] + "'" 
                                + ")"
                                + ";");
                          }
                    }
                    // now matl tax at the item level
                    if (matltax > 0) {
                         st.executeUpdate("insert into art_tax "
                                + "(art_nbr, art_desc, art_type, art_amt, art_percent ) "
                                + " values ( " + "'" + shipper + "'" + ","
                                + "'" + "MATERIAL TAX" + "'" + ","
                                + "'" + "MATERIAL" + "'" + ","
                                + "'" + matltax + "'" + ","   // amount is currently 'foreign' ...not base
                                + "'" + "0" + "'" 
                                + ")"
                                + ";"); 
                    }
                    
                  } // if type = "I"
                    
                    
                   if (type.equals("P")) {
                       String arnbr = (String.valueOf(OVData.getNextNbr("ar")));
                         st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_base_amt, ar_curr, ar_base_curr, ar_amt_tax, ar_base_amt_tax, ar_open_amt, ar_type, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_duedate, ar_paiddate, ar_acct, ar_cc, "
                        + "ar_terms, ar_tax_code, ar_bank, ar_site, ar_status) "
                        + " values ( " + "'" + cust + "'" + ","
                        + "'" + arnbr + "'" + ","
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(baseamt + basetaxamt) + "'" + ","   
                        + "'" + curr + "'" + ","   
                        + "'" + basecurr + "'" + ","        
                        + "'" + currformatDoubleUS(taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(basetaxamt) + "'" + ","        
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + "," // open_amount.....should this be base or foreign ?  currently it's foreign
                        + "'" + "P" + "'" + ","
                        + "'" + "cash" + "'" + ","
                        + "'" + rmks + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","        
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + taxcode + "'" + ","
                        + "'" + bank + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + "c" + "'" 
                        + ")"
                        + ";");
                         
                      st.executeUpdate("insert into ard_mstr "
                            + "(ard_id, ard_cust, ard_ref, ard_line, ard_date, ard_amt, ard_amt_tax, ard_base_amt, ard_base_amt_tax, ard_curr, ard_base_curr, ard_acct, ard_cc ) "
                            + " values ( " + "'" + arnbr + "'" + ","
                                + "'" + cust + "'" + ","
                            + "'" + shipper + "'" + ","
                            + "'" + "1" + "'" + ","
                            + "'" + dfdate.format(effdate) + "'" + ","
                            + "'" + currformatDoubleUS(amt) + "'"  + ","
                            + "'" + currformatDoubleUS(taxamt) + "'"  + ","
                            + "'" + currformatDoubleUS(baseamt) + "'"  + ","                
                            + "'" + currformatDoubleUS(basetaxamt) + "'" + "," 
                            + "'" + curr + "'"  + ","
                            + "'" + basecurr + "'" + ","
                            + "'" + acct + "'" + ","
                            + "'" + cc + "'"  
                            + ")"
                            + ";");    
                      
                      // update AR entry for original invoices with status and open amt  
                      ARUpdate(arnbr);  
                      // execute glentry
                      fglData.glEntryFromARPayment(arnbr,effdate);
                     
                  } // if type = "P"
          
                res.close();
                st.close();
    }
    
    
    public static String[] AREntry(String type, String shipper, Date effdate) {
            String[] m = new String[]{"",""};
            
            boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
            
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();
            
           
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
                                
                    String cust = "";
                    String ref = "";
                    String rmks = "";
                    String acct = "";
                    String cc = "";
                    String terms = "";
                    String site = "";
                    String taxcode = "";
                    String curr = "";
                    String basecurr = "";
                    String bank = "";
                    
                    Date duedate = new Date();
                            
                    double amt = 0.00;
                    double baseamt = 0.00;
                    double taxamt = 0.00;
                    double basetaxamt = 0.00;
                    
                    double matltax = 0.00;
                   
                   
                    res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                    amt += (res.getDouble("shd_qty") * res.getDouble("shd_netprice"));
                    matltax += OVData.getTaxAmtApplicableByItem(res.getString("shd_item"),res.getDouble("shd_qty") * res.getDouble("shd_netprice")); // line level matl tax
                    }
                    res.close();
                    

                    // lets retrieve any summary charges from orders associated with this shipment.
                    res = st.executeQuery("select * from shs_det where shs_nbr = " + "'" + shipper + "'" + 
                            " and shs_type = 'charge' " +               
                            ";");
                    while (res.next()) {
                    amt += res.getDouble("shs_amt");
                    }
                    res.close();
                    
                   
                    
                    
                    res = st.executeQuery("select * from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                     cust = res.getString("sh_cust");
                     ref = res.getString("sh_ref");
                     rmks = res.getString("sh_rmks");
                     acct = res.getString("sh_ar_acct");
                     cc = res.getString("sh_ar_cc");
                     terms = res.getString("sh_cust_terms");
                     taxcode = res.getString("sh_taxcode");
                     site = res.getString("sh_site");
                     curr = res.getString("sh_curr");
                    }
                    res.close();
                    
                   
                    
                    
                    
                    
                    // line matl tax versus order level tax....material Tax will be at line level..
                    //..summary tax will ONLY be at summary level...it will not be baked into line level
                    // ...summary level tax cannot be reported at line level
                    taxamt += shpData.getTaxAmtApplicableByShipper(shipper, amt);  
                  
                    
                    
                    
                    duedate = getDueDateFromTerms(effdate, terms);
                    if (duedate == null) {
                        myerror = true;
                    }
                    
                    bank = OVData.getBankCodeOfCust(cust);
                    if (bank.isEmpty()) {
                        bank = OVData.getDefaultARBank();
                    }
                    
                    
                    // let's handle the currency exchange...if any
                    basecurr = OVData.getDefaultCurrency();
                    
                    
                    if (curr.toUpperCase().equals(basecurr.toUpperCase())) {
                        baseamt = amt;
                        basetaxamt = taxamt;
                    } else {
                        baseamt = OVData.getExchangeBaseValue(basecurr, curr, amt);
                        basetaxamt = OVData.getExchangeBaseValue(basecurr, curr, taxamt);
                    }
                    
                  
                    
                    if (type.equals("I")) {
                         st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_base_amt, ar_curr, ar_base_curr, ar_amt_tax, ar_base_amt_tax, ar_open_amt, ar_type, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_duedate, ar_acct, ar_cc, "
                        + "ar_terms, ar_tax_code, ar_bank, ar_site, ar_status) "
                        + " values ( " + "'" + cust + "'" + ","
                        + "'" + shipper + "'" + ","
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(baseamt + basetaxamt) + "'" + ","   
                        + "'" + curr + "'" + ","   
                        + "'" + basecurr + "'" + ","        
                        + "'" + currformatDoubleUS(taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(basetaxamt) + "'" + ","        
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + "," // open_amount.....should this be base or foreign ?  currently it's foreign
                        + "'" + "I" + "'" + ","
                        + "'" + ref + "'" + ","
                        + "'" + rmks + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + dfdate.format(duedate) + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + taxcode + "'" + ","
                        + "'" + bank + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + "o" + "'" 
                        + ")"
                        + ";");
                    
                  
                    
                    if (taxamt > 0) {
                      ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(taxcode);
                          for (String[] elements : taxelements) {
                                  st.executeUpdate("insert into art_tax "
                                + "(art_nbr, art_desc, art_type, art_amt, art_percent ) "
                                + " values ( " + "'" + shipper + "'" + ","
                                + "'" + elements[0] + "'" + ","
                                + "'" + elements[2] + "'" + ","
                                + "'" + currformatDoubleUS(amt * ( bsParseDoubleUS(elements[1]) / 100 )) + "'" + ","   // amount is currently 'foreign' ...not base
                                + "'" + elements[1] + "'" 
                                + ")"
                                + ";");
                          }
                    }
                    
                  } // if type = "I"
                    
                    
                   if (type.equals("P")) {
                       String arnbr = (String.valueOf(OVData.getNextNbr("ar")));
                         st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_base_amt, ar_curr, ar_base_curr, ar_amt_tax, ar_base_amt_tax, ar_open_amt, ar_type, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_duedate, ar_paiddate, ar_acct, ar_cc, "
                        + "ar_terms, ar_tax_code, ar_bank, ar_site, ar_status) "
                        + " values ( " + "'" + cust + "'" + ","
                        + "'" + arnbr + "'" + ","
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(baseamt + basetaxamt) + "'" + ","   
                        + "'" + curr + "'" + ","   
                        + "'" + basecurr + "'" + ","        
                        + "'" + currformatDoubleUS(taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(basetaxamt) + "'" + ","        
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + "," // open_amount.....should this be base or foreign ?  currently it's foreign
                        + "'" + "P" + "'" + ","
                        + "'" + "cash" + "'" + ","
                        + "'" + rmks + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","        
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + taxcode + "'" + ","
                        + "'" + bank + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + "c" + "'" 
                        + ")"
                        + ";");
                         
                      st.executeUpdate("insert into ard_mstr "
                            + "(ard_id, ard_cust, ard_ref, ard_line, ard_date, ard_amt, ard_amt_tax, ard_base_amt, ard_base_amt_tax, ard_curr, ard_base_curr, ard_acct, ard_cc ) "
                            + " values ( " + "'" + arnbr + "'" + ","
                                + "'" + cust + "'" + ","
                            + "'" + shipper + "'" + ","
                            + "'" + "1" + "'" + ","
                            + "'" + dfdate.format(effdate) + "'" + ","
                            + "'" + currformatDoubleUS(amt) + "'"  + ","
                            + "'" + currformatDoubleUS(taxamt) + "'"  + ","
                            + "'" + currformatDoubleUS(baseamt) + "'"  + ","                
                            + "'" + currformatDoubleUS(basetaxamt) + "'" + "," 
                            + "'" + curr + "'"  + ","
                            + "'" + basecurr + "'" + ","
                            + "'" + acct + "'" + ","
                            + "'" + cc + "'"  
                            + ")"
                            + ";");    
                      
                      // update AR entry for original invoices with status and open amt  
                      ARUpdate(arnbr);  
                      // execute glentry
                      fglData.glEntryFromARPayment(arnbr,effdate);
                     
                  } // if type = "P"  
                    
         
            } catch (SQLException s) {
                myerror = true;
                MainFrame.bslog(s);
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
            myerror = true;
            MainFrame.bslog(e);
        }
           return m;
       }
           
    public static boolean AREntry(String shipper, Date effdate) {
            boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();
            
           
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
                    String cust = "";
                    String ref = "";
                    String rmks = "";
                    String acct = "";
                    String cc = "";
                    String terms = "";
                    String site = "";
                    String taxcode = "";
                    String curr = "";
                    String basecurr = "";
                    String bank = "";
                    
                    Date duedate = new Date();
                            
                    double amt = 0.00;
                    double baseamt = 0.00;
                    double taxamt = 0.00;
                    double basetaxamt = 0.00;
                    
                    double matltax = 0.00;
                   
                   
                    res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                  
                    amt += (res.getDouble("shd_qty") * res.getDouble("shd_netprice"));
                    matltax += OVData.getTaxAmtApplicableByItem(res.getString("shd_item"),res.getDouble("shd_qty") * res.getDouble("shd_netprice")); // line level matl tax
                    }
                    res.close();
                    

                    // lets retrieve any summary charges from orders associated with this shipment.
                    res = st.executeQuery("select * from shs_det where shs_nbr = " + "'" + shipper + "'" + 
                            " and shs_type = 'charge' " +               
                            ";");
                    while (res.next()) {
                    amt += res.getDouble("shs_amt");
                    }
                    res.close();
                    
                   
                    
                    
                    res = st.executeQuery("select * from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                     cust = res.getString("sh_cust");
                     ref = res.getString("sh_ref");
                     rmks = res.getString("sh_rmks");
                     acct = res.getString("sh_ar_acct");
                     cc = res.getString("sh_ar_cc");
                     terms = res.getString("sh_cust_terms");
                     taxcode = res.getString("sh_taxcode");
                     site = res.getString("sh_site");
                     curr = res.getString("sh_curr");
                    }
                    res.close();
                    
                   
                    
                    
                    
                    
                    // line matl tax versus order level tax....material Tax will be at line level..
                    //..summary tax will ONLY be at summary level...it will not be baked into line level
                    // ...summary level tax cannot be reported at line level
                    taxamt += shpData.getTaxAmtApplicableByShipper(shipper, amt);  
                  
                    
                    
                    
                    duedate = getDueDateFromTerms(effdate, terms);
                    if (duedate == null) {
                        myerror = true;
                    }
                    
                    bank = OVData.getBankCodeOfCust(cust);
                    if (bank.isEmpty()) {
                        bank = OVData.getDefaultARBank();
                    }
                    
                    
                    // let's handle the currency exchange...if any
                    basecurr = OVData.getDefaultCurrency();
                    
                    
                    if (curr.toUpperCase().equals(basecurr.toUpperCase())) {
                        baseamt = amt;
                        basetaxamt = taxamt;
                    } else {
                        baseamt = OVData.getExchangeBaseValue(basecurr, curr, amt);
                        basetaxamt = OVData.getExchangeBaseValue(basecurr, curr, taxamt);
                    }
                    
                  
                    
                    if (! myerror)
                         st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_base_amt, ar_curr, ar_base_curr, ar_amt_tax, ar_base_amt_tax, ar_open_amt, ar_type, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_duedate, ar_acct, ar_cc, "
                        + "ar_terms, ar_tax_code, ar_bank, ar_site, ar_status) "
                        + " values ( " + "'" + cust + "'" + ","
                        + "'" + shipper + "'" + ","
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(baseamt + basetaxamt) + "'" + ","   
                        + "'" + curr + "'" + ","   
                        + "'" + basecurr + "'" + ","        
                        + "'" + currformatDoubleUS(taxamt) + "'" + ","
                        + "'" + currformatDoubleUS(basetaxamt) + "'" + ","        
                        + "'" + currformatDoubleUS(amt + taxamt) + "'" + "," // open_amount.....should this be base or foreign ?  currently it's foreign
                        + "'" + "I" + "'" + ","
                        + "'" + ref + "'" + ","
                        + "'" + rmks + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + dfdate.format(duedate) + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + taxcode + "'" + ","
                        + "'" + bank + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + "o" + "'" 
                        + ")"
                        + ";");
                    
                  
                    
                    if (taxamt > 0) {
                      ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(taxcode);
                          for (String[] elements : taxelements) {
                                  st.executeUpdate("insert into art_tax "
                                + "(art_nbr, art_desc, art_type, art_amt, art_percent ) "
                                + " values ( " + "'" + shipper + "'" + ","
                                + "'" + elements[0] + "'" + ","
                                + "'" + elements[2] + "'" + ","
                                + "'" + currformatDoubleUS(amt * ( bsParseDoubleUS(elements[1]) / 100 )) + "'" + ","   // amount is currently 'foreign' ...not base
                                + "'" + elements[1] + "'" 
                                + ")"
                                + ";");
                          }
                    }
         
            } catch (SQLException s) {
                myerror = true;
                MainFrame.bslog(s);
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
            myerror = true;
            MainFrame.bslog(e);
        }
           return myerror;
       }
       
    public static boolean AREntryRV(String shipper, Date effdate) {
            boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();
            
           
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
              
                    String cust = "";
                    String ref = "";
                    String rmks = "";
                    String acct = "";
                    String cc = "";
                    String terms = "";
                    String site = "";
                    double amt = 0.00;
                    double baseamt = 0.00;
                   
                      res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                    amt = amt + (res.getDouble("shd_qty") * res.getDouble("shd_netprice"));
                    }
                    
                    // reverse the sign on the amount
                    amt = amt * -1;
                    
                    
                    
                    res = st.executeQuery("select * from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                     cust = res.getString("sh_cust");
                     ref = res.getString("sh_ref");
                     rmks = res.getString("sh_rmks");
                     acct = res.getString("sh_ar_acct");
                     cc = res.getString("sh_ar_cc");
                     terms = res.getString("sh_cust_terms");
                     site = res.getString("sh_site");
                    }
                    
                                        
                    Date duedate = getDueDateFromTerms(effdate, terms);
                    if (duedate == null) {
                        myerror = true;
                    }
                    
                    String bank = OVData.getBankCodeOfCust(cust);
                    if (bank.isEmpty()) {
                        bank = OVData.getDefaultARBank();
                    }
                    
                    
                    // let's handle the currency exchange...if any
                    String curr = cusData.getCustCurrency(cust);
                    String basecurr = OVData.getDefaultCurrency();
                    String exchangerate = OVData.getExchangeRate(basecurr, curr);
                    
                    if (curr.toUpperCase().equals(basecurr.toUpperCase())) {
                        baseamt = amt;
                    } else {
                        baseamt = amt / bsParseDoubleUS(exchangerate);
                    }
                    
                    
                    
                    if (! myerror)
                        
                        // should be only 1 ar_mstr record with same ar_nbr and ar_reverse = '0'....update this to ar_reverse = '1'
                         st.executeUpdate("update ar_mstr set ar_reverse = '1' where ar_nbr = " + "'" + shipper + "'" +
                                          " and ar_reverse = '0';");
                        
                    // now insert the reversing agent with reverse code 2 ...
                         st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_base_amt, ar_curr, ar_base_curr, ar_open_amt, ar_type, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_duedate, ar_acct, ar_cc, "
                        + "ar_terms, ar_bank, ar_site, ar_status, ar_reverse ) "
                        + " values ( " + "'" + cust + "'" + ","
                        + "'" + shipper + "'" + ","
                        + "'" + currformatDoubleUS(amt) + "'" + ","
                        + "'" + currformatDoubleUS(baseamt) + "'" + ","
                        + "'" + curr + "'" + ","
                         + "'" + basecurr + "'" + ","                 
                        + "'" + currformatDoubleUS(amt) + "'" + ","
                        + "'" + "I" + "'" + ","
                        + "'" + ref + "'" + ","
                        + "'" + rmks + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + dfdate.format(duedate) + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + bank + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + "o" + "'" + ","
                        + "'2'"
                        + ")"
                        + ";");
                     
         
            } catch (SQLException s) {
                myerror = true;
                MainFrame.bslog(s);
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
            myerror = true;
            MainFrame.bslog(e);
        }
           return myerror;
       }
             
    public static boolean ARUpdate(String batch) {
            boolean myerror = false;
            
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
                
                 // lets get original ar_mstr and ar_det info and update with applied amount
                    res = st.executeQuery("select ar_amt, ar_base_amt, ar_curr, ar_base_curr, ar_open_amt, ar_applied, ard_ref, ard_amt, ard_base_amt from ar_mstr inner join ard_mstr on ar_nbr = ard_ref " +
                                    " where ard_id = " + "'" + batch + "'"
                            );
                    
                     ArrayList ardref = new ArrayList();
                    ArrayList newamt = new ArrayList();
                    ArrayList openamt = new ArrayList();
                    ArrayList status = new ArrayList();
                    ArrayList gainloss = new ArrayList();
                    
                    while (res.next()) {
                        ardref.add(res.getString("ard_ref"));
                        newamt.add(res.getDouble("ard_amt") + res.getDouble("ar_applied"));
                        openamt.add(res.getDouble("ar_amt") - res.getDouble("ar_applied") - res.getDouble("ard_amt"));
                        if ( (res.getDouble("ard_amt") + res.getDouble("ar_applied")) >= res.getDouble("ar_amt") ) {
                         status.add("c");
                        } else {
                         status.add("o");
                        }
                    }
                    
                     for (int j = 0; j < ardref.size(); j++) {
                    st.executeUpdate("update ar_mstr set ar_applied = " + "'" + bsParseDoubleUS(newamt.get(j).toString()) + "'" + "," +
                            " ar_open_amt = " + "'" + bsParseDoubleUS(openamt.get(j).toString()) + "'" + "," +
                            " ar_status = " + "'" + status.get(j) + "'" +
                            " where ar_nbr = " + "'" + ardref.get(j) + "'" + 
                            " and ar_type = 'I' "
                            );
                     }
                
                
                
                
                 } catch (SQLException s) {
                myerror = true;
                MainFrame.bslog(s);
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
            myerror = true;
            MainFrame.bslog(e);
        }
            return myerror;
        }
        
    public static boolean isConfirmInShipMaint() {
         boolean myreturn = false;
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
               
                    res = st.executeQuery("SELECT * FROM  ship_ctrl ;");
                    while (res.next()) {
                        myreturn = res.getBoolean("shc_confirm");
                    }
           
            }
            catch (SQLException s) {
                 MainFrame.bslog(s);
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
         return myreturn;
     }
        
    public static void sourceOrder(String order) {
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
                int isSourced = 0;
                String status = "";
                 /* check to see if order number exists */
                 res = st.executeQuery("select so_nbr, so_status, so_issourced from so_mstr where so_nbr = " + "'" + order + "'" +";");
                 int i = 0;
                 while (res.next()) {
                     i++;
                     status = res.getString("so_status");
                     isSourced = res.getInt("so_issourced");
                 }
                 
                 if (isSourced == 1) {
                     bsmf.MainFrame.show("Order is already sourced");
                     return;
                 }
                 
                 if (i > 0 && ! status.isEmpty() && isSourced == 0) {
                       int error = EDI.Create940(order);
                       if (error == 0) {
                          bsmf.MainFrame.show(getMessageTag(1125));
                          updateOrderSourceFlag(order); 
                       }
                       if (error == 1)
                           bsmf.MainFrame.show("Missing WH/Doctype/Dir Record in cmedi_mstr");

                       if (error == 2)
                           bsmf.MainFrame.show(getMessageTag(1016));

                       if (error == 3)
                           bsmf.MainFrame.show(getMessageTag(1106));
                      
                 } else {
                     bsmf.MainFrame.show(getMessageTag(1034, order));
                     return;
                 }
              
             
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
       
    public static void quoteFreightOrder(String order) {
            
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
                int isQuoted = 0;
                int isTendered = 0; 
                String status = "";
                 /* check to see if order number exists */
                 res = st.executeQuery("select fo_nbr, fo_status, fo_isquoted, fo_istendered from fo_mstr where fo_nbr = " + "'" + order + "'" + ";");
                 int i = 0;
                 while (res.next()) {
                     i++;
                     status = res.getString("fo_status");
                     isQuoted = res.getInt("fo_isquoted");
                     isTendered = res.getInt("fo_istendered"); 
                 }
                 
                 if (isQuoted == 1) {
                     bsmf.MainFrame.show("Order is already quoted");
                     return;
                 }
                  if (isTendered == 1) {
                     bsmf.MainFrame.show("Order is already tendered");
                     return;
                 }
                 
                 if (i > 0 && ! status.isEmpty() && isQuoted == 0 && isTendered == 0) {
                       int error = EDI.Create219(order, "quote");
                       if (error == 0) {
                          bsmf.MainFrame.show("Order has been sent for Quote");
                          updateFreightOrderQuoteFlag(order); 
                       }
                       if (error == 1)
                           bsmf.MainFrame.show("Missing WH/Doctype/Dir Record in cmedi_mstr");

                       if (error == 2)
                           bsmf.MainFrame.show("Unable to retrieve wh from order");

                       if (error == 3)
                           bsmf.MainFrame.show("ClassDef and/or Invocation error");
                      
                 } else {
                     bsmf.MainFrame.show("Order does not exist");
                     return;
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
       
    public static void tenderResponse(String order, String response) {
            
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
                String status = "";
                 /* check to see if order number exists */
                 res = st.executeQuery("select fo_nbr, fo_status from fo_mstr where fo_nbr = " + "'" + order + "'" + ";");
                 int i = 0;
                 while (res.next()) {
                     i++;
                     status = res.getString("fo_status");
                 }
                 
                 if (status.equals("Accepted")) {
                     bsmf.MainFrame.show("Order is already accepted");
                     return;
                 }
                 if (status.equals("Declined")) {
                     bsmf.MainFrame.show("Order is already declined");
                     return;
                 }
                 
                 
                 if (i > 0 ) {
                       int error = EDI.Create990o(order, response);
                       if (error == 0) {
                          bsmf.MainFrame.show("Response has been sent.");
                          OVData.updateFreightOrderStatus(order, response);
                       }
                       if (error == 1)
                           bsmf.MainFrame.show("Missing WH/Doctype/Dir Record in cmedi_mstr");

                       if (error == 2)
                           bsmf.MainFrame.show("Unable to retrieve wh from order");

                       if (error == 3)
                           bsmf.MainFrame.show("ClassDef and/or Invocation error");
                      
                 } else {
                     bsmf.MainFrame.show("Order does not exist");
                     return;
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
        
    public static void tenderFreightOrder(String order) {

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
            int isTendered = 0; 
            String status = "";
             /* check to see if order number exists */
             res = st.executeQuery("select fo_nbr, fo_status, fo_isquoted, fo_istendered from fo_mstr where fo_nbr = " + "'" + order + "'" + ";");
             int i = 0;
             while (res.next()) {
                 i++;
                 status = res.getString("fo_status");
                 isTendered = res.getInt("fo_istendered"); 
             }

              if (isTendered == 1) {
                 bsmf.MainFrame.show("Order is already tendered");
                 return;
             }

             if (i > 0 && ! status.isEmpty() && isTendered == 0) {
                   int error = EDI.Create204(order);
                   if (error == 0) {
                      bsmf.MainFrame.show("Order has been sent for Tendering");
                      updateFreightOrderTenderFlag(order); 
                   }
                   if (error == 1)
                       bsmf.MainFrame.show("Missing WH/Doctype/Dir Record in cmedi_mstr");

                   if (error == 2)
                       bsmf.MainFrame.show("Unable to retrieve wh from order");

                   if (error == 3)
                       bsmf.MainFrame.show("ClassDef and/or Invocation error");

             } else {
                 bsmf.MainFrame.show("Order does not exist");
                 return;
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

    public static void unconfirmShipment(String shipper, Date effdate) {

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
           
            boolean proceed = true;
            boolean error = false;
             /* check to see if shipper number exists */
             res = st.executeQuery("select sh_id, sh_status from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
             if (! res.isBeforeFirst()) {
                 proceed = false;
                 bsmf.MainFrame.show("Shipper does not exist!");
             } else {
                  while (res.next()) {
                    if (res.getInt("sh_status") == 0) {
                        proceed = false;
                        bsmf.MainFrame.show("Shipper not yet invoiced");
                    }
                }
             }


            if (proceed) {
                    /* create ar_mstr record */
                    error = AREntryRV(shipper, effdate);

                    /* create tran_mstr records */
                    if (! error)
                    error = TRHistIssSalesRV(shipper, effdate);

                    /* adjust inventory */
                    if (! error)
                    error = UpdateInventoryFromShipperRV(shipper);

                    /* create gl_tran records */
                    if (! error)
                    error = fglData.glEntryFromShipperRV(shipper, effdate);

              if (! error) {   
              OVData.updateShipperStatusRV(shipper, effdate);
              OVData.updateOrderFromShipperRV(shipper);
              bsmf.MainFrame.show("shipper Unconfirmed");
              } else {
              bsmf.MainFrame.show("An Error Occurred");  
              }
           } // if proceed
        } catch (SQLException s) {
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
       
    
    public static ArrayList<String[]> getOrderSAC(String order) {
      ArrayList<String[]> sac = new ArrayList<String[]>();
      try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
             res = st.executeQuery("select sos_nbr, sos_desc, sos_type, sos_amttype, sos_amt from sos_det where sos_nbr = " + "'" + order + "'" + ";");
             while (res.next()) {
                 String[] myarray = new String[5];
                 myarray[0] = res.getString("sos_nbr");
                 myarray[1] = res.getString("sos_desc");
                 myarray[2] = res.getString("sos_type");
                 myarray[3] = res.getString("sos_amttype");
                 myarray[4] = res.getString("sos_amt");
                 sac.add(myarray);
             }
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            if (res != null) {
                res.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
      return sac;
   }

    public static void voidPOSStatus(String nbr, Connection bscon) throws SQLException {
     Statement st = bscon.createStatement();
               st.executeUpdate(
                 " update pos_mstr set pos_status = 'void' " +
                 " where pos_nbr = " + "'" + nbr + "'" + ";" );
               if (st != null) {st.close();}
   }

    public static void updateShipperStatusRV(String shipper, Date effdate) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        
        try{
                       st.executeUpdate(
                             " update ship_mstr set sh_status = '0', sh_confdate = " + "'" + dfdate.format(effdate) + "'" +
                             " where sh_id = " + "'" + shipper + "'" + ";" );
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }

   }

    public static void updateOrderFromShipper(String shipper, Connection bscon) throws SQLException {

        boolean partial = false;
        boolean complete = true;
        ArrayList<String> orders = new ArrayList<String>();
        Set<String> uniqueorders = new HashSet<String>();

       
        Statement st = bscon.createStatement();
        ResultSet res = null;
        
            ArrayList qty = new ArrayList();
            ArrayList shippedqty = new ArrayList();
            ArrayList line = new ArrayList();
            ArrayList ordqty = new ArrayList();
            ArrayList linestatus = new ArrayList();
            ArrayList ordernbr = new ArrayList();

             res = st.executeQuery("select sod_nbr, sod_status, sod_line, shd_item, sum(shd_qty) as sumqty, sod_shipped_qty, sod_ord_qty from ship_det inner join " +
                     " sod_det on shd_item = sod_item and shd_soline = sod_line and shd_so = sod_nbr " +
               " where shd_id = " + "'" + shipper + "'" + 
               " group by shd_item, sod_nbr, sod_status, sod_line, sod_shipped_qty, sod_ord_qty " +                        
               ";");
               while (res.next()) {
                   shippedqty.add(res.getString("sod_shipped_qty"));
                   qty.add(res.getString("sumqty"));
                   ordqty.add(res.getString("sod_ord_qty"));
                   linestatus.add(res.getString("sod_status"));
                   line.add(res.getString("sod_line"));
                   ordernbr.add(res.getString("sod_nbr"));
                }
               res.close();
                              // res = st.executeQuery("select shd_item from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
          if (dbtype.equals("sqlite")) {
              double total = 0;
              String status = "";
              for (int j = 0; j < line.size(); j++) {
                  total = bsParseDouble(qty.get(j).toString()) + bsParseDouble(shippedqty.get(j).toString());
                  if (total >= bsParseDouble(ordqty.get(j).toString())) {
                      status = getGlobalProgTag("closed");
                  } else {
                      status = linestatus.get(j).toString();
                  }
                  st.executeUpdate("update sod_det set sod_shipped_qty = " + "'" + total + "'" + ", sod_status = " + "'" + status + "'" + 
                           " where sod_nbr = " + "'" + ordernbr.get(j).toString() + "'" +
                           " and sod_line = " + "'" + line.get(j).toString() + "'" +
                          ";" );
              }
          } else {
              st.executeUpdate(
                     " update sod_det inner join ship_det on shd_item = sod_item and shd_soline = sod_line and shd_so = sod_nbr " +
                     " inner join so_mstr on so_nbr = sod_nbr and so_type = 'DISCRETE' " +
                      " set sod_shipped_qty = sod_shipped_qty + shd_qty, sod_status = " +
                      " (case when sod_shipped_qty + shd_qty >= sod_ord_qty then " + "'" + getGlobalProgTag("closed") + "'" +
                      " else sod_status end) " +
                 " where shd_id = " + "'" + shipper + "'" + ";" );
          }
                // now let's select the unique orders involved in that shipper
               res = st.executeQuery("select sod_nbr from sod_det inner join ship_det on shd_so = sod_nbr " +
               " where shd_id = " + "'" + shipper + "'" +";");
               while (res.next()) {
                   uniqueorders.add(res.getString("sod_nbr"));
                }
               for (String uniqueorder : uniqueorders) {
                   orders.clear();
                    partial = false;
                   complete = true;
                   res = st.executeQuery("select sod_nbr, sod_status from sod_det " +
                           " where sod_nbr = " + "'" + uniqueorder + "'" +";");
                   while (res.next()) {
                       // logic is that a shipper has been committed with at least some portion of this order
                       // therefore if any line items on that order are still open...then the order was shipped partial...
                       //  therefore flag it as backorder
                       if (res.getString("sod_status").equals(getGlobalProgTag("open"))) {
                               partial = true;
                            }
                       if (! res.getString("sod_status").equals(getGlobalProgTag("closed"))) {
                               complete = false;
                            }
                    }


                   if (complete) {
                    st.executeUpdate( "update so_mstr set so_status  = " + "'" + getGlobalProgTag("closed") + "'" + " where so_nbr = " + "'" + uniqueorder + "'" + ";"); 
                   }
                   if (partial && ! complete) {
                   st.executeUpdate( "update so_mstr set so_status = 'backorder' where so_nbr = " + "'" + uniqueorder + "'" + ";");
                   }
                }
        res.close();
        st.close();
   }

    public static void updateServiceOrderFromShipper(String shipper, Connection bscon) throws SQLException {
        Statement st = bscon.createStatement();
        ResultSet res = null;
            String ordernbr = "";
             res = st.executeQuery("select svd_nbr from ship_det inner join " +
                     " svd_det on shd_item = svd_item and shd_soline = svd_line and shd_so = svd_nbr " +
               " where shd_id = " + "'" + shipper + "'" +";");
               while (res.next()) {
                   ordernbr = res.getString("svd_nbr");
                }
               res.close();
               st.executeUpdate( "update sv_mstr set sv_status = " + "'" + getGlobalProgTag("closed") + "'" + " where sv_nbr = " + "'" + ordernbr + "'" + ";"); 
        res.close();
        st.close();
   }

    public static void updateOrderFromShipperRV(String shipper) {

        boolean partial = false;
        boolean complete = true;
        ArrayList<String> orders = new ArrayList<String>();
        Set<String> uniqueorders = new HashSet<String>();

        try{

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            
           // res = st.executeQuery("select shd_item from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
               st.executeUpdate(
                     " update sod_det inner join ship_det on shd_item = sod_item and shd_soline = sod_line and shd_so = sod_nbr " +
                     " inner join so_mstr on so_nbr = sod_nbr and so_type = 'DISCRETE' " +
                      " set sod_shipped_qty = sod_shipped_qty - shd_qty, " +
                      " sod_status = " + "'" + getGlobalProgTag("open") + "'" + " ) " +
                 " where shd_id = " + "'" + shipper + "'" + ";" );

                // now let's select the unique orders involved in that shipper
               res = st.executeQuery("select sod_nbr from sod_det inner join ship_det on shd_so = sod_nbr " +
               " where shd_id = " + "'" + shipper + "'" +";");
               while (res.next()) {
                   uniqueorders.add(res.getString("sod_nbr"));
                }


               for (String uniqueorder : uniqueorders) {
                   orders.clear();
                    partial = false;
                   complete = true;
                   res = st.executeQuery("select sod_nbr, sod_status from sod_det " +
                           " where sod_nbr = " + "'" + uniqueorder + "'" +";");
                   while (res.next()) {
                       // logic is that a shipper has been committed with at least some portion of this order
                       // therefore if any line items on that order are still open...then the order was shipped partial...
                       //  therefore flag it as backorder
                       if (res.getString("sod_status").equals(getGlobalProgTag("open"))) {
                               partial = true;
                            }
                       if (! res.getString("sod_status").equals(getGlobalProgTag("closed"))) {
                               complete = false;
                            }
                    }


                   if (complete) {
                    st.executeUpdate( "update so_mstr set so_status = " + "'" + getGlobalProgTag("closed") + "'" + " where so_nbr = " + "'" + uniqueorder + "'" + ";"); 
                   }
                   if (partial && ! complete) {
                   st.executeUpdate( "update so_mstr set so_status = " + "'" + getGlobalProgTag("backorder") + "'" + " where so_nbr = " + "'" + uniqueorder + "'" + ";");
                   }

               }

        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
           if (res != null) {
                res.close();
            }
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }

   }

    public static ArrayList getPrinterList() {
           ArrayList<String> mylist = new ArrayList<String>();  
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select prt_id from prt_mstr order by prt_id;");
               while (res.next()) {
                mylist.add(res.getString("prt_id"));                    
                }
               
           }
            catch (SQLException s){
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }    
     
    public static String[] getPrinterInfo(String printer) {
          String myreturn[] = new String[]{"","",""};
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select prt_ip, prt_port, prt_type from prt_mstr where  " +
                        " prt_id = " + "'" + printer + "'" + " ;");
               while (res.next()) {
                myreturn[0] = res.getString("prt_ip");
                myreturn[1] = res.getString("prt_port");
                myreturn[2] = res.getString("prt_type");
                }
               
           }
            catch (SQLException s){
                s.printStackTrace();
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myreturn;
        
    }    
      
    public static boolean isValidPrinter(String printer) {
          boolean myreturn = false;
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select prt_ip from prt_mstr where  " +
                        " prt_id = " + "'" + printer + "'" + " ;");
               while (res.next()) {
                myreturn = true;               
                }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myreturn;
        
    }    
      
    public static void exportCSV(JTable tablereport) {
          FileDialog fDialog;
                fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
                fDialog.setVisible(true);
                //fDialog.setFile("data.csv");
                String path = fDialog.getDirectory() + fDialog.getFile();
                File f = new File(path);
                BufferedWriter output;
                
                
        try {
            output = new BufferedWriter(new FileWriter(f));
            // lets put out the headers
            for (int j = 0; j < tablereport.getColumnCount(); j++) {
                if (tablereport.getColumnName(j).toString().toLowerCase().equals(getGlobalColumnTag("select")) ||  
                    tablereport.getColumnName(j).toString().toLowerCase().equals(getGlobalColumnTag("detail"))) {
                    continue;
                }
                output.write(tablereport.getColumnName(j).toString().replace(",", "") + ",");
            }
            output.write('\n');
            // now the data
            for (int i = 0; i < tablereport.getRowCount(); i++) {
                for (int j = 0; j < tablereport.getColumnCount(); j++) {
                  if (tablereport.getColumnName(j).toString().toLowerCase().equals(getGlobalColumnTag("select")) ||  
                    tablereport.getColumnName(j).toString().toLowerCase().equals(getGlobalColumnTag("detail"))) {
                    continue;
                }
                if (tablereport.getValueAt(i, j) != null ) {
                output.write(tablereport.getValueAt(i, j).toString().replace(",", "") + ",");
                } else {
                output.write(" " + ",");    
                }
                }
                output.write('\n');
            }
            output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
                
      }
    
    public static void exportTable(String tablename, String filename) {
         
         try{
            Path outpath = FileSystems.getDefault().getPath("temp" + "/" + filename);
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath.toFile())));
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                res = st.executeQuery("select * from " + tablename + ";");
               while (res.next()) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 1; j <= res.getMetaData().getColumnCount(); j++) {
                      line .append(res.getString(j)).append(":");
                    }
                    output.write(line.deleteCharAt(line.length() - 1).toString() + "\n");                  
                }
                output.close();
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (IOException | SQLException e){
            MainFrame.bslog(e);
        }
    }
    
    public static Map<String,Integer> getTableInfo(String tablename) {
         Map<String,Integer> hm = new HashMap<String,Integer>();
         try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                res = st.executeQuery("select * from " +  tablename  + " limit 1 ;");
                ResultSetMetaData rsmd = res.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                  hm.put(rsmd.getColumnName(i), rsmd.getPrecision(i));  
                }
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        }
        catch (SQLException e){
            MainFrame.bslog(e);
        }
         return hm;
    }
    
    
    /* print methods */
    public static void printPOS_Jasper(String nbr) {
        
            try (Connection con = DriverManager.getConnection(url + db, user, pass)) {
                String site = OVData.getDefaultSite();
                String imagepath = "";
                String logo = "";
                logo = OVData.getSiteLogo(site);
                String jasperfile = "";
                jasperfile = OVData.getDefaultPOSJasper(site);
                imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "RECEIPT");
                hm.put("myid",  nbr);
                hm.put("mysite",  site);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
                // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
                // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile);
                //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/posprt.pdf");
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
            } catch (Exception e) {
            MainFrame.bslog(e);
        } 
    }      
       
    public static void printInvoice(String invoice, boolean display) {
        
        File file = new File("temp/ivprt.pdf");
        if (file.exists()) {
            file.delete();
        }
        
        try{
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
               
                 String cust = ""; 
                 String site = ""; 
                 String site_csz = "";
                 String bill_csz = "";
                 String ship_csz = "";
                 
                res = st.executeQuery("select sh_cust, sh_site, cm_city, cm_state, cm_zip, " +
                        " cms_city, cms_state, cms_zip, site_city, site_state, site_zip " +
                        " from ship_mstr " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " where sh_id = " + "'" + invoice + "'" + ";");
                       while (res.next()) {
                          cust = res.getString(("sh_cust"));
                          site = res.getString(("sh_site"));
                          site_csz = res.getString(("site_city")) + " " + res.getString(("site_state")) + " " + res.getString(("site_zip"));
                          bill_csz = res.getString(("cm_city")) + " " + res.getString(("cm_state")) + " " + res.getString(("cm_zip"));
                          ship_csz = res.getString(("cms_city")) + " " + res.getString(("cms_state")) + " " + res.getString(("cms_zip"));
                       }
                
                
                String imagepath = "";
                String logo = "";
                logo = cusData.getCustLogo(cust);
                if (logo.isEmpty()) {
                    logo = OVData.getSiteLogo(site);
                }
                
                String jasperfile = "";
               jasperfile = cusData.getCustInvoiceJasper(cust);
                if (jasperfile.isEmpty()) {
                    jasperfile = OVData.getDefaultInvoiceJasper(site);
                }
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "INVOICE");
                hm.put("myid",  invoice);
                hm.put("site_csz", site_csz);
                hm.put("bill_csz", bill_csz);
                hm.put("ship_csz", ship_csz);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ivprt.pdf");
                
                if (display) {
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
    
    public static void printInvoiceByOrder(String order) {
        try{
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               
               
                 String cust = ""; 
                 String site = ""; 
                 String invoice = "";
               
                 
                 String site_csz = "";
                 String bill_csz = "";
                 String ship_csz = "";
                 
                res = st.executeQuery("select sh_id, sh_cust, sh_site, cm_city, cm_state, cm_zip, " +
                        " cms_city, cms_state, cms_zip, site_city, site_state, site_zip " +
                        " from ship_mstr " +
                        " inner join ship_det on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " where shd_so = " + "'" + order + "'" + ";");
                       int i = 0;
                       while (res.next()) {
                          i++;
                          cust = res.getString(("sh_cust"));
                          site = res.getString(("sh_site"));
                          site_csz = res.getString(("site_city")) + " " + res.getString(("site_state")) + " " + res.getString(("site_zip"));
                          bill_csz = res.getString(("cm_city")) + " " + res.getString(("cm_state")) + " " + res.getString(("cm_zip"));
                          ship_csz = res.getString(("cms_city")) + " " + res.getString(("cms_state")) + " " + res.getString(("cms_zip"));
                          invoice = res.getString("sh_id");
                          if (i > 1) {
                              break;
                          }
                       }
                
                
                
                String imagepath = "";
                String logo = "";
                logo = cusData.getCustLogo(cust);
                if (logo.isEmpty()) {
                    logo = OVData.getSiteLogo(site);
                }
                
                String jasperfile = "";
               jasperfile = cusData.getCustInvoiceJasper(cust);
                if (jasperfile.isEmpty()) {
                    jasperfile = OVData.getDefaultInvoiceJasper(site);
                }
                
               
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "INVOICE");
                hm.put("myid",  invoice);
                hm.put("site_csz", site_csz);
                hm.put("bill_csz", bill_csz);
                hm.put("ship_csz", ship_csz);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ivprt.pdf");
                
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
    
    public static void printJTableToJasper(String reportname, JTable tablereport, String type) {
        HashMap hm = new HashMap();
        hm.put("REPORT_TITLE", reportname);
        hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
        TableModel model = tablereport.getModel();
        
        DefaultTableModel newmodel = new DefaultTableModel();
        for (int k = 0; k < model.getColumnCount(); k++) {
            if (model.getColumnClass(k).getSimpleName().equals("ImageIcon")) {
                continue;
            }
            newmodel.addColumn(model.getColumnName(k));
        }
       
       
         int nRow = model.getRowCount(), nCol = newmodel.getColumnCount();
         int offset = model.getColumnCount() - nCol;
         String[] myarray = new String[nCol];
         
        for (int i = 0 ; i < nRow ; i++) {
           for (int j = 0 ; j < nCol ; j++) {
            myarray[j] = tablereport.getValueAt(i,(j + offset)).toString();
           }
           newmodel.addRow(myarray);
         }
        
        /*
        for (int i = 0 ; i < nRow ; i++) {
           for (int j = 0 ; j < nCol ; j++) {
           // tableData[i][j] = model.getValueAt(i,j).toString();
            myarray[j] = model.getValueAt(i,(j + offset)).toString();
           }
           newmodel.addRow(myarray);
         }
         */
       
        
        
        
        for (int j = 0; j < newmodel.getColumnCount(); j++) {
           hm.put("d" + j,  newmodel.getColumnName(j).toString());
        }
        
        String jasperfile = OVData.getCodeValueByCodeKey("jasper", type);
        if (jasperfile.isEmpty()) {
            jasperfile = "genericJTableL11.jasper";
        }
        
      
        
        File mytemplate = new File("jasper/" + jasperfile); 
        JasperPrint jasperPrint; 
       try {
           
         jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(newmodel) );
         JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
           jasperViewer.setVisible(true);
           jasperViewer.setFitPageZoomRatio();
           //  JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ivprt.pdf");
       } catch (JRException ex) {
           MainFrame.bslog(ex);
       }
      

        
                
           
    }   
        
    public static void printReceipt(String shipper) {
        try{
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
               
                String cust = ""; 
                String site = ""; 
                String site_csz = "";
                String bill_csz = "";
                String ship_csz = "";
                 
                res = st.executeQuery("select sh_cust, sh_site, cm_city, cm_state, cm_zip, " +
                        " cms_city, cms_state, cms_zip, site_city, site_state, site_zip " +
                        " from ship_mstr " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " where sh_id = " + "'" + shipper + "'" + ";");
                       while (res.next()) {
                          cust = res.getString(("sh_cust"));
                          site = res.getString(("sh_site"));
                          site_csz = res.getString(("site_city")) + " " + res.getString(("site_state")) + " " + res.getString(("site_zip"));
                          bill_csz = res.getString(("cm_city")) + " " + res.getString(("cm_state")) + " " + res.getString(("cm_zip"));
                          ship_csz = res.getString(("cms_city")) + " " + res.getString(("cms_state")) + " " + res.getString(("cms_zip"));
                       }
                String imagepath = "";
                String logo = "";
                logo = cusData.getCustLogo(cust);
                if (logo.isEmpty()) {
                    logo = OVData.getSiteLogo(site);
                }
                
                String jasperfile = "receipt_generic.jasper";
               
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "RECEIPT");
                hm.put("myid",  shipper);
                hm.put("site_csz", site_csz);
                hm.put("bill_csz", bill_csz);
                hm.put("ship_csz", ship_csz);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ivprt.pdf");
                
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
    
    public static void printAPCheck(String batch) {
           
            try (Connection con = DriverManager.getConnection(url + db, user, pass)) {
               
                String jasperfile = "apcheck_generic.jasper";
            
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "INVOICE");
                hm.put("myid",  batch);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/chkrun.pdf");
                
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
               
            } catch (Exception s) {
                MainFrame.bslog(s);
            } 
            
       
    } 
    
    public static void printImageJasper(String image) {
           
        Image photo;
        Path imagepath = FileSystems.getDefault().getPath("images" + "/" + image);
        ImageIcon icon = new ImageIcon(imagepath.toString());
      //  icon = new ImageIcon(icon.getImage().getScaledInstance(600, 600, Image.SCALE_DEFAULT));
        photo = icon.getImage();
      //  System.out.println(icon.getIconHeight() + "/" + icon.getIconWidth());
        String jasperfile = "";
        Map<String, Object> param = new HashMap<String, Object>();
        
            try (Connection con = DriverManager.getConnection(url + db, user, pass)) {
               
                if (icon.getIconWidth() >= icon.getIconHeight()) {
                  jasperfile = "image_generic_landscape.jasper";
                } else {
                  jasperfile = "image_generic.jasper";   
                }
            
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "INVOICE");
                hm.put("myid",  image);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
                hm.put("photo", photo);
               
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JREmptyDataSource() );
               
               // JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/itemimage.pdf");
                
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                                
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
               
            } catch (Exception s) {
                MainFrame.bslog(s);
            } 
            
       
    } 
       
    public static void showPDFusingIcePDF(String file) {
        
        PrintStream orgStream   = null;
        PrintStream fileStream  = null;
        
        try {
        
        // this redirect of Std Err is necessary for icePDF warnings/crap that you are using free stuff  
        // anyone know how to suppress these warnings...I'm all ears.
        
        orgStream = System.out;
        fileStream = new PrintStream(new FileOutputStream("icePDF.log",true));
        System.setErr(fileStream);
        
        Path pdfpath = FileSystems.getDefault().getPath("images" + "/" + file);

        // build a controller
        SwingController controller = new SwingController();

        // Build a SwingViewFactory configured with the controller
        SwingViewBuilder factory = new SwingViewBuilder(controller);

        // Use the factory to build a JPanel that is pre-configured
        //with a complete, active Viewer UI.
        JPanel viewerComponentPanel = factory.buildViewerPanel();

        // add copy keyboard command
        ComponentKeyBinding.install(controller, viewerComponentPanel);

        // add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback(
              new org.icepdf.ri.common.MyAnnotationCallback(
                     controller.getDocumentViewController())); 

        // Create a JFrame to display the panel in
        JFrame window = new JFrame("Viewer"); 
        window.getContentPane().add(viewerComponentPanel);
        window.pack();
        window.setVisible(true);

        // Open a PDF document to view
        controller.openDocument(pdfpath.toString());
        
        } catch (Exception ex) {
            bslog(ex);
        } finally {
           System.setErr(orgStream);
        }
    }
    
    public static void printShipper(String shipper) {
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
               
                String cust = "";
                String site = "";
                 String site_csz = "";
                 String bill_csz = "";
                 String ship_csz = "";
                 
                res = st.executeQuery("select sh_cust, sh_site, cm_city, cm_state, cm_zip, " +
                        " cms_city, cms_state, cms_zip, site_city, site_state, site_zip " +
                        " from ship_mstr " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " where sh_id = " + "'" + shipper + "'" + ";");
                       while (res.next()) {
                          cust = res.getString(("sh_cust"));
                          site = res.getString(("sh_site"));
                          site_csz = res.getString(("site_city")) + " " + res.getString(("site_state")) + " " + res.getString(("site_zip"));
                          bill_csz = res.getString(("cm_city")) + " " + res.getString(("cm_state")) + " " + res.getString(("cm_zip"));
                          ship_csz = res.getString(("cms_city")) + " " + res.getString(("cms_state")) + " " + res.getString(("cms_zip"));
                       }
                String imagepath = "";
                String logo = cusData.getCustLogo(cust);
                if (logo.isEmpty()) {
                    logo = OVData.getSiteLogo(site);
                }
                
                String jasperfile = cusData.getCustShipperJasper(cust);
                if (jasperfile.isEmpty()) {
                    jasperfile = OVData.getDefaultShipperJasper(site);
                }
                
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "SHIPPER");
                hm.put("myid",  shipper);
                hm.put("site_csz", site_csz);
                hm.put("bill_csz", bill_csz);
                hm.put("ship_csz", ship_csz);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile);
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/shprt.pdf");
         
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                con.close();
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } 
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }    
        
    public static void printShipperByOrder(String order) {
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            try{
                
                String cust = "";
                String site = "";
                String shipper = "";
                 String site_csz = "";
                 String bill_csz = "";
                 String ship_csz = "";
                 
                res = st.executeQuery("select sh_id, sh_cust, sh_site, cm_city, cm_state, cm_zip, " +
                        " cms_city, cms_state, cms_zip, site_city, site_state, site_zip " +
                        " from ship_mstr " +
                        " inner join ship_det on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " where shd_so = " + "'" + order + "'" + ";");
                       int i = 0;
                       while (res.next()) {
                          i++;
                          cust = res.getString(("sh_cust"));
                          site = res.getString(("sh_site"));
                          site_csz = res.getString(("site_city")) + " " + res.getString(("site_state")) + " " + res.getString(("site_zip"));
                          bill_csz = res.getString(("cm_city")) + " " + res.getString(("cm_state")) + " " + res.getString(("cm_zip"));
                          ship_csz = res.getString(("cms_city")) + " " + res.getString(("cms_state")) + " " + res.getString(("cms_zip"));
                          shipper = res.getString("sh_id");
                          if (i > 1) {
                              break;
                          }
                       }
                
                String imagepath = "";
                String logo = cusData.getCustLogo(cust);
                if (logo.isEmpty()) {
                    logo = OVData.getSiteLogo(site);
                }
                
                String jasperfile = cusData.getCustShipperJasper(cust);
                if (jasperfile.isEmpty()) {
                    jasperfile = OVData.getDefaultShipperJasper(site);
                }
                
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "SHIPPER");
                hm.put("myid",  shipper);
                hm.put("site_csz", site_csz);
                hm.put("bill_csz", bill_csz);
                hm.put("ship_csz", ship_csz);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile);
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
               
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/shprt.pdf");
         
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
      
    public static void printPurchaseOrder(String po) {
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            try{
                
                String vend = "";
                String site = "";
                String site_csz = "";
                String vend_csz = "";
                String ship_csz = "";
                res = st.executeQuery("select po_vend, po_site, " +
                        " vd_city, vd_state, vd_zip, site_city, site_state, site_zip " +
                        " from po_mstr " +
                        " inner join vd_mstr on vd_addr = po_vend " +
                        " inner join site_mstr on site_site = po_site " +
                        " where po_nbr = " + "'" + po + "'" + ";");
                       while (res.next()) {
                          vend = res.getString(("po_vend"));
                          site = res.getString(("po_site"));
                          site_csz = res.getString(("site_city")) + " " + res.getString(("site_state")) + " " + res.getString(("site_zip"));
                          vend_csz = res.getString(("vd_city")) + " " + res.getString(("vd_state")) + " " + res.getString(("vd_zip"));
                          ship_csz = site_csz;
                       }
                String imagepath = "";
                String logo = OVData.getSiteLogo(site);
                
                
              
                  String jasperfile = OVData.getDefaultPOPrintJasper(site);
               
                
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "SHIPPER");
                hm.put("myid",  po);
                hm.put("site_csz", site_csz);
                hm.put("vend_csz", vend_csz);
                hm.put("ship_csz", ship_csz);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile);
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                 JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/poprt.pdf");
         
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
     
    public static void printCustomerOrder(String order) {
        try{
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            try {
                
               
                String cust = ""; 
                String site = ""; 
                String site_csz = "";
                String bill_csz = "";
                String ship_csz = "";
                double taxes = getOrderTotalTax(order);
                res = st.executeQuery("select so_cust, so_site, " +
                        " cm_city, cm_state, cm_zip, cms_city, cms_state, cms_zip, site_city, site_state, site_zip " +
                        " from so_mstr " +
                        " inner join cm_mstr on cm_code = so_cust " +
                        " left outer join cms_det on cms_code = so_cust and cms_shipto = so_ship " +
                        " inner join site_mstr on site_site = so_site " +
                        " where so_nbr = " + "'" + order + "'" + ";");
                       while (res.next()) {
                          cust = res.getString(("so_cust"));
                          site = res.getString(("so_site"));
                          site_csz = res.getString(("site_city")) + " " + res.getString(("site_state")) + " " + res.getString(("site_zip"));
                          bill_csz = res.getString(("cm_city")) + " " + res.getString(("cm_state")) + " " + res.getString(("cm_zip"));
                          ship_csz = res.getString(("cms_city")) + " " + res.getString(("cms_state")) + " " + res.getString(("cms_zip"));
                       }
                
                
                String imagepath = "";
                String logo = "";
                logo = cusData.getCustLogo(cust);
                if (logo.isEmpty()) {
                    logo = OVData.getSiteLogo(site);
                }
                
                String jasperfile = "";
               jasperfile = OVData.getDefaultOrderJasper(site);
               
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "ORDER");
                hm.put("myid",  order);
                hm.put("site_csz", site_csz);
                hm.put("bill_csz", bill_csz);
                hm.put("ship_csz", ship_csz);
                hm.put("imagepath", imagepath);
                hm.put("taxes", taxes);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                 JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/orprt.pdf");
                
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        
    public static void printServiceOrder(String order) {
        try{
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            try {
                
               
                 String cust = ""; 
                String site = ""; 
                String type = "";
                String site_csz = "";
                String bill_csz = "";
                String ship_csz = "";
                res = st.executeQuery("select sv_cust, sv_site, sv_type, " +
                        " cm_city, cm_state, cm_zip, cms_city, cms_state, cms_zip, site_city, site_state, site_zip " +
                        " from sv_mstr " +
                        " inner join cm_mstr on cm_code = sv_cust " +
                        " left outer join cms_det on cms_code = sv_cust and cms_shipto = sv_ship " +
                        " inner join site_mstr on site_site = sv_site " +
                        " where sv_nbr = " + "'" + order + "'" + ";");
                       while (res.next()) {
                          cust = res.getString("sv_cust");
                          site = res.getString("sv_site");
                          type = res.getString("sv_type");
                          site_csz = res.getString(("site_city")) + " " + res.getString(("site_state")) + " " + res.getString(("site_zip"));
                          bill_csz = res.getString(("cm_city")) + " " + res.getString(("cm_state")) + " " + res.getString(("cm_zip"));
                          ship_csz = res.getString(("cms_city")) + " " + res.getString(("cms_state")) + " " + res.getString(("cms_zip"));
                       }
                
                
                String imagepath = "";
                String logo = "";
                logo = cusData.getCustLogo(cust);
                if (logo.isEmpty()) {
                    logo = OVData.getSiteLogo(site);
                }
               
               String jasperfile = "serviceorder.jasper";
               
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                if (type.equals("order")) {
                hm.put("REPORT_TITLE", "SERVICE ORDER");
                } else {
                hm.put("REPORT_TITLE", "QUOTE");    
                }
                hm.put("myid",  order);
                hm.put("site_csz", site_csz);
                hm.put("bill_csz", bill_csz);
                hm.put("ship_csz", ship_csz);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                 JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/svprt.pdf");
                
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        
    public static void printBOMJasper(String item, String bom, String matcost, String opcost, String curcost) {
        try{
             
            try (Connection con = DriverManager.getConnection(url + db, user, pass)) {
                                
                String imagepath = "";
                String logo = "";
              
                logo = OVData.getSiteLogo(OVData.getDefaultSite());
                
                
               String jasperfile = "bom_generic.jasper";
               //jasperfile = OVData.getDefaultOrderJasper(site);
               
               imagepath = "images/" + logo;
                HashMap hm = new HashMap();
               
                hm.put("REPORT_TITLE", "BOM RPT");    
                
                hm.put("myid",  item);
                hm.put("mybom",  bom);
                hm.put("matcost",  matcost);
                hm.put("opcost",  opcost);
                hm.put("curcost",  curcost);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                 JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/bomprt.pdf");
                
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
                
            } catch (Exception s) {
                MainFrame.bslog(s);
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }    
        
    public static void printLabelItem(String item, String printer) throws IOException, PrintException {
          String this_printer = "";
          try {

          if (printer.isEmpty()) {
              this_printer = OVData.getDefaultLabelPrinter();
          } else {
              this_printer = printer;
          }
          
          if (this_printer.isEmpty())
              return;
          
      
        String[] prt = OVData.getPrinterInfo(this_printer);
        if (prt[2].equals("DirectToIP") && prt[1].isEmpty()) {
            prt[1] = "9100";
        }

        BufferedReader fsr = new BufferedReader(new FileReader(new File("zebra/item.prn")));
        String line = "";
        String concatline = "";

        while ((line = fsr.readLine()) != null) {
            concatline += line;
        }
        fsr.close();
        // fos.write(concatline.getBytes());

        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");

        concatline = concatline.replace("$ITEMNBR", item);

         if (prt[2].equals("DirectToIP")) {
            Socket soc = null;
            DataOutputStream dos = null;
             soc = new Socket(prt[0], Integer.valueOf(prt[1]));
                    dos= new DataOutputStream(soc.getOutputStream());
                    dos.writeBytes(concatline);

             dos.close();
             soc.close();
            }
            
            if (prt[2].equals("NetworkShare")) {
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
             pras.add(new Copies(1));
             InputStream stream = new ByteArrayInputStream(concatline.getBytes(StandardCharsets.UTF_8));
             Doc doc = new SimpleDoc(stream, DocFlavor.INPUT_STREAM.AUTOSENSE,null);
             PrintService service = null;
             PrintService[] services = PrinterJob.lookupPrintServices();
              for (int index = 0; service == null && index < services.length; index++) {
                    if (services[index].getName().equalsIgnoreCase(prt[0])) {

                        service = services[index];
                    }
                }
             if (service != null) { 
             DocPrintJob job = service.createPrintJob();
             job.print(doc, pras);
             } 
            }
 
} catch (Exception e) {
MainFrame.bslog(e);
}
      }
      
    public static void printLabelStream(String text, String printer) throws IOException, PrintException {
          
          String[] prt = OVData.getPrinterInfo(printer);
            if (prt[2].equals("DirectToIP") && prt[1].isEmpty()) {
                prt[1] = "9100";
            }
          
          if (prt[2].equals("DirectToIP")) {
            Socket soc = null;
            DataOutputStream dos = null;
             soc = new Socket(prt[0], Integer.valueOf(prt[1]));
                    dos= new DataOutputStream(soc.getOutputStream());
                    dos.writeBytes(text);

             dos.close();
             soc.close();
            }
            
            if (prt[2].equals("NetworkShare")) {
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
             pras.add(new Copies(1));
             InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
             Doc doc = new SimpleDoc(stream, DocFlavor.INPUT_STREAM.AUTOSENSE,null);
             PrintService service = null;
             PrintService[] services = PrinterJob.lookupPrintServices();
              
                for (int index = 0; service == null && index < services.length; index++) {
                    if (services[index].getName().equalsIgnoreCase(prt[0])) {
                        service = services[index];
                    }
                }
             if (service != null) {  
             DocPrintJob job = service.createPrintJob(); 
             job.print(doc, pras);
             } 
            }
      }
    
    public static void printQPR(String id) throws SQLException, JRException {
       
                Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
                String imagepath = "";
                String logo = "";
                logo = OVData.getSiteLogo(OVData.getDefaultSite());
                String jasperfile = "";
                jasperfile = "qpr.jasper";
               
                imagepath = "images/" + logo;
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "QPR");
                hm.put("myid",  id);
                hm.put("imagepath", imagepath);
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile); 
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, con );
                 JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/qprprt.pdf");
                
                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
                jasperViewer.setFitPageZoomRatio();
                
          
    }    
     
    
    public static MimeBodyPart attachmentPart;
    
    public static class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
           String[] smtpcreds = getSMTPCredentials(); 
           String username = smtpcreds[2];
           String password = smtpcreds[3];
           return new PasswordAuthentication(username, password);
        }
    }
    
    public static String[] getSMTPCredentials() {
         String[] x = new String[4];
         for (int i = 0; i < x.length; i++) {
             x[i] = "";
         }
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select * from ov_ctrl;" );
               while (res.next()) {
                x[0] = res.getString("ov_email_server");   
                x[1] = res.getString("ov_email_from"); 
                x[2] = res.getString("ov_smtpauthuser"); 
                x[3] = bsmf.MainFrame.PassWord("1", res.getString("ov_smtpauthpass").toCharArray()); 
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
          
    public static String[] isSMTPServer() {
        String[] x = new String[]{"0",""};
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select * from ov_ctrl;" );
               while (res.next()) {
                   if (res.getString("ov_email_server").isEmpty()) {
                    x[0] = "1";
                    x[1] = getMessageTag(1154);
                   }
                   if (res.getString("ov_email_from").isEmpty()) {
                    x[0] = "1";
                    x[1] = getMessageTag(1155);
                   }
                   if (res.getString("ov_smtpauthuser").isEmpty()) {
                    x[0] = "1";
                    x[1] = getMessageTag(1156);
                   }
                   if (res.getString("ov_smtpauthpass").isEmpty()) {
                    x[0] = "1";
                    x[1] = getMessageTag(1157);
                   }
                     
                }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
    
    public static void sendEmail(String to, String subject, String body, String filename) {

  // Strings that contain from, to, subject, body and file path to the attachment

      String[] smtpcreds = getSMTPCredentials();
      String from = smtpcreds[1];
      String emailserver = smtpcreds[0];


      if (emailserver.isEmpty() || from.isEmpty()) {
          return;
      }
      // Set smtp properties
      Properties properties = new Properties();
      properties.put("mail.smtp.host", emailserver);
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      Authenticator auth = new SMTPAuthenticator();
      Session session = Session.getDefaultInstance(properties, auth);

  try {
   
    MimeMessage message = new MimeMessage(session);

    message.setFrom(new InternetAddress(from));

    message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

    message.setSubject(subject);

    message.setSentDate(new Date());

// Set the email body

    MimeBodyPart messagePart = new MimeBodyPart();

    messagePart.setText(body);

// Set the email attachment file

    if (! filename.isEmpty()) {
            attachmentPart = new MimeBodyPart();
            FileDataSource fileDataSource = new FileDataSource(filename) {

                @Override

                public String getContentType() {

              return "application/octet-stream";

                }

            };
            attachmentPart.setDataHandler(new DataHandler(fileDataSource));
            attachmentPart.setFileName(fileDataSource.getName());
    }
// Add all parts of the email to Multipart object

    Multipart multipart = new MimeMultipart();

    multipart.addBodyPart(messagePart);
     if (! filename.isEmpty()) {
     multipart.addBodyPart(attachmentPart);
     }
    message.setContent(multipart);

    // Send email

    Transport.send(message);

    } catch (MessagingException e) {
      MainFrame.bslog(e);
    }
 }
         
    public static void sendEmailByID(String to, String subject, String body, String filename) {
	 
	
          String[] smtpcreds = getSMTPCredentials();
          String from = smtpcreds[1];
          String emailserver = smtpcreds[0];
  
          if (emailserver.isEmpty() || from.isEmpty()) {
              return;
          }
        
        
        
        String ToEmail = "";
                    
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select user_email from user_mstr where user_id = " + "'" + to + "';");
               while (res.next()) {
                   ToEmail = (res.getString("user_email"));
                }
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }


	 
	  // Set smtp properties
	 
	  Properties properties = new Properties();
	 
	
	 
	 properties.put("mail.smtp.host", emailserver);
	 
	  Session session = Session.getDefaultInstance(properties, null);
	 
	  try {
	 
	MimeMessage message = new MimeMessage(session);
	 
	message.setFrom(new InternetAddress(from));
	 
	message.setRecipient(Message.RecipientType.TO, new InternetAddress(ToEmail));
	 
	message.setSubject(subject);
	 
	message.setSentDate(new Date());
	 
	// Set the email body
	 
	MimeBodyPart messagePart = new MimeBodyPart();
	 
	messagePart.setText(body);
	 
	// Set the email attachment file
	
        if (! filename.isEmpty()) {
        
	attachmentPart = new MimeBodyPart();
	 
	FileDataSource fileDataSource = new FileDataSource(filename) {
	 
	    @Override
	 
	    public String getContentType() {
	 
	  return "application/octet-stream";
	 
	    }
	 
	};
	 
	attachmentPart.setDataHandler(new DataHandler(fileDataSource));
	 
	attachmentPart.setFileName(fileDataSource.getName());
        }
	// Add all parts of the email to Multipart object
	 
	Multipart multipart = new MimeMultipart();
	 
	multipart.addBodyPart(messagePart);
	 if (! filename.isEmpty()) {
	 multipart.addBodyPart(attachmentPart);
         }
	message.setContent(multipart);
	 
	// Send email
	
	Transport.send(message);
	 
	  } catch (MessagingException e) {
	 
	MainFrame.bslog(e);
	 
	  }
	    }
     
    public static String[] sendInvoice(String invoice, String site) {
        String[] m = new String[]{"0","email transmitted"};
        printInvoice(invoice, false);
        String siteinfo[] = getSiteAddressArray(site);
        String filename = "temp/ivprt.pdf";
        String subject = "Automated communication from " + siteinfo[1];
        String body = "This is an automated delivery for invoice number: " + invoice + "." + "\n";
        body += "The attachment contains the details of the invoice in pdf format.";
        body += "\n\n";
        body += "Thank you, \n";
        body += "\n\n";
        body += siteinfo[1] + "\n";
        body += siteinfo[2] + "\n";
        body += siteinfo[5] + ", " + siteinfo[6] + " " + siteinfo[7] + "\n";
        body += siteinfo[8] + "\n";
        
        
        String custEmail = "";
        custEmail = cusData.getCustEmailByInvoice(invoice);
        File file = new File("temp/ivprt.pdf");
        if (file.exists()) {
            if (custEmail == null || custEmail.isEmpty()) {
               m[0] = "1";
               m[1] = "customer email not defined";
            } else {
               sendEmail(custEmail, subject, body, filename); 
            }
        } else {
            m[0] = "1";
            m[1] = "unable to locate attachment file";
        }
        return m;
        
    }

    public static String CreateVoucher(JTable voucherdet, String site, String vend, String invoice, Date effdate, String remarks ) {
      String messg = "";
      String nbr = String.valueOf(OVData.getNextNbr("voucher"));

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
            boolean proceed = true;
            int i = 0;
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();

            // initialize ord and due date if blank
            if ( effdate == null) {
                effdate = now;
            }


            Double amt = 0.00;
            Double baseamt = 0.00;
            double qty = 0;


            // get billto specific data
            String apacct = "";
            String apcc = "";
            String terms = "";
            String carrier = "";
            String apbank = "";
            String curr = "";
            String basecurr = "";

           res = st.executeQuery("select vd_ap_acct, vd_ap_cc, vd_terms, vd_bank, vd_curr from vd_mstr where vd_addr = " + "'" + vend + "'" + ";");
            while (res.next()) {
                i++;
               apacct = res.getString("vd_ap_acct");
               apcc = res.getString("vd_ap_cc");
               terms = res.getString("vd_terms");
               apbank = res.getString("vd_bank");
               curr = res.getString("vd_curr");
            }

            if (i == 0) {
                messg = "vendor unknown...unable to autovoucher";
                proceed = false;
            }

            Date duedate = OVData.getDueDateFromTerms(effdate, terms);
            if (duedate == null) {
                duedate = now;
            }



            if (proceed) {
             // "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine"
              //  "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "WH", "serial", "lot", "cost"
              for (int j = 0; j < voucherdet.getRowCount(); j++) {
                    qty = bsParseDouble(voucherdet.getValueAt(j,3).toString());
                    amt += bsParseDoubleUS(voucherdet.getValueAt(j, 3).toString()) * bsParseDoubleUS(voucherdet.getValueAt(j, 4).toString());
                    st.executeUpdate("insert into vod_mstr "
                        + "(vod_id, vod_vend, vod_rvdid, vod_rvdline, vod_item, vod_qty, "
                        + " vod_voprice, vod_date, vod_invoice, vod_expense_acct, vod_expense_cc )  "
                        + " values ( " + "'" + nbr + "'" + ","
                            + "'" + vend + "'" + ","
                        + "'" + voucherdet.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + voucherdet.getValueAt(j, 1).toString() + "'" + ","
                        + "'" + voucherdet.getValueAt(j, 2).toString() + "'" + ","
                        + "'" + currformatUS(voucherdet.getValueAt(j, 3).toString()) + "'" + ","
                        + "'" + currformatUS(voucherdet.getValueAt(j, 4).toString()) + "'" + ","
                        + "'" + dfdate.format(effdate) + "'" + ","
                        + "'" + invoice + "'" + ","
                        + "'" + apacct + "'" + ","
                        + "'" + apcc + "'"
                        + ")"
                        + ";");

            double voqty = 0.00;
            double rvqty = 0.00;
            double rvdvoqty = 0.00;
            String status = "0";

            res = st.executeQuery("select rvd_voqty, rvd_qty from recv_det " 
                     + " where rvd_id = " + "'" + voucherdet.getValueAt(j, 0).toString() + "'"
                    + " AND rvd_rline = " + "'" + voucherdet.getValueAt(j, 1).toString() + "'"
                    );
            while (res.next()) {
                voqty = res.getDouble("rvd_voqty");
                rvqty = res.getDouble("rvd_qty");
                if ((voqty + qty) >= rvqty) {
                    status = "1";
                }     
            }
            res.close();        

               rvdvoqty = voqty + qty;


                       if (dbtype.equals("sqlite")) { 
                        st.executeUpdate("update recv_det  "
                        + " set rvd_voqty =  " + "'" + rvdvoqty + "'" + ","
                        + " rvd_status = " + "'" + status + "'"
                        + " where rvd_id = " + "'" + voucherdet.getValueAt(j, 0).toString() + "'"
                        + " AND rvd_rline = " + "'" + voucherdet.getValueAt(j, 1).toString() + "'"
                        );
                       } else {
                        st.executeUpdate("update recv_det as r1 inner join recv_det as r2 "
                        + " set r1.rvd_voqty = r2.rvd_voqty + " +  "'" + qty + "'" + ","
                        + " r1.rvd_status = case when r1.rvd_qty <= ( r2.rvd_voqty + " + "'" + qty + "'" +  ") then '1' else '0' end " 
                        + " where r1.rvd_id = " + "'" + voucherdet.getValueAt(j, 0).toString() + "'"
                        + " AND r1.rvd_rline = " + "'" + voucherdet.getValueAt(j, 1).toString() + "'"
                        + " AND r2.rvd_id = " + "'" + voucherdet.getValueAt(j, 0).toString() + "'"
                        + " AND r2.rvd_rline = " + "'" + voucherdet.getValueAt(j, 1).toString() + "'"
                        );   
                       }

                 }  //end of for each line


              // let's handle the currency exchange...if any
                basecurr = OVData.getDefaultCurrency();


                if (curr.toUpperCase().equals(basecurr.toUpperCase())) {
                    baseamt = amt;
                } else {
                    baseamt = OVData.getExchangeBaseValue(basecurr, curr, amt);
                }

            // now the header voucher
                 st.executeUpdate("insert into ap_mstr "
                    + "(ap_vend, ap_site, ap_nbr, ap_amt, ap_base_amt, ap_type, ap_ref, ap_rmks, "
                    + "ap_entdate, ap_effdate, ap_duedate, ap_acct, ap_cc, "
                    + "ap_terms, ap_status, ap_bank, ap_curr, ap_base_curr ) "
                    + " values ( " + "'" + vend + "'" + ","
                          + "'" + site + "'" + ","
                    + "'" + nbr + "'" + ","
                    + "'" + currformatDoubleUS(amt) + "'" + ","
                    + "'" + currformatDoubleUS(baseamt) + "'" + ","        
                    + "'" + "V" + "'" + ","
                    + "'" + invoice + "'" + ","
                    + "'" + remarks + "'" + ","
                    + "'" + dfdate.format(now) + "'" + ","
                    + "'" + dfdate.format(effdate) + "'" + ","
                    + "'" + dfdate.format(duedate) + "'" + ","
                    + "'" + apacct + "'" + ","
                    + "'" + apcc + "'" + ","
                    + "'" + terms + "'" + ","
                    + "'" + "o" + "'"  + ","
                    + "'" + apbank + "'" + ","
                    + "'" + curr + "'" + ","
                    + "'" + basecurr + "'"        
                    + ")"
                    + ";");

                 fglData.glEntryFromVoucher(nbr, effdate);



        } // if proceed       


        } catch (SQLException s) {
            MainFrame.bslog(s);
        }
        con.close();
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
      return messg;
  } 

    
    public static String[] CreateSalesOrderByJSON(String jsonString) {
      String[] x = new String[]{"","",""};
      boolean isError = false; 
      JSONObject json = new JSONObject(jsonString);
      String junktag = "";
      String PONumber = "";
      String Remarks = "";
      String OrderDate = "";
      String DueDate = "";
      String Currency = "";
      String Type = "";
      String ShipVia = "";
      String BillToCode = "";
      String BillToName = "";
      String BillToAddr1 = "";
      String BillToCity = "";
      String BillToState = "";
      String BillToZip = "";
      String BillToCountry = "";
      String BillToEmail = "";
      String BillToPhone = "";
      String ShipToCode = "";
      String ShipToName = "";
      String ShipToAddr1 = "";
      String ShipToCity = "";
      String ShipToState = "";
      String ShipToZip = "";
      String ShipToCountry = "";
      ArrayList<String[]> detail = new ArrayList<String[]>();


      for (String keyStr : json.keySet()) { 
       Object keyvalue = json.get(keyStr);

       // process header tags in JSON
       switch(keyStr) {
             case "PONumber" :
                 PONumber = keyvalue.toString();
                 break;
             case "Remarks" :
                 Remarks = keyvalue.toString();
                 break;
             case "OrderDate" :
                 OrderDate = keyvalue.toString();
                 break;
             case "DueDate" :
                 DueDate = keyvalue.toString();
                 break;
             case "Currency" :
                 Currency = keyvalue.toString();
                 break;
             case "Type" :
                 Type = keyvalue.toString();
                 break; 
             case "ShipVia" :
                 ShipVia = keyvalue.toString();
                 break;    
             case "BillToCode" :
                 BillToCode = keyvalue.toString();
                 break;
             case "BillToName" :
                 BillToName = keyvalue.toString();
                 break;
             case "BillToAddr1" :
                 BillToAddr1 = keyvalue.toString();
                 break;
             case "BillToCity" :
                 BillToCity = keyvalue.toString();
                 break;
             case "BillToState" :
                 BillToState = keyvalue.toString();
                 break;
             case "BillToZip" :
                 BillToZip = keyvalue.toString();
                 break; 
             case "BillToCountry" :
                 BillToCountry = keyvalue.toString();
                 break;
             case "BillToEmail" :
                 BillToEmail = keyvalue.toString();
                 break;
             case "BillToPhone" :
                 BillToPhone = keyvalue.toString();
                 break;
             case "ShipToCode" :
                 ShipToCode = keyvalue.toString();
                 break;
             case "ShipToName" :
                 ShipToName = keyvalue.toString();
                 break;
             case "ShipToAddr1" :
                 ShipToAddr1 = keyvalue.toString();
                 break;
             case "ShipToCity" :
                 ShipToCity = keyvalue.toString();
                 break;
             case "ShipToState" :
                 ShipToState = keyvalue.toString();
                 break;
             case "ShipToZip" :
                 ShipToZip = keyvalue.toString();
                 break; 
             case "ShipToCountry" :
                 ShipToCountry = keyvalue.toString();
                 break;     
             default :
                 junktag = keyvalue.toString();
        }

       // process detail array 'Items' in JSON
       if (keyStr.equals("Items")) {
            for (Object line : (JSONArray) keyvalue) {
                JSONObject jsonDetail = new JSONObject(line.toString());

                String ItemNumber = "";
                String Line = "";
                String OrderQty = "";
                String UOM = "";
                String CustItem = "";
                String ListPrice = "";
                String NetPrice = "";
                String Discount = "";
                String junktagdet = "";

                for (String detailKey : jsonDetail.keySet()) {
                    Object detailValue = jsonDetail.get(detailKey);
                    switch(detailKey) {
                         case "ItemNumber" :
                             ItemNumber = detailValue.toString();
                             break;
                         case "Line" :
                             Line = detailValue.toString();
                             break;
                         case "OrderQty" :
                             OrderQty = detailValue.toString();
                             break;
                         case "UOM" :
                             UOM = detailValue.toString();
                             break;
                         case "CustItem" :
                             CustItem = detailValue.toString();
                             break;
                         case "ListPrice" :
                             ListPrice = detailValue.toString();
                             break;  
                         case "NetPrice" :
                             NetPrice  = detailValue.toString();
                             break;  
                         case "Discount" :
                             Discount = detailValue.toString();
                             break;      
                         default :
                             junktagdet = detailValue.toString();
                    }
                }
                detail.add(new String[]{ItemNumber, Line, OrderQty, UOM,CustItem,ListPrice,NetPrice,Discount});

            }
       } // if key = "Items"

      }




       try {


        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }

        try {
            Statement st = con.createStatement();
            ResultSet res = null;
            boolean proceed = true;
            int i = 0;
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();


            // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
            String[] custinfo = new String[]{"","","","","","","", ""};

            // if billto exists...use it...otherwise create unique billto/shipto
            res = st.executeQuery("select * from cm_mstr where cm_code = " + "'" + BillToCode + "'" + " ;");
           while (res.next()) {
               i++;
               custinfo[0] = res.getString("cm_ar_acct");
               custinfo[1] = res.getString("cm_ar_cc");
               custinfo[2] = res.getString("cm_curr");
               custinfo[3] = res.getString("cm_bank");
               custinfo[4] = res.getString("cm_terms");
               custinfo[5] = res.getString("cm_carrier");
               custinfo[6] = res.getString("cm_onhold");
               custinfo[7] = res.getString("cm_site");
            }
           if (i == 0) {
               // create billto/shipto on the fly and assign return to custcode
               BillToCode = String.valueOf(OVData.getNextNbr("customer"));
                // set String array of address info for creation of billto/shipto
               String[] addr = new String[]{BillToCode, BillToName, BillToAddr1, "", "", BillToCity, BillToState, BillToZip, BillToCountry, BillToEmail, BillToPhone, ShipToName, ShipToAddr1, "", "", ShipToCity, ShipToState, ShipToZip, ShipToCountry};  
               custinfo = OVData.addCustMstrWShipToMinimal(addr);
           }


            String nbr = String.valueOf(OVData.getNextNbr("order"));

            if (proceed) {
                st.executeUpdate("insert into so_mstr "
                    + "(so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, "
                    + "so_create_date, so_userid, so_status,"
                    + "so_rmks, so_terms, so_ar_acct, so_ar_cc, so_shipvia, so_type, so_site, so_onhold ) "
                    + " values ( " + "'" + nbr + "'" + ","
                    + "'" + BillToCode + "'" + ","
                    + "'" + BillToCode + "'" + ","
                    + "'" + PONumber + "'" + ","
                    + "'" + OrderDate + "'" + ","
                    + "'" + DueDate + "'" + ","
                    + "'" + dfdate.format(now) + "'" + ","
                    + "'" + "api" + "'" + ","
                    + "'" + getGlobalProgTag("open") + "'" + ","
                    + "'" + Remarks + "'" + ","
                    + "'" + custinfo[4] + "'" + ","
                    + "'" + custinfo[0] + "'" + ","
                    + "'" + custinfo[1] + "'" + ","
                    + "'" + custinfo[5] + "'" + ","
                    + "'DISCRETE'" + ","
                    + "'" + custinfo[7] + "'" + ","
                    + "'" + custinfo[6] + "'"
                    + ")"
                    + ";");

              //  detail.add(new String[]{ItemNumber, Line, OrderQty, UOM,CustItem,ListPrice,NetPrice,Discount});
                for (String[] s : detail) {
                st.executeUpdate("insert into sod_det "
                        + "(sod_nbr, sod_item, sod_site, sod_po, sod_ord_qty, sod_netprice, sod_listprice, sod_disc, sod_due_date, "
                        + "sod_shipped_qty, sod_custitem, sod_status, sod_line) "
                        + " values ( " + "'" + nbr + "'" + ","
                        + "'" + s[0] + "'" + ","
                        + "'" + custinfo[7] + "'" + ","
                        + "'" + PONumber + "'" + ","
                        + "'" + s[2].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + s[6].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + s[5].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + s[7].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + DueDate + "'" + ","
                        + '0' + "," + "'" + s[4] + "'" +  "," 
                        + "'" + getGlobalProgTag("open") + "'" + ","
                        + "'" + s[1] + "'"
                        + ")"
                        + ";");
                }

                x[0] = "success";
                x[1] = "Loaded Sales Order Successfully";
                x[2] = nbr;

            } // if proceed
            else {
                x[0] = "fail";
                x[1] = "unable to process";
            }
        } catch (SQLException s) {
            x[0] = "fail";
            x[1] = "unable to load order SQLException";
            MainFrame.bslog(s);
        }
        con.close();
    } catch (Exception e) {
        MainFrame.bslog(e);
    }



      return x;
  }

    public static void CreateSalesOrderSchedDet(String order, String line, String date, String qty, String type, String ref, String rlse) {
      String item = "";
      String po = "";
      try {

        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }

        try {
            Statement st = con.createStatement();
            ResultSet res = null;
            boolean proceed = true;
            int i = 0;

            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
             // Rule 1:  if no blanket order,line,status=open for this part then proceed = false
            i = 0;
            res = st.executeQuery("select sod_item, sod_po from sod_det where sod_nbr = " + "'" + order + "'" 
                     + " AND sod_line = " + "'" + line + "'" 
                     + " AND sod_status = " + "'" + getGlobalProgTag("open") + "'"                           
                     + " ;");
               while (res.next()) {
                   i++;
               item = res.getString("sod_item");
               po = res.getString("sod_po");
               }
               if (i == 0) {
                   proceed = false;
                   return;
               }

               // Rule 2:  if schedule so,po,part,line,Ref,DueDate,Type already exists then proceed = false
               i = 0;
               res = st.executeQuery("select * from srl_mstr where srl_so = " + "'" + order + "'" 
                        + " AND srl_line = " + "'" + line + "'" 
                        + " AND srl_item = " + "'" + item + "'" 
                        + " AND srl_po = " + "'" + po + "'" 
                        + " AND srl_ref = " + "'" + ref + "'" 
                        + " AND srl_duedate = " + "'" + date + "'" 
                        + " AND srl_type = " + "'" + type + "'"
                        + " ;");
               while (res.next()) {
              i++;
               }
                if (i > 0) {
                   proceed = false;
                   return;
               }



            if (proceed) {

                    st.executeUpdate("insert into srl_mstr "
                        + " (srl_so, srl_po, srl_line, srl_item, srl_ref, srl_qtyord, srl_duedate, srl_crtdate, "
                        + "srl_type, srl_rlse ) "
                        + " values ( " + "'" + order + "'" + ","
                        + "'" + po + "'" + ","
                        + "'" + line + "'" + ","
                        + "'" + item + "'" + ","
                        + "'" + ref + "'" + ","
                        + "'" + qty + "'" + ","
                        + "'" + date + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + type + "'" + ","
                        + "'" + rlse + "'"
                        + ")"
                        + ";");
            } // if proceed
        } catch (SQLException s) {
            MainFrame.bslog(s);
        }
        con.close();
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
  }
    
    public static boolean isInvCtrlSerialize() {
   boolean myreturn = false;
    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select serialize from inv_ctrl;");
           while (res.next()) {
               myreturn = res.getBoolean("serialize");
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    
    public static boolean isInvCtrlPlanMultiScan() {
   boolean myreturn = false;
    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select planmultiscan from inv_ctrl;");
           while (res.next()) {
               myreturn = res.getBoolean("planmultiscan");
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static boolean isPrintTicketFromPlanScan() {
   boolean myreturn = false;
    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select printsubticket from inv_ctrl;");
           while (res.next()) {
               myreturn = res.getBoolean("printsubticket");
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static boolean isInvCtrlDemdToPlan() {
   boolean myreturn = false;
    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

            res = st.executeQuery("select demdtoplan from inv_ctrl;");
           while (res.next()) {
               myreturn = res.getBoolean("demdtoplan");
           }
       }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static boolean isPlanDetByOp(String serialno, String op) {

      // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
      // assume it's false i.e. hasn't been scanned.
      boolean myreturn = false;

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
             res = st.executeQuery("select * from pland_mstr where pland_parent = " + "'" + serialno + "'" 
                     + " AND pland_op = " + "'" + op + "'"
                     + " ;");
           while (res.next()) {
               myreturn = true;
           }

        } catch (SQLException s) {
            MainFrame.bslog(s);
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
      return myreturn;
  }

    public static int CreatePlanDet(JTable mytable) {
  /*  0=part
      1=type 
      2=operation
      3=qty
      4=effdate
      5=location
      6=serialno
      7=reference
      8=site
      9=userid
      10=prodline
      11=AssyCell
      12=remarks
      13=PackCell
      14=packdate
      15=assydate
      16=program
     */ 
     int planid = 0; 

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
            
            String _part = "";
            String _parent = "";
            String _op = "";
            double _qty = 0;
         
              for (int i = 0; i < mytable.getRowCount(); i++) {
                  _parent = mytable.getValueAt(i, 6).toString();
                  _part = mytable.getValueAt(i, 0).toString();
                  _qty = bsParseDouble(mytable.getValueAt(i,3).toString());
                  _op = mytable.getValueAt(i, 2).toString();
                  planid = OVData.getNextNbr("pland");
                    st.executeUpdate("insert into pland_mstr "
                        + " (pland_id, pland_parent, pland_item, pland_op, pland_qty, pland_cell, pland_ref, pland_date ) "
                        + " values ( " + "'" + planid + "'" + ","  
                        + "'" + _parent + "'" + ","
                        + "'" + _part + "'" + ","
                        + "'" + _op + "'" + ","
                        + "'" + _qty + "'" + ","
                        + "'" + mytable.getValueAt(i, 11).toString() + "'" + ","
                        + "'" + mytable.getValueAt(i, 7).toString() + "'" + ","
                        + "'" + mytable.getValueAt(i, 4).toString() + "'" 
                        + ")"
                        + ";");

                    // if multiscan then close only when plan sched qty = scanned qty exactly
                    double scanqty = schData.getPlanDetTotQtyByOp(_parent, _op);
                    double schedqty = schData.getPlanSchedQty(_parent);
                    if ((scanqty >= schedqty) && OVData.isLastOperation(_part, _op) ) {
                     schData.updatePlanQty(_parent, scanqty);
                     schData.updatePlanStatus(_parent, "1");   
                    }


                    // if not multiscan then close plan order....one scan per planned order regardless if they produced sched qty exactly
                    if (! OVData.isInvCtrlPlanMultiScan() && OVData.isLastOperation(_part, _op)) {
                    schData.updatePlanQty(_parent, _qty);
                    schData.updatePlanStatus(_parent, "1");
                    }
              }

        } catch (SQLException s) {
            MainFrame.bslog(s);
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

      return planid;
  }

    public static void CreateItemMasterImport(JTable mytable) {
  /*  0=part
      1=desc 
      2=uom
      3=prodline
      4=type
      5=site
      6=location
      7=code
     */ 


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
            boolean proceed = true;
            String _part = "";
            String _parent = "";
            String _op = "";
            int _qty = 0;


            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
              for (int i = 0; i < mytable.getRowCount(); i++) {

                    st.executeUpdate("insert into item_mstr "
                        + " (it_item, it_desc, it_uom, it_prodline, it_type, it_site, it_loc, it_code ) "
                        + " values ( " + "'" + mytable.getValueAt(i, 0).toString() + "'" + ","  
                        + "'" + mytable.getValueAt(i, 1).toString() + "'" + ","
                        + "'" + mytable.getValueAt(i, 2).toString() + "'" + ","
                        + "'" + mytable.getValueAt(i, 3).toString() + "'" + ","
                        + "'" + mytable.getValueAt(i, 4).toString() + "'" + ","
                        + "'" + mytable.getValueAt(i, 5).toString() + "'" + ","
                        + "'" + mytable.getValueAt(i, 6).toString() + "'" + ","
                        + "'" + mytable.getValueAt(i, 7).toString() + "'" 
                        + ")"
                        + ";");
              }

        } catch (SQLException s) {
            MainFrame.bslog(s);
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

  
    
      
   
      
    public static void locTolocTransfer(String item, int qty, String user) throws ParseException {
          java.util.Date now = new java.util.Date();
          DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
          String op = "";
          OVData.TRHistIssDiscrete(now, 
                  item, 
                  qty, 
                  op,
                  "LOC-TRNF", 
                  0.00, 
                  0.00, 
                OVData.getDefaultSite(), 
                "locA",  // location
                "", // warehouse
                "", // expire
                "", 
                "", 
                "", 
                0, 
                "", 
                "", 
                "", // lot 
                "locA transfer", //rmks
                "locA", //ref
                "", 
                "", 
                "", 
                "locA",  // serial
                "someProg", // program
                user
                );
         
        
          OVData.UpdateInventoryDiscrete(item, "BS", "locA", "warehouse", "", "", (-1 * bsParseDouble(String.valueOf(qty))));
          OVData.UpdateInventoryDiscrete(item, "BS", "locB", "warehouse", "", "", (bsParseDouble(String.valueOf(qty)))); 
               
      }
     
    public static String CreateShipTo(String billtocode, String name, String line1, String line2, String line3, String city, String state, String zip, String country, String shipto) {
          String shiptocode = "";
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
                boolean proceed = true;
                int i = 0;
                if (! shipto.isEmpty()) {
                 res = st.executeQuery("select * from cms_det where cms_code = " + "'" + billtocode + "'" 
                         + " AND cms_shipto = " + "'" + shipto + "'"
                         + " ;");
                       while (res.next()) {
                           i++;
                           shiptocode = res.getString("cms_shipto");
                       }
                }
                
                if (i == 0) {
                    shiptocode = String.valueOf(getNextNbr("shipto"));
                    st.executeUpdate("insert into cms_det "
                        + "(cms_code, cms_shipto, cms_name, cms_line1, cms_line2, "
                        + "cms_line3, cms_city, cms_state, cms_zip, "
                        + "cms_country, cms_plantcode "
                        + " ) "
                        + " values ( " + "'" + billtocode + "'" + ","
                        + "'" + shiptocode + "'" + ","
                        + "'" + name.replace("'", "") + "'" + ","
                        + "'" + line1.replace("'", "") + "'" + ","
                        + "'" + line2.replace("'", "") + "'" + ","
                        + "'" + line3.replace("'", "") + "'" + ","
                        + "'" + city + "'" + ","
                        + "'" + state + "'" + ","
                        + "'" + zip + "'" + ","
                            + "'" + country + "'" + ","
                        + "'" + shipto + "'"                           
                        + ")"
                        + ";");
                } // if i == 0
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
          return shiptocode;
      }
     
    public static void updateOrderStatusError(String nbr) {
            try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                           st.executeUpdate(
                                 " update so_mstr set so_status = 'error' " +
                                 " where so_nbr = " + "'" + nbr + "'" + ";" );
            }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
       }
      
    public static void updateOrderSourceFlag(String nbr) {
            try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                           st.executeUpdate(
                                 " update so_mstr set so_issourced = '1' " +
                                 " where so_nbr = " + "'" + nbr + "'" + ";" );
            }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
       }
      
    public static void updateFreightOrderQuoteFlag(String nbr) {
            try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                           st.executeUpdate(
                                 " update fo_mstr set fo_isquoted = '1', fo_status = 'Quoted' " +
                                 " where fo_nbr = " + "'" + nbr + "'" + ";" );
            }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
       }
        
    public static void updateFreightOrderTenderFlag(String nbr) {
            try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                           st.executeUpdate(
                                 " update fo_mstr set fo_istendered = '1', fo_status = 'Tendered' " +
                                 " where fo_nbr = " + "'" + nbr + "'" + ";" );
            }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
       }
        
    public static void updateFreightOrderReasonCode(String nbr, String reasoncode) {
            try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                           st.executeUpdate(
                                 " update fo_mstr set fo_reasoncode = " + "'" + reasoncode + "'" +
                                 " where fo_type = 'tender' and fo_nbr = " + "'" + nbr + "'" + ";" );
            }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
       }
       
    public static void updateFreightOrderStatus(String nbr, String status) {
        try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                       st.executeUpdate(
                             " update fo_mstr set fo_status = " + "'" + status + "'" +
                             " where fo_nbr = " + "'" + nbr + "'" + ";" );
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }

   }

    public static int getForecastWeek(Date thisdate) {
    int week = 0;
    Calendar cal = Calendar.getInstance();
    cal.setTime(thisdate);
    ArrayList<Date> fctdates = getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));
    DateFormat format = new SimpleDateFormat("MM/dd/yy");
        for (int i = 0; i < 52; i++) {
         if ((thisdate.after(fctdates.get(i)) && thisdate.before(fctdates.get(i + 1))) || thisdate.compareTo(fctdates.get(i)) == 0) {
             week = i;
             break;
         }
    }
   // return d.after(min) && d.before(max);

    return week + 1;
}

    public static int createPlanFromFcst(String fromsite, String tosite, String fromitem, String toitem) {

    int recnumber = 0;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    cal.getTime();
    int wk = getForecastWeek(cal.getTime());
    ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));


     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            Statement st2 = con.createStatement();
            ResultSet res2 = null;
            Statement st3 = con.createStatement();
            try {
           String item = "";
           String site = "";
           
             int k = 0;
            ArrayList a_part = new ArrayList();
           ArrayList a_qty =   new ArrayList();
           ArrayList a_duedate =   new ArrayList(); 

             if (fromitem.isEmpty()) {
                 fromitem = bsmf.MainFrame.lowchar;
             }
             if (toitem.isEmpty()) {
                 toitem = bsmf.MainFrame.hichar;
             }
              if (fromsite.isEmpty()) {
                 fromsite = bsmf.MainFrame.lowchar;
             }
             if (tosite.isEmpty()) {
                 tosite = bsmf.MainFrame.hichar;
             }

             int qty = 0;
              res = st.executeQuery("select * from fct_mstr " +
                      " inner join item_mstr on it_item = fct_item " +
                      " where fct_item >= " + "'" + fromitem + "'" +
                      " and fct_item <= " + "'" + toitem + "'" + 
                      " and fct_site >= " + "'" + fromsite + "'" + 
                      " and fct_site <= " + "'" + tosite + "'" + 
                      " and it_plan = '1' " +
                                     ";" );
                while (res.next()) {
                  // for this part, this site, and this 'next' 13 weeks...see what plan_mstr records exist

                 item = res.getString("fct_item");
                 site = res.getString("fct_site");


                    for (int j = wk; j < wk + 13 ; j++) {
                      qty = res.getInt(j + 3);
                      k = 0;
                     // bsmf.MainFrame.show(String.valueOf(wk) + "/" + String.valueOf(qty) + "/" + String.valueOf(j) + "/" + String.valueOf(dates.get(j - 1)));

                      if (qty == 0)
                          continue;


                      res2 = st2.executeQuery("select * from plan_mstr where plan_item = " + "'" + item + "'" +
                               " AND plan_site = " + "'" + site + "'" + 
                              " AND plan_date_due = " + "'" + df.format(dates.get(j - 1)) + "'" + 
                              " AND plan_type = 'FCST' " + ";");
                         while (res2.next()) {
                             k++;
                         }
                         if (k == 0) {
                             a_part.add(item);
                             a_qty.add(qty);
                             a_duedate.add(df.format(dates.get(j - 1)));
                         }
                  }  // for 1 to 13

                }  // while res

                 // adjustment for sqlite
                 int nbr = 0;
                for (int z = 0 ; z < a_part.size(); z++) {
                    if (z == 0) {
                      nbr = OVData.getNextNbr("plan", a_part.size()); // skip ahead numbering to reduce db calls
                    } else {
                      nbr++;  
                    }
                    recnumber++;
                                st.executeUpdate("insert into plan_mstr "
                                    + "(plan_nbr, plan_order, plan_line, plan_item, plan_qty_req, plan_date_create, plan_date_due, plan_type, plan_site ) "
                                    + " values ( " + "'" + nbr + "'" + ","
                                    + "''" + ","
                                    + "''" + ","
                                    + "'" + a_part.get(z) + "'" + ","
                                    + "'" + a_qty.get(z) + "'" + ","
                                    + "'" + df.format(cal.getTime()) + "'" + ","
                                    + "'" + a_duedate.get(z) + "'" + ","
                                    + "'FCST'" + ","
                                    + "'" + site + "'"
                                    + ")"
                                    + ";");
                }
                
       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
                if (res != null) {
                    res.close();
                }
                if (res2 != null) {
                    res2.close();
                }
                if (st != null) {
                    st.close();
                }
                if (st2 != null) {
                    st2.close();
                }
                if (st3 != null) {
                    st3.close();
                }
                con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
    return recnumber;
   }

    public static int createPlanFromLocationMin(String fromsite, String tosite, String fromprod, String toprod) {

    int recnumber = 0;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date now = new java.util.Date();
    Calendar cal = Calendar.getInstance();
    cal.getTime();
    cal.add(Calendar.DATE, 14);
    String enddate = df.format(cal.getTime());
    int wk = getForecastWeek(cal.getTime());
    ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));


     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            Statement st2 = con.createStatement();
            ResultSet res2 = null;
            Statement st3 = con.createStatement();
            try {
           String item = "";
           double makeqty = 0;
           double demand = 0;
           double qoh = 0;
           double ticketqty = 0;
           double min = 0;
           double qtywk = 0;
           double qtyyear = 0;

           String site = "";
           String remarks = "";
             int k = 0;

             if (fromprod.isEmpty()) {
                 fromprod = bsmf.MainFrame.lowchar;
             }
             if (toprod.isEmpty()) {
                 toprod = bsmf.MainFrame.hichar;
             }
              if (fromsite.isEmpty()) {
                 fromsite = bsmf.MainFrame.lowchar;
             }
             if (tosite.isEmpty()) {
                 tosite = bsmf.MainFrame.hichar;
             }

             double qty = 0;
              res = st.executeQuery("select it_item, ita_fct, sod_site, in_qoh, round(ita_fct / 25) as '2week', " +
                      " coalesce(sum(sod_ord_qty - sod_shipped_qty),0) as mydemand " +
                      " from item_mstr " +
                      " left outer join in_mstr on in_item = it_item and in_loc = 'locA' and in_site = '1000' " +
                      " left outer join item_apd on ita_item = it_item " +
                      " left outer join sod_det on sod_char1 = it_item and sod_due_date <= " + "'" + enddate + "'" + " AND sod_site = '1000' " +
                      " where it_prodline >= " + "'" + fromprod + "'" +
                      " and it_prodline <= " + "'" + toprod + "'" + 
                      " group by it_item, ita_fct, sod_site, in_qoh " +
                                     ";" );
                while (res.next()) {
                 item = res.getString("it_item");
                 qoh = res.getDouble("in_qoh");
                 qtywk = res.getDouble("2week");
                 demand = res.getDouble("mydemand"); 
                 site = res.getString("sod_site");
                 qtyyear = res.getDouble("ita_fct");

                  if (qtywk < 2) {
                    qtywk = 2;
                }
                min = qtywk; 
                if (qtywk <= 60) {
                min = qtyyear; 
                }
                if (qtywk <= 60 && qtyyear <= 60 ) {
                min = qtyyear;
                }
                if (qtyyear >= 60 && qtyyear < 400  ) {
                min = qtyyear;
                }
                if (qtyyear >= 400 && qtyyear < 1000  ) {
                min = qtyyear / 2;
                }
                if (qtyyear >= 800 && qtyyear < 1250  ) {
                min = qtyyear / 3;
                }
                if (qtyyear >= 1250 && qtyyear < 2000  ) {
                min = qtyyear / 4;
                }
                if (qtyyear >= 2000 && qtyyear < 3000  ) {
                min = qtyyear / 6;
                }
                if (qtyyear >= 3000 && qtyyear < 4000  ) {
                min = qtyyear / 8;
                }
                if (qtyyear >= 4000 && qtyyear < 6000  ) {
                min = qtyyear / 12;
                }
                if (qtyyear >= 6000 && qtyyear < 8000  ) {
                min = qtyyear / 14;
                }
                if (qtyyear >= 8000 && qtyyear < 10000  ) {
                min = qtyyear / 16;
                }
                if (qtyyear >= 10000  ) {
                min = qtywk;
                }






                 ticketqty = 0;
                 makeqty = 0;
                 qty = 0;

                 // now lets find out what tickets we already have open ...tickets that are not closed (whether sched or not sched).

                      res2 = st2.executeQuery("select sum(plan_qty_req) as 'sum' from plan_mstr where plan_item = " + "'" + item + "'" +
                               " AND plan_site = " + "'" + site + "'" + 
                              " AND plan_status = '0' " + ";" );
                         while (res2.next()) {
                             ticketqty = res2.getInt("sum");
                         }

                        makeqty = demand - qoh - ticketqty;

                        // if make qty is less than the min2weekqty then create ticket for min
                        if (makeqty < 0) {
                            continue;
                        }
                        remarks = String.valueOf(makeqty) + "/" + String.valueOf(demand) + "/" + String.valueOf(qoh) + "/" + String.valueOf(ticketqty) + "/" + String.valueOf(min);
                        // if make qty is greater than 0 and less than the min2weekqty then create ticket for min
                        if (makeqty > 0 && makeqty <= min) {
                             recnumber++;
                                int nbr = OVData.getNextNbr("plan");
                                st3.executeUpdate("insert into plan_mstr "
                                    + "(plan_nbr, plan_item, plan_qty_req, plan_date_create, plan_date_due, plan_type, plan_site, plan_rmks ) "
                                    + " values ( " + "'" + nbr + "'" + ","
                                    + "'" + item + "'" + ","
                                    + "'" + min + "'" + ","
                                    + "'" + df.format(now) + "'" + ","
                                    + "'" + df.format(now) + "'" + ","
                                    + "'LocA'" + ","
                                    + "'" + site + "'" + "," 
                                    + "'" + remarks + "'"
                                    + ")"
                                    + ";");
                         }

                        // if makeqty is greater than minqty then create plan tickets as multiple of division.... + 1 if modulus > 0
                         if (makeqty > min) {
                             qty = (makeqty / min);
                             if (makeqty % min > 0) {
                                 qty += 1;
                             }
                             int roundqty = (int) Math.round(qty);
                             int nbr = 0;
                             for (int j = 0; j < roundqty; j++) {
                             recnumber++;
                                if (j == 0) {
                                  nbr = OVData.getNextNbr("plan", roundqty); // skip ahead numbering to reduce db calls
                                } else {
                                  nbr++;  
                                }
                                st3.executeUpdate("insert into plan_mstr "
                                    + "(plan_nbr, plan_item, plan_qty_req, plan_date_create, plan_date_due, plan_type, plan_site, plan_rmks ) "
                                    + " values ( " + "'" + nbr + "'" + ","
                                    + "'" + item + "'" + ","
                                    + "'" + min + "'" + ","
                                    + "'" + df.format(now) + "'" + ","
                                    + "'" + df.format(now) + "'" + ","
                                    + "'LocB'" + ","
                                    + "'" + site + "'" + "," 
                                    + "'" + remarks + "'"
                                    + ")"
                                    + ";");
                             }
                         }

                }

       }
        catch (SQLException s){
             MainFrame.bslog(s);

        } finally {
                if (res != null) {
                    res.close();
                }
                if (res2 != null) {
                    res2.close();
                }
                if (st != null) {
                    st.close();
                }
                if (st2 != null) {
                    st2.close();
                }
                if (st3 != null) {
                    st3.close();
                }
                con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);

    }
    return recnumber;
   }

    public static int createPlanFromDemand(String site) {

    int recnumber = 0;
    
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    cal.getTime();
   // int wk = getForecastWeek(cal.getTime());
   // ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));
     try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
           
            try {
           String item = "";
           String order = "";
           String line = "";
           String duedate = "";

           ArrayList a_part = new ArrayList();
           ArrayList a_order =  new ArrayList();
           ArrayList a_line =   new ArrayList();
           ArrayList a_qty =   new ArrayList();
           ArrayList a_duedate =   new ArrayList();

           

            double qty = 0;

             int k = 0;

          res = st.executeQuery("select sod_site, sod_nbr, sod_due_date, sod_item, " +
                  " sod_ord_qty, sod_shipped_qty, sod_line, coalesce(plan_nbr,0) as 'plan_nbr' from sod_det " +
                  " inner join so_mstr on sod_nbr = so_nbr and sod_status = so_status and (sod_ord_qty - sod_shipped_qty) > 0 " +
                  " inner join item_mstr on it_item = sod_item " +
                  " left outer join plan_mstr on plan_site = so_site and plan_item = sod_item and plan_order = sod_nbr and plan_line = sod_line " +
                  " where so_site = " + "'" + site + "'" +
                  " and so_status = " + "'" + getGlobalProgTag("open") + "'" +
                  " and so_plan = '1' " +        
                  " and it_plan = '1' " + 
                                 ";" );
              
                while (res.next()) {

                    // for this part, this site, this order, this line ....see what plan_mstr records exist

                 item = res.getString("sod_item");
                 order = res.getString("sod_nbr");
                 line = res.getString("sod_line");
                 qty = res.getDouble("sod_ord_qty") - res.getDouble("sod_shipped_qty");
                 duedate = res.getString("sod_due_date");
                 

                if (res.getString("plan_nbr").equals("0")) {
                             a_part.add(item);
                             a_order.add(order);
                             a_line.add(line);
                             a_qty.add(qty);
                             a_duedate.add(duedate);
                }
               
                }  


                // adjustment for sqlite
                int nbr = 0;
                for (int z = 0 ; z < a_part.size(); z++) {
                    if (z == 0) {
                      nbr = OVData.getNextNbr("plan", a_part.size()); // skip ahead numbering to reduce db calls
                    } else {
                      nbr++;  
                    }
                    recnumber++;
                                st.executeUpdate("insert into plan_mstr "
                                    + "(plan_nbr, plan_order, plan_line, plan_item, plan_qty_req, plan_date_create, plan_date_due, plan_type, plan_site ) "
                                    + " values ( " + "'" + nbr + "'" + ","
                                    + "'" + a_order.get(z) + "'" + ","
                                    + "'" + a_line.get(z) + "'" + ","
                                    + "'" + a_part.get(z) + "'" + ","
                                    + "'" + a_qty.get(z) + "'" + ","
                                    + "'" + df.format(cal.getTime()) + "'" + ","
                                    + "'" + a_duedate.get(z) + "'" + ","
                                    + "'DEMD'" + ","
                                    + "'" + site + "'"
                                    + ")"
                                    + ";");
                }
       } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
               
                con.close();
        }
    } catch (SQLException e){
        MainFrame.bslog(e);

    }
    return recnumber;
   }
  
    public static ArrayList getForecastDates(String year) {
        ArrayList<Date> fctdates = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
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
                res = st.executeQuery("SELECT * FROM  gl_cal where glc_year = " + "'" + year + "'" + " and glc_per = '1' " +
                           ";");
                while (res.next()) {
                    i++;
                    cal.setTime(bsmf.MainFrame.dfdate.parse(res.getString("glc_start")));
                    /*
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.MONTH, 1);
                    cal.set(Calendar.YEAR, 2012);
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date()); // Now use today date.
                    c.add(Calendar.DATE, 5); // Adding 5 days
                    String output = sdf.format(c.getTime());
                    System.out.println(output);
                     */
                    }
                
                if (i > 0) {
                   // fctdates.add(sdf.format(cal.getTime()));
                    fctdates.add(cal.getTime());
                    for (int j = 2; j <= 65; j++) {
                        cal.add(Calendar.DATE, 7);
                        fctdates.add(cal.getTime());
                    }
                } else {
                   // bsmf.MainFrame.show("No gl_cal record for year " + year);
                }
                    

            } catch (SQLException s) {
                s.printStackTrace();
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
        
        return fctdates;
    }
  
    public static void createClockRecord66(String empnbr, Date clockdate, String start, String end) {
       
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         DateFormat dt = new SimpleDateFormat("HH:mm");
         
         double hours = OVData.calcClockHours(df.format(clockdate), start, df.format(clockdate), end);
         
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               st.executeUpdate("insert into time_clock (emp_nbr, indate, intime, intime_adj, outdate, outtime, outtime_adj, dept,  code_id, code_orig, tothrs ) " +
                                "select emp_nbr, " +
                                "'" + df.format(clockdate) + "'" + "," +
                                "'" + start + "'" + "," +
                                "'" + start + "'" + "," +
                                "'" + df.format(clockdate) + "'" + "," +
                                "'" + end + "'" + "," +
                                "'" + end + "'" + "," +
                                "emp_dept, '66', '66', " +
                                "'" + hours + "'" +
                                " from emp_mstr where emp_nbr = " + "'" + empnbr + "'" + ";");
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
       
    }
    
    public static double degreesToRadians(double degrees) {
         return degrees * Math.PI / 180;
     }
     
    public static double calcDistanceBetweenTwoLatLon(double lat1, double lon1, double lat2, double lon2) {
         
         double earthradiusKM = 6371;
         
         double dLat = degreesToRadians(lat2 - lat1);
         double dLon = degreesToRadians(lon2 - lon1);
         
         lat1 = degreesToRadians(lat1);
         lat2 = degreesToRadians(lat2);
         
         double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
          Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);

         double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
         return earthradiusKM * c;
         
     }
    
    public static ArrayList getClockCodes() {
    ArrayList<String> mylist = new ArrayList<String>();

    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
          res = st.executeQuery("select clc_code from clock_code order by clc_code;" );
            while (res.next()) {
                mylist.add(res.getString("clc_code"));
            }
   }
    catch (SQLException s){
         MainFrame.bslog(s);

    } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
}
catch (Exception e){
    MainFrame.bslog(e);

}
return mylist;

 }

    public static ArrayList getClockCodesAndDesc() {
    ArrayList<String> mylist = new ArrayList<String>();

    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

          res = st.executeQuery("select clc_code, clc_desc from clock_code order by clc_code;" );
            while (res.next()) {
                mylist.add(res.getString("clc_code") + "," + res.getString("clc_desc"));
            }
   }
    catch (SQLException s){
         MainFrame.bslog(s);

    } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
}
catch (Exception e){
    MainFrame.bslog(e);

}
return mylist;

 }

    public static ArrayList getEmployeeIDAndName() {
    ArrayList<String> mylist = new ArrayList<String>();

    try{

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

          res = st.executeQuery("select emp_nbr, emp_lname, emp_fname from emp_mstr order by emp_nbr;" );
            while (res.next()) {
                mylist.add(res.getString("emp_nbr") + "," + res.getString("emp_fname") + " " + res.getString("emp_lname"));
            }
   }
    catch (SQLException s){
         MainFrame.bslog(s);

    } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
}
catch (Exception e){
    MainFrame.bslog(e);

}
return mylist;

 }

    public static boolean isClockRecord(String empnbr, Date clockdate) {
        boolean myreturn = false;
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select code_id from time_clock where emp_nbr = " + "'" + empnbr + "'" +
                        "and indate = " + "'" + df.format(clockdate) + "'" + ";");
               while (res.next()) {
                   myreturn = true;
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
        return myreturn;
    }
    
    public static boolean isClockScanCard() {
         boolean myreturn = false;
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
                
                    res = st.executeQuery("SELECT * FROM  clock_ctrl ;");
                    while (res.next()) {
                        myreturn = res.getBoolean("clctrl_scan");
                    }
           
            }
            catch (SQLException s) {
                 MainFrame.bslog(s);
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
         return myreturn;
     }
    
    public static Date getPayWindowForSalary(String frequency, java.util.Date weeklyPayDate) {
        java.util.Date r = null;
        
        java.util.Date now = new java.util.Date();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        java.util.Date endmonth = cal.getTime();
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 15);
        java.util.Date midmonth = cal.getTime();
        
        if ( frequency.equals("bi-monthly") && getDifferenceDays(now, midmonth) < 7    ) {
            r = midmonth;
        }
        if ( frequency.equals("bi-monthly") && getDifferenceDays(now, endmonth) < 7) {
            r = endmonth;
        }
        if (frequency.equals("monthly") && getDifferenceDays(now, endmonth) < 7) {
            r = endmonth;
        }
        if (frequency.equals("weekly")) {
            r = weeklyPayDate;
        }
         
         return r;
     }
     
    public static long getDifferenceDays(Date d1, Date d2) {
       long diff = d2.getTime() - d1.getTime();
       return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
     
    public static double calcClockHours(String indate, String intime, String outdate, String outtime) {
       
        // lets trim off millisecs from intime and outtime if exists
        if (intime.length() == 8) {
            intime = intime.substring(0,6);
        }
        if (outtime.length() == 8) {
            outtime = outtime.substring(0,6);
        }       
        
         Calendar clockstart = Calendar.getInstance();
         Calendar clockstop = Calendar.getInstance();
                           
          clockstart.set( Integer.valueOf(indate.substring(0,4)), Integer.valueOf(indate.substring(5,7)), Integer.valueOf(indate.substring(8,10)), Integer.valueOf(intime.substring(0,2)), Integer.valueOf(intime.substring(3,5)) );
          clockstop.set(Integer.valueOf(outdate.substring(0,4)), Integer.valueOf(outdate.substring(5,7)), Integer.valueOf(outdate.substring(8,10)), Integer.valueOf(outtime.substring(0,2)), Integer.valueOf(outtime.substring(3,5)));
                           
                            double quarterhour = 0;
                            long milis1 = clockstart.getTimeInMillis();
                            long milis2 = clockstop.getTimeInMillis();
                            long diff = milis2 - milis1;
                            long diffHours = diff / (60 * 60 * 1000);
                            long diffMinutes = diff / (60 * 1000);
                            long diffDays = diff / (24 * 60 * 60 * 1000);
                            long diffSeconds = diff / 1000;
                            if (diffMinutes > 0) {
                                quarterhour = (diffMinutes / (double) 60) ;

                            }
        
        return quarterhour;
    }
    
    public static String getShiftClockForThisDay(String shift, int day) {
        String myreturn = "";
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                res = st.executeQuery("select * from shift_mstr where shf_id = " + "'" + shift + "'"  + ";");
               while (res.next()) {
                   if (day == 1) {
                      myreturn = res.getString("shf_sun_intime") + "," + res.getString("shf_sun_outtime");
                   }
                   if (day == 2) {
                      myreturn = res.getString("shf_mon_intime") + "," + res.getString("shf_mon_outtime");
                   }
                   if (day == 3) {
                      myreturn = res.getString("shf_tue_intime") + "," + res.getString("shf_tue_outtime");
                   }
                   if (day == 4) {
                      myreturn = res.getString("shf_wed_intime") + "," + res.getString("shf_wed_outtime");
                   }
                   if (day == 5) {
                      myreturn = res.getString("shf_thu_intime") + "," + res.getString("shf_thu_outtime");
                   }
                   if (day == 6) {
                      myreturn = res.getString("shf_fri_intime") + "," + res.getString("shf_fri_outtime");
                   }
                   if (day == 7) {
                      myreturn = res.getString("shf_sat_intime") + "," + res.getString("shf_sat_outtime");
                   }
                   
                }
               
               if (myreturn.equals("00:00:00,00:00:00")) {
                   myreturn = "";
               }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
        
        return myreturn;
    }
    
    public static boolean shouldClock(String shift, int day) {
        boolean myreturn = false;
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                res = st.executeQuery("select * from shift_mstr where shf_id = " + "'" + shift + "'"  + ";");
               while (res.next()) {
                   if (day == 1) {
                      if (! res.getString("shf_sun_intime").equals("00:00") && ! res.getString("shf_sun_outtime").equals("00:00"))
                              myreturn = true;
                   }
                   if (day == 2) {
                     if (! res.getString("shf_sun_intime").equals("00:00") && ! res.getString("shf_sun_outtime").equals("00:00"))
                              myreturn = true;
                   }
                   if (day == 3) {
                      if (! res.getString("shf_sun_intime").equals("00:00") && ! res.getString("shf_sun_outtime").equals("00:00"))
                              myreturn = true;
                   }
                   if (day == 4) {
                      if (! res.getString("shf_sun_intime").equals("00:00") && ! res.getString("shf_sun_outtime").equals("00:00"))
                              myreturn = true;
                   }
                   if (day == 5) {
                      if (! res.getString("shf_sun_intime").equals("00:00") && ! res.getString("shf_sun_outtime").equals("00:00"))
                              myreturn = true;
                   }
                   if (day == 6) {
                      if (! res.getString("shf_sun_intime").equals("00:00") && ! res.getString("shf_sun_outtime").equals("00:00"))
                              myreturn = true;
                   }
                   if (day == 7) {
                      if (! res.getString("shf_sun_intime").equals("00:00") && ! res.getString("shf_sun_outtime").equals("00:00"))
                              myreturn = true;
                   }
                   
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
        
        return myreturn;
    }
    
    public static void autoclock(int days) {
         
         
         // return if days is not negative
         if (days > 0)
             return;
         
         ArrayList myarray = new ArrayList();
         Calendar c = Calendar.getInstance();
         java.util.Date now = new java.util.Date();
         java.util.Date thisdate = new java.util.Date();
         c.setTime(now);
         int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
         int day = 0;
         java.util.Date begdate = new java.util.Date();
         c.add(Calendar.DATE, days);
         begdate.setTime(c.getTime().getTime());
         
         boolean hasclocked = false;
         boolean shouldclock = false;
         String emp = "";
         String shift = "";
         String startend = "";
         // lets start from the begdate and roll forward until now and determine if there
         // should be any records and if there are any records
         
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select emp_nbr, emp_shift from emp_mstr where emp_autoclock = '1' order by emp_nbr ;");
               while (res.next()) {
                   emp = res.getString("emp_nbr");
                   shift = res.getString("emp_shift");
                   // lets loop through each date since begdate
                   c.setTime(begdate);
                   
                   for (int i = 0; i > days; i--) {
                    if (i < 0)
                     c.add(Calendar.DATE, 1);
                    thisdate.setTime(c.getTime().getTime());
                    day = c.get(Calendar.DAY_OF_WEEK);
                     // check and see if there is a clock record for this date
                     hasclocked = OVData.isClockRecord(emp, thisdate);
                     // check and see if emp should have clocked in
                     shouldclock = OVData.shouldClock(shift, day);
                    
                   
                     // if shouldclock = true  and hasclocked = false then create a clock record with code 66
                     if (shouldclock && ! hasclocked && thisdate.compareTo(now) < 0) {
                         startend = OVData.getShiftClockForThisDay(shift, day);
                        
                         if (! startend.isEmpty())
                         OVData.createClockRecord66(emp, thisdate, startend.split(",")[0], startend.split(",")[1]);
                     }
                     
                    } 
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        
        
    }
     
   
}
