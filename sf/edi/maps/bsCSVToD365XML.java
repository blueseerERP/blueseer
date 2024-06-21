mapSegment("Document:SalesOrderHeaderV2Entity","SALESORDERNUMBER",getRow(2,1));
mapSegment("Document:SalesOrderHeaderV2Entity","CURRENCYCODE","USD");
mapSegment("Document:SalesOrderHeaderV2Entity","INVOICECUSTOMERACCOUNTNUMBER",getRow(2,2));
mapSegment("Document:SalesOrderHeaderV2Entity","ORDERINGCUSTOMERACCOUNTNUMBER",getRow(2,2));
commitSegment("Document:SalesOrderHeaderV2Entity");


var rows = getRowCount();
for (int i = 2; i <= rows ; i++) {
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","SALESORDERNUMBER",getRow(i,1));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","CUSTOMERSLINENUMBER",snum(i-1));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","ITEMNUMBER",getRow(i,6));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","SALESUNITSYMBOL",getRow(i,9));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","LINEDESCRIPTION",getRow(i,11));
mapSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity","ORDEREDSALESQUANTITY",getRow(i,8));
commitSegment("Document:SalesOrderHeaderV2Entity:SalesOrderLineEntity", true);
}


