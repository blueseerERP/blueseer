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
import static com.blueseer.edi.EDI.exeEngine;
import static com.blueseer.fgl.fglData.getAccountBalanceReport;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
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
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;


/**
 *
 * @author terryva
 */
public class dataServ extends HttpServlet {
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
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
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                sb.append(line);
                }
                if (sb.toString().isBlank()) {
                  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + "empty payload" + "\n" + getHeaders(request));  
                  return; 
                }
                
                
                // process specific app (id)
                if (id.equals("2")) { // run EDI engine with list of files
                  String[] files = sb.toString().split(",",-1);
                  response.getWriter().println(exeEngine(null, files));  
                } else {
                  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": unknown id" + "\n" + getHeaders(request));  
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
