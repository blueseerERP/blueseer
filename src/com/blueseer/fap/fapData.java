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
package com.blueseer.fap;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import static com.blueseer.utl.BlueSeerUtils.setDateFormat;
import static com.blueseer.utl.BlueSeerUtils.setDateFormatNull;
import com.blueseer.utl.OVData;
import com.blueseer.vdr.venData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JTable;

/**
 *
 * @author terryva
 */
public class fapData {
    
    public static String[] apCheckRunTransaction(JTable mytable, int batchid, String basecurr, int checknbr, Date effdate, String ctype) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        java.util.Date now = new java.util.Date();
        
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            // lets loop through the JTable with the vouchers to pay
            for (int i = 0 ; i < mytable.getRowCount(); i++) {
                apd_mstr x = new apd_mstr(null, 
                String.valueOf(batchid),
                mytable.getValueAt(i,0).toString(),
                mytable.getValueAt(i,2).toString(),
                mytable.getValueAt(i,3).toString(),
                String.valueOf(checknbr),  // check nbr 
                bsParseDouble(mytable.getValueAt(i, 6).toString().replace(defaultDecimalSeparator, '.'))
                );
                _addAPDMstr(x, bscon, ps, res);  
            }
            // now retrieves the records just inserted into apd_mstr and group by vendor, site, currency
            ArrayList<String[]> ap = _getUniqueAPRecords(String.valueOf(batchid), basecurr, bscon, ps, res);
            
            for (String[] s : ap) {
                String[] vendinfo = venData.getVendInfo(s[0]);
                ap_mstr x = new ap_mstr(null,
                "", //ap_id
                s[0], // ap_vend, 
                String.valueOf(checknbr), // ap_nbr
                bsParseDouble(s[3]), // ap_amt
                bsParseDouble(s[4]), // ap_base_amt,  String ap_entdate, String ap_duedate,
                setDateFormatNull(effdate), // ap_effdate
                setDateFormatNull(now), // ap_entdate
                setDateFormatNull(now), // ap_duedate        
                "C", // ap_type
                "", //ap_rmks
                "", //ap_ref
                vendinfo[5], //ap_terms
                vendinfo[1], //ap_acct
                vendinfo[2], //ap_cc
                "0", //ap_applied
                "", //ap_status
                vendinfo[4], //ap_bank
                s[2], //ap_curr
                basecurr, //ap_base_curr
                String.valueOf(checknbr), //ap_check
                String.valueOf(batchid), //ap_batch
                s[1], //ap_site
                "", // subtype
                "", // entrytype
                "1", // approved
                "" // approver
                );
                
                
                
                _addAPMstr(x, bscon, ps, res);
                // increment each check nbr per record
                    checknbr++;
            }
            
            // ok....got apd_mstr and ap_mstr set for checkrun...now write transactions to GL
            fglData._glEntryFromCheckRun(batchid, effdate, ctype, bscon);  

            // ok...now lets close out the vouchers we just paid
            APCheckRunUpdateVouchers(batchid, bscon);
            
            // now commit
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
    
    public static String[] VouchAndPayTransaction(int batchid, String ctype, ArrayList<vod_mstr> vod, ap_mstr ap, boolean Void) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        java.util.Date now = new java.util.Date();
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
             _addAPMstr(ap, bscon, ps, res);  
            for (vod_mstr z : vod) {
                _addVODMstr(z, bscon, ps, res);
            }
                // the apd_mstr record holds vouchers to be paid
                // in the case of a single expense transaction, the apd_mstr is equivalent to ap_mstr type=V (vouchered)
                // this apd_mstr will be scanned and a new ap_mstr created with type=E (payment)
                apd_mstr z = new apd_mstr(null, 
                String.valueOf(batchid),
                ap.ap_vend,
                ap.ap_nbr,
                ap.ap_nbr,
                ap.ap_check,  // check nbr ...blank in this case
                ap.ap_amt
                );
                _addAPDMstr(z, bscon, ps, res);  
          
          
                // now for the expense side of the ap_mstr to close the ap voucher side
                ap_mstr x = new ap_mstr(null, 
                "", //ap_id
                ap.ap_vend, // ap_vend, 
                ap.ap_nbr, // ap_nbr
                ap.ap_amt, // ap_amt
                ap.ap_base_amt, 
                ap.ap_effdate,
                ap.ap_entdate,
                ap.ap_duedate, // ap_duedate        
                "E", // ap_type
                ap.ap_rmks, //ap_rmks
                ap.ap_ref, //ap_ref
                ap.ap_terms, //ap_terms
                ap.ap_acct, //ap_acct
                ap.ap_cc, //ap_cc
                "0", //ap_applied
                "c", //ap_status
                ap.ap_bank, //ap_bank
                ap.ap_curr, //ap_curr
                ap.ap_base_curr, //ap_base_curr
                String.valueOf(batchid), //ap_check 
                String.valueOf(batchid), //ap_batch
                ap.ap_site, //ap_site
                ap.ap_subtype,
                ap.ap_entrytype,
                ap.ap_approved,
                ap.ap_approver
                ); 
                _addAPMstr(x, bscon, ps, res); // add AP Type E payment
            
