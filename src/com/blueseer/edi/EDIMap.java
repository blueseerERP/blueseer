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
import static com.blueseer.edi.ediData.getDSFasString;
import static com.blueseer.edi.ediData.getMapMstr;
import static com.blueseer.edi.ediData.isSuppressEmptyTag;
import com.blueseer.edi.ediData.jsonRecord;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.BlueSeerUtils.bsNode;
import com.blueseer.utl.BlueSeerUtils.bsTree;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.xNull;
import com.blueseer.utl.EDData;
import com.blueseer.utl.OVData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jcifs.smb.SmbException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author terryva
 */
public abstract class EDIMap {  // took out the implements EDIMapi
    
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
     public static String inputfiletype = "";
     public static String inputdoctype = "";
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
     
     public static String osd = "";
     public static String oed = "";
     public static String oud = "";
     
     public static String content = "";

    public record segRecord(int p, String[] m) {
    }
    
    public record stackGHP(Stack<String> s, int i) {
    }
       
    public static LinkedHashMap<String, Integer> commitCounter = new LinkedHashMap<String, Integer>();
    public static LinkedHashMap<String, Integer> commitLoopCounter = new LinkedHashMap<String, Integer>();
    
    public static ArrayList<String[]> mxs = new ArrayList<String[]>();
    
    public static LinkedHashMap<String, ArrayList<String[]>> HASH = new  LinkedHashMap<String, ArrayList<String[]>>();
    public static LinkedHashMap<String, String[]> mappedInput = new  LinkedHashMap<String, String[]>();
    public static LinkedHashMap<String, ArrayList<String[]>> OSF = new  LinkedHashMap<String, ArrayList<String[]>>();
    public static ArrayList<String[]> ISF = new  ArrayList<String[]>();
    public static LinkedHashMap<String, HashMap<String,String>> OMD = new LinkedHashMap<String, HashMap<String,String>>();
    public static ArrayList<String> SegmentCounter = new ArrayList<String>();

    public static boolean isSet(ArrayList list, Integer index) {
     return index != null && index >=0 && index < list.size() && list.get(index) != null;
     }

    public static boolean isSet(String[] list, Integer index) {
     return index != null && index >=0 && index < list.length && list[index] != null;
     }

    public static void clearStaticVariables() {
        OMD.clear();
        HASH.clear();
        ISF.clear();
        OSF.clear();
        commitCounter.clear();
        commitLoopCounter.clear();
        mxs.clear();
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
    
    public void setOutputStructureFile(String ofs, String ofstype) {
        ofsfile = ofs;
        if (ofstype.toUpperCase().toUpperCase().equals("XML") || ofstype.toUpperCase().toUpperCase().equals("JSON")) {
            readOSFTreeType(ofsfile); // revert back to readOSF
        } else {
            readOSF(ofsfile);
        }
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
        inputfiletype = c[28];
     //   outsender = insender;  // can override within map
    //    outreceiver = inreceiver; // can override within map
        doctype = c[1];
        outputdoctype = c[15]; // can override within map
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
        
        osd = EDI.escapeDelimiter(delimConvertIntToStr(c[35]));
        oed = EDI.escapeDelimiter(delimConvertIntToStr(c[36]));
        oud = EDI.escapeDelimiter(delimConvertIntToStr(c[37]));
        
        
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
           setOutputStructureFile(x.map_ofs(), x.map_outfiletype());
       //   bsmf.MainFrame.show(outsender + "/" + outreceiver + "/" + outputdoctype + "/" + outputfiletype + "/" + ifsfile + "/" + ofsfile);
           return;
           
        }
        
        if (c[28].toUpperCase().equals("X12")) {
        outsender = gsArrayIN[2];
        outreceiver = gsArrayIN[3];
        } else {
        outsender = c[0];  // senderid
        outreceiver = c[21]; // receiverid
        }
        
        String[] tp = EDData.getEDITPDefaults(c[1], outsender, outreceiver );
    //    bsmf.MainFrame.show(outputdoctype + "/" + outsender + "/" + outreceiver );
    //    bsmf.MainFrame.show(tp[14] + "/" + tp[15] + "/" + tp[16] + "/" + tp[17] );
        
        osd = EDI.escapeDelimiter(delimConvertIntToStr(tp[7]));
        oed = EDI.escapeDelimiter(delimConvertIntToStr(tp[6]));
        oud = EDI.escapeDelimiter(delimConvertIntToStr(tp[8]));
    
        setOutPutDocType(tp[14]);
        setOutPutFileType(tp[15]);
        if (! tp[21].equals("DB")) {
        setInputStructureFile(tp[16]);
        }
        if (! tp[15].equals("DB")) {
        setOutputStructureFile(tp[17], tp[15]);
        }
     }

    public void setReference(String key) {
        ref = key;
    }
    
    public static String delimConvertIntToStr(String intdelim) {
    String delim = "";
    if (intdelim.isBlank()) {
        return "";
    }
    int x = Integer.valueOf(intdelim);
    delim = String.valueOf(Character.toString((char) x));
    return delim;
  }
   
     
    
    public void setOutPutEnvelopeStrings(String[] c) {         
         if ( ! isOverride) {  // if not override...use internal partner / doc lookup for envelope info
           
           if (c[29].toUpperCase().equals("X12")) {  
             if (c[0].equals("MapTester")) {
                envelope = EDI.generateEnvelope(c[1], c[13], c[21]);  //override use of c[13] from mapper ddsenderenvelope
             }  else {
                envelope = EDI.generateEnvelope(c[1], c[0], c[21]); 
             }
              // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber, gsctrlnbr)
           } else if(c[29].toUpperCase().equals("UNE")) {
             envelope = EDI.generateEnvelopeUNE(c[1], c[0], c[21]); // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber, gsctrlnbr)
           } else {
               return;
           }
           
           
           String ed = envelope[8];
           String ud = envelope[9];
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
           
            ArrayList<String> attrs = EDData.getEDIAttributesList(c[1], c[0], c[21]); 
            Map<String, String> attrkeys = new HashMap<String, String>();
            for (String x : attrs) {
                String[] z = x.split(":", -1);
                if (z != null && ! z[0].isEmpty()) {
                    attrkeys.put(z[0], z[1]);
                }
            }
           
           // for UNH of Edifact UNH+1+ORDERS:D:98A:UN:FUN02G' // last one is conditional
           StringBuilder unhe02 = new StringBuilder();
           String[] unhe02_array = null;
           if (attrkeys.containsKey("UNHE02")) {
               unhe02_array = attrkeys.get("UNHE02").split(":",-1);
           }
           unhe02.append(EDData.getEDIGSTypeFromBSDoc(outputdoctype));
           if (unhe02_array == null || unhe02_array.length == 0) {
             unhe02.append(":D:98A:UN"); // fallback if no EDI attributes found for key UNHE02
           } else {
               for (String ux : unhe02_array ) {
                   if (! ux.isBlank()) {
                     unhe02.append(ud);
                     unhe02.append(ux);
                   }
               }
           }
           
           
           if (c[29].toUpperCase().equals("X12")) { 
           ST = "ST" + ed + EDData.getEDIDocTypeFromBSDoc(outputdoctype) + ed + stctrl ;
           SE = "SE" + ed + String.valueOf(segcount) + ed + stctrl;  
           }  else {
           ST = "UNH" + ed + stctrl + ed + unhe02;
           SE = "UNT" + ed + String.valueOf(segcount) + ed + stctrl;  
           }
           
           overrideISAWithMapEntries(ed); 
           overrideGSWithMapEntries(ed);
           
          
           } else {
             // you can override elements within the envelope xxArray fields at this point....or merge into segment string
             // need to figure out what kind of error this bullshit is....
             
             if (ed.isBlank()) {
              ed = delimConvertIntToStr("42");   
             }
             ISA = c[13];
             GS = c[14];
             GE = "GE" + ed + "1" + ed + c[5];
             IEA = "IEA" + ed + "1" + ed + c[4];
             ST = "ST" + ed + EDData.getEDIDocTypeFromBSDoc(outputdoctype) + ed + c[6]; 
             SE = "SE" + ed + "1" + ed + c[6];

             xsetISA(9,""); // set date to now
             xsetISA(10,"");  // set time to now

           
             header = "";
             detail = "";
             trailer = "";
             content = "";
           }

     }

    public void overrideISAWithMapEntries(String _ed) {
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
        ISA = String.join(_ed,isaArray);
    }
    
