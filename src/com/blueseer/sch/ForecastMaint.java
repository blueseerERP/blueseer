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
package com.blueseer.sch;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */
public class ForecastMaint extends javax.swing.JPanel implements IBlueSeer {

     // global variable declarations
                boolean isLoad = false;
                boolean isNew = false;
    
    // global datatablemodel declarations       
                
    
    public ForecastMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
     // interface functions implemented
    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(String type, String[] key) { 
              this.type = type;
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "add":
                    message = addRecord(key);
                    break;
                case "update":
                    message = updateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
                    break;
                case "get":
                    message = getRecord(key);    
                    break;    
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
           if (this.type.equals("delete")) {
             initvars(null);  
           } else if (this.type.equals("get") && message[0].equals("1")) {
             tbkey.requestFocus();
           } else if (this.type.equals("get") && message[0].equals("0")) {
             tbkey.requestFocus();
           } else {
             initvars(null);  
           }
           
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
   
    public void setPanelComponentState(Object myobj, boolean b) {
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
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                if (component instanceof JScrollPane) {
                    setPanelComponentState((JScrollPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    
                    component.setEnabled(b);
                    
                }
            }
            if (scrollpane != null) {
                scrollpane.setEnabled(b);
                JViewport viewport = scrollpane.getViewport();
                Component[] componentspane = viewport.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    component.setEnabled(b);
                }
            }
    } 
    
    public void setLanguageTags(Object myobj) {
      // lblaccount.setText(labels.getString("LedgerAcctMstrPanel.labels.lblaccount"));
      
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
    
    public void setComponentDefaultValues() {
       
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        DateFormat dfperiod = new SimpleDateFormat("M");
        
        isLoad = true;
        isNew = false;
       
         tbkey.setText("");
        tbqty1.setText("0");
        tbqty2.setText("0");
        tbqty3.setText("0");
        tbqty4.setText("0");
        tbqty5.setText("0");
        tbqty6.setText("0");
        tbqty7.setText("0");
        tbqty8.setText("0");
        tbqty9.setText("0");
        tbqty10.setText("0");
        tbqty11.setText("0");
        tbqty12.setText("0");
        tbqty13.setText("0");
        tbqty14.setText("0");
        tbqty15.setText("0");
        tbqty16.setText("0");
        tbqty17.setText("0");
        tbqty18.setText("0");
        tbqty19.setText("0");
        tbqty20.setText("0");
        tbqty21.setText("0");
        tbqty22.setText("0");
        tbqty23.setText("0");
        tbqty24.setText("0");
        tbqty25.setText("0");
        tbqty26.setText("0");
        tbqty27.setText("0");
        tbqty28.setText("0");
        tbqty29.setText("0");
        tbqty30.setText("0");
        tbqty31.setText("0");
        tbqty32.setText("0");
        tbqty33.setText("0");
        tbqty34.setText("0");
        tbqty35.setText("0");
        tbqty36.setText("0");
        tbqty37.setText("0");
        tbqty38.setText("0");
        tbqty39.setText("0");
        tbqty40.setText("0");
        tbqty41.setText("0");
        tbqty42.setText("0");
        tbqty43.setText("0");
        tbqty44.setText("0");
        tbqty45.setText("0");
        tbqty46.setText("0");
        tbqty47.setText("0");
        tbqty48.setText("0");
        tbqty49.setText("0");
        tbqty50.setText("0");
        tbqty51.setText("0");
        tbqty52.setText("0");
        
        
         ddsite.removeAllItems();
        ArrayList<String> site = OVData.getSiteList();
        for (int i = 0; i < site.size(); i++) {
            ddsite.addItem(site.get(i));
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
      
        
        if (ddyear.getItemCount() == 0) {
        for (int i = 1967 ; i < 2222; i++) {
            ddyear.addItem(String.valueOf(i));
        }
        ddyear.setSelectedItem(dfyear.format(now));
        
        ddyear.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    ArrayList<String> fctdates = OVData.getForecastDates(ddyear.getSelectedItem().toString());
                  if (! fctdates.isEmpty()) {
                  setForecastDates(fctdates);
                  if (! isNew)
                  executeTask("get", new String[]{tbkey.getText(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString()});
                  }
                   
                   // System.out.println(e.getItem());
                }
            }
        });
        }
        
        ArrayList<String> fctdates = OVData.getForecastDates(ddyear.getSelectedItem().toString());
        if (! fctdates.isEmpty()) {
           setForecastDates(fctdates);
       }
        
      
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        isNew = true;
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public String[] setAction(int i) {
        String[] m = new String[2];
        if (i > 0) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   ddsite.setEnabled(false);
                   ddyear.setEnabled(false);
                   tbkey.setForeground(Color.blue);
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
        return m;
    }
    
    public boolean validateInput(String x) {
        boolean b = true;
        
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
                    return b;
                }
                
                 if (! OVData.isValidItem(tbkey.getText()) && x.equals("addRecord")) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1021, tbkey.getText()));
                    tbkey.requestFocus();
                    return b;
                }
        
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    return b;
                }
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btbrowse.setEnabled(true);
        
         // this is a three key data record
        if (arg != null && arg.length > 2) {
            executeTask("get",arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
            ddsite.setEnabled(true);
            ddsite.setEditable(true);
            ddyear.setEnabled(true);
            ddyear.setEditable(true);
        }
    }
    
    public String[] addRecord(String[] x) {
     String[] m = new String[2];
     
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
                 java.util.Date now = new java.util.Date();
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                boolean proceed = true;
                int i = 0;

                    res = st.executeQuery("SELECT fct_item FROM  fct_mstr where fct_item = " + "'" + tbkey.getText() + "'" +
                            " AND fct_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + 
                            " AND fct_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                       st.executeUpdate("insert into fct_mstr "
                            + "(fct_item, fct_site, fct_year,"
                            + "fct_wkqty1, fct_wkqty2, fct_wkqty3, fct_wkqty4, fct_wkqty5, fct_wkqty6, fct_wkqty7, fct_wkqty8, fct_wkqty9, fct_wkqty10, "
                                + "fct_wkqty11, fct_wkqty12, fct_wkqty13, fct_wkqty14, fct_wkqty15, fct_wkqty16, fct_wkqty17, fct_wkqty18, fct_wkqty19, fct_wkqty20, "
                                + "fct_wkqty21, fct_wkqty22, fct_wkqty23, fct_wkqty24, fct_wkqty25, fct_wkqty26, fct_wkqty27, fct_wkqty28, fct_wkqty29, fct_wkqty30, "
                                + "fct_wkqty31, fct_wkqty32, fct_wkqty33, fct_wkqty34, fct_wkqty35, fct_wkqty36, fct_wkqty37, fct_wkqty38, fct_wkqty39, fct_wkqty40, "
                            + "fct_wkqty41, fct_wkqty42, fct_wkqty43, fct_wkqty44, fct_wkqty45, fct_wkqty46, fct_wkqty47, fct_wkqty48, fct_wkqty49, fct_wkqty50, "
                              + "fct_wkqty51, fct_wkqty52, fct_crt_userid, fct_chg_userid, fct_crt_date, fct_chg_date ) "
                            + " values ( " + "'" + tbkey.getText().toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                            + "'" + ddyear.getSelectedItem().toString() + "'" + ","
                                + "'" + tbqty1.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty2.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty3.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty4.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty5.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty6.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty7.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty8.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty9.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty10.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty11.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty12.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty13.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty14.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty15.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty16.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty17.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty18.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty19.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty20.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty21.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty22.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty23.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty24.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty25.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty26.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty27.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty28.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty29.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty30.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty31.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty32.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty33.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty34.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty35.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty36.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty37.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty38.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty39.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty40.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty41.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty42.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty43.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty44.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty45.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty46.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty47.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty48.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty49.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty50.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty51.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + tbqty52.getText().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + bsmf.MainFrame.userid + "'" + ","
                                + "'" + bsmf.MainFrame.userid + "'" + ","
                                + "'" + dfdate.format(now) + "'" + ","
                                + "'" + dfdate.format(now) + "'"
                            + ")"
                            + ";");
                        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                    } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists}; 
                    }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                 m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
             m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordConnError};
        }
     
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     
     try {
            java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            boolean proceed = true;
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                     st.executeUpdate("update fct_mstr "
                            + "set " + "fct_chg_userid = " + "'" + bsmf.MainFrame.userid + "'" + "," +
                                " fct_chg_date = " + "'" + dfdate.format(now) + "'" + "," +
                                " fct_wkqty1 = " + "'" + tbqty1.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty2 = " + "'" + tbqty2.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty3 = " + "'" + tbqty3.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty4 = " + "'" + tbqty4.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty5 = " + "'" + tbqty5.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty6 = " + "'" + tbqty6.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty7 = " + "'" + tbqty7.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty8 = " + "'" + tbqty8.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty9 = " + "'" + tbqty9.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty10 = " + "'" + tbqty10.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty11 = " + "'" + tbqty11.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty12 = " + "'" + tbqty12.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty13 = " + "'" + tbqty13.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty14 = " + "'" + tbqty14.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty15 = " + "'" + tbqty15.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty16 = " + "'" + tbqty16.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty17 = " + "'" + tbqty17.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty18 = " + "'" + tbqty18.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty19 = " + "'" + tbqty19.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty20 = " + "'" + tbqty20.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty21 = " + "'" + tbqty21.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty22 = " + "'" + tbqty22.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty23 = " + "'" + tbqty23.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty24 = " + "'" + tbqty24.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty25 = " + "'" + tbqty25.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty26 = " + "'" + tbqty26.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty27 = " + "'" + tbqty27.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty28 = " + "'" + tbqty28.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty29 = " + "'" + tbqty29.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty30 = " + "'" + tbqty30.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty31 = " + "'" + tbqty31.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty32 = " + "'" + tbqty32.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty33 = " + "'" + tbqty33.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty34 = " + "'" + tbqty34.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty35 = " + "'" + tbqty35.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty36 = " + "'" + tbqty36.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty37 = " + "'" + tbqty37.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty38 = " + "'" + tbqty38.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty39 = " + "'" + tbqty39.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty40 = " + "'" + tbqty40.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty41 = " + "'" + tbqty41.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty42 = " + "'" + tbqty42.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty43 = " + "'" + tbqty43.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty44 = " + "'" + tbqty44.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty45 = " + "'" + tbqty45.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty46 = " + "'" + tbqty46.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty47 = " + "'" + tbqty47.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty48 = " + "'" + tbqty48.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty49 = " + "'" + tbqty49.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty50 = " + "'" + tbqty50.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty51 = " + "'" + tbqty51.getText().replace(defaultDecimalSeparator, '.') + "'" + "," +
                                " fct_wkqty52 = " + "'" + tbqty52.getText().replace(defaultDecimalSeparator, '.') + "'" + 
                             " where fct_item = " + "'" + tbkey.getText() + "'" +
                              " AND fct_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                              " AND fct_year = " + "'" + ddyear.getSelectedItem().toString() + "'"
                            + ";");
                      
                        st.executeUpdate("delete from plan_mstr where plan_item = " +  "'" + tbkey.getText() + "'"
                             + " AND plan_site = " + "'" + ddsite.getSelectedItem().toString() + "'" 
                                + " AND plan_is_sched = '0' " 
                                + ";");
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
               
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};  
            } finally {
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
        }
     
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                
                int i = st.executeUpdate("delete from fct_mstr where fct_item = " + "'" + tbkey.getText() + "'" +
                          " AND fct_site =  " + "'" + ddsite.getSelectedItem().toString() + "'" +
                           " AND fct_year = " + "'" + ddyear.getSelectedItem().toString() + "'" +  ";");
                 st.executeUpdate("delete from plan_mstr where plan_item = " +  "'" + tbkey.getText() + "'"
                             + " AND plan_site = " + "'" + ddsite.getSelectedItem().toString() + "'" 
                                + " AND plan_is_sched = '0' " 
                                + ";");
                    if (i > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    } else {
                    m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError};    
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
            } finally {
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordConnError};
        }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
       
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
                if (x == null && x.length < 2) { return new String[]{}; };
                // three key system....make accomodation for first key action performed returning first record where it exists..else grab specific rec with all three keys
                if (x[1] == null || x[2] == null) {
                res = st.executeQuery("SELECT * FROM  fct_mstr where fct_item = " + "'" + x[0] + "'"  + " limit 1 ;"); 
                } else {
                res = st.executeQuery("SELECT * FROM  fct_mstr where fct_item = " + "'" + x[0] + "'" +
                            " AND fct_site = " + "'" + x[1] + "'" + 
                            " AND fct_year = " + "'" + x[2] + "'" + ";");
                }
                while (res.next()) {
                    i++;
                    tbkey.setText(x[0]);
                    ddsite.setSelectedItem(res.getString("fct_site"));
                    ddyear.setSelectedItem(res.getString("fct_year"));
                     tbqty1.setText(res.getString("fct_wkqty1").replace('.',defaultDecimalSeparator));
        tbqty2.setText(res.getString("fct_wkqty2").replace('.',defaultDecimalSeparator));
        tbqty3.setText(res.getString("fct_wkqty3").replace('.',defaultDecimalSeparator));
        tbqty4.setText(res.getString("fct_wkqty4").replace('.',defaultDecimalSeparator));
        tbqty5.setText(res.getString("fct_wkqty5").replace('.',defaultDecimalSeparator));
        tbqty6.setText(res.getString("fct_wkqty6").replace('.',defaultDecimalSeparator));
        tbqty7.setText(res.getString("fct_wkqty7").replace('.',defaultDecimalSeparator));
        tbqty8.setText(res.getString("fct_wkqty8").replace('.',defaultDecimalSeparator));
        tbqty9.setText(res.getString("fct_wkqty9").replace('.',defaultDecimalSeparator));
        tbqty10.setText(res.getString("fct_wkqty10").replace('.',defaultDecimalSeparator));
        tbqty11.setText(res.getString("fct_wkqty11").replace('.',defaultDecimalSeparator));
        tbqty12.setText(res.getString("fct_wkqty12").replace('.',defaultDecimalSeparator));
        tbqty13.setText(res.getString("fct_wkqty13").replace('.',defaultDecimalSeparator));
        tbqty14.setText(res.getString("fct_wkqty14").replace('.',defaultDecimalSeparator));
        tbqty15.setText(res.getString("fct_wkqty15").replace('.',defaultDecimalSeparator));
        tbqty16.setText(res.getString("fct_wkqty16").replace('.',defaultDecimalSeparator));
        tbqty17.setText(res.getString("fct_wkqty17").replace('.',defaultDecimalSeparator));
        tbqty18.setText(res.getString("fct_wkqty18").replace('.',defaultDecimalSeparator));
        tbqty19.setText(res.getString("fct_wkqty19").replace('.',defaultDecimalSeparator));
        tbqty20.setText(res.getString("fct_wkqty20").replace('.',defaultDecimalSeparator));
        tbqty21.setText(res.getString("fct_wkqty21").replace('.',defaultDecimalSeparator));
        tbqty22.setText(res.getString("fct_wkqty22").replace('.',defaultDecimalSeparator));
        tbqty23.setText(res.getString("fct_wkqty23").replace('.',defaultDecimalSeparator));
        tbqty24.setText(res.getString("fct_wkqty24").replace('.',defaultDecimalSeparator));
        tbqty25.setText(res.getString("fct_wkqty25").replace('.',defaultDecimalSeparator));
        tbqty26.setText(res.getString("fct_wkqty26").replace('.',defaultDecimalSeparator));
        tbqty27.setText(res.getString("fct_wkqty27").replace('.',defaultDecimalSeparator));
        tbqty28.setText(res.getString("fct_wkqty28").replace('.',defaultDecimalSeparator));
        tbqty29.setText(res.getString("fct_wkqty29").replace('.',defaultDecimalSeparator));
        tbqty30.setText(res.getString("fct_wkqty30").replace('.',defaultDecimalSeparator));
        tbqty31.setText(res.getString("fct_wkqty31").replace('.',defaultDecimalSeparator));
        tbqty32.setText(res.getString("fct_wkqty32").replace('.',defaultDecimalSeparator));
        tbqty33.setText(res.getString("fct_wkqty33").replace('.',defaultDecimalSeparator));
        tbqty34.setText(res.getString("fct_wkqty34").replace('.',defaultDecimalSeparator));
        tbqty35.setText(res.getString("fct_wkqty35").replace('.',defaultDecimalSeparator));
        tbqty36.setText(res.getString("fct_wkqty36").replace('.',defaultDecimalSeparator));
        tbqty37.setText(res.getString("fct_wkqty37").replace('.',defaultDecimalSeparator));
        tbqty38.setText(res.getString("fct_wkqty38").replace('.',defaultDecimalSeparator));
        tbqty39.setText(res.getString("fct_wkqty39").replace('.',defaultDecimalSeparator));
        tbqty40.setText(res.getString("fct_wkqty40").replace('.',defaultDecimalSeparator));
        tbqty41.setText(res.getString("fct_wkqty41").replace('.',defaultDecimalSeparator));
        tbqty42.setText(res.getString("fct_wkqty42").replace('.',defaultDecimalSeparator));
        tbqty43.setText(res.getString("fct_wkqty43").replace('.',defaultDecimalSeparator));
        tbqty44.setText(res.getString("fct_wkqty44").replace('.',defaultDecimalSeparator));
        tbqty45.setText(res.getString("fct_wkqty45").replace('.',defaultDecimalSeparator));
        tbqty46.setText(res.getString("fct_wkqty46").replace('.',defaultDecimalSeparator));
        tbqty47.setText(res.getString("fct_wkqty47").replace('.',defaultDecimalSeparator));
        tbqty48.setText(res.getString("fct_wkqty48").replace('.',defaultDecimalSeparator));
        tbqty49.setText(res.getString("fct_wkqty49").replace('.',defaultDecimalSeparator));
        tbqty50.setText(res.getString("fct_wkqty50").replace('.',defaultDecimalSeparator));
        tbqty51.setText(res.getString("fct_wkqty51").replace('.',defaultDecimalSeparator));
        tbqty52.setText(res.getString("fct_wkqty52").replace('.',defaultDecimalSeparator));
        
                }
               
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError};  
        } 
      return m;
    }
    
    
   // custom funcs
    public void setForecastDates(ArrayList dates) {
        DateFormat dtf = new SimpleDateFormat("MM/dd/yy");
        if (dates.isEmpty())
            return;
        
        lbl1.setText(dtf.format(dates.get(0)));
        lbl2.setText(dtf.format(dates.get(1)));
        lbl3.setText(dtf.format(dates.get(2)));
        lbl4.setText(dtf.format(dates.get(3)));
        lbl5.setText(dtf.format(dates.get(4)));
        lbl6.setText(dtf.format(dates.get(5)));
        lbl7.setText(dtf.format(dates.get(6)));
        lbl8.setText(dtf.format(dates.get(7)));
        lbl9.setText(dtf.format(dates.get(8)));
        lbl10.setText(dtf.format(dates.get(9)));
        lbl11.setText(dtf.format(dates.get(10)));
        lbl12.setText(dtf.format(dates.get(11)));
        lbl13.setText(dtf.format(dates.get(12)));
        lbl14.setText(dtf.format(dates.get(13)));
        lbl15.setText(dtf.format(dates.get(14)));
        lbl16.setText(dtf.format(dates.get(15)));
        lbl17.setText(dtf.format(dates.get(16)));
        lbl18.setText(dtf.format(dates.get(17)));
        lbl19.setText(dtf.format(dates.get(18)));
        lbl20.setText(dtf.format(dates.get(19)));
        lbl21.setText(dtf.format(dates.get(20)));
        lbl22.setText(dtf.format(dates.get(21)));
        lbl23.setText(dtf.format(dates.get(22)));
        lbl24.setText(dtf.format(dates.get(23)));
        lbl25.setText(dtf.format(dates.get(24)));
        lbl26.setText(dtf.format(dates.get(25)));
        lbl27.setText(dtf.format(dates.get(26)));
        lbl28.setText(dtf.format(dates.get(27)));
        lbl29.setText(dtf.format(dates.get(28)));
        lbl30.setText(dtf.format(dates.get(29)));
        lbl31.setText(dtf.format(dates.get(30)));
        lbl32.setText(dtf.format(dates.get(31)));
        lbl33.setText(dtf.format(dates.get(32)));
        lbl34.setText(dtf.format(dates.get(33)));
        lbl35.setText(dtf.format(dates.get(34)));
        lbl36.setText(dtf.format(dates.get(35)));
        lbl37.setText(dtf.format(dates.get(36)));
        lbl38.setText(dtf.format(dates.get(37)));
        lbl39.setText(dtf.format(dates.get(38)));
        lbl40.setText(dtf.format(dates.get(39)));
        lbl41.setText(dtf.format(dates.get(40)));
        lbl42.setText(dtf.format(dates.get(41)));
        lbl43.setText(dtf.format(dates.get(42)));
        lbl44.setText(dtf.format(dates.get(43)));
        lbl45.setText(dtf.format(dates.get(44)));
        lbl46.setText(dtf.format(dates.get(45)));
        lbl47.setText(dtf.format(dates.get(46)));
        lbl48.setText(dtf.format(dates.get(47)));
        lbl49.setText(dtf.format(dates.get(48)));
        lbl50.setText(dtf.format(dates.get(49)));
        lbl51.setText(dtf.format(dates.get(50)));
        lbl52.setText(dtf.format(dates.get(51)));
        
        
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox();
        ddyear = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        btdelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        tbqty6 = new javax.swing.JTextField();
        tbqty5 = new javax.swing.JTextField();
        lbl4 = new javax.swing.JLabel();
        tbqty2 = new javax.swing.JTextField();
        lbl9 = new javax.swing.JLabel();
        tbqty8 = new javax.swing.JTextField();
        tbqty7 = new javax.swing.JTextField();
        lbl8 = new javax.swing.JLabel();
        lbl11 = new javax.swing.JLabel();
        lbl6 = new javax.swing.JLabel();
        tbqty10 = new javax.swing.JTextField();
        lbl5 = new javax.swing.JLabel();
        tbqty1 = new javax.swing.JTextField();
        tbqty3 = new javax.swing.JTextField();
        lbl2 = new javax.swing.JLabel();
        lbl7 = new javax.swing.JLabel();
        lbl10 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        tbqty9 = new javax.swing.JTextField();
        tbqty11 = new javax.swing.JTextField();
        lbl13 = new javax.swing.JLabel();
        lbl12 = new javax.swing.JLabel();
        tbqty4 = new javax.swing.JTextField();
        tbqty12 = new javax.swing.JTextField();
        tbqty13 = new javax.swing.JTextField();
        lbl1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        tbqty19 = new javax.swing.JTextField();
        tbqty18 = new javax.swing.JTextField();
        lbl17 = new javax.swing.JLabel();
        tbqty15 = new javax.swing.JTextField();
        lbl22 = new javax.swing.JLabel();
        tbqty21 = new javax.swing.JTextField();
        tbqty20 = new javax.swing.JTextField();
        lbl21 = new javax.swing.JLabel();
        lbl24 = new javax.swing.JLabel();
        lbl19 = new javax.swing.JLabel();
        tbqty23 = new javax.swing.JTextField();
        lbl18 = new javax.swing.JLabel();
        tbqty14 = new javax.swing.JTextField();
        tbqty16 = new javax.swing.JTextField();
        lbl15 = new javax.swing.JLabel();
        lbl20 = new javax.swing.JLabel();
        lbl23 = new javax.swing.JLabel();
        lbl16 = new javax.swing.JLabel();
        tbqty22 = new javax.swing.JTextField();
        tbqty24 = new javax.swing.JTextField();
        lbl26 = new javax.swing.JLabel();
        lbl25 = new javax.swing.JLabel();
        tbqty17 = new javax.swing.JTextField();
        tbqty25 = new javax.swing.JTextField();
        tbqty26 = new javax.swing.JTextField();
        lbl14 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tbqty32 = new javax.swing.JTextField();
        lbl31 = new javax.swing.JLabel();
        lbl35 = new javax.swing.JLabel();
        lbl32 = new javax.swing.JLabel();
        tbqty38 = new javax.swing.JTextField();
        tbqty27 = new javax.swing.JTextField();
        lbl28 = new javax.swing.JLabel();
        lbl30 = new javax.swing.JLabel();
        tbqty36 = new javax.swing.JTextField();
        tbqty29 = new javax.swing.JTextField();
        lbl33 = new javax.swing.JLabel();
        lbl29 = new javax.swing.JLabel();
        tbqty39 = new javax.swing.JTextField();
        tbqty35 = new javax.swing.JTextField();
        lbl27 = new javax.swing.JLabel();
        lbl34 = new javax.swing.JLabel();
        tbqty37 = new javax.swing.JTextField();
        tbqty30 = new javax.swing.JTextField();
        lbl38 = new javax.swing.JLabel();
        lbl36 = new javax.swing.JLabel();
        lbl39 = new javax.swing.JLabel();
        tbqty33 = new javax.swing.JTextField();
        tbqty28 = new javax.swing.JTextField();
        tbqty31 = new javax.swing.JTextField();
        lbl37 = new javax.swing.JLabel();
        tbqty34 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        tbqty48 = new javax.swing.JTextField();
        tbqty42 = new javax.swing.JTextField();
        tbqty44 = new javax.swing.JTextField();
        lbl49 = new javax.swing.JLabel();
        lbl45 = new javax.swing.JLabel();
        tbqty41 = new javax.swing.JTextField();
        lbl47 = new javax.swing.JLabel();
        lbl43 = new javax.swing.JLabel();
        tbqty43 = new javax.swing.JTextField();
        tbqty40 = new javax.swing.JTextField();
        lbl42 = new javax.swing.JLabel();
        lbl48 = new javax.swing.JLabel();
        lbl52 = new javax.swing.JLabel();
        tbqty45 = new javax.swing.JTextField();
        tbqty46 = new javax.swing.JTextField();
        tbqty50 = new javax.swing.JTextField();
        lbl50 = new javax.swing.JLabel();
        tbqty49 = new javax.swing.JTextField();
        tbqty47 = new javax.swing.JTextField();
        lbl51 = new javax.swing.JLabel();
        lbl40 = new javax.swing.JLabel();
        lbl44 = new javax.swing.JLabel();
        tbqty52 = new javax.swing.JTextField();
        tbqty51 = new javax.swing.JTextField();
        lbl46 = new javax.swing.JLabel();
        lbl41 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Forecast Maintenance"));
        jPanel7.setName("panelmain"); // NOI18N

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Edit");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel3.setText("Year");
        jLabel3.setName("lblyear"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel2.setText("Site");
        jLabel2.setName("lblsite"); // NOI18N

        jLabel1.setText("Part");
        jLabel1.setName("lblitem"); // NOI18N

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tbkey, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                    .addComponent(ddyear, 0, 123, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew))
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(btnew)
                        .addComponent(btclear))
                    .addComponent(btbrowse))
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        lbl4.setText("06/28/15");

        lbl9.setText("06/28/15");

        lbl8.setText("06/28/15");

        lbl11.setText("06/28/15");

        lbl6.setText("06/28/15");

        lbl5.setText("06/28/15");

        lbl2.setText("06/28/15");

        lbl7.setText("06/28/15");

        lbl10.setText("06/28/15");

        lbl3.setText("06/28/15");

        lbl13.setText("06/28/15");

        lbl12.setText("06/28/15");

        lbl1.setText("06/28/15");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbqty8, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty7, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty10, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty9, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty12, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty11, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty13, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty6, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel1);

        lbl17.setText("06/28/15");

        lbl22.setText("06/28/15");

        lbl21.setText("06/28/15");

        lbl24.setText("06/28/15");

        lbl19.setText("06/28/15");

        lbl18.setText("06/28/15");

        lbl15.setText("06/28/15");

        lbl20.setText("06/28/15");

        lbl23.setText("06/28/15");

        lbl16.setText("06/28/15");

        lbl26.setText("06/28/15");

        lbl25.setText("06/28/15");

        lbl14.setText("06/28/15");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbqty21, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty20, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty23, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty22, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty14, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty25, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty24, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty26, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty15, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty16, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty17, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty18, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty19, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl26))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel6);

        lbl31.setText("06/28/15");

        lbl35.setText("06/28/15");

        lbl32.setText("06/28/15");

        lbl28.setText("06/28/15");

        lbl30.setText("06/28/15");

        lbl33.setText("06/28/15");

        lbl29.setText("06/28/15");

        lbl27.setText("06/28/15");

        lbl34.setText("06/28/15");

        lbl38.setText("06/28/15");

        lbl36.setText("06/28/15");

        lbl39.setText("06/28/15");

        lbl37.setText("06/28/15");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lbl27, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tbqty27, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lbl28, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tbqty28, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl29, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty29, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl30, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty30, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl31, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty31, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl32, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty32, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl33, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty33, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl34, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty34, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl35, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty35, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl36, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty36, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl37, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty37, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl38, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty38, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl39, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty39, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl27)
                    .addComponent(tbqty27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl39))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel2);

        lbl49.setText("06/28/15");

        lbl45.setText("06/28/15");

        lbl47.setText("06/28/15");

        lbl43.setText("06/28/15");

        lbl42.setText("06/28/15");

        lbl48.setText("06/28/15");

        lbl52.setText("06/28/15");

        lbl50.setText("06/28/15");

        lbl51.setText("06/28/15");

        lbl40.setText("06/28/15");

        lbl44.setText("06/28/15");

        lbl46.setText("06/28/15");

        lbl41.setText("06/28/15");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl40, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty40, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl41, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty41, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl42, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty42, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl43, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty43, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl44, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty44, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl45, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty45, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl46, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty46, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl47, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty47, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl48, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty48, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl49, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty49, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl50, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty50, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl51, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty51, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl52, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty52, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl44))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl46))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl47))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl50))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl51))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl52))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel3);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(189, 189, 189)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(jPanel7);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
      if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput("deleteRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("delete", new String[]{tbkey.getText(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString()});   
     
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"fctmaint","fct_item"});
    }//GEN-LAST:event_btbrowseActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
       executeTask("get", new String[]{tbkey.getText(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddyear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl10;
    private javax.swing.JLabel lbl11;
    private javax.swing.JLabel lbl12;
    private javax.swing.JLabel lbl13;
    private javax.swing.JLabel lbl14;
    private javax.swing.JLabel lbl15;
    private javax.swing.JLabel lbl16;
    private javax.swing.JLabel lbl17;
    private javax.swing.JLabel lbl18;
    private javax.swing.JLabel lbl19;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl20;
    private javax.swing.JLabel lbl21;
    private javax.swing.JLabel lbl22;
    private javax.swing.JLabel lbl23;
    private javax.swing.JLabel lbl24;
    private javax.swing.JLabel lbl25;
    private javax.swing.JLabel lbl26;
    private javax.swing.JLabel lbl27;
    private javax.swing.JLabel lbl28;
    private javax.swing.JLabel lbl29;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl30;
    private javax.swing.JLabel lbl31;
    private javax.swing.JLabel lbl32;
    private javax.swing.JLabel lbl33;
    private javax.swing.JLabel lbl34;
    private javax.swing.JLabel lbl35;
    private javax.swing.JLabel lbl36;
    private javax.swing.JLabel lbl37;
    private javax.swing.JLabel lbl38;
    private javax.swing.JLabel lbl39;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl40;
    private javax.swing.JLabel lbl41;
    private javax.swing.JLabel lbl42;
    private javax.swing.JLabel lbl43;
    private javax.swing.JLabel lbl44;
    private javax.swing.JLabel lbl45;
    private javax.swing.JLabel lbl46;
    private javax.swing.JLabel lbl47;
    private javax.swing.JLabel lbl48;
    private javax.swing.JLabel lbl49;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lbl50;
    private javax.swing.JLabel lbl51;
    private javax.swing.JLabel lbl52;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl7;
    private javax.swing.JLabel lbl8;
    private javax.swing.JLabel lbl9;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbqty1;
    private javax.swing.JTextField tbqty10;
    private javax.swing.JTextField tbqty11;
    private javax.swing.JTextField tbqty12;
    private javax.swing.JTextField tbqty13;
    private javax.swing.JTextField tbqty14;
    private javax.swing.JTextField tbqty15;
    private javax.swing.JTextField tbqty16;
    private javax.swing.JTextField tbqty17;
    private javax.swing.JTextField tbqty18;
    private javax.swing.JTextField tbqty19;
    private javax.swing.JTextField tbqty2;
    private javax.swing.JTextField tbqty20;
    private javax.swing.JTextField tbqty21;
    private javax.swing.JTextField tbqty22;
    private javax.swing.JTextField tbqty23;
    private javax.swing.JTextField tbqty24;
    private javax.swing.JTextField tbqty25;
    private javax.swing.JTextField tbqty26;
    private javax.swing.JTextField tbqty27;
    private javax.swing.JTextField tbqty28;
    private javax.swing.JTextField tbqty29;
    private javax.swing.JTextField tbqty3;
    private javax.swing.JTextField tbqty30;
    private javax.swing.JTextField tbqty31;
    private javax.swing.JTextField tbqty32;
    private javax.swing.JTextField tbqty33;
    private javax.swing.JTextField tbqty34;
    private javax.swing.JTextField tbqty35;
    private javax.swing.JTextField tbqty36;
    private javax.swing.JTextField tbqty37;
    private javax.swing.JTextField tbqty38;
    private javax.swing.JTextField tbqty39;
    private javax.swing.JTextField tbqty4;
    private javax.swing.JTextField tbqty40;
    private javax.swing.JTextField tbqty41;
    private javax.swing.JTextField tbqty42;
    private javax.swing.JTextField tbqty43;
    private javax.swing.JTextField tbqty44;
    private javax.swing.JTextField tbqty45;
    private javax.swing.JTextField tbqty46;
    private javax.swing.JTextField tbqty47;
    private javax.swing.JTextField tbqty48;
    private javax.swing.JTextField tbqty49;
    private javax.swing.JTextField tbqty5;
    private javax.swing.JTextField tbqty50;
    private javax.swing.JTextField tbqty51;
    private javax.swing.JTextField tbqty52;
    private javax.swing.JTextField tbqty6;
    private javax.swing.JTextField tbqty7;
    private javax.swing.JTextField tbqty8;
    private javax.swing.JTextField tbqty9;
    // End of variables declaration//GEN-END:variables
}
