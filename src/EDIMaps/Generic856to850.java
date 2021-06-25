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
import static com.blueseer.edi.EDIMap.commitSegment;
import static com.blueseer.edi.EDIMap.getGroupCount;
import static com.blueseer.edi.EDIMap.getInput;
import static com.blueseer.edi.EDIMap.mapSegment;
import com.blueseer.utl.BlueSeerUtils;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author vaughnte
 */
public class Generic856to850 extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException  {
        
    // These master variables must be set for all maps    
    setControl(c);    // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
    setOutPutFileType("X12");  // X12 of FF
    setOutPutDocType("850");  // 850, 856, ORDERS05, SHPMNT05, etc
    setInputStructureFile("c:\\bs\\wip\\test\\edi\\structures\\X12856.csv");
    setOutputStructureFile("c:\\bs\\wip\\test\\edi\\structures\\X12850.csv");
    if (isError) { return error;}  // check errors for master variables
    
    mappedInput = mapInput(c, doc, ISF);
    setReference(getInput("BSN",2)); // must be ran after mappedInput
    setISA(6,"BOOHOO");
    setISA(8,"HOOBOO");
    debuginput(mappedInput);  // for debug purposes
    
    // set some global variables if necessary
    String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String mandt = "110";
    String docnum = String.format("%016d",Integer.valueOf(c[4]));
    int i = 0;
    
     /* Begin Mapping Segments */ 
    mapSegment("BEG","e01","00");
    mapSegment("BEG","e02",getInput("BSN","e02"));
    mapSegment("BEG","e03","NE");
    mapSegment("BEG","e04","");
    //mapSegment("BEG","e05",getInput("E2EDK03","7:012",8));
    mapSegment("BEG","e05",getInput("E2EDK03","iddat:011","datum"));
    mapSegment("BEG","e10","SP");
    commitSegment("BEG");
       
    mapSegment("CUR","e01","BY");
    mapSegment("CUR","e02","USD");
    commitSegment("CUR");
    
    mapSegment("REF","e01","VR");
    mapSegment("REF","e02",getInput("E2EDK01",25));
    commitSegment("REF");
    
    String plant = getInput("E2EDKA1","7:WE",9) + getInput("E2EDK01","belnr");
    mapSegment("REF","e01","PO");
    mapSegment("REF","e02",plant);
    commitSegment("REF");
   
    mapSegment("N1","e01","BT");
    mapSegment("N1","e02",getInput("E2EDKA1","7:WE",10));
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",getInput("E2EDKA1","7:WE",9));
    commitSegment("N1");
    
    mapSegment("N3","e01",getInput("E2EDKA1","7:WE",11));
    mapSegment("N3","e02",getInput("E2EDKA1","7:WE",14));
    commitSegment("N3");
    
    mapSegment("N4","e01",getInput("E2EDKA1","7:WE",17));
    mapSegment("N4","e02",getInput("E2EDKA1","7:WE",36));
    mapSegment("N4","e03",getInput("E2EDKA1","7:WE",19));
    mapSegment("N4","e04",getInput("E2EDKA1","7:WE",21));
    commitSegment("N4");
    
    mapSegment("PER","e01","BD");
    mapSegment("PER","e02",getInput("E2EDKA1","7:AG",42));
    commitSegment("PER");
    
    /* Item Loop */
    DecimalFormat df = new java.text.DecimalFormat("0.#####");
    int itemcount = getGroupCount("E2EDP01");
    int itemLoopCount = 0;
    for (i = 1; i <= itemcount; i++) {
        itemLoopCount++;
    mapSegment("PO1","e01",getInput("E2EDP01",7, i));
    mapSegment("PO1","e02",df.format(Double.valueOf(getInput("E2EDP01",11, i))));
    mapSegment("PO1","e03",getInput("E2EDP01",14, i));
    if (BlueSeerUtils.isParsableToDouble(getInput("E2EDP01",16, i))) {
    mapSegment("PO1","e04",df.format(Double.valueOf(getInput("E2EDP01",16, i))));
    }
    mapSegment("PO1","e06","SK");
    mapSegment("PO1","e07",getInput("E2EDP01:E2EDP19","7:001",8,i));
    mapSegment("PO1","e08","VN");
    mapSegment("PO1","e09",getInput("E2EDP01:E2EDP19","7:002",8,i));
    commitSegment("PO1");
    
    mapSegment("PID","e01","F");
    mapSegment("PID","e05",getInput("E2EDP01:E2EDP19","7:001",9,i));
    commitSegment("PID");
    
    mapSegment("SAC","e01","N");
    mapSegment("SAC","e02","B840");
    if (BlueSeerUtils.isParsableToDouble(getInput("E2EDP01",18, i))) {
    mapSegment("SAC","e05", df.format(Double.valueOf(getInput("E2EDP01",18, i)) * 100));
    }
    commitSegment("SAC");
    
    
    mapSegment("DTM","e01","002");
    mapSegment("DTM","e02",getInput("E2EDP01:E2EDP20",9,i));
    commitSegment("DTM");
    
    mapSegment("N1","e01","ST");
    mapSegment("N1","e02",getInput("E2EDKA1","7:WE",10));
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",getInput("E2EDKA1","7:WE",9));
    commitSegment("N1");
    
    mapSegment("N2","e01",getInput("E2EDKA1","7:AG",42));
    mapSegment("N2","e02",getInput("E2EDKA1","7:WE",11));
    commitSegment("N2");
    
    mapSegment("N3","e01",getInput("E2EDKA1","7:WE",14));
    commitSegment("N3");
    
    mapSegment("N4","e01",getInput("E2EDKA1","7:WE",17));
    mapSegment("N4","e02",getInput("E2EDKA1","7:WE",36));
    mapSegment("N4","e03",getInput("E2EDKA1","7:WE",19));
    mapSegment("N4","e04",getInput("E2EDKA1","7:WE",21));
    commitSegment("N4");
    
    }
    /* end of item loop */
    
    mapSegment("CTT","e01",String.valueOf(itemLoopCount));
    commitSegment("CTT");
        
    return packagePayLoad(c);
}

    
}
