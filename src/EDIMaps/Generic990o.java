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
import com.blueseer.edi.EDI;
import com.blueseer.edi.EDI.*;
import com.blueseer.utl.BlueSeerUtils;


/**
 *
 * @author vaughnte
 */
public class Generic990o {
    
    public static String[] Mapdata(String[] control, String nbr, String tp, String response) throws IOException {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = "990";
        
     
     DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
     Date now = new Date();
       
       // get delimiters for this trading partner, doctype, docdirection
        String[] delimiters = OVData.getDelimiters(tp, doctype);
        String dir = OVData.getEDICustDir(tp, doctype);
         String sd = delimiters[0];
         String ed = delimiters[1];
         String ud = delimiters[2];
         
         int i = 0;
         int segcount = 0;
         int hdrsegcount = 0;
         int detsegcount = 0;
         
         // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber)
         String[] envelope = EDI.generateEnvelope(tp, doctype);
         String ISA = envelope[0];
         String GS = envelope[1];
         String GE = envelope[2];
         String IEA = envelope[3];
         String filename = envelope[4];
         String isactrl = envelope[5];
         String gsctrl = envelope[6];
         String stctrl = "0001"; // String.format("%09d", gsctrl);
        
         // assign missing pieces of control (filename, isactrl, gsctrl, stctrl) which are characteristic of ALL outbound documents.  This must be done for ALL outbound maps
        control[3] = filename;
        control[4] = isactrl;
        control[5] = gsctrl;
        control[6] = stctrl;
        
         
         // now lets get order header info 
         // fonbr, ref, site, wh, date, remarks, carrier, carrier_assigned, reasoncode, custfo, type
         String[] h = OVData.getFreightOrderHeaderArray(nbr);
         
       
        
        
        String ST = "ST" + ed + doctype + ed + stctrl + sd;
        hdrsegcount = 2; // "ST and SE" included
         
         String Header = "";
         String yesno = "";
         if (response.equals("Accepted")) {
             yesno = "A";
         } else {
             yesno = "E";
         }
         ArrayList<String> S = new ArrayList();
         S.add("B1" + ed + h[7] + ed + nbr + ed + dfdate.format(now) + ed + yesno + ed + ed + h[8]);
         S.add("L11" + ed + h[9] + ed + "DO" + ed + h[1]);
         hdrsegcount += 2;
         
         for (String s : S) {
             Header += (EDI.trimSegment(s, ed).toUpperCase() + sd);
         }
         
         
            
         segcount = hdrsegcount;
         
         String SE = "SE" + ed + String.valueOf(segcount) + ed + "0001" + sd;
                         
         
                 
                 // concat and send content to edi.writeFile
                 String content = ISA + GS + ST + Header + SE + GE + IEA;
                 edi.writeFile(content, dir, filename); 
                 
        /*
         output.write(ISA);
         output.write(GS);
         output.write(ST);
         output.write(Header);
         output.write(Detail);
         output.write(Trailer);
         output.close();
 */
                 return control; 
}

}
