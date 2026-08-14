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
package com.blueseer.shp;
import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.dfdate;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData._getCMSDet;
import static com.blueseer.ctr.cusData._getCustMstr;
import com.blueseer.ctr.cusData.cm_mstr;
import com.blueseer.edi.EDI.edi810;
import com.blueseer.edi.EDI.edi855;
import com.blueseer.edi.EDI.edi856;
import com.blueseer.fap.fapData;
import static com.blueseer.fap.fapData.VouchAndPayTransaction;
import static com.blueseer.fap.fapData.VoucherTransaction;
import static com.blueseer.fap.fapData._VouchAndPayTransaction;
import static com.blueseer.fap.fapData._VoucherTransaction;
import com.blueseer.fap.fapData.vod_mstr;
import com.blueseer.fgl.fglData;
import static com.blueseer.fgl.fglData._glEntryFromSrvJobScan;
import static com.blueseer.fgl.fglData.glEntryXP;
import static com.blueseer.inv.invData._updateInventoryBalance;
import com.blueseer.ord.ordData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListString;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToBoolean;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import static com.blueseer.utl.BlueSeerUtils.setDateFormat;
import static com.blueseer.utl.BlueSeerUtils.setDateFormatNull;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.AREntry;
import static com.blueseer.utl.OVData.getCodeValueByCodeKey;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.vdr.venData.getVendInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JTable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
/**
 *
 * @author terryva
 */
public class shpData {
 
    
    
