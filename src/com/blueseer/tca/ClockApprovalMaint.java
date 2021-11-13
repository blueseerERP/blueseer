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

package com.blueseer.tca;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author vaughnte
 */
public class ClockApprovalMaint extends javax.swing.JPanel {

    /**
     * Creates new form ClockUpdatePanel
     */
    public ClockApprovalMaint() {
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
    
    
    public void getClockRecord(String rec) {
        try{

         Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
        

        int i = 0;

        res = st.executeQuery("SELECT * FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where recid = " + "'" + rec + "'" +
                              ";" );
        while (res.next()) {
            i++;
            if (res.getString("t.code_id").equals("01") ) {
                bsmf.MainFrame.show(getMessageTag(1159));
            } else {
            tbrecid.setText(res.getString("t.recid"));
            lblemployee.setText(res.getString("t.emp_nbr") + "  " + res.getString("e.emp_fname") + 
                               "  " + res.getString("e.emp_lname"));
            dcindate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("t.indate")));
            dcoutdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("t.outdate")));
            tbintime.setText(res.getString("t.intime"));
            tbintimeadj.setText(res.getString("t.intime_adj"));
            tbouttime.setText(res.getString("t.outtime"));
            tbouttimeadj.setText(res.getString("t.outtime_adj"));
            tbtothrs.setText(res.getString("t.tothrs"));
            tacomment.setText(res.getString("t.comment"));
            ddcode.setSelectedItem(res.getString("t.code_id"));
            }
        }

        if (i == 0) {
            bsmf.MainFrame.show(getMessageTag(1001));
        }

        }
      catch (SQLException s){
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
    }
    catch (Exception e){
      MainFrame.bslog(e);
    }
    }
    
    public String getclocktime(int hour, int minutes) {
   
       String minutestring = "";

       if (minutes >= 0 && minutes <= 7) { minutestring = "00";    }
            if (minutes > 7 && minutes <= 15) { minutestring = "15";    }
            if (minutes > 15 && minutes <= 22) {  minutestring = "15";    }
            if (minutes > 22 && minutes <= 30) {  minutestring = "30";    }
            if (minutes > 30 && minutes <= 37) { minutestring = "30";    }
            if (minutes > 37 && minutes <= 45) { minutestring = "45";    }
            if (minutes > 45 && minutes <= 52) { minutestring = "45";    }
             if (minutes > 52 && minutes <= 60) { minutestring = "00"; hour = hour + 1;   }


           return String.format("%02d", hour).toString() + ":" + minutestring;


   }
    
    
    
    public void initvars(String[] arg) {
        java.util.Date now = new java.util.Date();
                 
        tbrecid.setText("");
        dcindate.setDate(now);
        dcoutdate.setDate(now);
        tbintime.setText("");
        tbintimeadj.setText("");
        tbouttime.setText("");
        tbouttimeadj.setText("");
        tbtothrs.setText("");
        
        ddcode.removeAllItems();
        ArrayList<String> codes = OVData.getClockCodes();
        for (String code : codes) {
            ddcode.addItem(code);
        }
        
         if (arg != null && arg.length > 0) {
            getClockRecord(arg[0]);
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
        btget = new javax.swing.JButton();
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
        ddcode = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tacomment = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        btcalc = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Supervisor Approval / Review"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel1.setText("RecordID");
        jLabel1.setName("lblid"); // NOI18N

        btget.setText("Get");
        btget.setName("btget"); // NOI18N
        btget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btgetActionPerformed(evt);
            }
        });

        tbtothrs.setEditable(false);

        jLabel2.setText("InDate");
        jLabel2.setName("lblindate"); // NOI18N

        jLabel4.setText("Adj InTime");
        jLabel4.setName("lblintimeadj"); // NOI18N

        jLabel3.setText("InTime");
        jLabel3.setName("lblintime"); // NOI18N

        jLabel7.setText("Adj OutTime");
        jLabel7.setName("lblouttimeadj"); // NOI18N

        jLabel8.setText("Total Hours");
        jLabel8.setName("lbltothours"); // NOI18N

        jLabel6.setText("OutTime");
        jLabel6.setName("lblouttime"); // NOI18N

        tbintime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbintimeFocusLost(evt);
            }
        });

        dcindate.setDateFormatString("yyyy-MM-dd");

        tbouttimeadj.setEditable(false);

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        tbintimeadj.setEditable(false);

        jLabel5.setText("OutDate");
        jLabel5.setName("lbloutdate"); // NOI18N

        dcoutdate.setDateFormatString("yyyy-MM-dd");

        tbouttime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbouttimeFocusLost(evt);
            }
        });

        jLabel9.setText("Code");
        jLabel9.setName("lblcode"); // NOI18N

        tacomment.setColumns(20);
        tacomment.setRows(5);
        jScrollPane1.setViewportView(tacomment);

        jLabel10.setText("Comments");
        jLabel10.setName("lblcomments"); // NOI18N

        btcalc.setText("calcHours");
        btcalc.setName("btcalculate"); // NOI18N
        btcalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcalcActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbintime, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbouttime, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbintimeadj, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbouttimeadj, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbtothrs, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btcalc))
                                    .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(dcindate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                        .addComponent(dcoutdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblemployee, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbrecid, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btget)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btupdate)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btget)
                    .addComponent(tbrecid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(2, 2, 2)
                .addComponent(lblemployee, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcindate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbintime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbintimeadj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(11, 11, 11)
                        .addComponent(dcoutdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbouttime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbouttimeadj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtothrs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(btcalc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btgetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btgetActionPerformed
      getClockRecord(tbrecid.getText());
    }//GEN-LAST:event_btgetActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed

        java.util.Date now = new java.util.Date();
            DateFormat cdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
            String clockdate = cdate.format(now);
        
        
        
        try{

        Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
       
        int i = 0;
        boolean proceed = true;

            // Pattern match the times
          Pattern p = Pattern.compile("\\d\\d\\:\\d\\d\\:\\d\\d");
           
           Matcher m = p.matcher(tbintime.getText().toString());
           if (! m.find()) {
               bsmf.MainFrame.show(getMessageTag(1160));
               tbintime.requestFocus();
               return;
           }
            
            
                 m = p.matcher(tbouttime.getText().toString());
           if (! m.find() ) {
               bsmf.MainFrame.show(getMessageTag(1160));
                tbouttime.requestFocus();
               return;
           }
           
            p = Pattern.compile("\\d.\\d\\d");
            m = p.matcher(tbtothrs.getText().toString());
           if (! m.find()) {
               bsmf.MainFrame.show(getMessageTag(1161));
                tbtothrs.requestFocus();
               return;
           } 
           
           String comments = tacomment.getText();
           
           if (comments.length() >= 200)
           comments = comments.substring(0, 200);

         if (proceed) {
        st.executeUpdate("update time_clock t set " +
                              "t.indate = " + "'" + dfdate.format(dcindate.getDate()) + "'" + "," +
                              "t.outdate = " + "'" +  dfdate.format(dcindate.getDate()) + "'"  + "," +
                              "t.intime = " + "'" +  tbintime.getText() + "'"  + "," +
                              "t.outtime = " + "'" +  tbouttime.getText() + "'"  + "," +
                              "t.intime_adj = " + "'" +  tbintimeadj.getText() + "'"  + "," +
                              "t.outtime_adj = " + "'" +  tbouttimeadj.getText() + "'"  + "," +
                              "t.tothrs = " + "'" + tbtothrs.getText() + "'" + "," +
                              "t.comment = " + "'" + comments + "'" + "," +
                              "t.who_changed = " + "'" + bsmf.MainFrame.userid + "'" + "," +
                              "t.code_id = " + "'" + ddcode.getSelectedItem().toString() + "'" + "," +
                              "t.changed = " + "'" + clockdate + "'" +
                              " where t.recid = " + "'" + tbrecid.getText().toString() + "'" +
                              ";" );

        bsmf.MainFrame.show(getMessageTag(1008));
            } // if proceed
        }
      catch (SQLException s){
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
    }
    catch (Exception e){
      MainFrame.bslog(e);
    }
    }//GEN-LAST:event_btupdateActionPerformed

    private void tbintimeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbintimeFocusLost
        boolean proceed = true;
        Pattern p = Pattern.compile("\\d\\d\\:\\d\\d\\:\\d\\d");
           
           Matcher m = p.matcher(tbintime.getText().toString());
           if (! m.find()) {
               bsmf.MainFrame.show(getMessageTag(1160));
               tbintime.requestFocus();
               return;
           }
        
        if (proceed) {
        String realtime = tbintime.getText();
        String clocktime = "";
        int minutes = Integer.valueOf(realtime.substring(3,5));
        int hours = Integer.valueOf(realtime.substring(0,2));
        
        clocktime = getclocktime(hours, minutes);
        
        tbintimeadj.setText(clocktime + ":00");
        }
    }//GEN-LAST:event_tbintimeFocusLost

    private void tbouttimeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbouttimeFocusLost
        boolean proceed = true;
        Pattern p = Pattern.compile("\\d\\d\\:\\d\\d\\:\\d\\d");
           
           Matcher m = p.matcher(tbouttime.getText().toString());
           if (! m.find()) {
               bsmf.MainFrame.show(getMessageTag(1160));
               tbouttime.requestFocus();
               return;
           }
        
        if (proceed) {
        String realtime = tbouttime.getText();
        String clocktime = "";
        int minutes = Integer.valueOf(realtime.substring(3,5));
        int hours = Integer.valueOf(realtime.substring(0,2));
        
        clocktime = getclocktime(hours, minutes);
        
        tbouttimeadj.setText(clocktime + ":00");
        }
    }//GEN-LAST:event_tbouttimeFocusLost

    private void btcalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcalcActionPerformed
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        double hours = OVData.calcClockHours(dfdate.format(dcindate.getDate()),  tbintimeadj.getText(), dfdate.format(dcoutdate.getDate()), tbouttimeadj.getText() );
        tbtothrs.setText(currformatDouble(hours));
    }//GEN-LAST:event_btcalcActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcalc;
    private javax.swing.JButton btget;
    private javax.swing.JButton btupdate;
    private com.toedter.calendar.JDateChooser dcindate;
    private com.toedter.calendar.JDateChooser dcoutdate;
    private javax.swing.JComboBox ddcode;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblemployee;
    private javax.swing.JTextArea tacomment;
    private javax.swing.JTextField tbintime;
    private javax.swing.JTextField tbintimeadj;
    private javax.swing.JTextField tbouttime;
    private javax.swing.JTextField tbouttimeadj;
    private javax.swing.JTextField tbrecid;
    private javax.swing.JTextField tbtothrs;
    // End of variables declaration//GEN-END:variables
}
