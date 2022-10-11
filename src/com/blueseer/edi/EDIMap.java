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

import bsmf.MainFrame;
import static com.blueseer.edi.EDI.edilog;
import static com.blueseer.edi.EDI.hanoi;
import static com.blueseer.edi.EDI.trimSegment;
import static com.blueseer.edi.ediData.getMapMstr;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.EDData;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.SmbException;

/**
 *
 * @author terryva
 */
public abstract class EDIMap implements EDIMapi {
    
    String[] envelope = null;
         
    public static  int segcount = 0;
    public static boolean GlobalDebug = false;
    public static boolean isError = false;
    
    public static int commit = 0;
    
    DateFormat isadfdate = new SimpleDateFormat("yyMMdd");
    DateFormat gsdfdate = new SimpleDateFormat("yyyyMMdd");
    DateFormat isadftime = new SimpleDateFormat("HHmm");
    DateFormat gsdftime = new SimpleDateFormat("HHmm");
    Date now = new Date();

    public static String ISA = "";
    public static String GS = "";
    public static String GE = "";
    public static String IEA = "";

    public static String[] isaArrayIN = new String[17];
    public static String[] gsArrayIN = new String[9];
    
    public static String[] isaArray = new String[17];
    public static String[] mapISAArray = new String[]{"","","","","","","","","","","","","","","","",""}; // assigned at map level
    public static String[] gsArray = new String[9];
    public static String[] mapGSArray = new String[]{"","","","","","","","",""}; // assigned at map level
    public static String[] stArray = new String[3];
    public static String[] seArray = new String[3];
    public static String[] geArray = new String[3];
    public static String[] ieaArray = new String[3];
     
     public static String[] error = new String[2];

     public static String ST = "";
     public static String SE = "";

     public static String insender = "";
     public static String inreceiver = "";
     public static String outsender = "";
     public static String outreceiver = "";
     public static String map = "";
     public static boolean isOverride = false;
     public static String doctype = "";

     public static String infile = "";
     public static String outdir = "";
     public static String outfile = "";
     public static String outputfiletype = "";
     public static String outputdoctype = "";
     public static String ifsfile = "";
     public static String ofsfile = "";
     public static String filename = "";
     public static String isactrl = "";
     public static String gsctrl = "";
     public static String stctrl = "";
     public static String ref = "";

     public static String header = "";
     public static String detail = "";
     public static String trailer = "";
     public static ArrayList<String> H = new ArrayList();
     public static ArrayList<String> D = new ArrayList();
     public static ArrayList<String> T = new ArrayList();

     public static String sd = "";
     public static String ed = "";
     public static String ud = "";

     public static String content = "";

    public static Map<String, HashMap<Integer,String[]>> mISF = new LinkedHashMap<String, HashMap<Integer,String[]>>();
		 
    public static Map<String, ArrayList<String[]>> HASH = new  LinkedHashMap<String, ArrayList<String[]>>();
    public static Map<String, String[]> mappedInput = new  LinkedHashMap<String, String[]>();
    public static Map<String, ArrayList<String[]>> OSF = new  LinkedHashMap<String, ArrayList<String[]>>();
    public static ArrayList<String[]> ISF = new  ArrayList<String[]>();
    public static Map<String, HashMap<String,String>> OMD = new LinkedHashMap<String, HashMap<String,String>>();
    public static ArrayList<String> SegmentCounter = new ArrayList<String>();

    public static boolean isSet(ArrayList list, Integer index) {
     return index != null && index >=0 && index < list.size() && list.get(index) != null;
     }

    public static boolean isSet(String[] list, Integer index) {
     return index != null && index >=0 && index < list.length && list[index] != null;
     }

    public static void resetVariables() {
    commit = 0;    
    segcount = 0;
    isError = false;
    ISA = "";
    GS = "";
    GE = "";
    IEA = "";
    ST = "";
    SE = "";

     insender = "";
     inreceiver = "";
     outsender = "";
     outreceiver = "";
     map = "";
     doctype = "";

     infile = "";
     outdir = "";
     outfile = "";
     outputfiletype = "";
     outputdoctype = "";
     ifsfile = "";
     ofsfile = "";
     filename = "";
     isactrl = "";
     gsctrl = "";
     stctrl = "";
     ref = "";
    }
    
    public static void setError(String mssg) {
        isError = true;
        error = new String[]{"error", mssg};
    }
    
    public void setInputStructureFile(String ifs) {
        ifsfile = ifs;
        readISF(ifsfile);
    }
    
    public void setOutputStructureFile(String ofs) {
        ofsfile = ofs;
        readOSF(ofsfile);
    }
    
    public void setOutPutFileType(String filetype) {
        outputfiletype = filetype;
    }
    
    public void setOutPutDocType(String doc) {
        outputdoctype = doc;
    }

    public void setOutPutSender(String sender) {
        outsender = sender;
    }

    public void setOutPutReceiver(String receiver) {
        outreceiver = receiver;
    }

    
    
