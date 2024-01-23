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
import static bsmf.MainFrame.tags;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.Assert.assertEquals;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
/**
 *
 * @author TerryVa
 */
public class bsTestRunner {
    public static void main(String[] args) {
      
        bsmf.MainFrame.setConfig();  
        tags = ResourceBundle.getBundle("resources.bs", Locale.getDefault());
      
       // long z = System.currentTimeMillis() - ((long)daysBack * 24L * 60L * 60L * 1000L);
        long z = System.currentTimeMillis();
        long x = 0;
        Result result = JUnitCore.runClasses(bsTest.class);
		
        for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
        }
      
        File folder = new File("edi/out");
        File[] listOfFiles = folder.listFiles();
        int cnt = 0;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getParentFile().canWrite() && listOfFiles[i].lastModified() > z) {
                x += listOfFiles[i].length();
                cnt++;
                }
            }
        }
      String s = (16895 == x) ? "pass" : "fail";
      System.out.println("Overall Testing Success: " + result.wasSuccessful());
      System.out.println("output file count: " + cnt);
      System.out.println("output file size sum: " + x);
      System.out.println("cumalative file size match (16895): " + s);
   }
}
