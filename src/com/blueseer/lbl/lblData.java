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
package com.blueseer.lbl;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.xNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import org.json.JSONArray;

/**
 *
 * @author terryva
 */
public class lblData {
    
    
    public static String[] addLabelMstr(label_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from label_mstr where lbl_id = ?";
        String sqlInsert = "insert into label_mstr (lbl_id, lbl_item, lbl_custitem, lbl_id_str, lbl_conttype, lbl_name, lbl_qty, lbl_po, "
                        + "lbl_billto, lbl_order, lbl_line, lbl_ref, lbl_lot, lbl_parent, lbl_parent_str, "
                        + "lbl_addrcode, lbl_addrname, lbl_addr1, lbl_addr2, lbl_addrcity, lbl_addrstate, lbl_addrzip, lbl_addrcountry, "
                        + "lbl_crt_date, lbl_ship_date, lbl_userid, lbl_printer, lbl_prog, lbl_site, lbl_loc, lbl_trantype, lbl_type, lbl_shipto)  " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.lbl_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.lbl_id);
            psi.setString(2, x.lbl_item);
            psi.setString(3, x.lbl_custitem);
            psi.setString(4, x.lbl_id_str);
            psi.setString(5, x.lbl_conttype);
            psi.setString(6, x.lbl_name);
            psi.setString(7, x.lbl_qty);
            psi.setString(8, x.lbl_po);
            psi.setString(9, x.lbl_billto);
            psi.setString(10, x.lbl_order);
            psi.setString(11, x.lbl_line);
            psi.setString(12, x.lbl_ref);
            psi.setString(13, x.lbl_lot);
            psi.setString(14, x.lbl_parent);
            psi.setString(15, x.lbl_parent_str);
            psi.setString(16, x.lbl_addrcode);
            psi.setString(17, x.lbl_addrname);
            psi.setString(18, x.lbl_addr1);
            psi.setString(19, x.lbl_addr2);
            psi.setString(20, x.lbl_addrcity);
            psi.setString(21, x.lbl_addrstate);
            psi.setString(22, x.lbl_addrzip);
            psi.setString(23, x.lbl_addrcountry);
            psi.setString(24, x.lbl_crt_date);
            psi.setString(25, x.lbl_ship_date);
            psi.setString(26, x.lbl_userid);
            psi.setString(27, x.lbl_printer);
            psi.setString(28, x.lbl_prog);
            psi.setString(29, x.lbl_site);
            psi.setString(30, x.lbl_loc);
            psi.setString(31, x.lbl_trantype);
            psi.setString(32, x.lbl_type);
            psi.setString(33, x.lbl_shipto);
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
    
    private static int _addLabelMstr(label_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from label_mstr where lbl_id = ?";
        String sqlInsert = "insert into label_mstr (lbl_id, lbl_item, lbl_custitem, lbl_id_str, lbl_conttype, lbl_name, lbl_qty, lbl_po, "
                        + "lbl_billto, lbl_order, lbl_line, lbl_ref, lbl_lot, lbl_parent, lbl_parent_str, "
                        + "lbl_addrcode, lbl_addrname, lbl_addr1, lbl_addr2, lbl_addrcity, lbl_addrstate, lbl_addrzip, lbl_addrcountry, "
                        + "lbl_crt_date, lbl_ship_date, lbl_userid, lbl_printer, lbl_prog, lbl_site, lbl_loc, lbl_trantype, lbl_type, lbl_shipto)  " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.lbl_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
           if (! res.isBeforeFirst()) {
            ps.setString(1, x.lbl_id);
            ps.setString(2, x.lbl_item);
            ps.setString(3, x.lbl_custitem);
            ps.setString(4, x.lbl_id_str);
            ps.setString(5, x.lbl_conttype);
            ps.setString(6, x.lbl_name);
            ps.setString(7, x.lbl_qty);
            ps.setString(8, x.lbl_po);
            ps.setString(9, x.lbl_billto);
            ps.setString(10, x.lbl_order);
            ps.setString(11, x.lbl_line);
            ps.setString(12, x.lbl_ref);
            ps.setString(13, x.lbl_lot);
            ps.setString(14, x.lbl_parent);
            ps.setString(15, x.lbl_parent_str);
            ps.setString(16, x.lbl_addrcode);
            ps.setString(17, x.lbl_addrname);
            ps.setString(18, x.lbl_addr1);
            ps.setString(19, x.lbl_addr2);
            ps.setString(20, x.lbl_addrcity);
            ps.setString(21, x.lbl_addrstate);
            ps.setString(22, x.lbl_addrzip);
            ps.setString(23, x.lbl_addrcountry);
            ps.setString(24, x.lbl_crt_date);
            ps.setString(25, x.lbl_ship_date);
            ps.setString(26, x.lbl_userid);
            ps.setString(27, x.lbl_printer);
            ps.setString(28, x.lbl_prog);
            ps.setString(29, x.lbl_site);
            ps.setString(30, x.lbl_loc);
            ps.setString(31, x.lbl_trantype);
            ps.setString(32, x.lbl_type);
            ps.setString(33, x.lbl_shipto);
            rows = ps.executeUpdate();   
            }
           return rows;
    }
    
