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
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.pur.purData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
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

/**
 *
 * @author terryva
 */
public class prdData {
   
    
   public static String[] addJobClock(job_clock x) {
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
   
   public static job_clock getJobClock(String[] x) {
       // gets clockin jobs only
        job_clock r = null;
        String[] m = new String[2];
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
    
   // miscellaneous methods
   public static String[] getJobClockInTime(int plan, int op, String empnbr) {
           // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
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
             res = st.executeQuery("select * from job_clock where jobc_indate = " + "'" + now + "'" +
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
   
   public static int addPlanOpDet(String job, String op, String datatype, String item, double qty, double cost, String operator) {
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
                    st.executeUpdate("insert into plan_opdet values ( "
                            + "'" + job + "'" + "," 
                            + "'" + op + "'" + "," 
                            + "'" + datatype + "'" + "," 
                            + "'" + item + "'" + ","        
                            + "'" + qty + "'" + ","
                            + "'" + cost + "'" + ","
                            + "'" + operator + "'" + ","
                            + "'" + clockdate + "'" + ","  
                            + "'" + clocktime + "'" + ","        
                            +  ")"
                            + ";");
                  } else {
                     st.executeUpdate("insert into plan_opdet values ( "
                            + "'" + job + "'" + "," 
                            + "'" + op + "'" + "," 
                            + "'" + datatype + "'" + "," 
                            + "'" + item + "'" + ","        
                            + "'" + qty + "'" + ","
                            + "'" + cost + "'" + ","
                            + "'" + operator + "'" + ","
                            + "'" + clockdate + "'" + ","  
                            + "'" + clocktime + "'" + ","        
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;
    }

   public static void deletePlanOpDet(String id) {
        try {

                Connection con = DriverManager.getConnection(url + db, user, pass);
                Statement st = con.createStatement();
                ResultSet res = null;
                try {

                    int i = st.executeUpdate("delete from plan_opdet where plod_id = " + "'" + id + "'" + " ;");
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
    }
    
    
   public record job_clock (String[] m, int jobc_planid, int jobc_op, double jobc_qty, String jobc_empnbr,
        String jobc_indate, String jobc_outdate, String jobc_intime, String jobc_outtime, double jobc_tothrs,
        String jobc_code ) {
        public job_clock(String[] m) {
            this(m, 0, 0, 0.00, "", null, null, "", "", 0.00, "");
        }
    }
    
}
