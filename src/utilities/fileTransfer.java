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

/**
 *
 * @author vcs
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/// # File Traffic Processor
///
///
/// This is a cron dcriven application for moving files from one directory to another using multi-threaded events (virtual threads).
/// Operational variables are defined by a config file 'source.cfg' located in execution directory.
/// A log file is generated and appended to in the execution directory 'log.txt'.
/// To suppress event execution, drop a 'lock.txt' file in execution directory.
///
/// ## config file definition
/// column names separated by "," :  tpname, rectype, sourcedir|trantype, destdir, archdir, parse, extract, replace|unique|append|prefix|pu, regex|destdir, filemask
/// the rectype element (p or c) determines master directories to loop
/// the 'parse' 5th column (0 or 1) indicates a second loop through for trans type specific output directory
/// parent example:  acme, p, /somesourcedir, /somedestdir, /somearchdir, 0, 0, (u,a,p,pu),,  (no children transaction type determination...no file parsing...just movement)
/// parent example:  acme, p, /somesourcedir, /somedestdir, /somearchdir, 1, 1, (u,a,p,pu),,   (parse each file for transaction type override of dest directory)
/// child example:   acme, c, trans type    , /somedestdir, /somearchdir, 0, 1, (u,a,p,pu),, 
///
/// The 9th column allows for regex matches on source file names...and deviates destination if a match is found
/// example:  acme, p, /somesourcedir, /somedestdir, /somearchdir, 0, 0,r,^abc.*=/someotherdestdir|^def.*=/someotherdestdir2,
/// Application supports multiple regex/destdir entries in column 9...separated by a | .   The key/value for each entry is separated by an =
///
/// The 10th column is file masking...prefix, suffix, etc separated by |  ...ex PLGA_|.txt  adds prefix of PLGA_ and suffix of .txt

public class fileTransfer {
	
	 	private static final Semaphore DISK_IO_SEMAPHORE = new Semaphore(100);
	    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	    private static final int MAX_RETRIES = 5;
	    private static final long INITIAL_RETRY_DELAY_MS = 500;
	    private static final long STABILITY_CHECK_DELAY_MS = 200;
	    
	    private static final List<String> loglist = Collections.synchronizedList(new ArrayList<>());
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
		String  now = LocalDateTime.now().format(FORMATTER);
		BufferedWriter logfile = new BufferedWriter(new FileWriter(new File("log.txt"), true)); 
		File lf = new File("lock.txt");
    	
    	
    	
    	// bail if lock file found
    	if (lf.exists()) {
    		logfile.write(now + " Lock File Found...exiting" + "\n");
    		logfile.close();
    		return;
    	}
		
		
        ArrayList<String[]> trafficlist = new ArrayList<>();
        Path filePath = Paths.get("source.cfg");
            try {                    
                List<String> lines = Files.readAllLines(filePath);
                logfile.write(now + " start event" + "\n");
                for (String line : lines) {
                   if (line.startsWith("#")) {
                       continue;
                   }
                   trafficlist.add(line.split(",", -1));  
                  // System.out.println(now + " adding line to trafficlist: " + line);
                }                   
            } catch (IOException ex) {
            	logfile.write(now + " No config file or unable to read config file" + "\n");
            	logfile.close();
                return;
            } 
            
            // validate proper config file
            for (String[] s : trafficlist) {
                if (s.length != 10) {
                	logfile.write(now + " invalid config file format...each line must have 9 elements" + "\n");
                	logfile.close();
                    return;
                }
            }
            
            
		 try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
             
             //for (Path sourceDir : SOURCE_DIRECTORIES) {
             for (String[] s : trafficlist) {
             if (! s[1].equals("p") || s[2].isBlank()) {  // skip record if not a primary/parent record...ignore all 'c' records at this master loop level
                     continue;
             }             
             
             	
	         	String glob = "*";	         	
	         	String parentSourcedir = s[2];
	         	
	         	if (s[2].contains("*")) {
		         	String[] tokens = s[2].split("/");
		         	if (tokens.length > 0) {
		         	    glob = tokens[tokens.length - 1];
		         	   String[] parentTokens = Arrays.copyOf(tokens, tokens.length - 1);
		         	   parentSourcedir = String.join("/", parentTokens);
		         	} 
	         	}
	         	
	         
	         	if (s[7].equals("a")) { // appending operation cannot be multi-threaded  
	         		streamAndProcessDirectorySort(FileSystems.getDefault().getPath(parentSourcedir), glob, trafficlist, s);
	         	} else {
	         		streamAndProcessDirectory(FileSystems.getDefault().getPath(parentSourcedir), glob, virtualExecutor, trafficlist, s);	
	         	}
	         	
             }
             
         } // The block implicitly calls virtualExecutor.close(), blocking safely until ALL files in this run finish moving.
		 
		 if (! loglist.isEmpty()) {
			 synchronized (loglist) { 
		            for (String x : loglist) {
		                logfile.write(x + "\n");
		            }
		      }
		 }
		 
		 now = LocalDateTime.now().format(FORMATTER);
		 logfile.write(now + " end event" + "\n");
		 
		 if (logfile != null) {
	     	logfile.close();
	     }
		
	}
	
    private static void streamAndProcessDirectory(Path sourceDir, String glob, ExecutorService executor, ArrayList<String[]> trafficlist, String[] s) throws IOException {
     
    	
    	
    	
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir, glob)) {
        	
        	// List<Path> files = new ArrayList<>();
        	// for (Path path : stream) {
        	//        files.add(path);
        	// }
        	// Collections.sort(files); 
        	 
        	         	
            for (Path file : stream) {
                if (Files.isRegularFile(file) &&  s[5].equals("0")) {
                    executor.execute(() -> moveFile(file, s)); // do not need to track the progress or get the result via a Future object...so use execute in lieu of submit
                }
                if (Files.isRegularFile(file) && s[5].equals("1")) {
                    executor.execute(() -> parseAndMoveFile(file, s, trafficlist)); // do not need to track the progress or get the result via a Future object...so use execute in lieu of submit
                }
            }
        } catch (IOException e) {
        	loglist.add("CRITICAL: Error streaming directory " + sourceDir + ": " + e.getMessage());
        }
    }
    
    private static void streamAndProcessDirectorySort(Path sourceDir, String glob, ArrayList<String[]> trafficlist, String[] s) throws IOException {

    	
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir, glob)) {
        	
        	 List<Path> files = new ArrayList<>();
        	 for (Path path : stream) {
        	        files.add(path);
        	 }
        	 Collections.sort(files); 
        	 
        	         	
            for (Path file : files) {
                if (Files.isRegularFile(file) &&  s[5].equals("0")) {
                    moveFile(file, s); 
                }
                if (Files.isRegularFile(file) && s[5].equals("1")) {
                    parseAndMoveFile(file, s, trafficlist); 
                }
            }
        } catch (IOException e) {
        	loglist.add("CRITICAL: Error streaming directory " + sourceDir + ": " + e.getMessage());
        }
    }
    
	
	private static void moveFile(Path sourceFile, String[] s)  {
		 long delay = INITIAL_RETRY_DELAY_MS;
	        int attempts = 0;
	        Path destinationFile = FileSystems.getDefault().getPath(s[3]).resolve(sourceFile.getFileName());
	        String[] maskarray;
	        String[] namearray = null;
	        
	      //  System.out.println("Executing on virtual thread: " + Thread.currentThread().getName() + " / " + Thread.currentThread().threadId());
	        
	        if (s[8] != null && ! s[8].isBlank()) {
	            maskarray = s[8].split("\\|", -1);
	            for (String m : maskarray) {
	                if (! m.contains("=")) {
	                    break;
	                }
	                
	                String[] pair = m.split("=");
	                if (sourceFile.getFileName().toString().matches(pair[0])) {
	                    destinationFile = FileSystems.getDefault().getPath(pair[1]).resolve(sourceFile.getFileName());
	                    break;
	                }
	            }
	        }
	        
	        if (s[9] != null && ! s[9].isBlank() && s[9].contains("|")) {
	        	namearray  = s[9].split("\\|", -1);
	        }
	        
	        // make unique if file exists in destination directory and s[7] is 'u'  [ (r)eplace, (u)nique ]
	        if (Files.exists(destinationFile) && s[7].equals("u")) {
	          destinationFile = FileSystems.getDefault().getPath(destinationFile.getParent() + "/" + destinationFile.getFileName() + "." + Long.toHexString(System.currentTimeMillis()));   
	          loglist.add("File Exists...and flag is unique...Renaming to: " + destinationFile);
	        }
	        
	        if (s[7].equals("a")) {
		          destinationFile = FileSystems.getDefault().getPath(s[3] + "/" + s[9]); 
		    }
	        
	        if (s[7].equals("p") && ! s[9].isEmpty() && namearray != null && namearray.length > 1) {
		          destinationFile = FileSystems.getDefault().getPath(s[3] + "/" + namearray[0] + destinationFile.getFileName() + namearray[1]); 
		    }
	        
	        if (s[7].equals("pu") && ! s[9].isEmpty() && namearray != null && namearray.length > 1) {
		          destinationFile = FileSystems.getDefault().getPath(s[3] + "/" +  namearray[0] + destinationFile.getFileName() + namearray[1]);
		          if (Files.exists(destinationFile)) {
		        	  destinationFile = FileSystems.getDefault().getPath(destinationFile.getParent() + "/" + destinationFile.getFileName() + "." + Long.toHexString(System.currentTimeMillis()));
		          }
		    }
	        
	        
	        try {
	            // 1. Concurrency throttle
	            DISK_IO_SEMAPHORE.acquire();

	            while (attempts < MAX_RETRIES) {
	                attempts++;
	                try {
	                    // 2. Proactively verify if a file is still being appended to
	                    if (isFileGrowing(sourceFile)) {
	                        throw new IOException("File is actively being written to by another process.");
	                    }

	                 //   System.out.println("here: " + sourceFile + " --> " + destinationFile + "  with s[7] = " + s[7]);
	                    
	                    if (s[7].equals("a")) {
	                    	try {
	                            
	                            byte[] sourceBytes = Files.readAllBytes(sourceFile);
	                            Files.write(destinationFile, sourceBytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);	
	                            Files.delete(sourceFile);
	                            loglist.add("Appended successfully: " + sourceFile + " to " + destinationFile + " on attempt " + attempts + " on thread ID " + Thread.currentThread().threadId());
	                        } catch (IOException e) {
	                            loglist.add("[Attempt " + attempts + "/" + MAX_RETRIES + "] File locked or incomplete: " 
	                            + sourceFile.getFileName() + ". Error: " + e.getMessage());
	                        }
	                    } else {
	                    try {
	                        Files.move(sourceFile, destinationFile, 
	                                StandardCopyOption.REPLACE_EXISTING, 
	                                StandardCopyOption.ATOMIC_MOVE);
	                    } catch (AtomicMoveNotSupportedException e) {                   
	                        Files.move(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
	                    }
	                    loglist.add("Moved successfully: " + sourceFile + " to " + destinationFile + " on attempt " + attempts + " on thread ID " + Thread.currentThread().threadId());
	                    }
	                    
	                    return; // Success, break out of loop

	                } catch (IOException e) {
	                	loglist.add("[Attempt " + attempts + "/" + MAX_RETRIES + "] File locked or incomplete: " 
	                            + sourceFile.getFileName() + ". Error: " + e.getMessage());                    

	                    if (attempts >= MAX_RETRIES) {
	                    	loglist.add("CRITICAL: Exceeded max retries for file: " + sourceFile.getFileName());
	                        e.printStackTrace();
	                        return; // Abandon task for this scan cycle
	                    }

	                    // 4. Truncated Exponential Backoff (Virtual Threads unmount from carrier during Thread.sleep)
	                    Thread.sleep(delay);
	                    delay *= 2; // Double the sleep duration for the next cycle
	                }
	            }
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            loglist.add("CRITICAL: File movement task interrupted for: " + sourceFile.getFileName());
	        } finally {
	            DISK_IO_SEMAPHORE.release();
	        }
    }
	
	
    private static void parseAndMoveFile(Path sourceFile, String[] s, ArrayList<String[]> trafficlist) {
        long delay = INITIAL_RETRY_DELAY_MS;
        int attempts = 0;

        try {
            // 1. Concurrency throttle
            DISK_IO_SEMAPHORE.acquire();
            String p = null;
            while (attempts < MAX_RETRIES) {
                attempts++;
                try {
                    // 2. Proactively verify if a file is still being appended to
                    if (isFileGrowing(sourceFile)) {
                        throw new IOException("File is actively being written to by another process.");
                    }

                    try {
                        p = filterFile(sourceFile, s[0], trafficlist, s[3], s[6]);
                    } catch (Exception ex) {
                        System.err.println("[Attempt " + attempts + "/" + MAX_RETRIES + "] error using filterFile: " 
                            + sourceFile.getFileName() + ". Error: " + ex.getMessage());
                    }
                    Path destinationFile = (p == null || p.isBlank()) ? FileSystems.getDefault().getPath(s[3]).resolve(sourceFile.getFileName()) : FileSystems.getDefault().getPath(p).resolve(sourceFile.getFileName());
                    
                    // 3. Attempt the move operation if not extraction type...else delete original as file was extracted and moved in filterFile method
                    if (! s[6].equals("1")) {
                        try {
                            Files.move(sourceFile, destinationFile, 
                                    StandardCopyOption.REPLACE_EXISTING, 
                                    StandardCopyOption.ATOMIC_MOVE);
                        } catch (AtomicMoveNotSupportedException e) {
                            Files.move(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                        }
                        System.out.println("Moved successfully: " + sourceFile.getFileName() + " on attempt " + attempts);
                    } else {
                        Files.delete(sourceFile);
                        System.out.println("Extraction logic -- deleted original: " + sourceFile.getFileName() + " on attempt " + attempts);
                    }

                    return; // Success, break out of loop

                } catch (IOException e) {
                    System.err.println("[Attempt " + attempts + "/" + MAX_RETRIES + "] File locked or incomplete: " 
                            + sourceFile.getFileName() + ". Error: " + e.getMessage());

                    if (attempts >= MAX_RETRIES) {
                        System.err.println("CRITICAL: Exceeded max retries for file: " + sourceFile.getFileName());
                        return; // Abandon task for this scan cycle
                    }

                    // 4. Truncated Exponential Backoff (Virtual Threads unmount from carrier during Thread.sleep)
                    Thread.sleep(delay);
                    delay *= 2; // Double the sleep duration for the next cycle
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("File movement task interrupted for: " + sourceFile.getFileName());
        } finally {
            DISK_IO_SEMAPHORE.release();
        }
    }
	
	private static boolean isFileGrowing(Path file) {
        try {
            if (!Files.exists(file)) {
                return false;
            }
            long sizeBefore = Files.size(file);
            Thread.sleep(STABILITY_CHECK_DELAY_MS); // Safe blocking call for virtual threads
            long sizeAfter = Files.size(file);
            
            return sizeBefore != sizeAfter;
        } catch (IOException | InterruptedException e) {
            // If we can't read the size, it's likely exclusively locked by an active stream
        	loglist.add("CRITICAL: cant read filesize of: " + file.toString() + " --> " + e.getMessage());
            return true; 
        }
    }


	private static String filterFile(Path infilepath, String tp, ArrayList<String[]> trafficlist, String defaultoutdir, String extract) throws FileNotFoundException, IOException, Exception {
        String[] m = new String[]{"0","","","",""};  //status, message, doctype, tradeid, outdir
        Path r = null;
       
         String[] c = new String[12];
        
        System.out.println("reading file at: " + infilepath.toString() + "\n");
        BufferedReader f = new BufferedReader(new FileReader(infilepath.toFile()));
         char[] cbuf = new char[(int) infilepath.toFile().length()];
         int max = cbuf.length;
         f.read(cbuf); 
         f.close();
        
         
         // now lets see how many ISAs and STs within those ISAs and write character positions of each
         Map<Integer, Object[]> ISAmap = new HashMap<Integer, Object[]>();
         int start = 0;
         int end = 0;
         int isacount = 0;
         int gscount = 0;
         int stcount = 0;
         int ststart = 0;
         int sestart = 0;
         String ed_escape = "";
         String sd_escape = "";
         int gsstart = 0;
         String doctype = "";
         String docid = "";
         String reference = "";
         ArrayList<String> isaList = new ArrayList<String>();
          
          char e = 0;
          char s = 0;
          char u = 0;
          
          int mark = 0;
           Map<Integer, ArrayList> stse_hash = new HashMap<Integer, ArrayList>();
           ArrayList<Object> docs = new ArrayList<Object>();
          
           System.out.println("beginning to parse file ...length: " + cbuf.length + " for file: " + infilepath.toString() + "\n");
           
            for (int i = 0; i < cbuf.length; i++) {
                
                if ( ((i+103) <= max) && cbuf[i] == 'I' && cbuf[i+1] == 'S' && cbuf[i+2] == 'A' 
                        && (cbuf[i+103] == cbuf[i+3]) && (cbuf[i+103] == cbuf[i+6]) ) {
                    e = cbuf[i+103];
                    u = cbuf[i+104];
                    s = cbuf[i+105];
                    mark = i;
                    
                   // System.out.println("inside ISA");
                    
                    // lets bale if not proper ISA envelope.....unless the 106 is carriage return...then ok
                    if (i == mark && cbuf[mark+106] != 'G' && cbuf[mark+107] != 'S' && ! String.format("%02x",(int) cbuf[mark+106]).equals("0a")) {
                        System.out.println(infilepath.toString() + " --> malformed envelope");
                        return null;
                    }
                    
                    
                    ed_escape = escapeDelimiter(String.valueOf(e));
                    sd_escape = escapeDelimiter(String.valueOf(s));
                    if (String.format("%02x",(int) cbuf[i+105]).equals("0d") && String.format("%02x",(int) cbuf[i+106]).equals("0a"))
                        s = cbuf[i+106];
                    start = i;
                    isacount++;
                    String[] isa = new String(cbuf, i, 105).split(ed_escape);
                    
                      // set control
                   
                    c[0] = isa[6].trim(); // senderid
                    c[1] = isa[8].trim(); // receiverid
                    c[2] = isa[13]; //isactrlnbr
                    c[3] = ""; // gs sender
                    c[4] = ""; // gs receiver
                    c[5] = ""; // gs control number 
                    c[6] = ""; // gs element 1 transaction group code
                    c[7] = String.valueOf((int) s);
                    c[8] = String.valueOf((int) e);
                    c[9] = String.valueOf((int) u);
                    
                   
                }
                
                if (i > 1 && cbuf[i-1] == s && cbuf[i] == 'G' && cbuf[i+1] == 'S') {
                    gscount++;
                    gsstart = i;
                    String[] gs = new String(cbuf, gsstart, 90).split(ed_escape);
                                      
                     c[5] = gs[6]; // gsctrlnbr
                     c[6] = gs[1]; // group trans type
                    
                }
                if (i > 1 && cbuf[i-1] == s && cbuf[i] == 'S' && cbuf[i+1] == 'T') {
                   
                    stcount++;
                    ststart = i;
                    
                    String[] st = new String(cbuf, i, 16).split(ed_escape);
                    doctype = st[1]; // doctype
                    docid = st[2].split(sd_escape)[0]; //docID  // to separate 2nd element of ST because grabbing 16 characters in buffer
                   
                   // System.out.println(c[0] + "/" + c[1] + "/" + c[4] + "/" + c[5]);
                } 
                
                if (i > 1 && cbuf[i-1] == s && cbuf[i] == 'S' && cbuf[i+1] == 'E') {
                    sestart = i;
                    // add to hash if hash doesn't exist or insert into hash
                    docs.add(new Object[] {new Integer[] {ststart, sestart}, doctype, docid, reference});
                    // painful reminder that you have to create copy of array at instance in time
                    ArrayList copydocs = new ArrayList(docs);
                    stse_hash.put(isacount, copydocs);
                }
                if (i > 1 && cbuf[i-1] == s && cbuf[i] == 'I' && cbuf[i+1] == 'E' && cbuf[i+2] == 'A') {
                    end = i + 14 + String.valueOf(gscount).length() + 1;
                    // now add to ISAmap
                    HashMap<Integer,ArrayList> mycopy = new HashMap<Integer,ArrayList>(stse_hash);
                    ISAmap.put(isacount, new Object[] {start, end, (int) s, (int) e, (int) u, mycopy, c.clone()});
                    stcount = 0;
                    docs.clear();
                    stse_hash.remove(isacount);
                } 
            }
      
    
            
    
    
    
    System.out.println("envelope count: " + ISAmap.size() + " for file: " + infilepath.toString());
    int q = 0;
    for (Map.Entry<Integer, Object[]> isa : ISAmap.entrySet()) {
     q++;
     String[] control = (String[]) isa.getValue()[6];
     for (String[] def : trafficlist) { 
                 if (def[0].equals(tp) && def[1].equals("c") && control[6].equals(def[2])) {
                   r = FileSystems.getDefault().getPath(def[3] + "/" + infilepath.getFileName());
                   break;
                 }  
     }
     
     if (extract.equals("1")) {
        char[] newarray = Arrays.copyOfRange(cbuf, (int) isa.getValue()[0], (int) isa.getValue()[1]); 
        String tempdest = (r == null) ? defaultoutdir : r.getParent().toString();
        Path destinationpath = FileSystems.getDefault().getPath(tempdest + "/" + infilepath.getFileName() + "_" + isa.getKey());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationpath.toFile()))) {
            writer.write(newarray);
            System.out.println("envelope extraction successfully written to " + destinationpath.toString() + " for file: " + infilepath.toString());
        } catch (IOException ioe) {
            System.err.println("Error writing to file: " + destinationpath.toString() + " for file: " + infilepath.toString() + "\n" + ioe.getMessage());
        }
     }
     
     if (! extract.equals("1")) {
        System.out.println("ISA13: " + control[2] + " GS08: " + control[5] +  " GS01: " + control[6]  + " of envelope number " + isa.getKey() + " going to dest dir: " + r);
        if (q > 1) {
            System.out.println("Multiple Envelopes in file...last GS01 defines destination of file." + " for file: " + infilepath.toString());
        }
     }
    
    } // ISAMap entries
    
    if (extract.equals("1")) {
        return "extraction";
    }
    
    return (r == null) ? "" : r.toString();
    }
    

    private static String escapeDelimiter(String delim) {
        if (delim.equals("*")) {
              delim = "\\*";
          }
          if (delim.equals("^")) {
              delim = "\\^";
          }
          if (delim.equals("+")) {
              delim = "\\+";
          }
          if (delim.equals("\\")) {
              delim = "\\\\";
          }
          if (delim.equals("|")) {
              delim = "\\|";
          }
          if (delim.equals("?")) {
              delim = "\\?";
          }
          if (delim.equals("!")) {
              delim = "\\!";
          }
          return delim;
        }
}
