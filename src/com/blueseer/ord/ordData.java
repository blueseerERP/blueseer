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
import static bsmf.MainFrame.ds;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
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
            
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
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
                        + "so_terms, so_ar_acct, so_ar_cc, so_rmks, so_type, so_taxcode, "
                        + "so_issourced, so_confirm, so_plan, so_entrytype ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
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
            ps.setString(21, x.so_issourced);
            ps.setString(22, x.so_confirm);
            ps.setString(23, x.so_plan);
            ps.setString(24, x.so_entrytype);
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
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            _addOrderMstr(so, bscon, ps, res);  
            for (sod_det z : sod) {
                _addOrderDet(z, bscon, ps, res);
            }
            if (sot != null) {
                for (so_tax z : sot) {
                    _addOrderTaxMstr(z, bscon, ps, res);
                }
            }
            if (sotd != null) {
                for (sod_tax z : sotd) {
                    _addOrderTaxDet(z, bscon, ps, res);
                }
            }
            if (sos != null) {
                for (sos_det z : sos) {
                    _addOrderSummaryDet(z, bscon, ps, res);
                }
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
                    bscon.setAutoCommit(true);
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
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            int rows = _updateOrderMstr(x, con, ps);  // add cms_det
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
   
    private static int _updateOrderMstr(so_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update so_mstr set so_cust = ?, so_ship = ?, " +
                "so_site = ?, so_curr = ?, so_shipvia = ?, so_wh = ?, so_po = ?, so_due_date = ?, so_ord_date = ?, so_create_date = ?, " +
                "so_userid = ?, so_status = ?, so_isallocated = ?, so_terms = ?, so_ar_acct = ?, so_ar_cc = ?, so_rmks = ?, so_type = ?, " +
                "so_taxcode = ?, so_confirm = ?, so_plan = ? " +
                 " where so_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(22, x.so_nbr);
            ps.setString(1, x.so_cust);
            ps.setString(2, x.so_ship);
            ps.setString(3, x.so_site);
            ps.setString(4, x.so_curr);
            ps.setString(5, x.so_shipvia);
            ps.setString(6, x.so_wh);
            ps.setString(7, x.so_po);
            ps.setString(8, x.so_due_date);
            ps.setString(9, x.so_ord_date);
            ps.setString(10, x.so_create_date);
            ps.setString(11, x.so_userid);
            ps.setString(12, x.so_status);
            ps.setString(13, x.so_isallocated);
            ps.setString(14, x.so_terms);
            ps.setString(15, x.so_ar_acct);
            ps.setString(16, x.so_ar_cc);
            ps.setString(17, x.so_rmks);
            ps.setString(18, x.so_type);
            ps.setString(19, x.so_taxcode);
            ps.setString(20, x.so_confirm);
            ps.setString(21, x.so_plan);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateOrderDet(sod_det x, so_mstr z, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_det where sod_nbr = ? and sod_line = ?";
        String sqlUpdate = "update sod_det set sod_item = ?, sod_custitem = ?, " +
                "sod_po = ?, sod_ord_qty = ?, sod_uom = ?, sod_all_qty = ?, " +
                " sod_listprice = ?, sod_disc = ?, sod_netprice = ?, sod_ord_date = ?, " +
                "sod_due_date = ?, sod_shipped_qty = ?, sod_status = ?, sod_wh = ?, sod_loc = ?, " +
                " sod_desc = ?, sod_taxamt = ?, sod_site = ?, sod_bom = ?, sod_ship = ? " +
                 " where sod_nbr = ? and sod_line = ? ; ";
        String sqlInsert = "insert into sod_det (sod_nbr, sod_line, sod_item, sod_custitem, " 
                        + "sod_po, sod_ord_qty, sod_uom, sod_all_qty, " 
                        + "sod_listprice, sod_disc, sod_netprice, sod_ord_date, sod_due_date, " 
                        + "sod_shipped_qty, sod_status, sod_wh, sod_loc, "
                        + "sod_desc, sod_taxamt, sod_site, sod_bom, sod_ship ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.sod_nbr);
        ps.setString(2, x.sod_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.sod_nbr);
            ps.setString(2, x.sod_line);
            ps.setString(3, x.sod_item);
            ps.setString(4, x.sod_custitem);
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
            ps.setString(21, x.sod_bom);
            ps.setString(22, x.sod_ship);
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(21, x.sod_nbr);
            ps.setString(22, x.sod_line);
            ps.setString(1, x.sod_item);
            ps.setString(2, x.sod_custitem);
            ps.setString(3, z.so_po);
            ps.setString(4, x.sod_ord_qty);
            ps.setString(5, x.sod_uom);
            ps.setString(6, x.sod_all_qty);
            ps.setString(7, x.sod_listprice);
            ps.setString(8, x.sod_disc);
            ps.setString(9, x.sod_netprice);
            ps.setString(10, z.so_ord_date);
            ps.setString(11, z.so_due_date);
            ps.setString(12, x.sod_shipped_qty);
            ps.setString(13, x.sod_status);
            ps.setString(14, x.sod_wh);
            ps.setString(15, x.sod_loc);
            ps.setString(16, x.sod_desc);
            ps.setString(17, x.sod_taxamt);
            ps.setString(18, x.sod_site);
            ps.setString(19, x.sod_bom);
            ps.setString(20, x.sod_ship);
            rows = ps.executeUpdate();
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
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            for (String line : lines) {
               _deleteOrderLines(x, line, bscon, ps);  // discard unwanted lines
             }
            for (sod_det z : sod) {
                if (z.sod_status.equals(getGlobalProgTag("closed"))) {
                    continue;
                }
                _updateOrderDet(z, so, bscon, ps, res);
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
             _updateOrderMstr(so, bscon, ps);  // update so_mstr
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
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
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
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
             for (String line : lines) {
               _deleteOrderLines(x, line, con, ps);  // add cms_det
             }
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
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _deleteOrderLines(String x, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from sod_det where sod_nbr = ? and sod_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
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
    
    public static salesOrder getOrderMstrSet(String[] x ) {
        salesOrder r = null;
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            
            // order master
            so_mstr so = _getOrderMstr(x, bscon, ps, res);
            ArrayList<sod_det> sod = _getOrderDet(x, bscon, ps, res);
            ArrayList<sos_det> sos = _getOrderSOS(x, bscon, ps, res);
            ArrayList<sod_tax> sotd = _getOrderDetTax(x, bscon, ps, res);
            ArrayList<so_tax> sot = _getOrderTax(x, bscon, ps, res);
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new salesOrder(m, so, sod, sos, sotd, sot);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new salesOrder(m);
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
    return r;
    }
    
    public static so_mstr getOrderMstr(String[] x) {
        so_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from so_mstr where so_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
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
                    res.getString("so_rmks"), res.getString("so_type"), res.getString("so_taxcode"), res.getString("so_issourced"),
                    res.getString("so_confirm"), res.getString("so_plan"), res.getString("so_entrytype") );
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
    
    private static so_mstr _getOrderMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        so_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from so_mstr where so_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
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
                res.getString("so_rmks"), res.getString("so_type"), res.getString("so_taxcode"), res.getString("so_issourced"),
                res.getString("so_confirm"), res.getString("so_plan"), res.getString("so_entrytype"));
                }
            }
            return r;
    }
    
    
    public static ArrayList<sod_det> getOrderDet(String[] x) {
        ArrayList<sod_det> list = new ArrayList<sod_det>();
        sod_det r = null;
        String[] m = new String[2];
        String sql = "select * from sod_det where sod_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sod_det(m, res.getString("sod_nbr"), res.getString("sod_line"), res.getString("sod_item"),
                    res.getString("sod_custitem"), res.getString("sod_po"), res.getString("sod_ord_qty"), res.getString("sod_uom"), res.getString("sod_all_qty"),
                    res.getString("sod_listprice"), res.getString("sod_disc"), res.getString("sod_netprice"), res.getString("sod_ord_date"), res.getString("sod_due_date"),
                    res.getString("sod_shipped_qty"), res.getString("sod_status"), res.getString("sod_wh"), res.getString("sod_loc"), 
                    res.getString("sod_desc"), res.getString("sod_taxamt"), res.getString("sod_site"), res.getString("sod_bom"), res.getString("sod_ship") );
                    list.add(r);
                    }
                
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    private static ArrayList<sod_det> _getOrderDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<sod_det> list = new ArrayList<sod_det>();
        sod_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from sod_det where sod_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sod_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sod_det(m, res.getString("sod_nbr"), res.getString("sod_line"), res.getString("sod_item"),
                    res.getString("sod_custitem"), res.getString("sod_po"), res.getString("sod_ord_qty"), res.getString("sod_uom"), res.getString("sod_all_qty"),
                    res.getString("sod_listprice"), res.getString("sod_disc"), res.getString("sod_netprice"), res.getString("sod_ord_date"), res.getString("sod_due_date"),
                    res.getString("sod_shipped_qty"), res.getString("sod_status"), res.getString("sod_wh"), res.getString("sod_loc"), 
                    res.getString("sod_desc"), res.getString("sod_taxamt"), res.getString("sod_site"), res.getString("sod_bom"), res.getString("sod_ship") );
                    list.add(r);
                    }
            }
            return list;
    }
    
    
    public static ArrayList<sos_det> getOrderSOS(String[] x) {
        ArrayList<sos_det> list = new ArrayList<sos_det>();
        sos_det r = null;
        String[] m = new String[2];
        String sql = "select * from sos_det where sos_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sos_det(m, res.getString("sos_nbr"), res.getString("sos_desc"), res.getString("sos_type"),
                    res.getString("sos_amttype"), res.getString("sos_amt") );
                    list.add(r);
                    }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    private static ArrayList<sos_det> _getOrderSOS(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<sos_det> list = new ArrayList<sos_det>();
        sos_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from sos_det where sos_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sos_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sos_det(m, res.getString("sos_nbr"), res.getString("sos_desc"), res.getString("sos_type"),
                    res.getString("sos_amttype"), res.getString("sos_amt") );
                    list.add(r);
                }
            }
            return list;
    }
    
    
    
    public static so_tax getOrderTax(String[] x) {
        so_tax r = null;
        String[] m = new String[2];
        String sql = "select * from so_tax where sot_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new so_tax(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new so_tax(m, res.getString("sot_nbr"), res.getString("sot_desc"), res.getString("sot_percent"),
                    res.getString("sot_type"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new so_tax(m);
        }
        return r;
    }
    
    private static ArrayList<so_tax> _getOrderTax(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<so_tax> list = new ArrayList<so_tax>();
        so_tax r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from so_tax where sot_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new so_tax(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                     r = new so_tax(m, res.getString("sot_nbr"), res.getString("sot_desc"), res.getString("sot_percent"),
                    res.getString("sot_type"));
                    list.add(r);
                }
            }
            return list;
    }
    
    
    public static ArrayList<sod_tax> getOrderDetTax(String[] x) {
        ArrayList<sod_tax> list = new ArrayList<sod_tax>();
        sod_tax r = null;
        String[] m = new String[2];
        String sql = "select * from sod_tax where sodt_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sod_tax(m, res.getString("sodt_nbr"), res.getString("sodt_line"), res.getString("sodt_desc"),
                    res.getString("sodt_percent"), res.getString("sodt_type") );
                    list.add(r);
                    }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    private static ArrayList<sod_tax> _getOrderDetTax(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<sod_tax> list = new ArrayList<sod_tax>();
        sod_tax r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from sod_tax where sodt_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sod_tax(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sod_tax(m, res.getString("sodt_nbr"), res.getString("sodt_line"), res.getString("sodt_desc"),
                    res.getString("sodt_percent"), res.getString("sodt_type") );
                    list.add(r);
                }
            }
            return list;
    }
    
    
    private static int _addOrderDet(sod_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_det where sod_nbr = ? and sod_line = ?";
        String sqlInsert = "insert into sod_det (sod_nbr, sod_line, sod_item, sod_custitem, " 
                        + "sod_po, sod_ord_qty, sod_uom, sod_all_qty, " 
                        + "sod_listprice, sod_disc, sod_netprice, sod_ord_date, sod_due_date, " 
                        + "sod_shipped_qty, sod_status, sod_wh, sod_loc, "
                        + "sod_desc, sod_taxamt, sod_site, sod_bom, sod_ship ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sod_nbr);
          ps.setString(2, x.sod_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sod_nbr);
            ps.setString(2, x.sod_line);
            ps.setString(3, x.sod_item);
            ps.setString(4, x.sod_custitem);
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
            ps.setString(21, x.sod_bom);
            ps.setString(22, x.sod_ship);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
       
    private static int _addOrderSummaryDet(sos_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sos_det where sos_nbr = ? and sos_desc = ?";
        String sqlInsert = "insert into sos_det (sos_nbr, sos_desc, sos_type, " 
                        + "sos_amttype, sos_amt ) "
                        + " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sos_nbr);
          ps.setString(2, x.sos_desc);
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
        String sqlSelect = "select * from so_tax where sot_nbr = ? and sot_desc = ?";
        String sqlInsert = "insert into so_tax (sot_nbr, sot_desc, sot_percent, sot_type ) " 
                        + " values (?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sot_nbr);
          ps.setString(2, x.sot_desc);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sot_nbr);
            ps.setString(2, x.sot_desc);
            ps.setString(3, x.sot_percent);
            ps.setString(4, x.sot_type);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addServiceOrderMstr(sv_mstr x) {
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
            int rows = _addServiceOrderMstr(x, con, ps, res);  
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
    
    private static int _addServiceOrderMstr(sv_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sv_mstr where sv_nbr = ?";
        String sqlInsert = "insert into sv_mstr (sv_nbr, sv_cust, sv_ship, sv_site, " +
                          " sv_po, sv_due_date, sv_create_date, sv_type, sv_status, sv_rmks  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sv_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sv_nbr);
            ps.setString(2, x.sv_cust);
            ps.setString(3, x.sv_ship);
            ps.setString(4, x.sv_site);
            ps.setString(5, x.sv_po);
            ps.setString(6, x.sv_due_date);
            ps.setString(7, x.sv_create_date);
            ps.setString(8, x.sv_type);
            ps.setString(9, x.sv_status);
            ps.setString(10, x.sv_rmks);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addServiceOrderDet(svd_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from svd_det where svd_nbr = ? and svd_line = ?";
        String sqlInsert = "insert into svd_det (svd_line, svd_item, svd_type, svd_desc, " +
                           " svd_nbr, svd_qty, svd_uom, svd_netprice  ) "
                        + " values (?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.svd_nbr);
          ps.setString(2, x.svd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.svd_line);
            ps.setString(2, x.svd_item);
            ps.setString(3, x.svd_type);
            ps.setString(4, x.svd_desc);
            ps.setString(5, x.svd_nbr);
            ps.setString(6, x.svd_qty);
            ps.setString(7, x.svd_uom);
            ps.setString(8, x.svd_netprice);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addServiceOrderTransaction(ArrayList<svd_det> svd, sv_mstr sv) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addServiceOrderMstr(sv, bscon, ps, res);  
            for (svd_det z : svd) {
                _addServiceOrderDet(z, bscon, ps, res);
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
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
        
    public static String[] updateServiceOrderMstr(sv_mstr x) {
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
            int rows = _updateServiceOrderMstr(x, con, ps);  // add cms_det
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
   
    private static int _updateServiceOrderMstr(sv_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update sv_mstr set sv_cust = ?, sv_ship = ?, " +
                "sv_po = ?, sv_due_date = ?, sv_crew = ?, sv_rmks = ?, sv_status = ?  " +
                 " where sv_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(8, x.sv_nbr);
            ps.setString(1, x.sv_cust);
            ps.setString(2, x.sv_ship);
            ps.setString(3, x.sv_po);
            ps.setString(4, x.sv_due_date);
            ps.setString(5, x.sv_crew);
            ps.setString(6, x.sv_rmks);
            ps.setString(7, x.sv_status);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateServiceOrderDet(svd_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from svd_det where svd_nbr = ? and svd_line = ?";
        String sqlUpdate = "update svd_det set svd_item = ?, svd_qty = ?, " +
                 "svd_uom = ?, svd_netprice = ? " +
                 " where svd_nbr = ? and svd_line = ? ; ";
        String sqlInsert = "insert into svd_det (svd_line, svd_item, svd_type, svd_desc, " +
                           " svd_nbr, svd_qty, svd_uom, svd_netprice  ) "
                        + " values (?,?,?,?,?,?,?,?); ";  
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.svd_nbr);
        ps.setString(2, x.svd_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.svd_line);
            ps.setString(2, x.svd_item);
            ps.setString(3, x.svd_type);
            ps.setString(4, x.svd_desc);
            ps.setString(5, x.svd_nbr);
            ps.setString(6, x.svd_qty);
            ps.setString(7, x.svd_uom);
            ps.setString(8, x.svd_netprice);
            rows = ps.executeUpdate();
        } else {    // update
        ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(5, x.svd_nbr);
            ps.setString(6, x.svd_line);
            ps.setString(1, x.svd_item);
            ps.setString(2, x.svd_qty);
            ps.setString(3, x.svd_uom);
            ps.setString(4, x.svd_netprice);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    

    private static void _deleteServiceOrderLines(String x, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from svd_det where svd_nbr = ? and svd_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
    }
        
     // update order master.... multiple table transaction function
    public static String[] updateServiceOrderTransaction(String x, ArrayList<String> lines, ArrayList<svd_det> svd, sv_mstr sv) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            for (String line : lines) {
               _deleteServiceOrderLines(x, line, bscon, ps);  // discard unwanted lines
             }
            for (svd_det z : svd) {
                if (z.svd_status.equals(getGlobalProgTag("closed"))) {
                    continue;
                }
                _updateServiceOrderDet(z, bscon, ps, res);
            }
             _updateServiceOrderMstr(sv, bscon, ps);  // update so_mstr
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
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    public static String[] addPOSMstr(pos_mstr x) {
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
            int rows = _addPOSMstr(x, con, ps, res);  
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
        
    private static int _addPOSMstr(pos_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pos_mstr where pos_nbr = ?";
        String sqlInsert = "insert into pos_mstr (pos_nbr, pos_entrydate, pos_entrytime, "
                        + " pos_aracct, pos_arcc, pos_bank, pos_totqty, pos_totlines, "
                        + " pos_grossamt, pos_tottax, pos_totamt ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.pos_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.pos_nbr);
            ps.setString(2, x.pos_entrydate);
            ps.setString(3, x.pos_entrytime);
            ps.setString(4, x.pos_aracct);
            ps.setString(5, x.pos_arcc);
            ps.setString(6, x.pos_bank);
            ps.setString(7, x.pos_totqty);
            ps.setString(8, x.pos_totlines);
            ps.setString(9, x.pos_grossamt);
            ps.setString(10, x.pos_tottax);
            ps.setString(11, x.pos_totamt);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addPOSTransaction(ArrayList<pos_det> posd, pos_mstr pos) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            _addPOSMstr(pos, bscon, ps, res);  
            for (pos_det z : posd) {
                _addPOSDet(z, bscon, ps, res);
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
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
     
    private static int _addPOSDet(pos_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pos_det where posd_nbr = ?";
        String sqlInsert = "insert into pos_det (posd_nbr, posd_line, posd_item, "
                        + " posd_qty, posd_listprice, posd_disc, posd_netprice, posd_tax ) " 
                        + " values (?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.posd_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.posd_nbr);
            ps.setString(2, x.posd_line);
            ps.setString(3, x.posd_item);
            ps.setString(4, x.posd_qty);
            ps.setString(5, x.posd_listprice);
            ps.setString(6, x.posd_disc);
            ps.setString(7, x.posd_netprice);
            ps.setString(8, x.posd_tax);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
     
    
    public static String[] addUpdateORCtrl(order_ctrl x) {
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  order_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into order_ctrl (orc_autosource, orc_autoinvoice, orc_autoallocate, orc_custitem, " +
                            " orc_srvm_type, orc_srvm_item_default, orc_exceedqohu, orc_varchar) "
                        + " values (?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update order_ctrl set orc_autosource = ?, orc_autoinvoice = ?, orc_autoallocate = ?, orc_custitem = ?, " +
                            " orc_srvm_type = ?, orc_srvm_item_default = ?, orc_exceedqohu = ?, orc_varchar = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.orc_autosource);
            psi.setString(2, x.orc_autoinvoice);
            psi.setString(3, x.orc_autoallocate);
            psi.setString(4, x.orc_custitem);
            psi.setString(5, x.orc_srvm_type);
            psi.setString(6, x.orc_srvm_item_default);
            psi.setString(7, x.orc_exceedqohu);
            psi.setString(8, x.orc_varchar);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.orc_autosource);
            psu.setString(2, x.orc_autoinvoice);
            psu.setString(3, x.orc_autoallocate);
            psu.setString(4, x.orc_custitem);
            psu.setString(5, x.orc_srvm_type);
            psu.setString(6, x.orc_srvm_item_default);
            psu.setString(7, x.orc_exceedqohu);
            psu.setString(8, x.orc_varchar);
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
   
    public static order_ctrl getORCtrl(String[] x) {
        order_ctrl r = null;
        String[] m = new String[2];
        String sql = "select * from order_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new order_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new order_ctrl(m, 
                                res.getString("orc_autosource"),
                                res.getString("orc_autoinvoice"),
                                res.getString("orc_autoallocate"),
                                res.getString("orc_custitem"),
                                res.getString("orc_srvm_type"),
                                res.getString("orc_srvm_item_default"),
                                res.getString("orc_exceedqohu"),
                                res.getString("orc_varchar")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new order_ctrl(m);
        }
        return r;
    }
    
    
    public static String[] addQuoteTransaction(ArrayList<quo_det> qod, quo_mstr qo) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            _addQuoteMstr(qo, bscon, ps, res);  
            for (quo_det z : qod) {
                _addQuoteDet(z, bscon, ps, res);
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
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    public static String[] updateQuoteTransaction(String x, ArrayList<String> lines, ArrayList<quo_det> qod, quo_mstr qo) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            for (String line : lines) {
               _deleteQuoteLines(x, line, bscon, ps);  // discard unwanted lines
             }
            for (quo_det z : qod) {
                if (qo.quo_status().equals(getGlobalProgTag("closed"))) {
                    continue;
                }
                _updateQuoteDet(z, qo, bscon, ps, res);
            }
             _updateQuoteMstr(qo, bscon, ps);  
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
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
        
    private static int _addQuoteMstr(quo_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from quo_mstr where quo_nbr = ?";
        String sqlInsert = "insert into quo_mstr (quo_nbr, quo_cust, quo_ship," +
            "quo_site,  quo_date,  quo_expire,  quo_priceexpire,  quo_status, " +
            "quo_rmks,  quo_ref,  quo_type,  quo_taxcode,  quo_disccode," +
            "quo_groupcode,  quo_curr,  quo_approved,  quo_approver,  quo_varchar ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.quo_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.quo_nbr);
            ps.setString(2, x.quo_cust);
            ps.setString(3, x.quo_ship);
            ps.setString(4, x.quo_site);
            ps.setString(5, x.quo_date);
            ps.setString(6, x.quo_expire);
            ps.setString(7, x.quo_priceexpire);
            ps.setString(8, x.quo_status);
            ps.setString(9, x.quo_rmks);
            ps.setString(10, x.quo_ref);
            ps.setString(11, x.quo_type);
            ps.setString(12, x.quo_taxcode);
            ps.setString(13, x.quo_disccode);
            ps.setString(14, x.quo_groupcode);
            ps.setString(15, x.quo_curr);
            ps.setString(16, x.quo_approved);
            ps.setString(17, x.quo_approver);
            ps.setString(18, x.quo_varchar);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _updateQuoteMstr(quo_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update quo_mstr set quo_cust = ?, quo_ship = ?, " +
                "quo_site = ?, quo_date = ?, quo_expire = ?, quo_priceexpire = ?, quo_status = ?, quo_rmks = ?, " +
                "quo_ref = ?, quo_type = ?, quo_taxcode = ?, quo_disccode = ?, " +
                " quo_groupcode = ?, quo_curr = ?, quo_approved = ?, " +
                " quo_approver = ?, quo_varchar = ? " +
                 " where quo_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
            ps.setString(18, x.quo_nbr);
            ps.setString(1, x.quo_cust);
            ps.setString(2, x.quo_ship);
            ps.setString(3, x.quo_site);
            ps.setString(4, x.quo_date);
            ps.setString(5, x.quo_expire);
            ps.setString(6, x.quo_priceexpire);
            ps.setString(7, x.quo_status);
            ps.setString(8, x.quo_rmks);
            ps.setString(9, x.quo_ref);
            ps.setString(10, x.quo_type);
            ps.setString(11, x.quo_taxcode);
            ps.setString(12, x.quo_disccode);
            ps.setString(13, x.quo_groupcode);
            ps.setString(14, x.quo_curr);
            ps.setString(15, x.quo_approved);
            ps.setString(16, x.quo_approver);
            ps.setString(17, x.quo_varchar);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _addQuoteDet(quo_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from quo_det where quod_nbr = ? and quod_line = ?";
        String sqlInsert = "insert into quo_det (quod_nbr, quod_line, quod_item," +
                "quod_isinv,  quod_desc,  quod_pricetype,  quod_listprice,  quod_disc, " +
                "quod_netprice,  quod_qty,  quod_uom ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.quod_nbr);
          ps.setString(2, x.quod_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.quod_nbr);
            ps.setString(2, x.quod_line);
            ps.setString(3, x.quod_item);
            ps.setString(4, x.quod_isinv);
            ps.setString(5, x.quod_desc);
            ps.setString(6, x.quod_pricetype);
            ps.setString(7, x.quod_listprice);
            ps.setString(8, x.quod_disc);
            ps.setString(9, x.quod_netprice);
            ps.setString(10, x.quod_qty);
            ps.setString(11, x.quod_uom);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _updateQuoteDet(quo_det x, quo_mstr z, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from quo_det where quod_nbr = ? and quod_line = ?";
        String sqlUpdate = "update quo_det set quod_item = ?, quod_isinv = ?, " +
                "quod_desc = ?, quod_pricetype = ?, quod_listprice = ?, quod_disc = ?, " +
                " quod_netprice = ?, quod_qty = ?, quod_uom = ? " +
                 " where quod_nbr = ? and quod_line = ? ; ";
        String sqlInsert = "insert into quo_det (quod_nbr, quod_line, quod_item," +
                "quod_isinv,  quod_desc,  quod_pricetype,  quod_listprice,  quod_disc, " +
                "quod_netprice,  quod_qty,  quod_uom ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.quod_nbr);
        ps.setString(2, x.quod_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.quod_nbr);
            ps.setString(2, x.quod_line);
            ps.setString(3, x.quod_item);
            ps.setString(4, x.quod_isinv);
            ps.setString(5, x.quod_desc);
            ps.setString(6, x.quod_pricetype);
            ps.setString(7, x.quod_listprice);
            ps.setString(8, x.quod_disc);
            ps.setString(9, x.quod_netprice);
            ps.setString(10, x.quod_qty);
            ps.setString(11, x.quod_uom); 
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(10, x.quod_nbr);
            ps.setString(11, x.quod_line);
            ps.setString(1, x.quod_item);
            ps.setString(2, x.quod_isinv);
            ps.setString(3, x.quod_desc);
            ps.setString(4, x.quod_pricetype);
            ps.setString(5, x.quod_listprice);
            ps.setString(6, x.quod_disc);
            ps.setString(7, x.quod_netprice);
            ps.setString(8, x.quod_qty);
            ps.setString(9, x.quod_uom); 
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    public static String[] deleteQuoteLines(String x, ArrayList<String> lines) {
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
             for (String line : lines) {
               _deleteQuoteLines(x, line, con, ps);  // add cms_det
             }
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
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _deleteQuoteLines(String x, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from quo_det where quod_nbr = ? and quod_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
    }
    
    public static String[] deleteQuoteMstr(quo_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteQuoteMstr(x, con);  // add cms_det
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
    
    private static void _deleteQuoteMstr(quo_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from quo_mstr where quo_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.quo_nbr);
        ps.executeUpdate();
        sql = "delete from quo_det where quod_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.quo_nbr);
        ps.executeUpdate();
        ps.close();
    }
    
    public static quo_mstr getQuoteMstr(String[] x) {
        quo_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from quo_mstr where quo_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new quo_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new quo_mstr(m, 
                            res.getString("quo_nbr"), 
                            res.getString("quo_cust"),    
                            res.getString("quo_ship"),
                            res.getString("quo_site"),
                            res.getString("quo_date"),
                            res.getString("quo_expire"),  
                            res.getString("quo_priceexpire"),
                            res.getString("quo_status"),  
                            res.getString("quo_rmks"),
                            res.getString("quo_ref"),
                            res.getString("quo_type"),
                            res.getString("quo_taxcode"),
                            res.getString("quo_disccode"),
                            res.getString("quo_groupcode"),
                            res.getString("quo_curr"),
                            res.getString("quo_approved"),
                            res.getString("quo_approver"),
                            res.getString("quo_varchar")    
                        );
                    }
                }
            } 
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new quo_mstr(m);
        }
        return r;
    }
   
    public static ArrayList<quo_det> getQuoteDet(String code) {
        quo_det r = null;
        String[] m = new String[2];
        ArrayList<quo_det> list = new ArrayList<quo_det>();
        String sql = "select * from quo_det where quod_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new quo_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new quo_det(m, res.getString("quod_nbr"), 
                                res.getString("quod_line"), 
                                res.getString("quod_item"), 
                                res.getString("quod_isinv"), 
                                res.getString("quod_desc"),
                                res.getString("quod_pricetype"), 
                                res.getString("quod_listprice"),
                                res.getString("quod_disc"),
                                res.getString("quod_netprice"),
                                res.getString("quod_qty"),
                                res.getString("quod_uom"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new quo_det(m);
               list.add(r);
        }
        return list;
    }
   
    
    public static ArrayList<String> getQuoteLines(String nbr) {
        ArrayList<String> lines = new ArrayList<String>();
        try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("SELECT quod_line from quo_det " +
                   " where quod_nbr = " + "'" + nbr + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("quod_line"));
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
    
    
    // miscellaneous SQL queries
    
    public static ArrayList<String[]> getSalesOrderInit() {
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
            
            
            res = st.executeQuery("select wh_id from wh_mstr order by wh_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "warehouses";
               s[1] = res.getString("wh_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select loc_loc from loc_mstr order by loc_loc;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "locations";
               s[1] = res.getString("loc_loc");
               lines.add(s);
            }
            
            res = st.executeQuery("select cur_id from cur_mstr ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "currencies";
               s[1] = res.getString("cur_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select uom_id from uom_mstr order by uom_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "uoms";
               s[1] = res.getString("uom_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "customers";
               s[1] = res.getString("cm_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select tax_code from tax_mstr order by tax_code  ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "taxcodes";
               s[1] = res.getString("tax_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "states";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'country' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "countries";
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
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'orderstatus' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "statuses";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select it_item from item_mstr where it_site = " + "'" + defaultsite + "'" + " order by it_item ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "items";
               s[1] = res.getString("it_item");
               lines.add(s);
            }
            
            
            
            res = st.executeQuery("select orc_custitem, orc_autoallocate from order_ctrl;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "allocate";
               s[1] = res.getString("orc_autoallocate");
               lines.add(s);
               s = new String[2];
               s[0] = "custitemonly";
               s[1] = res.getString("orc_custitem");
               lines.add(s);
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
        return lines;
    }
    
    
    public static String[] getSOMstrHeaderEDI(String order) {
        String[] x = new String[12];
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
            
         // so, po, cust, ship, site, type, orddate, duedate, shipvia, rmks, cur, status
           res = st.executeQuery("SELECT * from so_mstr " +
                   " where so_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          x[0] = res.getString("so_nbr");
                          x[1] = res.getString("so_po");
                          x[2] = res.getString("so_cust");
                          x[3] = res.getString("so_ship");
                          x[4] = res.getString("so_site");
                          x[5] = res.getString("so_type");
                          x[6] = res.getString("so_ord_date");
                          x[7] = res.getString("so_due_date");
                          x[8] = res.getString("so_shipvia");
                          x[9] = res.getString("so_rmks");
                          x[10] = res.getString("so_curr");
                          x[11] = res.getString("so_status");
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
        return x;
    }
    
    public static ArrayList<String[]> getSOMstrdetailsEDI(String order) {
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
            
           // line, item, custitem, qty, price, uom, desc, custline, custuom, custprice
           res = st.executeQuery("SELECT * from sod_det " +
                   " where sod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          String[] s = new String[10];
                          for (int z = 0; z < 10; z++) {
                          s[z] = "";
                          }
                          s[0] = res.getString("sod_line");
                          s[1] = res.getString("sod_item");
                          s[2] = res.getString("sod_custitem");
                          s[3] = res.getString("sod_ord_qty");
                          s[4] = res.getString("sod_netprice");
                          s[5] = res.getString("sod_uom");
                          s[6] = res.getString("sod_desc");
                          s[7] = res.getString("sod_custline");
                          s[8] = res.getString("sod_custuom");
                          s[9] = res.getString("sod_custprice");
                          lines.add(s);
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
        return lines;
    }
    
    public static String getSOOrderBillto(String order) {
         String billto = "";
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
            
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);



                  res = st.executeQuery("select so_cust from so_mstr where so_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    billto = res.getString("so_cust");
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
         return billto;
     }

    
    public static Double getOrderItemAllocatedQty(String item, String site) {
       Double qty = 0.00;
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
            

           res = st.executeQuery("SELECT  sum(case when sod_all_qty = '' then 0 else (sod_all_qty - sod_shipped_qty) end) as allqty  " +
                                    " FROM  sod_det inner join so_mstr on so_nbr = sod_nbr  " +
                                    " where sod_item = " + "'" + item + "'" + 
                                    " AND so_status <> " + "'" + getGlobalProgTag("closed") + "'" +
                                    " AND so_site = " + "'" + site + "'" +          
                                    " group by sod_item ;");

                                    while (res.next()) {
                                    qty = res.getDouble("allqty");
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
    return qty;

    }

    public static double getOrderTotalTax(String nbr) {
       double tax = 0;
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
            
            double ordertotal = 0;
            
            res = st.executeQuery("SELECT  sum(sod_netprice * sod_ord_qty) as mytotal  " +
                                    " FROM  sod_det  " +
                                    " where sod_nbr = " + "'" + nbr + "'" +       
                                    ";");
                while (res.next()) {
                    ordertotal += res.getDouble("mytotal");
                }
            
            res = st.executeQuery("SELECT * " +
                                    " FROM  sos_det  " +
                                    " where sos_nbr = " + "'" + nbr + "'" +
                                    " and sos_type = 'tax' " +        
                                    " ;");

                double sosamt = 0;
                while (res.next()) {
                    sosamt = res.getDouble("sos_amt");
                    if (res.getString("sos_amttype").equals("percent")) {
                        if (sosamt > 0)
                        tax += (ordertotal * (sosamt / 100)); 
                    } else {
                       tax += sosamt;
                    }
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
    return tax;

    }
    
    public static double getOrderTotal(String nbr) {
       double tax = 0;
       double disc = 0;
       double charge = 0;
       double ordertotal = 0;
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
            res = st.executeQuery("SELECT  sum(sod_netprice * sod_ord_qty) as mytotal  " +
                                    " FROM  sod_det  " +
                                    " where sod_nbr = " + "'" + nbr + "'" +       
                                    ";");
                while (res.next()) {
                    ordertotal += res.getDouble("mytotal");
                }
            
            res = st.executeQuery("SELECT * " +
                                    " FROM  sos_det  " +
                                    " where sos_nbr = " + "'" + nbr + "'" +
                                    " and sos_type = 'tax' " +        
                                    " ;");

                double sosamt = 0;
                while (res.next()) {
                    sosamt = res.getDouble("sos_amt");
                    if (res.getString("sos_amttype").equals("percent")) {
                        if (sosamt > 0)
                        tax += (ordertotal * (sosamt / 100)); 
                    } else {
                       tax += sosamt;
                    }
                }
            
                res = st.executeQuery("SELECT * " +
                                    " FROM  sos_det  " +
                                    " where sos_nbr = " + "'" + nbr + "'" +
                                    " and sos_type <> 'tax' " +        
                                    " ;");

                sosamt = 0;
                while (res.next()) {
                    sosamt = res.getDouble("sos_amt");
                    if (res.getString("sos_type").equals("charge")) {
                       charge += sosamt; 
                    }
                    if (res.getString("sos_type").equals("discount")) {
                       if (sosamt > 0)
                        disc += (ordertotal * (sosamt / 100)); 
                    }
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
    return ordertotal + charge + tax;

    }
    
    public static String getOrderItem(String order, String line) {
        String item = "";
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
                res = st.executeQuery("select sod_item from sod_det where sod_nbr = " + "'" + order + "'" + 
                        " and sod_line = " + "'" + line + "'" + ";");
                while (res.next()) {
                    item = res.getString("sod_item");
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
             return item;
    }
    
    public static ArrayList<String> getOrderLines(String order) {
        ArrayList<String> lines = new ArrayList<String>();
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

           res = st.executeQuery("SELECT sod_line from sod_det " +
                   " where sod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("sod_line"));
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
        return lines;
    }
    
    public static ArrayList<String> getServiceOrderLines(String order) {
        ArrayList<String> lines = new ArrayList<String>();
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
           

           res = st.executeQuery("SELECT svd_line from svd_det " +
                   " where svd_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("svd_line"));
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
        return lines;
    }
         
    public static String getOrderCurrency(String order) {
        String curr = "";
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
    
    public static ArrayList getOpenOrdersList() {
   ArrayList mylist = new ArrayList() ;

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

            res = st.executeQuery("select so_nbr from so_mstr where so_status = " + "'" + getGlobalProgTag("open") + "'" + " or so_status = " + "'" + getGlobalProgTag("commit") + "'" + " or so_status = " + "'" + getGlobalProgTag("backorder") + "'" + " ;");
                   while (res.next()) {
                      mylist.add(res.getString(("so_nbr")));
                   }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return mylist;

}

    public record salesOrder(String[] m, so_mstr so, ArrayList<sod_det> sod,
        ArrayList<sos_det> sos, ArrayList<sod_tax> sodtax, ArrayList<so_tax> sotax) {
        public salesOrder(String[] m) {
            this (m, null, null, null, null, null);
        }
    }
    
    public record so_mstr(String[] m, String so_nbr, String so_cust, String so_ship, String so_site,
    String so_curr, String so_shipvia, String so_wh, String so_po, String so_due_date,
    String so_ord_date, String so_create_date, String so_userid, String so_status, String so_isallocated,
    String so_terms, String so_ar_acct, String so_ar_cc, String so_rmks, String so_type, String so_taxcode,
    String so_issourced, String so_confirm, String so_plan, String so_entrytype) {
        public so_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", ""
                    );
        }
    }
    
                              
    public record sod_det(String[] m, String sod_nbr, String sod_line, String sod_item, String sod_custitem, 
        String sod_po, String sod_ord_qty, String sod_uom, String sod_all_qty, 
        String sod_listprice, String sod_disc, String sod_netprice, String sod_ord_date, 
        String sod_due_date, String sod_shipped_qty, String sod_status, String sod_wh, 
        String sod_loc, String sod_desc, String sod_taxamt, String sod_site, String sod_bom, String sod_ship) {
        public sod_det(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "" );
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
    
    
    public record sv_mstr(String[] m, String sv_nbr, String sv_cust, String sv_ship, String sv_po,
        String sv_crew, String sv_create_date, String sv_due_date, String sv_rmks,
    String sv_status, String sv_issched, String sv_userid, String sv_type,
    String sv_char1, String sv_char2, String sv_char3, String sv_terms, 
    String sv_curr, String sv_ar_acct, String sv_ar_cc,String sv_onhold, 
    String sv_taxcode, String sv_site) {
        public sv_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", ""
                    );
        }
    }
    
    public record svd_det(String[] m, String svd_nbr, String svd_line, String svd_uom, 
        String svd_item, String svd_desc, String svd_type, String svd_custitem, 
        String svd_qty, String svd_completed_hrs, String svd_po,  String svd_ord_date, 
        String svd_due_date, String svd_create_date, String svd_char1, String svd_char2, String svd_char3,
        String svd_status, String svd_listprice, String svd_netprice, String svd_disc,  
        String svd_taxamt, String svd_taxcode, String svd_site) {
        public svd_det(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "");
        }
    }
    
    public record pos_mstr(String[] m, String pos_nbr, String pos_entrydate, String pos_entrytime,
        String pos_aracct, String pos_arcc, String pos_bank, String pos_totqty, 
        String pos_totlines, String pos_grossamt, String pos_tottax, String pos_totamt)  {
        public pos_mstr(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                     "");
        }
    }

    public record pos_det(String[] m, String posd_nbr, String posd_line, String posd_item, 
        String posd_qty, String posd_listprice, String posd_disc, 
        String posd_netprice, String posd_tax)  {
        public pos_det(String[] m) {
            this (m, "", "", "", "", "", "", "", "");
        }
    }

    public record order_ctrl(String[] m, String orc_autosource, String orc_autoinvoice, 
        String orc_autoallocate, String orc_custitem, String orc_srvm_type, 
        String orc_srvm_item_default, String orc_exceedqohu, String orc_varchar)  {
        public order_ctrl(String[] m) {
            this (m, "", "", "", "", "", "", "", "");
        }
    }
    
    public record quo_mstr(String[] m, String quo_nbr, String quo_cust, String quo_ship,
        String quo_site, String quo_date, String quo_expire, String quo_priceexpire, String quo_status, 
        String quo_rmks, String quo_ref, String quo_type, String quo_taxcode, String quo_disccode,
        String quo_groupcode, String quo_curr, String quo_approved, String quo_approver, String quo_varchar 
        )  {
        public quo_mstr(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                     "", "", "", "", "", "", "", "");
        }
    }
    

    public record quo_det(String[] m, String quod_nbr, String quod_line, String quod_item,
        String quod_isinv, String quod_desc, String quod_pricetype, String quod_listprice, String quod_disc, 
        String quod_netprice, String quod_qty, String quod_uom 
        )  {
        public quo_det(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                     "");
        }
    }
    
}
