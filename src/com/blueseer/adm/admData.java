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
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.EDData;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
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
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
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
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  site_mstr where site_site = ?";
        String sqlInsert = "insert into site_mstr (site_site, site_desc, site_line1, site_line2, site_line3, "
                        + " site_city, site_state, site_country, site_zip, site_logo, site_iv_jasper, " 
                        + " site_sh_jasper, site_po_jasper, site_or_jasper, site_pos_jasper ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
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
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
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
    
    public static site_mstr getSiteMstr(String[] x) {
        site_mstr r = null;
        String[] m = new String[2];
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
       String[] m = new String[2];
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
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  user_mstr where user_id = ?";
        String sqlInsert = "insert into user_mstr (user_id, user_site, user_lname, "
                        + " user_fname, user_mname, user_email, user_phone, user_cell, " 
                        + " user_rmks, user_passwd ) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
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
            psi.setString(10, x.user_passwd); 
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
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
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

    public static user_mstr getUserMstr(String[] x) {
        user_mstr r = null;
        String[] m = new String[2];
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
                            res.getString("user_passwd")
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
       String[] m = new String[2];
        String sql = "delete from user_mstr where user_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.user_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    
    public static String[] addUpdateOVMstr(ov_mstr x) {
        int rows = 0;
        String[] m = new String[2];
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
   
    public static ov_mstr getOVMstr(String[] x) {
        ov_mstr r = null;
        String[] m = new String[2];
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
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  ftp_mstr where ftp_id = ?";
        String sqlInsert = "insert into ftp_mstr (ftp_id, ftp_desc, ftp_ip, ftp_login, " +
                          " ftp_passwd, ftp_commands, ftp_indir, ftp_outdir, ftp_delete, ftp_passive, " +
                          " ftp_binary, ftp_timeout, ftp_port, ftp_enabled, ftp_sftp ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
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
            psi.setString(5, x.ftp_passwd);
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
        String[] m = new String[2];
        String sql = "update ftp_mstr set ftp_desc = ?, ftp_ip = ?, ftp_login = ?, " +
                          " ftp_passwd = ?, ftp_commands = ?, ftp_indir = ?, ftp_outdir = ?, " +
                          " ftp_delete = ?, ftp_passive = ?, " +
                          " ftp_binary = ?, ftp_timeout = ?, ftp_port = ?,  " +
                          " ftp_enabled = ?, ftp_sftp = ? " +
                          " where ftp_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(15, x.ftp_id);
            ps.setString(1, x.ftp_desc);
            ps.setString(2, x.ftp_ip);
            ps.setString(3, x.ftp_login);
            ps.setString(4, x.ftp_passwd);
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
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteFTPMstr(ftp_mstr x) { 
       String[] m = new String[2];
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
        String[] m = new String[2];
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
                            res.getString("ftp_sftp")
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
       String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
       String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
       String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
       String[] m = new String[2];
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
    
    public static String[] addPanelMstr(panel_mstr x) {
        String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
       String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
        
    public static prt_mstr getPrtMstr(String[] x) {
        prt_mstr r = null;
        String[] m = new String[2];
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
       String[] m = new String[2];
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
    
    
    public static String[] addPksMstr(pks_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  pks_mstr where pks_id = ? ";
        String sqlInsert = "insert into pks_mstr (pks_id, pks_desc, pks_type, "
                        + " pks_user, pks_pass, pks_file, pks_storeuser, pks_storepass, " 
                        + " pks_expire, pks_create, pks_parent ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
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
        String[] m = new String[2];
        String sql = "update pks_mstr set pks_desc = ?, pks_type = ?, pks_user = ?,  " +   
                          " pks_pass = ? , pks_file = ?, pks_storeuser = ?, " +
                          " pks_storepass = ?, pks_expire = ?, pks_create = ?, pks_parent = ? " +
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
        ps.setString(11, x.pks_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deletePksMstr(pks_mstr x) { 
       String[] m = new String[2];
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
        String[] m = new String[2];
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
                            res.getString("pks_parent")
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
       String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
        String[] m = new String[2];
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
    
    
    // misc
    
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
    
    public static void runFTPClient(String c) {
        ftp_mstr fm = admData.getFTPMstr(new String[]{c});
        
        if (fm.m[0].equals(BlueSeerUtils.ErrorBit)) {
            System.out.println(fm.m[1]);
            return;
        }
         FTPClient client = new FTPClient();
         FileOutputStream in = null;
         
           try {
               String homeIn = EDData.getEDIInDir();
               String homeOut = EDData.getEDIOutDir();
               int timeout = 0;
               if (! fm.ftp_timeout().isEmpty()) {
                   timeout = Integer.valueOf(fm.ftp_timeout());
               }
               timeout *= 1000;
               client.setDefaultTimeout(timeout);
               client.setDataTimeout(timeout);
               
                if (! fm.ftp_indir().isEmpty()) {
                 homeIn = fm.ftp_indir();
                }
                if (! fm.ftp_outdir().isEmpty()) {
                 homeOut = fm.ftp_outdir();
                }
             //  client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
		client.connect(fm.ftp_ip());
		showServerReply(client);
                
                int replyCode = client.getReplyCode();
                if (! FTPReply.isPositiveCompletion(replyCode)) {
                    System.out.println("connection failed..." + String.valueOf(replyCode));
                return;
                }
                
               
		// client.login(tblogin.getText(), String.valueOf(tbpasswd.getPassword()));
                client.login(fm.ftp_login(), fm.ftp_passwd());
		showServerReply(client);
                
                
                 if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_passive()))) {
		client.enterLocalPassiveMode();
                System.out.println("CLIENT: setting passive");
                } else {
                client.enterLocalActiveMode(); 
                System.out.println("CLIENT: setting active");
                }
                showServerReply(client);
                
                if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_binary()))) {
		client.setFileType(FTP.BINARY_FILE_TYPE);
                System.out.println("CLIENT: setting binary");
                } else {
                client.setFileType(FTP.ASCII_FILE_TYPE);
                System.out.println("CLIENT: setting ascii");
                }
                showServerReply(client);
		
		    /* not sure why...but in scenario where login credentials are wrong...you have to execute a function (client.listFiles) that 
		       returns IOError to generate the error.....client.login does not return an IOError when wrong login or password without subsequent data dive  */
		
                for (String line : fm.ftp_commands().split("\\n"))   {
                    String[] splitLine = line.trim().split("\\s+");
                    if (splitLine.length > 1 && splitLine[0].equals("cd")) {
                        client.changeWorkingDirectory(splitLine[1]);
                        showServerReply(client);
                    }
                    if (splitLine.length >= 1 && (splitLine[0].equals("dir") || splitLine[0].equals("ls"))) {
                        String x = "";
                        if (splitLine.length == 2) {
                         x = splitLine[1];
                        }
                        FTPFile[] ftpFiles = client.listFiles(x);
                        if (ftpFiles != null) {
                            for (FTPFile f : ftpFiles) {
                                System.out.println(f.getName());
                            }
		        }
                        showServerReply(client);
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("put")) {
                        File localfolder = new File(homeOut);
	                File[] localFiles = localfolder.listFiles();
                        for (int i = 0; i < localFiles.length; i++) {
                          if (localFiles[i].isFile()) {
                              String x = ("\\Q" + splitLine[1] + "\\E").replace("*", "\\E.*\\Q");
                                if (localFiles[i].getName().matches(x)) {
                                    InputStream inputStream = new FileInputStream(localFiles[i]);
                                    boolean done = client.storeFile(localFiles[i].getName(), inputStream);
                                    inputStream.close();
                                    if (done) {
                                        System.out.println("putting file: " + localFiles[i].getName());
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
                                if (f.getName().matches(x)) {
                                Path inpath = Paths.get(homeIn + "\\" + f.getName());
	              		in = new FileOutputStream(inpath.toFile());
                                client.retrieveFile(f.getName(), in);
                                in.close();
                                System.out.println("retrieving file: " + f.getName());
                                showServerReply(client);
                                if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_delete()))) {
                                    boolean deleted = client.deleteFile(f.getName());
                                    if (deleted) {
                                        System.out.println("deleted from server: " + f.getName());
                                    } else {
                                        System.out.println("Could not delete the file: "+ f.getName());
                                    }
                                }
                                }
                            }
		        }
                    }
                } 
		    
                client.logout();
                showServerReply(client);
                client.disconnect();
                showServerReply(client);
		
		
	} catch (SocketException e) {
		System.out.println("socket error: " + e.getMessage());
	} catch (IOException e) {
		System.out.println("io error: " + e.getMessage());
		
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
       }
   
    }
    
    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
    
    public static ArrayList<cron_mstr> getCronMstrEnabled() {
        cron_mstr r = null;
        ArrayList<cron_mstr> list = new ArrayList<cron_mstr>();
        String[] m = new String[2];
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
        String[] m = new String[2];
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
                       String user_cell, String user_rmks, String user_passwd) {
        public user_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", ""
                    );
        }
    }

    public record ov_mstr(String[] m, String ov_site, String ov_cc, String ov_wh, 
        String ov_currency, String ov_labelprinter) {
        public ov_mstr(String[] m) {
            this(m, "", "", "", "", "");
        }
    }

    public record ftp_mstr(String[] m, String ftp_id, String ftp_desc, String ftp_ip, String ftp_port, 
        String ftp_login, String ftp_passwd, String ftp_commands, String ftp_indir, 
        String ftp_outdir, String ftp_delete, String ftp_passive, String ftp_binary, 
        String ftp_timeout, String ftp_enabled, String ftp_sftp) {
        public ftp_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "");
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
        String pks_expire, String pks_create, String pks_parent ) {
        public pks_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", "");
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
    
    
}
