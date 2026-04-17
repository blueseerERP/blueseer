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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.blueseer.utl.OVData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.json.JSONArray;

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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addECNTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(ecnt);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(ecn);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServENG"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateECNTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(lines);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(ecnt);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(ecn);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServENG"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getECNMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServENG");
                r = objectMapper.readValue(returnstring, ecn_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<String[]>();
            paramlist.add(new String[]{"id", "getECNTask"});
            paramlist.add(new String[]{"param1",  code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServENG");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<ecn_task>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
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
    
    public static ecn_task getECNTaskSeq(String nbr, String masterid, String seq) {
        ecn_task r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<String[]>();
            paramlist.add(new String[]{"id", "getECNTaskSeq"});
            paramlist.add(new String[]{"param1",  nbr});
            paramlist.add(new String[]{"param2",  masterid});
            paramlist.add(new String[]{"param3",  seq});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServENG");
                r = objectMapper.readValue(returnstring, ecn_task.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteECNMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServENG"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addTaskTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(td);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(tm);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServENG"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateTaskTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(lines);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(td);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(tm);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServENG"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getTaskMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServENG");
                r = objectMapper.readValue(returnstring, task_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
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
                        r = new task_mstr(m, res.getString("task_id"), 
                                res.getString("task_desc"),
                                res.getString("task_class"),
                                res.getString("task_creator"),
                                res.getString("task_date_create"),
                                res.getString("task_date_mod"),
                                res.getString("task_status"),
                                res.getString("task_comments"));
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<String[]>();
            paramlist.add(new String[]{"id", "getTaskDet"});
            paramlist.add(new String[]{"param1",  code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServENG");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<task_det>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
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
    
    public static task_det getTaskDetSeq(String id, String sequence) {
        task_det r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<String[]>();
            paramlist.add(new String[]{"id", "getTaskDetSeq"});
            paramlist.add(new String[]{"param1",  id});
            paramlist.add(new String[]{"param2",  sequence});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServENG");
                r = objectMapper.readValue(returnstring, task_det.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteTaskMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServENG"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
    public static String getECNBrowseView(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                
                res = st.executeQuery("select ecn_nbr, ecn_item, ecn_poc, ecn_status, ecn_createdate, ecn_mstrtask, ecn_targetdate " +
                          " from ecn_mstr where " +
                        " ecn_createdate >= " + "'" + keys[0] + "'" + " AND " +
                        " ecn_createdate <= " + "'" + keys[1] + "'" +
                        " order by ecn_nbr desc;");
                    while (res.next()) {                  
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("ecn_nbr"));
                        rowArray.put(res.getString("ecn_mstrtask"));
                        rowArray.put(res.getString("ecn_poc"));
                        rowArray.put(res.getString("ecn_item"));
                        rowArray.put(res.getString("ecn_status"));
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
    }
    
    public static String getECNBrowseViewDet(String key) {
        JSONArray jsonarray = new JSONArray();
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
                
                res = st.executeQuery("select ecnt_seq, ecnt_owner, ecnt_task, ecnt_assigndate, ecnt_closedate, ecnt_status from ecn_task " +
                        " where ecnt_nbr = " + "'" + key + "'" +  ";");
                    while (res.next()) {                       
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("ecnt_seq"));
                        rowArray.put(res.getString("ecnt_owner"));
                        rowArray.put(res.getString("ecnt_task"));
                        rowArray.put(res.getString("ecnt_assigndate"));
                        rowArray.put(res.getString("ecnt_closedate"));
                        rowArray.put(res.getString("ecnt_status"));
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
    }
   
    public static String getTaskBrowseView(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                
                res = st.executeQuery("select task_id, task_desc, task_class, task_creator, task_date_mod, task_status " +
                          " from task_mstr where " +
                        " task_date_create >= " + "'" + keys[0] + "'" + " AND " +
                        " task_date_create <= " + "'" + keys[1] + "'" +
                        " order by task_id desc;");
                    while (res.next()) {     
                        
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("task_id"));
                        rowArray.put(res.getString("task_desc"));
                        rowArray.put(res.getString("task_class"));
                        rowArray.put(res.getString("task_creator"));
                        rowArray.put(res.getString("task_date_mod"));
                        rowArray.put(res.getString("task_status"));
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
    }
    
    public static String getTaskBrowseViewDet(String key) {
        JSONArray jsonarray = new JSONArray();
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
                
                res = st.executeQuery("select taskd_id, taskd_owner, taskd_desc, taskd_sequence, taskd_enabled from task_det " +
                        " where taskd_id = " + "'" + key + "'" +  ";");
                    while (res.next()) {        
                       
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("taskd_sequence"));
                        rowArray.put(res.getString("taskd_owner"));
                        rowArray.put(res.getString("taskd_desc"));
                        rowArray.put(res.getString("taskd_enabled"));
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
    }
   
    public static boolean completeECNTask(String id, String sequence) {
        boolean islast = false;
        
        try {
            
        int i = 0;
        int nextsequence = 0;
        
        boolean isEmail = false;
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            
            try {
                // OK...lets determine if last sequence
                
               res = st.executeQuery("select * from ecn_task left outer join ecn_ctrl on ecnc_email <> '' where ecnt_nbr = "
                     + "'" + id + "'" +  " order by ecnt_seq desc ;"); 
                 while (res.next()) {
                   i++;
                   isEmail = res.getBoolean("ecnc_email");
                   if (i == 1) {
                      if (bsParseInt(sequence) == Integer.parseInt(res.getString("ecnt_seq"))) {
                         islast = true;
                      }
                   }
                   
                   if (! islast && bsParseInt(sequence) == i) {
                       nextsequence = i + 1;
                       break;
                   }
                 }
                i = 0;
                
             
                
                // now...lets update task and set to complete 
                 st.executeUpdate("update ecn_task set ecnt_status = " +
                         "'" + getGlobalProgTag("complete") + "'" + " where " + 
                        " ecnt_nbr = " + "'" + id + "'" + " AND " + 
                         "ecnt_seq = " + "'" + sequence + "'" + ";");
                 
                
                // let's get the next sequence userid for email purposes
                 if (! islast) {
                 res = st.executeQuery("select * from ecn_task inner join user_mstr on " +
                          " user_id = ecnt_owner " + " inner join ecn_mstr on " +
                          " ecnt_nbr = ecn_nbr " +
                          "where " +
                          "ecnt_nbr = " + "'" + id + "'" + " AND " +
                          "ecnt_seq = " + "'" + nextsequence + "'" + ";");
                 while (res.next()) {
                     String subject = "ECN Notice of Action";
                     String body = "ECN number " + id + " requires your completion";
                     String requestor = "Eng POC = " + res.getString("ecn_poc");
                     String amount = "Task = " + res.getString("ecnt_task");
                     body = body + "\n" + requestor + "\n" + amount;
                     if (! res.getString("user_email").isEmpty())
                     OVData.sendEmail(res.getString("user_email"), subject, body, "", false);
                 }
                 
                 }
                 
                 // now...lets set next sequence to pending....if there is one
                 if (! islast) {
                 st.executeUpdate("update ecn_task set ecnt_status = " +
                         "'" + getGlobalProgTag("pending") + "'" + " where " + 
                        " ecnt_nbr = " + "'" + id + "'" + " AND " + 
                         "ecnt_seq = " + "'" + nextsequence + "'" + ";");
                 }
                 
                 //finally....if is last sequence...then set entire Req to 'approved'
                 if (islast) {
                
                  st.executeUpdate("update ecn_mstr set ecn_status = " +
                        "'" + getGlobalProgTag("closed") + "'" + " where " + 
                        " ecn_nbr = " + "'" + id + "'" +  ";");
                         
                       
                        // if (isEmail) {
                       //  sendEmailToAll(id);
                       //  }
                 }
                

            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return islast;
    }
    
    public static void updateECNNotes(String ecn, String seq, String note) {
       
        try {
            int i = 0;
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            try {
               
                st.executeUpdate(" update ecn_task " +
                        " set ecnt_notes = " + "'" + note.replace("'", "") + "'" +
                        " where ecnt_nbr = " + "'" + ecn + "'" 
                        + " and ecnt_seq = " + "'" + seq + "'" 
                        + ";");
               
               
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
   
    public static void sendECNEmailToAll(String myid) {
        
       int i = 0;
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
                res = st.executeQuery("select * from ecn_task " +
                        " inner join ecn_mstr on ecn_nbr = ecnt_nbr " +
                        " inner join user_mstr on " +
                          " user_id = ecnt_owner " +
                          "where " +
                          "ecnt_nbr = " + "'" + myid + "'" + 
                           ";");
                 while (res.next()) {
                     String subject = "ECN Notice of Closure";
                     String body = "ECN number " + myid + " is closed. \n";
                     body += "ECN Task ID " + res.getString("ecn_mstrtask") + "\n";
                     body += "Part Number: " + res.getString("ecn_item") + "\n";
                     
                     if (! res.getString("user_email").isEmpty())
                     OVData.sendEmail(res.getString("user_email"), subject, body, "", false);
                 }
               
           }
            catch (SQLException s){
                MainFrame.bslog(s);
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
       
    }
    
    
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
             bslog(s);
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
    
    
    public record task_mstr(String[] m, String task_id, String task_desc, String task_class, String task_creator,
        String task_date_create, String task_date_mod, String task_status, String task_comments) {
        public task_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "");
        }
    }
    
    public record task_det(String[] m, String taskd_id, String taskd_owner, String taskd_desc, 
        String taskd_enabled, String taskd_sequence) {
        public task_det(String[]m) {
            this(m, "", "", "", "", "");
        }
    }
    
}
