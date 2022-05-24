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
import com.blueseer.pur.purData;
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
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException, UserDefinedException {
     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
    
        
     // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
   // setOutPutEnvelopeStrings(c);
        
    String[] h = purData.getPOMstrHeaderEDI(key);  // 13 elements...see declaration
    // po, vend, ship, site, type, orddate, duedate, shipvia, rmks, cur
    
     /* Begin Mapping Segments */ 
    mapSegment("BEG","e01","00");
    mapSegment("BEG","e02","NE");
    mapSegment("BEG","e03",key);
    mapSegment("BEG","e05",h[5].replace("-", ""));
    commitSegment("BEG");
    
    mapSegment("REF","e01","ST");
    mapSegment("REF","e02",h[3]);
    commitSegment("REF");
    
    mapSegment("N1","e01","ST");
    mapSegment("N1","e02",OVData.getDefaultSiteName());
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",h[3]);
    commitSegment("N1");
    
    mapSegment("DTM","e01","002");
    mapSegment("DTM","e02",h[6].replace("-", ""));
    commitSegment("DTM");    
    
               
        // detail
         int i = 0;
         int sumqty = 0;
         double sumamt = 0;
         // line, item, venditem, qty, price, uom, desc
         ArrayList<String[]> lines = purData.getPOMstrdetailsEDI(key);
              for (String[] d : lines) {
                  i++;
                                    
                  sumqty = sumqty + Integer.valueOf(d[3]);
                  sumamt = sumamt + (bsParseDouble(d[3]) * bsParseDouble(d[4]));
                  
                mapSegment("PO1","e01",d[0]);
                mapSegment("PO1","e02",d[3]);
                mapSegment("PO1","e03",d[5]);
                mapSegment("PO1","e04",currformatDouble(bsParseDouble(d[4])));
                mapSegment("PO1","e06","BP");
                mapSegment("PO1","e07",d[1]);
                if (! d[2].isEmpty()) {
                mapSegment("PO1","e08","VP");
                mapSegment("PO1","e09",d[2]);
                }
                commitSegment("PO1");
                
                mapSegment("PID","e01","F");
                mapSegment("PID","e05",d[6]);
                commitSegment("PID");
              }
         
         mapSegment("CTT","e01",String.valueOf(i));
         commitSegment("CTT");
        
   
    // Package it      
    
    
    
    return packagePayLoad(c);
}

    
}
