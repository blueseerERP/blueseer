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
import static com.blueseer.edi.apiUtils.calculateMIC;
import static com.blueseer.edi.apiUtils.createMDN;
import static com.blueseer.edi.apiUtils.hashdigest;
import com.blueseer.edi.apiUtils.mdn;
import static com.blueseer.edi.apiUtils.verifySignature;
import static com.blueseer.edi.apiUtils.verifySignatureView;
import com.blueseer.edi.ediData.as2_mstr;
import static com.blueseer.edi.ediData.getAS2InfoByIDs;
import static com.blueseer.edi.ediData.getAS2Mstr;
import com.blueseer.inv.invData;
import com.blueseer.sch.schData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.createMessage;
import static com.blueseer.utl.BlueSeerUtils.createMessageJSON;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.getSystemEncKeyAlt;
import static com.blueseer.utl.EDData.writeAS2Log;
import static com.blueseer.utl.EDData.writeAS2LogDetail;
import static com.blueseer.utl.EDData.writeAS2LogStop;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.encoders.Base64;
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
        response.getWriter().println("BlueSeer AS2 server response:  use POST method for AS2 transmission");
    }
     
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
       // BufferedReader reader = request.getReader();
        
        boolean isDebug = (this.getServletContext().getAttribute("debug") != null) ? true : false;
       
        if (request == null) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("BlueSeer AS2 server response:  no valid AS2 payload provided");
        } else {
            
           // response.setContentType("multipart/report");
            mdn thismdn = null;
            try {
                thismdn = processRequest(request, isDebug);
            } catch (Exception ex) {
                bslog(ex);
            }
            
             
             if (thismdn.isSigned().equals("1") ) {
               response.setContentType("multipart/signed; protocol=" + "\"" + "application/pkcs7-signature" + "\"" + "; " + " micalg=sha256; boundary=" + "\"" + thismdn.boundary() + "\"");
               response.setHeader("Content-Transfer-Encoding", "binary"); 
             } else {
              // response.setContentType("message/disposition-notification");
              // response.setHeader("Content-Type", "");
               response.setContentType("multipart/report; report-type=disposition-notification; boundary=" + "\"" + thismdn.boundary() + "\""); 
              // response.setHeader("Content-Transfer-Encoding", "binary"); 
             }
            
           // response.setContentType("multipart/signed; protocol=" + "\"" + "application/pkcs7-signature" + "\"" + "; " + " micalg=sha1; boundary=" + "\"" + thismdn.boundary() + "\""); 
           // response.setContentType("text/plain; report-type=disposition-notification"); 
          //  response.setContentType("message/disposition-notification"); 
            if (thismdn.headers() != null) {
                for (Map.Entry<String, String> z : thismdn.headers().entrySet()) {
                    response.setHeader(z.getKey(), z.getValue());
                }
            } 
                       
            response.setStatus(thismdn.status());
            response.getWriter().print(thismdn.message());
            
            
            if (isDebug) { 
            System.out.println("sent MDN boundary: " + thismdn.boundary());    
            System.out.println("sent MDN HTTP Status Code: " + thismdn.status());
            System.out.println("sent MDN isSigned option: " + thismdn.isSigned());
            LocalDateTime localDateTime = LocalDateTime.now();
            String now = localDateTime.format(DateTimeFormatter.ISO_DATE);
            String debugfile = "debugMDN." + now + "." + Long.toHexString(System.currentTimeMillis());
            Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
            try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
                
                Collection<String> headerNames = response.getHeaderNames();
                for (String headerName : headerNames) {
                    Collection<String> headerValues = response.getHeaders(headerName);
                    for (String headerValue : headerValues) {
                        stream.write((headerName + ": " + headerValue + "\r\n").getBytes());
                    }
                }
              /*  
                for (Map.Entry<String, String> z : thismdn.headers().entrySet()) {
                    String h = z.getKey() + ": " + z.getValue() + "\r\n";
                    stream.write(h.getBytes());
                }    
            stream.write("Content-Transfer-Encoding: binary\r\n".getBytes());
            */
            stream.write(thismdn.message().getBytes());
            }
        }
            
            
        }
    }
    
   
    public static mdn processRequest(HttpServletRequest request, boolean isDebug) throws IOException, Exception {
        String defaultsite = OVData.getDefaultSite();
        BufferedWriter output = null;
        String[] elementals = new String[]{"","","","","","","","1"}; // default elementals[7] to '1'...to sign MDN...can be override below once as2_id is determinable
        HashMap<String, String> returnheaders = new HashMap<String, String>();
        mdn mymdn = null;
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        ArrayList<String[]> logdet = new ArrayList<String[]>(); 
        as2_mstr as2m = null;
        
        // request to inputstream as bytes        
        try {
            byte[] content = null;
            try (InputStream is = request.getInputStream()) {
                content = is.readAllBytes(); 
            }
            
        // if null content
        if (content == null) {
            writeAS2LogStop(new String[]{"0","unknown","in","error","null content",now,"",defaultsite});
           // return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, "null content");
            return createMDN("3000", elementals, returnheaders, isDebug, as2m);
        }
        
       
        
       
        
        
        // check headers and fill HashMap
        HashMap<String, String> inHM = new HashMap<>();
        HashMap<String, String> outHM = new HashMap<>();
        
        
        System.out.println("SENDING IP: " + request.getRemoteAddr() + " / " + request.getRemoteHost());
        
        
        
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                        String key = (String) headerNames.nextElement();
                        inHM.putIfAbsent(key.toLowerCase(), request.getHeader(key));
                        System.out.println("Header: " + key +  "=" + request.getHeader(key));
                }
        } else {
            // header info unrecognizable...bail out
            writeAS2LogStop(new String[]{"0","unknown","in","error","http header tags unrecognizable",now,"", defaultsite});
            // return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, "http header tags unrecognizable");
            return createMDN("3005", elementals, returnheaders, isDebug, as2m);
        }
    
        
        // check for sender / receiver
        String sender = "";
        String sysas2user = ""; // EDData.getAS2id();
        String receiver = "";
        String subject = "";
        String messageid = "";
        String michash = "";
        String filename = "";
        String[] info = null;
        
        boolean validSignature = false;
        byte[] FileWHeadersBytes = null;
        byte[] FileBytes = null;
        byte[] Signature = null;
        InputStream is = null;
        String mic = "";
        
        
        if (inHM == null || inHM.isEmpty()) {
          writeAS2LogStop(new String[]{"0","unknown","in","error","There are zero http headers",now,"", defaultsite});
            return createMDN("3007", elementals, returnheaders, isDebug, as2m);   
        }
        
        if (inHM.containsKey("subject")) {
            subject = inHM.get("subject");
            elementals[2] = subject;
        }
        
        if (inHM.containsKey("message-id")) {
            messageid = inHM.get("message-id");
            elementals[4] = messageid;
        }
        
        if (inHM.containsKey("disposition-notification-options")) {
            michash = inHM.get("disposition-notification-options");
        }
        
        System.out.println("here--> Requested MDN options: " + michash);
        
        
        if (inHM.containsKey("as2-to")) {
            // set return header as opposite direction
            returnheaders.put("as2-from", inHM.get("as2-to"));
            receiver = inHM.get("as2-to");
            elementals[1] = receiver;
        } else {
            writeAS2LogStop(new String[]{"0","unknown","in","error","AS2 receiver ID unrecognized",now,"", defaultsite});
            return createMDN("3100", elementals, returnheaders, isDebug, as2m); 
        }
        
        if (inHM.containsKey("as2-from")) {
            // set return header as opposite direction
            returnheaders.put("as2-to", inHM.get("as2-from"));
            
            sender = inHM.get("as2-from");
            elementals[0] = sender;
            
            info = getAS2InfoByIDs(sender , receiver); // need to remove this in favor of as2m
            as2m = getAS2Mstr(sender, receiver);
            
            
            if (as2m == null || as2m.m()[0].equals("1")) {
              writeAS2LogStop(new String[]{"0","unknown","in","error","AS2 sender / receiver unknown with keys: " + sender + "/" + receiver,now,"",defaultsite});  
              //return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, "AS2 sender ID unknown with keys: " + sender + "/" + receiver);    
            return createMDN("3200", elementals, returnheaders, isDebug, as2m);
            } 
        } else {
            writeAS2LogStop(new String[]{"0","unknown","in","error","AS2 sender ID unrecognized",now,"",info[22]});
            // return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, "AS2 sender ID unrecognized"); 
            return createMDN("3200", elementals, returnheaders, isDebug, as2m);
        }
        
        if (as2m == null || as2m.m()[0].equals("1")) { 
              writeAS2LogStop(new String[]{"0","unknown","in","error","unable to find sender / receiver keys: " + sender + "/" + receiver,now,"",info[22]});
              return createMDN("3300", elementals, returnheaders, isDebug, as2m);   
        }
        
        
        elementals[6] = as2m.as2_id();  // assigns 6th element the value of as2_id
        elementals[7] = as2m.as2_signmdn(); // whether to sign mdn
        
        
        System.out.println("here--> Request Content Type: " + request.getContentType());    
          
        
        System.out.println("here--> encoding:" + request.getCharacterEncoding());
        
        boolean isSigned = false;
        
        if (isDebug) { 
            String debugfile = "debugAS2enc." + now + "." + Long.toHexString(System.currentTimeMillis()); 
            Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
            try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
            stream.write(content);
            }
        }
        
        // check for encryption if forced usage
        boolean isEncrypted = apiUtils.isEncrypted(content);
        
        if (! isEncrypted && info[12].equals("1")) {
           writeAS2LogStop(new String[]{"0","unknown","in","error","Encryption is required for this partner " + sender + "/" + receiver,now,"", info[22]}); 
           return createMDN("3400", elementals, returnheaders, isDebug, as2m);
        }
         
        byte[] finalContent = null;
         // now decrypt as necessary
         String systemEncKey = null;
         if (isEncrypted) {
          systemEncKey = getSystemEncKeyAlt(info[0]);   
          finalContent = apiUtils.decryptData(content, apiUtils.getPrivateKey(getSystemEncKeyAlt(info[0])) );
           if (finalContent == null) {
             writeAS2LogStop(new String[]{"0","unknown","in","error","Unable to decrypt...possible incorrect public key " + sender + "/" + receiver,now,"", info[22]}); 
             return createMDN("3003", elementals, returnheaders, isDebug, as2m);
           }  
         } else {
          finalContent = content;
         }
         
         
         // send content to file for testing
        if (isDebug) { 
            String debugfile = "debugAS2dec." + now + "." + Long.toHexString(System.currentTimeMillis());
            Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
            try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
            stream.write(finalContent);
            }
        }
      
        
        // if here...should have as2 sender / receiver / info data required to create legitimate MDN
        // write original master log record...retrieve log key for parent of detail to follow
        // also...from here on down use writeAS2Log instead of hard stop writeAS2LogStop
        elementals[0] = sender;
        elementals[1] = receiver;
        elementals[2] = subject;
        elementals[3] = filename;
        elementals[4] = messageid;
        elementals[5] = mic;
        int parent = writeAS2Log(new String[]{"0",sender,"in","success"," Init as2 inbound for partner: " + info[0] + "/" + sender + "/" + receiver,now,"", defaultsite}); 
        String parentkey = String.valueOf(parent);
        logdet.add(new String[]{parentkey, "info", "processing as2 for relationship " + sender + "/" + receiver});
        logdet.add(new String[]{parentkey, "info", "Incoming AS2 Message ID = " + messageid});
        logdet.add(new String[]{parentkey, "info", "Decryption system key = " + systemEncKey});
        logdet.add(new String[]{parentkey, "info", "MDN flat/eol = " + as2m.as2_flatmdn() + "/" + as2m.as2_eol()});
        
        
        // establish mimemultipart format of decrypted data
        MimeMultipart mp  = new MimeMultipart(new ByteArrayDataSource(finalContent, request.getContentType()));
        
        try {
            boolean isComplete = mp.isComplete();
            if (isComplete) {
                System.out.println("MimeMultiPart isComplete = " + isComplete);    
                System.out.println("request ContentType=" + request.getContentType());
                System.out.println("MimeMultipart count=" + mp.getCount() + "/" + mp.getContentType());
            }
        
        } catch (MessagingException e) {            
            System.out.println("Error evaluating multipart completion: " + e.getMessage());
            return createMDN("2005", elementals, returnheaders, isDebug, as2m);
        }
        
        //if (mp.getContentType().isEmpty()) {  
        //    return createMDN("2005", elementals, returnheaders, isDebug, as2m);
        //}
        
        // if signed...should have a parent mp with two sub-mps (one the file and the other the sig)
        logdet.add(new String[]{parentkey, "info", "MP count: " + mp.getCount() + " MP content-type: " + mp.getContentType()});
        for (int i = 0; i < mp.getCount(); i++) {
            BodyPart bodyPart = mp.getBodyPart(i);
            String contentType = bodyPart.getContentType();
           
            if (i == 0) {
                // skip...need to remove outer mp ...because mpsub is exactly same content as mp
                continue;
            }
                      
            
            System.out.println("here--> level 1 mp count: " + i + " contentType: " + contentType);
            
            
            // if signed...mpsub should have two parts (one the file and the other the sig)
            MimeMultipart mpsub = new MimeMultipart(new ByteArrayDataSource(finalContent, contentType));
            
            if (mpsub.getCount() < 2 && info[13].equals("1") ) { // info[10] sig required
            //  return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, "Signature is required for this partner " + sender + "/" + receiver);    
              return createMDN("2000", elementals, returnheaders, isDebug, as2m);
            }
            
               
               for (int j = 0; j < mpsub.getCount(); j++) {
                    MimeBodyPart mbp = (MimeBodyPart) mpsub.getBodyPart(j); 
                    
                    if (mbp == null) {
                        continue;
                    }
                    
                   // if (mbp.getFileName() != null && ! mbp.getFileName().equals("smime.p7s")) { // must be non sig file
                      if (! mbp.getContentType().toLowerCase().startsWith("application/pkcs7-signature")) { // must be non sig file
                      // writing mpbsub part 0 (file) out to byte_stream is necessary to verify sig
                      // because the headers in mpbsub part 0 are used during the creation of the sig
                      //  .getContent apparently drops the headers so the entire byte stream must be 'verfied'
                      ByteArrayOutputStream aos = new ByteArrayOutputStream();
                      mpsub.getBodyPart(0).writeTo(aos);
                      aos.close(); 
                      FileWHeadersBytes = aos.toByteArray();
                      
                      if (mbp.getFileName() == null) {
                          filename = "nofilename." + Long.toHexString(System.currentTimeMillis());
                      } else {
                          filename = mbp.getFileName();
                      }
                      logdet.add(new String[]{parentkey, "info", "inside non-pkcs7-signature bodypart: " + mbp.getContentType() + " filename: " + filename});
                      
                     
                     if (mic.isBlank()) { // do only once
                        mic = hashdigest(FileWHeadersBytes, info[20]);
                        logdet.add(new String[]{parentkey, "info", "calculated MIC = " + mic});
                        elementals[5] = mic;
                     }
                      // now get file without headers into byte array
                      is = mbp.getInputStream();
                      FileBytes = is.readAllBytes();
                      is.close();
                     
                    }
                    
                    if (mbp.getFileName() != null && mbp.getFileName().equals("smime.p7s")) { // must be sig
                        Signature = IOUtils.toByteArray((InputStream) mbp.getContent());
                      //  if (! mbp.getEncoding().toLowerCase().equals("base64")) {
                      //    String sig = new String(Base64.encode(Signature));
                      //    System.out.println("here--> signature content-transfer-encoding not provided...sig shown as base64 is: \n" + sig);
                      //  }
                    }
                   
                    System.out.println("here--> level 2 mp count: " + j + " contentType/fileName/transferEncoding: " + mbp.getContentType() + " / " + mbp.getFileName() + " / " + mbp.getEncoding());
               
               } // for each mpsub (should be two if signed) 
               
           if (FileWHeadersBytes == null || FileBytes == null) {
            // must be unsigned regular mime body part
             for (int j = 0; j < mpsub.getCount(); j++) { // only getting last mime body part if not normal signed document
                    logdet.add(new String[]{parentkey, "info", "only getting last mime body part if not normal signed document " + sender + "/" + receiver,now,"" });
                    MimeBodyPart mbp = (MimeBodyPart) mpsub.getBodyPart(j); 
                    InputStream ins = mbp.getInputStream();
                    FileBytes = ins.readAllBytes();
                    ins.close();
                    
                    ByteArrayOutputStream aos = new ByteArrayOutputStream();
                    mpsub.getBodyPart(0).writeTo(aos);
                    aos.close(); 
                    FileWHeadersBytes = aos.toByteArray();
             }
              
              if (FileBytes == null) { // if neither normal signed nor non-signed mimebody content...then null it
               // writeAS2LogStop(new String[]{"0","unknown","in","error","Null content in FileBytes or FileWHeader Bytes " + sender + "/" + receiver,now,"", info[22]});
                logdet.add(new String[]{parentkey, "error", "Null content in FileBytes or FileWHeader Bytes " + sender + "/" + receiver,now,"" });
              if (! logdet.isEmpty()) {
                    writeAS2LogDetail(logdet, as2m.as2_id());
              }
                return createMDN("2010", elementals, returnheaders, isDebug, as2m);
              }
              
           }

            // if sig is null...then return fail mdn
            if (Signature == null && info[13].equals("1")) {
                // return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, "Signature content is null" + sender + "/" + receiver);
              //  writeAS2LogStop(new String[]{"0","unknown","in","error","Signature mp content is null " + sender + "/" + receiver,now,"", info[22]});
              logdet.add(new String[]{parentkey, "error", "Signature mp content is null " + sender + "/" + receiver,now,"" });
              if (! logdet.isEmpty()) {
                    writeAS2LogDetail(logdet, as2m.as2_id());
              }
                return createMDN("2015", elementals, returnheaders, isDebug, as2m);
            } 
            
            if (Signature != null) {
              logdet.add(new String[]{parentkey, "info", "signature check " + sender + "/" + receiver,now,"" });  
              validSignature = verifySignature(FileWHeadersBytes, Signature, isDebug); 
              logdet.add(new String[]{parentkey, "info", "signature verification:  " + String.valueOf(validSignature),now,"" });
              logdet.add(new String[]{parentkey, "info", "signature required:  " + info[13] ,now,"" });
            }
            
            // if it's not a valid signature and signing is required...then return fail mdn
            if (! validSignature && info[13].equals("1")) {
              logdet.add(new String[]{parentkey, "error", "invalid signature " + sender + "/" + receiver,now,"" });
              if (! logdet.isEmpty()) {
                    writeAS2LogDetail(logdet, as2m.as2_id());
              }
              return createMDN("2020", elementals, returnheaders, isDebug, as2m);
            } 
            
        


           if (isDebug) {
            System.out.println("validSignature: " + validSignature);
            if (FileWHeadersBytes != null) {
                System.out.println("ByteCount FileWHeadersBytes: " + String.valueOf(FileWHeadersBytes.length)); 
                String debugfile = "FileWHeadersBytes." + now + "." + Long.toHexString(System.currentTimeMillis());
                Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
                try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
                stream.write(FileWHeadersBytes);
                }
            }
            if (Signature != null) {
                System.out.println("ByteCount Signature: " + String.valueOf(Signature.length)); 
                String debugfile = "Signature." + now + "." + Long.toHexString(System.currentTimeMillis());
                Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
                try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
                stream.write( new String(Base64.encode(Signature)).getBytes());
                }
            }
           }
        }  // for parent mp ...should be just one
        
        System.out.println("here--> mic: " + mic);
        System.out.println("here--> messageid: " + messageid);
        
        

        // now save file
        elementals[3] = filename;
        if (info[17].isBlank()) {
            info[17] = "edi/as2/in";
        }
        Path path = FileSystems.getDefault().getPath(info[17] + "/" + filename);
        if (Files.exists(path)) {
            filename = filename + "_" + Long.toHexString(System.currentTimeMillis());
            path = FileSystems.getDefault().getPath(info[17] + "/" + filename);
        }
        Files.write(path, FileBytes);
        
        logdet.add(new String[]{parentkey, "info", "receiving file = " + path + " at size: " + FileBytes.length});
        
     
        } catch (FileNotFoundException ex) {
            bslog(ex);
            return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, " File Not Found Error occurred");
        } catch (IOException ex) {
            bslog(ex);
            return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, " IOException Error occurred");
        } catch (CMSException ex) {
            bslog(ex);
            return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, " Decryption Error occurred");
        } catch (MessagingException ex) {
                bslog(ex);
                return new mdn(HttpServletResponse.SC_BAD_REQUEST, null, " Malformed MIME Message");
        } finally {
            if (output != null) {
             output.close(); 
            }
           try {
            mymdn = createMDN("1000", elementals, returnheaders, isDebug, as2m); 
           } catch (MessagingException ex) {
                bslog(ex);
            }
           if (! logdet.isEmpty()) {
           writeAS2LogDetail(logdet, as2m.as2_id());
           }
           
        }
        
        return mymdn; 
    }
   
}
