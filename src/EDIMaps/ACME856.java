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

import java.util.ArrayList;
import com.blueseer.utl.BlueSeerUtils;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author vaughnte
 */
public class ACME856 extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException, UserDefinedException  { 
	        
		  
        setControl(c);    // set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
        if (isError) { return error;}  // check errors for master variables

        mappedInput = mapInput(c, doc, ISF);
        setReference(getInput("E2EDT20","TKNUM")); // must be ran after mappedInput
        debuginput(mappedInput);  // for debug purposes

        // set some global variables if necessary
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int i = 0;
        int hlcounter = 0;
        int itemLoopCount = 0;
        double totalqty = 0;


        /* Begin Mapping Segments */ 
        mapSegment("BSN","e01","00");
        mapSegment("BSN","e02",getInput("E2EDT20","tknum"));
        mapSegment("BSN","e03",now.substring(0,8));
        mapSegment("BSN","e04",now.substring(8,12));
        commitSegment("BSN");
        //mapSegment("BEG","e05",getInput("E2EDK03","7:012",8));
       // mapSegment("BEG","e05",getInput("E2EDK03","iddat:011","datum"));


        hlcounter++;   
        mapSegment("HL","e01", String.valueOf(hlcounter));
        mapSegment("HL","e03","S");
        mapSegment("HL","e04","1");
        commitSegment("HL");

        mapSegment("TD1","e01", "PCS25");
        mapSegment("TD1","e02",getInput("ZE1EDT856","zpackages"));
        mapSegment("TD1","e06","A3");
        mapSegment("TD1","e07",getInput("E2EDL20","ntgew"));
        mapSegment("TD1","e08","LB");
        commitSegment("TD1");

        mapSegment("TD5","e02", "2");
        mapSegment("TD5","e03",getInput("ZE1EDT856","zscac"));
        mapSegment("TD5","e05",getInput("E2ADRM4","partner_q:SP","name1"));
        mapSegment("TD5","e06","CC");
        commitSegment("TD5");

        mapSegment("REF","e01", "BM");
        mapSegment("REF","e02",getInput("E2EDT20","tknum"));
        commitSegment("REF");

        mapSegment("REF","e01", "CN");
        mapSegment("REF","e02",getInput("E2EDL24",69));
        commitSegment("REF");

        mapSegment("DTM","e01", "011");
        mapSegment("DTM","e02",now);
        commitSegment("DTM");

        // addresses
        // do OB exception
        if (! getInput("ZELEDL20","ZBP_STOREID").isEmpty()) {
             mapSegment("N1","e01", "OB");
                mapSegment("N1","e02",getInput("ZELEDL20","ZNAME_OB"));
                mapSegment("N1","e03","92");
                mapSegment("N1","e04",getInput("ZELEDL20","ZBP_STOREID"));
                commitSegment("N1");
                mapSegment("N3","e01",getInput("ZELEDL20","ZSTREET_OB"));
                commitSegment("N3");
                mapSegment("N4","e01",getInput("ZELEDL20","ZCITY_OB"));
                mapSegment("N4","e02",getInput("ZELEDL20","ZREGION_OB"));
                mapSegment("N4","e03",getInput("ZELEDL20","ZPOSTL_OB"));
                commitSegment("N4");
        }
        ArrayList<String> addrloop = getLoopKeys("E2ADRM1");
        int j = 0;
        String addrtype = "";
        for (String key : addrloop) {
             addrtype = getInput(key,7);
             if (addrtype.trim().equals("WE")) {
                        mapSegment("N1","e01", "ST");
                        mapSegment("N1","e02",getInput(key,13));
                        mapSegment("N1","e03","92");
                        mapSegment("N1","e04",getInput(key,9));
                        commitSegment("N1");
                        mapSegment("N3","e01",getInput(key,23));
                        commitSegment("N3");
                        mapSegment("N4","e01",getInput(key,31));
                        mapSegment("N4","e02",getInput(key,43));
                        mapSegment("N4","e03",getInput(key,28));
                        commitSegment("N4");
             }
             if (addrtype.trim().equals("OSO")) {
                        mapSegment("N1","e01", "SH");
                        mapSegment("N1","e02",getInput(key,13));
                        mapSegment("N1","e03","92");
                        mapSegment("N1","e04",getInput(key,9));
                        commitSegment("N1");
                        mapSegment("N3","e01",getInput(key,23));
                        commitSegment("N3");
                        mapSegment("N4","e01",getInput(key,31));
                        mapSegment("N4","e02",getInput(key,43));
                        mapSegment("N4","e03",getInput(key,28));
                        commitSegment("N4");
             }
             if (addrtype.trim().equals("OSP")) {
                        mapSegment("N1","e01", "SF");
                        mapSegment("N1","e02",getInput(key,13));
                        mapSegment("N1","e03","92");
                        mapSegment("N1","e04",getInput(key,9));
                        commitSegment("N1");
                        mapSegment("N3","e01",getInput(key,23));
                        commitSegment("N3");
                        mapSegment("N4","e01",getInput(key,31));
                        mapSegment("N4","e02",getInput(key,43));
                        mapSegment("N4","e03",getInput(key,28));
                        commitSegment("N4");
             }
        }



