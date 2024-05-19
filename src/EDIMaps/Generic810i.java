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

import com.blueseer.edi.EDI;
import com.blueseer.edi.EDI.edi204;
import com.blueseer.edi.EDI.edi810i;
import java.util.ArrayList;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import static com.blueseer.edi.EDIMap.ed;
import com.blueseer.pur.purData;
import com.blueseer.pur.purData.po_mstr;
import com.blueseer.utl.EDData;
import java.io.IOException;
import java.text.DecimalFormat;


/**
 *
 * @author vaughnte
 */
public class Generic810i extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException, UserDefinedException {
     
    
     setControl(c); // as defined by EDI.initEDIControl() and EDIMap.setControl()
        if (isError) { return error;}  // check errors for master variables

        mappedInput = mapInput(c, doc, ISF);
        setReference(getInput("B2","e04")); // must be ran after mappedInput
       // debuginput(mappedInput);  // for debug purposes
    
       edi810i e = new edi810i(getInputISA(6).trim(), getInputISA(8).trim(), getInputGS(2), getInputGS(3), c[4], getInputISA(9), c[1], c[6]);   
       isDBWrite(c);
       
    
      
        // set temp variables
        
        String purpose = "";
        int S5Count = 0;
        int linecount = 0;
        boolean S5 = false;
               
             
                  
       e.setVendNbr(getInput("REF","1:VN",4));
       e.setInvoiceNumber(getInput("BIG","2"));
       e.setInvoiceDate(getInput("BIG","1"));
       e.setPONumber(getInput("BIG","4"));
       e.setTPID(getInputISA(6).trim()); 

      

       
      po_mstr po = purData.getPOMstr(new String[]{e.getPONumber()});
      if (po.po_nbr().isEmpty()) {
          EDData.writeEDILog(c, "ERROR", "810 Purchase Order is unknown: " + e.getPONumber());
      }
      
   /* Now the Detail LOOP  */ 
       /* Item Loop */
    DecimalFormat df = new java.text.DecimalFormat("0.000##");
    int itemcount = getGroupCount("IT1");
    int itemLoopCount = 0;
    int totalqty = 0;
    int i = 0;
    for (i = 1; i <= itemcount; i++) {
        String[] a = new String[e.DetFieldsCount810i];
        e.detailArray.add(e.initDetailArray(a));   // INITIATE Detail ArrayList
        itemLoopCount++;
        totalqty += Integer.valueOf(getInput(i,"IT1",4));
        e.setDetQty(i-1, getInput(i,"IT1",4));
        e.setDetPrice(i-1, getInput(i,"IT1",2));
        e.setDetLine(i-1,getInput(i,"IT1",1));
        if (getInput(i,"IT1",6).equals("VP") || getInput(i,"IT1",6).equals("VN")) {
         e.setDetItem(i-1,getInput(i,"IT1",7));
        } else if (getInput(i,"IT1",8).equals("VP") || getInput(i,"IT1",8).equals("VN")) {
         e.setDetItem(i-1,getInput(i,"IT1",9));   
        } else {
         e.setDetItem(i-1,"UNKNOWN");   
        }
        
    }
    /* end of item loop */
        
        mappedInput.clear();
        
         /* Load Sales Order */
         /* call processDB ONLY if the output is direction of DataBase Internal */
        if (! isError) {
         processDB(c,com.blueseer.edi.EDI.createVoucherFrom810i(e, c)); 
        }
        
        return packagePayLoad(c);
    }

 
 
}


