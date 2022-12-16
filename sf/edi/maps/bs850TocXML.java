// Example methods

//GlobalDebug = true;
//debuginput(mappedInput);

mapSegment("cXML","timestamp","2012-12-12T12:12:12");
mapSegment("cXML","version","1.25");
mapSegment("cXML","MeansDescriptionCode","1.25");
mapSegment("cXML","payloadID","999");
commitSegment("cXML");

mapSegment("cXML:Header:From:Credential","Identity",getInputISA(6).trim());
mapSegment("cXML:Header:From:Credential","domain","ACME Trading Co");
mapSegment("cXML:Header:From:Credential","title","commID");
commitSegment("cXML:Header:From:Credential");

mapSegment("cXML:Header:To:Credential","Identity",getInputISA(8).trim());
mapSegment("cXML:Header:To:Credential","domain","BlueSeerCo");
mapSegment("cXML:Header:To:Credential","title","commID");
commitSegment("cXML:Header:To:Credential");

int count = getGroupCount("N1");
for (int i = 1; i <= count; i++) {

if (getInput(i,"N1",1).equals("ST")) {
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address","Name",getInput(i,"N1",2));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address","addressID",getInput(i,"N1",4));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address","isoCountryCode","US");
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address","ReturnData","en");
commitSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address", true);
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address:PostalAddress","DeliverTo",getInput(i,"N1",2));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address:PostalAddress","Street",getInput(i,"N1:N3",1));
commitSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address:PostalAddress");
}

if (getInput(i,"N1",1).equals("BT")) {
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","Name",getInput(i,"N1",2));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","addressID",getInput(i,"N1",4));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","isoCountryCode","US");
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","ReturnData","en");
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","name","BOOHOO");
commitSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address", true);
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address:PostalAddress","DeliverTo",getInput(i,"N1",2));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address:PostalAddress","Street",getInput(i,"N1:N3",1));
commitSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address:PostalAddress");
}

} // for address loop

count = getGroupCount("PO1");
for (int i = 1; i <= count; i++) {
mapSegment("cXML:Request:OrderRequest:ItemOut","lineNumber",getInput(i,"PO1",1));
mapSegment("cXML:Request:OrderRequest:ItemOut","quantity",getInput(i,"PO1",2));
commitSegment("cXML:Request:OrderRequest:ItemOut", true);
mapSegment("cXML:Request:OrderRequest:ItemOut:ItemID","SupplierPartID",getInput(i,"PO1",7));
commitSegment("cXML:Request:OrderRequest:ItemOut:ItemID");
//mapSegment("cXML:Request:OrderRequest:ItemOut:ItemDetail","UnitOfMeasure",getInput(i,"PO1",3));
//mapSegment("cXML:Request:OrderRequest:ItemOut:ItemDetail","Description",getInput(i,"PO1:PID",5));
//mapSegment("cXML:Request:OrderRequest:ItemOut:ItemDetail:UnitPrice","Money",getInput(i,"PO1",4));


//commitSegment("cXML:Request:OrderRequest:ItemOut:ItemID");
//commitSegment("cXML:Request:OrderRequest:ItemOut:ItemDetail");
//commitSegment("cXML:Request:OrderRequest:ItemOut:ItemDetail:UnitPrice");
} // for item loop
