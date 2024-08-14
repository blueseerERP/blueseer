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

import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.blueseer.utl.EDData;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
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
public class EDILoadMaint extends javax.swing.JPanel {

      MyTableModel mymodel = new MyTableModel(new Object[][]{},
                    new String[]{"File", "Load?"});
    
    private static String inDir = cleanDirString(EDData.getEDIInDir());
    private static String inArch = cleanDirString(EDData.getEDIInArch()); 
    private static String ErrorDir = cleanDirString(EDData.getEDIErrorDir()); 
    public static String rData = "";
    public static String rFileList = "";
    public static String rFileContent = "";
    
    
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
        String filepath;
        boolean isdebug;
         
        public myTask(String filepath, boolean isdebug) { 
              this.filepath = filepath;
              this.isdebug = isdebug;
          }
         
        @Override
        public Void doInBackground() throws IOException, FileNotFoundException, ClassNotFoundException {
            EDI.processFile(this.filepath,"","","", this.isdebug, false, 0, 0);
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
    public EDILoadMaint() {
        initComponents();
        setLanguageTags(this);
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
    
    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
         
          String action = "";
          String[] key = null;
          
          public Task(String action, String[] key) { 
              this.action = action;
              this.key = key;
          }     
            
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            rData = "";
            rFileList = "";
            rFileContent = "";
            
            
            switch(this.action) {
                case "process":
                    if (bsmf.MainFrame.remoteDB) {
                        message = processFilesPost();
                    } else {
                        message = processFiles(); 
                    }
                    break;
                case "getFiles":
                    ArrayList<String[]> arr = new ArrayList<String[]>();
                    arr.add(new String[]{"id","getFilesOfDir"});
                    arr.add(new String[]{"dir", inDir});
                    rFileList = sendServerPost(arr, "");
                    break;
                case "getFileContent":
                    ArrayList<String[]> arrx = new ArrayList<String[]>();
                    arrx.add(new String[]{"id","getFileContent"});
                    arrx.add(new String[]{"filepath", key[0]});
                    rFileContent = sendServerPost(arrx, "");
                    break;    
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            
            
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
            initvars(null);  
            updateForm();
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
   
    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        JToolBar toolbarpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj; 
        } else if (myobj instanceof JToolBar) {
           toolbarpane = (JToolBar) myobj;      
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
                if (component instanceof JToolBar) {
                    setPanelComponentState((JToolBar) component, b);
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
            if (toolbarpane != null) {
                toolbarpane.setEnabled(b);
                Component[] componentspane = toolbarpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
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
    
    
    public void readFile(String file, String dir) throws MalformedURLException, SmbException {
    tafile.setText("");
//    File folder = new File("smb://10.17.2.55/edi");
  // File[] listOfFiles = folder.listFiles();

  String inDir = cleanDirString(EDData.getEDIInDir());
  
    
  SmbFile smbfile = null;
  File edifile = null;
  
 //   NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, null, null);
    NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
   // SmbFile folder = new SmbFile("smb://10.17.2.55/edi/", auth);
  
  //  file = "smb://10.17.2.5/edi/rawin/" + file;
    file = inDir + file;
 
    
    
    if (file.startsWith("smb")) {
     smbfile = new SmbFile(file, auth);
    
   // SmbFile[] listOfFiles = folder.listFiles();
    ArrayList<String> segments = new ArrayList<String>();    
        try{
          BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new SmbFileInputStream(smbfile))));
          char[] cbuf = new char[(int) smbfile.length()];
          
          reader.read(cbuf,0,cbuf.length); 
          reader.close();
         
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
          tafile.append("Error while reading smb file line by line:" + e.getMessage()  + "\n");                      
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
     
    public String[] processFiles() {
        String[] x = new String[2];
        
        int j = 0;  
       tafile.setText("");
       try {
            
            
            for (int i = 0 ; i < mymodel.getRowCount(); i++) {    
                 if ( (boolean) mymodel.getValueAt(i, 1) ) {
                    String infile = inDir + mymodel.getValueAt(i,0).toString();
                    tafile.append("FilePath: " + infile  + "\n");
                    String[] m = EDI.processFile(infile,"","","", cbdebug.isSelected(), false, 0, 0);
                    
                    // show error if exists...usually malformed envelopes
                    if (m[0].equals("1")) {
                       tafile.append(m[1]  + "\n");
                        if (! cbno.isSelected()) {
                        Path movefrom = FileSystems.getDefault().getPath(inDir + mymodel.getValueAt(i,0).toString());
                        Path target = FileSystems.getDefault().getPath(inArch + mymodel.getValueAt(i,0).toString());
                        Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);
                        }
                        continue;  // bale from here
                    }
                    
                     if (! cbno.isSelected()) {
                         // if delete set in control panel...remove file and continue;
                         if (EDData.isEDIDeleteFlag()) {
                          Path filetodelete = FileSystems.getDefault().getPath(inDir + mymodel.getValueAt(i,0).toString());
                          Files.delete(filetodelete);
                         }
                     
                         // now archive file
                         if (! inArch.isEmpty() && ! EDData.isEDIDeleteFlag() && EDData.isEDIArchFlag() ) {
                         Path movefrom = FileSystems.getDefault().getPath(inDir + mymodel.getValueAt(i,0).toString());
                         Path target = FileSystems.getDefault().getPath(inArch + mymodel.getValueAt(i,0).toString());
                        Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);
                          // now remove from list
                         }
                     }
                         
                     //  mymodel.removeRow(i);   
                         
                     j++;
                 }
             }
              
            tafile.append(getMessageTag(1121,String.valueOf(j)) + "\n");
            
            getFiles();
            x[0] = "0";
            x[1] = "processing complete";
            
       } catch (IOException ex) {
           ex.printStackTrace();
          tafile.append(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()) + "\n");
         
       }  catch (ClassNotFoundException ex) {
          tafile.append(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()) + "\n");
       }
        
        return x;
    }
    
    public String[] processFilesPost() throws IOException {
        String[] x = new String[2];
        int j = 0;  
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < mymodel.getRowCount(); i++) {    
                 if ( (boolean) mymodel.getValueAt(i, 1) ) {
                    sb.append(mymodel.getValueAt(i,0).toString());
                    sb.append(",");
                 }
        }
        String postData = sb.toString();
        if (postData.endsWith(",")) {
                    postData = postData.substring(0, postData.length() - 1);
        }
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","runEDI"});
        
        rData = sendServerPost(list, postData);
        
        x[0] = "0";
        x[1] = "Processing complete";
       
        return x;
    }
    
