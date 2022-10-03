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

import com.blueseer.rcv.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.edi.EDI.createIMAP;
import static com.blueseer.edi.EDI.edilog;
import static com.blueseer.edi.EDI.getEDIType;
import com.blueseer.edi.EDIMap.UserDefinedException;
import static com.blueseer.edi.MapMaint.x;
import static com.blueseer.edi.ediData.addMapMstr;
import static com.blueseer.edi.ediData.deleteMapMstr;
import static com.blueseer.edi.ediData.getMapMstr;
import com.blueseer.edi.ediData.map_mstr;
import static com.blueseer.edi.ediData.updateMapMstr;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.EDData;
import com.blueseer.utl.IBlueSeerT;
import com.blueseer.vdr.venData;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 *
 * @author vaughnte
 */
public class MapTester extends javax.swing.JPanel implements IBlueSeerT  {
 
     public Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
     File infile = null;
     // global variable declarations
                boolean isLoad = false;
                public static map_mstr x = null;
                
                
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("select"), 
                            getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("id"),
                            getGlobalColumnTag("vendor"), 
                            getGlobalColumnTag("packingslip"), 
                            getGlobalColumnTag("recvdate"), 
                            getGlobalColumnTag("status"), 
                            getGlobalColumnTag("reference"), 
                            getGlobalColumnTag("remarks")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 1 )       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("id"), 
                            getGlobalColumnTag("po"),
                            getGlobalColumnTag("line"), 
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("packingslip"), 
                            getGlobalColumnTag("recvdate"), 
                            getGlobalColumnTag("netprice"), 
                            getGlobalColumnTag("recvqty"), 
                            getGlobalColumnTag("vouchqty")});
    
     class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
   
    public class MyClassLoader extends ClassLoader {
    
        private String classFileLocation;
    
        public MyClassLoader(String classFileLocation) {
            this.classFileLocation = classFileLocation;
        }
    
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] classBytes = loadClassBytesFromDisk(classFileLocation);
            return defineClass(name, classBytes, 0, classBytes.length);
        }
    
        private byte []  loadClassBytesFromDisk(String classFileLocation) {
            try {
                return Files.readAllBytes(Paths.get(classFileLocation));
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to read file from disk");
            }
        }
    }
    
    class JavaSourceFromString extends SimpleJavaFileObject {
      final String code;

      JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
            this.code = code;
          }

          @Override
          public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
          }
}
    
    /**
     * Creates new form ScrapReportPanel
     */
    public MapTester() {
        initComponents();
        setLanguageTags(this);
    }

           // interface functions implemented
    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "add":
                    message = addRecord(key);
                    break;
                case "update":
                    message = updateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
                    break;
                case "get":
                    message = getRecord(key);    
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
           if (this.type.equals("delete")) {
             initvars(null);  
           } else if (this.type.equals("get")) {
             updateForm();
             tbkey.requestFocus();
           } else {
             initvars(null);  
           }
            
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
        tbkey.setText("");
        tbdesc.setText("");
        tbversion.setText("");
        
        ddofs.removeAllItems();
        ddifs.removeAllItems();
        ArrayList<String> structs = ediData.getMapStructList();
        for (int i = 0; i < structs.size(); i++) {
            ddofs.addItem(structs.get(i));
        }
        for (int i = 0; i < structs.size(); i++) {
            ddifs.addItem(structs.get(i));
        }
        
        ddoutdoctype.removeAllItems();
        ddindoctype.removeAllItems();
        ArrayList<String> mylist = OVData.getCodeMstrKeyList("edidoctype");
        for (int i = 0; i < mylist.size(); i++) {
            ddoutdoctype.addItem(mylist.get(i));
            ddindoctype.addItem(mylist.get(i));
        }
        
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btadd.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        if (x[0].equals("0")) { 
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
        } else {
                   tbkey.setForeground(Color.red); 
        }
    }
     
    public boolean validateInput(BlueSeerUtils.dbaction x) {
        Map<String,Integer> f = OVData.getTableInfo("map_mstr");
        int fc;

        fc = checkLength(f,"map_id");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }

         fc = checkLength(f,"map_desc");
        if (tbdesc.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbdesc.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"map_version");
        if (tbversion.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbversion.requestFocus();
            return false;
        }
       
         if (! BlueSeerUtils.isEDIClassFile(tbkey.getText())) {
                    bsmf.MainFrame.show(getMessageTag(1145,tbkey.getText()));
                    tbkey.requestFocus();
                    return false;
        }
        
      return true;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btadd.setEnabled(true);
        btlookup.setEnabled(true);
      
       
        
        if (arg != null && arg.length > 0) {
            executeTask(BlueSeerUtils.dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
   
    public String[] getRecord(String[] key) {
        x = getMapMstr(key);   
        return x.m();
    }
    
    public map_mstr createRecord() { 
        map_mstr x = new map_mstr(null, tbkey.getText(),
                tbdesc.getText(),
                tbversion.getText(),
                ddifs.getSelectedItem().toString(),
                ddofs.getSelectedItem().toString(),
                ddindoctype.getSelectedItem().toString(),
                ddinfiletype.getSelectedItem().toString(),
                ddoutdoctype.getSelectedItem().toString(),
                ddoutfiletype.getSelectedItem().toString()
                );
        /* potential validation mechanism...would need association between record field and input field
        for(Field f : x.getClass().getDeclaredFields()){
        System.out.println(f.getName());
        }
        */
        return x;
    }
       
    public String[] addRecord(String[] key) {
         String[] m = addMapMstr(createRecord());
         return m;
    }
        
    public String[] updateRecord(String[] key) {
         String[] m = updateMapMstr(createRecord());
         return m;
    }
    
    public String[] deleteRecord(String[] key) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteMapMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getMapBrowseUtil(luinput.getText(),0, "map_id"); 
        } else {
         luModel = DTData.getMapBrowseUtil(luinput.getText(),0, "map_desc");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                initvars(new String[]{target.getValueAt(row,1).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName())); 
        
        
    }

    public void updateForm() {
        tbdesc.setText(x.map_desc());
        tbkey.setText(x.map_id());
        tbversion.setText(x.map_version());
        ddofs.setSelectedItem(x.map_ofs());
        ddifs.setSelectedItem(x.map_ifs());
        ddindoctype.setSelectedItem(x.map_indoctype());
        ddinfiletype.setSelectedItem(x.map_infiletype());
        ddoutdoctype.setSelectedItem(x.map_outdoctype());
        ddoutfiletype.setSelectedItem(x.map_outfiletype());
        setAction(x.m()); 
    }
    
    
    
    public void getdetail(String rvid) {
      
         modeldetail.setNumRows(0);
         double total = 0;
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                
                res = st.executeQuery("select rvd_id, rvd_po, rvd_poline, rvd_item, rvd_packingslip, rvd_date, rvd_netprice, rvd_qty, rvd_voqty " +
                        " from recv_det " +
                        " where rvd_id = " + "'" + rvid + "'" + ";");
                while (res.next()) {
                   modeldetail.addRow(new Object[]{ 
                      res.getString("rvd_id"), 
                       res.getString("rvd_po"),
                       res.getString("rvd_poline"),
                       res.getString("rvd_item"),
                       res.getString("rvd_packingslip"),
                       res.getString("rvd_date"),
                       currformatDouble(res.getDouble("rvd_netprice")),
                      res.getInt("rvd_qty"), 
                      res.getInt("rvd_voqty")});
                }
               
              
            

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public File getfile() {
        
        File file = null;
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            file = new File(SourceDir);
            
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
        return file;
    }
    
    public File getMapfile() {
        
        File file = null;
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            file = new File(SourceDir);
            
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
        return file;
    }
    
     private void addToJar(File source, JarOutputStream target) throws IOException
    {
        BufferedInputStream in = null;
        try
        {
            if (source.isDirectory())
            {
                String name = source.getPath().replace("\\", "/");
                if (!name.isEmpty())
                {
                    if (!name.endsWith("/"))
                        name += "/";
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile: source.listFiles())
                    addToJar(nestedFile, target);
                return;
            }
            
            JarEntry entry = new JarEntry(source.getPath().replace("\\", ""));
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source.getPath().replace("\\", "")));

            byte[] buffer = new byte[1024];
            while (true)
            {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        }
        finally
        {
            if (in != null)
                in.close();
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
        tablepanel = new javax.swing.JPanel();
        inputpanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tamap = new javax.swing.JTextArea();
        outputpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tasource = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        taoutput = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnew = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btcompile = new javax.swing.JButton();
        btinput = new javax.swing.JButton();
        btoutput = new javax.swing.JButton();
        bthide = new javax.swing.JButton();
        btunhide = new javax.swing.JButton();
        btrun = new javax.swing.JButton();
        btshiftleft = new javax.swing.JButton();
        btshiftright = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        tbpath = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        ddifs = new javax.swing.JComboBox<>();
        ddofs = new javax.swing.JComboBox<>();
        ddindoctype = new javax.swing.JComboBox<>();
        ddoutdoctype = new javax.swing.JComboBox<>();
        ddinfiletype = new javax.swing.JComboBox<>();
        ddoutfiletype = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tbversion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Mapper"));
        jPanel1.setName("panelmain"); // NOI18N

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        inputpanel.setLayout(new javax.swing.BoxLayout(inputpanel, javax.swing.BoxLayout.LINE_AXIS));

        tamap.setColumns(20);
        tamap.setRows(5);
        jScrollPane4.setViewportView(tamap);

        inputpanel.add(jScrollPane4);

        tablepanel.add(inputpanel);

        outputpanel.setLayout(new javax.swing.BoxLayout(outputpanel, javax.swing.BoxLayout.Y_AXIS));

        tasource.setColumns(20);
        tasource.setRows(5);
        jScrollPane3.setViewportView(tasource);

        outputpanel.add(jScrollPane3);

        taoutput.setColumns(20);
        taoutput.setRows(5);
        jScrollPane5.setViewportView(taoutput);

        outputpanel.add(jScrollPane5);

        tablepanel.add(outputpanel);

        jLabel6.setText("Map");
        jLabel6.setName("lblfromreceiver"); // NOI18N

        jToolBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/file.png"))); // NOI18N
        btnew.setFocusable(false);
        btnew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnew);

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/checkout.png"))); // NOI18N
        btlookup.setFocusable(false);
        btlookup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btlookup.setName("btlookup"); // NOI18N
        btlookup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });
        jToolBar1.add(btlookup);

        btadd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addfile.png"))); // NOI18N
        btadd.setFocusable(false);
        btadd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btadd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });
        jToolBar1.add(btadd);

        btdelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/deletefile.png"))); // NOI18N
        btdelete.setFocusable(false);
        btdelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btdelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btdelete);

        btupdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        btupdate.setFocusable(false);
        btupdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btupdate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });
        jToolBar1.add(btupdate);

        btcompile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coffee.png"))); // NOI18N
        btcompile.setFocusable(false);
        btcompile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btcompile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btcompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcompileActionPerformed(evt);
            }
        });
        jToolBar1.add(btcompile);

        btinput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leftdoc.png"))); // NOI18N
        btinput.setFocusable(false);
        btinput.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btinput.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btinput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btinputActionPerformed(evt);
            }
        });
        jToolBar1.add(btinput);

        btoutput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rightdoc.png"))); // NOI18N
        btoutput.setFocusable(false);
        btoutput.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btoutput.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btoutput);

        bthide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/hide.png"))); // NOI18N
        bthide.setFocusable(false);
        bthide.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bthide.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bthide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthideActionPerformed(evt);
            }
        });
        jToolBar1.add(bthide);

        btunhide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unhide.png"))); // NOI18N
        btunhide.setFocusable(false);
        btunhide.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btunhide.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btunhide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btunhideActionPerformed(evt);
            }
        });
        jToolBar1.add(btunhide);

        btrun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightning.png"))); // NOI18N
        btrun.setFocusable(false);
        btrun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btrun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });
        jToolBar1.add(btrun);

        btshiftleft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/doubleleft.png"))); // NOI18N
        btshiftleft.setFocusable(false);
        btshiftleft.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btshiftleft.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btshiftleft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshiftleftActionPerformed(evt);
            }
        });
        jToolBar1.add(btshiftleft);

        btshiftright.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/doubleright.png"))); // NOI18N
        btshiftright.setFocusable(false);
        btshiftright.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btshiftright.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btshiftright.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshiftrightActionPerformed(evt);
            }
        });
        jToolBar1.add(btshiftright);

        jLabel1.setText("Path");

        ddinfiletype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FF", "X12", "DB" }));

        ddoutfiletype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FF", "X12", "DB" }));

        jLabel2.setText("IFS");

        jLabel3.setText("OFS");

        jLabel4.setText("In Doc Type");

        jLabel5.setText("Out Doc Type");

        jLabel7.setText("In File Type");

        jLabel8.setText("Out File Type");

        jLabel9.setText("Desc");

        jLabel10.setText("Version");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(tbpath)
                                        .addGap(96, 96, 96)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddoutdoctype, 0, 138, Short.MAX_VALUE)
                    .addComponent(ddofs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddindoctype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddifs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(ddinfiletype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddoutfiletype, 0, 93, Short.MAX_VALUE))
                    .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddifs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ddinfiletype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddofs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddoutfiletype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddindoctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddoutdoctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 277, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btrunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrunActionPerformed
        String[] c = EDI.initEDIControl();
        map_mstr x = getMapMstr(new String[]{ddmap.getSelectedItem().toString()});
        
        
        // now absorb file into doc structure for input into map
        // ...only processing the first doc in a envelope if file contains multiple...for testing purposes
         BufferedReader f = null;
         char[] cbuf = null;
         try {
         f = new BufferedReader(new FileReader(infile));
         cbuf = new char[(int) infile.length()];
         f.read(cbuf); 
         } catch (IOException ex) {
             tasource.setText(ex.toString());
             return;
         }
       
        // beginning of needs revamping 
        String[] editype = getEDIType(cbuf, infile.getName());
        ArrayList<String> doc = new ArrayList<String>();
        
        if (editype[0].equals("X12")) {
        Map<Integer, Object[]> ISAmap = createIMAP(cbuf, c, "", "", "", "");
        Map<Integer, ArrayList> d = null;
        char segdelim = 0;
        int loopcount = 0;
        for (Map.Entry<Integer, Object[]> isa : ISAmap.entrySet()) {
           loopcount++;
           if (loopcount > 1) {
               break;
           }
           d = (HashMap<Integer, ArrayList>) isa.getValue()[5];
           segdelim = (char) Integer.valueOf(isa.getValue()[2].toString()).intValue();
        }
        
        Integer[] k = null;
        for (Map.Entry<Integer, ArrayList> z : d.entrySet()) {
         for (Object r : z.getValue()) {
            Object[] b = (Object[]) r ;        
            k = (Integer[])b[0];
            //String doctype = (String)b[1];
            //String docid = (String)b[2];
         }
        }
       
        StringBuilder segment = new StringBuilder();
        for (int i = k[0]; i < k[1]; i++) {
            if (cbuf[i] == segdelim) {
                doc.add(segment.toString());
                segment.delete(0, segment.length());
            } else {
                if (! (String.format("%02x",(int) cbuf[i]).equals("0d") || String.format("%02x",(int) cbuf[i]).equals("0a")) ) {
                    segment.append(cbuf[i]);
                } 
            }
        }
        }
        
        char segdelim = (char) Integer.valueOf("10").intValue(); 
        if (editype[0].equals("FF")) {
           StringBuilder segment = new StringBuilder();
           for (int i = 0; i < cbuf.length; i++) {
                if (cbuf[i] == segdelim) {
                    doc.add(segment.toString());
                    segment.delete(0, segment.length());
                } else {
                    if (! (String.format("%02x",(int) cbuf[i]).equals("0d") || String.format("%02x",(int) cbuf[i]).equals("0a")) ) {
                        segment.append(cbuf[i]);
                    } 
                }
            }
        }
        
         if (editype[0].isEmpty()) {
            bsmf.MainFrame.show("unknown file type");
            return;
         }
      
   // end of needs revamping
   
        c[0] = "MapTester";
        c[21] = "MapTester";
        c[1] = x.map_indoctype();
       // c[9] = "10";
       // c[10] = "42";
       // c[11] = "0";
        c[15] = x.map_outdoctype();
        c[2] = x.map_id();
        c[28] = x.map_infiletype();
        c[29] = x.map_outfiletype();
        c[30] = "1";
        StringWriter sw = null;
        URLClassLoader cl = null;
        try {
                // hot reloadable class capability...new classloader created and closed in finally block
                List<File> jars = Arrays.asList(new File("edi/maps").listFiles());
                URL[] urls = new URL[jars.size()];
                for (int i = 0; i < jars.size(); i++) {
                try {
                    urls[i] = jars.get(i).toURI().toURL();
                } catch (Exception e) {
                    edilog(e);
                }
                }
                cl = new URLClassLoader(urls);
                Class<?> cls = Class.forName(x.map_id(),true,cl);
                Object obj = cls.newInstance();
                Method method = cls.getDeclaredMethod("Mapdata", ArrayList.class, String[].class);
                Object oc = method.invoke(obj, doc, c);
                String[] oString = (String[]) oc;
                tasource.setText(oString[0]);
                
                if (oString.length > 1) {
                    tasource.append("\n" + oString[1]);
                }
                
                
                } catch (InvocationTargetException ex) {
                  sw = new StringWriter();
                  ex.printStackTrace(new PrintWriter(sw));
                  tasource.setText(sw.toString());
                  edilog(ex);
                } catch (ClassNotFoundException ex) {
                  sw = new StringWriter();
                  ex.printStackTrace(new PrintWriter(sw));
                  tasource.setText(sw.toString());
                  edilog(ex);
                } catch (IllegalAccessException |
                         InstantiationException | NoSuchMethodException ex
                        ) {
                  sw = new StringWriter();  
                  ex.printStackTrace(new PrintWriter(sw));
                  tasource.setText(sw.toString());
                  edilog(ex);
                } finally {
                    
                    try {
                    if (cl != null) {    
                        cl.close();
                    }
                    } catch (IOException ex) {
                    edilog(ex);
                    }
                    
                    try {
                        if (sw != null) { 
                         sw.close();
                        }
                     outputpanel.setVisible(true);
                     bthide.setEnabled(true);   
                    } catch (IOException ex1) {
                        edilog(ex1);
                    }
                }
    }//GEN-LAST:event_btrunActionPerformed

    private void btinputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btinputActionPerformed
         infile = getfile();
        tasource.setText("");
        if (infile != null) {
            try {   
                List<String> lines = Files.readAllLines(infile.toPath());
                for (String segment : lines ) {
                        tasource.append(segment);
                        tasource.append("\n");
                }
                btrun.setEnabled(true);
            } catch (IOException ex) {
                bslog(ex);
            }   
        } else {
            btrun.setEnabled(false);
        }
    }//GEN-LAST:event_btinputActionPerformed

    private void bthideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthideActionPerformed
       outputpanel.setVisible(false);
       bthide.setEnabled(false);
    }//GEN-LAST:event_bthideActionPerformed

    private void btcompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcompileActionPerformed
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);
    out.println("public class HelloWorld {");
    out.println("  public static void main(String args[]) {");
    out.println("    System.out.println(\"This is in another java file\");");    
    out.println("  }");
    out.println("}");
    out.close();
    JavaFileObject file = new JavaSourceFromString("HelloWorld", writer.toString());

    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
    CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

    boolean success = task.call();
    
    // let's create the jar
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "HelloWorld");
            JarOutputStream target;
         try {
             target = new JarOutputStream(new FileOutputStream(new File("output.jar")), manifest);
        //     String path = Bootstrapper.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "HelloWorld.class";
          //  System.out.println(path);
            //Error with the path I guess.
         //   addToJar(file.toUri().getPath(), target);
            String fileclass = file.getName().split("\\.")[0] + ".class";
            addToJar(new File(fileclass), target);
            target.close();
         } catch (FileNotFoundException ex) {
             Logger.getLogger(MapTester.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(MapTester.class.getName()).log(Level.SEVERE, null, ex);
         }
            
    
    taoutput.setText("");
    for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
      taoutput.append(diagnostic.getCode() + "\n");
      taoutput.append(String.valueOf(diagnostic.getKind()) + "\n");
      taoutput.append(String.valueOf(diagnostic.getPosition()) + "\n");
      taoutput.append(String.valueOf(diagnostic.getStartPosition()) + "\n");
      taoutput.append(String.valueOf(diagnostic.getEndPosition()) + "\n");
      taoutput.append(String.valueOf(diagnostic.getSource()) + "\n");
      taoutput.append(diagnostic.getMessage(null) + "\n");
      
    }
    taoutput.append("Success: " + success + "\n");
    
