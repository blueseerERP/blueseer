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
            con = DriverManager.getConnection(url + db, user, pass);
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
            con = DriverManager.getConnection(url + db, user, pass);
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
            con = DriverManager.getConnection(url + db, user, pass);
   
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
                    ld[30], ld[28], ld[29], ld[31], ld[32]
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
                        + "cm_label, cm_ps_jasper, cm_iv_jasper, cm_phone, cm_email ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update cm_mstr set " 
                + " cm_name = ?, cm_line1 = ?, cm_line2 = ?, "
                + "cm_line3 = ?, cm_city = ?, cm_state = ?, cm_zip = ?, "
                + "cm_country = ?, cm_dateadd = ?, cm_datemod = ?, cm_usermod = ?, "
                + "cm_group = ?, cm_market = ?, cm_creditlimit = ?, cm_onhold = ?, "
                + "cm_carrier = ?, cm_terms = ?, cm_freight_type = ?, cm_price_code = ?, "
                + "cm_disc_code = ?, cm_tax_code = ?, cm_salesperson = ?, "
                + "cm_ar_acct = ?, cm_ar_cc = ?, cm_bank = ?, cm_curr = ?, cm_remarks = ?, " 
                + "cm_label = ?, cm_ps_jasper = ?, cm_iv_jasper = ?, cm_phone = ?, cm_email = ? "
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
            rows = ps.executeUpdate();
            } else {
                if (addupdate) {
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
            con = DriverManager.getConnection(url + db, user, pass);
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
                + "cm_label = ?, cm_ps_jasper = ?, cm_iv_jasper = ?, cm_phone = ?, cm_email = ? "
                + " where cm_code = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(33, x.cm_code);
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
            con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
                    res.getString("cm_label"), res.getString("cm_ps_jasper"), res.getString("cm_iv_jasper"), res.getString("cm_phone"), res.getString("cm_email") 
                    );
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
        String sqlInsert = "insert into cust_term (cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent)  " +
                " values (?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
                " cut_discpercent = ? where cut_code = ? ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cut_desc);
        ps.setString(2, x.cut_days);
        ps.setString(3, x.cut_discdays);
        ps.setString(4, x.cut_discpercent);
        ps.setString(5, x.cut_code);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
                            res.getString("cut_discpercent")
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
            con = DriverManager.getConnection(url + db, user, pass);
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
            ps.setString(1, x.cms_shipto);
            res = ps.executeQuery();
            ps = con.prepareStatement(sqlInsert);
             if (! res.isBeforeFirst()) {
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
            con = DriverManager.getConnection(url + db, user, pass);
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
            con = DriverManager.getConnection(url + db, user, pass);
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
        ps.setString(1, y);
        ps.executeUpdate();
    }
    
    public static cms_det getCMSDet(String shipto, String code) {
        cms_det r = null;
        String[] m = new String[2];
        String sql = "select * from cms_det where cms_shipto = ? and cms_code = ? ;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
            con = DriverManager.getConnection(url + db, user, pass);
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
            con = DriverManager.getConnection(url + db, user, pass);
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
            con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
    
         
    // miscellaneous functions
    public static String[] getCustInfo(String cust) {
           // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
        String[] custinfo = new String[]{"","","","","","","", ""};
        String sql = "select cm_ar_acct, cm_ar_cc, cm_curr, cm_bank, cm_terms, cm_carrier, cm_onhold, cm_site from cm_mstr where cm_code = ?;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        
    public static String getCustSalesAcct(String cust) {
           String myitem = "";
         try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
        
        Connection con = DriverManager.getConnection(url + db, user, pass);
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
        
        Connection con = DriverManager.getConnection(url + db, user, pass);
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
        
        Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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

        Connection con = DriverManager.getConnection(url + db, user, pass);
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

        Connection con = DriverManager.getConnection(url + db, user, pass);
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

            Connection con = DriverManager.getConnection(url + db, user, pass);
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

            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
    String cm_label, String cm_ps_jasper, String cm_iv_jasper, String cm_phone, String cm_email) {
        public cm_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", ""
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
    
    public record cust_term(String[] m, String cut_code, String cut_desc, String cut_days, 
        String cut_discdays, String cut_discpercent) {
        public cust_term(String[] m) {
            this(m,"","","","","");
        }
    } 
     
    
    
}
