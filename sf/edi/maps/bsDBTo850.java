import com.blueseer.pur.purData;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;


     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
    
        
        
    String[] h = purData.getPOMstrHeaderEDI(key);  // 13 elements...see declaration
    // po, vend, ship, site, type, orddate, duedate, shipvia, rmks, cur
    
     /* Begin Mapping Segments */ 
    mapSegment("BEG","e01","00");
    mapSegment("BEG","e02","NE");
    mapSegment("BEG","e03",key);
    mapSegment("BEG","e05",h[5].replace("-", ""));
    commitSegment("BEG");
    
    mapSegment("REF","e01","ST");
    mapSegment("REF","e02",h[3]);
    commitSegment("REF");
    
    mapSegment("N1","e01","ST");
    mapSegment("N1","e02",OVData.getDefaultSiteName());
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",h[3]);
    commitSegment("N1");
    
    mapSegment("DTM","e01","002");
    mapSegment("DTM","e02",h[6].replace("-", ""));
    commitSegment("DTM");    
    
               
        // detail
         int i = 0;
         int sumqty = 0;
         double sumamt = 0;
         // line, item, venditem, qty, price, uom, desc
         ArrayList<String[]> lines = purData.getPOMstrdetailsEDI(key);
              for (String[] d : lines) {
                  i++;
                                    
                  sumqty = sumqty + Integer.valueOf(d[3]);
                  sumamt = sumamt + (BlueSeerUtils.bsParseDouble(d[3]) * BlueSeerUtils.bsParseDouble(d[4]));
                  
                mapSegment("PO1","e01",d[0]);
                mapSegment("PO1","e02",d[3]);
                mapSegment("PO1","e03",d[5]);
                mapSegment("PO1","e04",formatNumber(BlueSeerUtils.bsParseDouble(d[4]),"2"));
                mapSegment("PO1","e06","BP");
                mapSegment("PO1","e07",d[1]);
                if (! d[2].isEmpty()) {
                mapSegment("PO1","e08","VP");
                mapSegment("PO1","e09",d[2]);
                }
                commitSegment("PO1");
                
                mapSegment("PID","e01","F");
                mapSegment("PID","e05",d[6]);
                commitSegment("PID");
              }
         
         mapSegment("CTT","e01",snum(i));
         commitSegment("CTT");
        