    public void setControl(String[] c) throws UserDefinedException {
        
        resetVariables();
        
        insender = c[0];
        inreceiver = c[21];
     //   outsender = insender;  // can override within map
    //    outreceiver = inreceiver; // can override within map
        doctype = c[1];
        outputdoctype = c[1]; // can override within map
        map = c[2];
        infile = c[3];
        isactrl = c[4];
        gsctrl = c[5];
        stctrl = c[6];
        ref = c[7];
        outfile = c[8];
        sd = delimConvertIntToStr(c[9]);
        ed = delimConvertIntToStr(c[10]);
        ud = delimConvertIntToStr(c[11]);
        isOverride = Boolean.valueOf(c[12]); // isOverrideEnvelope
        GlobalDebug = Boolean.valueOf(c[30]);
        
        
        
        setControlISA(c[13].split(EDI.escapeDelimiter(ed), -1));  // EDIMap.setISA
        setControlGS(c[14].split(EDI.escapeDelimiter(ed), -1));   // EDIMap.setGS
        
       
        
        if (c[0].equals("MapTester")) {
           // throw new UserDefinedException("Houston...we have a problem"); 
           // c values override lookup below for map tester
          
           ediData.map_mstr x = getMapMstr(new String[]{c[2]});
           outsender = c[0];
           outreceiver = c[21];
           outputdoctype = c[15];
           outputfiletype = c[29];
           
           if (outsender.isBlank()) {
               throw new UserDefinedException("Missing outsender in c[0]"); 
           }
           if (outreceiver.isBlank()) {
               throw new UserDefinedException("Missing outreceiver in c[21]"); 
           }
           if (outputdoctype.isBlank()) {
               throw new UserDefinedException("Missing outputdoctype in c[15]"); 
           }
           if (outputfiletype.isBlank()) {
               throw new UserDefinedException("Missing outputfiletype in c[29]"); 
           }
           if (x.map_ifs().isBlank()) {
               throw new UserDefinedException("Missing IFS in map master"); 
           }
           if (x.map_ofs().isBlank()) {
               throw new UserDefinedException("Missing OFS in map master"); 
           }
           
           setInputStructureFile(x.map_ifs());
           setOutputStructureFile(x.map_ofs());
       //   bsmf.MainFrame.show(outsender + "/" + outreceiver + "/" + outputdoctype + "/" + outputfiletype + "/" + ifsfile + "/" + ofsfile);
           return;
           
        }
        
        if (c[28].equals("X12")) {
        outsender = gsArrayIN[2];
        outreceiver = gsArrayIN[3];
        } else {
        outsender = c[0];  // senderid
        outreceiver = c[21]; // receiverid
        }
        
        String[] tp = EDData.getEDITPDefaults(outputdoctype, outsender, outreceiver );
    //    bsmf.MainFrame.show(outputdoctype + "/" + outsender + "/" + outreceiver );
    //    bsmf.MainFrame.show(tp[14] + "/" + tp[15] + "/" + tp[16] + "/" + tp[17] );
        setOutPutDocType(tp[14]);
        setOutPutFileType(tp[15]);
        if (! tp[16].equals("DB")) {
        setInputStructureFile(tp[16]);
        }
        if (! tp[17].equals("DB")) {
        setOutputStructureFile(tp[17]);
        }
     }

    public void setReference(String key) {
        ref = key;
    }
    
    public static String delimConvertIntToStr(String intdelim) {
    String delim = "";
    int x = Integer.valueOf(intdelim);
    delim = String.valueOf(Character.toString((char) x));
    return delim;
  }
     
    public void setOutPutEnvelopeStrings(String[] c) {         
         if ( ! isOverride) {  // if not override...use internal partner / doc lookup for envelope info
           
           envelope = EDI.generateEnvelope(outputdoctype, outsender, outreceiver); // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber, gsctrlnbr)
           String ed = envelope[8];
           ISA = envelope[0];
           isaArray = ISA.split(EDI.escapeDelimiter(ed), -1);
           GS = envelope[1];
           gsArray = GS.split(EDI.escapeDelimiter(ed), -1);
           GE = envelope[2];
           IEA = envelope[3];
           filename = envelope[4];
           outfile = filename;  
           isactrl = envelope[5];
           gsctrl = envelope[6];
           stctrl = String.format("%04d", 1);
           ST = "ST" + ed + outputdoctype + ed + stctrl ;
           SE = "SE" + ed + String.valueOf(segcount) + ed + stctrl;  
           
           overrideISAWithMapEntries(); 
           overrideGSWithMapEntries();
           
           
           } else {
             // you can override elements within the envelope xxArray fields at this point....or merge into segment string
             // need to figure out what kind of error this bullshit is....
             ISA = c[13];
             GS = c[14];
             GE = "GE" + ed + "1" + ed + c[5];
             IEA = "IEA" + ed + "1" + ed + c[4];
             ST = "ST" + ed + outputdoctype + ed + c[6]; 
             SE = "SE" + ed + "1" + ed + c[6];

             xsetISA(9,""); // set date to now
             xsetISA(10,"");  // set time to now

           
             header = "";
             detail = "";
             trailer = "";
             content = "";
           }

     }

    public void overrideISAWithMapEntries() {
        for (int i = 1; i < mapISAArray.length; i++ ) { // skip 0 ...ISA landmark element
            if (mapISAArray[i] != null && ! mapISAArray[i].isEmpty()) {
               switch (i) {
               case 1 :
                 isaArray[i] = String.format("%-2s", mapISAArray[i]);
                 break;
               case 2 :
                 isaArray[i] = String.format("%-10s", mapISAArray[i]);
                 break;
               case 3 :
                 isaArray[i] = String.format("%-2s", mapISAArray[i]);
                 break;
               case 4 :
                 isaArray[i] = String.format("%-10s", mapISAArray[i]);
                 break;
               case 5 :
                 isaArray[i] = String.format("%-2s", mapISAArray[i]);
                 break; 
               case 6 :
                 isaArray[i] = String.format("%-15s", mapISAArray[i]);
                 break;
               case 7 :
                 isaArray[i] = String.format("%-2s", mapISAArray[i]);
                 break;   
               case 8 :
                 isaArray[i] = String.format("%-15s", mapISAArray[i]);
                 break;  
               case 9 :
                 isaArray[i] = isadfdate.format(now);
                 break;  
               case 10 :
                 isaArray[i] = isadftime.format(now);
                 break;  
               case 11 :
                 isaArray[i] = String.format("%-1s", mapISAArray[i]);  
                 break;  
               case 12 :
                 isaArray[i] = String.format("%-5s", mapISAArray[i]);  
                 break;  
               case 13 :
                 isaArray[i] = String.format("%-9s", mapISAArray[i]);  
                 break;  
               case 14 :
                 isaArray[i] = String.format("%-1s", mapISAArray[i]);  
                 break;  
               case 15 :
                 isaArray[i] = String.format("%-1s", mapISAArray[i]);  
                 break; 
                 case 16 :
                 isaArray[i] = String.format("%-1s", mapISAArray[i]);  
                 break; 
               default :
               break;
               }
            }
        }
        ISA = String.join(ed,isaArray);
    }
    
