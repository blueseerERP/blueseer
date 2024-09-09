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
import static bsmf.MainFrame.ConvertStringToBool;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.edi.EDI.deleteFile;
import static com.blueseer.edi.EDI.fileExists;
import static com.blueseer.edi.EDI.getFileContent;
import static com.blueseer.edi.EDI.getFileContentBytes;
import static com.blueseer.edi.EDI.getFilesOfDir;
import static com.blueseer.edi.EDI.runEDI;
import static com.blueseer.edi.EDI.runEDIsingle;
import static com.blueseer.edi.EDI.writeFile;
import static com.blueseer.edi.apiUtils.createKeyStore;
import static com.blueseer.edi.apiUtils.createNewKeyPair;
import static com.blueseer.edi.apiUtils.genereatePGPKeyPair;
import static com.blueseer.edi.apiUtils.getAsciiDumpPGPKey;
import static com.blueseer.edi.apiUtils.getPublicKeyAsOPENSSH;
import static com.blueseer.edi.apiUtils.getPublicKeyAsPEM;
import static com.blueseer.edi.apiUtils.postAS2;
import static com.blueseer.edi.apiUtils.runAPIPost;
import static com.blueseer.fgl.fglData.getAccountBalanceReport;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
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
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.IOUtils;


/**
 *
 * @author terryva
 */
public class dataServ extends HttpServlet {
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

    // doGet unused...moved to business function dataServXXX ...left as template for future use
    
    response.setContentType("text/plain");
        
    if (! confirmServerAuth(request)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println("br549 authorization failed");
        return;
    }
    
        
    if (request.getParameter("id") == null || request.getParameter("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id");  
      return;
    }
        
        String id = request.getParameter("id");
        
        response.setStatus(HttpServletResponse.SC_OK);
        
        
        // doGet unused...moved to business function dataServXXX ...left as template for future use
        if (id.equals("1")) {
           String[] keys = new String[]{
               request.getParameter("year"), 
               request.getParameter("period"), 
               request.getParameter("site"), 
               request.getParameter("iscc"), 
               request.getParameter("intype"), 
               request.getParameter("fromacct"), 
               request.getParameter("toacct")  
           }; 
           
           for (String k : keys) {
               if (k == null) {
                   response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                   response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing param");  
                   return;
               }
           }
           
           String r = getAccountBalanceReport(keys);
           
           if (r == null || r.isBlank()) {
             response.getWriter().println("no return for: " + String.join(",",keys));   
           } else {
             response.getWriter().println(r);   
           }
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
                /*
                if (! confirmServerAuth(request)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().println("br549 authorization failed");
                    return;
                }
                */
            
            
                if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
                  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
                  return;
                }

                String id = request.getHeader("id");
                
