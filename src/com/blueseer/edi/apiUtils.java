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
package com.blueseer.edi;

import static bsmf.MainFrame.bslog;
import static com.blueseer.edi.APIMaint.signDataSimple;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEException;

/**
 *
 * @author terryva
 */
public class apiUtils {
    public static String postAS2( URL url, String verb, String as2From, String as2To, String as2file, String internalURL) throws FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, NoSuchProviderException, CertificateEncodingException, CMSException, SMIMEException, MessagingException, Exception {
        
        StringBuilder r = null;
        
        Path as2filepath = FileSystems.getDefault().getPath(as2file);
        if (! Files.exists(as2filepath)) {
           return "source file does not exist"; 
        }
        
        Security.addProvider(new BouncyCastleProvider());
        CertificateFactory certFactory = CertificateFactory
          .getInstance("X.509", "BC");

        X509Certificate certificate = (X509Certificate) certFactory
          .generateCertificate(new FileInputStream("c:\\junk\\terrycer.cer"));

        char[] keystorePassword = "terry".toCharArray();
        char[] keyPassword = "terry".toCharArray();

        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new FileInputStream("c:\\junk\\terryp12.p12"), keystorePassword);
        PrivateKey key = (PrivateKey) keystore.getKey("terry", keyPassword);
        
       
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String boundary = Long.toHexString(System.currentTimeMillis()); 
        String messageid = "<BLUESEER-" + now + "." + boundary + "@Blueseer Software>";
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.
        try {
        CloseableHttpClient client = HttpClients.createDefault();
           
        String filecontent = Files.readString(as2filepath);
          
        MimeBodyPart mbp = signDataSimple(filecontent.getBytes(StandardCharsets.UTF_8),certificate,key);
          
          
          String newboundary = "";
          String[] mb = mbp.getContentType().split(";");
          for (String s : mb) {
              if (s.contains("boundary=")) {
                  String[] mbs = s.split("=", 2);
                  newboundary = mbs[1].trim().replace("\"", "");
              }
          }
          //System.out.println("boundary: " + mbp.getContentType());
          //System.out.println("myboundary: " + newboundary);
        
  
          
        URL urlObj = new URL(url.toString());
        RequestBuilder rb = RequestBuilder.post();
        rb.setUri(urlObj.toURI());
        rb.addHeader("User-Agent", "RPT-HTTPClient/0.3-3I (Windows Server 2016)"); 
        rb.addHeader("AS2-To", as2To);
        rb.addHeader("AS2-From", as2From); 
        rb.addHeader("AS2-Version", "1.2"); 
        rb.addHeader("Mime-Version", "1.0");
        rb.addHeader("Subject", "as2");
        rb.addHeader("Accept-Encoding", "deflate, gzip, x-gzip, compress, x-compress");
        rb.addHeader("Disposition-Notification-Options", "signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1");
        rb.addHeader("Disposition-Notification-To", internalURL);
        rb.addHeader("Message-ID", messageid);
        rb.addHeader("Recipient-Address", url.toString());
        rb.addHeader("EDIINT-Features", "CEM, multiple-attachments, AS2-Reliability");
        rb.addHeader("Content-Type", "multipart/signed; protocol=\"application/pkcs7-signature\"; boundary=" + "\"" + newboundary + "\"" + "; micalg=sha1");
        rb.addHeader("Content-Disposition", "attachment; filename=smime.p7m");
         
          InputStreamEntity ise = new InputStreamEntity(mbp.getInputStream());
          rb.setEntity(new BufferedHttpEntity(ise));
          HttpUriRequest request = rb.build();
          
          CloseableHttpResponse response = client.execute(request);
          BufferedReader br = null;
        
        if (response.getStatusLine().getStatusCode() != 200) {
                r.append(response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
                //throw new RuntimeException("Failed : HTTP error code : "
                //		+ conn.getResponseCode());
        } else {
            r.append("SUCCESS: " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
        }
        
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity); 
        r.append(result);
       
        } catch (MalformedURLException e) {
            bslog(e);
            bsmf.MainFrame.show("MalformedURLException");
        } catch (IOException ex) {
            bslog(ex);
            bsmf.MainFrame.show("IOException");
        } 
        
        return r.toString();
    }
    
}
