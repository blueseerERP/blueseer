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
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
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
    
      // add order master table only
    public static String[] addOrderMstr(so_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _addOrderMstr(x, con, ps, res);  
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
        
    private static int _addOrderMstr(so_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from so_mstr where so_nbr = ?";
        String sqlInsert = "insert into so_mstr (so_nbr, so_cust, so_ship, " 
                        + "so_site, so_curr, so_shipvia, so_wh, so_po, so_due_date, so_ord_date, "
                        + "so_create_date, so_userid, so_status, so_isallocated, "
                        + "so_terms, so_ar_acct, so_ar_cc, so_rmks, so_type, so_taxcode ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.so_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.so_nbr);
            ps.setString(2, x.so_cust);
            ps.setString(3, x.so_ship);
            ps.setString(4, x.so_site);
            ps.setString(5, x.so_curr);
            ps.setString(6, x.so_shipvia);
            ps.setString(7, x.so_wh);
            ps.setString(8, x.so_po);
            ps.setString(9, x.so_due_date);
            ps.setString(10, x.so_ord_date);
            ps.setString(11, x.so_create_date);
            ps.setString(12, x.so_userid);
            ps.setString(13, x.so_status);
            ps.setString(14, x.so_isallocated);
            ps.setString(15, x.so_terms);
            ps.setString(16, x.so_ar_acct);
            ps.setString(17, x.so_ar_cc);
            ps.setString(18, x.so_rmks);
            ps.setString(19, x.so_type);
            ps.setString(20, x.so_taxcode);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
        
     // add order master.... multiple table transaction function
    public static String[] addOrderTransaction(ArrayList<sod_det> sod, so_mstr so, ArrayList<so_tax> sot, ArrayList<sod_tax> sotd, ArrayList<sos_det> sos) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addOrderMstr(so, bscon, ps, res);  
            for (sod_det z : sod) {
                _addOrderDet(z, bscon, ps, res);
            }
            for (so_tax z : sot) {
                _addOrderTaxMstr(z, bscon, ps, res);
            }
            for (sod_tax z : sotd) {
                _addOrderTaxDet(z, bscon, ps, res);
            }
            for (sos_det z : sos) {
                _addOrderSummaryDet(z, bscon, ps, res);
            }
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
            if (bscon != null) {
                try {
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
     
    public static String[] updateOrderMstr(so_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _updateOrderMstr(x, con);  // add cms_det
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
   
    private static int _updateOrderMstr(so_mstr x, Connection con) throws SQLException {
        int rows = 0;
        String sql = "update so_mstr set so_cust = ?, so_ship = ?, " +
                "so_site = ?, so_curr = ?, so_shipvia = ?, so_wh = ?, so_po = ?, so_due_date = ?, so_ord_date = ?, so_create_date = ?, " +
                "so_userid = ?, so_status = ?, so_isallocated = ?, so_terms = ?, so_ar_acct = ?, so_ar_cc = ?, so_rmks = ?, so_type = ?, " +
                "so_taxcode = ? " +
                 " where so_nbr = ? ; ";
	PreparedStatement psu = con.prepareStatement(sql) ;
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
            rows = psu.executeUpdate();
            psu.close();
        return rows;
    }
    
    private static int _updateOrderDet(sod_det x, Connection con) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_det where sod_nbr = ? and sod_line = ?";
        String sqlUpdate = "update sod_det set sod_part = ?, sod_custpart = ?, " +
                "sod_po = ?, sod_ord_qty = ?, sod_uom = ?, sod_all_qty = ?, " +
                " sod_listprice = ?, sod_disc = ?, sod_netprice = ?, sod_ord_date = ?, " +
                "sod_due_date = ?, sod_shipped_qty = ?, sod_status = ?, sod_wh = ?, sod_loc = ?, " +
                " sod_desc = ?, sod_taxamt = ?, sod_site = ? " +
                 " where sod_nbr = ? and sod_line = ? ; ";
        String sqlInsert = "insert into sod_det (sod_nbr, sod_line, sod_part, sod_custpart, " 
                        + "sod_po, sod_ord_qty, sod_uom, sod_all_qty, " 
                        + "sod_listprice, sod_disc, sod_netprice, sod_ord_date, sod_due_date, " 
                        + "sod_shipped_qty, sod_status, sod_wh, sod_loc, "
                        + "sod_desc, sod_taxamt, sod_site ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        PreparedStatement ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.sod_nbr);
        ps.setString(2, x.sod_line);
        ResultSet res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	PreparedStatement psi = con.prepareStatement(sqlInsert) ;
            psi.setString(1, x.sod_nbr);
            psi.setString(2, x.sod_line);
            psi.setString(3, x.sod_part);
            psi.setString(4, x.sod_custpart);
            psi.setString(5, x.sod_po);
            psi.setString(6, x.sod_ord_qty);
            psi.setString(7, x.sod_uom);
            psi.setString(8, x.sod_all_qty);
            psi.setString(9, x.sod_listprice);
            psi.setString(10, x.sod_disc);
            psi.setString(11, x.sod_netprice);
            psi.setString(12, x.sod_ord_date);
            psi.setString(13, x.sod_due_date);
            psi.setString(14, x.sod_shipped_qty);
            psi.setString(15, x.sod_status);
            psi.setString(16, x.sod_wh);
            psi.setString(17, x.sod_loc);
            psi.setString(18, x.sod_desc);
            psi.setString(19, x.sod_taxamt);
            psi.setString(20, x.sod_site);
            rows = psi.executeUpdate();
            psi.close();
        } else {    // update
        PreparedStatement psu = con.prepareStatement(sqlUpdate) ;
            psu.setString(19, x.sod_nbr);
            psu.setString(20, x.sod_line);
            psu.setString(1, x.sod_part);
            psu.setString(2, x.sod_custpart);
            psu.setString(3, x.sod_po);
            psu.setString(4, x.sod_ord_qty);
            psu.setString(5, x.sod_uom);
            psu.setString(6, x.sod_all_qty);
            psu.setString(7, x.sod_listprice);
            psu.setString(8, x.sod_disc);
            psu.setString(9, x.sod_netprice);
            psu.setString(10, x.sod_ord_date);
            psu.setString(11, x.sod_due_date);
            psu.setString(12, x.sod_shipped_qty);
            psu.setString(13, x.sod_status);
            psu.setString(14, x.sod_wh);
            psu.setString(15, x.sod_loc);
            psu.setString(16, x.sod_desc);
            psu.setString(17, x.sod_taxamt);
            psu.setString(18, x.sod_site);
            rows = psu.executeUpdate();
            psu.close();
        }
            
        return rows;
    }
     
    
     // update order master.... multiple table transaction function
    public static String[] updateOrderTransaction(String x, ArrayList<String> lines, ArrayList<sod_det> sod, so_mstr so, ArrayList<so_tax> sot, ArrayList<sod_tax> sotd, ArrayList<sos_det> sos) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            for (String line : lines) {
               _deleteOrderLines(x, line, bscon);  // discard unwanted lines
             }
            for (sod_det z : sod) {
                if (z.sod_status.equals(getGlobalProgTag("closed"))) {
                    continue;
                }
                _updateOrderDet(z, bscon);
            }
            _deleteOrderTaxMstr(so.so_nbr, bscon);
            for (so_tax z : sot) {
                _addOrderTaxMstr(z, bscon, ps, res);
            }
            _deleteOrderTaxDet(so.so_nbr, bscon);
            for (sod_tax z : sotd) {
                _addOrderTaxDet(z, bscon, ps, res);
            }
            _deleteOrderSummaryDet(so.so_nbr, bscon);
            for (sos_det z : sos) {
                _addOrderSummaryDet(z, bscon, ps, res);
            }
             _updateOrderMstr(so, bscon);  // update so_mstr
            bscon.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
    
    
    public static String[] deleteOrderMstr(so_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteOrderMstr(x, con);  // add cms_det
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
    
    public static String[] deleteOrderLines(String x, ArrayList<String> lines) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
             for (String line : lines) {
               _deleteOrderLines(x, line, con);  // add cms_det
             }
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
    
    private static void _deleteOrderLines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from sod_det where sod_nbr = ? and sod_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        ps.close();
    }
    
    
    private static void _deleteOrderMstr(so_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
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
    }
    
    private static void _deleteOrderTaxMstr(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from so_tax where sot_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
      
    private static void _deleteOrderTaxDet(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from sod_tax where sodt_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    private static void _deleteOrderSummaryDet(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from sos_det where sos_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    
    
    public static so_mstr getOrderMstr(String[] x) {
        so_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from so_mstr where so_nbr = ? ;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new so_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new so_mstr(m, res.getString("so_nbr"), res.getString("so_cust"), res.getString("so_ship"),
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
               r = new so_mstr(m);
        }
        return r;
    }
                             
    private static int _addOrderDet(sod_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_det where sod_nbr = ? and sod_line = ?";
        String sqlInsert = "insert into sod_det (sod_nbr, sod_line, sod_part, sod_custpart, " 
                        + "sod_po, sod_ord_qty, sod_uom, sod_all_qty, " 
                        + "sod_listprice, sod_disc, sod_netprice, sod_ord_date, sod_due_date, " 
                        + "sod_shipped_qty, sod_status, sod_wh, sod_loc, "
                        + "sod_desc, sod_taxamt, sod_site ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sod_nbr);
          ps.setString(2, x.sod_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sod_nbr);
            ps.setString(2, x.sod_line);
            ps.setString(3, x.sod_part);
            ps.setString(4, x.sod_custpart);
            ps.setString(5, x.sod_po);
            ps.setString(6, x.sod_ord_qty);
            ps.setString(7, x.sod_uom);
            ps.setString(8, x.sod_all_qty);
            ps.setString(9, x.sod_listprice);
            ps.setString(10, x.sod_disc);
            ps.setString(11, x.sod_netprice);
            ps.setString(12, x.sod_ord_date);
            ps.setString(13, x.sod_due_date);
            ps.setString(14, x.sod_shipped_qty);
            ps.setString(15, x.sod_status);
            ps.setString(16, x.sod_wh);
            ps.setString(17, x.sod_loc);
            ps.setString(18, x.sod_desc);
            ps.setString(19, x.sod_taxamt);
            ps.setString(20, x.sod_site);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
       
    private static int _addOrderSummaryDet(sos_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sos_det where sos_nbr = ?";
        String sqlInsert = "insert into sos_det (sos_nbr, sos_desc, sos_type, " 
                        + "sos_amttype, sos_amt ) "
                        + " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sos_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sos_nbr);
            ps.setString(2, x.sos_desc);
            ps.setString(3, x.sos_type);
            ps.setString(4, x.sos_amttype);
            ps.setString(5, x.sos_amt);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addOrderTaxDet(sod_tax x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_tax where sodt_nbr = ? and sodt_line = ?";
        String sqlInsert = "insert into sod_tax (sodt_nbr, sodt_line, sodt_desc," 
                        + "sodt_percent, sodt_type ) "
                        + " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sodt_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);    
            if (! res.isBeforeFirst()) { 
            ps.setString(1, x.sodt_nbr);
            ps.setString(2, x.sodt_line);
            ps.setString(3, x.sodt_desc);
            ps.setString(4, x.sodt_percent);
            ps.setString(5, x.sodt_type);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addOrderTaxMstr(so_tax x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from so_tax where sot_nbr = ?";
        String sqlInsert = "insert into so_tax (sot_nbr, sot_desc, sot_percent, sot_type ) " 
                        + " values (?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sot_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sot_nbr);
            ps.setString(2, x.sot_desc);
            ps.setString(3, x.sot_percent);
            ps.setString(4, x.sot_type);
            rows = ps.executeUpdate();
            } 
           
            res.close();
            return rows;
    }
    
    // miscellaneous SQL queries
    public static Double getOrderItemAllocatedQty(String item, String site) {
       Double qty = 0.00;
     try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("SELECT  sum(case when sod_all_qty = '' then 0 else (sod_all_qty - sod_shipped_qty) end) as allqty  " +
                                    " FROM  sod_det inner join so_mstr on so_nbr = sod_nbr  " +
                                    " where sod_part = " + "'" + item + "'" + 
                                    " AND so_status <> " + "'" + getGlobalProgTag("closed") + "'" +
                                    " AND so_site = " + "'" + site + "'" +          
                                    " group by sod_part ;");

                                    while (res.next()) {
                                    qty = res.getDouble("allqty");
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
    return qty;

    }

    public static ArrayList<String> getOrderLines(String order) {
        ArrayList<String> lines = new ArrayList<String>();
        try{
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url + db, user, pass);
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("SELECT sod_line from sod_det " +
                   " where sod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("sod_line"));
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
        return lines;
    }
     
    public static String getOrderCurrency(String order) {
        String curr = "";
        try{
        Connection con = DriverManager.getConnection(url + db, user, pass);
        Statement st = con.createStatement();
        ResultSet res = null;
            try{
                res = st.executeQuery("select so_curr from so_mstr where so_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    curr = res.getString("so_curr");
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
             return curr;
    }
    
    
    public record so_mstr(String[] m, String so_nbr, String so_cust, String so_ship, String so_site,
    String so_curr, String so_shipvia, String so_wh, String so_po, String so_due_date,
    String so_ord_date, String so_create_date, String so_userid, String so_status, String so_isallocated,
    String so_terms, String so_ar_acct, String so_ar_cc, String so_rmks, String so_type, String so_taxcode) {
        public so_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", ""
                    );
        }
    }
    
                              
    public record sod_det(String[] m, String sod_nbr, String sod_line, String sod_part, String sod_custpart, 
        String sod_po, String sod_ord_qty, String sod_uom, String sod_all_qty, 
        String sod_listprice, String sod_disc, String sod_netprice, String sod_ord_date, 
        String sod_due_date, String sod_shipped_qty, String sod_status, String sod_wh, 
        String sod_loc, String sod_desc, String sod_taxamt, String sod_site) {
        public sod_det(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "");
        }
    }
    
          
    public record so_tax(String[] m, String sot_nbr, String sot_desc, String sot_percent, String sot_type ) {
        public so_tax(String[] m) {
            this (m, "", "", "", "");
        }
    }
    
   
     public record sod_tax(String[] m, String sodt_nbr, String sodt_line, String sodt_desc, 
        String sodt_percent, String sodt_type ) {
        public sod_tax(String[] m) {
            this (m, "", "", "", "", "");
        }
    }
    
    public record sos_det(String[] m, String sos_nbr, String sos_desc, String sos_type, 
        String sos_amttype, String sos_amt) {
        public sos_det(String[] m) {
            this (m, "", "", "", "", "");
        }
    }
    
}
