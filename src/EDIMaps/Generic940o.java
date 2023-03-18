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
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import com.blueseer.utl.EDData;


/**
 *
 * @author vaughnte
 */
public class Generic940o {
    
    public static String[] Mapdata(String[] control, String nbr, String entity) throws IOException {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = "940";
        
       
       // get delimiters for this trading partner, doctype, docdirection
        String[] delimiters = EDData.getDelimiters(entity, doctype);
        String dir = cleanDirString(EDData.getEDICustDir(doctype, entity, ""));
         String sd = delimiters[0];
         String ed = delimiters[1];
         String ud = delimiters[2];
         
         int i = 0;
         int segcount = 0;
         
         // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber)
         String[] envelope = EDI.generateEnvelope(doctype, entity, "");
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
         // so_nbr, so_po, so_cust, so_ship, so_due_date, so_shipvia, so_rmks
         String[] h = OVData.getOrderHeaderArray(nbr);
         
         // now detail
         // sod_line, sod_item, sod_custitem, sod_ord_qty, sod_uom, sod_desc, it_net_wt
         ArrayList<String[]> dt = OVData.getOrderDetailArray(nbr);
         
         
         // get Shipto Address (9 elements)  id, name, line1, line2, line3, city, state, zip, country
         String[] st = OVData.getShipToAddressArray(h[2], h[3]);
         
        
        
        
        String ST = "ST" + ed + doctype + ed + stctrl + sd;
        
         
         String Header = "";
         ArrayList<String> S = new ArrayList();
         S.add("W05" + ed + "N" + ed + h[0] + ed + h[1]);
         S.add("N1" + ed + "ST" + ed + st[1] + ed + "92" + ed + st[0]);
         S.add("N3" + ed + st[2]);
         S.add("N4" + ed + st[5] + ed + st[6] + ed + st[7]);
         S.add("N9" + ed + "BR" + ed + h[0]);
         S.add("G62" + ed + "10" + ed + h[4].replace("-", "")); // requested ship date
         S.add("NTE" + ed + "WHI" + ed + h[6]);
         S.add("G66" + ed + "PP" + ed + "M" + ed + ed + ed + h[5]);
         
         for (String s : S) {
             Header += (EDI.trimSegment(s, ed).toUpperCase() + sd);
         }
         
         
         
         String Detail = "";
         ArrayList<String> D = new ArrayList();
         int z = 0;
         int totqty = 0;
         double totwt = 0.00;
         for (String[] d : dt) {
             z++;
             totqty += Integer.valueOf(d[3]);
             totwt += Double.valueOf(d[6]);
             D.add("LX" + ed + String.valueOf(z));
             D.add("W01" + ed + d[3] + ed + d[4] + ed + ed + "VN" + ed + d[1] + ed + "BP" + ed + d[2]);
             if (! d[5].isEmpty())
             D.add("G69" + ed + d[5]);
         }  
         
         for (String d : D) {
             Detail += (EDI.trimSegment(d, ed).toUpperCase() + sd);
         }
         
         
         String Trailer = "";
          ArrayList<String> T = new ArrayList();
          T.add("W76" + ed + String.valueOf(totqty) + ed + String.valueOf(totwt) + ed + "LB");
          
         for (String t : T) {
             Trailer += (EDI.trimSegment(t, ed).toUpperCase() + sd);
         } 
            
         segcount = 11 + (z * 3);
         
         String SE = "SE" + ed + String.valueOf(segcount) + ed + "0001" + sd;
                         
         
                 
                 // concat and send content to edi.writeFile
                 String content = ISA + GS + ST + Header + Detail + Trailer + SE + GE + IEA;
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
