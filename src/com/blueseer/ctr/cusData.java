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

import com.blueseer.ord.*;
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
public class cusData {
    
     public static String[] addCustomerMstr(CustMstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from cm_mstr where cm_code = ?";
        String sqlInsert = "insert into cm_mstr (cm_code, cm_name, cm_line1, cm_line2, " 
                        + "cm_line3, cm_city, cm_state, cm_zip, "
                        + "cm_country, cm_dateadd, cm_datemod, cm_usermod, "
                        + "cm_group, cm_market, cm_creditlimit, cm_onhold, "
                        + "cm_carrier, cm_terms, cm_freight_type, cm_price_code, "
                        + "cm_disc_code, cm_tax_code, cm_salesperson, "
                        + "cm_ar_acct, cm_ar_cc, cm_bank, cm_curr, cm_remarks, " 
                        + "cm_label, cm_ps_jasper, cm_iv_jasper, cm_phone, cm_email ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cm_code);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cm_code);
            psi.setString(2, x.cm_name);
            psi.setString(3, x.cm_line1);
            psi.setString(4, x.cm_line2);
            psi.setString(5, x.cm_line3);
            psi.setString(6, x.cm_city);
            psi.setString(7, x.cm_state);
            psi.setString(8, x.cm_zip);
            psi.setString(9, x.cm_country);
            psi.setString(10, x.cm_dateadd);
            psi.setString(11, x.cm_datemod);
            psi.setString(12, x.cm_usermod);
            psi.setString(13, x.cm_group);
            psi.setString(14, x.cm_market);
            psi.setString(15, x.cm_creditlimit);
            psi.setString(16, x.cm_onhold);
            psi.setString(17, x.cm_carrier);
            psi.setString(18, x.cm_terms);
            psi.setString(19, x.cm_freight_type);
            psi.setString(20, x.cm_price_code);
            psi.setString(21,x.cm_disc_code);
            psi.setString(22,x.cm_tax_code);
            psi.setString(23,x.cm_salesperson);
            psi.setString(24,x.cm_ar_acct);
            psi.setString(25,x.cm_ar_cc);
            psi.setString(26,x.cm_bank);
            psi.setString(27,x.cm_curr);
            psi.setString(28,x.cm_remarks);
            psi.setString(29,x.cm_label);
            psi.setString(30,x.cm_ps_jasper);
            psi.setString(31,x.cm_iv_jasper);
            psi.setString(32,x.cm_phone);
            psi.setString(33,x.cm_email);
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
        
    public static String[] updateOrderMstr(CustMstr x) {
        String[] m = new String[2];
        String sql = "update cm_mstr set " 
                + " cm_name = ?, cm_line1 = ?, cm_line2 = ?, "
                + "cm_line3 = ?, cm_city = ?, cm_state = ?, cm_zip = ?, "
                + "cm_country = ?, cm_dateadd = ?, cm_datemod = ?, cm_usermod = ?, "
                + "cm_group = ?, cm_market = ?, cm_creditlimit = ?, cm_onhold = ?, "
                + "cm_carrier = ?, cm_terms = ?, cm_freight_type = ?, cm_price_code = ?, "
                + "cm_disc_code = ?, cm_tax_code = ?, cm_salesperson = ?, "
                + "cm_ar_acct = ?, cm_ar_cc = ?, cm_bank = ?, cm_curr = ?, cm_remarks = ?, " 
                + "cm_label = ?, cm_ps_jasper = ?, cm_iv_jasper = ?, cm_phone = ?, cm_email = ? "
                + " where cm_code = ? ; ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement psu = con.prepareStatement(sql)) {
        psu.setString(33, x.cm_code);
            psu.setString(1, x.cm_name);
            psu.setString(2, x.cm_line1);
            psu.setString(3, x.cm_line2);
            psu.setString(4, x.cm_line3);
            psu.setString(5, x.cm_city);
            psu.setString(6, x.cm_state);
            psu.setString(7, x.cm_zip);
            psu.setString(8, x.cm_country);
            psu.setString(9, x.cm_dateadd);
            psu.setString(10, x.cm_datemod);
            psu.setString(11, x.cm_usermod);
            psu.setString(12, x.cm_group);
            psu.setString(13, x.cm_market);
            psu.setString(14, x.cm_creditlimit);
            psu.setString(15, x.cm_onhold);
            psu.setString(16, x.cm_carrier);
            psu.setString(17, x.cm_terms);
            psu.setString(18, x.cm_freight_type);
            psu.setString(19, x.cm_price_code);
            psu.setString(20,x.cm_disc_code);
            psu.setString(21,x.cm_tax_code);
            psu.setString(22,x.cm_salesperson);
            psu.setString(23,x.cm_ar_acct);
            psu.setString(24,x.cm_ar_cc);
            psu.setString(25,x.cm_bank);
            psu.setString(26,x.cm_curr);
            psu.setString(27,x.cm_remarks);
            psu.setString(28,x.cm_label);
            psu.setString(29,x.cm_ps_jasper);
            psu.setString(30,x.cm_iv_jasper);
            psu.setString(31,x.cm_phone);
            psu.setString(32,x.cm_email);
        int rows = psu.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteOrderMstr(CustMstr x) { 
       String[] m = new String[2];
       
        try (Connection con = DriverManager.getConnection(url + db, user, pass);) {
        PreparedStatement ps = null;   
        
        con.setAutoCommit(false);
        String sql = "delete from cm_mstr where cm_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.cm_code);
        ps.executeUpdate();
        sql = "delete from cms_det where cms_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.cm_code);
        ps.executeUpdate();
        sql = "delete from cmc_det where cmc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.cm_code);
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
        
    public static CustMstr getOrderMstr(String[] x) {
        CustMstr r = null;
        String[] m = new String[2];
        String sql = "select * from cm_mstr where cm_code = ? ;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new CustMstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new CustMstr(m, res.getString("cm_code"), res.getString("cm_name"), res.getString("cm_line1"), res.getString("cm_line2"),
                    res.getString("cm_line3"), res.getString("cm_city"), res.getString("cm_state"), res.getString("cm_zip"),
                    res.getString("cm_country"), res.getString("cm_dateadd"), res.getString("cm_datemod"), res.getString("cm_usermod"), 
                    res.getString("cm_group"), res.getString("cm_market"), res.getString("cm_creditlimit"), res.getString("cm_onhold"), 
                    res.getString("cm_carrier"), res.getString("cm_terms"), res.getString("cm_freight_type"), res.getString("cm_price_code"), 
                    res.getString("cm_disc_code"), res.getString("cm_tax_code"), res.getString("cm_salesperson"), 
                    res.getString("cm_ar_acct"), res.getString("cm_ar_cc"), res.getString("cm_bank"), res.getString("cm_curr"), res.getString("cm_remarks"), 
                    res.getString("cm_label"), res.getString("cm_ps_jasper"), res.getString("cm_iv_jasper"), res.getString("cm_phone"), res.getString("cm_email") 
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new CustMstr(m);
        }
        return r;
    }
    
   
    
               
    public record CustMstr(String[] m, String cm_code, String cm_name, String cm_line1, String cm_line2,
    String cm_line3, String cm_city, String cm_state, String cm_zip,
    String cm_country, String cm_dateadd, String cm_datemod, String cm_usermod, 
    String cm_group, String cm_market, String cm_creditlimit, String cm_onhold, 
    String cm_carrier, String cm_terms, String cm_freight_type, String cm_price_code,
    String cm_disc_code, String cm_tax_code, String cm_salesperson, String cm_ar_acct,
    String cm_ar_cc, String cm_bank, String cm_curr, String cm_remarks,
    String cm_label, String cm_ps_jasper, String cm_iv_jasper, String cm_phone, String cm_email) {
        public CustMstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", ""
                    );
        }
    }
    
    
    
     
     
    
    
}
