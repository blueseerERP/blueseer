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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.isSSHConnected;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListString;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToInt;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.log;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.sendFTPErrorMail;
import static com.blueseer.utl.EDData.writeFTPLogMulti;
import com.blueseer.utl.OVData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author terryva
 */
public class admData {

    public static String[] addSiteMstr(site_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addSiteMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  site_mstr where site_site = ?";
        String sqlInsert = "insert into site_mstr (site_site, site_desc, site_line1, site_line2, site_line3, "
                        + " site_city, site_state, site_country, site_zip, site_phone, site_web, site_sqename, site_sqeemail, site_logo, site_iv_jasper, " 
                        + " site_sh_jasper, site_po_jasper, site_or_jasper, site_pos_jasper ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
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
            psi.setString(10, x.site_phone);
            psi.setString(11, x.site_web);
            psi.setString(12, x.site_sqename);
            psi.setString(13, x.site_sqeemail);
            psi.setString(14, x.site_logo);
            psi.setString(15, x.site_iv_jasper);
            psi.setString(16, x.site_sh_jasper);
            psi.setString(17, x.site_po_jasper);
            psi.setString(18, x.site_or_jasper);
            psi.setString(19, x.site_pos_jasper);
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateSiteMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update site_mstr set site_desc = ?, site_line1 = ?, site_line2 = ?, "
                + " site_line3 = ?, site_city = ?, site_state = ?, site_country = ?, "
                + " site_zip = ?, site_phone = ?, site_web = ?, site_sqename = ?, site_sqeemail = ?, "
                + " site_logo = ?, site_iv_jasper = ?, site_sh_jasper = ?, " 
                + " site_po_jasper = ?, site_or_jasper = ?, site_pos_jasper = ? " +               
                 " where site_site = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(19, x.site_site);
            ps.setString(1, x.site_desc);
            ps.setString(2, x.site_line1);
            ps.setString(3, x.site_line2);
            ps.setString(4, x.site_line3);
            ps.setString(5, x.site_city);
            ps.setString(6, x.site_state);
            ps.setString(7, x.site_country);
            ps.setString(8, x.site_zip);
            ps.setString(9, x.site_phone);
            ps.setString(10, x.site_web);
            ps.setString(11, x.site_sqename);
            ps.setString(12, x.site_sqeemail);
            ps.setString(13, x.site_logo);
            ps.setString(14, x.site_iv_jasper);
            ps.setString(15, x.site_sh_jasper);
            ps.setString(16, x.site_po_jasper);
            ps.setString(17, x.site_or_jasper);
            ps.setString(18, x.site_pos_jasper); 
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static site_mstr getSiteMstr(String[] x) {
        site_mstr r = null;
        String[] m;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getSiteMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, site_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new site_mstr(m);
                return r;
            }
        }
        String sql = "select * from site_mstr where site_site = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new site_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new site_mstr(m, res.getString("site_site"), 
                            res.getString("site_desc"),
                            res.getString("site_line1"),
                            res.getString("site_line2"),
                            res.getString("site_line3"),
                            res.getString("site_city"),
                            res.getString("site_state"),
                            res.getString("site_zip"),
                            res.getString("site_country"),
                            res.getString("site_phone"),
                            res.getString("site_web"),
                            res.getString("site_logo"),
                            res.getString("site_iv_jasper"),
                            res.getString("site_sh_jasper"),
                            res.getString("site_sqename"),
                            res.getString("site_sqephone"),
                            res.getString("site_sqefax"),
                            res.getString("site_sqeemail"),
                            res.getString("site_po_jasper"),
                            res.getString("site_or_jasper"),
                            res.getString("site_pos_jasper")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new site_mstr(m);
        }
        return r;
    }
    
    public static String[] deleteSiteMstr(site_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteSiteMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from site_mstr where site_site = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.site_site);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    public static String[] addUserMstr(user_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUserMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  user_mstr where user_id = ?";
        String sqlInsert = "insert into user_mstr (user_id, user_site, user_lname, "
                        + " user_fname, user_mname, user_email, user_phone, user_cell, " 
                        + " user_rmks, user_passwd, user_allowedsites ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.user_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.user_id);
            psi.setString(2, x.user_site);
            psi.setString(3, x.user_lname);
            psi.setString(4, x.user_fname);
            psi.setString(5, x.user_mname);
            psi.setString(6, x.user_email);
            psi.setString(7, x.user_phone);
            psi.setString(8, x.user_cell);
            psi.setString(9, x.user_rmks);
            psi.setString(10, bsmf.MainFrame.PassWord("0", x.user_passwd().toCharArray())); 
            psi.setString(11, x.user_allowedsites); 
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateUserMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update user_mstr set user_site = ?, user_lname = ?, user_fname = ?, "
                + " user_mname = ?, user_email = ?, user_phone = ?, user_cell = ?, "
                + " user_rmks = ?, user_passwd = ?, user_allowedsites = ? "          
                + " where user_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(11, x.user_id);
            ps.setString(1, x.user_site);
            ps.setString(2, x.user_lname);
            ps.setString(3, x.user_fname);
            ps.setString(4, x.user_mname);
            ps.setString(5, x.user_email);
            ps.setString(6, x.user_phone); 
            ps.setString(7, x.user_cell);
            ps.setString(8, x.user_rmks);
            ps.setString(9, bsmf.MainFrame.PassWord("0", x.user_passwd().toCharArray()));   
            ps.setString(10, x.user_allowedsites); 
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static user_mstr getUserMstr(String[] x) {
        user_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getUserMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, user_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new user_mstr(m);
                return r;
            }
        }
        String sql = "select * from user_mstr where user_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new user_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new user_mstr(m, res.getString("user_id"), 
                            res.getString("user_site"),
                            res.getString("user_lname"),
                            res.getString("user_fname"),
                            res.getString("user_mname"),
                            res.getString("user_email"),
                            res.getString("user_phone"),
                            res.getString("user_cell"),
                            res.getString("user_rmks"),
                            bsmf.MainFrame.PassWord("1", res.getString("user_passwd").toCharArray()),
                            res.getString("user_allowedsites")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new user_mstr(m);
        }
        return r;
    }
        
    public static String[] deleteUserMstr(user_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteUserMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
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
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteUserMstr(x, con);  
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
    
    public static void _deleteUserMstr(user_mstr x, Connection con) throws SQLException { 
       PreparedStatement ps = null;   
        String sql = "delete from user_mstr where user_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.user_id);
        ps.executeUpdate();
        sql = "delete from perm_mstr where perm_user = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.user_id);
        ps.executeUpdate();
        ps.close();
    }
        
   
    public static ov_ctrl getOVCtrl() {
        ov_ctrl r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getOVCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, ov_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new ov_ctrl(m);
                return r;
            }
        }
        String sql = "select * from ov_ctrl ;";  // will always be only 0 or 1 records
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ov_ctrl(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                     
                        r = new ov_ctrl(m, res.getString("ov_version"), res.getString("ov_dist_dir"),
                        res.getString("ov_source_dir"), res.getInt("ov_login"), res.getInt("ov_custom"),
                        res.getString("ov_bgimage"), res.getInt("ov_rcolor"), res.getInt("ov_gcolor"), res.getInt("ov_bcolor"),
                        res.getString("ov_fileservertype"), res.getString("ov_image_directory"), res.getString("ov_temp_directory"), res.getString("ov_label_directory"),
                        res.getString("ov_jasper_directory"),res.getString("ov_edi_directory"),res.getString("ov_email_server"),res.getString("ov_email_from"),
                        res.getString("ov_smtpauthuser"), bsmf.MainFrame.PassWord("1", res.getString("ov_image_directory").toCharArray()), res.getString("ov_varchar"), res.getString("ov_notes"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ov_ctrl(m);
        }
        return r;
    }
  
    public static String[] addUpdateOVCtrl(ov_ctrl x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","addUpdateOVCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        int rows = 0;
        String[] m ;
        String sqlSelect = "SELECT * FROM  ov_ctrl"; // there should always be only 1 or 0 records in ov_mstr
        String sqlInsert = "insert into ov_ctrl (ov_version, ov_dist_dir, ov_source_dir, " +
        "ov_login, ov_custom, ov_bgimage, ov_rcolor, ov_gcolor, ov_bcolor," +
        "ov_fileservertype, ov_image_directory, ov_temp_directory, ov_label_directory," +
        "ov_jasper_directory, ov_edi_directory, ov_email_server," +
        "ov_email_from, ov_smtpauthuser, ov_smtpauthpass, ov_varchar, ov_notes) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update ov_ctrl set ov_version = ?, ov_dist_dir = ?, ov_source_dir = ?, " +
        "ov_login = ?, ov_custom = ?, ov_bgimage = ?, ov_rcolor = ?, ov_gcolor = ?, ov_bcolor = ?," +
        "ov_fileservertype = ?, ov_image_directory = ?, ov_temp_directory = ?, ov_label_directory = ?," +
        "ov_jasper_directory = ?, ov_edi_directory = ?, ov_email_server = ?," +
        "ov_email_from = ?, ov_smtpauthuser = ?, ov_smtpauthpass = ?, ov_varchar = ?, ov_notes = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.ov_version);
            psi.setString(2, x.ov_dist_dir);
            psi.setString(3, x.ov_source_dir);
            psi.setInt(4, x.ov_login);
            psi.setInt(5, x.ov_custom);
            psi.setString(6, x.ov_bgimage);
            psi.setInt(7, x.ov_rcolor);
            psi.setInt(8, x.ov_gcolor);
            psi.setInt(9, x.ov_bcolor);
            psi.setString(10, x.ov_fileservertype);
            psi.setString(11, x.ov_image_directory);
            psi.setString(12, x.ov_temp_directory);
            psi.setString(13, x.ov_label_directory);
            psi.setString(14, x.ov_jasper_directory);
            psi.setString(15, x.ov_edi_directory);
            psi.setString(16, x.ov_email_server);
            psi.setString(17, x.ov_email_from);
            psi.setString(18, x.ov_smtpauthuser);
            psi.setString(19, bsmf.MainFrame.PassWord("0", x.ov_smtpauthpass().toCharArray())); 
            psi.setString(20, x.ov_varchar);
            psi.setString(21, x.ov_notes);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.ov_version);
            psu.setString(2, x.ov_dist_dir);
            psu.setString(3, x.ov_source_dir);
            psu.setInt(4, x.ov_login);
            psu.setInt(5, x.ov_custom);
            psu.setString(6, x.ov_bgimage);
            psu.setInt(7, x.ov_rcolor);
            psu.setInt(8, x.ov_gcolor);
            psu.setInt(9, x.ov_bcolor);
            psu.setString(10, x.ov_fileservertype);
            psu.setString(11, x.ov_image_directory);
            psu.setString(12, x.ov_temp_directory);
            psu.setString(13, x.ov_label_directory);
            psu.setString(14, x.ov_jasper_directory);
            psu.setString(15, x.ov_edi_directory);
            psu.setString(16, x.ov_email_server);
            psu.setString(17, x.ov_email_from);
            psu.setString(18, x.ov_smtpauthuser);
            psu.setString(19, bsmf.MainFrame.PassWord("0", x.ov_smtpauthpass().toCharArray())); 
            psu.setString(20, x.ov_varchar); 
            psu.setString(21, x.ov_notes);
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
        
    public static String[] addUpdateOVMstr(ov_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateOVMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        int rows = 0;
        String[] m ;
        String sqlSelect = "SELECT * FROM  ov_mstr"; // there should always be only 1 or 0 records in ov_mstr
        String sqlInsert = "insert into ov_mstr (ov_site, ov_cc, ov_wh, ov_currency, ov_labelprinter) "
                        + " values (?,?,?,?,?); "; 
        String sqlUpdate = "update ov_mstr set ov_site = ?, ov_cc = ?, ov_wh = ?, "
                         + "ov_currency = ?, ov_labelprinter = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.ov_site);
            psi.setString(2, x.ov_cc);
            psi.setString(3, x.ov_wh);
            psi.setString(4, x.ov_currency);
            psi.setString(5, x.ov_labelprinter);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.ov_site);
            psu.setString(2, x.ov_cc);
            psu.setString(3, x.ov_wh);
            psu.setString(4, x.ov_currency);
            psu.setString(5, x.ov_labelprinter); 
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
    
    public static String[] addUpdateTxtMeta(txt_meta x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","addUpdateTxtMeta"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        int rows = 0;
        String[] m ;
        String sqlSelect = "SELECT * FROM  txt_meta where txt_id = ? and txt_type = ? and txt_key = ?;";
        String sqlInsert = "insert into txt_meta (txt_id, txt_type, txt_key, txt_value) " +
                           " values (?,?,?,?); "; 
        String sqlUpdate = "update txt_meta set txt_value = ? where txt_id = ? and txt_type = ? and txt_key = ?;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.txt_id);
             ps.setString(2, x.txt_type);
             ps.setString(3, x.txt_key);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.txt_id);
            psi.setString(2, x.txt_type);
            psi.setString(3, x.txt_key);
            psi.setString(4, x.txt_value);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.txt_value);
            psu.setString(2, x.txt_id);
            psu.setString(3, x.txt_type);
            psu.setString(4, x.txt_key);
            
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
    
    public static String[] deleteTxtMeta(txt_meta x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","deleteTxtMeta"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        int rows = 0;
        String[] m ;
        String sqlDelete = "DELETE FROM  txt_meta where txt_id = ? and txt_type = ? and txt_key = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
             ps.setString(1, x.txt_id);
             ps.setString(2, x.txt_type);
             ps.setString(3, x.txt_key);
             rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static txt_meta getTxtMeta(String[] x) {
        txt_meta r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getTxtMeta"});
            list.add(new String[]{"param1",x[0]});
            list.add(new String[]{"param2",x[1]});
            list.add(new String[]{"param3",x[2]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, txt_meta.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new txt_meta(m);
                return r;
            }
        }
        String sql = "Select * FROM txt_meta where txt_id = ? and txt_type = ? and txt_key = ? ; " ; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, x[0]);
            ps.setString(2, x[1]);
            ps.setString(3, x[2]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new txt_meta(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new txt_meta(m, res.getString("txt_id"), res.getString("txt_type"),
                        res.getString("txt_key"), res.getString("txt_value"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new txt_meta(m);
        }
        return r;
    }
  
    public static ArrayList<txt_meta> getTxtMetaByID(String[] x) {
        ArrayList<txt_meta> rlist = new ArrayList<>();
        txt_meta r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getTxtMetaByID"});
            paramlist.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServADM");
                rlist = objectMapper.readValue(returnstring, new TypeReference<ArrayList<txt_meta>>() {});
                return rlist;
            } catch (IOException ex) {
                bslog(ex);
                return rlist;
            }
        }
        String sql = "Select * FROM txt_meta where txt_id = ?  ; " ; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new txt_meta(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new txt_meta(m, res.getString("txt_id"), 
                            res.getString("txt_type"),
                            res.getString("txt_key"),
                            res.getString("txt_value")
                        );
                        rlist.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new txt_meta(m);
        }
        return rlist;
    }
  
    public static ArrayList<txt_meta> getTxtMetaByIDandType(String[] x) {
        ArrayList<txt_meta> rlist = new ArrayList<>();
        txt_meta r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getTxtMetaByIDandType"});
            paramlist.add(new String[]{"param1",x[0]});
            paramlist.add(new String[]{"param2",x[1]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServADM");
                rlist = objectMapper.readValue(returnstring, new TypeReference<ArrayList<txt_meta>>() {});
                return rlist;
            } catch (IOException ex) {
                bslog(ex);
                return rlist;
            }
        }
        String sql = "Select * FROM txt_meta where txt_id = ? and txt_type = ?  ; " ; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, x[0]);
            ps.setString(2, x[1]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new txt_meta(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new txt_meta(m, res.getString("txt_id"), 
                            res.getString("txt_type"),
                            res.getString("txt_key"),
                            res.getString("txt_value")
                        );
                        rlist.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new txt_meta(m);
        }
        return rlist;
    }
  
    
    public static ov_mstr getOVMstr(String[] x) {
        ov_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getOVMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, ov_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new ov_mstr(m);
                return r;
            }
        }
        String sql = "select * from ov_mstr ;";  // will always be only 0 or 1 records
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ov_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ov_mstr(m, res.getString("ov_site"), res.getString("ov_cc"),
                        res.getString("ov_wh"), res.getString("ov_currency"), res.getString("ov_labelprinter"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ov_mstr(m);
        }
        return r;
    }
  
    public static String[] deleteOVMstr(ov_mstr x) {
        String[] m;
        String sqlDelete = "delete from ov_mstr ;"; // should only be at most 1 record...not sure this function will ever be used 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
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
    
    public static String[] addFTPMstr(ftp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addFTPMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String p = bsmf.MainFrame.PassWord("0", x.ftp_passwd().trim().toCharArray());
        String[] m ;
        String sqlSelect = "SELECT * FROM  ftp_mstr where ftp_id = ?";
        String sqlInsert = "insert into ftp_mstr (ftp_id, ftp_desc, ftp_ip, ftp_login, " +
                          " ftp_passwd, ftp_commands, ftp_indir, ftp_outdir, ftp_delete, ftp_passive, " +
                          " ftp_binary, ftp_timeout, ftp_port, ftp_enabled, ftp_sftp, ftp_site, ftp_email ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.ftp_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.ftp_id);
            psi.setString(2, x.ftp_desc);
            psi.setString(3, x.ftp_ip);
            psi.setString(4, x.ftp_login);
            psi.setString(5, p);
            psi.setString(6, x.ftp_commands);
            psi.setString(7, x.ftp_indir);
            psi.setString(8, x.ftp_outdir);
            psi.setString(9, x.ftp_delete);
            psi.setString(10, x.ftp_passive);
            psi.setString(11, x.ftp_binary);
            psi.setString(12, x.ftp_timeout);
            psi.setString(13, x.ftp_port);
            psi.setString(14, x.ftp_enabled);
            psi.setString(15, x.ftp_sftp);
            psi.setString(16, x.ftp_site);
            psi.setString(17, x.ftp_email);
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

    public static String[] updateFTPMstr(ftp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateFTPMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        String p = bsmf.MainFrame.PassWord("0", x.ftp_passwd().trim().toCharArray());
        String[] m ;
        String sql = "update ftp_mstr set ftp_desc = ?, ftp_ip = ?, ftp_login = ?, " +
                          " ftp_passwd = ?, ftp_commands = ?, ftp_indir = ?, ftp_outdir = ?, " +
                          " ftp_delete = ?, ftp_passive = ?, " +
                          " ftp_binary = ?, ftp_timeout = ?, ftp_port = ?,  " +
                          " ftp_enabled = ?, ftp_sftp = ?, ftp_site = ?, ftp_email = ? " +
                          " where ftp_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(17, x.ftp_id);
            ps.setString(1, x.ftp_desc);
            ps.setString(2, x.ftp_ip);
            ps.setString(3, x.ftp_login);
            ps.setString(4, p);
            ps.setString(5, x.ftp_commands);
            ps.setString(6, x.ftp_indir);
            ps.setString(7, x.ftp_outdir);
            ps.setString(8, x.ftp_delete);
            ps.setString(9, x.ftp_passive);
            ps.setString(10, x.ftp_binary);
            ps.setString(11, x.ftp_timeout);
            ps.setString(12, x.ftp_port);
            ps.setString(13, x.ftp_enabled);
            ps.setString(14, x.ftp_sftp);
            ps.setString(15, x.ftp_site);
            ps.setString(16, x.ftp_email);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteFTPMstr(ftp_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteFTPMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from ftp_mstr where ftp_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.ftp_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static ftp_mstr getFTPMstr(String[] x) {
        ftp_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getFTPMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, ftp_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new ftp_mstr(m);
                return r;
            }
        }
        
        String sql = "select * from ftp_mstr where ftp_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ftp_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ftp_mstr(m, res.getString("ftp_id"), 
                            res.getString("ftp_desc"),
                            res.getString("ftp_ip"),
                            res.getString("ftp_port"),    
                            res.getString("ftp_login"),
                            res.getString("ftp_passwd"),
                            res.getString("ftp_commands"),
                            res.getString("ftp_indir"),
                            res.getString("ftp_outdir"),
                            res.getString("ftp_delete"),
                            res.getString("ftp_passive"),
                            res.getString("ftp_binary"),
                            res.getString("ftp_timeout"),
                            res.getString("ftp_enabled"),
                            res.getString("ftp_sftp"),
                            res.getString("ftp_site"),
                            res.getString("ftp_email")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ftp_mstr(m);
        }
        return r;
    }
    
    public static String[] addCodeMstr(code_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addCodeMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  code_mstr where code_code = ? and code_key = ?";
        String sqlInsert = "insert into code_mstr (code_code, code_key, code_value, code_internal) " 
                        + " values (?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.code_code);
             ps.setString(2, x.code_key);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.code_code);
            psi.setString(2, x.code_key);
            psi.setString(3, x.code_value);
            psi.setString(4, x.code_internal);
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

    public static String[] addOrUpdateCodeMstr(code_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addOrUpdateCodeMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  code_mstr where code_code = ? and code_key = ?";
        String sqlInsert = "insert into code_mstr (code_code, code_key, code_value, code_internal) " 
                        + " values (?,?,?,?); "; 
        String sqlUpdate = "update code_mstr set code_value = ?, code_internal = ? " +   
                          " where code_code = ? and code_key = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.code_code);
             ps.setString(2, x.code_key);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.code_code);
            psi.setString(2, x.code_key);
            psi.setString(3, x.code_value);
            psi.setString(4, x.code_internal);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(3, x.code_code);
            psu.setString(4, x.code_key);
            psu.setString(1, x.code_value);
            psu.setString(2, x.code_internal);
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
    
    public static String[] updateCodeMstr(code_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateCodeMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update code_mstr set code_value = ?, code_internal = ? " +   
                          " where code_code = ? and code_key = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.code_value);
        ps.setString(2, x.code_internal);
        ps.setString(3, x.code_code);
        ps.setString(4, x.code_key);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteCodeMstr(code_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCodeMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from code_mstr where code_code = ? and code_key = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.code_code);
        ps.setString(2, x.code_key);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static code_mstr getCodeMstr(String[] x) {
        code_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getCodeMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, code_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new code_mstr(m);
                return r;
            }
        }
        String sql = "";
         if (x.length >= 2 && ! x[1].isEmpty()) {
            sql = "select * from code_mstr where code_code = ? and code_key = ? ;"; 
         } else {
            sql = "select * from code_mstr where code_code = ? limit 1 ;";  
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
                r = new code_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new code_mstr(m, 
                            res.getString("code_code"), 
                            res.getString("code_key"),
                            res.getString("code_value"),
                            res.getString("code_internal")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new code_mstr(m);
        }
        return r;
    }
    
    public static String[] addJaspMstr(jasp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addJaspMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  jasp_mstr where jasp_group = ? and jasp_sequence = ?";
        String sqlInsert = "insert into jasp_mstr (jasp_group, jasp_desc, jasp_func, jasp_sequence, jasp_format) " 
                        + " values (?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.jasp_group);
             ps.setString(2, x.jasp_sequence);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.jasp_group);
            psi.setString(2, x.jasp_desc);
            psi.setString(3, x.jasp_func);
            psi.setString(4, x.jasp_sequence);
            psi.setString(5, x.jasp_format);
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

    public static String[] updateJaspMstr(jasp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateJaspMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update jasp_mstr set jasp_desc = ?, jasp_func = ?, jasp_format = ? " +   
                          " where jasp_group = ? and jasp_sequence = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.jasp_desc);
        ps.setString(2, x.jasp_func);
        ps.setString(3, x.jasp_format);
        ps.setString(4, x.jasp_group);
        ps.setString(5, x.jasp_sequence);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteJaspMstr(jasp_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteJaspMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from jasp_mstr where jasp_group = ? and jasp_sequence = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.jasp_group);
        ps.setString(2, x.jasp_sequence);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static jasp_mstr getJaspMstr(String[] x) {
        jasp_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getJaspMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, jasp_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new jasp_mstr(m);
                return r;
            }
        }
        String sql = "";
         if (x.length >= 2 && ! x[1].isEmpty()) {
            sql = "select * from jasp_mstr where jasp_group = ? and jasp_sequence = ? ;"; 
         } else {
            sql = "select * from jasp_mstr where jasp_group = ? limit 1 ;";  
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
                r = new jasp_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new jasp_mstr(m, 
                            res.getString("jasp_group"), 
                            res.getString("jasp_desc"),
                            res.getString("jasp_func"),
                            res.getString("jasp_sequence"),
                            res.getString("jasp_format")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new jasp_mstr(m);
        }
        return r;
    }
    
    public static String[] addCounter(counter x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addCounter"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  counter where counter_name = ?";
        String sqlInsert = "insert into counter (counter_name, counter_desc, counter_prefix, "
                        + " counter_from, counter_to, counter_id ) "
                        + " values (?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.counter_name);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.counter_name);
            psi.setString(2, x.counter_desc);
            psi.setString(3, x.counter_prefix);
            psi.setString(4, x.counter_from);
            psi.setString(5, x.counter_to);
            psi.setString(6, x.counter_id);
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

    public static String[] updateCounter(counter x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateCounter"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update counter set counter_desc = ?, counter_prefix = ?, counter_from = ?, " +   
                          " counter_to = ?, counter_id = ? where counter_name = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.counter_desc);
        ps.setString(2, x.counter_prefix);
        ps.setString(3, x.counter_from);
        ps.setString(4, x.counter_to);
        ps.setString(5, x.counter_id);
        ps.setString(6, x.counter_name);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteCounter(counter x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCounter"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from counter where counter_name = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.counter_name);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static counter getCounter(String[] x) {
        counter r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getCounter"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, counter.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new counter(m);
                return r;
            }
        }
        String sql = "select * from counter where counter_name = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new counter(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new counter(m, res.getString("counter_name"), 
                            res.getString("counter_desc"),
                            res.getString("counter_prefix"),
                            res.getString("counter_from"),    
                            res.getString("counter_to"),
                            res.getString("counter_id")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new counter(m);
        }
        return r;
    }
    
    public static String[] addMenuMstr(menu_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addMenuMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  menu_mstr where menu_id = ?";
        String sqlInsert = "insert into menu_mstr (menu_id, menu_desc, menu_type, "
                        + " menu_panel, menu_navcode ) "
                        + " values (?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.menu_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.menu_id);
            psi.setString(2, x.menu_desc);
            psi.setString(3, x.menu_type);
            psi.setString(4, x.menu_panel);
            psi.setString(5, x.menu_navcode);
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

    public static String[] updateMenuMstr(menu_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateMenuMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update menu_mstr set menu_desc = ?, menu_type = ?, menu_panel = ?, " +   
                          " menu_navcode = ? where menu_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.menu_desc);
        ps.setString(2, x.menu_type);
        ps.setString(3, x.menu_panel);
        ps.setString(4, x.menu_navcode);
        ps.setString(5, x.menu_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static menu_mstr getMenuMstr(String[] x) {
        menu_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getMenuMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, menu_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new menu_mstr(m);
                return r;
            }
        }
        String sql = "select * from menu_mstr where menu_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new menu_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new menu_mstr(m, res.getString("menu_id"), 
                            res.getString("menu_desc"),
                            res.getString("menu_type"),
                            res.getString("menu_panel"),    
                            res.getString("menu_navcode")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new menu_mstr(m);
        }
        return r;
    }
    
    public static String[] deleteMenuMstr(menu_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteMenuMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from menu_mstr where menu_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.menu_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addMenuTree(menu_tree x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addMenuTree"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  menu_tree where mt_par = ? and mt_child = ?";
        String sqlInsert = "insert into menu_tree (mt_par, mt_child, mt_index, mt_type, " +
"        mt_label, mt_icon, mt_initvar, mt_func, mt_visible, mt_enable) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.mt_par);
             ps.setString(2, x.mt_child);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.mt_par);
            psi.setString(2, x.mt_child);
            psi.setString(3, x.mt_index);
            psi.setString(4, x.mt_type);
            psi.setString(5, x.mt_label);
            psi.setString(6, x.mt_icon);
            psi.setString(7, x.mt_initvar);
            psi.setString(8, x.mt_func);
            psi.setInt(9, x.mt_visible);
            psi.setInt(10, x.mt_enable);
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

    public static String[] updateMenuTree(menu_tree x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateMenuTree"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update menu_tree set mt_index = ?, mt_type = ?, " +
"        mt_label = ?, mt_icon = ?, mt_initvar = ?, mt_func = ?, mt_visible = ?, mt_enable = ? " +   
                          " where mt_par = ? and mt_child = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.mt_index);
        ps.setString(2, x.mt_type);
        ps.setString(3, x.mt_label);
        ps.setString(4, x.mt_icon);
        ps.setString(5, x.mt_initvar);
        ps.setString(6, x.mt_func);
        ps.setInt(7, x.mt_visible);
        ps.setInt(8, x.mt_enable);
        ps.setString(9, x.mt_par);
        ps.setString(10, x.mt_child);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static menu_tree getMenuTree(String[] x) {
        menu_tree r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getMenuTree"});
            list.add(new String[]{"param1",x[0]});
            list.add(new String[]{"param2",x[1]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, menu_tree.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new menu_tree(m);
                return r;
            }
        }
        String sql = "select * from menu_tree where mt_par = ? and mt_child = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new menu_tree(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new menu_tree(m, res.getString("mt_par"), 
                            res.getString("mt_child"),
                            res.getString("mt_index"),    
                            res.getString("mt_type"),
                            res.getString("mt_label"),
                            res.getString("mt_icon"),
                            res.getString("mt_initvar"),
                            res.getString("mt_func"),
                            res.getInt("mt_visible"),
                            res.getInt("mt_enable")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new menu_tree(m);
        }
        return r;
    }
    
    public static String[] deleteMenuTree(menu_tree x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteMenuTree"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from menu_tree where mt_par = ? and mt_child = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.mt_par);
        ps.setString(2, x.mt_child);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addPanelMstr(panel_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addPanelMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  panel_mstr where panel_id = ?";
        String sqlInsert = "insert into panel_mstr (panel_id, panel_desc, panel_core ) "
                        + " values (?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.panel_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.panel_id);
            psi.setString(2, x.panel_desc);
            psi.setString(3, x.panel_core);
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

    public static String[] updatePanelMstr(panel_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updatePanelMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update panel_mstr set panel_desc = ?, panel_core = ? " +   
                          " where panel_id = ? ; ";
       try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.panel_desc);
        ps.setString(2, x.panel_core);
        ps.setString(3, x.panel_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static panel_mstr getPanelMstr(String[] x) {
        panel_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getPanelMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, panel_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new panel_mstr(m);
                return r;
            }
        }
        String sql = "select * from panel_mstr where panel_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new panel_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new panel_mstr(m, res.getString("panel_id"), 
                            res.getString("panel_desc"),
                            res.getString("panel_core")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new panel_mstr(m);
        }
        return r;
    }
    
    public static String[] deletePanelMstr(panel_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deletePanelMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from panel_mstr where panel_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.panel_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addPrtMstr(prt_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addPrtMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  prt_mstr where prt_id = ?";
        String sqlInsert = "insert into prt_mstr (prt_id, prt_desc, prt_type, "
                        + " prt_ip, prt_port ) "
                        + " values (?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.prt_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.prt_id);
            psi.setString(2, x.prt_desc);
            psi.setString(3, x.prt_type);
            psi.setString(4, x.prt_ip);
            psi.setString(5, x.prt_port);
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

    public static String[] updatePrtMstr(prt_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updatePrtMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update prt_mstr set prt_desc = ?, prt_type = ?, prt_ip = ?, " +   
                          " prt_ip = ? where prt_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.prt_desc);
        ps.setString(2, x.prt_type);
        ps.setString(3, x.prt_ip);
        ps.setString(4, x.prt_ip);
        ps.setString(5, x.prt_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
     
    public static String[] deletePrtMstr(prt_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deletePrtMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from prt_mstr where prt_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.prt_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static prt_mstr getPrtMstr(String[] x) {
        prt_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getPrtMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, prt_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new prt_mstr(m);
                return r;
            }
        }
        String sql = "select * from prt_mstr where prt_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new prt_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new prt_mstr(m, res.getString("prt_id"), 
                            res.getString("prt_desc"),
                            res.getString("prt_type"),
                            res.getString("prt_ip"),    
                            res.getString("prt_port")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new prt_mstr(m);
        }
        return r;
    }
    
    public static String[] deleteMenuMstr(prt_mstr x) { 
       String[] m ;
        String sql = "delete from prt_mstr where prt_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.prt_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    public static String[] addPksMstr(pks_mstr x, String returned_keyid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addPksMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + returned_keyid;
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  pks_mstr where pks_id = ? ";
        String sqlInsert = "insert into pks_mstr (pks_id, pks_desc, pks_type, "
                        + " pks_user, pks_pass, pks_file, pks_storeuser, pks_storepass, " 
                        + " pks_expire, pks_create, pks_parent, pks_standard, pks_external, pks_keyid ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.pks_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.pks_id);
            psi.setString(2, x.pks_desc);
            psi.setString(3, x.pks_type);
            psi.setString(4, x.pks_user);
            psi.setString(5, x.pks_pass);
            psi.setString(6, x.pks_file);
            psi.setString(7, x.pks_storeuser);
            psi.setString(8, x.pks_storepass);
            psi.setString(9, x.pks_expire);
            psi.setString(10, x.pks_create);
            psi.setString(11, x.pks_parent);
            psi.setString(12, x.pks_standard);
            psi.setString(13, x.pks_external);
            psi.setString(14, returned_keyid);
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

    public static String[] updatePksMstr(pks_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updatePksMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update pks_mstr set pks_desc = ?, pks_type = ?, pks_user = ?,  " +   
                          " pks_pass = ? , pks_file = ?, pks_storeuser = ?, " +
                          " pks_storepass = ?, pks_expire = ?, pks_create = ?, pks_parent = ?, " +
                          " pks_standard = ?, pks_external = ? " +
                          " where pks_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.pks_desc);
        ps.setString(2, x.pks_type);
        ps.setString(3, x.pks_user);
        ps.setString(4, x.pks_pass);
        ps.setString(5, x.pks_file);
        ps.setString(6, x.pks_storeuser);
        ps.setString(7, x.pks_storepass);
        ps.setString(8, x.pks_expire);
        ps.setString(9, x.pks_create);
        ps.setString(10, x.pks_parent);
        ps.setString(11, x.pks_standard);
        ps.setString(12, x.pks_external);
       // ps.setString(13, x.pks_keyid);  // do not update keyid...created on fly with 'add'...and cannot be updated
        ps.setString(13, x.pks_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deletePksMstr(pks_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deletePksMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from pks_mstr where pks_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.pks_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static pks_mstr getPksMstr(String[] x) {
        pks_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getPksMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, pks_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new pks_mstr(m);
                return r;
            }
        }
        String sql = "select * from pks_mstr where pks_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new pks_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new pks_mstr(m, res.getString("pks_id"), 
                            res.getString("pks_desc"),
                            res.getString("pks_type"),
                            res.getString("pks_user"),    
                            res.getString("pks_pass"),
                            res.getString("pks_file"),
                            res.getString("pks_storeuser"),
                            res.getString("pks_storepass"),
                            res.getString("pks_expire"),
                            res.getString("pks_create"),
                            res.getString("pks_parent"),
                            res.getString("pks_standard"),
                            res.getString("pks_external"),
                            res.getString("pks_keyid")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new pks_mstr(m);
        }
        return r;
    }
    
    
    public static String[] addCronMstr(cron_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addCronMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sqlSelect = "SELECT * FROM  cron_mstr where cron_jobid = ? ";
        String sqlInsert = "insert into cron_mstr (cron_jobid, cron_desc, cron_group, " 
                        + " cron_prog, cron_param, cron_priority, cron_expression, cron_enabled, "
                        + " cron_modflag, cron_lastrun, cron_lastmod, cron_userid ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cron_jobid);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cron_jobid);
            psi.setString(2, x.cron_desc);
            psi.setString(3, x.cron_group);
            psi.setString(4, x.cron_prog);
            psi.setString(5, x.cron_param);
            psi.setString(6, x.cron_priority);
            psi.setString(7, x.cron_expression);
            psi.setString(8, x.cron_enabled);
            psi.setString(9, x.cron_modflag);
            psi.setString(10, x.cron_lastrun);
            psi.setString(11, x.cron_lastmod);
            psi.setString(12, x.cron_userid);
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

    public static String[] updateCronMstr(cron_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateCronMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "update cron_mstr set cron_desc = ?, cron_group = ?, " 
                        + " cron_prog = ?, cron_param = ?, cron_priority = ?, cron_expression = ?, cron_enabled = ?, "
                        + " cron_modflag = ?, cron_lastrun = ?, cron_lastmod = ?, cron_userid = ? "
                        + " where cron_jobid = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.cron_desc);
            ps.setString(2, x.cron_group);
            ps.setString(3, x.cron_prog);
            ps.setString(4, x.cron_param);
            ps.setString(5, x.cron_priority);
            ps.setString(6, x.cron_expression);
            ps.setString(7, x.cron_enabled);
            ps.setString(8, x.cron_modflag);
            ps.setString(9, x.cron_lastrun);
            ps.setString(10, x.cron_lastmod);
            ps.setString(11, x.cron_userid);
            ps.setString(12, x.cron_jobid);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteCronMstr(cron_mstr x) { 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteCronMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m ;
        String sql = "delete from cron_mstr where cron_jobid = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cron_jobid);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static cron_mstr getCronMstr(String[] x) {
        cron_mstr r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getCronMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServADM");
                r = objectMapper.readValue(returnstring, cron_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new cron_mstr(m);
                return r;
            }
        }
        String sql = "select * from cron_mstr where cron_jobid = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cron_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cron_mstr(m, res.getString("cron_jobid"), 
                            res.getString("cron_desc"),
                            res.getString("cron_group"),
                            res.getString("cron_prog"),    
                            res.getString("cron_param"),
                            res.getString("cron_priority"),
                            res.getString("cron_expression"),
                            res.getString("cron_enabled"),
                            res.getString("cron_modflag"),
                            res.getString("cron_lastrun"),
                            res.getString("cron_lastmod"),
                            res.getString("cron_userid")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cron_mstr(m);
        }
        return r;
    }
    
    public static String[] addChangeLog(ArrayList<change_log> chg) {
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addChangeLog"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(chg);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
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
            bscon.setAutoCommit(false); 
            if (chg != null) {
                for (change_log z : chg) {
                    _addChangeLog(z, bscon, ps, res);
                }
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
    
    public static int _addChangeLog(change_log x, Connection con, PreparedStatement psi, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlInsert = "insert into change_log (chg_key, chg_table, chg_class, " 
                        + " chg_userid, chg_desc, chg_type, chg_ref ) "
                        + " values (?,?,?,?,?,?,?); ";
            psi = con.prepareStatement(sqlInsert); 
            psi.setString(1, x.chg_key);
            psi.setString(2, x.chg_table);
            psi.setString(3, x.chg_class);
            psi.setString(4, x.chg_userid);
            psi.setString(5, x.chg_desc);
            psi.setString(6, x.chg_type);
            psi.setString(7, x.chg_ref);
            rows = psi.executeUpdate();
        return rows;
    }

    public static ArrayList<change_log> getChangeLog(String[] x) {
         ArrayList<change_log> list = new ArrayList<change_log>();
        change_log r = null;
        String[] m ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getChangeLog"});
            paramlist.add(new String[]{"param1",x[0]});
            paramlist.add(new String[]{"param2",x[1]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServADM");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<change_log>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        String sql = "select * from change_log where chg_key = ? and chg_class = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new change_log(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new change_log(m, res.getString("chg_id"), 
                            res.getString("chg_key"),
                            res.getString("chg_table"),
                            res.getString("chg_class"),    
                            res.getString("chg_userid"),
                            res.getString("chg_desc"),
                            res.getString("chg_ts"),
                            res.getString("chg_type"),
                            res.getString("chg_ref")
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new change_log(m);
        }
        return list;
    }
    
    public static ArrayList<change_log> getChangeLogByTable(String[] x) {
         ArrayList<change_log> list = new ArrayList<change_log>();
        change_log r = null;
        String[] m ;
        String sql = "select * from change_log where chg_key = ? and chg_table = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new change_log(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new change_log(m, res.getString("chg_id"), 
                            res.getString("chg_key"),
                            res.getString("chg_table"),
                            res.getString("chg_class"),    
                            res.getString("chg_userid"),
                            res.getString("chg_desc"),
                            res.getString("chg_ts"),
                            res.getString("chg_type"),
                            res.getString("chg_ref")
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new change_log(m);
        }
        return list;
    }
    
    public static String[] addFTPAttr(ArrayList<ftp_attr> ftpa) {
        String[] m = new String[2] ;
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
            if (ftpa != null) {
                for (ftp_attr z : ftpa) {
                    _addFTPAttr(z, bscon, ps, res); 
                }
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
    
    public static int _addFTPAttr(ftp_attr x, Connection con, PreparedStatement psi, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlInsert = "insert into ftp_attr (ftpa_id, ftpa_key, ftpa_value ) " 
                        + " values (?,?,?); ";
            psi = con.prepareStatement(sqlInsert); 
            psi.setString(1, x.ftpa_id);
            psi.setString(2, x.ftpa_key);
            psi.setString(3, x.ftpa_value);
            rows = psi.executeUpdate();
        return rows;
    }

    public static String[] addUpdateFTPAttr(String x, ArrayList<String[]> y) {
        String[] m = new String[2] ;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateFTPAttr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = x;
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(y);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String sqlDelete = "delete from ftp_attr where ftpa_id = ?";
        String sqlInsert = "insert into ftp_attr (ftpa_id, ftpa_key, ftpa_value)  " +
                " values (?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
             ps.setString(1, x);
             ps.executeUpdate();
             PreparedStatement psi = con.prepareStatement(sqlInsert); 
             for (String[] s : y) {
                 psi.setString(1, x);
                 psi.setString(2, s[1]);
                 psi.setString(3, s[2]);
                 psi.executeUpdate();
             }
             psi.close();
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteFTPAttrMstr(String x) { 
       String[] m ;
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteFTPAttrMstr"});
            list.add(new String[]{"param1",x});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String sql = "delete from ftp_attr where ftpa_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    // misc
    public static ArrayList<String[]> getLoginInit(String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLoginInit"});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServFIN"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
        
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
                    
           
             res = st.executeQuery("select ov_rcolor, ov_gcolor, ov_bcolor from ov_ctrl;" );
               while (res.next()) {
               String[] s = new String[2];
               s[0] = "bgcolor";
               s[1] = res.getString("ov_rcolor") + ":" + res.getString("ov_gcolor") + ":" + res.getString("ov_bcolor");
               lines.add(s);
            }
            
            
            res = st.executeQuery("select case when p.perm_menu is not null then '1' else '0' end as 'hasaccess', perm_user, mt_par, mt_child, mt_type, mt_label, mt_icon, mt_initvar, mt_func, mt_visible, mt_enable, menu_navcode from menu_tree " +
                        " inner join menu_mstr on menu_id = mt_child " +
                        " left outer join perm_mstr p on perm_menu = mt_child and perm_user = " + "'" + userid + "'" +
                        " where mt_visible = '1' " + 
                        " order by mt_par, mt_index ;");
               while (res.next()) {
                    String[] s = new String[2];
                    s[0] = "menusforuser";
                    s[1] = res.getString("perm_user") + "," + 
                            res.getString("mt_par") + "," +
                            res.getString("mt_child") + "," +
                            res.getString("mt_type") + "," +
                            res.getString("mt_label") + "," +
                            res.getString("mt_icon") + "," +
                            res.getString("mt_initvar") + "," +
                            res.getString("mt_func") + "," +
                            res.getString("mt_visible") + "," +
                            res.getString("mt_enable") + "," +
                            res.getString("menu_navcode") + "," +
                            res.getString("hasaccess")
                            ;
                    lines.add(s);
                }
            
            res = st.executeQuery("SELECT perm_user, perm_menu FROM  perm_mstr where perm_user = " + "'" + userid + "'"  + ";");
               while (res.next()) {
                    String[] s = new String[2];
                    s[0] = "perms";
                    s[1] = res.getString("perm_menu");
                    lines.add(s);
                }
               
            res = st.executeQuery("select ov_version from ov_ctrl;" );
               while (res.next()) {
                    String[] s = new String[2];
                    s[0] = "version";
                    s[1] = res.getString("ov_version");
                    lines.add(s);
                } 
            
            res = st.executeQuery("select menu_id, menu_panel from menu_mstr order by menu_id ;");
                while (res.next()) {
                String[] s = new String[2];
                    s[0] = "menus";
                    s[1] = res.getString("menu_id") + "," + res.getString("menu_panel");
                    lines.add(s);
                }  
                
            res = st.executeQuery("select menu_navcode, menu_id, menu_panel, mt_initvar  from menu_mstr " +
                        " inner join menu_tree on mt_child = menu_id " +  ";" );
                while (res.next()) {
                String[] s = new String[2];
                    s[0] = "navcodes";
                    s[1] = res.getString("menu_navcode") + "," + res.getString("menu_id") + "," + res.getString("menu_panel") + "," + res.getString("mt_initvar");
                    lines.add(s);
                }      
            
               
            res = st.executeQuery("SELECT ov_currency FROM ov_mstr" + ";");
                while (res.next()) {
                String[] s = new String[2];
                s[0] = "iscurrencyset";     
                     if (res.getString("ov_currency").isBlank()) {
                         s[1] = "0";
                     } else {
                         s[1] = "1";
                     }
                lines.add(s);     
                }
            
            res = st.executeQuery("SELECT ov_login FROM ov_ctrl" + ";");
                while (res.next()) {
                String[] s = new String[2];
                s[0] = "logincontrol";
                s[1] = res.getString("ov_login");
                lines.add(s);     
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
        return lines;
    }
    
    public static ArrayList<String[]> getSiteInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSiteInit"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
        String[] sites = null;
        boolean allsites = false;
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
                    
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
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
               
             
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
            }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
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
    
    public static ArrayList<String[]> getInitMinimum(String panelClassName, String userid, String datasets) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getInitMinimum"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            list.add(new String[]{"param3", datasets});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        String[] datasetsarray = null;
        if (datasets != null && ! datasets.isBlank()) {
          datasetsarray = datasets.split(",");
        }
        String[] sites = null;
        boolean allsites = false;
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
                    
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
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
               
             
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select ov_site, ov_currency, ov_cc, ov_wh from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
               s = new String[2];
               s[0] = "cc";
               s[1] = res.getString("ov_cc");
               lines.add(s);
               s = new String[2];
               s[0] = "wh";
               s[1] = res.getString("ov_wh");
               lines.add(s);
            }
            
            res = st.executeQuery("select gl_autopost from gl_ctrl;");
                while (res.next()) {
                   String[] s = new String[2];
                   s[0] = "autopost";
                   s[1] = res.getString("gl_autopost");
                   lines.add(s);
                }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
            }
            
            if (datasetsarray != null) {
                for (String sd : datasetsarray) {
                        if (sd.equals("depts")) {
                            res = st.executeQuery("select dept_id from dept_mstr ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "depts";
                               s[1] = res.getString("dept_id");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("vendors")) {
                            res = st.executeQuery("select vd_addr from vd_mstr order by vd_addr ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "vendors";
                               s[1] = res.getString("vd_addr");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("customers")) {
                            res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "customers";
                               s[1] = res.getString("cm_code");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("labeltype")) {
                            res = st.executeQuery("select code_key from code_mstr where code_code = 'labeltype' order by code_key ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "labeltype";
                               s[1] = res.getString("code_key");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("pricegroups")) {
                            res = st.executeQuery("select code_key from code_mstr where code_code = " + "'PRICEGROUP'" + " order by code_key ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "pricegroups";
                               s[1] = res.getString("code_key");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("icprofiles")) {
                            res = st.executeQuery("select distinct glic_profile from glic_def ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "icprofiles";
                               s[1] = res.getString("glic_profile");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("accounts")) {
                            res = st.executeQuery("select ac_id from ac_mstr order by ac_id ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "accounts";
                               s[1] = res.getString("ac_id");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("workcenters")) {
                            res = st.executeQuery("select wc_cell from wc_mstr;" );
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "workcenters";
                               s[1] = res.getString("wc_cell");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("demdtoplan")) {
                            res = st.executeQuery("select demdtoplan from inv_ctrl;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "demdtoplan";
                               s[1] = res.getString("demdtoplan");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("ap_ctrl")) {
                            res = st.executeQuery("select * from ap_ctrl;");
                            while (res.next()) {
                               lines.add(new String[]{"apc_autovoucher", res.getString("apc_autovoucher")});
                               lines.add(new String[]{"apc_bank", res.getString("apc_bank")});
                               lines.add(new String[]{"apc_apacct", res.getString("apc_apacct")});
                            }
                        }
                        
                        if (sd.equals("ar_ctrl")) {
                            res = st.executeQuery("select * from ar_ctrl;" );
                            while (res.next()) {
                               lines.add(new String[]{"arc_bank", res.getString("arc_bank")});
                            }
                        }
                        
                        if (sd.equals("def_bank_acct")) {
                            res = st.executeQuery("select bk_acct from bk_mstr inner join ar_ctrl on bk_id = arc_bank ;" );
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "def_bank_acct";
                               s[1] = res.getString("bk_acct");
                               lines.add(s);
                            }
                        }

                        
                        if (sd.equals("payc_payrolltax_acct")) {
                            res = st.executeQuery("select payc_payrolltax_acct from pay_ctrl;" );
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "payc_payrolltax_acct";
                               s[1] = res.getString("payc_payrolltax_acct");
                               lines.add(s);
                            }
                        }
                        
                        
                        if (sd.equals("serialize")) {
                            res = st.executeQuery("select serialize from inv_ctrl;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "serialize";
                               s[1] = res.getString("serialize");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("shifts")) {
                            res = st.executeQuery("select shf_id from shift_mstr order by shf_id ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "shifts";
                               s[1] = res.getString("shf_id");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("employees")) {
                            res = st.executeQuery("select emp_nbr from emp_mstr order by emp_nbr ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "employees";
                               s[1] = res.getString("emp_nbr");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("reps")) {
                            res = st.executeQuery("select slsp_name from slsp_mstr order by slsp_name ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "reps";
                               s[1] = res.getString("slsp_name");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("cells")) {
                            res = st.executeQuery("select code_key, code_value from code_mstr where code_code = 'CELL' order by code_key ;");
                            while (res.next()) {
                               String[] s = new String[3];
                               s[0] = "cells";
                               s[1] = res.getString("code_key");
                               s[2] = res.getString("code_value");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("states")) {
                            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "states";
                               s[1] = res.getString("code_key");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("countries")) {
                            res = st.executeQuery("select code_key from code_mstr where code_code = 'country' order by code_key ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "countries";
                               s[1] = res.getString("code_key");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("currencies")) {
                            res = st.executeQuery("select cur_id from cur_mstr ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "currencies";
                               s[1] = res.getString("cur_id");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("printers")) {
                            res = st.executeQuery("select prt_id from prt_mstr order by prt_id;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "printers";
                               s[1] = res.getString("prt_id");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("terms")) {
                            res = st.executeQuery("select cut_code from cust_term ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "terms";
                               s[1] = res.getString("cut_code");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("banks")) {
                            res = st.executeQuery("select bk_id from bk_mstr ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "banks";
                               s[1] = res.getString("bk_id");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("warehouses")) {
                            res = st.executeQuery("select wh_id from wh_mstr ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "warehouses";
                               s[1] = res.getString("wh_id");
                               lines.add(s);
                            }
                        }
                        
                        if (sd.equals("locations")) {
                            res = st.executeQuery("select loc_loc from loc_mstr;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "locations";
                               s[1] = res.getString("loc_loc");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("items")) {
                            res = st.executeQuery("select it_item from item_mstr;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "items";
                               s[1] = res.getString("it_item");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("pitems")) {
                            res = st.executeQuery("select it_item from item_mstr where it_code = 'P';");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "pitems";
                               s[1] = res.getString("it_item");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("mitems")) {
                            res = st.executeQuery("select it_item from item_mstr where it_code = 'M';");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "mitems";
                               s[1] = res.getString("it_item");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("aitems")) {
                            res = st.executeQuery("select it_item from item_mstr where it_code = 'A';");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "aitems";
                               s[1] = res.getString("it_item");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("sitems")) {
                            res = st.executeQuery("select it_item from item_mstr where it_code = 'S';");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "sitems";
                               s[1] = res.getString("it_item");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("operations")) {
                            res = st.executeQuery("SELECT wf_op from wf_mstr order by wf_op ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "operations";
                               s[1] = res.getString("wf_op");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("tooling")) {
                            res = st.executeQuery("select it_item from item_mstr where it_type = 'TOOLING';");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "tooling";
                               s[1] = res.getString("it_item");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("nontooling")) {
                            res = st.executeQuery("select it_item from item_mstr where it_type <> 'TOOLING';");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "nontooling";
                               s[1] = res.getString("it_item");
                               lines.add(s);
                            }
                        }
                        if (sd.equals("uoms")) {
                            res = st.executeQuery("select uom_id from uom_mstr;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "uoms";
                               s[1] = res.getString("uom_id");
                               lines.add(s);
                            }
                        }
                        if (sd.startsWith("jaspergroups=")) { // ex:  jaspergroups=ShpRptGroup
                            String[] x = sd.split("=", -1);
                            res = st.executeQuery("select * from jasp_mstr " +
                            " where jasp_group = " + "'" + x[1] + "'" + 
                            " order by cast(jasp_sequence as decimal) ;");
                            while (res.next()) {
                               String[] s = new String[2];
                               s[0] = "jaspergroups";
                               s[1] = res.getString("jasp_desc") + "," + res.getString("jasp_func") + "," + res.getString("jasp_format");
                               lines.add(s);
                            }
                        }
                        
                        
                }
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
        return lines;
    }
    
    public static ArrayList<String[]> getCronInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCronInit"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
        String[] sites = null;
        boolean allsites = false;
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
                    
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
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
               
             
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
            }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'sys_job_class' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "jobs";
               s[1] = res.getString("code_key");
               lines.add(s);
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
        return lines;
    }
    
    public static ArrayList<String[]> getPKSInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPKSInit"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
        String[] sites = null;
        boolean allsites = false;
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
                    
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
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
               
             
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
            }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
            }
            
            res = st.executeQuery("select pks_id from pks_mstr where pks_type = 'store' " + ";");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "stores";
               s[1] = res.getString("pks_id");
               lines.add(s);
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
        return lines;
    }
    
    public static ArrayList<String[]> getUserMenuInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getUserMenuInit"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
        String[] sites = null;
        boolean allsites = false;
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
                    
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
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
               
             
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
            }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
            }
            
            res = st.executeQuery("select user_id from user_mstr order by user_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "users";
               s[1] = res.getString("user_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select menu_id from menu_mstr order by menu_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "menus";
               s[1] = res.getString("menu_id");
               lines.add(s);
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
        return lines;
    }
    
    public static int getMenuCount(String parent) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getMenuCount"});
            list.add(new String[]{"param1", parent});
            try {
                return jsonToInt(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return 0;
            }
        } 
       int count = 0; 
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
            res = st.executeQuery("SELECT mt_child FROM  menu_tree where mt_par = " + "'" + parent + "'" + ";");
           while (res.next()) {
               count++;
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
    return count;

}

    public static String getSiteEmail(String site) {
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

            res = st.executeQuery("select site_sqeemail from site_mstr where site_site = " + "'" + site + "';" );
           while (res.next()) {
            myitem = res.getString("site_sqeemail");                    
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
    return myitem;

}

    public static String getSiteName(String site) {
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

            res = st.executeQuery("select site_desc from site_mstr where site_site = " + "'" + site + "';" );
           while (res.next()) {
            myitem = res.getString("site_desc");                    
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
    return myitem;

}

    public static String[] getSiteAddressInfo(String site) {
       String[] address = new String[9];
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

            res = st.executeQuery("select site_site, site_desc, site_line1, site_line2, site_line3, site_city, site_state, site_zip, site_country from site_mstr where site_site = " + "'" + site + 
                                  "';" );
           while (res.next()) {
            address[0] = res.getString("site_site"); 
            address[1] = res.getString("site_desc");
            address[2] = res.getString("site_line1"); 
            address[3] = res.getString("site_line2");
            address[4] = res.getString("site_line3");
            address[5] = res.getString("site_city"); 
            address[6] = res.getString("site_state"); 
            address[7] = res.getString("site_zip"); 
            address[8] = res.getString("site_country"); 
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

        return address;
    }  

    public static ArrayList<ftp_attr> getFTPAttr(String[] x) {
        
        ftp_attr r = null;
        ArrayList<ftp_attr> list = new ArrayList<ftp_attr>();
        String[] m ;
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getFTPAttr"});
            paramlist.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServADM");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<ftp_attr>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        String sql = "select * from ftp_attr where ftpa_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, x[0]); 
            try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ftp_attr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ftp_attr(m, res.getString("ftpa_id"), 
                            res.getString("ftpa_key"),
                            res.getString("ftpa_value")
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ftp_attr(m);
        }
        return list;
    }
    
    public static HashMap<String, String> getFTPAttrHash(String[] x) {
        ftp_attr r = null;
        HashMap<String, String> map = new HashMap<String, String>();
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getFTPAttrHash"});
            paramlist.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServADM");
                map = objectMapper.readValue(returnstring, new TypeReference<HashMap<String, String>>() {});
                return map;
            } catch (IOException ex) {
                bslog(ex);
                return map;
            }
        }
        
        
        String[] m ;
        String sql = "select * from ftp_attr where ftpa_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, x[0]); 
            try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ftp_attr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        map.put(res.getString("ftpa_key"), res.getString("ftpa_value"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ftp_attr(m);
        }
        return map;
    }
    
    
    
    public static boolean isValidPKSStore(String pksid) {
             
       boolean x = false;
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
                res = st.executeQuery("select pks_id from pks_mstr where pks_id = " + "'" + pksid + "'" +
                        " and pks_type = 'store' "+ ";");
               while (res.next()) {
                    x = true;
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
        return x;
        
    }
    
    public static boolean isValidKeyID(String pksid) {
             
       boolean x = false;
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
                res = st.executeQuery("select pks_id from pks_mstr where pks_id = " + "'" + pksid + "'" 
                        + ";");
               while (res.next()) {
                    x = true;
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
        return x;
        
    }
    
    
    public static String getPKSStoreFileName(String pksid) {
             
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
                res = st.executeQuery("select pks_file from pks_mstr where pks_id = " + "'" + pksid + "'" +
                        " and pks_type = 'store' "+ ";");
               while (res.next()) {
                    x = res.getString("pks_file");
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
        return x;
        
    }
    
    public static ArrayList<String> getPKSStores() {
             
       ArrayList<String> x = new ArrayList<String>();
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
                res = st.executeQuery("select pks_id from pks_mstr where pks_type = 'store' " + ";");
               while (res.next()) {
                    x.add(res.getString("pks_id"));
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
        return x;
        
    }
    
    public static ArrayList<String> getAllPKSKeysExceptStore() {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getAllPKSKeysExceptStore"});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList x = new ArrayList();
        String sql = "select pks_id from pks_mstr where pks_type <> 'store' ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               x.add(res.getString("pks_id"));
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return x;
    }
    
    
    public static String getPKSStorePWD(String pksid) {
             
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
                res = st.executeQuery("select pks_storepass from pks_mstr where pks_id = " + "'" + pksid + "'" +
                        " and pks_type = 'store' "+ ";");
               while (res.next()) {
                    x = bsmf.MainFrame.PassWord("1", res.getString("pks_storepass").toCharArray());
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
        return x;
        
    }
    
    public static ArrayList<String[]> runFTPClient(String c) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "runFTPClient"});
            list.add(new String[]{"param1", c});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServADM"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        boolean dblogging = BlueSeerUtils.ConvertStringToBool(OVData.getSysMetaValue("system", "ftp", "dblogging"));
        boolean hasError = false;
        
        File lf = new File("ftpbss.lck");
        ArrayList<String[]> logdata = new ArrayList<String[]>();
        ftp_mstr fm = admData.getFTPMstr(new String[]{c});
        HashMap<String, String> ftpa = getFTPAttrHash(new String[]{c});
        
        String homeIn = cleanDirString(EDData.getEDIInDir());
        String homeOut = cleanDirString(EDData.getEDIOutDir());
        if (! fm.ftp_indir().isEmpty()) {
         homeIn = cleanDirString(fm.ftp_indir());
        }
        if (! fm.ftp_outdir().isEmpty()) {
         homeOut = cleanDirString(fm.ftp_outdir());
        }
        int timeout = 0;
        if (! fm.ftp_timeout().isEmpty()) {
           timeout = Integer.valueOf(fm.ftp_timeout());
        }
        timeout *= 1000;
               
        if (fm.m[0].equals(BlueSeerUtils.ErrorBit)) {
            if (dblogging) { 
                hasError = true;
                logdata.add(new String[]{"1", "Unable to retrieve ftp_mstr record id"});
                writeFTPLogMulti(logdata, fm);
            } else {
                log("ftp", fm.m[1]); 
            } 
            return logdata; 
        }
        
        
        
        // if sftp is set....run sftp logic then bail
        if (fm.ftp_sftp().equals("1")) {
        
            JSch jsch = new JSch();
            Session session = null;
            Channel channel = null;
            ChannelSftp csftp = null;  
            FileOutputStream in = null;
            Properties config = new Properties();
            
            boolean usePrivateKey = false;
            if (ftpa.containsKey("usePrivateKey")) {
                   if (ftpa.get("usePrivateKey").equals("yes")) {
                       usePrivateKey = true;
                   }
            }
            
            String privateKeyPath = "";
            if (ftpa.containsKey("privateKeyPath")) {
                privateKeyPath = ftpa.get("usePrivateKey");
            }
            
            String knownHostsPath = "";
            if (ftpa.containsKey("knownHostsPath")) {
                knownHostsPath = ftpa.get("knownHostsPath");
            }
            
            if (ftpa.containsKey("StrictHostKeyChecking")) {
                   config.put("StrictHostKeyChecking", ftpa.get("StrictHostKeyChecking"));
            } else {
                   config.put("StrictHostKeyChecking", "no"); 
            }
            
            if (ftpa.containsKey("PreferredAuthentications")) {
                   config.put("PreferredAuthentications", ftpa.get("PreferredAuthentications"));
            } else {
                   config.put("PreferredAuthentications", "publickey,password"); 
            }
            
            if (ftpa.containsKey("server_host_key")) {
                   config.put("server_host_key", ftpa.get("server_host_key"));
            } 
            
            if (ftpa.containsKey("PubkeyAcceptedAlgorithms")) {
                   config.put("PubkeyAcceptedAlgorithms", ftpa.get("PubkeyAcceptedAlgorithms"));
            } 
            
            
             try {
                 
                if (lf.exists()) {
                    logdata.add(new String[]{"0", "ftpbss:  lock file found...exiting.  ftpbss.lck"});
                    if (dblogging) { 
                        writeFTPLogMulti(logdata, fm);
                    } else {
                        log("ftp", logdata); 
                    } 
                    return logdata;
                } else {
                    lf.createNewFile();
                    logdata.add(new String[]{"0", "ftpbss:  creating lock file.  ftpbss.lck"});
                }
                 
                 
                 if (usePrivateKey && ! privateKeyPath.isEmpty()) {
                    jsch.addIdentity(privateKeyPath); 
                 }
                 
                 if (! knownHostsPath.isEmpty()) {
                    jsch.setKnownHosts(knownHostsPath);
                 }
        
                session = jsch.getSession(fm.ftp_login(), fm.ftp_ip(), Integer.valueOf(fm.ftp_port())); 
                session.setPassword(bsmf.MainFrame.PassWord("1", fm.ftp_passwd().toCharArray()));
                
                
                
                
                session.setConfig(config);
                
                logdata.add(new String[]{"0", "***   Attempting sftp connection to " + fm.ftp_ip() + "   ***"});
                session.connect();
                channel = session.openChannel("sftp");
                channel.connect();
                csftp = (ChannelSftp) channel;
                
                
                 for (String line : fm.ftp_commands().split("\\n"))   {
                    String[] splitLine = line.trim().split("\\s+");
                    if (splitLine.length > 1 && splitLine[0].equals("cd")) {
                        try{
                            logdata.add(new String[]{"0", "changing directory..." + splitLine[1]});
                        csftp.cd(splitLine[1]); 
                        } catch(SftpException e){
                            hasError = true;
                            logdata.add(new String[]{"1", e.toString()});
                        }
                        
                    }
                    if (splitLine.length >= 1 && (splitLine[0].equals("dir") || splitLine[0].equals("ls"))) {
                        String x = ".";
                        if (splitLine.length == 2) {
                         x = splitLine[1];
                        }
                        
                        try{
                        logdata.add(new String[]{"0", "listing contents..."});
                        java.util.List ftpFiles = csftp.ls(x); 
                        logdata.add(new String[]{"0", "file count..." + ftpFiles.size()});
                        if (ftpFiles != null) {
                            for (Object f : ftpFiles) {
                                ChannelSftp.LsEntry le = (ChannelSftp.LsEntry) f;
                                logdata.add(new String[]{"0", le.getLongname()});
                            }
		        }
                        } catch(SftpException e){
                            hasError = true;
                            logdata.add(new String[]{"1", e.toString()});
                        }
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("put")) {
                        File localfolder = new File(homeOut);
	                File[] localFiles = localfolder.listFiles();
                        boolean isLocalDelete = false;
                        boolean isSuccess = false;
                        if (splitLine[1].equals("-d")) {
                            isLocalDelete = true;
                        }
                        for (int i = 0; i < localFiles.length; i++) {
                          if (localFiles[i].isFile()) {
                              isSuccess = false;
                              String x = ("\\Q" + splitLine[splitLine.length - 1] + "\\E").replace("*", "\\E.*\\Q");
                                if (localFiles[i].getName().matches(x)) {
                                    InputStream inputStream = new FileInputStream(localFiles[i]);
                                    logdata.add(new String[]{"0", "storing file: " + localFiles[i].getName() + " size: " + localFiles[i].length()} );
                                    try {
                                    csftp.put(inputStream, localFiles[i].getName());
                                     logdata.add(new String[]{"0", "file stored: " + localFiles[i].getName() } );
                                    isSuccess = true;
                                    } catch(SftpException e){
                                        hasError = true;
                                    logdata.add(new String[]{"0", "unable to store file: " + localFiles[i].getName()  } );  
                                    logdata.add(new String[]{"1", e.toString()});
                                    isSuccess = false;
                                    } finally {
                                      if (inputStream != null) {
                                          inputStream.close();
                                      }  
                                      if (isLocalDelete && isSuccess && ! localFiles[i].getName().isBlank()) {
                                        Path filepath = FileSystems.getDefault().getPath(homeOut + localFiles[i].getName());
                                        Files.deleteIfExists(filepath);
                                      }
                                    }
                                }
                          } 
                        }
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("get")) {
                        // first capture list of available files...
                        java.util.List ftpFiles = csftp.ls("."); 
                        if (ftpFiles != null) {
                            for (Object f : ftpFiles) {
                                LsEntry le = (LsEntry) f;
                                String x = ("\\Q" + splitLine[1] + "\\E").replace("*", "\\E.*\\Q");
                                if (! le.getAttrs().isDir() && le.getFilename().matches(x)) {
                                Path inpath = FileSystems.getDefault().getPath(homeIn + "/" + le.getFilename());
	              		in = new FileOutputStream(inpath.toFile());
                                logdata.add(new String[]{"0", "retrieving file: " + le.getFilename() + " size:" + le.getAttrs().getSize()  } );  
                                csftp.get(le.getFilename(), in);
                                in.close();
                                logdata.add(new String[]{"0", "file retrieved: " + le.getFilename()   } ); 
                                    if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_delete()))) {
                                        try {
                                        csftp.rm(le.getFilename());
                                        logdata.add(new String[]{"0", "deleted from server: " + le.getFilename()    } );
                                        } catch(SftpException e){
                                            hasError = true;
                                        logdata.add(new String[]{"0", "Could not delete the file: "+ le.getFilename()} );
                                        logdata.add(new String[]{"1", e.toString()});
                                        }
                                    }
                                }
                            }
		        }
                    } // if get
                    if (splitLine.length > 1 && splitLine[0].equals("delete") || splitLine.length > 1 && splitLine[0].equals("rm")) {
                        // first capture list of available files...
                        java.util.List ftpFiles = csftp.ls("."); 
                        if (ftpFiles != null) {
                            for (Object f : ftpFiles) {
                                LsEntry le = (LsEntry) f;
                                String x = ("\\Q" + splitLine[1] + "\\E").replace("*", "\\E.*\\Q");
                                if (! le.getAttrs().isDir() && le.getFilename().matches(x)) {
                                    try {
                                    csftp.rm(le.getFilename());
                                    logdata.add(new String[]{"0", "deleted from server: " + le.getFilename()});
                                    } catch(SftpException e){
                                        hasError = true;
                                    logdata.add(new String[]{"0", "Could not delete the file: "+ le.getFilename()} );
                                    logdata.add(new String[]{"1", e.toString()});
                                    }
                                in.close();
                                }
                            }
		        }
                    } // if delete
                    
                } // for commands
                
             } catch (Exception e) {
                 hasError = true;
                logdata.add(new String[]{"1", "***   Unable to connect to FTP server. " + e.toString() + "   ***" + ""});
            } finally {
                
                try {
                    
                    if (lf.exists()) {
                    lf.delete();
                    logdata.add(new String[]{"0", "ftpbss:  process complete...removing lock file.  ftpbss.lck"});
                    }
                    
                    if(session != null) {
                        session.disconnect();
                        logdata.add(new String[]{"0", "disconnect session..."});
                    }

                    if(channel != null) {
                        channel.disconnect();
                        logdata.add(new String[]{"0", "disconnect channel..."});
                    }

                    if(csftp != null) {
                        csftp.quit();
                        logdata.add(new String[]{"0", "quit..."});
                    }
                   
                  if (dblogging) { 
                        writeFTPLogMulti(logdata, fm);
                    } else {
                        log("ftp", logdata); 
                    } 
            
                } catch (Exception exc) {
                    hasError = true;
                    logdata.add(new String[]{"1", "***   Unable to disconnect from sFTP server. " + exc.toString()+"   ***"});
                    if (dblogging) { 
                        writeFTPLogMulti(logdata, fm);
                    } else {
                        log("ftp", logdata); 
                    }
                }  
            }
            if (hasError) { 
             sendFTPErrorMail(c, logdata);
            }
            return logdata;
        } 
        
        
         // vanilla FTP run
         FTPClient client = new FTPClient();
         FileOutputStream in = null;
         
           try {
               
               client.setDefaultTimeout(timeout);
               client.setDataTimeout(timeout);
               
              
             //  client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
                if (fm.ftp_port().isEmpty()) {
                    client.connect(fm.ftp_ip());
                } else {
                client.connect(fm.ftp_ip(),Integer.valueOf(fm.ftp_port()));
                }
             
		showServerReply(client, logdata);
                
                int replyCode = client.getReplyCode();
                if (! FTPReply.isPositiveCompletion(replyCode)) {
                    hasError = true;
                    logdata.add(new String[]{"1", "connection failed..." + String.valueOf(replyCode)});
                    if (dblogging) { 
                        writeFTPLogMulti(logdata, fm);
                    } else {
                        log("ftp", logdata); 
                    }
                return logdata;
                }
                
               
		// client.login(tblogin.getText(), String.valueOf(tbpasswd.getPassword()));
                client.login(fm.ftp_login(), bsmf.MainFrame.PassWord("1", fm.ftp_passwd().toCharArray()));
		showServerReply(client, logdata);
                
                
                 if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_passive()))) {
		client.enterLocalPassiveMode();
                logdata.add(new String[]{"0", "CLIENT: setting passive"});
                } else {
                client.enterLocalActiveMode(); 
                logdata.add(new String[]{"0", "CLIENT: setting active"});
                }
                showServerReply(client, logdata);
                
                if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_binary()))) {
		client.setFileType(FTP.BINARY_FILE_TYPE);
                logdata.add(new String[]{"0", "CLIENT: setting binary"});
                } else {
                client.setFileType(FTP.ASCII_FILE_TYPE);
                logdata.add(new String[]{"0", "CLIENT: setting ascii"});
                }
                showServerReply(client, logdata);
		
		    /* not sure why...but in scenario where login credentials are wrong...you have to execute a function (client.listFiles) that 
		       returns IOError to generate the error.....client.login does not return an IOError when wrong login or password without subsequent data dive  */
		
                for (String line : fm.ftp_commands().split("\\n"))   {
                    String[] splitLine = line.trim().split("\\s+");
                    if (splitLine.length > 1 && splitLine[0].equals("cd")) {
                        logdata.add(new String[]{"0", "changing directory..." + splitLine[1]});
                        client.changeWorkingDirectory(splitLine[1]);
                        showServerReply(client, logdata);
                    }
                    if (splitLine.length >= 1 && (splitLine[0].equals("dir") || splitLine[0].equals("ls"))) {
                        String x = "";
                        if (splitLine.length == 2) {
                         x = splitLine[1];
                        }
                        FTPFile[] ftpFiles = client.listFiles(x);
                        if (ftpFiles != null) {
                            for (FTPFile f : ftpFiles) {
                                logdata.add(new String[]{"0", f.getName()});
                            }
		        }
                        showServerReply(client, logdata);
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("put")) {
                        File localfolder = new File(homeOut);
	                File[] localFiles = localfolder.listFiles();
                        boolean isLocalDelete = false;
                        boolean isSuccess = false;
                        if (splitLine[1].equals("-d")) {
                            isLocalDelete = true;
                        }
                        for (int i = 0; i < localFiles.length; i++) {
                          if (localFiles[i].isFile()) {
                              String x = ("\\Q" + splitLine[splitLine.length - 1] + "\\E").replace("*", "\\E.*\\Q");
                                if (localFiles[i].getName().matches(x)) {
                                    InputStream inputStream = new FileInputStream(localFiles[i]);
                                    boolean done = client.storeFile(localFiles[i].getName(), inputStream);
                                    inputStream.close();
                                    if (done) {
                                        logdata.add(new String[]{"0", "file stored: " + localFiles[i].getName()});
                                        isSuccess = true;
                                    } else {
                                        hasError = true;
                                        logdata.add(new String[]{"1", "unable to store file: " + localFiles[i].getName()});
                                        isSuccess = false;
                                    }   
                                    if (isLocalDelete && isSuccess && ! localFiles[i].getName().isBlank()) {
                                        Path filepath = FileSystems.getDefault().getPath(homeOut + localFiles[i].getName());
                                        Files.deleteIfExists(filepath);
                                        logdata.add(new String[]{"0", "deleting local file: " + localFiles[i].getName()});
                                    }    
                                }
                          } 
                        }
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("get")) {
                        // first capture list of available files...
                        FTPFile[] ftpFiles = client.listFiles();
                        if (ftpFiles != null) {
                            for (FTPFile f : ftpFiles) {
                                String x = ("\\Q" + splitLine[1] + "\\E").replace("*", "\\E.*\\Q");
                                if (! f.isDirectory() && f.getName().matches(x)) {
                                Path inpath = Paths.get(homeIn + "\\" + f.getName());
	              		in = new FileOutputStream(inpath.toFile());
                                client.retrieveFile(f.getName(), in);
                                in.close();
                                logdata.add(new String[]{"0", "retrieving file: " + f.getName()});
                                showServerReply(client, logdata);
                                if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_delete()))) {
                                    boolean deleted = client.deleteFile(f.getName());
                                    if (deleted) {
                                        logdata.add(new String[]{"0", "deleted from server: " + f.getName()});
                                    } else {
                                        hasError = true;
                                        logdata.add(new String[]{"1", "Could not delete the file: "+ f.getName()});
                                    }
                                }
                                }
                            }
		        }
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("delete") || splitLine.length > 1 && splitLine[0].equals("rm")) {
                        // first capture list of available files...
                        FTPFile[] ftpFiles = client.listFiles();
                        if (ftpFiles != null) {
                            for (FTPFile f : ftpFiles) {
                                String x = ("\\Q" + splitLine[1] + "\\E").replace("*", "\\E.*\\Q");
                               // if (! le.getAttrs().isDir() && le.getFilename().matches(x)) {
                                if (! f.isDirectory() && f.getName().matches(x)) {
                                logdata.add(new String[]{"0", "deleting file: " + f.getName() + " size:" + f.getSize()});
                                client.deleteFile(f.getName());
                                in.close();
                                logdata.add(new String[]{"0", "file deleted: " + f.getName()});
                                showServerReply(client, logdata);
                                }
                            }
		        }
                    }
                } 
		    
                client.logout();
                showServerReply(client, logdata);
                client.disconnect();
                showServerReply(client, logdata);
		
		
	} catch (SocketException e) {
            hasError = true;
            logdata.add(new String[]{"1", "socket error: " + e.getMessage()});
                if (dblogging) { 
                        writeFTPLogMulti(logdata, fm);
                    } else {
                        log("ftp", logdata); 
                    }  
	} catch (IOException e) {
                hasError = true;
                logdata.add(new String[]{"1", "io error: " + e.getMessage()});
                if (dblogging) { 
                        writeFTPLogMulti(logdata, fm);
                    } else {
                        log("ftp", logdata); 
                    }  
		
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (client.isConnected()) {
              try {
                      client.disconnect();
              } catch (IOException ex) {
                  ex.printStackTrace();
              }
            }
            if (dblogging) { 
                writeFTPLogMulti(logdata, fm);
            } else {
                log("ftp", logdata); 
            }  
       }
   
     if (hasError) {      
      sendFTPErrorMail(c, logdata);
     }
     
     return logdata;
    }
    
    private static void showServerReply(FTPClient ftpClient, ArrayList<String[]> list) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            
            for (String aReply : replies) {
                list.add(new String[]{"0", "SERVER: " + aReply});
            }
        }
    }
    
    public static ArrayList<cron_mstr> getCronMstrEnabled() {
        cron_mstr r = null;
        ArrayList<cron_mstr> list = new ArrayList<cron_mstr>();
        String[] m ;
        String sql = "select * from cron_mstr where cron_enabled = '1' ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cron_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cron_mstr(m, res.getString("cron_jobid"), 
                            res.getString("cron_desc"),
                            res.getString("cron_group"),
                            res.getString("cron_prog"),    
                            res.getString("cron_param"),
                            res.getString("cron_priority"),
                            res.getString("cron_expression"),
                            res.getString("cron_enabled"),
                            res.getString("cron_modflag"),
                            res.getString("cron_lastrun"),
                            res.getString("cron_lastmod"),
                            res.getString("cron_userid")
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cron_mstr(m);
        }
        return list;
    }
    
    public static ArrayList<cron_mstr> getCronMstrMod() {
        cron_mstr r = null;
        ArrayList<cron_mstr> list = new ArrayList<cron_mstr>();
        String[] m ;
        String sql = "select * from cron_mstr where cron_modflag = '1' ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cron_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cron_mstr(m, res.getString("cron_jobid"), 
                            res.getString("cron_desc"),
                            res.getString("cron_group"),
                            res.getString("cron_prog"),    
                            res.getString("cron_param"),
                            res.getString("cron_priority"),
                            res.getString("cron_expression"),
                            res.getString("cron_enabled"),
                            res.getString("cron_modflag"),
                            res.getString("cron_lastrun"),
                            res.getString("cron_lastmod"),
                            res.getString("cron_userid")
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cron_mstr(m);
        }
        return list;
    }
    
    public static void updateCronJobID(String jobid, String modflag) {
         
       try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        try{
            st.executeUpdate("update cron_mstr set cron_modflag = " + "'" + modflag + "'" + 
                    " where cron_jobid = " + "'" + jobid + "'" + ";" );
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
    
    public static void updateCronJobIDMulti(ArrayList<String> list, String modflag) {
         
       try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        try{
            for (String x : list) {
            st.executeUpdate("update cron_mstr set cron_modflag = " + "'" + modflag + "'" + 
                    " where cron_jobid = " + "'" + x + "'" + ";" );
            }
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
    
    public static void updateCronLastRun(String jobid, String ts) {
         
       try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        try{
            st.executeUpdate("update cron_mstr set cron_lastrun = " + "'" + ts + "'" + 
                    " where cron_jobid = " + "'" + jobid + "'" + ";" );
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
    
    public static void updateDefaultCurrency(String x) {
         DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        try{
            st.executeUpdate("update ov_mstr set ov_currency = " + "'" + x + "'" + ";" );
            st.executeUpdate("update ac_mstr set ac_cur = " + "'" + x + "'" + ";" );
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
    
    public static void updateDefaultCountry(String x) {
      Locale[] availableLocales = Locale.getAvailableLocales();
        HashMap<String, String> map = new HashMap<String, String>();
        for ( Locale l : availableLocales ) {
        	if (isValidLocale(l))
            map.put(  l.getISO3Country(), l.getCountry());
        }  
          if (map.get(x) != null) {
          Locale locale = new Locale("",map.get(x));
          Currency currency = Currency.getInstance(locale);
          updateDefaultCurrency(currency.getCurrencyCode());
          writeBSConfig(map.get(x));
          } else {
            bsmf.MainFrame.show(getMessageTag(1158));
            System.exit(0);
          }
    }
      
    
    public static boolean isValidLocale(Locale locale) {
      try {
        return locale.getISO3Language() != null && locale.getISO3Country() != null;
      }
      catch (MissingResourceException e) {
        return false;
      }
    }

    public static void writeBSConfig(String x) {
        BufferedWriter f = null;
        try {
            f = new BufferedWriter(new FileWriter("bs.cfg", true));
        } catch (IOException ex) {
            bslog(ex);
        }
        try {
            f.write("COUNTRY="  + x.toUpperCase() + "\n");
        } catch (IOException ex) {
            bslog(ex);
        }
        try {
            f.close();
        } catch (IOException ex) {
            bslog(ex);
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
                       String user_cell, String user_rmks, String user_passwd, String user_allowedsites) {
        public user_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", ""
                    );
        }
    }

    public record ov_mstr(String[] m, String ov_site, String ov_cc, String ov_wh, 
        String ov_currency, String ov_labelprinter) {
        public ov_mstr(String[] m) {
            this(m, "", "", "", "", "");
        }
    }

    public record ov_ctrl(String[] m, String ov_version, String ov_dist_dir, String ov_source_dir, 
        int ov_login, int ov_custom, String ov_bgimage, int ov_rcolor, int ov_gcolor, int ov_bcolor,
        String ov_fileservertype, String ov_image_directory, String ov_temp_directory, String ov_label_directory, 
        String ov_jasper_directory, String ov_edi_directory, String ov_email_server,
        String ov_email_from, String ov_smtpauthuser, String ov_smtpauthpass, String ov_varchar, String ov_notes) {
        public ov_ctrl(String[] m) {
            this(m, "", "", "", 0, 0, "", 0, 0, 0, "",
                    "", "", "", "", "", "", "", "", "", "", "");
        }
    }

    public record ftp_mstr(String[] m, String ftp_id, String ftp_desc, String ftp_ip, String ftp_port, 
        String ftp_login, String ftp_passwd, String ftp_commands, String ftp_indir, 
        String ftp_outdir, String ftp_delete, String ftp_passive, String ftp_binary, 
        String ftp_timeout, String ftp_enabled, String ftp_sftp, String ftp_site, String ftp_email) {
        public ftp_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "");
        }
        
    }
    
    public record ftp_attr(String[] m, String ftpa_id, String ftpa_key, String ftpa_value) {
        public ftp_attr(String[] m) {
            this(m, "", "", "");
        }
    }

    
    public record code_mstr(String[] m, String code_code, String code_key, String code_value,
        String code_internal ) {
        public code_mstr(String[] m) {
            this(m, "", "", "", "");
        }
    }
    
    public record jasp_mstr(String[] m, String jasp_group, String jasp_desc, String jasp_func, 
        String jasp_sequence, String jasp_format ) {
        public jasp_mstr(String[] m) {
            this(m, "", "", "", "", "");
        }
    }
    
    public record counter(String[] m, String counter_name, String counter_desc, String counter_prefix, 
        String counter_from, String counter_to, String counter_id ) {
        public counter(String[] m) {
            this(m, "", "", "", "", "", "");
        }
    }
    
    public record menu_mstr(String[] m, String menu_id, String menu_desc, String menu_type, 
        String menu_panel, String menu_navcode ) {
        public menu_mstr(String[] m) {
            this(m, "", "", "", "", "");
        }
    }
    
    public record menu_tree(String[] m, String mt_par, String mt_child, String mt_index, String mt_type, 
        String mt_label, String mt_icon, String mt_initvar, String mt_func, int mt_visible, int mt_enable ) {
        public menu_tree(String[] m) {
            this(m, "", "", "", "", "", "", "", "", 0, 0);
        }
    }
    
    public record panel_mstr(String[] m, String panel_id, String panel_desc, String panel_core ) {
        public panel_mstr(String[] m) {
            this(m, "", "", "");
        }
    }
    
    
    public record prt_mstr(String[] m, String prt_id, String prt_desc, String prt_type, 
        String prt_ip, String prt_port ) {
        public prt_mstr(String[] m) {
            this(m, "", "", "", "", "");
        }
    }
            
    public record pks_mstr(String[] m, String pks_id, String pks_desc, String pks_type, 
        String pks_user, String pks_pass, String pks_file, String pks_storeuser, String pks_storepass,
        String pks_expire, String pks_create, String pks_parent, String pks_standard, String pks_external, String pks_keyid ) {
        public pks_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record cron_mstr(String[] m, String cron_jobid, String cron_desc, String cron_group, 
        String cron_prog, String cron_param, String cron_priority, String cron_expression, String cron_enabled,
        String cron_modflag, String cron_lastrun, String cron_lastmod, String cron_userid ) {
        public cron_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", "", "");
        }
    }
   
    public record change_log(String[] m, String chg_id, String chg_key, String chg_table, 
        String chg_class, String chg_userid, String chg_desc, String chg_ts, String chg_type,
        String chg_ref ) {
        public change_log(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record txt_meta(String[] m, String txt_id, String txt_type, String txt_key, String txt_value) {
        public txt_meta(String[] m) {
            this(m, "", "", "", "");
        }
    }
    
    
}
