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
       
        tainput.setText("");
        taoutput.setText("");
        
        ddmap.removeAllItems();
        ArrayList<String> maps = ediData.getMapMstrList();
        for (int i = 0; i < maps.size(); i++) {
            ddmap.addItem(maps.get(i));
        }
        
        btdetail.setEnabled(false);
        btRun.setEnabled(false);
        outputpanel.setVisible(false);
          
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
        tainput = new javax.swing.JTextArea();
        outputpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        taoutput = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        btdetail = new javax.swing.JButton();
        btRun = new javax.swing.JButton();
        ddmap = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        btupload = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Map Tester"));
        jPanel1.setName("panelmain"); // NOI18N

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        inputpanel.setLayout(new java.awt.BorderLayout());

        tainput.setColumns(20);
        tainput.setRows(5);
        jScrollPane4.setViewportView(tainput);

        inputpanel.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        tablepanel.add(inputpanel);

        outputpanel.setLayout(new java.awt.BorderLayout());

        taoutput.setColumns(20);
        taoutput.setRows(5);
        jScrollPane3.setViewportView(taoutput);

        outputpanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        tablepanel.add(outputpanel);

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel6.setText("Map");
        jLabel6.setName("lblfromreceiver"); // NOI18N

        btupload.setText("Upload");
        btupload.setName("btupload"); // NOI18N
        btupload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btuploadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddmap, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 158, Short.MAX_VALUE)
                .addComponent(btupload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdetail)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btRun)
                    .addComponent(btdetail)
                    .addComponent(jLabel6)
                    .addComponent(btupload))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 277, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE))
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

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
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
             taoutput.setText(ex.toString());
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
                     btdetail.setEnabled(true);   
                    } catch (IOException ex1) {
                        edilog(ex1);
                    }
                }

    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       outputpanel.setVisible(false);
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void btuploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btuploadActionPerformed
        infile = getfile();
        tainput.setText("");
        if (infile != null) {
            try {   
                List<String> lines = Files.readAllLines(infile.toPath());
                for (String segment : lines ) {
                        tainput.append(segment);
                        tainput.append("\n");
                }
                btRun.setEnabled(true);
            } catch (IOException ex) {
                bslog(ex);
            }   
        } else {
            btRun.setEnabled(false);
        }
    }//GEN-LAST:event_btuploadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btupload;
    private javax.swing.JComboBox ddmap;
    private javax.swing.JFileChooser fc;
    private javax.swing.JPanel inputpanel;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel outputpanel;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTextArea tainput;
    private javax.swing.JTextArea taoutput;
    // End of variables declaration//GEN-END:variables
}
