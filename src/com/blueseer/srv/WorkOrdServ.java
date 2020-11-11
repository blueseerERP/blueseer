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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
import org.xml.sax.SAXException;

/**
 *
 * @author terryva
 */
public class WorkOrdServ extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.getWriter().println("id is null or blank");
        } else {
           response.getWriter().println(getWorkOrderXML(id)); 
        }
    }
     
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        if (reader == null) {
            response.getWriter().println("no xml stream provided");
        } else {
            try { 
                response.getWriter().println(postWorkOrderXML(request));
            } catch (ParserConfigurationException ex) {
                bsmf.MainFrame.bslog(ex);
            } catch (SAXException ex) {
                bsmf.MainFrame.bslog(ex);
            }
        }
    }
    
    

    public static class  WorkOrderXML {
    
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
    
    public static Document createHeader(Document doc, String nbr, String item, String site,
            String desc, String code, String wf, String drawing, String rev,
            String loc, String wh, String lotsize, String comments, String uom,
            String qtyreq, String qtysched, String cell, String type,
            String rmks, String status, String order, String line,
            String datesched, String datedue ) {
        
        Element header = doc.createElement("WorkOrder");
        doc.getDocumentElement().appendChild(header);
       
       
        Element e = doc.createElement("WorkOrderNumber");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(nbr)));
        header.appendChild(e);
       
        e = doc.createElement("Item");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(item)));
        header.appendChild(e);
       
        e = doc.createElement("Site");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(site)));
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
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                res = st.executeQuery("select * from plan_mstr " +
                               " inner join item_mstr on " +
                               " it_item = plan_part " +
                               " where plan_nbr = " + "'" + id + "'" + ";");
                       
                 
                 StringWriter writer = new StringWriter();
                    int z = 0;
                    while (res.next()) {
                        z++;
                            // Create Root Element
                        doc = WorkOrderXML.createRoot(doc);
                        
                        // Create Routing Tag Content
                        doc = WorkOrderXML.createRouting(doc, "", "");
                       
                        // Create header Tag content
                        doc = WorkOrderXML.createHeader(doc, 
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
                                res.getString("plan_status"),
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
        
    public static String postWorkOrderXML(HttpServletRequest request) throws IOException, ParserConfigurationException, SAXException {
        String x = "";
        // read xml and convert to DOM
        InputStream xml = request.getInputStream();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder(); 
        Document doc = db.parse(xml);
        String origin = "";
        String destination = "";
        String remoteuser = request.getRemoteUser();
        String remoteaddr = request.getRemoteAddr();
       
        // parse the routing element and retrieve origin and destination
        NodeList routing = doc.getElementsByTagName("routing");
        for (int i = 0; i < routing.getLength(); i++) {

		Node nNode = routing.item(i);

		System.out.println("\nCurrent Element :" + nNode.getNodeName());

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;

			//System.out.println("Staff id : " + eElement.getAttribute("id"));
			origin = "Origin : " + eElement.getElementsByTagName("origin").item(0).getTextContent();
			destination = "Destination : " + eElement.getElementsByTagName("destination").item(0).getTextContent();
			

		}
	}

        return x;
    }
    
}
