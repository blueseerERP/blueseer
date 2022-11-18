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
package com.blueseer.edi;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
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
import java.util.ArrayList;

/**
 *
 * @author terryva
 */
public class ediData {
    
    public static String[] addEDIXref(edi_xref x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  edi_xref where exr_tpid = ? and exr_tpaddr = ? " +
                " and exr_ovaddr = ? and exr_gsid = ? and exr_type = ?";
        String sqlInsert = "insert into edi_xref (exr_tpid, exr_tpaddr, exr_ovaddr, exr_gsid, exr_type) " 
                        + " values (?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.exr_tpid);
             ps.setString(2, x.exr_tpaddr);
             ps.setString(3, x.exr_ovaddr);
             ps.setString(4, x.exr_gsid);
             ps.setString(5, x.exr_type);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.exr_tpid);
             psi.setString(2, x.exr_tpaddr);
             psi.setString(3, x.exr_ovaddr);
             psi.setString(4, x.exr_gsid);
             psi.setString(5, x.exr_type);
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

    public static String[] addOrUpdateEDIXref(edi_xref x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  edi_xref where exr_tpid = ? and exr_tpaddr = ? " +
                " and exr_ovaddr = ? and exr_gsid = ? and exr_type = ?";
        String sqlInsert = "insert into edi_xref (exr_tpid, exr_tpaddr, exr_ovaddr, exr_gsid, exr_type) " 
                        + " values (?,?,?,?,?); "; 
        String sqlUpdate = "update edi_xref set exr_tpid = ?, exr_tpaddr = ?, exr_ovaddr = ?, " +
                           " exr_gsid = ?, exr_type = ? ; "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.exr_tpid);
             ps.setString(2, x.exr_tpaddr);
             ps.setString(3, x.exr_ovaddr);
             ps.setString(4, x.exr_gsid);
             ps.setString(5, x.exr_type); 
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.exr_tpid);
             psi.setString(2, x.exr_tpaddr);
             psi.setString(3, x.exr_ovaddr);
             psi.setString(4, x.exr_gsid);
             psi.setString(5, x.exr_type); 
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.exr_tpid);
             psu.setString(2, x.exr_tpaddr);
             psu.setString(3, x.exr_ovaddr);
             psu.setString(4, x.exr_gsid);
             psu.setString(5, x.exr_type); 
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

    public static String[] updateEDIXref(edi_xref x) {
        String[] m = new String[2];
        String sqlUpdate = "update edi_xref set exr_tpid = ?, exr_tpaddr = ?, exr_ovaddr = ?, " +
                           " exr_gsid = ?, exr_type = ? ; "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
        ps.setString(1, x.exr_tpid);
             ps.setString(2, x.exr_tpaddr);
             ps.setString(3, x.exr_ovaddr);
             ps.setString(4, x.exr_gsid);
             ps.setString(5, x.exr_type);  
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    public static String[] deleteEDIXref(edi_xref x) { 
       String[] m = new String[2];
        String sql = "delete from edi_xref where exr_tpid = ? and exr_tpaddr = ? " +
                " and exr_ovaddr = ? and exr_gsid = ? and exr_type = ?";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
         ps.setString(1, x.exr_tpid);
         ps.setString(2, x.exr_tpaddr);
         ps.setString(3, x.exr_ovaddr);
         ps.setString(4, x.exr_gsid);
         ps.setString(5, x.exr_type);  
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static edi_xref getEDIXref(String[] x) {
        edi_xref r = null;
        String[] m = new String[2];       
        String sqlSelect = "SELECT * FROM  edi_xref where exr_tpid = ? and exr_gsid = ? " +
                " and exr_type = ? and exr_tpaddr = ? and exr_ovaddr = ?";
        
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sqlSelect);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
        if (x.length > 2) {
        ps.setString(3, x[2]);
        ps.setString(4, x[3]);
        ps.setString(5, x[4]);
        }
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new edi_xref(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new edi_xref(m, 
                            res.getString("exr_tpid"), 
                            res.getString("exr_tpaddr"),
                            res.getString("exr_ovaddr"),
                            res.getString("exr_gsid"),
                            res.getString("exr_type")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new edi_xref(m);
        }
        return r;
    }
    
    public static String[] addMapMstr(map_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from map_mstr where map_id = ?";
        String sqlInsert = "insert into map_mstr (map_id, map_desc, map_version, map_ifs, map_ofs, "
                + " map_indoctype, map_infiletype, map_outdoctype, map_outfiletype, map_source, map_package, map_internal )  " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.map_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.map_id);
            psi.setString(2, x.map_desc);
            psi.setString(3, x.map_version);
            psi.setString(4, x.map_ifs);
            psi.setString(5, x.map_ofs);
            psi.setString(6, x.map_indoctype);
            psi.setString(7, x.map_infiletype);
            psi.setString(8, x.map_outdoctype);
            psi.setString(9, x.map_outfiletype);
            psi.setString(10, x.map_source);
            psi.setString(11, x.map_package);
            psi.setString(12, x.map_internal);
            
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
        
    public static String[] updateMapMstr(map_mstr x) {
        String[] m = new String[2];
        String sql = "update map_mstr set map_desc = ?, map_version = ?, map_ifs = ?, " +
                " map_ofs = ?, map_indoctype = ?, map_infiletype = ?, map_outdoctype = ?, map_outfiletype = ?, " +
                " map_source = ?, map_package = ?, map_internal = ? " +
                " where map_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.map_desc);
        ps.setString(2, x.map_version);
        ps.setString(3, x.map_ifs);
        ps.setString(4, x.map_ofs);
        ps.setString(5, x.map_indoctype);
        ps.setString(6, x.map_infiletype);
        ps.setString(7, x.map_outdoctype);
        ps.setString(8, x.map_outfiletype);
        ps.setString(9, x.map_source);
        ps.setString(10, x.map_package);
        ps.setString(11, x.map_internal);
        ps.setString(12, x.map_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteMapMstr(map_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from map_mstr where map_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.map_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static map_mstr getMapMstr(String[] x) {
        map_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from map_mstr where map_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new map_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new map_mstr(m, res.getString("map_id"), 
                            res.getString("map_desc"),
                            res.getString("map_version"),
                            res.getString("map_ifs"),
                            res.getString("map_ofs"),
                            res.getString("map_indoctype"),
                            res.getString("map_infiletype"),
                            res.getString("map_outdoctype"),
                            res.getString("map_outfiletype"),
                            res.getString("map_source"),
                            res.getString("map_package"),
                            res.getString("map_internal")    
                                

                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new map_mstr(m);
        }
        return r;
    }
    
    public static String[] addMapStruct(map_struct x) {
        String[] m = new String[2];
        String sqlSelect = "select * from map_struct where mps_id = ?";
        String sqlInsert = "insert into map_struct (mps_id, mps_desc, mps_version, mps_doctype, mps_filetype "
                + "  )  " +
                " values (?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.mps_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.mps_id);
            psi.setString(2, x.mps_desc);
            psi.setString(3, x.mps_version);
            psi.setString(4, x.mps_doctype);
            psi.setString(5, x.mps_filetype);
            
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
        
    public static String[] updateMapStruct(map_struct x) {
        String[] m = new String[2];
        String sql = "update map_struct set mps_desc = ?, mps_version = ?, mps_doctype = ?, mps_filetype = ? " +
                "  where mps_id = ? ";
       try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.mps_desc);
        ps.setString(2, x.mps_version);
        ps.setString(3, x.mps_doctype);
        ps.setString(4, x.mps_filetype);
        ps.setString(5, x.mps_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteMapStruct(map_struct x) { 
       String[] m = new String[2];
        String sql = "delete from map_struct where mps_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.mps_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static map_struct getMapStruct(String[] x) {
        map_struct r = null;
        String[] m = new String[2];
        String sql = "select * from map_struct where mps_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new map_struct(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new map_struct(m, res.getString("mps_id"), 
                            res.getString("mps_desc"),
                            res.getString("mps_version"),
                            res.getString("mps_doctype"),
                            res.getString("mps_filetype")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new map_struct(m);
        }
        return r;
    }
    
    
    public static String[] addAPIMaint(api_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from api_mstr where api_id = ?";
        String sqlInsert = "insert into api_mstr (api_id, api_desc, api_version," +
        " api_url, api_port, api_path, api_user, " +
        " api_pass, api_key, api_protocol, api_class, api_encrypted, api_signed, api_cert ) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.api_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.api_id);
            psi.setString(2, x.api_desc);
            psi.setString(3, x.api_version);
            psi.setString(4, x.api_url);
            psi.setString(5, x.api_port);
            psi.setString(6, x.api_path);
            psi.setString(7, x.api_user);
            psi.setString(8, x.api_pass);
            psi.setString(9, x.api_key);
            psi.setString(10, x.api_protocol);
            psi.setString(11, x.api_class);
            ps.setString(12, x.api_encrypted);
            ps.setString(13, x.api_signed);
            ps.setString(13, x.api_cert);
            
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

    public static String[] addAS2Maint(as2_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from as2_mstr where as2_id = ?";
        String sqlInsert = "insert into as2_mstr (as2_id, as2_desc, as2_version," +
        " as2_url, as2_port, as2_path, as2_user, " +
        " as2_pass, as2_key, as2_protocol, as2_class, as2_indir, as2_outdir, as2_encrypted, as2_signed, as2_enccert, " +
                " as2_forceencrypted, as2_forcesigned, as2_signcert, as2_encalgo, as2_signalgo, as2_micalgo ) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.as2_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.as2_id);
            psi.setString(2, x.as2_desc);
            psi.setString(3, x.as2_version);
            psi.setString(4, x.as2_url);
            psi.setString(5, x.as2_port);
            psi.setString(6, x.as2_path);
            psi.setString(7, x.as2_user);
            psi.setString(8, x.as2_pass);
            psi.setString(9, x.as2_key);
            psi.setString(10, x.as2_protocol);
            psi.setString(11, x.as2_class);
            psi.setString(12, x.as2_indir);
            psi.setString(13, x.as2_outdir);
            ps.setString(14, x.as2_encrypted);
            ps.setString(15, x.as2_signed);
            ps.setString(16, x.as2_enccert);
            ps.setString(17, x.as2_forceencrypted);
            ps.setString(18, x.as2_forcesigned);
            ps.setString(19, x.as2_signcert);
            ps.setString(20, x.as2_encalgo);
            ps.setString(21, x.as2_signalgo);
            ps.setString(22, x.as2_micalgo);
            
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

    
    private static int _addAPIMstr(api_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from api_mstr where api_id = ?";
        String sqlInsert = "insert into api_mstr (api_id, api_desc, api_version," +
        " api_url, api_port, api_path, api_user, " +
        " api_pass, api_key, api_protocol, api_class, api_encrypted, api_signed, api_cert ) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.api_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.api_id);
            ps.setString(2, x.api_desc);
            ps.setString(3, x.api_version);
            ps.setString(4, x.api_url);
            ps.setString(5, x.api_port);
            ps.setString(6, x.api_path);
            ps.setString(7, x.api_user);
            ps.setString(8, x.api_pass);
            ps.setString(9, x.api_key);
            ps.setString(10, x.api_protocol);
            ps.setString(11, x.api_class);
            ps.setString(12, x.api_encrypted);
            ps.setString(13, x.api_signed);
            ps.setString(14, x.api_cert);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addAS2Mstr(as2_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from as2_mstr where as2_id = ?";
        String sqlInsert = "insert into as2_mstr (as2_id, as2_desc, as2_version," +
        " as2_url, as2_port, as2_path, as2_user, " +
        " as2_pass, as2_key, as2_protocol, as2_class, as2_indir, as2_outdir, " +
                " as2_encrypted, as2_signed, as2_enccert, as2_forceencrypted, as2_forcesigned, as2_signcert, " +
                " as2_encalgo, as2_signalgo, as2_micalgo ) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.as2_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.as2_id);
            ps.setString(2, x.as2_desc);
            ps.setString(3, x.as2_version);
            ps.setString(4, x.as2_url);
            ps.setString(5, x.as2_port);
            ps.setString(6, x.as2_path);
            ps.setString(7, x.as2_user);
            ps.setString(8, x.as2_pass);
            ps.setString(9, x.as2_key);
            ps.setString(10, x.as2_protocol);
            ps.setString(11, x.as2_class);
            ps.setString(12, x.as2_indir);
            ps.setString(13, x.as2_outdir);
            ps.setString(14, x.as2_encrypted);
            ps.setString(15, x.as2_signed);
            ps.setString(16, x.as2_enccert);
            ps.setString(17, x.as2_forceencrypted);
            ps.setString(18, x.as2_forcesigned);
            ps.setString(19, x.as2_signcert);
            ps.setString(20, x.as2_encalgo);
            ps.setString(21, x.as2_signalgo);
            ps.setString(22, x.as2_micalgo);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    
    private static int _addAPIDet(api_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from api_det where apid_id = ? and apid_method = ?";
        String sqlInsert = "insert into api_det (apid_id, apid_method, apid_seq,  " +
                             " apid_verb, apid_type, apid_path, apid_key, " +
                            " apid_value, apid_source, apid_destination, apid_enabled ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.apid_id);
          ps.setString(2, x.apid_method);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.apid_id);
            ps.setString(2, x.apid_method);
            ps.setString(3, x.apid_seq); 
            ps.setString(4, x.apid_verb);
            ps.setString(5, x.apid_type);
            ps.setString(6, x.apid_path);
            ps.setString(7, x.apid_key);
            ps.setString(8, x.apid_value);
            ps.setString(9, x.apid_source);
            ps.setString(10, x.apid_destination);
            ps.setString(11, x.apid_enabled);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addAPITransaction(ArrayList<api_det> apid, api_mstr api) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addAPIMstr(api, bscon, ps, res);  
            for (api_det z : apid) {
                _addAPIDet(z, bscon, ps, res);
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
        
    public static String[] addAS2Transaction(as2_mstr as2) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addAS2Mstr(as2, bscon, ps, res); 
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
    
    
    public static String[] updateAPIMaint(api_mstr x) {
        String[] m = new String[2];
        String sql = "update api_mstr set api_desc = ?, api_version = ?, api_url = ?, api_port = ?, " +
                " api_path = ?, api_user = ?, api_pass = ?, api_key = ?, api_protocol = ?, api_class = ?,  " +
                " api_encrypted = ?, api_signed = ?, api_cert = ? " +
                "  where api_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.api_desc);
        ps.setString(2, x.api_version);
        ps.setString(3, x.api_url);
        ps.setString(4, x.api_port);
        ps.setString(5, x.api_path);
        ps.setString(6, x.api_user);
        ps.setString(7, x.api_pass);
        ps.setString(8, x.api_key);
        ps.setString(9, x.api_protocol);
        ps.setString(10, x.api_class);
        ps.setString(11, x.api_encrypted);
        ps.setString(12, x.api_signed);
        ps.setString(13, x.api_cert);
        ps.setString(14, x.api_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] updateAS2Maint(as2_mstr x) {
        String[] m = new String[2];
        String sql = "update as2_mstr set as2_desc = ?, as2_version = ?, as2_url = ?, as2_port = ?, " +
                " as2_path = ?, as2_user = ?, as2_pass = ?, as2_key = ?, as2_protocol = ?, as2_class = ?,  " +
                " as2_indir = ?, as2_outdir = ?, " +
                " as2_encrypted = ?, as2_signed = ?, as2_enccert = ?, " +
                " as2_forceencrypted = ?, as2_forcesigned = ?, as2_signcert = ?, " +
                " as2_encalgo = ?, as2_signalgo = ?, as2_micalgo = ? " +
                "  where as2_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.as2_desc);
        ps.setString(2, x.as2_version);
        ps.setString(3, x.as2_url);
        ps.setString(4, x.as2_port);
        ps.setString(5, x.as2_path);
        ps.setString(6, x.as2_user);
        ps.setString(7, x.as2_pass);
        ps.setString(8, x.as2_key);
        ps.setString(9, x.as2_protocol);
        ps.setString(10, x.as2_class);
        ps.setString(11, x.as2_indir);
        ps.setString(12, x.as2_outdir);
        ps.setString(13, x.as2_encrypted);
        ps.setString(14, x.as2_signed);
        ps.setString(15, x.as2_enccert);
        ps.setString(16, x.as2_forceencrypted);
        ps.setString(17, x.as2_forcesigned);
        ps.setString(18, x.as2_signcert);
        ps.setString(19, x.as2_encalgo);
        ps.setString(20, x.as2_signalgo);
        ps.setString(21, x.as2_micalgo);
        ps.setString(22, x.as2_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    private static int _updateAPIMstr(api_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update api_mstr set api_desc = ?, api_version = ?, api_url = ?, api_port = ?, " +
                " api_path = ?, api_user = ?, api_pass = ?, api_key = ?, api_protocol = ?, api_class = ?,  " +
                " api_encrypted = ?, api_signed = ?, api_cert = ? " +
                "  where api_id = ? ";
	ps = con.prepareStatement(sql) ;
        ps.setString(1, x.api_desc);
        ps.setString(2, x.api_version);
        ps.setString(3, x.api_url);
        ps.setString(4, x.api_port);
        ps.setString(5, x.api_path);
        ps.setString(6, x.api_user);
        ps.setString(7, x.api_pass);
        ps.setString(8, x.api_key);
        ps.setString(9, x.api_protocol);
        ps.setString(10, x.api_class);
        ps.setString(11, x.api_encrypted);
        ps.setString(12, x.api_signed);
         ps.setString(13, x.api_cert);
        ps.setString(14, x.api_id);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateAS2Mstr(as2_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update as2_mstr set as2_desc = ?, as2_version = ?, as2_url = ?, as2_port = ?, " +
                " as2_path = ?, as2_user = ?, as2_pass = ?, as2_key = ?, as2_protocol = ?, as2_class = ?,  " +
                " as2_indir = ?, as2_outdir = ?, " +
                " as2_encrypted = ?, as2_signed = ?, as2_enccert = ?, " +
                " as2_forceencrypted = ?, as2_forcesigned = ?, as2_signcert = ?, " +
                " as2_encalgo = ?, as2_signalgo = ?, as2_micalgo = ? " +
                "  where as2_id = ? ";
	ps = con.prepareStatement(sql) ;
        ps.setString(1, x.as2_desc);
        ps.setString(2, x.as2_version);
        ps.setString(3, x.as2_url);
        ps.setString(4, x.as2_port);
        ps.setString(5, x.as2_path);
        ps.setString(6, x.as2_user);
        ps.setString(7, x.as2_pass);
        ps.setString(8, x.as2_key);
        ps.setString(9, x.as2_protocol);
        ps.setString(10, x.as2_class);
        ps.setString(11, x.as2_indir);
        ps.setString(12, x.as2_outdir);
        ps.setString(13, x.as2_encrypted);
        ps.setString(14, x.as2_signed);
        ps.setString(15, x.as2_enccert);
        ps.setString(16, x.as2_forceencrypted);
        ps.setString(17, x.as2_forcesigned);
        ps.setString(18, x.as2_signcert);
        ps.setString(19, x.as2_encalgo);
        ps.setString(20, x.as2_signalgo);
        ps.setString(21, x.as2_micalgo);
        ps.setString(22, x.as2_id);
            rows = ps.executeUpdate();
        return rows;
    }
    
    
    private static int _updateAPIdet(api_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from api_det where apid_id = ? and apid_method = ?";
        String sqlUpdate = "update api_det set apid_seq = ?, " +
                           " apid_verb = ?, apid_type = ?, apid_path = ?,  " +
                           " apid_key = ?, apid_value = ?, apid_source = ?, apid_destination = ?, apid_enabled = ? " +
                 " where apid_id = ? and apid_method = ? ; ";
        String sqlInsert = "insert into api_det (apid_id, apid_method, apid_seq,  " +
                             " apid_verb, apid_type, apid_path, apid_key, " +
                            " apid_value, apid_source, apid_destination, apid_enabled ) " +
                           " values (?,?,?,?,?,?,?,?,?,?,?); " ;
        ps = con.prepareStatement(sqlSelect);
        ps.setString(1, x.apid_id);
        ps.setString(2, x.apid_method);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.apid_id);
            ps.setString(2, x.apid_method);
            ps.setString(3, x.apid_seq);
            ps.setString(4, x.apid_verb);
            ps.setString(5, x.apid_type);
            ps.setString(6, x.apid_path);
            ps.setString(7, x.apid_key);
            ps.setString(8, x.apid_value);
            ps.setString(9, x.apid_source);
            ps.setString(10, x.apid_destination);
            ps.setString(11, x.apid_enabled); 
            // ps.setString(9, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        } else {    // update
         
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(1, x.apid_seq);
            ps.setString(2, x.apid_verb);
            ps.setString(3, x.apid_type);
            ps.setString(4, x.apid_path);
            ps.setString(5, x.apid_key);
            ps.setString(6, x.apid_value);
            ps.setString(7, x.apid_source);
            ps.setString(8, x.apid_destination);
            ps.setString(9, x.apid_enabled);  
            ps.setString(10, x.apid_id);
            ps.setString(11, x.apid_method);
            // ps.setString(7, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
        
    public static String[] updateAPITransaction(String x, ArrayList<String> lines, ArrayList<api_det> apid, api_mstr api) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            for (String line : lines) {
               _deleteAPILines(x, line, bscon);  // discard unwanted lines
             }
            for (api_det z : apid) {
                _updateAPIdet(z, bscon, ps, res);
            }
             _updateAPIMstr(api, bscon, ps);  // update so_mstr
            bscon.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
    
    public static String[] updateAS2Transaction(String x, as2_mstr as2) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
             _updateAS2Mstr(as2, bscon, ps);  // update so_mstr
            bscon.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
    
    
    
    public static String[] deleteAPIMstr(api_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from api_mstr where api_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.api_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static String[] deleteAS2Mstr(as2_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from as2_mstr where as2_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.as2_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    private static void _deleteAPILines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from api_det where apid_id = ? and apid_method = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        ps.close();
    }
    
    public static api_mstr getAPIMstr(String[] x) {
        api_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from api_mstr where api_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new api_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new api_mstr(m, res.getString("api_id"), 
                            res.getString("api_desc"),
                            res.getString("api_version"),
                            res.getString("api_url"),
                            res.getString("api_port"),
                            res.getString("api_path"),
                            res.getString("api_user"),
                            res.getString("api_pass"),
                            res.getString("api_key"),
                            res.getString("api_protocol"),
                            res.getString("api_class"),
                            res.getString("api_encrypted"),
                            res.getString("api_signed"),
                            res.getString("api_cert")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new api_mstr(m);
        }
        return r;
    }
    
    public static as2_mstr getAS2Mstr(String[] x) {
        as2_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from as2_mstr where as2_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new as2_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new as2_mstr(m, res.getString("as2_id"), 
                            res.getString("as2_desc"),
                            res.getString("as2_version"),
                            res.getString("as2_url"),
                            res.getString("as2_port"),
                            res.getString("as2_path"),
                            res.getString("as2_user"),
                            res.getString("as2_pass"),
                            res.getString("as2_key"),
                            res.getString("as2_protocol"),
                            res.getString("as2_class"),
                            res.getString("as2_indir"),
                            res.getString("as2_outdir"),
                            res.getString("as2_encrypted"),
                            res.getString("as2_signed"),
                            res.getString("as2_enccert"),
                            res.getString("as2_forceencrypted"),
                            res.getString("as2_forcesigned"),
                            res.getString("as2_signcert"),
                            res.getString("as2_encalgo"),
                            res.getString("as2_signalgo"),
                            res.getString("as2_micalgo")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new as2_mstr(m);
        }
        return r;
    }
    
    
    public static ArrayList<api_det> getAPIDet(String code) {
        api_det r = null;
        String[] m = new String[2];
        ArrayList<api_det> list = new ArrayList<api_det>();
        String sql = "select * from api_det where apid_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new api_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new api_det(m, res.getString("apid_id"), 
                        res.getString("apid_method"), 
                        res.getString("apid_seq"), 
                        res.getString("apid_verb"), 
                        res.getString("apid_type"),
                        res.getString("apid_path"),
                        res.getString("apid_key"),
                        res.getString("apid_value"),
                        res.getString("apid_source"),
                        res.getString("apid_destination"),        
                        res.getString("apid_enabled"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new api_det(m);
               list.add(r);
        }
        return list;
    }
    
    public static api_det getAPIDet(String id, String method) { 
        api_det r = null;
        String[] m = new String[2];
        String sql = "select * from api_det where apid_id = ? and apid_method = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
        ps.setString(2, method);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new api_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                      r = new api_det(m, res.getString("apid_id"), 
                        res.getString("apid_method"), 
                        res.getString("apid_seq"), 
                        res.getString("apid_verb"), 
                        res.getString("apid_type"),
                        res.getString("apid_path"),
                        res.getString("apid_key"),
                        res.getString("apid_value"),
                        res.getString("apid_source"),
                        res.getString("apid_destination"),
                        res.getString("apid_enabled"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new api_det(m);
        }
        return r;
    }
     
    public static ArrayList<String> getAPIMethodsList(String nbr) {
        ArrayList<String> lines = new ArrayList<String>();
        try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        try{
            Statement st = con.createStatement();
            ResultSet res = null;

           res = st.executeQuery("SELECT apid_method from api_det " +
                   " where apid_id = " + "'" + nbr + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("apid_method"));
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
        return lines;
    }
    
    
    //misc
    public static boolean isValidAS2id(String id) {
        boolean x = false;
        String sql = "select * from as2_mstr where as2_id = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
             try (ResultSet res = ps.executeQuery();) {
                if (res.isBeforeFirst()) {
                x = true;
                } 
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return x;
    }
    
    
    public static ArrayList getMapMstrList() {
       ArrayList mylist = new ArrayList();
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
                res = st.executeQuery("select map_id from map_mstr; ");
               while (res.next()) {
                   mylist.add(res.getString("map_id"));
                }
           }
            catch (SQLException s) {
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
        return mylist;
        
    }
    
    public static ArrayList getMapStructList() {
       ArrayList mylist = new ArrayList();
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
                res = st.executeQuery("select mps_id from map_struct order by mps_id ; ");
               while (res.next()) {
                   mylist.add(res.getString("mps_id"));
                }
           }
            catch (SQLException s) {
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
        return mylist;
        
    }
    
    public static boolean isAPIMethodUnique(String api, String method) {
        boolean x = false;
         String sql = "select * from api_det where apid_id = ? and apid_method = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, api);
        ps.setString(2, method);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                x = true;
                } 
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return x;
    }
    
    public static String[] getAS2Info(String id) {
        String[] info = new String[]{"","","","","","","","","","","", "", "", "", "", "", "", "", "", "", ""};
        String sql = "select as2_id, as2_url, as2_port, as2_path, as2_user, edic_as2id, edic_as2url, " +
                " as2_encrypted, as2_signed, as2_enccert, as2_forceencrypted, as2_forcesigned, as2_signcert, as2_protocol, as2_indir, as2_outdir, " +
                " edic_signkey, edic_enckey, as2_encalgo, as2_signalgo, as2_micalgo " +
                " from as2_mstr " +
                " inner join edi_ctrl where as2_id = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               info[0] = res.getString("as2_id");
               info[1] = res.getString("as2_url");
               info[2] = res.getString("as2_port");
               info[3] = res.getString("as2_path");
               info[4] = res.getString("as2_user");
               info[5] = res.getString("edic_as2id");
               info[6] = res.getString("edic_as2url");
               info[7] = res.getString("edic_signkey");
               info[8] = res.getString("edic_enckey");
               info[9] = res.getString("as2_encrypted");
               info[10] = res.getString("as2_signed");
               info[11] = res.getString("as2_enccert");
               info[12] = res.getString("as2_forceencrypted");
               info[13] = res.getString("as2_forcesigned");
               info[14] = res.getString("as2_signcert");
               info[15] = res.getString("as2_protocol");
               info[16] = res.getString("as2_indir");
               info[17] = res.getString("as2_outdir");
               info[18] = res.getString("as2_encalgo");
               info[19] = res.getString("as2_signalgo");
               info[20] = res.getString("as2_micalgo");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return info;
    }
    
    public static String[] getAS2InfoByIDs(String sender, String receiver) {
        String[] info = null;
        String sql = "select as2_id, as2_url, as2_port, as2_path, as2_user, edic_as2id, edic_as2url, " +
                " as2_encrypted, as2_signed, as2_enccert, as2_forceencrypted, as2_forcesigned, as2_signcert, as2_protocol, as2_indir, as2_outdir, " +
                " edic_signkey, edic_enckey, as2_encalgo, as2_signalgo, as2_micalgo " +
                " from as2_mstr " +
                " inner join edi_ctrl where as2_user = ? and edic_as2id = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, sender);
        ps.setString(2, receiver);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               info = new String[21];     
               info[0] = res.getString("as2_id");
               info[1] = res.getString("as2_url");
               info[2] = res.getString("as2_port");
               info[3] = res.getString("as2_path");
               info[4] = res.getString("as2_user");
               info[5] = res.getString("edic_as2id");
               info[6] = res.getString("edic_as2url");
               info[7] = res.getString("edic_signkey");
               info[8] = res.getString("edic_enckey");
               info[9] = res.getString("as2_encrypted");
               info[10] = res.getString("as2_signed");
               info[11] = res.getString("as2_enccert");
               info[12] = res.getString("as2_forceencrypted");
               info[13] = res.getString("as2_forcesigned");
               info[14] = res.getString("as2_signcert");
               info[15] = res.getString("as2_protocol");
               info[16] = res.getString("as2_indir");
               info[17] = res.getString("as2_outdir");
               info[18] = res.getString("as2_encalgo");
               info[19] = res.getString("as2_signalgo");
               info[20] = res.getString("as2_micalgo");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return info;
    }
    
    
    public static String getKeyStorePass(String id) {
        String x = "";
        String sql = "select pks_storepass from pks_mstr where pks_type = 'store' and pks_id = ?";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               x = res.getString("pks_storepass");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return x;
    }
    
    public static String[] getKeyStoreByUser(String userid) {
        String[] x = new String[]{"","","","",""};
        String sql = "select p.pks_storeuser as storeuser, p.pks_file as storefile, p.pks_storepass as storepass, u.pks_user as user, u.pks_pass as pass from pks_mstr p inner join pks_mstr u on u.pks_parent = p.pks_id where u.pks_id = ?";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, userid);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               x[0] = res.getString("storefile");
               x[1] = res.getString("storeuser");
               x[2] = res.getString("storepass");
               x[3] = res.getString("user");
               x[4] = res.getString("pass");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return x;
    }
        
    public static String getKeyUserPass(String key, String user) {
        String x = "";
        String sql = "select pks_pass from pks_mstr where pks_id = ? and pks_user = ?";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, key);
        ps.setString(2, user);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               x = res.getString("pks_pass");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return x;
    }
    
    public static ArrayList<String> getKeyAllByType(String keytype) {
        ArrayList x = new ArrayList();
        String sql = "select pks_id from pks_mstr where pks_type = ?";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, keytype);
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
    
    public static ArrayList<String> getAllPKSKeys() {
        ArrayList x = new ArrayList();
        String sql = "select pks_id from pks_mstr;";
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
    
    
    public record edi_xref(String[] m, String exr_tpid, String exr_tpaddr, String exr_ovaddr,
        String exr_gsid, String exr_type ) {
        public edi_xref(String[] m) {
            this(m, "", "", "", "", "");
        }
    }
    
    public record map_mstr(String[] m, String map_id, String map_desc, String map_version,
        String map_ifs, String map_ofs, String map_indoctype, String map_infiletype ,
        String map_outdoctype, String map_outfiletype, String map_source, String map_package, String map_internal ) {
        public map_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record map_struct(String[] m, String mps_id, String mps_desc, String mps_version, String mps_doctype, String mps_filetype) {
        public map_struct(String[] m) {
            this(m, "", "", "", "", "");
        }
    }
    
    public record as2_mstr(String[] m, String as2_id, String as2_desc, String as2_version,
        String as2_url, String as2_port, String as2_path, String as2_user ,
        String as2_pass, String as2_key, String as2_protocol, String as2_class,
        String as2_indir, String as2_outdir, String as2_encrypted, String as2_signed, String as2_enccert,
        String as2_forceencrypted, String as2_forcesigned, String as2_signcert,
        String as2_encalgo, String as2_signalgo, String as2_micalgo) {
        public as2_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", 
                    "", "", "", "", "", "", "", "", "", "",
                    "", "");
        }
    }
    
    public record api_mstr(String[] m, String api_id, String api_desc, String api_version,
        String api_url, String api_port, String api_path, String api_user ,
        String api_pass, String api_key, String api_protocol, String api_class,
        String api_encrypted, String api_signed, String api_cert) {
        public api_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", 
                    "", "", "", "");
        }
    }
    
    public record api_det(String[] m, String apid_id, String apid_method, String apid_seq,
        String apid_verb, String apid_type, String apid_path, String apid_key,
         String apid_value, String apid_source, String apid_destination, String apid_enabled ) {
        public api_det(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", "");
        }
    }
}
