import com.blueseer.shp.shpData;
import com.blueseer.ctr.cusData;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;

     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
    
        
    String[] h = shpData.getShipperHeader(key);  // 13 elements...see declaration
    
     /* Begin Mapping Segments */ 
    mapSegment("BIG","e01",h[5].replace("-", ""));
    mapSegment("BIG","e02",key);
    mapSegment("BIG","e03",h[4].replace("-", ""));
    mapSegment("BIG","e04",h[3]);
    commitSegment("BIG");
    
    mapSegment("REF","e01","ST");
    mapSegment("REF","e02",h[1]);
    commitSegment("REF");
    
    mapSegment("N1","e01","RE");
    mapSegment("N1","e02",OVData.getDefaultSiteName());
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",h[1]);
    commitSegment("N1");
    
    mapSegment("DTM","e01","011");
    mapSegment("DTM","e02",h[5].replace("-", ""));
    commitSegment("DTM");    
    
               
        // detail
         int i = 0;
         int sumqty = 0;
         double sumamt = 0;
         double sumlistamt = 0;
         double sumamtTDS = 0;
         String sku = "";
         // item, custitem, qty, po, cumqty, listprice, netprice, reference, sku, desc
         ArrayList<String[]> lines = shpData.getShipperLines(key);
              for (String[] d : lines) {
                  i++;
                  if (d[8].isEmpty() && d[8] != null) {
                      sku = cusData.getCustAltItem(h[0], d[0]);
                  }
                                    
                  sumqty = sumqty + Integer.valueOf(d[2]);
                  sumamt = sumamt + (BlueSeerUtils.bsParseDouble(d[2]) * BlueSeerUtils.bsParseDouble(d[6]));
                  sumlistamt = sumlistamt + (BlueSeerUtils.bsParseDouble(d[2]) * BlueSeerUtils.bsParseDouble(d[5]));
                  
                mapSegment("IT1","e01",String.valueOf(i));
                mapSegment("IT1","e02",d[2]);
                mapSegment("IT1","e03","EA");
                mapSegment("IT1","e04",formatNumber(BlueSeerUtils.bsParseDouble(d[5]),"2"));
                mapSegment("IT1","e06","IN");
                mapSegment("IT1","e07",sku);
                mapSegment("IT1","e08","VP");
                mapSegment("IT1","e09",d[0]);
                commitSegment("IT1");
                  
              }
            sumamtTDS = (sumamt * 100);
            
            // trailer
         mapSegment("TDS","e01",formatNumber(sumamtTDS,"2"));
         commitSegment("TDS");
         
         mapSegment("ISS","e01",snum(sumqty));
         mapSegment("ISS","e02","EA");
         mapSegment("ISS","e03",snum(sumqty));
         mapSegment("ISS","e04","LB");
         commitSegment("ISS");
         
         mapSegment("CTT","e01",snum(i));
         commitSegment("CTT");
        

