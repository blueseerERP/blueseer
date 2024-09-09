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

import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addSiteMstr;
import com.blueseer.adm.admData.site_mstr;
import com.blueseer.edi.EDI;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 *
 * @author TerryVa
 */
public class bsTest {
   
    String[] m = new String[]{"",""};
        
   @Ignore 
   @Test
   public void testAddSiteMstr() {
       
      try {
          m = addSiteMstr(createSiteMstr());
          System.out.println("Creating Site Master: " + m[0] + "  m1: "+ m[1]);
      } catch (Exception e) {
          System.out.println("BS Exception: " + e.getMessage() );
      }
      assertEquals("0",m[0]);
   }
   
   @Test
   public void testEDI_850x12() {
      try {
          m = EDI.processFile("edi/sampledata/ACME_850.txt","","","", false, false, 0, 0, "");
          if (m[0].equals("0")) {
              System.out.println("sample 850x12...   pass");
          } else {
              System.out.println("sample 850x12...   fail");
              System.out.println("m[1] message: " + m[1]);
          }
      } catch (Exception e) {
          System.out.println("BS Exception: " + e.getMessage() );
      }
      assertEquals("0",m[0]);
   }
   
   @Test
   public void testEDI_850json() {
      try {
          m = EDI.processFile("edi/sampledata/ACME_JSON_order.txt","","","", false, false, 0, 0, "");
          if (m[0].equals("0")) {
              System.out.println("sample 850json...  pass");
          } else {
              System.out.println("sample 850json...  fail");
              System.out.println("m[1] message: " + m[1]);
          }
      } catch (Exception e) {
          System.out.println("BS Exception: " + e.getMessage() );
      }
      assertEquals("0",m[0]);
   }
   
   @Test
   public void testEDI_850xml() {
      try {
          m = EDI.processFile("edi/sampledata/ACME_XML_order.xml","","","", false, false, 0, 0, "");
          if (m[0].equals("0")) {
              System.out.println("sample 850xml...   pass");
          } else {
              System.out.println("sample 850xml...   fail");
              System.out.println("m[1] message: " + m[1]);
          }
      } catch (Exception e) {
          System.out.println("BS Exception: " + e.getMessage() );
      }
      assertEquals("0",m[0]);
   }
   
   @Test
   public void testEDI_856Idoc() {
      try {
          m = EDI.processFile("edi/sampledata/ACME_IDOC_SHIPMNT02out.txt","","","", false, false, 0, 0, "");
          if (m[0].equals("0")) {
              System.out.println("sample 856idoc...  pass");
          } else {
              System.out.println("sample 850idoc...  fail");
              System.out.println("m[1] message: " + m[1]);
          }
      } catch (Exception e) {
          System.out.println("BS Exception: " + e.getMessage() );
      }
      assertEquals("0",m[0]);
   }
   
   @Test
   public void testEDI_850Idoc() {
      try {
          m = EDI.processFile("edi/sampledata/ACME_IDOC_INVOIC02out.txt","","","", false, false, 0, 0, "");
          if (m[0].equals("0")) {
              System.out.println("sample 810idoc...  pass");
          } else {
              System.out.println("sample 810idoc...  fail");
              System.out.println("m[1] message: " + m[1]);
          }
      } catch (Exception e) {
          System.out.println("BS Exception: " + e.getMessage() );
      }
      assertEquals("0",m[0]);
   }
   
   @Test
   public void testEDI_850csv() {
      try {
          m = EDI.processFile("edi/sampledata/ACME_CSV_order.csv","","","", false, false, 0, 0, "");
          if (m[0].equals("0")) {
              System.out.println("sample 850csv...   pass");
          } else {
              System.out.println("sample 850csv...   fail");
              System.out.println("m[1] message: " + m[1]);
          }
      } catch (Exception e) {
          System.out.println("BS Exception: " + e.getMessage() );
      }
      assertEquals("0",m[0]);
   }
   
   @Test
   public void testEDI_850une() {
      try {
          m = EDI.processFile("edi/sampledata/ACME_EDIFACT_ORDERS.txt","","","", false, false, 0, 0, "");
          if (m[0].equals("0")) {
              System.out.println("sample 850une...   pass");
          } else {
              System.out.println("sample 850une...   fail");
              System.out.println("m[1] message: " + m[1]);
          }
      } catch (Exception e) {
          System.out.println("BS Exception: " + e.getMessage() );
      }
      assertEquals("0",m[0]);
   }
   
   
   public site_mstr createSiteMstr() {
       site_mstr x = new site_mstr(null, "xsite",
                "xdesc",
                "xline1",
                "xline2",
                "xline3",
                "xcity",
                "xstate",
                "xzip",
                "xcountry",
                "xphone", // phone
                "xweb", // web
                "xlogo",
                "invoicejasper",  
                "shipperjasper",
                "contactname", // sqename
                "contactphone", // sqephone
                "contactfax", // sqefax
                "contactemail", // sqemail
                "pojasper",
                "orderjasper",
                "posjasper");
       return x;
   }
   
}
