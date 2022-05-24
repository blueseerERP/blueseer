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

import java.util.ArrayList;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author vaughnte
 */
public class Template extends com.blueseer.edi.EDIMap { 
    
    public String[] Mapdata(ArrayList doc, String[] c) throws IOException, UserDefinedException  {
        
    /* SECTION 1*/
    setControl(c);    //required...set the super class variables per the inbound array passed from the Processor (See EDIMap javadoc for defs)
        
    mappedInput = mapInput(c, doc, ISF); //required...sets the source data structure for all subsequent map functions
    
    // setReference(getInput("BEG",3)); //optional...must be ran after mappedInput
    
    // debuginput(mappedInput);  //optional... for debug purposes
    
    /* SECTION 2*/
    
    // begin mapping  
    
    
    
    
     
    // end mapping
      
    /* SECTION 3 */
    return packagePayLoad(c); //required...sets output payload
}

    
}
