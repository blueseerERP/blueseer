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
public class Generic204i {
    
    public static void Mapdata(ArrayList doc, String flddelim, String subdelim, String[] control) {
     
    edi204i e = null;
    
     String isaSenderID = "";
     String isaReceiverID = "";
     String gsSenderID = "";
     String gsReceiverID = "";
     String isaCtrlNum = "";
     String isaDate = "";
     String doctype = "";
     String docid = "";
     
        int doctypecount = 0;
        int linecount = 0;
        String custfo = "";
        String origfo = "";
        String purpose = "";
        String line = "";
     
        boolean hasline = true;
        boolean S5 = false;
        int S5Count = 0;
        boolean error = false;
        ArrayList<String[]> dt = new ArrayList<String[]>();
         
         
        
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
           e = new edi204i(isaSenderID, isaReceiverID, gsSenderID, gsReceiverID, isaCtrlNum, isaDate, doctype, docid);
           }
          
           
           
           if (segarr[0].toString().equals("B2")) {
               custfo = segarr[4];
               e.setCustFO(custfo);
               e.setCarrier(segarr[2]);
               
               // lets set tpid and cust at this point with ISA sender ID and cross reference lookup into cmedi_mstr
               e.setTPID(isaSenderID); 
               e.setCust(OVData.getEDICustFromSenderISA(isaSenderID, "204", "1"));
               
           }
           
           if (segarr[0].toString().equals("B2A")) {
               purpose = segarr[1];
               
               // if cancellation...cancel original freight order based on custfo number...if status is not 'InTransit'
               if (purpose.equals("01")) {
                   OVData.CancelFOFrom204i(custfo);
                   return;
               }
               
               if (purpose.equals("04")) {
                  origfo = OVData.getFreightOrderNbrFromCustFO(custfo);
               }
           }
           
           
           
           /* Now the Detail  */ 
           
                 
           
           if ( segarr[0].toString().equals("S5")  ) {
               S5 = true;  
               S5Count++;
               linecount = S5Count - 1; // for base zero array
               String[] a = new String[e.DetFieldsCount204i];
               e.detailArray.add(e.initDetailArray(a)); 
               e.setDetLine(linecount, segarr[1]);
               e.setDetType(linecount, segarr[2]);
               e.setDetWeight(linecount, segarr[3]);
               e.setDetWeightUOM(linecount, segarr[4]);
               e.setDetBoxes(linecount, segarr[5]);
                if ( segarr[6].toString().equals("PL")  ) {
                e.setDetUnits(linecount, segarr[5]);
                }
           }
           
           if ( segarr[0].toString().equals("G62") && S5  ) {
               if ( segarr[1].toString().equals("68") || segarr[1].toString().equals("70")) {
                   e.setDetDelvDate(linecount, convertDateFormat("yyyyMMdd", segarr[2]));
                   if (isSet(segarr,4)) {
                      e.setDetDelvTime(linecount, segarr[4]);
                      }
               } 
               if ( segarr[1].toString().equals("69")) {
                   e.setDetShipDate(linecount, convertDateFormat("yyyyMMdd", segarr[2]));
                   if (isSet(segarr,4)) {
                      e.setDetShipTime(linecount, segarr[4]);
                      }
               } 
           }
                      
           if ( segarr[0].toString().equals("N1") && S5  ) {
               e.setDetAddrName(linecount, segarr[2]);
               if (isSet(segarr,4)) {
                      e.setDetAddrCode(linecount, segarr[4]);
                      }
           }
           
           if ( segarr[0].toString().equals("N3") && S5  ) {
               e.setDetAddrLine1(linecount, segarr[1]);
           }
           
           if ( segarr[0].toString().equals("N4") && S5  ) {
               e.setDetAddrCity(linecount, segarr[1]);
               e.setDetAddrState(linecount, segarr[2]);
               e.setDetAddrZip(linecount, segarr[3]);
           }
           if ( segarr[0].toString().equals("G61") && S5  ) {
               e.setDetAddrContact(linecount, segarr[2]);
               e.setDetAddrPhone(linecount, segarr[4]);
           }
           
         }
         
         
         
         /* Load Freight Order unless cancellation....cancellation is handled above */
         if (! error &&  purpose.equals("00")) {
             com.blueseer.edi.EDI.createFOMSTRFrom204i(e, control);
         }
         
         if (! error &&  purpose.equals("04")) {
             // blueseer.EDI.updateFOMSTRFrom204i(e, control);   not yet implemented
         }
        
      
    }

 
 
}


