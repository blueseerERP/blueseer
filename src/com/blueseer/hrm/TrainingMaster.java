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
package com.blueseer.hrm;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;

/**
 *
 * @author vaughnte
 */
public class TrainingMaster extends javax.swing.JPanel {

    
    String trid = "";
    /**
     * Creates new form UserMaintPanel
     */
     javax.swing.table.DefaultTableModel empmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                   "EmpID", "LastName", "FirstName"
                   });
    
    
  
     
           class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            //Integer percentage = (Integer) table.getValueAt(row, 3);
            //if (percentage > 30)
           // if (table.getValueAt(row, 0).toString().compareTo("1923") == 0)   
            if (column == 0)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
    
    public TrainingMaster() {
        initComponents();
        

    }

    public void initvars(String[] arg) {
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        startdate.setDate(now);
        enddate.setDate(now);
        
        btadd.setEnabled(true);
        ddempid.setEnabled(true);
        emptable.setEnabled(true);
        
        location.setText("");
        instructor.setText("");
        trainingdesc.setText("");
        comments.setText("");
        tbhours.setText("");
        coursenum.setText("");
               
        empmodel.setRowCount(0);
         emptable.setModel(empmodel);
         
        ArrayList<String> mylist = new ArrayList();
        ddempid.removeAllItems();
        mylist = OVData.getempmstrlist();
        for (String emp : mylist) {
            ddempid.addItem(emp);
        }
        
          if (arg != null && arg.length > 0) {
        getemptraininfo(arg[0]);
        }
    }  
    
    public void getemptraininfo(String myid) {
            try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                    res = st.executeQuery("SELECT empid, emp_lname, emp_fname, coursenum, instructor, startdate, enddate, location, title, hours, comments " +
                            " from emp_train inner join emp_mstr on empid = emp_nbr where emptrid = " + "'" + 
                            myid.toString() + "'" + ";");
                    while (res.next()) {
                      ddempid.setSelectedItem(res.getString("empid"));
                      trainingdesc.setText(res.getString("title"));
                      instructor.setText(res.getString("instructor"));
                      coursenum.setText(res.getString("coursenum"));
                      location.setText(res.getString("location"));
                      tbhours.setText(res.getString("hours"));
                      comments.setText(res.getString("comments"));
                      startdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("startdate")));
                      enddate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("enddate")));
                      trid = myid;
                      empmodel.addRow(new Object[]{res.getInt("empid"), res.getString("emp_lname"), res.getString("emp_fname")}); 
                    }
                    if (i > 0) {
                    
                    emptable.setEnabled(false);
                    btadd.setEnabled(false);
                    ddempid.setEnabled(false);
                    }
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve emp_train");
            }
            bsmf.MainFrame.con.close();
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

        jPanel1 = new javax.swing.JPanel();
        btadd = new javax.swing.JButton();
        trainingdesc = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        instructor = new javax.swing.JTextField();
        coursenum = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        taUMperms1 = new javax.swing.JTextArea();
        tbhours = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        startdate = new com.toedter.calendar.JDateChooser();
        enddate = new com.toedter.calendar.JDateChooser();
        ddempid = new javax.swing.JComboBox();
        jLabel52 = new javax.swing.JLabel();
        btaddempid = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        emptable = new javax.swing.JTable();
        location = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        comments = new javax.swing.JTextArea();
        jLabel54 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Training Maintenance"));

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel46.setText("Training Description");

        jLabel47.setText("Instructor");

        jLabel49.setText("CourseNum (optional)");

        jLabel50.setText("StartDate");

        jLabel51.setText("EndDate");

        taUMperms1.setColumns(20);
        taUMperms1.setRows(5);
        jScrollPane7.setViewportView(taUMperms1);

        jLabel2.setText("Hours");

        startdate.setDateFormatString("yyyy-MM-dd");

        enddate.setDateFormatString("yyyy-MM-dd");

        jLabel52.setText("EmpID");

        btaddempid.setText("Add");
        btaddempid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddempidActionPerformed(evt);
            }
        });

        emptable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "EmpID", "LastName", "FirstName"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(emptable);

        jLabel53.setText("Location");

        comments.setColumns(20);
        comments.setRows(5);
        jScrollPane2.setViewportView(comments);

        jLabel54.setText("Comments");

        jButton1.setText("Delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel50)
                            .addComponent(jLabel51)
                            .addComponent(jLabel52)
                            .addComponent(jLabel54))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(startdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(ddempid, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(38, 38, 38))
                                    .addComponent(enddate, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btaddempid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(224, 224, 224))
                            .addComponent(jScrollPane2))))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel47)
                    .addComponent(jLabel46)
                    .addComponent(jLabel49)
                    .addComponent(jLabel2)
                    .addComponent(jLabel53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(location, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(instructor, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coursenum, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbhours, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trainingdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 76, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trainingdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instructor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coursenum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tbhours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel50)
                    .addComponent(startdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(enddate, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddempid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52)
                    .addComponent(btaddempid, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
      DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                
                 Pattern p = Pattern.compile("\\d\\.\\d\\d");
                 Matcher m = p.matcher(tbhours.getText());
                 if (!m.find() || tbhours.getText() == null) {
                 bsmf.MainFrame.show("Invalid hours need x.xx format");
                 proceed = false;
                 }
                
                 if (trainingdesc.getText().isEmpty()) {
                     bsmf.MainFrame.show("Training Desc cannot be blank");
                 proceed = false;
                 }
                 
                 if (instructor.getText().isEmpty()) {
                     bsmf.MainFrame.show("Instructor cannot be blank");
                 proceed = false;
                 }
                 
                 if (tbhours.getText().isEmpty()) {
                     bsmf.MainFrame.show("Hours cannot be blank...must be format x.xx");
                 proceed = false;
                 }
                 
                 if (location.getText().isEmpty()) {
                     bsmf.MainFrame.show("Instructor cannot be blank");
                 proceed = false;
                 }
                 
                
                if (proceed) {
                    
                    for (i = 0; i < emptable.getRowCount(); i++) {
                    
                    st.executeUpdate("insert into emp_train "
                        + "(empid, coursenum, instructor,"
                        + "startdate, enddate, location, title, hours, comments) "
                        + "values ( " + "'" + emptable.getValueAt(i, 0) + "'" + ","
                        + "'" + coursenum.getText().toString() + "'" + ","
                        + "'" + instructor.getText().toString().replace("'", "") + "'" + ","
                        + "'" + dfdate.format(startdate.getDate()) + "'" + ","
                        + "'" + dfdate.format(enddate.getDate()) + "'" + ","
                        + "'" + location.getText().toString().replace("'", "") + "'" + ","
                        + "'" + trainingdesc.getText().toString().replace("'", "") + "'" + ","
                        + "'" + tbhours.getText().toString().replace("'", "") + "'" + ","
                        + "'" + comments.getText().toString().replace("'", "") + "'"
                        + ")"
                        + ";");
                    }

                    initvars(null);
                    bsmf.MainFrame.show("Added Training Records");
                    
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot Add Training Records");
            }
            bsmf.MainFrame.con.close();
            
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btaddempidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddempidActionPerformed
          boolean canproceed = true;
        emptable.setModel(empmodel);
         
        if (canproceed) {
            
            try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;


                if (proceed) {
                    res = st.executeQuery("SELECT emp_nbr, emp_lname, emp_fname FROM  emp_mstr where emp_nbr = " + "'" + 
                            ddempid.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                      empmodel.addRow(new Object[]{res.getInt("emp_nbr"), res.getString("emp_lname"), res.getString("emp_fname")}); 
                    }
                }
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add User To Table");
            }
            bsmf.MainFrame.con.close();
           } catch (Exception e) {
            MainFrame.bslog(e);
        }
            
             
            
            
               
        }
        
    }//GEN-LAST:event_btaddempidActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int[] rows = emptable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) emptable.getModel()).removeRow(i);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            if (! trid.isEmpty())
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
               
                    st.executeUpdate("delete FROM  emp_train where emptrid = " + "'" + 
                            trid.toString() + "'" + ";");
             bsmf.MainFrame.show("Deleted Record");    
             initvars(null);
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to delete emp_train record");
            }
            bsmf.MainFrame.con.close();
           } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
       try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                
                 Pattern p = Pattern.compile("\\d\\.\\d\\d");
                 Matcher m = p.matcher(tbhours.getText());
                 if (!m.find() || tbhours.getText() == null) {
                 bsmf.MainFrame.show("Invalid hours need x.xx format");
                 proceed = false;
                 }
                
                 if (trid.isEmpty()) {
                     bsmf.MainFrame.show("Cannot Update..no Record to Update");
                 proceed = false;
                 }
                 
                 if (trainingdesc.getText().isEmpty()) {
                     bsmf.MainFrame.show("Training Desc cannot be blank");
                 proceed = false;
                 }
                 
                 if (instructor.getText().isEmpty()) {
                     bsmf.MainFrame.show("Instructor cannot be blank");
                 proceed = false;
                 }
                 
                 if (tbhours.getText().isEmpty()) {
                     bsmf.MainFrame.show("Hours cannot be blank...must be format x.xx");
                 proceed = false;
                 }
                 
                 if (location.getText().isEmpty()) {
                     bsmf.MainFrame.show("Instructor cannot be blank");
                 proceed = false;
                 }
                 
                
                if (proceed) {
                    
                    for (i = 0; i < emptable.getRowCount(); i++) {
                    
                    st.executeUpdate("update emp_train "
                        + " set coursenum = " +  "'" + coursenum.getText().toString() + "'" + ","
                        + " instructor = " + "'" + instructor.getText().toString().replace("'", "") + "'" + ","
                        + "startdate = " +  "'" + dfdate.format(startdate.getDate()) + "'" + ","
                        + "enddate = " +  "'" + dfdate.format(enddate.getDate()) + "'" + ","
                        + "location = " + "'" + location.getText().toString().replace("'", "") + "'" + ","
                        + "title = " + "'" + trainingdesc.getText().toString().replace("'", "") + "'" + ","
                        + "hours = " + "'" + tbhours.getText().toString().replace("'", "") + "'" + ","
                        + "comments = " + "'" + comments.getText().toString().replace("'", "") + "'"
                        + " where emptrid = " + "'" + trid + "'"
                        + ";");
                    }

                    initvars(null);
                    bsmf.MainFrame.show("Updated Training Record");
                    
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot Update Training Records");
            }
            bsmf.MainFrame.con.close();
            
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddempid;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JTextArea comments;
    private javax.swing.JTextField coursenum;
    private javax.swing.JComboBox ddempid;
    private javax.swing.JTable emptable;
    private com.toedter.calendar.JDateChooser enddate;
    private javax.swing.JTextField instructor;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextField location;
    private com.toedter.calendar.JDateChooser startdate;
    private javax.swing.JTextArea taUMperms1;
    private javax.swing.JTextField tbhours;
    private javax.swing.JTextField trainingdesc;
    // End of variables declaration//GEN-END:variables
}
