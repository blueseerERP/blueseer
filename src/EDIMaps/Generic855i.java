/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package EDIMaps;

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


/**
 *
 * @author vaughnte
 */
public class Generic855i extends com.blueseer.edi.EDIMap {
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException {
   
        setControl(c); // as defined by EDI.initEDIControl() and EDIMap.setControl()
        if (isError) { return error;}  // check errors for master variables

        mappedInput = mapInput(c, doc, ISF);
        String status = getInput("BAK","e02");
        String po = getInput("BAK","e03");
        setReference(getInput("BAK","e03")); // must be ran after mappedInput
       // debuginput(mappedInput);  // for debug purposes
        
         isDBWrite(c);
        
       
        mappedInput.clear();
        
        
         /* call processDB ONLY if the output is direction of DataBase Internal */
        processDB(c,com.blueseer.pur.purData.updatePOFromAck(po, status));
        
        return packagePayLoad(c);
        
    }

 
 
}


