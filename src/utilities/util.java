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
package utilities;

import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.decryptConfig;
import static bsmf.MainFrame.encryptConfig;
import static bsmf.MainFrame.tags;
import static com.blueseer.edi.apiUtils.verifySignature;
import static com.blueseer.utl.OVData.updateSet;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Security;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author terryva
 */
public class util {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String pass = "";
        
    String configfile = "bs.cfg";
    if (args != null && args.length > 0) {
    int i = 0;
        for (String s : args) {
            if (s.equals("-config")) {
            configfile = args[i+1];
            }
            i++;
        }
    }            
    
    bsmf.MainFrame.setConfig(configfile);
    tags = ResourceBundle.getBundle("resources.bs", Locale.getDefault());
        
        for (int i = 0; i < args.length ;i++) {
            if (args[i].substring(0,1).equals("-")) {
              
               if ( args.length > i+1 && args[i+1] != null) {
                
                 switch (args[i].toString().toLowerCase()) {
        
                    case "-enc" :
                        pass = args[i+1]; 
                        encrypt(pass);
                        break;
                    case "-dec" :
                        pass = args[i+1]; 
                        decrypt(pass);
                        break;
                    case "-set" :
                        set(args[i+1], args[i+2]);
                        break;  
                    case "-vsig" :
                        verifyAS2Sig(args[i+1], args[i+2]);
                        break;      
                    default:
                        System.out.println("Bad Qualifier");
                        System.exit(1);
                 }
                                    
               } else {
                  System.out.println("missing value for qualifier " + args[i]);
                  System.exit(1);
               }
            }
         }
        
        
    }
    
    public static void decrypt(String p) throws Exception {
        if (Files.exists(FileSystems.getDefault().getPath("bs.cfg"))) {
                byte[] tac = Files.readAllBytes(FileSystems.getDefault().getPath("bs.cfg"));
                String lines = new String(tac, StandardCharsets.UTF_8);
                String r = decryptConfig(lines, p);
                
                if (! r.isBlank()) {
                    Path outpath = FileSystems.getDefault().getPath("bs.cfg.dec");
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath.toFile())));
                    output.write(r);
                    output.close();
                    Files.move(FileSystems.getDefault().getPath("bs.cfg.dec"),FileSystems.getDefault().getPath("bs.cfg"), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("bs.cfg dec complete");
                }
                
        } 
    }
    
    public static void encrypt(String p) throws Exception {
        
        if (Files.exists(FileSystems.getDefault().getPath("bs.cfg"))) {
                byte[] tac = Files.readAllBytes(FileSystems.getDefault().getPath("bs.cfg"));
                String lines = new String(tac, StandardCharsets.UTF_8);
                String r = encryptConfig(lines, p);
                
                if (! r.isBlank()) {
                    Path outpath = FileSystems.getDefault().getPath("bs.cfg.enc");
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath.toFile())));
                    output.write(r);
                    output.close();
                    Files.move(FileSystems.getDefault().getPath("bs.cfg.enc"),FileSystems.getDefault().getPath("bs.cfg"), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("bs.cfg enc complete");
                }
                
        } 
        
    }
    
    public static void set(String x, String y) {
        updateSet(x,y);
    }
    
    public static void verifyAS2Sig(String source, String pksid) {
        Path filepath = FileSystems.getDefault().getPath(source);
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        try {
            byte[] data =  Files.readAllBytes(filepath);
            byte[] Signature = null;
            byte[] FileWHeadersBytes = null;
            boolean isvalid = false;
            MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(data, "application/pkcs7-signature; name=smime.p7s"));
            for (int i = 0; i < mp.getCount(); i++) {
                MimeBodyPart mbp = (MimeBodyPart) mp.getBodyPart(i);                     
                    if (mbp == null) {
                        continue;
                    }
                System.out.println("sub part: " + i + " " + mbp.getContentType());
                if (! mbp.getContentType().toLowerCase().startsWith("application/pkcs7-signature")) { // must be non sig file
                    ByteArrayOutputStream aos = new ByteArrayOutputStream();
                      mp.getBodyPart(0).writeTo(aos);
                      aos.close(); 
                      FileWHeadersBytes = aos.toByteArray();
                }    
                    
                if (mbp.getFileName() != null && mbp.getFileName().equals("smime.p7s")) { // must be sig
                        Signature = IOUtils.toByteArray((InputStream) mbp.getContent());
                }    
            }
            
            if (Signature != null && FileWHeadersBytes != null) {
                isvalid = verifySignature(FileWHeadersBytes, Signature, true, pksid); 
            }
            
            System.out.println("signature verified: " + isvalid);
            
            
        } catch (IOException ex) {
            bslog(ex);
        } catch (MessagingException ex) {
            bslog(ex);
        }
        
    }
    
}
