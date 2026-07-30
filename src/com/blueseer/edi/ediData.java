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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData.ftp_mstr;
import static com.blueseer.adm.admData.getFTPMstr;
import static com.blueseer.adm.admData.getPksMstr;
import static com.blueseer.adm.admData.isValidKeyID;
import com.blueseer.adm.admData.pks_mstr;
import static com.blueseer.edi.EDI.packageEnvelopes;
import static com.blueseer.edi.EDI.runEDIForSite;
import static com.blueseer.edi.EDILoad.runTranslationSingleFile;
import static com.blueseer.edi.apiUtils.runAPICall;
import static com.blueseer.edi.wfUtils.emailDir;
import static com.blueseer.edi.wfUtils.filterDir;
import static com.blueseer.edi.wfUtils.trafficDir;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ConvertIntToYesNo;
import static com.blueseer.utl.BlueSeerUtils.ConvertStringToBool;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.bsret;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListString;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToBoolean;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.parseFileName;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.blueseer.utl.EDData;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getSMTPCredentials;
import static com.blueseer.utl.OVData.getSysMetaValue;
import static com.blueseer.utl.OVData.isSMTPServer;
import static com.blueseer.utl.OVData.isSMTPServerBool;
import static com.blueseer.utl.OVData.sendEmail;
import static com.blueseer.utl.OVData.sendEmailwSession;
import static com.blueseer.utl.OVData.setEmailSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.mail.smime.SMIMEException;
import org.json.JSONArray;

/**
 *
 * @author terryva
 */
public class ediData {
    
