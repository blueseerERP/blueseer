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
  mapSegment("addresses:address","type","ship-to");
  mapSegment("addresses:address","name",getInput(i,"N1",2));
  mapSegment("addresses:address","line1",getInput(i,"N1:N3",1));
  mapSegment("addresses:address","city",getInput(i,"N1:N4",1));
  mapSegment("addresses:address","state",getInput(i,"N1:N4",2));
  mapSegment("addresses:address","zip",getInput(i,"N1:N4",3));
  commitSegment("addresses:address");
  }
if (getInput(i,"N1",1).equals("BT")) {
  mapSegment("addresses:address","type","bill-to");
  mapSegment("addresses:address","name",getInput(i,"N1",2));
  mapSegment("addresses:address","line1",getInput(i,"N1:N3",1));
  mapSegment("addresses:address","city",getInput(i,"N1:N4",1));
  mapSegment("addresses:address","state",getInput(i,"N1:N4",2));
  mapSegment("addresses:address","zip",getInput(i,"N1:N4",3));
  commitSegment("addresses:address");
  }
}

int count = getGroupCount("PO1");
for (int i = 1; i <= count; i++) {
mapSegment("items:item","line",getInput(i,"PO1",1));
mapSegment("items:item","itemnumber",getInput(i,"PO1",7));
mapSegment("items:item","uom",getInput(i,"PO1",3));
mapSegment("items:item","description",getInput(i,"PO1:PID",5));
mapSegment("items:item","skunumber",getInput(i,"PO1",9));
mapSegment("items:item","orderquantity",getInput(i,"PO1",2));
mapSegment("items:item","listprice",getInput(i,"PO1",4));
mapSegment("items:item","netprice",getInput(i,"PO1",4));
mapSegment("items:item","discount","0");
commitSegment("items:item");
}