/*
    if (success) {
      try {
        Class.forName("HelloWorld").getDeclaredMethod("main", new Class[] { String[].class })
            .invoke(null, new Object[] { null });
      } catch (ClassNotFoundException e) {
        taoutput.append("Class not found: " + e);
      } catch (NoSuchMethodException e) {
        taoutput.append("No such method: " + e);
      } catch (IllegalAccessException e) {
        taoutput.append("Illegal access: " + e);
      } catch (InvocationTargetException e) {
        taoutput.append("Invocation target: " + e);
      }
    }
    */
    
    }//GEN-LAST:event_btcompileActionPerformed

    private void btunhideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btunhideActionPerformed
       outputpanel.setVisible(true);
       bthide.setEnabled(true);
    }//GEN-LAST:event_btunhideActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        infile = getMapfile();
        tamap.setText("");
        if (infile != null) {
            try {   
                List<String> lines = Files.readAllLines(infile.toPath());
                for (String segment : lines ) {
                        tamap.append(segment);
                        tamap.append("\n");
                }
                btrun.setEnabled(true);
            } catch (IOException ex) {
                bslog(ex);
            }   
        } else {
            btrun.setEnabled(false);
        }
    }//GEN-LAST:event_btnewActionPerformed

    private void btshiftleftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshiftleftActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btshiftleftActionPerformed

    private void btshiftrightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshiftrightActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btshiftrightActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        newAction("");
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btupdateActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btcompile;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton bthide;
    private javax.swing.JButton btinput;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btoutput;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btshiftleft;
    private javax.swing.JButton btshiftright;
    private javax.swing.JButton btunhide;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox<String> ddifs;
    private javax.swing.JComboBox<String> ddindoctype;
    private javax.swing.JComboBox<String> ddinfiletype;
    private javax.swing.JComboBox<String> ddofs;
    private javax.swing.JComboBox<String> ddoutdoctype;
    private javax.swing.JComboBox<String> ddoutfiletype;
    private javax.swing.JFileChooser fc;
    private javax.swing.JPanel inputpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel outputpanel;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTextArea tamap;
    private javax.swing.JTextArea taoutput;
    private javax.swing.JTextArea tasource;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbpath;
    private javax.swing.JTextField tbversion;
    // End of variables declaration//GEN-END:variables
}
