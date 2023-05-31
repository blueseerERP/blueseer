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
package com.blueseer.vdr;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author terryva
 */
public class venData {
   
     // add customer master customer master table only
    public static String[] addVendMstr(vd_mstr x) {
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
            int rows = _addVendMstr(x, con, ps, res, false);  
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
    
   
    public static boolean addVendMstrMass(ArrayList<String> list) {
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
                vd_mstr x = new vd_mstr(null, 
                ld[0], ld[1], ld[2], ld[3], ld[4],
                    ld[5], ld[6], ld[7], ld[8], ld[9],
                    BlueSeerUtils.setDateFormat(new java.util.Date()), BlueSeerUtils.setDateFormat(new java.util.Date()), 
                    bsmf.MainFrame.userid, ld[10], ld[11], ld[12], ld[13], 
                    ld[14], ld[15], ld[16], ld[17], 
                    ld[18], ld[19], ld[20], 
                    ld[21], ld[22], ld[23], ld[24], ld[25], 
                    ld[26], ld[27], ld[28]
                );
                _addVendMstr(x, con, ps, res, true);
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
    
    
    private static int _addVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from vd_mstr where vd_addr = ?";
        String sqlInsert = "insert into vd_mstr (vd_addr, vd_name, vd_line1, vd_line2, "
                        + "vd_line3, vd_city, vd_state, vd_zip, "
                        + "vd_country, vd_dateadd, vd_datemod, vd_usermod, "
                        + "vd_group, vd_market, vd_buyer, "
                        + "vd_shipvia, vd_terms, vd_misc, vd_price_code, "
                        + "vd_disc_code, vd_tax_code,  "
                        + "vd_ap_acct, vd_ap_cc, vd_bank, vd_curr, vd_remarks, vd_phone, vd_email, vd_is850export, vd_type ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update vd_mstr set " 
                + " vd_name = ?, vd_line1 = ?, vd_line2 = ?, "
                + "vd_line3 = ?, vd_city = ?, vd_state = ?, vd_zip = ?, "
                + "vd_country = ?, vd_dateadd = ?, vd_datemod = ?, vd_usermod = ?, "
                + "vd_group = ?, vd_market = ?, vd_buyer = ?,  "
                + "vd_shipvia = ?, vd_terms = ?, vd_freight_type = ?, vd_price_code = ?, "
                + "vd_disc_code = ?, vd_tax_code = ?, vd_misc = ?, "
                + "vd_ap_acct = ?, vd_ap_cc = ?, vd_bank = ?, vd_curr = ?, " 
                + "vd_remarks = ?, vd_phone = ?, vd_email = ?, vd_is850export = ?, vd_type = ? "
                + " where vd_addr = ? ; ";
          ps = con.prepareStatement(sqlSelect);
          ps.setString(1, x.vd_addr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
          PreparedStatement psu = con.prepareStatement(sqlUpdate);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.vd_addr);
            ps.setString(2, x.vd_name);
            ps.setString(3, x.vd_line1);
            ps.setString(4, x.vd_line2);
            ps.setString(5, x.vd_line3);
            ps.setString(6, x.vd_city);
            ps.setString(7, x.vd_state);
            ps.setString(8, x.vd_zip);
            ps.setString(9, x.vd_country);
            ps.setString(10, x.vd_dateadd);
            ps.setString(11, x.vd_datemod);
            ps.setString(12, x.vd_usermod);
            ps.setString(13, x.vd_group);
            ps.setString(14, x.vd_market);
            ps.setString(15, x.vd_buyer);
            ps.setString(16, x.vd_shipvia);
            ps.setString(17, x.vd_terms);
            ps.setString(18, x.vd_misc);
            ps.setString(19, x.vd_price_code);
            ps.setString(20,x.vd_disc_code);
            ps.setString(21,x.vd_tax_code);
            ps.setString(22,x.vd_ap_acct);
            ps.setString(23,x.vd_ap_cc);
            ps.setString(24,x.vd_bank);
            ps.setString(25,x.vd_curr);
            ps.setString(26,x.vd_remarks);
            ps.setString(27,x.vd_phone);
            ps.setString(28,x.vd_email);
            ps.setString(29,x.vd_is850export);
            ps.setString(30,x.vd_type);
            rows = ps.executeUpdate();
            } else {
                if (addupdate) {
               psu.setString(31, x.vd_addr);
                psu.setString(1, x.vd_name);
                psu.setString(2, x.vd_line1);
                psu.setString(3, x.vd_line2);
                psu.setString(4, x.vd_line3);
                psu.setString(5, x.vd_city);
                psu.setString(6, x.vd_state);
                psu.setString(7, x.vd_zip);
                psu.setString(8, x.vd_country);
                psu.setString(9, x.vd_dateadd);
                psu.setString(10, x.vd_datemod);
                psu.setString(11, x.vd_usermod);
                psu.setString(12, x.vd_group);
                psu.setString(13, x.vd_market);
                psu.setString(14, x.vd_buyer);
                psu.setString(15, x.vd_shipvia);
                psu.setString(16, x.vd_terms);
                psu.setString(17, x.vd_freight_type);
                psu.setString(18, x.vd_price_code);
                psu.setString(19,x.vd_disc_code);
                psu.setString(20,x.vd_tax_code);
                psu.setString(21,x.vd_misc);
                psu.setString(22,x.vd_ap_acct);
                psu.setString(23,x.vd_ap_cc);
                psu.setString(24,x.vd_bank);
                psu.setString(25,x.vd_curr);
                psu.setString(26,x.vd_remarks);
                psu.setString(27,x.vd_phone);
                psu.setString(28,x.vd_email);
                psu.setString(29,x.vd_is850export);
                psu.setString(30,x.vd_type); 
                rows = psu.executeUpdate();  
                psu.close();
              }
            }
            return rows;
    }
     
