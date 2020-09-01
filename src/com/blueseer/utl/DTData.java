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
package com.blueseer.utl;

import bsmf.MainFrame;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author TerryVa
 */
public class DTData {
    
      
    public static DefaultTableModel getForecast13weeks(int wk) {
         Calendar cal = Calendar.getInstance();
        cal.getTime();
       
        ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));
        DateFormat df = new SimpleDateFormat("MM/dd");
        // week dates are base 0
        String wk1 = df.format(dates.get(wk - 1));
        String wk2 = df.format(dates.get(wk));
        String wk3 = df.format(dates.get(wk + 1));
        String wk4 = df.format(dates.get(wk + 2));
        String wk5 = df.format(dates.get(wk + 3));
        String wk6 = df.format(dates.get(wk + 4));
        String wk7 = df.format(dates.get(wk + 5));
        String wk8 = df.format(dates.get(wk + 6));
        String wk9 = df.format(dates.get(wk + 7));
        String wk10 = df.format(dates.get(wk + 8));
        String wk11 = df.format(dates.get(wk + 9));
        String wk12 = df.format(dates.get(wk + 10));
        String wk13 = df.format(dates.get(wk + 11));
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Part", "Year", "Site", wk1, wk2, wk3, wk4, wk5, wk6, wk7, wk8, wk9, wk10, wk11, wk12, wk13 })
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
               
                  // adjust wk for first three fields
                  wk = wk + 3;
                  
                  res = st.executeQuery("select * from fct_mstr;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fct_part"),
                                   res.getString("fct_year"),
                                   res.getString("fct_site"),
                                   res.getInt(wk),
                                   res.getInt(wk + 1),
                                   res.getInt(wk + 2),
                                   res.getInt(wk + 3),
                                   res.getInt(wk + 4),
                                   res.getInt(wk + 5),
                                   res.getInt(wk + 6),
                                   res.getInt(wk + 7),
                                   res.getInt(wk + 8),
                                   res.getInt(wk + 9),
                                   res.getInt(wk + 10),
                                   res.getInt(wk + 11),
                                   res.getInt(wk + 12)
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
               } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
    
     public static DefaultTableModel getForecast4weeksAndSec(int wk) {
         Calendar cal = Calendar.getInstance();
        cal.getTime();
       
        ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));
        DateFormat df = new SimpleDateFormat("MM/dd");
        // week dates are base 0
        String wk1 = df.format(dates.get(wk - 1));
        String wk2 = df.format(dates.get(wk));
        String wk3 = df.format(dates.get(wk + 1));
        String wk4 = df.format(dates.get(wk + 2));
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Part", "Year", "Site", "CrtUser", "CrtDate", "ChgUser", "ChgDate", wk1, wk2, wk3, wk4 })
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                  // adjust wk for first three fields
                  wk = wk + 3;
                  
                  res = st.executeQuery("select * from fct_mstr;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fct_part"),
                                   res.getString("fct_year"),
                                   res.getString("fct_site"),
                                   res.getString("fct_crt_userid"),
                                   res.getString("fct_crt_date"),
                                   res.getString("fct_chg_userid"),
                                   res.getString("fct_chg_date"),
                                   res.getInt(wk),
                                   res.getInt(wk + 1),
                                   res.getInt(wk + 2),
                                   res.getInt(wk + 3)
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
      
     public static DefaultTableModel getShiftAll() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Shift", "Desc"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
               
                  res = st.executeQuery("select * from shift_mstr;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("shf_id"),
                                   res.getString("shf_desc")                                  
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
    
    
          public static DefaultTableModel getClockCodesAll() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "Payable", "SystemCode"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
               
                  res = st.executeQuery("select clc_code, clc_desc, case when clc_payable = '1' then 'yes' else 'no' end as 'payable', " +
                          " case when clc_syscode = '1' then 'yes' else 'no' end as 'syscode' " +
                          " from clock_code order by clc_code;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("clc_code"),
                                   res.getString("clc_desc") , res.getString("payable"), res.getString("syscode")                                 
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
    
         public static DefaultTableModel getClockRecords66All() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "RecID", "EmpID", "LastName", "FirstName", "Dept", "Code", "InDate", "InTime", "InTmAdj", "OutDate", "OutTime", "OutTmAdj", "tothrs"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                 res = st.executeQuery("SELECT * FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr where t.code_id = '66';"  );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("t.recid"),
                                            res.getString("t.emp_nbr"),
                                            res.getString("e.emp_lname"),
                                            res.getString("e.emp_fname"),
                                            res.getString("e.emp_dept"),
                                            res.getString("t.code_id"),
                                             res.getString("t.indate"),
                                            res.getString("t.intime"),
                                            res.getString("t.intime_adj"),
                                            res.getString("t.outdate"),
                                            res.getString("t.outtime"),
                                            res.getString("t.outtime_adj"),
                                            res.getString("t.tothrs")                                 
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
    
           public static DefaultTableModel getQOHvsSSAll() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Item", "Desc", "UOM", "Type", "Status", "QtyOnHand", "SafetyStock"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                
                 res = st.executeQuery("SELECT it_item, it_desc, it_uom, it_type, it_status, sum(in_qoh) as 'sum', it_safestock  " +
                        " FROM  item_mstr inner join in_mstr on in_part = it_item  " +
                        " group by it_item order by it_item ;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("it_item"),
                                   res.getString("it_desc"),
                                   res.getString("it_uom"),
                                   res.getString("it_type"),
                                   res.getString("it_status"),
                                   res.getInt("sum"),
                                   res.getInt("it_safestock")
                                  
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
           
           
    public static DefaultTableModel getItemInfoAll() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Item", "Desc", "UOM", "Type", "Status", "Site", "ProdLine", "Rev", "MFCode", "RoutingCode"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                 res = st.executeQuery("SELECT it_item, it_desc, it_uom, it_type, it_status, it_site, it_prodline, it_rev, it_code, it_wf  " +
                        " FROM  item_mstr  " +
                        " order by it_item ;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("it_item"),
                                   res.getString("it_desc"),
                                   res.getString("it_uom"),
                                   res.getString("it_type"),
                                   res.getString("it_status"),
                                   res.getString("it_site"),
                                   res.getString("it_prodline"),
                                   res.getString("it_rev"),
                                   res.getString("it_code"),
                                   res.getString("it_wf")
                                  
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
     
   
    
    public static DefaultTableModel getGLTranBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ID", "REF", "Acct", "CC", "Site", "EffDate", "EntDate", "Desc", "Amount", "UserID"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_entdate, glt_desc, glt_base_amt, glt_userid " +
                        " FROM  gl_tran where " + myfield + " like " + "'" + str + "%'" +
                        " order by glt_id desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_entdate, glt_desc, glt_base_amt, glt_useride  " +
                        " FROM  gl_tran where " + myfield + " like " + "'%" + str + "'" +
                        " order by glt_id desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_entdate, glt_desc, glt_base_amt, glt_useride  " +
                        " FROM  gl_tran where " + myfield + " like " + "'%" + str + "%'" +
                        " order by glt_id desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("glt_id"),
                                   res.getString("glt_ref"),
                                   res.getString("glt_acct"),
                                   res.getString("glt_cc"),
                                   res.getString("glt_site"),
                                   res.getString("glt_effdate"),
                                   res.getString("glt_entdate"),
                                   res.getString("glt_desc"),
                                   res.getString("glt_base_amt"),
                                   res.getString("glt_userid")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
    
        
    
    public static DefaultTableModel getItemBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Item", "Desc", "ClassCode", "UOM", "Type", "Status", "Site", "ProdLine", "Routing"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by it_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by it_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by it_item ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("it_item"),
                                   res.getString("it_desc"),
                                   res.getString("it_code"),
                                   res.getString("it_uom"),
                                   res.getString("it_type"),
                                   res.getString("it_status"),
                                   res.getString("it_site"),
                                   res.getString("it_prodline"),
                                   res.getString("it_wf")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
      
    public static DefaultTableModel getItemMClassBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Item", "Desc", "ClassCode", "UOM", "Type", "Status", "Site", "ProdLine", "Routing"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " and it_code = 'M' " +
                        " order by it_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and it_code = 'M' " +
                        " order by it_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and it_code = 'M' " +
                        " order by it_item ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("it_item"),
                                   res.getString("it_desc"),
                                   res.getString("it_code"),
                                   res.getString("it_uom"),
                                   res.getString("it_type"),
                                   res.getString("it_status"),
                                   res.getString("it_site"),
                                   res.getString("it_prodline"),
                                   res.getString("it_wf")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
    
    
    public static DefaultTableModel getVendBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Name", "Line1", "City", "State", "Zip", "Country"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_country  " +
                        " FROM  vd_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by vd_addr ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_country  " +
                        " FROM  vd_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by vd_addr ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_country   " +
                        " FROM  vd_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by vd_addr ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("vd_addr"),
                                   res.getString("vd_name"),
                                   res.getString("vd_line1"),
                                   res.getString("vd_city"),
                                   res.getString("vd_state"),
                                   res.getString("vd_zip"),
                                   res.getString("vd_country")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
         public static DefaultTableModel getAcctBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "Type", "Currency"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT ac_id, ac_desc, ac_type, ac_cur  " +
                        " FROM  ac_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by ac_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT ac_id, ac_desc, ac_type, ac_cur  " +
                        " FROM  ac_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by ac_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT ac_id, ac_desc, ac_type, ac_cur   " +
                        " FROM  ac_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by ac_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("ac_id"),
                                   res.getString("ac_desc"),
                                   res.getString("ac_type"),
                                   res.getString("ac_cur")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
         public static DefaultTableModel getPanelBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "PanelClass", "Desc", "Core"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT panel_id, panel_desc, panel_core  " +
                        " FROM  panel_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by panel_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT panel_id, panel_desc, panel_core  " +
                        " FROM  panel_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by panel_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT panel_id, panel_desc, panel_core   " +
                        " FROM  panel_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by panel_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("panel_id"),
                                   res.getString("panel_desc"),
                                   res.getString("panel_core")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
          public static DefaultTableModel getKeyBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "KeyName", "Desc", "From", "To", "CurrentID"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT counter_name, counter_desc, counter_from, counter_to, counter_id  " +
                        " FROM  counter where " + myfield + " like " + "'" + str + "%'" +
                        " order by counter_name ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT counter_name, counter_desc, counter_from, counter_to, counter_id  " +
                        " FROM  counter where " + myfield + " like " + "'%" + str + "'" +
                        " order by counter_name ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT counter_name, counter_desc, counter_from, counter_to, counter_id  " +
                        " FROM  counter where " + myfield + " like " + "'%" + str + "%'" +
                        " order by counter_name ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("counter_name"),
                                   res.getString("counter_desc"),
                                   res.getString("counter_from"),
                                   res.getString("counter_to"),
                                   res.getString("counter_id")
                                   
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
         
         public static DefaultTableModel getSiteBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Site", "Desc", "Line1", "City", "State", "Zip", "Logo"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT site_site, site_desc, site_line1, site_city, site_state, site_zip, site_logo  " +
                        " FROM  site_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by site_site ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT site_site, site_desc, site_line1, site_city, site_state, site_zip, site_logo   " +
                        " FROM  site_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by site_site ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT site_site, site_desc, site_line1, site_city, site_state, site_zip, site_logo   " +
                        " FROM  site_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by site_site ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("site_site"),
                                   res.getString("site_desc"),
                                   res.getString("site_line1"),
                                   res.getString("site_city"),
                                   res.getString("site_state"),
                                   res.getString("site_zip"),
                                   res.getString("site_logo")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
        public static DefaultTableModel getCustBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Name", "Line1", "City", "State", "Zip", "Country"}){
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip, cm_country  " +
                        " FROM  cm_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cm_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip, cm_country  " +
                        " FROM  cm_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by cm_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip, cm_country   " +
                        " FROM  cm_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cm_code ;");
                 }
                 
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("cm_code"),
                                   res.getString("cm_name"),
                                   res.getString("cm_line1"),
                                   res.getString("cm_city"),
                                   res.getString("cm_state"),
                                   res.getString("cm_zip"),
                                   res.getString("cm_country")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
        public static DefaultTableModel getShipToBrowseUtil( String str, int state, String myfield, String cust) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ShipCode", "BillCode", "Name", "Line1", "City", "State", "Zip", "Country"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT cms_shipto, cms_code, cms_name, cms_line1, cms_city, cms_state, cms_zip, cms_country  " +
                        " FROM  cms_det where cms_code = " + "'" + cust + "'" + " AND " + myfield + " like " + "'" + str + "%'" +
                        " order by cms_shipto ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT cms_shipto, cms_code, cms_name, cms_line1, cms_city, cms_state, cms_zip, cms_country  " +
                        " FROM  cms_det where cms_code = " + "'" + cust + "'" + " AND " + myfield + " like " + "'%" + str + "'" +
                        " order by cms_shipto ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT cms_shipto, cms_code, cms_name, cms_line1, cms_city, cms_state, cms_zip, cms_country  " +
                        " FROM  cms_det where cms_code = " + "'" + cust + "'" + " AND "+ myfield + " like " + "'%" + str + "%'" +
                        " order by cms_shipto ;");
                 }
                
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("cms_shipto"),
                                   res.getString("cms_code"),
                                   res.getString("cms_name"),
                                   res.getString("cms_line1"),
                                   res.getString("cms_city"),
                                   res.getString("cms_state"),
                                   res.getString("cms_zip"),
                                   res.getString("cms_country")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
         public static DefaultTableModel getRoutingBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "RoutingID", "Operation", "Cell", "OpDesc", "RunHours", "SetupHours", "isAssert"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT wf_id, wf_op, wf_desc, wf_cell, wf_op_desc, wf_run_hours, wf_setup_hours, wf_assert  " +
                        " FROM  wf_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by wf_id, wf_op ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT wf_id, wf_op, wf_desc, wf_cell, wf_op_desc, wf_run_hours, wf_setup_hours, wf_assert  " +
                        " FROM  wf_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by wf_id, wf_op ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT wf_id, wf_op, wf_desc, wf_cell, wf_op_desc, wf_run_hours, wf_setup_hours, wf_assert  " +
                        " FROM  wf_mstr where "+ myfield + " like " + "'%" + str + "%'" +
                        " order by wf_id, wf_op ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("wf_id"),
                                   res.getString("wf_op"),
                                   res.getString("wf_cell"),
                                   res.getString("wf_desc"),
                                   res.getString("wf_run_hours"),
                                   res.getString("wf_setup_hours"),
                                   res.getString("wf_assert")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
          public static DefaultTableModel getWorkCenterBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Cell",  "Desc", "Dept/CC", "RunRate", "SetupRate", "BurdenRate"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT wc_cell,  wc_desc, wc_cc, wc_run_rate, wc_setup_rate, wc_bdn_rate  " +
                        " FROM  wc_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by wc_cell ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT SELECT wc_cell,  wc_desc, wc_cc, wc_run_rate, wc_setup_rate, wc_bdn_rate  " +
                        " FROM  wc_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by wc_cell ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT SELECT wc_cell,  wc_desc, wc_cc, wc_run_rate, wc_setup_rate, wc_bdn_rate  " +
                        " FROM  wc_mstr where "+ myfield + " like " + "'%" + str + "%'" +
                        " order by wc_cell ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("wc_cell"),
                                   res.getString("wc_desc"),
                                   res.getString("wc_cc"),
                                   res.getString("wc_run_rate"),
                                   res.getString("wc_setup_rate"),
                                   res.getString("wc_bdn_rate")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
           public static DefaultTableModel getShiftBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ShiftID", "ShiftDesc"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT shf_id, shf_desc " +
                        " FROM  shift_mstr  where " + myfield + " like " + "'" + str + "%'" +
                        " order by shf_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT shf_id, shf_desc " +
                        " FROM  shift_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by shf_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT shf_id, shf_desc   " +
                        " FROM  shift_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by shf_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("shf_id"),
                                   res.getString("shf_desc")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
           
       public static DefaultTableModel getUOMBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "UOM", "Desc"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT uom_id, uom_desc " +
                        " FROM  uom_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by uom_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT uom_id, uom_desc " +
                        " FROM  uom_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by uom_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT uom_id, uom_desc   " +
                        " FROM  uom_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by uom_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("uom_id"),
                                   res.getString("uom_desc")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }     
        
        public static DefaultTableModel getQPRBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ID", "Item",  "Desc", "Vendor", "Userid", "DateCreate", "DateClosed"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT qual_id, qual_part, qual_part_desc, qual_vend, qual_userid, qual_date_crt, qual_date_cls " +
                        " FROM  qual_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by qual_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT qual_id, qual_part, qual_part_desc, qual_vend, qual_userid, qual_date_crt, qual_date_cls " +
                        " FROM  qual_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by qual_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT qual_id, qual_part, qual_part_desc, qual_vend, qual_userid, qual_date_crt, qual_date_cls " +
                        " FROM  qual_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by qual_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, 
                            res.getString("qual_id"),
                            res.getString("qual_part"),
                            res.getString("qual_part_desc"),
                            res.getString("qual_vend"),
                            res.getString("qual_userid"),
                            res.getString("qual_date_crt"),
                            res.getString("qual_date_cls")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }     
        
       
         public static DefaultTableModel getPrinterBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "PrinterID", "Desc", "Type", "IP", "Port"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT * " +
                        " FROM  prt_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by prt_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT * " +
                        " FROM  prt_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by prt_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT * " +
                        " FROM  prt_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by prt_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, 
                                   res.getString("prt_id"),
                                   res.getString("prt_desc"),
                                   res.getString("prt_type"),
                                   res.getString("prt_ip"),
                                   res.getString("prt_port")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }     
      
       
       
        public static DefaultTableModel getCurrencyBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "CurID", "Desc"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT cur_id, cur_desc " +
                        " FROM  cur_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cur_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("  SELECT cur_id, cur_desc  " +
                        " FROM  cur_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by cur_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  SELECT cur_id, cur_desc    " +
                        " FROM  cur_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cur_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("cur_id"),
                                   res.getString("cur_desc")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }     
           
        public static DefaultTableModel getUOMConvBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Key1", "Key2", "Key2Amount", "Key2Amount", "Type", "Notes"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt, conv_type, conv_notes " +
                        " FROM  conv_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by conv_fromcode ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt, conv_type, conv_notes " +
                        " FROM  conv_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by conv_fromcode ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt, conv_type, conv_notes    " +
                        " FROM  conv_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by conv_fromcode ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, 
                            res.getString("conv_fromcode"),
                            res.getString("conv_tocode"),
                            res.getString("conv_fromamt"),
                            res.getString("conv_toamt"),
                            res.getString("conv_type"),
                            res.getString("conv_notes")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }     
       
        
        
        public static DefaultTableModel getECNBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ECN Nbr", "TaskID", "Desc", "POC", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT ecn_nbr, ecn_mstrtask, task_desc, ecn_poc, ecn_status " +
                        " FROM  ecn_mstr inner join task_mstr on task_mstr.task_id = ecn_mstr.ecn_mstrtask  where " + myfield + " like " + "'" + str + "%'" +
                        " order by ecn_nbr ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT ecn_nbr, ecn_mstrtask, task_desc, ecn_poc, ecn_status  " +
                        " FROM  ecn_mstr inner join task_mstr on task_mstr.task_id = ecn_mstr.ecn_mstrtask  where " + myfield + " like " + "'%" + str + "'" +
                        " order by ecn_nbr ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT ecn_nbr, ecn_mstrtask, task_desc, ecn_poc, ecn_status   " +
                        " FROM  ecn_mstr inner join task_mstr on task_mstr.task_id = ecn_mstr.ecn_mstrtask where " + myfield + " like " + "'%" + str + "%'" +
                        " order by ecn_nbr ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("ecn_nbr"),
                                   res.getString("ecn_mstrtask"),
                                   res.getString("task_desc"),
                                   res.getString("ecn_poc"),
                                   res.getString("ecn_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
         public static DefaultTableModel getTaskBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "TaskID", "Desc", "Class", "Creator", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select task_id, task_desc, task_class, task_creator, task_status " +
                        " FROM  task_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by task_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select task_id, task_desc, task_class, task_creator, task_status " +
                        " FROM  task_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by task_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select task_id, task_desc, task_class, task_creator, task_status  " +
                        " FROM  task_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by task_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("task_id"),
                                   res.getString("task_desc"),
                                   res.getString("task_class"),
                                   res.getString("task_creator"),
                                   res.getString("task_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
          public static DefaultTableModel getTaxBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "TaxCode", "Desc", "UserID"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select tax_code, tax_desc, tax_userid " +
                        " FROM  tax_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by tax_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select tax_code, tax_desc, tax_userid " +
                        " FROM  tax_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by tax_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select tax_code, tax_desc, tax_userid  " +
                        " FROM  tax_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by tax_code ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("tax_code"),
                                   res.getString("tax_desc"),
                                   res.getString("tax_userid")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
                  
        public static DefaultTableModel getGenCodeBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "GenCode", "GenKey", "GenValue"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by code_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by code_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by code_code ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("code_code"),
                                   res.getString("code_key"),
                                   res.getString("code_value")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
         public static DefaultTableModel getFctMstrBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Item", "Site", "Year"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select fct_part, fct_site, fct_year " +
                        " FROM  fct_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by fct_part ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("  select fct_part, fct_site, fct_year " +
                        " FROM  fct_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by fct_part ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select fct_part, fct_site, fct_year " +
                        " FROM  fct_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by fct_part ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fct_part"),
                                   res.getString("fct_site"),
                                   res.getString("fct_year")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
        
        
        public static DefaultTableModel getCustXrefBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Cust", "CustItem", "Item"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cup_cust, cup_citem ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by cup_cust, cup_citem ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cup_cust, cup_citem ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("cup_cust"),
                                   res.getString("cup_citem"),
                                   res.getString("cup_item")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
        public static DefaultTableModel getVendXrefBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Vend", "VendItem", "Item"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by vdp_vend, vdp_vitem ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by vdp_vend, vdp_vitem ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by vdp_vend, vdp_vitem ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("vdp_vend"),
                                   res.getString("vdp_vitem"),
                                   res.getString("vdp_item")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
        public static DefaultTableModel getVendPriceBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Vend", "Item", "UOM", "Currency", "Price"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select vpr_vend, vpr_item, vpr_uom, vpr_curr, vpr_price " +
                        " FROM  vpr_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by vpr_vend, vpr_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select vpr_vend, vpr_item, vpr_uom, vpr_curr, vpr_price " +
                        " FROM  vpr_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by vpr_vend, vpr_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select vpr_vend, vpr_item, vpr_uom, vpr_curr, vpr_price " +
                        " FROM  vpr_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by vpr_vend, vpr_item ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("vpr_vend"),
                                   res.getString("vpr_item"),
                                   res.getString("vpr_uom"),
                                   res.getString("vpr_curr"),
                                   res.getString("vpr_price")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
         public static DefaultTableModel getCustPriceBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Cust", "Item", "UOM", "Currency", "Price"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select cpr_cust, cpr_item, cpr_uom, cpr_curr, cpr_price " +
                        " FROM  cpr_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cpr_cust, cpr_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select cpr_cust, cpr_item, cpr_uom, cpr_curr, cpr_price " +
                        " FROM  cpr_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by cpr_cust, cpr_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select cpr_cust, cpr_item, cpr_uom, cpr_curr, cpr_price " +
                        " FROM  cpr_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cpr_cust, cpr_item ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("cpr_cust"),
                                   res.getString("cpr_item"),
                                   res.getString("cpr_uom"),
                                   res.getString("cpr_curr"),
                                   res.getString("cpr_price")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
          
        public static DefaultTableModel getPayProfileBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ProfileCode", "Desc", "UserID"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select payp_code, payp_desc, payp_userid " +
                        " FROM  pay_profile where " + myfield + " like " + "'" + str + "%'" +
                        " order by payp_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select payp_code, payp_desc, payp_userid " +
                        " FROM  pay_profile where " + myfield + " like " + "'%" + str + "'" +
                        " order by payp_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select payp_code, payp_desc, payp_userid   " +
                        " FROM  pay_profile where " + myfield + " like " + "'%" + str + "%'" +
                        " order by payp_code ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("payp_code"),
                                   res.getString("payp_desc"),
                                   res.getString("payp_userid")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
             public static DefaultTableModel getPayRollBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "BatchID", "Site", "Desc", "StartDate", "EndDate", "PayDate", "UserID"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select * " +
                        " FROM  pay_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by py_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select * " +
                        " FROM  pay_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by py_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select *  " +
                        " FROM  pay_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by py_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("py_id"),
                                   res.getString("py_site"),
                                   res.getString("py_desc"),
                                   res.getString("py_startdate"),
                                   res.getString("py_enddate"),
                                   res.getString("py_paydate"),
                                   res.getString("py_userid")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
          
          public static DefaultTableModel getEDITPBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "TPID", "Desc", "Contact", "Web", "Phone"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select editp_id, editp_name, editp_contact, editp_web, editp_helpdesk " +
                        " FROM  editp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by editp_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select editp_id, editp_name, editp_contact, editp_web, editp_helpdesk " +
                        " FROM  editp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by editp_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select editp_id, editp_name, editp_contact, editp_web, editp_helpdesk  " +
                        " FROM  editp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by editp_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("editp_id"),
                                   res.getString("editp_name"),
                                   res.getString("editp_contact"),
                                   res.getString("editp_web"),
                                   res.getString("editp_helpdesk")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
          public static DefaultTableModel getEDITPDOCBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "TPID", "DOC", "Desc", "Map"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select edi_id, edi_doctype, edi_desc, edi_map " +
                        " FROM  edi_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by edi_id, edi_doctype ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("  select edi_id, edi_doctype, edi_desc, edi_map " +
                        " FROM  edi_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by edi_id, edi_doctype ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  select edi_id, edi_doctype, edi_desc, edi_map  " +
                        " FROM  edi_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by edi_id, edi_doctype ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("edi_id"),
                                   res.getString("edi_doctype"),
                                   res.getString("edi_desc"),
                                   res.getString("edi_map")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }  
          
           public static DefaultTableModel getEDICustBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "CustCode", "DOC", "Dir", "ISA", "GS", "SiteISA", "SiteGS", "Map"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select edi_id, edi_doc, edi_dir, edi_isa, edi_gs, edi_bsisa, edi_bsgs, edi_map " +
                        " FROM  edi_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by edi_id, edi_doc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select edi_id, edi_doc, edi_dir, edi_isa, edi_gs, edi_bsisa, edi_bsgs, edi_map " +
                        " FROM  edi_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by edi_id, edi_doc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  select edi_id, edi_doc, edi_dir, edi_isa, edi_gs, edi_bsisa, edi_bsgs, edi_map  " +
                        " FROM  edi_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by edi_id, edi_doc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("edi_id"),
                                   res.getString("edi_doc"),
                                   res.getString("edi_dir"),
                                   res.getString("edi_isa"),
                                   res.getString("edi_gs"),
                                   res.getString("edi_bsisa"),
                                   res.getString("edi_bsgs"),
                                   res.getString("edi_map")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
               } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }  
          
          public static DefaultTableModel getTermBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "Days", "DiscDays", "DiscPercent", "DiscStart", "DueStart"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent, cut_discstart, cut_duestart " +
                        " FROM  cust_term where " + myfield + " like " + "'" + str + "%'" +
                        " order by cut_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent, cut_discstart, cut_duestart" +
                        " FROM  cust_term where " + myfield + " like " + "'%" + str + "'" +
                        " order by cut_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent, cut_discstart, cut_duestart  " +
                        " FROM  cust_term where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cut_code ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("cut_code"),
                                   res.getString("cut_desc"),
                                   res.getString("cut_days"),
                                   res.getString("cut_discdays"),
                                   res.getString("cut_discpercent"),
                                   res.getString("cut_discstart"),
                                   res.getString("cut_duestart")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
          
    public static DefaultTableModel getOrderBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "OrderNbr", "Billto", "Shipto", "PONumber", "OrderDate", "DueDate", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status " +
                        " FROM  so_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by so_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status " +
                        " FROM  so_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by so_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status  " +
                        " FROM  so_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by so_nbr desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("so_nbr"),
                                   res.getString("so_cust"),
                                   res.getString("so_ship"),
                                   res.getString("so_po"),
                                   res.getString("so_ord_date"),
                                   res.getString("so_due_date"),
                                   res.getString("so_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
    public static DefaultTableModel getPOBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "PO Nbr", "Vendor", "Site", "Remarks", "OrderDate", "DueDate", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select po_nbr, po_vend, po_site, po_rmks, po_ord_date, po_due_date, po_status " +
                        " FROM  po_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by po_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select po_nbr, po_vend, po_site, po_rmks, po_ord_date, po_due_date, po_status " +
                        " FROM  po_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by po_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select po_nbr, po_vend, po_site, po_rmks, po_ord_date, po_due_date, po_status  " +
                        " FROM  po_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by po_nbr desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("po_nbr"),
                                   res.getString("po_vend"),
                                   res.getString("po_site"),
                                   res.getString("po_rmks"),
                                   res.getString("po_ord_date"),
                                   res.getString("po_due_date"),
                                   res.getString("po_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
        public static DefaultTableModel getDOBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "DO Nbr", "From WH", "To WH", "Ref", "ShipDate", "RecvDate", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select do_nbr, do_wh_from, do_wh_to, do_ref, do_shipdate, do_recvdate, do_status " +
                        " FROM  do_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by do_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select do_nbr, do_wh_from, do_wh_to, do_ref, do_shipdate, do_recvdate, do_status " +
                        " FROM  do_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by do_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select do_nbr, do_wh_from, do_wh_to, do_ref, do_shipdate, do_recvdate, do_status  " +
                        " FROM  do_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by do_nbr desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("do_nbr"),
                                   res.getString("do_wh_from"),
                                   res.getString("do_wh_to"),
                                   res.getString("do_ref"),
                                   res.getString("do_shipdate"),
                                   res.getString("do_recvdate"),
                                   res.getString("do_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
        public static DefaultTableModel getSVBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "SV Nbr", "Cust", "PO", "ShedDate", "Type", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select sv_nbr, sv_cust, sv_po, sv_due_date, sv_type, sv_status " +
                        " FROM  sv_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sv_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select sv_nbr, sv_cust, sv_po, sv_due_date, sv_type, sv_status  " +
                        " FROM  sv_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sv_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select sv_nbr, sv_cust, sv_po, sv_due_date, sv_type, sv_status   " +
                        " FROM  sv_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sv_nbr desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("sv_nbr"),
                                   res.getString("sv_cust"),
                                   res.getString("sv_po"),
                                   res.getString("sv_due_date"),
                                   res.getString("sv_type"),
                                   res.getString("sv_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
        
        public static DefaultTableModel getFOBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "FrieghtNbr", "Carrier", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select fo_nbr, fo_carrier, fo_status " +
                        " FROM  fo_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by fo_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("select fo_nbr, fo_carrier, fo_status  " +
                        " FROM  fo_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by fo_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select fo_nbr, fo_carrier, fo_status  " +
                        " FROM  fo_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by fo_nbr desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fo_nbr"),
                                   res.getString("fo_carrier"),
                                   res.getString("fo_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
               } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
           public static DefaultTableModel getShipperBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ShipperNbr", "Cust", "Ship", "OrderNbr", "PONbr", "ShipDate", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                   if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then 'closed' else 'open' end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then 'closed' else 'open' end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sh_id desc ;");  
                   }
                }
                if (state == 2) { // ends
                    if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then 'closed' else 'open' end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then 'closed' else 'open' end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sh_id desc ;");  
                   }
                }
                 if (state == 0) { // match
                 if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then 'closed' else 'open' end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then 'closed' else 'open' end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sh_id desc ;");  
                   }
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("sh_id"),
                                   res.getString("sh_cust"),
                                   res.getString("sh_ship"),
                                   res.getString("sh_so"),
                                   res.getString("sh_po"),
                                   res.getString("sh_shipdate"),
                                   res.getString("sh_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
           
           
        public static DefaultTableModel getInvoiceBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "InvoiceNbr", "Cust", "Ship", "OrderNbr", "PONbr", "ShipDate", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                   if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then 'closed' else 'open' end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then 'closed' else 'open' end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sh_id desc ;");  
                   }
                }
                if (state == 2) { // ends
                    if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then 'closed' else 'open' end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then 'closed' else 'open' end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sh_id desc ;");  
                   }
                }
                 if (state == 0) { // match
                 if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then 'closed' else 'open' end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then 'closed' else 'open' end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sh_id desc ;");  
                   }
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("sh_id"),
                                   res.getString("sh_cust"),
                                   res.getString("sh_ship"),
                                   res.getString("sh_so"),
                                   res.getString("sh_po"),
                                   res.getString("sh_shipdate"),
                                   res.getString("sh_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
          
           
          public static DefaultTableModel getCarrierBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "SCAC", "Phone", "Email", "Contact", "Acct"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT car_code, car_desc, car_scac, car_phone, car_email, car_contact, car_acct " +
                        " FROM  car_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by car_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT car_code, car_desc, car_scac, car_phone, car_email, car_contact, car_acct " +
                        " FROM  car_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by car_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT car_code, car_desc, car_scac, car_phone, car_email, car_contact, car_acct  " +
                        " FROM  car_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by car_code ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("car_code"),
                                   res.getString("car_desc"),
                                   res.getString("car_scac"),
                                   res.getString("car_phone"),
                                   res.getString("car_email"),
                                   res.getString("car_contact"),
                                   res.getString("car_acct")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
               } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
           public static DefaultTableModel getProdCodeBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "InventoryAcct", "SalesAccount", "POReceiptAccount", "ScrapAccount"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT pl_line, pl_desc, pl_inventory, pl_sales, pl_po_rcpt, pl_scrap " +
                        " FROM  pl_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by pl_line ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT pl_line, pl_desc, pl_inventory, pl_sales, pl_po_rcpt, pl_scrap  " +
                        " FROM  pl_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by pl_line ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT pl_line, pl_desc, pl_inventory, pl_sales, pl_po_rcpt, pl_scrap  " +
                        " FROM  pl_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by pl_line ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("pl_line"),
                                   res.getString("pl_desc"),
                                   res.getString("pl_inventory"),
                                   res.getString("pl_sales"),
                                   res.getString("pl_po_rcpt"),
                                   res.getString("pl_scrap")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
          
           public static DefaultTableModel getLocationBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "LocationCode", "Desc", "Site", "Active"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT loc_loc, loc_desc, loc_site, loc_active " +
                        " FROM  loc_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by loc_loc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT loc_loc, loc_desc, loc_site, loc_active " +
                        " FROM  loc_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by loc_loc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT loc_loc, loc_desc, loc_site, loc_active  " +
                        " FROM  loc_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by loc_loc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("loc_loc"),
                                   res.getString("loc_desc"),
                                   res.getString("loc_site"),
                                   res.getString("loc_active")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
          
           public static DefaultTableModel getFTPBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "FTPID", "Desc", "IP", "Login", "Passwd", "CDDir", "InDir", "OutDir", "Delete?"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_chgdir, ftp_indir, ftp_outdir, ftp_delete " +
                        " FROM  ftp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by ftp_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_chgdir, ftp_indir, ftp_outdir, ftp_delete " +
                        " FROM  ftp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by ftp_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_chgdir, ftp_indir, ftp_outdir, ftp_delete  " +
                        " FROM  ftp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by ftp_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("ftp_id"),
                                   res.getString("ftp_desc"),
                                   res.getString("ftp_ip"),
                                   res.getString("ftp_login"),
                                   res.getString("ftp_passwd"),
                                   res.getString("ftp_chgdir"),
                                   res.getString("ftp_indir"),
                                   res.getString("ftp_outdir"),
                                   res.getString("ftp_delete")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
            public static DefaultTableModel getWareHouseBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "WHCode", "Site", "Name", "Addr1", "City", "State", "Zip"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT wh_id, wh_site, wh_name, wh_addr1, wh_city, wh_state, wh_zip " +
                        " FROM  wh_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by wh_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT wh_id, wh_site, wh_name, wh_addr1, wh_city, wh_state, wh_zip  " +
                        " FROM  wh_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by wh_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT wh_id, wh_site, wh_name, wh_addr1, wh_city, wh_state, wh_zip   " +
                        " FROM  wh_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by wh_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("wh_id"),
                                   res.getString("wh_site"),
                                   res.getString("wh_name"),
                                   res.getString("wh_addr1"),
                                   res.getString("wh_city"),
                                   res.getString("wh_state"),
                                   res.getString("wh_zip")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
           
           
            public static DefaultTableModel getBankBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "BankID", "Desc", "Account", "Currency", "Active"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT bk_id, bk_desc, bk_acct, bk_cur, bk_active " +
                        " FROM  bk_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by bk_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT bk_id, bk_desc, bk_acct, bk_cur, bk_active " +
                        " FROM  bk_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by bk_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT bk_id, bk_desc, bk_acct, bk_cur, bk_active  " +
                        " FROM  bk_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by bk_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("bk_id"),
                                   res.getString("bk_desc"),
                                   res.getString("bk_acct"),
                                   res.getString("bk_cur"),
                                   res.getString("bk_active")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
             
            
             public static DefaultTableModel getVoucherBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "VouchID", "Vend", "RecvID", "InvoiceNbr", "TotalAmt", "VOPrice", "Qty"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
     try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select vod_id, ap_vend, vod_rvdid, vod_invoice, ap_amt, vod_voprice, vod_qty " +
                        " FROM  vod_mstr inner join ap_mstr on vod_id = ap_nbr where " + myfield + " like " + "'" + str + "%'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select vod_id, ap_vend, vod_rvdid, vod_invoice, ap_amt, vod_voprice, vod_qty  " +
                        " FROM  vod_mstr inner join ap_mstr on vod_id = ap_nbr where " + myfield + " like " + "'%" + str + "'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select vod_id, ap_vend, vod_rvdid, vod_invoice, ap_amt, vod_voprice, vod_qty   " +
                        " FROM  vod_mstr inner join ap_mstr on vod_id = ap_nbr where " + myfield + " like " + "'%" + str + "%'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("vod_id"),
                                   res.getString("ap_vend"),
                                   res.getString("vod_rvdid"),
                                   res.getString("vod_invoice"),
                                   res.getString("ap_amt"),
                                   res.getString("vod_voprice"),
                                   res.getString("vod_qty")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
               } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
            
              public static DefaultTableModel getARPaymentBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ARBatch", "Cust", "Date", "Amt"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select ar_nbr, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr  where " + myfield + " like " + "'" + str + "%'" +
                        " and ar_type = 'P' order by ar_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select ar_nbr, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and ar_type = 'P' order by ar_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select ar_nbr, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and ar_type = 'P' order by ar_nbr desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("ar_nbr"),
                                   res.getString("ar_cust"),
                                   res.getString("ar_effdate"),
                                   res.getString("ar_amt")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
            
        public static DefaultTableModel getExpenseBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Batch", "Vend", "Date", "Amt"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select ap_nbr, ap_vend, ap_effdate, ap_amt " +
                        " FROM  ap_mstr  where " + myfield + " like " + "'" + str + "%'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select ap_nbr, ap_vend, ap_effdate, ap_amt " +
                        " FROM  ap_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select ap_nbr, ap_vend, ap_effdate, ap_amt " +
                        " FROM  ap_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("ap_nbr"),
                                   res.getString("ap_vend"),
                                   res.getString("ap_effdate"),
                                   res.getString("ap_amt")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
               } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
           
        public static DefaultTableModel getReceiverBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "RecvID", "Vend", "PO", "PackingSlip", "Item", "RecvDate", "Qty"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select rv_id, rv_vend, rvd_po, rvd_packingslip, rvd_part, rvd_date, rvd_qty " +
                        " FROM  recv_det inner join recv_mstr on rv_id = rvd_id where " + myfield + " like " + "'" + str + "%'" +
                        " order by rv_id desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select rv_id, rv_vend, rvd_po, rvd_packingslip, rvd_part, rvd_date, rvd_qty  " +
                        " FROM  recv_det inner join recv_mstr on rv_id = rvd_id where " + myfield + " like " + "'%" + str + "'" +
                        " order by rv_id desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select rv_id, rv_vend, rvd_po, rvd_packingslip, rvd_part, rvd_date, rvd_qty   " +
                        " FROM  recv_det inner join recv_mstr on rv_id = rvd_id where " + myfield + " like " + "'%" + str + "%'" +
                        " order by rv_id desc ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("rv_id"),
                                   res.getString("rv_vend"),
                                   res.getString("rvd_po"),
                                   res.getString("rvd_packingslip"),
                                   res.getString("rvd_part"),
                                   res.getString("rvd_date"),
                                   res.getString("rvd_qty")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
            
            public static DefaultTableModel getDeptCCBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Dept", "Desc", "LaborAcct", "BurdenAccount", "COPAccount"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT dept_id, dept_desc, dept_lbr_acct, dept_bdn_acct, dept_cop_acct " +
                        " FROM  dept_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by dept_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT dept_id, dept_desc, dept_lbr_acct, dept_bdn_acct, dept_cop_acct " +
                        " FROM  dept_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by dept_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT dept_id, dept_desc, dept_lbr_acct, dept_bdn_acct, dept_cop_acct  " +
                        " FROM  dept_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by dept_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("dept_id"),
                                   res.getString("dept_desc"),
                                   res.getString("dept_lbr_acct"),
                                   res.getString("dept_bdn_acct"),
                                   res.getString("dept_cop_acct")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
            
             public static DefaultTableModel getCalendarBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Year", "Period", "StartDate", "EndDate", "Status"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery(" select glc_year, glc_per, glc_start, glc_end, glc_status " +
                        " FROM  gl_cal where " + myfield + " like " + "'" + str + "%'" +
                        " order by glc_year, glc_per ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select glc_year, glc_per, glc_start, glc_end, glc_status " +
                        " FROM  gl_cal where " + myfield + " like " + "'%" + str + "'" +
                        " order by glc_year, glc_per ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select glc_year, glc_per, glc_start, glc_end, glc_status  " +
                        " FROM  gl_cal where " + myfield + " like " + "'%" + str + "%'" +
                        " order by glc_year, glc_per ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("glc_year"),
                                   res.getString("glc_per"),
                                   res.getString("glc_start"),
                                   res.getString("glc_end"),
                                   res.getString("glc_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
           
          public static DefaultTableModel getEmpBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "EmpNbr", "LastName", "FirstName", "Status", "StartDate", "Type", "Phone"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT emp_nbr, emp_lname, emp_fname, emp_status, emp_startdate, emp_type, emp_phone " +
                        " FROM  emp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by emp_lname ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT  emp_nbr, emp_lname, emp_fname, emp_status, emp_startdate, emp_type, emp_phone " +
                        " FROM  emp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by emp_lname ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT  emp_nbr, emp_lname, emp_fname, emp_status, emp_startdate, emp_type, emp_phone  " +
                        " FROM  emp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by emp_lname ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("emp_nbr"),
                                   res.getString("emp_lname"),
                                   res.getString("emp_fname"),
                                   res.getString("emp_status"),
                                   res.getString("emp_startdate"),
                                   res.getString("emp_type"),
                                   res.getString("emp_phone")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
          
           public static DefaultTableModel getClockRecBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              //  "RecID", "EmpID", "LastName", "FirstName", "Dept", "Code", "InDate", "InTime", "InTmAdj", "OutDate", "OutTime", "OutTmAdj", "tothrs"
                      new String[]{"select", "RecID", "EmpID", "LastName", "FirstName", "Dept", "Code", "InDate", "InTime", "InTmAdj", "OutDate", "OutTime", "OutTmAdj", "tothrs"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                     res = st.executeQuery("SELECT tothrs, recid, t.emp_nbr as 't_emp_nbr', emp_lname, emp_fname, emp_dept, code_id, indate, intime, " +
                           " intime_adj, outdate, outtime, outtime_adj FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where  " + myfield + " like " + "'" + str + "%'" +
                               " order by recid limit 300 " +
                               ";" );
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT tothrs, recid, t.emp_nbr as 't_emp_nbr', emp_lname, emp_fname, emp_dept, code_id, indate, intime, " +
                           " intime_adj, outdate, outtime, outtime_adj FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where  " + myfield + " like " + "'%" + str + "'" +
                               " order by recid limit 300 " +
                               ";" );
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT tothrs, recid, t.emp_nbr as 't_emp_nbr', emp_lname, emp_fname, emp_dept, code_id, indate, intime, " +
                           " intime_adj, outdate, outtime, outtime_adj FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where  " + myfield + " like " + "'%" + str + "%'" +
                               " order by recid limit 300 " +
                               ";" );
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object []{BlueSeerUtils.clickflag, res.getString("recid"),
                                            res.getString("t_emp_nbr"),
                                            res.getString("emp_lname"),
                                            res.getString("emp_fname"),
                                            res.getString("emp_dept"),
                                            res.getString("code_id"),
                                             res.getString("indate"),
                                            res.getString("intime"),
                                            res.getString("intime_adj"),
                                            res.getString("outdate"),
                                            res.getString("outtime"),
                                            res.getString("outtime_adj"),
                                            res.getString("tothrs")
                                            } );
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
         
          
             public static DefaultTableModel getUserBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "UserID", "LastName", "FirstName"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT user_id, user_lname, user_fname " +
                        " FROM  user_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by user_lname ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT user_id, user_lname, user_fname  " +
                        " FROM  user_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by user_lname ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT user_id, user_lname, user_fname  " +
                        " FROM  user_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by user_lname ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("user_id"),
                                   res.getString("user_lname"),
                                   res.getString("user_fname")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
        
            public static DefaultTableModel getMenuBrowseUtil( String str, int state, String myfield) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ID", "Desc", "Panel", "Type"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT menu_id, menu_desc, menu_panel, menu_type " +
                        " FROM  menu_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by menu_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT  menu_id, menu_desc, menu_panel, menu_type " +
                        " FROM  menu_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by menu_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT  menu_id, menu_desc, menu_panel, menu_type  " +
                        " FROM  menu_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by menu_id ;");
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("menu_id"),
                                   res.getString("menu_desc"),
                                   res.getString("menu_panel"),
                                   res.getString("menu_type")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         } 
          
    public static DefaultTableModel getPrintersAll() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Printer", "Desc", "Type", "IP", "Port"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                  res = st.executeQuery("select * from prt_mstr;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("prt_id"),
                                   res.getString("prt_desc"),
                                   res.getString("prt_type"),
                                   res.getString("prt_ip"),
                                   res.getString("prt_port")
                                  
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
    
     public static DefaultTableModel getCustEDIAll() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Cust", "DocType", "Dir", "Map", "FilePath", "Prefix", "Suffix"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
               
                  res = st.executeQuery("select * from edi_mstr;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("edi_id"),
                                   res.getString("edi_doc"),
                                   res.getString("edi_dir"),
                                   res.getString("edi_map"),
                                   res.getString("edi_filepath"),
                                   res.getString("edi_fileprefix"),
                                   res.getString("edi_filesuffix")
                                  
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
               } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
     
      public static DefaultTableModel getLabelFileAll() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "LabelCode", "Desc", "File", "Type"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                  res = st.executeQuery("select * from label_zebra;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("lblz_code"),
                                   res.getString("lblz_desc"),
                                   res.getString("lblz_file"),
                                   res.getString("lblz_type")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
      
      
       public static DefaultTableModel getForecastAll() {
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Item", "Site", "Year", "Wk1", "Wk2", "Wk3"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                  res = st.executeQuery("select * from fct_mstr;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fct_part"),
                                   res.getString("fct_site"),
                                   res.getString("fct_year"),
                                   res.getString("fct_wkqty1"),
                                   res.getString("fct_wkqty2"),
                                   res.getString("fct_wkqty3")
                                   
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
      
      
    public static DefaultTableModel getForecast13weeksByPart(String frompart, String topart, int wk) {
         Calendar cal = Calendar.getInstance();
        cal.getTime();
       
        
        
        ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));
        
        String thisyear = String.valueOf(cal.get(Calendar.YEAR));
        
        DateFormat df = new SimpleDateFormat("MM/dd");
        // week dates are base 0
        String wk1 = df.format(dates.get(wk - 1));
        String wk2 = df.format(dates.get(wk));
        String wk3 = df.format(dates.get(wk + 1));
        String wk4 = df.format(dates.get(wk + 2));
        String wk5 = df.format(dates.get(wk + 3));
        String wk6 = df.format(dates.get(wk + 4));
        String wk7 = df.format(dates.get(wk + 5));
        String wk8 = df.format(dates.get(wk + 6));
        String wk9 = df.format(dates.get(wk + 7));
        String wk10 = df.format(dates.get(wk + 8));
        String wk11 = df.format(dates.get(wk + 9));
        String wk12 = df.format(dates.get(wk + 10));
        String wk13 = df.format(dates.get(wk + 11));
        
        
       
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"Select", "Part", "Year", "Site", wk1, wk2, wk3, wk4, wk5, wk6, wk7, wk8, wk9, wk10, wk11, wk12, wk13 })
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            Integer[] values = new Integer[13];
            try{
                  
                  
                  res = st.executeQuery("select * from fct_mstr where fct_part >= " + "'" + frompart + "'" +
                                       " AND fct_part <= " + "'" + topart + "'" + 
                                       " AND fct_year = " + "'" + thisyear + "'" + 
                                       ";" );
                    while (res.next()) {
                        values = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0};
                        for (int k = 0 ; k < 13; k++) {
                             if ((wk + k) > 52) { continue;}
                          values[k] = res.getInt("fct_wkqty" + (wk + k));
                        }
                        
                                                
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fct_part"),
                                   res.getString("fct_year"),
                                   res.getString("fct_site"),
                                   values[0],
                                   values[1],
                                   values[2],
                                   values[3],
                                   values[4],
                                   values[5],
                                   values[6],
                                   values[7],
                                   values[8],
                                   values[9],
                                   values[10],
                                   values[11],
                                   values[12]
                                   
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
      
     
        
           public static DefaultTableModel getCallCenterReport1() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                         new String[]{"CallID", "Name", "Phone", "Date", "Type", "Desc"});
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

              res = st.executeQuery("SELECT call_id, call_name, call_phone, call_date, call_type, call_desc FROM  call_mstr where call_date >= '2013-01-01' order by abs(call_id) desc;");

                while (res.next()) {
                    
                    mymodel.addRow(new Object[]{res.getString("call_id"),
                                res.getString("call_name"),
                                res.getString("call_phone"),
                                res.getString("call_date"),
                                res.getString("call_type"),
                                res.getString("call_desc")
                            });
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       }   
       
        public static DefaultTableModel getPlantDirectory() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                         new String[]{"LastName", "FirstName", "Phone", "Cell", "Email"});
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

              res = st.executeQuery("SELECT * FROM  user_mstr order by user_lname;");

                while (res.next()) {
                  
                    mymodel.addRow(new Object[]{
                        res.getString("user_lname"),
                        res.getString("user_fname"),
                        res.getString("user_phone"),
                        res.getString("user_cell"),
                        res.getString("user_email")
                            });
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       }   
         
          public static DefaultTableModel getNavCodeList() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                         new String[]{"Menu", "Desc", "NavCode", "Type", "JavaClass"});
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

              res = st.executeQuery("SELECT * from menu_mstr order by menu_id;");

                while (res.next()) {
                  
                    mymodel.addRow(new Object[]{
                        res.getString("menu_id"),
                        res.getString("menu_desc"),
                        res.getString("menu_navcode"),
                        res.getString("menu_type"),
                        res.getString("menu_panel")
                            });
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       }   
       
        
       public static DefaultTableModel getReqByApprover(String arg) {
           
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "ReqID", "Requestor", "Date", "Type", "Vend", "Amt", "Owner"}) {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 6 )       
                            return Double.class; 
                        else if (col == 0)
                            return ImageIcon.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
             
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

               res = st.executeQuery("SELECT * FROM req_task inner join req_mstr where reqt_owner =  " +
                        "'" + bsmf.MainFrame.userid + "'" + " AND reqt_status = 'pending' AND reqt_id = req_id " +
                        " order by req_id desc;");

                
               // res = st.executeQuery("SELECT * FROM req_task inner join req_mstr where  " +
     //                   " reqt_status = 'pending' AND reqt_id = req_id " +
             //           " order by req_id desc;");
                while (res.next()) {
                     mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("req_id"),
                                res.getString("req_name"),
                                res.getString("req_date"),
                                res.getString("req_type"),
                                res.getString("req_vend"),
                                res.getDouble("req_amt"),
                                res.getString("reqt_owner")
                            });
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       }  
       
        public static DefaultTableModel getReqAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "ReqID", "Requestor", "Date", "PO", "Vend", "Amt", "Status"}) 
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 6 )       
                            return Double.class;  
                        else if (col == 0) 
                            return ImageIcon.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
           
           try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

             res = st.executeQuery("SELECT * FROM req_mstr order by req_id desc;");

                while (res.next()) {
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("req_id"),
                                res.getString("req_name"),
                                res.getString("req_date"),
                                res.getString("req_po"),
                                res.getString("req_vend"),
                                res.getDouble("req_amt"),
                                res.getString("req_status")
                                });
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
          } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
        
        public static DefaultTableModel getGLAcctAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "Acct", "Desc", "Type", "Currency"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
             res = st.executeQuery("SELECT * FROM ac_mstr order by ac_id;");

                while (res.next()) {
                    

                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("ac_id"),
                                res.getString("ac_desc"),
                                res.getString("ac_type"),
                                res.getString("ac_cur")
                                });
                }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
        
        public static DefaultTableModel getItemRoutingAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Item", "WorkFlowID", "Operation", "Component"});
           
         try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              res = st.executeQuery("SELECT it_item, it_wf, ps_op, ps_child FROM item_mstr " +
                        " inner join pbm_mstr on ps_parent = it_item " +
                        " where it_code = 'M' order by it_item ;");

                while (res.next()) {
                    
                    mymodel.addRow(new Object[]{res.getString("it_item"),
                                res.getString("it_wf"),
                                res.getString("ps_op"),
                                res.getString("ps_child")
                                });
                }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
        
         public static DefaultTableModel getItemBrowse() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "Item", "Desc", "Code", "ProdLine", "Group", "Loc", "WH", "CreateDate", "SellPrice", "PurchPrice", "Revision"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else if (col == 9 || col == 10) {
                            return Double.class;
                        }
                        else return String.class;  //other columns accept String values  
                      }  
                        };
           
           try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

              res = st.executeQuery("SELECT it_item, it_desc, it_code, it_prodline, it_group, it_loc, it_wh, " +
                        " it_createdate, it_sell_price, it_pur_price, it_rev from item_mstr order by it_item; ") ;
                while (res.next()) {
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("it_item"),
                                res.getString("it_desc"),
                                res.getString("it_code"),
                                res.getString("it_prodline"),
                                res.getString("it_group"),
                                res.getString("it_loc"),
                                res.getString("it_wh"),
                                res.getString("it_createdate"),
                                res.getDouble("it_sell_price"),
                                res.getDouble("it_pur_price"),
                                res.getString("it_rev")
                                });
                }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
        
        
         public static DefaultTableModel getEmployeeAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "EmpID", "LastName", "FirstName", "Dept", "Status", "Shift", "Type", "StartDate", "TermDate"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
           
           try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

                res = st.executeQuery("SELECT * FROM emp_mstr order by emp_nbr;");

                while (res.next()) {
                   

                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getInt("emp_nbr"),
                                res.getString("emp_lname"),
                                res.getString("emp_fname"),
                                res.getString("emp_dept"),
                                res.getString("emp_status"),
                                res.getString("emp_shift"),
                                res.getString("emp_type"),
                                res.getString("emp_startdate"),
                                res.getString("emp_termdate")
                                });
                }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
         
         public static DefaultTableModel getGenCodeAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
           new String[]{"select", "Code", "Key", "Value/Desc"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

                res = st.executeQuery("SELECT code_code, code_key, code_value " +
                        "from code_mstr order by code_code ;");

                while (res.next()) {
                    
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, 
                        res.getString("code_code"),
                        res.getString("code_key"),
                        res.getString("code_value")
                            });
                }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
         
          public static DefaultTableModel getWorkCellAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
           new String[]{"select","Cell", "Desc", "Site", "Dept/CC", "SetupRate$", "LaborRate$", "BurdenRate$", "RunCrewSize", "SetupCrewSize", "Remarks"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
           
           try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

                  res = st.executeQuery("select * from wc_mstr;");
                    while (res.next()) {
                        
                        mymodel.addRow(new Object[]{BlueSeerUtils.clickflag,
                            res.getString("wc_cell"),
                                res.getString("wc_desc").replace(",", ""),
                                res.getString("wc_site"),
                                res.getString("wc_cc"),
                                res.getString("wc_setup_rate"),
                                res.getString("wc_run_rate"),
                                res.getString("wc_bdn_rate"),
                                res.getString("wc_run_crew"),
                                res.getString("wc_setup"),
                                res.getString("wc_remarks").replace(",", "") });
                    }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
        
         public static DefaultTableModel getReqPending() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "ReqID", "Requestor", "Date", "Vend", "Amt", "Status", "Approver", "Desc"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 5 )       
                            return Double.class;  
                        else if (col == 0)
                            return ImageIcon.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
           
         try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

              res = st.executeQuery("SELECT * FROM  req_mstr inner join req_task on reqt_id = req_id where req_status = 'pending' and reqt_status = 'pending' order by req_id desc;");

                while (res.next()) {
                 
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("req_id"),
                                res.getString("req_name"),
                                res.getString("req_date"),
                                res.getString("req_vend"),
                                res.getDouble("req_amt"),
                                res.getString("req_status"),
                                res.getString("reqt_owner"),
                                res.getString("req_desc")
                            });
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
           
           
           
           return mymodel;
       } 
         
          public static DefaultTableModel getReqApproved() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "ReqID", "Requestor", "Date", "PO", "Vend", "Amt", "Status"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 6 )       
                            return Double.class;  
                        else if (col == 0)
                            return ImageIcon.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

              res = st.executeQuery("SELECT * FROM req_mstr where req_status = 'approved' order by req_id desc;");

                while (res.next()) {
                   
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("req_id"),
                                res.getString("req_name"),
                                res.getString("req_date"),
                                res.getString("req_po"),
                                res.getString("req_vend"),
                                res.getDouble("req_amt"),
                                res.getString("req_status")
                                });
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
         
         public static DefaultTableModel getUserAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                       new String[]{"UserID", "LastName", "FirstName", "Phone", "Cell", "Email"});
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

               res = st.executeQuery("SELECT * FROM  user_mstr order by user_lname desc;");

                while (res.next()) {
                    
                    mymodel.addRow(new Object[]{
                        res.getString("user_id"),
                        res.getString("user_lname"),
                        res.getString("user_fname"),
                        res.getString("user_phone"),
                        res.getString("user_cell"),
                        res.getString("user_email")
                            });
                }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
         
         public static DefaultTableModel getProdCodeAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "ProdCode", "Desc"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
           
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

              res = st.executeQuery("SELECT * FROM  pl_mstr order by pl_line;");

                while (res.next()) {
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("pl_line"),
                                res.getString("pl_desc")
                            });
                }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
        
          public static DefaultTableModel getQPRAll() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                         new String[]{"select", "Complaint#", "Originator", "PartNbr", "PartDesc", "Supplier", "DateAdded", "DateLastUpdate"})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
           
         try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
