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
package com.blueseer.srv;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
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
public class CustomerServ extends HttpServlet {
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        String id = request.getParameter("id");
        String fromcust = request.getParameter("fromcust");
        String tocust = request.getParameter("tocust");
        String fromname = request.getParameter("fromname");
        String toname = request.getParameter("toname");
        String fromzip = request.getParameter("fromzip");
        String tozip = request.getParameter("tozip");
        
        String status = request.getParameter("status");
        if (id != null && ! id.isEmpty()) {
            response.getWriter().println(getCustomerJSON(id));
        } else {
           response.getWriter().println(getCustomerListByVarJSON(fromcust,tocust,fromname,toname,fromzip,tozip)); 
        }
    }

 
public static String getCustomerJSON(String id) {
       
        String x = ""; 
        
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
                
               res = st.executeQuery("select cm_code as 'CustNumber', " +
               " cm_site as 'Site', cm_name as 'CustName', cm_line1 as 'CustLine1', cm_line2 as 'CustLine2',  " +
               " cm_line3 as 'CustLine3', cm_city as 'CustCity', cm_state as 'CustState', " +
               " cm_zip as 'CustZip', cm_country as 'CustCountry', cm_email as 'CustEmail', cm_phone as 'CustPhone', " +
               " cm_terms as 'CustTerms', cm_bank as 'CustBank', cm_curr as 'CustCurrency', " +
               " cm_group as 'CustGroup', cm_market as 'CustMarket', cm_carrier as 'CustCarrier' " +
                       " from cm_mstr " +  
               " where cm_code = " + "'" + id + "'" +
               ";");
                
               
                    org.json.simple.JsonArray json = new org.json.simple.JsonArray();
                    org.json.simple.JsonArray jsondet = new org.json.simple.JsonArray();
                    ResultSetMetaData rsmd = res.getMetaData(); 
                    LinkedHashMap<String, Object> jsonOrderedMapHdr = new LinkedHashMap<String, Object>();
                        
                    while (res.next()) {
                        int numColumns = rsmd.getColumnCount();
                       // LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<String, Object>();
                          for (int i=1; i<=numColumns; i++) {
                            String column_name = rsmd.getColumnName(i);
                            jsonOrderedMapHdr.put(column_name, res.getObject(column_name));
                          }
                         // json.add(jsonOrderedMap);
                    }
                    
                    res = st.executeQuery("select cms_code as 'ParentCode', cms_shipto as 'ShipToCode',  " +
               " cms_name as 'ShipName', cms_line1 as 'ShipLine1', cms_line2 as 'ShipLine2', cms_line3 as 'ShipLine3',  " +
               " cms_city as 'ShipCity', cms_state as 'ShipState', cms_zip as 'ShipZip', cms_country as 'ShipCountry', " +
               " cms_contact as 'ShipContact', cms_phone as 'ShipPhone', cms_email as 'ShipEmail' " +
               " from cms_det " +
               " where cms_code = " + "'" + id + "'" + ";");
                rsmd = res.getMetaData(); 
                while (res.next()) {
                    int numColumns = rsmd.getColumnCount();
                    LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<String, Object>();
                      for (int i=1; i<=numColumns; i++) {
                        String column_name = rsmd.getColumnName(i);
                        jsonOrderedMap.put(column_name, res.getObject(column_name));
                      }
                      jsondet.add(jsonOrderedMap);
                }
                    jsonOrderedMapHdr.put("ShipTos", jsondet);
                    json.add(jsonOrderedMapHdr);
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
     
public static String getCustomerListByVarJSON(String fromcust, String tocust, String fromname, String toname, String fromzip, String tozip) {
       
        String x = ""; 
       
        if (fromcust == null || fromcust.isEmpty()) {
            fromcust = bsmf.MainFrame.lowchar;
        }
        if (tocust == null || tocust.isEmpty()) {
            tocust = bsmf.MainFrame.hichar;
        }
        if (fromname == null || fromname.isEmpty()) {
            fromname = bsmf.MainFrame.lowchar;
        }
        if (toname == null || toname.isEmpty()) {
            toname = bsmf.MainFrame.hichar;
        }
        if (fromzip == null || fromzip.isEmpty()) {
            fromzip = bsmf.MainFrame.lowchar;
        }
        if (tozip == null || tozip.isEmpty()) {
            tozip = bsmf.MainFrame.hichar;
        }
       
       // System.out.println("HERE: " + fromnbr + "/" + tonbr + "/" + fromdate + "/" + todate + "/" + fromcust + "/" + tocust);
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
                
               res = st.executeQuery("select cm_code as 'CustNumber', " +
               " cm_site as 'Site', cm_name as 'CustName', cm_line1 as 'CustLine1', cm_line2 as 'CustLine2',  " +
               " cm_line3 as 'CustLine3', cm_city as 'CustCity', cm_state as 'CustState', " +
               " cm_zip as 'CustZip', cm_country as 'CustCountry', cm_email as 'CustEmail', cm_phone as 'CustPhone', " +
               " cm_terms as 'CustTerms', cm_bank as 'CustBank', cm_curr as 'CustCurrency', " +
               " cm_group as 'CustGroup', cm_market as 'CustMarket', cm_carrier as 'CustCarrier' " +
                       " from cm_mstr " +  
               " where cm_code >= " + "'" + fromcust + "'" +
               " and cm_code <= " + "'" + tocust + "'" +
               " and cm_name >= " + "'" + fromname + "'" +
               " and cm_name <= " + "'" + toname + "'" +
               " and cm_zip >= " + "'" + fromzip + "'" +
               " and cm_zip <= " + "'" + tozip + "'" +        
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
   
    
}
