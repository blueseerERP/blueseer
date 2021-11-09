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
import com.blueseer.edi.EDI.*;
import com.blueseer.shp.shpData;
import com.blueseer.utl.BlueSeerUtils;


/**
 *
 * @author vaughnte
 */
public class Generic856o extends com.blueseer.edi.EDIMap {
    
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
        
         H.add("BSN" + ed + "00" + ed +  key + ed + h[5] + ed + "1200");
         H.add("DTM" + ed + "011" + ed + h[4] + ed + "1200");
         H.add("DTM" + ed + "067" + ed + h[4] + ed + "1200");
         H.add("HL" + ed + "1" + ed + ed + "S" + ed + "1");
         H.add("TD5" + ed + "B" + ed + "2" + ed + h[8] + ed + "ZZ" + ed + h[8]);
         H.add("REF" + ed + "CN" + h[10]);
         H.add("N1" + ed + "ST" + ed + cusData.getCustName(h[0]) + ed + "92" + h[1]);
         H.add("N1" + ed + "SF" + ed + OVData.getDefaultSiteName() + ed + "92" + h[12]);
         H.add("HL" + ed + "2" + ed + "1" + ed + "O" + ed + "1");
         H.add("PRF" + ed + h[3] + ed + "00" + ed + ed + h[4]);
        
         int i = 0;
         int sumqty = 0;
         // part, custpart, qty, po, cumqty, listprice, netprice, reference
         ArrayList<String[]> lines = shpData.getShipperLines(key);
        
              for (String[] d : lines) {
                  sumqty = sumqty + Integer.valueOf(d[2]);
                  D.add("HL" + ed  + String.valueOf(i) + ed + "2" + ed + "I" + ed + "1");
                  D.add("LIN" + ed + String.valueOf(i) + ed + "BP" + ed + d[1] + ed + "VP" + ed +  d[0]);
                  D.add("SN1" + ed + String.valueOf(i) + ed + d[2] + ed + "EA" + ed + d[2] + ed + ed + ed + ed + "AC");
                  i++;
              }
        
         
          ArrayList<String> T = new ArrayList();
          T.add("CTT" + ed + String.valueOf(i));
     
        
      
   return packagePayLoad(c);
}

}
