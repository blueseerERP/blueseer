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
package com.blueseer.frt;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
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
public class frtData {
    
    
    public static String[] addCarrierMstr(car_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  car_mstr where car_id = ?";
        String sqlInsert = "insert into car_mstr (car_id, car_desc, car_apply, car_scac, car_name, car_line1, car_line2, car_city, " +
"        car_state, car_zip, car_country, car_phone, car_email, " +
"        car_type, car_acct, car_usdot, car_mc, car_ein, " +
"        car_minmiles, car_maxmiles, car_maxdh, car_milerate, car_tractors, car_trailers ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.car_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.car_id);
            psi.setString(2, x.car_desc);
            psi.setString(3, x.car_apply);
            psi.setString(4, x.car_scac);
            psi.setString(5, x.car_name);
            psi.setString(6, x.car_line1);
            psi.setString(7, x.car_line2);
            psi.setString(8, x.car_city);
            psi.setString(9, x.car_state);
            psi.setString(10, x.car_zip);
            psi.setString(11, x.car_country);
            psi.setString(12, x.car_phone);
            psi.setString(13, x.car_email);
            psi.setString(14, x.car_type);
            psi.setString(15, x.car_acct);
            psi.setString(16, x.car_usdot);
            psi.setString(17, x.car_mc);
            psi.setString(18, x.car_ein);
            psi.setString(19, x.car_minmiles);
            psi.setString(20, x.car_maxmiles);
            psi.setString(21, x.car_maxdh);
            psi.setString(22, x.car_milerate);
            psi.setString(23, x.car_tractors);
            psi.setString(24, x.car_trailers);
        
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

    public static String[] updateCarrierMstr(car_mstr x) {
        String[] m = new String[2];
        String sql = "update car_mstr set car_desc = ?, car_apply = ?, car_scac = ?, car_name = ?, " +   
                     " car_line1 = ?, car_line2 = ?, car_city = ?, car_state = ?, car_zip = ?, car_country = ?, " +
                     " car_phone = ?, car_email = ?, car_type = ?, car_acct = ?, car_usdot = ?, car_mc = ?, " +
                     " car_ein = ?, car_minmiles = ?, car_maxmiles = ?, car_maxdh = ?, car_milerate = ?, " +
                     " car_tractors = ?, car_trailers = ? " +
                     " where car_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.car_desc);
        ps.setString(2, x.car_apply);
        ps.setString(3, x.car_scac);
        ps.setString(4, x.car_name);
        ps.setString(5, x.car_line1);
        ps.setString(6, x.car_line2);
        ps.setString(7, x.car_city);
        ps.setString(8, x.car_state);
        ps.setString(9, x.car_zip);
        ps.setString(10, x.car_country);
        ps.setString(11, x.car_phone);
        ps.setString(12, x.car_email);
        ps.setString(13, x.car_type);
        ps.setString(14, x.car_acct);
        ps.setString(15, x.car_usdot);
        ps.setString(16, x.car_mc);
        ps.setString(17, x.car_ein);
        ps.setString(18, x.car_minmiles);
        ps.setString(19, x.car_maxmiles);
        ps.setString(20, x.car_maxdh);
        ps.setString(21, x.car_milerate);
        ps.setString(22, x.car_tractors);
        ps.setString(23, x.car_trailers);
        ps.setString(24, x.car_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static car_mstr getCarrierMstr(String[] x) {
        car_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from car_mstr where car_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new car_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new car_mstr(m, res.getString("car_id"), 
                            res.getString("car_desc"),
                            res.getString("car_apply"),
                            res.getString("car_scac"),
                            res.getString("car_name"),
                            res.getString("car_line1"),
                            res.getString("car_line2"),
                            res.getString("car_city"),
                            res.getString("car_state"),
                            res.getString("car_zip"),
                            res.getString("car_country"), 
                            res.getString("car_phone"),
                            res.getString("car_email"),
                            res.getString("car_type"),
                            res.getString("car_acct"),
                            res.getString("car_usdot"),
                            res.getString("car_mc"),
                            res.getString("car_ein"),
                            res.getString("car_minmiles"),
                            res.getString("car_maxmiles"),
                            res.getString("car_maxdh"),
                            res.getString("car_milerate"),
                            res.getString("car_tractors"),
                            res.getString("car_trailers")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new car_mstr(m);
        }
        return r;
    }
    
    public static String[] deleteCarrierMstr(car_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from car_mstr where car_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.car_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static ArrayList<String[]> getCarrierMaintInit() {
       
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
    
    public static String[] addCFOTransaction(ArrayList<cfo_det> cfod, cfo_mstr cfo, ArrayList<cfo_item> cfoi) {
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
            _addCFOMstr(cfo, bscon, ps, res);  
            for (cfo_det z : cfod) {
                _addCFODet(z, bscon, ps, res);
            }
            if (cfoi != null) {
                for (cfo_item z : cfoi) {
                    _addCFOItem(z, bscon, ps, res);
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
    
    private static int _addCFOMstr(cfo_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "SELECT * FROM  cfo_mstr where cfo_nbr = ?";
        String sqlInsert = "insert into cfo_mstr (cfo_nbr, cfo_cust, cfo_custfonbr, cfo_custfonbrrev, cfo_servicetype, cfo_equipmenttype, cfo_truckid, cfo_trailernbr, " +
        " cfo_orderstatus, cfo_deliverystatus, cfo_driver, cfo_drivercell, cfo_type, " +
        " cfo_brokerid, cfo_brokercontact, cfo_brokercell, cfo_ratetype, cfo_rate, " +
        " cfo_mileage, cfo_driverrate, cfo_driverstd, cfo_weight, cfo_loaddate, cfo_unloaddate, cfo_ishazmat, " +
        " cfo_miscexpense, cfo_misccharges, cfo_cost, cfo_bol, cfo_rmks) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.cfo_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cfo_nbr);
            ps.setString(2, x.cfo_cust);
            ps.setString(3, x.cfo_custfonbr);
            ps.setString(4, x.cfo_custfonbrrev);
            ps.setString(5, x.cfo_servicetype);
            ps.setString(6, x.cfo_equipmenttype);
            ps.setString(7, x.cfo_truckid);
            ps.setString(8, x.cfo_trailernbr);
            ps.setString(9, x.cfo_orderstatus);
            ps.setString(10, x.cfo_deliverystatus);
            ps.setString(11, x.cfo_driver);
            ps.setString(12, x.cfo_drivercell);
            ps.setString(13, x.cfo_type);
            ps.setString(14, x.cfo_brokerid);
            ps.setString(15, x.cfo_brokercontact);
            ps.setString(16, x.cfo_brokercell);
            ps.setString(17, x.cfo_ratetype);
            ps.setString(18, x.cfo_rate);
            ps.setString(19, x.cfo_mileage);
            ps.setString(20, x.cfo_driverrate);
            ps.setString(21, x.cfo_driverstd);
            ps.setString(22, x.cfo_weight);
            ps.setString(23, x.cfo_loaddate);
            ps.setString(24, x.cfo_unloaddate);
            ps.setString(25, x.cfo_ishazmat);
            ps.setString(26, x.cfo_miscexpense);
            ps.setString(27, x.cfo_misccharges);
            ps.setString(28, x.cfo_cost);
            ps.setString(29, x.cfo_bol);
            ps.setString(30, x.cfo_rmks);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addCFODet(cfo_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
       
        String sqlSelect = "select * from cfo_det where cfod_nbr = ? and cfod_stopline = ?";
        String sqlInsert = "insert into cfo_det (cfod_nbr, cfod_stopline, cfod_seq, cfod_type, " 
                        + "cfod_code, cfod_name, cfod_line1, cfod_line2, cfod_line3, " 
                        + "cfod_city, cfod_state, cfod_zip, cfod_country, cfod_phone, " 
                        + "cfod_email, cfod_contact, cfod_misc, cfod_rmks, "
                        + "cfod_reference, cfod_ordnum, cfod_weight, cfod_pallet, cfod_ladingqty, cfod_hazmat, "
                        + "cfod_datetype, cfod_date, cfod_timetype1, cfod_time1, cfod_timetype2, cfod_time2, cfod_timezone  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.cfod_nbr);
          ps.setString(2, x.cfod_stopline);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cfod_nbr);
            ps.setString(2, x.cfod_stopline);
            ps.setString(3, x.cfod_seq);
            ps.setString(4, x.cfod_type);
            ps.setString(5, x.cfod_code);
            ps.setString(6, x.cfod_name);
            ps.setString(7, x.cfod_line1);
            ps.setString(8, x.cfod_line2);
            ps.setString(9, x.cfod_line3);
            ps.setString(10, x.cfod_city);
            ps.setString(11, x.cfod_state);
            ps.setString(12, x.cfod_zip);
            ps.setString(13, x.cfod_country);
            ps.setString(14, x.cfod_phone);
            ps.setString(15, x.cfod_email);
            ps.setString(16, x.cfod_contact);
            ps.setString(17, x.cfod_misc);
            ps.setString(18, x.cfod_rmks);
            ps.setString(19, x.cfod_reference);
            ps.setString(20, x.cfod_ordnum);
            ps.setString(21, x.cfod_weight);
            ps.setString(22, x.cfod_pallet);
            ps.setString(23, x.cfod_ladingqty);
            ps.setString(24, x.cfod_hazmat);
            ps.setString(25, x.cfod_datetype);
            ps.setString(26, x.cfod_date);
            ps.setString(27, x.cfod_timetype1);
            ps.setString(28, x.cfod_time1);
            ps.setString(29, x.cfod_timetype2);
            ps.setString(30, x.cfod_time2);
            ps.setString(31, x.cfod_timezone);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addCFOItem(cfo_item x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
       /*
        String cfoi_nbr, String cfoi_stopline, String cfoi_itemline,
        String cfoi_item, String cfoi_itemdesc, String cfoi_order, 
        String cfoi_qty, String cfoi_pallets, String cfoi_weight,
        String cfoi_ref, String cfoi_rmks
        */
        String sqlSelect = "select * from cfo_item where cfoi_nbr = ? and cfoi_stopline = ? and cfoi_itemline = ?";
        String sqlInsert = "insert into cfo_item (cfoi_nbr, cfoi_stopline, cfoi_itemline, cfoi_item, " 
                        + "cfoi_itemdesc, cfoi_order, cfoi_qty, cfoi_pallets, cfoi_weight, " 
                        + "cfoi_ref, cfoi_rmks ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.cfoi_nbr);
          ps.setString(2, x.cfoi_stopline);
          ps.setString(3, x.cfoi_itemline);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cfoi_nbr);
            ps.setString(2, x.cfoi_stopline);
            ps.setString(3, x.cfoi_itemline);
            ps.setString(4, x.cfoi_item);
            ps.setString(5, x.cfoi_itemdesc);
            ps.setString(6, x.cfoi_order);
            ps.setString(7, x.cfoi_qty);
            ps.setString(8, x.cfoi_pallets);
            ps.setString(9, x.cfoi_weight);
            ps.setString(10, x.cfoi_ref);
            ps.setString(11, x.cfoi_rmks);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    
    public static String[] addCFOMstr(cfo_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  cfo_mstr where cfo_nbr = ?";
        String sqlInsert = "insert into cfo_mstr (cfo_nbr, cfo_cust, cfo_custfonbr, cfo_custfonbrrev, cfo_servicetype, cfo_equipmenttype, cfo_truckid, cfo_trailernbr, " +
        " cfo_orderstatus, cfo_deliverystatus, cfo_driver, cfo_drivercell, cfo_type, " +
        " cfo_brokerid, cfo_brokercontact, cfo_brokercell, cfo_ratetype, cfo_rate, " +
        " cfo_mileage, cfo_driverrate, cfo_driverstd, cfo_weight, cfo_loaddate, cfo_unloaddate, cfo_ishazmat, " +
        " cfo_miscexpense, cfo_misccharges, cfo_cost, cfo_bol, cfo_rmks) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.cfo_nbr);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.cfo_nbr);
            psi.setString(2, x.cfo_cust);
            psi.setString(3, x.cfo_custfonbr);
            psi.setString(4, x.cfo_custfonbrrev);
            psi.setString(5, x.cfo_servicetype);
            psi.setString(6, x.cfo_equipmenttype);
            psi.setString(7, x.cfo_truckid);
            psi.setString(8, x.cfo_trailernbr);
            psi.setString(9, x.cfo_orderstatus);
            psi.setString(10, x.cfo_deliverystatus);
            psi.setString(11, x.cfo_driver);
            psi.setString(12, x.cfo_drivercell);
            psi.setString(13, x.cfo_type);
            psi.setString(14, x.cfo_brokerid);
            psi.setString(15, x.cfo_brokercontact);
            psi.setString(16, x.cfo_brokercell);
            psi.setString(17, x.cfo_ratetype);
            psi.setString(18, x.cfo_rate);
            psi.setString(19, x.cfo_mileage);
            psi.setString(20, x.cfo_driverrate);
            psi.setString(21, x.cfo_driverstd);
            psi.setString(22, x.cfo_weight);
            psi.setString(23, x.cfo_loaddate);
            psi.setString(24, x.cfo_unloaddate);
            psi.setString(25, x.cfo_ishazmat);
            psi.setString(26, x.cfo_miscexpense);
            psi.setString(27, x.cfo_misccharges);
            psi.setString(28, x.cfo_cost);
            psi.setString(29, x.cfo_bol);
            psi.setString(30, x.cfo_rmks);
        
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

    public static String[] updateCFOMstr(cfo_mstr x) {
        String[] m = new String[2];
        String sql = "update cfo_mstr set cfo_cust = ?, cfo_custfonbr = ?, cfo_custfonbrrev = ?, cfo_servicetype = ?, cfo_equipmenttype = ?, cfo_truckid = ?, cfo_trailernbr = ?, " +
        " cfo_orderstatus = ?, cfo_deliverystatus = ?, cfo_driver = ?, cfo_drivercell = ?, cfo_type = ?, " +
        " cfo_brokerid = ?, cfo_brokercontact = ?, cfo_brokercell = ?, cfo_ratetype = ?, cfo_rate = ?, " +
        " cfo_mileage = ?, cfo_driverrate = ?, cfo_driverstd = ?, cfo_weight = ?, cfo_loaddate = ?, cfo_unloaddate = ?, cfo_ishazmat = ?, " +
        " cfo_miscexpense = ?, cfo_misccharges = ?, cfo_cost = ?, cfo_bol = ?, cfo_rmks = ? " +
                     " where cfo_nbr = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.cfo_cust);
            ps.setString(2, x.cfo_custfonbr);
            ps.setString(3, x.cfo_custfonbrrev);
            ps.setString(4, x.cfo_servicetype);
            ps.setString(5, x.cfo_equipmenttype);
            ps.setString(6, x.cfo_truckid);
            ps.setString(7, x.cfo_trailernbr);
            ps.setString(8, x.cfo_orderstatus);
            ps.setString(9, x.cfo_deliverystatus);
            ps.setString(10, x.cfo_driver);
            ps.setString(11, x.cfo_drivercell);
            ps.setString(12, x.cfo_type);
            ps.setString(13, x.cfo_brokerid);
            ps.setString(14, x.cfo_brokercontact);
            ps.setString(15, x.cfo_brokercell);
            ps.setString(16, x.cfo_ratetype);
            ps.setString(17, x.cfo_rate);
            ps.setString(18, x.cfo_mileage);
            ps.setString(19, x.cfo_driverrate);
            ps.setString(20, x.cfo_driverstd);
            ps.setString(21, x.cfo_weight);
            ps.setString(22, x.cfo_loaddate);
            ps.setString(23, x.cfo_unloaddate);
            ps.setString(24, x.cfo_ishazmat);
            ps.setString(25, x.cfo_miscexpense);
            ps.setString(26, x.cfo_misccharges);
            ps.setString(27, x.cfo_cost);
            ps.setString(28, x.cfo_bol);
            ps.setString(29, x.cfo_rmks);
            ps.setString(30, x.cfo_nbr);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static cfo_mstr getCFOMstr(String[] x) {
        cfo_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from cfo_mstr where cfo_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cfo_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cfo_mstr(m, res.getString("cfo_nbr"), 
                            res.getString("cfo_cust"),
                            res.getString("cfo_custfonbr"),
                            res.getString("cfo_custfonbrrev"),
                            res.getString("cfo_servicetype"),
                            res.getString("cfo_equipmenttype"),
                            res.getString("cfo_truckid"),
                            res.getString("cfo_trailernbr"),
                            res.getString("cfo_orderstatus"),
                            res.getString("cfo_deliverystatus"),
                            res.getString("cfo_driver"), 
                            res.getString("cfo_drivercell"),
                            res.getString("cfo_type"),
                            res.getString("cfo_brokerid"),
                            res.getString("cfo_brokercontact"),
                            res.getString("cfo_brokercell"),
                            res.getString("cfo_ratetype"),
                            res.getString("cfo_rate"),
                            res.getString("cfo_mileage"),
                            res.getString("cfo_driverrate"),
                            res.getString("cfo_driverstd"),
                            res.getString("cfo_weight"),
                            res.getString("cfo_loaddate"),
                            res.getString("cfo_unloaddate"),
                            res.getString("cfo_ishazmat"),
                            res.getString("cfo_miscexpense"),
                            res.getString("cfo_misccharges"),
                            res.getString("cfo_cost"),
                            res.getString("cfo_bol"),
                            res.getString("cfo_rmks")    
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cfo_mstr(m);
        }
        return r;
    }
    
    public static ArrayList<cfo_det> getCFODet(String code) {
        cfo_det r = null;
        String[] m = new String[2];
        ArrayList<cfo_det> list = new ArrayList<cfo_det>();
        String sql = "select * from cfo_det where cfod_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cfo_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cfo_det(m, res.getString("cfod_nbr"), 
                        res.getString("cfod_stopline"), 
                        res.getString("cfod_seq"), 
                        res.getString("cfod_type"), 
                        res.getString("cfod_code"), 
                        res.getString("cfod_name"), 
                        res.getString("cfod_line1"), 
                        res.getString("cfod_line2"), 
                        res.getString("cfod_line3"), 
                        res.getString("cfod_city"), 
                        res.getString("cfod_state"), 
                        res.getString("cfod_zip"), 
                        res.getString("cfod_country"), 
                        res.getString("cfod_phone"), 
                        res.getString("cfod_email"), 
                        res.getString("cfod_contact"), 
                        res.getString("cfod_misc"), 
                        res.getString("cfod_rmks"), 
                        res.getString("cfod_reference"), 
                        res.getString("cfod_ordnum"), 
                        res.getString("cfod_weight"), 
                        res.getString("cfod_pallet"), 
                        res.getString("cfod_ladingqty"), 
                        res.getString("cfod_hazmat"), 
                        res.getString("cfod_datetype"), 
                        res.getString("cfod_date"), 
                        res.getString("cfod_timetype1"), 
                        res.getString("cfod_time1"), 
                        res.getString("cfod_timetype2"), 
                        res.getString("cfod_time2"),
                        res.getString("cfod_timezone"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cfo_det(m);
               list.add(r);
        }
        return list;
    }
    
    
    public static String[] deleteCFOMstr(cfo_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from cfo_mstr where cfo_nbr = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cfo_nbr);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    public record car_mstr (String[] m, String car_id, String car_desc, String car_apply,
        String car_scac, String car_name, String car_line1, String car_line2, String car_city,
        String car_state, String car_zip, String car_country, String car_phone, String car_email,
        String car_type, String car_acct, String car_usdot, String car_mc, String car_ein,
        String car_minmiles, String car_maxmiles, String car_maxdh, String car_milerate,
        String car_tractors, String car_trailers) {
        public car_mstr(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","");
        }
    } 
    
  
    
    public record cfo_mstr (String[] m, String cfo_nbr, String cfo_cust, String cfo_custfonbr,
        String cfo_custfonbrrev, String cfo_servicetype, String cfo_equipmenttype, String cfo_truckid, String cfo_trailernbr,
        String cfo_orderstatus, String cfo_deliverystatus, String cfo_driver, String cfo_drivercell, String cfo_type,
        String cfo_brokerid, String cfo_brokercontact, String cfo_brokercell, String cfo_ratetype, String cfo_rate,
        String cfo_mileage, String cfo_driverrate, String cfo_driverstd, String cfo_weight,
        String cfo_loaddate, String cfo_unloaddate, String cfo_ishazmat, String cfo_miscexpense,
        String cfo_misccharges, String cfo_cost, String cfo_bol, String cfo_rmks) {
        public cfo_mstr(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","","","","","","","");
        }
    } 
   
    public record cfo_det (String[] m, String cfod_nbr, String cfod_stopline, String cfod_seq, String cfod_type,
        String cfod_code, String cfod_name, String cfod_line1, String cfod_line2, String cfod_line3,
        String cfod_city, String cfod_state, String cfod_zip, String cfod_country, 
        String cfod_phone, String cfod_email, String cfod_contact, String cfod_misc,
        String cfod_rmks, String cfod_reference, String cfod_ordnum,
        String cfod_weight, String cfod_pallet, String cfod_ladingqty, String cfod_hazmat,
        String cfod_datetype, String cfod_date, String cfod_timetype1, String cfod_time1, 
        String cfod_timetype2, String cfod_time2, String cfod_timezone) {
        public cfo_det(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","","","","","","","",
                   "");
        }
    }
   
    
    public record cfo_item (String[] m, String cfoi_nbr, String cfoi_stopline, String cfoi_itemline,
        String cfoi_item, String cfoi_itemdesc, String cfoi_order, 
        String cfoi_qty, String cfoi_pallets, String cfoi_weight,
        String cfoi_ref, String cfoi_rmks) {
        public cfo_item(String[] m) {
            this(m,"","","","","","","","","","",
                   "");
        }
    }
    
    
    
}
