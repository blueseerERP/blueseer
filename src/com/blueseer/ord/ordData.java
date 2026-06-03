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
package com.blueseer.ord;

import com.blueseer.inv.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData._getCMSDet;
import static com.blueseer.ctr.cusData._getCustMstr;
import com.blueseer.ctr.cusData.cm_mstr;
import com.blueseer.ctr.cusData.cms_det;
import static com.blueseer.ctr.cusData.getCustInfo;
import com.blueseer.edi.EDI.edi855;
import static com.blueseer.edi.ediData.getEDIMetaValueAsKVStringPair;
import com.blueseer.fgl.fglData;
import static com.blueseer.shp.ShipOrderLine.orderSet;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData._addShipperTransaction;
import static com.blueseer.shp.shpData._confirmShipperTransaction;
import static com.blueseer.shp.shpData._updateShipperSAC;
import static com.blueseer.shp.shpData.addShipperTransaction;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import static com.blueseer.shp.shpData.updateShipperSAC;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListString;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToBoolean;
import static com.blueseer.utl.BlueSeerUtils.jsonToDouble;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.parseDateLD;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getSysMetaValue;
import static com.blueseer.utl.OVData.printInvoiceRemote;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;
import org.json.JSONArray;

/**
 *
 * @author terryva
 */
public class ordData {
    
      // add order master table only
    public static String[] addOrderMstr(so_mstr x) {
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
            int rows = _addOrderMstr(x, con, ps, res);  
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
        
    private static int _addOrderMstr(so_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from so_mstr where so_nbr = ?";
        String sqlInsert = "insert into so_mstr (so_nbr, so_cust, so_ship, " 
                        + "so_site, so_curr, so_shipvia, so_wh, so_po, so_due_date, so_ord_date, "
                        + "so_create_date, so_userid, so_status, so_isallocated, "
                        + "so_terms, so_ar_acct, so_ar_cc, so_rmks, so_type, so_taxcode, "
                        + "so_issourced, so_confirm, so_plan, so_entrytype, so_mod_date, so_cascade, "
                        + " so_slsperson1, so_slsperson2 ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.so_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.so_nbr);
            ps.setString(2, x.so_cust);
            ps.setString(3, x.so_ship);
            ps.setString(4, x.so_site);
            ps.setString(5, x.so_curr);
            ps.setString(6, x.so_shipvia);
            ps.setString(7, x.so_wh);
            ps.setString(8, x.so_po);
            ps.setString(9, x.so_due_date);
            ps.setString(10, x.so_ord_date);
            ps.setString(11, x.so_create_date);
            ps.setString(12, x.so_userid);
            ps.setString(13, x.so_status);
            ps.setString(14, x.so_isallocated);
            ps.setString(15, x.so_terms);
            ps.setString(16, x.so_ar_acct);
            ps.setString(17, x.so_ar_cc);
            ps.setString(18, x.so_rmks);
            ps.setString(19, x.so_type);
            ps.setString(20, x.so_taxcode);
            ps.setString(21, x.so_issourced);
            ps.setString(22, x.so_confirm);
            ps.setString(23, x.so_plan);
            ps.setString(24, x.so_entrytype);
            ps.setString(25, x.so_mod_date);
            ps.setString(26, x.so_cascade);
            ps.setString(27, x.so_slsperson1);
            ps.setString(28, x.so_slsperson2);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
        
    private static int _addOrderChange(so_chg x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from so_chg where soc_id = ? and soc_po = ?";
        String sqlInsert = "insert into so_chg (soc_id, soc_po, soc_type, " 
                        + "soc_chgdate, soc_duedate, soc_billto, soc_shipto, soc_ref, "
                        + "soc_misc1, soc_misc2, soc_misc3, soc_status, soc_userid, soc_applydate ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.soc_id);
          ps.setString(2, x.soc_po);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.soc_id);
            ps.setString(2, x.soc_po);
            ps.setString(3, x.soc_type);
            ps.setString(4, x.soc_chgdate);
            ps.setString(5, x.soc_duedate);
            ps.setString(6, x.soc_billto);
            ps.setString(7, x.soc_shipto);
            ps.setString(8, x.soc_ref);
            ps.setString(9, x.soc_misc1);
            ps.setString(10, x.soc_misc2);
            ps.setString(11, x.soc_misc3);
            ps.setString(12, x.soc_status);
            ps.setString(13, x.soc_userid);
            ps.setString(14, x.soc_applydate);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addOrderDetChange(sod_chg x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_chg where sodc_id = ? and sodc_po = ? and sodc_line = ?";
        String sqlInsert = "insert into sod_chg (sodc_id, sodc_po, sodc_line, " 
                        + "sodc_type, sodc_item, sodc_custitem, sodc_qty, sodc_price, sodc_duedate, sodc_misc ) "
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sodc_id);
          ps.setString(2, x.sodc_po);
          ps.setString(3, x.sodc_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sodc_id);
            ps.setString(2, x.sodc_po);
            ps.setString(3, x.sodc_line);
            ps.setString(4, x.sodc_type);
            ps.setString(5, x.sodc_item);
            ps.setString(6, x.sodc_custitem);
            ps.setDouble(7, x.sodc_qty);
            ps.setDouble(8, x.sodc_price);
            ps.setString(9, x.sodc_duedate);
            ps.setString(10, x.sodc_misc);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addOrderChangeTransaction(ArrayList<sod_chg> sodc, so_chg soc) {
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
            _addOrderChange(soc, bscon, ps, res);  
            for (sod_chg z : sodc) {
                _addOrderDetChange(z, bscon, ps, res);
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
    
     // add order master.... multiple table transaction function
    public static String[] addOrderTransaction(ArrayList<sod_det> sod, so_mstr so, ArrayList<so_tax> sot, ArrayList<sod_tax> sotd, ArrayList<sos_det> sos) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addOrderTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(sod);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(so);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sot);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sotd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sos);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
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
            _addOrderMstr(so, bscon, ps, res);  
            for (sod_det z : sod) {
                _addOrderDet(z, so, bscon, ps, res);
            }
            if (sot != null) {
                for (so_tax z : sot) {
                    _addOrderTaxMstr(z, bscon, ps, res);
                }
            }
            if (sotd != null) {
                for (sod_tax z : sotd) {
                    _addOrderTaxDet(z, bscon, ps, res);
                }
            }
            if (sos != null && ! sos.isEmpty()) {
                for (sos_det z : sos) {
                    _addOrderSummaryDet(z, bscon, ps, res);
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
     
    public static String[] updateOrderMstr(so_mstr x) {
        
        
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
            int rows = _updateOrderMstr(x, con, ps);  // add cms_det
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
   
    private static int _updateOrderMstr(so_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update so_mstr set so_cust = ?, so_ship = ?, " +
                "so_site = ?, so_curr = ?, so_shipvia = ?, so_wh = ?, so_po = ?, so_due_date = ?, so_ord_date = ?, so_mod_date = ?, " +
                "so_userid = ?, so_status = ?, so_isallocated = ?, so_terms = ?, so_ar_acct = ?, so_ar_cc = ?, so_rmks = ?, so_type = ?, " +
                "so_taxcode = ?, so_confirm = ?, so_plan = ?, so_export_855 = ?, so_slsperson1 = ?, so_slsperson2 = ? " +
                 " where so_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(25, x.so_nbr);
            ps.setString(1, x.so_cust);
            ps.setString(2, x.so_ship);
            ps.setString(3, x.so_site);
            ps.setString(4, x.so_curr);
            ps.setString(5, x.so_shipvia);
            ps.setString(6, x.so_wh);
            ps.setString(7, x.so_po);
            ps.setString(8, x.so_due_date);
            ps.setString(9, x.so_ord_date);
            ps.setString(10, x.so_mod_date);
            ps.setString(11, x.so_userid);
            ps.setString(12, x.so_status);
            ps.setString(13, x.so_isallocated);
            ps.setString(14, x.so_terms);
            ps.setString(15, x.so_ar_acct);
            ps.setString(16, x.so_ar_cc);
            ps.setString(17, x.so_rmks);
            ps.setString(18, x.so_type);
            ps.setString(19, x.so_taxcode);
            ps.setString(20, x.so_confirm);
            ps.setString(21, x.so_plan);
            ps.setString(22, x.so_export_855);
            ps.setString(23, x.so_slsperson1);
            ps.setString(24, x.so_slsperson2);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateOrderDet(sod_det x, so_mstr z, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_det where sod_nbr = ? and sod_line = ?";
        String sqlUpdate = "update sod_det set sod_item = ?, sod_custitem = ?, " +
                "sod_po = ?, sod_ord_qty = ?, sod_uom = ?, sod_all_qty = ?, " +
                " sod_listprice = ?, sod_disc = ?, sod_netprice = ?, sod_ord_date = ?, " +
                "sod_due_date = ?, sod_shipped_qty = ?, sod_status = ?, sod_wh = ?, sod_loc = ?, " +
                " sod_desc = ?, sod_taxamt = ?, sod_site = ?, sod_bom = ?, sod_ship = ?, sod_char1 = ?, sod_char2 = ?, sod_char3 = ? " +
                 " where sod_nbr = ? and sod_line = ? ; ";
        String sqlInsert = "insert into sod_det (sod_nbr, sod_line, sod_item, sod_custitem, " 
                        + "sod_po, sod_ord_qty, sod_uom, sod_all_qty, " 
                        + "sod_listprice, sod_disc, sod_netprice, sod_ord_date, sod_due_date, " 
                        + "sod_shipped_qty, sod_status, sod_wh, sod_loc, "
                        + "sod_desc, sod_taxamt, sod_site, sod_bom, sod_ship, sod_char1, sod_char2, sod_char3 ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.sod_nbr);
        ps.setInt(2, x.sod_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.sod_nbr);
            ps.setInt(2, x.sod_line);
            ps.setString(3, x.sod_item);
            ps.setString(4, x.sod_custitem);
            ps.setString(5, z.so_po);
            ps.setDouble(6, x.sod_ord_qty);
            ps.setString(7, x.sod_uom);
            ps.setDouble(8, x.sod_all_qty);
            ps.setDouble(9, x.sod_listprice);
            ps.setDouble(10, x.sod_disc);
            ps.setDouble(11, x.sod_netprice);
            ps.setString(12, z.so_ord_date);
            ps.setString(13, z.so_due_date);
            ps.setDouble(14, x.sod_shipped_qty);
            ps.setString(15, x.sod_status);
            ps.setString(16, x.sod_wh);
            ps.setString(17, x.sod_loc);
            ps.setString(18, x.sod_desc);
            ps.setDouble(19, x.sod_taxamt);
            ps.setString(20, x.sod_site);
            ps.setString(21, x.sod_bom);
            ps.setString(22, x.sod_ship);
            ps.setString(23, x.sod_char1);
            ps.setString(24, x.sod_char2);
            ps.setString(25, x.sod_char3);
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(24, x.sod_nbr);
            ps.setInt(25, x.sod_line);
            ps.setString(1, x.sod_item);
            ps.setString(2, x.sod_custitem);
            ps.setString(3, z.so_po);
            ps.setDouble(4, x.sod_ord_qty);
            ps.setString(5, x.sod_uom);
            ps.setDouble(6, x.sod_all_qty);
            ps.setDouble(7, x.sod_listprice);
            ps.setDouble(8, x.sod_disc);
            ps.setDouble(9, x.sod_netprice);
            ps.setString(10, z.so_ord_date);
            ps.setString(11, z.so_due_date);
            ps.setDouble(12, x.sod_shipped_qty);
            ps.setString(13, x.sod_status);
            ps.setString(14, x.sod_wh);
            ps.setString(15, x.sod_loc);
            ps.setString(16, x.sod_desc);
            ps.setDouble(17, x.sod_taxamt);
            ps.setString(18, x.sod_site);
            ps.setString(19, x.sod_bom);
            ps.setString(20, x.sod_ship);
            ps.setString(21, x.sod_char1);
            ps.setString(22, x.sod_char2);
            ps.setString(23, x.sod_char3);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
        
     // update order master.... multiple table transaction function
    public static String[] updateOrderTransaction(String x, ArrayList<String> lines, ArrayList<sod_det> sod, so_mstr so, ArrayList<so_tax> sot, ArrayList<sod_tax> sotd, ArrayList<sos_det> sos) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateOrderTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(lines);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sod);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(so);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sot);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sotd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sos);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
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
            for (String line : lines) {
               _deleteOrderLines(x, line, bscon, ps);  // discard unwanted lines
             }
            for (sod_det z : sod) {
                if (z.sod_status.equals(getGlobalProgTag("closed"))) {
                    continue;
                }
                _updateOrderDet(z, so, bscon, ps, res);
            }
            if (sot != null) {
                _deleteOrderTaxMstr(so.so_nbr, bscon);
                for (so_tax z : sot) {
                    _addOrderTaxMstr(z, bscon, ps, res);
                }
            }
            _deleteOrderTaxDet(so.so_nbr, bscon);
            for (sod_tax z : sotd) {
                _addOrderTaxDet(z, bscon, ps, res);
            }
            if (sos != null) {
                _deleteOrderSummaryDet(so.so_nbr, bscon);
                for (sos_det z : sos) {
                    _addOrderSummaryDet(z, bscon, ps, res);
                }
            }
             _updateOrderMstr(so, bscon, ps);  // update so_mstr
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
        
    public static String[] deleteOrderMstr(String order) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "deleteOrderMstr"});
            list.add(new String[]{"param1", order});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        } 
        
        String[] m = new String[2];
        if (order == null || order.isBlank()) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteOrderMstr(order, con);  
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
    
    public static String[] deleteOrderLines(String x, ArrayList<String> lines) {
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
             for (String line : lines) {
               _deleteOrderLines(x, line, con, ps);  // add cms_det
             }
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
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _deleteOrderLines(String x, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from sod_det where sod_nbr = ? and sod_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setInt(1, bsParseInt(x));
        ps.setInt(2, bsParseInt(line));
        ps.executeUpdate();
    }
    
    private static void _deleteOrderMstr(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        
        String po = "";
        String sqlSelect = "select * from so_mstr where so_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x);
          ResultSet res = ps.executeQuery();
            while(res.next()) {
                po = res.getString("so_po");
            }
          res.close();
            
        
        String sql = "delete from so_mstr where so_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from sod_det where sod_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from sod_tax where sodt_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from sos_det where sos_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from so_tax where sot_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from so_meta where som_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        if (! po.isBlank()) {
            sql = "delete from edi_meta where edim_id = ?; ";
            ps = con.prepareStatement(sql);
            ps.setString(1, po);
            ps.executeUpdate();
        }
        ps.close();
        
    }
    
    private static void _deleteOrderTaxMstr(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from so_tax where sot_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
      
    private static void _deleteOrderTaxDet(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from sod_tax where sodt_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    private static void _deleteOrderSummaryDet(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from sos_det where sos_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    public static salesOrder getOrderMstrSet(String[] x ) {
        salesOrder r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOrderMstrSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServORD");
                r = objectMapper.readValue(returnstring, salesOrder.class); 
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
            so_mstr so = _getOrderMstr(x, bscon, ps, res);
            ArrayList<sod_det> sod = _getOrderDet(x, bscon, ps, res);
            ArrayList<sos_det> sos = _getOrderSOS(x, bscon, ps, res);
            ArrayList<sod_tax> sotd = _getOrderDetTax(x, bscon, ps, res);
            ArrayList<so_tax> sot = _getOrderTax(x, bscon, ps, res);
            cms_det cms = _getCMSDet(so.so_cust, so.so_ship, bscon, ps, res );
            ArrayList<String[]> someta = _getSOMeta(x[0], bscon, ps, res);
            cm_mstr cm = _getCustMstr(so.so_cust, bscon, ps, res );
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new salesOrder(m, so, sod, sos, sotd, sot, cms, someta, cm);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new salesOrder(m);
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
    
    public static sv_mstr getServiceOrderMstr(String[] x) {
        sv_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getServiceOrderMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServORD");
                r = objectMapper.readValue(returnstring, sv_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from sv_mstr where sv_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sv_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new sv_mstr(m, res.getString("sv_nbr"), res.getString("sv_cust"), res.getString("sv_ship"),
                    res.getString("sv_po"), res.getString("sv_crew"), res.getString("sv_create_date"), res.getString("sv_due_date"), res.getString("sv_rmks"),
                    res.getString("sv_status"), res.getString("sv_issched"), res.getString("sv_userid"), res.getString("sv_type"), res.getString("sv_char1"),
                    res.getString("sv_char2"), res.getString("sv_char3"), res.getString("sv_terms"), res.getString("sv_curr"), 
                    res.getString("sv_ar_acct"), res.getString("sv_ar_cc"), res.getString("sv_onhold"), res.getString("sv_taxcode"),
                    res.getString("sv_site"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new sv_mstr(m);
        }
        return r;
    }
    
    public static so_mstr getOrderMstr(String[] x) {
        so_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getOrderMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServORD");
                r = objectMapper.readValue(returnstring, so_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from so_mstr where so_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new so_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new so_mstr(m, res.getString("so_nbr"), res.getString("so_cust"), res.getString("so_ship"),
                    res.getString("so_site"), res.getString("so_curr"), res.getString("so_shipvia"), res.getString("so_wh"), res.getString("so_po"),
                    res.getString("so_due_date"), res.getString("so_ord_date"), res.getString("so_create_date"), res.getString("so_userid"), res.getString("so_status"),
                    res.getString("so_isallocated"), res.getString("so_terms"), res.getString("so_ar_acct"), res.getString("so_ar_cc"), 
                    res.getString("so_rmks"), res.getString("so_type"), res.getString("so_taxcode"), res.getString("so_issourced"),
                    res.getString("so_confirm"), res.getString("so_plan"), res.getString("so_entrytype"), res.getString("so_export_855"), res.getString("so_mod_date"),
                        res.getString("so_cascade"), res.getString("so_slsperson1"), res.getString("so_slsperson2"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new so_mstr(m);
        }
        return r;
    }
    
    public static so_mstr getOrderMstr(String x) {
        so_mstr r = null;
        String[] m;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getOrderMstr"});
            list.add(new String[]{"param1",  x});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServORD");
                r = objectMapper.readValue(returnstring, so_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from so_mstr where so_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new so_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new so_mstr(m, res.getString("so_nbr"), res.getString("so_cust"), res.getString("so_ship"),
                    res.getString("so_site"), res.getString("so_curr"), res.getString("so_shipvia"), res.getString("so_wh"), res.getString("so_po"),
                    res.getString("so_due_date"), res.getString("so_ord_date"), res.getString("so_create_date"), res.getString("so_userid"), res.getString("so_status"),
                    res.getString("so_isallocated"), res.getString("so_terms"), res.getString("so_ar_acct"), res.getString("so_ar_cc"), 
                    res.getString("so_rmks"), res.getString("so_type"), res.getString("so_taxcode"), res.getString("so_issourced"),
                    res.getString("so_confirm"), res.getString("so_plan"), res.getString("so_entrytype"), res.getString("so_export_855"), res.getString("so_mod_date"),
                    res.getString("so_cascade"), res.getString("so_slsperson1"), res.getString("so_slsperson2"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new so_mstr(m);
        }
        return r;
    }
    
    
    
    private static so_mstr _getOrderMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        so_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from so_mstr where so_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setInt(1, bsParseInt(x[0]));
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new so_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new so_mstr(m, res.getString("so_nbr"), res.getString("so_cust"), res.getString("so_ship"),
                res.getString("so_site"), res.getString("so_curr"), res.getString("so_shipvia"), res.getString("so_wh"), res.getString("so_po"),
                res.getString("so_due_date"), res.getString("so_ord_date"), res.getString("so_create_date"), res.getString("so_userid"), res.getString("so_status"),
                res.getString("so_isallocated"), res.getString("so_terms"), res.getString("so_ar_acct"), res.getString("so_ar_cc"), 
                res.getString("so_rmks"), res.getString("so_type"), res.getString("so_taxcode"), res.getString("so_issourced"),
                res.getString("so_confirm"), res.getString("so_plan"), res.getString("so_entrytype"), res.getString("so_export_855"), res.getString("so_mod_date"),
                    res.getString("so_cascade"), res.getString("so_slsperson1"), res.getString("so_slsperson2"));
                }
            }
            return r;
    }
    
    public static ArrayList<svd_det> getServiceOrderDet(String[] x) {
        ArrayList<svd_det> list = new ArrayList<svd_det>();
        svd_det r = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getServiceOrderDet"});
            paramlist.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServORD");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<svd_det>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        String[] m = new String[2];
        String sql = "select * from svd_det where svd_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new svd_det(m, res.getString("svd_nbr"), res.getInt("svd_line"), res.getString("svd_uom"),
                    res.getString("svd_item"), res.getString("svd_desc"), res.getString("svd_type"), res.getString("svd_custitem"), 
                    res.getDouble("svd_qty"),res.getDouble("svd_completed_hrs"), res.getString("svd_po"), res.getString("svd_ord_date"), 
                    res.getString("svd_due_date"), res.getString("svd_create_date"),res.getString("svd_char1"), res.getString("svd_char2"), res.getString("svd_char3"), 
                    res.getString("svd_status"), res.getDouble("svd_listprice"), res.getDouble("svd_netprice"), res.getDouble("svd_disc"), 
                    res.getDouble("svd_taxamt"), res.getString("svd_taxcode"), res.getString("svd_site") );
                    list.add(r);
                    }
                
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    public static sod_det getOrderDet(String x, String y) {
        sod_det r = null;
        String[] m;
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOrderDetline"});
            list.add(new String[]{"param1",  x});
            list.add(new String[]{"param2",  y});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServORD");
                r = objectMapper.readValue(returnstring, sod_det.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        String sql = "select * from sod_det where sod_nbr = ? and sod_line = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x));
        ps.setInt(2, bsParseInt(y));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sod_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new sod_det(m, res.getString("sod_nbr"), res.getInt("sod_line"), res.getString("sod_item"),
                    res.getString("sod_custitem"), res.getString("sod_po"), res.getDouble("sod_ord_qty"), res.getString("sod_uom"), res.getDouble("sod_all_qty"),
                    res.getDouble("sod_listprice"), res.getDouble("sod_disc"), res.getDouble("sod_netprice"), res.getString("sod_ord_date"), res.getString("sod_due_date"),
                    res.getDouble("sod_shipped_qty"), res.getString("sod_status"), res.getString("sod_wh"), res.getString("sod_loc"), 
                    res.getString("sod_desc"), res.getDouble("sod_taxamt"), res.getString("sod_site"), res.getString("sod_bom"), res.getString("sod_ship"),
                    res.getString("sod_char1"), res.getString("sod_char2"), res.getString("sod_char3"),
                    res.getString("sod_custline"), res.getString("sod_custuom"), res.getString("sod_custprice"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new sod_det(m);
        }
        return r;
    }
        
    public static ArrayList<sod_det> getOrderDet(String[] x) {
        ArrayList<sod_det> list = new ArrayList<sod_det>();
        sod_det r = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<String[]>();
            paramlist.add(new String[]{"id", "getOrderDet"});
            paramlist.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServORD");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<sod_det>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        String[] m = new String[2];
        String sql = "select * from sod_det where sod_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sod_det(m, res.getString("sod_nbr"), res.getInt("sod_line"), res.getString("sod_item"),
                    res.getString("sod_custitem"), res.getString("sod_po"), res.getDouble("sod_ord_qty"), res.getString("sod_uom"), res.getDouble("sod_all_qty"),
                    res.getDouble("sod_listprice"), res.getDouble("sod_disc"), res.getDouble("sod_netprice"), res.getString("sod_ord_date"), res.getString("sod_due_date"),
                    res.getDouble("sod_shipped_qty"), res.getString("sod_status"), res.getString("sod_wh"), res.getString("sod_loc"), 
                    res.getString("sod_desc"), res.getDouble("sod_taxamt"), res.getString("sod_site"), res.getString("sod_bom"), res.getString("sod_ship"),
                    res.getString("sod_char1"), res.getString("sod_char2"), res.getString("sod_char3"),
                    res.getString("sod_custline"), res.getString("sod_custuom"), res.getString("sod_custprice"));
                    list.add(r);
                    }
                
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    private static ArrayList<sod_det> _getOrderDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<sod_det> list = new ArrayList<sod_det>();
        sod_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from sod_det where sod_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setInt(1, bsParseInt(x[0]));
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sod_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sod_det(m, res.getString("sod_nbr"), res.getInt("sod_line"), res.getString("sod_item"),
                    res.getString("sod_custitem"), res.getString("sod_po"), res.getDouble("sod_ord_qty"), res.getString("sod_uom"), res.getDouble("sod_all_qty"),
                    res.getDouble("sod_listprice"), res.getDouble("sod_disc"), res.getDouble("sod_netprice"), res.getString("sod_ord_date"), res.getString("sod_due_date"),
                    res.getDouble("sod_shipped_qty"), res.getString("sod_status"), res.getString("sod_wh"), res.getString("sod_loc"), 
                    res.getString("sod_desc"), res.getDouble("sod_taxamt"), res.getString("sod_site"), res.getString("sod_bom"), res.getString("sod_ship"),
                    res.getString("sod_char1"),res.getString("sod_char2"),res.getString("sod_char3"),
                    res.getString("sod_custline"), res.getString("sod_custuom"), res.getString("sod_custprice"));
                    list.add(r);
                    }
            }
            return list;
    }
    
    
    public static ArrayList<sos_det> getOrderSOS(String[] x) {
        ArrayList<sos_det> list = new ArrayList<sos_det>();
        sos_det r = null;
        String[] m = new String[2];
        String sql = "select * from sos_det where sos_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
         ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sos_det(m, res.getString("sos_nbr"), res.getString("sos_desc"), res.getString("sos_type"),
                    res.getString("sos_amttype"), res.getDouble("sos_amt") );
                    list.add(r);
                    }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    private static ArrayList<sos_det> _getOrderSOS(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<sos_det> list = new ArrayList<sos_det>();
        sos_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from sos_det where sos_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
           ps.setInt(1, bsParseInt(x[0]));
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sos_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sos_det(m, res.getString("sos_nbr"), res.getString("sos_desc"), res.getString("sos_type"),
                    res.getString("sos_amttype"), res.getDouble("sos_amt") );
                    list.add(r);
                }
            }
            return list;
    }
    
    
    
    public static so_tax getOrderTax(String[] x) {
        so_tax r = null;
        String[] m = new String[2];
        String sql = "select * from so_tax where sot_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
         ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new so_tax(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new so_tax(m, res.getString("sot_nbr"), res.getString("sot_desc"), res.getDouble("sot_percent"),
                    res.getString("sot_type"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new so_tax(m);
        }
        return r;
    }
    
    private static ArrayList<so_tax> _getOrderTax(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<so_tax> list = new ArrayList<so_tax>();
        so_tax r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from so_tax where sot_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setInt(1, bsParseInt(x[0]));
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new so_tax(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                     r = new so_tax(m, res.getString("sot_nbr"), res.getString("sot_desc"), res.getDouble("sot_percent"),
                    res.getString("sot_type"));
                    list.add(r);
                }
            }
            return list;
    }
    
    
    public static ArrayList<sod_tax> getOrderDetTax(String[] x) {
        ArrayList<sod_tax> list = new ArrayList<sod_tax>();
        sod_tax r = null;
        String[] m = new String[2];
        String sql = "select * from sod_tax where sodt_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                    while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sod_tax(m, res.getString("sodt_nbr"), res.getString("sodt_line"), res.getString("sodt_desc"),
                    res.getDouble("sodt_percent"), res.getString("sodt_type") );
                    list.add(r);
                    }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s); 
        }
        return list;
    }
    
