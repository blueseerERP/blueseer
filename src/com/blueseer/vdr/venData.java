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
package com.blueseer.vdr;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
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
import java.util.Arrays;
import org.json.JSONArray;

/**
 *
 * @author terryva
 */
public class venData {
   
     // add customer master customer master table only
    public static String[] addVendMstr(vd_mstr x, ArrayList<String[]> contacts) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> xlist = new ArrayList<String[]>();
            xlist.add(new String[]{"id","addVendMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(contacts);
                return jsonToStringArray(sendServerPost(xlist, jsonString, null, "dataServVDR"));
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
            int rows = _addVendMstr(x, con, ps, res, false);  
            
            if (contacts != null) {
                _deleteVDCDetAll(x.vd_addr(), con, ps, res);    // delete cmc_det

                for (String[] s : contacts) {  
                vdc_det z = new vdc_det(null, 
                    s[0],
                    x.vd_addr(),
                    s[1],
                    s[2],
                    s[3],
                    s[4],
                    s[5]
                    );
                _addVDCDet(z, con, ps, res); 
                }
            }
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
       
    public static String[] addVendMstrMass(ArrayList<String> vendlist, String delim) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addVendMstrMass"});
            list.add(new String[]{"param1",delim});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(vendlist);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
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
               for (String rec : vendlist) {
                ld = rec.split(delim, -1);
                vd_mstr x = new vd_mstr(null, 
                ld[0], ld[1], ld[2], ld[3], ld[4],
                    ld[5], ld[6], ld[7], ld[8], ld[9],
                    BlueSeerUtils.setDateFormat(new java.util.Date()), BlueSeerUtils.setDateFormat(new java.util.Date()), 
                    bsmf.MainFrame.userid, ld[10], ld[11], ld[12], ld[13], 
                    ld[14], ld[15], ld[16], ld[17], 
                    ld[18], ld[19], ld[20], 
                    ld[21], ld[22], ld[23], ld[24], ld[25], 
                    ld[26], ld[27], ld[28], ld[29], ld[30], ld[31]
                );
                _addVendMstr(x, con, ps, res, true);
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
        
    private static int _addVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from vd_mstr where vd_addr = ?";
        String sqlInsert = "insert into vd_mstr (vd_addr, vd_name, vd_line1, vd_line2, "
                        + "vd_line3, vd_city, vd_state, vd_zip, "
                        + "vd_country, vd_dateadd, vd_datemod, vd_usermod, "
                        + "vd_group, vd_market, vd_buyer, "
                        + "vd_shipvia, vd_terms, vd_misc, vd_price_code, "
                        + "vd_disc_code, vd_tax_code,  "
                        + "vd_ap_acct, vd_ap_cc, vd_bank, vd_curr, vd_remarks, vd_phone, vd_email, vd_is850export, "
                        + " vd_type, vd_site, vd_freight_type, vd_taxid, vd_taxexempt, vd_1099 ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update vd_mstr set " 
                + " vd_name = ?, vd_line1 = ?, vd_line2 = ?, "
                + "vd_line3 = ?, vd_city = ?, vd_state = ?, vd_zip = ?, "
                + "vd_country = ?, vd_dateadd = ?, vd_datemod = ?, vd_usermod = ?, "
                + "vd_group = ?, vd_market = ?, vd_buyer = ?,  "
                + "vd_shipvia = ?, vd_terms = ?, vd_freight_type = ?, vd_price_code = ?, "
                + "vd_disc_code = ?, vd_tax_code = ?, vd_misc = ?, "
                + "vd_ap_acct = ?, vd_ap_cc = ?, vd_bank = ?, vd_curr = ?, " 
                + "vd_remarks = ?, vd_phone = ?, vd_email = ?, vd_is850export = ?, vd_type = ?, vd_site = ?, "
                + "vd_taxid = ?, vd_taxexempt = ?, vd_1099 = ? "
                + " where vd_addr = ? ; ";
          ps = con.prepareStatement(sqlSelect);
          ps.setString(1, x.vd_addr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
          PreparedStatement psu = con.prepareStatement(sqlUpdate);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.vd_addr);
            ps.setString(2, x.vd_name);
            ps.setString(3, x.vd_line1);
            ps.setString(4, x.vd_line2);
            ps.setString(5, x.vd_line3);
            ps.setString(6, x.vd_city);
            ps.setString(7, x.vd_state);
            ps.setString(8, x.vd_zip);
            ps.setString(9, x.vd_country);
            ps.setString(10, x.vd_dateadd);
            ps.setString(11, x.vd_datemod);
            ps.setString(12, x.vd_usermod);
            ps.setString(13, x.vd_group);
            ps.setString(14, x.vd_market);
            ps.setString(15, x.vd_buyer);
            ps.setString(16, x.vd_shipvia);
            ps.setString(17, x.vd_terms);
            ps.setString(18, x.vd_misc);
            ps.setString(19, x.vd_price_code);
            ps.setString(20,x.vd_disc_code);
            ps.setString(21,x.vd_tax_code);
            ps.setString(22,x.vd_ap_acct);
            ps.setString(23,x.vd_ap_cc);
            ps.setString(24,x.vd_bank);
            ps.setString(25,x.vd_curr);
            ps.setString(26,x.vd_remarks);
            ps.setString(27,x.vd_phone);
            ps.setString(28,x.vd_email);
            ps.setString(29,x.vd_is850export);
            ps.setString(30,x.vd_type);
            ps.setString(31,x.vd_site);
            ps.setString(32,x.vd_freight_type);
            ps.setString(33,x.vd_taxid);
            ps.setString(34,x.vd_taxexempt);
            ps.setString(35,x.vd_1099);
            rows = ps.executeUpdate();
            } else {
                if (addupdate) {
               psu.setString(35, x.vd_addr);
                psu.setString(1, x.vd_name);
                psu.setString(2, x.vd_line1);
                psu.setString(3, x.vd_line2);
                psu.setString(4, x.vd_line3);
                psu.setString(5, x.vd_city);
                psu.setString(6, x.vd_state);
                psu.setString(7, x.vd_zip);
                psu.setString(8, x.vd_country);
                psu.setString(9, x.vd_dateadd);
                psu.setString(10, x.vd_datemod);
                psu.setString(11, x.vd_usermod);
                psu.setString(12, x.vd_group);
                psu.setString(13, x.vd_market);
                psu.setString(14, x.vd_buyer);
                psu.setString(15, x.vd_shipvia);
                psu.setString(16, x.vd_terms);
                psu.setString(17, x.vd_freight_type);
                psu.setString(18, x.vd_price_code);
                psu.setString(19,x.vd_disc_code);
                psu.setString(20,x.vd_tax_code);
                psu.setString(21,x.vd_misc);
                psu.setString(22,x.vd_ap_acct);
                psu.setString(23,x.vd_ap_cc);
                psu.setString(24,x.vd_bank);
                psu.setString(25,x.vd_curr);
                psu.setString(26,x.vd_remarks);
                psu.setString(27,x.vd_phone);
                psu.setString(28,x.vd_email);
                psu.setString(29,x.vd_is850export);
                psu.setString(30,x.vd_type);
                psu.setString(31,x.vd_site);
                ps.setString(32,x.vd_taxid);
                ps.setString(33,x.vd_taxexempt);
                ps.setString(34,x.vd_1099);
                rows = psu.executeUpdate();  
                psu.close();
              }
            }
            return rows;
    }
     
    public static String[] updateVendMstr(vd_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> xlist = new ArrayList<String[]>();
            xlist.add(new String[]{"id","updateVendMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(xlist, jsonString, null, "dataServVDR"));
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
            int rows = _updateVendMstr(x, con, ps, res); 
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
    
    private static int _updateVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update vd_mstr set " 
                + " vd_name = ?, vd_line1 = ?, vd_line2 = ?, "
                + "vd_line3 = ?, vd_city = ?, vd_state = ?, vd_zip = ?, "
                + "vd_country = ?, vd_dateadd = ?, vd_datemod = ?, vd_usermod = ?, "
                + "vd_group = ?, vd_market = ?, vd_buyer = ?,  "
                + "vd_shipvia = ?, vd_terms = ?, vd_freight_type = ?, vd_price_code = ?, "
                + "vd_disc_code = ?, vd_tax_code = ?, vd_misc = ?, "
                + "vd_ap_acct = ?, vd_ap_cc = ?, vd_bank = ?, vd_curr = ?, " 
                + "vd_remarks = ?, vd_phone = ?, vd_email = ?, vd_is850export = ?, vd_type = ?, vd_site = ?, "
                + "vd_taxid = ?, vd_taxexempt = ?, vd_1099 = ? "
                + " where vd_addr = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(35, x.vd_addr);
            ps.setString(1, x.vd_name);
            ps.setString(2, x.vd_line1);
            ps.setString(3, x.vd_line2);
            ps.setString(4, x.vd_line3);
            ps.setString(5, x.vd_city);
            ps.setString(6, x.vd_state);
            ps.setString(7, x.vd_zip);
            ps.setString(8, x.vd_country);
            ps.setString(9, x.vd_dateadd);
            ps.setString(10, x.vd_datemod);
            ps.setString(11, x.vd_usermod);
            ps.setString(12, x.vd_group);
            ps.setString(13, x.vd_market);
            ps.setString(14, x.vd_buyer);
            ps.setString(15, x.vd_shipvia);
            ps.setString(16, x.vd_terms);
            ps.setString(17, x.vd_freight_type);
            ps.setString(18, x.vd_price_code);
            ps.setString(19,x.vd_disc_code);
            ps.setString(20,x.vd_tax_code);
            ps.setString(21,x.vd_misc);
            ps.setString(22,x.vd_ap_acct);
            ps.setString(23,x.vd_ap_cc);
            ps.setString(24,x.vd_bank);
            ps.setString(25,x.vd_curr);
            ps.setString(26,x.vd_remarks);
            ps.setString(27,x.vd_phone);
            ps.setString(28,x.vd_email);
            ps.setString(29,x.vd_is850export);
            ps.setString(30,x.vd_type);
            ps.setString(31,x.vd_site);
            ps.setString(32,x.vd_taxid);
            ps.setString(33,x.vd_taxexempt);
            ps.setString(34,x.vd_1099);
            rows = ps.executeUpdate();
        return rows;
    }
    
    public static String[] deleteVendMstr(vd_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> xlist = new ArrayList<String[]>();
            xlist.add(new String[]{"id","deleteVendMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(xlist, jsonString, null, "dataServVDR"));
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
            _deleteVendMstr(x, con, ps, res);  
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
    
    private static void _deleteVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from vd_mstr where vd_addr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
        sql = "delete from vpr_mstr where vpr_vend = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
        sql = "delete from vdp_mstr where vdp_vend = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
        sql = "delete from vdc_det where vdc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
    }
        
    public static vd_mstr getVendMstr(String[] x) {
        vd_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServVDR");
                r = objectMapper.readValue(returnstring, vd_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from vd_mstr where vd_addr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vd_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vd_mstr(m, res.getString("vd_addr"), res.getString("vd_site"), res.getString("vd_name"), 
                                res.getString("vd_line1"), res.getString("vd_line2"),
                    res.getString("vd_line3"), res.getString("vd_city"), res.getString("vd_state"), 
                    res.getString("vd_zip"), res.getString("vd_country"), res.getString("vd_dateadd"), 
                    res.getString("vd_datemod"), res.getString("vd_usermod"), res.getString("vd_group"), 
                    res.getString("vd_market"), res.getString("vd_buyer"), res.getString("vd_terms"), 
                    res.getString("vd_shipvia"), res.getString("vd_price_code"), res.getString("vd_disc_code"), 
                    res.getString("vd_tax_code"), res.getString("vd_ap_acct"), res.getString("vd_ap_cc"), 
                    res.getString("vd_remarks"), res.getString("vd_freight_type"), res.getString("vd_bank"), 
                    res.getString("vd_curr"), res.getString("vd_misc"), res.getString("vd_phone"), 
                    res.getString("vd_email"), res.getString("vd_is850export"), res.getString("vd_type"),
                    res.getString("vd_taxid"), res.getString("vd_taxexempt"), res.getString("vd_1099")
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vd_mstr(m);
        }
        return r;
    }
    
    public static vd_mstr _getVendMstr(String code, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        
        vd_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from vd_mstr where vd_addr = ? ;";
          ps = con.prepareStatement(sqlSelect); 
           ps.setString(1, code);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new vd_mstr(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                      r = new vd_mstr(m, res.getString("vd_addr"), res.getString("vd_site"), res.getString("vd_name"), 
                                res.getString("vd_line1"), res.getString("vd_line2"),
                    res.getString("vd_line3"), res.getString("vd_city"), res.getString("vd_state"), 
                    res.getString("vd_zip"), res.getString("vd_country"), res.getString("vd_dateadd"), 
                    res.getString("vd_datemod"), res.getString("vd_usermod"), res.getString("vd_group"), 
                    res.getString("vd_market"), res.getString("vd_buyer"), res.getString("vd_terms"), 
                    res.getString("vd_shipvia"), res.getString("vd_price_code"), res.getString("vd_disc_code"), 
                    res.getString("vd_tax_code"), res.getString("vd_ap_acct"), res.getString("vd_ap_cc"), 
                    res.getString("vd_remarks"), res.getString("vd_freight_type"), res.getString("vd_bank"), 
                    res.getString("vd_curr"), res.getString("vd_misc"), res.getString("vd_phone"), 
                    res.getString("vd_email"), res.getString("vd_is850export"), res.getString("vd_type"),
                    res.getString("vd_taxid"), res.getString("vd_taxexempt"), res.getString("vd_1099")
                    );
                    }
            }
            return r;
    }
    
    
     // vds_det Vendor Shipto Table
    public static String[] addVDSDet(vds_det x) {
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
            _addVDSDet(x, con, ps, res, false);  // add vds_det
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
    
    private static void _addVDSDet(vds_det x, Connection con, PreparedStatement ps, ResultSet res, boolean addupdate) throws SQLException {
        if (x == null) return;
        String sqlSelect = "select * from vds_det where vds_code = ? and vds_shipto = ?";
        String sqlInsert = "insert into vds_det (vds_code, vds_shipto, vds_name, vds_line1, vds_line2, " 
                        + "vds_line3, vds_city, vds_state, vds_zip, vds_country, vds_type ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update vds_det set " 
                + " vds_name = ?, vds_line1 = ?, vds_line2 = ?, "
                + "vds_line3 = ?, vds_city = ?, vds_state = ?, vds_zip = ?, "
                + "vds_country = ?, vds_type = ? "
                + " where vds_code = ? and vds_shipto = ? ; ";
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.vds_code);
            ps.setString(2, x.vds_shipto);
            res = ps.executeQuery();
             if (! res.isBeforeFirst()) {
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, x.vds_code);
            ps.setString(2, x.vds_shipto);
            ps.setString(3, x.vds_name);
            ps.setString(4, x.vds_line1);
            ps.setString(5, x.vds_line2);
            ps.setString(6, x.vds_line3);
            ps.setString(7, x.vds_city);
            ps.setString(8, x.vds_state);
            ps.setString(9, x.vds_zip);
            ps.setString(10, x.vds_country);
            ps.setString(11, x.vds_type);
            int rows = ps.executeUpdate();
            } else {
                 if (addupdate) {
                    ps = con.prepareStatement(sqlUpdate); 
                    ps.setString(10, x.vds_code);
                    ps.setString(11, x.vds_shipto);
                    ps.setString(1, x.vds_name);
                    ps.setString(2, x.vds_line1);
                    ps.setString(3, x.vds_line2);
                    ps.setString(4, x.vds_line3);
                    ps.setString(5, x.vds_city);
                    ps.setString(6, x.vds_state);
                    ps.setString(7, x.vds_zip);
                    ps.setString(8, x.vds_country);
                    ps.setString(9, x.vds_type);
                    ps.executeUpdate();    
                 }
             }
    }
        
    public static String[] updateVDSDet(vds_det x) {
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
            _updateVDSDet(x, con, ps, res);  // add vds_det
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
    
    private static int _updateVDSDet(vds_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update vds_det set " 
                + " vds_name = ?, vds_line1 = ?, vds_line2 = ?, "
                + "vds_line3 = ?, vds_city = ?, vds_state = ?, vds_zip = ?, "
                + "vds_country = ?, vds_type = ? "
                + " where vds_code = ? and vds_shipto = ? ; ";
       ps = con.prepareStatement(sql);
        ps.setString(10, x.vds_code);
        ps.setString(11, x.vds_shipto);
            ps.setString(1, x.vds_name);
            ps.setString(2, x.vds_line1);
            ps.setString(3, x.vds_line2);
            ps.setString(4, x.vds_line3);
            ps.setString(5, x.vds_city);
            ps.setString(6, x.vds_state);
            ps.setString(7, x.vds_zip);
            ps.setString(8, x.vds_country);
            ps.setString(9, x.vds_type);
            rows = ps.executeUpdate();
        
       
        return rows;
    }
         
    public static String[] deleteVDSDet(vds_det x) {
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
            _deleteVDSDet(x.vds_code, x.vds_shipto, con, ps, res);  // add vds_det
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
    
    private static void _deleteVDSDet(String x, String y, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from vds_det where vds_code = ? and vds_shipto = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    public static vds_det getVDSDet(String shipto, String code) {
        vds_det r = null;
        String[] m = new String[2];
        String sql = "select * from vds_det where vds_shipto = ? and vds_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, shipto);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vds_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vds_det(m, res.getString("vds_code"), res.getString("vds_shipto"), res.getString("vds_name"), res.getString("vds_line1"), res.getString("vds_line2"),
                    res.getString("vds_line3"), res.getString("vds_city"), res.getString("vds_state"), res.getString("vds_zip"),
                    res.getString("vds_country"), res.getString("vds_type") 
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vds_det(m);
        }
        return r;
    }
    
    public static ArrayList<vds_det> getVDSDet(String code) {
        vds_det r = null;
        String[] m = new String[2];
        ArrayList<vds_det> list = new ArrayList<vds_det>();
        String sql = "select * from vds_det where vds_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vds_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vds_det(m, res.getString("vds_code"), res.getString("vds_shipto"), res.getString("vds_name"), res.getString("vds_line1"), res.getString("vds_line2"),
                    res.getString("vds_line3"), res.getString("vds_city"), res.getString("vds_state"), res.getString("vds_zip"),
                    res.getString("vds_country"), res.getString("vds_type") 
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vds_det(m);
               list.add(r);
        }
        return list;
    }
    
    public static ArrayList<vds_det> _getVDSDet(String code, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<vds_det> list = new ArrayList<vds_det>();
        vds_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from vds_det where vds_code = ? ;";
          ps = con.prepareStatement(sqlSelect); 
           ps.setString(1, code);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new vds_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new vds_det(m, res.getString("vds_code"), res.getString("vds_shipto"), res.getString("vds_name"), res.getString("vds_line1"), res.getString("vds_line2"),
                    res.getString("vds_line3"), res.getString("vds_city"), res.getString("vds_state"), res.getString("vds_zip"),
                    res.getString("vds_country"), res.getString("vds_type") 
                    );
                    list.add(r);
                }
            }
            return list;
    }
    
    // vdc_det Vendor Contact table
    public static String[] addVDCDet(vdc_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addVDCDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
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
            _addVDCDet(x, con, ps, res);  // add cms_det
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
    
    private static void _addVDCDet(vdc_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        if (x == null) return;
        String sqlInsert = "insert into vdc_det (vdc_code, vdc_type, vdc_name, " 
                        + "vdc_phone, vdc_fax, vdc_email ) "
                        + " values (?,?,?,?,?,?); "; 
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, x.vdc_code);
            ps.setString(2, x.vdc_type);
            ps.setString(3, x.vdc_name);
            ps.setString(4, x.vdc_phone);
            ps.setString(5, x.vdc_fax);
            ps.setString(6, x.vdc_email);
            int rows = ps.executeUpdate();
            
    }
    
    public static String[] updateVDCDet(vdc_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateVDCDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
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
            _updateVDCDet(x, con, ps, res);  // add cms_det 
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
    
    private static int _updateVDCDet(vdc_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update vdc_det set " 
                + " vdc_type = ?, vdc_name = ?, vdc_phone = ?, "
                + "vdc_fax = ?, vdc_email = ? "
                + " where vdc_code = ? and vdc_id = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(6, x.vdc_code);
        ps.setString(7, x.vdc_id);
            ps.setString(1, x.vdc_type);
            ps.setString(2, x.vdc_name);
            ps.setString(3, x.vdc_phone);
            ps.setString(4, x.vdc_fax);
            ps.setString(5, x.vdc_email);
            rows = ps.executeUpdate();
        return rows;
    }
        
    public static String[] deleteVDCDet(vdc_det x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteVDCDet"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
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
            _deleteVDCDet(x.vdc_id, x.vdc_code, con, ps, res);  // add cms_det
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
           
    private static void _deleteVDCDet(String x, String y, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
        
        String sql = "delete from vdc_det where vdc_id = ? and vdc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    private static void _deleteVDCDetAll(String x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
        
        
        String sql = "delete from vdc_det where vdc_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        
        
    }
    
    public static vdc_det getVDCDet(String id, String code) {
        vdc_det r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getVDCDet"});
            list.add(new String[]{"param1",id});
            list.add(new String[]{"param2",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServVDR");
                r = objectMapper.readValue(returnstring, vdc_det.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new vdc_det(m);
                return r;
            }
        }
        String sql = "select * from vdc_det where vdc_id = ? and vdc_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
        ps.setString(2, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vdc_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vdc_det(m, res.getString("vdc_id"), res.getString("vdc_code"), 
                        res.getString("vdc_type"), res.getString("vdc_name"),
                        res.getString("vdc_phone"), res.getString("vdc_fax"), res.getString("vdc_email")                    
                    ); 
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vdc_det(m);
               
        }
        return r;
    }
    
    public static ArrayList<vdc_det> getVDCDet(String code) {
        vdc_det r = null;
        String[] m = new String[2];
        ArrayList<vdc_det> list = new ArrayList<vdc_det>();
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getVDCDets"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServVDR");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<vdc_det>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        String sql = "select * from vdc_det where vdc_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vdc_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vdc_det(m, res.getString("vdc_id"), res.getString("vdc_code"), 
                        res.getString("vdc_type"), res.getString("vdc_name"),
                        res.getString("vdc_phone"), res.getString("vdc_fax"), res.getString("vdc_email")                    
                    );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vdc_det(m);
               list.add(r);
        }
        return list;
    }
    
    
    
    public static String[] addUpdateVDCtrl(vd_ctrl x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateVDCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  vd_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into vd_ctrl (vdc_autovend) "
                        + " values (?); "; 
        String sqlUpdate = "update vd_ctrl set vdc_autovend = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.vdc_autovend);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.vdc_autovend);
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
   
    public static vd_ctrl getVDCtrl(String[] x) {
        vd_ctrl r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getVDCtrl"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServVDR");
                r = objectMapper.readValue(returnstring, vd_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new vd_ctrl(m);
                return r;
            }
        }
        String sql = "select * from vd_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vd_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vd_ctrl(m, 
                                res.getString("vdc_autovend")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vd_ctrl(m);
        }
        return r;
    }
    
    public static String[] addVdpMstr(vdp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addVdpMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  vdp_mstr where vdp_vitem = ? and vdp_vend = ?";
        String sqlInsert = "insert into vdp_mstr (vdp_vend, vdp_item, vdp_vitem, " +
         "vdp_upc, vdp_userid, vdp_misc, vdp_sku) " 
                        + " values (?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.vdp_vitem);
             ps.setString(2, x.vdp_vend);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.vdp_vend);
            psi.setString(2, x.vdp_item);
            psi.setString(3, x.vdp_vitem);
            psi.setString(4, x.vdp_upc);
            psi.setString(5, x.vdp_userid);
            psi.setString(6, x.vdp_misc);
            psi.setString(7, x.vdp_sku);
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

    public static String[] addOrUpdateVdpMstr(vdp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addOrUpdateVdpMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  vdp_mstr where vdp_vitem = ? and vdp_vend = ?";
        String sqlInsert = "insert into vdp_mstr (vdp_vend, vdp_item, vdp_vitem, " +
         "vdp_upc, vdp_userid, vdp_misc, vdp_sku) " 
                        + " values (?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update vdp_mstr set vdp_item = ?, vdp_upc = ?, " +
                " vdp_userid = ?, vdp_misc = ?, vdp_sku = ?  " +   
                          " where vdp_vitem = ? and vdp_vend = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.vdp_vitem);
             ps.setString(2, x.vdp_vend);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.vdp_vend);
            psi.setString(2, x.vdp_item);
            psi.setString(3, x.vdp_vitem);
            psi.setString(4, x.vdp_upc);
            psi.setString(5, x.vdp_userid);
            psi.setString(6, x.vdp_misc);
            psi.setString(7, x.vdp_sku); 
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.vdp_item);
            psu.setString(2, x.vdp_upc);
            psu.setString(3, x.vdp_userid);
            psu.setString(4, x.vdp_misc);
            psu.setString(5, x.vdp_sku);
            psu.setString(6, x.vdp_vitem);
            psu.setString(7, x.vdp_vend);
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

