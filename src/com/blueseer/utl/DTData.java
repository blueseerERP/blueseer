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
package com.blueseer.utl;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.jsonToDefaultTableModel;
import static com.blueseer.utl.BlueSeerUtils.jsonToHashMapStringInteger;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;

/**
 *
 * @author TerryVa
 */
public class DTData {
    
      
     
    public static DefaultTableModel getAcctBrowseUtil( String str, int state, String myfield) {
        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getAcctBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getAcctBrowseUtilData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("type"), getGlobalColumnTag("currency")})
                {
                      @Override  
                      public Class getColumnClass(int col) {
                    //      return String.class;
                        if (col == 0)        
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }   
                        }; 
        
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        
        return mymodel;
        
        } 
    
    public static String getAcctBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT ac_id, ac_desc, ac_type, ac_cur  " +
                        " FROM  ac_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by ac_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT ac_id, ac_desc, ac_type, ac_cur  " +
                        " FROM  ac_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by ac_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT ac_id, ac_desc, ac_type, ac_cur   " +
                        " FROM  ac_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by ac_id;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("ac_id"));
                        rowArray.put(res.getString("ac_desc"));
                        rowArray.put(res.getString("ac_type"));
                        rowArray.put(res.getString("ac_cur"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getBankBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBankBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getBankBrowseUtilData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("account"), getGlobalColumnTag("currency"), getGlobalColumnTag("active")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getBankBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT bk_id, bk_desc, bk_acct, bk_cur, bk_active " +
                        " FROM  bk_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by bk_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT bk_id, bk_desc, bk_acct, bk_cur, bk_active " +
                        " FROM  bk_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by bk_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT bk_id, bk_desc, bk_acct, bk_cur, bk_active  " +
                        " FROM  bk_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by bk_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("bk_id"));
                        rowArray.put(res.getString("bk_desc"));
                        rowArray.put(res.getString("bk_acct"));
                        rowArray.put(res.getString("bk_cur"));
                        rowArray.put(res.getString("bk_active"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getDeptCCBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDeptCCBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDeptCCBrowseUtilData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("dept"), getGlobalColumnTag("description"), "LaborAcct", "BurdenAccount", "COPAccount"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getDeptCCBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT dept_id, dept_desc, dept_lbr_acct, dept_bdn_acct, dept_cop_acct " +
                        " FROM  dept_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by dept_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT dept_id, dept_desc, dept_lbr_acct, dept_bdn_acct, dept_cop_acct " +
                        " FROM  dept_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by dept_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT dept_id, dept_desc, dept_lbr_acct, dept_bdn_acct, dept_cop_acct  " +
                        " FROM  dept_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by dept_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("dept_id"));
                        rowArray.put(res.getString("dept_desc"));
                        rowArray.put(res.getString("dept_lbr_acct"));
                        rowArray.put(res.getString("dept_bdn_acct"));
                        rowArray.put(res.getString("dept_cop_acct"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getCurrencyBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCurrencyBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCurrencyBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
       
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        
        return mymodel;
        
        } 
    
    public static String getCurrencyBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT cur_id, cur_desc " +
                        " FROM  cur_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cur_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("  SELECT cur_id, cur_desc  " +
                        " FROM  cur_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by cur_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  SELECT cur_id, cur_desc    " +
                        " FROM  cur_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cur_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cur_id"));
                        rowArray.put(res.getString("cur_desc"));
                        jsonarray.put(rowArray);
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
      
    public static DefaultTableModel getGLTranBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGLTranBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGLTranBrowseUtilData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("reference"), getGlobalColumnTag("account"), getGlobalColumnTag("cc"), getGlobalColumnTag("site"), getGlobalColumnTag("effectivedate"), getGlobalColumnTag("enterdate"), getGlobalColumnTag("description"), getGlobalColumnTag("amount"), getGlobalColumnTag("userid")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        } 
        return mymodel;
        
         } 
    
    public static String getGLTranBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_entdate, glt_desc, glt_base_amt, glt_userid " +
                        " FROM  gl_tran where " + myfield + " like " + "'" + str + "%'" +
                        " order by glt_id desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_entdate, glt_desc, glt_base_amt, glt_userid  " +
                        " FROM  gl_tran where " + myfield + " like " + "'%" + str + "'" +
                        " order by glt_id desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_entdate, glt_desc, glt_base_amt, glt_userid  " +
                        " FROM  gl_tran where " + myfield + " like " + "'%" + str + "%'" +
                        " order by glt_id desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("glt_id"));
                        rowArray.put(res.getString("glt_ref"));
                        rowArray.put(res.getString("glt_acct"));
                        rowArray.put(res.getString("glt_cc"));
                        rowArray.put(res.getString("glt_site"));
                        rowArray.put(res.getString("glt_effdate"));
                        rowArray.put(res.getString("glt_entdate"));
                        rowArray.put(res.getString("glt_desc"));
                        rowArray.put(res.getString("glt_base_amt"));
                        rowArray.put(res.getString("glt_userid"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getGLTranBrowseUtil2( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGLTranBrowseUtil2Data"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGLTranBrowseUtil2Data(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("number"), getGlobalColumnTag("account"), getGlobalColumnTag("date"), getGlobalColumnTag("amount")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else if (col == 5) 
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }     
        return mymodel;
        
         } 
    
    public static String getGLTranBrowseUtil2Data(String str, int state, String myfield) {  
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_type, glt_doc, glt_amt  " +
                        " FROM  gl_tran where " + myfield + " like " + "'" + str + "%'" +
                        " order by glt_id desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_type, glt_doc, glt_amt   " +
                        " FROM  gl_tran where " + myfield + " like " + "'%" + str + "'" +
                        " order by glt_id desc ;");
                }
                 if (state == 0) { // match
                    res = st.executeQuery("SELECT glt_id, glt_ref, glt_acct, glt_cc, glt_site, glt_effdate, glt_type, glt_doc, glt_amt " +
                        " FROM  gl_tran where " + myfield + " like " + "'%" + str + "%'" +
                        " order by glt_id desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("glt_id"));
                        rowArray.put(res.getString("glt_doc"));
                        rowArray.put(res.getString("glt_acct"));
                        rowArray.put(res.getString("glt_effdate"));
                        rowArray.put(res.getString("glt_amt"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getItemBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getItemBrowseUtilData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("class"), getGlobalColumnTag("uom"), getGlobalColumnTag("type"), getGlobalColumnTag("status"), getGlobalColumnTag("site"), getGlobalColumnTag("prodline"), getGlobalColumnTag("routing")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getItemBrowseUtilData(String str, int state, String myfield) {  
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
                 if (state == 1) { // begins
                    res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by it_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by it_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by it_item ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("it_uom"));
                        rowArray.put(res.getString("it_type"));
                        rowArray.put(res.getString("it_status"));
                        rowArray.put(res.getString("it_site"));
                        rowArray.put(res.getString("it_prodline"));
                        rowArray.put(res.getString("it_wf"));
                        jsonarray.put(rowArray);
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
   
    
    public static DefaultTableModel getItemMClassBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemMClassBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getItemMClassBrowseUtilData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("class"), getGlobalColumnTag("uom"), getGlobalColumnTag("type"), getGlobalColumnTag("status"), getGlobalColumnTag("site"), getGlobalColumnTag("prodline"), getGlobalColumnTag("routing")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getItemMClassBrowseUtilData(String str, int state, String myfield) {  
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
                 if (state == 1) { // begins
                    res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " and it_code = 'M' " +
                        " order by it_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and it_code = 'M' " +
                        " order by it_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT it_item, it_desc, it_code, it_uom, it_type, it_status, it_site, it_prodline, it_wf  " +
                        " FROM  item_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and it_code = 'M' " +
                        " order by it_item ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("it_uom"));
                        rowArray.put(res.getString("it_type"));
                        rowArray.put(res.getString("it_status"));
                        rowArray.put(res.getString("it_site"));
                        rowArray.put(res.getString("it_prodline"));
                        rowArray.put(res.getString("it_wf"));
                        jsonarray.put(rowArray);
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
       
    
    public static DefaultTableModel getVendBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVendBrowseUtilData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip"), getGlobalColumnTag("country")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getVendBrowseUtilData(String str, int state, String myfield) {  
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
                 if (state == 1) { // begins
                    res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_country  " +
                        " FROM  vd_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by vd_addr ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_country  " +
                        " FROM  vd_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by vd_addr ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip, vd_country   " +
                        " FROM  vd_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by vd_addr ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("vd_addr"));
                        rowArray.put(res.getString("vd_name"));
                        rowArray.put(res.getString("vd_line1"));
                        rowArray.put(res.getString("vd_city"));
                        rowArray.put(res.getString("vd_state"));
                        rowArray.put(res.getString("vd_zip"));
                        rowArray.put(res.getString("vd_country"));
                        jsonarray.put(rowArray);
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
   
    
    public static DefaultTableModel getPOAddrBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPOAddrBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPOAddrBrowseUtilData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("shipto"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip"), getGlobalColumnTag("country")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getPOAddrBrowseUtilData(String str, int state, String myfield) {  
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT poa_code, poa_shipto, poa_name, poa_line1, poa_city, poa_state, poa_zip, poa_country  " +
                        " FROM  po_addr where " + myfield + " like " + "'" + str + "%'" +
                        " order by poa_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT poa_code, poa_shipto, poa_name, poa_line1, poa_city, poa_state, poa_zip, poa_country  " +
                        " FROM  po_addr where " + myfield + " like " + "'%" + str + "'" +
                        " order by poa_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT poa_code, poa_shipto, poa_name, poa_line1, poa_city, poa_state, poa_zip, poa_country  " +
                        " FROM  po_addr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by poa_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("poa_code"));
                        rowArray.put(res.getString("poa_shipto"));
                        rowArray.put(res.getString("poa_name"));
                        rowArray.put(res.getString("poa_line1"));
                        rowArray.put(res.getString("poa_city"));
                        rowArray.put(res.getString("poa_state"));
                        rowArray.put(res.getString("poa_zip"));
                        rowArray.put(res.getString("poa_country"));
                        jsonarray.put(rowArray);
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
   
   
    
    public static DefaultTableModel getMapStructBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getMapStructBrowseData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getMapStructBrowseData(str, state, myfield);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("doctype")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        
        return mymodel;
        
         } 
    
    public static String getMapStructBrowseData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT dfs_id, dfs_desc, dfs_doctype  " +
                        " FROM  dfs_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by dfs_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT dfs_id, dfs_desc, dfs_doctype " +
                        " FROM  dfs_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by dfs_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT dfs_id, dfs_desc, dfs_doctype  " +
                        " FROM  dfs_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by dfs_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("dfs_id"));
                        rowArray.put(res.getString("dfs_desc"));
                        rowArray.put(res.getString("dfs_doctype"));
                        jsonarray.put(rowArray);
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
    
    
     
    public static DefaultTableModel getCronBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCronBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCronBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("group"), getGlobalColumnTag("enabled")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getCronBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT cron_jobid, cron_desc, cron_group, cron_enabled  " +
                        " FROM  cron_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cron_jobid ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT cron_jobid, cron_desc, cron_group, cron_enabled  " +
                        " FROM  cron_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by cron_jobid ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT cron_jobid, cron_desc, cron_group, cron_enabled  " +
                        " FROM  cron_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cron_jobid ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cron_jobid"));
                        rowArray.put(res.getString("cron_desc"));
                        rowArray.put(res.getString("cron_group"));
                        rowArray.put(res.getString("cron_enabled"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getLabelFileUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLabelFileUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getLabelFileUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("type"), getGlobalColumnTag("file")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getLabelFileUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT lblz_code, lblz_desc, lblz_type, lblz_file " +
                        " FROM  label_zebra where " + myfield + " like " + "'" + str + "%'" +
                        " order by lblz_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT lblz_code, lblz_desc, lblz_type, lblz_file  " +
                        " FROM  label_zebra where " + myfield + " like " + "'%" + str + "'" +
                        " order by lblz_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT lblz_code, lblz_code, lblz_type, lblz_file   " +
                        " FROM  label_zebra where " + myfield + " like " + "'%" + str + "%'" +
                        " order by lblz_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("lblz_code"));
                        rowArray.put(res.getString("lblz_desc"));
                        rowArray.put(res.getString("lblz_type"));
                        rowArray.put(res.getString("lblz_file"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getPanelBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPanelBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPanelBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("class"), getGlobalColumnTag("description"), getGlobalColumnTag("system")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 

    public static String getPanelBrowseUtilData(String str, int state, String myfield) {
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
                 if (state == 1) { // begins
                    res = st.executeQuery("SELECT panel_id, panel_desc, panel_core  " +
                        " FROM  panel_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by panel_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT panel_id, panel_desc, panel_core  " +
                        " FROM  panel_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by panel_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT panel_id, panel_desc, panel_core   " +
                        " FROM  panel_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by panel_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("panel_id"));
                        rowArray.put(res.getString("panel_desc"));
                        rowArray.put(res.getString("panel_core"));
                        jsonarray.put(rowArray);
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
        
    
    public static DefaultTableModel getKeyBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getKeyBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getKeyBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("key"), getGlobalColumnTag("description"), getGlobalColumnTag("from"), getGlobalColumnTag("to"), getGlobalColumnTag("number")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
       
    public static String getKeyBrowseUtilData(String str, int state, String myfield) {
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
                 if (state == 1) { // begins
                    res = st.executeQuery("SELECT counter_name, counter_desc, counter_from, counter_to, counter_id  " +
                        " FROM  counter where " + myfield + " like " + "'" + str + "%'" +
                        " order by counter_name ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT counter_name, counter_desc, counter_from, counter_to, counter_id  " +
                        " FROM  counter where " + myfield + " like " + "'%" + str + "'" +
                        " order by counter_name ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT counter_name, counter_desc, counter_from, counter_to, counter_id  " +
                        " FROM  counter where " + myfield + " like " + "'%" + str + "%'" +
                        " order by counter_name ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("counter_name"));
                        rowArray.put(res.getString("counter_desc"));
                        rowArray.put(res.getString("counter_from"));
                        rowArray.put(res.getString("counter_to"));
                        rowArray.put(res.getString("counter_id"));
                        jsonarray.put(rowArray);
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
     
    
    public static DefaultTableModel getPksBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPksBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPksBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("key"), getGlobalColumnTag("description"), getGlobalColumnTag("type"), getGlobalColumnTag("user"), getGlobalColumnTag("file")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getPksBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT pks_id, pks_desc, pks_type, pks_user, pks_file  " +
                        " FROM  pks_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by pks_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT pks_id, pks_desc, pks_type, pks_user, pks_file  " +
                        " FROM  pks_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by pks_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT pks_id, pks_desc, pks_type, pks_user, pks_file  " +
                        " FROM  pks_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by pks_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("pks_id"));
                        rowArray.put(res.getString("pks_desc"));
                        rowArray.put(res.getString("pks_type"));
                        rowArray.put(res.getString("pks_user"));
                        rowArray.put(res.getString("pks_file"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getSiteBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSiteBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getSiteBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("site"), getGlobalColumnTag("description"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip"), getGlobalColumnTag("image")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 

    public static String getSiteBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT site_site, site_desc, site_line1, site_city, site_state, site_zip, site_logo  " +
                        " FROM  site_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by site_site limit 300 ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT site_site, site_desc, site_line1, site_city, site_state, site_zip, site_logo   " +
                        " FROM  site_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by site_site limit 300 ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT site_site, site_desc, site_line1, site_city, site_state, site_zip, site_logo   " +
                        " FROM  site_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by site_site limit 300 ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("site_site"));
                        rowArray.put(res.getString("site_desc"));
                        rowArray.put(res.getString("site_line1"));
                        rowArray.put(res.getString("site_city"));
                        rowArray.put(res.getString("site_state"));
                        rowArray.put(res.getString("site_zip"));
                        rowArray.put(res.getString("site_logo"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getCustBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCustBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip"), getGlobalColumnTag("country")}){
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getCustBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip, cm_country  " +
                        " FROM  cm_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cm_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip, cm_country  " +
                        " FROM  cm_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by cm_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT cm_code, cm_name, cm_line1, cm_city, cm_state, cm_zip, cm_country   " +
                        " FROM  cm_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cm_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("cm_line1"));
                        rowArray.put(res.getString("cm_city"));
                        rowArray.put(res.getString("cm_state"));
                        rowArray.put(res.getString("cm_zip"));
                        rowArray.put(res.getString("cm_country"));
                        jsonarray.put(rowArray);
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
    
         
    public static DefaultTableModel getRoutingBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getRoutingBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getRoutingBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("operation"), getGlobalColumnTag("cell"), getGlobalColumnTag("description"), getGlobalColumnTag("runhours"), getGlobalColumnTag("setuphours"), getGlobalColumnTag("enabled")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getRoutingBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT wf_id, wf_op, wf_desc, wf_cell, wf_op_desc, wf_run_hours, wf_setup_hours, wf_assert  " +
                        " FROM  wf_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by wf_id, wf_op ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT wf_id, wf_op, wf_desc, wf_cell, wf_op_desc, wf_run_hours, wf_setup_hours, wf_assert  " +
                        " FROM  wf_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by wf_id, wf_op ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT wf_id, wf_op, wf_desc, wf_cell, wf_op_desc, wf_run_hours, wf_setup_hours, wf_assert  " +
                        " FROM  wf_mstr where "+ myfield + " like " + "'%" + str + "%'" +
                        " order by wf_id, wf_op ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("wf_id"));
                        rowArray.put(res.getString("wf_op"));
                        rowArray.put(res.getString("wf_desc"));
                        rowArray.put(res.getString("wf_cell"));
                        rowArray.put(res.getString("wf_op_desc"));
                        rowArray.put(res.getString("wf_run_hours"));
                        rowArray.put(res.getString("wf_setup_hours"));
                        rowArray.put(res.getString("wf_assert"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getWorkCenterBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWorkCenterBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getWorkCenterBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("cell"),  getGlobalColumnTag("description"), getGlobalColumnTag("dept"), getGlobalColumnTag("runrate"), getGlobalColumnTag("setuprate"), getGlobalColumnTag("burdenrate")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
     
    public static String getWorkCenterBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT wc_cell,  wc_desc, wc_cc, wc_run_rate, wc_setup_rate, wc_bdn_rate  " +
                        " FROM  wc_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by wc_cell ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT wc_cell,  wc_desc, wc_cc, wc_run_rate, wc_setup_rate, wc_bdn_rate  " +
                        " FROM  wc_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by wc_cell ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT wc_cell,  wc_desc, wc_cc, wc_run_rate, wc_setup_rate, wc_bdn_rate  " +
                        " FROM  wc_mstr where "+ myfield + " like " + "'%" + str + "%'" +
                        " order by wc_cell ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("wc_cell"));
                        rowArray.put(res.getString("wc_desc"));
                        rowArray.put(res.getString("wc_cc"));
                        rowArray.put(res.getString("wc_run_rate"));
                        rowArray.put(res.getString("wc_setup_rate"));
                        rowArray.put(res.getString("wc_bdn_rate"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getShiftBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShiftBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getShiftBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), "ShiftID", "ShiftDesc"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getShiftBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT shf_id, shf_desc " +
                        " FROM  shift_mstr  where " + myfield + " like " + "'" + str + "%'" +
                        " order by shf_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT shf_id, shf_desc " +
                        " FROM  shift_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by shf_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT shf_id, shf_desc   " +
                        " FROM  shift_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by shf_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("shf_id"));
                        rowArray.put(res.getString("shf_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getUOMBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getUOMBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getUOMBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag(getGlobalColumnTag("uom")), getGlobalColumnTag("description")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
      
    public static String getUOMBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT uom_id, uom_desc " +
                        " FROM  uom_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by uom_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT uom_id, uom_desc " +
                        " FROM  uom_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by uom_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT uom_id, uom_desc   " +
                        " FROM  uom_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by uom_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("uom_id"));
                        rowArray.put(res.getString("uom_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getGLICBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGLICBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGLICBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag(getGlobalColumnTag("code")), getGlobalColumnTag("name")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
      
    public static String getGLICBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT glic_profile, glic_name " +
                        " FROM  glic_def where " + myfield + " like " + "'" + str + "%'" +
                        " order by glic_profile ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT glic_profile, glic_name " +
                        " FROM  glic_def  where " + myfield + " like " + "'%" + str + "'" +
                        " order by glic_profile ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT glic_profile, glic_name  " +
                        " FROM  glic_def  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by glic_profile ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("glic_profile"));
                        rowArray.put(res.getString("glic_name"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getTXTBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getTXTBrowseUtil"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getTXTBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag(getGlobalColumnTag("id")), getGlobalColumnTag("type"), getGlobalColumnTag("key")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
      
    public static String getTXTBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT txt_id, txt_type, txt_key " +
                        " FROM  txt_meta where " + myfield + " like " + "'" + str + "%'" +
                        " order by txt_id, txt_type ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT txt_id, txt_type, txt_key " +
                        " FROM  txt_meta  where " + myfield + " like " + "'%" + str + "'" +
                        " order by txt_id, txt_type;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT txt_id, txt_type, txt_key  " +
                        " FROM  txt_meta  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by txt_id, txt_type ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("txt_id"));
                        rowArray.put(res.getString("txt_type"));
                        rowArray.put(res.getString("txt_key"));
                        jsonarray.put(rowArray);
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
    
    
    
    /*
    public static DefaultTableModel getUOMConvBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getUOMConvBrowseUtil"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getUOMConvBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), 
                          getGlobalColumnTag("uom"), 
                          getGlobalColumnTag("uom"), 
                          getGlobalColumnTag("value"),
                          getGlobalColumnTag("value")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
      
    public static String getUOMConvBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt " +
                        " FROM  conv_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by conv_fromcode ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt " +
                        " FROM  conv_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by conv_fromcode ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt   " +
                        " FROM  conv_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by conv_fromcode ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("conv_fromcode"));
                        rowArray.put(res.getString("conv_tocode"));
                        rowArray.put(res.getString("conv_fromamt"));
                        rowArray.put(res.getString("conv_toamt"));
                        jsonarray.put(rowArray);
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
    */
    public static DefaultTableModel getJobBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getJobBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getJobBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("order"), getGlobalColumnTag("customer")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
    
    public static String getJobBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT plan_nbr, plan_order, sv_cust " +
                        " FROM  plan_mstr  left outer join sv_mstr on sv_nbr = plan_order " +
                        " where " + myfield + " like " + "'" + str + "%'" +
                        " order by plan_nbr ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT plan_nbr, plan_order, sv_cust " +
                        " FROM  plan_mstr  left outer join sv_mstr on sv_nbr = plan_order " +
                        " where " + myfield + " like " + "'%" + str + "'" +
                        " order by plan_nbr ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT plan_nbr, plan_order, sv_cust   " +
                         " FROM  plan_mstr  left outer join sv_mstr on sv_nbr = plan_order " +
                        " where " + myfield + " like " + "'%" + str + "%'" +
                        " order by plan_nbr ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("plan_nbr"));
                        rowArray.put(res.getString("plan_order"));
                        rowArray.put(res.getString("sv_cust"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getJobSRVCBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getJobSRVCBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getJobSRVCBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("order"), getGlobalColumnTag("customer")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
    
    public static String getJobSRVCBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT plan_nbr, plan_rmks, plan_order, sv_cust " +
                        " FROM  plan_mstr  left outer join sv_mstr on sv_nbr = plan_order " +
                        " where plan_type = 'SRVC' and " + myfield + " like " + "'" + str + "%'" +
                        " order by plan_nbr ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT plan_nbr, plan_rmks, plan_order, sv_cust " +
                        " FROM  plan_mstr  left outer join sv_mstr on sv_nbr = plan_order " +
                        " where plan_type = 'SRVC' and " + myfield + " like " + "'%" + str + "'" +
                        " order by plan_nbr ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT plan_nbr, plan_rmks, plan_order, sv_cust   " +
                         " FROM  plan_mstr  left outer join sv_mstr on sv_nbr = plan_order " +
                        " where plan_type = 'SRVC' and " + myfield + " like " + "'%" + str + "%'" +
                        " order by plan_nbr ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("plan_nbr"));
                        rowArray.put(res.getString("plan_rmks"));
                        rowArray.put(res.getString("plan_order"));
                        rowArray.put(res.getString("sv_cust"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getQPRBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getQPRBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getQPRBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), 
                          getGlobalColumnTag("id"), 
                          getGlobalColumnTag("item"),  
                          getGlobalColumnTag("description"), 
                          getGlobalColumnTag("vendor"), 
                          getGlobalColumnTag("userid"), 
                          getGlobalColumnTag("createdate"), 
                          getGlobalColumnTag("closedate")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
    
    public static String getQPRBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT qual_id, qual_item, qual_item_desc, qual_vend, qual_userid, qual_date_crt, qual_date_cls " +
                        " FROM  qual_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by qual_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT qual_id, qual_item, qual_item_desc, qual_vend, qual_userid, qual_date_crt, qual_date_cls " +
                        " FROM  qual_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by qual_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT qual_id, qual_item, qual_item_desc, qual_vend, qual_userid, qual_date_crt, qual_date_cls " +
                        " FROM  qual_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by qual_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("qual_id"));
                        rowArray.put(res.getString("qual_item"));
                        rowArray.put(res.getString("qual_item_desc"));
                        rowArray.put(res.getString("qual_vend"));
                        rowArray.put(res.getString("qual_userid"));
                        rowArray.put(res.getString("qual_date_crt"));
                        rowArray.put(res.getString("qual_date_cls"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getPrinterBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPrinterBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPrinterBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("type"), getGlobalColumnTag("ip"), getGlobalColumnTag("port")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
      
    public static String getPrinterBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT * " +
                        " FROM  prt_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by prt_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT * " +
                        " FROM  prt_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by prt_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT * " +
                        " FROM  prt_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by prt_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("prt_id"));
                        rowArray.put(res.getString("prt_desc"));
                        rowArray.put(res.getString("prt_type"));
                        rowArray.put(res.getString("prt_ip"));
                        rowArray.put(res.getString("prt_port"));
                        jsonarray.put(rowArray);
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
       
    
    public static DefaultTableModel getExchangeBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getExchangeBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getExchangeBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("foreign"), getGlobalColumnTag("rate")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
    
    public static String getExchangeBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT exc_base, exc_foreign, exc_rate " +
                        " FROM  exc_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by exc_base ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("  SELECT exc_base, exc_foreign, exc_rate  " +
                        " FROM  exc_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by exc_base ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  SELECT exc_base, exc_foreign, exc_rate   " +
                        " FROM  exc_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by exc_base ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("exc_base"));
                        rowArray.put(res.getString("exc_foreign"));
                        rowArray.put(res.getString("exc_rate"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getUOMConvBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getUOMConvBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getUOMConvBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), 
                          getGlobalColumnTag("key") + getGlobalColumnTag("1"), 
                          getGlobalColumnTag("key") + getGlobalColumnTag("2"),
                          getGlobalColumnTag("key") + getGlobalColumnTag("amount") + getGlobalColumnTag("1"),
                          getGlobalColumnTag("key") + getGlobalColumnTag("amount") + getGlobalColumnTag("2"),
                          getGlobalColumnTag("type"), 
                          getGlobalColumnTag("remarks")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }     
    
    public static String getUOMConvBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt, conv_type, conv_notes " +
                        " FROM  conv_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by conv_fromcode ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt, conv_type, conv_notes " +
                        " FROM  conv_mstr  where " + myfield + " like " + "'%" + str + "'" +
                        " order by conv_fromcode ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  SELECT conv_fromcode, conv_tocode, conv_fromamt, conv_toamt, conv_type, conv_notes    " +
                        " FROM  conv_mstr  where " + myfield + " like " + "'%" + str + "%'" +
                        " order by conv_fromcode ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("conv_fromcode"));
                        rowArray.put(res.getString("conv_tocode"));
                        rowArray.put(res.getString("conv_fromamt"));
                        rowArray.put(res.getString("conv_toamt"));
                        rowArray.put(res.getString("conv_type"));
                        rowArray.put(res.getString("conv_notes"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getECNBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getECNBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getECNBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("number"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("user"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getECNBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT ecn_nbr, ecn_mstrtask, task_desc, ecn_poc, ecn_status " +
                        " FROM  ecn_mstr inner join task_mstr on task_mstr.task_id = ecn_mstr.ecn_mstrtask  where " + myfield + " like " + "'" + str + "%'" +
                        " order by ecn_nbr ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT ecn_nbr, ecn_mstrtask, task_desc, ecn_poc, ecn_status  " +
                        " FROM  ecn_mstr inner join task_mstr on task_mstr.task_id = ecn_mstr.ecn_mstrtask  where " + myfield + " like " + "'%" + str + "'" +
                        " order by ecn_nbr ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT ecn_nbr, ecn_mstrtask, task_desc, ecn_poc, ecn_status   " +
                        " FROM  ecn_mstr inner join task_mstr on task_mstr.task_id = ecn_mstr.ecn_mstrtask where " + myfield + " like " + "'%" + str + "%'" +
                        " order by ecn_nbr ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ecn_nbr"));
                        rowArray.put(res.getString("ecn_mstrtask"));
                        rowArray.put(res.getString("task_desc"));
                        rowArray.put(res.getString("ecn_poc"));
                        rowArray.put(res.getString("ecn_status"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getTaskBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getTaskBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getTaskBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("class"), getGlobalColumnTag("user"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getTaskBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select task_id, task_desc, task_class, task_creator, task_status " +
                        " FROM  task_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by task_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select task_id, task_desc, task_class, task_creator, task_status " +
                        " FROM  task_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by task_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select task_id, task_desc, task_class, task_creator, task_status  " +
                        " FROM  task_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by task_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("task_id"));
                        rowArray.put(res.getString("task_desc"));
                        rowArray.put(res.getString("task_class"));
                        rowArray.put(res.getString("task_creator"));
                        rowArray.put(res.getString("task_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getAPIBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getAPIBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getAPIBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getAPIBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select api_id, api_desc " +
                        " FROM  api_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by api_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select api_id, api_desc " +
                        " FROM  api_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by api_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select api_id, api_desc   " +
                        " FROM  api_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by api_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("api_id"));
                        rowArray.put(res.getString("api_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getAS2BrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getAS2BrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getAS2BrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getAS2BrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select as2_id, as2_desc " +
                        " FROM  as2_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by as2_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select as2_id, as2_desc " +
                        " FROM  as2_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by as2_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select as2_id, as2_desc   " +
                        " FROM  as2_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by as2_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("as2_id"));
                        rowArray.put(res.getString("as2_desc"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getFreightBrowseUtil(String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("enabled")})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getFreightBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                res = st.executeQuery(" select * " +
                        " FROM  car_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by car_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select * " +
                        " FROM  car_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by car_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select *  " +
                        " FROM  car_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by car_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("car_id"));
                        rowArray.put(res.getString("car_desc"));
                        rowArray.put(res.getString("car_apply"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getVehicleBrowseUtil(String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVehicleBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVehicleBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("make"), getGlobalColumnTag("model"), getGlobalColumnTag("year")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 
             
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getVehicleBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                res = st.executeQuery(" select veh_id, veh_desc, veh_make, veh_model, veh_year " +
                        " FROM  veh_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by veh_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select veh_id, veh_desc, veh_make, veh_model, veh_year " +
                        " FROM  veh_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by veh_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select veh_id, veh_desc, veh_make, veh_model, veh_year  " +
                        " FROM  veh_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by veh_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("veh_id"));
                        rowArray.put(res.getString("veh_desc"));
                        rowArray.put(res.getString("veh_make"));
                        rowArray.put(res.getString("veh_model"));
                        rowArray.put(res.getString("veh_year"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getDriverBrowseUtil(String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDriverBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDriverBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("city"), getGlobalColumnTag("state")})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getDriverBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                res = st.executeQuery(" select * " +
                        " FROM  drv_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by drv_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select * " +
                        " FROM  drv_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by drv_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select *  " +
                        " FROM  drv_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by drv_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("drv_id"));
                        rowArray.put(res.getString("drv_lname"));
                        rowArray.put(res.getString("drv_fname"));
                        rowArray.put(res.getString("drv_city"));
                        rowArray.put(res.getString("drv_state"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getBrokerBrowseUtil(String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDriverBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDriverBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);      
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("name"), getGlobalColumnTag("city"), getGlobalColumnTag("state")})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getBrokerBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                res = st.executeQuery(" select * " +
                        " FROM  brk_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by brk_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select * " +
                        " FROM  brk_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by brk_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select *  " +
                        " FROM  brk_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by brk_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("brk_id"));
                        rowArray.put(res.getString("brk_name"));
                        rowArray.put(res.getString("brk_city"));
                        rowArray.put(res.getString("brk_state"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getWkfMstrBrowseUtil(String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWkfMstrBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getWkfMstrBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);     
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("enabled")})
                      {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getWkfMstrBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                res = st.executeQuery(" select * " +
                        " FROM  wkf_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by wkf_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select * " +
                        " FROM  wkf_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by wkf_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select *  " +
                        " FROM  wkf_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by wkf_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("wkf_id"));
                        rowArray.put(res.getString("wkf_desc"));
                        rowArray.put(res.getString("wkf_enabled"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getTaxBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getTaxBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getTaxBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);  
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("userid")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
     
    public static String getTaxBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select tax_code, tax_desc, tax_userid " +
                        " FROM  tax_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by tax_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select tax_code, tax_desc, tax_userid " +
                        " FROM  tax_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by tax_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select tax_code, tax_desc, tax_userid  " +
                        " FROM  tax_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by tax_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("tax_code"));
                        rowArray.put(res.getString("tax_desc"));
                        rowArray.put(res.getString("tax_userid"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getDocRulesBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDocRulesBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDocRulesBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getDocRulesBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select edd_id, edd_desc " +
                        " FROM  edi_doc where " + myfield + " like " + "'" + str + "%'" +
                        " order by edd_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select edd_id, edd_desc " +
                        " FROM  edi_doc where " + myfield + " like " + "'%" + str + "'" +
                        " order by edd_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select edd_id, edd_desc " +
                        " FROM  edi_doc where " + myfield + " like " + "'%" + str + "%'" +
                        " order by edd_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("edd_id"));
                        rowArray.put(res.getString("edd_desc"));
                        jsonarray.put(rowArray);
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
        
   
    public static DefaultTableModel getGenCodeBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGenCodeBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGenCodeBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("key1"), getGlobalColumnTag("key2"), getGlobalColumnTag("value")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getGenCodeBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by code_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by code_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by code_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("code_code"));
                        rowArray.put(res.getString("code_key"));
                        rowArray.put(res.getString("code_value"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getFreightCodeBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightCodeBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightCodeBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("key1"), getGlobalColumnTag("key2"), getGlobalColumnTag("value")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getFreightCodeBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select freight_code, freight_key, freight_value " +
                        " FROM  code_freight where " + myfield + " like " + "'" + str + "%'" +
                        " order by freight_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select freight_code, freight_key, freight_value " +
                        " FROM  code_freight where " + myfield + " like " + "'%" + str + "'" +
                        " order by freight_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select freight_code, freight_key, freight_value " +
                        " FROM  code_freight where " + myfield + " like " + "'%" + str + "%'" +
                        " order by freight_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("freight_code"));
                        rowArray.put(res.getString("freight_key"));
                        rowArray.put(res.getString("freight_value"));
                        jsonarray.put(rowArray);
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
    
    
   
    
    public static DefaultTableModel getEDIXrefBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIXrefBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDIXrefBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), "partner GSID", "system GSID", getGlobalColumnTag("type"), getGlobalColumnTag("partner addrid"), getGlobalColumnTag("system addrid")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getEDIXrefBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select * " +
                        " FROM  edi_xref where " + myfield + " like " + "'" + str + "%'" +
                        " order by exr_bsgs ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select * " +
                        " FROM  edi_xref where " + myfield + " like " + "'%" + str + "'" +
                        " order by exr_bsgs ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select * " +
                        " FROM  edi_xref where " + myfield + " like " + "'%" + str + "%'" +
                        " order by exr_bsgs ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("exr_tpgs"));
                        rowArray.put(res.getString("exr_bsgs"));
                        rowArray.put(res.getString("exr_type"));
                        rowArray.put(res.getString("exr_tpaddr"));
                        rowArray.put(res.getString("exr_bsaddr"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getJaspRptBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getJaspRptBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getJaspRptBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("group"), getGlobalColumnTag("sequence"), getGlobalColumnTag("title"), getGlobalColumnTag("code")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 

    public static String getJaspRptBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select jasp_group, jasp_desc, jasp_sequence, jasp_format " +
                        " FROM  jasp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by jasp_sequence ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select jasp_group, jasp_desc, jasp_sequence, jasp_format " +
                        " FROM  jasp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by jasp_sequence ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select jasp_group, jasp_desc, jasp_sequence, jasp_format  " +
                        " FROM  jasp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by jasp_sequence ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("jasp_group"));
                        rowArray.put(res.getString("jasp_desc"));
                        rowArray.put(res.getString("jasp_sequence"));
                        rowArray.put(res.getString("jasp_format"));
                        jsonarray.put(rowArray);
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
        
    
    public static DefaultTableModel getFctMstrBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFctMstrBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFctMstrBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("site"), getGlobalColumnTag("year")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
     
    public static String getFctMstrBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select fct_item, fct_site, fct_year " +
                        " FROM  fct_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by fct_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("  select fct_item, fct_site, fct_year " +
                        " FROM  fct_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by fct_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select fct_item, fct_site, fct_year " +
                        " FROM  fct_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by fct_item ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("fct_item"));
                        rowArray.put(res.getString("fct_site"));
                        rowArray.put(res.getString("fct_year"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getCustXrefBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustXrefBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCustXrefBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("customer"), getGlobalColumnTag("custitem"), getGlobalColumnTag("item")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
      
    public static String getCustXrefBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cup_cust, cup_citem ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by cup_cust, cup_citem ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cup_cust, cup_citem ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cup_cust"));
                        rowArray.put(res.getString("cup_citem"));
                        rowArray.put(res.getString("cup_item"));
                        jsonarray.put(rowArray);
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
    
      
    
    public static DefaultTableModel getVendXrefBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendXrefBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVendXrefBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("vendor"), getGlobalColumnTag("venditem"), getGlobalColumnTag("item")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
      
    public static String getVendXrefBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by vdp_vend, vdp_vitem ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by vdp_vend, vdp_vitem ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by vdp_vend, vdp_vitem ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vdp_vend"));
                        rowArray.put(res.getString("vdp_vitem"));
                        rowArray.put(res.getString("vdp_item"));
                        jsonarray.put(rowArray);
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
    
   
    public static DefaultTableModel getVendPriceBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendPriceBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVendPriceBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("vendor"), getGlobalColumnTag("item"), getGlobalColumnTag("uom"), getGlobalColumnTag("currency"), getGlobalColumnTag("price")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
         
    public static String getVendPriceBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select vpr_vend, vpr_item, vpr_uom, vpr_curr, vpr_price " +
                        " FROM  vpr_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by vpr_vend, vpr_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select vpr_vend, vpr_item, vpr_uom, vpr_curr, vpr_price " +
                        " FROM  vpr_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by vpr_vend, vpr_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select vpr_vend, vpr_item, vpr_uom, vpr_curr, vpr_price " +
                        " FROM  vpr_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by vpr_vend, vpr_item ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vpr_vend"));
                        rowArray.put(res.getString("vpr_item"));
                        rowArray.put(res.getString("vpr_uom"));
                        rowArray.put(res.getString("vpr_curr"));
                        rowArray.put(res.getString("vpr_price"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getCustPriceBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustPriceBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCustPriceBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("customer"), getGlobalColumnTag("item"), getGlobalColumnTag("uom"), getGlobalColumnTag("currency"), getGlobalColumnTag("price")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
     
    public static String getCustPriceBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select cpr_cust, cpr_item, cpr_uom, cpr_curr, cpr_price " +
                        " FROM  cpr_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cpr_cust, cpr_item ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select cpr_cust, cpr_item, cpr_uom, cpr_curr, cpr_price " +
                        " FROM  cpr_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by cpr_cust, cpr_item ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select cpr_cust, cpr_item, cpr_uom, cpr_curr, cpr_price " +
                        " FROM  cpr_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cpr_cust, cpr_item ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cpr_cust"));
                        rowArray.put(res.getString("cpr_item"));
                        rowArray.put(res.getString("cpr_uom"));
                        rowArray.put(res.getString("cpr_curr"));
                        rowArray.put(res.getString("cpr_price"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getPayProfileBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPayProfileBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPayProfileBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("userid")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
         
    public static String getPayProfileBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select payp_code, payp_desc, payp_userid " +
                        " FROM  pay_profile where " + myfield + " like " + "'" + str + "%'" +
                        " order by payp_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select payp_code, payp_desc, payp_userid " +
                        " FROM  pay_profile where " + myfield + " like " + "'%" + str + "'" +
                        " order by payp_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select payp_code, payp_desc, payp_userid   " +
                        " FROM  pay_profile where " + myfield + " like " + "'%" + str + "%'" +
                        " order by payp_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("payp_code"));
                        rowArray.put(res.getString("payp_desc"));
                        rowArray.put(res.getString("payp_userid"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getPayRollBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPayRollBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPayRollBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("site"), getGlobalColumnTag("description"), getGlobalColumnTag("startdate"), getGlobalColumnTag("enddate"), getGlobalColumnTag("date"), getGlobalColumnTag("userid")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
     
    public static String getPayRollBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select * " +
                        " FROM  pay_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by py_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select * " +
                        " FROM  pay_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by py_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select *  " +
                        " FROM  pay_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by py_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("py_id"));
                        rowArray.put(res.getString("py_site"));
                        rowArray.put(res.getString("py_desc"));
                        rowArray.put(res.getString("py_startdate"));
                        rowArray.put(res.getString("py_enddate"));
                        rowArray.put(res.getString("py_paydate"));
                        rowArray.put(res.getString("py_userid"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getEDITPBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDITPBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDITPBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("user"), getGlobalColumnTag("web"), getGlobalColumnTag("phone")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getEDITPBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select editp_id, editp_name, editp_contact, editp_web, editp_helpdesk " +
                        " FROM  editp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by editp_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select editp_id, editp_name, editp_contact, editp_web, editp_helpdesk " +
                        " FROM  editp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by editp_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select editp_id, editp_name, editp_contact, editp_web, editp_helpdesk  " +
                        " FROM  editp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by editp_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("editp_id"));
                        rowArray.put(res.getString("editp_name"));
                        rowArray.put(res.getString("editp_contact"));
                        rowArray.put(res.getString("editp_web"));
                        rowArray.put(res.getString("editp_helpdesk"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getEDITPDOCBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDITPDOCBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDITPDOCBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("doc"), getGlobalColumnTag("map")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }  
      
    public static String getEDITPDOCBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select edi_id, edi_doc,  edi_map " +
                        " FROM  edi_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by edi_id, edi_doc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("  select edi_id, edi_doc,  edi_map " +
                        " FROM  edi_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by edi_id, edi_doc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  select edi_id, edi_doc,  edi_map  " +
                        " FROM  edi_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by edi_id, edi_doc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("edi_id"));
                        rowArray.put(res.getString("edi_doc"));
                        rowArray.put(res.getString("edi_map"));
                        jsonarray.put(rowArray);
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
        
    
    public static DefaultTableModel getTermBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getTermBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getTermBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("days"), getGlobalColumnTag("discount") + " " + getGlobalColumnTag("days"), getGlobalColumnTag("percent"), getGlobalColumnTag("discount") + " " + getGlobalColumnTag("start"), getGlobalColumnTag("due") + " " + getGlobalColumnTag("start")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
          
    public static String getTermBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent, cut_discstart, cut_duestart " +
                        " FROM  cust_term where " + myfield + " like " + "'" + str + "%'" +
                        " order by cut_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent, cut_discstart, cut_duestart" +
                        " FROM  cust_term where " + myfield + " like " + "'%" + str + "'" +
                        " order by cut_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent, cut_discstart, cut_duestart  " +
                        " FROM  cust_term where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cut_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cut_code"));
                        rowArray.put(res.getString("cut_desc"));
                        rowArray.put(res.getString("cut_days"));
                        rowArray.put(res.getString("cut_discdays"));
                        rowArray.put(res.getString("cut_discpercent"));
                        rowArray.put(res.getString("cut_discstart"));
                        rowArray.put(res.getString("cut_duestart"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getOrderBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOrderBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getOrderBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("order"), getGlobalColumnTag("customer"), getGlobalColumnTag("shipcode"), getGlobalColumnTag("po"), getGlobalColumnTag("orderdate"), getGlobalColumnTag("duedate"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
     
    public static String getOrderBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status " +
                        " FROM  so_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by so_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status " +
                        " FROM  so_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by so_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status  " +
                        " FROM  so_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by so_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("so_nbr"));
                        rowArray.put(res.getString("so_cust"));
                        rowArray.put(res.getString("so_ship"));
                        rowArray.put(res.getString("so_po"));
                        rowArray.put(res.getString("so_ord_date"));
                        rowArray.put(res.getString("so_due_date"));
                        rowArray.put(res.getString("so_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getOpenOrderBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOpenOrderBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getOpenOrderBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("order"), getGlobalColumnTag("customer"), getGlobalColumnTag("shipcode"), getGlobalColumnTag("po"), getGlobalColumnTag("orderdate"), getGlobalColumnTag("duedate"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getOpenOrderBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status " +
                        " FROM  so_mstr where (so_status = 'open' or so_status = 'partial') and " + myfield + " like " + "'" + str + "%'" +
                        " order by so_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status " +
                        " FROM  so_mstr where (so_status = 'open' or so_status = 'partial') and " + myfield + " like " + "'%" + str + "'" +
                        " order by so_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, so_status  " +
                        " FROM  so_mstr where (so_status = 'open' or so_status = 'partial') and " + myfield + " like " + "'%" + str + "%'" +
                        " order by so_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("so_nbr"));
                        rowArray.put(res.getString("so_cust"));
                        rowArray.put(res.getString("so_ship"));
                        rowArray.put(res.getString("so_po"));
                        rowArray.put(res.getString("so_ord_date"));
                        rowArray.put(res.getString("so_due_date"));
                        rowArray.put(res.getString("so_status"));
                        jsonarray.put(rowArray);
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
    
    
    
    
    public static DefaultTableModel getPOBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPOBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPOBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("po"), getGlobalColumnTag("vendor"), getGlobalColumnTag("site"), getGlobalColumnTag("remarks"), getGlobalColumnTag("orderdate"), getGlobalColumnTag("duedate"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
           
    public static String getPOBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select po_nbr, po_vend, po_site, po_rmks, po_ord_date, po_due_date, po_status " +
                        " FROM  po_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by po_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select po_nbr, po_vend, po_site, po_rmks, po_ord_date, po_due_date, po_status " +
                        " FROM  po_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by po_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select po_nbr, po_vend, po_site, po_rmks, po_ord_date, po_due_date, po_status  " +
                        " FROM  po_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by po_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("po_nbr"));
                        rowArray.put(res.getString("po_vend"));
                        rowArray.put(res.getString("po_site"));
                        rowArray.put(res.getString("po_rmks"));
                        rowArray.put(res.getString("po_ord_date"));
                        rowArray.put(res.getString("po_due_date"));
                        rowArray.put(res.getString("po_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getDOBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDOBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDOBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), 
                          getGlobalColumnTag("number"), 
                          getGlobalColumnTag("from") + " " + getGlobalColumnTag("warehouse"), 
                          getGlobalColumnTag("to") + " " + getGlobalColumnTag("warehouse"), 
                          getGlobalColumnTag("ref"), getGlobalColumnTag("shipdate"), getGlobalColumnTag("recvdate"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
        
    public static String getDOBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select do_nbr, do_wh_from, do_wh_to, do_ref, do_shipdate, do_recvdate, do_status " +
                        " FROM  do_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by do_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select do_nbr, do_wh_from, do_wh_to, do_ref, do_shipdate, do_recvdate, do_status " +
                        " FROM  do_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by do_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select do_nbr, do_wh_from, do_wh_to, do_ref, do_shipdate, do_recvdate, do_status  " +
                        " FROM  do_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by do_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("do_nbr"));
                        rowArray.put(res.getString("do_wh_from"));
                        rowArray.put(res.getString("do_wh_to"));
                        rowArray.put(res.getString("do_ref"));
                        rowArray.put(res.getString("do_shipdate"));
                        rowArray.put(res.getString("do_recvdate"));
                        rowArray.put(res.getString("do_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getSVBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSVBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getSVBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("number"), getGlobalColumnTag("customer"), getGlobalColumnTag("po"), getGlobalColumnTag("scheddate"), getGlobalColumnTag("type"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
        
    public static String getSVBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select sv_nbr, sv_cust, sv_po, sv_due_date, sv_type, sv_status " +
                        " FROM  sv_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sv_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select sv_nbr, sv_cust, sv_po, sv_due_date, sv_type, sv_status  " +
                        " FROM  sv_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sv_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select sv_nbr, sv_cust, sv_po, sv_due_date, sv_type, sv_status   " +
                        " FROM  sv_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sv_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("sv_nbr"));
                        rowArray.put(res.getString("sv_cust"));
                        rowArray.put(res.getString("sv_po"));
                        rowArray.put(res.getString("sv_due_date"));
                        rowArray.put(res.getString("sv_type"));
                        rowArray.put(res.getString("sv_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getFOBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFOBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFOBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("number"), getGlobalColumnTag("carrier"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getFOBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select fo_nbr, fo_carrier, fo_status " +
                        " FROM  fo_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by fo_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("select fo_nbr, fo_carrier, fo_status  " +
                        " FROM  fo_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by fo_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select fo_nbr, fo_carrier, fo_status  " +
                        " FROM  fo_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by fo_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("fo_nbr"));
                        rowArray.put(res.getString("fo_carrier"));
                        rowArray.put(res.getString("fo_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getCFOBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCFOBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCFOBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("number"), getGlobalColumnTag("revision"), getGlobalColumnTag("reference"), getGlobalColumnTag("customer"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
        
    public static String getCFOBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select cfo_nbr, cfo_revision, cfo_custfonbr, cfo_cust, cfo_orderstatus " +
                        " FROM  cfo_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by cfo_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("select cfo_nbr, cfo_revision, cfo_custfonbr, cfo_cust, cfo_orderstatus  " +
                        " FROM  cfo_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by cfo_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select cfo_nbr, cfo_revision, cfo_custfonbr, cfo_cust, cfo_orderstatus " +
                        " FROM  cfo_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by cfo_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cfo_nbr"));
                        rowArray.put(res.getString("cfo_revision"));
                        rowArray.put(res.getString("cfo_custfonbr"));
                        rowArray.put(res.getString("cfo_cust"));
                        rowArray.put(res.getString("cfo_orderstatus"));
                        jsonarray.put(rowArray);
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
    
        
    public static DefaultTableModel getShipperBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShipperBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getShipperBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("shipper"), getGlobalColumnTag("customer"), getGlobalColumnTag("shipcode"), getGlobalColumnTag("order"), getGlobalColumnTag("po"), getGlobalColumnTag("shipdate"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getShipperBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                   if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sh_id desc ;");  
                   }
                }
                if (state == 2) { // ends
                    if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sh_id desc ;");  
                   }
                }
                 if (state == 0) { // match
                 if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sh_id desc ;");  
                   }
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("sh_id"));
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("sh_ship"));
                        rowArray.put(res.getString("sh_so"));
                        rowArray.put(res.getString("sh_po"));
                        rowArray.put(res.getString("sh_shipdate"));
                        rowArray.put(res.getString("sh_status"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getInvoiceBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getInvoiceBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getInvoiceBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("invoice"), getGlobalColumnTag("customer"), getGlobalColumnTag("shipcode"), getGlobalColumnTag("order"), getGlobalColumnTag("po"), getGlobalColumnTag("shipdate"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
       
    public static String getInvoiceBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                   if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by sh_id desc ;");  
                   }
                }
                if (state == 2) { // ends
                    if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by sh_id desc ;");  
                   }
                }
                 if (state == 0) { // match
                 if (dbtype.equals("sqlite"))  {
                    res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case sh_status when '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end sh_status " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sh_id desc ;");
                   } else {
                     res = st.executeQuery(" select sh_id, sh_cust, sh_ship, sh_so, sh_po, sh_shipdate, case when sh_status = '1' then " + "'" + getGlobalProgTag("closed") + "'" + " else " + "'" + getGlobalProgTag("open") + "'" + " end as 'sh_status' " +
                        " FROM  ship_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by sh_id desc ;");  
                   }
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("sh_id"));
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("sh_ship"));
                        rowArray.put(res.getString("sh_so"));
                        rowArray.put(res.getString("sh_po"));
                        rowArray.put(res.getString("sh_shipdate"));
                        rowArray.put(res.getString("sh_status"));
                        jsonarray.put(rowArray);
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
        
       
    public static DefaultTableModel getProdCodeBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getProdCodeBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getProdCodeBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), "InventoryAcct", "SalesAccount", "POReceiptAccount", "ScrapAccount"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
         
    public static String getProdCodeBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT pl_line, pl_desc, pl_inventory, pl_sales, pl_po_rcpt, pl_scrap " +
                        " FROM  pl_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by pl_line ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT pl_line, pl_desc, pl_inventory, pl_sales, pl_po_rcpt, pl_scrap  " +
                        " FROM  pl_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by pl_line ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT pl_line, pl_desc, pl_inventory, pl_sales, pl_po_rcpt, pl_scrap  " +
                        " FROM  pl_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by pl_line ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("pl_line"));
                        rowArray.put(res.getString("pl_desc"));
                        rowArray.put(res.getString("pl_inventory"));
                        rowArray.put(res.getString("pl_sales"));
                        rowArray.put(res.getString("pl_po_rcpt"));
                        rowArray.put(res.getString("pl_scrap"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getLocationBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLocationBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getLocationBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("site"), getGlobalColumnTag("active")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
          
    public static String getLocationBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT loc_loc, loc_desc, loc_site, loc_active " +
                        " FROM  loc_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by loc_loc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT loc_loc, loc_desc, loc_site, loc_active " +
                        " FROM  loc_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by loc_loc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT loc_loc, loc_desc, loc_site, loc_active  " +
                        " FROM  loc_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by loc_loc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("loc_loc"));
                        rowArray.put(res.getString("loc_desc"));
                        rowArray.put(res.getString("loc_site"));
                        rowArray.put(res.getString("loc_active"));
                        jsonarray.put(rowArray);
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
    
   
    public static DefaultTableModel getWareHouseBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWareHouseBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getWareHouseBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("site"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
          
    public static String getWareHouseBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT wh_id, wh_site, wh_name, wh_addr1, wh_city, wh_state, wh_zip " +
                        " FROM  wh_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by wh_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT wh_id, wh_site, wh_name, wh_addr1, wh_city, wh_state, wh_zip  " +
                        " FROM  wh_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by wh_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT wh_id, wh_site, wh_name, wh_addr1, wh_city, wh_state, wh_zip   " +
                        " FROM  wh_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by wh_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("wh_id"));
                        rowArray.put(res.getString("wh_site"));
                        rowArray.put(res.getString("wh_name"));
                        rowArray.put(res.getString("wh_addr1"));
                        rowArray.put(res.getString("wh_city"));
                        rowArray.put(res.getString("wh_state"));
                        rowArray.put(res.getString("wh_zip"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getSalesRepBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSalesRepBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getSalesRepBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
          
    public static String getSalesRepBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT slsp_id, slsp_name, slsp_line1, slsp_city, slsp_state, slsp_zip " +
                        " FROM  slsp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by slsp_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT slsp_id, slsp_name, slsp_line1, slsp_city, slsp_state, slsp_zip  " +
                        " FROM  slsp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by slsp_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT slsp_id, slsp_name, slsp_line1, slsp_city, slsp_state, slsp_zip   " +
                        " FROM  slsp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by slsp_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("slsp_id"));
                        rowArray.put(res.getString("slsp_name"));
                        rowArray.put(res.getString("slsp_line1"));
                        rowArray.put(res.getString("slsp_city"));
                        rowArray.put(res.getString("slsp_state"));
                        rowArray.put(res.getString("slsp_zip"));
                        jsonarray.put(rowArray);
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
     
    
    public static DefaultTableModel getQuoteBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getQuoteBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getQuoteBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("key"), getGlobalColumnTag("customer")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getQuoteBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT quo_nbr, quo_cust " +
                        " FROM  quo_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by quo_nbr ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT quo_nbr, quo_cust " +
                        " FROM  quo_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by quo_nbr ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT quo_nbr, quo_cust  " +
                        " FROM  quo_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by quo_nbr ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("quo_nbr"));
                        rowArray.put(res.getString("quo_cust"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getBillingBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBillingBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getBillingBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("key"), getGlobalColumnTag("customer")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getBillingBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT bill_nbr, bill_cust " +
                        " FROM  bill_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by bill_nbr ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT bill_nbr, bill_cust " +
                        " FROM  bill_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by bill_nbr ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT bill_nbr, bill_cust  " +
                        " FROM  bill_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by bill_nbr ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("bill_nbr"));
                        rowArray.put(res.getString("bill_cust"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getVoucherBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVoucherBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVoucherBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("vendor"), getGlobalColumnTag("type"), getGlobalColumnTag("invoice"), getGlobalColumnTag("status"), getGlobalColumnTag("amount")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
           
    public static String getVoucherBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select ap_nbr, ap_status, ap_ref, ap_vend, ap_amt, ap_subtype " +
                        " FROM  ap_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select ap_nbr, ap_status, ap_ref, ap_vend, ap_amt, ap_subtype  " +
                        " FROM  ap_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select ap_nbr, ap_status, ap_ref, ap_vend, ap_amt, ap_subtype   " +
                        " FROM  ap_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ap_nbr"));
                        rowArray.put(res.getString("ap_vend"));
                        rowArray.put(res.getString("ap_subtype"));
                        rowArray.put(res.getString("ap_ref"));
                        rowArray.put(res.getString("ap_status"));
                        rowArray.put(res.getString("ap_amt"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getARPaymentBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getARPaymentBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getARPaymentBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("batch"), getGlobalColumnTag("customer"), getGlobalColumnTag("date"), getGlobalColumnTag("amount")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getARPaymentBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select ar_nbr, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr  where " + myfield + " like " + "'" + str + "%'" +
                        " and ar_type = 'P' order by ar_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select ar_nbr, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and ar_type = 'P' order by ar_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select ar_nbr, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and ar_type = 'P' order by ar_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ar_nbr"));
                        rowArray.put(res.getString("ar_cust"));
                        rowArray.put(res.getString("ar_effdate"));
                        rowArray.put(res.getString("ar_amt"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getARMemoBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getARMemoBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getARMemoBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("number"), getGlobalColumnTag("type"), getGlobalColumnTag("customer"), getGlobalColumnTag("date"), getGlobalColumnTag("amount")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getARMemoBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select ar_nbr, ar_type, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr  where " + myfield + " like " + "'" + str + "%'" +
                        " and (ar_type = 'C' or ar_type = 'D') order by ar_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select ar_nbr, ar_type, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and (ar_type = 'C' or ar_type = 'D') order by ar_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select ar_nbr, ar_type, ar_cust, ar_effdate, ar_amt " +
                        " FROM  ar_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and (ar_type = 'C' or ar_type = 'D') order by ar_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ar_nbr"));
                        rowArray.put(res.getString("ar_cust"));
                        rowArray.put(res.getString("ar_type"));
                        rowArray.put(res.getString("ar_effdate"));
                        rowArray.put(res.getString("ar_amt"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getExpenseBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getExpenseBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getExpenseBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("vendor"), getGlobalColumnTag("date"), getGlobalColumnTag("amount")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
           
    public static String getExpenseBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select ap_nbr, ap_vend, ap_effdate, ap_amt " +
                        " FROM  ap_mstr  where " + myfield + " like " + "'" + str + "%'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select ap_nbr, ap_vend, ap_effdate, ap_amt " +
                        " FROM  ap_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select ap_nbr, ap_vend, ap_effdate, ap_amt " +
                        " FROM  ap_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and ap_type = 'V' order by ap_nbr desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ap_nbr"));
                        rowArray.put(res.getString("ap_vend"));
                        rowArray.put(res.getString("ap_effdate"));
                        rowArray.put(res.getString("ap_amt"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getReceiverBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getReceiverBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getReceiverBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("vendor"), getGlobalColumnTag("po"), getGlobalColumnTag("packingslip"), getGlobalColumnTag("item"), getGlobalColumnTag("recvdate"), getGlobalColumnTag("quantity")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
            
    public static String getReceiverBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select rv_id, rv_vend, rvd_po, rvd_packingslip, rvd_item, rvd_date, rvd_qty " +
                        " FROM  recv_det inner join recv_mstr on rv_id = rvd_id where " + myfield + " like " + "'" + str + "%'" +
                        " order by rv_id desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select rv_id, rv_vend, rvd_po, rvd_packingslip, rvd_item, rvd_date, rvd_qty  " +
                        " FROM  recv_det inner join recv_mstr on rv_id = rvd_id where " + myfield + " like " + "'%" + str + "'" +
                        " order by rv_id desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select rv_id, rv_vend, rvd_po, rvd_packingslip, rvd_item, rvd_date, rvd_qty   " +
                        " FROM  recv_det inner join recv_mstr on rv_id = rvd_id where " + myfield + " like " + "'%" + str + "%'" +
                        " order by rv_id desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("rv_id"));
                        rowArray.put(res.getString("rv_vend"));
                        rowArray.put(res.getString("rvd_po"));
                        rowArray.put(res.getString("rvd_packingslip"));
                        rowArray.put(res.getString("rvd_item"));
                        rowArray.put(res.getString("rvd_date"));
                        rowArray.put(res.getString("rvd_qty"));
                        jsonarray.put(rowArray);
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
    
            
    public static DefaultTableModel getCalendarBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCalendarBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCalendarBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("year"), getGlobalColumnTag("period"), getGlobalColumnTag("startdate"), getGlobalColumnTag("enddate"), getGlobalColumnTag("status")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
           
    public static String getCalendarBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select glc_year, glc_per, glc_start, glc_end, glc_status " +
                        " FROM  gl_cal where " + myfield + " like " + "'" + str + "%'" +
                        " order by glc_year, glc_per ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select glc_year, glc_per, glc_start, glc_end, glc_status " +
                        " FROM  gl_cal where " + myfield + " like " + "'%" + str + "'" +
                        " order by glc_year, glc_per ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select glc_year, glc_per, glc_start, glc_end, glc_status  " +
                        " FROM  gl_cal where " + myfield + " like " + "'%" + str + "%'" +
                        " order by glc_year, glc_per ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("glc_year"));
                        rowArray.put(res.getString("glc_per"));
                        rowArray.put(res.getString("glc_start"));
                        rowArray.put(res.getString("glc_end"));
                        rowArray.put(res.getString("glc_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getEmpBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEmpBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEmpBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("empid"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("status"), getGlobalColumnTag("startdate"), getGlobalColumnTag("type"), getGlobalColumnTag("phone")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getEmpBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT emp_nbr, emp_lname, emp_fname, emp_status, emp_startdate, emp_type, emp_phone " +
                        " FROM  emp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by emp_lname ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT  emp_nbr, emp_lname, emp_fname, emp_status, emp_startdate, emp_type, emp_phone " +
                        " FROM  emp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by emp_lname ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT  emp_nbr, emp_lname, emp_fname, emp_status, emp_startdate, emp_type, emp_phone  " +
                        " FROM  emp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by emp_lname ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("emp_nbr"));
                        rowArray.put(res.getString("emp_lname"));
                        rowArray.put(res.getString("emp_fname"));
                        rowArray.put(res.getString("emp_status"));
                        rowArray.put(res.getString("emp_startdate"));
                        rowArray.put(res.getString("emp_type"));
                        rowArray.put(res.getString("emp_phone"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getJobClockUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getJobClockUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getJobClockUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              //  "RecID", "EmpID", "LastName", "FirstName", "Dept", getGlobalColumnTag("code"), "InDate", "InTime", "InTmAdj", "OutDate", "OutTime", "OutTmAdj", "tothrs"
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("empid"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("indate")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getJobClockUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                     res = st.executeQuery("SELECT jobc_id, jobc_empnbr, emp_lname, emp_fname, jobc_indate " +
                           " FROM  job_clock inner join emp_mstr on emp_nbr = jobc_empnbr " +
                              " where  " + myfield + " like " + "'" + str + "%'" +
                               " order by jobc_id " +
                               ";" );
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT jobc_id, jobc_empnbr, emp_lname, emp_fname, jobc_indate " +
                           " FROM  job_clock inner join emp_mstr on emp_nbr = jobc_empnbr " +
                              " where  " + myfield + " like " + "'%" + str + "'" +
                               " order by jobc_id " +
                               ";" );
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT jobc_id, jobc_empnbr, emp_lname, emp_fname, jobc_indate " +
                           " FROM  job_clock inner join emp_mstr on emp_nbr = jobc_empnbr " +
                              " where  " + myfield + " like " + "'%" + str + "%'" +
                               " order by jobc_id " +
                               ";" );
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("jobc_id"));
                        rowArray.put(res.getString("jobc_empnbr"));
                        rowArray.put(res.getString("emp_lname"));
                        rowArray.put(res.getString("emp_fname"));
                        rowArray.put(res.getString("jobc_indate"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getClockRecBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getClockRecBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getClockRecBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              //  "RecID", "EmpID", "LastName", "FirstName", "Dept", getGlobalColumnTag("code"), "InDate", "InTime", "InTmAdj", "OutDate", "OutTime", "OutTmAdj", "tothrs"
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("empid"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("dept"), getGlobalColumnTag("code"), getGlobalColumnTag("indate"), getGlobalColumnTag("intime"), getGlobalColumnTag("intimeadj"), getGlobalColumnTag("outdate"), getGlobalColumnTag("outtime"), getGlobalColumnTag("outtimeadj"), getGlobalColumnTag("hours")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
         
    public static String getClockRecBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                     res = st.executeQuery("SELECT tothrs, recid, t.emp_nbr as 't_emp_nbr', emp_lname, emp_fname, emp_dept, code_id, indate, intime, " +
                           " intime_adj, outdate, outtime, outtime_adj FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where  " + myfield + " like " + "'" + str + "%'" +
                               " order by recid limit 300 " +
                               ";" );
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT tothrs, recid, t.emp_nbr as 't_emp_nbr', emp_lname, emp_fname, emp_dept, code_id, indate, intime, " +
                           " intime_adj, outdate, outtime, outtime_adj FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where  " + myfield + " like " + "'%" + str + "'" +
                               " order by recid limit 300 " +
                               ";" );
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT tothrs, recid, t.emp_nbr as 't_emp_nbr', emp_lname, emp_fname, emp_dept, code_id, indate, intime, " +
                           " intime_adj, outdate, outtime, outtime_adj FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where  " + myfield + " like " + "'%" + str + "%'" +
                               " order by recid limit 300 " +
                               ";" );
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("recid"));
                        rowArray.put(res.getString("t_emp_nbr"));
                        rowArray.put(res.getString("emp_lname"));
                        rowArray.put(res.getString("emp_fname"));
                        rowArray.put(res.getString("emp_dept"));
                        rowArray.put(res.getString("code_id"));
                        rowArray.put(res.getString("indate"));
                        rowArray.put(res.getString("intime"));
                        rowArray.put(res.getString("intime_adj"));
                        rowArray.put(res.getString("outdate"));
                        rowArray.put(res.getString("outtime"));
                        rowArray.put(res.getString("outtime_adj"));
                        rowArray.put(res.getString("tothrs"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getUserBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getUserBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getUserBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("userid"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
        
    public static String getUserBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT user_id, user_lname, user_fname " +
                        " FROM  user_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by user_lname ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT user_id, user_lname, user_fname  " +
                        " FROM  user_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by user_lname ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT user_id, user_lname, user_fname  " +
                        " FROM  user_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by user_lname ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("user_id"));
                        rowArray.put(res.getString("user_lname"));
                        rowArray.put(res.getString("user_fname"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getMenuBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getMenuBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getMenuBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("class"), getGlobalColumnTag("type")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
          
    public static String getMenuBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT menu_id, menu_desc, menu_panel, menu_type " +
                        " FROM  menu_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by menu_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT  menu_id, menu_desc, menu_panel, menu_type " +
                        " FROM  menu_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by menu_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT  menu_id, menu_desc, menu_panel, menu_type  " +
                        " FROM  menu_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by menu_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("menu_id"));
                        rowArray.put(res.getString("menu_desc"));
                        rowArray.put(res.getString("menu_panel"));
                        rowArray.put(res.getString("menu_type"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getReqBrowseUtil(String str, int state, String myfield) {
           
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getReqBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getReqBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("requestor"), getGlobalColumnTag("date"), getGlobalColumnTag("po"), getGlobalColumnTag("name"), getGlobalColumnTag("amount"), getGlobalColumnTag("status")}) 
                    {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
           
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
       } 
    
    public static String getReqBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select * " +
                        " FROM  req_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by req_id desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("select * " +
                        " FROM  req_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by req_id desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select *  " +
                        " FROM  req_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by req_id desc ;");
                 }   
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("req_id"));
                        rowArray.put(res.getString("req_name"));
                        rowArray.put(res.getString("req_date"));
                        rowArray.put(res.getString("req_po"));
                        rowArray.put(res.getString("req_vend"));
                        rowArray.put(res.getString("req_amt"));
                        rowArray.put(res.getString("req_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getGLHistBrowseUtil( String str, int state, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGLHistBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGLHistBrowseUtilData(str, state, myfield);
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("ref"), getGlobalColumnTag("account"), getGlobalColumnTag("cc"), getGlobalColumnTag("site"), getGlobalColumnTag("effectivedate"), getGlobalColumnTag("enterdate"), getGlobalColumnTag("description"), getGlobalColumnTag("amount"), getGlobalColumnTag("userid")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getGLHistBrowseUtilData(String str, int state, String myfield) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT glh_id, glh_ref, glh_acct, glh_cc, glh_site, glh_effdate, glh_entdate, glh_desc, glh_base_amt, glh_userid " +
                        " FROM  gl_hist where " + myfield + " like " + "'" + str + "%'" +
                        " order by glh_id desc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT glh_id, glh_ref, glh_acct, glh_cc, glh_site, glh_effdate, glh_entdate, glh_desc, glh_base_amt, glh_userid  " +
                        " FROM  gl_hist where " + myfield + " like " + "'%" + str + "'" +
                        " order by glh_id desc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT glh_id, glh_ref, glh_acct, glh_cc, glh_site, glh_effdate, glh_entdate, glh_desc, glh_base_amt, glh_userid  " +
                        " FROM  gl_hist where " + myfield + " like " + "'%" + str + "%'" +
                        " order by glh_id desc ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("glh_id"));
                        rowArray.put(res.getString("glh_ref"));
                        rowArray.put(res.getString("glh_acct"));
                        rowArray.put(res.getString("glh_cc"));
                        rowArray.put(res.getString("glh_site"));
                        rowArray.put(res.getString("glh_effdate"));
                        rowArray.put(res.getString("glh_entdate"));
                        rowArray.put(res.getString("glh_desc"));
                        rowArray.put(res.getString("glh_base_amt"));
                        rowArray.put(res.getString("glh_userid"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getVendShipToBrowseUtil( String str, int state, String myfield, String code) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendShipToBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVendShipToBrowseUtilData(str, state, myfield, code);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("shipcode"), getGlobalColumnTag("vendor"), getGlobalColumnTag("type"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip"), getGlobalColumnTag("country")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getVendShipToBrowseUtilData(String str, int state, String myfield, String code) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT vds_shipto, vds_code, vds_type, vds_name, vds_line1, vds_city, vds_state, vds_zip, vds_country  " +
                        " FROM  vds_det where vds_code = " + "'" + code + "'" + " AND " + myfield + " like " + "'" + str + "%'" +
                        " order by vds_shipto ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT vds_shipto, vds_code, vds_type, vds_name, vds_line1, vds_city, vds_state, vds_zip, vds_country  " +
                        " FROM  vds_det where vds_code = " + "'" + code + "'" + " AND " + myfield + " like " + "'%" + str + "'" +
                        " order by vds_shipto ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT vds_shipto, vds_code, vds_type, vds_name, vds_line1, vds_city, vds_state, vds_zip, vds_country  " +
                        " FROM  vds_det where vds_code = " + "'" + code + "'" + " AND "+ myfield + " like " + "'%" + str + "%'" +
                        " order by vds_shipto ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vds_shipto"));
                        rowArray.put(res.getString("vds_code"));
                        rowArray.put(res.getString("vds_type"));
                        rowArray.put(res.getString("vds_name"));
                        rowArray.put(res.getString("vds_line1"));
                        rowArray.put(res.getString("vds_city"));
                        rowArray.put(res.getString("vds_state"));
                        rowArray.put(res.getString("vds_zip"));
                        rowArray.put(res.getString("vds_country"));
                        jsonarray.put(rowArray);
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
        
    public static DefaultTableModel getShipToBrowseUtil( String str, int state, String myfield, String cust) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShipToBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", cust});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getShipToBrowseUtilData(str, state, myfield, cust);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("shipcode"), getGlobalColumnTag("customer"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip"), getGlobalColumnTag("country")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getShipToBrowseUtilData(String str, int state, String myfield, String cust) {
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
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT cms_shipto, cms_code, cms_name, cms_line1, cms_city, cms_state, cms_zip, cms_country  " +
                        " FROM  cms_det where cms_code = " + "'" + cust + "'" + " AND " + myfield + " like " + "'" + str + "%'" +
                        " order by cms_shipto ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT cms_shipto, cms_code, cms_name, cms_line1, cms_city, cms_state, cms_zip, cms_country  " +
                        " FROM  cms_det where cms_code = " + "'" + cust + "'" + " AND " + myfield + " like " + "'%" + str + "'" +
                        " order by cms_shipto ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT cms_shipto, cms_code, cms_name, cms_line1, cms_city, cms_state, cms_zip, cms_country  " +
                        " FROM  cms_det where cms_code = " + "'" + cust + "'" + " AND "+ myfield + " like " + "'%" + str + "%'" +
                        " order by cms_shipto ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cms_shipto"));
                        rowArray.put(res.getString("cms_code"));
                        rowArray.put(res.getString("cms_name"));
                        rowArray.put(res.getString("cms_line1"));
                        rowArray.put(res.getString("cms_city"));
                        rowArray.put(res.getString("cms_state"));
                        rowArray.put(res.getString("cms_zip"));
                        rowArray.put(res.getString("cms_country"));
                        jsonarray.put(rowArray);
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
    
     public static DefaultTableModel getMapBrowseUtil( String str, int state, String myfield, String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getMapBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getMapBrowseUtilData(str, state, myfield, site);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("ifs"), getGlobalColumnTag("ofs")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getMapBrowseUtilData(String str, int state, String myfield, String site) {  
        JSONArray jsonarray = new JSONArray();
        try {
            
            // visible = <any char but '1' or blank>, not visible = 1
            String internal = "0";
            internal = OVData.getCodeValueByCodeKey("edimaps","internal"); 
            internal = (internal.equals("1")) ? "1" : "x"; // wierd work around for below query mess
                                                           // long story short...if intention is to prevent user access from sys map...then
                                                           // code_value = '1'
                                                           // else code_value = '0'...or no code_mstr record
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            
            try{
                if (site.equals("all")) {
                if (state == 1) { // begins
                    res = st.executeQuery("SELECT map_id, map_desc, map_ifs, map_ofs  " +
                        " FROM  map_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " and map_internal <> " + "'" + internal + "'" + " order by map_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT map_id, map_desc, map_ifs, map_ofs  " +
                        " FROM  map_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and map_internal <> " + "'" + internal + "'" + " order by map_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT map_id, map_desc, map_ifs, map_ofs   " +
                        " FROM  map_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and map_internal <> " + "'" + internal + "'" + " order by map_id ;");
                 }
                } else {
                 if (state == 1) { // begins
                    res = st.executeQuery("SELECT map_id, map_desc, map_ifs, map_ofs  " +
                        " FROM  map_mstr where map_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'" + str + "%'" +
                        " and map_internal <> " + "'" + internal + "'" + " order by map_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT map_id, map_desc, map_ifs, map_ofs  " +
                        " FROM  map_mstr where map_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'%" + str + "'" +
                        " and map_internal <> " + "'" + internal + "'" + " order by map_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT map_id, map_desc, map_ifs, map_ofs   " +
                        " FROM  map_mstr where map_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'%" + str + "%'" +
                        " and map_internal <> " + "'" + internal + "'" + " order by map_id ;");
                 }   
                }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("map_id"));
                        rowArray.put(res.getString("map_desc"));
                        rowArray.put(res.getString("map_ifs"));
                        rowArray.put(res.getString("map_ofs"));
                        jsonarray.put(rowArray);
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
   
    public static DefaultTableModel getBomBrowseUtil( String str, int state, String myfield, String item, String routing) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBomBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", item});
            list.add(new String[]{"param5", routing});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getBomBrowseUtilData(str, state, myfield, item, routing);
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("item"), getGlobalColumnTag("enabled"), getGlobalColumnTag("default"), })
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getBomBrowseUtilData(String str, int state, String myfield, String item, String routing) {  
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
                 if (state == 1) { // begins
                    res = st.executeQuery("SELECT *  " +
                        " FROM  bom_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " and bom_item = " + "'" + item + "'" +  
                        " and bom_routing = " + "'" + routing + "'" +
                        " order by bom_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery("SELECT * " +
                        " FROM  bom_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and bom_item = " + "'" + item + "'" +
                        " and bom_routing = " + "'" + routing + "'" +
                        " order by bom_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("SELECT *  " +
                        " FROM  bom_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and bom_item = " + "'" + item + "'" +
                        " and bom_routing = " + "'" + routing + "'" +
                        " order by bom_id ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("bom_id"));
                        rowArray.put(res.getString("bom_desc"));
                        rowArray.put(res.getString("bom_item"));
                        rowArray.put(res.getString("bom_enabled"));
                        rowArray.put(res.getString("bom_primary"));
                        jsonarray.put(rowArray);
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
   
     public static DefaultTableModel getEDIPartnerBrowseUtil( String str, int state, String myfield, String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIPartnerBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDIPartnerBrowseUtilData(str, state, myfield, site);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
     
    public static String getEDIPartnerBrowseUtilData(String str, int state, String myfield, String site) {
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
                if (site.equals("all")) {
                if (state == 1) { // begins
                    res = st.executeQuery(" select edp_id, edp_desc " +
                        " FROM  edp_partner where " + myfield + " like " + "'" + str + "%'" +
                        " order by edp_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select edp_id, edp_desc " +
                        " FROM  edp_partner where " + myfield + " like " + "'%" + str + "'" +
                        " order by edp_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select edp_id, edp_desc " +
                        " FROM  edp_partner where " + myfield + " like " + "'%" + str + "%'" +
                        " order by edp_id ;");
                 }
                } else {
                 if (state == 1) { // begins
                    res = st.executeQuery(" select edp_id, edp_desc " +
                        " FROM  edp_partner where edp_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'" + str + "%'" +
                        " order by edp_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select edp_id, edp_desc " +
                        " FROM  edp_partner where edp_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'%" + str + "'" +
                        " order by edp_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select edp_id, edp_desc " +
                        " FROM  edp_partner where edp_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'%" + str + "%'" +
                        " order by edp_id ;");
                 }   
                }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("edp_id"));
                        rowArray.put(res.getString("edp_desc"));
                        jsonarray.put(rowArray);
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
    
     public static DefaultTableModel getGenCodeBrowseUtilByCode( String str, int state, String myfield, String code) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGenCodeBrowseUtilByCodeData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", code});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGenCodeBrowseUtilByCodeData(str, state, myfield, code); 
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("key1"), getGlobalColumnTag("value")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getGenCodeBrowseUtilByCodeData(String str, int state, String myfield, String code) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " and code_code = " + "'" + code + "'" +
                        " order by code_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and code_code = " + "'" + code + "'" +
                        " order by code_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select code_code, code_key, code_value " +
                        " FROM  code_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and code_code = " + "'" + code + "'" +
                        " order by code_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("code_key"));
                        rowArray.put(res.getString("code_value"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getFreightCodeBrowseUtilByCode( String str, int state, String myfield, String code) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightCodeBrowseUtilByCodeData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", code});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightCodeBrowseUtilByCodeData(str, state, myfield, code); 
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("key1"), getGlobalColumnTag("value")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getFreightCodeBrowseUtilByCodeData(String str, int state, String myfield, String code) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select freight_code, freight_key, freight_value " +
                        " FROM  code_freight where " + myfield + " like " + "'" + str + "%'" +
                        " and freight_code = " + "'" + code + "'" +
                        " order by freight_code ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select freight_code, freight_key, freight_value" +
                        " FROM  code_freight where " + myfield + " like " + "'%" + str + "'" +
                        " and freight_code = " + "'" + code + "'" +
                        " order by freight_code ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select freight_code, freight_key, freight_value " +
                        " FROM  code_freight where " + myfield + " like " + "'%" + str + "%'" +
                        " and freight_code = " + "'" + code + "'" +
                        " order by freight_code ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("freight_key"));
                        rowArray.put(res.getString("freight_value"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getCustXrefBrowseUtil( String str, int state, String myfield, String cust) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustXrefBrowseUtil2Data"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", cust});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCustXrefBrowseUtil2Data(str, state, myfield, cust);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("custitem"), getGlobalColumnTag("customer")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 

    public static String getCustXrefBrowseUtil2Data(String str, int state, String myfield, String cust) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " and cup_cust = " + "'" + cust + "'" +        
                        " order by cup_cust, cup_citem ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'%" + str + "'" +
                                " and cup_cust = " + "'" + cust + "'" + 
                        " order by cup_cust, cup_citem ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select cup_cust, cup_citem, cup_item " +
                        " FROM  cup_mstr where " + myfield + " like " + "'%" + str + "%'" +
                                " and cup_cust = " + "'" + cust + "'" + 
                        " order by cup_cust, cup_citem ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cup_cust"));
                        rowArray.put(res.getString("cup_citem"));
                        rowArray.put(res.getString("cup_item"));
                        jsonarray.put(rowArray);
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
     
    public static DefaultTableModel getCustContactsBrowseUtil( String str, int state, String myfield, String cust) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustContactsBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", cust});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCustContactsBrowseUtilData(str, state, myfield, cust);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("type"), getGlobalColumnTag("name"), getGlobalColumnTag("phone"), getGlobalColumnTag("email")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                         return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 

    public static String getCustContactsBrowseUtilData(String str, int state, String myfield, String cust) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select cmc_type, cmc_name, cmc_phone, cmc_email " +
                        " FROM  cmc_det where " + myfield + " like " + "'" + str + "%'" +
                        " and cmc_code = " + "'" + cust + "'" +        
                        " order by cmc_type, cmc_name ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select cmc_type, cmc_name, cmc_phone, cmc_email " +
                        " FROM  cmc_det where " + myfield + " like " + "'%" + str + "'" +
                                " and cmc_code = " + "'" + cust + "'" + 
                        " order by cmc_type, cmc_name ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select cmc_type, cmc_name, cmc_phone, cmc_email " +
                        " FROM  cmc_det where " + myfield + " like " + "'%" + str + "%'" +
                                " and cmc_code = " + "'" + cust + "'" + 
                        " order by cmc_type, cmc_name ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("mail");
                        rowArray.put(res.getString("cmc_type"));
                        rowArray.put(res.getString("cmc_name"));
                        rowArray.put(res.getString("cmc_phone"));
                        rowArray.put(res.getString("cmc_email"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getVendContactsBrowseUtil( String str, int state, String myfield, String vend) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendContactsBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", vend});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getvendContactsBrowseUtilData(str, state, myfield, vend);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("type"), getGlobalColumnTag("name"), getGlobalColumnTag("phone"), getGlobalColumnTag("email")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                         return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 

    public static String getvendContactsBrowseUtilData(String str, int state, String myfield, String vend) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select vdc_type, vdc_name, vdc_phone, vdc_email " +
                        " FROM  vdc_det where " + myfield + " like " + "'" + str + "%'" +
                        " and vdc_code = " + "'" + vend + "'" +        
                        " order by vdc_type, vdc_name ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select vdc_type, vdc_name, vdc_phone, vdc_email " +
                        " FROM  vdc_det where " + myfield + " like " + "'%" + str + "'" +
                                " and vdc_code = " + "'" + vend + "'" + 
                        " order by vdc_type, vdc_name  ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select vdc_type, vdc_name, vdc_phone, vdc_email " +
                        " FROM  vdc_det where " + myfield + " like " + "'%" + str + "%'" +
                                " and vdc_code = " + "'" + vend + "'" + 
                        " order by vdc_type, vdc_name  ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("mail");
                        rowArray.put(res.getString("vdc_type"));
                        rowArray.put(res.getString("vdc_name"));
                        rowArray.put(res.getString("vdc_phone"));
                        rowArray.put(res.getString("vdc_email"));
                        jsonarray.put(rowArray);
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
    
     public static DefaultTableModel getVendXrefBrowseUtil2( String str, int state, String myfield, String vend) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendXrefBrowseUtil2Data"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", vend});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVendXrefBrowseUtil2Data(str, state, myfield, vend);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("venditem"), getGlobalColumnTag("vendor")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
     
    public static String getVendXrefBrowseUtil2Data(String str, int state, String myfield, String vend) {
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
                if (state == 1) { // begins
                    res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " and vdp_vend = " + "'" + vend + "'" +
                        " order by vdp_vend, vdp_vitem ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " and vdp_vend = " + "'" + vend + "'" +        
                        " order by vdp_vend, vdp_vitem ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" select vdp_vend, vdp_vitem, vdp_item " +
                        " FROM  vdp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " and vdp_vend = " + "'" + vend + "'" +        
                        " order by vdp_vend, vdp_vitem ;");
                 }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vdp_item"));
                        rowArray.put(res.getString("vdp_vitem"));
                        rowArray.put(res.getString("vdp_item"));
                        jsonarray.put(rowArray);
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
    
     public static DefaultTableModel getFTPBrowseUtil( String str, int state, String myfield, String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFTPBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFTPBrowseUtilData(str, state, myfield, site);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), "IP/URL", "Login", "Passwd", "CDDir", "InDir", "OutDir", "Delete?"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
           
    public static String getFTPBrowseUtilData(String str, int state, String myfield, String site) {
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
                if (site.equals("all")) {
                if (state == 1) { // begins
                    res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_commands, ftp_indir, ftp_outdir, ftp_delete " +
                        " FROM  ftp_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by ftp_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_commands, ftp_indir, ftp_outdir, ftp_delete " +
                        " FROM  ftp_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by ftp_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_commands, ftp_indir, ftp_outdir, ftp_delete  " +
                        " FROM  ftp_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by ftp_id ;");
                 }
                } else {
                 if (state == 1) { // begins
                    res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_commands, ftp_indir, ftp_outdir, ftp_delete " +
                        " FROM  ftp_mstr where ftp_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'" + str + "%'" +
                        " order by ftp_id ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_commands, ftp_indir, ftp_outdir, ftp_delete " +
                        " FROM  ftp_mstr where ftp_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'%" + str + "'" +
                        " order by ftp_id ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery(" SELECT ftp_id, ftp_desc, ftp_ip, ftp_login, ftp_passwd, ftp_commands, ftp_indir, ftp_outdir, ftp_delete  " +
                        " FROM  ftp_mstr where ftp_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'%" + str + "%'" +
                        " order by ftp_id ;");
                 }   
                }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ftp_id"));
                        rowArray.put(res.getString("ftp_desc"));
                        rowArray.put(res.getString("ftp_ip"));
                        rowArray.put(res.getString("ftp_login"));
                        rowArray.put(res.getString("ftp_passwd"));
                        rowArray.put(res.getString("ftp_commands"));
                        rowArray.put(res.getString("ftp_indir"));
                        rowArray.put(res.getString("ftp_outdir"));
                        rowArray.put(res.getString("ftp_delete"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getOrderDetailBrowseUtil( String str, String myfield, String cust, String ship) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOrderDetailBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", myfield});
            list.add(new String[]{"param3", cust});
            list.add(new String[]{"param4", ship});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getOrderDetailBrowseUtilData(str, myfield, cust, ship);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), 
                          getGlobalColumnTag("order"), 
                          getGlobalColumnTag("line"), 
                          getGlobalColumnTag("po"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("orderqty"), getGlobalColumnTag("shipqty")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getOrderDetailBrowseUtilData(String str, String myfield, String cust, String ship) {
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
                res = st.executeQuery(" select so_nbr, so_po, sod_line, sod_item, sod_desc, sod_ord_qty, sod_shipped_qty  " +
                        " FROM so_mstr " +
                        " inner join sod_det on sod_nbr = so_nbr " +
                        " where " + 
                        " so_cust = " + "'" + cust + "'" +
                        " AND sod_ship = " + "'" + ship + "'" +
                        " AND sod_status <> " + "'" + getGlobalProgTag("closed") + "'" +
                        " AND " + myfield + " like " + "'%" + str + "%'" +
                        " order by sod_line asc ;");
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("so_nbr"));
                        rowArray.put(res.getString("sod_line"));
                        rowArray.put(res.getString("so_po"));
                        rowArray.put(res.getString("sod_item"));
                        rowArray.put(res.getString("sod_desc"));
                        rowArray.put(res.getString("sod_ord_qty"));
                        rowArray.put(res.getString("sod_shipped_qty"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getOrderLineBrowseUtil( String str, String myfield, String order) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOrderLineBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", myfield});
            list.add(new String[]{"param3", order});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getOrderLineBrowseUtilData(str, myfield, order);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("line"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("orderqty"), getGlobalColumnTag("shipqty")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getOrderLineBrowseUtilData(String str, String myfield, String order) {
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
                res = st.executeQuery(" select so_nbr, so_po, sod_line, sod_item, sod_desc, sod_ord_qty, sod_shipped_qty  " +
                        " FROM so_mstr " +
                        " inner join sod_det on sod_nbr = so_nbr " +
                        " where " + 
                        " so_nbr = " + "'" + order + "'" + 
                        " AND sod_status <> " + "'" + getGlobalProgTag("closed") + "'" +
                        " AND " + myfield + " like " + "'%" + str + "%'" +
                        " order by sod_line asc ;");
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("sod_line"));
                        rowArray.put(res.getString("sod_item"));
                        rowArray.put(res.getString("sod_desc"));
                        rowArray.put(res.getString("sod_ord_qty"));
                        rowArray.put(res.getString("sod_shipped_qty"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getEDICustBrowseUtil( String str, int state, String myfield, String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDICustBrowseUtilData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", String.valueOf(state)});
            list.add(new String[]{"param3", myfield});
            list.add(new String[]{"param4", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDICustBrowseUtilData(str, state, myfield, site);
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("doctype"), "SenderISA", "SenderGS", "ReceiverISA", "ReceiverGS", getGlobalColumnTag("map")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }  
          
    public static String getEDICustBrowseUtilData(String str, int state, String myfield, String site) {
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
                if (site.equals("all")) {
                if (state == 1) { // begins
                    res = st.executeQuery(" select edi_id, edi_doc, edi_sndisa, edi_sndgs, edi_rcvisa, edi_rcvgs,  edi_map  " +
                        " FROM  edi_mstr where " + myfield + " like " + "'" + str + "%'" +
                        " order by edi_id, edi_doc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select edi_id, edi_doc, edi_sndisa, edi_sndgs, edi_rcvisa, edi_rcvgs,  edi_map  " +
                        " FROM  edi_mstr where " + myfield + " like " + "'%" + str + "'" +
                        " order by edi_id, edi_doc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  select edi_id, edi_doc, edi_sndisa, edi_sndgs, edi_rcvisa, edi_rcvgs,  edi_map  " +
                        " FROM  edi_mstr where " + myfield + " like " + "'%" + str + "%'" +
                        " order by edi_id, edi_doc ;");
                 }
                } else {
                 if (state == 1) { // begins
                    res = st.executeQuery(" select edi_id, edi_doc, edi_sndisa, edi_sndgs, edi_rcvisa, edi_rcvgs,  edi_map  " +
                        " FROM  edi_mstr where edi_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'" + str + "%'" +
                        " order by edi_id, edi_doc ;");
                }
                if (state == 2) { // ends
                    res = st.executeQuery(" select edi_id, edi_doc, edi_sndisa, edi_sndgs, edi_rcvisa, edi_rcvgs,  edi_map  " +
                        " FROM  edi_mstr where edi_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'%" + str + "'" +
                        " order by edi_id, edi_doc ;");
                }
                 if (state == 0) { // match
                 res = st.executeQuery("  select edi_id, edi_doc, edi_sndisa, edi_sndgs, edi_rcvisa, edi_rcvgs,  edi_map  " +
                        " FROM  edi_mstr where edi_site = " + "'" + site + "'" + " AND " + myfield + " like " + "'%" + str + "%'" +
                        " order by edi_id, edi_doc ;");
                 }    
                }
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("edi_id"));
                        rowArray.put(res.getString("edi_doc"));
                        rowArray.put(res.getString("edi_sndisa"));
                        rowArray.put(res.getString("edi_sndgs"));
                        rowArray.put(res.getString("edi_rcvisa"));
                        rowArray.put(res.getString("edi_rcvgs"));
                        rowArray.put(res.getString("edi_map"));
                        jsonarray.put(rowArray);
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
    
    
     public static DefaultTableModel getItemDescBrowse(String str, String myfield) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemDescBrowseData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", myfield});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getItemDescBrowseData(str, myfield);
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("class"), getGlobalColumnTag("type")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    };

       for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 
  
    public static String getItemDescBrowseData(String str, String myfield) {
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
            
                res = st.executeQuery(" select it_item, it_desc, it_code, it_type  " +
                    " FROM  item_mstr where " + myfield + " like " + "'%" + str + "%'" +
                    " order by it_item ;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("it_type"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getItemDescBrowseBySite(String str, String myfield, String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemDescBrowseBySiteData"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", myfield});
            list.add(new String[]{"param3", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getItemDescBrowseBySiteData(str, myfield, site);
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("class"), getGlobalColumnTag("type"), getGlobalColumnTag("uom"), getGlobalColumnTag("price")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 
  
    public static String getItemDescBrowseBySiteData(String str, String myfield, String site) {
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
            
                res = st.executeQuery(" select it_item, it_desc, it_code, it_type, it_uom, it_sell_price  " +
                    " FROM  item_mstr where " + myfield + " like " + "'%" + str + "%'" +
                    " and it_site = " + "'" + site + "'" +
                    " order by it_item ;"); 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("it_type"));
                        rowArray.put(res.getString("it_uom"));
                        rowArray.put(BlueSeerUtils.currformat(res.getString("it_sell_price")));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getItemDescBrowseBySite(String str, String myfield, String site, String type) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemDescBrowseBySite2Data"});
            list.add(new String[]{"param1", str});
            list.add(new String[]{"param2", myfield});
            list.add(new String[]{"param3", site});
            list.add(new String[]{"param4", type});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getItemDescBrowseBySite2Data(str, myfield, site, type);
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("class"), getGlobalColumnTag("type")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 
  
    public static String getItemDescBrowseBySite2Data(String str, String myfield, String site, String classtype) {
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
            
                if (classtype.equals("ALL")) {
                 res = st.executeQuery(" select it_item, it_desc, it_code, it_type  " +
                    " FROM  item_mstr where " + myfield + " like " + "'%" + str + "%'" +
                    " and it_site = " + "'" + site + "'" +     
                    " order by it_item ;"); 
                } else {
                 res = st.executeQuery(" select it_item, it_desc, it_code, it_type  " +
                    " FROM  item_mstr where " + myfield + " like " + "'%" + str + "%'" +
                    " and it_site = " + "'" + site + "'" +
                    " and it_code = " + "'" + classtype + "'" +        
                    " order by it_item ;");    
                }
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("it_type"));
                        jsonarray.put(rowArray);
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
    
    
    
    public static DefaultTableModel getFreightOrderQuotesTable(String order) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightOrderQuotesTableData"});
            list.add(new String[]{"param1", order});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightOrderQuotesTableData(order);
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), 
                      getGlobalColumnTag("order"), 
                      getGlobalColumnTag("id"), 
                      getGlobalColumnTag("carrier"), 
                      getGlobalColumnTag("type"), 
                      getGlobalColumnTag("file"), 
                      getGlobalColumnTag("direction"), 
                      getGlobalColumnTag("date")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getFreightOrderQuotesTableData(String order) {
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
                res = st.executeQuery("select fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_dir, fot_date " +
                        " from fot_det " +
                        "  where fot_nbr = " + "'" + order + "'" + 
                        " AND ( fot_doctype = '219' OR fot_doctype = '220') " +
                        ";");
               
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("fot_nbr"));
                        rowArray.put(res.getString("fot_uniqueid"));
                        rowArray.put(res.getString("fot_partnerid"));
                        rowArray.put(res.getString("fot_doctype"));
                        rowArray.put(res.getString("fot_docfile"));
                        rowArray.put(res.getString("fot_dir"));
                        rowArray.put(res.getString("fot_date"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getFreightOrderTendersTable(String order) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightOrderTendersTableData"});
            list.add(new String[]{"param1", order});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightOrderTendersTableData(order);
        }
        Object[][] data = jsonToData(jsonString);      
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), 
                      getGlobalColumnTag("order"), 
                      getGlobalColumnTag("id"), 
                      getGlobalColumnTag("carrier"), 
                      getGlobalColumnTag("type"), 
                      getGlobalColumnTag("file"), 
                      getGlobalColumnTag("direction"), 
                      getGlobalColumnTag("date")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getFreightOrderTendersTableData(String order) {
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
                res = st.executeQuery("select fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_dir, fot_date " +
                        " from fot_det " +
                        "  where fot_nbr = " + "'" + order + "'" + 
                        " AND ( fot_doctype = '204' OR fot_doctype = '990') " +
                        ";");
               
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("fot_nbr"));
                        rowArray.put(res.getString("fot_uniqueid"));
                        rowArray.put(res.getString("fot_partnerid"));
                        rowArray.put(res.getString("fot_doctype"));
                        rowArray.put(res.getString("fot_docfile"));
                        rowArray.put(res.getString("fot_dir"));
                        rowArray.put(res.getString("fot_date"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getFreightOrderStatusTable(String order) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightOrderStatusTableData"});
            list.add(new String[]{"param1", order});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightOrderStatusTableData(order);
        }
        Object[][] data = jsonToData(jsonString);  
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), 
                      getGlobalColumnTag("order"), 
                      getGlobalColumnTag("id"), 
                      getGlobalColumnTag("carrier"), 
                      getGlobalColumnTag("type"), 
                      getGlobalColumnTag("file"), 
                      getGlobalColumnTag("status"), 
                      getGlobalColumnTag("remarks"), 
                      getGlobalColumnTag("latitude"), 
                      getGlobalColumnTag("longitude"), 
                      getGlobalColumnTag("direction"), 
                      getGlobalColumnTag("date")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getFreightOrderStatusTableData(String order) {
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
                res = st.executeQuery("select fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_status, fot_remarks, fot_lat, fot_lon, fot_dir, fot_date " +
                        " from fot_det " +
                        "  where fot_nbr = " + "'" + order + "'" + 
                        " AND fot_doctype = '214' " +
                        ";");
               
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("fot_nbr"));
                        rowArray.put(res.getString("fot_uniqueid"));
                        rowArray.put(res.getString("fot_partnerid"));
                        rowArray.put(res.getString("fot_doctype"));
                        rowArray.put(res.getString("fot_docfile"));
                        rowArray.put(res.getString("fot_status"));
                        rowArray.put(res.getString("fot_remarks"));
                        rowArray.put(res.getString("fot_lat"));
                        rowArray.put(res.getString("fot_lon"));
                        rowArray.put(res.getString("fot_dir"));
                        rowArray.put(res.getString("fot_date"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getASCIIChartDT(int fromint, int toint) {
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("integer"), getGlobalColumnTag("ascii")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        return String.class;  //other columns accept String values  
                      }  
                        }; 
                    String s = "";
                    for (int i = fromint; i < toint; i++) {
                        s = String.valueOf(Character.toString((char) i));
                        if (i == 0) {s = "Null";};
                        if (i == 1) {s = "SOH";};
                        if (i == 2) {s = "STX";};
                        if (i == 3) {s = "ETX";};
                        if (i == 4) {s = "EOT";};
                        if (i == 5) {s = "ENQ";};
                        if (i == 6) {s = "ACK";};
                        if (i == 7) {s = "BEL";};
                        if (i == 8) {s = "BS";};
                        if (i == 9) {s = "TAB";};
                        if (i == 10) {s = "NL";};
                        if (i == 11) {s = "VT";};
                        if (i == 12) {s = "FF";};
                        if (i == 13) {s = "CR";};
                        if (i == 14) {s = "SO";};
                        if (i == 15) {s = "SI";};
                        if (i == 16) {s = "DLE";};
                        if (i == 17) {s = "DC1";};
                        if (i == 18) {s = "DC2";};
                        if (i == 19) {s = "DC3";};
                        if (i == 20) {s = "DC4";};
                        if (i == 21) {s = "NAK";};
                        if (i == 22) {s = "SYN";};
                        if (i == 23) {s = "ETB";};
                        if (i == 24) {s = "CAN";};
                        if (i == 25) {s = "EM";};
                        if (i == 26) {s = "SUB";};
                        if (i == 27) {s = "ESC";};
                        if (i == 28) {s = "FS";};
                        if (i == 29) {s = "GS";};
                        if (i == 30) {s = "RS";};
                        if (i == 31) {s = "US";};
                        if (i == 32) {s = "SPACE";};
                        mymodel.addRow(new Object[] {String.valueOf(i),s});
                    }
        return mymodel;
    }
       
       
    public static DefaultTableModel getPayRollHours(String fromdate, String todate) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPayRollHoursData"});
            list.add(new String[]{"param1", fromdate});
            list.add(new String[]{"param2", todate});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPayRollHoursData(fromdate, todate);
        }
        Object[][] data = jsonToData(jsonString);       
                 javax.swing.table.DefaultTableModel mymodel =  new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("empid"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("middlename"), getGlobalColumnTag("dept"), getGlobalColumnTag("shift"), getGlobalColumnTag("supervisor"), getGlobalColumnTag("type"), getGlobalColumnTag("profile"), getGlobalColumnTag("jobtitle"), getGlobalColumnTag("rate"), getGlobalColumnTag("hours"), getGlobalColumnTag("amount")})
                       {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
           
       for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
     
    public static String getPayRollHoursData(String fromdate, String todate) {
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
                double amount = 0;
                   
                       res = st.executeQuery("SELECT sum(t.tothrs) as 't.tothrs',  " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', e.emp_mname as 'e.emp_mname', e.emp_jobtitle as 'e.emp_jobtitle', " +
                           " e.emp_supervisor as 'e.emp_supervisor', e.emp_type as 'e.emp_type', e.emp_shift as 'e.emp_shift', e.emp_profile as 'e.emp_profile', e.emp_dept as 'e.emp_dept', e.emp_rate as 'e.emp_rate' " +
                           "  FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where t.indate >= " + "'" + fromdate + "'" +
                               " and t.indate <= " + "'" + todate + "'" + 
                                " and t.ispaid = '0' " +          
                                " group by t.emp_nbr, e.emp_lname, e.emp_fname, e.emp_mname, e.emp_jobtitle, e.emp_supervisor, e.emp_type, e.emp_shift, e.emp_profile, e.emp_dept, e.emp_rate " +       
                                " order by t.emp_nbr " +      
                               ";" );
                    
                    while (res.next()) {
                        amount = res.getDouble("t.tothrs") * res.getDouble("e.emp_rate"); 
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put("");
                        rowArray.put(res.getString("t.emp_nbr"));
                        rowArray.put(res.getString("e.emp_lname"));
                        rowArray.put(res.getString("e.emp_fname"));
                        rowArray.put(res.getString("e.emp_mname"));
                        rowArray.put(res.getString("e.emp_dept"));
                        rowArray.put(res.getString("e.emp_shift"));
                        rowArray.put(res.getString("e.emp_shift"));
                        rowArray.put(res.getString("e.emp_supervisor"));
                        rowArray.put(res.getString("e.emp_type"));
                        rowArray.put(res.getString("e.emp_profile"));
                        rowArray.put(res.getString("e.emp_jobtitle"));
                        rowArray.put(res.getString("e.emp_rate"));
                        rowArray.put(res.getString("t.tothrs"));
                        jsonarray.put(rowArray);
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
    
     public static DefaultTableModel getForecast13weeks(int wk) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getForecast13weeksData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getForecast13weeksData(wk);
        }
        Object[][] data = jsonToData(jsonString);  
        
        Calendar cal = Calendar.getInstance();
        cal.getTime();
       
        ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));
        DateFormat dtf = new SimpleDateFormat("MM/dd");
        // week dates are base 0
        String wk1 = dtf.format(dates.get(wk - 1));
        String wk2 = dtf.format(dates.get(wk));
        String wk3 = dtf.format(dates.get(wk + 1));
        String wk4 = dtf.format(dates.get(wk + 2));
        String wk5 = dtf.format(dates.get(wk + 3));
        String wk6 = dtf.format(dates.get(wk + 4));
        String wk7 = dtf.format(dates.get(wk + 5));
        String wk8 = dtf.format(dates.get(wk + 6));
        String wk9 = dtf.format(dates.get(wk + 7));
        String wk10 = dtf.format(dates.get(wk + 8));
        String wk11 = dtf.format(dates.get(wk + 9));
        String wk12 = dtf.format(dates.get(wk + 10));
        String wk13 = dtf.format(dates.get(wk + 11));
        
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("year"), getGlobalColumnTag("site"), wk1, wk2, wk3, wk4, wk5, wk6, wk7, wk8, wk9, wk10, wk11, wk12, wk13 })
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getForecast13weeksData(int wk) {
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
                 // adjust wk for first three fields
                  wk = wk + 3;
                  
                  res = st.executeQuery("select * from fct_mstr;" );
                   
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("fct_item"));
                        rowArray.put(res.getString("fct_year"));
                        rowArray.put(res.getString("fct_site"));
                        rowArray.put(res.getString(wk));
                        rowArray.put(res.getString(wk + 1));
                        rowArray.put(res.getString(wk + 2));
                        rowArray.put(res.getString(wk + 3));
                        rowArray.put(res.getString(wk + 4));
                        rowArray.put(res.getString(wk + 5));
                        rowArray.put(res.getString(wk + 6));
                        rowArray.put(res.getString(wk + 7));
                        rowArray.put(res.getString(wk + 8));
                        rowArray.put(res.getString(wk + 9));
                        rowArray.put(res.getString(wk + 10));
                        rowArray.put(res.getString(wk + 11));
                        rowArray.put(res.getString(wk + 12));
                        
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getForecast4weeksAndSec(int wk) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getForecast4weeksAndSecData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getForecast4weeksAndSecData(wk);
        }
        Object[][] data = jsonToData(jsonString);  
        
        Calendar cal = Calendar.getInstance();
        cal.getTime();
       
        ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));
        DateFormat df = new SimpleDateFormat("MM/dd");
        // week dates are base 0
        String wk1 = df.format(dates.get(wk - 1));
        String wk2 = df.format(dates.get(wk));
        String wk3 = df.format(dates.get(wk + 1));
        String wk4 = df.format(dates.get(wk + 2));
        
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("year"), getGlobalColumnTag("site"), getGlobalColumnTag("user"), getGlobalColumnTag("createdate"), wk1, wk2, wk3, wk4 })
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getForecast4weeksAndSecData(int wk) {
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
                res = st.executeQuery("select * from fct_mstr;" );
                                      
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("fct_item"));
                        rowArray.put(res.getString("fct_year"));
                        rowArray.put(res.getString("fct_site"));
                        rowArray.put(res.getString("fct_crt_userid"));
                        rowArray.put(res.getString("fct_crt_date"));
                        rowArray.put(res.getString(wk));
                        rowArray.put(res.getString(wk + 1));
                        rowArray.put(res.getString(wk + 2));
                        rowArray.put(res.getString(wk + 3));
                        
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getForecast13weeksByPart(String fromitem, String toitem, int wk) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getForecast13weeksByPartData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getForecast13weeksByPartData(fromitem, toitem, wk);
        }
        Object[][] data = jsonToData(jsonString); 
        
        Calendar cal = Calendar.getInstance();
        cal.getTime();
       
        
        
        ArrayList<Date> dates = OVData.getForecastDates(String.valueOf(cal.get(Calendar.YEAR)));
                
        DateFormat df = new SimpleDateFormat("MM/dd");
        // week dates are base 0
        String wk1 = df.format(dates.get(wk - 1));
        String wk2 = df.format(dates.get(wk));
        String wk3 = df.format(dates.get(wk + 1));
        String wk4 = df.format(dates.get(wk + 2));
        String wk5 = df.format(dates.get(wk + 3));
        String wk6 = df.format(dates.get(wk + 4));
        String wk7 = df.format(dates.get(wk + 5));
        String wk8 = df.format(dates.get(wk + 6));
        String wk9 = df.format(dates.get(wk + 7));
        String wk10 = df.format(dates.get(wk + 8));
        String wk11 = df.format(dates.get(wk + 9));
        String wk12 = df.format(dates.get(wk + 10));
        String wk13 = df.format(dates.get(wk + 11));
        
        
       
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("year"), getGlobalColumnTag("site"), wk1, wk2, wk3, wk4, wk5, wk6, wk7, wk8, wk9, wk10, wk11, wk12, wk13 })
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getForecast13weeksByPartData(String fromitem, String toitem, int wk) {
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
                Integer[] values = new Integer[13];
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                String thisyear = String.valueOf(cal.get(Calendar.YEAR));
                res = st.executeQuery("select * from fct_mstr where fct_item >= " + "'" + fromitem + "'" +
                                       " AND fct_item <= " + "'" + toitem + "'" + 
                                       " AND fct_year = " + "'" + thisyear + "'" + 
                                       ";" );
                   
                    while (res.next()) {
                        values = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0};
                        for (int k = 0 ; k < 13; k++) {
                             if ((wk + k) > 52) { continue;}
                          values[k] = res.getInt("fct_wkqty" + (wk + k));
                        }
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("fct_item"));
                        rowArray.put(res.getString("fct_year"));
                        rowArray.put(res.getString("fct_site"));
                        rowArray.put(values[0]);
                        rowArray.put(values[1]);
                        rowArray.put(values[2]);
                        rowArray.put(values[3]);
                        rowArray.put(values[4]);
                        rowArray.put(values[5]);
                        rowArray.put(values[6]);
                        rowArray.put(values[7]);
                        rowArray.put(values[8]);
                        rowArray.put(values[9]);
                        rowArray.put(values[10]);
                        rowArray.put(values[11]);
                        rowArray.put(values[12]);    
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getEDITPAll(String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDITPAllData"});
            list.add(new String[]{"param1", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDITPAllData(site);
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), 
                      getGlobalColumnTag("id"), 
                      getGlobalColumnTag("name"), 
                      getGlobalColumnTag("alias"), 
                      getGlobalColumnTag("default")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getEDITPAllData(String site) {
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
                if (site.equals("all")) { 
               res = st.executeQuery("select * from edp_partner inner join edpd_partner on edpd_parent = edp_id order by edp_id;");
            } else {
               res = st.executeQuery("select * from edp_partner inner join edpd_partner on edpd_parent = edp_id " +
                       " where edp_site = " + "'" + site + "'" + " order by edp_id;"); 
            }
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("edp_id"));
                        rowArray.put(res.getString("edp_desc"));
                        rowArray.put(res.getString("edpd_alias"));
                        rowArray.put(res.getString("edpd_default"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getEDITPDOCAll(String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDITPDOCAllData"});
            list.add(new String[]{"param1", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDITPDOCAllData(site);
        }
        Object[][] data = jsonToData(jsonString);  
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), 
                      getGlobalColumnTag("id"), 
                      getGlobalColumnTag("name"), 
                      getGlobalColumnTag("contact"), 
                      getGlobalColumnTag("website"), 
                      getGlobalColumnTag("phone")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getEDITPDOCAllData(String site) {
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
                if (site.equals("all")) { 
                res = st.executeQuery("select * from edi_mstr order by edi_id;");
            } else {
                res = st.executeQuery("select * from edi_mstr " + 
                        " where edi_site = " + "'" + site + "'" + " order by edi_id;");
            }
             
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("edi_id"));
                        rowArray.put(res.getString("edi_doc"));
                        rowArray.put(res.getString("edi_map"));
                        rowArray.put(res.getString("edi_fa_required"));
                        rowArray.put(res.getString("edi_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getEDIXrefAll(String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIXrefAllData"});
            list.add(new String[]{"param1", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDIXrefAllData(site);
        }
        Object[][] data = jsonToData(jsonString);   
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), 
                      "Partner GSID", 
                      "System GSID",
                      getGlobalColumnTag("type"), 
                      getGlobalColumnTag("tpaddr"), 
                      getGlobalColumnTag("bsaddr")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getEDIXrefAllData(String site) {
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
                if (site.equals("all")) {
                  res = st.executeQuery("select * from edi_xref order by exr_bsgs;");
                } else {
                  res = st.executeQuery("select * from edi_xref " + 
                          " where exr_site = " + "'" + site + "'" + " order by exr_bsgs;");  
                }
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("exr_tpgs"));
                        rowArray.put(res.getString("exr_bsgs"));
                        rowArray.put(res.getString("exr_type"));
                        rowArray.put(res.getString("exr_tpaddr"));
                        rowArray.put(res.getString("exr_bsaddr"));
                        jsonarray.put(rowArray);
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
    
     public static DefaultTableModel getEDIPartnerDocAll(String site) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDIPartnerDocAllData"});
            list.add(new String[]{"param1", site});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDIPartnerDocAllData(site); 
        }
        Object[][] data = jsonToData(jsonString); 
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), 
                          getGlobalColumnTag("id"), 
                          "Doc", 
                          "Sender ISA/UNB", 
                          "Receiver ISA/UNB",
                          getGlobalColumnTag("name"),
                          "Map", 
                          "Sender GS/UNG", 
                          "Receiver GS/UNG",
                          "OutDocType", 
                          "OutFileType", 
                          "IFS", 
                          "OFS"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
     
    public static String getEDIPartnerDocAllData(String site) {
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
                if (site.equals("all")) {
                  res = st.executeQuery("select * from edi_mstr inner join edp_partner on edp_id = edi_id order by edi_id;" );
                 } else {
                  res = st.executeQuery("select * from edi_mstr inner join edp_partner on edp_id = edi_id " +
                          " where edi_site = " + "'" + site + "'" + " order by edi_id;" );   
                 }
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("edi_id"));
                        rowArray.put(res.getString("edi_doc"));
                        rowArray.put(res.getString("edi_sndisa"));
                        rowArray.put(res.getString("edi_rcvisa"));
                        rowArray.put(res.getString("edp_desc"));
                        rowArray.put(res.getString("edi_map"));
                        rowArray.put(res.getString("edi_sndgs"));
                        rowArray.put(res.getString("edi_rcvgs"));
                        rowArray.put(res.getString("edi_doctypeout"));
                        rowArray.put(res.getString("edi_filetypeout"));
                        rowArray.put(res.getString("edi_ifs"));
                        rowArray.put(res.getString("edi_ofs"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getReqByApprover(String approver) {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getReqByApproverData"});
            list.add(new String[]{"param1", approver});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getReqByApproverData(approver);
        }
        Object[][] data = jsonToData(jsonString);   
           
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("requestor"), getGlobalColumnTag("date"), getGlobalColumnTag("type"), getGlobalColumnTag("vendor"), getGlobalColumnTag("amount"), getGlobalColumnTag("owner")}) {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 6 )       
                            return Double.class; 
                        else if (col == 0)
                            return ImageIcon.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
           return mymodel;
       }  
       
    public static String getReqByApproverData(String approver) {
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
               
                res = st.executeQuery("SELECT * FROM req_task inner join req_mstr where reqt_owner =  " +
                        "'" + approver + "'" + " AND reqt_status = 'pending' AND reqt_id = req_id " +
                        " order by req_id desc;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("req_id"));
                        rowArray.put(res.getString("req_name"));
                        rowArray.put(res.getString("req_date"));
                        rowArray.put(res.getString("req_type"));
                        rowArray.put(res.getString("req_vend"));
                        rowArray.put(res.getString("req_amt"));
                        rowArray.put(res.getString("reqt_owner"));
                        jsonarray.put(rowArray);
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
    
    
    
    
    public static DefaultTableModel getPrintersAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPrintersAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPrintersAllData();
        }
        Object[][] data = jsonToData(jsonString); 
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("printer"), getGlobalColumnTag("description"), getGlobalColumnTag("type"), getGlobalColumnTag("ip"), getGlobalColumnTag("port")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
    
    public static String getPrintersAllData() {
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
                res = st.executeQuery("select * from prt_mstr;" );
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("prt_id"));
                        rowArray.put(res.getString("prt_desc"));
                        rowArray.put(res.getString("prt_type"));
                        rowArray.put(res.getString("prt_ip"));
                        rowArray.put(res.getString("prt_port"));
                        jsonarray.put(rowArray);
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
    
   
    public static DefaultTableModel getLabelFileAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLabelFileAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getLabelFileAllData();
        }
        Object[][] data = jsonToData(jsonString); 
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("file"), getGlobalColumnTag("type")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
      
    public static String getLabelFileAllData() {
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
                res = st.executeQuery("select * from label_zebra;" );
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("lblz_code"));
                        rowArray.put(res.getString("lblz_desc"));
                        rowArray.put(res.getString("lblz_file"));
                        rowArray.put(res.getString("lblz_type"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getForecastAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getForecastAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getForecastAllData();
        }
        Object[][] data = jsonToData(jsonString); 
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("site"), getGlobalColumnTag("year"), "Wk1", "Wk2", "Wk3"})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
      
    public static String getForecastAllData() {
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
                res = st.executeQuery("select * from fct_mstr;" );
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("fct_item"));
                        rowArray.put(res.getString("fct_site"));
                        rowArray.put(res.getString("fct_year"));
                        rowArray.put(res.getString("fct_wkqty1"));
                        rowArray.put(res.getString("fct_wkqty2"));
                        rowArray.put(res.getString("fct_wkqty3"));
                        jsonarray.put(rowArray);
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
    
      
    public static DefaultTableModel getPlantDirectory() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPlantDirectoryData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPlantDirectoryData();
        }
        Object[][] data = jsonToData(jsonString);    
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                         new String[]{getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("phone"), getGlobalColumnTag("cell"), getGlobalColumnTag("email")});
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
           
           return mymodel;
       }   
         
    public static String getPlantDirectoryData() {
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
                res = st.executeQuery("SELECT * FROM  user_mstr order by user_lname;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("user_lname"));
                        rowArray.put(res.getString("user_fname"));
                        rowArray.put(res.getString("user_phone"));
                        rowArray.put(res.getString("user_cell"));
                        rowArray.put(res.getString("user_email"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getNavCodeList() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getNavCodeListData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getNavCodeListData();
        }
        Object[][] data = jsonToData(jsonString);    
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                         new String[]{getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("code"), getGlobalColumnTag("type"), getGlobalColumnTag("class")});
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
           return mymodel;
       }   
        
    public static String getNavCodeListData() {
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
               
                res = st.executeQuery("SELECT * from menu_mstr order by menu_id;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("menu_id"));
                        rowArray.put(res.getString("menu_desc"));
                        rowArray.put(res.getString("menu_navcode"));
                        rowArray.put(res.getString("menu_type"));
                        rowArray.put(res.getString("menu_panel"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getReqAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getReqAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getReqAllData();
        }
        Object[][] data = jsonToData(jsonString);      
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("requestor"), getGlobalColumnTag("date"), getGlobalColumnTag("po"), getGlobalColumnTag("vendor"), getGlobalColumnTag("amount"), getGlobalColumnTag("status")}) 
                    {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
           return mymodel;
       } 
        
    public static String getReqAllData() {
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
                
                res = st.executeQuery(" select * " +
                        " FROM  req_mstr order by req_id desc ;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("req_id"));
                        rowArray.put(res.getString("req_name"));
                        rowArray.put(res.getString("req_date"));
                        rowArray.put(res.getString("req_po"));
                        rowArray.put(res.getString("req_vend"));
                        rowArray.put(res.getString("req_amt"));
                        rowArray.put(res.getString("req_status"));
                        jsonarray.put(rowArray);
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
    
        
    public static DefaultTableModel getGLAcctAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGLAcctAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGLAcctAllData();
        }
        Object[][] data = jsonToData(jsonString);         
           javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("account"), getGlobalColumnTag("description"), getGlobalColumnTag("type"), getGlobalColumnTag("currency")})
                   {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        } 
           return mymodel;
       } 
        
    public static String getGLAcctAllData() {
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
                
                res = st.executeQuery("SELECT * FROM ac_mstr order by ac_id;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("ac_id"));
                        rowArray.put(res.getString("ac_desc"));
                        rowArray.put(res.getString("ac_type"));
                        rowArray.put(res.getString("ac_cur"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getItemRoutingAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemRoutingAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getItemRoutingAllData();
        }
        Object[][] data = jsonToData(jsonString);    
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("item"), getGlobalColumnTag("routing"), getGlobalColumnTag("operation"), getGlobalColumnTag("component")});

        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getItemRoutingAllData() {
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
                
                res = st.executeQuery("SELECT it_item, it_wf, ps_op, ps_child FROM item_mstr " +
                    " inner join bom_mstr on bom_item = it_item and bom_primary = '1' " +
                    " inner join pbm_mstr on ps_parent = bom_item and ps_bom = bom_id " +
                    " where it_code = 'M' order by it_item ;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_wf"));
                        rowArray.put(res.getString("ps_op"));
                        rowArray.put(res.getString("ps_child"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getItemBrowse() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemBrowseData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getItemBrowseData();
        }
        Object[][] data = jsonToData(jsonString);   
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("code"), getGlobalColumnTag("prodline"), 
                        getGlobalColumnTag("group"), 
                        getGlobalColumnTag("location"), 
                        getGlobalColumnTag("warehouse"), 
                        getGlobalColumnTag("createdate"), 
                        getGlobalColumnTag("sellprice"), 
                        getGlobalColumnTag("purchaseprice"), 
                        getGlobalColumnTag("revision")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else if (col == 9 || col == 10) {
                        return Double.class;
                    }
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getItemBrowseData() {
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
                
                res = st.executeQuery("SELECT it_item, it_desc, it_code, it_prodline, it_group, it_loc, it_wh, " +
                    " it_createdate, it_sell_price, it_pur_price, it_rev from item_mstr order by it_item; ") ;
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("it_prodline"));
                        rowArray.put(res.getString("it_group"));
                        rowArray.put(res.getString("it_loc"));
                        rowArray.put(res.getString("it_wh"));
                        rowArray.put(res.getString("it_createdate"));
                        rowArray.put(res.getString("it_sell_price"));
                        rowArray.put(res.getString("it_pur_price"));
                        rowArray.put(res.getString("it_rev"));
                        jsonarray.put(rowArray);
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
    
   
    public static DefaultTableModel getEmployeeAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEmployeeAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEmployeeAllData();
        }
        Object[][] data = jsonToData(jsonString);   
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("empid"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("dept"), getGlobalColumnTag("status"), getGlobalColumnTag("shift"), getGlobalColumnTag("type"), getGlobalColumnTag("startdate"), getGlobalColumnTag("termdate")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getEmployeeAllData() {
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
             
                res = st.executeQuery("SELECT * FROM emp_mstr order by emp_nbr;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("emp_nbr"));
                        rowArray.put(res.getString("emp_lname"));
                        rowArray.put(res.getString("emp_fname"));
                        rowArray.put(res.getString("emp_dept"));
                        rowArray.put(res.getString("emp_status"));
                        rowArray.put(res.getString("emp_shift"));
                        rowArray.put(res.getString("emp_type"));
                        rowArray.put(res.getString("emp_startdate"));
                        rowArray.put(res.getString("emp_termdate"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getGenCodeAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGenCodeAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGenCodeAllData();
        }
        Object[][] data = jsonToData(jsonString);  
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
       new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("key"), getGlobalColumnTag("description")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getGenCodeAllData() {
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
             
                res = st.executeQuery("SELECT code_code, code_key, code_value " +
                    "from code_mstr order by code_code ;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("code_code"));
                        rowArray.put(res.getString("code_key"));
                        rowArray.put(res.getString("code_value"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getFreightCodeAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightCodeAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightCodeAllData();
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
       new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("key"), getGlobalColumnTag("description")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getFreightCodeAllData() {
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
             
                res = st.executeQuery("SELECT freight_code, freight_key, freight_value " +
                    "from code_freight order by freight_code ;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("freight_code"));
                        rowArray.put(res.getString("freight_key"));
                        rowArray.put(res.getString("freight_value"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getWorkCellAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWorkCellAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getWorkCellAllData();
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
       new String[]{getGlobalColumnTag("select"),getGlobalColumnTag("cell"), getGlobalColumnTag("description"), getGlobalColumnTag("site"), getGlobalColumnTag("dept"), "SetupRate$", "LaborRate$", "BurdenRate$", "RunCrewSize", "SetupCrewSize", "Remarks"})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getWorkCellAllData() {
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
             
                res = st.executeQuery("select * from wc_mstr;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("wc_cell"));
                        rowArray.put(res.getString("wc_desc").replace(",", ""));
                        rowArray.put(res.getString("wc_site"));
                        rowArray.put(res.getString("wc_cc"));
                        rowArray.put(res.getString("wc_setup_rate"));
                        rowArray.put(res.getString("wc_run_rate"));
                        rowArray.put(res.getString("wc_bdn_rate"));
                        rowArray.put(res.getString("wc_run_crew"));
                        rowArray.put(res.getString("wc_setup"));
                        rowArray.put(res.getString("wc_remarks").replace(",", ""));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getReqPending() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getReqPendingData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getReqPendingData();
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("requestor"), getGlobalColumnTag("date"), getGlobalColumnTag("vendor"), getGlobalColumnTag("amount"), getGlobalColumnTag("status"), getGlobalColumnTag("approver"), getGlobalColumnTag("description")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 5 )       
                        return Double.class;  
                    else if (col == 0)
                        return ImageIcon.class;
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getReqPendingData() {
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
                    res = st.executeQuery("SELECT * FROM  req_mstr inner join req_task on reqt_id = req_id where req_status = 'pending' and reqt_status = 'pending' order by req_id desc;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("req_id"));
                        rowArray.put(res.getString("req_name"));
                        rowArray.put(res.getString("req_date"));
                        rowArray.put(res.getString("req_vend"));
                        rowArray.put(res.getString("req_amt"));
                        rowArray.put(res.getString("req_status"));
                        rowArray.put(res.getString("reqt_owner"));
                        rowArray.put(res.getString("req_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getReqApproved() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getReqApprovedData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getReqApprovedData();
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("requestor"), getGlobalColumnTag("date"), getGlobalColumnTag("po"), getGlobalColumnTag("vendor"), getGlobalColumnTag("amount"), getGlobalColumnTag("status")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 6 )       
                        return Double.class;  
                    else if (col == 0)
                        return ImageIcon.class;
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getReqApprovedData() {
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
                    res = st.executeQuery("SELECT * FROM req_mstr where req_status = 'approved' order by req_id desc;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("req_id"));
                        rowArray.put(res.getString("req_name"));
                        rowArray.put(res.getString("req_date"));
                        rowArray.put(res.getString("req_po"));
                        rowArray.put(res.getString("req_vend"));
                        rowArray.put(res.getString("req_amt"));
                        rowArray.put(res.getString("req_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getUserAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getUserAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getUserAllData();
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                   new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("userid"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("phone"), getGlobalColumnTag("cell"), getGlobalColumnTag("email")}){
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getUserAllData() {
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
                    res = st.executeQuery("SELECT user_id, user_lname, user_fname, user_phone, user_cell, user_email  FROM  user_mstr order by user_lname desc;");

                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("user_id"));
                        rowArray.put(res.getString("user_lname"));
                        rowArray.put(res.getString("user_fname"));
                        rowArray.put(res.getString("user_phone"));
                        rowArray.put(res.getString("user_cell"));
                        rowArray.put(res.getString("user_email"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getProdCodeAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getProdCodeAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getProdCodeAllData();
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    };

        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getProdCodeAllData() {
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
                    res = st.executeQuery("SELECT pl_line, pl_desc FROM  pl_mstr order by pl_line;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("pl_line"));
                        rowArray.put(res.getString("pl_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getQPRAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getQPRAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getQPRAllData();
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                     new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("number"), getGlobalColumnTag("user"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("vendor"), getGlobalColumnTag("createdate"), getGlobalColumnTag("date")})
               {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getQPRAllData() {
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
                    res = st.executeQuery("SELECT * FROM  qual_mstr order by qual_id;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("qual_id"));
                        rowArray.put(res.getString("qual_originator"));
                        rowArray.put(res.getString("qual_item"));
                        rowArray.put(res.getString("qual_item_desc"));
                        rowArray.put(res.getString("qual_vend"));
                        rowArray.put(res.getString("qual_date_crt"));
                        rowArray.put(res.getString("qual_date_upd"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getShipperAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShipperAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getShipperAllData();
        }
        Object[][] data = jsonToData(jsonString); 
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{getGlobalColumnTag("shipper"), getGlobalColumnTag("customer"), getGlobalColumnTag("item"), getGlobalColumnTag("po"), getGlobalColumnTag("quantity"), getGlobalColumnTag("price"), getGlobalColumnTag("shipdate")});
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   }   

    public static String getShipperAllData() {
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
                    res = st.executeQuery("SELECT * FROM  ship_mstr inner join ship_det on shd_id = sh_id order by sh_id;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("shd_id"));
                        rowArray.put(res.getString("sh_cust"));
                        rowArray.put(res.getString("shd_item"));
                        rowArray.put(res.getString("shd_po"));
                        rowArray.put(res.getString("shd_qty"));
                        rowArray.put(res.getString("shd_netprice"));
                        rowArray.put(res.getString("sh_shipdate"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getOrderOpen() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getOrderOpenData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getOrderOpenData();
        }
        Object[][] data = jsonToData(jsonString);
       javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                     new String[]{getGlobalColumnTag("order"), getGlobalColumnTag("customer"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("po"), getGlobalColumnTag("orderqty"), getGlobalColumnTag("shipqty")});
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
   } 

    public static String getOrderOpenData() {
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
           
                    res = st.executeQuery("SELECT sod_nbr, so_cust, sod_item, ifnull(it_desc,'') as 'description', sod_po, sod_ord_qty, sod_shipped_qty FROM  so_mstr inner join sod_det on sod_nbr = so_nbr " +
                    " left outer join item_mstr on it_item = sod_item " +
                    " where (so_status = 'open' or so_status = 'backorder') and (sod_status <> 'Shipped' or sod_status is null) order by so_nbr;");

                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("sod_nbr"));
                        rowArray.put(res.getString("so_cust"));
                        rowArray.put(res.getString("sod_item"));
                        rowArray.put(res.getString("description"));
                        rowArray.put(res.getString("sod_po"));
                        rowArray.put(res.getString("sod_ord_qty"));
                        rowArray.put(res.getString("sod_shipped_qty"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getDBSchema() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDBSchemaData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDBSchemaData();
        }
        Object[][] data = jsonToData(jsonString);
   javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
               new String[]{"TableName", "ColumnName", "ColumnType"});
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
   return mymodel;
} 

    public static String getDBSchemaData() {
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
                if (dbtype.equals("sqlite")) {
                    res = st.executeQuery("select m.name as tablename, p.name as columnname, p.type as columntype from sqlite_master m left outer join pragma_table_info((m.name)) p on m.name <> p.name order by tablename, columnname;"); 
                     while (res.next()) {
                                  JSONArray rowArray = new JSONArray(); 
                                  rowArray.put(res.getString("tablename"));
                                  rowArray.put(res.getString("columnname"));
                                  rowArray.put(res.getString("columntype"));
                                  jsonarray.put(rowArray);
                              }
                } else {
                   res = st.executeQuery("SELECT table_name, column_name, column_type from information_schema.columns where table_schema = 'bsdb' ;");  
                    while (res.next()) {
                                  JSONArray rowArray = new JSONArray(); 
                                  rowArray.put(res.getString("tablename"));
                                  rowArray.put(res.getString("columnname"));
                                  rowArray.put(res.getString("columntype"));
                                  jsonarray.put(rowArray);
                              }
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
    
    public static DefaultTableModel getRoutingsAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getRoutingsAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getRoutingsAllData();
        }
        Object[][] data = jsonToData(jsonString);
      javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"),getGlobalColumnTag("operation"), getGlobalColumnTag("description"), getGlobalColumnTag("site"), "Reportable", getGlobalColumnTag("cell"), getGlobalColumnTag("setuphours"), getGlobalColumnTag("runhours")})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;

 }

    public static String getRoutingsAllData() {
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
                     res = st.executeQuery("select * from wf_mstr;");
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("wf_id"));
                        rowArray.put(res.getString("wf_op"));
                        rowArray.put(res.getString("wf_desc"));
                        rowArray.put(res.getString("wf_site"));
                        rowArray.put(res.getString("wf_assert"));
                        rowArray.put(res.getString("wf_cell"));
                        rowArray.put(res.getString("wf_setup_hours"));
                        rowArray.put(res.getString("wf_run_hours"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getLocationsAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getLocationsAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getRoutingsAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("site"), getGlobalColumnTag("active")})
        {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
 }

    public static String getLocationsAllData() {
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
                  res = st.executeQuery("select * from loc_mstr;");
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("loc_loc"));
                        rowArray.put(res.getString("loc_desc"));
                        rowArray.put(res.getString("loc_site"));
                        rowArray.put(res.getString("loc_active"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getWareHousesAll() {
    String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWareHousesAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getWareHousesAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("site"), getGlobalColumnTag("name"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip")})
        {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
return mymodel;

 }

    public static String getWareHousesAllData() {
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
                 res = st.executeQuery("select * from wh_mstr;");
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("wh_id"));
                        rowArray.put(res.getString("wh_site"));
                        rowArray.put(res.getString("wh_name"));
                        rowArray.put(res.getString("wh_city"));
                        rowArray.put(res.getString("wh_state"));
                        rowArray.put(res.getString("wh_zip"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getDeptsAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDeptsAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDeptsAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), "COPAcct", "LBRAcct", "BDNAcct"})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }

    public static String getDeptsAllData() {
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
                 res = st.executeQuery("select * from dept_mstr;");
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("dept_id"));
                        rowArray.put(res.getString("dept_desc"));
                        rowArray.put(res.getString("dept_cop_acct"));
                        rowArray.put(res.getString("dept_lbr_acct"));
                        rowArray.put(res.getString("dept_bdn_acct"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getBankAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBankAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getBankAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("account"), getGlobalColumnTag("currency"), getGlobalColumnTag("active")})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }

    public static String getBankAllData() {
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
                 res = st.executeQuery("select * from bk_mstr;");
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("bk_id"));
                        rowArray.put(res.getString("bk_desc"));
                        rowArray.put(res.getString("bk_acct"));
                        rowArray.put(res.getString("bk_cur"));
                        rowArray.put(res.getString("bk_active"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getUnPostedGLTrans() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getUnPostedGLTransData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getUnPostedGLTransData();
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel =  new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("id"), getGlobalColumnTag("account"), getGlobalColumnTag("description"), getGlobalColumnTag("costcenter"), getGlobalColumnTag("type"), getGlobalColumnTag("document"), getGlobalColumnTag("reference"), getGlobalColumnTag("description"), getGlobalColumnTag("effectivedate"), getGlobalColumnTag("date"), getGlobalColumnTag("amount")})
                      {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 10) {      
                    return Double.class;
                } else if ( col == 0) {
                 return Integer.class;   
                } else return String.class;  //other columns accept String values  
              }  
                };  
           
       for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         }
 
    public static String getUnPostedGLTransData() {
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
                  res = st.executeQuery("SELECT glt_id, glt_acct, glt_doc, glt_cc, glt_ref, glt_effdate, glt_entdate, glt_base_amt, glt_desc, glt_type, ac_desc " +
                        "from gl_tran inner join ac_mstr on ac_id = glt_acct order by glt_id desc ;");
                   
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("glt_id"));
                        rowArray.put(res.getString("glt_acct"));
                        rowArray.put(res.getString("ac_desc"));
                        rowArray.put(res.getString("glt_cc"));
                        rowArray.put(res.getString("glt_type"));
                        rowArray.put(res.getString("glt_doc"));
                        rowArray.put(res.getString("glt_ref"));
                        rowArray.put(res.getString("glt_desc"));
                        rowArray.put(res.getString("glt_effdate"));
                        rowArray.put(res.getString("glt_entdate"));
                        rowArray.put(res.getString("glt_base_amt"));
                        jsonarray.put(rowArray);
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
    
       
    public static DefaultTableModel getARPaymentBrowse() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getARPaymentBrowseData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getARPaymentBrowseData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("number"), getGlobalColumnTag("customer"), getGlobalColumnTag("invoice"), getGlobalColumnTag("checknbr"), getGlobalColumnTag("applied"), getGlobalColumnTag("amount")}); 
             
       for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;
        
         }
      
    public static String getARPaymentBrowseData() {
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
                 res = st.executeQuery("select ar_cust, ar_effdate, ar_nbr, ard_amt, ar_ref, ard_ref, ar_amt from ar_mstr inner join ard_mstr on ard_nbr = ar_nbr where ar_type = 'P' order by ar_id desc; ");
                               
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray();
                        rowArray.put(res.getString("ar_nbr"));
                        rowArray.put(res.getString("ar_cust"));
                        rowArray.put(res.getString("ard_ref"));
                        rowArray.put(res.getString("ar_ref"));
                        rowArray.put(res.getString("ard_amt"));
                        rowArray.put(res.getString("ar_amt"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getMenusAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getMenusAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getMenusAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("class")})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }

    public static String getMenusAllData() {
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
                  res = st.executeQuery("select * from menu_mstr order by menu_id;");
            
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("menu_id"));
                        rowArray.put(res.getString("menu_desc"));
                        rowArray.put(res.getString("menu_panel"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getPanelsAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPanelsAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPanelsAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("class"), getGlobalColumnTag("description")})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }

    public static String getPanelsAllData() {
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
                    res = st.executeQuery("select * from panel_mstr order by panel_id;");
            
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("panel_id"));
                        rowArray.put(res.getString("panel_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getTermsAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getTermsAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getTermsAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description")})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }

    public static String getTermsAllData() {
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
                    res = st.executeQuery("select * from cust_term;");
            
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cut_code"));
                        rowArray.put(res.getString("cut_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getWorkFlowAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getWorkFlowAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getWorkFlowAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("enabled")})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }
    
    public static String getWorkFlowAllData() {
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
                 res = st.executeQuery("select * from wkf_mstr;");
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("wkf_id"));
                        rowArray.put(res.getString("wkf_desc"));
                        rowArray.put(res.getString("wkf_enabled"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getAS2All() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getAS2AllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getAS2AllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("description"), getGlobalColumnTag("url"), getGlobalColumnTag("port"), getGlobalColumnTag("path"), getGlobalColumnTag("user"), getGlobalColumnTag("enabled") })
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }

    public static String getAS2AllData() {
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
                res = st.executeQuery("select * from as2_mstr;");
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("as2_id"));
                        rowArray.put(res.getString("as2_desc"));
                        rowArray.put(res.getString("as2_url"));
                        rowArray.put(res.getString("as2_port"));
                        rowArray.put(res.getString("as2_path"));
                        rowArray.put(res.getString("as2_user"));
                        rowArray.put(res.getString("as2_enabled"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getCronAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCronAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCronAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), 
                  getGlobalColumnTag("id"), 
                  getGlobalColumnTag("description"), 
                  getGlobalColumnTag("group"), 
                  getGlobalColumnTag("program"), 
                  getGlobalColumnTag("parameter"), 
                  getGlobalColumnTag("expression"), 
                  getGlobalColumnTag("enabled"),
                  getGlobalColumnTag("modified"),
                  getGlobalColumnTag("lastrun"),
                  getGlobalColumnTag("lastmod")})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }

    public static String getCronAllData() {
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
                  res = st.executeQuery("select * from cron_mstr;");
            
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cron_jobid"));
                        rowArray.put(res.getString("cron_desc"));
                        rowArray.put(res.getString("cron_group"));
                        rowArray.put(res.getString("cron_prog"));
                        rowArray.put(res.getString("cron_param"));
                        rowArray.put(res.getString("cron_expression"));
                        rowArray.put(res.getString("cron_enabled"));
                        rowArray.put(res.getString("cron_modflag"));
                        rowArray.put(res.getString("cron_lastrun"));
                        rowArray.put(res.getString("cron_lastmod"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getPKSAll() {
      String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPKSAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPKSAllData();
        }
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
              new String[]{getGlobalColumnTag("select"), 
                  getGlobalColumnTag("id"), 
                  getGlobalColumnTag("description"), 
                  getGlobalColumnTag("type"), 
                  getGlobalColumnTag("parent"), 
                  getGlobalColumnTag("file"), 
                  getGlobalColumnTag("user")})
              {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class;  
                else return String.class;  //other columns accept String values  
              }  
                }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

 }

    public static String getPKSAllData() {
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
                res = st.executeQuery("select * from pks_mstr;");
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("pks_id"));
                        rowArray.put(res.getString("pks_desc"));
                        rowArray.put(res.getString("pks_type"));
                        rowArray.put(res.getString("pks_parent"));
                        rowArray.put(res.getString("pks_file"));
                        rowArray.put(res.getString("pks_user"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getFreightAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightAllData();
        }
        Object[][] data = jsonToData(jsonString);  
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("enabled")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getFreightAllData() {
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
                 res = st.executeQuery("select * from car_mstr;");
               
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("car_id"));
                        rowArray.put(res.getString("car_desc"));
                        rowArray.put(res.getString("car_apply"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getCarrierAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCarrierAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCarrierAllData();
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("id"), getGlobalColumnTag("phone"), getGlobalColumnTag("email"), getGlobalColumnTag("name"), getGlobalColumnTag("city"), getGlobalColumnTag("state")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getCarrierAllData() {
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
                 res = st.executeQuery("select * from car_mstr;");
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("car_id"));
                        rowArray.put(res.getString("car_desc"));
                        rowArray.put(res.getString("car_scac"));
                        rowArray.put(res.getString("car_phone"));
                        rowArray.put(res.getString("car_email"));
                        rowArray.put(res.getString("car_name"));
                        rowArray.put(res.getString("car_city"));
                        rowArray.put(res.getString("car_state"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getVehicleAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVehicleAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVehicleAllData();
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("make"), getGlobalColumnTag("model"), getGlobalColumnTag("year")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getVehicleAllData() {
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
                  res = st.executeQuery("select * from veh_mstr;");
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("veh_id"));
                        rowArray.put(res.getString("veh_desc"));
                        rowArray.put(res.getString("veh_make"));
                        rowArray.put(res.getString("veh_model"));
                        rowArray.put(res.getString("veh_year"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getDriverAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDriverAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDriverAllData();
        }
        Object[][] data = jsonToData(jsonString);      
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("phone"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip") })
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getDriverAllData() {
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
                res = st.executeQuery("select * from drv_mstr;");
                               
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("drv_id"));
                        rowArray.put(res.getString("drv_lname"));
                        rowArray.put(res.getString("drv_fname"));
                        rowArray.put(res.getString("drv_phone"));
                        rowArray.put(res.getString("drv_city"));
                        rowArray.put(res.getString("drv_state"));
                        rowArray.put(res.getString("drv_zip"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getBrokerAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getBrokerAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getBrokerAllData();
        }
        Object[][] data = jsonToData(jsonString);       
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("name"), getGlobalColumnTag("contact"), getGlobalColumnTag("phone"), getGlobalColumnTag("email"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip") })
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getBrokerAllData() {
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
                res = st.executeQuery("select * from brk_mstr;");
               
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("brk_id"));
                        rowArray.put(res.getString("brk_name"));
                        rowArray.put(res.getString("brk_contact"));
                        rowArray.put(res.getString("brk_phone"));
                        rowArray.put(res.getString("brk_email"));
                        rowArray.put(res.getString("brk_city"));
                        rowArray.put(res.getString("brk_state"));
                        rowArray.put(res.getString("brk_zip"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getTaxAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getTaxAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getTaxAllData();
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("element"), getGlobalColumnTag("percent"), getGlobalColumnTag("userid")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getTaxAllData() {
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
                res = st.executeQuery("select tax_code, tax_desc, taxd_desc, taxd_percent, tax_userid from tax_mstr inner join taxd_mstr on taxd_parentcode = tax_code ;");
               
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("tax_code"));
                        rowArray.put(res.getString("tax_desc"));
                        rowArray.put(res.getString("taxd_desc"));
                        rowArray.put(res.getString("taxd_percent"));
                        rowArray.put(res.getString("tax_userid"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getPayProfileAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPayProfileAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getPayProfileAllData();
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("element"), getGlobalColumnTag("type"), getGlobalColumnTag("amount"), getGlobalColumnTag("amounttype"), getGlobalColumnTag("userid")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getPayProfileAllData() {
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
                res = st.executeQuery("select * from pay_profile inner join pay_profdet on paypd_parentcode = payp_code ;");
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("payp_code"));
                        rowArray.put(res.getString("payp_desc"));
                        rowArray.put(res.getString("paypd_desc"));
                        rowArray.put(res.getString("paypd_type"));
                        rowArray.put(res.getString("paypd_amt"));
                        rowArray.put(res.getString("paypd_amttype"));
                        rowArray.put(res.getString("payp_userid"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getSitesAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSitesAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getSitesAllData();
        }
        Object[][] data = jsonToData(jsonString);      
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("site"), getGlobalColumnTag("description"), getGlobalColumnTag("image"), getGlobalColumnTag("formatinvoice"), getGlobalColumnTag("formatpackingslip")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getSitesAllData() {
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
                res = st.executeQuery("select site_site, site_desc, site_logo, site_iv_jasper, site_sh_jasper from site_mstr;" );
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("site_site"));
                        rowArray.put(res.getString("site_desc"));
                        rowArray.put(res.getString("site_logo"));
                        rowArray.put(res.getString("site_iv_jasper"));
                        rowArray.put(res.getString("site_sh_jasper"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getGLCalendar() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGLCalendarData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getGLCalendarData();
        }
        Object[][] data = jsonToData(jsonString);    
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("year"), getGlobalColumnTag("period"), getGlobalColumnTag("startdate"), getGlobalColumnTag("enddate"), getGlobalColumnTag("active")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getGLCalendarData() {
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
                res = st.executeQuery("select * from gl_cal;");
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("glc_year"));
                        rowArray.put(res.getString("glc_per"));
                        rowArray.put(res.getString("glc_start"));
                        rowArray.put(res.getString("glc_end"));
                        rowArray.put(res.getString("glc_status"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getNoStdCostItems() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getNoStdCostItemsData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getNoStdCostItemsData();
        }
        Object[][] data = jsonToData(jsonString);  
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("type")}); 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getNoStdCostItemsData() {
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
                 res = st.executeQuery("select it_item, it_desc, it_code from item_mstr where it_item not in (select itc_item from item_cost where itc_item = it_item) order by it_item;");
                
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_code"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getCustAddrInfoAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getCustAddrInfoAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getCustAddrInfoAllData();
        }
        Object[][] data = jsonToData(jsonString);   
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("market"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getCustAddrInfoAllData() {
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
                 res = st.executeQuery("SELECT cm_code, cm_market, cm_name, cm_line1, cm_city, cm_state, cm_zip " +
                    "from cm_mstr order by cm_code ;");

            
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("cm_code"));
                        rowArray.put(res.getString("cm_market"));
                        rowArray.put(res.getString("cm_name"));
                        rowArray.put(res.getString("cm_line1"));
                        rowArray.put(res.getString("cm_city"));
                        rowArray.put(res.getString("cm_state"));
                        rowArray.put(res.getString("cm_zip"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getVendorAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getVendorAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getVendorAllData();
        }
        Object[][] data = jsonToData(jsonString);     
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                  new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("name"), getGlobalColumnTag("addr1"), getGlobalColumnTag("city"), getGlobalColumnTag("state"), getGlobalColumnTag("zip")})
                  {
                  @Override  
                  public Class getColumnClass(int col) {  
                    if (col == 0)       
                        return ImageIcon.class;  
                    else return String.class;  //other columns accept String values  
                  }  
                    }; 

    for (Object[] rowData : data) {
        mymodel.addRow(rowData);
    }
    return mymodel;

     }

    public static String getVendorAllData() {
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
                res = st.executeQuery("SELECT vd_addr, vd_name, vd_line1, vd_city, vd_state, vd_zip " +
                    "from vd_mstr order by vd_addr ;");

           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("vd_addr"));
                        rowArray.put(res.getString("vd_name"));
                        rowArray.put(res.getString("vd_line1"));
                        rowArray.put(res.getString("vd_city"));
                        rowArray.put(res.getString("vd_state"));
                        rowArray.put(res.getString("vd_zip"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getShiftAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getShiftAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getShiftAllData();
        }        
       
        Object[][] data = jsonToData(jsonString);
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("shift"), getGlobalColumnTag("description")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }  
        return mymodel;
        
         }
       
    public static String getShiftAllData() {
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
                res = st.executeQuery("select * from shift_mstr;" );
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("shf_id"));
                        rowArray.put(res.getString("shf_desc"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getClockCodesAll() {
        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getClockCodesAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getClockCodesAllData();
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("code"), getGlobalColumnTag("description"), getGlobalColumnTag("payable"), getGlobalColumnTag("code")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        } 
        return mymodel;
        
         }
    
    public static String getClockCodesAllData() {
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
                res = st.executeQuery("select clc_code, clc_desc, case when clc_payable = '1' then 'yes' else 'no' end as 'payable', " +
                          " case when clc_syscode = '1' then 'yes' else 'no' end as 'syscode' " +
                          " from clock_code order by clc_code;" );
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("clc_code"));
                        rowArray.put(res.getString("clc_desc"));
                        rowArray.put(res.getString("payable"));
                        rowArray.put(res.getString("syscode"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getClockRecords66All() {
        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getClockRecords66AllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getClockRecords66AllData();
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("id"), getGlobalColumnTag("empid"), getGlobalColumnTag("lastname"), getGlobalColumnTag("firstname"), getGlobalColumnTag("dept"), getGlobalColumnTag("code"), getGlobalColumnTag("indate"), getGlobalColumnTag("intime"), "InTmAdj", getGlobalColumnTag("outdate"), getGlobalColumnTag("outtime"), "OutTmAdj", getGlobalColumnTag("totalhours")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
             
              
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        } 
        return mymodel;
        
         }
    
    public static String getClockRecords66AllData() {
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
                res = st.executeQuery("SELECT * FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr where t.code_id = '66';"  );
                 
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("t.recid"));
                        rowArray.put(res.getString("t.emp_nbr"));
                        rowArray.put(res.getString("e.emp_lname"));
                        rowArray.put(res.getString("e.emp_fname"));
                        rowArray.put(res.getString("e.emp_dept"));
                        rowArray.put(res.getString("t.code_id"));
                        rowArray.put(res.getString("t.indate"));
                        rowArray.put(res.getString("t.intime"));
                        rowArray.put(res.getString("t.intime_adj"));
                        rowArray.put(res.getString("t.outdate"));
                        rowArray.put(res.getString("t.outtime"));
                        rowArray.put(res.getString("t.outtime_adj"));
                        rowArray.put(res.getString("t.tothrs"));
                        jsonarray.put(rowArray);
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
    
    public static DefaultTableModel getQOHvsSSAll() {
        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getQOHvsSSAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getQOHvsSSAllData();
        }        
       
        Object[][] data = jsonToData(jsonString);
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("uom"), getGlobalColumnTag("type"), getGlobalColumnTag("status"), getGlobalColumnTag("qoh"), getGlobalColumnTag("safetystock")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
       return mymodel;
        
         } 
     
    public static String getQOHvsSSAllData() {
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
                res = st.executeQuery("SELECT it_item, it_desc, it_uom, it_type, it_status, sum(in_qoh) as 'sum', it_safestock  " +
                        " FROM  item_mstr inner join in_mstr on in_item = it_item  " +
                        " group by it_item, it_desc, it_uom, it_type, it_status order by it_item ;");
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_uom"));
                        rowArray.put(res.getString("it_type"));
                        rowArray.put(res.getString("it_status"));
                        rowArray.put(res.getString("sum"));
                        rowArray.put(res.getString("it_safestock"));
                        jsonarray.put(rowArray);
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
    
    
    public static DefaultTableModel getItemInfoAll() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getItemInfoAllData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getItemInfoAllData();
        }        
       
        Object[][] data = jsonToData(jsonString);
        
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("select"), getGlobalColumnTag("item"), getGlobalColumnTag("description"), getGlobalColumnTag("uom"), getGlobalColumnTag("type"), getGlobalColumnTag("status"), getGlobalColumnTag("site"), getGlobalColumnTag("prodline"), getGlobalColumnTag("rev"), getGlobalColumnTag("class"), getGlobalColumnTag("routing")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
        
         } 
    
    public static String getItemInfoAllData() {
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
                res = st.executeQuery("SELECT it_item, it_desc, it_uom, it_type, it_status, it_site, it_prodline, it_rev, it_code, it_wf  " +
                        " FROM  item_mstr  " +
                        " order by it_item ;");
                 
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select"); // BlueSeerUtils.clickflag
                        rowArray.put(res.getString("it_item"));
                        rowArray.put(res.getString("it_desc"));
                        rowArray.put(res.getString("it_uom"));
                        rowArray.put(res.getString("it_type"));
                        rowArray.put(res.getString("it_status"));
                        rowArray.put(res.getString("it_site"));
                        rowArray.put(res.getString("it_prodline"));
                        rowArray.put(res.getString("it_rev"));
                        rowArray.put(res.getString("it_code"));
                        rowArray.put(res.getString("it_wf"));
                        jsonarray.put(rowArray);
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
      
     
    public static DefaultTableModel getFreightRejectionCodeDT() {
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFreightRejectionCodeDTData"});
            try {
                jsonString = sendServerPost(list, "", null, "dataServDT"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFreightRejectionCodeDTData();
        }
        Object[][] data = jsonToData(jsonString);   
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{getGlobalColumnTag("code"), getGlobalColumnTag("description")})
                {
                      @Override  
                      public Class getColumnClass(int col) {  
                        return String.class;  //other columns accept String values  
                      }  
                        }; 
        
        
        for (Object[] rowData : data) {
        mymodel.addRow(rowData);
        }
        return mymodel;
    }
    
    public static String getFreightRejectionCodeDTData() {
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
                res = st.executeQuery("SELECT code_key, code_value " +
                    "from code_mstr where code_code = 'freightrejectioncodes' order by code_key ;");
           
                    while (res.next()) {
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("code_key"));
                        rowArray.put(res.getString("code_value"));
                        jsonarray.put(rowArray);
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
    
    
    
    
   
    
}
