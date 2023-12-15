import com.blueseer.frt.frtData;


String key = doc.get(0).toString();
 // now lets get order header info 
        // fonbr, ref, site, wh, date, remarks, carrier, carrier_assigned, reasoncode, custfo, type
        frtData.cfo_mstr cfo = frtData.getCFOMstr(new String[]{key,""});
        
        String status = "";
	String reason = "";
        
        if (cfo.cfo_orderstatus().equals("accepted") || cfo.cfo_orderstatus().equals("scheduled")) {
             status = "A";
         } else {
             status = "E";
	     reason = cfo.cfo_edireason();
         }
        
        mapSegment("B1","e01","WYXZ");
        mapSegment("B1","e02",key);
        mapSegment("B1","e03",now());
        mapSegment("B1","e04",status);
        mapSegment("B1","e06",reason);
        commitSegment("B1");
        
        mapSegment("L11","e01",cfo.cfo_custfonbr());
        mapSegment("L11","e02","DO");
        mapSegment("L11","e03",cfo.cfo_nbr());
        commitSegment("L11");
