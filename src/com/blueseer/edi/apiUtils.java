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
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.getPKSStoreFileName;
import static com.blueseer.adm.admData.getPksMstr;
import com.blueseer.adm.admData.pks_mstr;
import static com.blueseer.edi.APIMaint.apidm;
import static com.blueseer.edi.AS2Maint.certs;
import com.blueseer.edi.ediData.api_det;
import com.blueseer.edi.ediData.api_mstr;
import com.blueseer.edi.ediData.as2_mstr;
import com.blueseer.edi.ediData.edi_ctrl;
import static com.blueseer.edi.ediData.getAPIDMeta;
import static com.blueseer.edi.ediData.getAPIDet;
import static com.blueseer.edi.ediData.getAPIMstr;
import static com.blueseer.edi.ediData.getAS2Mstr;
import static com.blueseer.edi.ediData.getEDICtrl;
import static com.blueseer.edi.ediData.getKeyStoreByUser;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.BlueSeerUtils.bsr;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.getSystemSignKey;
import static com.blueseer.utl.EDData.getSystemSignKeyAlt;
import static com.blueseer.utl.EDData.updateAS2LogMDNFile;
import static com.blueseer.utl.EDData.writeAS2Log;
import static com.blueseer.utl.EDData.writeAS2LogDetail;
import com.blueseer.utl.OVData;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.crypto.BadPaddingException;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
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
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMEToolkit;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import static org.bouncycastle.openpgp.PGPUtil.getDecoderStream;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

/**
 *
 * @author terryva
 */
public class apiUtils {
    
     private static Set<String> cachedFingerprints = ConcurrentHashMap.newKeySet();
    
