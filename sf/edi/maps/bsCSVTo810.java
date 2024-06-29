import com.blueseer.utl.BlueSeerUtils;
var today = now();

mapSegment("BIG","e01",getRow(2,3));
    mapSegment("BIG","e02",getRow(2,1));
    mapSegment("BIG","e03",getRow(2,4));
    mapSegment("BIG","e04",getRow(2,2));
    commitSegment("BIG");

mapSegment("REF","e01","VN");
    mapSegment("REF","e02",getRow(2,7));
    commitSegment("REF");


mapSegment("DTM","e01","002");
mapSegment("DTM","e02",addDays(today.substring(0,8),"yyyyMMdd",7));
commitSegment("DTM");

// address billto
mapSegment("N1","e01","BT");
mapSegment("N1","e02",getRow(2,6));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getRow(2,5));
commitSegment("N1");

// address remitto
mapSegment("N1","e01","RI");
mapSegment("N1","e02",getRow(2,8));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getRow(2,7));
commitSegment("N1");

// items
var rows = getRowCount();
var lines = 0;
var qty = 0;
var sum = 0.00;
for (int i = 2; i <= rows ; i++) {

lines++;  // count lines
qty += Integer.valueOf(getRow(i,15));  // sum total qty
sum += Double.valueOf(getRow(i,15)) * Double.valueOf(getRow(i,16));
mapSegment("IT1","e01",String.valueOf(i));
                mapSegment("IT1","e02",getRow(i,15));
                mapSegment("IT1","e03","EA");
                mapSegment("IT1","e04",formatNumber(BlueSeerUtils.bsParseDouble(getRow(i,16)),"2"));
                mapSegment("IT1","e06","IN");
                mapSegment("IT1","e07",getRow(i,13));
                mapSegment("IT1","e08","VP");
                mapSegment("IT1","e09",getRow(i,14));
                commitSegment("IT1");

}

mapSegment("TDS","e01",formatNumber((sum * 100),"0"));
         commitSegment("TDS");
         
         mapSegment("ISS","e01",snum(qty));
         mapSegment("ISS","e02","EA");
         mapSegment("ISS","e03",snum(qty));
         mapSegment("ISS","e04","LB");
         commitSegment("ISS");


mapSegment("CTT","e01",snum(lines));
mapSegment("CTT","e02",snum(qty));
commitSegment("CTT");
