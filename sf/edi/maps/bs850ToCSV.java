// start mapping

// number of rows created equals number of line items
// header info is repeated for each item 

var linecount = getGroupCount("PO1");
for (int i = 1; i <= linecount; i++) {
mapSegment("ROW","f1",getInput("BEG",3));
mapSegment("ROW","f2",getInput("N1","1:ST",4));
mapSegment("ROW","f3",getInput("N1","1:ST",2));
mapSegment("ROW","f4",getInput("N1","1:BT",4));
mapSegment("ROW","f5",getInput("N1","1:BT",2));
mapSegment("ROW","f6",getInput(i,"PO1",7));
mapSegment("ROW","f7",getInput(i,"PO1",9));
mapSegment("ROW","f8",getInput(i,"PO1",2));
mapSegment("ROW","f9",getInput(i,"PO1",3));
mapSegment("ROW","f10",getInput(i,"PO1",4));
mapSegment("ROW","f11",getInput(i,"PO1:PID",5));
commitSegment("ROW");
}

// end mapping


