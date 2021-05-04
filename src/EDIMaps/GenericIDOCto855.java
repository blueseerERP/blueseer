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

import java.util.ArrayList;
import com.blueseer.edi.EDI;
import com.blueseer.utl.OVData;
import java.io.IOException;
import java.text.DecimalFormat;


/**
 *
 * @author vaughnte
 */
public class GenericIDOCto855 extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c)  {
    // com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
     
    setOutPutFileType("X12");
    setOutPutDocType("855");
     
     // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
    setOutPutEnvelopeStrings(c);
        
       // PRE-loop through IDOC segments and grab relevant values
       String po = "";
       String podate = "";
       for (String s : (ArrayList<String>) doc) {
           if (s.startsWith("E2EDK02") && s.substring(63, 66).equals("001")) {
               po = s.substring(66,88).trim();
               podate = s.substring(107,115).trim();
               
              
           }
       }
        
       // now create output records based on header and detail landmarks
        for (String s : (ArrayList<String>) doc) {
            if (s.startsWith("ZE1EDK")) {
               H.add("BAK" + ed +  "01" + ed + "AC" + ed + po +  ed + podate);
           
            }
        } 
        
        
    return packagePayLoad(c);
}

    
}
