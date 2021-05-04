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
public class Generic204o extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException {
   com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString(); 
        
     // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
    setOutPutEnvelopeStrings(c);
        
         
        
         // now lets get order header info 
         // fonbr, ref, site, wh, date, remarks, carrier, carrier_assigned
         String[] h = OVData.getFreightOrderHeaderArray(key);
         
         // now detail
         // fod_line, fod_type, fod_shipper, fod_ref, fod_shipdate, fod_shiptime, fod_delvdate, fod_delvtime, fod_code, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip ...
         // fod_phone, fod_contact, fod_remarks, fod_pallets, fod_boxes, fod_weight, fod_wt_uom 
         // 20 elements
         ArrayList<String[]> dt = OVData.getFreightOrderDetailArray(key);
         
         
         // get Shipto Address (9 elements)  id, name, line1, line2, line3, city, state, zip, country
      //   String[] st = OVData.getShipToAddressArray(h[2], h[3]);
         
        
        
        
       
         H.add("B2" + ed + ed + h[7] + ed + h[0] + ed + ed + ed + "PP");
         H.add("B2A" + ed + "00" + ed + "LT");
         if (! h[1].isEmpty())  {
           H.add("L11" + ed + h[1] + ed + "OQ");
         }
         
         if (! h[5].isEmpty()) {
             H.add("NTE" + ed + ed + h[5]);
         }
        
         
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
             
             if (! d[16].isEmpty() && ! d[15].isEmpty()) {
             D.add("G61" + ed + "SD" + ed + d[16] + ed + "TE" + ed + d[15]);
             }
             
             } else {
             D.add("S5" + ed + String.valueOf(z) + ed + d[1]);
             D.add("G62" + ed + "70" + ed + d[4] + ed + "G" + ed + d[7] + ed + "LT"); // delvdate if z <> 1...i.e. all other records are delivery dates    
             D.add("N1" + ed + "ST" + ed + d[9] + ed + "92" + ed + d[8]);
             D.add("N2" + ed + d[9]);
             D.add("N3" + ed + d[10]);
             D.add("N4" + ed + d[12] + ed + d[13] + ed + d[14]);
             if (! d[16].isEmpty() && ! d[15].isEmpty()) {
             D.add("G61" + ed + "SD" + ed + d[16] + ed + "TE" + ed + d[15]);
             }
             D.add("OID" + ed + d[2] +  ed + d[3]);
             D.add("L5" + ed + ed + "TBD");
             D.add("AT8" + ed + "G" + ed + "L" + ed + d[20]);
             }
         }  
         
        
         
         
        
        T.add("L3" + ed + String.valueOf(totwt) + ed + "G");
         
            
       // Package it      
    packagePayLoad(c);
    
    // Write to outfile
    edi.writeFile(content, "", outfile);  // you can override output directory by assign 2nd parameter here instead of ""
          
    return c;  
}

}
