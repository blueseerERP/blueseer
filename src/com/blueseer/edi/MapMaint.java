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
import static com.blueseer.edi.EDIMap.SegmentCounter;
import com.blueseer.edi.EDIMap.UserDefinedException;
import static com.blueseer.edi.EDIMap.splitFFSegment;
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
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.Element;
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
public class MapMaint extends javax.swing.JPanel implements IBlueSeerT  {
 
     public Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
     File infile = null;
     // global variable declarations
                boolean isLoad = false;
                public static map_mstr x = null;
                JPopupMenu mymenu = new JPopupMenu();
                JMenuItem menuraw = new JMenuItem("Raw Format");
                JMenuItem menulabeled = new JMenuItem("Labeled Format");
                JMenuItem menuhide = new JMenuItem("Hide Panel");
                
                boolean tamapLineToggle = false;
                boolean tainputLineToggle = false;
                boolean taoutputLineToggle = false;
                
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
    
    public class myPopupHandler implements ActionListener,
                              PopupMenuListener {
        
        JTextArea ta; 
        JPopupMenu popup;
       
        
        public myPopupHandler(JTextArea ta) {
        this.ta = ta;
        this.ta.setName(ta.getName());
        popup = new JPopupMenu();
        popup.setInvoker(ta);
       
        if (ta.getName().equals("tamap")) {
            popup.add(setMenuItem("Toggle Lines"));
            popup.add(setMenuItem("Hide Panel"));
        }
        if (ta.getName().equals("tainput")) {
            popup.add(setMenuItem("Toggle Lines"));
            popup.add(setMenuItem("Input"));
            popup.add(setMenuItem("Structure"));
            popup.add(setMenuItem("Overlay"));
            popup.add(setMenuItem("Hide Panel"));
        }
        if (ta.getName().equals("taoutput")) {
            popup.add(setMenuItem("Toggle Lines"));
            popup.add(setMenuItem("Structure"));
            popup.add(setMenuItem("Overlay"));
            popup.add(setMenuItem("Hide Panel"));
        }
        
        ta.addMouseListener(ma);
        popup.addPopupMenuListener(this);
    }
 
        public JPopupMenu getPopup() {
        return popup;
        }
        
        private JMenuItem setMenuItem(String s) {
        JMenuItem menuItem = new JMenuItem(s);
        menuItem.setActionCommand(s);
        menuItem.setName(this.ta.getName());
        menuItem.addActionListener(this);
        return menuItem;
    }
        
        
        private MouseListener ma = new MouseAdapter() {
            private void checkForPopup(MouseEvent e) {
               if (SwingUtilities.isRightMouseButton(e)) {
                  if (! ta.isEnabled()) {
                      return;
                  } 
                popup.show(ta, e.getX(), e.getY());
                } 
            }
            public void mousePressed(MouseEvent e)  { checkForPopup(e); }
            
        };

        @Override
        public void actionPerformed(ActionEvent e) { 
             String ac = e.getActionCommand();
             JMenuItem parentname = (JMenuItem) e.getSource();
             switch (ac) {
                case "Hide Panel" :
                    hidePanel(parentname.getName());
                    break;
                    
                case "Toggle Lines" :
                    toggleLines(parentname.getName());
                    break;
                    
                case "Structure" :
                    showStructure(parentname.getName());
                    break;   
                    
                case "Overlay" :
                    showOverlay(parentname.getName());
                    break;        
                    
                default:
                    System.out.println("unknown action: " + ac);
                    
             }
             
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
          //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
           // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
           // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public MapMaint() {
        initComponents();
        setLanguageTags(this);
        
        MapMaint.myPopupHandler handler = new MapMaint.myPopupHandler(tamap);
        tamap.add(handler.getPopup());        
        
        MapMaint.myPopupHandler handler2 = new MapMaint.myPopupHandler(tainput);
        tainput.add(handler2.getPopup());
        
        MapMaint.myPopupHandler handler3 = new MapMaint.myPopupHandler(taoutput);
        taoutput.add(handler3.getPopup());
        
        
        /*
        mymenu.add(menuraw);
        mymenu.add(menulabeled);
        mymenu.add(menuhide);
        */
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
           }  else if (this.type.equals("update")) {
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
        tbpath.setText("");
        tamap.setText("");
        cbinternal.setEnabled(false);
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
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        cbinternal.setEnabled(false);
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
                   cbinternal.setEnabled(false);
        } else {
                   tbkey.setForeground(Color.red); 
                   cbinternal.setEnabled(false);
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
       /*
         if (! BlueSeerUtils.isEDIClassFile(tbkey.getText())) {
                    bsmf.MainFrame.show(getMessageTag(1145,tbkey.getText()));
                    tbkey.requestFocus();
                    return false;
        }
        */
      return true;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
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
                ddoutfiletype.getSelectedItem().toString(),
                tbpath.getText(),
                tbpackage.getText(), // package
                String.valueOf(BlueSeerUtils.boolToInt(cbinternal.isSelected()))
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
         saveFile();  // save source map file
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
        tbpath.setText(x.map_source());
        tbpackage.setText(x.map_package());
        ddofs.setSelectedItem(x.map_ofs());
        ddifs.setSelectedItem(x.map_ifs());
        ddindoctype.setSelectedItem(x.map_indoctype());
        ddinfiletype.setSelectedItem(x.map_infiletype());
        ddoutdoctype.setSelectedItem(x.map_outdoctype());
        ddoutfiletype.setSelectedItem(x.map_outfiletype());
        cbinternal.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.map_internal())));
        Path path = FileSystems.getDefault().getPath(tbpath.getText());
        tamap.setText("");
        if (path.toFile().exists()) {
            try {   
                List<String> lines = Files.readAllLines(path);
                for (String segment : lines ) {
                        tamap.append(segment);
                        tamap.append("\n");
                }
            } catch (IOException ex) {
                bslog(ex);
            }   
        }
        
        setAction(x.m()); 
    }
    
    
    public ArrayList<String> extractImports() {
        ArrayList<String> s = new ArrayList<String>();
        String[] x = tamap.getText().split("\n");
        for (String e : x) {
            if (e.matches("\\s*import\\s.*;.*")) {
                s.add(e);
            }
        }
      //  bsmf.MainFrame.show(String.valueOf(s.size()));
        return s;
    }
    
    public void toggleLines(String taname) {
        
        int i = 0;
        String[] x;
        
        if (taname.equals("tamap")) {
            tamapLineToggle = ! tamapLineToggle;
        }
        if (taname.equals("tainput")) {
            tainputLineToggle = ! tainputLineToggle;
        }
        if (taname.equals("taoutput")) {
            taoutputLineToggle = ! taoutputLineToggle;
        }
        
        
        JTextArea lines = new JTextArea();
        lines.setBackground(Color.LIGHT_GRAY);
        lines.setEditable(false);
        
        if (tamapLineToggle) {
            jScrollPane4.setRowHeaderView(lines);
            lines.setVisible(true);
        } else {
            jScrollPane4.setRowHeaderView(null);
            lines.setVisible(false);
            return;
        }
         tamap.getDocument().addDocumentListener(new DocumentListener() {
         public String getText() {
            int caretPosition = tamap.getDocument().getLength();
            Element root = tamap.getDocument().getDefaultRootElement();
            String text = "1" + System.getProperty("line.separator");
               for(int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                  text += i + System.getProperty("line.separator");
               }
            return text;
         }
         @Override
         public void changedUpdate(DocumentEvent de) {
            lines.setText(getText());
         }
         @Override
         public void insertUpdate(DocumentEvent de) {
            lines.setText(getText());
         }
         @Override
         public void removeUpdate(DocumentEvent de) {
            lines.setText(getText());
         }
      });
        
        if (taname.equals("tamap")) {
          x = tamap.getText().split("\n");
          tamap.setText("");
            for (String e : x) {
                i++;
                lines.append(String.valueOf(i) + "\n");
                tamap.append(e + "\n");
            }  
        }
        if (taname.equals("tainput")) {
          x = tainput.getText().split("\n");
          tainput.setText("");
            for (String e : x) {
                i++;
                tainput.append(String.valueOf(i) + "  " + e + "\n");
            }  
        }
        if (taname.equals("taoutput")) {
          x = taoutput.getText().split("\n");
          taoutput.setText("");
            for (String e : x) {
                i++;
                taoutput.append(String.valueOf(i) + "  " + e + "\n");
            }  
        }
    }
    
    public List<String> getStructure(String structureName) {
        List<String> lines = new ArrayList<>();
        String dirpath;
        if (structureName.equals("ifs")) {
            dirpath = "edi/structure/" + ddifs.getSelectedItem().toString();
        } else {
            dirpath = "edi/structure/" + ddofs.getSelectedItem().toString();
        }
        Path path = FileSystems.getDefault().getPath(dirpath);
        File file = path.toFile();
        if (file != null && file.exists()) {
                try {   
                    lines = Files.readAllLines(file.toPath());
                } catch (IOException ex) {
                    bslog(ex);
                }   
            }
        return lines;
    }
    
    public ArrayList<String[]> getStructureSplit(List<String> lines) {
        ArrayList<String[]> linessplit = new ArrayList<>();
        for (String s : lines) {
          if (s.startsWith("#")) {
              continue;
          }
          linessplit.add(s.split(","));
        }
        return linessplit;
    }
    
    public Map<String, HashMap<Integer,String[]>> getStructureAsHashMap(List<String> lines) {
        Map<String, HashMap<Integer,String[]>> msf = new HashMap<String, HashMap<Integer,String[]>>();
        ArrayList<String[]> linessplit = new ArrayList<>();
        LinkedHashMap<Integer, String[]> z = new LinkedHashMap<Integer, String[]>();
        int i = 0;
        String lastkey = "";
        for (String s : lines) {
            if (s.startsWith("#")) {
              continue;
            }
            String[] t = s.split(",",-1);
             if (i == 0) { lastkey = t[0];}

            i++;
            if (! t[0].equals(lastkey)) {
                LinkedHashMap<Integer, String[]> w = z;
                msf.put(t[0], w);
                z = new LinkedHashMap<Integer, String[]>();
                i = 0;
                z.put(i, t);

            } else {
                z.put(i, t);
                msf.put(t[0], z);
            }
            lastkey = t[0];
        }
        return msf;
    }
       
    public void showStructure(String taname) {
        if (taname.equals("tainput")) {
            List<String> lines = getStructure("ifs");
            tainput.setText("");
            for (String segment : lines ) {
                            tainput.append(segment);
                            tainput.append("\n");
            }
        }
        if (taname.equals("taoutput")) {
            List<String> lines = getStructure("ofs");
            taoutput.setText("");
            for (String segment : lines ) {
                            taoutput.append(segment);
                            taoutput.append("\n");
            }
        }
    }
    
    public static LinkedHashMap<String, String[]> mapInput(String filetype, ArrayList<String> data, ArrayList<String[]> ISF) {
        LinkedHashMap<String,String[]> mappedData = new LinkedHashMap<String,String[]>();
        HashMap<String,Integer> groupcount = new HashMap<String,Integer>();
        HashMap<String,Integer> set = new HashMap<String,Integer>();
        String parenthead = "";
        String groupkey = "";
        String previouskey = "";
        for (String s : data) {
                String[] x = null;
                if (filetype.equals("FF")) {
                    x = splitFFSegment(s, ISF);
                } else {
                    x = s.split("\\*",-1); // if x12
                }

                for (String[] z : ISF) {
                    // skip non-landmarks
                    if (! z[4].equals("yes")) {
                        continue;
                    }
                        if (x != null && (x.length > 1) && x[0].equals(z[0])) {
                                boolean foundit = false;
                                boolean hasloop = false;
                                String[] temp = parenthead.split(":");
                                for (int i = temp.length - 1; i >= 0; i--) {
                                        if (z[1].compareTo(temp[i]) == 0) {
                                                foundit = true;
                                                String[] newarray = Arrays.copyOfRange(temp, 0, i + 1);
                                                parenthead = String.join(":", newarray);							
                                                break;
                                        }
                                }
                                if (! foundit) {
                                continue;	
                                } else {
                                        int loop = 1;
                                        String groupparent = parenthead + ":" + x[0];
                                        if (groupcount.containsKey(groupparent)) {
                                                int g = groupcount.get(groupparent);

                                                if (previouskey.equals(parenthead + ":" + x[0] + "+" + g) && ! z[3].equals("yes")) {
                                                        loop = set.get(parenthead + ":" + x[0] + "+" + groupcount.get(groupparent));	
                                                        hasloop = true;
                                                        loop++;
                                                        set.put(parenthead + ":" + x[0] + "+" + groupcount.get(groupparent), loop);
                                                } else {
                                                        g++;	
                                                        groupcount.put(groupparent, g);
                                                }
                                        } else {
                                               // groupcount.put(groupparent, 1);
                                               if (groupcount.get(parenthead) != null) {
                                                  groupcount.put(groupparent, groupcount.get(parenthead)); 
                                               } else {
                                                  groupcount.put(groupparent, 1); 
                                               }
                                               
                                        }

                                        previouskey = parenthead + ":" + x[0] + "+" + groupcount.get(groupparent);	
                                        if (hasloop) {
                                            groupkey = parenthead + ":" + x[0] + "+" + groupcount.get(groupparent) + "+" + loop;
                                        } else {
                                            groupkey = parenthead + ":" + x[0] + "+" + groupcount.get(groupparent);
                                        }

                                        set.put(groupkey, loop);
                                        mappedData.put(parenthead + ":" + x[0] + "+" + groupcount.get(groupparent) + "+" + loop , x);
                                        SegmentCounter.add(parenthead + ":" + x[0] + "+" + groupcount.get(groupparent));
                                        if (z[3].equals("yes")) {
                                                parenthead = parenthead + (":" + z[0]);
                                        }
                                        break;
                                }
                        }
                }
        }
        return mappedData;
    }

    public void showOverlay(String taname) {
        if (taname.equals("tainput")) {
            File file = getfile();
            List<String> input = getfiledata(file.toPath());
            List<String> structure = getStructure("ifs"); 
            Map<String, HashMap<Integer,String[]>> msf = getStructureAsHashMap(structure); 
            LinkedHashMap<String, String[]> mappedData = mapInput(ddinfiletype.getSelectedItem().toString(), (ArrayList<String>) input, getStructureSplit(structure));
            tainput.setText("");
            
            for (Map.Entry<String, String[]> z : mappedData.entrySet()) {
                    String value = String.join(",", z.getValue());
                    String[] keyx = z.getKey().split("\\+", -1);
                    String key = "";
                    if (keyx[0].contains(":")) {
                        key = keyx[0].substring(1);
                    } else {
                        key = keyx[0];
                    }
                    
                    int i = 0;
                    String fieldname = "";
                    for (String s : z.getValue()) {
                     if (msf.get(key) != null && msf.get(key).get(i) != null) {
                         String[] j = msf.get(key).get(i);
                         if (j != null && j.length > 4) {
                             fieldname = j[5];
                         } else {
                             fieldname = "unknown";
                         }
                     }
                     tainput.append(z.getKey() + " " + fieldname +  " / Field: " + i + " value: " + s + "\n");   
                     i++;
                    }
            }
        }
        
        bsmf.MainFrame.show("yep");
    }
    
    public ArrayList<String> cleanText() {
        ArrayList<String> s = new ArrayList<String>();
        String[] x = tamap.getText().split("\n");
        for (String e : x) {
            if (! e.matches("\\s*import\\s.*;.*")) {
                s.add(e);
            }
        }
        return s;
    }
    
    public void hidePanel(String panel) {
        if (panel.equals("tainput")) {
            inputpanel.setVisible(false);
        }
        if (panel.equals("taoutput")) {
            outputpanel.setVisible(false);
        }
        if (panel.equals("tamap")) {
            mappanel.setVisible(false);
        }
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
    
    public void saveFile() {
        Path path = FileSystems.getDefault().getPath(tbpath.getText());
         try {
             BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile(), false)));
             output.write(tamap.getText());
             output.close();  
         } catch (FileNotFoundException ex) {
             bslog(ex);
         } catch (IOException ex) {
             bslog(ex);
         }
    }
    
    public void jarFile(JavaFileObject file) {
        // let's create the jar
            String filename = file.getName().split("\\.")[0].replace("\\", "").replace("/", "");  // strip leading dir backslash
            String fileclass = filename + ".class";
            String jarname = filename + ".jar";
          //  bsmf.MainFrame.show(filename + "/" + jarname);
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, filename);
            JarOutputStream target;
            Path path = FileSystems.getDefault().getPath("edi/maps/" + jarname);
            
         try {
             target = new JarOutputStream(new FileOutputStream(path.toFile()), manifest);
        //     String path = Bootstrapper.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "HelloWorld.class";
          //  System.out.println(path);
            //Error with the path I guess.
         //   addToJar(file.toUri().getPath(), target);
            
            addToJar(new File(fileclass), target);
            target.close();
         } catch (FileNotFoundException ex) {
             bslog(ex);
         } catch (IOException ex) {
             bslog(ex);
         }
    }
    
    public JavaFileObject compileFile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        // get custom imports from text
        ArrayList<String> imports = extractImports();
        ArrayList<String> text = cleanText();
        
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("import java.util.ArrayList;");
        out.println("import java.io.IOException;");
        out.println("import static com.blueseer.utl.BlueSeerUtils.*;");
        out.println("import static com.blueseer.edi.EDI.*;");
        
        for (String s : imports) {
          out.println(s);  
        }
        
        out.println("public class " + tbkey.getText() + " extends com.blueseer.edi.EDIMap " +  " {");
        out.println("  public String[] Mapdata(ArrayList doc, String[] c) throws IOException, UserDefinedException  {");
        out.println("setControl(c);  ");
        out.println("if (isError) { return error;} ");
        out.println("mappedInput = mapInput(c, doc, ISF);");
        for (String s : text) {
          out.println(s);  
        }
        out.println("return packagePayLoad(c);  }");
        out.println("}");
        out.close();
        JavaFileObject file = new JavaSourceFromString(tbkey.getText(), writer.toString());

        
        
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

        boolean success = task.call();

        if (! success) {
            file = null;
        }
        
        taoutput.setText("");
        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
          if (String.valueOf(diagnostic.getKind()).equals("NOTE")) {
              continue;
          }
          taoutput.append(diagnostic.getCode() + "\n");
          taoutput.append(String.valueOf(diagnostic.getKind()) + "\n");
          taoutput.append(String.valueOf(diagnostic.getLineNumber()) + "\n");
          taoutput.append(String.valueOf(diagnostic.getPosition()) + "\n");
          taoutput.append(String.valueOf(diagnostic.getStartPosition()) + "\n");
          taoutput.append(String.valueOf(diagnostic.getEndPosition()) + "\n");
          taoutput.append(String.valueOf(diagnostic.getSource()) + "\n");
          taoutput.append(diagnostic.getMessage(null) + "\n");
          taoutput.append("\n");
        }
        taoutput.append("Success: " + success + "\n");

        return file;
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
    
    public List<String> getfiledata(Path path) {
      List<String> lines = new ArrayList<>();
        File file = path.toFile();
        if (file != null && file.exists()) {
                try {   
                    lines = Files.readAllLines(file.toPath());
                } catch (IOException ex) {
                    bslog(ex);
                }   
            }
        return lines;  
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
          //  bsmf.MainFrame.show(source.getName() + "<->" + source.getPath());
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
        jScrollPane7 = new javax.swing.JScrollPane();
        tainput = new javax.swing.JTextArea();
        mappanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tamap = new javax.swing.JTextArea();
        outputpanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        taoutput = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        toolbar = new javax.swing.JToolBar();
        btnew = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
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
        btfind = new javax.swing.JButton();
        tbpackage = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cbinternal = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Mapper"));
        jPanel1.setName("panelmain"); // NOI18N

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        inputpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Input"));
        inputpanel.setPreferredSize(new java.awt.Dimension(260, 764));
        inputpanel.setLayout(new javax.swing.BoxLayout(inputpanel, javax.swing.BoxLayout.LINE_AXIS));

        tainput.setColumns(20);
        tainput.setRows(5);
        tainput.setName("tainput"); // NOI18N
        jScrollPane7.setViewportView(tainput);

        inputpanel.add(jScrollPane7);

        tablepanel.add(inputpanel);

        mappanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Map"));
        mappanel.setPreferredSize(new java.awt.Dimension(525, 76));
        mappanel.setLayout(new javax.swing.BoxLayout(mappanel, javax.swing.BoxLayout.LINE_AXIS));

        tamap.setColumns(20);
        tamap.setRows(5);
        tamap.setName("tamap"); // NOI18N
        jScrollPane4.setViewportView(tamap);

        mappanel.add(jScrollPane4);

        tablepanel.add(mappanel);

        outputpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));
        outputpanel.setPreferredSize(new java.awt.Dimension(260, 764));
        outputpanel.setLayout(new javax.swing.BoxLayout(outputpanel, javax.swing.BoxLayout.LINE_AXIS));

        taoutput.setColumns(20);
        taoutput.setRows(5);
        taoutput.setName("taoutput"); // NOI18N
        jScrollPane5.setViewportView(taoutput);

        outputpanel.add(jScrollPane5);

        tablepanel.add(outputpanel);

        jLabel6.setText("Map");
        jLabel6.setName("lblfromreceiver"); // NOI18N

        toolbar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        btnew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newfile.png"))); // NOI18N
        btnew.setToolTipText("new");
        btnew.setFocusable(false);
        btnew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });
        toolbar.add(btnew);

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
        toolbar.add(btlookup);

        btclear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/file.png"))); // NOI18N
        btclear.setFocusable(false);
        btclear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btclear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });
        toolbar.add(btclear);

        btadd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addfile.png"))); // NOI18N
        btadd.setFocusable(false);
        btadd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btadd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });
        toolbar.add(btadd);

        btdelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/deletefile.png"))); // NOI18N
        btdelete.setFocusable(false);
        btdelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btdelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });
        toolbar.add(btdelete);

        btupdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        btupdate.setFocusable(false);
        btupdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btupdate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });
        toolbar.add(btupdate);

        btcompile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coffee.png"))); // NOI18N
        btcompile.setFocusable(false);
        btcompile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btcompile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btcompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcompileActionPerformed(evt);
            }
        });
        toolbar.add(btcompile);

        btinput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leftdoc.png"))); // NOI18N
        btinput.setFocusable(false);
        btinput.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btinput.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btinput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btinputActionPerformed(evt);
            }
        });
        toolbar.add(btinput);

        btoutput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rightdoc.png"))); // NOI18N
        btoutput.setFocusable(false);
        btoutput.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btoutput.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btoutput);

        bthide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/hide.png"))); // NOI18N
        bthide.setFocusable(false);
        bthide.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bthide.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bthide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthideActionPerformed(evt);
            }
        });
        toolbar.add(bthide);

        btunhide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unhide.png"))); // NOI18N
        btunhide.setFocusable(false);
        btunhide.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btunhide.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btunhide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btunhideActionPerformed(evt);
            }
        });
        toolbar.add(btunhide);

        btrun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightning.png"))); // NOI18N
        btrun.setFocusable(false);
        btrun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btrun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });
        toolbar.add(btrun);

        btshiftleft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/doubleleft.png"))); // NOI18N
        btshiftleft.setFocusable(false);
        btshiftleft.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btshiftleft.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btshiftleft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshiftleftActionPerformed(evt);
            }
        });
        toolbar.add(btshiftleft);

        btshiftright.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/doubleright.png"))); // NOI18N
        btshiftright.setFocusable(false);
        btshiftright.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btshiftright.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btshiftright.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshiftrightActionPerformed(evt);
            }
        });
        toolbar.add(btshiftright);

        jLabel1.setText("Source");

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

        btfind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btfind.setFocusable(false);
        btfind.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btfind.setName("btlookup"); // NOI18N
        btfind.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btfind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btfindActionPerformed(evt);
            }
        });

        jLabel11.setText("Package");

        cbinternal.setText("Internal");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel9)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbpath)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btfind, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbinternal)
                                .addGap(10, 10, 10)))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddifs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddofs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddindoctype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddoutdoctype, 0, 162, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(ddinfiletype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddoutfiletype, 0, 93, Short.MAX_VALUE))
                    .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpackage, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(jLabel8)
                            .addComponent(cbinternal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1))
                            .addComponent(btfind, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpackage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(ddindoctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddoutdoctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        map_mstr m = getMapMstr(new String[]{tbkey.getText()});
        
        
        // now absorb file into doc structure for input into map
        // ...only processing the first doc in a envelope if file contains multiple...for testing purposes
         
        // BufferedReader f = null;
         char[] cbuf  = tainput.getText().toCharArray();
         /*
         try {
         f = new BufferedReader(new FileReader(infile));
         cbuf = new char[(int) infile.length()];
         f.read(cbuf); 
         } catch (IOException ex) {
             tasource.setText(ex.toString());
             return;
         }
       
         */
        // beginning of needs revamping 
        String[] editype = getEDIType(cbuf, "testdata");
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
                taoutput.setText(oString[0]);
                
                if (oString.length > 1) {
                    taoutput.append("\n" + oString[1]);
                }
                
                
                } catch (InvocationTargetException ex) {
                  sw = new StringWriter();
                  ex.printStackTrace(new PrintWriter(sw));
                  taoutput.setText(sw.toString());
                  edilog(ex);
                } catch (ClassNotFoundException ex) {
                  sw = new StringWriter();
                  ex.printStackTrace(new PrintWriter(sw));
                  taoutput.setText(sw.toString());
                  edilog(ex);
                } catch (IllegalAccessException |
                         InstantiationException | NoSuchMethodException ex
                        ) {
                  sw = new StringWriter();  
                  ex.printStackTrace(new PrintWriter(sw));
                  taoutput.setText(sw.toString());
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
        tainput.setText("");
        if (infile != null) {
            try {   
                List<String> lines = Files.readAllLines(infile.toPath());
                for (String segment : lines ) {
                        tainput.append(segment);
                        tainput.append("\n");
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
    taoutput.setText("");
    JavaFileObject file = compileFile();
    if (file != null) {
        jarFile(file);
    }
    
    
    
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
       inputpanel.setVisible(true);
       mappanel.setVisible(true);
       bthide.setEnabled(true);
    }//GEN-LAST:event_btunhideActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
       
    }//GEN-LAST:event_btnewActionPerformed

    private void btshiftleftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshiftleftActionPerformed
        outputpanel.setVisible(true);
        inputpanel.setVisible(false);
    }//GEN-LAST:event_btshiftleftActionPerformed

    private void btshiftrightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshiftrightActionPerformed
        outputpanel.setVisible(false);
        inputpanel.setVisible(true);
    }//GEN-LAST:event_btshiftrightActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(BlueSeerUtils.dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
         if (! validateInput(BlueSeerUtils.dbaction.update)) {
           return;
       }
        // setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btfindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btfindActionPerformed
        
        infile = getfile();
        
      //  Path path = FileSystems.getDefault().getPath(infile.getAbsolutePath());
        if (infile != null && infile.exists()) {
            tbpath.setText(infile.getAbsolutePath());
            tamap.setText("");
            try {   
                List<String> lines = Files.readAllLines(infile.toPath());
                for (String segment : lines ) {
                        tamap.append(segment);
                        tamap.append("\n");
                }
                btrun.setEnabled(true);
                btcompile.setEnabled(true);
            } catch (IOException ex) {
                bslog(ex);
            }   
        } else {
            btrun.setEnabled(false);
            btcompile.setEnabled(false);
        }
        
    }//GEN-LAST:event_btfindActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcompile;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btfind;
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
    private javax.swing.JCheckBox cbinternal;
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
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JPanel mappanel;
    private javax.swing.JPanel outputpanel;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTextArea tainput;
    private javax.swing.JTextArea tamap;
    private javax.swing.JTextArea taoutput;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbpackage;
    private javax.swing.JTextField tbpath;
    private javax.swing.JTextField tbversion;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
