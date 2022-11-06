setReference(getInput("BGM",2)); //optional...


//optional...set some global variables as necessary
var now = now();
var mandt = "110";
var docnum = padNumber(c[4],16);
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
mapSegment("EDEDK01", "segnum", padNumber(segnum,6));
mapSegment("EDEDK01", "psgnum", padNumber(psgnum,6));
mapSegment("EDEDK01", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDK01","curcy","USD");
mapSegment("E2EDK01","hwaer","USD");
mapSegment("E2EDK01","wkurs",getInput("N1","1:DP",2));
mapSegment("E2EDK01","belnr",getInput("BGM",2));
commitSegment("E2EDK01");


// E2EDK14 loop
var addrloop = getLoopKeys("NAD");
for (var key : addrloop) {
segnum++;
hlevel++;
mapSegment("E2EDK14","mandt",mandt);
mapSegment("E2EDK14","docnum",docnum);
mapSegment("E2EDK14", "segnum", padNumber(segnum,6));
mapSegment("E2EDK14", "psgnum", padNumber(psgnum,6));
mapSegment("E2EDK14", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDK14","qualf",getInput(key,1));
mapSegment("E2EDK14","orgid",getInput(key,2));
commitSegment("E2EDK14");  
}



// DTM Loop
var dtmloop = getLoopKeys("DTM");
for (var key : dtmloop) {
segnum++;
hlevel++;
mapSegment("E2EDK03","mandt",mandt);
mapSegment("E2EDK03","docnum",docnum);
mapSegment("E2EDK03", "segnum", padNumber(segnum,6));
mapSegment("E2EDK03", "psgnum", padNumber(psgnum,6));
mapSegment("E2EDK03", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDK03","iddat",getInputComp(key,1,1));
mapSegment("E2EDK03","datum",getInputComp(key,1,2));
commitSegment("E2EDK03");   
}


// NAD group loop for E2EDKA1 segments
var nadcount = getGroupCount("NAD");
for (var i = 1; i <= nadcount; i++) {
segnum++;
hlevel++;
mapSegment("E2EDKA1","mandt",mandt);
mapSegment("E2EDKA1","docnum",docnum);
mapSegment("E2EDKA1", "segnum", padNumber(segnum,6));
mapSegment("E2EDKA1", "psgnum", padNumber(psgnum,6));
mapSegment("E2EDKA1", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDKA1","parvw",getInput(i,"NAD",1));
mapSegment("E2EDKA1","lifnr",getInput(i,"NAD",2));
mapSegment("E2EDKA1","name1",getInput(i,"NAD",3));
mapSegment("E2EDKA1","stras",getInput(i,"NAD",5));
mapSegment("E2EDKA1","ort01",getInput(i,"NAD",6));
mapSegment("E2EDKA1","regio",getInput(i,"NAD",7));
mapSegment("E2EDKA1","pstlz",getInput(i,"NAD",8));
mapSegment("E2EDKA1","isoal",getInput(i,"NAD",9));
commitSegment("E2EDKA1");
}


segnum++;
hlevel++;         
mapSegment("E2EDK17","mandt",mandt);
mapSegment("E2EDK17","docnum",docnum);
mapSegment("E2EDK17", "segnum", padNumber(segnum,6));
mapSegment("E2EDK17", "psgnum", padNumber(psgnum,6));
mapSegment("E2EDK17", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDK17","qualf","001");
mapSegment("E2EDK17","lkond","ZZ");
mapSegment("E2EDK17","lktxt",getInput("TDT",5));
commitSegment("E2EDK17");


// comments // E2EDKT1, E2EDKT2 (hard coded source)
segnum++;
hlevel++;
mapSegment("E2EDKT1","mandt",mandt);
mapSegment("E2EDKT1","docnum",docnum);
mapSegment("E2EDKT1", "segnum", padNumber(segnum,6));
mapSegment("E2EDKT1", "psgnum", padNumber(psgnum,6));
mapSegment("E2EDKT1", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDKT1","tdid","0001");
mapSegment("E2EDKT1","tsspras_iso","EN");
commitSegment("E2EDKT1");

// MSG segments
var msgloop = getLoopKeys("FTX");
for (var key : msgloop) {
segnum++;
hlevel++;
mapSegment("E2EDKT2","mandt",mandt);
mapSegment("E2EDKT2","docnum",docnum);
mapSegment("E2EDKT2", "segnum", padNumber(segnum,6));
mapSegment("E2EDKT2", "psgnum", padNumber(psgnum,6));
mapSegment("E2EDKT2", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDKT2","tdline",getInput(key,4));
commitSegment("E2EDKT2");  
} 

// line items  LIN group 
var lincount = getGroupCount("LIN");
for (var i = 1; i <= lincount; i++) {
segnum++;
hlevel++;  
mapSegment("E2EDP01","mandt",mandt);
mapSegment("E2EDP01","docnum",docnum);
mapSegment("E2EDP01", "segnum", padNumber(segnum,6));
mapSegment("E2EDP01", "psgnum", padNumber(segnum,6));
mapSegment("E2EDP01", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDP01", "posex", padNumber(i,6));
mapSegment("E2EDP01","menge",getInputComp(i,"LIN:QTY",1,2));
mapSegment("E2EDP01","menee",getInputComp(i,"LIN:QTY",1,3));
mapSegment("E2EDP01","pmene","EA");
mapSegment("E2EDP01","vprei",getInputComp(i,"LIN:PRI",1,2));
mapSegment("E2EDP01","matnr",getInputComp(i,"LIN",3,1));
mapSegment("E2EDP01","matnr_external",getInputComp(i,"LIN",3,1));
commitSegment("E2EDP01");

segnum++;
hlevel++;
mapSegment("E2EDP02","mandt",mandt);
mapSegment("E2EDP02","docnum",docnum);
mapSegment("E2EDP02", "segnum", padNumber(segnum,6));
mapSegment("E2EDP02", "psgnum", padNumber(segnum,6));
mapSegment("E2EDP02", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDP02", "qualf", "001");
mapSegment("E2EDP02","belnr",getInputComp(i,"LIN:QTY",1,2));
commitSegment("E2EDP02");

segnum++;
hlevel++;
mapSegment("E2EDP20","mandt",mandt);
mapSegment("E2EDP20","docnum",docnum);
mapSegment("E2EDP20", "segnum", padNumber(segnum,6));
mapSegment("E2EDP20", "psgnum", padNumber(segnum,6));
mapSegment("E2EDP20", "hlevel", padNumber(hlevel,2));
mapSegment("E2EDP20", "wmeng", getInputComp(i,"LIN:QTY",1,2));
commitSegment("E2EDP20");

segnum++;
hlevel++;
mapSegment("E2EDP19","mandt",mandt);
mapSegment("E2EDP19","docnum",docnum);
mapSegment("E2EDP19", "segnum", padNumber(segnum,6));
mapSegment("E2EDP19", "psgnum", padNumber(psgnum,6));
mapSegment("E2EDP19", "hlevel", padNumber(hlevel,2));
if (i == 0) {
mapSegment("E2EDP19", "qualf", "001");
mapSegment("E2EDP19", "idtnr", getInputComp(i,"LIN",3,1));
} else {
mapSegment("E2EDP19", "qualf", "002");
mapSegment("E2EDP19", "idtnr", getInputComp(i,"LIN",3,1));  
}
mapSegment("E2EDP19", "ktext", getInputComp(i,"LIN:IMD",3,4));
commitSegment("E2EDP19");
}

// end mapping

