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
import static com.blueseer.edi.EDI.delimConvertIntToStr;
import com.blueseer.utl.BlueSeerUtils;


/**
 *
 * @author vaughnte
 */
public class Generic997o  {
    
    public static String[] Mapdata(ArrayList<String> docs, String[] c) throws IOException {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
        String doctype = "997";
        
       
        
         String[] _isa = c[13].split(EDI.escapeDelimiter(delimConvertIntToStr(c[10])), -1);
         String[] _gs = c[14].split(EDI.escapeDelimiter(delimConvertIntToStr(c[10])), -1);  
     
     DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
     Date now = new Date();
       
       
         String[] defaults = OVData.getEDIOutCustDefaults(_isa[6].trim(), "997", "0");
        
        String sd = "\n";
        String ed = "*";
        String ud = ">";
        if (defaults[7] != null) {
         sd = delimConvertIntToStr(defaults[7]);  // "\n"  or "\u001c"
        }
        if (defaults[6] != null) {
         ed = delimConvertIntToStr(defaults[6]);
        } 
        if (defaults[8] != null) {
         ud = delimConvertIntToStr(defaults[8]);
        }
         
         int i = 0;
         int segcount = 0;
         int hdrsegcount = 0;
         int detsegcount = 0;
         
         // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber)
         String[] envelope = EDI.generate997Envelope(_isa, _gs);
         String ISA = envelope[0];
         String GS = envelope[1];
         String GE = envelope[2];
         String IEA = envelope[3];
         String filename = envelope[4];
         String isactrl = envelope[5];
         String gsctrl = envelope[6];
         String stctrl = "0001"; // String.format("%09d", gsctrl);
        
         // assign missing pieces of control (filename, isactrl, gsctrl, stctrl) which are characteristic of ALL outbound documents.  This must be done for ALL outbound maps
        c[3] = filename;
        c[4] = isactrl;
        c[5] = gsctrl;
        c[6] = stctrl;
        
         
                
        
        
        String ST = "ST" + ed + doctype + ed + stctrl + sd;
        hdrsegcount = 2; // "ST and SE" included
         
         String Header = "";
       
         ArrayList<String> S = new ArrayList();
         S.add("AK1" + ed + _gs[1] + ed + _gs[6]);
         hdrsegcount += 1;
         for (String d : docs) {
             S.add("AK2" + ed + c[1] + ed + d);
             S.add("AK5" + ed + "A");
             hdrsegcount += 2;
         }
         
         S.add("AK9" + ed + "A" + ed + docs.size() + ed + docs.size() + ed + docs.size());
         hdrsegcount += 1;
         
         // now cleanup
         for (String s : S) {
             Header += (EDI.trimSegment(s, ed).toUpperCase() + sd);
         }
         
         
            
         segcount = hdrsegcount;
         
         String SE = "SE" + ed + String.valueOf(segcount) + ed + stctrl + sd;
                         
         
                 
                 // concat and send content to edi.writeFile
                 String content = ISA + GS + ST + Header + SE + GE + IEA;
                 edi.writeFile(content, "", filename); 
                 
        /*
         output.write(ISA);
         output.write(GS);
         output.write(ST);
         output.write(Header);
         output.write(Detail);
         output.write(Trailer);
         output.close();
 */
                 return c; 
}

}
