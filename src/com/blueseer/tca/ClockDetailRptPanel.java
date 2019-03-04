/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.tca;

import com.blueseer.utl.OVData;
import java.awt.FileDialog;
import java.awt.Frame;
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
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
public class ClockDetailRptPanel extends javax.swing.JPanel {
 
    /**
     * Creates new form ScrapReportPanel
     */
    public ClockDetailRptPanel() {
        initComponents();
    }

    public void initvars(String arg) {
         java.util.Date now = new java.util.Date();
         Calendar calfrom = Calendar.getInstance();
         calfrom.add(Calendar.DATE, -7);
         dcFrom.setDate(calfrom.getTime());
         
         dcTo.setDate(now);
         
        ddempfrom.removeAllItems();
        ddempto.removeAllItems();
        ArrayList myemp = OVData.getempmstrlist();
        for (int i = 0; i < myemp.size(); i++) {
            ddempfrom.addItem(myemp.get(i));
            ddempto.addItem(myemp.get(i));
        }
         
         labelcount.setText(""); 
         labelhours.setText("");
          
          
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
        jLabel2 = new javax.swing.JLabel();
        dcFrom = new com.toedter.calendar.JDateChooser();
        dcTo = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablerecs = new javax.swing.JTable();
        btexport = new javax.swing.JButton();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labelhours = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ddempfrom = new javax.swing.JComboBox();
        ddempto = new javax.swing.JComboBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jLabel2.setText("From Date");

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("To Date");

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Employee");

        jLabel4.setText("To Employee");

        tablerecs.setAutoCreateRowSorter(true);
        tablerecs.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablerecs);

        btexport.setText("Export");
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        labelcount.setText("0");

        jLabel7.setText("Count");

        labelhours.setText("0");

        jLabel8.setText("Hours");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dcTo, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(dcFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ddempfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddempto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(225, 225, 225)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btexport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelhours, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btexport))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(ddempfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(ddempto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(labelhours, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
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

    
try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                   
               javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object [][] {

            },
            new String [] {
            "RecID", "EmpID", "LastName", "FirstName", "Dept", "Code", "InDate", "InTime", "InTmAdj", "OutDate", "OutTime", "OutTmAdj", "tothrs"
            });
                tablerecs.setModel(mymodel);
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
               
                 int i = 0;
                 double hours = 0.0;
                 
           res = st.executeQuery("SELECT t.tothrs as 't.tothrs', t.recid as 't.recid', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', " +
                           " e.emp_dept as 'e.emp_dept', t.code_id as 't.code_id', t.indate as 't.indate', t.intime as 't.intime', " +
                           " t.intime_adj as 't.intime_adj', t.outdate as 't.outdate', t.outtime as 't.outtime', " +
                           " t.outtime_adj as 't.outtime_adj' FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr" +
                              " where t.emp_nbr >= " + "'" + ddempfrom.getSelectedItem().toString() + "'" +
                              "and t.emp_nbr <= " + "'" + ddempto.getSelectedItem().toString() + "'" +
                              "and t.indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                               "and t.indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                               "and e.emp_termdate = '' order by e.emp_nbr, t.indate" +
                               ";" );
           

        while (res.next()) {
            i++;
           
            
            hours = hours + res.getDouble("t.tothrs");
            
            mymodel.addRow(new Object []{res.getString("t.recid"),
                                            res.getString("t.emp_nbr"),
                                            res.getString("e.emp_lname"),
                                            res.getString("e.emp_fname"),
                                            res.getString("e.emp_dept"),
                                            res.getString("t.code_id"),
                                             res.getString("t.indate"),
                                            res.getString("t.intime"),
                                            res.getString("t.intime_adj"),
                                            res.getString("t.outdate"),
                                            res.getString("t.outtime"),
                                            res.getString("t.outtime_adj"),
                                            res.getString("t.tothrs")

                                            } );
            
       
        }
                 
         labelcount.setText(String.valueOf(i));
         labelhours.setText(String.valueOf(hours));
                
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Cannot execute sql query for TimeClock Report");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
       FileDialog fDialog;
        fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        //fDialog.setFile("data.csv");
        String path = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(path);
        BufferedWriter output;
        
         int i = 0;
                String frompart = "";
                String topart = "";
                String fromcode = "";
                String tocode = "";
                
            
                   
           DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            output = new BufferedWriter(new FileWriter(f));
      
             
                
            String myheader = "RecID, EmpID, LastName, FirstName, InDate, InTime, InTmAdj, OutDate, OutTime, OutTmAdj, tothrs";
           
                 output.write(myheader + '\n');
                 
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

              
 res = st.executeQuery("SELECT t.tothrs as 't.tothrs', t.recid as 't.recid', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', " +
                           " e.emp_dept as 'e.emp_dept', t.code_id as 't.code_id', t.indate as 't.indate', t.intime as 't.intime', " +
                           " t.intime_adj as 't.intime_adj', t.outdate as 't.outdate', t.outtime as 't.outtime', " +
                           " t.outtime_adj as 't.outtime_adj' FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr" +
                              " where t.emp_nbr >= " + "'" + ddempfrom.getSelectedItem().toString() + "'" +
                              "and t.emp_nbr <= " + "'" + ddempto.getSelectedItem().toString() + "'" +
                              "and t.indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                               "and t.indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                               "and e.emp_termdate is " + null + " order by e.emp_nbr, t.indate" +
                               ";" );

                while (res.next()) {
                    String newstring = res.getString("t.recid") + "," + res.getString("t.emp_nbr").replace(",","") + "," + 
                            res.getString("e.emp_lname") + "," + res.getString("e.emp_fname") + "," + res.getString("t.indate") + "," + 
                            res.getString("t.intime") + "," + res.getString("t.intime_adu") + "," + 
                            res.getString("t.outdate") + "," + res.getString("t.outtime") + "," +
                            res.getString("t.outtime_adj") + "," + res.getString("t.tothrs");
                            
                    output.write(newstring + '\n');
                }
             bsmf.MainFrame.show("file has been created");
            } catch (SQLException s) {
                bsmf.MainFrame.show("Cannot extract timeclock data");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        output.close();
        
        
        
        } catch (IOException ex) {
            Logger.getLogger(bsmf.MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btexportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btexport;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox ddempfrom;
    private javax.swing.JComboBox ddempto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labelhours;
    private javax.swing.JTable tablerecs;
    // End of variables declaration//GEN-END:variables
}
