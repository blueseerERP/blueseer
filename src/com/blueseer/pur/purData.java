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
            con = DriverManager.getConnection(url + db, user, pass);
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
                        + " po_terms, po_ap_acct, po_ap_cc, po_rmks ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
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
            rows = ps.executeUpdate();
            } 
            return rows;
    }
            
    public static String[] addPOTransaction(ArrayList<pod_mstr> pod, po_mstr po) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addPOMstr(po, bscon, ps, res);  
            for (pod_mstr z : pod) {
                _addPODet(z, bscon, ps, res);
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
     
    public static String[] updatePOMstr(po_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _updatePOMstr(x, con, ps);  // add cms_det
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
                "po_site = ?, po_buyer = ?, po_due_date = ?, po_shipvia = ? " +               
                 " where po_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(7, x.po_nbr);
            ps.setString(1, x.po_status);
            ps.setString(2, x.po_rmks);
            ps.setString(3, x.po_site);
            ps.setString(4, x.po_buyer);
            ps.setString(5, x.po_due_date);
            ps.setString(6, x.po_shipvia);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updatePODet(pod_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pod_mstr where pod_nbr = ? and pod_line = ?";
        String sqlUpdate = "update pod_mstr set pod_part = ?, pod_vendpart = ?, " +
                " pod_ord_qty = ?, pod_uom = ?, " +
                " pod_listprice = ?, pod_disc = ?, pod_netprice = ?,  " +
                " pod_due_date = ?, pod_status = ?, " +
                " pod_site = ? " +
                 " where pod_nbr = ? and pod_line = ? ; ";
        String sqlInsert = "insert into pod_mstr (pod_nbr, pod_line, pod_part, pod_vendpart, "
                            + " pod_ord_qty, pod_uom, pod_listprice, pod_disc, "
                            + " pod_netprice, pod_ord_date, pod_due_date, "
                            + "pod_rcvd_qty, pod_status, pod_site) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.pod_nbr);
        ps.setString(2, x.pod_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.pod_nbr);
            ps.setString(2, x.pod_line);
            ps.setString(3, x.pod_part);
            ps.setString(4, x.pod_vendpart);
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
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(11, x.pod_nbr);
            ps.setString(12, x.pod_line);
            ps.setString(1, x.pod_part);
            ps.setString(2, x.pod_vendpart);
            ps.setString(3, x.pod_ord_qty);
            ps.setString(4, x.pod_uom);
            ps.setString(5, x.pod_listprice);
            ps.setString(6, x.pod_disc);
            ps.setString(7, x.pod_netprice);
            ps.setString(8, x.pod_due_date);
            ps.setString(9, x.pod_status);
            ps.setString(10, x.pod_site);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
        
    public static String[] updatePOTransaction(String x, ArrayList<String> lines, ArrayList<pod_mstr> pod, po_mstr po) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
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
          
             _updatePOMstr(po, bscon, ps);  // update so_mstr
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
    
    
    public static String[] deletePOMstr(po_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deletePOMstr(x, con);  // add cms_det
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
            con = DriverManager.getConnection(url + db, user, pass);
             for (String line : lines) {
               _deletePOLines(x, line, con);  // add cms_det
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
      
    public static po_mstr getPOMstr(String[] x) {
        po_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from po_mstr where po_nbr = ? ;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
                        res.getString("po_ap_cc"));
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
                             
    private static int _addPODet(pod_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pod_mstr where pod_nbr = ? and pod_line = ?";
        String sqlInsert = "insert into pod_mstr (pod_nbr, pod_line, pod_part, pod_vendpart, "
                            + " pod_ord_qty, pod_uom, pod_listprice, pod_disc, "
                            + " pod_netprice, pod_ord_date, pod_due_date, "
                            + "pod_rcvd_qty, pod_status, pod_site) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.pod_nbr);
          ps.setString(2, x.pod_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.pod_nbr);
            ps.setString(2, x.pod_line);
            ps.setString(3, x.pod_part);
            ps.setString(4, x.pod_vendpart);
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
            rows = ps.executeUpdate();
            } 
            return rows;
    }
       
    
    // miscellaneous SQL queries
   
    
    public record po_mstr(String[] m, String po_nbr, String po_vend, 
     String po_ord_date, String po_due_date, String po_rmks, String po_shipvia,
    String po_status, String po_userid, String po_type, String po_curr,
    String po_terms, String po_site, String po_buyer, String po_ap_acct, String po_ap_cc) {
        public po_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", ""
                    );
        }
    }
    
                              
    public record pod_mstr(String[] m, String pod_nbr, String pod_line, String pod_part, String pod_vendpart, 
        String pod_ord_qty, String pod_rcvd_qty, String pod_netprice, String pod_disc,
        String pod_listprice, String pod_due_date, String pod_status, String pod_site,
        String pod_ord_date, String pod_uom ) {
        public pod_mstr(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "");
        }
    }
    
   
}
