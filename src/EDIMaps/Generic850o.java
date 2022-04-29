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
public class Generic850o extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException {
     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
    
        
     // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
   // setOutPutEnvelopeStrings(c);
        
    String[] h = shpData.getShipperHeader(key);  // 13 elements...see declaration
    
     /* Begin Mapping Segments */ 
    mapSegment("BIG","e01",h[5].replace("-", ""));
    mapSegment("BIG","e02",key);
    mapSegment("BIG","e03",h[4].replace("-", ""));
    mapSegment("BIG","e04",h[3]);
    commitSegment("BIG");
    
    mapSegment("REF","e01","ST");
    mapSegment("REF","e02",h[1]);
    commitSegment("REF");
    
    mapSegment("N1","e01","RE");
    mapSegment("N1","e02",OVData.getDefaultSiteName());
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",h[1]);
    commitSegment("N1");
    
    mapSegment("DTM","e01","011");
    mapSegment("DTM","e02",h[5].replace("-", ""));
    commitSegment("DTM");    
    
               
        // detail
         int i = 0;
         int sumqty = 0;
         double sumamt = 0;
         double sumlistamt = 0;
         double sumamtTDS = 0;
         String sku = "";
         // item, custitem, qty, po, cumqty, listprice, netprice, reference, sku, desc
         ArrayList<String[]> lines = shpData.getShipperLines(key);
              for (String[] d : lines) {
                  i++;
                  if (d[8].isEmpty() && d[8] != null) {
                      sku = cusData.getCustAltItem(h[0], d[0]);
                  }
                                    
                  sumqty = sumqty + Integer.valueOf(d[2]);
                  sumamt = sumamt + (bsParseDouble(d[2]) * bsParseDouble(d[6]));
                  sumlistamt = sumlistamt + (bsParseDouble(d[2]) * bsParseDouble(d[5]));
                  
                mapSegment("IT1","e01",String.valueOf(i));
                mapSegment("IT1","e02",d[2]);
                mapSegment("IT1","e03","EA");
                mapSegment("IT1","e04",currformatDouble(bsParseDouble(d[5])));
                mapSegment("IT1","e06","IN");
                mapSegment("IT1","e07",sku);
                mapSegment("IT1","e08","VP");
                mapSegment("IT1","e09",d[0]);
                commitSegment("IT1");
                  
              }
            sumamtTDS = (sumamt * 100);
            
            // trailer
         mapSegment("TDS","e01",currformatDouble(sumamtTDS));
         commitSegment("TDS");
         
         mapSegment("ISS","e01",String.valueOf(sumqty));
         mapSegment("ISS","e02","EA");
         mapSegment("ISS","e03",String.valueOf(sumqty));
         mapSegment("ISS","e04","LB");
         commitSegment("ISS");
         
         mapSegment("CTT","e01",String.valueOf(i));
         commitSegment("CTT");
        
   
    // Package it      
    
    
    
    return packagePayLoad(c);
}

    
}
