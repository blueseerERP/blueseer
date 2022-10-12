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
import static com.blueseer.utl.BlueSeerUtils.createMessage;
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
import javax.swing.JTable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author terryva
 */
public class SalesOrdServ extends HttpServlet {
    
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
            response.getWriter().println(getSalesOrderJSON(id));
        } else {
           response.getWriter().println(getSalesOrderListByDateJSON(fromdate,todate,fromnbr,tonbr,fromcust,tocust,status)); 
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
                response.getWriter().println(postSalesOrderJSON(request));
            } catch (TransformerException ex) {
                response.getWriter().println(ex);
            }
        }
    }
    

public static String getSalesOrderXML(String id) {
       
    String x = ""; 
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = null;
     try {
         docBuilder = docFactory.newDocumentBuilder();
     } catch (ParserConfigurationException ex) {
         bsmf.MainFrame.bslog(ex);
         bsmf.MainFrame.show(ex.getMessage());
     }
    Document doc = docBuilder.newDocument();

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
                
            res = st.executeQuery("select * from so_mstr " +
                           " inner join cm_mstr on " +
                           " cm_mstr.cm_code = so_cust " +
                           " inner join cms_det on " +
                           " cms_det.cms_shipto = so_ship " +
                           " where so_nbr = " + "'" + id + "'" + ";");
                       
                 
             StringWriter writer = new StringWriter();
                int z = 0;
                while (res.next()) {
                    z++;
                        // Create Root Element
                    doc = SalesOrderXML.createRoot(doc);

                    // Create Routing Tag Content
                    doc = SalesOrderXML.createRouting(doc, "tev", "tex");

                    // Create header Tag content
                    doc = SalesOrderXML.createHeader(doc, res.getString("so_po"), 
                            res.getString("so_ord_date"),
                            res.getString("so_due_date"),
                            res.getString("so_cust"),
                            res.getString("so_shipvia"));

                      // Create shipto Tag content
                    doc = SalesOrderXML.createShipto(doc, res.getString("cms_shipto"), 
                            res.getString("cms_name"),
                            res.getString("cms_line1"),
                            res.getString("cms_line2"),
                            res.getString("cms_line3"),
                            res.getString("cms_city"),
                            res.getString("cms_state"),
                            res.getString("cms_zip"),   
                            res.getString("cms_country"));

                    // Create billto Tag content
                    doc = SalesOrderXML.createBillto(doc, res.getString("cm_code"), 
                            res.getString("cm_name"),
                            res.getString("cm_line1"),
                            res.getString("cm_line2"),
                            res.getString("cm_line3"),
                            res.getString("cm_city"),
                            res.getString("cm_state"),
                            res.getString("cm_zip"),   
                            res.getString("cm_country"));

                    }
                   if (z > 0) {
                   // now get the detail
                   res = st.executeQuery("select * from sod_det " +
                           " left outer join item_mstr on it_item = sod_item " +
                           " where sod_nbr = " + "'" + id + "'" + ";");
                   int i = 0;
                   ArrayList line = new ArrayList();
                   ArrayList item = new ArrayList();
                   ArrayList desc = new ArrayList();
                   ArrayList qty = new ArrayList();
                   ArrayList netprice = new ArrayList();
                   ArrayList listprice = new ArrayList();
                   ArrayList custnbr = new ArrayList();
                   ArrayList skunbr = new ArrayList();



                   while (res.next()) {
                       item.add(res.getString("sod_item"));
                       line.add(res.getString("sod_line"));
                       desc.add(res.getString("it_desc"));
                       qty.add(res.getString("sod_ord_qty"));
                       netprice.add(res.getString("sod_netprice"));
                       listprice.add(res.getString("sod_listprice"));
                       custnbr.add(res.getString("sod_custitem"));
                       skunbr.add(res.getString("sod_custitem"));

                   }
                       doc = SalesOrderXML.createDetailItem(doc,
                               line,
                               item,
                               desc,
                               qty,
                               netprice,
                               listprice,
                               custnbr,
                               skunbr
                               );

                   }  
                        
                       
                       // now transform to String
                       if (z > 0)
                       x = BlueSeerUtils.transformDocToString(doc);
                      
                    
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
  
public static String getSalesOrderJSON(String id) {
       
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
                
               res = st.executeQuery("select so_nbr as 'OrderNumber', " +
               " so_site as 'Site', so_po as 'PONumber', so_ord_date as 'OrderDate',  " +
               " so_due_date as 'DueDate', so_rmks as 'Remarks', so_type as 'Type', " +
               " so_curr as 'Currency', so_shipvia as 'ShipVia', so_status as 'Status', " +
               " so_cust as 'BillToCode', cm_name as 'BillToName', " +
               " cm_line1 as 'BillToAddr1', cm_city as 'BillToCity', " +
               " cm_state as 'BillToState', cm_zip as 'BillToZip', " + 
               " so_ship as 'ShipToCode', cms_name as 'ShipToName', " +
               " cms_line1 as 'ShipToAddr1', cms_city as 'ShipToCity', " +
               " cms_state as 'ShipToState', cms_zip as 'ShipToZip' " +          
               " from so_mstr " +
               " inner join cm_mstr on " +
               " cm_mstr.cm_code = so_cust " +
               " inner join cms_det on " +
               " cms_det.cms_shipto = so_ship " +
               " where so_nbr = " + "'" + id + "'" + ";");
                
               
                    JSONArray json = new JSONArray();
                    JSONArray jsondet = new JSONArray();
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
                    
                    res = st.executeQuery("select sod_item as 'ItemNumber', " +
               " sod_line as 'Line', sod_uom as 'UOM', sod_custitem as 'CustItem',  " +
               " sod_ord_qty as 'OrderQty', sod_shipped_qty as 'ShippedQty', sod_status as 'LineStatus', " +
               " sod_listprice as 'ListPrice', sod_netprice as 'NetPrice', sod_disc as 'Discount', " +
               " sod_loc as 'LineLocation', sod_wh as 'LineWarehouse' " +   
               " from sod_det " +
               " where sod_nbr = " + "'" + id + "'" + ";");
                rsmd = res.getMetaData(); 
                while (res.next()) {
                    int numColumns = rsmd.getColumnCount();
                    LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<String, Object>();
                      for (int i=1; i<=numColumns; i++) {
                        String column_name = rsmd.getColumnName(i);
                        jsonOrderedMap.put(column_name, res.getObject(column_name));
                      }
                      jsondet.put(jsonOrderedMap);
                }
                    jsonOrderedMapHdr.put("Items", jsondet);
                    json.put(jsonOrderedMapHdr);
                    x = json.toString(1);
                    
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
     
public static String getSalesOrderListByDateJSON(String fromdate, String todate, String fromnbr, String tonbr, String fromcust, String tocust, String status) {
       
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
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
              res = st.executeQuery("select so_nbr as 'OrderNumber', " +
               " so_site as 'Site', so_po as 'PONumber', so_ord_date as 'OrderDate',  " +
               " so_due_date as 'DueDate', so_rmks as 'Remarks', so_type as 'Type', " +
               " so_curr as 'Currency', so_shipvia as 'ShipVia', so_status as 'Status', " +
               " so_cust as 'BillToCode', cm_name as 'BillToName', " +
               " cm_line1 as 'BillToAddr1', cm_city as 'BillToCity', " +
               " cm_state as 'BillToState', cm_zip as 'BillToZip', " + 
               " so_ship as 'ShipToCode', cms_name as 'ShipToName', " +
               " cms_line1 as 'ShipToAddr1', cms_city as 'ShipToCity', " +
               " cms_state as 'ShipToState', cms_zip as 'ShipToZip' " +          
               " from so_mstr " +
               " inner join cm_mstr on " +
               " cm_mstr.cm_code = so_cust " +
               " inner join cms_det on " +
               " cms_det.cms_shipto = so_ship " +
               " where so_nbr >= " + "'" + fromnbr + "'" + 
               " and so_nbr <= " + "'" + tonbr + "'" +
               " and so_cust >= " + "'" + fromcust + "'" +
               " and so_cust <= " + "'" + tocust + "'" +
               " and so_ord_date >= " + "'" + fromdate + "'" +   
               " and so_ord_date <= " + "'" + todate + "'" +        
               
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
 
public static String postSalesOrderJSON(HttpServletRequest request) throws IOException, TransformerException {
        String x = "";
         
        String line = "";
        StringBuffer jb = new StringBuffer();
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
        jb.append(line);
        }
            
        String[] m = OVData.CreateSalesOrderByJSON(jb.toString());
        return createMessageJSON(m[0], m[1], m[2]); 
        
    }
    
public static class  SalesOrderXML {
    
    public static Document createRoot(Document doc) {
        Element rootElement = doc.createElement("doc");
        doc.appendChild(rootElement);        
        return doc;
    }
    
    public static Document createRouting(Document doc, String ip, String dest) {
      
        
       Element routing = doc.createElement("routing");
       doc.getDocumentElement().appendChild(routing);
       
       Element origin = doc.createElement("origin");
                        origin.appendChild(doc.createTextNode(ip));
       routing.appendChild(origin);
       
       Element destination = doc.createElement("destination");
                        destination.appendChild(doc.createTextNode(dest));
       routing.appendChild(destination);
       
        return doc;
    }
    
    public static Document createHeader(Document doc, String po, String orddate, 
            String duedate, String custid, String shipmethod) {
        
       Element header = doc.createElement("header");
       doc.getDocumentElement().appendChild(header);
       
       
       Element potag = doc.createElement("purchaseordernumber");
                        potag.appendChild(doc.createTextNode(BlueSeerUtils.xNull(po)));
       header.appendChild(potag);
       
       Element orddatetag = doc.createElement("orderdate");
                        orddatetag.appendChild(doc.createTextNode(BlueSeerUtils.xNull(orddate)));
       header.appendChild(orddatetag);
       
       Element duedatetag = doc.createElement("duedate");
                        duedatetag.appendChild(doc.createTextNode(BlueSeerUtils.xNull(duedate)));
       header.appendChild(duedatetag);
       
       Element custidtag = doc.createElement("custnumber");
                        custidtag.appendChild(doc.createTextNode(BlueSeerUtils.xNull(custid)));
       header.appendChild(custidtag);
       
       Element shipmethodtag = doc.createElement("shipmentmethod");
                        shipmethodtag.appendChild(doc.createTextNode(BlueSeerUtils.xNull(shipmethod)));
       header.appendChild(shipmethodtag);
       
        return doc;
    }
    
    public static Document createShipto(Document doc, String code, String name, 
            String line1, String line2, String line3, String city, String state, 
            String zip, String country ) {
        
        Element addr = doc.createElement("address");
        addr.setAttribute("type", "shipto");
       doc.getDocumentElement().appendChild(addr);
       
       Element addrname = doc.createElement("addressname");
                        addrname.appendChild(doc.createTextNode(BlueSeerUtils.xNull(name)));
       addr.appendChild(addrname);
       
       Element addrline1 = doc.createElement("addressline1");
                        addrline1.appendChild(doc.createTextNode(BlueSeerUtils.xNull(line1)));
       addr.appendChild(addrline1);
       
       Element addrline2 = doc.createElement("addressline2");
                        addrline2.appendChild(doc.createTextNode(BlueSeerUtils.xNull(line2)));
       addr.appendChild(addrline2);
       
       Element addrline3 = doc.createElement("addressline3");
                        addrline3.appendChild(doc.createTextNode(BlueSeerUtils.xNull(line3)));
       addr.appendChild(addrline3);
       
       Element addrcity = doc.createElement("addresscity");
                        addrcity.appendChild(doc.createTextNode(BlueSeerUtils.xNull(city)));
       addr.appendChild(addrcity);
       
       Element addrstate = doc.createElement("addressstate");
                        addrstate.appendChild(doc.createTextNode(BlueSeerUtils.xNull(state)));
       addr.appendChild(addrstate);
       
       Element addrzip = doc.createElement("addresszip");
                        addrzip.appendChild(doc.createTextNode(BlueSeerUtils.xNull(zip)));
       addr.appendChild(addrzip);
       
       Element addrcountry = doc.createElement("addresscountry");
                        addrcountry.appendChild(doc.createTextNode(BlueSeerUtils.xNull(country)));
       addr.appendChild(addrcountry);
       
        return doc;
    }
    
     public static Document createBillto(Document doc, String code, String name, 
            String line1, String line2, String line3, String city, String state, 
            String zip, String country ) {
        
        Element addr = doc.createElement("address");
        addr.setAttribute("type", "billto");
       doc.getDocumentElement().appendChild(addr);
       
       Element addrname = doc.createElement("addressname");
                        addrname.appendChild(doc.createTextNode(BlueSeerUtils.xNull(name)));
       addr.appendChild(addrname);
       
       Element addrline1 = doc.createElement("addressline1");
                        addrline1.appendChild(doc.createTextNode(BlueSeerUtils.xNull(line1)));
       addr.appendChild(addrline1);
       
       Element addrline2 = doc.createElement("addressline2");
                        addrline2.appendChild(doc.createTextNode(BlueSeerUtils.xNull(line2)));
       addr.appendChild(addrline2);
       
       Element addrline3 = doc.createElement("addressline3");
                        addrline3.appendChild(doc.createTextNode(BlueSeerUtils.xNull(line3)));
       addr.appendChild(addrline3);
       
       Element addrcity = doc.createElement("addresscity");
                        addrcity.appendChild(doc.createTextNode(BlueSeerUtils.xNull(city)));
       addr.appendChild(addrcity);
       
       Element addrstate = doc.createElement("addressstate");
                        addrstate.appendChild(doc.createTextNode(BlueSeerUtils.xNull(state)));
       addr.appendChild(addrstate);
       
       Element addrzip = doc.createElement("addresszip");
                        addrzip.appendChild(doc.createTextNode(BlueSeerUtils.xNull(zip)));
       addr.appendChild(addrzip);
       
       Element addrcountry = doc.createElement("addresscountry");
                        addrcountry.appendChild(doc.createTextNode(BlueSeerUtils.xNull(country)));
       addr.appendChild(addrcountry);
       
        return doc;
    }
    
     public static Document createDetailItem(Document doc, ArrayList line, ArrayList item, 
            ArrayList desc, ArrayList qty, ArrayList netprice, ArrayList listprice, ArrayList custnbr, 
            ArrayList skunbr) {
        
        Element det = doc.createElement("detail");
       doc.getDocumentElement().appendChild(det);
       
       for (int i = 0; i < item.size(); i++) {
           Element itmtag = doc.createElement("item");
                  
               Element eline = doc.createElement("line");
                                eline.appendChild(doc.createTextNode(BlueSeerUtils.xNull(line.get(i).toString())));
               itmtag.appendChild(eline);
               
               Element eitem = doc.createElement("itemnumber");
                                eitem.appendChild(doc.createTextNode(BlueSeerUtils.xNull(item.get(i).toString())));
               itmtag.appendChild(eitem);
               
               Element edsc = doc.createElement("description");
                                edsc.appendChild(doc.createTextNode(BlueSeerUtils.xNull(desc.get(i).toString())));
               itmtag.appendChild(edsc);
               
               Element eqty = doc.createElement("quantity");
                                eqty.appendChild(doc.createTextNode(BlueSeerUtils.xNull(qty.get(i).toString())));
               itmtag.appendChild(eqty);
               
               Element elistprice = doc.createElement("listprice");
                                elistprice.appendChild(doc.createTextNode(BlueSeerUtils.xNull(listprice.get(i).toString())));
               itmtag.appendChild(elistprice);
               
               Element enetprice = doc.createElement("netprice");
                                enetprice.appendChild(doc.createTextNode(BlueSeerUtils.xNull(netprice.get(i).toString())));
               itmtag.appendChild(enetprice);
               
               Element ecustnbr = doc.createElement("custnumber");
                                ecustnbr.appendChild(doc.createTextNode(BlueSeerUtils.xNull(custnbr.get(i).toString())));
               itmtag.appendChild(ecustnbr);
               
               Element eskunbr = doc.createElement("skunumber");
                                eskunbr.appendChild(doc.createTextNode(BlueSeerUtils.xNull(skunbr.get(i).toString())));
               itmtag.appendChild(eskunbr);
               
            det.appendChild(itmtag);
       }
       
      
       
        return doc;
    }
     
    
}
    
}
