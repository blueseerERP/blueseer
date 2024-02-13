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
package com.blueseer.utl;
import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.tags;
import com.blueseer.adm.admData;
import com.blueseer.adm.admData.change_log;
import static com.blueseer.edi.EDI.edilog;
import static com.blueseer.utl.OVData.getCodeValueByCodeKey;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 *
 * @author vaughnte
 */
public class BlueSeerUtils {
    
    public enum dbaction {
        add, update, get, delete, run, addItem, updateItem, deleteItem
    }
    
   
    
    public static DateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat bsdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static ImageIcon clickchange = new ImageIcon(BlueSeerUtils.class.getResource("/images/change.png")); 
    public static ImageIcon clickflag = new ImageIcon(BlueSeerUtils.class.getResource("/images/flag.png")); 
    public static ImageIcon clickbasket = new ImageIcon(BlueSeerUtils.class.getResource("/images/basket.png")); 
    public static ImageIcon clickfind = new ImageIcon(BlueSeerUtils.class.getResource("/images/find.png")); 
    public static ImageIcon clicklookup = new ImageIcon(BlueSeerUtils.class.getResource("/images/lookup.png")); 
    public static  ImageIcon clickprint = new ImageIcon(BlueSeerUtils.class.getResource("/images/print.png"));
    public static  ImageIcon clickclock = new ImageIcon(BlueSeerUtils.class.getResource("/images/clock.png"));
    public static  ImageIcon clickchart = new ImageIcon(BlueSeerUtils.class.getResource("/images/chart.png"));
    public static  ImageIcon clickcoffee = new ImageIcon(BlueSeerUtils.class.getResource("/images/coffee.png"));
    public static  ImageIcon clickgear = new ImageIcon(BlueSeerUtils.class.getResource("/images/gear.png"));
    public static  ImageIcon clicktrash = new ImageIcon(BlueSeerUtils.class.getResource("/images/trash.png"));
    public static  ImageIcon clickrefresh = new ImageIcon(BlueSeerUtils.class.getResource("/images/refresh.png"));
    public static  ImageIcon clickvoid = new ImageIcon(BlueSeerUtils.class.getResource("/images/void.png"));
    public static  ImageIcon clickmail = new ImageIcon(BlueSeerUtils.class.getResource("/images/mail.png"));
    public static  ImageIcon clicklock = new ImageIcon(BlueSeerUtils.class.getResource("/images/lock.png"));
    public static  ImageIcon clickcheck = new ImageIcon(BlueSeerUtils.class.getResource("/images/check.png"));
    public static  ImageIcon clickcheckblue = new ImageIcon(BlueSeerUtils.class.getResource("/images/checkblue.png"));
    public static  ImageIcon clickcheckyellow = new ImageIcon(BlueSeerUtils.class.getResource("/images/checkyellow.png"));
    public static  ImageIcon clicknocheck = new ImageIcon(BlueSeerUtils.class.getResource("/images/nocheck.png"));
    public static  ImageIcon clickleftdoc = new ImageIcon(BlueSeerUtils.class.getResource("/images/leftdoc.png"));
    public static  ImageIcon clickrightdoc = new ImageIcon(BlueSeerUtils.class.getResource("/images/rightdoc.png"));
    
    
    
    public static String addRecordInit = getMessageTag(1005);
    public static String getRecordSuccess = getMessageTag(1006);
    public static String addRecordSuccess = getMessageTag(1007);
    public static String updateRecordSuccess = getMessageTag(1008);
    public static String deleteRecordSuccess = getMessageTag(1009);
    
    public static String noRecordFound = getMessageTag(1001);
    public static String getRecordError = getMessageTag(1010);
    public static String addRecordError = getMessageTag(1011);
    public static String updateRecordError = getMessageTag(1012);
    public static String deleteRecordError = getMessageTag(1013);
    
    public static String addRecordAlreadyExists = getMessageTag(1014);
    public static String deleteRecordCanceled = getMessageTag(1015);
    
    public static String getRecordSQLError = getMessageTag(1016);
    public static String addRecordSQLError = getMessageTag(1017);
    public static String updateRecordSQLError = getMessageTag(1018);
    public static String deleteRecordSQLError = getMessageTag(1019);
    
    public static String getRecordConnError = getMessageTag(1020);
    public static String addRecordConnError = getMessageTag(1021);
    public static String updateRecordConnError = getMessageTag(1022);
    public static String deleteRecordConnError = getMessageTag(1023);
    
    public static String SuccessBit = "0";
    public static String ErrorBit = "1";
    
