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
import static bsmf.MainFrame.tags;
import static com.blueseer.adm.SystemControl.newFile;
import com.blueseer.edi.EDI.AnnoDoc;
import static com.blueseer.edi.EDI.createIMAP;
import static com.blueseer.edi.EDI.createMAPUNE;
import static com.blueseer.edi.EDI.edilog;
import static com.blueseer.edi.EDI.escapeDelimiter;
import static com.blueseer.edi.EDI.getEDIType;
import static com.blueseer.edi.ediData.addDFStructureTransaction;
import static com.blueseer.edi.ediData.addMapMstr;
import static com.blueseer.edi.ediData.deleteDFStructure;
import static com.blueseer.edi.ediData.deleteMapMstr;
import com.blueseer.edi.ediData.dfs_det;
import com.blueseer.edi.ediData.dfs_mstr;
import static com.blueseer.edi.ediData.getDFSMstr;
import static com.blueseer.edi.ediData.getDSFMstrasArray;
import static com.blueseer.edi.ediData.getDSFasArray;
import static com.blueseer.edi.ediData.getDSFasString;
import static com.blueseer.edi.ediData.getMapMstr;
import static com.blueseer.edi.ediData.isValidDFSid;
import static com.blueseer.edi.ediData.isValidMapid;

import com.blueseer.edi.ediData.map_mstr;
import static com.blueseer.edi.ediData.updateMapMstr;
import static com.blueseer.utl.BlueSeerUtils.ConvertTrueFalseToBoolean;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
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
import static com.blueseer.utl.EDData.cbufToList;
import static com.blueseer.utl.EDData.getBSDocTypeFromStds;
import static com.blueseer.utl.EDData.getDelimiters;
import static com.blueseer.utl.EDData.readEDIRawFileIntoCbuf;
import com.blueseer.utl.IBlueSeerT;
import static com.blueseer.utl.OVData.getSystemTempDirectory;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
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
                
                public final int MARGIN_WIDTH_PX = 28;
                
                private JScrollBar verticalScrollBar;
                
                private UndoManager undoManagerTAINPUT = new UndoManager();
                private UndoManager undoManagerTAINSTRUCT = new UndoManager();
                private UndoManager undoManagerTAOUTSTRUCT = new UndoManager();
                private UndoManager undoManagerTAOUTPUT = new UndoManager();
                private UndoManager undoManagerTAMAP = new UndoManager();
                
                Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.YELLOW);
                
                boolean tamapLineToggle = false;
                boolean tainputLineToggle = false;
                boolean tainstructLineToggle = false;
                boolean taoutputLineToggle = false;
                boolean taoutstructLineToggle = false;
                
                JTextArea lines = new JTextArea();
                
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
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Clear Highlights"));
            popup.add(setMenuItem("List Methods"));
            popup.add(setMenuItem("Hide Panel"));
        }
        if (ta.getName().equals("tainput")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Hex Replace"));
            popup.add(setMenuItem("Input"));
            popup.add(setMenuItem("Identify"));
            popup.add(setMenuItem("Download"));
            popup.add(setMenuItem("Clear Highlights"));
            popup.add(setMenuItem("Hide Panel"));
        }
        if (ta.getName().equals("tainstruct")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Structure"));
            popup.add(setMenuItem("Overlay"));
            popup.add(setMenuItem("Clear Highlights"));
        }
        if (ta.getName().equals("taoutput")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Hex Replace"));
            popup.add(setMenuItem("Download"));
            popup.add(setMenuItem("Clear Highlights"));
            popup.add(setMenuItem("Hide Panel"));
        }
        if (ta.getName().equals("taoutstruct")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Structure"));
            popup.add(setMenuItem("Overlay"));
            popup.add(setMenuItem("Clear Highlights"));
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
                
                case "Structure" :
                    showStructure(parentname.getName());
                    break; 
                
                case "Input" :
                    getInput();
                    break;
                    
                case "Overlay" :
                    showOverlay(parentname.getName());
                    break;  
                    
                case "Identify" :
                    identify();
                    break;    
                
                case "Hex Replace" :
                    hexReplace(parentname.getName());
                    break; 
                    
                case "Download" :
                    download(parentname.getName());
                    break;    
                    
                case "Search" :
                    searchTextArea(parentname.getName());
                    break;  
                    
                case "Clear Highlights":
                    cleanHighlights(parentname.getName());
                    break;
                
                case "Clear":
                    clear(parentname.getName());
                    break;    
                    
                case "List Methods":
                     listInternalMethods();
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
    
    class LineNumbersView extends JComponent implements DocumentListener, CaretListener, ComponentListener {

        // this class was borrowed from :  rememberjava.com/ui/2017/02/19/line_numbers.html  Thank you!!
	    private JTextComponent editor;

	    private Font font;

	    public LineNumbersView(JTextComponent editor) {
	      this.editor = editor;

	      editor.getDocument().addDocumentListener(this);
	      editor.addComponentListener(this);
	      editor.addCaretListener(this);
	    }

	    @Override
	    public void paintComponent(Graphics g) {
	      super.paintComponent(g);

	      Rectangle clip = g.getClipBounds();
	      int startOffset = editor.viewToModel(new Point(0, clip.y));
	      int endOffset = editor.viewToModel(new Point(0, clip.y + clip.height));

                            
	      while (startOffset <= endOffset && endOffset != 0 ) {
	        try {
	          String lineNumber = getLineNumber(startOffset);
	          if (lineNumber != null) {
	            int x = getInsets().left + 2;
	            int y = getOffsetY(startOffset);

	            font = font != null ? font : new Font(Font.MONOSPACED, Font.BOLD, editor.getFont().getSize());
	            g.setFont(font);

	            g.setColor(isCurrentLine(startOffset) ? Color.RED : Color.BLACK);

	            g.drawString(lineNumber, x, y);
	          }

	          startOffset = Utilities.getRowEnd(editor, startOffset) + 1;
	        } catch (BadLocationException e) {
	          e.printStackTrace();
	          // ignore and continue
	        }
	      }
	    }

	    /**
	     * Returns the line number of the element based on the given (start) offset
	     * in the editor model. Returns null if no line number should or could be
	     * provided (e.g. for wrapped lines).
	     */
	    private String getLineNumber(int offset) {
	      Element root = editor.getDocument().getDefaultRootElement();
	      int index = root.getElementIndex(offset);
	      Element line = root.getElement(index);

	      return line.getStartOffset() == offset ? String.format("%3d", index + 1) : null;
	    }

	    /**
	     * Returns the y axis position for the line number belonging to the element
	     * at the given (start) offset in the model.
	     */
	    private int getOffsetY(int offset) throws BadLocationException {
	      FontMetrics fontMetrics = editor.getFontMetrics(editor.getFont());
	      int descent = fontMetrics.getDescent();

	      Rectangle r = editor.modelToView(offset);
	      int y = r.y + r.height - descent;

	      return y;
	    }

	    /**
	     * Returns true if the given start offset in the model is the selected (by
	     * cursor position) element.
	     */
	    private boolean isCurrentLine(int offset) {
	      int caretPosition = editor.getCaretPosition();
	      Element root = editor.getDocument().getDefaultRootElement();
	      return root.getElementIndex(offset) == root.getElementIndex(caretPosition);
	    }

	    /**
	     * Schedules a refresh of the line number margin on a separate thread.
	     */
	    private void documentChanged() {
	      SwingUtilities.invokeLater(() -> {
	        repaint();
	      });
	    }

	    /**
	     * Updates the size of the line number margin based on the editor height.
	     */
	    private void updateSize() {
	      Dimension size = new Dimension(MARGIN_WIDTH_PX, editor.getHeight());
	      setPreferredSize(size);
	      setSize(size);
	    }

	    @Override
	    public void insertUpdate(DocumentEvent e) {
	      documentChanged();
	    }

	    @Override
	    public void removeUpdate(DocumentEvent e) {
	      documentChanged();
	    }

	    @Override
	    public void changedUpdate(DocumentEvent e) {
	      documentChanged();
	    }

	    @Override
	    public void caretUpdate(CaretEvent e) {
	      documentChanged();
	    }

	    @Override
	    public void componentResized(ComponentEvent e) {
	      updateSize();
	      documentChanged();
	    }

	    @Override
	    public void componentMoved(ComponentEvent e) {
	    }

	    @Override
	    public void componentShown(ComponentEvent e) {
	      updateSize();
	      documentChanged();
	    }

	    @Override
	    public void componentHidden(ComponentEvent e) {
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
        
        MapMaint.myPopupHandler handler4 = new MapMaint.myPopupHandler(tainstruct);
        tainstruct.add(handler4.getPopup());
        
        MapMaint.myPopupHandler handler5 = new MapMaint.myPopupHandler(taoutstruct);
        taoutstruct.add(handler5.getPopup());
        
        Document docmap = tamap.getDocument();
        docmap.addUndoableEditListener(new UndoableEditListener() {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
        undoManagerTAMAP.addEdit(e.getEdit());
        }
        });
        
        Document docinput = tainput.getDocument();
        docinput.addUndoableEditListener(new UndoableEditListener() {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
        undoManagerTAINPUT.addEdit(e.getEdit());
        }
        });
        
        Document docinstruct = tainstruct.getDocument();
        docinstruct.addUndoableEditListener(new UndoableEditListener() {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
        undoManagerTAINSTRUCT.addEdit(e.getEdit());
        }
        });
        
        Document docoutstruct = taoutstruct.getDocument();
        docoutstruct.addUndoableEditListener(new UndoableEditListener() {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
        undoManagerTAOUTSTRUCT.addEdit(e.getEdit());
        }
        });
        
        Document docoutput = taoutput.getDocument();
        docoutput.addUndoableEditListener(new UndoableEditListener() {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
        undoManagerTAOUTPUT.addEdit(e.getEdit());
        }
        });
        
        LineNumbersView lineNumbers = new LineNumbersView(tamap);
        scrollMap.setRowHeaderView(lineNumbers);
        
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
        
        InputTabbedPane.removeAll();
        InputTabbedPane.add("Input", inScrollPaneInput);
        InputTabbedPane.add("Structure", structScrollPane);
        
        OutputTabbedPane.removeAll();
        OutputTabbedPane.add("Output", outScrollPaneOutput);
        OutputTabbedPane.add("Structure", structScrollPaneout);
        
      //  InputTabbedPane.setEnabledAt(1, false);
      //  InputTabbedPane.setEnabledAt(2, false);
        
        
        tamap.setText("");
        tainput.setText("");
        tainstruct.setText("");
        taoutstruct.setText("");
        taoutput.setText("");
               
        addKeyBind(tamap);
        addKeyBind(tainput);
        addKeyBind(tainstruct);
        addKeyBind(taoutstruct);
        addKeyBind(taoutput);
        
        tainput.setFont(new Font("monospaced", Font.PLAIN, 12));
        tainstruct.setFont(new Font("monospaced", Font.PLAIN, 12));
        taoutstruct.setFont(new Font("monospaced", Font.PLAIN, 12));
        tamap.setFont(new Font("monospaced", Font.PLAIN, 12));
        taoutput.setFont(new Font("monospaced", Font.PLAIN, 12));
        
        
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
        
        ddinfiletype.setEnabled(false);
        ddoutfiletype.setEnabled(false);
        ddindoctype.setEnabled(false);
        ddoutdoctype.setEnabled(false);
        
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
                   
                   
                    ddinfiletype.setEnabled(false);
                    ddoutfiletype.setEnabled(false);
                   // ddindoctype.setEnabled(false);
                   // ddoutdoctype.setEnabled(false);
                   
        } else {
                   tbkey.setForeground(Color.red); 
                   cbinternal.setEnabled(false);
        }
    }
     
    public boolean validateInput(BlueSeerUtils.dbaction x) {
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"map_mstr"});
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
        btoverlay.setEnabled(true);
        btclear.setEnabled(true);
        mappanel.setVisible(true);
        inputpanel.setVisible(true);
        outputpanel.setVisible(true);
        btunzip.setEnabled(true);
       
        
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
         addFile(); // will create new empty file if path does not exist
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
         Path filetodelete = FileSystems.getDefault().getPath(tbpath.getText());
            try {
                if (Files.exists(filetodelete)) {
                Files.delete(filetodelete);
                }
            } catch (IOException ex) {
                bslog(ex);
            }
            String jarfile = tbpath.getText();
            if (tbpath.getText().endsWith(".java")) {
                jarfile = tbpath.getText().substring(0,tbpath.getText().length() - 5) + ".jar"; 
            }
            filetodelete = FileSystems.getDefault().getPath(jarfile);
            try {
                if (Files.exists(filetodelete)) {
                Files.delete(filetodelete);
                }
            } catch (IOException ex) {
                bslog(ex);
            }
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
        int i = 0;
        if (path.toFile().exists()) {
            try {   
                List<String> lines = Files.readAllLines(path);
                for (String segment : lines ) {
                        i++;
                        tamap.append(segment);
                        tamap.append("\n");
                }
                if (i == 0) {
                    tamap.append("// Example methods" + "\n");
                    tamap.append("// mapSegment(\"SEGMENT\",\"FIELD\",getInput(\"BEG\",3));" + "\n");
                    tamap.append("// commitSegment(\"SEGMENT\");" + "\n");
                }
            } catch (IOException ex) {
                bslog(ex);
            }   
        }
        tamap.setCaretPosition(0);
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
        String currentkey = "";
        for (String s : lines) {
            if (s.startsWith("#")) {
              continue;
            }
            
            String[] t = s.split(",",-1);
            
            if (t == null || t.length < 11) {  // should be 11 fields in each line of a structure file
                continue;
            }
            
            if (t[5].equals("groupend")) {
              continue;
            }
            
            currentkey = (t[1].isBlank()) ? t[0] : t[1] + ":" + t[0];
            
             //if (i == 0) { lastkey = t[0];}
               if (i == 0) { lastkey = currentkey; }
               
            
            if (! currentkey.equals(lastkey)) {
                LinkedHashMap<Integer, String[]> w = z;
                msf.put(currentkey, w);
                z = new LinkedHashMap<Integer, String[]>();
                i = 0;
                z.put(i, t);

            } else {
                z.put(i, t);
                msf.put(currentkey, z);
            }
            i++;
            lastkey = currentkey;
        }
        /*
        for (Map.Entry<String, HashMap<Integer,String[]>> u : msf.entrySet()) {
            HashMap<Integer,String[]> y = u.getValue();
            for (Map.Entry<Integer,String[]> t : y.entrySet()) {
            System.out.println("KEY:" + u.getKey());
            System.out.println("INTEGER:" + t.getKey());
            System.out.println("VALUE:" + String.join(",", t.getValue()));
            }
        }
        */
        
        return msf;
    }
       
    public void showStructure(String taname) {
        if (taname.equals("tainstruct")) {
           
            List<String> lines = getDSFasString(ddifs.getSelectedItem().toString());
            tainstruct.setText("");
            for (String segment : lines ) {
                            tainstruct.append(segment);
                            tainstruct.append("\n");
            }
            tainstruct.setCaretPosition(0);
        }
        if (taname.equals("taoutstruct")) {
            List<String> lines = getDSFasString(ddofs.getSelectedItem().toString());
            taoutstruct.setText("");
            for (String segment : lines ) {
                            taoutstruct.append(segment);
                            taoutstruct.append("\n");
            }
            taoutstruct.setCaretPosition(0);
        }
        
         

    }
   
    Action actionNext = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent s = (JTextComponent) e.getSource();
      searchTextNext(s.getName());
    }
    };
        
    Action actionSearch = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent s = (JTextComponent) e.getSource();
      searchTextArea(s.getName());
    }
    };
    
    Action actionUndo = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent s = (JTextComponent) e.getSource();
      try {
           if (s.getName().equals("tamap")) {
            if (undoManagerTAMAP.canUndo()) {
                undoManagerTAMAP.undo();
            }
           }
           if (s.getName().equals("tainput")) {
            if (undoManagerTAINPUT.canUndo()) {
                undoManagerTAINPUT.undo();
            }
           }
           if (s.getName().equals("tainstruct")) {
            if (undoManagerTAINSTRUCT.canUndo()) {
                undoManagerTAINSTRUCT.undo();
            }
           }
           if (s.getName().equals("taoutstruct")) {
            if (undoManagerTAOUTSTRUCT.canUndo()) {
                undoManagerTAOUTSTRUCT.undo();
            }
           }
           if (s.getName().equals("taoutput")) {
            if (undoManagerTAOUTPUT.canUndo()) {
                undoManagerTAOUTPUT.undo();
            }
           }
        } catch (CannotUndoException exp) {
            exp.printStackTrace();
        }
    }
    };

    private void documentChanged() {
	      SwingUtilities.invokeLater(() -> {
	        repaint();
	      });
	    }
    
    private void addKeyBind(JTextComponent ta) {
        ta.getInputMap().put(KeyStroke.getKeyStroke("control F"), ta.getName());
        ta.getActionMap().put(ta.getName(), actionSearch);
        ta.getInputMap().put(KeyStroke.getKeyStroke("control N"), ta.getName() + "_next");
        ta.getActionMap().put(ta.getName() + "_next", actionNext);
        
        ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
        ta.getActionMap().put("Undo", actionUndo);
        
  }
    
    public void showOverlay(String taname) {
        //GlobalDebug = true;
        List<String> structure = null;
        ArrayList<String> input = null;
        String[] delims = new String[]{"","",""};
        File file = null;
        String fs = "";
        boolean isInput = false;
        
        if (taname.equals("tainstruct")) {
            tainstruct.setText("");
            structure = getDSFasString(ddifs.getSelectedItem().toString());
            file = getfile("Open Input File to Overlay on Structure");
            if (structure.size() == 0) {
              tainstruct.setText("unable to read structure file");
              return;
            }            
            if (file != null) {
                char[] cbuf = readEDIRawFileIntoCbuf(file.toPath());
                if (cbuf == null) {
                  tainstruct.setText("file cbuf content is null");
                    return;  
                }
                delims = getDelimiters(cbuf, file.getName()); // seg, ele, sub
               // bsmf.MainFrame.show(delims[0] + "/" + delims[1] + "/" + delims[2]);
                if (delims == null) {
                  tainstruct.setText("unable to determine delimiters (null array returned) ");
                    return;  
                }
                if (ddinfiletype.getSelectedItem().toString().equals("JSON") || ddinfiletype.getSelectedItem().toString().equals("XML")) {
                    StringBuilder xstring = new StringBuilder();
                    for (int i = 0; i < cbuf.length; i++) {
                        xstring.append(cbuf[i]);
                    }
                    input = new ArrayList<String>();
                    input.add(xstring.toString());
                } else {
                input = cbufToList(cbuf, delims);
                }
            } else {
                tainstruct.setText("unable to read file");
                return;
            }
           
            fs = ddinfiletype.getSelectedItem().toString();
            /*
            if (ddinfiletype.getSelectedItem().toString().startsWith("X12")) {
                fs = "X12";
            } else if (ddifs.getSelectedItem().toString().startsWith("CSV")) {
                fs = "CSV";
            } else if (ddifs.getSelectedItem().toString().startsWith("UNE")) {
                fs = "UNE";  
            } else if (ddifs.getSelectedItem().toString().startsWith("XML")) {
                fs = "XML";  
            } else if (ddifs.getSelectedItem().toString().startsWith("JSON")) {
                fs = "JSON";      
            } else {
                fs = "FF";
            }
            */
            isInput = true;
        }
        if (taname.equals("taoutput")) {
            structure = getDSFasString(ddofs.getSelectedItem().toString());
            fs = ddoutfiletype.getSelectedItem().toString();
            if (! taoutput.getText().isBlank()) {
                input = (ArrayList) Arrays.asList(taoutput.getText().split("\n"));
            }
            taoutput.setText("");
            isInput = false;
        }
         
        if (input == null || structure == null) {
            return;
        }
        
            Map<String, HashMap<Integer,String[]>> msf = getStructureAsHashMap(structure);
            String[] c = new String[39];
            c[28] = fs;
            c[9] = delims[0];
            c[10] = delims[1];
            c[11] = delims[2];
            LinkedHashMap<String, String[]> mappedData = EDIMap.mapInput(c, input, getStructureSplit(structure));
            
            for (Map.Entry<String, String[]> z : mappedData.entrySet()) {
                    String[] keyx = z.getKey().split("\\+", -1);
                    String key = keyx[0];
                   
                 //   System.out.println("HERE: " + " key: " + z.getKey() + " value: " + String.join(",", z.getValue()));
                    
                    /*
                    if (keyx[0].contains(":")) {
                        String[] keyp = keyx[0].split(":", -1); 
                        key = keyp[keyp.length - 1];
                    } else {
                        key = keyx[0];
                    }
                    */
                    int i = 0;
                    String fieldname = "";
                    String desc = "";
                    for (String s : z.getValue()) {
                     fieldname = "";
                     desc = "";
                     if (s == null) {
                         continue;
                     }
                 //    System.out.println("HERE: " + msf.get(key).toString() + "/" + msf.get(key).get(i).toString() + "/" + s);
                     if (msf.get(key) != null && msf.get(key).get(i) != null) {
                         String[] j = msf.get(key).get(i);
                         if (j != null && j.length > 5) {
                             fieldname = j[5];
                             desc = j[6];
                         } else {
                             fieldname = "unknown";
                             desc = "unknown";
                         }
                     } 
                     tainstruct.append(z.getKey() + "\t" + fieldname + "\t" + desc +  " --> FieldNumber: " + i + "   " + "ValueLength: " + s.length() + " value: " + s +  "\n");   
                     i++;
                    }
                   
            }
        tainstruct.setCaretPosition(0);
        taoutput.setCaretPosition(0);
    }
    
    public void identify() {
        
        String bsdoctype = "";
        String sender = "";
        String receiver = "";
        
        ArrayList<String> input = null;
        String[] delims = new String[]{"","",""};
        char[] cbuf = tainput.getText().toCharArray();
        String[] editype = getEDIType(cbuf, "");
        if (editype != null) {
            taoutput.setText("");
            taoutput.append("File Type: " + editype[0] + "\n");
            bsdoctype = getBSDocTypeFromStds(editype[1]);
            taoutput.append("Document Type: " + bsdoctype + "\n");
        }
        
        String dummyName = "";
        if (editype[0].equals("CSV")) {
            dummyName = "dummy.CSV";
        }
        if (editype[0].equals("XML")) {
            dummyName = "dummy.XML";
        }
        delims = getDelimiters(cbuf, dummyName); // seg, ele, sub
           // bsmf.MainFrame.show(delims[0] + "/" + delims[1] + "/" + delims[2]);
            if (delims == null) {
              taoutput.append("unable to determine delimiters (null array returned) \n");
                return;  
            }
        input = cbufToList(cbuf, delims);
        if (editype[0].equals("X12")) {
            String[] s = input.get(1).split(escapeDelimiter(delims[1]));
            if (s != null && s.length > 2) {
                taoutput.append("GS SENDER: " + s[2].trim() + "\n");
                sender = s[2].trim();
            }
            if (s != null && s.length > 2) {
                taoutput.append("GS RECEIVER: " + s[3].trim() + "\n");
                receiver = s[3].trim();
            }            
        }
        if (editype[0].equals("UNE")) {
            String[] s = null;
            if (input.get(0).startsWith("UNA")) {
               s = input.get(1).split(escapeDelimiter(delims[1])); 
            } else {
               s = input.get(0).split(escapeDelimiter(delims[1])); 
            }
                  
            if (s != null && s.length > 2) {
                if (s[2].contains(":")) {
                 sender = s[2].split(":")[0];
                 receiver = s[3].split(":")[0];
                 taoutput.append("UNB SENDER: " + sender + "\n");
                 taoutput.append("UNB RECEIVER: " + receiver + "\n");   
                } else {
                 sender = s[2];
                 receiver = s[3];
                 taoutput.append("UNB SENDER: " + sender + "\n");
                 taoutput.append("UNB RECEIVER: " + receiver + "\n");   
                }
                
            }         
        }
        
        String[] tp = EDData.getEDITPDefaults(bsdoctype, sender, receiver );
        if (! tp[0].isBlank()) {
            taoutput.append("Found Partner Record: " + tp[18]  + "\n");
            taoutput.append("In Doc Type: " + tp[0]  + "\n");
            taoutput.append("Out Doc Type: " + tp[14]  + "\n");
            taoutput.append("MAP: " + tp[24]  + "\n");
        } else {
            taoutput.append("Could not identify Partner Record for this document"  + "\n");
        }
        
    }
    
    public void hexReplace(String taname) {
        JTextComponent ta = null;
        char toHex = 0;
        if (taname.equals("tainput")) {
            ta = tainput;
        } 
        if (taname.equals("taoutput")) {
            ta = taoutput;
        }
        String text = bsmf.MainFrame.input("Hex Chars: ");
        if (text == null || text.isBlank()) {
            return;
        }
        String[] replacehex = text.split("\\|",-1);
        if (replacehex == null || replacehex.length != 2 || replacehex[0].isBlank()) {
            return;
        }
        char fromHex = (char) Integer.parseInt(replacehex[0], 16);
        if (! replacehex[1].isBlank()) { 
        toHex = (char) Integer.parseInt(replacehex[1], 16);
        }
        
        Document d = ta.getDocument();
        int count = 0;
         try {
             String data = d.getText(0, d.getLength());
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
             if (taname.equals("tainput")) {
             tainput.setText("");
             tainput.setText(sb.toString());
             }
             
             if (taname.equals("taoutput")) {
             taoutput.setText("");
             taoutput.setText(sb.toString());
             }
             bsmf.MainFrame.show("Occurences: " + count);
         } catch (BadLocationException ex) {
             bslog(ex);
             bsmf.MainFrame.show(ex.getMessage());
         }
        
    }
    
    public void download(String taname) {
       JTextComponent ta = null; 
       if (taname.equals("tainput")) {
            ta = tainput;
        } 
        if (taname.equals("taoutput")) {
            ta = taoutput;
        }
        
        if (ta != null) {
           if (ta.getText().isBlank()) {
               bsmf.MainFrame.show("Panel is empty");
               return;
           }
        }
        
        String dir = bsmf.MainFrame.temp + "/" + "file." + Long.toHexString(System.currentTimeMillis()) + ".txt";
        Path path = FileSystems.getDefault().getPath(dir);
         try {
             if (ta != null) {
             Files.write(path, ta.getText().getBytes());
             bsmf.MainFrame.show("File written to: " + dir);
             } else {
               bsmf.MainFrame.show("input panel unknown!");  
             }
             
         } catch (IOException ex) {
             bsmf.MainFrame.show("unable to write to: " + dir);
         } 
    }
    
    public void searchTextArea(String taname) {
        if (taname.equals("tainput")) {
            cleanHighlights(taname);
            String text = bsmf.MainFrame.input("Text: ");
            highlightSearch(tainput, text);            
        }
        if (taname.equals("tainstruct")) {
            cleanHighlights(taname);
            String text = bsmf.MainFrame.input("Text: ");
            highlightSearch(tainstruct, text);            
        }
        if (taname.equals("taoutstruct")) {
            cleanHighlights(taname);
            String text = bsmf.MainFrame.input("Text: ");
            highlightSearch(taoutstruct, text);            
        }
        if (taname.equals("tamap")) {
            cleanHighlights(taname);
            String text = bsmf.MainFrame.input("Text: ");
            highlightSearch(tamap, text);            
        }
        if (taname.equals("taoutput")) {
            cleanHighlights(taname);
            String text = bsmf.MainFrame.input("Text: ");
            highlightSearch(taoutput, text);            
        }
    }
    
    public void searchTextNext(String taname) {
        if (taname.equals("tainput")) {
            highlightNext(tainput);            
        }
        if (taname.equals("tainstruct")) {
            highlightNext(tainstruct);            
        }
        if (taname.equals("taoutstruct")) {
            highlightNext(taoutstruct);            
        }
        if (taname.equals("tamap")) {
            highlightNext(tamap);            
        }
        if (taname.equals("taoutput")) {
            highlightNext(taoutput);            
        }
    }
    
    public void clear(String taname) {
        if (taname.equals("tainput")) {
            tainput.setText("");            
        }
        if (taname.equals("tainstruct")) {
            tainstruct.setText("");            
        }
        if (taname.equals("taoutstruct")) {
            taoutstruct.setText("");            
        }
        if (taname.equals("tamap")) {
            tamap.setText("");            
        }
        if (taname.equals("taoutput")) {
            taoutput.setText("");          
        }
    }
    
    
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }
    
    public void cleanHighlights(String taname) {
               
        if (taname.equals("tainput")) {
            Highlighter h = tainput.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    
                    h.removeHighlight(hl[i]);
                }
            }
        }
        if (taname.equals("tainstruct")) {
            Highlighter h = tainstruct.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    
                    h.removeHighlight(hl[i]);
                }
            }
        }
        if (taname.equals("tamap")) {
            Highlighter h = tamap.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    h.removeHighlight(hl[i]);
                }
            }
        }
        if (taname.equals("taoutput")) {
            Highlighter h = taoutput.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    h.removeHighlight(hl[i]);
                }
            }
        }
        if (taname.equals("taoutstruct")) {
            Highlighter h = taoutstruct.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    
                    h.removeHighlight(hl[i]);
                }
            }
        }
    }
    
    public void highlightSearch(JTextComponent ta, String phrase) {
        if (phrase == null || phrase.isBlank()) {
            return;
        }
        Highlighter h = ta.getHighlighter();
        Document d = ta.getDocument();
        int pos = 0;
        int count = 0;
        String text;
         try {
            text = d.getText(0, d.getLength());
            while ((pos = text.toUpperCase().indexOf(phrase.toUpperCase(),pos)) >= 0) {
                h.addHighlight(pos, pos + phrase.length(), myHighlightPainter);
                pos += phrase.length();
                count++;
            }
            bsmf.MainFrame.show("Occurences: " + count);
            
         } catch (BadLocationException ex) {
             bslog(ex);
         }
    }
    
    public void highlightNext(JTextComponent ta) {
       int current = 0; 
       
       if (ta.getName().equals("tainput")) {
            current = tainput.getCaretPosition();
            Highlighter h = tainput.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    if (hl[i].getStartOffset() > current) {
                      tainput.setCaretPosition(hl[i].getStartOffset());
                      break;
                    }
                }
            }
        }
       if (ta.getName().equals("tainstruct")) {
           current = tainstruct.getCaretPosition();
            Highlighter h = tainstruct.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    if (hl[i].getStartOffset() > current) {
                      tainstruct.setCaretPosition(hl[i].getStartOffset());
                      break;
                    }
                }
            }
        }
       if (ta.getName().equals("taoutstruct")) {
           current = taoutstruct.getCaretPosition();
            Highlighter h = taoutstruct.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    if (hl[i].getStartOffset() > current) {
                      taoutstruct.setCaretPosition(hl[i].getStartOffset());
                      break;
                    }
                }
            }
        }
        if (ta.getName().equals("tamap")) {
            current = tamap.getCaretPosition();
            Highlighter h = tamap.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    if (hl[i].getStartOffset() > current) {
                      tamap.setCaretPosition(hl[i].getStartOffset());
                      break;
                    }
                }
            }
        }
        if (ta.getName().equals("taoutput")) {
            current = taoutput.getCaretPosition();
            Highlighter h = taoutput.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    if (hl[i].getStartOffset() > current) {
                      taoutput.setCaretPosition(hl[i].getStartOffset());
                      break;
                    }
                }
            }
        }
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
    
    public void addFile() {
        Path path = FileSystems.getDefault().getPath(tbpath.getText());
         try {
             if (! path.toFile().exists()) {
                 if (! tamap.getText().isBlank()) {
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile(), false)));
                    output.write(tamap.getText());
                    output.close();  
                 } else {
                    path.toFile().createNewFile(); 
                 }
             }
         } catch (IOException ex) {
             bslog(ex);
         }
    }
    
    public String[] loadZippedMap() throws IOException {
        String[] m = new String[]{"",""};
        Path patch = null;
        String v_map = "";
        String v_inddf = "";
        String v_outddf = "";
        
        File zipfile = getfile("Select Map Zip File");
        Path pathextractdir = FileSystems.getDefault().getPath(cleanDirString(getSystemTempDirectory()) + "mapextract");
        if (! pathextractdir.toFile().exists()) {
           pathextractdir.toFile().mkdir();
        }        
        //  extract zip into temp         
        File destDir = pathextractdir.toFile();
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipfile));
        ZipEntry zipEntry = zis.getNextEntry();
        String root = zipEntry.getName().split(Pattern.quote("\\"),-1)[0];
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create zip directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create zip directory " + parent);
                }
                
                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
        zipEntry = zis.getNextEntry();
       }
        zis.closeEntry();
        zis.close();
        
        // read manifest.txt file and parse into variables
        Path manifestfile = FileSystems.getDefault().getPath(cleanDirString(pathextractdir.toString()) + "manifest.txt");
        if (manifestfile != null) {
        List<String> lines = Files.readAllLines(manifestfile);
                for (String segment : lines ) {
                    if (segment.isBlank()) {
                        continue;
                    }
                    String[] v = segment.split("=");
                    if (v.length > 1) {
                        if (v[0].equals("map")) {
                            v_map = v[1];
                        }
                        if (v[0].equals("ifs")) {
                            v_inddf = v[1];
                        }
                        if (v[0].equals("ofs")) {
                            v_outddf = v[1];
                        }
                    }
                }
        }
        
        // if all is good here...copy map (filename.java) over to edi/maps directory
        Path sourcepath = FileSystems.getDefault().getPath(cleanDirString(pathextractdir.toString()) + v_map);
        Path destinationpath = FileSystems.getDefault().getPath(cleanDirString(EDData.getEDIMapDir()) + v_map);
        
        boolean mapOverwrite = true;
        if (destinationpath.toFile().exists()) {
             mapOverwrite = bsmf.MainFrame.warn("Overwrite map file? " + v_map);
        }
        
        
        if (mapOverwrite) {
            Files.copy(sourcepath, destinationpath, StandardCopyOption.REPLACE_EXISTING);

            // load map_mstr table info
            String mapkey = "";
            int index = v_map.lastIndexOf(".java");
            if (index > 0) {
                mapkey = v_map.substring(0, index);
            }
            if (isValidMapid(mapkey)) { // delete old map rec if exists...since overwrite is true
                deleteMapMstr(new map_mstr(mapkey));
            }
            
            Path mapmstr = FileSystems.getDefault().getPath(cleanDirString(pathextractdir.toString()) + "mapmstr.csv");
            if (mapmstr != null) {
            map_mstr x = null;
            List<String> lines = Files.readAllLines(mapmstr);
            for (String segment : lines ) {
                if (segment.isBlank()) {
                    continue;
                }
                String[] v = segment.split(",", -1);
                x = new map_mstr(null, v[0],
                        v[1],
                        v[2],
                        v[3],
                        v[4],
                        v[5],
                        v[6],
                        v[7],
                        v[8],
                        v[9],
                        v[10], // package
                        v[11] // tinyint
                        );
            }
               m = addMapMstr(x);
            }

            if (! m[0].equals("0")) {
                return m;
            }
        }
        
        // load dfs_mstr, dfs_det table info for INBOUND Structure File
        boolean isfOverwrite = true;
        if (isValidDFSid(v_inddf)) {
             isfOverwrite = bsmf.MainFrame.warn("Overwrite inbound structure file? " + v_inddf);
        }
        if (isfOverwrite) {
            deleteDFStructure(new dfs_mstr(v_inddf));
            Path dfsmstr = FileSystems.getDefault().getPath(cleanDirString(pathextractdir.toString()) + v_inddf + ".dfs");
            Path dfsdet = FileSystems.getDefault().getPath(cleanDirString(pathextractdir.toString()) + v_inddf + ".det");
            if (dfsmstr != null && dfsdet != null) {
            List<String> dfslines = Files.readAllLines(dfsmstr);
            List<String> dfsdetlines = Files.readAllLines(dfsdet);
            dfs_mstr x = null;
            for (String dfs : dfslines ) {
                if (dfs.isBlank()) {
                    continue;
                }
                String[] v = dfs.split(",", -1);
                x = new dfs_mstr(null, v[0],
                    v[1],
                    v[2],
                    v[3],
                    v[4],
                    v[5],
                    "", // misc
                    "0" // default no suppression
                    );
            }
            ArrayList<dfs_det> detlist = new ArrayList<dfs_det>();
            for (String det : dfsdetlines ) {
                if (det.isBlank()) {
                    continue;
                }
                String[] v = det.split(",", -1);
                dfs_det y = new dfs_det(null, 
                    v[0],
                    v[1],
                    v[2],
                    v[3],    
                    v[4],
                    v[5],
                    v[6],
                    v[7],
                    v[8],
                    v[9],
                    v[10],
                    v[11],
                    v[12]
                    );
                detlist.add(y);
            }

             m = addDFStructureTransaction(detlist, x); 
            }

            if (! m[0].equals("0")) {
                return m;
            }
        }
        
         // load dfs_mstr, dfs_det table info for OUTBOUND Structure File
         boolean osfOverwrite = true;
        if (isValidDFSid(v_outddf)) {
             osfOverwrite = bsmf.MainFrame.warn("Overwrite outbound structure file? " + v_outddf);
        }
        if (osfOverwrite) {
            deleteDFStructure(new dfs_mstr(v_outddf));
            Path dfsmstr = FileSystems.getDefault().getPath(cleanDirString(pathextractdir.toString()) + v_outddf + ".dfs");
            Path dfsdet = FileSystems.getDefault().getPath(cleanDirString(pathextractdir.toString()) + v_outddf + ".det");
            if (dfsmstr != null && dfsdet != null) {
            List<String> dfslines = Files.readAllLines(dfsmstr);
            List<String> dfsdetlines = Files.readAllLines(dfsdet);
            dfs_mstr x = null;
            for (String dfs : dfslines ) {
                if (dfs.isBlank()) {
                    continue;
                }
                String[] v = dfs.split(",", -1);
                x = new dfs_mstr(null, v[0],
                    v[1],
                    v[2],
                    v[3],
                    v[4],
                    v[5],
                    "", // misc
                    "0" // default no suppression
                    );
            }
            ArrayList<dfs_det> detlist = new ArrayList<dfs_det>();
            for (String det : dfsdetlines ) {
                if (det.isBlank()) {
                    continue;
                }
                String[] v = det.split(",", -1);
                dfs_det y = new dfs_det(null, 
                    v[0],
                    v[1],
                    v[2],
                    v[3],    
                    v[4],
                    v[5],
                    v[6],
                    v[7],
                    v[8],
                    v[9],
                    v[10],
                    v[11],
                    v[12]
                    );
                detlist.add(y);
            }

              m = addDFStructureTransaction(detlist, x); 
            }
            if (! m[0].equals("0")) {
                return m;
            }
        }
        
        // cleanup 
        if (pathextractdir.toFile().exists()) {
            File[] contents = pathextractdir.toFile().listFiles();
            if (contents != null) {
                for (File f : contents) {
                    f.delete();
                }
            }
           pathextractdir.toFile().delete();
        }  
        
        return m;
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
            Path path = FileSystems.getDefault().getPath(cleanDirString(EDData.getEDIMapDir()) + jarname);
            
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
    
    public JavaFileObject compileFile(String FileType) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        // get custom imports from text
        ArrayList<String> imports = extractImports();
        ArrayList<String> text = cleanText();
        
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("import java.util.ArrayList;");
        out.println("import java.io.IOException;");
        out.println("import static com.blueseer.edi.MapperUtils.*;");
        out.println("import static com.blueseer.edi.EDI.*;");
        
        for (String s : imports) {
          out.println(s);  
        }
        
        out.println("public class " + tbkey.getText() + " extends com.blueseer.edi.EDIMap " +  " {");
        out.println("  public String[] Mapdata(ArrayList doc, String[] c, ArrayList mx) throws IOException, UserDefinedException  {");  
        out.println("setControl(c);  ");
        out.println("if (isError) { return error;} ");
        out.println("mappedInput = mapInput(c, doc, ISF);");    
       
        for (String s : text) {
          out.println(s);  
        }
        out.println("mx.addAll(mxs);");
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
          taoutput.append(String.valueOf(diagnostic.getLineNumber() - 9) + "\n"); // 9 prefix lines added to source
       //   taoutput.append(String.valueOf(diagnostic.getPosition()) + "\n");
       //   taoutput.append(String.valueOf(diagnostic.getStartPosition()) + "\n");
       //   taoutput.append(String.valueOf(diagnostic.getEndPosition()) + "\n");
          taoutput.append(String.valueOf(diagnostic.getSource()) + "\n");
          taoutput.append(diagnostic.getMessage(null) + "\n");
          taoutput.append("\n");
        }
        taoutput.append("Compilation Success: " + success + "\n");

        return file;
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
        
    public void getInput() {
       
        infile = getfile("Open Input (test) File");
        tainput.setText("");
        if (infile != null) {
            try {   
                List<String> lines = Files.readAllLines(infile.toPath());
                for (String segment : lines ) {
                        tainput.append(segment);
                        tainput.append("\n");
                }
                tainput.setCaretPosition(0);
                if (! tbkey.getText().isBlank()) {
                btrun.setEnabled(true);
                }
            } catch (IOException ex) {
                bslog(ex);
            }   
        } else {
            btrun.setEnabled(false);
        }
    }
    
    public void listInternalMethods() {
        
        taoutput.setText("");
        Method[] methods = MapperUtils.class.getDeclaredMethods();
        Method[] edimap_methods = EDIMap.class.getDeclaredMethods();
        List<Method> ml = new ArrayList<Method>();
        for (Method m : methods ) {
           ml.add(m);
        }
        for (Method m : edimap_methods ) {
           AnnoDoc serviceDef = m.getAnnotation(AnnoDoc.class);
            if (serviceDef != null) { 
                ml.add(m);
            }
        }
        Collections.sort(ml, (o1, o2) -> (o1.getName().compareTo(o2.getName())));
                
                for (Method m : ml ) {
                        taoutput.append(m.getName());
                        taoutput.append("() \n");
                        Type[] types = m.getGenericParameterTypes();
                        
                        String[] desc = null;
                        String[] params = null;
                        if (Modifier.isPublic(m.getModifiers())) {
                        AnnoDoc serviceDef = m.getAnnotation(AnnoDoc.class);
                            if (serviceDef != null) {
                              params = serviceDef.params();
                              desc = serviceDef.desc();
                            }
                        }
                        int i = 1;
                        for (String s : params) {
                            if (i ==1) {
                                taoutput.append("  Parameters: ");
                            }
                            taoutput.append(s);
                            if (i < params.length) {
                            taoutput.append(",");
                            }
                            i++;
                        }
                        taoutput.append("\n");
                        i = 1;
                        for (String s : desc) {
                            if (i ==1) {
                                taoutput.append("  Desc: ");
                            }
                            taoutput.append("  " + s);
                            taoutput.append("\n");
                            i++;
                        }
                        taoutput.append("  return type: " + m.getReturnType().getSimpleName() + "\n");
                       
                }
                taoutput.setCaretPosition(0);
                if (! tbkey.getText().isBlank()) {
                btrun.setEnabled(true);
                }
          
        
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
        InputTabbedPane = new javax.swing.JTabbedPane();
        inScrollPaneInput = new javax.swing.JScrollPane();
        tainput = new javax.swing.JTextArea();
        structScrollPane = new javax.swing.JScrollPane();
        tainstruct = new javax.swing.JTextArea();
        mappanel = new javax.swing.JPanel();
        scrollMap = new javax.swing.JScrollPane();
        tamap = new javax.swing.JTextArea();
        outputpanel = new javax.swing.JPanel();
        OutputTabbedPane = new javax.swing.JTabbedPane();
        outScrollPaneOutput = new javax.swing.JScrollPane();
        taoutput = new javax.swing.JTextArea();
        structScrollPaneout = new javax.swing.JScrollPane();
        taoutstruct = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        toolbar = new javax.swing.JToolBar();
        btnew = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        btoverlay = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btcompile = new javax.swing.JButton();
        btunhide = new javax.swing.JButton();
        btrun = new javax.swing.JButton();
        btshiftleft = new javax.swing.JButton();
        btshiftright = new javax.swing.JButton();
        btzip = new javax.swing.JButton();
        btunzip = new javax.swing.JButton();
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

        inputpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Source"));
        inputpanel.setPreferredSize(new java.awt.Dimension(260, 764));
        inputpanel.setLayout(new javax.swing.BoxLayout(inputpanel, javax.swing.BoxLayout.LINE_AXIS));

        InputTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                InputTabbedPaneStateChanged(evt);
            }
        });

        tainput.setColumns(20);
        tainput.setRows(5);
        tainput.setName("tainput"); // NOI18N
        inScrollPaneInput.setViewportView(tainput);

        InputTabbedPane.addTab("tab2", inScrollPaneInput);

        structScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                structScrollPaneMouseClicked(evt);
            }
        });

        tainstruct.setColumns(20);
        tainstruct.setRows(5);
        tainstruct.setName("tainstruct"); // NOI18N
        structScrollPane.setViewportView(tainstruct);

        InputTabbedPane.addTab("tab3", structScrollPane);

        inputpanel.add(InputTabbedPane);

        tablepanel.add(inputpanel);

        mappanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Map"));
        mappanel.setPreferredSize(new java.awt.Dimension(525, 76));
        mappanel.setLayout(new javax.swing.BoxLayout(mappanel, javax.swing.BoxLayout.LINE_AXIS));

        scrollMap.setAutoscrolls(true);

        tamap.setColumns(20);
        tamap.setRows(5);
        tamap.setName("tamap"); // NOI18N
        scrollMap.setViewportView(tamap);

        mappanel.add(scrollMap);

        tablepanel.add(mappanel);

        outputpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Destination"));
        outputpanel.setPreferredSize(new java.awt.Dimension(260, 764));
        outputpanel.setLayout(new javax.swing.BoxLayout(outputpanel, javax.swing.BoxLayout.LINE_AXIS));

        OutputTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                OutputTabbedPaneStateChanged(evt);
            }
        });

        taoutput.setColumns(20);
        taoutput.setRows(5);
        taoutput.setName("taoutput"); // NOI18N
        outScrollPaneOutput.setViewportView(taoutput);

        OutputTabbedPane.addTab("tab2", outScrollPaneOutput);

        taoutstruct.setColumns(20);
        taoutstruct.setRows(5);
        taoutstruct.setName("taoutstruct"); // NOI18N
        structScrollPaneout.setViewportView(taoutstruct);

        OutputTabbedPane.addTab("tab3", structScrollPaneout);

        outputpanel.add(OutputTabbedPane);

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

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map.png"))); // NOI18N
        btlookup.setToolTipText("Get Map");
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

        btoverlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spy.png"))); // NOI18N
        btoverlay.setToolTipText("Inspect");
        btoverlay.setFocusable(false);
        btoverlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btoverlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btoverlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btoverlayActionPerformed(evt);
            }
        });
        toolbar.add(btoverlay);

        btclear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/file.png"))); // NOI18N
        btclear.setToolTipText("Clear");
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
        btadd.setToolTipText("Add");
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
        btdelete.setToolTipText("Delete");
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
        btupdate.setToolTipText("Save");
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
        btcompile.setToolTipText("Compile");
        btcompile.setFocusable(false);
        btcompile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btcompile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btcompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcompileActionPerformed(evt);
            }
        });
        toolbar.add(btcompile);

        btunhide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unhide.png"))); // NOI18N
        btunhide.setToolTipText("Unhide");
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
        btrun.setToolTipText("Execute");
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
        btshiftleft.setToolTipText("Palnels Left");
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
        btshiftright.setToolTipText("Panels Right");
        btshiftright.setFocusable(false);
        btshiftright.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btshiftright.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btshiftright.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshiftrightActionPerformed(evt);
            }
        });
        toolbar.add(btshiftright);

        btzip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zip.png"))); // NOI18N
        btzip.setToolTipText("Zip Map");
        btzip.setFocusable(false);
        btzip.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btzip.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btzip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btzipActionPerformed(evt);
            }
        });
        toolbar.add(btzip);

        btunzip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/output.png"))); // NOI18N
        btunzip.setToolTipText("Zip Map");
        btunzip.setFocusable(false);
        btunzip.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btunzip.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btunzip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btunzipActionPerformed(evt);
            }
        });
        toolbar.add(btunzip);

        tbkey.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbkeyFocusLost(evt);
            }
        });

        jLabel1.setText("Source");

        ddifs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddifsActionPerformed(evt);
            }
        });

        ddofs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddofsActionPerformed(evt);
            }
        });

        ddinfiletype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FF", "X12", "DB", "CSV", "UNE", "XML", "JSON" }));

        ddoutfiletype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FF", "X12", "DB", "CSV", "UNE", "XML", "JSON" }));

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE))
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
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbinternal)
                                .addGap(10, 10, 10))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
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
                    .addComponent(ddoutdoctype, 0, 161, Short.MAX_VALUE))
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
                .addContainerGap(31, Short.MAX_VALUE))
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
                .addGap(22, 266, Short.MAX_VALUE))
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
        
        if (tbkey.getText().isBlank()) {
            return;
        }
        
        if (tainput.getText().isBlank()) {
            bsmf.MainFrame.show("There is no data in the Input Tab");
            return;
        }
        
        map_mstr m = getMapMstr(new String[]{tbkey.getText()});
        
        if (! ddifs.getSelectedItem().toString().equals(m.map_ifs()) || ! ddofs.getSelectedItem().toString().equals(m.map_ofs())) {
           bsmf.MainFrame.show("you have map meta data changes pending...click save");
           return;
        }
        
        taoutput.setText("");
        
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
    //    String[] editype = getEDIType(cbuf, "testdata");
        String[] editype = new String[]{ddinfiletype.getSelectedItem().toString(),ddindoctype.getSelectedItem().toString()};
        
        dfs_mstr dfs = getDFSMstr(new String[]{ddifs.getSelectedItem().toString()});
        
        ArrayList<String> doc = new ArrayList<String>();
        ArrayList<String[]> messages = new ArrayList<String[]>();
        char segdelim = 0;
        char eledelim = 0;
        
        
        if (editype[0].equals("X12")) {
        Map<Integer, Object[]> ISAmap = createIMAP(cbuf, c, "", "", "", "");
        Map<Integer, ArrayList> d = null;
        
        int loopcount = 0;
        for (Map.Entry<Integer, Object[]> isa : ISAmap.entrySet()) {
           loopcount++;
           if (loopcount > 1) {
               break;
           }
           d = (HashMap<Integer, ArrayList>) isa.getValue()[5];
           segdelim = (char) Integer.valueOf(isa.getValue()[2].toString()).intValue();
           eledelim = (char) Integer.valueOf(isa.getValue()[3].toString()).intValue();
        }
        
        if (d == null) {
            taoutput.setText("unable to establish isaMAP...check structure of input...possible double delimiters");
            return;
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
         
        
        if (editype[0].equals("UNE")) {
        Map<Integer, Object[]> UNBmap = createMAPUNE(cbuf, c, "", "", "", "");
        Map<Integer, ArrayList> d = null;
        
        int loopcount = 0;
        for (Map.Entry<Integer, Object[]> isa : UNBmap.entrySet()) {
           loopcount++;
           if (loopcount > 1) {
               break;
           }
           d = (HashMap<Integer, ArrayList>) isa.getValue()[5];
           segdelim = (char) Integer.valueOf(isa.getValue()[2].toString()).intValue();
           eledelim = (char) Integer.valueOf(isa.getValue()[3].toString()).intValue();
        }
        
        if (d == null) {
            taoutput.setText("unable to establish UNBmap...check structure of input...possible double delimiters");
            return;
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
        
        
        if (editype[0].equals("FF")) {
            segdelim = (char) Integer.valueOf("10").intValue(); 
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
         
         
         if (editype[0].equals("CSV")) {
            
            segdelim = (char) Integer.valueOf("10").intValue(); 
            if (dfs != null && ! dfs.dfs_delimiter().isBlank()) { 
              eledelim = (char) Integer.valueOf(dfs.dfs_delimiter()).intValue();
            } else {
              eledelim = (char) Integer.valueOf("44").intValue();  
            }
           // bsmf.MainFrame.show("CSV HERE: " + eledelim + "/" + segdelim) ;
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
        
         if (editype[0].equals("JSON")) {
             StringBuilder jsonstring = new StringBuilder();
            for (int i = 0; i < cbuf.length; i++) {
                jsonstring.append(cbuf[i]);
            }
            doc.add(jsonstring.toString());
         }
         
         if (editype[0].equals("XML")) {
             StringBuilder xmlstring = new StringBuilder();
            for (int i = 0; i < cbuf.length; i++) {
                xmlstring.append(cbuf[i]);
            }
            doc.add(xmlstring.toString());
         }
         
                       
         if (editype[0].isEmpty()) {
             taoutput.setText("unknown file type");
            return;
         }
      
   // end of needs revamping
      
        
        c[0] = "MapTester";
        c[21] = "MapTester";
        c[1] = x.map_indoctype();
        c[9] = String.valueOf(Integer.valueOf(segdelim));
        c[10] = String.valueOf(Integer.valueOf(eledelim));
        
        
        
       // c[11] = "0";
        c[15] = x.map_outdoctype();
        c[2] = x.map_id();
        c[28] = x.map_infiletype();
        c[29] = x.map_outfiletype();
        c[30] = "1";
        // without a trading partner...outbound delimiters are default for the type
        if (x.map_outfiletype().equals("CSV")) {
        c[35] = "10"; // outseg
        c[36] = ""; // outele
        c[37] = ""; // outsub
        }
        if (x.map_outfiletype().equals("FF")) {
        c[35] = "10"; // outseg
        c[36] = ""; // outele
        c[37] = ""; // outsub
        }
        if (x.map_outfiletype().equals("UNE")) {
        c[35] = "39"; // outseg
        c[36] = "43"; // outele
        c[37] = "58"; // outsub
        }
        if (x.map_outfiletype().equals("X12")) {
        c[35] = "10";  // outseg
        c[36] = "42"; // outele
        c[37] = "126"; // outsub
        }
        StringWriter sw = null;
        URLClassLoader cl = null;
        try {
                // hot reloadable class capability...new classloader created and closed in finally block
                List<File> jars = Arrays.asList(new File(cleanDirString(EDData.getEDIMapDir())).listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar");
                }
                }));
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
                Object obj = cls.getDeclaredConstructor().newInstance();
                Method method = cls.getDeclaredMethod("Mapdata", ArrayList.class, String[].class, ArrayList.class);
                Object oc = method.invoke(obj, doc, c, messages);
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
                    } catch (IOException ex1) {
                        edilog(ex1);
                    }
                    
                    taoutput.setCaretPosition(0); 
                }
    }//GEN-LAST:event_btrunActionPerformed

    private void btcompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcompileActionPerformed
    taoutput.setText("");
    JavaFileObject file = compileFile(ddinfiletype.getSelectedItem().toString());
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
         if (! validateInput(BlueSeerUtils.dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.delete, new String[]{tbkey.getText()});   
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
        
        infile = getfile("Select Map File");
        
      //  Path path = FileSystems.getDefault().getPath(infile.getAbsolutePath());
        if (infile != null && infile.exists()) {
            Path currpath = FileSystems.getDefault().getPath(infile.getAbsolutePath());
            Path newpath = FileSystems.getDefault().getPath(cleanDirString(EDData.getEDIMapDir()) + infile.getName());
            
            tbpath.setText(newpath.toString());
            tamap.setText("");
            try {   
                
                if (! currpath.getParent().toUri().equals(newpath.getParent().toUri())) {
                    boolean sure = bsmf.MainFrame.warn("Are you sure you want to replace map?");  
                    if (sure) {
                        Files.copy(currpath, newpath, StandardCopyOption.REPLACE_EXISTING);
                        List<String> lines = Files.readAllLines(newpath);
                        for (String segment : lines ) {
                            tamap.append(segment);
                            tamap.append("\n");
                        }
                    }
                } else {
                    //Files.copy(currpath, newpath, StandardCopyOption.REPLACE_EXISTING);
                        List<String> lines = Files.readAllLines(newpath);
                        for (String segment : lines ) {
                            tamap.append(segment);
                            tamap.append("\n");
                        }
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
       tamap.setCaretPosition(0);
    }//GEN-LAST:event_btfindActionPerformed

    private void btoverlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btoverlayActionPerformed
        mappanel.setVisible(false);
        outputpanel.setVisible(true);
        inputpanel.setVisible(true);
        ddifs.setEnabled(true);
        tbkey.setEnabled(false);
        setPanelComponentState(inputpanel, true);
        setPanelComponentState(outputpanel, true);
    }//GEN-LAST:event_btoverlayActionPerformed

    private void btzipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btzipActionPerformed
        
        ZipOutputStream out;
        ZipEntry e;
        File f = new File("edi/map.zip");
        String dirpath;
        Path path;
        File file;
        byte[] data = null;
        
        
        // clean out old map.zip
        if (Files.exists(f.toPath())) {
            try {
                Files.delete(f.toPath());
            } catch (IOException ex) {
                bsmf.MainFrame.show("unable to remove old edi/map.zip");
                return;
            }
        }
        
        // make call to grab a test file
        File testfile = null;
         boolean proceed = bsmf.MainFrame.warn("would you like to include an input sample file?");
        if (proceed) {
            testfile = getfile("open test data file if to be included in map zip");
        }
        
       
        
        
        // now try to zip all 4, testfile, ifs, ofs, and map
    try {
        out = new ZipOutputStream(new FileOutputStream(f));
        
        
        // manifest.txt file
        e = new ZipEntry("manifest.txt");
        out.putNextEntry(e);
        StringBuilder sb = new StringBuilder();
        sb.append("map=" + tbkey.getText() + ".java").append("\n");
        sb.append("ifs=" + ddifs.getSelectedItem().toString()).append("\n");
        sb.append("ofs=" + ddofs.getSelectedItem().toString()).append("\n");
        out.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
        out.closeEntry();
        
        
        // java Map file into Zip
        e = new ZipEntry(tbkey.getText() + ".java");
        out.putNextEntry(e);
        dirpath = cleanDirString(EDData.getEDIMapDir()) + tbkey.getText() + ".java";
        path = FileSystems.getDefault().getPath(dirpath);
        file = path.toFile();
        data = null;
        if (file != null && file.exists()) {
                try {   
                    data = Files.readAllBytes(file.toPath());
                } catch (IOException ex) {
                    bslog(ex);
                }   
        }
        out.write(data, 0, data.length);
        out.closeEntry();
        
        
        // map_mstr DB record
        e = new ZipEntry("mapmstr.csv");
        out.putNextEntry(e);
        sb = new StringBuilder();
        map_mstr mm = getMapMstr(new String[]{tbkey.getText()});
        sb.append(mm.map_id()).append(",");
        sb.append(mm.map_desc()).append(",");
        sb.append(mm.map_version()).append(",");
        sb.append(mm.map_ifs()).append(",");
        sb.append(mm.map_ofs()).append(",");
        sb.append(mm.map_indoctype()).append(",");
        sb.append(mm.map_infiletype()).append(",");
        sb.append(mm.map_outdoctype()).append(",");
        sb.append(mm.map_outfiletype()).append(",");
        sb.append(mm.map_source()).append(",");
        sb.append(mm.map_package()).append(",");
        sb.append(mm.map_internal()).append("\n");
        out.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
        out.closeEntry();
        
                
        // IFS dfs file into Zip
        String ifsname = ddifs.getSelectedItem().toString();
        String[] dfsmstr = getDSFMstrasArray(ddifs.getSelectedItem().toString());
        if (dfsmstr != null) {
            e = new ZipEntry(ifsname + ".dfs");
            out.putNextEntry(e);
            sb = new StringBuilder();
            sb.append(String.join(",",Arrays.copyOfRange(dfsmstr, 1, dfsmstr.length))).append("\n");
            out.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
            out.closeEntry();
        }
        // IFS det file into Zip
        e = new ZipEntry(ifsname + ".det");
        out.putNextEntry(e);
        ArrayList<String[]> list = getDSFasArray(ddifs.getSelectedItem().toString());
        sb = new StringBuilder();
        for (String[] sarray : list) {
            sb.append(String.join(",",Arrays.copyOfRange(sarray, 1, sarray.length))).append("\n");
        }
        out.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
        out.closeEntry();
        
        
        // OFS dfs file into Zip
        String ofsname = ddofs.getSelectedItem().toString();
        dfsmstr = getDSFMstrasArray(ddofs.getSelectedItem().toString()); 
        if (dfsmstr != null) {
            e = new ZipEntry(ofsname + ".dfs");
            out.putNextEntry(e);
            sb = new StringBuilder();
            sb.append(String.join(",",Arrays.copyOfRange(dfsmstr, 1, dfsmstr.length))).append("\n");
            out.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
            out.closeEntry();
        }
        // OFS det file into Zip
        e = new ZipEntry(ofsname + ".det");
        out.putNextEntry(e);
        list = getDSFasArray(ddofs.getSelectedItem().toString());
        sb = new StringBuilder();
        for (String[] sarray : list) {
            sb.append(String.join(",",Arrays.copyOfRange(sarray, 1, sarray.length))).append("\n");
        }
        out.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
        out.closeEntry();
        
        
        
        // now test file
        if (testfile != null && testfile.exists()) {
            e = new ZipEntry(testfile.getName());
            out.putNextEntry(e);
            data = null;

            try {   
                data = Files.readAllBytes(testfile.toPath());
            } catch (IOException ex) {
                bslog(ex);
            }   

            out.write(data, 0, data.length);
            out.closeEntry();
        }
        out.close();
        
     } catch (FileNotFoundException ex) {
         bslog(ex);
     } catch (IOException ex) {
         bslog(ex);
     }
    
    if (Files.exists(f.toPath())) {
     if (testfile != null) {   
       bsmf.MainFrame.show("created map.zip file in edi directory");
     } else {
       bsmf.MainFrame.show("created map.zip file without test data in edi directory");  
     }
    } else {
     bsmf.MainFrame.show("unable to create map.zip file in edi directory");   
    }
    }//GEN-LAST:event_btzipActionPerformed

    private void tbkeyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbkeyFocusLost
        if (! tbkey.getText().isBlank()) {
            if (Character.isDigit(tbkey.getText().charAt(0))) {
                bsmf.MainFrame.show("map names cannot start with numbers");
                tbkey.setText("");
                tbpath.setText("");
                return;
            }
        }
        
        if (! tbkey.getText().isBlank() && tbkey.isEditable() && tbpath.isEnabled() && tbpath.getText().isBlank()) {
           tbpath.setText(cleanDirString(EDData.getEDIMapDir()) + tbkey.getText() + ".java");
        }
    }//GEN-LAST:event_tbkeyFocusLost

    private void ddifsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddifsActionPerformed
        if (! isLoad && ddifs.getSelectedItem() != null) {  
          ediData.dfs_mstr x = getDFSMstr(new String[]{ddifs.getSelectedItem().toString()});
          ddindoctype.setSelectedItem(x.dfs_doctype());
          ddinfiletype.setSelectedItem(x.dfs_filetype());
        }
    }//GEN-LAST:event_ddifsActionPerformed

    private void ddofsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddofsActionPerformed
        if (! isLoad && ddofs.getSelectedItem() != null) {
        ediData.dfs_mstr x = getDFSMstr(new String[]{ddofs.getSelectedItem().toString()});
          ddoutdoctype.setSelectedItem(x.dfs_doctype());
          ddoutfiletype.setSelectedItem(x.dfs_filetype());
        }
    }//GEN-LAST:event_ddofsActionPerformed

    private void structScrollPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_structScrollPaneMouseClicked
        
    }//GEN-LAST:event_structScrollPaneMouseClicked

    private void InputTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_InputTabbedPaneStateChanged
        if (! isLoad && InputTabbedPane.getSelectedIndex() == 1) {
            showStructure("tainstruct");
        }
    }//GEN-LAST:event_InputTabbedPaneStateChanged

    private void OutputTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_OutputTabbedPaneStateChanged
        if (! isLoad && OutputTabbedPane.getSelectedIndex() == 1) {
            showStructure("taoutstruct");
        }
    }//GEN-LAST:event_OutputTabbedPaneStateChanged

    private void btunzipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btunzipActionPerformed
         try {
             String[] m = loadZippedMap();
             if (m[0].equals("0")) {
                 bsmf.MainFrame.show("Map has been imported");
             } else if (m[0].isBlank()) {
                 bsmf.MainFrame.show("map update cancelled");
             } else {
                 bsmf.MainFrame.show("Problem with Map Import..." + m[1]);
             }
         } catch (IOException ex) {
             bslog(ex);
         }
    }//GEN-LAST:event_btunzipActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane InputTabbedPane;
    private javax.swing.JTabbedPane OutputTabbedPane;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcompile;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btfind;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btoverlay;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btshiftleft;
    private javax.swing.JButton btshiftright;
    private javax.swing.JButton btunhide;
    private javax.swing.JButton btunzip;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btzip;
    private javax.swing.JCheckBox cbinternal;
    private javax.swing.JComboBox<String> ddifs;
    private javax.swing.JComboBox<String> ddindoctype;
    private javax.swing.JComboBox<String> ddinfiletype;
    private javax.swing.JComboBox<String> ddofs;
    private javax.swing.JComboBox<String> ddoutdoctype;
    private javax.swing.JComboBox<String> ddoutfiletype;
    private javax.swing.JFileChooser fc;
    private javax.swing.JScrollPane inScrollPaneInput;
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
    private javax.swing.JPanel mappanel;
    private javax.swing.JScrollPane outScrollPaneOutput;
    private javax.swing.JPanel outputpanel;
    private javax.swing.JScrollPane scrollMap;
    private javax.swing.JScrollPane structScrollPane;
    private javax.swing.JScrollPane structScrollPaneout;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTextArea tainput;
    private javax.swing.JTextArea tainstruct;
    private javax.swing.JTextArea tamap;
    private javax.swing.JTextArea taoutput;
    private javax.swing.JTextArea taoutstruct;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbpackage;
    private javax.swing.JTextField tbpath;
    private javax.swing.JTextField tbversion;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
