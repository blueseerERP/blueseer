// Example methods


mapSegment("cXML","timestamp","2012-12-12T12:12:12");
mapSegment("cXML","version","1.25");
mapSegment("cXML","MeansDescriptionCode","1.25");
mapSegment("cXML","payloadID","999");
commitSegment("cXML");

 mapSegment("cXML:Header:From:Credential","Identity","XXX");
mapSegment("cXML:Header:From:Credential","domain","Company");
mapSegment("cXML:Header:From:Credential","foo","hoo");
 commitSegment("cXML:Header:From:Credential");


mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address","Name",getInput("N1","1:ST",2));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address","addressID",getInput("N1","1:ST",4));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address","isoCountryCode","US");
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address","ReturnData","en");
commitSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address");

mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address:PostalAddress","DeliverTo",getInput("N1","1:ST",2));
commitSegment("cXML:Request:OrderRequest:OrderRequestHeader:ShipTo:Address:PostalAddress");

mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","Name",getInput("N1","1:BT",2));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","addressID",getInput("N1","1:BT",4));
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","isoCountryCode","US");
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","ReturnData","en");
mapSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address","addressname","BOOHOO");
commitSegment("cXML:Request:OrderRequest:OrderRequestHeader:BillTo:Address");


