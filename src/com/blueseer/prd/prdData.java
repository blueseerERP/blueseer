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
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.pur.purData;
import com.blueseer.utl.BlueSeerUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            int rows = _addUpdateJobClock(x, con, ps, res);  
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
   
   public static int _addUpdateJobClock(job_clock x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from job_clock where jobc_planid = ? and jobc_op = ? and jobc_empnbr = ? and jobc_code = ? ;";
        String sqlInsert = "insert into job_clock (jobc_planid, jobc_op, jobc_qty, jobc_empnbr, " 
                        + " jobc_indate, jobc_outdate, jobc_intime, jobc_outtime, "
                        + " jobc_tothrs, jobc_code ) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.jobc_planid());
          ps.setInt(2, x.jobc_op());
          ps.setString(3, x.jobc_empnbr());
          ps.setString(4, x.jobc_code());
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.jobc_planid());
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
    
    
   public record job_clock (String[] m, String jobc_planid, int jobc_op, double jobc_qty, String jobc_empnbr,
        String jobc_indate, String jobc_outdate, String jobc_intime, String jobc_outtime, double jobc_tothrs,
        String jobc_code ) {
        public job_clock(String[] m) {
            this(m,"", 0, 0.00, "", null, null, "", "", 0.00, "");
        }
    }
    
}
