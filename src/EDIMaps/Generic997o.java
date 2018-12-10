/*
   Copyright 2005-2017 Terry Evans Vaughn ("VCSCode").

With regard to the Blueseer Software:

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

For all third party components incorporated into the GitLab Software, those 
components are licensed under the original license provided by the owner of the 
applicable component.

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
public class Generic997o {
    // ArrayList doc, String flddelim, String subdelim, String control
    public static String[] Mapdata(ArrayList<String> docs, String[] control, String gstype, String gsctrlnbr, String sttype, String isaid, String gsid, String isaq, String bsisaq, String bsisa, String bsgs, String ver) throws IOException {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = "997";
        
     
     DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
     Date now = new Date();
       
       
         String sd = "\n";
         String ed = "*";
         String ud = ">";
         
         int i = 0;
         int segcount = 0;
         int hdrsegcount = 0;
         int detsegcount = 0;
         
         // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber)
         String[] envelope = EDI.generate997Envelope(isaq, isaid, gsid, bsisaq, bsisa, bsgs, ver);
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
        
         
                
        
        
        String ST = "ST" + ed + doctype + ed + stctrl + sd;
        hdrsegcount = 2; // "ST and SE" included
         
         String Header = "";
       
         ArrayList<String> S = new ArrayList();
         S.add("AK1" + ed + gstype + ed + gsctrlnbr);
         hdrsegcount += 1;
         for (String d : docs) {
             S.add("AK2" + ed + sttype + ed + d);
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
                 return control; 
}

}
