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
import static com.blueseer.utl.EDData.writeEDILog;


/**
 *
 * @author vaughnte
 */
public class Generic945i { 
    
    public static void Mapdata(ArrayList doc, String flddelim, String subdelim, String[] control) {
     
    edi945 e = null;
    
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
        String so = "";
        String line = "";
        String part = "";
        String uom = "";
        double discount = 0;
        double listprice = 0;
        double netprice = 0;
        boolean hasline = true;
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
           e = new edi945(isaSenderID, isaReceiverID, gsSenderID, gsReceiverID, isaCtrlNum, isaDate, doctype, docid);
           }
          
           
           
           
           /* lets work on the header segments now that 'e' is defined  */
           
           
           /* get the SO from the W06 ...this is crucial since most of the header data is derived from lookup of original Sales Order */
           if (segarr[0].toString().equals("W06")) {
               so = segarr[2];
               e.setSO(so);
               e.setShipper(segarr[4]);
               
               
                        // now lets get order header info 
                 // so_nbr, so_po, so_cust, so_ship, so_due_date, so_shipvia, so_rmks
                 String[] h = OVData.getOrderHeaderArray(so);
                 
                 
                 // get original sales order detail
                 // sod_line, sod_part, sod_custpart, sod_ord_qty, sod_uom, sod_desc, it_net_wt, sod_listprice, sod_disc, sod_netprice, sod_site, sod_wh, sod_loc
                 dt = OVData.getOrderDetailArray(so);
                 
                 
                 
                
                 e.setOVBillTo(h[2]);
                 e.setOVShipTo(h[3]);
                 e.setPO(h[1]);
                 po = h[1];
                 e.setRemarks(h[6]);
                 e.setShipVia(h[5]);  // may be overridden below by W27 segment

                 
                  // NOTE: it's imperative that we have an internal billto code assign for pricing and discounts look up during the detail loop
               // if here and we have a blank billto...then error out
               if (so.isEmpty()) {
               writeEDILog(control, "ERROR", "No Sales Order found for this 945");
               error = true;
               }
               
               
               // make sure this is not a duplicate 945 ...check sh_bol and sh_cust
               if (OVData.isValidShipperByCustAndBOL(segarr[4], h[2])) {
                  writeEDILog(control, "ERROR", "duplicate 945 so/bol=" + segarr[2] + "/" + segarr[4]);
               error = true; 
               }
                 
                
                 
           }
           
           // The ship date
           if (segarr[0].toString().equals("G62") && segarr[1].toString().equals("11")) {
              e.setShipDate(convertDateFormat("yyyyMMdd", segarr[2].toString()));
           }
           
           // The shipvia carrier
           if (segarr[0].toString().equals("W27")) {
              e.setShipVia(segarr[2]);
           }
           
           
           
         
           
           /* Now the Detail  */ 
           
                 
           
           if ( segarr[0].toString().equals("W12")  ) {
                            
               e.detailArray.add(new String[e.DetFieldsCount945]); 
              
              if (! error) { 
                  
                  // This 945 map depends on the original line number of the orignal order being returned in the W12E18 position with a "PL" qualifier
                  // all original order detail will flow from this line number match
                  if (isSet(segarr,18)) {
                  line = segarr[18];
                  } else {
                    writeEDILog(control, "ERROR", "Line Item detail on 945 missing PL info");
                    error = true;  
                  }
                  
                  hasline = false;
                  for (String[] d : dt) {
                      if (line.equals(d[0])) {
                          e.setDetLine(linecount, segarr[18]);
                          e.setDetDesc(linecount, d[5]);
                          e.setDetListPrice(linecount, d[7]);
                          e.setDetNetPrice(linecount, d[9]);
                          e.setDetWH(linecount, d[11]);
                          e.setDetPO(linecount, po);
                          e.setDetDisc(linecount, "0");
                          e.setDetLoc(linecount, d[12]);
                          e.setDetSite(linecount, d[10]);
                          e.setDetNetWt(linecount, d[6]);
                          e.setDetUOM(linecount, d[4]);
                          e.setDetItem(linecount, d[1]);
                          e.setDetCustItem(linecount, d[2]);
                          e.setDetQtyOrd(linecount, d[3]);
                          e.setDetQtyShp(linecount, segarr[3]);
                          hasline = true;
                          break;
                      }
                  }
                  if (! hasline) {
                    writeEDILog(control, "ERROR", "Cannot determine original line item from PL: " + line);
                    error = true;    
                  }
                  
              }
              linecount++;
           }
           
         }
         
         
         
         /* Load Shipper */
         if (! error) {
             com.blueseer.edi.EDI.createShipperFrom945(e, control);
         }
         
        
      
    }

 
 
}