    public static String[] runAPICall(api_mstr api, api_det apid, Path destinationpath, Path sourcepath) {
        String[] r = new String[]{"0",""};
       
        String verb = apid.apid_verb();
        String urlstring = setURL(api, apid);
        HttpURLConnection conn = null;
        
        if (api.m()[0].equals("1") || api.api_id().isBlank()) {
           r[0] = "1";
           r[1] = "unknown api_mstr id";
           return r;
        }
        
        if (apid.m()[0].equals("1") || apid.apid_id().isBlank()) {
           r[0] = "1";
           r[1] = "unknown api_det id or api_det method";
           return r;
        }
        
        if (api.api_url().isBlank() || api.api_protocol().isBlank()) {
           r[0] = "1";
           r[1] = "api url and/or protocol is blank";
           return r;
        }
        
        
            try {
              
                URL url = new URL(urlstring);
               // System.out.println("API: " + url.toURI());
                if (destinationpath == null) {
                    destinationpath = FileSystems.getDefault().getPath(apid.apid_destination());
                }
                if (sourcepath == null) {
                    sourcepath = FileSystems.getDefault().getPath(apid.apid_source());
                }
             
                
                
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", api.api_contenttype());
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            if (api.api_auth().equals("BASIC AUTH")) {
               if (! api.api_user().isBlank() && api.api_pass().length() > 0) {
                String userCredentials = new String(api.api_user() + ":" + api.api_pass());
                String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
                conn.setRequestProperty("Authorization", basicAuth);
                } 
            }
            
            StringBuilder requestHeaders = new StringBuilder();
            
            
            if (api.api_class().equals("REST")) {
                conn.setRequestMethod(verb);
                if (verb.equals("POST") || verb.equals("PUT")) {
                conn.setDoOutput(true);
                }
            }
            
            if (! api.api_class().equals("PARAM")) {
                ArrayList<ediData.apid_meta> headertags = getAPIDMeta(api.api_id());
                for (ediData.apid_meta am : headertags) {
                        if (am.apidm_method().equals(apid.apid_method())) {
                       // System.out.println(am.apidm_key() + "=" + am.apidm_value());
                        conn.setRequestProperty(am.apidm_key(),am.apidm_value());
                        }
                }
            }
            
            // let's see what request headers look like
            // call getRequestProperties before opening connection...otherwise generates exception already connected
            Map<String, List<String>> headers = conn.getRequestProperties();
            for (Map.Entry<String, List<String>> z : headers.entrySet()) {
            requestHeaders.append(z.getKey() + " : " + String.join(";", z.getValue()) + "\n");
            }
                
                
            // if posting data...add file
            // calling this opens the connection
            if (api.api_class().equals("REST") && (verb.equals("POST") || verb.equals("PUT"))) {
                if (Files.exists(sourcepath)) {
                    DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                    dos.write(Files.readAllBytes(sourcepath));
                    dos.flush();
                    dos.close();
                }
            }

                BufferedReader br = null;
		if (conn.getResponseCode() != 200) {
                        r[0] = "1";
                        r[1] = "api call to id: " + api.api_id() + " with urlstring: " + urlstring + " response->" + conn.getResponseCode() + ": " + conn.getResponseMessage();
		} else {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                }

                BufferedWriter outputfile = null;
                outputfile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationpath.toFile())));
		String output;
                if (br != null) {
                    while ((output = br.readLine()) != null) {
                             outputfile.write(output);
                    }
                    br.close();
                }
                outputfile.close(); 
                if (r[0].equals("0")) {
                r[1] = "api call to id: " + api.api_id() + " with urlstring: " + urlstring;
                }
                
                } catch (MalformedURLException e) {
                r[0] = "1";    
                r[1] = ("MalformedURLException: " + urlstring + "\n" + e + "\n");
                } catch (UnknownHostException ex) {
                    r[0] = "1";   
                    r[1] = ("UnknownHostException: " + urlstring + "\n" + ex + "\n");
                } catch (IOException ex) {
                    r[0] = "1";   
                    r[1] = ("IOException: " + urlstring + "\n" + ex + "\n");
                } catch (Exception ex) {
                    r[0] = "1";   
                    r[1] = ("Exception: " + urlstring + "\n" + ex + "\n");
                } finally {
                   if (conn != null) {
                    conn.disconnect();
                   }
                }
        
        return r;
    }
    
    public static String runAPIPost(String key, String method, String urlstring, String showReqHeader, String showRspHeader) {
        StringBuilder sb = new StringBuilder();
        StringBuilder responseHeaders = new StringBuilder();
        StringBuilder requestHeaders = new StringBuilder();
        api_mstr api = getAPIMstr(new String[]{key});
        api_det apid = getAPIDet(key, method);
        
        int k = 0;
        String verb = apid.apid_verb();
        String value = apid.apid_value();
        HttpURLConnection conn = null;
        
        if (api.m()[0].equals("1") || api.api_id().isBlank()) {
           return "unknown api_mstr id";
        }
        
        if (apid.m()[0].equals("1") || apid.apid_id().isBlank()) {
           return "unknown api_det id or api_det method";
        }
        
        if (api.api_url().isBlank() || api.api_protocol().isBlank()) {
           return "api url and/or protocol is blank";
        }
        
        
            try {
                
                
                URL url = new URL(urlstring);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", api.api_contenttype());
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);  

                BufferedReader br = null;
                
                if (api.api_auth().equals("BASIC AUTH")) {
                    if (! api.api_user().isBlank() && api.api_pass().length() > 0) {
                     String userCredentials = new String(api.api_user() + ":" + api.api_pass());
                     String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
                     conn.setRequestProperty("Authorization", basicAuth);
                     } 
                }
                
                if (api.api_class().equals("REST")) {
                    conn.setRequestMethod(verb);
                    if (verb.equals("POST") || verb.equals("PUT")) {
                    conn.setDoOutput(true);
                    }
                }
            
                if (! api.api_class().equals("PARAM")) {
                    ArrayList<ediData.apid_meta> headertags = getAPIDMeta(api.api_id());
                    for (ediData.apid_meta am : headertags) {
                            if (am.apidm_method().equals(method)) {
                           // System.out.println(am.apidm_key() + "=" + am.apidm_value());
                            conn.setRequestProperty(am.apidm_key(),am.apidm_value());
                            }
                    }
                }
                
                // let's see what request headers look like
            // call getRequestProperties before conn.getOutputStream()...otherwise generates exception already connected
            Map<String, List<String>> headers = conn.getRequestProperties();
            for (Map.Entry<String, List<String>> z : headers.entrySet()) {
            requestHeaders.append(z.getKey() + " : " + String.join(";", z.getValue()) + "\n");
            }
            
            // if posting data...add file
                // calling this opens the connection
                if (api.api_class().equals("REST") && (verb.equals("POST") || verb.equals("PUT"))) {
                    if (! apid.apid_source().isBlank()) {
                        DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                        Path sourcepath = FileSystems.getDefault().getPath(apid.apid_source());
                        dos.write(Files.readAllBytes(sourcepath));
                        dos.flush();
                        dos.close();
                    }
                }
                
		if (conn.getResponseCode() != 200) {
                        sb.append("api post to id: ")
                                .append(api.api_id())
                                .append(" with urlstring: ")
                                .append(urlstring)
                                .append(" response->")
                                .append(conn.getResponseCode())
                                .append(": ")
                                .append(conn.getResponseMessage());
		} else {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    // to get output headers 
                    conn.getHeaderFields().entrySet().stream()
                    .filter(entry -> entry.getKey() != null)
                    .forEach(entry -> {
                          responseHeaders.append(entry.getKey()).append(": ");
                          List headerValues = entry.getValue();
                          Iterator it = headerValues.iterator();
                          if (it.hasNext()) {
                              responseHeaders.append(it.next());
                              while (it.hasNext()) {
                                  responseHeaders.append(", ").append(it.next());
                              }
                          }
                          responseHeaders.append("\n");
                    });
                }

                if (Boolean.valueOf(showReqHeader)) {
                  sb.append("Request Headers: " + "\n");
                  sb.append(requestHeaders.toString());  
                }
                
                if (Boolean.valueOf(showRspHeader)) {
                  sb.append("Response Headers: " + "\n");
                  sb.append(responseHeaders.toString());  
                }
                
		String output = "";
                if (br != null) {
                    while ((output = br.readLine()) != null) {
                             sb.append(output);
                    }
                    br.close();
                }
              
                
                } catch (MalformedURLException e) {
                return "MalformedURLException: " + urlstring + "\n" + e + "\n";
                } catch (UnknownHostException ex) {
                    return "UnknownHostException: " + urlstring + "\n" + ex + "\n";
                } catch (IOException ex) {
                    return "IOException: " + urlstring + "\n" + ex + "\n";
                } catch (Exception ex) {
                    return "Exception: " + urlstring + "\n" + ex + "\n";
                } finally {
                   if (conn != null) {
                    conn.disconnect();
                   }
                }
        
        return sb.toString();
    }
    
    public static String setURL(api_mstr api, api_det apid) {
        String methodpath = apid.apid_value();
        String port;
        String urlstring;
        if (apid.apid_value().isBlank() && api.api_class().equals("PARAM")) {
                    ArrayList<String[]> list = apidm.get(apid.apid_method());
                    if (list != null) {
                        for (String[] s : list) {
                            if (s[2].equals("NO")) {  // param type KV
                             methodpath = methodpath + s[0] + "=" + s[1] + "&";
                            }
                        }
                        if (methodpath.endsWith("&")) {
                            methodpath = methodpath.substring(0, methodpath.length() - 1);
                        }
                    }
                }
                
                if (api.api_auth().equals("APIKEY")) {
                    if (! api.api_key().isBlank()) {
                    methodpath = methodpath + "&" + api.api_keylabel() + "=" + api.api_key();   
                    }
                }

                if (! methodpath.isBlank() && api.api_class().equals("PARAM")) {
                methodpath = "?" + methodpath;
                }
                
                if (api.api_port().isBlank()) {  
                   port = ""; 
                } else {
                   port = ":" + api.api_port();
                }
                
                urlstring = api.api_protocol() + "://" + api.api_url() + port + api.api_path() + methodpath;
        
        return urlstring;
    }
    
    public static PrivateKey getPrivateKey(String user)  {
        PrivateKey key = null;
        FileInputStream fis = null;
        try {
            String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass, standard
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
    
    public static PrivateKey readPrivateKeyFromFile(String file) throws Exception {
       byte[] privKeyByteArray = Files.readAllBytes(new File(file).toPath());

    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKeyByteArray);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

    PrivateKey key = keyFactory.generatePrivate(keySpec);
    return key;
    }
       
    
    // redo or scrap
    public static PGPPrivateKey getPGPPrivateKeyFromKeyStore(String user)  {
        PrivateKey key = null;
        PGPPrivateKey pgpkey = null;
        FileInputStream fis = null;
        try {
            String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass, standard
            k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
            k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
            char[] keyPassword = k[4].toCharArray();
            KeyStore keystore = KeyStore.getInstance("PKCS12");
             fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
            keystore.load(fis, k[2].toCharArray());
            key = (PrivateKey) keystore.getKey(k[3], keyPassword);
            
            // for pgp key
            X509Certificate cert = (X509Certificate) keystore.getCertificate(k[3]); // need to get public key
            JcaPGPKeyConverter c = new JcaPGPKeyConverter(); // convert from 509 to PGP
            PGPPublicKey pgppk = c.getPGPPublicKey(PGPPublicKey.RSA_GENERAL, cert.getPublicKey(), new Date());
            pgpkey = c.getPGPPrivateKey(pgppk, key);
            
            
            
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
        } catch (PGPException ex) {
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
        return pgpkey;
    }
        
    
    public static PublicKey getPublicKey(String user)  {
        X509Certificate cert = null;
        FileInputStream fis = null;
        pks_mstr pks = admData.getPksMstr(new String[]{user});
        try {
        String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass
        k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
        k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
        KeyStore keystore = KeyStore.getInstance("PKCS12");
         fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
        keystore.load(fis, k[2].toCharArray());
        cert = (X509Certificate) keystore.getCertificate(pks.pks_user());
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
        
        return cert.getPublicKey();
    }
    
    public static String[] getCertInfo(String user)  {
        X509Certificate cert = null;
        FileInputStream fis = null;
        String[] r = null;
        
        pks_mstr pks = admData.getPksMstr(new String[]{user});
        try {
        String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass
        k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
        k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        
        if (pks.pks_type().equals("keypair")) {
        fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
        keystore.load(fis, k[2].toCharArray());
        cert = (X509Certificate) keystore.getCertificate(pks.pks_user());
        }
        
        if (pks.pks_type().equals("publickey")) {
        Path certfilepath = FileSystems.getDefault().getPath(pks.pks_file());
                if (Files.exists(certfilepath)) {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
                    try (FileInputStream fiscert = new FileInputStream(certfilepath.toFile())) {
                        cert = (X509Certificate) certFactory.generateCertificate(fiscert);
                    } catch (IOException ex) {
                        bslog(ex);
                    }
                }
        }
        
        
        if (cert != null) {
            r = new String[]{cert.getSubjectX500Principal().getName(),
                cert.getIssuerX500Principal().getName(), 
                cert.getSigAlgName(), 
                cert.getSerialNumber().toString(),
                cert.getNotBefore().toString(),
                cert.getNotAfter().toString()};
        }
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
        } catch (NoSuchProviderException ex) {
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
        
        return r;
    }
    
    public static String getCertInfo_Str(String user)  {
        X509Certificate cert = null;
        FileInputStream fis = null;
        String r = null;
        
        pks_mstr pks = admData.getPksMstr(new String[]{user});
        try {
        String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass
        k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
        k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        
        if (pks.pks_type().equals("keypair")) {
        fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
        keystore.load(fis, k[2].toCharArray());
        cert = (X509Certificate) keystore.getCertificate(pks.pks_user());
        }
        
        if (pks.pks_type().equals("publickey")) {
        Path certfilepath = FileSystems.getDefault().getPath(pks.pks_file());
                if (Files.exists(certfilepath)) {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
                    try (FileInputStream fiscert = new FileInputStream(certfilepath.toFile())) {
                        cert = (X509Certificate) certFactory.generateCertificate(fiscert);
                    } catch (IOException ex) {
                        bslog(ex);
                    }
                }
        }
        
        
        if (cert != null) {
            r = cert.getSubjectX500Principal().getName() + "|" +
                cert.getIssuerX500Principal().getName()+ "|" + 
                cert.getSigAlgName()+ "|" +
                cert.getSerialNumber().toString()+ "|" +
                cert.getNotBefore().toString()+ "|" +
                cert.getNotAfter().toString();
        }
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
        } catch (NoSuchProviderException ex) {
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
        
        return r;
    }
    
    
    // redo or scrap
    public static PGPPublicKey getPGPPublicKeyFromKeyStore(String user)  {
       
        PGPPublicKey pgpkey = null;
        FileInputStream fis = null;
        try {
            String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass, standard
            k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
            k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
            char[] keyPassword = k[4].toCharArray();
            KeyStore keystore = KeyStore.getInstance("PKCS12");
             fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
            keystore.load(fis, k[2].toCharArray());
            
            // for pgp key
            X509Certificate cert = (X509Certificate) keystore.getCertificate(k[3]); // need to get public key
            JcaPGPKeyConverter c = new JcaPGPKeyConverter(); // convert from 509 to PGP
            pgpkey = c.getPGPPublicKey(PGPPublicKey.RSA_GENERAL, cert.getPublicKey(), new Date());
            
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
        } catch (PGPException ex) {
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
        return pgpkey;
    }
    
    // redo or scrap 
    public static String getAsciiDumpPGPKey(String id, String type) throws Exception {
        pks_mstr pks = getPksMstr(new String[]{id});
        String pass = bsmf.MainFrame.PassWord("1", pks.pks_pass().toCharArray());
        String s = "";
        if (type.equals("public")) {
        String storename = getPKSStoreFileName(pks.pks_parent()) + ".pkr";
        Path keyfilepath = FileSystems.getDefault().getPath(storename);
        List<PGPPublicKey> list = getPGPPublicKeyForExport(keyfilepath, id);
        PGPPublicKeyRing pkr = new PGPPublicKeyRing(list);
        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        OutputStream out = new ArmoredOutputStream(encOut);
        pkr.encode(out);
        out.close();
        s = new String(encOut.toByteArray(), Charset.defaultCharset());
        
        } else {
        String storename = getPKSStoreFileName(pks.pks_parent()) + ".skr";
        Path keyfilepath = FileSystems.getDefault().getPath(storename);
        List<PGPSecretKey> list = getPGPPrivateKeyForExport(keyfilepath, id);
        PGPSecretKeyRing pkr = new PGPSecretKeyRing(list);
        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        OutputStream out = new ArmoredOutputStream(encOut);
        pkr.encode(out);
        out.close();
        s = new String(encOut.toByteArray(), Charset.defaultCharset());  
        }
        
        
        return s;
        
    }
    
    // redo or scrap    
    public static void exportPGPKeyFiles(String id, String format) throws Exception {
        String data = getAsciiDumpPGPKey(id, format);
        FileDialog fDialog;
                fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
                format = (format.equals("private")) ? ".skr" : ".pkr";
                fDialog.setFile(id + format);
                fDialog.setVisible(true);
                String path = fDialog.getDirectory() + fDialog.getFile();
                File f = new File(path);
                BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(f));
            output.write(data);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
              try {  
                  output.close();
              } catch (IOException ex) {
                  ex.printStackTrace();
              }
        }
    }
    
    public static String genereatePGPKeyPair(String id, String pass, String keystoreID) throws Exception {
        Path filepath_pkr = FileSystems.getDefault().getPath("edi/certs/bsPGPRing.pkr");;
        Path filepath_skr = FileSystems.getDefault().getPath("edi/certs/bsPGPRing.skr");;
        if (! keystoreID.isBlank()) {
            String t = getPKSStoreFileName(keystoreID);
            if (! t.isBlank()) {
               filepath_pkr = FileSystems.getDefault().getPath(t + ".pkr"); 
               filepath_skr = FileSystems.getDefault().getPath(t + ".skr");
            }            
        } 
        if (! filepath_pkr.getParent().toFile().isDirectory()) {
            return "0"; // bail out
        }
        // return keyID as String if successfull....else blank string
        PGPKeyRingGenerator krgen = generateKeyRingGenerator(id, pass.toCharArray());
        
        // pub
        PGPPublicKeyRing pkr = krgen.generatePublicKeyRing();
        try (FileOutputStream fos = new FileOutputStream(filepath_pkr.toFile(), true)) {
            BufferedOutputStream pubout = new BufferedOutputStream(fos);
            pkr.encode(pubout);
            pubout.close();  
        } catch (IOException ex) {
            bslog(ex);
            return "";
        }  
        
        
        // prv
        PGPSecretKeyRing skr = krgen.generateSecretKeyRing();
        try (FileOutputStream fos = new FileOutputStream(filepath_skr.toFile(), true)) {
            BufferedOutputStream pubout = new BufferedOutputStream(fos);
            skr.encode(pubout);
            pubout.close();  
        } catch (IOException ex) {
            bslog(ex);
            return "";
        } 
        /*
        BufferedOutputStream pubout = new BufferedOutputStream(new FileOutputStream("c:\\junk\\temp6\\dummy.pkr"));
        pkr.encode(pubout);
        pubout.close();        
         // Generate private key, dump to file.
        PGPSecretKeyRing skr = krgen.generateSecretKeyRing();
        BufferedOutputStream secout = new BufferedOutputStream
            (new FileOutputStream("c:\\junk\\temp6\\dummy.skr"));
        skr.encode(secout);
        secout.close();
                */
        
        return Long.toHexString(skr.getSecretKey().getKeyID());
    }
    
    public final static PGPKeyRingGenerator generateKeyRingGenerator(String id, char[] pass) throws Exception { 
        return generateKeyRingGenerator(id, pass, 0xc0); 
    }
    
    public final static PGPKeyRingGenerator generateKeyRingGenerator(String id, char[] pass, int s2kcount) throws Exception {
        // This object generates individual key-pairs.
        RSAKeyPairGenerator  kpg = new RSAKeyPairGenerator();

        // Boilerplate RSA parameters, no need to change anything
        // except for the RSA key-size (2048). You can use whatever
        // key-size makes sense for you -- 4096, etc.
        kpg.init(new RSAKeyGenerationParameters
             (BigInteger.valueOf(0x10001),
              new SecureRandom(), 2048, 12));

        // First create the master (signing) key with the generator.
        PGPKeyPair rsakp_sign =
            new BcPGPKeyPair
            (PGPPublicKey.RSA_SIGN, kpg.generateKeyPair(), new Date());
        // Then an encryption subkey.
        PGPKeyPair rsakp_enc =
            new BcPGPKeyPair
            (PGPPublicKey.RSA_ENCRYPT, kpg.generateKeyPair(), new Date());

        // Add a self-signature on the id
        PGPSignatureSubpacketGenerator signhashgen =
            new PGPSignatureSubpacketGenerator();
        
        // Add signed metadata on the signature.
        // 1) Declare its purpose
        signhashgen.setKeyFlags
            (false, KeyFlags.SIGN_DATA|KeyFlags.CERTIFY_OTHER);
        // 2) Set preferences for secondary crypto algorithms to use
        //    when sending messages to this key.
        signhashgen.setPreferredSymmetricAlgorithms
            (false, new int[] {
                SymmetricKeyAlgorithmTags.AES_256,
                SymmetricKeyAlgorithmTags.AES_192,
                SymmetricKeyAlgorithmTags.AES_128
            });
        signhashgen.setPreferredHashAlgorithms
            (false, new int[] {
                HashAlgorithmTags.SHA256,
                HashAlgorithmTags.SHA1,
                HashAlgorithmTags.SHA384,
                HashAlgorithmTags.SHA512,
                HashAlgorithmTags.SHA224,
            });
        // 3) Request senders add additional checksums to the
        //    message (useful when verifying unsigned messages.)
        signhashgen.setFeature
            (false, Features.FEATURE_MODIFICATION_DETECTION);

        // Create a signature on the encryption subkey.
        PGPSignatureSubpacketGenerator enchashgen =
            new PGPSignatureSubpacketGenerator();
        // Add metadata to declare its purpose
        enchashgen.setKeyFlags
            (false, KeyFlags.ENCRYPT_COMMS|KeyFlags.ENCRYPT_STORAGE);

        // Objects used to encrypt the secret key.
        PGPDigestCalculator sha1Calc =
            new BcPGPDigestCalculatorProvider()
            .get(HashAlgorithmTags.SHA1);
        PGPDigestCalculator sha256Calc =
            new BcPGPDigestCalculatorProvider()
            .get(HashAlgorithmTags.SHA256);

        // bcpg 1.48 exposes this API that includes s2kcount. Earlier
        // versions use a default of 0x60.
        PBESecretKeyEncryptor pske =
            (new BcPBESecretKeyEncryptorBuilder
             (PGPEncryptedData.AES_256, sha256Calc, s2kcount))
            .build(pass);
        
        // Finally, create the keyring itself. The constructor
        // takes parameters that allow it to generate the self
        // signature.
        PGPKeyRingGenerator keyRingGen =
            new PGPKeyRingGenerator
            (PGPSignature.POSITIVE_CERTIFICATION, rsakp_sign,
             id, sha1Calc, signhashgen.generate(), null,
             new BcPGPContentSignerBuilder
             (rsakp_sign.getPublicKey().getAlgorithm(),
              HashAlgorithmTags.SHA1),
             pske);

        // Add our encryption subkey, together with its signature.
        keyRingGen.addSubKey
            (rsakp_enc, enchashgen.generate(), null);
        return keyRingGen;
    }
   
        
        
    public static String getPublicKeyAsOPENSSH(String user)  {
        String s = "";
        X509Certificate cert = null;
        FileInputStream fis = null;
        pks_mstr pks = admData.getPksMstr(new String[]{user});
        try {
        String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass
        k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
        k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
        KeyStore keystore = KeyStore.getInstance("PKCS12");
         fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
        keystore.load(fis, k[2].toCharArray());
        cert = (X509Certificate) keystore.getCertificate(pks.pks_user());
        fis.close();
        
        PublicKey pubkey = cert.getPublicKey();
        AsymmetricKeyParameter bpuv = PublicKeyFactory.createKey(pubkey.getEncoded());
        byte[] opuv = OpenSSHPublicKeyUtil.encodePublicKey(bpuv);
        
        // s = new String(Base64.encode(opuv));
        
        StringWriter writer = new StringWriter();
        PemWriter pemWriter = new PemWriter(writer);
        pemWriter.writeObject(new PemObject("SSH2 PUBLIC KEY", opuv));
        pemWriter.flush();
        pemWriter.close();
        s = writer.toString();
        writer.close();
        
        
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
        } 
        
        return s;  
    }
    
    
    public static String generateSSHCert(String certype) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Security.addProvider(new BouncyCastleProvider());
        String newstring = "";
        KeyPairGenerator generator;
         generator = KeyPairGenerator.getInstance("ED25519","BC");
       // generator = KeyPairGenerator.getInstance("RSA","BC");
       // generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        AsymmetricKeyParameter bprv = PrivateKeyFactory.createKey(pair.getPrivate().getEncoded());
        AsymmetricKeyParameter bpuv = PublicKeyFactory.createKey(pair.getPublic().getEncoded());
        
        
        byte[] oprv = OpenSSHPrivateKeyUtil.encodePrivateKey(bprv);
        byte[] opuv = OpenSSHPublicKeyUtil.encodePublicKey(bpuv);
        /*
        PemWriter w = new PemWriter(new OutputStreamWriter(System.out));
        w.writeObject(new PemObject("OPENSSH PRIVATE KEY", oprv)); 
        w.close();
        */
        
        
        if (certype.equals("public")) {
        newstring = new String(Base64.encode(opuv));
        } else {
        newstring = new String(Base64.encode(oprv));    
        }
        
        StringWriter writer = new StringWriter();
        PemWriter pemWriter = new PemWriter(writer);
        pemWriter.writeObject(new PemObject("OPENSSH PUBLIC KEY", opuv));
        pemWriter.flush();
        pemWriter.close();
        newstring = writer.toString();
        writer.close();
        
        
        return newstring;
    }
    
    public static X509Certificate getPublicKeyAsCert(String user)  {
        X509Certificate cert = null;
        FileInputStream fis = null;
        pks_mstr pks = admData.getPksMstr(new String[]{user});
        try {
            // File type
            Security.addProvider(new BouncyCastleProvider());
            
            if (pks.pks_type().equals("publickey") ) {
                Path certfilepath = FileSystems.getDefault().getPath(pks.pks_file());
                if (! Files.exists(certfilepath)) {
                     // throw new RuntimeException("bad path to cert file: " + certfile);
                     return cert; // return null
                }
               // System.out.println("here->" + certfilepath.toString());
                
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
                try (FileInputStream fiscert = new FileInputStream(certfilepath.toFile())) {
                    cert = (X509Certificate) certFactory.generateCertificate(fiscert);
                    return cert;
                } catch (IOException ex) {
                    bslog(ex);
                }
            }
            
            if (pks.pks_type().equals("keypair") ) {
            String[] k = getKeyStoreByUser(user); // store, storeuser, storepass, user, pass
            k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
            k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
            KeyStore keystore = KeyStore.getInstance("PKCS12");
             fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
            keystore.load(fis, k[2].toCharArray());
            cert = (X509Certificate) keystore.getCertificate(pks.pks_user());
            fis.close();
            return cert;
            }
            
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
        } catch (NoSuchProviderException ex) {
            bslog(ex);
        } 
        
        return cert;
    }
    
    public static String getPublicKeyAsPEM(String key)  {
        X509Certificate cert = null;
        String s = "";
        FileInputStream fis = null;
        pks_mstr pks = admData.getPksMstr(new String[]{key});
        try {
            // File type
            if (pks.pks_type().equals("publickey") ) {
                Path certfilepath = FileSystems.getDefault().getPath(pks.pks_file());
                if (! Files.exists(certfilepath)) {
                     // throw new RuntimeException("bad path to cert file: " + certfile);
                     return ""; // return null
                }
               // System.out.println("here->" + certfilepath.toString());
                Security.addProvider(new BouncyCastleProvider());
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
                try (FileInputStream fiscert = new FileInputStream(certfilepath.toFile())) {
                    cert = (X509Certificate) certFactory.generateCertificate(fiscert);
                    PublicKey pubkey = cert.getPublicKey();
                    AsymmetricKeyParameter bpuv = PublicKeyFactory.createKey(pubkey.getEncoded());
                    byte[] opuv = OpenSSHPublicKeyUtil.encodePublicKey(bpuv);
                    StringWriter writer = new StringWriter();
                    PemWriter pemWriter = new PemWriter(writer);
                    pemWriter.writeObject(new PemObject("CERTIFICATE", cert.getEncoded()));
                    pemWriter.flush();
                    pemWriter.close();
                    s = writer.toString();
                    writer.close();
                    return s;
                } catch (IOException ex) {
                    bslog(ex);
                }
            }
            
            if (pks.pks_type().equals("keypair") ) {
            String[] k = getKeyStoreByUser(key); // store, storeuser, storepass, user, pass
            k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
            k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            fis = new FileInputStream(FileSystems.getDefault().getPath(k[0]).toString());
            try {
                keystore.load(fis, k[2].toCharArray());
                cert = (X509Certificate) keystore.getCertificate(pks.pks_user());
                fis.close();
            } catch (IOException ex) {
               bslog(ex);
               return "cannot access keystore";
            }
            
            StringWriter writer = new StringWriter();
            PemWriter pemWriter = new PemWriter(writer);
                try {
                    pemWriter.writeObject(new PemObject("CERTIFICATE", cert.getEncoded()));
                    pemWriter.flush();
                    pemWriter.close();
                    s = writer.toString();
                    writer.close();
                } catch (IOException ex) {
                    bslog(ex);
                   return "cannot write PemObject";
                }
            
            return s;
            }
            
            //System.out.println("here-->" + cert.getSerialNumber());
        } catch (KeyStoreException | FileNotFoundException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException ex) {
            bslog(ex);
        } 
        
        return s;
    }
    
        
    
    public static PrivateKey readPrivateKeyFromPem(File file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
    KeyFactory factory = KeyFactory.getInstance("RSA");
    try (FileReader keyReader = new FileReader(file, StandardCharsets.UTF_8);
      PemReader pemReader = new PemReader(keyReader)) {
        PemObject pemObject = pemReader.readPemObject();
        byte[] content = pemObject.getContent();
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
        return (PrivateKey) factory.generatePrivate(privKeySpec);
    }
    
}
    
    public static X509Certificate readPublicKeyFromPem(File file) throws IOException {
    try (FileReader keyReader = new FileReader(file, StandardCharsets.UTF_8)) {
        PEMParser pemParser = new PEMParser(keyReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
        return (X509Certificate) converter.getPublicKey(publicKeyInfo);
    }
}
    
    public static boolean createKeyStoreWithNewKeyPair(String alias, String userpass, String passphrase, String filename, String sigalgo, int strength, int years) {
        
        Security.addProvider(new BouncyCastleProvider());
        // --- generate a key pair (you did this already it seems)
        KeyPairGenerator rsaGen;
        try {
        rsaGen = KeyPairGenerator.getInstance("RSA", "BC");
        rsaGen.initialize(strength, new SecureRandom());
        
        final KeyPair pair = rsaGen.generateKeyPair();

        // --- create the self signed cert
        Certificate cert = createSelfSigned(sigalgo, pair.getPrivate(), pair.getPublic(), years);

        // --- create a new pkcs12 key store in memory
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);

        // --- create entry in PKCS12
        pkcs12.setKeyEntry(alias, pair.getPrivate(), userpass.toCharArray(), new Certificate[] {cert});

         // --- store PKCS#12 as file
        Path filepath = FileSystems.getDefault().getPath(filename);
        try (FileOutputStream p12 = new FileOutputStream(filepath.toFile())) {
            pkcs12.store(p12, passphrase.toCharArray());
        }

        } catch (NoSuchAlgorithmException ex) {
            bslog(ex);
            return false;
        } catch (OperatorCreationException ex) {
            bslog(ex);
            return false;
        } catch (CertIOException ex) {
            bslog(ex);
            return false;
        } catch (CertificateException ex) {
            bslog(ex);
            return false;
        } catch (KeyStoreException ex) {
            bslog(ex);
            return false;
        } catch (IOException ex) {
            bslog(ex);
            return false;
        } catch (NoSuchProviderException ex) {
            bslog(ex);
            return false;
        }
        
        return true;
    }
    
    public static boolean createKeyStore(String passphrase, String filename) {
       
        try {
        // --- create a new pkcs12 key store in memory
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);

         // --- store PKCS#12 as file
        Path filepath = FileSystems.getDefault().getPath(filename);
        
        // if store file already exists...then simply return (to prevent overwriting a .p12 that is has already been placed their via 'upload' or manual creation)
        if (Files.exists(filepath)) {
            return true;
        }
        
        try (FileOutputStream p12 = new FileOutputStream(filepath.toFile())) {
            pkcs12.store(p12, passphrase.toCharArray());
        }

        } catch (NoSuchAlgorithmException ex) {
            bslog(ex);
            return false;
        } catch (CertIOException ex) {
            bslog(ex);
            return false;
        } catch (CertificateException ex) {
            bslog(ex);
            return false;
        } catch (KeyStoreException ex) {
            bslog(ex);
            return false;
        } catch (IOException ex) {
            bslog(ex);
            return false;
        } 
        
        return true;
    }
    
    
    public static boolean createNewKeyPair(String standard, String alias, String userpass, String passphrase, String filename, String algo, String sigalgo, String strength, String years) {
        
        Security.addProvider(new BouncyCastleProvider());
        // --- generate a key pair (you did this already it seems)
        KeyPairGenerator rsaGen;
        try {
        rsaGen = KeyPairGenerator.getInstance(algo, "BC");
        rsaGen.initialize(Integer.valueOf(strength), new SecureRandom());
        
        final KeyPair pair = rsaGen.generateKeyPair();
        
        Certificate cert = null;
        
        // if openPGP then
        if (standard.equals("openPGP")) { // using a converter to 509 type keys so that we can store it in keystore
        PGPKeyPair elgKp = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, pair, new Date());
        
        JcaPGPKeyConverter c = new JcaPGPKeyConverter();
        PrivateKey prvkey = c.getPrivateKey(elgKp.getPrivateKey()); 
        PublicKey pubkey = c.getPublicKey(elgKp.getPublicKey());
            cert = createSelfSigned(sigalgo, prvkey, pubkey, Integer.valueOf(years));
        } else {
            cert = createSelfSigned(sigalgo, pair.getPrivate(), pair.getPublic(), Integer.valueOf(years));
        }
        // --- create the self signed cert
        
       

        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(FileSystems.getDefault().getPath(filename).toString()))  {
            keystore.load(fis, passphrase.toCharArray());
            keystore.setKeyEntry(alias, pair.getPrivate(), userpass.toCharArray(), new Certificate[] {cert});
        }
        
         // --- now save back to .p12 file
        Path filepath = FileSystems.getDefault().getPath(filename);
        try (FileOutputStream p12 = new FileOutputStream(filepath.toFile())) {
            keystore.store(p12, passphrase.toCharArray());
        }
      
        } catch (NoSuchAlgorithmException ex) {
            bslog(ex);
            return false;
        } catch (OperatorCreationException ex) {
            bslog(ex);
            return false;
        } catch (CertIOException ex) {
            bslog(ex);
            return false;
        } catch (CertificateException ex) {
            bslog(ex);
            return false;
        } catch (KeyStoreException ex) {
            bslog(ex);
            return false;
        } catch (IOException ex) {
            bslog(ex);
            return false;
        } catch (NoSuchProviderException ex) {
            bslog(ex);
            return false;
        } catch (PGPException ex) {
            bslog(ex);
            return false;
        }
        
        return true;
    }
    
    
    public static X509Certificate createSelfSigned(String sigalgo, PrivateKey prv, PublicKey pub, int years) throws OperatorCreationException, CertIOException, CertificateException {
        String x = "";
        
        if (sigalgo.equals("SHA-256")) {
            x = "SHA256WithRSA";
        } else if (sigalgo.equals("SHA-1")) {
            x = "SHA1WithRSA";
        } else if (sigalgo.equals("MD-5")) {
            x = "MD5WithRSA";    
        } else {
           x = "SHA256WithRSA";  
        }
        
        KeyPairGenerator rsaGen;
        
        String[] siteinfo = admData.getSiteAddressInfo(OVData.getDefaultSite());
        X500Name dnName = new X500Name(" CN=BlueSeer Software, " +
"             E=services@blueseer.com, " +
"             OU=EDI, " +
"             O=BlueSeer Software," +
"             L=Dillon," +
"             ST=SC," +
"             C=US"); 
        // dnName = new JcaX509CertificateHolder((X509Certificate) caCert).getSubject();
        BigInteger certSerialNumber = new BigInteger(Long.toString(System.currentTimeMillis()));

        Date startDate = new Date(); // now
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, years);
        Date endDate = calendar.getTime();

        X500NameBuilder builder = new X500NameBuilder(RFC4519Style.INSTANCE);
        builder.addRDN(RFC4519Style.cn, "BlueSeer Software");
         builder.addRDN(PKCSObjectIdentifiers.pkcs_9_at_emailAddress, "services@blueseer.com");
         builder.addRDN(RFC4519Style.ou, "EDI");
         builder.addRDN(RFC4519Style.o, "BlueSeer Software");  // site ID
         builder.addRDN(RFC4519Style.l, "Dillon");  // site city
         builder.addRDN(RFC4519Style.st, "SC");  // site state
        builder.addRDN(RFC4519Style.c, "US");  // site country
        
        
        
       
        
        
        ContentSigner contentSigner = new JcaContentSignerBuilder(x).build(prv);
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber, startDate, endDate, builder.build(), pub);

        return new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));
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
    
    public static String hashdigestString(byte[] indata, String algo) {
        String x;
        
        MessageDigest messageDigest = null;
                    try {
                        messageDigest = MessageDigest.getInstance(algo);  // SHA-1, etc
                    } catch (NoSuchAlgorithmException ex) {
                        bslog(ex);
                    }
        byte[] hashedbytes = messageDigest.digest(indata);
        x = new String(hashedbytes);
        return x;
    }
    
     public static String calculateMIC(byte[] data, String digestAlgOID) throws GeneralSecurityException, MessagingException, IOException {
        if (data == null) {
            throw new GeneralSecurityException("calculateMIC: Data is null");
        }
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest messageDigest = MessageDigest.getInstance(digestAlgOID, "BC");
        DigestInputStream digestInputStream = new DigestInputStream(new ByteArrayInputStream(data), messageDigest);
        for (byte buf[] = new byte[4096]; digestInputStream.read(buf) >= 0;) {
        }
        byte mic[] = digestInputStream.getMessageDigest().digest();
        digestInputStream.close();
        String micString = new String(Base64.encode(mic));
        return (micString);
    }

    public static byte[] encryptData(byte[] data, X509Certificate encryptionCertificate, String algo) throws CertificateEncodingException, CMSException, IOException {
        ASN1ObjectIdentifier x;
        byte[] encryptedData = null;
        
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
        } else if (algo.equals("DES_EDE3_WRAP")) {
            x = CMSAlgorithm.DES_EDE3_WRAP;
        } else {
           x = CMSAlgorithm.DES_EDE3_CBC;  
        }
        
        if (data != null && encryptionCertificate != null) {
            CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();
            JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(encryptionCertificate);
            cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
            CMSTypedData msg = new CMSProcessableByteArray(data);
            OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(x).setProvider("BC").build();
            CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg,encryptor);
            encryptedData = cmsEnvelopedData.getEncoded();
        }
	return encryptedData;
	
}
    
    public static byte[] decryptData(byte[] encryptedData, PrivateKey decryptionKey)throws CMSException {
            byte[] decryptedData = null;
            if (encryptedData != null && decryptionKey != null) {
                CMSEnvelopedData envelopedData = new CMSEnvelopedData(encryptedData);
                Collection<RecipientInformation> recipients = envelopedData.getRecipientInfos().getRecipients();
                KeyTransRecipientInformation recipientInfo = (KeyTransRecipientInformation) recipients.iterator().next();
                JceKeyTransRecipient recipient = new JceKeyTransEnvelopedRecipient(decryptionKey);
                try {
                decryptedData = recipientInfo.getContent(recipient);
                } catch (CMSException ex) {
                bslog(ex);
                }
            }
            return decryptedData;
}
    
    public static PGPPrivateKey getPGPSecretKey(InputStream keyIn, long keyID, char[] pass)
	{
                PGPPrivateKey pgpkey = null;
		PGPSecretKeyRingCollection pgpSec;
        try {
                pgpSec = new PGPSecretKeyRingCollection(
                org.bouncycastle.openpgp.PGPUtil.getDecoderStream(keyIn),new BcKeyFingerprintCalculator());
                PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);
		if (pgpSecKey == null) {
			return null;
		}
		PBESecretKeyDecryptor a = new JcePBESecretKeyDecryptorBuilder(new JcaPGPDigestCalculatorProviderBuilder().setProvider("BC").build()).setProvider("BC").build(pass);
                pgpkey = pgpSecKey.extractPrivateKey(a);
	} catch (PGPException ex) {
            bslog(ex.getMessage());
        } catch (IOException ex) {
            bslog(ex.getMessage());
        }
                return pgpkey;
	}
    
    public static PGPPrivateKey getPGPPrivateKey(Path keyFilePath, String id) {
      PGPSecretKey key = null;
      PGPPrivateKey prvkey = null;
      InputStream in = null;
      char[] pass = null;
      
      pks_mstr pks = getPksMstr(new String[]{id});
      pass = bsmf.MainFrame.PassWord("1", pks.pks_pass().toCharArray()).toCharArray();
      Security.addProvider(new BouncyCastleProvider());
      
      try {
      if (keyFilePath != null) {
              in = new FileInputStream(keyFilePath.toFile());
      }
      in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
      PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in, new BcKeyFingerprintCalculator());
      
      Iterator<PGPSecretKeyRing> rIt = pgpSec.getKeyRings();

        while (key == null && rIt.hasNext()) {
            PGPSecretKeyRing kRing = rIt.next();
            
            Iterator<PGPSecretKey> kIt = kRing.getSecretKeys();
            ArrayList<String> al = new ArrayList<String>();
            while (key == null && kIt.hasNext()) {
                PGPSecretKey k = kIt.next();
                if (k.isMasterKey()) { // get list of users from master key
                    al.clear();
                    Iterator<String> users = k.getUserIDs();
                    while (users.hasNext()) {
                        al.add(users.next());
                    }
                }
               // System.out.println(Long.toHexString(k.getKeyID()) + "/" + k.isMasterKey() + "/" + k.isPrivateKeyEmpty() + "/" + al.size());
                if (! k.isMasterKey() && al.contains(pks.pks_user())) {
               // if (Long.toHexString(k.getKeyID()).equals(pks.pks_keyid().toLowerCase())) {
                    
                   PBESecretKeyDecryptor a = new JcePBESecretKeyDecryptorBuilder(new JcaPGPDigestCalculatorProviderBuilder().setProvider("BC").build()).setProvider("BC").build(pass);
                   prvkey = k.extractPrivateKey(a); 
                   return prvkey;
                   
                }
               
            }
        }
      
      } catch (FileNotFoundException ex) {
              bslog(ex);
      } catch (IOException ex) {
            bslog(ex);
        } catch (PGPException ex) {
            bslog(ex);
        }
      return prvkey;
    }
    
    public static List<PGPSecretKey> getPGPPrivateKeyForExport(Path keyFilePath, String id) {
      List<PGPSecretKey> list = new ArrayList<>();
      PGPSecretKey key = null;
      PGPPrivateKey prvkey = null;
      InputStream in = null;
      char[] pass = null;
      
      pks_mstr pks = getPksMstr(new String[]{id});
      pass = bsmf.MainFrame.PassWord("1", pks.pks_pass().toCharArray()).toCharArray();
      Security.addProvider(new BouncyCastleProvider());
      
      try {
      if (keyFilePath != null) {
              in = new FileInputStream(keyFilePath.toFile());
      }
      in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
      PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in, new BcKeyFingerprintCalculator());
      
      Iterator<PGPSecretKeyRing> rIt = pgpSec.getKeyRings();

        while (key == null && rIt.hasNext()) {
            PGPSecretKeyRing kRing = rIt.next();
            
            Iterator<PGPSecretKey> kIt = kRing.getSecretKeys();
            ArrayList<String> al = new ArrayList<String>();
            while (key == null && kIt.hasNext()) {
                PGPSecretKey k = kIt.next();
                if (k.isMasterKey()) { // get list of users from master key
                    al.clear();
                    Iterator<String> users = k.getUserIDs();
                    while (users.hasNext()) {
                        al.add(users.next());
                    }
                }
               // System.out.println(Long.toHexString(k.getKeyID()) + "/" + k.isMasterKey() + "/" + k.isPrivateKeyEmpty() + "/" + al.size());
                if (al.contains(pks.pks_user())) {
               // if (Long.toHexString(k.getKeyID()).equals(pks.pks_keyid().toLowerCase())) {
                    
                   //PBESecretKeyDecryptor a = new JcePBESecretKeyDecryptorBuilder(new JcaPGPDigestCalculatorProviderBuilder().setProvider("BC").build()).setProvider("BC").build(pass);
                  // prvkey = k.extractPrivateKey(a); 
                   list.add(k);
                   
                }
               
            }
        }
      
      } catch (FileNotFoundException ex) {
              bslog(ex);
      } catch (IOException ex) {
            bslog(ex);
        } catch (PGPException ex) {
            bslog(ex);
        }
      return list;
    }
    
    
    public static PGPPublicKey getPGPPublicKey(Path keyFilePath, String id) {
      PGPSecretKey key = null;
      InputStream in = null;
      char[] pass = null;
      
      pks_mstr pks = getPksMstr(new String[]{id});
      pass = bsmf.MainFrame.PassWord("1", pks.pks_pass().toCharArray()).toCharArray();
      Security.addProvider(new BouncyCastleProvider());
      
      try {
      if (keyFilePath != null) {
              in = new FileInputStream(keyFilePath.toFile());
      } 
      in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
      PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in, new BcKeyFingerprintCalculator());
     
      Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();

        while (key == null && rIt.hasNext()) {
            PGPPublicKeyRing kRing = rIt.next();
             
            Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
            ArrayList<String> al = new ArrayList<String>();
            while (key == null && kIt.hasNext()) {
                PGPPublicKey k = kIt.next();
                if (k.isMasterKey()) { // get list of users from master key
                    al.clear();
                    Iterator<String> users = k.getUserIDs();
                    while (users.hasNext()) {
                        al.add(users.next());
                    }
                }
              //  System.out.println(Long.toHexString(k.getKeyID()) + "/" + k.isMasterKey() + "/" + k.isEncryptionKey() + "/" + al.size());
                if (k.isEncryptionKey() && al.contains(pks.pks_user())) {
             //   if (Long.toHexString(k.getKeyID()).equals(pks.pks_keyid().toLowerCase())) {
                    return k;
                }
               
            }
        }
      
      } catch (FileNotFoundException ex) {
              bslog(ex);
      } catch (IOException ex) {
            bslog(ex);
        } catch (PGPException ex) {
            bslog(ex);
        }
      return null;
    }
    
    public static List<PGPPublicKey> getPGPPublicKeyForExport(Path keyFilePath, String id) {
      List<PGPPublicKey> list = new ArrayList<>();
      PGPSecretKey key = null;
      InputStream in = null;
      char[] pass = null;
      
      pks_mstr pks = getPksMstr(new String[]{id});
      pass = bsmf.MainFrame.PassWord("1", pks.pks_pass().toCharArray()).toCharArray();
      Security.addProvider(new BouncyCastleProvider());
      
      try {
      if (keyFilePath != null) {
              in = new FileInputStream(keyFilePath.toFile());
      } 
      in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
      PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in, new BcKeyFingerprintCalculator());
     
      Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();

        while (key == null && rIt.hasNext()) {
            PGPPublicKeyRing kRing = rIt.next();
             
            Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
            ArrayList<String> al = new ArrayList<String>();
            while (key == null && kIt.hasNext()) {
                PGPPublicKey k = kIt.next();
                if (k.isMasterKey()) { // get list of users from master key
                    al.clear();
                    Iterator<String> users = k.getUserIDs();
                    while (users.hasNext()) {
                        al.add(users.next());
                    }
                }
              //  System.out.println(Long.toHexString(k.getKeyID()) + "/" + k.isMasterKey() + "/" + k.isEncryptionKey() + "/" + al.size());
                if (al.contains(pks.pks_user())) {
             //   if (Long.toHexString(k.getKeyID()).equals(pks.pks_keyid().toLowerCase())) {
                    list.add(k);
                }
               
            }
        }
      
      } catch (FileNotFoundException ex) {
              bslog(ex);
      } catch (IOException ex) {
            bslog(ex);
        } catch (PGPException ex) {
            bslog(ex);
        }
      return list;
    }
    
    
    
    public static byte[] decryptPGPData(byte[] encryptedData, String passPhrase, Path keyFilePath, PGPPrivateKey pgpPrvKey) throws PGPException, IOException {
        byte[] decryptedData = null;
        InputStream keyIn = null;
        
        if (keyFilePath != null) {
           keyIn = new FileInputStream(keyFilePath.toFile());
        }
        InputStream in = new ByteArrayInputStream(encryptedData);
        Security.addProvider(new BouncyCastleProvider());
        in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
        PGPObjectFactory pgpF = new PGPObjectFactory(in,new BcKeyFingerprintCalculator());
        PGPEncryptedDataList enc;
        Object o = pgpF.nextObject();
        if (o instanceof  PGPEncryptedDataList) {
		enc = (PGPEncryptedDataList) o;
        } else {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
        }
        
        if (enc == null) {
            bslog("failed to create PGPEncryptedDataList...enc is null");
            return null;
        }
                Iterator<?> it = enc.getEncryptedDataObjects();
		PGPPrivateKey sKey = null;
		PGPPublicKeyEncryptedData pbe = null;
              
                    while (sKey == null && it.hasNext()) {
                            pbe = (PGPPublicKeyEncryptedData) it.next();
                            if (pgpPrvKey == null) { // must be external...retrieve private key
                            sKey = getPGPSecretKey(keyIn, pbe.getKeyID(), passPhrase.toCharArray());
                            } else {
                            sKey = pgpPrvKey; // use private key non-external
                            }
                    }
                    
                    if (sKey == null) {
                            bslog("Secret key for message not found when decrypting PGP with passphrase: " + passPhrase);
                            return null;
                    }
               
                   
                
                
                PublicKeyDataDecryptorFactory b = new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").setContentProvider("BC").build(sKey);

		InputStream clear = pbe.getDataStream(b);

		PGPObjectFactory plainFact = new PGPObjectFactory(clear,new BcKeyFingerprintCalculator());

		Object message = plainFact.nextObject();

		if (message instanceof  PGPCompressedData) {
			PGPCompressedData cData = (PGPCompressedData) message;
			PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream(),null);

			message = pgpFact.nextObject();
		}

		if (message instanceof  PGPLiteralData) {
			PGPLiteralData ld = (PGPLiteralData) message;
			//InputStream unc = ld.getInputStream();
                        decryptedData = ld.getInputStream().readAllBytes();
			
		} else if (message instanceof  PGPOnePassSignatureList) {
			throw new PGPException("Encrypted message contains a signed message - not literal data.");
		} else {
			throw new PGPException("Message is not a simple encrypted file - type unknown.");
		}

		if (pbe.isIntegrityProtected()) {
			if (!pbe.verify()) {
				throw new PGPException("Message failed integrity check");
			}
		}
                
        
                
    
             
        
        return decryptedData;
                    
        
        
    }
    
    public static boolean verifySignature(final byte[] plaintext, final byte[] signedData, boolean isDebug)  {
        boolean x = false;
        if (plaintext == null || signedData == null) {
            return x;
        }
        
        try {
            CMSSignedData s = new CMSSignedData(new CMSProcessableByteArray(plaintext), signedData);
            Store certstore = s.getCertificates();
            SignerInformationStore signers = s.getSignerInfos();
            Collection<SignerInformation> c = signers.getSigners();
            SignerInformation signer = c.iterator().next();
            
            if (signer == null) {
                System.out.println("Error in verifySignature...signer is null");
                return x;
            }
            
            
                Collection<X509CertificateHolder> certCollection = certstore.getMatches(signer.getSID());
                Iterator<X509CertificateHolder> certIt = certCollection.iterator(); 
                if (! certIt.hasNext()) {
                    System.out.println("ERROR:  inside verifySignature: certCollection.iterator() has no next ");
                    return x;
                }
                X509CertificateHolder certHolder = certIt.next();

                try {
                    x = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certHolder));
                    
                    // This Debug statement must be ran 'after' verify....an exception is thrown otherwise
                     if (isDebug) {
                    String signatureAlgName = signer.getDigestAlgorithmID().getAlgorithm().getId() + "with" + signer.getEncryptionAlgOID();
                    System.out.println("Composite signature algorithm (based on OIDs): " + signatureAlgName);
                    AttributeTable attributes = signer.getSignedAttributes();
                        if (attributes != null) {
                            Attribute attribute = attributes.get(CMSAttributes.messageDigest);
                            DEROctetString digest = (DEROctetString) attribute.getAttrValues().getObjectAt(0);
                            // if these values are different, the exception is thrown
                            System.out.println("digest hex string:");
                            System.out.println(Hex.toHexString(digest.getOctets()));
                            System.out.println("signer hex string:");
                            System.out.println(Hex.toHexString(signer.getContentDigest()));
                            System.out.println("data size: " + plaintext.length);
                        } else {
                            System.out.println("WARNING:  signer.getSignedAttributes() returned null attributes");
                        }
                    }
                    
                } catch (CMSException ex) {
                    bslog(ex);
                }
            
        } catch ( CMSException | OperatorCreationException | CertificateException ex) {
            bslog(ex);
        }
        return x;
}
    
    public static String[] verifySignatureView(byte[] data, String contentType, CMSSignedData cs, String as2m) throws MessagingException, IOException  {
        boolean x = false;
        String[] r = new String[]{"false","","","","","",""};

        byte[] FileWHeadersBytes = null;
        byte[] Signature = null;
        MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(data, contentType));
        
        
        System.out.println("MP Count: " + mp.getCount());
        System.out.println("MP ContentType: " + mp.getContentType() + "   /   " + contentType);
        System.out.println("data size: " + data.length);
        
        
        
        if (mp.getCount() < 1 ) { // ...must not be a signature...signed MDN required
            // need logging verbiage here
            return new String[]{"false","MP count is less than 2","","","","",""};
        }
        
        if (mp.getCount() == 1 ) { 
            for (int j = 0; j < mp.getCount(); j++) {
                BodyPart bodyPart = mp.getBodyPart(j);
                String contentTypex = bodyPart.getContentType();
                MimeMultipart mpsub = new MimeMultipart(new ByteArrayDataSource(data, contentTypex));
                for (int k = 0; k < mpsub.getCount(); k++) {
                    if (! mpsub.getBodyPart(k).getContentType().contains("smime.p7s")) {
                        ByteArrayOutputStream aos = new ByteArrayOutputStream();
                        mpsub.getBodyPart(k).writeTo(aos);
                        aos.close(); 
                        FileWHeadersBytes = aos.toByteArray();
                    }
                    if (mpsub.getBodyPart(k).getContentType().contains("smime.p7s")) {
                        Signature = IOUtils.toByteArray((InputStream) mpsub.getBodyPart(k).getInputStream());
                    }
                }
            }
        }
        
        if (mp.getCount() > 1 ) { 
        for (int j = 0; j < mp.getCount(); j++) {
                    MimeBodyPart mbp = (MimeBodyPart) mp.getBodyPart(j); 
                    
                    if (! mbp.getContentType().contains("smime.p7s")) { // must be non sig file
                    
                  //  ContentType ct = new ContentType(mbp.getContentType());
                 //   System.out.println("HERE IS verify mbp BOUNDARY: " + ct.getParameter("boundary"));

                    /*
                        // must be multiPart...need to get 2nd body of multipart
                    MimeMultipart mpInner = new MimeMultipart(new ByteArrayDataSource(mbp.getInputStream().readAllBytes(), contentType));
                    
                    ByteArrayOutputStream aos = new ByteArrayOutputStream();
                    mpInner.getBodyPart(0).writeTo(aos);
                    aos.close(); 
                    FileWHeadersBytes = aos.toByteArray();
                    */
                    
                      ByteArrayOutputStream aos = new ByteArrayOutputStream();
                      mp.getBodyPart(0).writeTo(aos);
                      aos.close(); 
                      FileWHeadersBytes = aos.toByteArray();
                     
                  //  FileWHeadersBytes = mbp.getRawInputStream().readAllBytes();
                    }
                    
                    if (mbp.getContentType().contains("smime.p7s")) { // must be sig
                      //  String sig = new String(Base64.encode(mbp.getInputStream().readAllBytes()));
                      //  Signature = sig.getBytes();
                     //   Signature = IOUtils.toByteArray((InputStream) mbp.getContent());
                        Signature = IOUtils.toByteArray((InputStream) mbp.getInputStream());
                     //   Signature = IOUtils.toByteArray(mbp.getRawInputStream());
                    }
               } // for each mpsub (should be two if signed) 
        } 
        System.out.println("FileWHeaderBytes size: " + FileWHeadersBytes.length);
        System.out.println("Signature size: " + Signature.length);
        
        /*
        for (int j = 0; j < mp.getCount(); j++) {
           MimeBodyPart mbp = (MimeBodyPart) mp.getBodyPart(j); 
           
           if (mbp.getInputStream() != null && ! mbp.getContentType().contains("signature")) { // must be non sig file
            //  System.out.println("verifyMDNSignature is NOT signature bodypart: " + j);
           //   System.out.println("verifyMDNSignature mpb number: " + j);
           //   System.out.println(new String (mbp.getInputStream().readAllBytes()));
              ByteArrayOutputStream aos = new ByteArrayOutputStream();
              mp.getBodyPart(0).writeTo(aos);
              aos.close(); 
              FileWHeadersBytes = aos.toByteArray();
             // FileWHeadersBytes = IOUtils.toByteArray(mbp.getInputStream());
           }
           
           if (mbp.getInputStream() != null && mbp.getContentType().contains("signature")) { // must be sig
              
            //  System.out.println("verifyMDNSignature is signature bodypart: " + j);
              System.out.println("verifyMDNSignature mpb number: " + j);
              System.out.println(new String (Base64.encode(mbp.getInputStream().readAllBytes())));
             // Signature = IOUtils.toByteArray((InputStream) mbp.getContent());
              Signature = mbp.getInputStream().readAllBytes();
           }
           
           if (mbp.getInputStream() != null && mbp.getContentType().contains("pkcs7-signature")) { // must be sig
            Signature = IOUtils.toByteArray((InputStream) mbp.getContent());
           }
        }
        
        */
        
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String debugfile = "FileWHeadersBytesMDN." + now + "." + Long.toHexString(System.currentTimeMillis());
            Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
            try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
            stream.write(FileWHeadersBytes);
            }
            
            debugfile = "SignatureMDN." + now + "." + Long.toHexString(System.currentTimeMillis());
            pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
            try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
            stream.write(Signature);
            }
            
            
        
        if (FileWHeadersBytes == null || Signature == null) {
            return new String[]{"false","plaintext or signedData is null","","","","",""};
        }
        
        
         boolean maybe = false;
         try {
             maybe = verifySignatureNonBC(FileWHeadersBytes, Signature, as2m);
         } catch (Exception ex) {
             System.out.println(ex.getMessage()); 
         }
         
        
        try {
          //  byte[] newdata = Arrays.copyOf(FileWHeadersBytes, FileWHeadersBytes.length - 4);
            CMSSignedData s = new CMSSignedData(new CMSProcessableByteArray(FileWHeadersBytes), Signature);
            if (cs != null) {
                s = cs;
            }
            
            
           
            // ContentInfo conInf = signed.getContentInfo();
            // CMSProcessable sigContent = signed.getSignedContent();
            byte[] byte_out = null;
            ByteArrayOutputStream out = null;
            out = new ByteArrayOutputStream();
            s.getSignedContent().write(out);
            byte_out = out.toByteArray();
            String ss = new String(byte_out);
            System.out.println("Original Content-->" +ss); 
           
            
            Store certstore = s.getCertificates();
            SignerInformationStore signers = s.getSignerInfos();
            Collection<SignerInformation> c = signers.getSigners();
            SignerInformation signer = c.iterator().next();
            Collection<X509CertificateHolder> certCollection = certstore.getMatches(signer.getSID());
            Iterator<X509CertificateHolder> certIt = certCollection.iterator();
            if (! certIt.hasNext()) {
              //  System.out.println("inside verify: certIt has no next ");
                return new String[]{"false","no CertIt","","","","",""};
            }
            X509CertificateHolder certHolder = certIt.next();
            
            try {
                x = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(certHolder));
            } catch (CMSException ex) {
                bslog(ex);
            }
            
            
            
            
            
            
            
            
            
            AttributeTable attributes = signer.getSignedAttributes();
            Attribute attribute = attributes.get(CMSAttributes.messageDigest);
            DEROctetString digest = (DEROctetString) attribute.getAttrValues().getObjectAt(0);
            // if these values are different, the exception is thrown
            r[0] = String.valueOf(x);
            r[1] = Hex.toHexString(digest.getOctets());
            r[2] = Hex.toHexString(signer.getContentDigest());
            r[3] = digest.toString();
            r[4] = String.valueOf(FileWHeadersBytes.length);
            r[5] = String.valueOf(Signature.length);
            r[6] = String.valueOf(maybe);
            
          
            
            
            
        } catch ( CMSException | OperatorCreationException | CertificateException ex) {
            bslog(ex);
        } catch (Exception ex) {
             System.getLogger(apiUtils.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
         }


        return r;

}
        
    public static boolean verifyMDNSignature(byte[] data, String contentType) throws MessagingException, IOException {
        boolean b = false;
        byte[] FileWHeadersBytes = null;
        byte[] Signature = null;
        MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(data, contentType));
        if (mp.getCount() < 2 ) { // ...must not be a signature...signed MDN required
            // need logging verbiage here
            return false; 
        }
        for (int j = 0; j < mp.getCount(); j++) {
           MimeBodyPart mbp = (MimeBodyPart) mp.getBodyPart(j); 
           
           if (mbp.getInputStream() != null && ! mbp.getContentType().contains("signature")) { // must be non sig file
            //  System.out.println("verifyMDNSignature is NOT signature bodypart: " + j);
              System.out.println("verifyMDNSignature mpb number: " + j);
              System.out.println(new String (mbp.getInputStream().readAllBytes()));
              ByteArrayOutputStream aos = new ByteArrayOutputStream();
              mp.getBodyPart(0).writeTo(aos);
              aos.close(); 
              FileWHeadersBytes = aos.toByteArray();
             // FileWHeadersBytes = IOUtils.toByteArray(mbp.getInputStream());
           }
           /*
           if (mbp.getInputStream() != null && mbp.getContentType().contains("signature")) { // must be sig
              
            //  System.out.println("verifyMDNSignature is signature bodypart: " + j);
              System.out.println("verifyMDNSignature mpb number: " + j);
              System.out.println(new String (Base64.encode(mbp.getInputStream().readAllBytes())));
             // Signature = IOUtils.toByteArray((InputStream) mbp.getContent());
              Signature = mbp.getInputStream().readAllBytes();
           }
           */
           if (mbp.getInputStream() != null && mbp.getContentType().contains("pkcs7-signature")) { // must be sig
            Signature = IOUtils.toByteArray((InputStream) mbp.getContent());
           }
        }
        if (FileWHeadersBytes != null && Signature != null) {  
           // System.out.println("verifyMDNSignature ...attempting to verify");
        b = verifySignature(FileWHeadersBytes, Signature, false);
        }
        
        return b;
    }
    
    public static boolean verifyMDNSignatureNew(MimeMessage message, X509Certificate pubcert) throws Exception {
        // Cast the message content to SMIMESigned if it's signed
        DigestCalculatorProvider digestCalculatorProvider = 
            new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();

        // 2. Instantiate SMIMEToolkit by passing the provider to the constructor.
        SMIMEToolkit smimeToolkit = new SMIMEToolkit(digestCalculatorProvider);

        if (! smimeToolkit.isSigned(message)) {   
            System.err.println("Message is not S/MIME signed.");
            return false;
        }
        
        SMIMESigned s = new SMIMESigned((MimeMultipart) message.getContent());
        Store certs = s.getCertificates();
        SignerInformationStore signers = s.getSignerInfos();
        
        // Iterate through each signature
        for (SignerInformation signer : signers.getSigners()) {
            // Find the signing certificate in the certificates store
            X509Certificate cert = (X509Certificate) certs.getMatches(signer.getSID()).iterator().next();

            // Check if the signing certificate matches a trusted anchor (or is trusted in a chain)
            if (cert.equals(pubcert)) {
                // Verify the signature with the signer's certificate
                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
                    System.out.println("NEW VERIFY MDN:  Signature verified successfully.");
                    return true;
                }
            }
        }
        System.err.println("NEW VERIFY MDN:  Signature verification failed.");
        return false;
    }
    
    public static boolean verifySignatureNonBC(byte[] plaintex, byte[] signatureBytes, String as2id) throws Exception {
        as2_mstr as2m = getAS2Mstr(new String[]{as2id});
        String sigalgo = as2m.as2_signalgo();
        String x = "";
        if (sigalgo.equals("SHA-256")) {
            x = "SHA256WithRSA";
        } else if (sigalgo.equals("SHA-1")) {
            x = "SHA1WithRSA";
        } else if (sigalgo.equals("MD-5")) {
            x = "MD5WithRSA";    
        } else {
           x = "SHA256WithRSA";  
        }
        Signature verifier = Signature.getInstance(x);
        verifier.initVerify(getPublicKey(as2m.as2_signcert()));
        verifier.update(plaintex);
        boolean verified = verifier.verify(signatureBytes);
        
        return verified;
    }
    
    public static bsr encryptFile(byte[] indata, String keyid) throws IOException {
     byte[] encryptedData = null;
     
     
     String[] r = new String[]{"0",""};
     pks_mstr pks = getPksMstr(new String[]{keyid}); 
     
     if (pks.pks_standard().equals("openPGP")) {
        ByteArrayOutputStream encOut = null;
        String storename = getPKSStoreFileName(pks.pks_parent()) + ".pkr";
        Path keyfilepath = FileSystems.getDefault().getPath(storename);  
        PGPPublicKey pgpPubKey = getPGPPublicKey(keyfilepath, pks.pks_id()); 
        try {
        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
            new JcePGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256)
                .setWithIntegrityPacket(true)
                .setSecureRandom(new SecureRandom()).setProvider("BC"));
        encGen.addMethod(
            new JcePublicKeyKeyEncryptionMethodGenerator(pgpPubKey)
                .setProvider("BC"));
        encOut = new ByteArrayOutputStream();
        // create an indefinite length encrypted stream
        OutputStream cOut;
        cOut = encGen.open(encOut, new byte[4096]);
         
        // write out the literal data
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(
            cOut, PGPLiteralData.BINARY,
            PGPLiteralData.CONSOLE, indata.length, new Date());
        pOut.write(indata);
        pOut.close();
        cOut.close();
       } catch (PGPException ex) {
             r[0] = "1";
             r[1] = ex.getMessage();
             bslog(ex);
       }
      return new bsr(r,encOut.toByteArray());      
     }
     
     if (pks.pks_standard().equals("X.509")) {
         try {
            encryptedData = apiUtils.encryptData(indata, apiUtils.getPublicKeyAsCert(keyid), "" );
            } catch (CMSException ex) {
                r[0] = "1";
                r[1] = ex.getMessage();
                bslog(ex);
            } catch (CertificateEncodingException ex) {
                r[0] = "1";
                r[1] = ex.getMessage();
                bslog(ex);
            }
     }
     
     return new bsr(r,encryptedData);
    }
        
    public static bsr decryptFile(byte[] indata, String keyid) throws IOException {
     byte[] decryptedData = null;
     String[] r = new String[]{"0",""};
     pks_mstr pks = getPksMstr(new String[]{keyid}); 
     Path keyfilepath = null;
     PGPPrivateKey pgpPrvKey = null;
     if (pks.pks_standard().equals("openPGP")) {
         try {
            if (pks.pks_external().equals("1")) { 
              keyfilepath = FileSystems.getDefault().getPath(pks.pks_file());
              if (! keyfilepath.toFile().exists()) {
                r[0] = "1";
                r[1] = "pgp keyfile path does not exist: " + keyid;
                return new bsr(r, null);
              }
            } else {
              String storename = getPKSStoreFileName(pks.pks_parent()) + ".skr";
              keyfilepath = FileSystems.getDefault().getPath(storename); 
              pgpPrvKey = getPGPPrivateKey(keyfilepath, pks.pks_id());
              
              if (pgpPrvKey == null) {
                r[0] = "1";
                r[1] = "pgp privateKey is null: " + keyid;
                return new bsr(r, null);
              }
            }
             
            String passphrase = bsmf.MainFrame.PassWord("1", pks.pks_pass().toCharArray());
            decryptedData = apiUtils.decryptPGPData(indata, passphrase, keyfilepath, pgpPrvKey );
            
            if (decryptedData == null) {
                r[0] = "1";
                r[1] = "failed to decrypt message in apiUtils.decryptPGPData";
            }
            
            } catch (PGPException ex) {
                r[0] = "1";
                r[1] = "decryptFile(): " + ex.getMessage();
                bslog(ex);
            }
     }
     if (pks.pks_standard().equals("X.509")) {
         try {
            decryptedData = apiUtils.decryptData(indata, apiUtils.getPrivateKey(keyid) );
            } catch (CMSException ex) {
             r[0] = "1";
                r[1] = ex.getMessage();
         }
     }
     
     return new bsr(r,decryptedData);
    }
    
    public static MimeBodyPart signData(
          byte[] data,
          String signalgo,
          X509Certificate signingCertificate,
          PrivateKey signingKey, String filename, as2_mstr as2m, String contenttype) throws Exception {
            
          if (signalgo.isBlank()) {
              signalgo = "SHA1withRSA";
          }
        
            List<X509Certificate> certList = new ArrayList<X509Certificate>();
            certList.add(signingCertificate);
            certs = new JcaCertStore(certList);

            SMIMESignedGenerator sGen = new SMIMESignedGenerator(false ? SMIMESignedGenerator.RFC3851_MICALGS : SMIMESignedGenerator.RFC5751_MICALGS);
            JcaSimpleSignerInfoGeneratorBuilder jSig = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC");
            SignerInfoGenerator sig = jSig.build(signalgo, signingKey, signingCertificate);
            sGen.addSignerInfoGenerator(sig);
            sGen.addCertificates(certs);
            MimeBodyPart dataPart = new MimeBodyPart();
            dataPart.removeHeader("Content-Type");
            dataPart.removeHeader("Content-Disposition");
            
            InputStream targetStream = new ByteArrayInputStream(data);
            //InputStream targetStream = new FileInputStream(new File("c:\\junk\\item_costs.pdf"));
            ByteArrayDataSource ds = new ByteArrayDataSource(targetStream, contenttype); 
            dataPart.setDataHandler(new DataHandler(ds));
            
            // dataPart.setText(new String(data, StandardCharsets.UTF_8), "UTF-8");
            dataPart.setHeader("Content-Type", contenttype);
            dataPart.setHeader("Content-Disposition", "attachment; filename=" + filename);
            // dataPart.setHeader("Content-Transfer-Encoding", "binary");
            
            ArrayList<String> list = EDData.getAS2AttributesList(as2m.as2_id(), "payloadheader");
            for (String x : list) {
                String[] h = x.split(":",-1);
                if (h != null && h.length > 1) {
                 dataPart.setHeader(h[0], h[1]);
                }
            }
            
            if (dataPart.getHeader("content-transfer-encoding") == null) {
              dataPart.setHeader("Content-Transfer-Encoding", "binary");  
            }
            
           /* 
        System.out.println("HERE IS certificate serial number: "  + "->"  + signingCertificate.getSerialNumber());
        System.out.println("HERE IS certificate signature: "  + "->"  + signingCertificate.getSignature());
        System.out.println("HERE IS certificate AlgName: "  + "->"  + signingCertificate.getSigAlgName());
        System.out.println("HERE IS privatekey algorithm: " + "->"  + signingKey.getAlgorithm());
        System.out.println("HERE IS privatekey format: "   + "->" + signingKey.getFormat());
         */
        
            byte[] dataPartBytes = dataPart.getInputStream().readAllBytes();            
            MimeMultipart signedData = sGen.generate(dataPart);             
            MimeBodyPart tmpBody = new MimeBodyPart();
            tmpBody.setContent(signedData);
            String revisedContentType = signedData.getContentType();
            revisedContentType = revisedContentType.replace("sha-1", "sha1");
            revisedContentType = revisedContentType.replace("sha-256", "sha256");
            tmpBody.setHeader("Content-Type", revisedContentType);
            
            return tmpBody;
	}

   
    public static MimeMultipart signMDN(byte[] messg, String keyuser, String signalgo, String boundary) throws Exception {
       // MimeBodyPart messagePart = new MimeBodyPart();
      //  messagePart.removeHeader("Content-Type");
      //  messagePart.removeHeader("Content-Disposition");
      //  InputStream targetStream = new ByteArrayInputStream(messg);
      //  ByteArrayDataSource ds = new ByteArrayDataSource(targetStream, "multipart/mixed; report-type=disposition-notification; boundary=" + "\"" + boundary + "\"");  
      //  messagePart.setDataHandler(new DataHandler(ds));
            
            MimeBodyPart messagePart = new MimeBodyPart();
            InputStream targetStream = new ByteArrayInputStream(messg);
            ByteArrayDataSource ds = new ByteArrayDataSource(targetStream, "multipart/report; report-type=disposition-notification; boundary=" + "\"" + boundary + "\""); 
            messagePart.setDataHandler(new DataHandler(ds));
      
   //     InternetHeaders fileHeaders = new InternetHeaders();
   //     fileHeaders.setHeader("Content-Type", "multipart/report; report-type=disposition-notification; boundary=" + "\"" + boundary + "\"");
    //    fileHeaders.setHeader("Content-Transfer-Encoding", "binary");
    //    MimeBodyPart messagePart = new MimeBodyPart(fileHeaders, messg);
        messagePart.removeHeader("Content-Type");
        messagePart.removeHeader("Content-Disposition");
          
        SMIMESignedGenerator gen = new SMIMESignedGenerator(false ? SMIMESignedGenerator.RFC3851_MICALGS : SMIMESignedGenerator.RFC5751_MICALGS);
        X509Certificate certificate = getPublicKeyAsCert(keyuser);
        PrivateKey privateKey = getPrivateKey(keyuser);
        
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(certificate);
        Store certs = new JcaCertStore(certList);
        /*
        System.out.println("HERE IS certificate serial number: " + keyuser + "->"  + certificate.getSerialNumber());
        System.out.println("HERE IS certificate signature: " + keyuser + "->"  + certificate.getSignature());
        System.out.println("HERE IS certificate AlgName: " + keyuser + "->"  + certificate.getSigAlgName());
        System.out.println("HERE IS privatekey algorithm: " + keyuser + "->"  + privateKey.getAlgorithm());
        System.out.println("HERE IS privatekey format: "  + keyuser + "->" + privateKey.getFormat());
       */
        if (signalgo.isBlank()) {
              signalgo = "SHA1withRSA";
        }
        JcaSimpleSignerInfoGeneratorBuilder jSig = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC");
        SignerInfoGenerator sig = jSig.build(signalgo, privateKey, certificate);
        gen.addCertificates(certs);
        gen.addSignerInfoGenerator(sig);
     //   messagePart.setHeader("Content-Type", "text/plain");
     //   messagePart.setHeader("Content-Transfer-Encoding", "binary");
     //   messagePart.addHeader("Content-Type", "multipart/report; report-type=disposition-notification; boundary=" + "\"" + "--" + boundary + "\"");
        messagePart.addHeader("Content-Type", "multipart/report; report-type=disposition-notification; boundary=" + "\"" + boundary + "\"");
        
       // messagePart.addHeader("Content-Type", "multipart/signed; report-type=disposition-notification; boundary=" + "\"" + boundary + "\"");
      //  messagePart.setHeader("Content-Disposition", "attachment; filename=" + filename);
        messagePart.setHeader("Content-Transfer-Encoding", "binary");  
        
        byte[] messagePartBytes = messagePart.getInputStream().readAllBytes();
        // byte[] newdata = Arrays.copyOf(messagePartBytes, messagePartBytes.length - 2);
        ByteArrayOutputStream xy = new ByteArrayOutputStream();
        messagePart.writeTo(xy);
        xy.flush();
        xy.close();
        
        
            
        MimeMultipart signedContent = gen.generate(messagePart);
       
       /*
                String debugfile = "mdn_part_signed" + "." + Long.toHexString(System.currentTimeMillis()) + ".txt";
                Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
                try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
                  stream.write(messagePartBytes);
                }
                String debugfilex = "mdn_mmp" + "." + Long.toHexString(System.currentTimeMillis()) + ".txt";
                Path pathinputx = FileSystems.getDefault().getPath("temp" + "/" + debugfilex);
                try (FileOutputStream stream = new FileOutputStream(pathinputx.toFile())) {
                  signedContent.writeTo(stream);
                }
            */
        
        //MimeBodyPart xxx = gen.generateEncapsulated(messagePart);
        //MimeMultipart xxxmmp = new MimeMultipart();
        //xxxmmp.addBodyPart(xxx);
                
       
        /*
        System.out.println("HERE IS WHAT IS BEING SIGNED bodypart0: ");
        System.out.println(new String (signedContent.getBodyPart(0).getInputStream().readAllBytes()));
        //System.out.println(new String (messagePart.getInputStream().readAllBytes()));
        System.out.println("HERE IS WHAT IS BEING SIGNED bodypart1: ");
        //System.out.println(new String (signedContent.getBodyPart(1).getInputStream().readAllBytes()));
        System.out.println(new String (Base64.encode(signedContent.getBodyPart(1).getInputStream().readAllBytes())));
      */
        
        /*
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String debugfile = "signedContentMDN." + now + "." + Long.toHexString(System.currentTimeMillis());
            Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
            try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
            signedContent.writeTo(stream);
            }
        */
        // HERE...take this out
        /*
        MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(messagePartBytes);
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            System.out.println("Java Security Digest: " + hexString.toString());
            
            
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(messagePartBytes);
            byte[] signatureBytes = signature.sign();
            signature.initVerify(certificate);
            signature.update(messagePartBytes);
            boolean isVerified = signature.verify(signatureBytes);
            
            System.out.println("Java Signature boolean: " + isVerified);
            System.out.println(new String (Base64.encode(signatureBytes)));
        
        
            ByteArrayOutputStream yy = new ByteArrayOutputStream();
            signedContent.writeTo(yy);
            yy.flush();
            yy.close();
            System.out.println("messg byte length: " + messg.length);
            System.out.println("messagePart byte length: " + messagePartBytes.length);
            System.out.println("signedContent byte length: " + yy.toByteArray().length);
            String contentType = "multipart/signed; protocol=\"application/pkcs7-signature\"; micalg=sha-1;";
            String[] jj = verifySignatureView(yy.toByteArray(), contentType, null);
            int ji = 0;
            System.out.println("MY TEST signedContent: content type = " + contentType);
            for (String j : jj) {
                System.out.println("MY TEST signedContent: " + ji + " = " + j);
                ji++;
            }
            */
      
            // HERE...to here
            
            
       // MimeBodyPart mbp = new MimeBodyPart();
       // mbp.setContent(signedContent);
       
      //  MimeBodyPart mbp = gen.generateEncapsulated(messagePart);
      //  mbp.setHeader("Content-Type", "message/disposition-notification");
      
     
        return signedContent;
    }
     
    public static MimeMultipart signMDNFlat(MimeBodyPart mbp, String keyuser, String signalgo) throws Exception {
      // SMIMESignedGenerator gen = new SMIMESignedGenerator(false ? SMIMESignedGenerator.RFC3851_MICALGS : SMIMESignedGenerator.RFC5751_MICALGS);
       SMIMESignedGenerator gen = new SMIMESignedGenerator(); 
        if (signalgo.isBlank()) {
              signalgo = "SHA1withRSA";
        }
       
        
        X509Certificate certificate = getPublicKeyAsCert(keyuser);
        PrivateKey privateKey = getPrivateKey(keyuser);
        
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(certificate);
        Store certs = new JcaCertStore(certList);
        
        JcaSimpleSignerInfoGeneratorBuilder jSig = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC");
        SignerInfoGenerator sig = jSig.build(signalgo, privateKey, certificate);
        gen.addCertificates(certs);
        gen.addSignerInfoGenerator(sig); 
        MimeMultipart signedContent = gen.generate(mbp);
        return signedContent;
    }
    
    public static MimeBodyPart signMDNFlatMBP(MimeBodyPart mbp, String keyuser, String signalgo) throws Exception {
      //  SMIMESignedGenerator gen = new SMIMESignedGenerator(false ? SMIMESignedGenerator.RFC3851_MICALGS : SMIMESignedGenerator.RFC5751_MICALGS);
        SMIMESignedGenerator gen = new SMIMESignedGenerator("binary");
        if (signalgo.isBlank()) {
              signalgo = "SHA1withRSA";
        }
        
        MimeMessage dummyMessage = new MimeMessage((Session) null);
        dummyMessage.setContent(mbp, mbp.getContentType());
        dummyMessage.saveChanges();
        
        X509Certificate certificate = getPublicKeyAsCert(keyuser);
        PrivateKey privateKey = getPrivateKey(keyuser);
        
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(certificate);
        Store certs = new JcaCertStore(certList);
        
        JcaSimpleSignerInfoGeneratorBuilder jSig = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC");
        SignerInfoGenerator sig = jSig.build(signalgo, privateKey, certificate);
        gen.addCertificates(certs);
        gen.addSignerInfoGenerator(sig); 
        MimeMultipart signedContent = gen.generate(dummyMessage);
        
        MimeBodyPart tmpBody = new MimeBodyPart();        
        tmpBody.setContent(signedContent);
        String revisedContentType = signedContent.getContentType();
        revisedContentType = revisedContentType.replace("sha-1", "sha1");
        revisedContentType = revisedContentType.replace("sha-256", "sha256");
        tmpBody.setHeader("Content-Type", revisedContentType);
            
        return tmpBody; 
    }
     
    public static String signMDNstr(MimeBodyPart mbp, String keyuser, String signalgo) throws Exception {
        SMIMESignedGenerator gen = new SMIMESignedGenerator(false ? SMIMESignedGenerator.RFC3851_MICALGS : SMIMESignedGenerator.RFC5751_MICALGS);
        
        if (signalgo.isBlank()) {
              signalgo = "SHA1withRSA";
        }
        
        
        X509Certificate certificate = getPublicKeyAsCert(keyuser);
        PrivateKey privateKey = getPrivateKey(keyuser);
        
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(certificate);
        Store certs = new JcaCertStore(certList);
        
        JcaSimpleSignerInfoGeneratorBuilder jSig = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC");
        SignerInfoGenerator sig = jSig.build(signalgo, privateKey, certificate);
        gen.addCertificates(certs);
        gen.addSignerInfoGenerator(sig); 
        MimeMultipart signedContent = gen.generate(mbp);
        BodyPart part = signedContent.getBodyPart(1);
        java.util.Base64.Encoder encoder = java.util.Base64.getMimeEncoder(76, new byte[]{'\r','\n'});        
        String output = new String(encoder.encode(part.getInputStream().readAllBytes()), StandardCharsets.UTF_8);
        /*
        try ( InputStream is = part.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer,0,bytesRead);
            }
            byte[] signatureBytes = baos.toByteArray();
            output = new String(Base64.encode(signatureBytes));   
            //output = Base64.toBase64String(signatureBytes);
        }
         */
            
        return output; 
    }
     
    
    public static boolean isEncrypted(byte[] encryptedData) {
            CMSEnvelopedData envelopedData;
            String x = null;
                    try {
                        envelopedData = new CMSEnvelopedData(encryptedData);
                        x = envelopedData.getEncryptionAlgOID();
                    } catch (CMSException ex) {
                        x = null;
                    }
            return ! (x == null);
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
    
    public static void saveRequestToFile(HttpUriRequest request, String filePath) throws IOException {
        try (OutputStream fileOut = new FileOutputStream(new File(filePath))) {
            // Write the request line (e.g., POST /api/data HTTP/1.1).
            String requestLine = request.getRequestLine().toString() + "\r\n";
            fileOut.write(requestLine.getBytes(StandardCharsets.UTF_8));

            // Write all request headers.
            for (Header header : request.getAllHeaders()) {
                String headerLine = header.getName() + ": " + header.getValue() + "\r\n";
                fileOut.write(headerLine.getBytes(StandardCharsets.UTF_8));
            }
            fileOut.write("\r\n".getBytes(StandardCharsets.UTF_8)); // Blank line separates headers from body.

            // Write the request body if it exists.
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                if (entity != null) {
                    // ByteArrayEntity is repeatable, so we can read its content safely.
                    try (InputStream entityIn = entity.getContent()) {
                        entityIn.transferTo(fileOut);
                    }
                }
            }
        }
    }
    
    public static String postAS2( String as2id, boolean isDebug) throws MalformedURLException, URISyntaxException, IOException, CertificateException, NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateEncodingException, CMSException, SMIMEException, Exception  {
        
        StringBuilder r = new StringBuilder();
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        ArrayList<String[]> logdet = new ArrayList<String[]>(); 
        Security.addProvider(new BouncyCastleProvider());
        
       
        // gather pertinent info for this AS2 ID / Partner
        as2_mstr as2m = getAS2Mstr(new String[]{as2id});
        edi_ctrl edic = getEDICtrl();
        
        
      //  String[] tp = ediData.getAS2Info(as2id);
        String url = "";
        if (as2m.as2_port().isBlank()) {
           url = as2m.as2_protocol() + "://" + as2m.as2_url() + "/" + as2m.as2_path(); 
        } else {
           url = as2m.as2_protocol() + "://" + as2m.as2_url() + ":" + as2m.as2_port() + "/" + as2m.as2_path();
        }
        String as2To = as2m.as2_user();
        String as2From = as2m.as2_sysas2id();
        String internalURL = edic.edic_as2url();
        String sourceDir = as2m.as2_outdir();
      //  String signkeyid = edic.edic_signkey();  
        String signkeyid = getSystemSignKeyAlt(as2m.as2_id());  //TEV 20250704 check for as2 partner level signing key...which if empty defaults to system sign key
        String contenttype = as2m.as2_contenttype();
        
        
        int parent = writeAS2Log(new String[]{"0",as2id,"out",""," Init as2 outbound for partner: " + as2id + "/" + as2From + "/" + as2To,now,"",as2m.as2_site()}); 
        String parentkey = String.valueOf(parent);
        logdet.add(new String[]{parentkey, "info", "processing as2 for relationship " + as2From + "/" + as2To});
        logdet.add(new String[]{parentkey, "info", "Sending to URL / Port / Path = " + url});
        logdet.add(new String[]{parentkey, "info", "Source Directory: " + sourceDir});
        logdet.add(new String[]{parentkey, "info", "Encryption Cert file: " + as2m.as2_enccert()});
        logdet.add(new String[]{parentkey, "info", "Signing Key ID: " + signkeyid});
        logdet.add(new String[]{parentkey, "info", "Encryption Algo: " + as2m.as2_encalgo()});
        logdet.add(new String[]{parentkey, "info", "Signing Algo: " + as2m.as2_signalgo()});
       
       // System.out.println("here->" + as2To + "/" +  as2From + "/" + internalURL + "/" + sourceDir + "/" + signkeyid);
        
        X509Certificate encryptcertificate = getPublicKeyAsCert(as2m.as2_enccert());
        if (encryptcertificate == null) {
          logdet.add(new String[]{parentkey, "error", "Unable to retrieve encryption cert for " + as2m.as2_enccert()}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          return "Unable to retrieve encryption cert for " + as2m.as2_enccert();
        }
        
        logdet.add(new String[]{parentkey, "info", "Encryption with: " + encryptcertificate.getIssuerX500Principal().getName() + "/" + encryptcertificate.getSigAlgName()});
        logdet.add(new String[]{parentkey, "info", "Encryption Serial#: " + encryptcertificate.getSerialNumber().toString(16)});
        logdet.add(new String[]{parentkey, "info", "Encryption Expiration Window: " + encryptcertificate.getNotBefore() + "/" + encryptcertificate.getNotAfter()});
        
        pks_mstr pkid = admData.getPksMstr(new String[]{signkeyid});
        if (pkid == null || pkid.m()[0].equals("1")) {
          logdet.add(new String[]{parentkey, "error", "Unable to retrieve pks_mstr signing key for " + signkeyid}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          return "Unable to retrieve pks_mstr signing key for  " + signkeyid;
        }
        
        pks_mstr pkstore = admData.getPksMstr(new String[]{pkid.pks_parent()});
      //  String[] k = new String[]{"","","","",""};
        X509Certificate signcertificate = null; 
        PrivateKey key = null;
        
        if ( pkid.pks_type().equals("store") || pkid.pks_type().equals("publickey") ) {
          logdet.add(new String[]{parentkey, "error", "Using non-user signing key " + signkeyid}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          return "Using non-user signing key  " + signkeyid; 
        } else if (pkid.pks_type().equals("privatekey")) {
           key = readPrivateKeyFromFile(pkid.pks_file());
        } else {
     //   k = getKeyStoreByUser(signkeyid); // storefile, storeuser, storepass, user, pass, standard  // system signing key...always points to system keystore in k[0]
     //   k[2] = bsmf.MainFrame.PassWord("1", k[2].toCharArray());
     //   k[4] = bsmf.MainFrame.PassWord("1", k[4].toCharArray());
       
        char[] keyPassword = bsmf.MainFrame.PassWord("1", pkid.pks_pass().toCharArray()).toCharArray();  
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FileSystems.getDefault().getPath(pkstore.pks_file()).toString());
            if (pkstore.pks_storepass().isBlank()) {
               keystore.load(fis, null);
           } else {
               keystore.load(fis, bsmf.MainFrame.PassWord("1", pkstore.pks_storepass().toCharArray()).toCharArray());
           }
        } catch (FileNotFoundException ex) {
          logdet.add(new String[]{parentkey, "error", "Unable to locate keystore file for " + pkid.pks_id()}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          return "Unable to locate keystore file for " + pkid.pks_id(); 
        } finally {
            if (fis != null ) {
              try {
                  fis.close();
              } catch (IOException ex) {
                  bslog(ex);
              }
          }
        }
        
        
        
        key = (PrivateKey) keystore.getKey(pkid.pks_user(), keyPassword);
        signcertificate = (X509Certificate) keystore.getCertificate(pkid.pks_user());
    }
        
        if (key == null) {
          logdet.add(new String[]{parentkey, "error", "Unable to retrieve private key for signing " + signkeyid}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          return "Unable to retrieve private key for signing " + signkeyid;
        }
        
        if (signcertificate == null) {
          logdet.add(new String[]{parentkey, "error", "Unable to retrieve signing cert " + pkid.pks_user()}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          return "Unable to retrieve signing cert " + pkid.pks_user();
        }
        
        logdet.add(new String[]{parentkey, "info", "Signing with: " + signcertificate.getIssuerX500Principal().getName() + "/" + signcertificate.getSigAlgName()});
        logdet.add(new String[]{parentkey, "info", "Signing Serial#: " + signcertificate.getSerialNumber().toString(16)});
        logdet.add(new String[]{parentkey, "info", "Signing Expiration Window: " + signcertificate.getNotBefore() + "/" + signcertificate.getNotAfter()});
        
        
        
        Path as2filepath = null;
        File folder = new File(sourceDir);
        File[] listOfFiles = folder.listFiles();
        boolean isSuccess = false;
        
        if (listOfFiles == null || listOfFiles.length == 0) {
            logdet.add(new String[]{parentkey, "passive", "No Files in output directory " + sourceDir}); 
            writeAS2LogDetail(logdet, as2m.as2_id());
            return "No Files in output directory " + sourceDir;
        }
        for (int i = 0; i < listOfFiles.length; i++) {
            
            isSuccess = false;
            
            if (! listOfFiles[i].isFile()) {
            continue;
            }
            
            as2filepath = FileSystems.getDefault().getPath(sourceDir + "/" + listOfFiles[i].getName()); 
            logdet.add(new String[]{parentkey, "info", "Transmitting file: " + listOfFiles[i].getName()});
       
                    
        String messageid = setMessageID();
        logdet.add(new String[]{parentkey, "info", "Message ID: " + messageid});
       
        
        CloseableHttpClient client;
        
        if (as2m.as2_protocol().toLowerCase().equals("https")) {
           SSLConnectionSocketFactory sslcsf = buildCustomSSLFactory(pkstore); 
           client = HttpClientBuilder.create().setSSLSocketFactory(sslcsf).build();
        } else {
          client = HttpClients.createDefault();
        }
        byte[] filecontent;
        // String filecontent;
        try {
            filecontent = Files.readAllBytes(as2filepath);
            
           // filecontent = Files.readString(as2filepath);
        } catch (IOException ex) {
            bslog(ex);
            continue;
        }
        
        MimeBodyPart mbp = null;
        byte[] sendbytes = null;
        
        
        
        boolean isSigned = BlueSeerUtils.ConvertStringToBool(as2m.as2_signed())  ;
        boolean isEncrypted = BlueSeerUtils.ConvertStringToBool(as2m.as2_encrypted());
        // need signed, signed+enc, enc, none ....condition logic here
        
            if (filecontent != null ) {    
                    try {
                        // mbp = signData(filecontent.getBytes(StandardCharsets.UTF_8),signcertificate,key,listOfFiles[i].getName());
                        if (BlueSeerUtils.ConvertStringToBool(as2m.as2_signed())) {
                           mbp = signData(filecontent,as2m.as2_signalgo(),signcertificate,key,listOfFiles[i].getName(),as2m,contenttype);
                        } else {
                            InternetHeaders headers = new InternetHeaders();
                            headers.addHeader("Content-Type", contenttype);
                            headers.addHeader("Content-Disposition", "attachment; filename=" + listOfFiles[i].getName());
                            mbp = new MimeBodyPart(headers, filecontent);
                        }

                    } catch (Exception ex) {
                        bslog(ex);
                        continue;
                    }
            } else {
               bslog("file content is null in AS2Post");
               continue; 
            }
            
            // calculate MIC from file payload + headers ...which is BodyPart(0) of mimemultipart
            ByteArrayOutputStream aos = new ByteArrayOutputStream();
            if (isSigned) {
                MimeMultipart mmpg = (MimeMultipart) mbp.getContent();
                mmpg.getBodyPart(0).writeTo(aos);
            } else {
                mbp.writeTo(aos);
            }
            aos.close(); 
            byte[] FileWHeadersBytes = aos.toByteArray();
            String mic = hashdigest(FileWHeadersBytes, as2m.as2_micalgo());
            logdet.add(new String[]{parentkey, "info", "THE MIC: " + mic});
       
        String newboundary = getPackagedBoundary(mbp);
        
        byte[] bytesToBeEncrypted;
        
        if (isSigned && isEncrypted) {
             bytesToBeEncrypted = buildMIME(mbp, isDebug);
        } else {
             bytesToBeEncrypted = mbp.getInputStream().readAllBytes();
        }
       
        
        if (isEncrypted) {
          sendbytes = encryptData(bytesToBeEncrypted, encryptcertificate, as2m.as2_encalgo());
        } else {
           sendbytes = bytesToBeEncrypted;
        }
        
       
        
        URL urlObj = new URL(url);
        RequestBuilder rb = RequestBuilder.post();
        rb.setUri(urlObj.toURI());
        
        rb.addHeader("User-Agent", "java/application (BlueSeer Software; +http://www.blueseer.com/)"); 
        rb.addHeader("AS2-To", as2To);
        rb.addHeader("AS2-From", as2From); 
        rb.addHeader("AS2-Version", "1.2"); 
        rb.addHeader("Mime-Version", "1.0");
        rb.addHeader("Subject", "as2");
       // rb.addHeader("Accept-Encoding", "deflate, gzip, x-gzip, compress, x-compress");
        if (as2m.as2_forcesigned().equals("1")) {
        rb.addHeader("Disposition-Notification-Options", "signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1");
        }
        rb.addHeader("Disposition-Notification-To", internalURL);
        rb.addHeader("Message-ID", messageid);
        rb.addHeader("Recipient-Address", url.toString());
        rb.addHeader("EDIINT-Features", "CEM, multiple-attachments, AS2-Reliability");
        
        
      //  if (smime_encapsulate) {
            if (! isSigned && isEncrypted) {
              rb.addHeader("Content-Type", "multipart/mixed; boundary=" + "\"" + newboundary + "\"" );
            } 
            if (! isSigned && ! isEncrypted) {
              rb.addHeader("Content-Type", as2m.as2_contenttype());
              rb.addHeader("Content-Disposition", "attachment; filename=" + listOfFiles[i].getName());  
            }

            if (isSigned && ! isEncrypted) {
              rb.addHeader("Content-Type", "multipart/signed; protocol=\"application/pkcs7-signature\"; boundary=" + "\"" + newboundary + "\"" + "; micalg=sha1");
            }

            if (isSigned && isEncrypted) {
            rb.addHeader("content-type", "application/pkcs7-mime; smime-type=enveloped-data; name=smime.p7m");
            rb.addHeader("Content-Transfer-Encoding", "binary");
            rb.addHeader("content-disposition", "attachment; filename=" + "\"" + "smime.p7m" + "\"");
            rb.addHeader("connection", "close, TE");
            }        
            
        // add custom headers
        ArrayList<String> list = EDData.getAS2AttributesList(as2m.as2_id(), "httpheader");
        for (String x : list) {
                String[] h = x.split(":",-1);
                if (h != null && h.length > 1) {
                 rb.addHeader(h[0], h[1]);
                }
            }
     
        ByteArrayEntity baentity = new ByteArrayEntity(sendbytes);  // after 20251005 used for repeatable stream...for debugging prior to send
        rb.setEntity(new BufferedHttpEntity(baentity));  // after 20251005
        
        
        HttpUriRequest request = rb.build();
        
          
        if (isDebug) { 
            RequestBuilder rb_debug = rb;
            ByteArrayEntity baentity_debug = new ByteArrayEntity(bytesToBeEncrypted);  // used for repeatable stream...for debugging prior to send
            rb_debug.setEntity(new BufferedHttpEntity(baentity_debug));
            HttpUriRequest request_debug = rb_debug.build();
            String debugfile = "debugAS2http." + now + "." + Long.toHexString(System.currentTimeMillis());
            saveRequestToFile(request_debug, "temp" + "/" + debugfile);           
        }
        
        
        try (CloseableHttpResponse response = client.execute(request)) {
        if (response.getStatusLine().getStatusCode() != 200) {
            r.append(response.getStatusLine().getStatusCode()).append(": ").append(response.getStatusLine().getReasonPhrase());
        } else {
            r.append("SUCCESS: ").append(response.getStatusLine().getStatusCode()).append(": ").append(response.getStatusLine().getReasonPhrase()).append("\n");
        }
        
        
        
        HttpEntity entity = response.getEntity();
        byte[] indata = EntityUtils.toByteArray(entity);
        String result = new String(indata); 
        
        if (isDebug) {
                String filename = "response." + now + "." + Long.toHexString(System.currentTimeMillis());
                Path path = FileSystems.getDefault().getPath("temp" + "/" + filename);
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
                output.write(result);
                output.close();
        } 
        
        if (isDebug) { 
            String debugfile = "debugAS2responseHeaders." + now + "." + Long.toHexString(System.currentTimeMillis());
            Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
            Header[] headers = response.getAllHeaders();
             try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
                 
                 stream.write(r.toString().getBytes());
                 for (Header x : headers) {
                    String h = x.getName() + ": " + x.getValue() + "\n";
                    stream.write(h.getBytes());
                }
             }  
        }
        
        if (! result.isBlank()) {
            // save MDN file if present
            try {
            Header h = entity.getContentType();
            String mdncontenttype = "multipart/form-data";
            if (h != null) {
                mdncontenttype = h.getValue();
            }
            
            if (response.getFirstHeader("Content-Type").getValue().contains("signed")) {
                
            
            
            MimeMultipart mpr  = new MimeMultipart(new ByteArrayDataSource(indata, mdncontenttype));
            
            for (int z = 0; z < mpr.getCount(); z++) {
                MimeBodyPart mbpr = (MimeBodyPart) mpr.getBodyPart(z);
                if (mbpr.getContentType().contains("disposition")) {
                    String filename = "mdn." + now + "." + Long.toHexString(System.currentTimeMillis());
                    Path path = FileSystems.getDefault().getPath("edi/mdn" + "/" + filename);
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
                    String datastring = new String(mbpr.getInputStream().readAllBytes());   
                    output.write(datastring);
                    output.close();

                    // verify signature if applicable
                    // mbpr should be mimemulitpart containing two sub parts....the mdn and the signature
                    boolean isValidMDNSignature = verifyMDNSignature(indata, "multipart/signed; protocol=\"application/pkcs7-signature\"; micalg=sha-1;");

                    /*
                    Session session = Session.getDefaultInstance(new java.util.Properties()); // Or obtain a proper session
                    //InputStream myis = new ByteArrayInputStream(indata);       // if indata has headers             
                   // MimeMessage message = new MimeMessage(session, myis);      // if indata has headers
                    
                    ByteArrayDataSource dataSource = new ByteArrayDataSource(indata, "multipart/signed"); 
                    MimeMultipart mimeMultipart = new MimeMultipart(dataSource);
                    MimeMessage message = new MimeMessage(session);
                    message.setContent(mimeMultipart);
                    
                    boolean isValidMDNSignatureNew = verifyMDNSignatureNew(message, getPublicKeyAsCert(as2m.as2_signcert()));
                    */ 
                    
                    
                    // log mdn filename into parent log entry
                    updateAS2LogMDNFile(parentkey, filename);

                    Pattern p = Pattern.compile("Disposition:.*(error|failed).*");
                    Matcher m = p.matcher(datastring);
                    if (m.find()) {
                        logdet.add(new String[]{parentkey, "error", "MDN error: " + filename});
                    } else {
                       logdet.add(new String[]{parentkey, "info", "MDN processed: " + filename}); 
                       logdet.add(new String[]{parentkey, "info", "MDN valid signature: " + isValidMDNSignature});
                       isSuccess = true;
                    }
                }
            }
          } else { // unsigned
                    String filename = "mdn." + now + "." + Long.toHexString(System.currentTimeMillis());
                    Path path = FileSystems.getDefault().getPath("edi/mdn" + "/" + filename);
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
                    String datastring = new String(result);   
                    output.write(datastring);
                    output.close();
                    Pattern p = Pattern.compile("Disposition:.*(error|failed).*");
                    Matcher m = p.matcher(result);
                    if (m.find()) {
                        logdet.add(new String[]{parentkey, "error", "unsigned MDN error: " + filename}); 
                    } else {
                       logdet.add(new String[]{parentkey, "info", "unsigned MDN processed: " + filename}); 
                       isSuccess = true;
                    }  
          }

            } catch (MessagingException ex) {
              logdet.add(new String[]{parentkey, "error", " Messaging error; Bad MDN Boundary " + ex.getMessage()}); 
              writeAS2LogDetail(logdet, as2m.as2_id());
              r.append("Messaging error; Bad MDN Boundary ").append(ex.getMessage()); 
              ex.printStackTrace();
              return r.toString(); 
            }   
        } // if result is not blank
        
        r.append(result);
        
        } catch (HttpHostConnectException | ConnectTimeoutException  ex) {
          logdet.add(new String[]{parentkey, "error", " Connection refused or timeout from server "}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          r.append("Connection refused or timeout from server ");
          return r.toString();
        } catch ( UnknownHostException ex) {
          logdet.add(new String[]{parentkey, "error", " Unknown host server " + request.getURI()}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          r.append("Unknown host server ").append(request.getURI());
          return r.toString();
        } catch ( SocketException ex) {
          logdet.add(new String[]{parentkey, "error", " Socket exception connection reset " + request.getURI()}); 
          writeAS2LogDetail(logdet, as2m.as2_id());
          r.append("Socket exception connection reset ").append(request.getURI());
          return r.toString();
        }
        
      // remove file if successful
      if (isSuccess) {
          if (! isDebug) {
            Files.deleteIfExists(as2filepath);
            r.append("\n").append("status__pass");
          } else {
            r.append("\n").append("status__pass  ...debug mode file not deleted");  
          }
        
      } 
        
    } // for each file
        writeAS2LogDetail(logdet, as2m.as2_id());
        return r.toString();
    }
    
    public static byte[] buildMIME(MimeBodyPart tmpBody, boolean isDebug) throws MessagingException, IOException {
            String h = "Content-Type: " + tmpBody.getHeader("content-type")[0] + "\r\n\r\n";
            byte[] byteheader = h.getBytes();
            byte[] bytestream = tmpBody.getInputStream().readAllBytes();
            byte[] r = new byte[byteheader.length + bytestream.length];
            for (int j = 0; j < r.length; ++j) {
                r[j] = j < byteheader.length ? byteheader[j] : bytestream[j - byteheader.length];
            }
            
            if (isDebug) {
                String debugfile = "debugsigned" + "." + Long.toHexString(System.currentTimeMillis()) + ".txt";
                Path pathinput = FileSystems.getDefault().getPath("temp" + "/" + debugfile);
                try (FileOutputStream stream = new FileOutputStream(pathinput.toFile())) {
                  stream.write(r);
                }
            }
            
            return r;
    }
       
    public static SSLConnectionSocketFactory buildCustomSSLFactory(pks_mstr pks) throws Exception {
        SSLConnectionSocketFactory sslcsf = null;
        
        KeyManager[] keyManagers = null;
        
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        
        X509TrustManager defaultX509CertificateTrustManager = null;
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager x509TrustManager) {
                defaultX509CertificateTrustManager = x509TrustManager;
                break;
            }
        }
        
        // TrustManagerFactory
        X509TrustManager myTrustManager = null;
        try (FileInputStream myKeys = new FileInputStream(pks.pks_file())) {
        KeyStore myStore = KeyStore.getInstance("pkcs12");
        myStore.load(myKeys, bsmf.MainFrame.PassWord("1", pks.pks_storepass().toCharArray()).toCharArray());
        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(myStore);

        for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
                if (tm instanceof X509TrustManager x509TrustManager) {
                    myTrustManager = x509TrustManager;
                    break;
                }
        }
        

        // KeyManagerFactory
        // Long story short... KeyManagerFactory blows chunks.   Apparently you can't use a .p12 file with more than one key in it if
        // those keys have different passwords....and if said key passwords are different than store password.
        // The only way I can get this to work is to create a .p12 with a store password of 'x' and one or more private keys
        // within store that also have password 'x'...all of them!  All keys with same password that also equals store password.
        // ...yeah...a huge crock of crap...way to go Java.  :(
        // so...on the outside chance that I need a 'KeyManager'...there will need to be a pks_id of 'kmfstore' with a keystore with the above
        // password requirements.
        // NOTE:   this kfmstore is a separate entity from the 'normal' key stores that are used to filter for edi as2 partner enc/sign certs and as2 system keypairs
        // NOTE:  I suspect t his will only be useful for BlueSeer HTTPS server side...which is not offered at this time.   I can't imagine a scenario where 
        //   this would be used on the client AS2 post side...i.e...why would the foreign server need my private key.  ;)
        pks_mstr pk_kmf = getPksMstr(new String[]{"kmfstore"});
        if (! pk_kmf.pks_file().isBlank() && ! pk_kmf.pks_storepass().isBlank()) {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(pk_kmf.pks_file()), bsmf.MainFrame.PassWord("1", pk_kmf.pks_storepass().toCharArray()).toCharArray()); //my_cert.p12 is my cerfificate file 
            kmf.init(ks, bsmf.MainFrame.PassWord("1", pk_kmf.pks_storepass().toCharArray()).toCharArray());
            keyManagers = kmf.getKeyManagers();
        }
        
        
            
        }

        X509TrustManager finalDefaultTm = defaultX509CertificateTrustManager;
        X509TrustManager finalMyTm = myTrustManager;

        X509TrustManager wrapper = new X509TrustManager() {
            private X509Certificate[] mergeCertificates() {
                ArrayList<X509Certificate> resultingCerts = new ArrayList<>();
                resultingCerts.addAll(Arrays.asList(finalDefaultTm.getAcceptedIssuers()));
                resultingCerts.addAll(Arrays.asList(finalMyTm.getAcceptedIssuers()));
                return resultingCerts.toArray(new X509Certificate[resultingCerts.size()]);
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return mergeCertificates();
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    finalMyTm.checkServerTrusted(chain, authType);
                } catch (CertificateException e) {
                    finalDefaultTm.checkServerTrusted(chain, authType);
                }
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                finalDefaultTm.checkClientTrusted(mergeCertificates(), authType);
            }
        };
        
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagers, new TrustManager[] { wrapper }, null);
        SSLContext.setDefault(context);
        sslcsf = new SSLConnectionSocketFactory(context, null, null, new NoopHostnameVerifier());
        
        return sslcsf;
    }
         
    public static String code1000(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String subject = elementals[2];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
       
        StringBuilder zb = new StringBuilder();         
        zb.append("The message ").append(filename).append(" with subject ").append(subject).append(" has been received.").append("\r").append("\n");
        zb.append("Message ").append(filename).append(" was sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
        zb.append(" Message received at ").append(now).append("\r").append("\n");
        zb.append("Note: The origin and integrity of the message have been verified.").append("\r").append("\n").append("\r").append("\n");
         
        
        return zb.toString();
    }
        
    public static String code2000(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append("\r").append("\n");
            zb.append("was not signed.").append("\r").append("\n").append("\r").append("\n");
       
        
        return zb.toString();
    }
        
    public static String code2005(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String subject = elementals[2];
        String filename = elementals[3];
        String messageid = elementals[4];
        String mic = elementals[5];
        String as2id = elementals[6];
        MimeBodyPart mbp = new MimeBodyPart();
        String boundary = "";
        MimeMultipart mpInner = new MimeMultipart();
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      
        StringBuilder zb = new StringBuilder();
            zb.append("The message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: MimeMultipart is incomplete ").append("\r").append("\n").append("\r").append("\n");
          
        
        return zb.toString();
    }
    
    public static String code2010(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
       
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append("\r").append("\n");
            zb.append("failed. Error: unable to retrieve contents of File. Error:  FileBytesRead is null  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
    
    public static String code2015(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: Signature content is null  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
    
    public static String code2020(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: Invalid Signature  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
    
    public static String code3000(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: The message was transmitted with null content.  ").append("\r").append("\n").append("\r").append("\n");
        
        
        
        return zb.toString();
    }
     
    public static String code3003(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: Unable to decrypt message transmitted at <%s>.  Potential bad public key.  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
        
    public static String code3005(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
       
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: The message had unrecognizable HTTP headers.  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
    
    public static String code3007(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: The message had zero HTTP headers.  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
   
    public static String code3100(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: The message was transmitted to unknown receiver ID.  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
    
    public static String code3200(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: The message was transmitted by unknown sender ID.  ").append("\r").append("\n").append("\r").append("\n");
       
        
        return zb.toString();
    }
    
    public static String code3300(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: unable to determine sender / receiver keys  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
    
    public static String code3400(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: encryption is required  ").append("\r").append("\n").append("\r").append("\n");
        
        
        return zb.toString();
    }
        
    public static String code9999(String[] elementals, as2_mstr as2m) {
        String sender = elementals[0];
        String receiver = elementals[1];
        String filename = elementals[3];
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ISO_DATE);
        
        StringBuilder zb = new StringBuilder();
            zb.append("The Message ").append(filename).append(" sent from: ").append(sender).append(" to: ").append(receiver).append("\r").append("\n");
            zb.append(" at ").append(now).append(" failed.").append("\r").append("\n");
            zb.append("Error: Internal server error 9999 ").append("\r").append("\n").append("\r").append("\n");
       
        
        return zb.toString();
    }
        
    public static mdn createMDN(String code, String[] e, HashMap<String, String> headers, boolean isDebug, as2_mstr as2m) throws IOException, MessagingException, Exception {
        mdn x;
        int httpResponseCode = HttpServletResponse.SC_OK;
        String micalgo = as2m.as2_micalgo().toLowerCase();
        micalgo = micalgo.replace("sha-1", "sha1");
        micalgo = micalgo.replace("sha-256", "sha256");
        micalgo = (micalgo.isBlank()) ? "sha1" : micalgo;
        String status;
        String boundary = "----1234554321";
        String isSigned = "0";
        String str_human;
        
        switch (code) {
            case "1000" :  // hee haw!!!   success          
            str_human = code1000(e, as2m);
            httpResponseCode = HttpServletResponse.SC_OK;
            break;     
            
            case "2000" :  // was not signed
            str_human = code2000(e, as2m);
            httpResponseCode = HttpServletResponse.SC_OK;
            break;
            
            case "2005" :  //  Error: MimeMultipart is incomplete            
            str_human = code2005(e, as2m);
            httpResponseCode = HttpServletResponse.SC_OK;
            break;
            
            case "2010" :  // Error: unable to retrieve contents of File. Error:  FileBytesRead is null             
            str_human = code2010(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
            
            case "2015" :  // Error: Signature content is null            
            str_human = code2015(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
            
            case "2020" :  // Error: Invalid Signature             
            str_human = code2020(e, as2m);
            httpResponseCode = HttpServletResponse.SC_NOT_ACCEPTABLE;
            break;
            
            case "3000" :  // Error: The message was transmitted with null content.
            str_human = code3000(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
            
            case "3003" :  // Error: Unable to decrypt message transmitted at <%s>.  Potential bad public key.
            str_human = code3003(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
            
            case "3005" : // Error: The message had unrecognizable HTTP headers.
            str_human = code3005(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
           
            case "3007" : // Error: The message had zero HTTP headers.
            str_human = code3007(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
            
            case "3100" : // Error: The message was transmitted to unknown receiver ID.
            str_human = code3100(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
            
            case "3200" : // Error: The message was transmitted by unknown sender ID. 
            str_human = code3200(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
            
            case "3300" : // Error: unable to determine sender / receiver keys  
            str_human = code3300(e, as2m);
            httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
            break;
            
            case "3400" : // Error: encryption is required 
            str_human = code3400(e, as2m); 
            httpResponseCode = HttpServletResponse.SC_NOT_ACCEPTABLE;
            break;
                                        
            default:  // something unaccounted for...            
            str_human = code9999(e, as2m);
            httpResponseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            
        }        
        
        status = (code.equals("1000")) ? "processed" : "failed"; 
        
        StringBuilder sb_mdn = new StringBuilder();
       
        
        sb_mdn.append("Reporting-UA: BlueSeer Software").append("\r").append("\n");
        sb_mdn.append("Original-Recipient: rfc822; ").append(e[1]).append("\r").append("\n");
        sb_mdn.append("Final-Recipient: rfc822; ").append(e[1]).append("\r").append("\n");
        sb_mdn.append("Original-Message-ID: ").append(e[4]).append("\r").append("\n");
        sb_mdn.append("Disposition: automatic-action/MDN-sent-automatically; ").append(status).append("\r").append("\n");
        sb_mdn.append("Received-Content-MIC: ").append(e[5]).append(", ").append(micalgo).append("\r").append("\n").append("\r").append("\n"); 
       
        
        MimeMultipart mmp = new MimeMultipart("report");
        
        MimeBodyPart mbpmdn = new MimeBodyPart(); 
        mbpmdn.setContent(sb_mdn.toString(), "message/disposition-notification");
        
        MimeBodyPart mbphuman = new MimeBodyPart(); 
        mbphuman.setContent(str_human, "text/plain; charset=utf-8");
        
        mmp.addBodyPart(mbphuman);
        mmp.addBodyPart(mbpmdn);
                
        MimeBodyPart mbpcontainer = new MimeBodyPart();
        mbpcontainer.setContent(mmp);
            
        if (! e[6].isBlank() && e[7].equals("1")) {  // isSigned ...reassign/resuse mmp
        isSigned = "1";
        mmp = signMDNFlat(mbpcontainer, getSystemSignKeyAlt(e[6]), as2m.as2_signalgo());         
        } else {
        mmp.getBodyPart(0).setHeader("Content-Type", "text/plain; charset=utf-8");
        mmp.getBodyPart(1).setHeader("Content-Type", "message/disposition-notification");         
        }
        ContentType ct = new ContentType(mmp.getContentType());
        boundary = ct.getParameter("boundary");
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        mmp.writeTo(bOut);
        bOut.flush();
        bOut.close();
        byte[] data = bOut.toByteArray(); 
        x = new mdn(httpResponseCode, headers, new String(data, StandardCharsets.UTF_8), boundary, isSigned);       
        
        return x;
    }
    
    public record mdn(int status, HashMap<String, String> headers, String message, String boundary, String isSigned) {       
        public mdn(int i, HashMap<String, String> hm, String bs, String isSigned) { 
            this(i, hm, bs, "", isSigned);     
        } 
        public mdn(int i, HashMap<String, String> hm, String bs) { 
            this(i, hm, bs, "", "");     
        }
    }
    
    
}
