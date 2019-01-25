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
public class GenericCSVi {
        public static void MapCSVdata(ArrayList doc, String[] control, String senderid) {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     ArrayList<edi850> e = new ArrayList<edi850>();
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
        
        DecimalFormat df = new DecimalFormat("#.0000");
         
        Map<String, ArrayList<String[]>> map = new HashMap<String, ArrayList<String[]>>();
         int i = -1;
         for (Object seg : doc) {
             
             if (seg.toString().startsWith("purchase"))   // SAI sends in column headers...skip this
                 continue;
             
             String segarr[] = seg.toString().split(",", -1);
             
             if (segarr[0].isEmpty()) // skip blanks
                 continue;
             
             if (i == -1)
                 billto = segarr[3];
             
             po = segarr[0];
             if (! map.containsKey(po)) {
                map.put(po, null);
                i++;
                e.add(new edi850(isaSenderID, isaReceiverID, gsSenderID, gsReceiverID,
                              isaCtrlNum, isaDate, doctype, docid));
                 
                e.get(i).setPO(segarr[0]);
                e.get(i).setPODate(segarr[1].toString());
                e.get(i).setDueDate(segarr[2].toString());
                e.get(i).setOVBillTo(segarr[3]); 
                e.get(i).setShipToName(segarr[5]);
                e.get(i).setShipToLine1(segarr[6]);
                e.get(i).setShipToLine2(segarr[7]);
                e.get(i).setShipToLine3(segarr[8]);
                e.get(i).setShipToCity(segarr[9]);
                e.get(i).setShipToState(segarr[10]);
                e.get(i).setShipToZip(segarr[11]);
                e.get(i).setShipToCountry(segarr[12]);
                
                // line level from first instance of key
                e.get(i).setDetLine(i,segarr[13]);
                e.get(i).setDetCustItem(i,segarr[14]);
                e.get(i).setDetQty(i,segarr[15]);
                 part = OVData.getPartFromCustCItem("", segarr[14]);  // SAI J5R00001...does not have xref tied to billto...tied to blank billto
                  e.get(i).setDetItem(i,part);
                  uom = OVData.getUOMByPart(part);
                  e.get(i).setDetUOM(i,uom); 
                listprice = OVData.getPartPriceFromCust(billto, part, uom, OVData.getCustCurrency(billto));
                  e.get(i).setDetListPrice(i,df.format(listprice));
                discount = OVData.getPartDiscFromCust(billto);
                  e.get(i).setDetDisc(i,df.format(discount));
                netprice = OVData.getNetPriceFromListAndDisc(listprice, discount);
                  e.get(i).setDetNetPrice(i,df.format(netprice));
                // have to set additional line items even if blank
                e.get(i).setDetSku(i,"");
                e.get(i).setDetRef(i,"");
                e.get(i).setDetPO(i,segarr[0]);
               
             } else {
                 
                // line level from first instance of key
                e.get(i).setDetLine(i,segarr[13]);
                e.get(i).setDetCustItem(i,segarr[14]);
                e.get(i).setDetQty(i,segarr[15]);
                 part = OVData.getPartFromCustCItem(billto, segarr[14]);
                  e.get(i).setDetItem(i,part);
                  uom = OVData.getUOMByPart(part);
                  e.get(i).setDetUOM(i,uom); 
                listprice = OVData.getPartPriceFromCust(billto, part, uom, OVData.getCustCurrency(billto));
                  e.get(i).setDetListPrice(i,df.format(listprice));
                discount = OVData.getPartDiscFromCust(billto);
                  e.get(i).setDetDisc(i,df.format(discount));
                netprice = OVData.getNetPriceFromListAndDisc(listprice, discount);
                  e.get(i).setDetNetPrice(i,df.format(netprice));
                // have to set additional line items even if blank
                e.get(i).setDetSku(i,"");
                e.get(i).setDetRef(i,"");
                e.get(i).setDetPO(i,segarr[0]);
                
             }
           
           // each unique PO in field 1 is an order that may repeat per how many line items on the order
           // therefore field 1 will be the key and have as many 'values' as it repeats with unique line items
          
          
           linecount++;
         
         
         
         
        
      
    } // for each line in file

  /* Load Sales Order(s) */
         EDI.createOrderFromCSV(e, control);
   
    }
}

         



