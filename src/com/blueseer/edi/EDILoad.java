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
import java.io.*;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class EDILoad {
public static void main(String args[]) {
 try {
             bsmf.MainFrame.setConfig();
               String archpath = "/apps/edi/rawin/";
               File folder = new File("/apps/edi/ovin");
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
                //  EDI.processFile(listOfFiles[i].getName(),"","","");
                  EDI.processFile(listOfFiles[i].getName(),"","","");
                  listOfFiles[i].renameTo(new File(archpath + listOfFiles[i].getName()));
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

