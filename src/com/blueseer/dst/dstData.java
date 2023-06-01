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
package com.blueseer.dst;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
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
public class dstData {
    
    
    public static String[] addDOMstr(do_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _addDOMstr(x, con, ps, res);  
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
    
    private static int _addDOMstr(do_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from do_mstr where do_nbr = ?";
        String sqlInsert = "insert into do_mstr (do_nbr, do_site, do_type, do_wh_from, do_wh_to, " 
                        + " do_shipdate, do_recvdate, do_status, do_ref, do_rmks, do_shipvia, "
                        + " do_pallets, do_gross_wt, do_char1, do_char2 ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.do_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.do_nbr);
            ps.setString(2, x.do_site);
            ps.setString(3, x.do_type);
            ps.setString(4, x.do_wh_from);
            ps.setString(5, x.do_wh_to);
            ps.setString(6, x.do_shipdate);
            ps.setString(7, x.do_recvdate);
            ps.setString(8, x.do_status);
            ps.setString(9, x.do_ref);
            ps.setString(10, x.do_rmks);
            ps.setString(11, x.do_shipvia);
            ps.setString(12, x.do_pallets);
            ps.setString(13, x.do_gross_wt);
            ps.setString(14, x.do_char1);
            ps.setString(15, x.do_char2);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addDODet(dod_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from dod_mstr where dod_nbr = ? and dod_line = ?";
        String sqlInsert = "insert into dod_mstr (dod_nbr, dod_line, dod_item,  "
                            + " dod_qty, dod_ref, dod_serial, dod_uom, dod_char1) "
                        + " values (?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.dod_nbr);
          ps.setString(2, x.dod_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.dod_nbr);
            ps.setString(2, x.dod_line);
            ps.setString(3, x.dod_item);
            ps.setString(4, x.dod_qty);
            ps.setString(5, x.dod_ref);
            ps.setString(6, x.dod_serial);
            ps.setString(7, x.dod_uom);
            ps.setString(8, x.dod_char1);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addDOTransaction(ArrayList<dod_mstr> dod, do_mstr dom) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addDOMstr(dom, bscon, ps, res);  
            for (dod_mstr z : dod) {
                _addDODet(z, bscon, ps, res);
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
    
     
    public static String[] updateDOMstr(do_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _updateDOMstr(x, con, ps);  // add cms_det
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
   
    private static int _updateDOMstr(do_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update do_mstr set do_site = ?, do_type = ?,  " +
                "do_wh_from = ?, do_wh_to = ?, do_shipdate = ?, do_recvdate = ?, " + 
                " do_status = ?, do_ref = ?, do_rmks = ?, do_shipvia = ?, " +
                " do_pallets = ?, do_gross_wt = ?, do_char1 = ?, do_char2 = ? " +
                 " where do_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(15, x.do_nbr);
            ps.setString(1, x.do_site);
            ps.setString(2, x.do_type);
            ps.setString(3, x.do_wh_from);
            ps.setString(4, x.do_wh_to);
            ps.setString(5, x.do_shipdate);
            ps.setString(6, x.do_recvdate);
            ps.setString(7, x.do_status);
            ps.setString(8, x.do_ref);
            ps.setString(9, x.do_rmks);
            ps.setString(10, x.do_shipvia);
            ps.setString(11, x.do_pallets);
            ps.setString(12, x.do_gross_wt);
            ps.setString(13, x.do_char1);
            ps.setString(14, x.do_char2);
       
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateDODet(dod_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from dod_mstr where dod_nbr = ? and dod_line = ?";
        String sqlUpdate = "update dod_mstr set dod_item = ?, " +
                           " dod_qty = ?, dod_ref = ?, dod_serial = ?, dod_uom = ?, dod_char1 = ? " +
                 " where dod_nbr = ? and dod_line = ? ; ";
        String sqlInsert = "insert into dod_mstr (dod_nbr, dod_line, dod_item, dod_qty, "
                            + " dod_ref, dod_serial, dod_uom, dod_char1) "
                        + " values (?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.dod_nbr);
        ps.setString(2, x.dod_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.dod_nbr);
            ps.setString(2, x.dod_line);
            ps.setString(3, x.dod_item);
            ps.setString(4, x.dod_qty);
            ps.setString(5, x.dod_ref);
            ps.setString(6, x.dod_serial);
            ps.setString(7, x.dod_uom);
            ps.setString(8, x.dod_char1);
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(7, x.dod_nbr);
            ps.setString(8, x.dod_line);
            ps.setString(1, x.dod_item);
            ps.setString(2, x.dod_qty);
            ps.setString(3, x.dod_ref);
            ps.setString(4, x.dod_serial);
            ps.setString(5, x.dod_uom);
            ps.setString(6, x.dod_char1);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
        
    public static String[] updateDOTransaction(String x, ArrayList<String> lines, ArrayList<dod_mstr> dod, do_mstr dom) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            for (String line : lines) {
               _deleteDOLines(x, line, bscon);  // discard unwanted lines
             }
            for (dod_mstr z : dod) {
                _updateDODet(z, bscon, ps, res);
            }
             _updateDOMstr(dom, bscon, ps);  // update so_mstr
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
    
    
    public static do_mstr getDOMstr(String[] x) {
        do_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from do_mstr where do_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new do_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new do_mstr(m, res.getString("do_nbr"), res.getString("do_site"), 
                        res.getString("do_type"), res.getString("do_wh_from"), res.getString("do_wh_to"), 
                        res.getString("do_shipdate"), res.getString("do_recvdate"), res.getString("do_status"), 
                        res.getString("do_ref"), res.getString("do_rmks"), res.getString("do_shipvia"), 
                        res.getString("do_pallets"), res.getString("do_gross_wt"), res.getString("do_char1"), 
                        res.getString("do_char2"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new do_mstr(m);
        }
        return r;
    }
    
    public static ArrayList<dod_mstr> getDODet(String code) {
        dod_mstr r = null;
        String[] m = new String[2];
        ArrayList<dod_mstr> list = new ArrayList<dod_mstr>();
        String sql = "select * from dod_mstr where dod_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new dod_mstr(m);
                } else {
                    // line, item, qty, uom, ref, serial 
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new dod_mstr(m, res.getString("dod_nbr"), res.getString("dod_line"), res.getString("dod_item"), res.getString("dod_qty"), res.getString("dod_uom"),
                    res.getString("dod_ref"), res.getString("dod_serial"), res.getString("dod_char1")
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new dod_mstr(m);
               list.add(r);
        }
        return list;
    }
    
     public static dod_mstr getDODet(String shipto, String code) {
        dod_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from dod_mstr where dod_nbr = ? and dod_line = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, shipto);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new dod_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                       r = new dod_mstr(m, res.getString("dod_nbr"), res.getString("dod_line"), res.getString("dod_item"), res.getString("dod_qty"), res.getString("dod_ref"),
                    res.getString("dod_serial"), res.getString("dod_uom"), res.getString("dod_char1")
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new dod_mstr(m);
        }
        return r;
    }
    
    
    public static String[] deleteDOMstr(do_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteDOMstr(x, con);  // add cms_det
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
    
    public static String[] deleteDOLines(String x, ArrayList<String> lines) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
             for (String line : lines) {
               _deleteDOLines(x, line, con);  // add cms_det
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
    
    private static void _deleteDOLines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from dod_mstr where dod_nbr = ? and dod_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        ps.close();
    }
    
    
    private static void _deleteDOMstr(do_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from do_mstr where do_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.do_nbr);
        ps.executeUpdate();
        sql = "delete from dod_mstr where dod_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.do_nbr);
        ps.executeUpdate();
        ps.close();
    }
     
    
    // misc
    public static ArrayList<String> getDOLines(String order) {
        ArrayList<String> lines = new ArrayList<String>();
        try{
        Class.forName(driver).newInstance();
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("SELECT dod_line from dod_mstr " +
                   " where dod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("dod_line"));
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
    
    
    public record do_mstr(String[] m, String do_nbr, String do_site, String do_type,
        String do_wh_from, String do_wh_to, String do_shipdate, String do_recvdate, 
        String do_status, String do_ref, String do_rmks, String do_shipvia, 
        String do_pallets, String do_gross_wt, String do_char1, String do_char2) {
        public do_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "");
        }
    }

    public record dod_mstr(String[] m, String dod_nbr, String dod_line, String dod_item,
        String dod_qty, String dod_uom, String dod_ref, String dod_serial, String dod_char1) {
        public dod_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "");
        }
    }

}