    public static String[] addEDIXref(edi_xref x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addEDIXref"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  edi_xref where exr_bsgs = ? and exr_tpaddr = ? " +
                " and exr_bsaddr = ? and exr_tpgs = ? and exr_type = ?";
        String sqlInsert = "insert into edi_xref (exr_bsgs, exr_tpaddr, exr_bsaddr, exr_tpgs, exr_type, exr_site) " 
                        + " values (?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.exr_bsgs);
             ps.setString(2, x.exr_tpaddr);
             ps.setString(3, x.exr_bsaddr);
             ps.setString(4, x.exr_tpgs);
             ps.setString(5, x.exr_type);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.exr_bsgs);
             psi.setString(2, x.exr_tpaddr);
             psi.setString(3, x.exr_bsaddr);
             psi.setString(4, x.exr_tpgs);
             psi.setString(5, x.exr_type);
             psi.setString(6, x.exr_site);
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
        String sqlSelect = "SELECT * FROM  edi_xref where exr_bsgs = ? and exr_tpaddr = ? " +
                " and exr_bsaddr = ? and exr_tpgs = ? and exr_type = ?";
        String sqlInsert = "insert into edi_xref (exr_bsgs, exr_tpaddr, exr_bsaddr, exr_tpgs, exr_type, exr_site) " 
                        + " values (?,?,?,?,?,?); "; 
        String sqlUpdate = "update edi_xref set exr_tpaddr = ?, exr_bsaddr = ? " +
                           " where exr_bsgs = ? and exr_tpgs = ? and exr_type = ? ; "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.exr_bsgs);
             ps.setString(2, x.exr_tpaddr);
             ps.setString(3, x.exr_bsaddr);
             ps.setString(4, x.exr_tpgs);
             ps.setString(5, x.exr_type); 
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.exr_bsgs);
             psi.setString(2, x.exr_tpaddr);
             psi.setString(3, x.exr_bsaddr);
             psi.setString(4, x.exr_tpgs);
             psi.setString(5, x.exr_type); 
             psi.setString(6, x.exr_site);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
             psu.setString(1, x.exr_tpaddr);
             psu.setString(2, x.exr_bsaddr);
             psu.setString(3, x.exr_bsgs);
             psu.setString(4, x.exr_tpgs);
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateEDIXref"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlUpdate = "update edi_xref set exr_tpaddr = ?, exr_bsaddr = ? " +
                           " where exr_bsgs = ? and exr_tpgs = ? and exr_type = ? ; "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
             ps.setString(1, x.exr_tpaddr);
             ps.setString(2, x.exr_bsaddr);
             ps.setString(3, x.exr_bsgs);
             ps.setString(4, x.exr_tpgs);
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
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteEDIXref"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from edi_xref where exr_bsgs = ? and exr_tpaddr = ? " +
                " and exr_bsaddr = ? and exr_tpgs = ? and exr_type = ?";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
         ps.setString(1, x.exr_bsgs);
         ps.setString(2, x.exr_tpaddr);
         ps.setString(3, x.exr_bsaddr);
         ps.setString(4, x.exr_tpgs);
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
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getEDIXref"});
            list.add(new String[]{"param1",x[0]});
            list.add(new String[]{"param2",x[1]});
            list.add(new String[]{"param3",x[2]});
            list.add(new String[]{"param4",x[3]});
            list.add(new String[]{"param5",x[4]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, edi_xref.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new edi_xref(m);
                return r;
            }
        }
        
        String sqlSelect = "SELECT * FROM  edi_xref where exr_tpgs = ? and exr_bsgs = ? " +
                " and exr_type = ? and exr_tpaddr = ? and exr_bsaddr = ?";
        
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
                            res.getString("exr_bsgs"), 
                            res.getString("exr_tpaddr"),
                            res.getString("exr_bsaddr"),
                            res.getString("exr_tpgs"),
                            res.getString("exr_type"),
                            res.getString("exr_site")
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addMapMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "select * from map_mstr where map_id = ?";
        String sqlInsert = "insert into map_mstr (map_id, map_desc, map_version, map_ifs, map_ofs, "
                + " map_indoctype, map_infiletype, map_outdoctype, map_outfiletype, map_source, map_package, map_internal, map_site )  " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
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
            psi.setString(13, x.map_site);
            
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateMapMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update map_mstr set map_desc = ?, map_version = ?, map_ifs = ?, " +
                " map_ofs = ?, map_indoctype = ?, map_infiletype = ?, map_outdoctype = ?, map_outfiletype = ?, " +
                " map_source = ?, map_package = ?, map_internal = ?, map_site = ? " +
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
        ps.setString(12, x.map_site);
        ps.setString(13, x.map_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteMapMstr(map_mstr x) { 
       
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteMapMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getMapMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, map_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new map_mstr(m);
                return r;
            }
        }
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
                            res.getString("map_internal"),
                            res.getString("map_site")
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
    
    public static String[] addDFStructureTransaction(ArrayList<dfs_det> dfsd, dfs_mstr dfs) {
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addDFStructureTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(dfsd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(dfs);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addDFSMstr(dfs, bscon, ps, res);  
            for (dfs_det z : dfsd) {
                _addDFSDet(z, bscon, ps, res);
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
    
    private static int _addDFSMstr(dfs_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from dfs_mstr where dfs_id = ?";
        String sqlInsert = "insert into dfs_mstr (dfs_id, dfs_desc, dfs_version, dfs_doctype, dfs_filetype, dfs_delimiter, " +
                " dfs_misc, dfs_suppressemptytag, dfs_suppressroot, dfs_wraparray "
                + "  )  " +
                " values (?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.dfs_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.dfs_id);
            ps.setString(2, x.dfs_desc);
            ps.setString(3, x.dfs_version);
            ps.setString(4, x.dfs_doctype);
            ps.setString(5, x.dfs_filetype);
            ps.setString(6, x.dfs_delimiter);
            ps.setString(7, x.dfs_misc);
            ps.setString(8, x.dfs_suppressemptytag);
            ps.setString(9, x.dfs_suppressroot);
            ps.setString(10, x.dfs_wraparray);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addDFSDet(dfs_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from dfs_det where dfsd_id = ? and dfsd_segment = ? and dfsd_parent = ? and dfsd_field = ?";
        String sqlInsert = "insert into dfs_det (dfsd_id, dfsd_segment, dfsd_parent, dfsd_loopcount,  " +
                             " dfsd_isgroup, dfsd_islandmark, dfsd_field, dfsd_desc, " +
                            " dfsd_min, dfsd_max, dfsd_align, dfsd_status, dfsd_type ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.dfsd_id);
          ps.setString(2, x.dfsd_segment);
          ps.setString(3, x.dfsd_parent);
          ps.setString(4, x.dfsd_field);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.dfsd_id);
            ps.setString(2, x.dfsd_segment);
            ps.setString(3, x.dfsd_parent);
            ps.setString(4, x.dfsd_loopcount); 
            ps.setString(5, x.dfsd_isgroup);
            ps.setString(6, x.dfsd_islandmark);
            ps.setString(7, x.dfsd_field);
            ps.setString(8, x.dfsd_desc);
            ps.setString(9, x.dfsd_min);
            ps.setString(10, x.dfsd_max);
            ps.setString(11, x.dfsd_align);
            ps.setString(12, x.dfsd_status);
            ps.setString(13, x.dfsd_type);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] updateDFStructureTransaction(String x, ArrayList<dfs_det> dfsd, dfs_mstr dfs) {
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateDFStructureTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = x;
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(dfsd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(dfs);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
             _deleteDFSLines(x, bscon);  // discard all lines
            for (dfs_det z : dfsd) {
                _addDFSDet(z, bscon, ps, res); 
            }
             _updateDFSMstr(dfs, bscon, ps);  // update so_mstr
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
    
    private static int _updateDFSMstr(dfs_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update dfs_mstr set dfs_desc = ?, dfs_version = ?, dfs_doctype = ?, dfs_filetype = ?, " +
                " dfs_delimiter = ?, dfs_misc = ?, dfs_suppressemptytag = ?, dfs_suppressroot = ?, dfs_wraparray = ? " +
                "  where dfs_id = ? ";
	ps = con.prepareStatement(sql) ;
            ps.setString(1, x.dfs_desc);
            ps.setString(2, x.dfs_version);
            ps.setString(3, x.dfs_doctype);
            ps.setString(4, x.dfs_filetype);
            ps.setString(5, x.dfs_delimiter);
            ps.setString(6, x.dfs_misc); 
            ps.setString(7, x.dfs_suppressemptytag);
            ps.setString(8, x.dfs_suppressroot);
            ps.setString(9, x.dfs_wraparray);
            ps.setString(10, x.dfs_id);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateDFSdet(dfs_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from dfs_det where dfsd_id = ? and dfsd_segment = ? and dfsd_parent = ? and dfsd_field = ?";
        String sqlUpdate = "update dfs_det set dfsd_loopcount = ?, " +
                           " dfsd_isgroup = ?, dfsd_islandmark = ?, dfsd_field = ?,  " +
                           " dfsd_desc = ?, dfsd_min = ?, dfsd_max = ?, dfsd_align = ?, dfsd_status = ?, dfsd_type = ? " +
                 " where dfsd_id = ? and dfsd_segment = ? and dfsd_parent = ? and dfsd_field = ? ; ";
        String sqlInsert = "insert into dfs_det (dfsd_id, dfsd_segment, dfsd_parent, dfsd_loopcount,  " +
                             " dfsd_isgroup, dfsd_islandmark, dfsd_field, dfsd_desc, " +
                            " dfsd_min, dfsd_max, dfsd_align, dfsd_status, dfsd_type ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect);
        ps.setString(1, x.dfsd_id);
        ps.setString(2, x.dfsd_segment);
        ps.setString(3, x.dfsd_parent);
        ps.setString(4, x.dfsd_field);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.dfsd_id);
            ps.setString(2, x.dfsd_segment);
            ps.setString(3, x.dfsd_parent);
            ps.setString(4, x.dfsd_loopcount); 
            ps.setString(5, x.dfsd_isgroup);
            ps.setString(6, x.dfsd_islandmark);
            ps.setString(7, x.dfsd_field);
            ps.setString(8, x.dfsd_desc);
            ps.setString(9, x.dfsd_min);
            ps.setString(10, x.dfsd_max);
            ps.setString(11, x.dfsd_align);
            ps.setString(12, x.dfsd_status);
            ps.setString(13, x.dfsd_type);
            // ps.setString(9, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        } else {    // update
         
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(1, x.dfsd_loopcount); 
            ps.setString(2, x.dfsd_isgroup);
            ps.setString(3, x.dfsd_islandmark);
            ps.setString(4, x.dfsd_desc);
            ps.setString(5, x.dfsd_min);
            ps.setString(6, x.dfsd_max);
            ps.setString(7, x.dfsd_align);
            ps.setString(8, x.dfsd_status);
            ps.setString(9, x.dfsd_type);
            ps.setString(10, x.dfsd_id);
            ps.setString(12, x.dfsd_segment);
            ps.setString(13, x.dfsd_parent);
            ps.setString(14, x.dfsd_field);
            // ps.setString(7, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    private static void _deleteDFSLines(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from dfs_det where dfsd_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    public static ArrayList<dfs_det> getDFSDet(String code) {
        dfs_det r = null;
        String[] m = new String[2];
        ArrayList<dfs_det> list = new ArrayList<dfs_det>();
        String sql = "select * from dfs_det where dfsd_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new dfs_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new dfs_det(m, res.getString("dfsd_id"), 
                        res.getString("dfsd_segment"), 
                        res.getString("dfsd_parent"), 
                        res.getString("dfsd_loopcount"), 
                        res.getString("dfsd_isgroup"),
                        res.getString("dfsd_islandmark"),
                        res.getString("dfsd_field"),
                        res.getString("dfsd_desc"),
                        res.getString("dfsd_min"),
                        res.getString("dfsd_max"),        
                        res.getString("dfsd_align"),
                        res.getString("dfsd_status"),
                        res.getString("dfsd_type"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new dfs_det(m);
               list.add(r);
        }
        return list;
    }
    
    public static DFSSet getEDIDFSSet(String[] x ) {
        DFSSet r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIDFSSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, DFSSet.class); 
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
            dfs_mstr dfs = _getDFSMstr(x, bscon, ps, res);
            ArrayList<dfs_det> dfsd = _getDFSDet(x, bscon, ps, res);
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new DFSSet(m, dfs, dfsd);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new DFSSet(m);
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
    
    private static dfs_mstr _getDFSMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        dfs_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from dfs_mstr where dfs_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new dfs_mstr(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new dfs_mstr(m, res.getString("dfs_id"), 
                            res.getString("dfs_desc"),
                            res.getString("dfs_version"),
                            res.getString("dfs_doctype"),
                            res.getString("dfs_filetype"),
                            res.getString("dfs_delimiter"),
                            res.getString("dfs_misc"),
                            res.getString("dfs_suppressemptytag"),
                            res.getString("dfs_suppressroot"),
                            res.getString("dfs_wraparray")
                        );
                    }
            }
            return r;
    }
    
    private static ArrayList<dfs_det> _getDFSDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<dfs_det> list = new ArrayList<dfs_det>();
        dfs_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from dfs_det where dfsd_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new dfs_det(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new dfs_det(m, res.getString("dfsd_id"), 
                        res.getString("dfsd_segment"), 
                        res.getString("dfsd_parent"), 
                        res.getString("dfsd_loopcount"), 
                        res.getString("dfsd_isgroup"),
                        res.getString("dfsd_islandmark"),
                        res.getString("dfsd_field"),
                        res.getString("dfsd_desc"),
                        res.getString("dfsd_min"),
                        res.getString("dfsd_max"),        
                        res.getString("dfsd_align"),
                        res.getString("dfsd_status"),
                        res.getString("dfsd_type"));
                        list.add(r);
                    }
            }
            return list;
    }
    
    
    public static dfs_mstr getDFSMstr(String[] x) {
        dfs_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getDFSMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, dfs_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new dfs_mstr(m);
                return r;
            }
        }
        String sql = "select * from dfs_mstr where dfs_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new dfs_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new dfs_mstr(m, res.getString("dfs_id"), 
                            res.getString("dfs_desc"),
                            res.getString("dfs_version"),
                            res.getString("dfs_doctype"),
                            res.getString("dfs_filetype"),
                            res.getString("dfs_delimiter"),
                            res.getString("dfs_misc"),
                            res.getString("dfs_suppressemptytag"),
                            res.getString("dfs_suppressroot"),
                            res.getString("dfs_wraparray")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new dfs_mstr(m);
        }
        return r;
    }
    
    public static String[] addWkfTransaction(ArrayList<wkfd_meta> wkfdm, ArrayList<wkf_det> wkfd, wkf_mstr wkf) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addWkfTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(wkfdm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(wkfd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(wkf);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
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
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addWkfMstr(wkf, bscon, ps, res);  
            for (wkf_det z : wkfd) {
                _addWkfDet(z, bscon, ps, res);
            }
            for (wkfd_meta z : wkfdm) {
                _addWkfdMeta(z, bscon, ps, res);
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
        
    private static int _addWkfMstr(wkf_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from wkf_mstr where wkf_id = ?";
        String sqlInsert = "insert into wkf_mstr (wkf_id, wkf_desc, wkf_enabled, wkf_site "
                + "  )  " +
                " values (?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.wkf_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.wkf_id);
            ps.setString(2, x.wkf_desc);
            ps.setString(3, x.wkf_enabled);
            ps.setString(4, x.wkf_site);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addWkfDet(wkf_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from wkf_det where wkfd_id = ? and wkfd_action = ? and wkfd_line = ?;";
        String sqlInsert = "insert into wkf_det (wkfd_id, wkfd_action, wkfd_line )  " 
                        + " values (?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.wkfd_id);
          ps.setString(2, x.wkfd_action);
          ps.setString(3, x.wkfd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.wkfd_id);
            ps.setString(2, x.wkfd_action);
            ps.setString(3, x.wkfd_line);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addWkfdMeta(wkfd_meta x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from wkfd_meta where wkfdm_id = ? and wkfdm_line = ? and wkfdm_key = ?;";
        String sqlInsert = "insert into wkfd_meta (wkfdm_id, wkfdm_line, wkfdm_key, wkfdm_value )  " 
                        + " values (?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.wkfdm_id);
          ps.setString(2, x.wkfdm_line);
          ps.setString(3, x.wkfdm_key);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.wkfdm_id);
            ps.setString(2, x.wkfdm_line);
            ps.setString(3, x.wkfdm_key);
            ps.setString(4, x.wkfdm_value);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] updateWkfMstrTransaction(String x, ArrayList<wkfd_meta> wkfdm, ArrayList<wkf_det> wkfd, wkf_mstr wkf) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateWkfMstrTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = x;
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(wkfdm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(wkfd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(wkf);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
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
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
             _deleteWkfDetLines(x, bscon);  // discard all lines
             for (wkfd_meta z : wkfdm) {
                _addWkfdMeta(z, bscon, ps, res); 
            }
            for (wkf_det z : wkfd) {
                _addWkfDet(z, bscon, ps, res); 
            }
             _updateWkfMstr(wkf, bscon, ps);  // update so_mstr
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
    
    private static void _deleteWkfDetLines(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from wkf_det where wkfd_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from wkfd_meta where wkfdm_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
        
    private static int _updateWkfMstr(wkf_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update wkf_mstr set wkf_desc = ?, wkf_enabled = ?, wkf_site = ? " +
                "  where wkf_id = ? ";
	ps = con.prepareStatement(sql) ;
            ps.setString(1, x.wkf_desc);
            ps.setString(2, x.wkf_enabled);
            ps.setString(3, x.wkf_site);
            ps.setString(4, x.wkf_id);
            rows = ps.executeUpdate();
        return rows;
    }
        
    public static String[] deleteWkfMstr(wkf_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from wkfd_meta where wkfdm_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.wkf_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        sql = "delete from wkf_det where wkfd_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.wkf_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        sql = "delete from wkf_mstr where wkf_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.wkf_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteWkfMstr(String x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "deleteWkfMstr"});
            list.add(new String[]{"param1", x});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }  
       String[] m = new String[2];
        try {
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            PreparedStatement ps = null;
            
            try {
                String sql = "delete from wkfd_meta where wkfdm_id = ?; ";
                ps = con.prepareStatement(sql);
                ps.setString(1, x);
                ps.executeUpdate();
                
                sql = "delete from wkf_det where wkfd_id = ?; ";
                ps = con.prepareStatement(sql);
                ps.setString(1, x);
                ps.executeUpdate();
                
                sql = "delete from wkf_mstr where wkf_id = ?; ";
                ps = con.prepareStatement(sql);
                ps.setString(1, x);
                ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};   
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
            } finally {
                if (ps != null) {
                    ps.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
        }
        return m;
    }
    
    
    public static ArrayList<wkf_det> getWkfDet(String code) {
        wkf_det r = null;
        String[] m = new String[2];
        ArrayList<wkf_det> list = new ArrayList<wkf_det>();
        String sql = "select * from wkf_det where wkfd_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wkf_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkf_det(m, res.getString("wkfd_id"), 
                        res.getString("wkfd_action"), 
                        res.getString("wkfd_line"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wkf_det(m);
               list.add(r);
        }
        return list;
    }
    
    public static ArrayList<wkfd_meta> getWkfdMeta(String code) {
        wkfd_meta r = null;
        String[] m = new String[2];
        ArrayList<wkfd_meta> list = new ArrayList<wkfd_meta>();
        String sql = "select * from wkfd_meta where wkfdm_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wkfd_meta(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkfd_meta(m, res.getString("wkfdm_id"), 
                        res.getString("wkfdm_line"), 
                        res.getString("wkfdm_key"),
                        res.getString("wkfdm_value"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wkfd_meta(m);
               list.add(r);
        }
        return list;
    }
    
    public static ArrayList<wkfd_meta> getWkfdMeta(String code, String line) {
        wkfd_meta r = null;
        String[] m = new String[2];
        ArrayList<wkfd_meta> list = new ArrayList<wkfd_meta>();
        String sql = "select * from wkfd_meta where wkfdm_id = ? and wkfdm_line = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
        ps.setString(2, line);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wkfd_meta(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkfd_meta(m, res.getString("wkfdm_id"), 
                        res.getString("wkfdm_line"), 
                        res.getString("wkfdm_key"),
                        res.getString("wkfdm_value"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wkfd_meta(m);
               list.add(r);
        }
        return list;
    }
    
    public static WorkFlowSet getWorkFlowSet(String[] x ) {
        WorkFlowSet r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWorkFlowSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, WorkFlowSet.class); 
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
            wkf_mstr wkf = _getWkfMstr(x, bscon, ps, res);
            ArrayList<wkf_det> wkfd = _getWkfDet(x, bscon, ps, res);
            ArrayList<wkfd_meta> wkfdm = _getWkfDetMeta(x, bscon, ps, res);
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new WorkFlowSet(m, wkf, wkfd, wkfdm);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new WorkFlowSet(m);
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
    
    private static wkf_mstr _getWkfMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        wkf_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from wkf_mstr where wkf_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new wkf_mstr(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkf_mstr(m, res.getString("wkf_id"), 
                            res.getString("wkf_desc"),
                            res.getString("wkf_enabled"),
                            res.getString("wkf_site")
                        );
                }
            }
            return r;
    }
    
    private static ArrayList<wkf_det> _getWkfDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<wkf_det> list = new ArrayList<wkf_det>();
        wkf_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from wkf_det where wkfd_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new wkf_det(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkf_det(m, res.getString("wkfd_id"), 
                        res.getString("wkfd_action"), 
                        res.getString("wkfd_line"));
                        list.add(r);
                    }
            }
            return list;
    }
    
    private static ArrayList<wkfd_meta> _getWkfDetMeta(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<wkfd_meta> list = new ArrayList<wkfd_meta>();
        wkfd_meta r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from wkfd_meta where wkfdm_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new wkfd_meta(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkfd_meta(m, res.getString("wkfdm_id"), 
                        res.getString("wkfdm_line"), 
                        res.getString("wkfdm_key"),
                        res.getString("wkfdm_value"));
                        list.add(r);
                }
            }
            return list;
    }
    
    
    public static wkf_mstr getWkfMstr(String[] x) {
        wkf_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from wkf_mstr where wkf_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wkf_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkf_mstr(m, res.getString("wkf_id"), 
                            res.getString("wkf_desc"),
                            res.getString("wkf_enabled"),
                            res.getString("wkf_site")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wkf_mstr(m);
        }
        return r;
    }
    
    public static wkf_mstr getWkfMstr(String x) {
        wkf_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from wkf_mstr where wkf_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wkf_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkf_mstr(m, res.getString("wkf_id"), 
                            res.getString("wkf_desc"),
                            res.getString("wkf_enabled"),
                            res.getString("wkf_site")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wkf_mstr(m);
        }
        return r;
    }
    
    public static ArrayList<String> getWkfMstrList() {
        ArrayList<String> r = new ArrayList<String>();
        String[] m = new String[2];
        String sql = "select wkf_id from wkf_mstr order by wkf_id ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r.add(res.getString("wkf_id")); 
                }
                
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
        }
        return r;
    }
    
    public static String[] addEDIPartnerTransaction(ArrayList<edpd_partner> edpd, edp_partner edp) {
        String[] m = new String[2];
        Connection bscon = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addEDIPartnerTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(edpd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(edp);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addEDIPartner(edp, bscon, ps, res);  
            for (edpd_partner z : edpd) {
                _addEDIPartnerDet(z, bscon, ps, res);
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
    
    private static int _addEDIPartner(edp_partner x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from edp_partner where edp_id = ?";
        String sqlInsert = "insert into edp_partner (edp_id, edp_desc, edp_site, edp_type, " +
                " edp_defoutdir, edp_defindir, edp_outwkfl, edp_inwkfl, edp_outenabled, edp_inenabled  " 
                + "  )  " +
                " values (?,?,?,?,?,?,?,?,?,?); "; 
        
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.edp_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.edp_id);
            ps.setString(2, x.edp_desc);
            ps.setString(3, x.edp_site);
            ps.setString(4, x.edp_type);
            ps.setString(5, x.edp_defoutdir);
            ps.setString(6, x.edp_defindir);
            ps.setString(7, x.edp_outwkfl);
            ps.setString(8, x.edp_inwkfl);
            ps.setString(9, x.edp_outenabled);
            ps.setString(10, x.edp_inenabled);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addEDIPartnerDet(edpd_partner x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from edpd_partner where edpd_parent = ? and edpd_alias = ?;";
        String sqlInsert = "insert into edpd_partner (edpd_parent, edpd_alias, edpd_default )  " 
                        + " values (?,?,?); "; 
                
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.edpd_parent);
          ps.setString(2, x.edpd_alias);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.edpd_parent);
            ps.setString(2, x.edpd_alias);
            ps.setString(3, x.edpd_default);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] updateEDIPartnerTransaction(String x, ArrayList<edpd_partner> edpd, edp_partner edp) {
        String[] m = new String[2];
        Connection bscon = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateEDIPartnerTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(edpd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(edp);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
             _deleteEDIPartnerDetLines(x, bscon);  // discard all lines
            for (edpd_partner z : edpd) {
                _addEDIPartnerDet(z, bscon, ps, res); 
            }
             _updateEDIPartner(edp, bscon, ps);  // update so_mstr
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
    
    private static int _updateEDIPartner(edp_partner x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update edp_partner set edp_desc = ?, edp_site = ?, edp_type = ?, " +
                " edp_defoutdir = ?, edp_defindir = ?, edp_outwkfl = ?, edp_inwkfl = ?, " +
                " edp_outenabled = ?, edp_inenabled = ? " +
                "  where edp_id = ? ";
	ps = con.prepareStatement(sql) ;
            ps.setString(1, x.edp_desc);
            ps.setString(2, x.edp_site);
            ps.setString(3, x.edp_type);
            ps.setString(4, x.edp_defoutdir);
            ps.setString(5, x.edp_defindir);
            ps.setString(6, x.edp_outwkfl);
            ps.setString(7, x.edp_inwkfl);
            ps.setString(8, x.edp_outenabled);
            ps.setString(9, x.edp_inenabled);
            ps.setString(10, x.edp_id);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static void _deleteEDIPartnerDetLines(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from edpd_partner where edpd_parent = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    public static String[] deleteEDIPartner(String x) { 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "deleteEDIPartner"});
            list.add(new String[]{"param1", x});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        } 
        String[] m = new String[2];
        String sql = "delete from edp_partner where edp_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        sql = "delete from edpd_partner where edpd_parent = ?; ";
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
     
    public static EDIPartnerSet getEDIPartnerSet(String[] x ) {
        EDIPartnerSet r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIPartnerSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, EDIPartnerSet.class); 
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
            edp_partner edp = _getEDIPartner(x, bscon, ps, res);
            ArrayList<edpd_partner> edpd = _getEDIPartnerDet(x, bscon, ps, res);
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new EDIPartnerSet(m, edp, edpd);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new EDIPartnerSet(m);
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
    
    private static edp_partner _getEDIPartner(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        edp_partner r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from edp_partner where edp_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new edp_partner(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new edp_partner(m, res.getString("edp_id"), res.getString("edp_desc"), res.getString("edp_site"),
                res.getString("edp_type"), res.getString("edp_defoutdir"), res.getString("edp_defindir"), res.getString("edp_outwkfl"), res.getString("edp_inwkfl"),
                res.getString("edp_outenabled"), res.getString("edp_inenabled"));
                }
            }
            return r;
    }
    
    private static ArrayList<edpd_partner> _getEDIPartnerDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<edpd_partner> list = new ArrayList<edpd_partner>();
        edpd_partner r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from edpd_partner where edpd_parent = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new edpd_partner(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new edpd_partner(m, res.getString("edpd_parent"), res.getString("edpd_alias"), res.getString("edpd_default"));
                    list.add(r);
                    }
            }
            return list;
    }
    
    public static ArrayList<String[]> getEDIPartners(String edptype) {
        ArrayList<String[]> r = new ArrayList<String[]>();
        String[] m = new String[2];
        String sql = "select edp_id, edp_site, edp_type, edp_defoutdir, edp_defindir, edp_outwkfl, edp_inwkfl, edp_outenabled, edp_inenabled from edp_partner where edp_type = ? order by edp_id ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, edptype);
             try (ResultSet res = ps.executeQuery();) {
                
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r.add(new String[]{
                    res.getString("edp_id"),
                    res.getString("edp_site"),
                    res.getString("edp_type"),
                    res.getString("edp_defoutdir"),
                    res.getString("edp_defindir"),
                    res.getString("edp_outwkfl"),
                    res.getString("edp_inwkfl"),
                    res.getString("edp_outenabled"),
                    res.getString("edp_inenabled")
                    }); 
                }
                
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
        }
        return r;
    }
    
    
    public static String[] addEdiMstr(edi_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addEdiMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "select * from edi_mstr where edi_id = ? and edi_doc = ? and edi_sndgs = ? and edi_rcvgs = ?";
        String sqlInsert = "insert into edi_mstr (edi_id, edi_doc, edi_sndisa, edi_sndq, " 
                            + "edi_sndgs, edi_map, edi_eledelim, edi_segdelim, edi_subdelim, edi_fileprefix, edi_filesuffix, edi_filepath, "
                            + "edi_version, edi_rcvisa, edi_rcvgs, edi_rcvq, edi_supcode, edi_doctypeout, edi_filetypeout, edi_ifs, edi_ofs, edi_filetype, " +
                                " edi_fa_required, edi_envelopeall, edi_una, edi_ung, edi_site, edi_mflag )  " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.edi_id);
             ps.setString(2, x.edi_doc);
             ps.setString(3, x.edi_sndgs);
             ps.setString(4, x.edi_rcvgs);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.edi_id);
            psi.setString(2, x.edi_doc);
            psi.setString(3, x.edi_sndisa);
            psi.setString(4, x.edi_sndq);
            psi.setString(5, x.edi_sndgs);
            psi.setString(6, x.edi_map);
            psi.setString(7, x.edi_eledelim);
            psi.setString(8, x.edi_segdelim);
            psi.setString(9, x.edi_subdelim);
            psi.setString(10, x.edi_fileprefix);
            psi.setString(11, x.edi_filesuffix);
            psi.setString(12, x.edi_filepath);
            psi.setString(13, x.edi_version);
            psi.setString(14, x.edi_rcvisa);
            psi.setString(15, x.edi_rcvgs);
            psi.setString(16, x.edi_rcvq);
            psi.setString(17, x.edi_supcode);
            psi.setString(18, x.edi_doctypeout);
            psi.setString(19, x.edi_filetypeout);
            psi.setString(20, x.edi_ifs);
            psi.setString(21, x.edi_ofs);
            psi.setString(22, x.edi_filetype);
            psi.setString(23, x.edi_fa_required);
            psi.setString(24, x.edi_envelopeall);
            psi.setString(25, x.edi_una);
            psi.setString(26, x.edi_ung);
            psi.setString(27, x.edi_site);
            psi.setString(28, x.edi_mflag);
            
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
    
    public static String[] updateEdiMstr(edi_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateEdiMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update edi_mstr set edi_sndisa = ?, edi_sndq  = ?, " +
                " edi_eledelim = ?, edi_segdelim = ?, edi_subdelim = ?, edi_fileprefix = ?, " +
                " edi_filesuffix = ?, edi_filepath = ?, edi_version = ?, edi_rcvisa = ?, edi_rcvq = ?, edi_supcode = ?, " +
                " edi_doctypeout = ?, edi_filetypeout = ?, edi_ifs = ?, edi_ofs = ?, edi_filetype = ?, edi_fa_required = ?, edi_envelopeall = ?, " +
                " edi_una = ?, edi_ung = ?, edi_site = ?, edi_mflag = ?, edi_map = ? " +
                " where edi_id = ? and edi_doc = ? and edi_sndgs = ? and edi_rcvgs = ?  ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
       ;
        ps.setString(1, x.edi_sndisa);
        ps.setString(2, x.edi_sndq);
        ps.setString(3, x.edi_eledelim);
        ps.setString(4, x.edi_segdelim);
        ps.setString(5, x.edi_subdelim);
        ps.setString(6, x.edi_fileprefix);
        ps.setString(7, x.edi_filesuffix);
        ps.setString(8, x.edi_filepath);
        ps.setString(9, x.edi_version);
        ps.setString(10, x.edi_rcvisa);
        ps.setString(11, x.edi_rcvq);
        ps.setString(12, x.edi_supcode);
        ps.setString(13, x.edi_doctypeout);
        ps.setString(14, x.edi_filetypeout);
        ps.setString(15, x.edi_ifs);
        ps.setString(16, x.edi_ofs);
        ps.setString(17, x.edi_filetype);
        ps.setString(18, x.edi_fa_required);
        ps.setString(19, x.edi_envelopeall);
        ps.setString(20, x.edi_una);
        ps.setString(21, x.edi_ung);
        ps.setString(22, x.edi_site);
        ps.setString(23, x.edi_mflag);
        ps.setString(24, x.edi_map);
        ps.setString(25, x.edi_id);
        ps.setString(26, x.edi_doc);
        ps.setString(27, x.edi_sndgs);
        ps.setString(28, x.edi_rcvgs);
        
                
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static void updateEdiMstrMM(map_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","updateEdiMstrMM"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                sendServerPost(list, jsonString, null, "dataServEDI");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        } 
        
        try {            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                    // NOTE:   cannot change edi_doc (it is a key in edi_mstr) ...if map_mstr has changed inbound doctype, then edi_mstr has to be recreated.
                    st.executeUpdate("update edi_mstr set "
                            + "edi_doctypeout = " + "'" + x.map_outdoctype() + "'"  + ","
                            + "edi_filetypeout = " + "'" + x.map_outfiletype() + "'"  + ","   
                            + "edi_filetype = " + "'" + x.map_infiletype() + "'"  + ","         
                            + "edi_ifs = " + "'" + x.map_ifs() + "'" + ","
                            + "edi_ofs = " + "'" + x.map_ofs() + "'"
                            + " where edi_map = " + "'" + x.map_id() + "'"  
                            + ";");                
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public static String[] deleteEdiMstr(edi_mstr x) { 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteEdiMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from edi_mstr where edi_id = ? and edi_doc = ? and edi_sndgs = ? and edi_rcvgs = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
         ps.setString(1, x.edi_id);
         ps.setString(2, x.edi_doc);
         ps.setString(3, x.edi_sndgs);
         ps.setString(4, x.edi_rcvgs);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static edi_mstr getEdiMstr(String[] x) {
        edi_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","getEdiMstr"});
            list.add(new String[]{"param1",x[0]});
            list.add(new String[]{"param2",x[1]});
            list.add(new String[]{"param3",x[2]});
            list.add(new String[]{"param4",x[3]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, edi_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new edi_mstr(m);
                return r;
            }
        }
        String sql = "select * from edi_mstr where edi_id = ? "  +
                                      " AND edi_doc = ? " + 
                                      " AND edi_sndgs = ? " +
                                      " AND edi_rcvgs = ? " +
                                      ";";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
        ps.setString(3, x[2]);
        ps.setString(4, x[3]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new edi_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                 
                        r = new edi_mstr(m, 
                                res.getString("edi_id"),
                                res.getString("edi_doc"),
                                res.getString("edi_sndisa"),
                                res.getString("edi_sndq"),
                                res.getString("edi_sndgs"),
                                res.getString("edi_map"),
                                res.getString("edi_eledelim"),
                                res.getString("edi_segdelim"),
                                res.getString("edi_subdelim"),
                                res.getString("edi_fileprefix"),
                                res.getString("edi_filesuffix"),
                                res.getString("edi_filepath"),
                                res.getString("edi_version"),
                                res.getString("edi_rcvisa"),
                                res.getString("edi_rcvgs"),
                                res.getString("edi_rcvq"),
                                res.getString("edi_supcode"),
                                res.getString("edi_doctypeout"),
                                res.getString("edi_filetypeout"),
                                res.getString("edi_ifs"),
                                res.getString("edi_ofs"),
                                res.getString("edi_filetype"),
                                res.getString("edi_fa_required"),
                                res.getString("edi_envelopeall"),
                                res.getString("edi_una"),
                                res.getString("edi_ung"),
                                res.getString("edi_site"),
                                res.getString("edi_mflag")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new edi_mstr(m);
        }
        return r;
    }
    
    
    public static String[] addEDIDocTransaction(ArrayList<edi_docdet> edid, edi_doc edd) {
        String[] m = new String[2];
        Connection bscon = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addEDIDocTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(edid);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(edd);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addEDIDoc(edd, bscon, ps, res);  
            for (edi_docdet z : edid) {
                _addEDIDocDet(z, bscon, ps, res);
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
    
    private static int _addEDIDoc(edi_doc x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from edi_doc where edd_id = ?";
        String sqlInsert = "insert into edi_doc (edd_id, edd_desc, edd_type, edd_subtype, " +
                " edd_segdelim, edd_eledelim, edd_priority, edd_landmark, edd_enabled  " 
                + "  )  " +
                " values (?,?,?,?,?,?,?,?,?); "; 
        
        
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.edd_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.edd_id);
            ps.setString(2, x.edd_desc);
            ps.setString(3, x.edd_type);
            ps.setString(4, x.edd_subtype);
            ps.setString(5, x.edd_segdelim);
            ps.setString(6, x.edd_eledelim);
            ps.setString(7, x.edd_priority);
            ps.setString(8, x.edd_landmark);
            ps.setString(9, x.edd_enabled);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addEDIDocDet(edi_docdet x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from edi_docdet where edid_id = ?;";
        String sqlInsert = "insert into edi_docdet (edid_id, edid_role, edid_rectype, edid_valuetype, edid_row, edid_col, " +
                " edid_length, edid_regex, edid_value, edid_tag, edid_xpath, edid_enabled  )  " 
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?); "; 
             
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.edid_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.edid_id);
            ps.setString(2, x.edid_role);
            ps.setString(3, x.edid_rectype);
            ps.setString(4, x.edid_valuetype);
            ps.setString(5, x.edid_row);
            ps.setString(6, x.edid_col);
            ps.setString(7, x.edid_length);
            ps.setString(8, x.edid_regex);
            ps.setString(9, x.edid_value);
            ps.setString(10, x.edid_tag);
            ps.setString(11, x.edid_xpath);
            ps.setString(12, x.edid_enabled);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] updateEDIDocTransaction(String x, ArrayList<edi_docdet> edid, edi_doc edd) {
        String[] m = new String[2];
        Connection bscon = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateEDIDocTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(edid);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(edd);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
             _deleteEDIDocDetLines(x, bscon);  // discard all lines
            for (edi_docdet z : edid) {
                _addEDIDocDet(z, bscon, ps, res); 
            }
             _updateEDIDoc(edd, bscon, ps);  
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
    
    private static int _updateEDIDoc(edi_doc x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update edi_doc set edd_desc = ?, edd_type = ?, " +
                " edd_subtype = ?, edd_segdelim = ?, edd_eledelim = ?, edd_priority = ?, " +
                " edd_landmark = ?, edd_enabled = ? " +
                "  where edd_id = ? ";
         
          ps = con.prepareStatement(sql);
            ps.setString(9, x.edd_id);
            ps.setString(2, x.edd_desc);
            ps.setString(3, x.edd_type);
            ps.setString(4, x.edd_subtype);
            ps.setString(5, x.edd_segdelim);
            ps.setString(6, x.edd_eledelim);
            ps.setString(7, x.edd_priority);
            ps.setString(8, x.edd_landmark);
            ps.setString(9, x.edd_enabled);
            rows = ps.executeUpdate();
            return rows;
    }
    
    private static void _deleteEDIDocDetLines(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from edi_docdet where edid_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    public static String[] deleteEDIDoc(String x) { 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "deleteEDIDoc"});
            list.add(new String[]{"param1", x});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        } 
        String[] m = new String[2];
        String sql = "delete from edi_doc where edd_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        sql = "delete from edi_docdet where edid_id = ?; ";
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
    
    public static EDIDocSet getEDIDocSet(String[] x ) {
        EDIDocSet r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIDocSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, EDIDocSet.class); 
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
            edi_doc edd = _getEDIDoc(x, bscon, ps, res);
            ArrayList<edi_docdet> edid = _getEDIDocDet(x, bscon, ps, res);
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new EDIDocSet(m, edd, edid);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new EDIDocSet(m);
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
   
    private static edi_doc _getEDIDoc(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        edi_doc r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from edi_doc where edd_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new edi_doc(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new edi_doc(m, res.getString("edd_id"), res.getString("edd_desc"), res.getString("edd_type"),
                res.getString("edd_subtype"), res.getString("edd_segdelim"), res.getString("edd_eledelim"), res.getString("edd_priority"), res.getString("edd_landmark"),
                res.getString("edd_enabled"));
                }
            }
            return r;
    }
    
    private static ArrayList<edi_docdet> _getEDIDocDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<edi_docdet> list = new ArrayList<edi_docdet>();
        edi_docdet r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from edi_docdet where edid_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new edi_docdet(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new edi_docdet(m, res.getString("edid_id"), res.getString("edid_role"), res.getString("edid_rectype"),
                    res.getString("edid_valuetype"), res.getString("edid_row"), res.getString("edid_col"), res.getString("edid_length"),
                    res.getString("edid_regex"), res.getString("edid_value"), res.getString("edid_tag"), res.getString("edid_xpath"),
                    res.getString("edid_enabled"));
                    list.add(r);
                    }
            }
            return list;
    }
    
    
    
    
    public static int writeWFLog(wkf_log wkfl, int origparentid, ArrayList<wkfd_log> wkfdl) {
        boolean isError = false;
        int parentid = -1;
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            
            parentid = origparentid;
            
            if (parentid <= 0 && wkfl != null) {
            parentid = _addWkfLog(wkfl, bscon, ps, res); 
            } else {
                if (wkfdl != null) {
                    for (wkfd_log z : wkfdl) {
                    _addWkfDetlog(parentid, z, bscon, ps, res);
                    isError = (z.wkfdl_status().equals("1")) ? true : false;
                    }
                    if (isError) {
                       _updateWkfLogStatus(parentid, bscon, ps, "1"); 
                    }
                }
            }
            
            bscon.commit();
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
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
    return parentid;
    }
    
    public static void updateWFLog(int id, String status, String message, String ref) {
        String[] m = new String[2];
        String sql = "update wkf_log set wkfl_status = ?, wkfl_messg = ?, wkfl_ref = ? " +
                "  where wkfl_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, status);
        ps.setString(2, message);
        ps.setString(3, ref);
        ps.setInt(4, id);
        int rows = ps.executeUpdate();
        } catch (SQLException s) {
	       MainFrame.bslog(s);
        }
    }
    
    private static void _updateWkfLogStatus(int id, Connection con, PreparedStatement ps, String status) throws SQLException {
        
        String sql = "update wkf_log set wkfl_status = ? " +
                "  where wkfl_id = ? ";
            ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, String.valueOf(id));
            ps.executeUpdate();
    }
    
    
    private static int _addWkfLog(wkf_log x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int returnkey = 0;
        String sqlInsert = "insert into wkf_log (wkfl_job, wkfl_desc, wkfl_ref, wkfl_status, wkfl_messg, wkfl_site "
                + "  )  " +
                " values (?,?,?,?,?,?); ";
            ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, x.wkfl_job);
            ps.setString(2, x.wkfl_desc);
            ps.setString(3, x.wkfl_ref);
            ps.setString(4, x.wkfl_status);
            ps.setString(5, x.wkfl_messg);
            ps.setString(6, x.wkfl_site);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next()) {
             returnkey = rs.getInt(1);
            }
             
            return returnkey;
    }
    
    private static int _addWkfDetlog(int parentid, wkfd_log x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlInsert = "insert into wkfd_log (wkfdl_parentid, wkfdl_action, wkfdl_ref, wkfdl_status, wkfdl_messg, wkfdl_site  )  " 
                        + " values (?,?,?,?,?,?); ";
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, String.valueOf(parentid));
            ps.setString(2, x.wkfdl_action);
            ps.setString(3, x.wkfdl_ref);
            ps.setString(4, x.wkfdl_status);
            ps.setString(5, x.wkfdl_messg);
            ps.setString(6, x.wkfdl_site);
            rows = ps.executeUpdate();
            return rows;
    }
    
    public static wkf_log getWFLog(String id) {
        wkf_log r = null;
        String[] m = new String[2];
        String sql = "select * from wkf_log where wkfl_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wkf_log(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkf_log(m, 
                            res.getString("wkfl_id"), 
                            res.getString("wkfl_job"),    
                            res.getString("wkfl_desc"),
                            res.getString("wkfl_ts"),
                            res.getString("wkfl_ref"),
                            res.getString("wkfl_status"),
                            res.getString("wkfl_messg"),
                            res.getString("wkfl_site")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wkf_log(m);
        }
        return r;
    }
    
    public static ArrayList<wkfd_log> getWFDLog(String parentid) {
        wkfd_log r = null;
        String[] m = new String[2];
        ArrayList<wkfd_log> list = new ArrayList<wkfd_log>();
        String sql = "select * from wkfd_log where wkfdl_parentid = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, parentid);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wkfd_log(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wkfd_log(m, 
                        res.getString("wkfdl_id"), 
                        res.getString("wkfdl_parentid"), 
                        res.getString("wkfdl_action"),
                        res.getString("wkfdl_ts"),
                        res.getString("wkfdl_ref"),
                        res.getString("wkfdl_status"),
                        res.getString("wkfdl_messg"),
                        res.getString("wkfdl_site"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wkfd_log(m);
               list.add(r);
        }
        return list;
    }
    
    
    
    public static String[] addMapStruct(dfs_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from dfs_mstr where dfs_id = ?";
        String sqlInsert = "insert into dfs_mstr (dfs_id, dfs_desc, dfs_version, dfs_doctype, dfs_filetype "
                + "  )  " +
                " values (?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.dfs_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.dfs_id);
            psi.setString(2, x.dfs_desc);
            psi.setString(3, x.dfs_version);
            psi.setString(4, x.dfs_doctype);
            psi.setString(5, x.dfs_filetype);
            
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
        
    public static String[] updateMapStruct(dfs_mstr x) {
        String[] m = new String[2];
        String sql = "update dfs_mstr set dfs_desc = ?, dfs_version = ?, dfs_doctype = ?, dfs_filetype = ? " +
                "  where dfs_id = ? ";
       try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.dfs_desc);
        ps.setString(2, x.dfs_version);
        ps.setString(3, x.dfs_doctype);
        ps.setString(4, x.dfs_filetype);
        ps.setString(5, x.dfs_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteDFStructure(dfs_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from dfs_det where dfsd_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.dfs_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        sql = "delete from dfs_mstr where dfs_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.dfs_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteDFStructure(String x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "deleteDFStructure"});
            list.add(new String[]{"param1", x});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        } 
        String[] m = new String[2];
        String sql = "delete from dfs_det where dfsd_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        sql = "delete from dfs_mstr where dfs_id = ?; ";
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
    
    
    
    public static String[] addAPIMstr(api_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addAPIMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "select * from api_mstr where api_id = ?";
        String sqlInsert = "insert into api_mstr (api_id, api_desc, api_version," +
        " api_url, api_port, api_path, api_user, " +
        " api_pass, api_key, api_keylabel, api_protocol, api_class, api_encrypted, api_signed, api_contenttype, " +
        " api_auth, api_char1, api_char2, api_char3, api_notes ) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
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
            psi.setString(10, x.api_keylabel);
            psi.setString(11, x.api_protocol);
            psi.setString(12, x.api_class);
            ps.setString(13, x.api_encrypted);
            ps.setString(14, x.api_signed);
            ps.setString(15, x.api_contenttype);
            ps.setString(16, x.api_auth);
            ps.setString(17, x.api_char1);
            ps.setString(18, x.api_char2);
            ps.setString(19, x.api_char3);
            ps.setString(20, x.api_notes);
            
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
        " api_pass, api_key, api_keylabel, api_protocol, api_class, api_encrypted, api_signed, api_contenttype, " +
        " api_auth, api_char1, api_char2, api_char3, api_notes ) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
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
            ps.setString(10, x.api_keylabel);
            ps.setString(11, x.api_protocol);
            ps.setString(12, x.api_class);
            ps.setString(13, x.api_encrypted);
            ps.setString(14, x.api_signed);
            ps.setString(15, x.api_contenttype);
            ps.setString(16, x.api_auth);
            ps.setString(17, x.api_char1);
            ps.setString(18, x.api_char2);
            ps.setString(19, x.api_char3);
            ps.setString(20, x.api_notes);
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
                " as2_encalgo, as2_signalgo, as2_micalgo, as2_contenttype, as2_enabled, as2_sysas2id, as2_site, " +
                " as2_inwkf, as2_outwkf, as2_sysenccert, as2_syssigncert, as2_syscert_bool, as2_signmdn, as2_flatmdn, as2_eol ) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
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
            ps.setString(23, x.as2_contenttype);
            ps.setString(24, x.as2_enabled);
            ps.setString(25, x.as2_sysas2id);
            ps.setString(26, x.as2_site);
            ps.setString(27, x.as2_inwkf);
            ps.setString(28, x.as2_outwkf);
            ps.setString(29, x.as2_sysenccert);
            ps.setString(30, x.as2_syssigncert);
            ps.setString(31, x.as2_syscert_bool);
            ps.setString(32, x.as2_signmdn);
            ps.setString(33, x.as2_flatmdn);
            ps.setString(34, x.as2_eol);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    
    private static int _addAPIDet(api_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from api_det where apid_id = ? and apid_method = ?";
        String sqlInsert = "insert into api_det (apid_id, apid_method, apid_seq,  " +
                            " apid_verb, apid_type, apid_path, apid_key, " +
                            " apid_value, apid_source, apid_destination, apid_enabled, " +
                            "apid_char1, apid_char2, apid_char3 ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.apid_id);
          ps.setString(2, x.apid_method);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.apid_id);
            ps.setString(2, x.apid_method);
            ps.setInt(3, x.apid_seq); 
            ps.setString(4, x.apid_verb);
            ps.setString(5, x.apid_type);
            ps.setString(6, x.apid_path);
            ps.setString(7, x.apid_key);
            ps.setString(8, x.apid_value);
            ps.setString(9, x.apid_source);
            ps.setString(10, x.apid_destination);
            ps.setString(11, x.apid_enabled);
            ps.setString(12, x.apid_char1);
            ps.setString(13, x.apid_char2);
            ps.setString(14, x.apid_char3);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addAPIDMeta(apid_meta x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from apid_meta where apidm_id = ? and apidm_method = ? and apidm_key = ?;";
        String sqlInsert = "insert into apid_meta (apidm_id, apidm_method, apidm_key, apidm_value, apidm_httphead )  " 
                        + " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.apidm_id);
          ps.setString(2, x.apidm_method);
          ps.setString(3, x.apidm_key);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.apidm_id);
            ps.setString(2, x.apidm_method);
            ps.setString(3, x.apidm_key);
            ps.setString(4, x.apidm_value);
            ps.setString(5, x.apidm_httphead);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
          
    public static String[] addAPITransaction(ArrayList<apid_meta> apidm, ArrayList<api_det> apid, api_mstr api) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addAPITransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(apidm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(apid);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(api);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
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
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addAPIMstr(api, bscon, ps, res);  
            for (api_det z : apid) {
                _addAPIDet(z, bscon, ps, res);
            }
            for (apid_meta z : apidm) {
                _addAPIDMeta(z, bscon, ps, res);
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
        
    public static String[] addAS2Mstr(as2_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addAS2Mstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
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
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            _addAS2Mstr(x, bscon, ps, res); 
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
                " api_path = ?, api_user = ?, api_pass = ?, api_key = ?, api_keylabel = ?, api_protocol = ?, api_class = ?,  " +
                " api_encrypted = ?, api_signed = ?, api_contenttype = ?, api_auth = ?, api_char1 = ?, api_char2 = ?, api_char3 = ?, api_notes = ? " +
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
        ps.setString(9, x.api_keylabel);
        ps.setString(10, x.api_protocol);
        ps.setString(11, x.api_class);
        ps.setString(12, x.api_encrypted);
        ps.setString(13, x.api_signed);
        ps.setString(14, x.api_contenttype);
        ps.setString(15, x.api_auth);
        ps.setString(16, x.api_char1);
        ps.setString(17, x.api_char2);
        ps.setString(18, x.api_char3);
        ps.setString(19, x.api_notes);
        ps.setString(20, x.api_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] updateAS2Maint(as2_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateAS2Maint"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update as2_mstr set as2_desc = ?, as2_version = ?, as2_url = ?, as2_port = ?, " +
                " as2_path = ?, as2_user = ?, as2_pass = ?, as2_key = ?, as2_protocol = ?, as2_class = ?,  " +
                " as2_indir = ?, as2_outdir = ?, " +
                " as2_encrypted = ?, as2_signed = ?, as2_enccert = ?, " +
                " as2_forceencrypted = ?, as2_forcesigned = ?, as2_signcert = ?, " +
                " as2_encalgo = ?, as2_signalgo = ?, as2_micalgo = ?, as2_contenttype = ?, as2_enabled = ?,  " +
                "as2_sysas2id = ?, as2_site = ?, as2_inwkf = ?, as2_outwkf = ?, as2_sysenccert = ?, as2_syssigncert = ?, " +
                " as2_syscert_bool = ?, as2_signmdn = ? " +
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
        ps.setString(22, x.as2_contenttype);
        ps.setString(23, x.as2_enabled);
        ps.setString(24, x.as2_sysas2id);
        ps.setString(25, x.as2_site);
        ps.setString(26, x.as2_inwkf);
        ps.setString(27, x.as2_outwkf);
        ps.setString(28, x.as2_sysenccert);
        ps.setString(29, x.as2_syssigncert);
        ps.setString(30, x.as2_syscert_bool);
        ps.setString(31, x.as2_signmdn);
        ps.setString(32, x.as2_id);
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
                " api_path = ?, api_user = ?, api_pass = ?, api_key = ?, api_keylabel = ?, api_protocol = ?, api_class = ?,  " +
                " api_encrypted = ?, api_signed = ?, api_contenttype = ?, api_auth = ?, api_char1 = ?, api_char2 = ?, api_char3 = ?, api_notes = ? " +
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
        ps.setString(9, x.api_keylabel);
        ps.setString(10, x.api_protocol);
        ps.setString(11, x.api_class);
        ps.setString(12, x.api_encrypted);
        ps.setString(13, x.api_signed);
        ps.setString(14, x.api_contenttype);
        ps.setString(15, x.api_auth);
        ps.setString(16, x.api_char1);
        ps.setString(17, x.api_char2);
        ps.setString(18, x.api_char3);
        ps.setString(19, x.api_notes);
        ps.setString(20, x.api_id);
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
                " as2_encalgo = ?, as2_signalgo = ?, as2_micalgo = ?, as2_contenttype = ?, " +
                " as2_enabled = ?, as2_sysas2id = ?, as2_site = ?, as2_inwkf = ?, as2_outwkf = ?, as2_sysenccert = ?, as2_syssigncert = ?, " +
                " as2_syscert_bool = ?, as2_signmdn = ?, as2_flatmdn = ?, as2_eol = ? " +
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
        ps.setString(22, x.as2_contenttype);
        ps.setString(23, x.as2_enabled);
        ps.setString(24, x.as2_sysas2id);
        ps.setString(25, x.as2_site);
        ps.setString(26, x.as2_inwkf);
        ps.setString(27, x.as2_outwkf);
        ps.setString(28, x.as2_sysenccert);
        ps.setString(29, x.as2_syssigncert);
        ps.setString(30, x.as2_syscert_bool);
        ps.setString(31, x.as2_signmdn);
        ps.setString(32, x.as2_flatmdn);
        ps.setString(33, x.as2_eol);
        ps.setString(34, x.as2_id);
            rows = ps.executeUpdate();
        return rows;
    }
    
    
    private static int _updateAPIdet(api_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from api_det where apid_id = ? and apid_method = ?";
        String sqlUpdate = "update api_det set apid_seq = ?, " +
                           " apid_verb = ?, apid_type = ?, apid_path = ?,  " +
                           " apid_key = ?, apid_value = ?, apid_source = ?, apid_destination = ?, apid_enabled = ?, " +
                           " apid_char1 = ?, apid_char2 = ?, apid_char3 = ? " + 
                           " where apid_id = ? and apid_method = ? ; ";
        String sqlInsert = "insert into api_det (apid_id, apid_method, apid_seq,  " +
                             " apid_verb, apid_type, apid_path, apid_key, " +
                            " apid_value, apid_source, apid_destination, apid_enabled, " +
                            " apid_char1, apid_char2, apid_char3 ) " +
                           " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); " ;
        ps = con.prepareStatement(sqlSelect);
        ps.setString(1, x.apid_id);
        ps.setString(2, x.apid_method);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.apid_id);
            ps.setString(2, x.apid_method);
            ps.setInt(3, x.apid_seq);
            ps.setString(4, x.apid_verb);
            ps.setString(5, x.apid_type);
            ps.setString(6, x.apid_path);
            ps.setString(7, x.apid_key);
            ps.setString(8, x.apid_value);
            ps.setString(9, x.apid_source);
            ps.setString(10, x.apid_destination);
            ps.setString(11, x.apid_enabled); 
            ps.setString(12, x.apid_char1);
            ps.setString(13, x.apid_char2);
            ps.setString(14, x.apid_char3);
            // ps.setString(9, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        } else {    // update
         
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setInt(1, x.apid_seq);
            ps.setString(2, x.apid_verb);
            ps.setString(3, x.apid_type);
            ps.setString(4, x.apid_path);
            ps.setString(5, x.apid_key);
            ps.setString(6, x.apid_value);
            ps.setString(7, x.apid_source);
            ps.setString(8, x.apid_destination);
            ps.setString(9, x.apid_enabled); 
            ps.setString(10, x.apid_char1);
            ps.setString(11, x.apid_char2);
            ps.setString(12, x.apid_char3);
            ps.setString(13, x.apid_id);
            ps.setString(14, x.apid_method);
            // ps.setString(7, x.ecnt_notes);  another mechanism updates the Notes field
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
     
    private static int _updateAPIDMeta(apid_meta x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from apid_meta where apidm_id = ? and apidm_method = ? and apidm_key = ?;";
        String sqlInsert = "insert into apid_meta (apidm_id, apidm_method, apidm_key, apidm_value, apidm_httphead )  " 
                        + " values (?,?,?,?,?); ";
        String sqlUpdate = "update apid_meta set apidm_key = ?, apidm_value = ?, apidm_httphead " +
                 " where apidm_id = ? and apidm_method = ? ; ";
       
        ps = con.prepareStatement(sqlSelect);
        ps.setString(1, x.apidm_id);
        ps.setString(2, x.apidm_method);
        ps.setString(3, x.apidm_key);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.apidm_id);
            ps.setString(2, x.apidm_method);
            ps.setString(3, x.apidm_key);
            ps.setString(4, x.apidm_value);
            ps.setString(5, x.apidm_httphead);
            rows = ps.executeUpdate();
        } else {    // update
         
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(1, x.apidm_key);
            ps.setString(2, x.apidm_value);
            ps.setString(3, x.apidm_httphead);
            ps.setString(4, x.apidm_id);
            ps.setString(5, x.apidm_method);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    
    public static String[] updateAPITransaction(String x, ArrayList<String> lines, ArrayList<apid_meta> apidm, ArrayList<api_det> apid, api_mstr api) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateAPITransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(lines);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(apidm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(apid);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(api);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
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
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            if (lines != null) {
                for (String line : lines) {
                   _deleteAPILines(x, line, bscon);  // discard unwanted lines
                }
            }
            for (api_det z : apid) {
                _updateAPIdet(z, bscon, ps, res);
            }
            _deleteAllAPIDMeta(x, bscon); // delete all meta details for this apidm_id...then add diff back
            for (apid_meta z : apidm) {
                _updateAPIDMeta(z, bscon, ps, res);
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
    
    public static String[] updateAPIDetTransaction(String x, ArrayList<apid_meta> apidm, ArrayList<api_det> apid) {
        // used for single Detail Record Updates;  apid should have only a single record
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateAPIDetTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(apidm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(apid);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
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
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            
            for (api_det z : apid) {
                _updateAPIdet(z, bscon, ps, res);
            }
            _deleteAllAPIDMeta(x, bscon); // delete all meta details for this apidm_id...then add diff back
            for (apid_meta z : apidm) {
                _updateAPIDMeta(z, bscon, ps, res);
            }
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
    
    
    public static String[] updateAS2Mstr(String x, as2_mstr as2) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","updateAS2Mstr"});
            list.add(new String[]{"param1", x});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(as2);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
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
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","deleteAPIMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","deleteAS2Mstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
    
    private static void _deleteAllAPIDMeta(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from apid_meta where apidm_id = ?;";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    
    private static void _deleteAPILines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from api_det where apid_id = ? and apid_method = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        sql = "delete from apid_meta where apidm_id = ? and apidm_method = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        ps.close();
    }
    
    public static api_mstr getAPIMstr(String[] x) {
        api_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getAPIMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, api_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new api_mstr(m);
                return r;
            }
        }
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
                                res.getString("api_keylabel"),
                            res.getString("api_protocol"),
                            res.getString("api_class"),
                            res.getString("api_encrypted"),
                            res.getString("api_signed"),
                            res.getString("api_contenttype"),
                            res.getString("api_auth"),
                            res.getString("api_char1"),
                            res.getString("api_char2"),
                            res.getString("api_char3"),
                            res.getString("api_notes")
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
    
    public static edi_ctrl getEDICtrl() {
        edi_ctrl r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getEDICtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, edi_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new edi_ctrl(m);
                return r;
            }
        }
        
        
        String sql = "select * from edi_ctrl ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new edi_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new edi_ctrl(m, res.getString("edic_indir"), 
                            res.getString("edic_outdir"),
                            res.getString("edic_scriptdir"),
                            res.getString("edic_inarch"),
                            res.getString("edic_outarch"),
                            res.getString("edic_batch"),
                            res.getString("edic_structure"),
                            res.getString("edic_errordir"),
                            res.getString("edic_mapdir"),
                            res.getString("edic_archyesno"),
                            res.getString("edic_delete"),
                            res.getString("edic_tpid"),
                            res.getString("edic_gsid"),
                            res.getString("edic_as2id"),
                            res.getString("edic_as2url"),
                            res.getString("edic_signkey"),
                            res.getString("edic_enckey"),
                            res.getString("edic_varchar")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new edi_ctrl(m);
        }
        return r;
    }
        
    public static String[] addupdateEDICtrl(edi_ctrl x) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addupdateEDICtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  edi_ctrl ";
        String sqlInsert = "insert into edi_ctrl (edic_indir, edic_outdir, edic_scriptdir, edic_inarch, " +
                " edic_outarch, edic_batch, edic_structure, edic_errordir, edic_mapdir, edic_archyesno, edic_delete,  " +
                " edic_tpid, edic_gsid, edic_as2id, edic_as2url, edic_signkey, edic_enckey, edic_varchar) " 
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update edi_ctrl set edic_indir = ?, edic_outdir = ?, edic_scriptdir = ?, edic_inarch = ?, " +
                " edic_outarch = ?, edic_batch = ?, edic_structure = ?, edic_errordir = ?, edic_mapdir = ?, edic_archyesno = ?, edic_delete = ?,  " +
                " edic_tpid = ?, edic_gsid = ?, edic_as2id = ?, edic_as2url = ?, edic_signkey = ?, edic_enckey = ?, edic_varchar = ?" ;
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.edic_indir);
            psi.setString(2, x.edic_outdir);
            psi.setString(3, x.edic_scriptdir);
            psi.setString(4, x.edic_inarch);
            psi.setString(5, x.edic_outarch);
            psi.setString(6, x.edic_batch);
            psi.setString(7, x.edic_structure);
            psi.setString(8, x.edic_errordir);
            psi.setString(9, x.edic_mapdir);
            psi.setString(10, x.edic_archyesno);
            psi.setString(11, x.edic_delete);
            psi.setString(12, x.edic_tpid);
            psi.setString(13, x.edic_gsid);
            psi.setString(14, x.edic_as2id);
            psi.setString(15, x.edic_as2url);
            psi.setString(16, x.edic_signkey);
            psi.setString(17, x.edic_enckey);
            psi.setString(18, x.edic_varchar);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.edic_indir);
            psu.setString(2, x.edic_outdir);
            psu.setString(3, x.edic_scriptdir);
            psu.setString(4, x.edic_inarch);
            psu.setString(5, x.edic_outarch);
            psu.setString(6, x.edic_batch);
            psu.setString(7, x.edic_structure);
            psu.setString(8, x.edic_errordir);
            psu.setString(9, x.edic_mapdir);
            psu.setString(10, x.edic_archyesno);
            psu.setString(11, x.edic_delete);
            psu.setString(12, x.edic_tpid);
            psu.setString(13, x.edic_gsid);
            psu.setString(14, x.edic_as2id);
            psu.setString(15, x.edic_as2url);
            psu.setString(16, x.edic_signkey);
            psu.setString(17, x.edic_enckey);
            psu.setString(18, x.edic_varchar);
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

    
    public static as2_mstr getAS2Mstr(String[] x) {
        as2_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getAS2Mstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, as2_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new as2_mstr(m);
                return r;
            }
        }
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
                            res.getString("as2_micalgo"),
                            res.getString("as2_contenttype"),
                            res.getString("as2_enabled"),
                            res.getString("as2_sysas2id"),
                            res.getString("as2_site"),
                            res.getString("as2_inwkf"),
                            res.getString("as2_outwkf"),
                            res.getString("as2_sysenccert"),
                            res.getString("as2_syssigncert"),
                            res.getString("as2_syscert_bool"),
                            res.getString("as2_signmdn"),
                            res.getString("as2_flatmdn"),
                            res.getString("as2_eol")
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
    
    public static as2_mstr getAS2Mstr(String senderAS2ID, String receiverAS2ID) {
        as2_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from as2_mstr where as2_user = ? and as2_sysas2id = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, senderAS2ID);
        ps.setString(2, receiverAS2ID);
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
                            res.getString("as2_micalgo"),
                            res.getString("as2_contenttype"),
                            res.getString("as2_enabled"),
                            res.getString("as2_sysas2id"),
                            res.getString("as2_site"),
                            res.getString("as2_inwkf"),
                            res.getString("as2_outwkf"),
                            res.getString("as2_sysenccert"),
                            res.getString("as2_syssigncert"),
                            res.getString("as2_syscert_bool"),
                            res.getString("as2_signmdn"),
                            res.getString("as2_flatmdn"),
                            res.getString("as2_eol")
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
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getAPIDets"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServEDI");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<api_det>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        
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
                        res.getInt("apid_seq"), 
                        res.getString("apid_verb"), 
                        res.getString("apid_type"),
                        res.getString("apid_path"),
                        res.getString("apid_key"),
                        res.getString("apid_value"),
                        res.getString("apid_source"),
                        res.getString("apid_destination"),        
                        res.getString("apid_enabled"),
                        res.getString("apid_char1"),
                        res.getString("apid_char2"),
                        res.getString("apid_char3")        
                        );
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
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getAPIDet"});
            list.add(new String[]{"param1",id});
            list.add(new String[]{"param2",method});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServEDI");
                r = objectMapper.readValue(returnstring, api_det.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new api_det(m);
                return r;
            }
        }
        
        
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
                        res.getInt("apid_seq"), 
                        res.getString("apid_verb"), 
                        res.getString("apid_type"),
                        res.getString("apid_path"),
                        res.getString("apid_key"),
                        res.getString("apid_value"),
                        res.getString("apid_source"),
                        res.getString("apid_destination"),
                        res.getString("apid_enabled"),
                        res.getString("apid_char1"),
                        res.getString("apid_char2"),
                        res.getString("apid_char3"));
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getAPIMethodsList"});
            list.add(new String[]{"param1", nbr});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
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
    
    public static ArrayList<apid_meta> getAPIDMeta(String code) {
        apid_meta r = null;
        String[] m = new String[2];
        ArrayList<apid_meta> list = new ArrayList<apid_meta>();
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getAPIDMeta"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServEDI");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<apid_meta>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        String sql = "select * from apid_meta where apidm_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new apid_meta(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new apid_meta(m, res.getString("apidm_id"), 
                        res.getString("apidm_method"), 
                        res.getString("apidm_key"),
                        res.getString("apidm_value"),
                        res.getString("apidm_httphead"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new apid_meta(m);
               list.add(r);
        }
        return list;
    }
    
    public static ArrayList<apid_meta> getAPIDMeta(String code, String line) {
        apid_meta r = null;
        String[] m = new String[2];
        ArrayList<apid_meta> list = new ArrayList<apid_meta>();
        String sql = "select * from apid_meta where apidm_id = ? and apidm_method = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
        ps.setString(2, line);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new apid_meta(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new apid_meta(m, res.getString("apidm_id"), 
                        res.getString("apidm_method"), 
                        res.getString("apidm_key"),
                        res.getString("apidm_value"),
                        res.getString("apidm_httphead"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new apid_meta(m);
               list.add(r);
        }
        return list;
    }
    
    
    //misc
    public static ArrayList<String[]> getEDIInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServEDI"));
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
             
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select ov_site, ov_currency, user_site from ov_mstr inner join user_mstr on  user_id = " + "'" + userid + "'" + ";" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("user_site");
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
            
             res = st.executeQuery("select edpd_alias from edpd_partner order by edpd_alias; ");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "aliases";
               s[1] = res.getString("edpd_alias");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'edidoctype' order by code_key ;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "doctypes";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'edixreftype' order by code_key ;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "edixreftype";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select wkf_id from wkf_mstr order by wkf_id ;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "workflows";
               s[1] = res.getString("wkf_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select map_id from map_mstr order by map_id ;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "maps";
               s[1] = res.getString("map_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select edp_id from edp_partner order by edp_id ;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "partners";
               s[1] = res.getString("edp_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select pks_id from pks_mstr where pks_type <> 'store' ;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "pks";
               s[1] = res.getString("pks_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select dfs_id from dfs_mstr order by dfs_id ; ");
             while (res.next()) {
               String[] s = new String[2];
               s[0] = "dfs_id";
               s[1] = res.getString("dfs_id");
               lines.add(s);
            }
             
            res = st.executeQuery("select edic_indir, edic_outdir, edic_inarch, edic_outarch, edic_batch, edic_errordir, edic_mapdir, edic_delete, edic_archyesno, edic_structure from edi_ctrl ;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "directories";
               s[1] = res.getString("edic_indir") + "," + 
                       res.getString("edic_outdir") + ","  + 
                       res.getString("edic_inarch") + "," +
                       res.getString("edic_outarch") + "," +
                       res.getString("edic_batch") + "," +
                       res.getString("edic_errordir") + "," +
                       res.getString("edic_mapdir") + "," +
                       res.getString("edic_delete") + "," +
                       res.getString("edic_archyesno") + "," +
                       res.getString("edic_structure");
               lines.add(s);
            }
            
            res = st.executeQuery("select eds_bsdoc, eds_doc from edi_stds " +
                        " order by eds_bsdoc; ");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "stds";
               s[1] = res.getString("eds_bsdoc") + "," + res.getString("eds_doc");
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
    
    public static String getEDITransBrowseDocView(String tradeid, String indoc, String outdoc, String ref, String site, String fromdate, String todate) {
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
            
            try{
                if (! tradeid.isEmpty() && indoc.isEmpty() ) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " coalesce(elg_severity,'success') as detstatus " +
                    " FROM edi_idx  " +
                    " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where edx_sender >= " + "'" + tradeid + "'" +
                    " AND edx_sender <= " + "'" + tradeid + "'" +
                    " AND edx_site = " + "'" + site + "'" +        
                    " AND edx_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + todate  + " 23:59:59" + "'" + " order by edx_id desc ;" ) ;
                    }
                if (! indoc.isEmpty() && tradeid.isEmpty()) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " coalesce(elg_severity,'success') as detstatus " +
                    " FROM edi_idx  " +
                    " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where " +
                    " edx_indoctype >= " + "'" + indoc + "'" +
                    " AND edx_indoctype <= " + "'" + indoc + "'" +    
                    " AND edx_site = " + "'" + site + "'" +         
                    " AND edx_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + todate  + " 23:59:59" + "'" + " order by edx_id desc ;" ) ;
                    }
                 if (! indoc.isEmpty() && ! tradeid.isEmpty()) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " coalesce(elg_severity,'success') as detstatus " +
                    " FROM edi_idx  " +
                    " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where edx_sender >= " + "'" + tradeid + "'" +
                    " AND edx_sender <= " + "'" + tradeid + "'" +
                    " AND edx_indoctype >= " + "'" + indoc + "'" +
                    " AND edx_indoctype <= " + "'" + indoc + "'" +    
                    " AND edx_site = " + "'" + site + "'" +         
                    " AND edx_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + todate  + " 23:59:59" + "'" + " order by edx_id desc ;" ) ;
                    }
                 if (tradeid.isEmpty() && indoc.isEmpty()) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " (select elg_severity from edi_log where elg_idxnbr = edx_id and elg_comkey = edx_comkey order by elg_id desc limit 1) as detstatus " +
                    " FROM edi_idx  " +
                   // " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where edx_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + todate  + " 23:59:59" + "'" + 
                    " AND edx_site = " + "'" + site + "'" + 
                    " order by edx_id desc ;" ) ;
                    }
                 if (! ref.isEmpty()) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " coalesce(elg_severity,'success') as detstatus " +
                    " FROM edi_idx  " +
                    " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where edx_ref like " + "'%" + ref + "%'" +
                    " AND edx_site = " + "'" + site + "'" +         
                    " order by edx_id desc ;" ) ;
                    }
                    
                 
                    while (res.next()) {
                        
                        if (! outdoc.isBlank() && ! res.getString("edx_outdoctype").equals(outdoc)) {
                        continue;
                    }
                        
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("edx_id"));
                        rowArray.put(res.getString("edx_comkey"));
                        rowArray.put(res.getString("edx_sender"));
                        rowArray.put(res.getString("edx_receiver"));
                        rowArray.put(res.getString("edx_ts"));
                        rowArray.put(res.getString("edx_infiletype"));
                        rowArray.put(res.getString("edx_indoctype"));
                        rowArray.put(res.getString("edx_inbatch"));
                        rowArray.put(res.getString("edx_ref"));
                        rowArray.put(res.getString("edx_outfiletype"));
                        rowArray.put(res.getString("edx_outdoctype"));
                        rowArray.put(res.getString("edx_outbatch"));
                        rowArray.put("inview");
                        rowArray.put("outview");
                        rowArray.put(res.getString("detstatus"));
                        rowArray.put(res.getString("edx_ack"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getEDITransBrowseFileView(String tradeid, String indoc, String outdoc, String ref, String site, String fromdate, String todate) {
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
            
            try{
                if (! tradeid.isEmpty() && indoc.isEmpty() ) {
                    res = st.executeQuery("SELECT * FROM edi_file  " +
                    " where edf_partner >= " + "'" + tradeid + "'" +
                    " AND edf_partner <= " + "'" + tradeid + "'" +        
                    " AND edf_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND edf_ts <= " + "'" + todate  + " 23:59:59" + "'" + 
                    " AND edf_site = " + "'" + site + "'" +         
                    " order by edf_id desc ;" ) ;
                    }
                if (! indoc.isEmpty() && tradeid.isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_file  " +
                    " where " +
                    " edf_doctype >= " + "'" + indoc + "'" +
                    " AND edf_doctype <= " + "'" + indoc + "'" +        
                    " AND edf_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND edf_ts <= " + "'" + todate  + " 23:59:59" + "'" +
                    " AND edf_site = " + "'" + site + "'" +         
                    " order by edf_id desc ;" ) ;
                    }
                 if (! indoc.isEmpty() && ! tradeid.isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_file  " +
                     " where edf_partner >= " + "'" + tradeid + "'" +
                    " AND edf_partner <= " + "'" + tradeid + "'" +
                    " AND edf_doctype >= " + "'" + indoc + "'" +
                    " AND edf_doctype <= " + "'" + indoc + "'" +        
                    " AND edf_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND edf_ts <= " + "'" + todate  + " 23:59:59" + "'" + 
                    " AND edf_site = " + "'" + site + "'" +         
                    " order by edf_id desc ;" ) ;
                    }
                 if (tradeid.isEmpty() && indoc.isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_file  " +
                    " where edf_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND edf_ts <= " + "'" + todate  + " 23:59:59" + "'" + 
                    " AND edf_site = " + "'" + site + "'" +         
                    " order by edf_id desc ;" ) ;
                    }
                
                 
                    while (res.next()) {
                        
                        if (! outdoc.isBlank() && ! res.getString("edx_outdoctype").equals(outdoc)) {
                        continue;
                    }
                        
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("edf_id"));
                        rowArray.put(res.getString("edf_comkey"));
                        rowArray.put(res.getString("edf_partner"));
                        rowArray.put(res.getString("edf_filetype"));
                        rowArray.put(res.getString("edf_doctype"));
                        rowArray.put(res.getString("edf_ts"));
                        rowArray.put(res.getString("edf_file"));
                        rowArray.put(res.getString("edf_dir"));
                        rowArray.put("find");
                        rowArray.put(res.getString("edf_status"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getEDITransBrowseDetail(String comkey, String idxkey) {
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
            
            try{
                if (idxkey.equals("0")) {
                 res = st.executeQuery("select elg_id, elg_comkey, elg_idxnbr, elg_severity, elg_desc, elg_ts from edi_log " +
                        " where elg_comkey = " + "'" + comkey + "'" +
                        ";");   
                } else {
                 res = st.executeQuery("select elg_id, elg_comkey, elg_idxnbr, elg_severity, elg_desc, elg_ts from edi_log " +
                        " where elg_comkey = " + "'" + comkey + "'" +
                        " and elg_idxnbr = " + "'" + idxkey + "'" +
                        ";");   
                }
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("elg_id"));
                        rowArray.put(res.getString("elg_comkey"));
                        rowArray.put(res.getString("elg_severity"));
                        rowArray.put(res.getString("elg_desc"));
                        rowArray.put(res.getString("elg_ts"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getAPILogView(String apiid, String site, String fromdate, String todate) {
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
            
            try{
                if (apiid.isEmpty()) {
                    res = st.executeQuery("SELECT * FROM api_log  " +
                    " where apil_datetime >= " + "'" + fromdate + "000000" + "'" +
                    " AND apil_datetime <= " + "'" + todate  + "235959" + "'" +
                    " AND apil_site = " + "'" + site + "'" +         
                    " order by apil_logid desc ;" ) ;
                    } else {
                    res = st.executeQuery("SELECT * FROM api_log  " +
                    " where apil_id = " + "'" + apiid + "'" +     
                    " AND apil_datetime >= " + "'" + fromdate + "000000" + "'" +
                    " AND apil_datetime <= " + "'" + todate  + "235959" + "'" +
                    " AND apil_site = " + "'" + site + "'" +        
                    " order by apil_logid desc ;" ) ;    
                    }
                
                 
                    while (res.next()) {
                        
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("apil_logid"));
                        rowArray.put(res.getString("apil_id"));
                        rowArray.put(res.getString("apil_method"));
                        rowArray.put(res.getString("apil_ts"));
                        rowArray.put(res.getString("apil_error"));
                        rowArray.put(res.getString("apil_file"));
                        rowArray.put(res.getString("apil_status"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getAPIBrowseView(String search, String ddtype) {
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
            
            try{
                if (ddtype.equals("URL")) {    
                    res = st.executeQuery("select * " +
                         " from api_mstr where " +
                     " api_url like " + "'%" + search + "%'" + 
                     " order by api_id ;");
                } else {
                    res = st.executeQuery("select * " +
                         " from api_mstr where " +
                     " api_desc like " + "'%" + search + "%'" + 
                     " order by api_id ;");   
                }
                
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("api_id"));
                        rowArray.put(res.getString("api_desc"));
                        rowArray.put(res.getString("api_class"));
                        rowArray.put(res.getString("api_url"));
                        rowArray.put(res.getString("api_port"));
                        rowArray.put(res.getString("api_path"));
                        rowArray.put(res.getString("api_protocol"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getAPIBrowseDetView(String apid) {
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
            
            try{
                res = st.executeQuery("select * " +
                        " from api_det " +
                        " where apid_id = " + "'" + apid + "'" + ";");
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("apid_id"));
                        rowArray.put(res.getString("apid_method"));
                        rowArray.put(res.getString("apid_seq"));
                        rowArray.put(res.getString("apid_verb"));
                        rowArray.put(res.getString("apid_type"));
                        rowArray.put(res.getString("apid_key"));
                        rowArray.put(res.getString("apid_value"));
                        rowArray.put(res.getString("apid_source"));
                        rowArray.put(res.getString("apid_destination"));
                        rowArray.put(res.getString("apid_enabled"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getWKFLogView(String wkfid, String site, String fromdate, String todate) {
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
            
            try{
                 if (wkfid.isEmpty()) {
                    res = st.executeQuery("SELECT * FROM wkf_log  " +
                    " where wkfl_ts >= " + "'" + fromdate + " 00:00:00" + "'" +
                    " AND wkfl_ts <= " + "'" + todate  + " 23:59:59" + "'" + 
                    " order by wkfl_id desc ;" ) ;
                    } else {
                    res = st.executeQuery("SELECT * FROM wkf_log  " +
                    " where wkfl_id = " + "'" + wkfid + "'" +  
                    " order by wkfl_id desc ;" ) ;    
                    }
                 
                    while (res.next()) {
                        
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("wkfl_id"));
                        rowArray.put(res.getString("wkfl_job"));
                        rowArray.put(res.getString("wkfl_desc"));
                        rowArray.put(res.getString("wkfl_ts"));
                        rowArray.put(res.getString("wkfl_ref"));
                        rowArray.put(res.getString("wkfl_messg"));
                        rowArray.put(res.getString("wkfl_status"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getWKFLogDetail(String key) {
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
            
            try{
                res = st.executeQuery("SELECT * FROM wkfd_log  " +     
                    " where wkfdl_parentid = " + "'" + key + "'" +
                    " order by wkfdl_id;" ) ;
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("wkfdl_id"));
                        rowArray.put(res.getString("wkfdl_action"));
                        rowArray.put(res.getString("wkfdl_messg"));
                        rowArray.put(res.getString("wkfdl_status"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    
    public static String getAS2LogView(String as2id, String site, String fromdate, String todate) {
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
            
            try{
                if (as2id.isEmpty()) {
                    res = st.executeQuery("SELECT * FROM as2_log  " +
                    " left outer join as2_mstr on as2_id = as2l_id " +        
                    " where as2l_datetime >= " + "'" + fromdate + "000000" + "'" +
                    " AND as2l_datetime <= " + "'" + todate  + "235959" + "'" + 
                    " AND as2l_site = " + "'" + site + "'" +         
                    " order by as2l_datetime desc ;" ) ;
                    } else {
                    res = st.executeQuery("SELECT * FROM as2_log  " +
                    " left outer join as2_mstr on as2_id = as2l_id " +        
                    " where as2l_id >= " + "'" + as2id + "'" +
                    " AND as2l_id <= " + "'" + as2id + "'" +        
                    " AND as2l_datetime >= " + "'" + fromdate + "000000" + "'" +
                    " AND as2l_datetime <= " + "'" + todate  + "235959" + "'" + 
                    " AND as2l_site = " + "'" + site + "'" +         
                    " order by as2l_datetime desc ;" ) ;    
                    }
                
                 
                    while (res.next()) {
                        
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("as2l_logid"));
                        rowArray.put(res.getString("as2l_id"));
                        rowArray.put(res.getString("as2_desc"));
                        rowArray.put(res.getString("as2l_datetime"));
                        rowArray.put(res.getString("as2l_dir"));
                        rowArray.put(res.getString("as2l_mdn"));
                        rowArray.put(res.getString("as2l_status"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    
    
    public static String getAS2LogDetailDetail(String key) {
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
            
            try{
                res = st.executeQuery("SELECT * FROM as2_log  " +     
                    " where as2l_parent = " + "'" + key + "'" +
                    " order by as2l_logid;" ) ;
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("as2l_logid"));
                        rowArray.put(res.getString("as2l_parent"));
                        rowArray.put(res.getString("as2l_messg"));
                        rowArray.put(res.getString("as2l_status"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getAPILogDetailDetail(String key) {
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
            
            try{
                res = st.executeQuery("SELECT * FROM as2_log  " +     
                    " where as2l_parent = " + "'" + key + "'" +
                    " order by as2l_logid;" ) ;
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("as2l_logid"));
                        rowArray.put(res.getString("as2l_parent"));
                        rowArray.put(res.getString("as2l_messg"));
                        rowArray.put(res.getString("as2l_status"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    
    public static boolean addUpdateEDIMeta(String id, String type, String key, String value) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "addUpdateEDIMeta"});
            list.add(new String[]{"param1", id});
            list.add(new String[]{"param2", type});
            list.add(new String[]{"param3", key});
            list.add(new String[]{"param4", value});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        }
        boolean x = false;
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
                res = st.executeQuery("SELECT edim_value FROM edi_meta where edim_id = " + "'" + id + "'"
                        + " AND edim_type = " + "'" + type + "'"
                        + " AND edim_key = " + "'" + key + "'"     
                        + " ;");
                while (res.next()) {
                    i++;
                }

                if (i == 0) {
                    st.executeUpdate("insert into edi_meta (edim_id, edim_type, edim_key, edim_value) values ( "
                            + "'" + id + "'" + ","
                            + "'" + type + "'" + ","
                            + "'" + key + "'" + ","
                            + "'" + value + "'" + ")"
                            + ";");
                    x = true;
                } else {
                    st.executeUpdate("update edi_meta set "
                            + " edim_value = " + "'" + value + "'"
                            + " where edim_id = " + "'" + id + "'" + " and "
                            + " edim_type = " +  "'" + type + "'" + " and "
                            + " edim_key = " +  "'" + key + "'"  
                            + ";");
                    x = true;
                }
            } // if proceed
            catch (SQLException s) {
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
        return x;
    }

    public static String[] updateEDIExport(String key, String keytype) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "updateEDIExport"});
            list.add(new String[]{"param1", key});
            list.add(new String[]{"param2", keytype});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        String[] x = new String[]{"1",""}; // default failure '1'
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

                if (keytype.equals("invoice")) { 
                st.executeUpdate(" update ship_mstr set sh_export_810 = " + "'0'" +
                                  " where sh_id = " + "'" + key + "'" + ";" );
                x = new String[]{"0","invoice export updated"};
                }
                if (keytype.equals("asn")) { 
                st.executeUpdate(" update ship_mstr set sh_export_856 = " + "'0'" +
                                  " where sh_id = " + "'" + key + "'" + ";" );
                x = new String[]{"0","asn export updated"};
                }
                if (keytype.equals("all")) { 
                st.executeUpdate(" update ship_mstr set sh_export_856 = '0', sh_export_810 = '0' " +
                                  " where sh_id = " + "'" + key + "'" + ";" );
                x = new String[]{"0","all edi export updated for this key"};
                }
            } 
            catch (SQLException s) {
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
        return x;
    }

    public static boolean deleteEDIMeta(String id, String type, String key, String value) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "deleteEDIMeta"});
            list.add(new String[]{"param1", id});
            list.add(new String[]{"param2", type});
            list.add(new String[]{"param3", key});
            list.add(new String[]{"param4", value});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        }
        boolean x = false;
        try {
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            
            try {
                if (value.isBlank()) {
                 st.executeUpdate("delete from edi_meta "
                            + " where edim_id = " + "'" + id + "'" + " and "
                            + " edim_type = " +  "'" + type + "'" + " and "
                            + " edim_key = " +  "'" + key +  ";");   
                } else {
                st.executeUpdate("delete from edi_meta "
                            + " where edim_id = " + "'" + id + "'" + " and "
                            + " edim_type = " +  "'" + type + "'" + " and "
                            + " edim_key = " +  "'" + key + "'" + " and "        
                            + " edim_value = " +  "'" + value + "'"  
                            + ";");
                }
               
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;
    }

    public static boolean addUpdateEDIMetaMulti(ArrayList<String[]> list) {
        boolean x = false;
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
                for (String[] s : list) {  // id, type, key, value
                i = 0;
                
                res = st.executeQuery("SELECT edim_value FROM edi_meta where edim_id = " + "'" + s[0] + "'"
                        + " AND edim_type = " + "'" + s[1] + "'"
                        + " AND edim_key = " + "'" + s[2] + "'"     
                        + " ;");
                while (res.next()) {
                    i++;
                }

                if (i == 0) {
                    st.executeUpdate("insert into edi_meta (edim_id, edim_type, edim_key, edim_value) values ( "
                            + "'" + s[0] + "'" + ","
                            + "'" + s[1] + "'" + ","
                            + "'" + s[2] + "'" + ","
                            + "'" + s[3] + "'" + ")"
                            + ";");
                    x = true;
                } else {
                    st.executeUpdate("update edi_meta set "
                            + " edim_value = " + "'" + s[3] + "'"
                            + " where edim_id = " + "'" + s[0] + "'" + " and "
                            + " edim_type = " +  "'" + s[1] + "'" + " and "
                            + " edim_key = " +  "'" + s[2] + "'"  
                            + ";");
                    x = true;
                }
                } // for loop
            } 
            catch (SQLException s) {
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
        return x;
    }

    
    public static String getEDIMetaValue(String id, String type, String key) {
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
            try {

                res = st.executeQuery("select edim_value from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + " AND " +
                        " edim_type = " + "'" + type + "'" + " AND " +
                        " edim_key = " + "'" + key + "'" +
                        " order by edim_value;" );
               while (res.next()) {
                x = res.getString("edim_value");                    
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
    
    public static ArrayList<String[]> getEDIMetaValueAll(String id, String type) {
         ArrayList<String[]> r = new ArrayList<String[]>();
         
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

                res = st.executeQuery("select edim_key, edim_value from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + " AND " +
                        " edim_type = " + "'" + type + "'" + 
                        " order by edim_key;" );
               while (res.next()) {
                r.add(new String[]{res.getString("edim_key"), res.getString("edim_value")});                    
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
        return r;
        
    }   
    
    public static ArrayList<String[]> getEDIMetaValueAll(String id) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIMetaValueAll"});
            list.add(new String[]{"param1", id});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
        ArrayList<String[]> r = new ArrayList<String[]>();
         
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

                res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + 
                        " order by edim_type;" );
               while (res.next()) {
                r.add(new String[]{res.getString("edim_id"), res.getString("edim_type"), res.getString("edim_key"), res.getString("edim_value")});                    
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
        return r;
        
    }   
    
    public static ArrayList<String[]> getEDIMetaValueDetail(String id, String line) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIMetaValueDetail"});
            list.add(new String[]{"param1", id});
            list.add(new String[]{"param2", line});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }  
        
        ArrayList<String[]> r = new ArrayList<String[]>();
         
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
                line = "detail:" + line;
                
                res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" +  " AND " +
                        " edim_type = " + "'" +  line + "'" +
                        " order by edim_key;" );
               while (res.next()) {
                r.add(new String[]{res.getString("edim_id"), res.getString("edim_type"), res.getString("edim_key"), res.getString("edim_value")});                    
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
        return r;
        
    }   
    
    public static ArrayList<String[]> getEDIMetaValueHeader(String id) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIMetaValueHeader"});
            list.add(new String[]{"param1", id});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }   
        
        ArrayList<String[]> r = new ArrayList<String[]>();
         
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
               
                
                res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" +  " AND " +
                        " not edim_type like 'detail%' "  +
                        " order by edim_key;" );
               while (res.next()) {
                r.add(new String[]{res.getString("edim_id"), res.getString("edim_type"), res.getString("edim_key"), res.getString("edim_value")});                    
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
        return r;
        
    }   
     
    
    public static String[] getEDIMetaValueAsRow(String id, boolean excludedetail) {
         String[] r = null;
         
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            StringBuilder sbh = new StringBuilder();
            StringBuilder sbd = new StringBuilder();
            try {

                if (excludedetail) {
                  res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + " AND " +
                        " not edim_type like 'detail%' " + 
                        " order by edim_type;" );
                } else {
                  res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + 
                        " order by edim_type;" );  
                }
               while (res.next()) {
                sbh.append(res.getString("edim_key")).append(",");
                sbd.append(res.getString("edim_value")).append(",");                   
                }
               
               r = new String[]{sbh.toString(),sbd.toString()};
               
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
        return r;
        
    }   
    
    public static String getEDIMetaValueAsKVString(String id, String datatype, String line) {
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
            StringBuilder sb = new StringBuilder();
            try {

                line = "detail:" + line;
                
                if (datatype.equals("header")) {
                  res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + " AND " +
                        " not edim_type like 'detail%' " + 
                        " order by edim_type;" );
                } else {
                  res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + " AND " +
                        " edim_type = " + "'" + line + "'" + 
                        " order by edim_type;" );  
                }
               while (res.next()) {
                sb.append(res.getString("edim_key")).append("=").append(res.getString("edim_value")).append(":");
                }
               
               if (sb.toString().length() > 0) {
                 r = sb.toString().substring(0, sb.length() - 1); // remove last ":"  
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
        return r;
        
    }   
    
    public static String[] getEDIMetaValueAsKVStringPair(String id, String line) {
         String[] r = new String[]{"",""};
         
         try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            StringBuilder sbh = new StringBuilder();
            StringBuilder sbd = new StringBuilder();
            try {

                line = "detail:" + line;
                
                res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + 
                        " and not edim_type like 'detail%' order by edim_type;" );
                while (res.next()) {
                    sbh.append(res.getString("edim_key")).append("=").append(res.getString("edim_value")).append(":");
                }
                res = st.executeQuery("select * from edi_meta where " +
                        " edim_id = " + "'" + id + "'" + 
                        " and edim_type = " + "'" + line + "'" +
                        " order by edim_type;" );
                while (res.next()) {
                    sbd.append(res.getString("edim_key")).append("=").append(res.getString("edim_value")).append(":");
                }
               
               if (sbh.toString().length() > 0) {
                 r[0] = sbh.toString().substring(0, sbh.length() - 1); // remove last ":"  
               } 
               if (sbd.toString().length() > 0) {
                 r[1] = sbd.toString().substring(0, sbd.length() - 1); // remove last ":"  
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
        return r;
        
    }   
    
    public static ArrayList<String[]> exportInvoices(ArrayList<String> targetlist) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "exportInvoices"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(targetlist);
                return jsonToArrayListStringArray(sendServerPost(list, jsonString, null, "dataServEDI")); 
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList<String[]> results = new ArrayList<>();
        int l_error = 0;
        for (String x : targetlist) {
          l_error = EDI.Create810(x); 
          if (l_error == 0) {
            EDData.updateEDIInvoiceStatus(x);   
          } 
          results.add(new String[]{x, String.valueOf(l_error)});
        }
        // if hanoi is not null
            packageEnvelopes();
        return results;
    }
    
    public static ArrayList<String[]> exportASNs(ArrayList<String> targetlist) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "exportASNs"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(targetlist);
                return jsonToArrayListStringArray(sendServerPost(list, jsonString, null, "dataServEDI")); 
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList<String[]> results = new ArrayList<>();
        int l_error = 0;
        for (String x : targetlist) {
          l_error = EDI.Create856(x); 
          if (l_error == 0) {
            EDData.updateEDIASNStatus(x);     
          } 
          results.add(new String[]{x, String.valueOf(l_error)});
        }
        // if hanoi is not null
            packageEnvelopes();
        return results;
    }
    
    public static ArrayList<String[]> exportACKs(ArrayList<String> targetlist) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "exportACKs"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(targetlist);
                return jsonToArrayListStringArray(sendServerPost(list, jsonString, null, "dataServEDI")); 
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList<String[]> results = new ArrayList<>();
        int l_error = 0;
        for (String x : targetlist) {
          l_error = EDI.Create855(x); 
          if (l_error == 0) {
            EDData.updateEDIOrderStatus(x);       
          } 
          results.add(new String[]{x, String.valueOf(l_error)});
        }
        // if hanoi is not null
            packageEnvelopes();
        return results;
    }
    
    public static ArrayList<String[]> exportPurchaseOrders(ArrayList<String> targetlist) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "exportPurchaseOrders"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(targetlist);
                return jsonToArrayListStringArray(sendServerPost(list, jsonString, null, "dataServEDI")); 
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList<String[]> results = new ArrayList<>();
        int l_error = 0;
        for (String x : targetlist) {
          l_error = EDI.Create850(x); 
          if (l_error == 0) {
            EDData.updateEDIPOStatus(x);       
          } 
          results.add(new String[]{x, String.valueOf(l_error)});
        }
        // if hanoi is not null
            packageEnvelopes();
        return results;
    }
    
    
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
    
    public static boolean isValidFTPid(String id) {
        boolean x = false;
        String sql = "select * from ftp_mstr where ftp_id = ?;";
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
    
    public static boolean isFTPidEnabled(String id) {
        boolean x = false;
        String sql = "select ftp_enabled from ftp_mstr where ftp_id = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
             try (ResultSet res = ps.executeQuery();) {
                while (res.next()) {
                x = res.getBoolean("ftp_enabled");
                } 
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return x;
    }
    
    public static boolean isValidDFSid(String id) {
        boolean x = false;
        String sql = "select * from dfs_mstr where dfs_id = ?;";
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
    
    public static boolean isValidMapid(String id) {
        boolean x = false;
        String sql = "select * from map_mstr where map_id = ? ;";
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
        
    public static boolean isValidEDDid(String id) {
        boolean x = false;
        String sql = "select * from edi_doc where edd_id = ?;";
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
    
    public static boolean isSuppressEmptyTag(String id) {
        boolean x = false;
        String sql = "select * from dfs_mstr where dfs_id = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
             try (ResultSet res = ps.executeQuery();) {
                while (res.next()) {
                x = res.getBoolean("dfs_suppressemptytag");
                } 
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return x;
    }
    
    
    
    public static ArrayList<String> getMapMstrList() {
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
                res = st.executeQuery("select map_id from map_mstr order by map_id ; ");
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
            
    public static ArrayList<String> getMapMstrList(String indoctype) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getMapMstrList"});
            list.add(new String[]{"param1", indoctype});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServEDI")); 
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
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
                if (indoctype.isBlank()) {
                  res = st.executeQuery("select map_id from map_mstr order by map_id; ");  
                } else {
                  res = st.executeQuery("select map_id from map_mstr where map_indoctype = " + "'" + indoctype + "'" + 
                          " order by map_id; ");  
                }
                
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
    
    
    public static ArrayList<String> getMapStructList() {
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
                res = st.executeQuery("select dfs_id from dfs_mstr order by dfs_id ; ");
               while (res.next()) {
                   mylist.add(res.getString("dfs_id"));
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
    
    public static String[] getDFSMstrasArray(String code) {
        
        String[] x = null;
        String sql = "select * from dfs_mstr where dfs_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
               
                    while(res.next()) {
                        x = new String[]{
                        res.getString("dfs_id"),
                        res.getString("dfs_desc"), 
                        res.getString("dfs_version"), 
                        res.getString("dfs_doctype"), 
                        res.getString("dfs_filetype"),
                        res.getString("dfs_delimiter"),
                        res.getString("dfs_misc")};
                    }
               
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return x;
    }
    
    public static ArrayList<String[]> getDFSasArray(String code) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDFSasArray"});
            list.add(new String[]{"param1", code});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServEDI")); 
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        ArrayList<String[]> list = new ArrayList<String[]>();
        String sql = "select * from dfs_det where dfsd_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
               
                    while(res.next()) {
                        String[] x = new String[]{
                        res.getString("dfsd_id"),
                        res.getString("dfsd_segment"), 
                        res.getString("dfsd_parent"), 
                        res.getString("dfsd_loopcount"), 
                        res.getString("dfsd_isgroup"),
                        res.getString("dfsd_islandmark"),
                        res.getString("dfsd_field"),
                        res.getString("dfsd_desc"),
                        res.getString("dfsd_min"),
                        res.getString("dfsd_max"),        
                        res.getString("dfsd_align"),
                        res.getString("dfsd_status"),
                        res.getString("dfsd_type")};
                        list.add(x);
                    }
               
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    public static ArrayList<String> getDSFasString(String code) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDSFasString"});
            list.add(new String[]{"param1", code});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        ArrayList<String> list = new ArrayList<String>();
        String sql = "select * from dfs_det where dfsd_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
               
                    while(res.next()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(res.getString("dfsd_segment"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_parent"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_loopcount"));
                        sb.append(",");
                        sb.append(ConvertIntToYesNo(Integer.valueOf(res.getString("dfsd_isgroup"))));
                        sb.append(",");
                        sb.append(ConvertIntToYesNo(Integer.valueOf(res.getString("dfsd_islandmark"))));
                        sb.append(",");
                        sb.append(res.getString("dfsd_field"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_desc"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_min"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_max"));      
                        sb.append(",");
                        sb.append(res.getString("dfsd_align"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_status"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_type"));
                        list.add(sb.toString());
                    }
               
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    public static ArrayList<String> getDSFasStringBase0(String code) {
        
        ArrayList<String> list = new ArrayList<String>();
        String sql = "select * from dfs_det where dfsd_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
               
                    while(res.next()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(res.getString("dfsd_id"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_segment"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_parent"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_loopcount"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_isgroup"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_islandmark"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_field"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_desc"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_min"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_max"));      
                        sb.append(",");
                        sb.append(res.getString("dfsd_align"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_status"));
                        sb.append(",");
                        sb.append(res.getString("dfsd_type"));
                        list.add(sb.toString());
                    }
               
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    
    
    public static boolean isAPIMethodUnique(String api, String method) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "isAPIMethodUnique"});
            list.add(new String[]{"param1", api});
            list.add(new String[]{"param2", method});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServEDI"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        } 
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
    
    public static ArrayList<String> getAS2Wkfl(String site) {
       ArrayList<String> mylist = new ArrayList<String>();
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
                if (site.toLowerCase().equals("all")) {
                res = st.executeQuery("select as2_id from as2_mstr where as2_enabled = '1' order by as2_id ; ");
                } else {
                 res = st.executeQuery("select as2_id from as2_mstr where as2_enabled = '1' AND " +
                         " as2_site = " + "'" + site + "'" + " order by as2_id ; ");   
                }
                while (res.next()) {
                   mylist.add(res.getString("as2_id"));
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
    
    public static ArrayList<String> getFTPWkfl(String site) {
       ArrayList<String> mylist = new ArrayList<String>();
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
                if (site.toLowerCase().equals("all")) {
                res = st.executeQuery("select ftp_id from ftp_mstr where ftp_enabled = '1' order by ftp_id ; ");
                } else {
                 res = st.executeQuery("select ftp_id from ftp_mstr where ftp_enabled = '1' AND " +
                         " ftp_site = " + "'" + site + "'" + " order by ftp_id ; ");   
                }
                while (res.next()) {
                   mylist.add(res.getString("ftp_id"));
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
         
    
    public static String[] getAS2Info(String id) {
        String[] info = new String[]{"","","","","","","","","","","", "", "", "", "", "", "", "", "", "", "", "", "", ""};
        String sql = "select as2_id, as2_url, as2_port, as2_path, as2_user, as2_sysas2id, edic_as2id, edic_as2url, " +
                " as2_encrypted, as2_signed, as2_enccert, as2_forceencrypted, as2_forcesigned, as2_signcert, as2_protocol, as2_indir, as2_outdir, " +
                " edic_signkey, edic_enckey, as2_encalgo, as2_signalgo, as2_micalgo, as2_contenttype, as2_enabled, as2_site " +
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
               info[5] = res.getString("as2_sysas2id"); // info[5] = res.getString("edic_as2id");
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
               info[16] = res.getString("as2_outdir");
               info[17] = res.getString("as2_indir");
               info[18] = res.getString("as2_encalgo");
               info[19] = res.getString("as2_signalgo");
               info[20] = res.getString("as2_micalgo");
               info[21] = res.getString("as2_contenttype");
               info[22] = res.getString("as2_enabled");
               info[23] = res.getString("as2_site");
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
        String sql = "select as2_id, as2_url, as2_port, as2_path, as2_user, as2_sysas2id, edic_as2id, edic_as2url, " +
                " as2_encrypted, as2_signed, as2_enccert, as2_forceencrypted, as2_forcesigned, as2_signcert, as2_protocol, as2_indir, as2_outdir, " +
                " edic_signkey, edic_enckey, as2_encalgo, as2_signalgo, as2_micalgo, as2_contenttype, as2_enabled, as2_site, as2_signmdn " +
                " from as2_mstr " +
                " inner join edi_ctrl where as2_user = ? and as2_sysas2id = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, sender);
        ps.setString(2, receiver);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               info = new String[24];     
               info[0] = res.getString("as2_id");
               info[1] = res.getString("as2_url");
               info[2] = res.getString("as2_port");
               info[3] = res.getString("as2_path");
               info[4] = res.getString("as2_user");
               info[5] = res.getString("as2_sysas2id"); // info[5] = res.getString("edic_as2id");
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
               info[16] = res.getString("as2_outdir");
               info[17] = res.getString("as2_indir");
               info[18] = res.getString("as2_encalgo");
               info[19] = res.getString("as2_signalgo");
               info[20] = res.getString("as2_micalgo");
               info[21] = res.getString("as2_contenttype");
               info[21] = res.getString("as2_enabled");
               info[22] = res.getString("as2_site");
               info[23] = res.getString("as2_signmdn");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return info;
    }
    
    public static String getEDIXvalue(String doctype, String seg, String ele, String code) {
        String x = "";
        String sql = "select edix_value from edi_xcode where edix_doctype = ? and edix_seg = ? and edix_ele = ? and edix_code = ?";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, doctype);
        ps.setString(2, seg);
        ps.setString(3, ele);
        ps.setString(4, code);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               x = res.getString("edix_value");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return x;
    }
    
    public static String getEDIXcode(String doctype, String seg, String ele, String value) {
        String x = "";
        String sql = "select edix_code from edi_xcode where edix_doctype = ? and edix_seg = ? and edix_ele = ? and edix_code = ?";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, doctype);
        ps.setString(2, seg);
        ps.setString(3, ele);
        ps.setString(4, value);
             try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
               x = res.getString("edix_code");
               }
            }
        }
        catch (SQLException s){
            MainFrame.bslog(s);
        }
        return x;
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
        String[] x = new String[]{"","","","","",""};
        String sql = "select p.pks_storeuser as storeuser, p.pks_file as storefile, p.pks_storepass as storepass, u.pks_user as user, u.pks_pass as pass, u.pks_standard as standard from pks_mstr p inner join pks_mstr u on u.pks_parent = p.pks_id where u.pks_id = ?";
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
               x[5] = res.getString("standard");
               
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
    
    public String[] processWorkFlowID(String id) {
      
       // String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));   
        wkf_mstr wkf = getWkfMstr(id);
        
        // log parent workflow ID
        wkf_log wkfl = new wkf_log(null,
                "", // id
                wkf.wkf_id(),
                wkf.wkf_desc(),
                "", // ts auto assigned
                "", // ref
                "0", // status
                "", // message
                wkf.wkf_site() // site
                );
        
        int logid = writeWFLog(wkfl,0,null); // init log event
        ArrayList<String[]> logdetail = new ArrayList<String[]>();
        
        if (wkf.wkf_enabled().equals("0")) {
         updateWFLog(logid, "error", "Workflow is disabled", "");
         return bsret("Workflow is disabled");
        }
        ArrayList<wkf_det> wkfdetlist = getWkfDet(id);
        if (wkfdetlist == null) {
            updateWFLog(logid, "error", "Null Workflow list", "");
            return bsret("Null Workflow list");
        }
        
        String[] r = new String[]{"",""};
        boolean suppress = false;
        forloop:
        for (wkf_det wkd : wkfdetlist) {
          suppress = false;
          String eventtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));  
          String[] lgd = new String[]{wkd.wkfd_action(), eventtime, "", "", "", wkf.wkf_site()}; // action,time,ref,status,messg,site
          
          JRRT rr = null;
          switch (wkd.wkfd_action()) {
            
            case "EmailDirList" :
                r = wkfaction_emaildirlist(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
                
            case "FileMatchMove" :
                r = wkfaction_filematchmove(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;   
              
            case "Encrypt" :
                r = wkfaction_encryptDir(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
                
            case "Decrypt" :
                r = wkfaction_decryptDir(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;     
            
            case "EncryptFile" :
                r = wkfaction_encryptFile(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
            
            case "DecryptFile" :
                r = wkfaction_decryptFile(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;      
                
            case "APICall" :
                suppress = true;
                rr = wkfaction_apicall(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                if (rr.rarray != null && ! rr.rarray().isEmpty()) {
                    for (String k : rr.rarray()) {
                     logdetail.add(new String[]{wkd.wkfd_action(), eventtime, "", rr.status(), k, wkf.wkf_site()});   
                    }
                }
                if (! rr.status().equals("0")) {
                    break forloop;
                } 
                break;     
              
            case "ScriptCall" :
                rr = wkfaction_scriptcall(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = rr.status();
                lgd[4] = r[1];
                // null rr array
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;     
              
            case "EmailDir" :
                r = wkfaction_emaildirectory(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;    
              
            case "TrafficDir" :
                r = wkfaction_trafficdirectory(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;  
              
            case "X12DirFilter" :
                r = wkfaction_filterdirectory(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
              
            case "FileCopy" :
                r = wkfaction_filecopy(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
                
            case "FileDelete" :
                r = wkfaction_filedelete(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
                
            case "FileMove" :
                r = wkfaction_filemove(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;     
            
                case "FileCopyDir" :
                r = wkfaction_filecopyall(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
                
                case "FileMoveDir" :
                r = wkfaction_filemoveall(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
                
                case "FileDeleteDir" :
                r = wkfaction_filedeleteall(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break; 
                
                case "FileMap" :
                r = wkfaction_filemap(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;
                
                case "EmailFile" :
                r = wkfaction_emailfile(wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;
                
                case "AS2ToTranslate" :
                r = wkfaction_as2ToTranslate(wkf, wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;
                
                case "AS2Outbound" :
                r = wkfaction_as2outbound(wkf, wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;
                
                case "FTPToTranslate" :
                r = wkfaction_ftpToTranslate(wkf, wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = r[0];
                lgd[4] = r[1];
                if (! r[0].equals("0")) {
                    logdetail.add(lgd);
                    break forloop;
                } 
                break;
                
                case "MBToTranslate" :
                suppress = true;
                rr = wkfaction_mbToTranslate(wkf, wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                lgd[3] = rr.status();
                if (! rr.rarray().isEmpty()) {
                    for (String k : rr.rarray()) {
                     logdetail.add(new String[]{wkd.wkfd_action(), eventtime, "", rr.status(), k, wkf.wkf_site()});   
                    }
                }
                if (! rr.status().equals("0")) {
                    break forloop;
                } 
                break;
                
                case "AS2ToEDIIn" :
                suppress = true;    
                rr = wkfaction_as2ToEDIIn(wkf, wkd, getWkfdMeta(wkd.wkfd_id(), wkd.wkfd_line()));
                if (! rr.rarray().isEmpty()) {
                    for (String k : rr.rarray()) {
                     logdetail.add(new String[]{wkd.wkfd_action(), eventtime, "", rr.status(), k, wkf.wkf_site()});   
                    }
                }
                if (! rr.status().equals("0")) {
                    break forloop;
                } 
                break;
                
            default:
                return bsret("Unknown WorkFlow Action! " + " id: " + id + " action: " + wkd.wkfd_action());
          
          }
          if (! suppress) { // added suppress logic due to JRTT (rr) type returns....messages are captured in rr instead of at the end
           logdetail.add(lgd);
          }
        } // forloop
        
            boolean isError = false;
            String statusmessg = "";
            ArrayList<wkfd_log> list = new ArrayList<wkfd_log>();
            for (String[] s : logdetail) {
            wkfd_log x = new wkfd_log(null, 
                "", // detail id
                String.valueOf(logid), // parentid
                s[0], // action
                s[1], // timestamp
                s[2], // ref
                s[3], // status
                s[4], // message
                s[5]
            );
            list.add(x);
            if (! s[3].equals("0")) {
                statusmessg = s[4];
                isError = true;
            }
            } // for each log detail
            
            writeWFLog(wkfl,logid,list);
        
        String status = (isError) ? "1" : "0";
        if (isError && getSysMetaValue("system", "workflow", "sendEmailOnError").equals("1")) {
            String to = OVData.getSysMetaValue("system", "workflow", "errorEmailRecipient");  
            if (! to.isBlank()) {
              String[] creds = getSMTPCredentials();
              if (! creds[1].isBlank()) {
                 sendEmail(to, "BlueSeer WorkFlow Error: " + wkf.wkf_id(), statusmessg, "", false);
              }
            }       
        }
        wkf = null;
        wkfl = null;
        wkfdetlist = null;
        logdetail = null;

        return bsret(status, statusmessg);
    }
    
    public static String[] wkfaction_filterdirectory(wkf_det wkfd, ArrayList<wkfd_meta> list) {
       String[] r = new String[]{"0",""};
        
        String indir = "";
        String outdir = "";
        String archdir = "";
        String logfile = "";
        String doctypes = "";
        String tffile = "";
        String[] doctypearray = null;
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("indir")) {
                indir = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("outdir")) {
                outdir = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("archdir")) {
                archdir = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("logfile")) {
                logfile = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("doctypes")) {
                doctypes = m.wkfdm_value();
                if (! doctypes.isEmpty()) {
                  doctypearray = doctypes.split(",",-1);
                }
            }
            if (m.wkfdm_key().equals("tffile")) {
                tffile = m.wkfdm_value();
            }
        }
        
        Path indirpath = FileSystems.getDefault().getPath(indir);
        if (indir.isEmpty() || ! Files.exists(indirpath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "indir path does not exist "; 
           return r; 
        }
        Path outdirpath = FileSystems.getDefault().getPath(outdir);
        if (outdir.isEmpty() || ! Files.exists(outdirpath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "outdir path does not exist "; 
           return r; 
        }
        Path archdirpath = FileSystems.getDefault().getPath(archdir);
        if (! archdir.isEmpty() && ! Files.exists(archdirpath)) { // archdir can be blank
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "archdir path does not exist "; 
           return r; 
        }
        Path tffilepath = FileSystems.getDefault().getPath(tffile);
        if (tffile.isEmpty() || ! Files.exists(tffilepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "tffile path does not exist "; 
           return r; 
        }
        
        if (doctypearray == null || doctypearray.length == 0) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + " zero or null doc types provided "; 
           return r;  
        }
        
        try {
            r = filterDir(indir, outdir, archdir, logfile, doctypearray, tffile);
        } catch (IOException ex) {
            r[0] = "1";
            r[1] = "IOException occurred: " + ex.getMessage();
        }
        
        return r;
    }
    
    public static String[] wkfaction_trafficdirectory(wkf_det wkfd, ArrayList<wkfd_meta> list) {
       String[] r = new String[]{"0",""};
        
        String indir = "";
        String logfile = "";
        String tffile = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("indir")) {
                indir = m.wkfdm_value();
            }
           
            if (m.wkfdm_key().equals("logfile")) {
                logfile = m.wkfdm_value();
            }
           
            if (m.wkfdm_key().equals("tffile")) {
                tffile = m.wkfdm_value();
            }
        }
        
        Path indirpath = FileSystems.getDefault().getPath(indir);
        if (indir.isEmpty() || ! Files.exists(indirpath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "indir path does not exist "; 
           return r; 
        }
       
       
        Path tffilepath = FileSystems.getDefault().getPath(tffile);
        if (tffile.isEmpty() || ! Files.exists(tffilepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "tffile path does not exist "; 
           return r; 
        }
        
        
        
        try {
            r = trafficDir(indir, logfile, tffile);
        } catch (IOException ex) {
            r[0] = "1";
            r[1] = "IOException occurred: " + ex.getMessage();
        }
        
        return r;
    }
    
    public static String[] wkfaction_emaildirectory(wkf_det wkfd, ArrayList<wkfd_meta> list) {
       String[] r = new String[]{"0",""};
       
        String indir = "";
        String logfile = "";
        String tffile = "";
        String archdir = "";
        String smtpfrom = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("indir")) {
                indir = m.wkfdm_value();
            }
           
            if (m.wkfdm_key().equals("logfile")) {
                logfile = m.wkfdm_value();
            }
           
            if (m.wkfdm_key().equals("tffile")) {
                tffile = m.wkfdm_value();
            }
            
            if (m.wkfdm_key().equals("archdir")) {
                archdir = m.wkfdm_value();
            }
            
            if (m.wkfdm_key().equals("smtpfrom")) {
                smtpfrom = m.wkfdm_value();
            }
        }
        
        
        
        if (smtpfrom.isEmpty()) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "must supply a legitimate from email address "; 
           return r; 
        }
        
        Path indirpath = FileSystems.getDefault().getPath(indir);
        if (indir.isEmpty() || ! Files.exists(indirpath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "indir path does not exist "; 
           return r; 
        }
        
        Path archdirpath = FileSystems.getDefault().getPath(archdir);
        if (archdir.isEmpty() || ! Files.exists(archdirpath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "archdir path does not exist "; 
           return r; 
        }
       
       
        Path tffilepath = FileSystems.getDefault().getPath(tffile);
        if (tffile.isEmpty() || ! Files.exists(tffilepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "tffile path does not exist "; 
           return r; 
        }
        
        
        if (! isSMTPServerBool()) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "Missing SMTP server/auth info "; 
           return r;  
        }
         
        try {
            r = emailDir(indir, logfile, tffile, archdir, smtpfrom);
        } catch (IOException ex) {
            r[0] = "1";
            r[1] = "IOException occurred: " + ex.getMessage();
        }
        
        return r;
    }
    
    public static JRRT wkfaction_scriptcall(wkf_det wkfd, ArrayList<wkfd_meta> list) {
                
        String source = "";
        String[] parameters = null;
        String directory = "";
        String[] commandstring = null; 
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source")) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("parameters")) {
                parameters = m.wkfdm_value().split(",",-1);
            }
            if (m.wkfdm_key().equals("directory")) {
                directory = m.wkfdm_value();
            }
           
        }
        
        
        
        if (parameters != null) {
           commandstring = new String[parameters.length + 1];
           commandstring[0] = source;
           int i = 1;
           for (String s : parameters) {
               commandstring[i] = s;
               i++;
           }
        }
        
      
        
      //  Path sourcepath = FileSystems.getDefault().getPath(source);
        Path directorypath = FileSystems.getDefault().getPath(directory);
        Runtime rt = Runtime.getRuntime();
        Process pr;
        String status = "0";
        String messg = "";
        
        
        try {
            if (! commandstring[0].isBlank()) {
                if (! directory.isBlank() && directorypath.toFile().exists()) {
                    pr = rt.exec(commandstring, null, directorypath.toFile());
                } else {
                    pr = rt.exec(commandstring);
                }
                BufferedReader stdInput = new BufferedReader(
                new InputStreamReader( pr.getInputStream() ));
                String s ;
                StringBuilder sbs = new StringBuilder();
                while ((s = stdInput.readLine()) != null) {
                sbs.append(s);
                sbs.append("\n");
                }
                stdInput.close();
               messg = "script file " + source + " output: " + sbs.toString(); 
            } else {
              status = "1";
              messg = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "unable to execute "; 
            }
            
        } catch (IOException ex) {
            status = "1";
            messg = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
        }
        
        return new JRRT(status, messg, null);
    }
    
    
    public static String[] wkfaction_filecopy(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String source = "";
        String destination = "";
        boolean append = false;
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source")) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination")) {
                destination = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("append")) {
                append = ConvertStringToBool(m.wkfdm_value());
            }
        }
        
        Path sourcepath = FileSystems.getDefault().getPath(source);
        // parse destination filename if contains %% date formatting
        destination = parseFileName(destination);
        Path destinationpath = FileSystems.getDefault().getPath(destination);
       
        try {
            if (append) {
               Files.write(destinationpath, Files.readAllBytes(sourcepath), StandardOpenOption.APPEND, StandardOpenOption.CREATE);  
               r[1] = "Appended file " + sourcepath + " to file: " + destinationpath;
            } else {
               Files.copy(sourcepath, destinationpath, StandardCopyOption.REPLACE_EXISTING);
               r[1] = "Copied file " + sourcepath + " to file: " + destinationpath;
            }
            
        } catch (IOException ex) {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
        }
        
        return r;
    }
    
    public static String[] wkfaction_filedelete(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        String source = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source")) {
                source = m.wkfdm_value();
            }
        }
        
        Path sourcepath = FileSystems.getDefault().getPath(source);
        try {
            Files.deleteIfExists(sourcepath);
            r[1] = "deleted file " + sourcepath.toString();
        } catch (IOException ex) {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
        }
        return r;
    }
    
    public static String[] wkfaction_filemove(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String source = "";
        String destination = "";
        boolean overwrite = false;
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source")) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination")) {
                destination = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("overwrite") && ! m.wkfdm_value.isBlank()) {
                overwrite = ConvertStringToBool(m.wkfdm_value());
            }
        }
        
        Path sourcepath = FileSystems.getDefault().getPath(source);
        
        // parse destination filename if contains %% date formatting
        destination = parseFileName(destination);
        
        Path destinationpath = FileSystems.getDefault().getPath(destination);
        Path dparent = destinationpath.getParent();
        
        if (Files.isDirectory(sourcepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "source path is directory"; 
           return r;
        }
        if (Files.isDirectory(destinationpath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "destination path is directory"; 
           return r;
        }
        
        try {
            if (! overwrite && Files.exists(destinationpath)) {
                destinationpath = FileSystems.getDefault().getPath(dparent + "/" + destinationpath.getFileName() + "." + Long.toHexString(System.currentTimeMillis())); 
                Files.move(sourcepath, destinationpath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(sourcepath, destinationpath, StandardCopyOption.REPLACE_EXISTING); 
            }
            r[1] = "Moved file from " + sourcepath.toString() +  " to " + destinationpath.toString();
        } catch (IOException ex) {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
        }
        
        return r;
    }
    
    public static String[] wkfaction_filecopyall(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String source = "";
        String destination = "";
        String filter = "";
        boolean overwrite = false;
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source dir") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("filter") && ! m.wkfdm_value.isBlank()) {
                filter = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination dir") && ! m.wkfdm_value.isBlank()) {
                destination = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("overwrite") && ! m.wkfdm_value.isBlank()) {
                overwrite = ConvertStringToBool(m.wkfdm_value());
            }
        }
        
        
       
        if (! source.isEmpty() && ! destination.isEmpty()) {
            int count = 0;
        Path sourcepath = FileSystems.getDefault().getPath(source);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath, filter)) {
                int f = 0;
                for (Path path : stream) {
                    if (! Files.isDirectory(path)) {
                        count++;
                    Path destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName());    
                        
                        if (! Files.exists(destinationpath)) {
                            Files.copy(path, destinationpath, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            if (overwrite) {
                            Files.copy(path, destinationpath, StandardCopyOption.REPLACE_EXISTING); 
                            }
                        }
                    }
                }
                r[1] = "Copying " + count +  " files " + " from " + source + " to " + destination;
            } catch (IOException ex) {  
                    r[0] = "1";
                    r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
            }  
        } 
        return r;
    }
    
    public static String[] wkfaction_filemoveall(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String source = "";
        String destination = "";
        String filter = null;
        boolean overwrite = false;
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source dir") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("filter") && ! m.wkfdm_value.isBlank()) {
                filter = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination dir") && ! m.wkfdm_value.isBlank()) {
                destination = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("overwrite") && ! m.wkfdm_value.isBlank()) {
                overwrite = ConvertStringToBool(m.wkfdm_value());
            }
        }
        
        
       
        if (! source.isEmpty() && ! destination.isEmpty()) {
        int count = 0;
        Path sourcepath = FileSystems.getDefault().getPath(source);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath, filter)) {
                int f = 0;
                for (Path path : stream) {
                    if (! Files.isDirectory(path)) {
                        count++;
                        Path destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName());    
                        if (! overwrite && Files.exists(destinationpath)) {
                            destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName() + "." + Long.toHexString(System.currentTimeMillis())); 
                            Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING); 
                        }
                    }
                }
                r[1] = "Moving " + count +  " files " + " from " + source + " to " + destination;
            } catch (IOException ex) {  
                    r[0] = "1";
                    r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
            }  
        } 
        return r;
    }
    
    public static String[] wkfaction_filedeleteall(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String source = "";
        int days = 0;
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source dir") && ! m.wkfdm_value.isBlank()) {
                source = cleanDirString(m.wkfdm_value());
            }
            if (m.wkfdm_key().equals("days") && ! m.wkfdm_value.isBlank()) {
                days = Integer.valueOf(m.wkfdm_value());
            }
           
        }
        
        
        
        File folder = new File(source);
        File[] listOfFiles = folder.listFiles();
        
        long z = System.currentTimeMillis() - ((long)days * 24L * 60L * 60L * 1000L);
        int count = 0;
        int cantcount = 0;
       
        if (! source.isEmpty()) {
        Path sourcepath = FileSystems.getDefault().getPath(source);
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getParentFile().canWrite() && listOfFiles[i].lastModified() < z) {
				   
				    Path filepath = FileSystems.getDefault().getPath(source + listOfFiles[i].getName());
				    
				    try {
						Files.delete(filepath);
						count++;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						cantcount++;
						continue;
					}
				   
				   // System.out.println(listOfFiles[i].getName());
				}
                }
            
            }
        } 
        r[1] = "Deleting " + count + " of " + listOfFiles.length + " files using days back: " + days;
        return r;
    }
    
    public static String[] wkfaction_emailfile(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String filepath = "";
        String smtpfrom = "";
        String smtpto = "";
        String smtpsubject = "";
        boolean deletefile = false;
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("filepath") && ! m.wkfdm_value.isBlank()) {
                filepath = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("smtpfrom") && ! m.wkfdm_value.isBlank()) {
                smtpfrom = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("smtpto") && ! m.wkfdm_value.isBlank()) {
                smtpto = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("smtpsubject") && ! m.wkfdm_value.isBlank()) {
                smtpsubject = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("delete") && ! m.wkfdm_value.isBlank()) {
                deletefile = ConvertStringToBool(m.wkfdm_value());
            }
        }
       
        if (smtpfrom.isEmpty()) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "must supply a legitimate from email address "; 
           return r; 
        }
        
        if (smtpto.isEmpty()) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "must supply a legitimate TO email address "; 
           return r; 
        }
        
        Path vfilepath = FileSystems.getDefault().getPath(filepath);
        if (filepath.isEmpty() || ! Files.exists(vfilepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "file path does not exist "; 
           return r; 
        }
        
        if (! isSMTPServerBool()) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "Missing SMTP server/auth info "; 
           return r;  
        }
        
        sendEmail(smtpfrom, smtpto, smtpsubject, "", filepath);
        try {
            if (deletefile) {
                Files.delete(vfilepath);
            }
        } catch (IOException ex) {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + " unable to delete file after emailing: \n" + ex.getMessage();
        }
        r[1] = "File has been emailed file: " + filepath.toString() + " recipient: " + smtpto;
        return r;
    }
        
    public static String[] wkfaction_filemap(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String source = "";
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source file") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
        }
       
        if (! source.isEmpty()) {
        Path sourcepath = FileSystems.getDefault().getPath(source);
            if (sourcepath.toFile().exists()) {
            r = runTranslationSingleFile(sourcepath);
            } else {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "sourcepath does not exist";
            }
        }
        
        return r;
    }
    
    public static JRRT wkfaction_apicall(wkf_det wkfd, ArrayList<wkfd_meta> list) {
       
        ArrayList<String> logdetails = new ArrayList<String>();
        String apiid = "";
        String apimethod = "";
        String filedest = "";
        String filesrc = "";
        String status = "0";
        String messg = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("api id") && ! m.wkfdm_value.isBlank()) {
                apiid = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("api method") && ! m.wkfdm_value.isBlank()) {
                apimethod = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination") && ! m.wkfdm_value.isBlank()) {
                filedest = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("source") && ! m.wkfdm_value.isBlank()) {
                filesrc = m.wkfdm_value();
            }
        }
       
        Path destinationpath = FileSystems.getDefault().getPath(filedest);
        Path sourcepath = FileSystems.getDefault().getPath(filesrc);
        
        if (apiid.isBlank()) {
           status = "1";
           logdetails.add("ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "api ID is blank"); 
           return new JRRT(status, messg, logdetails);
        }
        if (apimethod.isBlank()) {
           status = "1";
           logdetails.add("ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "apimethod is blank");
           return new JRRT(status, messg, logdetails);
        }
        if (filedest.isBlank()) {
           status = "1";
           logdetails.add("ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + " destination file parameter is empty");
           return new JRRT(status, messg, logdetails);
        }
        
        
        api_mstr api = getAPIMstr(new String[]{apiid});
        api_det apid = getAPIDet(apiid, apimethod);
        
        if (api.m[0].equals("0") && apid.m[0].equals("0")) { 
           String[] r;
           try { 
             r = runAPICall(api, apid, destinationpath, sourcepath);
             logdetails.add(r[1]);
             return new JRRT(r[0], r[1], logdetails);
           } catch (Exception e) {
             logdetails.add("ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + e.getMessage());  
             return new JRRT("1", "error", logdetails); 
           }
        } else {
          status = "1";
          messg = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "unable to get retrieve api/apid JRT"; 
           return new JRRT(status, messg, null);  
        }
    }
    
    public static String[] wkfaction_as2ToTranslate(wkf_mstr wkf, wkf_det wkfd, ArrayList<wkfd_meta> list) {
       
        String messg = "";
        String site = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("site") && ! m.wkfdm_value.isBlank()) {
                site = m.wkfdm_value();
            }
        }
        if (site.equals("*")) {
            site = "all";
        }
        ArrayList<String> as2list = getAS2Wkfl(site);  // list of all as2 that are 'enabled'
        for (String s : as2list) {
        as2_mstr as2 = getAS2Mstr(new String[]{s});
            if (as2.as2_inwkf().equals(wkf.wkf_id)) {  // if as2 ID is assigned this executing workflow id then fire
                File folder = new File(as2.as2_indir());
                File[] listOfFiles = folder.listFiles();
                if (listOfFiles != null) {
                   messg = runEDIForSite(null, wkf.wkf_site(), listOfFiles); 
                }
            }
        }
        return new String[]{"0", messg};  // overall...workflow suceeds even if individual internal actions do not...will be logged regardless
    }
    
    public static JRRT wkfaction_as2ToEDIIn(wkf_mstr wkf, wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        String messg = "";
        String site = "";
        String source = "";
        String destination = "";
        String archivedir = "";
        
        ArrayList<String> logdetails = new ArrayList<String>();
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("site") && ! m.wkfdm_value.isBlank()) {
                site = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination") && ! m.wkfdm_value.isBlank()) {
                destination = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("archivedir") && ! m.wkfdm_value.isBlank()) {
                archivedir = m.wkfdm_value();
            }
        }
        if (site.equals("*")) {
            site = "all";
        }
        
        int count = 0;
        
      //  ArrayList<String[]> mblist = getEDIPartners(edptype);  // list of all internal edi partners that are 'enabled'
        ArrayList<String> as2list = getAS2Wkfl(site);  // list of all as2 that are 'enabled'
        for (String s : as2list) {
            as2_mstr as2 = getAS2Mstr(new String[]{s});
            
            
           // System.out.println(as2.as2_inwkf() + " / " + wkf.wkf_id + " / " + as2.as2_indir());
           if (as2.as2_inwkf().equals(wkf.wkf_id)) {  // if as2 ID is assigned this executing workflow id then fire
                
               source = as2.as2_indir();
               
                count = 0;
                if (! source.isEmpty() && ! destination.isEmpty()) {
                Path sourcepath = FileSystems.getDefault().getPath(source);
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath, "*")) {
                        int f = 0;
                        for (Path path : stream) {
                            if (! Files.isDirectory(path)) {
                                count++;
                                Path destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName()); 
                                
                                // archive before move if archivedir is not blank
                                if (! archivedir.isBlank()) {
                                 Path archivefilepath = FileSystems.getDefault().getPath(archivedir + "/" + path.getFileName());  
                                 if (Files.isDirectory(FileSystems.getDefault().getPath(archivedir))) { // validate existing archivedir
                                    Files.copy(path, archivefilepath, StandardCopyOption.REPLACE_EXISTING);
                                 }
                                }
                                if (Files.exists(destinationpath)) {
                                    destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName() + "." + Long.toHexString(System.currentTimeMillis())); 
                                    Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING);
                                } else {
                                    Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING); 
                                }
                            }
                        }
                        logdetails.add("AS2id= " + as2.as2_id() + " Moving " + count +  " files " + " from " + source + " to " + destination);
                    } catch (IOException ex) {  
                            r[0] = "1";
                            logdetails.add("ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage());
                    }  
                }
            }
        }
        return new JRRT(r[0], messg, logdetails);
      //  return new String[]{"0", messg};  // overall...workflow suceeds even if individual internal actions do not...will be logged regardless
    }
    
    public static String[] wkfaction_ftpToTranslate(wkf_mstr wkf, wkf_det wkfd, ArrayList<wkfd_meta> list) {
       
        String[] r = new String[]{"0",""};
        String site = "";
        String direction = "";
        String filter = null;
        String destination = cleanDirString(EDData.getEDIInDir()); 
        String source = "";
        
        String to = OVData.getSysMetaValue("system", "edimail", "1000");  // system site
        String sendmail = OVData.getSysMetaValue("system", "emailkey", "edimail");
        String[] creds = getSMTPCredentials();
        
        
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("site") && ! m.wkfdm_value.isBlank()) {
                site = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("direction") && ! m.wkfdm_value.isBlank()) {
                direction = m.wkfdm_value();
            }
        }
        if (site.equals("*")) {
            site = "all";
        }
        ArrayList<String> ftplist = getFTPWkfl(site);  // list of all as2 that are 'enabled'
        for (String s : ftplist) {
        ftp_mstr ftp = getFTPMstr(new String[]{s});
            if (direction.equals("out")) {  
             source = ftp.ftp_outdir();
            } else {
             source = ftp.ftp_indir();   
            }
            int count = 0;
            
                if (! source.isEmpty() && ! destination.isEmpty()) {
                
                Path sourcepath = FileSystems.getDefault().getPath(source);
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath, "*")) {
                        int f = 0;
                        for (Path path : stream) {
                            if (! Files.isDirectory(path)) {
                                count++;
                                Path destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName());    
                                if (Files.exists(destinationpath)) {
                                    destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName() + "." + Long.toHexString(System.currentTimeMillis())); 
                                    Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING);
                                } else {
                                    Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING); 
                                }
                            }
                        }
                        r[1] = "FTPid= " + ftp.ftp_id() + "  Moving " + count +  " files " + " from " + source + " to " + destination;
                    } catch (IOException ex) {  
                            r[0] = "1";
                            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
                    }  
                }
                
                if (count > 0 && sendmail.equals("1")) {
                    if (! to.isBlank() && ! creds[1].isBlank()) {
                      sendEmail(to, " BlueSeer sysmail wkfaction_ftpToTranslate: ", r[1], "", false);
                    }
                }
            
        }
        return r;  // overall...workflow suceeds even if individual internal actions do not...will be logged regardless
    }
    
    public static JRRT wkfaction_mbToTranslate(wkf_mstr wkf, wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        String messg = "";
        String site = "";
        String edptype = "";
        String source = "";
        String destination = "";
        ArrayList<String> logdetails = new ArrayList<String>();
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("site") && ! m.wkfdm_value.isBlank()) {
                site = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("type") && ! m.wkfdm_value.isBlank()) {
                edptype = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination") && ! m.wkfdm_value.isBlank()) {
                destination = m.wkfdm_value();
            }
        }
        if (site.equals("*")) {
            site = "all";
        }
        
        int count = 0;
        
        ArrayList<String[]> mblist = getEDIPartners(edptype);  // list of all internal edi partners that are 'enabled'
        for (String[] s : mblist) {
            if (s[5].equals(wkf.wkf_id) && s[7].equals("1")) {  // if edp_outwkfl = current workflow && edp_outenabled is true
                source = s[3];
                count = 0;
                if (! source.isEmpty() && ! destination.isEmpty()) {
                Path sourcepath = FileSystems.getDefault().getPath(source);
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath, "*")) {
                        int f = 0;
                        for (Path path : stream) {
                            if (! Files.isDirectory(path)) {
                                count++;
                                Path destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName());    
                                if (Files.exists(destinationpath)) {
                                    destinationpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName() + "." + Long.toHexString(System.currentTimeMillis())); 
                                    Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING);
                                } else {
                                    Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING); 
                                }
                            }
                        }
                        logdetails.add("Moving " + count +  " files " + " from " + source + " to " + destination);
                    } catch (IOException ex) {  
                            r[0] = "1";
                            logdetails.add("ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage());
                    }  
                }
            }
        }
        return new JRRT(r[0], messg, logdetails);
      //  return new String[]{"0", messg};  // overall...workflow suceeds even if individual internal actions do not...will be logged regardless
    }
     
    public static String[] wkfaction_as2outbound(wkf_mstr wkf, wkf_det wkfd, ArrayList<wkfd_meta> list) {
       StringBuilder messg = new StringBuilder();
        String site = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("site") && ! m.wkfdm_value.isBlank()) {
                site = m.wkfdm_value();
            }
        }
        if (site.equals("*")) {
            site = "all";
        }
        ArrayList<String> as2list = getAS2Wkfl(site);  // list of all as2 that are 'enabled'
        messg.append("processing as2 id: ");
        for (String s : as2list) {
        as2_mstr as2 = getAS2Mstr(new String[]{s});
        
            if (as2.as2_outwkf().equals(wkf.wkf_id) && as2.as2_enabled().equals("1")) {  // if as2 ID is assigned this executing workflow id then fire
                try {
                    messg.append(as2.as2_id).append(", ");
                    apiUtils.postAS2(as2.as2_id, false);
                    //File folder = new File(as2.as2_outdir());
                    //File[] listOfFiles = folder.listFiles();
                } catch (IOException ex) {
                    bslog(ex);
                } catch (CertificateException ex) {
                    bslog(ex);
                } catch (NoSuchProviderException ex) {
                    bslog(ex);
                } catch (KeyStoreException ex) {
                    bslog(ex);
                } catch (NoSuchAlgorithmException ex) {
                    bslog(ex);
                } catch (UnrecoverableKeyException ex) {
                    bslog(ex);
                } catch (CMSException ex) {
                    bslog(ex);
                } catch (SMIMEException ex) {
                    bslog(ex);
                } catch (Exception ex) {
                    bslog(ex);
                }
            }
        }
        return new String[]{"0", messg.toString()};  // overall...workflow suceeds even if individual internal actions do not...will be logged regardless
    }
    
    public static String[] wkfaction_encryptDir(wkf_det wkfd, ArrayList<wkfd_meta> list) {
         String[] r = new String[]{"0",""};
        
        String keyid = "";
        String destination = "";
        String source = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("key id") && ! m.wkfdm_value.isBlank()) {
                keyid = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("source dir") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination dir") && ! m.wkfdm_value.isBlank()) {
                destination = m.wkfdm_value();
            }
        }
        
        if (! isValidKeyID(keyid)) {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "unknown PKS key id: " + keyid;
            return r;  
        }
        
        Path sourcepath = FileSystems.getDefault().getPath(source);
        Path destinationpath = FileSystems.getDefault().getPath(destination);
        
        if (! Files.isDirectory(sourcepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "invalid source path: " + source; 
           return r;
        }
        
        if (! Files.isDirectory(destinationpath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "invalid destination path: " + destination; 
           return r;
        }
        
        int count = 0;
       
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath, "*")) {
                int f = 0;
                for (Path path : stream) {
                    if (! Files.isDirectory(path)) {
                        count++;
                        byte[] indata = Files.readAllBytes(path);
                        BlueSeerUtils.bsr x = apiUtils.encryptFile(indata, keyid);
                        if (x.data() != null) {
                            Path outpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName() + ".enc");    
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outpath.toFile()));
                            bos.write(x.data());
                            bos.flush();
                            bos.close();
                        } else {
                            return x.m();   
                        }
                    }
                }
                r[1] = "encrypting " + count +  " files " + " from " + source + " to " + destination;
            } catch (IOException ex) {  
                    r[0] = "1";
                    r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
            }  
        
        return r;
    }
    
    public static String[] wkfaction_encryptFile(wkf_det wkfd, ArrayList<wkfd_meta> list) {
         String[] r = new String[]{"0",""};
        
        String keyid = "";
        boolean overwrite = false;
        String source = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("key id") && ! m.wkfdm_value.isBlank()) {
                keyid = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("source file") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("overwrite") && ! m.wkfdm_value.isBlank()) {
                overwrite = ConvertStringToBool(m.wkfdm_value());
            }
        }
        
        if (! isValidKeyID(keyid)) {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "unknown PKS key id: " + keyid;
            return r;  
        }
        
        Path sourcepath = FileSystems.getDefault().getPath(source);
        
        if (! Files.exists(sourcepath) || Files.isDirectory(sourcepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "invalid source file " + source; 
           return r;
        }
            
        Path dparent = sourcepath.getParent();
        Path outpath = FileSystems.getDefault().getPath(dparent + "/" + sourcepath.getFileName() + ".enc");
        
            try {
                int f = 0;
               
                byte[] indata = Files.readAllBytes(sourcepath);
                BlueSeerUtils.bsr x = apiUtils.encryptFile(indata, keyid);
                if (x.data() != null) {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outpath.toFile()));
                    bos.write(x.data());
                    bos.flush();
                    bos.close();
                    
                    if (overwrite && Files.exists(outpath)) {
                        Files.move(outpath, sourcepath, StandardCopyOption.REPLACE_EXISTING); 
                    }
                    
                } else {
                    return x.m();   
                }
                
                r[1] = "encrypting  file " + " from " + sourcepath + " to " + outpath + " with overwrite = " + String.valueOf(overwrite);
            } catch (IOException ex) {  
                    r[0] = "1";
                    r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
            }  
        
        return r;
    }
        
    public static String[] wkfaction_decryptDir(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String keyid = "";
        String destination = "";
        String source = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("key id") && ! m.wkfdm_value.isBlank()) {
                keyid = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("source dir") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination dir") && ! m.wkfdm_value.isBlank()) {
                destination = m.wkfdm_value();
            }
        }
        
        if (! isValidKeyID(keyid)) {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "unknown PKS key id: " + keyid;
            return r;  
        }
        
        Path sourcepath = FileSystems.getDefault().getPath(source);
        Path destinationpath = FileSystems.getDefault().getPath(destination);
        
        if (! Files.isDirectory(sourcepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "invalid source path: " + source; 
           return r;
        }
        
        if (! Files.isDirectory(destinationpath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "invalid destination path: " + destination; 
           return r;
        }
        
        int count = 0;
       
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath, "*")) {
                int f = 0;
                for (Path path : stream) {
                    if (! Files.isDirectory(path)) {
                        count++;
                        byte[] indata = Files.readAllBytes(path);
                        
                        BlueSeerUtils.bsr x = apiUtils.decryptFile(indata, keyid);
                        if (x.data() != null) {
                            Path outpath = FileSystems.getDefault().getPath(destination + "/" + path.getFileName() + ".dec");    
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outpath.toFile()));
                            bos.write(x.data());
                            bos.flush();
                            bos.close();
                        } else {
                            return x.m();   
                        }
                    }
                }
                r[1] = "decrypting " + count +  " files " + " from " + source + " to " + destination;
            } catch (IOException ex) {  
                    r[0] = "1";
                    r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
            }  
        
        return r;
    }
    
    public static String[] wkfaction_decryptFile(wkf_det wkfd, ArrayList<wkfd_meta> list) {
         String[] r = new String[]{"0",""};
        
        String keyid = "";
        boolean overwrite = false;
        String source = "";
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("key id") && ! m.wkfdm_value.isBlank()) {
                keyid = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("source file") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("overwrite") && ! m.wkfdm_value.isBlank()) {
                overwrite = ConvertStringToBool(m.wkfdm_value());
            }
        }
        
        if (! isValidKeyID(keyid)) {
            r[0] = "1";
            r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "unknown PKS key id: " + keyid;
            return r;  
        }
        
        Path sourcepath = FileSystems.getDefault().getPath(source);
        
        if (! Files.exists(sourcepath) || Files.isDirectory(sourcepath)) {
           r[0] = "1";
           r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + "invalid source file " + source; 
           return r;
        }
            
        Path dparent = sourcepath.getParent();
        Path outpath = FileSystems.getDefault().getPath(dparent + "/" + sourcepath.getFileName() + ".dec");
        
            try {
                int f = 0;
               
                byte[] indata = Files.readAllBytes(sourcepath);
                BlueSeerUtils.bsr x = apiUtils.decryptFile(indata, keyid);
                if (x.data() != null) {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outpath.toFile()));
                    bos.write(x.data());
                    bos.flush();
                    bos.close();
                    
                    if (overwrite && Files.exists(outpath)) {
                        Files.move(outpath, sourcepath, StandardCopyOption.REPLACE_EXISTING); 
                    }
                    
                } else {
                    return x.m();   
                }
                
                r[1] = "decrypting file " + " from " + sourcepath + " to " + outpath + " with overwrite = " + String.valueOf(overwrite);
            } catch (IOException ex) {  
                    r[0] = "1";
                    r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
            }  
        
        return r;
    }
        
    public static String[] wkfaction_filematchmove(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String source = "";
        String destination = "";
        String destinationFileName = "";
        String filter = null;
        boolean overwrite = false;
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source dir") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("filter") && ! m.wkfdm_value.isBlank()) {
                filter = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination dir") && ! m.wkfdm_value.isBlank()) {
                destination = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("destination filename") && ! m.wkfdm_value.isBlank()) {
                destinationFileName = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("overwrite") && ! m.wkfdm_value.isBlank()) {
                overwrite = ConvertStringToBool(m.wkfdm_value());
            }
        }
        
        // parse filename if contains %% date formatting
        destinationFileName = parseFileName(destinationFileName);
       
        if (! source.isEmpty() && ! destination.isEmpty() && ! destinationFileName.isEmpty()) {
        int count = 0;
        Path sourcepath = FileSystems.getDefault().getPath(source);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath, filter)) {
                int f = 0;
                for (Path path : stream) {
                    if (! Files.isDirectory(path)) {
                        count++;
                        Path destinationpath = FileSystems.getDefault().getPath(destination + "/" + destinationFileName);    
                        if (! overwrite && Files.exists(destinationpath)) {
                            destinationpath = FileSystems.getDefault().getPath(destination + "/" + destinationFileName + "." + Long.toHexString(System.currentTimeMillis())); 
                            Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            Files.move(path, destinationpath, StandardCopyOption.REPLACE_EXISTING); 
                        }
                    }
                }
                r[1] = "Moving " + count +  " files " + " from " + source + " to " + destination + " as " + destinationFileName;
            } catch (IOException ex) {  
                    r[0] = "1";
                    r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
            }  
        } 
        return r;
    }
    
    public static String[] wkfaction_emaildirlist(wkf_det wkfd, ArrayList<wkfd_meta> list) {
        String[] r = new String[]{"0",""};
        
        String source = "";
        String smtpfrom = "";
        String smtpto = "";
        StringBuilder subject = new StringBuilder();
        StringBuilder body = new StringBuilder();
        
        for (wkfd_meta m : list) {
            if (m.wkfdm_key().equals("source dir") && ! m.wkfdm_value.isBlank()) {
                source = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("smtpfrom") && ! m.wkfdm_value.isBlank()) {
                smtpfrom = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("smtpto") && ! m.wkfdm_value.isBlank()) {
                smtpto = m.wkfdm_value();
            }
            if (m.wkfdm_key().equals("subject") && ! m.wkfdm_value.isBlank()) {
                subject.append(m.wkfdm_value());
            }
        }
        
        
       
        if (! source.isEmpty() && ! smtpfrom.isEmpty() && ! smtpto.isEmpty()) {
        int count = 0;
        Path sourcepath = FileSystems.getDefault().getPath(source);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcepath)) {
                int f = 0;
                for (Path path : stream) {
                    if (! Files.isDirectory(path)) {
                        count++;   
                        body.append(path.toString()).append("\n");                        
                    }
                }
                subject.append(" filecount: ").append(String.valueOf(count));
                sendEmail(smtpfrom, smtpto, subject.toString(), body.toString(), ""); 
                r[1] = "Emailing Dir listing count: " + count +  " files " + " from " + smtpfrom + " to " + smtpto;
            } catch (IOException ex) {  
                    r[0] = "1";
                    r[1] = "ERROR WorkFlowID: " + wkfd.wkfd_id + " action: " + wkfd.wkfd_action + "->"  + ex.getMessage();
            }  
        } 
        return r;
    }
    
    
    public record edi_xref(String[] m, String exr_bsgs, String exr_tpaddr, String exr_bsaddr,
        String exr_tpgs, String exr_type, String exr_site ) {
        public edi_xref(String[] m) {
            this(m, "", "", "", "", "", "");
        }
    }
    
    public record edp_partner(String[] m, String edp_id, String edp_desc, String edp_site,
        String edp_type, String edp_defoutdir, String edp_defindir, String edp_outwkfl, String edp_inwkfl,
        String edp_outenabled, String edp_inenabled ) {
        public edp_partner(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record edpd_partner(String[] m, String edpd_parent, String edpd_alias, String edpd_default ) {
        public edpd_partner(String[] m) {
            this(m, "", "", "");
        }
    }
    
    public record EDIPartnerSet(String[] m, edp_partner edp, ArrayList<edpd_partner> edpd) {
        public EDIPartnerSet(String[] m) {
            this (m, null, null);
        }
    }
    
    public record DFSSet(String[] m, dfs_mstr dfs, ArrayList<dfs_det> dfsd) {
        public DFSSet(String[] m) {
            this (m, null, null);
        }
    }
    
    public record WorkFlowSet(String[] m, wkf_mstr wkf, ArrayList<wkf_det> wkfd, ArrayList<wkfd_meta> wkfm) {
        public WorkFlowSet(String[] m) {
            this (m, null, null, null);
        }
    }
    
    
    public record edi_doc(String[] m, String edd_id, String edd_desc, String edd_type,
        String edd_subtype, String edd_segdelim, String edd_eledelim, String edd_priority, String edd_landmark,
        String edd_enabled) {
        public edi_doc(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record edi_docdet(String[] m, String edid_id, String edid_role, String edid_rectype,
        String edid_valuetype, String edid_row, String edid_col, String edid_length, String edid_regex,
        String edid_value, String edid_tag, String edid_xpath, String edid_enabled) {
        public edi_docdet(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "");
        }
    }
    
    public record EDIDocSet(String[] m, edi_doc edd, ArrayList<edi_docdet> edid) {
        public EDIDocSet(String[] m) {
            this (m, null, null);
        }
    }
    
   
    public record edi_mstr(String[] m, String edi_id, String edi_doc, String edi_sndisa, String edi_sndq,
        String edi_sndgs, String edi_map, String edi_eledelim, String edi_segdelim, String edi_subdelim,
        String edi_fileprefix, String edi_filesuffix, String edi_filepath, String edi_version, String edi_rcvisa, String edi_rcvgs,
        String edi_rcvq, String edi_supcode, String edi_doctypeout, String edi_filetypeout, String edi_ifs, String edi_ofs, 
        String edi_filetype, String edi_fa_required, String edi_envelopeall, String edi_una, String edi_ung,
        String edi_site, String edi_mflag) {
        public edi_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "");
        }
    }
   
    public record map_mstr(String[] m, String map_id, String map_desc, String map_version,
        String map_ifs, String map_ofs, String map_indoctype, String map_infiletype ,
        String map_outdoctype, String map_outfiletype, String map_source, String map_package, String map_internal, String map_site ) {
        public map_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", "", "","");
        }
        public map_mstr(String id) {
            this(null, id, "", "", "", "", "", "", "", "", "", "", "","");
        }
    }
    
    public record dfs_mstr(String[] m, String dfs_id, String dfs_desc, String dfs_version, String dfs_doctype, 
        String dfs_filetype, String dfs_delimiter, String dfs_misc, String dfs_suppressemptytag, String dfs_suppressroot,
        String dfs_wraparray) {
        public dfs_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "");
        }
        public dfs_mstr(String id) {
            this(null, id, "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record dfs_det(String[] m, String dfsd_id, String dfsd_segment, String dfsd_parent, String dfsd_loopcount, 
        String dfsd_isgroup, String dfsd_islandmark, String dfsd_field, String dfsd_desc, String dfsd_min,
        String dfsd_max, String dfsd_align, String dfsd_status, String dfsd_type) {
        public dfs_det(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "");
        }
    }
    
    public record wkf_mstr(String[] m, String wkf_id, String wkf_desc, String wkf_enabled, String wkf_site) {
        public wkf_mstr(String[] m) {
            this(m, "", "", "", "");
        }
    }
    
    public record wkf_det(String[] m, String wkfd_id, String wkfd_action, String wkfd_line) {
        public wkf_det(String[] m) {
            this(m, "", "", "");
        }
    }
    
    public record wkfd_meta(String[] m, String wkfdm_id, String wkfdm_line, String wkfdm_key, String wkfdm_value) {
        public wkfd_meta(String[] m) {
            this(m, "", "", "", "");
        }
    }
    
    public record wkf_log(String[] m, String wkfl_id, String wkfl_job, String wkfl_desc, String wkfl_ts,
        String wkfl_ref, String wkfl_status, String wkfl_messg, String wkfl_site ) {
        public wkf_log(String[] m) {
            this(m, "", "", "", "", "", "", "", "");
        }
    }
    
    public record wkfd_log(String[] m, String wkfdl_id, String wkfdl_parentid, String wkfdl_action, String wkfdl_ts,
        String wkfdl_ref, String wkfdl_status, String wkfdl_messg, String wkfdl_site ) {
        public wkfd_log(String[] m) {
            this(m, "", "", "", "", "", "", "", "");
        }
    }
    
    public record edi_ctrl(String[] m, String edic_indir, String edic_outdir, String edic_scriptdir,
        String edic_inarch, String edic_outarch, String edic_batch, String edic_structure, String edic_errordir,
        String edic_mapdir, String edic_archyesno, String edic_delete, String edic_tpid, String edic_gsid,
        String edic_as2id, String edic_as2url, String edic_signkey, String edic_enckey, String edic_varchar) {
        public edi_ctrl(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "");
        }
    }
    
    public record as2_mstr(String[] m, String as2_id, String as2_desc, String as2_version,
        String as2_url, String as2_port, String as2_path, String as2_user ,
        String as2_pass, String as2_key, String as2_protocol, String as2_class,
        String as2_indir, String as2_outdir, String as2_encrypted, String as2_signed, String as2_enccert,
        String as2_forceencrypted, String as2_forcesigned, String as2_signcert,
        String as2_encalgo, String as2_signalgo, String as2_micalgo, String as2_contenttype, 
        String as2_enabled, String as2_sysas2id, String as2_site, String as2_inwkf, String as2_outwkf,
        String as2_sysenccert, String as2_syssigncert, String as2_syscert_bool, String as2_signmdn, String as2_flatmdn, String as2_eol) {
        public as2_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", 
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "");
        }
    }
    
    public record api_mstr(String[] m, String api_id, String api_desc, String api_version,
        String api_url, String api_port, String api_path, String api_user,
        String api_pass, String api_key, String api_keylabel, String api_protocol, String api_class,
        String api_encrypted, String api_signed, String api_contenttype, String api_auth,
        String api_char1, String api_char2, String api_char3, String api_notes) {
        public api_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", 
                    "", "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record api_det(String[] m, String apid_id, String apid_method, int apid_seq,
        String apid_verb, String apid_type, String apid_path, String apid_key,
         String apid_value, String apid_source, String apid_destination, String apid_enabled,
         String apid_char1, String apid_char2, String apid_char3) {
        public api_det(String[] m) {
            this(m, "", "", 0, "", "", "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record apid_meta(String[] m, String apidm_id, String apidm_method, String apidm_key, String apidm_value, String apidm_httphead) {
        public apid_meta(String[] m) {
            this(m, "", "", "", "", "");
        }
    }
    
    public record api_log(String[] m, String apil_logid, String apil_comkey, String apil_idxnbr, String apil_id, String apil_method,
        String apil_status, String apil_error, String apil_ts, String apil_file, String apil_mdn, String apil_site ) {
        public api_log(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "");
        }
    }
    
    
    
    public record JRRT(String status, String messg, ArrayList<String> rarray) {};
    
    public record jsonRecord(ObjectNode on, boolean isArray) {}
    
    
}
