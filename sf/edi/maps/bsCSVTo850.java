var today = now();

mapSegment("BEG","e01","00");
mapSegment("BEG","e01","NE");
mapSegment("BEG","e03",getRow(2,1));
mapSegment("BEG","e05",today.substring(0,8));
commitSegment("BEG");

mapSegment("DTM","e01","002");
mapSegment("DTM","e02",addDays(today.substring(0,8),"yyyyMMdd",7));
commitSegment("DTM");

// address billto
mapSegment("N1","e01","BT");
mapSegment("N1","e02",getRow(2,3));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getRow(2,2));
commitSegment("N1");

// address shipto
mapSegment("N1","e01","ST");
mapSegment("N1","e02",getRow(2,5));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getRow(2,4));
commitSegment("N1");

// items
var rows = getRowCount();
var lines = 0;
var total = 0;
for (int i = 2; i <= rows; i++) {

lines++;  // count lines
total += Integer.valueOf(getRow(i,7));  // sum total qty

mapSegment("PO1","e01",snum(i - 1));
mapSegment("PO1","e02",getRow(i,7));
mapSegment("PO1","e03",getRow(i,8));
mapSegment("PO1","e04",getRow(i,9));
mapSegment("PO1","e06","VP");
mapSegment("PO1","e07",getRow(i,6));
commitSegment("PO1");
}

mapSegment("CTT","e01",snum(lines));
mapSegment("CTT","e02",snum(total));
commitSegment("CTT");
