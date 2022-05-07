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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.blueseer.utl.OVData;


/**
 *
 * @author vaughnte
 */
public class Generic990o extends com.blueseer.edi.EDIMap {
    
     public String[] Mapdata(ArrayList doc, String[] c) throws IOException {
     String key = doc.get(0).toString(); 
        
        // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
        setControl(c);    
  
        
         
         
         // now lets get order header info 
         // fonbr, ref, site, wh, date, remarks, carrier, carrier_assigned, reasoncode, custfo, type
        String[] h = OVData.getFreightOrderHeaderArray(key);
        String status = "";
        DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        
        if (h[11].equals("Accepted")) {
             status = "A";
         } else {
             status = "E";
         }
        
        mapSegment("B1","e01",h[7]);
        mapSegment("B1","e02",key);
        mapSegment("B1","e03",dfdate.format(now));
        mapSegment("B1","e04",status);
        mapSegment("B1","e06",h[8]);
        commitSegment("B1");
        
        mapSegment("L11","e01",h[9]);
        mapSegment("L11","e02","DO");
        mapSegment("L11","e03",h[1]);
        commitSegment("L11");
        
        
      // Package it      
    return packagePayLoad(c);
}

}
