import com.blueseer.pur.purData;
import com.blueseer.utl.OVData;
import com.blueseer.inv.invData;
import com.blueseer.utl.EDData;


    setReference(getInput("BIG","e02")); // must be ran after mappedInput
   
    isDBWrite(c);// optional...unless this map is writing to internal database tables (orders, etc)
    
    //since this is a DB entry map, create class object specific to inbound doctype (edi850, edi824, etc)
    edi810i e = new edi810i(getInputISA(6).trim(), getInputISA(8).trim(), getInputGS(2), getInputGS(3), c[4], getInputISA(9), c[1], c[6]);   
      
    //optional...set some global variables as necessary
    String  now = now();
        

    // begin mapping
    
	           
       e.setVendNbr(getInput("REF","1:VN",2));
       e.setInvoiceNumber(getInput("BIG",2));
       e.setInvoiceDate(getInput("BIG",1));
       e.setPONumber(getInput("BIG",4));
       e.setTPID(getInputISA(6).trim()); 
       e.setTDSAMT(getInput("TDS",1));

      

       
      purData.po_mstr po = purData.getPOMstr(new String[]{e.getPONumber()});
      if (po.po_nbr().isEmpty()) {
          EDData.writeEDILog(c, "ERROR", "810 Purchase Order is unknown: " + e.getPONumber());
      }
      
   /* Now the Detail LOOP  */ 
       /* Item Loop */
    int itemcount = getGroupCount("IT1");
    int itemLoopCount = 0;
    int totalqty = 0;
    int i = 0;
    for (i = 1; i <= itemcount; i++) {
        String[] a = new String[e.DetFieldsCount810i];
        e.detailArray.add(e.initDetailArray(a));   // INITIATE Detail ArrayList
        itemLoopCount++;
        totalqty += Integer.valueOf(getInput(i,"IT1",4));
        e.setDetQty(i-1, getInput(i,"IT1",4));
        e.setDetPrice(i-1, getInput(i,"IT1",2));
        e.setDetLine(i-1,getInput(i,"IT1",1));
        if (getInput(i,"IT1",6).equals("VP") || getInput(i,"IT1",6).equals("VN")) {
         e.setDetItem(i-1,getInput(i,"IT1",7));
        } else if (getInput(i,"IT1",8).equals("VP") || getInput(i,"IT1",8).equals("VN")) {
         e.setDetItem(i-1,getInput(i,"IT1",9));   
        } else {
         e.setDetItem(i-1,"UNKNOWN");   
        }
        
    }
    /* end of item loop */
        
        mappedInput.clear();
        
         /* Load Invoice */
         /* call processDB ONLY if the output is direction of DataBase Internal */
        if (! isError) {
         processDB(c,com.blueseer.edi.EDI.createVoucherFrom810i(e, c)); 
        }

