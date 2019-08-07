/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.sch;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

/**
 *
 * @author vaughnte
 */
public class ForecastMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form ForecastMaintPanel
     */
    public ForecastMaintPanel() {
        initComponents();
    }

   
    public void setForecastDates(ArrayList dates) {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        if (dates.isEmpty())
            return;
        
        lbl1.setText(df.format(dates.get(0)));
        lbl2.setText(df.format(dates.get(1)));
        lbl3.setText(df.format(dates.get(2)));
        lbl4.setText(df.format(dates.get(3)));
        lbl5.setText(df.format(dates.get(4)));
        lbl6.setText(df.format(dates.get(5)));
        lbl7.setText(df.format(dates.get(6)));
        lbl8.setText(df.format(dates.get(7)));
        lbl9.setText(df.format(dates.get(8)));
        lbl10.setText(df.format(dates.get(9)));
        lbl11.setText(df.format(dates.get(10)));
        lbl12.setText(df.format(dates.get(11)));
        lbl13.setText(df.format(dates.get(12)));
        lbl14.setText(df.format(dates.get(13)));
        lbl15.setText(df.format(dates.get(14)));
        lbl16.setText(df.format(dates.get(15)));
        lbl17.setText(df.format(dates.get(16)));
        lbl18.setText(df.format(dates.get(17)));
        lbl19.setText(df.format(dates.get(18)));
        lbl20.setText(df.format(dates.get(19)));
        lbl21.setText(df.format(dates.get(20)));
        lbl22.setText(df.format(dates.get(21)));
        lbl23.setText(df.format(dates.get(22)));
        lbl24.setText(df.format(dates.get(23)));
        lbl25.setText(df.format(dates.get(24)));
        lbl26.setText(df.format(dates.get(25)));
        lbl27.setText(df.format(dates.get(26)));
        lbl28.setText(df.format(dates.get(27)));
        lbl29.setText(df.format(dates.get(28)));
        lbl30.setText(df.format(dates.get(29)));
        lbl31.setText(df.format(dates.get(30)));
        lbl32.setText(df.format(dates.get(31)));
        lbl33.setText(df.format(dates.get(32)));
        lbl34.setText(df.format(dates.get(33)));
        lbl35.setText(df.format(dates.get(34)));
        lbl36.setText(df.format(dates.get(35)));
        lbl37.setText(df.format(dates.get(36)));
        lbl38.setText(df.format(dates.get(37)));
        lbl39.setText(df.format(dates.get(38)));
        lbl40.setText(df.format(dates.get(39)));
        lbl41.setText(df.format(dates.get(40)));
        lbl42.setText(df.format(dates.get(41)));
        lbl43.setText(df.format(dates.get(42)));
        lbl44.setText(df.format(dates.get(43)));
        lbl45.setText(df.format(dates.get(44)));
        lbl46.setText(df.format(dates.get(45)));
        lbl47.setText(df.format(dates.get(46)));
        lbl48.setText(df.format(dates.get(47)));
        lbl49.setText(df.format(dates.get(48)));
        lbl50.setText(df.format(dates.get(49)));
        lbl51.setText(df.format(dates.get(50)));
        lbl52.setText(df.format(dates.get(51)));
        
        
    }
    
    public void getForecast(String part, String year, String site)  {
        initvars("");
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("SELECT * FROM  fct_mstr where fct_part = " + "'" + part + "'" +
                            " AND fct_site = " + "'" + site + "'" + 
                            " AND fct_year = " + "'" + year + "'" + ";");
                while (res.next()) {
                    i++;
                    tbpart.setText(part);
                    ddsite.setSelectedItem(res.getString("fct_site"));
                    ddyear.setSelectedItem(res.getString("fct_year"));
                     tbqty1.setText(res.getString("fct_wkqty1"));
        tbqty2.setText(res.getString("fct_wkqty2"));
        tbqty3.setText(res.getString("fct_wkqty3"));
        tbqty4.setText(res.getString("fct_wkqty4"));
        tbqty5.setText(res.getString("fct_wkqty5"));
        tbqty6.setText(res.getString("fct_wkqty6"));
        tbqty7.setText(res.getString("fct_wkqty7"));
        tbqty8.setText(res.getString("fct_wkqty8"));
        tbqty9.setText(res.getString("fct_wkqty9"));
        tbqty10.setText(res.getString("fct_wkqty10"));
        tbqty11.setText(res.getString("fct_wkqty11"));
        tbqty12.setText(res.getString("fct_wkqty12"));
        tbqty13.setText(res.getString("fct_wkqty13"));
        tbqty14.setText(res.getString("fct_wkqty14"));
        tbqty15.setText(res.getString("fct_wkqty15"));
        tbqty16.setText(res.getString("fct_wkqty16"));
        tbqty17.setText(res.getString("fct_wkqty17"));
        tbqty18.setText(res.getString("fct_wkqty18"));
        tbqty19.setText(res.getString("fct_wkqty19"));
        tbqty20.setText(res.getString("fct_wkqty20"));
        tbqty21.setText(res.getString("fct_wkqty21"));
        tbqty22.setText(res.getString("fct_wkqty22"));
        tbqty23.setText(res.getString("fct_wkqty23"));
        tbqty24.setText(res.getString("fct_wkqty24"));
        tbqty25.setText(res.getString("fct_wkqty25"));
        tbqty26.setText(res.getString("fct_wkqty26"));
        tbqty27.setText(res.getString("fct_wkqty27"));
        tbqty28.setText(res.getString("fct_wkqty28"));
        tbqty29.setText(res.getString("fct_wkqty29"));
        tbqty30.setText(res.getString("fct_wkqty30"));
        tbqty31.setText(res.getString("fct_wkqty31"));
        tbqty32.setText(res.getString("fct_wkqty32"));
        tbqty33.setText(res.getString("fct_wkqty33"));
        tbqty34.setText(res.getString("fct_wkqty34"));
        tbqty35.setText(res.getString("fct_wkqty35"));
        tbqty36.setText(res.getString("fct_wkqty36"));
        tbqty37.setText(res.getString("fct_wkqty37"));
        tbqty38.setText(res.getString("fct_wkqty38"));
        tbqty39.setText(res.getString("fct_wkqty39"));
        tbqty40.setText(res.getString("fct_wkqty40"));
        tbqty41.setText(res.getString("fct_wkqty41"));
        tbqty42.setText(res.getString("fct_wkqty42"));
        tbqty43.setText(res.getString("fct_wkqty43"));
        tbqty44.setText(res.getString("fct_wkqty44"));
        tbqty45.setText(res.getString("fct_wkqty45"));
        tbqty46.setText(res.getString("fct_wkqty46"));
        tbqty47.setText(res.getString("fct_wkqty47"));
        tbqty48.setText(res.getString("fct_wkqty48"));
        tbqty49.setText(res.getString("fct_wkqty49"));
        tbqty50.setText(res.getString("fct_wkqty50"));
        tbqty51.setText(res.getString("fct_wkqty51"));
        tbqty52.setText(res.getString("fct_wkqty52"));
        
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No Forecast Record found" );

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve fct_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void initvars(String key)  {
        
        String[] keys = key.split(",",-1);
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        DateFormat dfperiod = new SimpleDateFormat("M");
        
        tbpart.setText("");
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
       ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
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
                  }
                    //JOptionPane.showMessageDialog(box, e.getItem());
                   // System.out.println(e.getItem());
                }
            }
        });
        }
        
        ArrayList<String> fctdates = OVData.getForecastDates(ddyear.getSelectedItem().toString());
        if (! fctdates.isEmpty()) {
           setForecastDates(fctdates);
       }
        
        
        if (! key.isEmpty()) {
            getForecast(keys[0], keys[1], keys[2]);
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

        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        tbadd = new javax.swing.JButton();
        tbedit = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox();
        ddyear = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        tbpart = new javax.swing.JTextField();
        tbdelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tbget = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
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

        tbadd.setText("Add");
        tbadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbaddActionPerformed(evt);
            }
        });

        tbedit.setText("Edit");
        tbedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbeditActionPerformed(evt);
            }
        });

        jLabel3.setText("Year");

        tbdelete.setText("Delete");
        tbdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbdeleteActionPerformed(evt);
            }
        });

        jLabel2.setText("Site");

        tbget.setText("Get");
        tbget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbgetActionPerformed(evt);
            }
        });

        jLabel1.setText("Part");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tbget)
                .addGap(29, 29, 29)
                .addComponent(tbadd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbedit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbdelete)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbget)
                    .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbadd)
                    .addComponent(tbedit)
                    .addComponent(tbdelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
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

    private void tbaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbaddActionPerformed
        try {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                // check the site field
                if (tbpart.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter an item number");
                }
                
                 res = st.executeQuery("SELECT it_item FROM  item_mstr where it_item = " + "'" + tbpart.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                if (i == 0) {
                    proceed = false;
                    bsmf.MainFrame.show("Item not in item master");
                }
                
                
                i = 0;
                
                if (proceed) {

                    res = st.executeQuery("SELECT fct_part FROM  fct_mstr where fct_part = " + "'" + tbpart.getText() + "'" +
                            " AND fct_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + 
                            " AND fct_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into fct_mstr "
                            + "(fct_part, fct_site, fct_year,"
                            + "fct_wkqty1, fct_wkqty2, fct_wkqty3, fct_wkqty4, fct_wkqty5, fct_wkqty6, fct_wkqty7, fct_wkqty8, fct_wkqty9, fct_wkqty10, "
                                + "fct_wkqty11, fct_wkqty12, fct_wkqty13, fct_wkqty14, fct_wkqty15, fct_wkqty16, fct_wkqty17, fct_wkqty18, fct_wkqty19, fct_wkqty20, "
                                + "fct_wkqty21, fct_wkqty22, fct_wkqty23, fct_wkqty24, fct_wkqty25, fct_wkqty26, fct_wkqty27, fct_wkqty28, fct_wkqty29, fct_wkqty30, "
                                + "fct_wkqty31, fct_wkqty32, fct_wkqty33, fct_wkqty34, fct_wkqty35, fct_wkqty36, fct_wkqty37, fct_wkqty38, fct_wkqty39, fct_wkqty40, "
                            + "fct_wkqty41, fct_wkqty42, fct_wkqty43, fct_wkqty44, fct_wkqty45, fct_wkqty46, fct_wkqty47, fct_wkqty48, fct_wkqty49, fct_wkqty50, "
                              + "fct_wkqty51, fct_wkqty52, fct_crt_userid, fct_chg_userid, fct_crt_date, fct_chg_date ) "
                            + " values ( " + "'" + tbpart.getText().toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                            + "'" + ddyear.getSelectedItem().toString() + "'" + ","
                                + "'" + tbqty1.getText() + "'" + ","
                                + "'" + tbqty2.getText() + "'" + ","
                                + "'" + tbqty3.getText() + "'" + ","
                                + "'" + tbqty4.getText() + "'" + ","
                                + "'" + tbqty5.getText() + "'" + ","
                                + "'" + tbqty6.getText() + "'" + ","
                                + "'" + tbqty7.getText() + "'" + ","
                                + "'" + tbqty8.getText() + "'" + ","
                                + "'" + tbqty9.getText() + "'" + ","
                                + "'" + tbqty10.getText() + "'" + ","
                                + "'" + tbqty11.getText() + "'" + ","
                                + "'" + tbqty12.getText() + "'" + ","
                                + "'" + tbqty13.getText() + "'" + ","
                                + "'" + tbqty14.getText() + "'" + ","
                                + "'" + tbqty15.getText() + "'" + ","
                                + "'" + tbqty16.getText() + "'" + ","
                                + "'" + tbqty17.getText() + "'" + ","
                                + "'" + tbqty18.getText() + "'" + ","
                                + "'" + tbqty19.getText() + "'" + ","
                                + "'" + tbqty20.getText() + "'" + ","
                                + "'" + tbqty21.getText() + "'" + ","
                                + "'" + tbqty22.getText() + "'" + ","
                                + "'" + tbqty23.getText() + "'" + ","
                                + "'" + tbqty24.getText() + "'" + ","
                                + "'" + tbqty25.getText() + "'" + ","
                                + "'" + tbqty26.getText() + "'" + ","
                                + "'" + tbqty27.getText() + "'" + ","
                                + "'" + tbqty28.getText() + "'" + ","
                                + "'" + tbqty29.getText() + "'" + ","
                                + "'" + tbqty30.getText() + "'" + ","
                                + "'" + tbqty31.getText() + "'" + ","
                                + "'" + tbqty32.getText() + "'" + ","
                                + "'" + tbqty33.getText() + "'" + ","
                                + "'" + tbqty34.getText() + "'" + ","
                                + "'" + tbqty35.getText() + "'" + ","
                                + "'" + tbqty36.getText() + "'" + ","
                                + "'" + tbqty37.getText() + "'" + ","
                                + "'" + tbqty38.getText() + "'" + ","
                                + "'" + tbqty39.getText() + "'" + ","
                                + "'" + tbqty40.getText() + "'" + ","
                                + "'" + tbqty41.getText() + "'" + ","
                                + "'" + tbqty42.getText() + "'" + ","
                                + "'" + tbqty43.getText() + "'" + ","
                                + "'" + tbqty44.getText() + "'" + ","
                                + "'" + tbqty45.getText() + "'" + ","
                                + "'" + tbqty46.getText() + "'" + ","
                                + "'" + tbqty47.getText() + "'" + ","
                                + "'" + tbqty48.getText() + "'" + ","
                                + "'" + tbqty49.getText() + "'" + ","
                                + "'" + tbqty50.getText() + "'" + ","
                                + "'" + tbqty51.getText() + "'" + ","
                                + "'" + tbqty52.getText() + "'" + ","
                                + "'" + bsmf.MainFrame.userid + "'" + ","
                                + "'" + bsmf.MainFrame.userid + "'" + ","
                                + "'" + dfdate.format(now) + "'" + ","
                                + "'" + dfdate.format(now) + "'"
                            + ")"
                            + ";");
                      bsmf.MainFrame.show("Added Forecast Record");
                      initvars("");
                    } else {
                        bsmf.MainFrame.show("Forecast Record Already Exists");
                    }

                   initvars("");
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to fct_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_tbaddActionPerformed

    private void tbgetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbgetActionPerformed
       
       getForecast(tbpart.getText(), ddyear.getSelectedItem().toString(), ddsite.getSelectedItem().toString());
    }//GEN-LAST:event_tbgetActionPerformed

    private void tbdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbdeleteActionPerformed
          boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {
        
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from fct_mstr where fct_part = " + "'" + tbpart.getText() + "'" +
                          " AND fct_site =  " + "'" + ddsite.getSelectedItem().toString() + "'" +
                           " AND fct_year = " + "'" + ddyear.getSelectedItem().toString() + "'" +  ";");
                    if (i > 0) {
                        // now delete any planned orders that has not been scheduled
                         st.executeUpdate("delete from plan_mstr where plan_part = " +  "'" + tbpart.getText() + "'"
                             + " AND plan_site = " + "'" + ddsite.getSelectedItem().toString() + "'" 
                                + " AND plan_is_sched = '0' " 
                                + ";");
                        
                    bsmf.MainFrame.show("deleted forecast record");
                    initvars("");
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete Forecast Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_tbdeleteActionPerformed

    private void tbeditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbeditActionPerformed
        try {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                // check the site field
                if (tbpart.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a Part Number");
                }
                
                
                if (proceed) {
                        st.executeUpdate("update fct_mstr "
                            + "set " + "fct_chg_userid = " + "'" + bsmf.MainFrame.userid + "'" + "," +
                                " fct_chg_date = " + "'" + dfdate.format(now) + "'" + "," +
                                " fct_wkqty1 = " + "'" + tbqty1.getText() + "'" + "," +
                                " fct_wkqty2 = " + "'" + tbqty2.getText() + "'" + "," +
                                " fct_wkqty3 = " + "'" + tbqty3.getText() + "'" + "," +
                                " fct_wkqty4 = " + "'" + tbqty4.getText() + "'" + "," +
                                " fct_wkqty5 = " + "'" + tbqty5.getText() + "'" + "," +
                                " fct_wkqty6 = " + "'" + tbqty6.getText() + "'" + "," +
                                " fct_wkqty7 = " + "'" + tbqty7.getText() + "'" + "," +
                                " fct_wkqty8 = " + "'" + tbqty8.getText() + "'" + "," +
                                " fct_wkqty9 = " + "'" + tbqty9.getText() + "'" + "," +
                                " fct_wkqty10 = " + "'" + tbqty10.getText() + "'" + "," +
                                " fct_wkqty11 = " + "'" + tbqty11.getText() + "'" + "," +
                                " fct_wkqty12 = " + "'" + tbqty12.getText() + "'" + "," +
                                " fct_wkqty13 = " + "'" + tbqty13.getText() + "'" + "," +
                                " fct_wkqty14 = " + "'" + tbqty14.getText() + "'" + "," +
                                " fct_wkqty15 = " + "'" + tbqty15.getText() + "'" + "," +
                                " fct_wkqty16 = " + "'" + tbqty16.getText() + "'" + "," +
                                " fct_wkqty17 = " + "'" + tbqty17.getText() + "'" + "," +
                                " fct_wkqty18 = " + "'" + tbqty18.getText() + "'" + "," +
                                " fct_wkqty19 = " + "'" + tbqty19.getText() + "'" + "," +
                                " fct_wkqty20 = " + "'" + tbqty20.getText() + "'" + "," +
                                " fct_wkqty21 = " + "'" + tbqty21.getText() + "'" + "," +
                                " fct_wkqty22 = " + "'" + tbqty22.getText() + "'" + "," +
                                " fct_wkqty23 = " + "'" + tbqty23.getText() + "'" + "," +
                                " fct_wkqty24 = " + "'" + tbqty24.getText() + "'" + "," +
                                " fct_wkqty25 = " + "'" + tbqty25.getText() + "'" + "," +
                                " fct_wkqty26 = " + "'" + tbqty26.getText() + "'" + "," +
                                " fct_wkqty27 = " + "'" + tbqty27.getText() + "'" + "," +
                                " fct_wkqty28 = " + "'" + tbqty28.getText() + "'" + "," +
                                " fct_wkqty29 = " + "'" + tbqty29.getText() + "'" + "," +
                                " fct_wkqty30 = " + "'" + tbqty30.getText() + "'" + "," +
                                " fct_wkqty31 = " + "'" + tbqty31.getText() + "'" + "," +
                                " fct_wkqty32 = " + "'" + tbqty32.getText() + "'" + "," +
                                " fct_wkqty33 = " + "'" + tbqty33.getText() + "'" + "," +
                                " fct_wkqty34 = " + "'" + tbqty34.getText() + "'" + "," +
                                " fct_wkqty35 = " + "'" + tbqty35.getText() + "'" + "," +
                                " fct_wkqty36 = " + "'" + tbqty36.getText() + "'" + "," +
                                " fct_wkqty37 = " + "'" + tbqty37.getText() + "'" + "," +
                                " fct_wkqty38 = " + "'" + tbqty38.getText() + "'" + "," +
                                " fct_wkqty39 = " + "'" + tbqty39.getText() + "'" + "," +
                                " fct_wkqty40 = " + "'" + tbqty40.getText() + "'" + "," +
                                " fct_wkqty41 = " + "'" + tbqty41.getText() + "'" + "," +
                                " fct_wkqty42 = " + "'" + tbqty42.getText() + "'" + "," +
                                " fct_wkqty43 = " + "'" + tbqty43.getText() + "'" + "," +
                                " fct_wkqty44 = " + "'" + tbqty44.getText() + "'" + "," +
                                " fct_wkqty45 = " + "'" + tbqty45.getText() + "'" + "," +
                                " fct_wkqty46 = " + "'" + tbqty46.getText() + "'" + "," +
                                " fct_wkqty47 = " + "'" + tbqty47.getText() + "'" + "," +
                                " fct_wkqty48 = " + "'" + tbqty48.getText() + "'" + "," +
                                " fct_wkqty49 = " + "'" + tbqty49.getText() + "'" + "," +
                                " fct_wkqty50 = " + "'" + tbqty50.getText() + "'" + "," +
                                " fct_wkqty51 = " + "'" + tbqty51.getText() + "'" + "," +
                                " fct_wkqty52 = " + "'" + tbqty52.getText() + "'" + 
                             " where fct_part = " + "'" + tbpart.getText() + "'" +
                              " AND fct_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                              " AND fct_year = " + "'" + ddyear.getSelectedItem().toString() + "'"
                            + ";");
                      
                        st.executeUpdate("delete from plan_mstr where plan_part = " +  "'" + tbpart.getText() + "'"
                             + " AND plan_site = " + "'" + ddsite.getSelectedItem().toString() + "'" 
                                + " AND plan_is_sched = '0' " 
                                + ";");
                        
                      bsmf.MainFrame.show("Updated Forecast Record");
                      initvars("");
                    } 
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Edit to fct_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_tbeditActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JButton tbadd;
    private javax.swing.JButton tbdelete;
    private javax.swing.JButton tbedit;
    private javax.swing.JButton tbget;
    private javax.swing.JTextField tbpart;
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
