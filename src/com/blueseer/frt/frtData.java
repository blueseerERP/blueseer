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
    
    public static String[] addVehicleMstr(veh_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  veh_mstr where veh_id = ?";
        String sqlInsert = "insert into veh_mstr (veh_id, veh_desc, veh_type," +
        "veh_subtype,  veh_status,  veh_make,  veh_model,  veh_submodel," +
        "veh_engine,  veh_fueltype,  veh_year,  veh_vin,  veh_rmks," +
        "veh_servicedate,  veh_servicefreqdays,  veh_servicefreqmiles,  veh_odometer,  veh_odometerdate," +
        "veh_regnbr,  veh_regdate,  veh_regtax,  veh_regstate," +
        "veh_weight,  veh_condition,  veh_loc,  veh_misc1," +
        "veh_misc2,  veh_misc3, veh_inspectdate ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.veh_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.veh_id);
            psi.setString(2, x.veh_desc);
            psi.setString(3, x.veh_type);
            psi.setString(4, x.veh_subtype);
            psi.setString(5, x.veh_status);
            psi.setString(6, x.veh_make);
            psi.setString(7, x.veh_model);
            psi.setString(8, x.veh_submodel);
            psi.setString(9, x.veh_engine);
            psi.setString(10, x.veh_fueltype);
            psi.setString(11, x.veh_year);
            psi.setString(12, x.veh_vin);
            psi.setString(13, x.veh_rmks);
            psi.setString(14, x.veh_servicedate);
            psi.setString(15, x.veh_servicefreqdays);
            psi.setString(16, x.veh_servicefreqmiles);
            psi.setString(17, x.veh_odometer);
            psi.setString(18, x.veh_odometerdate);
            psi.setString(19, x.veh_regnbr);
            psi.setString(20, x.veh_regdate);
            psi.setString(21, x.veh_regtax);
            psi.setString(22, x.veh_regstate);
            psi.setString(23, x.veh_weight);
            psi.setString(24, x.veh_condition);
            psi.setString(25, x.veh_loc);
            psi.setString(26, x.veh_misc1);
            psi.setString(27, x.veh_misc2);
            psi.setString(28, x.veh_misc3);
            psi.setString(29, x.veh_inspectdate);
        
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

    public static String[] updateVehicleMstr(veh_mstr x) {
        String[] m = new String[2];
        String sql = "update veh_mstr set veh_desc = ?, veh_type = ?," +
        "veh_subtype = ?,  veh_status = ?,  veh_make = ?,  veh_model = ?,  veh_submodel = ?," +
        "veh_engine = ?,  veh_fueltype = ?,  veh_year = ?,  veh_vin = ?,  veh_rmks = ?," +
        "veh_servicedate = ?,  veh_servicefreqdays = ?,  veh_servicefreqmiles = ?,  veh_odometer = ?,  veh_odometerdate = ?," +
        "veh_regnbr = ?,  veh_regdate = ?,  veh_regtax = ?,  veh_regstate = ?," +
        "veh_weight = ?,  veh_condition = ?,  veh_loc = ?,  veh_misc1 = ?," +
        "veh_misc2 = ?,  veh_misc3 = ?, veh_inspectdate = ? " +
                     " where veh_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.veh_desc);
            ps.setString(2, x.veh_type);
            ps.setString(3, x.veh_subtype);
            ps.setString(4, x.veh_status);
            ps.setString(5, x.veh_make);
            ps.setString(6, x.veh_model);
            ps.setString(7, x.veh_submodel);
            ps.setString(8, x.veh_engine);
            ps.setString(9, x.veh_fueltype);
            ps.setString(10, x.veh_year);
            ps.setString(11, x.veh_vin);
            ps.setString(12, x.veh_rmks);
            ps.setString(13, x.veh_servicedate);
            ps.setString(14, x.veh_servicefreqdays);
            ps.setString(15, x.veh_servicefreqmiles);
            ps.setString(16, x.veh_odometer);
            ps.setString(17, x.veh_odometerdate);
            ps.setString(18, x.veh_regnbr);
            ps.setString(19, x.veh_regdate);
            ps.setString(20, x.veh_regtax);
            ps.setString(21, x.veh_regstate);
            ps.setString(22, x.veh_weight);
            ps.setString(23, x.veh_condition);
            ps.setString(24, x.veh_loc);
            ps.setString(25, x.veh_misc1);
            ps.setString(26, x.veh_misc2);
            ps.setString(27, x.veh_misc3);
            ps.setString(28, x.veh_inspectdate);
            ps.setString(29, x.veh_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static veh_mstr getVehicleMstr(String[] x) {
        veh_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from veh_mstr where veh_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new veh_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new veh_mstr(m, res.getString("veh_id"), 
                            res.getString("veh_desc"),
                            res.getString("veh_type"),
                            res.getString("veh_subtype"),
                            res.getString("veh_status"),
                            res.getString("veh_make"),
                            res.getString("veh_model"),
                            res.getString("veh_submodel"),
                            res.getString("veh_engine"),
                            res.getString("veh_fueltype"),
                            res.getString("veh_year"), 
                            res.getString("veh_vin"),
                            res.getString("veh_rmks"),
                            res.getString("veh_servicedate"),
                            res.getString("veh_servicefreqdays"),
                            res.getString("veh_servicefreqmiles"),
                            res.getString("veh_odometer"),
                            res.getString("veh_odometerdate"),
                            res.getString("veh_regnbr"),
                            res.getString("veh_regdate"),
                            res.getString("veh_regtax"),
                            res.getString("veh_regstate"),
                            res.getString("veh_weight"),
                            res.getString("veh_condition"),
                            res.getString("veh_loc"),
                            res.getString("veh_misc1"),
                            res.getString("veh_misc2"),
                            res.getString("veh_misc3"),
                            res.getString("veh_inspectdate")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new veh_mstr(m);
        }
        return r;
    }
    
    public static String[] deleteVehicleMstr(veh_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from veh_mstr where veh_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.veh_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addDriverMstr(drv_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  drv_mstr where drv_id = ?";
        String sqlInsert = "insert into drv_mstr (drv_id, drv_status, drv_lname," +
        "drv_fname,  drv_line1,  drv_line2,  drv_city,  drv_state," +
        "drv_zip,  drv_country,  drv_phone,  drv_email,  drv_type," +
        "drv_ap_acct,  drv_ap_cc,  drv_terms,  drv_certificate,  drv_licensenbr," +
        "drv_licenseexpire,  drv_insurancenbr,  drv_insuranceexpire,  drv_insurancecarrier," +
        "drv_dhmiles,  drv_rmks,  drv_payrate,  drv_paytype," +
        "drv_hiredate,  drv_termdate ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.drv_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.drv_id);
            psi.setString(2, x.drv_status);
            psi.setString(3, x.drv_lname);
            psi.setString(4, x.drv_fname);
            psi.setString(5, x.drv_line1);
            psi.setString(6, x.drv_line2);
            psi.setString(7, x.drv_city);
            psi.setString(8, x.drv_state);
            psi.setString(9, x.drv_zip);
            psi.setString(10, x.drv_country);
            psi.setString(11, x.drv_phone);
            psi.setString(12, x.drv_email);
            psi.setString(13, x.drv_type);
            psi.setString(14, x.drv_ap_acct);
            psi.setString(15, x.drv_ap_cc);
            psi.setString(16, x.drv_terms);
            psi.setString(17, x.drv_certificate);
            psi.setString(18, x.drv_licensenbr);
            psi.setString(19, x.drv_licenseexpire);
            psi.setString(20, x.drv_insurancenbr);
            psi.setString(21, x.drv_insuranceexpire);
            psi.setString(22, x.drv_insurancecarrier);
            psi.setString(23, x.drv_dhmiles);
            psi.setString(24, x.drv_rmks);
            psi.setString(25, x.drv_payrate);
            psi.setString(26, x.drv_paytype);
            psi.setString(27, x.drv_hiredate);
            psi.setString(28, x.drv_termdate);
        
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

    public static String[] updateDriverMstr(drv_mstr x) {
        String[] m = new String[2];
        String sql = "update drv_mstr set drv_status = ?, drv_lname = ?," +
        "drv_fname = ?,  drv_line1 = ?,  drv_line2 = ?,  drv_city = ?,  drv_state = ?," +
        "drv_zip = ?,  drv_country = ?,  drv_phone = ?,  drv_email = ?,  drv_type = ?," +
        "drv_ap_acct = ?,  drv_ap_cc = ?,  drv_terms = ?,  drv_certificate = ?,  drv_licensenbr = ?," +
        "drv_licenseexpire = ?,  drv_insurancenbr = ?,  drv_insuranceexpire = ?,  drv_insurancecarrier = ?," +
        "drv_dhmiles = ?,  drv_rmks = ?,  drv_payrate = ?,  drv_paytype = ?," +
        "drv_hiredate = ?,  drv_termdate = ?" +
                     " where drv_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.drv_status);
            ps.setString(2, x.drv_lname);
            ps.setString(3, x.drv_fname);
            ps.setString(4, x.drv_line1);
            ps.setString(5, x.drv_line2);
            ps.setString(6, x.drv_city);
            ps.setString(7, x.drv_state);
            ps.setString(8, x.drv_zip);
            ps.setString(9, x.drv_country);
            ps.setString(10, x.drv_phone);
            ps.setString(11, x.drv_email);
            ps.setString(12, x.drv_type);
            ps.setString(13, x.drv_ap_acct);
            ps.setString(14, x.drv_ap_cc);
            ps.setString(15, x.drv_terms);
            ps.setString(16, x.drv_certificate);
            ps.setString(17, x.drv_licensenbr);
            ps.setString(18, x.drv_licenseexpire);
            ps.setString(19, x.drv_insurancenbr);
            ps.setString(20, x.drv_insuranceexpire);
            ps.setString(21, x.drv_insurancecarrier);
            ps.setString(22, x.drv_dhmiles);
            ps.setString(23, x.drv_rmks);
            ps.setString(24, x.drv_payrate);
            ps.setString(25, x.drv_paytype);
            ps.setString(26, x.drv_hiredate);
            ps.setString(27, x.drv_termdate);
            ps.setString(28, x.drv_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static drv_mstr getDriverMstr(String[] x) {
        drv_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from drv_mstr where drv_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new drv_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new drv_mstr(m, res.getString("drv_id"), 
                            res.getString("drv_status"),
                            res.getString("drv_lname"),
                            res.getString("drv_fname"),
                            res.getString("drv_line1"),
                            res.getString("drv_line2"),
                            res.getString("drv_city"),
                            res.getString("drv_state"),
                            res.getString("drv_zip"),
                            res.getString("drv_country"),
                            res.getString("drv_phone"),
                            res.getString("drv_email"),
                            res.getString("drv_type"),
                            res.getString("drv_ap_acct"),
                            res.getString("drv_ap_cc"),
                            res.getString("drv_terms"),
                            res.getString("drv_certificate"),
                            res.getString("drv_licensenbr"),
                            res.getString("drv_licenseexpire"),
                            res.getString("drv_insurancenbr"),
                            res.getString("drv_insuranceexpire"),
                            res.getString("drv_insurancecarrier"),
                            res.getString("drv_dhmiles"),
                            res.getString("drv_rmks"),
                            res.getString("drv_payrate"),
                            res.getString("drv_paytype"),
                            res.getString("drv_hiredate"),
                            res.getString("drv_termdate")
                                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new drv_mstr(m);
        }
        return r;
    }
    
    public static String[] deleteDriverMstr(drv_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from drv_mstr where drv_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.drv_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addBrokerMstr(brk_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  brk_mstr where brk_id = ?";
        String sqlInsert = "insert into brk_mstr (brk_id, brk_status, brk_name," +
            "brk_line1, brk_line2, brk_city, brk_state, brk_zip," +
            "brk_country, brk_phone, brk_contact, brk_email, brk_type," +
            "brk_acct, brk_cc, brk_certificate, brk_payrate, brk_paytype," +
            "brk_terms, brk_bank, brk_taxid, brk_rmks) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.brk_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.brk_id);
            psi.setString(2, x.brk_status);
            psi.setString(3, x.brk_name);
            psi.setString(4, x.brk_line1);
            psi.setString(5, x.brk_line2);
            psi.setString(6, x.brk_city);
            psi.setString(7, x.brk_state);
            psi.setString(8, x.brk_zip);
            psi.setString(9, x.brk_country);
            psi.setString(10, x.brk_phone);
            psi.setString(11, x.brk_contact);
            psi.setString(12, x.brk_email);
            psi.setString(13, x.brk_type);
            psi.setString(14, x.brk_acct);
            psi.setString(15, x.brk_cc);
            psi.setString(16, x.brk_certificate);
            psi.setString(17, x.brk_payrate);
            psi.setString(18, x.brk_paytype);
            psi.setString(19, x.brk_terms);
            psi.setString(20, x.brk_bank);
            psi.setString(21, x.brk_taxid);
            psi.setString(22, x.brk_rmks);
        
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

    public static String[] updateBrokerMstr(brk_mstr x) {
        String[] m = new String[2];
        String sql = "update brk_mstr set brk_status = ?, brk_name = ?," +
            "brk_line1 = ?, brk_line2 = ?, brk_city = ?, brk_state = ?, brk_zip = ?," +
            "brk_country = ?, brk_phone = ?, brk_contact = ?, brk_email = ?, brk_type = ?," +
            "brk_ap_acct = ?, brk_ap_cc = ?, brk_certificate = ?, brk_payrate = ?, brk_paytype = ?," +
            "brk_terms = ?, brk_bank = ?, brk_taxid = ?, brk_rmks = ?" +
                     " where brk_id = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.brk_status);
            ps.setString(2, x.brk_name);
            ps.setString(3, x.brk_line1);
            ps.setString(4, x.brk_line2);
            ps.setString(5, x.brk_city);
            ps.setString(6, x.brk_state);
            ps.setString(7, x.brk_zip);
            ps.setString(8, x.brk_country);
            ps.setString(9, x.brk_phone);
            ps.setString(10, x.brk_contact);
            ps.setString(11, x.brk_email);
            ps.setString(12, x.brk_type);
            ps.setString(13, x.brk_acct);
            ps.setString(14, x.brk_cc);
            ps.setString(15, x.brk_certificate);
            ps.setString(16, x.brk_payrate);
            ps.setString(17, x.brk_paytype);
            ps.setString(18, x.brk_terms);
            ps.setString(19, x.brk_bank);
            ps.setString(20, x.brk_taxid);
            ps.setString(21, x.brk_rmks);
            ps.setString(22, x.brk_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static brk_mstr getBrokerMstr(String[] x) {
        brk_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from brk_mstr where brk_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new brk_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new brk_mstr(m, res.getString("brk_id"), 
                            res.getString("brk_status"),
                            res.getString("brk_name"),
                            res.getString("brk_line1"),
                            res.getString("brk_line2"),
                            res.getString("brk_city"),
                            res.getString("brk_state"),
                            res.getString("brk_zip"),
                            res.getString("brk_country"),
                            res.getString("brk_phone"),
                            res.getString("brk_contact"),
                            res.getString("brk_email"),
                            res.getString("brk_type"),
                            res.getString("brk_acct"),
                            res.getString("brk_cc"),
                            res.getString("brk_certificate"),
                            res.getString("brk_payrate"),
                            res.getString("brk_paytype"),
                            res.getString("brk_terms"),
                            res.getString("brk_bank"),
                            res.getString("brk_taxid"),
                            res.getString("brk_rmks")
                            
                                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new brk_mstr(m);
        }
        return r;
    }
    
    public static String[] deleteBrokerMstr(brk_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from brk_mstr where brk_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.brk_id);
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
    
    public static ArrayList<String[]> getVehicleMaintInit() {
       
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
            
            res = st.executeQuery("select distinct make from makemodel order by make ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "make";
               s[1] = res.getString("make");
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
    
    public static ArrayList<String[]> getDriverMaintInit() {
       
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
            
                 res = st.executeQuery("select bk_id from bk_mstr order by bk_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "banks";
               s[1] = res.getString("bk_id");
               lines.add(s);
            }
            
             res = st.executeQuery("select cut_code from cust_term order by cut_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "terms";
               s[1] = res.getString("cut_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select ac_id from ac_mstr order by ac_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "accounts";
               s[1] = res.getString("ac_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select dept_id from dept_mstr order by dept_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "depts";
               s[1] = res.getString("dept_id");
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
    
    public static ArrayList<String[]> getBrokerMaintInit() {
       
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
            
               res = st.executeQuery("select bk_id from bk_mstr order by bk_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "banks";
               s[1] = res.getString("bk_id");
               lines.add(s);
            }
            
             res = st.executeQuery("select cut_code from cust_term order by cut_code ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "terms";
               s[1] = res.getString("cut_code");
               lines.add(s);
            }
            
            res = st.executeQuery("select ac_id from ac_mstr order by ac_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "accounts";
               s[1] = res.getString("ac_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select dept_id from dept_mstr order by dept_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "depts";
               s[1] = res.getString("dept_id");
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
    
    public static ArrayList<String[]> getCFOMaintInit() {
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
            
            res = st.executeQuery("select cm_code from cm_mstr order by cm_code;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "customers";
               s[1] = res.getString("cm_code");
               lines.add(s);
            }
        
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
                        
            res = st.executeQuery("select code_key from code_mstr where code_code = 'country' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "countries";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "states";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'freightsvctype' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "servicetypes";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'freighteqptype' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "equipmenttypes";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            res = st.executeQuery("select car_id from car_mstr order by car_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "carriers";
               s[1] = res.getString("car_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select veh_id from veh_mstr order by veh_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "vehicle";
               s[1] = res.getString("veh_id");
               lines.add(s);
            }
            res = st.executeQuery("select drv_id from drv_mstr order by drv_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "driver";
               s[1] = res.getString("drv_id");
               lines.add(s);
            }
            res = st.executeQuery("select brk_id from brk_mstr order by brk_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "broker";
               s[1] = res.getString("brk_id");
               lines.add(s);
            }
            
            
            /*
             res = st.executeQuery("select car_id from car_mstr order by car_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "freight";
               s[1] = res.getString("car_id");
               lines.add(s);
            }
            */
            
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
    
    
    public static ArrayList<String> getMakeModel(String make) {
       
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
        
            res = st.executeQuery("select model from makemodel where make = " + "'" + make + "'" + " order by model ;");
            while (res.next()) {
               lines.add(res.getString("model"));
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
    
    public static String getDriverPhone(String driver) {
       
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
            res = st.executeQuery("select drv_phone from drv_mstr where drv_id = " + "'" + driver + "'" + ";");
            while (res.next()) {
               x =res.getString("drv_phone");
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
    
    public static String[] getBrokerInfo(String broker) {
       
        String[] x = new String[8]; // id, name, phone, contact, acct, cc, bank, terms
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
            res = st.executeQuery("select * from brk_mstr where brk_id = " + "'" + broker + "'" + ";");
            while (res.next()) {
               x[0] =res.getString("brk_id");
               x[1] =res.getString("brk_name");
               x[2] =res.getString("brk_phone");
               x[3] =res.getString("brk_contact");
               x[4] =res.getString("brk_acct");
               x[5] =res.getString("brk_cc");
               x[6] =res.getString("brk_bank");
               x[7] =res.getString("brk_terms");
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
    
    public static ArrayList<String> getCFOLines(String order) {
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

           res = st.executeQuery("SELECT cfod_stopline from cfo_det " +
                   " where cfod_nbr = " + "'" + order + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("cfod_stopline"));
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
    
    
    
    public static String[] addCFOTransaction(ArrayList<cfo_det> cfod, cfo_mstr cfo, ArrayList<cfo_item> cfoi, ArrayList<cfo_sos> cfos) {
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
            if (cfos != null) {
                for (cfo_sos z : cfos) {
                    _addCFOSummaryDet(z, bscon, ps, res);
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
    
    public static String[] updateCFOTransaction(String x, String y, ArrayList<String> lines, ArrayList<cfo_det> cfod, cfo_mstr cfo, ArrayList<cfo_item> cfoi, ArrayList<cfo_sos> cfos) {
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
               _deleteCFOLines(x, y, line, bscon, ps);  // discard unwanted lines
             }
            // before updating CFOMstr...check if incoming default revision has changed to '1'
            if (cfo.cfo_defaultrev.equals("1")) {
                _resetCFOMstrRev(cfo, bscon, ps);
            }
            _updateCFOMstr(cfo, bscon, ps);  
            for (cfo_det z : cfod) {
                _updateCFODet(z, bscon, ps, res);
            }
            if (cfoi != null) {
                _deleteCFOItemsALL(x, y, bscon, ps);  // delete ALL items for all stops and re-add
                for (cfo_item z : cfoi) {
                    _addCFOItem(z, bscon, ps, res);
                }
            }
             _deleteCFOSummaryDet(cfo.cfo_nbr, cfo.cfo_revision, bscon);
            for (cfo_sos z : cfos) {
                _addCFOSummaryDet(z, bscon, ps, res);
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
        String sqlSelect = "SELECT * FROM  cfo_mstr where cfo_nbr = ? and cfo_revision = ?";
        String sqlInsert = "insert into cfo_mstr (cfo_nbr, cfo_cust, cfo_custfonbr, cfo_revision, cfo_servicetype, cfo_equipmenttype, cfo_truckid, cfo_trailernbr, " +
        " cfo_orderstatus, cfo_deliverystatus, cfo_driver, cfo_drivercell, cfo_type, " +
        " cfo_brokerid, cfo_brokercontact, cfo_brokercell, cfo_ratetype, cfo_rate, " +
        " cfo_mileage, cfo_driverrate, cfo_driverstd, cfo_weight, cfo_orddate, cfo_confdate, cfo_ishazmat, " +
        " cfo_miscexpense, cfo_misccharges, cfo_cost, cfo_bol, cfo_rmks, cfo_derived, cfo_logic, cfo_site, cfo_edi, cfo_edireason, cfo_defaultrev ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.cfo_nbr);
          ps.setString(2, x.cfo_revision);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cfo_nbr);
            ps.setString(2, x.cfo_cust);
            ps.setString(3, x.cfo_custfonbr);
            ps.setString(4, x.cfo_revision);
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
            ps.setString(23, x.cfo_orddate);
            ps.setString(24, x.cfo_confdate);
            ps.setString(25, x.cfo_ishazmat);
            ps.setString(26, x.cfo_miscexpense);
            ps.setString(27, x.cfo_misccharges);
            ps.setString(28, x.cfo_cost);
            ps.setString(29, x.cfo_bol);
            ps.setString(30, x.cfo_rmks);
            ps.setString(31, x.cfo_derived);
            ps.setString(32, x.cfo_logic);
            ps.setString(33, x.cfo_site);
            ps.setString(34, x.cfo_edi);
            ps.setString(35, x.cfo_edireason);
            ps.setString(36, x.cfo_defaultrev);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addCFODet(cfo_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
       
        String sqlSelect = "select * from cfo_det where cfod_nbr = ? and cfod_revision = ? and cfod_stopline = ?";
        String sqlInsert = "insert into cfo_det (cfod_nbr, cfod_revision, cfod_stopline, cfod_seq, cfod_type, " 
                        + "cfod_code, cfod_name, cfod_line1, cfod_line2, cfod_line3, " 
                        + "cfod_city, cfod_state, cfod_zip, cfod_country, cfod_phone, " 
                        + "cfod_email, cfod_contact, cfod_misc, cfod_rmks, "
                        + "cfod_reference, cfod_ordnum, cfod_weight, cfod_pallet, cfod_ladingqty, cfod_hazmat, "
                        + "cfod_datetype, cfod_date, cfod_timetype1, cfod_time1, cfod_timetype2, cfod_time2, cfod_timezone, "
                        + "cfod_rate, cfod_miles ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.cfod_nbr);
          ps.setString(2, x.cfod_revision);
          ps.setString(3, x.cfod_stopline);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cfod_nbr);
            ps.setString(2, x.cfod_revision);
            ps.setString(3, x.cfod_stopline);
            ps.setString(4, x.cfod_seq);
            ps.setString(5, x.cfod_type);
            ps.setString(6, x.cfod_code);
            ps.setString(7, x.cfod_name);
            ps.setString(8, x.cfod_line1);
            ps.setString(9, x.cfod_line2);
            ps.setString(10, x.cfod_line3);
            ps.setString(11, x.cfod_city);
            ps.setString(12, x.cfod_state);
            ps.setString(13, x.cfod_zip);
            ps.setString(14, x.cfod_country);
            ps.setString(15, x.cfod_phone);
            ps.setString(16, x.cfod_email);
            ps.setString(17, x.cfod_contact);
            ps.setString(18, x.cfod_misc);
            ps.setString(19, x.cfod_rmks);
            ps.setString(20, x.cfod_reference);
            ps.setString(21, x.cfod_ordnum);
            ps.setString(22, x.cfod_weight);
            ps.setString(23, x.cfod_pallet);
            ps.setString(24, x.cfod_ladingqty);
            ps.setString(25, x.cfod_hazmat);
            ps.setString(26, x.cfod_datetype);
            ps.setString(27, x.cfod_date);
            ps.setString(28, x.cfod_timetype1);
            ps.setString(29, x.cfod_time1);
            ps.setString(30, x.cfod_timetype2);
            ps.setString(31, x.cfod_time2);
            ps.setString(32, x.cfod_timezone);
            ps.setString(33, x.cfod_rate);
            ps.setString(34, x.cfod_miles);
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
        String sqlSelect = "select * from cfo_item where cfoi_nbr = ? and cfoi_revision = ? and cfoi_stopline = ? and cfoi_itemline = ?";
        String sqlInsert = "insert into cfo_item (cfoi_nbr, cfoi_revision, cfoi_stopline, cfoi_itemline, cfoi_item, " 
                        + "cfoi_itemdesc, cfoi_order, cfoi_qty, cfoi_pallets, cfoi_weight, " 
                        + "cfoi_ref, cfoi_rmks ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.cfoi_nbr);
          ps.setString(2, x.cfoi_revision);
          ps.setString(3, x.cfoi_stopline);
          ps.setString(4, x.cfoi_itemline);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cfoi_nbr);
            ps.setString(2, x.cfoi_revision);
            ps.setString(3, x.cfoi_stopline);
            ps.setString(4, x.cfoi_itemline);
            ps.setString(5, x.cfoi_item);
            ps.setString(6, x.cfoi_itemdesc);
            ps.setString(7, x.cfoi_order);
            ps.setString(8, x.cfoi_qty);
            ps.setString(9, x.cfoi_pallets);
            ps.setString(10, x.cfoi_weight);
            ps.setString(11, x.cfoi_ref);
            ps.setString(12, x.cfoi_rmks);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _addCFOSummaryDet(cfo_sos x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from cfo_sos where cfos_nbr = ? and cfos_revision = ? and cfos_desc = ?";
        String sqlInsert = "insert into cfo_sos (cfos_nbr, cfos_revision, cfos_desc, cfos_type, " 
                        + "cfos_amttype, cfos_amt ) "
                        + " values (?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.cfos_nbr);
          ps.setString(2, x.cfos_revision);
          ps.setString(3, x.cfos_desc);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cfos_nbr);
            ps.setString(2, x.cfos_revision);
            ps.setString(3, x.cfos_desc);
            ps.setString(4, x.cfos_type);
            ps.setString(5, x.cfos_amttype);
            ps.setString(6, x.cfos_amt);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _updateCFOMstr(cfo_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;  
        
        String sql = "update cfo_mstr set cfo_cust = ?, cfo_custfonbr = ?, cfo_servicetype = ?, cfo_equipmenttype = ?, cfo_truckid = ?, cfo_trailernbr = ?, " +
        " cfo_orderstatus = ?, cfo_deliverystatus = ?, cfo_driver = ?, cfo_drivercell = ?, cfo_type = ?, " +
        " cfo_brokerid = ?, cfo_brokercontact = ?, cfo_brokercell = ?, cfo_ratetype = ?, cfo_rate = ?, " +
        " cfo_mileage = ?, cfo_driverrate = ?, cfo_driverstd = ?, cfo_weight = ?, cfo_orddate = ?, cfo_confdate = ?, cfo_ishazmat = ?, " +
        " cfo_miscexpense = ?, cfo_misccharges = ?, cfo_cost = ?, cfo_bol = ?, cfo_rmks = ?, cfo_derived = ?, cfo_logic = ?, cfo_site = ?, " +
        " cfo_edi = ?, cfo_edireason = ?, cfo_defaultrev = ? " +
        " where cfo_nbr = ? and cfo_revision = ? ; ";
        
            ps = con.prepareStatement(sql);
            ps.setString(1, x.cfo_cust);
            ps.setString(2, x.cfo_custfonbr);
            ps.setString(3, x.cfo_servicetype);
            ps.setString(4, x.cfo_equipmenttype);
            ps.setString(5, x.cfo_truckid);
            ps.setString(6, x.cfo_trailernbr);
            ps.setString(7, x.cfo_orderstatus);
            ps.setString(8, x.cfo_deliverystatus);
            ps.setString(9, x.cfo_driver);
            ps.setString(10, x.cfo_drivercell);
            ps.setString(11, x.cfo_type);
            ps.setString(12, x.cfo_brokerid);
            ps.setString(13, x.cfo_brokercontact);
            ps.setString(14, x.cfo_brokercell);
            ps.setString(15, x.cfo_ratetype);
            ps.setString(16, x.cfo_rate);
            ps.setString(17, x.cfo_mileage);
            ps.setString(18, x.cfo_driverrate);
            ps.setString(19, x.cfo_driverstd);
            ps.setString(20, x.cfo_weight);
            ps.setString(21, x.cfo_orddate);
            ps.setString(22, x.cfo_confdate);
            ps.setString(23, x.cfo_ishazmat);
            ps.setString(24, x.cfo_miscexpense);
            ps.setString(25, x.cfo_misccharges);
            ps.setString(26, x.cfo_cost);
            ps.setString(27, x.cfo_bol);
            ps.setString(28, x.cfo_rmks);
            ps.setString(29, x.cfo_derived);
            ps.setString(30, x.cfo_logic);
            ps.setString(31, x.cfo_site);
            ps.setString(32, x.cfo_edi);
            ps.setString(33, x.cfo_edireason);
            ps.setString(34, x.cfo_defaultrev);
            ps.setString(35, x.cfo_nbr);
            ps.setString(36, x.cfo_revision);
            
            rows = ps.executeUpdate();
        
        return rows;
    }
    
    private static int _resetCFOMstrRev(cfo_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;  
        
        String sql = "update cfo_mstr set cfo_defaultrev = '0' " +
        " where cfo_nbr = ?; ";
            ps = con.prepareStatement(sql);
            ps.setString(1, x.cfo_nbr);
            rows = ps.executeUpdate();
        
        return rows;
    }
    
    
    private static int _updateCFODet(cfo_det x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
       
        String sqlSelect = "select * from cfo_det where cfod_nbr = ? and cfod_revision = ? and cfod_stopline = ?";
        String sqlInsert = "insert into cfo_det (cfod_nbr, cfod_revision, cfod_stopline, cfod_seq, cfod_type, " 
                        + "cfod_code, cfod_name, cfod_line1, cfod_line2, cfod_line3, " 
                        + "cfod_city, cfod_state, cfod_zip, cfod_country, cfod_phone, " 
                        + "cfod_email, cfod_contact, cfod_misc, cfod_rmks, "
                        + "cfod_reference, cfod_ordnum, cfod_weight, cfod_pallet, cfod_ladingqty, cfod_hazmat, "
                        + "cfod_datetype, cfod_date, cfod_timetype1, cfod_time1, cfod_timetype2, cfod_time2, cfod_timezone, cfod_rate, cfod_miles  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update cfo_det set cfod_seq = ?, cfod_type = ?, " 
                        + "cfod_code = ?, cfod_name = ?, cfod_line1 = ?, cfod_line2 = ?, cfod_line3 = ?, " 
                        + "cfod_city = ?, cfod_state = ?, cfod_zip = ?, cfod_country = ?, cfod_phone = ?, " 
                        + "cfod_email = ?, cfod_contact = ?, cfod_misc = ?, cfod_rmks = ?, "
                        + "cfod_reference = ?, cfod_ordnum = ?, cfod_weight = ?, cfod_pallet = ?, cfod_ladingqty = ?, cfod_hazmat = ?, "
                        + "cfod_datetype = ?, cfod_date = ?, cfod_timetype1 = ?, cfod_time1 = ?, cfod_timetype2 = ?, cfod_time2 = ?, cfod_timezone = ?, "
                        + "cfod_rate = ?, cfod_miles = ? "
                        + " where cfod_nbr = ? and cfod_revision = ? and cfod_stopline = ?; ";
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.cfod_nbr);
          ps.setString(2, x.cfod_revision);
          ps.setString(3, x.cfod_stopline);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.cfod_nbr);
            ps.setString(2, x.cfod_revision);
            ps.setString(3, x.cfod_stopline);
            ps.setString(4, x.cfod_seq);
            ps.setString(5, x.cfod_type);
            ps.setString(6, x.cfod_code);
            ps.setString(7, x.cfod_name);
            ps.setString(8, x.cfod_line1);
            ps.setString(9, x.cfod_line2);
            ps.setString(10, x.cfod_line3);
            ps.setString(11, x.cfod_city);
            ps.setString(12, x.cfod_state);
            ps.setString(13, x.cfod_zip);
            ps.setString(14, x.cfod_country);
            ps.setString(15, x.cfod_phone);
            ps.setString(16, x.cfod_email);
            ps.setString(17, x.cfod_contact);
            ps.setString(18, x.cfod_misc);
            ps.setString(19, x.cfod_rmks);
            ps.setString(20, x.cfod_reference);
            ps.setString(21, x.cfod_ordnum);
            ps.setString(22, x.cfod_weight);
            ps.setString(23, x.cfod_pallet);
            ps.setString(24, x.cfod_ladingqty);
            ps.setString(25, x.cfod_hazmat);
            ps.setString(26, x.cfod_datetype);
            ps.setString(27, x.cfod_date);
            ps.setString(28, x.cfod_timetype1);
            ps.setString(29, x.cfod_time1);
            ps.setString(30, x.cfod_timetype2);
            ps.setString(31, x.cfod_time2);
            ps.setString(32, x.cfod_timezone);
            ps.setString(33, x.cfod_rate);
            ps.setString(34, x.cfod_miles);
            rows = ps.executeUpdate();
            } else {
            ps = con.prepareStatement(sqlUpdate) ;    
            ps.setString(32, x.cfod_nbr);
            ps.setString(33, x.cfod_revision);
            ps.setString(34, x.cfod_stopline);
            ps.setString(1, x.cfod_seq);
            ps.setString(2, x.cfod_type);
            ps.setString(3, x.cfod_code);
            ps.setString(4, x.cfod_name);
            ps.setString(5, x.cfod_line1);
            ps.setString(6, x.cfod_line2);
            ps.setString(7, x.cfod_line3);
            ps.setString(8, x.cfod_city);
            ps.setString(9, x.cfod_state);
            ps.setString(10, x.cfod_zip);
            ps.setString(11, x.cfod_country);
            ps.setString(12, x.cfod_phone);
            ps.setString(13, x.cfod_email);
            ps.setString(14, x.cfod_contact);
            ps.setString(15, x.cfod_misc);
            ps.setString(16, x.cfod_rmks);
            ps.setString(17, x.cfod_reference);
            ps.setString(18, x.cfod_ordnum);
            ps.setString(19, x.cfod_weight);
            ps.setString(20, x.cfod_pallet);
            ps.setString(21, x.cfod_ladingqty);
            ps.setString(22, x.cfod_hazmat);
            ps.setString(23, x.cfod_datetype);
            ps.setString(24, x.cfod_date);
            ps.setString(25, x.cfod_timetype1);
            ps.setString(26, x.cfod_time1);
            ps.setString(27, x.cfod_timetype2);
            ps.setString(28, x.cfod_time2);
            ps.setString(29, x.cfod_timezone);
            ps.setString(30, x.cfod_rate);
            ps.setString(31, x.cfod_miles);
            rows = ps.executeUpdate();
            }
            return rows;
    }
    
    private static void _deleteCFOLines(String x, String y, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from cfo_det where cfod_nbr = ? and cfod_revision = ? and cfod_stopline = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.setString(3, line);
        ps.executeUpdate();
    }
    
    private static void _deleteCFOItemsALL(String x, String y, Connection con, PreparedStatement ps) throws SQLException { 
        String sql = "delete from cfo_item where cfoi_nbr = ? and cfoi_revision = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
    }
    
    private static void _deleteCFOSummaryDet(String x, String y, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from cfo_sos where cfos_nbr = ? and cfos_revision = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, y);
        ps.executeUpdate();
        ps.close();
    }
    
    
    public static cfo_mstr getCFOMstr(String[] x) {
        cfo_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from cfo_mstr where cfo_nbr = ? and cfo_revision = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
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
                            res.getString("cfo_revision"),
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
                            res.getString("cfo_orddate"),
                            res.getString("cfo_confdate"),
                            res.getString("cfo_ishazmat"),
                            res.getString("cfo_miscexpense"),
                            res.getString("cfo_misccharges"),
                            res.getString("cfo_cost"),
                            res.getString("cfo_bol"),
                            res.getString("cfo_rmks"),
                            res.getString("cfo_derived"),
                            res.getString("cfo_logic"),
                            res.getString("cfo_site"),
                            res.getString("cfo_edi"),
                            res.getString("cfo_edireason"),
                            res.getString("cfo_defaultrev")
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
    
    public static ArrayList<cfo_det> getCFODet(String code, String revision) {
        cfo_det r = null;
        String[] m = new String[2];
        ArrayList<cfo_det> list = new ArrayList<cfo_det>();
        String sql = "select * from cfo_det where cfod_nbr = ? and cfod_revision = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
        ps.setString(2, revision);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cfo_det(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cfo_det(m, res.getString("cfod_nbr"), 
                        res.getString("cfod_revision"),        
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
                        res.getString("cfod_timezone"),
                        res.getString("cfod_rate"),
                        res.getString("cfod_miles"));
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
    
    public static ArrayList<cfo_item> getCFOItem(String code, String revision) {
        cfo_item r = null;
        String[] m = new String[2];
        ArrayList<cfo_item> list = new ArrayList<cfo_item>();
        String sql = "select * from cfo_item where cfoi_nbr = ? and cfoi_revision = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
        ps.setString(2, revision);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new cfo_item(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new cfo_item(m, 
                        res.getString("cfoi_nbr"), 
                        res.getString("cfoi_revision"),         
                        res.getString("cfoi_stopline"), 
                        res.getString("cfoi_itemline"), 
                        res.getString("cfoi_item"), 
                        res.getString("cfoi_itemdesc"), 
                        res.getString("cfoi_order"), 
                        res.getString("cfoi_qty"), 
                        res.getString("cfoi_pallets"), 
                        res.getString("cfoi_weight"), 
                        res.getString("cfoi_ref"), 
                        res.getString("cfoi_rmks"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new cfo_item(m);
               list.add(r);
        }
        return list;
    }
        
    public static String[] deleteCFOMstr(cfo_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from cfo_mstr where cfo_nbr = ? and cfo_revision = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.cfo_nbr);
        ps.setString(1, x.cfo_revision);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    // misc
    public static ArrayList<String> getCFORevisions(String cfo) {
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

           res = st.executeQuery("SELECT cfo_revision from cfo_mstr " +
                   " where cfo_nbr = " + "'" + cfo + "'" + " order by cfo_revision ;");
                        while (res.next()) {
                          lines.add(res.getString("cfo_revision"));
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
    
    public static String getCFODefaultRevision(String cfo) {
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
                res = st.executeQuery("select cfo_revision from cfo_mstr where cfo_nbr = " + "'" + cfo + "'" + 
                        " and cfo_defaultrev = " + "'" + "1" + "'" + ";");
                while (res.next()) {
                    item = res.getString("cfo_revision");
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
        String cfo_revision, String cfo_servicetype, String cfo_equipmenttype, String cfo_truckid, String cfo_trailernbr,
        String cfo_orderstatus, String cfo_deliverystatus, String cfo_driver, String cfo_drivercell, String cfo_type,
        String cfo_brokerid, String cfo_brokercontact, String cfo_brokercell, String cfo_ratetype, String cfo_rate,
        String cfo_mileage, String cfo_driverrate, String cfo_driverstd, String cfo_weight,
        String cfo_orddate, String cfo_confdate, String cfo_ishazmat, String cfo_miscexpense,
        String cfo_misccharges, String cfo_cost, String cfo_bol, String cfo_rmks, String cfo_derived, String cfo_logic,
        String cfo_site, String cfo_edi, String cfo_edireason, String cfo_defaultrev ) {
        public cfo_mstr(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","","","");
        }
    } 
   
    public record cfo_det (String[] m, String cfod_nbr, String cfod_revision, String cfod_stopline, String cfod_seq, String cfod_type,
        String cfod_code, String cfod_name, String cfod_line1, String cfod_line2, String cfod_line3,
        String cfod_city, String cfod_state, String cfod_zip, String cfod_country, 
        String cfod_phone, String cfod_email, String cfod_contact, String cfod_misc,
        String cfod_rmks, String cfod_reference, String cfod_ordnum,
        String cfod_weight, String cfod_pallet, String cfod_ladingqty, String cfod_hazmat,
        String cfod_datetype, String cfod_date, String cfod_timetype1, String cfod_time1, 
        String cfod_timetype2, String cfod_time2, String cfod_timezone, String cfod_rate, String cfod_miles) {
        public cfo_det(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","");
        }
    }
   
    
    public record cfo_item (String[] m, String cfoi_nbr, String cfoi_revision, String cfoi_stopline, String cfoi_itemline,
        String cfoi_item, String cfoi_itemdesc, String cfoi_order, 
        String cfoi_qty, String cfoi_pallets, String cfoi_weight,
        String cfoi_ref, String cfoi_rmks) {
        public cfo_item(String[] m) {
            this(m,"","","","","","","","","","",
                   "","");
        }
    }
    
    
    public record cfo_sos(String[] m, String cfos_nbr, String cfos_revision, String cfos_desc, String cfos_type, 
        String cfos_amttype, String cfos_amt) {
        public cfo_sos(String[] m) {
            this (m, "", "", "", "", "", "");
        }
    }
    
    

    public record veh_mstr (String[] m, String veh_id, String veh_desc, String veh_type,
        String veh_subtype, String veh_status, String veh_make, String veh_model, String veh_submodel,
        String veh_engine, String veh_fueltype, String veh_year, String veh_vin, String veh_rmks,
        String veh_servicedate, String veh_servicefreqdays, String veh_servicefreqmiles, String veh_odometer, String veh_odometerdate,
        String veh_regnbr, String veh_regdate, String veh_regtax, String veh_regstate,
        String veh_weight, String veh_condition, String veh_loc, String veh_misc1,
        String veh_misc2, String veh_misc3, String veh_inspectdate) {
        public veh_mstr(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","","","","","","");
        }
    } 
  

    public record drv_mstr (String[] m, String drv_id, String drv_status, String drv_lname,
        String drv_fname, String drv_line1, String drv_line2, String drv_city, String drv_state,
        String drv_zip, String drv_country, String drv_phone, String drv_email, String drv_type,
        String drv_ap_acct, String drv_ap_cc, String drv_terms, String drv_certificate, String drv_licensenbr,
        String drv_licenseexpire, String drv_insurancenbr, String drv_insuranceexpire, String drv_insurancecarrier,
        String drv_dhmiles, String drv_rmks, String drv_payrate, String drv_paytype,
        String drv_hiredate, String drv_termdate) {
        public drv_mstr(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","","","","","","","");
        }
    } 
    
  
    public record brk_mstr (String[] m, String brk_id, String brk_status, String brk_name,
        String brk_line1, String brk_line2, String brk_city, String brk_state, String brk_zip,
        String brk_country, String brk_phone, String brk_contact, String brk_email, String brk_type,
        String brk_acct, String brk_cc, String brk_certificate, String brk_payrate, String brk_paytype,
        String brk_terms, String brk_bank, String brk_taxid, String brk_rmks) {
        public brk_mstr(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","");
        }
    }
    
    public record veh_meta(String[] m, String vehm_id, String vehm_type, String vehm_key, String vehm_value) {
        public veh_meta(String[] m) {
            this (m, "", "", "", "");
        }
    } 
    
    public record drv_meta(String[] m, String drvm_id, String drvm_type, String drvm_key, String drvm_value) {
        public drv_meta(String[] m) {
            this (m, "", "", "", "");
        }
    }
    
    public record brk_meta(String[] m, String brkm_id, String brkm_type, String brkm_key, String brkm_value) {
        public brk_meta(String[] m) {
            this (m, "", "", "", "");
        }
    }
    
    public record frt_ctrl(String[] m, String frtc_function, String frtc_varchar)  {
        public frt_ctrl(String[] m) {
            this (m, "", "");
        }
    }
    
    
    
}
