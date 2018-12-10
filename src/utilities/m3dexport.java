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
import java.io.*;
import java.util.ArrayList;

public class m3dexport {
public static void main(String args[]) {
 try {
     
             bsmf.MainFrame.setConfig();
              BufferedWriter output;
              String detail = "";
              String delimiter = "__M3D__";
     
              // lets do the Ledger Balance for 1516
               File f = new File("/apps/edi/m3d/out/avm/AVM_LedgerBalances.csv");
                if(f.exists()) {
                    f.delete();
                }
              output = new BufferedWriter(new FileWriter(f));
              String myheader = "Company" + delimiter +
                      "AcctID" + delimiter +
                      "DeptID" + delimiter +
                      "Period" + delimiter +
                      "Year" + delimiter + 
                      "Amt"
                      ;
              output.write(myheader + '\n');
              
                ArrayList<String> mylist = new ArrayList<String>();
                String[] ac = null;
                mylist = OVData.getGLBalanceRangeM3DByCC(2014, 2015, "1516");
                for (String rec : mylist) {
                ac = rec.split(",", -1);
                   detail = "AVM" + delimiter +
                           ac[0] + delimiter +
                           ac[1] + delimiter +
                           ac[2] + delimiter +
                           ac[3] + delimiter +
                           ac[4]
                           ;
                output.write(detail + '\n');
               }
             
               output.close();
               
               
                 // lets do the Ledger Balance for 1517 Premier
               f = new File("/apps/edi/m3d/out/premier/PREMIER_LedgerBalances.csv");
                if(f.exists()) {
                    f.delete();
                }
              output = new BufferedWriter(new FileWriter(f));
              myheader = "Company" + delimiter +
                      "AcctID" + delimiter +
                      "DeptID" + delimiter +
                      "Period" + delimiter +
                      "Year" + delimiter + 
                      "Amt"
                      ;
              output.write(myheader + '\n');
               mylist = new ArrayList<String>();
             
                mylist = OVData.getGLBalanceRangeM3DByCC(2014, 2015, "1517");
                for (String rec : mylist) {
                ac = rec.split(",", -1);
                   detail = "PREMIER" + delimiter +
                           ac[0] + delimiter +
                           ac[1] + delimiter +
                           ac[2] + delimiter +
                           ac[3] + delimiter +
                           ac[4]
                           ;
                output.write(detail + '\n');
               }
               output.close();
               
               // DONE
              
       } catch (IOException ex) {
          ex.printStackTrace();
       }
}
} // class

