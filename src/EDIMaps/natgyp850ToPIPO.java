package EDIMaps;



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




import java.util.ArrayList;
import com.blueseer.edi.EDI;
import java.io.IOException;


/**
 *
 * @author vaughnte
 */
public  class natgyp850ToPIPO extends com.blueseer.edi.EDIMap {      
    
    public void Mapdata(ArrayList doc, String[] c) throws IOException {
     
    
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
    
    // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
    setOutPutEnvelopeStrings(c);
       
    // set temp variables
    String po = "";    
            
     // MAP  ...this is the MAP section  Note:  Outbound looping is driven by Inbound assignments and conditional logic (if user defines)
     // All non-envelope segments are constructed here and assigned to one of three arrays H = Header, D = Detail, T = Trailer
     for (Object seg : doc) { 
       String elementArray[] = seg.toString().split(EDI.escapeDelimiter(ed), -1);
       String segment = elementArray[0];

       // skip envelope segments ISA, GS, ST, SE, GE, IEA
       if (EDI.isEnvelopeSegment(elementArray[0].toString())) { 
       continue;
       }
       
       String[] newseg = null;
       
       switch (segment) {
         case "BEG" :
           D.add(String.join(ed,elementArray));  // write out BEG as-is
           po = elementArray[3];
           
           // write out new REF CR
           if (getISA(6).trim().equals("9495828695")) {
           newseg = new String[]{"REF","CR",getGS(2).trim()};
           } else {
           newseg = new String[]{"REF","CR",getISA(6).trim()};    
           }
           D.add(String.join(ed,newseg));  
           
           // write out new REF BT
           newseg = new String[]{"REF","BT",isactrl};
           D.add(String.join(ed,newseg));
           
            // write out new REF DD
           newseg = new String[]{"REF","DD",stctrl};
           D.add(String.join(ed,newseg));
           
            // write out new REF ZZ
           newseg = new String[]{"REF","ZZ",getISA(9).trim()};
           D.add(String.join(ed,newseg));
           
            // write out new REF SES
           newseg = new String[]{"REF","SES",getISA(10).trim()};
           D.add(String.join(ed,newseg));
         break;
         
         case "DTM" :
             if (elementArray[1].equals("002")) {
             D.add(String.join(ed,elementArray)); 
             }
             break;
         case "N1" :
             if (elementArray[1].equals("ST") || elementArray[1].equals("SO") ) {
                 elementArray[2] = isSet(elementArray,4) ? elementArray[4] : "" ;
             D.add(String.join(ed,elementArray));
             }
             break;    
        case "PO1" :
              newseg = new String[]{"PO1",elementArray[1],elementArray[2],elementArray[3],elementArray[4],elementArray[5],"IN",getCustItem(elementArray),"","","PO",po};
              D.add(String.join(ed,newseg));
             break; 
        case "PID" :
              elementArray[2] = elementArray[3] = elementArray[4] = "";
              D.add(String.join(ed,elementArray));
             break;   
        case "CTT" :
              D.add(String.join(ed,elementArray));
             break;     
             
         default :
           break;
       }


     } // for each segment
     // END MAP
         
     // override ISA if necessary
     updateISA(5,"ZZ");
     updateISA(6,"NATGYP");
     updateISA(7,"ZZ");
     updateISA(8,"NATSAP");
     updateISA(11,"|");
     updateISA(12,"00501");
     updateISA(16,">");
     
     updateGS(2,"NATGYP");
     updateGS(3,"NATSAP");
     updateGS(8,"005010");
     
    // Call this function to join H, D, T arrays into H, D, T Strings     
    setHDTStrings();
        
    // concat all into one Output String          
    setFinalOutputString();  
    
    // Write to Standard Out
    System.out.println(content);
    
    // Write to outfile
    edi.writeFileCmdLine(content, outfile);
        
      
    }

    public String getCustItem(String[] a) {
        String s = "";
        for (int i = 0; i < a.length ; i++) {
            s = (a[i].equals("BP")) ? a[i+1] : "";
            if (! s.isEmpty()) break;
        }
        if (s.isEmpty()) {
          for (int i = 0; i < a.length ; i++) {
            s = (a[i].equals("SK")) ? a[i+1] : "";
            if (! s.isEmpty()) break;
          }  
        }
        if (s.isEmpty()) {
          for (int i = 0; i < a.length ; i++) {
            s = (a[i].equals("IN")) ? a[i+1] : "";
            if (! s.isEmpty()) break;
          }  
        }
        if (s.isEmpty()) {
          for (int i = 0; i < a.length ; i++) {
            s = (a[i].equals("PI")) ? a[i+1] : "";
            if (! s.isEmpty()) break;
          }  
        }
        return s;
    }
}


