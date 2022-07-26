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
import static com.blueseer.edi.apiUtils.createMDN;
import com.blueseer.edi.apiUtils.mdn;
import static com.blueseer.edi.ediData.getAS2InfoByIDs;
import com.blueseer.inv.invData;
import com.blueseer.sch.schData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.createMessage;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.getSystemEncKey;
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
import java.util.Enumeration;
import java.util.HashMap;
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
import java.util.Map;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
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
       
        if (request == null) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("no valid payload provided");
        } else {
            response.setContentType("text/plain");
           // response.setStatus(HttpServletResponse.SC_OK);
            mdn thismdn = processRequest(request);
            response.setStatus(thismdn.status());
            response.getWriter().println(thismdn.message());
        }
    }
    
   
    public static mdn processRequest(HttpServletRequest request) throws IOException {
       
        /*
        
        This is a work in progress....  
        
        
        */
        
        String x = "";
        Path path = FileSystems.getDefault().getPath("temp" + "/" + "somefile");
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
        String[] elementals = new String[]{"","","",""};
        mdn mymdn = null;

        // request to inputstream as bytes        
        try {
        
            
            byte[] content = null;
            try (InputStream is = request.getInputStream()) {
                content = is.readAllBytes();
            }
            
        // if null content
        if (content == null) {
            return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, "null content");
        }
        
        
        // check headers and fill HashMap
        HashMap<String, String> inHM = new HashMap<>();
        HashMap<String, String> outHM = new HashMap<>();
        
       
        
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                        String key = (String) headerNames.nextElement();
                        inHM.putIfAbsent(key, request.getHeader(key));
                        System.out.println("here--> Header: " + key +  "=" + request.getHeader(key));
                }
        } else {
            // header info unrecognizable...bail out
            return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, "http header tags unrecognizable");
        }
        
        // check for sender / receiver
        String sender = "";
        String sysas2user = EDData.getAS2id();
        String receiver = "";
        String subject = "";
        String filename = "";
        String[] info = null;
        
        
        if (inHM.containsKey("AS2-To")) {
            if (inHM.get("AS2-To").equals(sysas2user)) {
              receiver = sysas2user;  
            } else {
              return new mdn(HttpServletResponse.SC_OK, null, "AS2 receiver ID unknown");  
            }
        } else {
            return new mdn(HttpServletResponse.SC_OK, null, "AS2 receiver ID unrecognized"); 
        }
        
        if (inHM.containsKey("AS2-From")) {
            sender = inHM.get("AS2-From");
            info = getAS2InfoByIDs(sender , receiver);
            if (info == null) {
              return new mdn(HttpServletResponse.SC_OK, null, "AS2 sender ID unknown with keys: " + sender + "/" + receiver);    
            } 
        } else {
            return new mdn(HttpServletResponse.SC_OK, null, "AS2 sender ID unrecognized"); 
        }
        
        if (inHM.containsKey("Subject")) {
            subject = inHM.get("Subject");
        }
        
        // if here...should have as2 sender / receiver / info data required to create legitimate MDN
        elementals[0] = sender;
        elementals[1] = receiver;
        elementals[2] = subject;
        elementals[3] = filename;
        
        System.out.println("here--> Request Content Type: " + request.getContentType());    
          
        
        System.out.println("here--> encoding:" + request.getCharacterEncoding());
        
        boolean isEncrypted = false;
        boolean isSigned = false;
        
        byte[] decryptedContent = APIMaint.decryptData(content, apiUtils.getPrivateKey(getSystemEncKey()) );
         
        MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(decryptedContent, request.getContentType()));
        
        if (mp.getCount() > 1) {
           BodyPart bp = mp.getBodyPart(0); 
           if (bp.getContentType().contains("multipart/signed")) {
              // if here...decryption must have occurred successfully...else bail out with failed MDN
              isEncrypted = true;
           }
        }
        
        
        if (! isEncrypted) {
            
        }
        
        
        for (int i = 0; i < mp.getCount(); i++) {
            BodyPart bodyPart = mp.getBodyPart(i);
            String contentType = bodyPart.getContentType();
            if (contentType.contains("multipart/signed")) {
                
            }
            System.out.println("here--> level 1 mp count: " + i + " contentType: " + contentType);
            MimeMultipart mp2 = new MimeMultipart(new ByteArrayDataSource(decryptedContent, contentType));
            if (mp2.getCount() > 1) {
               for (int j = 0; j < mp2.getCount(); j++) {
                    MimeBodyPart mbp = (MimeBodyPart) mp2.getBodyPart(j); 
                    // resume here  ...use MimeBodyPart instead of BodyPart...and spit out attributes for debug
                    String contentTypePayload = mbp.getContentType();
                    if (! contentTypePayload.contains("pkcs7-signature") && contentTypePayload.contains("file=")) {
                      String[] elements = contentTypePayload.split(";");
                      for (String g : elements) {
                          if (g.startsWith("file=")) {
                              filename = g.substring(5);
                          }
                      }
                    }
                    System.out.println("here--> level 2 mp count: " + j + " contentType: " + contentTypePayload);
               } 
            }
        }  
        
        elementals[3] = filename;
         
         String datastring = new String(decryptedContent);   
         output.write(datastring);
         System.out.println("here--> decryption");
         System.out.println(datastring);
          
        } catch (FileNotFoundException ex) {
            bslog(ex);
        } catch (IOException ex) {
            bslog(ex);
        } catch (CMSException ex) {
            bslog(ex);
        } catch (MessagingException ex) {
            Logger.getLogger(AS2Serv.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
           output.close();  
        }
        
        
        try {
            mymdn = createMDN("1000", elementals, null);
        } catch (MessagingException ex) {
            bslog(ex);
        }
        
        return mymdn; 
    }
   
}
