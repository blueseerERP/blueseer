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
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author terryva
 */
public class RPData {
    
    public static DefaultTableModel getPlanByItem(String from, String to) {
        
        boolean autoitem = OVData.isAutoItem();
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), 
                          getGlobalColumnTag("item"), 
                        getGlobalColumnTag("desc"), 
                        getGlobalColumnTag("class"), 
                          getGlobalColumnTag("site"), 
                          getGlobalColumnTag("planid"), 
                          getGlobalColumnTag("type"), 
                          getGlobalColumnTag("order"), 
                          getGlobalColumnTag("issched"), 
                          getGlobalColumnTag("cell"), 
                          getGlobalColumnTag("schedqty"), 
                          getGlobalColumnTag("scheddate"), 
                          getGlobalColumnTag("status")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                 if (autoitem) {
                 res = st.executeQuery("SELECT it_item, it_desc, it_code, it_site, plan_nbr, plan_type, plan_order, case plan_is_sched when '1' then 'yes' else 'no' end plan_is_sched , plan_cell, plan_qty_sched, plan_date_sched, case plan_status when '1' then 'complete' when '0' then 'open' else 'void' end plan_status  " +
                        " FROM  item_mstr left outer join plan_mstr on plan_part = it_item  " +
                        " where cast(it_item as decimal) >= " + "'" + from + "'" +
                        " and cast(it_item as decimal) <= " + "'" + to + "'" +
                        " order by plan_nbr ;");
                 } else {
                  res = st.executeQuery("SELECT it_item, it_desc, it_code, it_site, plan_nbr, plan_type, plan_order, case plan_is_sched when '1' then 'yes' else 'no' end plan_is_sched , plan_cell, plan_qty_sched, plan_date_sched, case plan_status when '1' then 'complete' when '0' then 'open' else 'void' end plan_status  " +
                        " FROM  item_mstr left outer join plan_mstr on plan_part = it_item  " +
                        " where it_item >= " + "'" + from + "'" +
                        " and it_item <= " + "'" + to + "'" +
                        " order by plan_nbr ;");   
                 }
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, res.getString("it_item"),
                                   res.getString("it_desc"),
                                   res.getString("it_code"),
                                   res.getString("it_site"),
                                   res.getString("plan_nbr"),
                                   res.getString("plan_type"),
                                   res.getString("plan_order"),
                                   res.getString("plan_is_sched"),
                                   res.getString("plan_cell"),
                                   res.getString("plan_qty_sched"),
                                   res.getString("plan_date_sched"),
                                   res.getString("plan_status")
                                  
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
    
