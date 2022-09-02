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
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; 
/**
 *
 * @author terryva
 */
public class ACME810 extends com.blueseer.edi.EDIMap {
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
		    java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("0.#####");
		     
		    /* Begin Mapping Segments */ 
		    mapSegment("BIG","e01",getInput("E2EDK02","qualf:009", "datum"));
		    mapSegment("BIG","e02",getInput("E2EDK02","qualf:009", "belnr"));
		    mapSegment("BIG","e03",getInput("E2EDK02","qualf:001", "datum"));
		    mapSegment("BIG","e04",getInput("E2EDK02","qualf:001", "belnr"));
		    commitSegment("BIG");
		   
		    mapSegment("REF","e01", "BM");
		    mapSegment("REF","e02",getInput("E2EDK02","qualf:012", "belnr"));
		    commitSegment("REF");
		  
		    
		    
		    
		    // addresses
		    ArrayList<String> addrloop = getLoopKeys("E2EDKA1");
		    
		    String addrtype;
		    for (String key : addrloop) {
		    	 
		    	 addrtype = getInput(key,7);
		    	 if (addrtype.trim().equals("WE")) {
		    		    mapSegment("N1","e01", "ST");
		    		    mapSegment("N1","e02",getInput(key,10));
		    		    mapSegment("N1","e03","92");
		    		    mapSegment("N1","e04",getInput(key,8));
		    		    commitSegment("N1");
		    		    mapSegment("N3","e01",getInput(key,14));
		    		    commitSegment("N3");
		    		    mapSegment("N4","e01",getInput(key,17));
		    		    mapSegment("N4","e02",getInput(key,38));
		    		    mapSegment("N4","e03",getInput(key,19));
		    		    commitSegment("N4");
		    	 }
		    	 if (addrtype.trim().equals("AG")) {
		    		    mapSegment("N1","e01", "BT");
		    		    mapSegment("N1","e02",getInput(key,10));
		    		    mapSegment("N1","e03","92");
		    		    mapSegment("N1","e04",getInput(key,8));
		    		    commitSegment("N1");
		    		    mapSegment("N3","e01",getInput(key,14));
		    		    commitSegment("N3");
		    		    mapSegment("N4","e01",getInput(key,17));
		    		    mapSegment("N4","e02",getInput(key,38));
		    		    mapSegment("N4","e03",getInput(key,19));
		    		    commitSegment("N4");
		    	 }
		    	 if (addrtype.trim().equals("RS")) {
		    		    mapSegment("N1","e01", "RE");
		    		    mapSegment("N1","e02",getInput(key,10));
		    		    commitSegment("N1");
		    		    mapSegment("N3","e01",getInput(key,14));
		    		    commitSegment("N3");
		    		    mapSegment("N4","e01",getInput(key,17));
		    		    mapSegment("N4","e02",getInput(key,38));
		    		    mapSegment("N4","e03",getInput(key,19));
		    		    commitSegment("N4");
		    	 }
		    }
		   
		    
		    // ITD
		    mapSegment("ITD","e01", "14");
		    mapSegment("ITD","e02","1");
		    mapSegment("ITD","e03","1");
		    mapSegment("ITD","e12",getInput("E2EDK18","qualf:005", "zterm_txt"));
		    commitSegment("ITD");
		    
		    
		    // DTM
		    mapSegment("DTM","e01", "011");
		    mapSegment("DTM","e02",getInput("E2EDK03","iddat:026", "datum"));
		    commitSegment("DTM");
		    
		    
		 
		    
		    // Item Loop 
		    DecimalFormat df = new java.text.DecimalFormat("0.#####");
		    int itemcount = getGroupCount("E2EDP01");
		    
		    for (i = 1; i <= itemcount; i++) {
			    itemLoopCount++;
			    totalqty += Double.valueOf(getInput(i,"E2EDP01",11).trim());
			    
			    mapSegment("IT1","e01", String.valueOf(i));
			    mapSegment("IT1","e02", decimalFormat.format(Double.valueOf(getInput(i,"E2EDP01",11))));  // menge
			    mapSegment("IT1","e03", getInput(i,"E2EDP01",12));  // menee
			    mapSegment("IT1","e04",decimalFormat.format(Double.valueOf(getInput(i,"E2EDP26","qualf:001", 8))));  // price
			    mapSegment("IT1","e06","IN");
			    mapSegment("IT1","e07",getInput(i,"E2EDP19","qualf:002", 8));  // mfg item
			    mapSegment("IT1","e08","UP");
			    mapSegment("IT1","e09",getInput(i,"E2EDP19","qualf:003", 8));  // upc item
			    commitSegment("IT1");
			    
			    
			    mapSegment("PID","e01","F");
			    mapSegment("PID","e05",getInput(i,"E2EDP19","qualf:002", 9));
			    commitSegment("PID");
		    
		    }
		    
		    /* end of item loop */
		    
		    double tds_sum = Double.valueOf(getInput("E2EDS01","sumid:010", "summe").trim()) * 100;
		    mapSegment("TDS","e01",String.valueOf(decimalFormat.format(tds_sum)));
		    commitSegment("TDS");
		        
		    return packagePayLoad(c);
		}

	
}
