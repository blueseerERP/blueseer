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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.edi.APIMaint;
import com.blueseer.edi.apiUtils;
import com.blueseer.inv.invData;
import com.blueseer.sch.schData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.createMessage;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
import org.bouncycastle.cms.CMSException;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author terryva
 */
public class AS2Serv extends HttpServlet {
    
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
            response.getWriter().println("N/A at this time TEV");
        } else {
           response.getWriter().println("N/A at this time TEV"); 
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
            response.getWriter().println(requestToFile(request));
        }
    }
    
   
    public static String requestToFile(HttpServletRequest request) throws FileNotFoundException, IOException {
        String x = "";
        Path path = FileSystems.getDefault().getPath("temp" + "/" + "somefile");
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
        try {
            String line = "";
            StringBuilder jb = new StringBuilder();
            BufferedReader reader = request.getReader();
            
            while ((line = reader.readLine()) != null) {
            jb.append(line);
            }
            reader.close();
        
         
         byte[] data = APIMaint.decryptData(String.valueOf(jb).getBytes(), apiUtils.getPrivateKey("terry") );
         String datastring = new String(data);   
         output.write(datastring);
          
        } catch (FileNotFoundException ex) {
            bslog(ex);
        } catch (IOException ex) {
            bslog(ex);
        } catch (CMSException ex) {
            bslog(ex);
        } finally {
           output.close();  
        }
        
        return x;
    }
   
}
