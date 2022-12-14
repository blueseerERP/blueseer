// Example methods
// mapSegment("SEGMENT","FIELD",getInput("BEG",3));
// commitSegment("SEGMENT");

mapSegment("order:header","purchaseordernumber",getInput("BEG",3));
mapSegment("order:header","type","EDI");
mapSegment("order:header","senderid",getInputISA(6).trim());
mapSegment("order:header","receiverid",getInputISA(8).trim());
commitSegment("order:header");

int addrcount = getGroupCount("N1");
for (int i = 1; i <= addrcount; i++) {
  if (getInput(i,"N1",1).equals("ST")) {
  mapSegment("order:address","type","ship-to");
  mapSegment("order:address","addressname",getInput(i,"N1",2));
  mapSegment("order:address","addressline1",getInput(i,"N1:N3",1));
  mapSegment("order:address","addresscity",getInput(i,"N1:N4",1));
  mapSegment("order:address","addressstate",getInput(i,"N1:N4",2));
  mapSegment("order:address","addresszip",getInput(i,"N1:N4",3));
  commitSegment("order:address");
  }
 if (getInput(i,"N1",1).equals("BT")) {
  mapSegment("order:address","type","bill-to");
  mapSegment("order:address","addressname",getInput(i,"N1",2));
  mapSegment("order:address","addressline1",getInput(i,"N1:N3",1));
  mapSegment("order:address","addresscity",getInput(i,"N1:N4",1));
  mapSegment("order:address","addressstate",getInput(i,"N1:N4",2));
  mapSegment("order:address","addresszip",getInput(i,"N1:N4",3));
  commitSegment("order:address");
  }
}

int count = getGroupCount("PO1");
for (int i = 1; i <= count; i++) {
mapSegment("order:detail:item","line",getInput(i,"PO1",1));
mapSegment("order:detail:item","itemnumber",getInput(i,"PO1",7));
mapSegment("order:detail:item","uom",getInput(i,"PO1",3));
mapSegment("order:detail:item","description",getInput(i,"PO1:PID",5));
mapSegment("order:detail:item","skunumber",getInput(i,"PO1",9));
mapSegment("order:detail:item","orderquantity",getInput(i,"PO1",2));
mapSegment("order:detail:item","listprice",getInput(i,"PO1",4));
mapSegment("order:detail:item","netprice",getInput(i,"PO1",4));
mapSegment("order:detail:item","discount","0");
commitSegment("order:detail:item");
}


