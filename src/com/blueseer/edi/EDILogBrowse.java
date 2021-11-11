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
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.EDData;
import java.awt.Color;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 *
 * @author vaughnte
 */
public class EDILogBrowse extends javax.swing.JPanel {

    
     javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{"IdxNbr", "TradeID", "Dir", "DOCTYPE", "TimeStamp", "File", "isaCtrlNbr", "gsCtrlNbr", "stCtrlNbr"});
    
    
    class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 2 || col == 3 || col == 4 || col == 5)       
                return String.class;  
            else return String.class;  //other columns accept String values  
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
        }    
    
     class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            if (column == 5)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
       
     
    
    
    /**
     * Creates new form EDILogBrowse
     */
    public EDILogBrowse() {
        initComponents();
    }

    
    public void initvars(String[] arg) {
        java.util.Date now = new java.util.Date();
         dcfrom.setDate(now);
         dcto.setDate(now);
         
         tbtradeid.setText("");
         tbdoc.setText("");
       
         
         tafile.setText("");
         mymodel.setNumRows(0);
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
    
   public void getEDIIDX() {
     
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
             
        mymodel.setNumRows(0);
        tafile.setText("");
        try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                int i = 0;

                String dir = "0";
               
                tablereport.setModel(mymodel);
                 tablereport.getColumnModel().getColumn(5).setCellRenderer(new EDILogBrowse.SomeRenderer()); 
                 tablereport.getColumnModel().getColumn(7).setCellRenderer(new EDILogBrowse.SomeRenderer()); 
                
                 
              
                    if (! tbtradeid.getText().isEmpty() && tbdoc.getText().isEmpty() ) {
                    res = st.executeQuery("SELECT * FROM edi_idx  " +
                    " where edx_sender >= " + "'" + tbtradeid.getText() + "'" +
                    " AND edx_sender <= " + "'" + tbtradeid.getText() + "'" +
                    " AND edx_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 24:00:00" + "'" + " order by edx_id desc ;" ) ;
                    }
                    
                    if (! tbdoc.getText().isEmpty() && tbtradeid.getText().isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_idx  " +
                    " where " +
                    " edx_doc >= " + "'" + tbdoc.getText() + "'" +
                    " AND edx_doc <= " + "'" + tbdoc.getText() + "'" +        
                    " AND edx_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 24:00:00" + "'" + " order by edx_id desc ;" ) ;
                    }
                    
                    if (! tbdoc.getText().isEmpty() && ! tbtradeid.getText().isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_idx  " +
                     " where edx_sender >= " + "'" + tbtradeid.getText() + "'" +
                    " AND edx_sender <= " + "'" + tbtradeid.getText() + "'" +
                    " edx_doc >= " + "'" + tbdoc.getText() + "'" +
                    " AND edx_doc <= " + "'" + tbdoc.getText() + "'" +        
                    " AND edx_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 24:00:00" + "'" + " order by edx_id desc ;" ) ;
                    }
                    
                    
                    if (tbtradeid.getText().isEmpty() && tbdoc.getText().isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_idx  " +
                    " where edx_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 24:00:00" + "'" + " order by edx_id desc ;" ) ;
                    }
                    
              

                while (res.next()) {
                    i++;
                    if (res.getInt("edx_dir") == 0) {
                     dir = "In";   
                    } else {
                     dir = "Out";
                    }
                     // "IdxNbr", "TradeID", "Dir", "DOCTYPE", "TimeStamp", "File", "isaCtrlNbr", "gsCtrlNbr", "stCtrlNbr"
                    mymodel.addRow(new Object[]{
                        res.getString("edx_id"),
                        res.getString("edx_sender"),
                        dir,
                        res.getString("edx_doc"),
                        res.getString("edx_ts"),
                        res.getString("edx_file"),
                        res.getString("edx_ctrlnum"),
                        res.getString("edx_gsctrlnum"),
                        res.getString("edx_stctrlnum")
                    });
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql code does not execute");
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
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

        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        dcto = new com.toedter.calendar.JDateChooser();
        dcfrom = new com.toedter.calendar.JDateChooser();
        btview = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tbtradeid = new javax.swing.JTextField();
        tbdoc = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tafile = new javax.swing.JTextArea();

        jTextField1.setText("jTextField1");

        jTextField2.setText("jTextField2");

        setBackground(new java.awt.Color(0, 102, 204));
        setLayout(new java.awt.BorderLayout());

        dcto.setDateFormatString("yyyy-MM-dd");

        dcfrom.setDateFormatString("yyyy-MM-dd");

        btview.setText("View");
        btview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btviewActionPerformed(evt);
            }
        });

        jLabel1.setText("TP ID:");

        jLabel2.setText("Doc Type:");

        jLabel3.setText("To Date:");

        jLabel4.setText("From Date:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcto, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addComponent(dcfrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbtradeid)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btview)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tbdoc))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcfrom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tbtradeid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tbdoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btview)
                .addGap(7, 7, 7))
        );

        tablereport.setAutoCreateRowSorter(true);
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
        tablereport.setColumnSelectionAllowed(true);
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

        tafile.setColumns(20);
        tafile.setRows(5);
        jScrollPane2.setViewportView(tafile);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(jScrollPane2)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(611, Short.MAX_VALUE))
        );

        add(jPanel4, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btviewActionPerformed
        getEDIIDX();
    }//GEN-LAST:event_btviewActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
         int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
       /*
        if ( col == 7) {
                if (! checkperms("MenuOrderMaint")) { return; }
                String args = tablereport.getValueAt(row, 7).toString();
              //  bsmf.MainFrame.itemmastmaintpanel.initvars(tablescrap.getValueAt(row, col).toString());
              reinitpanels("MenuOrderMaint",  true, args);
        }
       */
        if ( col == 5) {
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, col).toString().isEmpty()) {
                  ArrayList<String> segments = EDData.readEDIRawFileIntoArrayList(tablereport.getValueAt(row, 5).toString(), tablereport.getValueAt(row, 6).toString());  
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btview;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextArea tafile;
    private javax.swing.JTextField tbdoc;
    private javax.swing.JTextField tbtradeid;
    // End of variables declaration//GEN-END:variables
}