    public static String[] updateVendMstr(vd_mstr x) {
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
            int rows = _updateVendMstr(x, con, ps, res); 
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
    
    private static int _updateVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update vd_mstr set " 
                + " vd_name = ?, vd_line1 = ?, vd_line2 = ?, "
                + "vd_line3 = ?, vd_city = ?, vd_state = ?, vd_zip = ?, "
                + "vd_country = ?, vd_dateadd = ?, vd_datemod = ?, vd_usermod = ?, "
                + "vd_group = ?, vd_market = ?, vd_buyer = ?,  "
                + "vd_shipvia = ?, vd_terms = ?, vd_freight_type = ?, vd_price_code = ?, "
                + "vd_disc_code = ?, vd_tax_code = ?, vd_misc = ?, "
                + "vd_ap_acct = ?, vd_ap_cc = ?, vd_bank = ?, vd_curr = ?, " 
                + "vd_remarks = ?, vd_phone = ?, vd_email = ?, vd_is850export = ?, vd_type = ? "
                + " where vd_addr = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(31, x.vd_addr);
            ps.setString(1, x.vd_name);
            ps.setString(2, x.vd_line1);
            ps.setString(3, x.vd_line2);
            ps.setString(4, x.vd_line3);
            ps.setString(5, x.vd_city);
            ps.setString(6, x.vd_state);
            ps.setString(7, x.vd_zip);
            ps.setString(8, x.vd_country);
            ps.setString(9, x.vd_dateadd);
            ps.setString(10, x.vd_datemod);
            ps.setString(11, x.vd_usermod);
            ps.setString(12, x.vd_group);
            ps.setString(13, x.vd_market);
            ps.setString(14, x.vd_buyer);
            ps.setString(15, x.vd_shipvia);
            ps.setString(16, x.vd_terms);
            ps.setString(17, x.vd_freight_type);
            ps.setString(18, x.vd_price_code);
            ps.setString(19,x.vd_disc_code);
            ps.setString(20,x.vd_tax_code);
            ps.setString(21,x.vd_misc);
            ps.setString(22,x.vd_ap_acct);
            ps.setString(23,x.vd_ap_cc);
            ps.setString(24,x.vd_bank);
            ps.setString(25,x.vd_curr);
            ps.setString(26,x.vd_remarks);
            ps.setString(27,x.vd_phone);
            ps.setString(28,x.vd_email);
            ps.setString(29,x.vd_is850export);
            ps.setString(30,x.vd_type);
            rows = ps.executeUpdate();
        return rows;
    }
    