        // PO loop
        ArrayList<String> loop = getLoopKeys("E2EDL41");
        j = 0;
        String po = "";
        for (String key : loop) {
             j++;
             po = getInput(key,8);
        }
        hlcounter++;
        mapSegment("HL","e01", String.valueOf(hlcounter));
        mapSegment("HL","e02","1");
        mapSegment("HL","e03","O");
        mapSegment("HL","e04","1");
        commitSegment("HL");

        mapSegment("PRF","e01", po);
        commitSegment("PRF");


        // Item Loop 
        DecimalFormat df = new java.text.DecimalFormat("0.#####");
        int itemcount = getGroupCount("E2EDL24");

        for (i = 1; i <= itemcount; i++) {
                itemLoopCount++;
                totalqty += Double.valueOf(getInput(i,"E2EDL24",19).trim());

                hlcounter++;
                mapSegment("HL","e01", String.valueOf(hlcounter));
                mapSegment("HL","e02","2");
                mapSegment("HL","e03","I");
                mapSegment("HL","e04","1");
                commitSegment("HL");

                mapSegment("LIN","e02","UP");
                if (getInput(i,"E2EDL24",8).length() > 2) {
                    mapSegment("LIN","e03",getInput(i,"E2EDL24",38).substring(2));
                } else {
                    mapSegment("LIN","e03",getInput(i,"E2EDL24",38));
                }

                mapSegment("LIN","e04","BP");
                mapSegment("LIN","e05",getInput(i,"E2EDL24",18));
                mapSegment("LIN","e06","VN");
                if (getInput(i,"E2EDL24",8).length() > 10) {
                    mapSegment("LIN","e07",getInput(i,"E2EDL24",8).substring(10));
                } else {
                    mapSegment("LIN","e07",getInput(i,"E2EDL24",8));
                }

                commitSegment("LIN");


                if (BlueSeerUtils.isParsableToDouble(getInput(i,"E2EDL24",19))) {
                    mapSegment("SN1","e02",df.format(Double.valueOf(getInput(i,"E2EDL24",19).trim())));
                } else {
                    mapSegment("SN1","e02",getInput(i,"E2EDL24",19).trim());	
                }
                mapSegment("SN1","e03","PC");
                commitSegment("SN1");

                mapSegment("PO4","e01","1");
                mapSegment("PO4","e02",getInput(i,"ZE1EDL856",9));
                mapSegment("PO4","e03","PC");
                commitSegment("PO4");


                mapSegment("PID","e01","F");
                mapSegment("PID","e05",getInput(i,"E2EDL24",10));
                commitSegment("PID");

        }

        /* end of item loop */

        mapSegment("CTT","e01",String.valueOf(hlcounter));
        mapSegment("CTT","e02",String.valueOf(totalqty));
        commitSegment("CTT");

        return packagePayLoad(c);
    }
    
}
