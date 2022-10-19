String status = getInput("BAK","e02");
String po = getInput("BAK","e03");
setReference(getInput("BAK","e03")); // must be ran after mappedInput
        
isDBWrite(c);
        
       
mappedInput.clear();
        
        
/* call processDB ONLY if the output is direction of DataBase Internal */
processDB(c,com.blueseer.pur.purData.updatePOFromAck(po, status));
