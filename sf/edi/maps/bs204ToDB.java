import com.blueseer.ctr.cusData;
import com.blueseer.utl.OVData;
import com.blueseer.inv.invData;
import com.blueseer.utl.EDData;


    setReference(getInput("B2","e04")); // must be ran after mappedInput
   
    isDBWrite(c);// optional...unless this map is writing to internal database tables (orders, etc)
    
    //since this is a DB entry map, create class object specific to inbound doctype (edi850, edi824, etc)
    edi204 e = new edi204(getInputISA(6).trim(), getInputISA(8).trim(), getInputGS(2), getInputGS(3), c[4], getInputISA(9), c[1], c[6]); // mandatory class creation
    
    //optional...set some global variables as necessary
    String  now = now();
    int i = 0; 
        String custfo = ""; 
        String origfo = "";
        String purpose = "";
        int S5Count = 0;
        int linecount = 0;
        boolean S5 = false;

    // begin mapping
    
	e.setCustFO(getInput("B2","e04"));
       e.setCarrier(getInput("B2","e02"));
       // lets set tpid and cust at this point with ISA sender ID and cross reference lookup into cmedi_mstr
       e.setTPID(getInputISA(6).trim()); 
       e.setCust(EDData.getEDIXrefIn(getInputISA(6), getInputGS(2), "BT", ""));




        // if cancellation...cancel original freight order based on custfo number...if status is not 'InTransit'
       purpose = getInput("B2A","e01");
       if (purpose.equals("01")) {
       EDData.CancelFOFrom204i(getInput("B2","e04"));
       EDData.writeEDILog(c, "INFO", "204 Cancel");
       }

       if (purpose.equals("04")) {
       origfo = OVData.getFreightOrderNbrFromCustFO(getInput("B2","e04"));
          if (origfo.isEmpty()) {
              EDData.writeEDILog(c, "ERROR", "204 Update Orig Not Found");
          }
       EDData.writeEDILog(c, "INFO", "204 Update Not Implemented");
       }

       if (getInput("L11","e02").toUpperCase().equals("ZI")) {
           e.setRef(getInput("L11","e01"));
       }

       S5 = true;  
       S5Count++;
       linecount = S5Count - 1; // for base zero array
       String[] a = new String[e.DetFieldsCount204i];
       e.detailArray.add(e.initDetailArray(a)); 
       e.setDetLine(linecount, getInput("S5","e01"));
       e.setDetType(linecount, getInput("S5","e02"));              
        if ( getInput("G62","e01").equals("68") || getInput("G62","e01").equals("70")) {
        e.setDetDelvDate(linecount, convertDate("yyyyMMdd", getInput("G62","e02")));
          if ( ! getInput("G62","e04").isEmpty() ) {
          e.setDetDelvTime(linecount, getInput("G62","e04"));
          }
        } 
        if ( getInput("G62","e01").equals("69") || getInput("G62","e01").equals("78")) {
           e.setDetShipDate(linecount, convertDate("yyyyMMdd", getInput("G62","e02")));
           if ( ! getInput("G62","e04").isEmpty() ) {
          e.setDetShipTime(linecount, getInput("G62","e04"));
          }
        }  
        e.setDetAddrName(linecount, getInput("N1","e02"));
        if (! getInput("N1","e04").isEmpty()) {
          e.setDetAddrCode(linecount, getInput("N1","e04"));
        }
        e.setDetAddrLine1(linecount, getInput("N3","e01"));
        e.setDetAddrCity(linecount, getInput("N4","e01"));
        e.setDetAddrState(linecount, getInput("N4","e02"));
        e.setDetAddrZip(linecount, getInput("N4","e03"));
        e.setWeight(getInput("L3","e01"));

    // mapping end
    
    mappedInput.clear();

     /* Load Sales Order */
     /* call processDB ONLY if the output is database write */
    processDB(c,com.blueseer.edi.EDI.createCFOFrom204(e, c));

