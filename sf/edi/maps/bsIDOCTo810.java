import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

setReference(getInput("E2EDT20","TKNUM")); 

// set some global variables if necessary
var now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
var i = 0;
var hlcounter = 0;
var itemLoopCount = 0;
var totalqty = 0;


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
var addrloop = getLoopKeys("E2EDKA1");

var addrtype = "";
for (var key : addrloop) {

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
var itemcount = getGroupCount("E2EDP01");

for (i = 1; i <= itemcount; i++) {
itemLoopCount++;
totalqty += Double.valueOf(getInput(i,"E2EDP01",11).trim());

mapSegment("IT1","e01", String.valueOf(i));
mapSegment("IT1","e02", bsformat(getInput(i,"E2EDP01",11),"4"));  // menge
mapSegment("IT1","e03", getInput(i,"E2EDP01",12));  // menee
mapSegment("IT1","e04", bsformat(getInput(i,"E2EDP26","qualf:001", 8),"4"));  // price
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

var tds_sum = Double.valueOf(getInput("E2EDS01","sumid:010", "summe").trim()) * 100;
mapSegment("TDS","e01",bsformat(String.valueOf(tds_sum),"4"));
commitSegment("TDS");
