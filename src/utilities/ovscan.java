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


import com.blueseer.lbl.lblData;
import com.blueseer.utl.OVData;
import java.io.*;
import java.text.ParseException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


public class ovscan {
    private static Scanner scanner = new Scanner(System.in);
    private static ArrayList<String> scannedLabels = new ArrayList<String>();

    /**
     * Returns the scanner instance for reading input.
     * @return the Scanner instance
     */
    public static Scanner getScanner() {
        return scanner;
    }

    /**
     * Returns an unmodifiable view of the scanned labels list.
     * @return unmodifiable list of scanned labels
     */
    public static List<String> getScannedLabels() {
        return Collections.unmodifiableList(scannedLabels);
    }

    /**
     * Adds a label to the scanned labels list.
     * @param label the label to add
     */
    public static void addScannedLabel(String label) {
        scannedLabels.add(label);
    }

    /**
     * Clears all scanned labels.
     */
    public static void clearScannedLabels() {
        scannedLabels.clear();
    }

    /**
     * Checks if a label has already been scanned.
     * @param label the label to check
     * @return true if the label exists in the list
     */
    public static boolean containsLabel(String label) {
        return scannedLabels.contains(label);
    }

    public static void main (String[] args) throws InterruptedException, ParseException {

        String configfile = "";
        if (args != null && args.length > 0) {
            int i = 0;
            for (String s : args) {
                if (s.equals("-config")) {
                    configfile = args[i+1];
                }
                i++;
            }
        }

        bsmf.MainFrame.setConfig(configfile);

        clearscreen();
        printscreen();
        System.out.print("Choose option:");
        String choice = scanner.nextLine();


        System.out.println(choice);
        Thread.sleep(2000);
    }

    public static void printscreen() throws InterruptedException, ParseException {

        clearScannedLabels();
        Scanner inputScanner = new Scanner(System.in);
        System.out.println("*************************");
        System.out.println("*  1) Ship to ABC       *");
        System.out.println("*  2) Recv from ABC     *");
        System.out.println("*  0) Quit              *");
        System.out.println("*************************");
        System.out.print("Choose option:");
        String choice = inputScanner.nextLine();
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
        String scannedInput = "";
        int i = 0;
        boolean isbad = false;

        while ( ! scannedInput.equals("d") ) {
            clearscreen();
            System.out.println("Scan Label:");
            scannedInput = scanner.nextLine();
            if (scannedInput.equals("d")) {
                // lets insert tran_mstr record for each serial number
                for (String element : getScannedLabels()) {
                    if (lblData.isLabel(element) &&  lblData.getLabelStatus(element) == 2 ) {
                        //OVData.doTransfer(element);
                    }
                }
                System.out.println("program complete.");
                Thread.sleep(1000);
                return;
            }



            if (! scannedInput.equals("d") && ! scannedInput.isEmpty()) {

                if (! lblData.isLabel(scannedInput)) {
                    System.out.println("Bad Label");
                    Thread.sleep(1000);
                    continue;
                }
                if ( lblData.isLabel(scannedInput) && lblData.getLabelStatus(scannedInput) == 0) {
                    System.out.println("Missing CR Scan");
                    Thread.sleep(1000);
                    continue;
                }
                if ( lblData.isLabel(scannedInput) && lblData.getLabelStatus(scannedInput) == 1) {
                    System.out.println("Missing TT Scan");
                    Thread.sleep(1000);
                    continue;
                }
                if ( lblData.isLabel(scannedInput) && lblData.getLabelStatus(scannedInput) == 3) {
                    System.out.println("Previously Scanned");
                    Thread.sleep(1000);
                    continue;
                }
                if (! containsLabel(scannedInput)) {
                    i++;
                    addScannedLabel(scannedInput);
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
        String scannedInput = "";
        int i = 0;
        boolean isbad = false;

        while ( ! scannedInput.equals("d") ) {
            clearscreen();
            System.out.println("Scan Label:");
            scannedInput = scanner.nextLine();
            if (scannedInput.equals("d")) {
                // lets insert tran_mstr record for each serial number
                for (String element : getScannedLabels()) {
                    if (lblData.isLabel(element) &&  lblData.getLabelStatus(element) == 2 ) {
                        //OVData.doTransfer(element);
                    }
                }
                System.out.println("program complete.");
                Thread.sleep(1000);
                return;
            }



            if (! scannedInput.equals("d") && ! scannedInput.isEmpty()) {

                if (! lblData.isLabel(scannedInput)) {
                    System.out.println("Bad Label");
                    Thread.sleep(1000);
                    continue;
                }
                if ( lblData.isLabel(scannedInput) && lblData.getLabelStatus(scannedInput) == 0) {
                    System.out.println("Missing CR Scan");
                    Thread.sleep(1000);
                    continue;
                }
                if ( lblData.isLabel(scannedInput) && lblData.getLabelStatus(scannedInput) == 1) {
                    System.out.println("Missing TT Scan");
                    Thread.sleep(1000);
                    continue;
                }
                if ( lblData.isLabel(scannedInput) && lblData.getLabelStatus(scannedInput) == 3) {
                    System.out.println("Previously Scanned");
                    Thread.sleep(1000);
                    continue;
                }
                if (! containsLabel(scannedInput)) {
                    i++;
                    addScannedLabel(scannedInput);
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