    public void overrideGSWithMapEntries() {
        for (int i = 1; i < mapGSArray.length; i++ ) { // skip 0 ...ISA landmark element
            if (mapGSArray[i] != null && ! mapGSArray[i].isEmpty()) {
                gsArray[i] = String.valueOf(mapGSArray[i]);
            }
        }
        GS = String.join(ed,gsArray);
    }
    
    public String getISA(int i) {
         if (i > 16) {
             return "";
         }
       isaArray = ISA.split(EDI.escapeDelimiter(ed), -1);  
       return isaArray[i];
     }

    public String getInputISA(int i) {
       if (isaArrayIN != null && isaArrayIN.length >= i) {
       return isaArrayIN[i];
       } else {
           return "";
       }
     }
    
    public String getInputGS(int i) {
       if (gsArrayIN != null && gsArrayIN.length >= i) {
       return gsArrayIN[i];
       } else {
           return "";
       }
     }

    public String getGS(int i) {
         if (i > 8) {
             return "";
         }
       gsArray = GS.split(EDI.escapeDelimiter(ed), -1);  
       return gsArray[i];
     }

    
    public void xsetISA(int i, String value) {
         isaArray = ISA.split(EDI.escapeDelimiter(ed), -1);
         switch (i) {
           case 1 :
             isaArray[i] = String.format("%-2s", value);
             break;
           case 2 :
             isaArray[i] = String.format("%-10s", value);
             break;
           case 3 :
             isaArray[i] = String.format("%-2s", value);
             break;
           case 4 :
             isaArray[i] = String.format("%-10s", value);
             break;
           case 5 :
             isaArray[i] = String.format("%-2s", value);
             break; 
           case 6 :
             isaArray[i] = String.format("%-15s", value);
             break;
           case 7 :
             isaArray[i] = String.format("%-2s", value);
             break;   
           case 8 :
             isaArray[i] = String.format("%-15s", value);
             break;  
           case 9 :
             isaArray[i] = isadfdate.format(now);
             break;  
           case 10 :
             isaArray[i] = isadftime.format(now);
             break;  
           case 11 :
             isaArray[i] = String.format("%-1s", value);  
             break;  
           case 12 :
             isaArray[i] = String.format("%-5s", value);  
             break;  
           case 13 :
             isaArray[i] = String.format("%-9s", value);  
             break;  
           case 14 :
             isaArray[i] = String.format("%-1s", value);  
             break;  
           case 15 :
             isaArray[i] = String.format("%-1s", value);  
             break; 
             case 16 :
             isaArray[i] = String.format("%-1s", value);  
             break; 
           default :
           break;
   }

         ISA = String.join(ed,isaArray);
     }

    public void setISA(int i, String value) {
         switch (i) {
           case 1 :
             mapISAArray[i] = String.format("%-2s", value);
             break;
           case 2 :
             mapISAArray[i] = String.format("%-10s", value);
             break;
           case 3 :
             mapISAArray[i] = String.format("%-2s", value);
             break;
           case 4 :
             mapISAArray[i] = String.format("%-10s", value);
             break;
           case 5 :
             mapISAArray[i] = String.format("%-2s", value);
             break; 
           case 6 :
             mapISAArray[i] = String.format("%-15s", value);
             break;
           case 7 :
             mapISAArray[i] = String.format("%-2s", value);
             break;   
           case 8 :
             mapISAArray[i] = String.format("%-15s", value);
             break;  
           case 9 :
             mapISAArray[i] = isadfdate.format(now);
             break;  
           case 10 :
             mapISAArray[i] = isadftime.format(now);
             break;  
           case 11 :
             mapISAArray[i] = String.format("%-1s", value);  
             break;  
           case 12 :
             mapISAArray[i] = String.format("%-5s", value);  
             break;  
           case 13 :
             mapISAArray[i] = String.format("%-9s", value);  
             break;  
           case 14 :
             mapISAArray[i] = String.format("%-1s", value);  
             break;  
           case 15 :
             mapISAArray[i] = String.format("%-1s", value);  
             break; 
             case 16 :
             mapISAArray[i] = String.format("%-1s", value);  
             break; 
           default :
           break;
   }

         ISA = String.join(ed,mapISAArray);
     }
    
    public void setControlISA (String[] isa) {
        
         if (isa != null && isa.length > 16) {
             isaArrayIN = isa;
         }
     }

    
    public void setGS(int i, String value) {
         if (i > 8) {
             return;
         }
         mapGSArray[i] = String.valueOf(value);
         GS = String.join(ed,mapGSArray);
     }

    public void setControlGS (String[] gs) {
        if (gs != null && gs.length > 8) {
         gsArrayIN = gs;
        }
     }

    
    public void setSE() {
         seArray = SE.split(EDI.escapeDelimiter(ed), -1);
         seArray[1] = String.valueOf(segcount);
         SE = String.join(ed,seArray);
     }

