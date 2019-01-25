/*
 * Copyright 2015 Terry Evans Vaughn ("VCSCode").
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                  EDI.processFileCmdLine(listOfFiles[i].getName(),"","","");
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

