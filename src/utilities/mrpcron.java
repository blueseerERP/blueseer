/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
          OVData.deleteAllMRP();
        
        // create zero level demand for specific site
          OVData.createMRPZeroLevel(site);
        
        // create derived mrp records from zero level demand
        for (int i = 0; i < 8; i++) {
            OVData.createMRPByLevel(i, site);
        }

              
               
               // DONE
              
      
}
} // class

