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

import com.blueseer.ctr.cusData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDI.*;
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import com.blueseer.edi.EDI;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.writeEDILog;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author vaughnte
 */
public class Generic850i extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException {
   
        setControl(c); // as defined by EDI.initEDIControl() and EDIMap.setControl()
        if (isError) { return error;}  // check errors for master variables

        mappedInput = mapInput(c, doc, ISF);

        edi850 e = new edi850(getInputISA(6), getInputISA(8), getInputGS(2), getInputGS(3), getInputISA(13), getInputISA(9), doctype, stctrl);  // mandatory class creation

        // set some global variables if necessary
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int i = 0; 
        
        // set misc document wide variables
        String po = "";
        String part = "";
        String uom = "";
        double discount = 0;
        double listprice = 0;
        double netprice = 0;
        boolean shiploop = false;
        boolean useInternalPrice = false;
         
        e.setOVBillTo(EDData.getEDICustFromSenderISA(doctype, getInputISA(6), getInputISA(8)));   // 3rd parameter '0' is outbound direction '1' is inbound
        po = getInput("BEG","e03");
        e.setPO(po);  
        e.setPODate(convertDateFormat("yyyyMMdd", getInput("BEG","e05")));
           
        if (segmentExists("DTM","1:002","e01")) {
        e.setDueDate(convertDateFormat("yyyyMMdd", getInput("DTM","1:002","e02")));
        } else {
        e.setDueDate(now);    
        }   
        
        int n1count = getGroupCount("N1");
        boolean isN1ST = false;
        for (i = 1; i <= n1count; i++) {
            if (getInput("N1",1,i).equals("ST")) {
            isN1ST = true;
            } else {
            isN1ST = false;
            }
            if (isN1ST) {
                e.setShipTo(getInput("N1",4,i));
                e.setShipToName(getInput("N1",2,i));
                e.setShipToLine1(getInput("N3",1,i));
                e.setShipToCity(getInput("N4",1,i));
                e.setShipToState(getInput("N4",2,i));
                e.setShipToZip(getInput("N4",3,i));
            }
        }  // shipto loop
        
               if (! e.getOVShipTo().isEmpty()) {
               e.setOVBillTo(cusData.getcustBillTo(e.getOVShipTo()));
               } 
               // NOTE: it's imperative that we have an internal billto code assign for pricing and discounts look up during the detail loop
               // if here and we have a blank billto...then error out
               if (e.getOVBillTo().isEmpty()) {
               setError("No internal Billto Found PO:" + po);
               return error; 
               }
        
           /* Now the Detail LOOP  */ 
           /* Item Loop */
        DecimalFormat df = new java.text.DecimalFormat("0.000##");
        int itemcount = getGroupCount("PO1");
        int itemLoopCount = 0;
        int totalqty = 0;
        for (i = 1; i <= itemcount; i++) {
            e.addDetail();  // INITIATE An ArrayList for Each PO1 SEGMENT....variable i is set at bottom of loop as index  i == 0 is first PO1
	    itemLoopCount++;
	    totalqty += Integer.valueOf(getInput("PO1",2,i));
	    e.setDetQty(i, getInput("PO1",2,i));
            if (getInput("PO1",6,i).equals("VP") || getInput("PO1",6,i).equals("VN")) {
             e.setDetItem(i,getInput("PO1",7,i));
            } else if (getInput("PO1",8,i).equals("BP") || getInput("PO1",8,i).equals("SK")) {
             e.setDetItem(i,getInput("PO1",9,i));   
            } else {
             e.setDetItem(i,"UNKNOWN");   
            }
           // e.setDetCustItem(i,getInput("PO1",9,i));
            e.setDetPO(i,po);
            e.setDetLine(i,getInput("PO1",1,i));
            
            if (useInternalPrice) {
            listprice = invData.getItemPriceFromCust(e.getOVBillTo(), getInput("PO1",7,i), getInput("PO1",3,i), cusData.getCustCurrency(e.getOVBillTo()));
            discount = invData.getItemDiscFromCust(e.getOVBillTo());
            netprice = listprice;
            if (discount != 0) {
            netprice = listprice - (listprice * (discount / 100));
            }
            e.setDetNetPrice(i,String.valueOf(currformatDouble(netprice)));
            e.setDetListPrice(i,String.valueOf(currformatDouble(listprice)));
            e.setDetDisc(i,String.valueOf(currformatDouble(discount)));
            } else {
             if (BlueSeerUtils.isParsableToDouble(getInput("PO1",4, i))) {
	        e.setDetNetPrice(i, df.format(Double.valueOf(getInput("PO1",4, i))));
                e.setDetListPrice(i, df.format(Double.valueOf(getInput("PO1",4, i))));
	     } else {
	    	e.setDetNetPrice(i, "0");
                e.setDetListPrice(i, "0");	
	     }   
            }
        }
        /* end of item loop */
         
        mappedInput.clear();
        
         /* Load Sales Order */
         if (! isError) {
         com.blueseer.edi.EDI.createOrderFrom850(e, c); 
         }
         
        return new String[]{"success","transaction mapped successfully"};
      
    }

 
 
}


