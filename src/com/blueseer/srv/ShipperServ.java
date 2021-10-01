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
public class ShipperServ extends HttpServlet {
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        String id = request.getParameter("id");
        String fromdate = request.getParameter("fromdate");
        String todate = request.getParameter("todate");
        String fromnbr = request.getParameter("fromnbr");
        String tonbr = request.getParameter("tonbr");
        String fromcust = request.getParameter("fromcust");
        String tocust = request.getParameter("tocust");
        String status = request.getParameter("status");
        if (id != null && ! id.isEmpty()) {
            response.getWriter().println(getShipperJSON(id));
        } else {
           response.getWriter().println(getShipperListByDateJSON(fromdate,todate,fromnbr,tonbr,fromcust,tocust,status)); 
        }
    }

 @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
       // BufferedReader reader = request.getReader();
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        if (request == null) {
            response.getWriter().println("no valid payload provided");
        } else {
            try {
                response.getWriter().println(postShipperJSON(request));
            } catch (TransformerException ex) {
                response.getWriter().println(ex);
            }
        }
    }
    

 
public static String getShipperJSON(String id) {
       
        String x = ""; 
        
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
               res = st.executeQuery("select sh_id as 'ShipperNumber', " +
               " sh_site as 'Site', sh_so as 'OrderNumber', sh_po as 'PONumber', sh_po_date as 'OrderDate',  " +
               " sh_shipdate as 'ShipDate', sh_rmks as 'Remarks', sh_type as 'Type', " +
               " sh_curr as 'Currency', sh_shipvia as 'ShipVia', sh_status as 'Status', " +
               " sh_cust as 'BillToCode', sh_ship as 'ShipToCode' " +
               " from ship_mstr " +
               " where sh_id = " + "'" + id + "'" + ";");
                
               
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
                    
                    res = st.executeQuery("select shd_part as 'ItemNumber', shd_desc as 'ItemDescription',  " +
               " shd_soline as 'Line', shd_so as 'Order', shd_po as 'PO', shd_qty as 'ShipQty', shd_uom as 'UOM', shd_custpart as 'CustItem',  " +
               " shd_sku as 'SkuItem', shd_sku as 'UpcItem', shd_qty as 'ShipQty', " +
               " shd_listprice as 'ListPrice', shd_netprice as 'NetPrice', shd_disc as 'Discount', shd_taxamt as 'TaxAmt', " +
               " shd_loc as 'LineLocation', shd_wh as 'LineWarehouse' " +   
               " from ship_det " +
               " where shd_id = " + "'" + id + "'" + ";");
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
                    jsonOrderedMapHdr.put("Items", jsondet);
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
     
public static String getShipperListByDateJSON(String fromdate, String todate, String fromnbr, String tonbr, String fromcust, String tocust, String status) {
       
        String x = ""; 
        if (fromnbr == null || fromnbr.isEmpty()) {
            fromnbr = bsmf.MainFrame.lownbr;
        }
        if (tonbr == null || tonbr.isEmpty()) {
            tonbr = bsmf.MainFrame.hinbr;
        }
        if (fromdate == null || fromdate.isEmpty()) {
            fromdate = bsmf.MainFrame.lowdate;
        }
        if (todate == null || todate.isEmpty()) {
            todate = bsmf.MainFrame.hidate;
        }
        if (fromcust == null || fromcust.isEmpty()) {
            fromcust = bsmf.MainFrame.lowchar;
        }
        if (tocust == null || tocust.isEmpty()) {
            tocust = bsmf.MainFrame.hichar;
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
                
               res = st.executeQuery("select sh_id as 'ShipperNumber', " +
               " sh_site as 'Site', sh_so as 'OrderNumber', sh_po as 'PONumber', sh_po_date as 'OrderDate',  " +
               " sh_shipdate as 'ShipDate', sh_rmks as 'Remarks', sh_type as 'Type', " +
               " sh_curr as 'Currency', sh_shipvia as 'ShipVia', sh_status as 'Status', " +
               " sh_cust as 'BillToCode', sh_ship as 'ShipToCode' " +
               " from ship_mstr " +   
               " where sh_id >= " + "'" + fromnbr + "'" + 
               " and sh_id <= " + "'" + tonbr + "'" +
               " and sh_cust >= " + "'" + fromcust + "'" +
               " and sh_cust <= " + "'" + tocust + "'" +
               " and sh_shipdate >= " + "'" + fromdate + "'" +   
               " and sh_shipdate <= " + "'" + todate + "'" +    
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
 
public static String postShipperJSON(HttpServletRequest request) throws IOException, TransformerException {
        String x = "";
         
        String line = "";
        StringBuffer jb = new StringBuffer();
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
        jb.append(line);
        }
            
        String[] m = OVData.CreateShipperByJSON(jb.toString());
        return createMessageJSON(m[0], m[1], m[2]); 
        
    }
   
    
    
}