    public void setGE() {
         geArray = GE.split(EDI.escapeDelimiter(ed), -1);
         geArray[1] = String.valueOf(segcount);
         GE = String.join(ed,geArray);
     }

    public String[] packagePayLoad(String[] c) {
     // cleanup of mapped input
     mappedInput.clear();
      
        if (c[0].equals("MapTester")) {
           // writeOMD(c, EDData.getEDITPDefaults(c[1], "SYSSENDER", "SYSRECEIVER" ));
            writeOMD(c, null);
            return new String[]{content};
        }
        
        if (c[29].equals("DB")) {
            return new String[]{c[23],c[38]};
        }
        // get TP/Doc defaults
        String[] tp = EDData.getEDITPDefaults(doctype, outsender, outreceiver );
        
        if (tp == null || tp.length < 18) {
            setError("tp defaults is null or empty for: " + doctype + "/" + outsender + "/" + outreceiver);
            return error;  
        }
        
        if (GlobalDebug) {
        System.out.println("Getting tp defaults for: " + doctype + "/" + outsender + "/" + outreceiver);
        System.out.println("Value of tp defaults found: " + String.join(",", tp));
        }
        
         // concatenate all output strings to string variable 'content' 
        writeOMD(c, tp);
        
        // create out batch file name
         int filenumber = OVData.getNextNbr("edifile");
         String batchfile = "X" + String.format("%07d", filenumber);
         String outfilemulti = "";
        if (outfile.isEmpty()) {
            outfile = tp[10] + String.format("%07d", filenumber) + "." + tp[11];
        }
        
        if (tp[15].equals("X12")) {
           setOutPutEnvelopeStrings(c);
           String s = delimConvertIntToStr(tp[7]); // segment delimiter
           if (tp[20].equals("1")) { // if envelopeall...then multi envelope
            // content = ST + s + content  + SE + s;
            
             // write to hash map with key = doctype , outsender , outreceiver
             String hk = doctype + "," + outsender + "," + outreceiver;
             
                if (hanoi != null && hanoi.containsKey(hk)) {
                        ArrayList<String> g = hanoi.get(hk);
                        outfilemulti = g.get(2);
                        g.add(content);
			hanoi.put(hk, g);
		} else {
                        outfilemulti = outfile;
                        ArrayList<String> g = new ArrayList<String>();
                        g.add(ISA);
                        g.add(GS);
                        g.add(outfilemulti);
                        g.add(content);
			hanoi.put(hk, g);
		}
                
           } else {  // else single envelope
             content = ISA + s + GS + s + ST + s + content  + SE + s + GE + s + IEA + s;  
           }
        }

        

        if (outdir.isEmpty()) {
            outdir = tp[9];
            if (outdir.isEmpty()) {
                outdir = EDData.getEDIOutDir();
            }
        }
        
        

        c[7] = ref;
        c[15] = tp[14];
        c[25] = batchfile;
        if (tp[20].equals("0")) { // if single package
            c[8] = outfile;
        } else {
            c[8] = outfilemulti;    
        }
        c[27] = outdir;
        c[29] = tp[15];
        c[6] = stctrl;
        c[5] = gsctrl;
        c[4] = isactrl;
        c[31] = "0";
        c[32]  = "99999";
        c[33] = "0";
        c[34] = "99999";
        c[35] = tp[7];
        c[36] = tp[6];
        c[37] = tp[8];

     if (GlobalDebug)
     System.out.println("Value of c within EDIMap class: " + String.join(",", c));

        // error handling
        if (batchfile.isEmpty()) {
            setError("batch file is empty");
            return error;
        }
        if (outfile.isEmpty()) {
            setError("out file is empty");
            return error;
        }
        if (tp[15].isEmpty()) {
            setError("out file type is unknown");
            return error;
        }
        try {
            // Write output batch file
            EDI.writeFile(content, EDData.getEDIBatchDir(), batchfile);
            // Write to outfile if single
            if (tp[20].equals("0")) { // Write to outfile if single envelope...else done at end of EDI.processor
            EDI.writeFile(content, outdir, outfile);  // you can override output directory by assign 2nd parameter here instead of ""
            }
        } catch (SmbException ex) {
            edilog(ex);
        } catch (IOException ex) {
            edilog(ex);
        }

        // need confirmation file was created   
        if (tp[20].equals("0")) { // if single package
            File file = new File(outdir + "/" + outfile);
            if (! file.exists()) {
                setError("output file not created: " + file.getPath().toString());
                return error;
            } else {
                c[23] = "success";
            }
        }

     return new String[]{"success","transaction mapped successfully"};
     }
    
    public void isDBWrite(String[] c) {
        c[7] = ref;
        c[15] = "DB";
        c[25] = "dbload";
        c[27] = outdir;
        c[29] = "DB";
        c[6] = stctrl;
        c[5] = gsctrl;
        c[4] = isactrl;
        c[31] = "0";
        c[32]  = "99999";
        c[33] = "0";
        c[34] = "99999";
        c[35] = "0";
        c[36] = "0";
        c[37] = "0";

     if (GlobalDebug)
     System.out.println("Value of c within EDIMap class: " + String.join(",", c));

     }
    
    public void processDB(String[] c, String[] m) {
        c[23] = m[0];  // set return status
        c[38] = m[1];
    }
    
    public static void mapSegment(String segment, String element, String value) {
    	 String[] z = new String[] {element,value};
    	 // get old arraylist and add to it
    	 ArrayList<String[]> old = new ArrayList<String[]>();
    	 if (HASH.get(segment) != null) {
    		 old = HASH.get(segment);
    	 }
    	 old.add(z);
    	 HASH.put(segment, old);
     }
     
