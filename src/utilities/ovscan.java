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


public class ovscan {
public static Scanner in = new Scanner(System.in);
public static ArrayList<String> mylist = new ArrayList<String>();

public static void main (String[] args) throws InterruptedException, ParseException { 
bsmf.MainFrame.setConfig();

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
System.out.println("*  1) Ship to ABC       *");
System.out.println("*  2) Recv from ABC     *");
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
    } else if (choice.equals("2")) {
    clearscreen();
    choice2();
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



public static void choice1() throws InterruptedException, ParseException {
String myscan = "";
int i = 0;
boolean isbad = false;

while ( ! myscan.equals("d") ) {
clearscreen();
System.out.println("Scan Label:");
myscan = in.nextLine();
   if (myscan.equals("d")) {
       // lets insert tran_mstr record for each serial number
       for (String element : mylist) {
           if (OVData.isLabel(element) &&  OVData.getLabelStatus(element) == 2 ) {
               //OVData.doTransfer(element);
           } 
       }
       System.out.println("program complete.");
   Thread.sleep(1000);
   return;
   } 

  
   
   if (! myscan.equals("d") && ! myscan.isEmpty()) {
      
      if (! OVData.isLabel(myscan)) {
          System.out.println("Bad Label");
          Thread.sleep(1000);
          continue;
      } 
       if ( OVData.isLabel(myscan) && OVData.getLabelStatus(myscan) == 0) {
          System.out.println("Missing CR Scan");
          Thread.sleep(1000);
          continue;
      }
       if ( OVData.isLabel(myscan) && OVData.getLabelStatus(myscan) == 1) {
          System.out.println("Missing TT Scan");
          Thread.sleep(1000);
          continue;
      }
      if ( OVData.isLabel(myscan) && OVData.getLabelStatus(myscan) == 3) {
          System.out.println("Previously Scanned");
          Thread.sleep(1000);
          continue;
      } 
      if (! mylist.contains(myscan)) {
          i++;
          mylist.add(myscan);
          System.out.println("Scanned " + String.valueOf(i));
          Thread.sleep(1000);
      } else {
          System.out.println("Already Scanned");
          Thread.sleep(1000);
      }
   }

} // while loop

}



public static void choice2() throws InterruptedException, ParseException {
String myscan = "";
int i = 0;
boolean isbad = false;

while ( ! myscan.equals("d") ) {
clearscreen();
System.out.println("Scan Label:");
myscan = in.nextLine();
   if (myscan.equals("d")) {
       // lets insert tran_mstr record for each serial number
       for (String element : mylist) {
           if (OVData.isLabel(element) &&  OVData.getLabelStatus(element) == 2 ) {
               //OVData.doTransfer(element);
           } 
       }
       System.out.println("program complete.");
   Thread.sleep(1000);
   return;
   } 

  
   
   if (! myscan.equals("d") && ! myscan.isEmpty()) {
      
      if (! OVData.isLabel(myscan)) {
          System.out.println("Bad Label");
          Thread.sleep(1000);
          continue;
      } 
       if ( OVData.isLabel(myscan) && OVData.getLabelStatus(myscan) == 0) {
          System.out.println("Missing CR Scan");
          Thread.sleep(1000);
          continue;
      }
       if ( OVData.isLabel(myscan) && OVData.getLabelStatus(myscan) == 1) {
          System.out.println("Missing TT Scan");
          Thread.sleep(1000);
          continue;
      }
      if ( OVData.isLabel(myscan) && OVData.getLabelStatus(myscan) == 3) {
          System.out.println("Previously Scanned");
          Thread.sleep(1000);
          continue;
      } 
      if (! mylist.contains(myscan)) {
          i++;
          mylist.add(myscan);
          System.out.println("Scanned " + String.valueOf(i));
          Thread.sleep(1000);
      } else {
          System.out.println("Already Scanned");
          Thread.sleep(1000);
      }
   }

} // while loop

}



}
