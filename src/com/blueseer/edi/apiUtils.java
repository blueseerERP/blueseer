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
import static com.blueseer.edi.AS2Maint.certs;
import static com.blueseer.edi.ediData.getKeyStoreByUser;
import static com.blueseer.edi.ediData.getKeyStorePass;
import static com.blueseer.edi.ediData.getKeyUserPass;
import static com.blueseer.utl.EDData.writeAS2Log;
import static com.blueseer.utl.EDData.writeAS2LogDetail;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author terryva
 */
public class apiUtils {
    
    public static PrivateKey getPrivateKey(String user)  {
        PrivateKey key = null;
        FileInputStream fis = null;
        try {
            String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass
            k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
            k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
            char[] keyPassword = k[4].toCharArray();
            KeyStore keystore = KeyStore.getInstance("PKCS12");
             fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
            keystore.load(fis, k[2].toCharArray());
            key = (PrivateKey) keystore.getKey(k[3], keyPassword);
            //System.out.println("key-->");
            //System.out.println(key);
        } catch (KeyStoreException ex) {
            bslog(ex);
        } catch (FileNotFoundException ex) {
            bslog(ex);
        } catch (IOException ex) {
            bslog(ex);
        } catch (NoSuchAlgorithmException ex) {
            bslog(ex);
        } catch (CertificateException ex) {
            bslog(ex);
        } catch (UnrecoverableKeyException ex) {
            bslog(ex);
        } finally {
          if (fis != null ) {
              try {
                  fis.close();
              } catch (IOException ex) {
                  bslog(ex);
              }
          }
          
        }
        return key;
    }
    
    public static X509Certificate getPublicKey(String user)  {
        X509Certificate cert = null;
        FileInputStream fis = null;
        try {
            String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass
            k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
            k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
            KeyStore keystore = KeyStore.getInstance("PKCS12");
             fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
            keystore.load(fis, k[2].toCharArray());
            cert = (X509Certificate) keystore.getCertificate(user);
            //System.out.println("here-->" + cert.getSerialNumber());
        } catch (KeyStoreException ex) {
            bslog(ex);
        } catch (FileNotFoundException ex) {
            bslog(ex);
        } catch (IOException ex) {
            bslog(ex);
        } catch (NoSuchAlgorithmException ex) {
            bslog(ex);
        } catch (CertificateException ex) {
            bslog(ex);
        } finally {
          if (fis != null ) {
              try {
                  fis.close();
              } catch (IOException ex) {
                  bslog(ex);
              }
          }
          
        }
        return cert;
    }
    
    public static String hashdigest(byte[] indata, String algo) {
        String x;
        
        MessageDigest messageDigest = null;
                    try {
                        messageDigest = MessageDigest.getInstance(algo);  // SHA-1, etc
                    } catch (NoSuchAlgorithmException ex) {
                        bslog(ex);
                    }
        byte[] hashedbytes = messageDigest.digest(indata);
        x = new String(Base64.encode(hashedbytes));
        return x;
    }
    
    public static byte[] encryptData(byte[] data, X509Certificate encryptionCertificate, String algo) throws CertificateEncodingException, CMSException, IOException {
        ASN1ObjectIdentifier x = null;
        if (algo.equals("AES128_CBC")) {
            x = CMSAlgorithm.AES128_CBC;
        } else if (algo.equals("AES192_CBC")) {
            x = CMSAlgorithm.AES192_CBC;
        } else if (algo.equals("AES256_CBC")) {
            x = CMSAlgorithm.AES256_CBC;
        } else if (algo.equals("DES_CBC")) {
            x = CMSAlgorithm.DES_CBC;  
        } else if (algo.equals("DES_EDE3_CBC")) {
            x = CMSAlgorithm.DES_EDE3_CBC;    
        } else {
           x = CMSAlgorithm.AES128_CBC;  
        }
        byte[] encryptedData = null;
        if (null != data && null != encryptionCertificate) {
            CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator
              = new CMSEnvelopedDataGenerator();

            JceKeyTransRecipientInfoGenerator jceKey 
              = new JceKeyTransRecipientInfoGenerator(encryptionCertificate);
            cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
            CMSTypedData msg = new CMSProcessableByteArray(data);
            OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(x)
              .setProvider("BC").build();
            CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator
              .generate(msg,encryptor);
            encryptedData = cmsEnvelopedData.getEncoded();
        }
			    return encryptedData;
	
}
    
