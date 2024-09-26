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
import static com.blueseer.fgl.fglData.getAccountActivityYear;
import static com.blueseer.fgl.fglData.getAccountBalanceReport;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerLogin;
import static com.blueseer.utl.BlueSeerUtils.confirmServerSession;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import static com.blueseer.utl.BlueSeerUtils.getSiteFromSessionCookie;
import static com.blueseer.utl.BlueSeerUtils.killServerSession;
import com.blueseer.utl.EDData;
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
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;


/**
 *
 * @author terryva
 */
public class webServ extends HttpServlet {
    
    private static final String[] HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    
    response.setContentType("text/html");
    String sessionid = "";
    if (request.getSession() == null) {
        sessionid = request.getSession(true).getId();
    } else {
        sessionid = request.getSession().getId();
    }
    
/*
    if (request.getHeader("mycookie").isBlank()) {
        response.addCookie(new Cookie("mycookie", request.getSession(true).getId()));
    } else {
        response.getWriter().println("mycookie --> " + request.getHeader("mycookie"));
    }
    */
 
    String prog = "";
    if (request.getHeader("Prog") != null && ! request.getHeader("Prog").isBlank()) {
        prog = request.getHeader("Prog");
    } 
    
    // kill session
    if (request.getHeader("kill") != null) {
        killServerSession(request);
        return;
    }
    
    // if login
    if (request.getHeader("Pass") != null && ! request.getHeader("Pass").isBlank()) {
        if (! confirmServerLogin(request, sessionid)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("br549 authorization failed");
            response.getWriter().println("session=" + sessionid + "<br>");
            response.getWriter().println("RemoteAddr=" + request.getRemoteAddr() + "<br>");
            response.getWriter().println("RemoteHost=" + request.getRemoteHost() + "<br>");
            response.getWriter().println("RequestURI=" + request.getRequestURI() + "<br>");
            response.getWriter().println(getHeaders(request));
            Cookie[] cookies = request.getCookies(); 
            if (cookies != null) {
                for (Cookie c : cookies) {
                    response.getWriter().println("cookie --> " + c.getName() + " / " + c.getValue());
                }
            }
        } else {
            String cookiecontent = request.getHeader("User") + "," + sessionid + "," + request.getRemoteAddr() + "," + OVData.getDefaultSiteForUserid(request.getHeader("User"));
            Cookie c = new Cookie("bscookie", Base64.toBase64String(cookiecontent.getBytes()));
            c.setHttpOnly(true);
            response.addCookie(c);
        }
    } else { // must be session
        if (! confirmServerSession(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("br549x authorization failed");
            response.getWriter().println("session=" + sessionid + "<br>");
            response.getWriter().println("RemoteAddr=" + request.getRemoteAddr() + "<br>");
            response.getWriter().println("RemoteHost=" + request.getRemoteHost() + "<br>");
            response.getWriter().println("RequestURI=" + request.getRequestURI() + "<br>");
            response.getWriter().println(getHeaders(request));
            Cookie[] cookies = request.getCookies(); 
            if (cookies != null) {
                for (Cookie c : cookies) {
                    response.getWriter().println("cookie --> " + c.getName() + " / " + c.getValue());
                }
            }
        } else {
           response.setStatus(HttpServletResponse.SC_OK);
           response.getWriter().println(getData(prog, request));
        }
    }
    }

 @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
       
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

    private String getData(String prog, HttpServletRequest request) {     
        String r = "bad request: " + prog;
        String site = getSiteFromSessionCookie(request);
        
        switch (prog) {
            case "getPartnerCount" :
            ArrayList<String> list = EDData.getEDIPartners();
            r = String.valueOf(list.size());
            r = r +",,,";
            break; 
            
            case "getEDIDocTrans" :
            r = h_getEDIDocTrans(request, site);
            break;
            
        default:
            r = "Unknown Data Request";
        }
        
        
        return r;
    }

    private String h_getEDIDocTrans(HttpServletRequest request, String site) {
        String fromdate = "";
        String todate = "";
        String today = BlueSeerUtils.bsdate.format(new Date());
        if (request.getHeader("fromdate") != null && ! request.getHeader("fromdate").isBlank()) {
                fromdate = BlueSeerUtils.convertDateFormat("yyyyMMdd", request.getHeader("fromdate"));
        } else {
            fromdate = today;
        }   
        if (request.getHeader("todate") != null && ! request.getHeader("todate").isBlank()) {
            todate = BlueSeerUtils.convertDateFormat("yyyyMMdd", request.getHeader("todate"));
        } else {
            todate = today;
        }
        return EDData.get_edi_idx_JSON(site, fromdate, todate);
    }
}
