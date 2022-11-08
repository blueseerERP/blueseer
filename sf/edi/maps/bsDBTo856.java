import com.blueseer.ctr.cusData;
import com.blueseer.shp.shpData;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;

     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String shipper = doc.get(0).toString();

		    String  now = now();
		    int i = 0;
		    int hlcounter = 0;
		    int itemLoopCount = 0;
		    double totalqty = 0;    
    String[] h = shpData.getShipperHeader(shipper);  // 13 elements...see declaration
    
     /* Begin Mapping Segments */ 
    mapSegment("BSN","e01","00");
    mapSegment("BSN","e02",shipper);
    mapSegment("BSN","e03",now.substring(0,8));
    mapSegment("BSN","e04",now.substring(8,12));
    commitSegment("BSN");
    
     hlcounter++;   
        mapSegment("HL","e01", snum(hlcounter));
        mapSegment("HL","e03","S");
        mapSegment("HL","e04","1");
        commitSegment("HL");

        mapSegment("TD1","e01", "PCS25");
        mapSegment("TD1","e02","0");
        mapSegment("TD1","e06","A3");
        mapSegment("TD1","e07","0");
        mapSegment("TD1","e08","LB");
        commitSegment("TD1");

        mapSegment("TD5","e02", "2");
        mapSegment("TD5","e03",h[8]);
        mapSegment("TD5","e05",h[8]);
        mapSegment("TD5","e06","CC");
        commitSegment("TD5");

        mapSegment("REF","e01", "BM");
        mapSegment("REF","e02",h[7]);
        commitSegment("REF");

        mapSegment("REF","e01", "CN");
        mapSegment("REF","e02",shipper);
        commitSegment("REF");

        mapSegment("DTM","e01", "011");
        mapSegment("DTM","e02",h[5].replace("-",""));
        commitSegment("DTM");

        // addresses
        String[] shipaddr = cusData.getShipAddressInfo(h[0], h[1]);
        mapSegment("N1","e01", "ST");
        mapSegment("N1","e02",shipaddr[1]);
        mapSegment("N1","e03","92");
        mapSegment("N1","e04",shipaddr[0]);
        commitSegment("N1");
        mapSegment("N3","e01",shipaddr[2]);
        commitSegment("N3");
        mapSegment("N4","e01",shipaddr[5]);
        mapSegment("N4","e02",shipaddr[6]);
        mapSegment("N4","e03",shipaddr[7]);
        commitSegment("N4");
       
	

        // Item Loop 
        // item, custitem, qty, po, cumqty, listprice, netprice, reference, sku, desc
         ArrayList<String[]> lines = shpData.getShipperLines(shipper);
              for (String[] d : lines) {
                itemLoopCount++;
                totalqty += Double.valueOf(d[2]);

                // do PRF once...this map is one PRF only
                if (itemLoopCount == 1) {
                hlcounter++;
                mapSegment("HL","e01", snum(hlcounter));
                mapSegment("HL","e02","1");
                mapSegment("HL","e03","O");
                mapSegment("HL","e04","1");
                commitSegment("HL");
                mapSegment("PRF","e01", d[3]);
                commitSegment("PRF");
                }
                
                hlcounter++;
                mapSegment("HL","e01", snum(hlcounter));
                mapSegment("HL","e02","2");
                mapSegment("HL","e03","I");
                mapSegment("HL","e04","1");
                commitSegment("HL");


                mapSegment("LIN","e04","VN");
                mapSegment("LIN","e05",d[0]);
                if (! d[1].isEmpty()) {
                  mapSegment("LIN","e06","BP");
                  mapSegment("LIN","e07",d[1]);
                } 
                commitSegment("LIN");


                if (BlueSeerUtils.isParsableToDouble(d[2])) {
                    mapSegment("SN1","e02",formatNumber(Double.valueOf(d[2]),"0"));
                } else {
                    mapSegment("SN1","e02","0");	
                }
                mapSegment("SN1","e03","PC");
                commitSegment("SN1");

                mapSegment("PO4","e01","1");
                mapSegment("PO4","e02",d[2]);
                mapSegment("PO4","e03","PC");
                commitSegment("PO4");


                mapSegment("PID","e01","F");
                mapSegment("PID","e05",d[9]);
                commitSegment("PID");

        }

            /* end of item loop */

        mapSegment("CTT","e01",snum(hlcounter));
        mapSegment("CTT","e02",snum(totalqty));
        commitSegment("CTT");