    public static String[] updateVdpMstr(vdp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateVdpMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update vdp_mstr set vdp_item = ?, vdp_upc = ?, " +
                " vdp_userid = ?, vdp_misc = ?, vdp_sku = ?  " +   
                          " where vdp_vitem = ? and vdp_vend = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.vdp_item);
            ps.setString(2, x.vdp_upc);
            ps.setString(3, x.vdp_userid);
            ps.setString(4, x.vdp_misc);
            ps.setString(5, x.vdp_sku);
            ps.setString(6, x.vdp_vitem);
            ps.setString(7, x.vdp_vend);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteVdpMstr(vdp_mstr x) { 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteVdpMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from vdp_mstr where vdp_vitem = ? and vdp_vend = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.vdp_vitem);
        ps.setString(2, x.vdp_vend);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static vdp_mstr getVdpMstr(String[] x) {
        vdp_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getVdpMstr"});
            list.add(new String[]{"param1",x[0]});
            if (x.length > 1) {
            list.add(new String[]{"param2",x[1]});            
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServVDR");
                r = objectMapper.readValue(returnstring, vdp_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new vdp_mstr(m);
                return r;
            }
        }
        String sql = "";
         if (x.length >= 2 && ! x[1].isEmpty()) {
            sql = "select * from vdp_mstr where vdp_vend = ? and vdp_vitem = ?;";
         } else {
            sql = "select * from vdp_mstr where vdp_vitem = ? limit 1 ;";  
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
                r = new vdp_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vdp_mstr(m, res.getString("vdp_vend"), 
                            res.getString("vdp_item"),
                            res.getString("vdp_vitem"),
                            res.getString("vdp_upc"),
                            res.getString("vdp_userid"),
                            res.getString("vdp_misc"),
                            res.getString("vdp_sku")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vdp_mstr(m);
        }
        return r;
    }
    
    public static String[] addVprMstr(vpr_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addVprMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  vpr_mstr where vpr_vend = ? and vpr_item = ? and vpr_uom = ? and vpr_curr = ? ;";
        String sqlInsert = "insert into vpr_mstr (vpr_vend, vpr_item, vpr_type, vpr_desc, vpr_uom, vpr_curr, "
                        + "vpr_price)  " 
                        + " values (?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.vpr_vend);
             ps.setString(2, x.vpr_item);
             ps.setString(3, x.vpr_uom);
             ps.setString(4, x.vpr_curr);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.vpr_vend);
            psi.setString(2, x.vpr_item);
            psi.setString(3, x.vpr_type);
            psi.setString(4, x.vpr_desc);
            psi.setString(5, x.vpr_uom);
            psi.setString(6, x.vpr_curr);
            psi.setString(7, x.vpr_price);
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

    public static String[] updateVprMstr(vpr_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateVprMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update vpr_mstr set vpr_desc = ?, vpr_price = ? " +   
                " where vpr_vend = ? and vpr_item = ? and vpr_uom = ? and vpr_curr = ? and vpr_type = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, x.vpr_desc);
        ps.setString(2, x.vpr_price);
        ps.setString(3, x.vpr_vend);
        ps.setString(4, x.vpr_item);
        ps.setString(5, x.vpr_uom);
        ps.setString(6, x.vpr_curr);
        ps.setString(7, x.vpr_type);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteVprMstr(vpr_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteVprMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServVDR"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from vpr_mstr where vpr_vend = ? and vpr_item = ? and vpr_uom = ? and vpr_curr = ? and vpr_type = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.vpr_vend);
        ps.setString(2, x.vpr_item);
        ps.setString(3, x.vpr_uom);
        ps.setString(4, x.vpr_curr);
        ps.setString(5, x.vpr_type);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static vpr_mstr getVprMstr(String[] x) {
        vpr_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getVprMstr"});
            list.add(new String[]{"param1",x[0]}); // vend
            list.add(new String[]{"param2",x[1]}); // item
            list.add(new String[]{"param3",x[2]}); // uom
            list.add(new String[]{"param4",x[3]}); // curr
            list.add(new String[]{"param5",x[4]}); // type
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServVDR");
                r = objectMapper.readValue(returnstring, vpr_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new vpr_mstr(m);
                return r;
            }
        }
        String sql = "select * from vpr_mstr where vpr_vend = ? and vpr_item = ? and vpr_uom = ? and vpr_curr = ? and vpr_type = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
        ps.setString(3, x[2]);
        ps.setString(4, x[3]);
        ps.setString(5, x[4]);
        
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vpr_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                      
                        r = new vpr_mstr(m, res.getString("vpr_vend"), 
                            res.getString("vpr_item"),
                            res.getString("vpr_type"),
                            res.getString("vpr_desc"),
                            res.getString("vpr_uom"),
                            res.getString("vpr_curr"),
                            res.getString("vpr_price") 
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vpr_mstr(m);
        }
        return r;
    }
    
    public static ArrayList<vpr_mstr> getVprPriceLists(String code) {
        vpr_mstr r = null;
        String[] m = new String[2];
        ArrayList<vpr_mstr> list = new ArrayList<vpr_mstr>();
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getVprPriceLists"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServVDR");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<vpr_mstr>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        String sql = "select * from vpr_mstr where vpr_vend = ? and vpr_type = 'LIST' order by vpr_item ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vpr_mstr(m);
                } else {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    while(res.next()) {
                        r = new vpr_mstr(m, res.getString("vpr_vend"), 
                            res.getString("vpr_item"),
                            res.getString("vpr_type"),
                            res.getString("vpr_desc"),
                            res.getString("vpr_uom"),
                            res.getString("vpr_curr"),
                            res.getString("vpr_price")
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vpr_mstr(m);
               list.add(r);
        }
        return list;
    }
    
    
    public static VendShipSet getVendShipSet(String[] x ) {
        VendShipSet r = null;
        String[] m;
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendShipSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServVDR");
                r = objectMapper.readValue(returnstring, VendShipSet.class); 
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
            
            vd_mstr vd = _getVendMstr(x[0], bscon, ps, res);
            ArrayList<vds_det> vdslist = _getVDSDet(x[0], bscon, ps, res );
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new VendShipSet(m, vd, vdslist);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new VendShipSet(m);
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
     
    
    
    // misc
    public static String getVendPriceBrowseView(String[] keys) {
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
                if (! keys[1].isBlank()) {
                    if (keys[0].equals("item")) {
                    res = st.executeQuery("SELECT vpr_vend, vd_name, vpr_item, it_desc, vpr_uom, vpr_curr, vpr_price, vpr_type FROM  vpr_mstr inner join vd_mstr on vd_addr = vpr_vend inner join item_mstr on it_item = vpr_item where " +
                        " vpr_item like " + "'" + "%" + keys[1] + "%' ;") ;
                    } else {
                        res = st.executeQuery("SELECT vpr_vend, vd_name, vpr_item, it_desc, vpr_uom, vpr_curr, vpr_price, vpr_type FROM  vpr_mstr inner join vd_mstr on vd_addr = vpr_vend inner join item_mstr on it_item = vpr_item where " +
                        " vpr_vend like " + "'" + "%" + keys[1] + "%' ;") ;
                    }
                } else {
                  res = st.executeQuery("SELECT vpr_vend, vd_name, vpr_item, it_desc, vpr_uom, vpr_curr, vpr_price, vpr_type FROM  vpr_mstr inner join vd_mstr on vd_addr = vpr_vend inner join item_mstr on it_item = vpr_item  " +
                        " limit 500 ;") ;  
                }
                
                
                while (res.next()) {
                        i++;
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vpr_vend"));
                        rowArray.put(res.getString("vd_name"));
                        rowArray.put(res.getString("vpr_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("vpr_uom"));
                        rowArray.put(res.getString("vpr_curr"));
                        rowArray.put(res.getString("vpr_type"));
                        rowArray.put(res.getString("vpr_price"));
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
    
    public static String getVendXrefBrowseView(String[] keys) {
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
                res = st.executeQuery("SELECT * FROM  vdp_mstr  left outer join vd_mstr on vd_addr = vdp_vend where " +
                    " vdp_item like " + "'%" + keys[1] + "%' ;") ;
                } else {
                    res = st.executeQuery("SELECT * FROM  vdp_mstr  left outer join vd_mstr on vd_addr = vdp_vend where " +
                    " vdp_vitem like " + "'%" + keys[1] + "%' ;") ;
                }
              
                while (res.next()) {
                        i++;
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vdp_vend"));
                        rowArray.put(res.getString("vd_name"));
                        rowArray.put(res.getString("vdp_item"));
                        rowArray.put(res.getString("vdp_vitem"));
                        rowArray.put(res.getString("vdp_sku"));
                        rowArray.put(res.getString("vdp_upc"));
                        rowArray.put(res.getString("vdp_misc"));
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
    
    public static String getVenRptPickerData(String[] keys) {
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
                if (keys[0].equals("vendAddrInfoByRange")) {
                res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, " +
                    " vd_city, vd_state, vd_zip,  vd_phone, vd_email, " +
                    " vd_terms, vd_bank, vd_curr, vd_ap_acct " +
                    "from vd_mstr " +
                    " where cast(vd_addr as decimal) >= " + "'" + keys[1] + "'" +
                    " and cast(vd_addr as decimal) <= " + "'" + keys[2] + "'" +
                    "order by vd_addr ;");              
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("vd_addr"));
                            rowArray.put(res.getString("vd_name"));
                            rowArray.put(res.getString("vd_line1"));
                            rowArray.put(res.getString("vd_city"));
                            rowArray.put(res.getString("vd_state"));
                            rowArray.put(res.getString("vd_zip"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("vendPhoneEmailByRange")) {
                res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, " +
                    " vd_city, vd_state, vd_zip,  vd_phone, vd_email, " +
                    " vd_terms, vd_bank, vd_curr, vd_ap_acct " +
                    "from vd_mstr " +
                    " where cast(vd_addr as decimal) >= " + "'" + keys[1] + "'" +
                    " and cast(vd_addr as decimal) <= " + "'" + keys[2] + "'" +
                    "order by vd_addr ;");             
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("vd_addr"));
                            rowArray.put(res.getString("vd_name"));
                            rowArray.put(res.getString("vd_phone"));
                            rowArray.put(res.getString("vd_email"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("vendFinanceInfoByRange")) {
                res = st.executeQuery("SELECT vd_addr, vd_market, vd_name,  " +
                        " vd_terms, vd_bank, vd_curr, vd_ap_acct, vd_ap_cc " +
                        "from vd_mstr " +
                        " where cast(vd_addr as decimal) >= " + "'" + keys[1] + "'" +
                        " and cast(vd_addr as decimal) <= " + "'" + keys[2] + "'" +
                        "order by vd_addr ;");                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("vd_addr"));
                            rowArray.put(res.getString("vd_name"));
                            rowArray.put(res.getString("vd_terms"));
                            rowArray.put(res.getString("vd_bank"));
                            rowArray.put(res.getString("vd_curr"));
                            rowArray.put(res.getString("vd_ap_acct"));
                            rowArray.put(res.getString("vd_ap_cc"));
                            rowArray.put(res.getString("vd_market"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("vendTotalPurchasesByRange")) {
                res = st.executeQuery("SELECT vd_addr, vd_name,  " +
                        " sum(rvd_qty * rvd_netprice) as 'total' " +
                          "from vd_mstr inner join recv_mstr on rv_vend = vd_addr " +
                        " inner join recv_det on rvd_id = rv_id and rv_status = '1' " +
                        " where cast(vd_addr as decimal) >= " + "'" + keys[1] + "'" +
                        " and cast(vd_addr as decimal) <= " + "'" + keys[2] + "'" +
                        " and rv_recvdate >= " + "'" + keys[3] + "'" +
                        " and rv_recvdate <= " + "'" + keys[4] + "'" +
                        " group by vd_addr, vd_name order by vd_addr ;");                 
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("vd_addr"));
                            rowArray.put(res.getString("vd_name"));
                            rowArray.put(keys[3]);
                            rowArray.put(keys[4]);
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
    
    
    public static ArrayList<String[]> getVendMaintInit(String panelClassName, String userid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendMaintInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServVDR"));
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
            
            res = st.executeQuery("select vdc_autovend from vd_ctrl;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "autovend";
               s[1] = res.getString("vdc_autovend");
               lines.add(s);
            }
            
            res = st.executeQuery("select apc_apacct from ap_ctrl;;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "apacct";
               s[1] = res.getString("apc_apacct");
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
    
    
    public static String getVendBrowseView(String[] keys) {
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
                
                if (keys[0].equals("vd_addr")) {
                res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip " +
                        " from vd_mstr where vd_addr like " + "'" + '%' + keys[1] + '%' + "'" + ";");
                }
                if (keys[0].equals("vd_name")) {
                res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip  " +
                        " from vd_mstr where vd_name like " + "'" + '%' + keys[1] + '%' + "'" + ";");
                }
                if (keys[0].equals("vd_zip")) {
                res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip  " +
                        " from vd_mstr where vd_zip like " + "'" + '%' + keys[1] + '%' + "'" + ";");
                }
                
                
                    while (res.next()) {
                   
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vd_addr"));
                        rowArray.put(res.getString("vd_name"));
                        rowArray.put(res.getString("vd_line1"));
                        rowArray.put(res.getString("vd_city"));
                        rowArray.put(res.getString("vd_state"));
                        rowArray.put(res.getString("vd_zip"));
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
    
    public static ArrayList getVendMstrList() {
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

                res = st.executeQuery("select vd_addr from vd_mstr order by vd_addr;");
                while (res.next()) {
                    myarray.add(res.getString("vd_addr"));

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
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getVendMstrListMinusCarrier() {
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

                res = st.executeQuery("select vd_addr from vd_mstr where vd_type <> 'carrier' order by vd_addr;");
                while (res.next()) {
                    myarray.add(res.getString("vd_addr"));

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
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    
    
    public static ArrayList getVendShipList(String code, String type) {
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

                res = st.executeQuery("select vds_shipto from vds_det where vds_code = " + "'" + code + "'" +
                        " and vds_type = " + "'" + type + "'" +
                        " order by vds_shipto;");
                while (res.next()) {
                    myarray.add(res.getString("vds_shipto"));

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
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    
    public static ArrayList getVendMstrListBetween(String from, String to) {
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

                res = st.executeQuery("select vd_addr from vd_mstr "
                        + " where vd_addr >= " + "'" + from + "'"
                        + " and vd_addr <= " + "'" + to + "'"
                        + " order by vd_addr;");
                while (res.next()) {
                    myarray.add(res.getString("vd_addr"));

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

    public static ArrayList getVendTermsList() {
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

                res = st.executeQuery("select cut_code from cust_term order by cut_code;");
                while (res.next()) {
                    myarray.add(res.getString("cut_code"));

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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }
        
    public static ArrayList getVendNameList() {
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

                res = st.executeQuery("select vd_name from vd_mstr order by vd_name;");
                while (res.next()) {
                    myarray.add(res.getString("vd_name").replace("'", ""));

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

    public static String getVendTerms(String vend) {
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
            try{
                

                res = st.executeQuery("select vd_terms from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("vd_terms");                    
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
           
    public static String getVendName(String vend) {
           String r = "";
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
                res = st.executeQuery("select vd_name from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                r = res.getString("vd_name");                    
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
        return r;
        
    }       
         
    public static String getVendAPAcct(String vend) {
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
    try{
        

        res = st.executeQuery("select vd_ap_acct from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
       while (res.next()) {
        myitem = res.getString("vd_ap_acct");                    
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

    public static String getVendAPCC(String vend) {
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
    try{
        

        res = st.executeQuery("select vd_ap_cc from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
       while (res.next()) {
        myitem = res.getString("vd_ap_cc");                    
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

    public static String getVendItemFromItem(String vend, String item) {
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

        try{
            
            res = st.executeQuery("select vdp_vitem from vdp_mstr where vdp_vend = " + "'" + vend + "'" + 
                                  " AND vdp_item = " + "'" + item + "'" + ";");
           while (res.next()) {
               mystring = res.getString("vdp_vitem");

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

    public static String getVendCurrency(String vend) {
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
            try{
                

                res = st.executeQuery("select vd_curr from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("vd_curr");                    
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

    
    public static String[] getVendInfo(String vend) {
           // get vendor specific data
            // addr, acct, cc, currency, bank, terms, site
            String[] vendinfo = new String[]{"","","","","","",""};
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
                res = st.executeQuery("select * from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                   vendinfo[0] = res.getString("vd_addr");
                   vendinfo[1] = res.getString("vd_ap_acct");
                   vendinfo[2] = res.getString("vd_ap_cc");
                   vendinfo[3] = res.getString("vd_curr");
                   vendinfo[4] = res.getString("vd_bank");
                   vendinfo[5] = res.getString("vd_terms");
                   vendinfo[6] = res.getString("vd_site");         
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
        return vendinfo;
        
    } 

    
    public record vd_mstr(String[] m, String vd_addr, String vd_site, String vd_name, 
        String vd_line1, String vd_line2, String vd_line3, 
        String vd_city, String vd_state, String vd_zip,
    String vd_country, String vd_dateadd, String vd_datemod, String vd_usermod, 
    String vd_group, String vd_market, String vd_buyer,  
    String vd_terms, String vd_shipvia, String vd_price_code,
    String vd_disc_code, String vd_tax_code, String vd_ap_acct,
    String vd_ap_cc, String vd_remarks, String vd_freight_type, String vd_bank, String vd_curr, 
    String vd_misc, String vd_phone, String vd_email, String vd_is850export, String vd_type,
    String vd_taxid, String vd_taxexempt, String vd_1099) {
        public vd_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "");
        }
    }
   
     public record vd_ctrl (String[] m, String vdc_autovend) {
        public vd_ctrl(String[] m) {
            this(m,"");
        }
    } 
     
    public record vdc_det(String[] m, String vdc_id, String vdc_code, String vdc_type, String vdc_name, 
    String vdc_phone, String vdc_fax, String vdc_email) {
        public vdc_det(String[] m) {
            this(m,"","","","","","","");
        }
    } 
    
    public record vdp_mstr(String[] m, String vdp_vend, String vdp_item, String vdp_vitem,  
    String vdp_upc, String vdp_userid, String vdp_misc, String vdp_sku) {
        public vdp_mstr(String[] m) {
            this(m,"","","","","","","");
        }
    }
 
    public record vpr_mstr(String[] m, String vpr_vend, String vpr_item, String vpr_type,  
    String vpr_desc, String vpr_uom, String vpr_curr, String vpr_price) {
        public vpr_mstr(String[] m) {
            this(m,"","","","","","","");
        }
    }
  
    public record VendShipSet(String[] m, vd_mstr vd, ArrayList<vds_det> vdslist) {
        public VendShipSet(String[] m) {
            this (m, null, null);
        }
    }
    public record vds_det(String[] m, String vds_code, String vds_shipto, 
        String vds_name, String vds_line1, String vds_line2,
        String vds_line3, String vds_city, String vds_state, 
        String vds_zip, String vds_country, String vds_type) {
        public vds_det(String[] m) {
            this(m,"","","","","","","","","","","");
        }
    } 
}