    public static void commitSegment(String segment) {
    	 // loop through HASH and create t for this segment
         commit++;
    	 HashMap<String, String> t = new LinkedHashMap<String,String>();
    	 Map<String, ArrayList<String[]>> X = new  LinkedHashMap<String, ArrayList<String[]>>(HASH);
    	 for (Map.Entry<String, ArrayList<String[]>> z : X.entrySet()) {
    		 if (z.getKey().equals(segment)) {
    			 ArrayList<String[]> k = z.getValue();
    			 for (String[] g : k) {
    				 t.put(g[0], g[1]);
    			 }
    			 
    		 }
    	 }
    	// HashMap<String, String> t = new HashMap<String,String>(j);
    	 if (! OMD.containsKey(segment)) {
    		OMD.put(segment + ":" + commit, t);
    	 }	
         
         HASH.clear();
     }
        
    public void readOSF(String osf)  {
        
	        Map<String, ArrayList<String[]>> hm = new LinkedHashMap<String, ArrayList<String[]>>();
	        List<String[]> list = new ArrayList<String[]>();
	        Set<String> set = new LinkedHashSet<String>();
	        File cf = new File(EDData.getEDIStructureDir() + "/" + osf);
	    	BufferedReader reader; 
        try {
            reader = new BufferedReader(new FileReader(cf));
        
			String line;
			while ((line = reader.readLine()) != null) {
                                if (line.startsWith("#")) {
				continue;
			        }
				if (! line.isEmpty()) {
				String[] t = line.split(",",-1);
				list.add(t);
				set.add(t[0]);
				}
			}
			reader.close();
			
			
			for (String s : set) {
				ArrayList<String[]> x = new ArrayList<String[]>();
				for (String[] ss : list) {
					if (ss[0].equals(s)) {
						x.add(ss);
					}
				}
				hm.put(s, x);
			}
			
		OSF = hm;
                } catch (FileNotFoundException ex) {
             edilog(ex);
            setError("outbound structure file not found: " + EDData.getEDIStructureDir() + "/" + osf);
        } catch (IOException ex) {
             edilog(ex);
            setError("outbound structure file IOException");
        }
}

