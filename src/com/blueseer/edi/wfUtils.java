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

import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.OVData.sendEmailwSession;
import static com.blueseer.utl.OVData.setEmailSession;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.mail.Session;

/**
 *
 * @author TerryVa
 */
public class wfUtils {
    
    public static String[] filterDir(String indir, String outdir, String archdir, String logfile, String[] doctypes, String tffile) throws FileNotFoundException, IOException {
    String[] r = new String[]{"0",""};
    
    indir = cleanDirString(indir);
    outdir = cleanDirString(outdir);
    archdir = cleanDirString(archdir);
    
    Path logpath = FileSystems.getDefault().getPath(logfile);
    BufferedWriter log = new BufferedWriter(new FileWriter(logpath.toFile(), true)); 
    
     String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
     ArrayList<String[]> trafficarray = new ArrayList<String[]>();
     File tf = new File(tffile);
     if(tf.exists()) {
          FileReader reader = new FileReader(tf);
          BufferedReader br = new BufferedReader(reader);
          String line;
          while ((line = br.readLine()) != null) {
                trafficarray.add(line.split(",", -1));  // (pattern,destdir,archdir,singlefilename)
          }
          br.close();
          reader.close();
          
     } else {
         log.write(now + " No Traffic File");
         log.write("\n");
         log.close();
         log = null;
         r[0] = "1";
         r[1] = now + " No Traffic File";
         return r;
     }
     
     // check trafficarray...make sure 4 elements per row
     boolean isBad = false;
     for (String[] s : trafficarray) {
         if (s.length != 4) {
             isBad = true;
         }
     }
     if (isBad) {
         log.write(now + " Bad Format Traffic File");
         log.write("\n");
         log.close();
         log = null;
         r[0] = "1";
         r[1] = now + " Bad Format Traffic File";
         return r;
     }
     
     
     
     
     String outfile;
     String newoutdir;
    if (! indir.isEmpty() && doctypes.length > 0) {
         try {   
               File folder = new File(indir);
               File[] listOfFiles = folder.listFiles();
               if (listOfFiles.length == 0) {
                   log.write(now + ", No files to process,,,,,,,,,,,,,");
                   log.write("\n");
                   log.close();
                   log = null;
                    r[0] = "0";
                    r[1] = now + ", No files to process,,,,,,,,,,,,,";
                    return r;
               }
              
              r[1] = now + " " + " File Count=" + listOfFiles.length ;
               
              for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
               // System.out.println("file: " + listOfFiles[i].getName());
                  if(listOfFiles[i].length() == 0) { 
                  log.write(now + "," + listOfFiles[i].getName() + ",zerosize,,,,,,,,,,,," ); 
                  log.write("\n");
                  listOfFiles[i].delete();
                  } else { 
                  outfile = listOfFiles[i].getName();
                  Path oldpath = FileSystems.getDefault().getPath(listOfFiles[i].getPath());
                  Path newpath = FileSystems.getDefault().getPath(outdir + listOfFiles[i].getName());
                  
                  String[] m = EDI.filterFileWF(listOfFiles[i].getPath(), outdir, log, doctypes, trafficarray);
                  
                  
                  if (! m[4].isEmpty())  {
                      newoutdir = m[4]; 
                      newpath = FileSystems.getDefault().getPath(newoutdir + listOfFiles[i].getName());
                  }
                  
                  
                    
                    if (m != null && m[0].equals("0")) {
			Files.copy(oldpath, newpath, StandardCopyOption.REPLACE_EXISTING);
                    } else if (m != null && m[0].equals("9")) {
                        //mixed bag
                        String[] keepers = m[1].split(",",-1);
                        EDI.reduceFile(Paths.get(listOfFiles[i].getPath()), outdir, keepers);
                        //Files.copy(oldpath, newpath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        log.write(now + "," + listOfFiles[i].getName() + "," + m[1]);
                        log.write("\n");
                    }
                    if (! archdir.isEmpty()) {
                       Path archivepath = FileSystems.getDefault().getPath(archdir + listOfFiles[i].getName());
                       Files.copy(oldpath, archivepath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    listOfFiles[i].delete(); // now delete file
                  }
                }
              }
              
        listOfFiles = null;      
       } catch (IOException ex) {
          ex.printStackTrace();
       }
    }
    
    log.close();
    log = null;
    
return r; 
}
 
