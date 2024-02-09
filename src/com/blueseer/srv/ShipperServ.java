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
import com.blueseer.shp.shpData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import com.blueseer.utl.OVData;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
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
                    
                    res = st.executeQuery("select shd_item as 'ItemNumber', shd_desc as 'ItemDescription',  " +
               " shd_soline as 'Line', shd_so as 'Order', shd_po as 'PO', shd_qty as 'ShipQty', shd_uom as 'UOM', shd_custitem as 'CustItem',  " +
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

public static String getInvoiceJSON(String id) {
       
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
                
               res = st.executeQuery("select sh_id as 'InvoiceNumber', " +
               " sh_site as 'Site', sh_so as 'OrderNumber', sh_po as 'PONumber', sh_po_date as 'OrderDate',  " +
               " sh_shipdate as 'ShipDate', sh_confdate as 'InvoiceDate', sh_rmks as 'Remarks', sh_type as 'Type', " +
               " sh_curr as 'Currency', sh_shipvia as 'ShipVia', sh_status as 'Status' " +
               " from ship_mstr " +
               " where sh_id = " + "'" + id + "'" + ";");
                
               
                    org.json.simple.JsonArray json = new org.json.simple.JsonArray();
                    org.json.simple.JsonArray jsonaddr = new org.json.simple.JsonArray();
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
                    
                res = st.executeQuery("select cm_code as 'BillToCode', cm_name as 'BillToName', cm_line1 as 'BillToAddr1',  " +
                " cm_city as 'BillToCity', cm_state as 'BillToState', cm_zip as 'BillToZip', cm_country as 'BillToCountry', cm_misc1 as 'TaxID' " +
                " from cm_mstr inner join ship_mstr on sh_cust = cm_code " +
                " where sh_id = " + "'" + id + "'" + ";");
                rsmd = res.getMetaData(); 
                while (res.next()) {
                    int numColumns = rsmd.getColumnCount();
                    LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<String, Object>();
                      for (int i=1; i<=numColumns; i++) {
                        String column_name = rsmd.getColumnName(i);
                        jsonOrderedMap.put(column_name, res.getObject(column_name));
                      }
                      jsonaddr.add(jsonOrderedMap);
                }  
                
                res = st.executeQuery("select cms_shipto as 'ShipToCode', cms_name as 'ShipToName', cms_line1 as 'ShipToAddr1',  " +
                " cms_city as 'ShipToCity', cms_state as 'ShipToState', cms_zip as 'ShipToZip', cms_country as 'ShipToCountry' " +
                " from cms_det inner join ship_mstr on sh_cust = cms_code and sh_ship = cms_shipto " +
                " where sh_id = " + "'" + id + "'" + ";");
                rsmd = res.getMetaData(); 
                while (res.next()) {
                    int numColumns = rsmd.getColumnCount();
                    LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<String, Object>();
                      for (int i=1; i<=numColumns; i++) {
                        String column_name = rsmd.getColumnName(i);
                        jsonOrderedMap.put(column_name, res.getObject(column_name));
                      }
                      jsonaddr.add(jsonOrderedMap);
                }
                
                jsonOrderedMapHdr.put("Addresses", jsonaddr);
                    
                    res = st.executeQuery("select shd_item as 'ItemNumber', shd_desc as 'ItemDescription',  " +
               " shd_soline as 'Line', shd_so as 'Order', shd_po as 'PO', shd_qty as 'ShipQty', shd_uom as 'UOM', shd_custitem as 'CustItem',  " +
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
                    
                    ObjectMapper mapper = new ObjectMapper();
                    Object jsonx = mapper.readValue(x, Object.class);
                    x = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonx); // prettify
                    
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

public static String getInvoiceXML(String id) {
       
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
                
            res = st.executeQuery("select * from ship_mstr " +
                           " inner join so_mstr on " +
                           " so_mstr.so_nbr = sh_so " +
                           " inner join cm_mstr on " +
                           " cm_mstr.cm_code = sh_cust " +
                           " inner join cms_det on " +
                           " cms_det.cms_shipto = sh_ship and cms_det.cms_code = sh_cust " +
                           " where sh_id = " + "'" + id + "'" + ";");
                       
                 
             StringWriter writer = new StringWriter();
                int z = 0;
                while (res.next()) {
                    z++;
                        // Create Root Element
                    doc = InvoiceXML.createRoot(doc);

                    // Create Routing Tag Content
                    doc = InvoiceXML.createRouting(doc, "tev", "tex");

                    // Create header Tag content
                    doc = InvoiceXML.createHeader(doc, res.getString("sh_po"), 
                            res.getString("so_ord_date"),
                            res.getString("so_due_date"),
                            res.getString("sh_cust"),
                            res.getString("sh_shipvia"));

                      // Create shipto Tag content
                    doc = InvoiceXML.createShipto(doc, res.getString("cms_shipto"), 
                            res.getString("cms_name"),
                            res.getString("cms_line1"),
                            res.getString("cms_line2"),
                            res.getString("cms_line3"),
                            res.getString("cms_city"),
                            res.getString("cms_state"),
                            res.getString("cms_zip"),   
                            res.getString("cms_country"));

                    // Create billto Tag content
                    doc = InvoiceXML.createBillto(doc, res.getString("cm_code"), 
                            res.getString("cm_name"),
                            res.getString("cm_line1"),
                            res.getString("cm_line2"),
                            res.getString("cm_line3"),
                            res.getString("cm_city"),
                            res.getString("cm_state"),
                            res.getString("cm_zip"),   
                            res.getString("cm_country"),
                            res.getString("cm_misc1"));

                    }
                   if (z > 0) {
                   // now get the detail
                   res = st.executeQuery("select * from ship_det " +
                           " left outer join item_mstr on it_item = shd_item " +
                           " where shd_id = " + "'" + id + "'" + ";");
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
                       item.add(res.getString("shd_item"));
                       line.add(res.getString("shd_line"));
                       desc.add(res.getString("shd_desc"));
                       qty.add(res.getString("shd_qty"));
                       netprice.add(res.getString("shd_netprice"));
                       listprice.add(res.getString("shd_listprice"));
                       custnbr.add(res.getString("shd_custitem"));
                       skunbr.add(res.getString("shd_sku"));

                   }
                       doc = InvoiceXML.createDetailItem(doc,
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
 
public static class  InvoiceXML {
    
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
            String zip, String country, String taxid ) {
        
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
       
       Element addrtaxid = doc.createElement("addresstaxid");
                        addrtaxid.appendChild(doc.createTextNode(BlueSeerUtils.xNull(taxid)));
       addr.appendChild(addrtaxid);
       
        return doc;
    }
    
     public static Document createDetailItem(Document doc, ArrayList line, ArrayList item, 
            ArrayList desc, ArrayList qty, ArrayList netprice, ArrayList listprice, ArrayList custnbr, 
            ArrayList skunbr) {
        
        Element det = doc.createElement("items");
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
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
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
            
        String[] m = shpData.CreateShipperByJSON(jb.toString());
        return createMessageJSON(m[0], m[1], m[2]); 
        
    }
   
    
    
}
