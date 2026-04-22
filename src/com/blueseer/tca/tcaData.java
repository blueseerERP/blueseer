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
package com.blueseer.tca;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.hrm.hrmData;
import static com.blueseer.hrm.hrmData._getEmployeeMstr;
import com.blueseer.hrm.hrmData.emp_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
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
public class tcaData {
    
    public static String[] addUpdateCLKCtrl(clock_ctrl x) {
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  clock_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into clock_ctrl (clctrl_scan) "
                        + " values (?); "; 
        String sqlUpdate = "update clock_ctrl set clctrl_scan = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.clctrl_scan);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.clctrl_scan);
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
   
    public static clock_ctrl getCLKCtrl(String[] x) {
        clock_ctrl r = null;
        String[] m = new String[2];
        String sql = "select * from clock_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new clock_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new clock_ctrl(m, 
                                res.getString("clctrl_scan")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new clock_ctrl(m);
        }
        return r;
    }
    
    public static String[] addTimeClock(time_clock x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addTimeClock"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServTCA"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlInsert = "insert into time_clock (emp_nbr, " +
        " indate, outdate, intime, outtime, dept, code_id, " +
        " intime_adj, outtime_adj, tothrs, login, changed, " +
        " user_fld1, user_fld2, user_fld3, comment, code_orig, " +
        " who_changed, ispaid, checknbr) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";        
          try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            psi.setString(1, x.emp_nbr);
            psi.setString(2, x.indate);
            psi.setString(3, x.outdate);
            psi.setString(4, x.intime);
            psi.setString(5, x.outtime);
            psi.setString(6, x.dept);
            psi.setString(7, x.code_id);
            psi.setString(8, x.intime_adj);
            psi.setString(9, x.outtime_adj);
            psi.setDouble(10, x.tothrs);
            psi.setString(11, x.login);
            psi.setString(12, x.changed);
            psi.setString(13, x.user_fld1);
            psi.setString(14, x.user_fld2);
            psi.setString(15, x.user_fld3);
            psi.setString(16, x.comment);
            psi.setString(17, x.code_orig);
            psi.setString(18, x.who_changed);
            psi.setString(19, x.ispaid);
            psi.setString(20, x.checknbr);
            
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            
          } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
          }
       
        return m;
    }

    public static String[] updateTimeClock(time_clock x) { // key is recid
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateTimeClock"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServTCA"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlUpdate = "update time_clock set " +
        " indate = ?, outdate = ?, intime = ?, outtime = ?, intime_adj = ?, outtime_adj = ?, " +
        " tothrs = ?, code_id = ?, who_changed = ?, changed = ? " +
        " where recid = ? ";        
          try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlUpdate);) {             
            ps.setString(1, x.indate);
            ps.setString(2, x.outdate);
            ps.setString(3, x.intime);
            ps.setString(4, x.outtime);
            ps.setString(5, x.intime_adj);
            ps.setString(6, x.outtime_adj);
            ps.setDouble(7, x.tothrs);
            ps.setString(8, x.code_id);
            ps.setString(9, x.who_changed);
            ps.setString(10, x.changed);
            ps.setInt(11, x.recid);
            
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
            
          } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
          }
       
        return m;
    }

    public static String[] updateTimeClockRec(time_clock x) { // used only for updating clock out recs
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateTimeClockRec"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServTCA"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlUpdate = "update time_clock set " +
        " outdate = ?, outtime = ?, outtime_adj = ?,  tothrs = ?, code_id = '00' " +
        " where emp_nbr = ? and code_id = '01' ";        
          try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlUpdate);) {             
            ps.setString(1, x.outdate);
            ps.setString(2, x.outtime);
            ps.setString(3, x.outtime_adj);
            ps.setDouble(4, x.tothrs);
            ps.setString(5, x.emp_nbr);
            
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
            
          } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
          }
       
        return m;
    }

    public static time_clock getTimeClockRec(String[] x) { // used only for getting clock out recs
        time_clock r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getTimeClockRec"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServTCA");
                r = objectMapper.readValue(returnstring, time_clock.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from time_clock where emp_nbr = ? and code_id = '01' ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new time_clock(m);
                } else {
                    while(res.next()) {
        
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new time_clock(m, 
                        res.getString("emp_nbr"), 
                        res.getString("indate"),
                        res.getString("outdate"),
                        res.getString("intime"),
                        res.getString("outtime"),
                        res.getString("dept"),
                        res.getString("code_id"),
                        res.getString("intime_adj"),
                        res.getString("outtime_adj"),
                        res.getDouble("tothrs"),
                        res.getInt("recid"),
                        res.getString("login"),
                        res.getString("changed"),
                        res.getString("user_fld1"),
                        res.getString("user_fld2"),
                        res.getString("user_fld3"),
                        res.getString("comment"),
                        res.getString("code_orig"),
                        res.getString("who_changed"),
                        res.getString("ispaid"),
                        res.getString("checknbr")     
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new time_clock(m);
        }
        return r;
    }
   
    public static time_clock getTimeClock(String[] x) { // recid is key
        time_clock r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getTimeClock"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServTCA");
                r = objectMapper.readValue(returnstring, time_clock.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from time_clock where recid = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new time_clock(m);
                } else {
                    while(res.next()) {
        
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new time_clock(m, 
                        res.getString("emp_nbr"), 
                        res.getString("indate"),
                        res.getString("outdate"),
                        res.getString("intime"),
                        res.getString("outtime"),
                        res.getString("dept"),
                        res.getString("code_id"),
                        res.getString("intime_adj"),
                        res.getString("outtime_adj"),
                        res.getDouble("tothrs"),
                        res.getInt("recid"),
                        res.getString("login"),
                        res.getString("changed"),
                        res.getString("user_fld1"),
                        res.getString("user_fld2"),
                        res.getString("user_fld3"),
                        res.getString("comment"),
                        res.getString("code_orig"),
                        res.getString("who_changed"),
                        res.getString("ispaid"),
                        res.getString("checknbr")     
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new time_clock(m);
        }
        return r;
    }
   
    public static TimeClockSet getTimeClockSet(String[] x ) {
        TimeClockSet r = null;
        String[] m;
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "TimeClockSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServTCA");
                r = objectMapper.readValue(returnstring, TimeClockSet.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            
            
            time_clock tc = _getTimeClock(x[0], bscon, ps, res );
            emp_mstr em = _getEmployeeMstr(tc.emp_nbr(), bscon, ps, res);
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new TimeClockSet(m, em, tc);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new TimeClockSet(m);
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
   
    public static time_clock _getTimeClock(String code, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        
        time_clock r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from time_clock where recid = ? ;";
          ps = con.prepareStatement(sqlSelect); 
           ps.setString(1, code);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new time_clock(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new time_clock(m, 
                        res.getString("emp_nbr"), 
                        res.getString("indate"),
                        res.getString("outdate"),
                        res.getString("intime"),
                        res.getString("outtime"),
                        res.getString("dept"),
                        res.getString("code_id"),
                        res.getString("intime_adj"),
                        res.getString("outtime_adj"),
                        res.getDouble("tothrs"),
                        res.getInt("recid"),
                        res.getString("login"),
                        res.getString("changed"),
                        res.getString("user_fld1"),
                        res.getString("user_fld2"),
                        res.getString("user_fld3"),
                        res.getString("comment"),
                        res.getString("code_orig"),
                        res.getString("who_changed"),
                        res.getString("ispaid"),
                        res.getString("checknbr")     
                        );
                }
            }
            return r;
    }
   
    public static String[] addClockCode(clock_code x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addClockCode"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServTCA"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  clock_code where clc_code = ?";
        String sqlInsert = "insert into clock_code (clc_code, clc_desc, clc_payable, clc_syscode ) "
                        + " values (?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.clc_code);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.clc_code);
            psi.setString(2, x.clc_desc);
            psi.setString(3, x.clc_payable);
            psi.setString(4, x.clc_syscode);
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

    public static String[] updateClockCode(clock_code x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateClockCode"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServTCA"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update clock_code set clc_desc = ?, clc_payable = ?, clc_syscode = ? "
                        + " where clc_code = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.clc_desc);
            ps.setString(2, x.clc_payable);
            ps.setString(3, x.clc_syscode);
            ps.setString(4, x.clc_code);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static clock_code getClockCode(String[] x) {
        clock_code r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getClockCode"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServTCA");
                r = objectMapper.readValue(returnstring, clock_code.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new clock_code(m);
                return r;
            }
        }
        String sql = "select * from clock_code where clc_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new clock_code(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new clock_code(m, res.getString("clc_code"), res.getString("clc_desc"),
                        res.getString("clc_payable"), res.getString("clc_syscode"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new clock_code(m);
        }
        return r;
    }
  
    public static String[] deleteClockCode(clock_code x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteClockCode"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServTCA"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m;
        String sqlDelete = "delete from clock_code where clc_code = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
             ps.setString(1, x.clc_code);
             int rows = ps.executeUpdate();
             if (rows > 0) {
                m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess}; 
             } else {
                m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError}; 
             }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String getClockDetView(String[] keys) {
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
                
                res = st.executeQuery("SELECT t.tothrs as 't.tothrs', t.recid as 't.recid', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', " +
                           " e.emp_dept as 'e.emp_dept', t.code_id as 't.code_id', t.indate as 't.indate', t.intime as 't.intime', " +
                           " t.intime_adj as 't.intime_adj', t.outdate as 't.outdate', t.outtime as 't.outtime', " +
                           " t.outtime_adj as 't.outtime_adj', ispaid, checknbr FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr" +
                              " where t.emp_nbr >= " + "'" + keys[0] + "'" +
                              "and t.emp_nbr <= " + "'" + keys[1] + "'" +
                              "and t.indate >= " + "'" + keys[2] + "'" +
                               "and t.indate <= " + "'" + keys[3] + "'" + 
                               " order by e.emp_nbr, t.indate" +
                               ";" );                
               
                
                    while (res.next()) {                  
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("t.recid"));
                        rowArray.put(res.getString("t.emp_nbr"));
                        rowArray.put(res.getString("e.emp_lname"));
                        rowArray.put(res.getString("e.emp_fname"));
                        rowArray.put(res.getString("e.emp_dept"));
                        rowArray.put(res.getString("t.code_id"));
                        rowArray.put(res.getString("t.indate"));
                        rowArray.put(res.getString("t.intime"));
                        rowArray.put(res.getString("t.intime_adj"));
                        rowArray.put(res.getString("t.outdate"));
                        rowArray.put(res.getString("t.outtime"));
                        rowArray.put(res.getString("t.outtime_adj"));
                        rowArray.put(res.getString("t.tothrs"));
                        rowArray.put(res.getString("ispaid"));
                        rowArray.put(res.getString("checknbr"));
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
   
    
    
    public record clock_ctrl (String[] m, String clctrl_scan) {
        public clock_ctrl(String[] m) {
            this(m,"");
        }
    } 
    
    public record clock_code (String[] m, String clc_code, String clc_desc, String clc_payable, String clc_syscode) {
        public clock_code(String[] m) {
            this(m,"", "", "", "");
        }
    } 
    
    public record time_clock (String[] m, String emp_nbr,
        String indate, String outdate, String intime, String outtime, String dept, String code_id,
        String intime_adj, String outtime_adj, double tothrs, int recid, String login, String changed,
        String user_fld1, String user_fld2, String user_fld3, String comment, String code_orig,
        String who_changed, String ispaid, String checknbr
        ) {
        public time_clock(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", 0.0,
                    0, "", "", "", "", "", "", "", "", "",
                    "");
        }
    }
    
   public record TimeClockSet(String[] m, hrmData.emp_mstr em, time_clock tc) {
        public TimeClockSet(String[] m) {
            this (m, null, null);
        }
    }
    
    
}
