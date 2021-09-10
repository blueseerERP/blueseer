/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.lang.invoke.MethodHandles;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author terryva
 */
public class fglData {
  
    
    public static String[] addAcctMstr(AcctMstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from ac_mstr where ac_id = ?";
        String sqlInsert = "insert into ac_mstr (ac_id, ac_desc, ac_type, ac_cur, ac_display)  " +
                " values (?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.id);
            psi.setString(2, x.desc);
            psi.setString(3, x.type);
            psi.setString(4, x.currency);
            psi.setInt(5, x.cbdisplay);
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
        
    public static String[] updateAcctMstr(AcctMstr x) {
        String[] m = new String[2];
        String sql = "update ac_mstr set ac_desc = ?, ac_type = ?, ac_cur = ?, " +
                " ac_display = ? where ac_id = ? ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.desc);
        ps.setString(2, x.type);
        ps.setString(3, x.currency);
        ps.setInt(4, x.cbdisplay);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new AcctMstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new AcctMstr(m, res.getString("ac_id"), 
                            res.getString("ac_desc"),
                            res.getString("ac_type"),
                            res.getString("ac_cur"),
                            res.getInt("ac_display")
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
                " values (?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
            psi.setInt(6, x.cbactive);
            psi.setString(7, x.routing);
            psi.setString(8, x.assignedID);
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
    
    public static String[] updateBankMstr(BankMstr x) {
      String[] m = new String[2];
        String sql = "update bk_mstr set bk_site = ?, bk_desc = ?, bk_acct = ?, bk_cur = ?, " +
                " bk_active = ?, bk_route = ?, bk_assignedID = ? where ac_id = ? ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.site);
            ps.setString(2, x.desc);
            ps.setString(3, x.account);
            ps.setString(4, x.currency);
            ps.setInt(5, x.cbactive);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new BankMstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new BankMstr(m, res.getString("bk_id"), 
                            res.getString("bk_site"),    
                            res.getString("bk_desc"),
                            res.getString("bk_acct"),
                            res.getString("bk_route"),
                            res.getString("bk_assignID"),
                            res.getString("bk_cur"),    
                            res.getInt("bk_active")
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        
    public static String[] updateCurrMstr(CurrMstr x) {
        String[] m = new String[2];
        String sql = "update cur_mstr set cur_desc = ? " +
                " where cur_id = ? ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
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
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
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
    
    
    public record AcctMstr(String[] m, String id, String desc, String type, String currency, int cbdisplay) {
        public AcctMstr(String[] m) {
            this(m, "", "", "", "", 0);
        }
    }
    public record BankMstr(String[] m, String id, String site, String desc, String account, String routing, String assignedID, String currency, int cbactive) {
        public BankMstr(String[] m) {
            this(m, "", "", "", "", "", "", "", 0);
        }
    }
    public record CurrMstr(String[] m, String id, String desc) {
        public CurrMstr(String[] m) {
            this(m, "", "");
        }
    }
    
}