    public static String[] trafficDir(String indir, String logfile, String tffile) throws IOException {
        
    String[] r = new String[]{"0",""};
    
    indir = cleanDirString(indir);
    
    Path logpath = FileSystems.getDefault().getPath(logfile);
    BufferedWriter log = new BufferedWriter(new FileWriter(logpath.toFile(), true)); 
    
     String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
     ArrayList<String[]> trafficarray = new ArrayList<String[]>();
     File tf = new File(tffile);
     if(tf.exists()) {
          FileReader reader = new FileReader(tf);
          BufferedReader br = new BufferedReader(reader);
          String line = "";
          while ((line = br.readLine()) != null) {
                trafficarray.add(line.split(",", -1));  // (pattern,destdir,archdir,singlefilename,deleteOrignal)
          }
          br.close();
          reader.close();
     } else {
         log.write(now + " No Traffic File");
         log.write("\n");
         log.close();
         r[0] = "1";
         r[1] = now + " No Traffic File";
         return r;
     }
     
     // check trafficarray...make sure 5 elements per row
     // (pattern,destdir,archdir,singlefilename,deleteOrignal)
     boolean isBad = false;
     for (String[] s : trafficarray) {
         if (s.length != 5) {
             isBad = true;
         }
     }
     if (isBad) {
         log.write(now + " Bad Format Traffic File");
         log.write("\n");
         log.close();
         r[0] = "1";
         r[1] = now + " Bad Format Traffic File";
         return r;
     }
     
      
      FileFilter byfiletype = new FileFilter() {
             @Override
             public boolean accept(File f) {
                 // return name.endsWith(".txt");
                 return f.isFile();
             }
         };
     
     // OK...now let's loop through indir for files and compare against traffic file
     String outdir; 
     String archdir;
     String singlefile;
     boolean match = false;
     boolean deleteFile = false;
     Path newpath = null;
    
         try {   
              File folder = new File(indir);
              File[] listOfFiles = folder.listFiles(byfiletype);
                  
               if (listOfFiles.length == 0) {
                   log.write(now + " " + listOfFiles.length + " No files to process");
                   log.write("\n");
                   log.close();
                    r[0] = "0";
                    r[1] = now + " " + listOfFiles.length + " No files to process";
                    return r;
               }
              
              r[1] = now + " " + " File Count=" + listOfFiles.length; 
              log.write(now + " " + " File Count=" + listOfFiles.length );
              log.write("\n");
               
               
              for (int i = 0; i < listOfFiles.length; i++) {
                match = false;
                deleteFile = false;
                if (listOfFiles[i].isFile()) {
                  if(listOfFiles[i].length() == 0) { 
                  log.write(now + " " + listOfFiles[i].getName() + " zerosize" );
                  log.write("\n");
                  listOfFiles[i].delete();
                  } else { 
                  for (String[] s : trafficarray) {
                      if (listOfFiles[i].getName().matches(s[0])) {
                          // found match
                          match = true;
                          singlefile = s[3];
                          outdir = s[1];
                          if (! s[4].isEmpty() && s[4].toLowerCase().equals("yes")) {
                              deleteFile = true;
                          }
                          // if destination path is not valid...bail loop
                          if (! Files.isDirectory(FileSystems.getDefault().getPath(outdir))) {
                            log.write(now + " " + " destination dir does not exist for " + listOfFiles[i].getName()  );      
                            log.write("\n");
                            match = false;
                            break;
                          } 
                          if (!s[2].isEmpty()) {
                              archdir = s[2];
                          } else {
                              archdir = "";
                          }
                          
                          Path oldpath = FileSystems.getDefault().getPath(listOfFiles[i].getPath());
                          
                          if (singlefile.isEmpty()) {
                              newpath = FileSystems.getDefault().getPath(outdir + listOfFiles[i].getName());
                              
                              Files.copy(oldpath, newpath, StandardCopyOption.REPLACE_EXISTING); 
                              
                              
                          } else {
                              newpath = Paths.get(outdir + singlefile);
                              byte[] data = Files.readAllBytes(oldpath);
                              Files.write(newpath, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                          }
                           if (! archdir.isEmpty()) {
                             Path archivepath = FileSystems.getDefault().getPath(archdir + listOfFiles[i].getName());
                             Files.copy(oldpath, archivepath, StandardCopyOption.REPLACE_EXISTING); 
                          }
                          break;
                      }
                  }    
                    
                    
                    // now delete if match found...otherwise leave file in place.
                    if (match) {
                        if (deleteFile) {
                            listOfFiles[i].delete(); // now delete file
                            log.write(now + " " + " copy file " + listOfFiles[i].getName() + " to " + newpath.toString() + " file placed and deleted"); 
                            log.write("\n"); 
                        } else {
                            log.write(now + " " + " copy file " + listOfFiles[i].getName() + " to " + newpath.toString() + " file placed but not deleted"); 
                            log.write("\n");
                        }
                    } else {
                    log.write(now + " " + listOfFiles[i].getName() + " file cannot be placed" );     
                    log.write("\n");
                    }
                    
                    
                  } // else it's non-zero size...process it
                } // yep it's a file
              } // for all files found
       } catch (IOException ex) {
          ex.printStackTrace();
          log.write(now + " " + " IOException" ); 
          log.write("\n");
       }
       
    log.close();     
    return r;
 }
 
    public static String[] emailDir(String indir, String logfile, String tffile, String archdir, String from) throws IOException {
        
    String[] r = new String[]{"0",""};
      
    indir = cleanDirString(indir);
    archdir = cleanDirString(archdir);
    
    
    Path logpath = FileSystems.getDefault().getPath(logfile);
    BufferedWriter log = new BufferedWriter(new FileWriter(logpath.toFile(), true)); 
    
     String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
     ArrayList<String[]> trafficarray = new ArrayList<String[]>();
     File tf = new File(tffile);
     if(tf.exists()) {
          FileReader reader = new FileReader(tf);
          BufferedReader br = new BufferedReader(reader);
          String line = "";
          while ((line = br.readLine()) != null) {
                trafficarray.add(line.split(":", -1));  // (pattern,destdir,archdir,singlefilename,deleteOrignal)
          }
          br.close();
          reader.close();
     } else {
         log.write(now + " No Traffic File");
         log.write("\n");
         log.close();
         r[0] = "1";
         r[1] = now + " No Traffic File";
         return r;
     }
     
     // check trafficarray...make sure 5 elements per row
     // (pattern,destdir,archdir,singlefilename,deleteOrignal)
     boolean isBad = false;
     for (String[] s : trafficarray) {
         if (s.length != 3) {
             isBad = true;
         }
     }
     if (isBad) {
         log.write(now + " Bad Format Traffic File");
         log.write("\n");
         log.close();
         r[0] = "1";
         r[1] = now + " Bad Format Traffic File";
         return r;
     }
     
      
      FileFilter byfiletype = new FileFilter() {
             @Override
             public boolean accept(File f) {
                 // return name.endsWith(".txt");
                 return f.isFile();
             }
         };
     
     // OK...now let's loop through indir for files 
    
         try {   
              File folder = new File(indir);
              File[] listOfFiles = folder.listFiles(byfiletype);
                  
               if (listOfFiles.length == 0) {
                   log.write(now + " " + listOfFiles.length + " No files to process");
                   log.write("\n");
                   log.close();
                    r[0] = "0";
                    r[1] = now + " " + listOfFiles.length + " No files to process";
                    return r;
               }
              
              r[1] = now + " " + " File Count=" + listOfFiles.length; 
              log.write(now + " " + " File Count=" + listOfFiles.length );
              log.write("\n");
               
             
              
              boolean sent = false;
              String message = "This is an automated email alert." + '\n';
             
              Session session = setEmailSession();
              
              for (int i = 0; i < listOfFiles.length; i++) {
            	 sent = false;
                 if (listOfFiles[i].isFile()) {
                	 for (String[] e : trafficarray) {                		 
                		 if (listOfFiles[i].getName().contains(e[0])) {
                			 // found match
                			 sent = true;
                			 Path filepath = FileSystems.getDefault().getPath(indir + listOfFiles[i].getName());
                			 Path archpath = FileSystems.getDefault().getPath(archdir + listOfFiles[i].getName() + "." + now);
                			 log.write(now + " sending email for file: " + listOfFiles[i].getName() + " to " + e[1] +  "\n");
                			 log.write("\n");
                                         sendEmailwSession(session, from, e[2], e[1], message, filepath.toString());
                			 Files.copy(filepath, archpath, StandardCopyOption.REPLACE_EXISTING);
                			 Files.delete(filepath);
                			 break;
                		 }
                		 
                	 }
                 	
                 }
                 if (! sent) {
                    log.write(now + " no match found in config file...skipping " + listOfFiles[i].getName() +  "\n");
                    log.write("\n");
                 }
             }
       } catch (IOException ex) {
          ex.printStackTrace();
          log.write(now + " " + " IOException" ); 
          log.write("\n");
          r[0] = "1";
          r[1] = now + " IOException " + ex.getMessage();
       }
       
    log.close();     
    return r;
 }
 
    
}
