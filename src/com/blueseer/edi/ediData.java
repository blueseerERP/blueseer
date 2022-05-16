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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
                + " map_outdoctype, map_outfiletype )  " +
                " values (?,?,?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
            psi.setString(6, x.map_outfiletype);
            psi.setString(7, x.map_ofs);
            
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
                " map_ofs = ?, map_outdoctype = ?, map_outfiletype = ? where map_id = ? ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.map_desc);
        ps.setString(2, x.map_version);
        ps.setString(3, x.map_ifs);
        ps.setString(4, x.map_ofs);
        ps.setString(5, x.map_outdoctype);
        ps.setString(6, x.map_outfiletype);
        ps.setString(7, x.map_id);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
                            res.getString("map_outdoctype"),
                            res.getString("map_outfiletype")
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
        String sqlInsert = "insert into map_struct (mps_id, mps_desc, mps_version "
                + "  )  " +
                " values (?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.mps_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.mps_id);
            psi.setString(2, x.mps_desc);
            psi.setString(3, x.mps_version);
            
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
        String sql = "update map_struct set mps_desc = ?, mps_version = ? " +
                "  where mps_id = ? ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.mps_desc);
        ps.setString(2, x.mps_version);
        ps.setString(3, x.mps_id);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
                            res.getString("mps_version")
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
    
    
    public record edi_xref(String[] m, String exr_tpid, String exr_tpaddr, String exr_ovaddr,
        String exr_gsid, String exr_type ) {
        public edi_xref(String[] m) {
            this(m, "", "", "", "", "");
        }
    }
    
    public record map_mstr(String[] m, String map_id, String map_desc, String map_version,
        String map_ifs, String map_ofs, String map_outdoctype, String map_outfiletype ) {
        public map_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "");
        }
    }
    
    public record map_struct(String[] m, String mps_id, String mps_desc, String mps_version) {
        public map_struct(String[] m) {
            this(m, "", "", "");
        }
    }
    
}
