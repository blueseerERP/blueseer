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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author vaughnte
 */
public class Generic850toIDOC extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException  {
    // com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
     
    setOutPutFileType("FF");
    setOutPutDocType("850IDOC");
    OSF = loadSDF("c:\\junk\\ORDERS05.csv"); 
    
    
   
    setControl(c); // as defined by EDI.initEDIControl() and EDIMap.setControl()
    setISA(c[13].toString().split(EDI.escapeDelimiter(ed), -1));  // EDIMap.setISA
    setGS(c[14].toString().split(EDI.escapeDelimiter(ed), -1));   // EDIMap.setGS
      
    
    // set some global variables if necessary
    String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String mandt = "110";
    String docnum = String.format("%016d",Integer.valueOf(c[4]));
    String fob = "";
    String item = "";
    String custitem = "";
    int segnum = 0;
    int psgnum = 0;
    int hlevel = 0;
    int N1Loop = 0;
    int DTMLoop = 0;
    int PO1Loop = 0;
    ArrayList<String[]> addr = new ArrayList<String[]>();
    String addrtype, addrcode, name, line1, line2, city, state, zip, country;
    addrtype = addrcode = name = line1 = line2 = city = state = zip = country = "";
     for (Object seg : doc) {  // loop through all segments in the input (top down)
        
           // create array of each segment per input delimiter used  
           String x[] = seg.toString().split(EDI.escapeDelimiter(ed), -1);
           

           // skip envelope segments ISA, GS, GE, IEA
           if (EDI.isEnvelopeSegment(x[0].toString())) { 
           continue;
           }
           
           if (x[0].toString().equals("BEG")) { 
               mapSegment("EDI_DC","tabnam","40_U");
               mapSegment("EDI_DC","mandt",mandt);
               mapSegment("EDI_DC","docnum",docnum);
               mapSegment("EDI_DC","mestyp","ORDERS");
               mapSegment("EDI_DC","sndpor","SAPPEN");
               mapSegment("EDI_DC","sndprt","LS");
               mapSegment("EDI_DC","sndprn","ACME");
               mapSegment("EDI_DC","idoctyp","ORDERS05");
               mapSegment("EDI_DC","rcvpor","EDI");
               mapSegment("EDI_DC","rcvprt","LI");
               mapSegment("EDI_DC","rcvpfc","LF");
               mapSegment("EDI_DC","credat",now.substring(0,8));
               mapSegment("EDI_DC","cretim",now.substring(8,14));
               commitSegment("EDI_DC",1);
               
               segnum++;
               hlevel++;
               mapSegment("E2EDK01","mandt",mandt);
               mapSegment("E2EDK01","docnum",docnum);
               mapSegment("EDEDK01", "segnum", String.format("%06d",segnum));
               mapSegment("EDEDK01", "psgnum", String.format("%06d",psgnum));
               mapSegment("EDEDK01", "hlevel", String.format("%02d",hlevel));
               mapSegment("E2EDK01","curcy","USD");
               mapSegment("E2EDK01","hwaer","USD");
               mapSegment("E2EDK01","belnr",x[3]);
               commitSegment("E2EDK01",1);
               hlevel++;
           } // if BEG segment
           
           if (x[0].toString().equals("FOB")) { 
              if (x.length > 3) {
                  fob = x[3];
              }
           }
           
           if (x[0].toString().equals("N1")) { 
              N1Loop++;
              segnum++;
              addrtype = x[1];
              addrcode = x[4];
              name = x[2];
              mapSegment("E2EDK14","mandt",mandt);
              mapSegment("E2EDK14","docnum",docnum);
              mapSegment("E2EDK14", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDK14", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDK14", "hlevel", String.format("%02d",hlevel));
              mapSegment("E2EDK14","qualf",x[1]);
              mapSegment("E2EDK14","orgid",x[4]);
              commitSegment("E2EDK14",N1Loop);
           } // N1 segment
           
           if (x[0].toString().equals("N3")) { 
              line1 = x[1];
              if (x.length > 2) {
                  line2 = x[2];
              }
           }
           if (x[0].toString().equals("N4")) { 
               city = x[1];
               state = x[2];
               zip = x[3];
               if (x.length > 4) {
                  country = x[4];
               }
               addr.add(new String[]{addrtype, addrcode, name, line1, line2, city, state, zip, country});
               // reset string variables for addresses for next N1 .. N4 loop
               addrtype = addrcode = name = line1 = line2 = city = state = zip = country = "";
           }
           
           if (x[0].toString().equals("DTM")) { 
              DTMLoop++;
              segnum++;
              mapSegment("E2EDK03","mandt",mandt);
              mapSegment("E2EDK03","docnum",docnum);
              mapSegment("E2EDK03", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDK03", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDK03", "hlevel", String.format("%02d",hlevel));
              mapSegment("E2EDK03","iddat",x[1]);
              mapSegment("E2EDK03","datum",x[2]);
              commitSegment("E2EDK03",DTMLoop);
              
             
              
           } // DTM segment
           
           if (x[0].toString().equals("PO1") && PO1Loop == 0) { 
               
              // now fire all header segments
               // Now loop through addresses and create E2EDKA1
              int addrloop = 0;
              for (String[] s : addr) {
                  addrloop++;
                  segnum++;
                  mapSegment("E2EDKA1","mandt",mandt);
                  mapSegment("E2EDKA1","docnum",docnum);
                  mapSegment("E2EDKA1", "segnum", String.format("%06d",segnum));
                  mapSegment("E2EDKA1", "psgnum", String.format("%06d",psgnum));
                  mapSegment("E2EDKA1", "hlevel", String.format("%02d",hlevel));
                  mapSegment("E2EDKA1","parvw",s[0]);
                  mapSegment("E2EDKA1","lifnr",s[1]);
                  mapSegment("E2EDKA1","name1",s[2]);
                  mapSegment("E2EDKA1","stras",s[3]);
                  mapSegment("E2EDKA1","stras2",s[4]);
                  mapSegment("E2EDKA1","ort01",s[5]);
                  mapSegment("E2EDKA1","regio",s[6]);
                  mapSegment("E2EDKA1","pstlz",s[7]);
                  mapSegment("E2EDKA1","isoal",s[8]);
                  commitSegment("E2EDKA1",addrloop);
              }
              
              // fire E2EDK17
              mapSegment("E2EDK17","mandt",mandt);
              mapSegment("E2EDK17","docnum",docnum);
              mapSegment("E2EDK17", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDK17", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDK17", "hlevel", String.format("%02d",hlevel));
              mapSegment("E2EDK17","qualf","001");
              mapSegment("E2EDK17","lkond","ZZ");
              mapSegment("E2EDK17","lktxt",fob);
              commitSegment("E2EDK17",1);
              
              
              // comments // E2EDKT1, E2EDKT2 (hard coded source)
              mapSegment("E2EDKT1","mandt",mandt);
              mapSegment("E2EDKT1","docnum",docnum);
              mapSegment("E2EDKT1", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDKT1", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDKT1", "hlevel", String.format("%02d",hlevel));
              mapSegment("E2EDKT1","tdid","0001");
              mapSegment("E2EDKT1","tsspras_iso","EN");
              commitSegment("E2EDKT1",1);
              
              for (int commentLoop = 1 ; commentLoop < 4; commentLoop++) {
              mapSegment("E2EDKT2","mandt",mandt);
              mapSegment("E2EDKT2","docnum",docnum);
              mapSegment("E2EDKT2", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDKT2", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDKT2", "hlevel", String.format("%02d",hlevel));
              mapSegment("E2EDKT2","tdline","This is comment number: " + commentLoop);
              commitSegment("E2EDKT2", commentLoop);
              }
              
              
              
              
              
              
           }
           
           if (x[0].toString().equals("PO1")) { 
               PO1Loop++;
               item = x[7];
               custitem = x[9];
               
              mapSegment("E2EDP01","mandt",mandt);
              mapSegment("E2EDP01","docnum",docnum);
              mapSegment("E2EDP01", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDP01", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDP01", "hlevel", String.format("%02d",hlevel));
              mapSegment("E2EDP01", "posex", String.format("%06d",PO1Loop));
              mapSegment("E2EDP01","menge",x[2]);
              mapSegment("E2EDP01","menee",x[3]);
              mapSegment("E2EDP01","pmene","EA");
              mapSegment("E2EDP01","vprei",x[4]);
              mapSegment("E2EDP01","matnr",x[7]);
              mapSegment("E2EDP01","matnr_external",x[9]);
              commitSegment("E2EDP01",PO1Loop);
              
              mapSegment("E2EDP02","mandt",mandt);
              mapSegment("E2EDP02","docnum",docnum);
              mapSegment("E2EDP02", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDP02", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDP02", "hlevel", String.format("%02d",hlevel));
              mapSegment("E2EDP02", "qualf", "001");
              mapSegment("E2EDP02","belnr",x[7]);
              commitSegment("E2EDP02",PO1Loop);
              
              mapSegment("E2EDP20","mandt",mandt);
              mapSegment("E2EDP20","docnum",docnum);
              mapSegment("E2EDP20", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDP20", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDP20", "hlevel", String.format("%02d",hlevel));
              mapSegment("E2EDP20", "wmeng", x[2]);
              commitSegment("E2EDP20",PO1Loop);
              
              
              
              
           }
           
           if (x[0].toString().equals("PID")) { 
              for (int i = 0; i < 2; i++) {
              mapSegment("E2EDP19","mandt",mandt);
              mapSegment("E2EDP19","docnum",docnum);
              mapSegment("E2EDP19", "segnum", String.format("%06d",segnum));
              mapSegment("E2EDP19", "psgnum", String.format("%06d",psgnum));
              mapSegment("E2EDP19", "hlevel", String.format("%02d",hlevel));
              if (i == 0) {
              mapSegment("E2EDP19", "qualf", "001");
              mapSegment("E2EDP19", "idtnr", item);
              } else {
              mapSegment("E2EDP19", "qualf", "002");
              mapSegment("E2EDP19", "idtnr", custitem);    
              }
              mapSegment("E2EDP19", "ktext", x[5]);
              commitSegment("E2EDP19",PO1Loop + i);
              }
           }
           
     } // end of inbound segments
         
        
    return packagePayLoad(c);
}

    
}
