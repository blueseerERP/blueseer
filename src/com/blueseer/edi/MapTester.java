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
import static com.blueseer.edi.ediData.getMapMstr;
import com.blueseer.edi.ediData.map_mstr;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.EDData;
import com.blueseer.vdr.venData;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author vaughnte
 */
public class MapTester extends javax.swing.JPanel  {
 
     public Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
     File infile = null;
     
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
    
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public MapTester() {
        initComponents();
        setLanguageTags(this);
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
    
    public void initvars(String[] arg) {
     
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        DateFormat dfperiod = new SimpleDateFormat("M");
       
        tamap.setText("");
        tasource.setText("");
        
        ddmap.removeAllItems();
        ArrayList<String> maps = ediData.getMapMstrList();
        for (int i = 0; i < maps.size(); i++) {
            ddmap.addItem(maps.get(i));
        }
        
        bthide.setEnabled(false);
        btrun.setEnabled(false);
        //outputpanel.setVisible(false);
          
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
        ddmap = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btcheckout = new javax.swing.JButton();
        btsave = new javax.swing.JButton();
        btcompile = new javax.swing.JButton();
        btinput = new javax.swing.JButton();
        btoutput = new javax.swing.JButton();
        bthide = new javax.swing.JButton();
        btunhide = new javax.swing.JButton();
        btrun = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Map Tester"));
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

        jToolBar1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btcheckout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/checkout2.png"))); // NOI18N
        btcheckout.setFocusable(false);
        btcheckout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btcheckout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btcheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcheckoutActionPerformed(evt);
            }
        });
        jToolBar1.add(btcheckout);

        btsave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        btsave.setFocusable(false);
        btsave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btsave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btsave);

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddmap, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 281, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
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
        // TODO add your handling code here:
    }//GEN-LAST:event_btcompileActionPerformed

    private void btunhideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btunhideActionPerformed
       outputpanel.setVisible(true);
       bthide.setEnabled(true);
    }//GEN-LAST:event_btunhideActionPerformed

    private void btcheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcheckoutActionPerformed
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
    }//GEN-LAST:event_btcheckoutActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcheckout;
    private javax.swing.JButton btcompile;
    private javax.swing.JButton bthide;
    private javax.swing.JButton btinput;
    private javax.swing.JButton btoutput;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btsave;
    private javax.swing.JButton btunhide;
    private javax.swing.JComboBox ddmap;
    private javax.swing.JFileChooser fc;
    private javax.swing.JPanel inputpanel;
    private javax.swing.JLabel jLabel6;
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
    // End of variables declaration//GEN-END:variables
}
