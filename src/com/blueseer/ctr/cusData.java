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
import java.util.ArrayList;
import javax.swing.JTable;

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
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _addCustMstr(x, con);  // add cms_det
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        } finally {
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
    public static String[] addCustomerTransaction(cm_mstr cm, JTable contacttable, cms_det cms) {
        String[] m = new String[2];
        Connection bscon = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addCustMstr(cm, bscon);  // add cm_mstr
            _deleteCMCDetAll(cm.cm_code, bscon);    // delete cmc_det
            for (int j = 0; j < contacttable.getRowCount(); j++) {  
            cmc_det z = new cmc_det(null, 
                contacttable.getValueAt(j, 0).toString(),
                cm.cm_code,
                contacttable.getValueAt(j, 1).toString(),
                contacttable.getValueAt(j, 2).toString(),
                contacttable.getValueAt(j, 3).toString(),
                contacttable.getValueAt(j, 4).toString(),
                contacttable.getValueAt(j, 5).toString()
                );
            _addCMCDet(z, bscon);  // add cmc_det
            }
            _addCMSDet(cms, bscon);  // add cms_det
            bscon.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
             } catch (SQLException rb) {
                 MainFrame.bslog(rb);
             }
        } finally {
            if (bscon != null) {
                try {
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
     
    
    private static int _addCustMstr(cm_mstr x, Connection con) throws SQLException {
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
          PreparedStatement ps = con.prepareStatement(sqlSelect);
          ps.setString(1, x.cm_code);
          ResultSet res = ps.executeQuery();
          PreparedStatement psi = con.prepareStatement(sqlInsert);  
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
            rows = psi.executeUpdate();
            } 
            ps.close();
            psi.close();
            res.close();
            return rows;
    }
     
    
    
    public static String[] updateCustMstr(cm_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _updateCustMstr(x, con);  // add cms_det
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        } finally {
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
    
    private static int _updateCustMstr(cm_mstr x, Connection con) throws SQLException {
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
        PreparedStatement psu = con.prepareStatement(sql);
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
        return rows;
    }
    
    
    public static String[] deleteCustMstr(cm_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteCustMstr(x, con);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        } finally {
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
    
    private static void _deleteCustMstr(cm_mstr x, Connection con) throws SQLException { 
       
        PreparedStatement ps = null;  
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
    
    
    // cms_det Customer Shipto Table
    public static String[] addCMSDet(cms_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _addCMSDet(x, con);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        } finally {
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
    
    private static void _addCMSDet(cms_det x, Connection con) throws SQLException {
        if (x == null) return;
        String sqlInsert = "insert into cms_det (cms_code, cms_shipto, cms_name, cms_line1, cms_line2, " 
                        + "cms_line3, cms_city, cms_state, cms_zip, cms_country ) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
           PreparedStatement psi = con.prepareStatement(sqlInsert);
            psi.setString(1, x.cms_code);
            psi.setString(2, x.cms_shipto);
            psi.setString(3, x.cms_name);
            psi.setString(4, x.cms_line1);
            psi.setString(5, x.cms_line2);
            psi.setString(6, x.cms_line3);
            psi.setString(7, x.cms_city);
            psi.setString(8, x.cms_state);
            psi.setString(9, x.cms_zip);
            psi.setString(10, x.cms_country);
            int rows = psi.executeUpdate();
            psi.close();
    }
        
    public static String[] updateCMSDet(cms_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _updateCMSDet(x, con);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        } finally {
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
    
    private static int _updateCMSDet(cms_det x, Connection con) {
        int rows = 0;
        String sql = "update cms_det set " 
                + " cms_name = ?, cms_line1 = ?, cms_line2 = ?, "
                + "cms_line3 = ?, cms_city = ?, cms_state = ?, cms_zip = ?, "
                + "cms_country = ? "
                + " where cms_code = ? and cms_shipto = ? ; ";
        try (PreparedStatement psu = con.prepareStatement(sql)) {
        psu.setString(9, x.cms_code);
        psu.setString(10, x.cms_shipto);
            psu.setString(1, x.cms_name);
            psu.setString(2, x.cms_line1);
            psu.setString(3, x.cms_line2);
            psu.setString(4, x.cms_line3);
            psu.setString(5, x.cms_city);
            psu.setString(6, x.cms_state);
            psu.setString(7, x.cms_zip);
            psu.setString(8, x.cms_country);
            rows = psu.executeUpdate();
        
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               
        }
        return rows;
    }
         
    public static String[] deleteCMSDet(cms_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteCMSDet(x.cms_code, x.cms_shipto, con);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        } finally {
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
    
    private static void _deleteCMSDet(String x, String y, Connection con) throws SQLException { 
       
        PreparedStatement ps = null; 
        String sql = "delete from cms_det where cms_code = ? and cms_shipto = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(1, y);
        ps.executeUpdate();
        ps.close();
        
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
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _addCMCDet(x, con);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        } finally {
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
    
    private static void _addCMCDet(cmc_det x, Connection con) throws SQLException {
        if (x == null) return;
        String sqlInsert = "insert into cmc_det (cmc_code, cmc_type, cmc_name, " 
                        + "cmc_phone, cmc_fax, cmc_email ) "
                        + " values (?,?,?,?,?,?); "; 
           PreparedStatement psi = con.prepareStatement(sqlInsert);
            psi.setString(1, x.cmc_code);
            psi.setString(2, x.cmc_type);
            psi.setString(3, x.cmc_name);
            psi.setString(4, x.cmc_phone);
            psi.setString(5, x.cmc_fax);
            psi.setString(6, x.cmc_email);
            int rows = psi.executeUpdate();
            psi.close();
            
    }
    
    public static String[] updateCMCDet(cmc_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _updateCMCDet(x, con);  // add cms_det 
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        } finally {
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
    
    private static int _updateCMCDet(cmc_det x, Connection con) {
        int rows = 0;
        String sql = "update cmc_det set " 
                + " cmc_type = ?, cmc_name = ?, cmc_phone = ?, "
                + "cmc_fax = ?, cmc_email = ? "
                + " where cmc_code = ? and cmc_id = ? ; ";
        try (PreparedStatement psu = con.prepareStatement(sql)) {
        psu.setString(6, x.cmc_code);
        psu.setString(7, x.cmc_id);
            psu.setString(1, x.cmc_type);
            psu.setString(2, x.cmc_name);
            psu.setString(3, x.cmc_phone);
            psu.setString(4, x.cmc_fax);
            psu.setString(5, x.cmc_email);
            rows = psu.executeUpdate();
        
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               
        }
        return rows;
    }
        
    public static String[] deleteCMCDet(cmc_det x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteCMCDet(x.cmc_id, x.cmc_code, con);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        } finally {
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
           
    private static void _deleteCMCDet(String x, String y, Connection con) throws SQLException { 
        
        PreparedStatement ps = null; 
        String sql = "delete from cmc_det where cmc_id = ? and cmc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
        ps.close();
        
    }
    
    private static void _deleteCMCDetAll(String x, Connection con) throws SQLException { 
        
        PreparedStatement ps = null; 
        String sql = "delete from cmc_det where cmc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
        
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
    
     
     
    
    
}
