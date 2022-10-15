import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

setReference(getInput("BEG",3)); //optional...but must be ran after mappedInput


//optional...set some global variables as necessary
var now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
var mandt = "110";
var docnum = String.format("%016d",Integer.valueOf(c[4]));
var segnum = 0;
var psgnum = 0;
var hlevel = 0;

// begin mapping  /* SECTION 2*/

mapSegment("EDI_DC","tabnam","40_U");
mapSegment("EDI_DC","mandt",mandt);
mapSegment("EDI_DC","docnum",docnum);
mapSegment("EDI_DC","mestyp","ORDERS");
mapSegment("EDI_DC","sndpor","SAPPEN");
mapSegment("EDI_DC","sndprt","LS");
mapSegment("EDI_DC","sndprn","ACME");
mapSegment("EDI_DC","idoctyp","ORDERS05");
mapSegment("EDI_DC","rcvpor","EDI");
mapSegment("EDI_DC","rcvprt","LI");
mapSegment("EDI_DC","rcvpfc","LF");
mapSegment("EDI_DC","credat",now.substring(0,8));
mapSegment("EDI_DC","cretim",now.substring(8,14));
commitSegment("EDI_DC");

segnum++;
hlevel++;
mapSegment("E2EDK01","mandt",mandt);
mapSegment("E2EDK01","docnum",docnum);
mapSegment("EDEDK01", "segnum", String.format("%06d",segnum));
mapSegment("EDEDK01", "psgnum", String.format("%06d",psgnum));
mapSegment("EDEDK01", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDK01","curcy","USD");
mapSegment("E2EDK01","hwaer","USD");
mapSegment("E2EDK01","wkurs",getInput("N1","1:ST",4));
mapSegment("E2EDK01","belnr",getInput("BEG",3));
commitSegment("E2EDK01");


// E2EDK14 loop
var addrloop = getLoopKeys("N1");
for (var key : addrloop) {
segnum++;
hlevel++;
mapSegment("E2EDK14","mandt",mandt);
mapSegment("E2EDK14","docnum",docnum);
mapSegment("E2EDK14", "segnum", String.format("%06d",segnum));
mapSegment("E2EDK14", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDK14", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDK14","qualf",getInput(key,1));
mapSegment("E2EDK14","orgid",getInput(key,4));
commitSegment("E2EDK14");  
}



// DTM Loop
var dtmloop = getLoopKeys("DTM");
for (var key : dtmloop) {
segnum++;
hlevel++;
mapSegment("E2EDK03","mandt",mandt);
mapSegment("E2EDK03","docnum",docnum);
mapSegment("E2EDK03", "segnum", String.format("%06d",segnum));
mapSegment("E2EDK03", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDK03", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDK03","iddat",getInput(key,1));
mapSegment("E2EDK03","datum",getInput(key,2));
commitSegment("E2EDK03");   
}


// N1..N4 group loop for E2EDKA1 segments
var n1count = getGroupCount("N1");
for (var i = 1; i <= n1count; i++) {
segnum++;
hlevel++;
mapSegment("E2EDKA1","mandt",mandt);
mapSegment("E2EDKA1","docnum",docnum);
mapSegment("E2EDKA1", "segnum", String.format("%06d",segnum));
mapSegment("E2EDKA1", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDKA1", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDKA1","parvw",getInput(i,"N1",1));
mapSegment("E2EDKA1","lifnr",getInput(i,"N1",4));
mapSegment("E2EDKA1","name1",getInput(i,"N1",2));
mapSegment("E2EDKA1","stras",getInput(i,"N1:N3",1));
mapSegment("E2EDKA1","stras2",getInput(i,"N1:N3",2));
mapSegment("E2EDKA1","ort01",getInput(i,"N1:N4",1));
mapSegment("E2EDKA1","regio",getInput(i,"N1:N4",2));
mapSegment("E2EDKA1","pstlz",getInput(i,"N1:N4",3));
mapSegment("E2EDKA1","isoal",getInput(i,"N1:N4",4));
commitSegment("E2EDKA1");
}


segnum++;
hlevel++;         
mapSegment("E2EDK17","mandt",mandt);
mapSegment("E2EDK17","docnum",docnum);
mapSegment("E2EDK17", "segnum", String.format("%06d",segnum));
mapSegment("E2EDK17", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDK17", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDK17","qualf","001");
mapSegment("E2EDK17","lkond","ZZ");
mapSegment("E2EDK17","lktxt",getInput("FOB",2));
commitSegment("E2EDK17");


// comments // E2EDKT1, E2EDKT2 (hard coded source)
segnum++;
hlevel++;
mapSegment("E2EDKT1","mandt",mandt);
mapSegment("E2EDKT1","docnum",docnum);
mapSegment("E2EDKT1", "segnum", String.format("%06d",segnum));
mapSegment("E2EDKT1", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDKT1", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDKT1","tdid","0001");
mapSegment("E2EDKT1","tsspras_iso","EN");
commitSegment("E2EDKT1");

// MSG segments
var msgloop = getLoopKeys("MSG");
for (var key : msgloop) {
segnum++;
hlevel++;
mapSegment("E2EDKT2","mandt",mandt);
mapSegment("E2EDKT2","docnum",docnum);
mapSegment("E2EDKT2", "segnum", String.format("%06d",segnum));
mapSegment("E2EDKT2", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDKT2", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDKT2","tdline",getInput(key,2));
commitSegment("E2EDKT2");  
} 

// line items  PO1..PID group 
var po1count = getGroupCount("PO1");
for (var i = 1; i <= po1count; i++) {
segnum++;
hlevel++;  
mapSegment("E2EDP01","mandt",mandt);
mapSegment("E2EDP01","docnum",docnum);
mapSegment("E2EDP01", "segnum", String.format("%06d",segnum));
mapSegment("E2EDP01", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDP01", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDP01", "posex", String.format("%06d",i));
mapSegment("E2EDP01","menge",getInput(i,"PO1",2));
mapSegment("E2EDP01","menee",getInput(i,"PO1",3));
mapSegment("E2EDP01","pmene","EA");
mapSegment("E2EDP01","vprei",getInput(i,"PO1",4));
mapSegment("E2EDP01","matnr",getInput(i,"PO1",7));
mapSegment("E2EDP01","matnr_external",getInput(i,"PO1",9));
commitSegment("E2EDP01");

segnum++;
hlevel++;
mapSegment("E2EDP02","mandt",mandt);
mapSegment("E2EDP02","docnum",docnum);
mapSegment("E2EDP02", "segnum", String.format("%06d",segnum));
mapSegment("E2EDP02", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDP02", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDP02", "qualf", "001");
mapSegment("E2EDP02","belnr",getInput(i,"PO1",7));
commitSegment("E2EDP02");

segnum++;
hlevel++;
mapSegment("E2EDP20","mandt",mandt);
mapSegment("E2EDP20","docnum",docnum);
mapSegment("E2EDP20", "segnum", String.format("%06d",segnum));
mapSegment("E2EDP20", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDP20", "hlevel", String.format("%02d",hlevel));
mapSegment("E2EDP20", "wmeng", getInput(i,"PO1",2));
commitSegment("E2EDP20");

segnum++;
hlevel++;
mapSegment("E2EDP19","mandt",mandt);
mapSegment("E2EDP19","docnum",docnum);
mapSegment("E2EDP19", "segnum", String.format("%06d",segnum));
mapSegment("E2EDP19", "psgnum", String.format("%06d",psgnum));
mapSegment("E2EDP19", "hlevel", String.format("%02d",hlevel));
if (i == 0) {
mapSegment("E2EDP19", "qualf", "001");
mapSegment("E2EDP19", "idtnr", getInput(i,"PO1",7));
} else {
mapSegment("E2EDP19", "qualf", "002");
mapSegment("E2EDP19", "idtnr", getInput(i,"PO1",9));  
}
mapSegment("E2EDP19", "ktext", getInput(i,"PO1:PID",5));
commitSegment("E2EDP19");
}

// end mapping

