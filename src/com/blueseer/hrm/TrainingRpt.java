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

package com.blueseer.hrm;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.panelmap;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;

import static com.blueseer.utl.ReportPanel.TableReport;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author vaughnte
 */
public class TrainingRpt extends javax.swing.JPanel {

     javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("id"), 
                            getGlobalColumnTag("empid"), 
                            getGlobalColumnTag("lastname"), 
                            getGlobalColumnTag("firstname"), 
                            getGlobalColumnTag("description"), 
                            getGlobalColumnTag("startdate"), 
                            getGlobalColumnTag("enddate"), 
                            getGlobalColumnTag("hours"), 
                            getGlobalColumnTag("instructor"), 
                            getGlobalColumnTag("location") });
    /**
     * Creates new form CCReportPanel
     */
    public TrainingRpt() {
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
    
    public void initvars(String[] arg) {
       
        mymodel.setRowCount(0);
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        dcfrom.setDate(now);
        dcto.setDate(now);
        
        
        ArrayList<String> mylist = new ArrayList();
        if (ddempid.getItemCount() == 0) {
        mylist = hrmData.getempmstrlist();
        for (String emp : mylist) {
            ddempid.addItem(emp);
        }
        ddempid.insertItemAt("ALL", 0);
        ddempid.setSelectedIndex(0);
        } else {
            ddempid.setSelectedIndex(0);
        }
    }
    
     class MyTableModel extends DefaultTableModel {  
  
    public MyTableModel(Object rowData[][], Object columnNames[]) {  
         super(rowData, columnNames);  
      }  
     
    @Override  
      public Class getColumnClass(int col) {  
        if (col == 0)       //second column accepts only Integer values  
            return Integer.class;  
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

       
            if (column == 0)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        ddempid = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        dcto = new com.toedter.calendar.JDateChooser();
        btcsv = new javax.swing.JButton();
        btview = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        username = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

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
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

        jPanel1.setName("panelmain"); // NOI18N

        ddempid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddempidActionPerformed(evt);
            }
        });

        jLabel2.setText("Date From:");
        jLabel2.setName("lblfromdate"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        jLabel1.setText("Date To:");
        jLabel1.setName("lbltodate"); // NOI18N

        dcto.setDateFormatString("yyyy-MM-dd");

        btcsv.setText("Export");
        btcsv.setName("btexport"); // NOI18N
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        btview.setText("View");
        btview.setName("btview"); // NOI18N
        btview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btviewActionPerformed(evt);
            }
        });

        jLabel3.setText("EmployeeID");
        jLabel3.setName("lblempid"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddempid, 0, 162, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btcsv)
                .addGap(5, 5, 5)
                .addComponent(btview))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddempid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(btcsv))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(btview)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btviewActionPerformed
        
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                int i = 0;

               
                tablereport.setModel(mymodel);
                tablereport.getColumnModel().getColumn(0).setCellRenderer(new SomeRenderer());              
                
// ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
      //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                 //       new ButtonEditor(new JCheckBox()));


                if (! ddempid.getSelectedItem().toString().equals("ALL")) {
                res = st.executeQuery("SELECT emptrid, empid, title, startdate, enddate, hours, instructor, location, emp_lname, emp_fname FROM  emp_train inner join emp_mstr on emp_nbr = emp_train.empid where " + 
                              " startdate >= " + "'" + BlueSeerUtils.setDateFormat(dcfrom.getDate()) + "'" +
                              " AND startdate <= " + "'" + BlueSeerUtils.setDateFormat(dcto.getDate()) + "'" +
                              " And empid = " + "'" + ddempid.getSelectedItem().toString() + "'" +
                              " order by startdate desc;"
                              );
                } else {
                    res = st.executeQuery("SELECT emptrid, empid, title, startdate, enddate, hours, instructor, location, emp_lname, emp_fname FROM  emp_train inner join emp_mstr on emp_nbr = emp_train.empid where " + 
                              " startdate >= " + "'" + BlueSeerUtils.setDateFormat(dcfrom.getDate()) + "'" +
                              " AND startdate <= " + "'" + BlueSeerUtils.setDateFormat(dcto.getDate()) + "'" +
                              " order by startdate desc;"
                              );
                }

                while (res.next()) {
                    i++;

                    mymodel.addRow(new Object[]{res.getString("emptrid"), res.getString("empid"),
                                res.getString("emp_lname"),
                                res.getString("emp_fname"),
                                res.getString("title"),
                                res.getString("startdate"),
                                res.getString("enddate"),
                                res.getString("hours"),
                                res.getString("instructor"),
                                res.getString("location")
                            });
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
    }//GEN-LAST:event_btviewActionPerformed

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
        FileDialog fDialog;
        fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        //fDialog.setFile("data.csv");
        String path = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(path);
        BufferedWriter output;
        try {
            output = new BufferedWriter(new FileWriter(f));
      
               String myheader = "EmpID, Training, StartDate, EndDate, Hours, Instructor, Location ";
                 output.write(myheader + '\n');
                 
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
              if (! ddempid.getSelectedItem().toString().equals("ALL")) {
                res = st.executeQuery("SELECT empid, title, startdate, enddate, hours, instructor, location, emp_lname, emp_fname FROM  emp_train inner join emp_mstr on emp_nbr = emp_train.empid where " + 
                              " startdate >= " + "'" + BlueSeerUtils.setDateFormat(dcfrom.getDate()) + "'" +
                              " AND startdate <= " + "'" + BlueSeerUtils.setDateFormat(dcto.getDate()) + "'" +
                              " And empid = " + "'" + ddempid.getSelectedItem().toString() + "'" +
                              " order by startdate desc;"
                              );
                } else {
                    res = st.executeQuery("SELECT empid, title, startdate, enddate, hours, instructor, location, emp_lname, emp_fname FROM  emp_train inner join emp_mstr on emp_nbr = emp_train.empid where " + 
                              " startdate >= " + "'" + BlueSeerUtils.setDateFormat(dcfrom.getDate()) + "'" +
                              " AND startdate <= " + "'" + BlueSeerUtils.setDateFormat(dcto.getDate()) + "'" +
                              " order by startdate desc;"
                              );
                }

                while (res.next()) {
                    String newstring = res.getString("empid") + "," + res.getString("emp_lname") + "," + res.getString("emp_fname") + "," +
                            res.getString("title").replace(",","") + "," + 
                            res.getString("startdate") + "," + res.getString("enddate") + "," + res.getString("hours") + "," + 
                            res.getString("instructor") + "," +
                            res.getString("location"); 
                    output.write(newstring + '\n');
                }
             bsmf.MainFrame.show(getMessageTag(1126));
            } catch (SQLException s) {
                MainFrame.bslog(s);
               bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
        output.close();
        } catch (IOException ex) {
            Logger.getLogger(TrainingRpt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btcsvActionPerformed

    private void ddempidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddempidActionPerformed
      try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               
                int i = 0;

                res = st.executeQuery("SELECT * FROM  emp_mstr where " + 
                             " emp_nbr = " + "'" + ddempid.getSelectedItem().toString() + "'" +
                              ";"
                              );

                while (res.next()) {
                    i++;
                    username.setText(res.getString("emp_lname") + ", " + res.getString("emp_fname"));
                }

                if (ddempid.getSelectedItem().toString().equals("ALL")) {
                    username.setText("");
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
    }//GEN-LAST:event_ddempidActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
              if (! checkperms("TrainingMstr")) { return; }
                reinitpanels("TrainingMstr", true, new String[]{tablereport.getValueAt(row, col).toString()});
           
        }
    }//GEN-LAST:event_tablereportMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcsv;
    private javax.swing.JButton btview;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox ddempid;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablereport;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}
