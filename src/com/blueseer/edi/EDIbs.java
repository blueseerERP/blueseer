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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class EDIbs {
    
    
    
public static void main(String args[]) {
 
    // set config
   // bsmf.MainFrame.setConfig();
    
    // lets process the arguments
    String[] vs = checkargs(args);
    String infile = vs[0].toString();
    String outfile = vs[1].toString();
    String indir = vs[2].toString();
    String outdir = vs[3].toString();
    String map = vs[4].toString();
    String isOverride = vs[5].toString();
    String prog = vs[6].toString();
    
    String myargs = String.join(",", args);
        
   
    switch (prog) {
        
        case "single" :
            processSingle(infile, outfile, map, isOverride);
            break;
        case "multiple" :
            processMultiple(indir, outdir, map, isOverride);
            break;
        default:
            System.out.println("Unable to process arguments " + myargs);
        
    }
      
    System.exit(0);
    
   
}



 public static String[] checkargs(String[] args) {
        List<String> legitargs = Arrays.asList("-if", "-of", "-id", "-od", "-m", "-x");
        String[] vals = new String[7]; // last element is the program type (single or mulitiple)
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
             vals[6] = "single";
         if (isMultiple) 
             vals[6] = "multiple";
         
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
              
              
              
               if ( (args.length > i+1 && args[i+1] != null) || (args[i].toString().equals("-x")) ) {
                
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
            EDI.processFile(infile, map, outfile, isOverride); 
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
                  EDI.processFile(listOfFiles[i].getName(), map, "", isOverride);
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
 
 
} // class

