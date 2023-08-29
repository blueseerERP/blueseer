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
import java.util.ArrayList;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import static com.blueseer.edi.EDIMap.ed;
import com.blueseer.utl.EDData;
import java.io.IOException;


/**
 *
 * @author vaughnte
 */
public class Generic204i extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException, UserDefinedException {
     
    
     setControl(c); // as defined by EDI.initEDIControl() and EDIMap.setControl()
        if (isError) { return error;}  // check errors for master variables

        mappedInput = mapInput(c, doc, ISF);
        setReference(getInput("B2","e04")); // must be ran after mappedInput
       // debuginput(mappedInput);  // for debug purposes
    
       edi204 e = new edi204(getInputISA(6).trim(), getInputISA(8).trim(), getInputGS(2), getInputGS(3), c[4], getInputISA(9), c[1], c[6]);   
       isDBWrite(c);
       
    
      
        // set temp variables
        String custfo = ""; 
        String origfo = "";
        String purpose = "";
        int S5Count = 0;
        int linecount = 0;
        boolean S5 = false;
               
             
                  
       e.setCustFO(getInput("B2","e04"));
       e.setCarrier(getInput("B2","e02"));
       // lets set tpid and cust at this point with ISA sender ID and cross reference lookup into cmedi_mstr
       e.setTPID(getInputISA(6).trim()); 
       e.setCust(EDData.getEDIXrefIn(getInputISA(6), getInputGS(2), "BT", ""));




        // if cancellation...cancel original freight order based on custfo number...if status is not 'InTransit'
       purpose = getInput("B2A","e01");
       if (purpose.equals("01")) {
       EDData.CancelFOFrom204i(getInput("B2","e04"));
       EDData.writeEDILog(c, "INFO", "204 Cancel");
       }

       if (purpose.equals("04")) {
       origfo = OVData.getFreightOrderNbrFromCustFO(getInput("B2","e04"));
          if (origfo.isEmpty()) {
              EDData.writeEDILog(c, "ERROR", "204 Update Orig Not Found");
          }
       EDData.writeEDILog(c, "INFO", "204 Update Not Implemented");
       }

       if (getInput("L11","e02").toUpperCase().equals("ZI")) {
           e.setRef(getInput("L11","e01"));
       }

       S5 = true;  
       S5Count++;
       linecount = S5Count - 1; // for base zero array
       String[] a = new String[e.DetFieldsCount204i];
       e.detailArray.add(e.initDetailArray(a)); 
       e.setDetLine(linecount, getInput("S5","e01"));
       e.setDetType(linecount, getInput("S5","e02"));              
        if ( getInput("G62","e01").equals("68") || getInput("G62","e01").equals("70")) {
        e.setDetDelvDate(linecount, convertDateFormat("yyyyMMdd", getInput("G62","e02")));
          if ( ! getInput("G62","e04").isEmpty() ) {
          e.setDetDelvTime(linecount, getInput("G62","e04"));
          }
        } 
        if ( getInput("G62","e01").equals("69") || getInput("G62","e01").equals("78")) {
           e.setDetShipDate(linecount, convertDateFormat("yyyyMMdd", getInput("G62","e02")));
           if ( ! getInput("G62","e04").isEmpty() ) {
          e.setDetShipTime(linecount, getInput("G62","e04"));
          }
        }  
        e.setDetAddrName(linecount, getInput("N1","e02"));
        if (! getInput("N1","e04").isEmpty()) {
          e.setDetAddrCode(linecount, getInput("N1","e04"));
        }
        e.setDetAddrLine1(linecount, getInput("N3","e01"));
        e.setDetAddrCity(linecount, getInput("N4","e01"));
        e.setDetAddrState(linecount, getInput("N4","e02"));
        e.setDetAddrZip(linecount, getInput("N4","e03"));
        e.setWeight(getInput("L3","e01"));
         
        
        mappedInput.clear();
        
         /* Load Sales Order */
         /* call processDB ONLY if the output is direction of DataBase Internal */
        if (! isError &&  purpose.equals("00")) {
         processDB(c,com.blueseer.edi.EDI.createCFOFrom204(e, c)); 
        }
        
        return packagePayLoad(c);
    }

 
 
}


