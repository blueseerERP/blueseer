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
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.dfdate;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import com.blueseer.ctr.cusData.cm_mstr;
import com.blueseer.fap.fapData;
import static com.blueseer.fap.fapData.VouchAndPayTransaction;
import static com.blueseer.fap.fapData.VoucherTransaction;
import static com.blueseer.fap.fapData._VouchAndPayTransaction;
import static com.blueseer.fap.fapData._VoucherTransaction;
import com.blueseer.fap.fapData.vod_mstr;
import com.blueseer.fgl.fglData;
import static com.blueseer.fgl.fglData.glEntryXP;
import com.blueseer.ord.ordData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateFormat;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.AREntry;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.vdr.venData.getVendInfo;
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
                    + " sh_cust_terms, sh_taxcode, sh_ar_acct, sh_ar_cc) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.sh_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.sh_id);
            ps.setString(2, x.sh_cust);
            ps.setString(3, x.sh_ship);
            ps.setString(4, x.sh_pallets);
            ps.setString(5, x.sh_boxes);
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
            rows = ps.executeUpdate();
            } 
            return rows;
    }
   
    private static int _addShipDet(ship_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ship_det where shd_id = ? and shd_line = ?";
        String sqlInsert = "insert into ship_det (shd_id, shd_line, shd_item, shd_so, shd_soline, shd_date, shd_po, shd_qty, shd_curr, shd_uom, "
                        + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, shd_loc, shd_taxamt, shd_cont, shd_serial, shd_site, shd_bom ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.shd_id);
          ps.setString(2, x.shd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.shd_id);
            ps.setString(2, x.shd_line);
            ps.setString(3, x.shd_item);
            ps.setString(4, x.shd_so);
            ps.setString(5, x.shd_soline);
            ps.setString(6, x.shd_date);
            ps.setString(7, x.shd_po);
            ps.setString(8, x.shd_qty);
            ps.setString(9, x.shd_curr);
            ps.setString(10, x.shd_uom);
            ps.setString(11, x.shd_netprice);
            ps.setString(12, x.shd_disc);
            ps.setString(13, x.shd_listprice);
            ps.setString(14, x.shd_desc);
            ps.setString(15, x.shd_wh);
            ps.setString(16, x.shd_loc);
            ps.setString(17, x.shd_taxamt);
            ps.setString(18, x.shd_cont);
            ps.setString(19, x.shd_serial);
            ps.setString(20, x.shd_site);
            ps.setString(21, x.shd_bom);
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
   
    public static String[] addShipperTransaction(ArrayList<ship_det> shd, ship_mstr sh) {
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
    
    public static String[] confirmShipperTransaction(String type, String shipper, Date effdate) {
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
           
            _addTranMstrShipper(shipper, effdate, bscon);
            _updateInventoryFromShipper(shipper, bscon);
            fglData._glEntryFromShipper(shipper, effdate, bscon);
            
            _updateShipperStatus(shipper, effdate, bscon); 
            if (type.equals("order")) {
            _updateOrderFromShipper(shipper, bscon); 
            }
            if (type.equals("serviceorder")) {
            _updateServiceOrderFromShipper(shipper, bscon); 
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
    
    
    private static int _updateShipMstr(ship_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update ship_mstr set " 
                + " sh_shipdate = ?, sh_ref = ?, sh_rmks = ?, "
                + "sh_shipvia = ?, sh_pallets = ?, sh_boxes = ? "
                + " where sh_id = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(7, x.sh_id);
            ps.setString(1, x.sh_shipdate);
            ps.setString(2, x.sh_ref);
            ps.setString(3, x.sh_rmks);
            ps.setString(4, x.sh_shipvia);
            ps.setString(5, x.sh_pallets);
            ps.setString(6, x.sh_boxes);
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
                " shd_site = ?, shd_bom = ?" +
                 " where shd_id = ? and shd_line = ? ; ";
        String sqlInsert = "insert into ship_det (shd_id, shd_line, shd_item, shd_so, shd_soline, " 
                        + " shd_date, shd_po, shd_qty,"
                        + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, "
                        + " shd_loc, shd_taxamt, shd_cont, shd_serial, shd_site, shd_bom  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.shd_id);
        ps.setString(2, x.shd_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.shd_id);
            ps.setString(2, x.shd_line);
            ps.setString(3, x.shd_item);
            ps.setString(4, x.shd_so);
            ps.setString(5, x.shd_soline);
            ps.setString(6, x.shd_date);
            ps.setString(7, x.shd_po);
            ps.setString(8, x.shd_qty);
            ps.setString(9, x.shd_netprice);
            ps.setString(10, x.shd_disc);
            ps.setString(11, x.shd_listprice);
            ps.setString(12, x.shd_desc);
            ps.setString(13, x.shd_wh);
            ps.setString(14, x.shd_loc);
            ps.setString(15, x.shd_taxamt);
            ps.setString(16, x.shd_cont);
            ps.setString(17, x.shd_serial);
            ps.setString(18, x.shd_site); 
            ps.setString(19, x.shd_bom); 
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(18, x.shd_id);
            ps.setString(19, x.shd_line);
            ps.setString(1, x.shd_item);
            ps.setString(2, x.shd_so);
            ps.setString(3, x.shd_soline);
            ps.setString(4, x.shd_date);
            ps.setString(5, x.shd_po);
            ps.setString(6, x.shd_qty);
            ps.setString(7, x.shd_netprice);
            ps.setString(8, x.shd_disc);
            ps.setString(9, x.shd_listprice);
            ps.setString(10, x.shd_desc);
            ps.setString(11, x.shd_wh);
            ps.setString(12, x.shd_loc);
            ps.setString(13, x.shd_taxamt);
            ps.setString(14, x.shd_cont);
            ps.setString(15, x.shd_serial);
            ps.setString(16, x.shd_site); 
            ps.setString(17, x.shd_bom);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    public static String[] updateShipTransaction(String x, ArrayList<String> lines, ArrayList<ship_det> shd, ship_mstr sh) {
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
               _deleteShipperLines(x, line, bscon);  // discard unwanted lines
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
    
    private static void _deleteShipperLines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from ship_det where shd_id = ? and shd_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
    }
    
    
    public static ship_mstr createShipMstrJRT(String nbr, String site, String bol, String billto, String shipto, String so, String po, String ref, String shipdate, String orddate, String remarks, String shipvia, String shiptype, String taxcode) {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        cm_mstr cm = cusData.getCustMstr(new String[]{billto});
        String acct = cm.cm_ar_acct();
        String cc = cm.cm_ar_cc();
        String terms = cm.cm_terms();
        String carrier = cm.cm_carrier();
        String onhold = cm.cm_onhold();
        if (taxcode == null || taxcode.isEmpty()) {
        taxcode = cm.cm_tax_code();
        }
        String curr = cm.cm_curr();
        // logic for asset type shipment/sale
        if (shiptype.equals("A")) {
            terms = "N00";
        }
        // override default cust carrier from inbound shipvia variable
        if (! shipvia.isEmpty()) {
            carrier = shipvia;
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
                "0", // pallets
                "0",  // boxes
                carrier,  
                shipdate,
                orddate,
                so,
                po,
                remarks,
                bsmf.MainFrame.userid,
                site,
                curr,
                "", // warehouse
                terms,
                taxcode,
                acct,
                cc );
                
        return x;        
    }
    
    public static ArrayList<ship_det> createShipDetJRT(ArrayList<String[]> detail, String shippernbr, String shipdate, String site) {
        ArrayList<ship_det> list = new ArrayList<ship_det>();
        for (String[] d : detail) {            
            // field order:  "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc", "Taxamt"
            // service order field order:  line, item, type, desc, order, qty, price, uom
            ship_det x = new ship_det(null,
                  shippernbr,
                  d[0], //shline
                  d[1], //item
                  d[2], //custitem
                  d[3], // so
                  d[0], // soline = shline  
                  shipdate, //shipdate
                  d[4], // po
                  d[5].replace(defaultDecimalSeparator, '.'), // qty
                  d[6], // uom
                  "", // currency  
                  d[9].replace(defaultDecimalSeparator, '.'), // netprice
                  d[8].replace(defaultDecimalSeparator, '.'), // disc
                  d[7].replace(defaultDecimalSeparator, '.'), // listprice
                  d[14], // desc
                  d[12], // wh
                  d[13], // loc
                  d[15].replace(defaultDecimalSeparator, '.'), // taxamt
                  "", // cont
                  "", // ref
                  "", // serial
                  site,
                  d[16] // bom
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
                  d[0], //shline
                  d[1], //item
                  d[1], //custitem
                  d[4], // so
                  d[0], // soline = shline  
                  shipdate, //shipdate
                  d[4], // po
                  d[5].replace(defaultDecimalSeparator, '.'), // qty
                  d[7], // uom
                  "", // currency  
                  d[6].replace(defaultDecimalSeparator, '.'), // netprice
                  "0", // disc
                  d[6].replace(defaultDecimalSeparator, '.'), // listprice
                  d[3], // desc
                  "", // wh
                  "", // loc
                  "0", // taxamt
                  "", // cont
                  "", // ref
                  "", // serial
                  site,
                  "" // bom
                  );
          list.add(x);
        }
        return list;
    }
    
    
    public static String[] addUpdateSHCtrl(ship_ctrl x) {
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
    
    public static void _updateShipperStatus(String shipper, Date effdate, Connection bscon) throws SQLException {
        Statement st = bscon.createStatement();
        st.executeUpdate(
             " update ship_mstr set sh_status = '1', sh_confdate = " + "'" + BlueSeerUtils.setDateFormat(effdate) + "'" +
             " where sh_id = " + "'" + shipper + "'" + ";" );
        st.close();
   }

    public static void _updateInventoryFromShipperOld(String shipper, Connection bscon) throws SQLException {
   
            Statement st = bscon.createStatement();
            Statement st2 = bscon.createStatement();
            Statement st3 = bscon.createStatement();
            Statement st4 = bscon.createStatement();
            ResultSet res;
            ResultSet res2;
            ResultSet nres;

           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            String mydate = dfdate.format(now);
                String part = "";
                double qty = 0;
                String uom = "";
                double baseqty = 0;
                String loc = "";
                String wh = "";
                String site = "";
                String serial = "";
                String expire = "";
                double sum = 0;
                int i = 0;
                  res = st.executeQuery("select sh_site, shd_item, shd_qty, shd_uom, shd_loc, shd_wh, shd_site, shd_serial " +
                          " from ship_det inner join ship_mstr on sh_id = shd_id  " +
                          " where shd_id = " + "'" + shipper + "'" +";");
                while (res.next()) {
                    i = 0;
                    part = res.getString("shd_item");
                    qty = res.getDouble("shd_qty");
                    uom = res.getString("shd_uom");
                    loc = res.getString("shd_loc");
                    wh = res.getString("shd_wh");
                    site = res.getString("sh_site");
                    serial = res.getString("shd_serial");
                    baseqty = OVData.getUOMBaseQty(part, site, uom, qty);
                  //  bsmf.MainFrame.show(baseqty + "/" + uom + "/" + qty);

                    // lets determine if this is a legitimate item or a misc item...do not inventory misc items
                    res2 = st4.executeQuery("select it_item, it_loc, it_wh, it_code " +
                          " from  item_mstr  " +
                          " where it_item = " + "'" + part + "'" + ";");

                    while (res2.next()) {
                        // if item type 'S' service....then continue
                        if (res2.getString("it_code").equals("S")) {
                          continue;
                        }

                        i++;
                        // if no loc in shipper then grab the item default loc
                        if (loc.isEmpty())
                           loc = res2.getString("it_loc");
                         // if no loc in shipper then grab the item default loc
                        if (wh.isEmpty())
                           wh = res2.getString("it_wh");
                    }
                    // if no item_mstr then continue loop...must be miscellaneous item
                    if (i == 0) {
                        continue;
                    }



                    // check if in_mstr record exists for this part,loc,site combo
                    // if not add it
                    int z = 0;
                    double qoh = 0.00;
                    nres = st2.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + part + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"      
                            + ";");

                    while (nres.next()) {
                        z++;
                        qoh = bsParseDouble(nres.getString("in_qoh"));
                    }
                    nres.close();


                    if (z == 0) {
                     sum = (-1 * baseqty);
                     st3.executeUpdate("insert into in_mstr "
                            + "(in_site, in_item, in_loc, in_wh, in_serial, in_expire, in_qoh, in_date ) "
                            + " values ( " 
                            + "'" + site + "'" + ","
                            + "'" + part + "'" + ","
                            + "'" + loc + "'" + ","
                            + "'" + wh + "'" + ","
                            + "'" + serial + "'" + ","
                            + "'" + expire + "'" + ","   
                            + "'" + sum + "'" + ","
                            + "'" + mydate + "'"
                            + ")"
                            + ";");

                    }  else {
                       // nres.first();
                        sum = qoh - baseqty;
                         st3.executeUpdate("update in_mstr "
                            + " set in_qoh = " + "'" + sum + "'" + "," +
                              " in_date = " + "'" + mydate + "'"
                            + " where in_item = " + "'" + part + "'" 
                            + " and in_loc = " + "'" + loc + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_site = " + "'" + site + "'"
                            + " and in_serial = " + "'" + serial + "'"             
                            + ";");
                    }
                }
     }

    public static void _updateInventoryFromShipper(String shipper, Connection bscon) throws SQLException {
   
            Statement st = bscon.createStatement();
            Statement st2 = bscon.createStatement();
            ResultSet res;

           java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            String mydate = dfdate.format(now);
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
                  res = st.executeQuery("select sh_site, shd_item, shd_qty, shd_uom, shd_loc, shd_wh, shd_site, shd_serial, shd_bom, it_loc, it_wh, it_code, it_phantom " +
                          " from ship_det inner join ship_mstr on sh_id = shd_id  " +
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
                    baseqty = OVData.getUOMBaseQty(x[0], x[5], x[2], Double.valueOf(x[1]));
                    x[12] = String.valueOf(baseqty);
                    if (x[3].isEmpty()) {x[3] = x[7];} // if no loc in shipper...use item default loc
                    if (x[4].isEmpty()) {x[4] = x[8];} // if no wh in shipper...use item default wh
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
                    OVData._updateNonSerializedInventory(bscon, item, site, wh, loc, (-1 * lineqty), mydate);
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
                              " in_date = " + "'" + mydate + "'"
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
                              " in_date = " + "'" + mydate + "'"
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
                        OVData._updateNonSerializedInventory(bscon, item, site, wh, loc, (-1 * remaining), mydate);
                    }
                   } //  serialized logic
                } // for each ship_det
     }

    
    public static void _addTranMstrShipper(String shipper, Date effdate, Connection bscon) throws SQLException {
           
        Statement st = bscon.createStatement();
        Statement st2 = bscon.createStatement();
        ResultSet res;
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String mydate = dfdate.format(now);
                
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
                    + "'" + qty + "'" + ","
                    + "'" + baseqty + "'" + ","
                    + "'" + uom + "'" + ","        
                    + "'" + mydate + "'" + ","
                    + "'" + dfdate.format(effdate) + "'" + ","
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
        String[] v = getVendInfo(si[8]);
        Date duedate = OVData.getDueDateFromTerms(parseDate(si[5]), v[5]);
        String strduedate = setDateFormat(effdate); // as default...in case no duedate terms
        if (duedate != null) {
            strduedate = setDateFormat(duedate);
        }
        String defaultsalescc = OVData.getDefaultSalesCC(); // sales cc
        String defaultshippingacct = OVData.getDefaultShippingAcct(); // shipping acct 
        
        int batchid = Integer.valueOf(shipper);
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
                        currformatDouble(Double.valueOf(s[4])).replace(defaultDecimalSeparator, '.'), // ap_amt
                        currformatDouble(Double.valueOf(s[4])).replace(defaultDecimalSeparator, '.'), // ap_base_amt
                        setDateFormat(effdate), // ap_effdate, ship_date
                        setDateFormat(effdate), // ap_entdate, ship_date
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
                        "Expense"); 
                        
                        // create vod_mstr JRT
                        fapData.vod_mstr y = new fapData.vod_mstr(null, 
                        String.valueOf(voucher),
                        shipper, // receiver
                        "1", // line
                        s[2], // item
                        "1", // qty
                        currformatDouble(Double.valueOf(s[4])).replace(defaultDecimalSeparator, '.'), //amt
                        setDateFormat(effdate), // date
                        si[8], // vendor
                        "", // ap_check 
                        defaultshippingacct,
                        defaultsalescc
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

    public static String[] getShipperHeader(String shipper) {

          String[] H = new String[14];
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


                res = st.executeQuery("select * from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
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

    public static ArrayList<String[]> getShipperLines(String shipper) {
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
                    String[] d = new String[10];
                    for (int z = 0; z < 10; z++) {
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
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       ArrayList<String> orders = new ArrayList<String>();
       ArrayList<String[]> sac = new ArrayList<String[]>();
       Double matltax = 0.00;
       Double totamt = 0.00;
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
                 myamttype = s[3].toString();
                 myamt = bsParseDoubleUS(s[4].toString());

                 // adjust if percent based
                 if (s[3].toString().equals("percent") && bsParseDoubleUS(s[4].toString()) > 0) {
                   myamttype = "amount";
                   if (s[2].equals("discount")) {
                     myamt = -1 * (bsParseDoubleUS(s[4].toString()) / 100) * totamt;
                   } else {
                     myamt = (bsParseDoubleUS(s[4].toString()) / 100) * totamt;  
                   }
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
                 st.executeUpdate(" insert into shs_det (shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt ) " +
                                 " values ( "  + "'" + shipper + "'" + "," +
                                 "'" + "" + "'" + "," +
                                 "'" + getGlobalProgTag("matltax") + "'" + "," +
                                 "'" + "tax" + "'" + "," +
                                 "'" + "amount" + "'" + "," +
                                 "'" + currformatDoubleUS(matltax) + "'" + 
                                 ") ;");
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
            if (orddate.isEmpty() || orddate == null) {
                orddate = dfdate.format(now);
            }
            if (shipdate.isEmpty() || shipdate == null) {
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
                    + " sh_shipdate, sh_po_date, sh_bol, sh_po, sh_ref, sh_rmks, sh_userid, sh_site, sh_curr, sh_shipvia, sh_cust_terms, sh_taxcode, sh_ar_acct, sh_ar_cc, sh_type ) "
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
                    + "'" + shiptype + "'"
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

    
    public record ship_mstr(String[] m, String sh_id, String sh_cust, String sh_ship, String sh_pallets, 
        String sh_boxes, String sh_shipvia, String sh_shipdate, String sh_po_date,
        String sh_ref, String sh_po, String sh_rmks, String sh_userid, String sh_site,
        String sh_curr, String sh_wh, String sh_cust_terms, String sh_taxcode,
        String sh_ar_acct, String sh_ar_cc ) {
         public ship_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", ""
                    );
        }
    }
   
    public record ship_det(String[] m, String shd_id, String shd_line, String shd_item, String shd_custitem, String shd_so,
        String shd_soline, String shd_date, String shd_po, String shd_qty, String shd_uom, String shd_curr,
        String shd_netprice, String shd_disc, String shd_listprice, String shd_desc, 
        String shd_wh, String shd_loc, String shd_taxamt, String shd_cont, String shd_ref,
        String shd_serial, String shd_site, String shd_bom) {
        public ship_det(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", ""
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

}
