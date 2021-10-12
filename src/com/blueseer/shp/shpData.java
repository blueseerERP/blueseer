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
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
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
        String sqlInsert = "insert into ship_det (shd_id, shd_line, shd_part, shd_so, shd_soline, shd_date, shd_po, shd_qty,"
                        + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, shd_loc, shd_taxamt, shd_cont, shd_serial, shd_site  ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.shd_id);
          ps.setString(2, x.shd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.shd_id);
            ps.setString(2, x.shd_line);
            ps.setString(3, x.shd_part);
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
            bscon = DriverManager.getConnection(url + db, user, pass);
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
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
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
   
    public record ship_det(String[] m, String shd_id, String shd_line, String shd_part, String shd_so,
        String shd_soline, String shd_date, String shd_po, String shd_qty,
        String shd_netprice, String shd_disc, String shd_listprice, String shd_desc, 
        String shd_wh, String shd_loc, String shd_taxamt, String shd_cont, 
        String shd_serial, String shd_site) {
        public ship_det(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", ""
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
}
