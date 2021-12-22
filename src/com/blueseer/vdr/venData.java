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
package com.blueseer.vdr;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
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
public class venData {
   
     // add customer master customer master table only
    public static String[] addVendMstr(vd_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _addVendMstr(x, con, ps, res);  
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
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
    
     private static int _addVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from vd_mstr where vd_addr = ?";
        String sqlInsert = "insert into vd_mstr (vd_addr, vd_name, vd_line1, vd_line2, "
                        + "vd_line3, vd_city, vd_state, vd_zip, "
                        + "vd_country, vd_dateadd, vd_datemod, vd_usermod, "
                        + "vd_group, vd_market, vd_buyer, "
                        + "vd_shipvia, vd_terms, vd_misc, vd_price_code, "
                        + "vd_disc_code, vd_tax_code,  "
                        + "vd_ap_acct, vd_ap_cc, vd_bank, vd_curr, vd_remarks, vd_phone, vd_email ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
          ps = con.prepareStatement(sqlSelect);
          ps.setString(1, x.vd_addr);
          res = ps.executeQuery();
          ps = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            ps.setString(1, x.vd_addr);
            ps.setString(2, x.vd_name);
            ps.setString(3, x.vd_line1);
            ps.setString(4, x.vd_line2);
            ps.setString(5, x.vd_line3);
            ps.setString(6, x.vd_city);
            ps.setString(7, x.vd_state);
            ps.setString(8, x.vd_zip);
            ps.setString(9, x.vd_country);
            ps.setString(10, x.vd_dateadd);
            ps.setString(11, x.vd_datemod);
            ps.setString(12, x.vd_usermod);
            ps.setString(13, x.vd_group);
            ps.setString(14, x.vd_market);
            ps.setString(15, x.vd_buyer);
            ps.setString(16, x.vd_shipvia);
            ps.setString(17, x.vd_terms);
            ps.setString(18, x.vd_misc);
            ps.setString(19, x.vd_price_code);
            ps.setString(20,x.vd_disc_code);
            ps.setString(21,x.vd_tax_code);
            ps.setString(22,x.vd_ap_acct);
            ps.setString(23,x.vd_ap_cc);
            ps.setString(24,x.vd_bank);
            ps.setString(25,x.vd_curr);
            ps.setString(26,x.vd_remarks);
            ps.setString(27,x.vd_phone);
            ps.setString(28,x.vd_email);
            rows = ps.executeUpdate();
            } 
            return rows;
    }
     
     public static String[] updateVendMstr(vd_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            int rows = _updateVendMstr(x, con, ps, res); 
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
    
    private static int _updateVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException {
        int rows = 0;
        String sql = "update vd_mstr set " 
                + " vd_name = ?, vd_line1 = ?, vd_line2 = ?, "
                + "vd_line3 = ?, vd_city = ?, vd_state = ?, vd_zip = ?, "
                + "vd_country = ?, vd_dateadd = ?, vd_datemod = ?, vd_usermod = ?, "
                + "vd_group = ?, vd_market = ?, vd_buyer = ?,  "
                + "vd_shipvia = ?, vd_terms = ?, vd_freight_type = ?, vd_price_code = ?, "
                + "vd_disc_code = ?, vd_tax_code = ?, vd_misc = ?, "
                + "vd_ap_acct = ?, vd_ap_cc = ?, vd_bank = ?, vd_curr = ?, " 
                + "vd_remarks = ?, vd_phone = ?, vd_email = ? "
                + " where vd_addr = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(29, x.vd_addr);
            ps.setString(1, x.vd_name);
            ps.setString(2, x.vd_line1);
            ps.setString(3, x.vd_line2);
            ps.setString(4, x.vd_line3);
            ps.setString(5, x.vd_city);
            ps.setString(6, x.vd_state);
            ps.setString(7, x.vd_zip);
            ps.setString(8, x.vd_country);
            ps.setString(9, x.vd_dateadd);
            ps.setString(10, x.vd_datemod);
            ps.setString(11, x.vd_usermod);
            ps.setString(12, x.vd_group);
            ps.setString(13, x.vd_market);
            ps.setString(14, x.vd_buyer);
            ps.setString(15, x.vd_shipvia);
            ps.setString(16, x.vd_terms);
            ps.setString(17, x.vd_freight_type);
            ps.setString(18, x.vd_price_code);
            ps.setString(19,x.vd_disc_code);
            ps.setString(20,x.vd_tax_code);
            ps.setString(21,x.vd_misc);
            ps.setString(22,x.vd_ap_acct);
            ps.setString(23,x.vd_ap_cc);
            ps.setString(24,x.vd_bank);
            ps.setString(25,x.vd_curr);
            ps.setString(26,x.vd_remarks);
            ps.setString(27,x.vd_phone);
            ps.setString(28,x.vd_email);
            rows = ps.executeUpdate();
        return rows;
    }
    
    public static String[] deleteCustMstr(vd_mstr x) {
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            con = DriverManager.getConnection(url + db, user, pass);
            _deleteVendMstr(x, con, ps, res);  
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
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
    
    private static void _deleteVendMstr(vd_mstr x, Connection con, PreparedStatement ps, ResultSet res) throws SQLException { 
       
        String sql = "delete from vd_mstr where vd_addr = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.vd_addr);
        ps.executeUpdate();
    }
        
    public static vd_mstr getVendMstr(String[] x) {
        vd_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from vd_mstr where vd_addr = ? ;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new vd_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new vd_mstr(m, res.getString("vd_addr"), res.getString("vd_site"), res.getString("vd_name"), 
                                res.getString("vd_line1"), res.getString("vd_line2"),
                    res.getString("vd_line3"), res.getString("vd_city"), res.getString("vd_state"), 
                    res.getString("vd_zip"), res.getString("vd_country"), res.getString("vd_dateadd"), 
                    res.getString("vd_datemod"), res.getString("vd_usermod"), res.getString("vd_group"), 
                    res.getString("vd_market"), res.getString("vd_buyer"), res.getString("vd_terms"), 
                    res.getString("vd_shipvia"), res.getString("vd_price_code"), res.getString("vd_disc_code"), 
                    res.getString("vd_tax_code"), res.getString("vd_ap_acct"), res.getString("vd_ap_cc"), 
                    res.getString("vd_remarks"), res.getString("vd_freight_type"), res.getString("vd_bank"), 
                    res.getString("vd_curr"), res.getString("vd_misc"), res.getString("vd_phone"), res.getString("vd_email") 
                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new vd_mstr(m);
        }
        return r;
    }
    
    // misc
    
    public static ArrayList getVendMstrList() {
        ArrayList myarray = new ArrayList();
        try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select vd_addr from vd_mstr order by vd_addr;");
                while (res.next()) {
                    myarray.add(res.getString("vd_addr"));

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
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getVendMstrListBetween(String from, String to) {
        ArrayList myarray = new ArrayList();
        try {
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select vd_addr from vd_mstr "
                        + " where vd_addr >= " + "'" + from + "'"
                        + " and vd_addr <= " + "'" + to + "'"
                        + " order by vd_addr;");
                while (res.next()) {
                    myarray.add(res.getString("vd_addr"));

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

    public static ArrayList getVendTermsList() {
        ArrayList myarray = new ArrayList();
        try {
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select cut_code from cust_term order by cut_code;");
                while (res.next()) {
                    myarray.add(res.getString("cut_code"));

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
        return myarray;

    }
        
    public static ArrayList getVendNameList() {
        ArrayList myarray = new ArrayList();
        try {
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                res = st.executeQuery("select vd_name from vd_mstr order by vd_name;");
                while (res.next()) {
                    myarray.add(res.getString("vd_name").replace("'", ""));

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

    public static String getVendTerms(String vend) {
           String myitem = null;
         try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
                ResultSet res = null;
            try{
                

                res = st.executeQuery("select vd_terms from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("vd_terms");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }
           
    public static String getVendName(String vend) {
           String myitem = null;
         try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
                ResultSet res = null;
            try{
                res = st.executeQuery("select vd_name from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("vd_name");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    }       
         
    public static String getVendAPAcct(String vend) {
   String myitem = null;
 try{

    Connection con = DriverManager.getConnection(url + db, user, pass);
    Statement st = con.createStatement();
        ResultSet res = null;
    try{
        

        res = st.executeQuery("select vd_ap_acct from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
       while (res.next()) {
        myitem = res.getString("vd_ap_acct");                    
        }

   }
    catch (SQLException s){
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
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myitem;

}

    public static String getVendAPCC(String vend) {
   String myitem = null;
 try{

    Connection con = DriverManager.getConnection(url + db, user, pass);
    Statement st = con.createStatement();
        ResultSet res = null;
    try{
        

        res = st.executeQuery("select vd_ap_cc from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
       while (res.next()) {
        myitem = res.getString("vd_ap_cc");                    
        }

   }
    catch (SQLException s){
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
}
catch (Exception e){
    MainFrame.bslog(e);
}
return myitem;

}

    public static String getVendItemFromItem(String vend, String item) {
   String mystring = "";
    try{

        Connection con = DriverManager.getConnection(url + db, user, pass);
        Statement st = con.createStatement();
            ResultSet res = null;

        try{
            
            res = st.executeQuery("select vdp_vitem from vdp_mstr where vdp_vend = " + "'" + vend + "'" + 
                                  " AND vdp_item = " + "'" + item + "'" + ";");
           while (res.next()) {
               mystring = res.getString("vdp_vitem");

            }

       }
        catch (SQLException s){
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
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
    return mystring;

}        

    public static String getVendCurrency(String vend) {
           String myitem = "";
         try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
                ResultSet res = null;
            try{
                

                res = st.executeQuery("select vd_curr from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                myitem = res.getString("vd_curr");                    
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myitem;
        
    } 

    public static String[] getVendInfo(String vend) {
           // get vendor specific data
            // addr, acct, cc, currency, bank, terms, site
            String[] vendinfo = new String[]{"","","","","","","", ""};
         try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
                ResultSet res = null;
            try{
                res = st.executeQuery("select * from vd_mstr where vd_addr = " + "'" + vend + "'" + ";" );
               while (res.next()) {
                   vendinfo[0] = res.getString("vd_addr");
                   vendinfo[1] = res.getString("vd_ap_acct");
                   vendinfo[2] = res.getString("vd_ap_cc");
                   vendinfo[3] = res.getString("vd_curr");
                   vendinfo[4] = res.getString("vd_bank");
                   vendinfo[5] = res.getString("vd_terms");
                   vendinfo[6] = res.getString("vd_site");         
                }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return vendinfo;
        
    } 

    
    public record vd_mstr(String[] m, String vd_addr, String vd_site, String vd_name, 
        String vd_line1, String vd_line2, String vd_line3, 
        String vd_city, String vd_state, String vd_zip,
    String vd_country, String vd_dateadd, String vd_datemod, String vd_usermod, 
    String vd_group, String vd_market, String vd_buyer,  
    String vd_terms, String vd_shipvia, String vd_price_code,
    String vd_disc_code, String vd_tax_code, String vd_ap_acct,
    String vd_ap_cc, String vd_remarks, String vd_freight_type, String vd_bank, String vd_curr, 
    String vd_misc, String vd_phone, String vd_email) {
        public vd_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", ""
                    );
        }
    }
    
}
