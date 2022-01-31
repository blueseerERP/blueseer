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
package com.blueseer.adm;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.hidate;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

/**
 *
 * @author vaughnte
 */
public class About extends javax.swing.JPanel {

    
    String currenttext = "";
    /**
     * Creates new form AboutPanel
     */
    public About() {
        initComponents();
         currenttext = jTextArea2.getText();
    }

    
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            logSysInfo();
            return null;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
            BlueSeerUtils.endTask(new String[]{"0","System info is complete"});
           
             
      
       // now pull sysinfo from app/sys.log file generated at startup (bsmf.MainFrame.logSysInfo())
       BufferedReader fsr;
        try {
            fsr = new BufferedReader(new FileReader(new File("data/sys.log")));
            String line = "";
            while ((line = fsr.readLine()) != null) {
               jTextArea2.append(line + "\n");
            }
            fsr.close();
        } catch (FileNotFoundException ex) {
            MainFrame.bslog(ex);
            jTextArea2.append("File not found at data/sys.log");
        } catch (IOException ex) {
            MainFrame.bslog(ex);
        }
        }
    }  
    
    
    public static void logSysInfo() {
        
       
       SystemInfo si = new SystemInfo(); 
       HardwareAbstractionLayer hal = si.getHardware();
       
       
       long availableMemory = hal.getMemory().getAvailable();
       
       String Text = "INIT DATE:  " + hidate + "\n";
       Text += "Locale: " +  Locale.getDefault().toString() + "\n"; 
       Text += "Version: " +  MainFrame.ver + "\n"; 
       Text += "DataBase: " +  MainFrame.db + "\n";
       Text += "DataBaseType: " +  MainFrame.dbtype + "\n";
       Text += "DataBaseDriver: " +  MainFrame.driver + "\n";
      
       
       
       Text += "Available memory (bytes): " + 
       (availableMemory == Long.MAX_VALUE ? "no limit" : availableMemory) + "\n";
       
       long totalMemory = hal.getMemory().getTotal();
       Text += "Total memory (bytes): " + 
       (totalMemory == Long.MAX_VALUE ? "no limit" : totalMemory) + "\n"; 
       
       Text += "Processor: " + si.getHardware().getProcessor().getName() + "\n";
       Text += "Processor Vendor: " + si.getHardware().getProcessor().getVendor() + "\n"; 
       Text += "Processor Model: " + si.getHardware().getProcessor().getModel() + "\n"; 
       Text += "Processor Logical Count: " + si.getHardware().getProcessor().getLogicalProcessorCount() + "\n"; 
       Text += "Processor Physical Count: " + si.getHardware().getProcessor().getPhysicalProcessorCount() + "\n"; 
       
       
       OperatingSystem os = si.getOperatingSystem();
       
       
       
       Text += "Operating System: " + os + "\n";
       Text += "OS Family: " + os.getFamily() + "\n";
       Text += "OS Manufacturer: " + os.getManufacturer() + "\n";
       Text += "OS Version: " + os.getVersion().getVersion() + "\n";
       Text += "OS Bit: " + os.getBitness() + "\n";
       Text += "OS Build: " + os.getVersion().getBuildNumber() + "\n";
       Text += "OS Codename: " + os.getVersion().getCodeName() + "\n";
       Text += "OS FileSystem: " + os.getFileSystem() + "\n";
       
       
        NetworkIF[] s = si.getHardware().getNetworkIFs();
        
        
       for (NetworkIF x : s) {
        Text += "Network: " + x.getName() + " / " + String.join(", ", x.getIPv4addr()) + "\n";
       }
       
       Text += "Java Version: " + System.getProperty("java.version") + "\n";   
       Text += "Java VM: " + System.getProperty("java.vm.name") + "\n";
       Text += "Java VM Version: " + System.getProperty("java.vm.version") + "\n";
       Text += "Java Runtime Name: " + System.getProperty("java.runtime.name") + "\n";   
       Text += "Java Runtime Version: " + System.getProperty("java.runtime.version") + "\n";  
       Text += "Java Class Version: " + System.getProperty("java.class.version") + "\n";   
       Text += "Java Compiler: " + System.getProperty("sun.management.compiler") + "\n";   
       
       
       // now get patch info...created from git command :  git rev-parse HEAD
       /*
       BufferedReader fsr;
        try {
            fsr = new BufferedReader(new FileReader(new File(".patch")));
            String line = "";
            while ((line = fsr.readLine()) != null) {
               Text += "Patch: " + line;
            }
            fsr.close();
        } catch (FileNotFoundException ex) {
            MainFrame.bslog(ex);
        } catch (IOException ex) {
            MainFrame.bslog(ex);
        }
       */
       // now write to app.log
        try(FileWriter fileWriter =
         new FileWriter("data/sys.log", false) ){

         fileWriter.write(Text);
        }      catch (IOException ex) {
           bslog(ex);
        }
       
        
    }
    
    
    
    public void getSysInfo() {
                 /* Total number of processors or cores available to the JVM */
       jTextArea2.setText("");
       BlueSeerUtils.startTask(new String[]{"","Retrieving System Info..."});
        Task task = new Task();
        task.execute();
                
       
      
           
    }
    
    
    public void initvars(String[] arg) {
        
        if (arg != null && arg.length > 0 && arg[0].equals("SysInfo")) {
            getSysInfo();
        } else {
            jTextArea2.setText(currenttext);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setText("\nBlueSeer ERP was developed to provide the open source community with a free alternative to proprietary ERPs\nwith regards to both cost and customization capability.   For more information, you can visit the application project\nsite hosted on github or the primary website shown below.   There exists forums to submit issues, discussions, or feedback\nat these locations.\n\nWebsite:\t\twww.blueseer.com\nProject Site:\t\thttps://github.com/BlueSeerERP   \nEmail:\t\tsupport@blueseer.com\nCreator:\t\tTerry Evans Vaughn\nLanguage:\t\tJava\nSource:\t\thttps://github.com/BlueSeerERP/blueseer\n");
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 989, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 844, Short.MAX_VALUE)
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}
