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

import java.util.ArrayList;
import com.blueseer.edi.EDI;
import com.blueseer.utl.OVData;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author vaughnte
 */
public class GenericIDOCto850 extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException  {
    
    
      
    
    // These 6 global variables must be set for all maps    
    setControl(c);    // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    ISF = readISF(c, "c:\\junk\\ORDERS05.csv");
    OSF = readOSF("c:\\junk\\X12850.csv"); 
    setOutPutFileType("X12");  // X12 of FF
    setOutPutDocType("850");  // 850, 856, ORDERS05, SHPMNT05, etc
    mappedInput = mapInput(c, doc, ISF);
    debuginput(mappedInput);
    
    /* a few global variables */
    int i = 0; // used for all looping ...for loops reset it's initial value each time
    
    /* Begin Mapping Segments */ 
    mapSegment("BEG","e01","00");
    mapSegment("BEG","e02",getInput("E2EDK01","belnr"));
    mapSegment("BEG","e03","NE");
    mapSegment("BEG","e04","");
    //mapSegment("BEG","e05",getInput("E2EDK03","7:012",8));
    mapSegment("BEG","e05",getInput("E2EDK03","iddat:011","datum"));
    mapSegment("BEG","e10","SP");
    commitSegment("BEG",1);
       
    mapSegment("CUR","e01","BY");
    mapSegment("CUR","e02","USD");
    commitSegment("CUR",1);
    
    mapSegment("REF","e01","VR");
    mapSegment("REF","e02",getInput("E2EDK01",25));
    commitSegment("REF",1);
    
    String plant = getInput("E2EDKA1","7:WE",9) + getInput("E2EDK01","belnr");
    mapSegment("REF","e01","PO");
    mapSegment("REF","e02",plant);
    commitSegment("REF",2);
   
    mapSegment("N1","e01","BT");
    mapSegment("N1","e02",getInput("E2EDKA1","7:WE",10));
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",getInput("E2EDKA1","7:WE",9));
    commitSegment("N1",1);
    
    mapSegment("N3","e01",getInput("E2EDKA1","7:WE",11));
    mapSegment("N3","e02",getInput("E2EDKA1","7:WE",14));
    commitSegment("N3",1);
    
    mapSegment("N4","e01",getInput("E2EDKA1","7:WE",17));
    mapSegment("N4","e02",getInput("E2EDKA1","7:WE",36));
    mapSegment("N4","e03",getInput("E2EDKA1","7:WE",19));
    mapSegment("N4","e04",getInput("E2EDKA1","7:WE",21));
    commitSegment("N4",1);
    
    mapSegment("PER","e01","BD");
    mapSegment("PER","e02",getInput("E2EDKA1","7:AG",42));
    commitSegment("PER",1);
    
    /* Item Loop */
    DecimalFormat df = new java.text.DecimalFormat("0.#####");
    int itemcount = getGroupCount("E2EDP01");
    int itemLoopCount = 0;
    for (i = 1; i <= itemcount; i++) {
        itemLoopCount++;
    mapSegment("PO1","e01",getInput("E2EDP01",7, i));
    mapSegment("PO1","e02",df.format(Double.valueOf(getInput("E2EDP01",11, i))));
    mapSegment("PO1","e03",getInput("E2EDP01",14, i));
    mapSegment("PO1","e04",df.format(Double.valueOf(getInput("E2EDP01",16, i))));
    mapSegment("PO1","e06","SK");
    mapSegment("PO1","e07",getInput("E2EDP01:E2EDP19","7:001",8,i));
    mapSegment("PO1","e08","VN");
    mapSegment("PO1","e09",getInput("E2EDP01:E2EDP19","7:002",8,i));
    commitSegment("PO1",i);
    
    mapSegment("PID","e01","F");
    mapSegment("PID","e05",getInput("E2EDP01:E2EDP19","7:001",9,i));
    commitSegment("PID",i);
    
    mapSegment("SAC","e01","N");
    mapSegment("SAC","e02","B840");
    mapSegment("SAC","e05", df.format(Double.valueOf(getInput("E2EDP01",18, i)) * 100));
    commitSegment("SAC",i);
    
    
    mapSegment("DTM","e01","002");
    mapSegment("DTM","e02",getInput("E2EDP01:E2EDP20",9,i));
    commitSegment("DTM",i);
    
    mapSegment("N1","e01","ST");
    mapSegment("N1","e02",getInput("E2EDKA1","7:WE",10));
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",getInput("E2EDKA1","7:WE",9));
    commitSegment("N1",i + 1);
    
    mapSegment("N2","e01",getInput("E2EDKA1","7:AG",42));
    mapSegment("N2","e02",getInput("E2EDKA1","7:WE",11));
    commitSegment("N2",i + 1);
    
    mapSegment("N3","e01",getInput("E2EDKA1","7:WE",14));
    commitSegment("N3",i + 1);
    
    mapSegment("N4","e01",getInput("E2EDKA1","7:WE",17));
    mapSegment("N4","e02",getInput("E2EDKA1","7:WE",36));
    mapSegment("N4","e03",getInput("E2EDKA1","7:WE",19));
    mapSegment("N4","e04",getInput("E2EDKA1","7:WE",21));
    commitSegment("N4",i + 1);
    
    }
    /* end of item loop */
    
    mapSegment("CTT","e01",String.valueOf(itemLoopCount));
    commitSegment("CTT",i);
   
    return packagePayLoad(c);
}

    
}
