mapSegment("Document:SalesOrderHeaderV2Entity","SALESORDERNUMBER",getInput("BEG",3));
mapSegment("Document:SalesOrderHeaderV2Entity","CURRENCYCODE","USD");
mapSegment("Document:SalesOrderHeaderV2Entity","INVOICECUSTOMERACCOUNTNUMBER",getInput("N1","1:ST",4));
mapSegment("Document:SalesOrderHeaderV2Entity","ORDERINGCUSTOMERACCOUNTNUMBER",getInput("N1","1:BT",4));
commitSegment("Document:SalesOrderHeaderV2Entity");


int count = getGroupCount("PO1");
for (int i = 1; i <= count; i++) {
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","SALESORDERNUMBER",getInput("BEG",3));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","CUSTOMERSLINENUMBER",getInput(i,"PO1",1));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","ITEMNUMBER",getInput(i,"PO1",7));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","SALESUNITSYMBOL",getInput(i,"PO1",3));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","LINEDESCRIPTION",getInput(i,"PO1:PID",5));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","ORDEREDSALESQUANTITY",getInput(i,"PO1",2));
commitSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity", true);
}
