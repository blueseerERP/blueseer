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

package com.blueseer.edi;

import com.blueseer.utl.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.tags;
import static com.blueseer.ctr.cusData.addCustMstrMass;
import static com.blueseer.edi.EDI.generate997;
import static com.blueseer.edi.apiUtils.calculateMIC;
import static com.blueseer.edi.apiUtils.hashdigest;
import static com.blueseer.edi.apiUtils.hashdigestString;
import static com.blueseer.edi.apiUtils.verifySignatureView;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import java.awt.Component;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author vaughnte
 */
public class EDIUtilities extends javax.swing.JPanel {

     // global variable declarations
                boolean isLoad = false;
    /**
     * Creates new form FileOrderLoadPanel
     */
    public EDIUtilities() {
        initComponents();
        setLanguageTags(this);
    }

    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                if (component instanceof JScrollPane) {
                    setPanelComponentState((JScrollPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    
                    component.setEnabled(b);
                    
                }
            }
            if (scrollpane != null) {
                scrollpane.setEnabled(b);
                JViewport viewport = scrollpane.getViewport();
                Component[] componentspane = viewport.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    component.setEnabled(b);
                }
            }
    } 
    
    public void setLanguageTags(Object myobj) {
       JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
       Component[] components = panel.getComponents();
       for (Component component : components) {
           if (component instanceof JPanel) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".panel." + component.getName())) {
                       ((JPanel) component).setBorder(BorderFactory.createTitledBorder(tags.getString(this.getClass().getSimpleName() +".panel." + component.getName())));
                    } 
                    setLanguageTags((JPanel) component);
                }
                if (component instanceof JLabel ) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JLabel) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
       }
    }
    
    public void setComponentDefaultValues() {
       isLoad = true;
        taoutput.setText("");
       isLoad = false;
       
    }
    
    public void setState() {
        setPanelComponentState(this, true);
    }
    
    public void executeTask(String x, File y, String type) { 
      
        class Task extends SwingWorker<String[], Void> { 
       
          String type = "";
          String[] key = null;
          
        
          public Task(String x, File y, String type) { 
              this.type = type;
          }
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            if (type.equals("testdata")) {
           // message = loadTestData();    
            } else {
          //  message = processfile(x, y);
            }
            return message;
        }
      
        
       public void done() {
            try {
            String[] message = get();
            if (message[0] == null) {
                message[0] = "1"; // cancel upload
            }
            BlueSeerUtils.endTask(message);
            setState(); 
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x,y,type); 
       z.execute();
       
       
    }
   
    
    public void initvars(String[] arg) {
      isLoad = true;
      if (! ddtable.getItemAt(0).equals("")) {
          ddtable.insertItemAt("", 0);
      } 
      ddtable.setSelectedIndex(0);
      resetVariables();
      hideInputPanels();
      panelinput.setVisible(true);
      paneloutput.setVisible(true);
      
      rbactive.setSelected(true);
      rbinactive.setSelected(false);
      buttonGroup1.add(rbactive);
      buttonGroup1.add(rbinactive);
      
      btrun.setVisible(true);
      btclear.setVisible(true);
      btfile.setVisible(false);
      btdir.setVisible(false);
      taoutput.setText("");
      tainput.setText("");
      isLoad = false;
    }
   
    public void processAction(boolean b) {
        taoutput.removeAll();
        String x = ddtable.getSelectedItem().toString();
            String[] xar = x.split("\\.",-1);
            String util = xar[0];
            if (xar != null && xar.length > 1) {
                if (util.equals("1")) {
                    determineX12Delimiters(b);
                }
                if (util.equals("2")) {
                    replaceDelimiterWithNL(b);
                }
                if (util.equals("3")) {
                    countOccurrencesHex(b);
                }
                if (util.equals("4")) {
                    getDigest(b);
                }
                if (util.equals("5")) {
                    decryptPGPFile(b);
                }
                if (util.equals("6")) {
                    create997(b);
                }
                if (util.equals("7")) {
                    verifyHashSignature(b);
                }
            } else { // no period to split...must be blank or malformed selection element
               resetVariables();
               hideInputPanels();
            }
    }
    
    public File getfile(String title) {
        
        File file = null;
        JFileChooser jfc = new JFileChooser(FileSystems.getDefault().getPath("edi").toFile());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle(title);
        int returnVal = jfc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = jfc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            file = new File(SourceDir);
               if (! file.exists()) {
                 return null;
               }
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
        return file;
    }
    
    public void replacehex(String data, String fromhexparam, String tohexparam) {
       
        char toHex = 0;
        char fromHex = 0;
        
        if (fromhexparam.isBlank() || tohexparam.isBlank()) {
            String text = bsmf.MainFrame.input("Hex Chars: ");
            if (text == null || text.isBlank()) {
                return;
            }
            String[] replacehex = text.split("\\|",-1);
            if (replacehex == null || replacehex.length != 2 || replacehex[0].isBlank()) {
                return;
            }
            fromHex = (char) Integer.parseInt(replacehex[0], 16);
            if (! replacehex[1].isBlank()) { 
            toHex = (char) Integer.parseInt(replacehex[1], 16);
            }
        } else {
            fromHex = (char) Integer.parseInt(fromhexparam, 16);
            toHex = (char) Integer.parseInt(tohexparam, 16);
        }
       
        int count = 0;
        char[] carray = data.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < carray.length; i++) {
            if (carray[i] == fromHex) {
                if (toHex > 0) {
                    sb.append(toHex);  // skip is toHex is blank (0)...removes character
                }
                count++;
            } else {
                sb.append(carray[i]);
            }
        }
        taoutput.setText("");
        taoutput.setText(sb.toString());
        paneloutput.setVisible(true);
        taoutput.setCaretPosition(0);
       // bsmf.MainFrame.show("Occurences: " + count);
        
    }
    
    public void countHexOccurences(String data, String hexparam) {
      
        char Hex = 0;
        try {
        if (hexparam.isBlank()) {
            String text = bsmf.MainFrame.input("Hex Char (ex: 0a): ");
            if (text == null || text.isBlank()) {
                return;
            }
            Hex = (char) Integer.parseInt(text, 16);
        } else {
            Hex = (char) Integer.parseInt(hexparam, 16);
        }
        } catch (NumberFormatException nfe) {
            bsmf.MainFrame.show("number format exception");
        }
       
        int count = 0;
        char[] carray = data.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < carray.length; i++) {
            sb.append(i);
            sb.append("-->");
            sb.append(Integer.toHexString((int) carray[i]));
           // sb.append(String.format("%04x", (int) carray[i]));
            sb.append("\n");
            if (carray[i] == Hex) {
                count++;
            } 
        }
        
        taoutput.setText(sb.toString());
       bsmf.MainFrame.show("Occurences: " + count);
        
    }
    
    
    public void hideInputPanels() {
        paneltb.setVisible(false);
        paneltb2.setVisible(false);
        paneldc.setVisible(false);
        paneldd.setVisible(false);
        panelrb.setVisible(false);
        panelboxes.setVisible(false);
    }   
    
    public void showPanels(String[] panels) {
        for (String panel : panels) {
            if (panel.equals("tb"))   // two textboxes tbkey1 & tbkey2
                paneltb.setVisible(true);
            if (panel.equals("tb2"))  // two textboxes tbkey3 & tbkey4
                paneltb2.setVisible(true);
            if (panel.equals("dc"))  // two datechoosers dcdate1 & dcdate2
                paneldc.setVisible(true);
            if (panel.equals("dd"))  // two dropdowns ddkey1 & ddkey2
                paneldd.setVisible(true);
            if (panel.equals("rb"))  // two radio buttosn  rbactive & rbinactive
                panelrb.setVisible(true);
           
        }
    }
    
    public void resetVariables() {
        tbkey1.setEnabled(true);
        tbkey2.setEnabled(true);
        tbkey3.setEnabled(true);
        tbkey4.setEnabled(true);
        dcdate1.setEnabled(true);
        dcdate2.setEnabled(true);
        ddkey1.setEnabled(true);
        ddkey2.setEnabled(true);
        rbactive.setEnabled(true);
        rbinactive.setEnabled(true);
        
        tbkey1.setVisible(true);
        tbkey2.setVisible(true);
        tbkey3.setVisible(true);
        tbkey4.setVisible(true);
        dcdate1.setVisible(true);
        dcdate2.setVisible(true);
        ddkey1.setVisible(true);
        ddkey2.setVisible(true);
        rbactive.setVisible(true);
        rbinactive.setVisible(true);
        
        tbkey1.setText("");
        tbkey2.setText("");
        tbkey3.setText("");
        tbkey4.setText("");
        dcdate1.setVisible(true);
        dcdate2.setVisible(true);
        ddkey1.setSelectedIndex(0);
        ddkey2.setSelectedIndex(0);
        rbactive.setSelected(true);
        rbinactive.setSelected(false);
        
        lbkey1.setText("");
        lbkey1.setVisible(true);
        lbkey2.setText("");
        lbkey2.setVisible(true);
        lbkey3.setText("");
        lbkey3.setVisible(true);
        lbkey4.setText("");
        lbkey4.setVisible(true);
        lbddkey1.setText("");
        lbddkey1.setVisible(true);
        lbddkey2.setText("");
        lbddkey2.setVisible(true);
        lbdate1.setText("");
        lbdate1.setVisible(true);
        lbdate2.setText("");
        lbdate2.setVisible(true);
    }
    
    public String[] getHexDelimiters(String doc) {
        String[] x = null;
        
        char[] cbuf = doc.toCharArray();
       
        if (cbuf[0] == 'I' && cbuf[1] == 'S' && cbuf[2] == 'A') {
            x = new String[3];
            x[0] = String.format("%04x", (int) cbuf[103]); // element
            x[1] = String.format("%04x", (int) cbuf[104]); // sub
            x[2] = String.format("%04x", (int) cbuf[105]); // segment
        }
          
        return x;
    }
    
    public void replaceDelimiterWithNL(boolean input) {
         
         if (input) { // input...draw variable input panel
           resetVariables();
           hideInputPanels();
          // showPanels(new String[]{"tb","dc"});
           btrun.setVisible(true);
           btfile.setVisible(false);
           btdir.setVisible(false);
           lbkey1.setText(getClassLabelTag("lblfromcode", this.getClass().getSimpleName()));
           lbkey2.setText(getClassLabelTag("lbltocode", this.getClass().getSimpleName()));
           lbdate1.setText(getClassLabelTag("lblfromdate", this.getClass().getSimpleName()));
           lbdate2.setText(getClassLabelTag("lbltodate", this.getClass().getSimpleName()));
           java.util.Date now = new java.util.Date();
          // dcdate1.setDate(now);
          // dcdate2.setDate(now);
         } else { // output...fill report
            // colect variables from input
            String from = tbkey1.getText();
            String to = tbkey2.getText();
            String fromdate = BlueSeerUtils.setDateFormat(dcdate1.getDate());
            String todate = BlueSeerUtils.setDateFormat(dcdate2.getDate());
            // cleanup variables
            if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
            }
            if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
            }
            if (fromdate.isEmpty()) {
                  fromdate = bsmf.MainFrame.lowdate;
            }
            if (todate.isEmpty()) {
                  todate = bsmf.MainFrame.hidate;
            }
       
            
        if (tainput.getText().isEmpty()) {    
        File file = getfile("open target file");
        taoutput.setText("");
        tainput.setText("");
        // executeTask(ddtable.getSelectedItem().toString(), null, "");
        if (file != null && file.exists()) {
                try {  
                    BufferedReader f = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
                     char[] cbuf = new char[(int) file.length()];
                     f.read(cbuf); 
                     f.close();
                    
                     StringBuilder docstring = new StringBuilder();
                    for (int i = 0; i < cbuf.length; i++) {
                        docstring.append(cbuf[i]);
                    }
                    tainput.setText(docstring.toString());
                  //  lines = Files.readAllLines(file.toPath());
                    /*
                    for (String segment : lines ) {
                        tainput.append(segment);
                        tainput.append("\n");
                    }
                    */
                    tainput.setCaretPosition(0);
                    panelinput.setVisible(true);
                    
                    
                } catch (MalformedInputException m) {
                    bslog(m);
                    bsmf.MainFrame.show("Input file may not be UTF-8 encoded");
                } catch (IOException ex) {
                    bslog(ex);
                    bsmf.MainFrame.show("Error...check data/app.log");
                }   
        }
        } // if tainput is empty   
            
            if (! tainput.getText().isEmpty()) {
                String[] delims = getHexDelimiters(tainput.getText());
                 //   if (delims != null) {
                  //      bsmf.MainFrame.show("hex delims: (seg, ele, sub) = ( " + delims[2] + "," + delims[0] + "," + delims[1] + ")");
                  //  }
               replacehex(tainput.getText(),delims[2],"0a");
            }
            
            
      
        } // else run report
    }
    
    public void decryptPGPFile(boolean input) {
        taoutput.removeAll();
        File file = null;
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyyMMddHHmmss");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            
            Path inpath = FileSystems.getDefault().getPath(file.getAbsolutePath());
            
            String myfile = file.getName() + "." + dfdate.format(now) + ".dec";
            Path outpath = FileSystems.getDefault().getPath(bsmf.MainFrame.temp + "/" + myfile);
            
            
            byte[] indata = Files.readAllBytes(inpath);
            
            String text = bsmf.MainFrame.input("passPhrase: ");
            if (text == null || text.isBlank()) {
                bsmf.MainFrame.show("passPhrase is empty");
                return;
            }
            String keyfiletext = bsmf.MainFrame.input("SecretKeyFile Path: ");
            Path keyfilepath = FileSystems.getDefault().getPath(keyfiletext);
            if (! keyfilepath.toFile().exists()) {
                bsmf.MainFrame.show("keyfile path does not exist");
                return;
            }
            
            byte[] outdata = apiUtils.decryptPGPData(indata, text, keyfilepath, null ); 
            if (outdata == null) {
                bsmf.MainFrame.show("unable to decrypt file");
                return;
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outpath.toFile()));
            bos.write(outdata);
            bos.flush();
            bos.close();   
           
            taoutput.append(new String(outdata));
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
           
        } else {
           System.out.println("cancelled");
        }
    }
    
    
    public void determineX12Delimiters(boolean input) {
         
         if (input) { // input...draw variable input panel
           resetVariables();
           hideInputPanels();
          // showPanels(new String[]{"tb","dc"});
           btrun.setVisible(true);
           btfile.setVisible(false);
           btdir.setVisible(false);
           lbkey1.setText(getClassLabelTag("lblfromcode", this.getClass().getSimpleName()));
           lbkey2.setText(getClassLabelTag("lbltocode", this.getClass().getSimpleName()));
           lbdate1.setText(getClassLabelTag("lblfromdate", this.getClass().getSimpleName()));
           lbdate2.setText(getClassLabelTag("lbltodate", this.getClass().getSimpleName()));
           java.util.Date now = new java.util.Date();
          // dcdate1.setDate(now);
          // dcdate2.setDate(now);
         } else { // output...fill report
            // colect variables from input
            taoutput.setText("");
            String from = tbkey1.getText();
            String to = tbkey2.getText();
            String fromdate = BlueSeerUtils.setDateFormat(dcdate1.getDate());
            String todate = BlueSeerUtils.setDateFormat(dcdate2.getDate());
            // cleanup variables
            if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
            }
            if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
            }
            if (fromdate.isEmpty()) {
                  fromdate = bsmf.MainFrame.lowdate;
            }
            if (todate.isEmpty()) {
                  todate = bsmf.MainFrame.hidate;
            }
       
            
        if (tainput.getText().isEmpty()) {    
        File file = getfile("open target file");
        taoutput.setText("");
        tainput.setText("");
        // executeTask(ddtable.getSelectedItem().toString(), null, "");
        if (file != null && file.exists()) {
                try {  
                    BufferedReader f = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
                     char[] cbuf = new char[(int) file.length()];
                     f.read(cbuf); 
                     f.close();
                    
                     StringBuilder docstring = new StringBuilder();
                    for (int i = 0; i < cbuf.length; i++) {
                        docstring.append(cbuf[i]);
                    }
                    tainput.setText(docstring.toString());
                  //  lines = Files.readAllLines(file.toPath());
                    /*
                    for (String segment : lines ) {
                        tainput.append(segment);
                        tainput.append("\n");
                    }
                    */
                    tainput.setCaretPosition(0);
                    panelinput.setVisible(true);
                    
                    
                } catch (MalformedInputException m) {
                    bslog(m);
                    bsmf.MainFrame.show("Input file may not be UTF-8 encoded");
                } catch (IOException ex) {
                    bslog(ex);
                    bsmf.MainFrame.show("Error...check data/app.log");
                }   
        }
        } // if tainput is empty   
            
            if (! tainput.getText().isEmpty()) {
                String[] delims = getHexDelimiters(tainput.getText());
                 //   if (delims != null) {
                  //      bsmf.MainFrame.show("hex delims: (seg, ele, sub) = ( " + delims[2] + "," + delims[0] + "," + delims[1] + ")");
                  //  }
              // replacehex(tainput.getText(),delims[2],"0a");
              taoutput.append("X12 Delimiter Values in Hex: " + "\n");
              if (delims != null) {
              taoutput.append("Segment (hex): " + delims[2] + "\n");
              taoutput.append("Element (hex): " + delims[0] + "\n");
              taoutput.append("SubElement (hex): " + delims[1] + "\n");
              } else {
                taoutput.append("Unable to determine delimiter values! " + "\n");  
              }
              
            }
            
            
      
        } // else run report
    }
    
    public void countOccurrencesHex(boolean input) {
         
         if (input) { // input...draw variable input panel
           resetVariables();
           hideInputPanels();
          // showPanels(new String[]{"tb","dc"});
           btrun.setVisible(true);
           btfile.setVisible(false);
           btdir.setVisible(false);
           lbkey1.setText(getClassLabelTag("lblfromcode", this.getClass().getSimpleName()));
           lbkey2.setText(getClassLabelTag("lbltocode", this.getClass().getSimpleName()));
           lbdate1.setText(getClassLabelTag("lblfromdate", this.getClass().getSimpleName()));
           lbdate2.setText(getClassLabelTag("lbltodate", this.getClass().getSimpleName()));
           java.util.Date now = new java.util.Date();
          // dcdate1.setDate(now);
          // dcdate2.setDate(now);
         } else { // output...fill report
            // colect variables from input
            taoutput.setText("");
            String from = tbkey1.getText();
            String to = tbkey2.getText();
            String fromdate = BlueSeerUtils.setDateFormat(dcdate1.getDate());
            String todate = BlueSeerUtils.setDateFormat(dcdate2.getDate());
            // cleanup variables
            if (from.isEmpty()) {
                  from = bsmf.MainFrame.lownbr;
            }
            if (to.isEmpty()) {
                  to = bsmf.MainFrame.hinbr;
            }
            if (fromdate.isEmpty()) {
                  fromdate = bsmf.MainFrame.lowdate;
            }
            if (todate.isEmpty()) {
                  todate = bsmf.MainFrame.hidate;
            }
       
            
        if (tainput.getText().isEmpty()) {    
        File file = getfile("open target file");
        taoutput.setText("");
        tainput.setText("");
        // executeTask(ddtable.getSelectedItem().toString(), null, "");
            if (file != null && file.exists()) {
                Path filepath = file.toPath();
                byte[] filecontent;
                try {
                    filecontent = Files.readAllBytes(filepath);
                    String s = new String(filecontent);
                    countHexOccurences(s,"");
                   // filecontent = Files.readString(as2filepath);
                } catch (IOException ex) {
                    bslog(ex);
                }
            }
        } // if tainput is empty   
            
      
        } // else run report
    }
    
    public void getDigest(boolean input) {
        taoutput.setText("");
        String mic = "";
        if (! tainput.getText().isEmpty()) { 
            String x = hashdigestString(tainput.getText().getBytes(), "SHA-1");
            try { 
                mic = calculateMIC(tainput.getText().getBytes(), "SHA-1");
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(EDIUtilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(EDIUtilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EDIUtilities.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            taoutput.setText("string: " + x + "\n" + "calculateMIC: " + mic + "\n" +  "hashdigest: " + hashdigest(tainput.getText().getBytes(), "SHA-1") + "\n");
         }
    }
    
    public void getInput() {
        if (tainput.getText().isEmpty()) {    
        File file = getfile("open target file");
        taoutput.setText("");
        tainput.setText("");
        // executeTask(ddtable.getSelectedItem().toString(), null, "");
        if (file != null && file.exists()) {
                try {  
                    BufferedReader f = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
                     char[] cbuf = new char[(int) file.length()];
                     f.read(cbuf); 
                     f.close();
                    
                     StringBuilder docstring = new StringBuilder();
                    for (int i = 0; i < cbuf.length; i++) {
                        docstring.append(cbuf[i]);
                    }
                    tainput.setText(docstring.toString());
                  //  lines = Files.readAllLines(file.toPath());
                    /*
                    for (String segment : lines ) {
                        tainput.append(segment);
                        tainput.append("\n");
                    }
                    */
                    tainput.setCaretPosition(0);
                    panelinput.setVisible(true);
                    
                    
                } catch (MalformedInputException m) {
                    bslog(m);
                    bsmf.MainFrame.show("Input file may not be UTF-8 encoded");
                } catch (IOException ex) {
                    bslog(ex);
                    bsmf.MainFrame.show("Error...check data/app.log");
                }   
        }
        }
    }
    
    public void create997(boolean input) {
        taoutput.setText("");
        
        if (! input) {
        getInput();
        
        
            if (! tainput.getText().isEmpty()) { 
                String x = "";
                try {
                    x = generate997(tainput.getText());
                } catch (IOException ex) {
                    bslog(ex);
                }
                taoutput.setText(x);
             }
        }
    }
    
    public void verifyHashSignature(boolean input) {
        taoutput.setText("");
        String mic = "";
        String[] x = new String[]{"","","","","",""};
        if (! tainput.getText().isEmpty()) {
            try { 
                x = verifySignatureView(tainput.getText().getBytes(), "text/plain");
            } catch (MessagingException ex) {
                Logger.getLogger(EDIUtilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EDIUtilities.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            taoutput.setText("boolean: " + x[0] + "\n" + 
                    "Sig hex: " + x[1] + "\n" +  
                    "Data hex: " + x[2] + "\n" +
                    "Sig tostring: " + x[3] + "\n" +
                    "ByteCount FilewHeaders: " + x[4] + "\n" +
                    "ByteCount signature: " + x[5] + "\n");
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

        fc = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        panelbt = new javax.swing.JPanel();
        ddtable = new javax.swing.JComboBox();
        btdir = new javax.swing.JButton();
        btfile = new javax.swing.JButton();
        btrun = new javax.swing.JButton();
        btshowpanels = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        panelboxes = new javax.swing.JPanel();
        paneltb = new javax.swing.JPanel();
        lbkey2 = new javax.swing.JLabel();
        tbkey1 = new javax.swing.JTextField();
        lbkey1 = new javax.swing.JLabel();
        tbkey2 = new javax.swing.JTextField();
        paneltb2 = new javax.swing.JPanel();
        lbkey3 = new javax.swing.JLabel();
        tbkey3 = new javax.swing.JTextField();
        lbkey4 = new javax.swing.JLabel();
        tbkey4 = new javax.swing.JTextField();
        paneldc = new javax.swing.JPanel();
        lbdate1 = new javax.swing.JLabel();
        lbdate2 = new javax.swing.JLabel();
        dcdate2 = new com.toedter.calendar.JDateChooser();
        dcdate1 = new com.toedter.calendar.JDateChooser();
        paneldd = new javax.swing.JPanel();
        ddkey1 = new javax.swing.JComboBox<>();
        ddkey2 = new javax.swing.JComboBox<>();
        lbddkey2 = new javax.swing.JLabel();
        lbddkey1 = new javax.swing.JLabel();
        panelrb = new javax.swing.JPanel();
        rbinactive = new javax.swing.JRadioButton();
        rbactive = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        panelinput = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tainput = new javax.swing.JTextArea();
        paneloutput = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taoutput = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Utilties"));
        jPanel1.setName("panelmain"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1103, 625));

        ddtable.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1.  determine hex value of delimiters in X12 file", "2.  convert delimiter from original to newline in X12 file", "3.  count occurrences of hex character", "4.  get SHA-1 MIC/Digest of content", "5.  decrypt PGP file with passphrase and keyfile", "6.  create 997 from inbound envelope", "7.  verify hash signature" }));
        ddtable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtableActionPerformed(evt);
            }
        });

        btdir.setText("Open Directory");
        btdir.setName("btdir"); // NOI18N
        btdir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdirActionPerformed(evt);
            }
        });

        btfile.setText("Open File");
        btfile.setName("btfile"); // NOI18N
        btfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btfileActionPerformed(evt);
            }
        });

        btrun.setText("Run");
        btrun.setName("btrun"); // NOI18N
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });

        btshowpanels.setText("Show Panels");
        btshowpanels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshowpanelsActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelbtLayout = new javax.swing.GroupLayout(panelbt);
        panelbt.setLayout(panelbtLayout);
        panelbtLayout.setHorizontalGroup(
            panelbtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelbtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ddtable, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btrun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btclear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btfile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btdir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btshowpanels)
                .addContainerGap())
        );
        panelbtLayout.setVerticalGroup(
            panelbtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelbtLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelbtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btrun)
                    .addComponent(btfile)
                    .addComponent(btdir)
                    .addComponent(btshowpanels)
                    .addComponent(btclear))
                .addContainerGap())
        );

        lbkey2.setText("Some Text:");

        lbkey1.setText("Some Text:");

        javax.swing.GroupLayout paneltbLayout = new javax.swing.GroupLayout(paneltb);
        paneltb.setLayout(paneltbLayout);
        paneltbLayout.setHorizontalGroup(
            paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbkey1)
                    .addComponent(lbkey2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbkey2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbkey1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        paneltbLayout.setVerticalGroup(
            paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbkey1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbkey2))
                .addContainerGap())
        );

        lbkey3.setText("Some Text:");

        lbkey4.setText("Some Text:");

        javax.swing.GroupLayout paneltb2Layout = new javax.swing.GroupLayout(paneltb2);
        paneltb2.setLayout(paneltb2Layout);
        paneltb2Layout.setHorizontalGroup(
            paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltb2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbkey4)
                    .addComponent(lbkey3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbkey4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbkey3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        paneltb2Layout.setVerticalGroup(
            paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltb2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbkey4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbkey3))
                .addContainerGap())
        );

        lbdate1.setText("jLabel1");

        lbdate2.setText("jLabel1");

        dcdate2.setDateFormatString("yyyy-MM-dd");

        dcdate1.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout paneldcLayout = new javax.swing.GroupLayout(paneldc);
        paneldc.setLayout(paneldcLayout);
        paneldcLayout.setHorizontalGroup(
            paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneldcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbdate1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbdate2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcdate2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcdate1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        paneldcLayout.setVerticalGroup(
            paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneldcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbdate1)
                    .addComponent(dcdate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbdate2)
                    .addComponent(dcdate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ddkey1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ddkey2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbddkey2.setText("jLabel2");

        lbddkey1.setText("jLabel1");

        javax.swing.GroupLayout panelddLayout = new javax.swing.GroupLayout(paneldd);
        paneldd.setLayout(panelddLayout);
        panelddLayout.setHorizontalGroup(
            panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelddLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbddkey1)
                    .addComponent(lbddkey2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddkey1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddkey2, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        panelddLayout.setVerticalGroup(
            panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelddLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddkey1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbddkey1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddkey2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbddkey2))
                .addContainerGap())
        );

        rbinactive.setText("Inactive");
        rbinactive.setName("cbinactive"); // NOI18N

        rbactive.setText("Active");
        rbactive.setName("cbactive"); // NOI18N

        javax.swing.GroupLayout panelrbLayout = new javax.swing.GroupLayout(panelrb);
        panelrb.setLayout(panelrbLayout);
        panelrbLayout.setHorizontalGroup(
            panelrbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelrbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelrbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbactive)
                    .addComponent(rbinactive))
                .addContainerGap())
        );
        panelrbLayout.setVerticalGroup(
            panelrbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelrbLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbactive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbinactive)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelboxesLayout = new javax.swing.GroupLayout(panelboxes);
        panelboxes.setLayout(panelboxesLayout);
        panelboxesLayout.setHorizontalGroup(
            panelboxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelboxesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paneltb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(paneltb2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(paneldc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(paneldd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelrb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelboxesLayout.setVerticalGroup(
            panelboxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelboxesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelboxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelboxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelboxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(paneldc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(paneltb2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(paneltb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(paneldd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelrb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelboxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelbt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(panelbt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelboxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        panelinput.setBorder(javax.swing.BorderFactory.createTitledBorder("Input"));
        panelinput.setName("panelinput"); // NOI18N
        panelinput.setPreferredSize(new java.awt.Dimension(300, 475));

        tainput.setColumns(20);
        tainput.setRows(5);
        jScrollPane2.setViewportView(tainput);

        javax.swing.GroupLayout panelinputLayout = new javax.swing.GroupLayout(panelinput);
        panelinput.setLayout(panelinputLayout);
        panelinputLayout.setHorizontalGroup(
            panelinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelinputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelinputLayout.setVerticalGroup(
            panelinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
        );

        jPanel4.add(panelinput);

        paneloutput.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));
        paneloutput.setName("paneloutput"); // NOI18N
        paneloutput.setPreferredSize(new java.awt.Dimension(300, 475));

        taoutput.setColumns(20);
        taoutput.setRows(5);
        jScrollPane1.setViewportView(taoutput);

        javax.swing.GroupLayout paneloutputLayout = new javax.swing.GroupLayout(paneloutput);
        paneloutput.setLayout(paneloutputLayout);
        paneloutputLayout.setHorizontalGroup(
            paneloutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
        );
        paneloutputLayout.setVerticalGroup(
            paneloutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
        );

        jPanel4.add(paneloutput);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1103, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(440, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(165, 165, 165)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btrunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrunActionPerformed
        
        processAction(false);
        
        
        
    }//GEN-LAST:event_btrunActionPerformed

    private void ddtableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtableActionPerformed
        if (! isLoad) {
            processAction(true);
        }
    }//GEN-LAST:event_ddtableActionPerformed

    private void btfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btfileActionPerformed
             
        
    }//GEN-LAST:event_btfileActionPerformed

    private void btdirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btdirActionPerformed

    private void btshowpanelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshowpanelsActionPerformed
       panelinput.setVisible(true);
       paneloutput.setVisible(true);
    }//GEN-LAST:event_btshowpanelsActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        taoutput.setText("");
        tainput.setText("");
    }//GEN-LAST:event_btclearActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdir;
    private javax.swing.JButton btfile;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btshowpanels;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser dcdate1;
    private com.toedter.calendar.JDateChooser dcdate2;
    private javax.swing.JComboBox<String> ddkey1;
    private javax.swing.JComboBox<String> ddkey2;
    private javax.swing.JComboBox ddtable;
    private javax.swing.JFileChooser fc;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbdate1;
    private javax.swing.JLabel lbdate2;
    private javax.swing.JLabel lbddkey1;
    private javax.swing.JLabel lbddkey2;
    private javax.swing.JLabel lbkey1;
    private javax.swing.JLabel lbkey2;
    private javax.swing.JLabel lbkey3;
    private javax.swing.JLabel lbkey4;
    private javax.swing.JPanel panelboxes;
    private javax.swing.JPanel panelbt;
    private javax.swing.JPanel paneldc;
    private javax.swing.JPanel paneldd;
    private javax.swing.JPanel panelinput;
    private javax.swing.JPanel paneloutput;
    private javax.swing.JPanel panelrb;
    private javax.swing.JPanel paneltb;
    private javax.swing.JPanel paneltb2;
    private javax.swing.JRadioButton rbactive;
    private javax.swing.JRadioButton rbinactive;
    private javax.swing.JTextArea tainput;
    private javax.swing.JTextArea taoutput;
    private javax.swing.JTextField tbkey1;
    private javax.swing.JTextField tbkey2;
    private javax.swing.JTextField tbkey3;
    private javax.swing.JTextField tbkey4;
    // End of variables declaration//GEN-END:variables
}
