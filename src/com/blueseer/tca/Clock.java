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
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Component;
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
public class Clock extends javax.swing.JPanel {

    
    public double start = 0;
    public double end = 0;
    public double timediff = 0;
    public int count = 0;
   
   
    
     class AnswerWorker extends SwingWorker<Integer, Integer> {
        protected Integer doInBackground() throws Exception
        {
                // Do a time-consuming task.
                Thread.sleep(2000);
                return 1;
        }

        protected void done()
        {
                try
                {
                       // JOptionPane.showMessageDialog(f, get());
                        reinitcomponents();
                }
                catch (Exception e)
                {
                        MainFrame.bslog(e);
                }
        }
}
    /**
     * Creates new form ClockPanel
     */
    public Clock() {
        initComponents();
        setLanguageTags(this);
        tbclocknumber.addKeyListener(new KeyListener() {

    public void keyTyped(KeyEvent e) { 
       
        
    }

    public void keyReleased(KeyEvent e) { 
         if (e.getKeyCode() ==  KeyEvent.VK_0 ||
            e.getKeyCode() ==  KeyEvent.VK_1 ||
            e.getKeyCode() ==  KeyEvent.VK_2 ||
            e.getKeyCode() ==  KeyEvent.VK_3 ||
            e.getKeyCode() ==  KeyEvent.VK_4 ||
            e.getKeyCode() ==  KeyEvent.VK_5 ||
            e.getKeyCode() ==  KeyEvent.VK_6 ||
            e.getKeyCode() ==  KeyEvent.VK_7 ||
            e.getKeyCode() ==  KeyEvent.VK_8 ||
            e.getKeyCode() ==  KeyEvent.VK_9 ||
        e.getKeyCode() ==  KeyEvent.VK_NUMPAD0 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD1 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD2 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD3 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD4 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD5 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD6 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD7 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD8 ||
            e.getKeyCode() ==  KeyEvent.VK_NUMPAD9 
            
            
            ) {
        //JOptionPane.showMessageDialog(mydialog, "Must scan ID Card!");
       // reinitcomponents();
             count++;
             if (count == 1)
                 start = System.currentTimeMillis();
             if (count == 4)
                 end = System.currentTimeMillis();
             timediff = end - start;
      }
        
    }

    public void keyPressed(KeyEvent e) { 
    
    }
});
        
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
        count = 0;
    lbempid.setText(null);
    lbstatus.setText(null);
    lbstatus.setForeground(Color.black);
    lbimage.setIcon(null);
   //lbimage.setIcon(new ImageIcon(getClass().getResource("images/statusnogo.png")));
    lbname.setText(null);
    lbclockdatetime.setText(null);
    dddir.removeAllItems();
    dddir.addItem(getGlobalProgTag("clockin"));
    dddir.addItem(getGlobalProgTag("clockout"));
    tbclocknumber.setText(null);
    tbclocknumber.requestFocus(); 
    
    }
     
    private void reinitcomponents() {
    
    count = 0;
    lbempid.setText(null);
    lbstatus.setText(null);
    lbstatus.setForeground(Color.black);
    lbimage.setIcon(null);
    lbname.setText(null);
    lbclockdatetime.setText(null);

    tbclocknumber.setText(null);
    tbclocknumber.requestFocus();
    }
    
