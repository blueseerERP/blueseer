
mapSegment("BEG","e01","00");
mapSegment("BEG","e02","NE");
mapSegment("BEG","e03",getInput("order:header",1));
mapSegment("BEG","e05",getInput("order:header",2).replace("-",""));
commitSegment("BEG");

mapSegment("REF","e01","VN");
mapSegment("REF","e02",getInput("order:header",6));
commitSegment("REF");

int itemcount = getGroupCount("order:detail:item");
for (int i = 1; i <= itemcount; i++) {
mapSegment("PO1","e01",getInput(i, "order:detail:item","linenumber"));
mapSegment("PO1","e02",getInput(i, "order:detail:item","quantity"));
mapSegment("PO1","e03","EA");
mapSegment("PO1","e04",getInput(i, "order:detail:item","listprice"));
mapSegment("PO1","e06","VN");
mapSegment("PO1","e07",getInput(i, "order:detail:item","itemnumber"));
mapSegment("PO1","e08","SK");
mapSegment("PO1","e09",getInput(i, "order:detail:item","skunumber"));
commitSegment("PO1");
}

