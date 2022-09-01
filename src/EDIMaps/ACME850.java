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

import java.util.ArrayList;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author vaughnte
 */
public class ACME850 extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException, UserDefinedException  { 
        
    /* SECTION 1*/
    setControl(c);    //required...set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
       
    mappedInput = mapInput(c, doc, ISF); //required...sets the source data structure for all subsequent map functions
    
    setReference(getInput("BEG",3)); //optional...but must be ran after mappedInput
    
    debuginput(mappedInput);  //optional... for debug purposes
    
    //optional...set some global variables as necessary
    String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String mandt = "110";
    String docnum = String.format("%016d",Integer.valueOf(c[4]));
    int segnum = 0;
    int psgnum = 0;
    int hlevel = 0;
    
    // begin mapping  /* SECTION 2*/
    
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
       commitSegment("EDI_DC");

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
       commitSegment("E2EDK01");


      // E2EDK14 loop
      int n1count = getLoopCount("N1");
       for (int i = 1; i <= n1count; i++) {
      segnum++;
      hlevel++;
      mapSegment("E2EDK14","mandt",mandt);
      mapSegment("E2EDK14","docnum",docnum);
      mapSegment("E2EDK14", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDK14", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDK14", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDK14","qualf",getInput(i,"N1",1));
      mapSegment("E2EDK14","orgid",getInput(i,"N1",4));
      commitSegment("E2EDK14");  
       }
              
               
       // DTM Loop
       int dtmcount = getLoopCount("DTM");
       for (int i = 1; i <= dtmcount; i++) {
          segnum++;
          hlevel++;
          mapSegment("E2EDK03","mandt",mandt);
          mapSegment("E2EDK03","docnum",docnum);
          mapSegment("E2EDK03", "segnum", String.format("%06d",segnum));
          mapSegment("E2EDK03", "psgnum", String.format("%06d",psgnum));
          mapSegment("E2EDK03", "hlevel", String.format("%02d",hlevel));
          mapSegment("E2EDK03","iddat",getInput(i,"DTM",1));
          mapSegment("E2EDK03","datum",getInput(i,"DTM",2));
          commitSegment("E2EDK03");   
       }
               
       // N1 loop
       for (int i = 1; i <= n1count; i++) {
          segnum++;
          hlevel++;
          mapSegment("E2EDKA1","mandt",mandt);
          mapSegment("E2EDKA1","docnum",docnum);
          mapSegment("E2EDKA1", "segnum", String.format("%06d",segnum));
          mapSegment("E2EDKA1", "psgnum", String.format("%06d",psgnum));
          mapSegment("E2EDKA1", "hlevel", String.format("%02d",hlevel));
          mapSegment("E2EDKA1","parvw",getInput(i,"N1",1));
          mapSegment("E2EDKA1","lifnr",getInput(i,"N1",4));
          mapSegment("E2EDKA1","name1",getInput(i,"N1",2));
          mapSegment("E2EDKA1","stras",getInput(i,"N1:N3",1));
          mapSegment("E2EDKA1","stras2",getInput(i,"N1:N3",2));
          mapSegment("E2EDKA1","ort01",getInput(i,"N1:N4",1));
          mapSegment("E2EDKA1","regio",getInput(i,"N1:N4",2));
          mapSegment("E2EDKA1","pstlz",getInput(i,"N1:N4",3));
          mapSegment("E2EDKA1","isoal",getInput(i,"N1:N4",4));
          commitSegment("E2EDKA1");
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
      commitSegment("E2EDK17");


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
      commitSegment("E2EDKT1");

      int msgcount = getLoopCount("MSG");
       
       for (int i = 1; i <= msgcount; i++) {
          segnum++;
          hlevel++;
          mapSegment("E2EDKT2","mandt",mandt);
          mapSegment("E2EDKT2","docnum",docnum);
          mapSegment("E2EDKT2", "segnum", String.format("%06d",segnum));
          mapSegment("E2EDKT2", "psgnum", String.format("%06d",psgnum));
          mapSegment("E2EDKT2", "hlevel", String.format("%02d",hlevel));
          mapSegment("E2EDKT2","tdline",getInput(i,"MSG",2));
          commitSegment("E2EDKT2");  
       } 

    // line items
    int po1count = getLoopCount("PO1");
    for (int i = 1; i <= po1count; i++) {
      segnum++;
      hlevel++;  
      mapSegment("E2EDP01","mandt",mandt);
      mapSegment("E2EDP01","docnum",docnum);
      mapSegment("E2EDP01", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDP01", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDP01", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDP01", "posex", String.format("%06d",i));
      mapSegment("E2EDP01","menge",getInput(i,"PO1",2));
      mapSegment("E2EDP01","menee",getInput(i,"PO1",3));
      mapSegment("E2EDP01","pmene","EA");
      mapSegment("E2EDP01","vprei",getInput(i,"PO1",4));
      mapSegment("E2EDP01","matnr",getInput(i,"PO1",7));
      mapSegment("E2EDP01","matnr_external",getInput(i,"PO1",9));
      commitSegment("E2EDP01");

      segnum++;
      hlevel++;
      mapSegment("E2EDP02","mandt",mandt);
      mapSegment("E2EDP02","docnum",docnum);
      mapSegment("E2EDP02", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDP02", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDP02", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDP02", "qualf", "001");
      mapSegment("E2EDP02","belnr",getInput(i,"PO1",7));
      commitSegment("E2EDP02");

      segnum++;
      hlevel++;
      mapSegment("E2EDP20","mandt",mandt);
      mapSegment("E2EDP20","docnum",docnum);
      mapSegment("E2EDP20", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDP20", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDP20", "hlevel", String.format("%02d",hlevel));
      mapSegment("E2EDP20", "wmeng", getInput(i,"PO1",2));
      commitSegment("E2EDP20");

      segnum++;
      hlevel++;
      mapSegment("E2EDP19","mandt",mandt);
      mapSegment("E2EDP19","docnum",docnum);
      mapSegment("E2EDP19", "segnum", String.format("%06d",segnum));
      mapSegment("E2EDP19", "psgnum", String.format("%06d",psgnum));
      mapSegment("E2EDP19", "hlevel", String.format("%02d",hlevel));
      if (i == 0) {
      mapSegment("E2EDP19", "qualf", "001");
      mapSegment("E2EDP19", "idtnr", getInput(i,"PO1",7));
      } else {
      mapSegment("E2EDP19", "qualf", "002");
      mapSegment("E2EDP19", "idtnr", getInput(i,"PO1",9));  
      }
      mapSegment("E2EDP19", "ktext", getInput(i,"PO1:PID",5));
      commitSegment("E2EDP19");

    }
   
    // end mapping
      
    /* SECTION 3 */    
    return packagePayLoad(c); //required...sets output payload
}

    
}