                String line = "";
                StringBuilder sb = new StringBuilder();
                InputStream body = null;
                if (id.equals("uploadFile")) {
                   body = request.getInputStream(); // as byte array
                } else {
                    BufferedReader reader = request.getReader();  // as string
                    while ((line = reader.readLine()) != null) {  
                    sb.append(line);
                    }
                }
              
                
                // process specific app (id)
                if (id.equals("runEDI")) { // run EDI engine with list of files
                  String[] files = sb.toString().split(",",-1);
                  response.getWriter().println(runEDI(null, files, ""));
                } else if (id.equals("runEDIsingle")) { 
                  response.getWriter().println(runEDIsingle(null,sb.toString(),"")); 
                } else if (id.equals("getFilesOfDir")) { 
                  String dir = request.getHeader("dir");
                  response.getWriter().println(getFilesOfDir(dir)); 
                } else if (id.equals("getFileContent")) { 
                  String filepath = request.getHeader("filepath");
                  response.setContentType("application/octet-stream");
                  response.setHeader("Content-Transfer-Encoding", "binary");
                  response.getOutputStream().write(getFileContentBytes(filepath));
                 // response.getWriter().println(getFileContent(filepath)); 
                } else if (id.equals("getAsciiDumpPGPKey")) { 
                  String pksid = request.getHeader("pksid");
                  String pkstype = request.getHeader("pkstype");
                    try {  
                        response.getWriter().println(getAsciiDumpPGPKey(pksid, pkstype));
                    } catch (Exception ex) {
                        response.getWriter().println("Exception (getAsciiDumpPGPKey): " + ex.getMessage());
                    }
                } else if (id.equals("getPublicKeyAsPEM")) { 
                  String key = request.getHeader("key");
                    try {  
                        response.getWriter().println(getPublicKeyAsPEM(key));
                    } catch (Exception ex) {
                        response.getWriter().println("Exception (getPublicKeyAsPEM): " + ex.getMessage());
                    }   
                } else if (id.equals("getPublicKeyAsOPENSSH")) { 
                  String key = request.getHeader("key");
                    try {  
                        response.getWriter().println(getPublicKeyAsOPENSSH(key));
                    } catch (Exception ex) {
                        response.getWriter().println("Exception (getPublicKeyAsOPENSSH): " + ex.getMessage());
                    }      
                } else if (id.equals("createKeyStore")) { 
                  String storepass = request.getHeader("storepass");
                  String storefile = request.getHeader("storefile");
                  response.getWriter().println(createKeyStore(storepass,storefile));
                } else if (id.equals("createNewKeyPair")) { 
                  String standard = request.getHeader("standard");
                  String user = request.getHeader("user");
                  String pass = request.getHeader("pass");
                  String storepass = request.getHeader("storepass");
                  String storefilename = request.getHeader("storefilename");
                  String encal = request.getHeader("encal");
                  String signal = request.getHeader("signal");
                  String strength = request.getHeader("strength");
                  String years = request.getHeader("years");
                  response.getWriter().println(createNewKeyPair(standard,user,pass,storepass,storefilename,encal,signal,strength,years));   
                } else if (id.equals("genereatePGPKeyPair")) { 
                  String user = request.getHeader("user");
                  String pass = request.getHeader("pass");
                  String parent = request.getHeader("parent");
                    try {
                        response.getWriter().println(genereatePGPKeyPair(user,pass,parent));
                    } catch (Exception ex) {
                        response.getWriter().println("Exception (genereatePGPKeyPair): " + ex.getMessage());
                    }
                } else if (id.equals("postAS2")) { 
                  String key = request.getHeader("key");
                  String debug = request.getHeader("debug");
                    try {  
                        response.getWriter().println(postAS2(key, Boolean.valueOf(debug)));
                    } catch (Exception ex) {
                        response.getWriter().println("Exception (postAS2): " + ex.getMessage());
                    } 
                } else if (id.equals("postAPI")) { 
                  String key = request.getHeader("key");
                  String method = request.getHeader("method");
                  String urlstring = request.getHeader("urlstring");
                  String requestheader = request.getHeader("requestheader");
                  String responseheader = request.getHeader("responseheader");
                    try {  
                        response.getWriter().println(runAPIPost(key, method, urlstring, requestheader, responseheader));
                    } catch (Exception ex) {
                        response.getWriter().println("Exception (postAS2): " + ex.getMessage());
                    } 
                } else if (id.equals("uploadFile")) { 
                  String filepath = request.getHeader("filepath");
                  response.getWriter().println(writeFile(filepath, IOUtils.toByteArray(body)));  
                } else if (id.equals("deleteFile")) { 
                  String filepath = request.getHeader("filepath");
                  response.getWriter().println(deleteFile(filepath));   
                } else if (id.equals("uploadFileExists")) { 
                  String filepath = request.getHeader("filepath");
                  response.getWriter().println(fileExists(filepath));   
                } else {
                  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": unknown ID " + "\n" + getHeaders(request));  
                  return;  
                }
                
        }
    }
     
    
    private String getHeaders(HttpServletRequest request) {
    
    StringBuilder requestHeaders = new StringBuilder();

            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String hd = headerNames.nextElement();
                requestHeaders.append("Header  " + hd).append("  Value  " + request.getHeader(hd)).append("\n");
            }
    return requestHeaders.toString();
}


}
