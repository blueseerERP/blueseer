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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.fgl.fglData._glEntryFromARMemo;
import static com.blueseer.fgl.fglData._glEntryFromARPayment;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.OVData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;

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
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
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
                        + "ar_status, ar_bank, ar_site, "
                        + "ar_amt_tax, ar_base_amt_tax, ar_amt_disc, ar_base_amt_disc, " 
                        + "ar_open_amt, ar_applied, ar_terms, ar_tax_code, " 
                        + " ar_invdate,  ar_duedate,  ar_discdate,  ar_reverse, " 
                        + " ar_termsdisc_amt, ar_termsdisc_pct, ar_termsdisc_days ) " 
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.ar_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.ar_nbr);
            ps.setString(2, x.ar_cust);
            ps.setDouble(3, x.ar_amt);
            ps.setDouble(4, x.ar_base_amt);
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
            ps.setDouble(18, x.ar_amt_tax);
            ps.setDouble(19, x.ar_base_amt_tax);
            ps.setDouble(20, x.ar_amt_disc);
            ps.setDouble(21, x.ar_base_amt_disc);
            ps.setDouble(22, x.ar_open_amt);
            ps.setString(23, x.ar_applied);
            ps.setString(24, x.ar_terms);
            ps.setString(25, x.ar_tax_code);
            ps.setString(26, x.ar_invdate);
            ps.setString(27, x.ar_duedate);
            ps.setString(28, x.ar_discdate);
            ps.setString(29, x.ar_reverse);
            ps.setDouble(30, x.ar_termsdisc_amt);
            ps.setDouble(31, x.ar_termsdisc_pct);
            ps.setInt(32, x.ar_termsdisc_days);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
                                 
    private static int _addArdMstr(ard_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ard_mstr where ard_nbr = ? and ard_line = ?";
        String sqlInsert = "insert into ard_mstr (ard_nbr, ard_line, ard_cust, ard_ref, ard_date, "
                        + " ard_amt, ard_amt_tax, ard_base_amt, ard_base_amt_tax, ard_curr, ard_base_curr, " 
                        + " ard_acct, ard_cc, ard_deduction ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.ard_nbr);
          ps.setInt(2, x.ard_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.ard_nbr);
            ps.setInt(2, x.ard_line);
            ps.setString(3, x.ard_cust);
            ps.setString(4, x.ard_ref);
            ps.setString(5, x.ard_date);
            ps.setDouble(6, x.ard_amt);
            ps.setDouble(7, x.ard_amt_tax);
            ps.setDouble(8, x.ard_base_amt);
            ps.setDouble(9, x.ard_base_amt_tax);
            ps.setString(10, x.ard_curr);
            ps.setString(11, x.ard_base_curr);
            ps.setString(12, x.ard_acct);
            ps.setString(13, x.ard_cc);
            ps.setDouble(14, x.ard_deduction);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
     
    public static String[] addArTransaction(String artype, ArrayList<ard_mstr> ard, ar_mstr ar) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addArTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(artype);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(ard);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(ar);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAR"));
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
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            _addArMstr(ar, bscon, ps, res);  
            for (ard_mstr z : ard) {
                _addArdMstr(z, bscon, ps, res);
            }
           
            _updateCustAR(ar.ar_cust(), bscon);
            _updateARopen(ar.ar_nbr(), bscon);
            if (artype.equals("ARPayment")) {
              _glEntryFromARPayment(ar.ar_nbr(), new java.util.Date(), bscon);
            }
            if (artype.equals("ARMemo")) {
              _glEntryFromARMemo(ar.ar_nbr(), new java.util.Date(), bscon);
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
    
    
    public static ARSet getARMstrSet(String[] x ) {
        ARSet r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getARMstrSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServFAR");
                r = objectMapper.readValue(returnstring, ARSet.class); 
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
            
            // order master
            ar_mstr ar = _getARMstr(x, bscon, ps, res); 
            ArrayList<ard_mstr> ard = _getARDet(x, bscon, ps, res);
           
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new ARSet(m, ar, ard);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new ARSet(m);
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
    
    public static ar_mstr getARMstr(String[] x) {
        ar_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","getARMstr"});
            list.add(new String[]{"param1",x[0]});
            list.add(new String[]{"param2",x[1]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServFIN");
                r = objectMapper.readValue(returnstring, ar_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new ar_mstr(m);
                return r;
            }
        }
        String sql = "select * from ar_mstr where ar_nbr = ? and ar_type = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ar_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ar_mstr(m, res.getString("ar_id"), res.getString("ar_nbr"), res.getString("ar_cust"),
                                res.getDouble("ar_amt"), res.getDouble("ar_base_amt"), res.getString("ar_type"),
                                res.getString("ar_curr"), res.getString("ar_base_curr"), res.getString("ar_ref"),
                                res.getString("ar_rmks"), res.getString("ar_entdate"), res.getString("ar_effdate"),
                                res.getString("ar_paiddate"), res.getString("ar_acct"), res.getString("ar_cc"),
                                res.getString("ar_status"), res.getString("ar_bank"), res.getString("ar_site"),
                                res.getDouble("ar_amt_tax"), res.getDouble("ar_base_amt_tax"), res.getDouble("ar_amt_disc"),
                                res.getDouble("ar_base_amt_disc"), res.getDouble("ar_open_amt"), res.getString("ar_applied"),
                                res.getString("ar_terms"), res.getString("ar_tax_code"), res.getString("ar_invdate"), 
                                res.getString("ar_duedate"), res.getString("ar_discdate"), res.getString("ar_reverse"),
                                res.getDouble("ar_termsdisc_amt"), res.getDouble("ar_termsdisc_pct"), res.getInt("ar_termsdisc_days"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ar_mstr(m);
        }
        return r;
    }
    
    private static ar_mstr _getARMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ar_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from ar_mstr where ar_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ar_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new ar_mstr(m, res.getString("ar_id"), res.getString("ar_nbr"), res.getString("ar_cust"),
                                res.getDouble("ar_amt"), res.getDouble("ar_base_amt"), res.getString("ar_type"),
                                res.getString("ar_curr"), res.getString("ar_base_curr"), res.getString("ar_ref"),
                                res.getString("ar_rmks"), res.getString("ar_entdate"), res.getString("ar_effdate"),
                                res.getString("ar_paiddate"), res.getString("ar_acct"), res.getString("ar_cc"),
                                res.getString("ar_status"), res.getString("ar_bank"), res.getString("ar_site"),
                                res.getDouble("ar_amt_tax"), res.getDouble("ar_base_amt_tax"), res.getDouble("ar_amt_disc"),
                                res.getDouble("ar_base_amt_disc"), res.getDouble("ar_open_amt"), res.getString("ar_applied"),
                                res.getString("ar_terms"), res.getString("ar_tax_code"), res.getString("ar_invdate"), 
                                res.getString("ar_duedate"), res.getString("ar_discdate"), res.getString("ar_reverse"),
                                res.getDouble("ar_termsdisc_amt"), res.getDouble("ar_termsdisc_pct"), res.getInt("ar_termsdisc_days"));
                }
            }
            return r;
    }
     
    
    public static ArrayList<ard_mstr> getARDet(String[] x) {
        ArrayList<ard_mstr> list = new ArrayList<ard_mstr>();
        ard_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from ard_mstr where ard_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new ard_mstr(m, res.getString("ard_nbr"), res.getInt("ard_line"), res.getString("ard_cust"),
                    res.getString("ard_ref"), res.getString("ard_date"), res.getDouble("ard_amt"), 
                    res.getDouble("ard_amt_tax"), res.getDouble("ard_base_amt"),res.getDouble("ard_base_amt_tax"), 
                    res.getString("ard_curr"), res.getString("ard_base_curr"), res.getString("ard_acct"), 
                    res.getString("ard_cc"), res.getDouble("ard_deduction") );
                    list.add(r);
                    }
                
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    private static ArrayList<ard_mstr> _getARDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<ard_mstr> list = new ArrayList<ard_mstr>();
        ard_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from ard_mstr where ard_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ard_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new ard_mstr(m, res.getString("ard_nbr"), res.getInt("ard_line"), res.getString("ard_cust"),
                    res.getString("ard_ref"), res.getString("ard_date"), res.getDouble("ard_amt"), 
                    res.getDouble("ard_amt_tax"), res.getDouble("ard_base_amt"),res.getDouble("ard_base_amt_tax"), 
                    res.getString("ard_curr"), res.getString("ard_base_curr"), res.getString("ard_acct"), 
                    res.getString("ard_cc"), res.getDouble("ard_deduction") );
                    list.add(r);
                    }
            }
            return list;
    }
    
    
    
    public static String[] addUpdateARCtrl(ar_ctrl x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateARCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFIN"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  ar_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into ar_ctrl (arc_bank, arc_default_acct, arc_default_cc,"
                + "arc_sales_acct, arc_sales_cc, arc_asset_acct, arc_asset_cc,"
                + "arc_fedtax_acct, arc_fedtax_cc, arc_statetax_acct, arc_statetax_cc,"
                + "arc_localtax_acct, arc_localtax_cc, arc_othertax_acct, arc_othertax_cc, arc_varchar ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update ar_ctrl set arc_bank = ?, arc_default_acct = ?, arc_default_cc = ?,"
                + "arc_sales_acct = ?, arc_sales_cc = ?, arc_asset_acct = ?, arc_asset_cc = ?,"
                + "arc_fedtax_acct = ?, arc_fedtax_cc = ?, arc_statetax_acct = ?, arc_statetax_cc = ?,"
                + "arc_localtax_acct = ?, arc_localtax_cc = ?, arc_othertax_acct = ?, arc_othertax_cc = ?, arc_varchar = ?, "
                + " arc_salestax_acct = ?, arc_salestax_cc = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.arc_bank);
            psi.setString(2, x.arc_default_acct);
            psi.setString(3, x.arc_default_cc);
            psi.setString(4, x.arc_sales_acct);
            psi.setString(5, x.arc_sales_cc);
            psi.setString(6, x.arc_asset_acct);
            psi.setString(7, x.arc_asset_cc);
            psi.setString(8, x.arc_fedtax_acct);
            psi.setString(9, x.arc_fedtax_cc);
            psi.setString(10, x.arc_statetax_acct);
            psi.setString(11, x.arc_statetax_cc);
            psi.setString(12, x.arc_localtax_acct);
            psi.setString(13, x.arc_localtax_cc);
            psi.setString(14, x.arc_othertax_acct);
            psi.setString(15, x.arc_othertax_cc);
            psi.setString(16, x.arc_varchar);
            psi.setString(17, x.arc_salestax_acct);
            psi.setString(18, x.arc_salestax_cc);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.arc_bank);
            psu.setString(2, x.arc_default_acct);
            psu.setString(3, x.arc_default_cc);
            psu.setString(4, x.arc_sales_acct);
            psu.setString(5, x.arc_sales_cc);
            psu.setString(6, x.arc_asset_acct);
            psu.setString(7, x.arc_asset_cc);
            psu.setString(8, x.arc_fedtax_acct);
            psu.setString(9, x.arc_fedtax_cc);
            psu.setString(10, x.arc_statetax_acct);
            psu.setString(11, x.arc_statetax_cc);
            psu.setString(12, x.arc_localtax_acct);
            psu.setString(13, x.arc_localtax_cc);
            psu.setString(14, x.arc_othertax_acct);
            psu.setString(15, x.arc_othertax_cc);
            psu.setString(16, x.arc_varchar);
            psu.setString(17, x.arc_salestax_acct);
            psu.setString(18, x.arc_salestax_cc);
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
   
    public static ar_ctrl getARCtrl(String[] x) {
        ar_ctrl r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getARCtrl"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServFIN");
                r = objectMapper.readValue(returnstring, ar_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new ar_ctrl(m);
                return r;
            }
        }
        String sql = "select * from ar_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ar_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ar_ctrl(m, 
                                res.getString("arc_bank"),
                                res.getString("arc_default_acct"),
                                res.getString("arc_default_cc"),
                                res.getString("arc_sales_acct"),
                                res.getString("arc_sales_cc"),
                                res.getString("arc_asset_acct"),
                                res.getString("arc_asset_cc"),
                                res.getString("arc_fedtax_acct"),
                                res.getString("arc_fedtax_cc"),
                                res.getString("arc_statetax_acct"),
                                res.getString("arc_statetax_cc"),
                                res.getString("arc_localtax_acct"),
                                res.getString("arc_localtax_cc"),
                                res.getString("arc_othertax_acct"),
                                res.getString("arc_othertax_cc"),
                                res.getString("arc_varchar"),
                                res.getString("arc_salestax_acct"),
                                res.getString("arc_salestax_cc")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ar_ctrl(m);
        }
        return r;
    }
    
    
    // misc functions
    public static String getFarRptPickerData(String[] keys) {
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
                if (keys[0].equals("AREntriesByCust")) {
                res = st.executeQuery("select ar_id, ar_cust, cm_name, ar_type, " +
                               " ar_ref, ar_nbr, ar_effdate, ar_invdate, ar_duedate, ar_curr, ar_amt, ar_base_amt, ar_open_amt, " +
                               " case when ar_status = 'c' then 'closed' else 'open' end as 'status', " +
                               " ar_curr, ar_acct " +
                               " from ar_mstr inner join cm_mstr on cm_code = ar_cust " +
                               " where ar_cust >= " + "'" + keys[1] + "'" +
                               " and ar_cust <= " + "'" + keys[2] + "'" + ";");  
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put(res.getString("ar_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("ar_type"));
                            rowArray.put(res.getString("ar_ref"));
                            rowArray.put(res.getString("ar_nbr"));
                            rowArray.put(res.getString("ar_effdate"));
                            rowArray.put(res.getString("ar_duedate"));
                            rowArray.put(res.getString("ar_curr"));
                            rowArray.put(currformat(res.getString("ar_amt")));
                            rowArray.put(currformat(res.getString("ar_open_amt")));
                            rowArray.put(res.getString("status"));
                            jsonarray.put(rowArray);

                    } 
                }
               
                if (keys[0].equals("AREntriesByDate")) {
                res = st.executeQuery("select ar_id, ar_cust, cm_name, ar_type, " +
                               " ar_ref, ar_nbr, ar_effdate, ar_invdate, ar_duedate, ar_curr, ar_amt, ar_base_amt, ar_open_amt, " +
                               " case when ar_status = 'c' then 'closed' else 'open' end as 'status', " +
                               " ar_curr, ar_acct " +
                               " from ar_mstr inner join cm_mstr on cm_code = ar_cust " +
                               " where ar_effdate >= " + "'" + keys[1] + "'" +
                               " and ar_effdate <= " + "'" + keys[2] + "'" + ";");  
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put(res.getString("ar_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("ar_type"));
                            rowArray.put(res.getString("ar_ref"));
                            rowArray.put(res.getString("ar_nbr"));
                            rowArray.put(res.getString("ar_effdate"));
                            rowArray.put(res.getString("ar_duedate"));
                            rowArray.put(res.getString("ar_curr"));
                            rowArray.put(currformat(res.getString("ar_amt")));
                            rowArray.put(currformat(res.getString("ar_open_amt")));
                            rowArray.put(res.getString("status"));
                            jsonarray.put(rowArray);

                    } 
                }
               
                if (keys[0].equals("ARAgingByCust")) {
                    ArrayList custs = cusData.getcustmstrlistBetween(keys[1], keys[2]);
                    for (int j = 0; j < custs.size(); j++) {    

                            if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                               res = st.executeQuery("SELECT ar_cust, cm_name, " +
                                  " sum(case when ar_duedate > date() then ar_open_amt else 0 end) as '0', " +
                                  " sum(case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end) as '30', " +
                                  " sum(case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end) as '60', " +
                                  " sum(case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end) as '90', " +
                                  " sum(case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end) as '90p' " +
                                  " FROM  ar_mstr " +
                                  " inner join cm_mstr on cm_code = ar_cust " +
                                  " where ar_cust = " + "'" + custs.get(j) + "'" + 
                                  " AND ar_status = 'o' " +
                                   " group by ar_cust, cm_name order by ar_cust;");
                            }  else {
                            res = st.executeQuery("SELECT ar_cust, cm_name, " +
                                  " sum(case when ar_duedate > curdate() then ar_open_amt else 0 end) as '0', " +
                                  " sum(case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end) as '30', " +
                                  " sum(case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end) as '60', " +
                                  " sum(case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end) as '90', " +
                                  " sum(case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end) as '90p' " +
                                  " FROM  ar_mstr " +
                                  " inner join cm_mstr on cm_code = ar_cust " +
                                   " where ar_cust = " + "'" + custs.get(j) + "'" + 
                                  " AND ar_status = 'o' " +
                                   " group by ar_cust, cm_name order by ar_cust;");
                            }
                            while (res.next()) {
                                  i++;
                                  JSONArray rowArray = new JSONArray(); 
                                  rowArray.put(res.getString("ar_cust"));
                                  rowArray.put(res.getString("cm_name"));
                                  rowArray.put(currformat(res.getString("0")));
                                  rowArray.put(currformat(res.getString("30")));
                                  rowArray.put(currformat(res.getString("60")));
                                  rowArray.put(currformat(res.getString("90")));
                                  rowArray.put(currformat(res.getString("90p")));                           
                                  jsonarray.put(rowArray);
                            }
                    }
                }
               
                if (keys[0].equals("ARAgingDetailByCust")) {
                    ArrayList custs = cusData.getcustmstrlistBetween(keys[1], keys[2]);
                    for (int j = 0; j < custs.size(); j++) {    
                
                        if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                          res = st.executeQuery("SELECT cm_name, ar_cust, ar_rmks, ar_ref, ar_type, ar_nbr, ar_effdate, ar_duedate, " +
                                 " case when ar_duedate > date() then ar_open_amt else 0 end as '0', " +
                                 " case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end as '30', " +
                                 " case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end as '60', " +
                                 " case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end as '90', " +
                                 " case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end as '90p' " +
                                 " FROM  ar_mstr " +
                                 " inner join cm_mstr on cm_code = ar_cust " +
                                 " where ar_cust = " + "'" + custs.get(j) + "'" + 
                                 " AND ar_status = 'o' " +
                                  " order by ar_cust, ar_nbr ;"); 
                          } else {
                          res = st.executeQuery("SELECT cm_name, ar_cust, ar_rmks, ar_ref, ar_type, ar_nbr, ar_effdate, ar_duedate, " +
                                 " case when ar_duedate > curdate() then ar_open_amt else 0 end as '0', " +
                                 " case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end as '30', " +
                                 " case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end as '60', " +
                                 " case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end as '90', " +
                                 " case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end as '90p' " +
                                 " FROM  ar_mstr " +
                                 " inner join cm_mstr on cm_code = ar_cust " + 
                                 " where ar_cust = " + "'" + custs.get(j) + "'" + 
                                 " AND ar_status = 'o' " +
                                  " order by ar_cust, ar_nbr ;");     
                          }
                         while (res.next()) {
                                 i++;
                                  JSONArray rowArray = new JSONArray(); 
                                  rowArray.put(res.getString("ar_cust"));
                                  rowArray.put(res.getString("cm_name"));
                                  rowArray.put(res.getString("ar_nbr"));
                                  rowArray.put(res.getString("ar_ref"));
                                  rowArray.put(res.getString("ar_effdate"));
                                  rowArray.put(res.getString("ar_duedate"));
                                  rowArray.put(currformat(res.getString("0")));
                                  rowArray.put(currformat(res.getString("30")));
                                  rowArray.put(currformat(res.getString("60")));
                                  rowArray.put(currformat(res.getString("90")));
                                  rowArray.put(currformat(res.getString("90p")));                           
                                  jsonarray.put(rowArray);
                             }
           
                    }
                }
               
                if (keys[0].equals("ARPaymentsByCustDate")) {
                    ArrayList custs = cusData.getcustmstrlistBetween(keys[1], keys[2]);
                    for (int j = 0; j < custs.size(); j++) {    

                        res = st.executeQuery("SELECT a.ar_cust, cm_name, b.ar_ref as 'b.ar_ref', b.ar_duedate as 'b.ar_duedate', a.ar_nbr, a.ar_ref, ard_ref, a.ar_type, a.ar_effdate, a.ar_amt, ard_amt " +
                        " FROM  ar_mstr a " +
                        " inner join ard_mstr on ard_nbr = a.ar_nbr " +
                        " inner join ar_mstr b on b.ar_nbr = ard_ref and b.ar_type = 'I' " +
                        " inner join cm_mstr on cm_code = a.ar_cust " +
                        " where a.ar_cust = " + "'" + custs.get(j) + "'" + 
                        " AND a.ar_type = 'P' " +
                        " AND a.ar_effdate >= " + "'" + keys[1] + "'" +
                        " AND a.ar_effdate <= " + "'" + keys[2] + "'" +
                         " order by a.ar_effdate desc ;");                        
                            while (res.next()) {
                                  i++;
                                  JSONArray rowArray = new JSONArray(); 
                                  rowArray.put(res.getString("ar_cust"));
                                  rowArray.put(res.getString("cm_name"));
                                  rowArray.put(res.getString("ard_ref"));
                                  rowArray.put(res.getString("b.ar_ref"));
                                  rowArray.put(res.getString("ar_type"));
                                  rowArray.put(res.getString("ar_ref"));
                                  rowArray.put(currformat(res.getString("ard_amt")));
                                  rowArray.put(currformat(res.getString("ar_amt")));                         
                                  jsonarray.put(rowArray);
                            }
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
    
    public static String getARAgingView(String[] keys) {
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
                double dol = 0;
                double qty = 0;
                 ArrayList custs = cusData.getcustmstrlistBetween(keys[0], keys[1]);
                 String custname = "";
                 for (int j = 0; j < custs.size(); j++) {
                 custname = cusData.getCustName(custs.get(j).toString());
                 // init for new cust
                 i = 0;
                 
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                     res = st.executeQuery("SELECT ar_cust, cm_name, " +
                        " sum(case when ar_duedate > date() then ar_open_amt else 0 end) as '0', " +
                        " sum(case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end) as '30', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end) as '60', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end) as '90', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end) as '90p' " +
                        " FROM  ar_mstr " +
                        " inner join cm_mstr on cm_code = ar_cust " +
                        " where ar_cust = " + "'" + custs.get(j) + "'" + 
                        " AND ar_status = 'o' " +
                        " AND ar_site = " + "'" + keys[2] + "'" +
                         " group by ar_cust, cm_name order by ar_cust;");
                 }  else {
                 res = st.executeQuery("SELECT ar_cust, cm_name, " +
                        " sum(case when ar_duedate > curdate() then ar_open_amt else 0 end) as '0', " +
                        " sum(case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end) as '30', " +
                        " sum(case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end) as '60', " +
                        " sum(case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end) as '90', " +
                        " sum(case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end) as '90p' " +
                        " FROM  ar_mstr " +
                        " inner join cm_mstr on cm_code = ar_cust " +
                         " where ar_cust = " + "'" + custs.get(j) + "'" + 
                        " AND ar_status = 'o' " +
                        " AND ar_site = " + "'" + keys[2] + "'" +         
                         " group by ar_cust, cm_name order by ar_cust;");
                 }
                  while (res.next()) {
                   dol = dol + (res.getDouble("0") + res.getDouble("30") + res.getDouble("60") + res.getDouble("90") + res.getDouble("90p") );
                   qty = qty + 0;
                    i++;
                        
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("ar_cust"));
                        rowArray.put(custname);
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("0"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("30"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("60"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("90"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("90p"))));
                        jsonarray.put(rowArray);
                        
                }
                 
                   if (i == 0) {
                       JSONArray rowArray = new JSONArray(); 
                       rowArray.put("detail");
                        rowArray.put(custs.get(j));
                        rowArray.put(custname);
                        rowArray.put(0);
                        rowArray.put(0);
                        rowArray.put(0);
                        rowArray.put(0);
                        rowArray.put(0);
                        jsonarray.put(rowArray);
                  }
                  
                  
                  
             } // for each customer in range
                  
                   
                
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
    
    public static String getARAgingDetailView(String[] keys) {
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
                 
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                 res = st.executeQuery("SELECT ar_cust, ar_rmks, ar_type, ar_nbr, ar_effdate, ar_duedate, " +
                        " case when ar_duedate > date() then ar_open_amt else 0 end as '0', " +
                        " case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end as '30', " +
                        " case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end as '60', " +
                        " case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end as '90', " +
                        " case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end as '90p' " +
                        " FROM  ar_mstr " +
                        " where ar_cust = " + "'" + keys[0] + "'" + 
                        " AND ar_status = 'o' " +
                        " AND ar_site = " + "'" + keys[1] + "'" +        
                         " order by ar_cust, ar_nbr ;"); 
                 } else {
                 res = st.executeQuery("SELECT ar_cust, ar_rmks, ar_type, ar_nbr, ar_effdate, ar_duedate, " +
                        " case when ar_duedate > curdate() then ar_open_amt else 0 end as '0', " +
                        " case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end as '30', " +
                        " case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end as '60', " +
                        " case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end as '90', " +
                        " case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end as '90p' " +
                        " FROM  ar_mstr " +
                        " where ar_cust = " + "'" + keys[0] + "'" + 
                        " AND ar_status = 'o' " +
                        " AND ar_site = " + "'" + keys[1] + "'" +        
                         " order by ar_cust, ar_nbr ;");     
                 }
                  while (res.next()) {
                  
                        
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ar_nbr"));
                        rowArray.put(res.getString("ar_rmks"));
                        rowArray.put(res.getString("ar_type"));
                        rowArray.put(getDateDB(res.getString("ar_effdate")));
                        rowArray.put(getDateDB(res.getString("ar_duedate")));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("0"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("30")))); 
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("60"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("90"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("90p"))));
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
    
    public static String getARAgingPaymentView(String cust) {
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
                 
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                 res = st.executeQuery("SELECT a.ar_cust, b.ar_duedate as 'b.ar_duedate', a.ar_nbr, a.ar_ref, ard_ref, a.ar_type, a.ar_effdate, a.ar_amt, ard_amt " +
                        " FROM  ar_mstr a " +
                        " inner join ard_mstr on ard_nbr = a.ar_nbr " +
                        " inner join ar_mstr b on b.ar_nbr = ard_ref and b.ar_type = 'I' " +
                        " where a.ar_cust = " + "'" + cust + "'" + 
                        " AND a.ar_type = 'P' " +
                        " AND a.ar_effdate >= date() - date(date(), '-90 day') " +
                         " order by a.ar_effdate desc ;");        
                } else {
                   res = st.executeQuery("SELECT a.ar_cust, b.ar_duedate as 'b.ar_duedate', a.ar_nbr, a.ar_ref, ard_ref, a.ar_type, a.ar_effdate, a.ar_amt, ard_amt " +
                        " FROM  ar_mstr a " +
                        " inner join ard_mstr on ard_nbr = a.ar_nbr " +
                        " inner join ar_mstr b on b.ar_nbr = ard_ref and b.ar_type = 'I' " +
                        " where a.ar_cust = " + "'" + cust + "'" + 
                        " AND a.ar_type = 'P' " +
                        " AND a.ar_effdate >= curdate() - interval 90 day " +
                         " order by a.ar_effdate desc ;");    
                }
                  while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("ar_nbr"));
                        rowArray.put(res.getString("ard_ref"));                        
                        rowArray.put(getDateDB(res.getString("ar_effdate")));
                        rowArray.put(getDateDB(res.getString("b.ar_duedate")));
                        rowArray.put(res.getString("ar_type"));
                        rowArray.put(res.getString("ar_ref"));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ard_amt"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_amt")))); 
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
    
    public static String getARAgingExport(String cust) {
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
                 
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                 res = st.executeQuery("SELECT ar_cust, ar_nbr, ar_type, ar_effdate, ar_duedate, sh_po, " +
                        " case when ar_duedate > date() then ar_open_amt else 0 end as '0', " +
                        " case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end as '30', " +
                        " case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end as '60', " +
                        " case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end as '90', " +
                        " case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end as '90p' " +
                        " FROM  ar_mstr left outer join ship_mstr on sh_id = ar_nbr  " +
                        " where ar_cust = " + "'" + cust + "'" + 
                        " AND ar_status = 'o' " +   
                         " order by ar_cust, ar_nbr ;"); 
                 } else {
                 res = st.executeQuery("SELECT ar_cust, ar_nbr, ar_type, ar_effdate, ar_duedate, sh_po, " +
                        " case when ar_duedate > curdate() then ar_open_amt else 0 end as '0', " +
                        " case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end as '30', " +
                        " case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end as '60', " +
                        " case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end as '90', " +
                        " case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end as '90p' " +
                        " FROM  ar_mstr left outer join ship_mstr on sh_id = ar_nbr  " +
                        " where ar_cust = " + "'" + cust + "'" + 
                        " AND ar_status = 'o' " +      
                         " order by ar_cust, ar_nbr ;");     
                 }
                  while (res.next()) {
                  
                        
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("ar_cust"));
                        rowArray.put(res.getString("ar_nbr"));
                        rowArray.put(res.getString("sh_po"));
                        rowArray.put(getDateDB(res.getString("ar_effdate")));
                        rowArray.put(getDateDB(res.getString("ar_duedate")));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("0"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("30")))); 
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("60"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("90"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("90p"))));
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
    
    public static String getARReferencesView(String cust, String curr) {
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
                 
                res = st.executeQuery("select * from ar_mstr where ar_cust = " + "'" + cust + "'" +
                        " AND ar_curr = " + "'" + curr + "'" + 
                        " AND ar_status = 'o' " + ";");
                
                  while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("ar_nbr"));
                        rowArray.put(getDateDB(res.getString("ar_discdate")));
                        rowArray.put(getDateDB(res.getString("ar_duedate")));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_amt")))); 
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_applied"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_open_amt"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_amt_tax"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_termsdisc_amt"))));
                        rowArray.put(res.getString("ar_curr"));
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
    
    public static String getARTransactionsView(String[] keys) {
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
                     res = st.executeQuery("SELECT * " +
                        " FROM  ar_mstr " +
                        " where ar_cust >= " + "'" + keys[0] + "'" + 
                        " AND ar_cust <= " + "'" + keys[1] + "'" + 
                        " AND ar_effdate >= " + "'" + keys[2] + "'" + 
                        " AND ar_effdate <= " + "'" + keys[3] + "'" + 
                         " order by ar_cust;");    
                 } else {
                     res = st.executeQuery("SELECT * " +
                        " FROM  ar_mstr " +
                        " where ar_cust >= " + "'" + keys[0] + "'" + 
                        " AND ar_cust <= " + "'" + keys[1] + "'" + 
                        " AND ar_effdate >= " + "'" + keys[2] + "'" + 
                        " AND ar_effdate <= " + "'" + keys[3] + "'" + 
                        " AND ar_type = " + "'" + keys[4] + "'" +
                         " order by ar_cust;");    
                 }
               
                  while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("ar_id"));
                        rowArray.put(res.getString("ar_nbr"));
                        rowArray.put(res.getString("ar_cust"));
                        rowArray.put(res.getString("ar_type"));
                        rowArray.put(getDateDB(res.getString("ar_effdate")));
                        rowArray.put(res.getString("ar_status"));
                        rowArray.put(res.getString("ar_ref"));
                        rowArray.put(res.getString("ar_rmks"));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_amt")))); 
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_applied"))));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ar_open_amt")))); 
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
    
    public static String getARTransactionsDetView(String id) {
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
                 
                res = st.executeQuery("SELECT * " +
                        " FROM  ard_mstr " +
                        " where ard_nbr = " + "'" +id + "'" + 
                         " order by ard_line;"); 
              
                  while (res.next()) {
                        JSONArray rowArray = new JSONArray();
                        rowArray.put(res.getString("ard_nbr"));
                        rowArray.put(res.getString("ard_cust"));
                        rowArray.put(res.getString("ard_ref"));
                        rowArray.put(res.getString("ard_line"));
                        rowArray.put(res.getString("ard_date"));
                        rowArray.put(res.getString("ard_acct"));
                        rowArray.put(res.getString("ard_cc"));
                        rowArray.put(bsNumber(currformatDouble(res.getDouble("ard_amt")))); 
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
    
    
    public static void _updateCustAR(String cust, Connection bscon) throws SQLException {
           
        Statement st = bscon.createStatement();
        ResultSet res;
                
        // high balance
        double highbal = 0.00;
        res = st.executeQuery("select ar_open_amt from ar_mstr where ar_status = 'o' and ar_cust = " + "'" + cust + "'" +";");
        while (res.next()) {
         highbal += res.getDouble("ar_open_amt");
        }
        // avg days to pay
        int avgdays = 0;
        if (bsmf.MainFrame.dbtype.equals("sqlite")) {
            res = st.executeQuery("SELECT coalesce(avg(julianday(ar_paiddate) - julianday(ar_duedate)),0) AS nbrofdays from ar_mstr " +
                    " where ar_cust = " + "'" + cust + "'" );
        } else {
            res = st.executeQuery("SELECT coalesce(avg(datediff(ar_paiddate, ar_duedate)),0) AS nbrofdays from ar_mstr " +
                    " where ar_cust = " + "'" + cust + "'" );
        }
        while (res.next()) {
         avgdays += res.getInt("nbrofdays");
         
        }

        
        st.executeUpdate("update cm_mstr set cm_highbal = case when cm_highbal < " +  highbal + " then " + highbal + " else cm_highbal end, cm_avgdays = " + "'" + avgdays + "'" +
                " where cm_code = " + "'" + cust + "'");
       
        
        res.close();
        st.close();
           
    }
    
    public static void _updateARopen(String batch, Connection bscon) throws SQLException {
           
        Statement st = bscon.createStatement();
        ResultSet res;
                
        
         res = st.executeQuery("select ar_amt, ar_base_amt, ar_curr, ar_base_curr, " +
                            " ar_open_amt, ar_applied, ard_ref, ard_amt, ard_base_amt, ard_deduction " +
                            " from ar_mstr inner join ard_mstr on ar_nbr = ard_ref " +
                                    " where ard_nbr = " + "'" + batch + "'"
                            );
                    
                     ArrayList ardref = new ArrayList();
                    ArrayList newamt = new ArrayList();
                    ArrayList openamt = new ArrayList();
                    ArrayList status = new ArrayList();
                    ArrayList gainloss = new ArrayList();
                    
                    while (res.next()) {
                        ardref.add(res.getString("ard_ref"));
                        newamt.add(res.getDouble("ard_amt") + res.getDouble("ar_applied"));
                        openamt.add(res.getDouble("ar_amt") - res.getDouble("ar_applied") - res.getDouble("ard_amt") - res.getDouble("ard_deduction"));
                        if ( (res.getDouble("ard_amt") + res.getDouble("ar_applied") + res.getDouble("ard_deduction")) >= res.getDouble("ar_amt") ) {
                         status.add("c");
                        } else {
                         status.add("o");
                        }
                    }
                    
                     for (int j = 0; j < ardref.size(); j++) {
                    st.executeUpdate("update ar_mstr set ar_applied = " + "'" + currformatDouble(bsParseDouble(newamt.get(j).toString())) + "'" + "," +
                            " ar_open_amt = " + "'" + currformatDouble(bsParseDouble(openamt.get(j).toString())) + "'" + "," +
                            " ar_status = " + "'" + status.get(j) + "'" +
                            " where ar_nbr = " + "'" + ardref.get(j) + "'" + 
                            " and ar_type = 'I' "
                            );
                     }
       
        
        res.close();
        st.close();
           
    }
    
    
    public static String[] getARTaxMaterialOnly(String ref) {
           // get AR tax info
            // art_nbr, art_desc, art_type, art_amt, art_percent
        String[] taxinfo = new String[]{"","","","",""};
        String sql = "select art_nbr, art_desc, art_type, art_amt, art_percent from art_tax where art_type = 'MATERIAL' and art_nbr = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
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
    
    public record ARSet(String[] m, ar_mstr ar, ArrayList<ard_mstr> ard) {
        public ARSet(String[] m) {
            this (m, null, null);
        }
    }
    
    public record ar_mstr(String[] m, String ar_id, String ar_nbr, String ar_cust, double ar_amt, double ar_base_amt, 
        String ar_type, String ar_curr, String ar_base_curr, String ar_ref, String ar_rmks,
        String ar_entdate, String ar_effdate, String ar_paiddate, String ar_acct, String ar_cc,
        String ar_status, String ar_bank, String ar_site, 
        double ar_amt_tax, double ar_base_amt_tax, double ar_amt_disc, double ar_base_amt_disc, 
        double ar_open_amt, String ar_applied, String ar_terms, String ar_tax_code,
        String ar_invdate, String ar_duedate, String ar_discdate, String ar_reverse,
        double ar_termsdisc_amt, double ar_termsdisc_pct, int ar_termsdisc_days) {
        public ar_mstr(String[] m) {
            this(m, "", "", "", 0, 0, "", "", "", "", "",
                    "", "", "", "", "", "", "", "", 0, 0,
                    0, 0, 0, "", "", "", "", "", "", "",
                    0, 0, 0);
        }
    }
    
    public record ard_mstr(String[] m, String ard_nbr, int ard_line, String ard_cust, String ard_ref, 
        String ard_date, double ard_amt, double ard_amt_tax, 
        double ard_base_amt, double ard_base_amt_tax, String ard_curr, String ard_base_curr, 
        String ard_acct, String ard_cc, double ard_deduction) {
        public ard_mstr(String[] m) {
            this(m, "", 0, "", "", "", 0, 0, 0, 0, "",
                    "", "", "", 0 );
        }
    }
    
    public record ar_ctrl(String[] m, String arc_bank, String arc_default_acct, String arc_default_cc, 
        String arc_sales_acct, String arc_sales_cc, String arc_asset_acct, String arc_asset_cc, 
        String arc_fedtax_acct, String arc_fedtax_cc, String arc_statetax_acct, String arc_statetax_cc, 
        String arc_localtax_acct, String arc_localtax_cc, String arc_othertax_acct, String arc_othertax_cc,
        String arc_varchar, String arc_salestax_acct, String arc_salestax_cc) {
        public ar_ctrl(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "" );
        }
    }
}