    private static int _addShipMstr(ship_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ship_mstr where sh_id = ?";
        String sqlInsert = "insert into ship_mstr (sh_id, sh_cust, sh_ship, sh_pallets, sh_boxes,  "
                    + "  sh_shipvia, sh_shipdate, sh_po_date, sh_ref, sh_po, " 
                    + " sh_rmks, sh_userid, sh_site, sh_curr, sh_wh, "
                    + " sh_cust_terms, sh_taxcode, sh_ar_acct, sh_ar_cc, sh_type, sh_so, sh_shipfrom, sh_trailer, sh_char2) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sh_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sh_id);
            ps.setString(2, x.sh_cust);
            ps.setString(3, x.sh_ship);
            ps.setInt(4, x.sh_pallets);
            ps.setInt(5, x.sh_boxes);
            ps.setString(6, x.sh_shipvia);
            ps.setString(7, x.sh_shipdate);
            ps.setString(8, x.sh_po_date);
            ps.setString(9, x.sh_ref);
            ps.setString(10, x.sh_po);
            ps.setString(11, x.sh_rmks);
            ps.setString(12, x.sh_userid);
            ps.setString(13, x.sh_site);
            ps.setString(14, x.sh_curr);
            ps.setString(15, x.sh_wh);
            ps.setString(16, x.sh_cust_terms);
            ps.setString(17, x.sh_taxcode);
            ps.setString(18, x.sh_ar_acct);
            ps.setString(19, x.sh_ar_cc);
            ps.setString(20, x.sh_type);
            ps.setString(21, x.sh_so);
            ps.setString(22, x.sh_shipfrom);
            ps.setString(23, x.sh_trailer);
            ps.setString(24, x.sh_char2);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
   
    private static int _addShipDet(ship_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ship_det where shd_id = ? and shd_line = ?";
        String sqlInsert = "insert into ship_det (shd_id, shd_line, shd_item, shd_so, shd_soline, shd_date, shd_po, shd_qty, shd_curr, shd_uom, "
                        + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, shd_loc, shd_taxamt, shd_cont, shd_serial, shd_site, shd_bom, shd_custitem, shd_packqty, shd_kvpair ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.shd_id);
          ps.setInt(2, x.shd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.shd_id);
            ps.setInt(2, x.shd_line);
            ps.setString(3, x.shd_item);
            ps.setString(4, x.shd_so);
            ps.setInt(5, x.shd_soline);
            ps.setString(6, x.shd_date);
            ps.setString(7, x.shd_po);
            ps.setDouble(8, x.shd_qty);
            ps.setString(9, x.shd_curr);
            ps.setString(10, x.shd_uom);
            ps.setDouble(11, x.shd_netprice);
            ps.setDouble(12, x.shd_disc);
            ps.setDouble(13, x.shd_listprice);
            ps.setString(14, x.shd_desc);
            ps.setString(15, x.shd_wh);
            ps.setString(16, x.shd_loc);
            ps.setDouble(17, x.shd_taxamt);
            ps.setString(18, x.shd_cont);
            ps.setString(19, x.shd_serial);
            ps.setString(20, x.shd_site);
            ps.setString(21, x.shd_bom);
            ps.setString(22, x.shd_custitem);
            ps.setDouble(23, x.shd_packqty);
            ps.setString(24, x.shd_kvpair);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
   
    private static int _addShipTree(ship_tree x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ship_tree where ship_parent = ? and ship_child = ? and ship_sh = ?";
        String sqlInsert = "insert into ship_tree (ship_parent, ship_child, ship_site, ship_type, ship_sh, ship_shline, ship_so, ship_soline, ship_po, ship_item, "
                        + "ship_qty, ship_serial) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.ship_parent);
          ps.setString(2, x.ship_child);
          ps.setString(3, x.ship_sh);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.ship_parent);
            ps.setString(2, x.ship_child);
            ps.setString(3, x.ship_site);
            ps.setString(4, x.ship_type);
            ps.setString(5, x.ship_sh);
            ps.setString(6, x.ship_shline);
            ps.setString(7, x.ship_so);
            ps.setString(8, x.ship_soline);
            ps.setString(9, x.ship_po);
            ps.setString(10, x.ship_item);
            ps.setDouble(11, x.ship_qty);
            ps.setString(12, x.ship_serial);
            
            rows = ps.executeUpdate();
            } 
            return rows;
    }
   
    
    private static int _addShipSummaryDet(shs_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from shs_det where shs_nbr = ?";
        String sqlInsert = "insert into shs_det (shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt ) "
                        + " values (?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.shs_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.shs_nbr);
            ps.setString(2, x.shs_so);
            ps.setString(3, x.shs_desc);
            ps.setString(4, x.shs_type);
            ps.setString(5, x.shs_amttype);
            ps.setString(6, x.shs_amt);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
   
    public static String[] addShipperTransaction(ArrayList<ship_det> shd, ship_mstr sh, ArrayList<ship_tree> sht) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addShipperTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(shd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sh);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sht);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServSHP"));
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
            _addShipMstr(sh, bscon, ps, res);  
            for (ship_det z : shd) {
                _addShipDet(z, bscon, ps, res);
            }
            if (sht != null) {
                for (ship_tree z : sht) {
                _addShipTree(z, bscon, ps, res);
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
    
    public static String[] _addShipperTransaction(ArrayList<ship_det> shd, ship_mstr sh, ArrayList<ship_tree> sht, Connection bscon) {
        String[] m = new String[2];
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            
            _addShipMstr(sh, bscon, ps, res);  
            for (ship_det z : shd) {
                _addShipDet(z, bscon, ps, res);
            }
            if (sht != null) {
                for (ship_tree z : sht) {
                _addShipTree(z, bscon, ps, res);
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
        }
    return m;
    }
    
    public static String[] confirmShipperTransaction(String type, String shipper, Date effdate) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","confirmShipperTransaction"});
            list.add(new String[]{"param1",type});
            list.add(new String[]{"param2",shipper});
            list.add(new String[]{"param3",setDateDB(effdate)});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServSHP"));
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
            
            AREntry("I", shipper, effdate, bscon);  
            // if sac 'shipping XXX' type...create voucher
            // String[] m = VoucherTransaction(OVData.getNextNbr("batch"), ddtype.getSelectedItem().toString() , createDetRecord(), createRecord(), false);
           // ArrayList<String[]> sac = shpData.getShipperSAC(shipper);
           // add function that takes shipper number and loops through sac to create vouchers
            if (! type.equals("freight")) {
            _addTranMstrShipper(shipper, effdate, bscon);
            _updateInventoryFromShipper(shipper, bscon);
            }
            
            fglData._glEntryFromShipper(shipper, effdate, bscon);
            
            
            _updateShipperStatus(shipper, effdate, bscon); 
            if (type.equals("order")) {
            _updateOrderFromShipper(shipper, bscon); 
            }
            
            if (type.equals("serviceorder")) {
            _updateServiceOrderFromShipper(shipper, bscon); 
            _glEntryFromSrvJobScan(shipper, bscon);
            _updateInventoryFromJob(shipper, bscon);
            }
            // if type.equals("cash")....no order to update
            
            if (OVData.isVoucherShippingSO()) {
            _processShipperVouchers(shipper, effdate, bscon);
            }
            bscon.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1125)};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
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
    
    public static String[] _confirmShipperTransaction(String type, String shipper, Date effdate, Connection bscon) {
        String[] m = new String[2];
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            
            AREntry("I", shipper, effdate, bscon);  
            // if sac 'shipping XXX' type...create voucher
            // String[] m = VoucherTransaction(OVData.getNextNbr("batch"), ddtype.getSelectedItem().toString() , createDetRecord(), createRecord(), false);
           // ArrayList<String[]> sac = shpData.getShipperSAC(shipper);
           // add function that takes shipper number and loops through sac to create vouchers
            if (! type.equals("freight")) {
            _addTranMstrShipper(shipper, effdate, bscon);
            _updateInventoryFromShipper(shipper, bscon);
            }
            
            fglData._glEntryFromShipper(shipper, effdate, bscon);
            
            
            _updateShipperStatus(shipper, effdate, bscon); 
            if (type.equals("order")) {
            _updateOrderFromShipper(shipper, bscon); 
            }
            if (type.equals("serviceorder")) {
            _updateServiceOrderFromShipper(shipper, bscon); 
            _glEntryFromSrvJobScan(shipper, bscon);
            _updateInventoryFromJob(shipper, bscon);
            }
            // if type.equals("cash")....no order to update
            
            if (OVData.isVoucherShippingSO()) {
            _processShipperVouchers(shipper, effdate, bscon);
            }
            bscon.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1125)};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
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
        }
    return m;
    }
    
    
    
    private static int _updateShipMstr(ship_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update ship_mstr set " 
                + " sh_shipdate = ?, sh_ref = ?, sh_rmks = ?, "
                + "sh_shipvia = ?, sh_pallets = ?, sh_boxes = ?, sh_trailer = ?, sh_char2 = ? "
                + " where sh_id = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(9, x.sh_id);
            ps.setString(1, x.sh_shipdate);
            ps.setString(2, x.sh_ref);
            ps.setString(3, x.sh_rmks);
            ps.setString(4, x.sh_shipvia);
            ps.setInt(5, x.sh_pallets);
            ps.setInt(6, x.sh_boxes);
            ps.setString(7, x.sh_trailer);
            ps.setString(8, x.sh_char2);  // ship ready
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateShipDet(ship_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ship_det where shd_id = ? and shd_line = ?";
        String sqlUpdate = "update ship_det set shd_item = ?, shd_so = ?, " +
                "shd_soline = ?, shd_date = ?, shd_po = ?, shd_qty = ?, " +
                " shd_netprice = ?, shd_disc = ?, shd_listprice = ?, shd_desc = ?, " +
                "shd_wh = ?, shd_loc = ?, shd_taxamt = ?, shd_cont = ?, shd_serial = ?, " +
                " shd_site = ?, shd_bom = ?, shd_packqty = ?, shd_uom = ?, shd_custitem = ? " +
                 " where shd_id = ? and shd_line = ? ; ";
        String sqlInsert = "insert into ship_det (shd_id, shd_line, shd_item, shd_so, shd_soline, " 
                        + " shd_date, shd_po, shd_qty,"
                        + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, "
                        + " shd_loc, shd_taxamt, shd_cont, shd_serial, shd_site, shd_bom, shd_packqty, shd_kvpair, shd_uom, shd_custitem  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.shd_id);
        ps.setInt(2, x.shd_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.shd_id);
            ps.setInt(2, x.shd_line);
            ps.setString(3, x.shd_item);
            ps.setString(4, x.shd_so);
            ps.setInt(5, x.shd_soline);
            ps.setString(6, x.shd_date);
            ps.setString(7, x.shd_po);
            ps.setDouble(8, x.shd_qty);
            ps.setDouble(9, x.shd_netprice);
            ps.setDouble(10, x.shd_disc);
            ps.setDouble(11, x.shd_listprice);
            ps.setString(12, x.shd_desc);
            ps.setString(13, x.shd_wh);
            ps.setString(14, x.shd_loc);
            ps.setDouble(15, x.shd_taxamt);
            ps.setString(16, x.shd_cont);
            ps.setString(17, x.shd_serial);
            ps.setString(18, x.shd_site); 
            ps.setString(19, x.shd_bom); 
            ps.setDouble(20, x.shd_packqty);
            ps.setString(21, x.shd_kvpair);
            ps.setString(22, x.shd_uom);
            ps.setString(23, x.shd_custitem);
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(21, x.shd_id);
            ps.setInt(22, x.shd_line);
            ps.setString(1, x.shd_item);
            ps.setString(2, x.shd_so);
            ps.setInt(3, x.shd_soline);
            ps.setString(4, x.shd_date);
            ps.setString(5, x.shd_po);
            ps.setDouble(6, x.shd_qty);
            ps.setDouble(7, x.shd_netprice);
            ps.setDouble(8, x.shd_disc);
            ps.setDouble(9, x.shd_listprice);
            ps.setString(10, x.shd_desc);
            ps.setString(11, x.shd_wh);
            ps.setString(12, x.shd_loc);
            ps.setDouble(13, x.shd_taxamt);
            ps.setString(14, x.shd_cont);
            ps.setString(15, x.shd_serial);
            ps.setString(16, x.shd_site); 
            ps.setString(17, x.shd_bom);
            ps.setDouble(18, x.shd_packqty);
            ps.setString(19, x.shd_uom);
            ps.setString(20, x.shd_custitem);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    public static String[] updateShipTransaction(ArrayList<String> lines, ArrayList<ship_det> shd, ship_mstr sh) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateShipTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(lines);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(shd);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(sh);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServSHP"));
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
            if (lines != null) {
             for (String line : lines) {
               _deleteShipperLines(sh.sh_id(), line, bscon);  // discard unwanted lines
             }
            } else {
                _deleteShipperLines(sh.sh_id(), bscon);  // delete all lines
            }
            for (ship_det z : shd) {
                _updateShipDet(z, bscon, ps, res);
            }
             _updateShipMstr(sh, bscon, ps, res);  // update so_mstr
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
    
    public static String[] deleteShipMstr(String x) { 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteShipMstr"});
            list.add(new String[]{"param1", x});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServSHP"));
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
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteShipMstr(x, con);  
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
   
    private static void _deleteShipMstr(String x, Connection con) throws SQLException { 
        String sql = "delete from ship_det where shd_id = ?; ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from shs_det where shs_nbr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from ship_tree where ship_sh = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from ship_log where shl_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        sql = "delete from ship_mstr where sh_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    private static void _deleteShipperLines(String x, Connection con) throws SQLException { 
        String sql = "delete from ship_det where shd_id = ?; ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.executeUpdate();
        ps.close();
    }
    
    
    private static void _deleteShipperLines(String x, String line, Connection con) throws SQLException { 
        String sql = "delete from ship_det where shd_id = ? and shd_line = ?; ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
        ps.close();
    }
    
    public static ship_mstr getShipMstr(String[] x) {
        ship_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShipMstr"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServSHP");
                r = objectMapper.readValue(returnstring, ship_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from ship_mstr where sh_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
      //  ps.setInt(1, bsParseInt(x[0]));  // may need to revisit for langpack issue
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ship_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                     
                        r = new ship_mstr(m, 
                                res.getString("sh_id"), 
                                res.getString("sh_cust"),
                                res.getString("sh_ship"), 
                                res.getInt("sh_pallets"),
                                res.getInt("sh_boxes"), 
                                res.getString("sh_shipvia"),
                                res.getString("sh_shipdate"), 
                                res.getString("sh_po_date"),
                                res.getString("sh_ref"), 
                                res.getString("sh_po"),
                                res.getString("sh_rmks"), 
                                res.getString("sh_userid"),
                                res.getString("sh_site"),
                                res.getString("sh_curr"),
                                res.getString("sh_wh"),
                                res.getString("sh_cust_terms"),
                                res.getString("sh_taxcode"),
                                res.getString("sh_ar_acct"),
                                res.getString("sh_ar_cc"),
                                res.getString("sh_type"),
                                res.getString("sh_so"),
                                res.getString("sh_shipfrom"),
                                res.getString("sh_trailer"),
                                res.getString("sh_status"),
                                res.getString("sh_char1"),
                                res.getString("sh_char2"),
                                res.getString("sh_char3")
                            );
                    }
                }
            } 
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ship_mstr(m);
        }
        return r;
    }
   
    public static Shipper getShipperMstrSet(String[] x ) {
        Shipper r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShipperMstrSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServSHP");
                r = objectMapper.readValue(returnstring, Shipper.class); 
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
            
            ship_mstr sh = _getShipMstr(x, bscon, ps, res);
            ArrayList<shs_det> shs = _getShipshs(x, bscon, ps, res);
            ArrayList<ship_det> shd = _getShipDet(x, bscon, ps, res);
            ArrayList<ship_tree> sht = _getShipTree(x, bscon, ps, res);
            ArrayList<sh_meta> shm = _getShipMeta(x, bscon, ps, res);
            cusData.cms_det cms = _getCMSDet(sh.sh_cust(), sh.sh_ship(), bscon, ps, res );
            cusData.cm_mstr cm = _getCustMstr(sh.sh_cust(), bscon, ps, res );
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new Shipper(m, sh, shd, shs, sht, shm, cms, cm);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new Shipper(m);
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
    
    private static ship_mstr _getShipMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ship_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from ship_mstr where sh_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ship_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new ship_mstr(m, 
                                res.getString("sh_id"), 
                                res.getString("sh_cust"),
                                res.getString("sh_ship"), 
                                res.getInt("sh_pallets"),
                                res.getInt("sh_boxes"), 
                                res.getString("sh_shipvia"),
                                res.getString("sh_shipdate"), 
                                res.getString("sh_po_date"),
                                res.getString("sh_ref"), 
                                res.getString("sh_po"),
                                res.getString("sh_rmks"), 
                                res.getString("sh_userid"),
                                res.getString("sh_site"),
                                res.getString("sh_curr"),
                                res.getString("sh_wh"),
                                res.getString("sh_cust_terms"),
                                res.getString("sh_taxcode"),
                                res.getString("sh_ar_acct"),
                                res.getString("sh_ar_cc"),
                                res.getString("sh_type"),
                                res.getString("sh_so"),
                                res.getString("sh_shipfrom"),
                                res.getString("sh_trailer"),
                                res.getString("sh_status"),
                                res.getString("sh_char1"),
                                res.getString("sh_char2"),
                                res.getString("sh_char3")
                            );
                }
            }
            return r;
    }
    
    private static ArrayList<ship_det> _getShipDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<ship_det> list = new ArrayList<ship_det>();
        ship_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from ship_det where shd_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ship_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    
                    r = new ship_det(m, res.getString("shd_id"), res.getInt("shd_line"), res.getString("shd_item"), 
                    res.getString("shd_custitem"), res.getString("shd_so"), res.getInt("shd_soline"), res.getString("shd_date"), res.getString("shd_po"),
                    res.getDouble("shd_qty"), res.getString("shd_uom"), res.getString("shd_curr"),
                    res.getDouble("shd_netprice"), res.getDouble("shd_disc"), res.getDouble("shd_listprice"), res.getString("shd_desc"), 
                    res.getString("shd_wh"), res.getString("shd_loc"), res.getDouble("shd_taxamt"),
                    res.getString("shd_cont"), res.getString("shd_ref"), res.getString("shd_serial"), res.getString("shd_site"), 
                    res.getString("shd_bom"), res.getDouble("shd_packqty"), res.getString("shd_kvpair") );
                    list.add(r);
                    }
            }
            return list;
    }
    
    private static ArrayList<ship_tree> _getShipTree(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<ship_tree> list = new ArrayList<ship_tree>();
        ship_tree r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from ship_tree where ship_sh = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ship_tree(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            
                    r = new ship_tree(m, res.getString("ship_parent"), res.getString("ship_child"), res.getString("ship_site"), 
                    res.getString("ship_type"), res.getString("ship_sh"), res.getString("ship_shline"), res.getString("ship_so"), res.getString("ship_soline"),
                    res.getString("ship_po"), res.getString("ship_item"), res.getDouble("ship_qty"),
                    res.getString("ship_serial") );
                    list.add(r); 
                    }
            }
            return list;
    }
    
    private static ArrayList<shs_det> _getShipshs(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<shs_det> list = new ArrayList<shs_det>();
        shs_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from shs_det where shs_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new shs_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new shs_det(m, res.getString("shs_nbr"), res.getString("shs_so"), res.getString("shs_desc"), 
                    res.getString("shs_type"), res.getString("shs_amttype"), res.getString("shs_amt") );
                    list.add(r); 
                    }
            }
            return list;
    }
    
    private static ArrayList<sh_meta> _getShipMeta(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<sh_meta> list = new ArrayList<sh_meta>();
        sh_meta r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from sh_meta where shm_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new sh_meta(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new sh_meta(m, res.getString("shm_id"), res.getString("shm_type"), res.getString("shm_key"), 
                    res.getString("shm_value") );
                    list.add(r); 
                    }
            }
            return list;
    }
    
    
    public static ship_mstr createShipMstrJRT(String nbr, String site, String bol, String billto, String shipto, String so, String po, String ref, String shipdate, String orddate, String remarks, String shipvia, String shiptype, String taxcode, String shipfrom, String tracking) {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        cm_mstr cm = cusData.getCustMstr(new String[]{billto});
        String acct = cm.cm_ar_acct();
        String cc = cm.cm_ar_cc();
        String terms = cm.cm_terms();
        String onhold = cm.cm_onhold();
     //   if (taxcode == null || taxcode.isEmpty()) {
     //   taxcode = cm.cm_tax_code();
     //   }
        String curr = cm.cm_curr();
        // logic for asset type shipment/sale
        if (shiptype.equals("A")) {
            terms = "N00";
        }
       
        // override cust currency with order currency
        String order_curr = ordData.getOrderCurrency(so);
        if (! order_curr.isEmpty()) {
        curr = order_curr;
        }
        ship_mstr x = new ship_mstr(null, 
                nbr,
                billto,
                shipto,
                0, // pallets
                0,  // boxes
                shipvia,  
                shipdate,
                orddate,
                ref,
                po,
                remarks,
                bsmf.MainFrame.userid,
                site,
                curr,
                "", // warehouse
                terms,
                taxcode,
                acct,
                cc,
                shiptype,
                so,
                shipfrom,
                tracking, // trailer/tracking
                "", // status
                "", // char1
                "1", // char2  ...auto generated shippers should be 'complete' for ASN production
                "" // char3
            );
                
        return x;        
    }
    
    public static ArrayList<ship_det> createShipDetJRT(ArrayList<String[]> detail, String shippernbr, String shipdate, String site) {
        ArrayList<ship_det> list = new ArrayList<ship_det>();
        for (String[] d : detail) {            
            // field order:  Line, Part, CustPart, SO, PO, Qty, UOM, ListPrice, Discount, NetPrice,QtyShip,Status,WH, LOC, Desc, Taxamt,cont, ref, serial, site, bom
            // service order field order:  line, item, type, desc, order, qty, price, uom
            ship_det x = new ship_det(null,
                  shippernbr,
                  bsParseInt(d[0]), //shline
                  d[1], //item
                  d[2], //custitem
                  d[3], // so
                  bsParseInt(d[0]), // soline = shline  
                  shipdate, //shipdate
                  d[4], // po
                  bsParseDouble(d[5]), // qty
                  d[6], // uom
                  "", // currency  
                  bsParseDouble(d[9]), // netprice
                  bsParseDouble(d[8]), // disc
                  bsParseDouble(d[7]), // listprice
                  d[14], // desc
                  d[12], // wh
                  d[13], // loc
                  bsParseDouble(d[15]), // taxamt
                  "", // cont
                  "", // ref
                  "", // serial
                  site,
                  d[16], // bom
                  0, // packqty
                  "" // shd_kvpair
                  );
          list.add(x);
        }
        return list;
    }
    
    public static ArrayList<ship_det> createShipDetJRTmin(ArrayList<String[]> detail, String shippernbr, String shipdate, String site) {
        ArrayList<ship_det> list = new ArrayList<ship_det>();
        for (String[] d : detail) {            
              // service order field order:  line, item, type, desc, order, qty, price, uom
            ship_det x = new ship_det(null,
                  shippernbr,
                  bsParseInt(d[0]), //shline
                  d[1], //item
                  "", //custitem
                  d[4], // so
                  bsParseInt(d[0]), // soline = shline  
                  shipdate, //shipdate
                  d[4], // po
                  bsParseDouble(d[5]), // qty
                  d[7], // uom
                  "", // currency  
                  bsParseDouble(d[6]), // netprice
                  0, // disc
                  bsParseDouble(d[6]), // listprice
                  d[3], // desc
                  "", // wh
                  "", // loc
                  0, // taxamt
                  "", // cont
                  "", // ref
                  "", // serial
                  site,
                  "", // bom
                  0, // packqty
                  "" // shd_kvpair  
                  );
          list.add(x);
        }
        return list;
    }
    
    public static ArrayList<ship_det> createShipDetFreight(ArrayList<String[]> detail, String shippernbr, String confdate, String site) {
        ArrayList<ship_det> list = new ArrayList<ship_det>();
        for (String[] d : detail) {   
            
            // Freight field order:  "Line", "Item", "FO", "CUSTFO", "NetPrice", "TAXAMT", "desc", "sku" (rate type)
            ship_det x = new ship_det(null,
                  shippernbr,
                  bsParseInt(d[0]), // shline
                  d[1], //item
                  d[7], // custitem, sku, freight rate type
                  d[2], // fo
                  bsParseInt(d[0]), // foline = shline  
                  confdate, //confdate
                  d[3], // po
                  1, // qty
                  "EA", // uom
                  "", // currency
                  bsParseDouble(d[4]), // netprice
                  0, // disc
                  bsParseDouble(d[4]),
                  d[6], // desc
                  "", // wh
                  "", // loc
                  bsParseDouble(d[5]), // taxamt
                  "", // cont
                  "", // ref
                  "", // serial
                  site,
                  "", // bom
                  0, // packqty
                  "" // shd_kvpair  
                  );
          list.add(x);
        }
        return list;
    }
    
    
    public static String[] addUpdateSHCtrl(ship_ctrl x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateSHCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServSHP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  ship_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into ship_ctrl (shc_confirm, shc_custitemonly) "
                        + " values (?,?); "; 
        String sqlUpdate = "update ship_ctrl set shc_confirm = ?, shc_custitemonly = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.shc_confirm);
            psi.setString(2, x.shc_custitemonly);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.shc_confirm);
            psu.setString(2, x.shc_custitemonly);
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
   
    public static ship_ctrl getSHCtrl(String[] x) {
        ship_ctrl r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSHCtrl"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServSHP");
                r = objectMapper.readValue(returnstring, ship_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from ship_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ship_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ship_ctrl(m, 
                                res.getString("shc_confirm"),
                                res.getString("shc_custitemonly")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ship_ctrl(m);
        }
        return r;
    }
    
    
    
    // misc functions
    public static String getShpRptPickerData(String[] keys) {
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
                if (keys[0].equals("shippersByShipDateRange")) {
                res = st.executeQuery("SELECT sh_id, sh_cust, cm_name, " +
                        " sh_shipdate, sh_type, sh_site, sh_po, sh_so, sh_curr, sh_status, " +
                        " sum(shd_qty * shd_netprice) as amt FROM  ship_mstr " +
                        " inner join ship_det " +
                        " on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where sh_shipdate >= " + "'" + keys[1] + "'" +
                        " and sh_shipdate <= " + "'" + keys[2] + "'" +
                        " group by sh_id, sh_cust, cm_name, sh_shipdate, sh_type, sh_site, sh_po, sh_so, sh_curr, sh_status " +
                        " order by sh_id;");               
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("sh_id"));
                            rowArray.put(res.getString("sh_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("sh_shipdate"));
                            rowArray.put(res.getString("sh_type"));
                            rowArray.put(res.getString("sh_site"));
                            rowArray.put(res.getString("sh_po"));
                            rowArray.put(res.getString("sh_so"));
                            rowArray.put(res.getString("sh_curr"));
                            rowArray.put(currformat(res.getString("amt")));
                            rowArray.put(res.getString("sh_status"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("shippersByShipNbrRange")) {
                res = st.executeQuery("SELECT sh_id, sh_cust, cm_name, " +
                        " sh_shipdate, sh_type, sh_site, sh_po, sh_so, sh_curr, sh_status, " +
                        " sum(shd_qty * shd_netprice) as amt FROM  ship_mstr " +
                        " inner join ship_det " +
                        " on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where sh_id >= " + "'" + keys[1] + "'" +
                        " and sh_id <= " + "'" + keys[2] + "'" +
                        " group by sh_id, sh_cust, cm_name, sh_shipdate, sh_type, sh_site, sh_po, sh_so, sh_curr, sh_status " +
                         " order by sh_id;");              
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("sh_id"));
                            rowArray.put(res.getString("sh_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("sh_shipdate"));
                            rowArray.put(res.getString("sh_type"));
                            rowArray.put(res.getString("sh_site"));
                            rowArray.put(res.getString("sh_po"));
                            rowArray.put(res.getString("sh_so"));
                            rowArray.put(res.getString("sh_curr"));
                            rowArray.put(currformat(res.getString("amt")));
                            rowArray.put(res.getString("sh_status"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("shippersByCustDateRange")) {
                res = st.executeQuery("SELECT sh_id, sh_cust, cm_name, " +
                        " sh_shipdate, sh_type, sh_site, sh_po, sh_so, sh_curr, sh_status, " +
                        " sum(shd_qty * shd_netprice) as amt FROM  ship_mstr " +
                        " inner join ship_det " +
                        " on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where sh_shipdate >= " + "'" + keys[1] + "'" +
                        " and sh_shipdate <= " + "'" + keys[2] + "'" +
                        " and sh_cust >= " + "'" + keys[3] + "'" +
                        " and sh_cust <= " + "'" + keys[4] + "'" +
                        " group by sh_id, sh_cust, cm_name, sh_shipdate, sh_type, sh_site, sh_po, sh_so, sh_curr, sh_status " +
                        " order by sh_id;");             
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("sh_id"));
                            rowArray.put(res.getString("sh_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("sh_shipdate"));
                            rowArray.put(res.getString("sh_type"));
                            rowArray.put(res.getString("sh_site"));
                            rowArray.put(res.getString("sh_po"));
                            rowArray.put(res.getString("sh_so"));
                            rowArray.put(res.getString("sh_curr"));
                            rowArray.put(currformat(res.getString("amt")));
                            rowArray.put(res.getString("sh_status"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("shipperDetailByCustDateRange")) {
                res = st.executeQuery("SELECT sh_id, sh_cust, cm_name, " +
                        " sh_shipdate, sh_type, sh_site, sh_po, " +
                        " shd_item, shd_qty, shd_netprice from ship_mstr " +
                        " inner join ship_det " +
                        " on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where sh_shipdate >= " + "'" + keys[1] + "'" +
                        " and sh_shipdate <= " + "'" + keys[2] + "'" +
                        " and sh_cust >= " + "'" + keys[3] + "'" +
                        " and sh_cust <= " + "'" + keys[4] + "'" +
                        " order by sh_id;");  
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("sh_id"));
                            rowArray.put(res.getString("sh_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("sh_shipdate"));
                            rowArray.put(res.getString("sh_po"));
                            rowArray.put(res.getString("shd_item"));
                            rowArray.put(res.getString("shd_qty"));
                            rowArray.put(currformat(res.getString("shd_netprice")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("shipperDetailByPO")) {
                res = st.executeQuery("SELECT sh_id, sh_cust, cm_name, " +
                        " sh_shipdate, sh_type, sh_site, shd_po, " +
                        " shd_item, shd_qty, shd_netprice from ship_mstr " +
                        " inner join ship_det " +
                        " on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where shd_po >= " + "'" + keys[1] + "'" +
                        " and shd_po <= " + "'" + keys[2] + "'" +
                        " order by sh_id;");  
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("sh_id"));
                            rowArray.put(res.getString("sh_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("sh_shipdate"));
                            rowArray.put(res.getString("sh_po"));
                            rowArray.put(res.getString("shd_item"));
                            rowArray.put(res.getString("shd_qty"));
                            rowArray.put(currformat(res.getString("shd_netprice")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("shipperByRawSerial")) {
                res = st.executeQuery("SELECT sh_id, sh_cust, cm_name, " +
                        " sh_shipdate, sh_type, sh_site, shd_po, " +
                        " shd_item, shd_qty, shd_netprice from ship_mstr " +
                        " inner join ship_det " +
                        " on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where shd_id in (select tr_nbr from tran_mstr " +
                        " where tr_serial in (select tr_lot from tran_mstr where tr_serial = " + "'" + keys[1] + "'" + 
                        " and tr_type <> 'RCT-PURCH') and tr_type = 'ISS-SALES') " +
                        " order by sh_id;");
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("sh_id"));
                            rowArray.put(res.getString("sh_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("sh_shipdate"));
                            rowArray.put(res.getString("sh_po"));
                            rowArray.put(res.getString("shd_item"));
                            rowArray.put(res.getString("shd_qty"));
                            rowArray.put(currformat(res.getString("shd_netprice")));
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
     
    public static ArrayList<String[]> getShipperInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShipperInit"});
            list.add(new String[]{"param1", panelClassName});
            list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServSHP"));
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
            
            res = st.executeQuery("select * from gl_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"autopost", res.getString("gl_autopost")});
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
            
            res = st.executeQuery("select it_item from item_mstr where it_type = " + "'" + "CONT" + "'" +
                        " order by it_item ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "items";
               s[1] = res.getString("it_item");
               lines.add(s);
            }
            
            res = st.executeQuery("select so_nbr from so_mstr where so_status = " + "'" + getGlobalProgTag("open") + "'" + 
                    " or so_status = " + "'" + getGlobalProgTag("commit") + "'" + 
                    " or so_status = " + "'" + getGlobalProgTag("backorder") + "'" + " ;");
            
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "orders";
               s[1] = res.getString("so_nbr");
               lines.add(s);
            }
            
            res = st.executeQuery("select sysm_value from sys_meta where " +
                        " sysm_id = 'system' " + " AND " +
                        " sysm_type = 'shippercontrol' AND " +
                        " sysm_key = 'auto_generate_shipper_number' " + 
                        " order by sysm_value;" );
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "autonumber";
               s[1] = res.getString("sysm_value");
               lines.add(s);
            }
            
            res = st.executeQuery("SELECT * FROM  ship_ctrl ;");
            while (res.next()) {
                String[] s = new String[2];
                s[0] = "canconfirm";
                s[1] = res.getString("shc_confirm");
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
    
    public static String getShipperBrowseView(String shipperfrom, String shipperto, String custfrom, String custto, String po, String fromdate, String todate) {
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
                custfrom = (custfrom.isBlank()) ? bsmf.MainFrame.lowchar : custfrom; 
                custto = (custto.isBlank()) ? bsmf.MainFrame.hichar : custto;
                shipperfrom = (shipperfrom.isBlank()) ? bsmf.MainFrame.lowchar : shipperfrom; 
                shipperto = (shipperto.isBlank()) ? bsmf.MainFrame.hichar : shipperto;
                
                if (po.isBlank()) {
                    res = st.executeQuery("select sh_id, sh_status, sh_cust, cm_name, sh_shipdate, sh_po, sum(shd_qty) as 'qty', sum(shd_qty * shd_netprice) as 'price' from ship_mstr " +
                        " inner join ship_det on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where " +
                        " sh_id >= " + "'" + shipperfrom + "'" + " AND " +
                        " sh_id <= " + "'" + shipperto + "'" + " AND " +
                        " sh_shipdate >= " + "'" + fromdate + "'" + " AND " +
                        " sh_shipdate <= " + "'" + todate + "'" + " AND " +
                        " sh_cust >= " + "'" + custfrom + "'" + " AND " +
                        " sh_cust <= " + "'" + custto + "'"  +
                        " group by sh_id, sh_status, sh_cust, cm_name, sh_shipdate, sh_po;");
                  } else {
                    res = st.executeQuery("select sh_id, sh_status, sh_cust, cm_name, sh_shipdate, sh_po, sum(shd_qty) as 'qty', sum(shd_qty * shd_netprice) as 'price' from ship_mstr " +
                        " inner join ship_det on shd_id = sh_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " where " +
                        " sh_id >= " + "'" + shipperfrom + "'" + " AND " +
                        " sh_id <= " + "'" + shipperto + "'" + " AND " +
                        " sh_shipdate >= " + "'" + fromdate + "'" + " AND " +
                        " sh_shipdate <= " + "'" + todate + "'" + " AND " +
                        " sh_cust >= " + "'" + custfrom + "'" + " AND " +
                        " sh_cust <= " + "'" + custto + "'"  + " AND " +
                        " sh_po like '%" + po + "%'" +
                        " group by sh_id, sh_status, sh_cust, cm_name, sh_shipdate, sh_po;");
                  }
                
                    
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("detail");
                        rowArray.put(res.getString("sh_id"));
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("sh_shipdate"));
                        rowArray.put(res.getString("sh_po"));
                        rowArray.put(res.getString("sh_status"));
                        rowArray.put(res.getDouble("qty"));
                        rowArray.put(res.getDouble("price"));
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
    
    public static String getShipperBrowseDetail(String shipper) {
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
                res = st.executeQuery("select shd_id, shd_soline, shd_item, shd_custitem, shd_so, shd_po, shd_qty, shd_netprice from ship_det " +
                        " where shd_id = " + "'" + shipper + "'"  +
                                ";");
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("shd_id"));
                        rowArray.put(res.getString("shd_item"));
                        rowArray.put(res.getString("shd_custitem"));
                        rowArray.put(res.getString("shd_so"));
                        rowArray.put(res.getString("shd_soline"));
                        rowArray.put(res.getString("shd_po"));
                        rowArray.put(res.getString("shd_qty"));
                        rowArray.put(res.getString("shd_netprice"));
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
    
    public static String getShipperDetBrowseView(String[] keys) {
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
                keys[0] = (keys[0].isBlank()) ? bsmf.MainFrame.lowchar : keys[0]; 
                keys[1] = (keys[1].isBlank()) ? bsmf.MainFrame.hichar : keys[1];
                keys[2] = (keys[2].isBlank()) ? bsmf.MainFrame.lowchar : keys[2];
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.hichar : keys[3];
                
                
                if (keys[4].equals("1")) {
                    res = st.executeQuery("select sh_id, sh_cust, shd_item, shd_po, sh_shipdate, shd_qty, shd_netprice from ship_mstr " +
                        " inner join ship_det on shd_id = sh_id where " +
                        " sh_id >= " + "'" + keys[0] + "'" + " AND " +
                        " sh_id <= " + "'" + keys[1] + "'" + " AND " +
                        " sh_cust >= " + "'" + keys[2] + "'" + " AND " +
                        " sh_cust <= " + "'" + keys[3] + "'" + " AND " +
                        " sh_status = '1' " +
                        " ;");
                  } else {
                    res = st.executeQuery("select sh_id, sh_cust, shd_item, shd_po, sh_shipdate, shd_qty, shd_netprice from ship_mstr " +
                        " inner join ship_det on shd_id = sh_id where " +
                        " sh_id >= " + "'" + keys[0] + "'" + " AND " +
                        " sh_id <= " + "'" + keys[1] + "'" + " AND " +
                        " sh_cust >= " + "'" + keys[2] + "'" + " AND " +
                        " sh_cust <= " + "'" + keys[3] + "'" + 
                        " ;");
                  }
                
                    double sales = 0.00;
                    while (res.next()) {
                        sales = (res.getDouble("shd_qty") * res.getDouble("shd_netprice"));
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("sh_id"));
                        rowArray.put(res.getString("shd_po"));
                        rowArray.put(res.getString("sh_shipdate"));
                        rowArray.put(res.getString("shd_item"));
                        rowArray.put(res.getString("shd_qty")); 
                        rowArray.put(res.getString("shd_netprice"));
                        rowArray.put(currformatDouble(sales));
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
    
    public static String getShipperItemBrowseView(String[] keys) {
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
                keys[0] = (keys[0].isBlank()) ? bsmf.MainFrame.lowchar : keys[0]; 
                keys[1] = (keys[1].isBlank()) ? bsmf.MainFrame.hichar : keys[1];
                keys[2] = (keys[2].isBlank()) ? bsmf.MainFrame.lowchar : keys[2];
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.hichar : keys[3];
                keys[4] = (keys[4].isBlank()) ? bsmf.MainFrame.lowchar : keys[4];
                keys[5] = (keys[5].isBlank()) ? bsmf.MainFrame.hichar : keys[5];
                
                
                res = st.executeQuery("select shd_item, shd_so, shd_date, shd_qty, shd_netprice, shd_serial, sh_cust, shd_id from ship_det " +
                        " inner join ship_mstr on sh_id = shd_id where " +
                        " shd_id >= " + "'" + keys[0] + "'" + " AND " +
                        " shd_id <= " + "'" + keys[1] + "'" + " AND " +
                        " shd_item >= " + "'" + keys[4] + "'" + " AND " +
                        " shd_item <= " + "'" + keys[5] + "'" + " AND " +        
                        " shd_date >= " + "'" + keys[6] + "'" + " AND " +
                        " shd_date <= " + "'" + keys[7] + "'" + " AND " +
                        " sh_cust >= " + "'" + keys[2] + "'" + " AND " +
                        " sh_cust <= " + "'" + keys[3] + "'" + 
                        " order by shd_id desc;");
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("shd_id"));
                        rowArray.put(res.getString("shd_so"));
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("shd_date"));
                        rowArray.put(res.getString("shd_item"));
                        rowArray.put(res.getString("shd_serial"));
                        rowArray.put(res.getString("shd_qty")); 
                        rowArray.put(res.getString("shd_netprice"));
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
    
    
    public static String getShipperPrintData(String key, String keytype) {
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
                if (keytype.equals("order")) {
                res = st.executeQuery("select shd_id, it_desc, sh_cust, sh_cust, sh_rmks, shd_po, " +
                        " shd_item, shd_custitem, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, " +
                        " cms_name, cms_line1, site_desc, site_line1, sh_boxes, sh_pallets, sh_shipvia, " +
                        " cm_terms, sh_ref, sh_bol, shd_serial, shd_cont, sh_trailer, " +
                        " cm_city, cm_state, cm_zip, cm_country, cms_city, cms_state, cms_zip, cms_country, " +
                        " site_city, site_state, site_zip, site_country, site_site, " +
                        " cm_logo, site_logo, ov_image_directory, cm_ps_jasper, site_sh_jasper, ov_jasper_directory " +
                        " from ship_det " +
                        " left outer join item_mstr on it_item = shd_item " + 
                        " inner join ship_mstr on sh_id = shd_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " inner join ov_ctrl " +
                        " where shd_so = " + "'" + key + "'"  +
                                ";");
                } else {
                  res = st.executeQuery("select shd_id, it_desc, sh_cust, sh_cust, sh_rmks, shd_po, " +
                        " shd_item, shd_custitem, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, " +
                        " cms_name, cms_line1, site_desc, site_line1, sh_boxes, sh_pallets, sh_shipvia, " +
                        " cm_terms, sh_ref, sh_bol, shd_serial, shd_cont, sh_trailer, " +
                        " cm_city, cm_state, cm_zip, cm_country, cms_city, cms_state, cms_zip, cms_country, " +
                        " site_city, site_state, site_zip, site_country, site_site, " +
                        " cm_logo, site_logo, ov_image_directory, cm_ps_jasper, site_sh_jasper, ov_jasper_directory " +
                        " from ship_det " +
                        " left outer join item_mstr on it_item = shd_item " + 
                        " inner join ship_mstr on sh_id = shd_id " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " inner join ov_ctrl " +
                        " where shd_id = " + "'" + key + "'"  +
                                ";");  
                }
                    
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("shd_id").toUpperCase());
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("sh_rmks"));
                        rowArray.put(res.getString("shd_po"));
                        rowArray.put(res.getString("shd_item"));
                        rowArray.put(res.getString("shd_custitem"));
                        rowArray.put(res.getInt("shd_qty"));
                        rowArray.put(res.getString("shd_netprice")); 
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_name")); // 10 zero base
                        rowArray.put(res.getString("cm_line1"));
                        rowArray.put(res.getString("cm_line2"));
                        rowArray.put(res.getString("cms_name"));
                        rowArray.put(res.getString("cms_line1"));
                        rowArray.put(res.getString("site_desc"));
                        rowArray.put(res.getString("site_line1"));
                        rowArray.put(res.getString("sh_boxes"));
                        rowArray.put(res.getString("sh_pallets"));
                        rowArray.put(res.getString("sh_shipvia"));
                        rowArray.put(res.getString("cm_terms")); // 20 zero base
                        rowArray.put(res.getString("sh_ref"));
                        rowArray.put(res.getString("sh_bol"));
                        rowArray.put(res.getString("shd_serial"));
                        rowArray.put(res.getString("shd_cont"));
                        rowArray.put(res.getString("sh_trailer"));
                        rowArray.put(res.getString("cm_city"));
                        rowArray.put(res.getString("cm_state"));
                        rowArray.put(res.getString("cm_zip"));
                        rowArray.put(res.getString("cm_country"));
                        rowArray.put(res.getString("cms_city"));  // 30 zero base
                        rowArray.put(res.getString("cms_state"));
                        rowArray.put(res.getString("cms_zip"));
                        rowArray.put(res.getString("cms_country"));
                        rowArray.put(res.getString("site_city"));
                        rowArray.put(res.getString("site_state"));
                        rowArray.put(res.getString("site_zip"));
                        rowArray.put(res.getString("site_country"));
                        rowArray.put(res.getString("site_site"));
                        rowArray.put(res.getString("cm_logo"));
                        rowArray.put(res.getString("site_logo")); // 40 zero base
                        rowArray.put(res.getString("ov_image_directory"));
                        rowArray.put(res.getString("cm_ps_jasper"));
                        rowArray.put(res.getString("site_sh_jasper"));
                        rowArray.put(res.getString("ov_jasper_directory"));
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
    
    public static String getInvoicePrintData(String key, String keytype) {
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
                if (keytype.equals("order")) {
                res = st.executeQuery("select " +
                        " (select case when sum(shs_amt) is null then 0 else sum(shs_amt) end from shs_det " +
                        " where shs_nbr = shd_id and shs_amttype = 'amount' and shs_type <> 'tax' and shs_type <> 'passive' " +
                        " and shs_type <> 'shipping Bil' and shs_type <> 'shipping PPD' ) as charges, " +
                        " (select case when sum(shs_amt) is null then 0 else sum(shs_amt) end from shs_det " +
                        " where shs_nbr = shd_id and shs_amttype = 'amount' and shs_type = 'tax' ) as taxes, " +
                        " shd_id, it_desc, sh_cust, sh_cust, sh_rmks, shd_po, " +
                        " shd_item, shd_custitem, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, " +
                        " cms_name, cms_line1, site_desc, site_line1, sh_boxes, sh_pallets, sh_shipvia, " +
                        " cm_terms, sh_ref, sh_bol, shd_serial, shd_cont, sh_trailer, " +
                        " cm_city, cm_state, cm_zip, cm_country, cms_city, cms_state, cms_zip, cms_country, " +
                        " site_city, site_state, site_zip, site_country, site_site, " +
                        " cm_logo, site_logo, ov_image_directory, cm_iv_jasper, site_iv_jasper, ov_jasper_directory, " +
                        " sh_type, ifNull(cfod_date,'') as cfod_date, ifNull(cfo_mileage, '0') as cfo_mileage, ifNull(cfo_weight, '0') as cfo_weight, sh_so, sh_curr, " +
                        " shd_taxamt, shd_taxpercent, shd_uom, sh_confdate, ar_duedate, shd_listprice, cms_line2, shd_desc, it_comments, it_servicetype " +
                        " from ship_det " +
                        " left outer join item_mstr on it_item = shd_item " + 
                        " inner join ship_mstr on sh_id = shd_id " +
                        " inner join ar_mstr on ar_nbr = sh_id " + 
                        " left outer join cfo_det on cfod_nbr = sh_so and cfod_type = 'Unload Complete' and sh_type = 'F' " +
                        " left outer join cfo_mstr on cfo_nbr = sh_so and sh_type = 'F' " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " inner join ov_ctrl " +
                        " where shd_so = " + "'" + key + "'"  +
                                ";");
                } else {
                    res = st.executeQuery("select " +
                        " (select case when sum(shs_amt) is null then 0 else sum(shs_amt) end from shs_det " +
                        " where shs_nbr = shd_id and shs_amttype = 'amount' and shs_type <> 'tax' and shs_type <> 'passive' " +
                        " and shs_type <> 'shipping Bil' and shs_type <> 'shipping PPD' ) as charges, " +
                        " (select case when sum(shs_amt) is null then 0 else sum(shs_amt) end from shs_det " +
                        " where shs_nbr = shd_id and shs_amttype = 'amount' and shs_type = 'tax' ) as taxes, " +
                        " shd_id, it_desc, sh_cust, sh_cust, sh_rmks, shd_po, " +
                        " shd_item, shd_custitem, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, " +
                        " cms_name, cms_line1, site_desc, site_line1, sh_boxes, sh_pallets, sh_shipvia, " +
                        " cm_terms, sh_ref, sh_bol, shd_serial, shd_cont, sh_trailer, " +
                        " cm_city, cm_state, cm_zip, cm_country, cms_city, cms_state, cms_zip, cms_country, " +
                        " site_city, site_state, site_zip, site_country, site_site, " +
                        " cm_logo, site_logo, ov_image_directory, cm_iv_jasper, site_iv_jasper, ov_jasper_directory, " +
                        " sh_type, ifNull(cfod_date,'') as cfod_date, ifNull(cfo_mileage, '0') as cfo_mileage, ifNull(cfo_weight, '0') as cfo_weight, sh_so, sh_curr, " +
                        " shd_taxamt, shd_taxpercent, shd_uom, sh_confdate, ar_duedate, shd_listprice, cms_line2, shd_desc, it_comments, it_servicetype " +
                        " from ship_det " +
                        " left outer join item_mstr on it_item = shd_item " + 
                        " inner join ship_mstr on sh_id = shd_id " +
                        " inner join ar_mstr on ar_nbr = sh_id " +    
                        " left outer join cfo_det on cfod_nbr = sh_so and cfod_type = 'Unload Complete' and sh_type = 'F' " +
                        " left outer join cfo_mstr on cfo_nbr = sh_so and sh_type = 'F' " +    
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " left outer join cms_det on cms_code = sh_cust and cms_shipto = sh_ship " +
                        " inner join site_mstr on site_site = sh_site " +
                        " inner join ov_ctrl " +
                        " where shd_id = " + "'" + key + "'"  +
                                ";");
                }
                    
                    String shipper = "";
                    String cust = "";
                    int i = 0;
                    while (res.next()) {
                        if (i == 0) {
                         shipper = res.getString("shd_id");
                         cust = res.getString("sh_cust");
                        }
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("shd_id").toUpperCase()); 
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("sh_rmks"));
                        rowArray.put(res.getString("shd_po"));
                        rowArray.put(res.getString("shd_item"));
                        rowArray.put(res.getString("shd_custitem"));
                        rowArray.put(res.getString("shd_qty")); 
                        rowArray.put(res.getDouble("shd_netprice")); 
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_name")); // 10 zero base
                        rowArray.put(res.getString("cm_line1"));
                        rowArray.put(res.getString("cm_line2"));
                        rowArray.put(res.getString("cms_name"));
                        rowArray.put(res.getString("cms_line1"));
                        rowArray.put(res.getString("site_desc"));
                        rowArray.put(res.getString("site_line1"));
                        rowArray.put(res.getString("sh_boxes"));
                        rowArray.put(res.getString("sh_pallets"));
                        rowArray.put(res.getString("sh_shipvia"));
                        rowArray.put(res.getString("cm_terms")); // 20 zero base
                        rowArray.put(res.getString("sh_ref"));
                        rowArray.put(res.getString("sh_bol"));
                        rowArray.put(res.getString("shd_serial"));
                        rowArray.put(res.getString("shd_cont"));
                        rowArray.put(res.getString("sh_trailer"));
                        rowArray.put(res.getString("cm_city"));
                        rowArray.put(res.getString("cm_state"));
                        rowArray.put(res.getString("cm_zip"));
                        rowArray.put(res.getString("cm_country"));
                        rowArray.put(res.getString("cms_city"));  // 30 zero base
                        rowArray.put(res.getString("cms_state"));
                        rowArray.put(res.getString("cms_zip"));
                        rowArray.put(res.getString("cms_country"));
                        rowArray.put(res.getString("site_city"));
                        rowArray.put(res.getString("site_state"));
                        rowArray.put(res.getString("site_zip"));
                        rowArray.put(res.getString("site_country"));
                        rowArray.put(res.getString("site_site"));
                        rowArray.put(res.getString("cm_logo"));
                        rowArray.put(res.getString("site_logo")); // 40 zero base
                        rowArray.put(res.getString("ov_image_directory"));
                        rowArray.put(res.getString("cm_iv_jasper"));
                        rowArray.put(res.getString("site_iv_jasper"));
                        rowArray.put(res.getString("ov_jasper_directory"));
                        rowArray.put(res.getString("sh_type"));
                        rowArray.put(res.getString("cfod_date"));
                        rowArray.put(res.getString("cfo_weight"));
                        rowArray.put(res.getString("cfo_mileage"));
                        rowArray.put(res.getString("sh_so"));
                        rowArray.put(res.getString("sh_curr")); // 50 zero base
                        rowArray.put(res.getDouble("shd_taxamt"));
                        rowArray.put(res.getDouble("shd_taxpercent"));
                        rowArray.put(res.getString("shd_uom"));
                        rowArray.put(res.getString("sh_confdate"));
                        rowArray.put(res.getString("ar_duedate"));
                        rowArray.put(res.getDouble("charges"));
                        rowArray.put(res.getDouble("taxes"));
                        rowArray.put(res.getDouble("shd_listprice"));
                        rowArray.put(res.getString("cms_line2"));
                        rowArray.put(res.getString("shd_desc"));
                        rowArray.put(res.getString("it_comments"));
                        rowArray.put(res.getString("it_servicetype"));
                        jsonarray.put(rowArray);
                        i++;
                    }
                
              // get SAC
              if (i > 0) {
              res = st.executeQuery("select shs_desc, " +
                      " case when shs_amttype = 'percent' and shs_type <> 'tax' then (myamt * -1 * (shs_amt / 100.0)) " +
                      " when shs_amttype = 'percent' and shs_type = 'tax' then (myamt * (shs_amt / 100.0)) " +
                      " else shs_amt end as 'amt' " +
                      " from shs_det, (select shd_id, sum(shd_qty * shd_listprice) as 'myamt' from ship_det group by shd_id) sub " +
                      " where sub.shd_id = shs_nbr and shs_nbr = " + "'" + shipper + "'");
              while (res.next()) {
                  JSONArray rowArray = new JSONArray(); 
                        rowArray.put("sacarray");
                        rowArray.put(res.getString("shs_desc")); 
                        rowArray.put(res.getString("amt"));
                        jsonarray.put(rowArray);
              }
              
              
              // get pick ticket notes    
              res = st.executeQuery("select txt_value from txt_meta where txt_type = 'invoiceprint' and txt_id = 'custkey' and " +
                      " txt_key = " + "'" + cust + "'" );    
              
              if (! res.isBeforeFirst()) { 
                  res = st.executeQuery("select txt_value from txt_meta where txt_type = 'invoiceprint' and txt_id = 'syskey' ; " );                   
                  while (res.next()) {
                  String[] notes = res.getString("txt_value").split("\\n", -1);
                  for (String note : notes) {
                  JSONArray rowArray = new JSONArray(); 
                        rowArray.put("notesarray");
                        rowArray.put(note); 
                        jsonarray.put(rowArray);
                  }
                 } 
                  
              } else {
                 while (res.next()) {
                  String[] notes = res.getString("txt_value").split("\\n", -1);
                  for (String note : notes) {
                  JSONArray rowArray = new JSONArray(); 
                        rowArray.put("notesarray");
                        rowArray.put(note); 
                        jsonarray.put(rowArray);
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
       return jsonarray.toString(); 
    }
    
    
    public static void _updateShipperStatus(String shipper, Date effdate, Connection bscon) throws SQLException {
        Statement st = bscon.createStatement();
        st.executeUpdate(
             " update ship_mstr set sh_status = '1', sh_confdate = " + "'" + setDateDB(effdate) + "'" +
             " where sh_id = " + "'" + shipper + "'" + ";" );
        st.close();
   }

    public static void _updateInventoryFromShipper(String shipper, Connection bscon) throws SQLException {
   
            Statement st = bscon.createStatement();
            Statement st2 = bscon.createStatement();
            ResultSet res;

           java.util.Date now = new java.util.Date();
           
           
           
                String item = "";
                double qty = 0;
                String uom = "";
                double baseqty = 0;
                double lineqty = 0;
                String loc = "";
                String wh = "";
                String site = "";
                String serial = "";
                String expire = "";
                double sum = 0;
                boolean serialized = false;
                int i = 0;
                  res = st.executeQuery("select sh_site, shd_item, shd_qty, shd_uom, shd_loc, serialize, " +
                          " shd_wh, shd_site, shd_serial, shd_bom, it_loc, it_wh, it_code, it_phantom, it_uom " +
                          " from ship_det inner join ship_mstr on sh_id = shd_id  " +
                          " inner join inv_ctrl " +
                          " left outer join item_mstr on it_item = shd_item " +
                          " where shd_id = " + "'" + shipper + "'" +";");
                ArrayList<String[]> list = new ArrayList<String[]>();
                while (res.next()) {
                    String[] x = new String[13];
                    i = 0;
                    x[0] = res.getString("shd_item");
                    x[1] = res.getString("shd_qty");
                    x[2] = res.getString("shd_uom");
                    x[3] = res.getString("shd_loc");
                    x[4] = res.getString("shd_wh");
                    x[5] = res.getString("sh_site");
                    x[6] = res.getString("shd_serial");
                    x[7] = res.getString("it_loc");
                    x[8] = res.getString("it_wh");
                    x[9] = res.getString("it_code");
                    x[10] = res.getString("it_phantom");
                    x[11] = res.getString("shd_bom");
                    if (res.getString("it_uom") != null && ! res.getString("shd_uom").toUpperCase().equals(res.getString("it_uom").toUpperCase())) {
                        baseqty = OVData.getUOMBaseQty(x[0], x[5], x[2], bsParseDouble(x[1]));
                    } else {
                        baseqty = bsParseDouble(x[1]);
                    }
                    x[12] = String.valueOf(baseqty);
                 //   if (x[3].isEmpty()) {x[3] = x[7];} // if no loc in shipper...use item default loc
                //    if (x[4].isEmpty()) {x[4] = x[8];} // if no wh in shipper...use item default wh
                    if (x[9] != null && ! x[9].equals("S")) {  // no service items
                     list.add(x);
                    }
                    serialized = res.getBoolean("serialize");
                }
                res.close();
                
                // lets wash out phantoms and add BOM to new ArrayList
                ArrayList<String[]> newlist = new ArrayList<String[]>();
                for (String[] sd : list) {
                    if (sd[10].equals("1")) {  // if phantom...just BOM is added
                        ArrayList<String> bom = OVData.getBOM(sd[0], sd[11]);
                        for (String b : bom) {
                            String[] x = Arrays.copyOf(sd, sd.length);
                            x[0] = b;
                            newlist.add(x);
                        }
                    } else {  // if not phantom....just parent
                        newlist.add(sd);
                    }
                }
                
                for (String[] sd : newlist) {                
                    item = sd[0];
                    uom = sd[2];
                    loc = sd[3];
                    wh = sd[4];
                    site = sd[5];
                    serial = sd[6];
                    lineqty = Double.valueOf(sd[12]);
                 
                    // update InventoryBalance ...independent of serialized or non serialized
                    _updateInventoryBalance(item, site, String.valueOf(LocalDate.now().getYear()), String.valueOf(LocalDate.now().getMonthValue()), (-1 * lineqty), bscon);
                    
                    if (! serialized) {
                        serial = "";
                        expire = "";
                    }
                    int z = 0;
                    double qoh = 0.00;
                  
                    if (! serialized) {  // if not serialized
                    OVData._updateNonSerializedInventory(bscon, item, site, wh, loc, (-1 * lineqty), setDateDB(now));
                   } else if (serialized && ! serial.isEmpty()) {
                    res = st.executeQuery("select in_qoh, in_serial from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"  
                            + " and in_serial = " + "'" + serial + "'"         
                            + ";");
                    ArrayList<String[]> serialinventory = new ArrayList<String[]>();
                    double diff = 0;
                    while (res.next()) {
                      diff = res.getDouble("in_qoh") - lineqty;  // app logic must always insure diff >= 0
                      if (diff <= 0) { 
                          st2.executeUpdate("delete from in_mstr where " 
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"             
                            + ";");
                      } else {
                          st2.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + diff + "'" + "," +
                              " in_date = " + "'" + setDateDB(now) + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"             
                            + ";");
                      }
                        
                    }
                    res.close();   
                   } else { // must be serialized...yet no serial inventory specifically chosen...relieve oldest inventory first by serial / expire
                    res = st.executeQuery("select in_qoh, in_serial from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"  
                            + " order by in_expire asc ;");
                    ArrayList<String[]> serialinventory = new ArrayList<String[]>();
                    while (res.next()) {
                        z++;
                        serialinventory.add(new String[]{res.getString("in_serial"), res.getString("in_qoh")});
                    }
                    res.close();
                    double remaining = lineqty;
                    for (String[] s : serialinventory) {
                        if (remaining == 0) break;
                        if (Double.valueOf(s[1]) <= remaining) {
                            remaining = remaining - Double.valueOf(s[1]);
                            // delete serial in_mstr record
                            st2.executeUpdate("delete from in_mstr where " 
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + s[0] + "'"             
                            + ";");
                        } else {
                            // update serial in_mstr with Double.valueOf(s[1]) - remaining
                            sum = Double.valueOf(s[1]) - remaining;
                            st2.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + sum + "'" + "," +
                              " in_date = " + "'" + setDateDB(now) + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + s[0] + "'"             
                            + ";");
                            remaining = 0;
                            break;
                        }
                    }
                    if (remaining > 0) {
                        // no inventory to remove
                        OVData._updateNonSerializedInventory(bscon, item, site, wh, loc, (-1 * remaining), setDateDB(now));
                    }
                   } //  serialized logic
                } // for each ship_det
     }

    public static void _updateInventoryFromJob(String shipper, Connection bscon) throws SQLException {
   
            Statement st = bscon.createStatement();
            Statement st2 = bscon.createStatement();
            ResultSet res;
            ResultSet res2 = null;

            java.util.Date now = new java.util.Date();
            
            String jobid = getShipperRef(shipper);
                    
                    if (jobid.isBlank()) {
                        return;
                    }
            
                String item = "";
                double qty = 0;
                String uom = "";
                double baseqty = 0;
                double lineqty = 0;
                String loc = "";
                String wh = "";
                String site = "";
                String serial = "";
                String expire = "";
                double sum = 0;
                boolean serialized = false;
                int i = 0;
               
                 ArrayList<String[]> list = new ArrayList<String[]>();
                res = st.executeQuery("select * from plan_opdet " +
                       " left outer join item_mstr on it_item = plod_item " + 
                       " where plod_parent = " + "'" + jobid + "'" +
                       " and ( plod_type = 'material' or plod_type = 'tooling') " +  
                       " and plod_consumable = '1' " +        
                       " order by plod_op ;");
                    while (res.next()) {
                                  
                    String[] x = new String[13];
                    i = 0;
                    x[0] = res.getString("plod_item");
                    x[1] = res.getString("plod_qty");
                    x[2] = res.getString("it_uom");
                    x[3] = res.getString("it_loc");
                    x[4] = res.getString("it_wh");
                    x[5] = res.getString("it_site");
                    x[6] = ""; // serial number
                    x[7] = res.getString("it_loc");
                    x[8] = res.getString("it_wh");
                    x[9] = res.getString("it_code");
                    x[10] = res.getString("it_phantom");
                    x[11] = ""; // bom
                    baseqty = OVData.getUOMBaseQty(x[0], x[5], x[2], bsParseDouble(x[1]));
                    x[12] = String.valueOf(baseqty);
                 //   if (x[3].isEmpty()) {x[3] = x[7];} // if no loc in shipper...use item default loc
                //    if (x[4].isEmpty()) {x[4] = x[8];} // if no wh in shipper...use item default wh
                    if (x[9] != null && ! x[9].equals("S")) {  // no service items
                     list.add(x);
                    }
                  
                }
                res.close();
                
                // lets wash out phantoms and add BOM to new ArrayList
                ArrayList<String[]> newlist = new ArrayList<String[]>();
                for (String[] sd : list) {
                    if (sd[10].equals("1")) {  // if phantom...just BOM is added
                        ArrayList<String> bom = OVData.getBOM(sd[0], sd[11]);
                        for (String b : bom) {
                            String[] x = Arrays.copyOf(sd, sd.length);
                            x[0] = b;
                            newlist.add(x);
                        }
                    } else {  // if not phantom....just parent
                        newlist.add(sd);
                    }
                }
                
                for (String[] sd : newlist) {                
                    item = sd[0];
                    uom = sd[2];
                    loc = sd[3];
                    wh = sd[4];
                    site = sd[5];
                    serial = sd[6];
                    lineqty = Double.valueOf(sd[12]);
                 //   bsmf.MainFrame.show(item + "/" + uom + "/" + loc + "/" + wh + "/" + site + "/" + serial + "/" + baseqty);
                    // if not serialized...pull from non-serialized inventory... in_serial = ""
                    // check for serialized inventory flag...if not...prevent serial from entry into in_mstr
                    
                  // update InventoryBalance ...independent of serialized or non serialized
                    _updateInventoryBalance(item, site, String.valueOf(LocalDate.now().getYear()), String.valueOf(LocalDate.now().getMonthValue()), (-1 * lineqty), bscon);
                      
                    
                    if (! OVData.isInvCtrlSerialize()) {
                        serialized = false;
                        serial = "";
                        expire = "";
                    } else {
                        serialized = true;
                    }
                    int z = 0;
                    double qoh = 0.00;
                  
                    if (! serialized) {  // if not serialized
                    OVData._updateNonSerializedInventory(bscon, item, site, wh, loc, (-1 * lineqty), setDateDB(now));
                   } else if (serialized && ! serial.isEmpty()) {
                    res = st.executeQuery("select in_qoh, in_serial from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"  
                            + " and in_serial = " + "'" + serial + "'"         
                            + ";");
                    ArrayList<String[]> serialinventory = new ArrayList<String[]>();
                    double diff = 0;
                    while (res.next()) {
                      diff = res.getDouble("in_qoh") - lineqty;  // app logic must always insure diff >= 0
                      if (diff <= 0) { 
                          st2.executeUpdate("delete from in_mstr where " 
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"             
                            + ";");
                      } else {
                          st2.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + diff + "'" + "," +
                              " in_date = " + "'" + setDateDB(now) + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"             
                            + ";");
                      }
                        
                    }
                    res.close();   
                   } else { // must be serialized...yet no serial inventory specifically chosen...relieve oldest inventory first by serial / expire
                    res = st.executeQuery("select in_qoh, in_serial from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"  
                            + " order by in_expire asc ;");
                    ArrayList<String[]> serialinventory = new ArrayList<String[]>();
                    while (res.next()) {
                        z++;
                        serialinventory.add(new String[]{res.getString("in_serial"), res.getString("in_qoh")});
                    }
                    res.close();
                    double remaining = lineqty;
                    for (String[] s : serialinventory) {
                        if (remaining == 0) break;
                        if (Double.valueOf(s[1]) <= remaining) {
                            remaining = remaining - Double.valueOf(s[1]);
                            // delete serial in_mstr record
                            st2.executeUpdate("delete from in_mstr where " 
                            + " in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + s[0] + "'"             
                            + ";");
                        } else {
                            // update serial in_mstr with Double.valueOf(s[1]) - remaining
                            sum = Double.valueOf(s[1]) - remaining;
                            st2.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + sum + "'" + "," +
                              " in_date = " + "'" + setDateDB(now) + "'"
                            + " where in_item = " + "'" + item + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + s[0] + "'"             
                            + ";");
                            remaining = 0;
                            break;
                        }
                    }
                    if (remaining > 0) {
                        // no inventory to remove
                        OVData._updateNonSerializedInventory(bscon, item, site, wh, loc, (-1 * remaining), setDateDB(now));
                    }
                   } //  serialized logic
                } // for each ship_det
     }

    public static void _addTranMstrShipper(String shipper, Date effdate, Connection bscon) throws SQLException {
           
        Statement st = bscon.createStatement();
        Statement st2 = bscon.createStatement();
        ResultSet res;
        java.util.Date now = new java.util.Date();
        
                
        String cust = "";
        String ref = "";
        String rmks = "";
        String acct = "";
        String cc = "";
        String type = "";
        String jobnbr = "";
        String serial = "";
        String part = "";
        String uom = "";
        double qty = 0;
        double baseqty = 0;
        double price = 0.00;
        double cost = 0.00;
        String loc = "";
        int line = 0;
        String order = "";
        String po = "";
        String site = "";
        String lot = "";
        String terms = "";


        res = st.executeQuery("select * from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
        while (res.next()) {
         cust = res.getString("sh_cust");
         ref = res.getString("sh_ref");
         rmks = res.getString("sh_rmks");
         acct = res.getString("sh_ar_acct");
         cc = res.getString("sh_ar_cc");
         site = res.getString("sh_site");
         terms = res.getString("sh_cust_terms");
         type = "ISS-SALES";
        }

        res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
        while (res.next()) {
            part = res.getString("shd_item");
            uom = res.getString("shd_uom");
            qty = res.getDouble("shd_qty");
            order = res.getString("shd_so");
            po = res.getString("shd_po");
            line = res.getInt("shd_soline");
            lot = res.getString("shd_lot");
            loc = res.getString("shd_loc");
            jobnbr = res.getString("shd_jobnbr");
            serial = res.getString("shd_serial");
            baseqty = OVData.getUOMBaseQty(part, site, uom, qty);

    st2.executeUpdate("insert into tran_mstr "
                    + "(tr_site, tr_item, tr_qty, tr_base_qty, tr_uom, tr_ent_date, tr_eff_date, "
                    + " tr_userid, tr_ref, tr_addrcode, tr_type, tr_rmks, tr_nbr, "
                    + " tr_acct, tr_cc, tr_lot, tr_serial, tr_program, tr_loc, "
                    + " tr_order, tr_line, tr_po, tr_price, tr_cost, tr_terms ) "
                    + " values ( " 
                    + "'" + site + "'" + ","
                    + "'" + part + "'" + ","
                    + "'" + (-1 * qty) + "'" + ","
                    + "'" + (-1 * baseqty) + "'" + ","
                    + "'" + uom + "'" + ","        
                    + "'" + setDateDB(now) + "'" + ","
                    + "'" + setDateDB(effdate) + "'" + ","
                    + "'" + bsmf.MainFrame.userid + "'" + ","
                    + "'" + ref + "'" + ","
                    + "'" + cust + "'" + ","
                    + "'" + type + "'" + ","
                    + "'" + rmks + "'" + ","
                    + "'" + shipper + "'" + ","
                    + "'" + acct + "'" + ","
                    + "'" + cc + "'" + ","
                    + "'" + lot + "'" + ","
                    + "'" + serial + "'" + ","
                    + "'" + "shconf" + "'" + ","
                    + "'" + loc + "'" + ","
                    + "'" + order + "'" + ","
                    + "'" + line + "'" + ","
                    + "'" + po + "'" + ","
                    + "'" + price + "'" + ","
                    + "'" + cost + "'" + ","
                    + "'" + terms + "'"
                    + ")"
                    + ";");
        }
        res.close();
        st.close();
        if (st2 != null) {
        st2.close();
        }            
           
    }
       
    public static void _updateOrderFromShipper(String shipper, Connection bscon) throws SQLException {

        boolean partial = false;
        boolean complete = true;
        ArrayList<String> orders = new ArrayList<String>();
        Set<String> uniqueorders = new HashSet<String>();

       
        Statement st = bscon.createStatement();
        ResultSet res = null;
        
            ArrayList qty = new ArrayList();
            ArrayList shippedqty = new ArrayList();
            ArrayList line = new ArrayList();
            ArrayList ordqty = new ArrayList();
            ArrayList linestatus = new ArrayList();
            ArrayList ordernbr = new ArrayList();

             res = st.executeQuery("select sod_nbr, sod_status, sod_line, shd_item, sum(shd_qty) as sumqty, sod_shipped_qty, sod_ord_qty from ship_det inner join " +
                     " sod_det on shd_item = sod_item and shd_soline = sod_line and shd_so = sod_nbr " +
               " where shd_id = " + "'" + shipper + "'" + 
               " group by shd_item, sod_nbr, sod_status, sod_line, sod_shipped_qty, sod_ord_qty " +                        
               ";");
               while (res.next()) {
                   shippedqty.add(res.getString("sod_shipped_qty"));
                   qty.add(res.getString("sumqty"));
                   ordqty.add(res.getString("sod_ord_qty"));
                   linestatus.add(res.getString("sod_status"));
                   line.add(res.getString("sod_line"));
                   ordernbr.add(res.getString("sod_nbr"));
                }
               res.close();
                              // res = st.executeQuery("select shd_item from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
          if (dbtype.equals("sqlite")) {
              double total = 0;
              String status = "";
              for (int j = 0; j < line.size(); j++) {
                 // bsmf.MainFrame.show(qty.get(j).toString() + " / " + shippedqty.get(j).toString() + " / " + ordqty.get(j).toString());
                  total = bsParseDouble(qty.get(j).toString()) + bsParseDouble(shippedqty.get(j).toString());
                  if (total >= bsParseDouble(ordqty.get(j).toString())) {
                      status = getGlobalProgTag("closed");
                  } else {
                      status = linestatus.get(j).toString();
                  }
                  
                  st.executeUpdate("update sod_det set sod_shipped_qty = " + "'" + total + "'" + ", sod_status = " + "'" + status + "'" + 
                           " where sod_nbr = " + "'" + ordernbr.get(j).toString() + "'" +
                           " and sod_line = " + "'" + line.get(j).toString() + "'" +
                          ";" );
              }
          } else {
              st.executeUpdate(
                     " update sod_det inner join ship_det on shd_item = sod_item and shd_soline = sod_line and shd_so = sod_nbr " +
                     " inner join so_mstr on so_nbr = sod_nbr and so_type = 'DISCRETE' " +
                      " set sod_shipped_qty = sod_shipped_qty + shd_qty, sod_status = " +
                      " (case when sod_shipped_qty + shd_qty >= sod_ord_qty then " + "'" + getGlobalProgTag("closed") + "'" +
                      " else sod_status end) " +
                 " where shd_id = " + "'" + shipper + "'" + ";" );
          }
                // now let's select the unique orders involved in that shipper
               res = st.executeQuery("select sod_nbr from sod_det inner join ship_det on shd_so = sod_nbr " +
               " where shd_id = " + "'" + shipper + "'" +";");
               while (res.next()) {
                   uniqueorders.add(res.getString("sod_nbr"));
                }
               for (String uniqueorder : uniqueorders) {
                   orders.clear();
                    partial = false;
                   complete = true;
                   res = st.executeQuery("select sod_nbr, sod_status from sod_det " +
                           " where sod_nbr = " + "'" + uniqueorder + "'" +";");
                   while (res.next()) {
                       // logic is that a shipper has been committed with at least some portion of this order
                       // therefore if any line items on that order are still open...then the order was shipped partial...
                       //  therefore flag it as backorder
                       if (res.getString("sod_status").equals(getGlobalProgTag("open"))) {
                               partial = true;
                            }
                       if (! res.getString("sod_status").equals(getGlobalProgTag("closed"))) {
                               complete = false;
                            }
                    }


                   if (complete) {
                    st.executeUpdate( "update so_mstr set so_status  = " + "'" + getGlobalProgTag("closed") + "'" + " where so_nbr = " + "'" + uniqueorder + "'" + ";"); 
                   }
                   if (partial && ! complete) {
                   st.executeUpdate( "update so_mstr set so_status = 'backorder' where so_nbr = " + "'" + uniqueorder + "'" + ";");
                   }
                }
        res.close();
        st.close();
   }

    public static void _updateServiceOrderFromShipper(String shipper, Connection bscon) throws SQLException {
        
        Statement st = bscon.createStatement();
        ResultSet res = null;
            String ordernbr = "";
             res = st.executeQuery("select svd_nbr from ship_det inner join " +
                     " svd_det on shd_item = svd_item and shd_soline = svd_line and shd_so = svd_nbr " +
               " where shd_id = " + "'" + shipper + "'" +";");
               while (res.next()) {
                   ordernbr = res.getString("svd_nbr");
                }
               res.close();
               st.executeUpdate( "update sv_mstr set sv_status = " + "'" + getGlobalProgTag("closed") + "'" + " where sv_nbr = " + "'" + ordernbr + "'" + ";"); 
        res.close();
        st.close();
   }

    public static void _processShipperVouchers(String shipper, Date effdate, Connection bscon) throws SQLException {
        // create necessary JRT types for vouchering
       String[] si = getShipperHeader(shipper);
        // get shipper carrier and ship date
        // si[5] = ship date
        // si[8] = shipvia/carrier
        
        // get carrier/vendor apinfo
         // addr, acct, cc, currency, bank, terms, site
        if (si[8] == null || si[8].isBlank()) { // if shipvia is blank
            return;
        }
        String[] v = getVendInfo(si[8]);
        Date duedate = OVData.getDueDateFromTerms(parseDate(si[5]), v[5]);
        String strduedate = setDateDB(effdate); // as default...in case no duedate terms
        if (duedate != null) {
            strduedate = setDateDB(duedate);
        }
        String defaultsalescc = OVData.getDefaultSalesCC(); // sales cc
        String defaultshippingacct = OVData.getDefaultShippingAcct(); // shipping acct 
        
       // int batchid = Integer.valueOf(shipper);
        int voucher = getNextNbr("voucher", bscon);
        
        ArrayList<String[]> sac = shpData.getShipperSAC(shipper);
        // charges = shpData.getShipperTrailerCharges(shipper);
                    for (String[] s : sac) {
                     if (Double.valueOf(s[4]) > 0) {
                        if (s[2].equals("shipping BIL") || s[2].equals("shipping ADD") || s[2].equals("shipping PPD")) {
                        // vouch only
                        
                        // create ap_mstr JRT
                        fapData.ap_mstr x = new fapData.ap_mstr(null, 
                        "", //ap_id
                        si[8], // ap_vend, // shipvia carrier 
                        String.valueOf(voucher), // ap_nbr
                        parseDouble(s[4]), // ap_amt
                        parseDouble(s[4]), // ap_base_amt
                        setDateDB(effdate), // ap_effdate, ship_date
                        setDateDB(effdate), // ap_entdate, ship_date
                        strduedate, // ap_duedate         
                        "V", // ap_type
                        s[2] + "/" + shipper, //ap_rmks
                        shipper, //ap_ref
                        v[5], //ap_terms
                        v[1], //ap_acct
                        v[2], //ap_cc
                        "0", //ap_applied
                        "o", //ap_status
                        v[4], //ap_bank
                        si[13], //ap_curr
                        si[13], //ap_base_curr
                        shipper, //ap_check // in this case voucher number is reference field
                        String.valueOf(voucher), //ap_batch
                        si[12], //ap_site
                        "Expense",
                        "",
                        "1",
                        "",
                        0,
                        0);  
                        
                        // create vod_mstr JRT
                        fapData.vod_mstr y = new fapData.vod_mstr(null, 
                        String.valueOf(voucher),
                        shipper, // receiver
                        1, // line
                        s[2], // item
                        1, // qty
                        parseDouble(s[4]), //amt
                        setDateDB(effdate), // date
                        si[8], // vendor
                        "", // ap_check 
                        defaultshippingacct,
                        defaultsalescc,
                        "", // po
                        0, // po line
                        "1"    // auto approved
                        );      
                        ArrayList<vod_mstr> vd = new ArrayList<vod_mstr>();
                        vd.add(y);
                        
                        if (s[2].equals("shipping PPD")) {
                        String[] m = _VouchAndPayTransaction(voucher, "AP-Expense", bscon, vd, x, false);    
                        } else {
                        String[] m = _VoucherTransaction(voucher, "AP-Expense", bscon, vd, x, false);
                        }
                        
                        } // if 'shipping BIL' type
                        
                    } // if charge > 0
                   } // for each sac charge
            
           
                    
    }
    
    public static double getShipperTrailerCharges(String shipper) {
        double amt = 0.00;
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

                res = st.executeQuery("select shs_amt from shs_det where shs_type = 'charge' and shs_nbr = " + "'" + shipper + "'" + ";");
                while (res.next()) {
                    amt += res.getDouble("shs_amt");
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
        return amt;

    }

    public static ArrayList<String[]> getShipperSAC(String shipper) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShipperSAC"});
            list.add(new String[]{"param1", shipper});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServSHP"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
      ArrayList<String[]> sac = new ArrayList<String[]>();
      ArrayList<String> orders = new ArrayList<String>();
      
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
            
            // get Orders on shipper
             res = st.executeQuery("select shd_so from ship_det where shd_id = " + "'" + shipper + "'" + " group by shd_so;");
             while (res.next()) {
                 orders.add(res.getString("shd_so"));
             }
            
             for (String o : orders) {
                 res = st.executeQuery("select * from shs_det " +
                         " where shs_nbr = " + "'" + shipper + "'" +
                         " and shs_so = " + "'" + o + "'" + 
                         ";");
                 while (res.next()) {
                     String[] myarray = new String[5];
                     myarray[0] = res.getString("shs_so");
                     myarray[1] = res.getString("shs_desc");
                     myarray[2] = res.getString("shs_type");
                     myarray[3] = res.getString("shs_amttype");
                     myarray[4] = res.getString("shs_amt");
                     sac.add(myarray);
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
            if (con != null) {
                con.close();
            }
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
      return sac;
   }

    public static double getShipperSACTotal(String nbr) {
       double tax = 0;
       double disc = 0;
       double charge = 0;
       double shippertotal = 0;
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
            res = st.executeQuery("SELECT  sum(shd_netprice * shd_qty) as mytotal  " +
                                    " FROM  ship_det  " +
                                    " where shd_nbr = " + "'" + nbr + "'" +       
                                    ";");
                while (res.next()) {
                    shippertotal += res.getDouble("mytotal");
                }
            
            res = st.executeQuery("SELECT * " +
                                    " FROM  shs_det  " +
                                    " where shs_nbr = " + "'" + nbr + "'" +
                                    " and shs_type = 'tax' " +        
                                    " ;");

                double shsamt = 0;
                while (res.next()) {
                    shsamt = res.getDouble("shs_amt");
                    if (res.getString("shs_amttype").equals("percent")) {
                        if (shsamt > 0)
                        tax += (shippertotal * (shsamt / 100)); 
                    } else {
                       tax += shsamt;
                    }
                }
            
                res = st.executeQuery("SELECT * " +
                                    " FROM  shs_det  " +
                                    " where shs_nbr = " + "'" + nbr + "'" +
                                    " and shs_type <> 'tax' " +        
                                    " ;");

                shsamt = 0;
                while (res.next()) {
                    shsamt = res.getDouble("shs_amt");
                    if (res.getString("shs_type").equals("charge")) {
                       charge += shsamt; 
                    }
                    if (res.getString("shs_type").equals("discount")) {
                       if (shsamt > 0)
                        disc += (shippertotal * (shsamt / 100)); 
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
    return charge + tax;

    }

    public static double getShipperTAXTotal(String nbr) {
       double tax = 0;
       double disc = 0;
       double charge = 0;
       double shippertotal = 0;
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
            res = st.executeQuery("SELECT  sum(shd_netprice * shd_qty) as mytotal  " +
                                    " FROM  ship_det  " +
                                    " where shd_nbr = " + "'" + nbr + "'" +       
                                    ";");
                while (res.next()) {
                    shippertotal += res.getDouble("mytotal");
                }
            
            res = st.executeQuery("SELECT * " +
                                    " FROM  shs_det  " +
                                    " where shs_nbr = " + "'" + nbr + "'" +
                                    " and shs_type = 'tax' " +        
                                    " ;");

                double shsamt = 0;
                while (res.next()) {
                    shsamt = res.getDouble("shs_amt");
                    if (res.getString("shs_amttype").equals("percent")) {
                        if (shsamt > 0)
                        tax += (shippertotal * (shsamt / 100)); 
                    } else {
                       tax += shsamt;
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
    
    public static double getShipperTotal(String nbr) {
       double tax = 0.00;
       double disc = 0.00;
       double charge = 0.00;
       double shippertotal = 0.00;
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
            res = st.executeQuery("SELECT  sum(shd_netprice * shd_qty) as mytotal  " +
                                    " FROM  ship_det  " +
                                    " where shd_id = " + "'" + nbr + "'" +       
                                    ";");
                while (res.next()) {
                    shippertotal += res.getDouble("mytotal");
                }
            
            res = st.executeQuery("SELECT * " +
                                    " FROM  shs_det  " +
                                    " where shs_nbr = " + "'" + nbr + "'" +
                                    " and shs_type = 'tax' " +        
                                    " ;");

                double shsamt = 0;
                while (res.next()) {
                    shsamt = res.getDouble("shs_amt");
                    if (res.getString("shs_amttype").equals("percent")) {
                        if (shsamt > 0)
                        tax += (shippertotal * (shsamt / 100)); 
                    } else {
                       tax += shsamt;
                    }
                }
            
                res = st.executeQuery("SELECT * " +
                                    " FROM  shs_det  " +
                                    " where shs_nbr = " + "'" + nbr + "'" +
                                    " and shs_type <> 'tax' " +        
                                    " ;");

                shsamt = 0;
                while (res.next()) {
                    shsamt = res.getDouble("shs_amt");
                    if (res.getString("shs_type").equals("charge")) {
                       charge += shsamt; 
                    }
                    if (res.getString("shs_type").equals("discount")) {
                       if (shsamt > 0)
                        disc += (shippertotal * (shsamt / 100)); 
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
     
         
    return shippertotal + charge + tax;

    }
    
    public static double getTaxAmtApplicableByShipper(String shipper, double amt) {
        double taxamt = 0.00;
        double taxpercent = 0.00;

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

                res = st.executeQuery("select taxd_parentcode, taxd_desc, taxd_percent from taxd_mstr "
                        + " inner join tax_mstr on tax_code = taxd_parentcode "
                        + " inner join ship_mstr on sh_taxcode = tax_code and sh_id = " + "'" + shipper + "'"
                        + " order by tax_code ;");
                while (res.next()) {
                    taxpercent += res.getDouble("taxd_percent");
                }

                if (taxpercent > 0) {
                    taxamt = (amt * (taxpercent / 100));
                } else {
                    taxamt = 0;
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
        return taxamt;

    }

    public static String getShipperBillto(String shipper) {
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



                  res = st.executeQuery("select sh_cust from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    billto = res.getString("sh_cust");
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

    public static String getShipperSite(String shipper) {
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



                  res = st.executeQuery("select sh_site from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    billto = res.getString("sh_site");
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
       
    public static String getShipperStatus(String shipper) {
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
            
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);



                  res = st.executeQuery("select sh_status from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    r = res.getString("sh_status");
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
    
    public static String getShipperRef(String shipper) {
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



                  res = st.executeQuery("select sh_ref from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    billto = res.getString("sh_ref");
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

    public static edi856 init_edi856_object(String shipper) {
        edi856 e = null;
        ArrayList<ship_det> lines = new ArrayList<ship_det>();
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
               res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    ship_det shd = new ship_det(null,
                    shipper,
                    bsParseInt(res.getString("shd_line")), //shline
                    res.getString("shd_item"),
                    res.getString("shd_custitem"), 
                    res.getString("shd_so"), // so
                    bsParseInt(res.getString("shd_soline")), // soline = shline  
                    res.getString("shd_date"), //shipdate
                    res.getString("shd_po"), // po
                    bsParseDouble(res.getString("shd_qty")), // qty 
                    res.getString("shd_uom"), // uom
                    "", // currency  
                    bsParseDouble(res.getString("shd_netprice")), // netprice
                    bsParseDouble(res.getString("shd_disc")), // disc
                    bsParseDouble(res.getString("shd_listprice")), // listprice
                    res.getString("shd_desc"), // desc
                    res.getString("shd_wh"), // wh
                    res.getString("shd_loc"), // loc
                    bsParseDouble(res.getString("shd_taxamt")), // taxamt
                    res.getString("shd_cont"), // cont
                    res.getString("shd_ref"), // ref
                    res.getString("shd_serial"), // serial
                    res.getString("shd_site"),
                    res.getString("shd_bom"), // bom
                    res.getDouble("shd_packqty"),
                    res.getString("shd_kvpair")
                    );
                    lines.add(shd);
                }

                res = st.executeQuery("select * from ship_mstr " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " inner join cms_det on cms_det.cms_shipto = sh_ship and cms_det.cms_code = sh_cust " +
                        " inner join cust_term on cm_terms = cut_code " +
                        " inner join ar_mstr on ar_nbr = sh_id and ar_type = 'I' " +
                        " where sh_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    e = new edi856(res.getString("sh_cust"),
                    res.getString("sh_ship"),
                    res.getString("sh_so"),
                    res.getString("sh_po"),
                    res.getString("sh_po_date"),
                    res.getString("sh_shipdate"),
                    res.getString("sh_rmks"),
                    res.getString("sh_ref"),
                    res.getString("sh_shipvia"),
                    res.getString("sh_gross_wt"),
                    res.getString("sh_net_wt"),
                    res.getString("sh_trailer"),
                    res.getString("sh_site"),
                    res.getString("sh_curr"),
                    res.getString("sh_shipfrom"),
                    res.getString("cm_misc1"),
                    res.getString("cm_name"),
                    res.getString("cm_line1"),
                    res.getString("cm_city"),
                    res.getString("cm_state"),
                    res.getString("cm_zip"),
                    res.getString("cm_country"),
                    res.getString("cms_name"),
                    res.getString("cms_line1"),
                    res.getString("cms_city"),
                    res.getString("cms_state"),
                    res.getString("cms_zip"),
                    res.getString("cms_country"),
                    res.getString("sh_site"),
                    res.getString("cut_code"),
                    res.getString("cut_desc"),
                    res.getString("cut_discpercent"),
                    res.getString("cut_days"),
                    res.getString("ar_duedate"),
                    res.getString("sh_confdate"), lines);
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
    
    public static edi810 init_edi810_object(String shipper) {
        edi810 e = null;
        ArrayList<ship_det> lines = new ArrayList<ship_det>();
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
               res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    ship_det shd = new ship_det(null,
                    shipper,
                    bsParseInt(res.getString("shd_line")), //shline
                    res.getString("shd_item"),
                    res.getString("shd_custitem"), 
                    res.getString("shd_so"), // so
                    bsParseInt(res.getString("shd_soline")), // soline = shline  
                    res.getString("shd_date"), //shipdate
                    res.getString("shd_po"), // po
                    bsParseDouble(res.getString("shd_qty")), // qty 
                    res.getString("shd_uom"), // uom
                    "", // currency  
                    bsParseDouble(res.getString("shd_netprice")), // netprice
                    bsParseDouble(res.getString("shd_disc")), // disc
                    bsParseDouble(res.getString("shd_listprice")), // listprice
                    res.getString("shd_desc"), // desc
                    res.getString("shd_wh"), // wh
                    res.getString("shd_loc"), // loc
                    bsParseDouble(res.getString("shd_taxamt")), // taxamt
                    res.getString("shd_cont"), // cont
                    res.getString("shd_ref"), // ref
                    res.getString("shd_serial"), // serial
                    res.getString("shd_site"),
                    res.getString("shd_bom"), // bom
                    res.getDouble("shd_packqty"),
                    res.getString("shd_kvpair")
                    );
                    lines.add(shd);
                }

                res = st.executeQuery("select * from ship_mstr " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " inner join cms_det on cms_det.cms_shipto = sh_ship and cms_det.cms_code = sh_cust " +
                        " inner join cust_term on cm_terms = cut_code " +
                        " inner join ar_mstr on ar_nbr = sh_id and ar_type = 'I' " +
                        " where sh_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    e = new edi810(res.getString("sh_cust"),
                    res.getString("sh_ship"),
                    res.getString("sh_so"),
                    res.getString("sh_po"),
                    res.getString("sh_po_date"),
                    res.getString("sh_shipdate"),
                    res.getString("sh_rmks"),
                    res.getString("sh_ref"),
                    res.getString("sh_shipvia"),
                    res.getString("sh_gross_wt"),
                    res.getString("sh_net_wt"),
                    res.getString("sh_trailer"),
                    res.getString("sh_site"),
                    res.getString("sh_curr"),
                    res.getString("sh_shipfrom"),
                    res.getString("cm_misc1"),
                    res.getString("cm_name"),
                    res.getString("cm_line1"),
                    res.getString("cm_city"),
                    res.getString("cm_state"),
                    res.getString("cm_zip"),
                    res.getString("cm_country"),
                    res.getString("cms_name"),
                    res.getString("cms_line1"),
                    res.getString("cms_city"),
                    res.getString("cms_state"),
                    res.getString("cms_zip"),
                    res.getString("cms_country"),
                    res.getString("sh_site"),
                    res.getString("cut_code"),
                    res.getString("cut_desc"),
                    res.getString("cut_discpercent"),
                    res.getString("cut_days"),
                    res.getString("ar_duedate"),
                    res.getString("sh_confdate"), lines);
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
    
    
    
    public static String[] getShipperHeader(String shipper) {

        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getShipperHeader"});
            list.add(new String[]{"param1",shipper});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServSHP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
          String[] H = new String[35];
          for (int i = 0; i < H.length; i++) {
              H[i] = "";
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
        try{
           
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);


                res = st.executeQuery("select * from ship_mstr " +
                        " inner join cm_mstr on cm_code = sh_cust " +
                        " inner join cms_det on cms_det.cms_shipto = sh_ship and cms_det.cms_code = sh_cust " +
                        " inner join cust_term on cm_terms = cut_code " +
                        " left outer join ar_mstr on ar_nbr = sh_id and ar_type = 'I' " +
                        " where sh_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    H[0] = res.getString("sh_cust");
                    H[1] = res.getString("sh_ship");
                    H[2] = res.getString("sh_so");
                    H[3] = res.getString("sh_po");
                    H[4] = res.getString("sh_po_date");
                    H[5] = res.getString("sh_shipdate");
                    H[6] = res.getString("sh_rmks");
                    H[7] = res.getString("sh_ref");
                    H[8] = res.getString("sh_shipvia");
                    H[9] = res.getString("sh_gross_wt");
                    H[10] = res.getString("sh_net_wt");
                    H[11] = res.getString("sh_trailer");
                    H[12] = res.getString("sh_site");
                    H[13] = res.getString("sh_curr");
                    H[14] = res.getString("sh_shipfrom");
                    H[15] = res.getString("cm_misc1");
                    H[16] = res.getString("cm_name");
                    H[17] = res.getString("cm_line1");
                    H[18] = res.getString("cm_city");
                    H[19] = res.getString("cm_state");
                    H[20] = res.getString("cm_zip");
                    H[21] = res.getString("cm_country");
                    H[22] = res.getString("cms_name");
                    H[23] = res.getString("cms_line1");
                    H[24] = res.getString("cms_city");
                    H[25] = res.getString("cms_state");
                    H[26] = res.getString("cms_zip");
                    H[27] = res.getString("cms_country");
                    H[28] = res.getString("sh_site");
                    H[29] = res.getString("cut_code");
                    H[30] = res.getString("cut_desc");
                    H[31] = res.getString("cut_discpercent");
                    H[32] = res.getString("cut_days");
                    H[33] = res.getString("ar_duedate");
                    H[34] = res.getString("sh_confdate");
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
    return H;

     }

    public static ArrayList<String> getShipperLineNumbers(String shipper) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShipperLineNumbers"});
            list.add(new String[]{"param1", shipper});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServSHP"));
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

           res = st.executeQuery("SELECT shd_line from ship_det " +
                   " where shd_id = " + "'" + shipper + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("shd_line"));
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
    
    
    public static ArrayList<String[]> getShipperLines(String shipper) {
    
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","getShipperLines"});
            list.add(new String[]{"param1",shipper});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServSHP"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
    ArrayList<String[]> mylist = new ArrayList();  

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


               
                  res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    String[] d = new String[13];
                    for (int z = 0; z < 13; z++) {
                        d[z] = "";
                    }
                    d[0] = res.getString("shd_item");
                    d[1] = res.getString("shd_custitem");
                    d[2] = res.getString("shd_qty");
                    d[3] = res.getString("shd_po");
                    d[4] = res.getString("shd_cumqty");
                    d[5] = res.getString("shd_listprice");
                    d[6] = res.getString("shd_netprice");
                    d[7] = res.getString("shd_ref");
                    d[8] = res.getString("shd_sku");
                    d[9] = res.getString("shd_desc");
                    d[10] = res.getString("shd_soline");
                    d[11] = res.getString("shd_line");
                    d[12] = res.getString("shd_uom");
                    mylist.add(d);
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
    return mylist;

     }

    public static HashSet<String> getShipperTreeUniquePOs(String shipper) {
          HashSet<String> set = new HashSet();  

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


               
                  res = st.executeQuery("select ship_po from ship_tree where ship_sh = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    if (! res.getString("ship_po").isBlank() && ! set.contains(res.getString("ship_po"))) {
                        set.add(res.getString("ship_po"));
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
    return set;

     }

    public static HashSet<String> getShipperTreePackOfPO(String shipper, String po) {
          HashSet<String> set = new HashSet();  

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


               
                  res = st.executeQuery("select ship_parent from ship_tree where ship_sh = " + "'" + shipper + "'" + 
                          " AND ship_po = " + "'" + po + "'" +
                          ";");
                while (res.next()) {
                    if (! res.getString("ship_parent").isBlank() && ! set.contains(res.getString("ship_parent"))) {
                        set.add(res.getString("ship_parent"));
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
    return set;

     }

    public static ArrayList<String[]> getShipperTreeLinesOfPack(String serial, String shipper) {
          ArrayList<String[]> mylist = new ArrayList();  

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
            
          
                  res = st.executeQuery("select ship_item, ship_qty, shd_uom, shd_custitem, shd_desc, shd_line, coalesce(lbl_id_str,'') as 'lbl_id_str' from ship_tree " +
                          " inner join ship_det on shd_id = ship_sh and shd_line = ship_shline " +
                          " left outer join label_mstr on lbl_id = ship_parent " +
                          " where ship_parent = " + "'" + serial + "'" + 
                          " AND ship_sh = " + "'" + shipper + "'" +
                          ";");
                  
                  String packtype = "none";
                  
                  while (res.next()) {
                    String[] d = new String[8];
                    for (int z = 0; z < 8; z++) {
                        d[z] = "";
                    }
                    packtype = "none";
                    if (! res.getString("lbl_id_str").isEmpty()) {
                        packtype = "GM";
                    }
                    d[0] = res.getString("ship_item");
                    d[1] = res.getString("ship_qty");
                    d[2] = res.getString("shd_custitem");
                    d[3] = res.getString("shd_desc");
                    d[4] = res.getString("shd_line");
                    d[5] = res.getString("lbl_id_str");
                    d[6] = packtype;
                    d[7] = res.getString("shd_uom");
                    mylist.add(d);
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
    return mylist;

     }

    public static String getShipperTreeRootType(String shipper) {
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
            
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);


               
                  res = st.executeQuery("select ship_type from ship_tree where ship_sh = " + "'" + shipper + "'" + 
                          " and ship_type <> 'i' " +    
                          ";");
                while (res.next()) {
                    r = res.getString("ship_type");
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

    
    public static ArrayList getShippersOpenListForFreight() {
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
            
           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String mydate = dfdate.format(now);

               // if shipper has been assigned to a freight order...the sh_freight field will be occupied with the freight order number...otherwise 
               // it will be blank and available for freight.                  
                  res = st.executeQuery("select sh_id from ship_mstr where sh_status = '0' AND sh_freight = '' " + " order by sh_id desc ;");
                while (res.next()) {
                    mylist.add(res.getString("sh_id"));
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
    return mylist;

     }

    public static void updateShipperSAC(String shipper) {
       
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "updateShipperSAC"});
            list.add(new String[]{"param1",  shipper});
            try {
                sendServerPost(list, "", null, "dataServSHP");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
        
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       ArrayList<String> orders = new ArrayList<String>();
       ArrayList<String[]> sac = new ArrayList<String[]>();
       Double matltax = 0.00;
       Double totamt = 0.00;
       
       String fieldlabel = "";
       fieldlabel = getCodeValueByCodeKey("fieldlabel", "materialtax");
       
       if (fieldlabel.isBlank()) {
       fieldlabel = "Material Tax";
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
        try{

            // determine if cascading
            boolean isCascade = false;
            res = st.executeQuery("select so_cascade from so_mstr inner join ship_mstr on sh_so = so_nbr where sh_id = " + "'" + shipper + "'" + ";");
             while (res.next()) {
                 isCascade = BlueSeerUtils.ConvertStringToBool(res.getString("so_cascade"));
             }

             // get Orders on shipper
             res = st.executeQuery("select shd_so from ship_det where shd_id = " + "'" + shipper + "'" + " group by shd_so;");
             while (res.next()) {
                 orders.add(res.getString("shd_so"));
             }

            // get material tax for each item (if any) associated with this shipper
            res = st.executeQuery("select shd_taxamt, shd_qty, shd_listprice from ship_det where shd_id = " + "'" + shipper + "'" + ";");
             while (res.next()) {
                 matltax += res.getDouble("shd_taxamt");
                 totamt += res.getDouble("shd_qty") * res.getDouble("shd_listprice");
             }


             // delete old shs_det records first
             st.executeUpdate("delete from shs_det where shs_nbr = " + "'" + shipper + "'");

              // now lets loop through the orders sos_det table and write to shs_det
              // we also convert any percent based records to percentage amount of totamt
             for (String o : orders) {
             sac = OVData.getOrderSAC(o);
             //write to shs_det
                 String myamttype = "";
                 double myamt = 0.00;

                 // sac order of elements...sos_nbr, sos_desc, sos_type, sos_amttype, sos_amt
                 for (String[] s : sac) {
                 myamttype = s[3];
                 myamt = bsParseDouble(s[4]);
                 
                 // adjust if percent based
                 if (s[3].equals("percent")) {
                    myamttype = "amount";
                    myamt = (bsParseDouble(s[4]) / 100) * totamt; 
                 }    
                 st.executeUpdate(" insert into shs_det (shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt ) " +
                                 " values ( "  + "'" + shipper + "'" + "," +
                                 "'" + s[0] + "'" + "," +
                                 "'" + s[1] + "'" + "," +
                                 "'" + s[2] + "'" + "," +
                                 "'" + myamttype + "'" + "," +
                                 "'" + currformatDoubleUS(myamt) + "'" + 
                                 ") ;");
                 
                    if (isCascade) {
                    totamt = (totamt + (totamt * (bsParseDouble(s[4]) / 100)));
                    }
                 }
                 // now insert matltax if any for summary purposes
                 if (matltax > 0) {
                 st.executeUpdate(" insert into shs_det (shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt ) " +
                                 " values ( "  + "'" + shipper + "'" + "," +
                                 "'" + "" + "'" + "," +
                                 "'" + fieldlabel + "'" + "," +
                                 "'" + "tax" + "'" + "," +
                                 "'" + "amount" + "'" + "," +
                                 "'" + currformatDoubleUS(matltax) + "'" + 
                                 ") ;");
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

   }
     
    public static void _updateShipperSAC(String shipper, Connection bscon) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       ArrayList<String> orders = new ArrayList<String>();
       ArrayList<String[]> sac = new ArrayList<String[]>();
       Double matltax = 0.00;
       Double totamt = 0.00;
       
       String fieldlabel = "";
       fieldlabel = getCodeValueByCodeKey("fieldlabel", "materialtax");
       
       if (fieldlabel.isBlank()) {
       fieldlabel = "Material Tax";
       }
       
       try{
        
        Statement st = bscon.createStatement();
        ResultSet res = null;
        try{

             // determine if cascading
            boolean isCascade = false;
            res = st.executeQuery("select so_cascade from so_mstr inner join ship_mstr on sh_so = so_nbr where sh_id = " + "'" + shipper + "'" + ";");
             while (res.next()) {
                 isCascade = BlueSeerUtils.ConvertStringToBool(res.getString("so_cascade"));
             }

             // get Orders on shipper
             res = st.executeQuery("select shd_so from ship_det where shd_id = " + "'" + shipper + "'" + " group by shd_so;");
             while (res.next()) {
                 orders.add(res.getString("shd_so"));
             }

            // get material tax for each item (if any) associated with this shipper
            res = st.executeQuery("select shd_taxamt, shd_qty, shd_listprice from ship_det where shd_id = " + "'" + shipper + "'" + ";");
             while (res.next()) {
                 matltax += res.getDouble("shd_taxamt");
                 totamt += res.getDouble("shd_qty") * res.getDouble("shd_listprice");
             }


             // delete old shs_det records first
             st.executeUpdate("delete from shs_det where shs_nbr = " + "'" + shipper + "'");

              // now lets loop through the orders sos_det table and write to shs_det
              // we also convert any percent based records to percentage amount of totamt
             for (String o : orders) {
             sac = OVData.getOrderSAC(o);
             //write to shs_det
                 String myamttype = "";
                 double myamt = 0.00;

                 // sac order of elements...sos_nbr, sos_desc, sos_type, sos_amttype, sos_amt
                 for (String[] s : sac) {
                 myamttype = s[3];
                 myamt = bsParseDouble(s[4]);
                 
                 if (isCascade) {
                 totamt = (totamt + (totamt * (bsParseDouble(s[4]) / 100)));
                 }

                 // adjust if percent based...shs_det should have absolute value of discounts...not percentages
                 if (s[3].equals("percent")) {
                   myamttype = "amount";
                   myamt = (bsParseDouble(s[4]) / 100) * totamt;
                 }    
                 
                 st.executeUpdate(" insert into shs_det (shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt ) " +
                                 " values ( "  + "'" + shipper + "'" + "," +
                                 "'" + s[0] + "'" + "," +
                                 "'" + s[1] + "'" + "," +
                                 "'" + s[2] + "'" + "," +
                                 "'" + myamttype + "'" + "," +
                                 "'" + currformatDoubleUS(myamt) + "'" + 
                                 ") ;");
                 }
                 // now insert matltax if any for summary purposes
                 if (matltax > 0) {
                 st.executeUpdate(" insert into shs_det (shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt ) " +
                                 " values ( "  + "'" + shipper + "'" + "," +
                                 "'" + "" + "'" + "," +
                                 "'" + fieldlabel + "'" + "," +
                                 "'" + "tax" + "'" + "," +
                                 "'" + "amount" + "'" + "," +
                                 "'" + currformatDoubleUS(matltax) + "'" + 
                                 ") ;");
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
        }

    }
    catch (Exception e){
        MainFrame.bslog(e);
    }

   }
    
    
    public static void updateShipperWithFreightOrder(ArrayList<String[]> tablelist) {
        // table structure    "line", "FONbr", "Type", "Shipper", "Ref", "Name", "Addr1", "Addr2", "City", "State", "Zip", "Contact", "Phone", "Email", "Units", "Weight"
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
           
            for (String[] v : tablelist) {
                   if (v[3].isEmpty()) /// if shipper is empty (the LD)
                       continue;
                       st.executeUpdate(
                             " update ship_mstr set sh_freight = " + "'" + v[1] + "'" +
                             " where sh_id = " + "'" + v[3] + "'" + ";" );
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

    public static void CreateShipperDetFromTable(JTable dettable, String shippernbr, String shipdate, String site) {

      // table field order:  "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc", "Taxamt"
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
            
            boolean proceed = true;

            if (proceed) {
                for (int j = 0; j < dettable.getRowCount(); j++) {
                    st.executeUpdate("insert into ship_det "
                        + "(shd_id, shd_soline, shd_item, shd_custitem, shd_so, shd_po, shd_date, shd_qty, shd_uom, "
                        + "shd_listprice, shd_disc, shd_netprice, shd_wh, shd_loc, shd_desc, shd_taxamt, shd_site ) "
                        + " values ( " + "'" + shippernbr + "'" + ","
                        + "'" + dettable.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + dettable.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                        + "'" + dettable.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                        + "'" + dettable.getValueAt(j, 3).toString().replace("'", "") + "'" + ","
                        + "'" + dettable.getValueAt(j, 4).toString().replace("'", "") + "'" + ","        
                        + "'" + shipdate + "'" + ","        
                        + "'" + dettable.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + dettable.getValueAt(j, 6).toString() + "'" + ","
                        + "'" + dettable.getValueAt(j, 7).toString().replace(defaultDecimalSeparator, '.') + "'" + ","        
                        + "'" + dettable.getValueAt(j, 8).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + dettable.getValueAt(j, 9).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + dettable.getValueAt(j, 12).toString() + "'" + ","
                        + "'" + dettable.getValueAt(j, 13).toString() + "'" + ","
                        + "'" + dettable.getValueAt(j, 14).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + dettable.getValueAt(j, 15).toString() + "'" + ","        
                        + "'" + site + "'"
                        + ")"
                        + ";");
                }
            } // if proceed
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
  }

    
    public static boolean CreateShipperHdr(String nbr, String site, String bol, String billto, String shipto, String so, String po, String ref, String shipdate, String orddate, String remarks, String shipvia, String shiptype ) {
      boolean isError = false; 

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
            boolean proceed = true;
            int i = 0;
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();

            // initialize ord and due date if blank
            if (orddate == null || orddate.isEmpty()) {
                orddate = dfdate.format(now);
            }
            if (shipdate == null || shipdate.isEmpty()) {
                shipdate = dfdate.format(now);
            }

            if (! BlueSeerUtils.isValidDateStr(orddate)) {
                if (orddate.length() == 8) {
                    DateFormat format = new SimpleDateFormat("yyyyMMdd");
                    Date mydate = format.parse(orddate);
                    orddate = BlueSeerUtils.setDateFormat(mydate);
                }  
                if (orddate.length() == 6) {
                    DateFormat format = new SimpleDateFormat("yyMMdd");
                    Date mydate = format.parse(orddate);
                    orddate = BlueSeerUtils.setDateFormat(mydate);
                }   
            }

            if (! BlueSeerUtils.isValidDateStr(shipdate)) {
                if (shipdate.length() == 8) {
                    DateFormat format = new SimpleDateFormat("yyyyMMdd");
                    Date mydate = format.parse(shipdate);
                    shipdate = BlueSeerUtils.setDateFormat(mydate);
                }  
                if (shipdate.length() == 6) {
                    DateFormat format = new SimpleDateFormat("yyMMdd");
                    Date mydate = format.parse(shipdate);
                    shipdate = BlueSeerUtils.setDateFormat(mydate);
                }   
            }


            // get billto specific data
            String acct = "";
            String cc = "";
            String terms = "";
            String carrier = "";
            String onhold = "";
            String taxcode = "";
            String curr = "";

            res = st.executeQuery("select * from cm_mstr where cm_code = " + "'" + billto + "'" + " ;");
           while (res.next()) {
               i++;
               acct = res.getString("cm_ar_acct");
               cc = res.getString("cm_ar_cc");
               carrier = res.getString("cm_carrier");
               terms = res.getString("cm_terms");
               taxcode = res.getString("cm_tax_code");
               onhold = res.getString("cm_onhold");
               curr = res.getString("cm_curr");
            }


            if (! shipvia.isEmpty()) {
                carrier = shipvia;
            }

            // override cust currency with order currency
            String order_curr = ordData.getOrderCurrency(so);
            if (! order_curr.isEmpty()) {
            curr = order_curr;
            }
            // logic for asset type shipment/sale
            if (shiptype.equals("A")) {
                terms = "N00";
            }

            if (proceed) {
                st.executeUpdate("insert into ship_mstr " 
                    + " (sh_id, sh_cust, sh_ship,"
                    + " sh_shipdate, sh_po_date, sh_bol, sh_po, sh_ref, sh_rmks, sh_userid, sh_site, sh_curr, sh_shipvia, sh_cust_terms, sh_taxcode, sh_ar_acct, sh_ar_cc, sh_type, sh_char2 ) "
                    + " values ( " + "'" + nbr + "'" + "," 
                    + "'" + billto + "'" + "," 
                    + "'" + shipto + "'" + ","
                    + "'" + shipdate + "'" + ","
                    + "'" + orddate + "'" + ","
                    + "'" + bol + "'" + "," 
                    + "'" + po + "'" + "," 
                    + "'" + ref + "'" + ","        
                    + "'" + remarks + "'" + "," 
                    + "'" + bsmf.MainFrame.userid + "'" + "," 
                    + "'" + site + "'" + ","
                    + "'" + curr + "'" + ","
                    + "'" + carrier + "'" + ","        
                    + "'" + terms + "'" + ","
                    + "'" + taxcode + "'" + ","
                    + "'" + acct + "'" + ","
                    + "'" + cc + "'" + ","
                    + "'" + shiptype + "'" + ","
                    + "'0'" // shipper complete
                    + ");" );
            } // if proceed
            else {
                isError = true;
            }
        } catch (SQLException s) {
            isError = true;
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
      return isError;
  } 

    public static void CreateShipperDet(String nbr, String part, String custpart, String skupart, String so, String po, String qty, String uom, String listprice, String discpercent, String netprice, String shipdate, String desc, String line, String site, String wh, String loc, String taxamt) {
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
            
            boolean proceed = true;
            int i = 0;
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");


            if (proceed) {
                    st.executeUpdate("insert into ship_det "
                        + "(shd_id, shd_soline, shd_item, shd_so, shd_date, shd_po, shd_qty, shd_uom, "
                        + "shd_netprice, shd_listprice, shd_disc, shd_desc, shd_wh, shd_loc, shd_taxamt, shd_site ) "
                        + " values ( " + "'" + nbr + "'" + ","
                        + "'" + line + "'" + ","
                        + "'" + part + "'" + ","
                        + "'" + so + "'" + ","
                        + "'" + shipdate + "'" + ","        
                        + "'" + po + "'" + ","
                        + "'" + qty.replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + uom + "'" + ","        
                        + "'" + netprice.replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + listprice.replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + discpercent.replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + desc + "'" + ","
                        + "'" + wh + "'" + ","
                        + "'" + loc + "'" + ","
                        + "'" + taxamt.replace(defaultDecimalSeparator, '.') + "'" + ","        
                        + "'" + site + "'"
                        + ")"
                        + ";");
            } // if proceed
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
  }

    public static String[] CreateShipperByJSON(String jsonString) {
      String[] x = new String[]{"","",""};
      boolean isError = false; 
      JSONObject json = new JSONObject(jsonString);
      String junktag = "";
      String nbr = "";
      String Site = "";
      String BOLNumber = "";
      String BillToCode = "";
      String ShipToCode = "";
      String OrderNumber = "";
      String PONumber = "";
      String Reference = "";
      String ShipDate = "";
      String OrderDate = "";
      String Remarks = "";
      String ShipVia = "";
      String Type = "";
      ArrayList<String[]> detail = new ArrayList<String[]>();


      for (String keyStr : json.keySet()) { 
       Object keyvalue = json.get(keyStr);

       // process header tags in JSON
       switch(keyStr) {
             case "OrderNumber" :
                 OrderNumber = keyvalue.toString();
                 break;
             case "PONumber" :
                 PONumber = keyvalue.toString();
                 break;
             case "Remarks" :
                 Remarks = keyvalue.toString();
                 break;
             case "OrderDate" :
                 OrderDate = keyvalue.toString();
                 break;
             case "ShipDate" :
                 ShipDate = keyvalue.toString();
                 break;
             case "BOLNumber" :
                 BOLNumber = keyvalue.toString();
                 break;
             case "Type" :
                 Type = keyvalue.toString();
                 break; 
             case "ShipVia" :
                 ShipVia = keyvalue.toString();
                 break;    
             case "BillToCode" :
                 BillToCode = keyvalue.toString();
                 break;
             case "Reference" :
                 Reference = keyvalue.toString();
                 break;
             case "Site" :
                 Site = keyvalue.toString();
                 break;
             default :
                 junktag = keyvalue.toString();
        }

       // process detail array 'Items' in JSON
       if (keyStr.equals("Items")) {
            for (Object line : (JSONArray) keyvalue) {
                JSONObject jsonDetail = new JSONObject(line.toString());

                String ItemNumber = "";
                String ItemDescription = "";
                String Line = "";
                String Order = "";
                String PO = "";
                String ShipQty = "";
                String UOM = "";
                String CustItem = "";
                String SkuItem = "";
                String UpcItem = "";
                String ListPrice = "";
                String NetPrice = "";
                String Discount = "";
                String TaxAmt = "";
                String Warehouse = "";
                String Location = "";
                String junktagdet = "";


                for (String detailKey : jsonDetail.keySet()) {
                    Object detailValue = jsonDetail.get(detailKey);
                    switch(detailKey) {
                         case "ItemNumber" :
                             ItemNumber = detailValue.toString();
                             break;
                         case "ItemDescription" :
                             ItemDescription = detailValue.toString();
                             break;    
                         case "Line" :
                             Line = detailValue.toString();
                             break;
                         case "Order" :
                             Order = detailValue.toString();
                             break;
                         case "PO" :
                             PO = detailValue.toString();
                             break;    
                         case "ShipQty" :
                             ShipQty = detailValue.toString();
                             break;
                         case "UOM" :
                             UOM = detailValue.toString();
                             break;
                         case "CustItem" :
                             CustItem = detailValue.toString();
                             break;
                         case "SkuItem" :
                             SkuItem = detailValue.toString();
                             break;    
                         case "UpcItem" :
                             UpcItem = detailValue.toString();
                             break;    
                         case "ListPrice" :
                             ListPrice = detailValue.toString();
                             break;  
                         case "NetPrice" :
                             NetPrice  = detailValue.toString();
                             break;  
                         case "Discount" :
                             Discount = detailValue.toString();
                             break;  
                         case "TaxAmt" :
                             TaxAmt = detailValue.toString();
                             break;
                         case "Warehouse" :
                             Warehouse = detailValue.toString();
                             break;
                         case "Location" :
                             Location = detailValue.toString();
                             break;    
                         default :
                             junktagdet = detailValue.toString();
                    }
                }
                detail.add(new String[]{ItemNumber, ItemDescription, Line, Order, PO, ShipQty, UOM, CustItem, SkuItem, UpcItem, ListPrice, NetPrice, Discount, TaxAmt, Warehouse, Location});

            }
       } // if key = "Items"

      }

      // here we create the table records
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
            
            boolean proceed = true;
            int i = 0;
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();

            if (nbr.isEmpty()) {
            nbr = String.valueOf(OVData.getNextNbr("shipper"));
            }

             // get billto specific data
            // aracct, arcc, currency, bank, terms, carrier, onhold, site
            String[] custinfo = new String[]{"","","","","","","", ""};

            // if billto exists...use it...otherwise create unique billto/shipto
            res = st.executeQuery("select * from cm_mstr where cm_code = " + "'" + BillToCode + "'" + " ;");
           while (res.next()) {
               i++;
               custinfo[0] = res.getString("cm_ar_acct");
               custinfo[1] = res.getString("cm_ar_cc");
               custinfo[2] = res.getString("cm_curr");
               custinfo[3] = res.getString("cm_bank");
               custinfo[4] = res.getString("cm_terms");
               custinfo[5] = res.getString("cm_carrier");
               custinfo[6] = res.getString("cm_onhold");
               custinfo[7] = res.getString("cm_site");
            }


            if (proceed) {
                st.executeUpdate("insert into ship_mstr " 
                    + " (sh_id, sh_cust, sh_ship,"
                    + " sh_shipdate, sh_po_date, sh_bol, sh_po, sh_ref, sh_rmks, sh_userid, sh_site, sh_curr, sh_shipvia, sh_cust_terms, sh_taxcode, sh_ar_acct, sh_ar_cc, sh_type ) "
                    + " values ( " + "'" + nbr + "'" + "," 
                    + "'" + BillToCode + "'" + "," 
                    + "'" + ShipToCode + "'" + ","
                    + "'" + ShipDate + "'" + ","
                    + "'" + OrderDate + "'" + ","
                    + "'" + BOLNumber + "'" + "," 
                    + "'" + PONumber + "'" + "," 
                    + "'" + Reference + "'" + ","        
                    + "'" + Remarks + "'" + "," 
                    + "'" + bsmf.MainFrame.userid + "'" + "," 
                    + "'" + Site + "'" + ","
                    + "'" + custinfo[2] + "'" + ","
                    + "'" + ShipVia + "'" + ","        
                    + "'" + custinfo[4] + "'" + ","
                    + "'" + "" + "'" + ","
                    + "'" + custinfo[0] + "'" + ","
                    + "'" + custinfo[1] + "'" + ","
                    + "'" + Type + "'"
                    + ");" );


                //ItemNumber, ItemDescription, Line, Order, PO, ShipQty, UOM, CustItem, SkuItem, UpcItem, ListPrice, NetPrice, Discount, TaxAmt, Warehouse, Location});
                for (String[] s : detail) {
                st.executeUpdate("insert into ship_det "
                        + "(shd_id, shd_soline, shd_item, shd_so, shd_date, shd_po, shd_qty, shd_uom, "
                        + "shd_netprice, shd_listprice, shd_disc, shd_desc, shd_wh, shd_loc, shd_taxamt, shd_site ) "
                        + " values ( " + "'" + nbr + "'" + ","
                        + "'" + s[2] + "'" + ","
                        + "'" + s[0] + "'" + ","
                        + "'" + s[3] + "'" + ","
                        + "'" + ShipDate + "'" + ","        
                        + "'" + s[4] + "'" + ","
                        + "'" + s[5] + "'" + ","
                        + "'" + s[6] + "'" + ","        
                        + "'" + s[11] + "'" + ","
                        + "'" + s[10] + "'" + ","
                        + "'" + s[12] + "'" + ","
                        + "'" + s[1] + "'" + ","
                        + "'" + s[14] + "'" + ","
                        + "'" + s[15] + "'" + ","
                        + "'" + s[13] + "'" + ","        
                        + "'" + Site + "'"
                        + ")"
                        + ";");
                }

                x[0] = "success";
                x[1] = "Loaded Shipper Successfully";
                x[2] = nbr;

            } // if proceed
            else {
                x[0] = "fail";
                x[1] = "unable to process";
            }
        } catch (SQLException s) {
            x[0] = "fail";
            x[1] = "unable to load shipper SQLException";
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

    public static boolean addUpdateShipMeta(String id, String type, String key, String value) {
        boolean x = false;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "addUpdateShipMeta"});
            list.add(new String[]{"param1", id});
            list.add(new String[]{"param2", type});
            list.add(new String[]{"param3", key});
            list.add(new String[]{"param4", value});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServSHP"));
            } catch (IOException ex) {
                bslog(ex);
                return x;
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
            ResultSet res = null;
            try {

                int i = 0;
                res = st.executeQuery("SELECT shm_value FROM sh_meta where shm_id = " + "'" + id + "'"
                        + " AND shm_type = " + "'" + type + "'"
                        + " AND shm_key = " + "'" + key + "'"     
                        + " ;");
                while (res.next()) {
                    i++;
                }

                if (i == 0) {
                    st.executeUpdate("insert into sh_meta (shm_id, shm_type, shm_key, shm_value) values ( "
                            + "'" + id + "'" + ","
                            + "'" + type + "'" + ","
                            + "'" + key + "'" + ","
                            + "'" + value + "'" + ")"
                            + ";");
                    x = true;
                } else {
                    st.executeUpdate("update sh_meta set "
                            + " shm_value = " + "'" + value + "'"
                            + " where shm_id = " + "'" + id + "'" + " and "
                            + " shm_type = " +  "'" + type + "'" + " and "
                            + " shm_key = " +  "'" + key + "'"  
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
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;
    }

    public static String getShipMetaValue(String id, String type, String key) {
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

                res = st.executeQuery("select shm_value from sh_meta where " +
                        " shm_id = " + "'" + id + "'" + " AND " +
                        " shm_type = " + "'" + type + "'" + " AND " +
                        " shm_key = " + "'" + key + "'" +
                        " order by shm_value;" );
               while (res.next()) {
                x = res.getString("shm_value");                    
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
    
    public record Shipper(String[] m, ship_mstr sh, ArrayList<ship_det> shd,
        ArrayList<shs_det> shs, ArrayList<ship_tree> sht, ArrayList<sh_meta> shmeta, 
        cusData.cms_det cms, cusData.cm_mstr cm) {
        public Shipper(String[] m) {
            this (m, null, null, null, null, null, null, null);
        }
    }
    
    public record ship_mstr(String[] m, String sh_id, String sh_cust, String sh_ship, int sh_pallets, 
        int sh_boxes, String sh_shipvia, String sh_shipdate, String sh_po_date,
        String sh_ref, String sh_po, String sh_rmks, String sh_userid, String sh_site,
        String sh_curr, String sh_wh, String sh_cust_terms, String sh_taxcode,
        String sh_ar_acct, String sh_ar_cc, String sh_type, String sh_so, String sh_shipfrom, String sh_trailer, String sh_status,
        String sh_char1, String sh_char2, String sh_char3) {
         public ship_mstr(String[] m) {
            this(m, "", "", "", 0, 0, "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "" );
        }
    }
   
    public record ship_det(String[] m, String shd_id, int shd_line, String shd_item, String shd_custitem, String shd_so,
        int shd_soline, String shd_date, String shd_po, double shd_qty, String shd_uom, String shd_curr,
        double shd_netprice, double shd_disc, double shd_listprice, String shd_desc, 
        String shd_wh, String shd_loc, double shd_taxamt, String shd_cont, String shd_ref,
        String shd_serial, String shd_site, String shd_bom, double shd_packqty, String shd_kvpair) {
        public ship_det(String[] m) {
            this(m, "", 0, "", "", "", 0, "", "", 0, "",
                    "", 0, 0, 0, "", "", "", 0, "", "",
                    "", "", "", 0, ""
            );
        }
    }
    
    public record shs_det(String[] m, String shs_nbr, String shs_so, String shs_desc, 
        String shs_type, String shs_amttype, String shs_amt ) {
        public shs_det(String[] m) {
            this(m, "", "", "", "", "", ""
            );
        }
    }

    public record ship_ctrl (String[] m, String shc_confirm, String shc_custitemonly) {
        public ship_ctrl(String[] m) {
            this(m,"","");
        }
    }

    public record ship_tree(String[] m, String ship_parent, String ship_child, String ship_site, 
        String ship_type, String ship_sh, String ship_shline, String ship_so, String ship_soline,
        String ship_po, String ship_item, Double ship_qty, String ship_serial) {
        public ship_tree(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    0.0, "" );
        }
    }
    
    public record sh_meta(String[] m, String shm_id, String shm_type, String shm_key, String shm_value) {
        public sh_meta(String[] m) {
            this(m, "", "", "", "");
        }
    }

    
    
}
