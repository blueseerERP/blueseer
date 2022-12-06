// Example methods
// mapSegment("SEGMENT","FIELD",getInput("BEG",3));
// commitSegment("SEGMENT");

mapSegment("order","ordernumber",getInput("BEG",3));
mapSegment("order","site","MYSITE");
mapSegment("order","orderdate",getInput("BEG",5));
mapSegment("order","type","EDI");
mapSegment("order","currency","USD");
mapSegment("order","duedate",getInput("DTM","1:002",2));
commitSegment("order");

int addrcount = getGroupCount("N1");
for (int i = 1; i <= addrcount; i++) {
  if (getInput(i,"N1",1).equals("ST")) {
  mapSegment("addresses","type","ship-to");
  mapSegment("addresses","name",getInput(i,"N1",2));
  mapSegment("addresses","line1",getInput(i,"N1:N3",1));
  mapSegment("addresses","city",getInput(i,"N1:N4",1));
  mapSegment("addresses","state",getInput(i,"N1:N4",2));
  mapSegment("addresses","zip",getInput(i,"N1:N4",3));
  commitSegment("addresses");
  }
}

int count = getGroupCount("PO1");
for (int i = 1; i <= count; i++) {
mapSegment("items","line",getInput(i,"PO1",1));
mapSegment("items","itemnumber",getInput(i,"PO1",7));
mapSegment("items","uom",getInput(i,"PO1",3));
mapSegment("items","description",getInput(i,"PO1:PID",5));
mapSegment("items","skunumber",getInput(i,"PO1",9));
mapSegment("items","orderquantity",getInput(i,"PO1",2));
mapSegment("items","listprice",getInput(i,"PO1",4));
mapSegment("items","netprice",getInput(i,"PO1",4));
mapSegment("items","discount","0");
commitSegment("items");
}



