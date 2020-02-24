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
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import static com.blueseer.utl.BlueSeerUtils.isSet;
import com.blueseer.edi.EDI;
import bsmf.MainFrame;
import static com.blueseer.utl.OVData.writeEDILog;
import java.io.IOException;


/**
 *
 * @author vaughnte
 */
public class Generic214i extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException {
     
   
    
         com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
    
    // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    
    // case inbound
    // set envelope segments
    String[] isa = c[13].split(EDI.escapeDelimiter(ed), -1);
    String[] gs = c[14].split(EDI.escapeDelimiter(ed), -1);
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
    // Only for Outbound!!!
    // setOutPutEnvelopeStrings(c);
      
    // set temp variables
    String order = ""; 
    boolean error = false;
    
        // set edi doc class (for loading into BlueSeer)
         edi214 e = null;   
        
         
         // MAP  ...this is the MAP section  Note:  Outbound looping is driven by Inbound assignments and conditional logic (if user defines)
     // All non-envelope segments are constructed here and assigned to one of three arrays H = Header, D = Detail, T = Trailer
         for (Object seg : doc) {
            String elementArray[] = seg.toString().split(EDI.escapeDelimiter(ed), -1);
            String segment = elementArray[0]; 
           
           
           switch (segment) {
               
               case "ST" :
                   e = new edi214(isa[6].trim(), isa[8].trim(), gs[2], gs[3], c[4], isa[9], c[1], c[6]);
                   break;
               
               case "B10" :
                   e.setOrder(elementArray[2]);  
                   order = elementArray[2];
                   e.setProNbr(elementArray[1]);
                   break;
                   
               case "AT7" :
                   e.setStatus(elementArray[1]);
                   e.setRemarks(elementArray[2]);
                   if (isSet(elementArray, 5)) {
                    e.setApptDate(elementArray[5]);
                   }
                   if (isSet(elementArray, 6)) {
                    e.setApptTime(elementArray[6]);
                   }
                   break;
                
               case "MS1" :
                   e.setLat(elementArray[4]); 
                   e.setLon(elementArray[5]); 
                   break;
                   
                   
               case "MS2" :
                   e.setSCAC(elementArray[1]);
                   e.setEquipmentNbr(elementArray[2]); 
                   e.setEquipmentType(elementArray[3]); 
                   break;
                   
               default :
                   break;
           } // end switch
            
           
         } // end for each doc
         
         
          if (order.isEmpty()) {
               writeEDILog(c, "0", "ERROR", "No Freight Order found for this 214 in the B10 Segment");
               error = true;
          }
          
          if (! order.isEmpty()) {
               if (! OVData.isValidFreightOrderNbr(order)) {
               writeEDILog(c, "0", "ERROR", "Invalid Freight Order found for this 214 in the B10 Segment");
               error = true;
               }
          }
         
         
         /* Load Shipper */
         if (! error) {
             com.blueseer.edi.EDI.createFOTDETFrom214(e, c); 
         }
         
        return c;
      
    }

 
 
}