            if (ctype.equals("AP-Expense")) {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate), bscon, Void); // aptype=V
                fglData._glEntryFromCheckRun(batchid, parseDate(ap.ap_effdate), ctype, bscon); //aptype=E
            }
            if (ctype.equals("AP-Cash-Purch")) {
                fglData._glEntryFromCashTranBuy(ap.ap_nbr, parseDate(ap.ap_effdate), ctype, bscon);
            }
            if (ctype.equals("AP-Cash")) {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate),  bscon, Void);
                fglData._glEntryFromCheckRun(batchid, parseDate(ap.ap_effdate), ctype, bscon); //aptype=E
            }
            if (ctype.equals("AP-Vendor")) {
                fglData._glEntryFromCheckRun(batchid, parseDate(ap.ap_effdate), ctype, bscon);
            }
            
            
            // ok...now lets close out the vouchers we just paid
            _APCheckRunUpdateVouchers(batchid, bscon);
            
            // now commit
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
    
    public static String[] _VouchAndPayTransaction(int batchid, String ctype, Connection bscon, ArrayList<vod_mstr> vod, ap_mstr ap, boolean Void) throws SQLException {
        String[] m = new String[2];
       
        PreparedStatement ps = null;
        ResultSet res = null;
        java.util.Date now = new java.util.Date();
       
             _addAPMstr(ap, bscon, ps, res);  
            for (vod_mstr z : vod) {
                _addVODMstr(z, bscon, ps, res);
            }
                // the apd_mstr record holds vouchers to be paid
                // in the case of a single expense transaction, the apd_mstr is equivalent to ap_mstr type=V (vouchered)
                // this apd_mstr will be scanned and a new ap_mstr created with type=E (payment)
                apd_mstr z = new apd_mstr(null, 
                String.valueOf(batchid),
                ap.ap_vend,
                ap.ap_nbr,
                ap.ap_nbr,
                ap.ap_check,  // check nbr ...blank in this case
                ap.ap_amt
                );
                _addAPDMstr(z, bscon, ps, res);  
          
          
                // now for the expense side of the ap_mstr to close the ap voucher side
                ap_mstr x = new ap_mstr(null, 
                "", //ap_id
                ap.ap_vend, // ap_vend, 
                ap.ap_nbr, // ap_nbr
                ap.ap_amt, // ap_amt
                ap.ap_base_amt, 
                ap.ap_effdate,
                ap.ap_entdate,
                ap.ap_duedate, // ap_duedate        
                "E", // ap_type
                ap.ap_rmks, //ap_rmks
                ap.ap_ref, //ap_ref
                ap.ap_terms, //ap_terms
                ap.ap_acct, //ap_acct
                ap.ap_cc, //ap_cc
                "0", //ap_applied
                "c", //ap_status
                ap.ap_bank, //ap_bank
                ap.ap_curr, //ap_curr
                ap.ap_base_curr, //ap_base_curr
                String.valueOf(batchid), //ap_check 
                String.valueOf(batchid), //ap_batch
                ap.ap_site, //ap_site
                ap.ap_subtype,
                ap.ap_entrytype,
                ap.ap_approved,
                ap.ap_approver
                ); 
                _addAPMstr(x, bscon, ps, res); // add AP Type E payment
            
            if (ctype.equals("AP-Expense")) {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate), bscon, Void); // aptype=V
                fglData._glEntryFromCheckRun(batchid, parseDate(ap.ap_effdate), ctype, bscon); //aptype=E
            }
            if (ctype.equals("AP-Cash-Purch")) {
                fglData._glEntryFromCashTranBuy(ap.ap_nbr, parseDate(ap.ap_effdate), ctype, bscon);
            }
            if (ctype.equals("AP-Cash")) {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate),  bscon, Void);
                fglData._glEntryFromCheckRun(batchid, parseDate(ap.ap_effdate), ctype, bscon); //aptype=E
            }
            if (ctype.equals("AP-Vendor")) {
                fglData._glEntryFromCheckRun(batchid, parseDate(ap.ap_effdate), ctype, bscon);
            }
            
            
            // ok...now lets close out the vouchers we just paid
            _APCheckRunUpdateVouchers(batchid, bscon);
            
         
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
       
    return m;
    }
    
    
    public static String[] VoucherTransaction(String ctype, ArrayList<vod_mstr> vod, ap_mstr ap, boolean Void) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        java.util.Date now = new java.util.Date();
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            
            if (! Void) {
                 _addAPMstr(ap, bscon, ps, res);  
                for (vod_mstr z : vod) {
                    _addVODMstr(z, bscon, ps, res);
                }
            } 
            
            if (ctype.equals("Receipt")) {
            fglData._glEntryFromVoucher(ap, bscon, Void); 
            } else {
            fglData._glEntryFromVoucherExpense(ap.ap_nbr(), parseDate(ap.ap_effdate()), bscon, Void);    
            }
            // now commit
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
     
     public static String[] _VoucherTransaction(int batchid, String ctype, Connection bscon, ArrayList<vod_mstr> vod, ap_mstr ap, boolean Void) throws SQLException {
          String[] m = new String[2];
        PreparedStatement ps = null;
        ResultSet res = null;
        java.util.Date now = new java.util.Date();
            if (! Void) {
                 _addAPMstr(ap, bscon, ps, res);  
                for (vod_mstr z : vod) {
                    _addVODMstr(z, bscon, ps, res);
                }
            } 
            if (ctype.equals("Receipt")) {
            fglData._glEntryFromVoucher(ap, bscon, Void); 
            } else {
            fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate), bscon, Void);    
            }
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        return m;
     }
    
    public static String[] APExpense(int batchid, String basecurr, Date effdate, int checknbr, String voucher, String invoice, String vend, Double amount, String ctype) {
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        java.util.Date now = new java.util.Date();
        String[] vendinfo = venData.getVendInfo(vend);
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            // lets loop through the JTable with the vouchers to pay
           
                apd_mstr z = new apd_mstr(null, 
                String.valueOf(batchid),
                vend,
                voucher,
                invoice,
                "",  // check nbr ...blank in this case
                amount
                );
                _addAPDMstr(z, bscon, ps, res);  
          
            // now retrieves the records just inserted into apd_mstr and group by vendor, site, currency
            ArrayList<String[]> ap = _getUniqueAPRecords(String.valueOf(batchid), basecurr, bscon, ps, res);
            for (String[] s : ap) {
                ap_mstr x = new ap_mstr(null,
                "", //ap_id
                s[0], // ap_vend, 
                String.valueOf(checknbr), // ap_nbr
                bsParseDouble(s[3]), // ap_amt
                bsParseDouble(s[4]), // ap_base_amt,  String ap_entdate, String ap_duedate,
                setDateDB(effdate), // ap_effdate
                setDateDB(now), // ap_entdate
                "", // ap_duedate        
                "E", // ap_type
                "", //ap_rmks
                voucher, //ap_ref
                vendinfo[5], //ap_terms
                vendinfo[1], //ap_acct
                vendinfo[2], //ap_cc
                "", //ap_applied
                "", //ap_status
                vendinfo[4], //ap_bank
                s[2], //ap_curr
                basecurr, //ap_base_curr
                "", //ap_check
                String.valueOf(batchid), //ap_batch
                s[1], //ap_site
                "Expense",
                "manual",
                "1",
                ""
                );
                _addAPMstr(x, bscon, ps, res);
                // increment each check nbr per record
                    checknbr++;
            }
            
            if (ctype.equals("AP-Expense")) {
                fglData._glEntryFromVoucherExpense(voucher, effdate, bscon, false);
            }
            if (ctype.equals("AP-Cash-Purch")) {
                fglData._glEntryFromCashTranBuy(voucher, effdate, ctype, bscon);
            }
            if (ctype.equals("AP-Cash")) { // Misc Expense from CashClass
                fglData._glEntryFromVoucherExpense(voucher, effdate, bscon, false);
            }
            if (ctype.equals("AP-Vendor")) {
                fglData._glEntryFromCheckRun(batchid, effdate, ctype, bscon);
            }
            
            
            // ok...now lets close out the vouchers we just paid
            APCheckRunUpdateVouchers(batchid, bscon);
            
            // now commit
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
    
    
    private static int _addAPDMstr(apd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from apd_mstr where apd_batch = ? and apd_nbr = ?";
        String sqlInsert = "insert into apd_mstr (apd_batch, apd_vend, apd_nbr, apd_ref, apd_check, apd_voamt ) "
                        + " values (?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.apd_batch);
          ps.setString(2, x.apd_nbr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.apd_batch);
            ps.setString(2, x.apd_vend);
            ps.setString(3, x.apd_nbr);
            ps.setString(4, x.apd_ref);
            ps.setString(5, x.apd_check);
            ps.setDouble(6, x.apd_voamt);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    public static int _addVODMstr(vod_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from vod_mstr where vod_id = ? and vod_rvdid = ? and vod_rvdline = ?";
        String sqlInsert = "insert into vod_mstr (vod_id, vod_rvdid, vod_rvdline, vod_item, vod_qty, vod_voprice, vod_date, vod_vend," +
        "vod_invoice, vod_expense_acct, vod_expense_cc, vod_po, vod_poline, vod_approved ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.vod_id);
          ps.setString(2, x.vod_rvdid);
          ps.setInt(3, x.vod_rvdline);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.vod_id);
            ps.setString(2, x.vod_rvdid);
            ps.setInt(3, x.vod_rvdline);
            ps.setString(4, x.vod_item);
            ps.setDouble(5, x.vod_qty);
            ps.setDouble(6, x.vod_voprice);
            ps.setString(7, x.vod_date);
            ps.setString(8, x.vod_vend);
            ps.setString(9, x.vod_invoice);
            ps.setString(10, x.vod_expense_acct);
            ps.setString(11, x.vod_expense_cc);
            ps.setString(12, x.vod_po);
            ps.setInt(13, x.vod_poline);
            ps.setString(14, x.vod_approved);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    
    public static int _addAPMstr(ap_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from ap_mstr where ap_batch = ? and ap_nbr = ? and ap_type = ?";
        String sqlInsert = "insert into ap_mstr (ap_vend, ap_nbr, " +
        "ap_amt, ap_base_amt, ap_effdate, ap_entdate, ap_duedate, " +
        "ap_type, ap_rmks, ap_ref, ap_terms, ap_acct, " +
        "ap_cc, ap_applied, ap_status, ap_bank, ap_curr, " +
        "ap_base_curr, ap_check, ap_batch, ap_site, ap_subtype ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x.ap_batch);
          ps.setString(2, x.ap_nbr);
          ps.setString(3, x.ap_type);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.ap_vend);
            ps.setString(2, x.ap_nbr);
            ps.setDouble(3, x.ap_amt);
            ps.setDouble(4, x.ap_base_amt);
            ps.setString(5, x.ap_effdate);
            ps.setString(6, x.ap_entdate);
            ps.setString(7, x.ap_duedate);
            ps.setString(8, x.ap_type);
            ps.setString(9, x.ap_rmks);
            ps.setString(10, x.ap_ref);
            ps.setString(11, x.ap_terms);
            ps.setString(12, x.ap_acct);
            ps.setString(13, x.ap_cc);
            ps.setString(14, x.ap_applied);
            ps.setString(15, x.ap_status);
            ps.setString(16, x.ap_bank);
            ps.setString(17, x.ap_curr);
            ps.setString(18, x.ap_base_curr);
            ps.setString(19, x.ap_check);
            ps.setString(20, x.ap_batch);
            ps.setString(21, x.ap_site);
            ps.setString(22, x.ap_subtype);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    
    private static ArrayList<String[]> _getUniqueAPRecords(String batchid, String basecurr, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<String[]> list = new ArrayList<String[]>(); // vend, site, currency, amount, baseamount
        String sqlSelect = "select ap_site, ap_curr, apd_vend, sum(apd_voamt) as sum from apd_mstr " +
                       " inner join ap_mstr on ap_nbr = apd_nbr " +
                       " where apd_batch = ? " +
                       " group by apd_vend, ap_site, ap_curr order by apd_vend ";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, batchid);
          res = ps.executeQuery();
          double sum = 0.00;
          double sumbase = 0.00;
          while (res.next()) {
            sum = res.getDouble("sum");
            if (basecurr.toUpperCase().equals(res.getString("ap_curr").toUpperCase())) {
            sumbase = res.getDouble("sum");
            } else {
            sumbase = OVData.getExchangeBaseValue(basecurr, res.getString("ap_curr"), res.getDouble("sum"));    
            }  
            String[] s = new String[]{res.getString("apd_vend"),
                res.getString("ap_site"),
                res.getString("ap_curr"),
                String.valueOf(sum), String.valueOf(sumbase)};
            list.add(s);
            }
          
          return list;
    }
     
    // misc
    
    public static void updateAPVoucherStatus(String nbr, String status) {
            try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                           st.executeUpdate(
                                 " update ap_mstr set ap_status = " + "'" + status + "'" +
                                 " where ap_type = 'V' and ap_nbr = " + "'" + nbr + "'" + ";" );
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
    
    public static void approveAPVoucher(String nbr, String status) {
            try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                           st.executeUpdate(
                                 " update ap_mstr set ap_approved = " + "'" + status + "'" +
                                 " where ap_type = 'V' and ap_nbr = " + "'" + nbr + "'" + ";" );
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
    
    
    public static String getVoucherStatus(String nbr) {
       String status = "";
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

            res = st.executeQuery("select ap_status from ap_mstr where ap_type = 'V' and ap_nbr = " + "'" + nbr + "';" );
           while (res.next()) {
            status = res.getString("ap_status");                    
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
    return status;

}  

    
    public static boolean APCheckRun_apd_mstr(JTable mytable, int batchid) {
       boolean myreturn = false;

        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
        for (int i = 0 ; i < mytable.getRowCount(); i++) {
            st.executeUpdate("insert into apd_mstr "
                    + "(apd_batch, apd_vend, apd_nbr, apd_ref, apd_voamt) "
                    + " values ( " + "'" + batchid + "'" + ","
                    + "'" + mytable.getValueAt(i,0).toString() + "'" + ","
                    + "'" + mytable.getValueAt(i,2).toString() + "'" + ","
                    + "'" + mytable.getValueAt(i,3).toString() + "'" + ","
                    + "'" + bsFormatDouble(bsParseDouble(mytable.getValueAt(i, 6).toString())).replace(defaultDecimalSeparator, '.') + "'" 
                    + ")"
                    + ";");
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

       return myreturn;
   }

    public static boolean APCheckRunUpdateVouchers(int batchid, Connection bscon) throws SQLException {
       boolean myreturn = false;
       ArrayList<String[]> mylist = new ArrayList<String[]>();
       
        
            Statement st = bscon.createStatement();
            ResultSet res = null;
          
            double checkamt = 0.00;
            double applied = 0.00;
            double newamt = 0.00;
            double apamt = 0.00;
            String status = "";
            String voucher = "";
            res = st.executeQuery("select ap_nbr, ap_amt, apd_voamt, ap_applied from apd_mstr " +
                       " inner join ap_mstr on ap_nbr = apd_nbr " +
                       " where apd_batch = " + "'" + batchid + "'" +
                        ";");


            while (res.next()) {
                    voucher = res.getString("ap_nbr");
                    apamt = res.getDouble("ap_amt");
                    checkamt = res.getDouble("apd_voamt");
                    applied = res.getDouble("ap_applied");
                    newamt = applied + checkamt;
     
              if (apamt <= newamt) {
                status = "c";
              } else {
                status = "o";
              }

               // now store record in arraylist
            String[] rec = new String[5];
            rec[0] = voucher;
            rec[1] = currformatDoubleUS(newamt);
            rec[2] = status;
            mylist.add(rec);

            }
            res.close();
            // set ap_applied to ap_applied + apd_voamt...and set status




          for (String[] s : mylist) {
                st.executeUpdate("update ap_mstr set ap_applied = " + "'" + s[1] + "'" + ", ap_status = " + "'" + s[2] + "'" + 
                  " where ap_type = 'V' and ap_nbr = " + "'" + s[0] + "'" +
                  ";");  
          }
          st.close();

       return myreturn;
   }

    private static boolean _APCheckRunUpdateVouchers(int batchid, Connection bscon) throws SQLException {
       boolean myreturn = false;
       ArrayList<String[]> mylist = new ArrayList<String[]>();
       String[] rec = new String[5];
        
            Statement st = bscon.createStatement();
            ResultSet res = null;
          
            double checkamt = 0.00;
            double applied = 0.00;
            double newamt = 0.00;
            double apamt = 0.00;
            String status = "";
            String voucher = "";
            res = st.executeQuery("select ap_nbr, ap_amt, apd_voamt, ap_applied from apd_mstr " +
                       " inner join ap_mstr on ap_nbr = apd_nbr " +
                       " where apd_batch = " + "'" + batchid + "'" +
                        ";");


            while (res.next()) {
                    voucher = res.getString("ap_nbr");
                    apamt = res.getDouble("ap_amt");
                    checkamt = res.getDouble("apd_voamt");
                    applied = res.getDouble("ap_applied");
                    newamt = applied + checkamt;

              if (apamt <= newamt) {
                status = "c";
              } else {
                status = "o";
              }

               // now store record in arraylist
            rec[0] = voucher;
            rec[1] = currformatDoubleUS(newamt);
            rec[2] = status;
            mylist.add(rec);

            }
            res.close();
            // set ap_applied to ap_applied + apd_voamt...and set status




          for (String[] s : mylist) {
                st.executeUpdate("update ap_mstr set ap_applied = " + "'" + s[1] + "'" + ", ap_status = " + "'" + s[2] + "'" + 
                  " where ap_type = 'V' and ap_nbr = " + "'" + s[0] + "'" +
                  ";");  
          }
          st.close();

       return myreturn;
   }

    
    public static String[] addUpdateAPCtrl(ap_ctrl x) {
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  ap_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into ap_ctrl (apc_bank, apc_assetacct, apc_autovoucher, apc_apacct, apc_varchar ) "
                        + " values (?,?,?,?,?); "; 
        String sqlUpdate = "update ap_ctrl set apc_bank = ?, apc_assetacct = ?, apc_autovoucher = ?,  apc_apacct = ?, apc_varchar = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.apc_bank);
            psi.setString(2, x.apc_assetacct);
            psi.setString(3, x.apc_autovoucher);
            psi.setString(4, x.apc_apacct);
            psi.setString(5, x.apc_varchar);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.apc_bank);
            psu.setString(2, x.apc_assetacct);
            psu.setString(3, x.apc_autovoucher);
            psu.setString(4, x.apc_apacct);
            psu.setString(5, x.apc_varchar);
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
   
    public static ap_ctrl getAPCtrl(String[] x) {
        ap_ctrl r = null;
        String[] m = new String[2];
        String sql = "select * from ap_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new ap_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new ap_ctrl(m, 
                                res.getString("apc_bank"),
                                res.getString("apc_assetacct"),
                                res.getString("apc_autovoucher"),
                                res.getString("apc_apacct"),
                                res.getString("apc_varchar")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new ap_ctrl(m);
        }
        return r;
    }
    
    
    public record ap_mstr(String[] m, String ap_id, String ap_vend, String ap_nbr, 
        double ap_amt, double ap_base_amt, String ap_effdate, String ap_entdate, String ap_duedate,
        String ap_type, String ap_rmks, String ap_ref, String ap_terms, String ap_acct,
        String ap_cc, String ap_applied, String ap_status, String ap_bank, String ap_curr,
        String ap_base_curr, String ap_check, String ap_batch, String ap_site, String ap_subtype,
        String ap_entrytype, String ap_approved, String ap_approver) {
        public ap_mstr(String[]m) {
            this(m, "", "", "", 0, 0, "", "", "", "", "", 
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "");
        }
    }
    
    public record apd_mstr(String[] m, String apd_batch, String apd_vend, String apd_nbr, 
        String apd_ref, String apd_check, double apd_voamt) {
        public apd_mstr(String[]m) {
            this(m, "", "", "", "", "", 0);
        }
    }
    
    public record vod_mstr(String[] m, String vod_id, String vod_rvdid, int vod_rvdline, 
        String vod_item, double vod_qty, double vod_voprice, String vod_date, String vod_vend,
        String vod_invoice, String vod_expense_acct, String vod_expense_cc, String vod_po, int vod_poline,
        String vod_approved) {
        public vod_mstr(String[]m) {
            this(m, "", "", 0, "", 0, 0, "", "", "", "",
                    "", "", 0, "" );
        }
    }
    
    public record ap_ctrl (String[] m, String apc_bank, String apc_assetacct, 
        String apc_autovoucher, String apc_apacct, String apc_varchar) {
        public ap_ctrl(String[] m) {
            this(m,"", "", "", "", "");
        }
    } 
    
}
