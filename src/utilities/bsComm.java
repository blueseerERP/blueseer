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
import static com.blueseer.edi.EDI.escapeDelimiter;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
/**
 *
 * @author TerryVa
 */
public class bsComm {
    private ScheduledExecutorService scheduler;

    public void startService(int cycle) {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new MyScheduledTask(), 0, cycle, TimeUnit.SECONDS);
        System.out.println("Service started. Task scheduled every " + cycle + " seconds.");

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownService()));
    }

    public void shutdownService() {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("Shutting down service...");
            scheduler.shutdown(); // Initiate graceful shutdown
            try {
                // Wait for existing tasks to complete within a timeout
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow(); // Force shutdown if tasks don't complete
                    System.out.println("Service shutdown forced.");
                } else {
                    System.out.println("Service shut down gracefully.");
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
                System.err.println("Service shutdown interrupted.");
            }
        }
    }

    public static void main(String[] args)  {
    	int cycle = 30;
        if (args != null && args.length > 0) {
            cycle = Integer.parseInt(args[0]);
        }
        
        bsComm service = new bsComm();
        service.startService(cycle);
        // Keep the main thread alive for the service to run (e.g., in a server application)
        // For a simple standalone example, you might add a delay or a loop.
    }
    
    
    
    public class MyScheduledTask implements Runnable {
        private int counter = 0;
        //  Reuse the thread pool across runs instead of creating/destroying it every time
      // private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
       private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        
        @Override
        public void run() {
            System.out.println("Executing task. Count: " + ++counter);
            
          //  boolean eFlag = false;
          //  String p = "";
            String  now = LocalDateTime.now().format(FORMATTER);
            ArrayList<String[]> trafficlist = new ArrayList<>();
            Path filePath = Paths.get("bscomm.cfg");
                try {                    
                    List<String> lines = Files.readAllLines(filePath);
                    for (String line : lines) {
                       if (line.startsWith("#")) {
                           continue;
                       }
                       trafficlist.add(line.split(",", -1));  
                       // tpname, rectype, sourcedir|trantype, destdir, archdir, parse, extract
                       // the rectype element (p or c) determines master directories to loop
                       // the hasChildren element (0 or 1) indicates a second loop through for transtype specific output directory
                       // parent example:  acme, p, /somesourcedir, /somedestdir, /somearchdir, 0, 1   (no children transaction type determination...no file parsing...just movement)
                       // parent example:  acme, p, /somesourcedir, /somedestdir, /somearchdir, 1, 1   (parse each file for transaction type override of dest directory)
                       // child example:   acme, c, trans type    , /somedestdir, /somearchdir, 0, 1  
                    
                    }                   
                } catch (IOException ex) {
                    System.out.println(now + " No config file or unable to read config file");
                    return;
                } 
                
                // validate proper config file
                for (String[] s : trafficlist) {
                    if (s.length != 7) {
                        System.out.println(now + " invalid config file format...each line must have 7 elements");
                        return;
                    }
                }
                
                FileFilter byfiletype = new FileFilter() {
                @Override
                    public boolean accept(File f) {
                        return f.isFile();
                    }
                };
               
                try {
                ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                
                // move and archive files
                for (String[] s : trafficlist) {
                if (! s[1].equals("p")) {  // skip record if not a primary/parent record...ignore all 'c' records at this master loop level
                        continue;
                }
                
                executor.submit(() -> {
                    
                    File folder = new File(s[2]);
                    File[] listOfFiles = folder.listFiles(byfiletype); // files only
                  
                    if (listOfFiles.length == 0) {
                        System.out.println(now + " Thread: " + Thread.currentThread().getName() + " client: " + s[0] + " no files to process ");
                    }
                    
                    
                    
                    
                    for (File listOfFile : listOfFiles) {
                        
                            //boolean eFlag = false;
                            AtomicBoolean eFlag = new AtomicBoolean(false);
                            Path sourcepath = Paths.get(listOfFile.getPath());
                            Path destinationpath = FileSystems.getDefault().getPath(s[3] + "/" + listOfFile.getName());
                            Path archivefilepath = FileSystems.getDefault().getPath(s[4] + "/" + listOfFile.getName() + "." + Long.toHexString(System.currentTimeMillis()));
                            String p = "";
                            if (s[5].equals("1")) { // parse file  
                                try {
                                    // parse file for transaction type and re-assign destinationpath based on return value
                                    p = filterFile(sourcepath, s[0], trafficlist, s[3], s[6]);
                                    eFlag.set(p.equals("extraction"));
                                    Path newpath = Paths.get(p);
                                    if (! p.isEmpty() && newpath != null) {
                                        destinationpath = newpath;
                                    }
                                } catch (IOException ex) {
                                    System.out.println(ex);
                                } catch (Exception ex) {
                                    System.out.println(ex);
                                }
                            }
                            try {
                                if (eFlag.get()) { // parse/extraction...file movement done inside filterFile...just archive original and delete original
                                    Files.copy(sourcepath, archivefilepath, StandardCopyOption.REPLACE_EXISTING);
                                    Files.delete(sourcepath);
                                    System.out.println(now + " Thread: " + Thread.currentThread().getName() + " client: " + s[0] + " moved file: " + listOfFile.getName() + " to " + " extraction override " + "  eFlag=" + eFlag.get());
                                } else {
                                    Files.move(sourcepath, destinationpath, StandardCopyOption.REPLACE_EXISTING);
                                    Files.copy(destinationpath, archivefilepath, StandardCopyOption.REPLACE_EXISTING);
                                    System.out.println(now + " Thread: " + Thread.currentThread().getName() +" client: " + s[0] + " moved file: " + listOfFile.getName() + " to " + destinationpath + "  eFlag=" + eFlag.get());
                                }
                                
                            } catch (IOException ex) {
                                System.out.println(now + " client: " + s[0] + " unable to move file: " + listOfFile.getName() + "\n" + ex);
                            }
                        
                    } // for each record
                    
                }); // executor.submit
                  
                /*
                executor.submit(() -> {
                Path folderPath = Paths.get(s[2]);
                if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
                    return;
                }

                // 5. Use Stream API to process files lazily instead of loading all File[] into memory
                try (Stream<Path> fileStream = Files.list(folderPath)) {
                    long processedCount = fileStream.filter(Files::isRegularFile).peek(sourcepath -> {
                        try {
                            String fileName = sourcepath.getFileName().toString();
                            Path destinationpath = Paths.get(s[3]).resolve(fileName);
                            
                            // Generate unique timestamp string with minimal allocations
                            String hexTime = Long.toHexString(System.currentTimeMillis());
                            Path archivefilepath = Paths.get(s[4]).resolve(fileName + "." + hexTime);

                            boolean isExtraction = false;

                            if ("1".equals(s[5])) { // Parse file
                                try {
                                    String p = filterFile(sourcepath, s[0], trafficlist, s[3], s[6]);
                                    isExtraction = "extraction".equals(p);
                                    if (!p.isEmpty()) {
                                        destinationpath = Paths.get(p);
                                    }
                                } catch (Exception ex) {
                                    System.err.println(ex.getMessage());
                                }
                            }

                            if (isExtraction) { 
                                Files.copy(sourcepath, archivefilepath, StandardCopyOption.REPLACE_EXISTING);
                                Files.delete(sourcepath);
                                System.out.printf("%s Thread: %s client: %s moved file: %s to extraction override%n", 
                                        now, Thread.currentThread().getName(), s[0], fileName);
                            } else {
                                Files.move(sourcepath, destinationpath, StandardCopyOption.REPLACE_EXISTING);
                                Files.copy(destinationpath, archivefilepath, StandardCopyOption.REPLACE_EXISTING);
                                System.out.printf("%s Thread: %s client: %s moved file: %s to %s%n", 
                                        now, Thread.currentThread().getName(), s[0], fileName, destinationpath);
                            }

                        } catch (IOException ex) {
                            System.err.printf("%s client: %s unable to move file: %s%n%s%n", 
                                    now, s[0], sourcepath.getFileName(), ex.getMessage());
                        }
                    }).count();

                    if (processedCount == 0) {
                        System.out.printf("%s Thread: %s client: %s no files to process%n", 
                                now, Thread.currentThread().getName(), s[0]);
                    }

                } catch (IOException e) {
                    System.err.println("Error reading directory: " + s[2]);
                }
            });
                */
                
                } // for trafficlist
                
                    executor.shutdown();
                    boolean hasStopped = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Wait indefinitely for tasks to complete    
                    System.out.println("executor has stopped: " + hasStopped);
                } catch (InterruptedException ex) {
                    System.out.println(now + "  Interrupted Exception ...\n" + ex);
                } 
                
                
        }
    
        /*
        public void shutdownExecutor() {
        executor.shutdown();
        }
        */
    }


     public String filterFile(Path infilepath, String tp, ArrayList<String[]> trafficlist, String defaultoutdir, String extract) throws FileNotFoundException, IOException, Exception {
        String[] m = new String[]{"0","","","",""};  //status, message, doctype, tradeid, outdir
        Path r = null;
       
         String[] c = new String[12];
        
        System.out.println("reading file at: " + infilepath.toString());
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
          
           System.out.println("beginning to parse file ...length: " + cbuf.length + " for file: " + infilepath.toString());
           
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
     
     /*
        System.out.println("Envelope start: " + isa.getValue()[0] + "   end: " + isa.getValue()[1] + " of envelope number " + isa.getKey());
        Map<Integer, ArrayList> d = (HashMap<Integer, ArrayList>)isa.getValue()[5];
    
        System.out.println("document count: " + d.size() + " of envelope number " + isa.getKey());
        for (Map.Entry<Integer, ArrayList> z : d.entrySet()) {
            for (Object j : z.getValue()) {
                Object[] x = (Object[]) j;
                Integer[] docints = (Integer[]) x[0];
                System.out.println("document type: " + (String) x[1] + " document start: " + docints[0] + "   end: " + docints[1] + " of document number " + z.getKey() + " of envelope number " + isa.getKey());
            } // j         
        } // object k
    */
    } // ISAMap entries
    
    if (extract.equals("1")) {
        return "extraction";
    }
    
    return (r == null) ? "" : r.toString();
    }
    


}
