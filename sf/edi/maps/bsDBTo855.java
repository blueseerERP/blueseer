import com.blueseer.ctr.cusData;
import java.util.ArrayList;
import com.blueseer.edi.EDI;
import static com.blueseer.ord.ordData.getSOMstrHeaderEDI;
import static com.blueseer.ord.ordData.getSOMstrdetailsEDI;
import com.blueseer.pur.purData;
import com.blueseer.shp.shpData;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.OVData;
import java.io.IOException;
import java.text.DecimalFormat;


     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
    
        
        
    String[] h = getSOMstrHeaderEDI(key);  // 13 elements...see declaration 
    // so, po, cust, ship, site, type, orddate, duedate, shipvia, rmks, cur, status
    
    if (h[0] == null || h[0].isEmpty()) {
    setError("Cannot find order number:" + key);
    return error; 
    }
    
     /* Begin Mapping Segments */ 
    String status = "AD";  // accept by default
    String itemstatus = "IA"; // accept by default
    if (h[11].equals("rejected")) {
        status = "RJ";
        itemstatus = "IR";  // if one then all
    }
    mapSegment("BAK","e01","00");
    mapSegment("BAK","e02",status);
    mapSegment("BAK","e03",h[1]);
    mapSegment("BAK","e05",h[6].replace("-", ""));
    commitSegment("BAK");
    
    mapSegment("REF","e01","OR");
    mapSegment("REF","e02",h[0]);
    commitSegment("REF");
    
    String[] shipaddr = cusData.getShipAddressInfo(h[2], h[3]);
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
    
    mapSegment("N1","e01","VN");
    mapSegment("N1","e02",OVData.getDefaultSiteName());
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",h[4]);
    commitSegment("N1");
    
    mapSegment("DTM","e01","067");
    mapSegment("DTM","e02",h[7].replace("-", ""));
    commitSegment("DTM");    
    
               
        // detail
         int i = 0;
         int sumqty = 0;
         double sumamt = 0;
         // line, item, custitem, qty, price, uom, desc, custline, custuom, custprice
         ArrayList<String[]> lines = getSOMstrdetailsEDI(key);
              for (String[] d : lines) {
                  i++;
                                    
                  sumqty = sumqty + Integer.valueOf(d[3]);
                  sumamt = sumamt + (bsParseDouble(d[3]) * bsParseDouble(d[4]));
                  
                mapSegment("PO1","e01",d[7]);
                mapSegment("PO1","e02",d[3]);
                mapSegment("PO1","e03",d[5]);
                mapSegment("PO1","e04",currformatDouble(bsParseDouble(d[4])));
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
                
                mapSegment("ACK","e01",itemstatus);
                mapSegment("ACK","e02",d[3]);
                mapSegment("ACK","e03",d[5]);
                mapSegment("ACK","e04","076");
                mapSegment("ACK","e05",h[7].replace("-", ""));
                commitSegment("ACK");
              }
         
         mapSegment("CTT","e01",String.valueOf(i));
         commitSegment("CTT");
        
