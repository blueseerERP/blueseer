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
package com.blueseer.ord;

import com.blueseer.inv.*;
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
public class ordData {
    
     public static String[] addOrderMstr(OrderMstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from so_mstr where so_nbr = ?";
        String sqlInsert = "insert into so_mstr (so_nbr, so_cust, so_ship, " 
                        + "so_site, so_curr, so_shipvia, so_wh, so_po, so_due_date, so_ord_date, "
                        + "so_create_date, so_userid, so_status, so_isallocated, "
                        + "so_terms, so_ar_acct, so_ar_cc, so_rmks, so_type, so_taxcode ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.so_nbr);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.so_nbr);
            psi.setString(2, x.so_cust);
            psi.setString(3, x.so_ship);
            psi.setString(4, x.so_site);
            psi.setString(5, x.so_curr);
            psi.setString(6, x.so_shipvia);
            psi.setString(7, x.so_wh);
            psi.setString(8, x.so_po);
            psi.setString(9, x.so_due_date);
            psi.setString(10, x.so_ord_date);
            psi.setString(11, x.so_create_date);
            psi.setString(12, x.so_userid);
            psi.setString(13, x.so_status);
            psi.setString(14, x.so_isallocated);
            psi.setString(15, x.so_terms);
            psi.setString(16, x.so_ar_acct);
            psi.setString(17, x.so_ar_cc);
            psi.setString(18, x.so_rmks);
            psi.setString(19, x.so_type);
            psi.setString(20, x.so_taxcode);
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
        
    public static String[] updateOrderMstr(OrderMstr x) {
        String[] m = new String[2];
        String sql = "update so_mstr set so_cust = ?, so_ship = ?, " +
                "so_site = ?, so_curr = ?, so_shipvia = ?, so_wh = ?, so_po = ?, so_due_date = ?, so_ord_date = ?, so_create_date = ?, " +
                "so_userid = ?, so_status = ?, so_isallocated = ?, so_terms = ?, so_ar_acct = ?, so_ar_cc = ?, so_rmks = ?, so_type = ?, " +
                "so_taxcode = ? " +
                 " where so_nbr = ? ; ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement psu = con.prepareStatement(sql)) {
        psu.setString(20, x.so_nbr);
            psu.setString(1, x.so_cust);
            psu.setString(2, x.so_ship);
            psu.setString(3, x.so_site);
            psu.setString(4, x.so_curr);
            psu.setString(5, x.so_shipvia);
            psu.setString(6, x.so_wh);
            psu.setString(7, x.so_po);
            psu.setString(8, x.so_due_date);
            psu.setString(9, x.so_ord_date);
            psu.setString(10, x.so_create_date);
            psu.setString(11, x.so_userid);
            psu.setString(12, x.so_status);
            psu.setString(13, x.so_isallocated);
            psu.setString(14, x.so_terms);
            psu.setString(15, x.so_ar_acct);
            psu.setString(16, x.so_ar_cc);
            psu.setString(17, x.so_rmks);
            psu.setString(18, x.so_type);
            psu.setString(19, x.so_taxcode);
        int rows = psu.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteOrderMstr(OrderMstr x) { 
       String[] m = new String[2];
       
        try (Connection con = DriverManager.getConnection(url + db, user, pass);) {
        PreparedStatement ps = null;   
        
        con.setAutoCommit(false);
        String sql = "delete from so_mstr where so_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.so_nbr);
        ps.executeUpdate();
        sql = "delete from sod_det where sod_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.so_nbr);
        ps.executeUpdate();
        sql = "delete from sod_tax where sodt_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.so_nbr);
        ps.executeUpdate();
        sql = "delete from sos_det where sos_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.so_nbr);
        ps.executeUpdate();
        sql = "delete from so_tax where sot_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.so_nbr);
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
        
    public static OrderMstr getOrderMstr(String[] x) {
        OrderMstr r = null;
        String[] m = new String[2];
        String sql = "select * from so_mstr where so_nbr = ? ;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new OrderMstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new OrderMstr(m, res.getString("so_nbr"), res.getString("so_cust"), res.getString("so_ship"),
                    res.getString("so_site"), res.getString("so_curr"), res.getString("so_shipvia"), res.getString("so_wh"), res.getString("so_po"),
                    res.getString("so_due_date"), res.getString("so_ord_date"), res.getString("so_create_date"), res.getString("so_userid"), res.getString("so_status"),
                    res.getString("so_isallocated"), res.getString("so_terms"), res.getString("so_ar_acct"), res.getString("so_ar_cc"), 
                    res.getString("so_rmks"), res.getString("so_type"), res.getString("so_taxcode") 
                    
        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new OrderMstr(m);
        }
        return r;
    }
    
   
    
               
    public record OrderMstr(String[] m, String so_nbr, String so_cust, String so_ship, String so_site,
    String so_curr, String so_shipvia, String so_wh, String so_po, String so_due_date,
    String so_ord_date, String so_create_date, String so_userid, String so_status, String so_isallocated,
    String so_terms, String so_ar_acct, String so_ar_cc, String so_rmks, String so_type, String so_taxcode) {
        public OrderMstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", ""
                    );
        }
    }
    
    
    
     
     
    
    
}
