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

package com.blueseer.edi;

import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author vaughnte
 */
public class EDILoadPanel extends javax.swing.JPanel {

      MyTableModel mymodel = new MyTableModel(new Object[][]{},
                    new String[]{"File", "Load?"});
    String inDir = OVData.getEDIInDir();
    String inArch = OVData.getEDIInArch(); 
    
    
     public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

          CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            
          }

          public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              //super.setBackground(table.getSelectionBackground());
              setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            setSelected((value != null && ((Boolean) value).booleanValue()));
            return this;
          }
} 
    
     
      class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
      
     //     public Class getColumnClass(int col) {  
     //       if (col == 6)       
     //           return Double.class;  
     //       else return String.class;  //other columns accept String values  
    //    }  
        
        public Class getColumnClass(int column) {
            
            
               if (column == 1)       
                return Boolean.class; 
            else return String.class;  //other columns accept String values 
            
       /*     
      if (column >= 0 && column < getColumnCount()) {
          
          
           if (getRowCount() > 0) {
             // you need to check 
             Object value = getValueAt(0, column);
             // a line for robustness (in real code you probably would loop all rows until
             // finding a not-null value 
             if (value != null) {
                return value.getClass();
             }

        }
          
          
          
      }  
              
        return Object.class;
*/
               }
       
        
        
   }    
     
    
     class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            if (column == 0)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
    
     class myTask extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() throws IOException, FileNotFoundException, ClassNotFoundException {
            EDI.processFileCmdLine(getFileName(),"","","");
            return null;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
              BlueSeerUtils.endTask(new String[]{"","Done"});          
        }
    } 
    
     
     
    /**
     * Creates new form EDILoadPanel
     */
    public EDILoadPanel() {
        initComponents();
    }

     public void readFile(String file, String dir) throws MalformedURLException, SmbException {
    tafile.setText("");
//    File folder = new File("smb://10.17.2.55/edi");
  // File[] listOfFiles = folder.listFiles();

  String inDir = OVData.getEDIInDir();
  
    
  SmbFile smbfile = null;
  File edifile = null;
  
 //   NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, null, null);
    NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
   // SmbFile folder = new SmbFile("smb://10.17.2.55/edi/", auth);
  
  //  file = "smb://10.17.2.5/edi/rawin/" + file;
    file = inDir + "/" + file;
 
    
    
    if (file.startsWith("smb")) {
     smbfile = new SmbFile(file, auth);
    
   // SmbFile[] listOfFiles = folder.listFiles();
    ArrayList<String> segments = new ArrayList<String>();    
        try{
          BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new SmbFileInputStream(smbfile))));
          char[] cbuf = new char[(int) smbfile.length()];
          
          reader.read(cbuf,0,cbuf.length); 
          reader.close();
         
        // bsmf.MainFrame.show(String.valueOf(cbuf.length) + " " + String.valueOf(cbuf[8192]));
        int i = 0;
        if (smbfile.toString().toUpperCase().endsWith(".XML") ) {
            tafile.append(parseXMLsmb(smbfile));
        } else {
            segments = parseFile(cbuf, smbfile.getName());
            for (String segment : segments ) {
                i++;
                tafile.append(segment);
                tafile.append("\n");
            }
        }
       }catch(Exception e){
          System.out.println("Error while reading smb file line by line:" + e.getMessage());                      
       }
        
    }   else {
        // must be a non smb file
       edifile = new File(file);
    
   // SmbFile[] listOfFiles = folder.listFiles();
    ArrayList<String> segments = new ArrayList<String>();    
        try{
          BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(edifile))));
          char[] cbuf = new char[(int) edifile.length()];
          
          reader.read(cbuf,0,cbuf.length); 
          reader.close();
         
        // bsmf.MainFrame.show(String.valueOf(cbuf.length) + " " + String.valueOf(cbuf[8192]));
        int i = 0;
        if (edifile.toString().toUpperCase().endsWith(".XML") ) {
            tafile.append(parseXML(edifile));
        } else {
            segments = parseFile(cbuf, edifile.getName());
            for (String segment : segments ) {
                i++;
                tafile.append(segment);
                tafile.append("\n");
            }
        }
       }catch(Exception e){
          System.out.println("Error while reading smb file line by line:" + e.getMessage());                      
       } 
    }
        
        
        
        
    }
    
       public static String parseXMLsmb(SmbFile edifile) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        String xmlString = "";
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(edifile.getInputStream());

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        //initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        xmlString = result.getWriter().toString();
        return xmlString;
    }
    
     public static String parseXML(File edifile) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        String xmlString = "";
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(edifile);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        //initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        xmlString = result.getWriter().toString();
        return xmlString;
    }
    
    public static ArrayList parseFile(char[] cbuf, String filename)   {
    ArrayList<String> doc = new ArrayList<String>();
    
     char flddelim = 0;
    char subdelim = 0;
    char segdelim = 0;
    
    String flddelim_str = "";
    String subdelim_str = "";
    String segdelim_str = "";
    
    /* lets set the delimiters X12 */
    if (cbuf[0] == 'I' &&
        cbuf[1] == 'S' &&
        cbuf[2] == 'A') {
           flddelim = cbuf[103];
           subdelim = cbuf[104];
           segdelim = cbuf[105];
        flddelim_str = String.valueOf(flddelim);
        subdelim_str = String.valueOf(subdelim);
        segdelim_str = String.valueOf(segdelim);
        
        if (flddelim_str.equals("*")) {
            flddelim_str = "\\*";
        }
        if (flddelim_str.equals("^")) {
            flddelim_str = "\\^";
        }
              
    } 
    
     /* lets set the delimiters EDIFACT */
    if (cbuf[0] == 'U' &&
        cbuf[1] == 'N' &&
        cbuf[2] == 'B') {
           flddelim = '+';
           subdelim = ':';
           segdelim = '\'';
        flddelim_str = String.valueOf(flddelim);
        subdelim_str = String.valueOf(subdelim);
        segdelim_str = String.valueOf(segdelim);
        
        if (flddelim_str.equals("*")) {
            flddelim_str = "\\*";
        }
        if (flddelim_str.equals("^")) {
            flddelim_str = "\\^";
        }
              
    } 
    
    /* lets set delimiters for CSV */
    if (filename.toString().toUpperCase().endsWith(".CSV") ) {
           flddelim = ',';
           subdelim = ':';
           segdelim = '\n';
        flddelim_str = String.valueOf(flddelim);
        subdelim_str = String.valueOf(subdelim);
        segdelim_str = String.valueOf(segdelim);
    }
    
    /* lets set delimiters for XML */
    if (filename.toString().toUpperCase().endsWith(".XML") ) {
           segdelim = '\n';
        segdelim_str = String.valueOf(segdelim);
    }
    
    
    
    StringBuilder segment = new StringBuilder();
    if (filename.toString().toUpperCase().endsWith(".XML") ) {
        for (int i = 0; i < cbuf.length; i++) {
        segment.append(cbuf[i]);
        }
        doc.add(segment.toString());
    } else {
    for (int i = 0; i < cbuf.length; i++) {
        if (cbuf[i] == segdelim) {
            doc.add(segment.toString());
            segment.delete(0, segment.length());
        } else {
        segment.append(cbuf[i]);
        }
    }
    }
    
    return doc;
    }
     
      public String getFileName() {
        String filename = "";
        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                filename = fc.getSelectedFile().getAbsolutePath();
                BlueSeerUtils.startTask(new String[]{"","Processing..."});
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
        return filename;
    }
    
    public void initvars(String[] arg) throws MalformedURLException, SmbException {
    tafile.setText("");
   
    File folder = new File(inDir);
   File[] listOfFiles = folder.listFiles();
   mymodel.setNumRows(0);
   tablereport.setModel(mymodel);
   //tablereport.getColumnModel().getColumn(1).setCellRenderer(new SomeRenderer()); 
   
   CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
   tablereport.getColumnModel().getColumn(1).setCellRenderer(checkBoxRenderer); 
   tablereport.getColumnModel().getColumn(0).setCellRenderer(new SomeRenderer()); 
   boolean toggle = false;

   
   
    
 //   NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, null, null);
  //  NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
 //   SmbFile folder = new SmbFile("smb://10.17.2.55/edi/", auth);
 //   SmbFile[] listOfFiles = folder.listFiles();
     if (listOfFiles != null) 
     for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
         mymodel.addRow(new Object[]{
                  listOfFiles[i].getName(),
                   toggle
                    });
      } 
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
        jPanel1 = new javax.swing.JPanel();
        btProcess = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tafile = new javax.swing.JTextArea();
        cbtoggle = new javax.swing.JCheckBox();
        btmanual = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDI Inbound Loading"));

        btProcess.setText("Process");
        btProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btProcessActionPerformed(evt);
            }
        });

        tablereport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablereport);

        tafile.setColumns(20);
        tafile.setRows(5);
        jScrollPane3.setViewportView(tafile);

        cbtoggle.setText("Load Toggle");
        cbtoggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbtoggleActionPerformed(evt);
            }
        });

        btmanual.setText("Manual Search");
        btmanual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmanualActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btmanual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbtoggle))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btProcess)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(btProcess, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbtoggle)
                    .addComponent(btmanual))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btProcessActionPerformed
       int j = 0;  
       tafile.setText("");
       try {
           
            for (int i = 0 ; i < mymodel.getRowCount(); i++) {    
                 if ( (boolean) mymodel.getValueAt(i, 1) ) {
                     String infile = inDir + "/" + mymodel.getValueAt(i,0).toString();
                     
                    // File edifile = new File(inDir + "/" + mymodel.getValueAt(i,0).toString());
                    // EDI.processFile(edifile);
                     EDI.processFileCmdLine(infile,"","","");
                    
                         // now archive file
                         if (! inArch.isEmpty() && OVData.getEDIArchFlag()) {
                         Path movefrom = FileSystems.getDefault().getPath(inDir + "/" + mymodel.getValueAt(i,0).toString());
                         Path target = FileSystems.getDefault().getPath(inArch + "/" + mymodel.getValueAt(i,0).toString());
                         Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);
                          // now remove from list
                         }
                         
                       mymodel.removeRow(i);   
                         
                     j++;
                 }
             }
              
            bsmf.MainFrame.show("Processed " + String.valueOf(j) + " files");
            
       } catch (IOException ex) {
           ex.printStackTrace();
          bsmf.MainFrame.show("ioexception");
         
       }  catch (ClassNotFoundException ex) {
          bsmf.MainFrame.show("classexception");
       }
         
    }//GEN-LAST:event_btProcessActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
          int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, col).toString().isEmpty()) {
                 ArrayList<String> segments = OVData.readEDIRawFileLiveDirIntoArrayList(tablereport.getValueAt(row, col).toString(), "In");  
                    for (String segment : segments ) {
                        tafile.append(segment);
                        tafile.append("\n");
                    }
                 }
             } catch (MalformedURLException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (SmbException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             }
           
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void cbtoggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbtoggleActionPerformed
        for (int i = 0 ; i < mymodel.getRowCount(); i++) {  
            mymodel.setValueAt(cbtoggle.isSelected(), i, 1);
        }
    }//GEN-LAST:event_cbtoggleActionPerformed

    private void btmanualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmanualActionPerformed
        myTask task = new myTask();
        task.execute();  
    }//GEN-LAST:event_btmanualActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btProcess;
    private javax.swing.JButton btmanual;
    private javax.swing.JCheckBox cbtoggle;
    private javax.swing.JFileChooser fc;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextArea tafile;
    // End of variables declaration//GEN-END:variables
}
