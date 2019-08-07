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

package com.blueseer.tca;

import bsmf.MainFrame;
import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author vaughnte
 */
public class ClockUpdatePanel extends javax.swing.JPanel {

    
    boolean isLoad = false;
    
    /**
     * Creates new form ClockUpdatePanel
     */
    public ClockUpdatePanel() {
        initComponents();
    }

    
    
    public void enableAll() {
        
    }
    
    public void disableAll() {
        
    }
    
    public void clearAll() {
         java.util.Date now = new java.util.Date();
        tbrecid.setText("");
        tbrecid.setEditable(true);
        tbrecid.setForeground(Color.black);
        dcindate.setDate(now);
        dcoutdate.setDate(now);
        tbintime.setText("");
        tbintimeadj.setText("");
        tbouttime.setText("");
        tbouttimeadj.setText("");
        tbtothrs.setText("0.00");
        
        tbcode.setText("");
         tbnewintime.setText("");
         tbnewouttime.setText("");
         tbnewtothours.setText("");
        tbnewintime.setEditable(false);
        tbnewouttime.setEditable(false);
        tbnewtothours.setEditable(false);
        tbcode.setEditable(false);
        tbintime.setEditable(false);
        tbintimeadj.setEditable(false);
        tbouttime.setEditable(false);
        tbouttimeadj.setEditable(false);
        tbtothrs.setEditable(false);
        
        
        isLoad = true;
        ddInTimeHr.removeAllItems();
        ddOutTimeHr.removeAllItems();
        for (int i = 0; i < 24 ; i++) {
            ddInTimeHr.addItem(String.format("%02d", i));
            ddOutTimeHr.addItem(String.format("%02d", i));
        }
        for (int i = 0; i < 60 ; i++) {
            ddInTimeMin.addItem(String.format("%02d", i));
            ddOutTimeMin.addItem(String.format("%02d", i));
        }
        isLoad = false;
        
    }
    
