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
package com.blueseer.fgl;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import com.blueseer.fap.fapData.ap_mstr;
import static com.blueseer.far.farData.getARTaxMaterialOnly;
import static com.blueseer.ord.ordData.getServiceOrderMstr;
import com.blueseer.ord.ordData.sv_mstr;
import static com.blueseer.pur.purData.getPOCurrency;
import com.blueseer.rcv.rcvData.recv_det;
import com.blueseer.rcv.rcvData.recv_mstr;
import static com.blueseer.sch.schData.getPlanSrvOrderNumber;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.getShipperRef;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsFormatIntUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.formatUSC;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getSysMetaValue;
import static java.lang.Math.abs;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author terryva
 */
public class fglData {
  
    public static String[] addGL(gl_pair x) {
        String[] m;
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());) {
             glEntryXPpair(con, x);
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addGL(ArrayList<gl_tran> x) {
        String[] m;
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());) {
            for (gl_tran gt : x) {
                _glTranAdd(con, gt);
            } 
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addGL(gl_tran x) {
        String[] m;
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());) {
            _glTranAdd(con, x);
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    private static void _glTranAdd(Connection bscon, gl_tran gv) throws SQLException {
        
          String ref =  (gv.glt_ref().length() > 20) ? gv.glt_ref().substring(0,20) : gv.glt_ref();
          String desc = (gv.glt_desc().length() > 30) ? gv.glt_desc().substring(0,30) : gv.glt_desc();
         
          
       if ( gv.glt_amt() != 0 && ! gv.glt_acct().isBlank()) {
        String sqlInsert = "insert into gl_tran "
                        + "( glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc, glt_doc, glt_entdate ) " +
                          " values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";   
        PreparedStatement ps = bscon.prepareStatement(sqlInsert);  
            ps.setString(1, gv.glt_acct());
            ps.setString(2, gv.glt_cc());
            ps.setString(3, gv.glt_effdate());
            ps.setString(4, currformatDoubleUS(gv.glt_amt()));
            ps.setString(5, currformatDoubleUS(gv.glt_base_amt()));
            ps.setString(6, gv.glt_curr());
            ps.setString(7, gv.glt_base_curr());
            ps.setString(8, ref);
            ps.setString(9, gv.glt_site());
            ps.setString(10, gv.glt_type());
            ps.setString(11, desc);
            ps.setString(12, gv.glt_doc());
            ps.setString(13, setDateDB(new java.util.Date()));
            ps.executeUpdate();
            ps.close();
       
       } // if amount does not equal 0
      }
    
    public static String[] deleteGL(String x) {
     String[] m = new String[2];
        String sql = "delete from gl_tran where glt_ref = ?; ";
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
    
    
    public static String[] addAcctMstr(AcctMstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from ac_mstr where ac_id = ?";
        String sqlInsert = "insert into ac_mstr (ac_id, ac_desc, ac_type, ac_cur, ac_display)  " +
                " values (?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.id);
            psi.setString(2, x.desc);
            psi.setString(3, x.type);
            psi.setString(4, x.currency);
            psi.setString(5, x.cbdisplay);
            
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
        
    public static String[] updateAcctMstr(AcctMstr x) {
        String[] m = new String[2];
        String sql = "update ac_mstr set ac_desc = ?, ac_type = ?, ac_cur = ?, " +
                " ac_display = ? where ac_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.desc);
        ps.setString(2, x.type);
        ps.setString(3, x.currency);
        ps.setString(4, x.cbdisplay);
        ps.setString(5, x.id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteAcctMstr(AcctMstr x) { 
       String[] m = new String[2];
        String sql = "delete from ac_mstr where ac_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static AcctMstr getAcctMstr(String[] x) {
        AcctMstr r = null;
        String[] m = new String[2];
        String sql = "select * from ac_mstr where ac_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new AcctMstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new AcctMstr(m, res.getString("ac_id"), 
                            res.getString("ac_desc"),
                            res.getString("ac_type"),
                            res.getString("ac_cur"),
                            res.getString("ac_display")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new AcctMstr(m);
        }
        return r;
    }
    
    public static String[] addBankMstr(BankMstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from bk_mstr where bk_id = ?";
        String sqlInsert = "insert into bk_mstr (bk_id, bk_site, bk_desc, bk_acct, bk_cur, " +
                " bk_active, bk_route, bk_assignedID)  " +
                " values (?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.id);
            psi.setString(2, x.site);
            psi.setString(3, x.desc);
            psi.setString(4, x.account);
            psi.setString(5, x.currency);
            psi.setString(6, x.cbactive);
            psi.setString(7, x.routing);
            psi.setString(8, x.assignedID);
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
    
    public static String[] updateBankMstr(BankMstr x) {
      String[] m = new String[2];
        String sql = "update bk_mstr set bk_site = ?, bk_desc = ?, bk_acct = ?, bk_cur = ?, " +
                " bk_active = ?, bk_route = ?, bk_assignedID = ? where bk_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.site);
            ps.setString(2, x.desc);
            ps.setString(3, x.account);
            ps.setString(4, x.currency);
            ps.setString(5, x.cbactive);
            ps.setString(6, x.routing);
            ps.setString(7, x.assignedID);
            ps.setString(8, x.id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteBankMstr(BankMstr x) {
     String[] m = new String[2];
        String sql = "delete from bk_mstr where bk_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static BankMstr getBankMstr(String[] x) {
        BankMstr r = null;
        String[] m = new String[2];
        String sql = "select * from bk_mstr where bk_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new BankMstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new BankMstr(m, res.getString("bk_id"), 
                            res.getString("bk_site"),    
                            res.getString("bk_desc"),
                            res.getString("bk_acct"),
                            res.getString("bk_route"),
                            res.getString("bk_assignedID"),
                            res.getString("bk_cur"),    
                            res.getString("bk_active")
                        );
                    }
                }
            } 
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new BankMstr(m);
        }
        return r;
    }
    
    public static String[] addCurrMstr(CurrMstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from cur_mstr where cur_id = ?";
        String sqlInsert = "insert into cur_mstr (cur_id, cur_desc)  " +
                " values (?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.id);
            psi.setString(2, x.desc);
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
        
    public static String[] updateCurrMstr(CurrMstr x) {
        String[] m = new String[2];
        String sql = "update cur_mstr set cur_desc = ? " +
                " where cur_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.desc);
        ps.setString(2, x.id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteCurrMstr(CurrMstr x) { 
       String[] m = new String[2];
        String sql = "delete from cur_mstr where cur_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static CurrMstr getCurrMstr(String[] x) {
        CurrMstr r = null;
        String[] m = new String[2];
        String sql = "select * from cur_mstr where cur_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new CurrMstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new CurrMstr(m, res.getString("cur_id"), 
                            res.getString("cur_desc")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new CurrMstr(m);
        }
        return r;
    }
    
    
    public static String[] addDeptMstr(dept_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from dept_mstr where dept_id = ?";
        String sqlInsert = "insert into dept_mstr (dept_id, dept_desc, dept_cop_acct, dept_lbr_acct, "
                + " dept_bdn_acct, dept_lbr_usg_acct, dept_lbr_rate_acct, dept_bdn_usg_acct, dept_bdn_rate_acct  )  " +
                " values (?,?,?,?,?,?,?,?,?); "; 
      
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.dept_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.dept_id);
            psi.setString(2, x.dept_desc);
            psi.setString(3, x.dept_cop_acct);
            psi.setString(4, x.dept_lbr_acct);
            psi.setString(5, x.dept_bdn_acct);
            psi.setString(6, x.dept_lbr_usg_acct);
            psi.setString(7, x.dept_lbr_rate_acct);
            psi.setString(8, x.dept_bdn_usg_acct);
            psi.setString(9, x.dept_bdn_rate_acct);
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
    
    public static String[] updateDeptMstr(dept_mstr x) {
        String[] m = new String[2];
        String sql = "update dept_mstr set dept_desc = ?, dept_cop_acct = ?, dept_lbr_acct = ?, "
                + " dept_bdn_acct = ?, dept_lbr_usg_acct= ?, dept_lbr_rate_acct = ?, dept_bdn_usg_acct = ?, dept_bdn_rate_acct = ? " +
                " where dept_id = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.dept_desc);
        ps.setString(2, x.dept_cop_acct);
        ps.setString(3, x.dept_lbr_acct);
        ps.setString(4, x.dept_bdn_acct);
        ps.setString(5, x.dept_lbr_usg_acct);
        ps.setString(6, x.dept_lbr_rate_acct);
        ps.setString(7, x.dept_bdn_usg_acct);
        ps.setString(8, x.dept_bdn_rate_acct);
        ps.setString(9, x.dept_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteDeptMstr(dept_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from dept_mstr where dept_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.dept_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static dept_mstr getDeptMstr(String[] x) {
        dept_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from dept_mstr where dept_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new dept_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new dept_mstr(m, res.getString("dept_id"), 
                            res.getString("dept_desc"),    
                            res.getString("dept_cop_acct"),
                            res.getString("dept_lbr_acct"),
                            res.getString("dept_bdn_acct"),
                            res.getString("dept_lbr_usg_acct"),
                            res.getString("dept_lbr_rate_acct"),    
                            res.getString("dept_bdn_usg_acct"),
                            res.getString("dept_bdn_rate_acct")
                        );
                    }
                }
            } 
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new dept_mstr(m);
        }
        return r;
    }
   
    public static String[] addTaxMstr(tax_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from tax_mstr where tax_code = ?";
        String sqlInsert = "insert into tax_mstr (tax_code, tax_desc, tax_crtdate, tax_moddate, "
                + " tax_userid)  " +
                " values (?,?,?,?,?); "; 
      
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.tax_code);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.tax_code);
            psi.setString(2, x.tax_desc);
            psi.setString(3, x.tax_crtdate);
            psi.setString(4, x.tax_moddate);
            psi.setString(5, x.tax_userid);
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
    
    private static int _addTaxMstr(tax_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from tax_mstr where tax_code = ?";
        String sqlInsert = "insert into tax_mstr (tax_code, tax_desc, tax_crtdate, tax_moddate, "
                + " tax_userid)  " +
                " values (?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.tax_code);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.tax_code);
            ps.setString(2, x.tax_desc);
            ps.setString(3, x.tax_crtdate);
            ps.setString(4, x.tax_moddate);
            ps.setString(5, x.tax_userid);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static String[] addTaxTransaction(ArrayList<taxd_mstr> taxd, tax_mstr tax) {
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
            _addTaxMstr(tax, bscon, ps, res);  
            for (taxd_mstr z : taxd) {
                _addTaxDet(z, bscon, ps, res);
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
    
    
    public static String[] updateTaxMstr(tax_mstr x) {
        String[] m = new String[2];
        String sql = "update tax_mstr set tax_desc = ?, tax_crtdate = ?, tax_moddate = ?, "
                + " tax_userid = ? " + " where tax_code = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.tax_desc);
        ps.setString(2, x.tax_crtdate);
        ps.setString(3, x.tax_moddate);
        ps.setString(4, x.tax_userid);
        ps.setString(5, x.tax_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    private static int _updateTaxMstr(tax_mstr x, Connection con, PreparedStatement ps) throws SQLException {
        int rows = 0;
        String sql = "update tax_mstr set tax_desc = ?, tax_crtdate = ?, tax_moddate = ?, "
                + " tax_userid = ? " + " where tax_code = ? ";
	ps = con.prepareStatement(sql) ;
        ps.setString(1, x.tax_desc);
        ps.setString(2, x.tax_crtdate);
        ps.setString(3, x.tax_moddate);
        ps.setString(4, x.tax_userid);
        ps.setString(5, x.tax_code); 
        rows = ps.executeUpdate();
        return rows;
    }
    
    public static String[] updateTaxTransaction(String x, ArrayList<String> lines, ArrayList<taxd_mstr> taxd, tax_mstr tax) {
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
               _deleteTaxLines(x, line, bscon, ps);  // discard unwanted lines
             }
            for (taxd_mstr z : taxd) {
                _updateTaxDet(z, bscon, ps, res);
            }
             _updateTaxMstr(tax, bscon, ps);  // update so_mstr
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
    
    private static void _deleteTaxLines(String x, String line, Connection con, PreparedStatement ps) throws SQLException { 
        
        String sql = "delete from taxd_mstr where taxd_parentcode = ? and taxd_line = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x);
        ps.setString(2, line);
        ps.executeUpdate();
    }
    
    public static String[] deleteTaxMstr(tax_mstr x) { 
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteTaxMstr(x, con);  // add cms_det
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
    
    private static void _deleteTaxMstr(tax_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null; 
        String sql = "delete from tax_mstr where tax_code = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.tax_code);
        ps.executeUpdate();
        sql = "delete from taxd_mstr where taxd_parentcode = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.tax_code);
        ps.executeUpdate();
        ps.close();
    }
    
    
    public static tax_mstr getTaxMstr(String[] x) {
        tax_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from tax_mstr where tax_code = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new tax_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new tax_mstr(m, res.getString("tax_code"), 
                            res.getString("tax_desc"),    
                            res.getString("tax_crtdate"),
                            res.getString("tax_moddate"),
                            res.getString("tax_userid")
                        );
                    }
                }
            } 
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new tax_mstr(m);
        }
        return r;
    }
   
    public static ArrayList<String> getTaxLines(String nbr) {
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

           res = st.executeQuery("SELECT taxd_line from taxd_mstr " +
                   " where taxd_parentcode = " + "'" + nbr + "'" + ";");
                        while (res.next()) {
                          lines.add(res.getString("taxd_line"));
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
    
    public static ArrayList<taxd_mstr> getTaxDet(String code) {
        taxd_mstr r = null;
        String[] m = new String[2];
        ArrayList<taxd_mstr> list = new ArrayList<taxd_mstr>();
        String sql = "select * from taxd_mstr where taxd_parentcode = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, code);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new taxd_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new taxd_mstr(m, res.getString("taxd_parentcode"), 
                                res.getString("taxd_id"), 
                                res.getString("taxd_desc"), 
                                res.getString("taxd_type"), 
                                res.getString("taxd_percent"),
                                res.getString("taxd_crtdate"), 
                                res.getString("taxd_moddate"),
                                res.getString("taxd_enabled"),
                                res.getString("taxd_userid"),
                                res.getString("taxd_line"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new taxd_mstr(m);
               list.add(r);
        }
        return list;
    }
   
    
    private static int _addTaxDet(taxd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from taxd_mstr where taxd_parentcode = ? and taxd_line = ?";
        String sqlInsert = "insert into taxd_mstr (taxd_parentcode, taxd_line, taxd_desc, taxd_type, " 
                        + "taxd_percent, taxd_crtdate, taxd_moddate, taxd_enabled, " 
                        + "taxd_userid ) "
                        + " values (?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.taxd_parentcode);
          ps.setString(2, x.taxd_line);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.taxd_parentcode);
            ps.setString(2, x.taxd_line);
            ps.setString(3, x.taxd_desc);
            ps.setString(4, x.taxd_type);
            ps.setString(5, x.taxd_percent);
            ps.setString(6, x.taxd_crtdate);
            ps.setString(7, x.taxd_moddate);
            ps.setString(8, x.taxd_enabled);
            ps.setString(9, x.taxd_userid);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static int _updateTaxDet(taxd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from taxd_mstr where taxd_parentcode = ? and taxd_line = ?";
        String sqlUpdate = "update taxd_mstr set taxd_desc = ?, taxd_type = ?, " +
                "taxd_percent = ?, taxd_crtdate = ?, taxd_moddate = ?, taxd_enabled = ?, " +
                " taxd_userid = ? " +
                 " where taxd_parentcode = ? and taxd_line = ? ; ";
        String sqlInsert = "insert into taxd_mstr (taxd_parentcode, taxd_line, taxd_desc, taxd_type, " 
                        + "taxd_percent, taxd_crtdate, taxd_moddate, taxd_enabled, " 
                        + "taxd_userid ) "
                        + " values (?,?,?,?,?,?,?,?,?); ";  
        ps = con.prepareStatement(sqlSelect); 
        ps.setString(1, x.taxd_parentcode);
        ps.setString(2, x.taxd_line);
        res = ps.executeQuery();
        if (! res.isBeforeFirst()) {  // insert
	 ps = con.prepareStatement(sqlInsert) ;
            ps.setString(1, x.taxd_parentcode);
            ps.setString(2, x.taxd_line);
            ps.setString(3, x.taxd_desc);
            ps.setString(4, x.taxd_type);
            ps.setString(5, x.taxd_percent);
            ps.setString(6, x.taxd_crtdate);
            ps.setString(7, x.taxd_moddate);
            ps.setString(8, x.taxd_enabled);
            ps.setString(9, x.taxd_userid);
            rows = ps.executeUpdate();
        } else {    // update
         ps = con.prepareStatement(sqlUpdate) ;
            ps.setString(8, x.taxd_parentcode);
            ps.setString(9, x.taxd_line);
            ps.setString(1, x.taxd_desc);
            ps.setString(2, x.taxd_type);
            ps.setString(3, x.taxd_percent);
            ps.setString(4, x.taxd_crtdate);
            ps.setString(5, x.taxd_moddate);
            ps.setString(6, x.taxd_enabled);
            ps.setString(7, x.taxd_userid);
            rows = ps.executeUpdate();
        }
            
        return rows;
    }
    
    
    
    public static String[] addUpdateGLCtrl(gl_ctrl x) {
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  gl_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into gl_ctrl (gl_bs_from, gl_bs_to, gl_is_from, " +
        "gl_is_to, gl_earnings, gl_foreignreal, gl_autopost ) "
                        + " values (?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update gl_ctrl set gl_bs_from = ?, gl_bs_to = ?, gl_is_from = ?, " +
        "gl_is_to = ?, gl_earnings = ?, gl_foreignreal = ?, gl_autopost = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.gl_bs_from);
            psi.setString(2, x.gl_bs_to);
            psi.setString(3, x.gl_is_from);
            psi.setString(4, x.gl_is_to);
            psi.setString(5, x.gl_earnings);
            psi.setString(6, x.gl_foreignreal);
            psi.setString(7, x.gl_autopost);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.gl_bs_from);
            psu.setString(2, x.gl_bs_to);
            psu.setString(3, x.gl_is_from);
            psu.setString(4, x.gl_is_to);
            psu.setString(5, x.gl_earnings);
            psu.setString(6, x.gl_foreignreal);
            psu.setString(7, x.gl_autopost);
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
   
    public static gl_ctrl getGLCtrl(String[] x) {
        gl_ctrl r = null;
        String[] m = new String[2];
        String sql = "select * from gl_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new gl_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new gl_ctrl(m, 
                                res.getString("gl_bs_from"),
                                res.getString("gl_bs_to"),
                                res.getString("gl_is_from"),
                                res.getString("gl_is_to"),
                                res.getString("gl_earnings"),
                                res.getString("gl_foreignreal"),
                                res.getString("gl_autopost")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new gl_ctrl(m);
        }
        return r;
    }
    
    
    public static String[] addUpdatePAYCtrl(pay_ctrl x) {
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  pay_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into pay_ctrl (payc_bank, payc_labor_acct, payc_labor_cc, " +
        " payc_salaried_acct,  payc_salaried_cc,  payc_payrolltax_acct,  payc_payrolltax_cc, " +
        "payc_withhold_acct, payc_varchar ) "
                        + " values (?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update pay_ctrl set payc_bank = ?, payc_labor_acct = ?, payc_labor_cc = ?, " +
        " payc_salaried_acct = ?,  payc_salaried_cc = ?,  payc_payrolltax_acct = ?,  payc_payrolltax_cc = ?, " +
        "payc_withhold_acct = ?, payc_varchar = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.payc_bank);
            psi.setString(2, x.payc_labor_acct);
            psi.setString(3, x.payc_labor_cc);
            psi.setString(4, x.payc_salaried_acct);
            psi.setString(5, x.payc_salaried_cc);
            psi.setString(6, x.payc_payrolltax_acct);
            psi.setString(7, x.payc_payrolltax_cc);
            psi.setString(8, x.payc_withhold_acct);
            psi.setString(9, x.payc_varchar);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.payc_bank);
            psu.setString(2, x.payc_labor_acct);
            psu.setString(3, x.payc_labor_cc);
            psu.setString(4, x.payc_salaried_acct);
            psu.setString(5, x.payc_salaried_cc);
            psu.setString(6, x.payc_payrolltax_acct);
            psu.setString(7, x.payc_payrolltax_cc);
            psu.setString(8, x.payc_withhold_acct);
            psu.setString(9, x.payc_varchar);
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
   
    public static pay_ctrl getPAYCtrl(String[] x) {
        pay_ctrl r = null;
        String[] m = new String[2];
        String sql = "select * from pay_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new pay_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new pay_ctrl(m, 
                                res.getString("payc_bank"),
                                res.getString("payc_labor_acct"),
                                res.getString("payc_labor_cc"),
                                res.getString("payc_salaried_acct"),
                                res.getString("payc_salaried_cc"),
                                res.getString("payc_payrolltax_acct"),
                                res.getString("payc_payrolltax_cc"),
                                res.getString("payc_withhold_acct"),
                                res.getString("payc_varchar")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new pay_ctrl(m);
        }
        return r;
    }
    
    
    // misc functions
    
    public static String getAccountBalanceReport(String[] key) {
        int year = Integer.valueOf(key[0]); 
        int period = Integer.valueOf(key[1]);
        String site = key[2];
        boolean iscc = BlueSeerUtils.ConvertStringToBool(key[3]);
        String in_accttype = key[4];
        String fromacct = key[5];
        String toacct = key[6];
        
        StringBuilder sb = new StringBuilder();
        ArrayList<String[]> accounts = fglData.getGLAcctListRangeWCurrTypeDesc(fromacct, toacct);
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

                 Statement st2 = con.createStatement();
                ResultSet res2 = null;
                
                int qty = 0;
                double dol = 0;
                int j = 0;
          
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                
                 
                 int prioryear = 0;
                 double begbal = 0.00;
                 double activity = 0.00;
                 double endbal = 0.00;
                 double totbegbal = 0.00;
                 double totactivity = 0.00;
                 double totendbal = 0.00;
                 double preact = 0.00;
                 double postact = 0.00;
                 Date p_datestart = null;
                 Date p_dateend = null;
                 
                 ArrayList<String> ccamts = new ArrayList<String>();
                 
                // ArrayList<String[]> accounts = fglData.getGLAcctListRangeWCurrTypeDesc(ddacctfrom.getSelectedItem().toString(), ddacctto.getSelectedItem().toString());
                // ArrayList<String> ccs = fglData.getGLCCList();
                 
                  totbegbal = 0.00;
                  totactivity = 0.00;
                  totendbal = 0.00;
                 
                 prioryear = year - 1;
                 String acctid = "";
                 String acctdesc = "";
                 String acctcurr = "";
                 String accttype = "";
                 String cc = "";
                 
                 
                 if (iscc) {
                 
                 ACCTS:    for (String account[] : accounts) {
                     
                     
                  
                  acctid = account[0];
                  acctcurr = account[1];
                  accttype = account[2];
                  acctdesc = account[3];
                  
                 
                  
                  begbal = 0.00;
                  activity = 0.00;
                  endbal = 0.00;
                  preact = 0.00;
                  postact = 0.00;
                
                  
                  
                 // calculate all acb_mstr records for whole periods < fromdateperiod
                    // begbal += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
                  if (accttype.equals("L") || accttype.equals("A")) {
                      //must be type balance sheet
                  res = st.executeQuery("select acb_cc, sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct = " + "'" + acctid + "'" + " AND " +
                        " acb_site = " + "'" + site + "'" + " AND " +
                        " (( acb_year = " + "'" + year + "'" + " AND acb_per <= " + "'" + period + "'" + " ) OR " +
                        "  ( acb_year <= " + "'" + prioryear + "'" + " )) " +
                        " group by acb_cc ;");
                
                       while (res.next()) {
                          endbal = 0;
                          activity = 0;
                          begbal = 0;
                          begbal = res.getDouble("sum");
                          
                           // now activity
                                      res2= st2.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                                "'" + String.valueOf(year) + "'" + 
                                " AND acb_per = " +
                                "'" + String.valueOf(period) + "'" +
                                " AND acb_acct = " +
                                "'" + acctid + "'" +
                                " AND acb_cc = " +
                                "'" + res.getString("acb_cc") + "'" +
                                " AND acb_site = " + "'" + site + "'" +
                                " ;");
                               while (res2.next()) {
                                  activity = res2.getDouble(("sum"));
                               }
                            
                               begbal = begbal - activity;
                               endbal = begbal + activity;
                        if (in_accttype.equals(getGlobalProgTag("all"))) {       
                            sb.append(acctid + "," +
                            res.getString("acb_cc") + "," +
                            accttype + "," + 
                            acctcurr + "," +
                            acctdesc + "," +
                            site + "," +
                                currformatDouble(begbal) + "," +
                            currformatDouble(activity) + "," +
                            currformatDouble(endbal)
                            );
                            sb.append("\n");
                            
                            
                        } else {
                          if (accttype.equals(in_accttype))  {
                          sb.append(acctid + "," +
                            res.getString("acb_cc") + "," +
                            accttype + "," + 
                            acctcurr + "," +
                            acctdesc + "," +
                            site + "," +
                                currformatDouble(begbal) + "," +
                            currformatDouble(activity) + "," +
                            currformatDouble(endbal)
                            );
                            sb.append("\n");
                          }
                        }
                       }
                  } else {
                     // must be income statement
                      res = st.executeQuery("select acb_cc, sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct = " + "'" + acctid + "'" + " AND " +
                        " acb_site = " + "'" + site + "'" + " AND " +
                        " ( acb_year = " + "'" + year + "'" + " AND acb_per <= " + "'" + period + "'" + ")" +
                        " group by acb_cc ;");
                
                       while (res.next()) {
                          endbal = 0;
                          activity = 0;
                          begbal = 0;
                          
                       
                          begbal = res.getDouble("sum");
                        
                                    // now activity
                                      res2= st2.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                                "'" + String.valueOf(year) + "'" + 
                                " AND acb_per = " +
                                "'" + String.valueOf(period) + "'" +
                                " AND acb_acct = " +
                                "'" + acctid + "'" +
                                " AND acb_cc = " +
                                "'" + res.getString("acb_cc") + "'" +
                                " AND acb_site = " + "'" + site + "'" +
                                "  ;");
                               while (res2.next()) {
                                  activity = res2.getDouble(("sum"));
                               }
                            
                               begbal = begbal - activity;
                               endbal = begbal + activity;
                    if (in_accttype.equals(getGlobalProgTag("all"))) {       
                            sb.append(acctid + "," +
                            res.getString("acb_cc") + "," +
                            accttype + "," + 
                            acctcurr + "," +
                            acctdesc + "," +
                            site + "," +
                                currformatDouble(begbal) + "," +
                            currformatDouble(activity) + "," +
                            currformatDouble(endbal)
                            );
                            sb.append("\n");
                           
                    } else {
                        if (accttype.equals(in_accttype)) {
                        sb.append(acctid + "," +
                            res.getString("acb_cc") + "," +
                            accttype + "," + 
                            acctcurr + "," +
                            acctdesc + "," +
                            site + "," +
                                currformatDouble(begbal) + "," +
                            currformatDouble(activity) + "," +
                            currformatDouble(endbal)
                            );
                            sb.append("\n");
                       
                        }
                    }       
                                  
                 
                            
                       }
                 
                       
                  }
                  
                  /* 
                   // calculate period(s) activity defined by date range 
                  // activity += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
                       res = st.executeQuery("select acb_cc, sum(acb_amt) as sum from acb_mstr where acb_year = " +
                        "'" + String.valueOf(year) + "'" + 
                        " AND acb_per = " +
                        "'" + String.valueOf(period) + "'" +
                        " AND acb_acct = " +
                        "'" + acctid + "'" +
                        " AND acb_site = " + "'" + site + "'" +
                        " group by acb_cc ;");
                       while (res.next()) {
                         // activity += res.getDouble(("sum"));
                         // ccamts.add(res.getString("acb_cc") + "," + "activity" + "," + res.getString("sum"));
                       }
                 
                  */
                
                 } // Accts
                               
                   
                 // now sum for the total labels display
                 
                
                
                 } else {    // else if not CC included
                     
                  
                 ACCTS:    for (String[] account : accounts) {
                     
                     
                  
                  acctid = account[0];
                  acctcurr = account[1];
                  accttype = account[2];
                  acctdesc = account[3];
               
                  
                  begbal = 0.00;
                  activity = 0.00;
                  endbal = 0.00;
                  preact = 0.00;
                  postact = 0.00;
                
                  
                  
                 // calculate all acb_mstr records for whole periods < fromdateperiod
                    // begbal += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
                  if (accttype.equals("L") || accttype.equals("A")) {
                      //must be type balance sheet
                  res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct = " + "'" + acctid + "'" + " AND " +
                        " acb_site = " + "'" + site + "'" + " AND " +
                        " (( acb_year = " + "'" + year + "'" + " AND acb_per < " + "'" + period + "'" + " ) OR " +
                        "  ( acb_year <= " + "'" + prioryear + "'" + " )) " +
                        ";");
                
                       while (res.next()) {
                          begbal += res.getDouble("sum");
                       }
                  } else {
                     // must be income statement
                      res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct = " + "'" + acctid + "'" + " AND " +
                        " acb_site = " + "'" + site + "'" + " AND " +
                        " ( acb_year = " + "'" + year + "'" + " AND acb_per < " + "'" + period + "'" + ")" +
                        ";");
                
                       while (res.next()) {
                          begbal += res.getDouble("sum");
                       }
                  }
                  
                   
                   // calculate period(s) activity defined by date range 
                  // activity += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
               
                  
                 
                       res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                        "'" + String.valueOf(year) + "'" + 
                        " AND acb_per = " +
                        "'" + String.valueOf(period) + "'" +
                        " AND acb_acct = " +
                        "'" + acctid + "'" +
                        " AND acb_site = " + "'" + site + "'" +
                        ";");
               
                  
                
                       while (res.next()) {
                          activity += res.getDouble(("sum"));
                       }
                 
                               
                 endbal = begbal + activity;
                 
               
                
               if (in_accttype.equals(getGlobalProgTag("all"))) {
                sb.append(acctid + "," + 
                            "" + "," + 
                            accttype + "," +  
                            acctcurr + "," + 
                            acctdesc + "," + 
                            site + "," + 
                            currformatDouble(begbal) + "," + 
                            currformatDouble(activity) + "," + 
                            currformatDouble(endbal)
                            );
                sb.append("\n");
               
               } else {
                  if (accttype.equals(in_accttype)) {
                  sb.append(acctid + "," + 
                            "" + "," + 
                            accttype + "," +  
                            acctcurr + "," + 
                            acctdesc + "," + 
                            site + "," + 
                            currformatDouble(begbal) + "," + 
                            currformatDouble(activity) + "," + 
                            currformatDouble(endbal)
                            );
                sb.append("\n");
                    
                  }
               }
             
                   
                } // Accts   
                     
                     
                 } // else of cc is not included
                 
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                sb.append(s.getMessage());
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
    
    public static String getAccountActivityYear(String[] key) {
        // key = year, site, acctfrom, acctto
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
                int qty = 0;
                double dol = 0;
                int i = 0;
               
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                
                 
                 int year = bsParseInt(key[0]);
                 
                 
                 String[] str_activity = {"","","","","","","","","","","","",""};
                 double activity = 0.00;
                
                 java.sql.Date p_datestart = null;
                 java.sql.Date p_dateend = null;
                 
                 ArrayList<String> accounts = fglData.getGLAcctListRangeWTypeDesc(key[2], key[3]);
                
                 
                 String acctid = "";
                 String accttype = "";
                 String acctdesc = "";
                 String[] ac = null;
                 
                 
                 ACCTS:    for (String account : accounts) {
                  ac = account.split(",", -1);
                  acctid = ac[0];
                  accttype = ac[1];
                  acctdesc = ac[2];
                  str_activity[0] = "";
                  str_activity[1] = "";
                  str_activity[2] = "";
                  str_activity[3] = "";
                  str_activity[4] = "";
                  str_activity[5] = "";
                  str_activity[6] = "";
                  str_activity[7] = "";
                  str_activity[8] = "";
                  str_activity[9] = "";
                  str_activity[10] = "";
                  str_activity[11] = "";
                  
                  for (int k = 1 ; k<= 12 ; k++) {
                  
                  activity = 0.00;
                   
                  // calculate period(s) activity defined by date range 
                  // activity += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
               
                  res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                        "'" + String.valueOf(year) + "'" + 
                        " AND acb_per = " +
                        "'" + String.valueOf(k) + "'" +
                        " AND acb_acct = " +
                        "'" + acctid + "'" +
                        " AND acb_site = " + "'" + key[1] + "'" +
                        ";");
                
                       while (res.next()) {
                          activity += res.getDouble(("sum"));
                       }
                 
                     str_activity[k - 1] = currformatDouble(activity);
                 
                 
                  } // k
                 
                 sb.append(acctid + "," + 
                            acctdesc + "," + 
                            str_activity[0] + "," + 
                            str_activity[1] + "," + 
                            str_activity[2] + "," + 
                            str_activity[3] + "," + 
                            str_activity[4] + "," + 
                            str_activity[5] + "," + 
                            str_activity[6] + "," + 
                            str_activity[7] + "," + 
                            str_activity[8] + "," + 
                            str_activity[9] + "," + 
                            str_activity[10] + "," + 
                            str_activity[11]
                            );
                sb.append("\n");
              
                 } // account
             
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
    
    public static String setGLRecNbr(String type) {
           String mystring = "";
          // int nextnbr = OVData.getNextNbr("gl");
           java.util.Date now = new java.util.Date();
           DateFormat dfdate = new SimpleDateFormat("yyyyMM");
           // format should be two char type code + 8 char date code + 6 char unique number ...16 chars in all
          // mystring = type + dfdate.format(now) + String.format("%06d", nextnbr);   
           // 20230804 TEV using hex string instead of getNextNbr for performance
           mystring = type + Long.toHexString(System.currentTimeMillis());
           return mystring;
       }
    
    public static void glEntry(String acct_cr, String cc_cr, String acct_dr, String cc_dr, String date, Double amt, Double baseamt, String curr, String basecurr, String ref, String site, String type, String desc, String doc) {
          
           /* any amount = 0 passed to this method will be ignored */
           
           /* amount passed here will be rounded to 2 decimal places with DecimalFormat func */
           
          /*
          Field count must be 8 fields...
          0=acct_cr   8 char string
          1=cc_cr     4 char string
          2=acct_dr   8 char string
          3=cc_dr     4 char string
          4=date      Date format yyyy-MM-dd
          5=amt       postive or negative digits (no commas) 
          6=ref       20 char string
          7=site      10 char string
          8=type      10 char string
          9=desc      30 char string
          
          */
           
          if (ref.length() > 20) {
              ref = ref.substring(0,20);
          } 
          if (desc.length() > 30) {
              desc = desc.substring(0,30);
          }
         
          String rndamt = "";
          
       
          
        if ( amt != 0 && ! acct_cr.isBlank() && ! acct_dr.isBlank()) {
       try {
             
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            String sqlInsert = "insert into gl_tran "
                            + "( glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc, glt_doc, glt_entdate ) " +
                              " values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";   
            PreparedStatement ps = con.prepareStatement(sqlInsert); 
            try {
                ps.setString(1, acct_cr);
                ps.setString(2, cc_cr);
                ps.setString(3, date);
                ps.setString(4, currformatDoubleUS(-1 * amt));
                ps.setString(5, currformatDoubleUS(-1 * baseamt));
                ps.setString(6, curr);
                ps.setString(7, basecurr);
                ps.setString(8, ref);
                ps.setString(9, site);
                ps.setString(10, type);
                ps.setString(11, desc);
                ps.setString(12, doc);
                ps.setString(13, BlueSeerUtils.setDateFormatNull(new java.util.Date()));
                ps.executeUpdate();

                ps.setString(1, acct_dr);
                ps.setString(2, cc_dr);
                ps.setString(3, date);
                ps.setString(4, currformatDoubleUS(amt));
                ps.setString(5, currformatDoubleUS(baseamt));
                ps.setString(6, curr);
                ps.setString(7, basecurr);
                ps.setString(8, ref);
                ps.setString(9, site);
                ps.setString(10, type);
                ps.setString(11, desc);
                ps.setString(12, doc);
                ps.setString(13, BlueSeerUtils.setDateFormatNull(new java.util.Date()));
                ps.executeUpdate();

                ps.close();

            } catch (SQLException s) {
                MainFrame.bslog(s);
            }  finally {
                    if (ps != null) {
                        ps.close();
                    }
                    con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
       } // if amount does not equal 0
          
      }
    
    public static void glEntryXP(Connection bscon, String acct_cr, String cc_cr, String acct_dr, String cc_dr, String date, Double amt, Double baseamt, String curr, String basecurr, String ref, String site, String type, String desc, String doc) throws SQLException {
          
           /* any amount = 0 passed to this method will be ignored */
           /* record entry requires a non-blank acct_cr and acct_dr
           /* amount passed here will be rounded to 2 decimal places with DecimalFormat func */
           
          if (ref.length() > 20) {
              ref = ref.substring(0,20);
          } 
          if (desc.length() > 30) {
              desc = desc.substring(0,30);
          }
         
          String rndamt = "";
       if ( amt != 0 && ! acct_cr.isBlank() && ! acct_dr.isBlank()) {
        String sqlInsert = "insert into gl_tran "
                        + "( glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc, glt_doc, glt_entdate ) " +
                          " values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";   
        PreparedStatement ps = bscon.prepareStatement(sqlInsert);  
            ps.setString(1, acct_cr);
            ps.setString(2, cc_cr);
            ps.setString(3, date);
            ps.setString(4, currformatDoubleUS(-1 * amt));
            ps.setString(5, currformatDoubleUS(-1 * baseamt));
            ps.setString(6, curr);
            ps.setString(7, basecurr);
            ps.setString(8, ref);
            ps.setString(9, site);
            ps.setString(10, type);
            ps.setString(11, desc);
            ps.setString(12, doc);
            ps.setString(13, setDateDB(new java.util.Date()));
            ps.executeUpdate();
            
            ps.setString(1, acct_dr);
            ps.setString(2, cc_dr);
            ps.setString(3, date);
            ps.setString(4, currformatDoubleUS(amt));
            ps.setString(5, currformatDoubleUS(baseamt));
            ps.setString(6, curr);
            ps.setString(7, basecurr);
            ps.setString(8, ref);
            ps.setString(9, site);
            ps.setString(10, type);
            ps.setString(11, desc);
            ps.setString(12, doc);
            ps.setString(13, setDateDB(new java.util.Date()));
            ps.executeUpdate();
       
            ps.close();
       
       } // if amount does not equal 0
      }
     
    public static void glEntryXPpair(Connection bscon, gl_pair gv) throws SQLException {
          
           /* any amount = 0 passed to this method will be ignored */
           /* record entry requires a non-blank acct_cr and acct_dr
           /* amount passed here will be rounded to 2 decimal places with DecimalFormat func */
           
          
          String ref =  (gv.glv_ref().length() > 20) ? gv.glv_ref().substring(0,20) : gv.glv_ref();
          String desc = (gv.glv_desc().length() > 30) ? gv.glv_desc().substring(0,30) : gv.glv_desc();
         
          
       if ( gv.glv_amt() != 0 && ! gv.glv_acct_cr().isBlank() && ! gv.glv_acct_dr().isBlank()) {
        String sqlInsert = "insert into gl_tran "
                        + "( glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc, glt_doc, glt_entdate ) " +
                          " values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";   
        PreparedStatement ps = bscon.prepareStatement(sqlInsert);  
            ps.setString(1, gv.glv_acct_cr());
            ps.setString(2, gv.glv_cc_cr());
            ps.setString(3, gv.glv_date());
            ps.setString(4, currformatDoubleUS(-1 * gv.glv_amt()));
            ps.setString(5, currformatDoubleUS(-1 * gv.glv_baseamt()));
            ps.setString(6, gv.glv_curr());
            ps.setString(7, gv.glv_basecurr());
            ps.setString(8, ref);
            ps.setString(9, gv.glv_site());
            ps.setString(10, gv.glv_type());
            ps.setString(11, desc);
            ps.setString(12, gv.glv_doc());
            ps.setString(13, setDateDB(new java.util.Date()));
            ps.executeUpdate();
            
            ps.setString(1, gv.glv_acct_dr());
            ps.setString(2, gv.glv_cc_dr());
            ps.setString(3, gv.glv_date());
            ps.setString(4, currformatDoubleUS(gv.glv_amt()));
            ps.setString(5, currformatDoubleUS(gv.glv_baseamt()));
            ps.setString(6, gv.glv_curr());
            ps.setString(7, gv.glv_basecurr());
            ps.setString(8, ref);
            ps.setString(9, gv.glv_site());
            ps.setString(10, gv.glv_type());
            ps.setString(11, desc);
            ps.setString(12, gv.glv_doc());
            ps.setString(13, setDateDB(new java.util.Date()));
            ps.executeUpdate();
       
            ps.close();
       
       } // if amount does not equal 0
      }
    
    
    public static boolean _glEntryFromVoucher(ap_mstr ap, Connection bscon, boolean Void) throws SQLException {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
       
          
                Statement st = bscon.createStatement();
                ResultSet res = null;
               
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                 // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();   
                    ArrayList<Double> basecost =  new ArrayList();
                    ArrayList curr =  new ArrayList();
                    ArrayList basecurr =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                   
                    String thistype = "RCT-VOUCH";
                    String thisdesc = "RCT VOUCHER";   
                
                    // set parent GL doc number
                    String gldoc = fglData.setGLRecNbr("AP");
                   
                    res = st.executeQuery("select * from po_ctrl;");
                    while (res.next()) {
                     // credit vendor AP Acct (AP Voucher) and debit unvouchered receipts (po_rcpts acct)
                    acct_cr.add(ap.ap_acct());
                    acct_dr.add(res.getString("poc_rcpt_acct"));
                    cc_cr.add(ap.ap_cc());
                    cc_dr.add(res.getString("poc_rcpt_cc"));
                    if (Void) {
                    cost.add(-1 * ap.ap_amt());
                    basecost.add(-1 * ap.ap_base_amt());
                    } else {
                    cost.add(ap.ap_amt());
                    basecost.add(ap.ap_base_amt());   
                    }
                    curr.add(ap.ap_curr());
                    basecurr.add(ap.ap_base_curr());
                    site.add(ap.ap_site());
                    ref.add(ap.ap_ref());
                    doc.add(gldoc);
                    type.add(thistype);
                    desc.add(ap.ap_rmks());     
          
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                    res.close();
                    st.close();
                      for (int j = 0; j < acct_cr.size(); j++) {
                      glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(parseDate(ap.ap_effdate())), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
          
        return myerror;
        
        }
    
    public static boolean _glEntryFromVoucherExpense(String voucher, Date effdate, Connection bscon, boolean Void) throws SQLException {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        
                Statement st = bscon.createStatement();
                ResultSet res = null;
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                   // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();   
                    ArrayList<Double> basecost =  new ArrayList();
                    ArrayList curr =  new ArrayList();
                    ArrayList basecurr =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                   
                    String thistype = "RCT-VOUCH";
                   
                    // set parent GL doc number
                    String gldoc = fglData.setGLRecNbr("AP");
                    
                    
                       res = st.executeQuery("select ap_amt, ap_base_amt, ap_curr, ap_base_curr, ap_ref, ap_check, ap_nbr, vod_item, ap_site, ap_acct, ap_cc, ap_vend, vod_qty, vod_voprice, vod_expense_acct, vod_expense_cc from vod_mstr " +
                               "inner join ap_mstr on ap_nbr = vod_id and ap_type = 'V' where vod_id = " + "'" + voucher + "'" +";");
                   
                    Double amt = 0.00;   
                    while (res.next()) {
                     // credit vendor AP Acct (AP Voucher) and debit unvouchered receipts (po_rcpts acct)
                    amt = res.getDouble("vod_qty") * res.getDouble("vod_voprice");
                       acct_cr.add(res.getString("ap_acct"));
                    acct_dr.add(res.getString("vod_expense_acct"));
                    cc_cr.add(res.getString("ap_cc"));
                    cc_dr.add(res.getString("vod_expense_cc"));
                    if (Void) {
                    cost.add(-1 * amt);
                    basecost.add(-1 * amt);
                    } else {
                    cost.add(amt);
                    basecost.add(amt);    
                    }
                    curr.add(res.getString("ap_curr"));
                    basecurr.add(res.getString("ap_base_curr"));
                    site.add(res.getString("ap_site"));
                    ref.add(res.getString("ap_check"));
                    doc.add(gldoc);
                    type.add(thistype);
                    if (res.getString("ap_ref").isEmpty()) {
                       desc.add(res.getString("vod_item")); 
                    } else {
                       desc.add(res.getString("ap_ref") + "/" + res.getString("vod_item"));
                    }
                             
               
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    
                
                    }
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
          
                     res.close();
                     st.close();
        return myerror;
        
        }
    
    
    public static boolean glEntryFromPayRoll(String batch, Date effdate) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
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
               
               
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                   // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();   
                    ArrayList<Double> basecost =  new ArrayList();
                    ArrayList curr =  new ArrayList();
                    ArrayList basecurr =  new ArrayList();
                   
                    String gldoc = setGLRecNbr("PR");
                    String thistype = "PayRoll";
                    
                    pay_ctrl pc = getPAYCtrl(null);
                    
                    
                    String laboracct = OVData.getDefaultPayLaborAcct();
                    String withhold = OVData.getDefaultPayWithHoldAcct();
                    String salariedacct = OVData.getDefaultPaySalariedAcct();
                    String defaulttaxacct = OVData.getDefaultPayTaxAcct();
                    String taxacct = "";
                    String cc = OVData.getDefaultCC();
                    String defaultcurr = OVData.getDefaultCurrency();
                    String bank = OVData.getDefaultAPBank();
                    String bankacct = OVData.getDefaultBankAcct(bank);
                    
                    
                    // credit payables...debit expense direct/indirect labor
                    // LETS DO LABOR FIRST....THIS WILL DEBIT LABOR EXPENSE AND CREDIT CASH WITH THE NET CHECK PAYMENT
                    res = st.executeQuery("select py_id, py_site, pyd_emptype, pyd_checknbr, pyd_payamt, pyd_empdept, " +
                            " (select sum(case when pyl_type = 'deduction' then pyl_amt end) from pay_line where pyl_id = pyd_id and pyl_empnbr = pyd_empnbr and pyl_checknbr = pyd_checknbr)as 'deductions' " +
                            " from pay_det inner join pay_mstr on py_id = pyd_id  " +
                               " where pyd_id = " + "'" + batch + "'" +";");
                    Double netpay = 0.00;
                    Double amt = 0.00;   
                    while (res.next()) {
                     // credit Cash account and debit labor expense
                    amt = res.getDouble("pyd_payamt");
                    netpay = amt - res.getDouble("deductions");
                    
                    if (res.getString("pyd_emptype").equals("Hourly-Direct")) {
                     acct_dr.add(laboracct);   
                    } else {
                     acct_dr.add(salariedacct);   
                    }
                    acct_cr.add(pc.payc_varchar); // varchar = payroll payables
                    cc_cr.add(res.getString("pyd_empdept"));
                    cc_dr.add(res.getString("pyd_empdept"));
                    cost.add(netpay);
                    basecost.add(netpay);
                    curr.add(defaultcurr);
                    basecurr.add(defaultcurr);
                    site.add(res.getString("py_site"));
                    ref.add(res.getString("py_id"));
                    type.add(thistype);
                    desc.add("CheckNbr:" + res.getString("pyd_checknbr"));
                    doc.add(gldoc);
                    
                    // now debit payroll payable (payc_varchar) and credit cashacct
                    acct_cr.add(bankacct);
                    acct_dr.add(pc.payc_varchar); // varchar = payroll payables
                    cc_cr.add(res.getString("pyd_empdept"));
                    cc_dr.add(res.getString("pyd_empdept"));
                    cost.add(netpay);
                    basecost.add(netpay);
                    curr.add(defaultcurr);
                    basecurr.add(defaultcurr);
                    site.add(res.getString("py_site"));
                    ref.add(res.getString("py_id"));
                    type.add(thistype);
                    desc.add("CheckNbr:" + res.getString("pyd_checknbr"));
                    doc.add(gldoc);
                    }
                    
                    
                    
                    // NOW LETS DO WITHHOLDINGS...
                    // NOTE!!! THis needs to be broken into individual withholding accounts...currently lumped into one withholding account...with 'descriptions'
                      res = st.executeQuery("select py_id, py_site, pyd_checknbr, pyl_amt, pyl_profile, pyl_profile_line, pyl_type, pyl_code, pyl_desc, pyl_empnbr, pyd_empdept, pyd_emptype from pay_line " +
                              " inner join pay_det on pyd_id = pyl_id and pyd_empnbr = pyl_empnbr " +
                              " inner join pay_mstr on py_id = pyd_id  " +
                               " where pyl_type = 'deduction' and pyd_id = " + "'" + batch + "'" +";");
                   
                    amt = 0.00;   
                    while (res.next()) {
                     // credit withholding account and debit payroll tax expense
                     // lets determine tax account based on profile line
                     
                     
                    taxacct = OVData.getPayProfileDetAcct(res.getString("pyl_profile"), res.getString("pyl_profile_line"));
                    if (taxacct == null || taxacct.isEmpty()) {
                       taxacct = defaulttaxacct; 
                    }  
                     
                    amt = res.getDouble("pyl_amt");
                    acct_cr.add(taxacct);
                    if (res.getString("pyd_emptype").equals("Hourly-Direct")) {
                     acct_dr.add(laboracct);   
                    } else {
                     acct_dr.add(salariedacct);   
                    }
                    cc_cr.add(res.getString("pyd_empdept"));
                    cc_dr.add(res.getString("pyd_empdept"));
                      cost.add(amt);
                      basecost.add(amt);
                    curr.add(defaultcurr);
                    basecurr.add(defaultcurr);
                    site.add(res.getString("py_site"));
                    ref.add(res.getString("py_id"));
                    type.add(thistype);
                    desc.add("WithholdType:" + res.getString("pyl_desc"));  
                    doc.add(gldoc);
                    }
                    
                    
                    
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
                     
                     
                     
                     
                     
                     
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
        }
                
    public static boolean glEntryFromCashTranBuy(String voucher, Date effdate) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
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
               
               
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                   // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList cost =  new ArrayList();   
                    ArrayList basecost =  new ArrayList(); 
                    ArrayList curr =  new ArrayList(); 
                    ArrayList basecurr =  new ArrayList(); 
                   
                    String thistype = "RCT-VOUCH";
                    String gldoc = setGLRecNbr("AP");
                   
                       res = st.executeQuery("select pl_line, pl_po_rcpt, pl_inventory, ap_amt, ap_base_amt, ap_curr, ap_base_curr, ap_ref, ap_nbr, vod_item, ap_site, ap_acct, ap_cc, ap_vend, " +
                               " vod_qty, vod_voprice, vod_expense_acct, vod_expense_cc from vod_mstr " +
                               " inner join item_mstr on it_item = vod_item " +
                               " inner join pl_mstr on pl_line = it_prodline " +
                               "inner join ap_mstr on ap_nbr = vod_id and ap_type = 'V' where vod_id = " + "'" + voucher + "'" +";");
                   
                    Double amt = 0.00;   
                    while (res.next()) {
                     // credit vendor AP Acct (AP Voucher) and debit unvouchered receipts (po_rcpts acct)
                    amt = res.getDouble("vod_qty") * res.getDouble("vod_voprice");
                    acct_cr.add(res.getString("ap_acct"));
                    acct_dr.add(res.getString("vod_expense_acct"));
                    cc_cr.add(res.getString("ap_cc"));
                    cc_dr.add(res.getString("vod_expense_cc"));
                    cost.add(amt);
                    basecost.add(amt);
                    curr.add(res.getString("ap_curr"));
                    basecurr.add(res.getString("ap_base_curr"));
                    site.add(res.getString("ap_site"));
                    ref.add(res.getString("ap_nbr"));
                    type.add(thistype);
                    desc.add("cashtranvouch:" + res.getString("ap_ref") + "/" + res.getString("vod_item"));         
                    doc.add(gldoc);
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    
                         // Now we do the Rct-purch so that we add to inventory account
                         
                    acct_cr.add(res.getString("vod_expense_acct"));
                    acct_dr.add(res.getString("pl_inventory"));
                    cc_cr.add(res.getString("pl_line"));
                    cc_dr.add(res.getString("pl_line"));
                    cost.add(amt);
                    basecost.add(amt);
                    curr.add(res.getString("ap_curr"));
                    basecurr.add(res.getString("ap_base_curr"));
                    site.add(res.getString("ap_site"));
                    ref.add(res.getString("ap_nbr"));
                    type.add("RCT-PURCH");
                    desc.add("cashtranpurch:" + res.getString("ap_ref") + "/" + res.getString("vod_item"));      
                    doc.add(gldoc);
                    }
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
        }
      
    public static boolean _glEntryFromCashTranBuy(String voucher, Date effdate, String ctype, Connection bscon) throws SQLException {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
       
                Statement st = bscon.createStatement();
                ResultSet res = null;
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                   // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList cost =  new ArrayList();   
                    ArrayList basecost =  new ArrayList(); 
                    ArrayList curr =  new ArrayList(); 
                    ArrayList basecurr =  new ArrayList(); 
                   
                    String thistype = "RCT-VOUCH";
                   
                    // set parent GL doc number
                    String gldoc = fglData.setGLRecNbr("AP");
                    
                    
                       res = st.executeQuery("select pl_line, pl_po_rcpt, pl_inventory, ap_amt, ap_base_amt, ap_curr, ap_base_curr, ap_ref, ap_nbr, vod_item, ap_site, ap_acct, ap_cc, ap_vend, " +
                               " vod_qty, vod_voprice, vod_expense_acct, vod_expense_cc from vod_mstr " +
                               " inner join item_mstr on it_item = vod_item " +
                               " inner join pl_mstr on pl_line = it_prodline " +
                               "inner join ap_mstr on ap_nbr = vod_id and ap_type = 'V' where vod_id = " + "'" + voucher + "'" +";");
                   
                    Double amt = 0.00;   
                    while (res.next()) {
                     // credit vendor AP Acct (AP Voucher) and debit unvouchered receipts (po_rcpts acct)
                    amt = res.getDouble("vod_qty") * res.getDouble("vod_voprice");
                    acct_cr.add(res.getString("ap_acct"));
                    acct_dr.add(res.getString("vod_expense_acct"));
                    cc_cr.add(res.getString("ap_cc"));
                    cc_dr.add(res.getString("vod_expense_cc"));
                    cost.add(amt);
                    basecost.add(amt);
                    curr.add(res.getString("ap_curr"));
                    basecurr.add(res.getString("ap_base_curr"));
                    site.add(res.getString("ap_site"));
                    ref.add(res.getString("ap_nbr"));
                    doc.add(gldoc);
                    type.add(thistype);
                    desc.add("cashtranvouch:" + res.getString("ap_ref") + "/" + res.getString("vod_item"));         
               
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    
                         // Now we do the Rct-purch so that we add to inventory account
                         
                    acct_cr.add(res.getString("vod_expense_acct"));
                    acct_dr.add(res.getString("pl_inventory"));
                    cc_cr.add(res.getString("pl_line"));
                    cc_dr.add(res.getString("pl_line"));
                    cost.add(amt);
                    basecost.add(amt);
                    curr.add(res.getString("ap_curr"));
                    basecurr.add(res.getString("ap_base_curr"));
                    site.add(res.getString("ap_site"));
                    ref.add(res.getString("ap_nbr"));
                    type.add("RCT-PURCH");
                    desc.add("cashtranpurch:" + res.getString("ap_ref") + "/" + res.getString("vod_item"));      
                    
                    }
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
          
                     res.close();
                     st.close();
        return myerror;
        
        }
    
    public static boolean glEntryFromARMemo(String batchnbr, Date effdate) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
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
               
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                    // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList(); 
                    ArrayList<Double> basecost =  new ArrayList(); 
                    ArrayList curr =  new ArrayList(); 
                    ArrayList basecurr =  new ArrayList(); 
                   
                    String thistype = "";
                    String thisdesc = "AR Memo";
                    String gldoc = setGLRecNbr("AR");
                    
                    res = st.executeQuery("select ar_type, ard_acct, ard_cc, ard_id, ard_amt, ard_base_amt, ard_curr, ard_base_curr, ar_ref, ard_ref, ar_site, ar_acct, ar_cc from ard_mstr " +
                               " inner join ar_mstr on ar_nbr = ard_id  where ard_id = " + "'" + batchnbr + "'" +";");
                   
                    while (res.next()) {
                     // if CM credit cust acct and debit cash
                     // if DM debit cust acct and credit cash
                    if (res.getString("ar_type").equals("C")) {
                        thistype = "AR-MEMO-CM";
                       acct_cr.add(res.getString("ard_acct"));
                       acct_dr.add(res.getString("ar_acct"));
                       cc_cr.add(res.getString("ard_cc"));
                       cc_dr.add(res.getString("ar_cc")); 
                    } else {
                        thistype = "AR-MEMO-DM";
                       acct_cr.add(res.getString("ar_acct"));
                       acct_dr.add(res.getString("ard_acct"));
                       cc_cr.add(res.getString("ar_cc"));
                       cc_dr.add(res.getString("ard_cc"));  
                    }
                    
                    cost.add(res.getDouble("ard_amt"));
                    basecost.add(res.getDouble("ard_base_amt"));
                    curr.add(res.getString("ard_curr"));
                    basecurr.add(res.getString("ard_base_curr"));
                    site.add(res.getString("ar_site"));
                    ref.add(res.getString("ard_id"));
                    type.add(thistype);
                    desc.add("Memo " + res.getString("ard_ref"));
                    doc.add(gldoc);
                    
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                    
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
        }
                  
    public static boolean glEntryFromARPayment(String batchnbr, Date effdate) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try{
                Statement st = con.createStatement();
                Statement st2 = con.createStatement();
                Statement st3 = con.createStatement();
                ResultSet res = null;
                ResultSet res2 = null;
                ResultSet res3 = null;
                
               
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();   
                    ArrayList<Double> basecost =  new ArrayList(); 
                    ArrayList curr =  new ArrayList(); 
                    ArrayList basecurr =  new ArrayList(); 
                    
                    
                    String thistype = "AR-Payment";
                    String thisdesc = "";
                    String gldoc = fglData.setGLRecNbr("AR");
                
                   double net = 0.00;
                   double netbase = 0.00;
                   double amt = 0.00;
                   double baseamt = 0.00;
                    
                  
                   
                       res = st.executeQuery("select ard_id, ard_amt, ard_base_amt, ard_curr, ard_base_curr, ard_amt_tax, ard_base_amt_tax, ar_ref, ard_ref, ar_site, bk_acct, cm_ar_acct, cm_ar_cc from ard_mstr " +
                               " inner join ar_mstr on ar_nbr = ard_id " +
                               " inner join bk_mstr on bk_id = ar_bank " +
                               " inner join cm_mstr on cm_code = ar_cust where ard_id = " + "'" + batchnbr + "'" +";");
                   
                    while (res.next()) {
                     // credit AR Acct and debit cash account
                     thisdesc = "Cust Check: " + res.getString("ar_ref");
                     amt = res.getDouble("ard_amt");
                     baseamt = res.getDouble("ard_base_amt");
                     net = res.getDouble("ard_amt") - res.getDouble("ard_amt_tax"); // credit AR for sales less tax
                     netbase = res.getDouble("ard_base_amt") - res.getDouble("ard_base_amt_tax"); // credit AR for sales less tax
                     acct_cr.add(res.getString("cm_ar_acct"));
                    acct_dr.add(res.getString("bk_acct"));
                    cc_cr.add(res.getString("cm_ar_cc"));
                    cc_dr.add(res.getString("cm_ar_cc"));
                    cost.add(net);  // credit AR for sales less tax
                    basecost.add(netbase);  // credit AR for sales less tax
                    curr.add(res.getString("ard_curr"));
                    basecurr.add(res.getString("ard_base_curr"));
                    site.add(res.getString("ar_site"));
                    ref.add(res.getString("ard_ref"));
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
                                       
                    
                    // now lets do any taxes
                    res2 = st2.executeQuery("select ar_tax_code, ar_amt_tax, ar_base_amt_tax from ar_mstr where ar_nbr = " + "'" + res.getString("ard_ref") + "'" + ";");
                    int k = 0;
                    String artaxcode = "";
                    Double taxamt = 0.00;
                    Double basetaxamt = 0.00;
                    while (res2.next()) {
                        k++;
                        taxamt = res2.getDouble("ar_amt_tax");
                        basetaxamt = res2.getDouble("ar_base_amt_tax");
                        artaxcode = res2.getString("ar_tax_code");
                    }
                    res2.close();
                    if ( k > 0 ) {
                        // order level tax
                        if (taxamt > 0 && basetaxamt > 0) {
                            if (! artaxcode.isEmpty()) {
                                 ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(artaxcode);
                              for (String[] elements : taxelements) {
                                    // tax entries
                                    acct_cr.add(OVData.getDefaultTaxAcctByType(elements[2]));
                                    acct_dr.add(res.getString("bk_acct"));
                                    cc_cr.add(OVData.getDefaultTaxCCByType(elements[2]));
                                    cc_dr.add(res.getString("cm_ar_cc"));
                                    cost.add(( net * ( bsParseDouble(elements[1]) / 100 )));  // credit AR for sales less tax
                                    basecost.add(( netbase * ( bsParseDouble(elements[1]) / 100 )));  // credit AR for sales less tax
                                    curr.add(res.getString("ard_curr"));
                                    basecurr.add(res.getString("ard_base_curr"));
                                    site.add(res.getString("ar_site"));
                                    ref.add(res.getString("ard_ref"));
                                    type.add(thistype);
                                    desc.add(thisdesc);
                                    doc.add(gldoc);

                              }
                            }
                        }
                        // item level tax
                        String[] taxinfo = getARTaxMaterialOnly(res.getString("ard_ref"));
                        acct_cr.add(OVData.getDefaultTaxAcctByType(taxinfo[2]));
                        acct_dr.add(res.getString("bk_acct"));
                        cc_cr.add(OVData.getDefaultTaxCCByType(taxinfo[2]));
                        cc_dr.add(res.getString("cm_ar_cc"));
                        cost.add(bsParseDouble(taxinfo[3]));  // problem here...need art_base_amt as well as art_amt...an issue only for material tax with regard to currency.
                        basecost.add(bsParseDouble(taxinfo[3]));  // base and non-base currency material tax is the same...needs to be addresssed.
                        curr.add(res.getString("ard_curr"));
                        basecurr.add(res.getString("ard_base_curr"));
                        site.add(res.getString("ar_site"));
                        ref.add(res.getString("ard_ref"));
                        type.add(thistype);
                        desc.add(thisdesc); 
                        doc.add(gldoc);
                        
                    }
                    
                     // now lets do foreign currency gain/loss for any closed invoices
                    res3 = st3.executeQuery("select ar_curr, ar_base_curr, ar_amt, ar_base_amt, ar_status from ar_mstr " +
                            " where ar_nbr = " + "'" + res.getString("ard_ref") + "'" + 
                            " and ar_type = 'I' " + 
                            " and ar_status = 'c' " +         
                            ";");
                    Double gainloss = 0.00;
                    boolean isForeign = true;
                    while (res3.next()) {
                        gainloss = res3.getDouble("ar_base_amt") - baseamt;
                        if (res3.getString("ar_curr").toUpperCase().equals(res3.getString("ar_base_curr").toUpperCase())) {
                            isForeign = false;
                        }
                    }
                    res3.close();
                    st3.close();
                    if (gainloss != 0.00 && isForeign) {
                                    acct_cr.add(res.getString("cm_ar_acct"));
                                    acct_dr.add(OVData.getDefaultForeignCurrRealAcct());
                                    cc_cr.add(res.getString("cm_ar_cc"));
                                    cc_dr.add(res.getString("cm_ar_cc"));
                                    cost.add(gainloss);  
                                    basecost.add(gainloss); 
                                    curr.add(res.getString("ard_curr"));
                                    basecurr.add(res.getString("ard_base_curr"));
                                    site.add(res.getString("ar_site"));
                                    ref.add(res.getString("ard_ref"));
                                    type.add(thistype);
                                    desc.add(thisdesc);
                                    doc.add(gldoc);
                    }
                    
                    
                    
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                    res.close();
                    
                    
                    
                    
                    
                    
                     // process the arrays into glEntry
                    for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
        }
        
    public static boolean glEntryFromPOS(String batchnbr, Date effdate, Connection bscon) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            Statement st = bscon.createStatement();
            ResultSet res = null;
            try{
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                   
                       res = st.executeQuery("select pos_nbr, pos_tottax, pos_grossamt, pos_totamt, pos_bank, posc_taxacct, bk_acct, pos_aracct, pos_arcc from pos_mstr " +
                               " inner join pos_ctrl inner join bk_mstr on bk_id = pos_bank where pos_nbr = " + "'" + batchnbr + "'" +";");
                  
                       
                       // POS is always in base currency
                       String curr = OVData.getDefaultCurrency();
                       String basecurr = curr;
                       
                       // set parent GL doc number
                    String gldoc = fglData.setGLRecNbr("POS");
                       
                   ArrayList v_acct_cr = new ArrayList();
                    ArrayList v_ref =  new ArrayList();
                    ArrayList v_doc =  new ArrayList();
                    ArrayList v_desc =   new ArrayList();
                    ArrayList v_type =   new ArrayList();
                    ArrayList v_cc_cr =   new ArrayList();
                    ArrayList v_acct_dr =   new ArrayList();
                    ArrayList v_cc_dr =   new ArrayList();
                    ArrayList v_site =   new ArrayList();
                    ArrayList v_cost =  new ArrayList();
                   
                    int i = -1;
                    while (res.next()) {
                        i++;
                        
                    // credit vendor Default AR Acct and debit cash acct from posc_bank cash acct
                    v_acct_cr.add(res.getString("pos_aracct"));
                    v_acct_dr.add(res.getString("bk_acct"));
                    v_cc_cr.add(res.getString("pos_arcc"));
                    v_cc_dr.add(res.getString("pos_arcc"));
                    v_cost.add(res.getDouble("pos_grossamt"));
                    v_ref.add(res.getString("pos_nbr"));
                    v_site.add(OVData.getDefaultSite());
                    v_desc.add("Point Of Sales");
                    v_type.add("POS");
                    v_doc.add(gldoc);
                                 
          
                    // now do tax entry
                    v_acct_cr.add(res.getString("pos_aracct"));
                    v_acct_dr.add(res.getString("posc_taxacct"));
                    v_cc_cr.add(res.getString("pos_arcc"));
                    v_cc_dr.add(res.getString("pos_arcc"));
                    v_cost.add(res.getDouble("pos_tottax"));
                    v_ref.add(res.getString("pos_nbr"));
                    v_site.add(OVData.getDefaultSite());
                    v_desc.add("POS Sales Tax");
                    v_type.add("POS");
                    v_doc.add(gldoc);
                    }
                    res.close();
                    // process the arrays into glEntry
                    for (int j = 0; j < v_acct_cr.size(); j++) {
                      glEntryXP(bscon, v_acct_cr.get(j).toString(), v_cc_cr.get(j).toString(), v_acct_dr.get(j).toString(), v_cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(v_cost.get(j).toString()), bsParseDouble(v_cost.get(j).toString()), curr, basecurr, v_ref.get(j).toString(), v_site.get(j).toString(), v_type.get(j).toString(), v_desc.get(j).toString(), v_doc.get(j).toString());  
                    }
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            }
            finally {
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
            myerror = true;
        }
        return myerror;
        
        }
              
    public static void voidGLEntryFromPOS(String batchnbr, Date effdate, Connection bscon) throws SQLException {
            
               Statement st = bscon.createStatement();
               ResultSet res = null;
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                 // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList cost =  new ArrayList();   
                   
                    String thistype = "POS";
                    String thisdesc = "POS REVERSE";   
                       // set parent GL doc number
                    String gldoc = fglData.setGLRecNbr("POS");
                     // POS is always in base currency
                       String curr = OVData.getDefaultCurrency();
                       String basecurr = curr;
                   
                       res = st.executeQuery("select pos_nbr, pos_tottax, pos_grossamt, pos_totamt, pos_bank, posc_taxacct, bk_acct, pos_aracct, pos_arcc from pos_mstr " +
                               " inner join pos_ctrl inner join bk_mstr on bk_id = pos_bank where pos_nbr = " + "'" + batchnbr + "'" +";");
                   
                    while (res.next()) {
                     // credit vendor Default AR Acct and debit cash acct from posc_bank cash acct
                    acct_cr.add(res.getString("pos_aracct"));
                    acct_dr.add(res.getString("bk_acct"));
                    cc_cr.add(res.getString("pos_arcc"));
                    cc_dr.add(res.getString("pos_arcc"));
                    cost.add((-1 * res.getDouble("pos_grossamt")));
                    site.add(OVData.getDefaultSite());
                    ref.add(res.getString("pos_nbr"));
                    type.add(thistype);
                    desc.add(thisdesc); 
                    doc.add(gldoc);
          
                  
                    // now do tax entry
                    acct_cr.add(res.getString("pos_aracct"));
                    acct_dr.add(res.getString("posc_taxacct"));
                    cc_cr.add(res.getString("pos_arcc"));
                    cc_dr.add(res.getString("pos_arcc"));
                    cost.add((-1 * res.getDouble("pos_tottax")));
                    site.add(OVData.getDefaultSite());
                    ref.add(res.getString("pos_nbr"));
                    type.add(thistype);
                    desc.add(thisdesc);   
                    doc.add(gldoc);
                       
                    }
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(cost.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
                    
                    st.close();
                    res.close();
        }
       
    public static void _glEntryFromReceiver(recv_mstr rv, ArrayList<recv_det> rvd, Connection bscon) throws SQLException {
        
            Statement st = bscon.createStatement();
            Statement st2 = bscon.createStatement();
            ResultSet res = null;
            ResultSet nres = null;
                
               java.util.Date now = new java.util.Date();
               Date effdate = parseDate(rv.rv_recvdate());
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();   
                    ArrayList<Double> basecost =  new ArrayList();
                    ArrayList currarray =  new ArrayList();
                    ArrayList basecurrarray =  new ArrayList();
                   
                   
                    String thistype = "RCT-PURCH";
                    String gldoc = fglData.setGLRecNbr("AP");
                
                    String unvouchacct = "";
                    String unvouchcc = "";
                   
                    
                    String curr = getPOCurrency(rvd.get(0).rvd_po());
                    String basecurr = OVData.getDefaultCurrency();
                   
                    double thiscost = 0;
                    double costtot = 0;
                    double variance = 0;
                    double variancetot = 0;
                    
                   
                    res = st.executeQuery("select poc_rcpt_acct, poc_rcpt_cc from po_ctrl;");
                    while (res.next()) {
                        unvouchacct = res.getString("poc_rcpt_acct");
                        unvouchcc = res.getString("poc_rcpt_cc"); // not used at this time
                    }
                    res.close();
                    
                    for (recv_det z : rvd) { 
                        nres = st2.executeQuery("select  itc_total, pl_po_rcpt, pl_po_ovh, pl_line, pl_inventory, pl_po_pricevar, " +
                       " pl_cogs_mtl, pl_cogs_lbr, pl_cogs_bdn, pl_cogs_ovh, pl_cogs_out, pl_sales, itc_total, " +
                       " itc_mtl_top, itc_mtl_low, itc_lbr_top, itc_lbr_low, itc_bdn_top, itc_bdn_low, " +
                       " itc_ovh_top, itc_ovh_low, itc_out_top, itc_out_low, itc_bdn_top, itc_bdn_low " +
                       " from item_mstr  " + 
                       " inner join pl_mstr on pl_line = it_prodline " +
                       " inner join item_cost on itc_item = it_item and itc_set = 'standard' where it_item = " + "'" + z.rvd_item() + "'" + ";"
                        );
                    while (nres.next()) {
                    thiscost = nres.getDouble("itc_mtl_top") + nres.getDouble("itc_mtl_low");
                    costtot = thiscost * z.rvd_qty();
                    variance = thiscost - z.rvd_netprice();
                      if (! curr.toUpperCase().equals(basecurr.toUpperCase())) {
                          variance = thiscost - (OVData.getExchangeBaseValue(basecurr, curr, z.rvd_netprice()));
                      }
                    variancetot = variance * z.rvd_qty();
                     
                        // material cost
                    acct_cr.add(unvouchacct);
                    acct_dr.add(nres.getString("pl_inventory"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(costtot);
                    basecost.add(costtot);
                    site.add(rv.rv_site());
                    currarray.add(curr);
                    basecurrarray.add(basecurr);
                    ref.add(rv.rv_id());
                    type.add(thistype);
                    desc.add("Receipts"); 
                    doc.add(gldoc);
          
                   
          
                    // ppv 
                    acct_cr.add(nres.getString("pl_po_pricevar"));
                    acct_dr.add(unvouchacct);
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(variancetot);
                    basecost.add(variancetot);
                    site.add(rv.rv_site());
                    currarray.add(curr);
                    basecurrarray.add(basecurr);
                    ref.add(rv.rv_id());
                    type.add(thistype);
                    desc.add("Receipts"); 
                    doc.add(gldoc);
          
                    // overhead cost
                    acct_cr.add(unvouchacct);
                    acct_dr.add(nres.getString("pl_po_ovh"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_ovh_top") + nres.getDouble("itc_ovh_low")) * z.rvd_qty()));
                    basecost.add(((nres.getDouble("itc_ovh_top") + nres.getDouble("itc_ovh_low")) * z.rvd_qty()));
                    site.add(rv.rv_site());
                    currarray.add(curr);
                    basecurrarray.add(basecurr);
                    ref.add(rv.rv_id());
                    type.add(thistype);
                    desc.add("Receipts"); 
                    doc.add(gldoc);
          
               
          
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                    nres.close();
                  }  // for each rvd
                    for (int j = 0; j < acct_cr.size(); j++) {
                      glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), currarray.get(j).toString(), basecurrarray.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
            
            st.close();
            st2.close();
                    
    }
    
    
    public static void _glEntryFromShipper(String shipper, Date effdate, Connection bscon) throws SQLException {
        
            Statement st = bscon.createStatement();
            Statement st2 = bscon.createStatement();
            ResultSet res;
            ResultSet nres = null;
                
                double totamt = 0.00;
                double basetotamt = 0.00;
                double charges = 0.00;
                double tottax = 0.00;
                
                
                 // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();
                    ArrayList<Double> basecost =  new ArrayList();
                    String thissite = "";
                    String thisref = "";
                    String thistype = "ISS-SALES";
                    String thisdesc = "Sales Order Shipment";
                    
                       // set parent GL doc number
                    String gldoc = fglData.setGLRecNbr("SO");
                    
                    String aracct = "";
                    String cust = "";
                    String arcc = "";
                    String shiptype = "";
                   
                    
                    String part = "";
                    double qty = 0;
                    double baseqty = 0.0;
                    String uom = "";
                    String loc = "";
                    double netprice = 0.00;
                    double matltax = 0.00;
                    double basenetprice = 0.00;
                    double basematltax = 0.00;
                    
                    String taxcode = "";
                    String curr = "";
                    String custsalesacct = "";
                    String custsalescc = "";
                    String basecurr = "";
                    String defaultsalesacct = ""; // sales acct
                    String defaultsalescc = ""; // sales cc
                    String defaultassetacct = "";
                    String defaultassetcc = "";
                    String defaultshippingacct = ""; // shipping acct 
                    String apbankacct = ""; // OVData.getDefaultBankAcct(OVData.getDefaultAPBank());
                    String apacct = ""; // OVData.getDefaultAPAcct();
                    
                    int i = 0;
                   
                       res = st.executeQuery("select sh_site, sh_ar_acct, sh_taxcode, sh_curr, " +
                               " sh_ar_cc, sh_cust, sh_type, cm_ar_acct, cm_ar_cc, " +
                               " arc_sales_acct, arc_sales_cc, arc_asset_acct, arc_asset_cc, arc_varchar, " +
                               " ov_currency, bk_acct, apc_apacct " +
                               " from ship_mstr " +
                               " inner join cm_mstr on cm_code = sh_cust " +
                               " inner join ov_mstr " +
                               " inner join ar_ctrl " +
                               " inner join ap_ctrl " +
                               " inner join bk_mstr on bk_id = apc_bank " +
                               " where sh_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                        aracct = res.getString("sh_ar_acct");
                        arcc = res.getString("sh_ar_cc");
                        cust = res.getString("sh_cust");
                        thissite = res.getString("sh_site");
                        taxcode = res.getString("sh_taxcode");
                        shiptype = res.getString("sh_type");
                        curr = res.getString("sh_curr");
                        custsalesacct = res.getString("cm_ar_acct");
                        custsalescc = res.getString("cm_ar_cc");
                        defaultsalesacct = res.getString("arc_sales_acct");
                        defaultsalescc = res.getString("arc_sales_cc");
                        defaultassetacct = res.getString("arc_asset_acct");
                        defaultassetcc = res.getString("arc_asset_cc");
                        basecurr = res.getString("ov_currency");
                        defaultshippingacct = res.getString("arc_varchar");
                        apbankacct = res.getString("bk_acct");
                        apacct = res.getString("apc_apacct");
                    }
                    
                    
                      res = st.executeQuery("select shd_item, shd_qty, shd_uom, shd_loc, shd_id, " +
                              " shd_netprice, shd_taxamt, coalesce(it_uom, '') as ituom " +
                              " from ship_det " +
                              " left outer join item_mstr on it_item = shd_item " +
                              " where shd_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                        part = res.getString("shd_item");
                        qty = res.getDouble("shd_qty");
                        uom = res.getString("shd_uom");
                        loc = res.getString("shd_loc");
                        thisref = res.getString("shd_id");
                        if (! uom.toUpperCase().equals(res.getString("ituom").toUpperCase())) {
                        baseqty = OVData.getUOMBaseQty(part, thissite, uom, qty);
                        } else {
                        baseqty = qty;
                        }
                        netprice = res.getDouble("shd_netprice"); 
                        matltax += res.getDouble("shd_taxamt");
                        if (basecurr.toUpperCase().equals(curr.toUpperCase())) {
                        basenetprice = netprice;
                        basematltax = matltax;
                        } else {
                        basenetprice = OVData.getExchangeBaseValue(basecurr, curr, res.getDouble("shd_netprice"));  
                        basematltax += OVData.getExchangeBaseValue(basecurr, curr, res.getDouble("shd_taxamt"));
                        }
                        
                       
                        totamt += (qty * netprice);
                        basetotamt += (qty * basenetprice);
                        
                        
                        i = 0;
                        
                        nres = st2.executeQuery("select  itc_total, pl_scrap, pl_line, pl_inventory, " +
                       " pl_cogs_mtl, pl_cogs_lbr, pl_cogs_bdn, pl_cogs_ovh, pl_cogs_out, pl_sales, " +
                       " itc_mtl_top, itc_mtl_low, itc_lbr_top, itc_lbr_low, itc_bdn_top, itc_bdn_low, " +
                       " itc_ovh_top, itc_ovh_low, itc_out_top, itc_out_low, itc_bdn_top, itc_bdn_low " +
                       " from item_mstr  " + 
                       " inner join pl_mstr on pl_line = it_prodline " +
                       " inner join item_cost on itc_item = it_item and itc_set = 'standard' " +
                       " where it_item = " + "'" + part + "'" +  ";"
                        );
                    
                    while (nres.next()) {
                        i++;
                        // this assumes item is not miscellaenous...if so...just do credit sales and debit AR per customer master
                    
                                                
                     // material COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_mtl"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_mtl_top") + nres.getDouble("itc_mtl_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_mtl_top") + nres.getDouble("itc_mtl_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
                   
          
                    // labor COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_lbr"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_lbr_top") + nres.getDouble("itc_lbr_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_lbr_top") + nres.getDouble("itc_lbr_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
                             
                    // burden COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_bdn"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_bdn_top") + nres.getDouble("itc_bdn_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_bdn_top") + nres.getDouble("itc_bdn_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
                    
          
                    // overhead COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_ovh"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_ovh_top") + nres.getDouble("itc_ovh_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_ovh_top") + nres.getDouble("itc_ovh_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
          
                    // services COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_out"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_out_top") + nres.getDouble("itc_out_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_out_top") + nres.getDouble("itc_out_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
          
                    
                    // credit sales and debit AR
                    acct_cr.add(nres.getString("pl_sales"));
                    acct_dr.add(aracct);
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(arcc);
                    cost.add((res.getDouble("shd_netprice") * qty));
                    if (basecurr.toUpperCase().equals(curr.toUpperCase())) {
                     basecost.add((res.getDouble("shd_netprice") * qty));   
                    } else {
                     basecost.add((OVData.getExchangeBaseValue(basecurr, curr, res.getDouble("shd_netprice")) * qty));  
                    }
                    
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
          
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                       
                      if (i == 0) {
                          // must be misc...just do sales / AR GL transaction
                        if (shiptype.equals("A")) {  // if from asset transaction
                        acct_cr.add(defaultassetacct); 
                        cc_cr.add(defaultassetcc);
                        } else {
                        acct_cr.add(defaultsalesacct);  
                        cc_cr.add(defaultsalescc);
                        }
                        acct_dr.add(custsalesacct);
                        
                        cc_dr.add(custsalescc);
                        cost.add((res.getDouble("shd_netprice") * qty));
                        if (basecurr.toUpperCase().equals(curr.toUpperCase())) {
                        basecost.add((res.getDouble("shd_netprice") * qty));   
                        } else {
                        basecost.add((OVData.getExchangeBaseValue(basecurr, curr, res.getDouble("shd_netprice")) * qty));  
                        }
                        site.add(thissite);
                        ref.add(thisref);
                        type.add(thistype);
                        desc.add("Misc Item Shipment NonInventory");
                        doc.add(gldoc);
                      }  
                        
                        
                        
                    } // for each line on shipper
                    
                    
                    
                    // Tax entry if tottax > 0 necessary
                    // we will credit sales (income) acct and debit (liability) appropriate tax account for each tax element in cm_tax_code
                    tottax = shpData.getTaxAmtApplicableByShipper(shipper, totamt);
                    if (tottax > 0) {
                      ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(taxcode);
                          double taxvalue = 0;
                          double basetaxvalue = 0;
                          for (String[] elements : taxelements) {
                              taxvalue = totamt * ( bsParseDouble(elements[1]) / 100 );
                              basetaxvalue = basetotamt * ( bsParseDouble(elements[1]) / 100 );
                           //   bsmf.MainFrame.show(taxvalue + "/" + basetaxvalue + "/" + totamt + "/" + basetotamt );
                          glEntryXP(bscon, defaultsalesacct, defaultsalescc, OVData.getDefaultTaxAcctByType(elements[2]), OVData.getDefaultTaxCCByType(elements[2]), setDateDB(effdate), taxvalue, basetaxvalue, curr, basecurr, thisref, thissite, thistype, "Tax: " + elements[2], gldoc);
                          }
                          // now add matl tax at item level
                    }
                    // now add matl tax at item level
                    if (matltax > 0)
                    glEntryXP(bscon, defaultsalesacct, defaultsalescc, OVData.getDefaultTaxAcctByType("MATERIAL"), OVData.getDefaultTaxCCByType("MATERIAL"), setDateDB(effdate), matltax, basematltax, curr, basecurr, thisref, thissite, thistype, "Tax: Material ", gldoc);
                          
                    
                   // Trailer / Summary Charges
                    // we will credit sales and debit AR
                    ArrayList<String[]> sac = shpData.getShipperSAC(shipper);
                     
                   // charges = shpData.getShipperTrailerCharges(shipper);
                    for (String[] s : sac) {
                     if (Double.valueOf(s[4]) > 0) {
                        if (s[2].equals("charge") || s[2].equals("shipping ADD")) {
                        acct_cr.add(defaultsalesacct);
                        acct_dr.add(custsalesacct);
                        cc_cr.add(defaultsalescc);
                        cc_dr.add(custsalescc);
                        cost.add(bsParseDouble(s[4]));
                        if (basecurr.toUpperCase().equals(curr.toUpperCase())) {
                        basecost.add(bsParseDouble(s[4]));   
                        } else {
                        basecost.add(OVData.getExchangeBaseValue(basecurr, curr, bsParseDouble(s[4])));  
                        }
                        site.add(thissite);
                        ref.add(thisref);
                        type.add(thistype);
                        desc.add("Summary Charges for Shipper");
                        doc.add(gldoc);
                    
                          
                        } // if 'charge' or 'shipping ADD' type
                        
                    } // if charge > 0
                   } // for each sac charge
                    
                for (int j = 0; j < acct_cr.size(); j++) {
                    glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), cost.get(j), basecost.get(j), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                } 
            
            res.close();    
            if (nres != null) {       
              nres.close();
            }
            st.close();
            st2.close();
    }
               
    public static boolean glEntryFromShipperRV(String shipper, Date effdate) {
              boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try{
                Statement st = con.createStatement();
                Statement st2 = con.createStatement();
                ResultSet res = null;
                ResultSet nres = null;
               
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                 // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();
                    ArrayList<Double> basecost =  new ArrayList();
                    String thissite = "";
                    String thisref = "";
                    String thistype = "ISS-SALES";
                    String thisdesc = "Sales Order RV";
                    String gldoc = setGLRecNbr("RV");
                
                    String aracct = "";
                    String cust = "";
                    String arcc = "";
                    String part = "";
                    double qty = 0;
                    double baseqty = 0;
                    String loc = "";
                    String uom = "";
                    int i = 0;
                    
                    double totamt = 0.00;
                    double charges = 0.00;
                    double tottax = 0.00;
                    double basetotamt = 0.00;
                    double netprice = 0.00;
                    double basenetprice = 0.00;
                
                    String taxcode = "";
                    
                    
                     String curr = "";
                    String basecurr = OVData.getDefaultCurrency();
                   
                       res = st.executeQuery("select sh_site, sh_ar_acct, sh_ar_cc, sh_cust, sh_curr from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                        aracct = res.getString("sh_ar_acct");
                        arcc = res.getString("sh_ar_cc");
                        cust = res.getString("sh_cust");
                        thissite = res.getString("sh_site");
                        curr = res.getString("sh_curr");
                    }
                    
                      res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                        part = res.getString("shd_item");
                        qty = res.getDouble("shd_qty");
                        loc = res.getString("shd_loc");
                        thisref = res.getString("shd_id");
                        baseqty = OVData.getUOMBaseQty(part, thissite, uom, qty);
                        // reverse quantity
                        qty = -1 * qty;
                        baseqty = -1 * baseqty;
                        
                        
                        
                        if (basecurr.toUpperCase().equals(curr.toUpperCase())) {
                        netprice = res.getDouble("shd_netprice");   
                        } else {
                        basenetprice = OVData.getExchangeBaseValue(basecurr, curr, res.getDouble("shd_netprice"));  
                        }
                        
                       
                        totamt += (qty * netprice);
                        basetotamt += (qty * basenetprice);
                        
                        
                        
                        i = 0;
                        
                        nres = st2.executeQuery("select  itc_total, pl_scrap, pl_line, pl_inventory, " +
                       " pl_cogs_mtl, pl_cogs_lbr, pl_cogs_bdn, pl_cogs_ovh, pl_cogs_out, pl_sales, " +
                       " itc_mtl_top, itc_mtl_low, itc_lbr_top, itc_lbr_low, itc_bdn_top, itc_bdn_low, " +
                       " itc_ovh_top, itc_ovh_low, itc_out_top, itc_out_low, itc_bdn_top, itc_bdn_low " +
                       " from item_mstr  " + 
                       " inner join pl_mstr on pl_line = it_prodline " +
                       " inner join item_cost on itc_item = it_item and itc_set = 'standard' where it_item = " + "'" + part.toString() + "'" + ";"
                        );
                    
                    while (nres.next()) {
                        i++;
                        // this assumes item is not miscellaenous...if so...just do credit sales and debit AR per customer master
                        
                        
                     // material COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_mtl"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_mtl_top") + nres.getDouble("itc_mtl_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_mtl_top") + nres.getDouble("itc_mtl_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
                   
          
                    // labor COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_lbr"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_lbr_top") + nres.getDouble("itc_lbr_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_lbr_top") + nres.getDouble("itc_lbr_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
                             
                    // burden COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_bdn"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_bdn_top") + nres.getDouble("itc_bdn_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_bdn_top") + nres.getDouble("itc_bdn_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
                    
          
                    // overhead COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_ovh"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_ovh_top") + nres.getDouble("itc_ovh_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_ovh_top") + nres.getDouble("itc_ovh_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
          
                    // services COGS
                    acct_cr.add(nres.getString("pl_inventory"));
                    acct_dr.add(nres.getString("pl_cogs_out"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_out_top") + nres.getDouble("itc_out_low")) * baseqty));
                    basecost.add(((nres.getDouble("itc_out_top") + nres.getDouble("itc_out_low")) * baseqty));
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
          
          
                    
                     // credit sales and debit AR
                    acct_cr.add(nres.getString("pl_sales"));
                    acct_dr.add(aracct);
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(arcc);
                    cost.add((res.getDouble("shd_netprice") * qty));
                    if (basecurr.toUpperCase().equals(curr.toUpperCase())) {
                     basecost.add((res.getDouble("shd_netprice") * qty));   
                    } else {
                     basecost.add((OVData.getExchangeBaseValue(basecurr, curr, res.getDouble("shd_netprice")) * qty));  
                    }
                    site.add(thissite);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
          
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                        
                      if (i == 0) {
                          // must be misc...just do sales / AR GL transaction
                         acct_cr.add(OVData.getDefaultSalesAcct());
                        acct_dr.add(cusData.getCustSalesAcct(cust));
                        cc_cr.add(OVData.getDefaultSalesCC());
                        cc_dr.add(cusData.getCustSalesCC(cust));
                        cost.add((res.getDouble("shd_netprice") * qty));
                        if (basecurr.toUpperCase().equals(curr.toUpperCase())) {
                        basecost.add((res.getDouble("shd_netprice") * qty));   
                        } else {
                        basecost.add((OVData.getExchangeBaseValue(basecurr, curr, res.getDouble("shd_netprice")) * qty));  
                        }
                        site.add(thissite);
                        ref.add(thisref);
                        type.add(thistype);
                        desc.add("Misc Item Shipment NonInventory");
                        doc.add(gldoc);
                      }  
                                               
                    }
                    
                    
                      // Tax entry if tottax > 0 necessary
                    // we will credit sales (income) acct and debit (liability) appropriate tax account for each tax element in cm_tax_code
                    tottax = OVData.getTaxAmtApplicableByCust(cust, totamt);
                    if (tottax > 0) {
                      ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(taxcode);
                          for (String[] elements : taxelements) {
                          glEntry(OVData.getDefaultSalesAcct(), OVData.getDefaultSalesCC(), OVData.getDefaultTaxAcctByType(elements[2]), OVData.getDefaultTaxCCByType(elements[2]), setDateDB(effdate), ( totamt * ( bsParseDouble(elements[1]) / 100 )), ( basetotamt * ( bsParseDouble(elements[1]) / 100 )), curr, basecurr, thisref, thissite, thistype, "Tax: " + elements[2], gldoc);
                          }
                    }
                    
                   // Trailer / Summary Charges
                    // we will credit sales and debit AR
                    charges = shpData.getShipperTrailerCharges(shipper);
                    if (tottax > 0) {
                       acct_cr.add(OVData.getDefaultSalesAcct());
                        acct_dr.add(cusData.getCustSalesAcct(cust));
                        cc_cr.add(OVData.getDefaultSalesCC());
                        cc_dr.add(cusData.getCustSalesCC(cust));
                        cost.add(charges);
                        if (basecurr.toUpperCase().equals(curr.toUpperCase())) {
                        basecost.add(charges);   
                        } else {
                        basecost.add(OVData.getExchangeBaseValue(basecurr, curr, charges));  
                        }
                        site.add(thissite);
                        ref.add(thisref);
                        type.add(thistype);
                        desc.add("Summary Charges for Shipper");
                        doc.add(gldoc);
                    }
                    
                    
                      for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
         }
                
    public static boolean _glEntryFromCheckRun(int batchid, Date effdate, String ctype, Connection bscon) throws SQLException {
              boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
       
                Statement st = bscon.createStatement();
                ResultSet res = null;
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                 // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();   
                    ArrayList<Double> basecost =  new ArrayList();
                    ArrayList curr =  new ArrayList();
                    ArrayList basecurr =  new ArrayList();
                   
                    
                    
                       // set parent GL doc number
                    String gldoc = fglData.setGLRecNbr("AP");
                    
                    String thistype = ctype;
                    String thisdesc = "";   
                   
                  
                   
                    if (ctype.equals("AP-Expense")) {
                        thisdesc = "Expense Maint";
                    }
                    if (ctype.equals("AP-Cash")) {
                        thisdesc = "Cash Maint";
                    }
                    if (ctype.equals("AP-Vendor")) {
                        thisdesc = "Check Run";
                    }
                    
                    
                    res = st.executeQuery("select ap_check, ap_ref, ap_site, ap_acct, bk_acct, ap_cc, ap_amt, ap_base_amt, ap_curr, ap_base_curr from ap_mstr inner join bk_mstr on bk_id = ap_bank " +
                               " where (ap_type = 'C' or ap_type = 'E') AND ap_batch = " + "'" + batchid + "'" +";");
                    while (res.next()) {
                        acct_cr.add(res.getString("bk_acct"));
                        acct_dr.add(res.getString("ap_acct"));
                        cc_cr.add(res.getString("ap_cc"));
                        cc_dr.add(res.getString("ap_cc"));
                        cost.add(res.getDouble("ap_amt"));
                        basecost.add(res.getDouble("ap_base_amt"));
                        curr.add(res.getString("ap_curr"));
                        basecurr.add(res.getString("ap_base_curr"));
                        site.add(res.getString("ap_site"));
                        ref.add(res.getString("ap_check"));
                        type.add(thistype);
                        desc.add(thisdesc);
                        doc.add(gldoc);
                    }
                    
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(effdate), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
                 
                    st.close();
                    res.close();
           
        return myerror;
        
         }
    
    public static boolean _glEntryFromSrvJobScan(String shipper, Connection bscon) throws SQLException {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
       
          
                Statement st = bscon.createStatement();
                ResultSet res = null;
               
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                 // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList<Double> cost =  new ArrayList();   
                    ArrayList<Double> basecost =  new ArrayList();
                    ArrayList curr =  new ArrayList();
                    ArrayList basecurr =  new ArrayList();
                    ArrayList doc =  new ArrayList();
                   
                    String thistype = "ISS-SALES";
                    String thisdesc = "SERVICE ORDER SALES";   
                
                    double mtlcost = 0.00;
                    double lbrcost = 0.00;
                    double bdncost = 0.00;
                    String jobid = getShipperRef(shipper);
                    
                    if (jobid.isBlank()) {
                        return false;
                    }
                    
                    String bdn = getSysMetaValue("system", "glcontrol", "burden_rate");
                    double bdnrate = (bdn.isBlank()) ? 0.00 : bsParseDouble(bdn);
                    String cc = OVData.getDefaultCC();
                    String cogsmtl = "";
                    String cogslbr = "";
                    String cogsbdn = "";
                    String invacct = "";
                    String order = getPlanSrvOrderNumber(jobid);
                    sv_mstr sv = getServiceOrderMstr(new String[]{order});
                    
                    // set parent GL doc number
                    String gldoc = fglData.setGLRecNbr("SV");
                    
                    res = st.executeQuery("select * from pl_mstr where pl_line = " + "'" + cc + "'" + ";");
                    while (res.next()) {
                        invacct = res.getString("pl_inventory");
                        cogsmtl = res.getString("pl_cogs_mtl");
                        cogslbr = res.getString("pl_cogs_lbr");
                        cogsbdn = res.getString("pl_cogs_bdn");
                    }
                    
                    // get material cost
                    res = st.executeQuery("select * from plan_opdet where plod_parent = " + "'" + jobid + "'" 
                     + " and plod_consumable = '1' order by plod_op ;");
                    while (res.next()) {
                        mtlcost += res.getDouble("plod_qty") * res.getDouble("plod_cost");
                    }
                    
                    acct_cr.add(invacct);
                    acct_dr.add(cogsmtl);
                    cc_cr.add(cc);
                    cc_dr.add(cc);
                    cost.add(mtlcost);
                    basecost.add(mtlcost);
                    site.add(sv.sv_site());
                    curr.add(sv.sv_curr());
                    basecurr.add(sv.sv_curr());
                    ref.add(jobid);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc);
                    
                                        
                    // get labor cost
                     res = st.executeQuery("select jobc_planid, jobc_op, jobc_empnbr, jobc_qty, jobc_indate, jobc_intime, jobc_outdate, jobc_outtime, jobc_tothrs, jobc_code, emp_lname, emp_fname, emp_rate from job_clock inner join emp_mstr on emp_nbr = jobc_empnbr where jobc_planid = " + "'" + jobid + "'" 
                     + " order by jobc_indate ;");
                     while (res.next()) {
                        lbrcost += res.getDouble("emp_rate") * res.getDouble("jobc_tothrs");
                        bdncost += bdnrate * res.getDouble("jobc_tothrs");  // revisit...probably should be calculated based on start / finish or proj
                    }
                    
                    acct_cr.add(invacct);
                    acct_dr.add(cogslbr);
                    cc_cr.add(cc);
                    cc_dr.add(cc);
                    cost.add(lbrcost);
                    basecost.add(lbrcost);
                    site.add(sv.sv_site());
                    curr.add(sv.sv_curr());
                    basecurr.add(sv.sv_curr());
                    ref.add(jobid);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc); 
                    
                    // burden cost if any
                    if (bdnrate > 0) {
                    acct_cr.add(invacct);
                    acct_dr.add(cogsbdn);
                    cc_cr.add(cc);
                    cc_dr.add(cc);
                    cost.add(bdncost);
                    basecost.add(bdncost);
                    site.add(sv.sv_site());
                    curr.add(sv.sv_curr());
                    basecurr.add(sv.sv_curr());
                    ref.add(jobid);
                    type.add(thistype);
                    desc.add(thisdesc);
                    doc.add(gldoc); 
                    }
                  
                    res.close();
                    st.close();
                      for (int j = 0; j < acct_cr.size(); j++) {
                      glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), setDateDB(parseDate(mydate)), bsParseDouble(cost.get(j).toString()), bsParseDouble(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString(), doc.get(j).toString());  
                    }
          
        return myerror;
        
        }
    
    
    public static ArrayList getCurrlist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select cur_id from cur_mstr ;");
                while (res.next()) {
                    myarray.add(res.getString("cur_id"));

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
        return myarray;

    }

    public static ArrayList getdeptidlist() {
        ArrayList myarray = new ArrayList();
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

                res = st.executeQuery("select dept_id from dept_mstr ;");
                while (res.next()) {
                    myarray.add(res.getString("dept_id"));

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
        return myarray;

    }

         
    public static ArrayList getGLAcctList() {
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

                res = st.executeQuery("select ac_id from ac_mstr order by ac_id ;");
               while (res.next()) {
                    myarray.add(res.getString("ac_id"));
                    
                }
               
           }
            catch (SQLException s){
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
          
    public static ArrayList getGLAcctListByType(String type) {
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

                res = st.executeQuery("select ac_id from ac_mstr where ac_type = " + "'" + type + "'" + " order by ac_id ;");
               while (res.next()) {
                    myarray.add(res.getString("ac_id"));
                    
                }
               
           }
            catch (SQLException s){
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
       
    public static ArrayList getGLAcctExpenseDisplayOnly() {
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

            res = st.executeQuery("select ac_id from ac_mstr where ac_display = '1' and ac_type = " + "'" + 'e' + "'" + " order by ac_id ;");
           while (res.next()) {
                myarray.add(res.getString("ac_id"));

            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static ArrayList getGLAcctIncomeDisplayOnly() {
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

            res = st.executeQuery("select ac_id from ac_mstr where ac_display = '1' and ac_type = " + "'" + 'I' + "'" + " order by ac_id ;");
           while (res.next()) {
                myarray.add(res.getString("ac_id"));

            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    
    public static ArrayList getGLAcctListRange(String fromacct, String toacct) {
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

            res = st.executeQuery("select ac_id from ac_mstr where " +
                     " ac_id >= " + "'" + fromacct + "'" + " AND " +
                     " ac_id <= " + "'" +  toacct + "'" + "order by ac_id ;");
           while (res.next()) {
                myarray.add(res.getString("ac_id"));
            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static ArrayList getGLAcctListRangeWTypeDesc(String fromacct, String toacct) {
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

            res = st.executeQuery("select ac_id, ac_type, ac_desc from ac_mstr where " +
                     " ac_id >= " + "'" + fromacct + "'" + " AND " +
                     " ac_id <= " + "'" +  toacct + "'" + "order by ac_id ;");
           while (res.next()) {
                myarray.add(res.getString("ac_id") + "," + res.getString("ac_type") + "," + res.getString("ac_desc"));
            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static ArrayList<String[]> getGLAcctListRangeWCurrTypeDesc(String fromacct, String toacct) {
   ArrayList<String[]> myarray = new ArrayList();

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
            if (fromacct.isEmpty() && toacct.isEmpty()) {
                res = st.executeQuery("select ac_id, ac_cur, ac_type, ac_desc from ac_mstr order by ac_id ;");
            } else {
            res = st.executeQuery("select ac_id, ac_cur, ac_type, ac_desc from ac_mstr where " +
                     " ac_id >= " + "'" + fromacct + "'" + " AND " +
                     " ac_id <= " + "'" +  toacct + "'" + "order by ac_id ;");
            }
           while (res.next()) {
               String[] x = new String[4];
               x[0] = res.getString("ac_id");
               x[1] = res.getString("ac_cur");
               x[2] = res.getString("ac_type");
               x[3] = res.getString("ac_desc");
                myarray.add(x);
            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static String getGLAcctType(String acct) {
  String myreturn = "";
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

            res = st.executeQuery("select ac_type from ac_mstr where " +
                     " ac_id = " + "'" + acct + "'" + ";");
           while (res.next()) {
                myreturn = res.getString("ac_type");
            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static String getGLAcctDesc(String acct) {
  String myreturn = "";
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

            res = st.executeQuery("select ac_desc from ac_mstr where " +
                     " ac_id = " + "'" + acct + "'" + ";");
           while (res.next()) {
                myreturn = res.getString("ac_desc");
            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myreturn;

}

    public static ArrayList getGLCCList() {
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

            res = st.executeQuery("select dept_id from dept_mstr ;");
           while (res.next()) {
                myarray.add(res.getString("dept_id"));

            }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static String[] getGLCalForDate(Date EffDate) {
          // function returns a String array
          // first element = year  
          // second element = period 
          // third element = startdate 
          // fourth element = enddate 
          // fifth element = status 
  String[] x = new String[]{"","","","",""};        

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

            res = st.executeQuery("select * from gl_cal where glc_start <= " +
                    "'" + setDateDB(EffDate) + "'" + " AND glc_end >= " + "'" + setDateDB(EffDate) + "'" + ";");
           while (res.next()) {
                x[0] = res.getString("glc_year");
                x[1] = res.getString("glc_per");
                x[2] = res.getString("glc_start");
                x[3] = res.getString("glc_end");
                x[4] = res.getString("glc_status");
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

 
    public static ArrayList getGLCalByYearAndPeriod(String year, String per) {
          // function returns a 5 items from the gl_cal record where a date matches
          // 0 element = year  as int
          // 1 element = period as int
          // 2 element = startdate as string
          // 3 element = enddate as string
          // 4 element = status as string

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

             res = st.executeQuery("select * from gl_cal where glc_per = " +
                    "'" + per.toString() + "'" + 
                    " AND glc_year = " +
                    "'" + year.toString() + "'" + ";");
           while (res.next()) {
                myarray.add(res.getString("glc_year"));
                 myarray.add(res.getString("glc_per"));
                  myarray.add(res.getString("glc_start"));
                   myarray.add(res.getString("glc_end"));
                    myarray.add(res.getString("glc_status"));
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
    return myarray;

}

    public static ArrayList getGLCalForPeriod(int year, int per) {
          // function returns a 2 items from the gl_cal record where a period matches
          // first element = startdate
          // second element = enddate
    
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

            res = st.executeQuery("select * from gl_cal where glc_per = " +
                    "'" + bsFormatIntUS(per) + "'" + 
                    " AND glc_year = " +
                    "'" + bsFormatIntUS(year) + "'" + ";");
           while (res.next()) {
                  myarray.add(res.getString("glc_start"));
                   myarray.add(res.getString("glc_end"));
           }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return myarray;

}

    public static ArrayList getGLControl() {
              // function returns a 2 items from the gl_cal record where a period matches
              // first element = startdate
              // second element = enddate
              
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

                res = st.executeQuery("select * from gl_ctrl;"); 
               while (res.next()) {
                      myarray.add(res.getString("gl_bs_from"));
                       myarray.add(res.getString("gl_bs_to"));
                       myarray.add(res.getString("gl_is_from"));
                       myarray.add(res.getString("gl_is_to"));
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
        return myarray;
        
    }
     
    public static String getGLIncomeStatementFromAcct() {
              // function returns a 2 items from the gl_cal record where a period matches
              // first element = startdate
              // second element = enddate
              
      String account = "";
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

                res = st.executeQuery("select gl_is_from from gl_ctrl;"); 
               while (res.next()) {
                       account = res.getString("gl_is_from");
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
        return account;
        
    }
      
    public static String getGLIncomeStatementToAcct() {
              // function returns a 2 items from the gl_cal record where a period matches
              // first element = startdate
              // second element = enddate
              
      String account = "";
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

                res = st.executeQuery("select gl_is_to from gl_ctrl;"); 
               while (res.next()) {
                       account = res.getString("gl_is_to");
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
        return account;
        
    }
      
    public static String getGLBalanceSheetFromAcct() {
              // function returns a 2 items from the gl_cal record where a period matches
              // first element = startdate
              // second element = enddate
              
      String account = "";
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

                res = st.executeQuery("select gl_bs_from from gl_ctrl;"); 
               while (res.next()) {
                       account = res.getString("gl_bs_from");
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
        return account;
        
    }
      
    public static String getGLBalanceSheetToAcct() {
              // function returns a 2 items from the gl_cal record where a period matches
              // first element = startdate
              // second element = enddate
              
      String account = "";
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

                res = st.executeQuery("select gl_bs_to from gl_ctrl;"); 
               while (res.next()) {
                       account = res.getString("gl_bs_to");
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
        return account;
        
    }
         
    public static double getGLAcctBal(String site, String acct, String cc, String year, String per) {
              
              
       double amt = 0.00;
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
               
                res = st.executeQuery("select * from acb_mstr where acb_year = " +
                        "'" + year + "'" + 
                        " AND acb_per = " +
                        "'" + per + "'" +
                        " AND acb_site = " +
                        "'" + site + "'" +
                        " AND acb_acct = " +
                        "'" + acct + "'" +
                        " AND acb_cc = " +
                        "'" + cc + "'" +
                        ";");
                
                       while (res.next()) {
                          amt = res.getDouble(("acb_amt"));
                       }
               
           }
            catch (SQLException s){
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return amt;
        
    }
         
    public static double getGLAcctBalYTD(String site, String acct) {
   double amt = 0.00;
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

            res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where  " +
                    " acb_acct = " + "'" + acct + "'" +
                    " AND acb_site = " + "'" + site + "'" +
                    ";");

                   while (res.next()) {
                      amt = res.getDouble(("sum"));
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
    return amt;

}

    public static double getGLAcctBalAsOfDate(String site, String acct, String indate) { 
   double amt = 0.00;

    DateFormat dfdate = new SimpleDateFormat("yyyy");
    java.util.Date now = new java.util.Date();
    String currentyear = dfdate.format(now);

    int year = Integer.valueOf(indate.substring(0,4));
    int period = Integer.valueOf(indate.substring(5,7));
    int prioryear = year - 1;

    ArrayList<java.sql.Date> actdatearray = getGLCalForPeriod(year, period);  
            String datestart = String.valueOf(actdatearray.get(0));
            String dateend = String.valueOf(actdatearray.get(1));

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
            String accttype = "";
              res = st.executeQuery("select ac_type from ac_mstr where ac_id = " + "'" + acct + "'" +  ";");
              while (res.next()) {
                      accttype = res.getString("ac_type");
              }


              // get all acb_mstr records associated with this account PRIOR to this date's period
              if (accttype.equals("L") || accttype.equals("A") || accttype.equals("O")) {
                  //must be type balance sheet
              res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct = " + "'" + acct + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " (( acb_year = " + "'" + year + "'" + " AND acb_per < " + "'" + period + "'" + " ) OR " +
                    "  ( acb_year <= " + "'" + prioryear + "'" + " )) " +
                    ";");

                   while (res.next()) {
                      amt += res.getDouble("sum");
                   }
              } else {
                 // must be income statement
                  res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct = " + "'" + acct + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " ( acb_year = " + "'" + year + "'" + " AND acb_per < " + "'" + period + "'" + ")" +
                    ";");

                   while (res.next()) {
                      amt += res.getDouble("sum");
                   }
              }

         //  bsmf.MainFrame.show("1: " + datestart + "/" + dateend + "/" + amt);      
              // now get all transactions in gl_hist that equate to current period transactions of inbound date
              res = st.executeQuery("select sum(glh_base_amt) as sum from gl_hist " +
                    " where glh_acct = " + "'" + acct + "'" + " AND " + 
                    " glh_site = " + "'" + site + "'" + " AND " +
                    " glh_effdate >= " + "'" + datestart + "'" + " AND " +
                    " glh_effdate <= " + "'" + indate + "'" + 
                    " group by glh_acct ;");

                   while (res.next()) {
                      amt += res.getDouble("sum"); 
                   }

         // bsmf.MainFrame.show("2: " + datestart + "/" + indate + "/" + amt);         
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
    return amt;

}     

    public static double getGLAcctBalSummCC(String site, String acct, String year, String per) {


   double amt = 0.00;
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

            res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                    "'" + year + "'" + 
                    " AND acb_per = " +
                    "'" + per + "'" +
                    " AND acb_acct = " +
                    "'" + acct + "'" +
                    " AND acb_site = " +
                    "'" + site + "'" +
                    ";");

                   while (res.next()) {
                      amt = res.getDouble(("sum"));
                   }

       }
        catch (SQLException s){
             bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return amt;

}

    public static double getSummaryGLHist(String acct, String cc, String fromdate, String todate) {
         double myamt = 0.00;
         DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
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
            res = st.executeQuery("SELECT sum(glh_base_amt) as sum from gl_hist where " +
                    " glh_effdate >= " + "'" + fromdate + "'" + " AND " +
                    " glh_effdate <= " + "'" + todate + "'" + " AND " +
                    " glh_acct = " + "'" + acct + "'" + " AND " +
                    " glh_cc = " + "'" + cc + "'" + ";" );

            while (res.next()) {
               myamt = res.getDouble("sum");
            }

        } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
         return myamt;
     }

    public static double getSummaryGLHistSumCC(String acct, String fromdate, String todate) {
         double myamt = 0.00;
         DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
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
            res = st.executeQuery("SELECT sum(glh_base_amt) as sum from gl_hist where " +
                    " glh_effdate >= " + "'" + fromdate + "'" + " AND " +
                    " glh_effdate <= " + "'" + todate + "'" + " AND " +
                    " glh_acct = " + "'" + acct + "'" +  
                    " group by glh_acct " + ";" );
            while (res.next()) {
               myamt = res.getDouble("sum");
            }

        } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
         return myamt;
     }

    
    public static ArrayList getGLICDefsList() {
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

            res = st.executeQuery("select glic_name from glic_def;");
                   while (res.next()) {
                      mylist.add(res.getString(("glic_name")));
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
    return mylist;

}

    public static String getGLICDefsStart(String name) {
   String mystring = "";

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

            res = st.executeQuery("select glic_start from glic_def where glic_name = " + "'" + name + "'" + ";");
                   while (res.next()) {
                     mystring = res.getString("glic_start");
                   }

       }
        catch (SQLException s){
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    } catch (Exception e){
        MainFrame.bslog(e);
    }
    return mystring;

}

    public static String getGLICDefsEnd(String name) {
   String mystring = "";

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

            res = st.executeQuery("select glic_end from glic_def where glic_name = " + "'" + name + "'" + ";");
                   while (res.next()) {
                     mystring = res.getString("glic_end");
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
    return mystring;

}

    public static double getGLICElementSeq(String name) {
   double myreturn = 0;

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

            res = st.executeQuery("select glic_seq from glic_def where glic_name = " + "'" + name + "'"  + ";");
                   while (res.next()) {
                      myreturn = res.getDouble("glic_seq"); 
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
    return myreturn;

}

    public static ArrayList getGLICAccts(String name, String type) {
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

            res = st.executeQuery("select glicd_acct from glic_accts where glicd_name = " + "'" + name + "'" +
                    " AND glicd_type = " + "'" + type + "'" + ";");
                   while (res.next()) {
                      mylist.add(res.getString(("glicd_acct")));
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
    return mylist;

}

    public static Double getGLICBackOut(String acct, String site, String year, String per, Double begamt) {
          double myamt = 0.00;

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

            res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                   " acb_acct = " + "'" + acct + "'" + 
                    "AND acb_site = " + "'" + site + "'" +
                    " AND acb_year = " + "'" + year + "'" + 
                    " AND acb_per = " + "'" + per + "'" +
                    ";");
                   while (res.next()) {
                      myamt = begamt - res.getDouble("sum");
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

          return myamt;

      }

    public static Double getGLICAddIn(String acct, String site, String year, String per, Double begamt) {
          double myamt = 0.00;

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

            res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                   " acb_acct = " + "'" + acct + "'" + 
                    "AND acb_site = " + "'" + site + "'" +
                    " AND acb_year = " + "'" + year + "'" + 
                    " AND acb_per = " + "'" + per + "'" +
                    ";");
                   while (res.next()) {
                      myamt = begamt + res.getDouble("sum");
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

          return myamt;

      }

    public static ArrayList getGLBalanceRange(int fromyear, int toyear, String site) {
          java.util.Date now = new java.util.Date();
          DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
          ArrayList<String> mylist = new ArrayList<String>();   
          String[] fromdatearray = fglData.getGLCalForDate(now);

          int current_year = Integer.valueOf(fromdatearray[0].toString());
          int current_period = Integer.valueOf(fromdatearray[1].toString());
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

            int qty = 0;
            double dol = 0;
            int i = 0;

             int prioryear = 0;
             double begbal = 0.00;
             double activity = 0.00;
             double endbal = 0.00;
            String acctid = "";
            String accttype = "";
            String acctdesc = "";
            String[] ac = null;


             ArrayList<String> accounts = fglData.getGLAcctListRangeWTypeDesc("00000000", "9999999999");

            for (int year = fromyear; year <= toyear ; year++) {
                  prioryear = year - 1;
                  if (year > current_year)
                      break;

                for (int period = 1; period <= 12; period++ ) {
                     if (period > current_period && year == current_year)
                      break;

             ACCTS:    for (String account : accounts) {
              ac = account.split(",", -1);
              acctid = ac[0];
              accttype = ac[1];
              acctdesc = ac[2];


              begbal = 0.00;
              activity = 0.00;
              endbal = 0.00;




             // calculate all acb_mstr records for whole periods < fromdateperiod
                // begbal += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
              if (accttype.equals("L") || accttype.equals("A")) {
                  //must be type balance sheet
              res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct = " + "'" + acctid + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " (( acb_year = " + "'" + year + "'" + " AND acb_per < " + "'" + period + "'" + " ) OR " +
                    "  ( acb_year <= " + "'" + prioryear + "'" + " )) " +
                    ";");

                   while (res.next()) {
                      begbal += res.getDouble("sum");
                   }
              } else {
                 // must be income statement
                  res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct = " + "'" + acctid + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " ( acb_year = " + "'" + year + "'" + " AND acb_per < " + "'" + period + "'" + ")" +
                    ";");

                   while (res.next()) {
                      begbal += res.getDouble("sum");
                   }
              }


               // calculate period(s) activity defined by date range 
              // activity += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));

              res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                    "'" + String.valueOf(year) + "'" + 
                    " AND acb_per = " +
                    "'" + String.valueOf(period) + "'" +
                    " AND acb_acct = " +
                    "'" + acctid + "'" +
                    " AND acb_site = " + "'" + site + "'" +
                    ";");

                   while (res.next()) {
                      activity += res.getDouble(("sum"));
                   }

             endbal = begbal + activity;

             if ( endbal == 0 ) {
                 continue ACCTS;
             }

           //  if (begbal == 0 && endbal == 0 && activity == 0)
           //      bsmf.MainFrame.show(account);

             mylist.add(acctid + "," + acctdesc + "," + year + "," + period + "," + currformatDoubleUS(endbal) + ",");


                    } // account
                } // period
            } // year


        } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
          return mylist;
      }

    public static ArrayList getGLBalanceRangeXXX(int fromyear, int toyear, String site) {
          java.util.Date now = new java.util.Date();
          DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
          ArrayList<String> mylist = new ArrayList<String>();   
          String[] fromdatearray = fglData.getGLCalForDate(now);
          int current_year = Integer.valueOf(fromdatearray[0].toString());
          int current_period = Integer.valueOf(fromdatearray[1].toString());
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

            int qty = 0;
            double dol = 0;
            int i = 0;

             int prioryear = 0;
             double begbal = 0.00;
             double activity = 0.00;
             double endbal = 0.00;
            String acctid = "";
            String accttype = "";
            String acctdesc = "";
            String[] ac = null;


             ArrayList<String> accounts = fglData.getGLAcctListRangeWTypeDesc("00000000", "9999999999");

            for (int year = fromyear; year <= toyear ; year++) {
                  prioryear = year - 1;
                  if (year > current_year)
                      break;

                for (int period = 1; period <= 12; period++ ) {
                     if (period > current_period && year == current_year)
                      break;

             ACCTS:    for (String account : accounts) {
              ac = account.split(",", -1);
              acctid = ac[0];
              accttype = ac[1];
              acctdesc = ac[2];


              begbal = 0.00;
              activity = 0.00;
              endbal = 0.00;




             // calculate all acb_mstr records for whole periods < fromdateperiod
                // begbal += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
              if (accttype.equals("L") || accttype.equals("A")) {
                  //must be type balance sheet
              res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct = " + "'" + acctid + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " (( acb_year = " + "'" + year + "'" + " AND acb_per < " + "'" + period + "'" + " ) OR " +
                    "  ( acb_year <= " + "'" + prioryear + "'" + " )) " +
                    ";");

                   while (res.next()) {
                      begbal += res.getDouble("sum");
                   }
              } else {
                 // must be income statement
                  res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct = " + "'" + acctid + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " ( acb_year = " + "'" + year + "'" + " AND acb_per < " + "'" + period + "'" + ")" +
                    ";");

                   while (res.next()) {
                      begbal += res.getDouble("sum");
                   }
              }


               // calculate period(s) activity defined by date range 
              // activity += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));

              res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                    "'" + String.valueOf(year) + "'" + 
                    " AND acb_per = " +
                    "'" + String.valueOf(period) + "'" +
                    " AND acb_acct = " +
                    "'" + acctid + "'" +
                    " AND acb_site = " + "'" + site + "'" +
                    ";");

                   while (res.next()) {
                      activity += res.getDouble("sum");
                   }

             endbal = begbal + activity;

             if ( endbal == 0 ) {
                 continue ACCTS;
             }

           //  if (begbal == 0 && endbal == 0 && activity == 0)
           //      bsmf.MainFrame.show(account);
             if (accttype.equals("L") || accttype.equals("A")) {
             mylist.add(acctid + "," + acctdesc + "," + year + "," + period + "," + currformatDoubleUS(endbal) + ",");
             } else {
             mylist.add(acctid + "," + acctdesc + "," + year + "," + period + "," + currformatDoubleUS(activity) + ",");    
             }       


                    } // account
                } // period
            } // year


        } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
          return mylist;
      }

    public static ArrayList getGLBalanceRangeXXXByCC(int fromyear, int toyear, String site) {
          java.util.Date now = new java.util.Date();
          DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
          ArrayList<String> mylist = new ArrayList<String>();   
          String[] fromdatearray = fglData.getGLCalForDate(now);
          int current_year = Integer.valueOf(fromdatearray[0].toString());
          int current_period = Integer.valueOf(fromdatearray[1].toString());
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

            int qty = 0;
            double dol = 0;
             int i = 0;

             int prioryear = 0;
             double begbal = 0.00;
             double activity = 0.00;
             double endbal = 0.00;
             Map<String,Double> map = new HashMap<String,Double>();

            String acctid = "";
            String accttype = "";
            String acctdesc = "";
            String cc = "";
            String[] ac = null;

            ArrayList<String> glcontrol = getGLControl(); 
            String balfrom = glcontrol.get(0);
            String balto = glcontrol.get(1);
            String isfrom = glcontrol.get(2);
            String isto = glcontrol.get(3);

             ArrayList<String> accounts = fglData.getGLAcctListRangeWTypeDesc(balfrom, isto);
             ArrayList<String> cclist = fglData.getGLCCList();

            for (int year = fromyear; year <= toyear ; year++) {
                  prioryear = year - 1;
                  if (year > current_year)
                      break;

                for (int period = 1; period <= 12; period++ ) {
                     if (period > current_period && year == current_year)
                      break;

              begbal = 0.00;
              activity = 0.00;
              endbal = 0.00;

             // balance sheet first
              res = st.executeQuery("select acb_acct, acb_cc, sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct >= " + "'" + balfrom + "'" + " AND " +
                    " acb_acct <= " + "'" + balto + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " (( acb_year = " + "'" + year + "'" + " AND acb_per <= " + "'" + period + "'" + " ) OR " +
                    "  ( acb_year <= " + "'" + prioryear + "'" + " )) " +
                    " group by acb_acct, acb_cc order by acb_acct, acb_cc, acb_year, acb_per;");

                   while (res.next()) {
                       endbal = res.getDouble("sum");
                       acctid = res.getString("acb_acct");
                       cc = res.getString("acb_cc");
                    mylist.add(acctid + "," + cc + "," + period + "," + year + "," + currformatDoubleUS(endbal) );
                   }

             //now income statement
              res = st.executeQuery("select acb_acct, acb_cc, sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct >= " + "'" + isfrom + "'" + " AND " +
                    " acb_acct <= " + "'" + isto + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " acb_year = " + "'" + year + "'" + " AND " +
                    " acb_per  = " + "'" + period + "'" + 
                    " group by acb_acct, acb_cc order by acb_acct, acb_cc, acb_year, acb_per;");

                   while (res.next()) {
                       endbal = res.getDouble("sum");
                       acctid = res.getString("acb_acct");
                       cc = res.getString("acb_cc");
                    mylist.add(acctid + "," + cc + "," + period + "," + year + "," + currformatDoubleUS(endbal) );
                   }      



                } // period
            } // year


        } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
          return mylist;
      }

    public static ArrayList getGLBalByYearByPeriod(int fromyear, int toyear, int fromper, int toper, String site, boolean supress, boolean bsact) {
          java.util.Date now = new java.util.Date();
          DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
          ArrayList<String> mylist = new ArrayList<String>();   
          String[] fromdatearray = fglData.getGLCalForDate(now);
          int current_year = Integer.valueOf(fromdatearray[0].toString());
          int current_period = Integer.valueOf(fromdatearray[1].toString());
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

            int qty = 0;
            double dol = 0;
            int i = 0;

             int prioryear = 0;
             double begbal = 0.00;
             double activity = 0.00;
             double endbal = 0.00;
             Map<String,Double> map = new HashMap<String,Double>();

            String acctid = "";
            String accttype = "";
            String acctdesc = "";
            String cc = "";
            String[] ac = null;


             ArrayList<String> glcontrol = getGLControl(); 
            String balfrom = glcontrol.get(0);
            String balto = glcontrol.get(1);
            String isfrom = glcontrol.get(2);
            String isto = glcontrol.get(3);

             ArrayList<String> accounts = fglData.getGLAcctListRangeWTypeDesc(balfrom, isto);
             ArrayList<String> cclist = fglData.getGLCCList();

            for (int year = fromyear; year <= toyear ; year++) {
                  prioryear = year - 1;
                  if (year > current_year)
                      break;

                for (int period = fromper; period <= toper; period++ ) {
                     if (period > current_period && year == current_year)
                      break;

              begbal = 0.00;
              activity = 0.00;
              endbal = 0.00;

             // balance sheet first
              if (! bsact) {
              res = st.executeQuery("select acb_acct, acb_cc, sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct >= " + "'" + balfrom + "'" + " AND " +
                    " acb_acct <= " + "'" + balto + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " (( acb_year = " + "'" + year + "'" + " AND acb_per <= " + "'" + period + "'" + " ) OR " +
                    "  ( acb_year <= " + "'" + prioryear + "'" + " )) " +
                    " group by acb_acct, acb_cc order by acb_acct, acb_cc, acb_year, acb_per;");
              } else {
                  res = st.executeQuery("select acb_acct, acb_cc, sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct >= " + "'" + balfrom + "'" + " AND " +
                    " acb_acct <= " + "'" + balto + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " acb_year = " + "'" + year + "'" + " AND " +
                    " acb_per  = " + "'" + period + "'" + 
                    " group by acb_acct, acb_cc order by acb_acct, acb_cc, acb_year, acb_per;");
              }
                   while (res.next()) {
                       endbal = res.getDouble("sum");
                       acctid = res.getString("acb_acct");
                       cc = res.getString("acb_cc");
                       if (supress && endbal == 0) 
                           continue;
                    mylist.add(acctid + "," + cc + "," + period + "," + year + "," + currformatDoubleUS(endbal) );
                   }




             //now income statement
           // this assumes Income statement activity ONLY

                   res = st.executeQuery("select acb_acct, acb_cc, sum(acb_amt) as sum from acb_mstr where " +
                    " acb_acct >= " + "'" + isfrom + "'" + " AND " +
                    " acb_acct <= " + "'" + isto + "'" + " AND " +
                    " acb_site = " + "'" + site + "'" + " AND " +
                    " acb_year = " + "'" + year + "'" + " AND " +
                    " acb_per  = " + "'" + period + "'" + 
                    " group by acb_acct, acb_cc order by acb_acct, acb_cc, acb_year, acb_per;");


                   while (res.next()) {
                       endbal = res.getDouble("sum");
                       acctid = res.getString("acb_acct");
                       cc = res.getString("acb_cc");
                       if (supress && endbal == 0) 
                           continue;
                    mylist.add(acctid + "," + cc + "," + period + "," + year + "," + currformatDoubleUS(endbal) );

                   }      



                } // period
            } // year


        } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
          return mylist;
      }


    public static String[] getYearEndValues(String site, String year) {
       String[] myarray = new String[5];
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

        int qty = 0;
        double dol = 0;

        int i = 0;



        double amt = 0;
        double i_amt = 0;
        double e_amt = 0;
        String acct = "";
        String acctdesc = "";
        String accttype = "";

       double current_retearn = getGLAcctBalYTD(site, OVData.getDefaultRetainedEarningsAcct()); 

       res = st.executeQuery("select acb_acct, ac_desc, ac_type, sum(acb_amt) as sum from acb_mstr " +
                " inner join ac_mstr on ac_id = acb_acct " +
                "where acb_year = " + "'" + year + "'" +
                " and acb_acct >= " + "'" + getGLIncomeStatementFromAcct() + "'" +
                " and acb_acct <= " + "'" + getGLIncomeStatementToAcct() + "'" +
                " group by acb_acct, ac_desc, ac_type " +
                ";");
        while (res.next()) {
           amt = res.getDouble("sum");
           acct = res.getString("acb_acct");
           accttype = res.getString("ac_type");
           acctdesc = (res.getString("ac_desc") == null) ? "" : res.getString("ac_desc");
          i++;

          if (accttype.equals("I")) {
              i_amt += amt;
          }
          if (accttype.equals("E")) {
              e_amt += amt;
          }
        }

        myarray[0] = bsFormatDouble(current_retearn);
        myarray[1] = bsFormatDouble(abs(i_amt));
        myarray[2] = bsFormatDouble(abs(e_amt));
        myarray[3] = bsFormatDouble(abs(i_amt) - abs(e_amt));
        myarray[4] = bsFormatDouble(current_retearn + (abs(i_amt) - abs(e_amt)));

    } catch (SQLException s) {
        MainFrame.bslog(s);

    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
} catch (Exception e) {
    MainFrame.bslog(e);
}
return myarray;
}

    public static void setYearEndValues(String site, String year) {

       String[] myarray = new String[2];
       String gldoc = setGLRecNbr("YE");
       ArrayList<String[]> accounts = new ArrayList<String[]>();

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

        int qty = 0;
        double dol = 0;

        int i = 0;


        // always base currency
        String curr = OVData.getDefaultCurrency();
        String basecurr = curr;


        double amt = 0;
        String acct = "";
        String date = year + "-12" + "-31";

       String cc = OVData.getDefaultCC();
       String re_acct = OVData.getDefaultRetainedEarningsAcct();
       double re_value = getGLAcctBalYTD(site, re_acct); 

       res = st.executeQuery("select acb_acct, ac_desc, ac_type, sum(acb_amt) as sum from acb_mstr " +
                " inner join ac_mstr on ac_id = acb_acct " +
                "where acb_year = " + "'" + year + "'" +
                " and acb_acct >= " + "'" + getGLIncomeStatementFromAcct() + "'" +
                " and acb_acct <= " + "'" + getGLIncomeStatementToAcct() + "'" +
                " group by acb_acct, ac_desc, ac_type " +
                ";");
        while (res.next()) {
           amt = res.getDouble("sum");
           acct = res.getString("acb_acct");


          // insert the negative of the account summary into a temp ArrayList to be added back through glentry
          if (amt != 0) {
              String[] c = new String[2];
              c[0] = acct;
              c[1] = currformatDoubleUS(-1 * amt);
              accounts.add(i, c);
               i++;
          }
        }



        // now do glentry for all the reversed accounts in the arraylist..washing against the Retained Earnings account
         for (String[] a : accounts) {
              fglData.glEntry(re_acct, cc, a[0], cc, date, bsParseDouble(a[1]), bsParseDouble(a[1]), curr, basecurr, "YearEndClose", site, "GL", "YearEndClose", gldoc);  
         }

       // now post
       PostGL();

    } catch (SQLException s) {
        MainFrame.bslog(s);

    } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
} catch (Exception e) {
    MainFrame.bslog(e);
}

}

           
    public static void AcctBalEntry(String site, String acct, String cc, double amt, String EffDate) {
           try {
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            
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
                int per = 0;
                int year = 0;
                double newamt = 0.00;
                res = st.executeQuery("select * from gl_cal where glc_start <= " +
                        "'" + EffDate.toString() + "'" + 
                        " AND glc_end >= " +
                        "'" + EffDate.toString() + "'" + ";");
                
               while (res.next()) {
                  per = res.getInt("glc_per");
                  year = res.getInt("glc_year");
                  i++;
               }
              
               if (i > 0 && per != 0 && year != 0) {
                   
                   int j = 0;
                   
                   res = st.executeQuery("select * from acb_mstr where acb_year = " +
                        "'" + year + "'" + 
                        " AND acb_per = " +
                        "'" + per + "'" +
                        " AND acb_site = " +
                        "'" + site + "'" +
                           " AND acb_acct = " +
                        "'" + acct + "'" +
                        " AND acb_cc = " +
                        "'" + cc + "'" +
                        ";");
                
                       while (res.next()) {
                          j++;
                          newamt = amt + res.getDouble(("acb_amt"));
                       }
                   
                     if (j > 0) { 
                     st.executeUpdate("update acb_mstr set "
                            + " acb_amt = " + "'" + currformatDoubleUS(newamt).replace(defaultDecimalSeparator, '.') + "'"
                            + " where acb_acct = " + "'" + acct + "'" 
                            + " AND acb_cc = " + "'" + cc + "'" 
                             + " AND acb_site = " + "'" + site + "'" 
                             + " AND acb_year = " + "'" + year + "'"
                             + " AND acb_per = " + "'" + per + "'"
                                + ";");
                     } else {
                         newamt = amt;
                         st.executeUpdate("insert into acb_mstr values ( "
                                  + "'" + acct + "'" + "," 
                                  + "'" + cc + "'" + "," 
                                  + "'" + per + "'" + "," 
                                  + "'" + year + "'" + "," 
                                  + "'" + currformatDoubleUS(newamt).replace(defaultDecimalSeparator, '.') + "'" + ","
                                  + "'" + site + "'" 
                                  + ");");
                                  
                     }   
               }
               
               
            } catch (SQLException s) {
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
           
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       }

    public static void PostGL() {
       try {
        ArrayList<Integer> gltran = new ArrayList();

        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass); 
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
            double amt = 0.00;
            double newamt = 0.00;
            int i = 0;
            ArrayList acct_list = new ArrayList();
            ArrayList cc_list =   new ArrayList();
            ArrayList site_list =   new ArrayList();
            ArrayList amt_list =  new ArrayList();   
            ArrayList per_list =  new ArrayList();
            ArrayList year_list =  new ArrayList();
            
            

            // get IDs to move to gl_hist
            res = st.executeQuery("select glt_id " +
                    "  from gl_tran " +
                    " inner join gl_cal on glc_start <= glt_effdate " +
                    " and glc_end >= glt_effdate " +
                    " ;");
            while (res.next()) {
                i++;
                gltran.add(res.getInt("glt_id"));
            }
            res.close();
            
            Collections.sort(gltran);

            
            /*
            if (i > 0) {            
                if (dbtype.equals("sqlite")) {
                 st.executeUpdate("begin transaction;");
                } else {
                 st.executeUpdate("start transaction;");  
                }
            }
            */
            
            if (i > 0) {
            // now get group by sums of those IDs
            res = st.executeQuery("select glt_site, glt_acct, glt_cc, " +
                    " sum(glt_base_amt) as 'sum', glc_per, glc_year from gl_tran " +
                    " inner join gl_cal on glc_start <= glt_effdate " +
                    " and glc_end >= glt_effdate " +
                    " and glt_id >= " + "'" + gltran.get(0) + "'" +
                    " and glt_id <= " + "'" + gltran.get(gltran.size() - 1) + "'" +
                    " group by glt_acct, glt_cc, glc_per, glc_year, glt_site ;");
            
            while (res.next()) {
                acct_list.add(res.getString("glt_acct"));
                cc_list.add(res.getString("glt_cc"));
                per_list.add(res.getString("glc_per"));
                year_list.add(res.getString("glc_year"));
                site_list.add(res.getString("glt_site"));
                amt_list.add(res.getString("sum"));
            }
            res.close();
            
            int j = 0;
            for (int k = 0; k < acct_list.size(); k++) {
               j = 0;
               res = st.executeQuery("select * from acb_mstr where acb_year = " +
                    "'" + year_list.get(k) + "'" + 
                    " AND acb_per = " +
                    "'" + per_list.get(k) + "'" +
                    " AND acb_site = " +
                    "'" + site_list.get(k) + "'" +
                    " AND acb_acct = " +
                    "'" + acct_list.get(k) + "'" +
                    " AND acb_cc = " +
                    "'" + cc_list.get(k) + "'" +
                    ";");
                   while (res.next()) {
                      j++;
                      newamt = res.getDouble(("acb_amt")) + Double.valueOf(amt_list.get(k).toString());
                   }
                   res.close();

                 if (j > 0) {
                 st.executeUpdate("update acb_mstr set "
                        + " acb_amt = " + "'" + currformatDoubleUS(newamt).replace(defaultDecimalSeparator, '.') + "'"
                        + " where acb_acct = " + "'" + acct_list.get(k) + "'" 
                        + " AND acb_cc = " + "'" + cc_list.get(k) + "'" 
                         + " AND acb_site = " + "'" + site_list.get(k) + "'" 
                         + " AND acb_year = " + "'" + year_list.get(k) + "'"
                         + " AND acb_per = " + "'" + per_list.get(k) + "'"
                            + ";");
                 } else {
                     newamt = Double.valueOf(amt_list.get(k).toString());
                     st.executeUpdate("insert into acb_mstr values ( "
                              + "'" + acct_list.get(k) + "'" + "," 
                              + "'" + cc_list.get(k) + "'" + "," 
                              + "'" + per_list.get(k) + "'" + "," 
                              + "'" + year_list.get(k) + "'" + "," 
                              + "'" + currformatDoubleUS(newamt).replace(defaultDecimalSeparator, '.') + "'" + ","
                              + "'" + site_list.get(k) + "'" 
                              + ");");
                 }   
            }
            
             
                 /*
                st.executeUpdate("commit;");
                */
                glCopyTranToHist(gltran);
             }
             
        } catch (SQLException s) {
            MainFrame.bslog(s);
            bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }

    } catch (Exception e) {
        MainFrame.bslog(e);
    }
   }

    public static void glCopyTranToHist(ArrayList<Integer> trans) {
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

             if (dbtype.equals("sqlite")) {
             st.executeUpdate("begin transaction;");
            } else {
             st.executeUpdate("start transaction;");  
            }

           for (int tran : trans) {
                   st.executeUpdate("insert into gl_hist "
                    + "(glh_ref, glh_effdate, glh_entdate, glh_acct, "
                    + "glh_cc, glh_amt, glh_base_amt, glh_site, glh_doc, glh_line,"
                    + "glh_type, glh_curr, glh_base_curr, glh_desc, glh_userid) "
                    + " select glt_ref, glt_effdate, glt_entdate, glt_acct, "
                    + " glt_cc, glt_amt, glt_base_amt, glt_site, glt_doc, glt_line, "
                    + " glt_type, glt_curr, glt_base_curr, glt_desc, glt_userid " 
                    + " from gl_tran where glt_id = " + "'" + tran + "'" 
                    + ";");
                   st.executeUpdate("delete from gl_tran "
                    + " where glt_id = " + "'" + tran + "'" 
                    + ";");
           }

             st.executeUpdate("commit;");


        } catch (SQLException s) {
           MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }

    } catch (Exception e) {
        MainFrame.bslog(e);
    }
   }

    public static int clearGLEntries() {
        int rows = 0;
        String sql = "delete from gl_tran;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        rows = ps.executeUpdate();
        } catch (SQLException s) {
	       MainFrame.bslog(s);
        }
        
        sql = "delete from gl_hist; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        rows += ps.executeUpdate();
        } catch (SQLException s) {
	       MainFrame.bslog(s);
        }
        
        sql = "delete from acb_mstr; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.executeUpdate(); // not added to row count
        } catch (SQLException s) {
	       MainFrame.bslog(s);
        }
        
        return rows;
    }
    
    public record AcctMstr(String[] m, String id, String desc, String type, String currency, String cbdisplay) {
        public AcctMstr(String[] m) {
            this(m, "", "", "", "", "0");
        }
    }
    
    public record BankMstr(String[] m, String id, String site, String desc, String account, String routing, String assignedID, String currency, String cbactive) {
        public BankMstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "0");
        }
    }
    
    public record dept_mstr(String[] m, String dept_id, String dept_desc, String dept_cop_acct, 
        String dept_lbr_acct, String dept_bdn_acct, String dept_lbr_usg_acct, String dept_lbr_rate_acct, 
        String dept_bdn_usg_acct, String dept_bdn_rate_acct) {
        public dept_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record tax_mstr(String[] m, String tax_code, String tax_desc, String tax_crtdate, 
        String tax_moddate, String tax_userid) {
        public tax_mstr(String[] m) {
            this(m, "", "", "", "", "");
        }
    }
    
    public record taxd_mstr(String[] m, String taxd_parentcode, String taxd_id,  String taxd_desc, 
        String taxd_type, String taxd_percent, String taxd_crtdate, String taxd_moddate, String taxd_enabled, 
        String taxd_userid, String taxd_line) {
        public taxd_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "");
        }
    }
    
    
    public record CurrMstr(String[] m, String id, String desc) {
        public CurrMstr(String[] m) {
            this(m, "", "");
        }
    }
    
    public record gl_ctrl(String[] m, String gl_bs_from, String gl_bs_to, String gl_is_from,
        String gl_is_to, String gl_earnings, String gl_foreignreal, String gl_autopost) {
        public gl_ctrl(String[] m) {
            this(m, "", "", "", "", "", "", "");
        }
    }
    
    public record pay_ctrl(String[] m, String payc_bank, String payc_labor_acct, String payc_labor_cc,
        String payc_salaried_acct, String payc_salaried_cc, String payc_payrolltax_acct, String payc_payrolltax_cc,
        String payc_withhold_acct, String payc_varchar ) {
        public pay_ctrl(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record gl_tran(String[] m, String glt_id, String glt_ref, String glt_effdate,
        String glt_entdate, String glt_ts, String glt_acct, String glt_cc,
        double glt_amt, double glt_base_amt, String glt_site, String glt_doc,
        String glt_line, String glt_type, String glt_curr, String glt_base_curr,
        String glt_desc, String glt_userid) {
        public gl_tran(String[] m) {
            this(m, "", "", "", "", "", "", "", 0.00, 0.00, "",
                    "", "", "", "", "", "", "");
        }
    }
    
    public record gl_pair(String[] m, String glv_acct_cr, String glv_cc_cr, 
        String glv_acct_dr, String glv_cc_dr, String glv_date,
        Double glv_amt, Double glv_baseamt, String glv_curr, String glv_basecurr,
        String glv_ref, String glv_type, String glv_site, String glv_desc, String glv_doc) {
        public gl_pair(String[] m) {
            this(m, "", "", "", "", "", 0.00, 0.00, "", "", "",
                    "", "", "", "");
        }
    }
    
}


