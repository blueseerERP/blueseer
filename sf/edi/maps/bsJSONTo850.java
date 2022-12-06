// Example methods
// mapSegment("SEGMENT","FIELD",getInput("BEG",3));
// commitSegment("SEGMENT");

mapSegment("BEG","e01","00");
mapSegment("BEG","e03",getInput("order","ponumber"));
commitSegment("BEG");

int itemcount = getGroupCount("order:items");
for (int i = 1; i <= itemcount; i++) {
mapSegment("PO1","e06","SK");
mapSegment("PO1","e07",getInput(i, "order:items","skunumber"));
commitSegment("PO1");
}
