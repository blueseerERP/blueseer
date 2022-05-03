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
package com.blueseer.lbl;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author terryva
 */
public class lblData {
    
    
    public static String[] addLabelMstr(label_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from label_mstr where lbl_id = ?";
        String sqlInsert = "insert into label_mstr (lbl_id, lbl_item, lbl_custitem, lbl_id_str, lbl_conttype, lbl_qty, lbl_po, "
                        + "lbl_order, lbl_line, lbl_ref, lbl_lot, lbl_parent, lbl_parent_str, "
                        + "lbl_addrcode, lbl_addrname, lbl_addr1, lbl_addr2, lbl_addrcity, lbl_addrstate, lbl_addrzip, lbl_addrcountry, "
                        + "lbl_crt_date, lbl_ship_date, lbl_userid, lbl_printer, lbl_prog, lbl_site, lbl_loc, lbl_trantype)  " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.lbl_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.lbl_id);
            psi.setString(2, x.lbl_item);
            psi.setString(3, x.lbl_custitem);
            psi.setString(4, x.lbl_id_str);
            psi.setString(5, x.lbl_conttype);
            psi.setString(6, x.lbl_qty);
            psi.setString(7, x.lbl_po);
            psi.setString(8, x.lbl_order);
            psi.setString(9, x.lbl_line);
            psi.setString(10, x.lbl_ref);
            psi.setString(11, x.lbl_lot);
            psi.setString(12, x.lbl_parent);
            psi.setString(13, x.lbl_parent_str);
            psi.setString(14, x.lbl_addrcode);
            psi.setString(15, x.lbl_addrname);
            psi.setString(16, x.lbl_addr1);
            psi.setString(17, x.lbl_addr2);
            psi.setString(18, x.lbl_addrcity);
            psi.setString(19, x.lbl_addrstate);
            psi.setString(20, x.lbl_addrzip);
            psi.setString(21, x.lbl_addrcountry);
            psi.setString(22, x.lbl_crt_date);
            psi.setString(23, x.lbl_ship_date);
            psi.setString(24, x.lbl_userid);
            psi.setString(25, x.lbl_printer);
            psi.setString(26, x.lbl_prog);
            psi.setString(27, x.lbl_site);
            psi.setString(28, x.lbl_loc);
            psi.setString(29, x.lbl_trantype);
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
       
