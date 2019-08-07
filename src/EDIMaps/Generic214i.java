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
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDI.*;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import static com.blueseer.utl.BlueSeerUtils.isSet;
import com.blueseer.edi.EDI;
import bsmf.MainFrame;
import static com.blueseer.utl.OVData.writeEDILog;


/**
 *
 * @author vaughnte
 */
public class Generic214i {
    
    public static void Mapdata(ArrayList doc, String flddelim, String subdelim, String[] control) {
     
    edi214 e = null;  
    
     String isaSenderID = "";
     String isaReceiverID = "";
     String gsSenderID = "";
     String gsReceiverID = "";
     String isaCtrlNum = "";
     String isaDate = "";
     String doctype = "";
     String docid = "";
     
     String order = "";
     
      
        boolean error = false;
       
         
        
         for (Object seg : doc) {
             
           String segarr[] = seg.toString().split(flddelim, -1);
           
           
           if (segarr[0].toString().equals("ISA")) {
              isaSenderID = segarr[6].trim();
              isaReceiverID = segarr[8].trim();
              isaCtrlNum = segarr[13];
           }
           
           if (segarr[0].toString().equals("GS")) {
              gsSenderID = segarr[1];
              gsReceiverID = segarr[2];
           }
           
           if (segarr[0].toString().equals("ST")) {
               doctype = segarr[1];
           e = new edi214(isaSenderID, isaReceiverID, gsSenderID, gsReceiverID, isaCtrlNum, isaDate, doctype, docid);
           }
           
          
           if (segarr[0].toString().equals("B10")) {
               e.setOrder(segarr[2]);  
               order = segarr[2];
               e.setProNbr(segarr[1]);
           } // B10 segment
           
           
           if (segarr[0].toString().equals("AT7")) {
               e.setStatus(segarr[1]);
               e.setRemarks(segarr[2]);
               if (isSet(segarr, 5)) {
                   e.setApptDate(segarr[5]);
               }
               if (isSet(segarr, 6)) {
                   e.setApptTime(segarr[6]);
               }
               
           } // AT7 segment
           
           if (segarr[0].toString().equals("MS1")) {
               e.setLat(segarr[4]); 
               e.setLon(segarr[5]); 
           } // MS1 segment
           
           if (segarr[0].toString().equals("MS2")) {
               e.setSCAC(segarr[1]);
               e.setEquipmentNbr(segarr[2]); 
               e.setEquipmentType(segarr[3]); 
           } // MS2 segment
           
           
         }
         
          if (order.isEmpty()) {
               writeEDILog(control, "0", "ERROR", "No Freight Order found for this 214 in the B10 Segment");
               error = true;
          }
          
          if (! order.isEmpty()) {
               if (! OVData.isValidFreightOrderNbr(order)) {
               writeEDILog(control, "0", "ERROR", "Invalid Freight Order found for this 214 in the B10 Segment");
               error = true;
               }
          }
         
         
         /* Load Shipper */
         if (! error) {
             com.blueseer.edi.EDI.createFOTDETFrom214(e, control); 
         }
         
        
      
    }

 
 
}


