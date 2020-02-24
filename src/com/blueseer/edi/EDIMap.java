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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author terryva
 */
public abstract class EDIMap implements EDIMapi {
    
    String[] envelope = null;
         
         public static  int segcount = 0;
         
         DateFormat isadfdate = new SimpleDateFormat("yyMMdd");
         DateFormat gsdfdate = new SimpleDateFormat("yyyyMMdd");
         DateFormat isadftime = new SimpleDateFormat("HHmm");
         DateFormat gsdftime = new SimpleDateFormat("HHmm");
         Date now = new Date();
         
         public static String ISA = "";
         public static String GS = "";
         public static String GE = "";
         public static String IEA = "";
         
         public static String[] isaArray = new String[17];
         public static String[] gsArray = new String[9];
         public static String[] stArray = new String[3];
         public static String[] seArray = new String[3];
         public static String[] geArray = new String[3];
         public static String[] ieaArray = new String[3];
         
         public static String ST = "";
         public static String SE = "";
         
         public static String sender = "";
         public static String map = "";
         public static boolean isOverride = false;
         public static String doctype = "";
         
         public static String infile = "";
         public static String outfile = "";
         public static String filename = "";
         public static String isactrl = "";
         public static String gsctrl = "";
         public static String stctrl = "";
         public static String ref = "";
         
         public static String header = "";
         public static String detail = "";
         public static String trailer = "";
         
         public static String isa06 = "";
         public static String isa08 = "";
         public static String isa13 = "";
         public static String isa09 = "";
         
         public static String gs02 = "";
         public static String gs03 = "";
         
         public static ArrayList<String> H = new ArrayList();
         public static ArrayList<String> D = new ArrayList();
         public static ArrayList<String> T = new ArrayList();
         
         public static String sd = "";
         public static String ed = "";
         public static String ud = "";
         
         public static String content = "";
         
         
         public static boolean isSet(ArrayList list, Integer index) {
         return index != null && index >=0 && index < list.size() && list.get(index) != null;
         }
    
         public static boolean isSet(String[] list, Integer index) {
         return index != null && index >=0 && index < list.length && list[index] != null;
         }
    
         
         public void setISA (String[] isa) {
             isa06 = isa[6].trim();
             isa08 = isa[8].trim();
             isa09 = isa[9].trim();
             isa13 = isa[13].trim();
         }
         
         public void setGS (String[] gs) {
             gs02 = gs[2].trim();
             gs03 = gs[3].trim();
         }
         
         public void setControl(String[] c) {
            sender = c[0];
            doctype = c[1];
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
         }
         
        public String delimConvertIntToStr(String intdelim) {
        String delim = "";
        int x = Integer.valueOf(intdelim);
        delim = String.valueOf(Character.toString((char) x));
        return delim;
      }
      
         public void setFinalOutputString() {
           content = ISA + GS + ST + header + detail  + trailer + SE + GE + IEA;  
         } 
    
     
         public void setOutPutEnvelopeStrings(String[] c) { 
         
             if ( ! isOverride) {  // if not override...use internal partner / doc lookup for envelope info
               envelope = EDI.generateEnvelope(sender, doctype, "0"); // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber, gsctrlnbr)
               ISA = envelope[0];
               GS = envelope[1];
               GE = envelope[2];
               IEA = envelope[3];
               filename = envelope[4];
               outfile = filename;  
               isactrl = envelope[5];
               gsctrl = envelope[6];
               stctrl = String.format("%09d", Integer.valueOf(gsctrl));
               ST = "ST" + ed + doctype + ed + stctrl ;
               SE = "SE" + ed + String.valueOf(segcount) + ed + stctrl;  
               } else {
                 // you can override elements within the envelope xxArray fields at this point....or merge into segment string
                 // need to figure out what kind of error this bullshit is....
                 ISA = c[13];
                 GS = c[14];
                 GE = "GE" + ed + "1" + ed + c[5];
                 IEA = "IEA" + ed + "1" + ed + c[4];
                 ST = "ST" + ed + doctype + ed + c[6]; 
                 SE = "SE" + ed + "1" + ed + c[6];
                 
                 updateISA(9,""); // set date to now
                 updateISA(10,"");  // set time to now
                
                 H.clear();
                 D.clear();
                 T.clear();
                 
                 header = "";
                 detail = "";
                 trailer = "";
                 content = "";
               }
             
         }
         
         public String getISA(int i) {
             if (i > 16) {
                 return "";
             }
           isaArray = ISA.split(EDI.escapeDelimiter(ed), -1);  
           return isaArray[i];
         }
         
         public String getGS(int i) {
             if (i > 8) {
                 return "";
             }
           gsArray = GS.split(EDI.escapeDelimiter(ed), -1);  
           return gsArray[i];
         }
         
         
         public void updateISA(int i, String value) {
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
         
         public void updateGS(int i, String value) {
             if (i > 8) {
                 return;
             }
             gsArray = GS.split(EDI.escapeDelimiter(ed), -1);
             gsArray[i] = String.valueOf(value);
             GS = String.join(ed,gsArray);
         }
         
         public void updateSE() {
             seArray = SE.split(EDI.escapeDelimiter(ed), -1);
             seArray[1] = String.valueOf(segcount);
             SE = String.join(ed,seArray);
         }
         
         public void updateGE() {
             geArray = GE.split(EDI.escapeDelimiter(ed), -1);
             geArray[1] = String.valueOf(segcount);
             GE = String.join(ed,geArray);
         }
         
         public void setHDTStrings() {
              // first set segment terminator for envelope segments
                 ISA = ISA + sd;
                 GS = GS + sd;
                 ST = ST + sd;
                 SE = SE + sd;
                 GE = GE + sd;
                 IEA = IEA + sd;
             
             segcount = 2;  // ST and SE inclusive
             for (String h : H) {
                 header += (EDI.trimSegment(h, ed).toUpperCase() + sd);
                 segcount++;                 
             }

             for (String d : D) {
                 detail += (EDI.trimSegment(d, ed).toUpperCase() + sd);
                 segcount++;
             }

             for (String t : T) {
                 trailer += (EDI.trimSegment(t, ed).toUpperCase() + sd);
                 segcount++;
             }
             updateSE();
             
                 
             
         }
         
}