    public static String[] addLabelZebraMstr(label_zebra x) {
        String[] m = new String[2];
        String sqlSelect = "select * from label_zebra where lblz_code = ?";
        String sqlInsert = "insert into label_zebra (lblz_code, lblz_desc, lblz_type, lblz_file)  " +
                " values (?,?,?,?); "; 
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.lblz_code);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.lblz_code);
            psi.setString(2, x.lblz_desc);
            psi.setString(3, x.lblz_type);
            psi.setString(4, x.lblz_file);
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
    
    public static String[] updateLabelZebraMstr(label_zebra x) {
        String[] m = new String[2];
        String sql = "update label_zebra set lblz_desc = ?, lblz_type = ?, lblz_file = ? " +
                "  where lblz_code = ? ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.lblz_desc);
        ps.setString(2, x.lblz_type);
        ps.setString(3, x.lblz_file);
        ps.setString(4, x.lblz_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteLabelZebraMstr(label_zebra x) { 
       String[] m = new String[2];
        String sql = "delete from label_zebra where lblz_code = ?; ";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.lblz_code);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static label_zebra getLabelZebraMstr(String[] x) {
        label_zebra r = null;
        String[] m = new String[2];
        String sql = "select * from label_zebra where lblz_code = ? ;";
        try (Connection con = DriverManager.getConnection(url + db, user, pass);
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new label_zebra(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new label_zebra(m, res.getString("lblz_code"), 
                            res.getString("lblz_desc"),
                            res.getString("lblz_type"),
                            res.getString("lblz_file")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new label_zebra(m);
        }
        return r;
    }
    
    
    public static String CreateLabelMstr(String serialno, String item, String custpart, String serialnostring, 
              String conttype, String qty, String po, String order, String line, String ref, String lot,
              String parent, String parentstring, String addrcode, String addrname, String addr1, String addr2,
              String addrcity, String addrstate, String addrzip, String addrcountry, String createdate, String effdate, 
              String userid, String printer, String prog, String site, String loc, String trantype) {
          String shiptocode = "";
          try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
              
                 res = st.executeQuery("select lbl_id from label_mstr where lbl_id  = " + "'" + serialno + "'" 
                               + " ;");
                       while (res.next()) {
                           i++;
                       }
                if (i == 0) {
                    st.executeUpdate("insert into label_mstr "
                        + "(lbl_id, lbl_item, lbl_custitem, lbl_id_str, lbl_conttype, lbl_qty, lbl_po, "
                        + "lbl_order, lbl_line, lbl_ref, lbl_lot, lbl_parent, lbl_parent_str, "
                        + "lbl_addrcode, lbl_addrname, lbl_addr1, lbl_addr2, lbl_addrcity, lbl_addrstate, lbl_addrzip, lbl_addrcountry, "
                        + "lbl_crt_date, lbl_ship_date, lbl_userid, lbl_printer, lbl_prog, lbl_site, lbl_loc, lbl_trantype "
                        + " ) "
                        + " values ( " + "'" + serialno + "'" + ","
                        + "'" + item + "'" + ","
                        + "'" + custpart + "'" + ","
                        + "'" + serialnostring + "'" + ","
                        + "'" + conttype + "'" + ","
                        + "'" + qty + "'" + ","
                        + "'" + po + "'" + ","
                        + "'" + order + "'" + ","
                        + "'" + line + "'" + ","
                        + "'" + ref + "'" + ","
                        + "'" + lot + "'" + ","
                        + "'" + parent + "'" + ","
                        + "'" + parentstring + "'" + ","
                        + "'" + addrcode + "'" + ","
                        + "'" + addrname + "'" + ","
                        + "'" + addr1 + "'" + ","
                        + "'" + addr2 + "'" + ","
                        + "'" + addrcity + "'" + ","
                        + "'" + addrstate + "'" + ","
                        + "'" + addrzip + "'" + ","
                        + "'" + addrcountry + "'" + ","
                        + "'" + createdate + "'" + ","
                        + "'" + effdate + "'" + ","
                        + "'" + userid + "'" + ","
                        + "'" + printer + "'" + ","
                        + "'" + prog + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + loc + "'" + ","
                        + "'" + trantype + "'"
                        + ")"
                        + ";");
                } // if i == 0
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return shiptocode;
      }
      
    public static void updateLabelStatus(String serialno, String value) {
          try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            try {
                 st.executeUpdate("update label_mstr set lbl_scan = " + "'" + value + "'" + " where lbl_id = " + "'" + serialno + "'" 
                         + " ;");
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
    
    public static String getLabelInfo(String serialno) {
          String myreturn = "";
          String delim = "+-+";
          try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                 res = st.executeQuery("select * from label_mstr where lbl_id = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                   myreturn = res.getString("lbl_id") + delim + 
                           res.getString("lbl_item") + delim +
                           res.getString("lbl_custitem") + delim +
                           res.getString("lbl_id_str") + delim +
                           res.getString("lbl_conttype") + delim +
                           res.getString("lbl_qty") + delim +
                           res.getString("lbl_po") + delim +
                           res.getString("lbl_order") + delim +
                           res.getString("lbl_line") + delim +
                           res.getString("lbl_ref") + delim +
                           res.getString("lbl_lot") + delim +
                           res.getString("lbl_parent") + delim +
                           res.getString("lbl_parent_str") + delim +
                           res.getString("lbl_addrcode") + delim +
                           res.getString("lbl_addrname") + delim +
                           res.getString("lbl_addr1") + delim +
                           res.getString("lbl_addr2") + delim +
                           res.getString("lbl_addrcity") + delim +
                           res.getString("lbl_addrstate") + delim +
                           res.getString("lbl_addrzip") + delim +
                           res.getString("lbl_addrcountry") + delim +
                           res.getString("lbl_crt_date") + delim +
                           res.getString("lbl_ship_date") + delim +
                           res.getString("lbl_scan") + delim +
                           res.getString("lbl_void") + delim +
                           res.getString("lbl_post") + delim +
                           res.getString("lbl_userid") + delim +
                           res.getString("lbl_printer") + delim +
                           res.getString("lbl_prog") + delim +
                           res.getString("lbl_site") + delim +
                           res.getString("lbl_loc") + delim +
                           res.getString("lbl_trantype") 
                           ;
                           
               }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return myreturn;
      }
    
    public static boolean isLabel(String serialno) {
          
          // From perspective of "does it exist"
          // assume it's false i.e. it doesnt exist.
          boolean myreturn = false;
          int i = 0;
          
          try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                 res = st.executeQuery("select lbl_id from label_mstr where lbl_id = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                 i++; 
               }
               if (i > 0) {
                   myreturn = true;
               }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return myreturn;
      }
      
    public static ArrayList getLabelFileList(String type) {
       ArrayList myarray = new ArrayList();
        try{
            
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                if (type.equals("all")) {
                res = st.executeQuery("select lblz_code from label_zebra ;");
                } else {
                res = st.executeQuery("select lblz_code from label_zebra where lblz_type = " + "'" + type + "'" + ";"); 
                }
                
               while (res.next()) {
                    myarray.add(res.getString("lblz_code"));
                    
                }
               
           }
            catch (SQLException s){
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;
        
    }
    
    public static int getLabelStatus(String serialno) {

      // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
      // assume it's false i.e. hasn't been scanned.
      int myreturn = 0;

      try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
             res = st.executeQuery("select lbl_scan from label_mstr where lbl_id = " + "'" + serialno + "'" 
                     + " ;");
           while (res.next()) {
               myreturn = res.getInt("lbl_scan");
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

    public record label_zebra(String[] m, String lblz_code, String lblz_desc, 
        String lblz_type, String lblz_file) {
        public label_zebra(String[]m) {
            this(m, "", "", "", "");
        }
    }
     
    public record label_mstr(String[] m, String lbl_id, String lbl_item, String lbl_custitem, 
        String lbl_id_str, String lbl_conttype, String lbl_qty, String lbl_po,
        String lbl_order, String lbl_line, String lbl_ref, String lbl_lot, 
        String lbl_parent, String lbl_parent_str, String lbl_addrcode, String lbl_addrname, 
        String lbl_addr1, String lbl_addr2, String lbl_addrcity, String lbl_addrstate, 
        String lbl_addrzip, String lbl_addrcountry, String lbl_crt_date, String lbl_ship_date, 
        String lbl_userid, String lbl_printer, String lbl_prog, String lbl_site, 
        String lbl_loc, String lbl_trantype) {
        public label_mstr(String[]m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "");
        }
    }
    
}
