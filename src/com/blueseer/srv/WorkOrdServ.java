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
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.createMessage;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.util.LinkedHashMap;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author terryva
 */
public class WorkOrdServ extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        String id = request.getParameter("id");
        String fromdate = request.getParameter("fromdate");
        String todate = request.getParameter("todate");
        String fromitem = request.getParameter("fromitem");
        String toitem = request.getParameter("toitem");
        String fromcell = request.getParameter("fromcell");
        String tocell = request.getParameter("tocell");
        String status = request.getParameter("status");
        if (id != null && ! id.isEmpty()) {
            response.getWriter().println(getWorkOrderJSON(id));
        } else {
           response.getWriter().println(getWorkOrderListByDateJSON(fromdate,todate,fromitem,toitem,fromcell,tocell,status)); 
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
                response.getWriter().println(postWorkOrderJSON(request));
            } catch (TransformerException ex) {
                response.getWriter().println(ex);
            }
        }
    }
    
    

    public static class  WorkOrderXML {
    
    public static Document createRoot(Document doc) {
        Element rootElement = doc.createElement("Document");
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
    
    public static Document createBody(Document doc, String nbr, String item, String site,
            String desc, String code, String wf, String drawing, String rev,
            String loc, String wh, String lotsize, String comments, String uom,
            String qtyreq, String qtysched, String cell, String type,
            String rmks, String status, String order, String line,
            String datesched, String datedue ) {
        
        Element header = doc.createElement("WorkOrder");
        doc.getDocumentElement().appendChild(header);
       
        Element e = doc.createElement("Action");
                        e.appendChild(doc.createTextNode("query"));
        header.appendChild(e);
        
        e = doc.createElement("WorkOrderNumber");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(nbr)));
        header.appendChild(e);
       
        e = doc.createElement("Item");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(item)));
        header.appendChild(e);
       
        e = doc.createElement("Site");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(site)));
        header.appendChild(e);
        
        e = doc.createElement("Operation");
                        e.appendChild(doc.createTextNode("")); // multi operations
        header.appendChild(e);
       
        e = doc.createElement("ItemDescription");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(desc)));
        header.appendChild(e);
       
        e = doc.createElement("ItemCode");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(code)));
        header.appendChild(e);
        
        e = doc.createElement("Routing");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(wf)));
        header.appendChild(e);
        
        e = doc.createElement("Drawing");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(drawing)));
        header.appendChild(e);
        
        e = doc.createElement("Revision");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(rev)));
        header.appendChild(e);
        
        e = doc.createElement("Location");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(loc)));
        header.appendChild(e);
        
        e = doc.createElement("Warehouse");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(wh)));
        header.appendChild(e);
        
        e = doc.createElement("LotSize");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(lotsize)));
        header.appendChild(e);
        
        e = doc.createElement("ItemComments");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(comments)));
        header.appendChild(e);
        
        e = doc.createElement("UOM");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(uom)));
        header.appendChild(e);
        
        e = doc.createElement("QtyRequired");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(qtyreq)));
        header.appendChild(e);
        
        e = doc.createElement("QtyScheduled");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(qtysched)));
        header.appendChild(e);
        
        e = doc.createElement("WorkCell");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(cell)));
        header.appendChild(e);
        
        e = doc.createElement("PlanType");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(type)));
        header.appendChild(e);
        
        e = doc.createElement("PlanRemarks");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(rmks)));
        header.appendChild(e);
        
        e = doc.createElement("PlanStatus");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(status)));
        header.appendChild(e);
        
        e = doc.createElement("SalesOrderNumber");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(order)));
        header.appendChild(e);
        
        e = doc.createElement("SalesOrderLine");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(line)));
        header.appendChild(e);
        
        e = doc.createElement("DateScheduled");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(datesched)));
        header.appendChild(e);
        
        e = doc.createElement("OrderDueDate");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(datedue)));
        header.appendChild(e);
        
        e = doc.createElement("QtyComplete");
                        e.appendChild(doc.createTextNode(""));
        header.appendChild(e);
        
        e = doc.createElement("DateComplete");
                        e.appendChild(doc.createTextNode(""));
        header.appendChild(e);
        
        e = doc.createElement("LotNumber");
                        e.appendChild(doc.createTextNode(""));
        header.appendChild(e);
        
        e = doc.createElement("Operator");
                        e.appendChild(doc.createTextNode(""));
        header.appendChild(e);
        
        e = doc.createElement("PostComments");
                        e.appendChild(doc.createTextNode(""));
        header.appendChild(e);
       
        return doc;
    }
    
   
    
}
    
   
    public static String getWorkOrderXML(String id) {
       
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                res = st.executeQuery("select * from plan_mstr " +
                               " inner join item_mstr on " +
                               " it_item = plan_part " +
                               " where plan_nbr = " + "'" + id + "'" + ";");
                       
                 
                 StringWriter writer = new StringWriter();
                    int z = 0;
                    String status = "";
                    while (res.next()) {
                        z++;
                        
                        if (res.getString("plan_status").equals("1")) {
                            status = "closed";
                        } else if (res.getString("plan_status").equals("-1")) {
                            status = "void";
                        } else {
                            status = "open";
                        }
                            // Create Root Element
                        doc = WorkOrderXML.createRoot(doc);
                        
                        // Create Routing Tag Content
                     //   doc = WorkOrderXML.createRouting(doc, "", "");
                       
                        // Create header Tag content
                        doc = WorkOrderXML.createBody(doc, 
                                res.getString("plan_nbr"), 
                                res.getString("plan_part"),
                                res.getString("it_site"),
                                res.getString("it_desc"),
                                res.getString("it_code"),
                                res.getString("it_wf"),
                                res.getString("it_drawing"),
                                res.getString("it_rev"),
                                res.getString("it_loc"),
                                res.getString("it_wh"),
                                res.getString("it_lotsize"),
                                res.getString("it_comments"),
                                res.getString("it_uom"),
                                res.getString("plan_qty_req"),
                                res.getString("plan_qty_sched"),
                                res.getString("plan_cell"),
                                res.getString("plan_type"),
                                res.getString("plan_rmks"),
                                status,
                                res.getString("plan_order"),
                                res.getString("plan_line"),
                                res.getString("plan_date_sched"),
                                res.getString("plan_date_due")
                                );
                        
                        }
                       
                       // now transform to String
                       if (z > 0) {
                       x = BlueSeerUtils.transformDocToString(doc);
                       }
                       
                    
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
        
    public static String getWorkOrderJSON(String id) {
       
        String x = ""; 
        
        try{
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                res = st.executeQuery("select plan_nbr as 'WorkOrderNumber', plan_site as 'Site', plan_part as 'Item', " +
                        " it_desc as 'Description', it_wf as 'Routing', it_drawing as 'Drawing', " +
                        " it_rev as 'Revision', it_loc as 'Location', it_wh as 'Warehouse', " +
                        " it_lotsize as 'LotSize', it_comments as 'ItemComments', it_uom as 'UOM', " +
                        " plan_qty_req as 'QtyRequired', plan_qty_sched as 'QtyScheduled', " +
                        " plan_cell as 'Cell', plan_type as 'PlanType', plan_rmks as 'PlanRemarks', " +
                        " plan_order as 'Order', plan_line as 'Line', plan_date_sched as 'DateScheduled', " +
                        " plan_date_due as 'DateDue', " +
                        " case when plan_status = '1' then 'closed' when plan_status = '0' then 'open' when plan_status = '-1' then 'void' end as 'Status' " +
                        " from plan_mstr " +
                               " inner join item_mstr on " +
                               " it_item = plan_part " +
                               " where plan_nbr = " + "'" + id + "'" + ";");
                       
               
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
     
    public static String getWorkOrderListByDateJSON(String fromdate, String todate, String fromitem, String toitem, String fromcell, String tocell, String status) {
       
        String x = ""; 
        if (fromitem == null || fromitem.isEmpty()) {
            fromitem = bsmf.MainFrame.lowchar;
        }
        if (toitem == null || toitem.isEmpty()) {
            toitem = bsmf.MainFrame.hichar;
        }
        if (fromcell == null || fromcell.isEmpty()) {
            fromcell = bsmf.MainFrame.lowchar;
        }
        if (tocell == null || tocell.isEmpty()) {
            tocell = bsmf.MainFrame.hichar;
        }
        if (fromdate == null) {
            fromdate = bsmf.MainFrame.lowdate;
        }
        if (todate == null) {
            todate = bsmf.MainFrame.hidate;
        }
        if (status == null) {
            status = "";
        }
       
        
        try{
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                res = st.executeQuery("select plan_nbr as 'WorkOrderNumber', plan_site as 'Site', plan_part as 'Item', " +
                        " it_desc as 'Description', it_wf as 'Routing', it_drawing as 'Drawing', " +
                        " it_rev as 'Revision', it_loc as 'Location', it_wh as 'Warehouse', " +
                        " it_lotsize as 'LotSize', it_comments as 'ItemComments', it_uom as 'UOM', " +
                        " plan_qty_req as 'QtyRequired', plan_qty_sched as 'QtyScheduled', " +
                        " plan_cell as 'Cell', plan_type as 'PlanType', plan_rmks as 'PlanRemarks', " +
                        " plan_order as 'Order', plan_line as 'Line', plan_date_create as 'DateCreate', " +
                        " plan_date_due as 'DateDue', " +
                        " case when plan_status = '1' then 'closed' when plan_status = '0' then 'open' when plan_status = '-1' then 'void' end as 'Status' " +
                        " from plan_mstr " +
                               " inner join item_mstr on " +
                               " it_item = plan_part " +
                               " where plan_date_create >= " + "'" + fromdate + "'" + 
                               " and plan_date_create <= " + "'" + todate + "'" + 
                               " and plan_part >= " + "'" + fromitem + "'" +
                               " and plan_part <= " + "'" + toitem + "'" + 
                               " and plan_cell >= " + "'" + fromcell + "'" +
                               " and plan_cell <= " + "'" + tocell + "'" +                
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
    
    
    public static String postWorkOrderXML(HttpServletRequest request) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String x = "";
        
         JTable mytable = new JTable();
            javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Part", "Type", "Operation", "Qty", "Date", "Location", "SerialNo", "Reference", "Site", "Userid", "ProdLine", "AssyCell", "Rmks", "PackCell", "PackDate", "AssyDate", "ExpireDate", "Program", "Warehouse"
            });
        
        // read xml and convert to DOM
        InputStream xml = request.getInputStream();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder(); 
        Document doc = db.parse(xml);
        String action = "";
        String workordernbr = "";
        String item = "";
        String site = "";
        String operation = "";
        String qtycomp = "";
        String datecomp = "";
        String lotnbr = "";
        String operator = "";
        String postcomments = "";
        String junktag = "";
        
        String remoteuser = request.getRemoteUser();
        String remoteaddr = request.getRemoteAddr();
       
        // parse the routing element and retrieve origin and destination
        NodeList orderElements = doc.getElementsByTagName("WorkOrder");
        for (int i = 0; i < orderElements.getLength(); i++) {
		Node nNode = orderElements.item(i);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;
                        NodeList n = eElement.getChildNodes();
                        for (int k = 0; k < n.getLength(); k++) {
                            if (n.item(k).getNodeType() == Node.ELEMENT_NODE) {
                          //  System.out.println("\n" + k + ": " + "Element :" + n.item(k).getNodeName());
                                switch(n.item(k).getNodeName()) {
                                     case "Action" :
                                         action = n.item(k).getTextContent();
                                         break;
                                     case "WorkOrderNumber" :
                                         workordernbr = n.item(k).getTextContent();
                                         break;
                                     case "Item" :
                                         item = n.item(k).getTextContent();
                                      //    System.out.println("\n" + k + ": " + "Element :" + n.item(k).getTextContent());
                                          break;
                                     case "Site" :
                                         site = n.item(k).getTextContent();
                                         break;
                                     case "Operation" :
                                         operation = n.item(k).getTextContent();
                                         break;
                                     case "QtyComplete" :
                                         qtycomp = n.item(k).getTextContent();
                                         break; 
                                     case "DateComplete" :
                                         datecomp = n.item(k).getTextContent();
                                         break; 
                                     case "LotNumber" :
                                         lotnbr = n.item(k).getTextContent();
                                         break; 
                                     case "Operator" :
                                         operator = n.item(k).getTextContent();
                                         break;   
                                     case "PostComments" :
                                         postcomments = n.item(k).getTextContent();
                                         break;       
                                     default :
                                         junktag = n.item(k).getNodeName();
                                }
                            }
                        }
                        
			// x = eElement.getElementsByTagName("Item").item(0).getTextContent();
		}
	}
        
        // check for bad elements
        if (item.isEmpty() || ! OVData.isValidItem(item)) {
           return createMessage("Item not in item master", "fail", "0"); 
        }
        
        if (! OVData.isValidOperation(item, operation)) {
           return createMessage("Not a valid operation for this item", "fail", "0"); 
        }
        
        if (OVData.getPlanStatus(workordernbr) != 0) {
           return createMessage("Work Order is closed", "fail", "0"); 
        }
        
        
        // create table of data
        String[] det = invData.getItemDetail(item); 
        mymodel.addRow(new Object[]{item,
                "ISS-WIP",
                operation,
                qtycomp,
                datecomp,
                det[8], // location
                workordernbr,  // serialno  ...using JOBID from tubtraveler
                lotnbr,  // reference -- tr_ref holds the scrap code
                site,
                operator,
                det[3],
                "",   //  tr_actcell
                postcomments.replace(",", ""),   // remarks 
                "", // pack station
                "", // pack date
                "", // assembly date
                det[10], // expiredate
                "WorkOrdServ", // program
                det[9] // warehouse
            });
            mytable.setModel(mymodel);
        // load tran table and create pland_mstr
         if (! OVData.loadTranHistByTable(mytable)) {
           return createMessage("loadTranHistByTable failed", "fail", "0");   
         } else {
             int key = OVData.CreatePlanDet(mytable);
             return createMessage("work order loaded successfully", "success", String.valueOf(key));
         }
       
    }
    
    public static String postWorkOrderJSON(HttpServletRequest request) throws IOException, TransformerException {
        String x = "";
        
         JTable mytable = new JTable();
            javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Part", "Type", "Operation", "Qty", "Date", "Location", "SerialNo", "Reference", "Site", "Userid", "ProdLine", "AssyCell", "Rmks", "PackCell", "PackDate", "AssyDate", "ExpireDate", "Program", "Warehouse"
            });
        String line = "";
        StringBuffer jb = new StringBuffer();
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
        jb.append(line);
        }
        
        JSONObject json = new JSONObject(jb.toString());
       
        String action = "";
        String workordernbr = "";
        String item = "";
        String site = "";
        String operation = "";
        String qtycomp = "";
        String datecomp = "";
        String lotnbr = "";
        String operator = "";
        String postcomments = "";
        String junktag = "";
        
        
        // now lets iterate over the JSON object
        for (String keyStr : json.keySet()) { 
        Object keyvalue = json.get(keyStr);
        
            switch(keyStr) {
                 case "Action" :
                     action = keyvalue.toString();
                     break;
                 case "WorkOrderNumber" :
                     workordernbr = keyvalue.toString();
                     break;
                 case "Item" :
                     item = keyvalue.toString();
                     break;
                 case "Site" :
                     site = keyvalue.toString();
                     break;
                 case "Operation" :
                     operation = keyvalue.toString();
                     break;
                 case "QtyComplete" :
                     qtycomp = keyvalue.toString();
                     break; 
                 case "DateComplete" :
                     datecomp = keyvalue.toString();
                     break; 
                 case "LotNumber" :
                     lotnbr = keyvalue.toString();
                     break; 
                 case "Operator" :
                     operator = keyvalue.toString();
                     break;   
                 case "PostComments" :
                     postcomments = keyvalue.toString();
                     break;       
                 default :
                     junktag = keyvalue.toString();
            }

       
        //for nested objects iteration if required
        //if (keyvalue instanceof JSONObject)
        //    printJsonObject((JSONObject)keyvalue);
    }
        
       
        // check for bad elements
        if (item.isEmpty() || ! OVData.isValidItem(item)) {
           return createMessage("Item not in item master", "fail", "0"); 
        }
        
        if (! OVData.isValidOperation(item, operation)) {
           return createMessage("Not a valid operation for this item", "fail", "0"); 
        }
        
        if (OVData.getPlanStatus(workordernbr) != 0) {
           return createMessage("Work Order is closed", "fail", "0"); 
        }
        
        
        // create table of data
        String[] det = invData.getItemDetail(item); 
        mymodel.addRow(new Object[]{item,
                "ISS-WIP",
                operation,
                qtycomp,
                datecomp,
                det[8], // location
                workordernbr,  // serialno  ...using JOBID from tubtraveler
                lotnbr,  // reference -- tr_ref holds the scrap code
                site,
                operator,
                det[3],
                "",   //  tr_actcell
                postcomments.replace(",", ""),   // remarks 
                "", // pack station
                "", // pack date
                "", // assembly date
                det[10], // expire date
                "WorkOrdServ", // program
                det[9] // warehouse
            });
            mytable.setModel(mymodel);
        // load tran table and create pland_mstr
         if (! OVData.loadTranHistByTable(mytable)) {
           return createMessageJSON("fail", "loadTranHistByTable failed", "");   
         } else {
             int key = OVData.CreatePlanDet(mytable);
             return createMessageJSON("success", "work order loaded successfully",String.valueOf(key));
         }
       
    }
    
    
   
}