    public static String[] addLabelDet(label_det x) {
        String[] m = new String[2];
        String sqlSelect = "select * from label_det where lbld_id = ? and lbld_order = ? and lbld_line = ?";
        String sqlInsert = "insert into label_det (lbld_id, lbld_order, lbld_line, lbld_item, lbld_desc, lbld_qty ) " +
                " values (?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.lbld_id);
             ps.setString(2, x.lbld_order);
             ps.setString(3, x.lbld_line);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.lbld_id);
            psi.setString(2, x.lbld_order);
            psi.setString(3, x.lbld_line);
            psi.setString(4, x.lbld_item);
            psi.setString(5, x.lbld_desc);
            psi.setDouble(6, x.lbld_qty);
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
    
    private static int _addLabelDet(label_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from label_det where lbld_id = ? and lbld_order = ? and lbld_line = ?";
        String sqlInsert = "insert into label_det (lbld_id, lbld_order, lbld_line, lbld_item, lbld_desc, lbld_qty ) " +
                " values (?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.lbld_id);
          ps.setString(2, x.lbld_order);
          ps.setString(3, x.lbld_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.lbld_id);
            ps.setString(2, x.lbld_order);
            ps.setString(3, x.lbld_line);
            ps.setString(4, x.lbld_item);
            ps.setString(5, x.lbld_desc);
            ps.setDouble(6, x.lbld_qty);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addMixedLabelTransaction(ArrayList<label_det> lbld, label_mstr lbl) {
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
            _addLabelMstr(lbl, bscon, ps, res);  
            for (label_det z : lbld) {
                _addLabelDet(z, bscon, ps, res);
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
    
    public static String[] addMultiLabelTransaction(ArrayList<label_det> lbld, ArrayList<label_mstr> lbl) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","addMultiLabelTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(lbld);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(lbl);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServLBL"));
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
           // _addLabelMstr(lbl, bscon, ps, res);  
            for (label_mstr z : lbl) {
                _addLabelMstr(z, bscon, ps, res);
            }
            if (lbld != null) {
                for (label_det z : lbld) {
                    _addLabelDet(z, bscon, ps, res);
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
    
    
    public static label_mstr getLabelMstr(String x) {
        label_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLabelMstr"});
            list.add(new String[]{"param1", x});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServLBL");
                r = objectMapper.readValue(returnstring, label_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from label_mstr where lbl_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new label_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new label_mstr(m, res.getString("lbl_id"), 
                            res.getString("lbl_item"),
                            res.getString("lbl_custitem"),
                            res.getString("lbl_id_str"),
                            res.getString("lbl_conttype"),
                            res.getString("lbl_name"),
                            res.getString("lbl_qty"),
                            res.getString("lbl_po"),
                            res.getString("lbl_billto"),
                            res.getString("lbl_order"),
                            res.getString("lbl_line"),
                            res.getString("lbl_ref"),
                            res.getString("lbl_lot"),
                            res.getString("lbl_parent"),
                            res.getString("lbl_parent_str"),
                            res.getString("lbl_addrcode"),
                            res.getString("lbl_addrname"),
                            res.getString("lbl_addr1"),
                            res.getString("lbl_addr2"),
                            res.getString("lbl_addrcity"),
                            res.getString("lbl_addrstate"),
                            res.getString("lbl_addrzip"),
                            res.getString("lbl_addrcountry"),
                            res.getString("lbl_crt_date"),
                            res.getString("lbl_ship_date"),
                            res.getString("lbl_userid"),
                            res.getString("lbl_printer"),
                            res.getString("lbl_prog"),
                            res.getString("lbl_site"),
                            res.getString("lbl_loc"),
                            res.getString("lbl_trantype"),
                            res.getString("lbl_type"),
                            res.getString("lbl_shipto"),
                            res.getString("lbl_scan"),
                            res.getString("lbl_void"),
                            res.getString("lbl_post")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new label_mstr(m);
        }
        return r;
    }
    
    public static label_mstr getLabelMstrByStrID(String x) {
        label_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLabelMstrByStrID"});
            list.add(new String[]{"param1", x});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServLBL");
                r = objectMapper.readValue(returnstring, label_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from label_mstr where lbl_id_str = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new label_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new label_mstr(m, res.getString("lbl_id"), 
                            res.getString("lbl_item"),
                            res.getString("lbl_custitem"),
                            res.getString("lbl_id_str"),
                            res.getString("lbl_conttype"),
                            res.getString("lbl_name"),
                            res.getString("lbl_qty"),
                            res.getString("lbl_po"),
                            res.getString("lbl_billto"),
                            res.getString("lbl_order"),
                            res.getString("lbl_line"),
                            res.getString("lbl_ref"),
                            res.getString("lbl_lot"),
                            res.getString("lbl_parent"),
                            res.getString("lbl_parent_str"),
                            res.getString("lbl_addrcode"),
                            res.getString("lbl_addrname"),
                            res.getString("lbl_addr1"),
                            res.getString("lbl_addr2"),
                            res.getString("lbl_addrcity"),
                            res.getString("lbl_addrstate"),
                            res.getString("lbl_addrzip"),
                            res.getString("lbl_addrcountry"),
                            res.getString("lbl_crt_date"),
                            res.getString("lbl_ship_date"),
                            res.getString("lbl_userid"),
                            res.getString("lbl_printer"),
                            res.getString("lbl_prog"),
                            res.getString("lbl_site"),
                            res.getString("lbl_loc"),
                            res.getString("lbl_trantype"),
                            res.getString("lbl_type"),
                            res.getString("lbl_shipto"),
                            res.getString("lbl_scan"),
                            res.getString("lbl_void"),
                            res.getString("lbl_post")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new label_mstr(m);
        }
        return r;
    }
    
    
    public static String[] addLabelZebraMstr(label_zebra x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addLabelZebraMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServLBL"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "select * from label_zebra where lblz_code = ?";
        String sqlInsert = "insert into label_zebra (lblz_code, lblz_desc, lblz_type, lblz_file)  " +
                " values (?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.lblz_code);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.lblz_code);
            psi.setString(2, x.lblz_desc);
            psi.setString(3, x.lblz_type);
            psi.setString(4, x.lblz_file);
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
    
    public static String[] updateLabelZebraMstr(label_zebra x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateLabelZebraMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServLBL"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update label_zebra set lblz_desc = ?, lblz_type = ?, lblz_file = ? " +
                "  where lblz_code = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.lblz_desc);
        ps.setString(2, x.lblz_type);
        ps.setString(3, x.lblz_file);
        ps.setString(4, x.lblz_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteLabelZebraMstr(label_zebra x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteLabelZebraMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServLBL"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from label_zebra where lblz_code = ?; ";
        
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.lblz_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static label_zebra getLabelZebraMstr(String[] x) {
        label_zebra r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLabelZebraMstr"});
            list.add(new String[]{"param1", x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServLBL");
                r = objectMapper.readValue(returnstring, label_zebra.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        String sql = "select * from label_zebra where lblz_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new label_zebra(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new label_zebra(m, res.getString("lblz_code"), 
                            res.getString("lblz_desc"),
                            res.getString("lblz_type"),
                            res.getString("lblz_file")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new label_zebra(m);
        }
        return r;
    }
    
    public static String getLabelMultiPrintData(String shipper) {
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
                String sqlquery = ""; 
               if (bsmf.MainFrame.dbtype.equals("sqlite")) {
               sqlquery = "select sh_id, shd_so, lbl_id, lbl_id_str, lbl_item, shd_desc, lbl_qty," +
                "sh_cust,  sh_shipvia," +
                "lbl_po, shd_uom," +
                "cm_code, cm_name, cm_line1," +
                "cm_line2," +
                "cms_name, cms_line1, cms_line2, cms_city, cms_state, cms_zip, cms_country, cms_plantcode," +
                "site_desc, site_line1, site_city, site_state, site_zip, site_country, ov_jasper_directory, " +
                "(select edim_value from edi_meta where edim_id = lbl_po and edim_type = 'detail:' || lbl_line AND edim_key = 'CL' ) as color," +
                "(select edim_value from edi_meta where edim_id = lbl_po and edim_type = 'detail:' || lbl_line AND edim_key = 'IZ' ) as size " +
                "from label_mstr " +
                "inner join ship_det on shd_id = " + "'" + shipper + "'" + " and shd_so = lbl_order and shd_soline = lbl_line  " +
                "inner join ship_mstr on sh_id = " + "'" + shipper + "'" +
                " inner join cm_mstr on cm_code = sh_cust " +
                "left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                "inner join site_mstr on site_site = sh_site " +
                "inner join ov_ctrl " +        
                " where lbl_ref = " + "'" + shipper + "'"; 
               } else {
                sqlquery = "select sh_id, shd_so, lbl_id, lbl_id_str, lbl_item, shd_desc, lbl_qty," +
                "sh_cust, sh_shipvia," +
                "lbl_po, shd_uom," +
                "cm_code, cm_name, cm_line1," +
                "cm_line2," +
                "cms_name, cms_line1, cms_line2, cms_city, cms_state, cms_zip, cms_country, cms_plantcode," +
                "site_desc, site_line1, site_city, site_state, site_zip, site_country, ov_jasper_directory, " +
                "(select edim_value from edi_meta where edim_id = lbl_po and edim_type = concat('detail:',lbl_line) AND edim_key = 'CL' ) as color," +
                "(select edim_value from edi_meta where edim_id = lbl_po and edim_type = concat('detail:',lbl_line) AND edim_key = 'IZ' ) as size " +
                "from label_mstr " +
                "inner join ship_det on shd_id = " + "'" + shipper + "'" + " and shd_so = lbl_order and shd_soline = lbl_line  " +
                "inner join ship_mstr on sh_id = " + "'" + shipper + "'" +
                " inner join cm_mstr on cm_code = sh_cust " +
                "left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                "inner join site_mstr on site_site = sh_site " +
                "inner join ov_ctrl " +        
                " where lbl_ref = " + "'" + shipper + "'"; 
               }
               res = st.executeQuery(sqlquery);
                    
                   
                    int i = 0;
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("sh_id")); 
                        rowArray.put(res.getString("shd_so"));
                        rowArray.put(res.getString("lbl_id"));
                        rowArray.put(res.getString("lbl_id_str"));
                        rowArray.put(res.getString("lbl_item"));
                        rowArray.put(res.getString("shd_desc"));
                        rowArray.put(res.getString("lbl_qty"));
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("sh_shipvia"));
                        rowArray.put(res.getString("lbl_po")); 
                        rowArray.put(res.getString("shd_uom")); // 10 zero base
                        rowArray.put(res.getString("cm_code")); 
                        rowArray.put(res.getString("cm_name")); 
                        rowArray.put(res.getString("cm_line1"));
                        rowArray.put(res.getString("cm_line2"));
                        rowArray.put(res.getString("cms_name"));
                        rowArray.put(res.getString("cms_line1"));
                        rowArray.put(res.getString("cms_line2"));
                        rowArray.put(res.getString("cms_city"));  
                        rowArray.put(res.getString("cms_state"));
                        rowArray.put(res.getString("cms_zip")); // 20 zero base
                        rowArray.put(res.getString("cms_country"));
                        rowArray.put(res.getString("cms_plantcode"));
                        rowArray.put(res.getString("site_desc"));
                        rowArray.put(res.getString("site_line1"));
                        rowArray.put(res.getString("site_city"));
                        rowArray.put(res.getString("site_state"));
                        rowArray.put(res.getString("site_zip"));
                        rowArray.put(res.getString("site_country"));
                        rowArray.put(res.getString("ov_jasper_directory"));
                        rowArray.put(res.getString("color")); // 30 zero base
                        rowArray.put(res.getString("size"));
                        
                        jsonarray.put(rowArray);
                        i++;
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
    
    public static String getJobTicketPrintData(String jobid) {
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
                String sqlquery = ""; 
               
               sqlquery = "select plan_nbr, plan_item, plan_qty_req, plan_qty_sched, plan_date_due, " +
                       " plan_date_sched, plan_date_create, plan_order, plan_type, plan_line, " +
                       " plan_cell, it_item, it_rev, it_desc, it_wf, wf_op, wf_desc, wf_cell, ov_jasper_directory,  " +
                       " plo_operator, plo_operator, plo_operatorname, plo_cell, plo_op, plo_desc, plo_notes, plan_rmks, " + 
                       " case when wf_assert = 0 then 'no' else 'yes' end as assert, " +
                       " case when wf_run_hours = 0 then '0' when wf_run_hours = '' then '0' when wf_run_hours is null then '0' else (1 / wf_run_hours) end as runrate, " +
                       " case when plan_type = 'DEMD' then so_po else 'N/A' end as custpo, " +
                       " case when plan_type = 'DEMD' then so_cust else 'N/A' end as custcode, " + 
                       " case when plan_type = 'DEMD' then sod_bom else bom_id end as bomcode, " +
                       " case when plan_type = 'DEMD' then sod_custitem else 'N/A' end as custitem " +
                       " FROM plan_mstr left outer join item_mstr on it_item = plan_item " +
                       " inner join ov_ctrl " + 
                       " left outer join wf_mstr on wf_id = it_wf " +
                       " left outer join plan_operation on plo_parent = plan_nbr " +
                       " left outer join bom_mstr on bom_item = plan_item and bom_primary = '1' and bom_enabled = '1' " +
                       " left outer join so_mstr on so_nbr = plan_order " +
                       " left outer join sod_det on sod_nbr = so_nbr and sod_item = plan_item and sod_line = plan_line " +
                       " where plan_nbr = " + "'" + jobid + "'" + " order by wf_op ;"; 
               res = st.executeQuery(sqlquery);
                    int i = 0;
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("plan_nbr")); 
                        rowArray.put(res.getString("plan_item"));
                        rowArray.put(res.getString("plan_qty_req"));
                        rowArray.put(res.getString("plan_qty_sched"));
                        rowArray.put(res.getString("plan_date_due"));
                        rowArray.put(res.getString("plan_date_sched"));
                        rowArray.put(res.getString("plan_date_create"));
                        rowArray.put(res.getString("plan_order"));
                        rowArray.put(res.getString("plan_type"));
                        rowArray.put(res.getString("plan_line")); 
                        rowArray.put(res.getString("plan_cell")); // 10 zero base
                        rowArray.put(xNull(res.getString("it_item"))); 
                        rowArray.put(xNull(res.getString("it_rev"))); 
                        rowArray.put(xNull(res.getString("it_desc")));
                        rowArray.put(xNull(res.getString("it_wf")));
                        rowArray.put(xNull(res.getString("wf_op")));
                        rowArray.put(xNull(res.getString("wf_desc")));
                        rowArray.put(xNull(res.getString("wf_cell")));
                        rowArray.put(res.getString("assert"));  
                        rowArray.put(res.getString("runrate"));
                        rowArray.put(res.getString("custpo")); // 20 zero base
                        rowArray.put(res.getString("custcode"));
                        rowArray.put(res.getString("bomcode"));
                        rowArray.put(res.getString("custitem"));
                        rowArray.put(res.getString("ov_jasper_directory"));
                        
                        rowArray.put(xNull(res.getString("plo_operator")));
                        rowArray.put(xNull(res.getString("plo_operatorname")));
                        rowArray.put(xNull(res.getString("plo_cell")));
                        rowArray.put(xNull(res.getString("plo_op")));
                        rowArray.put(xNull(res.getString("plo_desc")));
                        rowArray.put(xNull(res.getString("plo_notes"))); // 30 zero based
                        rowArray.put(res.getString("plan_rmks"));
                        jsonarray.put(rowArray);
                        i++;
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
    
    public static String getJobOperationPrintData(String jobid, String op) {
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
               
              String sqlquery = "select plan_nbr, plan_item, plan_qty_req, plan_qty_sched, plan_date_due, " +
                       " plan_date_sched, plan_date_create, plan_order, plan_type, plan_line, " +
                       " plan_cell, plo_operator, plo_operatorname, plo_cell, it_item, it_rev, it_desc, it_wf, wf_op, wf_desc, wf_cell, ov_jasper_directory,  " +
                       " plo_op, plo_date, plo_qty, plo_desc, plo_notes, plan_rmks, " +
                       " case when wf_assert = 0 then 'no' else 'yes' end as assert, " +
                       " case when wf_run_hours = 0 then '0' when wf_run_hours = '' then '0' when wf_run_hours is null then '0' else (1 / wf_run_hours) end as runrate, " +
                       " case when plan_type = 'DEMD' then so_po else 'N/A' end as custpo, " +
                       " case when plan_type = 'DEMD' then so_cust else 'N/A' end as custcode, " + 
                       " case when plan_type = 'DEMD' then sod_bom else bom_id end as bomcode, " +
                       " case when plan_type = 'DEMD' then sod_custitem else 'N/A' end as custitem " +
                       " FROM plan_mstr left outer join item_mstr on it_item = plan_item " +
                       " inner join ov_ctrl " + 
                       " left outer join wf_mstr on wf_id = it_wf and wf_op = " + "'" + op + "'" +
                       " left outer join plan_operation on plo_parent = plan_nbr and plo_op = " + "'" + op + "'" +
                       " left outer join bom_mstr on bom_item = plan_item and bom_primary = '1' and bom_enabled = '1' " +
                       " left outer join so_mstr on so_nbr = plan_order " +
                       " left outer join sod_det on sod_nbr = so_nbr and sod_item = plan_item and sod_line = plan_line " +
                       " where plan_nbr = " + "'" + jobid + "'" + 
                       " ;"; 
               res = st.executeQuery(sqlquery);
                    int i = 0;
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("plan_nbr")); 
                        rowArray.put(res.getString("plan_item"));
                        rowArray.put(res.getString("plan_qty_req"));
                        rowArray.put(res.getString("plan_qty_sched"));
                        rowArray.put(res.getString("plan_date_due"));
                        rowArray.put(res.getString("plan_date_sched"));
                        rowArray.put(res.getString("plan_date_create"));
                        rowArray.put(res.getString("plan_order"));
                        rowArray.put(res.getString("plan_type"));
                        rowArray.put(res.getString("plan_line")); 
                        rowArray.put(res.getString("plan_cell")); // 10 zero base
                        rowArray.put(xNull(res.getString("it_item"))); 
                        rowArray.put(xNull(res.getString("it_rev"))); 
                        rowArray.put(xNull(res.getString("it_desc")));
                        rowArray.put(xNull(res.getString("it_wf")));
                        rowArray.put(xNull(res.getString("wf_op")));
                        rowArray.put(xNull(res.getString("wf_desc")));
                        rowArray.put(xNull(res.getString("wf_cell")));
                        rowArray.put(res.getString("assert"));  
                        rowArray.put(res.getString("runrate"));
                        rowArray.put(res.getString("custpo")); // 20 zero base
                        rowArray.put(res.getString("custcode"));
                        rowArray.put(res.getString("bomcode"));
                        rowArray.put(res.getString("custitem"));
                        rowArray.put(res.getString("ov_jasper_directory"));
                        rowArray.put(xNull(res.getString("plo_op")));
                        rowArray.put(xNull(res.getString("plo_operator")));
                        rowArray.put(xNull(res.getString("plo_operatorname")));
                        rowArray.put(xNull(res.getString("plo_cell")));
                        rowArray.put(xNull(res.getString("plo_date")));
                        rowArray.put(xNull(res.getString("plo_qty")));
                        rowArray.put(xNull(res.getString("plo_desc")));
                        rowArray.put(xNull(res.getString("plo_notes")));    
                        rowArray.put(xNull(res.getString("plan_rmks")));
                        jsonarray.put(rowArray);
                        i++;
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
    
    
    public static String CreateLabelMstr(String serialno, String item, String custpart, String serialnostring, 
              String conttype, String qty, String po, String order, String line, String ref, String lot,
              String parent, String parentstring, String addrcode, String addrname, String addr1, String addr2,
              String addrcity, String addrstate, String addrzip, String addrcountry, String createdate, String effdate, 
              String userid, String printer, String prog, String site, String loc, String trantype) {
          String shiptocode = "";
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
              
                 res = st.executeQuery("select lbl_id from label_mstr where lbl_id  = " + "'" + serialno + "'" 
                               + " ;");
                       while (res.next()) {
                           i++;
                       }
                if (i == 0) {
                    st.executeUpdate("insert into label_mstr "
                        + "(lbl_id, lbl_item, lbl_custitem, lbl_id_str, lbl_conttype, lbl_qty, lbl_po, "
                        + "lbl_order, lbl_line, lbl_ref, lbl_lot, lbl_parent, lbl_parent_str, "
                        + "lbl_addrcode, lbl_addrname, lbl_addr1, lbl_addr2, lbl_addrcity, lbl_addrstate, lbl_addrzip, lbl_addrcountry, "
                        + "lbl_crt_date, lbl_ship_date, lbl_userid, lbl_printer, lbl_prog, lbl_site, lbl_loc, lbl_trantype "
                        + " ) "
                        + " values ( " + "'" + serialno + "'" + ","
                        + "'" + item + "'" + ","
                        + "'" + custpart + "'" + ","
                        + "'" + serialnostring + "'" + ","
                        + "'" + conttype + "'" + ","
                        + "'" + qty + "'" + ","
                        + "'" + po + "'" + ","
                        + "'" + order + "'" + ","
                        + "'" + line + "'" + ","
                        + "'" + ref + "'" + ","
                        + "'" + lot + "'" + ","
                        + "'" + parent + "'" + ","
                        + "'" + parentstring + "'" + ","
                        + "'" + addrcode + "'" + ","
                        + "'" + addrname + "'" + ","
                        + "'" + addr1 + "'" + ","
                        + "'" + addr2 + "'" + ","
                        + "'" + addrcity + "'" + ","
                        + "'" + addrstate + "'" + ","
                        + "'" + addrzip + "'" + ","
                        + "'" + addrcountry + "'" + ","
                        + "'" + createdate + "'" + ","
                        + "'" + effdate + "'" + ","
                        + "'" + userid + "'" + ","
                        + "'" + printer + "'" + ","
                        + "'" + prog + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + loc + "'" + ","
                        + "'" + trantype + "'"
                        + ")"
                        + ";");
                } // if i == 0
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return shiptocode;
      }
    
    
    public static void deleteLabelByShipper(String shipper) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteLabelByShipper"});
            list.add(new String[]{"param1",shipper});
            try {
                sendServerPost(list, "", null, "dataServLBL");
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
                 st.executeUpdate("delete from label_mstr where lbl_ref = " + "'" + shipper + "'" 
                         + " ;");
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
    
    
    public static ArrayList<String[]> getLabelTableRecs(String billto) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLabelTableRecs"});
            list.add(new String[]{"param1", billto});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServLBL"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList<String[]> list = new ArrayList<String[]>();
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
                 res = st.executeQuery("select lbld_id, lbld_order, lbld_line, lbld_item, lbld_qty, lbl_type, sod_desc, sod_custitem, sod_wh, sod_loc, sod_uom, sod_listprice, sod_disc, sod_netprice, sod_po " +
                         " from label_det inner join label_mstr on lbl_id = lbld_id " +
                         " inner join sod_det on sod_nbr = lbld_order and sod_line = lbld_line " +
                         " where lbl_scan = '0' and lbl_type = 'mixed' and lbl_billto = " + "'" + billto + "'" 
                         + " ;");
               while (res.next()) {
                   list.add(new String[]{res.getString("lbld_id"),
                           res.getString("lbld_order"),
                           res.getString("lbld_line"),
                           res.getString("lbld_item"),
                           res.getString("sod_desc"),
                           res.getString("sod_custitem"),
                           res.getString("sod_wh"),
                           res.getString("sod_loc"),
                           res.getString("lbld_qty"),
                           res.getString("sod_uom"),
                           res.getString("sod_listprice"),
                           res.getString("sod_disc"),
                           res.getString("sod_netprice"),
                           res.getString("sod_po")}); 
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
        
        
        return list;
    }
    
    public static void updateLabelStatus(String serialno, String value) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updateLabelStatus"});
            list.add(new String[]{"param1",  serialno});
            list.add(new String[]{"param2",  value});
            try {
                sendServerPost(list, "", null, "dataServLBL");
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
                 st.executeUpdate("update label_mstr set lbl_scan = " + "'" + value + "'" + " where lbl_id = " + "'" + serialno + "'" 
                         + " ;");
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
    
    public static String getLabelInfo(String serialno) {
          String myreturn = "";
          String delim = "+-+";
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
                 res = st.executeQuery("select * from label_mstr where lbl_id = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                   myreturn = res.getString("lbl_id") + delim + 
                           res.getString("lbl_item") + delim +
                           res.getString("lbl_custitem") + delim +
                           res.getString("lbl_id_str") + delim +
                           res.getString("lbl_conttype") + delim +
                           res.getString("lbl_qty") + delim +
                           res.getString("lbl_po") + delim +
                           res.getString("lbl_order") + delim +
                           res.getString("lbl_line") + delim +
                           res.getString("lbl_ref") + delim +
                           res.getString("lbl_lot") + delim +
                           res.getString("lbl_parent") + delim +
                           res.getString("lbl_parent_str") + delim +
                           res.getString("lbl_addrcode") + delim +
                           res.getString("lbl_addrname") + delim +
                           res.getString("lbl_addr1") + delim +
                           res.getString("lbl_addr2") + delim +
                           res.getString("lbl_addrcity") + delim +
                           res.getString("lbl_addrstate") + delim +
                           res.getString("lbl_addrzip") + delim +
                           res.getString("lbl_addrcountry") + delim +
                           res.getString("lbl_crt_date") + delim +
                           res.getString("lbl_ship_date") + delim +
                           res.getString("lbl_scan") + delim +
                           res.getString("lbl_void") + delim +
                           res.getString("lbl_post") + delim +
                           res.getString("lbl_userid") + delim +
                           res.getString("lbl_printer") + delim +
                           res.getString("lbl_prog") + delim +
                           res.getString("lbl_site") + delim +
                           res.getString("lbl_loc") + delim +
                           res.getString("lbl_trantype") 
                           ;
                           
               }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }finally {
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
          return myreturn;
      }
    
    public static boolean isLabel(String serialno) {
          
          // From perspective of "does it exist"
          // assume it's false i.e. it doesnt exist.
          boolean myreturn = false;
          int i = 0;
          
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
                 res = st.executeQuery("select lbl_id from label_mstr where lbl_id = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                 i++; 
               }
               if (i > 0) {
                   myreturn = true;
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
          return myreturn;
      }
      
    public static ArrayList getLabelFileList(String type) {
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
            try {

                if (type.equals("all")) {
                res = st.executeQuery("select lblz_code from label_zebra ;");
                } else {
                res = st.executeQuery("select lblz_code from label_zebra where lblz_type = " + "'" + type + "'" + ";"); 
                }
                
               while (res.next()) {
                    myarray.add(res.getString("lblz_code"));
                    
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
        return myarray;
        
    }
    
    public static int getLabelStatus(String serialno) {

      // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
      // assume it's false i.e. hasn't been scanned.
      int myreturn = 0;

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
             res = st.executeQuery("select lbl_scan from label_mstr where lbl_id = " + "'" + serialno + "'" 
                     + " ;");
           while (res.next()) {
               myreturn = res.getInt("lbl_scan");
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
      return myreturn;
  }

    public static String getLabelSerialDisplay(String serialno) {

        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","getLabelSerialDisplay"});
            list.add(new String[]{"param1",serialno});
            try {
                return sendServerPost(list, "", null, "dataServLBL"); 
            } catch (IOException ex) {
                bslog(ex);
                return "";
            }
        }
        
        String r = "";
        
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
             res = st.executeQuery("select lbl_id_str from label_mstr where lbl_id = " + "'" + serialno + "'" 
                     + " ;");
           while (res.next()) {
               r = res.getString("lbl_id_str");
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
      return r;
  }

    public static String getLabelBrowseView(String[] keys) {
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
                               
                keys[2] = (keys[2].isBlank()) ? bsmf.MainFrame.lowchar : keys[2];
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.hichar : keys[3];
                keys[4] = (keys[4].isBlank()) ? bsmf.MainFrame.lowchar : keys[4];
                keys[5] = (keys[5].isBlank()) ? bsmf.MainFrame.hichar : keys[5]; 
                
                res = st.executeQuery("SELECT * " +
                        " FROM  label_mstr " +
                        " where lbl_crt_date >= " + "'" + keys[0]  + "'" + 
                        " AND lbl_crt_date <= " + "'" + keys[1] + "'" + 
                        " AND lbl_item >= " + "'" + keys[2] + "'" + 
                        " AND lbl_item <= " + "'" + keys[3] + "'" + 
                         " AND lbl_id >= " + "'" + keys[4] + "'" + 
                         " AND lbl_id <= " + "'" + keys[5] + "'" + 
                         " ;");
                    while (res.next()) {
                  
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("lbl_id"));
                        rowArray.put(res.getString("lbl_id_str"));
                        rowArray.put(res.getString("lbl_item"));
                        rowArray.put(res.getString("lbl_qty"));
                        rowArray.put(res.getString("lbl_po"));
                        rowArray.put(res.getString("lbl_order"));
                        rowArray.put(res.getString("lbl_line"));
                        rowArray.put(res.getString("lbl_ref"));
                        rowArray.put(res.getString("lbl_type"));
                        rowArray.put(res.getString("lbl_prog"));
                        rowArray.put(res.getString("lbl_name"));
                        rowArray.put(res.getString("lbl_crt_date"));
                        rowArray.put(res.getString("lbl_scan"));
                        rowArray.put(res.getString("lbl_void"));
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
   
    public static String getLabelBrowseDetView(String[] keys) {
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
                
                if (keys[0].equals("trans")) {
                    res = st.executeQuery("select * from tran_mstr " +
                        " where tr_serial = " + "'" + keys[1] + "'" + ";");
                    while (res.next()) {                        
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("tr_serial"));
                        rowArray.put(res.getString("tr_item"));
                        rowArray.put(res.getString("tr_qty"));
                        rowArray.put(res.getString("tr_ref"));
                        rowArray.put(res.getString("tr_eff_date"));
                        jsonarray.put(rowArray);
                    }
                } else {
                    res = st.executeQuery("select * from label_det " +
                        " where lbld_id = " + "'" + keys[1] + "'" + ";");
                    while (res.next()) {                        
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("lbld_id"));
                        rowArray.put(res.getString("lbld_order"));
                        rowArray.put(res.getString("lbld_line"));
                        rowArray.put(res.getString("lbld_item"));
                        rowArray.put(res.getString("lbld_desc"));
                        rowArray.put(res.getString("lbld_qty"));
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
   
    
    public record label_zebra(String[] m, String lblz_code, String lblz_desc, 
        String lblz_type, String lblz_file) {
        public label_zebra(String[]m) {
            this(m, "", "", "", "");
        }
    }
     
    public record label_mstr(String[] m, String lbl_id, String lbl_item, String lbl_custitem, 
        String lbl_id_str, String lbl_conttype, String lbl_name, String lbl_qty, String lbl_po,
        String lbl_billto, String lbl_order, String lbl_line, String lbl_ref, String lbl_lot, 
        String lbl_parent, String lbl_parent_str, String lbl_addrcode, String lbl_addrname, 
        String lbl_addr1, String lbl_addr2, String lbl_addrcity, String lbl_addrstate, 
        String lbl_addrzip, String lbl_addrcountry, String lbl_crt_date, String lbl_ship_date, 
        String lbl_userid, String lbl_printer, String lbl_prog, String lbl_site, 
        String lbl_loc, String lbl_trantype, String lbl_type, String lbl_shipto,
        String lbl_scan, String lbl_void, String lbl_post) {
        public label_mstr(String[]m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "","","","");
        }
    }
    
    public record label_det(String[] m, String lbld_id, String lbld_order, String lbld_line, String lbld_item, 
        String lbld_desc, double lbld_qty) {
        public label_det(String[]m) {
            this(m, "", "", "", "", "", 0);
        }
    }
    
}
