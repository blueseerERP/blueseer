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
package com.blueseer.far;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author terryva
 */
public class farData {
    
    
    public static String[] addArMstr(ar_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _addArMstr(x, con, ps, res);  
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
        
    private static int _addArMstr(ar_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ar_mstr where ar_nbr = ?";
        String sqlInsert = "insert into ar_mstr (ar_nbr, ar_cust, ar_amt, ar_base_amt, ar_type, "
                        + " ar_curr, ar_base_curr, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_paiddate, ar_acct, ar_cc, "
                        + "ar_status, ar_bank, ar_site ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.ar_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.ar_nbr);
            ps.setString(2, x.ar_cust);
            ps.setString(3, x.ar_amt);
            ps.setString(4, x.ar_base_amt);
            ps.setString(5, x.ar_type);
            ps.setString(6, x.ar_curr);
            ps.setString(7, x.ar_base_curr);
            ps.setString(8, x.ar_ref);
            ps.setString(9, x.ar_rmks);
            ps.setString(10, x.ar_entdate);
            ps.setString(11, x.ar_effdate);
            ps.setString(12, x.ar_paiddate);
            ps.setString(13, x.ar_acct);
            ps.setString(14, x.ar_cc);
            ps.setString(15, x.ar_status);
            ps.setString(16, x.ar_bank);
            ps.setString(17, x.ar_site);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
                                 
    private static int _addArdMstr(ard_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ard_mstr where ard_id = ? and ard_line = ?";
        String sqlInsert = "insert into ard_mstr (ard_id, ard_line, ard_cust, ard_ref, ard_date, "
                        + " ard_amt, ard_amt_tax, ard_base_amt, ard_base_amt_tax, ard_curr, ard_base_curr, " 
                        + " ard_acct, ard_cc ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.ard_id);
          ps.setString(2, x.ard_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.ard_id);
            ps.setString(2, x.ard_line);
            ps.setString(3, x.ard_cust);
            ps.setString(4, x.ard_ref);
            ps.setString(5, x.ard_date);
            ps.setString(6, x.ard_amt);
            ps.setString(7, x.ard_amt_tax);
            ps.setString(8, x.ard_base_amt);
            ps.setString(9, x.ard_base_amt_tax);
            ps.setString(10, x.ard_curr);
            ps.setString(11, x.ard_base_curr);
            ps.setString(12, x.ard_acct);
            ps.setString(13, x.ard_cc);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
     
    public static String[] addArTransaction(ArrayList<ard_mstr> ard, ar_mstr ar) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addArMstr(ar, bscon, ps, res);  
            for (ard_mstr z : ard) {
                _addArdMstr(z, bscon, ps, res);
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
    
    // misc functions
    
    public static String[] getARTaxMaterialOnly(String ref) {
           // get AR tax info
            // art_nbr, art_desc, art_type, art_amt, art_percent
        String[] taxinfo = new String[]{"","","","",""};
        String sql = "select art_nbr, art_desc, art_type, art_amt, art_percent from art_tax where art_type = 'MATERIAL' and art_nbr = ?;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, ref);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               taxinfo[0] = res.getString("art_nbr");
               taxinfo[1] = res.getString("art_desc");
               taxinfo[2] = res.getString("art_type");
               taxinfo[3] = res.getString("art_amt");
               taxinfo[4] = res.getString("art_percent");             
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return taxinfo;
    }
    
    public record ar_mstr(String[] m, String ar_nbr, String ar_cust, String ar_amt, String ar_base_amt, 
        String ar_type, String ar_curr, String ar_base_curr, String ar_ref, String ar_rmks,
        String ar_entdate, String ar_effdate, String ar_paiddate, String ar_acct, String ar_cc,
        String ar_status, String ar_bank, String ar_site) {
        public ar_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "" );
        }
    }
    
    public record ard_mstr(String[] m, String ard_id, String ard_line, String ard_cust, String ard_ref, 
        String ard_date, String ard_amt, String ard_amt_tax, 
        String ard_base_amt, String ard_base_amt_tax, String ard_curr, String ard_base_curr, 
        String ard_acct, String ard_cc) {
        public ard_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "" );
        }
    }
    
    
}
