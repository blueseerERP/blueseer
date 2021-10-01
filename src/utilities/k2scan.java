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


import com.blueseer.utl.OVData;
import java.io.*;
import java.text.ParseException;
import java.util.Scanner;
import java.util.ArrayList;


public class k2scan {
public static Scanner in = new Scanner(System.in);
public static ArrayList<String> mylist = new ArrayList<String>();
public static String user = "";
public static void main (String[] args) throws InterruptedException, ParseException { 
bsmf.MainFrame.setConfig();



if (args.length == 1) {
    user = args[0].toString();
} else {
    user = "k2";
}

clearscreen();
printscreen();
System.out.print("Choose option:");
String choice = in.nextLine();


System.out.println(choice);
Thread.sleep(2000);
}

public static void printscreen() throws InterruptedException, ParseException {

mylist.clear();
Scanner in = new Scanner(System.in);
System.out.println("*************************");
System.out.println("*  1) Scan To LocA      *");
System.out.println("*  0) Quit              *");
System.out.println("*************************");
System.out.print("Choose option:");
String choice = in.nextLine();
    if (choice.equals("1")) {
    clearscreen();
    choice1();
    Thread.sleep(1000);
    clearscreen();
    printscreen();
    } else if (choice.equals("0")) {
    System.out.println("Exiting...");
    Thread.sleep(1000);
    System.exit(0);
    } else {
    System.out.println("Bad Choice");
    Thread.sleep(1000);
    clearscreen();
    printscreen();
    }


}

public static void clearscreen() throws InterruptedException {
/* System.out.println("\f"); */
final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
}


public static boolean isParsableToInt(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }



public static void choice1() throws InterruptedException, ParseException {
String myscan = "";
String sqty = "";
int myqty = 0;
int i = 0;
boolean isbad = false;

while ( ! myscan.equals("d") ) {
clearscreen();

System.out.println("Scan Label:");
myscan = in.nextLine();

if (! myscan.equals("d") && (! OVData.isValidItem(myscan) || myscan.isEmpty())) {
    System.out.println("Bad Part");
          Thread.sleep(1000);
          continue;
}

System.out.println("Enter Qty:");
sqty = in.nextLine();
if (! isParsableToInt(sqty)) {
    System.out.println("Bad Qty");
          Thread.sleep(1000);
          continue;
} else {
   myqty = Integer.valueOf(sqty); 
}



   if (! myscan.equals("d")) {
       OVData.locTolocTransfer(myscan.toString(), myqty, user);
       System.out.println("scan complete.");
       Thread.sleep(1000);
       continue;
   } 

   // end program by pressing 'd'
   if (myscan.equals("d")) {
       OVData.locTolocTransfer(myscan.toString(),myqty, user);
       System.out.println("End Program");
       Thread.sleep(1000);
       return;
   } 
  

} // while loop

}

}
