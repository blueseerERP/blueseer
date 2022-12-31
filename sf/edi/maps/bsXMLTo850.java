//GlobalDebug = true;
//debuginput(mappedInput);
mapSegment("BEG","e01","00");
mapSegment("BEG","e02","NE");
mapSegment("BEG","e03",getInput("order:header",1));
mapSegment("BEG","e05",getInput("order:header",2).replace("-",""));
commitSegment("BEG");

mapSegment("REF","e01","VN");
mapSegment("REF","e02",getInput("order:header",6));
commitSegment("REF");

mapSegment("REF","e01","XX");
mapSegment("REF","e02",getInput("order:header",1));
commitSegment("REF");

int addrcount = getGroupCount("order:address");
for (int i = 1; i <= addrcount; i++) {
if (getInput(i,"order:address","type").equals("shipto")) {
mapSegment("N1","e01","ST");
mapSegment("N1","e02",getInput(i, "order:address","addressname"));
mapSegment("N1","e03","92");
mapSegment("N1","e04","99999");
commitSegment("N1");
mapSegment("N3","e01",getInput(i, "order:address","addressline1"));
commitSegment("N3");
mapSegment("N4","e01",getInput(i, "order:address","addresscity"));
mapSegment("N4","e02",getInput(i, "order:address","addressstate"));
mapSegment("N4","e03",getInput(i, "order:address","addresszip"));
commitSegment("N4");
}
if (getInput(i,"order:address","type").equals("billto")) {
mapSegment("N1","e01","BT");
mapSegment("N1","e02",getInput(i, "order:address","addressname"));
mapSegment("N1","e03","92");
mapSegment("N1","e04","11111");
commitSegment("N1");
mapSegment("N3","e01",getInput(i, "order:address","addressline1"));
commitSegment("N3");
mapSegment("N4","e01",getInput(i, "order:address","addresscity"));
mapSegment("N4","e02",getInput(i, "order:address","addressstate"));
mapSegment("N4","e03",getInput(i, "order:address","addresszip"));
commitSegment("N4");
}
}

int itemcount = getGroupCount("order:detail:item");
for (int i = 1; i <= itemcount; i++) {
mapSegment("PO1","e01",getInput(i, "order:detail:item","line"));
mapSegment("PO1","e02",getInput(i, "order:detail:item","quantity"));
mapSegment("PO1","e03","EA");
mapSegment("PO1","e04",getInput(i, "order:detail:item","listprice"));
mapSegment("PO1","e06","VN");
mapSegment("PO1","e07",getInput(i, "order:detail:item","itemnumber"));
mapSegment("PO1","e08","SK");
mapSegment("PO1","e09",getInput(i, "order:detail:item","skunumber"));
commitSegment("PO1");
mapSegment("PID","e01","F");
mapSegment("PID","e05",getInput(i, "order:detail:item","description"));
commitSegment("PID");
}

