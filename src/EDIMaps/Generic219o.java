/*
   Copyright 2005-2017 Terry Evans Vaughn ("VCSCode").

With regard to the Blueseer Software:

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

For all third party components incorporated into the GitLab Software, those 
components are licensed under the original license provided by the owner of the 
applicable component.

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
public class Generic219o {
    
    public static String[] Mapdata(String[] control, String nbr, String entity) throws IOException {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = "219";
        
       
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
        
         
         // now lets get order header info 
          // fonbr, ref, site, wh, date, remarks, carrier, carrier_assigned
         String[] h = OVData.getFreightOrderHeaderArray(nbr);
         
         // now detail
         // fod_line, fod_type, fod_shipper, fod_ref, fod_shipdate, fod_shiptime, fod_delvdate, fod_delvtime, fod_code, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip ...
         // fod_phone, fod_contact, fod_remarks, fod_pallets, fod_boxes, fod_weight, fod_wt_uom 
         // 20 elements
         ArrayList<String[]> dt = OVData.getFreightOrderDetailArray(nbr);
         
         
         // get Shipto Address (9 elements)  id, name, line1, line2, line3, city, state, zip, country
      //   String[] st = OVData.getShipToAddressArray(h[2], h[3]);
         
        
        
        
        String ST = "ST" + ed + doctype + ed + stctrl + sd;
        hdrsegcount = 2; // "ST and SE" included
         
         String Header = "";
         ArrayList<String> S = new ArrayList();
         S.add("B9" + ed + h[0] + ed + "13" + ed + "PP");
         S.add("B9A" + ed + "CS");
         hdrsegcount += 2;
         if (! h[1].isEmpty())  {
           S.add("L11" + ed + h[1] + ed + "BM");
           S.add("L11" + ed + h[1] + ed + "DO");
           hdrsegcount += 2;
         }
         
         if (! h[5].isEmpty()) {
             S.add("NTE" + ed + ed + h[5]);
             hdrsegcount += 1;
         }
        
         
         for (String s : S) {
             Header += (EDI.trimSegment(s, ed).toUpperCase() + sd);
         }
         
         
         
         String Detail = "";
         ArrayList<String> D = new ArrayList();
         int z = 0;
         int totqty = 0;
         double totwt = 0.00;
         for (String[] d : dt) {
             z++;
            
             
             if (d[1].equals("LD")) {   // there should always ONLY BE 1 LD
               totqty = Integer.valueOf(d[18]);
               totwt = Double.valueOf(d[20]);
             D.add("S5" + ed + String.valueOf(z) + ed + d[1]);
             D.add("G62" + ed + "69" + ed + d[4] + ed + "EP" + ed + d[5] + ed + "LT"); // shipdate if z = 1...i.e. 1st record is always pickup....shipdate = date pickup from warehouse
             D.add("N1" + ed + "SF" + ed + d[9] + ed + "92" + ed + d[8]);
             D.add("N2" + ed + d[9]);
             D.add("N3" + ed + d[10]);
             D.add("N4" + ed + d[12] + ed + d[13] + ed + d[14]);
             D.add("G61" + ed + "SD" + ed + d[16] + ed + "TE" + ed + d[15]);
             D.add("LX" + ed + "1");
             D.add("LAD" + ed + "PLT" + ed + d[18] + ed + ed + "L" + ed + d[20]);
             detsegcount += 9;
             } else {
             D.add("S5" + ed + String.valueOf(z) + ed + d[1]);
             D.add("G62" + ed + "70" + ed + d[4] + ed + "G" + ed + d[7] + ed + "LT"); // delvdate if z <> 1...i.e. all other records are delivery dates    
             D.add("N1" + ed + "ST" + ed + d[9] + ed + "92" + ed + d[8]);
             D.add("N2" + ed + d[9]);
             D.add("N3" + ed + d[10]);
             D.add("N4" + ed + d[12] + ed + d[13] + ed + d[14]);
             D.add("G61" + ed + "SD" + ed + d[16] + ed + "TE" + ed + d[15]);
             D.add("LX" + ed + "1");
             D.add("LAD" + ed + "PLT" + ed + d[18] + ed + ed + "L" + ed + d[20]);
             detsegcount += 9;
             }
         }  
         
         for (String d : D) {
             Detail += (EDI.trimSegment(d, ed).toUpperCase() + sd);
         }
         
         
         String Trailer = "";
          ArrayList<String> T = new ArrayList();
          T.add("L3" + ed + String.valueOf(totwt) + ed + "G");
          hdrsegcount += 1;
          
         for (String t : T) {
             Trailer += (EDI.trimSegment(t, ed).toUpperCase() + sd);
         } 
            
         segcount = hdrsegcount + detsegcount;
         
         String SE = "SE" + ed + String.valueOf(segcount) + ed + "0001" + sd;
                         
         
                 
                 // concat and send content to edi.writeFile
                 String content = ISA + GS + ST + Header + Detail + Trailer + SE + GE + IEA;
                 edi.writeFile(content, dir, filename); 
                 
        /*
         output.write(ISA);
         output.write(GS);
         output.write(ST);
         output.write(Header);
         output.write(Detail);
         output.write(Trailer);
         output.close();
 */
                 return control;
}

}
