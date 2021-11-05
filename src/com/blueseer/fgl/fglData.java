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
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
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
            psi.setString(5, x.cbdisplay);
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
            psi.setString(6, x.cbactive);
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
    
    // misc functions
     
    public static String setGLRecNbr(String type) {
           String mystring = "";
           int nextnbr = OVData.getNextNbr("gl");
           java.util.Date now = new java.util.Date();
           DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
           // format should be two char type code + 8 char date code + 6 char unique number ...16 chars in all
           mystring = type + dfdate.format(now) + String.format("%06d", nextnbr);       
           return mystring;
       }
    
    public static void glEntry(String acct_cr, String cc_cr, String acct_dr, String cc_dr, String date, Double amt, Double baseamt, String curr, String basecurr, String ref, String site, String type, String desc) {
          
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
          
          //bsmf.MainFrame.show(String.valueOf(amt) + "/" + df.format(amt));
          
       if ( amt != 0 ) {   
       try {
             
            Connection con = DriverManager.getConnection(url + db, user, pass);
        try {
                Statement st = con.createStatement();
       
        st.executeUpdate("insert into gl_tran "
                        + "( glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc )"
                        + " values ( " + "'" + acct_cr + "'" + ","
                        + "'" + cc_cr + "'" + ","
                        + "'" + date + "'" + ","
                        + "'" + currformatDoubleUS(-1 * amt) + "'" + ","
                        + "'" + currformatDoubleUS(-1 * baseamt) + "'" + ","        
                        + "'" + curr + "'" + ","
                        + "'" + basecurr + "'" + ","
                        + "'" + ref + "'" + ","        
                        + "'" + site + "'" + ","
                        + "'" + type + "'"+ ","
                        + "'" + desc + "'"
                        + " )"
                        + ";" );
             
        //      bsmf.MainFrame.show(acct_dr.toString() + "/" + cc_dr + "/" + date + "/" + amt.toString());
              st.executeUpdate( "insert into gl_tran "
                        + "(glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc )"
                        + " values ( " + "'" + acct_dr + "'" + ","
                        + "'" + cc_dr + "'" + ","
                        + "'" + date + "'" + ","
                        + "'" + currformatDoubleUS(amt) + "'" + ","
                        + "'" + currformatDoubleUS(baseamt) + "'" + ","  
                        + "'" + curr + "'" + ","
                        + "'" + basecurr + "'" + ","        
                         + "'" + ref + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + type + "'"+ ","
                        + "'" + desc + "'"
                        + ")"
                        + ";"
                        );
         
        } catch (SQLException s) {
            MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot Write GL");
            }
              
        con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
               }  
       } // if amount does not equal 0
          
      }
    
    public static void glEntryXP(Connection bscon, String acct_cr, String cc_cr, String acct_dr, String cc_dr, String date, Double amt, Double baseamt, String curr, String basecurr, String ref, String site, String type, String desc) throws SQLException {
          
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
          
       if ( amt != 0 ) {  
        Statement st = bscon.createStatement();
        st.executeUpdate("insert into gl_tran "
                        + "( glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc )"
                        + " values ( " + "'" + acct_cr + "'" + ","
                        + "'" + cc_cr + "'" + ","
                        + "'" + date + "'" + ","
                        + "'" + currformatDoubleUS(-1 * amt) + "'" + ","
                        + "'" + currformatDoubleUS(-1 * baseamt) + "'" + ","        
                        + "'" + curr + "'" + ","
                        + "'" + basecurr + "'" + ","
                        + "'" + ref + "'" + ","        
                        + "'" + site + "'" + ","
                        + "'" + type + "'"+ ","
                        + "'" + desc + "'"
                        + " )"
                        + ";" );
             
        //      bsmf.MainFrame.show(acct_dr.toString() + "/" + cc_dr + "/" + date + "/" + amt.toString());
              st.executeUpdate( "insert into gl_tran "
                        + "(glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc )"
                        + " values ( " + "'" + acct_dr + "'" + ","
                        + "'" + cc_dr + "'" + ","
                        + "'" + date + "'" + ","
                        + "'" + currformatDoubleUS(amt) + "'" + ","
                        + "'" + currformatDoubleUS(baseamt) + "'" + ","  
                        + "'" + curr + "'" + ","
                        + "'" + basecurr + "'" + ","        
                         + "'" + ref + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + type + "'"+ ","
                        + "'" + desc + "'"
                        + ")"
                        + ";"
                        );
       st.close();       
       } // if amount does not equal 0
      }
          
    public static boolean glEntryFromVoucher(String voucher, Date effdate) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                Statement st2 = con.createStatement();
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
                    ArrayList cost =  new ArrayList();   
                    ArrayList basecost =  new ArrayList();
                    ArrayList curr =  new ArrayList();
                    ArrayList basecurr =  new ArrayList();
                   
                    String thistype = "RCT-VOUCH";
                    String thisdesc = "RCT VOUCHER";   
                
                    
                   
                       res = st.executeQuery("select ap_amt, ap_base_amt, ap_curr, ap_base_curr, ap_ref, ap_site, ap_acct, ap_cc, ap_vend, poc_rcpt_cc, poc_rcpt_acct from ap_mstr " +
                               " inner join po_ctrl where ap_type = 'V' and ap_nbr = " + "'" + voucher + "'" +";");
                   
                    while (res.next()) {
                     // credit vendor AP Acct (AP Voucher) and debit unvouchered receipts (po_rcpts acct)
                        acct_cr.add(res.getString("ap_acct"));
                    acct_dr.add(res.getString("poc_rcpt_acct"));
                    cc_cr.add(res.getString("ap_cc"));
                    cc_dr.add(res.getString("poc_rcpt_cc"));
                    cost.add(res.getDouble("ap_amt"));
                    basecost.add(res.getDouble("ap_base_amt"));
                    curr.add(res.getDouble("ap_curr"));
                    basecurr.add(res.getDouble("ap_base_curr"));
                    site.add(res.getString("ap_site"));
                    ref.add(res.getString("ap_ref"));
                    type.add(thistype);
                    desc.add(res.getString("ap_ref"));     
          
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                      for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
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
        
    public static boolean glEntryFromVoucherExpense(String voucher, Date effdate) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
                   
                   
                       res = st.executeQuery("select ap_amt, ap_base_amt, ap_curr, ap_base_curr, ap_ref, ap_check, ap_nbr, vod_part, ap_site, ap_acct, ap_cc, ap_vend, vod_qty, vod_voprice, vod_expense_acct, vod_expense_cc from vod_mstr " +
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
                    ref.add(res.getString("ap_check"));
                    type.add(thistype);
                    if (res.getString("ap_ref").isEmpty()) {
                       desc.add(res.getString("vod_part")); 
                    } else {
                       desc.add(res.getString("ap_ref") + "/" + res.getString("vod_part"));
                    }
                             
               
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    
                
                    }
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
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
        
    public static boolean glEntryFromPayRoll(String batch, Date effdate) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
                   
                    String thistype = "PayRoll";
                    String laboracct = OVData.getDefaultPayLaborAcct();
                    String withhold = OVData.getDefaultPayWithHoldAcct();
                    String salariedacct = OVData.getDefaultPaySalariedAcct();
                    String defaulttaxacct = OVData.getDefaultPayTaxAcct();
                    String taxacct = "";
                    String cc = OVData.getDefaultCC();
                    String defaultcurr = OVData.getDefaultCurrency();
                    String bank = OVData.getDefaultAPBank();
                    String bankacct = OVData.getDefaultBankAcct(bank);
                    
                    
                    // LETS DO LABOR FIRST....THIS WILL DEBIT LABOR EXPENSE AND CREDIT CASH WITH THE NET CHECK PAYMENT
                    
                       res = st.executeQuery("select py_id, py_site, pyd_checknbr, pyd_payamt, pyd_empdept from pay_det inner join pay_mstr on py_id = pyd_id  " +
                               " where pyd_id = " + "'" + batch + "'" +";");
                   
                    Double amt = 0.00;   
                    while (res.next()) {
                     // credit Cash account and debit labor expense
                    amt = res.getDouble("pyd_payamt");
                       acct_cr.add(bankacct);
                    acct_dr.add(laboracct);
                    cc_cr.add(res.getString("pyd_empdept"));
                    cc_dr.add(res.getString("pyd_empdept"));
                      cost.add(amt);
                      basecost.add(amt);
                    curr.add(defaultcurr);
                    basecurr.add(defaultcurr);
                    site.add(res.getString("py_site"));
                    ref.add(res.getString("py_id"));
                    type.add(thistype);
                    desc.add("CheckNbr:" + res.getString("pyd_checknbr"));  
                    }
                    
                    
                    
                    // NOW LETS DO WITHHOLDINGS...
                    // NOTE!!! THis needs to be broken into individual withholding accounts...currently lumped into one withholding account...with 'descriptions'
                      res = st.executeQuery("select py_id, py_site, pyd_checknbr, pyl_amt, pyl_profile, pyl_profile_line, pyl_type, pyl_code, pyl_desc, pyl_empnbr, pyd_empdept from pay_line " +
                              " inner join pay_det on pyd_id = pyl_id " +
                              " inner join pay_mstr on py_id = pyd_id  " +
                               " where pyl_type = 'deduction' and pyd_id = " + "'" + batch + "'" +";");
                   
                    amt = 0.00;   
                    while (res.next()) {
                     // credit withholding account and debit payroll tax expense
                     // lets determine tax account based on profile line
                     
                     
                    taxacct = OVData.getPayProfileDetAcct(res.getString("pyl_profile"), res.getString("pyl_profile_line"));
                    if (taxacct.isEmpty()) {
                       taxacct = defaulttaxacct; 
                    }  
                     
                    amt = res.getDouble("pyl_amt");
                    acct_cr.add(withhold);
                    acct_dr.add(taxacct);
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
                    }
                    
                    
                    
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
                   
                   
                       res = st.executeQuery("select pl_line, pl_po_rcpt, pl_inventory, ap_amt, ap_base_amt, ap_curr, ap_base_curr, ap_ref, ap_nbr, vod_part, ap_site, ap_acct, ap_cc, ap_vend, " +
                               " vod_qty, vod_voprice, vod_expense_acct, vod_expense_cc from vod_mstr " +
                               " inner join item_mstr on it_item = vod_part " +
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
                    desc.add("cashtranvouch:" + res.getString("ap_ref") + "/" + res.getString("vod_part"));         
               
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
                    desc.add("cashtranpurch:" + res.getString("ap_ref") + "/" + res.getString("vod_part"));      
                    
                    }
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
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
                
    public static boolean glEntryFromARMemo(String batchnbr, Date effdate) {
                boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
                   
                    String thistype = "AR-PAYMENT";
                    String thisdesc = "AR Payment";
                   
                       res = st.executeQuery("select ard_acct, ard_cc, ard_id, ard_amt, ard_base_amt, ard_curr, ard_base_curr, ar_ref, ard_ref, ar_site, ar_acct, ar_cc, ar_from ard_mstr " +
                               " inner join ar_mstr on ar_nbr = ard_id  where ard_id = " + "'" + batchnbr + "'" +";");
                   
                    while (res.next()) {
                     // credit vendor AP Acct (AP Voucher) and debit unvouchered receipts (po_rcpts acct)
                      acct_cr.add(res.getString("ard_acct"));
                    acct_dr.add(res.getString("ar_acct"));
                    cc_cr.add(res.getString("ard_cc"));
                    cc_dr.add(res.getString("ar_cc"));
                    cost.add(res.getDouble("ard_amt"));
                    basecost.add(res.getDouble("ard_base_amt"));
                    curr.add(res.getString("ard_curr"));
                    basecurr.add(res.getString("ard_base_curr"));
                    site.add(res.getString("ar_site"));
                    ref.add(res.getString("ard_id"));
                    type.add(thistype);
                    desc.add("Memo " + res.getString("ard_ref"));
                    
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                    
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
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
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
                    
                    
                    String thistype = "AR-Payment";
                    String thisdesc = "";
                
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
                        if (taxamt > 0 && basetaxamt > 0) {
                            if (! artaxcode.isEmpty()) {
                                 ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(artaxcode);
                              for (String[] elements : taxelements) {
                                    // tax entries
                                    acct_cr.add(OVData.getDefaultTaxAcctByType(elements[2]));
                                    acct_dr.add(res.getString("bk_acct"));
                                    cc_cr.add(OVData.getDefaultTaxCCByType(elements[2]));
                                    cc_dr.add(res.getString("cm_ar_cc"));
                                    cost.add(( net * ( bsParseDoubleUS(elements[1]) / 100 )));  // credit AR for sales less tax
                                    basecost.add(( netbase * ( bsParseDoubleUS(elements[1]) / 100 )));  // credit AR for sales less tax
                                    curr.add(res.getString("ard_curr"));
                                    basecurr.add(res.getString("ard_base_curr"));
                                    site.add(res.getString("ar_site"));
                                    ref.add(res.getString("ard_ref"));
                                    type.add(thistype);
                                    desc.add(thisdesc);

                              }
                            }
                        }
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
                    }
                    
                    
                    
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                    res.close();
                    
                    
                    
                    
                    
                    
                     // process the arrays into glEntry
                    for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
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
                                 
                   ArrayList v_acct_cr = new ArrayList();
                    ArrayList v_ref =  new ArrayList();
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
                    }
                    res.close();
                    // process the arrays into glEntry
                    for (int j = 0; j < v_acct_cr.size(); j++) {
                      glEntryXP(bscon, v_acct_cr.get(j).toString(), v_cc_cr.get(j).toString(), v_acct_dr.get(j).toString(), v_cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(v_cost.get(j).toString()), bsParseDoubleUS(v_cost.get(j).toString()), curr, basecurr, v_ref.get(j).toString(), v_site.get(j).toString(), v_type.get(j).toString(), v_desc.get(j).toString());  
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
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList cost =  new ArrayList();   
                   
                    String thistype = "POS";
                    String thisdesc = "POS REVERSE";   
                   
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
                       
                    }
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntryXP(bscon, acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(cost.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
                    }
                    
                    st.close();
                    res.close();
        }
         
    public static boolean glEntryFromReceiver(String receiver, Date effdate) {
              boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet res = null;
            ResultSet nres = null;
            try{
                
               
                
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
                    ArrayList cost =  new ArrayList();   
                    ArrayList basecost =  new ArrayList();
                    ArrayList currarray =  new ArrayList();
                    ArrayList basecurrarray =  new ArrayList();
                   
                   
                    String thistype = "RCT-PURCH";
                    String thisdesc = "";  
                    String thissite = "";
                    String thisref = "";
                
                
                    String unvouchacct = "";
                    String unvouchcc = "";
                    
                    String part = "";
                    double qty = 0;
                    String loc = "";
                    
                    String curr = "";
                    String basecurr = OVData.getDefaultCurrency();
                   
                    double thiscost = 0;
                    double costtot = 0;
                    double variance = 0;
                    double variancetot = 0;
                    double price = 0;
                   
                    res = st.executeQuery("select poc_rcpt_acct, poc_rcpt_cc from po_ctrl;");
                    while (res.next()) {
                        unvouchacct = res.getString("poc_rcpt_acct");
                        unvouchcc = res.getString("poc_rcpt_cc"); // not used at this time
                    }
                    
                      res = st.executeQuery("select rvd_part, rvd_qty, rvd_loc, rvd_site, rvd_id, rvd_netprice, rvd_po, po_curr from recv_det inner join po_mstr on rvd_po = po_nbr where rvd_id = " + "'" + receiver + "'" +";");
                    while (res.next()) {
                        part = res.getString("rvd_part");
                        qty = res.getDouble("rvd_qty");
                        loc = res.getString("rvd_loc");
                        thissite = res.getString("rvd_site");
                        thisref = res.getString("rvd_id");
                        price = res.getDouble("rvd_netprice");
                        thisdesc = "Receipts";
                        curr = res.getString("po_curr");
                        
                        nres = st2.executeQuery("select  itc_total, pl_po_rcpt, pl_po_ovh, pl_line, pl_inventory, pl_po_pricevar, " +
                       " pl_cogs_mtl, pl_cogs_lbr, pl_cogs_bdn, pl_cogs_ovh, pl_cogs_out, pl_sales, itc_total, " +
                       " itc_mtl_top, itc_mtl_low, itc_lbr_top, itc_lbr_low, itc_bdn_top, itc_bdn_low, " +
                       " itc_ovh_top, itc_ovh_low, itc_out_top, itc_out_low, itc_bdn_top, itc_bdn_low " +
                       " from item_mstr  " + 
                       " inner join pl_mstr on pl_line = it_prodline " +
                       " inner join item_cost on itc_item = it_item and itc_set = 'standard' where it_item = " + "'" + part.toString() + "'" + ";"
                        );
                    while (nres.next()) {
                     
                    thiscost = nres.getDouble("itc_mtl_top") + nres.getDouble("itc_mtl_low");
                    costtot = thiscost * qty;
                    variance = thiscost - price;
                      if (! curr.toUpperCase().equals(basecurr.toUpperCase())) {
                          variance = thiscost - (OVData.getExchangeBaseValue(basecurr, curr, price));
                      }
                    variancetot = variance * qty;
                     
                        // material cost
                    acct_cr.add(unvouchacct);
                    acct_dr.add(nres.getString("pl_inventory"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(costtot);
                    basecost.add(costtot);
                    site.add(thissite);
                    currarray.add(curr);
                    basecurrarray.add(basecurr);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);     
          
                   
          
                    // ppv 
                    acct_cr.add(nres.getString("pl_po_pricevar"));
                    acct_dr.add(unvouchacct);
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(variancetot);
                    basecost.add(variancetot);
                    site.add(thissite);
                    currarray.add(curr);
                    basecurrarray.add(basecurr);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);   
          
                    // overhead cost
                    acct_cr.add(unvouchacct);
                    acct_dr.add(nres.getString("pl_po_ovh"));
                    cc_cr.add(nres.getString("pl_line"));
                    cc_dr.add(nres.getString("pl_line"));
                    cost.add(((nres.getDouble("itc_ovh_top") + nres.getDouble("itc_ovh_low")) * qty));
                    basecost.add(((nres.getDouble("itc_ovh_top") + nres.getDouble("itc_ovh_low")) * qty));
                    site.add(thissite);
                    currarray.add(curr);
                    basecurrarray.add(basecurr);
                    ref.add(thisref);
                    type.add(thistype);
                    desc.add(thisdesc);   
          
               
          
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                    nres.close();
                  }
                    for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), currarray.get(j).toString(), basecurrarray.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
                    }
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            } finally {
                if (res != null) {
                    res.close();
                }
                if (nres != null) {
                    nres.close();
                }
                if (st != null) {
                    st.close();
                }
                if (st2 != null) {
                    st2.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
         }
       
    public static boolean glEntryFromShipper(String shipper, Date effdate) {
              boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet res = null;
            ResultSet nres = null;
            try{
                
               
                
               java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String mydate = dfdate.format(now);
                
                double totamt = 0.00;
                double basetotamt = 0.00;
                double charges = 0.00;
                double tottax = 0.00;
                
                
                 // added SQLITE adjustment here...create arraylist of entries for glentry instead of inline
                    ArrayList acct_cr = new ArrayList();
                    ArrayList ref =  new ArrayList();
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList cost =  new ArrayList();
                    ArrayList basecost =  new ArrayList();
                    String thissite = "";
                    String thisref = "";
                    String thistype = "ISS-SALES";
                    String thisdesc = "Sales Order Shipment";
                    
                    
                    
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
                    double basenetprice = 0.00;
                    
                    String taxcode = "";
                    String curr = "";
                    String basecurr = OVData.getDefaultCurrency();
                    
                    
                    int i = 0;
                   
                       res = st.executeQuery("select sh_site, sh_ar_acct, sh_taxcode, sh_curr, sh_ar_cc, sh_cust, sh_type from ship_mstr where sh_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                        aracct = res.getString("sh_ar_acct");
                        arcc = res.getString("sh_ar_cc");
                        cust = res.getString("sh_cust");
                        thissite = res.getString("sh_site");
                        taxcode = res.getString("sh_taxcode");
                        shiptype = res.getString("sh_type");
                        curr = res.getString("sh_curr");
                    }
                    
                      res = st.executeQuery("select * from ship_det where shd_id = " + "'" + shipper + "'" +";");
                    while (res.next()) {
                        part = res.getString("shd_part");
                        qty = res.getDouble("shd_qty");
                        uom = res.getString("shd_uom");
                        loc = res.getString("shd_loc");
                        thisref = res.getString("shd_id");
                        baseqty = OVData.getUOMBaseQty(part, thissite, uom, qty);
                        
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
                       " inner join item_cost on itc_item = it_item and itc_set = 'standard' " +
                       " where it_item = " + "'" + part.toString() + "'" +  ";"
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
          
                    // need to do discounts ..credit sales, debit disc, debit AR (-$4.00, $.02, $3.98)
                    }
                       
                      if (i == 0) {
                          // must be misc...just do sales / AR GL transaction
                        if (shiptype.equals("A")) {  // if from asset transaction
                        acct_cr.add(OVData.getDefaultAssetAcctAR()); 
                        cc_cr.add(OVData.getDefaultAssetCC());
                        } else {
                        acct_cr.add(OVData.getDefaultSalesAcct());  
                        cc_cr.add(OVData.getDefaultSalesCC());
                        }
                        acct_dr.add(cusData.getCustSalesAcct(cust));
                        
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
                      }  
                        
                        
                        
                    } // for each line on shipper
                    
                    
                    
                    // Tax entry if tottax > 0 necessary
                    // we will credit sales (income) acct and debit (liability) appropriate tax account for each tax element in cm_tax_code
                    tottax = OVData.getTaxAmtApplicableByCust(cust, totamt);
                    if (tottax > 0) {
                      ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(taxcode);
                          for (String[] elements : taxelements) {
                          glEntry(OVData.getDefaultSalesAcct(), OVData.getDefaultSalesCC(), OVData.getDefaultTaxAcctByType(elements[2]), OVData.getDefaultTaxCCByType(elements[2]), dfdate.format(effdate), ( totamt * ( bsParseDoubleUS(elements[1]) / 100 )), ( basetotamt * ( bsParseDoubleUS(elements[1]) / 100 )), curr, basecurr, thisref, thissite, thistype, "Tax: " + elements[2]);
                          }
                    }
                    
                   // Trailer / Summary Charges
                    // we will credit sales and debit AR
                    charges = OVData.getShipperTrailerCharges(shipper);
                    if (charges > 0) {
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
                    }
                    
                    
                    
                    
                    
                   for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
                    }
                    
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myerror = true;
            } finally {
                if (res != null) {
                    res.close();
                }
                if (nres != null) {
                    nres.close();
                }
                if (st != null) {
                    st.close();
                }
                if (st2 != null) {
                    st2.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            myerror = true;
        }
        return myerror;
        
         }
               
    public static boolean glEntryFromShipperRV(String shipper, Date effdate) {
              boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
                    ArrayList desc =   new ArrayList();
                    ArrayList type =   new ArrayList();
                    ArrayList cc_cr =   new ArrayList();
                    ArrayList acct_dr =   new ArrayList();
                    ArrayList cc_dr =   new ArrayList();
                    ArrayList site =   new ArrayList();
                    ArrayList cost =  new ArrayList();
                    ArrayList basecost =  new ArrayList();
                    String thissite = "";
                    String thisref = "";
                    String thistype = "ISS-SALES";
                    String thisdesc = "Sales Order RV";
                
                
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
                        part = res.getString("shd_part");
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
                      }  
                                               
                    }
                    
                    
                      // Tax entry if tottax > 0 necessary
                    // we will credit sales (income) acct and debit (liability) appropriate tax account for each tax element in cm_tax_code
                    tottax = OVData.getTaxAmtApplicableByCust(cust, totamt);
                    if (tottax > 0) {
                      ArrayList<String[]> taxelements = OVData.getTaxPercentElementsApplicableByTaxCode(taxcode);
                          for (String[] elements : taxelements) {
                          glEntry(OVData.getDefaultSalesAcct(), OVData.getDefaultSalesCC(), OVData.getDefaultTaxAcctByType(elements[2]), OVData.getDefaultTaxCCByType(elements[2]), dfdate.format(effdate), ( totamt * ( bsParseDoubleUS(elements[1]) / 100 )), ( basetotamt * ( bsParseDoubleUS(elements[1]) / 100 )), curr, basecurr, thisref, thissite, thistype, "Tax: " + elements[2]);
                          }
                    }
                    
                   // Trailer / Summary Charges
                    // we will credit sales and debit AR
                    charges = OVData.getShipperTrailerCharges(shipper);
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
                    }
                    
                    
                      for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr, basecurr, ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
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
                
    public static boolean glEntryFromCheckRun(int batchid, Date effdate, String ctype) {
              boolean myerror = false;  // Set myerror to true for any captured problem...otherwise return false
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
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
                        
                    }
                    
                     for (int j = 0; j < acct_cr.size(); j++) {
                      glEntry(acct_cr.get(j).toString(), cc_cr.get(j).toString(), acct_dr.get(j).toString(), cc_dr.get(j).toString(), dfdate.format(effdate), bsParseDoubleUS(cost.get(j).toString()), bsParseDoubleUS(basecost.get(j).toString()), curr.get(j).toString(), basecurr.get(j).toString(), ref.get(j).toString(), site.get(j).toString(), type.get(j).toString(), desc.get(j).toString());  
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
    
    public record CurrMstr(String[] m, String id, String desc) {
        public CurrMstr(String[] m) {
            this(m, "", "");
        }
    }
    
}


