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
"        car_minmiles, car_maxmiles, car_maxdh, car_milerate ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
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
                     " car_line1 = ?, car_line2 = ?, car_city = ?, car_state = ?, car_zip = ? car_country = ?, " +
                     " car_phone = ?, car_email = ?, car_type = ?, car_acct = ?, car_usdot = ?, car_mc = ?, " +
                     " car_ein = ?, car_minmiles = ?, car_maxmiles = ?, car_maxdh = ?, car_milerate = ? " +
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
        ps.setString(22, x.car_id);
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
                            res.getString("car_milerate")
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
    
    public record car_mstr (String[] m, String car_id, String car_desc, String car_apply,
        String car_scac, String car_name, String car_line1, String car_line2, String car_city,
        String car_state, String car_zip, String car_country, String car_phone, String car_email,
        String car_type, String car_acct, String car_usdot, String car_mc, String car_ein,
        String car_minmiles, String car_maxmiles, String car_maxdh, String car_milerate) {
        public car_mstr(String[] m) {
            this(m,"","","","","","","","","","",
                   "","","","","","","","","","",
                   "","");
        }
    } 
    
    
    
}
