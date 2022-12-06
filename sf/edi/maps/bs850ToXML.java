// Example methods
// mapSegment("SEGMENT","FIELD",getInput("BEG",3));
// commitSegment("SEGMENT");

mapSegment("header","purchaseordernumber",getInput("BEG",3));
mapSegment("header","type","EDI");
mapSegment("header","senderid",getInputISA(6).trim());
mapSegment("header","receiverid",getInputISA(8).trim());
commitSegment("header");

int addrcount = getGroupCount("N1");
for (int i = 1; i <= addrcount; i++) {
  if (getInput(i,"N1",1).equals("ST")) {
  mapSegment("address","type","ship-to");
  mapSegment("address","addressname",getInput(i,"N1",2));
  mapSegment("address","addressline1",getInput(i,"N1:N3",1));
  mapSegment("address","addresscity",getInput(i,"N1:N4",1));
  mapSegment("address","addressstate",getInput(i,"N1:N4",2));
  mapSegment("address","addresszip",getInput(i,"N1:N4",3));
  commitSegment("address");
  }
}

int count = getGroupCount("PO1");
for (int i = 1; i <= count; i++) {
mapSegment("item","line",getInput(i,"PO1",1));
mapSegment("item","itemnumber",getInput(i,"PO1",7));
mapSegment("item","uom",getInput(i,"PO1",3));
mapSegment("item","description",getInput(i,"PO1:PID",5));
mapSegment("item","skunumber",getInput(i,"PO1",9));
mapSegment("item","orderquantity",getInput(i,"PO1",2));
mapSegment("item","listprice",getInput(i,"PO1",4));
mapSegment("item","netprice",getInput(i,"PO1",4));
mapSegment("item","discount","0");
commitSegment("item");
}


