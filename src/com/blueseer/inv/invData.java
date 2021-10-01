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
package com.blueseer.inv;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author terryva
 */
public class invData {
    
     public static String[] addItemMstr(ItemMstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from item_mstr where it_item = ?";
        String sqlInsert = "insert into item_mstr (it_item, it_desc, it_lotsize, " 
                        + "it_sell_price, it_pur_price, it_ovh_cost, it_out_cost, it_mtl_cost, it_code, it_type, it_group, "
                        + "it_prodline, it_drawing, it_rev, it_custrev, it_wh, it_loc, it_site, it_comments, "
                        + "it_status, it_uom, it_net_wt, it_ship_wt, it_cont, it_contqty, "
                        + "it_leadtime, it_safestock, it_minordqty, it_mrp, it_sched, it_plan, it_wf, it_taxcode, it_createdate, it_expire, it_expiredays ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.it_item);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.it_item);
            psi.setString(2, x.it_desc);
            psi.setString(3, x.it_lotsize);
            psi.setString(4, x.it_sell_price);
            psi.setString(5, x.it_pur_price);
            psi.setString(6, x.it_ovh_cost);
            psi.setString(7, x.it_out_cost);
            psi.setString(8, x.it_mtl_cost);
            psi.setString(9, x.it_code);
            psi.setString(10, x.it_type);
            psi.setString(11, x.it_group);
            psi.setString(12, x.it_prodline);
            psi.setString(13, x.it_drawing);
            psi.setString(14, x.it_rev);
            psi.setString(15, x.it_custrev);
            psi.setString(16, x.it_wh);
            psi.setString(17, x.it_loc);
            psi.setString(18, x.it_site);
            psi.setString(19, x.it_comments);
            psi.setString(20, x.it_status);
            psi.setString(21, x.it_uom);
            psi.setString(22, x.it_net_wt);
            psi.setString(23, x.it_ship_wt);
            psi.setString(24, x.it_cont);
            psi.setString(25, x.it_contqty);
            psi.setString(26, x.it_leadtime);
            psi.setString(27, x.it_safestock);
            psi.setString(28, x.it_minordqty);
            psi.setInt(29, x.it_mrp);
            psi.setInt(30, x.it_sched);
            psi.setInt(31, x.it_plan);
            psi.setString(32, x.it_wf);
            psi.setString(33, x.it_taxcode);
            psi.setString(34, x.it_createdate);
            psi.setString(35, x.it_expire);
            psi.setString(36, x.it_expiredays);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists};    
            }
          } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
          }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static String[] updateItemMstr(ItemMstr x) {
        String[] m = new String[2];
        String sql = "update item_mstr set it_desc = ?, it_lotsize = ?, " +
                "it_sell_price = ?, it_pur_price = ?, it_ovh_cost = ?, it_out_cost = ?, it_mtl_cost = ?, it_code = ?, it_type = ?, it_group = ?, " +
                "it_prodline = ?, it_drawing = ?, it_rev = ?, it_custrev = ?, it_wh = ?, it_loc = ?, it_site = ?, it_comments = ?, " +
                "it_status = ?, it_uom = ?, it_net_wt = ?, it_ship_wt = ?, it_cont = ?, it_contqty = ?, " +
                "it_leadtime = ?, it_safestock = ?, it_minordqty = ?, it_mrp = ?, it_sched = ?, it_plan = ?, it_wf = ?, it_taxcode = ?, it_createdate = ?, " +
                "it_expire = ?, it_expiredays = ? " +
                " where it_item = ? ; ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement psu = con.prepareStatement(sql)) {
        psu.setString(36, x.it_item);
            psu.setString(1, x.it_desc);
            psu.setString(2, x.it_lotsize);
            psu.setString(3, x.it_sell_price);
            psu.setString(4, x.it_pur_price);
            psu.setString(5, x.it_ovh_cost);
            psu.setString(6, x.it_out_cost);
            psu.setString(7, x.it_mtl_cost);
            psu.setString(8, x.it_code);
            psu.setString(9, x.it_type);
            psu.setString(10, x.it_group);
            psu.setString(11, x.it_prodline);
            psu.setString(12, x.it_drawing);
            psu.setString(13, x.it_rev);
            psu.setString(14, x.it_custrev);
            psu.setString(15, x.it_wh);
            psu.setString(16, x.it_loc);
            psu.setString(17, x.it_site);
            psu.setString(18, x.it_comments);
            psu.setString(19, x.it_status);
            psu.setString(20, x.it_uom);
            psu.setString(21, x.it_net_wt);
            psu.setString(22, x.it_ship_wt);
            psu.setString(23, x.it_cont);
            psu.setString(24, x.it_contqty);
            psu.setString(25, x.it_leadtime);
            psu.setString(26, x.it_safestock);
            psu.setString(27, x.it_minordqty);
            psu.setInt(28, x.it_mrp);
            psu.setInt(29, x.it_sched);
            psu.setInt(30, x.it_plan);
            psu.setString(31, x.it_wf);
            psu.setString(32, x.it_taxcode);
            psu.setString(33, x.it_createdate);
            psu.setString(34, x.it_expire);
            psu.setString(35, x.it_expiredays);
        int rows = psu.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteItemMstr(ItemMstr x) { 
       String[] m = new String[2];
       
        try (Connection con = DriverManager.getConnection(url + db, user, pass);) {
        PreparedStatement ps = null;   
        
        con.setAutoCommit(false);
        String sql = "delete from item_mstr where it_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from item_cost where itc_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from item_image where iti_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from in_mstr where in_part = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from pbm_mstr where ps_parent = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from pbm_mstr where ps_child = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from plan_mstr where plan_part = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from pland_mstr where pland_part = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from mrp_mstr where mrp_part = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        ps.close();
        con.commit();
        
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        } 
        return m;
    }
        
    public static ItemMstr getItemMstr(String[] x) {
        ItemMstr r = null;
        String[] m = new String[2];
        String sql = "select * from item_mstr where it_item = ? ;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ItemMstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ItemMstr(m, res.getString("it_item"), res.getString("it_desc"), res.getString("it_lotsize"),
                    res.getString("it_sell_price").replace('.',defaultDecimalSeparator), res.getString("it_pur_price").replace('.',defaultDecimalSeparator), res.getString("it_ovh_cost").replace('.',defaultDecimalSeparator), res.getString("it_out_cost").replace('.',defaultDecimalSeparator),
                    res.getString("it_mtl_cost").replace('.',defaultDecimalSeparator), res.getString("it_code"), res.getString("it_type"), res.getString("it_group"),
                    res.getString("it_prodline"), res.getString("it_drawing"), res.getString("it_rev"), res.getString("it_custrev"), res.getString("it_wh"),
                    res.getString("it_loc"), res.getString("it_site"), res.getString("it_comments"), res.getString("it_status"), res.getString("it_uom"),
                    res.getString("it_net_wt").replace('.',defaultDecimalSeparator), res.getString("it_ship_wt").replace('.',defaultDecimalSeparator), res.getString("it_cont"), res.getString("it_contqty").replace('.',defaultDecimalSeparator), 
                    res.getString("it_leadtime").replace('.',defaultDecimalSeparator), res.getString("it_safestock").replace('.',defaultDecimalSeparator), res.getString("it_minordqty").replace('.',defaultDecimalSeparator), res.getInt("it_mrp"), 
                    res.getInt("it_sched"), res.getInt("it_plan"), res.getString("it_wf"), res.getString("it_taxcode"), res.getString("it_createdate"), res.getString("it_expire"), res.getString("it_expiredays")
        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ItemMstr(m);
        }
        return r;
    }
    
    


    public static ArrayList getItemListFromCustCode(String cust) {
        ArrayList myarray = new ArrayList();   
        try {


        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);

        try {
            Statement st = con.createStatement();
            ResultSet res = null;

            res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust.toString() + "'" + ";");
            while (res.next()) {
                myarray.add(res.getString("cup_item"));
            }

        } catch (SQLException s) {
            bsmf.MainFrame.show("SQL Code does not execute");
        }
        con.close();
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
         return myarray;
    }

    public static String getItemFromCustCItem(String cust, String custpart) {
    String mystring = "";
    try{
       Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

            res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                  " AND cup_citem = " + "'" + custpart + "'" + ";");
           while (res.next()) {
               mystring = res.getString("cup_item");

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
    return mystring;

    }

    public static Double getItemPriceFromCust(String cust, String part, String uom, String curr) {
    Double price = 0.00;
    String pricecode = "";

    try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

            res = st.executeQuery("select cm_price_code from cm_mstr where cm_code = " + "'" + cust + "'" + ";");
             while (res.next()) {
               pricecode = res.getString("cm_price_code");
            }     
              // if there is no pricecode....it defaults to billto
             if (! pricecode.isEmpty()) {
                 cust = pricecode;
             }

            res = st.executeQuery("select cpr_price from cpr_mstr where cpr_cust = " + "'" + cust + "'" + 
                                  " AND cpr_item = " + "'" + part + "'" +
                                  " AND cpr_uom = " + "'" + uom + "'" +
                                  " AND cpr_curr = " + "'" + curr + "'" +
                                  " AND cpr_type = 'LIST' "+ ";");
           while (res.next()) {
               price = res.getDouble("cpr_price");

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
    return price;

    }

    public static Double getItemPriceFromListCode(String code, String part, String uom, String curr) {
    Double myreturn = 0.00;
    String pricecode = "";

    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null; 
        res = st.executeQuery("select cpr_price from cpr_mstr where cpr_cust = " + "'" + code + "'" + 
                              " AND cpr_item = " + "'" + part + "'" +
                              " AND cpr_uom = " + "'" + uom + "'" +
                              " AND cpr_curr = " + "'" + curr + "'" +        
                              " AND cpr_type = 'LIST' "+ ";");
       while (res.next()) {
           myreturn = res.getDouble("cpr_price");
        }
    }
    catch (SQLException s){
         bsmf.MainFrame.show("SQL cannot get Cpr_Mstr");
    }
    con.close();
    }
    catch (Exception e){
    MainFrame.bslog(e);
    }
    return myreturn;

    }

    public static Double getItemDiscFromCust(String cust) {
    Double myreturn = 0.00;
    String disccode = "";
    int i = 0;
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

         res = st.executeQuery("select cm_disc_code from cm_mstr where cm_code = " + "'" + cust + "'" + ";");
         while (res.next()) {
           disccode = res.getString("cm_disc_code");
        }     
          // if there is no pricecode....it defaults to billto
         if (! disccode.isEmpty()) {
             cust = disccode;
         }

        res = st.executeQuery("select cpr_disc from cpr_mstr where cpr_cust = " + "'" + cust + "'" + 
                              " AND cpr_type = " + "'" + "DISCOUNT" + "'" + ";");
       while (res.next()) {
               if (i == 0)
               myreturn = res.getDouble("cpr_disc");
               if (i > 0)
               myreturn = myreturn + res.getDouble("cpr_disc");                   
           i++;
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

    public static String getItemFromCustCItem2(String cust, String custpart) {
       String mystring = "";
        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                      " AND cup_citem2 = " + "'" + custpart + "'" + ";");
               while (res.next()) {
                   mystring = res.getString("cup_item");

                }

           }
            catch (SQLException s){
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "SQL cannot get Cup_Mstr");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;

    }

    public static String getItemFromCustUpc(String cust, String custpart) {
       String mystring = "";
        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                      " AND cup_upc = " + "'" + custpart + "'" + ";");
               while (res.next()) {
                   mystring = res.getString("cup_item");

                }

           }
            catch (SQLException s){
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "SQL cannot get Cup_Mstr");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;

    }

    public static Double getItemPriceFromVend(String vend, String part, String uom, String curr) {
       Double myreturn = 0.00;
       String pricecode = "";

        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select vd_price_code from vd_mstr where vd_addr = " + "'" + vend + "'" + ";");
                 while (res.next()) {
                   pricecode = res.getString("vd_price_code");
                }     
                  // if there is no pricecode....it defaults to billto
                 if (! pricecode.isEmpty()) {
                     vend = pricecode;
                 }

                res = st.executeQuery("select vpr_price from vpr_mstr where vpr_vend = " + "'" + vend + "'" + 
                                      " AND vpr_item = " + "'" + part + "'" +
                                      " AND vpr_uom = " + "'" + uom + "'" +
                                      " AND vpr_curr = " + "'" + curr + "'" +        
                                      " AND vpr_type = 'LIST' "+ ";");
               while (res.next()) {
                   myreturn = res.getDouble("vpr_price");

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

    public static String[] getItemPrice(String type, String entity, String part, String uom, String curr) {

           // type is either 'c' for customer price or 'v' for vendor price      

           String[] TypeAndPrice = new String[2];   
           String Type = "none";
           Double price = 0.00;
           String pricecode = "";

            try{
                Class.forName(driver).newInstance();
                Connection con = DriverManager.getConnection(url + db, user, pass);
                try{
                    Statement st = con.createStatement();
                    ResultSet res = null;

                    // customer based pricing
                    if (type.equals("c")) {
                        res = st.executeQuery("select cm_price_code from cm_mstr where cm_code = " + "'" + entity + "'" + ";");
                         while (res.next()) {
                           pricecode = res.getString("cm_price_code");
                        }     
                          // if there is no pricecode....it defaults to billto
                         if (! pricecode.isEmpty()) {
                             entity = pricecode;
                         }

                        res = st.executeQuery("select cpr_price from cpr_mstr where cpr_cust = " + "'" + entity + "'" + 
                                              " AND cpr_item = " + "'" + part + "'" +
                                              " AND cpr_uom = " + "'" + uom + "'" +
                                              " AND cpr_curr = " + "'" + curr + "'" +
                                              " AND cpr_type = 'LIST' "+ ";");
                       while (res.next()) {
                           price = res.getDouble("cpr_price");
                           Type = "cust";

                        }
                    }

                    // vendor based pricing
                    if (type.equals("v")) {
                       res = st.executeQuery("select vd_price_code from vd_mstr where vd_addr = " + "'" + entity + "'" + ";");
                     while (res.next()) {
                       pricecode = res.getString("vd_price_code");
                    }     
                      // if there is no pricecode....it defaults to billto
                     if (! pricecode.isEmpty()) {
                         entity = pricecode;
                     }

                    res = st.executeQuery("select vpr_price from vpr_mstr where vpr_vend = " + "'" + entity + "'" + 
                                          " AND vpr_item = " + "'" + part + "'" +
                                          " AND vpr_uom = " + "'" + uom + "'" +
                                          " AND vpr_curr = " + "'" + curr + "'" +        
                                          " AND vpr_type = 'LIST' "+ ";");
                   while (res.next()) {
                       price = res.getDouble("vpr_price");
                       Type = "vend";

                    }
                    }


                   // if there is no customer specific price...then pull price from item master it_sell_price
                      if ( price <= 0.00 ) {
                         if (type.equals("c")) { 
                         res = st.executeQuery("select it_sell_price as itemprice from item_mstr where it_item = " + "'" + part + "'" + ";");
                         } else {
                         res = st.executeQuery("select it_pur_price as itemprice from item_mstr where it_item = " + "'" + part + "'" + ";");    
                         }
                         while (res.next()) {
                         price = res.getDouble("itemprice");   
                         Type = "item";
                         }
                      }

               TypeAndPrice[0] = Type;
               TypeAndPrice[1] = String.valueOf(price);

               }
                catch (SQLException s){
                     MainFrame.bslog(s);
                }
                con.close();
            }
            catch (Exception e){
                MainFrame.bslog(e);
            }
            return TypeAndPrice;

        }

    public static ArrayList getItemMaintInit() {
               ArrayList myarray = new ArrayList();
             try{
                Class.forName(driver).newInstance();
                Connection con = DriverManager.getConnection(url + db, user, pass);
                try{
                    Statement st = con.createStatement();
                    ResultSet res = null;


                    res = st.executeQuery("select pl_line from pl_mstr order by pl_line ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"prodline",res.getString("pl_line")};
                    myarray.add(arr); 
                    }

                    res = st.executeQuery("select site_site from site_mstr order by site_site ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"site",res.getString("site_site")};
                    myarray.add(arr); 
                    }

                    res = st.executeQuery("select uom_id from uom_mstr order by uom_id ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"uom",res.getString("uom_id")};
                    myarray.add(arr); 
                    }

                    res = st.executeQuery("select tax_code from tax_mstr order by tax_code ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"tax",res.getString("tax_code")};
                    myarray.add(arr); 
                    }

                    res = st.executeQuery("select loc_loc from loc_mstr order by loc_loc ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"loc",res.getString("loc_loc")};
                    myarray.add(arr); 
                    }

                    res = st.executeQuery("select wh_id from wh_mstr order by wh_id ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"wh",res.getString("wh_id")};
                    myarray.add(arr); 
                    }

                    res = st.executeQuery("select code_key from code_mstr  where code_code = 'ItemType' order by code_key ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"type",res.getString("code_key")};
                    myarray.add(arr); 
                    }

                    res = st.executeQuery("select wf_id from wf_mstr order by wf_id ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"routing",res.getString("wf_id")};
                    myarray.add(arr); 
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
            return myarray;

        }

    public static ArrayList getItemImagesFile(String item) {
               ArrayList myarray = new ArrayList();
             try{
                Class.forName(driver).newInstance();
                Connection con = DriverManager.getConnection(url + db, user, pass);
                try{
                    Statement st = con.createStatement();
                    ResultSet res = null;

                    res = st.executeQuery("select iti_file from item_image where iti_item = " + "'" + item + "'" + " order by iti_order ;" );
                   while (res.next()) {
                    myarray.add(res.getString("iti_file"));                    
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
            return myarray;

        }

    public static String getItemCode(String mypart) {
      String myitem = "";
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select it_code from item_mstr where it_item = " + "'" + mypart.toString() + "';" );
       while (res.next()) {
        myitem = res.getString("it_code");                    
        }

    }
    catch (SQLException s){
        MainFrame.bslog(s);
         bsmf.MainFrame.show("SQL cannot get pm code from item");
    }
    con.close();
    }
    catch (Exception e){
    MainFrame.bslog(e);
    }
    return myitem;  
    }

    public static String getItemTypeByPart(String mypart) {
    String myitem = "";
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select it_type from item_mstr where it_item = " + "'" + mypart.toString() + "';" );
       while (res.next()) {
        myitem = res.getString("it_type");                    
        }

    }
    catch (SQLException s){
         bsmf.MainFrame.show("SQL cannot get type from item");
    }
    con.close();
    }
    catch (Exception e){
    MainFrame.bslog(e);
    }
    return myitem;

    }

    public static String getItemSite(String item) {
    String myreturn = "";
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select it_site from item_mstr where it_item = " + "'" + item + "'" +  ";" );
       while (res.next()) {
        myreturn = res.getString("it_site");                    
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

    public static String getItemLotSize(String item) {
    String myreturn = "";
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select it_lotsize from item_mstr where it_item = " + "'" + item + "'" +  ";" );
       while (res.next()) {
        myreturn = res.getString("it_lotsize");                    
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


    public static String getItemRouting(String item) {
    String myreturn = "";
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select it_wf from item_mstr where it_item = " + "'" + item + "'" +  ";" );
       while (res.next()) {
        myreturn = res.getString("it_wf");                    
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


    public static Double getItemQOHTotal(String item, String site) {
       Double cost = 0.00;
     try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_part = " + "'" + item + "'" 
                            + " and in_site = " + "'" + site + "'"
                            + ";");
           while (res.next()) {
            cost += res.getDouble("in_qoh");                    
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
    return cost;

    }


    public static Double getItemQOHUnallocated(String item, String site, String currentorder) {
       Double qohu = 0.00;
     try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_part = " + "'" + item + "'" 
                            + " and in_site = " + "'" + site + "'"
                            + ";");
               while (res.next()) {
                qohu += res.getDouble("in_qoh");                    
                }

            res = st.executeQuery("SELECT  sum(case when sod_all_qty = '' then 0 else (sod_all_qty - sod_shipped_qty) end) as allqty  " +
                                " FROM  sod_det inner join so_mstr on so_nbr = sod_nbr  " +
                                " where sod_part = " + "'" + item + "'" + 
                                " AND so_status <> 'closed' " + 
                                " AND so_site = " + "'" + site + "'" +   
                              //  " AND so_nbr <> " + "'" + currentorder + "'" +
                                " group by sod_part ;");

                while (res.next()) {
                qohu -= res.getInt("allqty");
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
    return qohu;

    }   


    public static Double getItemQtyByWarehouse(String item, String site, String wh) {
       Double cost = 0.00;
     try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_part = " + "'" + item + "'" 
                            + " and in_site = " + "'" + site + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + ";");
           while (res.next()) {
            cost += res.getDouble("in_qoh");                    
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
    return cost;

    }   

    public static Double getItemQtyByWarehouseAndLocation(String item, String site, String wh, String loc) {
       Double cost = 0.00;
     try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_part = " + "'" + item + "'" 
                            + " and in_site = " + "'" + site + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_loc = " + "'" + loc + "'"
                            + ";");
           while (res.next()) {
            cost += res.getDouble("in_qoh");                    
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
    return cost;

    }    

    public static Double getItemPOSPrice(String item) {
       Double price = 0.00;
     try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

            res = st.executeQuery("select it_sell_price from item_mstr where it_item = " + "'" + item + "'" + ";" );
           while (res.next()) {
            price = res.getDouble("it_sell_price");                    
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
    return price;

    }

    public static Double getItemPOSDisc(String item) {
       Double price = 0.00;
     try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

            res = st.executeQuery("select it_disc_pct from item_mstr where it_item = " + "'" + item + "'" + ";" );
           while (res.next()) {
            price = res.getDouble("it_disc_pct");                    
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
    return price;

    }

    public static Double getItemCost(String item, String set, String site) {
               Double cost = 0.00;
             try{
                Class.forName(driver).newInstance();
                Connection con = DriverManager.getConnection(url + db, user, pass);
                try{
                    Statement st = con.createStatement();
                    ResultSet res = null;

                    res = st.executeQuery("select itc_total from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + set + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                   while (res.next()) {
                    cost = res.getDouble("itc_total");                    
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
            return cost;

        }

    public static Double getItemCostUpToOp(String item, String set, String site, String op) {
               Double cost = 0.00;
             try{
                Class.forName(driver).newInstance();
                Connection con = DriverManager.getConnection(url + db, user, pass);
                try{
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    ///  if this is material type 'P'...just return standard cost for item.
                    if (getItemCode(item).toUpperCase().equals("P")) {
                      res = st.executeQuery("select itc_total from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + set + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                       while (res.next()) {
                       cost = res.getDouble("itc_total");                    
                       }  
                    } else {
                        res = st.executeQuery("select itr_total from itemr_cost where itr_item = " + "'" + item + "'" +  " AND " 
                                + " itr_set = " + "'" + set + "'" + " AND "
                                + " itr_op <= " + "'" + op + "'" + " AND "
                                + " itr_site = " + "'" + site + "'" + " order by itr_op asc ;" );
                       while (res.next()) {
                       cost += res.getDouble("itr_total");                    
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
            return cost;

        }

    public static ArrayList getItemCostElements(String item, String set, String site) {
               ArrayList<Double> mylist = new ArrayList<Double>();
             try{
                Class.forName(driver).newInstance();
                Connection con = DriverManager.getConnection(url + db, user, pass);
                try{
                    Statement st = con.createStatement();
                    ResultSet res = null;

                    res = st.executeQuery("select * from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + set + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                   while (res.next()) {
                    mylist.add(res.getDouble("itc_mtl_low"));
                    mylist.add(res.getDouble("itc_lbr_low"));
                    mylist.add(res.getDouble("itc_bdn_low"));
                    mylist.add(res.getDouble("itc_ovh_low"));
                    mylist.add(res.getDouble("itc_out_low"));
                    mylist.add(res.getDouble("itc_mtl_top"));
                    mylist.add(res.getDouble("itc_lbr_top"));
                    mylist.add(res.getDouble("itc_bdn_top"));
                    mylist.add(res.getDouble("itc_ovh_top"));
                    mylist.add(res.getDouble("itc_out_top"));
                    mylist.add(res.getDouble("itc_total"));

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
            return mylist;

        }

    public static ArrayList<String[]> getItemCostByRange(String item, String from, String set, String site) {
               ArrayList<String[]> mylist = new ArrayList<String[]>();
               String[] myarray = new String[]{"","","","","","",""};
             try{
                Class.forName(driver).newInstance();
                Connection con = DriverManager.getConnection(url + db, user, pass);
                try{
                    Statement st = con.createStatement();
                    ResultSet res = null;

                    res = st.executeQuery("select * from item_cost inner join item_mstr on it_item = itc_item where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + set + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                   while (res.next()) {
                    myarray[0] = res.getString("it_item");
                    myarray[1] = String.valueOf(res.getDouble("itc_mtl_low") + res.getDouble("itc_mtl_top") );
                    myarray[2] = String.valueOf(res.getDouble("itc_lbr_low") + res.getDouble("itc_lbr_top") );
                    myarray[3] = String.valueOf(res.getDouble("itc_bdn_low") + res.getDouble("itc_bdn_top") );
                    myarray[4] = String.valueOf(res.getDouble("itc_ovh_low") + res.getDouble("itc_ovh_top") );
                    myarray[5] = String.valueOf(res.getDouble("itc_out_low") + res.getDouble("itc_out_top") );
                    myarray[6] = String.valueOf(res.getDouble("itc_total"));

                    mylist.add(myarray);

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
            return mylist;

        }


    public static Double getItemOperationalCost(String item, String set, String site) {
    double cost = 0.00; 
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select * from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                + " itc_set = " + "'" + set + "'" + " AND "
                + " itc_site = " + "'" + site + "'" + ";" );
       while (res.next()) {
           cost += res.getDouble("itc_lbr_top") + 
                   res.getDouble("itc_bdn_top") +
                   res.getDouble("itc_ovh_top") +
                   res.getDouble("itc_out_top");
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
    return cost;

    }

    public static Double getItemMtlCostStd(String item, String set, String site) {
    Double cost = 0.00;
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select itc_mtl_top, itc_mtl_low from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                + " itc_set = " + "'" + set + "'" + " AND "
                + " itc_site = " + "'" + site + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("itc_mtl_top") + res.getDouble("itc_mtl_low");                    
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
    return cost;

    }

    public static Double getItemMtlCost(String item) {
    Double cost = 0.00;
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select it_mtl_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("it_mtl_cost");                    
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
    return cost;

    }

    public static Double getItemOvhCostStd(String item, String set, String site) {
    Double cost = 0.00;
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select itc_ovh_top, itc_ovh_low from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                + " itc_set = " + "'" + set + "'" + " AND "
                + " itc_site = " + "'" + site + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("itc_ovh_top") + res.getDouble("itc_ovh_low");                    
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
    return cost;

    }

    public static Double getItemOvhCost(String item) {
    Double cost = 0.00;
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select it_ovh_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("it_ovh_cost");                    
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
    return cost;

    }

    public static Double getItemOutCostStd(String item, String set, String site) {
    Double cost = 0.00;
    try{
    Class.forName(driver).newInstance();
    Connection con = DriverManager.getConnection(url + db, user, pass);
    try{
        Statement st = con.createStatement();
        ResultSet res = null;

        res = st.executeQuery("select itc_out_top, itc_out_low from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                + " itc_set = " + "'" + set + "'" + " AND "
                + " itc_site = " + "'" + site + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("itc_out_top") + res.getDouble("itc_out_low");                    
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
    return cost;

    }

    public static Double getItemOutCost(String item) {
           Double cost = 0.00;
         try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_out_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
               while (res.next()) {
                cost = res.getDouble("it_out_cost");                    
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
        return cost;

    }

    public static String getItemStatusByPart(String mypart) {
           String myitem = "";
         try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_status from item_mstr where it_item = " + "'" + mypart.toString() + "';" );
               while (res.next()) {
                myitem = res.getString("it_status");                    
                }

           }
            catch (SQLException s){
                 bsmf.MainFrame.show("SQL cannot get info from item");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;

    }

    public static String getItemDesc(String mypart) {
           String myitem = "";
         try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_desc from item_mstr where it_item = " + "'" + mypart.toString() + "';" );
               while (res.next()) {
                myitem = res.getString("it_desc");                    
                }

           }
            catch (SQLException s){
                 bsmf.MainFrame.show("SQL cannot get desc from item");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;

    }

    public static String[] getItemDetail(String mypart) {
           String[] x = new String[]{"","","","","","","","","",""};
         try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select it_item, it_desc, it_uom, it_prodline, it_code, it_rev, it_status, it_site, it_loc, it_wh from item_mstr where it_item = " + "'" + mypart.toString() + "';" );
               while (res.next()) {
                x[0] = res.getString("it_item"); 
                x[1] = res.getString("it_desc"); 
                x[2] = res.getString("it_uom"); 
                x[3] = res.getString("it_prodline"); 
                x[4] = res.getString("it_code"); 
                x[5] = res.getString("it_rev"); 
                x[6] = res.getString("it_status"); 
                x[7] = res.getString("it_site"); 
                x[8] = res.getString("it_loc"); 
                x[9] = res.getString("it_wh"); 
                }
          } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("SQL cannot get Item Master");
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
        } catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;

    }

    public static ArrayList getItemRoutingOPs(String myitem) {
       ArrayList myarray = new ArrayList();
        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                 res = st.executeQuery("SELECT itr_op from itemr_cost where itr_item = " + "'" + myitem.toString() + "'" + " order by itr_op;");
               while (res.next()) {
                    myarray.add(res.getString("itr_op"));
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
        return myarray;

    }

    public static ArrayList getItemWFOPs(String myitem) {
       ArrayList myarray = new ArrayList();
        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                 res = st.executeQuery("SELECT wf_op from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + myitem.toString() + "'" + " order by wf_op;");
               while (res.next()) {
                    myarray.add(res.getString("wf_op"));
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
        return myarray;

    }

    public static ArrayList getItemMasterSchedlist() {
       ArrayList myarray = new ArrayList();
        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr where it_sched = '1' order by it_item;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));
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
        return myarray;

    }    

    public static ArrayList getItemMasterMCodelist() {
       ArrayList myarray = new ArrayList();
        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr where it_code = 'M' order by it_item;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));
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
        return myarray;

    }


    public static ArrayList getItemMasterACodelist() {
       ArrayList myarray = new ArrayList();
        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr where it_code = 'A' order by it_item;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));
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
        return myarray;

    }

    public static ArrayList getItemMasterACodeForCashTran() {
       ArrayList myarray = new ArrayList();
        try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr  " +
                        " where it_code = 'A' and it_status = 'ACTIVE' order by it_item;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));
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
        return myarray;

    }


    public static ArrayList getItemMasterRawlist() {
       ArrayList myarray = new ArrayList();
        try{
           Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr where it_code = 'P' order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

                }

           }
            catch (SQLException s){
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "SQL cannot get Item Master FG");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getItemsByType(String type) {
       ArrayList myarray = new ArrayList();
        try{
           Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr where it_type = " + "'" + type + "'" +
                        " order by it_item ;");

               while (res.next()) {
                    myarray.add(res.getString("it_item"));

                }

           }
            catch (SQLException s){
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "SQL cannot get Item Master FG");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;

    }


    public static ArrayList getItemMasterAlllist() {
       ArrayList myarray = new ArrayList();
        try{
           Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

                }

           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "SQL cannot get Item Master FG");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getItemMasterListBySite(String site) {
       ArrayList myarray = new ArrayList();
        try{
           Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr " +
                        " where it_site = " + "'" + site + "'" + " order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

                }

           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "SQL cannot get Item Master FG");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;

    }


    public static ArrayList getItemRange(String site, String fromitem, String toitem) {
       ArrayList myarray = new ArrayList();
        try{
           Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr where it_item >= " + "'" + fromitem + "'" +
                        " and it_item <= " + "'" + toitem + "'" + 
                        " and it_site = " + "'" + site + "'" + " order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

                }

           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "SQL cannot get Item Master FG");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;

    }


    public static ArrayList getItemRangeByClass(String site, String fromitem, String toitem, String classcode) {
       ArrayList myarray = new ArrayList();
        try{
           Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select it_item from item_mstr where it_item >= " + "'" + fromitem + "'" +
                        " and it_item <= " + "'" + toitem + "'" + 
                        " and it_code = " + "'" + classcode + "'" +        
                        " and it_site = " + "'" + site + "'" + " order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

                }

           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "SQL cannot get Item Master FG");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static Double getItemLbrCost(String part, String op, String site, String set) {
             Double labor = 0.00;
              DecimalFormat df = new DecimalFormat("#.00000", new DecimalFormatSymbols(Locale.US)); 
              try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select itr_lbr_top, itr_lbr_low from itemr_cost inner join item_mstr on it_item = itr_item " + 
                        " where itr_item = " + "'" + part + "'" +
                        " AND itr_op = " + "'" + op + "'" + 
                        " AND itr_site = " + "'" + site + "'" + 
                        " AND itr_set = " + "'" + set + "'" +
                        " AND itr_routing = it_wf " +
                        ";");
               while (res.next()) {
                    labor += ( res.getDouble("itr_lbr_top") + res.getDouble("itr_lbr_low") );
                }
               labor = Double.valueOf(df.format(labor));
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show("SQL cannot get Labor Cost");
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
             return labor;
         }

    public static Double getItemBdnCost(String part, String op, String site, String set) {
         Double burden = 0.00;
          DecimalFormat df = new DecimalFormat("#.00000", new DecimalFormatSymbols(Locale.US)); 
          try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

            res = st.executeQuery("select itr_bdn_top, itr_bdn_low from itemr_cost inner join item_mstr on it_item = itr_item " + 
                    " where itr_item = " + "'" + part + "'" +
                    " AND itr_op = " + "'" + op + "'" + 
                    " AND itr_site = " + "'" + site + "'" + 
                    " AND itr_set = " + "'" + set + "'" +
                    " AND itr_routing = it_wf " +
                    ";");
           while (res.next()) {
                burden += ( res.getDouble("itr_bdn_top") + res.getDouble("itr_bdn_low") );
            }
           burden = Double.valueOf(df.format(burden));
       }
        catch (SQLException s){
            MainFrame.bslog(s);
             bsmf.MainFrame.show("SQL cannot get Labor Cost");
        }
        con.close();
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
         return burden;
     }

    
    
               
    public record ItemMstr(String[] m, String it_item, String it_desc, String it_lotsize,
        String it_sell_price, String it_pur_price, String it_ovh_cost, String it_out_cost,
        String it_mtl_cost, String it_code, String it_type, String it_group,
        String it_prodline, String it_drawing, String it_rev, String it_custrev, String it_wh,
        String it_loc, String it_site, String it_comments, String it_status, String it_uom, 
        String it_net_wt, String it_ship_wt, String it_cont, String it_contqty,
        String it_leadtime, String it_safestock, String it_minordqty, int it_mrp,
        int it_sched, int it_plan, String it_wf, String it_taxcode, String it_createdate,
        String it_expire, String it_expiredays) {
        public ItemMstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", 0, 0,
                    0, "", "", "", "", "");
        }
    }
    
    
    
     
     
    
    
}