    private static ArrayList<sod_tax> _getOrderDetTax(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<sod_tax> list = new ArrayList<sod_tax>();
        sod_tax r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from sod_tax where sodt_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setInt(1, bsParseInt(x[0]));
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sod_tax(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sod_tax(m, res.getString("sodt_nbr"), res.getString("sodt_line"), res.getString("sodt_desc"),
                    res.getDouble("sodt_percent"), res.getString("sodt_type") );
                    list.add(r);
                }
            }
            return list;
    }
    
    
    private static int _addOrderDet(sod_det x, so_mstr z, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_det where sod_nbr = ? and sod_line = ?";
        String sqlInsert = "insert into sod_det (sod_nbr, sod_line, sod_item, sod_custitem, " 
                        + "sod_po, sod_ord_qty, sod_uom, sod_all_qty, " 
                        + "sod_listprice, sod_disc, sod_netprice, sod_ord_date, sod_due_date, " 
                        + "sod_shipped_qty, sod_status, sod_wh, sod_loc, "
                        + "sod_desc, sod_taxamt, sod_site, sod_bom, sod_ship, sod_char1, sod_char2, sod_char3, "
                        + " sod_custline, sod_custuom, sod_custprice ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sod_nbr);
          ps.setInt(2, x.sod_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sod_nbr);
            ps.setInt(2, x.sod_line);
            ps.setString(3, x.sod_item);
            ps.setString(4, x.sod_custitem);
            ps.setString(5, z.so_po);
            ps.setDouble(6, x.sod_ord_qty);
            ps.setString(7, x.sod_uom);
            ps.setDouble(8, x.sod_all_qty);
            ps.setDouble(9, x.sod_listprice);
            ps.setDouble(10, x.sod_disc);
            ps.setDouble(11, x.sod_netprice);
            ps.setString(12, z.so_ord_date);
            ps.setString(13, z.so_due_date);
            ps.setDouble(14, x.sod_shipped_qty);
            ps.setString(15, x.sod_status);
            ps.setString(16, x.sod_wh);
            ps.setString(17, x.sod_loc);
            ps.setString(18, x.sod_desc);
            ps.setDouble(19, x.sod_taxamt);
            ps.setString(20, x.sod_site);
            ps.setString(21, x.sod_bom);
            ps.setString(22, x.sod_ship);
            ps.setString(23, x.sod_char1);
            ps.setString(24, x.sod_char2);
            ps.setString(25, x.sod_char3);
            ps.setString(26, x.sod_custline);
            ps.setString(27, x.sod_custuom);
            ps.setString(28, x.sod_custprice);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
       
    private static int _addOrderSummaryDet(sos_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sos_det where sos_nbr = ? and sos_desc = ?";
        String sqlInsert = "insert into sos_det (sos_nbr, sos_desc, sos_type, " 
                        + "sos_amttype, sos_amt ) "
                        + " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sos_nbr);
          ps.setString(2, x.sos_desc);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sos_nbr);
            ps.setString(2, x.sos_desc);
            ps.setString(3, x.sos_type);
            ps.setString(4, x.sos_amttype);
            ps.setDouble(5, x.sos_amt);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addOrderTaxDet(sod_tax x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sod_tax where sodt_nbr = ? and sodt_line = ?";
        String sqlInsert = "insert into sod_tax (sodt_nbr, sodt_line, sodt_desc," 
                        + "sodt_percent, sodt_type ) "
                        + " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sodt_nbr);
          ps.setString(1, x.sodt_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);    
            if (! res.isBeforeFirst()) { 
            ps.setString(1, x.sodt_nbr);
            ps.setString(2, x.sodt_line);
            ps.setString(3, x.sodt_desc);
            ps.setDouble(4, x.sodt_percent);
            ps.setString(5, x.sodt_type);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addOrderTaxMstr(so_tax x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from so_tax where sot_nbr = ? and sot_desc = ?";
        String sqlInsert = "insert into so_tax (sot_nbr, sot_desc, sot_percent, sot_type ) " 
                        + " values (?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sot_nbr);
          ps.setString(2, x.sot_desc);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sot_nbr);
            ps.setString(2, x.sot_desc);
            ps.setDouble(3, x.sot_percent);
            ps.setString(4, x.sot_type);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addServiceOrderMstr(sv_mstr x) {
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
            int rows = _addServiceOrderMstr(x, con, ps, res);  
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
    
    private static int _addServiceOrderMstr(sv_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from sv_mstr where sv_nbr = ?";
        String sqlInsert = "insert into sv_mstr (sv_nbr, sv_cust, sv_ship, sv_site, " +
                          " sv_po, sv_due_date, sv_create_date, sv_type, sv_status, sv_rmks, sv_curr, sv_char1, sv_taxcode  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sv_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sv_nbr);
            ps.setString(2, x.sv_cust);
            ps.setString(3, x.sv_ship);
            ps.setString(4, x.sv_site);
            ps.setString(5, x.sv_po);
            ps.setString(6, x.sv_due_date);
            ps.setString(7, x.sv_create_date);
            ps.setString(8, x.sv_type);
            ps.setString(9, x.sv_status);
            ps.setString(10, x.sv_rmks);
            ps.setString(11, x.sv_curr);
            ps.setString(12, x.sv_char1);
            ps.setString(13, x.sv_taxcode);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addServiceOrderDet(svd_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from svd_det where svd_nbr = ? and svd_line = ?";
        String sqlInsert = "insert into svd_det (svd_line, svd_item, svd_type, svd_desc, " +
                           " svd_nbr, svd_qty, svd_uom, svd_netprice  ) "
                        + " values (?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.svd_nbr);
          ps.setInt(2, x.svd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setInt(1, x.svd_line);
            ps.setString(2, x.svd_item);
            ps.setString(3, x.svd_type);
            ps.setString(4, x.svd_desc);
            ps.setString(5, x.svd_nbr);
            ps.setDouble(6, x.svd_qty);
            ps.setString(7, x.svd_uom);
            ps.setDouble(8, x.svd_netprice);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addServiceOrderTransaction(ArrayList<svd_det> svd, sv_mstr sv, ArrayList<sos_det> sos) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addServiceOrderTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(svd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sv);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sos);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
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
            _addServiceOrderMstr(sv, bscon, ps, res);  
            if (svd != null) {
                for (svd_det z : svd) {
                    _addServiceOrderDet(z, bscon, ps, res);
                }
            }
            if (sos != null) {
                for (sos_det z : sos) {
                    _addOrderSummaryDet(z, bscon, ps, res);
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
        
    public static String[] updateServiceOrderMstr(sv_mstr x) {
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
            int rows = _updateServiceOrderMstr(x, con, ps);  // add cms_det
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
   
    private static int _updateServiceOrderMstr(sv_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update sv_mstr set sv_cust = ?, sv_ship = ?, " +
                "sv_po = ?, sv_due_date = ?, sv_crew = ?, sv_rmks = ?, sv_status = ?, sv_taxcode = ?  " +
                 " where sv_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
        ps.setString(9, x.sv_nbr);
            ps.setString(1, x.sv_cust);
            ps.setString(2, x.sv_ship);
            ps.setString(3, x.sv_po);
            ps.setString(4, x.sv_due_date);
            ps.setString(5, x.sv_crew);
            ps.setString(6, x.sv_rmks);
            ps.setString(7, x.sv_status);
            ps.setString(8, x.sv_taxcode);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateServiceOrderDet(svd_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from svd_det where svd_nbr = ? and svd_line = ?";
        String sqlUpdate = "update svd_det set svd_item = ?, svd_qty = ?, " +
                 "svd_uom = ?, svd_netprice = ? " +
                 " where svd_nbr = ? and svd_line = ? ; ";
        String sqlInsert = "insert into svd_det (svd_line, svd_item, svd_type, svd_desc, " +
                           " svd_nbr, svd_qty, svd_uom, svd_netprice  ) "
                        + " values (?,?,?,?,?,?,?,?); ";  
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.svd_nbr);
        ps.setInt(2, x.svd_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	ps = con.prepareStatement(sqlInsert) ;
            ps.setInt(1, x.svd_line);
            ps.setString(2, x.svd_item);
            ps.setString(3, x.svd_type);
            ps.setString(4, x.svd_desc);
            ps.setString(5, x.svd_nbr);
            ps.setDouble(6, x.svd_qty);
            ps.setString(7, x.svd_uom);
            ps.setDouble(8, x.svd_netprice);
            rows = ps.executeUpdate();
        } else {    // update
        ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(5, x.svd_nbr);
            ps.setInt(6, x.svd_line);
            ps.setString(1, x.svd_item);
            ps.setDouble(2, x.svd_qty);
            ps.setString(3, x.svd_uom);
            ps.setDouble(4, x.svd_netprice);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    public static String[] deleteServiceOrderMstr(String order) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "deleteServiceOrderMstr"});
            list.add(new String[]{"param1", order});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        } 
        
        String[] m = new String[2];
        if (order == null || order.isBlank()) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteServiceOrderMstr(order, con);  
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
    
    private static void _deleteServiceOrderMstr(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        
        String po = "";
        String sqlSelect = "select * from sv_mstr where sv_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x);
          ResultSet res = ps.executeQuery();
            while(res.next()) {
                po = res.getString("sv_po");
            }
          res.close();
            
        
        String sql = "delete from sv_mstr where sv_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from svd_det where svd_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from sod_tax where sodt_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from sos_det where sos_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
       
        ps.close();
        
    }
    
    private static void _deleteServiceOrderLines(String x, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from svd_det where svd_nbr = ? and svd_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
    }
        
     // update order master.... multiple table transaction function
    public static String[] updateServiceOrderTransaction(String x, ArrayList<String> lines, ArrayList<svd_det> svd, sv_mstr sv, ArrayList<sos_det> sos) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateServiceOrderTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(lines);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(svd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sv);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sos);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
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
            for (String line : lines) {
               _deleteServiceOrderLines(x, line, bscon, ps);  // discard unwanted lines
             }
            for (svd_det z : svd) {
                if (z.svd_status.equals(getGlobalProgTag("closed"))) {
                    continue;
                }
                _updateServiceOrderDet(z, bscon, ps, res);
            }
            _deleteOrderSummaryDet(sv.sv_nbr, bscon);
            for (sos_det z : sos) {
                _addOrderSummaryDet(z, bscon, ps, res);
            }
             _updateServiceOrderMstr(sv, bscon, ps);  // update so_mstr
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
    
    public static String[] addPOSMstr(pos_mstr x) {
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
            int rows = _addPOSMstr(x, con, ps, res);  
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
        
    private static int _addPOSMstr(pos_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pos_mstr where pos_nbr = ?";
        String sqlInsert = "insert into pos_mstr (pos_nbr, pos_key, pos_type, pos_entity, pos_entityname, pos_entrydate, pos_entrytime, "
                        + " pos_aracct, pos_arcc, pos_totqty, pos_totlines, "
                        + " pos_tottax, pos_totamt, pos_bank, pos_grossamt, pos_status, pos_site ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
      
        
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.pos_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.pos_nbr);
            ps.setString(2, x.pos_key);
            ps.setString(3, x.pos_type);
            ps.setString(4, x.pos_entity);
            ps.setString(5, x.pos_entityname);
            ps.setString(6, x.pos_entrydate);
            ps.setString(7, x.pos_entrytime);
            ps.setString(8, x.pos_aracct);
            ps.setString(9, x.pos_arcc);
            ps.setString(10, x.pos_totqty);
            ps.setString(11, x.pos_totlines);
            ps.setString(12, x.pos_tottax);
            ps.setString(13, x.pos_totamt);
            ps.setString(14, x.pos_bank);
            ps.setString(15, x.pos_grossamt);
            ps.setString(16, x.pos_status);
            ps.setString(17, x.pos_site);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addPOSTransaction(ArrayList<pos_det> posd, pos_mstr pos) {
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
            _addPOSMstr(pos, bscon, ps, res);  
            for (pos_det z : posd) {
                _addPOSDet(z, bscon, ps, res);
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
     
    private static int _addPOSDet(pos_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pos_det where posd_nbr = ? and posd_line = ?";
        String sqlInsert = "insert into pos_det (posd_nbr, posd_line, posd_item, posd_desc, posd_ref, "
                        + " posd_qty, posd_listprice, posd_disc, posd_netprice, posd_tax, posd_acct, posd_cc ) " 
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?); "; 
        
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.posd_nbr);
          ps.setString(2, x.posd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.posd_nbr);
            ps.setString(2, x.posd_line);
            ps.setString(3, x.posd_item);
            ps.setString(4, x.posd_desc);
            ps.setString(5, x.posd_ref);
            ps.setString(6, x.posd_qty);
            ps.setString(7, x.posd_listprice);
            ps.setString(8, x.posd_disc);
            ps.setString(9, x.posd_netprice);
            ps.setString(10, x.posd_tax);
            ps.setString(11, x.posd_acct);
            ps.setString(12, x.posd_cc);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
     
    
    public static String[] addUpdateORCtrl(order_ctrl x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateORCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  order_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into order_ctrl (orc_autosource, orc_autoinvoice, orc_autoallocate, orc_custitem, " +
                            " orc_srvm_type, orc_srvm_item_default, orc_exceedqohu, orc_varchar) "
                        + " values (?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update order_ctrl set orc_autosource = ?, orc_autoinvoice = ?, orc_autoallocate = ?, orc_custitem = ?, " +
                            " orc_srvm_type = ?, orc_srvm_item_default = ?, orc_exceedqohu = ?, orc_varchar = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.orc_autosource);
            psi.setString(2, x.orc_autoinvoice);
            psi.setString(3, x.orc_autoallocate);
            psi.setString(4, x.orc_custitem);
            psi.setString(5, x.orc_srvm_type);
            psi.setString(6, x.orc_srvm_item_default);
            psi.setString(7, x.orc_exceedqohu);
            psi.setString(8, x.orc_varchar);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.orc_autosource);
            psu.setString(2, x.orc_autoinvoice);
            psu.setString(3, x.orc_autoallocate);
            psu.setString(4, x.orc_custitem);
            psu.setString(5, x.orc_srvm_type);
            psu.setString(6, x.orc_srvm_item_default);
            psu.setString(7, x.orc_exceedqohu);
            psu.setString(8, x.orc_varchar);
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
   
    public static order_ctrl getORCtrl(String[] x) {
        order_ctrl r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getORCtrl"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServORD");
                r = objectMapper.readValue(returnstring, order_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new order_ctrl(m);
                return r;
            }
        }
        String sql = "select * from order_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new order_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new order_ctrl(m, 
                                res.getString("orc_autosource"),
                                res.getString("orc_autoinvoice"),
                                res.getString("orc_autoallocate"),
                                res.getString("orc_custitem"),
                                res.getString("orc_srvm_type"),
                                res.getString("orc_srvm_item_default"),
                                res.getString("orc_exceedqohu"),
                                res.getString("orc_varchar")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new order_ctrl(m);
        }
        return r;
    }
    
    
    // Quote Master
    
    public static String[] addQuoteTransaction(ArrayList<quo_det> qod, quo_mstr qo, ArrayList<quo_sac> qsac) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addQuoteTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(qod);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(qo);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(qsac);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
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
            _addQuoteMstr(qo, bscon, ps, res);  
            for (quo_det z : qod) {
                _addQuoteDet(z, bscon, ps, res);
            }
            if (qsac != null) {
                for (quo_sac z : qsac) {
                    _addQuoteSAC(z, bscon, ps, res);
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
    
    public static String[] updateQuoteTransaction(String x, ArrayList<String> lines, ArrayList<quo_det> qod, quo_mstr qo, ArrayList<quo_sac> qsac) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateQuoteTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(lines);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(qod);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(qo);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(qsac);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
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
            
            quo_mstr db_qm = _getQuoteMstr(x, bscon, ps, res);
            if (db_qm != null && db_qm.quo_status().equals(getGlobalProgTag("closed"))) {
                  return new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1097)};
            }
            
            if (lines != null) {
                for (String line : lines) {
                   _deleteQuoteLines(x, line, bscon, ps);  // discard unwanted lines
                 }
            }
            if (qod != null) {
                for (quo_det z : qod) {                    
                    _updateQuoteDet(z, qo, bscon, ps, res);
                }
            }
            if (qsac != null) {
                _deleteQuoteSAC(qo.quo_nbr, bscon);
                for (quo_sac z : qsac) {
                    _addQuoteSAC(z, bscon, ps, res);
                }
            }
            if (qo != null) {
             _updateQuoteMstr(qo, bscon, ps);
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
        
    private static int _addQuoteMstr(quo_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from quo_mstr where quo_nbr = ?";
        String sqlInsert = "insert into quo_mstr (quo_nbr, quo_cust, quo_ship," +
            "quo_site,  quo_date,  quo_expire,  quo_priceexpire,  quo_status, " +
            "quo_rmks,  quo_ref,  quo_type,  quo_taxcode,  quo_disccode," +
            "quo_groupcode,  quo_curr,  quo_approved,  quo_approver,  quo_varchar, quo_terms ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.quo_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.quo_nbr);
            ps.setString(2, x.quo_cust);
            ps.setString(3, x.quo_ship);
            ps.setString(4, x.quo_site);
            ps.setString(5, x.quo_date);
            ps.setString(6, x.quo_expire);
            ps.setString(7, x.quo_priceexpire);
            ps.setString(8, x.quo_status);
            ps.setString(9, x.quo_rmks);
            ps.setString(10, x.quo_ref);
            ps.setString(11, x.quo_type);
            ps.setString(12, x.quo_taxcode);
            ps.setString(13, x.quo_disccode);
            ps.setString(14, x.quo_groupcode);
            ps.setString(15, x.quo_curr);
            ps.setString(16, x.quo_approved);
            ps.setString(17, x.quo_approver);
            ps.setString(18, x.quo_varchar);
            ps.setString(19, x.quo_terms);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static quo_mstr _getQuoteMstr(String x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        quo_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from quo_mstr where quo_nbr = ?";       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x);
          res = ps.executeQuery();
          while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new quo_mstr(m, 
                            res.getString("quo_nbr"), 
                            res.getString("quo_cust"),    
                            res.getString("quo_ship"),
                            res.getString("quo_site"),
                            res.getString("quo_date"),
                            res.getString("quo_expire"),  
                            res.getString("quo_priceexpire"),
                            res.getString("quo_status"),  
                            res.getString("quo_rmks"),
                            res.getString("quo_ref"),
                            res.getString("quo_type"),
                            res.getString("quo_taxcode"),
                            res.getString("quo_disccode"),
                            res.getString("quo_groupcode"),
                            res.getString("quo_curr"),
                            res.getString("quo_approved"),
                            res.getString("quo_approver"),
                            res.getString("quo_varchar"),
                            res.getString("quo_terms")
                        );
                    }
          
          return r;
    }
    
    
    private static int _updateQuoteMstr(quo_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update quo_mstr set quo_cust = ?, quo_ship = ?, " +
                "quo_site = ?, quo_date = ?, quo_expire = ?, quo_priceexpire = ?, quo_status = ?, quo_rmks = ?, " +
                "quo_ref = ?, quo_type = ?, quo_taxcode = ?, quo_disccode = ?, " +
                " quo_groupcode = ?, quo_curr = ?, quo_approved = ?, " +
                " quo_approver = ?, quo_varchar = ?, quo_terms = ? " +
                 " where quo_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
            ps.setString(19, x.quo_nbr);
            ps.setString(1, x.quo_cust);
            ps.setString(2, x.quo_ship);
            ps.setString(3, x.quo_site);
            ps.setString(4, x.quo_date);
            ps.setString(5, x.quo_expire);
            ps.setString(6, x.quo_priceexpire);
            ps.setString(7, x.quo_status);
            ps.setString(8, x.quo_rmks);
            ps.setString(9, x.quo_ref);
            ps.setString(10, x.quo_type);
            ps.setString(11, x.quo_taxcode);
            ps.setString(12, x.quo_disccode);
            ps.setString(13, x.quo_groupcode);
            ps.setString(14, x.quo_curr);
            ps.setString(15, x.quo_approved);
            ps.setString(16, x.quo_approver);
            ps.setString(17, x.quo_varchar);
            ps.setString(18, x.quo_terms);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _addQuoteDet(quo_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from quo_det where quod_nbr = ? and quod_line = ?";
        String sqlInsert = "insert into quo_det (quod_nbr, quod_line, quod_item," +
                "quod_isinv,  quod_desc,  quod_pricetype,  quod_listprice,  quod_disc, " +
                "quod_netprice,  quod_qty,  quod_uom ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.quod_nbr);
          ps.setInt(2, x.quod_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.quod_nbr);
            ps.setInt(2, x.quod_line);
            ps.setString(3, x.quod_item);
            ps.setString(4, x.quod_isinv);
            ps.setString(5, x.quod_desc);
            ps.setString(6, x.quod_pricetype);
            ps.setDouble(7, x.quod_listprice);
            ps.setDouble(8, x.quod_disc);
            ps.setDouble(9, x.quod_netprice);
            ps.setDouble(10, x.quod_qty);
            ps.setString(11, x.quod_uom);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _updateQuoteDet(quo_det x, quo_mstr z, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from quo_det where quod_nbr = ? and quod_line = ?";
        String sqlUpdate = "update quo_det set quod_item = ?, quod_isinv = ?, " +
                "quod_desc = ?, quod_pricetype = ?, quod_listprice = ?, quod_disc = ?, " +
                " quod_netprice = ?, quod_qty = ?, quod_uom = ? " +
                 " where quod_nbr = ? and quod_line = ? ; ";
        String sqlInsert = "insert into quo_det (quod_nbr, quod_line, quod_item," +
                "quod_isinv,  quod_desc,  quod_pricetype,  quod_listprice,  quod_disc, " +
                "quod_netprice,  quod_qty,  quod_uom ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.quod_nbr);
        ps.setInt(2, x.quod_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.quod_nbr);
            ps.setInt(2, x.quod_line);
            ps.setString(3, x.quod_item);
            ps.setString(4, x.quod_isinv);
            ps.setString(5, x.quod_desc);
            ps.setString(6, x.quod_pricetype);
            ps.setDouble(7, x.quod_listprice);
            ps.setDouble(8, x.quod_disc);
            ps.setDouble(9, x.quod_netprice);
            ps.setDouble(10, x.quod_qty);
            ps.setString(11, x.quod_uom); 
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(10, x.quod_nbr);
            ps.setInt(11, x.quod_line);
            ps.setString(1, x.quod_item);
            ps.setString(2, x.quod_isinv);
            ps.setString(3, x.quod_desc);
            ps.setString(4, x.quod_pricetype);
            ps.setDouble(5, x.quod_listprice);
            ps.setDouble(6, x.quod_disc);
            ps.setDouble(7, x.quod_netprice);
            ps.setDouble(8, x.quod_qty);
            ps.setString(9, x.quod_uom); 
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    private static int _addQuoteSAC(quo_sac x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from quo_sac where quos_nbr = ? and quos_desc = ?";
        String sqlInsert = "insert into quo_sac (quos_nbr, quos_desc, quos_type, " 
                        + "quos_amttype, quos_amt, quos_appcode ) "
                        + " values (?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.quos_nbr);
          ps.setString(2, x.quos_desc);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.quos_nbr);
            ps.setString(2, x.quos_desc);
            ps.setString(3, x.quos_type);
            ps.setString(4, x.quos_amttype);
            ps.setDouble(5, x.quos_amt);
            ps.setString(6, x.quos_appcode);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static void _deleteQuoteSAC(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from quo_sac where quos_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
        
    public static String[] deleteQuoteLines(String x, ArrayList<String> lines) {
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
             for (String line : lines) {
               _deleteQuoteLines(x, line, con, ps);  // add cms_det
             }
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
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _deleteQuoteLines(String x, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from quo_det where quod_nbr = ? and quod_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
    }
    
    public static String[] deleteQuoteMstr(String x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "deleteQuoteMstr"});
            list.add(new String[]{"param1", x});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServORD"));
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
            _deleteQuoteMstr(x, con);  // add cms_det
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
    
    private static void _deleteQuoteMstr(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from quo_mstr where quo_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from quo_det where quod_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from quo_sac where quos_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    public static quo_mstr getQuoteMstr(String[] x) {
        quo_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getQuoteMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServORD");
                r = objectMapper.readValue(returnstring, quo_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from quo_mstr where quo_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new quo_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new quo_mstr(m, 
                            res.getString("quo_nbr"), 
                            res.getString("quo_cust"),    
                            res.getString("quo_ship"),
                            res.getString("quo_site"),
                            res.getString("quo_date"),
                            res.getString("quo_expire"),  
                            res.getString("quo_priceexpire"),
                            res.getString("quo_status"),  
                            res.getString("quo_rmks"),
                            res.getString("quo_ref"),
                            res.getString("quo_type"),
                            res.getString("quo_taxcode"),
                            res.getString("quo_disccode"),
                            res.getString("quo_groupcode"),
                            res.getString("quo_curr"),
                            res.getString("quo_approved"),
                            res.getString("quo_approver"),
                            res.getString("quo_varchar"),
                            res.getString("quo_terms")
                        );
                    }
                }
            } 
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new quo_mstr(m);
        }
        return r;
    }
   
    public static ArrayList<quo_det> getQuoteDet(String code) {
        quo_det r = null;
        String[] m = new String[2];
        ArrayList<quo_det> list = new ArrayList<quo_det>();
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getQuoteDet"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServORD");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<quo_det>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        String sql = "select * from quo_det where quod_nbr = ? order by quod_line ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(code));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new quo_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new quo_det(m, res.getString("quod_nbr"), 
                                res.getInt("quod_line"), 
                                res.getString("quod_item"), 
                                res.getString("quod_isinv"), 
                                res.getString("quod_desc"),
                                res.getString("quod_pricetype"), 
                                res.getDouble("quod_listprice"),
                                res.getDouble("quod_disc"),
                                res.getDouble("quod_netprice"),
                                res.getDouble("quod_qty"),
                                res.getString("quod_uom"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new quo_det(m);
               list.add(r);
        }
        return list;
    }
   
    public static ArrayList<quo_sac> getQuoteSAC(String code) {
        quo_sac r = null;
        String[] m = new String[2];
        ArrayList<quo_sac> list = new ArrayList<quo_sac>();
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getQuoteSAC"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServORD");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<quo_sac>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        String sql = "select * from quo_sac where quos_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(code));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new quo_sac(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new quo_sac(m, res.getString("quos_nbr"), 
                                res.getString("quos_desc"), 
                                res.getString("quos_type"), 
                                res.getString("quos_amttype"), 
                                res.getDouble("quos_amt"),
                                res.getString("quos_appcode"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new quo_sac(m);
               list.add(r);
        }
        return list;
    }
       
    public static ArrayList<String> getQuoteLines(String nbr) {
        ArrayList<String> lines = new ArrayList<String>();
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getQuoteLines"});
            list.add(new String[]{"param1", nbr});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
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

           res = st.executeQuery("SELECT quod_line from quo_det " +
                   " where quod_nbr = " + "'" + nbr + "'" + " order by quod_line;");
                        while (res.next()) {
                          lines.add(res.getString("quod_line"));
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
    
    // Billing Recurrable
    
    public static String[] addBillingTransaction(ArrayList<bill_det> bd, bill_mstr bm, ArrayList<bill_sac> bsac) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addBillingTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(bd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(bm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(bsac);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
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
            _addBillMstr(bm, bscon, ps, res);  
            for (bill_det z : bd) {
                _addBillDet(z, bscon, ps, res);
            }
            if (bsac != null) {
                for (bill_sac z : bsac) {
                    _addBillSAC(z, bscon, ps, res);
                }
            }
            
            String nbd = findNextBillDate(LocalDate.parse(bm.bill_servicedate), bm.bill_billingtype, bm.bill_frequencytype);
            _updateBillNextDate(bm.bill_nbr, nbd, null, bscon);
            
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
    
    public static String[] updateBillingTransaction(String x, ArrayList<String> lines, ArrayList<bill_det> bd, bill_mstr bm, ArrayList<bill_sac> bsac) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateBillingTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(lines);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(bd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(bm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(bsac);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServORD"));
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
            for (String line : lines) {
               _deleteBillLines(x, line, bscon, ps);  // discard unwanted lines
             }
            for (bill_det z : bd) {
                if (bm.bill_acctstatus().equals(getGlobalProgTag("closed"))) {
                    continue;
                }
                _updateBillDet(z, bm, bscon, ps, res);
            }
            _deleteBillSAC(bm.bill_nbr, bscon);
            for (bill_sac z : bsac) {
                _addBillSAC(z, bscon, ps, res);
            }
             _updateBillMstr(bm, bscon, ps);  
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
        
    private static int _addBillMstr(bill_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from bill_mstr where bill_nbr = ?";
        String sqlInsert = "insert into bill_mstr (bill_nbr, bill_cust, " +
                    "bill_site, bill_servicedate, bill_billingdate," +
                    " bill_termdate, bill_lastbilldate, bill_nextbilldate," +
                    " bill_acctstatus, bill_orderstatus, bill_rmks, bill_ref," +
                    " bill_type, bill_servicetype, bill_subtype, bill_billingtype, " +
                    " bill_frequencytype, bill_group, bill_category, bill_terms, bill_autobill ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.bill_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.bill_nbr);
            ps.setString(2, x.bill_cust);
            ps.setString(3, x.bill_site);
            ps.setString(4, x.bill_servicedate);
            ps.setString(5, x.bill_billingdate);
            ps.setString(6, x.bill_termdate);
            ps.setString(7, x.bill_lastbilldate);
            ps.setString(8, x.bill_nextbilldate);
            ps.setString(9, x.bill_acctstatus);
            ps.setString(10, x.bill_orderstatus);
            ps.setString(11, x.bill_rmks);
            ps.setString(12, x.bill_ref);
            ps.setString(13, x.bill_type);
            ps.setString(14, x.bill_servicetype);
            ps.setString(15, x.bill_subtype);
            ps.setString(16, x.bill_billingtype);
            ps.setString(17, x.bill_frequencytype);
            ps.setString(18, x.bill_group);
            ps.setString(19, x.bill_category);
            ps.setString(20, x.bill_terms);
            ps.setString(21, x.bill_autobill);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _updateBillMstr(bill_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update bill_mstr set bill_cust = ?, " +
                    "bill_site = ?, bill_servicedate = ?, bill_billingdate = ?," +
                    " bill_termdate = ?, bill_nextbilldate = ?, " +
                    " bill_acctstatus = ?, bill_orderstatus = ?, bill_rmks = ?, bill_ref = ?," +
                    " bill_type = ?, bill_servicetype = ?, bill_subtype = ?, bill_billingtype = ?, " +
                    " bill_frequencytype = ?, bill_group = ?, bill_category = ?, bill_terms = ?, bill_autobill = ? " +
                 " where bill_nbr = ? ; ";
	ps = con.prepareStatement(sql) ;
            ps.setString(20, x.bill_nbr);
            ps.setString(1, x.bill_cust);
            ps.setString(2, x.bill_site);
            ps.setString(3, x.bill_servicedate);
            ps.setString(4, x.bill_billingdate);
            ps.setString(5, x.bill_termdate);
            ps.setString(6, x.bill_nextbilldate);
            ps.setString(7, x.bill_acctstatus);
            ps.setString(8, x.bill_orderstatus);
            ps.setString(9, x.bill_rmks);
            ps.setString(10, x.bill_ref);
            ps.setString(11, x.bill_type);
            ps.setString(12, x.bill_servicetype);
            ps.setString(13, x.bill_subtype);
            ps.setString(14, x.bill_billingtype);
            ps.setString(15, x.bill_frequencytype);
            ps.setString(16, x.bill_group);
            ps.setString(17, x.bill_category);
            ps.setString(18, x.bill_terms);
            ps.setString(19, x.bill_autobill);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _addBillDet(bill_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from bill_det where billd_nbr = ? and billd_line = ?";
        String sqlInsert = "insert into bill_det (billd_nbr, billd_line, billd_item," +
                "billd_isinv,  billd_desc,  billd_pricetype,  billd_listprice,  billd_disc, " +
                "billd_netprice,  billd_qty,  billd_uom ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.billd_nbr);
          ps.setInt(2, x.billd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.billd_nbr);
            ps.setInt(2, x.billd_line);
            ps.setString(3, x.billd_item);
            ps.setString(4, x.billd_isinv);
            ps.setString(5, x.billd_desc);
            ps.setString(6, x.billd_pricetype);
            ps.setDouble(7, x.billd_listprice);
            ps.setDouble(8, x.billd_disc);
            ps.setDouble(9, x.billd_netprice);
            ps.setDouble(10, x.billd_qty);
            ps.setString(11, x.billd_uom);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _updateBillDet(bill_det x, bill_mstr z, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from bill_det where billd_nbr = ? and billd_line = ?";
        String sqlUpdate = "update bill_det set billd_item = ?, billd_isinv = ?, " +
                "billd_desc = ?, billd_pricetype = ?, billd_listprice = ?, billd_disc = ?, " +
                " billd_netprice = ?, billd_qty = ?, billd_uom = ? " +
                 " where billd_nbr = ? and billd_line = ? ; ";
        String sqlInsert = "insert into bill_det (billd_nbr, billd_line, billd_item," +
                "billd_isinv,  billd_desc,  billd_pricetype,  billd_listprice,  billd_disc, " +
                "billd_netprice,  billd_qty,  billd_uom ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.billd_nbr);
        ps.setInt(2, x.billd_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.billd_nbr);
            ps.setInt(2, x.billd_line);
            ps.setString(3, x.billd_item);
            ps.setString(4, x.billd_isinv);
            ps.setString(5, x.billd_desc);
            ps.setString(6, x.billd_pricetype);
            ps.setDouble(7, x.billd_listprice);
            ps.setDouble(8, x.billd_disc);
            ps.setDouble(9, x.billd_netprice);
            ps.setDouble(10, x.billd_qty);
            ps.setString(11, x.billd_uom); 
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(10, x.billd_nbr);
            ps.setInt(11, x.billd_line);
            ps.setString(1, x.billd_item);
            ps.setString(2, x.billd_isinv);
            ps.setString(3, x.billd_desc);
            ps.setString(4, x.billd_pricetype);
            ps.setDouble(5, x.billd_listprice);
            ps.setDouble(6, x.billd_disc);
            ps.setDouble(7, x.billd_netprice);
            ps.setDouble(8, x.billd_qty);
            ps.setString(9, x.billd_uom); 
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    private static int _addBillSAC(bill_sac x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from bill_sac where bills_nbr = ? and bills_desc = ?";
        String sqlInsert = "insert into bill_sac (bills_nbr, bills_desc, bills_type, " 
                        + "bills_amttype, bills_amt, bills_appcode ) "
                        + " values (?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.bills_nbr);
          ps.setString(2, x.bills_desc);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.bills_nbr);
            ps.setString(2, x.bills_desc);
            ps.setString(3, x.bills_type);
            ps.setString(4, x.bills_amttype);
            ps.setDouble(5, x.bills_amt);
            ps.setString(6, x.bills_appcode);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static void _deleteBillSAC(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from bill_sac where bills_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
        
    public static String[] deleteBillLines(String x, ArrayList<String> lines) {
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
             for (String line : lines) {
               _deleteBillLines(x, line, con, ps);  // add cms_det
             }
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
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    private static void _deleteBillLines(String x, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from bill_det where billd_nbr = ? and billd_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
    }
    
    public static String[] deleteBillMstr(String x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "deleteBillMstr"});
            list.add(new String[]{"param1", x});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServORD"));
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
            _deleteBillMstr(x, con);  // add cms_det
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
    
    private static void _deleteBillMstr(String x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from bill_mstr where bill_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from bill_det where billd_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from bill_sac where bills_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    public static bill_mstr getBillMstr(String[] x) {
        bill_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getBillMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServORD");
                r = objectMapper.readValue(returnstring, bill_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from bill_mstr where bill_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(x[0]));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new bill_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                     
                        r = new bill_mstr(m, 
                            res.getString("bill_nbr"),
                            res.getString("bill_cust"),
                            res.getString("bill_site"),
                            res.getString("bill_servicedate"),
                            res.getString("bill_billingdate"),
                            res.getString("bill_termdate"),
                            res.getString("bill_lastbilldate"),
                            res.getString("bill_nextbilldate"),
                            res.getString("bill_acctstatus"),
                            res.getString("bill_orderstatus"),
                            res.getString("bill_rmks"),
                            res.getString("bill_ref"),
                            res.getString("bill_type"),
                            res.getString("bill_servicetype"),
                            res.getString("bill_subtype"),
                            res.getString("bill_billingtype"),
                            res.getString("bill_frequencytype"),
                            res.getString("bill_group"),
                            res.getString("bill_category"),
                            res.getString("bill_terms"),
                            res.getString("bill_autobill"));
                    }
                }
            } 
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new bill_mstr(m);
        }
        return r;
    }
   
    public static bill_mstr _getBillMstr(String x, Connection bscon, PreparedStatement ps, ResultSet res) throws SQLException {
        bill_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from bill_mstr where bill_nbr = ?";
          ps = bscon.prepareStatement(sqlSelect); 
          ps.setString(1, x);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new bill_mstr(m);
            } else {
                while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                     
                        r = new bill_mstr(m, 
                            res.getString("bill_nbr"),
                            res.getString("bill_cust"),
                            res.getString("bill_site"),
                            res.getString("bill_servicedate"),
                            res.getString("bill_billingdate"),
                            res.getString("bill_termdate"),
                            res.getString("bill_lastbilldate"),
                            res.getString("bill_nextbilldate"),
                            res.getString("bill_acctstatus"),
                            res.getString("bill_orderstatus"),
                            res.getString("bill_rmks"),
                            res.getString("bill_ref"),
                            res.getString("bill_type"),
                            res.getString("bill_servicetype"),
                            res.getString("bill_subtype"),
                            res.getString("bill_billingtype"),
                            res.getString("bill_frequencytype"),
                            res.getString("bill_group"),
                            res.getString("bill_category"),
                            res.getString("bill_terms"),
                            res.getString("bill_autobill"));
                    }
            }
            return r;
    }
    
    public static ArrayList<bill_det> getBillDet(String code) {
        
        ArrayList<bill_det> list = new ArrayList<bill_det>();
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getBillDet"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServORD");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<bill_det>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        bill_det r;
        String sql = "select * from bill_det where billd_nbr = ? order by billd_line ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(code));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new bill_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new bill_det(m, res.getString("billd_nbr"), 
                                res.getInt("billd_line"), 
                                res.getString("billd_item"), 
                                res.getString("billd_isinv"), 
                                res.getString("billd_desc"),
                                res.getString("billd_pricetype"), 
                                res.getDouble("billd_listprice"),
                                res.getDouble("billd_disc"),
                                res.getDouble("billd_netprice"),
                                res.getDouble("billd_qty"),
                                res.getString("billd_uom"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new bill_det(m);
               list.add(r);
        }
        return list;
    }
   
    public static ArrayList<bill_det> _getBillDet(String x, Connection bscon, PreparedStatement ps, ResultSet res) throws SQLException {
        bill_det r = null;
        String[] m = new String[2];
        ArrayList<bill_det> list = new ArrayList<bill_det>();
        String sql = "select * from bill_det where billd_nbr = ? order by billd_line ;";
	ps = bscon.prepareStatement(sql);
        ps.setString(1, x);
        res = ps.executeQuery();
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new bill_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new bill_det(m, res.getString("billd_nbr"), 
                                res.getInt("billd_line"), 
                                res.getString("billd_item"), 
                                res.getString("billd_isinv"), 
                                res.getString("billd_desc"),
                                res.getString("billd_pricetype"), 
                                res.getDouble("billd_listprice"),
                                res.getDouble("billd_disc"),
                                res.getDouble("billd_netprice"),
                                res.getDouble("billd_qty"),
                                res.getString("billd_uom"));
                        list.add(r);
                    }
                }
        return list;
    }
   
    public static ArrayList<bill_sac> getBillSAC(String code) {
        bill_sac r = null;
        String[] m = new String[2];
        ArrayList<bill_sac> list = new ArrayList<bill_sac>();
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getBillSAC"});
            paramlist.add(new String[]{"param1",code});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServORD");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<bill_sac>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        String sql = "select * from bill_sac where bills_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, bsParseInt(code));
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new bill_sac(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new bill_sac(m, res.getString("bills_nbr"), 
                                res.getString("bills_desc"), 
                                res.getString("bills_type"), 
                                res.getString("bills_amttype"), 
                                res.getDouble("bills_amt"),
                                res.getString("bills_appcode"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new bill_sac(m);
               list.add(r);
        }
        return list;
    }
     
    public static ArrayList<bill_sac> _getBillSAC(String x, Connection bscon, PreparedStatement ps, ResultSet res) throws SQLException {
        bill_sac r = null;
        String[] m = new String[2];
        ArrayList<bill_sac> list = new ArrayList<bill_sac>();
        String sql = "select * from bill_sac where bills_nbr = ? ;";
        ps = bscon.prepareStatement(sql);
        ps.setString(1, x);
        res = ps.executeQuery();
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new bill_sac(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new bill_sac(m, res.getString("bills_nbr"), 
                                res.getString("bills_desc"), 
                                res.getString("bills_type"), 
                                res.getString("bills_amttype"), 
                                res.getDouble("bills_amt"),
                                res.getString("bills_appcode"));
                        list.add(r);
                    }
                }
        return list;
    }
    
    public static ArrayList<String> getBillLines(String nbr) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBillLines"});
            list.add(new String[]{"param1", nbr});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServORD"));
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

           res = st.executeQuery("SELECT billd_line from bill_det " +
                   " where billd_nbr = " + "'" + nbr + "'" + " order by billd_line;");
                        while (res.next()) {
                          lines.add(res.getString("billd_line"));
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
    
    public static int _addBillTran(bill_tran x, Connection con) throws SQLException {
        int rows = 0;
        String sqlInsert = "insert into bill_tran ( billt_nbr, " +
            " billt_invoice, billt_amt, billt_invdate," +
            " billt_billingtype, billt_frequencytype, billt_servicedate," +
            " billt_billingdate, billt_usage, billt_qty," +
            " billt_startdate, billt_enddate, billt_remarks, billt_status)" +
               " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
          // tr_id and tr_timestamp are db assigned         
        try (PreparedStatement psi = con.prepareStatement(sqlInsert)) {
            psi.setString(1, x.billt_nbr);
            psi.setString(2, x.billt_invoice);
            psi.setDouble(3, x.billt_amt);
            psi.setString(4, x.billt_invdate);
            psi.setString(5, x.billt_billingtype);
            psi.setString(6, x.billt_frequencytype);
            psi.setString(7, x.billt_servicedate);
            psi.setString(8, x.billt_billingdate);
            psi.setString(9, x.billt_usage);
            psi.setDouble(10, x.billt_qty);
            psi.setString(11, x.billt_startdate);
            psi.setString(12, x.billt_enddate);
            psi.setString(13, x.billt_remarks);
            psi.setString(14, x.billt_status);
            
            rows = psi.executeUpdate();
        }
        return rows;
    }
    
    public static String billTransAll() {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "billTransAll"});
            try {
                return sendServerPost(list, "", null, "dataServORD");
            } catch (IOException ex) {
                bslog(ex);
                return "";
            }
        }
        
        ArrayList<String> bills = new ArrayList<String>();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try{
        Connection con = null;
        ResultSet res = null;
        PreparedStatement ps = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        try{
          //  Statement st = con.createStatement();
            String sql = "SELECT bill_nbr from bill_mstr " +
                   " where bill_acctstatus <> 'closed' " +
                   " and bill_autobill = '1' " + 
                   " and bill_nextbilldate <= " + "'" + today + "'" +
                   " order by bill_nbr;";
            ps = con.prepareStatement(sql);
            res = ps.executeQuery();
                while (res.next()) {
                  bills.add(res.getString("bill_nbr"));
                }
        
            for (String b : bills) {
                _billTrans(_getBillMstr(b, con, ps, res), _getBillDet(b, con, ps, res), con);
            }            
         // st.close();
         // res.close();
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (ps != null) ps.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
     
        return "processed billing count: " + bills.size();
    }
    
    public static void _billTrans(bill_mstr bm, ArrayList<bill_det> bd, Connection bscon) throws SQLException {
        
        if (bm.bill_nextbilldate().isBlank()) {
            return;
        }
        // check if we already have a billing record...if so...bale
        if (getBillTranByDate(bm.bill_nbr(), parseDateLD(bm.bill_nextbilldate())) != null) {
           return;   // tran record already created...bale
        } 
        String[] custdata = getCustInfo(bm.bill_cust());
        String[] m = null;
        LocalDate xstart = null;
        LocalDate xend = null;
        String usage = "";
        double amt = 0.00;
        LocalDate now = LocalDate.now();
    
        // get last tran record...if any
       String[] lasttran = getBillTranLast(bm.bill_nbr());  // id, invoice nbr, start, end
       
     //  if (lasttran == null) {
       //    xstart = parseDateLD(bm.bill_servicedate());
           //xend = now.withDayOfMonth(now.lengthOfMonth());
       //    xend = xstart.plusMonths(1);
     //  } else {
         //  xstart = parseDateLD(lasttran[3]).plusDays(1);
         //  xend = xstart.plusDays(xstart.lengthOfMonth()); // total for year should sum to 365 or 366
         
          // !!! this billing method only bills for service of the previous month prior to billing execution...does not retro bill
           int day = parseDateLD(bm.bill_servicedate()).getDayOfMonth();
           xstart = now.withDayOfMonth(day).minusMonths(1); 
           xend = xstart.plusMonths(1);
           // Current bill date must be >= period end date
           if (xend.isAfter(now)) {
               return;
           }
    //   }
        
       int shipperid = OVData.getNextNbr("shipper", bscon);
       
       // create ship mstr
       shpData.ship_mstr sh = shpData.createShipMstrJRT(String.valueOf(shipperid), 
                bm.bill_site(),
                String.valueOf(shipperid), 
                bm.bill_cust(),
                bm.bill_cust(), // shipto ...same as billto or null if a cust code was created without a corresponding 'same as' shipto
                bsNumberToUS(bm.bill_nbr()),
                bm.bill_nbr(),  // po 
                bm.bill_ref(),  // ref
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), //duedate
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),  // orddate
                bm.bill_rmks(),
                "", // shipvia
                "B", 
                custdata[8],
                bm.bill_site(),
                "" // tracking
                ); 
       
       // create shp_det 
       ArrayList<shpData.ship_det> shd = new ArrayList<shpData.ship_det>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // line, item, order, orderline, po, qty, netprice, desc, wh, loc, disc, listprice, tax, cont, serial
        
        for (bill_det bdline : bd) {
            shpData.ship_det x = new shpData.ship_det(null, 
                String.valueOf(shipperid), // shipper
                bdline.billd_line(), //shline
                bdline.billd_item(), // item
                "", // custimtem
                bdline.billd_nbr(),  // order
                bdline.billd_line(), //soline    
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "", // po
                bdline.billd_qty(), // qty
                bdline.billd_uom(), //uom
                custdata[2], //currency
                bdline.billd_netprice(), // net price
                0, // disc
                bdline.billd_listprice(), // list price
                "Period( " + xstart.toString() + " - " + xend.toString() + ")", // bdline.billd_desc(), // desc
                "", // wh
                "", // loc
                0, // taxamt
                "0", // cont
                "", // ref
                "", // serial   
                bm.bill_site(),
                "", // bom
                0,  // packqty
                "" // kvpair    
                );
        shd.add(x);
        amt += bdline.billd_netprice() * bdline.billd_qty();
        }      
       
        bscon.setAutoCommit(false);    
        try {                
        _addShipperTransaction(shd, sh, null, bscon);
        _updateShipperSAC(sh.sh_id(), bscon);
        m = _confirmShipperTransaction("bill", String.valueOf(shipperid), new java.util.Date(), bscon);
        bslog(m[0] + " " + m[1]);
        
      
       bill_tran bt = new bill_tran(null, 
                "", // primary key
                bm.bill_nbr(), 
                String.valueOf(shipperid), // invoice
                0, // amt
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), // invdate
                bm.bill_billingtype(),
                bm.bill_frequencytype(),
                bm.bill_servicedate(),
                bm.bill_billingdate(),
                "", // usage  ...to be used later for actual service measurements for period
                0, // qty  ...to be used later for actual service measurements for period
                xstart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), // xstartdate
                xend.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), // xenddate
                bm.bill_rmks(), // remarks
                "open" // status
        );  
        _addBillTran(bt, bscon);
        LocalDate nbd = LocalDate.parse(bm.bill_nextbilldate());
        if (bm.bill_frequencytype().equals("monthly")) {
            nbd = nbd.plusMonths(1);
        }
        if (bm.bill_frequencytype().equals("yearly")) {
            nbd = nbd.plusYears(1);
        }
        if (bm.bill_frequencytype().equals("weekly")) {
            nbd = nbd.plusWeeks(1);
        }
        
        _updateBillNextDate(bm.bill_nbr(), 
                nbd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                bscon
                );
        
        bscon.commit();
       
        // email invoice
        String rfile = OVData.printInvoiceRemote(String.valueOf(shipperid), "shipper", false);
          if (rfile != null && ! rfile.isBlank()) {
           m = OVData.sendInvoice(String.valueOf(shipperid), bm.bill_site(), rfile);
           System.out.println("billing: "  + m[1]);
          }
        } catch (SQLException e) {
           try {
                 bscon.rollback();
             } catch (SQLException rb) {
                 MainFrame.bslog(rb);
             } 
        } 
    }
    
    
    // miscellaneous SQL queries
    public static String getOrdRptPickerData(String[] keys) {
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
                if (keys[0].equals("ordersDueDateByRange")) {
                res = st.executeQuery("SELECT so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name, " +
                    " sum(sod_ord_qty * sod_netprice) as 'total' " +
                    " from so_mstr inner join sod_det on so_nbr = sod_nbr " +
                    " inner join cm_mstr on cm_code = so_cust " +
                    " where " +
                    " so_due_date >= " + "'" + keys[1] + "'" +
                    " and so_due_date <= " + "'" + keys[2] + "'" +         
                    " group by so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name order by so_nbr ;");
                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("so_nbr"));
                            rowArray.put(res.getString("so_po"));
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("so_ord_date"));
                            rowArray.put(res.getString("so_due_date"));
                            rowArray.put(res.getString("so_status"));
                            rowArray.put(currformat(res.getString("total")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("ordersOrdDateByRange")) {
                res = st.executeQuery("SELECT so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name, " +
                    " sum(sod_ord_qty * sod_netprice) as 'total' " +
                    " from so_mstr inner join sod_det on so_nbr = sod_nbr " +
                    " inner join cm_mstr on cm_code = so_cust " +
                    " where " +
                    " so_ord_date >= " + "'" + keys[1] + "'" +
                    " and so_ord_date <= " + "'" + keys[2] + "'" +         
                    " group by so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name order by so_nbr ;");
                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("so_nbr"));
                            rowArray.put(res.getString("so_po"));
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("so_ord_date"));
                            rowArray.put(res.getString("so_due_date"));
                            rowArray.put(res.getString("so_status"));
                            rowArray.put(currformat(res.getString("total")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("ordersCustDateByRange")) {
                res = st.executeQuery("SELECT so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name, " +
                    " sum(sod_ord_qty  * sod_netprice) as 'total' " +
                    " from so_mstr inner join sod_det on so_nbr = sod_nbr " +
                    " inner join cm_mstr on cm_code = so_cust " +
                    " where " +
                    " so_cust >= " + "'" + keys[1] + "'" +
                    " and so_cust <= " + "'" + keys[2] + "'" +   
                    " and so_ord_date >= " + "'" + keys[3] + "'" +
                    " and so_ord_date <= " + "'" + keys[4] + "'" +         
                    " group by so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name order by so_nbr ;");
                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("so_nbr"));
                            rowArray.put(res.getString("so_po"));
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("so_ord_date"));
                            rowArray.put(res.getString("so_due_date"));
                            rowArray.put(res.getString("so_status"));
                            rowArray.put(currformat(res.getString("total")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("ordersOpenByCust")) {
                res = st.executeQuery("SELECT so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name, " +
                    " sum(sod_ord_qty  * sod_netprice) as 'total' " +
                    " from so_mstr inner join sod_det on so_nbr = sod_nbr " +
                    " inner join cm_mstr on cm_code = so_cust " +
                    " where " +
                    " so_cust >= " + "'" + keys[1] + "'" +
                    " and so_cust <= " + "'" + keys[2] + "'" +   
                    " and so_status <> " + "'" + "close" + "'" +       
                    " group by so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name order by so_nbr ;");
                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("so_nbr"));
                            rowArray.put(res.getString("so_po"));
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("so_ord_date"));
                            rowArray.put(res.getString("so_due_date"));
                            rowArray.put(res.getString("so_status"));
                            rowArray.put(currformat(res.getString("total")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("ordersOpenDetailByCust")) {
                res = st.executeQuery("SELECT so_nbr, so_po, so_status, sod_item, cm_code, cm_name, " +
                    " sod_ord_qty, sod_shipped_qty, (sod_ord_qty - sod_shipped_qty) as 'remaining' " +
                    " from so_mstr inner join sod_det on so_nbr = sod_nbr " +
                    " inner join cm_mstr on cm_code = so_cust " +
                    " where " +
                    " so_cust >= " + "'" + keys[1] + "'" +
                    " and so_cust <= " + "'" + keys[2] + "'" +   
                    " and so_status <> " + "'" + "close" + "'" +       
                    " order by so_nbr ;");                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("so_nbr"));
                            rowArray.put(res.getString("so_po"));
                            rowArray.put(res.getString("so_status"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("sod_item"));
                            rowArray.put(res.getString("sod_ord_qty"));
                            rowArray.put(res.getString("sod_shipped_qty"));
                            rowArray.put(res.getString("remaining"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("ordersOnHoldByCust")) {
                res = st.executeQuery("SELECT so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name, " +
                    " sum(sod_ord_qty  * sod_netprice) as 'total' " +
                    " from so_mstr inner join sod_det on so_nbr = sod_nbr " +
                    " inner join cm_mstr on cm_code = so_cust " +
                    " where " +
                    " so_cust >= " + "'" + keys[1] + "'" +
                    " and so_cust <= " + "'" + keys[2] + "'" +   
                    " and so_status = " + "'" + "onhold" + "'" +       
                    " group by so_nbr, so_po, so_status, so_ord_date, so_due_date, cm_code, cm_name order by so_nbr ;");
                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("so_nbr"));
                            rowArray.put(res.getString("so_po"));
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("so_ord_date"));
                            rowArray.put(res.getString("so_due_date"));
                            rowArray.put(res.getString("so_status"));
                            rowArray.put(currformat(res.getString("total")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("ordersShippedDetailByPO")) {
                res = st.executeQuery("SELECT sod_nbr, shd_po, sh_id, sh_shipdate,  " +
                    " sod_ord_qty, shd_qty, shd_item, shd_desc, sh_rmks " +
                    " from ship_det inner join ship_mstr on sh_id = shd_id " +
                    " inner join sod_det on sod_nbr = shd_so and sod_line = shd_soline" + 
                    " where " +
                    " shd_po >= " + "'" + keys[1] + "'" +
                    " and shd_po <= " + "'" + keys[2] + "'" +  
                    " order by shd_po ;"); 
               
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("sod_nbr"));
                            rowArray.put(res.getString("shd_po"));
                            rowArray.put(res.getString("sh_id"));
                            rowArray.put(res.getString("sh_shipdate"));
                            rowArray.put(res.getString("sh_rmks"));
                            rowArray.put(res.getString("shd_item"));
                            rowArray.put(res.getString("shd_desc"));
                            rowArray.put(res.getString("shd_qty"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("ordersTaxByCustDate")) {
                res = st.executeQuery("SELECT so_nbr, so_cust, so_po, so_ord_date, so_status, cm_code, cm_name,  " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when sos_type <> 'tax' and sos_type <> 'passive' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " inner join cm_mstr on cm_code = so_cust " +
                        " where so_ord_date >= " + "'" + keys[3]  + "'" + 
                        " AND so_ord_date <= " + "'" + keys[4] + "'" + 
                        " AND so_cust >= " + "'" + keys[1] + "'" + 
                        " AND so_cust <= " + "'" + keys[2] + "'" + 
                        " AND so_type = 'DISCRETE' " +
                         " group by so_nbr, so_cust, so_po, so_ord_date, so_status order by so_nbr desc ;");   
               
                    double total = 0;
                    double tax = 0;
                    double disc = 0;
                    double charge = 0;
                    while (res.next()) {
                        
                        total = 0;
                        tax = 0;
                        disc = 0;
                        charge = 0;
                    
                        if (res.getDouble("discountpercent") != 0) {
                          disc = res.getDouble("totdol") * (res.getDouble("discountpercent") / 100.0);
                        } else {
                          disc = 0;  
                        }
                        charge = res.getDouble("charge");
                        total = res.getDouble("totdol") + charge;  // charges added to total before taxing

                        // now do tax
                        if (res.getDouble("taxpercent") != 0) {
                          tax = total * (res.getDouble("taxpercent") / 100.0);
                        } else {
                          tax = 0;  
                        }
                        tax += res.getDouble("taxcharge");

                        total = total + tax;
                        
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("so_nbr"));
                            rowArray.put(res.getString("so_po"));
                            rowArray.put(res.getString("cm_code"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("so_ord_date")); 
                            rowArray.put(res.getString("so_status"));
                            rowArray.put(currformatDouble(total));
                            rowArray.put(currformatDouble(tax));
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
        
    public static String getOrderPrintData(String order) {
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
            double charges = 0.00;
            try{
                
                // lets select 'charges' first...as it will be applied to all rows returned by sod_det search
                res = st.executeQuery("select " +
                      " case when sos_amttype = 'percent' and sos_type <> 'tax' then (myamt * (sos_amt / 100.0)) " +
                      " when sos_amttype = 'percent' and sos_type = 'tax' then (myamt * (sos_amt / 100.0)) " +
                      " else sos_amt end as 'amt' " +
                      " from sos_det, (select sod_nbr, sum(sod_ord_qty * sod_listprice) as 'myamt' from sod_det group by sod_nbr) sub " +
                      " where sub.sod_nbr = sos_nbr and sos_nbr = " + "'" + order + "'");
                while (res.next()) {
                    charges = res.getDouble("amt");
                }
                
                res = st.executeQuery("select case when sum(sos_amt) is null then 0 else sum(sos_amt) end as amt from sos_det " +
                " where sos_nbr = " + "'" + order + "'" + " and sos_amttype = 'amount' " +
                " and sos_type <> 'tax' and sos_type <> 'passive' " +
                " and sos_type <> 'shipping BIL' and sos_type <> 'shipping PPD' ");
                while (res.next()) {
                    charges += res.getDouble("amt");
                }
                
                
                res = st.executeQuery("select so_nbr, sod_nbr, so_curr, sod_desc, so_shipvia, cm_terms,  " + 
               // " (select case when sum(sos_amt) is null then 0 else sum(sos_amt) end from sos_det " +
               // " where sos_nbr = " + "'" + order + "'" + " and sos_amttype = 'amount' and sos_type <> 'tax' and sos_type <> 'passive' and sos_type <> 'shipping BIL' and sos_type <> 'shipping PPD' " +
               // " ) as charges, " +
                " (select case when sum(sos_amt) is null then 0 else sum(sos_amt) end from sos_det " +
                " where sos_nbr = " + "'" + order + "'" + " and sos_amttype = 'amount' and sos_type = 'tax' ) as taxes, " +        
                " so_cust, so_rmks, sod_po, sod_item, sod_custitem, sod_ord_qty, " +
                " sod_netprice, sod_listprice, sod_taxamt, cm_code, cm_name, cm_line1, cm_line2,  " +
                " cm_city, cm_state, cm_zip, cm_country, cms_city, cms_state, cms_zip, cms_country, " +
                " site_site, site_desc, site_line1, site_city, site_state, site_zip, site_country, " +                        
                " cms_name, cms_line1, cms_line2, so_create_date, so_due_date, cm_logo, site_logo, " +
                " cm_iv_jasper, site_or_jasper, ov_image_directory, ov_jasper_directory, sod_taxamt " +
                " from sod_det  " +
                " inner join so_mstr on so_nbr = sod_nbr " +
                " inner join cm_mstr on cm_code = so_cust " +
                " left outer join cms_det on cms_code = so_cust and cms_shipto = so_ship " +
                " inner join site_mstr on site_site = so_site " +
                " inner join ov_ctrl " +         
                " where sod_nbr = " + "'" + order + "'");
                
                    
                    String shipper = "";
                    int i = 0;
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("sod_nbr")); 
                        rowArray.put(res.getString("sod_desc"));
                        rowArray.put(res.getString("so_cust"));
                        rowArray.put(res.getString("so_rmks"));
                        rowArray.put(res.getString("sod_po"));
                        rowArray.put(res.getString("sod_item"));
                        rowArray.put(res.getString("sod_custitem"));
                        rowArray.put(res.getDouble("sod_ord_qty"));  
                        rowArray.put(res.getDouble("sod_netprice"));  
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_name")); // 10 zero base
                        rowArray.put(res.getString("cm_line1"));
                        rowArray.put(res.getString("cm_line2"));
                        rowArray.put(res.getString("cms_name"));
                        rowArray.put(res.getString("cms_line1"));
                        rowArray.put(res.getString("site_desc"));
                        rowArray.put(res.getString("site_line1"));
                        rowArray.put(res.getString("so_shipvia"));
                        rowArray.put(res.getString("cm_terms")); 
                        rowArray.put(res.getString("so_create_date"));
                        rowArray.put(res.getString("so_due_date")); // 20 zero base
                        rowArray.put(res.getString("cm_city"));
                        rowArray.put(res.getString("cm_state"));
                        rowArray.put(res.getString("cm_zip"));
                        rowArray.put(res.getString("cm_country"));
                        rowArray.put(res.getString("cms_city"));  
                        rowArray.put(res.getString("cms_state"));
                        rowArray.put(res.getString("cms_zip"));
                        rowArray.put(res.getString("cms_country"));
                        rowArray.put(res.getString("site_city"));
                        rowArray.put(res.getString("site_state"));  // 30 zero base
                        rowArray.put(res.getString("site_zip"));
                        rowArray.put(res.getString("site_country"));
                        rowArray.put(res.getString("site_site"));
                        rowArray.put(res.getString("cm_logo"));
                        rowArray.put(res.getString("site_logo")); 
                        rowArray.put(res.getString("ov_image_directory"));
                        rowArray.put(res.getString("cm_iv_jasper"));
                        rowArray.put(res.getString("site_or_jasper"));
                        rowArray.put(res.getString("ov_jasper_directory"));
                        rowArray.put(res.getString("so_nbr")); // 40 zero base
                        rowArray.put(res.getString("so_curr")); 
                        rowArray.put(bsNumber(charges)); // rowArray.put(res.getDouble("charges"));
                        rowArray.put(res.getDouble("taxes"));
                        rowArray.put(res.getDouble("sod_listprice"));
                        rowArray.put(res.getString("cms_line2"));
                        rowArray.put(res.getDouble("sod_taxamt"));
                        jsonarray.put(rowArray);
                        i++;
                    }
                
              // get SAC
              if (i > 0) {
              res = st.executeQuery("select sos_desc, " +
                      " case when sos_amttype = 'percent' and sos_type <> 'tax' then (myamt * (sos_amt / 100.0)) " +
                      " when sos_amttype = 'percent' and sos_type = 'tax' then (myamt * (sos_amt / 100.0)) " +
                      " else sos_amt end as 'amt' " +
                      " from sos_det, (select sod_nbr, sum(sod_ord_qty * sod_listprice) as 'myamt' from sod_det group by sod_nbr) sub " +
                      " where sub.sod_nbr = sos_nbr and sos_nbr = " + "'" + order + "'");
              while (res.next()) {
                  JSONArray rowArray = new JSONArray(); 
                        rowArray.put("sacarray");
                        rowArray.put(res.getString("sos_desc")); 
                        rowArray.put(res.getString("amt"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static String getPickPrintData(String order) {
        JSONArray jsonarray = new JSONArray();
        
        // get generic pick jasper from sysmeta
        String jasperfile = getSysMetaValue("system", "jasper", "generic");
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
                
                res = st.executeQuery("select so_nbr, sod_nbr, so_curr, sod_desc, so_shipvia, cm_terms,  " + 
                " (select case when sum(sos_amt) is null then 0 else sum(sos_amt) end from sos_det " +
                " where sos_nbr = " + "'" + order + "'" + " and sos_amttype = 'amount' and sos_type <> 'tax' and sos_type <> 'passive' and sos_type <> 'shipping BIL' and sos_type <> 'shipping PPD' " +
                " ) as charges, " +
                " (select case when sum(sos_amt) is null then 0 else sum(sos_amt) end from sos_det " +
                " where sos_nbr = " + "'" + order + "'" + " and sos_amttype = 'amount' and sos_type = 'tax' ) as taxes, " +        
                " so_cust, so_rmks, sod_po, sod_item, sod_custitem, sod_ord_qty, " +
                " sod_netprice, sod_listprice, sod_taxamt, cm_code, cm_name, cm_line1, cm_line2,  " +
                " cm_city, cm_state, cm_zip, cm_country, cms_city, cms_state, cms_zip, cms_country, " +
                " site_site, site_desc, site_line1, site_city, site_state, site_zip, site_country, " +                        
                " cms_name, cms_line1, cms_line2, so_create_date, so_due_date, cm_logo, site_logo, " +
                " cm_iv_jasper, site_or_jasper, ov_image_directory, ov_jasper_directory, sod_taxamt, sod_uom, so_slsperson1 " +
                " from sod_det  " +
                " inner join so_mstr on so_nbr = sod_nbr " +
                " inner join cm_mstr on cm_code = so_cust " +
                " left outer join cms_det on cms_code = so_cust and cms_shipto = so_ship " +
                " inner join site_mstr on site_site = so_site " +
                " inner join ov_ctrl " +         
                " where sod_nbr = " + "'" + order + "'");
                
                    
                    String shipper = "";
                    int i = 0;
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("sod_nbr")); 
                        rowArray.put(res.getString("sod_desc"));
                        rowArray.put(res.getString("so_cust"));
                        rowArray.put(res.getString("so_rmks"));
                        rowArray.put(res.getString("sod_po"));
                        rowArray.put(res.getString("sod_item"));
                        rowArray.put(res.getString("sod_custitem"));
                        rowArray.put(res.getDouble("sod_ord_qty"));
                        rowArray.put(res.getDouble("sod_netprice")); 
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_name")); // 10 zero base
                        rowArray.put(res.getString("cm_line1"));
                        rowArray.put(res.getString("cm_line2"));
                        rowArray.put(res.getString("cms_name"));
                        rowArray.put(res.getString("cms_line1"));
                        rowArray.put(res.getString("site_desc"));
                        rowArray.put(res.getString("site_line1"));
                        rowArray.put(res.getString("so_shipvia"));
                        rowArray.put(res.getString("cm_terms")); 
                        rowArray.put(res.getString("so_create_date"));
                        rowArray.put(res.getString("so_due_date")); // 20 zero base
                        rowArray.put(res.getString("cm_city"));
                        rowArray.put(res.getString("cm_state"));
                        rowArray.put(res.getString("cm_zip"));
                        rowArray.put(res.getString("cm_country"));
                        rowArray.put(res.getString("cms_city"));  
                        rowArray.put(res.getString("cms_state"));
                        rowArray.put(res.getString("cms_zip"));
                        rowArray.put(res.getString("cms_country"));
                        rowArray.put(res.getString("site_city"));
                        rowArray.put(res.getString("site_state"));  // 30 zero base
                        rowArray.put(res.getString("site_zip"));
                        rowArray.put(res.getString("site_country"));
                        rowArray.put(res.getString("site_site"));
                        rowArray.put(res.getString("cm_logo"));
                        rowArray.put(res.getString("site_logo")); 
                        rowArray.put(res.getString("ov_image_directory"));
                        rowArray.put(res.getString("cm_iv_jasper"));
                        rowArray.put(jasperfile); // from sysmeta
                        rowArray.put(res.getString("ov_jasper_directory"));
                        rowArray.put(res.getString("so_nbr")); // 40 zero base
                        rowArray.put(res.getString("so_curr")); 
                        rowArray.put(res.getDouble("charges"));
                        rowArray.put(res.getDouble("taxes"));
                        rowArray.put(res.getDouble("sod_listprice"));
                        rowArray.put(res.getString("cms_line2"));
                        rowArray.put(res.getDouble("sod_taxamt"));
                        rowArray.put(res.getString("sod_uom"));
                        rowArray.put(res.getString("so_slsperson1"));
                        jsonarray.put(rowArray);
                        i++;
                    }
                
              // get SAC
              if (i > 0) {
              res = st.executeQuery("select sos_desc, " +
                      " case when sos_amttype = 'percent' and sos_type <> 'tax' then (myamt * -1 * (sos_amt / 100.0)) " +
                      " when sos_amttype = 'percent' and sos_type = 'tax' then (myamt * (sos_amt / 100.0)) " +
                      " else sos_amt end as 'amt' " +
                      " from sos_det, (select sod_nbr, sum(sod_ord_qty * sod_listprice) as 'myamt' from sod_det group by sod_nbr) sub " +
                      " where sub.sod_nbr = sos_nbr and sos_nbr = " + "'" + order + "'");
              while (res.next()) {
                  JSONArray rowArray = new JSONArray(); 
                        rowArray.put("sacarray");
                        rowArray.put(res.getString("sos_desc")); 
                        rowArray.put(res.getString("amt"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    
    public static String getServiceOrderPrintData(String order) {
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
                
                res = st.executeQuery("select sv_nbr, sv_po, svd_nbr, sv_curr, svd_desc, cm_terms,  " + 
                " (select case when sum(sos_amt) is null then 0 else sum(sos_amt) end from sos_det " +
                " where sos_nbr = " + "'" + order + "'" + " and sos_amttype = 'amount' and sos_type <> 'tax' and sos_type <> 'passive' and sos_type <> 'shipping BIL' and sos_type <> 'shipping PPD' " +
                " ) as charges, " + 
                " sv_cust, sv_rmks, svd_po, svd_item, svd_qty, " +
                " svd_netprice, svd_listprice, svd_taxamt, cm_code, cm_name, cm_line1, cm_line2,  " +
                " cm_city, cm_state, cm_zip, cm_country, cms_city, cms_state, cms_zip, cms_country, " +
                " site_site, site_desc, site_line1, site_city, site_state, site_zip, site_country, " +                        
                " cms_name, cms_line1, cms_line2, sv_create_date, sv_due_date, cm_logo, site_logo, " +
                " cm_iv_jasper, site_or_jasper, ov_image_directory, ov_jasper_directory, cm_phone, cm_email " +
                " from svd_det  " +
                " inner join sv_mstr on sv_nbr = svd_nbr " +
                " inner join cm_mstr on cm_code = sv_cust " +
                " left outer join cms_det on cms_code = sv_cust and cms_shipto = sv_ship " +
                " inner join site_mstr on site_site = sv_site " +
                " inner join ov_ctrl " +         
                " where svd_nbr = " + "'" + order + "'");
                
                    
                    String shipper = "";
                    int i = 0;
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("svd_nbr")); 
                        rowArray.put(res.getString("svd_desc"));
                        rowArray.put(res.getString("sv_cust"));
                        rowArray.put(res.getString("sv_rmks"));
                        rowArray.put(res.getString("svd_po"));
                        rowArray.put(res.getString("svd_item"));
                        rowArray.put(res.getDouble("svd_qty"));
                        rowArray.put(res.getDouble("svd_netprice")); 
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_name")); 
                        rowArray.put(res.getString("cm_line1"));  // 10 zero base
                        rowArray.put(res.getString("cm_line2"));
                        rowArray.put(res.getString("cms_name"));
                        rowArray.put(res.getString("cms_line1"));
                        rowArray.put(res.getString("site_desc"));
                        rowArray.put(res.getString("site_line1"));
                        rowArray.put(res.getString("cm_terms")); 
                        rowArray.put(res.getString("sv_create_date"));
                        rowArray.put(res.getString("sv_due_date")); 
                        rowArray.put(res.getString("cm_city")); 
                        rowArray.put(res.getString("cm_state")); // 20 zero base
                        rowArray.put(res.getString("cm_zip"));
                        rowArray.put(res.getString("cm_country"));
                        rowArray.put(res.getString("cms_city"));  
                        rowArray.put(res.getString("cms_state"));
                        rowArray.put(res.getString("cms_zip"));
                        rowArray.put(res.getString("cms_country"));
                        rowArray.put(res.getString("site_city"));
                        rowArray.put(res.getString("site_state"));  
                        rowArray.put(res.getString("site_zip"));  
                        rowArray.put(res.getString("site_country")); // 30 zero base
                        rowArray.put(res.getString("site_site"));
                        rowArray.put(res.getString("cm_logo"));
                        rowArray.put(res.getString("site_logo")); 
                        rowArray.put(res.getString("ov_image_directory"));
                        rowArray.put(res.getString("cm_iv_jasper"));
                        rowArray.put(res.getString("site_or_jasper"));
                        rowArray.put(res.getString("ov_jasper_directory"));
                        rowArray.put(res.getString("sv_nbr")); 
                        rowArray.put(res.getString("sv_curr")); 
                        rowArray.put(res.getDouble("charges")); // 40 zero base
                        rowArray.put(res.getDouble("svd_listprice"));
                        rowArray.put(res.getString("cms_line2"));
                        rowArray.put(res.getString("sv_po"));
                        rowArray.put(res.getString("cm_phone"));
                        rowArray.put(res.getString("cm_email"));
                        jsonarray.put(rowArray);
                        i++;
                    }
                
              // get SAC
              if (i > 0) {
              res = st.executeQuery("select sos_desc, " +
                      " case when sos_amttype = 'percent' and sos_type <> 'tax' then (myamt * -1 * (sos_amt / 100.0)) " +
                      " when sos_amttype = 'percent' and sos_type = 'tax' then (myamt * (sos_amt / 100.0)) " +
                      " else sos_amt end as 'amt' " +
                      " from sos_det, (select sod_nbr, sum(sod_ord_qty * sod_listprice) as 'myamt' from sod_det group by sod_nbr) sub " +
                      " where sub.sod_nbr = sos_nbr and sos_nbr = " + "'" + order + "'");
              while (res.next()) {
                  JSONArray rowArray = new JSONArray(); 
                        rowArray.put("sacarray");
                        rowArray.put(res.getString("sos_desc")); 
                        rowArray.put(res.getString("amt"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    public static boolean addUpdateSOMeta(String id, String type, String key, String value) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "addUpdateSOMeta"});
            list.add(new String[]{"param1", id});
            list.add(new String[]{"param2", type});
            list.add(new String[]{"param3", key});
            list.add(new String[]{"param4", value});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServORD"));
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
                res = st.executeQuery("SELECT som_value FROM so_meta where som_id = " + "'" + id + "'"
                        + " AND som_type = " + "'" + type + "'"
                        + " AND som_key = " + "'" + key + "'"     
                        + " ;");
                while (res.next()) {
                    i++;
                }

                if (i == 0) {
                    st.executeUpdate("insert into so_meta (som_id, som_type, som_key, som_value) values ( "
                            + "'" + id + "'" + ","
                            + "'" + type + "'" + ","
                            + "'" + key + "'" + ","
                            + "'" + value + "'" + ")"
                            + ";");
                    x = true;
                } else {
                    st.executeUpdate("update so_meta set "
                            + " som_value = " + "'" + value + "'"
                            + " where som_id = " + "'" + id + "'" + " and "
                            + " som_type = " +  "'" + type + "'" + " and "
                            + " som_key = " +  "'" + key + "'"  
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

    public static boolean deleteSOMeta(String id, String type, String key, String value) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "deleteSOMeta"});
            list.add(new String[]{"param1", id});
            list.add(new String[]{"param2", type});
            list.add(new String[]{"param3", key});
            list.add(new String[]{"param4", value});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServORD"));
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
                st.executeUpdate("delete from so_meta "
                            + " where som_id = " + "'" + id + "'" + " and "
                            + " som_type = " +  "'" + type + "'" + " and "
                            + " som_key = " +  "'" + key + "'" + 
                            ";");    
                } else {
                st.executeUpdate("delete from so_meta "
                            + " where som_id = " + "'" + id + "'" + " and "
                            + " som_type = " +  "'" + type + "'" + " and "
                            + " som_key = " +  "'" + key + "'" + " and "        
                            + " som_value = " +  "'" + value + "'"  
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

    public static String[] orderToInvoice(String order, String userid, String trackingnbr) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "orderToInvoice"});
            list.add(new String[]{"param1", order});
            list.add(new String[]{"param2", userid});
            list.add(new String[]{"param3", trackingnbr});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        } 
        
        
        String[] m;
        int shipperid = OVData.getNextNbr("shipper");
        salesOrder orderSet = getOrderMstrSet(new String[]{order});
        shpData.ship_mstr sh = new shpData.ship_mstr(null, 
                bsNumber(shipperid),
                orderSet.so().so_cust(),
                orderSet.so().so_ship(),
                0, // pallets
                0, // boxes
                orderSet.so().so_shipvia(), // shipvia  
                setDateDB(new java.util.Date()),
                orderSet.so().so_ord_date(), // po date
                orderSet.so().so_type(),
                orderSet.so().so_po(), // po number
                orderSet.so().so_rmks(),
                userid,
                orderSet.so().so_site(),
                orderSet.so().so_curr(),
                "", // wh
                orderSet.cm().cm_terms(), // terms
                orderSet.so().so_taxcode(), // taxcode
                orderSet.cm().cm_ar_acct(), // aracct
                orderSet.cm().cm_ar_cc(), // arcc
                "S", // type
                orderSet.so().so_nbr(), // sh_so 
                orderSet.so().so_site(),
                trackingnbr, // tracking number
                "", // status
                "", // sh_char1
                "1", // sh_char2 ...shipper complete 
                "" // sh_char3
                );
        
        
        ArrayList<shpData.ship_det> shd = new ArrayList<shpData.ship_det>();
        int i = 1;
        for (sod_det sod : orderSet.sod()) {
            shpData.ship_det z = new shpData.ship_det(null, 
                bsNumber(shipperid), // shipper
                i, //shline
                sod.sod_item(), // item
                sod.sod_custitem(), // custimtem
                sod.sod_nbr(),  // order
                sod.sod_line(), //soline    
                setDateDB(new java.util.Date()),
                sod.sod_po(), // po
                sod.sod_ord_qty(), // qty
                sod.sod_uom(), //uom
                orderSet.so().so_curr(), //currency 
                sod.sod_netprice(), // net price
                sod.sod_disc(), // disc
                sod.sod_listprice(), // list price
                sod.sod_desc(), // desc
                sod.sod_wh(), // wh
                sod.sod_loc(), // loc
                0, // taxamt
                "0", // cont
                "", // ref
                trackingnbr, // serial   
                sod.sod_site(),
                sod.sod_bom(), // bom
                sod.sod_ord_qty(),  // packqty
                "" // kvpair    
                );
        shd.add(z);
        i++;
        }
        
        ArrayList<shpData.ship_tree> sht = new ArrayList<>();
        shpData.ship_tree x = new shpData.ship_tree(null,
            bsNumber(shipperid),
            "", // label serial number ... no labels
            orderSet.so().so_site(),
            "f", // flat ...no labels
            bsNumber(shipperid),
            "",
            "",
            "",
            "",
            "", // empty item
            1.0,
            "" // get display serial
            );
            sht.add(x);
            for (sod_det sod : orderSet.sod()) {
                shpData.ship_tree y = new shpData.ship_tree(null,
                bsNumber(shipperid),
                sod.sod_nbr() + "," + sod.sod_item() + "," + sod.sod_line(),
                sod.sod_site(),
                "i",
                bsNumber(shipperid),
                bsNumber(sod.sod_line()),
                sod.sod_nbr(),
                bsNumber(sod.sod_line()),
                sod.sod_po(),
                sod.sod_item(),
                sod.sod_ord_qty(),
                "" // get display serial
                );
                sht.add(y);
            }
        
        addShipperTransaction(shd, sh, sht);
        
        updateShipperSAC(bsNumber(shipperid));
        
        m = confirmShipperTransaction("order",bsNumber(shipperid),new java.util.Date());
        
        if (OVData.isAutoPost()) {
            fglData.PostGL();
        }
        
        return m;
    }
    
    public static void addUpdateSOMetaNotes(String id, String notetype, String[] values) {  //used primarily for order notes where key is counter
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            String valuesString = String.join("=_=", values);
            list.add(new String[]{"id", "addUpdateSOMetaNotes"});
            list.add(new String[]{"param1", id});
            list.add(new String[]{"param2", notetype});
            list.add(new String[]{"param3", valuesString});
            try {
               sendServerPost(list, "", null, "dataServORD");
               return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        } 
        
        if (values != null) {
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
                
                
                // delete old order notes if available
                st.executeUpdate("delete from so_meta "
                            + " where som_id = " + "'" + id + "'" + " and "
                            + " som_type = " + "'" + notetype + "'"  
                            + ";");
              

                //now add
                for (String s : values) {
                    if (s.isBlank()) {
                        continue;
                    }
                    i++;
                    if (s.length() > 199) {
                        s = s.substring(0,199);
                    }
                st.executeUpdate("insert into so_meta (som_id, som_type, som_key, som_value) values ( "
                        + "'" + id + "'" + ","
                        + "'" + notetype + "'" + ","
                        + "'" + String.valueOf(i) + "'" + ","
                        + "'" + s + "'" + ")"
                        + ";");
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
        
    }
    }

    public static ArrayList<String> getSOMetaNotes(String id, String notetype) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSOMetaNotes"});
            list.add(new String[]{"param1", id});
            list.add(new String[]{"param2", notetype});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList<String> r = new ArrayList<String>();
        
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
                                
                res = st.executeQuery("select * from so_meta " +
                        " where som_id = " + "'" + id + "'" +
                        " and som_type = " + "'" + notetype + "'" +
                        " order by som_key;");
                while (res.next()) {
                    r.add(res.getString("som_value"));
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
    
    public static String getSOMetaValue(String id, String type, String key) {
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

                res = st.executeQuery("select som_value from so_meta where " +
                        " som_id = " + "'" + id + "'" + " AND " +
                        " som_type = " + "'" + type + "'" + " AND " +
                        " som_key = " + "'" + key + "'" +
                        " order by som_value;" );
               while (res.next()) {
                x = res.getString("som_value");                    
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
    
    public static ArrayList<String[]> getSOMetaData(String id) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSOMetaData"});
            list.add(new String[]{"param1",  id});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        ArrayList<String[]> x = new ArrayList<String[]>();
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

                res = st.executeQuery("select * from so_meta where " +
                        " som_id = " + "'" + id + "'" + 
                        " order by som_key;" );
               while (res.next()) {
                x.add(new String[]{res.getString("som_id"),res.getString("som_type"),res.getString("som_key"),res.getString("som_value")});                    
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
    
    
    private static ArrayList<String[]> _getSOMeta(String sonbr, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<String[]> list = new ArrayList<String[]>();
        String sqlSelect = "select * from so_meta where som_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setInt(1, bsParseInt(sonbr));
          res = ps.executeQuery();
            while(res.next()) {
                list.add(new String[]{res.getString("som_id"), res.getString("som_type"), res.getString("som_key"), res.getString("som_value")});
            }
        return list;
    }
    
    
    public static String[] getBillTranByDate(String bill, LocalDate billdate) {
        String[] r = null;
        
        if (billdate == null) {
                    return r;
        }
        
        String strbilldate = billdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
                                
                res = st.executeQuery("select billt_nbr, billt_invoice, billt_startdate, billt_enddate " +
                        " from bill_tran where billt_nbr = " + "'" + bill + "'" +
                        " and billt_invdate >= " + "'" + strbilldate + "'" + 
                        " and billt_status <> 'void' " + ";");
                while (res.next()) {
                    r = new String[]{res.getString("billt_nbr"),
                    res.getString("billt_invoice"),
                    res.getString("billt_startdate"),
                    res.getString("billt_enddate")};
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
    
    public static String[] getBillTranByInvoice(String bill, String invoice) {
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
            try{
                res = st.executeQuery("select billt_nbr, billt_invoice, billt_startdate, billt_enddate " +
                        " from bill_tran where billt_nbr = " + "'" + bill + "'" +
                        " and billt_invoice = " + "'" + invoice + "'" + 
                        " and billt_status <> 'void' " + ";");
                while (res.next()) {
                    r = new String[]{res.getString("billt_nbr"),
                    res.getString("billt_invoice"),
                    res.getString("billt_startdate"),
                    res.getString("billt_enddate")};
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
    
    public static String[] getBillTranLast(String bill) {
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
            try{
                res = st.executeQuery("select billt_nbr, billt_invoice, billt_startdate, billt_enddate " +
                        " from bill_tran where billt_nbr = " + "'" + bill + "'" +
                        " and billt_status <> 'void' " + 
                        " order by billt_id desc limit 1 "+ ";");
                while (res.next()) {
                    r = new String[]{res.getString("billt_nbr"),
                    res.getString("billt_invoice"),
                    res.getString("billt_startdate"),
                    res.getString("billt_enddate")};
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
    
    private static int _updateBillNextDate(String nbr, String nbd, String lbd, Connection con) throws SQLException {
        int rows = 0;
        String sql = "update bill_mstr set bill_nextbilldate = ?, bill_lastbilldate = ? " +
                 " where bill_nbr = ? ; ";
	PreparedStatement ps = con.prepareStatement(sql) ;
            ps.setString(3, nbr);
            ps.setString(1, nbd);
            ps.setString(2, lbd);
            rows = ps.executeUpdate();
            ps.close();
        return rows;
    }
    
    public static String findNextBillDate(LocalDate servicedate, String billingtype, String frequencytype) {
        String r = "";
        LocalDate now = LocalDate.now();
        
        
        // determine target date
        LocalDate targetdate = now;
        if (billingtype.equals("fom")) {
            if (frequencytype.equals("monthly")) {
              targetdate = now.plusMonths(1).withDayOfMonth(1);
            } else if (frequencytype.equals("yearly")) {
              targetdate = now.plusYears(1).withDayOfMonth(1); 
            } else if (frequencytype.equals("weekly")) {
              targetdate = now.plusWeeks(1).withDayOfMonth(1);   
            } else {
              targetdate = now.plusMonths(1).withDayOfMonth(1);  
            }
        }
        if (billingtype.equals("mom")) { 
            if (frequencytype.equals("monthly")) {
              targetdate = now.plusMonths(1).withDayOfMonth(15);
            } else if (frequencytype.equals("yearly")) {
              targetdate = now.plusYears(1).withDayOfMonth(15); 
            } else if (frequencytype.equals("weekly")) {
              targetdate = now.plusWeeks(1).withDayOfMonth(15);   
            } else {
              targetdate = now.plusMonths(1).withDayOfMonth(15);  
            }
        }
        if (billingtype.equals("lom")) { 
            if (frequencytype.equals("monthly")) {
              targetdate = now.withDayOfMonth(now.lengthOfMonth());
            } else if (frequencytype.equals("yearly")) {
              targetdate = now.plusYears(1);
              targetdate = targetdate.withDayOfMonth(targetdate.lengthOfMonth());
            } else if (frequencytype.equals("weekly")) {
              targetdate = now.plusWeeks(1);
              targetdate = targetdate.withDayOfMonth(targetdate.lengthOfMonth());  
            } else {
              targetdate = now.withDayOfMonth(now.lengthOfMonth()); 
            }
        }
        
        r = targetdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        
        
        return r;
    }
    
    
    public static ArrayList<String[]> getSalesOrderInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSalesOrderInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServORD"));
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
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
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
            while (res.next()) {
               if (res.getString("perm_readonly").equals("0")) {
                String[] s = new String[2];
                s[0] = "canupdate";
                s[1] = "true";
                lines.add(s);
               } else {
                String[] s = new String[2];
                s[0] = "canupdate";
                s[1] = "false";
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
            
            
            res = st.executeQuery("select wh_id from wh_mstr order by wh_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "warehouses";
               s[1] = res.getString("wh_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select loc_loc from loc_mstr order by loc_loc;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "locations";
               s[1] = res.getString("loc_loc");
               lines.add(s);
            }
            
            res = st.executeQuery("select cur_id from cur_mstr ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "currencies";
               s[1] = res.getString("cur_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select uom_id from uom_mstr order by uom_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "uoms";
               s[1] = res.getString("uom_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "customers";
               s[1] = res.getString("cm_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select tax_code from tax_mstr order by tax_code  ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "taxcodes";
               s[1] = res.getString("tax_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "states";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'country' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "countries";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select slsp_name from slsp_mstr where slsp_active = '1' order by slsp_name ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "salesreps";
               s[1] = res.getString("slsp_name");
               lines.add(s);
            }
            
            res = st.executeQuery("select car_id from car_mstr order by car_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "carriers";
               s[1] = res.getString("car_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'orderstatus' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "statuses";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select it_item from item_mstr where it_site = " + "'" + defaultsite + "'" + " order by it_item ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "items";
               s[1] = res.getString("it_item");
               lines.add(s);
            }
            
            
            
            res = st.executeQuery("select orc_custitem, orc_autoallocate, orc_autoinvoice, orc_varchar from order_ctrl;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "allocate";
               s[1] = res.getString("orc_autoallocate");
               lines.add(s);
               s = new String[2];
               s[0] = "custitemonly";
               s[1] = res.getString("orc_custitem");
               lines.add(s);
               s = new String[2];
               s[0] = "autoinvoice";
               s[1] = res.getString("orc_autoinvoice");
               lines.add(s);
               s = new String[2];
               s[0] = "orcvarchar";
               s[1] = res.getString("orc_varchar");
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
    
    public static ArrayList<String[]> getServiceOrderInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getServiceOrderInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServORD"));
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
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
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
            while (res.next()) {
               if (res.getString("perm_readonly").equals("0")) {
                String[] s = new String[2];
                s[0] = "canupdate";
                s[1] = "true";
                lines.add(s);
               } else {
                String[] s = new String[2];
                s[0] = "canupdate";
                s[1] = "false";
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
           
            res = st.executeQuery("select orc_srvm_type, orc_srvm_item_default from order_ctrl;");
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "orc_srvm_type";
               s[1] = res.getString("orc_srvm_type");
               lines.add(s);
               s = new String[2];
               s[0] = "orc_srvm_item_default";
               s[1] = res.getString("orc_srvm_item_default");
               lines.add(s);
            }
            
            res = st.executeQuery("select cur_id from cur_mstr ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "currencies";
               s[1] = res.getString("cur_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select uom_id from uom_mstr order by uom_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "uoms";
               s[1] = res.getString("uom_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "customers";
               s[1] = res.getString("cm_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select tax_code from tax_mstr order by tax_code  ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "taxcodes";
               s[1] = res.getString("tax_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select distinct wf_id from wf_mstr;" );
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "routings";
               s[1] = res.getString("wf_id");
               lines.add(s);
            }
            
           
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'orderstatus' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "statuses";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select it_item from item_mstr where it_site = " + "'" + defaultsite + "'" + " order by it_item ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "items";
               s[1] = res.getString("it_item");
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
    
    public static ArrayList<String[]> getBillingInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBillingInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServORD"));
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
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
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
            while (res.next()) {
               if (res.getString("perm_readonly").equals("0")) {
                String[] s = new String[2];
                s[0] = "canupdate";
                s[1] = "true";
                lines.add(s);
               } else {
                String[] s = new String[2];
                s[0] = "canupdate";
                s[1] = "false";
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
            
            res = st.executeQuery("select uom_id from uom_mstr order by uom_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "uoms";
               s[1] = res.getString("uom_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "customers";
               s[1] = res.getString("cm_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select cut_code from cust_term order by cut_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "terms";
               s[1] = res.getString("cut_code");
               lines.add(s);
            }
            
           
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'billingtype' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "billingtype";
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
    
    public static ArrayList<String[]> getQuoteInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getQuoteInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServORD"));
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses   
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
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
            while (res.next()) {
               if (res.getString("perm_readonly").equals("0")) {
                String[] s = new String[2];
                s[0] = "canupdate";
                s[1] = "true";
                lines.add(s);
               } else {
                String[] s = new String[2];
                s[0] = "canupdate";
                s[1] = "false";
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
            
            res = st.executeQuery("select uom_id from uom_mstr order by uom_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "uoms";
               s[1] = res.getString("uom_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "customers";
               s[1] = res.getString("cm_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select cut_code from cust_term order by cut_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "terms";
               s[1] = res.getString("cut_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = " + "'PRICEGROUP'" + " order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "pricegroups";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select tax_code from tax_mstr order by tax_code  ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "taxcodes";
               s[1] = res.getString("tax_code");
               lines.add(s);
            }
            
            java.util.Date now = new java.util.Date();
                res = st.executeQuery("select cpr_item from cpr_mstr " +
                      " where cpr_type = 'DISCOUNT' " + 
                      " AND (cpr_expire = null OR cpr_expire >= " + "'" + BlueSeerUtils.setDateFormat(now) + "'" + ") " +
                      ";");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "discs";
               s[1] = res.getString("cpr_item");
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
    

    public static ArrayList<String[]> getOrderBrowseInit(String panelClassName, String userid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getOrderBrowseInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServORD"));
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses    
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
            
            
            res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "customers";
               s[1] = res.getString("cm_code");
               lines.add(s);
            }
          
            res = st.executeQuery("select sysm_key, sysm_value from sys_meta where " +
                        " sysm_id = " + "'system'" + " AND " +
                        " sysm_type = " + "'ordercontrol'" + 
                        " order by sysm_value;" );
               while (res.next()) {
                String[] s = new String[2];
                s[0] = "system";
                s[1] = res.getString("sysm_key") + "," + res.getString("sysm_value");     
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
    
    public static String[] validateOrderDetail(String key, String cust, String item, String qty, String site, String uom, String curr) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "validateOrderDetail"});
            list.add(new String[]{"param1", key});
            list.add(new String[]{"param2", cust});
            list.add(new String[]{"param3", item});
            list.add(new String[]{"param4", qty});
            list.add(new String[]{"param5", site});
            list.add(new String[]{"param6", uom});
            list.add(new String[]{"param7", curr});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        // check unallocated qty
        if (! OVData.isOrderExceedQOHU() && bsParseDouble(qty) > invData.getItemQOHUnallocated(item,site,key)) {
             return new String[]{"0", "1092"};
        }
        if (OVData.isValidItem(item) && ! OVData.isValidUOMConversion(item, site, uom)) {
                return new String[]{"0", "1093"};
        }
        if (OVData.isValidItem(item)
                && OVData.getSysMetaValue("system", "ordercontrol", "uom_pricing").equals("1")
                && ! OVData.isBaseUOMOfItem(item, site, uom) 
                && ! OVData.isValidCustPriceRecordExists(cust,item,uom,curr)) {
                return new String[]{"0", "1094"};
        }
        return new String[]{"1", "0"}; // true
    }
    
    public static ArrayList<String[]> getServiceOrderChartData(String fromdate, String todate) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getServiceOrderChartData"});
            list.add(new String[]{"param1", fromdate});
            list.add(new String[]{"param2", todate});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServORD"));
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
        // allocate, custitemonly, site, currency, sites, currencies, uoms, 
        // states, warehouses, locations, customers, taxcodes, carriers, statuses    
           
            res = st.executeQuery("select sv_cust, cm_name, sv_type, sum(svd_netprice * svd_qty) as 'sum' from svd_det " +
                        " inner join sv_mstr on sv_nbr = svd_nbr  " +
                        " inner join cm_mstr on cm_code = sv_cust  " +
                        " where sv_create_date >= " + "'" + fromdate + "'" +
                        " AND sv_create_date <= " + "'" + todate + "'" +
                        " AND sv_status <> 'void' " +
                        " group by sv_cust, cm_name, sv_type order by sv_cust desc   ;");

               while (res.next()) {
                String[] s = new String[3];
                s[0] = res.getString("cm_name");
                s[1] = res.getString("sum");  
                s[2] = res.getString("sv_type"); 
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
    
    
    public static edi855 init_edi855_object(String order) {
        edi855 e = null;
        ArrayList<sod_det> lines = new ArrayList<sod_det>();
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

            // get shipper lines first ...to be included in edi856 object record
               res = st.executeQuery("select * from sod_det where sod_nbr = " + "'" + order + "'" +";");
       
                while (res.next()) {
                    sod_det sod = new sod_det(null, res.getString("sod_nbr"), res.getInt("sod_line"), res.getString("sod_item"),
                    res.getString("sod_custitem"), res.getString("sod_po"), res.getDouble("sod_ord_qty"), res.getString("sod_uom"), res.getDouble("sod_all_qty"),
                    res.getDouble("sod_listprice"), res.getDouble("sod_disc"), res.getDouble("sod_netprice"), res.getString("sod_ord_date"), res.getString("sod_due_date"),
                    res.getDouble("sod_shipped_qty"), res.getString("sod_status"), res.getString("sod_wh"), res.getString("sod_loc"), 
                    res.getString("sod_desc"), res.getDouble("sod_taxamt"), res.getString("sod_site"), res.getString("sod_bom"), res.getString("sod_ship"),
                    res.getString("sod_char1"),res.getString("sod_char2"),res.getString("sod_char3"), 
                    res.getString("sod_custline"), res.getString("sod_custuom"), res.getString("sod_custprice"));
                    lines.add(sod);
                }

                res = st.executeQuery("SELECT * from so_mstr " +
                   " where so_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          e = new edi855(res.getString("so_nbr"),
                          res.getString("so_po"),
                          res.getString("so_cust"),
                          res.getString("so_ship"),
                          res.getString("so_site"),
                          res.getString("so_type"),
                          res.getString("so_ord_date"),
                          res.getString("so_due_date"),
                          res.getString("so_shipvia"),
                          res.getString("so_rmks"),
                          res.getString("so_curr"),
                          res.getString("so_status"), lines);
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
    catch (Exception ex){
        MainFrame.bslog(ex);

    }
        
        return e;
    }
    
    
    public static String[] getSOMstrHeaderEDI(String order) {
        String[] x = new String[12];
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
            
         // so, po, cust, ship, site, type, orddate, duedate, shipvia, rmks, cur, status
           res = st.executeQuery("SELECT * from so_mstr " +
                   " where so_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          x[0] = res.getString("so_nbr");
                          x[1] = res.getString("so_po");
                          x[2] = res.getString("so_cust");
                          x[3] = res.getString("so_ship");
                          x[4] = res.getString("so_site");
                          x[5] = res.getString("so_type");
                          x[6] = res.getString("so_ord_date");
                          x[7] = res.getString("so_due_date");
                          x[8] = res.getString("so_shipvia");
                          x[9] = res.getString("so_rmks");
                          x[10] = res.getString("so_curr");
                          x[11] = res.getString("so_status");
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
    
    public static ArrayList<String[]> getSOMstrdetailsEDI(String order) {
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
            
           // line, item, custitem, qty, price, uom, desc, custline, custuom, custprice
           res = st.executeQuery("SELECT * from sod_det " +
                   " where sod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          String[] s = new String[10];
                          for (int z = 0; z < 10; z++) {
                          s[z] = "";
                          }
                          s[0] = res.getString("sod_line");
                          s[1] = res.getString("sod_item");
                          s[2] = res.getString("sod_custitem");
                          s[3] = res.getString("sod_ord_qty");
                          s[4] = res.getString("sod_netprice");
                          s[5] = res.getString("sod_uom");
                          s[6] = res.getString("sod_desc");
                          s[7] = res.getString("sod_custline");
                          s[8] = res.getString("sod_custuom");
                          s[9] = res.getString("sod_custprice");
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
    
    public static String getSOOrderBillto(String order) {
         String billto = "";
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
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);



                  res = st.executeQuery("select so_cust from so_mstr where so_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    billto = res.getString("so_cust");
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
         return billto;
     }

    public static String getSOOrderBilltoByPO(String po) {
         String billto = "";
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
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);



                  res = st.executeQuery("select so_cust from so_mstr where so_po = " + "'" + po + "'" +";");
                while (res.next()) {
                    billto = res.getString("so_cust");
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
         return billto;
     }

    public static String getOrderByPO(String po) {
         String order = "";
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
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);



                  res = st.executeQuery("select so_nbr from so_mstr where so_po = " + "'" + po + "'" +";");
                while (res.next()) {
                    order = res.getString("so_nbr");
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
         return order;
     }

    
    public static Double getOrderItemAllocatedQty(String item, String site) {
     if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getOrderItemAllocatedQty"});
            list.add(new String[]{"param1",  item});
            list.add(new String[]{"param2",  site});
            try {
                return jsonToDouble(sendServerPost(list, "", null, "dataServINV")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0.00;
            }
     }
     
     Double qty = 0.00;
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
            

           res = st.executeQuery("SELECT  sum(case when sod_all_qty = '' then 0 else (sod_all_qty - sod_shipped_qty) end) as allqty  " +
                                    " FROM  sod_det inner join so_mstr on so_nbr = sod_nbr  " +
                                    " where sod_item = " + "'" + item + "'" + 
                                    " AND so_status <> " + "'" + getGlobalProgTag("closed") + "'" +
                                    " AND so_site = " + "'" + site + "'" +          
                                    " group by sod_item ;");

                                    while (res.next()) {
                                    qty = res.getDouble("allqty");
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
    return qty;

    }

    public static double getOrderTotalTax(String nbr) {
       double tax = 0;
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
            
            double ordertotal = 0;
            
            res = st.executeQuery("SELECT  sum(sod_netprice * sod_ord_qty) as mytotal  " +
                                    " FROM  sod_det  " +
                                    " where sod_nbr = " + "'" + nbr + "'" +       
                                    ";");
                while (res.next()) {
                    ordertotal += res.getDouble("mytotal");
                }
            
            res = st.executeQuery("SELECT * " +
                                    " FROM  sos_det  " +
                                    " where sos_nbr = " + "'" + nbr + "'" +
                                    " and sos_type = 'tax' " +        
                                    " ;");

                double sosamt = 0;
                while (res.next()) {
                    sosamt = res.getDouble("sos_amt");
                    if (res.getString("sos_amttype").equals("percent")) {
                        if (sosamt > 0)
                        tax += (ordertotal * (sosamt / 100)); 
                    } else {
                       tax += sosamt;
                    }
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
    return tax;

    }
    
    public static double getSVOrderTotalTax(String nbr) {
    if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getSVOrderTotalTax"});
            list.add(new String[]{"param1",  nbr});
            try {
                return jsonToDouble(sendServerPost(list, "", null, "dataServORD")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0.00;
            }
     } 
    double tax = 0;
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
            
            double ordertotal = 0;
            
            res = st.executeQuery("SELECT  sum(svd_netprice * svd_qty) as mytotal  " +
                                    " FROM  svd_det  " +
                                    " where svd_nbr = " + "'" + nbr + "'" +       
                                    ";");
                while (res.next()) {
                    ordertotal += res.getDouble("mytotal");
                }
            
            res = st.executeQuery("SELECT * " +
                                    " FROM  sos_det  " +
                                    " where sos_nbr = " + "'" + nbr + "'" +
                                    " and sos_type = 'tax' " +        
                                    " ;");

                double sosamt = 0;
                while (res.next()) {
                    sosamt = res.getDouble("sos_amt");
                    if (res.getString("sos_amttype").equals("percent")) {
                        if (sosamt > 0)
                        tax += (ordertotal * (sosamt / 100)); 
                    } else {
                       tax += sosamt;
                    }
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
    return tax;

    }
    
    
    public static double getOrderTotal(String nbr) {
       double tax = 0;
       double disc = 0;
       double charge = 0;
       double ordertotal = 0;
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
            res = st.executeQuery("SELECT  sum(sod_netprice * sod_ord_qty) as mytotal  " +
                                    " FROM  sod_det  " +
                                    " where sod_nbr = " + "'" + nbr + "'" +       
                                    ";");
                while (res.next()) {
                    ordertotal += res.getDouble("mytotal");
                }
            
            res = st.executeQuery("SELECT * " +
                                    " FROM  sos_det  " +
                                    " where sos_nbr = " + "'" + nbr + "'" +
                                    " and sos_type = 'tax' " +        
                                    " ;");

                double sosamt = 0;
                while (res.next()) {
                    sosamt = res.getDouble("sos_amt");
                    if (res.getString("sos_amttype").equals("percent")) {
                        if (sosamt > 0)
                        tax += (ordertotal * (sosamt / 100)); 
                    } else {
                       tax += sosamt;
                    }
                }
            
                res = st.executeQuery("SELECT * " +
                                    " FROM  sos_det  " +
                                    " where sos_nbr = " + "'" + nbr + "'" +
                                    " and sos_type <> 'tax' " +        
                                    " ;");

                sosamt = 0;
                while (res.next()) {
                    sosamt = res.getDouble("sos_amt");
                    if (res.getString("sos_type").equals("charge")) {
                       charge += sosamt; 
                    }
                    if (res.getString("sos_type").equals("discount")) {
                       if (sosamt > 0)
                        disc += (ordertotal * (sosamt / 100)); 
                    }
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
    return ordertotal + charge + tax;

    }
    
    public static String getOrderItem(String order, String line) {
        String item = "";
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
                res = st.executeQuery("select sod_item from sod_det where sod_nbr = " + "'" + order + "'" + 
                        " and sod_line = " + "'" + line + "'" + ";");
                while (res.next()) {
                    item = res.getString("sod_item");
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
             return item;
    }
    
    public static double getNetPrice(String order, String line) {
        double price = 0.00;
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
                res = st.executeQuery("select sod_netprice from sod_det where sod_nbr = " + "'" + order + "'" + 
                        " and sod_line = " + "'" + line + "'" + ";");
                while (res.next()) {
                    price = res.getDouble("sod_netprice");
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
             return price;
    }
    
    
    public static String[] getOrderLineInfo(String order, String line) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOrderLineInfo"});
            list.add(new String[]{"param1", order});
            list.add(new String[]{"param2", line});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServORD"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        } 
        
        String[] x = null;  // returns item, desc, ordqty, uom, listprice, disc, netprice, custitem, wh, loc, po
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
                res = st.executeQuery("select sod_item, sod_desc, sod_ord_qty, sod_uom, sod_listprice, sod_disc, sod_netprice, sod_custitem, sod_wh, sod_loc, sod_po from sod_det where sod_nbr = " + "'" + order + "'" + 
                        " and sod_line = " + "'" + line + "'" + ";");
                while (res.next()) {
                    x = new String[]{res.getString("sod_item"), 
                        res.getString("sod_desc"), 
                        res.getString("sod_ord_qty"),
                        res.getString("sod_uom"),
                        res.getString("sod_listprice"),
                        res.getString("sod_disc"),
                        res.getString("sod_netprice"),
                        res.getString("sod_custitem"),
                        res.getString("sod_wh"),
                        res.getString("sod_loc"),
                        res.getString("sod_po")};
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
    
    
    public static ArrayList<String> getOrderLines(String order) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOrderLines"});
            list.add(new String[]{"param1", order});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServORD"));
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
        Statement st = con.createStatement();
        ResultSet res = null;
        try{

           res = st.executeQuery("SELECT sod_line from sod_det " +
                   " where sod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("sod_line"));
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
    
    public static ArrayList<String> getServiceOrderLines(String order) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getServiceOrderLines"});
            list.add(new String[]{"param1", order});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServORD"));
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
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
           

           res = st.executeQuery("SELECT svd_line from svd_det " +
                   " where svd_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("svd_line"));
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
    
    public static boolean isServiceOrderGeneric(String order) {
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
            
                  res = st.executeQuery("select sv_nbr from sv_mstr where sv_char1 = 'generic' and sv_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    x = true;
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

    public static boolean isDuplicatePO(String billto, String po) {
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
            
                  res = st.executeQuery("select so_po from so_mstr where so_cust = " + "'" + billto + "'" + 
                          " and so_po = " + "'" + po + "'" +
                          ";");
                while (res.next()) {
                    x = true;
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

    
    public static String getOrderCurrency(String order) {
        String curr = "";
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
                res = st.executeQuery("select so_curr from so_mstr where so_nbr = " + "'" + order + "'" +";");
                while (res.next()) {
                    curr = res.getString("so_curr");
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
             return curr;
    }
    
    
    public static ArrayList getOpenOrdersList() {
   ArrayList mylist = new ArrayList() ;

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

            res = st.executeQuery("select so_nbr from so_mstr where so_status = " + "'" + getGlobalProgTag("open") + "'" + " or so_status = " + "'" + getGlobalProgTag("commit") + "'" + " or so_status = " + "'" + getGlobalProgTag("backorder") + "'" + " ;");
                   while (res.next()) {
                      mylist.add(res.getString(("so_nbr")));
                   }

       }
        catch (SQLException s){
             bslog(s);
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

    public static void updateQuoteStatus(String nbr, String status, String ref) {
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update quo_mstr set quo_status = " + "'" + status + "'" + "," +
                 " quo_ref = " + "'" + ref + "'" +
                 " where quo_nbr = " + "'" + nbr + "'" + ";" );
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
    
    public static void updateOrderChangeStatus(String changeID, String status) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updateOrderChangeStatus"});
            list.add(new String[]{"param1",  changeID});
            list.add(new String[]{"param2",  status});
            try {
                sendServerPost(list, "", null, "dataServORD");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
       
        try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update so_chg set soc_status = " + "'" + status + "'" + 
                 " where soc_id = " + "'" + changeID + "'" + ";" );
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
    
    public static void updateOrderStatus(String order, String status) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updateOrderStatus"});
            list.add(new String[]{"param1",  order});
            list.add(new String[]{"param2",  status});
            try {
                sendServerPost(list, "", null, "dataServORD");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
       
        try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update so_mstr set so_status = " + "'" + status + "'" + 
                 " where so_nbr = " + "'" + order + "'" + ";" );
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
    
    public static void updateServiceOrderType(String order, String ordtype) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updateServiceOrderType"});
            list.add(new String[]{"param1",  order});
            list.add(new String[]{"param2",  ordtype});
            try {
                sendServerPost(list, "", null, "dataServORD");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
       
        try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update sv_mstr set sv_type = " + "'" + ordtype + "'" + 
                 " where sv_nbr = " + "'" + order + "'" + ";" );
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
    
    public static void updateOrderStatusByPO(String po, String status) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updateOrderStatusByPO"});
            list.add(new String[]{"param1",  po});
            list.add(new String[]{"param2",  status});
            try {
                sendServerPost(list, "", null, "dataServORD");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
       
       LocalDate now = LocalDate.now();
       now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update so_mstr set so_status = " + "'" + status + "'" + "," +
                 " so_mod_date = " + "'" + now + "'" +
                 " where so_po = " + "'" + po + "'" + ";" );
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
    
    public static void updateOrder05(String po, String duedate, ArrayList<String[]> detlist) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updateOrder05"});
            list.add(new String[]{"param1",  po});
            try {
                sendServerPost(list, "", null, "dataServORD");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
       
        LocalDate now = LocalDate.now();
        now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        double netprice = 0.0;
        try{
           st.executeUpdate(
                 " update so_mstr set so_due_date = " + "'" + duedate + "'" + "," +
                 " so_mod_date = " + "'" + now + "'" +
                 " where so_po = " + "'" + po + "'" + ";" );
           for (String[] det : detlist) { // custline, item, custitem, qty, listprice
               
               res = st.executeQuery("select sod_disc from sod_det " +
                    " where sod_po = " + "'" + po + "'" +
                 " and sod_custline = " + "'" + det[0] + "'" + ";" );
                while (res.next()) {
                    if (res.getDouble("sod_disc") != 0) {
                    netprice = bsParseDouble(det[4]) - (bsParseDouble(det[4]) * (res.getDouble("sod_disc") / 100) );
                    } else {
                        netprice = bsParseDouble(det[4]);
                    }
                }
               
               st.executeUpdate(
                 " update sod_det set sod_ord_qty = " + "'" + det[3] + "'" + "," +
                 " sod_listprice = " + "'" + det[4] + "'" + "," +
                 " sod_netprice = " + "'" + bsNumber(netprice) + "'" + "," +        
                 " sod_item = " + "'" + det[1] + "'" + "," +
                 " sod_custitem = " + "'" + det[2] + "'" +         
                 " where sod_po = " + "'" + po + "'" +
                 " and sod_custline = " + "'" + det[0] + "'" + ";" );
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
        
    public static void applyOrderChange(String changeID, String po) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "applyOrderChange"});
            list.add(new String[]{"param1",  changeID});
            list.add(new String[]{"param2",  po});
            try {
                sendServerPost(list, "", null, "dataServORD");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
        
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        ResultSet res = null;
        LocalDate now = LocalDate.now();
        now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int i = 0;
        
        ArrayList<String[]> list = new ArrayList<String[]>();
        try{
                res = st.executeQuery("select * from sod_chg inner join so_chg on sodc_id = soc_id " +
                    " where sodc_id = " + "'" + changeID + "'" +
                    " and sodc_po = " + "'" + po + "'");
                while (res.next()) {
                    list.add(new String[]{res.getString("soc_duedate"),
                    res.getString("sodc_po"),
                    res.getString("sodc_line"),
                    res.getString("sodc_item"),
                    res.getString("sodc_price"),
                    res.getString("sodc_qty"),
                    res.getString("soc_type")});
                }
                for (String[] s : list) {
                    if (i == 0) {
                      st.executeUpdate(
                        " update so_chg set soc_status = " + "'applied'" + "," + " soc_applydate = " + "'" + now + "'" + 
                        " where soc_id = " + "'" + changeID + "'" + " and soc_po = " + "'" + po + "'" + ";" );  
                      if (s[6].equals("01")) {
                          st.executeUpdate(
                        " update so_mstr set so_mod_date = " + "'" + setDateDB(new Date()) + "'" + "," + 
                        " so_due_date = " + "'" + s[0] + "'" + "," +
                        " so_status = " + "'" + "cancel" + "'" +
                         " where so_po = " + "'" + po + "'" + ";" );
                      } else {
                      st.executeUpdate(
                        " update so_mstr set so_mod_date = " + "'" + setDateDB(new Date()) + "'" + "," + 
                        " so_due_date = " + "'" + s[0] + "'" + 
                         " where so_po = " + "'" + po + "'" + ";" );  
                      }
                    }
                      st.executeUpdate(
                        " update sod_det set sod_ord_qty = " + "'" + s[5] + "'" + "," +
                        " sod_listprice = " + "'" + s[4] + "'" + "," +
                        " sod_netprice = " + "'" + s[4] + "'" + "," +        
                        " sod_due_date = " + "'" + s[0] + "'" +        
                        " where sod_po = " + "'" + s[1] + "'" + 
                                " and sod_line = " + "'" + s[2] + "'" +
                                " and sod_item = " + "'" + s[3] + "'" +
                                ";" );
                    i++;
                }
                
           
         
           
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    }
    
    public static String _evaluateOrderChange(String changeID, String po, Connection con) throws SQLException {
         String x = "none";
         boolean isQtyChange = false;  
         boolean isPriceChange = false;  
         boolean isCancel = false;
 
         
        String sql = "select soc_type, sod_line, sod_item, sod_ord_qty, sod_listprice, sodc_qty, sodc_price from sod_chg " +
                " inner join so_chg on soc_id = sodc_id " +
                " inner join sod_det on sodc_po = sod_po and sodc_line = sod_line " +
                " where sod_po = ? " +
                " and sodc_id = ? " +
                ";";
                
            PreparedStatement ps = con.prepareStatement(sql) ;
            ps.setString(1, po);
            ps.setString(2, changeID);
            ResultSet res = ps.executeQuery();
               while (res.next()) {
                   if (bsParseDouble(res.getString("sod_ord_qty")) != bsParseDouble(res.getString("sodc_qty"))) {
                       isQtyChange = true;
                   }   
                   if (bsParseDouble(res.getString("sod_listprice")) != bsParseDouble(res.getString("sodc_price"))) {
                       isPriceChange = true;
                   } 
                   if (res.getString("soc_type").equals("01")) {
                       isCancel = true;
                   }
                }
            
               ps.close();
               res.close();
               
               if (isQtyChange && isPriceChange) {
                   x = "multi";
               }
               if (isQtyChange && ! isPriceChange) {
                   x = "quantity";
               }
               if (! isQtyChange && isPriceChange) {
                   x = "price";
               }   
               if (isCancel) {
                   x = "cancel";
               }
        return x;
        
    }   
    
    public static String getOrderReportData(String[] keys) {
        StringBuilder sb = new StringBuilder();
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
             
                double qty = 0;
                double dol = 0;
                double total = 0;
                double tax = 0;
                double disc = 0;
                double charge = 0;
                int i = 0;
                String fromcust = "";
                String tocust = "";
                String fromcode = "";
                String tocode = "";
                String planstatus = "";
                
                // keys :   fromdate, todate, fromcust, tocust, site, datetype
             
                 if (keys[5].equals("create")) {
                    res = st.executeQuery("SELECT so_nbr, so_rmks, so_type, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " sum(sod_taxamt) as matltax, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when (sos_type = 'charge' or sos_type = 'shipping ADD') and sos_amttype = 'amount' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_create_date >= " + "'" + keys[0]  + "'" + 
                        " AND so_create_date <= " + "'" + keys[1] + "'" + 
                        " AND so_cust >= " + "'" + keys[2] + "'" + 
                        " AND so_cust <= " + "'" + keys[3] + "'" + 
                        " AND so_site = " + "'" + keys[4] + "'" + 
                         " group by so_nbr, so_rmks, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status order by so_nbr asc ;"); 
                 } else if (keys[5].equals("due")) {
                        res = st.executeQuery("SELECT so_nbr, so_rmks, so_type, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " sum(sod_taxamt) as matltax, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when (sos_type = 'charge' or sos_type = 'shipping ADD') and sos_amttype = 'amount' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_due_date >= " + "'" + keys[0]  + "'" + 
                        " AND so_due_date <= " + "'" + keys[1] + "'" + 
                        " AND so_cust >= " + "'" + keys[2] + "'" + 
                        " AND so_cust <= " + "'" + keys[3] + "'" + 
                        " AND so_site = " + "'" + keys[4] + "'" + 
                         " group by so_nbr, so_rmks, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status order by so_nbr asc ;");
                 } else if (keys[5].equals("modified")) {
                        res = st.executeQuery("SELECT so_nbr, so_rmks, so_type, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " sum(sod_taxamt) as matltax, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when (sos_type = 'charge' or sos_type = 'shipping ADD') and sos_amttype = 'amount' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_mod_date >= " + "'" + keys[0]  + "'" + 
                        " AND so_mod_date <= " + "'" + keys[1] + "'" + 
                        " AND so_cust >= " + "'" + keys[2] + "'" + 
                        " AND so_cust <= " + "'" + keys[3] + "'" + 
                        " AND so_site = " + "'" + keys[4] + "'" + 
                         " group by so_nbr, so_rmks, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status order by so_nbr asc ;");       
                 } else {
                        res = st.executeQuery("SELECT so_nbr, so_rmks, so_type, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " sum(sod_taxamt) as matltax, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when (sos_type = 'charge' or sos_type = 'shipping ADD') and sos_amttype = 'amount' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_ord_date >= " + "'" + keys[0]  + "'" + 
                        " AND so_ord_date <= " + "'" + keys[1] + "'" + 
                        " AND so_cust >= " + "'" + keys[2] + "'" + 
                        " AND so_cust <= " + "'" + keys[3] + "'" + 
                        " AND so_site = " + "'" + keys[4] + "'" + 
                         " group by so_nbr, so_rmks, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status order by so_nbr asc ;");
                 }
                
                  
                
                    while (res.next()) {
                    total = 0;
                    tax = 0;
                    disc = 0;
                    charge = 0;

                    if (res.getDouble("discountpercent") != 0) {
                      disc = res.getDouble("totdol") * (res.getDouble("discountpercent") / 100.0);
                    } else {
                      disc = 0;  
                    }
                    charge = res.getDouble("charge");
                    total = res.getDouble("totdol") + charge;  // charges added to total before taxing
                    
                    // now do tax
                    if (res.getDouble("taxpercent") != 0) {
                      tax = total * (res.getDouble("taxpercent") / 100.0);
                    } else {
                      tax = 0;  
                    }
                    tax += (res.getDouble("taxcharge") + res.getDouble("matltax"));
                                        
                    total = total + tax;
                    
                    sb.append(bsNumber(res.getString("so_nbr"))).append(",");
                    sb.append(res.getString("so_cust")).append(",");
                    sb.append(res.getString("so_po")).append(",");
                    sb.append(res.getString("so_rmks")).append(",");
                    sb.append(getDateDB(res.getString("so_create_date"))).append(",");
                    sb.append(getDateDB(res.getString("so_due_date"))).append(",");
                    sb.append(bsNumber(res.getDouble("totqty"))).append(",");
                    sb.append(bsNumber(total)).append(",");
                    sb.append(res.getString("so_curr")).append(",");
                    sb.append(res.getString("so_status")).append(",");
                    sb.append(res.getString("so_mod_date")).append("\n");
                       
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
        return sb.toString();
    }
    
    public static String getOrderBrowseView(String[] keys) {
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
             
                double qty = 0;
                double dol = 0;
                double total = 0;
                double tax = 0;
                double disc = 0;
                double charge = 0;
                int i = 0;
                String fromcust = "";
                String tocust = "";
                String fromcode = "";
                String tocode = "";
                String planstatus = "";
                
                // keys :   fromdate, todate, fromcust, tocust, site, datetype
             
                 if (keys[5].equals("create")) {
                    res = st.executeQuery("SELECT so_nbr, so_rmks, so_type, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " sum(sod_taxamt) as matltax, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when (sos_type = 'charge' or sos_type = 'shipping ADD') and sos_amttype = 'amount' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_create_date >= " + "'" + keys[0]  + "'" + 
                        " AND so_create_date <= " + "'" + keys[1] + "'" + 
                        " AND so_cust >= " + "'" + keys[2] + "'" + 
                        " AND so_cust <= " + "'" + keys[3] + "'" + 
                        " AND so_site = " + "'" + keys[4] + "'" + 
                         " group by so_nbr, so_rmks, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status order by so_nbr asc ;"); 
                 } else if (keys[5].equals("due")) {
                        res = st.executeQuery("SELECT so_nbr, so_rmks, so_type, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " sum(sod_taxamt) as matltax, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when (sos_type = 'charge' or sos_type = 'shipping ADD') and sos_amttype = 'amount' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_due_date >= " + "'" + keys[0]  + "'" + 
                        " AND so_due_date <= " + "'" + keys[1] + "'" + 
                        " AND so_cust >= " + "'" + keys[2] + "'" + 
                        " AND so_cust <= " + "'" + keys[3] + "'" + 
                        " AND so_site = " + "'" + keys[4] + "'" + 
                         " group by so_nbr, so_rmks, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status order by so_nbr asc ;");
                 } else if (keys[5].equals("modified")) {
                        res = st.executeQuery("SELECT so_nbr, so_rmks, so_type, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " sum(sod_taxamt) as matltax, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when (sos_type = 'charge' or sos_type = 'shipping ADD') and sos_amttype = 'amount' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_mod_date >= " + "'" + keys[0]  + "'" + 
                        " AND so_mod_date <= " + "'" + keys[1] + "'" + 
                        " AND so_cust >= " + "'" + keys[2] + "'" + 
                        " AND so_cust <= " + "'" + keys[3] + "'" + 
                        " AND so_site = " + "'" + keys[4] + "'" + 
                         " group by so_nbr, so_rmks, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status order by so_nbr asc ;");       
                 } else {
                        res = st.executeQuery("SELECT so_nbr, so_rmks, so_type, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol, " +
                        " sum(sod_taxamt) as matltax, " +
                        " (select sum(case when sos_type = 'discount' and sos_amttype = 'percent' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'discountpercent', " +
                        " (select sum(case when (sos_type = 'charge' or sos_type = 'shipping ADD') and sos_amttype = 'amount' then sos_amt else '0' end) from sos_det where sos_nbr = so_nbr) as 'charge'," + 
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = so_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = so_nbr) as 'taxcharge' " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_ord_date >= " + "'" + keys[0]  + "'" + 
                        " AND so_ord_date <= " + "'" + keys[1] + "'" + 
                        " AND so_cust >= " + "'" + keys[2] + "'" + 
                        " AND so_cust <= " + "'" + keys[3] + "'" + 
                        " AND so_site = " + "'" + keys[4] + "'" + 
                         " group by so_nbr, so_rmks, so_cust, so_curr, so_po, so_create_date, so_due_date, so_mod_date, so_status order by so_nbr asc ;");
                 }
                
                  
                
                    while (res.next()) {
                    total = 0;
                    tax = 0;
                    disc = 0;
                    charge = 0;

                    if (res.getDouble("discountpercent") != 0) {
                      disc = res.getDouble("totdol") * (res.getDouble("discountpercent") / 100.0);
                    } else {
                      disc = 0;  
                    }
                    charge = res.getDouble("charge");
                    total = res.getDouble("totdol") + charge;  // charges added to total before taxing
                    
                    // now do tax
                    if (res.getDouble("taxpercent") != 0) {
                      tax = total * (res.getDouble("taxpercent") / 100.0);
                    } else {
                      tax = 0;  
                    }
                    tax += (res.getDouble("taxcharge") + res.getDouble("matltax"));
                                        
                    total = total + tax;
                    
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("so_nbr"));
                        rowArray.put(res.getString("so_cust"));
                        rowArray.put(res.getString("so_po"));
                        rowArray.put(res.getString("so_rmks"));
                        rowArray.put(res.getString("so_create_date"));
                        rowArray.put(res.getString("so_due_date"));
                        rowArray.put(bsNumber(res.getDouble("totqty"))); 
                        rowArray.put(total); 
                        rowArray.put(res.getString("so_curr"));
                        rowArray.put(res.getString("so_status"));
                        rowArray.put(res.getString("so_mod_date"));
                        jsonarray.put(rowArray);
                    /*
                    sb.append(bsNumber(res.getString("so_nbr"))).append(",");
                    sb.append(res.getString("so_cust")).append(",");
                    sb.append(res.getString("so_po")).append(",");
                    sb.append(res.getString("so_rmks")).append(",");
                    sb.append(getDateDB(res.getString("so_create_date"))).append(",");
                    sb.append(getDateDB(res.getString("so_due_date"))).append(",");
                    sb.append(bsNumber(res.getDouble("totqty"))).append(",");
                    sb.append(bsNumber(total)).append(",");
                    sb.append(res.getString("so_curr")).append(",");
                    sb.append(res.getString("so_status")).append(",");
                    sb.append(res.getString("so_mod_date")).append("\n");
                      */ 
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
    
    public static String getServiceOrderBrowseView(String[] keys) {
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
             
                double qty = 0;
                double dol = 0;
                double total = 0;
                double tax = 0;
                double disc = 0;
                double charge = 0;
                int i = 0;
                String fromcust = "";
                String tocust = "";
                String fromcode = "";
                String tocode = "";
                String planstatus = "";
                
                // keys :   fromdate, todate, fromcust, tocust, site, datetype
             
                 res = st.executeQuery("select sv_nbr, sv_cust, sv_ship, sv_type, sv_status, sv_create_date, sv_due_date, sv_issched, sum(svd_qty * svd_netprice) as 'price', " +
                          " (select sum(case when sos_type = 'tax' and sos_amttype = 'percent' then sos_amt end) from sos_det where sos_nbr = sv_nbr)as 'taxpercent', " +
                        " (select sum(case when sos_type = 'tax' and sos_amttype = 'amount' then sos_amt end) from sos_det where sos_nbr = sv_nbr) as 'taxcharge' " +
                          " from sv_mstr " +
                        " inner join svd_det on svd_nbr = sv_nbr " +
                        " where sv_create_date >= " + "'" + keys[0] + "'" + 
                        " and sv_create_date <= " + "'" + keys[1] + "'" +
                        " and sv_cust >= " + "'" + keys[2] + "'" +
                        " and sv_cust <= " + "'" + keys[3] + "'" +
                        " and sv_site = " + "'" + keys[4] + "'" +        
                        " group by sv_nbr, sv_cust, sv_ship, sv_type, sv_status, sv_create_date, sv_due_date, sv_issched " +
                        " order by sv_nbr desc;");
                
                  
                
                    while (res.next()) {
                    total = 0;
                    tax = 0;
                    
                     if (res.getDouble("taxpercent") != 0) {
                          tax = res.getDouble("price") * (res.getDouble("taxpercent") / 100.0);
                         } else {
                           tax = 0;  
                         }
                        tax += res.getDouble("taxcharge");
                                        
                        total = res.getDouble("price") + tax;
                    
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("sv_nbr"));
                        rowArray.put(res.getString("sv_cust"));
                        rowArray.put(res.getString("sv_ship"));
                        rowArray.put(res.getString("sv_type"));
                        rowArray.put(res.getString("sv_status"));
                        rowArray.put(res.getString("sv_create_date"));
                        rowArray.put(res.getString("sv_due_date"));
                        rowArray.put(total); 
                        rowArray.put("print");
                        jsonarray.put(rowArray);
                    /*
                    mymodel.addRow(new Object[]{
                             BlueSeerUtils.clickflag, 
                             BlueSeerUtils.clickbasket, 
                               bsNumber(res.getInt("sv_nbr")),
                                res.getString("sv_cust"),
                                res.getString("sv_ship"),
                                res.getString("sv_type"),
                                res.getString("sv_status"),
                                getDateDB(res.getString("sv_create_date")),
                                getDateDB(res.getString("sv_due_date")),
                                bsParseDouble(currformatDouble(total)),
                                BlueSeerUtils.clickprint 
                            });
                      */ 
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
    
    public static String getQuoteBrowseView(String[] keys) {
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
                
            boolean isactive = BlueSeerUtils.ConvertStringToBool(keys[6]);
                // keys :   fromdate, todate, fromcust, tocust, site, datetype
             if (isactive) {
                  res = st.executeQuery("select quo_nbr, quo_status, quo_cust, quo_date, quo_expire, sum(quod_qty) as 'qty', sum(quod_qty * quod_netprice) as 'price' from quo_mstr " +
                        " inner join quo_det on quod_nbr = quo_nbr where " +
                        " quo_nbr >= " + "'" + keys[4] + "'" + " AND " +
                        " quo_nbr <= " + "'" + keys[5] + "'" + " AND " +
                        " quo_date >= " + "'" + keys[0] + "'" + " AND " +
                        " quo_date <= " + "'" + keys[1] + "'" + " AND " +
                        " quo_cust >= " + "'" + keys[2] + "'" + " AND " +
                        " quo_cust <= " + "'" + keys[3] + "'" + " AND " +
                        " quo_status = " + "'" + getGlobalProgTag("open") + "'" +
                        " group by quo_nbr, quo_status, quo_cust, quo_date, quo_expire;");
                 } else {
                    res = st.executeQuery("select quo_nbr, quo_status, quo_cust, quo_date, quo_expire, sum(quod_qty) as 'qty', sum(quod_qty * quod_netprice) as 'price' from quo_mstr " +
                        " inner join quo_det on quod_nbr = quo_nbr where " +
                        " quo_nbr >= " + "'" + keys[4] + "'" + " AND " +
                        " quo_nbr <= " + "'" + keys[5] + "'" + " AND " +
                        " quo_date >= " + "'" + keys[0] + "'" + " AND " +
                        " quo_date <= " + "'" + keys[1] + "'" + " AND " +
                        " quo_cust >= " + "'" + keys[2] + "'" + " AND " +
                        " quo_cust <= " + "'" + keys[3] + "'" + 
                        " group by quo_nbr, quo_status, quo_cust, quo_date, quo_expire;"); 
                 }
                
                  
                
                    while (res.next()) {
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("quo_nbr"));
                        rowArray.put(res.getString("quo_cust"));
                        rowArray.put(res.getString("quo_date"));
                        rowArray.put(res.getString("quo_expire"));
                        rowArray.put(res.getString("quo_status"));
                        rowArray.put(res.getString("qty"));
                        rowArray.put(res.getString("price"));
                        jsonarray.put(rowArray);
                    /*
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, BlueSeerUtils.clickbasket, 
                               bsNumber(res.getString("quo_nbr")),
                                res.getString("quo_cust"),
                                getDateDB(res.getString("quo_date")),
                                getDateDB(res.getString("quo_expire")),
                                res.getString("quo_status"),
                                bsNumber(res.getDouble("qty")),
                                bsParseDouble(currformatDouble(res.getDouble("price")))
                            });
                      */ 
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
    
    public static String getBillBrowseView(String[] keys) {
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
                
            boolean isactive = BlueSeerUtils.ConvertStringToBool(keys[6]);
                // keys :   fromdate, todate, fromcust, tocust, site, datetype
             if (isactive) {
                  res = st.executeQuery("select bill_nbr, bill_acctstatus, bill_cust, cm_name, bill_servicedate, bill_nextbilldate, sum(billd_qty) as 'qty', sum(billd_qty * billd_netprice) as 'price' from bill_mstr " +
                        " inner join cm_mstr on cm_code = bill_cust " +
                        " inner join bill_det on billd_nbr = bill_nbr where " +
                        " bill_nbr >= " + "'" + keys[4] + "'" + " AND " +
                        " bill_nbr <= " + "'" + keys[5] + "'" + " AND " +
                        " bill_servicedate >= " + "'" + keys[0] + "'" + " AND " +
                        " bill_servicedate <= " + "'" + keys[1] + "'" + " AND " +
                        " bill_cust >= " + "'" + keys[2] + "'" + " AND " +
                        " bill_cust <= " + "'" + keys[3] + "'" + " AND " +
                        " bill_site = " + "'" + keys[7] + "'" + " AND " +        
                        " bill_acctstatus = " + "'" + getGlobalProgTag("open") + "'" +
                        " group by bill_nbr, bill_acctstatus, bill_cust, cm_name, bill_servicedate, bill_nextbilldate;");
                 } else {
                    res = st.executeQuery("select bill_nbr, bill_acctstatus, bill_cust, cm_name, bill_servicedate, bill_nextbilldate, sum(billd_qty) as 'qty', sum(billd_qty * billd_netprice) as 'price' from bill_mstr " +
                        " inner join cm_mstr on cm_code = bill_cust " +
                        " inner join bill_det on billd_nbr = bill_nbr where " +
                        " bill_site = " + "'" + keys[7] + "'" + " AND " +         
                        " bill_nbr >= " + "'" + keys[4] + "'" + " AND " +
                        " bill_nbr <= " + "'" + keys[5] + "'" + " AND " +
                        " bill_servicedate >= " + "'" + keys[0]  + "'" + " AND " +
                        " bill_servicedate <= " + "'" + keys[1] + "'" + " AND " +
                        " bill_cust >= " + "'" + keys[2] + "'" + " AND " +
                        " bill_cust <= " + "'" + keys[3] + "'" + 
                        " group by bill_nbr, bill_acctstatus, bill_cust, cm_name, bill_servicedate, bill_nextbilldate;"); 
                 }

                
                  
                
                    while (res.next()) {
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("bill_nbr"));
                        rowArray.put(res.getString("bill_cust"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("bill_servicedate"));
                        rowArray.put(res.getString("bill_nextbilldate"));
                        rowArray.put(res.getString("bill_acctstatus"));
                        rowArray.put(res.getString("qty"));
                        rowArray.put(res.getString("price"));
                        jsonarray.put(rowArray);
                    /*
                   mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, BlueSeerUtils.clickbasket, 
                               bsNumber(res.getString("bill_nbr")),
                                res.getString("bill_cust"),
                                res.getString("cm_name"),
                                getDateDB(res.getString("bill_servicedate")),
                                getDateDB(res.getString("bill_nextbilldate")),
                                res.getString("bill_acctstatus"),
                                bsNumber(res.getDouble("qty")),
                                bsParseDouble(currformatDouble(res.getDouble("price")))
                            });
                      */ 
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
    
    public static String getOrderSourceBrowseView(String[] keys) {
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
                
            keys[0] = (keys[0].isBlank()) ? bsmf.MainFrame.lowchar : keys[0]; 
            keys[1] = (keys[1].isBlank()) ? bsmf.MainFrame.hichar : keys[1];
            keys[2] = (keys[2].isBlank()) ? bsmf.MainFrame.lowchar : keys[2]; 
            keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.hichar : keys[3];
                
             res = st.executeQuery("select so_nbr, so_cust, so_ord_date, so_type, so_status, so_issourced, " +
                      " sum(sod_ord_qty * sod_netprice) as 'total', sum(sod_ord_qty) as 'qty' " +
                         " from so_mstr inner join sod_det on sod_nbr = so_nbr where " +
                        " so_cust >= " + "'" + keys[0] + "'" + " AND " +
                        " so_cust <= " + "'" + keys[1] + "'" + " AND " +
                     " so_nbr >= " + "'" + keys[2] + "'" + " AND " +
                        " so_nbr <= " + "'" + keys[3] + "'" + 
                        " group by so_nbr, so_cust, so_ord_date, so_type, so_status, so_issourced ;");
                                 
                
                    while (res.next()) {
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("so_nbr"));
                        rowArray.put(res.getString("so_cust"));
                        rowArray.put(res.getString("so_ord_date"));
                        rowArray.put(res.getString("so_type"));
                        rowArray.put(res.getString("so_status"));
                        rowArray.put(res.getString("qty"));
                        rowArray.put(res.getString("total"));
                        rowArray.put(res.getString("so_issourced"));
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
    
    public static String getOrderSourceBrowseViewDet(String key) {
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
               
             res = st.executeQuery("select sod_nbr, sod_item, sod_netprice, sod_ord_qty, sod_shipped_qty, sod_status, sod_wh, sod_loc from sod_det " +
                        " where sod_nbr = " + "'" + key + "'" +  ";");
                
                    while (res.next()) {
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("sod_nbr"));
                        rowArray.put(res.getString("sod_item"));
                        rowArray.put(res.getString("sod_netprice"));
                        rowArray.put(res.getString("sod_ord_qty"));
                        rowArray.put(res.getString("sod_shipped_qty"));
                        rowArray.put(res.getString("sod_status"));
                        rowArray.put(res.getString("sod_wh"));
                        rowArray.put(res.getString("sod_loc"));
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
    
    
    public static String getBillBrowseDetail(String order, String detailtype) {
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
                if (detailtype.equals("trans")) {
                res = st.executeQuery("select billt_id, billt_nbr, billt_invoice, billt_invdate, billt_amt, billt_status from bill_tran " +
                        " where billt_nbr = " + "'" + order + "'" +  " order by billt_nbr desc;");
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("billt_id"));
                        rowArray.put(res.getString("billt_nbr"));  
                        rowArray.put(res.getString("billt_invoice"));
                        rowArray.put(res.getString("billt_invdate"));
                        rowArray.put(res.getDouble("billt_amt"));
                        rowArray.put(res.getString("billt_status")); 
                        jsonarray.put(rowArray);
                       
                    }
                } else {
                    res = st.executeQuery("select billd_nbr, billd_line, billd_item, billd_listprice, billd_disc, billd_netprice, billd_qty from bill_det " +
                        " where billd_nbr = " + "'" + order + "'" +  ";");
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("billd_nbr"));
                        rowArray.put(res.getString("billd_line"));  
                        rowArray.put(res.getString("billd_item"));
                        rowArray.put(res.getDouble("billd_listprice"));
                        rowArray.put(res.getDouble("billd_disc"));
                        rowArray.put(res.getDouble("billd_netprice"));
                        rowArray.put(res.getDouble("billd_qty"));
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    
    public static String getOrderItemBrowseView(String[] keys) {
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
             
                 res = st.executeQuery("select sod_item, sod_nbr, sod_ord_date, sod_ord_qty, sod_shipped_qty, sod_netprice, so_cust, cm_name from sod_det " +
                        " inner join so_mstr on so_nbr = sod_nbr " +
                        " inner join cm_mstr on cm_code = so_cust where " +
                        " so_site = " + "'" + keys[8] + "'" + " AND " + 
                        " sod_nbr >= " + "'" + keys[4] + "'" + " AND " +
                        " sod_nbr <= " + "'" + keys[5] + "'" + " AND " +
                        " sod_item >= " + "'" + keys[6] + "'" + " AND " +
                        " sod_item <= " + "'" + keys[7] + "'" + " AND " +        
                        " sod_ord_date >= " + "'" + keys[0] + "'" + " AND " +
                        " sod_ord_date <= " + "'" + keys[1] + "'" + " AND " +
                        " so_cust >= " + "'" + keys[2] + "'" + " AND " +
                        " so_cust <= " + "'" + keys[3] + "'" + 
                        " order by sod_nbr desc;");
                
                    while (res.next()) {
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("sod_nbr"));
                        rowArray.put(res.getString("so_cust"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("sod_ord_date"));
                        rowArray.put(res.getString("sod_item"));
                        rowArray.put(res.getDouble("sod_ord_qty"));
                        rowArray.put(res.getDouble("sod_shipped_qty")); 
                        rowArray.put(res.getDouble("sod_netprice")); 
                        jsonarray.put(rowArray);
                    /*
                    modeltable.addRow(new Object[]{BlueSeerUtils.clickflag, 
                                bsNumber(res.getString("sod_nbr")),
                                res.getString("so_cust"),
                                res.getString("cm_name"),
                                getDateDB(res.getString("sod_ord_date")),
                                res.getString("sod_item"),
                                bsNumber(res.getDouble("sod_ord_qty")),
                                bsNumber(res.getDouble("sod_shipped_qty")),
                                bsParseDouble(currformatDouble(res.getDouble("sod_netprice")))
                            });
                      */ 
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
    
    public static String getOrderBrowseDetail(String order) {
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
                res = st.executeQuery("select sod_nbr, sod_item, sod_netprice, sod_ord_qty, sod_shipped_qty, sod_status from sod_det " +
                        " where sod_nbr = " + "'" + order + "'" +  ";");
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("sod_nbr"));
                        rowArray.put(res.getString("sod_item"));
                        rowArray.put(res.getDouble("sod_netprice"));
                        rowArray.put(res.getDouble("sod_ord_qty"));
                        rowArray.put(res.getDouble("sod_shipped_qty"));
                        rowArray.put(res.getString("sod_status"));
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
    
    public static String getServiceOrderBrowseDetail(String order) {
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
                res = st.executeQuery("select svd_nbr, svd_item, svd_qty, svd_netprice from svd_det " +
                        " where svd_nbr = " + "'" + order + "'" +  ";");
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("svd_nbr"));
                        rowArray.put(res.getString("svd_item"));
                        rowArray.put(res.getDouble("svd_qty"));
                        rowArray.put(res.getDouble("svd_netprice"));
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
    
    public static String getQuoteBrowseDetail(String order) {
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
                res = st.executeQuery("select quod_nbr, quod_line, quod_item, quod_listprice, quod_disc, quod_netprice, quod_qty from quo_det " +
                        " where quod_nbr = " + "'" + order + "'" +  ";");
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("quod_nbr"));
                        rowArray.put(res.getString("quod_line"));  
                        rowArray.put(res.getString("quod_item"));
                        rowArray.put(res.getDouble("quod_listprice"));
                        rowArray.put(res.getDouble("quod_disc"));
                        rowArray.put(res.getDouble("quod_netprice"));
                        rowArray.put(res.getDouble("quod_qty"));
                        jsonarray.put(rowArray);
                        /*
                        modeldetail.addRow(new Object[]{ 
                      res.getString("quod_nbr"), 
                      res.getString("quod_line"),
                      res.getString("quod_item"),
                      bsFormatDouble(res.getDouble("quod_listprice")),
                      bsFormatDouble(res.getDouble("quod_disc")), 
                      bsFormatDouble(res.getDouble("quod_netprice")),
                      bsFormatDoubleZ(res.getDouble("quod_qty"))
                   });
                        */
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
    
    
    public static String getOrderChangeBrowseView(String[] keys) {
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
            String change = "";
            String status = "";
               
            // keys :   fromdate, todate, fromcust, tocust, site, posearch, isdetached
            try{
                if (! keys[5].isBlank()) {
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status  " +
                     " from so_mstr inner join so_chg on soc_po = so_po inner join cm_mstr on cm_code = so_cust where " +
                        " so_site = " + "'" + keys[4] + "'" + " AND " +
                        " so_po like " + "'%" + keys[5] + "%'" +
                        " order by so_nbr desc ;");
                 
             } else if (keys[6].equals("true")) {
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status  " +
                     " from so_chg left outer join so_mstr on so_po = soc_po left outer join cm_mstr on cm_code = so_cust where " +
                         " soc_billto >= " + "'" + keys[2] + "'" + " AND " +        
                        " soc_billto <= " + "'" + keys[3] + "'" + " AND " +
                        " soc_chgdate >= " + "'" + keys[0] + "'" + " AND " +
                        " soc_chgdate <= " + "'" + keys[1] + "'" + 
                        " order by soc_id desc ;");
             } else {
                 
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status  " +
                     " from so_mstr inner join so_chg on soc_po = so_po inner join cm_mstr on cm_code = so_cust where " +
                        " so_site = " + "'" + keys[4] + "'" + " AND " +
                        " so_cust >= " + "'" + keys[2] + "'" + " AND " +        
                        " so_cust <= " + "'" + keys[3] + "'" + " AND " +
                        " so_create_date >= " + "'" + keys[0] + "'" + " AND " +
                        " so_create_date <= " + "'" + keys[1] + "'" +     
                        " order by so_nbr desc ;");
                 
                 /*
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status,  " +
                     " (select sodc_qty from sod_chg where sodc_id = soc_id) as chgqty " +    
                     " from so_mstr inner join so_chg on soc_po = so_po inner join cm_mstr on cm_code = so_cust where " +
                        " so_site = " + "'" + keys[4] + "'" + " AND " +
                        " so_cust >= " + "'" + keys[2] + "'" + " AND " +        
                        " so_cust <= " + "'" + keys[3] + "'" + " AND " +
                        " so_create_date >= " + "'" + keys[0] + "'" + " AND " +
                        " so_create_date <= " + "'" + keys[1] + "'" +     
                        " order by so_nbr desc ;");
                 */
                 
             }  
             
                /* 
                String sql = "select soc_type, sod_line, sod_item, sod_ord_qty, sod_listprice, sodc_qty, sodc_price from sod_chg " +
                " inner join so_chg on soc_id = sodc_id " +
                " inner join sod_det on sodc_po = sod_po and sodc_line = sod_line " +
                " where sod_po = ? " +
                " and sodc_id = ? " +
                ";";
                */
                 
                    while (res.next()) {
                        
                        if (res.getString("so_nbr") != null && ! res.getString("so_nbr").isBlank()) {   
                        change = _evaluateOrderChange(res.getString("soc_id"), res.getString("so_po"), con); 
                        status = res.getString("soc_status");
                        } else {
                            change = "N/A";
                            status = "detached";
                        }
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("soc_id"));
                        rowArray.put(res.getString("so_nbr"));
                        rowArray.put(res.getString("soc_po"));
                        rowArray.put(res.getString("soc_chgdate"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("so_due_date"));
                        rowArray.put(res.getString("soc_duedate"));
                        rowArray.put(change);
                        rowArray.put(status);
                        rowArray.put("change");
                        rowArray.put("void");
                        rowArray.put("gear");
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
    
    public static String getOrderChangeBrowseDetail(String id, String po, String cbdetached) {
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
                if (BlueSeerUtils.ConvertStringToBool(cbdetached)) {
                    res = st.executeQuery("select sodc_line, sodc_item, sod_listprice, sodc_price, sod_ord_qty, sodc_qty from sod_chg " +
                        " inner join so_chg on soc_id = sodc_id " +
                        " left outer join sod_det on sodc_po = sod_po and sodc_line = sod_line " +
                        " where sodc_po = " + "'" + po + "'" + 
                        " and sodc_id = " + "'" + id + "'" +
                        ";"); 
                 } else {
                   res = st.executeQuery("select sodc_line, sodc_item, sod_listprice, sodc_price, sod_ord_qty, sodc_qty from sod_chg " +
                        " inner join so_chg on soc_id = sodc_id " +
                        " inner join sod_det on sodc_po = sod_po and sodc_line = sod_line " +
                        " where sod_po = " + "'" + po + "'" + 
                        " and soc_id = " + "'" + id + "'" +
                        ";");  
                 }
                    
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("gear");
                        rowArray.put(res.getString("sodc_line"));
                        rowArray.put(res.getString("sodc_item"));
                        rowArray.put(res.getDouble("sod_listprice"));
                        rowArray.put(res.getDouble("sodc_price"));
                        rowArray.put(res.getDouble("sod_ord_qty"));
                        rowArray.put(res.getDouble("sodc_qty"));
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
    
    
    public static String getOrderChangeReportData(String[] keys) {
        StringBuilder sb = new StringBuilder();
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
             
               String change = "";
               String status = "";
                
                // keys :   fromdate, todate, fromcust, tocust, site, posearch, isdetached
             
            if (! keys[5].isBlank()) {
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status  " +
                     " from so_mstr inner join so_chg on soc_po = so_po inner join cm_mstr on cm_code = so_cust where " +
                        " so_site = " + "'" + keys[4] + "'" + " AND " +
                        " so_po like " + "'%" + keys[5] + "%'" +
                        " order by so_nbr desc ;");
                 
             } else if (keys[6].equals("true")) {
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status  " +
                     " from so_chg left outer join so_mstr on so_po = soc_po left outer join cm_mstr on cm_code = so_cust where " +
                         " soc_billto >= " + "'" + keys[2] + "'" + " AND " +        
                        " soc_billto <= " + "'" + keys[3] + "'" + " AND " +
                        " soc_chgdate >= " + "'" + keys[0] + "'" + " AND " +
                        " soc_chgdate <= " + "'" + keys[1] + "'" + 
                        " order by soc_id desc ;");
             } else {
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status  " +
                     " from so_mstr inner join so_chg on soc_po = so_po inner join cm_mstr on cm_code = so_cust where " +
                        " so_site = " + "'" + keys[4] + "'" + " AND " +
                        " so_cust >= " + "'" + keys[2] + "'" + " AND " +        
                        " so_cust <= " + "'" + keys[3] + "'" + " AND " +
                        " so_create_date >= " + "'" + keys[0] + "'" + " AND " +
                        " so_create_date <= " + "'" + keys[1] + "'" +     
                        " order by so_nbr desc ;");
             }  
                
                  
                
                    while (res.next()) {

                   if (res.getString("so_nbr") != null && ! res.getString("so_nbr").isBlank()) {   
                        change = _evaluateOrderChange(res.getString("soc_id"), res.getString("so_po"), con); 
                        status = res.getString("soc_status");
                        } else {
                            change = "N/A";
                            status = "detached";
                        }
                    
                    sb.append(res.getString("soc_id")).append(",");
                    sb.append(res.getString("so_nbr")).append(",");
                    sb.append(res.getString("soc_po")).append(",");
                    sb.append(res.getString("soc_chgdate")).append(",");
                    sb.append(res.getString("cm_name")).append(",");
                    sb.append(res.getString("so_due_date")).append(",");
                    sb.append(res.getString("soc_duedate")).append(",");
                    sb.append(change).append(",");
                    sb.append(status).append("\n");
                       
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
        return sb.toString();
    }
    
    public static String getOrderDetailExport(String fromdate, String todate, String fromcust, String tocust, String site) {
        
        StringBuilder sb = new StringBuilder();
         try{
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            
            String headerkvpair = "";
            String detailkvpair = "";
            
            
            
            String header = "Sales Order Number, PO Number, Order Create Date, PO/Order Date, Customer Name, Shipto ID, Shipto Name, DueDate, Order Line Number, Item Number, Item Description, Master Sku, Sku Number, AltItemNumber, UOM, Order Quantity, Order Price, Pack Qty, Header KVPair, Detail KVPair";
          //  output.write(header + "\n");
            sb.append(header).append("\n");
            try {
                // for (int i = 0; i < list.size(); i++) {
               
               // headerkvpair = getEDIMetaValueAsKVString(tablereport.getValueAt(i, 4).toString(), "header","");
                                    
                res = st.executeQuery("select so_nbr, so_po, so_create_date, so_ord_date, " +
                        " cm_name, cms_plantcode, cms_name, so_due_date, " +
                        " sod_line, sod_item, sod_desc, '' as msku, sod_custitem, sod_char1, " +
                        " sod_uom, sod_ord_qty, sod_netprice, sod_char2 from so_mstr " + 
                        " inner join sod_det on sod_nbr = so_nbr " +
                        " inner join cm_mstr on cm_code = so_cust " +
                        " inner join cms_det on cms_code = so_cust and cms_shipto = so_ship " +
                        " where so_create_date >= " + "'" + fromdate  + "'" + 
                        " AND so_create_date <= " + "'" + todate + "'" + 
                        " AND so_cust >= " + "'" + fromcust + "'" + 
                        " AND so_cust <= " + "'" + tocust + "'" + 
                        " AND so_site = " + "'" + site + "'" + 
                         " order by so_nbr asc ;"); 
                int k = 0;
                while (res.next()) {
                    k++;
                     StringBuilder line = new StringBuilder();
                     for (int j = 1; j <= res.getMetaData().getColumnCount(); j++) {
                       line.append(res.getString(j).replace(",","")).append(",");
                     }
                     String[] hd = getEDIMetaValueAsKVStringPair(res.getString("so_po"), res.getString("sod_line"));
                    // headerkvpair = getEDIMetaValueAsKVString(res.getString("so_nbr"), "header", "");
                    // detailkvpair = getEDIMetaValueAsKVString(res.getString("so_nbr"), "detail", res.getString("sod_line"));
                     
                     sb.append(line.toString()).append(hd[0]).append(",").append(hd[1]).append("\n");
                    // output.write(line.toString() + headerkvpair + "," + detailkvpair);
                    // output.write("\n");
                     // now add detailkvpair
                     
                 }
               
                
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        } catch (SQLException e){
            MainFrame.bslog(e);
        } 
         
         return (sb == null) ? "no data" : sb.toString();
    }
    
    public static String getOrderDetailExportNew(String fromdate, String todate, String fromcust, String tocust, String site) {
        
        StringBuilder sb = new StringBuilder();
         try{
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            
            String headerkvpair = "";
            String detailkvpair = "";
            
            
            
            String header = "Sales Order Number, PO Number, Order Create Date, PO/Order Date, Customer Name, Shipto ID, Shipto Name, DueDate, Order Line Number, Item Number, Item Description, Master Sku, Sku Number, AltItemNumber, UOM, Order Quantity, Order Price, Pack Qty, Header KVPair, Detail KVPair";
          //  output.write(header + "\n");
            sb.append(header).append("=_=");
            try {
                // for (int i = 0; i < list.size(); i++) {
               
               // headerkvpair = getEDIMetaValueAsKVString(tablereport.getValueAt(i, 4).toString(), "header","");
                                    
                res = st.executeQuery("select so_nbr, so_po, so_create_date, so_ord_date, " +
                        " cm_name, cms_plantcode, cms_name, so_due_date, " +
                        " sod_line, sod_item, sod_desc, '' as msku, sod_custitem, sod_char1, " +
                        " sod_uom, sod_ord_qty, sod_netprice, sod_char2, " +
                        " (select ifnull(group_concat(concat(edim_key, '=', edim_value) separator ':'),'') from edi_meta where not edim_type like 'detail%' and edim_id = so_po order by edim_type) as 'kvheader', " +
                        " (select ifnull(group_concat(concat(edim_key, '=', edim_value) separator ':'),'') from edi_meta where edim_type = concat('detail:',sod_line) and edim_id = so_po order by edim_type) as 'kvdetail' " +
                        " from so_mstr " + 
                        " inner join sod_det on sod_nbr = so_nbr " +
                        " inner join cm_mstr on cm_code = so_cust " +
                        " inner join cms_det on cms_code = so_cust and cms_shipto = so_ship " +
                        " where so_create_date >= " + "'" + fromdate  + "'" + 
                        " AND so_create_date <= " + "'" + todate + "'" + 
                        " AND so_cust >= " + "'" + fromcust + "'" + 
                        " AND so_cust <= " + "'" + tocust + "'" + 
                        " AND so_site = " + "'" + site + "'" + 
                         " order by so_nbr asc ;"); 
                int k = 0;
                String xline = "";
                while (res.next()) {
                    k++;
                    xline = "";
                    // StringBuilder line = new StringBuilder();
                     for (int j = 1; j <= res.getMetaData().getColumnCount(); j++) {
                       xline = xline + res.getString(j).replace(",","") + ",";
                     }
                     if (xline != null && xline.length() > 0 && xline.charAt(xline.length() - 1) == ',') {
                        xline = xline.substring(0, xline.length() - 1); // SB remove last ,
                     }
                    sb.append(xline).append("=_=");
                 }
               
                
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        } catch (SQLException e){
            MainFrame.bslog(e);
        } 
         
         return (sb == null) ? "no data" : sb.toString();
    }
    
    
    public static String getOrderChangeExport(String fromdate, String todate, String fromcust, String tocust, String site) {
        
        StringBuilder sb = new StringBuilder();
         try{
             
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            
            String headerkvpair = "";
            String detailkvpair = "";
            
            
            
           String header = "ChangeID, Sales Order Number, PO Number, Order Date, Change Date, Customer Name, Shipto Name, Shipto City, Shipto State, Shipto Zip,  Original DueDate, New DueDate, Remarks, Change Remarks, Order Line Number, Change Code, Item Number, Item Description, Sku Number, Original Order Quantity, Change Quantity, Original Order Price, Change Price, HeaderKVPair, DetailKVPair ";
           sb.append(header).append("=_=");
            try {
                // for (int i = 0; i < list.size(); i++) {
               
               // headerkvpair = getEDIMetaValueAsKVString(tablereport.getValueAt(i, 4).toString(), "header","");
                
               res = st.executeQuery("select soc_id, so_nbr, so_po, so_create_date, soc_chgdate, " +
                        " cm_name, cms_name, cms_city, cms_state, cms_zip,  so_due_date, soc_duedate, " +
                        " so_rmks, soc_remarks, sod_line, sodc_change, sod_item, sod_desc, sod_custitem, " +
                        " sod_ord_qty, sodc_qty, sod_listprice, sodc_price from so_mstr " + 
                        " inner join sod_det on sod_nbr = so_nbr " +
                        " inner join so_chg on soc_po = so_po " + // soc_id = sodc_id " +
                        " inner join sod_chg on sodc_po = sod_po and sodc_line = sod_line " +
                        " inner join cm_mstr on cm_code = so_cust " +
                        " inner join cms_det on cms_code = so_cust and cms_shipto = so_ship " +                        
                        " where so_site = " + "'" + site + "'" + " AND " +
                        " so_cust >= " + "'" + fromcust + "'" + " AND " +        
                        " so_cust <= " + "'" + tocust + "'" + " AND " +
                        " so_create_date >= " + "'" + fromdate + "'" + " AND " +
                        " so_create_date <= " + "'" + todate + "'" +     
                        " order by soc_id ;");
               
               
                int k = 0;
                String xline = "";
                while (res.next()) {
                    xline = "";
                    k++;
                     // StringBuilder line = new StringBuilder();
                     for (int j = 1; j <= res.getMetaData().getColumnCount(); j++) {
                       xline = xline + res.getString(j).replace(",","") + ",";
                     }
                     if (xline != null && xline.length() > 0 && xline.charAt(xline.length() - 1) == ',') {
                        xline = xline.substring(0, xline.length() - 1); // SB remove last ,
                     }
                     String[] hd = getEDIMetaValueAsKVStringPair(res.getString("soc_id"), res.getString("sod_line"));
                    // headerkvpair = getEDIMetaValueAsKVString(res.getString("so_nbr"), "header", "");
                    // detailkvpair = getEDIMetaValueAsKVString(res.getString("so_nbr"), "detail", res.getString("sod_line"));
                     
                     sb.append(xline).append(hd[0]).append(",").append(hd[1]).append("=_=");
                    // output.write(line.toString() + headerkvpair + "," + detailkvpair);
                    // output.write("\n");
                     // now add detailkvpair
                     
                 }
               
                
           }
            catch (SQLException s){
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
        } catch (SQLException e){
            MainFrame.bslog(e);
        } 
         
         return (sb == null) ? "no data" : sb.toString();
    }
    
    
    public record salesOrder(String[] m, so_mstr so, ArrayList<sod_det> sod,
        ArrayList<sos_det> sos, ArrayList<sod_tax> sodtax, ArrayList<so_tax> sotax, cms_det cms, ArrayList<String[]> someta, cm_mstr cm) {
        public salesOrder(String[] m) {
            this (m, null, null, null, null, null, null, null, null);
        }
    }
    
    public record so_mstr(String[] m, String so_nbr, String so_cust, String so_ship, String so_site,
    String so_curr, String so_shipvia, String so_wh, String so_po, String so_due_date,
    String so_ord_date, String so_create_date, String so_userid, String so_status, String so_isallocated,
    String so_terms, String so_ar_acct, String so_ar_cc, String so_rmks, String so_type, String so_taxcode,
    String so_issourced, String so_confirm, String so_plan, String so_entrytype, String so_export_855, String so_mod_date, 
    String so_cascade, String so_slsperson1, String so_slsperson2) {
        public so_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", ""
                    );
        }
    }
    
                              
    public record sod_det(String[] m, String sod_nbr, int sod_line, String sod_item, String sod_custitem, 
        String sod_po, double sod_ord_qty, String sod_uom, double sod_all_qty, 
        double sod_listprice, double sod_disc, double sod_netprice, String sod_ord_date, 
        String sod_due_date, double sod_shipped_qty, String sod_status, String sod_wh, 
        String sod_loc, String sod_desc, double sod_taxamt, String sod_site, String sod_bom, String sod_ship,
        String sod_char1, String sod_char2, String sod_char3, String sod_custline, String sod_custuom, String sod_custprice) {
        public sod_det(String[] m) {
            this (m, "", 0, "", "", "", 0.00, "", 0.00, 0.00, 0.00,
                    0.00, "", "", 0.00, "", "", "", "", 0.00, "",
                    "", "", "", "", "", "", "", "" );
        }
    }
    
          
    public record so_tax(String[] m, String sot_nbr, String sot_desc, double sot_percent, String sot_type ) {
        public so_tax(String[] m) {
            this (m, "", "", 0.00, "");
        }
    }
    
   
     public record sod_tax(String[] m, String sodt_nbr, String sodt_line, String sodt_desc, 
        double sodt_percent, String sodt_type ) {
        public sod_tax(String[] m) {
            this (m, "", "", "", 0.00, "");
        }
    }
    
    public record sos_det(String[] m, String sos_nbr, String sos_desc, String sos_type, 
        String sos_amttype, double sos_amt) {
        public sos_det(String[] m) {
            this (m, "", "", "", "", 0.00);
        }
    }
    
    
    public record sv_mstr(String[] m, String sv_nbr, String sv_cust, String sv_ship, String sv_po,
        String sv_crew, String sv_create_date, String sv_due_date, String sv_rmks,
    String sv_status, String sv_issched, String sv_userid, String sv_type,
    String sv_char1, String sv_char2, String sv_char3, String sv_terms, 
    String sv_curr, String sv_ar_acct, String sv_ar_cc,String sv_onhold, 
    String sv_taxcode, String sv_site) {
        public sv_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", ""
                    );
        }
    }
    
    public record svd_det(String[] m, String svd_nbr, int svd_line, String svd_uom, 
        String svd_item, String svd_desc, String svd_type, String svd_custitem, 
        double svd_qty, double svd_completed_hrs, String svd_po,  String svd_ord_date, 
        String svd_due_date, String svd_create_date, String svd_char1, String svd_char2, String svd_char3,
        String svd_status, double svd_listprice, double svd_netprice, double svd_disc,  
        double svd_taxamt, String svd_taxcode, String svd_site) {
        public svd_det(String[] m) {
            this (m, "", 0, "", "", "", "", "", 0, 0, "",
                    "", "", "", "", "", "", "", 0, 0, 0,
                    0, "", "");
        }
    }
    
    public record pos_mstr(String[] m, String pos_nbr, String pos_key, String pos_type, String pos_entity, String pos_entityname, 
        String pos_entrydate, String pos_entrytime, String pos_aracct, String pos_arcc,  String pos_totqty, 
        String pos_totlines,  String pos_tottax, String pos_totamt, String pos_bank, String pos_grossamt,
        String pos_status, String pos_site)  {
        public pos_mstr(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                     "", "", "", "", "", "", "");
        }
    }

    public record pos_det(String[] m, String posd_nbr, String posd_line, String posd_item, 
        String posd_desc, String posd_ref, String posd_qty, String posd_listprice, String posd_disc, 
        String posd_netprice, String posd_tax, String posd_acct, String posd_cc)  {
        public pos_det(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                    "", "");
        }
    }

    public record order_ctrl(String[] m, String orc_autosource, String orc_autoinvoice, 
        String orc_autoallocate, String orc_custitem, String orc_srvm_type, 
        String orc_srvm_item_default, String orc_exceedqohu, String orc_varchar)  {
        public order_ctrl(String[] m) {
            this (m, "", "", "", "", "", "", "", "");
        }
    }
    
    public record quo_mstr(String[] m, String quo_nbr, String quo_cust, String quo_ship,
        String quo_site, String quo_date, String quo_expire, String quo_priceexpire, String quo_status, 
        String quo_rmks, String quo_ref, String quo_type, String quo_taxcode, String quo_disccode,
        String quo_groupcode, String quo_curr, String quo_approved, String quo_approver, String quo_varchar, 
        String quo_terms )  {
        public quo_mstr(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                     "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record quo_sac(String[] m, String quos_nbr, String quos_desc, String quos_type,
        String quos_amttype, double quos_amt, String quos_appcode 
        )  {
        public quo_sac(String[] m) {
            this (m, "", "", "", "", 0.00, "");
        }
    }
    
    
    public record quo_det(String[] m, String quod_nbr, int quod_line, String quod_item,
        String quod_isinv, String quod_desc, String quod_pricetype, double quod_listprice, double quod_disc, 
        double quod_netprice, double quod_qty, String quod_uom 
        )  {
        public quo_det(String[] m) {
            this (m, "", 0, "", "", "", "", 0.00, 0.00, 0.00, 0.00,
                     "");
        }
    }
    
    public record bill_mstr(String[] m, String bill_nbr, String bill_cust, 
        String bill_site, String bill_servicedate, String bill_billingdate, 
        String bill_termdate, String bill_lastbilldate, String bill_nextbilldate, 
        String bill_acctstatus, String bill_orderstatus, String bill_rmks, String bill_ref, 
        String bill_type, String bill_servicetype, String bill_subtype, String bill_billingtype,
        String bill_frequencytype, String bill_group, String bill_category, String bill_terms, String bill_autobill )  {
        public bill_mstr(String[] m) {
            this (m, "", "", "", "", "", "", "", "", "", "",
                     "", "", "", "", "", "", "", "", "", "",
                     "");
        }        
    }
    public record bill_det(String[] m, String billd_nbr, int billd_line, String billd_item,
        String billd_isinv, String billd_desc, String billd_pricetype, double billd_listprice, double billd_disc, 
        double billd_netprice, double billd_qty, String billd_uom 
        )  {
        public bill_det(String[] m) {
            this (m, "", 0, "", "", "", "", 0.00, 0.00, 0.00, 0.00,
                     "");
        }
    }
    public record bill_sac(String[] m, String bills_nbr, String bills_desc, String bills_type,
        String bills_amttype, double bills_amt, String bills_appcode 
        )  {
        public bill_sac(String[] m) {
            this (m, "", "", "", "", 0.00, "");
        }
    }
    public record bill_tran(String[] m, String billt_id, String billt_nbr, 
        String billt_invoice, double billt_amt, String billt_invdate,
        String billt_billingtype, String billt_frequencytype, String billt_servicedate,
        String billt_billingdate, String billt_usage, double billt_qty, 
        String billt_startdate, String billt_enddate, String billt_remarks, String billt_status 
        )  {
        public bill_tran(String[] m) {
            this (m, "", "", "", 0.00, "", "", "", "", "", "",
                    0.00, "", "", "", "");
        }
    }
    
    public record so_chg(String[] m, String soc_id, String soc_po, String soc_type,
    String soc_chgdate, String soc_duedate, String soc_billto, String soc_shipto, String soc_ref,
    String soc_misc1, String soc_misc2, String soc_misc3, String soc_status, String soc_userid,
    String soc_applydate ) {
        public so_chg(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "");
        }
    }
    
    public record sod_chg(String[] m, String sodc_id, String sodc_po, String sodc_line,
    String sodc_type, String sodc_item, String sodc_custitem, double sodc_qty, double sodc_price,
    String sodc_duedate, String sodc_misc) {
        public sod_chg(String[] m) {
            this(m, "", "", "", "", "", "", 0, 0, "", "");
        }
    }
    
}
