setReference(getInput("E2EDK01","belnr")); //optional...


//optional...set some global variables as necessary
var now = now();

// begin mapping  /* SECTION 2*/

mapSegment("BGM","e01","220");
mapSegment("BGM","e02",getInput("E2EDK01","belnr"));
mapSegment("BGM","e03","9");
commitSegment("BGM");

var shipdate = composite("4",getInput("E2EDK03","iddat:4","datum"),"102");
mapSegment("DTM","e01",shipdate);
commitSegment("DTM");

mapSegment("RFF","e01",composite("CT","MYREFERENCE"));
commitSegment("RFF");

var addrloop = getLoopKeys("E2EDKA1");
for (var key : addrloop) {
mapSegment("NAD","e01",getInput(key,"qualf"));
mapSegment("NAD","e02",getInput(key,"orgid"));
mapSegment("NAD","e05",getInput(key,"stras"));
mapSegment("NAD","e06",getInput(key,"ort01"));
mapSegment("NAD","e07",getInput(key,"regio"));
mapSegment("NAD","e08",getInput(key,"pstlz"));
commitSegment("NAD");  

mapSegment("CTA","e01","PD");
mapSegment("CTA","e02",composite("","JOHN DOE"));
commitSegment("CTA"); 

mapSegment("COM","e01",composite("999-999-9999","TE"));
commitSegment("COM"); 

}

mapSegment("CUX","e01",composite("2",getInput("E2EDK01",9),"9"));
commitSegment("CUX"); 

mapSegment("PYT","e01","1");
mapSegment("PYT","e01",composite("5","1","CD","45"));
commitSegment("PYT");

// line items  E2EDP01 group 
var lincount = getGroupCount("E2EDP01");
for (var i = 1; i <= lincount; i++) {
mapSegment("LIN","e01",snum(i));
mapSegment("LIN","e03",composite(getInput(i,"E2EDP01","matnr"),"BP"));
commitSegment("LIN");

mapSegment("IMD","e02","3");
mapSegment("IMD","e03",composite("","","",getInput(i,"E2EDP01:E2EDP19",9)));
commitSegment("IMD");

mapSegment("QTY","e01",composite("21",getInput(i,"E2EDP01","menge"),"EA"));
commitSegment("QTY");

var pricedate = composite("2",getInput("E2EDK03","iddat:4","datum"),"102");
mapSegment("DTM","e01",pricedate);
commitSegment("DTM");

mapSegment("PRI","e01",composite("INF",getInput(i,"E2EDP01","vprei")));
commitSegment("PRI");

}

mapSegment("UNS","e01","S");
commitSegment("UNS");

// end mapping

