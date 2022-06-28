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
package com.blueseer.eng;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
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
public class engData {
    
    
      
    public static String[] addECNMstr(ecn_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _addECNMstr(x, con, ps, res);  
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
    
    private static int _addECNMstr(ecn_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ecn_mstr where ecn_nbr = ?";
        String sqlInsert = "insert into ecn_mstr (ecn_nbr, ecn_poc, ecn_mstrtask, ecn_status, "
                        + " ecn_targetdate, ecn_createdate, ecn_closedate, "
                        + " ecn_drawing, ecn_item, ecn_rev, ecn_custrev ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.ecn_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.ecn_nbr);
            ps.setString(2, x.ecn_poc);
            ps.setString(3, x.ecn_mstrtask);
            ps.setString(4, x.ecn_status);
            ps.setString(5, x.ecn_targetdate);
            ps.setString(6, x.ecn_createdate);
            ps.setString(7, x.ecn_closedate);
            ps.setString(8, x.ecn_drawing);
            ps.setString(9, x.ecn_item);
            ps.setString(10, x.ecn_rev);
            ps.setString(11, x.ecn_custrev);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addECNTask(ecn_task x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ecn_task where ecnt_nbr = ? and ecnt_mstrid = ? and ecnt_seq = ?";
        String sqlInsert = "insert into ecn_task (ecnt_nbr, ecnt_mstrid, ecnt_seq,  "
                            + " ecnt_owner, ecnt_task, ecnt_assigndate, ecnt_closedate, ecnt_status, ecnt_notes) "
                        + " values (?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.ecnt_nbr);
          ps.setString(2, x.ecnt_mstrid);
          ps.setString(3, x.ecnt_seq);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.ecnt_nbr);
            ps.setString(2, x.ecnt_mstrid);
            ps.setString(3, x.ecnt_seq);
            ps.setString(4, x.ecnt_owner);
            ps.setString(5, x.ecnt_task);
            ps.setString(6, x.ecnt_assigndate);
            ps.setString(7, x.ecnt_closedate);
            ps.setString(8, "pending");  // hardcoded to 'pending' for initial add
            ps.setString(9, x.ecnt_notes);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addECNTransaction(ArrayList<ecn_task> ecnt, ecn_mstr ecn) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addECNMstr(ecn, bscon, ps, res);  
            for (ecn_task z : ecnt) {
                _addECNTask(z, bscon, ps, res);
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
    
     
    public static String[] updateECNMstr(ecn_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _updateECNMstr(x, con, ps);  // add cms_det
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
   
    private static int _updateECNMstr(ecn_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update ecn_mstr set ecn_poc = ?, ecn_mstrtask = ?,  " +
                "ecn_status = ?, ecn_targetdate = ?, ecn_createdate = ?, ecn_closedate = ?, " + 
                " ecn_drawing = ?, ecn_item = ?, ecn_rev = ?, ecn_custrev = ? " +
                 " where ecn_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(11, x.ecn_nbr);
            ps.setString(1, x.ecn_poc);
            ps.setString(2, x.ecn_mstrtask);
            ps.setString(3, x.ecn_status);
            ps.setString(4, x.ecn_targetdate);
            ps.setString(5, x.ecn_createdate);
            ps.setString(6, x.ecn_closedate);
            ps.setString(7, x.ecn_drawing);
            ps.setString(8, x.ecn_item);
            ps.setString(9, x.ecn_rev);
            ps.setString(10, x.ecn_custrev);
       
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateECNTask(ecn_task x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ecn_task where ecnt_nbr = ? and ecnt_mstrid = ? and ecnt_seq = ?";
        String sqlUpdate = "update ecn_task set  " +
                           " ecnt_owner = ?, ecnt_task = ?, ecnt_assigndate = ?, ecnt_closedate = ?, ecnt_status = ? " +
                 " where ecnt_nbr = ? and ecnt_mstrid = ? and ecnt_seq = ? ; ";
        String sqlInsert = "insert into ecn_task (ecnt_nbr, ecnt_mstrid, ecnt_seq, ecnt_owner, "
                            + " ecnt_task, ecnt_assigndate, ecnt_closedate, ecnt_status) "
                        + " values (?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.ecnt_nbr);
        ps.setString(2, x.ecnt_mstrid);
        ps.setString(3, x.ecnt_seq);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.ecnt_nbr);
            ps.setString(2, x.ecnt_mstrid);
            ps.setString(3, x.ecnt_seq);
            ps.setString(4, x.ecnt_owner);
            ps.setString(5, x.ecnt_task);
            ps.setString(6, x.ecnt_assigndate);
            ps.setString(7, x.ecnt_closedate);
            ps.setString(8, x.ecnt_status);
            // ps.setString(9, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(6, x.ecnt_nbr);
            ps.setString(7, x.ecnt_mstrid);
            ps.setString(8, x.ecnt_seq);
            ps.setString(1, x.ecnt_owner);
            ps.setString(2, x.ecnt_task);
            ps.setString(3, x.ecnt_assigndate);
            ps.setString(4, x.ecnt_closedate);
            ps.setString(5, x.ecnt_status);
            // ps.setString(7, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
        
    public static String[] updateECNTransaction(String x, ArrayList<String> lines, ArrayList<ecn_task> ecnt, ecn_mstr ecn) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            for (String line : lines) {
               _deleteECNLines(x, line, bscon);  // discard unwanted lines
             }
            for (ecn_task z : ecnt) {
                _updateECNTask(z, bscon, ps, res);
            }
             _updateECNMstr(ecn, bscon, ps);  // update so_mstr
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
    
    
    public static ecn_mstr getECNMstr(String[] x) {
        ecn_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from ecn_mstr where ecn_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ecn_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ecn_mstr(m, res.getString("ecn_nbr"), res.getString("ecn_poc"), 
                        res.getString("ecn_mstrtask"), res.getString("ecn_status"), res.getString("ecn_targetdate"), 
                        res.getString("ecn_createdate"), res.getString("ecn_closedate"), res.getString("ecn_drawing"), 
                        res.getString("ecn_item"), res.getString("ecn_rev"), res.getString("ecn_custrev"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ecn_mstr(m);
        }
        return r;
    }
    
    public static ArrayList<ecn_task> getECNTask(String code) {
        ecn_task r = null;
        String[] m = new String[2];
        ArrayList<ecn_task> list = new ArrayList<ecn_task>();
        String sql = "select * from ecn_task where ecnt_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ecn_task(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ecn_task(m, res.getString("ecnt_nbr"), res.getString("ecnt_mstrid"), res.getString("ecnt_seq"), res.getString("ecnt_owner"), res.getString("ecnt_task"),
                    res.getString("ecnt_assigndate"), res.getString("ecnt_closedate"), res.getString("ecnt_status"), res.getString("ecnt_notes")
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ecn_task(m);
               list.add(r);
        }
        return list;
    }
    
    public static ecn_task getECNTask(String nbr, String masterid, String seq) {
        ecn_task r = null;
        String[] m = new String[2];
        String sql = "select * from ecn_task where ecnt_nbr = ? and ecnt_mstrid = ? and ecnt_seq = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, nbr);
        ps.setString(2, masterid);
        ps.setString(3, seq); 
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ecn_task(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                      r = new ecn_task(m, res.getString("ecnt_nbr"), res.getString("ecnt_mstrid"), res.getString("ecnt_seq"), res.getString("ecnt_owner"), res.getString("ecnt_task"),
                    res.getString("ecnt_assigndate"), res.getString("ecnt_closedate"), res.getString("ecnt_status"), res.getString("ecnt_notes")
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ecn_task(m);
        }
        return r;
    }
        
    
    public static String[] deleteECNMstr(ecn_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteECNMstr(x, con);  // add cms_det
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
    
    public static String[] deleteECNLines(String x, ArrayList<String> lines) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
             for (String line : lines) {
               _deleteECNLines(x, line, con);  // add cms_det
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
    
    private static void _deleteECNLines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from ecn_task where ecnt_nbr = ? and ecnt_mstrid = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        ps.close();
    }
        
    private static void _deleteECNMstr(ecn_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from ecn_mstr where ecn_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.ecn_nbr);
        ps.executeUpdate();
        sql = "delete from ecn_task where ecnt_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.ecn_nbr);
        ps.executeUpdate();
        ps.close();
    }
     
       
    public static String[] addTaskMstr(task_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _addTaskMstr(x, con, ps, res);  
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
    
    private static int _addTaskMstr(task_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from task_mstr where task_id = ?";
        String sqlInsert = "insert into task_mstr (task_id, task_desc) "
                        + " values (?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.task_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.task_id);
            ps.setString(2, x.task_desc);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addTaskDet(task_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from task_det where taskd_id = ? and taskd_sequence = ?";
        String sqlInsert = "insert into task_det (taskd_id, taskd_owner, taskd_desc,  "
                            + " taskd_enabled, taskd_sequence ) "
                        + " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.taskd_id);
          ps.setString(2, x.taskd_sequence);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.taskd_id);
            ps.setString(2, x.taskd_owner);
            ps.setString(3, x.taskd_desc);
            ps.setString(4, x.taskd_enabled);
            ps.setString(5, x.taskd_sequence);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addTaskTransaction(ArrayList<task_det> td, task_mstr tm) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addTaskMstr(tm, bscon, ps, res);  
            for (task_det z : td) {
                _addTaskDet(z, bscon, ps, res);
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
    
     public static String[] updateTaskMstr(task_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _updateTaskMstr(x, con, ps);  // add cms_det
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
   
    private static int _updateTaskMstr(task_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update task_mstr set task_desc = ?  " +
                 " where task_id = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(2, x.task_id);
            ps.setString(1, x.task_desc);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateTaskdet(task_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from task_det where taskd_id = ? and taskd_sequence = ?";
        String sqlUpdate = "update task_det set taskd_owner = ?, " +
                           " taskd_desc = ?, taskd_enabled = ? " +
                 " where taskd_id = ? and taskd_sequence = ? ; ";
        String sqlInsert = "insert into task_det (taskd_id, taskd_owner, taskd_desc, taskd_enabled, "
                            + " taskd_sequence) "
                        + " values (?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.taskd_id);
        ps.setString(2, x.taskd_sequence);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.taskd_id);
            ps.setString(2, x.taskd_owner);
            ps.setString(3, x.taskd_desc);
            ps.setString(4, x.taskd_enabled);
            ps.setString(5, x.taskd_sequence);
            // ps.setString(9, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(4, x.taskd_id);
            ps.setString(5, x.taskd_sequence);
            ps.setString(1, x.taskd_owner);
            ps.setString(2, x.taskd_desc);
            ps.setString(3, x.taskd_enabled);
            // ps.setString(7, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
        
    public static String[] updateTaskTransaction(String x, ArrayList<String> lines, ArrayList<task_det> td, task_mstr tm) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            for (String line : lines) {
               _deleteTaskLines(x, line, bscon);  // discard unwanted lines
             }
            for (task_det z : td) {
                _updateTaskdet(z, bscon, ps, res);
            }
             _updateTaskMstr(tm, bscon, ps);  // update so_mstr
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
    
    
    public static task_mstr getTaskMstr(String[] x) {
        task_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from task_mstr where task_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new task_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new task_mstr(m, res.getString("task_id"), res.getString("task_desc"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new task_mstr(m);
        }
        return r;
    }
    
    public static ArrayList<task_det> getTaskDet(String code) {
        task_det r = null;
        String[] m = new String[2];
        ArrayList<task_det> list = new ArrayList<task_det>();
        String sql = "select * from task_det where taskd_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new task_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new task_det(m, res.getString("taskd_id"), res.getString("taskd_owner"), res.getString("taskd_desc"), res.getString("taskd_enabled"), res.getString("taskd_sequence"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new task_det(m);
               list.add(r);
        }
        return list;
    }
    
    public static task_det getTaskDet(String id, String sequence) {
        task_det r = null;
        String[] m = new String[2];
        String sql = "select * from task_det where taskd_id = ? and taskd_sequence = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
        ps.setString(2, sequence);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new task_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                      r = new task_det(m, res.getString("taskd_id"), res.getString("taskd_owner"), res.getString("taskd_desc"), res.getString("taskd_enabled"), res.getString("taskd_sequence"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new task_det(m);
        }
        return r;
    }
        
    
    public static String[] deleteTaskMstr(task_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteTaskMstr(x, con);  // add cms_det
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
    
    public static String[] deleteTaskLines(String x, ArrayList<String> lines) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
             for (String line : lines) {
               _deleteTaskLines(x, line, con);  // add cms_det
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
    
    private static void _deleteTaskLines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from task_det where taskd_id = ? and taskd_sequence = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        ps.close();
    }
        
    private static void _deleteTaskMstr(task_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from task_mstr where task_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.task_id);
        ps.executeUpdate();
        sql = "delete from task_det where taskd_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.task_id);
        ps.executeUpdate();
        ps.close();
    }
     
    
    // misc
    public static ArrayList<String> getECNSequences(String nbr) {
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

           res = st.executeQuery("SELECT ecnt_seq from ecn_task " +
                   " where ecnt_nbr = " + "'" + nbr + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("ecnt_seq"));
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
    
    public static ArrayList<String> getTaskSequences(String nbr) {
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

           res = st.executeQuery("SELECT taskd_sequence from task_det " +
                   " where taskd_id = " + "'" + nbr + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("taskd_sequence"));
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
    
    public static ArrayList getTaskMasterList() {
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
            try {

            res = st.executeQuery("select task_id from task_mstr order by task_id ;"); 

           while (res.next()) {
                myarray.add(res.getString("task_id"));

            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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

    
    public record ecn_mstr(String[] m, String ecn_nbr, String ecn_poc, String ecn_mstrtask, 
        String ecn_status, String ecn_targetdate, String ecn_createdate, String ecn_closedate,
        String ecn_drawing, String ecn_item, String ecn_rev, String ecn_custrev) {
        public ecn_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "");
        }
    }
    
    public record ecn_task(String[] m, String ecnt_nbr, String ecnt_mstrid, String ecnt_seq, 
        String ecnt_owner, String ecnt_task, String ecnt_assigndate, String ecnt_closedate, 
        String ecnt_status, String ecnt_notes) {
        public ecn_task(String[]m) {
            this(m, "", "", "", "", "", "", "", "", "");
        }
    }
    
    
    public record task_mstr(String[] m, String task_id, String task_desc) {
        public task_mstr(String[] m) {
            this(m, "", "");
        }
    }
    
    public record task_det(String[] m, String taskd_id, String taskd_owner, String taskd_desc, 
        String taskd_enabled, String taskd_sequence) {
        public task_det(String[]m) {
            this(m, "", "", "", "", "");
        }
    }
    
}
