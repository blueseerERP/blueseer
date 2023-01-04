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
package com.blueseer.pur;

import com.blueseer.ord.*;
import com.blueseer.inv.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author terryva
 */
public class purData {
    
      // add order master table only
    public static String[] addPOMstr(po_mstr x) {
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
            int rows = _addPOMstr(x, con, ps, res);  
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
        
    private static int _addPOMstr(po_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from po_mstr where po_nbr = ?";
        String sqlInsert = "insert into po_mstr (po_nbr, po_vend, po_site, po_type, " 
                        + " po_curr, po_buyer, po_due_date, "
                        + " po_ord_date, po_userid, po_status,"
                        + " po_terms, po_ap_acct, po_ap_cc, po_rmks, po_ship ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.po_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.po_nbr);
            ps.setString(2, x.po_vend);
            ps.setString(3, x.po_site);
            ps.setString(4, x.po_type);
            ps.setString(5, x.po_curr);
            ps.setString(6, x.po_buyer);
            ps.setString(7, x.po_due_date);
            ps.setString(8, x.po_ord_date);
            ps.setString(9, x.po_userid);
            ps.setString(10, x.po_status);
            ps.setString(11, x.po_terms);
            ps.setString(12, x.po_ap_acct);
            ps.setString(13, x.po_ap_cc);
            ps.setString(14, x.po_rmks);
            ps.setString(15, x.po_ship);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
            
    public static String[] addPOTransaction(ArrayList<pod_mstr> pod, po_addr poa, po_mstr po, ArrayList<po_meta> pom) {
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
            _addPOMstr(po, bscon, ps, res);  
            _addPOAddr(poa, bscon, ps, res, true);
            for (pod_mstr z : pod) {
                _addPODet(z, bscon, ps, res);
            }
            if (pom != null) {
                for (po_meta z : pom) { 
                    _addPOMeta(z, bscon, ps, res);
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
    
    private static int _addPOMeta(po_meta x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from po_meta where pom_nbr = ? and pom_desc = ?";
        String sqlInsert = "insert into po_meta (pom_nbr, pom_desc, pom_type, " 
                        + "pom_amttype, pom_amt ) "
                        + " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.pom_nbr);
          ps.setString(2, x.pom_desc);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.pom_nbr);
            ps.setString(2, x.pom_desc);
            ps.setString(3, x.pom_type);
            ps.setString(4, x.pom_amttype);
            ps.setString(5, x.pom_amt);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    
    public static String[] updatePOMstr(po_mstr x) {
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
            int rows = _updatePOMstr(x, con, ps); 
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
   
    private static int _updatePOMstr(po_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update po_mstr set po_status = ?, po_rmks = ?,  " +
                "po_site = ?, po_buyer = ?, po_due_date = ?, po_shipvia = ?, po_ship = ?, po_edistatus = ?, " +
                "po_confirm = ? " +
                 " where po_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(10, x.po_nbr);
            ps.setString(1, x.po_status);
            ps.setString(2, x.po_rmks);
            ps.setString(3, x.po_site);
            ps.setString(4, x.po_buyer);
            ps.setString(5, x.po_due_date);
            ps.setString(6, x.po_shipvia);
            ps.setString(7, x.po_ship);
            ps.setString(8, x.po_edistatus); 
            ps.setString(9, x.po_confirm); 
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updatePODet(pod_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pod_mstr where pod_nbr = ? and pod_line = ?";
        String sqlUpdate = "update pod_mstr set pod_item = ?, pod_venditem = ?, " +
                " pod_ord_qty = ?, pod_uom = ?, " +
                " pod_listprice = ?, pod_disc = ?, pod_netprice = ?,  " +
                " pod_due_date = ?, pod_status = ?, " +
                " pod_site = ?, pod_desc = ?, pod_ship = ? " +
                 " where pod_nbr = ? and pod_line = ? ; ";
        String sqlInsert = "insert into pod_mstr (pod_nbr, pod_line, pod_item, pod_venditem, "
                            + " pod_ord_qty, pod_uom, pod_listprice, pod_disc, "
                            + " pod_netprice, pod_ord_date, pod_due_date, "
                            + "pod_rcvd_qty, pod_status, pod_site, pod_desc, pod_ship) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.pod_nbr);
        ps.setString(2, x.pod_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.pod_nbr);
            ps.setString(2, x.pod_line);
            ps.setString(3, x.pod_item);
            ps.setString(4, x.pod_venditem);
            ps.setString(5, x.pod_ord_qty);
            ps.setString(6, x.pod_uom);
            ps.setString(7, x.pod_listprice);
            ps.setString(8, x.pod_disc);
            ps.setString(9, x.pod_netprice);
            ps.setString(10, x.pod_ord_date);
            ps.setString(11, x.pod_due_date);
            ps.setString(12, x.pod_rcvd_qty);
            ps.setString(13, x.pod_status);
            ps.setString(14, x.pod_site);
            ps.setString(15, x.pod_desc);
            ps.setString(16, x.pod_ship);
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(13, x.pod_nbr);
            ps.setString(14, x.pod_line);
            ps.setString(1, x.pod_item);
            ps.setString(2, x.pod_venditem);
            ps.setString(3, x.pod_ord_qty);
            ps.setString(4, x.pod_uom);
            ps.setString(5, x.pod_listprice);
            ps.setString(6, x.pod_disc);
            ps.setString(7, x.pod_netprice);
            ps.setString(8, x.pod_due_date);
            ps.setString(9, x.pod_status);
            ps.setString(10, x.pod_site);
            ps.setString(11, x.pod_desc);
            ps.setString(12, x.pod_ship);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
        
    public static String[] updatePOTransaction(String x, ArrayList<String> lines, ArrayList<pod_mstr> pod, po_addr poa, po_mstr po, ArrayList<po_meta> pom) {
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
               _deletePOLines(x, line, bscon);  // discard unwanted lines
             }
            for (pod_mstr z : pod) {
                if (z.pod_status.equals(getGlobalProgTag("closed"))) {
                    continue;
                }
                _updatePODet(z, bscon, ps, res);
            }
             _deletePOMeta(po.po_nbr, bscon);
            for (po_meta z : pom) {
                _addPOMeta(z, bscon, ps, res);
            }
             _updatePOMstr(po, bscon, ps);  // update po_mstr
             _updatePOAddr(poa, bscon, ps);  // update po_addr
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
    
    private static void _deletePOMeta(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from po_meta where pom_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    
    public static String[] deletePOMstr(po_mstr x) {
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
            _deletePOMstr(x, con);  // add po_addr
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
    
    public static String[] deletePOLines(String x, ArrayList<String> lines) {
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
             for (String line : lines) {
               _deletePOLines(x, line, con);  // add po_addr
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
    
    private static void _deletePOLines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from pod_mstr where pod_nbr = ? and pod_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        ps.close();
    }
    
    
    private static void _deletePOMstr(po_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from po_mstr where po_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.po_nbr);
        ps.executeUpdate();
        sql = "delete from pod_mstr where pod_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.po_nbr);
        ps.executeUpdate();
        ps.close();
    }
    
    public static purchaseOrder getPOMstrSet(String[] x ) {
        purchaseOrder r = null;
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
            po_mstr po = _getPOMstr(x, bscon, ps, res);
            po_addr poa = _getPOAddr(new String[]{po.po_nbr, po.po_ship}, bscon, ps, res);
            ArrayList<pod_mstr> pod = _getPODet(x, bscon, ps, res);
            ArrayList<po_meta> pom = _getPOM(x, bscon, ps, res);
           
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new purchaseOrder(m, po, poa, pod, pom);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new purchaseOrder(m);
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
    
    
    public static po_mstr getPOMstr(String[] x) {
        po_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from po_mstr where po_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new po_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new po_mstr(m, res.getString("po_nbr"), res.getString("po_vend"), 
                        res.getString("po_ord_date"), res.getString("po_due_date"), res.getString("po_rmks"), 
                        res.getString("po_shipvia"), res.getString("po_status"), res.getString("po_userid"), 
                        res.getString("po_type"), res.getString("po_curr"), res.getString("po_terms"), 
                        res.getString("po_site"), res.getString("po_buyer"), res.getString("po_ap_acct"), 
                        res.getString("po_ap_cc"), res.getString("po_ship"), res.getString("po_edistatus"),
                        res.getString("po_confirm"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new po_mstr(m);
        }
        return r;
    }
    
    private static po_mstr _getPOMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        po_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from po_mstr where po_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new po_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new po_mstr(m, res.getString("po_nbr"), res.getString("po_vend"), 
                        res.getString("po_ord_date"), res.getString("po_due_date"), res.getString("po_rmks"), 
                        res.getString("po_shipvia"), res.getString("po_status"), res.getString("po_userid"), 
                        res.getString("po_type"), res.getString("po_curr"), res.getString("po_terms"), 
                        res.getString("po_site"), res.getString("po_buyer"), res.getString("po_ap_acct"), 
                        res.getString("po_ap_cc"), res.getString("po_ship"), res.getString("po_edistatus"),
                        res.getString("po_confirm"));
                }
            }
            return r;
    }
       
    public static ArrayList<pod_mstr> getPODet(String[] x) {
        ArrayList<pod_mstr> list = new ArrayList<pod_mstr>();
        pod_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from pod_mstr where pod_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new pod_mstr(m, res.getString("pod_nbr"), res.getString("pod_line"), res.getString("pod_item"),
                    res.getString("pod_venditem"), res.getString("pod_ord_qty"), res.getString("pod_rcvd_qty"), 
                    res.getString("pod_netprice"), res.getString("pod_disc"),res.getString("pod_listprice"), 
                    res.getString("pod_due_date"), res.getString("pod_status"), res.getString("pod_site"), 
                    res.getString("pod_ord_date"), res.getString("pod_uom"), res.getString("pod_desc"),
                    res.getString("pod_ship") );
                    list.add(r);
                    }
                
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
        
    private static ArrayList<pod_mstr> _getPODet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<pod_mstr> list = new ArrayList<pod_mstr>();
        pod_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from pod_mstr where pod_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new pod_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new pod_mstr(m, res.getString("pod_nbr"), res.getString("pod_line"), res.getString("pod_item"),
                    res.getString("pod_venditem"), res.getString("pod_ord_qty"), res.getString("pod_rcvd_qty"), 
                    res.getString("pod_netprice"), res.getString("pod_disc"),res.getString("pod_listprice"), 
                    res.getString("pod_due_date"), res.getString("pod_status"), res.getString("pod_site"), 
                    res.getString("pod_ord_date"), res.getString("pod_uom"), res.getString("pod_desc"),
                    res.getString("pod_ship") );
                    list.add(r);
                    }
            }
            return list;
    }
    
    private static int _addPODet(pod_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pod_mstr where pod_nbr = ? and pod_line = ?";
        String sqlInsert = "insert into pod_mstr (pod_nbr, pod_line, pod_item, pod_venditem, "
                            + " pod_ord_qty, pod_uom, pod_listprice, pod_disc, "
                            + " pod_netprice, pod_ord_date, pod_due_date, "
                            + "pod_rcvd_qty, pod_status, pod_site, pod_desc, pod_ship) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.pod_nbr);
          ps.setString(2, x.pod_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.pod_nbr);
            ps.setString(2, x.pod_line);
            ps.setString(3, x.pod_item);
            ps.setString(4, x.pod_venditem);
            ps.setString(5, x.pod_ord_qty);
            ps.setString(6, x.pod_uom);
            ps.setString(7, x.pod_listprice);
            ps.setString(8, x.pod_disc);
            ps.setString(9, x.pod_netprice);
            ps.setString(10, x.pod_ord_date);
            ps.setString(11, x.pod_due_date);
            ps.setString(12, x.pod_rcvd_qty);
            ps.setString(13, x.pod_status);
            ps.setString(14, x.pod_site); 
            ps.setString(15, x.pod_desc);
            ps.setString(16, x.pod_ship);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
       
    
    public static String[] addUpdatePOCtrl(po_ctrl x) {
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  po_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into po_ctrl (poc_rcpt_acct, poc_rcpt_cc, poc_venditem, poc_rawonly) "
                        + " values (?,?,?,?); "; 
        String sqlUpdate = "update po_ctrl set poc_rcpt_acct = ?, poc_rcpt_cc = ?, poc_venditem = ?, poc_rawonly = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.poc_rcpt_acct);
            psi.setString(2, x.poc_rcpt_cc);
            psi.setString(3, x.poc_venditem);
            psi.setString(4, x.poc_rawonly);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.poc_rcpt_acct);
            psu.setString(2, x.poc_rcpt_cc);
            psu.setString(3, x.poc_venditem);
            psu.setString(4, x.poc_rawonly);
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
   
    public static po_ctrl getPOCtrl(String[] x) {
        po_ctrl r = null;
        String[] m = new String[2];
        String sql = "select * from po_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new po_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new po_ctrl(m, 
                                res.getString("poc_rcpt_acct"),
                                res.getString("poc_rcpt_cc"),
                                res.getString("poc_venditem"),
                                res.getString("poc_rawonly")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new po_ctrl(m);
        }
        return r;
    }
    
    public static String[] addPOAddr(po_addr x) {
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
            _addPOAddr(x, con, ps, res, false);  // add po_addr
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
    
    private static void _addPOAddr(po_addr x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        if (x == null) return;
        String sqlSelect = "select * from po_addr where poa_code = ? and poa_shipto = ?";
        String sqlInsert = "insert into po_addr (poa_code, poa_shipto, poa_name, poa_line1, poa_line2, " 
                        + "poa_line3, poa_city, poa_state, poa_zip, poa_country, poa_contact, "
                        + "poa_phone, poa_email ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update po_addr set " 
                + " poa_name = ?, poa_line1 = ?, poa_line2 = ?, "
                + "poa_line3 = ?, poa_city = ?, poa_state = ?, poa_zip = ?, "
                + "poa_country = ?, poa_contact = ?, poa_phone = ?, poa_email = ? "
                + " where poa_code = ? and poa_shipto = ? ; ";
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.poa_code);
            ps.setString(2, x.poa_shipto);
            res = ps.executeQuery();
             if (! res.isBeforeFirst()) {
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, x.poa_code);
            ps.setString(2, x.poa_shipto);
            ps.setString(3, x.poa_name);
            ps.setString(4, x.poa_line1);
            ps.setString(5, x.poa_line2);
            ps.setString(6, x.poa_line3);
            ps.setString(7, x.poa_city);
            ps.setString(8, x.poa_state);
            ps.setString(9, x.poa_zip);
            ps.setString(10, x.poa_country);
            ps.setString(11, x.poa_contact);
            ps.setString(12, x.poa_phone);
            ps.setString(13, x.poa_email);
            int rows = ps.executeUpdate();
            } else {
                 if (addupdate) {
                    ps = con.prepareStatement(sqlUpdate); 
                    ps.setString(12, x.poa_code);
                    ps.setString(13, x.poa_shipto);
                    ps.setString(1, x.poa_name);
                    ps.setString(2, x.poa_line1);
                    ps.setString(3, x.poa_line2);
                    ps.setString(4, x.poa_line3);
                    ps.setString(5, x.poa_city);
                    ps.setString(6, x.poa_state);
                    ps.setString(7, x.poa_zip);
                    ps.setString(8, x.poa_country);
                    ps.setString(9, x.poa_contact);
                    ps.setString(10, x.poa_phone);
                    ps.setString(11, x.poa_email);
                    ps.executeUpdate();    
                 }
             }
    }
    
    public static String[] updatePOAddr(po_addr x) {
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
            _updatePOAddr(x, con, ps);  // add po_addr
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
    
    private static int _updatePOAddr(po_addr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update po_addr set " 
                + " poa_name = ?, poa_line1 = ?, poa_line2 = ?, "
                + "poa_line3 = ?, poa_city = ?, poa_state = ?, poa_zip = ?, "
                + "poa_country = ?, poa_contact = ?, poa_phone = ?, poa_email = ? "
                + " where poa_code = ? and poa_shipto = ? ; ";
       ps = con.prepareStatement(sql);
        ps.setString(12, x.poa_code);
        ps.setString(13, x.poa_shipto);
            ps.setString(1, x.poa_name);
            ps.setString(2, x.poa_line1);
            ps.setString(3, x.poa_line2);
            ps.setString(4, x.poa_line3);
            ps.setString(5, x.poa_city);
            ps.setString(6, x.poa_state);
            ps.setString(7, x.poa_zip);
            ps.setString(8, x.poa_country);
            ps.setString(9, x.poa_contact);
            ps.setString(10, x.poa_phone);
            ps.setString(11, x.poa_email);
            rows = ps.executeUpdate();
        
       
        return rows;
    }
         
    public static String[] deletePOAddr(po_addr x) {
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
            _deletePOAddr(x.poa_code, x.poa_shipto, con, ps, res);  // add po_addr
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
    
    private static void _deletePOAddr(String x, String y, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from po_addr where poa_code = ? and poa_shipto = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    public static po_addr getPOAddr(String shipto, String code) {
        po_addr r = null;
        String[] m = new String[2];
        String sql = "select * from po_addr where poa_shipto = ? and poa_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, shipto);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new po_addr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new po_addr(m, res.getString("poa_code"), res.getString("poa_shipto"), res.getString("poa_name"), res.getString("poa_line1"), res.getString("poa_line2"),
                    res.getString("poa_line3"), res.getString("poa_city"), res.getString("poa_state"), res.getString("poa_zip"),
                    res.getString("poa_country"), res.getString("poa_contact"), res.getString("poa_phone"),
                    res.getString("poa_email")
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new po_addr(m);
        }
        return r;
    }
    
    private static po_addr _getPOAddr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        po_addr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from po_addr where poa_code = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new po_addr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                      r = new po_addr(m, res.getString("poa_code"), res.getString("poa_shipto"), res.getString("poa_name"), res.getString("poa_line1"), res.getString("poa_line2"),
                    res.getString("poa_line3"), res.getString("poa_city"), res.getString("poa_state"), res.getString("poa_zip"),
                    res.getString("poa_country"), res.getString("poa_contact"), res.getString("poa_phone"),
                    res.getString("poa_email")
                    );
                }
            }
            return r;
    }
        
    public static ArrayList<po_addr> getPOAddr(String code) {
        po_addr r = null;
        String[] m = new String[2];
        ArrayList<po_addr> list = new ArrayList<po_addr>();
        String sql = "select * from po_addr where poa_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new po_addr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new po_addr(m, res.getString("poa_code"), res.getString("poa_shipto"), res.getString("poa_name"), res.getString("poa_line1"), res.getString("poa_line2"),
                    res.getString("poa_line3"), res.getString("poa_city"), res.getString("poa_state"), res.getString("poa_zip"),
                    res.getString("poa_country"), res.getString("poa_contact"), res.getString("poa_phone"),
                    res.getString("poa_email") 
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new po_addr(m);
               list.add(r);
        }
        return list;
    }
    
    private static ArrayList<po_meta> _getPOM(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<po_meta> list = new ArrayList<po_meta>();
        po_meta r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from po_meta where pom_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new po_meta(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new po_meta(m, res.getString("pom_nbr"), res.getString("pom_desc"), res.getString("pom_type"),
                    res.getString("pom_amttype"), res.getString("pom_amt"), res.getString("pom_key"), res.getString("pom_value") );
                    list.add(r);
                }
            }
            return list;
    }
    
    
    
    
    
    // miscellaneous SQL queries
    public static ArrayList<String[]> getPurchaseOrderInit() {
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
            
            res = st.executeQuery("select uom_id from uom_mstr order by uom_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "uoms";
               s[1] = res.getString("uom_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select vd_addr from vd_mstr order by vd_addr ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "vendors";
               s[1] = res.getString("vd_addr");
               lines.add(s);
            }
           
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "states";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select car_code from car_mstr where car_type = 'carrier' order by car_code;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "carriers";
               s[1] = res.getString("car_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'orderstatus' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "statuses";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
           
            String itemclass = "";
            res = st.executeQuery("select poc_venditem, poc_rawonly from po_ctrl;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "venditemonly";
               s[1] = res.getString("poc_venditem");
               lines.add(s);
               s = new String[2];
               s[0] = "rawitemonly";
               s[1] = res.getString("poc_rawonly");
               lines.add(s);
               if (res.getString("poc_rawonly").equals("1")) {
                   itemclass = "P";
               }
            }
            
            
            if (itemclass.isBlank()) {
               res = st.executeQuery("select it_item from item_mstr where it_site = " + "'" + defaultsite + "'" + " order by it_item ;");
            } else {
               res = st.executeQuery("select it_item from item_mstr where it_site = " + "'" + defaultsite + "'" + " and it_code = 'P' " + " order by it_item ;"); 
            }
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "items";
               s[1] = res.getString("it_item");
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
        
    public static String getPOVendor(String po) {
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
           
            res = st.executeQuery("select po_vend from po_mstr where po_nbr = " + "'" + po + "'" +";");
            while (res.next()) {
                x = res.getString("po_vend");
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

    public static ArrayList<String> getPOLines(String order) {
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
            

           res = st.executeQuery("SELECT pod_line from pod_mstr " +
                   " where pod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("pod_line"));
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
    
    public static String[] getPOMstrHeaderEDI(String order) {
        String[] x = new String[10];
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
            
         // po, vend, ship, site, type, orddate, duedate, shipvia, rmks, cur
           res = st.executeQuery("SELECT * from po_mstr " +
                   " where po_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          x[0] = res.getString("po_nbr");
                          x[1] = res.getString("po_vend");
                          x[2] = res.getString("po_ship");
                          x[3] = res.getString("po_site");
                          x[4] = res.getString("po_type");
                          x[5] = res.getString("po_ord_date");
                          x[6] = res.getString("po_due_date");
                          x[7] = res.getString("po_shipvia");
                          x[8] = res.getString("po_rmks");
                          x[9] = res.getString("po_curr");
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
    
    public static ArrayList<String[]> getPOMstrdetailsEDI(String order) {
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
            
           // line, item, venditem, qty, price, uom, desc
           res = st.executeQuery("SELECT * from pod_mstr " +
                   " where pod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          String[] s = new String[7];
                          for (int z = 0; z < 7; z++) {
                          s[z] = "";
                          }
                          s[0] = res.getString("pod_line");
                          s[1] = res.getString("pod_item");
                          s[2] = res.getString("pod_venditem");
                          s[3] = res.getString("pod_ord_qty");
                          s[4] = res.getString("pod_netprice");
                          s[5] = res.getString("pod_uom");
                          s[6] = res.getString("pod_desc");
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
    
    
    public static void updatePOFromReceiver(String receiver) {

        boolean partial = false;
        boolean complete = true;
        ArrayList<String> orders = new ArrayList<String>();
        Set<String> uniqueorders = new HashSet<String>();

        try{

        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;

            ArrayList qty = new ArrayList();
            ArrayList recvdqty = new ArrayList();
            ArrayList line = new ArrayList();
            ArrayList ordqty = new ArrayList();
            ArrayList linestatus = new ArrayList();
            ArrayList ponbr = new ArrayList();

        try{
            


            res = st.executeQuery("select pod_nbr, pod_status, pod_line, rvd_qty, pod_rcvd_qty, pod_ord_qty from recv_det inner join " +
                     " pod_mstr on rvd_item = pod_item and rvd_poline = pod_line and rvd_po = pod_nbr " +
               " where rvd_id = " + "'" + receiver + "'" +";");
               while (res.next()) {
                   recvdqty.add(res.getString("pod_rcvd_qty"));
                   qty.add(res.getString("rvd_qty"));
                   ordqty.add(res.getString("pod_ord_qty"));
                   linestatus.add(res.getString("pod_status"));
                   line.add(res.getString("pod_line"));
                   ponbr.add(res.getString("pod_nbr"));
                }
               res.close();

            if (dbtype.equals("sqlite")) {
              double total = 0;
              String status = "";
              for (int j = 0; j < line.size(); j++) {
                  total = bsParseDouble(qty.get(j).toString()) + bsParseDouble(recvdqty.get(j).toString());
                  if (total >= bsParseDouble(ordqty.get(j).toString())) {
                      status = getGlobalProgTag("closed");
                  } else {
                      status = linestatus.get(j).toString();
                  }
                  st.executeUpdate("update pod_mstr set pod_rcvd_qty = " + "'" + total + "'" + ", pod_status = " + "'" + status + "'" + 
                          " where pod_nbr = " + "'" + ponbr.get(j).toString() + "'" + 
                          " and pod_line = " + "'" + line.get(j).toString() + "'" +
                          ";" );
              
              }

            } else {
                st.executeUpdate(
                     " update pod_mstr inner join recv_det on rvd_item = pod_item and rvd_poline = pod_line and rvd_po = pod_nbr " +
                     " inner join po_mstr on po_nbr = pod_nbr " +
                      " set pod_rcvd_qty = pod_rcvd_qty + rvd_qty, pod_status = (case when pod_rcvd_qty + rvd_qty >= pod_ord_qty then " + "'" + getGlobalProgTag("closed") + "'" + " else pod_status end) " +
                 " where rvd_id = " + "'" + receiver + "'" + ";" );
            }



                // now let's select the unique orders involved in that shipper
               res = st.executeQuery("select pod_nbr from pod_mstr inner join recv_det on rvd_po = pod_nbr " +
               " where rvd_id = " + "'" + receiver + "'" +";");
               while (res.next()) {
                   uniqueorders.add(res.getString("pod_nbr"));
                }


               for (String uniqueorder : uniqueorders) {
                   orders.clear();
                    partial = false;
                   complete = true;
                   res = st.executeQuery("select pod_nbr, pod_status from pod_mstr " +
                           " where pod_nbr = " + "'" + uniqueorder + "'" +";");
                   while (res.next()) {
                       // logic is that a shipper has been committed with at least some portion of this order
                       // therefore if any line items on that order are still open...then the order was shipped partial...
                       //  therefore flag it as backorder
                       if (res.getString("pod_status").equals(getGlobalProgTag("open"))) {
                               partial = true;
                            }
                       if (! res.getString("pod_status").equals(getGlobalProgTag("closed"))) {
                               complete = false;
                            }
                    }
                    res.close();

                   if (complete) {
                    st.executeUpdate( "update po_mstr set po_status = " + "'" + getGlobalProgTag("closed") + "'" + " where po_nbr = " + "'" + uniqueorder + "'" + ";"); 
                   }
                   if (partial && ! complete) {
                   st.executeUpdate( "update po_mstr set po_status = 'partial' where po_nbr = " + "'" + uniqueorder + "'" + ";");
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

   }
 
    public static String[] updatePOFromAck(String po, String status) {
        String[] m = new String[]{"",""};
        String postat = "";
        try {
            
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            try {
                if (status.equals("AC") || status.equals("AD")) {
                  postat = "accepted";  
                } else {
                    postat = "rejected";
                }
                    st.executeUpdate(
                            " update po_mstr set po_edistatus = " + "'" + status + "'"
                            + ", po_import_855 = '1' "
                            + " where po_nbr = " + "'" + po + "'" + ";");
                    
                    m[0] = "success";
                    m[1] = "po: " + po;
               

            } catch (SQLException s) {
                MainFrame.bslog(s);
                m[0] = "error";
                m[1] = "Exception processing 855";
            } finally {
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m[0] = "error";
            m[1] = "Exception processing 855";
        }
        
        return m;
    }

    
    public record purchaseOrder(String[] m, po_mstr po, po_addr poa, ArrayList<pod_mstr> pod, ArrayList<po_meta> pom) {
        public purchaseOrder(String[] m) {
            this (m, null, null, null, null);
        }
    }
    
    
    public record po_mstr(String[] m, String po_nbr, String po_vend, 
     String po_ord_date, String po_due_date, String po_rmks, String po_shipvia,
    String po_status, String po_userid, String po_type, String po_curr,
    String po_terms, String po_site, String po_buyer, String po_ap_acct, String po_ap_cc, 
    String po_ship, String po_edistatus, String po_confirm ) {
        public po_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", ""
                    );
        }
    }
    
                              
    public record pod_mstr(String[] m, String pod_nbr, String pod_line, String pod_item, String pod_venditem, 
        String pod_ord_qty, String pod_rcvd_qty, String pod_netprice, String pod_disc,
        String pod_listprice, String pod_due_date, String pod_status, String pod_site,
        String pod_ord_date, String pod_uom, String pod_desc, String pod_ship ) {
        public pod_mstr(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "");
        }
    }
    
     public record po_addr(String[] m, String poa_code, String poa_shipto, 
        String poa_name, String poa_line1, String poa_line2,
        String poa_line3, String poa_city, String poa_state, 
        String poa_zip, String poa_country, String poa_contact, String poa_phone, String poa_email) {
        public po_addr(String[] m) {
            this(m,"","","","","","","","","","","","","");
        }
    }
    
    public record po_meta(String[] m, String pom_nbr, String pom_desc, String pom_type, 
        String pom_amttype, String pom_amt, String pom_key, String pom_value) {
        public po_meta(String[] m) {
            this (m, "", "", "", "", "", "", "");
        }
    } 
     
    public record po_ctrl (String[] m, String poc_rcpt_acct, String poc_rcpt_cc, String poc_venditem, String poc_rawonly ) {
        public po_ctrl(String[] m) {
            this(m,"", "", "", "");
        }
    } 
}
