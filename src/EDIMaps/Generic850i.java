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
import static com.blueseer.utl.OVData.writeEDILog;


/**
 *
 * @author vaughnte
 */
public class Generic850i {
    
    public static void Mapdata(ArrayList doc, String flddelim, String subdelim, String[] control) {
     
    edi850 e = null;
    
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
        String duedate = "";
        String po = "";
        String billto = "";
        String part = "";
        String uom = "";
        double discount = 0;
        double listprice = 0;
        double netprice = 0;
        boolean shiploop = false;
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
             /* We now set the instance object for edi850 class...to be called and updated later on */
          //   e.add(edi.new edi850(isaSenderID, isaReceiverID, gsSenderID, gsReceiverID,
            //                  isaCtrlNum, isaDate, doctype, docid));
           
             
              e = new edi850(isaSenderID, isaReceiverID, gsSenderID, gsReceiverID, isaCtrlNum, isaDate, doctype, docid);
              
              
              /* If there is a one to one relationship between an ISA sender ID and a billto code in CustMaster...then */
           /* set Customer/TPDOC Maintenance with appropriate values ...example:  if ISA is 'generic' and custmaster billto is 'mycust' */
           /* or you can hardcode 'billto' right here in the code to whatever the custmstr record is...i.e.  billto = "mycust".  */
          
           /* OR Some customers only send in the shipto destination in N1 ST segment...assign billto from N1 ST shipto parent lookup */
           /* can be overridden by N1 ST assignment below */
           
           /* IF this is a 'dropship' shipto address that is not currently in the system...i.e. the shipto address will */
           /* be created on the fly.....you WILL NEED to assign   */
           /* the billto code here at the header level based on the ISA senderID or N1 BT segment  */
           /* because you will not be able to retrieve a billto code from a shipto code that hasn't been previously defined */
               
           e.setOVBillTo(OVData.getEDICustFromSenderISA(isaSenderID, "850", "1"));   // 3rd parameter '1' is inbound direction '0' is outbound
              
           }
          
           
           
           
           /* lets work on the header segments now that 'e' is defined  */
           
           
           /* get the PO and PO date */
           if (segarr[0].toString().equals("BEG")) {
               po = segarr[3];
               e.setPO(segarr[3]);
               if (isSet(segarr, 5)) {
               e.setPODate(convertDateFormat("yyyyMMdd", segarr[5].toString()));
               } else {
               e.setPODate("");
               }
           }
           
           // The due date
           if (segarr[0].toString().equals("DTM") && segarr[1].toString().equals("002")) {
              e.setDueDate(convertDateFormat("yyyyMMdd", segarr[2].toString()));
              duedate = convertDateFormat("yyyyMMdd", segarr[2].toString());
           }
           
           
           if (segarr[0].toString().equals("N1") && segarr[1].toString().equals("ST")) {
               shiploop = true;
           }
           if (segarr[0].toString().equals("N1") && segarr[1].toString().equals("BT")) {
               shiploop = false;
           }
           
           // shipto address info
           if (segarr[0].toString().equals("N3") && shiploop) {
               e.setShipToLine1(segarr[1]);
           }
           
           if (segarr[0].toString().equals("N4") && shiploop) {
               e.setShipToCity(segarr[1]);
               e.setShipToState(segarr[2]);
               e.setShipToZip(segarr[3]);
           }
           
           
           if (segarr[0].toString().equals("N1") && segarr[1].toString().equals("ST"))  {
               // regular Store code
              e.setShipTo(segarr[4]);
              e.setShipToName(segarr[2]);
              // here we take the EDI shipto code in N1 ST E04....and retrieve from generic code xref the internal shipto code
              e.setOVShipTo(OVData.getCodeValueByCodeKey("edixref", e.getShipTo()));
              // here we assign the internal billto code from the retrieved internal shipto code
              // unless shipto lookup returned a blank...i.e....a drop ship type where ship address is previously unknown
              // in that case...OVBillTo 'should' have been set above in header level
              if (! e.getOVShipTo().isEmpty())
              e.setOVBillTo(OVData.getcustBillTo(e.getOVShipTo()));
             
               // NOTE: it's imperative that we have an internal billto code assign for pricing and discounts look up during the detail loop
               // if here and we have a blank billto...then error out
               if (e.getOVBillTo().isEmpty()) {
               writeEDILog(control, "0", "ERROR", "No internal Billto Found");
               error = true;
               }
              
           }
           
           /* Now the Detail  */ 
                  
           if ( segarr[0].toString().equals("PO1")  ) {
               
              if (! error) { 
                  // check and see if last element used is set
                 if (isSet(segarr,7)) {
                  e.setDetCustItem(segarr[7]);
                  e.setDetSku(segarr[7]);
                  e.setDetPO(po);
                  e.setDetQty(segarr[2]);
                  e.setDetLine(segarr[1]);
                  e.setDetRef(segarr[1]);

                  /* lets find the internal part and internal pricing */
                  part = OVData.getPartFromCustCItem(e.getOVBillTo(), segarr[7]);
                  e.setDetItem(part);
                  uom = OVData.getUOMByPart(part);
                  e.setDetUOM(uom);
                  

                  /* lets get the internal list price and discounts*/
                  listprice = OVData.getPartPriceFromCust(e.getOVBillTo(), part, uom, OVData.getCustCurrency(e.getOVBillTo()));
                  discount = OVData.getPartDiscFromCust(e.getOVBillTo());
                  netprice = listprice;

                  if (discount != 0)
                  netprice = listprice - (listprice * (discount / 100));

                  DecimalFormat df = new DecimalFormat("#.##");

                  e.setDetNetPrice(String.valueOf(df.format(netprice)));
                  e.setDetListPrice(String.valueOf(df.format(listprice)));
                  e.setDetDisc(String.valueOf(df.format(discount)));
                 }
              }
              
           }
           linecount++;
         }
         
         
         
         /* Load Sales Order */
         if (! error)
         com.blueseer.edi.EDI.createOrderFrom850(e, control); 
         
         
        
      
    }

 
 
}


