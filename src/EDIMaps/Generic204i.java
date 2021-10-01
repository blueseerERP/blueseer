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

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDI.*;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import static com.blueseer.utl.BlueSeerUtils.isSet;
import com.blueseer.edi.EDI;
import bsmf.MainFrame;
import static com.blueseer.edi.EDIMap.ed;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.writeEDILog;
import java.io.IOException;


/**
 *
 * @author vaughnte
 */
public class Generic204i extends com.blueseer.edi.EDIMap {
    
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
    String custfo = ""; 
    String origfo = "";
    String purpose = "";
    int S5Count = 0;
    int linecount = 0;
    boolean S5 = false;
    boolean error = false;
    
        // set edi doc class (for loading into BlueSeer)
         edi204i e = null;   
        
         
         // MAP  ...this is the MAP section  Note:  Outbound looping is driven by Inbound assignments and conditional logic (if user defines)
     // All non-envelope segments are constructed here and assigned to one of three arrays H = Header, D = Detail, T = Trailer
         for (Object seg : doc) {
            String elementArray[] = seg.toString().split(EDI.escapeDelimiter(ed), -1);
            String segment = elementArray[0]; 
           
         
             switch (segment) {
               
               case "ST" :
                   e = new edi204i(isa[6].trim(), isa[8].trim(), gs[2], gs[3], c[4], isa[9], c[1], c[6]);
                   break;
               
               case "B2" :
                   custfo = elementArray[4];
                   e.setCustFO(custfo);
                   e.setCarrier(elementArray[2]);
                   // lets set tpid and cust at this point with ISA sender ID and cross reference lookup into cmedi_mstr
                   e.setTPID(isa[6].trim()); 
                   e.setCust(EDData.getEDICustFromSenderISA(isa[6].trim(), "204", ""));
                   break;
               
               case "B2A" :
                   purpose = elementArray[1];
               
                    // if cancellation...cancel original freight order based on custfo number...if status is not 'InTransit'
                   if (purpose.equals("01")) {
                   EDData.CancelFOFrom204i(custfo);
                   EDData.writeEDILog(c, "INFO", "204 Cancel");
                   break;
                   }
               
                   if (purpose.equals("04")) {
                   origfo = OVData.getFreightOrderNbrFromCustFO(custfo);
                      if (origfo.isEmpty()) {
                          EDData.writeEDILog(c, "ERROR", "204 Update Orig Not Found");
                      }
                   EDData.writeEDILog(c, "INFO", "204 Update Not Implemented");
                   }
                   break;    
               
               case "L11" :
                   if (elementArray[2].toUpperCase().equals("ZI")) {
                       e.setRef(elementArray[1]);
                   }
                   break;    
                   
               case "S5" :
                   S5 = true;  
                   S5Count++;
                   linecount = S5Count - 1; // for base zero array
                   String[] a = new String[e.DetFieldsCount204i];
                   e.detailArray.add(e.initDetailArray(a)); 
                   e.setDetLine(linecount, elementArray[1]);
                   e.setDetType(linecount, elementArray[2]);
                  /*
                   e.setDetWeight(linecount, elementArray[3]);
                   e.setDetWeightUOM(linecount, elementArray[4]);
                   e.setDetBoxes(linecount, elementArray[5]);
                    if ( elementArray[6].toString().equals("PL")  ) {
                    e.setDetUnits(linecount, elementArray[5]);
                    }
                    */
                   break;    
                  
               case "G62" :
                   if ( elementArray[1].toString().equals("68") || elementArray[1].toString().equals("70")) {
                   e.setDetDelvDate(linecount, convertDateFormat("yyyyMMdd", elementArray[2]));
                   if (isSet(elementArray,4)) {
                      e.setDetDelvTime(linecount, elementArray[4]);
                      }
                   } 
                   if ( elementArray[1].toString().equals("69") || elementArray[1].toString().equals("78")) {
                       e.setDetShipDate(linecount, convertDateFormat("yyyyMMdd", elementArray[2]));
                       if (isSet(elementArray,4)) {
                          e.setDetShipTime(linecount, elementArray[4]);
                          }
                   } 
                   break;    
                   
               case "N1" :
                    e.setDetAddrName(linecount, elementArray[2]);
                    if (isSet(elementArray,4)) {
                      e.setDetAddrCode(linecount, elementArray[4]);
                    }
                   break;        
              
               case "N3" :
                     e.setDetAddrLine1(linecount, elementArray[1]);
                   break;   
                   
               case "N4" :
                    e.setDetAddrCity(linecount, elementArray[1]);
                    e.setDetAddrState(linecount, elementArray[2]);
                    e.setDetAddrZip(linecount, elementArray[3]);
                   break;        
               
               case "61" :
                    e.setDetAddrContact(linecount, elementArray[2]);
                    e.setDetAddrPhone(linecount, elementArray[4]);
                   break; 
               
               case "L3" :
                   e.setWeight(elementArray[1]);
                   break;    
                   
               default :
                   break;
           } // end switch
            
           
         } // for each seg
         
         
         
         /* Load Freight Order unless cancellation....cancellation is handled above */
         if (! error &&  purpose.equals("00")) {
             com.blueseer.edi.EDI.createFOMSTRFrom204i(e, c);
         }
         
         if (! error &&  purpose.equals("04")) {
             // blueseer.EDI.updateFOMSTRFrom204i(e, control);   not yet implemented
         }
        
      return c;
    }

 
 
}


