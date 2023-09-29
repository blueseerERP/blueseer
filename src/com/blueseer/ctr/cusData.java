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
package com.blueseer.ctr;


import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
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
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author terryva
 */
public class cusData {
    
    
    // add customer master customer master table only
    public static String[] addCustMstr(cm_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            int rows = _addCustMstr(x, con, ps, res, false);  // add cms_det
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    // add customer master.... multiple table transaction function
    public static String[] addCustomerTransaction(cm_mstr cm, ArrayList<String[]> list, cms_det cms) {
        String[] m = new String[2];
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            con.setAutoCommit(false);
            _addCustMstr(cm, con, ps, res, false);  // add cm_mstr
            _deleteCMCDetAll(cm.cm_code, con, ps, res);    // delete cmc_det
            
            for (String[] s : list) {  
            cmc_det z = new cmc_det(null, 
                s[0],
                cm.cm_code,
                s[1],
                s[2],
                s[3],
                s[4],
                s[5]
                );
            _addCMCDet(z, con, ps, res);  // add cmc_det
            }
            _addCMSDet(cms, con, ps, res, false);  // add cms_det
            con.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 con.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
             } catch (SQLException rb) {
                 MainFrame.bslog(rb);
             }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
     
     public static boolean addCustMstrMass(ArrayList<String> list) {
        boolean r = false;
        String[] ld = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
   
            for (String rec : list) {
                ld = rec.split(":", -1);
                cm_mstr x = new cm_mstr(null, 
                ld[0], ld[2], ld[3], ld[4],
                    ld[5], ld[6], ld[7], ld[8],
                    ld[9], BlueSeerUtils.setDateFormat(new java.util.Date()), BlueSeerUtils.setDateFormat(new java.util.Date()), 
                    bsmf.MainFrame.userid, ld[10], ld[11], ld[12], ld[13], 
                    ld[14], ld[15], ld[16], ld[17], 
                    ld[18], ld[19], ld[20], 
                    ld[21], ld[22], ld[25], ld[26], ld[23], 
                    ld[30], ld[28], ld[29], ld[31], ld[32],
                    ld[33], ld[34], ld[35], ld[1]
                );
                _addCustMstr(x, con, ps, res, true);
                cms_det y = new cms_det(null, 
                ld[0], ld[0], ld[2], ld[3], ld[4],
                    ld[5], ld[6], ld[7], ld[8],
                    ld[9] );
                _addCMSDet(y,  con, ps, res, true);
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            
        }
    return r;
    }
    
    
    private static int _addCustMstr(cm_mstr x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from cm_mstr where cm_code = ?";
        String sqlInsert = "insert into cm_mstr (cm_code, cm_name, cm_line1, cm_line2, " 
                        + "cm_line3, cm_city, cm_state, cm_zip, "
                        + "cm_country, cm_dateadd, cm_datemod, cm_usermod, "
                        + "cm_group, cm_market, cm_creditlimit, cm_onhold, "
                        + "cm_carrier, cm_terms, cm_freight_type, cm_price_code, "
                        + "cm_disc_code, cm_tax_code, cm_salesperson, "
                        + "cm_ar_acct, cm_ar_cc, cm_bank, cm_curr, cm_remarks, " 
                        + "cm_label, cm_ps_jasper, cm_iv_jasper, cm_phone, cm_email, "
                        + "cm_is855export, cm_is856export, cm_is810export, cm_site ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update cm_mstr set " 
                + " cm_name = ?, cm_line1 = ?, cm_line2 = ?, "
                + "cm_line3 = ?, cm_city = ?, cm_state = ?, cm_zip = ?, "
                + "cm_country = ?, cm_dateadd = ?, cm_datemod = ?, cm_usermod = ?, "
                + "cm_group = ?, cm_market = ?, cm_creditlimit = ?, cm_onhold = ?, "
                + "cm_carrier = ?, cm_terms = ?, cm_freight_type = ?, cm_price_code = ?, "
                + "cm_disc_code = ?, cm_tax_code = ?, cm_salesperson = ?, "
                + "cm_ar_acct = ?, cm_ar_cc = ?, cm_bank = ?, cm_curr = ?, cm_remarks = ?, " 
                + "cm_label = ?, cm_ps_jasper = ?, cm_iv_jasper = ?, cm_phone = ?, cm_email = ?, "
                + "cm_is855export = ?, cm_is856export = ?, cm_is810export = ?, cm_site = ? "
                + " where cm_code = ? ; ";  
          ps = con.prepareStatement(sqlSelect);
          ps.setString(1, x.cm_code);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
          PreparedStatement psu = con.prepareStatement(sqlUpdate); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cm_code);
            ps.setString(2, x.cm_name);
            ps.setString(3, x.cm_line1);
            ps.setString(4, x.cm_line2);
            ps.setString(5, x.cm_line3);
            ps.setString(6, x.cm_city);
            ps.setString(7, x.cm_state);
            ps.setString(8, x.cm_zip);
            ps.setString(9, x.cm_country);
            ps.setString(10, x.cm_dateadd);
            ps.setString(11, x.cm_datemod);
            ps.setString(12, x.cm_usermod);
            ps.setString(13, x.cm_group);
            ps.setString(14, x.cm_market);
            ps.setString(15, x.cm_creditlimit);
            ps.setString(16, x.cm_onhold);
            ps.setString(17, x.cm_carrier);
            ps.setString(18, x.cm_terms);
            ps.setString(19, x.cm_freight_type);
            ps.setString(20, x.cm_price_code);
            ps.setString(21,x.cm_disc_code);
            ps.setString(22,x.cm_tax_code);
            ps.setString(23,x.cm_salesperson);
            ps.setString(24,x.cm_ar_acct);
            ps.setString(25,x.cm_ar_cc);
            ps.setString(26,x.cm_bank);
            ps.setString(27,x.cm_curr);
            ps.setString(28,x.cm_remarks);
            ps.setString(29,x.cm_label);
            ps.setString(30,x.cm_ps_jasper);
            ps.setString(31,x.cm_iv_jasper);
            ps.setString(32,x.cm_phone);
            ps.setString(33,x.cm_email);
            ps.setString(34,x.cm_is855export);
            ps.setString(35,x.cm_is856export);
            ps.setString(36,x.cm_is810export);
            ps.setString(37,x.cm_site);
            rows = ps.executeUpdate();
            } else {
                if (addupdate) {
                 psu.setString(37, x.cm_code);
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
                psu.setString(33,x.cm_is855export); 
                psu.setString(34,x.cm_is856export); 
                psu.setString(35,x.cm_is810export);
                psu.setString(36,x.cm_site);
                rows = psu.executeUpdate();
                psu.close();
                }
            } 
            return rows;
    }
     
    
    
    public static String[] updateCustMstr(cm_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            int rows = _updateCustMstr(x, con, ps, res);  // add cms_det
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static int _updateCustMstr(cm_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update cm_mstr set " 
                + " cm_name = ?, cm_line1 = ?, cm_line2 = ?, "
                + "cm_line3 = ?, cm_city = ?, cm_state = ?, cm_zip = ?, "
                + "cm_country = ?, cm_dateadd = ?, cm_datemod = ?, cm_usermod = ?, "
                + "cm_group = ?, cm_market = ?, cm_creditlimit = ?, cm_onhold = ?, "
                + "cm_carrier = ?, cm_terms = ?, cm_freight_type = ?, cm_price_code = ?, "
                + "cm_disc_code = ?, cm_tax_code = ?, cm_salesperson = ?, "
                + "cm_ar_acct = ?, cm_ar_cc = ?, cm_bank = ?, cm_curr = ?, cm_remarks = ?, " 
                + "cm_label = ?, cm_ps_jasper = ?, cm_iv_jasper = ?, cm_phone = ?, cm_email = ?, "
                + "cm_is855export = ?, cm_is856export = ?, cm_is810export = ?, cm_site = ? "
                + " where cm_code = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(37, x.cm_code);
            ps.setString(1, x.cm_name);
            ps.setString(2, x.cm_line1);
            ps.setString(3, x.cm_line2);
            ps.setString(4, x.cm_line3);
            ps.setString(5, x.cm_city);
            ps.setString(6, x.cm_state);
            ps.setString(7, x.cm_zip);
            ps.setString(8, x.cm_country);
            ps.setString(9, x.cm_dateadd);
            ps.setString(10, x.cm_datemod);
            ps.setString(11, x.cm_usermod);
            ps.setString(12, x.cm_group);
            ps.setString(13, x.cm_market);
            ps.setString(14, x.cm_creditlimit);
            ps.setString(15, x.cm_onhold);
            ps.setString(16, x.cm_carrier);
            ps.setString(17, x.cm_terms);
            ps.setString(18, x.cm_freight_type);
            ps.setString(19, x.cm_price_code);
            ps.setString(20,x.cm_disc_code);
            ps.setString(21,x.cm_tax_code);
            ps.setString(22,x.cm_salesperson);
            ps.setString(23,x.cm_ar_acct);
            ps.setString(24,x.cm_ar_cc);
            ps.setString(25,x.cm_bank);
            ps.setString(26,x.cm_curr);
            ps.setString(27,x.cm_remarks);
            ps.setString(28,x.cm_label);
            ps.setString(29,x.cm_ps_jasper);
            ps.setString(30,x.cm_iv_jasper);
            ps.setString(31,x.cm_phone);
            ps.setString(32,x.cm_email);
            ps.setString(33,x.cm_is855export); 
            ps.setString(34,x.cm_is856export); 
            ps.setString(35,x.cm_is810export); 
            ps.setString(36,x.cm_site);
            rows = ps.executeUpdate();
        return rows;
    }
    
    
    public static String[] deleteCustMstr(cm_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteCustMstr(x, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _deleteCustMstr(cm_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
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
    }
        
    
    public static cm_mstr getCustMstr(String[] x) {
        cm_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from cm_mstr where cm_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cm_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cm_mstr(m, res.getString("cm_code"), res.getString("cm_name"), res.getString("cm_line1"), res.getString("cm_line2"),
                    res.getString("cm_line3"), res.getString("cm_city"), res.getString("cm_state"), res.getString("cm_zip"),
                    res.getString("cm_country"), res.getString("cm_dateadd"), res.getString("cm_datemod"), res.getString("cm_usermod"), 
                    res.getString("cm_group"), res.getString("cm_market"), res.getString("cm_creditlimit"), res.getString("cm_onhold"), 
                    res.getString("cm_carrier"), res.getString("cm_terms"), res.getString("cm_freight_type"), res.getString("cm_price_code"), 
                    res.getString("cm_disc_code"), res.getString("cm_tax_code"), res.getString("cm_salesperson"), 
                    res.getString("cm_ar_acct"), res.getString("cm_ar_cc"), res.getString("cm_bank"), res.getString("cm_curr"), res.getString("cm_remarks"), 
                    res.getString("cm_label"), res.getString("cm_ps_jasper"), res.getString("cm_iv_jasper"), res.getString("cm_phone"), res.getString("cm_email"), 
                    res.getString("cm_is855export"),res.getString("cm_is856export"),res.getString("cm_is810export"),res.getString("cm_site"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cm_mstr(m);
        }
        return r;
    }
    
     
    public static String[] addTermsMstr(cust_term x) {
        String[] m = new String[2];
        String sqlSelect = "select * from cust_term where cut_code = ?";
        String sqlInsert = "insert into cust_term (cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent, " +
                " cut_mfi, cut_mfimonth, cut_mfiday )  " +
                " values (?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cut_code);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cut_code);
            psi.setString(2, x.cut_desc);
            psi.setString(3, x.cut_days);
            psi.setString(4, x.cut_discdays);
            psi.setString(5, x.cut_discpercent);
            psi.setString(6, x.cut_mfi);
            psi.setString(7, x.cut_mfimonth);
            psi.setString(8, x.cut_mfiday);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists};    
            }
          } 
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static String[] updateTermsMstr(cust_term x) {
        String[] m = new String[2];
        String sql = "update cust_term set cut_desc = ?, cut_days = ?, cut_discdays = ?, " +
                " cut_discpercent = ?, cut_mfi = ?, cut_mfimonth = ?, cut_mfiday = ? where cut_code = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cut_desc);
        ps.setString(2, x.cut_days);
        ps.setString(3, x.cut_discdays);
        ps.setString(4, x.cut_discpercent);
        ps.setString(5, x.cut_mfi);
        ps.setString(6, x.cut_mfimonth);
        ps.setString(7, x.cut_mfiday);
        ps.setString(8, x.cut_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteTermsMstr(cust_term x) { 
       String[] m = new String[2];
        String sql = "delete from cust_term where cut_code = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cut_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static cust_term getTermsMstr(String[] x) {
        cust_term r = null;
        String[] m = new String[2];
        String sql = "select * from cust_term where cut_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cust_term(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cust_term(m, res.getString("cut_code"), 
                            res.getString("cut_desc"),
                            res.getString("cut_days"),
                            res.getString("cut_discdays"),
                            res.getString("cut_discpercent"),
                            res.getString("cut_syscode"),
                            res.getString("cut_mfi"),
                            res.getString("cut_mfimonth"),
                            res.getString("cut_mfiday")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cust_term(m);
        }
        return r;
    }
   
    
   
    
    public static String[] addUpdateCMCtrl(cm_ctrl x) {
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cm_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into cm_ctrl (cmc_autocust) "
                        + " values (?); "; 
        String sqlUpdate = "update cm_ctrl set cmc_autocust = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cmc_autocust);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.cmc_autocust);
            rows = psu.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};    
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
   
    public static cm_ctrl getCMCtrl(String[] x) {
        cm_ctrl r = null;
        String[] m = new String[2];
        String sql = "select * from cm_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cm_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cm_ctrl(m, 
                                res.getString("cmc_autocust")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cm_ctrl(m);
        }
        return r;
    }
    
    
    
    // cms_det Customer Shipto Table
    public static String[] addCMSDet(cms_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _addCMSDet(x, con, ps, res, false);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _addCMSDet(cms_det x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        if (x == null) return;
        String sqlSelect = "select * from cms_det where cms_code = ? and cms_shipto = ?";
        String sqlInsert = "insert into cms_det (cms_code, cms_shipto, cms_name, cms_line1, cms_line2, " 
                        + "cms_line3, cms_city, cms_state, cms_zip, cms_country ) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update cms_det set " 
                + " cms_name = ?, cms_line1 = ?, cms_line2 = ?, "
                + "cms_line3 = ?, cms_city = ?, cms_state = ?, cms_zip = ?, "
                + "cms_country = ? "
                + " where cms_code = ? and cms_shipto = ? ; ";
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.cms_code);
            ps.setString(2, x.cms_shipto);
            res = ps.executeQuery();
             if (! res.isBeforeFirst()) {
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, x.cms_code);
            ps.setString(2, x.cms_shipto);
            ps.setString(3, x.cms_name);
            ps.setString(4, x.cms_line1);
            ps.setString(5, x.cms_line2);
            ps.setString(6, x.cms_line3);
            ps.setString(7, x.cms_city);
            ps.setString(8, x.cms_state);
            ps.setString(9, x.cms_zip);
            ps.setString(10, x.cms_country);
            int rows = ps.executeUpdate();
            } else {
                 if (addupdate) {
                    ps = con.prepareStatement(sqlUpdate); 
                    ps.setString(9, x.cms_code);
                    ps.setString(10, x.cms_shipto);
                    ps.setString(1, x.cms_name);
                    ps.setString(2, x.cms_line1);
                    ps.setString(3, x.cms_line2);
                    ps.setString(4, x.cms_line3);
                    ps.setString(5, x.cms_city);
                    ps.setString(6, x.cms_state);
                    ps.setString(7, x.cms_zip);
                    ps.setString(8, x.cms_country);
                    ps.executeUpdate();    
                 }
             }
    }
        
    public static String[] updateCMSDet(cms_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _updateCMSDet(x, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static int _updateCMSDet(cms_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update cms_det set " 
                + " cms_name = ?, cms_line1 = ?, cms_line2 = ?, "
                + "cms_line3 = ?, cms_city = ?, cms_state = ?, cms_zip = ?, "
                + "cms_country = ? "
                + " where cms_code = ? and cms_shipto = ? ; ";
       ps = con.prepareStatement(sql);
        ps.setString(9, x.cms_code);
        ps.setString(10, x.cms_shipto);
            ps.setString(1, x.cms_name);
            ps.setString(2, x.cms_line1);
            ps.setString(3, x.cms_line2);
            ps.setString(4, x.cms_line3);
            ps.setString(5, x.cms_city);
            ps.setString(6, x.cms_state);
            ps.setString(7, x.cms_zip);
            ps.setString(8, x.cms_country);
            rows = ps.executeUpdate();
        
       
        return rows;
    }
         
    public static String[] deleteCMSDet(cms_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteCMSDet(x.cms_code, x.cms_shipto, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _deleteCMSDet(String x, String y, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from cms_det where cms_code = ? and cms_shipto = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    public static cms_det getCMSDet(String shipto, String code) {
        cms_det r = null;
        String[] m = new String[2];
        String sql = "select * from cms_det where cms_shipto = ? and cms_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, shipto);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cms_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cms_det(m, res.getString("cms_code"), res.getString("cms_shipto"), res.getString("cms_name"), res.getString("cms_line1"), res.getString("cms_line2"),
                    res.getString("cms_line3"), res.getString("cms_city"), res.getString("cms_state"), res.getString("cms_zip"),
                    res.getString("cms_country") 
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cms_det(m);
        }
        return r;
    }
    
    public static ArrayList<cms_det> getCMSDet(String code) {
        cms_det r = null;
        String[] m = new String[2];
        ArrayList<cms_det> list = new ArrayList<cms_det>();
        String sql = "select * from cms_det where cms_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cms_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cms_det(m, res.getString("cms_code"), res.getString("cms_shipto"), res.getString("cms_name"), res.getString("cms_line1"), res.getString("cms_line2"),
                    res.getString("cms_line3"), res.getString("cms_city"), res.getString("cms_state"), res.getString("cms_zip"),
                    res.getString("cms_country") 
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cms_det(m);
               list.add(r);
        }
        return list;
    }
    
    
    // cmc_det Customer Contact table
    public static String[] addCMCDet(cmc_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _addCMCDet(x, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _addCMCDet(cmc_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        if (x == null) return;
        String sqlInsert = "insert into cmc_det (cmc_code, cmc_type, cmc_name, " 
                        + "cmc_phone, cmc_fax, cmc_email ) "
                        + " values (?,?,?,?,?,?); "; 
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, x.cmc_code);
            ps.setString(2, x.cmc_type);
            ps.setString(3, x.cmc_name);
            ps.setString(4, x.cmc_phone);
            ps.setString(5, x.cmc_fax);
            ps.setString(6, x.cmc_email);
            int rows = ps.executeUpdate();
            
    }
    
    public static String[] updateCMCDet(cmc_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _updateCMCDet(x, con, ps, res);  // add cms_det 
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static int _updateCMCDet(cmc_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update cmc_det set " 
                + " cmc_type = ?, cmc_name = ?, cmc_phone = ?, "
                + "cmc_fax = ?, cmc_email = ? "
                + " where cmc_code = ? and cmc_id = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(6, x.cmc_code);
        ps.setString(7, x.cmc_id);
            ps.setString(1, x.cmc_type);
            ps.setString(2, x.cmc_name);
            ps.setString(3, x.cmc_phone);
            ps.setString(4, x.cmc_fax);
            ps.setString(5, x.cmc_email);
            rows = ps.executeUpdate();
        return rows;
    }
        
    public static String[] deleteCMCDet(cmc_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteCMCDet(x.cmc_id, x.cmc_code, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
           
    private static void _deleteCMCDet(String x, String y, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
        
        String sql = "delete from cmc_det where cmc_id = ? and cmc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    private static void _deleteCMCDetAll(String x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
        
        
        String sql = "delete from cmc_det where cmc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        
        
    }
    
    public static cmc_det getCMCDet(String id, String code) {
        cmc_det r = null;
        String[] m = new String[2];
        String sql = "select * from cmc_det where cmc_id = ? and cmc_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cmc_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cmc_det(m, res.getString("cmc_id"), res.getString("cmc_code"), 
                        res.getString("cmc_type"), res.getString("cmc_name"),
                        res.getString("cmc_phone"), res.getString("cmc_fax"), res.getString("cmc_email")                    
                    ); 
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cmc_det(m);
               
        }
        return r;
    }
    
    public static ArrayList<cmc_det> getCMCDet(String code) {
        cmc_det r = null;
        String[] m = new String[2];
        ArrayList<cmc_det> list = new ArrayList<cmc_det>();
        String sql = "select * from cmc_det where cmc_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cmc_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cmc_det(m, res.getString("cmc_id"), res.getString("cmc_code"), 
                        res.getString("cmc_type"), res.getString("cmc_name"),
                        res.getString("cmc_phone"), res.getString("cmc_fax"), res.getString("cmc_email")                    
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cmc_det(m);
               list.add(r);
        }
        return list;
    }
    
    public static String[] addCupMstr(cup_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cup_mstr where cup_citem = ? and cup_cust = ?";
        String sqlInsert = "insert into cup_mstr (cup_cust, cup_item, cup_citem, cup_citem2, " +
         "cup_upc, cup_userid, cup_ts, cup_misc, cup_sku) " 
                        + " values (?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cup_citem);
             ps.setString(2, x.cup_cust);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cup_cust);
            psi.setString(2, x.cup_item);
            psi.setString(3, x.cup_citem);
            psi.setString(4, x.cup_citem2);
            psi.setString(5, x.cup_upc);
            psi.setString(6, x.cup_userid);
            psi.setString(7, x.cup_ts);
            psi.setString(8, x.cup_misc);
            psi.setString(9, x.cup_sku);
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

    public static String[] addOrUpdateCupMstr(cup_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cup_mstr where cup_citem = ? and cup_cust = ?";
        String sqlInsert = "insert into cup_mstr (cup_cust, cup_item, cup_citem, cup_citem2, " +
         "cup_upc, cup_userid, cup_ts, cup_misc, cup_sku) " 
                        + " values (?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update cup_mstr set cup_item = ?, cup_citem2 = ?, cup_upc = ?, " +
                " cup_userid = ?, cup_ts = ?, cup_misc = ?, cup_sku = ?  " +   
                          " where cup_citem = ? and cup_cust = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cup_citem);
             ps.setString(2, x.cup_cust);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cup_cust);
            psi.setString(2, x.cup_item);
            psi.setString(3, x.cup_citem);
            psi.setString(4, x.cup_citem2);
            psi.setString(5, x.cup_upc);
            psi.setString(6, x.cup_userid);
            psi.setString(7, x.cup_ts);
            psi.setString(8, x.cup_misc);
            psi.setString(9, x.cup_sku); 
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.cup_item);
            psu.setString(2, x.cup_citem2);
            psu.setString(3, x.cup_upc);
            psu.setString(4, x.cup_userid);
            psu.setString(5, x.cup_ts);
            psu.setString(6, x.cup_misc);
            psu.setString(7, x.cup_sku);
            psu.setString(8, x.cup_citem);
            psu.setString(9, x.cup_cust);
            int rows = psu.executeUpdate();    
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};    
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

    public static String[] updateCupMstr(cup_mstr x) {
        String[] m = new String[2];
        String sql = "update cup_mstr set cup_item = ?, cup_citem2 = ?, cup_upc = ?, " +
                " cup_userid = ?, cup_ts = ?, cup_misc = ?, cup_sku = ?  " +   
                          " where cup_citem = ? and cup_cust = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cup_item);
        ps.setString(2, x.cup_citem2);
        ps.setString(3, x.cup_upc);
        ps.setString(4, x.cup_userid);
        ps.setString(5, x.cup_ts);
        ps.setString(6, x.cup_misc);
        ps.setString(7, x.cup_sku);
        ps.setString(8, x.cup_citem);
        ps.setString(9, x.cup_cust);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteCupMstr(cup_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from cup_mstr where cup_citem = ? and cup_cust = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cup_citem);
        ps.setString(2, x.cup_cust);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static cup_mstr getCupMstr(String[] x) {
        cup_mstr r = null;
        String[] m = new String[2];
        String sql = "";
         if (x.length >= 2 && ! x[1].isEmpty()) {
            sql = "select * from cup_mstr where cup_cust = ? and cup_citem = ?;";
         } else {
            sql = "select * from cup_mstr where cup_citem = ? limit 1 ;";  
         }
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        if (x.length >= 2 && ! x[1].isEmpty()) {
        ps.setString(2, x[1]);
        }
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cup_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cup_mstr(m, res.getString("cup_cust"), 
                            res.getString("cup_item"),
                            res.getString("cup_citem"),
                            res.getString("cup_citem2"),
                            res.getString("cup_upc"),
                            res.getString("cup_userid"),
                            res.getString("cup_ts"),    
                            res.getString("cup_misc"),
                            res.getString("cup_sku")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cup_mstr(m);
        }
        return r;
    }
    
    
         
    // miscellaneous functions
    
     public static ArrayList<String[]> getCustMaintInit() {
        String defaultsite = "";
        ArrayList<String[]> lines = new ArrayList<String[]>();
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses    
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "sites";
               s[1] = res.getString("site_site");
               lines.add(s);
            }
            
            res = st.executeQuery("select ov_site, ov_currency from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
               defaultsite = s[1];
            }
            
            
            res = st.executeQuery("select cur_id from cur_mstr ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "currencies";
               s[1] = res.getString("cur_id");
               lines.add(s);
            }
            
             res = st.executeQuery("select bk_id from bk_mstr order by bk_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "banks";
               s[1] = res.getString("bk_id");
               lines.add(s);
            }
            
             res = st.executeQuery("select cut_code from cust_term order by cut_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "terms";
               s[1] = res.getString("cut_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select ac_id from ac_mstr order by ac_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "accounts";
               s[1] = res.getString("ac_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select dept_id from dept_mstr order by dept_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "depts";
               s[1] = res.getString("dept_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select tax_code from tax_mstr order by tax_code  ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "taxcodes";
               s[1] = res.getString("tax_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select lblz_code from label_zebra where lblz_type = 'cont' order by lblz_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "labels";
               s[1] = res.getString("lblz_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'country' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "countries";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "states";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select car_id from car_mstr order by car_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "carriers";
               s[1] = res.getString("car_id");
               lines.add(s);
            }
            
            
            /*
             res = st.executeQuery("select car_id from car_mstr order by car_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "freight";
               s[1] = res.getString("car_id");
               lines.add(s);
            }
            */
            
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
        return lines;
    }
    
    
    public static String[] getCustInfo(String cust) {
           // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
        String[] custinfo = new String[]{"","","","","","","", ""};
        String sql = "select cm_ar_acct, cm_ar_cc, cm_curr, cm_bank, cm_terms, cm_carrier, cm_onhold, cm_site from cm_mstr where cm_code = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, cust);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               custinfo[0] = res.getString("cm_ar_acct");
               custinfo[1] = res.getString("cm_ar_cc");
               custinfo[2] = res.getString("cm_curr");
               custinfo[3] = res.getString("cm_bank");
               custinfo[4] = res.getString("cm_terms");
               custinfo[5] = res.getString("cm_carrier");
               custinfo[6] = res.getString("cm_onhold");
               custinfo[7] = res.getString("cm_site");                   
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return custinfo;
    }
    
    public static String[] getCustAddressInfo(String cust) {
           // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
        String[] custinfo = new String[]{"","","","","","","",""};
        String sql = "select cm_name, cm_line1, cm_line2, cm_line3, cm_city, cm_state, cm_zip, cm_country, cm_email from cm_mstr where cm_code = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, cust);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               custinfo[0] = res.getString("cm_name");
               custinfo[1] = res.getString("cm_line1");
               custinfo[2] = res.getString("cm_line2");
               custinfo[3] = res.getString("cm_line3");
               custinfo[4] = res.getString("cm_city");
               custinfo[5] = res.getString("cm_state");
               custinfo[6] = res.getString("cm_country");
               custinfo[7] = res.getString("cm_email");                   
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return custinfo;
    }
    
    public static String[] getShipAddressInfo(String cust, String ship) {
           // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
        String[] custinfo = new String[]{"","","","","","","","",""};
        String sql = "select cms_shipto, cms_name, cms_line1, cms_line2, cms_line3, cms_city, cms_state, cms_zip, cms_country, cms_plantcode from cms_det where cms_code = ? and cms_shipto = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, cust);
        ps.setString(2, ship);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               custinfo[0] = res.getString("cms_shipto");
               custinfo[1] = res.getString("cms_name");
               custinfo[2] = res.getString("cms_line1");
               custinfo[3] = res.getString("cms_line2");
               custinfo[4] = res.getString("cms_line3");
               custinfo[5] = res.getString("cms_city");
               custinfo[6] = res.getString("cms_state");
               custinfo[7] = res.getString("cms_zip");
               custinfo[8] = res.getString("cms_country");                   
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return custinfo;
    }
    
    public static ArrayList<String> getTermsUsage(String terms) {
        ArrayList<String> usage = new ArrayList<String>();
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
                  
            res = st.executeQuery("select cm_code from cm_mstr where cm_terms = " + "'" + terms + "'" + ";");
            while (res.next()) {
               usage.add(res.getString("cm_code"));
            }
           
            res = st.executeQuery("select vd_addr from vd_mstr where vd_terms = " + "'" + terms + "'" + ";");
            while (res.next()) {
               usage.add(res.getString("vd_addr"));
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
        return usage;
    }
    
    
    public static String getCustSalesAcct(String cust) {
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
                res = st.executeQuery("select cm_ar_acct from cm_mstr where cm_code = " + "'" + cust + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("cm_ar_acct");                    
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
                
    public static String getCustSalesCC(String cust) {
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
                res = st.executeQuery("select cm_ar_cc from cm_mstr where cm_code = " + "'" + cust + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("cm_ar_cc");                    
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
         
    public static String getCustCurrency(String cust) {
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
                res = st.executeQuery("select cm_curr from cm_mstr where cm_code = " + "'" + cust + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("cm_curr");                    
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
         
    public static String getCustTerms(String cust) {
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
                res = st.executeQuery("select cm_terms from cm_mstr where cm_code = " + "'" + cust + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("cm_terms");                    
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
    
    public static String getCustEmail(String cust) {
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
            
                res = st.executeQuery("select cm_email from cm_mstr where cm_code = " + "'" + cust + "'" + ";");
               while (res.next()) {
                    x = res.getString("cm_email");
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
        return x;
    }
    
    public static String getCustEmailByInvoice(String invoice) {
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
            
                res = st.executeQuery("select cm_email from cm_mstr " +
                        " inner join ship_mstr on sh_cust = cm_code " +
                        " where sh_id = " + "'" + invoice + "'" + ";");
               while (res.next()) {
                    x = res.getString("cm_email");
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
        return x;
    }
        
    public static ArrayList getcustmstrlist() {
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
                

                res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
               while (res.next()) {
                    myarray.add(res.getString("cm_code"));
                }
               
           }
            catch (SQLException s){
                 bsmf.MainFrame.show("SQL cannot get Cust list");
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
        return myarray;
        
    }
    
    public static ArrayList getcustmstrlistBetween(String from, String to) {
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

                res = st.executeQuery("select cm_code from cm_mstr "
                        + " where cm_code >= " + "'" + from + "'"
                        + " and cm_code <= " + "'" + to + "'"
                        + " order by cm_code ;");
                while (res.next()) {
                    myarray.add(res.getString("cm_code"));
                }

            } catch (SQLException s) {
                bsmf.MainFrame.show("SQL cannot get Cust list");
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

    public static ArrayList getCustShipToListAll() {
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

                res = st.executeQuery("select cms_shipto from cms_det order by cms_shipto ;");
                while (res.next()) {
                    myarray.add(res.getString("cms_shipto"));
                }

            } catch (SQLException s) {
                bsmf.MainFrame.show("SQL cannot get Cust list");
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

    public static ArrayList getcustshipmstrlist(String cust) {
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

                res = st.executeQuery("select cms_shipto from cms_det where cms_code = " + "'" + cust + "'" + " order by cms_shipto;");
                while (res.next()) {
                    myarray.add(res.getString("cms_shipto"));

                }

            } catch (SQLException s) {
                bsmf.MainFrame.show("SQL cannot get cms_det list");
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

    public static String getcustBillTo(String shipto) {
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

                res = st.executeQuery("select cms_code from cms_det where cms_shipto = " + "'" + shipto + "'" + " order by cms_shipto;");
                while (res.next()) {
                    mystring = res.getString("cms_code");
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
        return mystring;

    }
     
    public static ArrayList getcusttermslist() {
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

                res = st.executeQuery("select cut_code from cust_term order by cut_code ;");
                while (res.next()) {
                    myarray.add(res.getString("cut_code"));

                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("SQL cannot get Terms Master");
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

    public static String getCustAltItem(String cust, String part) {
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
            try {
            res = st.executeQuery("select cup_citem2 from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                  " AND cup_item = " + "'" + part + "'" + ";");
           while (res.next()) {
               mystring = res.getString("cup_citem2");

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
    return mystring;

}

    public static String getCustSku(String cust, String part) {
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
            try {
            res = st.executeQuery("select cup_sku from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                  " AND cup_item = " + "'" + part + "'" + ";");
           while (res.next()) {
               mystring = res.getString("cup_sku");

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
    return mystring;

    }

    public static String getCustPartFromPart(String cust, String part) {
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
            try {
            res = st.executeQuery("select cup_citem from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                  " AND cup_item = " + "'" + part + "'" + ";");
           while (res.next()) {
               mystring = res.getString("cup_citem");

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
    return mystring;

    }

    public static String getCustFromOrder(String order) {
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
                res = st.executeQuery("select so_cust from so_mstr where so_nbr = " + "'" + order + "';" );
               while (res.next()) {
                myitem = res.getString("so_cust");                    
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
              
    public static String getCustName(String cust) {
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
        try  {
        res = st.executeQuery("select cm_name from cm_mstr where cm_code = " + "'" + cust + "';" );
       while (res.next()) {
        myitem = res.getString("cm_name");                    
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

    public static String getShipName(String cust, String ship) {
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
        try  {
        res = st.executeQuery("select cms_name from cms_det where cms_code = " + "'" + cust + "'" +
                     " AND cms_shipto = " + "'" + ship + "'" + ";");
       while (res.next()) {
        myitem = res.getString("cms_name");                    
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

    
    public static String getCustLabel(String cust) {
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
            res = st.executeQuery("select cm_label from cm_mstr where cm_code = " + "'" + cust + "';" );
           while (res.next()) {
            myitem = res.getString("cm_label");                    
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

    public static String getCustLogo(String cust) {
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
            res = st.executeQuery("select cm_logo from cm_mstr where cm_code = " + "'" + cust + "';" );
           while (res.next()) {
            myitem = res.getString("cm_logo");                    
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
        } catch (Exception e){
        MainFrame.bslog(e);
        }
        return myitem;

}

    public static String getCustInvoiceJasper(String cust) {
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
                res = st.executeQuery("select cm_iv_jasper from cm_mstr where cm_code = " + "'" + cust + "';" );
               while (res.next()) {
                myitem = res.getString("cm_iv_jasper");                    
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
          
    public static String getCustShipperJasper(String cust) {
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
                res = st.executeQuery("select cm_ps_jasper from cm_mstr where cm_code = " + "'" + cust + "';" );
               while (res.next()) {
                myitem = res.getString("cm_ps_jasper");                    
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
        return myitem;
        
    } 
             
      
    
    public record cm_mstr(String[] m, String cm_code, String cm_name, String cm_line1, String cm_line2,
    String cm_line3, String cm_city, String cm_state, String cm_zip,
    String cm_country, String cm_dateadd, String cm_datemod, String cm_usermod, 
    String cm_group, String cm_market, String cm_creditlimit, String cm_onhold, 
    String cm_carrier, String cm_terms, String cm_freight_type, String cm_price_code,
    String cm_disc_code, String cm_tax_code, String cm_salesperson, String cm_ar_acct,
    String cm_ar_cc, String cm_bank, String cm_curr, String cm_remarks,
    String cm_label, String cm_ps_jasper, String cm_iv_jasper, String cm_phone, String cm_email,
    String cm_is855export, String cm_is856export, String cm_is810export, String cm_site) {
        public cm_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", ""
                    );
        }
    }
    
    public record cms_det(String[] m, String cms_code, String cms_shipto, 
        String cms_name, String cms_line1, String cms_line2,
        String cms_line3, String cms_city, String cms_state, 
        String cms_zip, String cms_country) {
        public cms_det(String[] m) {
            this(m,"","","","","","","","","","");
        }
    }
    
    public record cmc_det(String[] m, String cmc_id, String cmc_code, String cmc_type, String cmc_name, 
    String cmc_phone, String cmc_fax, String cmc_email) {
        public cmc_det(String[] m) {
            this(m,"","","","","","","");
        }
    }
    
    public record cup_mstr(String[] m, String cup_cust, String cup_item, String cup_citem, String cup_citem2, 
    String cup_upc, String cup_userid, String cup_ts, String cup_misc, String cup_sku) {
        public cup_mstr(String[] m) {
            this(m,"","","","","","","","","");
        }
    }
    
    
    public record cust_term(String[] m, String cut_code, String cut_desc, String cut_days, 
        String cut_discdays, String cut_discpercent, String cut_syscode, String cut_mfi,
        String cut_mfimonth, String cut_mfiday) {
        public cust_term(String[] m) {
            this(m,"","","","","","","","","");
        }
    } 
  
     
    public record cm_ctrl (String[] m, String cmc_autocust) {
        public cm_ctrl(String[] m) {
            this(m,"");
        }
    } 
    
     
}
