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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
public class Generic862i {
    
    public static void Mapdata(ArrayList doc, String flddelim, String subdelim, String[] control) {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     edi862 e = null;
     String isaSenderID = "";
     String isaReceiverID = "";
     String gsSenderID = "";
     String gsReceiverID = "";
     String isaCtrlNum = "";
     String isaDate = "";
     String doctype = "";
     String docid = "";
     
         int linecount = 0;
         int LIN = 0;
        
        String billto = "";
        String part = "";
        String shipto = "";
       
        // THESE MUST BE DEFINED !!!!!  
        String fcsdate = "";
        String fcsqty = "";
        String fcstype = "";
        String fcsref = "";
        ArrayList<String> fcsarray = new ArrayList<String>();
        
        boolean linloop = false;
        
         
         
        
         for (Object seg : doc) {
             
           String segarr[] = seg.toString().split(flddelim, -1);
           
           
           if (segarr[0].toString().equals("ISA")) {
              isaSenderID = segarr[6];
              isaReceiverID = segarr[8];
              isaCtrlNum = segarr[13];
           }
           
           if (segarr[0].toString().equals("GS")) {
              gsSenderID = segarr[1];
              gsReceiverID = segarr[2];
           }
           
           if (segarr[0].toString().equals("ST")) {
               doctype = segarr[1];
               docid = segarr[2];
             /* We now set the instance object for edi862 class...to be called and updated later on */
             e = new edi862(isaSenderID, isaReceiverID, gsSenderID, gsReceiverID, isaCtrlNum, isaDate, doctype, docid);
           
           }
          
           
           
           
           /* lets get the Header Info */
           
           
          
           
           /* get the BSS ref */
           if (segarr[0].toString().equals("BSS")) {
               e.setRlse(segarr[10]);
               e.setDetPO(segarr[10]);  // line item PO is in the header BSS segment
           }
           
           /* Set shipto based on lookup in edi_xref table */
           if (segarr[0].toString().equals("N1") && segarr[1].toString().equals("ST")) {
               shipto = OVData.getCodeValueByCodeKey("edixref", segarr[4].toString());
               if (shipto.isEmpty()) {
                 OVData.writeEDILog(control, "ERROR", "No Shipto Found in edi_xref: " + segarr[4].toString()); 
                 return;
               } else {
                e.setOVShipTo(shipto);
                   billto = OVData.getcustBillTo(shipto);
                   if (billto.isEmpty()) {
                       OVData.writeEDILog(control, "ERROR", "No Billto Found for shipto: " + shipto.toString());
                   } else {
                       e.setOVBillTo(billto);
                   }
               }
           }
           
           
          
           
           /* Now the Detail  */         
           
           if ( segarr[0].toString().equals("LIN")  ) {
              linloop = true;
              LIN++;
              e.setDetCustItem(segarr[5]);             
              // e.setDetPO(segarr[7]);  line item PO is in the header BSS segment
              
             
              /* lets find the internal part and internal pricing */
              part = OVData.getPartFromCustCItem(billto, segarr[5].toString());
              e.setDetItem(part);
              
           }
          
           if ( segarr[0].toString().equals("FST")  ) {
               fcsdate = BlueSeerUtils.convertDateFormat("yyyyMMdd", segarr[4].toString());
               fcsqty = segarr[1];
               if ( segarr[2].toString().equals("C")  ) {
                   fcstype = "FIRM";
                   fcsref = segarr[9];
               } else {
                   fcstype = "FCST";
                   fcsref = ""; 
               }
               fcsarray.add(fcsdate + "," + fcsqty + "," + fcstype + "," + fcsref);
               fcsdate = fcstype = fcsqty = fcsref = "";
           }
           
           
           if ( segarr[0].toString().equals("SHP") && linloop ) {
               linloop = false;
             //  e.setFSTMap(LIN - 1, new ArrayList<String>(Arrays.asList(fcsdate, fcsqty, fcstype, fcsref)));
                e.setFSTMap(LIN - 1, new ArrayList<String>(fcsarray));
               fcsarray.clear();
           }
        
          
           linecount++;
         }
         
      
         /* Load Schedule */
         EDI.createOrderFrom862(e, control);
         
         
        
      
    }

 
 
}