res = st.executeQuery("SELECT * FROM  qual_mstr order by qual_id;");

                while (res.next()) {
                    
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("qual_id"), res.getString("qual_originator"), res.getString("qual_part"),
                                res.getString("qual_part_desc"),
                                res.getString("qual_vend"),
                                res.getString("qual_date_crt"),
                                res.getString("qual_date_upd"),});
                }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
      
         public static DefaultTableModel getShipperAll() {
           DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
                df.setMinimumFractionDigits(2);
                df.setMaximumFractionDigits(2);
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Shipper", "Cust", "PartNbr", "PONbr", "Qty", "Price", "DateShipped"});
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("SELECT * FROM  ship_mstr inner join ship_det on shdet_id = ship_id order by ship_id;");

                while (res.next()) {
                 
                    mymodel.addRow(new Object[]{res.getString("shdet_id"), res.getString("ship_cust"), res.getString("shdet_part"),
                                res.getString("shdet_po"),
                                res.getDouble("shdet_ship_qty"),
                                df.format(res.getDouble("shdet_ship_price")),
                                res.getString("shdet_shdet_date"),});
                }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       }   
          
            public static DefaultTableModel getOrderOpen() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                         new String[]{"Order", "Cust", "PartNbr", "Description", "PONbr", "QtyOrd", "QtySHipped"});
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
            
                res = st.executeQuery("SELECT sod_nbr, so_cust, sod_part, ifnull(it_desc,'') as 'description', sod_po, sod_ord_qty, sod_shipped_qty FROM  so_mstr inner join sod_det on sod_nbr = so_nbr " +
                        " left outer join item_mstr on it_item = sod_part " +
                        " where (so_status = 'open' or so_status = 'backorder') and (sod_status <> 'Shipped' or sod_status is null) order by so_nbr;");

                while (res.next()) {
                  
                    mymodel.addRow(new Object[]{res.getString("sod_nbr"), res.getString("so_cust"), res.getString("sod_part"),
                                res.getString("description"),
                                res.getString("sod_po"),
                                res.getString("sod_ord_qty"),
                                res.getString("sod_shipped_qty")
                                });
                }

               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
           
           
           
           return mymodel;
       } 
          
             public static DefaultTableModel getDBSchema() {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                       new String[]{"TableName", "ColumnName", "ColumnType"});
           
          try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
               if (dbtype.equals("sqlite")) {
                  res = st.executeQuery("select m.name as tablename, p.name as columnname, p.type as columntype from sqlite_master m left outer join pragma_table_info((m.name)) p on m.name <> p.name order by tablename, columnname;"); 
                   while (res.next()) {
                    // String[] sql = res.getString("sql").split(",", -1);
                    mymodel.addRow(new Object[]{
                        res.getString("tablename"),
                        res.getString("columnname"),
                        res.getString("columntype")});
                  }
               } else {
                 res = st.executeQuery("SELECT table_name, column_name, column_type from information_schema.columns where table_schema = 'bsdb' ;");  
                  while (res.next()) {
                    mymodel.addRow(new Object[]{
                        res.getString("table_name"),
                        res.getString("column_name"),
                        res.getString("column_type")
                            });
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
           
           
           
           return mymodel;
       } 
        
             
             
         
          public static DefaultTableModel getRoutingsAll() {
              
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "RoutingID", "Operation", "Operation Desc", "Site", "Reportable", "Work Cell", "SetupHours", "RunHours"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   
                      res = st.executeQuery("select * from wf_mstr;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("wf_id"),
                                   res.getString("wf_op"),
                                res.getString("wf_desc").replace(",", ""),
                                res.getString("wf_site"),
                                res.getString("wf_assert"),
                                res.getString("wf_cell"),
                                res.getString("wf_setup_hours"),
                                res.getString("wf_run_hours")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
          
           public static DefaultTableModel getLocationsAll() {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "LocationID", "Desc", "Site", "Active"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   
                      res = st.executeQuery("select * from loc_mstr;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("loc_loc"),
                                   res.getString("loc_desc").replace(",", ""),
                                   res.getString("loc_site"),
                                res.getString("loc_active") 
                        } );
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
           
           public static DefaultTableModel getWareHousesAll() {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "WH ID", "Site", "Name", "City", "State", "Zip"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   
                      res = st.executeQuery("select * from wh_mstr;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("wh_id"),
                                   res.getString("wh_site"),
                                   res.getString("wh_name"),
                                   res.getString("wh_city"),
                                   res.getString("wh_state"),
                                   res.getString("wh_zip")
                        } );
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
           
            public static DefaultTableModel getDeptsAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Dept", "Desc", "COPAcct", "LBRAcct", "BDNAcct"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   
                      res = st.executeQuery("select * from dept_mstr;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("dept_id"),
                                   res.getString("dept_desc").replace(",", ""),
                                   res.getString("dept_cop_acct"),
                                res.getString("dept_lbr_acct"),
                                res.getString("dept_bdn_acct")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
            
            public static DefaultTableModel getBankAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Bank", "Desc", "Acct", "Currency", "Active"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from bk_mstr;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("bk_id"),
                                   res.getString("bk_desc").replace(",", ""),
                                   res.getString("bk_acct"),
                                res.getString("bk_cur"),
                                res.getString("bk_active")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
           
            public static DefaultTableModel getUnPostedGLTrans() {
              javax.swing.table.DefaultTableModel mymodel =  new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"ID", "Acct", "AcctDesc", "CC", "Type", "Ref", "Desc", "EffDate", "EntDate", "Amt"}); 
           
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                       res = st.executeQuery("SELECT glt_id, glt_acct, glt_cc, glt_ref, glt_effdate, glt_entdate, glt_base_amt, glt_desc, glt_type, ac_desc " +
                        "from gl_tran inner join ac_mstr on ac_id = glt_acct order by glt_id desc ;");
                               
                    while (res.next()) {
                        
                        mymodel.addRow(new Object[] {res.getInt("glt_id"),
                                   res.getString("glt_acct"),
                                   res.getString("ac_desc"),
                                res.getString("glt_cc"),
                                res.getString("glt_type"),
                                res.getString("glt_ref"),
                                res.getString("glt_desc"),
                                res.getString("glt_effdate"),
                                res.getString("glt_entdate"),
                                res.getDouble("glt_base_amt")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
            
            
             public static DefaultTableModel getPayRollHours(String fromdate, String todate) {
           
                 javax.swing.table.DefaultTableModel mymodel =  new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "RecID", "EmpID", "LastName", "FirstName", "MidName", "Dept", "Shift", "Supervisor", "Type", "Profile", "JobTitle", "Rate", "tothrs", "Amount"})
                       {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
           
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   double amount = 0.00;
                   
                       res = st.executeQuery("SELECT sum(t.tothrs) as 't.tothrs', t.recid as 't.recid', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', e.emp_mname as 'e.emp_mname', e.emp_jobtitle as 'e.emp_jobtitle', " +
                           " e.emp_supervisor as 'e.emp_supervisor', e.emp_type as 'e.emp_type', e.emp_shift as 'e.emp_shift', e.emp_profile as 'e.emp_profile', e.emp_dept as 'e.emp_dept', e.emp_rate as 'e.emp_rate' " +
                           "  FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where t.indate >= " + "'" + fromdate + "'" +
                               " and t.indate <= " + "'" + todate + "'" + 
                                " and t.ispaid = '0' " +          
                                " group by t.emp_nbr " +       
                                " order by t.emp_nbr " +      
                               ";" );
                     
                    while (res.next()) {
                        amount = res.getDouble("t.tothrs") * res.getDouble("e.emp_rate"); 
                          mymodel.addRow(new Object []{BlueSeerUtils.clickflag, "",
                                            res.getString("t.emp_nbr"),
                                            res.getString("e.emp_lname"),
                                            res.getString("e.emp_fname"),
                                            res.getString("e.emp_mname"),
                                            res.getString("e.emp_dept"),
                                            res.getString("e.emp_shift"),
                                            res.getString("e.emp_supervisor"),
                                            res.getString("e.emp_type"),
                                            res.getString("e.emp_profile"),
                                            res.getString("e.emp_jobtitle"),
                                            res.getString("e.emp_rate"),
                                            res.getString("t.tothrs"),
                                            String.valueOf(amount)
                                            } );
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
            
            
    public static DefaultTableModel getARPaymentBrowse() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"ARNbr", "Cust", "InvoiceNbr", "CheckNbr", "DateApplied", "Amount"}); 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                       res = st.executeQuery("select ar_cust, ar_effdate, ar_id, ard_amt, ar_ref, ard_ref from ar_mstr inner join ard_mstr on ard_id = ar_nbr where ar_type = 'P'; ");
                               
                    while (res.next()) {
                        mymodel.addRow(new Object[] {res.getString("ar_id"),
                                   res.getString("ar_cust"),
                                   res.getString("ard_ref"),
                                res.getString("ar_ref"),
                                res.getString("ar_effdate"),
                                res.getDouble("ard_amt")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
            
            public static DefaultTableModel getMenusAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "MenuID", "Desc", "ClassID"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from menu_mstr order by menu_id;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("menu_id"),
                                   res.getString("menu_desc"),
                                   res.getString("menu_panel")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
            
            public static DefaultTableModel getPanelsAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "ClassID", "Desc"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   
                      res = st.executeQuery("select * from panel_mstr order by panel_id;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("panel_id"),
                                   res.getString("panel_desc")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
            
            public static DefaultTableModel getTermsAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from cust_term;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("cut_code"),
                                   res.getString("cut_desc")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
            
             public static DefaultTableModel getFreightAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "Apply?"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   
                      res = st.executeQuery("select * from frt_mstr;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("frt_code"),
                                   res.getString("frt_desc"),
                                   res.getString("frt_apply")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
             
        public static DefaultTableModel getCarrierAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "SCAC", "Phone", "Email", "Contact", "Acct"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from car_mstr;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("car_code"),
                                   res.getString("car_desc"),
                                   res.getString("car_scac"),
                                   res.getString("car_phone"),
                                   res.getString("car_email"),
                                   res.getString("car_contact"),
                                   res.getString("car_acct")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
        
         public static DefaultTableModel getTaxAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "Element", "Percent", "Userid"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from tax_mstr inner join taxd_mstr on taxd_parentcode = tax_code ;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("tax_code"),
                                   res.getString("tax_desc"),
                                   res.getString("taxd_desc"),
                                   res.getString("taxd_percent"),
                                   res.getString("tax_userid")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
        
           public static DefaultTableModel getPayProfileAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Code", "Desc", "Element", "type", "Amt", "AmtType", "Userid"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   
                      res = st.executeQuery("select * from pay_profile inner join pay_profdet on paypd_parentcode = payp_code ;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("payp_code"),
                                   res.getString("payp_desc"),
                                   res.getString("paypd_desc"),
                                   res.getString("paypd_type"),
                                   res.getString("paypd_amt"),
                                   res.getString("paypd_amttype"),
                                   res.getString("payp_userid")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
        
        
         public static DefaultTableModel getSitesAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Site", "Desc", "Logo", "GenericInvoice", "GenericPackSlip"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{  
                  res = st.executeQuery("select * from site_mstr;" );
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("site_site"),
                                   res.getString("site_desc"),
                                   res.getString("site_logo"),
                                   res.getString("site_iv_jasper"),
                                   res.getString("site_sh_jasper")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
         
       
             public static DefaultTableModel getGLCalendar() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "Year", "Period", "StartDate", "EndDate", "Active"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from gl_cal;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("glc_year"),
                                   res.getString("glc_per"),
                                   res.getString("glc_start"),
                                   res.getString("glc_end"),
                                   res.getString("glc_status")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
        
        public static DefaultTableModel getNoStdCostItems() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"Part", "Desc", "Type"}); 
             
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{     
                      res = st.executeQuery("select it_item, it_desc, it_code from item_mstr where it_item not in (select itc_item from item_cost where itc_item = it_item) order by it_item;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {res.getString("it_item"),
                                   res.getString("it_desc"),
                                   res.getString("it_code")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
               } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
        
             
        public static DefaultTableModel getEDITPAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "TP ID", "Name", "Contact", "Web", "HelpDesk"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                   
                      res = st.executeQuery("select * from editp_mstr order by editp_id;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("editp_id"),
                                   res.getString("editp_name"),
                                   res.getString("editp_contact"),
                                   res.getString("editp_web"),
                                   res.getString("editp_helpdesk")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
        
        
          public static DefaultTableModel getEDITPDOCAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "TP ID", "Name", "Contact", "Web", "HelpDesk"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from edi_mstr order by edi_id;");
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("edi_id"),
                                   res.getString("edi_doctype"),
                                   res.getString("edi_map"),
                                   res.getString("edi_fa_required"),
                                   res.getString("edi_desc")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
             } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
       
            public static DefaultTableModel getCustAddrInfoAll() {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "CustCode", "Market", "Name", "Line1", "City", "State", "Zip"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
            
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{     
                    res = st.executeQuery("SELECT cm_code, cm_market, cm_name, cm_line1, cm_city, cm_state, cm_zip " +
                        "from cm_mstr order by cm_code ;");

                while (res.next()) {
                   
                    mymodel.addRow(new Object[]{ BlueSeerUtils.clickflag,
                        res.getString("cm_code"),
                        res.getString("cm_market"),
                        res.getString("cm_name"),
                         res.getString("cm_line1"),
                          res.getString("cm_city"),
                           res.getString("cm_state"),
                            res.getString("cm_zip")
                            });
                }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
       
     
           public static DefaultTableModel getFreightOrderQuotesTable(String order) {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "OrdNbr", "UniqueID", "Carrier", "DocType", "DocFile", "Dir", "Date"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                   
                    
                    
                    res = st.executeQuery("select fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_dir, fot_date " +
                            " from fot_det " +
                            "  where fot_nbr = " + "'" + order + "'" + 
                            " AND ( fot_doctype = '219' OR fot_doctype = '220') " +
                            ";");
                    while (res.next()) {
                         mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fot_nbr"),
                                   res.getString("fot_uniqueid"),
                                   res.getString("fot_partnerid"),
                                   res.getString("fot_doctype"),
                                   res.getString("fot_docfile"),
                                   res.getString("fot_dir"),
                                   res.getString("fot_date")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
          
           
            public static DefaultTableModel getFreightOrderTendersTable(String order) {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "OrdNbr", "UniqueID", "Carrier", "DocType", "DocFile", "Dir", "Date"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                   
                    
                    
                    res = st.executeQuery("select fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_dir, fot_date " +
                            " from fot_det " +
                            "  where fot_nbr = " + "'" + order + "'" + 
                            " AND ( fot_doctype = '204' OR fot_doctype = '990') " +
                            ";");
                    while (res.next()) {
                         mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fot_nbr"),
                                   res.getString("fot_uniqueid"),
                                   res.getString("fot_partnerid"),
                                   res.getString("fot_doctype"),
                                   res.getString("fot_docfile"),
                                   res.getString("fot_dir"),
                                   res.getString("fot_date")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
           
             public static DefaultTableModel getFreightOrderStatusTable(String order) {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "OrdNbr", "UniqueID", "Carrier", "DocType", "DocFile", "Status", "Remarks", "Lat", "Lon", "Dir", "Date"})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                   
                    
                    
                    res = st.executeQuery("select fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_status, fot_remarks, fot_lat, fot_lon, fot_dir, fot_date " +
                            " from fot_det " +
                            "  where fot_nbr = " + "'" + order + "'" + 
                            " AND fot_doctype = '214' " +
                            ";");
                    while (res.next()) {
                         mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("fot_nbr"),
                                   res.getString("fot_uniqueid"),
                                   res.getString("fot_partnerid"),
                                   res.getString("fot_doctype"),
                                   res.getString("fot_docfile"),
                                   res.getString("fot_status"),
                                   res.getString("fot_remarks"),
                                   res.getString("fot_lat"),
                                   res.getString("fot_lon"),
                                   res.getString("fot_dir"),
                                   res.getString("fot_date")
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mymodel;
        
         }
            
            
    
    
}
