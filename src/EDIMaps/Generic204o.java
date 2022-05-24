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

package EDIMaps;

import java.io.IOException;
import java.util.ArrayList;
import com.blueseer.utl.OVData;


/**
 *
 * @author vaughnte
 */
public class Generic204o extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException, UserDefinedException {
     String key = doc.get(0).toString(); 
        
     // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
  
        
         
        
         // now lets get order header info 
         // fonbr, ref, site, wh, date, remarks, carrier, carrier_assigned
         String[] h = OVData.getFreightOrderHeaderArray(key);
         
         // now detail
         // fod_line, fod_type, fod_shipper, fod_ref, fod_shipdate, fod_shiptime, fod_delvdate, fod_delvtime, fod_code, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip ...
         // fod_phone, fod_contact, fod_remarks, fod_pallets, fod_boxes, fod_weight, fod_wt_uom 
         // 20 elements
        ArrayList<String[]> dt = OVData.getFreightOrderDetailArray(key);
    
        mapSegment("B2","e01","");
        mapSegment("B2","e02",h[7]);
        mapSegment("B2","e03",h[0]);
        mapSegment("B2","e06","PP");
        commitSegment("B2");
        
        mapSegment("B2A","e01","00");
        mapSegment("B2A","e02","LT");
        commitSegment("B2A");
         
         
        if (! h[1].isEmpty())  {
          mapSegment("L11","e01",h[1]);
          mapSegment("L11","e02","OQ");
          commitSegment("L11");
        }
         
        if (! h[5].isEmpty()) {
            mapSegment("NTE","e01","");
            mapSegment("NTE","e02",h[5]);
            commitSegment("NTE");
        }
        
         
         int z = 0;
         int totqty = 0;
         double totwt = 0.00;
         for (String[] d : dt) {
             z++;
            
             
            if (d[1].equals("LD")) {   // there should always ONLY BE 1 LD
               totqty = Integer.valueOf(d[18]);
               totwt = Double.valueOf(d[20]);
            mapSegment("S5","e01",String.valueOf(z));
            mapSegment("S5","e02",d[1]);
            commitSegment("S5");
            
            mapSegment("G62","e01","69");
            mapSegment("G62","e02",d[4]);
            mapSegment("G62","e03","EP");
            mapSegment("G62","e04",d[5]);
            mapSegment("G62","e05","LT");
            commitSegment("G62");
            
            mapSegment("N1","e01","SF");
            mapSegment("N1","e02",d[9]);
            mapSegment("N1","e03","92");
            mapSegment("N1","e04",d[8]);
            commitSegment("N1");
            
            mapSegment("N2","e01",d[9]);
            commitSegment("N2");
            
            mapSegment("N3","e01",d[10]);
            commitSegment("N3");
           
            mapSegment("N4","e01",d[12]);
            mapSegment("N4","e02",d[13]);
            mapSegment("N4","e03",d[14]);
            commitSegment("N4");
            
             
            if (! d[16].isEmpty() && ! d[15].isEmpty()) {
            mapSegment("G61","e01","SD");
            mapSegment("G61","e02",d[16]);
            mapSegment("G61","e03","TE");
            mapSegment("G61","e04",d[15]);
            commitSegment("G61");
            }
             
            } else {
            mapSegment("S5","e01",String.valueOf(z));
            mapSegment("S5","e02",d[1]);
            commitSegment("S5");
            
            if (! d[16].isEmpty() && ! d[15].isEmpty()) {
            mapSegment("G62","e01","70");
            mapSegment("G62","e02",d[4]);
            mapSegment("G62","e03","G");
            mapSegment("G62","e04",d[7]);
            mapSegment("G62","e05","LT");
            commitSegment("G62");
            }
            
            mapSegment("N1","e01","ST");
            mapSegment("N1","e02",d[9]);
            mapSegment("N1","e03","92");
            mapSegment("N1","e04",d[8]);
            commitSegment("N1");
            
            mapSegment("N2","e01",d[9]);
            commitSegment("N2");
            
            mapSegment("N3","e01",d[10]);
            commitSegment("N3");
           
            mapSegment("N4","e01",d[12]);
            mapSegment("N4","e02",d[13]);
            mapSegment("N4","e03",d[14]);
            commitSegment("N4");
            
            
             if (! d[16].isEmpty() && ! d[15].isEmpty()) {
             mapSegment("G61","e01","SD");
             mapSegment("G61","e02",d[16]);
             mapSegment("G61","e03","TE");
             mapSegment("G61","e04",d[15]);
             commitSegment("G61");
             }
             
             mapSegment("OID","e01",d[2]);
             mapSegment("OID","e02",d[3]);
             commitSegment("OID");
             
             mapSegment("L5","e01","");
             mapSegment("L5","e02","TBD");
             commitSegment("L5");
             
             mapSegment("AT8","e01","G");
             mapSegment("AT8","e02","L");
             mapSegment("AT8","e03",d[20]);
             commitSegment("AT8");
             
             
            }
         }  
         
        
         mapSegment("L3","e01",String.valueOf(totwt));
         mapSegment("L3","e02","G");
         commitSegment("L3");
         
            
       // Package it      
    return packagePayLoad(c);
   
}

}
