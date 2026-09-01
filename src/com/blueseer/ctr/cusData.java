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
package com.blueseer.ctr;


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
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListString;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONArray;

/**
 *
 * @author terryva
 */
public class cusData {
    
    
    // add customer master customer master table only
    public static String[] addCustMstr(cm_mstr x) {
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
            int rows = _addCustMstr(x, con, ps, res, false);  // add cms_det
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
    
    // add customer master.... multiple table transaction function
    public static String[] addCustomerTransaction(cm_mstr cm, ArrayList<String[]> list, cms_det cms) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> xlist = new ArrayList<String[]>();
            xlist.add(new String[]{"id","addCustomerTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(cm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(list);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(cms);
                return jsonToStringArray(sendServerPost(xlist, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        String[] m = new String[2];
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            con.setAutoCommit(false);
            _addCustMstr(cm, con, ps, res, false);  // add cm_mstr
            if (list != null) {
                _deleteCMCDetAll(cm.cm_code, con, ps, res);    // delete cmc_det
                for (String[] s : list) {  
                cmc_det z = new cmc_det(null, 
                    s[0],
                    cm.cm_code,
                    s[1],
                    s[2],
                    s[3],
                    s[4],
                    s[5]
                    );
                _addCMCDet(z, con, ps, res);  // add cmc_det
                }
            }
            _addCMSDet(cms, con, ps, res, false);  // add cms_det
            con.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 con.rollback();
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
     
    public static String[] addCustMstrMass(ArrayList<String> custlist, String delim) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addCustMstrMass"});
            list.add(new String[]{"param1",delim});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(custlist);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[]{"0",""};
        String[] ld = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
   
            for (String rec : custlist) {
                ld = rec.split(delim, -1);
                cm_mstr x = new cm_mstr(null, 
                ld[0], ld[1], ld[2], ld[3],
                    ld[4], ld[5], ld[6], ld[7],
                    ld[8], 
                    BlueSeerUtils.setDateFormat(new java.util.Date()), 
                    BlueSeerUtils.setDateFormat(new java.util.Date()), 
                    bsmf.MainFrame.userid, ld[10], ld[11], ld[12], ld[13], 
                    ld[14], (ld[15].isBlank()) ? "N30" : ld[15], ld[16], ld[17], 
                    ld[18], ld[19], ld[20], 
                    (ld[21].isBlank()) ? "20000000" : ld[21],
                    (ld[22].isBlank()) ? "9999" : ld[22],    
                    (ld[25].isBlank()) ? "BK" : ld[25],
                    (ld[26].isBlank()) ? "USD" : ld[26],
                    ld[23], 
                    ld[30], ld[28], ld[29], ld[31], ld[32],
                    ld[33], ld[34], ld[35], (ld[9].isBlank()) ? "1000" : ld[9], "", "0", 
                    "0", // highbal
                    "0", // avgdays
                    "",
                    "",
                    "", // muncipal
                    "", // county
                    "0" // tax exempt
                );
     
                _addCustMstr(x, con, ps, res, true);
                cms_det y = new cms_det(null, 
                ld[0], ld[0], ld[1], ld[2], ld[3],
                    ld[4], ld[5], ld[6], ld[7],
                    ld[8], "", "", "", "", "", "", "", "" );
                _addCMSDet(y,  con, ps, res, true);
            }
        } catch (SQLException s) {
            MainFrame.bslog(s);
            m = new String[]{"1", s.getMessage()};
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
        
    private static int _addCustMstr(cm_mstr x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from cm_mstr where cm_code = ?";
        String sqlInsert = "insert into cm_mstr (cm_code, cm_name, cm_line1, cm_line2, " 
                        + "cm_line3, cm_city, cm_state, cm_zip, "
                        + "cm_country, cm_dateadd, cm_datemod, cm_usermod, "
                        + "cm_group, cm_market, cm_creditlimit, cm_onhold, "
                        + "cm_carrier, cm_terms, cm_freight_type, cm_price_code, "
                        + "cm_disc_code, cm_tax_code, cm_salesperson, "
                        + "cm_ar_acct, cm_ar_cc, cm_bank, cm_curr, cm_remarks, " 
                        + "cm_label, cm_ps_jasper, cm_iv_jasper, cm_phone, cm_email, "
                        + "cm_is855export, cm_is856export, cm_is810export, cm_site, cm_misc1, cm_cascade, cm_municipality, cm_county, cm_tax_exempt ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update cm_mstr set " 
                + " cm_name = ?, cm_line1 = ?, cm_line2 = ?, "
                + "cm_line3 = ?, cm_city = ?, cm_state = ?, cm_zip = ?, "
                + "cm_country = ?, cm_dateadd = ?, cm_datemod = ?, cm_usermod = ?, "
                + "cm_group = ?, cm_market = ?, cm_creditlimit = ?, cm_onhold = ?, "
                + "cm_carrier = ?, cm_terms = ?, cm_freight_type = ?, cm_price_code = ?, "
                + "cm_disc_code = ?, cm_tax_code = ?, cm_salesperson = ?, "
                + "cm_ar_acct = ?, cm_ar_cc = ?, cm_bank = ?, cm_curr = ?, cm_remarks = ?, " 
                + "cm_label = ?, cm_ps_jasper = ?, cm_iv_jasper = ?, cm_phone = ?, cm_email = ?, "
                + "cm_is855export = ?, cm_is856export = ?, cm_is810export = ?, cm_site = ?, cm_misc1 = ?, cm_cascade = ?, "
                + "cm_municipality = ?, cm_county = ?, cm_tax_exempt = ? "
                + " where cm_code = ? ; ";  
          ps = con.prepareStatement(sqlSelect);
          ps.setString(1, x.cm_code);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
          PreparedStatement psu = con.prepareStatement(sqlUpdate); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cm_code);
            ps.setString(2, x.cm_name);
            ps.setString(3, x.cm_line1);
            ps.setString(4, x.cm_line2);
            ps.setString(5, x.cm_line3);
            ps.setString(6, x.cm_city);
            ps.setString(7, x.cm_state);
            ps.setString(8, x.cm_zip);
            ps.setString(9, x.cm_country);
            ps.setString(10, x.cm_dateadd);
            ps.setString(11, x.cm_datemod);
            ps.setString(12, x.cm_usermod);
            ps.setString(13, x.cm_group);
            ps.setString(14, x.cm_market);
            ps.setString(15, x.cm_creditlimit);
            ps.setString(16, x.cm_onhold);
            ps.setString(17, x.cm_carrier);
            ps.setString(18, x.cm_terms);
            ps.setString(19, x.cm_freight_type);
            ps.setString(20, x.cm_price_code);
            ps.setString(21,x.cm_disc_code);
            ps.setString(22,x.cm_tax_code);
            ps.setString(23,x.cm_salesperson);
            ps.setString(24,x.cm_ar_acct);
            ps.setString(25,x.cm_ar_cc);
            ps.setString(26,x.cm_bank);
            ps.setString(27,x.cm_curr);
            ps.setString(28,x.cm_remarks);
            ps.setString(29,x.cm_label);
            ps.setString(30,x.cm_ps_jasper);
            ps.setString(31,x.cm_iv_jasper);
            ps.setString(32,x.cm_phone);
            ps.setString(33,x.cm_email);
            ps.setString(34,x.cm_is855export);
            ps.setString(35,x.cm_is856export);
            ps.setString(36,x.cm_is810export);
            ps.setString(37,x.cm_site);
            ps.setString(38,x.cm_misc1);
            ps.setString(39,x.cm_cascade);
            ps.setString(40,x.cm_municipality);
            ps.setString(41,x.cm_county);
            ps.setString(42,x.cm_tax_exempt);
            rows = ps.executeUpdate();
            } else {
                if (addupdate) {
                 psu.setString(42, x.cm_code);
                psu.setString(1, x.cm_name);
                psu.setString(2, x.cm_line1);
                psu.setString(3, x.cm_line2);
                psu.setString(4, x.cm_line3);
                psu.setString(5, x.cm_city);
                psu.setString(6, x.cm_state);
                psu.setString(7, x.cm_zip);
                psu.setString(8, x.cm_country);
                psu.setString(9, x.cm_dateadd);
                psu.setString(10, x.cm_datemod);
                psu.setString(11, x.cm_usermod);
                psu.setString(12, x.cm_group);
                psu.setString(13, x.cm_market);
                psu.setString(14, x.cm_creditlimit);
                psu.setString(15, x.cm_onhold);
                psu.setString(16, x.cm_carrier);
                psu.setString(17, x.cm_terms);
                psu.setString(18, x.cm_freight_type);
                psu.setString(19, x.cm_price_code);
                psu.setString(20,x.cm_disc_code);
                psu.setString(21,x.cm_tax_code);
                psu.setString(22,x.cm_salesperson);
                psu.setString(23,x.cm_ar_acct);
                psu.setString(24,x.cm_ar_cc);
                psu.setString(25,x.cm_bank);
                psu.setString(26,x.cm_curr);
                psu.setString(27,x.cm_remarks);
                psu.setString(28,x.cm_label);
                psu.setString(29,x.cm_ps_jasper);
                psu.setString(30,x.cm_iv_jasper);
                psu.setString(31,x.cm_phone);
                psu.setString(32,x.cm_email);
                psu.setString(33,x.cm_is855export); 
                psu.setString(34,x.cm_is856export); 
                psu.setString(35,x.cm_is810export);
                psu.setString(36,x.cm_site);
                psu.setString(37,x.cm_misc1);
                psu.setString(38,x.cm_cascade);
                psu.setString(39,x.cm_municipality);
                psu.setString(40,x.cm_county);
                psu.setString(41,x.cm_tax_exempt);
                rows = psu.executeUpdate();
                psu.close();
                }
            } 
            return rows;
    }
        
    public static String[] updateCustMstr(cm_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateCustMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
            int rows = _updateCustMstr(x, con, ps, res);  // add cms_det
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
    
    private static int _updateCustMstr(cm_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update cm_mstr set " 
                + " cm_name = ?, cm_line1 = ?, cm_line2 = ?, "
                + "cm_line3 = ?, cm_city = ?, cm_state = ?, cm_zip = ?, "
                + "cm_country = ?, cm_dateadd = ?, cm_datemod = ?, cm_usermod = ?, "
                + "cm_group = ?, cm_market = ?, cm_creditlimit = ?, cm_onhold = ?, "
                + "cm_carrier = ?, cm_terms = ?, cm_freight_type = ?, cm_price_code = ?, "
                + "cm_disc_code = ?, cm_tax_code = ?, cm_salesperson = ?, "
                + "cm_ar_acct = ?, cm_ar_cc = ?, cm_bank = ?, cm_curr = ?, cm_remarks = ?, " 
                + "cm_label = ?, cm_ps_jasper = ?, cm_iv_jasper = ?, cm_phone = ?, cm_email = ?, "
                + "cm_is855export = ?, cm_is856export = ?, cm_is810export = ?, cm_site = ?, cm_misc1 = ?, cm_cascade = ?, "
                + "cm_municipality = ?, cm_county = ?, cm_tax_exempt = ? "
                + " where cm_code = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(42, x.cm_code);
            ps.setString(1, x.cm_name);
            ps.setString(2, x.cm_line1);
            ps.setString(3, x.cm_line2);
            ps.setString(4, x.cm_line3);
            ps.setString(5, x.cm_city);
            ps.setString(6, x.cm_state);
            ps.setString(7, x.cm_zip);
            ps.setString(8, x.cm_country);
            ps.setString(9, x.cm_dateadd);
            ps.setString(10, x.cm_datemod);
            ps.setString(11, x.cm_usermod);
            ps.setString(12, x.cm_group);
            ps.setString(13, x.cm_market);
            ps.setString(14, x.cm_creditlimit);
            ps.setString(15, x.cm_onhold);
            ps.setString(16, x.cm_carrier);
            ps.setString(17, x.cm_terms);
            ps.setString(18, x.cm_freight_type);
            ps.setString(19, x.cm_price_code);
            ps.setString(20,x.cm_disc_code);
            ps.setString(21,x.cm_tax_code);
            ps.setString(22,x.cm_salesperson);
            ps.setString(23,x.cm_ar_acct);
            ps.setString(24,x.cm_ar_cc);
            ps.setString(25,x.cm_bank);
            ps.setString(26,x.cm_curr);
            ps.setString(27,x.cm_remarks);
            ps.setString(28,x.cm_label);
            ps.setString(29,x.cm_ps_jasper);
            ps.setString(30,x.cm_iv_jasper);
            ps.setString(31,x.cm_phone);
            ps.setString(32,x.cm_email);
            ps.setString(33,x.cm_is855export); 
            ps.setString(34,x.cm_is856export); 
            ps.setString(35,x.cm_is810export); 
            ps.setString(36,x.cm_site);
            ps.setString(37,x.cm_misc1);
            ps.setString(38,x.cm_cascade);
            ps.setString(39,x.cm_municipality);
            ps.setString(40,x.cm_county);
            ps.setString(41,x.cm_tax_exempt);
            rows = ps.executeUpdate();
        return rows;
    }
        
    public static String[] deleteCustMstr(cm_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCustMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
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
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteCustMstr(x, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
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
    
    private static void _deleteCustMstr(cm_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from cm_mstr where cm_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.cm_code);
        ps.executeUpdate();
        sql = "delete from cms_det where cms_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.cm_code);
        ps.executeUpdate();
        sql = "delete from cmc_det where cmc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.cm_code);
        ps.executeUpdate();
    }
       
    public static cm_mstr getCustMstr(String[] x) {
        cm_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, cm_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from cm_mstr where cm_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cm_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cm_mstr(m, res.getString("cm_code"), res.getString("cm_name"), res.getString("cm_line1"), res.getString("cm_line2"),
                    res.getString("cm_line3"), res.getString("cm_city"), res.getString("cm_state"), res.getString("cm_zip"),
                    res.getString("cm_country"), res.getString("cm_dateadd"), res.getString("cm_datemod"), res.getString("cm_usermod"), 
                    res.getString("cm_group"), res.getString("cm_market"), res.getString("cm_creditlimit"), res.getString("cm_onhold"), 
                    res.getString("cm_carrier"), res.getString("cm_terms"), res.getString("cm_freight_type"), res.getString("cm_price_code"), 
                    res.getString("cm_disc_code"), res.getString("cm_tax_code"), res.getString("cm_salesperson"), 
                    res.getString("cm_ar_acct"), res.getString("cm_ar_cc"), res.getString("cm_bank"), res.getString("cm_curr"), res.getString("cm_remarks"), 
                    res.getString("cm_label"), res.getString("cm_ps_jasper"), res.getString("cm_iv_jasper"), res.getString("cm_phone"), res.getString("cm_email"), 
                    res.getString("cm_is855export"),res.getString("cm_is856export"),res.getString("cm_is810export"),res.getString("cm_site"), res.getString("cm_misc1"),
                    res.getString("cm_cascade"), res.getString("cm_highbal"), res.getString("cm_avgdays"), res.getString("cm_lastpaydate"), res.getString("cm_lastselldate"),
                        res.getString("cm_municipality"), res.getString("cm_county"), res.getString("cm_tax_exempt"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cm_mstr(m);
        }
        return r;
    }
    
    public static cm_mstr _getCustMstr(String code, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        
        cm_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from cm_mstr where cm_code = ? ;";
          ps = con.prepareStatement(sqlSelect); 
           ps.setString(1, code);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new cm_mstr(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cm_mstr(m, res.getString("cm_code"), res.getString("cm_name"), res.getString("cm_line1"), res.getString("cm_line2"),
                    res.getString("cm_line3"), res.getString("cm_city"), res.getString("cm_state"), res.getString("cm_zip"),
                    res.getString("cm_country"), res.getString("cm_dateadd"), res.getString("cm_datemod"), res.getString("cm_usermod"), 
                    res.getString("cm_group"), res.getString("cm_market"), res.getString("cm_creditlimit"), res.getString("cm_onhold"), 
                    res.getString("cm_carrier"), res.getString("cm_terms"), res.getString("cm_freight_type"), res.getString("cm_price_code"), 
                    res.getString("cm_disc_code"), res.getString("cm_tax_code"), res.getString("cm_salesperson"), 
                    res.getString("cm_ar_acct"), res.getString("cm_ar_cc"), res.getString("cm_bank"), res.getString("cm_curr"), res.getString("cm_remarks"), 
                    res.getString("cm_label"), res.getString("cm_ps_jasper"), res.getString("cm_iv_jasper"), res.getString("cm_phone"), res.getString("cm_email"), 
                    res.getString("cm_is855export"),res.getString("cm_is856export"),res.getString("cm_is810export"),res.getString("cm_site"), res.getString("cm_misc1"),
                    res.getString("cm_cascade"), res.getString("cm_highbal"), res.getString("cm_avgdays"), res.getString("cm_lastpaydate"), res.getString("cm_lastselldate"),
                    res.getString("cm_municipality"), res.getString("cm_county"), res.getString("cm_tax_exempt"));
                    }
            }
            return r;
    }
    
    
    public static CustShipSet getCustShipSet(String[] x ) {
        CustShipSet r = null;
        String[] m;
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustShipSet"});
            list.add(new String[]{"param1",  x[0]});
            list.add(new String[]{"param2",  x[1]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, CustShipSet.class); 
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
            
            cm_mstr cm = _getCustMstr(x[0], bscon, ps, res);
            cusData.cms_det cms = _getCMSDet(x[0], x[1], bscon, ps, res );
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new CustShipSet(m, cm, cms);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new CustShipSet(m);
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
     
    public static String[] addTermsMstr(cust_term x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addTermsMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "select * from cust_term where cut_code = ?";
        String sqlInsert = "insert into cust_term (cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent, " +
                " cut_mfi, cut_mfimonth, cut_mfiday )  " +
                " values (?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cut_code);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cut_code);
            psi.setString(2, x.cut_desc);
            psi.setInt(3, x.cut_days);
            psi.setInt(4, x.cut_discdays);
            psi.setDouble(5, x.cut_discpercent);
            psi.setString(6, x.cut_mfi);
            psi.setString(7, x.cut_mfimonth);
            psi.setString(8, x.cut_mfiday);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists};    
            }
          } 
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static String[] updateTermsMstr(cust_term x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateTermsMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update cust_term set cut_desc = ?, cut_days = ?, cut_discdays = ?, " +
                " cut_discpercent = ?, cut_mfi = ?, cut_mfimonth = ?, cut_mfiday = ? where cut_code = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cut_desc);
        ps.setInt(2, x.cut_days);
        ps.setInt(3, x.cut_discdays);
        ps.setDouble(4, x.cut_discpercent);
        ps.setString(5, x.cut_mfi);
        ps.setString(6, x.cut_mfimonth);
        ps.setString(7, x.cut_mfiday);
        ps.setString(8, x.cut_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteTermsMstr(cust_term x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteTermsMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from cust_term where cut_code = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cut_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static cust_term getTermsMstr(String[] x) {
        cust_term r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getTermsMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, cust_term.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from cust_term where cut_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cust_term(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cust_term(m, res.getString("cut_code"), 
                            res.getString("cut_desc"),
                            res.getInt("cut_days"),
                            res.getInt("cut_discdays"),
                            res.getDouble("cut_discpercent"),
                            res.getString("cut_syscode"),
                            res.getString("cut_mfi"),
                            res.getString("cut_mfimonth"),
                            res.getString("cut_mfiday")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cust_term(m);
        }
        return r;
    }
   
    
    public static String[] addUpdateCMCtrl(cm_ctrl x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateCMCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cm_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into cm_ctrl (cmc_autocust) "
                        + " values (?); "; 
        String sqlUpdate = "update cm_ctrl set cmc_autocust = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cmc_autocust);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.cmc_autocust);
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
   
    public static cm_ctrl getCMCtrl(String[] x) {
        cm_ctrl r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getCMCtrl"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, cm_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new cm_ctrl(m);
                return r;
            }
        }
        String sql = "select * from cm_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cm_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cm_ctrl(m, 
                                res.getString("cmc_autocust")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cm_ctrl(m);
        }
        return r;
    }
    
    
    
    // cms_det Customer Shipto Table
    public static String[] addCMSDet(cms_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addCMSDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
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
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _addCMSDet(x, con, ps, res, false);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
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
    
    private static void _addCMSDet(cms_det x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        if (x == null) return;
        String sqlSelect = "select * from cms_det where cms_code = ? and cms_shipto = ?";
        String sqlInsert = "insert into cms_det (cms_code, cms_shipto, cms_name, cms_line1, cms_line2, " 
                        + "cms_line3, cms_city, cms_state, cms_zip, cms_country, cms_plantcode, "
                        + "cms_contact, cms_phone, cms_email, cms_municipality, cms_county ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update cms_det set " 
                + " cms_name = ?, cms_line1 = ?, cms_line2 = ?, "
                + "cms_line3 = ?, cms_city = ?, cms_state = ?, cms_zip = ?, "
                + "cms_country = ?, cms_plantcode = ?, cms_contact = ?, cms_phone = ?, cms_email = ?, "
                + " cms_municipality = ?, cms_county = ?  "
                + " where cms_code = ? and cms_shipto = ? ; ";
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.cms_code);
            ps.setString(2, x.cms_shipto);
            res = ps.executeQuery();
             if (! res.isBeforeFirst()) {
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, x.cms_code);
            ps.setString(2, x.cms_shipto);
            ps.setString(3, x.cms_name);
            ps.setString(4, x.cms_line1);
            ps.setString(5, x.cms_line2);
            ps.setString(6, x.cms_line3);
            ps.setString(7, x.cms_city);
            ps.setString(8, x.cms_state);
            ps.setString(9, x.cms_zip);
            ps.setString(10, x.cms_country);
            ps.setString(11, x.cms_plantcode);
            ps.setString(12, x.cms_contact);
            ps.setString(13, x.cms_phone);
            ps.setString(14, x.cms_email);
            ps.setString(15, x.cms_municipality);
            ps.setString(16, x.cms_county);
            int rows = ps.executeUpdate();
            } else {
                 if (addupdate) {
                    ps = con.prepareStatement(sqlUpdate); 
                    ps.setString(15, x.cms_code);
                    ps.setString(16, x.cms_shipto);
                    ps.setString(1, x.cms_name);
                    ps.setString(2, x.cms_line1);
                    ps.setString(3, x.cms_line2);
                    ps.setString(4, x.cms_line3);
                    ps.setString(5, x.cms_city);
                    ps.setString(6, x.cms_state);
                    ps.setString(7, x.cms_zip);
                    ps.setString(8, x.cms_country);
                    ps.setString(9, x.cms_plantcode);
                    ps.setString(10, x.cms_contact);
                    ps.setString(11, x.cms_phone);
                    ps.setString(12, x.cms_email);
                    ps.setString(13, x.cms_municipality);
                    ps.setString(14, x.cms_county);
                    ps.executeUpdate();    
                 }
             }
    }
        
    public static String[] updateCMSDet(cms_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateCMSDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
            _updateCMSDet(x, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
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
    
    private static int _updateCMSDet(cms_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update cms_det set " 
                + " cms_name = ?, cms_line1 = ?, cms_line2 = ?, "
                + "cms_line3 = ?, cms_city = ?, cms_state = ?, cms_zip = ?, "
                + "cms_country = ?, cms_plantcode = ?, cms_contact = ?, cms_phone = ?, cms_email = ?, "
                + "cms_municipality = ?, cms_county = ? "
                + " where cms_code = ? and cms_shipto = ? ; ";
       ps = con.prepareStatement(sql);
        ps.setString(15, x.cms_code);
        ps.setString(16, x.cms_shipto);
            ps.setString(1, x.cms_name);
            ps.setString(2, x.cms_line1);
            ps.setString(3, x.cms_line2);
            ps.setString(4, x.cms_line3);
            ps.setString(5, x.cms_city);
            ps.setString(6, x.cms_state);
            ps.setString(7, x.cms_zip);
            ps.setString(8, x.cms_country);
            ps.setString(9, x.cms_plantcode);
            ps.setString(10, x.cms_contact);
            ps.setString(11, x.cms_phone);
            ps.setString(12, x.cms_email);
            ps.setString(13, x.cms_municipality);
            ps.setString(14, x.cms_county);
            rows = ps.executeUpdate();
        
       
        return rows;
    }
         
    public static String[] deleteCMSDet(cms_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCMSDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
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
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteCMSDet(x.cms_code, x.cms_shipto, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
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
    
    public static String[] deleteCMSDet(String cust, String shipto) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCMSDet_x"});
            list.add(new String[]{"param1",cust});
            list.add(new String[]{"param2",shipto});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteCMSDet(cust, shipto, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
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
    
    
    private static void _deleteCMSDet(String x, String y, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from cms_det where cms_code = ? and cms_shipto = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    public static cms_det getCMSDet(String shipto, String code) {
        cms_det r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getCMSDet"});
            list.add(new String[]{"param1",shipto});
            list.add(new String[]{"param2",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, cms_det.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new cms_det(m);
                return r;
            }
        }
        String sql = "select * from cms_det where cms_shipto = ? and cms_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, shipto);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cms_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cms_det(m, res.getString("cms_code"), res.getString("cms_shipto"), res.getString("cms_name"), res.getString("cms_line1"), res.getString("cms_line2"),
                    res.getString("cms_line3"), res.getString("cms_city"), res.getString("cms_state"), res.getString("cms_zip"),
                    res.getString("cms_country"), res.getString("cms_contact"), res.getString("cms_phone"),
                    res.getString("cms_email"), res.getString("cms_misc"), res.getString("cms_plantcode"),
                    res.getString("cms_type"), res.getString("cms_municipality"), res.getString("cms_county") 
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cms_det(m);
        }
        return r;
    }
    
    public static ArrayList<cms_det> getCMSDet(String code) {
        cms_det r = null;
        String[] m = new String[2];
        ArrayList<cms_det> list = new ArrayList<cms_det>();
        String sql = "select * from cms_det where cms_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cms_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cms_det(m, res.getString("cms_code"), res.getString("cms_shipto"), res.getString("cms_name"), res.getString("cms_line1"), res.getString("cms_line2"),
                    res.getString("cms_line3"), res.getString("cms_city"), res.getString("cms_state"), res.getString("cms_zip"),
                    res.getString("cms_country"), res.getString("cms_contact"), res.getString("cms_phone"),
                    res.getString("cms_email"), res.getString("cms_misc"), res.getString("cms_plantcode"),
                    res.getString("cms_type") , res.getString("cms_municipality"), res.getString("cms_county")
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cms_det(m);
               list.add(r);
        }
        return list;
    }
    
    public static ArrayList<cms_det> _getCMSDet(String code, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<cms_det> list = new ArrayList<cms_det>();
        cms_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from cms_det where cms_code = ? ;";
          ps = con.prepareStatement(sqlSelect); 
           ps.setString(1, code);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new cms_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new cms_det(m, res.getString("cms_code"), res.getString("cms_shipto"), res.getString("cms_name"), res.getString("cms_line1"), res.getString("cms_line2"),
                    res.getString("cms_line3"), res.getString("cms_city"), res.getString("cms_state"), res.getString("cms_zip"),
                    res.getString("cms_country"), res.getString("cms_contact"), res.getString("cms_phone"),
                    res.getString("cms_email"), res.getString("cms_misc"), res.getString("cms_plantcode"),
                    res.getString("cms_type") , res.getString("cms_municipality"), res.getString("cms_county")
                    );
                    list.add(r);
                }
            }
            return list;
    }
    
    public static cms_det _getCMSDet(String code, String shipto, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        
        cms_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from cms_det where cms_shipto = ? and cms_code = ? ;";
          ps = con.prepareStatement(sqlSelect); 
           ps.setString(1, shipto);
           ps.setString(2, code);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new cms_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new cms_det(m, res.getString("cms_code"), res.getString("cms_shipto"), res.getString("cms_name"), res.getString("cms_line1"), res.getString("cms_line2"),
                    res.getString("cms_line3"), res.getString("cms_city"), res.getString("cms_state"), res.getString("cms_zip"),
                    res.getString("cms_country"), res.getString("cms_contact"), res.getString("cms_phone"),
                    res.getString("cms_email"), res.getString("cms_misc"), res.getString("cms_plantcode"),
                    res.getString("cms_type"), res.getString("cms_municipality"), res.getString("cms_county")
                        
                    );
                }
            }
            return r;
    }
    
    
    // cmc_det Customer Contact table
    public static String[] addCMCDet(cmc_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addCMCDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
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
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _addCMCDet(x, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
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
    
    private static void _addCMCDet(cmc_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        if (x == null) return;
        String sqlInsert = "insert into cmc_det (cmc_code, cmc_type, cmc_name, " 
                        + "cmc_phone, cmc_fax, cmc_email ) "
                        + " values (?,?,?,?,?,?); "; 
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, x.cmc_code);
            ps.setString(2, x.cmc_type);
            ps.setString(3, x.cmc_name);
            ps.setString(4, x.cmc_phone);
            ps.setString(5, x.cmc_fax);
            ps.setString(6, x.cmc_email);
            int rows = ps.executeUpdate();
            
    }
    
    public static String[] updateCMCDet(cmc_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateCMCDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
            _updateCMCDet(x, con, ps, res);  // add cms_det 
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
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
    
    private static int _updateCMCDet(cmc_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update cmc_det set " 
                + " cmc_type = ?, cmc_name = ?, cmc_phone = ?, "
                + "cmc_fax = ?, cmc_email = ? "
                + " where cmc_code = ? and cmc_id = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(6, x.cmc_code);
        ps.setString(7, x.cmc_id);
            ps.setString(1, x.cmc_type);
            ps.setString(2, x.cmc_name);
            ps.setString(3, x.cmc_phone);
            ps.setString(4, x.cmc_fax);
            ps.setString(5, x.cmc_email);
            rows = ps.executeUpdate();
        return rows;
    }
        
    public static String[] deleteCMCDet(cmc_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCMCDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
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
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteCMCDet(x.cmc_id, x.cmc_code, con, ps, res);  // add cms_det
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
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
           
    private static void _deleteCMCDet(String x, String y, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
        
        String sql = "delete from cmc_det where cmc_id = ? and cmc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    private static void _deleteCMCDetAll(String x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
        
        
        String sql = "delete from cmc_det where cmc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        
        
    }
    
    public static cmc_det getCMCDet(String id, String code) {
        cmc_det r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getCMCDet"});
            list.add(new String[]{"param1",id});
            list.add(new String[]{"param2",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, cmc_det.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new cmc_det(m);
                return r;
            }
        }
        String sql = "select * from cmc_det where cmc_id = ? and cmc_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cmc_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cmc_det(m, res.getString("cmc_id"), res.getString("cmc_code"), 
                        res.getString("cmc_type"), res.getString("cmc_name"),
                        res.getString("cmc_phone"), res.getString("cmc_fax"), res.getString("cmc_email")                    
                    ); 
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cmc_det(m);
               
        }
        return r;
    }
    
    public static ArrayList<cmc_det> getCMCDet(String code) {
        cmc_det r = null;
        String[] m = new String[2];
        ArrayList<cmc_det> list = new ArrayList<cmc_det>();
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getCMCDets"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServCUS");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<cmc_det>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        String sql = "select * from cmc_det where cmc_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cmc_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cmc_det(m, res.getString("cmc_id"), res.getString("cmc_code"), 
                        res.getString("cmc_type"), res.getString("cmc_name"),
                        res.getString("cmc_phone"), res.getString("cmc_fax"), res.getString("cmc_email")                    
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cmc_det(m);
               list.add(r);
        }
        return list;
    }
    
    
    public static String[] addCupMstr(cup_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addCupMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cup_mstr where cup_citem = ? and cup_cust = ?";
        String sqlInsert = "insert into cup_mstr (cup_cust, cup_item, cup_citem, cup_citem2, " +
         "cup_upc, cup_userid, cup_misc, cup_sku) " 
                        + " values (?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cup_citem);
             ps.setString(2, x.cup_cust);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cup_cust);
            psi.setString(2, x.cup_item);
            psi.setString(3, x.cup_citem);
            psi.setString(4, x.cup_citem2);
            psi.setString(5, x.cup_upc);
            psi.setString(6, x.cup_userid);
            psi.setString(7, x.cup_misc);
            psi.setString(8, x.cup_sku);
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

    public static String[] addOrUpdateCupMstr(cup_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addOrUpdateCupMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cup_mstr where cup_citem = ? and cup_cust = ?";
        String sqlInsert = "insert into cup_mstr (cup_cust, cup_item, cup_citem, cup_citem2, " +
         "cup_upc, cup_userid, cup_misc, cup_sku) " 
                        + " values (?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update cup_mstr set cup_item = ?, cup_citem2 = ?, cup_upc = ?, " +
                " cup_userid = ?, cup_misc = ?, cup_sku = ?  " +   
                          " where cup_citem = ? and cup_cust = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cup_citem);
             ps.setString(2, x.cup_cust);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cup_cust);
            psi.setString(2, x.cup_item);
            psi.setString(3, x.cup_citem);
            psi.setString(4, x.cup_citem2);
            psi.setString(5, x.cup_upc);
            psi.setString(6, x.cup_userid);
            psi.setString(7, x.cup_misc);
            psi.setString(8, x.cup_sku); 
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.cup_item);
            psu.setString(2, x.cup_citem2);
            psu.setString(3, x.cup_upc);
            psu.setString(4, x.cup_userid);
            psu.setString(5, x.cup_misc);
            psu.setString(6, x.cup_sku);
            psu.setString(7, x.cup_citem);
            psu.setString(8, x.cup_cust);
            int rows = psu.executeUpdate();    
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

    public static String[] updateCupMstr(cup_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateCupMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update cup_mstr set cup_item = ?, cup_citem2 = ?, cup_upc = ?, " +
                " cup_userid = ?, cup_misc = ?, cup_sku = ?  " +   
                          " where cup_citem = ? and cup_cust = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cup_item);
        ps.setString(2, x.cup_citem2);
        ps.setString(3, x.cup_upc);
        ps.setString(4, x.cup_userid);
        ps.setString(5, x.cup_misc);
        ps.setString(6, x.cup_sku);
        ps.setString(7, x.cup_citem);
        ps.setString(8, x.cup_cust);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteCupMstr(cup_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCupMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from cup_mstr where cup_citem = ? and cup_cust = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cup_citem);
        ps.setString(2, x.cup_cust);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static cup_mstr getCupMstr(String[] x) {
        cup_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getCupMstr"});
            list.add(new String[]{"param1",x[0]});
            if (x.length > 1) {
            list.add(new String[]{"param2",x[1]});            
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, cup_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new cup_mstr(m);
                return r;
            }
        }
        String sql = "";
         if (x.length >= 2 && ! x[1].isEmpty()) {
            sql = "select * from cup_mstr where cup_cust = ? and cup_citem = ?;";
         } else {
            sql = "select * from cup_mstr where cup_citem = ? limit 1 ;";  
         }
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        if (x.length >= 2 && ! x[1].isEmpty()) {
        ps.setString(2, x[1]);
        }
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cup_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cup_mstr(m, res.getString("cup_cust"), 
                            res.getString("cup_item"),
                            res.getString("cup_citem"),
                            res.getString("cup_citem2"),
                            res.getString("cup_upc"),
                            res.getString("cup_userid"),
                            res.getString("cup_ts"),    
                            res.getString("cup_misc"),
                            res.getString("cup_sku")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cup_mstr(m);
        }
        return r;
    }
    
    public static String[] addCprMstr(cpr_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addCprMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cpr_mstr where cpr_cust = ? and cpr_uom = ? and cpr_curr = ? and cpr_type = ? and cpr_volqty = ?";
        String sqlInsert = "insert into cpr_mstr (cpr_cust, cpr_item, cpr_type, cpr_desc, cpr_uom, cpr_curr, "
                        + "cpr_price, cpr_volqty, cpr_expire)  " 
                        + " values (?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cpr_cust);
             ps.setString(2, x.cpr_uom);
             ps.setString(3, x.cpr_curr);
             ps.setString(4, x.cpr_type);
             ps.setDouble(5, x.cpr_volqty);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cpr_cust);
            psi.setString(2, x.cpr_item);
            psi.setString(3, x.cpr_type);
            psi.setString(4, x.cpr_desc);
            psi.setString(5, x.cpr_uom);
            psi.setString(6, x.cpr_curr);
            psi.setDouble(7, x.cpr_price);
            psi.setDouble(8, x.cpr_volqty);
            psi.setString(9, x.cpr_expire);
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

    public static String[] updateCprMstr(cpr_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateCprMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update cpr_mstr set cpr_desc = ?, cpr_price = ?, cpr_expire = ? " +   
                " where cpr_cust = ? and cpr_item = ? and cpr_uom = ? and cpr_curr = ? and cpr_type = ? and cpr_volqty = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, x.cpr_desc);
        ps.setDouble(2, x.cpr_price);
        ps.setString(3, x.cpr_expire);
        ps.setString(4, x.cpr_cust);
        ps.setString(5, x.cpr_item);
        ps.setString(6, x.cpr_uom);
        ps.setString(7, x.cpr_curr);
        ps.setString(8, x.cpr_type);
        ps.setDouble(9, x.cpr_volqty);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addOrUpdateCprMstr(cpr_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addOrUpdateCprMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cpr_mstr where cpr_cust = ? and cpr_item = ? and cpr_uom = ? and cpr_curr = ? and cpr_type = ? and cpr_volqty = ?";
        String sqlInsert = "insert into cpr_mstr (cpr_cust, cpr_item, cpr_type, cpr_desc, cpr_uom, cpr_curr, "
                        + "cpr_price, cpr_volqty, cpr_expire)  " 
                        + " values (?,?,?,?,?,?,?,?,?); ";  
        String sqlUpdate = "update cpr_mstr set cpr_desc = ?, cpr_price = ?, cpr_expire = ? " +   
                " where cpr_cust = ? and cpr_item = ? and cpr_uom = ? and cpr_curr = ? and cpr_type = ? and cpr_volqty = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cpr_cust);
             ps.setString(2, x.cpr_item);
             ps.setString(3, x.cpr_uom);
             ps.setString(4, x.cpr_curr);
             ps.setString(5, x.cpr_type);
             ps.setDouble(6, x.cpr_volqty);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cpr_cust);
            psi.setString(2, x.cpr_item);
            psi.setString(3, x.cpr_type);
            psi.setString(4, x.cpr_desc);
            psi.setString(5, x.cpr_uom);
            psi.setString(6, x.cpr_curr);
            psi.setDouble(7, x.cpr_price);
            psi.setDouble(8, x.cpr_volqty);
            psi.setString(9, x.cpr_expire);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.cpr_desc);
            psu.setDouble(2, x.cpr_price);
            psu.setString(3, x.cpr_expire);
            psu.setString(4, x.cpr_cust);
            psu.setString(5, x.cpr_item);
            psu.setString(6, x.cpr_uom);
            psu.setString(7, x.cpr_curr);
            psu.setString(8, x.cpr_type);
            psu.setDouble(9, x.cpr_volqty);
            int rows = psu.executeUpdate();    
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

    
    public static String[] deleteCprMstr(cpr_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCprMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from cpr_mstr where cpr_cust = ? and cpr_item = ? and cpr_uom = ? and cpr_curr = ? and cpr_type = ? and cpr_volqty = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cpr_cust);
        ps.setString(2, x.cpr_item);
        ps.setString(3, x.cpr_uom);
        ps.setString(4, x.cpr_curr);
        ps.setString(5, x.cpr_type);
        ps.setDouble(6, x.cpr_volqty);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static cpr_mstr getCprMstr(String[] x) {
        cpr_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getCprMstr"});
            list.add(new String[]{"param1",x[0]}); // cust
            list.add(new String[]{"param2",x[1]}); // item
            list.add(new String[]{"param3",x[2]}); // uom
            list.add(new String[]{"param4",x[3]}); // curr
            list.add(new String[]{"param5",x[4]}); // type
            list.add(new String[]{"param6",x[5]}); // volqty
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, cpr_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new cpr_mstr(m);
                return r;
            }
        }
        String sql = "select * from cpr_mstr where cpr_cust = ? and cpr_item = ? and cpr_uom = ? and cpr_curr = ? and cpr_type = ? and cpr_volqty = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
        ps.setString(3, x[2]);
        ps.setString(4, x[3]);
        ps.setString(5, x[4]);
        ps.setString(6, x[5]);
        
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cpr_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                      
                        r = new cpr_mstr(m, res.getString("cpr_cust"), 
                            res.getString("cpr_item"),
                            res.getString("cpr_type"),
                            res.getString("cpr_desc"),
                            res.getString("cpr_uom"),
                            res.getString("cpr_curr"),
                            res.getDouble("cpr_price"),     
                            res.getDouble("cpr_volqty"),
                            res.getString("cpr_expire")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cpr_mstr(m);
        }
        return r;
    }
    
    public static ArrayList<cpr_mstr> getCprPriceLists(String code) {
        cpr_mstr r = null;
        String[] m = new String[2];
        ArrayList<cpr_mstr> list = new ArrayList<cpr_mstr>();
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getCprPriceLists"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServCUS");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<cpr_mstr>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        String sql = "select * from cpr_mstr where cpr_cust = ? and cpr_type <> 'DISCOUNT' order by cpr_item ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cpr_mstr(m);
                } else {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    while(res.next()) {
                        r = new cpr_mstr(m, res.getString("cpr_cust"), 
                            res.getString("cpr_item"),
                            res.getString("cpr_type"),
                            res.getString("cpr_desc"),
                            res.getString("cpr_uom"),
                            res.getString("cpr_curr"),
                            res.getDouble("cpr_price"),     
                            res.getDouble("cpr_volqty"),
                            res.getString("cpr_expire")
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cpr_mstr(m);
               list.add(r);
        }
        return list;
    }
    
    public static ArrayList<cpr_mstr> getCprDiscLists(String code) {
        cpr_mstr r = null;
        String[] m = new String[2];
        ArrayList<cpr_mstr> list = new ArrayList<cpr_mstr>();
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getCprDiscLists"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServCUS");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<cpr_mstr>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        String sql = "select * from cpr_mstr where cpr_cust = ? and cpr_type = 'DISCOUNT' order by cpr_item ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cpr_mstr(m);
                } else {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    while(res.next()) {
                        r = new cpr_mstr(m, res.getString("cpr_cust"), 
                            res.getString("cpr_item"),
                            res.getString("cpr_type"),
                            res.getString("cpr_desc"),
                            res.getString("cpr_uom"),
                            res.getString("cpr_curr"),
                            res.getDouble("cpr_price"),     
                            res.getDouble("cpr_volqty"),
                            res.getString("cpr_expire")
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cpr_mstr(m);
               list.add(r);
        }
        return list;
    }
    
    public static String[] addSlspMstr(slsp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addSlspMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "select * from slsp_mstr where slsp_id = ?";
        String sqlInsert = "insert into slsp_mstr (slsp_id, slsp_name, slsp_line1," +
        " slsp_line2, slsp_city, slsp_state, slsp_zip, " +
        " slsp_phone, slsp_email, slsp_company, slsp_active, " +
        " slsp_rate )  " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.slsp_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.slsp_id);
            psi.setString(2, x.slsp_name);
            psi.setString(3, x.slsp_line1);
            psi.setString(4, x.slsp_line2);
            psi.setString(5, x.slsp_city);
            psi.setString(6, x.slsp_state);
            psi.setString(7, x.slsp_zip);
            psi.setString(8, x.slsp_phone);
            psi.setString(9, x.slsp_email);
            psi.setString(10, x.slsp_company);
            psi.setString(11, x.slsp_active);
            psi.setDouble(12, x.slsp_rate);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists};    
            }
          } 
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static String[] updateSlspMstr(slsp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateSlspMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update slsp_mstr set slsp_name = ?, slsp_line1 = ?," +
        " slsp_line2 = ?, slsp_city = ?, slsp_state = ?, slsp_zip = ?, " +
        " slsp_phone = ?, slsp_email = ?, slsp_company = ?, slsp_active = ?, " +
        " slsp_rate = ? where slsp_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(12, x.slsp_id);
        ps.setString(1, x.slsp_name);
        ps.setString(2, x.slsp_line1);
        ps.setString(3, x.slsp_line2);
        ps.setString(4, x.slsp_city);
        ps.setString(5, x.slsp_state);
        ps.setString(6, x.slsp_zip);
        ps.setString(7, x.slsp_phone);
        ps.setString(8, x.slsp_email);
        ps.setString(9, x.slsp_company);
        ps.setString(10, x.slsp_active);
        ps.setDouble(11, x.slsp_rate);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteSlspMstr(slsp_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteSlspMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from slsp_mstr where slsp_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.slsp_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static slsp_mstr getSlspMstr(String[] x) {
        slsp_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getSlspMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServCUS");
                r = objectMapper.readValue(returnstring, slsp_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from slsp_mstr where slsp_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new slsp_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new slsp_mstr(m, res.getString("slsp_id"), 
                            res.getString("slsp_name"),
                            res.getString("slsp_line1"),
                            res.getString("slsp_line2"),
                            res.getString("slsp_city"),
                            res.getString("slsp_state"),
                            res.getString("slsp_zip"),
                            res.getString("slsp_phone"),
                            res.getString("slsp_email"),
                            res.getString("slsp_company"),
                            res.getString("slsp_active"),
                            res.getDouble("slsp_rate")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new slsp_mstr(m);
        }
        return r;
    }
   
    
         
    // miscellaneous functions
    
    public static String getCustBrowseView(String[] keys) {
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
                
                if (keys[0].equals("cm_code")) {
                res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip " +
                        " from cm_mstr where cm_code like " + "'" + '%' + keys[1] + '%' + "'" + ";");
                }
                if (keys[0].equals("cm_name")) {
                res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip " +
                        " from cm_mstr where cm_name like " + "'" + '%' + keys[1] + '%' + "'" + ";");
                }
                if (keys[0].equals("cm_zip")) {
                res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip " +
                        " from cm_mstr where cm_zip like " + "'" + '%' + keys[1] + '%' + "'" + ";");
                }
                
                
                    while (res.next()) {
                   
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("cm_line1"));
                        rowArray.put(res.getString("cm_city"));
                        rowArray.put(res.getString("cm_state"));
                        rowArray.put(res.getString("cm_zip"));
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
    
    public static ArrayList<String[]> getCustMaintInit(String panelClassName, String userid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustMaintInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
         
         String defaultsite = "";
        ArrayList<String[]> lines = new ArrayList<String[]>();
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
      
            String[] sites = null;
            boolean allsites = false;
            res = st.executeQuery("select user_allowedsites from user_mstr where user_id = " + "'" + userid + "'" + ";");
            while (res.next()) {
              if (res.getString("user_allowedsites").equals("*")) {
                  allsites = true;
              } else {
                  sites = res.getString("user_allowedsites").split(",");
              }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
           while (res.next()) {
               String[] s = new String[2];
               s[0] = "canupdate";
               s[1] = "0";
               if (res.getString("perm_readonly").equals("0")) {
                 s[1] = "1";
               }
               
               lines.add(s);
           }
            
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select ov_site, ov_currency from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
               defaultsite = s[1];
            }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
            }
            
            
            res = st.executeQuery("select cur_id from cur_mstr ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "currencies";
               s[1] = res.getString("cur_id");
               lines.add(s);
            }
            
             res = st.executeQuery("select bk_id from bk_mstr order by bk_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "banks";
               s[1] = res.getString("bk_id");
               lines.add(s);
            }
            
             res = st.executeQuery("select cut_code from cust_term order by cut_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "terms";
               s[1] = res.getString("cut_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select ac_id from ac_mstr order by ac_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "accounts";
               s[1] = res.getString("ac_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select dept_id from dept_mstr order by dept_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "depts";
               s[1] = res.getString("dept_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select tax_code from tax_mstr order by tax_code  ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "taxcodes";
               s[1] = res.getString("tax_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select lblz_code from label_zebra order by lblz_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "labels";
               s[1] = res.getString("lblz_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'country' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "countries";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "states";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select car_id from car_mstr order by car_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "carriers";
               s[1] = res.getString("car_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select cmc_autocust from cm_ctrl;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "autocust";
               s[1] = res.getString("cmc_autocust");
               lines.add(s);
            }
            
            /*
             res = st.executeQuery("select car_id from car_mstr order by car_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "freight";
               s[1] = res.getString("car_id");
               lines.add(s);
            }
            */
            
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
        return lines;
    }
    
    public static String getCustXrefBrowseView(String[] keys) {
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
                if (keys[0].equals("item")) {
                res = st.executeQuery("SELECT * FROM  cup_mstr  left outer join cm_mstr on cm_code = cup_cust where " +
                    " cup_item like " + "'%" + keys[1] + "%' ;") ;
                } else {
                    res = st.executeQuery("SELECT * FROM  cup_mstr left outer join cm_mstr on cm_code = cup_cust where " +
                    " cup_citem like " + "'%" + keys[1] + "%' ;") ;
                }
               
                while (res.next()) {
                        i++;
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cup_cust"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("cup_item"));
                        rowArray.put(res.getString("cup_citem"));
                        rowArray.put(res.getString("cup_citem2"));
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
    
    public static String getCustPriceBrowseView(String[] keys) {
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
                String expire = "";
                if (keys[0].equals("item")) {
                res = st.executeQuery("SELECT * FROM  cpr_mstr where " +
                    " cpr_item like " + "'" + "%" + keys[1] + "%' order by cpr_cust, cpr_item;") ;
                } else {
                    res = st.executeQuery("SELECT * FROM  cpr_mstr where " +
                    " cpr_cust like " + "'" + "%" + keys[1] + "%' order by cpr_cust, cpr_item ;") ;
                }
                if (res.getString("cpr_expire") == null || res.getString("cpr_expire").equals("null") ) {
                        expire = "";  
                      } else {
                        expire = res.getString("cpr_expire");  
                      } 
               
                while (res.next()) {
                        i++;
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cpr_cust"));
                        rowArray.put(res.getString("cpr_type"));
                        rowArray.put(res.getString("cpr_item"));
                        rowArray.put(res.getString("cpr_uom"));
                        rowArray.put(res.getString("cpr_curr"));
                        rowArray.put(expire);
                        rowArray.put(res.getString("cpr_volqty"));
                        rowArray.put(res.getString("cpr_price"));
                        rowArray.put(res.getString("cpr_disc"));
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
    
    public static String getSalesRepBrowseView(String[] keys) {
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
                double repamt = 0.00;
                
                res = st.executeQuery("SELECT sh_id, sh_confdate, so_slsperson1, so_slsperson2, cm_code, cm_name, sh_po, ar_amt, slsp_rate from ship_mstr " +
                        " inner join ar_mstr on ar_nbr = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " inner join so_mstr on so_nbr = sh_so " + 
                        " inner join slsp_mstr on slsp_name = so_slsperson1 " +  
                        " where sh_confdate >= " + "'" + keys[0]  + "'" + 
                        " AND sh_confdate <= " + "'" + keys[1] + "'" +
                         " AND so_slsperson1 >= " + "'" + keys[2] + "'" +
                         " AND so_slsperson1 <= " + "'" + keys[3] + "'" +
                         " AND sh_site = " + "'" + keys[4] + "'" +
                         " order by sh_id desc ;");   
               
                repamt = (res.getDouble("ar_amt") * (res.getDouble("slsp_rate") / 100));
                
                while (res.next()) {
                        i++;
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("sh_id"));
                        rowArray.put(res.getString("sh_confdate"));
                        rowArray.put(res.getString("so_slsperson1"));
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("sh_po"));
                        rowArray.put(currformatDouble(res.getDouble("ar_amt")));
                        rowArray.put(currformatDouble(repamt));
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
    
    public static String getCusRptPickerData(String[] keys) {
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
                if (keys[0].equals("custAddrInfoByRange")) {
                res = st.executeQuery("SELECT cm_code, cm_market, cm_name, cm_line1, " +
                    " cm_city, cm_state, cm_zip, cm_market, cm_phone, cm_email, " +
                    " cm_terms, cm_bank, cm_curr, cm_ar_acct, cm_onhold " +
                    "from cm_mstr " +
                    " where cm_code >= " + "'" + keys[1] + "'" +
                    " and cm_code <= " + "'" + keys[2] + "'" +
                    "order by cm_code ;");
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("cm_line1"));
                            rowArray.put(res.getString("cm_city"));
                            rowArray.put(res.getString("cm_state"));
                            rowArray.put(res.getString("cm_zip"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("custPhoneEmailByRange")) {
                res = st.executeQuery("SELECT cm_code, cm_name, " +
                    " cm_phone, cm_email " +
                    "from cm_mstr " +
                    " where cm_code >= " + "'" + keys[1] + "'" +
                    " and cm_code <= " + "'" + keys[2] + "'" +
                    "order by cm_code ;");
               
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("cm_phone"));
                            rowArray.put(res.getString("cm_email"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("custFinanceInfoByRange")) {
                    res = st.executeQuery("SELECT cm_code, cm_market, cm_name,  " +
                        " cm_terms, cm_bank, cm_curr, cm_ar_acct, cm_ar_cc, " +
                        " case when cm_onhold = '0' then 'false' else 'true' end as 'cm_onhold' " +
                        "from cm_mstr " +
                        " where cm_code >= " + "'" + keys[1] + "'" +
                        " and cm_code <= " + "'" + keys[2] + "'" +
                        "order by cm_code ;");
              
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("cm_terms"));
                            rowArray.put(res.getString("cm_bank"));
                            rowArray.put(res.getString("cm_curr"));
                            rowArray.put(res.getString("cm_ar_acct"));
                            rowArray.put(res.getString("cm_ar_cc"));
                            rowArray.put(res.getString("cm_onhold"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("custShipTosByRange")) {
                    res = st.executeQuery("SELECT cm_code, cm_name,  " +
                        " cms_shipto, cms_name, cms_line1, cms_city, cms_state, cms_zip " +
                          "from cm_mstr inner join cms_det on cms_code = cm_code " +
                        " where cm_code >= " + "'" + keys[1] + "'" +
                        " and cm_code <= " + "'" + keys[2] + "'" +
                        "order by cm_code ;");
              
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("cms_shipto"));
                            rowArray.put(res.getString("cms_name"));
                            rowArray.put(res.getString("cms_line1"));
                            rowArray.put(res.getString("cms_city"));
                            rowArray.put(res.getString("cms_state"));
                            rowArray.put(res.getString("cms_zip"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("custTotalSalesByRange")) {
                    res = st.executeQuery("select ar_id, ar_cust, cm_name, ar_type, " +
                               " ar_ref, ar_nbr, ar_effdate, ar_invdate, ar_duedate, ar_curr, ar_amt, ar_base_amt, ar_open_amt, " +
                               " case when ar_status = 'c' then 'closed' else 'open' end as 'status', " +
                               " ar_curr, ar_acct " +
                               " from ar_mstr inner join cm_mstr on cm_code = ar_cust " +
                               " where " +
                               " ar_cust >= " + "'" + keys[1] + "'" +
                               " and ar_cust <= " + "'" + keys[2] + "'" +        
                               " and ar_effdate >= " + "'" + keys[3] + "'" +
                               " and ar_effdate <= " + "'" + keys[4] + "'" + ";");  
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("ar_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(keys[3]);
                            rowArray.put(keys[4]);
                            rowArray.put(currformat(res.getString("ar_amt")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("custXrefByRange")) {
                    res = st.executeQuery("SELECT cm_code, cm_name,  " +
                        " cup_item, cup_citem, cup_citem2, cup_sku, cup_upc, cup_misc " +
                          "from cm_mstr inner join cup_mstr on cup_cust = cm_code " +
                        " where cm_code >= " + "'" + keys[1] + "'" +
                        " and cm_code <= " + "'" + keys[2] + "'" +
                        "order by cm_code ;");
              
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("cup_item"));
                            rowArray.put(res.getString("cup_citem"));
                            rowArray.put(res.getString("cup_sku"));
                            rowArray.put(res.getString("cup_upc"));
                            rowArray.put(res.getString("cup_citem2"));
                            rowArray.put(res.getString("cup_misc"));
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
    
    
    public static String[] getCustInfo(String cust) {
           // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site, taxcode, cascadediscount
        String[] custinfo = new String[]{"","","","","","","", "", "", ""};
        String sql = "select cm_ar_acct, cm_ar_cc, cm_curr, cm_bank, cm_terms, cm_carrier, cm_onhold, cm_site, cm_tax_code, cm_cascade from cm_mstr where cm_code = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, cust);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               custinfo[0] = res.getString("cm_ar_acct");
               custinfo[1] = res.getString("cm_ar_cc");
               custinfo[2] = res.getString("cm_curr");
               custinfo[3] = res.getString("cm_bank");
               custinfo[4] = res.getString("cm_terms");
               custinfo[5] = res.getString("cm_carrier");
               custinfo[6] = res.getString("cm_onhold");
               custinfo[7] = res.getString("cm_site");
               custinfo[8] = res.getString("cm_tax_code");
               custinfo[9] = res.getString("cm_cascade");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return custinfo;
    }
    
    public static String[] getCustAddressInfo(String cust) {
          
        String[] custinfo = new String[]{"","","","","","","","",""};
        String sql = "select cm_name, cm_line1, cm_line2, cm_line3, cm_city, cm_state, cm_zip, cm_country, cm_email from cm_mstr where cm_code = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, cust);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               custinfo[0] = res.getString("cm_name");
               custinfo[1] = res.getString("cm_line1");
               custinfo[2] = res.getString("cm_line2");
               custinfo[3] = res.getString("cm_line3");
               custinfo[4] = res.getString("cm_city");
               custinfo[5] = res.getString("cm_state");
               custinfo[6] = res.getString("cm_zip");
               custinfo[7] = res.getString("cm_country");
               custinfo[8] = res.getString("cm_email"); 
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return custinfo;
    }
    
    public static String[] getShipAddressInfo(String cust, String ship) {
           // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
        String[] custinfo = new String[]{"","","","","","","","",""};
        String sql = "select cms_shipto, cms_name, cms_line1, cms_line2, cms_line3, cms_city, cms_state, cms_zip, cms_country, cms_plantcode from cms_det where cms_code = ? and cms_shipto = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, cust);
        ps.setString(2, ship);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               custinfo[0] = res.getString("cms_shipto");
               custinfo[1] = res.getString("cms_name");
               custinfo[2] = res.getString("cms_line1");
               custinfo[3] = res.getString("cms_line2");
               custinfo[4] = res.getString("cms_line3");
               custinfo[5] = res.getString("cms_city");
               custinfo[6] = res.getString("cms_state");
               custinfo[7] = res.getString("cms_zip");
               custinfo[8] = res.getString("cms_country");                   
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return custinfo;
    }
    
    public static ArrayList<String> getTermsUsage(String terms) {
        ArrayList<String> usage = new ArrayList<String>();
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
                  
            res = st.executeQuery("select cm_code from cm_mstr where cm_terms = " + "'" + terms + "'" + ";");
            while (res.next()) {
               usage.add(res.getString("cm_code"));
            }
           
            res = st.executeQuery("select vd_addr from vd_mstr where vd_terms = " + "'" + terms + "'" + ";");
            while (res.next()) {
               usage.add(res.getString("vd_addr"));
            }
            
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
        return usage;
    }
    
    
    public static String getCustSalesAcct(String cust) {
           String myitem = "";
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
                res = st.executeQuery("select cm_ar_acct from cm_mstr where cm_code = " + "'" + cust + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("cm_ar_acct");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
                
    public static String getCustSalesCC(String cust) {
           String myitem = "";
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
                res = st.executeQuery("select cm_ar_cc from cm_mstr where cm_code = " + "'" + cust + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("cm_ar_cc");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
         
    public static String getCustCurrency(String cust) {
           String myitem = null;
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
                res = st.executeQuery("select cm_curr from cm_mstr where cm_code = " + "'" + cust + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("cm_curr");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
         
    public static String getCustTerms(String cust) {
           String myitem = null;
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
                res = st.executeQuery("select cm_terms from cm_mstr where cm_code = " + "'" + cust + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("cm_terms");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
    
    public static String getCustEmail(String cust) {
        String x = "";
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
            
                res = st.executeQuery("select cm_email from cm_mstr where cm_code = " + "'" + cust + "'" + ";");
               while (res.next()) {
                    x = res.getString("cm_email");
                }
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
    }
    
    public static String getCustEmailByInvoice(String invoice) {
        String x = "";
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
            
                res = st.executeQuery("select cm_email from cm_mstr " +
                        " inner join ship_mstr on sh_cust = cm_code " +
                        " where sh_id = " + "'" + invoice + "'" + ";");
               while (res.next()) {
                    x = res.getString("cm_email");
                }
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
    }
        
    public static ArrayList getcustmstrlist() {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getcustmstrlist"});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
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
            try{
                

                res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
               while (res.next()) {
                    myarray.add(res.getString("cm_code"));
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
    
    public static ArrayList getdisclist() {
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
            try{
                
                java.util.Date now = new java.util.Date();
                res = st.executeQuery("select cpr_item from cpr_mstr " +
                      " where cpr_type = 'DISCOUNT' " + 
                      " AND (cpr_expire = null OR cpr_expire >= " + "'" + BlueSeerUtils.setDateFormat(now) + "'" + ") " +
                      ";");
               while (res.next()) {
                    myarray.add(res.getString("cpr_item"));
                }
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
    
    public static ArrayList<String[]> getDiscountByKey(String cust, String key) {
       ArrayList<String[]> myarray = new ArrayList<String[]>();
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
                
                java.util.Date now = new java.util.Date();
                res = st.executeQuery("select cpr_desc, cpr_disc from cpr_mstr " +
                      " where cpr_type = 'DISCOUNT' " + 
                      " AND cpr_cust = " + "'" + cust + "'"  +
                      " AND cpr_item = " + "'" + key + "'"  +
                      ";");
               while (res.next()) {
                    myarray.add(new String[]{res.getString("cpr_desc"), res.getString("cpr_disc")});
                }
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
    
    public static String getDiscCodeByCust(String cust) {
       String x = "";
       
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
                
                res = st.executeQuery("select cm_disc_code from cm_mstr where cm_code = " + "'" + cust + "'" + ";");
                while (res.next()) {
                  x = res.getString("cm_disc_code");
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
        return x;
        
    }
    
    
    public static ArrayList<String[]> getDiscountRecsByCust(String cust) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDiscountRecsByCust"});
            list.add(new String[]{"param1",  cust});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
       ArrayList<String[]> myarray = new ArrayList<String[]>();
       java.util.Date now = new java.util.Date(); 
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
                res = st.executeQuery("select cpr_price, cpr_item from cpr_mstr where cpr_cust = " + "'" + cust + "'" + 
                        " AND cpr_type = " + "'" + "DISCOUNT" + "'" +
                        " AND ( cpr_expire is null OR cpr_expire >= " + "'" + BlueSeerUtils.setDateFormat(now) + "'" + " ) " +
                        ";");                
               while (res.next()) {
                    myarray.add(new String[]{res.getString("cpr_item"), res.getString("cpr_price")});
                }
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
    
    
     
    
    
    public static ArrayList gettermsmstrlist() {
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
            try{
                

                res = st.executeQuery("select cut_code from cust_term order by cut_code ;");
               while (res.next()) {
                    myarray.add(res.getString("cut_code"));
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
    
    
    public static ArrayList getcustmstrlistBetween(String from, String to) {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select cm_code from cm_mstr "
                        + " where cm_code >= " + "'" + from + "'"
                        + " and cm_code <= " + "'" + to + "'"
                        + " order by cm_code ;");
                while (res.next()) {
                    myarray.add(res.getString("cm_code"));
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
        return myarray;

    }

    public static ArrayList getCustShipToListAll() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select cms_shipto from cms_det order by cms_shipto ;");
                while (res.next()) {
                    myarray.add(res.getString("cms_shipto"));
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
        return myarray;

    }


    public static ArrayList getcustshipmstrlist(String cust) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getcustshipmstrlist"});
            list.add(new String[]{"param1",  cust});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServCUS"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select cms_shipto from cms_det where cms_code = " + "'" + cust + "'" + " order by cms_shipto;");
                while (res.next()) {
                    myarray.add(res.getString("cms_shipto"));

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
        return myarray;

    }

    public static String getcustBillTo(String shipto) {
        String mystring = "";
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

                res = st.executeQuery("select cms_code from cms_det where cms_shipto = " + "'" + shipto + "'" + " order by cms_shipto;");
                while (res.next()) {
                    mystring = res.getString("cms_code");
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
        return mystring;

    }
        
    public static String getCustAltItem(String cust, String part) {
   String mystring = "";
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
            res = st.executeQuery("select cup_citem2 from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                  " AND cup_item = " + "'" + part + "'" + ";");
           while (res.next()) {
               mystring = res.getString("cup_citem2");

            }

       }
        catch (SQLException s){
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
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return mystring;

}

    public static String getCustSku(String cust, String part) {
    String mystring = "";
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
            res = st.executeQuery("select cup_sku from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                  " AND cup_item = " + "'" + part + "'" + ";");
           while (res.next()) {
               mystring = res.getString("cup_sku");

            }

       }
        catch (SQLException s){
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
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return mystring;

    }

    public static String getCustPartFromPart(String cust, String part) {
    String mystring = "";
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
            res = st.executeQuery("select cup_citem from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                  " AND cup_item = " + "'" + part + "'" + ";");
           while (res.next()) {
               mystring = res.getString("cup_citem");

            }

       }
        catch (SQLException s){
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
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return mystring;

    }

    public static String getCustFromOrder(String order) {
           String myitem = "";
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
                res = st.executeQuery("select so_cust from so_mstr where so_nbr = " + "'" + order + "';" );
               while (res.next()) {
                myitem = res.getString("so_cust");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
              
    public static String getCustName(String cust) {
    if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getCustName"});
            list.add(new String[]{"param1", cust});
            try {
                return sendServerPost(list, "", null, "dataServCUS"); 
            } catch (IOException ex) {
                bslog(ex);
                return "";
            }
        }    
    String myitem = "";
    try{

        Connection con = null;
        if (ds != null) {
        con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try  {
        res = st.executeQuery("select cm_name from cm_mstr where cm_code = " + "'" + cust + "';" );
       while (res.next()) {
        myitem = res.getString("cm_name");                    
        }

    }
    catch (SQLException s){
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
    }
    catch (Exception e){
    MainFrame.bslog(e);
    }
    return myitem;

}

    public static String getShipName(String cust, String ship) {
    if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getShipName"});
            list.add(new String[]{"param1", cust});
            list.add(new String[]{"param2", ship});
            try {
                return sendServerPost(list, "", null, "dataServCUS"); 
            } catch (IOException ex) {
                bslog(ex);
                return "";
            }
        }    
    String myitem = "";
    try{

        Connection con = null;
        if (ds != null) {
        con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try  {
        res = st.executeQuery("select cms_name from cms_det where cms_code = " + "'" + cust + "'" +
                     " AND cms_shipto = " + "'" + ship + "'" + ";");
       while (res.next()) {
        myitem = res.getString("cms_name");                    
        }

    }
    catch (SQLException s){
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
    }
    catch (Exception e){
    MainFrame.bslog(e);
    }
    return myitem;

}

    
    public static String getCustLabel(String cust) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getCustLabel"});
            list.add(new String[]{"param1", cust});
            try {
                return sendServerPost(list, "", null, "dataServCUS"); 
            } catch (IOException ex) {
                bslog(ex);
                return "";
            }
        }
        String myitem = "";
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
            res = st.executeQuery("select cm_label from cm_mstr where cm_code = " + "'" + cust + "';" );
           while (res.next()) {
            myitem = res.getString("cm_label");                    
            }

        }
        catch (SQLException s){
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
        }
        catch (Exception e){
        MainFrame.bslog(e);
        }
        return myitem;

}

    public static String getCustLogo(String cust) {
        String myitem = "";
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
            res = st.executeQuery("select cm_logo from cm_mstr where cm_code = " + "'" + cust + "';" );
           while (res.next()) {
            myitem = res.getString("cm_logo");                    
            }

        }
        catch (SQLException s){
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
        } catch (Exception e){
        MainFrame.bslog(e);
        }
        return myitem;

}

    public static String getCustInvoiceJasper(String cust) {
           String myitem = "";
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
                res = st.executeQuery("select cm_iv_jasper from cm_mstr where cm_code = " + "'" + cust + "';" );
               while (res.next()) {
                myitem = res.getString("cm_iv_jasper");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
          
    public static String getCustShipperJasper(String cust) {
           String myitem = "";
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
                res = st.executeQuery("select cm_ps_jasper from cm_mstr where cm_code = " + "'" + cust + "';" );
               while (res.next()) {
                myitem = res.getString("cm_ps_jasper");                    
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
        return myitem;
        
    } 
             
    public record CustShipSet(String[] m, cm_mstr cm, cms_det cms) {
        public CustShipSet(String[] m) {
            this (m, null, null);
        }
    }  
    
    public record cm_mstr(String[] m, String cm_code, String cm_name, String cm_line1, String cm_line2,
    String cm_line3, String cm_city, String cm_state, String cm_zip,
    String cm_country, String cm_dateadd, String cm_datemod, String cm_usermod, 
    String cm_group, String cm_market, String cm_creditlimit, String cm_onhold, 
    String cm_carrier, String cm_terms, String cm_freight_type, String cm_price_code,
    String cm_disc_code, String cm_tax_code, String cm_salesperson, String cm_ar_acct,
    String cm_ar_cc, String cm_bank, String cm_curr, String cm_remarks,
    String cm_label, String cm_ps_jasper, String cm_iv_jasper, String cm_phone, String cm_email,
    String cm_is855export, String cm_is856export, String cm_is810export, String cm_site, String cm_misc1, String cm_cascade,
    String cm_highbal, String cm_avgdays, String cm_lastpaydate, String cm_lastselldate,
    String cm_municipality, String cm_county, String cm_tax_exempt) {
        public cm_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", ""
                    );
        }
    }
    
    public record cms_det(String[] m, String cms_code, String cms_shipto, 
        String cms_name, String cms_line1, String cms_line2,
        String cms_line3, String cms_city, String cms_state, 
        String cms_zip, String cms_country, String cms_contact, String cms_phone, String cms_email,
        String cms_misc, String cms_plantcode, String cms_type, String cms_municipality, String cms_county) {
        public cms_det(String[] m) {
            this(m,"","","","","","","","","","",
                    "","","","","","","","");
        }
    }
    
    public record cmc_det(String[] m, String cmc_id, String cmc_code, String cmc_type, String cmc_name, 
    String cmc_phone, String cmc_fax, String cmc_email) {
        public cmc_det(String[] m) {
            this(m,"","","","","","","");
        }
    }
    
    public record cup_mstr(String[] m, String cup_cust, String cup_item, String cup_citem, String cup_citem2, 
    String cup_upc, String cup_userid, String cup_ts, String cup_misc, String cup_sku) {
        public cup_mstr(String[] m) {
            this(m,"","","","","","","","","");
        }
    }
    
    public record cpr_mstr(String[] m, String cpr_cust, String cpr_item, String cpr_type, String cpr_desc, 
    String cpr_uom, String cpr_curr, double cpr_price, double cpr_volqty, String cpr_expire) {
        public cpr_mstr(String[] m) {
            this(m,"","","","","","",0.00,0.00,"");
        }
    }
    
    public record cust_term(String[] m, String cut_code, String cut_desc, int cut_days, 
        int cut_discdays, double cut_discpercent, String cut_syscode, String cut_mfi,
        String cut_mfimonth, String cut_mfiday) {
        public cust_term(String[] m) {
            this(m,"","",0,0,0.00,"","","","");
        }
    } 
  
    public record slsp_mstr(String[] m, String slsp_id, String slsp_name, String slsp_line1, 
        String slsp_line2, String slsp_city, String slsp_state, String slsp_zip, 
        String slsp_phone, String slsp_email, String slsp_company, String slsp_active,
        double slsp_rate ) {
        public slsp_mstr(String[] m) {
            this(m,"","","","","","","","","", "",
                   "", 0.00);
        }
    } 
  
     
    public record cm_ctrl (String[] m, String cmc_autocust) {
        public cm_ctrl(String[] m) {
            this(m,"");
        }
    } 
    
     
}
