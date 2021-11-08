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
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        add, update, get, delete
    }
    
    public static DateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat bsdate = new SimpleDateFormat("yyyy-MM-dd");
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
    
    public static void callCurrencySet() {
        
        JDialog currdialog = new JDialog();
        currdialog.setTitle(getMessageTag(1153));
        currdialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        javax.swing.JComboBox ddcurr = new javax.swing.JComboBox<>();
        javax.swing.JButton btcommit = new javax.swing.JButton();
        ddcurr.removeAllItems();
        ArrayList<String> curr = OVData.getCurrlist();
        Collections.sort(curr);
        for (int i = 0; i < curr.size(); i++) {
            if (curr.get(i).equals("ALL"))
                continue;
            ddcurr.addItem(curr.get(i));
        }
        
        btcommit.setText(getGlobalProgTag("commit"));
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              admData.updateDefaultCurrency(ddcurr.getSelectedItem().toString());
              currdialog.dispose();
            }
        });
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
      
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(ddcurr, gbc);
        
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(btcommit, gbc);
       
        
        currdialog.add(panel);
        currdialog.setPreferredSize(new Dimension(300, 200));
        currdialog.pack();
        currdialog.setLocationRelativeTo( null );
        currdialog.setResizable(false);
        currdialog.setVisible(true);
        ddcurr.requestFocus();
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
        rbpanel.add(lurb1);
        JLabel spacer = new JLabel("   ");
        rbpanel.add(spacer);
        rbpanel.add(lurb2);
        rbpanel.add(spacer);
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
        rbpanel.add(lurb1);
        JLabel spacer = new JLabel("   ");
        rbpanel.add(spacer);
        rbpanel.add(lurb2);
        rbpanel.add(spacer);
        rbpanel.add(lurb3);
        rbpanel.add(spacer);
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
        rbpanel.add(lurb1);
        JLabel spacer = new JLabel("   ");
        rbpanel.add(spacer);
        rbpanel.add(lurb2);
        rbpanel.add(spacer);
        rbpanel.add(lurb3);
        rbpanel.add(spacer);
        rbpanel.add(lurb4);
        rbpanel.add(spacer);
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
        double z = 0;
        if (! x.isEmpty()) {
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                    try {
                        z = nf.parse(x).doubleValue();
                    } catch (ParseException ex) {
                        bsmf.MainFrame.show(getMessageTag(1017) + "/" + x);
                    }
        }
        return z;
    }
    
    public static double bsParseDoubleUS(String x) {
        double z = 0;
        if (! x.isEmpty()) {
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        Number number = 0;
                    try {
                        number = format.parse(x);
                    } catch (ParseException ex) {
                        bsmf.MainFrame.show(getMessageTag(1017));
                    }
        z = number.doubleValue();
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
    
    public static String bsFormatDouble5(double invalue) {
        String outvalue = "";
        String pattern = "#0.00000"; 
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
        if (defaultDecimalSeparator == ',' && invalue.contains(".")) {
            return "error";
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
       
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        try {   
            outvalue = df.format(df.parse(invalue));
        } catch (ParseException ex) {
            outvalue = "error";
        }
       
        return outvalue;
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
        String pattern = "#0.00###";
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        x = df.format(invalue);
        return x;
    }
    
    
    public static String currformatUS(String invalue) {
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
   
    
    public static boolean isSet(ArrayList list, Integer index) {
     return index != null && index >=0 && index < list.size() && list.get(index) != null;
     }
    
    public static boolean isSet(String[] list, Integer index) {
     return index != null && index >=0 && index < list.length && list[index] != null;
     }
     
    public static boolean ConvertStringToBool(String i) {
        boolean b;
        if (i != null && i.equals("1") ) {
            b = true;
        } else {
            b = false;
        }
        return b;
    }
     
    public static boolean ConvertIntegerToBool(int i) {
        boolean b;
        if (i == 1) {
            b = true;
        } else {
            b = false;
        }
        return b;
    }
    
    public static String ConvertIntToYesNo(int i) {
        String s;
        if (i == 1) {
            s = "YES";
        } else {
            s = "NO";
        }
        return s;
    }
      
    public static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
    
    public static String boolToString(boolean b) {
        return b ? String.valueOf(1) : String.valueOf(0);
    }

    
    public static String xNull(String mystring) {
       String returnstring = "";
       returnstring = (mystring == null) ? "" : mystring;
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
            Double.parseDouble(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public static Date parseDate(String indate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
    
    public static String setDateFormat(Date date) {
       String mydate = "";
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       return mydate = sdf.format(date);
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
                formatter.setMaximumFractionDigits(4);
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
  
    
    
     public static String getGlobalProgTag(String key) {
         String tag = "";
          if (tags.containsKey("global.prog." + key)) {
            tag = tags.getString("global.prog." + key);
          }
         return tag;
     }
     
     public static String getGlobalLabelTag(String key) {
         String tag = "";
          if (tags.containsKey("global.label." + key)) {
            tag = tags.getString("global.label." + key);
          }
         return tag;
     }
     
     public static String getGlobalMenuTag(String key) {
         String tag = "";
          if (tags.containsKey("global.menu." + key)) {
            tag = tags.getString("global.menu." + key);
          }
         return tag;
     }
    
     public static String getGlobalColumnTag(String key) {
         String tag = "";
          if (tags.containsKey("global.column." + key)) {
            tag = tags.getString("global.column." + key);
          }
         return tag;
     }
         
     public static String getClassLabelTag(String key, String thisclass) {
         String tag = "";
          if (tags.containsKey(thisclass + ".label." + key)) {
            tag = tags.getString(thisclass + ".label." + key);
          }
         return tag;
     }
    
     public static String getMessageTag(int key, String thisclass) {
         String tag = "";
          if (tags.containsKey("global.message." + key)) {
              tag = MessageFormat.format(tags.getString("global.message." + key).replace("'", "''"), thisclass);
          }
         return tag;
     }
    
     public static String getMessageTag(int key) {
         String tag = "";
          if (tags.containsKey("global.message." + key)) {
            tag = tags.getString("global.message." + key);
          }
         return tag;
     }
    
     public static String getTitleTag(int key) {
         String tag = "";
          if (tags.containsKey("global.title." + key)) {
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
    
}
