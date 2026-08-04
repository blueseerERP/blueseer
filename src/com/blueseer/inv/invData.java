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
package com.blueseer.inv;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import com.blueseer.fgl.fglData.gl_pair;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble5;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.bsformat;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.formatUSC;
import static com.blueseer.utl.BlueSeerUtils.formatUSZ;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListDouble;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListString;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToBoolean;
import static com.blueseer.utl.BlueSeerUtils.jsonToDouble;
import static com.blueseer.utl.BlueSeerUtils.jsonToHashMapStringString;
import static com.blueseer.utl.BlueSeerUtils.jsonToHashMapStringStringArr;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import static com.blueseer.utl.BlueSeerUtils.xNull;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getPackQtyForItem;
import com.blueseer.utl.TreeConverter;
import static com.blueseer.utl.TreeConverter.fromPOJO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.json.JSONArray;
import org.threeten.bp.LocalDate;

/**
 *
 * @author terryva
 */
public class invData {
    
    public static String[] addItemMasterMass(ArrayList<String> itemlist, String delim) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addItemMasterMass"});
            list.add(new String[]{"param1",delim});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(itemlist);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[]{"0",""};
        String[] ld = new String[29];
        Connection con = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            
            for (String rec : itemlist) {
                ld = rec.split(delim, -1);
                item_mstr x = new item_mstr(null, 
                ld[0], // item
                ld[1].toUpperCase(), //desc
                bsParseInt(ld[8]), // lotsize
                bsParseDouble(ld[2]), // selling price
                bsParseDouble(ld[9]), // purch price
                bsParseDouble(ld[11]), // ovh cost
                bsParseDouble(ld[12]), // out cost
                bsParseDouble(ld[10]), // matl cost
                (ld[4].isBlank()) ? "A" : ld[4],
                (ld[13].isBlank()) ? "ASSET" : ld[13], // type
                ld[14], // group
                (ld[5].isBlank()) ? "9999" : ld[5], // prodline
                ld[15], // drawing
                ld[16], // rev
                ld[17], // custrev
                ld[7], // wh
                ld[6],      // loc  
                (ld[3].isBlank()) ? OVData.getDefaultSite() : ld[3], //site
                ld[18], // comments
                "ACTIVE", // status
                (ld[19].isBlank()) ? "EA" : ld[19], // uom
                bsParseDouble(ld[20]), //net wt
                bsParseDouble(ld[21]), //ship wt
                "", //default cont
                0, // default cont qty
                bsParseInt(ld[22]), // lead
                bsParseDouble(ld[23]), //safety stock
                bsParseDouble(ld[24]), // minordqty
                (ld[25].isBlank()) ? "0" : ld[25], //mrp
                (ld[26].isBlank()) ? "0" : ld[26], //sched
                (ld[27].isBlank()) ? "0" : ld[27], //plan
                ld[28], // routing
                "", // tax
                bsmf.MainFrame.dfdate.format(new Date()),
                null, // expire date
                0, // expire days
                "0", // phantom boolean
                "", // label
                "" // servicetype
                );
                item_cost y = new item_cost(null, 
                    ld[0], 
                    ld[3], 
                    "standard",
                    String.valueOf((bsParseDouble(ld[10]) + bsParseDouble(ld[11]) + bsParseDouble(ld[12]))),
                    ld[10], 
                    "0", 
                    "0", 
                    ld[11], 
                    ld[12],
                    "0", "0", "0", "0", "0");
                invData._addItemMstr(x, con, true);
                invData._addItemCostRec(y, con, true);
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
            m = new String[]{"1", s.getMessage()};
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
        
    public static String[] addItemMstr(item_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addItemMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            int rows = _addItemMstr(x, con, false);  
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
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
    
    private static int _addItemMstr(item_mstr x, Connection con, boolean addupdate) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from item_mstr where it_item = ?";
        String sqlInsert = "insert into item_mstr (it_item, it_desc, it_lotsize, " 
                        + "it_sell_price, it_pur_price, it_ovh_cost, it_out_cost, it_mtl_cost, it_code, it_type, it_group, "
                        + "it_prodline, it_drawing, it_rev, it_custrev, it_wh, it_loc, it_site, it_comments, "
                        + "it_status, it_uom, it_net_wt, it_ship_wt, it_cont, it_contqty, "
                        + "it_leadtime, it_safestock, it_minordqty, it_mrp, it_sched, it_plan, it_wf, it_taxcode, it_createdate, it_expire, it_expiredays, "
                        + "it_phantom, it_label, it_servicetype ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update item_mstr set it_desc = ?, it_lotsize = ?, " +
                "it_sell_price = ?, it_pur_price = ?, it_ovh_cost = ?, it_out_cost = ?, it_mtl_cost = ?, it_code = ?, it_type = ?, it_group = ?, " +
                "it_prodline = ?, it_drawing = ?, it_rev = ?, it_custrev = ?, it_wh = ?, it_loc = ?, it_site = ?, it_comments = ?, " +
                "it_status = ?, it_uom = ?, it_net_wt = ?, it_ship_wt = ?, it_cont = ?, it_contqty = ?, " +
                "it_leadtime = ?, it_safestock = ?, it_minordqty = ?, it_mrp = ?, it_sched = ?, it_plan = ?, it_wf = ?, it_taxcode = ?, it_createdate = ?, " +
                "it_expire = ?, it_expiredays = ?, it_phantom = ?, it_label = ?, it_servicetype = ? " +
                " where it_item = ? ; ";
            PreparedStatement ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.it_item);
            ResultSet res = ps.executeQuery();
            PreparedStatement psi = con.prepareStatement(sqlInsert);
            PreparedStatement psu = con.prepareStatement(sqlUpdate);
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.it_item);
            psi.setString(2, x.it_desc);
            psi.setInt(3, x.it_lotsize);
            psi.setDouble(4, x.it_sell_price);
            psi.setDouble(5, x.it_pur_price);
            psi.setDouble(6, x.it_ovh_cost);
            psi.setDouble(7, x.it_out_cost);
            psi.setDouble(8, x.it_mtl_cost);
            psi.setString(9, x.it_code);
            psi.setString(10, x.it_type);
            psi.setString(11, x.it_group);
            psi.setString(12, x.it_prodline);
            psi.setString(13, x.it_drawing);
            psi.setString(14, x.it_rev);
            psi.setString(15, x.it_custrev);
            psi.setString(16, x.it_wh);
            psi.setString(17, x.it_loc);
            psi.setString(18, x.it_site);
            psi.setString(19, x.it_comments);
            psi.setString(20, x.it_status);
            psi.setString(21, x.it_uom);
            psi.setDouble(22, x.it_net_wt);
            psi.setDouble(23, x.it_ship_wt);
            psi.setString(24, x.it_cont);
            psi.setInt(25, x.it_contqty);
            psi.setInt(26, x.it_leadtime);
            psi.setDouble(27, x.it_safestock);
            psi.setDouble(28, x.it_minordqty);
            psi.setString(29, x.it_mrp);
            psi.setString(30, x.it_sched);
            psi.setString(31, x.it_plan);
            psi.setString(32, x.it_wf);
            psi.setString(33, x.it_taxcode);
            psi.setString(34, x.it_createdate);
            psi.setString(35, x.it_expire);
            psi.setInt(36, x.it_expiredays);
            psi.setString(37, x.it_phantom);
            psi.setString(38, x.it_label);
            psi.setString(39, x.it_servicetype);
            rows = psi.executeUpdate();
            } else {
                if (addupdate) {
                  psu.setString(39, x.it_item);
            psu.setString(1, x.it_desc);
            psu.setInt(2, x.it_lotsize);
            psu.setDouble(3, x.it_sell_price);
            psu.setDouble(4, x.it_pur_price);
            psu.setDouble(5, x.it_ovh_cost);
            psu.setDouble(6, x.it_out_cost);
            psu.setDouble(7, x.it_mtl_cost);
            psu.setString(8, x.it_code);
            psu.setString(9, x.it_type);
            psu.setString(10, x.it_group);
            psu.setString(11, x.it_prodline);
            psu.setString(12, x.it_drawing);
            psu.setString(13, x.it_rev);
            psu.setString(14, x.it_custrev);
            psu.setString(15, x.it_wh);
            psu.setString(16, x.it_loc);
            psu.setString(17, x.it_site);
            psu.setString(18, x.it_comments);
            psu.setString(19, x.it_status);
            psu.setString(20, x.it_uom);
            psu.setDouble(21, x.it_net_wt);
            psu.setDouble(22, x.it_ship_wt);
            psu.setString(23, x.it_cont);
            psu.setInt(24, x.it_contqty);
            psu.setInt(25, x.it_leadtime);
            psu.setDouble(26, x.it_safestock);
            psu.setDouble(27, x.it_minordqty);
            psu.setString(28, x.it_mrp);
            psu.setString(29, x.it_sched);
            psu.setString(30, x.it_plan);
            psu.setString(31, x.it_wf);
            psu.setString(32, x.it_taxcode);
            psu.setString(33, x.it_createdate);
            psu.setString(34, x.it_expire);
            psu.setInt(35, x.it_expiredays);
            psu.setString(36, x.it_phantom);
            psu.setString(37, x.it_label);
            psu.setString(38, x.it_servicetype);
            rows = psu.executeUpdate();
                }
            }
            psu.close();
            ps.close();
            psi.close();
            res.close();
        return rows;
    }
    
    private static double _addItemCostRec(item_cost x, Connection con, boolean addupdate) throws SQLException {
        
         int rows = 0;
        String sqlSelect = "SELECT itc_item FROM item_cost where itc_item = ? "
                        + " AND itc_site = ? "
                        + " AND itc_set = ? ;";
        String sqlInsert = "insert into item_cost (itc_item, itc_site, itc_set, itc_total,  "
                + "itc_mtl_top, itc_lbr_top, itc_bdn_top, itc_ovh_top, itc_out_top, "
                + "itc_mtl_low, itc_lbr_low, itc_bdn_low, itc_ovh_low, itc_out_low  ) "
                 + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        String sqlUpdate = "update item_cost set itc_total = ?, "
                + "itc_mtl_top = ?, itc_lbr_top = ?, itc_bdn_top = ?, itc_ovh_top = ?, itc_out_top = ?, "
                + "itc_mtl_low = ?, itc_lbr_low = ?, itc_bdn_low = ?, itc_ovh_low = ?, itc_out_low  = ? "
                + " where itc_item = ? and itc_site = ? and itc_set = ? ; ";
            PreparedStatement ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.itc_item);
            ps.setString(2, x.itc_site);
            ps.setString(3, x.itc_set);
            ResultSet res = ps.executeQuery();
            PreparedStatement psi = con.prepareStatement(sqlInsert);
            PreparedStatement psu = con.prepareStatement(sqlUpdate);
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.itc_item);
            psi.setString(2, x.itc_site);
            psi.setString(3, x.itc_set);
            psi.setString(4, x.itc_total);
            psi.setString(5, x.itc_mtl_top);
            psi.setString(6, x.itc_lbr_top);
            psi.setString(7, x.itc_bdn_top);
            psi.setString(8, x.itc_ovh_top);
            psi.setString(9, x.itc_out_top);
            psi.setString(10, x.itc_mtl_low);
            psi.setString(11, x.itc_lbr_low);
            psi.setString(12, x.itc_bdn_low);
            psi.setString(13, x.itc_ovh_low);
            psi.setString(14, x.itc_out_low);
           
            rows = psi.executeUpdate();
            } else {
                if (addupdate) {
            psu.setString(12, x.itc_item);
            psu.setString(13, x.itc_site);
            psu.setString(14, x.itc_set);
            psu.setString(1, x.itc_total);
            psu.setString(2, x.itc_mtl_top);
            psu.setString(3, x.itc_lbr_top);
            psu.setString(4, x.itc_bdn_top);
            psu.setString(5, x.itc_ovh_top);
            psu.setString(6, x.itc_out_top);
            psu.setString(7, x.itc_mtl_low);
            psu.setString(8, x.itc_lbr_low);
            psu.setString(9, x.itc_bdn_low);
            psu.setString(10, x.itc_ovh_low);
            psu.setString(11, x.itc_out_low);
            
            rows = psu.executeUpdate();
                }
            }
            psu.close();
            ps.close();
            psi.close();
            res.close();
        return rows;
    }
    
    
    public static String[] updateItemMstr(item_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateItemMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            int rows = _updateItemMstr(x, con);  
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
    
    private static int _updateItemMstr(item_mstr x, Connection con) throws SQLException {
        int rows = 0;
        String sql = "update item_mstr set it_desc = ?, it_lotsize = ?, " +
                "it_sell_price = ?, it_pur_price = ?, it_ovh_cost = ?, it_out_cost = ?, it_mtl_cost = ?, it_code = ?, it_type = ?, it_group = ?, " +
                "it_prodline = ?, it_drawing = ?, it_rev = ?, it_custrev = ?, it_wh = ?, it_loc = ?, it_site = ?, it_comments = ?, " +
                "it_status = ?, it_uom = ?, it_net_wt = ?, it_ship_wt = ?, it_cont = ?, it_contqty = ?, " +
                "it_leadtime = ?, it_safestock = ?, it_minordqty = ?, it_mrp = ?, it_sched = ?, it_plan = ?, it_wf = ?, it_taxcode = ?, it_createdate = ?, " +
                "it_expire = ?, it_expiredays = ?, it_phantom = ?, it_label = ?, it_servicetype = ? " +
                " where it_item = ? ; ";
        PreparedStatement psu = con.prepareStatement(sql);
            psu.setString(39, x.it_item);
            psu.setString(1, x.it_desc);
            psu.setInt(2, x.it_lotsize);
            psu.setDouble(3, x.it_sell_price);
            psu.setDouble(4, x.it_pur_price);
            psu.setDouble(5, x.it_ovh_cost);
            psu.setDouble(6, x.it_out_cost);
            psu.setDouble(7, x.it_mtl_cost);
            psu.setString(8, x.it_code);
            psu.setString(9, x.it_type);
            psu.setString(10, x.it_group);
            psu.setString(11, x.it_prodline);
            psu.setString(12, x.it_drawing);
            psu.setString(13, x.it_rev);
            psu.setString(14, x.it_custrev);
            psu.setString(15, x.it_wh);
            psu.setString(16, x.it_loc);
            psu.setString(17, x.it_site);
            psu.setString(18, x.it_comments);
            psu.setString(19, x.it_status);
            psu.setString(20, x.it_uom);
            psu.setDouble(21, x.it_net_wt);
            psu.setDouble(22, x.it_ship_wt);
            psu.setString(23, x.it_cont);
            psu.setInt(24, x.it_contqty);
            psu.setInt(25, x.it_leadtime);
            psu.setDouble(26, x.it_safestock);
            psu.setDouble(27, x.it_minordqty);
            psu.setString(28, x.it_mrp);
            psu.setString(29, x.it_sched);
            psu.setString(30, x.it_plan);
            psu.setString(31, x.it_wf);
            psu.setString(32, x.it_taxcode);
            psu.setString(33, x.it_createdate);
            psu.setString(34, x.it_expire);
            psu.setInt(35, x.it_expiredays);
            psu.setString(36, x.it_phantom);
            psu.setString(37, x.it_label);
            psu.setString(38, x.it_servicetype);
            rows = psu.executeUpdate();
            psu.close();
        return rows;
    }
    
    public static String[] deleteItemMstr(item_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteItemMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            _deleteItemMstr(x, con);  
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
    
    private static void _deleteItemMstr(item_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null;   
        String sql = "delete from item_mstr where it_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from item_cost where itc_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from item_image where iti_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from in_mstr where in_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from pbm_mstr where ps_parent = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from pbm_mstr where ps_child = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from plan_mstr where plan_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from pland_mstr where pland_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        sql = "delete from mrp_mstr where mrp_item = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.it_item);
        ps.executeUpdate();
        ps.close();
    }
        
    public static item_mstr getItemMstr(String[] x) {
        item_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getItemMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, item_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new item_mstr(m);
                return r;
            }
        }
        String sql = "select * from item_mstr where it_item = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new item_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new item_mstr(m, res.getString("it_item"), res.getString("it_desc"), res.getInt("it_lotsize"),
                    res.getDouble("it_sell_price"), res.getDouble("it_pur_price"), res.getDouble("it_ovh_cost"), res.getDouble("it_out_cost"),
                    res.getDouble("it_mtl_cost"), res.getString("it_code"), res.getString("it_type"), res.getString("it_group"),
                    res.getString("it_prodline"), res.getString("it_drawing"), res.getString("it_rev"), res.getString("it_custrev"), res.getString("it_wh"),
                    res.getString("it_loc"), res.getString("it_site"), res.getString("it_comments"), res.getString("it_status"), res.getString("it_uom"),
                    res.getDouble("it_net_wt"), res.getDouble("it_ship_wt"), res.getString("it_cont"), res.getInt("it_contqty"), 
                    res.getInt("it_leadtime"), res.getDouble("it_safestock"), res.getDouble("it_minordqty"), res.getString("it_mrp"), 
                    res.getString("it_sched"), res.getString("it_plan"), res.getString("it_wf"), res.getString("it_taxcode"), 
                    res.getString("it_createdate"), res.getString("it_expire"), res.getInt("it_expiredays"), 
                    res.getString("it_phantom"), res.getString("it_label"), res.getString("it_servicetype")
        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new item_mstr(m);
        }
        return r;
    }
    
    public static item_mstr getItemMstr(String x) {
        item_mstr r = null;
        String[] m = new String[2];
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemMstr"});
            list.add(new String[]{"param1",  x});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, item_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from item_mstr where it_item = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new item_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new item_mstr(m, res.getString("it_item"), res.getString("it_desc"), res.getInt("it_lotsize"),
                    res.getDouble("it_sell_price"), res.getDouble("it_pur_price"), res.getDouble("it_ovh_cost"), res.getDouble("it_out_cost"),
                    res.getDouble("it_mtl_cost"), res.getString("it_code"), res.getString("it_type"), res.getString("it_group"),
                    res.getString("it_prodline"), res.getString("it_drawing"), res.getString("it_rev"), res.getString("it_custrev"), res.getString("it_wh"),
                    res.getString("it_loc"), res.getString("it_site"), res.getString("it_comments"), res.getString("it_status"), res.getString("it_uom"),
                    res.getDouble("it_net_wt"), res.getDouble("it_ship_wt"), res.getString("it_cont"), res.getInt("it_contqty"), 
                    res.getInt("it_leadtime"), res.getDouble("it_safestock"), res.getDouble("it_minordqty"), res.getString("it_mrp"), 
                    res.getString("it_sched"), res.getString("it_plan"), res.getString("it_wf"), res.getString("it_taxcode"), 
                    res.getString("it_createdate"), res.getString("it_expire"), res.getInt("it_expiredays"), 
                    res.getString("it_phantom"), res.getString("it_label"), res.getString("it_servicetype")
        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new item_mstr(m);
        }
        return r;
    }
    
    
    public static String[] addPBM(pbm_mstr x, bom_mstr y, boolean addBomID) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","addPBM"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(y);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(addBomID);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
        }
        Connection con = null;
        int rows = 0;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
           // if (addBomID) {
           //  rows = _addBomMstr(y, con);    done with save button in BOMM
          //  }
            
           // if (rows > 0)
             rows = _addPBM(x, con);
            
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
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
    
    private static int _addPBM(pbm_mstr x, Connection con) throws SQLException {
        int rows = 0;
        String sqlSelect = "select * from pbm_mstr where ps_parent = ? and ps_child = ? and ps_op = ? and ps_bom = ?";
        String sqlInsert = "insert into pbm_mstr (ps_parent, ps_child, ps_qty_per, " 
                        + "ps_op, ps_ref, ps_type, ps_bom, ps_serialized, ps_routing ) "
                        + " values (?,?,?,?,?,?,?,?,?); "; 
       
            PreparedStatement ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.ps_parent);
            ps.setString(2, x.ps_child);
            ps.setString(3, x.ps_op);
            ps.setString(4, x.ps_bom);
            ResultSet res = ps.executeQuery();
            PreparedStatement psi = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.ps_parent);
            psi.setString(2, x.ps_child);
            psi.setString(3, x.ps_qty_per);
            psi.setString(4, x.ps_op);
            psi.setString(5, x.ps_ref);
            psi.setString(6, x.ps_type);
            psi.setString(7, x.ps_bom);
            psi.setString(8, x.ps_serialized);
            psi.setString(9, x.ps_routing);
            rows = psi.executeUpdate();
            } 
            ps.close();
            psi.close();
            res.close();
        return rows;
    }
    
    private static int _addBomMstr(bom_mstr x, Connection con) throws SQLException {
        int rows = 0;
        
        
        
        String sqlSelect = "select * from bom_mstr where bom_id = ? and bom_item = ?;";
        String sqlInsert = "insert into bom_mstr (bom_id, bom_desc, bom_item, " 
                        + "bom_enabled, bom_primary, bom_routing ) "
                        + " values (?,?,?,?,?,?); "; 
       
            PreparedStatement ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.bom_id);
            ps.setString(2, x.bom_item);
            ResultSet res = ps.executeQuery();
            PreparedStatement psi = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
                // if bom_primary is true...then must reset all other bom_mstr records for this item as false
                String sqlReset = "update bom_mstr set bom_primary = '0' where bom_item = ?;";
                if (x.bom_primary.equals("1")) {
                    PreparedStatement psr = con.prepareStatement(sqlReset); 
                    psr.setString(1, x.bom_item);
                    rows = psr.executeUpdate();
                    psr.close();
                }
            psi.setString(1, x.bom_id);
            psi.setString(2, x.bom_desc);
            psi.setString(3, x.bom_item);
            psi.setString(4, x.bom_enabled);
            psi.setString(5, x.bom_primary);
            psi.setString(6, x.bom_routing);
            rows = psi.executeUpdate();
            } 
            ps.close();
            psi.close();
            res.close();
        return rows;
    }
        
    public static String[] updatePBM(pbm_mstr x, bom_mstr y) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","updatePBM"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(y);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        int rows = 0;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            /*  now done by save button in BOMM
            _updateBomMstr(y, con); // the bom_mstr gets updated every time a component is updated
                                    // where a change to bom_mstr has occurred or not
            */
            rows = _updatePBM(x, con); 
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
    
    private static int _updatePBM(pbm_mstr x, Connection con) throws SQLException {
        int rows = 0;
        String sqlUpdate = "update pbm_mstr set ps_qty_per = ?, " 
                        + "ps_ref = ?, ps_type = ?, ps_serialized = ?, ps_routing = ? "
                        + " where ps_parent = ? and ps_child = ? and ps_op = ? and ps_bom = ? ";
            PreparedStatement psu = con.prepareStatement(sqlUpdate); 
            psu.setString(1, x.ps_qty_per);
            psu.setString(2, x.ps_ref);
            psu.setString(3, x.ps_type);
            psu.setString(4, x.ps_serialized);
            psu.setString(5, x.ps_routing);
            psu.setString(6, x.ps_parent);
            psu.setString(7, x.ps_child);
            psu.setString(8, x.ps_op);
            psu.setString(9, x.ps_bom);
            
            rows = psu.executeUpdate();
            psu.close();
        return rows;
    }
    
    public static String[] addupdateBOMMstr(bom_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addupdateBOMMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
        }
        Connection con = null;
        int rows = 0;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            
            rows = _addupdateBomMstr(x, con); // the bom_mstr gets updated every time a component is updated
                                    // where a change to bom_mstr has occurred or not
            if (rows > 0) {
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};    
            }
        } catch (SQLException s) {
             MainFrame.bslog(s);
             m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordError};
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
    
    private static int _addupdateBomMstr(bom_mstr x, Connection con) throws SQLException {
        int rows = 0;
        // if bom_primary is true...then must reset all other bom_mstr records for this item as false
        String sqlReset = "update bom_mstr set bom_primary = '0' where bom_item = ? and bom_id <> ?;";
        String sqlSelect = "select * from bom_mstr where bom_id = ? and bom_item = ?;";
        String sqlInsert = "insert into bom_mstr (bom_id, bom_desc, bom_item, " 
                        + "bom_enabled, bom_primary, bom_routing ) "
                        + " values (?,?,?,?,?,?); "; 
        String sqlUpdate = "update bom_mstr set bom_desc = ?, " 
                        + "bom_enabled = ?, bom_primary = ? "
                        + " where bom_id = ? and bom_item = ? ";
        
         PreparedStatement ps = con.prepareStatement(sqlSelect);
         PreparedStatement psi = con.prepareStatement(sqlInsert);  
            ps.setString(1, x.bom_id);
            ps.setString(2, x.bom_item);
            ResultSet res = ps.executeQuery();
            if (! res.isBeforeFirst()) {
                psi.setString(1, x.bom_id);
                psi.setString(2, x.bom_desc);
                psi.setString(3, x.bom_item);
                psi.setString(4, x.bom_enabled);
                psi.setString(5, x.bom_primary);
                psi.setString(6, x.bom_routing);
                rows = psi.executeUpdate(); 
                psi.close();
            } else {
               PreparedStatement psu = con.prepareStatement(sqlUpdate); 
                psu.setString(1, x.bom_desc);
                psu.setString(2, x.bom_enabled);
                psu.setString(3, x.bom_primary);
                psu.setString(4, x.bom_id);
                psu.setString(5, x.bom_item);
                rows = psu.executeUpdate();
                psu.close(); 
            }
            
            if (x.bom_primary.equals("1")) { // inserting record as primary....remove all other primaries for this item before inserting record
                PreparedStatement psr = con.prepareStatement(sqlReset); 
                psr.setString(1, x.bom_item);
                 psr.setString(2, x.bom_id);
                rows = psr.executeUpdate();
                psr.close();
            }
        
        
        return rows;
    }
    
    public static int _getBomPbmCount(String bomid, Connection con) throws SQLException {
       int x = 0;
       Statement st = null;
       ResultSet res = null;
            try {
                st = con.createStatement();
                res = st.executeQuery("select * from pbm_mstr "
                        + " inner join bom_mstr on bom_id = ps_bom "
                        + " where ps_bom = " + "'" + bomid + "';");
                while (res.next()) {
                    x++;
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
            }
        
        return x;

    }

    
    public static String[] deletePBM(pbm_mstr x, bom_mstr y) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","deletePBM"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(y);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        if (x == null) {
            return new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};
        }
        Connection con = null;
        try { 
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            
            _deletePBM(x, con); 
            
            if (_getBomPbmCount(x.ps_bom(), con) == 0) { // if 0 pbm_mstr remaining for this bom id...delete bom id
                _deleteBomMstr(y, con);                
            }
            
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
    
    private static void _deletePBM(pbm_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null;   
        String sql = "delete from pbm_mstr where ps_parent = ? and ps_child = ? and ps_op = ? and ps_bom = ? ; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.ps_parent);
        ps.setString(2, x.ps_child);
        ps.setString(3, x.ps_op);
        ps.setString(4, x.ps_bom);
        ps.executeUpdate();
        
        ps.close();
    }
    
    private static void _deleteBomMstr(bom_mstr x, Connection con) throws SQLException { 
        PreparedStatement ps = null;   
        String sql = "delete from bom_mstr where bom_id = ? and bom_item = ?;";
        ps = con.prepareStatement(sql);
        ps.setString(1, x.bom_id);
        ps.setString(2, x.bom_item);
        ps.executeUpdate();
        ps.close();
    }
    
    public static bom_mstr getBOMMstr(String[] x) {
        bom_mstr r = null;
        String[] m = new String[2];
        String param2 = "";
        if (x.length > 1) {
            param2 = x[1];
        }
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getBOMMstr"});
            list.add(new String[]{"param1",x[0]});
            list.add(new String[]{"param2",param2});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, bom_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new bom_mstr(m);
                return r;
            }
        }
        String sql = "select * from bom_mstr where bom_item = ? and bom_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
      	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new bom_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new bom_mstr(m, res.getString("bom_id"),
                                res.getString("bom_desc"),
                                res.getString("bom_item"),
                                res.getString("bom_enabled"),
                                res.getString("bom_primary"),
                                res.getString("bom_routing"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new bom_mstr(m);
        }
        return r;
    }
  
    
    public static String[] addWorkCenterMstr(wc_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addWorkCenterMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  wc_mstr where wc_cell = ?";
        String sqlInsert = "insert into wc_mstr (wc_cell, wc_desc, wc_site, "
                        + " wc_cc, wc_run_rate, wc_setup_rate, " 
                        + " wc_bdn_rate, wc_run_crew, wc_setup, wc_remarks ) " 
                        + " values (?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.wc_cell);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.wc_cell);
            psi.setString(2, x.wc_desc);
            psi.setString(3, x.wc_site);
            psi.setString(4, x.wc_cc);
            psi.setString(5, x.wc_run_rate);
            psi.setString(6, x.wc_setup_rate);
            psi.setString(7, x.wc_bdn_rate);
            psi.setString(8, x.wc_run_crew);
            psi.setString(9, x.wc_setup);
            psi.setString(10, x.wc_remarks);
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

    public static String[] updateWorkCenterMstr(wc_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateWorkCenterMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update wc_mstr set wc_desc = ?, wc_site = ?, "
                        + " wc_cc = ?, wc_run_rate = ?, wc_setup_rate = ?, " 
                        + " wc_bdn_rate = ?, wc_run_crew = ?, wc_setup = ?, wc_remarks = ? " 
                        + " where wc_cell = ? ;"; 
         try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(10, x.wc_cell);
            ps.setString(1, x.wc_desc);
            ps.setString(2, x.wc_site);
            ps.setString(3, x.wc_cc);
            ps.setString(4, x.wc_run_rate);
            ps.setString(5, x.wc_setup_rate);
            ps.setString(6, x.wc_bdn_rate);
            ps.setString(7, x.wc_run_crew);
            ps.setString(8, x.wc_setup);
            ps.setString(9, x.wc_remarks);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static wc_mstr getWorkCenterMstr(String[] x) {
        wc_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getWorkCenterMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, wc_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new wc_mstr(m);
                return r;
            }
        }
        String sql = "select * from wc_mstr where wc_cell = ? ;";
        
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
       
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wc_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wc_mstr(m, res.getString("wc_cell"), res.getString("wc_desc"),
                        res.getString("wc_site"), res.getString("wc_cc"),
                        res.getString("wc_run_rate"), res.getString("wc_setup_rate"),
                        res.getString("wc_bdn_rate"), res.getString("wc_run_crew"),
                        res.getString("wc_setup"), res.getString("wc_remarks"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wc_mstr(m);
        }
        return r;
    }
  
    public static String[] deleteWorkCenterMstr(wc_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteWorkCenterMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m;
        String sqlDelete = "delete from wc_mstr where wc_cell = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
             ps.setString(1, x.wc_cell);
             int rows = ps.executeUpdate();
             if (rows > 0) {
                m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess}; 
             } else {
                m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError}; 
             }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    
    public static String[] addLocationMstr(loc_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addLocationMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  loc_mstr where loc_loc = ?";
        String sqlInsert = "insert into loc_mstr (loc_loc, loc_desc, loc_site, "
                        + " loc_wh, loc_active ) "
                        + " values (?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.loc_loc);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.loc_loc);
            psi.setString(2, x.loc_desc);
            psi.setString(3, x.loc_site);
            psi.setString(4, x.loc_wh);
            psi.setString(5, x.loc_active);
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

    public static String[] updateLocationMstr(loc_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateLocationMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update loc_mstr set loc_desc = ?, loc_site = ?, "
                        + " loc_wh = ?, loc_active = ? " 
                        + " where loc_loc = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(5, x.loc_loc);
            ps.setString(1, x.loc_desc);
            ps.setString(2, x.loc_site);
            ps.setString(3, x.loc_wh);
            ps.setString(4, x.loc_active);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static String[] deleteLocationMstr(loc_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteLocationMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from loc_mstr where loc_loc = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.loc_loc);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static loc_mstr getLocationMstr(String[] x) {
        loc_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getLocationMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, loc_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new loc_mstr(m);
                return r;
            }
        }
        String sql = "select * from loc_mstr where loc_loc = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new loc_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new loc_mstr(m, res.getString("loc_loc"), 
                            res.getString("loc_desc"),
                            res.getString("loc_site"),
                            res.getString("loc_wh"),    
                            res.getString("loc_active")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new loc_mstr(m);
        }
        return r;
    }
    
    public static String[] addRoutingMstr(ArrayList<wf_mstr> wfd) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addRoutingMstrByArray"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(wfd);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
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
            for (wf_mstr z : wfd) {
                _addRoutingMstr(z, bscon);
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

    public static String[] updateRoutingMstr(ArrayList<wf_mstr> wfd) {
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
            int i = 0;
            for (wf_mstr z : wfd) {
                i++;
                if (i == 1) {
                    _deleteRoutingMstr(z.wf_id, bscon, ps);
                }
                _addRoutingMstr(z, bscon);
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

    
    private static int _addRoutingMstr(wf_mstr x, Connection con) throws SQLException {
        int rows = 0;
        String sqlSelect = "SELECT * FROM  wf_mstr where wf_id = ? and wf_op = ?";
        String sqlInsert = "insert into wf_mstr (wf_id, wf_desc, wf_site, "
                        + " wf_op, wf_op_desc, wf_assert, wf_cell, " +
                          " wf_setup_hours, wf_run_hours ) " 
                        + " values (?,?,?,?,?,?,?,?,?); "; 
       
            PreparedStatement ps = con.prepareStatement(sqlSelect);
            ps.setString(1, x.wf_id);
             ps.setString(2, x.wf_op);
            ResultSet res = ps.executeQuery();
            PreparedStatement psi = con.prepareStatement(sqlInsert);  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.wf_id);
            psi.setString(2, x.wf_desc);
            psi.setString(3, x.wf_site);
            psi.setString(4, x.wf_op);
            psi.setString(5, x.wf_assert);
            psi.setString(6, x.wf_op_desc);
            psi.setString(7, x.wf_cell);
            psi.setString(8, x.wf_setup_hours);
            psi.setString(9, x.wf_run_hours); 
            rows = psi.executeUpdate();
            } 
            ps.close();
            psi.close();
            res.close();
        return rows;
    }
    
    private static void _deleteRoutingMstr(String wfid, Connection con, PreparedStatement ps) throws SQLException {
        String sql = "delete from wf_mstr where wf_id = ?; ";
        ps = con.prepareStatement(sql);
        ps.setString(1, wfid);
        ps.executeUpdate();
    }
    
    
    public static String[] addRoutingMstr(wf_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addRoutingMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  wf_mstr where wf_id = ? and wf_op = ?";
        String sqlInsert = "insert into wf_mstr (wf_id, wf_desc, wf_site, "
                        + " wf_op, wf_assert, wf_op_desc, wf_cell, " +
                          " wf_setup_hours, wf_run_hours ) " 
                        + " values (?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.wf_id);
             ps.setString(2, x.wf_op);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.wf_id);
            psi.setString(2, x.wf_desc);
            psi.setString(3, x.wf_site);
            psi.setString(4, x.wf_op);
            psi.setString(5, x.wf_assert);
            psi.setString(6, x.wf_op_desc);
            psi.setString(7, x.wf_cell);
            psi.setString(8, x.wf_setup_hours);
            psi.setString(9, x.wf_run_hours);
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

    public static String[] updateRoutingMstr(wf_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateRoutingMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update wf_mstr set wf_desc = ?, wf_site = ?, "
                        + " wf_assert = ?, wf_op_desc = ?, wf_cell = ?, " +
                          " wf_setup_hours = ?, wf_run_hours = ? " 
                        + " where wf_id = ? and wf_op = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(8, x.wf_id);
            ps.setString(9, x.wf_op);
            ps.setString(1, x.wf_desc);
            ps.setString(2, x.wf_site);
            ps.setString(3, x.wf_assert);
            ps.setString(4, x.wf_op_desc);
            ps.setString(5, x.wf_cell);
            ps.setString(6, x.wf_setup_hours);
            ps.setString(7, x.wf_run_hours);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static String[] deleteRoutingMstr(wf_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteRoutingMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m;
        String sqlDelete = "delete from wf_mstr where wf_id = ?;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
             ps.setString(1, x.wf_id);
             int rows = ps.executeUpdate();
             if (rows > 0) {
                m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess}; 
             } else {
                m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError}; 
             }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static wf_mstr getRoutingMstr(String[] x) {
        wf_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getRoutingMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, wf_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new wf_mstr(m);
                return r;
            }
        }
        String sql = "";
        if (x.length == 1) {
            sql = "select * from wf_mstr where wf_id = ? ;";
        } else {
            sql = "select * from wf_mstr where wf_id = ? and wf_op = ? ;";
        }
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        if (x.length > 1) {
        ps.setString(2, x[1]);
        }
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wf_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wf_mstr(m, res.getString("wf_id"), res.getString("wf_desc"),
                        res.getString("wf_site"), res.getString("wf_op"),
                        res.getString("wf_assert"), res.getString("wf_op_desc"),
                        res.getString("wf_cell"), res.getString("wf_setup_hours"),
                        res.getString("wf_run_hours"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wf_mstr(m);
        }
        return r;
    }
  
    public static ArrayList<wf_mstr> getRoutingMstrList(String[] x) {
        ArrayList<wf_mstr> r = new ArrayList<wf_mstr>();
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<String[]>();
            paramlist.add(new String[]{"id", "getRoutingMstrList"});
            paramlist.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, new TypeReference<ArrayList<wf_mstr>>() {});
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return r;
            }
        }
        String sql = "";
        if (x.length == 1) {
            sql = "select * from wf_mstr where wf_id = ? order by wf_op;";
        } else {
            sql = "select * from wf_mstr where wf_id = ? and wf_op = ? ;";
        }
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        if (x.length > 1) {
        ps.setString(2, x[1]);
        }
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};                        
                        r.add(new wf_mstr(m, res.getString("wf_id"), res.getString("wf_desc"),
                        res.getString("wf_site"), res.getString("wf_op"),
                        res.getString("wf_assert"), res.getString("wf_op_desc"),
                        res.getString("wf_cell"), res.getString("wf_setup_hours"),
                        res.getString("wf_run_hours")));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               
        }
        return r;
    }
  
    
     
    
    
    public static String[] addUOMMstr(uom_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUOMMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  uom_mstr where uom_id = ?";
        String sqlInsert = "insert into uom_mstr (uom_id, uom_desc ) "
                        + " values (?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.uom_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.uom_id);
            psi.setString(2, x.uom_desc);
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

    public static String[] updateUOMMstr(uom_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateUOMMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update uom_mstr set uom_desc = ? "
                        + " where uom_id = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(2, x.uom_id);
            ps.setString(1, x.uom_desc);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static uom_mstr getUOMMstr(String[] x) {
        uom_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getUOMMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, uom_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new uom_mstr(m);
                return r;
            }
        }
        String sql = "select * from uom_mstr where uom_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new uom_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new uom_mstr(m, res.getString("uom_id"), res.getString("uom_desc"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new uom_mstr(m);
        }
        return r;
    }
  
    public static String[] deleteUOMMstr(uom_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteUOMMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m;
        String sqlDelete = "delete from uom_mstr where uom_id = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
             ps.setString(1, x.uom_id);
             int rows = ps.executeUpdate();
             if (rows > 0) {
                m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess}; 
             } else {
                m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError}; 
             }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    public static String[] addUOMConvMstr(conv_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUOMConvMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  conv_mstr where conv_fromcode = ? and conv_tocode = ?";
        String sqlInsert = "insert into conv_mstr (conv_notes, conv_type, conv_fromcode, conv_tocode, conv_fromamt, conv_toamt  ) "
                        + " values (?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.conv_fromcode);
             ps.setString(2, x.conv_tocode);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.conv_notes);
            psi.setString(2, x.conv_type);
            psi.setString(3, x.conv_fromcode);
            psi.setString(4, x.conv_tocode);
            psi.setDouble(5, x.conv_fromamt);
            psi.setDouble(6, x.conv_toamt);
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
    
    public static String[] updateUOMConvMstr(conv_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateUOMConvMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update conv_mstr set conv_notes = ?, conv_type = ?, conv_fromamt = ?, conv_toamt = ?   "
                        + " where conv_fromcode = ? and conv_tocode = ?;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, x.conv_notes);
            ps.setString(2, x.conv_type);
            ps.setString(5, x.conv_fromcode);
            ps.setString(6, x.conv_tocode);
            ps.setDouble(3, x.conv_fromamt);
            ps.setDouble(4, x.conv_toamt);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static conv_mstr getUOMConvMstr(String[] x) {
        conv_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getUOMConvMstr"});
            list.add(new String[]{"param1",x[0]});
            list.add(new String[]{"param2",x[1]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, conv_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new conv_mstr(m);
                return r;
            }
        }
        String sql = "select * from conv_mstr where conv_fromcode = ? and conv_tocode = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        ps.setString(2, x[1]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new conv_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new conv_mstr(m, res.getString("conv_notes"), 
                                res.getString("conv_type"),
                                res.getString("conv_fromcode"),
                                res.getString("conv_tocode"),
                                res.getDouble("conv_fromamt"),
                                res.getDouble("conv_toamt"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new conv_mstr(m);
        }
        return r;
    }
  
    public static String[] deleteUOMConvMstr(conv_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteUOMConvMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m;
        String sqlDelete = "delete from conv_mstr where where conv_fromcode = ? and conv_tocode = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
             ps.setString(1, x.conv_fromcode);
             ps.setString(2, x.conv_tocode);
             int rows = ps.executeUpdate();
             if (rows > 0) {
                m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess}; 
             } else {
                m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError}; 
             }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    
    public static String[] addWareHouseMstr(wh_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addWareHouseMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  wh_mstr where wh_id = ?";
        String sqlInsert = "insert into wh_mstr (wh_id, wh_site, wh_name, "
                        + " wh_addr1, wh_addr2, wh_city, wh_state, wh_zip, wh_country, "
                        + " wh_municipality, wh_county, wh_email, wh_phone ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.wh_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.wh_id);
            psi.setString(2, x.wh_site);
            psi.setString(3, x.wh_name);
            psi.setString(4, x.wh_addr1);
            psi.setString(5, x.wh_addr2);
            psi.setString(6, x.wh_city);
            psi.setString(7, x.wh_state);
            psi.setString(8, x.wh_zip);
            psi.setString(9, x.wh_country);
            psi.setString(10, x.wh_municipality);
            psi.setString(11, x.wh_county);
            psi.setString(12, x.wh_email);
            psi.setString(13, x.wh_phone);
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

    public static String[] updateWareHouseMstr(wh_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateWareHouseMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update wh_mstr set wh_site = ?, wh_name = ?, "
                        + " wh_addr1 = ?, wh_addr2 = ?, wh_city = ?, wh_state = ?, "
                        + " wh_zip = ?, wh_country = ?, wh_municipality = ?, wh_county = ?, "
                        + " wh_email = ?, wh_phone = ? "
                        + " where wh_id = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(13, x.wh_id);
            ps.setString(1, x.wh_site);
            ps.setString(2, x.wh_name);
            ps.setString(3, x.wh_addr1);
            ps.setString(4, x.wh_addr2);
            ps.setString(5, x.wh_city);
            ps.setString(6, x.wh_state);
            ps.setString(7, x.wh_zip);
            ps.setString(8, x.wh_country);
            ps.setString(9, x.wh_municipality);
            ps.setString(10, x.wh_county);
            ps.setString(11, x.wh_email);
            ps.setString(12, x.wh_phone);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static String[] deleteWareHouseMstr(wh_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteWareHouseMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from wh_mstr where wh_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.wh_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static wh_mstr getWareHouseMstr(String[] x) {
        wh_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getWareHouseMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, wh_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new wh_mstr(m);
                return r;
            }
        }
        String sql = "select * from wh_mstr where wh_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new wh_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new wh_mstr(m, res.getString("wh_id"), 
                            res.getString("wh_site"),
                            res.getString("wh_name"),
                            res.getString("wh_addr1"),    
                            res.getString("wh_addr2"),
                            res.getString("wh_city"),
                            res.getString("wh_state"),
                            res.getString("wh_zip"),
                            res.getString("wh_country"),
                            res.getString("wh_municipality"),
                            res.getString("wh_county"),
                            res.getString("wh_email"),
                            res.getString("wh_phone")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new wh_mstr(m);
        }
        return r;
    }
    
    
    
    public static String[] addPLMstr(pl_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addPLMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  pl_mstr where pl_line = ?";
        String sqlInsert = "insert into pl_mstr "
            + "(pl_line, pl_desc, pl_inventory, pl_inv_discr, "
            + "pl_scrap, pl_wip, pl_wip_var, pl_inv_change, pl_sales, pl_sales_disc, "
            + "pl_cogs_mtl, pl_cogs_lbr, pl_cogs_bdn, pl_cogs_ovh, pl_cogs_out, "
            + "pl_purchases, pl_po_rcpt, pl_po_ovh, pl_po_pricevar, pl_ap_usage, pl_ap_ratevar, "
            + "pl_sales_tax, pl_mtl_usagevar, pl_mtl_ratevar, pl_mix_var, pl_cop, pl_out_usagevar, pl_out_ratevar )"
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.pl_line);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.pl_line);
            psi.setString(2, x.pl_desc);
            psi.setString(3, x.pl_inventory);
            psi.setString(4, x.pl_inv_discr);
            psi.setString(5, x.pl_scrap);
            psi.setString(6, x.pl_wip);
            psi.setString(7, x.pl_wip_var);
            psi.setString(8, x.pl_inv_change);
            psi.setString(9, x.pl_sales);
            psi.setString(10, x.pl_sales_disc);
            psi.setString(11, x.pl_cogs_mtl);
            psi.setString(12, x.pl_cogs_lbr);
            psi.setString(13, x.pl_cogs_bdn);
            psi.setString(14, x.pl_cogs_ovh);
            psi.setString(15, x.pl_cogs_out);
            psi.setString(16, x.pl_purchases);
            psi.setString(17, x.pl_po_rcpt);
            psi.setString(18, x.pl_po_ovh);
            psi.setString(19, x.pl_po_pricevar);
            psi.setString(20, x.pl_ap_usage);
            psi.setString(21, x.pl_ap_ratevar);
            psi.setString(22, x.pl_sales_tax);
            psi.setString(23, x.pl_mtl_usagevar);
            psi.setString(24, x.pl_mtl_ratevar);
            psi.setString(25, x.pl_mix_var);
            psi.setString(26, x.pl_cop);
            psi.setString(27, x.pl_out_usagevar);
            psi.setString(28, x.pl_out_ratevar);
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

    public static String[] updatePLMstr(pl_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updatePLMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update pl_mstr set  pl_desc = ?,  pl_inventory = ?, " +
        "pl_inv_discr = ?,  pl_scrap = ?,  pl_wip = ?,  pl_wip_var = ?,  pl_inv_change = ?, " +
        "pl_sales = ?,  pl_sales_disc = ?,  pl_cogs_mtl = ?,  pl_cogs_lbr = ?," +
        "pl_cogs_bdn = ?,  pl_cogs_ovh = ?,  pl_cogs_out = ?,  pl_purchases = ?," +
        "pl_po_rcpt = ?,  pl_po_ovh = ?,  pl_po_pricevar = ?,  pl_ap_usage = ?," +
        "pl_ap_ratevar = ?,  pl_sales_tax = ?,  pl_mtl_usagevar = ?,  pl_mtl_ratevar = ?," +
        "pl_mix_var = ?,  pl_cop = ?,  pl_out_usagevar = ?,  pl_out_ratevar = ? " +
        " where pl_line = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(28, x.pl_line);
            ps.setString(1, x.pl_desc);
            ps.setString(2, x.pl_inventory);
            ps.setString(3, x.pl_inv_discr);
            ps.setString(4, x.pl_scrap);
            ps.setString(5, x.pl_wip);
            ps.setString(6, x.pl_wip_var);
            ps.setString(7, x.pl_inv_change);
            ps.setString(8, x.pl_sales);
            ps.setString(9, x.pl_sales_disc);
            ps.setString(10, x.pl_cogs_mtl);
            ps.setString(11, x.pl_cogs_lbr);
            ps.setString(12, x.pl_cogs_bdn);
            ps.setString(13, x.pl_cogs_ovh);
            ps.setString(14, x.pl_cogs_out);
            ps.setString(15, x.pl_purchases);
            ps.setString(16, x.pl_po_rcpt);
            ps.setString(17, x.pl_po_ovh);
            ps.setString(18, x.pl_po_pricevar);
            ps.setString(19, x.pl_ap_usage);
            ps.setString(20, x.pl_ap_ratevar);
            ps.setString(21, x.pl_sales_tax);
            ps.setString(22, x.pl_mtl_usagevar);
            ps.setString(23, x.pl_mtl_ratevar);
            ps.setString(24, x.pl_mix_var);
            ps.setString(25, x.pl_cop);
            ps.setString(26, x.pl_out_usagevar);
            ps.setString(27, x.pl_out_ratevar);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static String[] deletePLMstr(pl_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deletePLMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from pl_mstr where pl_line = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.pl_line);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static pl_mstr getPLMstr(String[] x) {
        pl_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getPLMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, pl_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new pl_mstr(m);
                return r;
            }
        }
        String sql = "select * from pl_mstr where pl_line = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new pl_mstr(m);
                } else {   
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new pl_mstr(m, res.getString("pl_line"), 
                            res.getString("pl_desc"),
                            res.getString("pl_inventory"),
                            res.getString("pl_inv_discr"),    
                            res.getString("pl_scrap"),
                            res.getString("pl_wip"),
                            res.getString("pl_wip_var"),
                            res.getString("pl_inv_change"),
                            res.getString("pl_sales"), 
                            res.getString("pl_sales_disc"),
                            res.getString("pl_cogs_mtl"),
                            res.getString("pl_cogs_lbr"),
                            res.getString("pl_cogs_bdn"),
                            res.getString("pl_cogs_ovh"),
                            res.getString("pl_cogs_out"),
                            res.getString("pl_purchases"),
                            res.getString("pl_po_rcpt"),
                            res.getString("pl_po_ovh"),
                            res.getString("pl_po_pricevar"),
                            res.getString("pl_ap_usage"),
                            res.getString("pl_ap_ratevar"),
                            res.getString("pl_sales_tax"),
                            res.getString("pl_mtl_usagevar"),
                            res.getString("pl_mtl_ratevar"),
                            res.getString("pl_mix_var"),
                            res.getString("pl_cop"),
                            res.getString("pl_out_usagevar"),
                            res.getString("pl_out_ratevar")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new pl_mstr(m);
        }
        return r;
    }
    
    
    public static String[] addQualMstr(qual_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addQualMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  qual_mstr where qual_id = ?";
        String sqlInsert = "insert into qual_mstr "
            + "(qual_id, qual_site," +
            " qual_userid, qual_date_crt, qual_date_upd, qual_date_cls, qual_originator, " +
            " qual_vend, qual_vend_name, qual_vend_contact, qual_qpr, qual_infor," +
            " qual_sendsupp, qual_sort, qual_rework, qual_scrap, qual_dev, qual_dev_nbr," +
            " qual_src_line, qual_line_dept, qual_src_recv, qual_src_cust, qual_src_eng," +
            " qual_src_oth, qual_src_oth_desc, qual_int_sup, qual_ext_sup, qual_item," +
            " qual_item_desc, qual_qty_rej, qual_qty_susp, qual_qty_tot_def, qual_desc_iss," +
            " qual_desc_fin_hist, qual_desc_sqe_comt, qual_tot_charge," +
            " qual_dec1, qual_date1, qual_int1 )"
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.qual_id);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.qual_id);
            psi.setString(2, x.qual_site);
            psi.setString(3, x.qual_userid);
            psi.setString(4, x.qual_date_crt);
            psi.setString(5, x.qual_date_upd);
            psi.setString(6, x.qual_date_cls);
            psi.setString(7, x.qual_originator);
            psi.setString(8, x.qual_vend);
            psi.setString(9, x.qual_vend_name);
            psi.setString(10, x.qual_vend_contact);
            psi.setString(11, x.qual_qpr);
            psi.setString(12, x.qual_infor);
            psi.setString(13, x.qual_sendsupp);
            psi.setString(14, x.qual_sort);
            psi.setString(15, x.qual_rework);
            psi.setString(16, x.qual_scrap);
            psi.setString(17, x.qual_dev);
            psi.setString(18, x.qual_dev_nbr);
            psi.setString(19, x.qual_src_line);
            psi.setString(20, x.qual_line_dept);
            psi.setString(21, x.qual_src_recv);
            psi.setString(22, x.qual_src_cust);
            psi.setString(23, x.qual_src_eng);
            psi.setString(24, x.qual_src_oth);
            psi.setString(25, x.qual_src_oth_desc);
            psi.setString(26, x.qual_int_sup);
            psi.setString(27, x.qual_ext_sup);
            psi.setString(28, x.qual_item);
            psi.setString(29, x.qual_item_desc);
            psi.setString(30, x.qual_qty_rej);
            psi.setString(31, x.qual_qty_susp);
            psi.setString(32, x.qual_qty_tot_def);
            psi.setString(33, x.qual_desc_iss);
            psi.setString(34, x.qual_desc_fin_hist);
            psi.setString(35, x.qual_desc_sqe_comt);
            psi.setString(36, x.qual_tot_charge);
            psi.setString(37, x.qual_dec1);
            psi.setString(38, x.qual_date1);
            psi.setString(39, x.qual_int1);
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

    public static String[] updateQualMstr(qual_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updateQualMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "update qual_mstr set "
            + "qual_site = ?," +
            " qual_userid = ?, qual_date_crt = ?, qual_date_upd = ?, qual_date_cls = ?, qual_originator = ?, " +
            " qual_vend = ?, qual_vend_name = ?, qual_vend_contact = ?, qual_qpr = ?, qual_infor = ?," +
            " qual_sendsupp = ?, qual_sort = ?, qual_rework = ?, qual_scrap = ?, qual_dev = ?, qual_dev_nbr = ?," +
            " qual_src_line = ?, qual_line_dept = ?, qual_src_recv = ?, qual_src_cust = ?, qual_src_eng = ?," +
            " qual_src_oth = ?, qual_src_oth_desc = ?, qual_int_sup = ?, qual_ext_sup = ?, qual_item = ?," +
            " qual_item_desc = ?, qual_qty_rej = ?, qual_qty_susp = ?, qual_qty_tot_def = ?, qual_desc_iss = ?," +
            " qual_desc_fin_hist = ?, qual_desc_sqe_comt = ?, qual_tot_charge = ?," +
            " qual_dec1 = ?, qual_date1 = ?, qual_int1 = ? where qual_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(39, x.qual_id); 
            ps.setString(1, x.qual_site);
            ps.setString(2, x.qual_userid);
            ps.setString(3, x.qual_date_crt);
            ps.setString(4, x.qual_date_upd);
            ps.setString(5, x.qual_date_cls);
            ps.setString(6, x.qual_originator);
            ps.setString(7, x.qual_vend);
            ps.setString(8, x.qual_vend_name);
            ps.setString(9, x.qual_vend_contact);
            ps.setString(10, x.qual_qpr);
            ps.setString(11, x.qual_infor);
            ps.setString(12, x.qual_sendsupp);
            ps.setString(13, x.qual_sort);
            ps.setString(14, x.qual_rework);
            ps.setString(15, x.qual_scrap);
            ps.setString(16, x.qual_dev);
            ps.setString(17, x.qual_dev_nbr);
            ps.setString(18, x.qual_src_line);
            ps.setString(19, x.qual_line_dept);
            ps.setString(20, x.qual_src_recv);
            ps.setString(21, x.qual_src_cust);
            ps.setString(22, x.qual_src_eng);
            ps.setString(23, x.qual_src_oth);
            ps.setString(24, x.qual_src_oth_desc);
            ps.setString(25, x.qual_int_sup);
            ps.setString(26, x.qual_ext_sup);
            ps.setString(27, x.qual_item);
            ps.setString(28, x.qual_item_desc);
            ps.setString(29, x.qual_qty_rej);
            ps.setString(30, x.qual_qty_susp);
            ps.setString(31, x.qual_qty_tot_def);
            ps.setString(32, x.qual_desc_iss);
            ps.setString(33, x.qual_desc_fin_hist);
            ps.setString(34, x.qual_desc_sqe_comt);
            ps.setString(35, x.qual_tot_charge);
            ps.setString(36, x.qual_dec1);
            ps.setString(37, x.qual_date1);
            ps.setString(38, x.qual_int1);
            int rows = ps.executeUpdate(); 
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static String[] deleteQualMstr(qual_mstr x) { 
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deleteQualMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sql = "delete from qual_mstr where qual_id = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.qual_id);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static qual_mstr getQualMstr(String[] x) {
        qual_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getQualMstr"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, qual_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new qual_mstr(m);
                return r;
            }
        }
        String sql = "select * from qual_mstr where qual_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new qual_mstr(m);
                } else {   
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new qual_mstr(m, res.getString("qual_id"), 
                            res.getString("qual_site"),
                            res.getString("qual_userid"),
                            res.getString("qual_date_crt"),    
                            res.getString("qual_date_upd"),
                            res.getString("qual_date_cls"),
                            res.getString("qual_originator"),
                            res.getString("qual_vend"),
                            res.getString("qual_vend_name"), 
                            res.getString("qual_vend_contact"),
                            res.getString("qual_qpr"),
                            res.getString("qual_infor"),
                            res.getString("qual_sendsupp"),
                            res.getString("qual_sort"),
                            res.getString("qual_rework"),
                            res.getString("qual_scrap"),
                            res.getString("qual_dev"),
                            res.getString("qual_dev_nbr"),
                            res.getString("qual_src_line"),
                            res.getString("qual_line_dept"),
                            res.getString("qual_src_recv"),
                            res.getString("qual_src_cust"),
                            res.getString("qual_src_eng"),
                            res.getString("qual_src_oth"),
                            res.getString("qual_src_oth_desc"),
                            res.getString("qual_int_sup"),
                            res.getString("qual_ext_sup"),
                            res.getString("qual_item"),
                                res.getString("qual_item_desc"),
                                res.getString("qual_qty_rej"),
                                res.getString("qual_qty_susp"),
                                res.getString("qual_qty_tot_def"),
                                res.getString("qual_desc_iss"),
                                res.getString("qual_desc_fin_hist"),
                                res.getString("qual_desc_sqe_comt"),
                                res.getString("qual_tot_charge"),
                                res.getString("qual_dec1"),
                                res.getString("qual_date1"),
                                res.getString("qual_int1")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new qual_mstr(m);
        }
        return r;
    }
    
    
    
    
    public static String[] addUpdateINVCtrl(inv_ctrl x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addUpdateINVCtrl"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        int rows = 0;
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  inv_ctrl"; // there should always be only 1 or 0 records 
        String sqlInsert = "insert into inv_ctrl (planmultiscan, demdtoplan, printsubticket, autoitem, serialize) "
                        + " values (?,?,?,?,?); "; 
        String sqlUpdate = "update inv_ctrl set planmultiscan = ?, demdtoplan = ?, printsubticket = ?, autoitem = ?, serialize = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);
               PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.planmultiscan);
            psi.setString(2, x.demdtoplan);
            psi.setString(3, x.printsubticket);
            psi.setString(4, x.autoitem);
            psi.setString(5, x.serialize);
             rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            psu.setString(1, x.planmultiscan);
            psu.setString(2, x.demdtoplan);
            psu.setString(3, x.printsubticket);
            psu.setString(4, x.autoitem);
            psu.setString(5, x.serialize);
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
   
    public static inv_ctrl getINVCtrl(String[] x) {
        inv_ctrl r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getINVCtrl"});
            list.add(new String[]{"param1",x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, inv_ctrl.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new inv_ctrl(m);
                return r;
            }
        }
        String sql = "select * from inv_ctrl;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new inv_ctrl(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new inv_ctrl(m, 
                                res.getString("planmultiscan"),
                                res.getString("demdtoplan"),
                                res.getString("printsubticket"),
                                res.getString("autoitem"),
                                res.getString("serialize")
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new inv_ctrl(m);
        }
        return r;
    }
    
    public static int _addTranMstr(tran_mstr x, Connection con) throws SQLException {
        int rows = 0;
        String sqlInsert = "insert into tran_mstr (tr_site, tr_item, tr_qty," +
                "tr_ent_date, tr_eff_date, tr_userid, tr_ref, tr_addrcode," +
                "tr_type, tr_datetime, tr_rmks, tr_nbr, tr_misc1," +
                "tr_lot, tr_serial, tr_program," +
                "tr_amt, tr_mtl, tr_lbr, tr_bdn, tr_ovh, tr_out," +
                "tr_batch, tr_op, tr_loc, tr_wh, tr_expire," +
                "tr_cc, tr_wc, tr_wf, tr_prodline, " +
                "tr_actcell, tr_export, tr_order, tr_line, tr_po, tr_price," +
                "tr_cost, tr_acct, tr_terms, tr_pack, tr_curr, " +
                "tr_pack_date, tr_assy_date, tr_uom, tr_base_qty, tr_bom)" +
               " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
          // tr_id and tr_timestamp are db assigned         
        try (PreparedStatement psi = con.prepareStatement(sqlInsert)) {
            psi.setString(1, x.tr_site);
            psi.setString(2, x.tr_item);
            psi.setDouble(3, x.tr_qty);
            psi.setString(4, x.tr_ent_date);
            psi.setString(5, x.tr_eff_date);
            psi.setString(6, x.tr_userid);
            psi.setString(7, x.tr_ref);
            psi.setString(8, x.tr_addrcode);
            psi.setString(9, x.tr_type);
            psi.setString(10, x.tr_datetime);
            psi.setString(11, x.tr_rmks);
            psi.setString(12, x.tr_nbr);
            psi.setString(13, x.tr_misc1);
            psi.setString(14, x.tr_lot);
            psi.setString(15, x.tr_serial);
            psi.setString(16, x.tr_program);
            psi.setDouble(17, x.tr_amt);
            psi.setDouble(18, x.tr_mtl);
            psi.setDouble(19, x.tr_lbr);
            psi.setDouble(20, x.tr_bdn);
            psi.setDouble(21, x.tr_ovh);
            psi.setDouble(22, x.tr_out);
            psi.setString(23, x.tr_batch);
            psi.setString(24, x.tr_op);
            psi.setString(25, x.tr_loc);
            psi.setString(26, x.tr_wh);
            psi.setString(27, x.tr_expire);
            psi.setString(28, x.tr_cc);
            psi.setString(29, x.tr_wc);
            psi.setString(30, x.tr_wf);
            psi.setString(31, x.tr_prodline);
            psi.setString(32, x.tr_actcell);
            psi.setInt(33, x.tr_export);
            psi.setString(34, x.tr_order);
            psi.setInt(35, x.tr_line);
            psi.setString(36, x.tr_po);
            psi.setDouble(37, x.tr_price);
            psi.setDouble(38, x.tr_cost);
            psi.setString(39, x.tr_acct);
            psi.setString(40, x.tr_terms);
            psi.setString(41, x.tr_pack);
            psi.setString(42, x.tr_curr);
            psi.setString(43, x.tr_pack_date);
            psi.setString(44, x.tr_assy_date);
            psi.setString(45, x.tr_uom);
            psi.setDouble(46, x.tr_base_qty);
            psi.setString(47, x.tr_bom);
            rows = psi.executeUpdate();
        }
        return rows;
    }
    
    public static int _addUpdateInMstr(in_mstr in, boolean isInventorySerialized, Connection con) throws SQLException {
          int rows = 0; 
          String expire = in.in_expire();
          String serial = in.in_serial();
          if (! isInventorySerialized) {
                serial = "";
                expire = null;
            }
            java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            String mydate = setDateDB(now);

            double sum = 0.00;    
            
            String sqlSelect = "select in_qoh from in_mstr where in_item = ? and in_loc = ? and in_wh = ? and in_site = ? and in_serial = ?";
            String sqlInsert = "insert into in_mstr (in_item, in_loc, in_wh, in_site, in_serial, in_expire, in_qoh, in_date) " 
                             + " values (?,?,?,?,?,?,?,?); ";
            String sqlUpdate = "update in_mstr set in_qoh = ?, in_date = ? where " 
                             + " in_item = ? and in_loc = ? and in_wh = ? and in_site = ? and in_serial = ? ; "; 
            
            PreparedStatement ps = con.prepareStatement(sqlSelect);
            ps.setString(1, in.in_item);
            ps.setString(2, in.in_loc);
            ps.setString(3, in.in_wh);
            ps.setString(4, in.in_site);
            ps.setString(5, serial);
            try (ResultSet res = ps.executeQuery();
                PreparedStatement psi = con.prepareStatement(sqlInsert);
                PreparedStatement psu = con.prepareStatement(sqlUpdate);) {  
                int z = 0;
                double qoh = 0.00;
                while (res.next()) {
                    z++;
                    qoh = bsParseDouble(res.getString("in_qoh"));
                }
                    res.close();

                    if (z == 0) {
                    sum = Double.valueOf(in.in_qoh());
                    psi.setString(1, in.in_item);
                    psi.setString(2, in.in_loc);
                    psi.setString(3, in.in_wh);
                    psi.setString(4, in.in_site);
                    psi.setString(5, serial);
                    psi.setString(6, expire);
                    psi.setDouble(7, sum);
                    psi.setString(8, mydate);
                    rows = psi.executeUpdate();
                    }  else {
                    sum = qoh + Double.valueOf(in.in_qoh());
                    psu.setDouble(1, sum);
                    psu.setString(2, mydate);
                    psu.setString(3, in.in_item);
                    psu.setString(4, in.in_loc);
                    psu.setString(5, in.in_wh);
                    psu.setString(6, in.in_site);
                    psu.setString(7, serial);
                    rows = psu.executeUpdate();
                    }
    }
    return rows;
    }
    
    public static ArrayList<in_mstr> getInMstr(String[] x) {
        ArrayList<in_mstr> r = new ArrayList<in_mstr>();
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<String[]>();
            paramlist.add(new String[]{"id", "getInMstr"});
            paramlist.add(new String[]{"param1",  x[0]});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, new TypeReference<ArrayList<in_mstr>>() {});
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return r;
            }
        }
        String sql = "select * from in_mstr where in_item = ? order by in_loc;";
        
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r.add(new in_mstr(m, res.getString("in_item"), res.getDouble("in_qoh"),
                        res.getString("in_date"), res.getString("in_loc"), 
                        res.getString("in_wh"), res.getString("in_site"),
                        res.getString("in_serial"), res.getString("in_expire"),
                        res.getString("in_userid"), res.getString("in_prog")));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               
        }
        return r;
    }
  
    
    public static tran_mstr getTranMstr(String id) {
        tran_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getTranMstr"});
            list.add(new String[]{"param1",id});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, tran_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new tran_mstr(m);
                return r;
            }
        }
        String sql = "select * from tran_mstr where tr_id = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, id);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new tran_mstr(m);
                } else {   
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};        
                        r = new tran_mstr(m, res.getString("tr_id"), 
                            res.getString("tr_site"),
                            res.getString("tr_item"),
                            res.getDouble("tr_qty"),    
                            res.getString("tr_ent_date"),
                            res.getString("tr_eff_date"),
                            res.getString("tr_userid"),
                            res.getString("tr_ref"),
                            res.getString("tr_addrcode"), 
                            res.getString("tr_type"),
                            res.getString("tr_datetime"),
                            res.getString("tr_rmks"),
                            res.getString("tr_nbr"),
                            res.getString("tr_misc1"),
                            res.getString("tr_lot"),
                            res.getString("tr_serial"),
                            res.getString("tr_program"),
                            res.getDouble("tr_amt"),
                            res.getDouble("tr_mtl"),
                            res.getDouble("tr_lbr"),
                            res.getDouble("tr_bdn"),
                            res.getDouble("tr_ovh"),
                            res.getDouble("tr_out"),
                            res.getString("tr_batch"),
                            res.getString("tr_op"),
                            res.getString("tr_loc"),
                            res.getString("tr_wh"),
                            res.getString("tr_expire"),
                            res.getString("tr_cc"),
                            res.getString("tr_wc"),
                            res.getString("tr_wf"),
                            res.getString("tr_prodline"),
                            res.getString("tr_timestamp"),
                            res.getString("tr_actcell"),
                            res.getInt("tr_export"),
                            res.getString("tr_order"),
                            res.getInt("tr_line"),
                            res.getString("tr_po"),
                            res.getDouble("tr_price"),
                            res.getDouble("tr_cost"),
                            res.getString("tr_acct"),
                            res.getString("tr_terms"),
                            res.getString("tr_pack"),
                            res.getString("tr_curr"),
                            res.getString("tr_pack_date"),
                            res.getString("tr_assy_date"),
                            res.getString("tr_uom"),
                            res.getDouble("tr_base_qty"),
                            res.getString("tr_bom")
                                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new tran_mstr(m);
        }
        return r;
    }
    
    public static tran_mstr getTranMstrBySerial(String serial, String lot) {
        tran_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","getTranMstrBySerial"});
            list.add(new String[]{"param1",serial});
            list.add(new String[]{"param2",lot});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                r = objectMapper.readValue(returnstring, tran_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new tran_mstr(m);
                return r;
            }
        }
        String sql = "select * from tran_mstr where tr_serial = ? and tr_lot = ?;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, serial);
        ps.setString(2, lot);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new tran_mstr(m);
                } else {   
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};        
                        r = new tran_mstr(m, res.getString("tr_id"), 
                            res.getString("tr_site"),
                            res.getString("tr_item"),
                            res.getDouble("tr_qty"),    
                            res.getString("tr_ent_date"),
                            res.getString("tr_eff_date"),
                            res.getString("tr_userid"),
                            res.getString("tr_ref"),
                            res.getString("tr_addrcode"), 
                            res.getString("tr_type"),
                            res.getString("tr_datetime"),
                            res.getString("tr_rmks"),
                            res.getString("tr_nbr"),
                            res.getString("tr_misc1"),
                            res.getString("tr_lot"),
                            res.getString("tr_serial"),
                            res.getString("tr_program"),
                            res.getDouble("tr_amt"),
                            res.getDouble("tr_mtl"),
                            res.getDouble("tr_lbr"),
                            res.getDouble("tr_bdn"),
                            res.getDouble("tr_ovh"),
                            res.getDouble("tr_out"),
                            res.getString("tr_batch"),
                            res.getString("tr_op"),
                            res.getString("tr_loc"),
                            res.getString("tr_wh"),
                            res.getString("tr_expire"),
                            res.getString("tr_cc"),
                            res.getString("tr_wc"),
                            res.getString("tr_wf"),
                            res.getString("tr_prodline"),
                            res.getString("tr_timestamp"),
                            res.getString("tr_actcell"),
                            res.getInt("tr_export"),
                            res.getString("tr_order"),
                            res.getInt("tr_line"),
                            res.getString("tr_po"),
                            res.getDouble("tr_price"),
                            res.getDouble("tr_cost"),
                            res.getString("tr_acct"),
                            res.getString("tr_terms"),
                            res.getString("tr_pack"),
                            res.getString("tr_curr"),
                            res.getString("tr_pack_date"),
                            res.getString("tr_assy_date"),
                            res.getString("tr_uom"),
                            res.getDouble("tr_base_qty"),
                            res.getString("tr_bom")
                                    );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new tran_mstr(m);
        }
        return r;
    }
    
    public static void _deleteZeroInventoryRecs(Connection con) throws SQLException { 
        PreparedStatement ps = null;   
        String sql = "delete from in_mstr where in_qoh = '0';";
        ps = con.prepareStatement(sql);
        ps.executeUpdate();
        ps.close();
    }
    
    public static void deleteZeroInventoryRecs() { 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "deleteZeroInventoryRecs"});
            try {
                sendServerPost(list, "", null, "dataServINV");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        } 

        String sql = "delete from in_mstr where in_qoh = '0'; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
        int rows = ps.executeUpdate();
        } catch (SQLException s) {
	       MainFrame.bslog(s); 
        }
    }
    
    
    public static String[] inventoryAdjustmentTransaction(tran_mstr tm, in_mstr in, gl_pair gv) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","inventoryAdjustmentTransaction"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(tm);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(in);
                jsonString = jsonString + "=_=" + objectMapper.writeValueAsString(gv);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        boolean isInventorySerialized = (OVData.isInvCtrlSerialize()) ? true : false;
        
        // Skip item code "S" when adjusting inventory -- service item
        if (invData.getItemCode(in.in_item()).equals("S")) {  
            return new String[]{"1", "non-inventory item"}; 
        }
        
        try { 
           
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            
            /* do _addTranMstr */
            _addTranMstr(tm, bscon);
            /* do _addInMstr */
            _addUpdateInMstr(in, isInventorySerialized, bscon);
            /* update InventoryBalance ...independent of serialized or non serialized */
            _updateInventoryBalance(in.in_item(), in.in_site(), String.valueOf(LocalDate.now().getYear()), String.valueOf(LocalDate.now().getMonthValue()), in.in_qoh(), bscon);
                     
            /* do glEntryXPv2 */
            fglData.glEntryXPpair(bscon, gv);
             
            
            /* clean up zero inventory records */
            _deleteZeroInventoryRecs(bscon);
            
            bscon.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1125)};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
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
    
    public static int _updateInventoryBalance(String item, String site, String year, String period, double amt, Connection con) throws SQLException {
        int rows = 0;
        String sql = "select inb_amt from inb_mstr " 
                + " where inb_item = ? and inb_site = ? and inb_year = ? and inb_per = ? ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, item);
        ps.setString(2, site);
        ps.setString(3, year);
        ps.setString(4, period);
        ResultSet res = ps.executeQuery();
        int i = 0;
        double prevamt = 0;
        while (res.next()) {
            i++;
            prevamt = res.getDouble("inb_amt");
        }
        res.close();
        if (i > 0) {
            prevamt += amt;
            sql = "update inb_mstr set inb_amt = ? " 
                        + " where inb_item = ? and inb_site = ? and inb_year = ? and inb_per = ? ";
            ps = con.prepareStatement(sql); 
            ps.setDouble(1, prevamt);
            ps.setString(2, item);
            ps.setString(3, site);
            ps.setString(4, year);
            ps.setString(5, period);
            rows = ps.executeUpdate(); 
        } else {
            sql = "insert into inb_mstr (inb_item, inb_site, inb_year, inb_per, inb_amt) " +
                    " values (?,?,?,?,?); ";
            ps = con.prepareStatement(sql); 
            ps.setString(1, item);
            ps.setString(2, site);
            ps.setString(3, year);
            ps.setString(4, period);
            ps.setDouble(5, amt);
            rows = ps.executeUpdate();   
        }
        ps.close();
        return rows;
    }
    
   

    /* misc functions */
    
    public static String getBOMBrowseView(String[] keys) {
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
                
                if (keys[2].equals("1")) {
                 res = st.executeQuery("SELECT ps_parent, ps_bom, it_desc, ps_op, ps_child, ps_qty_per, ps_ref " +
                    " FROM  pbm_mstr " +
                    " inner join bom_mstr on bom_id = ps_bom and bom_primary = '1' " +     
                    " inner join item_mstr on it_item = ps_parent " +
                    " where ps_parent >= " + "'" + keys[0] + "'" +
                    " AND ps_parent <= " + "'" + keys[1] + "'" +
                    " order by ps_parent, ps_op ;");   
                } else {
                res = st.executeQuery("SELECT ps_parent, ps_bom, it_desc, ps_op, ps_child, ps_qty_per, ps_ref " +
                    " FROM  pbm_mstr " +
                    " inner join bom_mstr on bom_id = ps_bom " +     
                    " inner join item_mstr on it_item = ps_parent " +
                    " where ps_parent >= " + "'" + keys[0] + "'" +
                    " AND ps_parent <= " + "'" + keys[1] + "'" +
                    " order by ps_parent, ps_op ;");
                }
                    while (res.next()) {                  
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ps_parent"));
                        rowArray.put(res.getString("ps_bom"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("ps_op"));
                        rowArray.put(res.getString("ps_child"));
                        rowArray.put(res.getString("ps_qty_per"));
                        rowArray.put(res.getString("ps_ref"));
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
   
    
    public static boolean isBOMUnique(String bom, String item, String routing) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "isBOMUnique"});
            list.add(new String[]{"param1", bom});
            list.add(new String[]{"param2", item});
            list.add(new String[]{"param3", routing});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        }
       boolean r = true;
       ArrayList<String> boms = new ArrayList<String>();
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

                res = st.executeQuery("select bom_routing from bom_mstr "
                        + " where bom_id = " + "'" + bom + "'" +
                        " and bom_item = " + "'" + item + "'" + ";");
                while (res.next()) {
                    if (! res.getString("bom_routing").toLowerCase().equals(routing.toLowerCase())) {
                        r = false;
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
        return r;

    }

    public static String[] getBOMValidation(String item, String bom, String routing) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBOMValidation"});
            list.add(new String[]{"param1", item});
            list.add(new String[]{"param2", bom});
            list.add(new String[]{"param3", routing});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) { 
                bslog(ex);
                return null;
            }
        } 
        
       String[] x = new String[]{"","",""}; // validItem, isUniqueBOM, defaultbom   
       int i = 0;
       ArrayList<String> boms = new ArrayList<String>();
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

                // isvalid item
                res = st.executeQuery("select it_item from item_mstr "
                        + " where it_item = " + "'" + item + "';");
                while (res.next()) {
                    i++;
                }
                if (i > 0) {
                    x[0] = "1";
                }
                i = 0; // reset i
                
                
                // DefaultBOM
                res = st.executeQuery("select distinct ps_bom from pbm_mstr "
                        + " where ps_parent = " + "'" + item + "';");
                while (res.next()) {
                    i++;
                  boms.add(res.getString("ps_bom")); 
                }
                res.close();
                if (i > 0) {
                    for (String s : boms) {
                        res = st.executeQuery("select bom_id from bom_mstr "
                        + " where bom_id = " + "'" + s + "'" +
                          " and bom_primary = '1' " + ";");
                        while (res.next()) {
                           x[1] = res.getString("bom_id");
                        } 
                    }
                }
                i = 0;
                
                // isUniqueBOM
                x[2] = "1"; // true
                res = st.executeQuery("select bom_routing from bom_mstr "
                        + " where bom_id = " + "'" + bom + "'" +
                        " and bom_item = " + "'" + item + "'" + ";");
                while (res.next()) {
                    if (! res.getString("bom_routing").toLowerCase().equals(routing.toLowerCase())) {
                        x[2] = "0"; // false  more than one routing for this bom
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
        return x;

    }

    public static String getQPRBrowseView(String[] keys) {
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
                
                if (keys[5].isBlank()) {
                    res = st.executeQuery("select * from qual_mstr where " +
                        " qual_vend >= " + "'" + keys[2] + "'" + " AND " +
                        " qual_vend <= " + "'" + keys[3] + "'" + " AND " +
                        " qual_site = " + "'" + keys[4] + "'" + " AND " +        
                        " qual_date_crt >= " + "'" + keys[0] + "'" + " AND " +
                        " qual_date_crt <= " + "'" + keys[1] + "'" + 
                        " order by qual_id ;");     
                } else {
                    res = st.executeQuery("select * from qual_mstr where " +
                        " qual_vend >= " + "'" + keys[2] + "'" + " AND " +
                        " qual_vend <= " + "'" + keys[3] + "'" + " AND " +
                        " qual_site = " + "'" + keys[4] + "'" + " AND " + 
                        " qual_item = " + "'" + keys[5] + "'" + " AND " +         
                        " qual_date_crt >= " + "'" + keys[0] + "'" + " AND " +
                        " qual_date_crt <= " + "'" + keys[1] + "'" + 
                        " order by qual_id ;");   
                }
                    while (res.next()) {
                   
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("qual_id"));
                        rowArray.put(res.getString("qual_item"));
                        rowArray.put(res.getString("qual_item_desc"));
                        rowArray.put(res.getString("qual_vend"));
                        rowArray.put(res.getString("qual_vend_name"));
                        rowArray.put(res.getString("qual_userid"));
                        rowArray.put(res.getString("qual_date_crt"));
                        rowArray.put(res.getString("qual_date_cls"));
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
   
    public static String getItemRoutingBrowseView(String item) {
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
                
                res = st.executeQuery("SELECT it_wf, wf_desc, it_item, itr_total, itr_op, itr_lbr_top, itr_ovh_top, itr_bdn_top, itr_out_top, itr_mtl_top " +
                        " FROM  item_mstr  " +
                        " inner join itemr_cost on itr_item = it_item and itr_routing = item_mstr.it_wf " +
                        " inner join wf_mstr on wf_id = it_wf and wf_op = itr_op " +
                        " where it_item = " + "'" + item + "'" + 
                        " order by it_item desc ;");
                int i = 0;
                double opcost = 0.00;
                double prevcost = 0.00;
                
                while (res.next()) {  
                   i++;
                    opcost = (res.getDouble("itr_lbr_top") + res.getDouble("itr_ovh_top") + + res.getDouble("itr_bdn_top") + + res.getDouble("itr_out_top"));
                    
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_wf"));
                        rowArray.put(res.getString("wf_desc"));
                        rowArray.put(res.getString("itr_op"));
                        rowArray.put(bsNumber(opcost));  
                        rowArray.put(res.getString("itr_mtl_top"));
                        rowArray.put(res.getString("itr_total")); 
                        jsonarray.put(rowArray);
                        
                        prevcost = res.getDouble("itr_total");
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
   
    
    public static String getQPRPrintData(String key) {
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
            
            try{
                
                res = st.executeQuery("select * from qual_mstr " + 
                " inner join site_mstr on site_site = qual_site " +
                " inner join ov_ctrl " +   
                " left outer join vd_mstr on vd_addr = qual_vend " +        
                " where qual_id = " + "'" + key + "'");
                
                    
                    int i = 0;
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray();   
                        rowArray.put(res.getString("qual_id"));  // 0
                        rowArray.put(res.getString("qual_site")); 
                        rowArray.put(res.getString("qual_userid")); 
                        rowArray.put(res.getString("qual_date_crt"));    
                        rowArray.put(res.getString("qual_date_upd")); 
                        rowArray.put(res.getString("qual_date_cls")); 
                        rowArray.put(res.getString("qual_originator")); 
                        rowArray.put(res.getString("qual_vend")); 
                        rowArray.put(res.getString("qual_vend_name"));  
                        rowArray.put(res.getString("qual_vend_contact")); 
                        rowArray.put(res.getString("qual_qpr")); // 10
                        rowArray.put(res.getString("qual_infor")); 
                        rowArray.put(res.getString("qual_sendsupp")); 
                        rowArray.put(res.getString("qual_sort")); 
                        rowArray.put(res.getString("qual_rework")); 
                        rowArray.put(res.getString("qual_scrap")); 
                        rowArray.put(res.getString("qual_dev")); 
                        rowArray.put(res.getString("qual_dev_nbr")); 
                        rowArray.put(res.getString("qual_src_line")); 
                        rowArray.put(res.getString("qual_line_dept")); 
                        rowArray.put(res.getString("qual_src_recv"));  // 20
                        rowArray.put(res.getString("qual_src_cust")); 
                        rowArray.put(res.getString("qual_src_eng")); 
                        rowArray.put(res.getString("qual_src_oth")); 
                        rowArray.put(res.getString("qual_src_oth_desc")); 
                        rowArray.put(res.getString("qual_int_sup")); 
                        rowArray.put(res.getString("qual_ext_sup")); 
                        rowArray.put(res.getString("qual_item")); 
                        rowArray.put(res.getString("qual_item_desc")); 
                        rowArray.put(res.getString("qual_qty_rej")); 
                        rowArray.put(res.getString("qual_qty_susp")); // 30
                        rowArray.put(res.getString("qual_qty_tot_def")); 
                        rowArray.put(res.getString("qual_desc_iss")); 
                        rowArray.put(res.getString("qual_desc_fin_hist")); 
                        rowArray.put(res.getString("qual_desc_sqe_comt")); 
                        rowArray.put(res.getString("qual_tot_charge")); 
                        rowArray.put(res.getString("qual_dec1")); 
                        rowArray.put(res.getString("qual_date1")); 
                        rowArray.put(res.getString("qual_int1")); 
                        rowArray.put(res.getString("vd_addr"));
                        rowArray.put(res.getString("vd_name")); // 40
                        rowArray.put(res.getString("ov_image_directory"));
                        rowArray.put(res.getString("ov_jasper_directory"));
                        rowArray.put(res.getString("site_logo"));
                      
                        jsonarray.put(rowArray);
                        i++;
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
       return jsonarray.toString(); 
    }
    
    public static String getInvRptPickerData(String[] keys) {
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
                if (keys[0].equals("iteminfoByItemRange")) {
                res = st.executeQuery("SELECT it_item, it_desc, it_code, it_prodline, " +
                      " it_group, it_loc, it_wh, it_createdate, it_sell_price, " +
                        "  it_pur_price, it_rev from item_mstr " +
                        " where cast(it_item as double) >= " + "'" + keys[1] + "'" +
                        " and cast(it_item as double) <= " + "'" + keys[2] + "'" +
                        " order by it_item; ") ;
                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("it_createdate"));
                            rowArray.put(res.getString("it_prodline"));
                            rowArray.put(res.getString("it_code"));
                            rowArray.put(res.getString("it_group"));
                            rowArray.put(res.getString("it_loc"));
                            rowArray.put(res.getString("it_wh"));
                            rowArray.put(currformat(res.getString("it_sell_price")));
                            rowArray.put(currformat(res.getString("it_pur_price")));
                            rowArray.put(res.getString("it_rev"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemByProdLineRange")) {
                res = st.executeQuery("SELECT it_item, it_desc, it_code, it_prodline, " +
                      " it_group, it_loc, it_wh, it_type, it_sell_price, " +
                        "  it_pur_price, it_rev from item_mstr " +
                        " where it_prodline >= " + "'" + keys[1] + "'" +
                        " and it_prodline <= " + "'" + keys[2] + "'" +
                        " order by it_item; ") ;                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("it_type"));
                            rowArray.put(res.getString("it_prodline"));
                            rowArray.put(res.getString("it_code"));
                            rowArray.put(res.getString("it_group"));
                            rowArray.put(res.getString("it_loc"));
                            rowArray.put(res.getString("it_wh"));
                            rowArray.put(currformat(res.getString("it_sell_price")));
                            rowArray.put(currformat(res.getString("it_pur_price")));
                            rowArray.put(res.getString("it_rev"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemCostsRange")) {
                res = st.executeQuery("select * from item_cost inner join item_mstr on it_item = itc_item " +
                       " where itc_item >= " + "'" + keys[1] + "'" +  " AND " 
                       + " itc_item <= " + "'" + keys[2] + "'" + " AND "        
                        + " itc_set = " + "'" + "standard" + "'" + " AND "
                        + " itc_site = " + "'" + keys[3] + "'" + ";" );                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(bsNumber(res.getDouble("itc_mtl_low") + res.getDouble("itc_mtl_top")));
                            rowArray.put(bsNumber(res.getDouble("itc_lbr_low") + res.getDouble("itc_lbr_top")));
                            rowArray.put(bsNumber(res.getDouble("itc_bdn_low") + res.getDouble("itc_bdn_top")));
                            rowArray.put(bsNumber(res.getDouble("itc_ovh_low") + res.getDouble("itc_ovh_top")));
                            rowArray.put(bsNumber(res.getDouble("itc_out_low") + res.getDouble("itc_out_top")));   
                            rowArray.put(bsNumber(res.getDouble("itc_total")));  
                            jsonarray.put(rowArray); 
                    } 
                }
                
                if (keys[0].equals("itemCustPriceRange")) {
                res = st.executeQuery("select it_item, cpr_item, it_desc, it_sell_price, " +
                      " cpr_cust, cpr_curr, cpr_uom, cm_name, cpr_price " +
                      " from cpr_mstr " +
                      " inner join item_mstr on cpr_item = it_item " +
                      " inner join cm_mstr on cm_code = cpr_cust " +
                      " where cpr_item >= " + "'" + keys[1] + "'" +  " AND " +
                       " cpr_item <= " + "'" + keys[2] + "'" +
                       " and cpr_type = 'List' " +
                       ";" );                  
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("cpr_cust"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("cpr_curr"));
                            rowArray.put(res.getString("cpr_uom"));
                            rowArray.put(currformat(res.getString("it_sell_price")));
                            rowArray.put(currformat(res.getString("cpr_price")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemInventoryRange")) {
                res = st.executeQuery("select it_item, it_desc, " + 
                     // " in_qoh as qoh, in_loc as loc, in_wh as wh, " +
                      " in_serial as serial, in_expire as expire, " +
                      
                       "case when in_qoh is null then '0' else in_qoh end as qoh, " +
                       "case when in_loc is null then '' else in_loc end as loc, " +
                       "case when in_wh is null then '' else in_wh end as wh " +
                    //   "case when in_serial is null then '0' else in_serial end as serial, " +
                    //   "case when in_expire is null then '0' else in_expire end as expire " +
                       
                      " from item_mstr left outer join in_mstr on in_item = it_item " +
                       " where it_item >= " + "'" + keys[1] + "'" +  " AND " 
                       + " it_item <= " + "'" + keys[2] + "'"         
                       + ";" );               
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("qoh"));
                            rowArray.put(res.getString("serial"));
                            rowArray.put(res.getString("loc"));
                            rowArray.put(res.getString("wh"));
                            rowArray.put(res.getString("expire"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemQOHRange")) {
                res = st.executeQuery("select it_item, it_desc, " + 
                       "case when in_qoh is null then '0' else in_qoh end as qoh, " +
                       "case when in_loc is null then '' else in_loc end as loc, " +
                       "case when in_wh is null then '' else in_wh end as wh, " +
                       " (select sum(sod_all_qty - sod_shipped_qty) from sod_det where sod_item = it_item and sod_status <> 'close' group by sod_item) as qtyall " +
                       " from item_mstr left outer join in_mstr on in_item = it_item " +
                       " where it_item >= " + "'" + keys[1] + "'" +  " AND " 
                       + " it_item <= " + "'" + keys[2] + "'"         
                       + ";" );
                    String qtyall = "";
                    while (res.next()) {
                        qtyall = "0";
                    if (res.getString("qtyall") != null) {
                       qtyall = res.getString("qtyall");
                    } 
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("qoh"));
                            rowArray.put(qtyall);
                            rowArray.put(res.getString("loc"));
                            rowArray.put(res.getString("wh"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemAllocatedToOrder")) {
                res = st.executeQuery("select sod_item, it_desc, sod_nbr, cm_name, (sod_all_qty - sod_shipped_qty) as qtyall, " + 
                       " sod_loc, sod_wh " +
                       " from sod_det inner join item_mstr on it_item = sod_item " +
                       " inner join so_mstr on so_nbr = sod_nbr " +
                       " inner join cm_mstr on cm_code = so_cust " +
                       " where sod_item >= " + "'" + keys[1] + "'" +  " AND " 
                       + " sod_item <= " + "'" + keys[2] + "'" + " AND "
                       + " sod_status <> 'close' "
                       + ";" );
                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("sod_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("sod_nbr"));
                            rowArray.put(res.getString("cm_name"));
                            rowArray.put(res.getString("qtyall"));
                            rowArray.put(res.getString("sod_loc"));
                            rowArray.put(res.getString("sod_wh"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemStockRange")) {
                res = st.executeQuery("select it_item, it_desc, it_code, it_safestock, it_minordqty, " + 
                       "coalesce(sum(in_qoh),0) as qoh " +
                        " from item_mstr left outer join in_mstr on in_item = it_item " +
                       " where it_item >= " + "'" + keys[1] + "'" +  " AND " 
                       + " it_item <= " + "'" + keys[2] + "'" 
                       + " group by it_item, it_desc, it_code, it_safestock, it_minordqty ;" );
                
                    while (res.next()) {
                        if (res.getDouble("qoh") >= res.getDouble("it_safestock")) {
                        continue;
                        }
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("it_code"));
                            rowArray.put(res.getString("qoh"));
                            rowArray.put(res.getString("it_safestock"));
                            rowArray.put(res.getString("it_minordqty"));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemValuationRange")) {
                res = st.executeQuery("select it_item, it_desc, it_prodline, it_code, coalesce(itc_total,0) as itctotal, " + 
                       "case when in_qoh is null then '0' else in_qoh end as qoh " +
                       " from item_mstr left outer join in_mstr on in_item = it_item " +
                       " left outer join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = " + "'" + keys[3] + "'" +
                       " where it_item >= " + "'" + keys[1] + "'" +  " AND " 
                       + " it_item <= " + "'" + keys[2] + "'"         
                       + ";" );
                    double totalval = 0;
                    while (res.next()) {
                        totalval = (res.getDouble("qoh") * res.getDouble("itctotal"));
                    
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("it_code"));
                            rowArray.put(res.getString("it_prodline"));
                            rowArray.put(res.getString("qoh"));
                            rowArray.put(res.getString("itctotal"));
                            rowArray.put(totalval);
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemQOHWH")) {
                res = st.executeQuery("select it_item, it_desc, it_code, " + 
                       "case when in_qoh is null then '0' else in_qoh end as qoh, " +
                       "case when in_loc is null then '' else in_loc end as loc, " +
                       "case when in_wh is null then '' else in_wh end as wh, " +
                       " (select sum(sod_all_qty - sod_shipped_qty) from sod_det where sod_item = in_item and sod_status <> 'close' group by sod_item) as qtyall " +
                       " from in_mstr inner join item_mstr on it_item = in_item " +
                       " where in_wh >= " + "'" + keys[1] + "'" +  " AND " 
                       + " in_wh <= " + "'" + keys[2] + "'"         
                       + ";" );
                    String qtyall = "0";
                    while (res.next()) {
                        qtyall = "0";
                        if (res.getString("qtyall") != null) {
                           qtyall = res.getString("qtyall");
                        } 
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("it_code"));
                            rowArray.put(res.getString("qoh"));
                            rowArray.put(qtyall);
                            rowArray.put(res.getString("loc"));
                            rowArray.put(res.getString("wh"));
                            
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("itemQOHLoc")) {
                res = st.executeQuery("select it_item, it_desc, it_code, " + 
                       "case when in_qoh is null then '0' else in_qoh end as qoh, " +
                       "case when in_loc is null then '0' else in_loc end as loc, " +
                       "case when in_wh is null then '0' else in_wh end as wh, " +
                       " (select sum(sod_all_qty - sod_shipped_qty) from sod_det where sod_item = in_item and sod_status <> 'close' group by sod_item) as qtyall " +
                       " from in_mstr inner join item_mstr on it_item = in_item " +
                       " where in_loc >= " + "'" + keys[1] + "'" +  " AND " 
                       + " in_loc <= " + "'" + keys[2] + "'"         
                       + ";" );
                    String qtyall = "0";
                    while (res.next()) {
                        qtyall = "0";
                        if (res.getString("qtyall") != null) {
                           qtyall = res.getString("qtyall");
                        } 
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put("select");
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("it_code"));
                            rowArray.put(res.getString("qoh"));
                            rowArray.put(qtyall);
                            rowArray.put(res.getString("loc"));
                            rowArray.put(res.getString("wh"));
                            
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
     
    public static ArrayList<String> getRoutingOperations(String routing) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getRoutingOperations"});
            list.add(new String[]{"param1", routing});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        ArrayList<String> r = new ArrayList<String>();
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

                res = st.executeQuery("select wf_op from wf_mstr " +
                        " where wf_id = " + "'" + routing + "'" + " order by wf_op ;" );
               
                while (res.next()) {
                   r.add(res.getString("wf_op"));
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
        return r; 
        
    }
   
    public static ArrayList<String> getRoutingOperationsByBOM(String bomid) {
          ArrayList<String> r = new ArrayList<String>();
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

                res = st.executeQuery("select wf_op from wf_mstr inner join bom_mstr on bom_routing = wf_id " +
                        " where bom_id = " + "'" + bomid + "'" + " order by wf_op ;" );
               
                while (res.next()) {
                   r.add(res.getString("wf_op"));
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
        return r; 
        
    }
   
    
    public static ArrayList<String[]> getInvMaintInit(String panelClassName, String userid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getInvMaintInit"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        String[] sites = null;
        boolean allsites = false;
        ArrayList<String[]> lines = new ArrayList<String[]>();
        try{
        Connection con = null;
        if (ds != null) {
        con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
        res = st.executeQuery("select user_allowedsites from user_mstr where user_id = " + "'" + userid + "'" + ";");
            while (res.next()) {
              if (res.getString("user_allowedsites").equals("*")) {
                  allsites = true;
              } else {
                  sites = res.getString("user_allowedsites").split(",");
              }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
           while (res.next()) {
               String[] s = new String[2];
               s[0] = "canupdate";
               s[1] = "0";
               if (res.getString("perm_readonly").equals("0")) {
                 s[1] = "1";
               }
               
               lines.add(s);
           }
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
               
             
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select ov_site, ov_currency from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
            }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
            }
            
            res = st.executeQuery("select * from inv_ctrl;" );
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "isSerialized";
               s[1] = res.getString("serialize");
               lines.add(s);
            }
            
            res = st.executeQuery("select ac_id from ac_mstr order by ac_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "accounts";
               s[1] = res.getString("ac_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select dept_id from dept_mstr order by dept_id ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "depts";
               s[1] = res.getString("dept_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select it_item from item_mstr order by it_item ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "items";
               s[1] = res.getString("it_item");
               lines.add(s);
            }
          
            
            res = st.executeQuery("select wh_id from wh_mstr order by wh_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "warehouses";
               s[1] = res.getString("wh_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select loc_loc from loc_mstr order by loc_loc;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "locations";
               s[1] = res.getString("loc_loc");
               lines.add(s);
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
        return lines;
    }
    
    public static ArrayList<String[]> getInvMaintInit_min(String panelClassName, String userid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getInvMaintInit_min"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        String[] sites = null;
        boolean allsites = false;
        ArrayList<String[]> lines = new ArrayList<String[]>();
        try{
        Connection con = null;
        if (ds != null) {
        con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
        res = st.executeQuery("select user_allowedsites from user_mstr where user_id = " + "'" + userid + "'" + ";");
            while (res.next()) {
              if (res.getString("user_allowedsites").equals("*")) {
                  allsites = true;
              } else {
                  sites = res.getString("user_allowedsites").split(",");
              }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
           while (res.next()) {
               String[] s = new String[2];
               s[0] = "canupdate";
               s[1] = "0";
               if (res.getString("perm_readonly").equals("0")) {
                 s[1] = "1";
               }
               
               lines.add(s);
           }
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
               
             
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select ov_site, ov_currency from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
            }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
            }
            
            res = st.executeQuery("select it_item from item_mstr order by it_item;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "items";
               s[1] = res.getString("it_item");
               lines.add(s);
            }
            res = st.executeQuery("select code_key from code_mstr where code_code = 'trantype' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "trantype";
               s[1] = res.getString("code_key");
               lines.add(s);
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
        return lines;
    }
    
    public static String getItemBrowseView(String[] keys) {
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
                
                res = st.executeQuery("SELECT it_item, it_desc, it_code, " +
                        " it_type, it_uom, it_mtl_cost, it_sell_price, it_site,  " +
                        " coalesce(sum(in_qoh),0) as qty " +
                        " from item_mstr " +
                        " left outer join in_mstr on in_item = it_item and in_site = it_site " +
                        " where it_item >= " + "'" + keys[0]  + "'" + 
                        " AND it_item <= " + "'" + keys[1] + "'" +
                         " AND it_code >= " + "'" + keys[2] + "'" +
                         " AND it_code <= " + "'" + keys[3] + "'" +
                         " AND it_site = " + "'" + keys[4] + "'" +
                         " group by it_item, it_desc, it_code, it_type, it_uom, it_mtl_cost, it_sell_price, it_site order by it_item;");
                    while (res.next()) {
                   
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("it_type"));
                        rowArray.put(res.getString("it_uom"));
                        rowArray.put(bsNumber(res.getDouble("it_mtl_cost"))); 
                        rowArray.put(bsNumber(res.getDouble("it_sell_price")));
                        rowArray.put(bsNumber(res.getDouble("qty")));
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
    
    public static String getInvBrowseView(String[] keys) {
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
                
                if (keys[3].isBlank()) {
                 res = st.executeQuery("select it_item, it_code, it_desc, " + 
                      " in_serial as serial, in_expire as expire,  " +
                      "case when in_qoh is null then '0' else in_qoh end as qoh, " +
                       "case when in_loc is null then '' else in_loc end as loc, " +
                       "case when in_wh is null then '' else in_wh end as wh " +   
                      " from item_mstr left outer join in_mstr on in_item = it_item and " +
                      " in_site = it_site " +   
                       " where it_item >= " + "'" + keys[0] + "'" +  " AND " 
                       + " it_item <= " + "'" + keys[1] + "'" +  " AND " 
                       + " it_site = " + "'" + keys[2] + "'"        
                       + ";" );
                 } else {
                res = st.executeQuery("select it_item, it_code, it_desc, " + 
                      " in_serial as serial, in_expire as expire,  " +
                      "case when in_qoh is null then '0' else in_qoh end as qoh, " +
                       "case when in_loc is null then '' else in_loc end as loc, " +
                       "case when in_wh is null then '' else in_wh end as wh " +   
                      " from item_mstr left outer join in_mstr on in_item = it_item and " +
                      " in_site = it_site " +   
                       " where it_item >= " + "'" + keys[0] + "'" +  " AND " 
                       + " it_item <= " + "'" + keys[1] + "'" +  " AND " 
                       + " in_serial = " + "'" + keys[3] + "'" +  " AND "         
                       + " it_site = " + "'" + keys[2] + "'"        
                       + ";" );  
                }
                
               
                    while (res.next()) {
                   
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("wh"));
                        rowArray.put(res.getString("loc"));
                        rowArray.put(xNull(res.getString("serial"))); 
                        rowArray.put(xNull(res.getString("expire")));
                        rowArray.put(bsNumber(res.getDouble("qoh")));
                        jsonarray.put(rowArray);
                    /*
                    mymodel.addRow(new Object[]{
                                res.getString("it_item"),
                                res.getString("it_desc"),
                                res.getString("it_code"),
                                res.getString("wh"),
                                res.getString("loc"),
                                res.getString("serial"),
                                res.getString("expire"),
                                res.getDouble("qoh")   
                            });
                      */ 
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
    
    public static String getInvValuationBrowseView(String[] keys) {
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
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                String[] td = new String[]{"","","","",""};
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                int thisyear = cal.get(Calendar.YEAR);
                int thisperiod = cal.get(Calendar.MONTH) + 1;


                Date targetdate = dfdate.parse(keys[3]);
                cal.setTime(targetdate);
                int targetyear = cal.get(Calendar.YEAR);
                int targetperiod = cal.get(Calendar.MONTH) + 1;

                // back targetperiod up by 1
                targetperiod = targetperiod - 1;
                if (targetperiod < 1) {
                    targetperiod = 12;
                    targetyear = targetyear - 1;
                }

                double actqty = 0.0;
                double amt = 0.0;

                // if dcdate = today...then reset target period and target year for today
                boolean isToday = false;
                if (setDateDB(targetdate).equals(setDateDB(now))) {
                    targetperiod = thisperiod;
                    targetyear = thisyear;
                    isToday = true;
                } else {
                    td = fglData.getGLCalForDate(targetdate); // td[2] = startdate of period
                }
                
                /*
                System.out.println("target date: " + targetdate);
                System.out.println("target period: " + targetperiod);
                System.out.println("target year: " + targetyear);
                System.out.println("start date: " + td[2]);
                */
                
                if (isToday) {
                 res = st.executeQuery("select it_item, it_code, it_desc, itc_total, " + 
                      "case when inb_amt is null then '0' else inb_amt end as qty " +
                      " from item_mstr " +
                      " inner join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                      " left outer join inb_mstr on inb_item = it_item and " +
                      " inb_site = it_site " + 
                      " AND inb_year = " + "'" + targetyear + "'" +
                      " AND inb_per = " + "'" + targetperiod + "'" +
                       " where it_item >= " + "'" + keys[0] + "'" +  " AND " 
                       + " it_item <= " + "'" + keys[1] + "'" +  " AND " 
                       + " it_site = " + "'" + keys[2] + "'"        
                       + ";" );
                 } else {
                    res = st.executeQuery("select it_item, it_code, it_desc, itc_total, " + 
                      "case when inb_amt is null then '0' else inb_amt end as qty, " +
                      " (select tr_qty from tran_mstr where tr_item = it_item and " +
                            " tr_eff_date >= " + "'" + td[2] + "'" + // td[2] is period start date of targetdate
                            " and " + " tr_eff_date <= " + "'" + setDateDB(targetdate) + "'"  + ") as trqty " +   
                      " from item_mstr " +
                      " inner join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                      " left outer join inb_mstr on inb_item = it_item and " +
                      " inb_site = it_site " + 
                      " AND inb_year = " + "'" + targetyear + "'" +
                      " AND inb_per = " + "'" + targetperiod + "'" +
                       " where it_item >= " + "'" + keys[0] + "'" +  " AND " 
                       + " it_item <= " + "'" + keys[1] + "'" +  " AND " 
                       + " it_site = " + "'" + keys[2] + "'"        
                       + ";" ); 
                 }
                    while (res.next()) {
                    if (isToday) {
                      actqty = res.getDouble("qty");
                    } else {
                      actqty = (res.getDouble("qty") + res.getDouble("trqty"));  
                    }
                    amt = (actqty * res.getDouble("itc_total"));
                    
                    JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(currformatDouble(res.getDouble("itc_total"))); 
                        rowArray.put(bsNumber(actqty));
                        rowArray.put(bsNumber(amt));
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
    
    
    public static void resetBOMDefault(String item) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "resetBOMDefault"});
            list.add(new String[]{"param1", item});
            try {
                sendServerPost(list, "", null, "dataServINV");
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
        try{
            st.executeUpdate("update bom_mstr set bom_primary = '0' where bom_item = " + "'" + item + "'" + ";" );
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
    
    public static void updateBOMDefault(String bomid, String item, String value) {
       
     try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        try{
            st.executeUpdate("update bom_mstr set bom_primary = " + "'" + value + "'" + " where bom_item = " + "'" + item + "'" +
                    " and bom_id = " + "'" + bomid + "'" + ";" );
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
    
    
    
    
    public static String[] getWHLOCfromSerialNumber(String item, String serial) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWHLOCfromSerialNumber"});
            list.add(new String[]{"param1", item});
            list.add(new String[]{"param2", serial});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) { 
                bslog(ex);
                return null;
            }
        } 
        String[] r = null ;
        try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
     
        res = st.executeQuery("select tr_wh, tr_loc from tran_mstr " +
            " where tr_item = " + "'" + item + "'" + " AND " +
            " tr_serial = " + "'" + serial + "'" + ";");
       while (res.next()) {
        r = new String[]{res.getString("tr_wh"), res.getString("tr_loc")};                    
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
        
        return r;
    }
    
    public static void updateCurrentItemCost(String item) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "updateCurrentItemCost"});
            list.add(new String[]{"param1", item});
            try {
                sendServerPost(list, "", null, "dataServINV");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
        calcCost cur = new calcCost("current");
        ArrayList<Double> costlist = cur.getTotalCost(item, OVData.getDefaultBomID(item) );
        OVData.updateItemCostRec(item, invData.getItemSite(item), "current", costlist.get(0), costlist.get(1), costlist.get(2), costlist.get(3), costlist.get(4), costlist.get(0) + costlist.get(1) + costlist.get(2) + costlist.get(3) + costlist.get(4));
    }
    
    public static void rebaseCurrentCost(String item, double mtlcost, double ovhcost, double outcost) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "rebaseCurrentCost"});
            list.add(new String[]{"param1", item});
            list.add(new String[]{"param2", bsNumber(mtlcost)});
            list.add(new String[]{"param3", bsNumber(ovhcost)});
            list.add(new String[]{"param4", bsNumber(outcost)});
            try {
                sendServerPost(list, "", null, "dataServINV");
                return;
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }
        calcCost cur = new calcCost("current");
        double curcost = cur.getTotalCostSum(item, OVData.getDefaultBomID(item));
        updateCurrentItemCost(item);
        if (curcost != (mtlcost + ovhcost + outcost)) {
            Set<String> parents = OVData.getpsmstrparents2(item);
            parents.stream().forEach((s) -> updateCurrentItemCost(s));
        }
    }
    
    public static ArrayList getItemListFromCustCode(String cust) {
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
            

            res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust.toString() + "'" + ";");
            while (res.next()) {
                myarray.add(res.getString("cup_item"));
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

    public static String getItemFromCustCItem(String cust, String custitem) {
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
        try{
            

            res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                  " AND cup_citem = " + "'" + custitem + "'" + ";");
           while (res.next()) {
               mystring = res.getString("cup_item");

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

    public static String[] getItemPriceFromCust(String cust, String item, String uom, String curr, String pricetype, String qty) {
        // returns price, itemdesc
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemPriceFromCust"});
            list.add(new String[]{"param1", cust});
            list.add(new String[]{"param2", item});
            list.add(new String[]{"param3", uom});
            list.add(new String[]{"param4", curr});
            list.add(new String[]{"param5", pricetype});
            list.add(new String[]{"param6", qty});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) { 
                bslog(ex);
                return null;
            }
        } 
       
    String[] r = new String[]{"0",""};  
    String pricecode = "";

    try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            

            res = st.executeQuery("select cm_price_code from cm_mstr where cm_code = " + "'" + cust + "'" + ";");
             while (res.next()) {
               pricecode = res.getString("cm_price_code");
            }     
              // if there is no pricecode....it defaults to billto
             if (! pricecode.isEmpty()) {
                 cust = pricecode;
             }

            if (pricetype.equals("VOLUME")) {
            res = st.executeQuery("select cpr_price, it_desc from cpr_mstr " +
                                  " inner join item_mstr on it_item = cpr_item " +
                                  " where cpr_cust = " + "'" + cust + "'" + 
                                  " AND cpr_item = " + "'" + item + "'" +
                                  " AND cpr_uom = " + "'" + uom + "'" +
                                  " AND cpr_curr = " + "'" + curr + "'" +
                                  " AND cpr_type = " + "'" + pricetype + "'" +
                                  " AND cpr_volqty = " + "'" + qty + "'"        
                                  + ";");
            } else {
              res = st.executeQuery("select cpr_price, it_desc from cpr_mstr " +
                                  " inner join item_mstr on it_item = cpr_item " +
                                  " where cpr_cust = " + "'" + cust + "'" + 
                                  " AND cpr_item = " + "'" + item + "'" +
                                  " AND cpr_uom = " + "'" + uom + "'" +
                                  " AND cpr_curr = " + "'" + curr + "'" +
                                  " AND cpr_type = " + "'" + pricetype + "'"  
                                  + ";");  
            }
           while (res.next()) {
              // price = res.getDouble("cpr_price");
               r[0] = res.getString("cpr_price");
               r[1] = res.getString("it_desc");
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
    return r;

    }

    public static double getItemPriceFromListCode(String code, String item, String uom, String curr) {
    double myreturn = 0;
    String pricecode = "";

    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
      
        res = st.executeQuery("select cpr_price from cpr_mstr where cpr_cust = " + "'" + code + "'" + 
                              " AND cpr_item = " + "'" + item + "'" +
                              " AND cpr_uom = " + "'" + uom + "'" +
                              " AND cpr_curr = " + "'" + curr + "'" +        
                              " AND cpr_type = 'LIST' "+ ";");
       while (res.next()) {
           myreturn = res.getDouble("cpr_price");
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
    return myreturn;

    }

    public static double getItemDiscFromCust(String cust) {
    double myreturn = 0;
    String disccode = "";
    int i = 0;
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        

         res = st.executeQuery("select cm_disc_code from cm_mstr where cm_code = " + "'" + cust + "'" + ";");
         while (res.next()) {
           disccode = res.getString("cm_disc_code");
        }     
          // if there is no pricecode....it defaults to billto
         if (! disccode.isEmpty()) {
             cust = disccode;
         }

        res = st.executeQuery("select cpr_disc from cpr_mstr where cpr_cust = " + "'" + cust + "'" + 
                              " AND cpr_type = " + "'" + "DISCOUNT" + "'" + ";");
       while (res.next()) {
               if (i == 0)
               myreturn = res.getDouble("cpr_disc");
               if (i > 0)
               myreturn = myreturn + res.getDouble("cpr_disc");                   
           i++;
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

    return myreturn;

    }

    public static String getItemFromCustCItem2(String cust, String custitem) {
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
            try{
                res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                      " AND cup_citem2 = " + "'" + custitem + "'" + ";");
               while (res.next()) {
                   mystring = res.getString("cup_item");

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

    public static String getItemFromCustUpc(String cust, String custitem) {
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
            try{
              
                res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust + "'" + 
                                      " AND cup_upc = " + "'" + custitem + "'" + ";");
               while (res.next()) {
                   mystring = res.getString("cup_item");

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

    public static String[] getItemPriceFromVend(String vend, String item, String uom, String curr) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemPriceFromVend"});
            list.add(new String[]{"param1", vend});
            list.add(new String[]{"param2", item});
            list.add(new String[]{"param3", uom});
            list.add(new String[]{"param4", curr});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) { 
                bslog(ex);
                return null;
            }
        } 
        
        String[] myreturn = new String[]{"",""};
       String pricecode = "";

        try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
             
                res = st.executeQuery("select vd_price_code from vd_mstr where vd_addr = " + "'" + vend + "'" + ";");
                 while (res.next()) {
                   pricecode = res.getString("vd_price_code");
                }     
                  // if there is no pricecode....it defaults to billto
                 if (! pricecode.isEmpty()) {
                     vend = pricecode;
                 }

                res = st.executeQuery("select vpr_price, it_desc from vpr_mstr " + 
                        " inner join item_mstr on it_item = vpr_item " +
                        " where vpr_vend = " + "'" + vend + "'" + 
                                      " AND vpr_item = " + "'" + item + "'" +
                                      " AND vpr_uom = " + "'" + uom + "'" +
                                      " AND vpr_curr = " + "'" + curr + "'" +        
                                      " AND vpr_type = 'LIST' "+ ";");
               while (res.next()) {
                   myreturn[0] = res.getString("it_desc");
                   myreturn[1] = res.getString("vpr_price");

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
        return myreturn;

    }

    public static String[] getItemPrice(String type, String partner, String item, String uom, String curr, String qty) {

           // type is either 'c' for customer price or 'v' for vendor price      

        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemPrice"});
            list.add(new String[]{"param1", type});
            list.add(new String[]{"param2", partner});
            list.add(new String[]{"param3", item});
            list.add(new String[]{"param4", uom});
            list.add(new String[]{"param5", curr});
            list.add(new String[]{"param6", qty});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) { 
                bslog(ex);
                return null;
            }
        } 
           
           String[] TypeAndPrice = new String[3];   
           String Type = "none";
           String price = "0";
           String pricecode = "";
           String disccode = "";
           double disc = 0;
           java.util.Date now = new java.util.Date();
           
            try{
                Connection con = null;
                if (ds != null) {
                  con = ds.getConnection();
                } else {
                  con = DriverManager.getConnection(url + db, user, pass);  
                }
                Statement st = con.createStatement();
                ResultSet res = null;
                try{
                    int v = 0;
                    // customer based pricing
                    if (type.equals("c")) {
                        res = st.executeQuery("select cm_price_code from cm_mstr where cm_code = " + "'" + partner + "'" + ";");
                         while (res.next()) {
                           pricecode = res.getString("cm_price_code");
                        }     
                          // if there is no pricecode....it defaults to billto
                         if (! pricecode.isEmpty()) {
                             partner = pricecode;
                         }

                        // check for volume pricing first
                        res = st.executeQuery("select cpr_price from cpr_mstr where cpr_cust = " + "'" + partner + "'" + 
                                              " AND cpr_item = " + "'" + item + "'" +
                                              " AND cpr_uom = " + "'" + uom + "'" +
                                              " AND cpr_curr = " + "'" + curr + "'" +
                                              " AND cpr_volqty <= " + "'" + qty + "'" + 
                                              " AND ( cpr_expire is null OR cpr_expire >= " + "'" + BlueSeerUtils.setDateFormat(now) + "'" + " ) " +           
                                              " AND cpr_type = 'VOLUME' "+ " order by cpr_volqty desc;");
                        while (res.next()) {
                           v++;
                           price = res.getString("cpr_price").replace('.', defaultDecimalSeparator);
                           Type = "cust";
                           break; // break after first record...should be the ideal volume pricing
                        }
                       
                       if (v == 0) { // ok now check for list price
                        res = st.executeQuery("select cpr_price from cpr_mstr where cpr_cust = " + "'" + partner + "'" + 
                                              " AND cpr_item = " + "'" + item + "'" +
                                              " AND cpr_uom = " + "'" + uom + "'" +
                                              " AND cpr_curr = " + "'" + curr + "'" +
                                              " AND ( cpr_expire is null OR cpr_expire >= " + "'" + BlueSeerUtils.setDateFormat(now) + "'" + " ) " +         
                                              " AND cpr_type = 'LIST' "+ ";");
                        while (res.next()) {
                           price = res.getString("cpr_price").replace('.', defaultDecimalSeparator);
                           Type = "cust";
                        }   
                       }
                       
                    }

                    // vendor based pricing
                    if (type.equals("v")) {
                       res = st.executeQuery("select vd_price_code from vd_mstr where vd_addr = " + "'" + partner + "'" + ";");
                     while (res.next()) {
                       pricecode = res.getString("vd_price_code");
                    }     
                      // if there is no pricecode....it defaults to billto
                     if (! pricecode.isEmpty()) {
                         partner = pricecode;
                     }

                    res = st.executeQuery("select vpr_price from vpr_mstr where vpr_vend = " + "'" + partner + "'" + 
                                          " AND vpr_item = " + "'" + item + "'" +
                                          " AND vpr_uom = " + "'" + uom + "'" +
                                          " AND vpr_curr = " + "'" + curr + "'" +        
                                          " AND vpr_type = 'LIST' "+ ";");
                   while (res.next()) {
                       price = res.getString("vpr_price").replace('.', defaultDecimalSeparator);
                       Type = "vend";

                    }
                    }


                   // if there is no customer specific price...then pull price from item master it_sell_price
                      if ( price.equals("0") ) {
                         if (type.equals("c")) { 
                         res = st.executeQuery("select it_sell_price as itemprice from item_mstr where it_item = " + "'" + item + "'" + ";");
                         } else {
                         res = st.executeQuery("select it_pur_price as itemprice from item_mstr where it_item = " + "'" + item + "'" + ";");    
                         }
                         while (res.next()) {
                         price = res.getString("itemprice").replace('.', defaultDecimalSeparator);   
                         Type = "item";
                         }
                      }
                      
                      
                // discounts if applicable
                if (type.equals("c")) {
                
                res = st.executeQuery("select cm_disc_code from cm_mstr where cm_code = " + "'" + partner + "'" + ";");
                while (res.next()) {
                  disccode = res.getString("cm_disc_code");
                }     
                 // if there is no pricecode....it defaults to billto
                if (! disccode.isEmpty()) {
                    partner = disccode;
                }

               res = st.executeQuery("select cpr_price from cpr_mstr where cpr_cust = " + "'" + partner + "'" + 
                                     " AND ( cpr_expire is null OR cpr_expire >= " + "'" + BlueSeerUtils.setDateFormat(now) + "'" + " ) " +
                                     " AND cpr_type = " + "'" + "DISCOUNT" + "'" + ";");
               int i = 0;
               while (res.next()) {
                      if (i == 0) {
                      disc = res.getDouble("cpr_price");
                      }
                      if (i > 0) {
                      disc = disc + res.getDouble("cpr_price");  
                      }
                  i++;
               }
               }
                
               TypeAndPrice[0] = Type;
               TypeAndPrice[1] = String.valueOf(price);
               TypeAndPrice[2] = String.valueOf(disc);

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
            return TypeAndPrice;

        }

    public static double getItemPrice(String item) {
       double price = 0;
     try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            res = st.executeQuery("select it_sell_price from item_mstr where it_item = " + "'" + item + "'" + ";" );
           while (res.next()) {
            price = res.getDouble("it_sell_price");                    
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
    return price;

    }
    
    public static double getItemPurchPrice(String item) {
       double price = 0;
     try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            res = st.executeQuery("select it_pur_price from item_mstr where it_item = " + "'" + item + "'" + ";" );
           while (res.next()) {
            price = res.getDouble("it_pur_price");                    
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
    return price;

    }
   
    public static String getDeptByItemOperation(String item, int op) {
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
        try{
            res = st.executeQuery("SELECT wc_cc from wc_mstr inner join wf_mstr on wf_cell = wc_cell " +
                    " inner join item_mstr on it_wf = wf_id " + 
                    " where it_item = " + "'" + item + "'" +
                    " and wf_op = " + "'" + op + "'" + ";");
               while (res.next()) {
                    mystring = (res.getString("wc_cc"));
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

    public static Set getAllOperationIDs() {
        Set<String> set = new LinkedHashSet<String>();
        try{
       Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
            res = st.executeQuery("SELECT wf_op from wf_mstr order by wf_op ;");
               while (res.next()) {
                    set.add(res.getString("wf_op"));
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
        return set;
    }
    
    public static ArrayList<String> getInvMetaOperators(String item, String op) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getInvMetaOperators"});
            list.add(new String[]{"param1", item});
             list.add(new String[]{"param2", op});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }    
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
                try{ 
                    String routing = "";
                    res = st.executeQuery("select it_wf from item_mstr where it_item = " + "'" + item + "'" + " ;" );
                    while (res.next()) {
                    routing = res.getString("it_wf");                    
                    }
                    res = st.executeQuery("select invm_value from inv_meta where invm_id = " + "'" + routing + "'" +
                            " and invm_type = 'operator' and invm_key = " + "'" + op + "'" +
                            " order by invm_value ;" );
                   while (res.next()) {
                    myarray.add(res.getString("invm_value"));                    
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
            return myarray;

        }

    public static void addInvMetaOperator(String routing, String op, String value) {
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
                res = st.executeQuery("select invm_value from inv_meta where invm_id = " + "'" + routing + "'" +
                            " and invm_type = 'operator' " +
                            " and invm_key = " + "'" + op + "'" +
                            " and invm_value = " + "'" + value + "'" +
                            "  ;" );
                while (res.next()) {
                    i++;
                }

                if (i == 0) {
                    st.executeUpdate("insert into inv_meta values ( "
                            + "'" + routing + "'" + ","
                            + "'" + "operator" + "'" + ","
                            + "'" + op + "'" + ","
                            + "'" + value + "'"     
                            + ")"
                            + ";");
                }
            } // if proceed
            catch (SQLException s) {
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
    }

    public static void deleteInvMetaOperator(String routing, String op, String value) {
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
                st.executeUpdate("delete from inv_meta where invm_id = " + "'" + routing + "'" +
                            " and invm_type = 'operator' " +
                            " and invm_key = " + "'" + op + "'" +
                            " and invm_value = " + "'" + value + "'" +
                            "  ;" );
            } 
            catch (SQLException s) {
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
    }

    public static ArrayList<Double> getCurrentCost(String item) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getCurrentCost"});
            list.add(new String[]{"param1", item});
            try {
                return jsonToArrayListDouble(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
     calcCost cur = new calcCost("current");
     ArrayList<Double> costlist = cur.getTotalCost(item, ""); // assume default bom
     return costlist;
    }
    
    public static ArrayList<Double> getTotalCostElements(String item) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getTotalCostElements"});
            list.add(new String[]{"param1", item});
            try {
                return jsonToArrayListDouble(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
     calcCost cur = new calcCost("current");
     ArrayList<Double> costlist = cur.getTotalCostElements(item, ""); // assume default bom
     return costlist;
    }
    
    public static ArrayList getItemMaintInit(String panelClassName, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemMaintInit"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
        ArrayList lines = new ArrayList();
             try{
                Connection con = null;
                if (ds != null) {
                  con = ds.getConnection();
                } else {
                  con = DriverManager.getConnection(url + db, user, pass);  
                }
                Statement st = con.createStatement();
                ResultSet res = null;
                try{
                  
            res = st.executeQuery("select pl_line from pl_mstr order by pl_line ;" );
            while (res.next()) {
            String[] arr = new String[]{"prodline",res.getString("pl_line")};
            lines.add(arr); 
            }

            String[] sites = null;
            boolean allsites = false;
            res = st.executeQuery("select user_allowedsites from user_mstr where user_id = " + "'" + userid + "'" + ";");
            while (res.next()) {
              if (res.getString("user_allowedsites").equals("*")) {
                  allsites = true;
              } else {
                  sites = res.getString("user_allowedsites").split(",");
              }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
           while (res.next()) {
               String[] s = new String[2];
               s[0] = "canupdate";
               s[1] = "0";
               if (res.getString("perm_readonly").equals("0")) {
                 s[1] = "1";
               }
               
               lines.add(s);
           }
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
               
             
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
            res = st.executeQuery("select ov_site, ov_currency, ov_labelprinter from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
               s = new String[2];
               s[0] = "printer";
               s[1] = res.getString("ov_labelprinter");
               lines.add(s);
            }
            
            res = st.executeQuery("select * from ov_ctrl;" );
            while (res.next()) {
               lines.add(new String[]{"jasperdir", res.getString("ov_jasper_directory")});
               lines.add(new String[]{"imagedir", res.getString("ov_image_directory")});
               lines.add(new String[]{"tempdir", res.getString("ov_temp_directory")});
               lines.add(new String[]{"labeldir", res.getString("ov_label_directory")});
               lines.add(new String[]{"edidir", res.getString("ov_edi_directory")});
            }

                    res = st.executeQuery("select uom_id from uom_mstr order by uom_id ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"uom",res.getString("uom_id")};
                    lines.add(arr); 
                    }

                    res = st.executeQuery("select tax_code from tax_mstr order by tax_code ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"tax",res.getString("tax_code")};
                    lines.add(arr); 
                    }

                    res = st.executeQuery("select loc_loc from loc_mstr order by loc_loc ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"loc",res.getString("loc_loc")};
                    lines.add(arr); 
                    }

                    res = st.executeQuery("select wh_id from wh_mstr order by wh_id ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"wh",res.getString("wh_id")};
                    lines.add(arr); 
                    }

                    res = st.executeQuery("select code_key from code_mstr  where code_code = 'ItemType' order by code_key ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"type",res.getString("code_key")};
                    lines.add(arr); 
                    }
                    
                    res = st.executeQuery("select code_key from code_mstr  where code_code = 'servicetype' order by code_key ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"servicetype",res.getString("code_key")};
                    lines.add(arr); 
                    }

                    res = st.executeQuery("select distinct wf_id from wf_mstr order by wf_id ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"routing",res.getString("wf_id")};
                    lines.add(arr); 
                    }
                    
                    res = st.executeQuery("select autoitem from inv_ctrl;");
                    while (res.next()) {
                    String[] arr = new String[]{"autoitem",res.getString("autoitem")};
                    lines.add(arr); 
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
            return lines;

        }

    public static ArrayList getBOMInit(String item, String site, String bomid, String userid) {
        
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBOMInit"});
            list.add(new String[]{"param1", item});
            list.add(new String[]{"param2", site});
            list.add(new String[]{"param3", bomid});
            list.add(new String[]{"param4", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        
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
                try{
                
            String[] sites = null;
            boolean allsites = false;
            String bomrouting = "";
            res = st.executeQuery("select user_allowedsites from user_mstr where user_id = " + "'" + userid + "'" + ";");
            while (res.next()) {
              if (res.getString("user_allowedsites").equals("*")) {
                  allsites = true;
              } else {
                  sites = res.getString("user_allowedsites").split(",");
              }
            }
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 myarray.add(s);
               }
            } 
                    
                    
                // get default bom for item    
                if (bomid.isBlank()) {
                    ArrayList<String> boms = new ArrayList<String>();
                    int i = 0;
                    res = st.executeQuery("select distinct ps_bom from pbm_mstr "
                            + " where ps_parent = " + "'" + item + "';");
                    while (res.next()) {
                        i++;
                      boms.add(res.getString("ps_bom")); 
                    }
                    res.close();
                    if (i > 0) {
                        for (String s : boms) {
                            res = st.executeQuery("select bom_id, bom_routing from bom_mstr "
                            + " where bom_id = " + "'" + s + "'" +
                              " and bom_primary = '1' " + ";");
                            while (res.next()) {
                               bomid = res.getString("bom_id");
                               bomrouting = res.getString("bom_routing");
                               String[] arr = new String[]{"defaultbom",res.getString("bom_id")};
                               myarray.add(arr);
                               arr = new String[]{"defaultrouting",res.getString("bom_routing")};
                               myarray.add(arr);
                            } 
                        }
                    }
                }
                
                if (bomrouting.isBlank()) {  // get routing from item level if there is no primary bomid
                    res = st.executeQuery("select it_wf from item_mstr where it_item = " + "'" + item + "'" +  ";" );
                    while (res.next()) { 
                     String[] arr = new String[]{"defaultrouting",res.getString("it_wf")};
                     myarray.add(arr); 
                    } 
                }
                
                res = st.executeQuery("select distinct wf_id from wf_mstr order by wf_id ;" );
                    while (res.next()) {
                    String[] arr = new String[]{"routings",res.getString("wf_id")};
                    myarray.add(arr); 
                    }
                
                res = st.executeQuery("select it_lotsize from item_mstr where it_item = " + "'" + item + "'" +  ";" );
                while (res.next()) {
                 String[] arr = new String[]{"lotsize",res.getString("it_lotsize")};
                 myarray.add(arr); 
                }
                
                res = st.executeQuery("select itc_total from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + "STANDARD" + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                while (res.next()) {
                String[] arr = new String[]{"cost", res.getString("itc_total")};
                 myarray.add(arr);
                } 
                
                res = st.executeQuery("select it_ovh_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
                while (res.next()) {
                String[] arr = new String[]{"ovhcost", res.getString("it_ovh_cost")};
                 myarray.add(arr);
                }
                
                res = st.executeQuery("select it_out_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
                while (res.next()) {
                String[] arr = new String[]{"outcost", res.getString("it_out_cost")};
                 myarray.add(arr);
                }
                
                if (! bomid.isBlank()) {
                res = st.executeQuery("select wf_op from wf_mstr inner join bom_mstr on bom_routing = wf_id where bom_item = " + "'" + item + "'" + 
                        " and bom_id = " + "'" + bomid + "'" +
                        " order by wf_op ;");
                } else {
                    res = st.executeQuery("select wf_op from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + item + "'" + 
                        " order by wf_op ;");
                }
                while (res.next()) {
                 String[] arr = new String[]{"operations",res.getString("wf_op")};
                 myarray.add(arr);
                }
                
                res = st.executeQuery("SELECT ps_child, ps_qty_per, ps_type, ps_op, coalesce(a.itc_total,0) as 'a.itc_total', coalesce(b.itc_total,0) as 'b.itc_total' " +
                        " FROM  pbm_mstr  " +
                        " left outer join item_cost a on a.itc_item = ps_child and a.itc_set = 'standard' and a.itc_site = " + "'" + site + "'" +
                        " left outer join item_cost b on b.itc_item = ps_child and b.itc_set = 'current' and b.itc_site = " + "'" + site + "'" +
                        " where ps_parent = " + "'" + item + "'" + 
                        " and ps_bom = " + "'" + bomid + "'" +        
                        " order by ps_child ;");
                while (res.next()) {
                    String[] arr = new String[]{"components",
                        res.getString("ps_child"),
                        res.getString("ps_op"),
                    res.getString("ps_qty_per"),
                    res.getString("b.itc_total"),
                    res.getString("a.itc_total")};
                    myarray.add(arr);
                }
                
                res = st.executeQuery("select * from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + "STANDARD" + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                while (res.next()) {
                    String[] arr = new String[]{"costelements",
                    res.getString("itc_mtl_low"),
                    res.getString("itc_lbr_low"),
                    res.getString("itc_bdn_low"),
                    res.getString("itc_ovh_low"),
                    res.getString("itc_out_low"),
                    res.getString("itc_mtl_top"),
                    res.getString("itc_lbr_top"),
                    res.getString("itc_bdn_top"),
                    res.getString("itc_ovh_top"),
                    res.getString("itc_out_top")};
                    myarray.add(arr);
                }
                
                ArrayList<Double> currlist = invData.getCurrentCost(item);
                double totalcurr = 0.0;
                for (double d : currlist) {
                    totalcurr += d;
                }
                String[] arrm = new String[]{"currentmtl", bsNumber(currlist.get(0))};
                myarray.add(arrm);
                String[] arrl = new String[]{"currentlbr", bsNumber(currlist.get(1))};
                myarray.add(arrl);
                String[] arrb = new String[]{"currentbdn", bsNumber(currlist.get(2))};
                myarray.add(arrb);
                String[] arrovh = new String[]{"currentovh", bsNumber(currlist.get(3))};
                myarray.add(arrovh);
                String[] arrout = new String[]{"currentout", bsNumber(currlist.get(4))};
                myarray.add(arrout);
                String[] arrtot = new String[]{"currenttot", bsNumber(currlist.get(5))};
                myarray.add(arrtot);
              

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
            return myarray;

        }
    
    public static ArrayList<String[]> getWareHouseMaintInit(String panelClassName, String userid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWareHouseMaintInit"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        String defaultsite = "";
        ArrayList<String[]> lines = new ArrayList<String[]>();
        try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
        
            
            res = st.executeQuery("select ov_site, ov_currency from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
               defaultsite = s[1];
            }
            
            String[] sites = null;
            boolean allsites = false;
            res = st.executeQuery("select user_allowedsites from user_mstr where user_id = " + "'" + userid + "'" + ";");
            while (res.next()) {
              if (res.getString("user_allowedsites").equals("*")) {
                  allsites = true;
              } else {
                  sites = res.getString("user_allowedsites").split(",");
              }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
           while (res.next()) {
               String[] s = new String[2];
               s[0] = "canupdate";
               s[1] = "0";
               if (res.getString("perm_readonly").equals("0")) {
                 s[1] = "1";
               }
               
               lines.add(s);
           }
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
            
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
           
            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "states";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'country' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "countries";
               s[1] = res.getString("code_key");
               lines.add(s);
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
        return lines;
    }
    
    public static ArrayList<String[]> getLocationMaintInit(String panelClassName, String userid) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLocationMaintInit"});
            list.add(new String[]{"param1", panelClassName});
             list.add(new String[]{"param2", userid});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        } 
        String defaultsite = "";
        ArrayList<String[]> lines = new ArrayList<String[]>();
        try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
        ResultSet res = null;
        try{
        
            
            res = st.executeQuery("select ov_site, ov_currency from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
               defaultsite = s[1];
            }
            
            String[] sites = null;
            boolean allsites = false;
            res = st.executeQuery("select user_allowedsites from user_mstr where user_id = " + "'" + userid + "'" + ";");
            while (res.next()) {
              if (res.getString("user_allowedsites").equals("*")) {
                  allsites = true;
              } else {
                  sites = res.getString("user_allowedsites").split(",");
              }
            }
            
            res = st.executeQuery("select perm_readonly from perm_mstr inner join menu_mstr on menu_id = perm_menu where perm_user = " + "'" + userid + "'" + 
                    " AND menu_panel = " + "'" + panelClassName + "'" +
                    ";");
           while (res.next()) {
               String[] s = new String[2];
               s[0] = "canupdate";
               s[1] = "0";
               if (res.getString("perm_readonly").equals("0")) {
                 s[1] = "1";
               }
               
               lines.add(s);
           }
           
           String conditionalsite = "";
           res = st.executeQuery("select user_allowedsites, user_site from  " +
                        "  user_mstr where user_id = " + "'" + userid + "'" + ";" );
               while (res.next()) {
                    if (res.getString("user_allowedsites").equals("*")) {
                      conditionalsite = "all";
                    } else {
                      conditionalsite = res.getString("user_site");
                    }
               }
               String[] sx = new String[2];
               sx[0] = "conditionalsite";
               sx[1] = conditionalsite;
               lines.add(sx);
            res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
               if (allsites || Arrays.stream(sites).anyMatch(res.getString("site_site")::equals)) {
                 String[] s = new String[2];
                 s[0] = "sites";
                 s[1] = res.getString("site_site");
                 lines.add(s);
               }
            }
            
             res = st.executeQuery("select wh_id from wh_mstr order by wh_id;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "warehouses";
               s[1] = res.getString("wh_id");
               lines.add(s);
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
        return lines;
    }
    
    
    public static ArrayList<String> getItemImagesFile(String item) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemImagesFile"});
            list.add(new String[]{"param1",  item});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }    
        ArrayList<String> myarray = new ArrayList();
             try{
                Connection con = null;
                if (ds != null) {
                  con = ds.getConnection();
                } else {
                  con = DriverManager.getConnection(url + db, user, pass);  
                }
                Statement st = con.createStatement();
                ResultSet res = null;
                try{
                    res = st.executeQuery("select iti_file from item_image where iti_item = " + "'" + item + "'" + " order by iti_order ;" );
                   while (res.next()) {
                    myarray.add(res.getString("iti_file"));                    
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
            return myarray;

        }

    public static String getItemCode(String item) {
      String myitem = "";
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
     
        res = st.executeQuery("select it_code from item_mstr where it_item = " + "'" + item + "';" );
       while (res.next()) {
        myitem = res.getString("it_code");                    
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

    public static String getItemExpireDate(String item) {
      String mydate = "";
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
     
        res = st.executeQuery("select it_expire from item_mstr where it_item = " + "'" + item + "';" );
       while (res.next()) {
        mydate = res.getString("it_expire");                    
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
    return mydate;  
    }

    public static String getItemExpireDateCalc(String item) {
      String mydate = "";
      Calendar caldate = Calendar.getInstance();
      int days = 0;
         
    try{
    Connection con = null;
    if (ds != null) {
      con = ds.getConnection();
    } else {
      con = DriverManager.getConnection(url + db, user, pass);  
    }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
     
        res = st.executeQuery("select it_expiredays from item_mstr where it_item = " + "'" + item + "';" );
       while (res.next()) {
           if (res.getString("it_expiredays") != null && ! res.getString("it_expiredays").isEmpty()) {
            days = res.getInt("it_expiredays");
           }           
        }
       if (days > 0) {
         caldate.add(Calendar.DATE, days);
         mydate = BlueSeerUtils.setDateFormat(caldate.getTime());
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
    return mydate;  
    }

    public static int getItemExpireDays(String item) {
      int days = 0;
    try{
    Connection con = null;
    if (ds != null) {
      con = ds.getConnection();
    } else {
      con = DriverManager.getConnection(url + db, user, pass);  
    }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
     
        res = st.executeQuery("select it_expiredays from item_mstr where it_item = " + "'" + item + "';" );
       while (res.next()) {
        if (res.getString("it_expiredays") != null && ! res.getString("it_expiredays").isEmpty()) {
            days = res.getInt("it_expiredays");
        }                   
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
    return days;  
    }
    
    public static String getItemTypeByPart(String item) {
    String myitem = "";
    try{
    Connection con = null;
    if (ds != null) {
      con = ds.getConnection();
    } else {
      con = DriverManager.getConnection(url + db, user, pass);  
    }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        
        res = st.executeQuery("select it_type from item_mstr where it_item = " + "'" + item + "';" );
       while (res.next()) {
        myitem = res.getString("it_type");                    
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
    
    public static String getItemSite(String item) {
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
    try{
        res = st.executeQuery("select it_site from item_mstr where it_item = " + "'" + item + "'" +  ";" );
       while (res.next()) {
        myreturn = res.getString("it_site");                    
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
    return myreturn;

    }

    
    public static String getItemLotSize(String item) {
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
    try{
        

        res = st.executeQuery("select it_lotsize from item_mstr where it_item = " + "'" + item + "'" +  ";" );
       while (res.next()) {
        myreturn = res.getString("it_lotsize");                    
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
    return myreturn;

    }

    public static String getItemRouting(String item) {
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
    try{
        

        res = st.executeQuery("select it_wf from item_mstr where it_item = " + "'" + item + "'" +  ";" );
       while (res.next()) {
        myreturn = res.getString("it_wf");                    
        }

    }
    catch (SQLException s){
         MainFrame.bslog(s);
    }finally {
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
    return myreturn;

    }

    public static double getItemQOHTotal(String item, String site) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getItemQOHTotal"});
            list.add(new String[]{"param1",  item});
            list.add(new String[]{"param2",  site});
            try {
                return jsonToDouble(sendServerPost(list, "", null, "dataServINV")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0.00;
            }
        }
     double qty = 0;
     try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            

           res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_site = " + "'" + site + "'"
                            + ";");
           while (res.next()) {
            qty += res.getDouble("in_qoh");                    
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
    return qty;

    }

    public static double getItemQOHUnallocated(String item, String site, String currentorder) {
       double qohu = 0;
     try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            

           res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_site = " + "'" + site + "'"
                            + ";");
               while (res.next()) {
                qohu += res.getDouble("in_qoh");                    
                }

            res = st.executeQuery("SELECT  sum(case when sod_all_qty = '' then 0 else (sod_all_qty - sod_shipped_qty) end) as allqty  " +
                                " FROM  sod_det inner join so_mstr on so_nbr = sod_nbr  " +
                                " where sod_item = " + "'" + item + "'" + 
                                " AND so_status <> 'closed' " + 
                                " AND so_site = " + "'" + site + "'" +   
                              //  " AND so_nbr <> " + "'" + currentorder + "'" +
                                " group by sod_item ;");

                while (res.next()) {
                qohu -= res.getInt("allqty");
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
    return qohu;

    }   

    public static double getItemQtyByWarehouse(String item, String site, String wh) {
       double cost = 0;
     try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            

           res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_site = " + "'" + site + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + ";");
           while (res.next()) {
            cost += res.getDouble("in_qoh");                    
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
    return cost;

    }   

    public static double getItemQtyByWarehouseAndLocation(String item, String site, String wh, String loc) {
     if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getItemQtyByWarehouseAndLocation"});
            list.add(new String[]{"param1",  item});
            list.add(new String[]{"param2",  site});
            list.add(new String[]{"param3",  wh});
            list.add(new String[]{"param4",  loc});
            try {
                return jsonToDouble(sendServerPost(list, "", null, "dataServINV")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0;
            }
        }
     double qty = 0;
     try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            

           res = st.executeQuery("select in_qoh from in_mstr where "
                            + " in_item = " + "'" + item + "'" 
                            + " and in_site = " + "'" + site + "'"
                            + " and in_wh = " + "'" + wh + "'"
                            + " and in_loc = " + "'" + loc + "'"
                            + ";");
           while (res.next()) {
            qty += res.getDouble("in_qoh");                    
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
    return qty;

    }    

    public static String[] getOrderMaintDetailEvent(String item, String site, String uom, String wh, String loc, String key) {
       // returns QOH, uomDesc, PackQtyPerUOM...per uom and qty events in OrderMaint
       
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getOrderMaintDetailEvent"});
            list.add(new String[]{"param1",  item});
            list.add(new String[]{"param2",  site});
            list.add(new String[]{"param3",  uom});
            list.add(new String[]{"param4",  wh});
            list.add(new String[]{"param5",  loc});
            list.add(new String[]{"param6",  key});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));  
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
       
       double qoh = getItemQtyByWarehouseAndLocation(item, site, wh, loc);
       String uomdesc = OVData.getUOMDesc(uom);
       double packqty = getPackQtyForItem(item, site, uom);
       double qualloc = getItemQOHUnallocated(item, site, key);
       
       return new String[]{String.valueOf(qoh), uomdesc, String.valueOf(packqty), String.valueOf(qualloc)};
       
    }
    
    public static double getItemPOSDisc(String item) {
       double price = 0;
     try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try{
            

            res = st.executeQuery("select it_disc_pct from item_mstr where it_item = " + "'" + item + "'" + ";" );
           while (res.next()) {
            price = res.getDouble("it_disc_pct");                    
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
    return price;

    }

    public static double getItemCost(String item, String set, String site) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getItemCost"});
            list.add(new String[]{"param1",  item});
            list.add(new String[]{"param2",  set});
            list.add(new String[]{"param3",  site});
            try {
                return jsonToDouble(sendServerPost(list, "", null, "dataServINV")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0.00;
            }
        }    
        double cost = 0;
             try{
                Connection con = null;
                if (ds != null) {
                  con = ds.getConnection();
                } else {
                  con = DriverManager.getConnection(url + db, user, pass);  
                }
                Statement st = con.createStatement();
                    ResultSet res = null;
                try{
                    

                    res = st.executeQuery("select itc_total from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + set + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                   while (res.next()) {
                    cost = res.getDouble("itc_total");                    
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
            return cost;

        }

    public static double getItemCostUpToOp(String item, String set, String site, String op) {
               double cost = 0;
             try{
                Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
                Statement st = con.createStatement();
                    ResultSet res = null;
                try{
                    
                    ///  if this is material type 'P'...just return standard cost for item.
                    if (getItemCode(item).toUpperCase().equals("P")) {
                      res = st.executeQuery("select itc_total from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + set + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                       while (res.next()) {
                       cost = res.getDouble("itc_total");                    
                       }  
                    } else {
                        res = st.executeQuery("select itr_total from itemr_cost where itr_item = " + "'" + item + "'" +  " AND " 
                                + " itr_set = " + "'" + set + "'" + " AND "
                                + " itr_op <= " + "'" + op + "'" + " AND "
                                + " itr_site = " + "'" + site + "'" + " order by itr_op asc ;" );
                       while (res.next()) {
                       cost += res.getDouble("itr_total");                    
                       }   
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
            return cost;

        }

    public static ArrayList getItemCostElements(String item, String set, String site) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getItemCostElements"});
            list.add(new String[]{"param1", item});
            list.add(new String[]{"param2", set});
            list.add(new String[]{"param3", site});
            try {
                return jsonToArrayListDouble(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }    
        ArrayList<Double> mylist = new ArrayList<Double>();
             try{
                Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
                Statement st = con.createStatement();
                    ResultSet res = null;
                try{
                  

                    res = st.executeQuery("select * from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + set + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                   while (res.next()) {
                    mylist.add(res.getDouble("itc_mtl_low"));
                    mylist.add(res.getDouble("itc_lbr_low"));
                    mylist.add(res.getDouble("itc_bdn_low"));
                    mylist.add(res.getDouble("itc_ovh_low"));
                    mylist.add(res.getDouble("itc_out_low"));
                    mylist.add(res.getDouble("itc_mtl_top"));
                    mylist.add(res.getDouble("itc_lbr_top"));
                    mylist.add(res.getDouble("itc_bdn_top"));
                    mylist.add(res.getDouble("itc_ovh_top"));
                    mylist.add(res.getDouble("itc_out_top"));
                    mylist.add(res.getDouble("itc_total"));

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
            return mylist;

        }

    public static ArrayList<String[]> getItemCostByRange(String item, String from, String set, String site) {
               ArrayList<String[]> mylist = new ArrayList<String[]>();
               String[] myarray = new String[]{"","","","","","",""};
             try{
                Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
                Statement st = con.createStatement();
                    ResultSet res = null;
                try{
                   

                    res = st.executeQuery("select * from item_cost inner join item_mstr on it_item = itc_item where itc_item = " + "'" + item + "'" +  " AND " 
                            + " itc_set = " + "'" + set + "'" + " AND "
                            + " itc_site = " + "'" + site + "'" + ";" );
                   while (res.next()) {
                    myarray[0] = res.getString("it_item");
                    myarray[1] = String.valueOf(res.getDouble("itc_mtl_low") + res.getDouble("itc_mtl_top") );
                    myarray[2] = String.valueOf(res.getDouble("itc_lbr_low") + res.getDouble("itc_lbr_top") );
                    myarray[3] = String.valueOf(res.getDouble("itc_bdn_low") + res.getDouble("itc_bdn_top") );
                    myarray[4] = String.valueOf(res.getDouble("itc_ovh_low") + res.getDouble("itc_ovh_top") );
                    myarray[5] = String.valueOf(res.getDouble("itc_out_low") + res.getDouble("itc_out_top") );
                    myarray[6] = String.valueOf(res.getDouble("itc_total"));

                    mylist.add(myarray);

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
            return mylist;

        }

    public static ArrayList<String[]> getBOMsByItemSite(String item) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getBOMsByItemSite"});
            list.add(new String[]{"param1",  item});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList<String[]> mylist = new ArrayList<String[]>();
        try{
                Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
                Statement st = con.createStatement();
                    ResultSet res = null;
                try{
                   

                    res = st.executeQuery("select bom_id, bom_primary from bom_mstr "
                            + " where bom_item = " + "'" + item + "'" 
                            + " and bom_enabled = '1' " + " order by bom_primary desc ;" ); 
                   while (res.next()) {
                    mylist.add(new String[]{res.getString("bom_id"),res.getString("bom_primary")});
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
            return mylist;

        }

    public static ArrayList<String[]> getBOMsByItemSite_mg(String item) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBOMsByItemSite_mg"});
            list.add(new String[]{"param1",  item});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
        ArrayList<String[]> mylist = new ArrayList<String[]>();
        try{
                Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
                Statement st = con.createStatement();
                    ResultSet res = null;
                try{
                   

                    res = st.executeQuery("select bom_id, bom_primary from bom_mstr "
                            + " where bom_item = " + "'" + item + "'" 
                            + " and bom_enabled = '1' " + " order by bom_primary desc ;" ); 
                   while (res.next()) {
                    mylist.add(new String[]{"boms", res.getString("bom_id")});
                    }
                   
                    int i = 0;
                    ArrayList<String> boms = new ArrayList<String>();
                    res = st.executeQuery("select distinct ps_bom from pbm_mstr "
                        + " where ps_parent = " + "'" + item + "';");
                    while (res.next()) {
                        i++;
                      boms.add(res.getString("ps_bom")); 
                    }
                    res.close();
                    if (i == 0) {
                        mylist.add(new String[]{"bomprimary", ""});
                    } else {
                        for (String s : boms) {
                            res = st.executeQuery("select bom_id from bom_mstr "
                            + " where bom_id = " + "'" + s + "'" +
                              " and bom_primary = '1' " + ";");
                            while (res.next()) {
                               mylist.add(new String[]{"bomprimary", res.getString("bom_id")});
                            } 
                        }
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
            return mylist;

        }

    public static ArrayList<String> getLocationListByWarehouse(String wh) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLocationListByWarehouse"});
            list.add(new String[]{"param1",  wh});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        ArrayList<String> myarray = new ArrayList();
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

            res = st.executeQuery("select loc_loc from loc_mstr  " + 
                    "where loc_wh = " + "'" + wh + "'" +
                    " order by loc_loc ;" );
           while (res.next()) {
            myarray.add(res.getString("loc_loc"));                    
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

    public static ArrayList<String[]> getRecentTransByItem(String item) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getRecentTransByItem"});
            list.add(new String[]{"param1",  item});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
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

            res = st.executeQuery("SELECT tr_type, tr_eff_date, tr_id, tr_base_qty  " +
                        " FROM  tran_mstr  " +
                        " where tr_item =  " + "'" + item + "'" + 
                        " order by tr_eff_date desc limit 25");
           while (res.next()) {
            myarray.add(new String[]{res.getString("tr_type"),
                res.getString("tr_eff_date"),
                res.getString("tr_id"),
                res.getString("tr_base_qty")
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

    public static ArrayList<String[]> getInventoryQtyByItem(String item) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getInventoryQtyByItem"});
            list.add(new String[]{"param1",  item});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
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

            res = st.executeQuery("SELECT in_site, in_wh, in_loc, in_qoh, in_serial, in_date  " +
                        " FROM  in_mstr  " +
                        " where in_item = " + "'" + item + "'" + 
                        " order by in_wh, in_loc ;");
           while (res.next()) {
            myarray.add(new String[]{res.getString("in_site"),
                res.getString("in_wh"),
                res.getString("in_loc"),
                res.getString("in_qoh"),
                res.getString("in_serial"),
                res.getString("in_date")
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

    public static ArrayList<String[]> getItemInvInfo(String item) {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemInvInfo"});
            list.add(new String[]{"param1",  item});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
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

            res = st.executeQuery("select * from in_mstr inner join item_mstr on it_item = in_item where in_item = " + "'" + item + "'" 
                        + ";");
           while (res.next()) {               
            myarray.add(new String[]{res.getString("in_item"),
                res.getString("it_desc"),
                res.getString("in_serial"),
                res.getString("in_wh"),
                res.getString("in_loc"),
                res.getString("in_expire"),
                res.getString("in_qoh"),
                res.getString("it_uom"),
                res.getString("it_sell_price")
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


    public static double getItemOperationalCost(String item, String set, String site) {
    double cost = 0; 
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
                    ResultSet res = null;
    try{
       

        res = st.executeQuery("select * from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                + " itc_set = " + "'" + set + "'" + " AND "
                + " itc_site = " + "'" + site + "'" + ";" );
       while (res.next()) {
           cost += res.getDouble("itc_lbr_top") + 
                   res.getDouble("itc_bdn_top") +
                   res.getDouble("itc_ovh_top") +
                   res.getDouble("itc_out_top");
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
    return cost;

    }

    public static double getItemMtlCostStd(String item, String set, String site) {
    double cost = 0;
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
                    ResultSet res = null;
    try{
        

        res = st.executeQuery("select itc_mtl_top, itc_mtl_low from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                + " itc_set = " + "'" + set + "'" + " AND "
                + " itc_site = " + "'" + site + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("itc_mtl_top") + res.getDouble("itc_mtl_low");                    
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
    return cost;

    }

    public static double[] getItemCostSet(String item, Connection bscon) {
    double[] cost = new double[]{0,0,0};
    try{
    
    Statement st = bscon.createStatement();
                    ResultSet res = null;
    try{
       

        res = st.executeQuery("select it_mtl_cost, it_out_cost, it_ovh_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
       while (res.next()) {
        cost[0] = res.getDouble("it_mtl_cost"); 
        cost[1] = res.getDouble("it_ovh_cost");
        cost[2] = res.getDouble("it_out_cost");
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
          }
    }
    catch (Exception e){
    MainFrame.bslog(e);
    }
    return cost;

    }

    
    public static double getItemMtlCost(String item) {
    double cost = 0;
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
                    ResultSet res = null;
    try{
       

        res = st.executeQuery("select it_mtl_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("it_mtl_cost");                    
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
    return cost;

    }

    public static double getItemOvhCostStd(String item, String set, String site) {
    double cost = 0;
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
                    ResultSet res = null;
    try{
       

        res = st.executeQuery("select itc_ovh_top, itc_ovh_low from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                + " itc_set = " + "'" + set + "'" + " AND "
                + " itc_site = " + "'" + site + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("itc_ovh_top") + res.getDouble("itc_ovh_low");                    
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
    return cost;

    }

    public static double getItemOvhCost(String item) {
    double cost = 0;
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
    ResultSet res = null;
    try{
        res = st.executeQuery("select it_ovh_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("it_ovh_cost");                    
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
    return cost;

    }

    public static double getItemOutCostStd(String item, String set, String site) {
    double cost = 0;
    try{
    Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
    Statement st = con.createStatement();
                    ResultSet res = null;
    try{
        

        res = st.executeQuery("select itc_out_top, itc_out_low from item_cost where itc_item = " + "'" + item + "'" +  " AND " 
                + " itc_set = " + "'" + set + "'" + " AND "
                + " itc_site = " + "'" + site + "'" + ";" );
       while (res.next()) {
        cost = res.getDouble("itc_out_top") + res.getDouble("itc_out_low");                    
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
    return cost;

    }

    public static double getItemOutCost(String item) {
           double cost = 0;
         try{
            Class.forName(driver).newInstance();
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
                    ResultSet res = null;
            try{
               
                res = st.executeQuery("select it_out_cost from item_mstr where it_item = " + "'" + item + "'" + ";" );
               while (res.next()) {
                cost = res.getDouble("it_out_cost");                    
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
        return cost;

    }

    public static String getItemStatusByPart(String item) {
           String myitem = "";
         try{
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
                    ResultSet res = null;
            try{
                
                res = st.executeQuery("select it_status from item_mstr where it_item = " + "'" + item + "';" );
               while (res.next()) {
                myitem = res.getString("it_status");                    
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

    public static String getItemDesc(String item) {
           String myitem = "";
         try{
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
                    ResultSet res = null;
            try{
               

                res = st.executeQuery("select it_desc from item_mstr where it_item = " + "'" + item + "';" );
               while (res.next()) {
                myitem = res.getString("it_desc");                    
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

    public static HashMap<String, String[]> getItemDataInit(String item, String site, String entity, String type) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemDataInit"});
            list.add(new String[]{"param1", item});
            list.add(new String[]{"param2", site});
            list.add(new String[]{"param3", entity});
            list.add(new String[]{"param4", type}); 
            try {
                String jsonString = sendServerPost(list, "", null, "dataServINV");
                return jsonToHashMapStringStringArr(jsonString);
            } catch (IOException ex) { 
                bslog(ex);
                return null;
            }
        } 
        HashMap<String,String[]> hm = new HashMap<>();
        String[] x = new String[]{"","","","","","","","","","","","",""};
        int days = 0;
        String servicetype = "";
        
        Calendar caldate = Calendar.getInstance();
        try{
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select it_item, it_desc, it_uom, it_prodline, it_code, it_rev, it_status, it_site, it_loc, it_wh, it_expiredays, it_type, it_servicetype " +
                        " from item_mstr " +
                        " where it_item = " + "'" + item + "';" );
               while (res.next()) {
                   if (res.getString("it_expiredays") != null && ! res.getString("it_expiredays").isEmpty()) {
                   days = res.getInt("it_expiredays");
                   } 
                   if (res.getString("it_type").equals("SERVICE")) {
                   servicetype = res.getString("it_servicetype");
                   } 
                x[0] = res.getString("it_item"); 
                x[1] = res.getString("it_desc"); 
                x[2] = res.getString("it_uom"); 
                x[3] = res.getString("it_prodline"); 
                x[4] = res.getString("it_code"); 
                x[5] = res.getString("it_rev"); 
                x[6] = res.getString("it_status"); 
                x[7] = res.getString("it_site"); 
                x[8] = res.getString("it_loc"); 
                x[9] = res.getString("it_wh"); 
                if (days > 0) {
                  caldate.add(Calendar.DATE, days);
                  x[10] = BlueSeerUtils.setDateFormat(caldate.getTime());
                }
                x[11] = res.getString("it_type");
                }
               
               if (servicetype != null && ! servicetype.isBlank()) {
                   res = st.executeQuery("select code_value from code_mstr where " +
                        " code_code = 'servicetype' AND code_key = " + "'" + servicetype + "';" );
                   while (res.next()) {
                       x[12] = res.getString("code_value"); // service code description
                   }
               }
               
               hm.put("itemdata", x);
               
            // cust item info   
            if (type.equals("cust")) {
                res = st.executeQuery("select cup_citem from cup_mstr where cup_cust = " + "'" + entity + "'" + 
                                      " AND cup_item = " + "'" + item + "'" + ";");
                while (res.next()) {
                   hm.put("itemcust", new String[]{res.getString("cup_citem")});
                }

                // by cust item search key   
                res = st.executeQuery("select cup_item, cup_citem from cup_mstr where cup_cust = " + "'" + entity + "'" + 
                                      " AND cup_citem = " + "'" + item + "'" + ";");
                while (res.next()) {
                   hm.put("bycustitem", new String[]{res.getString("cup_item"),res.getString("cup_citem")});
                }


                // BOM info
                res = st.executeQuery("select bom_id, bom_primary from bom_mstr "
                                + " where bom_item = " + "'" + item + "'" 
                                + " and bom_enabled = '1' " + " order by bom_primary desc ;" ); 
                       while (res.next()) {
                        hm.put("boms", new String[]{res.getString("bom_id")});
                        }

                // default BOM
                String defaultBOM = "";
                ArrayList<String> boms = new ArrayList<String>();
                res = st.executeQuery("select distinct ps_bom from pbm_mstr "
                            + " where ps_parent = " + "'" + item + "';");
                    while (res.next()) {
                      boms.add(res.getString("ps_bom")); 
                    }
                    res.close();

                    for (String s : boms) {
                        res = st.executeQuery("select bom_id from bom_mstr "
                        + " where bom_id = " + "'" + s + "'" +
                          " and bom_primary = '1' " + ";");
                        while (res.next()) {
                           defaultBOM = res.getString("bom_id");
                        } 
                    }
                 hm.put("defaultbom", new String[]{defaultBOM});   


                // top wh/loc qty
                String[] myloc = new String[2];
                res = st.executeQuery("select it_loc, it_wh from item_mstr where it_item = " + "'" + item + "'" + 
                        " AND it_site = " + "'" + site + "'" +
                        " ;" );
               while (res.next()) {
                myloc[0] = res.getString("it_wh");  
                myloc[1] = res.getString("it_loc");
                }

                // now overwrite with optimum wh and loc with highest qoh
                res = st.executeQuery("select in_loc, in_wh from in_mstr where in_item = " + "'" + item + "'" + 
                        " AND in_site = " + "'" + site + "'" +
                        " order by in_qoh desc limit 1;" );
                while (res.next()) {
                myloc[0] = res.getString("in_wh");  
                myloc[1] = res.getString("in_loc");
                }
                hm.put("topwhloc", myloc);
            }
            
            if (type.equals("vend")) {
                res = st.executeQuery("select vdp_vitem from vdp_mstr where vdp_vend = " + "'" + entity + "'" + 
                                      " AND vdp_item = " + "'" + item + "'" + ";");
                while (res.next()) {
                   hm.put("itemvend", new String[]{("vdp_vitem")});
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
        } catch (Exception e){
            MainFrame.bslog(e);
        }
        return hm;
    } 
       
    public static String[] getItemDetail(String item) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getItemDetail"});
            list.add(new String[]{"param1",  item});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));  
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }   
        
        String[] x = new String[]{"","","","","","","","","","","",""};
           int days = 0;
           Calendar caldate = Calendar.getInstance();
           try{
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select it_item, it_desc, it_uom, it_prodline, it_code, it_rev, it_status, it_site, it_loc, it_wh, it_expiredays, itc_total " +
                        " from item_mstr " +
                        " left outer join item_cost on itc_item = it_item and itc_set = 'standard' " +
                        " where it_item = " + "'" + item + "';" );
               while (res.next()) {
                   if (res.getString("it_expiredays") != null && ! res.getString("it_expiredays").isEmpty()) {
                   days = res.getInt("it_expiredays");
                   }  
                x[0] = res.getString("it_item"); 
                x[1] = res.getString("it_desc"); 
                x[2] = res.getString("it_uom"); 
                x[3] = res.getString("it_prodline"); 
                x[4] = res.getString("it_code"); 
                x[5] = res.getString("it_rev"); 
                x[6] = res.getString("it_status"); 
                x[7] = res.getString("it_site"); 
                x[8] = res.getString("it_loc"); 
                x[9] = res.getString("it_wh"); 
                if (days > 0) {
                  caldate.add(Calendar.DATE, days);
                  x[10] = BlueSeerUtils.setDateFormat(caldate.getTime());
                }
                x[11] = res.getString("itc_total");
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
        } catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;

    }

    public static String[] getItemComponentDetail(String parent, String component) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getItemComponentDetail"});
            list.add(new String[]{"param1",  parent});
            list.add(new String[]{"param2",  component});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));  
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }   
        
        String[] x = new String[]{"","","","","","","","","","",""};
           int days = 0;
           Calendar caldate = Calendar.getInstance();
           try{
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("SELECT ps_child, it_desc, ps_op, ps_qty_per, itc_total, it_expiredays  " +
                        " FROM  pbm_mstr inner join item_mstr on it_item = ps_child  " +
                       " left outer join item_cost on itc_item = it_item and itc_set = 'standard' " +
                       " where ps_parent = " + "'" + parent + "'" + 
                       " AND ps_child = " + "'" + component + "'" +
                        " ;");
                while (res.next()) {
                   if (res.getString("it_expiredays") != null && ! res.getString("it_expiredays").isEmpty()) {
                   days = res.getInt("it_expiredays");
                   }  
                x[0] = res.getString("ps_child"); 
                x[1] = res.getString("it_desc"); 
                x[2] = res.getString("ps_op"); 
                x[3] = res.getString("ps_qty_per"); 
                x[4] = res.getString("itc_total"); 
                if (days > 0) {
                  caldate.add(Calendar.DATE, days);
                  x[5] = BlueSeerUtils.setDateFormat(caldate.getTime());
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
        } catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;

    }

    public static String[] getComponentByBomOp(String parent, String component, String op, String bom) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getItemComponentDetail"});
            list.add(new String[]{"param1",  parent});
            list.add(new String[]{"param2",  component});
            list.add(new String[]{"param3",  op});
            list.add(new String[]{"param4",  bom});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));  
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }   
        
        String[] x = new String[]{"","","","","","","","",""};
           int days = 0;
           Calendar caldate = Calendar.getInstance();
           try{
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("SELECT ps_child, it_desc, ps_op, ps_qty_per, itc_total, it_expiredays, ps_serialized, ps_ref, it_code  " +
                       " FROM  pbm_mstr inner join item_mstr on it_item = ps_child  " +
                       " left outer join item_cost on itc_item = it_item and itc_set = 'standard' " +
                       " where ps_parent = " + "'" + parent + "'" + 
                       " AND ps_child = " + "'" + component + "'" + 
                       " AND ps_op = " + "'" + op + "'" +
                       " AND ps_bom = " + "'" + bom + "'" +          
                             " ;");
                while (res.next()) {
                   if (res.getString("it_expiredays") != null && ! res.getString("it_expiredays").isEmpty()) {
                   days = res.getInt("it_expiredays");
                   }  
                x[0] = res.getString("ps_child"); 
                x[1] = res.getString("it_desc"); 
                x[2] = res.getString("ps_op"); 
                x[3] = res.getString("ps_qty_per"); 
                x[4] = res.getString("itc_total"); 
                if (days > 0) {
                  caldate.add(Calendar.DATE, days);
                  x[5] = BlueSeerUtils.setDateFormat(caldate.getTime());
                }
                x[6] = res.getString("ps_serialized");
                x[7] = res.getString("ps_ref");
                x[8] = res.getString("it_code");
                
                
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
        } catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;

    }

    
    public static ArrayList<String> getItemRoutingOPs(String myitem) {
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
            try{
                
                 res = st.executeQuery("SELECT itr_op from itemr_cost where itr_item = " + "'" + myitem.toString() + "'" + " order by itr_op;");
               while (res.next()) {
                    myarray.add(res.getString("itr_op"));
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
        return myarray;

    }

    public static ArrayList getItemWFOPs(String myitem) {
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
            try{
               
                 res = st.executeQuery("SELECT wf_op from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + myitem.toString() + "'" + " order by wf_op;");
               while (res.next()) {
                    myarray.add(res.getString("wf_op"));
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
        return myarray;

    }

    public static ArrayList _getItemWFOPs(String myitem, Connection bscon) {
       ArrayList myarray = new ArrayList();
        try{
            Statement st = bscon.createStatement();
            ResultSet res = null;
            try{
               
                 res = st.executeQuery("SELECT wf_op from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + myitem.toString() + "'" + " order by wf_op;");
               while (res.next()) {
                    myarray.add(res.getString("wf_op"));
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
          }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return myarray;

    }

    
    public static ArrayList<String[]> getItemWFOPandDESC(String myitem) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemWFOPandDESC"});
            list.add(new String[]{"param1", myitem});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServINV"));
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
            try{
               
                 res = st.executeQuery("SELECT wf_op, wf_op_desc from wf_mstr inner join item_mstr on it_wf = wf_id where it_item = " + "'" + myitem.toString() + "'" + " order by wf_op;");
               while (res.next()) {
                    myarray.add(new String[]{res.getString("wf_op"), res.getString("wf_op_desc")});
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
        return myarray;

    }

    
    public static ArrayList getItemMasterSchedlist() {
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemMasterSchedlist"});
            try {
                return jsonToArrayListString(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
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
            try{
                
                res = st.executeQuery("select it_item from item_mstr where it_sched = '1' order by it_item;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));
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
        return myarray;

    }    

    public static ArrayList getItemMasterMCodelist() {
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
            try{
               
                res = st.executeQuery("select it_item from item_mstr where it_code = 'M' order by it_item;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));
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
        return myarray;

    }


    public static ArrayList getItemMasterACodelist() {
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
            try{
                
                res = st.executeQuery("select it_item from item_mstr where it_code = 'A' order by it_item;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));
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
        return myarray;

    }

    public static ArrayList getItemMasterACodeForCashTran() {
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
            try{
               

                res = st.executeQuery("select it_item from item_mstr  " +
                        " where it_code = 'A' and it_status = 'ACTIVE' order by it_item;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));
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
        return myarray;

    }


    public static ArrayList getItemMasterRawlist() {
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
            try{
                
                res = st.executeQuery("select it_item from item_mstr where it_code = 'P' order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

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
        return myarray;

    }

    public static ArrayList getItemsByType(String type) {
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
            try{
              
                res = st.executeQuery("select it_item from item_mstr where it_type = " + "'" + type + "'" +
                        " order by it_item ;");

               while (res.next()) {
                    myarray.add(res.getString("it_item"));

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
        return myarray;

    }

    public static ArrayList getItemsByTypeExcept(String[] types) {
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
            try{
              
                res = st.executeQuery("select it_item, it_type from item_mstr order by it_item ;");
               while (res.next()) {
                    for (String s : types) {
                    if (! res.getString("it_type").equals(s))
                     myarray.add(res.getString("it_item"));
                    }
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
        return myarray;

    }

    
    public static ArrayList<String[]> getItemsAndPriceByType(String type) {
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
            try{
              
                res = st.executeQuery("select it_item, it_code, it_sell_price, it_pur_price, it_uom, it_desc from item_mstr where it_type = " + "'" + type + "'" +
                        " order by it_item ;");

               while (res.next()) {
                    if (res.getString("it_code").equals("P")) {
                     myarray.add(new String[]{res.getString("it_item"), 
                         res.getString("it_pur_price"),
                         res.getString("it_uom"),
                         res.getString("it_desc")
                     });
                    } else {
                     myarray.add(new String[]{res.getString("it_item"), 
                         res.getString("it_sell_price"),
                         res.getString("it_uom"),
                         res.getString("it_desc")
                     });   
                    }
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
        return myarray;

    }


    public static ArrayList getItemMasterAlllist() {
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
            try{
                
                res = st.executeQuery("select it_item from item_mstr order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

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
        return myarray;

    }

    public static ArrayList getItemMasterListBySite(String site) {
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
            try{
               
                res = st.executeQuery("select it_item from item_mstr " +
                        " where it_site = " + "'" + site + "'" + " order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

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
        return myarray;

    }

    public static ArrayList getItemMasterRawListBySite(String site) {
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
            try{
               
                res = st.executeQuery("select it_item from item_mstr " +
                        " where it_class = 'P' and it_site = " + "'" + site + "'" + " order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

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
        return myarray;

    }


    public static ArrayList getItemRange(String site, String fromitem, String toitem) {
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
            try{
               
                res = st.executeQuery("select it_item from item_mstr where it_item >= " + "'" + fromitem + "'" +
                        " and it_item <= " + "'" + toitem + "'" + 
                        " and it_site = " + "'" + site + "'" + " order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

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
        return myarray;

    }


    public static ArrayList getItemRangeByClass(String site, String fromitem, String toitem, String classcode) {
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
            try{
               
                res = st.executeQuery("select it_item from item_mstr where it_item >= " + "'" + fromitem + "'" +
                        " and it_item <= " + "'" + toitem + "'" + 
                        " and it_code = " + "'" + classcode + "'" +        
                        " and it_site = " + "'" + site + "'" + " order by it_item ;");
               while (res.next()) {
                    myarray.add(res.getString("it_item"));

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
        return myarray;

    }

    public static double getItemLbrCost(String item, String op, String site, String set) {
      double labor = 0;
      try{
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
                    ResultSet res = null;
            try{
               

                res = st.executeQuery("select itr_lbr_top, itr_lbr_low from itemr_cost inner join item_mstr on it_item = itr_item " + 
                        " where itr_item = " + "'" + item + "'" +
                        " AND itr_op = " + "'" + op + "'" + 
                        " AND itr_site = " + "'" + site + "'" + 
                        " AND itr_set = " + "'" + set + "'" +
                        " AND itr_routing = it_wf " +
                        ";");
               while (res.next()) {
                    labor += ( res.getDouble("itr_lbr_top") + res.getDouble("itr_lbr_low") );
                }
               labor = bsParseDouble(currformatDouble(labor));
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
             return labor;
         }

    public static double getItemBdnCost(String item, String op, String site, String set) {
        double burden = 0;
        try{
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
                    ResultSet res = null;
        try{
           
            res = st.executeQuery("select itr_bdn_top, itr_bdn_low from itemr_cost inner join item_mstr on it_item = itr_item " + 
                    " where itr_item = " + "'" + item + "'" +
                    " AND itr_op = " + "'" + op + "'" + 
                    " AND itr_site = " + "'" + site + "'" + 
                    " AND itr_set = " + "'" + set + "'" +
                    " AND itr_routing = it_wf " +
                    ";");
           while (res.next()) {
                burden += ( res.getDouble("itr_bdn_top") + res.getDouble("itr_bdn_low") );
            }
           burden = bsParseDouble(currformatDouble(burden));
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
         return burden;
     }

    public static double getItemQOHBySerial(String item, String site, String serial) {
      double qty = 0;
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

            res = st.executeQuery("select in_qoh from in_mstr where in_item = " + "'" + item + "'" + 
                    " AND in_site = " + "'" + site + "'" +
                    " AND in_serial = " + "'" + serial + "'" +        
                    ";" );
           while (res.next()) {
            qty = res.getDouble("in_qoh");                    
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
    return qty;
}

    public static double getBOMComponentRecursive(String item, String comp, String bom)  { 
        String[] newitem = item.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        if (! bom.isEmpty()) {
        mylist = OVData.getpsmstrlist(newitem[0], bom);
        } else {
        mylist = OVData.getpsmstrlist(newitem[0]);     
        }
        double perqty = 0;
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newitem[0].toUpperCase().toString()) == 0) {
                   if (value[2].toUpperCase().compareTo("M") == 0) {
                    perqty += (getBOMComponentRecursive(value[1] + "___" + value[4] + "___" + value[3], comp, "") * Double.valueOf(value[3]));
                  } else {
                    if (value[1].equals(comp))  {
                       perqty += Double.valueOf(value[3]);
                    }
                  }
              } 
        }
       return perqty;
     }
    
    public static DefaultMutableTreeNode bind_tree_op(String parentpart) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","bind_tree_op"});
            list.add(new String[]{"param1",parentpart});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                DefaultMutableTreeNode r = new DefaultMutableTreeNode();
                if (returnstring != null && ! returnstring.isBlank()) {
                // r = objectMapper.readValue(returnstring, DefaultMutableTreeNode.class); 
                 objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                 r = fromPOJO(objectMapper.readValue(returnstring, TreeConverter.MyNodePOJO.class));
                }
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
          DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(parentpart);
          ArrayList<String> myops = null;
          myops = OVData.getOperationsByItem(parentpart);
          for (String myop : myops) {
              mynode.add(get_nodes_by_op(parentpart, myop));
          }
        
        return mynode;
        
    }
    
    public static DefaultMutableTreeNode get_nodes_by_op(String mypart, String myop)  {
        String pattern = "#0.00000";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern); 
       DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(myop);
        String[] newpart = mypart.split("___");
        ArrayList<String[]> mylist = OVData.getpsmstrlistbyopWCost(newpart[0], myop);
     //   mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String[] myvalue : mylist) {
              if (myvalue[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
               
                  if (myvalue[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                   mfgnode = get_nodes_op(myvalue[1] + "___" + myvalue[4] + "___" + df.format(Double.valueOf(myvalue[3])) + "___" + df.format(Double.valueOf(myvalue[5])));
                   
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(myvalue[1] + "___" + myvalue[4] + "___" + df.format(Double.valueOf(myvalue[3])) + "___" + df.format(Double.valueOf(myvalue[5])));   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     }
      
    public static DefaultMutableTreeNode get_nodes_op(String mypart)  {
        String pattern = "#0.00000";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);  
        DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(mypart);
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        ArrayList<String> myops = new ArrayList<String>();
      //  myops = OVData.getpsmstrlist(newpart[0]);
        mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                    mfgnode = get_nodes_op(value[1] + "___" + value[4] + "___" + df.format(Double.valueOf(value[3])) + "___" + df.format(Double.valueOf(value[5])));
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1] + "___" + value[4] + "___" + df.format(Double.valueOf(value[3])) + "___" + df.format(Double.valueOf(value[5])));   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     }
    
    public static DefaultMutableTreeNode bind_tree(String item, String bomid, Connection bscon)  {  
       if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","bind_tree"});
            list.add(new String[]{"param1",item});
            list.add(new String[]{"param2",bomid});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServINV");
                DefaultMutableTreeNode r = new DefaultMutableTreeNode();
                if (returnstring != null && ! returnstring.isBlank()) {
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    r = fromPOJO(objectMapper.readValue(returnstring, TreeConverter.MyNodePOJO.class));
                }
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        
       DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(item); 
       ArrayList<String> myops = new ArrayList<String>();
        //myops = OVData.getItemRoutingOPs(mypart);  //based on itr_cost
       //  myops = invData._getItemWFOPs(item, bscon);   // based on it_wf and wf_mstr
       
       myops = invData.getRoutingOperationsByBOM(bomid);  // based on the specific BOM id
       System.out.println("HERE BOM: " + bomid + "/" + myops.size());   
        if (bscon == null) {
            if (ds != null) {
                 try {
                     bscon = ds.getConnection();
                 } catch (SQLException ex) {
                     MainFrame.bslog(ex);
                 }
            } else {
                 try {   
                     bscon = DriverManager.getConnection(url + db, user, pass);
                 } catch (SQLException ex) {
                     MainFrame.bslog(ex);
                 }
            }
        }
        
        for ( String myvalue : myops) {
            DefaultMutableTreeNode opnode = new DefaultMutableTreeNode(myvalue);
            opnode = get_nodes_by_op_detail_new(item, item, myvalue, bomid, bscon);
            mynode.add(opnode);
            System.out.println("HERE BOM count: " + + mynode.getChildCount() + "/" + opnode.toString());   
        }
       return mynode;
      }
    
    public static DefaultMutableTreeNode get_nodes_by_op_detail_new(String root, String item, String myop, String rootbom, Connection bscon)  {
        //  bsmf.MainFrame.show(root + "/" + mypart + "/" + myop);
        String myroot = "";
            if (root.toLowerCase().equals(item.toLowerCase()))
            myroot = myop;
        else
            myroot = item;
         DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(myroot);
         
        
        ArrayList<String[]> mylist = OVData._getpsmstrlistbyopnew(item, myop, rootbom, bscon);
     //   mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String[] myvalue : mylist) {
              if (myvalue[0].toUpperCase().compareTo(item.toUpperCase().toString()) == 0) {
               
                  if (myvalue[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode(); 
                    mfgnode = bind_tree(myvalue[1], myvalue[5], bscon);
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(myvalue[1]);   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     } 
     
    public static String[] getBOMParentOpElements(String parent, String op) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBOMParentOpElements"});
            list.add(new String[]{"param1", parent});
            list.add(new String[]{"param2", op});
            try {
                return jsonToStringArray(sendServerPost(list, "", null, "dataServINV"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String[] myarray = new String[10];
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

                res = st.executeQuery("select wf_setup_hours, wf_run_hours, " +
                        " wc_run_rate, wc_run_crew, wc_setup_rate, wc_setup, " +
                        " wc_bdn_rate, wc_cc, wf_desc, wc_desc " +
                        " from wf_mstr " +
                        " inner join wc_mstr on wf_cell = wc_cell " +
                        " inner join item_mstr on it_wf = wf_id " +
                        " where it_item = " + "'" + parent + "'" + 
                        " and wf_op = " + "'" + op + "'" +  ";" );
               
                while (res.next()) {
                myarray = new String[]{
                    res.getString("wc_run_rate"),
                    res.getString("wc_setup_rate"),
                    res.getString("wc_bdn_rate"),
                    res.getString("wc_run_crew"),
                    res.getString("wc_setup"),
                    res.getString("wf_run_hours"),
                    res.getString("wf_setup_hours"),
                    res.getString("wc_cc"),
                    res.getString("wf_desc"),
                    res.getString("wc_desc")
                    };                    
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
    
    
     
    
               
    public record item_mstr(String[] m, String it_item, String it_desc, int it_lotsize,
        double it_sell_price, double it_pur_price, double it_ovh_cost, double it_out_cost,
        double it_mtl_cost, String it_code, String it_type, String it_group,
        String it_prodline, String it_drawing, String it_rev, String it_custrev, String it_wh,
        String it_loc, String it_site, String it_comments, String it_status, String it_uom, 
        double it_net_wt, double it_ship_wt, String it_cont, int it_contqty,
        int it_leadtime, double it_safestock, double it_minordqty, String it_mrp,
        String it_sched, String it_plan, String it_wf, String it_taxcode, String it_createdate,
        String it_expire, int it_expiredays, String it_phantom, String it_label, String it_servicetype) {
        public item_mstr(String[] m) {
            this(m, "", "", 0, 0, 0, 0, 0, 0, "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", 0, 0, "", 0, 0, 0, 0, "", "",
                    "", "", "", "", "", 0, "", "", "");
        }
    }
    
    public record item_cost(String[] m, String itc_item, String itc_site, String itc_set, 
        String itc_total, String itc_mtl_top, String itc_lbr_top, String itc_bdn_top,
        String itc_ovh_top, String itc_out_top, String itc_mtl_low, String itc_lbr_low,
        String itc_bdn_low, String itc_ovh_low, String itc_out_low) {
        public item_cost(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "");
        }
    }
    
    public record itemr_cost(String[] m, String itr_item, String itr_site, String itr_set, 
        String itr_routing, String itr_op, 
        String itr_total, String itr_mtl_top, String itr_lbr_top, String itr_bdn_top,
        String itr_ovh_top, String itr_out_top, String itr_mtl_low, String itr_lbr_low,
        String itr_bdn_low, String itr_ovh_low, String itr_out_low,
        String itr_date, String itr_userid) {
        public itemr_cost(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "");
        }
    }
    

    
    public record pbm_mstr(String[] m, String ps_parent, String ps_child,
        String ps_qty_per, String ps_op, String ps_ref, String ps_type, String ps_bom, 
        String ps_misc1, String ps_desc, String ps_serialized, String ps_routing) {
        public pbm_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", "");
        }
    }
    
    public record bom_mstr(String[] m, String bom_id, String bom_desc,
        String bom_item, String bom_enabled, String bom_primary, String bom_routing) {
        public bom_mstr(String[] m) {
            this(m, "", "", "", "", "", "");
        }
    }
    
    public record wf_mstr(String[] m, String wf_id, String wf_desc,
        String wf_site, String wf_op, String wf_assert, String wf_op_desc,
        String wf_cell, String wf_setup_hours, String wf_run_hours ) {
        public wf_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "");
        }
    }
     
    public record wc_mstr(String[] m, String wc_cell, String wc_desc,
        String wc_site, String wc_cc, String wc_run_rate, String wc_setup_rate,
        String wc_bdn_rate, String wc_run_crew, String wc_setup, String wc_remarks) {
        public wc_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "");
        }
    }
     
    public record loc_mstr(String[] m, String loc_loc, String loc_desc, String loc_site, 
        String loc_wh, String loc_active) {
        public loc_mstr(String[] m) {
            this(m, "", "", "", "", "");
        }
    }

    public record uom_mstr(String[] m, String uom_id, String uom_desc) {
        public uom_mstr(String[] m) {
            this(m, "", "");
        }
    }
    
    public record wh_mstr(String[] m, String wh_id, String wh_site, String wh_name, 
        String wh_addr1, String wh_addr2, String wh_city, 
        String wh_state, String wh_zip, String wh_country, String wh_municipality, String wh_county,
        String wh_email, String wh_phone ) {
        public wh_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "", "", "", "");
        }
    }

    public record pl_mstr(String[] m, String pl_line, String pl_desc, String pl_inventory, 
        String pl_inv_discr, String pl_scrap, String pl_wip, String pl_wip_var, String pl_inv_change, 
        String pl_sales, String pl_sales_disc, String pl_cogs_mtl, String pl_cogs_lbr, 
        String pl_cogs_bdn, String pl_cogs_ovh, String pl_cogs_out, String pl_purchases, 
        String pl_po_rcpt, String pl_po_ovh, String pl_po_pricevar, String pl_ap_usage, 
        String pl_ap_ratevar, String pl_sales_tax, String pl_mtl_usagevar, String pl_mtl_ratevar, 
        String pl_mix_var, String pl_cop, String pl_out_usagevar, String pl_out_ratevar) {
        public pl_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "");
        }
    }
    
    public record inv_ctrl(String[] m, String planmultiscan, String demdtoplan, String printsubticket, 
        String autoitem, String serialize) {
        public inv_ctrl(String[] m) {
            this(m, "", "", "", "", "");
        }
    }   
    
    public record qual_mstr(String[] m, String qual_id, String qual_site,
        String qual_userid, String qual_date_crt, String qual_date_upd, String qual_date_cls, String qual_originator, 
        String qual_vend, String qual_vend_name, String qual_vend_contact, String qual_qpr, String qual_infor,
        String qual_sendsupp, String qual_sort, String qual_rework, String qual_scrap, String qual_dev, String qual_dev_nbr,
        String qual_src_line, String qual_line_dept, String qual_src_recv, String qual_src_cust, String qual_src_eng,
        String qual_src_oth, String qual_src_oth_desc, String qual_int_sup, String qual_ext_sup, String qual_item,
        String qual_item_desc, String qual_qty_rej, String qual_qty_susp, String qual_qty_tot_def, String qual_desc_iss,
        String qual_desc_fin_hist, String qual_desc_sqe_comt, String qual_tot_charge, 
        String qual_dec1, String qual_date1, String qual_int1) {
        
        public qual_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "");
        }
    }

    public record tran_mstr(String[] m, String tr_id, String tr_site, String tr_item, double tr_qty,
        String tr_ent_date, String tr_eff_date, String tr_userid, String tr_ref, String tr_addrcode,
        String tr_type, String tr_datetime, String tr_rmks, String tr_nbr, String tr_misc1, 
        String tr_lot, String tr_serial, String tr_program,
        double tr_amt, double tr_mtl, double tr_lbr, double tr_bdn, double tr_ovh, double tr_out,
        String tr_batch, String tr_op, String tr_loc, String tr_wh, String tr_expire,
        String tr_cc, String tr_wc, String tr_wf, String tr_prodline, String tr_timestamp, 
        String tr_actcell, int tr_export, String tr_order, int tr_line, String tr_po, double tr_price,
        double tr_cost, String tr_acct, String tr_terms, String tr_pack, String tr_curr, 
        String tr_pack_date, String tr_assy_date, String tr_uom, double tr_base_qty, String tr_bom) {
        public tran_mstr(String[] m) {
            this(m, "", "", "", 0, "", "", "", "", "", "",
                    "", "", "", "", "", "", "", 0, 0, 0,
                    0, 0, 0, "", "", "", "", "", "", "",
                    "", "", "", "", 0, "", 0, "", 0, 0,  
                    "", "", "", "", "", "", "", 0, "");
        }
    }

    public record in_mstr(String[] m, String in_item, double in_qoh, String in_date, 
        String in_loc, String in_wh, String in_site, 
        String in_serial, String in_expire, String in_userid, String in_prog) {
        public in_mstr(String[] m) {
            this(m, "", 0, "", "", "", "", "", "", "", "");
        }
    }

     public record conv_mstr(String[] m, String conv_notes, String conv_type, String conv_fromcode, 
        String conv_tocode, double conv_fromamt, double conv_toamt) {
        public conv_mstr(String[] m) {
            this(m, "", "", "", "", 0.00, 0.00);
        }
    }
     
     
}
