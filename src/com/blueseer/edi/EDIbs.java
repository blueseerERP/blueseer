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

/**
 *
 * @author vaughnte
 */
import static bsmf.MainFrame.tags;
import static com.blueseer.adm.admData.runFTPClient;
import static com.blueseer.utl.BlueSeerUtils.isParsableToInt;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class EDIbs {
    
    public static DateFormat dfdate = new SimpleDateFormat("yyyyMMddHHmm");
    public static Date now = new Date();
    public static boolean isDebug = false;
    
public static void main(String args[]) throws IOException {
 
    //first things first...escape clause
    // check for lock file...if exists then bail
		Path lockpath = Paths.get("lock.txt");
		if (Files.exists(lockpath)) {
			System.out.println("lock file...exiting");
			return;
		}
    
    
    // set config
   // bsmf.MainFrame.setConfig();
    bsmf.MainFrame.setConfig();
    tags = ResourceBundle.getBundle("resources.bs", Locale.getDefault());
    // lets process the arguments
    String[] vs = checkargs(args);
    String infile = vs[0].toString();
    String outfile = vs[1].toString();
    String indir = vs[2].toString();
    String outdir = vs[3].toString();
    String map = vs[4].toString();
    String isOverride = vs[5].toString();
    String[] doctypes = vs[6].split(",");
    String prog = vs[7].toString();
    String archdir = vs[8].toString();
    
    String myargs = String.join(",", args);
    
   
    switch (prog) {
        
        case "single" :
            processSingle(infile, outfile, map, isOverride);
            break;
        case "multiple" :
            processMultiple(indir, outdir, map, isOverride);
            break;
        case "filterFile" :
            filterFile(infile, outfile, doctypes);
            break; 
        case "extractFile" :
            extractFile(map, indir, outdir);
            break;       
        case "filterDir" :
            filterDir(indir, outdir, archdir, doctypes, map);
            break;   
        case "trafficDir" :
            trafficDir(indir, map);
            break;   
        case "purgeDir" :
            purgeDir(indir, map, isOverride);
            break; 
        case "purgeDirRecurse" :
            purgeDirRecurse(indir, map, isOverride);
            break;    
        case "ftpClient" :
            ftpClient(map);
            break;       
        default:
            System.out.println("Unable to process arguments " + myargs);
        
    }
      
    System.exit(0);
    
   
}



 public static String[] checkargs(String[] args) {
        List<String> legitargs = Arrays.asList("-if", "-of", "-id", "-od", "-m", "-x", "-ff", "-fd", "-ad", "-td", "-tf", "-e", "-debug", "-ftp", "-pd", "-pdr" );
     
        String[] vals = new String[9]; // last element is the program type (single or mulitiple)
        Arrays.fill(vals, "");
        
        String myargs = String.join(",", args);
        
         // single and multiple are mutually exclusive...if both infile and indir or set...then balk
         boolean isSingle = false ;
         boolean isMultiple = false ;
         for (String s : args) {
             if (s.equalsIgnoreCase("-if") || s.equalsIgnoreCase("-of"))
                 isSingle = true;
             if (s.equalsIgnoreCase("-id") || s.equalsIgnoreCase("-od"))
                 isMultiple = true;
         }
         if (isSingle && isMultiple) {
             System.out.println("Cannot use both -if and -id together " + myargs);
             System.exit(1);
         }
         if (isSingle) 
             vals[7] = "single";
         if (isMultiple) 
             vals[7] = "multiple";
         
         
         // initialize override to false
         vals[5] = "false";
            
         
         
         
        // now process the qualifiers
        for (int i = 0; i < args.length ;i++) {
            if (args[i].substring(0,1).equals("-")) {
              
              // first make sure -xx argument qualifier is legit  
              if (! legitargs.contains(args[i])) {
                  System.out.println("Bad Qualifier");
                  System.exit(1);
              }
              
              
              
               if ( (args.length > i+1 && args[i+1] != null) || (args[i].toString().equals("-x")) || (args[i].toString().equals("-ff") || (args[i].toString().equals("-fd"))) ) {
                
                 switch (args[i].toString().toLowerCase()) {
        
                    case "-if" :
                        vals[0] = args[i+1]; 
                        break;
                    case "-of" :
                        vals[1] = args[i+1]; 
                        break;
                    case "-id" :
                        vals[2] = args[i+1]; 
                        break;
                    case "-od" :
                        vals[3] = args[i+1]; 
                        break;
                    case "-m" :
                        vals[4] = args[i+1]; 
                        break;
                    case "-x" :
                        vals[5] = "true"; 
                        break; 
                    case "-e" :
                        vals[4] = args[i+1];
                        vals[7] = "extractFile"; 
                        break;      
                    case "-ff" :
                        vals[6] = args[i+1];
                        vals[7] = "filterFile"; 
                        break;  
                    case "-fd" :
                        vals[6] = args[i+1];
                        vals[7] = "filterDir"; 
                        break;  
                    case "-td" :
                        vals[2] = args[i+1];
                        vals[7] = "trafficDir"; 
                        break;  
                    case "-pd" :
                        vals[2] = args[i+1];
                        vals[4] = args[i+2];
                        vals[5] = args[i+3];
                        vals[7] = "purgeDir"; 
                        break;  
                    case "-pdr" :
                        vals[2] = args[i+1];
                        vals[4] = args[i+2];
                        vals[5] = args[i+3];
                        vals[7] = "purgeDirRecurse"; 
                        break;          
                    case "-tf" :
                        vals[4] = args[i+1];
                        break;      
                    case "-ad" :
                        vals[8] = args[i+1]; 
                        break;
                    case "-debug" :
                        isDebug = true; 
                        break;  
                    case "-ftp" :
                        vals[4] = args[i+1];
                        vals[7] = "ftpClient"; 
                        break;      
                    default:
                        System.out.println("Unable to process arguments " + myargs);
                        System.exit(1);
                 }
                                    
               } else {
                  System.out.println("missing value for qualifier " + myargs);
                  System.exit(1);
               }
            }
             
           
         }
        return vals;
    }

 public static void processSingle(String infile, String outfile, String map, String isOverride) {
      // case of single input and output file    
    if (! infile.isEmpty() && ! outfile.isEmpty() ) {
        try {
            EDI.processFile(infile, map, outfile, isOverride, isDebug, false, 0, 0); 
        } catch (IOException ex) {
           ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
 }
 
 public static void processMultiple(String indir, String outdir, String map, String isOverride) {
     // case of multiple files....directory in and directory out 
    if (! indir.isEmpty() && ! outdir.isEmpty() && ! map.isEmpty()) {
         try {   
               File folder = new File(indir);
               File[] listOfFiles = folder.listFiles();
               if (listOfFiles.length == 0) {
                   System.out.println("No files to process");
                   System.exit(1);
               }
              for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                System.out.println("processing file " + listOfFiles[i].getName());
                  if(listOfFiles[i].length() == 0) { 
                  listOfFiles[i].delete();
                  } else { 
                  EDI.processFile(listOfFiles[i].getName(), map, "", isOverride, isDebug, false, 0, 0);
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
 }
 
 public static void extractFile(String key, String indir, String outdir) throws IOException {
     // case of multiple files....directory in and directory out 
     boolean isMatch = false;
    
     if (! indir.isEmpty() && ! outdir.isEmpty() && ! key.isEmpty()) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(FileSystems.getDefault().getPath(indir))) {
            int f = 0;
            for (Path path : stream) {
                isMatch = false;
                if (! Files.isDirectory(path)) {
                    f++;
                    List<String> fc = Files.readAllLines(path);
                     for (String x : fc ) {
                         if (x.contains(key)) {
                             isMatch = true;
                             break;
                         }
                     }
                 if (isMatch) {
                     Path newpath = FileSystems.getDefault().getPath(outdir + "/" + path.getFileName());
                     Files.copy(path, newpath);
                 }
                }
            }
            if (f == 0) {
                   System.out.println("No files to process");
            }
        }  
    } 
 }
 
 public static void ftpClient(String key) {
     
     runFTPClient(key); 
 }
 
 public static void filterFile(String infile, String outfile, String[] doctypes) {
      // case of single input and output file THIS NEEDS TO BE REVISTED!!!!!   
    if (! infile.isEmpty()) {
        try {
           String[] m = EDI.filterFile(infile, outfile, doctypes, null);
           if (m != null && m[0].equals("1")) {
               System.out.println(m[1]);
           }
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }
 }
  
 public static void filterDir(String indir, String outdir, String archdir, String[] doctypes, String map) throws FileNotFoundException, IOException {
     // case of multiple files....directory in and directory out 
    
      // indir is the directory to traffic
     // map is the definition file used to traffic file patterns to destinations
     // the map (traffic file) is comma delimited (pattern,destdir,archdir,singlefilename)
     
     // read traffic file...aka map...exit if no file
     ArrayList<String[]> trafficarray = new ArrayList<String[]>();
     File tf = new File(map);
     if(tf.exists()) {
          FileReader reader = new FileReader(tf,StandardCharsets.UTF_8);
          BufferedReader br = new BufferedReader(reader);
          String line = "";
          while ((line = br.readLine()) != null) {
                trafficarray.add(line.split(",", -1));  // (pattern,destdir,archdir,singlefilename)
          }
          br.close();
          reader.close();
     } else {
         System.out.println(dfdate.format(now) + " No Traffic File");
         System.exit(1);
     }
     
     // check trafficarray...make sure 4 elements per row
     boolean isBad = false;
     for (String[] s : trafficarray) {
         if (s.length != 4) {
             isBad = true;
         }
     }
     if (isBad) {
       System.out.println(dfdate.format(now) + " Bad Format Traffic File");
         System.exit(1);  
     }
     
     
     
     
     String outfile = "";
     outdir = outdir.replace("\\","\\\\"); 
     String newoutdir = "";
    if (! indir.isEmpty() && doctypes.length > 0) {
         try {   
               File folder = new File(indir);
               File[] listOfFiles = folder.listFiles();
               if (listOfFiles.length == 0) {
                   System.out.println(dfdate.format(now) + ", No files to process,,,,,,,,,,,,,");
                   System.exit(1);
               }
               long starttime = System.currentTimeMillis(); 
              for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
               // System.out.println("file: " + listOfFiles[i].getName());
                  if(listOfFiles[i].length() == 0) { 
                  System.out.println(dfdate.format(now) + "," + listOfFiles[i].getName() + ",zerosize,,,,,,,,,,,," ); 
                  listOfFiles[i].delete();
                  } else { 
                  outfile = listOfFiles[i].getName();
                  Path oldpath = Paths.get(listOfFiles[i].getPath());
                  Path newpath = Paths.get(outdir + listOfFiles[i].getName());
                  
                  String[] m = EDI.filterFile(listOfFiles[i].getPath(), outdir, doctypes, trafficarray);
                  
                  
                  if (! m[4].isEmpty())  {
                      newoutdir = m[4].replace("\\","\\\\"); 
                      newpath = Paths.get(newoutdir + listOfFiles[i].getName());
                  }
                  
                  
                    
                    if (m != null && m[0].equals("0")) {
			Files.copy(oldpath, newpath, StandardCopyOption.REPLACE_EXISTING);
                    } else if (m != null && m[0].equals("9")) {
                        //mixed bag
                        String[] keepers = m[1].split(",",-1);
                        EDI.reduceFile(Paths.get(listOfFiles[i].getPath()), outdir, keepers);
                        //Files.copy(oldpath, newpath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        System.out.println(dfdate.format(now) + "," + listOfFiles[i].getName() + "," + m[1]);
                    }
                    if (! archdir.isEmpty()) {
                       Path archivepath = Paths.get(archdir + listOfFiles[i].getName());
                       Files.copy(oldpath, archivepath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    listOfFiles[i].delete(); // now delete file
                  }
                }
              }
              long duration = System.currentTimeMillis() - starttime;
              System.out.println(dfdate.format(now) + " execution time = " + duration + " millsecs" );
       } catch (IOException ex) {
          ex.printStackTrace();
       }
    } 
 }
 
 public static void trafficDir(String indir, String map) throws IOException {
     // indir is the directory to traffic
     // map is the definition file used to traffic file patterns to destinations
     // the map (traffic file) is comma delimited (pattern,destdir,archdir,singlefilename,delete(yes,no))
     
     // read traffic file...aka map...exit if no file
     ArrayList<String[]> trafficarray = new ArrayList<String[]>();
     File tf = new File(map);
     if(tf.exists()) {
          FileReader reader = new FileReader(tf,StandardCharsets.UTF_8);
          BufferedReader br = new BufferedReader(reader);
          String line = "";
          while ((line = br.readLine()) != null) {
                trafficarray.add(line.split(",", -1));  // (pattern,destdir,archdir,singlefilename)
          }
          br.close();
          reader.close();
     } else {
         System.out.println(dfdate.format(now) + " No Traffic File");
         System.exit(1);
     }
     
     // check trafficarray...make sure 4 elements per row
     boolean isBad = false;
     for (String[] s : trafficarray) {
         if (s.length != 5) {
             isBad = true;
         }
     }
     if (isBad) {
       System.out.println(dfdate.format(now) + " Bad Format Traffic File");
         System.exit(1);  
     }
     
     
      FilenameFilter byfilename = new FilenameFilter() {
             @Override
             public boolean accept(File f, String name) {
                 // We want to find only .c files
                 return name.endsWith(".txt");
             }
         };
      
      FileFilter byfiletype = new FileFilter() {
             @Override
             public boolean accept(File f) {
                 // We want to find only .c files
                 return f.isFile();
             }
         };
     
     // OK...now let's loop through indir for files and compare against traffic file
     String outfile = "";
     String outdir = ""; 
     String archdir = "";
     String singlefile = "";
     boolean match = false;
     boolean deleteFile = false;
     Path newpath = null;
    
         try {   
              File folder = new File(indir);
              File[] listOfFiles = folder.listFiles(byfiletype);
                  
               if (listOfFiles.length == 0) {
                   System.out.println(dfdate.format(now) + " " + listOfFiles.length + " No files to process");
                   System.exit(1);
               }
               
              System.out.println(dfdate.format(now) + " " + " File Count=" + listOfFiles.length );
              long starttime = System.currentTimeMillis(); 
               
              for (int i = 0; i < listOfFiles.length; i++) {
                match = false;
                deleteFile = false;
                if (listOfFiles[i].isFile()) {
                  if(listOfFiles[i].length() == 0) { 
                  System.out.println(dfdate.format(now) + " " + listOfFiles[i].getName() + " zerosize" ); 
                  listOfFiles[i].delete();
                  } else { 
                  for (String[] s : trafficarray) {
                      if (listOfFiles[i].getName().contains(s[0])) {
                          // found match
                          match = true;
                          singlefile = s[3];
                          outdir = s[1].replace("\\","\\\\");
                          if (! s[4].isEmpty() && s[4].toLowerCase().equals("yes")) {
                              deleteFile = true;
                          }
                          // if destination path is not valid...bail loop
                          if (! Files.isDirectory(Paths.get(outdir))) {
                            System.out.println(dfdate.format(now) + " destination dir does not exist for " + listOfFiles[i].getName()  );      
                            match = false;
                            break;
                          } 
                          if (!s[2].isEmpty()) {
                              archdir = s[2].replace("\\","\\\\");
                          } else {
                              archdir = "";
                          }
                          
                          Path oldpath = Paths.get(listOfFiles[i].getPath());
                          
                          if (singlefile.isEmpty()) {
                              newpath = Paths.get(outdir + listOfFiles[i].getName());
                              
                              Files.copy(oldpath, newpath, StandardCopyOption.REPLACE_EXISTING); 
                              
                              
                          } else {
                              newpath = Paths.get(outdir + singlefile);
                              byte[] data = Files.readAllBytes(oldpath);
                              Files.write(newpath, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                          }
                           if (! archdir.isEmpty()) {
                             Path archivepath = Paths.get(archdir + listOfFiles[i].getName());
                             Files.copy(oldpath, archivepath, StandardCopyOption.REPLACE_EXISTING); 
                          }
                          break;
                      }
                  }    
                    
                    
                    // now delete if match found...otherwise leave file in place.
                    if (match) {
                        if (deleteFile) {
                            listOfFiles[i].delete(); // now delete file
                             System.out.println(dfdate.format(now) + " copy file " + listOfFiles[i].getName() + " to " + newpath.toString() + " file placed and deleted"); 
                         } else {
                            System.out.println(dfdate.format(now) + " copy file " + listOfFiles[i].getName() + " to " + newpath.toString() + " file placed but not deleted"); 
                        }
                    } else {
                    System.out.println(dfdate.format(now) + " " + listOfFiles[i].getName() + " file cannot be placed" );     
                    }
                    
                    
                  } // else it's non-zero size...process it
                } // yep it's a file
              } // for all files found
              long duration = System.currentTimeMillis() - starttime;
              System.out.println(dfdate.format(now) + " execution time = " + duration + " millsecs" );
              
       } catch (IOException ex) {
          ex.printStackTrace();
          System.out.println(dfdate.format(now) + " " + " IOException" ); 
       }
    
 }
 
 public static void purgeDir(String dir, String days, String flag) throws IOException {
     
        int daysBack = 0;
        boolean isDelete = false;
        if (flag != null && flag.equals("dElEtE")) {
            isDelete = true;
        }
        if (isParsableToInt(days)) {
            daysBack = Integer.valueOf(days);
        } else {
         System.out.println("parameter 2 is not an integer");
         System.exit(1);
        }
        File folder = new File(dir);
        if (! folder.isDirectory()) {
         System.out.println("parameter 1 is not a valid directory");
         System.exit(1);   
        }
        
        File[] listOfFiles = folder.listFiles();
        long starttime = System.currentTimeMillis(); 
        long z = starttime - ((long)daysBack * 24L * 60L * 60L * 1000L);
        int count = 0;
        long dt = new Date().getTime();
        
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].lastModified() < z) {
                    count++;
                    if (isDelete) {
                    Path filepath = Paths.get(dir + "\\" + listOfFiles[i].getName());
                    Files.delete(filepath);
                    }
                }
            }
            
        }
        long duration = System.currentTimeMillis() - starttime;
        System.out.println("execution time: " + duration + " millsecs" );
        System.out.println("delete is set to: " + isDelete);
        System.out.println("file count is: " + listOfFiles.length);
        System.out.println("delete count is: " + count);
 }

 public static void purgeDirRecurse(String dir, String days, String flag) throws IOException {
     
        int daysBack = 0;
        boolean isDelete = false;
        if (flag != null && flag.equals("dElEtE")) {
            isDelete = true;
        }
        if (isParsableToInt(days)) {
            daysBack = Integer.valueOf(days);
        } else {
         System.out.println("parameter 2 is not an integer");
         System.exit(1);
        }
        File folder = new File(dir);
        if (! folder.isDirectory()) {
         System.out.println("parameter 1 is not a valid directory");
         System.exit(1);   
        }
        long count = 0;
        long starttime = System.currentTimeMillis();
        long z = starttime - ((long)daysBack * 24L * 60L * 60L * 1000L);
        
        Path targetdir = FileSystems.getDefault().getPath(dir);
	File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].lastModified() < z) {
                count++;
                if (isDelete) {
                Path filepath = FileSystems.getDefault().getPath(dir + "/" + listOfFiles[i].getName());
                    if (listOfFiles[i].isFile()) {
                    Files.delete(filepath);
                    } else {
                      deleteDirectory(listOfFiles[i]);  
                    }
                }
            }
        }
        long duration = System.currentTimeMillis() - starttime;
        System.out.println("execution time: " + duration + " millsecs" );
        System.out.println("For Files/Dirs older than this number of days: " + days);
        System.out.println("delete is set to: " + isDelete);
        System.out.println("delete count is: " + count);
 }

    public static void deleteDirectory(File file)
    {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
        file.delete();
    }
 
} // class

