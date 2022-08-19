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
package com.blueseer.utl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Objects;

/**
 *
 * @author terryva
 */
public class pksUtils {
    
     public static boolean addCertToStore(String certPath, String certAlias, String storePath, String storePassword)
            throws FileNotFoundException, KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {

        if (certPath.isBlank() || certAlias.isBlank() || storePath.isBlank() || storePassword.isBlank()) {
            return false;
        } 
        
        Path sp = FileSystems.getDefault().getPath(storePath); 
        if (! sp.toFile().exists()) {
            return false;
        }
        
        Path cp = FileSystems.getDefault().getPath(certPath); 
        if (! cp.toFile().exists()) {
            return false;
        }
         
        KeyStore keystore;
        try (FileInputStream storeInputStream = new FileInputStream(storePath);
                FileInputStream certInputStream = new FileInputStream(certPath)) {
            keystore = KeyStore.getInstance("PKCS12");
            keystore.load(storeInputStream, storePassword.toCharArray());

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certFactory.generateCertificate(certInputStream);

            keystore.setCertificateEntry(certAlias, certificate);
        } 

        // now save store file
        try (FileOutputStream storeOutputStream = new FileOutputStream(storePath)) {
            keystore.store(storeOutputStream, storePassword.toCharArray());
        } 
        return true;
    }
    
}
