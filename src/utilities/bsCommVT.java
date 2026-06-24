package utilities;

import static com.blueseer.edi.EDI.escapeDelimiter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/// # File Traffic Processor
///
/// This service provides functions for moving files from one directory to another
/// as defined by a config file ...Config file is hard coded as bscomm.cfg in
/// execution directory.   
///
/// ## config file definition
/// column names separated by "," :  tpname, rectype, sourcedir|trantype, destdir, archdir, parse, extract
/// the rectype element (p or c) determines master directories to loop
/// the 'parse' 5th column (0 or 1) indicates a second loop through for trans type specific output directory
/// parent example:  acme, p, /somesourcedir, /somedestdir, /somearchdir, 0, 0   (no children transaction type determination...no file parsing...just movement)
/// parent example:  acme, p, /somesourcedir, /somedestdir, /somearchdir, 1, 1   (parse each file for transaction type override of dest directory)
/// child example:   acme, c, trans type    , /somedestdir, /somearchdir, 0, 1  

/// @author Terry Vaughn


public class bsCommVT {
    
    
    // Throttles maximum concurrent physical disk operations (Backpressure Control)
    private static final Semaphore DISK_IO_SEMAPHORE = new Semaphore(100);

    private ScheduledExecutorService scheduler;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    public void startService(int cycleSeconds) {
        
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
                    }                   
                } catch (IOException ex) {
                    System.out.println(now + " No config file or unable to read config file");
                    return;
                } 
                
                // validate proper config file
                for (String[] s : trafficlist) {
                    if (s.length != 8) {
                        System.out.println(now + " invalid config file format...each line must have 7 elements");
                        return;
                    }
                }
                
                
                

        this.scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("\n[Scan Task] Scanning source directories...");
            
            // BEST PRACTICE: Instantiate the virtual thread executor as a short-lived local asset
            try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
                
                //for (Path sourceDir : SOURCE_DIRECTORIES) {
                for (String[] s : trafficlist) {
                if (! s[1].equals("p") || s[2].isBlank()) {  // skip record if not a primary/parent record...ignore all 'c' records at this master loop level
                        continue;
                }                    
                    streamAndProcessDirectory(FileSystems.getDefault().getPath(s[2]), virtualExecutor, trafficlist, s);
                }
                
            } // The block implicitly calls virtualExecutor.close(), blocking safely until ALL files in this run finish moving.
            
        }, 0, cycleSeconds, TimeUnit.SECONDS);

        System.out.println("Service started. Polling every " + cycleSeconds + " seconds.");
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownService));
    }

    public void shutdownService() {
        System.out.println("Shutting down service...");
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Service fully stopped.");
    }

    public static void main(String[] args) {
        int cycle = 30;
        if (args != null && args.length > 0) {
            try {
                cycle = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid interval argument. Defaulting to 30 seconds.");
            }
        }

        bsCommVT service = new bsCommVT();
        service.startService(cycle);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            service.shutdownService();
        }
    }

    private static void streamAndProcessDirectory(Path sourceDir, ExecutorService executor, ArrayList<String[]> trafficlist, String[] s) {
        if (!Files.exists(sourceDir)) {
            return;
        } 

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file) &&  s[5].equals("0")) {
                    executor.execute(() -> moveFile(file, s)); // do not need to track the progress or get the result via a Future object...so use execute in lieu of submit
                }
                if (Files.isRegularFile(file) && s[5].equals("1")) {
                    executor.execute(() -> parseAndMoveFile(file, s, trafficlist)); // do not need to track the progress or get the result via a Future object...so use execute in lieu of submit
                }
            }
        } catch (IOException e) {
            System.err.println("Error streaming directory " + sourceDir + ": " + e.getMessage());
        }
    }

    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_RETRY_DELAY_MS = 500;
    private static final long STABILITY_CHECK_DELAY_MS = 200;

    private static void moveFile(Path sourceFile, String[] s) {
        long delay = INITIAL_RETRY_DELAY_MS;
        int attempts = 0;
        Path destinationFile = FileSystems.getDefault().getPath(s[3]).resolve(sourceFile.getFileName());
        String[] maskarray;
        System.out.println("Executing on virtual thread: " + Thread.currentThread().getName() + " / " + Thread.currentThread().threadId());
        
        if (s[7] != null && ! s[7].isBlank()) {
            maskarray = s[7].split("\\|", -1);
            System.out.println("HERE 1 " + s[7]);
            for (String m : maskarray) {
                if (! m.contains("=")) {
                    break;
                }
                
                String[] pair = m.split("=");
                System.out.println("HERE 2 " + pair[0] + " " + sourceFile.getFileName().toString());
                if (sourceFile.getFileName().toString().matches(pair[0])) {
                    destinationFile = FileSystems.getDefault().getPath(pair[1]).resolve(sourceFile.getFileName());
                    System.out.println("FOUND MATCH " + destinationFile);
                    break;
                }
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

                    
                    // 3. Attempt the move operation
                    try {
                        Files.move(sourceFile, destinationFile, 
                                StandardCopyOption.REPLACE_EXISTING, 
                                StandardCopyOption.ATOMIC_MOVE);
                    } catch (AtomicMoveNotSupportedException e) {
                        Files.move(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                    }

                    System.out.println("Moved successfully: " + sourceFile.getFileName() + " on attempt " + attempts + " on thread ID " + Thread.currentThread().threadId());
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

    /**
     * Checks if the file size changes over a short window to identify active writes.
     */
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
            return true; 
        }
    }
    
    private static String filterFile(Path infilepath, String tp, ArrayList<String[]> trafficlist, String defaultoutdir, String extract) throws FileNotFoundException, IOException, Exception {
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
    
    } // ISAMap entries
    
    if (extract.equals("1")) {
        return "extraction";
    }
    
    return (r == null) ? "" : r.toString();
    }
    

}