    public static byte[] decryptData(byte[] encryptedData,PrivateKey decryptionKey)throws CMSException {
            byte[] decryptedData = null;
            if (null != encryptedData && null != decryptionKey) {
                CMSEnvelopedData envelopedData = new CMSEnvelopedData(encryptedData);
                Collection<RecipientInformation> recipients = envelopedData.getRecipientInfos().getRecipients();
                KeyTransRecipientInformation recipientInfo = (KeyTransRecipientInformation) recipients.iterator().next();
                JceKeyTransRecipient recipient = new JceKeyTransEnvelopedRecipient(decryptionKey);
                return recipientInfo.getContent(recipient);
            }
            return decryptedData;
}
    
    public static boolean verifySignature(final byte[] plaintext, final byte[] signedData)  {
        boolean x = false;
        try {
            CMSSignedData s = new CMSSignedData(new CMSProcessableByteArray(plaintext), signedData);
            Store certstore = s.getCertificates();
            SignerInformationStore signers = s.getSignerInfos();
            Collection<SignerInformation> c = signers.getSigners();
            SignerInformation signer = c.iterator().next();
            Collection<X509CertificateHolder> certCollection = certstore.getMatches(signer.getSID());
            Iterator<X509CertificateHolder> certIt = certCollection.iterator();
            if (! certIt.hasNext()) {
                return x;
            }
            X509CertificateHolder certHolder = certIt.next();
            x = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certHolder));
        } catch ( CMSException | OperatorCreationException | CertificateException ex) {
            bslog(ex);
        }
        return x;
}
    
  
     public static MimeBodyPart signData(
          byte[] data, 
          X509Certificate signingCertificate,
          PrivateKey signingKey) throws Exception {
            byte[] signedMessage = null;
            List<X509Certificate> certList = new ArrayList<X509Certificate>();
            CMSTypedData cmsData= new CMSProcessableByteArray(data);
            certList.add(signingCertificate);
            certs = new JcaCertStore(certList);

            SMIMESignedGenerator sGen = new SMIMESignedGenerator(false ? SMIMESignedGenerator.RFC3851_MICALGS : SMIMESignedGenerator.RFC5751_MICALGS);
            JcaSimpleSignerInfoGeneratorBuilder jSig = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC");
             SignerInfoGenerator sig = jSig.build("SHA1withRSA", signingKey, signingCertificate);
            sGen.addSignerInfoGenerator(sig);
            sGen.addCertificates(certs);
            MimeBodyPart dataPart = new MimeBodyPart();
            dataPart.removeHeader("Content-Type");
            dataPart.removeHeader("Content-Disposition");
            
            dataPart.setText(new String(data));
            dataPart.setHeader("Content-Type", "application/edi-x12; file=test.txt");
            dataPart.setHeader("Content-Disposition", "attachment; filename=test.txt");
            MimeMultipart signedData = sGen.generate(dataPart);
            MimeBodyPart tmpBody = new MimeBodyPart();
            tmpBody.setContent(signedData);
            
        // Content-type header is required, unit tests fail badly on async MDNs if not set.
            tmpBody.setHeader("Content-Type", signedData.getContentType());
            
           
            return tmpBody;
	}

    
    
    public static X509Certificate getCert(String certfile) throws CertificateException, NoSuchProviderException {
        X509Certificate certificate = null;
        Path certfilepath = FileSystems.getDefault().getPath("edi/certs/" + certfile);
        if (! Files.exists(certfilepath)) {
             // throw new RuntimeException("bad path to cert file: " + certfile);
             return certificate; // return null
        }
       // System.out.println("here->" + certfilepath.toString());
        Security.addProvider(new BouncyCastleProvider());
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
        try (FileInputStream fis = new FileInputStream(certfilepath.toFile())) {
            certificate = (X509Certificate) certFactory.generateCertificate(fis);
        } catch (IOException ex) {
            bslog(ex);
        }
        
        return certificate;
    }
    
    public static String setMessageID() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "<BLUESEER-" + now + "." + Long.toHexString(System.currentTimeMillis()) + "@Blueseer Software>";
    }
    
    public static String setBoundary() {
        return "BSPart_" + Long.toHexString(System.currentTimeMillis());
    }
    
    public static String getPackagedBoundary(MimeBodyPart mbp) throws MessagingException {
        
        String[] mb = mbp.getContentType().split(";");
        for (String s : mb) {
            if (s.contains("boundary=")) {
                String[] mbs = s.split("=", 2);
                return mbs[1].trim().replace("\"", "");
            }
        }
        return null;
    }
    
    public static String postAS2( String as2id) throws MessagingException, MalformedURLException, URISyntaxException, IOException, CertificateException, NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateEncodingException, CMSException, SMIMEException, Exception  {
        
        StringBuilder r = new StringBuilder();
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        ArrayList<String[]> logdet = new ArrayList<String[]>(); 
        
        
        
        
     //   Path as2filepath = FileSystems.getDefault().getPath(as2file);
     //   if (! Files.exists(as2filepath)) {
     //      return "source file does not exist"; 
      //  }
       
       
        // gather pertinent info for this AS2 ID / Partner
        String[] tp = ediData.getAS2Info(as2id);
        String url = tp[15] + "://" + tp[1] + ":" + tp[2] + "/" + tp[3];
        String as2To = tp[4];
        String as2From = tp[5];
        String internalURL = tp[6];
        String sourceDir = tp[16];
        String signkeyid = tp[7];
        
        
        int parent = writeAS2Log(new String[]{"0",as2From,"out",""," Init as2 outbound for partner: " + as2id + "/" + as2From + "/" + as2To,now,""}); 
        String parentkey = String.valueOf(parent);
        logdet.add(new String[]{parentkey, "info", "processing as2 for relationship " + as2From + "/" + as2To});
        logdet.add(new String[]{parentkey, "info", "Sending to URL / Port / Path = " + url});
        logdet.add(new String[]{parentkey, "info", "Source Directory: " + sourceDir});
        logdet.add(new String[]{parentkey, "info", "Encryption Cert file: " + tp[11]});
        logdet.add(new String[]{parentkey, "info", "Signing Key ID: " + signkeyid});
        
       
        System.out.println("here->" + as2To + "/" +  as2From + "/" + internalURL + "/" + sourceDir + "/" + signkeyid);
        
        X509Certificate encryptcertificate = getCert(tp[11]);
        if (encryptcertificate == null) {
          logdet.add(new String[]{parentkey, "error", "Unable to retrieve encryption cert file " + tp[11]}); 
          writeAS2LogDetail(logdet);
          return "Unable to retrieve encryption cert file " + tp[11];
        }
        
        logdet.add(new String[]{parentkey, "info", "Encryption with: " + encryptcertificate.getIssuerX500Principal().getName() + "/" + encryptcertificate.getSigAlgName()});
        logdet.add(new String[]{parentkey, "info", "Encryption Serial#: " + encryptcertificate.getSerialNumber().toString(16)});
        logdet.add(new String[]{parentkey, "info", "Encryption Expiration Window: " + encryptcertificate.getNotBefore() + "/" + encryptcertificate.getNotAfter()});
        
        
        String[] k = getKeyStoreByUser(signkeyid); // store, storeuser, storepass, user, pass
        k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
        k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
    //    System.out.println("here->" + k[0] + "/" +  k[1] + "/" + k[2] + "/" + k[3] + "/" + k[4]);
        
       
        char[] keyPassword = k[4].toCharArray();  
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        
        FileInputStream fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
        keystore.load(fis, k[2].toCharArray());
        fis.close();
        
        PrivateKey key = (PrivateKey) keystore.getKey(k[3], keyPassword);
        X509Certificate signcertificate = (X509Certificate) keystore.getCertificate(k[3]);
        
        if (signcertificate == null) {
          logdet.add(new String[]{parentkey, "error", "Unable to retrieve signing cert " + k[3]}); 
          writeAS2LogDetail(logdet);
          return "Unable to retrieve signing cert " + k[3];
        }
        
        logdet.add(new String[]{parentkey, "info", "Signing with: " + signcertificate.getIssuerX500Principal().getName() + "/" + signcertificate.getSigAlgName()});
        logdet.add(new String[]{parentkey, "info", "Signing Serial#: " + signcertificate.getSerialNumber().toString(16)});
        logdet.add(new String[]{parentkey, "info", "Signing Expiration Window: " + signcertificate.getNotBefore() + "/" + signcertificate.getNotAfter()});
        
        
        
        Path as2filepath = null;
        File folder = new File(sourceDir);
        File[] listOfFiles = folder.listFiles();
        
        
        for (int i = 0; i < listOfFiles.length; i++) {
            if (! listOfFiles[i].isFile()) {
            continue;
            }
            
            as2filepath = FileSystems.getDefault().getPath(sourceDir + "/" + listOfFiles[i].getName()); 
            logdet.add(new String[]{parentkey, "info", "Transmitting file: " + listOfFiles[i].getName()});
       
        
        String messageid = setMessageID();
        
        CloseableHttpClient client = HttpClients.createDefault();
           
        String filecontent;
        try {
            filecontent = Files.readString(as2filepath);
        } catch (IOException ex) {
            bslog(ex);
            continue;
        }
        
        MimeBodyPart mbp;
        byte[] signedAndEncrypteddata = null;
        
        
        boolean isSignedAndEncrypted = true;
        // need signed, signed+enc, enc, none ....condition logic here
        if (filecontent != null) {    
                try {
                    mbp = signData(filecontent.getBytes(StandardCharsets.UTF_8),signcertificate,key);
                    
                } catch (Exception ex) {
                    bslog(ex);
                    continue;
                }
        } else {
           bslog("file content is null in AS2Post");
           continue; 
        }
        
        MimeBodyPart mbp2 = new MimeBodyPart();
        MimeMultipart mp = new MimeMultipart();
        String newboundary = getPackagedBoundary(mbp);  
        if (isSignedAndEncrypted) {
         // mbp.addHeader("Content-Type", "multipart/signed; protocol=\"application/pkcs7-signature\"; boundary=" + "\"" + newboundary + "\"" + "; micalg=sha1");
         // mbp.addHeader("Content-Disposition", "attachment; filename=smime.p7m");
           /*
           mbp2 = encryptDataSMIME(mbp.getInputStream().readAllBytes(), encryptcertificate);
           Enumeration<?> list = mbp2.getAllHeaders();
            while (list.hasMoreElements()) {
                javax.mail.Header head = (javax.mail.Header) list.nextElement();
                System.out.println(head.getName() + ": " + head.getValue());
            }
           */ 
          mp.addBodyPart(mbp);
          mbp2.setContent(mp);
          mbp2.addHeader("Content-Type", "multipart/signed; protocol=\"application/pkcs7-signature\"; boundary=" + "\"" + newboundary + "\"" + "; micalg=sha1");
          mbp2.addHeader("Content-Disposition", "attachment; filename=smime.p7m");
        
          signedAndEncrypteddata = encryptData(mbp2.getInputStream().readAllBytes(), encryptcertificate, tp[18]);
          
        }
        
        
       // BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
       // output.write(new String(Base64.encode(signedAndEncrypteddata)));
       // output.close();
        /*
        Path path = FileSystems.getDefault().getPath("temp" + "/" + "beforefile");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path.toFile()));
        bos.write(signedAndEncrypteddata);
        bos.flush();
        bos.close();
        */
        
        URL urlObj = new URL(url);
        RequestBuilder rb = RequestBuilder.post();
        rb.setUri(urlObj.toURI());
        
        if (! isSignedAndEncrypted) {
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
        } else {
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
        rb.addHeader("Content-Type", "application/pkcs7-mime; smime-type=enveloped-data; name=smime.p7m");
        rb.addHeader("Content-Transfer-Encoding", "binary");
        rb.addHeader("Content-Disposition", "attachment; filename=smime.p7m");    
        }
        
        InputStreamEntity ise = new InputStreamEntity(new ByteArrayInputStream(signedAndEncrypteddata));
          
          rb.setEntity(new BufferedHttpEntity(ise));
          HttpUriRequest request = rb.build();
          
        
        try (CloseableHttpResponse response = client.execute(request)) {
        if (response.getStatusLine().getStatusCode() != 200) {
                r.append(response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
                //throw new RuntimeException("Failed : HTTP error code : "
                //		+ conn.getResponseCode());
        } else {
            r.append("SUCCESS: " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase() + "\n");
        }
        
        HttpEntity entity = response.getEntity();
        byte[] indata = EntityUtils.toByteArray(entity);
        String result = new String(indata); 
        
        // save MDN file if present
        MimeMultipart mpr  = new MimeMultipart(new ByteArrayDataSource(indata, entity.getContentType().getValue()));
        for (int z = 0; z < mpr.getCount(); z++) {
            MimeBodyPart mbpr = (MimeBodyPart) mpr.getBodyPart(z);
            if (mbpr.getContentType().contains("disposition")) {
                String filename = "mdn." + now + "." + Long.toHexString(System.currentTimeMillis());
                Path path = FileSystems.getDefault().getPath("edi/mdn" + "/" + filename);
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
                String datastring = new String(mbpr.getInputStream().readAllBytes());   
                output.write(datastring);
                output.close();
                logdet.add(new String[]{parentkey, "info", "MDN file received: " + filename});
            }
        }
            
        
        
        r.append(result);
        } catch (HttpHostConnectException | ConnectTimeoutException ex) {
          logdet.add(new String[]{parentkey, "error", " Connection refused or timeout from server "}); 
          writeAS2LogDetail(logdet);
          return "Connection refused or timeout from server ";
        } 
        
        
        
    } // for each file
        writeAS2LogDetail(logdet);
        return r.toString();
    }
    
    
    public static MimeMultipart bundleit(String z, String receiver, String messageid, String mic, String status) {
        MimeBodyPart mbp = new MimeBodyPart();
        MimeBodyPart mbp2 = new MimeBodyPart();
        MimeMultipart mp = new MimeMultipart();
        try {
            mbp.setText(z);
            mbp.setHeader("Content-Type", "text/plain; charset=us-ascii");
            mbp.setHeader("Content-Transfer-Encoding", "7bit");
            
            String y = """
                       Reporting-UA: BlueSeer Software
                       Original-Recipient: rfc822; %s
                       Final-Recipient: rfc822; %s
                       Original-Message-ID: %s
                       Disposition: automatic-action/MDN-sent-automatically; %s
                       Received-Content-MIC: %s, sha
                       """.formatted(receiver, receiver, messageid, status, mic);
            
            mbp2.setText(y);
            mbp2.setHeader("Content-Type", "message/disposition-notification");
            mbp2.setHeader("Content-Transfer-Encoding", "7bit");
            
            mp.addBodyPart(mbp);
            mp.addBodyPart(mbp2);
            
            
        } catch (MessagingException ex) {
            bslog(ex);
        }
        return mp;
    }
    
    public static MimeMultipart code1000(String sender, String receiver, String subject, String filename, String messageid, String mic) {
        MimeBodyPart mbp = new MimeBodyPart();
        MimeMultipart mp = new MimeMultipart();
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_DATE);
        String z = """
                The message <%s> sent to <%s>
                on %s with Subject <%s> has been received,
                the payload was successfully decrypted and its integrity was verified.
                In addition, the sender of the message, <%s> was authenticated
                as the originator of the message.
                
                There is no guarantee however that the payload was syntactically
                correct, or was received by any applicable back-end processes.
                """.formatted(filename, receiver, now, subject, sender);
        try {
           // mbp.setText(z);
           MimeMultipart mpInner = bundleit(z, receiver, messageid, mic, "processed");
           ContentType ct = new ContentType(mpInner.getContentType());
           String boundary = ct.getParameter("boundary");
            mbp.setContent(mpInner);
            mbp.setHeader("Content-Type", "multipart/report; report-type=disposition-notification; boundary=" + "\"" + boundary + "\"");
            mp.addBodyPart(mbp);
            
        } catch (MessagingException ex) {
            bslog(ex);
        }
        
        return mp;
    }
    
    public static MimeMultipart code2000(String sender, String receiver, String subject, String filename, String messageid, String mic) {
        MimeBodyPart mbp = new MimeBodyPart();
        MimeMultipart mp = new MimeMultipart();
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_DATE);
        String z = """
                The message <%s> sent to <%s>
                on %s with Subject <%s> was not signed.
                The message was transmitted by <%s>.
                """.formatted(filename, receiver, now, subject, sender);
        try {
           // mbp.setText(z);
           MimeMultipart mpInner = bundleit(z, receiver, messageid, mic, "failed");
           ContentType ct = new ContentType(mpInner.getContentType());
           String boundary = ct.getParameter("boundary");
            mbp.setContent(mpInner);
            mbp.setHeader("Content-Type", "multipart/report; report-type=disposition-notification; boundary=" + "\"" + boundary + "\"");
            mp.addBodyPart(mbp);
            
        } catch (MessagingException ex) {
            bslog(ex);
        }
        
        return mp;
    }
    
    
    public static mdn createMDN(String code, String[] e, ArrayList<String> headers) throws IOException, MessagingException {
        mdn x = null;
        MimeBodyPart mbp = new MimeBodyPart();
        String z;
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_DATE);
        
        switch (code) {
            case "1000" :
            mbp.setContent(code1000(e[0], e[1], e[2], e[3], e[4], e[5]));
            break;        
        default:
            z = """
                The message <unknown> sent to <unknown>
                on %s with Subject <unknown> has been rejected.
                """.formatted( now);
        }        
        
        if (mbp != null) {
            x = new mdn(HttpServletResponse.SC_OK, null, new String(mbp.getInputStream().readAllBytes()));
        } else {
            x = new mdn(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null, "problem creating MIME structure for MDN");
        }
        
        return x; 
    }
    
    public record mdn(int status, HashMap<String, String> headers, String message) {
        public mdn(int i, HashMap<String, String> hm) {
            this(i, hm, ""); 
        }
    }
    
    
}
