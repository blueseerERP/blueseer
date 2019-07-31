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
public class Generic990To990 extends com.blueseer.edi.EDIMap {    
    
    public void Mapdata(ArrayList doc, String[] c) throws IOException {
     
    
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
    
    // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setControl(c);    
    
    // set the envelope segments (ISA, GS, ST, SE, GE, IEA)...the default is to create envelope from DB read...-x will override this and keep inbound envelopes
    // you can then override individual envelope elements as desired
    setOutPutEnvelopeStrings(c);
           
            
     // MAP  ...this is the MAP section  Note:  Outbound looping is driven by Inbound assignments and conditional logic (if user defines)
     // All non-envelope segments are constructed here and assigned to one of three arrays H = Header, D = Detail, T = Trailer
     for (Object seg : doc) {
       String elementArray[] = seg.toString().split(EDI.escapeDelimiter(ed), -1);
       String segment = elementArray[0];

       // skip envelope segments ISA, GS, ST, SE, GE, IEA
       if (EDI.isEnvelopeSegment(elementArray[0].toString())) { 
       continue;
       }

       
       switch (segment) {
         case "B1" :
           elementArray[1] = "WHAT";
           D.add(String.join(ed,elementArray));
         break;
         default :
           D.add(String.join(ed,elementArray));
       }


     } // for each segment
     // END MAP
         
         
    // Call this function to join H, D, T arrays into H, D, T Strings     
    setHDTStrings();
        
    // concat all into one Output String          
    setFinalOutputString();  
    
    // Write to Standard Out
    System.out.println(content);
    
    // Write to outfile
    edi.writeFileCmdLine(content, outfile);
        
      
    }

 
}


