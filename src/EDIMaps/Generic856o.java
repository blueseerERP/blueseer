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

package EDIMaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDI;
import com.blueseer.edi.EDI.*;
import com.blueseer.utl.BlueSeerUtils;


/**
 *
 * @author vaughnte
 */
public class Generic856o {
    
    public static String[] Mapdata(String control[], String shipper, String entity) throws IOException {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = "856";
        
       // get delimiters for this trading partner, doctype, docdirection
        String[] delimiters = OVData.getDelimiters(entity, doctype, "0");
        String dir = OVData.getEDICustDir(entity, doctype, "0");
         String sd = delimiters[0];
         String ed = delimiters[1];
         String ud = delimiters[2];
         
         int i = 0;
         int segcount = 0;
         int hdrsegcount = 0;
         int detsegcount = 0;
         int trlsegcount = 0;
         
         // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber)
         String[] envelope = EDI.generateEnvelope(entity, doctype, "0");
         String ISA = envelope[0];
         String GS = envelope[1];
         String GE = envelope[2];
         String IEA = envelope[3];
         String filename = envelope[4];
           String isactrl = envelope[5];
         String gsctrl = envelope[6];
         String stctrl = "0001"; // String.format("%09d", gsctrl);
        
         // assign missing pieces of control (filename, isactrl, gsctrl, stctrl) which are characteristic of ALL outbound documents.  This must be done for ALL outbound maps
        control[3] = filename;
        control[4] = isactrl;
        control[5] = gsctrl;
        control[6] = stctrl;
         
      
        String ST = "ST" + ed + doctype + ed + stctrl + sd;
        hdrsegcount = 2; // "ST and SE" included
         // now lets get shipper header info 
         // cust, ship, so, po, podate, shipdate, remarks, ref, shipvia, grosswt, netwt, trailernumber, site
         
         String sh_string = OVData.getShipperHeader(shipper);
        String[] sh = sh_string.split(",", -1);
        
          
         String Header = "";
         ArrayList<String> H = new ArrayList();
         H.add("BSN" + ed + "00" + ed +  shipper + ed + sh[5] + ed + "1200");
         H.add("DTM" + ed + "011" + ed + sh[4] + ed + "1200");
         H.add("DTM" + ed + "067" + ed + sh[4] + ed + "1200");
         H.add("HL" + ed + "1" + ed + ed + "S" + ed + "1");
         H.add("TD5" + ed + "B" + ed + "2" + ed + sh[8] + ed + "ZZ" + ed + sh[8]);
         H.add("REF" + ed + "CN" + sh[10]);
         H.add("N1" + ed + "ST" + ed + OVData.getCustName(sh[0]) + ed + "92" + sh[1]);
         H.add("N1" + ed + "SF" + ed + OVData.getDefaultSiteName() + ed + "92" + sh[12]);
         H.add("HL" + ed + "2" + ed + "1" + ed + "O" + ed + "1");
         H.add("PRF" + ed + sh[3] + ed + "00" + ed + ed + sh[4]);
         hdrsegcount += 10;
                  
         for (String z : H) {
             Header += (EDI.trimSegment(z, ed).toUpperCase() + sd);
         }
         
         
         
         String Detail = "";
         ArrayList<String> D = new ArrayList();
         
         i = 3;
         int sumqty = 0;
         // part, custpart, qty, po, cumqty, listprice, netprice, reference
         ArrayList<String> mylines = OVData.getShipperLines(shipper);
         String[] detail = null;
              for (String myline : mylines) {
                  detail = myline.split(",",-1);
                  sumqty = sumqty + Integer.valueOf(detail[2]);
                  D.add("HL" + ed  + String.valueOf(i) + ed + "2" + ed + "I" + ed + "1");
                  D.add("LIN" + ed + String.valueOf(i) + ed + "BP" + ed + detail[1] + ed + "VP" + ed +  detail[0]);
                  D.add("SN1" + ed + String.valueOf(i) + ed + detail[2] + ed + "EA" + ed + detail[2] + ed + ed + ed + ed + "AC");
                  i++;
              }
              
         detsegcount += 3;
            
         for (String d : D) {
             Detail += (EDI.trimSegment(d, ed).toUpperCase() + sd);
         }    
                 
         String Trailer = "";
          ArrayList<String> T = new ArrayList();
          T.add("CTT" + ed + String.valueOf(i));
          trlsegcount += 1;
          
         for (String t : T) {
             Trailer += (EDI.trimSegment(t, ed).toUpperCase() + sd);
         } 
            
         segcount = hdrsegcount + detsegcount + trlsegcount;
           
         String SE = "SE" + ed + String.valueOf(segcount) + ed + "0001" + sd;
                 // concat and send content to edi.writeFile
                 String content = ISA + GS + ST + Header + Detail + Trailer + SE + GE + IEA;
                 edi.writeFile(content, dir, filename); 
                 
                 
 
                 return control;
}

}
