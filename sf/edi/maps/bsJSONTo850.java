//GlobalDebug = true;
mapSegment("BEG","e01","00");
mapSegment("BEG","e02","NE");
mapSegment("BEG","e03",getInput("order","orderid"));
mapSegment("BEG","e05",getInput("order","orderdate").replace("-",""));
commitSegment("BEG");

mapSegment("CUR","e01",getInput("order","currency"));
commitSegment("CUR");

mapSegment("REF","e01","XX");
mapSegment("REF","e02",getInput("order","reference"));
commitSegment("REF");

int addrcount = getGroupCount("addresses:address");
for (int i = 1; i <= addrcount; i++) {

if (getInput(i,"addresses:address","type").equals("billto")) {
mapSegment("N1","e01","BT");
mapSegment("N1","e02",getInput(i,"addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if billto

if (getInput(i,"addresses:address","type").equals("shipto")) {
mapSegment("N1","e01","ST");
mapSegment("N1","e02",getInput(i,"addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if shipto

}

int itemcount = getGroupCount("items:item");
int total = 0;
for (int i = 1; i <= itemcount; i++) {
total += Double.valueOf(getInput(i, "items:item","orderquantity"));
mapSegment("PO1","e01",getInput(i, "items:item","line"));
mapSegment("PO1","e02",getInput(i, "items:item","orderquantity"));
mapSegment("PO1","e03",getInput(i, "items:item","uom"));
mapSegment("PO1","e04",getInput(i, "items:item","listprice"));
mapSegment("PO1","e06","SK");
mapSegment("PO1","e07",getInput(i, "items:item","skunumber"));
mapSegment("PO1","e08","VN");
mapSegment("PO1","e09",getInput(i, "items:item","itemnumber"));
mapSegment("PO1","e10","UP");
mapSegment("PO1","e11",getInput(i, "items:item","upcnumber"));
commitSegment("PO1");
}

mapSegment("CTT","e01",snum(itemcount));
mapSegment("CTT","e02",snum(total));
commitSegment("CTT");
