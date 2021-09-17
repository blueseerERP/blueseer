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
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import com.blueseer.edi.EDI;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.writeEDILog;


/**
 *
 * @author vaughnte
 */
public class Generic850i extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) {
   
    setControl(c); // as defined by EDI.initEDIControl() and EDIMap.setControl()
    setControlISA(c[13].toString().split(EDI.escapeDelimiter(ed), -1));  // EDIMap.setISA
    setControlGS(c[14].toString().split(EDI.escapeDelimiter(ed), -1));   // EDIMap.setGS
     
        // set mandatory document wide variables
         edi850 e = null;  // mandatory class creation
        boolean error = false;  // set at any time to prevent order creation
        int i = 0;   // detail line counter
        
        // set misc document wide variables
        String po = "";
        String part = "";
        String uom = "";
        double discount = 0;
        double listprice = 0;
        double netprice = 0;
        boolean shiploop = false;
               
        // loop through each segment in the inbound raw 850
         for (Object seg : doc) {
        
             
           // create array of each segment per delimiter used  
           String x[] = seg.toString().split(EDI.escapeDelimiter(ed), -1);
           

           // skip envelope segments ISA, GS, GE, IEA
           if (EDI.isEnvelopeSegment(x[0].toString())) { 
           continue;
           }
           
           
           if (x[0].toString().equals("BEG")) {   // BEG SEGMENT DRIVE HEADER ORDER CREATION
              
                /* We now set the instance object for edi850 class...to be called and updated later on */
               /* this is done once and only once per doc */
               /* the object 'e' will contain cherry-picked information form the raw 850 file ...and passed to the CreateOrder routine */
               e = new edi850(getISA(6), getISA(8), getGS(2), getGS(3), getISA(13), getISA(9), doctype, stctrl);
              
              /* If there is a one to one relationship between an ISA sender ID and a billto code in CustMaster...then */
           /* set Customer/TPDOC Maintenance with appropriate values ...example:  if ISA is 'generic' and custmaster billto is 'mycust' */
           /* or you can hardcode 'billto' right here in the code to whatever the custmstr record is...i.e.  billto = "mycust".  */
          
           /* OR Some customers only send in the shipto destination in N1 ST segment...assign billto from N1 ST shipto parent lookup */
           /* can be overridden by N1 ST assignment below */
           
           /* IF this is a 'dropship' shipto address that is not currently in the system...i.e. the shipto address will */
           /* be created on the fly.....you WILL NEED to assign   */
           /* the billto code here at the header level based on the ISA senderID or N1 BT segment  */
           /* because you will not be able to retrieve a billto code from a shipto code that hasn't been previously defined */
            
          
               e.setOVBillTo(EDData.getEDICustFromSenderISA(doctype, getISA(6), getISA(8)));   // 3rd parameter '0' is outbound direction '1' is inbound
               
               
               po = x[3];
               e.setPO(po);
               
               if (isSet(x, 5)) {
               e.setPODate(convertDateFormat("yyyyMMdd", x[5].toString()));
               } else {
               e.setPODate("");
               }
           } // END BEG SEGMENT
           
           
           // DTM SEGMENT
           if (x[0].toString().equals("DTM") && x[1].toString().equals("002")) {
              e.setDueDate(convertDateFormat("yyyyMMdd", x[2].toString()));
           }
           
           
           // N1 SEGMENT
           if (x[0].toString().equals("N1") && x[1].toString().equals("ST")) {
               shiploop = true;
           }
           if (x[0].toString().equals("N1") && x[1].toString().equals("BT")) {
               shiploop = false;
           }
           
           // shipto address info
           if (x[0].toString().equals("N3") && shiploop) {
               e.setShipToLine1(x[1]);
           }
           
           if (x[0].toString().equals("N4") && shiploop) {
               e.setShipToCity(x[1]);
               e.setShipToState(x[2]);
               e.setShipToZip(x[3]);
           }
           
           
           if (x[0].toString().equals("N1") && x[1].toString().equals("ST"))  {
               // regular Store code
              e.setShipTo(x[4]);
              e.setShipToName(x[2]);
              // here we take the EDI shipto code in N1 ST E04....and retrieve from generic code xref the internal shipto code
              e.setOVShipTo(OVData.getCodeValueByCodeKey("edixref", e.getShipTo()));
              // here we assign the internal billto code from the retrieved internal shipto code
              // unless shipto lookup returned a blank...i.e....a drop ship type where ship address is previously unknown
              // in that case...OVBillTo 'should' have been set above in header level
              
               
              if (! e.getOVShipTo().isEmpty()) {
              e.setOVBillTo(OVData.getcustBillTo(e.getOVShipTo()));
              } 
              
             
              
               // NOTE: it's imperative that we have an internal billto code assign for pricing and discounts look up during the detail loop
               // if here and we have a blank billto...then error out
               if (e.getOVBillTo().isEmpty()) {
               writeEDILog(c, "ERROR", "No internal Billto Found");
               error = true;
               }
              
           }
           // END N1 SEGMENT
          
           /* Now the Detail LOOP  */ 
            // PO1 SEGMENT      
           if ( x[0].toString().equals("PO1")  ) {  
               e.addDetail();  // INITIATE An ArrayList for Each PO1 SEGMENT....variable i is set at bottom of loop as index  i == 0 is first PO1
               part = "";   // init part
              if (! error) { 
                  // check and see if last element used is set
                 if (isSet(x,7)) {
                  e.setDetCustItem(i,x[7]);
                  e.setDetSku(i,x[7]);
                  e.setDetPO(i,po);
                  e.setDetQty(i,x[2]);
                  e.setDetLine(i,x[1]);
                  e.setDetRef(i,x[1]);

                  /* lets find the internal part and internal pricing */
                  part = OVData.getItemFromCustCItem(e.getOVBillTo(), x[7]);
                  if (part.isEmpty()) {
                      part = x[7];
                      e.setDetItem(i,part);
                  } else {
                      e.setDetItem(i,part);
                      uom = OVData.getUOMByPart(part);
                      e.setDetUOM(i,uom);


                      /* lets get the internal list price and discounts*/
                      listprice = OVData.getItemPriceFromCust(e.getOVBillTo(), part, uom, OVData.getCustCurrency(e.getOVBillTo()));
                      discount = OVData.getItemDiscFromCust(e.getOVBillTo());
                      netprice = listprice;

                      if (discount != 0)
                      netprice = listprice - (listprice * (discount / 100));

                      DecimalFormat df = new DecimalFormat("#.##");

                      e.setDetNetPrice(i,String.valueOf(df.format(netprice)));
                      e.setDetListPrice(i,String.valueOf(df.format(listprice)));
                      e.setDetDisc(i,String.valueOf(df.format(discount)));
                 }
                }
                  
              }
               i++;  // increment index counter
               
           } // END PO1 SEGMENT
          
         } // END of segments in 850 Doc
         
         
         
         /* Load Sales Order */
         if (! error)
         com.blueseer.edi.EDI.createOrderFrom850(e, c); 
         
         
        return c;
      
    }

 
 
}