    public static String[] deleteVendMstr(vd_mstr x) {
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
            _deleteVendMstr(x, con, ps, res);  
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
    
    private static void _deleteVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from vd_mstr where vd_addr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
        sql = "delete from vpr_mstr where vpr_vend = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
        sql = "delete from vdp_mstr where vdp_vend = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
        sql = "delete from vdc_det where vdc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
    }
        
    public static vd_mstr getVendMstr(String[] x) {
        vd_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from vd_mstr where vd_addr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vd_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vd_mstr(m, res.getString("vd_addr"), res.getString("vd_site"), res.getString("vd_name"), 
                                res.getString("vd_line1"), res.getString("vd_line2"),
                    res.getString("vd_line3"), res.getString("vd_city"), res.getString("vd_state"), 
                    res.getString("vd_zip"), res.getString("vd_country"), res.getString("vd_dateadd"), 
                    res.getString("vd_datemod"), res.getString("vd_usermod"), res.getString("vd_group"), 
                    res.getString("vd_market"), res.getString("vd_buyer"), res.getString("vd_terms"), 
                    res.getString("vd_shipvia"), res.getString("vd_price_code"), res.getString("vd_disc_code"), 
                    res.getString("vd_tax_code"), res.getString("vd_ap_acct"), res.getString("vd_ap_cc"), 
                    res.getString("vd_remarks"), res.getString("vd_freight_type"), res.getString("vd_bank"), 
                    res.getString("vd_curr"), res.getString("vd_misc"), res.getString("vd_phone"), 
                    res.getString("vd_email"), res.getString("vd_is850export"), res.getString("vd_type") 
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vd_mstr(m);
        }
        return r;
    }
    
    
     // vds_det Vendor Shipto Table
    public static String[] addVDSDet(vds_det x) {
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
            _addVDSDet(x, con, ps, res, false);  // add vds_det
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
    
    private static void _addVDSDet(vds_det x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        if (x == null) return;
        String sqlSelect = "select * from vds_det where vds_code = ? and vds_shipto = ?";
        String sqlInsert = "insert into vds_det (vds_code, vds_shipto, vds_name, vds_line1, vds_line2, " 
                        + "vds_line3, vds_city, vds_state, vds_zip, vds_country, vds_type ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update vds_det set " 
                + " vds_name = ?, vds_line1 = ?, vds_line2 = ?, "
                + "vds_line3 = ?, vds_city = ?, vds_state = ?, vds_zip = ?, "
                + "vds_country = ?, vds_type = ? "
                + " where vds_code = ? and vds_shipto = ? ; ";
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.vds_code);
            ps.setString(2, x.vds_shipto);
            res = ps.executeQuery();
             if (! res.isBeforeFirst()) {
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, x.vds_code);
            ps.setString(2, x.vds_shipto);
            ps.setString(3, x.vds_name);
            ps.setString(4, x.vds_line1);
            ps.setString(5, x.vds_line2);
            ps.setString(6, x.vds_line3);
            ps.setString(7, x.vds_city);
            ps.setString(8, x.vds_state);
            ps.setString(9, x.vds_zip);
            ps.setString(10, x.vds_country);
            ps.setString(11, x.vds_type);
            int rows = ps.executeUpdate();
            } else {
                 if (addupdate) {
                    ps = con.prepareStatement(sqlUpdate); 
                    ps.setString(10, x.vds_code);
                    ps.setString(11, x.vds_shipto);
                    ps.setString(1, x.vds_name);
                    ps.setString(2, x.vds_line1);
                    ps.setString(3, x.vds_line2);
                    ps.setString(4, x.vds_line3);
                    ps.setString(5, x.vds_city);
                    ps.setString(6, x.vds_state);
                    ps.setString(7, x.vds_zip);
                    ps.setString(8, x.vds_country);
                    ps.setString(9, x.vds_type);
                    ps.executeUpdate();    
                 }
             }
    }
        
    public static String[] updateVDSDet(vds_det x) {
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
            _updateVDSDet(x, con, ps, res);  // add vds_det
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
    
    private static int _updateVDSDet(vds_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update vds_det set " 
                + " vds_name = ?, vds_line1 = ?, vds_line2 = ?, "
                + "vds_line3 = ?, vds_city = ?, vds_state = ?, vds_zip = ?, "
                + "vds_country = ?, vds_type = ? "
                + " where vds_code = ? and vds_shipto = ? ; ";
       ps = con.prepareStatement(sql);
        ps.setString(10, x.vds_code);
        ps.setString(11, x.vds_shipto);
            ps.setString(1, x.vds_name);
            ps.setString(2, x.vds_line1);
            ps.setString(3, x.vds_line2);
            ps.setString(4, x.vds_line3);
            ps.setString(5, x.vds_city);
            ps.setString(6, x.vds_state);
            ps.setString(7, x.vds_zip);
            ps.setString(8, x.vds_country);
            ps.setString(9, x.vds_type);
            rows = ps.executeUpdate();
        
       
        return rows;
    }
         
    public static String[] deleteVDSDet(vds_det x) {
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
            _deleteVDSDet(x.vds_code, x.vds_shipto, con, ps, res);  // add vds_det
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
    
    private static void _deleteVDSDet(String x, String y, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from vds_det where vds_code = ? and vds_shipto = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    public static vds_det getVDSDet(String shipto, String code) {
        vds_det r = null;
        String[] m = new String[2];
        String sql = "select * from vds_det where vds_shipto = ? and vds_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, shipto);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vds_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vds_det(m, res.getString("vds_code"), res.getString("vds_shipto"), res.getString("vds_name"), res.getString("vds_line1"), res.getString("vds_line2"),
                    res.getString("vds_line3"), res.getString("vds_city"), res.getString("vds_state"), res.getString("vds_zip"),
                    res.getString("vds_country"), res.getString("vds_type") 
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vds_det(m);
        }
        return r;
    }
    
    public static ArrayList<vds_det> getVDSDet(String code) {
        vds_det r = null;
        String[] m = new String[2];
        ArrayList<vds_det> list = new ArrayList<vds_det>();
        String sql = "select * from vds_det where vds_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vds_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vds_det(m, res.getString("vds_code"), res.getString("vds_shipto"), res.getString("vds_name"), res.getString("vds_line1"), res.getString("vds_line2"),
                    res.getString("vds_line3"), res.getString("vds_city"), res.getString("vds_state"), res.getString("vds_zip"),
                    res.getString("vds_country"), res.getString("vds_type") 
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vds_det(m);
               list.add(r);
        }
        return list;
    }
    
   
    
    
    public static String[] addUpdateVDCtrl(vd_ctrl x) {
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  vd_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into vd_ctrl (vdc_autovend) "
                        + " values (?); "; 
        String sqlUpdate = "update vd_ctrl set vdc_autovend = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.vdc_autovend);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.vdc_autovend);
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
   
    public static vd_ctrl getVDCtrl(String[] x) {
        vd_ctrl r = null;
        String[] m = new String[2];
        String sql = "select * from vd_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vd_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vd_ctrl(m, 
                                res.getString("vdc_autovend")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vd_ctrl(m);
        }
        return r;
    }
    
    public static String[] addVdpMstr(vdp_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  vdp_mstr where vdp_vitem = ? and vdp_vend = ?";
        String sqlInsert = "insert into vdp_mstr (vdp_vend, vdp_item, vdp_vitem, " +
         "vdp_upc, vdp_userid, vdp_misc, vdp_sku) " 
                        + " values (?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.vdp_vitem);
             ps.setString(2, x.vdp_vend);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.vdp_vend);
            psi.setString(2, x.vdp_item);
            psi.setString(3, x.vdp_vitem);
            psi.setString(4, x.vdp_upc);
            psi.setString(5, x.vdp_userid);
            psi.setString(6, x.vdp_misc);
            psi.setString(7, x.vdp_sku);
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

    public static String[] addOrUpdateVdpMstr(vdp_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  vdp_mstr where vdp_vitem = ? and vdp_vend = ?";
        String sqlInsert = "insert into vdp_mstr (vdp_vend, vdp_item, vdp_vitem, " +
         "vdp_upc, vdp_userid, vdp_misc, vdp_sku) " 
                        + " values (?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update vdp_mstr set vdp_item = ?, vdp_upc = ?, " +
                " vdp_userid = ?, vdp_misc = ?, vdp_sku = ?  " +   
                          " where vdp_vitem = ? and vdp_vend = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.vdp_vitem);
             ps.setString(2, x.vdp_vend);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.vdp_vend);
            psi.setString(2, x.vdp_item);
            psi.setString(3, x.vdp_vitem);
            psi.setString(4, x.vdp_upc);
            psi.setString(5, x.vdp_userid);
            psi.setString(6, x.vdp_misc);
            psi.setString(7, x.vdp_sku); 
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.vdp_item);
            psu.setString(2, x.vdp_upc);
            psu.setString(3, x.vdp_userid);
            psu.setString(4, x.vdp_misc);
            psu.setString(5, x.vdp_sku);
            psu.setString(6, x.vdp_vitem);
            psu.setString(7, x.vdp_vend);
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

    public static String[] updateVdpMstr(vdp_mstr x) {
        String[] m = new String[2];
        String sql = "update vdp_mstr set vdp_item = ?, vdp_upc = ?, " +
                " vdp_userid = ?, vdp_misc = ?, vdp_sku = ?  " +   
                          " where vdp_vitem = ? and vdp_vend = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.vdp_item);
            ps.setString(2, x.vdp_upc);
            ps.setString(3, x.vdp_userid);
            ps.setString(4, x.vdp_misc);
            ps.setString(5, x.vdp_sku);
            ps.setString(6, x.vdp_vitem);
            ps.setString(7, x.vdp_vend);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteVdpMstr(vdp_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from vdp_mstr where vdp_vitem = ? and vdp_vend = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.vdp_vitem);
        ps.setString(2, x.vdp_vend);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static vdp_mstr getVdpMstr(String[] x) {
        vdp_mstr r = null;
        String[] m = new String[2];
        String sql = "";
         if (x.length >= 2 && ! x[1].isEmpty()) {
            sql = "select * from vdp_mstr where vdp_vend = ? and vdp_vitem = ?;";
         } else {
            sql = "select * from vdp_mstr where vdp_vitem = ? limit 1 ;";  
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
                r = new vdp_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vdp_mstr(m, res.getString("vdp_vend"), 
                            res.getString("vdp_item"),
                            res.getString("vdp_vitem"),
                            res.getString("vdp_upc"),
                            res.getString("vdp_userid"),
                            res.getString("vdp_misc"),
                            res.getString("vdp_sku")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vdp_mstr(m);
        }
        return r;
    }
    
    
    
    // misc
    
    public static ArrayList getVendMstrList() {
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

                res = st.executeQuery("select vd_addr from vd_mstr order by vd_addr;");
                while (res.next()) {
                    myarray.add(res.getString("vd_addr"));

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

    public static ArrayList getVendMstrListMinusCarrier() {
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

                res = st.executeQuery("select vd_addr from vd_mstr where vd_type <> 'carrier' order by vd_addr;");
                while (res.next()) {
                    myarray.add(res.getString("vd_addr"));

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

    
    public static ArrayList getVendShipList(String code, String type) {
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

                res = st.executeQuery("select vds_shipto from vds_det where vds_code = " + "'" + code + "'" +
                        " and vds_type = " + "'" + type + "'" +
                        " order by vds_shipto;");
                while (res.next()) {
                    myarray.add(res.getString("vds_shipto"));

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

    
    public static ArrayList getVendMstrListBetween(String from, String to) {
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

                res = st.executeQuery("select vd_addr from vd_mstr "
                        + " where vd_addr >= " + "'" + from + "'"
                        + " and vd_addr <= " + "'" + to + "'"
                        + " order by vd_addr;");
                while (res.next()) {
                    myarray.add(res.getString("vd_addr"));

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

    public static ArrayList getVendTermsList() {
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

                res = st.executeQuery("select cut_code from cust_term order by cut_code;");
                while (res.next()) {
                    myarray.add(res.getString("cut_code"));

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
        
    public static ArrayList getVendNameList() {
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

                res = st.executeQuery("select vd_name from vd_mstr order by vd_name;");
                while (res.next()) {
                    myarray.add(res.getString("vd_name").replace("'", ""));

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

    public static String getVendTerms(String vend) {
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
                

                res = st.executeQuery("select vd_terms from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("vd_terms");                    
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
           
    public static String getVendName(String vend) {
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
                res = st.executeQuery("select vd_name from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("vd_name");                    
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
         
    public static String getVendAPAcct(String vend) {
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
        

        res = st.executeQuery("select vd_ap_acct from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
       while (res.next()) {
        myitem = res.getString("vd_ap_acct");                    
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

    public static String getVendAPCC(String vend) {
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
        

        res = st.executeQuery("select vd_ap_cc from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
       while (res.next()) {
        myitem = res.getString("vd_ap_cc");                    
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

    public static String getVendItemFromItem(String vend, String item) {
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
            
            res = st.executeQuery("select vdp_vitem from vdp_mstr where vdp_vend = " + "'" + vend + "'" + 
                                  " AND vdp_item = " + "'" + item + "'" + ";");
           while (res.next()) {
               mystring = res.getString("vdp_vitem");

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

    public static String getVendCurrency(String vend) {
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
            try{
                

                res = st.executeQuery("select vd_curr from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("vd_curr");                    
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

    public static String[] getVendInfo(String vend) {
           // get vendor specific data
            // addr, acct, cc, currency, bank, terms, site
            String[] vendinfo = new String[]{"","","","","","",""};
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
                res = st.executeQuery("select * from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                   vendinfo[0] = res.getString("vd_addr");
                   vendinfo[1] = res.getString("vd_ap_acct");
                   vendinfo[2] = res.getString("vd_ap_cc");
                   vendinfo[3] = res.getString("vd_curr");
                   vendinfo[4] = res.getString("vd_bank");
                   vendinfo[5] = res.getString("vd_terms");
                   vendinfo[6] = res.getString("vd_site");         
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
        return vendinfo;
        
    } 

    
    public record vd_mstr(String[] m, String vd_addr, String vd_site, String vd_name, 
        String vd_line1, String vd_line2, String vd_line3, 
        String vd_city, String vd_state, String vd_zip,
    String vd_country, String vd_dateadd, String vd_datemod, String vd_usermod, 
    String vd_group, String vd_market, String vd_buyer,  
    String vd_terms, String vd_shipvia, String vd_price_code,
    String vd_disc_code, String vd_tax_code, String vd_ap_acct,
    String vd_ap_cc, String vd_remarks, String vd_freight_type, String vd_bank, String vd_curr, 
    String vd_misc, String vd_phone, String vd_email, String vd_is850export, String vd_type) {
        public vd_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "");
        }
    }
   
     public record vd_ctrl (String[] m, String vdc_autovend) {
        public vd_ctrl(String[] m) {
            this(m,"");
        }
    } 
    
    public record vdp_mstr(String[] m, String vdp_vend, String vdp_item, String vdp_vitem,  
    String vdp_upc, String vdp_userid, String vdp_misc, String vdp_sku) {
        public vdp_mstr(String[] m) {
            this(m,"","","","","","","");
        }
    }
 
    public record vpr_mstr(String[] m, String vpr_vend, String vpr_item, String vpr_type,  
    String vpr_desc, String vpr_uom, String vpr_curr, String vpr_price) {
        public vpr_mstr(String[] m) {
            this(m,"","","","","","","");
        }
    }
  
    public record vds_det(String[] m, String vds_code, String vds_shipto, 
        String vds_name, String vds_line1, String vds_line2,
        String vds_line3, String vds_city, String vds_state, 
        String vds_zip, String vds_country, String vds_type) {
        public vds_det(String[] m) {
            this(m,"","","","","","","","","","","");
        }
    } 
}
