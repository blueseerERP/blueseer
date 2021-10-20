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
package com.blueseer.adm;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author terryva
 */
public class admData {

    public static String[] addSiteMstr(site_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  site_mstr where site_site = ?";
        String sqlInsert = "insert into site_mstr (site_site, site_desc, site_line1, site_line2, site_line3, "
                        + " site_city, site_state, site_country, site_zip, site_logo, site_iv_jasper, " 
                        + " site_sh_jasper, site_po_jasper, site_or_jasper, site_pos_jasper ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.site_site);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.site_site);
            psi.setString(2, x.site_desc);
            psi.setString(3, x.site_line1);
            psi.setString(4, x.site_line2);
            psi.setString(5, x.site_line3);
            psi.setString(6, x.site_city);
            psi.setString(7, x.site_state);
            psi.setString(8, x.site_country);
            psi.setString(9, x.site_zip);
            psi.setString(10, x.site_logo);
            psi.setString(11, x.site_iv_jasper);
            psi.setString(12, x.site_sh_jasper);
            psi.setString(13, x.site_po_jasper);
            psi.setString(14, x.site_or_jasper);
            psi.setString(15, x.site_pos_jasper);
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

    public static String[] updateSiteMstr(site_mstr x) {
        String[] m = new String[2];
        String sql = "update site_mstr set site_desc = ?, site_line1 = ?, site_line2 = ?, "
                + " site_line3 = ?, site_city = ?, site_state = ?, site_country = ?, "
                + " site_zip = ?, site_logo = ?, site_iv_jasper = ?, site_sh_jasper = ?, " 
                + " site_po_jasper = ?, site_or_jasper = ?, site_pos_jasper = ? " +               
                 " where site_site = ? ; ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(15, x.site_site);
            ps.setString(1, x.site_desc);
            ps.setString(2, x.site_line1);
            ps.setString(3, x.site_line2);
            ps.setString(4, x.site_line3);
            ps.setString(5, x.site_city);
            ps.setString(6, x.site_state);
            ps.setString(7, x.site_country);
            ps.setString(8, x.site_zip);
            ps.setString(9, x.site_logo);
            ps.setString(10, x.site_iv_jasper);
            ps.setString(11, x.site_sh_jasper);
            ps.setString(12, x.site_po_jasper);
            ps.setString(13, x.site_or_jasper);
            ps.setString(14, x.site_pos_jasper); 
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addUserMstr(user_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  user_mstr where user_id = ?";
        String sqlInsert = "insert into user_mstr (user_id, user_site, user_lname, "
                        + " user_fname, user_mname, user_email, user_phone, user_cell, " 
                        + " user_rmks, user_passwd ) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.user_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.user_id);
            ps.setString(2, x.user_site);
            ps.setString(3, x.user_lname);
            ps.setString(4, x.user_fname);
            ps.setString(5, x.user_mname);
            ps.setString(6, x.user_email);
            ps.setString(7, x.user_phone);
            ps.setString(8, x.user_cell);
            ps.setString(9, x.user_rmks);
            ps.setString(10, x.user_passwd); 
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

    public static String[] updateUserMstr(user_mstr x) {
        String[] m = new String[2];
        String sql = "update user_mstr set user_site = ?, user_lname = ?, user_fname = ?, "
                + " user_mname = ?, user_email = ?, user_phone = ?, user_cell = ?, "
                + " user_rmks = ?, user_passwd = ? "          
                + " where user_id = ? ; ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(10, x.user_id);
            ps.setString(1, x.user_site);
            ps.setString(2, x.user_lname);
            ps.setString(3, x.user_fname);
            ps.setString(4, x.user_mname);
            ps.setString(5, x.user_email);
            ps.setString(6, x.user_phone); 
            ps.setString(7, x.user_cell);
            ps.setString(8, x.user_rmks);
            ps.setString(9, x.user_passwd);     
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    // misc
    public static void updateDefaultCurrency(String x) {
         DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = DriverManager.getConnection(url + db, user, pass);
        Statement st = con.createStatement();
        try{
            st.executeUpdate("update ov_mstr set ov_currency = " + "'" + x + "'" + ";" );
            st.executeUpdate("update cm_mstr set cm_curr = " + "'" + x + "'" + ";" );
            st.executeUpdate("update vd_mstr set vd_curr = " + "'" + x + "'" + ";" );
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
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
    
    public record site_mstr(String[] m, String site_site, String site_desc, 
    String site_line1, String site_line2, String site_line3, String site_city,
    String site_state, String site_zip, String site_country, String site_phone,
    String site_web, String site_logo, String site_iv_jasper, String site_sh_jasper, 
    String site_sqename, String site_sqephone, String site_sqefax, String site_sqeemail,
    String site_po_jasper, String site_or_jasper, String site_pos_jasper) {
        public site_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    ""
                    );
        }
    }

    public record user_mstr(String[] m, String user_id, String user_site, String user_lname, 
                       String user_fname, String user_mname, String user_email, String user_phone, 
                       String user_cell, String user_rmks, String user_passwd) {
        public user_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", ""
                    );
        }
    }


}
