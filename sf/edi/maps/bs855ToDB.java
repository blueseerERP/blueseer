
import com.blueseer.ctr.cusData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDI.*;
import static com.blueseer.utl.BlueSeerUtils.convertDateFormat;
import com.blueseer.edi.EDI;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.writeEDILog;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

        String status = getInput("BAK","e02");
        String po = getInput("BAK","e03");
        setReference(getInput("BAK","e03")); // must be ran after mappedInput
       // debuginput(mappedInput);  // for debug purposes
        
         isDBWrite(c);
        
       
        mappedInput.clear();
        
        
         /* call processDB ONLY if the output is direction of DataBase Internal */
        processDB(c,com.blueseer.pur.purData.updatePOFromAck(po, status));
        
