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
        String custfo = ""; 
        String origfo = "";
        String purpose = "";
        int eline = 0;
        

    // begin mapping
    
	e.setCustFO(getInput("B2","e04"));
       e.setCarrier(getInput("B2","e02"));
       // lets set tpid and cust at this point with ISA sender ID and cross reference lookup into cmedi_mstr
       e.setTPID(getInputISA(6).trim()); 
       e.setCust(EDData.getEDIXrefIn(getInputISA(6).trim(), getInputGS(2), "BT", getInputGS(3)));




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

// S5 Stop details
var linecount = getGroupCount("S5");

for (int i = 1; i <= linecount; i++) {
       String[] a = new String[e.DetFieldsCount204i];
       e.detailArray.add(e.initDetailArray(a)); 
       eline = i - 1;  // base zero
       e.setDetLine(eline, getInput(i,"S5","e01"));
       e.setDetType(eline, getInput(i,"S5","e02"));              
        if ( getInput(i,"S5:G62","e01").equals("68") || getInput(i,"S5:G62","e01").equals("70")) {
        e.setDetDate(eline, convertDate("yyyyMMdd", getInput(i,"S5:G62","e02")));
          if ( ! getInput(i,"S5:G62","e04").isEmpty() ) {
          e.setDetTime1(eline, getInput(i,"S5:G62","e04"));
          }
        } 
        if ( getInput(i,"S5:G62","e01").equals("69") || getInput(i,"S5:G62","e01").equals("78")) {
           e.setDetDate(eline, convertDate("yyyyMMdd", getInput(i,"S5:G62","e02")));
           if ( ! getInput(i,"S5:G62","e04").isEmpty() ) {
          e.setDetTime1(eline, getInput(i,"S5:G62","e04"));
          }
        }  
        e.setDetAddrName(eline, getInput(i,"S5:N1","e02"));
        if (! getInput(i,"S5:N1","e04").isEmpty()) {
          e.setDetAddrCode(eline, getInput(i,"S5:N1","e04"));
        }
        e.setDetPhone(eline, getInput(i,"S5:N1:G61","e04"));
        e.setDetAddrLine1(eline, getInput(i,"S5:N1:N3","e01"));
        e.setDetAddrCity(eline, getInput(i,"S5:N1:N4","e01"));
        e.setDetAddrState(eline, getInput(i,"S5:N1:N4","e02"));
        e.setDetAddrZip(eline, getInput(i,"S5:N1:N4","e03"));
        e.setWeight(getInput("L3","e01"));
}
    // mapping end


   
    mappedInput.clear();

     /* Load Sales Order */
     /* call processDB ONLY if the output is database write */
    processDB(c,com.blueseer.edi.EDI.createCFOFrom204(e, c));