    public static DefaultTableModel getPlanBySalesOrder(String from, String to) {
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{
                          getGlobalColumnTag("select"), 
                          getGlobalColumnTag("order"), 
                        getGlobalColumnTag("line"), 
                        getGlobalColumnTag("item"), 
                        getGlobalColumnTag("desc"), 
                        getGlobalColumnTag("class"), 
                          getGlobalColumnTag("site"), 
                          getGlobalColumnTag("planid"), 
                          getGlobalColumnTag("type"),  
                          getGlobalColumnTag("issched"), 
                          getGlobalColumnTag("cell"), 
                          getGlobalColumnTag("schedqty"), 
                          getGlobalColumnTag("scheddate"), 
                          getGlobalColumnTag("status")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
               if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }  
                 res = st.executeQuery("SELECT sod_part, sod_nbr, sod_line, it_desc, it_code, it_site, plan_nbr, plan_type, plan_order, case plan_is_sched when '1' then 'yes' else 'no' end plan_is_sched , plan_cell, plan_qty_sched, plan_date_sched, case plan_status when '1' then 'complete' when '0' then 'open' else 'void' end plan_status  " +
                        " FROM  sod_det inner join item_mstr on sod_part = it_item left outer join plan_mstr on plan_order = sod_nbr and plan_line = sod_line  " +
                        " where cast(sod_nbr as decimal) >= " + "'" + from + "'" +
                        " and cast(sod_nbr as decimal) <= " + "'" + to + "'" +
                        " order by sod_line ;");
                
                    while (res.next()) {
                        mymodel.addRow(new Object[] {BlueSeerUtils.clickflag, 
                                   res.getString("sod_nbr"),
                                   res.getString("sod_line"),
                                   res.getString("sod_part"),
                                   res.getString("it_desc"),
                                   res.getString("it_code"),
                                   res.getString("it_site"),
                                   res.getString("plan_nbr"),
                                   res.getString("plan_type"),
                                   res.getString("plan_is_sched"),
                                   res.getString("plan_cell"),
                                   res.getString("plan_qty_sched"),
                                   res.getString("plan_date_sched"),
                                   res.getString("plan_status")
                                  
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
        
    public static DefaultTableModel getItemBrowse(String from, String to) {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("select"), 
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("description"), 
                            getGlobalColumnTag("createdate"), 
                            getGlobalColumnTag("prodline"), 
                            getGlobalColumnTag("class"), 
                            getGlobalColumnTag("group"), 
                            getGlobalColumnTag("location"), 
                            getGlobalColumnTag("warehouse"),  
                            getGlobalColumnTag("sellprice"), 
                            getGlobalColumnTag("purchprice"), 
                            getGlobalColumnTag("revision")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
              res = st.executeQuery("SELECT it_item, it_desc, it_code, it_prodline, " +
                      " it_group, it_loc, it_wh, it_createdate, it_sell_price, " +
                        "  it_pur_price, it_rev from item_mstr " +
                        " where cast(it_item as double) >= " + "'" + from + "'" +
                        " and cast(it_item as double) <= " + "'" + to + "'" +
                        " order by it_item; ") ;
                while (res.next()) {
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, res.getString("it_item"),
                                res.getString("it_desc"),
                                res.getString("it_createdate"),
                                res.getString("it_prodline"),
                                res.getString("it_code"),
                                res.getString("it_group"),
                                res.getString("it_loc"),
                                res.getString("it_wh"),
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

    public static DefaultTableModel getCustBrowseAddrInfo(String from, String to) {
      javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), 
                  getGlobalColumnTag("code"), 
                  getGlobalColumnTag("name"), 
                  getGlobalColumnTag("addr1"), 
                  getGlobalColumnTag("city"), 
                  getGlobalColumnTag("state"), 
                  getGlobalColumnTag("zip"), 
                  getGlobalColumnTag("phone"),
                  getGlobalColumnTag("email")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{    
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                    res = st.executeQuery("SELECT cm_code, cm_market, cm_name, cm_line1, " +
                        " cm_city, cm_state, cm_zip, cm_market, cm_phone, cm_email, " +
                        " cm_terms, cm_bank, cm_curr, cm_ar_acct, cm_onhold " +
                        "from cm_mstr " +
                        " where cast(cm_code as decimal) >= " + "'" + from + "'" +
                        " and cast(cm_code as decimal) <= " + "'" + to + "'" +
                        "order by cm_code ;");

                while (res.next()) {
                   
                    mymodel.addRow(new Object[]{ BlueSeerUtils.clickflag,
                        res.getString("cm_code"),
                        res.getString("cm_name"),
                        res.getString("cm_line1"),
                        res.getString("cm_city"),
                        res.getString("cm_state"),
                        res.getString("cm_zip"),
                        res.getString("cm_phone"),
                        res.getString("cm_email")
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
    
    public static DefaultTableModel getCustBrowseFinInfo(String from, String to) {
      javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), 
                  getGlobalColumnTag("code"), 
                  getGlobalColumnTag("name"), 
                  getGlobalColumnTag("terms"), 
                  getGlobalColumnTag("bank"), 
                  getGlobalColumnTag("currency"), 
                  getGlobalColumnTag("account"), 
                  getGlobalColumnTag("cc"), 
                  getGlobalColumnTag("onhold")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{    
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                    res = st.executeQuery("SELECT cm_code, cm_market, cm_name,  " +
                        " cm_terms, cm_bank, cm_curr, cm_ar_acct, cm_ar_cc, cm_onhold " +
                        "from cm_mstr " +
                        " where cast(cm_code as decimal) >= " + "'" + from + "'" +
                        " and cast(cm_code as decimal) <= " + "'" + to + "'" +
                        "order by cm_code ;");

                while (res.next()) {
                   
                    mymodel.addRow(new Object[]{ BlueSeerUtils.clickflag,
                        res.getString("cm_code"),
                        res.getString("cm_name"),
                        res.getString("cm_terms"),
                        res.getString("cm_bank"),
                        res.getString("cm_curr"),
                        res.getString("cm_ar_acct"),
                        res.getString("cm_ar_cc"),
                        res.getString("cm_onhold")
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
    
    
    public static DefaultTableModel getVendBrowse( String from, String to) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), 
                  getGlobalColumnTag("code"), 
                  getGlobalColumnTag("name"), 
                  getGlobalColumnTag("addr1"), 
                  getGlobalColumnTag("city"), 
                  getGlobalColumnTag("state"), 
                  getGlobalColumnTag("zip"), 
                  getGlobalColumnTag("country")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_country  " +
                        " FROM  vd_mstr " +
                        " where cast(vd_addr as decimal) >= " + "'" + from + "'" + 
                        " and cast(vd_addr as decimal) <= " + "'" + to + "'" +
                        " order by vd_addr ;");
              
               
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
   
    public static DefaultTableModel getOrderBrowse( String from, String to) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), 
                  getGlobalColumnTag("order"), 
                  getGlobalColumnTag("customer"), 
                  getGlobalColumnTag("shipcode"), 
                  getGlobalColumnTag("po"), 
                  getGlobalColumnTag("orderdate"), 
                  getGlobalColumnTag("duedate"), 
                  getGlobalColumnTag("status")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                 res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status  " +
                        " FROM  so_mstr " +
                        " where cast(so_nbr as decimal) >= " + "'" + from + "'" + 
                        " and cast(so_nbr as decimal) <= " + "'" + to + "'" +
                        " order by so_nbr desc ;");
              
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
    
    public static DefaultTableModel getPOBrowse( String from, String to) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), 
                  getGlobalColumnTag("purchaseorder"), 
                  getGlobalColumnTag("vendor"), 
                  getGlobalColumnTag("site"), 
                  getGlobalColumnTag("remarks"),
                  getGlobalColumnTag("orderdate"), 
                  getGlobalColumnTag("duedate"), 
                  getGlobalColumnTag("status")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
               
                    res = st.executeQuery(" select po_nbr, po_vend, po_site, po_rmks, po_ord_date, po_due_date, po_status " +
                        " FROM  po_mstr " +
                        " where cast(po_nbr as decimal) >= " + "'" + from + "'" + 
                        " and cast(po_nbr as decimal) <= " + "'" + to + "'" +
                        " order by po_nbr desc ;");
               
                
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
    
    public static DefaultTableModel getARBrowse(String from, String to) {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("id"), 
                   getGlobalColumnTag("customer"), 
                   getGlobalColumnTag("name"), 
                   getGlobalColumnTag("type"), 
                   getGlobalColumnTag("reference"), 
                   getGlobalColumnTag("number"), 
                   getGlobalColumnTag("effectivedate"), 
                   getGlobalColumnTag("invdate"), 
                   getGlobalColumnTag("duedate"), 
                   getGlobalColumnTag("amount"), 
                   getGlobalColumnTag("baseamount"), 
                   getGlobalColumnTag("open"), 
                   getGlobalColumnTag("status"), 
                   getGlobalColumnTag("currency"), 
                   getGlobalColumnTag("account")}); 
             
       try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                       res = st.executeQuery("select ar_id, ar_cust, cm_name, ar_type, " +
                               " ar_ref, ar_nbr, ar_effdate, ar_invdate, ar_duedate, ar_amt, ar_base_amt, ar_open_amt, " +
                               " ar_status, ar_curr, ar_acct " +
                               " from ar_mstr inner join cm_mstr on cm_code = ar_cust " +
                               " where ar_id >= " + "'" + from + "'" +
                               " and ar_id <= " + "'" + to + "'" + ";");
                               
                    while (res.next()) {
                        mymodel.addRow(new Object[] {
                            res.getString("ar_id"),
                            res.getString("ar_cust"),
                            res.getString("cm_name"),
                            res.getString("ar_type"),
                            res.getString("ar_ref"),
                            res.getString("ar_nbr"),
                            res.getString("ar_effdate"),
                            res.getString("ar_invdate"),
                            res.getString("ar_duedate"),
                            res.getDouble("ar_amt"),
                            res.getString("ar_base_amt"),
                            res.getString("ar_open_amt"),
                            res.getString("ar_status"),
                            res.getString("ar_curr"),
                            res.getString("ar_acct")
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
    
    public static DefaultTableModel getAPBrowse(String from, String to) {
              javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("id"), 
                getGlobalColumnTag("vendor"), 
                getGlobalColumnTag("name"),
                getGlobalColumnTag("type"),
                getGlobalColumnTag("reference"), 
                getGlobalColumnTag("effectivedate"), 
                getGlobalColumnTag("duedate"), 
                getGlobalColumnTag("amount"), 
                getGlobalColumnTag("baseamount"), 
                getGlobalColumnTag("status"), 
                getGlobalColumnTag("currency"), 
                getGlobalColumnTag("account")}); 
             
       try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                       res = st.executeQuery("select ap_id, ap_vend, vd_name, ap_type, " +
                               " ap_ref, ap_effdate, ap_duedate, ap_amt, ap_base_amt,  " +
                               " ap_status, ap_curr, ap_acct " +
                               " from ap_mstr inner join vd_mstr on vd_addr = ap_vend " +
                               " where ap_id >= " + "'" + from + "'" +
                               " and ap_id <= " + "'" + to + "'" + ";");
                               
                    while (res.next()) {
                        mymodel.addRow(new Object[] {
                            res.getString("ap_id"),
                            res.getString("ap_vend"),
                            res.getString("vd_name"),
                            res.getString("ap_type"),
                            res.getString("ap_ref"),
                            res.getString("ap_effdate"),
                            res.getString("ap_duedate"),
                            res.getDouble("ap_amt"),
                            res.getString("ap_base_amt"),
                            res.getString("ap_status"),
                            res.getString("ap_curr"),
                            res.getString("ap_acct")
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
    
    public static DefaultTableModel getEmpBrowse(String from, String to) {
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), 
                            getGlobalColumnTag("empid"), 
                            getGlobalColumnTag("lastname"), 
                            getGlobalColumnTag("firstname"), 
                            getGlobalColumnTag("department"), 
                            getGlobalColumnTag("status"), 
                            getGlobalColumnTag("shift"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("startdate"), 
                            getGlobalColumnTag("termdate")})
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                res = st.executeQuery("SELECT * FROM emp_mstr " +
                        " where emp_nbr >= " + "'" + from + "'" +
                        " and emp_nbr <= " + "'" + to + "'" + 
                        "order by emp_nbr;");

                while (res.next()) {
                   

                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, 
                                res.getInt("emp_nbr"),
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
    
    public static DefaultTableModel getShipperBrowse(String from, String to) {
           DecimalFormat df = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.US));
               // df.setMinimumFractionDigits(2);
               // df.setMaximumFractionDigits(2);
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("shipper"), 
                  getGlobalColumnTag("code"), 
                  getGlobalColumnTag("name"), 
                  getGlobalColumnTag("shipdate"), 
                  getGlobalColumnTag("type"), 
                  getGlobalColumnTag("site"), 
                  getGlobalColumnTag("po"), 
                  getGlobalColumnTag("order"), 
                  getGlobalColumnTag("currency"), 
                  getGlobalColumnTag("amount"), 
                  getGlobalColumnTag("status")});
          
          try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
              if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
              }
              if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
              }
                res = st.executeQuery("SELECT sh_id, sh_cust, cm_name, " +
                        " sh_shipdate, sh_type, sh_site, sh_po, sh_so, sh_curr, sh_status, " +
                        " sum(shd_qty * shd_netprice) as amt FROM  ship_mstr " +
                        " inner join ship_det " +
                        " on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where cast(sh_id as decimal) >= " + "'" + from + "'" +
                        " and cast(sh_id as decimal) <= " + "'" + to + "'" +
                        " group by sh_id, sh_cust, cm_name, sh_shipdate, sh_type, sh_site, sh_po, sh_so, sh_curr, sh_status  " +
                        " order by sh_id;");

                while (res.next()) {
                 
                    mymodel.addRow(new Object[]{
                        res.getString("sh_id"), 
                        res.getString("sh_cust"), 
                        res.getString("cm_name"),
                        res.getString("sh_shipdate"),
                        res.getString("sh_type"),
                        res.getString("sh_site"),
                        res.getString("sh_po"),
                        res.getString("sh_so"),
                        res.getString("sh_curr"),
                        df.format(res.getDouble("amt")),
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
         
      
   
}
