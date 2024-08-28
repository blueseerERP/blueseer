// set some global variables if necessary
var now = now();
var hlcounter = 0;
var itemLoopCount = 0;
var totalqty = 0;


/* Begin Mapping Segments */ 

// BSN
mapSegment("BSN","e01","00");
mapSegment("BSN","e02",getInput("BEG",3));
mapSegment("BSN","e03",now.substring(0,8));
mapSegment("BSN","e04",now.substring(8,12));
commitSegment("BSN");


hlcounter++;   
mapSegment("HL","e01", snum(hlcounter));
mapSegment("HL","e03","S");
mapSegment("HL","e04","1");
commitSegment("HL");

// TD1...if available
for (int i = 1; i <= getGroupCount("TD1"); i++) {
mapSegment("TD1","e01", getInput(i,"TD1",1));
mapSegment("TD1","e02", getInput(i,"TD1",2));
mapSegment("TD1","e03", getInput(i,"TD1",3));
commitSegment("TD1");
}

// REF...if available
for (int i = 1; i <= getGroupCount("REF"); i++) {
mapSegment("REF","e01", getInput(i,"REF",1));
mapSegment("REF","e02", getInput(i,"REF",2));
commitSegment("REF");
}

// DTM...if available
for (int i = 1; i <= getGroupCount("DTM"); i++) {
mapSegment("DTM","e01", getInput(i,"DTM",1));
mapSegment("DTM","e02", getInput(i,"DTM",2));
commitSegment("DTM");
}


// addresses
int addrcount = getGroupCount("N1");
for (int i = 1; i <= addrcount; i++) {
  if (getInput(i,"N1",1).equals("ST")) {
  mapSegment("N1","e01", "ST");
  mapSegment("N1","e02",getInput(i,"N1",2));
  mapSegment("N1","e03","92");
  mapSegment("N1","e04",getInput(i,"N1",4));
  commitSegment("N1");
  mapSegment("N3","e01",getInput(i,"N1:N3",1));
  commitSegment("N3");
  mapSegment("N4","e01",getInput(i,"N1:N4",1));
  mapSegment("N4","e02",getInput(i,"N1:N4",2));
  mapSegment("N4","e03",getInput(i,"N1:N4",3));
  commitSegment("N4");
  }
if (getInput(i,"N1",1).equals("BT")) {
  mapSegment("N1","e01", "BT");
  mapSegment("N1","e02",getInput(i,"N1",2));
  mapSegment("N1","e03","92");
  mapSegment("N1","e04",getInput(i,"N1",4));
  commitSegment("N1");
  mapSegment("N3","e01",getInput(i,"N1:N3",1));
  commitSegment("N3");
  mapSegment("N4","e01",getInput(i,"N1:N4",1));
  mapSegment("N4","e02",getInput(i,"N1:N4",2));
  mapSegment("N4","e03",getInput(i,"N1:N4",3));
  commitSegment("N4");
  }
}

// HL Order type loop...assumes one HL O
hlcounter++;
mapSegment("HL","e01", snum(hlcounter));
mapSegment("HL","e02","1");
mapSegment("HL","e03","O");
mapSegment("HL","e04","1");
commitSegment("HL");

mapSegment("PRF","e01", getInput("BEG",3));
commitSegment("PRF");


// Item Loop 
// HL Item type loop
var itemcount = getGroupCount("PO1");
for (int i = 1; i <= itemcount; i++) {
itemLoopCount++;
totalqty += Double.valueOf(getInput(i,"PO1",2));

hlcounter++;
mapSegment("HL","e01", snum(hlcounter));
mapSegment("HL","e02","2");
mapSegment("HL","e03","I");
mapSegment("HL","e04","1");
commitSegment("HL");

mapSegment("LIN","e02","VP");
mapSegment("LIN","e03",getInput(i,"PO1",7));
mapSegment("LIN","e04","BP");
mapSegment("LIN","e05",getInput(i,"PO1",9));
commitSegment("LIN");

mapSegment("SN1","e02",formatNumber(getInput(i,"PO1",2).trim(),"4"));
mapSegment("SN1","e03",getInput(i,"PO1",3));
commitSegment("SN1");

} // end of itemcount loop


mapSegment("CTT","e01",snum(hlcounter));
mapSegment("CTT","e02",snum(totalqty));
commitSegment("CTT");

/* end of mapping */

