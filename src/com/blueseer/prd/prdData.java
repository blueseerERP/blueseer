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
package com.blueseer.prd;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.hrm.hrmData._getEmployeeMstr;
import com.blueseer.hrm.hrmData.emp_mstr;
import com.blueseer.inv.invData;
import com.blueseer.pur.purData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToInt;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;

/**
 *
 * @author terryva
 */
public class prdData {
   
    
   public static String[] addJobClock(job_clock x) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addJobClock"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServPRD"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
       String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
                
              try {  
              con = ds.getConnection();
              } catch (SQLException s) {
                  System.out.println(s);
              }
              
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
              
            }
            
            int rows = _addJobClock(x, con, ps, res);  
            
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
   
   public static int _addJobClock(job_clock x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from job_clock where jobc_planid = ? and jobc_op = ? and jobc_empnbr = ? and jobc_code = ? ;";
        String sqlInsert = "insert into job_clock (jobc_planid, jobc_op, jobc_qty, jobc_empnbr, " 
                        + " jobc_indate, jobc_outdate, jobc_intime, jobc_outtime, "
                        + " jobc_tothrs, jobc_code ) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setInt(1, x.jobc_planid());
          ps.setInt(2, x.jobc_op());
          ps.setString(3, x.jobc_empnbr());
          ps.setString(4, x.jobc_code());
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setInt(1, x.jobc_planid());
            ps.setInt(2, x.jobc_op());
            ps.setDouble(3, x.jobc_qty());
            ps.setString(4, x.jobc_empnbr());
            ps.setString(5, x.jobc_indate());
            ps.setString(6, x.jobc_outdate());
            ps.setString(7, x.jobc_intime());
            ps.setString(8, x.jobc_outtime());
            ps.setDouble(9, x.jobc_tothrs());
            ps.setString(10, x.jobc_code());
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
   public static String[] updateJobClock(job_clock x) {
        // method only updates job_clock records that are in 'open' condition....jobc_code = '01'
        // method only updates table fields that are relevant to clocking out
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateJobClock"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServPRD"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        int rows = 0;
        String sql = "update job_clock set jobc_outdate = ?, jobc_outtime = ?, jobc_qty = ?, " +
                " jobc_tothrs = ?, jobc_code = ? where jobc_planid = ? and jobc_op = ? and jobc_empnbr = ? " +
                " and jobc_code = '01' ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.jobc_outdate());
        ps.setString(2, x.jobc_outtime());
        ps.setDouble(3, x.jobc_qty());
        ps.setDouble(4, x.jobc_tothrs());
        ps.setString(5, x.jobc_code());
        ps.setInt(6, x.jobc_planid());
        ps.setInt(7, x.jobc_op());
        ps.setString(8, x.jobc_empnbr());
        rows = ps.executeUpdate();
        if (rows > 0) {
           m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};  
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1012)}; 
        }
        
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
   
   public static String[] updateJobClockRec(String recid, String indate, String outdate, String intime, String outtime, String tothrs) {
        // method only updates job_clock records that are in 'open' condition....jobc_code = '01'
        // method only updates table fields that are relevant to clocking out
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "updateJobClockRec"});
            list.add(new String[]{"param1",  recid});
            list.add(new String[]{"param2",  indate});
            list.add(new String[]{"param3",  outdate});
            list.add(new String[]{"param4",  intime});
            list.add(new String[]{"param5",  outtime});
            list.add(new String[]{"param6",  tothrs});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServPRD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        String[] m = new String[2];
        int rows = 0;
        String sql = "update job_clock set " +
                              "jobc_code = '77' " + "," +
                              "jobc_indate = ?, " +
                              "jobc_outdate = ?, " + 
                              "jobc_intime = ?, " + 
                              "jobc_outtime = ?, " +       
                              "jobc_tothrs = ? " + 
                              " where jobc_id = ? " + 
                              ";";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {       
        ps.setString(1, indate);
        ps.setString(2, outdate);
        ps.setString(3, intime);
        ps.setString(4, outtime);
        ps.setDouble(5, bsParseDouble(tothrs));
        ps.setString(6, recid);
        rows = ps.executeUpdate();
        if (rows > 0) {
           m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};  
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1012)}; 
        }
        
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
   
   public static job_clock getJobClock(String[] x) {
       // gets clockin jobs only
        job_clock r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getJobClock"});
            list.add(new String[]{"param1",  x[0]});
            list.add(new String[]{"param2",  x[1]});
            list.add(new String[]{"param3",  x[2]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServPRD");
                r = objectMapper.readValue(returnstring, job_clock.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from job_clock where jobc_planid = ? and jobc_op = ? and jobc_empnbr = ? and jobc_code = '01' ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
        ps.setString(3, x[2]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new job_clock(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new job_clock(m, res.getInt("jobc_planid"), res.getInt("jobc_op"), res.getDouble("jobc_qty"), res.getString("jobc_empnbr"),
                    res.getString("jobc_indate"), res.getString("jobc_outdate"), res.getString("jobc_intime"), res.getString("jobc_outtime"),
                    res.getDouble("jobc_tothrs"), res.getString("jobc_code"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new job_clock(m);
        }
        return r;
    }
    
   public static job_clock _getJobClock(String code, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        
        job_clock r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from job_clock where jobc_id = ? ;";
          ps = con.prepareStatement(sqlSelect); 
           ps.setString(1, code);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new job_clock(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new job_clock(m, res.getInt("jobc_planid"), res.getInt("jobc_op"), res.getDouble("jobc_qty"), res.getString("jobc_empnbr"),
                    res.getString("jobc_indate"), res.getString("jobc_outdate"), res.getString("jobc_intime"), res.getString("jobc_outtime"),
                    res.getDouble("jobc_tothrs"), res.getString("jobc_code"));
                    }
            }
            return r;
    }
    
   // miscellaneous methods
   public static String getPrdRptPickerData(String[] keys) {
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
                 
                int i = 0;
                if (keys[0].equals("poByOrdDateRange")) {
                res = st.executeQuery(" select po_nbr, po_vend, vd_name, po_site, po_rmks, po_ord_date, po_due_date, po_status, " +
                        " sum(pod_ord_qty  * pod_netprice) as 'total' " +
                        " FROM  po_mstr inner join pod_mstr on pod_nbr = po_nbr " +
                        " inner join vd_mstr on vd_addr = po_vend " +
                        " where po_ord_date >= " + "'" + keys[1] + "'" + 
                        " and po_ord_date <= " + "'" + keys[2] + "'" +
                        " group by po_nbr, po_vend, vd_name, po_site, po_rmks, po_ord_date, po_due_date, po_status " +
                        " order by po_nbr desc ;");                  
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("po_nbr"));
                            rowArray.put(res.getString("po_vend"));
                            rowArray.put(res.getString("vd_name"));
                            rowArray.put(res.getString("po_rmks"));
                            rowArray.put(res.getString("po_ord_date"));
                            rowArray.put(res.getString("po_due_date"));
                            rowArray.put(res.getString("po_status"));
                            rowArray.put(currformat(res.getString("total")));
                            jsonarray.put(rowArray);

                    } 
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
    
   public static String getSerialBrowseView(String[] keys) {
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
                
                keys[2] = (keys[2].isBlank()) ? bsmf.MainFrame.lowchar : keys[2]; 
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.hichar : keys[3];
                
                if (keys[6].equals("ALL")) {
                    res = st.executeQuery("SELECT tr_id, tr_op, tr_cost, tr_item, tr_type, tr_wh, tr_loc, tr_qty, tr_base_qty, tr_uom, tr_eff_date, tr_timestamp, tr_ref, tr_serial, tr_program , tr_userid, tr_lot " +
                        " FROM  tran_mstr  " +
                        " where tr_eff_date >= " + "'" + keys[0]  + "'" + 
                        " AND tr_eff_date <= " + "'" + keys[1] + "'" + 
                        " AND tr_serial >= " + "'" + keys[2] + "'" + 
                        " AND tr_serial <= " + "'" + keys[3] + "'" + 
                        " AND tr_item >= " + "'" + keys[4] + "'" + 
                        " AND tr_item <= " + "'" + keys[5] + "'" + 
                         " order by tr_ent_date desc ;");   
                } else {
                    res = st.executeQuery("SELECT tr_id, tr_op, tr_cost, tr_item, tr_type, tr_wh, tr_loc, tr_qty, tr_base_qty, tr_uom, tr_eff_date, tr_timestamp, tr_ref, tr_serial, tr_program , tr_userid, tr_lot " +
                        " FROM  tran_mstr  " +
                        " where tr_eff_date >= " + "'" + keys[0]  + "'" + 
                        " AND tr_eff_date <= " + "'" + keys[1] + "'" + 
                        " AND tr_serial >= " + "'" + keys[2] + "'" + 
                        " AND tr_serial <= " + "'" + keys[3] + "'" + 
                        " AND tr_item >= " + "'" + keys[4] + "'" + 
                        " AND tr_item <= " + "'" + keys[5] + "'" +          
                        " AND tr_type = " + "'" + keys[6] + "'" +
                               
                         " order by tr_ent_date desc ;");    
                }
                    while (res.next()) {
                   
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("tr_id"));
                        rowArray.put(res.getString("tr_serial"));
                        rowArray.put(res.getString("tr_item"));
                        rowArray.put(res.getString("tr_type"));
                        rowArray.put(bsNumber(res.getDouble("tr_qty")));
                        rowArray.put(res.getString("tr_uom"));
                        rowArray.put(res.getString("tr_eff_date"));
                        rowArray.put(res.getString("tr_timestamp"));
                        rowArray.put(res.getString("tr_lot"));
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
    
   public static String getSerialBrowseViewDet(String key) {
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
                
                res = st.executeQuery("SELECT tr_id, tr_op, tr_cost,  tr_item, tr_type, tr_wh, tr_loc, tr_qty, tr_base_qty, tr_uom, tr_eff_date, tr_timestamp, tr_ref, tr_serial, tr_program , tr_userid, tr_lot " +
                        " FROM  tran_mstr  " +
                        " where tr_lot = " + "'" + key  + "'" + 
                        " and tr_serial <> " + "'" + key  + "'" + // prevent cyclic grab
                         " order by tr_id desc ;"); 
                    while (res.next()) {                  
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("tr_id"));
                        rowArray.put(res.getString("tr_serial"));
                        rowArray.put(res.getString("tr_item"));
                        rowArray.put(res.getString("tr_type"));
                        rowArray.put(bsNumber(res.getDouble("tr_qty")));
                        rowArray.put(res.getString("tr_uom"));
                        rowArray.put(res.getString("tr_eff_date"));
                        rowArray.put(res.getString("tr_timestamp"));
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
   
   public static String getTransBrowseView(String[] keys) {
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
                
                if (keys[4].equals("ALL")) {
                   res = st.executeQuery("SELECT tr_id, tr_op, tr_cost,  tr_item, tr_type, tr_wh, tr_loc, tr_qty, tr_base_qty, tr_uom, tr_eff_date, tr_timestamp, tr_ref, tr_serial, tr_program , tr_userid " +
                        " FROM  tran_mstr  " +
                        " where tr_eff_date >= " + "'" + keys[0]  + "'" + 
                        " AND tr_eff_date <= " + "'" + keys[1] + "'" + 
                        " AND tr_item >= " + "'" + keys[2] + "'" + 
                        " AND tr_item <= " + "'" + keys[3] + "'" + 
                         " order by tr_ent_date desc ;");    
                 } else {
                    res = st.executeQuery("SELECT tr_id, tr_op, tr_cost,  tr_item, tr_type, tr_wh, tr_loc, tr_qty, tr_base_qty, tr_uom, tr_eff_date, tr_timestamp, tr_ref, tr_serial, tr_program , tr_userid " +
                        " FROM  tran_mstr  " +
                        " where tr_eff_date >= " + "'" + keys[0]  + "'" + 
                        " AND tr_eff_date <= " + "'" + keys[1] + "'" + 
                        " AND tr_item >= " + "'" + keys[2] + "'" + 
                        " AND tr_item <= " + "'" + keys[3] + "'" + 
                        " AND tr_type = " + "'" + keys[4] + "'" +
                         " order by tr_ent_date desc ;");     
                 }
                    while (res.next()) {                  
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("tr_id"));
                        rowArray.put(res.getString("tr_item"));
                        rowArray.put(res.getString("tr_type"));
                        rowArray.put(bsNumber(res.getDouble("tr_qty")));
                        rowArray.put(res.getString("tr_uom"));
                        rowArray.put(bsNumber(res.getDouble("tr_base_qty")));
                        rowArray.put(res.getString("tr_op"));
                        rowArray.put(res.getString("tr_eff_date"));
                        rowArray.put(res.getString("tr_timestamp"));
                        rowArray.put(res.getString("tr_ref"));
                        rowArray.put(res.getString("tr_serial"));
                        rowArray.put(res.getString("tr_program"));
                        rowArray.put(res.getString("tr_userid"));
                        rowArray.put(res.getString("tr_cost"));
                        rowArray.put(res.getString("tr_wh"));
                        rowArray.put(res.getString("tr_loc"));
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
   
   public static String getJobBrowseView(String[] keys) {
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
                
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.lowchar : keys[3];  //fromitem
                keys[4] = (keys[4].isBlank()) ? bsmf.MainFrame.hichar : keys[4];  //toitem
                
                if (! keys[0].isBlank()) {
                     
                    if (keys[5].equals("1")) {
                     res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_order, " +
                         "plan_qty_sched, plan_qty_comp, plan_line, plan_cell, plan_date_sched, plan_date_create, plan_status " +
                        " FROM  plan_mstr " +
                        " where plan_nbr = " + "'" + keys[0] + "'" + 
                        " order by plan_nbr;");    
                    } else {
                    res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plo_op, plo_operator, plo_operatorname, plo_cell, " +
                         "plo_qty, plo_qty_comp, plo_date, plo_status, " +
                         " jobc_id, jobc_empnbr, jobc_qty, coalesce(jobc_tothrs,0) as jobc_tothrs, jobc_code,  " +
                         " emp_lname, emp_fname " +   
                        " FROM  plan_operation " +
                        " inner join plan_mstr on plan_nbr = plo_parent " +
                        " left outer join job_clock on jobc_planid = plo_parent and jobc_op = plo_op " + 
                        " left outer join emp_mstr on emp_nbr = jobc_empnbr " +   
                        " where plo_parent = " + "'" + keys[0] + "'" + 
                        " order by plo_op;"); 
                    }
  
                 } else {
                    
                    if (keys[5].equals("1")) {
                     res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_order, " +
                         "plan_qty_sched, plan_qty_comp, plan_line, plan_cell, plan_date_sched, plan_date_create, plan_status " +
                        " FROM  plan_mstr " +
                        " where " +
                        " (( plan_date_create >= " + "'" + keys[1] + "'" + 
                        " AND plan_date_create <= " + "'" + keys[2] + "'" + " ) OR plan_date_create is null )" + 
                        " AND plan_item >= " + "'" + keys[3] + "'" + 
                        " AND plan_item <= " + "'" + keys[4] + "'" + 
                        " order by plan_nbr;");     
                    } else { 
                    res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plo_op, plo_operator, plo_operatorname, plo_cell, " +
                         "plo_qty, plo_qty_comp, plo_date, plo_status, " +
                         " jobc_id, jobc_empnbr, jobc_qty, coalesce(jobc_tothrs,0) as jobc_tothrs, jobc_code,  " +
                         " emp_lname, emp_fname " +  
                        " FROM  plan_operation " +
                        " inner join plan_mstr on plan_nbr = plo_parent " +
                        " left outer join job_clock on jobc_planid = plo_parent and jobc_op = plo_op " + 
                        " left outer join emp_mstr on emp_nbr = jobc_empnbr " +    
                        " where " +
                        " (( plo_date >= " + "'" + keys[1] + "'" + 
                        " AND plo_date <= " + "'" + keys[2] + "'" + " ) OR plo_date is null )" + 
                        " AND plan_item >= " + "'" + keys[3] + "'" + 
                        " AND plan_item <= " + "'" + keys[4] + "'" + 
                      //  " AND plan_is_sched = " + "'1' "  +
                        " order by plan_nbr, plo_op;");    
                    }
                 }
                String imagevar;
                String clockstatus = "";
                String plostatus = "";
                String clockid = "";
                String operatorname = "";
                
                    while (res.next()) {
                        
                    if (res.getString("plan_type").equals("SRVC")) {
                        imagevar = "select";
                    } else {
                        imagevar = "void";
                    }
                    
                    if (! keys[5].equals("1")) {
                        if (res.getString("jobc_code") == null) {
                            clockstatus = "n/c";
                        } else {
                            clockstatus = (res.getString("jobc_code").equals("01")) ? "in" : "out";
                        }
                        if (res.getString("plo_status").isBlank()) {
                            plostatus = "unscheduled";
                        } else {
                            plostatus = res.getString("plo_status");
                        }

                        if (res.getString("jobc_id") == null) {
                            clockid = "0";
                        } else {
                            clockid = res.getString("jobc_id");
                        }

                        if (res.getString("emp_lname") != null) {
                            operatorname = res.getString("emp_lname") + ", " + res.getString("emp_fname");
                        } else {
                            operatorname = "";
                        }
                    }    
                        
                    if (keys[5].equals("1")) {
                        JSONArray rowArray = new JSONArray();                         
                        rowArray.put(imagevar);                
                        rowArray.put("detail");
                        rowArray.put("clock");
                        rowArray.put(res.getString("plan_nbr"));
                        rowArray.put(res.getString("plan_type"));
                        rowArray.put(res.getString("plan_item"));
                        rowArray.put(res.getString("plan_order"));
                        rowArray.put(res.getString("plan_cell"));    
                        rowArray.put(res.getString("plan_date_sched"));
                        rowArray.put(bsNumber(res.getDouble("plan_qty_sched")));
                        rowArray.put(bsNumber(res.getDouble("plan_qty_comp")));                        
                        rowArray.put(0);
                        rowArray.put(res.getString("plan_status"));
                        jsonarray.put(rowArray);
                    } else {
                       JSONArray rowArray = new JSONArray();                        
                        rowArray.put(imagevar);
                        rowArray.put(res.getString("plan_nbr"));
                        rowArray.put(res.getString("plan_type"));
                        rowArray.put(res.getString("plan_item"));
                        rowArray.put(res.getString("plo_op"));
                        rowArray.put(operatorname);
                        rowArray.put(res.getString("plo_cell"));
                        rowArray.put(res.getString("plo_date"));
                        rowArray.put(bsNumber(res.getDouble("plo_qty")));
                        rowArray.put(bsNumber(res.getDouble("plo_qty_comp")));
                        rowArray.put(bsNumber(res.getDouble("jobc_tothrs")));
                        rowArray.put(plostatus);
                        rowArray.put(clockstatus);
                        rowArray.put(clockid);
                        jsonarray.put(rowArray); 
                    }   
                    
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
   
   public static String getJobBrowseViewDet(String dataview, String key) {
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
                
                if (dataview.equals("clock")) {
                 res = st.executeQuery("SELECT * from job_clock   " +
                        " inner join emp_mstr on emp_nbr = jobc_empnbr " +
                        " where jobc_planid = " + "'" + key + "'" + 
                         " order by jobc_op ;"); 
                 while (res.next()) {                       
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("jobc_op"));
                        rowArray.put(res.getString("jobc_id"));
                        rowArray.put(res.getString("emp_lname") + ", " + res.getString("emp_fname"));
                        rowArray.put(res.getString("jobc_indate"));
                        rowArray.put(res.getString("jobc_intime"));
                        rowArray.put(res.getString("jobc_outdate"));
                        rowArray.put(res.getString("jobc_outtime"));
                        rowArray.put(res.getString("jobc_code"));
                        jsonarray.put(rowArray);
                   }                 
                } else {
                  res = st.executeQuery("SELECT * from plan_operation   " +
                        " where plo_parent = " + "'" + key + "'" + 
                         " order by plo_op ;"); 
                  while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("plo_op"));
                        rowArray.put(res.getString("plo_desc"));
                        rowArray.put(res.getString("plo_operatorname"));
                        rowArray.put(res.getString("plo_cell"));
                        rowArray.put(bsNumber(res.getDouble("plo_qty")));
                        rowArray.put(bsNumber(res.getDouble("plo_qty_comp")));
                        rowArray.put(res.getString("plo_date"));
                        rowArray.put(res.getString("plo_status"));
                        jsonarray.put(rowArray);
                   }
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
   
   public static String getProdSchedBrowseView(String[] keys) {
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
                
                keys[2] = (keys[2].isBlank()) ? bsmf.MainFrame.lowchar : keys[2];
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.lowchar : keys[3];
                keys[4] = (keys[4].isBlank()) ? bsmf.MainFrame.lowchar : keys[4];
                keys[5] = (keys[5].isBlank()) ? bsmf.MainFrame.lowchar : keys[5];
                
                res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_qty_req, plan_qty_comp, "
                         + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + keys[0] + "'" + 
                        " AND plan_date_due <= " + "'" + keys[1] + "'" + 
                        " AND plan_item >= " + "'" + keys[2] + "'" + 
                        " AND plan_item <= " + "'" + keys[3] + "'" + 
                         " AND plan_cell >= " + "'" + keys[4] + "'" + 
                        " AND plan_cell <= " + "'" + keys[5] + "'" + 
                        " AND plan_is_sched = " + "'1' "  +
                        " order by plan_item, plan_date_due;");
                    String status = "";
                    
                    while (res.next()) {
                    if (res.getInt("plan_status") == 0) {
                        status = getGlobalProgTag("open");
                    } else if (res.getInt("plan_status") == 1) {
                        status = getGlobalProgTag("closed");
                    } else {
                        status = getGlobalProgTag("void"); // -1
                    }    
                        
                    JSONArray rowArray = new JSONArray();
                        rowArray.put("detail");
                        rowArray.put(res.getString("plan_nbr"));
                        rowArray.put(res.getString("plan_item"));
                        rowArray.put(res.getString("plan_date_due"));
                        rowArray.put(res.getString("plan_type"));
                        rowArray.put(res.getString("plan_is_sched"));
                        rowArray.put(res.getString("plan_cell"));                        
                        rowArray.put(bsNumber(res.getDouble("plan_qty_sched")));
                        rowArray.put(bsNumber(res.getDouble("plan_qty_req")));
                        rowArray.put(bsNumber(res.getDouble("plan_qty_comp")));
                        rowArray.put(res.getString("plan_date_sched"));
                        rowArray.put(res.getString("plan_order"));
                        rowArray.put(res.getString("plan_line"));
                        rowArray.put(status);
                        rowArray.put("print");
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
   
   public static String getProdSchedBrowseViewDet(String key) {
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
               
                 res = st.executeQuery("SELECT * from pland_mstr   " +
                        " where pland_parent = " + "'" + key + "'" + 
                         " order by pland_id desc ;");                 
                 while (res.next()) {                       
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("pland_id"));
                        rowArray.put(res.getString("pland_parent"));
                        rowArray.put(res.getString("pland_item"));
                        rowArray.put(res.getString("pland_op"));
                        rowArray.put(res.getString("pland_cell"));
                        rowArray.put(res.getString("pland_date"));
                        rowArray.put(res.getString("pland_ref"));
                        rowArray.put(res.getString("pland_qty"));
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
   
   public static String getProdDetBrowseView(String[] keys) {
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
                
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.lowchar : keys[3];
                keys[4] = (keys[4].isBlank()) ? bsmf.MainFrame.lowchar : keys[4];
                keys[5] = (keys[5].isBlank()) ? bsmf.MainFrame.lowchar : keys[5];
                keys[6] = (keys[6].isBlank()) ? bsmf.MainFrame.lowchar : keys[6];
                
                 if (keys[0].equals("ALL")) {    
                   res = st.executeQuery("SELECT tr_id, tr_item,  tr_cost, tr_type, it_code, tr_qty, " +
                        " tr_op, tr_eff_date, tr_assy_date, tr_ref, tr_actcell, tr_cost, " +
                         " tr_timestamp, tr_userid, tr_pack, tr_pack_date " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_item " +
                        " left outer join itemr_cost on itr_item = tr_item and itr_op = tr_op and itr_routing = it_wf and itr_set = 'standard' and itr_site = it_site " +
                         " left outer join item_cost on itc_item = tr_item and itc_set = 'standard' and itc_site = it_site " +
                        " where  tr_eff_date   >= " + "'" + keys[1] + "'" + 
                        " AND  tr_eff_date   <= " + "'" + keys[2] + "'" + 
                        " AND tr_item >= " + "'" + keys[3] + "'" + 
                        " AND tr_item <= " + "'" + keys[4] + "'" + 
                         " AND tr_actcell >= " + "'" + keys[5] + "'" + 
                        " AND tr_actcell <= " + "'" + keys[6] + "'" + 
                        " AND ( tr_type = 'ISS-PRD' or tr_type = 'RCT-FG') " + 
                        " order by tr_id desc ;");    
                  } else {
                      res = st.executeQuery("SELECT tr_id, tr_item,  tr_cost, tr_type, it_code, tr_qty, " +
                        " tr_op, tr_eff_date, tr_assy_date, tr_ref, tr_actcell, tr_cost, " +
                         " tr_timestamp, tr_userid, tr_pack, tr_pack_date " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_item " +
                        " left outer join itemr_cost on itr_item = tr_item and itr_op = tr_op and itr_routing = it_wf and itr_set = 'standard' and itr_site = it_site " +
                         " left outer join item_cost on itc_item = tr_item and itc_set = 'standard' and itc_site = it_site " +
                        " where  tr_eff_date   >= " + "'" + keys[1] + "'" + 
                        " AND  tr_eff_date   <= " + "'" + keys[2] + "'" + 
                        " AND tr_item >= " + "'" + keys[3] + "'" + 
                        " AND tr_item <= " + "'" + keys[4] + "'" + 
                         " AND tr_actcell >= " + "'" + keys[5] + "'" + 
                        " AND tr_actcell <= " + "'" + keys[6] + "'" + 
                        " AND tr_type = " + "'" + keys[0] + "'" + 
                        " order by tr_id desc ;");    
                  }
                    String status = "";
                    
                    while (res.next()) {
                    JSONArray rowArray = new JSONArray();
                        rowArray.put(res.getString("tr_id"));
                        rowArray.put(res.getString("tr_item"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("tr_type"));                      
                        rowArray.put(bsNumber(res.getDouble("tr_qty")));
                        rowArray.put(bsNumber(res.getDouble("tr_cost")));
                        rowArray.put(res.getString("tr_op"));
                        rowArray.put(res.getString("tr_eff_date"));
                        rowArray.put(res.getString("tr_actcell"));
                        rowArray.put(res.getString("tr_userid"));
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
   
   
   
   public static String[] getJobClockInTime(int plan, int op, String empnbr) {
           // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getJobClockInTime"});
            list.add(new String[]{"param1",  bsNumber(plan)});
            list.add(new String[]{"param2",  bsNumber(op)});
            list.add(new String[]{"param3",  empnbr});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServPRD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        String[] timeinfo = new String[]{"",""};
        String sql = "select jobc_indate, jobc_intime from job_clock where jobc_planid = ? and jobc_op = ? and jobc_empnbr = ? and jobc_code = '01';";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, plan);
        ps.setInt(2, op);
        ps.setString(3, empnbr);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               timeinfo[0] = res.getString("jobc_indate");
               timeinfo[1] = res.getString("jobc_intime");           
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return timeinfo;
    }
   
   public static ArrayList<String[]> getJobClockHistory(String now) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getJobClockHistory"});
            list.add(new String[]{"param1",  now});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServPRD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
       ArrayList<String[]> x = new ArrayList<String[]>();
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
             res = st.executeQuery("select * from job_clock inner join emp_mstr on emp_nbr = jobc_empnbr where jobc_indate = " + "'" + now + "'" +
                     " or jobc_outdate = " + "'" + now + "'"
                     + " order by jobc_planid ;");
           while (res.next()) {
               String[] w = new String[]{
                    res.getString("jobc_planid"),
                    res.getString("jobc_op"),
                    res.getString("jobc_empnbr"),
                    res.getString("jobc_qty"),
                    res.getString("jobc_indate"),
                    res.getString("jobc_intime"),
                    res.getString("jobc_outdate"),
                    res.getString("jobc_outtime"),
                    res.getString("jobc_code"),
                    res.getString("emp_lname") + ", " + res.getString("emp_fname")
               };
               x.add(w);
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
      return x;
  }
   
   public static ArrayList<String[]> getJobClockDetail(int plan) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getJobClockDetail"});
            list.add(new String[]{"param1",  bsNumber(plan)});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServPRD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
      ArrayList<String[]> x = new ArrayList<String[]>();
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
             res = st.executeQuery("select jobc_planid, jobc_op, jobc_empnbr, jobc_qty, jobc_indate, jobc_intime, jobc_outdate, jobc_outtime, jobc_tothrs, jobc_code, emp_lname, emp_fname, emp_rate from job_clock inner join emp_mstr on emp_nbr = jobc_empnbr where jobc_planid = " + "'" + plan + "'" 
                     + " order by jobc_indate ;");
           while (res.next()) {
               String[] w = new String[]{
                    res.getString("jobc_planid"),
                    res.getString("jobc_op"),
                    res.getString("jobc_empnbr"),
                    res.getString("emp_lname") + ", " + res.getString("emp_fname"),
                    res.getString("emp_rate"),
                    res.getString("jobc_qty"),
                    res.getString("jobc_indate"),
                    res.getString("jobc_intime"),
                    res.getString("jobc_outdate"),
                    res.getString("jobc_outtime"),
                    res.getString("jobc_tothrs"), 
                    res.getString("jobc_code")
               };
               x.add(w);
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
      return x;
  }
   
   public static int updatePlanOPNotes(String job, String op, String notes) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            String notes_encoded = "";
            try {                
                notes_encoded = URLEncoder.encode(notes, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException ex) {
                bslog(ex);
            }
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updatePlanOPNotes"});
            list.add(new String[]{"param1", job});
            list.add(new String[]{"param2", op});
            list.add(new String[]{"param3", notes_encoded});
            try {
                return jsonToInt(sendServerPost(list, "", null, "dataServPRD")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0;
            }
        }
        int x = 0;
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
                 st.executeUpdate("update plan_operation set plo_notes = " + "'" + notes + "'" +
                         " where plo_parent = " + "'" + job + "'" +
                         " and plo_op = " + "'" + op + "'" + ";");
                
            } 
            catch (SQLException s) {
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
        return x;
    }

   public static int updatePlanOPOperator(String job, String op, String operator, String operatorname) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updatePlanOPOperator"});
            list.add(new String[]{"param1", job});
            list.add(new String[]{"param2", op});
            list.add(new String[]{"param3", operator});
            list.add(new String[]{"param4", operatorname});
            try {
                return jsonToInt(sendServerPost(list, "", null, "dataServPRD")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0;
            }
        }
        int x = 0;
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
                 st.executeUpdate("update plan_operation set plo_operator = " + "'" + operator + "'" + ", "
                 + " plo_operatorname = " + "'" + operatorname + "'" +
                         " where plo_parent = " + "'" + job + "'" +
                         " and plo_op = " + "'" + op + "'" + ";");
                
            } 
            catch (SQLException s) {
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
        return x;
    }

   public static int updatePlanOPDate(String job, String op, String scheddate) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updatePlanOPDate"});
            list.add(new String[]{"param1", job});
            list.add(new String[]{"param2", op});
            list.add(new String[]{"param3", scheddate});
            try {
                return jsonToInt(sendServerPost(list, "", null, "dataServPRD")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0;
            }
        } 
       int x = 0;
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
                 st.executeUpdate("update plan_operation set plo_date = " + "'" + scheddate + "'" +
                         " where plo_parent = " + "'" + job + "'" +
                         " and plo_op = " + "'" + op + "'" + ";");
                
            } 
            catch (SQLException s) {
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
        return x;
    }

   public static int updatePlanOPDesc(String job, String op, String desc) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updatePlanOPDesc"});
            list.add(new String[]{"param1", job});
            list.add(new String[]{"param2", op});
            list.add(new String[]{"param3", desc});
            try {
                return jsonToInt(sendServerPost(list, "", null, "dataServPRD")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0;
            }
        } 
        int x = 0;
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
                 st.executeUpdate("update plan_operation set plo_desc = " + "'" + desc + "'" + 
                         " where plo_parent = " + "'" + job + "'" +
                         " and plo_op = " + "'" + op + "'" + ";");
                
            } 
            catch (SQLException s) {
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
        return x;
    }
   
   public static int getPlanOpLastOp(String jobid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getPlanOpLastOp"});
            list.add(new String[]{"param1", jobid});
            try {
                return jsonToInt(sendServerPost(list, "", null, "dataServPRD")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0;
            }
        }
          int x = 0;
          
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
                 res = st.executeQuery("select plo_op from plan_operation where plo_parent = " + "'" + jobid + "'" 
                         + " order by plo_op ;");
               while (res.next()) {
                   x = res.getInt("plo_op");
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
          return x;
      }
   
   public static int addPlanOpDet(String job, String op, String datatype, String item, String itemdesc, double qty, double cost, String operator, int consumable) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "addPlanOpDet"});
            list.add(new String[]{"param1", job});
            list.add(new String[]{"param2", op});
            list.add(new String[]{"param3", datatype});
            list.add(new String[]{"param4", item});
            list.add(new String[]{"param5", itemdesc});
            list.add(new String[]{"param6", bsNumber(qty)});
            list.add(new String[]{"param7", bsNumber(cost)});
            list.add(new String[]{"param8", operator});
            list.add(new String[]{"param9", bsNumber(consumable)});
            try {
                return jsonToInt(sendServerPost(list, "", null, "dataServPRD")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0;
            }
        }
        int x = 0;
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
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
                  if (dbtype.equals("sqlite")) { 
                    st.executeUpdate("insert into plan_opdet (plod_parent, plod_op, plod_type, plod_item, plod_itemdesc, plod_qty, plod_cost, plod_operator, plod_date, plod_time, plod_consumable) values ( "
                            + "'" + job + "'" + "," 
                            + "'" + op + "'" + "," 
                            + "'" + datatype + "'" + "," 
                            + "'" + item + "'" + ","   
                            + "'" + itemdesc + "'" + ","        
                            + "'" + qty + "'" + ","
                            + "'" + cost + "'" + ","
                            + "'" + operator + "'" + ","
                            + "'" + clockdate + "'" + ","  
                            + "'" + clocktime + "'"  + ","
                            + "'" + consumable + "'"
                            +  ")"
                            + ";");
                  } else {
                     st.executeUpdate("insert into plan_opdet (plod_parent, plod_op, plod_type, plod_item, plod_itemdesc, plod_qty, plod_cost, plod_operator, plod_date, plod_time, plod_consumable) values ( "
                            + "'" + job + "'" + "," 
                            + "'" + op + "'" + "," 
                            + "'" + datatype + "'" + "," 
                            + "'" + item + "'" + ","
                            + "'" + itemdesc + "'" + ","
                            + "'" + qty + "'" + ","
                            + "'" + cost + "'" + ","
                            + "'" + operator + "'" + ","
                            + "'" + clockdate + "'" + ","  
                            + "'" + clocktime + "'"  + ","
                            + "'" + consumable + "'"       
                            +  ")"
                            + ";" , Statement.RETURN_GENERATED_KEYS); 
                  }
                res = st.getGeneratedKeys();
                while (res.next()) {
                    x = res.getInt(1);
                }
                res.close();   
                
            } // if proceed
            catch (SQLException s) {
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
        return x;
    }
   
   public static void deletePlanOpDet(String id) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "deletePlanOpDet"});
            list.add(new String[]{"param1", id});
            try {
                sendServerPost(list, "", null, "dataServPRD");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
        try {

                Connection con = DriverManager.getConnection(url + db, user, pass);
                Statement st = con.createStatement();
                ResultSet res = null;
                try {

                    int i = st.executeUpdate("delete from plan_opdet where plod_id = " + "'" + id + "'" + " ;");
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
    }
   
   public static ArrayList<String[]> getPlanOpDet(String job) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPlanOpDet"});
            list.add(new String[]{"param1",  job});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServPRD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
      ArrayList<String[]> x = new ArrayList<String[]>();
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
             res = st.executeQuery("select * from plan_opdet where plod_parent = " + "'" + job + "'" 
                     + " order by plod_op ;");
           while (res.next()) {
               String[] w = new String[]{
                   res.getString("plod_id"),
                    res.getString("plod_parent"),
                    res.getString("plod_op"),
                    res.getString("plod_type"),
                    res.getString("plod_itemdesc"),
                    res.getString("plod_qty"),
                    res.getString("plod_cost"),
                    res.getString("plod_operator"),
                    res.getString("plod_date"),
                    res.getString("plod_time"),
                    res.getString("plod_consumable")
               };
               x.add(w);
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
      return x;
   }
   
   public static ArrayList<String[]> getPlanOpDet(String job, String op) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPlanOpDetX"});
            list.add(new String[]{"param1",  job});
            list.add(new String[]{"param2",  op});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServPRD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }  
      ArrayList<String[]> x = new ArrayList<String[]>();
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
             res = st.executeQuery("select * from plan_opdet where plod_parent = " + "'" + job + "'" 
                     + " and plod_op = " + "'" + op + "'"
                     + " order by plod_type ;");
           while (res.next()) {
               String[] w = new String[]{
                   res.getString("plod_id"),
                    res.getString("plod_parent"),
                    res.getString("plod_op"),
                    res.getString("plod_type"),
                    res.getString("plod_itemdesc"),
                    res.getString("plod_qty"),
                    res.getString("plod_cost"),
                    res.getString("plod_operator"),
                    res.getString("plod_date"),
                    res.getString("plod_time")
               };
               x.add(w);
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
      return x;
   }
   
   public static JobClockSet getJobClockSet(String[] x ) {
        JobClockSet r = null;
        String[] m;
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getJobClockSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServPRD");
                r = objectMapper.readValue(returnstring, JobClockSet.class); 
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
            
            
            job_clock jc = _getJobClock(x[0], bscon, ps, res );
            emp_mstr em = _getEmployeeMstr(jc.jobc_empnbr(), bscon, ps, res);
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new JobClockSet(m, em, jc);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new JobClockSet(m);
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
    
   
   public record job_clock (String[] m, int jobc_planid, int jobc_op, double jobc_qty, String jobc_empnbr,
        String jobc_indate, String jobc_outdate, String jobc_intime, String jobc_outtime, double jobc_tothrs,
        String jobc_code ) {
        public job_clock(String[] m) {
            this(m, 0, 0, 0.00, "", null, null, "", "", 0.00, "");
        }
    }
    
   public record JobClockSet(String[] m, emp_mstr em, job_clock jc) {
        public JobClockSet(String[] m) {
            this (m, null, null);
        }
    }
}
