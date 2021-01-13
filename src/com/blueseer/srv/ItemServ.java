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
package com.blueseer.srv;

import bsmf.MainFrame;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author terryva
 */
public class ItemServ extends HttpServlet {
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        String id = request.getParameter("id");
        String fromitem = request.getParameter("fromitem");
        String toitem = request.getParameter("toitem");
        String fromcode = request.getParameter("fromcode");
        String tocode = request.getParameter("tocode");
        String status = request.getParameter("status");
        if (id != null && ! id.isEmpty()) {
            response.getWriter().println(getItemJSON(id));
        } else {
           response.getWriter().println(getItemListByVarJSON(fromitem,toitem,fromcode,tocode,status)); 
        }
    }

 
    

 
public static String getItemJSON(String id) {
       
        String x = ""; 
        
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
               res = st.executeQuery("select it_item as 'ItemNumber', " +
               " it_site as 'Site', it_uom as 'UOM', it_code as 'TypeCode', it_wf as 'RoutingCode',  " +
               " it_loc as 'Location', it_wh as 'Warehouse', " +
               " it_sell_price as 'SellingPrice', it_pur_price as 'PurchasePrice', it_mtl_cost as 'MaterialCost', " +
               " it_leadtime as 'LeadTime', it_safestock as 'SafetyStock', " +
               " it_rev as 'Revision', it_custrev as 'CustRevision', " +  
               " it_taxcode as 'TaxCode', it_custrev as 'Status', " +  
               " it_group as 'Group', it_drawing as 'Drawing' " +          
               " from item_mstr " +   
               " where it_item = " + "'" + id + "'" + 
               ";");
                   
                   org.json.simple.JsonArray json = new org.json.simple.JsonArray();
                    ResultSetMetaData rsmd = res.getMetaData(); 
                    while (res.next()) {
                        int numColumns = rsmd.getColumnCount();
                        LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<String, Object>();
                          for (int i=1; i<=numColumns; i++) {
                            String column_name = rsmd.getColumnName(i);
                            jsonOrderedMap.put(column_name, res.getObject(column_name));
                          }
                          json.add(jsonOrderedMap);
                    }
                    x = json.toJson();
                    
              
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return x;
        
         } 
     
public static String getItemListByVarJSON(String fromitem, String toitem, String fromcode, String tocode, String status) {
       
        String x = ""; 
        if (fromitem == null || fromitem.isEmpty()) {
            fromitem = bsmf.MainFrame.lownbr;
        }
        if (toitem == null || toitem.isEmpty()) {
            toitem = bsmf.MainFrame.hinbr;
        }
        if (fromcode == null || fromcode.isEmpty()) {
            fromcode = bsmf.MainFrame.lowchar;
        }
        if (tocode == null || tocode.isEmpty()) {
            tocode = bsmf.MainFrame.hichar;
        }
        if (status == null) {
            status = "";
        }
       
       // System.out.println("HERE: " + fromnbr + "/" + tonbr + "/" + fromdate + "/" + todate + "/" + fromcust + "/" + tocust);
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
               res = st.executeQuery("select it_item as 'ItemNumber', " +
               " it_site as 'Site', it_uom as 'UOM', it_code as 'TypeCode', it_wf as 'RoutingCode',  " +
               " it_loc as 'Location', it_wh as 'Warehouse', " +
               " it_sell_price as 'SellingPrice', it_pur_price as 'PurchasePrice', it_mtl_cost as 'MaterialCost', " +
               " it_leadtime as 'LeadTime', it_safestock as 'SafetyStock', " +
               " it_rev as 'Revision', it_custrev as 'CustRevision', " +  
               " it_taxcode as 'TaxCode', it_status as 'Status', " +  
               " it_group as 'Group', it_drawing as 'Drawing' " +          
               " from item_mstr " +   
               " where it_item >= " + "'" + fromitem + "'" + 
               " and it_item <= " + "'" + toitem + "'" +
               " and it_code >= " + "'" + fromcode + "'" +
               " and it_code <= " + "'" + tocode + "'" +
               ";");
               
                    org.json.simple.JsonArray json = new org.json.simple.JsonArray();
                    ResultSetMetaData rsmd = res.getMetaData(); 
                    while (res.next()) {
                        if (! status.isEmpty() && ! res.getString("Status").equals(status)) {
                        continue;
                        }
                        int numColumns = rsmd.getColumnCount();
                        LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<String, Object>();
                          for (int i=1; i<=numColumns; i++) {
                            String column_name = rsmd.getColumnName(i);
                            jsonOrderedMap.put(column_name, res.getObject(column_name));
                          }
                          json.add(jsonOrderedMap);
                    }
                    x = json.toJson();
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return x;
        
         } 
  
    
    
}
