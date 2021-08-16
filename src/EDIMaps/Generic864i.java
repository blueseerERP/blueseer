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

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDI.*;
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import com.blueseer.edi.EDI;
import static com.blueseer.utl.EDData.writeEDILog;


/**
 *
 * @author vaughnte
 */
public class Generic864i extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) {
   
    setControl(c); // as defined by EDI.initEDIControl() and EDIMap.setControl()
    setControlISA(c[13].toString().split(EDI.escapeDelimiter(ed), -1));  // EDIMap.setISA
    setControlGS(c[14].toString().split(EDI.escapeDelimiter(ed), -1));   // EDIMap.setGS
     
        // set mandatory document wide variables
         edi850 e = null;  // mandatory class creation
        boolean error = false;  // set at any time to prevent order creation
        int i = 0;   // detail line counter
        
       
               
        // loop through each segment in the inbound raw 850
         for (Object seg : doc) {
        
             
           // create array of each segment per delimiter used  
           String x[] = seg.toString().split(EDI.escapeDelimiter(ed), -1);
           

           // skip envelope segments ISA, GS, GE, IEA
           if (EDI.isEnvelopeSegment(x[0].toString())) { 
           continue;
           }
           
           
           if (x[0].toString().equals("BMG")) {   
            // do something here  
           } 
           
           if (x[0].toString().equals("MIT")) {
           // do something here
           }
           
           if (x[0].toString().equals("MSG")) {
           // do something here
           }
          
         } // END of segments in 864 Doc
         
         
         
         /* Post Process */
         //if (! error)
         //com.blueseer.edi.EDI.createOrderFrom850(e, c); 
         
         
        return c;
      
    }

 
 
}


