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

import static bsmf.MainFrame.decryptConfig;
import static bsmf.MainFrame.encryptConfig;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
    
}