    public void updateForm() {
        
        
        
        if (rData != null && ! rData.isBlank()) {
            tafile.setText("");
            String[] arr = rData.split("\n", -1);
            for (String s : arr) {
                tafile.append(s);
            }
        }
        if (rFileList != null && ! rFileList.isBlank()) {
            tafile.setText("");
            mymodel.setNumRows(0);
            String[] arr = rFileList.split("\n", -1);
            for (String s : arr) {
                if (! s.isBlank()) 
                mymodel.addRow(new Object[]{
                s,
                false
                });
            }
            lbcount.setText(String.valueOf(mymodel.getRowCount()));
        }
        
        if (rFileContent != null && ! rFileContent.isBlank()) {
            tafile.setText("");
            String[] arr = rFileContent.split("\n", -1);
            for (String s : arr) {
                tafile.append(s);
                tafile.append("\n");
            }
        }
        
        
    }
    
    public String getFileName() {
        String filename = "";
        JFileChooser jfc = new JFileChooser(FileSystems.getDefault().getPath("edi").toFile());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = jfc.showOpenDialog(this);
        

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                filename = jfc.getSelectedFile().getAbsolutePath();
                BlueSeerUtils.startTask(new String[]{"","Processing..."});
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
        return filename;
    }
    
    public void getFiles() {
         
   
   File folder = new File(inDir);
   File[] listOfFiles = folder.listFiles();
   mymodel.setNumRows(0);
   tablereport.setModel(mymodel);
   //tablereport.getColumnModel().getColumn(1).setCellRenderer(new SomeRenderer()); 
   
   
   boolean toggle = false;
     if (listOfFiles != null) 
     for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
         mymodel.addRow(new Object[]{
                  listOfFiles[i].getName(),
                   toggle
                    });
      } 
     }
     
     lbcount.setText(String.valueOf(mymodel.getRowCount()));
    }  
    
    
    
    public void initvars(String[] arg) throws MalformedURLException, SmbException {
    
    setPanelComponentState(this, true);
    CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
    tablereport.getColumnModel().getColumn(1).setCellRenderer(checkBoxRenderer); 
    tablereport.getColumnModel().getColumn(0).setCellRenderer(new SomeRenderer()); 
    tablereport.setModel(mymodel);
    tafile.setFont(new Font("monospaced", Font.PLAIN, 12));
    //getFiles();        
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
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tafile = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        cbtoggle = new javax.swing.JCheckBox();
        btProcess = new javax.swing.JButton();
        btrefresh = new javax.swing.JButton();
        btmanual = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lbcount = new javax.swing.JLabel();
        cbdebug = new javax.swing.JCheckBox();
        cbno = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDI Inbound Loading"));
        jPanel1.setName("panelmain"); // NOI18N

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cbtoggle.setText("Load Toggle");
        cbtoggle.setName("cbtoggle"); // NOI18N
        cbtoggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbtoggleActionPerformed(evt);
            }
        });

        btProcess.setText("Run");
        btProcess.setName("btrun"); // NOI18N
        btProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btProcessActionPerformed(evt);
            }
        });

        btrefresh.setText("Refresh List");
        btrefresh.setName("btrefresh"); // NOI18N
        btrefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrefreshActionPerformed(evt);
            }
        });

        btmanual.setText("Manual Search");
        btmanual.setName("btsearch"); // NOI18N
        btmanual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmanualActionPerformed(evt);
            }
        });

        jLabel1.setText("Files:");
        jLabel1.setName("lblcount"); // NOI18N

        cbdebug.setText("Debug");
        cbdebug.setName("cbdebug"); // NOI18N

        cbno.setText("No Delete/Archive");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbtoggle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btrefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btProcess)
                        .addGap(74, 74, 74)
                        .addComponent(cbdebug)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbno)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btmanual, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbtoggle)
                    .addComponent(lbcount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btrefresh)
                        .addComponent(jLabel1)
                        .addComponent(btProcess)
                        .addComponent(cbdebug)
                        .addComponent(cbno)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(btmanual)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btProcessActionPerformed
        tafile.setText("");
        
        int count = 0;
        for (int i = 0 ; i < mymodel.getRowCount(); i++) {    
             if ( (boolean) mymodel.getValueAt(i, 1) ) {
                 count++;
             }
        }
        
        if (cbdebug.isSelected() && count > 1) {
            bsmf.MainFrame.show("debug option is selected...only one file can be processed");
            return;
        }
        
        setPanelComponentState(this, false);
        
        
        tafile.append("Loading " + String.valueOf(count) + " File(s)..." + "\n");
        
        executeTask("process", null);
    }//GEN-LAST:event_btProcessActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
          int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
             if (bsmf.MainFrame.remoteDB) {
            executeTask("getFileContent", new String[]{cleanDirString(EDData.getEDIInDir()) + tablereport.getValueAt(row, col).toString()});
        } else {
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, col).toString().isEmpty()) {
               //  ArrayList<String> segments = EDData.readEDIRawFileLiveDirIntoArrayList(tablereport.getValueAt(row, col).toString(), "In");  
                  File infile = new File(cleanDirString(EDData.getEDIInDir()) + tablereport.getValueAt(row, col).toString());
                  Path path = FileSystems.getDefault().getPath(infile.getAbsolutePath());
                  List<String> segments = Files.readAllLines(infile.toPath());
                  
                    for (String segment : segments ) {
                        tafile.append(segment);
                        tafile.append("\n");
                    }
                    
                    tafile.setCaretPosition(0);
                    
                 }
             } catch (MalformedURLException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (SmbException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void cbtoggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbtoggleActionPerformed
        for (int i = 0 ; i < mymodel.getRowCount(); i++) {  
            mymodel.setValueAt(cbtoggle.isSelected(), i, 1);
        }
    }//GEN-LAST:event_cbtoggleActionPerformed

    private void btmanualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmanualActionPerformed
        String filepath = getFileName();
        myTask task = new myTask(filepath, cbdebug.isSelected());
        task.execute();  
    }//GEN-LAST:event_btmanualActionPerformed

    private void btrefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrefreshActionPerformed
        tafile.setText("");
        if (bsmf.MainFrame.remoteDB) {
            executeTask("getFiles", null);
        } else {
            getFiles();
        }
    }//GEN-LAST:event_btrefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btProcess;
    private javax.swing.JButton btmanual;
    private javax.swing.JButton btrefresh;
    private javax.swing.JCheckBox cbdebug;
    private javax.swing.JCheckBox cbno;
    private javax.swing.JCheckBox cbtoggle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbcount;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextArea tafile;
    // End of variables declaration//GEN-END:variables
}