    public void overrideGSWithMapEntries(String _ed) {
        for (int i = 1; i < mapGSArray.length; i++ ) { // skip 0 ...ISA landmark element
            if (mapGSArray[i] != null && ! mapGSArray[i].isEmpty()) {
                gsArray[i] = String.valueOf(mapGSArray[i]);
            }
        }
        GS = String.join(_ed,gsArray);
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
       return isaArrayIN[i].trim();
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
            if (! c[21].equals("MapTester") && c[29].equals("X12")) { // override with ddenvelope choice for enveloping segments
               String[] tp = EDData.getEDITPDefaults(c[1], c[13], c[21] ); // override use of c[13] isa string for 'sender'
               tp[7] = (tp[7].isBlank() || tp[7].equals("0")) ? "10" : tp[7];
               tp[6] = (tp[6].isBlank() || tp[6].equals("0")) ? "42" : tp[6];
               tp[8] = (tp[8].isBlank() || tp[8].equals("0")) ? "126" : tp[8];
               
               writeOMD(c, tp);
               setOutPutEnvelopeStrings(c);
               String s = delimConvertIntToStr(tp[7]); // segment delimiter
               content = ISA + s + GS + s + ST + s + content  + SE + s + GE + s + IEA + s;
            } else {
               writeOMD(c, null); 
            }
            
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
        
        if (c[29].toUpperCase().equals("X12") || c[29].toUpperCase().equals("UNE")) {
           setOutPutEnvelopeStrings(c);
          
           if (c[29].toUpperCase().equals("X12")) {
           tp[7] = (tp[7].isBlank() || tp[7].equals("0")) ? "10" : tp[7];
           tp[6] = (tp[6].isBlank() || tp[6].equals("0")) ? "42" : tp[6];
           tp[8] = (tp[8].isBlank() || tp[8].equals("0")) ? "126" : tp[8];
           }
           
           if (c[29].toUpperCase().equals("UNE")) {
           tp[7] = (tp[7].isBlank() || tp[7].equals("0")) ? "39" : tp[7];
           tp[6] = (tp[6].isBlank() || tp[6].equals("0")) ? "43" : tp[6];
           tp[8] = (tp[8].isBlank() || tp[8].equals("0")) ? "58" : tp[8];
           }
           
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
            outdir = cleanDirString(tp[9]);
            if (outdir.isEmpty()) {
                outdir = cleanDirString(EDData.getEDIOutDir());
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
        // c[29] ...(outboundfiletype) should be defined at first available getTPDefaults prior to mapping
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
            EDI.writeFile(content, cleanDirString(EDData.getEDIBatchDir()), batchfile);
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
           
    public void readOSF(String osf)  {
        
        LinkedHashMap<String, ArrayList<String[]>> hm = new LinkedHashMap<String, ArrayList<String[]>>();
        List<String[]> list = new ArrayList<String[]>();
        Set<String> set = new LinkedHashSet<String>();
        ArrayList<String> lines = getDSFasString(osf);

        if (lines == null || lines.size() == 0) {
            setError("Structure File (dsf) not available: " + osf);
            return;
        }
        
                for (String line : lines) {
                        if (line.startsWith("#")) {
                        continue;
                        }
                        if (! line.isEmpty()) {
                        String[] t = line.split(",",-1);
                               list.add(t);
                              set.add(t[0]);
                        }
                }


                ArrayList<String> excludes = new ArrayList<String>();
                for (String s : set) {
                        ArrayList<String[]> x = new ArrayList<String[]>();
                        for (String[] ss : list) {
                                if (ss[0].equals(s) && ! excludes.contains(s + ss[5])) {  //prevent duplicate fields
                                        x.add(ss);
                                        excludes.add(s + ss[5]);
                                }
                        }
                        hm.put(s, x);
                }
        OSF = hm;
           
}

    public void readOSFTreeType(String osf)  {
        
        LinkedHashMap<String, ArrayList<String[]>> hm = new LinkedHashMap<String, ArrayList<String[]>>();
        ArrayList<String[]> list = new ArrayList<String[]>();
        Set<String> set = new LinkedHashSet<String>();
        String tag = "";
        String ptag = "";
        String ckey = "";
        ArrayList<String> lines = getDSFasString(osf);

        if (lines == null || lines.size() == 0) {
            setError("Structure File (dsf) not available: " + osf);
            return;
        }	
        for (String line : lines) {
                if (line.startsWith("#")) {
                continue;
                }
                if (! line.isEmpty()) {
                    String[] t = line.split(",",-1);
                    tag = t[0];
                    ptag = t[1];
                    if (ptag.isBlank()) {
                        ckey = tag;
                    } else {
                        ckey = ptag + ":" + tag;
                    }
                    if (hm.containsKey(ckey)) {
                      ArrayList<String[]> xlist = hm.get(ckey);
                      xlist.add(t);
                      hm.put(ckey, xlist);
                    }  else {
                      ArrayList<String[]> x = new ArrayList<String[]>();
                      x.add(t);
                      hm.put(ckey, x);
                    }  
                }
        }
        OSF = hm;
          
}

    public static void readISF(String isf) {
        ArrayList<String[]> list = new ArrayList<String[]>();
        ArrayList<String> lines = getDSFasString(isf);
        int i = 0;
        String lastkey = "";
        LinkedHashMap<Integer, String[]> z = new LinkedHashMap<Integer, String[]>();
        
        if (! isf.equals("DB") && (lines == null || lines.size() == 0)) {
            setError("Structure File (dsf) not available: " + isf);
            return;
        }
        
        for (String line : lines) {
            
            if (line.startsWith("#")) {
            continue;
            }
                        
            if (! line.isEmpty()) {
                String[] t = line.split(",",-1);
                
                if (GlobalDebug && t.length < 11) {
                 System.out.println("readISF: line " + i + " delimited count is less than 11 " + t.length);
                }
            
                list.add(t);
            } 
        }
        ISF = list;
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
            if (z[5].equals("groupend")) {
                  continue;
            }  
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
            if (z[5].equals("groupend")) {
                  continue;
            }  
            
            
            // skip non-landmarks
            if (segment.startsWith(z[0])) {
                // break if more ISF fields than actual segment  // TEV 20221005
                if ((Integer.valueOf(z[7]) + start) > segment.length()) {
                    break;
                }
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
    
    public static segRecord getSegmentInISF(String rawSegmentLM, String rawGroupHeadLM, ArrayList<String[]> isf) {
        
        String[] segment = null;
        int i = 0;
        for (String[] x : isf) {
            i++;
            if (x[5].equals("groupend")) {
                  continue;
            }
          //  System.out.println("HERE: " + rawSegmentLM + "/" + rawGroupHeadLM + "/" + x[0] + "/" + x[1]);
            if (rawSegmentLM.equals(x[0]) && rawGroupHeadLM.equals(x[1])) {
                segment = x;
                break;
            }
        }
        return new segRecord(i,segment);
    }
    
    public static String[] getFieldsFromStructure(String tag, String parent, ArrayList<String[]> isf) {
        ArrayList<String> temp = new ArrayList<String>();
        int i = 0;
        for (String[] x : isf) {
            i++;
            if (x[4].toLowerCase().equals("yes") || x[5].equals("groupend")) {
                  continue;
            }
          //  System.out.println("HERE: " + rawSegmentLM + "/" + rawGroupHeadLM + "/" + x[0] + "/" + x[1]);
            if (tag.equals(x[0]) && parent.equals(x[1])) {
                temp.add(x[5]); // should be only fields, attributes
            }
        }
        return temp.toArray(new String[0]);
    }
    
    
    public static stackGHP preGroupHead(String rawSegmentLM, Stack<String> instack, ArrayList<String[]> isf, int GHP) {
        String currentGroupHeadLM = String.join(":",instack.toArray(new String[instack.size()])); 
        Stack<String> stack = new Stack<String>();
        stack = (Stack<String>) instack.clone();
        int i = 0;
        
      //  System.out.println("vars: " + rawSegmentLM + " current group head: " + currentGroupHeadLM + " count: " + GHP );
        
        for (String[] x : isf) {
            i++;
            if (i < GHP) {
                continue;
            }
           
            
            if (! x[4].toLowerCase().equals("yes")) {
                continue;
            }
            
            if (x[5].equals("groupend")) {
                  continue;
            } 
            /*
            if (rawSegmentLM.equals(x[0])) {
            System.out.println("RawSegment:" + rawSegmentLM + " currentgrouphead=" + currentGroupHeadLM + "  x01=" + x[1] + "  GHP=" + GHP );
            }
            */
            if (rawSegmentLM.equals(x[0]) && x[1].isBlank()) {
               stack.removeAllElements();
               GHP = i;
               
               if (GlobalDebug) {
               System.out.println("PRE:" + " IncomingSegment: " + rawSegmentLM + "  GroupHead:" + currentGroupHeadLM + "  x[1]: " + x[1] + " GHP=" + GHP);
               }
               
               
               break;  
            }
            
            if (rawSegmentLM.equals(x[0]) && currentGroupHeadLM.equals(x[1])) {
                break;
            }
            
            if (rawSegmentLM.equals(x[0]) && ! currentGroupHeadLM.equals(x[1])) {
                if (! x[1].isBlank()) {
                  stack.removeAllElements();
                  if (! x[1].contains(":")) {
                      stack.push(x[1]);
                  } else {
                      String[] arr = x[1].split(":");
                      for (String e : arr) {
                          stack.push(e);
                      }
                  }
                  String newGroup = String.join(":",stack.toArray(new String[stack.size()]));
                  if (GlobalDebug) {
                  System.out.println("DropDown:" + " IncomingSegment: " + rawSegmentLM + "  OldGroupHead:" + currentGroupHeadLM +  "  NewGroupHead:" + newGroup + "  x[1]: " + x[1] + " GHP=" + GHP);
                  }
                }
                break;
            }
            
        } // for each isf
        
        return new stackGHP(stack, GHP);
    }
    
    public static stackGHP preGroupHeadRLoop(String rawSegmentLM, Stack<String> instack, ArrayList<String[]> isf, int GHP) {
        String currentGroupHeadLM = String.join(":",instack.toArray(new String[instack.size()])); 
        Stack<String> stack = (Stack<String>) instack.clone();
      //  System.out.println("MATCH incoming: " + rawSegmentLM + "/" + currentGroupHeadLM);
        int i = 0;
        boolean isMatch = false;
        boolean isFieldInParent = false;
        String matchparent = "";
        long count = currentGroupHeadLM.chars().filter(ch -> ch == ':').count();
        for (long j = count; j >= 0; j--) {
            for (String[] x : isf) {
            if (rawSegmentLM.equals(x[5]) && currentGroupHeadLM.equals(x[1] + ":" + x[0])) {
                isFieldInParent = true;
              //  System.out.println("MATCH FIELD: " + rawSegmentLM + "/" + currentGroupHeadLM);
                break;
            }
            if (! x[4].toLowerCase().equals("yes")) {
                continue;
            }
            
            if (x[5].equals("groupend")) {
                  continue;
            } 
            if (rawSegmentLM.equals(x[0]) && currentGroupHeadLM.equals(x[1])) {
                isMatch = true;
                matchparent = x[1];
              //  System.out.println("MATCH GROUP: " + rawSegmentLM + "/" + currentGroupHeadLM);
                break;
            }
        }
            if (isMatch || isFieldInParent) {
                break;
            }
            if (stack.size() > 0) {
            stack.pop();
            currentGroupHeadLM = String.join(":",stack.toArray(new String[stack.size()])); 
            }
        }
       
        return new stackGHP(stack, GHP);
        
       
        
    }
    
    public static stackGHP preGroupHeadRecursivebkup(String rawSegmentLM, Stack<String> instack, ArrayList<String[]> isf, int GHP) {
        String currentGroupHeadLM = String.join(":",instack.toArray(new String[instack.size()])); 
        System.out.println("MATCH incoming: " + rawSegmentLM + "/" + currentGroupHeadLM);
        Stack<String> stack = new Stack<String>();
        Stack<String> rstack = new Stack<String>();
        
        stack = (Stack<String>) instack.clone();
        rstack = (Stack<String>) instack.clone();
        int i = 0;
        
      //  System.out.println("vars: " + rawSegmentLM + " current group head: " + currentGroupHeadLM + " count: " + GHP );
        boolean isMatch = false;
        boolean isFieldInParent = false;
        String matchparent = "";
        
        
       // if (stack.size() <= 0) {
       //    return new stackGHP(stack, GHP); 
       // }
        
        for (String[] x : isf) {
            if (rawSegmentLM.equals(x[5]) && currentGroupHeadLM.equals(x[1] + ":" + x[0])) {
                isFieldInParent = true;
                break;
            }
            if (! x[4].toLowerCase().equals("yes")) {
                continue;
            }
            
            if (x[5].equals("groupend")) {
                  continue;
            } 
            if (rawSegmentLM.equals(x[0]) && currentGroupHeadLM.equals(x[1])) {
                isMatch = true;
                matchparent = x[1];
                //System.out.println("MATCH: " + x[0] + "/" + x[1]);
                break;
            }
        }
        
        // if true then return Parent
        if (isMatch || isFieldInParent ) {
            System.out.println("MATCH ISMATCH: " + rawSegmentLM + "/" + currentGroupHeadLM + " "  + stack.size());
            rstack = stack;
          //  return new stackGHP(stack, GHP);
        } else {
            if (stack.size() > 0) {
            System.out.println("MATCH POPPING: " + rawSegmentLM + "/" + currentGroupHeadLM + " "  + stack.size());
            stack.pop();
            Stack<String> newstack = (Stack<String>) stack.clone();
            preGroupHeadRecursivebkup(rawSegmentLM, newstack, isf, GHP);
            }
        }
        // if not...then recurse with popped parent
        
      
        System.out.println("MATCH: " + rawSegmentLM +  " END SIZE: " + rstack.size());
        return new stackGHP(rstack, GHP);
    }
    
    
    public static stackGHP postGroupHead(String rawSegmentLM, Stack<String> instack, ArrayList<String[]> isf, int GHP) {
        String currentGroupHeadLM = String.join(":",instack.toArray(new String[instack.size()])); 
        Stack<String> stack = new Stack<String>();
        stack = (Stack<String>) instack.clone();
        int i = 0;
        for (String[] x : isf) {
            i++;
            
            if (i < GHP) {
                continue;
            }
            
            if (! x[4].toLowerCase().equals("yes")) {
                continue;
            }
            if (x[5].equals("groupend")) {
                  continue;
            } 
            /*
            if (rawSegmentLM.equals(x[0])) {
              System.out.println("PostCheck:" + rawSegmentLM + " currentGroup= " + currentGroupHeadLM +  " incoming:" + x[0]);  
            }
            */
            
            if (rawSegmentLM.equals(x[0]) && (currentGroupHeadLM.equals(x[1])) && x[4].toLowerCase().equals("yes")) {
                stack.push(rawSegmentLM);
                if (stack.get(0).equals(x[0]) && x[1].isBlank()) {
                    GHP = i;
                }
                
                if (GlobalDebug) {
                System.out.println("POST:" + " IncomingSegment: " + rawSegmentLM + "  GroupHead:" + currentGroupHeadLM + "  xx[1]: " + x[1] + " GHP=" + GHP);
                }
               
                
                break;
            }
            
        }
        return new stackGHP(stack, GHP);
    }
        
    public static LinkedHashMap<String, String[]> mapInput(String[] c, ArrayList<String> data, ArrayList<String[]> ISF)  {
        LinkedHashMap<String,String[]> mappedData = new LinkedHashMap<String,String[]>();
        HashMap<String,Integer> groupcount = new HashMap<String,Integer>();
        HashMap<String,Integer> set = new HashMap<String,Integer>();
        String parenthead = "";
        String groupkey = "";
        String previouskey = "";
        String mappedinput = "";
        String eledelim = "";
        Stack<String> stack = new Stack<String>();
        String[] delims = new String[]{c[9], c[10], c[11]}; 
        if (BlueSeerUtils.isParsableToInt(delims[1])) {
            eledelim = EDI.escapeDelimiter(delimConvertIntToStr(delims[1]));
        } else {
            eledelim = EDI.escapeDelimiter(delims[1]);
        }
        
     //  bsmf.MainFrame.show(delims[0] + "/" + delims[1] + "/" + delims[2]);
        int currentPosition = 0;
        int GHP = 0;
        Stack<Integer> positionStack = new Stack<Integer>();
        
        List<String[]> dataAsArrays = null;
        if (c[28].toUpperCase().equals("JSON")) {
            try {
               return mappedData = jsonToSegments(data.get(0));
               
            } catch (IOException ex) {
                edilog(ex);
            }
        } else if (c[28].toUpperCase().equals("XML")) {   
            try {
               //dataAsArrays = xmlToSegments(data.get(0), ISF);
               return mappedData = xmlToSegments(data.get(0),ISF);
            } catch (IOException ex) {
                edilog(ex);
            }
        } else if (c[28].toUpperCase().equals("CSV")) {   
            try {
                return mappedData = csvToSegments(data, eledelim);
            } catch (IOException ex) {
                edilog(ex);
            }
               
        } else {
          dataAsArrays  = new ArrayList<String[]>();
            for (String s : data) {
                    String[] x = null;
                    if (c[28].equals("FF")) {
                        x = splitFFSegment(s, ISF);
                    } else {
                        x = s.split(eledelim,-1); // delims = seg, ele, sub
                    }
                    dataAsArrays.add(x);
            }
        }
        
        if (dataAsArrays != null) {
        for (String[] x : dataAsArrays) {
                
                if (x == null || x.length == 0) {
                    continue;
                }
                
                String[] IFSseg = null;
                if(c[28].toUpperCase().equals("XML")) {
                    // identify immediate parent/group head
                stackGHP sp = preGroupHeadRLoop(x[0], stack, ISF, GHP);
                stack = sp.s();
                GHP = sp.i();
                parenthead = String.join(":",stack.toArray(new String[stack.size()]));
                // now lets find segments place in ISF
              //  System.out.println("incoming segment:" + x[0] + " with parenthead: " + parenthead);
                segRecord sr = getSegmentInISF(x[0], parenthead, ISF);
                IFSseg = sr.m();
                currentPosition = sr.p();
                } else {
                // identify immediate parent/group head
                stackGHP sp = preGroupHead(x[0], stack, ISF, GHP);
                stack = sp.s();
                GHP = sp.i();
                parenthead = String.join(":",stack.toArray(new String[stack.size()]));
                // now lets find segments place in ISF
               // System.out.println("incoming segment:" + x[0] + " with parenthead: " + parenthead);
                segRecord sr = getSegmentInISF(x[0], parenthead, ISF);
                IFSseg = sr.m();
                currentPosition = sr.p();
                }
                
                
               
                
                if (IFSseg != null) {
                   if (GlobalDebug) {
                   System.out.println("WRITE:" + " IncomingSegment: " + x[0] +  "  x[1]: " + IFSseg[1] + "  ParentHead:" + parenthead + " GHP=" + GHP);
                   }
                    
                    if (! parenthead.isBlank()) {
                        parenthead = parenthead + ":";
                    }
                    
                    int loop = 1;
                    boolean hasloop = false;
                    String groupparent = parenthead + x[0];
                    
                    
                    
                    if (groupcount.containsKey(groupparent)) {
                            int g = groupcount.get(groupparent);

                            if (previouskey.equals(parenthead + x[0] + "+" + g) && ! IFSseg[3].toLowerCase().equals("yes")) {
                                    loop = set.get(parenthead + x[0] + "+" + groupcount.get(groupparent));	
                                    hasloop = true;
                                    loop++;
                                    set.put(parenthead + x[0] + "+" + groupcount.get(groupparent), loop);
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

                    previouskey = parenthead + x[0] + "+" + groupcount.get(groupparent);	
                    if (hasloop) {
                        groupkey = parenthead + x[0] + "+" + groupcount.get(groupparent) + "+" + loop;
                    } else {
                        groupkey = parenthead + x[0] + "+" + groupcount.get(groupparent);
                    }

                    mappedinput = parenthead + x[0] + "+" + groupcount.get(groupparent) + "+" + loop + "=" + String.join(",",x);
              //      System.out.println("HERE: " + mappedinput);
                    set.put(groupkey, loop);
                    mappedData.put(parenthead + x[0] + "+" + groupcount.get(groupparent) + "+" + loop , x);
                    SegmentCounter.add(parenthead + x[0] + "+" + groupcount.get(groupparent));
                   
                }  // if foundit
             
                if (GlobalDebug && IFSseg == null) {
                System.out.println("ifSeg is null: " + x[0] + " with parenthead: " + parenthead);
                }
               
              //  if(! c[28].toUpperCase().equals("XML")) {
                stackGHP postGH = postGroupHead(x[0], stack, ISF, GHP);
                stack = postGH.s();
                GHP = postGH.i(); 
                parenthead = String.join(":",stack.toArray(new String[stack.size()])); 
              //  }
        }
        }
        return mappedData;
    }
  
    public static LinkedHashMap<String, String[]> csvToSegments(ArrayList<String> data, String eledelim) throws IOException {
	   
            LinkedHashMap<String,String[]> lhm = new LinkedHashMap<String,String[]>();
	    int lc = 0;
            for (String s : data) {
                lc++;
                s = "ROW" + eledelim + s; // prepend row on each line of csv file
                String[] recs = s.split(eledelim, -1);
                lhm.put("ROW" + "+1+" + lc, recs); 
            }
            // lhm.forEach((k,v) -> System.out.println("lhmnew: " + k + " = " + v));
	    return lhm;
	}
    
    
    public static LinkedHashMap<String, String[]> jsonToSegments(String json) throws IOException {
	    ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
	    JsonParser jsonParser = jsonNode.traverse();
	    Stack<String> segments = new Stack<String>();
            Stack<String> arraystack = new Stack<String>();
            LinkedHashMap<String,Integer> arraycounter = new LinkedHashMap<String,Integer>();
            LinkedHashMap<String,String[]> lhm = new LinkedHashMap<String,String[]>();
	   JsonToken z = null;
           JsonToken previous = null;
	    while (!jsonParser.isClosed()) {
	    	
	    	JsonToken x = jsonParser.nextToken();
             
	    	if (x == JsonToken.FIELD_NAME) {
	    		z = jsonParser.nextToken();
                        
	    		if (z.name().startsWith("VALUE")) {
                          String currentstack = String.join(":",segments.toArray(new String[segments.size()])); 
                        //  String currentarraystack = String.join(":",arraystack.toArray(new String[segments.size()])); 
                          
                          String lc = "";
                          for (String g : segments) {
                              if (arraycounter.containsKey(g)) {
                                 lc += String.valueOf(arraycounter.get(g)) + ","; 
                              } else {
                                  lc += "1" + ",";
                              }
                          }
                          lc = lc.replaceAll(",$", "");
                          lhm.put(currentstack + ":" + jsonParser.getCurrentName() + "+" + lc, new String[]{jsonParser.getText()});
                          
                	} else if(z == JsonToken.START_OBJECT) {
                                segments.push(jsonParser.getCurrentName());
	    		} else if(z == JsonToken.START_ARRAY) {
	    			segments.push(jsonParser.getCurrentName());
                                arraystack.push(jsonParser.getCurrentName());
                                arraycounter.put(jsonParser.getCurrentName(), 1);
                               
	    		} else if(z == JsonToken.END_ARRAY) {
	    			segments.pop();
                                arraystack.pop();
	    		} else if(z == JsonToken.END_OBJECT) {
	    			segments.pop();
	    		}
	    	}
	    	if (x == JsonToken.START_OBJECT) {
                        if (! arraystack.empty()) {
                            if (jsonParser.getCurrentName() != null) {
                             segments.push(jsonParser.getCurrentName());
                            }
                        }
	    	}
	    	if (x == JsonToken.END_OBJECT) {
	    		
                        if (jsonParser.getCurrentName() == null && jsonParser.getText().equals("}") && jsonParser.getValueAsString() == null) {
                           // this is end of object within an array....increase the counter of this array
                           
                           int k = 1;
                           if (! arraystack.isEmpty()) {
                             k = arraycounter.get(arraystack.peek());
                             k++;
                             arraycounter.replace(arraystack.peek(), k);
                           }
                           
                        } else {
                           if (! segments.isEmpty()) {
                            segments.pop();
                           } 
                        }
	    	}
	    	if (x == JsonToken.END_ARRAY) {
	    		if (! segments.isEmpty()) {
                            segments.pop();
                        }
                        if (! arraystack.isEmpty()) {
                            arraystack.pop();
                        }
	    	}
                if (x != null && x.name().startsWith("VALUE") && previous == JsonToken.START_ARRAY ) {
                    // must be inside value array
                    
                    String lc = "";
                    String currentstack = String.join(":",segments.toArray(new String[segments.size()]));                           
                          for (String g : segments) {
                              if (arraycounter.containsKey(g)) {
                                 lc += String.valueOf(arraycounter.get(g)) + ","; 
                              } else {
                                  lc += "1" + ",";
                              }
                          }
                          lc = lc.replaceAll(",$", "");
                          lhm.put(currentstack +  "+" + lc, new String[]{jsonParser.getText()});
                    
                          int k = 1;
                           if (! arraystack.isEmpty()) {
                             k = arraycounter.get(arraystack.peek());
                             k++;
                             arraycounter.replace(arraystack.peek(), k);
                           }
                     }
                
              if (z != null) {
                 previous = z;
              } else {
                 previous = x;   
              }
	      
	    }
           
           // lhm.forEach((k,v) -> System.out.println("lhmnew: " + k + " = " + v));
	    return lhm;
	}
       
    public static List<String[]> jsonTagsToSegment(String json) throws IOException {
      ArrayList<String[]> flat = new ArrayList<String[]>();
      ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
	    JsonParser jsonParser = jsonNode.traverse();
	    Stack<String> segments = new Stack<String>();
            HashSet<String> seen = new HashSet<>();
           String currentvalue = "";
           String currenttoken = "";
           ArrayList<String[]> result = new ArrayList<String[]>();
	   
           //parse all tags into flat ArrayList...for look ahead capabilities 
           while (!jsonParser.isClosed()) {
	    	JsonToken x = jsonParser.nextToken();
                currentvalue = (jsonParser.getText() != null) ? jsonParser.getText() : "";
                currenttoken = (jsonParser.getCurrentToken() != null) ? jsonParser.getCurrentToken().name() : "";
                String[] s = new String[]{jsonParser.getCurrentName(),currenttoken,currentvalue};
                flat.add(s);
            }
            
           // now loop through flat array and assign parents and fields...filtering for unique only
           for (int k = 0; k < flat.size(); k++) {
               if (k == 0) {
                   segments.push("");
                   continue;
               }
               // push
               if (flat.get(k)[1].equals("FIELD_NAME") && flat.get(k+1)[1].equals("START_OBJECT")) {
                   segments.push(flat.get(k)[0]);
                   String v[] = new String[]{segments.get(segments.size()-1),segments.get(segments.size()-2),"1","no","yes","landmark",flat.get(k)[0],"0","100","-","M","N"};
                   if (! seen.contains(segments.get(segments.size()-1) + "," + segments.get(segments.size()-2) + "," + flat.get(k)[0])) {
                    result.add(v);
                   }
                   seen.add(segments.get(segments.size()-1) + "," + segments.get(segments.size()-2) + "," + flat.get(k)[0]);
               }
               if (flat.get(k)[1].equals("FIELD_NAME") && flat.get(k+1)[1].equals("START_ARRAY") && flat.get(k+2)[1].equals("START_OBJECT")) {
                   segments.push(flat.get(k)[0]);
                   String v[] = new String[]{segments.get(segments.size()-1),segments.get(segments.size()-2),"100","no","yes","landmark",flat.get(k)[0],"0","100","-","M","N"};
                   if (! seen.contains(segments.get(segments.size()-1) + "," + segments.get(segments.size()-2) + "," + flat.get(k)[0])) {
                    result.add(v);
                   }
                   seen.add(segments.get(segments.size()-1) + "," + segments.get(segments.size()-2) + "," + flat.get(k)[0]);
               } 
               if (flat.get(k)[1].equals("FIELD_NAME") && flat.get(k+1)[1].equals("START_ARRAY") && (flat.get(k+2)[1].equals("END_ARRAY") || flat.get(k+2)[1].startsWith("VALUE") )) {
                   segments.push(flat.get(k)[0]);
                   String v[] = new String[]{segments.get(segments.size()-1),segments.get(segments.size()-2),"15","no","no",flat.get(k)[0],flat.get(k)[0],"0","100","-","O","F"};
                   if (! seen.contains(segments.get(segments.size()-1) + "," + segments.get(segments.size()-2) + "," + flat.get(k)[0])) {
                    result.add(v);
                   }
                   seen.add(segments.get(segments.size()-1) + "," + segments.get(segments.size()-2) + "," + flat.get(k)[0]);
               }
               
               // field
               if (flat.get(k)[1].equals("FIELD_NAME") && flat.get(k+1)[1].startsWith("VALUE")) {
                   String v[] = new String[]{segments.get(segments.size()-1),segments.get(segments.size()-2),"0","no","no",flat.get(k)[0],flat.get(k)[0],"0","100","-","O","F"};
                   if (! seen.contains(segments.get(segments.size()-1) + "," + segments.get(segments.size()-2) + "," + flat.get(k)[0])) {
                    result.add(v);
                   }
                   seen.add(segments.get(segments.size()-1) + "," + segments.get(segments.size()-2) + "," + flat.get(k)[0]);
               }
               
               // pop
               if (flat.get(k)[1].equals("END_OBJECT")) {
                   if (! flat.get(k+1)[1].equals("START_OBJECT") && ! flat.get(k+1)[1].equals("END_ARRAY")) {
                       if (! segments.empty()) {                       
                         segments.pop();
                       }
                   }
               }
               if (flat.get(k)[1].equals("END_ARRAY")) {
                    if (! flat.get(k-1)[1].equals("START_ARRAY") && ! flat.get(k-1)[1].startsWith("VALUE")) { // skip value array ending 
                       if (! segments.empty()) {                       
                         segments.pop();
                       }
                    }
                }
             //  System.out.println(String.join(",",flat.get(k)) + " / " + String.join(",", segments));
           } 
            
      return result;
    }
    
    public static LinkedHashMap<String, String[]> xmlToSegments(String xml, ArrayList<String[]> isf) throws IOException {
        ArrayList<String[]> result = new ArrayList<String[]>();
        LinkedHashMap<String,ArrayList<String>> lhm = new LinkedHashMap<String,ArrayList<String>>();
        LinkedHashMap<String,String[]> lhmnew = new LinkedHashMap<String,String[]>();
        LinkedHashMap<String,Integer> arraycounter = new LinkedHashMap<String,Integer>();
        String lhmkey = "";
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document document = null;
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            document = docBuilder.parse(is);
        } catch (ParserConfigurationException ex) {
            edilog(ex);
        } catch (SAXException ex) {
            edilog(ex);
        }
        
        int counter = 0;
        String root = "";
        String parent = "";
        LinkedHashMap<String, String> plhm = new LinkedHashMap<String, String>();
        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getParentNode().getNodeName().equals("#document")) {
                root = node.getNodeName();
            }
            
            String ppath = "";
            Node pnode = node.getParentNode();
            while (pnode != null) {
            ppath = pnode.getNodeName() + ':' + ppath;
            pnode = pnode.getParentNode();
            }
            
            Node nextnode = null;
            counter = 0;
            if (node.getNodeType() == Node.ELEMENT_NODE) {            	
            	NodeList childnodes = node.getChildNodes();
            	for (int j = 0; j < childnodes.getLength(); j++) {
            		Node child = childnodes.item(j);
                        if (ppath.length() > 10) {
                         parent = ppath.substring(10,ppath.length() - 1);  
                        } else {
                         parent = ppath.substring(10); 
                        }
                        lhmkey = node.getNodeName() + "," + parent + "," + node.hashCode();
                      //  System.out.println("here: init" + lhmkey);
                        if (! lhm.containsKey(lhmkey)) {
            				lhm.put(lhmkey, null);
                        }
                        // get attributes of parent node on first iteration of child nodes
                        if (j == 0 && node.hasAttributes()) {
                            ArrayList<String> xx = lhm.get(lhmkey);
                                if (xx != null) {
                                    for (int a = 0; a < node.getAttributes().getLength(); a++){
                                      Attr attr = (Attr) node.getAttributes().item(a);
                                      xx.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                    }
                                    lhm.put(lhmkey, xx);
                                } else {
                                    ArrayList<String> al = new ArrayList<String>();
                                    for (int a = 0; a < node.getAttributes().getLength(); a++){
                                      Attr attr = (Attr) node.getAttributes().item(a);
                                      al.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                    }
                                    lhm.put(lhmkey, al);
                                }
                            
                        }
                        
                        // now process child tag if leaf (no children)
                        if (child.getNodeType() == Node.ELEMENT_NODE && child.getChildNodes().getLength() == 1) {
            			
            			ArrayList<String> temp = lhm.get(lhmkey);
                                if (temp != null) {
                                      temp.add(child.getNodeName() + "=" + child.getTextContent()); // add child node value
                                      // get attributes of child node
                                      if (child.hasAttributes()) {
                                            for (int a = 0; a < child.getAttributes().getLength(); a++){
                                               Attr attr = (Attr) child.getAttributes().item(a);
                                               temp.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                            }
                                      }
                                      lhm.put(lhmkey, temp);
                                } else {
                                      ArrayList<String> al = new ArrayList<String>();
                                      al.add(child.getNodeName() + "=" + child.getTextContent());
                                      // get attributes of child node
                                      if (child.hasAttributes()) {
                                            for (int a = 0; a < child.getAttributes().getLength(); a++){
                                               Attr attr = (Attr) child.getAttributes().item(a);
                                               al.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                            }
                                      }
                                      lhm.put(lhmkey, al);
                                }	
            	     }
            	}
            	
            }
        }
        
	    for (Map.Entry<String, ArrayList<String>> val : lhm.entrySet()) {
                
                if (val.getValue() != null) {
                    
                    String key = val.getKey().split(",")[1] + ":" + val.getKey().split(",")[0];
                    if (! arraycounter.containsKey(key)) {
                        arraycounter.put(key, 1);
                    } else {
                        arraycounter.replace(key, arraycounter.get(key) + 1);
                    }
                    
                    for (String sv : val.getValue()) {
                        String k = val.getKey().split(",")[1] + ":" + val.getKey().split(",")[0] + ":" + sv.split("=")[0] + "+1,1," + String.valueOf(arraycounter.get(key));
                        String[] v = new String[]{sv.split("=")[1]};
                        lhmnew.put(k, v);
                      // System.out.println("HEREprint: " + k + " / " + v[0]);
                 
                    }
                }
                /*
                String[] t = getFieldsFromStructure(val.getKey().split(",")[0], val.getKey().split(",")[1], isf);
                String[] td = new String[t.length + 1];
                td[0] = val.getKey().split(",")[0];
                for (int k = 0; k < t.length; k++) {
                  //  System.out.println("HERE size: " + t.length + "/" + val.getValue().size());
                    if (val.getValue() != null) {
                        for (int m = 0; m < val.getValue().size(); m++) {
                            if (t[k].equals(val.getValue().get(m).split("=")[0])) {
                               // System.out.println("HERE Loop: " + t[0] + "/" +  val.getKey() + "/" + k + "/" +  t[k] + "/" + val.getValue().get(m));

                                td[k + 1] = val.getValue().get(m).split("=")[1];
                                break;
                            }
                        }
                    }
                }
                result.add(td);
                */
            //  System.out.println("HEREprint: " + String.join(",", td) + "/" + val.getKey());
	    }
        
		//return result;
                return lhmnew;
	}
   
    public static LinkedHashMap<String, String[]> xmlToSegments_bkup(String xml, ArrayList<String[]> isf) throws IOException {
        ArrayList<String[]> result = new ArrayList<String[]>();
        LinkedHashMap<String,ArrayList<String>> lhm = new LinkedHashMap<String,ArrayList<String>>();
        LinkedHashMap<String,String[]> lhmnew = new LinkedHashMap<String,String[]>();
        String lhmkey = "";
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document document = null;
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            document = docBuilder.parse(is);
        } catch (ParserConfigurationException ex) {
            edilog(ex);
        } catch (SAXException ex) {
            edilog(ex);
        }
        
        int counter = 0;
        String root = "";
        String parent = "";
        LinkedHashMap<String, String> plhm = new LinkedHashMap<String, String>();
        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getParentNode().getNodeName().equals("#document")) {
                root = node.getNodeName();
            }
            
            String ppath = "";
            Node pnode = node.getParentNode();
            while (pnode != null) {
            ppath = pnode.getNodeName() + ':' + ppath;
            pnode = pnode.getParentNode();
            }
            
            Node nextnode = null;
            counter = 0;
            if (node.getNodeType() == Node.ELEMENT_NODE) {            	
            	NodeList childnodes = node.getChildNodes();
            	for (int j = 0; j < childnodes.getLength(); j++) {
            		Node child = childnodes.item(j);
                        if (ppath.length() > 10) {
                         parent = ppath.substring(10,ppath.length() - 1);  
                        } else {
                         parent = ppath.substring(10); 
                        }
                        lhmkey = node.getNodeName() + "," + parent + "," + node.hashCode();
                      //  System.out.println("here: init" + lhmkey);
                        if (! lhm.containsKey(lhmkey)) {
            				lhm.put(lhmkey, null);
                        }
                        // get attributes of parent node on first iteration of child nodes
                        if (j == 0 && node.hasAttributes()) {
                            ArrayList<String> xx = lhm.get(lhmkey);
                                if (xx != null) {
                                    for (int a = 0; a < node.getAttributes().getLength(); a++){
                                      Attr attr = (Attr) node.getAttributes().item(a);
                                      xx.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                    }
                                    lhm.put(lhmkey, xx);
                                } else {
                                    ArrayList<String> al = new ArrayList<String>();
                                    for (int a = 0; a < node.getAttributes().getLength(); a++){
                                      Attr attr = (Attr) node.getAttributes().item(a);
                                      al.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                    }
                                    lhm.put(lhmkey, al);
                                }
                            
                        }
                        
                        // now process child tag if leaf (no children)
                        if (child.getNodeType() == Node.ELEMENT_NODE && child.getChildNodes().getLength() == 1) {
            			
            			ArrayList<String> temp = lhm.get(lhmkey);
                                if (temp != null) {
                                      temp.add(child.getNodeName() + "=" + child.getTextContent()); // add child node value
                                      // get attributes of child node
                                      if (child.hasAttributes()) {
                                            for (int a = 0; a < child.getAttributes().getLength(); a++){
                                               Attr attr = (Attr) child.getAttributes().item(a);
                                               temp.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                            }
                                      }
                                      lhm.put(lhmkey, temp);
                                } else {
                                      ArrayList<String> al = new ArrayList<String>();
                                      al.add(child.getNodeName() + "=" + child.getTextContent());
                                      // get attributes of child node
                                      if (child.hasAttributes()) {
                                            for (int a = 0; a < child.getAttributes().getLength(); a++){
                                               Attr attr = (Attr) child.getAttributes().item(a);
                                               al.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                            }
                                      }
                                      lhm.put(lhmkey, al);
                                }	
            	     }
            	}
            	
            }
        }
        
	    for (Map.Entry<String, ArrayList<String>> val : lhm.entrySet()) {
                String[] t = getFieldsFromStructure(val.getKey().split(",")[0], val.getKey().split(",")[1], isf);
                String[] td = new String[t.length + 1];
                td[0] = val.getKey().split(",")[0];
                for (int k = 0; k < t.length; k++) {
                  //  System.out.println("HERE size: " + t.length + "/" + val.getValue().size());
                    if (val.getValue() != null) {
                        for (int m = 0; m < val.getValue().size(); m++) {
                            if (t[k].equals(val.getValue().get(m).split("=")[0])) {
                               // System.out.println("HERE Loop: " + t[0] + "/" +  val.getKey() + "/" + k + "/" +  t[k] + "/" + val.getValue().get(m));

                                td[k + 1] = val.getValue().get(m).split("=")[1];
                                break;
                            }
                        }
                    }
                }
               // mappedData.put(parenthead + x[0] + "+" + groupcount.get(groupparent) + "+" + loop , x);
                result.add(td);
              System.out.println("HEREprint: " + String.join(",", td) + "/" + val.getKey());
	    }
        
		return lhmnew;
	}
       
    public static List<String[]> xmlTagsToSegments(String xml) throws IOException {
        ArrayList<String[]> result = new ArrayList<String[]>();
        ArrayList<String[]> newresult = new ArrayList<String[]>();
        Stack<String> uniques = new Stack<String>();
        LinkedHashMap<String,ArrayList<String>> lhm = new LinkedHashMap<String,ArrayList<String>>();
        String lhmkey = "";
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document document = null;
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            document = docBuilder.parse(is);
        } catch (ParserConfigurationException ex) {
            edilog(ex);
        } catch (SAXException ex) {
            edilog(ex);
        }
        
        int counter = 0;
        String root = "";
        String parent = "";
        LinkedHashMap<String, String> plhm = new LinkedHashMap<String, String>();
        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getParentNode().getNodeName().equals("#document")) {
                root = node.getNodeName();
            }
            