    public static void readISF(String isf) {
        ArrayList<String[]> list = new ArrayList<String[]>();
        File cf = new File(EDData.getEDIStructureDir() + "/" + isf);
    	BufferedReader reader; 
        try {
            reader = new BufferedReader(new FileReader(cf));
         
		String line;
                LinkedHashMap<Integer, String[]> z = new LinkedHashMap<Integer, String[]>();
                int i = 0;
                String lastkey = "";
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			}
                        
			if (! line.isEmpty()) {
			String[] t = line.split(",",-1);
                             if (i == 0) { lastkey = t[0];}
                        if (GlobalDebug && t.length != 10) {
                        System.out.println("readISF: line " + i + " delimited count is not 10 " + t.length);
                        }
                        list.add(t);
                        
                        i++;
                            if (! t[0].equals(lastkey)) {
                                LinkedHashMap<Integer, String[]> w = z;
                                mISF.put(t[0], w);
                                z = new LinkedHashMap<Integer, String[]>();
                                i = 0;
                                z.put(i, t);
                                
                            } else {
                                z.put(i, t);
                                mISF.put(t[0], z);
                            }
                        lastkey = t[0];
			}
		}
		reader.close();
		ISF = list;
                
                
        }   catch (FileNotFoundException ex) {
            edilog(ex);
            setError("inbound structure file not found");
        } catch (IOException ex) {
            edilog(ex);
            setError("inbound structure file IOException");
        }
	}
     
    public static Map<String, HashMap<String, String>> readIMD(String isf, ArrayList<String> doc) throws IOException {
	        Map<String, HashMap<String, String>> hm = new LinkedHashMap<String, HashMap<String, String>>();
	        HashMap<String, String> data = new LinkedHashMap<String, String>();
	        
                List<String[]> list = new ArrayList<String[]>();
	        Set<String> set = new LinkedHashSet<String>();
	        File cf = new File(isf);
	    	BufferedReader reader =  new BufferedReader(new FileReader(cf)); 
			String line;
			while ((line = reader.readLine()) != null) {
				if (! line.isEmpty()) {
				String[] t = line.split(",",-1);
				list.add(t);
				set.add(t[0]);
				}
			}
			reader.close();
			
                        for (String r : doc) {
                            for (String s : set) {
                                if (r.startsWith(s)) {
                                    // got def of data record...now fill fields
                                    int start = 0;
                                   
                                    for (String[] ss : list) {
					if (ss[0].equals(s)) {
                                              //  System.out.println("field:" + ss[1] + " " + start + "/" + (Integer.valueOf(ss[3]) + start));
						if (start == 0) {
                                                    start = ss[0].length();
                                                }
                                                data.put(ss[1], r.substring(start,(Integer.valueOf(ss[3]) + start)));
					        start += Integer.valueOf(ss[3]);
                                                
                                        }
				    }
                                hm.put(s, data);
                                break;
                                }
                            }
                        }
			
	        return hm;
	    }
     
    public static String[] splitFFSegment(String segment) {
         boolean inside = false;
         int start = 0;
         ArrayList<String> list = new ArrayList<String>();
         for (String[] z : ISF) {
            // break if more ISF fields than actual segment  // TEV 20221005
            if ((Integer.valueOf(z[7]) + start) > segment.length()) {
                break;
            }
            // skip non-landmarks
            if (segment.startsWith(z[0])) {
                inside = true;
                 list.add(segment.substring(start,(Integer.valueOf(z[7]) + start)));
                 start += Integer.valueOf(z[7]);
            } else {
                inside = false;
            }
            if (! inside && start > 0) {  // should break out if end of target ISF definitions...to improve performance
                break;
            }
         }
        String[] x = new String[list.size()];
        x = list.toArray(x);
         return x;
     }
    
    public static String[] splitFFSegment(String segment, ArrayList<String[]> isf) {
         boolean inside = false;
         int start = 0;
         ArrayList<String> list = new ArrayList<String>();
         for (String[] z : isf) {
            // break if more ISF fields than actual segment  // TEV 20221005
            if ((Integer.valueOf(z[7]) + start) > segment.length()) {
                break;
            }
            // skip non-landmarks
            if (segment.startsWith(z[0])) {
                inside = true;
                 list.add(segment.substring(start,(Integer.valueOf(z[7]) + start)));
                 start += Integer.valueOf(z[7]);
            } else {
                inside = false;
            }
            if (! inside && start > 0) {  // should break out if end of target ISF definitions...to improve performance
                break;
            }
         }
        String[] x = new String[list.size()];
        x = list.toArray(x);
         return x;
     }
     
    
    public static LinkedHashMap<String, String[]> mapInput(String[] c, ArrayList<String> data, ArrayList<String[]> ISF) throws IOException {
        LinkedHashMap<String,String[]> mappedData = new LinkedHashMap<String,String[]>();
        HashMap<String,Integer> groupcount = new HashMap<String,Integer>();
        HashMap<String,Integer> set = new HashMap<String,Integer>();
        String parenthead = "";
        String groupkey = "";
        String previouskey = "";
        for (String s : data) {
                String[] x = null;
                if (c[28].equals("FF")) {
                    x = splitFFSegment(s);
                } else {
                    x = s.split("\\*",-1); // if x12
                }

                for (String[] z : ISF) {
                    // skip non-landmarks
                    if (! z[4].equals("yes")) {
                        continue;
                    }
                        if (x != null && (x.length > 1) && x[0].equals(z[0])) {
                                boolean foundit = false;
                                boolean hasloop = false;
                                String[] temp = parenthead.split(":");
                                for (int i = temp.length - 1; i >= 0; i--) {
                                        if (z[1].compareTo(temp[i]) == 0) {
                                                foundit = true;
                                                String[] newarray = Arrays.copyOfRange(temp, 0, i + 1);
                                                parenthead = String.join(":", newarray);							
                                                break;
                                        }
                                }
                                if (! foundit) {
                                continue;	
                                } else {
                                        int loop = 1;
                                        String groupparent = parenthead + ":" + x[0];
                                        if (groupcount.containsKey(groupparent)) {
                                                int g = groupcount.get(groupparent);

                                                if (previouskey.equals(parenthead + ":" + x[0] + "+" + g) && ! z[3].equals("yes")) {
                                                        loop = set.get(parenthead + ":" + x[0] + "+" + groupcount.get(groupparent));	
                                                        hasloop = true;
                                                        loop++;
                                                        set.put(parenthead + ":" + x[0] + "+" + groupcount.get(groupparent), loop);
                                                } else {
                                                        g++;	
                                                        groupcount.put(groupparent, g);
                                                }
                                        } else {
                                               // groupcount.put(groupparent, 1);
                                               if (groupcount.get(parenthead) != null) {
                                                  groupcount.put(groupparent, groupcount.get(parenthead)); 
                                               } else {
                                                  groupcount.put(groupparent, 1); 
                                               }
                                               
                                        }

                                        previouskey = parenthead + ":" + x[0] + "+" + groupcount.get(groupparent);	
                                        if (hasloop) {
                                            groupkey = parenthead + ":" + x[0] + "+" + groupcount.get(groupparent) + "+" + loop;
                                        } else {
                                            groupkey = parenthead + ":" + x[0] + "+" + groupcount.get(groupparent);
                                        }

                                        set.put(groupkey, loop);
                                        mappedData.put(parenthead + ":" + x[0] + "+" + groupcount.get(groupparent) + "+" + loop , x);
                                        SegmentCounter.add(parenthead + ":" + x[0] + "+" + groupcount.get(groupparent));
                                        if (z[3].equals("yes")) {
                                                parenthead = parenthead + (":" + z[0]);
                                        }
                                        break;
                                }
                        }
                }
        }
        return mappedData;
    }

    public static void debuginput(Map<String, String[]> mappedData) {
        if (GlobalDebug) {
            for (Map.Entry<String, String[]> z : mappedData.entrySet()) {
                    String value = String.join(",", z.getValue());
                    String[] keyx = z.getKey().split("\\+", -1);
                    String key = "";
                    if (keyx[0].contains(":")) {
                        key = keyx[0].substring(1);
                    } else {
                        key = keyx[0];
                    }
                    
                    int i = 0;
                    String fieldname = "";
                    for (String s : z.getValue()) {
                     if (mISF.get(key) != null && mISF.get(key).get(i) != null) {
                         String[] j = mISF.get(key).get(i);
                         if (j != null && j.length > 4) {
                             fieldname = j[5];
                         } else {
                             fieldname = "unknown";
                         }
                     }
                     System.out.println("mapInput: " + z.getKey() + " " + fieldname +  " / Field: " + i + " value: " + s);   
                     i++;
                    }
            }
            /*
            for (Map.Entry<String, HashMap<Integer,String[]>> y : mISF.entrySet()) {
                HashMap<Integer, String[]> w = y.getValue();
                for (Map.Entry<Integer, String[]> k : w.entrySet()) {
                  System.out.println("Extra: " + y.getKey() + " / " + k.getKey() + " / " + k.getValue()[5]);  
                }
            }
            */
        }
    }
    
    public static String[] writeOMD(String[] c, String[] tp) {
         String[] r = new String[2];
    	 String segment = "";
         String s = "";
         String e = "";
    	 content = "";
        
    	 Map<String, HashMap<String,String>> MD = new LinkedHashMap<String, HashMap<String,String>>(OMD);
    	 
    	 if (outputfiletype.equals("X12")) {
         segcount = 0;  // init segment count for this doc
         if (tp == null || tp[7].equals("0")) {
            s = delimConvertIntToStr("10"); // segment delimiter 
         } else {
            s = delimConvertIntToStr(tp[7]); // segment delimiter 
         }
         if (tp == null || tp[6].equals("0")) {
            e = delimConvertIntToStr("42"); // segment delimiter 
         } else {
            e = delimConvertIntToStr(tp[6]); // segment delimiter 
         }
         
    	 for (Map.Entry<String, HashMap<String,String>> z : MD.entrySet()) {
 		//	ArrayList<String[]> fields = z.getValue();
 			
                // write out all fields of this segment
                HashMap<String,String> mapValues = MD.get(z.getKey());
                // loop through integers

                if (GlobalDebug) {
                    System.out.println("OMD key/value: " + z.getKey() + " : " + mapValues);
                }
                        segment = z.getKey().split(":")[0];  // start with landmark
                
                        ArrayList<String[]> fields = OSF.get(segment);
                        ArrayList<String> segaccum = new ArrayList<String>();
                        segaccum.add(segment); 
                        
                        if (fields != null) { 
                        for (String[] f : fields) {
                                if (f[5].equals("landmark")) {
                                    continue;
                                }
                                // overlay with values that were actually assigned...otherwise blanks
                                if (mapValues.containsKey(f[5])) {
                                        if (mapValues.get(f[5]).length() > Integer.valueOf(f[8])) {
                                                segaccum.add(mapValues.get(f[5]).substring(0, Integer.valueOf(f[8])).trim()); // properly formatted
                                        } else {
                                                segaccum.add(mapValues.get(f[5]).trim()); // properly formatted
                                        }

                                } else {
                                    segaccum.add("");
                                }
                        }
                        } // if fields not null
                        segment = trimSegment(String.join(e,segaccum), e);
                        if (GlobalDebug) {
                            System.out.println("SegBeforeTrim: " + segaccum);
                            System.out.println("SegAfterTrim: " + segment);
                        }
                        segcount++;
                        content += segment + s;
                        segment = ""; // reset the segment string
 		}
         segcount += 2; // include ST and SE
         // wrap content with envelope
         } // if x12
         
         if (outputfiletype.equals("FF")) {
    	 for (Map.Entry<String, HashMap<String,String>> z : MD.entrySet()) {
 		//	ArrayList<String[]> fields = z.getValue();
 			
                // write out all fields of this segment
                HashMap<String,String> mapValues = MD.get(z.getKey());
        //	System.out.println("loopentrycount:" + mapValuesLoops.keySet());
                // loop through integers
 
                if (GlobalDebug) {
                    System.out.println("OMD: " + z.getKey() + " : " + mapValues);
                }               
                        segment = z.getKey().split(":")[0];  // start with landmark
                      //  System.out.println(">:" + segment);
                        ArrayList<String[]> fields = OSF.get(segment);
                        if (fields != null) { 
                        for (String[] f : fields) {
                                if (f[9].equals("+")) {
                                        f[9] = "";
                                }
                                String format = "%" + f[9] + f[7] + "s";

                                // overlay with values that were actually assigned...otherwise blanks
                                if (mapValues.containsKey(f[5])) {
                                        if (mapValues.get(f[5]).length() > Integer.valueOf(f[7])) {
                                                segment += String.format(format, mapValues.get(f[5]).substring(0, Integer.valueOf(f[7]))); // properly formatted
                                        } else {
                                                segment += String.format(format, mapValues.get(f[5])); // properly formatted
                                        }

                                } else {
                                        segment += String.format(format, ""); // properly formatted
                                }
                        }
                        } // if fields not null
                        
                         if (GlobalDebug) {
                            System.out.println("OMD segment: " + segment);
                        }
                        
                        content += segment + "\n";
                        segment = ""; // reset the segment string
 		}
         } // if FF
         
       
        // bsmf.MainFrame.show("here..." + outputfiletype + ": " + content);
    	OMD.clear();
        HASH.clear();
        ISF.clear();
        OSF.clear();
    	 
    	 return r;
     }
    
    public static boolean segmentExists(String segment, String qual, String elementName) {
        boolean segexists = false;
         int elementNbr = getElementNumber(segment, elementName); 
         if (elementNbr == 0) {
             return segexists;
         }
         String[] q = qual.split(":",-1);
         int qualNbr = getElementNumber(segment,q[0]);
         if (qualNbr == 0) {
             return segexists;
         }
        
         String[] t = null;
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             if (z.getKey().split("\\+")[0].equals(segment)) {
                 t = z.getValue();
                 if (t != null && t.length >= Integer.valueOf(qualNbr) && t[Integer.valueOf(qualNbr)].trim().equals(q[1].toUpperCase())) {
                     segexists = true;
                     break;
                 }
             }
         }
         
        return segexists;
    }
    
    // non-looping getInput
    public static String getInput(String segment, String qual, Integer elementNbr) {
        String x = "";
         int count = 0;
         String[] q = qual.split(":",-1);
         String[] k = null;
         String[] t = null;
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             if (z.getKey().split("\\+")[0].equals(segment)) {
                 count++;
                 t = z.getValue();
                // System.out.println("getInput: " + segment + "/" + qual + "/" + element + "/" + t.length + "/" + t[9]);
                 if (t != null && t.length >= Integer.valueOf(q[0]) && t[Integer.valueOf(q[0])].trim().equals(q[1].toUpperCase())) {
                     k = t;
                 }
             }
         }
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr].trim();
         }
         
         return x;
     }
    
    public static String getInput(String segment, String qual, String elementName) {
        String x = "";
         int elementNbr = getElementNumber(segment, elementName); 
         if (elementNbr == 0) {
             return x;
         }
         String[] q = qual.split(":",-1);
         int qualNbr = getElementNumber(segment,q[0]);
         if (qualNbr == 0) {
             return x;
         }
         String[] k = null;
         String[] t = null;
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             if (z.getKey().split("\\+")[0].equals(segment)) {
                 t = z.getValue();
                 if (t != null && t.length >= Integer.valueOf(qualNbr) && t[Integer.valueOf(qualNbr)].trim().equals(q[1].toUpperCase())) {
                     k = t;
                 }
             }
         }
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr];
         }
         return x;
     }
        
    public static String getInput(String segment, Integer elementNbr) {
         String x = "";
         int count = 0;
         String[] k = null;
         
         if (segment.startsWith(":") && segment.contains("+")) {  // overloading (again) as key type entry (used with getLoopKeys)
           k = mappedInput.get(segment);  
         } else { // else as actual segment entry
             segment = ":" + segment; // preprend blank
             for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
                 if (z.getKey().split("\\+")[0].equals(segment)) {
                     count++;
                     k = z.getValue();
                 }
             }
         }
       
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr];
         }
        
         if (GlobalDebug)
         System.out.println("getInput:" + segment + "/" + x);
         
         return x;
     }
    
    public static String getInput(String segment, String elementName) {
         String x = "";
         int elementNbr = getElementNumber(segment, elementName); 
         if (elementNbr == 0) {
             return x;
         }
         String[] k = null;
         if (segment.startsWith(":") && segment.contains("+")) {  // overloading (again) as key type entry (used with getLoopKeys)
           k = mappedInput.get(segment);  
         } else { // else as actual segment entry
             segment = ":" + segment; // preprend blank
             for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
                 if (z.getKey().split("\\+")[0].equals(segment)) {
                     k = z.getValue();
                 }
             }
         }
        // for (String g : k) {
        //     System.out.println("getInput:" + segment + "/" + g);
        // }
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr];
         }
         if (GlobalDebug)
         System.out.println("getInput:" + segment + "/" + x);
         
         return x;
     }
    
    // looping getInput
    public static String getInput(Integer gloop, String segment, Integer elementNbr) {
         String x = "";
         String[] k = null;
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 k = z.getValue();
             }
         }
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr];
         }
         return x;
     }
       
    public static String getInput(Integer gloop, String segment, String elementName) {
         String x = "";
         int elementNbr = getElementNumber(segment, elementName); 
         if (elementNbr == 0) {
             return x;
         }
        
         String[] k = null;
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 k = z.getValue();
             }
         }
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr];
         }
         return x;
     }
    
    public static String getInput(Integer gloop, String segment, String qual, Integer elementNbr) {
         String x = "";
         String[] k = null;
         String[] q = qual.split(":",-1);
         int qualNbr = getElementNumber(segment,q[0]);
         if (qualNbr == 0) {
             return x;
         }
         String[] t = null;
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 t = z.getValue();
                 if ( (t != null) && (t.length >= qualNbr) && (t[qualNbr].equals(q[1].toUpperCase())) ) {
                     k = t;
                 } 
             }
         }
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr];
         }
         return x;
     }
    
    public static String getInput(Integer gloop, String segment, String qual, String elementName) {
         String x = "";
         String[] k = null;
         int elementNbr = getElementNumber(segment, elementName); 
         if (elementNbr == 0) {
             return x;
         }
         String[] q = qual.split(":",-1);
         int qualNbr = getElementNumber(segment,q[0]);
         if (qualNbr == 0) {
             return x;
         }
         String[] t = null;
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 t = z.getValue();
                 if ( (t != null) && (t.length >= qualNbr) && (t[qualNbr].equals(q[1].toUpperCase())) ) {
                     k = t;
                 } 
             }
         }
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr];
         }
         return x;
     }
        
    public static int getElementNumber(String segment, String element) {
         boolean inside = false;
         int x = 0;
         int r = 0;
         if (BlueSeerUtils.isParsableToInt(element)) {
             return Integer.valueOf(element);
         }
         
         for (String[] z : ISF) {
            if (segment.startsWith(z[0])) {
                if (! z[5].equals("landmark")) {
                  x++;
                }
                if (element.toLowerCase().equals(z[5].toLowerCase())) {
                    r = x;
                    break;
                }
            } else {
                inside = false;
            }
            if (! inside && r > 0) {  // should break out if end of target ISF definitions...to improve performance
                break;
            }
         }
         if (GlobalDebug) {
         System.out.println("getElementNumber: " + segment + "/" + element + "/" + r);
         }
         return r;
     }
    
    public static int getGroupCount(String segment) {
         
         int count = 0;
         String[] k = null;
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             if (z.getKey().split("\\+")[0].equals(segment)) {
                 count++;
             }
         }
        
         return count;
     }
     
    public static ArrayList<String> getLoopKeys(String segment) {
         ArrayList<String> k = new ArrayList<String>();
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v.length == 3) {
                 k.add(v[0] + "+" + v[1] + "+" + v[2]);
             }
         }
         return k;
     }
    
    public static ArrayList<String> getLoopKeys(String segment, Integer g) {
         ArrayList<String> k = new ArrayList<String>();
         segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(g)) && v.length == 3) {
                 k.add(v[0] + "+" + v[1] + "+" + v[2]);
             }
         }
         return k;
     }
        
    public class UserDefinedException extends Exception  
    {  
        public UserDefinedException(String str)  
        {  
            // Calling constructor of parent Exception  
            super(str);  
        }  
    }   
}
