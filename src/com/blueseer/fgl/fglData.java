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
        String sql = "insert into ac_mstr (ac_id, ac_desc, ac_type, ac_cur, ac_display)  " +
                " values (?,?,?,?,?); ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.id);
        ps.setString(2, x.desc);
        ps.setString(3, x.type);
        ps.setString(4, x.currency);
        ps.setInt(5, x.cbdisplay);
        
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordSQLError}; 
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
               m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError}; 
        }
        return m;
    }
    
    public static String[] deleteAcctMstr(AcctMstr x) { 
         String[] m = new String[2];
        try {

            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                   int i = st.executeUpdate("delete from ac_mstr where ac_id = " + "'" + x.id + "'" + ";");
                    if (i > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
            } finally {
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordConnError};
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
                int i = 0;
                while(res.next()) {
                    i++;
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new AcctMstr(m, res.getString("ac_id"), 
                        res.getString("ac_desc"),
                        res.getString("ac_type"),
                        res.getString("ac_cur"),
                        res.getInt("ac_display")
                    );
                }
                if (i == 0) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new AcctMstr(m);
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
     
     try {

            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                boolean proceed = true;
                int i = 0;

                    res = st.executeQuery("SELECT bk_id FROM  bk_mstr where bk_id = " + "'" + x.id + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into bk_mstr "
                            + "(bk_id, bk_site, bk_desc, bk_acct, bk_cur, bk_active, bk_route, bk_assignedID ) "
                            + " values ( " + "'" + x.id() + "'" + ","
                            + "'" + x.site() + "'" + ","
                            + "'" + x.desc() + "'" + ","
                            + "'" + x.account() + "'" + ","
                            + "'" + x.currency() + "'" + ","
                            + "'" + x.cbactive() + "'" + ","
                            + "'" + x.routing() + "'" + ","
                            + "'" + x.assignedID() + "'"      
                            + ")"
                            + ";");
                        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                    } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists}; 
                    }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                 m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
             m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordConnError};
        }
     
     return m;
    }
    
    public static String[] updateBankMstr(BankMstr x) {
       String[] m = new String[2];
     
     try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                    st.executeUpdate("update bk_mstr set bk_desc = " + "'" + x.desc() + "'" + ","
                            + "bk_acct = " + "'" + x.account() + "'" + ","
                            + "bk_route = " + "'" + x.routing() + "'" + ","
                            + "bk_assignedID = " + "'" + x.assignedID() + "'" + ","        
                            + "bk_cur = " + "'" + x.currency() + "'" + ","
                            + "bk_site = " + "'" + x.site() + "'" + ","        
                            + "bk_active = " + "'" + x.cbactive() + "'"
                            + " where bk_id = " + "'" + x.id() + "'"                             
                            + ";");
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
               
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
        }
     
     return m;  
    }
    
    public static String[] deleteBankMstr(BankMstr x) {
     String[] m = new String[2];
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                   int i = st.executeUpdate("delete from bk_mstr where bk_id = " + "'" + x.id() + "'" + ";");
                    if (i > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    } else {
                    m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};    
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordConnError};
        }
       
     return m;   
    }
    
    public static BankMstr getBankMstr(String[] x) {
        BankMstr r = null;
        String[] m = new String[2];
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            Statement st = bsmf.MainFrame.con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                res = st.executeQuery("select * from bk_mstr where bk_id = " + "'" + x[0] + "'" + " limit 1;");
                while (res.next()) {
                    i++;
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new BankMstr(m, res.getString("bk_id"),
                    res.getString("bk_site"),
                    res.getString("bk_desc"),
                    res.getString("bk_acct"),
                    res.getString("bk_route"),
                    res.getString("bk_assignedID"),
                    res.getString("bk_cur"),
                    res.getInt("bk_active")
                    );
                }
                if (i == 0) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new BankMstr(m);
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError}; 
                r = new BankMstr(m);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError}; 
            r = new BankMstr(m);
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
    
}