    // lookup variables
        public static javax.swing.table.DefaultTableModel luModel = null;
        public static JTable luTable = new JTable();
        public static MouseListener luml = null;
        public static ActionListener lual = null;
        public static JDialog ludialog = new JDialog();
        public static ButtonGroup lubg = null;
        public static JRadioButton lurb1 = null;
        public static JRadioButton lurb2 = null;
        public static JRadioButton lurb3 = null;
        public static JRadioButton lurb4 = null;
        public static JRadioButton lurb5 = null;
        public static JTextField luinput = new JTextField(20);
    
    
    public static void callCountrySet() {
        
        JDialog countrydialog = new JDialog();
        countrydialog.setTitle(getMessageTag(1153));
        countrydialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        javax.swing.JComboBox ddcountries = new javax.swing.JComboBox<>();
        javax.swing.JButton btcommit = new javax.swing.JButton();
        
        ddcountries.removeAllItems();
        ArrayList<String> countries = OVData.getCodeMstrValueList("country");
      //  ArrayList<String> curr = OVData.getCurrlist();
        Collections.sort(countries);
        for (int i = 0; i < countries.size(); i++) {
            if (countries.get(i).equals("ALL"))
                continue;
            ddcountries.addItem(countries.get(i));
        }
        
        btcommit.setText(getGlobalProgTag("commit"));
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              admData.updateDefaultCountry(OVData.getCodeMstrKeyFromCodeValue("country",ddcountries.getSelectedItem().toString()));
              countrydialog.dispose();
              bsmf.MainFrame.show(getMessageTag(1165));
              System.exit(0);
            }
        });
        
        JPanel panel = new JPanel();
        /*
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(ddcountries, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(btcommit, gbc);
        */
        
        panel.add(ddcountries);
        panel.add(btcommit);
        countrydialog.add(panel);
        countrydialog.setPreferredSize(new Dimension(300, 200));
        countrydialog.pack();
        countrydialog.setLocationRelativeTo( null );
        countrydialog.setResizable(false);
        countrydialog.setVisible(true);
        ddcountries.requestFocus();
    } 
    
    public static void callChangeDialog(String x, String y) {
        
        String[] keys = new String[]{x,y};
        JDialog changedialog = new JDialog();
        changedialog.setTitle("Change Logging...");
        changedialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        javax.swing.JTextArea ta = new javax.swing.JTextArea();
        
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        
              
        ArrayList<change_log> changes = admData.getChangeLog(keys);
        ta.setText("Change Log:  " + "\n\n");
        for (change_log cl : changes) {
            ta.append("TimeStamp: " + cl.chg_ts() + "\n" + "User: " +  cl.chg_userid() + "\n" +  "Change: " +  cl.chg_desc() + "\n\n");
        }
        ta.setCaretPosition(0);
        ta.setEditable(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints panelGBC = new GridBagConstraints();

        panelGBC.weightx = 1;                    //I want to fill whole panel with JTextArea
        panelGBC.weighty = 1;                    //so both weights =1
        panelGBC.fill = GridBagConstraints.BOTH; //and fill is set to BOTH
        
        
        
        panel.add(scroll, panelGBC);
        changedialog.add(panel);
        changedialog.setPreferredSize(new Dimension(400, 300));
        changedialog.pack();
        changedialog.setLocationRelativeTo( null );
        changedialog.setResizable(false);
        changedialog.setVisible(true);
    } 
    
    public static void callDialog() {
        
        if (ludialog != null) {
            ludialog.dispose();
        }
        /* 
        if (luModel != null && luModel.getRowCount() > 0) {
        luModel.setRowCount(0);
        luModel.setColumnCount(0);
        }
        */
        luTable.setPreferredScrollableViewportSize(new Dimension(300,200));
        JScrollPane scrollPane = new JScrollPane(luTable);
       
        ludialog = new JDialog();
        ludialog.setTitle("ASCII Chart:");
        ludialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(false);
        ludialog.setVisible(true);
    } 
        
    public static void callDialog(String rb1) {
        
         if (ludialog != null) {
            ludialog.dispose();
        }
        if (luModel != null && luModel.getRowCount() > 0) {
        luModel.setRowCount(0);
        luModel.setColumnCount(0);
        }
        
        luinput.setText("");
        
        luTable.setPreferredScrollableViewportSize(new Dimension(500,200));
        luTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(luTable);
        JPanel rbpanel = new JPanel();
        lubg = new ButtonGroup();
        lurb1 = new JRadioButton(rb1);
        lurb1.setSelected(true);
        BoxLayout radiobuttonpanellayout = new BoxLayout(rbpanel, BoxLayout.X_AXIS);
        rbpanel.setLayout(radiobuttonpanellayout);
        rbpanel.add(lurb1);
        lubg.add(lurb1);
        lubg.add(lurb2);
        
        
        ludialog = new JDialog();
        ludialog.setTitle("Search By Text and Press Enter:");
        ludialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(false);
        ludialog.setVisible(true);
        luinput.requestFocus();
    } 
            
    public static void callDialog(String rb1, String rb2) {
        
         if (ludialog != null) {
            ludialog.dispose();
        }
        if (luModel != null && luModel.getRowCount() > 0) {
        luModel.setRowCount(0);
        luModel.setColumnCount(0);
        }
        luinput.setText("");
        luTable.setPreferredScrollableViewportSize(new Dimension(500,200));
        luTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(luTable);
        JPanel rbpanel = new JPanel();
        lubg = new ButtonGroup();
        lurb1 = new JRadioButton(rb1);
        lurb2 = new JRadioButton(rb2);
        lurb1.setSelected(true);
        lurb2.setSelected(false);
        BoxLayout radiobuttonpanellayout = new BoxLayout(rbpanel, BoxLayout.X_AXIS);
        rbpanel.setLayout(radiobuttonpanellayout);
        rbpanel.add(lurb1);
        JLabel spacer = new JLabel("   ");
        rbpanel.add(spacer);
        rbpanel.add(lurb2);
        lubg.add(lurb1);
        lubg.add(lurb2);
        
        
        ludialog = new JDialog();
        ludialog.setTitle("Search By Text and Press Enter:");
        ludialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(false);
        ludialog.setVisible(true);
        luinput.requestFocus();
    } 
     
    public static void callDialog(String rb1, String rb2, String rb3) {
        
         if (ludialog != null) {
            ludialog.dispose();
        }
        if (luModel != null && luModel.getRowCount() > 0) {
        luModel.setRowCount(0);
        luModel.setColumnCount(0);
        }
        luinput.setText("");
        luTable.setPreferredScrollableViewportSize(new Dimension(500,200));
        luTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(luTable);
        JPanel rbpanel = new JPanel();
        
        lubg = new ButtonGroup();
        lurb1 = new JRadioButton(rb1);
        lurb2 = new JRadioButton(rb2);
        lurb3 = new JRadioButton(rb3);
        
        lurb1.setSelected(true);
        lurb2.setSelected(false);
        lurb3.setSelected(false);
        BoxLayout radiobuttonpanellayout = new BoxLayout(rbpanel, BoxLayout.X_AXIS);
        rbpanel.setLayout(radiobuttonpanellayout);
        JLabel spacer1 = new JLabel("   ");
        JLabel spacer2 = new JLabel("   ");
        rbpanel.add(lurb1);        
        rbpanel.add(spacer1);
        rbpanel.add(lurb2);
        rbpanel.add(spacer2);
        rbpanel.add(lurb3);
        lubg.add(lurb1);
        lubg.add(lurb2);
        lubg.add(lurb3);
        
        
        ludialog = new JDialog();
        ludialog.setTitle("Search By Text and Press Enter:");
        ludialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(false);
        ludialog.setVisible(true);
        luinput.requestFocus();
    } 
    
    public static void callDialog(String rb1, String rb2, String rb3, String rb4) {
        
         if (ludialog != null) {
            ludialog.dispose();
        }
        if (luModel != null && luModel.getRowCount() > 0) {
        luModel.setRowCount(0);
        luModel.setColumnCount(0);
        }
        luinput.setText("");
        luTable.setPreferredScrollableViewportSize(new Dimension(500,200));
        luTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(luTable);
        JPanel rbpanel = new JPanel();
        
        lubg = new ButtonGroup();
        lurb1 = new JRadioButton(rb1);
        lurb2 = new JRadioButton(rb2);
        lurb3 = new JRadioButton(rb3);
        lurb4 = new JRadioButton(rb4);
        
        lurb1.setSelected(true);
        lurb2.setSelected(false);
        lurb3.setSelected(false);
        lurb4.setSelected(false);
        BoxLayout radiobuttonpanellayout = new BoxLayout(rbpanel, BoxLayout.X_AXIS);
        rbpanel.setLayout(radiobuttonpanellayout);
        JLabel spacer1 = new JLabel("   ");
        JLabel spacer2 = new JLabel("   ");
        JLabel spacer3 = new JLabel("   ");
        rbpanel.add(lurb1);
        rbpanel.add(spacer1);
        rbpanel.add(lurb2);
        rbpanel.add(spacer2);
        rbpanel.add(lurb3);
        rbpanel.add(spacer3);
        rbpanel.add(lurb4);
        lubg.add(lurb1);
        lubg.add(lurb2);
        lubg.add(lurb3);
        lubg.add(lurb4);
        
        
        ludialog = new JDialog();
        ludialog.setTitle("Search By Text and Press Enter:");
        ludialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(false);
        ludialog.setVisible(true);
        luinput.requestFocus();
    } 
    
    public static void callDialog(String rb1, String rb2, String rb3, String rb4, String rb5) {
        
         if (ludialog != null) {
            ludialog.dispose();
        }
        if (luModel != null && luModel.getRowCount() > 0) {
        luModel.setRowCount(0);
        luModel.setColumnCount(0);
        }
        luinput.setText("");
        luTable.setPreferredScrollableViewportSize(new Dimension(500,200));
        luTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(luTable);
        JPanel rbpanel = new JPanel();
        
        lubg = new ButtonGroup();
        lurb1 = new JRadioButton(rb1);
        lurb2 = new JRadioButton(rb2);
        lurb3 = new JRadioButton(rb3);
        lurb4 = new JRadioButton(rb4);
        lurb5 = new JRadioButton(rb5);
        
        lurb1.setSelected(true);
        lurb2.setSelected(false);
        lurb3.setSelected(false);
        lurb4.setSelected(false);
        lurb5.setSelected(false);
        BoxLayout radiobuttonpanellayout = new BoxLayout(rbpanel, BoxLayout.X_AXIS);
        rbpanel.setLayout(radiobuttonpanellayout);
        JLabel spacer1 = new JLabel("   ");
        JLabel spacer2 = new JLabel("   ");
        JLabel spacer3 = new JLabel("   ");
        JLabel spacer4 = new JLabel("   ");
        rbpanel.add(lurb1);
        rbpanel.add(spacer1);
        rbpanel.add(lurb2);
        rbpanel.add(spacer2);
        rbpanel.add(lurb3);
        rbpanel.add(spacer3);
        rbpanel.add(lurb4);
        rbpanel.add(spacer4);
        rbpanel.add(lurb5);
        lubg.add(lurb1);
        lubg.add(lurb2);
        lubg.add(lurb3);
        lubg.add(lurb4);
        lubg.add(lurb5);
        
        
        ludialog = new JDialog();
        ludialog.setTitle("Search By Text and Press Enter:");
        ludialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(false);
        ludialog.setVisible(true);
        luinput.requestFocus();
    } 
    
    public static double bsParseDouble(String x) {
        // always returns . decimal based double
        double z = 0.00;
        
        
        if (! x.isEmpty()) {
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        if (Locale.getDefault().getLanguage().equals("ar") && x.contains(".")) { // if AR locale and US keyboard "." then change decimal separator
            x = x.replace('.', '\u066B'); 
        } 
        if (Locale.getDefault().getLanguage().equals("ar") && x.startsWith("-")) {
            x = x.substring(1) + "-";
        }
        
        Number number = 0.00;
                    try {
                        number = nf.parse(x.trim());
                    } catch (ParseException ex) {
                        bsmf.MainFrame.show(getMessageTag(1017) + "/d  " + x);
                        ex.printStackTrace();
                    }
             z =  number.doubleValue();
        }
        return z;
    }
    
    public static double bsParseDoubleUS(String x) {
        double z = 0;
        if (! x.isEmpty()) {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        Number number = 0;
                    try {
                        number = nf.parse(x);
                    } catch (ParseException ex) {
                        bsmf.MainFrame.show(getMessageTag(1017));
                    }
        z = number.doubleValue();
        }
        return z;
    }
    
    public static int bsParseInt(String x) {
        // always returns . decimal based double
        int z = 0;
        if (! x.isEmpty()) {
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        
        Number number = 0;
                    try {
                        number = nf.parse(x.trim());
                    } catch (ParseException ex) {
                        bsmf.MainFrame.show(getMessageTag(1017) + "/ " + x);
                    }
             z =  number.intValue();
        }
        return z;
    }
    
    
    public static String bsFormatDouble(double invalue, String precision) {
        String pattern = "";
        String outvalue = "";
        
       
        if (precision.equals("2")) {
         pattern = "#0.00"; 
        } else if (precision.equals("3")) {
         pattern = "#0.000";  
        } else if (precision.equals("4")) {
         pattern = "#0.0000";   
        } else if (precision.equals("5")) {
         pattern = "#0.00000";    
        } else if (precision.equals("6")) {
         pattern = "#0.000000";  
        } else if (precision.equals("7")) {
         pattern = "#0.0000000";    
         } else if (precision.equals("0")) {
         pattern = "#0";    
        } else {
         pattern = "#0.00";    
        }
       
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        outvalue = df.format(invalue); 
        return outvalue;
    }
    
    public static String bsFormatDouble(double invalue) {
        String outvalue = "";
        String pattern = "#0.00"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        outvalue = df.format(invalue); 
        return outvalue;
    }
    
    public static String bsFormatDoubleZ(double invalue) {
        String outvalue = "";
        String pattern = "#0.#####"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        outvalue = df.format(invalue); 
        return outvalue;
    }
    
    
    public static String bsFormatInt(int invalue) {
        String outvalue = "";
        String pattern = "#"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        outvalue = df.format(invalue); 
        return outvalue;
    }
    
    
    public static String bsFormatDouble5(double invalue) {
        String outvalue = "";
        String pattern = "#0.00###"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        outvalue = df.format(invalue); 
        return outvalue;
    }
    
    
    
    public static String bsformat(String type, String invalue, String precision) {
        String pattern = "";
        String outvalue = "";
        if (invalue.isEmpty() && type.equals("")) {
           return "0";
        }
        if (invalue.isEmpty() && type.equals("s")) {
           return "";
        }
        if (invalue.isEmpty() && type.equals("i")) {
           return "0";
        }
        if (invalue.isEmpty() && type.equals("d")) {
           invalue = "0"; // for use down below
        }
        if (precision.equals("2")) {
         pattern = "#0.00"; 
        } else if (precision.equals("3")) {
         pattern = "#0.000";  
        } else if (precision.equals("4")) {
         pattern = "#0.0000";   
        } else if (precision.equals("5")) {
         pattern = "#0.00000";    
         } else if (precision.equals("0")) {
         pattern = "#0";    
        } else {
         pattern = "#0.00";    
        }
       
        if (Locale.getDefault().getLanguage().equals("ar") && invalue.contains(".")) { // if AR locale and US keyboard "." then change decimal separator
            invalue = invalue.replace('.', '\u066B'); 
        } 
        if (Locale.getDefault().getLanguage().equals("ar") && invalue.startsWith("-")) {
            invalue = invalue.substring(1) + "-";
        }
       
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        
        try {   
            outvalue = df.format(df.parse(invalue));
        } catch (ParseException ex) {
            outvalue = "error";
        }
       
        return outvalue;
    }
    
    public static String bsformat(String invalue, String precision) {
        String pattern = "";
        String outvalue = "";
        
        if (invalue.isEmpty()) {
           return "0";
        }
        if (precision.equals("2")) {
         pattern = "#0.00"; 
        } else if (precision.equals("3")) {
         pattern = "#0.000";  
        } else if (precision.equals("4")) {
         pattern = "#0.0000";   
        } else if (precision.equals("5")) {
         pattern = "#0.00000";    
        } else if (precision.equals("0")) {
         pattern = "#0";   
        } else if (precision.equals("1")) {
         pattern = "#0.0";   
        } else {
         pattern = "#0.00";    
        }
       
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        try {   
            outvalue = df.format(df.parse(invalue));
        } catch (ParseException ex) {
            outvalue = "error";
        }
       
        return outvalue;
    }
    
    public static String bsNumber(double invalue) {
        String outvalue = "";
        String pattern = "#0.#####"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        outvalue = df.format(invalue); 
        return outvalue;
    }
    
    public static String bsNumber(String invalue) {
        // invalue will come over as a . decimal regardless of Locale
        // currformat will return 3,56 for the following scenarios if
        // default separator is ','   
        // currformat("3.56")
        // currformat("3,56") 
         
        String x = "0";
        String pattern = "#0.#####";
        if (! invalue.isEmpty()) {
        String adjvalue = invalue.replace('.', defaultDecimalSeparator);
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
     //  NumberFormat nf = NumberFormat.getInstance(Locale.getDefault()); 
       DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        try { 
            x = df.format(df.parse(adjvalue));
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
        return x;
    }
    
    public static String currformatWithSymbol(String invalue) {
        // invalue will come over as a . decimal regardless of Locale
        // currformat will return 3,56 for the following scenarios if
        // default separator is ','   
        // currformat("3.56")
        // currformat("3,56") 
         
        String x = "0";
        String pattern = "¤#0.00###"; 
        if (! invalue.isEmpty()) {
        String adjvalue = invalue.replace('.', defaultDecimalSeparator);
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
     //  NumberFormat nf = NumberFormat.getInstance(Locale.getDefault()); 
       DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        try { 
            x = df.format(df.parse(adjvalue));
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
        return x;
    }
    
    public static String currformat(String invalue) {
        // invalue will come over as a . decimal regardless of Locale
        // currformat will return 3,56 for the following scenarios if
        // default separator is ','   
        // currformat("3.56")
        // currformat("3,56") 
         
        String x = "0";
        String pattern = "#0.00###";
        if (! invalue.isEmpty()) {
        String adjvalue = invalue.replace('.', defaultDecimalSeparator);
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
     //  NumberFormat nf = NumberFormat.getInstance(Locale.getDefault()); 
       DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        try { 
            x = df.format(df.parse(adjvalue));
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
        return x;
    }
    
    
    public static String currformatDouble(double invalue) {
        String x = "";
        String pattern = "#0.00";
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        x = df.format(invalue);
        return x;
    }
    
    public static String currformatDoubleWithSymbol(double invalue, String currency) {
        String x = "";
        String pattern = "#0.00###";
        Currency c = Currency.getInstance(currency);
        String symbol = "$";
        if (! currency.equals("USD")) {
            symbol = c.getSymbol(bsmf.MainFrame.currencymap.get(c));
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
       
        x = symbol + df.format(invalue);
        
        return x;
    }
    
    
    public static String getCurrencySymbol(String currency) {
        String symbol = "$";
        Currency c = Currency.getInstance(currency);
        if (! currency.equals("USD")) {
            symbol = c.getSymbol(bsmf.MainFrame.currencymap.get(c));
        }
        return symbol;
    }
    
    public static Locale getCurrencyLocale(String currency) {
        Locale locale = null;
        Currency c = Currency.getInstance(currency);
        if (! currency.isBlank()) {
        locale = bsmf.MainFrame.currencymap.get(c);
        }
        if (locale == null || currency.equals("USD")) { // had to add USD override...currencymap was pulling locale with US prepended to $ sign
           locale = Locale.getDefault(); 
        }
        return locale;
    }
    
    public static String formatUSZ(String invalue) {
        // invalue will come over as a . decimal regardless of Locale
        String x = "0";
        String pattern = "#0.#####";
        if (! invalue.isEmpty()) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(pattern);
        try {   
            x = df.format(df.parse(invalue));
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
        return x;
    }
        
    public static String formatUS(String invalue) {
        // invalue will come over as a . decimal regardless of Locale
        String x = "0";
        String pattern = "#0.00###";
        if (! invalue.isEmpty()) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(pattern);
        try {   
            x = df.format(df.parse(invalue));
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
        return x;
    }
        
    public static String currformatDoubleUS(double invalue) {
        String x = "";
        String pattern = "#0.00###";
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(pattern);
        x = df.format(invalue);
        return x;
    }
    
    
    public static String priceformat(String invalue) {
        String x = "0";
        String pattern = "#0.0000#";
        String adjvalue = invalue.replace('.', defaultDecimalSeparator);
        if (! invalue.isEmpty()) {
       // DecimalFormat df = new DecimalFormat("#0.0000#", new DecimalFormatSymbols(Locale.US)); 
       DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        try {   
            x = df.format(df.parse(adjvalue));
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
        return x;
    }
    
    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
    
    
    public static String convertDateFormat(String format, String indate) {
       String mydate = "";
        if (format.equals("yyyyMMdd") && indate.length() == 8) {
           mydate = indate.substring(0,4) + "-" + indate.substring(4,6) + "-" + indate.substring(6);
        }
        if (format.equals("yyMMdd") && indate.length() == 6 ) {
           mydate = "20" + indate.substring(0,2) + "-" + indate.substring(2,4) + "-" + indate.substring(4);
        }
        if (format.equals("yyyy-MM-dd hh:mm:ss") && indate.length() == 19) {
           mydate = indate.substring(0,10);
        }
       return mydate;
    }
    
    public static String transformDocToString(Document document) throws TransformerConfigurationException, TransformerException {
        String xml = "";
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        StringWriter sw = new StringWriter();
        trans.transform(new DOMSource(document), new StreamResult(sw)); 
        xml = sw.toString();
        return xml;
    }
   
    public static boolean isClassFile(String myfile) {
         // lets check and see if class exists in package
       try {
           Class.forName(myfile);
           return true;
           
       } catch( ClassNotFoundException e ) {
           return false;
        //my class isn't there!
       }
    }
    
    public static boolean isEDIClassFile(String myfile) {
         // lets check and see if class exists in package
       URLClassLoader cl = null;
       try {
           List<File> jars = Arrays.asList(new File("edi/maps").listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar");
                }
                }));
                URL[] urls = new URL[jars.size()];
                for (int i = 0; i < jars.size(); i++) {
                try {
                  urls[i] = jars.get(i).toURI().toURL();
                } catch (Exception e) {
                    edilog(e);
                }
                }
               cl = new URLClassLoader(urls);
               Class.forName(myfile,true,cl);
              return true;
           
       } catch( ClassNotFoundException e ) {
           edilog(e);
           return false;
           
        //my class isn't there!
       } finally {
           if (cl != null) {
               try {
                   cl.close();
               } catch (IOException ex) {
                   edilog(ex);
               }
           }
       }
    }
    
    public static URLClassLoader getEDIClassLoader() {
         // lets check and see if class exists in package
       URLClassLoader cl = null;
       List<File> jars = Arrays.asList(new File("edi/maps").listFiles(new FilenameFilter() {
           public boolean accept(File dir, String name) {
               return name.toLowerCase().endsWith(".jar");
           }
       })); //my class isn't there!
       URL[] urls = new URL[jars.size()];
       for (int i = 0; i < jars.size(); i++) {
           try {
               urls[i] = jars.get(i).toURI().toURL();
           } catch (Exception e) {
               edilog(e);
           }
       }
       cl = new URLClassLoader(urls);
       return cl;
    }
    
    
    public static boolean isFile(String dir, String file) {
        Path mypath = FileSystems.getDefault().getPath(dir + "/" + file);
            if (! mypath.toFile().exists()) {
                return false;
            } else {
                return true;
            }
    }
    
    public static boolean isFile(String filepath) {
        Path mypath = FileSystems.getDefault().getPath(filepath);
            if (! mypath.toFile().exists()) {
                return false;
            } else {
                return true;
            }
    }
    
    public static boolean isSet(ArrayList list, Integer index) {
     return index != null && index >=0 && index < list.size() && list.get(index) != null;
     }
    
    public static boolean isSet(String[] list, Integer index) {
     return index != null && index >=0 && index < list.length && list[index] != null;
     }
     
    public static boolean ConvertStringToBool(String i) {
        return (i.equals("1") || i.toLowerCase().equals("yes") || i.toLowerCase().equals("true")) ? true : false;
    }
     
    public static boolean ConvertIntegerToBool(int i) {
        return(i == 1) ? true : false;
    }
    
    public static boolean ConvertTrueFalseToBoolean(String x) {
        return (x.toLowerCase().equals("true") || x.toLowerCase().equals("yes")) ? true : false;
    }
    
    public static String ConvertTrueFalseToStringInt(String x) {
        return (x.toLowerCase().equals("true") || x.toLowerCase().equals("yes")) ? "1" : "0";
    }
    
    
    public static String ConvertIntToYesNo(int i) {
        return (i == 1) ? "YES" : "NO";
    }
    
    public static String ConvertBoolToYesNo(boolean x) {
        return (x) ? "YES" : "NO";
    }
    
    
    public static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
    
    public static String boolToString(boolean b) {
        return b ? String.valueOf(1) : String.valueOf(0);
    }

    public static int checkLength(Map<String, Integer> m, String f) {
        int x = 0;
        if (m != null && m.get(f) != null) {
            x = m.get(f);
        }
        return x;
    }
    
    public static String xNull(String mystring) {
       String returnstring = "";
       returnstring = (mystring == null) ? "" : mystring;
       return returnstring;
   }
      
    public static String xZero(String mystring) {
       String returnstring = (mystring.isBlank()) ? "0" : mystring;
       return returnstring;
   }
    
    
    public static String convertToX(boolean i) {
        String mystring = null;
        if (i) {
            mystring = "X";
        } else {
            mystring = "";
        }
        return mystring;

    }

    public static boolean isParsableToInt(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public static boolean isParsableToDouble(String i) {
        try {
           // Double.parseDouble(i);
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
            df.parse(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        } catch (ParseException ex) {
            return false;
        }
    }
    
    public static boolean isParsableToBoolean(String i) {
        try {
            Boolean.parseBoolean(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    
    public static Date parseDate(String indate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date r = null;
        if (indate != null && ! indate.isEmpty() && ! indate.equals("0000-00-00") && ! indate.equals("null")) {
            try {
                r = sdf.parse(indate);
            } catch (ParseException ex) {
                bsmf.MainFrame.show("parseDate Exception");
            }
        }
        return r;
    }
    
    public static Date parseDate(String indate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());  // "yyyy-MM-dd"
        Date r = null;
        if (indate != null && ! indate.isEmpty()) {
            try {
                r = sdf.parse(indate);
            } catch (ParseException ex) {
                bsmf.MainFrame.show("parseDate Exception");
            }
        }
        return r;
    }
   
    public static String getDateDB(String indate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String r = "";
        if (indate != null && ! indate.isEmpty() && ! indate.equals("0000-00-00") && ! indate.equals("null")) {
            try {
                r = sdf.format(sdf.parse(indate));
            } catch (ParseException ex) {
                bsmf.MainFrame.show("getDateDB Exception");
            }
        }
        return r;
    }
    
    
    public static String setDateDB(Date date) {
       String mydate = null;
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("en-US"));
       if (date == null) {
           return mydate;
       } else {
           return sdf.format(date);
       }
    }
    
    
    public static String setDateFormat(Date date) {
       String mydate = "";
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       if (date == null) {
           return mydate;
       } else {
           return sdf.format(date);
       }
    }
     
    public static String setDateFormatNull(Date date) {
       String mydate = null;
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       if (date == null) {
           return mydate;
       } else {
           return sdf.format(date);
       }
    }
    
    
    public static boolean isValidDateStr(String date) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      sdf.setLenient(false);
      sdf.parse(date);
    }
    catch (ParseException e) {
      return false;
    }
    catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }
    
    public static boolean isMoneyFormat(String value) {
     boolean myreturn = false;
     int i = value.lastIndexOf('.');
     if(i != -1 && value.substring(i + 1).length() == 2)
         myreturn = true;
     return myreturn;
  }
    
    public class LineWrapCellRenderer extends JTextArea implements TableCellRenderer {
        int rowHeight = 0;  // current max row height for this scan
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column)
        {
            setText((String) value);
            setWrapStyleWord(true);
            setLineWrap(true);
          //  setBackground(Color.YELLOW);

          // current table column width in pixels
        int colWidth = table.getColumnModel().getColumn(column).getWidth();

        // set the text area width (height doesn't matter here)
        setSize(new Dimension(colWidth, 1)); 

        // get the text area preferred height and add the row margin
        int height = getPreferredSize().height + table.getRowMargin();

            // ensure the row height fits the cell with most lines
            if (column == 0 || height > rowHeight) {
                table.setRowHeight(row, height);
                rowHeight = height;
            }
           return this;
            }

}
    
    public static class FormatRenderer extends DefaultTableCellRenderer
{
	private Format formatter;

	/*
	 *   Use the specified formatter to format the Object
	 */
	public FormatRenderer(Format formatter)
	{
		this.formatter = formatter;
	}

	public void setValue(Object value)
	{
		//  Format the Object before setting its value in the renderer

		try
		{
			if (value != null)
				value = formatter.format(value);
		}
		catch(IllegalArgumentException e) {}

		super.setValue(value);
	}

	/*
	 *  Use the default date/time formatter for the default locale
	 */
	
}
  
    public static class NumberRenderer extends FormatRenderer
{
	/*
	 *  Use the specified number formatter and right align the text
	 */
	public NumberRenderer(NumberFormat formatter)
	{
                
		super(formatter);
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(5);
		setHorizontalAlignment( SwingConstants.RIGHT );
                
	}

        
        public static NumberRenderer getNumberRenderer()
	{
		return new NumberRenderer( NumberFormat.getNumberInstance());
	}
	/*
	 *  Use the default currency formatter for the default locale
	 */
	public static NumberRenderer getCurrencyRenderer()
	{
          return new NumberRenderer( NumberFormat.getCurrencyInstance());
        }
        
        public static NumberRenderer getCurrencyRenderer(Locale locale)
	{
          return new NumberRenderer( NumberFormat.getCurrencyInstance(locale));
        }

	/*
	 *  Use the default integer formatter for the default locale
	 */
	public static NumberRenderer getIntegerRenderer()
	{
		return new NumberRenderer( NumberFormat.getIntegerInstance() );
	}

	/*
	 *  Use the default percent formatter for the default locale
	 */
	public static NumberRenderer getPercentRenderer()
	{
		return new NumberRenderer( NumberFormat.getPercentInstance() );
	}
}
  
    public static class bsTree<T> {
	 
    private bsNode<T> rootElement;
    
    public bsTree() {
        super();
    }
 
    /**
     * Return the root Node of the tree.
     * @return the root element.
     */
    public bsNode<T> getRootElement() {
        return this.rootElement;
    }
 
    /**
     * Set the root Element for the tree.
     * @param rootElement the root element to set.
     */
    public void setRootElement(bsNode<T> rootElement) {
        this.rootElement = rootElement;
    }
     
    /**
     * Returns the Tree<T> as a List of Node<T> objects. The elements of the
     * List are generated from a pre-order traversal of the tree.
     * @return a List<Node<T>>.
     */
    public List<bsNode<T>> toList() {
        List<bsNode<T>> list = new ArrayList<bsNode<T>>();
        walk(rootElement, list);
        return list;
    }
     
    /**
     * Returns a String representation of the Tree. The elements are generated
     * from a pre-order traversal of the Tree.
     * @return the String representation of the Tree.
     */
    public String toString() {
        return toList().toString();
    }
     
    /**
     * Walks the Tree in pre-order style. This is a recursive method, and is
     * called from the toList() method with the root element as the first
     * argument. It appends to the second argument, which is passed by reference     * as it recurses down the tree.
     * @param element the starting element.
     * @param list the output of the walk.
     */
    private void walk(bsNode<T> element, List<bsNode<T>> list) {
        list.add(element);
        for (bsNode<T> data : element.getChildren()) {
            walk(data, list);
        }
    }
}
    
    public static class bsNode<T> {
 
    public T data;
    public List<bsNode<T>> children;
 
    /**
     * Default ctor.
     */
    public bsNode() {
        super();
    }
 
    /**
     * Convenience ctor to create a Node<T> with an instance of T.
     * @param data an instance of T.
     */
    public bsNode(T data) {
        this();
        setData(data);
    }
     
    /**
     * Return the children of Node<T>. The Tree<T> is represented by a single
     * root Node<T> whose children are represented by a List<Node<T>>. Each of
     * these Node<T> elements in the List can have children. The getChildren()
     * method will return the children of a Node<T>.
     * @return the children of Node<T>
     */
    public List<bsNode<T>> getChildren() {
        if (this.children == null) {
            return new ArrayList<bsNode<T>>();
        }
        return this.children;
    }
 
    /**
     * Sets the children of a Node<T> object. See docs for getChildren() for
     * more information.
     * @param children the List<Node<T>> to set.
     */
    public void setChildren(List<bsNode<T>> children) {
        this.children = children;
    }
 
    /**
     * Returns the number of immediate children of this Node<T>.
     * @return the number of immediate children.
     */
    public int getNumberOfChildren() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }
     
    /**
     * Adds a child to the list of children for this Node<T>. The addition of
     * the first child will create a new List<Node<T>>.
     * @param child a Node<T> object to set.
     */
    public void addChild(bsNode<T> child) {
        if (children == null) {
            children = new ArrayList<bsNode<T>>();
        }
        children.add(child);
    }
     
    /**
     * Inserts a Node<T> at the specified position in the child list. Will     * throw an ArrayIndexOutOfBoundsException if the index does not exist.
     * @param index the position to insert at.
     * @param child the Node<T> object to insert.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void insertChildAt(int index, bsNode<T> child) throws IndexOutOfBoundsException {
        if (index == getNumberOfChildren()) {
            // this is really an append
            addChild(child);
            return;
        } else {
            children.get(index); //just to throw the exception, and stop here
            children.add(index, child);
        }
    }
     
    /**
     * Remove the Node<T> element at index index of the List<Node<T>>.
     * @param index the index of the element to delete.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void removeChildAt(int index) throws IndexOutOfBoundsException {
        children.remove(index);
    }
 
    public T getData() {
        return this.data;
    }
 
    public void setData(T data) {
        this.data = data;
    }
     
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(getData().toString()).append(",[");
        int i = 0;
        for (bsNode<T> e : getChildren()) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(e.getData().toString());
            i++;
        }
        sb.append("]").append("}");
        return sb.toString();
    }
}

    
     public static String getGlobalProgTag(String key) {
         String tag = "";
          if (tags != null && tags.containsKey("global.prog." + key)) {
            tag = tags.getString("global.prog." + key);
          }
         return tag;
     }
     
     public static String getGlobalLabelTag(String key) {
         String tag = "";
          if (tags != null && tags.containsKey("global.label." + key)) {
            tag = tags.getString("global.label." + key);
          }
         return tag;
     }
     
     public static String getGlobalMenuTag(String key) {
         String tag = "";
          if (tags != null && tags.containsKey("global.menu." + key)) {
            tag = tags.getString("global.menu." + key);
          }
         return tag;
     }
    
     public static String getGlobalColumnTag(String key) {
         String tag = "";
          if (tags != null && tags.containsKey("global.column." + key)) {
            tag = tags.getString("global.column." + key);
          }
         return tag;
     }
         
     public static String getClassLabelTag(String key, String thisclass) {
         String tag = "";
          if (tags != null && tags.containsKey(thisclass + ".label." + key)) {
            tag = tags.getString(thisclass + ".label." + key);
          }
         return tag;
     }
    
     public static String getMessageTag(int key, String thisclass) {
         String tag = "";
          if (tags != null && tags.containsKey("global.message." + key)) {              
              tag = MessageFormat.format(tags.getString("global.message." + key).replace("'", "''"), thisclass);
              tag = (Locale.getDefault().getLanguage().equals("en")) ? tag : String.valueOf(key) + ": " + tag;
          }
         return tag;
     }
    
     public static String getMessageTag(int key) {
         String tag = "";
          if (tags != null && tags.containsKey("global.message." + key)) {
            tag = tags.getString("global.message." + key);
            tag = (Locale.getDefault().getLanguage().equals("en")) ? tag : String.valueOf(key) + ": " + tag;
          }
         return tag;
     }
    
     public static String getTitleTag(int key) {
         String tag = "";
          if (tags != null && tags.containsKey("global.title." + key)) {
            tag = tags.getString("global.title." + key);
          }
         return tag;
     }
    
    
     public static void startTask(String[] message) {
        bsmf.MainFrame.MainProgressBar.setVisible(true);
        bsmf.MainFrame.MainProgressBar.setIndeterminate(true);
        bsmf.MainFrame.MainProgressBar.setBackground(Color.BLUE);
        message(message);
     }
     
     
     public static void endTask(String[] message) {
        bsmf.MainFrame.MainProgressBar.setVisible(false);
        bsmf.MainFrame.MainProgressBar.setIndeterminate(false);
        message(message);
     }
          
     public static void message(String[] message) {
         
         if (message.length != 2) {
           bsmf.MainFrame.messagelabel.setText("message arguments missing: " + message);
           bsmf.MainFrame.messagelabel.setForeground(Color.RED);  
           return;
         }
          bsmf.MainFrame.messagelabel.setText(message[1]);   
         if (message[0].equals("1")) {
            bsmf.MainFrame.messagelabel.setForeground(Color.RED); 
         } else if (message[0].equals("2")) {
            bsmf.MainFrame.messagelabel.setForeground(Color.decode("#006600")); 
         } else if (message[0].equals("3")) {
            bsmf.MainFrame.messagelabel.setForeground(Color.decode("#6600CC")); 
         } else if (message[0].equals("0")) {
            bsmf.MainFrame.messagelabel.setForeground(Color.BLUE);  
         } else {
            bsmf.MainFrame.messagelabel.setForeground(Color.BLACK);   
         }
     }
     
     public static String[] bsret(String message) {
         String[] r = new String[]{"0",""};
         if (! message.isBlank()) {
         r[0] = "1";
         r[1] = message;
         }
         return r;
     }
     
     public static String[] bsret(String status, String message) {
         String[] r = new String[]{status,""};
         if (! message.isBlank()) {
         r[1] = message;
         } else {
             r[1] = "Transaction Complete";
         }
         return r;
     }
     
     public static void messagereset() {
         bsmf.MainFrame.messagelabel.setForeground(Color.BLACK);
         bsmf.MainFrame.messagelabel.setText("");
         
     }
    
     public static class MessageXML {
         public static Document createRoot(Document doc) {
        Element rootElement = doc.createElement("Document");
        doc.appendChild(rootElement);        
        return doc;
        }
         
         public static Document createBody(Document doc, String message, String status, String key ) {
        
        Element header = doc.createElement("Message");
        doc.getDocumentElement().appendChild(header);
       
        Element e = doc.createElement("Status");
                        e.appendChild(doc.createTextNode(status));
        header.appendChild(e);
        
        e = doc.createElement("ReturnKey");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(key)));
        header.appendChild(e);
       
        e = doc.createElement("Description");
                        e.appendChild(doc.createTextNode(BlueSeerUtils.xNull(message)));
        header.appendChild(e);
       
        return doc;
    }
     
    }
    
     
     public static String createMessage(String status, String message, String key) throws TransformerException {
        String x = "";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
         try {
             docBuilder = docFactory.newDocumentBuilder();
         } catch (ParserConfigurationException ex) {
             bsmf.MainFrame.bslog(ex);
             bsmf.MainFrame.show(ex.getMessage());
         }
        Document doc = docBuilder.newDocument();
        
        doc = MessageXML.createRoot(doc);
        
        doc = MessageXML.createBody(doc, message, status, key);
        
        x = BlueSeerUtils.transformDocToString(doc);
        
        return x;
    }
    
     public static String createMessageJSON(String status, String message, String key) throws TransformerException {
        String x = "{\"Status\":" + "\"" + status + "\"" + "," +
                   "\"Message\":" + "\"" + message + "\"" + "," + 
                   "\"Key\":" + "\"" + key + "\"}";
        return x;
    }
    
     public static change_log clog(String key, String table, String classname, String fieldname, String oldvalue, String newvalue) {
         String desc;
         String type;
         if (fieldname.toLowerCase().equals("deletion")) {
             desc = fieldname + " of record key: " + key;
             type = "deletion";
         } else {
             desc = fieldname + "-> Old: " + oldvalue + " New: " + newvalue; 
             type = "update";
         }
         change_log x = new change_log(null, 
                 "", // id <generated>
                 key, 
                 table, 
                 classname, // class
                 bsmf.MainFrame.userid, 
                 desc, 
                 "", // ts <generated>
                 type, // type 
                 ""  // ref
         );
         return x;
     } 
     
     public static <T> ArrayList<change_log> logChange(String key, String callclass, T x, T y)  {
        
        ArrayList<change_log> c = new ArrayList<change_log>();
        if (x != null && y != null && ! x.equals(y)) {  // if x != y...proceed to compare...else return empty c
        Field[] xfs = x.getClass().getDeclaredFields();
        Field[] yfs = y.getClass().getDeclaredFields();
        for (Field f : xfs) {
            for (Field g : yfs) {
                if (g.getName().equals(f.getName())) {
                    f.setAccessible(true);
                    g.setAccessible(true);
                    try {
                        if (f.get(x) != null && g.get(y) != null && ! g.get(y).equals(f.get(x))) {
                         c.add(clog(key, 
                                 x.getClass().getSimpleName(), 
                                 callclass, 
                                 f.getName(), 
                                 f.get(x).toString(), 
                                 g.get(y).toString()));   
                        }
                        break;
                      //  System.out.println("Name: " + f.getName() + " Value: " + f.get(x));
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        bslog(ex);
                    } 
                }
            }
        }
        }
        return c;
    }
  
     public static void log(String logtype, ArrayList<String> list) {
                 
        if (list == null || list.size() <= 0) {
            return;
        }
         
        BufferedWriter output = null;
        String logpath = getCodeValueByCodeKey("logpath", logtype);
        
        if (logpath.isBlank()) {
        logpath = "logs/generic.log";
        }
        
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
               
        Path edilogpath = FileSystems.getDefault().getPath(logpath);
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(edilogpath.toFile(), true)));
            output.write("TIMESTAMP: " + now + "" + "\n");
            for (String s : list) {
                output.write(s + "\n");
            }
            list.clear();
        } catch (FileNotFoundException ex) {
            bslog(ex);
        } catch (IOException ex) {
            bslog(ex);
        } finally {
            try { 
                output.close();
            } catch (IOException ex) {
                bslog(ex);
            }
        }
         
              
    }
    
     public static void log(String logtype, String s) {
                 
        if (s == null || s.isBlank()) {
            return;
        }
         
        BufferedWriter output = null;
        String logpath = getCodeValueByCodeKey("logpath", logtype);
        
        if (logpath.isBlank()) {
        logpath = "logs/generic.log";
        }
        
        String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
               
        Path edilogpath = FileSystems.getDefault().getPath(logpath);
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(edilogpath.toFile(), true)));
            output.write("TIMESTAMP: " + now + "" + "\n");
            output.write(s + "\n");
            
        } catch (FileNotFoundException ex) {
            bslog(ex);
        } catch (IOException ex) {
            bslog(ex);
        } finally {
            try { 
                output.close();
            } catch (IOException ex) {
                bslog(ex);
            }
        }
         
              
    }
    
     public static String cleanDirString(String dir) {    
      if (dir.isBlank()) {
          return "";
      }
      return (! dir.endsWith("/") || ! dir.endsWith("\\")) ? dir + "/" : dir ;
    }
   
     public static String padString(String s, String c, int count) {
         
         StringBuilder sb = new StringBuilder();
         sb.append(s);
         for (int j = 0; j < count; j++) {
             sb.append(c);
         }
         return sb.toString();
     }
     
     public static String asciivalues(int t) {
         String r = "";
         if (t == 0) {
                r = "NULL";
            } else if (t == 10) {
                r = "LF";    
            } else if (t == 11) {
                r = "VT";
            } else if (t == 12) {
                r = "FF";
            } else if (t == 13) {
                r = "CR";
            } else if (t == 28) {
                r = "FS";
            } else if (t == 29) {
                r = "GS";
            } else if (t == 30) {
                r = "RS";   
            } else if (t > 0 && t < 10) {
                r = "UNSU";  
            } else if (t > 13 && t < 28) {
                r = "UNSU";    
            } else {
                r = String.valueOf(Character.toString((char) t));
            }
         return r;
     }

    public record bsr(String[] m, byte[] data) {
     
    }
    
    public static String parseFileName(String x) {
        String filename = "";
        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(x);
        String format = "";
        if (matcher.find()) {
            format = matcher.group(1);
            try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);  
            Date now = new Date();
            String d = sdf.format(now);
            int index = x.indexOf("%");
            filename = x.substring(0,index) + d + x.substring(index + (format.length() + 2));
            } catch (IllegalArgumentException ex) {
            	return x.replace("%", "");
            }
        } else {
            return x.replace("%", "");
        }
        return filename.replace("%", "");
    }
}



