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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import com.blueseer.ord.ordData;
import static com.blueseer.ord.ordData.addPOSTransaction;
import com.blueseer.ord.ordData.pos_det;
import com.blueseer.rcv.rcvData;
import static com.blueseer.rcv.rcvData._updateReceiverLinesByVoucher;
import static com.blueseer.rcv.rcvData.addReceiverTransaction;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ConvertIntToYesNo;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToDouble;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import static com.blueseer.utl.BlueSeerUtils.setDateFormat;
import static com.blueseer.utl.BlueSeerUtils.setDateFormatNull;
import com.blueseer.utl.OVData;
import com.blueseer.vdr.venData;
import static com.blueseer.vdr.venData.getVendInfo;
import static com.blueseer.vdr.venData.getVendMstr;
import com.blueseer.vdr.venData.vd_mstr;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JTable;
import org.json.JSONArray;

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
                "", // approver
                0,
                0
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
    
    public static String[] VouchAndPayTransaction(String ctype, ArrayList<vod_mstr> vod, ap_mstr ap, boolean Void) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","VouchAndPayTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(ctype);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(vod);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(ap);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(Void);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        java.util.Date now = new java.util.Date();
        int batchid = 0;
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
             batchid = OVData.getNextNbr("batch", bscon);
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
                ap.ap_approver,
                0,
                0
                ); 
                _addAPMstr(x, bscon, ps, res); // add AP Type E payment
            
            if (ctype.equals("AP-Expense")) {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate), bscon, Void, ctype); // aptype=V
                fglData._glEntryFromCheckRun(batchid, parseDate(ap.ap_effdate), ctype, bscon); //aptype=E
            }
            if (ctype.equals("AP-Cash-Purch")) {
                fglData._glEntryFromCashTranBuy(ap.ap_nbr, parseDate(ap.ap_effdate), ctype, bscon);
            }
            if (ctype.equals("AP-Cash")) {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate),  bscon, Void, ctype);
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
                ap.ap_approver,
                0,
                0
                ); 
                _addAPMstr(x, bscon, ps, res); // add AP Type E payment
            
            if (ctype.equals("AP-Expense")) {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate), bscon, Void, ctype); // aptype=V
                fglData._glEntryFromCheckRun(batchid, parseDate(ap.ap_effdate), ctype, bscon); //aptype=E
            }
            if (ctype.equals("AP-Cash-Purch")) {
                fglData._glEntryFromCashTranBuy(ap.ap_nbr, parseDate(ap.ap_effdate), ctype, bscon);
            }
            if (ctype.equals("AP-Cash")) {
                fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate),  bscon, Void, ctype);
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
    
    public static VoucherAP getAPVoucherSet(String[] x ) {
        VoucherAP r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getAPVoucherSet"});
            list.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServFAP");
                r = objectMapper.readValue(returnstring, VoucherAP.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            
            ap_mstr ap = _getAPMstr(x, bscon, ps, res);
            ArrayList<vod_mstr> vod = _getVodMstr(x, bscon, ps, res);
            
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
            r = new VoucherAP(m, ap, vod);
            
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
             r = new VoucherAP(m);
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
    
    
    public static String[] VoucherTransaction(String ctype, ArrayList<vod_mstr> vod, ap_mstr ap, boolean Void) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","VoucherTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(ctype);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(vod);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(ap);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(Void);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
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
            
            // update receiver lines
            for (vod_mstr z : vod) {
            _updateReceiverLinesByVoucher(z, ctype, Void, bscon);
            }
            
            // gl entries
            if (ctype.equals("Receipt")) {
            fglData._glEntryFromVoucher(ap, bscon, Void); 
            } else {
            fglData._glEntryFromVoucherExpense(ap.ap_nbr(), parseDate(ap.ap_effdate()), bscon, Void, "RCT-VOUCH");    
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
            fglData._glEntryFromVoucherExpense(ap.ap_nbr, parseDate(ap.ap_effdate), bscon, Void, "RCT-VOUCH");    
            }
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        return m;
     }
    
    public static String[] APExpense(int batchid, String basecurr, Date effdate, int checknbr, String voucher, String invoice, String vend, double amount, String ctype) {
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
                "",
                0,
                0
                );
                _addAPMstr(x, bscon, ps, res);
                // increment each check nbr per record
                    checknbr++;
            }
            
            if (ctype.equals("AP-Expense")) {
                fglData._glEntryFromVoucherExpense(voucher, effdate, bscon, false, ctype);
            }
            if (ctype.equals("AP-Cash-Purch")) {
                fglData._glEntryFromCashTranBuy(voucher, effdate, ctype, bscon);
            }
            if (ctype.equals("AP-Cash")) { // Misc Expense from CashClass
                fglData._glEntryFromVoucherExpense(voucher, effdate, bscon, false, ctype);
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
        "ap_base_curr, ap_check, ap_batch, ap_site, ap_subtype, ap_entrytype, ap_approved, ap_approver ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
       
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
            ps.setString(23, x.ap_entrytype);
            ps.setString(24, x.ap_approved);
            ps.setString(25, x.ap_approver);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
    
    private static ap_mstr _getAPMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ap_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from ap_mstr where ap_nbr = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new ap_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new ap_mstr(m, 
                                res.getString("ap_id"), 
                                res.getString("ap_vend"),
                                res.getString("ap_nbr"), 
                                res.getDouble("ap_amt"),
                                res.getDouble("ap_base_amt"), 
                                res.getString("ap_effdate"),
                                res.getString("ap_entdate"), 
                                res.getString("ap_duedate"),
                                res.getString("ap_type"), 
                                res.getString("ap_rmks"),
                                res.getString("ap_ref"), 
                                res.getString("ap_terms"),
                                res.getString("ap_acct"),
                                res.getString("ap_cc"),
                                res.getString("ap_applied"),
                                res.getString("ap_status"),
                                res.getString("ap_bank"),
                                res.getString("ap_curr"),
                                res.getString("ap_base_curr"), 
                                res.getString("ap_check"),
                                res.getString("ap_batch"),
                                res.getString("ap_site"),
                                res.getString("ap_subtype"),
                                res.getString("ap_entrytype"),
                                res.getString("ap_approved"),
                                res.getString("ap_approver"),
                                res.getDouble("ap_amt_tax"),
                                res.getDouble("ap_amt_sac")
                            );
                }
            }
            return r;
    }
    
    public static ArrayList<vod_mstr> _getVodMstr(String[] x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        ArrayList<vod_mstr> list = new ArrayList<vod_mstr>();
        vod_mstr r = null;
        String[] m = new String[2];
        String sqlSelect = "select * from vod_mstr where vod_id = ?";
          ps = con.prepareStatement(sqlSelect); 
          ps.setString(1, x[0]);
          res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
                r = new vod_mstr(m);
            } else {
                while(res.next()) {
                    m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                    r = new vod_mstr(m, res.getString("vod_id"), res.getString("vod_rvdid"), res.getInt("vod_rvdline"), res.getString("vod_item"),
                    res.getDouble("vod_qty"), res.getDouble("vod_voprice"), 
                    res.getString("vod_date"), res.getString("vod_vend"), res.getString("vod_invoice"), 
                    res.getString("vod_expense_acct"), res.getString("vod_expense_cc") , res.getString("vod_po") , 
                            res.getInt("vod_poline") , res.getString("vod_approved")  );
                    list.add(r);
                    }
            }
            return list;
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
     
    public static String[] addExpMstr(exp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addExpMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "select * from exp_mstr where exp_id = ?";
        String sqlInsert = "insert into exp_mstr (exp_id, exp_site, exp_entity, exp_name," +
        "exp_acct, exp_cc, exp_createdate, exp_changedate, exp_userid," +
        "exp_desc, exp_ref, exp_amt, exp_active)  " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.exp_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.exp_id);
            psi.setString(2, x.exp_site);
            psi.setString(3, x.exp_entity);
            psi.setString(4, x.exp_name);
            psi.setString(5, x.exp_acct);
            psi.setString(6, x.exp_cc);
            psi.setString(7, x.exp_createdate);
            psi.setString(8, x.exp_changedate);
            psi.setString(9, x.exp_userid);
            psi.setString(10, x.exp_desc);
            psi.setString(11, x.exp_ref);
            psi.setDouble(12, x.exp_amt);            
            psi.setString(13, x.exp_active);
            
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
    
    public static String[] updateExpMstr(exp_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateExpMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update exp_mstr set exp_site = ?, exp_entity = ?, exp_name = ?," +
        "exp_acct = ?, exp_cc = ?, exp_createdate = ?, exp_changedate = ?, exp_userid = ?," +
        "exp_desc = ?, exp_ref = ?, exp_amt = ?, exp_active = ? where exp_id =  = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.exp_site);
            ps.setString(2, x.exp_entity);
            ps.setString(3, x.exp_name);
            ps.setString(4, x.exp_acct);
            ps.setString(5, x.exp_cc);
            ps.setString(6, x.exp_createdate);
            ps.setString(7, x.exp_changedate);
            ps.setString(8, x.exp_userid);
            ps.setString(9, x.exp_desc);
            ps.setString(10, x.exp_ref);
            ps.setDouble(11, x.exp_amt);            
            ps.setString(12, x.exp_active);
            ps.setString(13, x.exp_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteExpMstr(exp_mstr x) {
     if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteExpMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from exp_mstr where exp_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.exp_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static exp_mstr getExpMstr(String[] x) {
        exp_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getExpMstr"});
            list.add(new String[]{"key",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServFAP");
                r = objectMapper.readValue(returnstring, exp_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new exp_mstr(m);
                return r;
            }
        }
        String sql = "select * from exp_mstr where exp_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new exp_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};             
                        r = new exp_mstr(m, res.getString("exp_id"), 
                            res.getString("exp_site"),    
                            res.getString("exp_entity"),
                            res.getString("exp_name"),
                            res.getString("exp_acct"),
                            res.getString("exp_cc"),
                            res.getString("exp_createdate"),    
                            res.getString("exp_changedate"),
                            res.getString("exp_userid"),
                            res.getString("exp_desc"),
                            res.getString("exp_ref"),
                            res.getDouble("exp_amt"),
                            res.getString("exp_active")
                        );
                    }
                }
            } 
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new exp_mstr(m);
        }
        return r;
    }
    

    public static ArrayList<String[]> getRecurringExpenseRecords(String site, String showall) {
         if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getRecurringExpenseRecords"});
            list.add(new String[]{"param1",  site});
            list.add(new String[]{"param2",  showall});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
         ArrayList<String[]> myarray = new ArrayList<String[]>();
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

                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
        
                if (showall.equals("1")) {                
                res = st.executeQuery("select * from exp_mstr left outer join pos_mstr on pos_key = exp_id " +
                                  " and pos_entrydate like " + "'" + dfdate.format(now).substring(0,8) + "%" + "'" +
                                  " where exp_entity <> '' and exp_site =  " + "'" + site + "'" +
                                  ";");
                } else {
                res = st.executeQuery("select * from exp_mstr left outer join pos_mstr on pos_key = exp_id " +
                                  " and pos_entrydate like " + "'" + dfdate.format(now).substring(0,8) + "%" + "'" +
                                  " where exp_entity <> '' and exp_active = '1' " +
                                  " and exp_site = " + "'" + site + "'" +
                                  ";");    
                }
                
                 
                
               while (res.next()) {
                myarray.add(new String[]{res.getString("exp_id"),
                    res.getString("exp_site"),
                    res.getString("exp_entity"),
                    res.getString("exp_name"),
                    res.getString("exp_desc"),
                    res.getString("exp_acct"),
                    res.getString("exp_amt"),
                    res.getString("pos_totamt")
                });                    
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

    public static ArrayList<String[]> getRecurringExpenseHistory(String key) {
         if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getRecurringExpenseHistory"});
            list.add(new String[]{"param1",  key});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
         ArrayList<String[]> myarray = new ArrayList<String[]>();
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

                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
        
                res = st.executeQuery("select * from pos_mstr where pos_key = " + "'" + key + "'" + " order by pos_entrydate desc;");                
                
               while (res.next()) {
                myarray.add(new String[]{res.getString("pos_key"),
                    res.getString("pos_nbr"),
                    res.getString("pos_entity"),
                    res.getString("pos_entityname"),
                    res.getString("pos_entrydate"),
                    res.getString("pos_aracct"),
                    res.getString("pos_totamt")
                });                    
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
    
    
    public static double getRecurringIncomeTotal(String site) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getRecurringIncomeTotal"});
            list.add(new String[]{"param1",  site});
            try {
                return jsonToDouble(sendServerPost(list, "", null, "dataServFAP")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0.00;
            }
        }
        
        double totincome = 0.00;

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

                res = st.executeQuery("select * from exp_mstr where exp_id = 'bsint' " +
                        " and exp_entity = '' " +
                        " and exp_site = " + "'" + site + "'" + ";");
                while (res.next()) {
                totincome += bsParseDouble(res.getString("exp_amt"));
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return totincome;

    }

    public static double getCashTranInvAssetTotal() {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getCashTranInvAssetTotal"});
            try {
                return jsonToDouble(sendServerPost(list, "", null, "dataServFAP")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0.00;
            }
        }
        
        double r = 0.00;

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

                    res = st.executeQuery("select sum(in_qoh * it_mtl_cost) as 'sum' from in_mstr " +
                        " inner join item_mstr on it_item = in_item where it_code = 'A' " );
                      while (res.next()) {
                          r += res.getDouble("sum");
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
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return r;

    }


    
    
    
    // misc
    public static String getCashTranBrowseView(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                 
                    res = st.executeQuery("select pos_nbr, pos_site, pos_key, pos_type, pos_entity, pos_entityname, pos_entrydate, pos_totqty, pos_totamt from pos_mstr " +
                        " where pos_entrydate >= " + "'" + keys[0] + "'" + 
                        " and pos_entrydate <= " + "'" + keys[1] + "'" +
                        " and pos_site = " + "'" + keys[2] + "'" +        
                        " order by pos_nbr desc;");
                    
                    while (res.next()) {                  
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("detail");
                        rowArray.put(res.getString("pos_nbr"));
                        rowArray.put(res.getString("pos_key"));
                        rowArray.put(res.getString("pos_type"));
                        rowArray.put(res.getString("pos_entity"));
                        rowArray.put(res.getString("pos_entityname"));
                        rowArray.put(res.getString("pos_entrydate"));
                        rowArray.put(bsNumber(res.getDouble("pos_totqty")));
                        rowArray.put(bsNumber(res.getDouble("pos_totamt")));
                        rowArray.put("click_tbd");
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
    }
   
    public static String getCashTranBrowseViewDet(String key) {
        JSONArray jsonarray = new JSONArray();
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
                 
                    res = st.executeQuery("select posd_nbr, posd_item, posd_desc, posd_ref, posd_qty, posd_netprice from pos_det " +
                        " where posd_nbr = " + "'" + key + "'" +  ";");
                    
                    while (res.next()) {                  
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("posd_nbr"));
                        rowArray.put(res.getString("posd_item"));
                        rowArray.put(res.getString("posd_desc"));
                        rowArray.put(res.getString("posd_ref"));
                        rowArray.put(res.getString("posd_qty"));
                        rowArray.put(res.getString("posd_netprice"));
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
    }
   
    
    public static String[] cashBuy(ArrayList<String[]> details, String[] headers) {
        // headers = vendorid, expensenbr, effdate, ref, po, site, currency
        // details = item, qty, price
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","cashBuy"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(details);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(headers);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
       String[] m = new String[2];
       vd_mstr vd = getVendMstr(new String[]{headers[0]});     
       int receiverNbr = OVData.getNextNbr("receiver");
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       // create vod list
        ArrayList<fapData.vod_mstr> vodlist = new ArrayList<fapData.vod_mstr>();
        ArrayList<rcvData.recv_det> recvlist = new ArrayList<rcvData.recv_det>();
        ArrayList<ordData.pos_det> posdlist = new ArrayList<ordData.pos_det>();
        String[] terms = OVData.getTermsResults(parseDate(headers[2]), vd.vd_terms());
        int batchid = OVData.getNextNbr("batch");
        double totamt = 0.00;
        double totqty = 0.00;
         
          int j = 0;
          for (String[] d : details) {
             fapData.vod_mstr x = new fapData.vod_mstr(null, 
                headers[1], // id
                String.valueOf(receiverNbr),  // rvid
                bsParseInt(String.valueOf(j + 1)), // rvdline
                d[0], // item
                bsParseDouble(d[1]), // qty
                bsParseDouble(d[2]), // price
                headers[2], // date
                headers[0], // vend
                headers[3], // invoice 
                vd.vd_ap_acct(),
                vd.vd_ap_cc(),
                headers[4], // po
                bsParseInt(String.valueOf(j + 1)), // poline
                "1"    // auto approved
                );
        vodlist.add(x);
         
         
          
         // create receiver det 
             rcvData.recv_det rvd = new rcvData.recv_det(null, 
                String.valueOf(receiverNbr), // receiver
                headers[4], // po
                bsParseInt(String.valueOf(j + 1)), // poline
                headers[3], // packingslip
                d[0], // item
                bsParseDouble(d[1]),  // qty
                headers[2],
                bsParseDouble(d[2]),
                bsParseDouble(d[2]),
                0,  
                "", // lot
                "", // wh
                "", // serial
                "",  // loc
                "", // jobnbr
                headers[5],
                "", // status
                bsParseInt(String.valueOf(j + 1)), // rline
                0, // voqty
                bsParseDouble(d[2]), // cost
                "EA" // uom    
                );
        recvlist.add(rvd);
                
            // create pos_det
            ordData.pos_det posd = new ordData.pos_det(null,
                headers[1],
                bsNumber(j),
                d[0],
                "", // desc
                "", // ref
                d[1],
                d[2],
                "0",
                d[2],
                "0",
                "", // acct
                "" // cc
            );
            posdlist.add(posd);
        
        totamt += bsParseDouble(d[1]) * bsParseDouble(d[2]);
        totqty += bsParseDouble(d[1]);
        j++;
        } // end of detail loop
          
         // create AP 
         fapData.ap_mstr ap = new fapData.ap_mstr(null, 
                "", //ap_id
                headers[0], // ap_vend, 
                headers[1], // ap_nbr
                totamt, // ap_amt
                totamt, // ap_base_amt
                headers[2], // ap_effdate 
                headers[2], // ap_entdate        
                terms[0],   
                "V", // ap_type
                "auto-voucher", //ap_rmks
                String.valueOf(receiverNbr), //ap_ref
                vd.vd_terms(), //ap_terms
                vd.vd_ap_acct(), //ap_acct
                vd.vd_ap_cc(), //ap_cc
                "0", //ap_applied
                "o", //ap_status
                vd.vd_bank(), //ap_bank
                vd.vd_curr(), //ap_curr
                headers[6], //ap_base_curr
                headers[1], //ap_check // in this case voucher number is reference field
                String.valueOf(batchid), //ap_batch
                headers[5], //ap_site
                "Receipt",
                "",
                "1",
                "",
                0,
                0); 
         
         // recv_mstr
         rcvData.recv_mstr rv = new rcvData.recv_mstr(null, 
                String.valueOf(receiverNbr),
                headers[0],
                headers[2],
                "", // status
                headers[3],
                bsmf.MainFrame.userid,
                vd.vd_ap_acct(),
                vd.vd_ap_cc(),
                vd.vd_terms(),
                headers[5],
                "", // confdate
                "", // ref
                "" // remarks
                );
         
         // pos_mstr         
         ordData.pos_mstr pos = new ordData.pos_mstr(null, 
                headers[1],
                String.valueOf(receiverNbr),
                "buy",
                vd.vd_addr(),
                vd.vd_name(),
                headers[2],
                "", // time
                vd.vd_ap_acct(),
                vd.vd_ap_cc(), 
                bsNumber(totqty),
                bsNumber(j),
                "0", // tax
                currformatDouble(totamt),
                vd.vd_bank(),
                currformatDouble(totamt),
                "", // status
                headers[5]
                );
         
         addPOSTransaction(posdlist, pos);
       
       //fapData.ap_mstr ap = createAPMstr(expensenbr.getText(), vd);
       m = addReceiverTransaction(recvlist, rv, ap, vodlist);
       return m;
    }
        
    public static String[] cashSell(ArrayList<String[]> details, String[] headers) {
         // headers = cust, transnbr, po, site, currency, remarks
        // details = "Line", "Item", "Qty", "Price", "Desc", "Ref"
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","cashSell"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(details);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(headers);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        String[] message = new String[2];
        message[0] = "";
        message[1] = ""; 
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
                boolean error = false;
                String key = "";
                
                
              
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
                 
                String acct = OVData.getDefaultARAcct();
                String cc = OVData.getDefaultARCC();
                                    
                    
                 if (proceed) {
                     
                 
                          int shipperid = OVData.getNextNbr("shipper");   
                          key = String.valueOf(shipperid);
                             boolean iserror = shpData.CreateShipperHdr(key, headers[3],
                             String.valueOf(key), 
                              headers[0], // sh_cust
                              headers[0],  // sh_ship
                              headers[1].replace("'", ""), // sh_so
                              headers[2].replace("'", ""),  // sh_po
                              headers[2].replace("'", ""),  // sh_ref
                              dfdate.format(now), // duedate
                              dfdate.format(now),  // orddate
                              headers[5].replace("'", ""), // sh_rmks
                              "", "A");  // shipvia, ShipType

                     if (iserror) {
                         return message = new String[]{"1", "Error creating shipper header"};
                     }        
                             
                         int j = 0;
                         double totamt = 0.00;
                         double totqty = 0.00;
                         // details = "Line", "Item", "Qty", "Price", "Desc", "Ref"
                         for (String[] d : details) {
                             shpData.CreateShipperDet(String.valueOf(shipperid), d[1], "", "", "", "", d[2], "EA", 
                                     d[3], "0", d[3], dfdate.format(now), 
                                     d[4], d[0], headers[3], "", "", "0");
                             totamt += bsParseDouble(d[2]) * bsParseDouble(d[3]);
                             totqty += bsParseDouble(d[2]);
                             j++;
                         }
                    

                     // now confirm shipment
                    message = confirmShipperTransaction("cash", String.valueOf(shipperid), now);
                     if (message[0].equals("1")) { // if error
                       error = true;
                       return message;
                     } 
                     
                                     
                     // now emulate AR payment
                     if (! error) {
                     String batchnbr = String.valueOf(OVData.getNextNbr("ar"));
                      st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_type, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_paiddate, ar_acct, ar_cc, "
                        + "ar_status, ar_bank, ar_curr, ar_base_curr, ar_site ) "
                        + " values ( " + "'" + headers[0] + "'" + ","
                        + "'" + batchnbr + "'" + ","
                        + "'" + currformatDouble(totamt).replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + "P" + "'" + ","
                        + "'" + shipperid + "'" + ","
                        + "'" + headers[5] + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + "c" + "'"  + ","
                        + "'" + OVData.getDefaultARBank() + "'" + ","
                        + "'" + headers[4] + "'" + ","     
                        + "'" + headers[4] + "'" + ","         
                        + "'" + headers[3] + "'"
                        + ")"
                        + ";");
                      
                      
                     
                      
                        j = 0;                        
                        for (String[] d : details) {
                            st.executeUpdate("insert into ard_mstr "
                                + "(ard_nbr, ard_cust, ard_ref, ard_line, ard_date, ard_amt, ard_amt_tax, ard_acct, ard_cc ) "
                                + " values ( " + "'" + batchnbr + "'" + ","
                                    + "'" + headers[0] + "'" + ","
                                + "'" + shipperid + "'" + ","
                                + "'" + (j + 1) + "'" + ","
                                + "'" + dfdate.format(now) + "'" + ","
                                + "'" + currformatDouble(bsParseDouble(d[2]) * bsParseDouble(d[3])) + "'"  + ","
                                + "'" + "0" + "'" + ","
                                + "'" + acct + "'" + ","
                                + "'" + cc + "'"   
                                + ")"
                                + ";");                            
                            
                            
                            j++;
                        }
                    
                         // update AR entry for original invoices with status and open amt  
                        error = OVData.ARUpdate(batchnbr);
                        if (! error) {
                        error = fglData.glEntryFromARPayment(batchnbr, now);
                        }
                     }
                    // end of emulate AR Payment
                     
                    
                    // now POS
                     ordData.pos_mstr pos = new ordData.pos_mstr(null, 
                            headers[1],
                            key,
                            "sell",
                            headers[0], // cust
                            "", // name
                            dfdate.format(now), // date
                            "", // time
                            acct,
                            cc, 
                            bsNumber(totqty),
                            bsNumber(j),
                            "0", // tax
                            currformatDouble(totamt),
                            OVData.getDefaultARBank(),
                            currformatDouble(totamt),
                            "", // status
                            headers[5]
                            );
                            // create pos_det
                            ArrayList<ordData.pos_det> posdlist = new ArrayList<ordData.pos_det>();         
                            j = 0;         
                            for (String[] d : details) {         
                            ordData.pos_det posd = new ordData.pos_det(null,
                                headers[1],
                                bsNumber(j),
                                d[1],
                                "", // desc
                                "", // ref
                                d[2],
                                d[3],
                                "0",
                                d[3],
                                "0",
                                acct, // acct
                                cc // cc
                            );
                            posdlist.add(posd);
                            j++;
                            }

                     addPOSTransaction(posdlist, pos);

                    
                    
                     
                     if (! error) {
                        message = new String[]{"0", "sell complete"};
                     } else {
                         message = new String[]{"1", "Unable to complete sell transaction"};
                     }
                    
                 }  // proceed
                  
                   
                  
                if (OVData.isAutoPost()) {
                    fglData.PostGL();
                }     
                    
            } catch (SQLException s) {
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
        
        return message;

    }
    
    public static String[] cashExpense(ArrayList<String[]> details, String[] headers) {
        // headers = vendorid, expensenbr, effdate, ref, po, site, currency, remarks
        // details = "Line", "Item", "Qty", "Price", "Ref", "Acct"
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","cashExpense"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(details);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(headers);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        
        String[] vi = getVendInfo(headers[0]);  // addr, acct, cc, currency, bank, terms, site
        
        int j = 0;
        double totamt = 0.00;
        double totqty = 0.00;
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        ArrayList<fapData.vod_mstr> list = new ArrayList<fapData.vod_mstr>();
         for (String[] d : details) {
             fapData.vod_mstr y = new fapData.vod_mstr(null, 
                headers[1],
                "expense",
                bsParseInt(d[0]),
                d[1],
                bsParseDouble(d[2].replace(defaultDecimalSeparator, '.')),
                bsParseDouble(d[3].replace(defaultDecimalSeparator, '.')),
                headers[2],
                headers[0],
                headers[1], 
                d[5],
                vi[2],
                headers[4],
                bsParseInt(d[0]),
                "1"
                );
             j++;
             totamt += ((bsParseDouble(d[2]) * bsParseDouble(d[3])));
             totqty += bsParseDouble(d[2]);
        list.add(y);
        
         }
        
        fapData.ap_mstr x = new fapData.ap_mstr(null, 
                "", //ap_id
                headers[0], // ap_vend, 
                headers[1], // ap_nbr
                totamt, // ap_amt
                totamt, // ap_base_amt
                headers[2], // ap_effdate
                headers[2], // ap_entdate
                headers[2], // ap_duedate        
                "V", // ap_type
                headers[7], //ap_rmks
                headers[4], //ap_ref
                vi[5], //ap_terms
                vi[1], //ap_acct
                vi[2], //ap_cc
                "0", //ap_applied
                "o", //ap_status
                vi[4], //ap_bank
                vi[3], //ap_curr
                vi[3], //ap_base_curr
                headers[1], //ap_check // in this case voucher number is reference field
                "", //ap_batch
                headers[5], //ap_site
                "Expense",
                "",
                "1",
                "",
                0,
                0); 
        
        // now POS
                     ordData.pos_mstr pos = new ordData.pos_mstr(null, 
                            headers[1],
                            headers[1],
                            "expense",
                            headers[0], // entity
                            "", // name
                            headers[2], // date
                            "", // time
                            vi[1],
                            vi[2], 
                            bsNumber(totqty),
                            bsNumber(j),
                            "0", // tax
                            currformatDouble(totamt),
                            vi[4],
                            currformatDouble(totamt),
                            "", // status
                            headers[5]
                            );
                            // create pos_det
                            ArrayList<ordData.pos_det> posdlist = new ArrayList<ordData.pos_det>();         
                            j = 0;         
                            for (String[] d : details) {         
                            ordData.pos_det posd = new ordData.pos_det(null,
                                headers[1],
                                bsNumber(j),
                                d[1],
                                "", // desc
                                "", // ref
                                d[2],
                                d[3],
                                "0",
                                d[3],
                                "0",
                                vi[1], // acct
                                vi[2] // cc
                            );
                            posdlist.add(posd);
                            j++;
                            }

                     addPOSTransaction(posdlist, pos);
        
        
        String[] m = VouchAndPayTransaction("AP-Cash", list, x, false);
               
        return m;
    }
    
    public static String[] cashIncome(ArrayList<String[]> details, String[] headers) {
         // headers = cust, transnbr, effdate, desc, site, currency, ref
        // details = "Line", "Item", "Qty", "Price", "Ref", "Acct"
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","cashSell"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(details);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(headers);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        String[] message = new String[2];
        message[0] = "";
        message[1] = ""; 
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
                boolean error = false;
                String key = "";
                
                
              
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                    
                
                
                String bank = OVData.getDefaultARBank();
                String cashacct = OVData.getDefaultBankAcct(bank);
                String cc = OVData.getDefaultCC();
                     
                     
               
               // "Line", "Item", "Qty", "Price", "Ref", "Acct"
                    int i = 0;
                    for (String[] d : details) {
                       
                          // Credit Income Account
                       st.executeUpdate("insert into gl_tran "
                        + "(glt_line, glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc, glt_userid, glt_entdate )"
                        + " values ( " 
                        + "'" + d[0] + "'" + ","
                        + "'" + d[5] + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + headers[2] + "'" + ","
                        + "'" + currformatDouble(bsParseDouble(d[3]) * -1).replace(defaultDecimalSeparator,'.') + "'" + ","
                        + "'" + currformatDouble(bsParseDouble(d[3]) * -1).replace(defaultDecimalSeparator,'.') + "'" + ","
                        + "'" + headers[5] + "'" + ","
                        + "'" + headers[5] + "'" + ","        
                        + "'" + headers[1] + "'" + ","
                        + "'" + headers[4] + "'" + ","
                        + "'" + "JL" + "'" + ","
                        + "'" + d[1].replace(",", "") + "'" + ","
                        + "'" + bsmf.MainFrame.userid + "'" + ","
                         + "'" + dfdate.format(now) + "'"
                                + ")"
                        + ";" );
                    
                       // Debit Cash Account
                        st.executeUpdate("insert into gl_tran "
                        + "(glt_line, glt_acct, glt_cc, glt_effdate, glt_amt, glt_base_amt, glt_curr, glt_base_curr, glt_ref, glt_site, glt_type, glt_desc, glt_userid, glt_entdate )"
                        + " values ( " 
                        + "'1'" + ","
                        + "'" + cashacct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + headers[2] + "'" + ","
                        + "'" + currformatDouble(bsParseDouble(d[3])).replace(defaultDecimalSeparator,'.') + "'" + ","
                        + "'" + currformatDouble(bsParseDouble(d[3])).replace(defaultDecimalSeparator,'.') + "'" + ","
                        + "'" + headers[5] + "'" + ","
                        + "'" + headers[5] + "'" + ","    
                        + "'" + headers[1] + "'" + ","
                        + "'" + headers[4] + "'" + ","
                        + "'" + "JL" + "'" + ","
                        + "'" + d[1].replace(",", "") + "'" + ","
                        + "'" + bsmf.MainFrame.userid + "'" + ","
                         + "'" + dfdate.format(now) + "'"
                                + ")"
                        + ";" );  
                        
                        i++;
                    }
                    
                    if (i > 0) {
                        
                        // now POS
                     // create pos_det
                            double totamt = 0.00;
                            ArrayList<ordData.pos_det> posdlist = new ArrayList<ordData.pos_det>();         
                            int j = 0;         
                            for (String[] d : details) {         
                            ordData.pos_det posd = new ordData.pos_det(null,
                                headers[1],
                                bsNumber(j),
                                d[1],
                                "", // desc
                                "", // ref
                                d[2],
                                d[3],
                                "0",
                                d[3],
                                "0",
                                d[5], // acct
                                cc // cc
                            );
                            totamt += bsParseDouble(d[3]);
                            posdlist.add(posd);
                            j++;
                            }   
                            
                     ordData.pos_mstr pos = new ordData.pos_mstr(null, 
                            headers[1],
                            key,
                            "income",
                            headers[0], // cust
                            "", // name
                            dfdate.format(now), // date
                            "", // time
                            cashacct,
                            cc, 
                            bsNumber(j),
                            bsNumber(j),
                            "0", // tax
                            currformatDouble(totamt),
                            bank,
                            currformatDouble(totamt),
                            "", // status
                            headers[4] // site
                            );
                     addPOSTransaction(posdlist, pos);
                        
                    }
                    
                      
                    if (i == 0) {
                        message = new String[]{"1", "An Error Occurred in Income"};
                    } else {
                    message = new String[]{"0", "income complete"};
                    }
                    
                  
                   
                  
                if (OVData.isAutoPost()) {
                    fglData.PostGL();
                }     
                    
            } catch (SQLException s) {
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
        
        return message;

    }
    
    public static String[] cashExpenseRecurring(ArrayList<String[]> details, String[] headers) {
         // headers = site
        // details = "History", "ID", "Site", "Entity", "Name", "Desc", "Acct", "Amt", "ThisMonth?", "ExactAmt", "Pay?", "dummyyesno"
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","cashExpenseRecurring"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(details);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(headers);
                System.out.println("HERE: " + jsonString);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        
        String[] message = new String[2];
        message[0] = "";
        message[1] = ""; 
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
                boolean error = false;
                String key = "";
                
                
              
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                String[] vi = getVendInfo(headers[0]);  // addr, acct, cc, currency, bank, terms, site
                    
               
                String po = "cashtran";
                     // loop from here through end of selected items to be paid
                     double totamt = 0.00;
                     double totqty = 0.00;
                     
                     for (String[] d : details) {
                     
                         if (! Boolean.valueOf(d[10])) {  // if not selected in checkbox
                             continue;
                         }
                     
                         
                         int exp = OVData.getNextNbr("expensenumber");
                         totamt = bsParseDouble(d[9]); // qty = 1
                         totqty = 1;
                         key = String.valueOf(exp);
                         // "History", "ID", "Site", "Entity", "Name", "Desc", "Acct", "Amt", "ThisMonth?", "ExactAmt", "Pay?", "dummyyesno"
                       st.executeUpdate("insert into ap_mstr "
                        + "(ap_vend, ap_site, ap_nbr, ap_amt, ap_type, ap_ref, ap_rmks, "
                        + "ap_entdate, ap_effdate, ap_duedate, ap_curr, ap_acct, ap_cc, "
                        + "ap_terms, ap_status, ap_bank ) "
                        + " values ( " + "'" + d[3] + "'" + ","
                              + "'" + d[2] + "'" + ","
                        + "'" + key + "'" + ","
                        + "'" + d[9].replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + "V" + "'" + ","
                        + "'" + d[3].replace("'", "''") + "'" + ","
                        + "'" + "" + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + vi[3] + "'" + ","
                        + "'" + vi[1] + "'" + ","
                        + "'" + vi[2] + "'" + ","
                        + "'" + vi[5] + "'" + ","
                        + "'" + "o" + "'"  + ","
                        + "'" + vi[4] + "'"
                        + ")"
                        + ";");
               
                        // "History", "ID", "Site", "Entity", "Name", "Desc", "Acct", "Amt", "ThisMonth?", "ExactAmt", "Pay?", "dummyyesno"          
                        st.executeUpdate("insert into vod_mstr "
                            + "(vod_id, vod_vend, vod_rvdid, vod_rvdline, vod_item, vod_qty, "
                            + " vod_voprice, vod_date, vod_invoice, vod_expense_acct, vod_expense_cc )  "
                            + " values ( " + "'" + key + "'" + ","
                                + "'" + d[3] + "'" + ","
                            + "'" + "expense" + "'" + ","
                            + "'" + "1" + "'" + ","
                            + "'" + d[5] + "'" + ","
                            + "'" + "1" + "'" + ","
                            + "'" + d[9].replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "'" + dfdate.format(now) + "'" + ","
                            + "'" + d[1] + "'" + ","
                            + "'" + d[6] + "'" + ","
                            + "'" + vi[2] + "'"
                            + ")"
                            + ";");
                  
                 
                    
                     
                    
                    /* create gl_tran records */
                    //    if (! error)
                    //    error = fglData.glEntryFromVoucherExpense(key, now);
                     
                    message = fapData.APExpense(OVData.getNextNbr("batch"), 
                            OVData.getDefaultCurrency(), 
                            now, 
                            exp, 
                            key, d[1], d[3], bsParseDouble(d[9]), "AP-Cash");
                        
                    if (error) {
                        message = new String[]{"1", "An Error Occurred in Expense"};
                    } else {
                    message = new String[]{"0", "expense complete"};
                    }
                    
                    
                    // now POS  one POS record per recurring expense line in details
                     // create pos_det
                            ArrayList<ordData.pos_det> posdlist = new ArrayList<ordData.pos_det>(); 
                            ordData.pos_det posd = new ordData.pos_det(null,
                                key,
                                "1",
                                d[5],
                                d[5], // desc
                                "", // ref
                                d[2],
                                d[3],
                                "0",
                                d[3],
                                "0",
                                vi[1], // acct
                                vi[2] // cc
                            );
                            posdlist.add(posd);
                           
                            
                     ordData.pos_mstr pos = new ordData.pos_mstr(null, 
                            key,
                            d[1],
                            "expense",
                            d[3], // entity
                            d[4], // name
                            dfdate.format(now), // date
                            "", // time
                            vi[1],
                            vi[2], 
                            "1",  // totqty
                            "1", // totlines
                            "0", // tax
                            currformatDouble(totamt),
                            vi[4],
                            currformatDouble(totamt),
                            "", // status
                            d[2]
                            );
                     addPOSTransaction(posdlist, pos);
                    
                    
                    }   
                 // loop end  
                 
                 
                 
                 
                  
                if (OVData.isAutoPost()) {
                    fglData.PostGL();
                }     
                    
            } catch (SQLException s) {
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
        
        return message;

    }
    
    public static String updateRecurExp_Income(String site, String amt) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateRecurExp_Income"});
            list.add(new String[]{"param1",site});
            list.add(new String[]{"param2",amt});
            try {
                return sendServerPost(list, "", null, "dataServFAP");
            } catch (IOException ex) {
                bslog(ex);
                return "";
            }
        }   
        
        String r = "";
        try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            int i = 0;
            try {
                            
                res = st.executeQuery("SELECT *  FROM  exp_mstr where exp_id = 'bsint' and exp_site = " + "'" + site + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                    st.executeUpdate("insert into exp_mstr (exp_id, exp_site, exp_amt) values (" + 
                            "'" + "bsint" + "'" + "," +
                            "'" + site + "'" + "," +
                            "'" + amt + "'" +      
                            ") ;");           
                          r = "income set";
                    } else {
                    st.executeUpdate("update exp_mstr set exp_amt = " + "'" + amt + "'" +
                            " where exp_id = 'bsint' and exp_site = " + "'" + site + "'" 
                            + ";");  
                           r = "income updated";
                    }
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
        
        return r;
       }
    
    public static String updateExpActive(String key, String status) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateExpActive"});
            list.add(new String[]{"param1",key});
            list.add(new String[]{"param2",status});
            try {
                return sendServerPost(list, "", null, "dataServFAP");
            } catch (IOException ex) {
                bslog(ex);
                return "";
            }
        }   
        
        String r = "";
        try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            int i = 0;
            try {
                            
                st.executeUpdate("update exp_mstr set exp_active = " + BlueSeerUtils.ConvertStringToBool(status) +
                            " where exp_id = " + "'" + key + "'" 
                            + ";"); 
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
        
        return r;
       }
    
    
    public static String getVoucherBrowseView(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                keys[1] = (keys[1].isBlank()) ? bsmf.MainFrame.lowchar : keys[1];  
                keys[2] = (keys[2].isBlank()) ? bsmf.MainFrame.hichar : keys[2];  
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.lowchar : keys[3];  
                keys[4] = (keys[4].isBlank()) ? bsmf.MainFrame.hichar : keys[4];  
                if (keys[0].isBlank()) {
                res = st.executeQuery(" select ap_nbr, ap_status, ap_ref, ap_rmks, ap_vend, ap_amt, ap_subtype, ap_approved " +
                             " FROM  ap_mstr where " + 
                             " ap_vend >= " + "'" + keys[1] + "'" + " AND " +
                             " ap_vend <= " + "'" + keys[2] + "'" + " AND " +
                             " ap_nbr >= " + "'" + keys[3] + "'" + " AND " +
                             " ap_nbr <= " + "'" + keys[4] + "'" + " AND " +
                             " ap_type = 'V' order by ap_nbr desc ;");
                } else {
                    res = st.executeQuery(" select ap_nbr, ap_status, ap_ref, ap_rmks, ap_vend, ap_amt, ap_subtype, ap_approved " +
                             " FROM  ap_mstr where " + 
                             " ap_ref like " + "'%" + keys[0] + "%'" + " AND " +
                             " ap_type = 'V' order by ap_nbr desc ;");
                }
                    while (res.next()) {                   
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ap_nbr"));
                        rowArray.put(res.getString("ap_vend"));
                        rowArray.put(res.getString("ap_subtype"));
                        rowArray.put(res.getString("ap_ref"));                        
                        rowArray.put(res.getString("ap_rmks"));
                        rowArray.put(res.getString("ap_status"));
                        rowArray.put(bsNumber(res.getDouble("ap_amt")));
                        rowArray.put(ConvertIntToYesNo(res.getInt("ap_approved")));
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
    }
   
    public static String getVoucherBrowseDetView(String po, String line) {
        JSONArray jsonarray = new JSONArray();
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
               
                res = st.executeQuery("select rvd_id, rvd_poline, rvd_item, rvd_packingslip, rvd_date, rvd_netprice, rvd_qty, rvd_voqty " +
                        " from recv_det " +
                        " where rvd_po = " + "'" + po + "'" +
                        " AND rvd_poline = " + "'" + line + "'" + ";");
                    while (res.next()) {                        
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("rvd_id"));
                        rowArray.put(res.getString("rvd_poline"));
                        rowArray.put(res.getString("rvd_item"));
                        rowArray.put(res.getString("rvd_packingslip"));                        
                        rowArray.put(res.getString("rvd_date"));
                        rowArray.put(bsNumber(res.getDouble("rvd_netprice")));
                        rowArray.put(bsNumber(res.getDouble("rvd_qty")));
                        rowArray.put(bsNumber(res.getDouble("rvd_voqty")));
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
    }
   
    public static String getFapRptPickerData(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                 
                int i = 0;
                if (keys[0].equals("poByOrdDateRange")) {
                res = st.executeQuery(" select po_nbr, po_vend, vd_name, po_site, po_rmks, po_ord_date, po_due_date, po_status, " +
                        " sum(pod_ord_qty  * pod_netprice) as 'total' " +
                        " FROM  po_mstr inner join pod_mstr on pod_nbr = po_nbr " +
                        " inner join vd_mstr on vd_addr = po_vend " +
                        " where po_ord_date >= " + "'" + keys[1] + "'" + 
                        " and po_ord_date <= " + "'" + keys[2] + "'" +
                        " group by po_nbr, po_vend, vd_name, po_site, po_rmks, po_ord_date, po_due_date, po_status " +
                        " order by po_nbr desc ;");                  
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("po_nbr"));
                            rowArray.put(res.getString("po_vend"));
                            rowArray.put(res.getString("vd_name"));
                            rowArray.put(res.getString("po_rmks"));
                            rowArray.put(res.getString("po_ord_date"));
                            rowArray.put(res.getString("po_due_date"));
                            rowArray.put(res.getString("po_status"));
                            rowArray.put(currformat(res.getString("total")));
                            jsonarray.put(rowArray);

                    } 
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
        return jsonarray.toString(); 
    }
    
    public static ArrayList<String[]> getAPExpenseByVendor(String fromdate, String todate, String fromvend, String tovend, String site) {
    if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getAPExpenseByVendor"});
            list.add(new String[]{"param1", fromdate});
            list.add(new String[]{"param2", todate});
            list.add(new String[]{"param3", fromvend});
            list.add(new String[]{"param4", tovend});
            list.add(new String[]{"param5", site});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
    }
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
            res = st.executeQuery("select ap_vend, sum(vod_voprice * vod_qty) as 'sum' from ap_mstr " +
                             //  " ap_ref, ap_effdate, ap_duedate, ap_amt, ap_base_amt,  " +
                             //  " ap_status, ap_curr, vod_item, vod_expense_acct " +
                             //  " inner join vd_mstr on vd_addr = ap_vend " +
                               " inner join vod_mstr on vod_id = ap_nbr " + 
                               " where ap_vend >= " + "'" + fromvend + "'" +
                               " and ap_vend <= " + "'" + tovend + "'" +
                               " and ap_effdate >= " + "'" + fromdate + "'" +
                               " and ap_effdate <= " + "'" + todate + "'" +
                               " and ap_type <> 'V' " +
                               " and ap_status = 'c' " +
                               " group by ap_vend " +
                               ";");
           while (res.next()) {
               String[] x = new String[2];
               x[0] = res.getString("ap_vend");
               x[1] = res.getString("sum");
                myarray.add(x);
            }

       }
        catch (SQLException s){
             bslog(s);
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

    public static ArrayList<String[]> getAPExpenseByAcct(String fromdate, String todate, String fromvend, String tovend, String site) {
    if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getAPExpenseByAcct"});
            list.add(new String[]{"param1", fromdate});
            list.add(new String[]{"param2", todate});
            list.add(new String[]{"param3", fromvend});
            list.add(new String[]{"param4", tovend});
            list.add(new String[]{"param5", site});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
    }
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
            res = st.executeQuery("select vod_expense_acct, sum(vod_voprice * vod_qty) as 'sum' from ap_mstr " +
                             //  " ap_ref, ap_effdate, ap_duedate, ap_amt, ap_base_amt,  " +
                             //  " ap_status, ap_curr, vod_item, vod_expense_acct " +
                             //  " inner join vd_mstr on vd_addr = ap_vend " +
                               " inner join vod_mstr on vod_id = ap_nbr " + 
                               " where ap_vend >= " + "'" + fromvend + "'" +
                               " and ap_vend <= " + "'" + tovend + "'" +
                               " and ap_effdate >= " + "'" + fromdate + "'" +
                               " and ap_effdate <= " + "'" + todate + "'" +
                               " and ap_type <> 'V' " +
                               " and ap_status = 'c' " +
                               " group by vod_expense_acct " +
                               ";");
           while (res.next()) {
               String[] x = new String[2];
               x[0] = res.getString("vod_expense_acct");
               x[1] = res.getString("sum");
                myarray.add(x);
            }

       }
        catch (SQLException s){
             bslog(s);
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

    public static ArrayList<String[]> getCashTranChartExpense(String fromdate, String todate, String site) {
    if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getCashTranChartExpense"});
            list.add(new String[]{"param1", fromdate});
            list.add(new String[]{"param2", todate});
            list.add(new String[]{"param3", site});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
    }
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
            res = st.executeQuery("select posd_acct, ac_desc, sum(posd_netprice * posd_qty) as 'sum' from pos_det " +
                        " inner join pos_mstr on pos_nbr = posd_nbr  " +
                        " inner join ac_mstr on ac_id = posd_acct  " +
                        " where pos_entrydate >= " + "'" + fromdate + "'" +
                        " AND pos_entrydate <= " + "'" + todate + "'" +
                        " AND pos_type = 'expense' " +
                        " AND pos_site = " + "'" + site + "'" +       
                        " group by posd_acct, ac_desc order by posd_acct desc   ;");
           while (res.next()) {
               String[] x = new String[2];
               x[0] = res.getString("ac_desc");
               x[1] = res.getString("sum");
                myarray.add(x);
            }

       }
        catch (SQLException s){
             bslog(s);
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

    public static ArrayList<String[]> getCashTranChartBuySell(String fromdate, String todate, String site) {
    if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getCashTranChartBuySell"});
            list.add(new String[]{"param1", fromdate});
            list.add(new String[]{"param2", todate});
            list.add(new String[]{"param3", site});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
    }
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
            res = st.executeQuery("select posd_acct, ac_desc, sum(posd_netprice * posd_qty) as 'sum' from pos_det " +
                        " inner join pos_mstr on pos_nbr = posd_nbr  " +
                        " inner join ac_mstr on ac_id = posd_acct  " +
                        " where pos_entrydate >= " + "'" + fromdate + "'" +
                        " AND pos_entrydate <= " + "'" + todate + "'" +
                        " AND pos_type <> 'expense' " +
                        " AND pos_site = " + "'" + site + "'" +       
                        " group by posd_acct, ac_desc order by posd_acct desc   ;");
           while (res.next()) {
               String[] x = new String[2];
               x[0] = res.getString("ac_desc");
               x[1] = res.getString("sum");
                myarray.add(x);
            }

       }
        catch (SQLException s){
             bslog(s);
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

    
    public static String[] getPOsummaryChargesTaxes(String po) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPOsummaryChargesTaxes"});
            list.add(new String[]{"param1", po});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServFAP"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        } 
        
        String[] r = new String[]{"0", "0", "0"}; // gross, tax, sac
        double taxamt = 0.00;
        double sacamt = 0.00;
        double grossamt = 0.00;
        
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
                    
                    res = st.executeQuery("select pod_listprice, pod_ord_qty  from pod_mstr where pod_nbr = " + "'" + po + "';" );
                    while (res.next()) {
                        grossamt += (bsParseDouble(res.getString("pod_listprice")) * bsParseDouble(res.getString("pod_ord_qty")));
                    }

                   res = st.executeQuery("select *  from po_meta where pom_nbr = " + "'" + po + "';" );
                   while (res.next()) {
                        if (res.getString("pom_type").equals("tax")) {
                            if (res.getString("pom_amttype").equals("amount")) {
                                taxamt += bsParseDouble(res.getString("pom_amt"));
                            } else {
                                taxamt += (grossamt + (grossamt * (bsParseDouble(res.getString("pom_amt")) / 100)));
                            }
                        }
                        if (res.getString("pom_type").equals("charge") || res.getString("pom_type").equals("discount")) {
                            if (res.getString("pom_amttype").equals("amount")) {
                                sacamt += bsParseDouble(res.getString("pom_amt"));
                            } else {
                                sacamt += (grossamt + (grossamt * (bsParseDouble(res.getString("pom_amt")) / 100)));
                            }
                        }
                    }
                   
                    // standard taxes from header po_tax
                    res = st.executeQuery("select *  from po_tax where pot_nbr = " + "'" + po + "';" );
                   while (res.next()) {
                        if (res.getString("pot_type").equals("tax")) {
                            if (res.getString("pot_amttype").equals("amount")) {
                                taxamt += bsParseDouble(res.getString("pot_amt"));
                            } else {
                                taxamt += (grossamt + (grossamt * (bsParseDouble(res.getString("pot_amt")) / 100)));
                            }
                        }
                    }
                   
                    
                   
                   
                   r[0] = bsNumber(grossamt);
                   r[1] = bsNumber(taxamt);
                   r[2] = bsNumber(sacamt);

                }  catch (SQLException s){
                 bslog(s);
                } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
            } catch (Exception e){
            MainFrame.bslog(e);
            }
        
        return r;
    }
    
    public static void updateAPVoucherStatus(String nbr, String status) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateAPVoucherStatus"});
            list.add(new String[]{"param1",nbr});
            list.add(new String[]{"param2",status});
            try {
                sendServerPost(list, "", null, "dataServFAP");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }   
        
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
    
    public static String getVendPaymentsByYear(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                             
                if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                 res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_curr, sum(ap_amt) as total, strftime('%Y', ap_effdate)  " +
                        " from ap_mstr inner join vd_mstr on vd_addr = ap_vend where strftime('%Y', ap_effdate) = " + "'" + keys[0] + "'" +
                        " and ap_vend >= " + "'" + keys[1] + "'" +
                        " and ap_vend <= "+ "'" + keys[2] + "'" +
                        " and ap_type = 'c' " +   
                        " and ap_status <> 'void' " +        
                        " and vd_1099 = '1' " +         
                        " and vd_site = " + "'" + keys[3] + "'" +        
                        " group by vd_addr, strftime('%Y', ap_effdate) ;");   
                } else {
                res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_curr, sum(ap_amt) as total, year(ap_effdate)  " +
                        " from ap_mstr inner join vd_mstr on vd_addr = ap_vend where year(ap_effdate) = " + "'" + keys[0] + "'" +
                        " and ap_vend >= " + "'" + keys[1] + "'" +
                        " and ap_vend <= "+ "'" + keys[2] + "'" +
                        " and ap_type = 'c' " + 
                        " and ap_status <> 'void' " +        
                        " and vd_1099 = '1' " +        
                        " and vd_site = " + "'" + keys[3] + "'" +        
                        " group by vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_curr, year(ap_effdate) ;");
                }
                
                
                    while (res.next()) {
                   
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vd_addr"));
                        rowArray.put(res.getString("vd_name"));
                        rowArray.put(res.getString("vd_line1"));
                        rowArray.put(res.getString("vd_city"));
                        rowArray.put(res.getString("vd_state"));
                        rowArray.put(res.getString("vd_zip"));
                        rowArray.put(res.getString("vd_curr"));
                        rowArray.put(currformatDouble(bsParseDouble(res.getString("total"))));
                        jsonarray.put(rowArray);
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
        return jsonarray.toString(); 
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
             bslog(s);
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateAPCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServFIN"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getAPCtrl"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServFIN");
                r = objectMapper.readValue(returnstring, ap_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new ap_ctrl(m);
                return r;
            }
        }
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
        String ap_entrytype, String ap_approved, String ap_approver, double ap_amt_tax, double ap_amt_sac) {
        public ap_mstr(String[]m) {
            this(m, "", "", "", 0, 0, "", "", "", "", "", 
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", 0, 0);
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
    
    public record VoucherAP(String[] m, ap_mstr ap, ArrayList<vod_mstr> vod) {
        public VoucherAP(String[] m) {
            this (m, null, null);
        }
    }
    
    public record ap_ctrl (String[] m, String apc_bank, String apc_assetacct, 
        String apc_autovoucher, String apc_apacct, String apc_varchar) {
        public ap_ctrl(String[] m) {
            this(m,"", "", "", "", "");
        }
    } 
    
    public record exp_mstr(String[] m, String exp_id, String exp_site, String exp_entity, String exp_name,
        String exp_acct, String exp_cc, String exp_createdate, String exp_changedate, String exp_userid,
        String exp_desc, String exp_ref, double exp_amt, String exp_active) {
        public exp_mstr(String[]m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", 0, "" );
        }
    }

    
}