            String ppath = "";
            Node pnode = node.getParentNode();
            while (pnode != null) {
            ppath = pnode.getNodeName() + ':' + ppath;
            pnode = pnode.getParentNode();
            }
            
            Node nextnode = null;
            counter = 0;
                
                
             //   System.out.println("PARENT: " + node.getNodeName() + "/" + node.getNodeType() + "  " + node.hasChildNodes() + "/" + node.hasAttributes() + "/" + node.getFirstChild() + "/" + node.getLastChild());
                         
            	NodeList childnodes = node.getChildNodes();
                int ck = 0;
                boolean childempty = false;
            	for (int j = 0; j < childnodes.getLength(); j++) {
                        childempty = false;
            		Node child = childnodes.item(j);
                     //   System.out.println("CHILD: " + child.getNodeName() + "/" + child.getNodeType() + "  " + child.hasChildNodes() + "/" + child.hasAttributes() + "/" + child.getFirstChild() + "/" + child.getLastChild());
                 
                        if (child.getFirstChild() != null && child.getNodeType() == 1 && child.getFirstChild().getNodeValue().isBlank() ) {
                      //    System.out.println("CHILD EMPTY: " + child.getNodeName());  
                          childempty = true;
                        }
                        
                     if (ppath.length() > 10) {
                         parent = ppath.substring(10,ppath.length() - 1);  
                      } else {
                         parent = ppath.substring(10); 
                      }
                      lhmkey = node.getNodeName() + "," + parent + "," + node.hashCode();
                        
                        if (! lhm.containsKey(lhmkey)) {
            				lhm.put(lhmkey, null);
                                        
                        }
                     
                        if (child.getNodeType() == Node.ELEMENT_NODE ) {
            
                            if (ck == 0 && ! uniques.contains(node.getNodeName()+parent+"landmark")) {
                             String b[] = new String[]{node.getNodeName(),parent,"100","no","yes","landmark","landmark","0","100","-","M","N"};
                             newresult.add(b); 
                             uniques.add(node.getNodeName()+parent+"landmark");
                            } 
                            
                            
                            
                                    // get attributes of parent node on first iteration of child nodes
                               if (ck == 0 && node.hasAttributes()) {
                                 
                                   ArrayList<String> xx = lhm.get(lhmkey);
                                       if (xx != null) {
                                           for (int a = 0; a < node.getAttributes().getLength(); a++){
                                             Attr attr = (Attr) node.getAttributes().item(a);
                                             xx.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                           }
                                           lhm.put(lhmkey, xx);

                                       } else {
                                           ArrayList<String> al = new ArrayList<String>();
                                           for (int a = 0; a < node.getAttributes().getLength(); a++){
                                             Attr attr = (Attr) node.getAttributes().item(a);
                                             al.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                             if (! uniques.contains(parent+node.getNodeName()+attr.getNodeName())) {
                                             String k[] = new String[]{node.getNodeName(),parent,"0","no","no",attr.getNodeName(),attr.getNodeName(),"0","100","-","O","A"};
                                             newresult.add(k);
                                             uniques.add(parent+node.getNodeName()+attr.getNodeName());
                                             }
                                           }
                                           lhm.put(lhmkey, al);
                                       }

                               }
                            
                            ck++;
                            
                            if (! uniques.contains(node.getNodeName()+parent+child.getNodeName()) && ! childempty ) {
                            String b[] = new String[]{node.getNodeName(),parent,"0","no","no",child.getNodeName(),child.getNodeName(),"0","100","-","O","F"};
                            newresult.add(b);
                            uniques.add(node.getNodeName()+parent+child.getNodeName());
                            }
                            
            			ArrayList<String> temp = lhm.get(lhmkey);
                                if (temp != null) {
                                      temp.add(child.getNodeName() + "=" + child.getTextContent()); // add child node value
                                      // get attributes of child node
                                      if (child.hasAttributes()) {
                                            for (int a = 0; a < child.getAttributes().getLength(); a++){
                                               Attr attr = (Attr) child.getAttributes().item(a);
                                               temp.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                            }
                                      }
                                      lhm.put(lhmkey, temp);
                                } else {
                                      ArrayList<String> al = new ArrayList<String>();
                                      al.add(child.getNodeName() + "=" + child.getTextContent());
                                      // get attributes of child node
                                      if (child.hasAttributes()) {
                                            for (int a = 0; a < child.getAttributes().getLength(); a++){
                                               Attr attr = (Attr) child.getAttributes().item(a);
                                               al.add(attr.getNodeName() + "=" + attr.getNodeValue());
                                               String k[] = new String[]{child.getNodeName(),node.getNodeName(),"0","no","no",attr.getNodeName(),attr.getNodeName(),"0","100","-","O","A"};
                                               newresult.add(k);
                                              // System.out.println("CHILD ATTR: " + child.getNodeName() + "/" + attr.getNodeName());
                 
                                            }
                                      }
                                      lhm.put(lhmkey, al);
                                }	
            	     } // if element node
            	} // child node loop
            	                
           
            
        }  // parent node loop
        /*
	    for (Map.Entry<String, ArrayList<String>> val : lhm.entrySet()) {
                String[] t = getFieldsFromStructure(val.getKey().split(",")[0], val.getKey().split(",")[1], isf);
                String[] td = new String[t.length + 1];
                td[0] = val.getKey().split(",")[0];
                for (int k = 0; k < t.length; k++) {
                  //  System.out.println("HERE size: " + t.length + "/" + val.getValue().size());
                    for (int m = 0; m < val.getValue().size(); m++) {
                      //  System.out.println("HERE Loop: " + t[0] + "/" +  val.getKey() + "/" + k + "/" +  t[k] + "/" + val.getValue().get(m));
                        if (t[k].equals(val.getValue().get(m).split("=")[0])) {
                            td[k + 1] = val.getValue().get(m).split("=")[1];
                            break;
                        }
                        
                    }
                }
                
                
	    	for (int k = 1; k < val.getValue().size() + 1; k++) {
	    		j[k] = val.getValue().get(k - 1);
	    	}
	    	result.add(j);
                System.out.println("HERE: " + String.join(",", j) + "/" + val.getKey());
                
                result.add(td);
              //  System.out.println("HEREprint: " + String.join(",", td) + "/" + val.getKey());
	    }
        */
      //  newresult.forEach((k) -> System.out.println("HERExml: " + String.join(",",k)));
        
		return newresult;
	}
    
    public static List<String[]> csvToSegment(String csv) throws IOException {
	   
           String[] lines = csv.split("\n");
           ArrayList<String[]> result = new ArrayList<String[]>();
           
           if (lines != null && lines.length > 0) {
               String[] fields = lines[0].split(",");
               for (String s : fields) {
               String[] k = new String[]{"ROW","","0","no","no",s,s,"0","100","-","M","F"};
	       result.add(k);
               }
               String[] k = new String[]{"ROW","","100","no","yes","landmark","landmark","8","8","-","M","N"};
               result.add(0, k);
           } else {
               return null;
           }
	  
	    return result;
	}
    
    public static String xmlgetPathToRoot(String tagc, String tagp, String root, LinkedHashMap<String, String> lhm) {
		String r = tagp;
		if (lhm.containsKey(tagc)) {
			
		} else {
			lhm.put(tagc, tagp);
		}
		
		if (tagp.equals(root)) {
			return r;
		} else {
			r = seekParent(tagp, root, lhm, "");
		}
		
		return r;
	}
	
    public static String seekParent(String p, String root, LinkedHashMap<String, String> lhm, String r) {
		if (lhm.containsKey(p)) {
			String x = lhm.get(p);
			if (! x.equals(root)) {
				r = seekParent(x, root, lhm, r) + ":" + p;
			} else {
				r = x + ":" + p;
			}
		} 
		return r;
	}
    
    public static void debuginput(Map<String, String[]> mappedData) {
        if (GlobalDebug) {
            for (Map.Entry<String, String[]> z : mappedData.entrySet()) {
                    String value = String.join(",", z.getValue());
                    System.out.println("mappedData: " + "mappedDatakey: " + z.getKey()  + " / value: " + String.join(",",z.getValue()));
            }
        }
    }
    
    public static int CountOMD(String tag) {
        Map<String, HashMap<String,String>> MD = new LinkedHashMap<String, HashMap<String,String>>(OMD);
        int i = 0;
        for (Map.Entry<String, HashMap<String,String>> z : MD.entrySet()) {
                if (z.getKey().substring(0, z.getKey().lastIndexOf(":")).equals(tag)) {
                    i++;
                }
        }
        return i;
    }
    
    public static int CountOMDTrial(String tag) {
        int i = 0;
        for (Map.Entry<String, Integer> z : commitLoopCounter.entrySet()) {
                if (z.getKey().equals(tag)) {
                    i = z.getValue();
                }
        }
        return i;
    }
    
    public static boolean isLooper(String tag) {
        return (commitLoopCounter.containsKey(tag));
    }
    
    
    public static int CountLMLoopsOFS(String landmark) {
        
        int i = 0;
        if (OSF.get(landmark) != null && OSF.get(landmark).size() > 0) {
            String[] LMFields = OSF.get(landmark).get(0);
            if (LMFields != null && LMFields.length > 5) {
                i = Integer.valueOf(LMFields[2]);
            }
        }
        return i;
    }
    
    public static int commitCount(String segment) {
        int r = 1;
        if (commitCounter.containsKey(segment)) {
            r = commitCounter.get(segment) + 1;
            commitCounter.put(segment, r);
        } else {
            commitCounter.put(segment, 1);
        }
        return r;
    }
    
     public static int commitLoopCount(String segment) {
        
        int r = 0;
        if (commitLoopCounter.containsKey(segment)) {
            r = commitLoopCounter.get(segment) + 1;
            commitLoopCounter.put(segment, r);
        } else {
            commitLoopCounter.put(segment, 1);
        }
        return r;
    }
    
    
    public static String getRootOFS() {
        String root = "";
        for (Map.Entry<String, ArrayList<String[]>> z : OSF.entrySet()) {
            ArrayList<String[]> x = z.getValue();
            root = x.get(0)[0];
            break;
        }
       
        return root;
    }
    
    public static String[] writeOMD(String[] c, String[] tp) {
         String[] r = new String[2];
    	 String segment = "";
         String s = "";
         String e = "";
         String u = "";
    	 content = "";
        
    	 Map<String, HashMap<String,String>> MD = new LinkedHashMap<String, HashMap<String,String>>(OMD);
    	 
    	 if (outputfiletype.toUpperCase().equals("X12")) {
         segcount = 0;  // init segment count for this doc
         if (tp == null || tp[7].equals("0") || tp[7].isBlank()) {
            s = delimConvertIntToStr("10"); // segment delimiter 
         } else {
            s = delimConvertIntToStr(tp[7]); // segment delimiter 
         }
         if (tp == null || tp[6].equals("0") || tp[6].isBlank()) {
            e = delimConvertIntToStr("42"); // element delimiter 
         } else {
            e = delimConvertIntToStr(tp[6]); // element delimiter 
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
                        //    System.out.println(f[0] + "/" + f[6]);
                                if (f[4].toLowerCase().equals("yes") || f[5].equals("groupend")) {
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
         
          if (outputfiletype.toUpperCase().equals("UNE")) {
         segcount = 0;  // init segment count for this doc
         if (tp == null || tp[7].equals("0") || tp[7].isBlank()) {
            s = delimConvertIntToStr("39"); // segment delimiter 
         } else {
            s = delimConvertIntToStr(tp[7]); // segment delimiter 
         }
         if (tp == null || tp[6].equals("0") || tp[6].isBlank()) {
            e = delimConvertIntToStr("43"); // element delimiter 
         } else {
            e = delimConvertIntToStr(tp[6]); // element delimiter 
         }
         if (tp == null || tp[8].equals("0") || tp[8].isBlank()) {
            u = delimConvertIntToStr("58"); // element delimiter 
         } else {
            u = delimConvertIntToStr(tp[8]); // element delimiter 
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
                        //    System.out.println(f[0] + "/" + f[6]);
                                if (f[4].toLowerCase().equals("yes") || f[5].equals("groupend")) {
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
         } // if UNE edifact
         
         if (outputfiletype.toUpperCase().equals("FF")) {
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
                        int fc = 0;    
                        for (String[] f : fields) {
                                fc++;
                                if (fc == 1) {
                                    continue;  //skip first field (landmark) since assigned above
                                }
                                if (f[5].equals("groupend")) {
                                        continue;
                                }
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
         
          if (outputfiletype.toUpperCase().equals("CSV")) {
    	 for (Map.Entry<String, HashMap<String,String>> z : MD.entrySet()) {
 		//	ArrayList<String[]> fields = z.getValue();
 			
                // write out all fields of this segment
                HashMap<String,String> mapValues = MD.get(z.getKey());
        //	System.out.println("loopentrycount:" + mapValuesLoops.keySet());
                // loop through integers
 
                if (GlobalDebug) {
                    System.out.println("OMD: " + z.getKey() + " : " + mapValues);
                }               
                          String landmark = z.getKey().split(":")[0];  // start with landmark
                      //  System.out.println(">:" + segment);
                        ArrayList<String[]> fields = OSF.get(landmark);
                        if (fields != null) { 
                        int fc = 0;    
                        for (String[] f : fields) {
                                fc++;
                                if (fc == 1) {
                                    continue;  //skip first field (landmark) since assigned above
                                }
                                if (f[5].equals("groupend")) {
                                        continue;
                                }
                                

                                // overlay with values that were actually assigned...otherwise blanks
                                if (mapValues.containsKey(f[5])) {
                                        if (mapValues.get(f[5]).length() > Integer.valueOf(f[8])) {
                                                segment += mapValues.get(f[5]).substring(0, Integer.valueOf(f[8])).trim() + ","; // properly formatted
                                        } else {
                                                segment += mapValues.get(f[5]).trim() + ","; // properly formatted
                                        }

                                } else {
                                        segment += "" + ","; // properly formatted
                                }
                                
                        }
                        } // if fields not null
                        
                         if (GlobalDebug) {
                            System.out.println("OMD segment: " + segment);
                        }
                        
                        // trim off last ,
                        if (segment.endsWith(",")) {
                            segment = segment.substring(0, segment.length() - 1);
                        }
                         
                        content += segment + "\n";
                        segment = ""; // reset the segment string
 		}
         } // if CSV
       
        if (outputfiletype.toUpperCase().equals("XML")) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = null;
            ArrayList<String> exclude = new ArrayList<String>();
             try {
                 docBuilder = docFactory.newDocumentBuilder();
             } catch (ParserConfigurationException ex) {
                 edilog(ex);
             }
        Document doc = docBuilder.newDocument();
        
        Element rootElement = doc.createElement(getRootOFS());
       // System.out.println("HERE root is: " + rootElement.getNodeName());
        if (GlobalDebug) {
            System.out.println("OMD output:");
            for (Map.Entry<String, HashMap<String,String>> z : MD.entrySet()) {
                    HashMap<String,String> mapValues = MD.get(z.getKey());
                    for (Map.Entry<String,String> k : mapValues.entrySet()) {
                    System.out.println(z.getKey() + " / " + k.getKey() + " / " + k.getValue());
                    }
            }
            System.out.println("OSF output:");
            for (Map.Entry<String, ArrayList<String[]>> osf : OSF.entrySet()) {
                ArrayList<String[]> osfarray = osf.getValue();
                for (String[] osfx : osfarray) {
                    System.out.println("osf key: " + osf.getKey() + "   osf value: " + String.join(",", osfx)); 
                }
            }
        }
        // check if empty tag suppression
        
        boolean suppress = isSuppressEmptyTag(ofsfile);
        
        // set Attributes if exist of root Element
        overlayData(rootElement, "", doc, OSF, 1, MD, suppress);
        
                
        createXML(rootElement, "", 0, doc, exclude, OSF, MD, 0, suppress);
        
        doc.appendChild(rootElement);
             
         

        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans;
             try {
                trans = tf.newTransformer();
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                StringWriter sw = new StringWriter();
                trans.transform(new DOMSource(doc), new StreamResult(sw)); 
                String precontent = sw.toString();
                content = precontent.replaceAll(".bLuEsEeR", "");  // crappy way to preserver order in attributes
                
                // System.out.println(content);
             
             } catch (TransformerConfigurationException ex) {
                 edilog(ex);
             } catch (TransformerException ex) {
                 edilog(ex);
             }
        
           
        } // end if XML
        
        if (outputfiletype.toUpperCase().equals("JSON")) {
            
             if (GlobalDebug) {
            System.out.println("OMD output:");
            for (Map.Entry<String, HashMap<String,String>> z : MD.entrySet()) {
                    HashMap<String,String> mapValues = MD.get(z.getKey());
                    for (Map.Entry<String,String> k : mapValues.entrySet()) {
                    System.out.println(z.getKey() + " / " + k.getKey() + " / " + k.getValue());
                    }
            }
            /*
            System.out.println("OSF output:");
            for (Map.Entry<String, ArrayList<String[]>> osf : OSF.entrySet()) {
                ArrayList<String[]> osfarray = osf.getValue();
                for (String[] osfx : osfarray) {
                    System.out.println("osf key: " + osf.getKey() + "   osf value: " + String.join(",", osfx)); 
                }
            }
            */
        }
            
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            String rootname = getRootOFS();
            // root.putObject(rootname);
           // ObjectNode x = buildJSON(mapper, OSF, MD); bsNode<String> node, ObjectNode obN
            
            bsTree<String> tree = new bsTree<String>();
            bsNode<String> rootNode = new bsNode<String>(rootname);
	    bsNode<String> phantomroot = new bsNode<String>("");
            phantomroot.addChild(treeFromFile(rootNode, rootname));
            tree.setRootElement(rootNode);
          //  root.set(rootname, generateJSONx(tree.getRootElement(), mapper.createArrayNode(), mapper.createObjectNode(), OSF, MD).on());
           
            root.set(rootname, generateJSONx(tree.getRootElement(), rootname, null, 0, OSF, MD).on()); 
            
            if (GlobalDebug) {
            System.out.println("JSON TREE: " + tree.toString());
            }
            try {
                 content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
                 if (GlobalDebug) {
                   System.out.println("prettyprint: " + "\n" + content);  
                 }
             } catch (JsonProcessingException ex) {
                 edilog(ex);
             }
        } // if JSON
        
        // bsmf.MainFrame.show("here..." + outputfiletype + ": " + content);
    	clearStaticVariables();
    	 
    	 return r;
     }
    
    public static void createXML(Element ele, String parentx, int level, Document doc, ArrayList<String> exclude, LinkedHashMap<String, ArrayList<String[]>> osf, Map<String, HashMap<String,String>> MD, int j, boolean suppress) {
	  //  System.out.println(level + " " + "Name: "+ele.getNodeName() + "     Value: "+ele.getNodeValue());
	    
            ArrayList<String> list = getChildren(ele.getNodeName(), parentx ,osf);
	    exclude.add(ele.getNodeName());
            String tag = "";
            String ptag = "";
            String mykey = "";
	    for (int i = 0; i < list.size(); i++) {
		Element childNode = null;
		      
		String[] sp = list.get(i).split("=");
                tag = sp[0];
                ptag = sp[1];
                
                if (ptag.isBlank()) {
                    mykey = tag;
                } else {
                    mykey = ptag + ":" + tag;
                }
		int actual = CountOMDTrial(mykey);  // get actual loopcount  
		int maxallowed = CountLMLoopsOFS(mykey); 
                int limit = 0;
              //  System.out.println("HERE 0x:  " + mykey+ "/" + actual + "/" + maxallowed + "/" + limit + "/" + j );
                if (actual >= maxallowed) {
                    limit = maxallowed;
                } else {
                    limit = actual;
                }
                if (limit <= 0) {
                    limit = 1;
                }
		
                if (j < 1) {
                    j = 1;
                }
                
                for (int k = 1; k <= limit; k++) {
                  if (isLooper(mykey)) {
                   //   System.out.println("HERE: " + mykey + " is a looper"); 
                      j = k;
                  }
                  childNode = doc.createElement(tag);
               //   System.out.println("HERE 1x:  " + mykey + "/" + actual + "/" + maxallowed + "/" + limit + "/" + j );
                  overlayData(childNode, ptag, doc, osf, j, MD, suppress);
                  ele.appendChild(childNode);
                  
              //  if (childNode != null && ! exclude.contains(childNode.getNodeName())) {
                if (childNode != null) {
               // System.out.println("HERE: " + tag + "/" + limit + "/" + k + "/" + j + "/" + mykey + "/" + level);
            //   System.out.println("HERE: Calling createXML with " + childNode.getTagName() + " and k value = " + k + " and j value = " + j);  
               createXML(childNode, ptag, level + 1, doc, exclude, osf, MD, j, suppress);
                }
                
                } // for limit
	    }
	    
    }	
    
    public static ArrayList<String> getChildren(String x, String parentx, LinkedHashMap<String, ArrayList<String[]>> osf) {
		ArrayList<String> list = new ArrayList<String>();
		String parent = "";
                String thiskey = "";
                String tag = "";
                String param = "";
                if (parentx.isBlank()) {
                    param = x;
                } else {
                    param = parentx + ":" + x;
                }
                
		for (Map.Entry<String, ArrayList<String[]>> s : osf.entrySet()) {
                    thiskey = s.getKey();
			String[] recArray = s.getValue().get(0);
			if (! recArray[4].toLowerCase().equals("yes")) {
				continue;
			}
                        
                        if (thiskey.contains(":")) {
                            tag = thiskey.substring(thiskey.lastIndexOf(":") + 1);
                        } else {
                            tag = thiskey;
                        }
                        
                        /*
                        System.out.println("PREHERE: " + s.getKey() + "/" + recArray[1] + "/" +  x + "/" + parentx);
                        if (! recArray[1].equals(parentx) && ! recArray[1].isBlank()) {
				continue;
			}
                        */
                   //     System.out.println("HEREgetC: " + s.getKey() + "/" + x + "/" + parentx);
			if (recArray[1].contains(":")) {
                            parent = recArray[1].substring(recArray[1].lastIndexOf(":") + 1);
                        } else {
                            parent = recArray[1];
                        }
                        /*
			if (parent.equals(x) && ! recArray[0].equals(x)) {
				list.add(tag + "=" + recArray[1]);
			}
                        */
                        
                        if (recArray[1].equals(param)) {
				list.add(tag + "=" + recArray[1]);
			}
		}
            
		return list;
	}
    
    public static void overlayData(Element ele, String parentx, Document doc, LinkedHashMap<String, ArrayList<String[]>> osf, int k, Map<String, HashMap<String,String>> MD, boolean suppressEmptyTag) {
        String v = "";
        String parent = "";
        String parentChildKey = "";
        int prefixI = 65;
        String prefixtag = "bLuEsEeR";
        String prefix = "";
        String thiskey = "";
        String tag = "";
        OSFLoop:
        for (Map.Entry<String, ArrayList<String[]>> s : osf.entrySet()) {
            thiskey = s.getKey();
            
            if (thiskey.contains(":")) {
                tag = thiskey.substring(thiskey.lastIndexOf(":") + 1);
            } else {
                tag = thiskey;
            }
                       
          //  System.out.println("HERE 1: " + tag + "/" + thiskey + "/" + ele.getNodeName() + "/" + parentx);
            if (tag.equals(ele.getNodeName())) {
                for (String[] x : s.getValue()) {
                        
                        if (! x[1].equals(parentx)) {
                            continue OSFLoop;
                        }
                        if (x[4].toLowerCase().equals("yes")) {
                                parent = x[1];
                                continue;
                        }
                        
                        
                        if (! parent.isEmpty()) {
                            parentChildKey = parent + ":";
                        } else {
                            parentChildKey = "";
                        }
                        Element e = doc.createElement(x[5]);
                          HashMap<String,String> mapValues = MD.get(parentChildKey + tag + ":" + k);
                          if (mapValues != null && mapValues.containsKey(x[5])) {
                            v = mapValues.get(x[5]);
                          } else {
                              v = "";
                          }
                    //    System.out.println("HERE OUTPUT: " + tag + "/" + thiskey + "/" + ele.getNodeName() + "/" +  parentChildKey + "/" + k + "/" + x[5] + "/" + v);
                        if (x.length > 10 && x[11].toUpperCase().equals("A")) {
                            prefix = Character.toString(prefixI++) + prefixtag; // crappy way to preserver order in attributes...prefix removed with regex
                          ele.setAttribute(prefix + x[5].toString(),v);	// set attribute of parent node  
                        } else if (x.length > 10 && x[11].toUpperCase().equals("X")) { // set attribute of previous TextNode
                           prefix = Character.toString(prefixI++) + prefixtag; // crappy way to preserver order in attributes...prefix removed with regex
                           Node nd = ele.getLastChild();
                           if (nd != null) {
                           ((Element) nd).setAttribute(prefix + x[5], v);
                         //  System.out.println("HERE: lastchild" + nd.getNodeName());
                           }                           
                        } else {
                         // System.out.println(e.getTagName() + " v = " + v); 
                          e.appendChild(doc.createTextNode(v));	// create data TextNode  
                          if (v.isBlank()) {
                            if (! suppressEmptyTag) {  
                            ele.appendChild(e);
                            }
                          } else {
                            ele.appendChild(e);  
                          } 
                        }
                        	
                        
                }
                break;
            }
        }
    }
    
    public static jsonRecord generateJSON(bsNode<String> node, String rootnodename, int level, LinkedHashMap<String, ArrayList<String[]>> osf, Map<String, HashMap<String,String>> MD) {
    
    ObjectNode obN = new ObjectMapper().createObjectNode();  
    ArrayNode arN = new ObjectMapper().createArrayNode();
    jsonRecord jr = null;
    if (node == null) {
        return jr;
    }

    
    
    String thiskey = "";
    String tag = "";
    
    // collect fields here
    
    for (Map.Entry<String, ArrayList<String[]>> s : osf.entrySet()) {
    thiskey = s.getKey();    
    if (thiskey.contains(":")) {
        tag = thiskey.substring(thiskey.lastIndexOf(":") + 1);
    } else {
        tag = thiskey;
    }    
    System.out.println("here tag: " + s.getKey() + "/" + tag + "/" + node.getData());    
    if (! tag.equals(node.getData())) {
        continue;
    }
    int actual = CountOMD(s.getKey());  // get actual loopcount  
    int maxallowed = CountLMLoopsOFS(s.getKey()); 
    
    int limit = 0;
  //  System.out.println("here preloopcount: " + s.getKey() + "/" + actual + "/" + maxallowed + "/" + limit); 
    if (actual >= maxallowed) {
        limit = maxallowed;
    } else {
        limit = actual;
    }
   if (limit <= 0) {
        limit = 1;
    }
  // System.out.println("here postloopcount: " + s.getKey() + "/" + actual + "/" + maxallowed); 
    String v = "";
    String parent = "";
    String parentChildKey = "";
    
    ArrayList<String[]> fields = osf.get(s.getKey());
    
    
  //  System.out.println("entering fields: " + s.getKey() + "/" + limit );
    if (fields != null) {
        if (limit > 1) {
            for (int k = 1; k <= limit; k++) {
                ObjectNode elementNode = new ObjectMapper().createObjectNode(); 
            for (int i = 0; i < fields.size(); i++) {
                                        String[] x = fields.get(i);
                                        if (x[4].toLowerCase().equals("yes")) {
                                            parent = x[1];
                                                continue;                                        
                                        }
                                        if (! parent.isEmpty()) {
                                            parentChildKey = parent + ":";
                                        } else {
                                            parentChildKey = "";
                                        }
                                        HashMap<String,String> mapValues = MD.get(parentChildKey + tag + ":" + k);
                                        if (mapValues != null && mapValues.containsKey(x[5])) {
                                          v = mapValues.get(x[5]);
                                        } else {
                                            v = "";
                                        }
                                        elementNode.put(x[5], v);
                                }
                arN.add(elementNode);
           //     System.out.println("here limit > 1: " + node.getData() + "/" +  elementNode.toString());
            } 
        } else {
            ObjectNode elementNode = new ObjectMapper().createObjectNode();
            for (int i = 0; i < fields.size(); i++) {
                        
                                    String[] x = fields.get(i);
                                    if (x[4].toLowerCase().equals("yes")) {
                                            parent = x[1];
                                            continue;
                                    }
                                    
                                    
                                    if (! parent.isEmpty()) {
                                        parentChildKey = parent + ":";
                                    } else {
                                        parentChildKey = "";
                                    }
                                    HashMap<String,String> mapValues = MD.get(parentChildKey + tag + ":" + "1");
                                    if (mapValues != null && mapValues.containsKey(x[5])) {
                                      v = mapValues.get(x[5]);
                                    } else {
                                        v = "";
                                    }
                                    elementNode.put(x[5], v);
                                     
                            }
            if (elementNode.size() > 0) {
            arN.add(elementNode);
          //  System.out.println("here limit <= 1: " + node.getData() + "/" +  elementNode.toString());
            }
        }  
    }  // if fields != null
    } // for each s.getKey
    if (arN.size() > 1) {
       // obN.set(node.getData(), arN);
        if (obN.has(node.getData())) {
                 obN.set(node.getData(), arN.get(node.getData()));
                } else {
                 obN.set(node.getData(), arN);   
                }
       // obN.putIfAbsent(node.getData(), arN);
        jr = new jsonRecord(obN, true);
        System.out.println("level=" + level + " arN > 1 " + node.getData() + "/"  + obN.toString() + "/" + jr.toString());
        
      //  System.out.println("here: arN > 1 " + node.getData() + "/" + jr.toString() + "/" + obN.toString());
    } else if (arN.size() == 1) {
        obN = (ObjectNode) arN.get(0);
       // System.out.println("obN " + obN.toString());
        jr = new jsonRecord(obN, false);
        System.out.println("level=" + level + " arN == 1" + node.getData() + "/"  + obN.toString() + "/" + jr.toString());
     //   System.out.println("here: arN == 1" + node.getData() + "/" + jr.toString() + "/" + obN.toString());    
    } else {
       // obN.set(node.getData(), null);
        jr = new jsonRecord(obN, false);
        System.out.println("level=" + level + " arN < 1" + node.getData() + "/"  + obN.toString() + "/" + jr.toString());
    }
    
    // now do children
    Iterator<bsNode<String>> it = node.getChildren().iterator();
  //  System.out.println("here children: " + node.getData() + "/" +  node.getChildren().size() + "/" + it.hasNext());
 //   System.out.println("root node name: " + rootnodename);
    /*
    if (! it.hasNext() && node.getData().equals(rootnodename)) {
       obN.set(node.getData(), jr.on()); 
    }
    */
    while (it.hasNext()) {  
    //    System.out.println("hasNext=" + node.getData() + "/" + obN.toString());
    	bsNode<String> nextNode = it.next();
    //    System.out.println("hasNextnode=" + node.getData() + "/" + nextNode.getData());
        int mylevel = level;
        mylevel++;
        jsonRecord jrNode = generateJSON(nextNode, rootnodename, mylevel, OSF, MD);
        if (jrNode.isArray()) { 
             System.out.println("obN before: " + obN.toString());
                if (obN.has(nextNode.getData())) {
                 obN.set(nextNode.getData(), jrNode.on().get(nextNode.getData()));
                } else {
                 obN.set(nextNode.getData(), jrNode.on());   
                }
             System.out.println("obN after: " + obN.toString());
            jr = new jsonRecord(obN, true);
            //jr = new jsonRecord(jrNode.on(), false);
            System.out.println("jr is array=" + nextNode.getData() + "/" + jrNode.on());
        } else {
            if (obN.has(nextNode.getData())) {
                 obN.set(nextNode.getData(), jrNode.on().get(nextNode.getData()));
                } else {
                 obN.set(nextNode.getData(), jrNode.on());   
                }
          //  System.out.println("obN Set=" + nextNode.getData() + "/" + jrNode.on() + "/" + obN.toString());
        }
      //  System.out.println("While children nextNode=" + nextNode.getData());
      //  System.out.println("While children obN=" + obN.toString());
     //   System.out.println("While children jr=" + jr.toString());
    }
    
    System.out.println("jr=level:" + level + "value: " + jr.toString());  
    return jr;
}

    public static jsonRecord generateJSONx(bsNode<String> node, String pnode, ObjectNode obNx, int level, LinkedHashMap<String, ArrayList<String[]>> osf, Map<String, HashMap<String,String>> MD) {
    
    ObjectNode obN = new ObjectMapper().createObjectNode(); 
    if (obNx != null) {
        obN = obNx;
    }
    ArrayNode arN = new ObjectMapper().createArrayNode();
    jsonRecord jr = null;
    if (node == null) {
        return jr;
    }

    
    
    String thiskey = "";
    String tag = "";
    
    // collect fields here
    
    for (Map.Entry<String, ArrayList<String[]>> s : osf.entrySet()) {
    thiskey = s.getKey();    
    if (thiskey.contains(":")) {
        tag = thiskey.substring(thiskey.lastIndexOf(":") + 1);
    } else {
        tag = thiskey;
    }    
  //  System.out.println("here tag: " + s.getKey() + "/" + tag + "/" + node.getData());    
    if (! tag.equals(node.getData())) {
        continue;
    }
    int actual = CountOMD(s.getKey());  // get actual loopcount  
    int maxallowed = CountLMLoopsOFS(s.getKey()); 
    
    int limit = 0;
  //  System.out.println("here preloopcount: " + s.getKey() + "/" + actual + "/" + maxallowed + "/" + limit); 
    if (actual >= maxallowed) {
        limit = maxallowed;
    } else {
        limit = actual;
    }
   if (limit <= 0) {
        limit = 1;
    }
  // System.out.println("here postloopcount: " + s.getKey() + "/" + actual + "/" + maxallowed); 
    String v = "";
    String parent = "";
    String parentChildKey = "";
    
    ArrayList<String[]> fields = osf.get(s.getKey());
    
    
  //  System.out.println("entering fields: " + s.getKey() + "/" + limit );
    if (fields != null) {
        if (limit > 1) {
            for (int k = 1; k <= limit; k++) {
                ObjectNode elementNode = new ObjectMapper().createObjectNode(); 
            for (int i = 0; i < fields.size(); i++) {
                                        String[] x = fields.get(i);
                                        if (x[4].toLowerCase().equals("yes")) {
                                            parent = x[1];
                                                continue;                                        
                                        }
                                        if (! parent.isEmpty()) {
                                            parentChildKey = parent + ":";
                                        } else {
                                            parentChildKey = "";
                                        }
                                        HashMap<String,String> mapValues = MD.get(parentChildKey + tag + ":" + k);
                                        if (mapValues != null && mapValues.containsKey(x[5])) {
                                          v = mapValues.get(x[5]);
                                        } else {
                                            v = "";
                                        }
                                        elementNode.put(x[5], v);
                                }
                arN.add(elementNode);
           //     System.out.println("here limit > 1: " + node.getData() + "/" +  elementNode.toString());
            } 
        } else {
            ObjectNode elementNode = new ObjectMapper().createObjectNode();
            for (int i = 0; i < fields.size(); i++) {
                        
                                    String[] x = fields.get(i);
                                    if (x[4].toLowerCase().equals("yes")) {
                                            parent = x[1];
                                            continue;
                                    }
                                    
                                    
                                    if (! parent.isEmpty()) {
                                        parentChildKey = parent + ":";
                                    } else {
                                        parentChildKey = "";
                                    }
                                    HashMap<String,String> mapValues = MD.get(parentChildKey + tag + ":" + "1");
                                    if (mapValues != null && mapValues.containsKey(x[5])) {
                                      v = mapValues.get(x[5]);
                                    } else {
                                        v = "";
                                    }
                                    elementNode.put(x[5], v);
                                     
                            }
            if (elementNode.size() > 0) {
            arN.add(elementNode);
          //  System.out.println("here limit <= 1: " + node.getData() + "/" +  elementNode.toString());
            }
        }  
    }  // if fields != null
    } // for each s.getKey
    if (arN.size() > 1) {
       // obN.set(node.getData(), arN);
        ObjectNode en = new ObjectMapper().createObjectNode();
        en.set(node.getData(), arN);
        // obN.set(pnode, arN.get(node.getData()));
        
        if (obN.has(pnode) && obN.get(pnode).isNull()) {
         obN.set(pnode, en);
        } else {
         obN.set(node.getData(), arN);   
        }
        
       // obN.putIfAbsent(node.getData(), arN);
        jr = new jsonRecord(obN, true);
     //   System.out.println("level=" + level + " arN > 1 " + node.getData() + "/"  + pnode);
        
      //  System.out.println("here: arN > 1 " + node.getData() + "/" + jr.toString() + "/" + obN.toString());
    } else if (arN.size() == 1) {
        obN = (ObjectNode) arN.get(0);
       // System.out.println("obN " + obN.toString());
        jr = new jsonRecord(obN, false);
      //  System.out.println("level=" + level + " arN == 1 " +  node.getData() + "/"  + pnode);
     //   System.out.println("here: arN == 1" + node.getData() + "/" + jr.toString() + "/" + obN.toString());    
    } else {
        obN.set(node.getData(), null);
        jr = new jsonRecord(obN, false);
      //  System.out.println("level=" + level + " arN < 1 " + node.getData() + "/"  + pnode);
    }
    
    // now do children
    Iterator<bsNode<String>> it = node.getChildren().iterator();
  //  System.out.println("here children: " + node.getData() + "/" +  node.getChildren().size() + "/" + it.hasNext());
 //   System.out.println("root node name: " + rootnodename);
    /*
    if (! it.hasNext() && node.getData().equals(rootnodename)) {
       obN.set(node.getData(), jr.on()); 
    }
    */
    while (it.hasNext()) {  
    //    System.out.println("hasNext=" + node.getData() + "/" + obN.toString());
    	bsNode<String> nextNode = it.next();
    //    System.out.println("hasNextnode=" + node.getData() + "/" + nextNode.getData());
        int mylevel = level;
        mylevel++;
      //  System.out.println("recursive: " + "parent=" + node.getData() + "child:" + nextNode.getData());
        jsonRecord jrNode = generateJSONx(nextNode, node.getData(), obN, mylevel, OSF, MD);
        /*
        if (jrNode.isArray()) { 
             System.out.println("obN before: " + obN.toString());
                if (obN.has(nextNode.getData())) {
                 obN.set(nextNode.getData(), jrNode.on().get(nextNode.getData()));
                } else {
                 obN.set(nextNode.getData(), jrNode.on());   
                }
             System.out.println("obN after: " + obN.toString());
            jr = new jsonRecord(obN, true);
            //jr = new jsonRecord(jrNode.on(), false);
            System.out.println("jr is array=" + nextNode.getData() + "/" + jrNode.on());
        } else {
            if (obN.has(nextNode.getData())) {
                 obN.set(nextNode.getData(), jrNode.on().get(nextNode.getData()));
                } else {
                 obN.set(nextNode.getData(), jrNode.on());   
                }
          //  System.out.println("obN Set=" + nextNode.getData() + "/" + jrNode.on() + "/" + obN.toString());
        }
        */
      //  System.out.println("While children nextNode=" + nextNode.getData());
      //  System.out.println("While children obN=" + obN.toString());
     //   System.out.println("While children jr=" + jr.toString());
    }
    
  //  System.out.println("jr=level:" + level + "value: " + jr.toString());  
    return jr;
}

    
    public static bsNode<String> treeFromFile(bsNode<String> node, String tag) {
	//if (tag.isBlank()) {
       // return node;
       // }
	ArrayList<String> list = getChildrenFromFile(tag, OSF);
	
	for (int i = 0; i < list.size(); i++) {
		bsNode<String> newnode = new bsNode<String>(list.get(i));
		node.addChild(newnode);
		if (hasChildren(list.get(i), OSF)) {
			treeFromFile(newnode, list.get(i));
		} 
	}
	return node;
    }

    public static boolean hasChildren(String x, LinkedHashMap<String, ArrayList<String[]>> osf) {
	for (Map.Entry<String, ArrayList<String[]>> s : osf.entrySet()) {
		String[] recArray = s.getValue().get(0);
		if (! recArray[4].toLowerCase().equals("yes")) {
			continue;
		}
		if (recArray[1].equals(x)) {
			return true;
		}
	}
	return false;
}

    public static ArrayList<String> getChildrenFromFile(String x, LinkedHashMap<String, ArrayList<String[]>> osf) {
	ArrayList<String> list = new ArrayList<String>();
	String parent = "";
        String thiskey = "";
        String tag = "";
	for (Map.Entry<String, ArrayList<String[]>> s : osf.entrySet()) {
		thiskey = s.getKey();    
                if (thiskey.contains(":")) {
                    tag = thiskey.substring(thiskey.lastIndexOf(":") + 1);
                } else {
                    tag = thiskey;
                }
                
		String[] recArray = s.getValue().get(0);
		if (! recArray[4].toLowerCase().equals("yes")) {
			continue;
		}
                // skip implied root types...where root = blank and parent = blank
                if (recArray[0].isBlank() && recArray[1].isBlank()) {
			continue;
		}
                
		if (recArray[1].equals(x)) {
			//System.out.println("children: " + s.getKey());
			list.add(tag);
		}
	}
	return list;
}

    
    @EDI.AnnoDoc(desc = {"method assigns output value for element of specific segment.",
                     "NOTE: 2nd parameter...element...must be named element...not position",
                     "Example:  mapSegment(\"BIG\",\"e02\",\"12345678\") assigns: 2nd element/field of BIG segment with value 12345678"},
                 params = {"String segment","String element","String value"}) 
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
     
    @EDI.AnnoDoc(desc = {"method assigns output component of element of segment.",
                     "NOTE: Used primarily for EDIFACT composites within elements",
                     "NOTE: 2nd parameter...element...must be named element...not position",
                     "NOTE: 3nd parameter...comp...must be sub-element position (integer) of element",
                     "Example:  mapSegment(\"QTY\",\"e01\",2,\"50\") assigns: 2nd comp of 1st element of QTY (EDIFACT) segment with value 50...QTY+21:50:EA"},
                 params = {"String segment","String element", "Integer comp", "String value"}) 
    public static void mapSegment(String segment, String element, Integer comp, String value) {
    	 String[] z = new String[] {element, value};
    	 // get old arraylist and add to it
    	 ArrayList<String[]> old = new ArrayList<String[]>();
    	 if (HASH.get(segment) != null) {
    		 old = HASH.get(segment);
    	 }
    	 old.add(z);
    	 HASH.put(segment, old);
     }
    
    @EDI.AnnoDoc(desc = {"method commits all assigned elements/fields of segment to output array.",
                     "NOTE: after all elements/fields have been assigned, the commitSegment method must be called",
                     "Example:  commitSegment(\"BIG\") writes BIG segment (and all assigned fields) to output"},
                 params = {"String segment"}) 
    public static void commitSegment(String segment) {
    	 // loop through HASH and create t for this segment
         int count = commitCount(segment);
         
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
    		OMD.put(segment + ":" + count, t);
    	 }	
         
         HASH.clear();
     }
    
    @EDI.AnnoDoc(desc = {"method commits all assigned elements/fields of segment to output array with Group Loop Anchoring boolean.",
                     "NOTE: after all elements/fields have been assigned, the commitSegment method must be called",
                     "Example:  commitSegment(\"PO1\", true) writes PO1 segment (and all assigned fields) to output"},
                 params = {"String segment"}) 
    public static void commitSegment(String segment, boolean x) {
    	 // loop through HASH and create t for this segment
         
         if (x) {
            commitLoopCount(segment); 
         }
         
         int count = commitCount(segment);
         
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
    		OMD.put(segment + ":" + count, t);
    	 }	
         
         HASH.clear();
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
        // segment = ":" + segment; // preprend blank
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
  
    
    
    @EDI.AnnoDoc(desc = {"method reads value from source at segment and element (by integer) with qualifier x.",
                     "NOTE: Qualifier has a position indicator of which field/element the qualifier is in",
                     "NOTE: this method only supports integer qualifier positions...not named",
                     "Example:  getInput(\"N1\",\"1:ST\",4) returns: 4th element/field of N1 segment...if field 1 = ST"},
            params = {"String segment","String position:qualifier","Integer ElementNumber"})  
    public static String getInput(String segment, String qual, Integer elementNbr) {
        String x = "";
         int count = 0;
         String[] q = qual.split(":",-1);
         String[] k = null;
         String[] t = null;
        // segment = ":" + segment; // preprend blank
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
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
         
         return x;
     }
    
    
    @EDI.AnnoDoc(desc = {"method reads value from source at segment and element (by integer) with qualifier x.",
                     "NOTE: Qualifier has a position indicator of which field/element the qualifier is in",   
                     "Example:  getInput(\"E2EDK02\",\"qualf:009\",\"belnr\") returns: specific value of field named belnr of IDOC segment E2EDK02...if qualf fieldname = 009"},
            params = {"String segment","String position:qualifier","Integer ElementName"})  
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
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             if (z.getKey().split("\\+")[0].equals(segment)) {
                 t = z.getValue();
                 if (t != null && t.length >= Integer.valueOf(qualNbr) && t[Integer.valueOf(qualNbr)].trim().equals(q[1].toUpperCase())) {
                     k = t;
                 }
             }
         }
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
         return x;
     }
    
    @EDI.AnnoDoc(desc = {"method reads value from source at segment and element (by integer).",
                        "Example:  getInput(\"BEG\",3) returns: 3rd element/field of BEG segment"},
            params = {"String segment","Integer ElementNumber"})      
    public static String getInput(String segment, Integer elementNbr) {
         String x = "";
         int count = 0;
         String[] k = null;
         if (segment.contains("+")) {  // overloading (again) as key type entry (used with getLoopKeys)
           k = mappedInput.get(segment);  
         } else { // else as actual segment entry
           //  segment = ":" + segment; // preprend blank
             for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
                 if (z.getKey().split("\\+")[0].equals(segment)) {
                     count++;
                     k = z.getValue();
                 }
             }
         }
       
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
        
         if (GlobalDebug)
         System.out.println("getInput:" + segment + "/" + x);
         
         return x;
     }
        
    @EDI.AnnoDoc(desc = {"method reads value from source at segment and element (by name).",
                        "NOTE: JSON, XML compatible ",
                        "Example:  getInput(\"BEG\",\"fieldname\") returns: specific field of BEG segment"},
            params = {"String segment","String ElementName"}) 
    public static String getInput(String segment, String elementName) {
         String x = "";
         int elementNbr = 0;
         if (inputfiletype.equals("X12") || inputfiletype.equals("UNE") || inputfiletype.equals("FF")) {
             elementNbr = getElementNumber(segment, elementName);
             if (elementNbr == 0) {
              return x;
             }
         } 
         
         String[] k = null;
         if (segment.contains("+")) {  // overloading (again) as key type entry (used with getLoopKeys)
           k = mappedInput.get(segment);  
         } else { // else as actual segment entry
            // segment = ":" + segment; // preprend blank
             for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
                String[] v = z.getKey().split("\\+");
                if (inputfiletype.equals("X12") || inputfiletype.equals("UNE") || inputfiletype.equals("FF")) {
                    if (v[0].equals(segment)) {
                        k = z.getValue();
                    }
                } else {
                    if (v[0].equals(segment + ":" + elementName)) {
                        k = z.getValue();
                    }
                }
                
             }
         }
         
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
         if (GlobalDebug)
         System.out.println("getInput:" + segment + "/" + x);
         
         
         return x;
         
     }
    
    @EDI.AnnoDoc(desc = {"method returns numeric (double) value from source at segment and element.",
                        "Example:  getInput(\"BEG\",3) returns: 3rd element/field of BEG segment as double"},
            params = {"String segment","Integer ElementNumber"})      
    public static double getInputNumber(String segment, Integer elementNbr) {
         String x = "";
         double d = 0;
         int count = 0;
         String[] k = null;
         DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
         df.applyPattern("#0.####");
         if (segment.contains("+")) {  // overloading (again) as key type entry (used with getLoopKeys)
           k = mappedInput.get(segment);  
         } else { // else as actual segment entry
           //  segment = ":" + segment; // preprend blank
             for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
                 if (z.getKey().split("\\+")[0].equals(segment)) {
                     count++;
                     k = z.getValue();
                 }
             }
         }
       
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr].trim();
         }
        
         try {
           d = df.parse(x).doubleValue();
         } catch (ParseException ex) {
         }
         
         if (GlobalDebug)
         System.out.println("getInputNumber:" + segment + "/" + x);
         
         return d;
     }
    
    // composite methods
    @EDI.AnnoDoc(desc = {"method reads composite element identified at segment and elementNumber and compNumber.",
                        "Example:  getInput(\"UNB\",2,2) returns: 2nd component of 2rd element of UNB segment (EDIFACT sender qualifier)"},
            params = {"String segment","Integer ElementNumber","Integer compNumber"})      
    public static String getInputComp(String segment, Integer elementNbr, Integer comp) {
         String x = "";
         String r = "";
         int count = 0;
         String[] k = null;
         
         if (comp <= 0) {
             return x;
         }
         
         if (segment.contains("+")) {  // overloading (again) as key type entry (used with getLoopKeys)
           k = mappedInput.get(segment);  
         } else { // else as actual segment entry
           //  segment = ":" + segment; // preprend blank
             for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
                 if (z.getKey().split("\\+")[0].equals(segment)) {
                     count++;
                     k = z.getValue();
                 }
             }
         }
       
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
        
         if (x.contains(ud)) {
            String[] y = x.split(ud,-1);
            if (y != null && y.length >= comp) {
              r = y[comp - 1].trim();  
            }
         }
         
         if (GlobalDebug) {
         System.out.println("getInputComp:" + segment + "/" + r);
         }
         
         return r;
     }
    
    @EDI.AnnoDoc(desc = {"method reads composite element identified at segment and elementNumber and compNumber for a Group Segment.",
                      "Note:  this is typically used in a looping construct in conjunction with getGroupCount()",  
                      "Example:  getInput(i,\"LIN\",3, 2) returns: 2st composite value of 3th element of Edifact LIN segment in loop index 'i' "},
            params = {"Integer LoopIndex", "String segment", "Integer ElementNumber", "Integer compNumber"})  
    public static String getInputComp(Integer gloop, String segment, Integer elementNbr, Integer comp) {
         String x = "";
         String r = "";
         String[] k = null;
         
         if (comp <= 0) {
             return x;
         }
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 k = z.getValue();
             }
         }
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
         if (x.contains(ud)) {
            String[] y = x.split(ud,-1);
            if (y != null && y.length >= comp) {
              r = y[comp - 1].trim();  
            }
         }
         return r;
     }
    
    @EDI.AnnoDoc(desc = {"method concatenates values into String delimited by sub elmement delimiter (oud) ",
                        "Example:  composite(\"w\", \"x\", \"y\", \"z\") returns: \"w:x:y:z\" as String"},
            params = {"String 1stvalue, String 2ndvalue, String 3rdvalue, String 4thvalue"})  
    public static String composite(String a, String b, String c, String d) {
        String[] segaccum = new String[]{a,b,c,d};
        trimSegment(String.join(oud,segaccum), oud);
        return trimSegment(String.join(oud,segaccum), oud);
    }

    @EDI.AnnoDoc(desc = {"method concatenates values into String delimited by sub elmement delimiter (oud) ",
                        "Example:  composite(\"x\", \"y\", \"z\") returns: \"x:y:z\" as String"},
            params = {"String 1stvalue, String 2ndvalue, String 3rdvalue"})  
    public static String composite(String a, String b, String c) {
        String[] segaccum = new String[]{a,b,c};
        trimSegment(String.join(oud,segaccum), oud);
        return trimSegment(String.join(oud,segaccum), oud);
    }

    @EDI.AnnoDoc(desc = {"method concatenates values into String delimited by sub elmement delimiter (oud) ",
                        "Example:  composite(\"x\", \"y\") returns: \"x:y\" as String"},
            params = {"String 1stvalue, String 2ndvalue"})  
    public static String composite(String a, String b) {
        String[] segaccum = new String[]{a,b};
        trimSegment(String.join(oud,segaccum), oud);
        return trimSegment(String.join(oud,segaccum), oud);
    }

    // looping getInput
    
    @EDI.AnnoDoc(desc = {"method reads value from source at segment and element (by integer) for a Group Segment.",
                      "Note:  this is typically used in a looping construct in conjunction with getGroupCount()",  
                      "Example:  getInput(i,\"PO1\",4) returns: 4th element/field of PO1 segment in loop index 'i' "},
            params = {"Integer LoopIndex", "String segment", "Integer ElementNumber"})  
    public static String getInput(Integer gloop, String segment, Integer elementNbr) {
         String x = "";
         String[] k = null;
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 k = z.getValue();
             }
         }
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
         return x;
     }
    
    @EDI.AnnoDoc(desc = {"method returns numeric (double) value from source at segment and element for a Group Segment.",
                      "Note:  this is typically used in a looping construct in conjunction with getGroupCount()",  
                      "Example:  getInput(i,\"PO1\",4) returns: 4th element/field of PO1 segment as double in loop index 'i' "},
            params = {"Integer LoopIndex", "String segment", "Integer ElementNumber"})  
    public static double getInputNumber(Integer gloop, String segment, Integer elementNbr) {
         String x = "";
         double d = 0;
         String[] k = null;
         DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
         df.applyPattern("#0.####");
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 k = z.getValue();
             }
         }
         if (k != null && k.length > elementNbr) {
          x =  k[elementNbr].trim();
         }
         
         try {
           d = df.parse(x).doubleValue();
         } catch (ParseException ex) {
         }
         
         return d;
     }
    
    
    @EDI.AnnoDoc(desc = {"method reads value from source at segment and element (by fieldname) for a Group/Loop Segment.",
                      "NOTE: JSON, XML compatible ",
                      "Note:  this is typically used in a looping construct in conjunction with getGroupCount() or getLoopCount()",  
                      "Example:  getInput(i,\"E2EDP01\",\"belnr\") returns: fieldname 'belnr' of idoc segment ED2EDP01 in loop index 'i' "},
            params = {"Integer LoopIndex", "String segment", "String ElementName"})  
    public static String getInput(Integer gloop, String segment, String elementName) {
         String x = "";
         int elementNbr = 0;
         if (inputfiletype.equals("X12") || inputfiletype.equals("UNE") || inputfiletype.equals("FF")) {
             elementNbr = getElementNumber(segment, elementName);
             if (elementNbr == 0) {
              return x;
             }
         }
         // get count of tags in segment... for json/xml usage
         String[] j = segment.split(":",-1);
         
         String[] k = null;
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v.length < 2) {
                 continue;
             }
            if (inputfiletype.equals("X12") || inputfiletype.equals("UNE") || inputfiletype.equals("FF")) {
                    if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                        k = z.getValue();
                    }
            } else {
                String[] vsub = v[1].split(",",-1);
                // this needs to be revisited... vsub[j.length-1] only works for variable loop counts...not implemented yet
                // hardcode to third ,,, value...which will be element 2
                if (v[0].equals(segment + ":" + elementName) && vsub.length >= j.length && vsub[j.length-1].equals(String.valueOf(gloop))) {
              //  if (v[0].equals(segment + ":" + elementName) && vsub.length >= j.length && vsub[2].equals(String.valueOf(gloop))) {
                    k = z.getValue();
                }
            }
            
             
         }
         
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
         
         if (GlobalDebug)
         System.out.println("getInput (i): " + String.valueOf(gloop) + "->"  + segment + "/" + elementName + "/" + elementNbr + ": " + x);
         
         return x;
     }
    
    @EDI.AnnoDoc(desc = {"method reads value from source at segment and element (by integer) for a Group Segment.",
                      "Note:  this is typically used in a looping construct in conjunction with getGroupCount()",  
                      "Example:  getInput(i,\"E2EDP19\",\"qualf:002\", 8) returns: 4th element/field of idoc segment 'E2EDP19' with fieldname 'qualf' value = '002' in loop index 'i' "},
            params = {"Integer LoopIndex", "String segment", "String qualfieldname:value", "Integer ElementNumber"})
    public static String getInput(Integer gloop, String segment, String qual, Integer elementNbr) {
         String x = "";
         String[] k = null;
         String[] q = qual.split(":",-1);
         int qualNbr = getElementNumber(segment,q[0]);
         if (qualNbr == 0) {
             return x;
         }
         String[] t = null;
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 t = z.getValue();
                 if ( (t != null) && (t.length >= qualNbr) && (t[qualNbr].equals(q[1].toUpperCase())) ) {
                     k = t;
                 } 
             }
         }
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
         return x;
     }
    
    @EDI.AnnoDoc(desc = {"method reads value from source at segment and element (by fieldname) for a Group Segment.",
                      "Note:  this is typically used in a looping construct in conjunction with getGroupCount()",  
                      "Example:  getInput(i,\"E2EDP19\",\"qualf:002\", \"iddat\") returns: fieldname 'iddat' value of idoc segment 'E2EDP19' ...if value of fieldname 'qualf' = '002'... in loop index 'i' "},
            params = {"Integer LoopIndex", "String segment", "String qualfieldname:value", "String ElementName"})
    public static String getInput(Integer gloop, String segment, String qual, String elementName) {
         String x = "";
         String[] k = null;
         int elementNbr = 0;
         if (inputfiletype.equals("X12") || inputfiletype.equals("UNE") || inputfiletype.equals("FF") || inputfiletype.equals("XML")) {
             elementNbr = getElementNumber(segment, elementName);
             if (elementNbr == 0) {
              return x;
             }
         }
         
         // get count of tags in segment... for json/xml usage
         String[] j = segment.split(":",-1);
         
         String[] q = qual.split(":",-1);
         int qualNbr = getElementNumber(segment,q[0]);
         if (qualNbr == 0) {
             return x;
         }
         String[] t = null;
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                 t = z.getValue();
                 if ( (t != null) && (t.length >= qualNbr) && (t[qualNbr].equals(q[1].toUpperCase())) ) {
                     k = t;
                 } 
             }
             
             if (inputfiletype.equals("X12") || inputfiletype.equals("UNE") || inputfiletype.equals("FF") || inputfiletype.equals("XML")) {
                    if (v[0].equals(segment) && v[1].equals(String.valueOf(gloop))) {
                        t = z.getValue();
                        if ( (t != null) && (t.length >= qualNbr) && (t[qualNbr].equals(q[1].toUpperCase())) ) {
                            k = t;
                        } 
                    }
            } else {
                String[] vsub = v[1].split(",",-1);
                if (v[0].equals(segment + ":" + elementName) && vsub.length >= j.length && vsub[j.length-1].equals(String.valueOf(gloop))) {
                    t = z.getValue();
                        if ( (t != null) && (t.length >= qualNbr) && (t[qualNbr].equals(q[1].toUpperCase())) ) {
                            k = t;
                        } 
                }
            }
             
             
         }
         if (k != null && k.length > elementNbr && k[elementNbr] != null) {
          x =  k[elementNbr].trim();
         }
         return x;
     }
     
    @EDI.AnnoDoc(desc = {"method used for CSV files to read rows and fields from source.",
                      "Note:  this is typically used CSV files where the landmark tag is always 'ROW'",  
                      "Example:  getRow(i,j) returns: jth column of row i "},
            params = {"Integer row", "Integer column"})  
    public static String getRow(Integer row, Integer field) {
         String x = "";
         String[] k = null;
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[2].equals(String.valueOf(row))) {
                 k = z.getValue();
             }
         }
         if (k != null && k.length > field) {
          x =  k[field].trim();
         }
         return x;
     }
    
    @EDI.AnnoDoc(desc = {"method returns row count (as integer) of rows in a CSV file",
                      "Note:  this is exclusively used for CSV files",  
                      "Example:  getRowCount() returns: x number of rows "},
            params = {})  
    public static int getRowCount() {
         int i = 0;
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             if (z.getKey() == null || z.getKey().isBlank()) {
                 continue;
             }
             if (z.getValue() == null || z.getValue().length <= 0) {
                 continue;
             }
            i++; 
         }
         return i;
     }
    
    @EDI.AnnoDoc(desc = {"method returns sum of all row of column x in a CSV file",
                      "Note:  this is exclusively used with CSV files",  
                      "Note:  returned format is #0.####",
                      "Example:  getRowSum(7) returns: sum of all values in column 7"},
            params = {"Integer field"})  
    public static String getRowSum(Integer field) {
         double sum = 0;
         double d = 0;
         String[] k = null;
         DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
         df.applyPattern("#0.####");
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
                 k = z.getValue();
                 try {
                       d = df.parse(k[field]).doubleValue();
                    } catch (ParseException ex) {
                    }
                 sum += d;
         }
         
         return df.format(sum);
     }
    
    
    public static int getElementNumber(String segment, String element) {
         boolean inside = false;
         int x = 0;
         int r = 0;
         String parent = "";
         String seg = segment;
         if (BlueSeerUtils.isParsableToInt(element)) {
             return Integer.valueOf(element);
         }
         
         if (segment.contains(":")) {
             parent = segment.substring(0, segment.lastIndexOf(":"));
             seg = segment.substring(segment.lastIndexOf(":") + 1);
         }
         
         for (String[] z : ISF) {  
            if (z[5].equals("groupend")) {
                  continue;
            } 
            if (seg.equals(z[0]) && (parent.isBlank() || parent.equals(z[1]))) {
                if (! z[4].toLowerCase().equals("yes")) {
                  x++;
                }
             ///   System.out.println("HERE: " + seg + "/" + z[0] + "/" + parent + "/" + z[1] + "/" + x);
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
    
    @EDI.AnnoDoc(desc = {"method reads the loop count of a specific group segment.",
                     "NOTE: This is particularly useful for grouping segments like N1 (N2,N3) where the number of N1 loops is required",
                     "Example:  getGroupCount(\"N1\") returns: Number of N1 'group' occurences in source file"},
                 params = {"String segment"}) 
    public static int getGroupCount(String segment) {
         
         int count = 0;
         String[] k = null;
        // segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             if (z.getKey().split("\\+")[0].equals(segment)) {
                 count++;
             }
         }
        
         return count;
     }
    
    @EDI.AnnoDoc(desc = {"method retrieves the keys for a specific repeating segment...such as a REF segment.",
                     "NOTE: The keys are defined as unique identifier tags for each occurence of the repeating segment.",
                     "Example:  getLoopKeys(\"REF\") returns: all repeating occurences of REF scoped to header",
                     "Example:  getLoopKeys(\"PO1:PID\") returns: all repeating occurences of PID for a specific PO1 index"},
                 params = {"String segment"}) 
    public static ArrayList<String> getLoopKeys(String segment) {
         ArrayList<String> k = new ArrayList<String>();
       //  segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v.length == 3) {
                 k.add(v[0] + "+" + v[1] + "+" + v[2]);
             }
         }
         return k;
     }
    
    @EDI.AnnoDoc(desc = {"method retrieves the keys for a specific repeating segment...such as a REF segment.",
                     "NOTE: The keys are defined as unique identifier tags for each occurence of the repeating segment.",
                     "Example:  getLoopKeys(\"PO1:PID\", i) returns: all repeating occurences of PID for a specific PO1 loop index 'i'"},
                 params = {"String segment", "Integer LoopIndex"}) 
    public static ArrayList<String> getLoopKeys(String segment, Integer g) {
         ArrayList<String> k = new ArrayList<String>();
       //  segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v[0].equals(segment) && v[1].equals(String.valueOf(g)) && v.length == 3) {
                 k.add(v[0] + "+" + v[1] + "+" + v[2]);
             }
         }
         return k;
     }
    
      @EDI.AnnoDoc(desc = {"method retrieves the loop count of a parent:tag segment...used in json and xml inbound",
                     "NOTE: JSON, XML compatible ",
                     "NOTE: tag/position format:  A:B:C+1,1,1+ ...x,x,x is 'positional loop count' of A or B or C ",
                     "NOTE: The keys are defined as unique identifier tags for each occurence of the repeating segment.",
                     "Example:  getLoopCount(\"GPARENT:PARENT:CHILD:\", pos) returns: last count of tag position subelement(tag+,,,+) "},
                 params = {"String segment", "Integer TagPosition"}) 
    public static int getLoopCount(String segment, Integer g) {
         int k = 0;
       //  segment = ":" + segment; // preprend blank
         for (Map.Entry<String, String[]> z : mappedInput.entrySet()) {
             String[] v = z.getKey().split("\\+");
             if (v != null && v.length > 1 && v[0].startsWith(segment)) {
                 String[] tagLoopPosition = v[1].split(",",-1);
                 if (tagLoopPosition != null && tagLoopPosition.length > g) {
                   k = Integer.valueOf(tagLoopPosition[g]);
                 }
             }
         }
         return k;
     }
  
    
       @EDI.AnnoDoc(desc = {"method kills or stops the execution of the map at point of call",
                     "NOTE:  the first variable...mx...is not a String...and should not be put in quotes",
                     "NOTE:  the log file will display UserDefinedException along with the passed message parameter",
                     "Example:  kill(mx, \"some message\");"},
                 params = {"ArrayList<String[]> mx", "String messg"}) 
    public void kill(ArrayList<String[]> mx, String messg) throws UserDefinedException {
        mx.addAll(mxs);
        throw new UserDefinedException(messg);
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
