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
package com.blueseer.rcv;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fap.fapData;
import static com.blueseer.fap.fapData._addAPMstr;
import static com.blueseer.fap.fapData._addVODMstr;
import com.blueseer.fap.fapData.ap_mstr;
import com.blueseer.fap.fapData.vod_mstr;
import com.blueseer.fgl.fglData;
import static com.blueseer.fgl.fglData._glEntryFromReceiver;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData._addTranMstr;
import static com.blueseer.inv.invData._addUpdateInMstr;
import static com.blueseer.pur.purData._updatePOFromReceiver;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.OVData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author terryva
 */
public class rcvData {
    
    private static int _addRecvMstr(recv_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from recv_mstr where rv_id = ?";
        String sqlInsert = "insert into recv_mstr (rv_id, rv_vend, "
                        + " rv_recvdate, rv_packingslip, rv_userid, rv_site, "
                        + " rv_terms, rv_ap_acct, rv_ap_cc) "
                        + " values (?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.rv_id);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.rv_id);
            ps.setString(2, x.rv_vend);
            ps.setString(3, x.rv_recvdate);
            ps.setString(4, x.rv_packingslip);
            ps.setString(5, x.rv_userid);
            ps.setString(6, x.rv_site);
            ps.setString(7, x.rv_terms);
            ps.setString(8, x.rv_ap_acct);
            ps.setString(9, x.rv_ap_cc);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
   
    private static int _addRecvDet(recv_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from recv_det where rvd_id = ? and rvd_rline = ?";
        String sqlInsert = "insert into recv_det (rvd_id, rvd_rline, rvd_item, rvd_po, "
                            + " rvd_poline, rvd_qty, rvd_uom, "
                            + "rvd_listprice, rvd_disc, rvd_netprice,  "
                            + " rvd_loc, rvd_wh, rvd_serial, rvd_lot, rvd_cost, rvd_site, " 
                            + " rvd_packingslip, rvd_date  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.rvd_id);
          ps.setInt(2, x.rvd_rline);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.rvd_id);
            ps.setInt(2, x.rvd_rline);
            ps.setString(3, x.rvd_item);
            ps.setString(4, x.rvd_po);
            ps.setInt(5, x.rvd_poline);
            ps.setDouble(6, x.rvd_qty);
            ps.setString(7, x.rvd_uom);
            ps.setDouble(8, x.rvd_listprice);
            ps.setDouble(9, x.rvd_disc);
            ps.setDouble(10, x.rvd_netprice);
            ps.setString(11, x.rvd_loc);
            ps.setString(12, x.rvd_wh);
            ps.setString(13, x.rvd_serial);
            ps.setString(14, x.rvd_lot);
            ps.setDouble(15, x.rvd_cost);
            ps.setString(16, x.rvd_site);
            ps.setString(17, x.rvd_packingslip);
            ps.setString(18, x.rvd_date);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
   
    public static String[] addReceiverTransaction(ArrayList<recv_det> rvd, recv_mstr rv, ap_mstr ap, ArrayList<vod_mstr> vod) {
        // if auto-vouchering....ap and vod should not be null...otherwise pass null,null as 3rd,4th parameter
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
            _addRecvMstr(rv, bscon, ps, res);  
            for (recv_det z : rvd) {
                _addRecvDet(z, bscon, ps, res);
            }
            
            // tran_mstr
            _addTranMstrReceiver(rv,rvd,bscon);
           
            // inventory
            _updateInventoryFromReceiver(rv,rvd,bscon);
            
            // GL
            _glEntryFromReceiver(rv,rvd,bscon); 
            
            // update PO
            _updatePOFromReceiver(rv.rv_id(),bscon);
            
            // if auto-vouchering....ap and vod should not be null...otherwise pass null,null to method
            if (ap != null && vod != null) {
                 _addAPMstr(ap, bscon, ps, res);  
                for (vod_mstr z : vod) {
                    _addVODMstr(z, bscon, ps, res);
                }
            
                if (ap.ap_subtype().equals("Receipt")) {
                fglData._glEntryFromVoucher(ap, bscon, false); 
                } else {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr(), parseDate(ap.ap_effdate()), bscon, false);    
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
        
    public static void _addTranMstrReceiver(recv_mstr rv, ArrayList<recv_det> rvd, Connection bscon) throws SQLException {
       
        double baseqty = 0.00;
        
        for (recv_det z : rvd) {
        
            baseqty = OVData.getUOMBaseQty(z.rvd_item(), rv.rv_site(), z.rvd_uom(), z.rvd_qty());
            
        invData.tran_mstr tm = new invData.tran_mstr(null,
                "", // id
                rv.rv_id(), // site
                z.rvd_item(),
                z.rvd_qty(),
                setDateDB(new java.util.Date()), //entdate
                setDateDB(parseDate(rv.rv_recvdate())), //effdate
                rv.rv_userid(), //userid
                rv.rv_ref(), //ref
                rv.rv_vend(), //addrcode
                "RCT-PURCH", //type
                null, //datetime
                rv.rv_rmks(), //remarks
                rv.rv_id(), //tr_nbr
                "", //misc1
                z.rvd_lot(), //lot
                z.rvd_serial(), //serial
                "RecvMaint", //program
                0, //amt
                0, //mtl
                0, //lbr
                0, //bdn
                0, //ovh
                0, //out
                "", //batch
                "", //op
                z.rvd_loc(), //loc
                z.rvd_wh(), //wh
                null, //expire
                rv.rv_ap_cc(), //cc
                "", //wc
                "", //wf
                "", //prodline
                "0", //timestamp ; db assigned
                "", //cell
                0, //export
                z.rvd_po(), //order
                z.rvd_rline(), //line
                "", //po
                z.rvd_netprice(), //price
                z.rvd_cost(), //cost
                rv.rv_ap_acct(), //acct
                "", //terms
                "", //pack
                "", //curr
                null, //packdate
                null, //assydate
                z.rvd_uom(), //uom
                baseqty, //baseqty
                "" //bom
        );
       
        _addTranMstr(tm, bscon); 
        
        } // rvd loop                 
    }

    public static void _updateInventoryFromReceiver(recv_mstr rv, ArrayList<recv_det> rvd, Connection bscon) throws SQLException {
       boolean isInventorySerialized = (OVData.isInvCtrlSerialize()) ? true : false;
        
        Date expire = new java.util.Date();
        double baseqty = 0.00;
        String serial;

        for (recv_det z : rvd) {

            baseqty = OVData.getUOMBaseQty(z.rvd_item(), rv.rv_site(), z.rvd_uom(), z.rvd_qty());
            serial = z.rvd_serial();
                    
                    // check for serialized inventory flag...if not...prevent serial from entry into in_mstr
                    if (! OVData.isInvCtrlSerialize()) {
                        serial = "";
                        expire = null;
                    } 

                    invData.in_mstr in = new invData.in_mstr(null,
                        z.rvd_item(),
                        baseqty,
                        null, // date
                        z.rvd_loc(),
                        z.rvd_wh(),
                        rv.rv_site(),
                        z.rvd_serial(),
                        setDateDB(expire),
                        rv.rv_userid(),
                        "" // prog
                    );
                    
                    _addUpdateInMstr(in, isInventorySerialized, bscon);
                }
     
     }
    
    
    private static int _updateRecvMstr(recv_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update recv_mstr set " 
                + " rv_packingslip = ?, rv_recvdate = ? "
                + " where rv_id = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(3, x.rv_id);
            ps.setString(1, x.rv_packingslip);
            ps.setString(2, x.rv_recvdate);
            rows = ps.executeUpdate();
        return rows;
    }
    
    private static int _updateRecvDet(recv_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from recv_det where rvd_id = ? and rvd_rline = ?";
        String sqlUpdate = "update recv_det set rvd_item = ?, rvd_po = ?, " +
                "rvd_poline = ?, rvd_qty = ?, rvd_uom = ?, rvd_listprice = ?, rvd_disc = ?, " +
                " rvd_netprice = ?, rvd_loc = ?, rvd_wh = ?, rvd_serial = ?, rvd_lot = ?, " +
                "rvd_cost = ?, rvd_site = ?, rvd_packingslip = ?, rvd_date = ? " +
                 " where rvd_id = ? and rvd_rline = ? ; ";
        String sqlInsert = "insert into recv_det (rvd_id, rvd_rline, rvd_item, rvd_po, rvd_poline, "
                            + "rvd_qty, rvd_uom, rvd_listprice, rvd_disc, rvd_netprice,  "
                            + " rvd_loc, rvd_wh, rvd_serial, rvd_lot, rvd_cost, rvd_site, "
                            + " rvd_packingslip, rvd_date  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.rvd_id);
        ps.setInt(2, x.rvd_rline);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(17, x.rvd_id);
            ps.setInt(18, x.rvd_rline);
            ps.setString(1, x.rvd_item);
            ps.setString(2, x.rvd_po);
            ps.setInt(3, x.rvd_poline);
            ps.setDouble(4, x.rvd_qty);
            ps.setString(5, x.rvd_uom);
            ps.setDouble(6, x.rvd_listprice);
            ps.setDouble(7, x.rvd_disc);
            ps.setDouble(8, x.rvd_netprice);
            ps.setString(9, x.rvd_loc);
            ps.setString(10, x.rvd_wh);
            ps.setString(11, x.rvd_serial);
            ps.setString(12, x.rvd_lot);
            ps.setDouble(13, x.rvd_cost);
            ps.setString(14, x.rvd_site);
            ps.setString(15, x.rvd_packingslip);
            ps.setString(16, x.rvd_date); 
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(1, x.rvd_id);
            ps.setInt(2, x.rvd_rline);
            ps.setString(3, x.rvd_item);
            ps.setString(4, x.rvd_po);
            ps.setInt(5, x.rvd_poline);
            ps.setDouble(6, x.rvd_qty);
            ps.setString(7, x.rvd_uom);
            ps.setDouble(8, x.rvd_listprice);
            ps.setDouble(9, x.rvd_disc);
            ps.setDouble(10, x.rvd_netprice);
            ps.setString(11, x.rvd_loc);
            ps.setString(12, x.rvd_wh);
            ps.setString(13, x.rvd_serial);
            ps.setString(14, x.rvd_lot);
            ps.setDouble(15, x.rvd_cost);
            ps.setString(16, x.rvd_site);
            ps.setString(17, x.rvd_packingslip);
            ps.setString(18, x.rvd_date); 
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    public static String[] updateReceiverTransaction(String x, ArrayList<String> lines, ArrayList<recv_det> rvd, recv_mstr rv) {
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
               _deleteReceiverLines(x, line, bscon);  // discard unwanted lines
             }
            for (recv_det z : rvd) {
                _updateRecvDet(z, bscon, ps, res);
            }
             _updateRecvMstr(rv, bscon, ps, res);  // update so_mstr
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
    
    private static void _deleteReceiverLines(String x, String line, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from recv_det where rvd_id = ? and rvd_rline = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
    }
    
    public static Receiver getReceiverMstrSet(String[] x ) {
        Receiver r = null;
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
            
            recv_mstr rv = _getReceiverMstr(x, bscon, ps, res);
            ArrayList<recv_det> rvd = _getReceiverDet(x, bscon, ps, res);
            
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new Receiver(m, rv, rvd);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new Receiver(m);
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
    
    private static recv_mstr _getReceiverMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        recv_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from recv_mstr where rv_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new recv_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new recv_mstr(m, res.getString("rv_id"), res.getString("rv_vend"), res.getString("rv_recvdate"),
                    res.getString("rv_status"), res.getString("rv_packingslip"), res.getString("rv_userid"), 
                    res.getString("rv_ap_acct"), res.getString("rv_ap_cc"), res.getString("rv_terms"), 
                    res.getString("rv_site"), res.getString("rv_confdate"), res.getString("rv_ref"), 
                    res.getString("rv_rmks"));
                }
            }
            return r;
    }
    
    private static ArrayList<recv_det> _getReceiverDet(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<recv_det> list = new ArrayList<recv_det>();
        recv_det r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from recv_det where rvd_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new recv_det(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new recv_det(m, res.getString("rvd_id"), res.getString("rvd_po"), res.getInt("rvd_poline"),
                    res.getString("rvd_packingslip"), res.getString("rvd_item"), res.getDouble("rvd_qty"), res.getString("rvd_date"), res.getDouble("rvd_listprice"),
                    res.getDouble("rvd_netprice"), res.getDouble("rvd_disc"), res.getString("rvd_lot"), res.getString("rvd_wh"), res.getString("rvd_serial"),
                    res.getString("rvd_loc"), res.getString("rvd_jobnbr"), res.getString("rvd_site"), res.getString("rvd_status"), 
                    res.getInt("rvd_rline"), res.getDouble("rvd_voqty"), res.getDouble("rvd_cost"), res.getString("rvd_uom") );
                    list.add(r);
                    }
            }
            return list;
    }
    
    
    // misc functions
    public static ArrayList<String[]> getReceiverInit() {
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
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "sites";
               s[1] = res.getString("site_site");
               lines.add(s);
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
            
          
            
            res = st.executeQuery("select vd_addr from vd_mstr order by vd_addr ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "vendors";
               s[1] = res.getString("vd_addr");
               lines.add(s);
            }
            
            
            res = st.executeQuery("select apc_autovoucher from ap_ctrl;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "voucher";
               s[1] = res.getString("apc_autovoucher");
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
    
    
    
    public static boolean isReceived(String x) {
           boolean r = false;
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
                
                res = st.executeQuery("select rvd_id, rvd_rline, rvd_item from recv_det where rvd_id = " + "'" + x + "'" + 
                                      " and rvd_voqty > 0 " + ";");
                int i = 0;
                while (res.next()) {
                    i++;              
                }
                if (i > 0)
                    r = true;
               
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
    
    public static ArrayList<String> getReceiverLines(String x) {
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
           res = st.executeQuery("SELECT rvd_rline from recv_det " +
                   " where rvd_id = " + "'" + x + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("rvd_rline"));
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
        return lines;
    }
    
    public record Receiver(String[] m, recv_mstr rv, ArrayList<recv_det> rvd) {
        public Receiver(String[] m) {
            this (m, null, null);
        }
    }
    
    
    public record recv_mstr(String[] m, String rv_id, String rv_vend, String rv_recvdate, 
        String rv_status, String rv_packingslip, String rv_userid, String rv_ap_acct, 
        String rv_ap_cc, String rv_terms, String rv_site, 
        String rv_confdate, String rv_ref, String rv_rmks ) {
         public recv_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", ""
                    );
        }
    }
   
    public record recv_det(String[] m, String rvd_id, String rvd_po, int rvd_poline, 
        String rvd_packingslip, String rvd_item, double rvd_qty, 
        String rvd_date, double rvd_listprice, double rvd_netprice, double rvd_disc, 
        String rvd_lot, String rvd_wh, String rvd_serial, String rvd_loc,
        String rvd_jobnbr, String rvd_site, String rvd_status, int rvd_rline, 
        double rvd_voqty, double rvd_cost, String rvd_uom ) {
         public recv_det(String[] m) {
            this(m, "", "", 0, "", "", 0, "", 0, 0, 0,
                    "", "", "", "", "", "", "", 0, 0, 0,
                    ""
                    );
        }
    }
}