    public boolean getTimeClockRecord(String recid) {
        boolean hasRec = false;
        
        try{
         Class.forName(bsmf.MainFrame.driver).newInstance();
         bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url+bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
        try{
         Statement st = bsmf.MainFrame.con.createStatement();
        ResultSet res = null;

        int i = 0;

        res = st.executeQuery("SELECT code_id, t.emp_nbr as t_emp_nbr, emp_fname, emp_lname, indate, outdate, intime, intime_adj, outtime, outtime_adj, tothrs FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where recid = " + "'" + recid + "'" +
                              ";" );
        while (res.next()) {
            i++;
            hasRec = true;
            
            if (res.getString("code_id").equals("01") ) {
                bsmf.MainFrame.show("Must close record first...must clock out");
            } else {
            lblemployee.setText(res.getString("t_emp_nbr") + "  " + res.getString("emp_fname") + 
                               "  " + res.getString("emp_lname"));
            dcindate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("indate")));
            dcoutdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("outdate")));
            tbintime.setText(res.getString("intime"));
            tbintimeadj.setText(res.getString("intime_adj"));
            tbouttime.setText(res.getString("outtime"));
            tbouttimeadj.setText(res.getString("outtime_adj"));
            tbtothrs.setText(res.getString("tothrs"));
            tbcode.setText(res.getString("code_id"));
            }
        }

        if (hasRec) {
            tbrecid.setText(recid);
            tbrecid.setEditable(false);
            tbrecid.setForeground(Color.blue);
        }

        }
      catch (SQLException s){
          MainFrame.bslog(s);
       bsmf.MainFrame.show("Cannot Retrieve View on RecID.");
      }
      bsmf.MainFrame.con.close();
    }
    catch (Exception e){
      MainFrame.bslog(e);
    }
        return hasRec;
    }
    
    public double calcTotHours(String start, String stop) {
        
        double tothours = 0.00;
        Calendar clockstart = Calendar.getInstance();
        Calendar clockstop = Calendar.getInstance();
        clockstart.set( Integer.valueOf(start.substring(0,4)), Integer.valueOf(start.substring(5,7)), Integer.valueOf(start.substring(8,10)), Integer.valueOf(start.substring(10,12)), Integer.valueOf(start.substring(13,15)) );
        clockstop.set( Integer.valueOf(stop.substring(0,4)), Integer.valueOf(stop.substring(5,7)), Integer.valueOf(stop.substring(8,10)), Integer.valueOf(stop.substring(10,12)), Integer.valueOf(stop.substring(13,15)) );
       
                            long milis1 = clockstart.getTimeInMillis();
                            long milis2 = clockstop.getTimeInMillis();
                            long diff = milis2 - milis1;
                            long diffHours = diff / (60 * 60 * 1000);
                            long diffMinutes = diff / (60 * 1000);
                            long diffDays = diff / (24 * 60 * 60 * 1000);
                            long diffSeconds = diff / 1000;
                            if (diffMinutes > 0) {
                                tothours = (diffMinutes / (double) 60) ;
                            }
        return tothours;
    }
    
    public void adjustTime() {
       
         DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
       tbnewintime.setText(ddInTimeHr.getSelectedItem().toString() + ":" + ddInTimeMin.getSelectedItem().toString());
       tbnewouttime.setText(ddOutTimeHr.getSelectedItem().toString() + ":" + ddOutTimeMin.getSelectedItem().toString());
       String start = dfdate.format(dcindate.getDate()) + tbnewintime.getText();
       String stop = dfdate.format(dcoutdate.getDate()) + tbnewouttime.getText();
       tbnewtothours.setText(String.valueOf(calcTotHours(start, stop)));
    }
    
    
    public void initvars(String arg) {
          clearAll();
          disableAll();
          btbrowse.setEnabled(true);
        if (! arg.isEmpty()) {
            getTimeClockRecord(arg);
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
        jLabel1 = new javax.swing.JLabel();
        tbtothrs = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tbrecid = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbintime = new javax.swing.JTextField();
        dcindate = new com.toedter.calendar.JDateChooser();
        tbouttimeadj = new javax.swing.JTextField();
        btupdate = new javax.swing.JButton();
        tbintimeadj = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        dcoutdate = new com.toedter.calendar.JDateChooser();
        tbouttime = new javax.swing.JTextField();
        lblemployee = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        ddInTimeHr = new javax.swing.JComboBox<>();
        ddInTimeMin = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ddOutTimeHr = new javax.swing.JComboBox<>();
        ddOutTimeMin = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tbnewintime = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        tbnewouttime = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        tbnewtothours = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        tbcode = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Clock Record Maintenance"));

        jLabel1.setText("RecordID");

        tbtothrs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbtothrsFocusLost(evt);
            }
        });

        jLabel2.setText("InDate");

        jLabel4.setText("Adj InTime");

        tbrecid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbrecidActionPerformed(evt);
            }
        });

        jLabel3.setText("InTime");

        jLabel7.setText("Adj OutTime");

        jLabel8.setText("Total Hours");

        jLabel6.setText("OutTime");

        dcindate.setDateFormatString("yyyy-MM-dd");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel5.setText("OutDate");

        dcoutdate.setDateFormatString("yyyy-MM-dd");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        ddInTimeHr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddInTimeHrActionPerformed(evt);
            }
        });

        ddInTimeMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddInTimeMinActionPerformed(evt);
            }
        });

        jLabel9.setText("Hour");

        jLabel10.setText("Minute");

        ddOutTimeHr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddOutTimeHrActionPerformed(evt);
            }
        });

        ddOutTimeMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddOutTimeMinActionPerformed(evt);
            }
        });

        jLabel11.setText("Hour");

        jLabel12.setText("Minute");

        jLabel13.setText("NewInTime");

        jLabel14.setText("NewOutTime");

        jLabel15.setText("New Tot Hours");

        jLabel16.setText("ClockCode");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btupdate))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbrecid, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbouttime, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddOutTimeHr, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dcindate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dcoutdate, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                    .addComponent(tbintimeadj, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbouttimeadj, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbtothrs, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblemployee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tbintime, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(13, 13, 13)
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(ddInTimeHr, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddOutTimeMin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tbnewintime, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(ddInTimeMin, javax.swing.GroupLayout.Alignment.TRAILING, 0, 80, Short.MAX_VALUE))
                                    .addComponent(tbnewouttime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbnewtothours, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(47, 47, 47))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbrecid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                    .addComponent(btbrowse))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblemployee, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel16)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dcindate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbintime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(ddInTimeHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddInTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbintimeadj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(tbnewintime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGap(11, 11, 11)
                        .addComponent(dcoutdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbouttime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(ddOutTimeHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddOutTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbouttimeadj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(tbnewouttime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtothrs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(tbnewtothours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdate)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try{

         Class.forName(bsmf.MainFrame.driver).newInstance();
         bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url+bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
        try{
         Statement st = bsmf.MainFrame.con.createStatement();
        ResultSet res = null;

        int i = 0;
        boolean proceed = true;

            // Pattern match the times
            /*
          Pattern p = Pattern.compile("\\d\\d\\:\\d\\d\\:\\d\\d");
           
           Matcher m = p.matcher(tbintime.getText().toString());
           if (! m.find()) {
               bsmf.MainFrame.show("Invalid InTime..must be xx:xx:xx");
               proceed = false;
           }
            */
            
         
            
           
            java.util.Date now = new java.util.Date();
            DateFormat cdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            String clockdate = cdate.format(now);


         if (proceed) {
        st.executeUpdate("update time_clock set " +
                              "code_id = '77' " + "," +
                              "indate = " + "'" + dfdate.format(dcindate.getDate()) + "'" + "," +
                              "outdate = " + "'" +  dfdate.format(dcoutdate.getDate()) + "'"  + "," +
                              "intime_adj = " + "'" +  tbnewintime.getText() + "'"  + "," +
                              "outtime_adj = " + "'" +  tbnewouttime.getText() + "'"  + "," +
                              "tothrs = " + "'" + tbnewtothours.getText() + "'" + "," +
                              "changed = " + "'" + clockdate + "'" +
                              " where recid = " + "'" + tbrecid.getText().toString() + "'" +
                              ";" );
        
        bsmf.MainFrame.show("Updated RecID");
        initvars(tbrecid.getText());
            } // if proceed
        }
      catch (SQLException s){
          MainFrame.bslog(s);
        bsmf.MainFrame.show("SQL code does not execute.");
      }
      bsmf.MainFrame.con.close();
    }
    catch (Exception e){
      MainFrame.bslog(e);
    }
    }//GEN-LAST:event_btupdateActionPerformed

    private void tbtothrsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbtothrsFocusLost
           String x = BlueSeerUtils.bsformat("", tbtothrs.getText(), "2");
        if (x.equals("error")) {
            tbtothrs.setText("");
            tbtothrs.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbtothrs.requestFocus();
        } else {
            tbtothrs.setText(x);
            tbtothrs.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbtothrsFocusLost

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "clockrecupdate,recid");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void tbrecidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbrecidActionPerformed
        boolean gotIt = getTimeClockRecord(tbrecid.getText());
        if (gotIt) {
          tbrecid.setEditable(false);
          tbrecid.setForeground(Color.blue);
        } else {
           tbrecid.setForeground(Color.red); 
        }
    }//GEN-LAST:event_tbrecidActionPerformed

    private void ddInTimeHrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddInTimeHrActionPerformed
        if (! isLoad) {
            adjustTime();
        }
    }//GEN-LAST:event_ddInTimeHrActionPerformed

    private void ddInTimeMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddInTimeMinActionPerformed
         if (! isLoad) {
            adjustTime();
        }
    }//GEN-LAST:event_ddInTimeMinActionPerformed

    private void ddOutTimeHrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddOutTimeHrActionPerformed
         if (! isLoad) {
            adjustTime();
        }
    }//GEN-LAST:event_ddOutTimeHrActionPerformed

    private void ddOutTimeMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddOutTimeMinActionPerformed
         if (! isLoad) {
            adjustTime();
        }
    }//GEN-LAST:event_ddOutTimeMinActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btupdate;
    private com.toedter.calendar.JDateChooser dcindate;
    private com.toedter.calendar.JDateChooser dcoutdate;
    private javax.swing.JComboBox<String> ddInTimeHr;
    private javax.swing.JComboBox<String> ddInTimeMin;
    private javax.swing.JComboBox<String> ddOutTimeHr;
    private javax.swing.JComboBox<String> ddOutTimeMin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblemployee;
    private javax.swing.JTextField tbcode;
    private javax.swing.JTextField tbintime;
    private javax.swing.JTextField tbintimeadj;
    private javax.swing.JTextField tbnewintime;
    private javax.swing.JTextField tbnewouttime;
    private javax.swing.JTextField tbnewtothours;
    private javax.swing.JTextField tbouttime;
    private javax.swing.JTextField tbouttimeadj;
    private javax.swing.JTextField tbrecid;
    private javax.swing.JTextField tbtothrs;
    // End of variables declaration//GEN-END:variables
}