     public boolean ifClockedDir( String clockdir ) {
   
       boolean returnvalue = false;
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
                char[] input = tbclocknumber.getPassword();
                String myid = String.valueOf(input);
                String emp_clockin = null;
                int i = 0;

                res = st.executeQuery("SELECT * FROM  emp_mstr where emp_nbr = " + "'" + myid + "'" + ";" );
              //  System.out.println("Make: " + "\t" + "MakeName: ");

                while (res.next()) {
                    emp_clockin = res.getString("emp_clockin");
                    i++;
                }
                if (i > 0) {
                    if (emp_clockin.toString().equals("1") && clockdir.toString().equals(getGlobalProgTag("clockin"))) {
                        //  Already clocked in
                        failureAction(getMessageTag(1119));
                        returnvalue = true;
                    }
                    if (emp_clockin.equals("0") && clockdir.equals(getGlobalProgTag("clockout"))) {
                        //  Already clocked out
                        failureAction(getMessageTag(1118));
                        returnvalue = true;
                    }
                } 
            } catch (SQLException s){
              MainFrame.bslog(s);
              System.out.println(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
      return returnvalue;
   }
     
      public void failureAction(String myaction ) {
   
       //JOptionPane.showMessageDialog(mydialog, "Employee ID not in Database");
              lbimage.setIcon(new ImageIcon(getClass().getResource("/images/statusnogo.png")));
              lbstatus.setText(myaction);
              lbstatus.setForeground(Color.red);
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
     
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbimage = new javax.swing.JLabel();
        lbstatus = new javax.swing.JLabel();
        lbclockdatetime = new javax.swing.JLabel();
        lbempid = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        lbname = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dddir = new javax.swing.JComboBox();
        tbclocknumber = new javax.swing.JPasswordField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Employee Clock"));
        jPanel1.setName("panelmain"); // NOI18N

        lbimage.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbstatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbclockdatetime.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lbclockdatetime.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbempid.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lbempid.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setFocusable(false);
        jScrollPane1.setViewportView(jTextArea1);

        lbname.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lbname.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("ClockNumber");
        jLabel2.setName("lblclocknumber"); // NOI18N

        tbclocknumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbclocknumberActionPerformed(evt);
            }
        });

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setFocusable(false);
        jScrollPane2.setViewportView(jTextArea2);

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 204));
        jLabel1.setText("Input ClockNumber and Press Enter");
        jLabel1.setName("lbldirection"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 51, 204));
        jLabel3.setText("Choose Direction");
        jLabel3.setName("lblchoose"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(209, 209, 209)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbclocknumber, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(dddir, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbclockdatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(130, 130, 130)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbname, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbempid, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(48, 48, 48)
                        .addComponent(lbimage, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbclocknumber, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dddir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lbname, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lbempid, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(lbclockdatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(lbstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbimage, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void tbclocknumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbclocknumberActionPerformed
       

        // the logic below is used to determine if emp used keyboard or scan card
        // to clock in/out.  
        // If a scan card was used...keystrokes would be less than 100 millisecs
        // if a keyboard was used...keystrokes would be more than 100 millisecs
        //  choice of 100 was completely arbitrary.
        
        if (timediff > 100 && OVData.isClockScanCard()) {
            bsmf.MainFrame.show(getMessageTag(1120));
            reinitcomponents();
            return;
        }

        
        
        boolean isclocked = ifClockedDir(dddir.getSelectedItem().toString());
          // the check of login status is performed within ifClockedDir...
        // it will simply check the emp_clockin field within the emp_mstr table which
        // is set at time of clock in / out by ClockPanel.java .
        // if the clockin or clockout action is allowed...i.e. meaning they haven't already
        // clocked in or out accordingly previously...then the function will return false...thus
        // allowing the below logic to continue.
        if (! isclocked) {

             // ok...I must have clocked in or out correctly...continue
            try{
                Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            Statement st2 = con.createStatement();
            ResultSet res2 = null;
            try{
                    
                char[] input = tbclocknumber.getPassword();
                String myid = String.valueOf(input) ;
                res = st.executeQuery("SELECT * FROM  emp_mstr where emp_nbr = " + "'" + myid + "'" + ";" );
                
                int i = 0;
                String clockdate = "";
                String clocktime = "";
                String timeclock_adj = "";
                double nbrhours = 0;
                boolean proceed = false;

                while (res.next()) {
                    lbname.setText(res.getString("emp_lname") + "," + res.getString("emp_fname"));
                    lbempid.setText(res.getString("emp_nbr"));
                    java.util.Date now = new java.util.Date();
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                    clockdate = dfdate.format(now);
                    clocktime = dftime.format(now);

                    int minutes = Integer.valueOf(clocktime.substring(3,5));
                    int hours = Integer.valueOf(clocktime.substring(0,2));
                    double quarter = 0;
                    String minutestring = "";
                    String hourstring = "";

                    lbclockdatetime.setText(clockdate +  " ...  " + clocktime);

                    // get clocktime that will be charged
                    timeclock_adj = getclocktime(hours, minutes);
                    proceed = true;
                    i++;
                 }


                if (proceed) {
                    // clockin or clockout direction logic
                    if (dddir.getSelectedItem().toString().equals(getGlobalProgTag("clockin"))) {
                        lbstatus.setText("CLOCKED IN...")   ;
                        st2.executeUpdate("update emp_mstr set emp_clockin = '1' where emp_nbr = " + "'" + myid + "'" +";");

                        // insert time record
                        st2.executeUpdate("insert into time_clock (emp_nbr, indate, intime, intime_adj, dept, code_id, code_orig, login ) " +
                            "select emp_nbr, " +
                            "'" + clockdate + "'" + "," +
                            "'" + clocktime + "'" + "," +
                            "'" + timeclock_adj + "'" + "," +
                            "emp_dept, '01', '01', " +
                            "'" + bsmf.MainFrame.userid.toString() + "'" + " from emp_mstr where emp_nbr = " + "'" + myid + "'" + ";");

                    } else {

                        // OK...huge assumption here...that there is one and only one '01' timeclock record
                        // for any employee at any given time...this should be true based on action rules defined prior
                      // need to put logic here to redflag if more than one rec is found.


                      //now calc the hours put in by this employee based on clock in date and time
                        res2 = st2.executeQuery("SELECT * FROM  time_clock where code_id = '01' and emp_nbr = " + "'" + myid + "'" + ";" );
                        Calendar clockstart = Calendar.getInstance();
                        Calendar clockstop = Calendar.getInstance();
                        while (res2.next()) {
                            clockstart.set( Integer.valueOf(res2.getString("indate").substring(0,4)), Integer.valueOf(res2.getString("indate").substring(5,7)), Integer.valueOf(res2.getString("indate").substring(8,10)), Integer.valueOf(res2.getString("intime_adj").substring(0,2)), Integer.valueOf(res2.getString("intime_adj").substring(3,5)) );
                            clockstop.set(Integer.valueOf(clockdate.substring(0,4)), Integer.valueOf(clockdate.substring(5,7)), Integer.valueOf(clockdate.substring(8,10)), Integer.valueOf(timeclock_adj.substring(0,2)), Integer.valueOf(timeclock_adj.substring(3,5)));
                        }
                        double quarterhour = 0;
                        long milis1 = clockstart.getTimeInMillis();
                        long milis2 = clockstop.getTimeInMillis();
                        long diff = milis2 - milis1;
                        long diffHours = diff / (60 * 60 * 1000);
                        long diffMinutes = diff / (60 * 1000);
                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        long diffSeconds = diff / 1000;
                        if (diffMinutes > 0) {
                            quarterhour = (diffMinutes / (double) 60) ;

                        }
                        nbrhours = quarterhour;   // this is the total hours accumulated in quarter increments


                        lbstatus.setText("CLOCKED OUT...")   ;
                        st2.executeUpdate("update emp_mstr set emp_clockin = '0' where emp_nbr = " + "'" + myid + "'" +";");
                        // update time record and close
                        st2.executeUpdate("update time_clock set outdate = " +
                            "'" + clockdate + "'" + "," +
                            "outtime = " + "'" + clocktime + "'" + "," +
                            "outtime_adj = " + "'" + timeclock_adj + "'" + "," +
                            "tothrs = " + "'" + nbrhours + "'" + "," +
                            "code_id = " + "'" + "00" + "'" +
                            " where emp_nbr = " + "'" + myid + "'" +
                            "AND code_id = " + "'01'" +
                            ";");

                    }

                    if (dddir.getSelectedItem().toString().equals(getGlobalProgTag("clockin"))) {
                    lbimage.setIcon(new ImageIcon(getClass().getResource("/images/bugs.png")));
                    } else {
                    lbimage.setIcon(new ImageIcon(getClass().getResource("/images/coyote.png")));    
                    }
                    jTextArea1.append(lbname.getText() + "..." + lbstatus.getText() + "..." + lbclockdatetime.getText() + "...clocktime-> "  + timeclock_adj + "\n");

                        // int i = res.getInt("MakeID");
                        //  String s = res.getString("MakeName");
                        //  System.out.println(i + "\t\t" + s);
                    } // if proceed                    

                    if (i == 0) {
                        failureAction(getMessageTag(1117));
                    } 
                } catch (SQLException s){
                    MainFrame.bslog(s);
                   bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                } finally {
                if (res != null) {
                    res.close();
                }
                if (res2 != null) {
                    res2.close();
                }
                if (st != null) {
                    st.close();
                }
                if (st2 != null) {
                    st2.close();
                }
                con.close();
                }

            } catch (Exception e){
                MainFrame.bslog(e);
            }

        } // if not isclocked

        new AnswerWorker().execute();
    }//GEN-LAST:event_tbclocknumberActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox dddir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel lbclockdatetime;
    private javax.swing.JLabel lbempid;
    private javax.swing.JLabel lbimage;
    private javax.swing.JLabel lbname;
    private javax.swing.JLabel lbstatus;
    private javax.swing.JPasswordField tbclocknumber;
    // End of variables declaration//GEN-END:variables
}
