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

import com.blueseer.ctr.cusData;
import java.util.ArrayList;
import com.blueseer.edi.EDI;
import com.blueseer.shp.shpData;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.OVData;
import java.io.IOException;
import java.text.DecimalFormat;


/**
 *
 * @author vaughnte
 */
public class Generic810o extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException {
     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
        
     // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
    setOutPutEnvelopeStrings(c);
        
         String[] h = shpData.getShipperHeader(key);  // 13 elements...see declaration
        
         // header
        
         H.add("BIG" + ed +  h[5].replace("-", "") + ed + key + ed + h[4].replace("-", "") +  ed + h[3]);
         H.add("REF" + ed + "ST" + ed + h[1]);
         H.add("N1" + ed + "RE" + ed + OVData.getDefaultSiteName() + ed + "92" + ed + h[1]);
         H.add("DTM" + ed + "011" + ed + h[5].replace("-", ""));
         
               
        // detail
         int i = 0;
         int sumqty = 0;
         double sumamt = 0;
         double sumlistamt = 0;
         
        
         double sumamtTDS = 0;
         String sku = "";
         
      
         // part, custpart, qty, po, cumqty, listprice, netprice, reference, sku
         ArrayList<String[]> lines = shpData.getShipperLines(key);
              for (String[] d : lines) {
                  
                  if (d[8].isEmpty() && d[8] != null) {
                      sku = cusData.getCustAltItem(h[0], d[0]);
                  }
                                    
                  sumqty = sumqty + Integer.valueOf(d[2]);
                  sumamt = sumamt + (bsParseDouble(d[2]) * bsParseDouble(d[6]));
                  sumlistamt = sumlistamt + (bsParseDouble(d[2]) * bsParseDouble(d[5]));
                  D.add("IT1" + ed  +  ed + d[2] + ed + "EA" + ed + currformatDouble(bsParseDouble(d[5])) + ed + ed + "IN" + ed + sku + ed + "VP" + ed + d[1]);
                  i++;
              }
            sumamtTDS = (sumamt * 100);
            
            // trailer
         
          T.add("TDS" + ed + currformatDouble(sumamtTDS));
          T.add("ISS" + ed + String.valueOf(sumqty) + ed + "EA" + ed + String.valueOf(sumqty) + ed + "LB");
          T.add("CTT" + ed + String.valueOf(i));
   
    // Package it      
    
    
    
    return packagePayLoad(c);
}

    
}
