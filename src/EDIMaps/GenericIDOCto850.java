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
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author vaughnte
 */
public class GenericIDOCto850 extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException  {
    // com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
     
    setOutPutFileType("X12");
    setOutPutDocType("850");
  //  OSF = readOSF("c:\\junk\\X12850.csv"); 
  //  IMD = readIMD("c:\\junk\\ORDERS05.csv",doc);
    
    ISF = readISF(c, "c:\\junk\\ORDERS05.csv");
    mappedInput = mapInput(c, doc, ISF);
    OSF = readOSF("c:\\junk\\X12850.csv"); 
    
     // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
    setOutPutEnvelopeStrings(c);
     
    mapSegment("BEG","e01","01");
    mapSegment("BEG","e02",getInput("E2EDK01",16));
    commitSegment("BEG",1);
        
       // now create output records based on header and detail landmarks
       /* 
       for (String s : (ArrayList<String>) doc) {
            if (s.startsWith("E2EDK01")) {
                mapSegment("BAK","e01","01");
                mapSegment("BAK","e02",IMD.get("E2EDK01").get("belnr"));
                commitSegment("BAK",1);
               // System.out.println("YEP:" + IMD.get("E2EDK01").get("belnr"));
            }
        } 
        */
       /*
        for (Map.Entry<String, HashMap<String,String>> z : OMD.entrySet()) {
            if (z.getKey().startsWith("BEG")) {
                HashMap<String,String> h = OMD.get(z.getKey());
                 for (Map.Entry<String, String> me : h.entrySet()) {
                     System.out.println("OMD:" + me.getKey() + "/" + me.getValue());
                 }
            }
            System.out.println("OMD Key:" + z.getKey());
        }
        */
    return packagePayLoad(c);
}

    
}
