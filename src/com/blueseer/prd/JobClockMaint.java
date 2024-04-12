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

package com.blueseer.prd;

import com.blueseer.tca.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.lurb2;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import com.blueseer.utl.DTData;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import javax.swing.JTable;

/**
 *
 * @author vaughnte
 */
public class JobClockMaint extends javax.swing.JPanel {

    
    boolean isLoad = false;
    
    /**
     * Creates new form ClockUpdatePanel
     */
    public JobClockMaint() {
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
        tbouttime.setText("");
        tbtothrs.setText("0.00");
        lblmessage.setText("");
        lblmessage.setForeground(Color.black);
        lblemployee.setText("");
        tbcode.setText("");
         tbnewintime.setText("");
         tbnewouttime.setText("");
         tbnewtothours.setText("");
        tbnewintime.setEditable(false);
        tbnewouttime.setEditable(false);
        tbnewtothours.setEditable(false);
        tbcode.setEditable(false);
        tbintime.setEditable(false);
        tbouttime.setEditable(false);
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
         Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

        int i = 0;

        res = st.executeQuery("SELECT jobc_code, t.jobc_empnbr as t_emp_nbr, emp_fname, emp_lname, jobc_indate, jobc_outdate, jobc_intime, jobc_outtime, jobc_tothrs FROM  job_clock t inner join emp_mstr e on e.emp_nbr = t.jobc_empnbr " +
                              " where jobc_id = " + "'" + recid + "'" +
                              ";" );
        while (res.next()) {
            i++;
            hasRec = true;
            
            if (res.getString("jobc_code").equals("01") ) {
                lblmessage.setText(getMessageTag(1159));
                lblmessage.setForeground(Color.red);
                btupdate.setEnabled(false);
                return false;
            } else {
            btupdate.setEnabled(true);
            lblmessage.setText("");
            lblmessage.setForeground(Color.black);
            lblemployee.setText(res.getString("t_emp_nbr") + "  " + res.getString("emp_fname") + 
                               "  " + res.getString("emp_lname"));
            dcindate.setDate(parseDate(res.getString("jobc_indate")));
            dcoutdate.setDate(parseDate(res.getString("jobc_outdate")));
            tbintime.setText(res.getString("jobc_intime"));
            tbouttime.setText(res.getString("jobc_outtime"));
            tbtothrs.setText(bsFormatDouble(res.getDouble("jobc_tothrs"),"2"));
            tbcode.setText(res.getString("jobc_code"));
            
            // preset adj times
            ddInTimeHr.setSelectedItem(res.getString("jobc_intime").substring(0,2));
            ddInTimeMin.setSelectedItem(res.getString("jobc_intime").substring(3,5));
            
            ddOutTimeHr.setSelectedItem(res.getString("jobc_outtime").substring(0,2));
            ddOutTimeMin.setSelectedItem(res.getString("jobc_outtime").substring(3,5));
            
            }
        }

        if (hasRec) {
            tbrecid.setText(recid);
            tbrecid.setEditable(false);
            tbrecid.setForeground(Color.blue);
            adjustTime();
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
       tbnewtothours.setText(bsFormatDouble(calcTotHours(start, stop), "2"));
    }
    
    
    public void initvars(String[] arg) {
          clearAll();
          disableAll();
          btbrowse.setEnabled(true);
         if (arg != null && arg.length > 0) {
            getTimeClockRecord(arg[0]);
        }
        
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getJobClockUtil(luinput.getText(),0, "jobc_empnbr"); 
        } else if (lurb2.isSelected()) {
         luModel = DTData.getJobClockUtil(luinput.getText(),0, "emp_lname");
        } else {
         luModel = DTData.getJobClockUtil(luinput.getText(),0, "jobc_indate");    
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
      
        callDialog(getClassLabelTag("lblempnbr", this.getClass().getSimpleName()), getClassLabelTag("lbllastname", this.getClass().getSimpleName()),
                getClassLabelTag("lblindate", this.getClass().getSimpleName()) ); 
        
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
        tbrecid = new javax.swing.JTextField();
        btupdate = new javax.swing.JButton();
        lblemployee = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        tbcode = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        lblmessage = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dcoutdate = new com.toedter.calendar.JDateChooser();
        tbintime = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbouttime = new javax.swing.JTextField();
        dcindate = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        tbtothrs = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        ddOutTimeHr = new javax.swing.JComboBox<>();
        tbnewintime = new javax.swing.JTextField();
        ddOutTimeMin = new javax.swing.JComboBox<>();
        tbnewouttime = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbnewtothours = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ddInTimeMin = new javax.swing.JComboBox<>();
        ddInTimeHr = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Clock Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel1.setText("Key");
        jLabel1.setName("lblid"); // NOI18N

        tbrecid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbrecidActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        jLabel16.setText("Clock Code");
        jLabel16.setName("lblcode"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Clock Values"));
        jPanel2.setName("currentpanel"); // NOI18N

        jLabel5.setText("Out Date");
        jLabel5.setName("lbloutdate"); // NOI18N

        jLabel2.setText("In Date");
        jLabel2.setName("lblindate"); // NOI18N

        dcoutdate.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("In Time");
        jLabel3.setName("lblintime"); // NOI18N

        jLabel6.setText("Out Time");
        jLabel6.setName("lblouttime"); // NOI18N

        dcindate.setDateFormatString("yyyy-MM-dd");

        jLabel8.setText("Total Hours");
        jLabel8.setName("lbltothours"); // NOI18N

        tbtothrs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbtothrsFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tbouttime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(dcindate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dcoutdate, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                        .addComponent(tbtothrs, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tbintime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcindate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbintime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcoutdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbouttime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtothrs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("New / Proposed Clock Values"));
        jPanel3.setName("newpanel"); // NOI18N

        jLabel13.setText("New In Time");
        jLabel13.setName("lblnewintime"); // NOI18N

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

        jLabel12.setText("Minute");
        jLabel12.setName("lblminute"); // NOI18N

        jLabel9.setText("Hour");
        jLabel9.setName("lblhour"); // NOI18N

        jLabel14.setText("New Out Time");
        jLabel14.setName("lblnewouttime"); // NOI18N

        jLabel10.setText("Minute");
        jLabel10.setName("lblminute"); // NOI18N

        ddInTimeMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddInTimeMinActionPerformed(evt);
            }
        });

        ddInTimeHr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddInTimeHrActionPerformed(evt);
            }
        });

        jLabel11.setText("Hour");
        jLabel11.setName("lblhour"); // NOI18N

        jLabel15.setText("New Tot Hours");
        jLabel15.setName("lblnewtothours"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbnewtothours, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddOutTimeHr, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddOutTimeMin, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbnewintime, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ddInTimeMin, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbnewouttime)
                            .addComponent(ddInTimeHr, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddInTimeHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddInTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbnewintime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddOutTimeHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddOutTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbnewouttime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbnewtothours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btupdate)
                .addGap(47, 47, 47))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbrecid, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btclear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(lblemployee, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbrecid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(btbrowse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btclear, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblemployee, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)))
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btupdate)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44))))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try{

         Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{

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
        st.executeUpdate("update job_clock set " +
                              "jobc_code = '77' " + "," +
                              "jobc_indate = " + "'" + dfdate.format(dcindate.getDate()) + "'" + "," +
                              "jobc_outdate = " + "'" +  dfdate.format(dcoutdate.getDate()) + "'"  + "," +
                              "jobc_intime = " + "'" +  tbnewintime.getText() + "'"  + "," +
                              "jobc_outtime = " + "'" +  tbnewouttime.getText() + "'"  + "," +        
                              "jobc_tothrs = " + "'" + tbnewtothours.getText() + "'" +
                              " where jobc_id = " + "'" + tbrecid.getText().toString() + "'" +
                              ";" );
        
        bsmf.MainFrame.show(getMessageTag(1008));
        initvars(new String[]{tbrecid.getText()});
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

    private void tbtothrsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbtothrsFocusLost
           String x = BlueSeerUtils.bsformat("", tbtothrs.getText(), "2");
        if (x.equals("error")) {
            tbtothrs.setText("");
            tbtothrs.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbtothrs.requestFocus();
        } else {
            tbtothrs.setText(x);
            tbtothrs.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbtothrsFocusLost

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        lookUpFrame();
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

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btclear;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblemployee;
    private javax.swing.JLabel lblmessage;
    private javax.swing.JTextField tbcode;
    private javax.swing.JTextField tbintime;
    private javax.swing.JTextField tbnewintime;
    private javax.swing.JTextField tbnewouttime;
    private javax.swing.JTextField tbnewtothours;
    private javax.swing.JTextField tbouttime;
    private javax.swing.JTextField tbrecid;
    private javax.swing.JTextField tbtothrs;
    // End of variables declaration//GEN-END:variables
}
