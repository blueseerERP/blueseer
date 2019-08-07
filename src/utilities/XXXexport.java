/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
import java.io.*;
import java.util.ArrayList;

public class XXXexport {
public static void main(String args[]) {
 try {
     
             bsmf.MainFrame.setConfig();
              BufferedWriter output;
              String detail = "";
              String delimiter = "__XXX__";
     
              // lets do the Ledger Balance for 1516
               File f = new File("LedgerBalances.csv");
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
                mylist = OVData.getGLBalanceRangeXXXByCC(2014, 2015, "123"); 
                for (String rec : mylist) {
                ac = rec.split(",", -1);
                   detail = "XXX" + delimiter +
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
               f = new File("LedgerBalances.csv");
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
             
                mylist = OVData.getGLBalanceRangeXXXByCC(2014, 2015, "123"); 
                for (String rec : mylist) {
                ac = rec.split(",", -1);
                   detail = "XXX" + delimiter +
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

