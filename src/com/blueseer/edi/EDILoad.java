/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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

/**
 *
 * @author vaughnte
 */
import com.blueseer.utl.OVData;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class EDILoad {
    
    
    
public static void main(String args[]) {
 try {
     
            boolean isDebug = false;
            if (args != null && args.length > 0) {
                if (args[0].equals("-debug")) {
                    isDebug = true;
                }
            }
            bsmf.MainFrame.setConfig();
            String inDir = OVData.getEDIInDir();
            String inArch = OVData.getEDIInArch(); 
            String ErrorDir = OVData.getEDIErrorDir(); 
               String archpath = inArch;
               File folder = new File(inDir);
               File[] listOfFiles = folder.listFiles();
               if (listOfFiles.length == 0) {
                   System.out.println("No files to process");
               }
              for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                System.out.println("processing file " + listOfFiles[i].getName());
                  if(listOfFiles[i].length() == 0) { 
                  listOfFiles[i].delete();
                  } else { 
                 String[] m = EDI.processFile(inDir + "/" + listOfFiles[i].getName(),"","","", isDebug, false, 0, 0);
                 
                 // show error if exists...usually malformed envelopes
                    if (m[0].equals("1")) {
                        System.out.println(m[1]);
                        // now move to error folder
                        Path movefrom = FileSystems.getDefault().getPath(inDir + "/" + listOfFiles[i].getName());
                        Path errortarget = FileSystems.getDefault().getPath(ErrorDir + "/" + listOfFiles[i].getName());
                        // bsmf.MainFrame.show(movefrom.toString() + "  /  " + target.toString());
                         Files.move(movefrom, errortarget, StandardCopyOption.REPLACE_EXISTING);
                         continue;  // bale from here
                    }
                    
                    // if delete set in control panel...remove file and continue;
                         if (OVData.isEDIDeleteFlag()) {
                          Path filetodelete = FileSystems.getDefault().getPath(inDir + "/" + listOfFiles[i].getName());
                          Files.delete(filetodelete);
                         }
                    
                    // now archive file
                         if (! inArch.isEmpty() && ! OVData.isEDIDeleteFlag() && OVData.isEDIArchFlag() ) {
                         Path movefrom = FileSystems.getDefault().getPath(inDir + "/" + listOfFiles[i].getName());
                         Path target = FileSystems.getDefault().getPath(inArch + "/" + listOfFiles[i].getName());
                        // bsmf.MainFrame.show(movefrom.toString() + "  /  " + target.toString());
                         Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);
                          // now remove from list
                         }
                 
                  
                  }
                }
              }
       } catch (IOException ex) {
          ex.printStackTrace();
       }
  catch (ClassNotFoundException ex) {
          ex.printStackTrace();
       }
}



} // class

