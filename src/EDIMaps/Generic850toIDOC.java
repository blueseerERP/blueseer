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
    
    // These 6 global variables must be set for all maps    
    setControl(c);    // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    ISF = readISF(c, "c:\\junk\\X12850.csv");
    OSF = readOSF("c:\\junk\\ORDERS05.csv"); 
    setOutPutFileType("FF");  // X12 of FF
    setOutPutDocType("850IDOC");  // 850, 856, 850IDOC, etc
    mappedInput = mapInput(c, doc, ISF);
    
    // set some global variables if necessary
    String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String mandt = "110";
    String docnum = String.format("%016d",Integer.valueOf(c[4]));
    
    int segnum = 0;
    int psgnum = 0;
    int hlevel = 0;
    
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
       mapSegment("E2EDK01","wkurs",getInput("N1","1:ST",4));
       mapSegment("E2EDK01","belnr",getInput("BEG",3));
       commitSegment("E2EDK01",1);


      // E2EDK14 loop
      ArrayList<String> loop = getLoopKeys("N1");
       int j = 0;
       for (String key : loop) {
      j++;    
      segnum++;
      hlevel++;
      mapSegment("E2EDK14","mandt",mandt);
      mapSegment("E2EDK14","docnum",docnum);
      mapSegment("E2EDK14", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDK14", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDK14", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDK14","qualf",getGroupInput(key,1));
      mapSegment("E2EDK14","orgid",getGroupInput(key,4));
      commitSegment("E2EDK14",j);  
       }
              
               
       // DTM Loop
       ArrayList<String> dtmloop = getLoopKeys("DTM");
       int i = 0;
       for (String key : dtmloop) {
          i++;
          segnum++;
          hlevel++;
          mapSegment("E2EDK03","mandt",mandt);
          mapSegment("E2EDK03","docnum",docnum);
          mapSegment("E2EDK03", "segnum", String.format("%06d",segnum));
          mapSegment("E2EDK03", "psgnum", String.format("%06d",psgnum));
          mapSegment("E2EDK03", "hlevel", String.format("%02d",hlevel));
          mapSegment("E2EDK03","iddat",getLoopInput(key,1,i));
          mapSegment("E2EDK03","datum",getLoopInput(key,2,i));
          commitSegment("E2EDK03",i);   
       }
               
       int m = getGroupCount("N1");
       for (j = 1; j <= m; j++) {
          segnum++;
          hlevel++;
          mapSegment("E2EDKA1","mandt",mandt);
          mapSegment("E2EDKA1","docnum",docnum);
          mapSegment("E2EDKA1", "segnum", String.format("%06d",segnum));
          mapSegment("E2EDKA1", "psgnum", String.format("%06d",psgnum));
          mapSegment("E2EDKA1", "hlevel", String.format("%02d",hlevel));
          mapSegment("E2EDKA1","parvw",getInput("N1",1, j));
          mapSegment("E2EDKA1","lifnr",getInput("N1",4, j));
          mapSegment("E2EDKA1","name1",getInput("N1",2, j));
          mapSegment("E2EDKA1","stras",getInput("N1:N3",1, j));
          mapSegment("E2EDKA1","stras2",getInput("N1:N3",2, j));
          mapSegment("E2EDKA1","ort01",getInput("N1:N4",1, j));
          mapSegment("E2EDKA1","regio",getInput("N1:N4",2, j));
          mapSegment("E2EDKA1","pstlz",getInput("N1:N4",3, j));
          mapSegment("E2EDKA1","isoal",getInput("N1:N4",4, j));
          commitSegment("E2EDKA1",j);
      }
      segnum++;
      hlevel++;         
      mapSegment("E2EDK17","mandt",mandt);
      mapSegment("E2EDK17","docnum",docnum);
      mapSegment("E2EDK17", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDK17", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDK17", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDK17","qualf","001");
      mapSegment("E2EDK17","lkond","ZZ");
      mapSegment("E2EDK17","lktxt",getInput("FOB",2));
      commitSegment("E2EDK17",1);


      // comments // E2EDKT1, E2EDKT2 (hard coded source)
      segnum++;
      hlevel++;
      mapSegment("E2EDKT1","mandt",mandt);
      mapSegment("E2EDKT1","docnum",docnum);
      mapSegment("E2EDKT1", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDKT1", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDKT1", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDKT1","tdid","0001");
      mapSegment("E2EDKT1","tsspras_iso","EN");
      commitSegment("E2EDKT1",1);

      ArrayList<String> msg = getLoopKeys("MSG");
       int v = 0;
       for (String key : msg) {
          v++;
          segnum++;
          hlevel++;
          mapSegment("E2EDKT2","mandt",mandt);
          mapSegment("E2EDKT2","docnum",docnum);
          mapSegment("E2EDKT2", "segnum", String.format("%06d",segnum));
          mapSegment("E2EDKT2", "psgnum", String.format("%06d",psgnum));
          mapSegment("E2EDKT2", "hlevel", String.format("%02d",hlevel));
          mapSegment("E2EDKT2","tdline",getLoopInput(key,2,v));
          commitSegment("E2EDKT2", v);  
       } 

    // line items
    int po1count = getGroupCount("PO1");
    for (j = 1; j <= po1count; j++) {
      segnum++;
      hlevel++;  
      mapSegment("E2EDP01","mandt",mandt);
      mapSegment("E2EDP01","docnum",docnum);
      mapSegment("E2EDP01", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDP01", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDP01", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDP01", "posex", String.format("%06d",j));
      mapSegment("E2EDP01","menge",getInput("PO1",2, j));
      mapSegment("E2EDP01","menee",getInput("PO1",3, j));
      mapSegment("E2EDP01","pmene","EA");
      mapSegment("E2EDP01","vprei",getInput("PO1",4, j));
      mapSegment("E2EDP01","matnr",getInput("PO1",7, j));
      mapSegment("E2EDP01","matnr_external",getInput("PO1",9, j));
      commitSegment("E2EDP01",j);

      segnum++;
      hlevel++;
      mapSegment("E2EDP02","mandt",mandt);
      mapSegment("E2EDP02","docnum",docnum);
      mapSegment("E2EDP02", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDP02", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDP02", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDP02", "qualf", "001");
      mapSegment("E2EDP02","belnr",getInput("PO1",7, j));
      commitSegment("E2EDP02",j);

      segnum++;
      hlevel++;
      mapSegment("E2EDP20","mandt",mandt);
      mapSegment("E2EDP20","docnum",docnum);
      mapSegment("E2EDP20", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDP20", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDP20", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDP20", "wmeng", getInput("PO1",2, j));
      commitSegment("E2EDP20",j);

      segnum++;
      hlevel++;
      mapSegment("E2EDP19","mandt",mandt);
      mapSegment("E2EDP19","docnum",docnum);
      mapSegment("E2EDP19", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDP19", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDP19", "hlevel", String.format("%02d",hlevel));
      if (i == 0) {
      mapSegment("E2EDP19", "qualf", "001");
      mapSegment("E2EDP19", "idtnr", getInput("PO1",7, j));
      } else {
      mapSegment("E2EDP19", "qualf", "002");
      mapSegment("E2EDP19", "idtnr", getInput("PO1",9, j));  
      }
      mapSegment("E2EDP19", "ktext", getInput("PO1:PID",5, j));
      commitSegment("E2EDP19",j + i);

    }
   
        
    return packagePayLoad(c);
}

    
}
