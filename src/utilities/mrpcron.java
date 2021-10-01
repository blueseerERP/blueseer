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
package utilities;

/**
 *
 * @author vaughnte
 */
import com.blueseer.utl.OVData;
import java.util.ArrayList;

public class mrpcron {
public static void main(String args[]) {

             bsmf.MainFrame.setConfig();
             
       String site = "";
       
       if (args.length == 1) {
           site = args[0].toString();
       } else {
           site = OVData.getDefaultSite();
       }
       
       System.out.println(site);
       // lets do the itemlevel calc first
       ArrayList<String> myarray = new ArrayList<String>();
        OVData.setzerolevelpsmstr();
        for (int i = 0; i < 8; i++) {
            myarray = OVData.getNextLevelpsmstr(i);
            if (! myarray.isEmpty()) {
            OVData.updateItemlevel(myarray, i + 1);
            } else {
                break;
            }
        }
        
        
          // now lets do the MRP regen
           ArrayList<String> myarray2 = new ArrayList<String>();
        // delete all current MRP records...clean slate
          OVData.deleteAllMRP(site, bsmf.MainFrame.lowchar, bsmf.MainFrame.hichar);
        
        // create zero level demand for specific site
          OVData.createMRPZeroLevel(site);
        
        // create derived mrp records from zero level demand
        for (int i = 0; i < 8; i++) {
            OVData.createMRPByLevel(i, site, bsmf.MainFrame.lowchar, bsmf.MainFrame.hichar);
        }

              
               
               // DONE
              
      
}
} // class

