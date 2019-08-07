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
public class Generic810o {
    
    public static String[] Mapdata(String[] control, String shipper, String entity) throws IOException {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = "810";
        
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
         // cust, ship, so, po, podate, shipdate, remarks, ref, shipvia, grosswt, netwt, trailernumber
         
         String sh_string = OVData.getShipperHeader(shipper);
        String[] h = sh_string.split(",", -1);
        
        String shipto = h[1];
        String cust = h[0];
        String rmks = h[6];
         
         
         String Header = "";
         ArrayList<String> H = new ArrayList();
         H.add("BIG" + ed +  h[5].replace("-", "") + ed + shipper + ed + h[4].replace("-", "") +  ed + h[3]);
         H.add("REF" + ed + "ST" + ed + shipto);
         H.add("N1" + ed + "RE" + ed + OVData.getDefaultSiteName() + ed + "92" + ed + shipto);
         H.add("DTM" + ed + "011" + ed + h[5].replace("-", ""));
         hdrsegcount += 4;
                  
         for (String z : H) {
             Header += (EDI.trimSegment(z, ed).toUpperCase() + sd);
         }
                          
         
         String Detail = "";
         ArrayList<String> D = new ArrayList();
         String lin = "";
         i = 0;
         int sumqty = 0;
         double sumamt = 0.00;
         double sumlistamt = 0.00;
         
         // allowances 
         double promo = 0.00;
         double warranty = 0.00;
         double options = 0.00;
         double freight = 0.00;
         double sumamtTDS = 0.00;
         String sku = "";
         
           DecimalFormat df00 = new DecimalFormat("#0.00");
           DecimalFormat df = new DecimalFormat("#0");
         // part, custpart, qty, po, cumqty, listprice, netprice, reference, sku
         ArrayList<String> mylines = OVData.getShipperLines(shipper);
         String[] detail = null;
              for (String myline : mylines) {
                  detail = myline.split(",",-1);
                  
                  if (detail[8].isEmpty() && detail[8] != null) {
                      sku = OVData.getCustAltItem(cust, detail[0]);
                  }
                  
                  // skip if sku is blank
                  if (sku.isEmpty()) {
                      continue;
                  }
                  
                  // skip if zero net price
                  if (Double.valueOf(detail[6]) == 0) {
                      continue;
                  }
                  
                  sumqty = sumqty + Integer.valueOf(detail[2]);
                  sumamt = sumamt + (Double.valueOf(detail[2]) * Double.valueOf(detail[6]));
                  sumlistamt = sumlistamt + (Double.valueOf(detail[2]) * Double.valueOf(detail[5]));
                  D.add("IT1" + ed  +  ed + detail[2] + ed + "EA" + ed + df00.format(Double.valueOf(detail[5])) + ed + ed + "IN" + ed + sku + ed + "VP" + ed + detail[1]);
                  i++;
              }
            promo = (sumlistamt * .06 * 100);
            warranty = (sumlistamt * .02 * 100);
            options = (sumlistamt * .01 * 100);
            freight = (sumlistamt * .023 * 100);
            sumamtTDS = (sumamt * 100);
            
            detsegcount += 1;
            
             for (String d : D) {
             Detail += (EDI.trimSegment(d, ed).toUpperCase() + sd);
         }
            
                         
         String Trailer = "";
          ArrayList<String> T = new ArrayList();
          T.add("TDS" + ed + df.format(sumamtTDS));
          T.add("ISS" + ed + String.valueOf(sumqty) + ed + "EA" + ed + String.valueOf(sumqty) + ed + "LB");
          T.add("CTT" + ed + String.valueOf(i));
          trlsegcount += 3;
          
         for (String t : T) {
             Trailer += (EDI.trimSegment(t, ed).toUpperCase() + sd);
         } 
            
         segcount = hdrsegcount + detsegcount + trlsegcount;
           
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
