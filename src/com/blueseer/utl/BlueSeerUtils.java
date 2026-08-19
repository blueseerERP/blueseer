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
import com.blueseer.fgl.fglData;
import com.blueseer.fgl.fglData.AcctMstr;
import static com.blueseer.utl.OVData.getCodeValueByCodeKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 *
 * @author vaughnte
 */
public class BlueSeerUtils {
    
    public enum dbaction {
        init, add, update, get, delete, run, addItem, updateItem, deleteItem
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
    public static  ImageIcon clickfile = new ImageIcon(BlueSeerUtils.class.getResource("/images/pdf100.png"));
    
    
    
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
    
    public static String dataInitError = getMessageTag(1207);
    
    public static String SuccessBit = "0";
    public static String ErrorBit = "1";
    
    // lookup variables
        public static javax.swing.table.DefaultTableModel luModel = null;
        public static JTable luTable = new JTable();
        public static MouseListener luml = null;
        public static ActionListener lual = null;
        public static JDialog ludialog = null;
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(true);
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
        
        luTable.setPreferredScrollableViewportSize(new Dimension(600,300));
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(true);
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
        luTable.setPreferredScrollableViewportSize(new Dimension(600,300));
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(true);
        ludialog.setVisible(true);
        luinput.requestFocus();
    } 
    
    public static void callDialog(String rb1, String rb2, int rbdefault) {
        
         if (ludialog != null) {
            ludialog.dispose();
        }
        if (luModel != null && luModel.getRowCount() > 0) {
        luModel.setRowCount(0);
        luModel.setColumnCount(0);
        }
        luinput.setText("");
        luTable.setPreferredScrollableViewportSize(new Dimension(600,300));
        luTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(luTable);
        JPanel rbpanel = new JPanel();
        lubg = new ButtonGroup();
        lurb1 = new JRadioButton(rb1);
        lurb2 = new JRadioButton(rb2);
        lurb1.setSelected(true);
        lurb2.setSelected(false);
        if (rbdefault == 2) {
            lurb1.setSelected(false);
            lurb2.setSelected(true);
        }
        
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(true);
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
        luTable.setPreferredScrollableViewportSize(new Dimension(600,300));
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(true);
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
        luTable.setPreferredScrollableViewportSize(new Dimension(600,300));
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(true);
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
        luTable.setPreferredScrollableViewportSize(new Dimension(600,300));
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(rbpanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(luinput, gbc);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( scrollPane, gbc );
        
        ludialog.add(panel);
        
        ludialog.pack();
        ludialog.setLocationRelativeTo( null );
        ludialog.setResizable(true);
        ludialog.setVisible(true);
        luinput.requestFocus();
    } 
    
    public static double bsParseDouble(String x) {
        // always returns . decimal based double
        double z = 0.00;
        
        
        if (! x.isBlank()) {
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        if (Locale.getDefault().getLanguage().equals("ar") && x.contains(".")) { // if AR locale and US keyboard "." then change decimal separator
            x = x.replace('.', '\u066B'); 
        } 
        if (Locale.getDefault().getLanguage().equals("ar") && x.startsWith("-")) {
            x = x.substring(1) + "-";
        }
        
        Number number = 0.00;
                    try {
                        if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
                        Locale cn = new Locale("C@numbers=hans");
                        com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
                        number = formatter.parse(x.trim());
                        } else {
                        number = nf.parse(x.trim());
                        }
                    } catch (ParseException ex) {
                       // bsmf.MainFrame.show(getMessageTag(1017) + "/d  " + x);
                        ex.printStackTrace();
                    }
             z =  number.doubleValue();
        }
        return z;
    }
        
    public static int bsParseInt(String x) {
        // always returns . decimal based double
        int z = 0;
        if (! x.isBlank()) {
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        
        Number number = 0;
                    try {
                        if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
                        Locale cn = new Locale("C@numbers=hans");
                        com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
                        number = formatter.parse(x.trim());
                        } else {
                        number = nf.parse(x.trim());
                        }
                    } catch (ParseException ex) {
                       // bsmf.MainFrame.show(getMessageTag(1017) + "/ " + x);
                        ex.printStackTrace();
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
        String pattern = "#0.00######"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        
        if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            outvalue = formatter.format(invalue);
        } else {
        outvalue = df.format(invalue);
        }
        return outvalue;
    }
    
    public static String bsFormatDoubleZ(double invalue) {
        String outvalue = "";
        String pattern = "#0.#####"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            outvalue = formatter.format(invalue);
        } else {
        outvalue = df.format(invalue);
        }
        return outvalue;
    }
    
    public static String bsFormatDoubleUS(double invalue) {
        String x = "";
        String pattern = "#0.00###";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(pattern);
        x = df.format(invalue);
        return x;
    }
    
    
    public static String bsFormatInt(int invalue) {
        String outvalue = "";
        String pattern = "#"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            outvalue = formatter.format(invalue);
        } else {
        outvalue = df.format(invalue);
        }
        return outvalue;
    }
    
    public static String bsFormatIntUS(int invalue) {
        String outvalue = "";
        String pattern = "#"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);    
        df.applyPattern(pattern);
        outvalue = df.format(invalue); 
        return outvalue;
    }
    
    
    public static String bsFormatDouble5(double invalue) {
        String outvalue = "";
        String pattern = "#0.00###"; 
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            outvalue = formatter.format(invalue);
        } else {
        outvalue = df.format(invalue);
        }
        return outvalue;
    }
    
    
    
    public static String bsformat(String type, String invalue, String precision) {
        String pattern = "";
        String outvalue = "";
        if (invalue.isBlank() && type.equals("")) {
           return "0";
        }
        if (invalue.isBlank() && type.equals("s")) {
           return "";
        }
        if (invalue.isBlank() && type.equals("i")) {
           return "0";
        }
        if (invalue.isBlank() && type.equals("d")) {
           invalue = "0"; // for use down below
        }
        if (precision.equals("2")) {
         pattern = "#0.00######"; 
        } else if (precision.equals("3")) {
         pattern = "#0.000#####";  
        } else if (precision.equals("4")) {
         pattern = "#0.0000####";   
        } else if (precision.equals("5")) {
         pattern = "#0.00000###";    
         } else if (precision.equals("0")) {
         pattern = "#0";    
        } else {
         pattern = "#0.00######";    
        }
       
        if (Locale.getDefault().getLanguage().equals("ar") && invalue.contains(".")) { // if AR locale and US keyboard "." then change decimal separator
            invalue = invalue.replace('.', '\u066B'); 
        } 
        if (Locale.getDefault().getLanguage().equals("ar") && invalue.startsWith("-")) {
            invalue = invalue.substring(1) + "-";
        }
       
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        if (! Locale.getDefault().getLanguage().equals("ar")) { // revisit
        df.applyPattern(pattern);
        }
        
        try {   
            if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            outvalue = formatter.format(bsParseDouble(invalue));
            } else {
            outvalue = df.format(df.parse(invalue));
            }
        } catch (ParseException ex) {
            outvalue = "error";
        }
       
        return outvalue;
    }
    
    public static String bsformat(String invalue, String precision) {
        String pattern = "";
        String outvalue = "";
        
        if (invalue.isBlank()) {
           return "0";
        }
        if (precision.equals("2")) {
         pattern = "#0.00######"; 
        } else if (precision.equals("3")) {
         pattern = "#0.000#####";  
        } else if (precision.equals("4")) {
         pattern = "#0.0000####";   
        } else if (precision.equals("5")) {
         pattern = "#0.00000###";    
        } else if (precision.equals("0")) {
         pattern = "#0";   
        } else if (precision.equals("1")) {
         pattern = "#0.0#######";   
        } else {
         pattern = "#0.00######";    
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
        String x = "0";
        String pattern = "#0.########"; 
         if (invalue != 0) {
         if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            x = formatter.format(invalue); 
         }  else { 
           // String adjvalue = String.valueOf(invalue).replace('.', defaultDecimalSeparator);
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
            df.applyPattern(pattern);
            x = df.format(invalue);
         }
        }
        return x;
    }
    
    public static String bsNumber(String invalue) {
        // invalue will come over as a . decimal regardless of Locale
        // currformat will return 3,56 for the following scenarios if
        // default separator is ','   
        // currformat("3.56")
        // currformat("3,56") 
         
        String x = "0";
        String pattern = "#0.########";
        if (invalue != null && ! invalue.isBlank()) {
         if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            x = formatter.format(bsParseDouble(invalue));
         }  else { 
            String adjvalue = invalue.replace('.', defaultDecimalSeparator);
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
            df.applyPattern(pattern);
            try { 
                x = df.format(df.parse(adjvalue));
            } catch (ParseException ex) {
                bslog(ex);
            }
         }
        }
        return x;
    }
    
    public static String bsNumberToUS(String invalue) {
                
        String x = "0";
        String pattern = "#";
        if (! invalue.isBlank()) {
        String adjvalue = invalue.replace('.', defaultDecimalSeparator);
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
     //  NumberFormat nf = NumberFormat.getInstance(Locale.getDefault()); 
       DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
       DecimalFormat usdf = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
       usdf.applyPattern(pattern);
        try { 
            if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            x = usdf.format(bsParseDouble(invalue));
            } else {
            x = usdf.format(df.parse(adjvalue));
            }
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
        String pattern = "¤#0.00"; 
        if (! invalue.isBlank()) {
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
        String pattern = "#0.00";
        String adjvalue = invalue;
        if (! invalue.isBlank()) {
           adjvalue = invalue.replace('.', defaultDecimalSeparator);
        
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
     //  NumberFormat nf = NumberFormat.getInstance(Locale.getDefault()); 
       DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        try { 
            if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            x = formatter.format(Double.valueOf(invalue));
            } else {
            x = df.format(df.parse(adjvalue));
            }
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
       // System.out.println(invalue + "/" + adjvalue + "/" + x);
        return x;
    }
    
    public static String currformatDouble(double invalue) {
        // invalue will come over as a . decimal regardless of Locale
        // currformat will return 3,56 for the following scenarios if
        // default separator is ','   
        // currformat("3.56")
        // currformat("3,56") 
        
        if (invalue == 0) {
            return "0";
        }
        
        String x = "0.00";
        String pattern = "#0.00";
        String adjvalue = String.valueOf(invalue);
      
           adjvalue = adjvalue.replace('.', defaultDecimalSeparator);
       // System.out.println("before: " + invalue + "/" + adjvalue + "/" + x);
       // DecimalFormat df = new DecimalFormat("#0.00###", new DecimalFormatSymbols(Locale.getDefault())); 
     //  NumberFormat nf = NumberFormat.getInstance(Locale.getDefault()); 
       DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        try { 
            if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            x = formatter.format(Double.valueOf(invalue));
            } else {
            x = df.format(df.parse(adjvalue));
            }
        } catch (ParseException ex) {
            bslog(ex);
        }
        
       // System.out.println("after: " + invalue + "/" + adjvalue + "/" + x);
        return x;
    }
      
    public static String currformatDoubleWithSymbol(double invalue, String currency) {
        String x = "";
        String pattern = "#0.00";
        Currency c = Currency.getInstance(currency);
        String symbol = "$";
        if (! currency.equals("USD")) {
            symbol = c.getSymbol(bsmf.MainFrame.currencymap.get(c));
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        df.applyPattern(pattern);
        if (Locale.getDefault().getLanguage().equals("zh") && ! Locale.getDefault().getCountry().equals("US")) {
            Locale cn = new Locale("C@numbers=hans");
            com.ibm.icu.text.NumberFormat formatter = com.ibm.icu.text.NumberFormat.getInstance(cn);
            x = symbol + formatter.format(invalue);
        } else {
        x = symbol + df.format(invalue);
        }
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
        
        if (! currency.isBlank()) {
        Currency c = Currency.getInstance(currency);    
        locale = bsmf.MainFrame.currencymap.get(c);
        }
        if (locale == null || currency.equals("USD")) { // had to add USD override...currencymap was pulling locale with US prepended to $ sign
           locale = Locale.getDefault(); 
        }
        return locale;
    }
    
    public static String formatUSZ(String invalue) {
        // invalue will come over as a . decimal regardless of Locale
        String x = "0.00";
        String pattern = "#0.#####";
        if (! invalue.isBlank()) {
            
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        if (Locale.getDefault().getLanguage().equals("ar") && invalue.contains(".")) { // if AR locale and US keyboard "." then change decimal separator
            invalue = invalue.replace('.', '\u066B'); 
        } 
        if (Locale.getDefault().getLanguage().equals("ar") && invalue.startsWith("-")) {
            invalue = invalue.substring(1) + "-";
        }    
            
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(pattern);
        try {   
            x = df.format(nf.parse(invalue));
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
        return x;
    }
        
    public static String formatUSC(String invalue) {
        // invalue will come over as a . decimal regardless of Locale
        String x = "0.00";
        String pattern = "#0.00###";
        if (! invalue.isBlank()) {
            
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        if (Locale.getDefault().getLanguage().equals("ar") && invalue.contains(".")) { // if AR locale and US keyboard "." then change decimal separator
            invalue = invalue.replace('.', '\u066B'); 
        } 
        if (Locale.getDefault().getLanguage().equals("ar") && invalue.startsWith("-")) {
            invalue = invalue.substring(1) + "-";
        }    
            
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(pattern);
        try {   
            x = df.format(nf.parse(invalue));
        } catch (ParseException ex) {
            bslog(ex);
        }
        }
        return x;
    }
        
    public static String currformatDoubleUS(double invalue) {
        String x = "";
        String pattern = "#0.00";
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
        if (! invalue.isBlank()) {
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
    
    public static long timediff(LocalDateTime fromDateTime) {
       // return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        Duration d = Duration.between(fromDateTime, LocalDateTime.now()); 
        //long days = d.toDays();
        return d.toMillis();
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
    
    public static String convertDate(String toformat, String indate) {
       String mydate = "";
        // indate is assumed to be yyyy-MM-dd formatted string 10 chars length
        if (toformat.equals("MM/dd/yy") && indate.length() == 10) {
           mydate = indate.substring(5,7) + "/" + indate.substring(8) + "/" + indate.substring(2,4);
        }
        if (toformat.equals("MM/dd/yyyy") && indate.length() == 10) {
           mydate = indate.substring(5,7) + "/" + indate.substring(8) + "/" + indate.substring(0,4);
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
        
        if (bsmf.MainFrame.remoteDB) {
                        ArrayList<String[]> arrx = new ArrayList<String[]>();
                        arrx.add(new String[]{"id","FileExists"});
                        arrx.add(new String[]{"filepath", filepath});
                        String s = "false";  
                        try {
                            s = sendServerPost(arrx, "", null, "dataServ");
                        } catch (IOException ex) {
                            bslog(ex);
                        }
                        return BlueSeerUtils.ConvertStringToBool(s.trim());
        } else { // local
           Path mypath = FileSystems.getDefault().getPath(filepath);
            if (! mypath.toFile().exists()) {
                return false;
            } else {
                return true;
            } 
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
    
    public static String ConvertIntToBoolString(int i) {
        return (i == 1) ? "true" : "false";
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
        // use of NumberFormat was necessary for non-english locale (could not use Double.parseDouble) but will not completely 
        // work for instances such as 4.8x or 4.3.3 which should return false
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
   
    public static boolean isNumbersEqual(String x, String y) {
        try {
                if (x == null || x.isEmpty() || y == null || y.isEmpty()) {
                return false;
                }
                double xd = Double.parseDouble(x);
                double yd = Double.parseDouble(y);
                return (xd == yd);
            } catch (NumberFormatException nfe) {
                return false;
            }
    }
    
    public static boolean isNumeric(String i) {
        
        if (Locale.getDefault().getLanguage().equals("en")) {
            try {
                if (i == null || i.isEmpty()) {
                return false;
                }
                Double.parseDouble(i);
                return true;
            } catch (NumberFormatException nfe) {
                return false;
            }
        } else {
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
                bsmf.MainFrame.show("parseDate Exception: " + indate);
            }
        }
        return r;
    }
    
    public static LocalDate parseDateLD(String indate) {
        LocalDate r = null;
        if (indate != null && ! indate.isEmpty() && ! indate.equals("0000-00-00") && ! indate.equals("null")) {
            r = LocalDate.parse(indate);
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
                bslog("getDateDB Exception: " + ex.getMessage());
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
    
    public static String setDateDBLD(LocalDate date) {
       String mydate = null;
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("en-US"));
       if (date == null) {
           return mydate;
       } else {
           return sdf.format(date);
       }
    }
    
    
    public static String setDateDB(String date, String format) {
       String mydate = null;
       SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("en-US"));
       if (date == null || date.isBlank()) {
           return mydate;
       } else {
           try {
               return sdf.format(sdf.parse(date));
           } catch (ParseException ex) {
               bslog(ex);
               return null;
           }
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

     public static String getGlobalTag(String key) {
         String tag = "";
          if (tags != null && tags.containsKey(key)) {
            tag = tags.getString(key);
          }
         return tag;
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
        bsmf.MainFrame.disableAllMenus();
        bsmf.MainFrame.MainProgressBar.setVisible(true);
        bsmf.MainFrame.MainProgressBar.setIndeterminate(true);
        bsmf.MainFrame.MainProgressBar.setBackground(Color.BLUE);
        message(message);
     }
     
     public static void startTaskNoBar(String[] message) {
        bsmf.MainFrame.disableAllMenus();
        message(message);
     }
     
     
     public static void endTask(String[] message) {
        bsmf.MainFrame.enableAllMenus();
        bsmf.MainFrame.MainProgressBar.setVisible(false);
        bsmf.MainFrame.MainProgressBar.setIndeterminate(false);
        message(message);
     }
     
     public static void endTaskNoBar(String[] message) {
        bsmf.MainFrame.enableAllMenus();
        message(message);
     }
          
     public static void message(String[] message) {
         
         if (message.length != 2) {
           bsmf.MainFrame.messagelabel.setText("message arguments missing: " + message);
           bsmf.MainFrame.messagelabel.setForeground(Color.RED);  
           return;
         }
         if (message[0] == null || message[1] == null) {
           bsmf.MainFrame.messagelabel.setText("message arguments are null: " + message);
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
    
     public static change_log clog(String key, String k, String table, String classname, String fieldname, String oldvalue, String newvalue) {
         String desc;
         String type;
         String linestring = (k.equals("0")) ? "" : "line: " + k;
         if (fieldname.toLowerCase().equals("deletion")) {
             desc = fieldname + " of record key: " + key;
             type = "deletion";
         } else {
             desc = linestring + " " + fieldname + "-> Old: " + oldvalue + " New: " + newvalue; 
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
     
     public static <T> ArrayList<change_log> logChangeArrays(String key, String callclass, ArrayList<T> x, ArrayList<T> y) {
        ArrayList<change_log> c = new ArrayList<change_log>();
        ArrayList<change_log> cf = new ArrayList<change_log>();
        for (int k = 0; k < x.size(); k++) {
            if (x.size() == y.size()) {
            c = logChange(key, k + 1, callclass, x.get(k), y.get(k));
            cf.addAll(c);
            } else {
                cf.add(clog(key,
                 String.valueOf(k),
                 x.getClass().getSimpleName(), 
                 callclass, 
                 "order line count difference", 
                 String.valueOf(x.size()), 
                 String.valueOf(y.size())));
                break; // only pass through once if count difference...otherwise it reports same thing each instance of x line
            }
        }        
        return cf;
     }
     
     public static <T> ArrayList<change_log> logChange(String key, int k, String callclass, T x, T y)  {
        
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
                                 String.valueOf(k),
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
  
     public static <T> String toJson(T x)  {
        StringBuilder r = new StringBuilder();
        StringJoiner j = new StringJoiner(",");
        if (x != null) {  
        Field[] xfs = x.getClass().getDeclaredFields();
        for (Field f : xfs) {
            f.setAccessible(true);
            if (f.getName().equals("m")) {
                continue;
            }
            try {
                j.add('"' + f.getName() + '"' + ":" + '"' + f.get(x).toString() + '"');
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                bslog(ex);
            }
        }
        r.append("{");
        r.append(j);
        r.append("}");
        }
        return r.toString();
    }
  
     
     public static void log(String logtype, ArrayList<String[]> list) {
                 
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
            for (String[] s : list) {
                output.write(s[1] + "\n");
            }
           // list.clear();
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

    public static String sendServerRequest(ArrayList<String[]> vlist, String dataClass) throws MalformedURLException, IOException {
       
        StringBuilder sb = new StringBuilder();
        String urlString = "";
        if (! bsmf.MainFrame.rhost.isBlank()) {
            urlString = bsmf.MainFrame.protocol + "://" + bsmf.MainFrame.rhost + ":" + bsmf.MainFrame.serverport + "/bsapi/" + dataClass; 
        } else {
            urlString = bsmf.MainFrame.protocol + "://" + bsmf.MainFrame.ip + ":" + bsmf.MainFrame.serverport + "/bsapi/" + dataClass;
        }
        
        String user = bsmf.MainFrame.user;
        String pass = bsmf.MainFrame.pass;
        
        // set parameter string
        String methodpath = "";
        for (String[] v : vlist) {
         methodpath = methodpath + v[0] + "=" + v[1] + "&";
        }
        if (methodpath.endsWith("&")) {
                    methodpath = methodpath.substring(0, methodpath.length() - 1);
        }
        methodpath = "?" + methodpath;
        urlString = urlString + methodpath;
        
        URL url = new URL(urlString);
        
        if (bsmf.MainFrame.protocol.equals("http")) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(300000);
           // conn.setRequestMethod("GET");    

            if (! user.isBlank() && ! pass.isBlank()) {
            String userCredentials = new String(user + ":" + pass);
            String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
            conn.setRequestProperty("Authorization", basicAuth);
            } else {
                return sb.toString();
            } 

           // System.out.println(urlString);


            if (conn.getResponseCode() != 200) {
                        sb.append(conn.getResponseCode() + ": " + conn.getResponseMessage());
                        //throw new RuntimeException("Failed : HTTP error code : "
                        //		+ conn.getResponseCode());

            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                String output = "";

                while ((output = br.readLine()) != null) {
                    sb.append(output).append("\n");
                }
                br.close(); 
            }

            if (conn != null) {
              conn.disconnect();
            }
        } else {
            HttpsURLConnection connssl = (HttpsURLConnection) url.openConnection();
            connssl.setHostnameVerifier(allHostsValid);
            connssl.setRequestProperty("Content-Type", "text/plain");
            connssl.setConnectTimeout(10000);
            connssl.setReadTimeout(300000);
           // conn.setRequestMethod("GET");    

            if (! user.isBlank() && ! pass.isBlank()) {
            String userCredentials = new String(user + ":" + pass);
            String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
            connssl.setRequestProperty("Authorization", basicAuth);
            } else {
                return sb.toString();
            } 

           // System.out.println(urlString);


            if (connssl.getResponseCode() != 200) {
                        sb.append(connssl.getResponseCode() + ": " + connssl.getResponseMessage());
                        //throw new RuntimeException("Failed : HTTP error code : "
                        //		+ conn.getResponseCode());

            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader((connssl.getInputStream())));
                String output = "";

                while ((output = br.readLine()) != null) {
                    sb.append(output).append("\n");
                }
                br.close(); 
            }

            if (connssl != null) {
              connssl.disconnect();
            }
        }
        
       return sb.toString();
    }

    public static String sendServerPost(ArrayList<String[]> hlist, String postData, byte[] b, String dataClass) throws MalformedURLException, IOException {
       
        StringBuilder sb = new StringBuilder();
        String urlString = "";
        if (! bsmf.MainFrame.rhost.isBlank()) {
            urlString = bsmf.MainFrame.protocol + "://" + bsmf.MainFrame.rhost + ":" + bsmf.MainFrame.serverport + "/bsapi/" + dataClass; 
        } else {
            urlString = bsmf.MainFrame.protocol + "://"  + bsmf.MainFrame.ip + ":" + bsmf.MainFrame.serverport + "/bsapi/" + dataClass; 
        }
        
        String user = bsmf.MainFrame.userid;
        String pass = "";
        
        URL url = new URL(urlString);
        
        byte[] postDataBytes;
        if (b != null) {
            postDataBytes = b;
        } else {
            postDataBytes = postData.getBytes("UTF-8");
        }
        
        if (bsmf.MainFrame.protocol.equals("http")) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(300000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            // Custom Headers
            
            conn.setRequestProperty("sessionid",bsmf.MainFrame.sessionid);
            for (String[] h : hlist) {
                if (h[0].equals("user")) { // must be original login call...only call that passes userid...it's auto set below otherwise
                    user = h[1];              
                }
                if (h[0].equals("pass")) { // must be original login call...only call that passes passwd
                    pass = h[1];
                    continue; // do not add pass to headers...will be added to Auth below                    
                }
             conn.setRequestProperty(h[0],h[1]);
            }
            if (conn.getRequestProperty("user") == null || conn.getRequestProperty("user").isBlank()) {
               conn.setRequestProperty("user",bsmf.MainFrame.userid); 
               user = bsmf.MainFrame.userid;
               
            }

            // auth   
            if (! user.isBlank()) {
            String userCredentials = user + ":" + pass;
            String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
            conn.setRequestProperty("Authorization", basicAuth);
            } 

            conn.getOutputStream().write(postDataBytes);


            if (conn.getResponseCode() != 200) {
                        sb.append(conn.getResponseCode()).append(":").append(conn.getResponseMessage());  // return error resp code,messg
                        String output;
                        StringBuilder sberror = new StringBuilder();
                        sberror.append(conn.getResponseCode()).append(": ").append(conn.getResponseMessage());
                        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                        while ((output = br.readLine()) != null) {
                            sberror.append(output).append("\n");
                        }
                        br.close(); 
                        bslog(sberror.toString());
                        
                        //throw new RuntimeException("Failed : HTTP error code : "
                        //		+ conn.getResponseCode());

            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                String output = "";


                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                br.close(); 
            }

            if (conn != null) {
              conn.disconnect();
            }
        
        } else {  // else https
            HttpsURLConnection connssl = (HttpsURLConnection) url.openConnection();
            connssl.setHostnameVerifier(allHostsValid);
            connssl.setDoOutput(true);
            connssl.setConnectTimeout(10000);
            connssl.setReadTimeout(300000);
            connssl.setRequestMethod("POST");
            connssl.setRequestProperty("Content-Type", "text/plain");
            connssl.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            // Custom Headers
                       
            
            connssl.setRequestProperty("sessionid",bsmf.MainFrame.sessionid);
            for (String[] h : hlist) {
                if (h[0].equals("user")) { // must be original login call...only call that passes userid...it's auto set below otherwise
                    user = h[1];              
                }
                if (h[0].equals("pass")) { // must be original login call...only call that passes passwd
                    pass = h[1];
                    continue; // do not add pass to headers...will be added to Auth below                    
                }
             connssl.setRequestProperty(h[0],h[1]);
            }
            if (connssl.getRequestProperty("user") == null || connssl.getRequestProperty("user").isBlank()) {
               connssl.setRequestProperty("user",bsmf.MainFrame.userid); 
               user = bsmf.MainFrame.userid;
               
            }
            
            // auth   
            if (! user.isBlank()) {
            String userCredentials = new String(user + ":" + pass);
            String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
            connssl.setRequestProperty("Authorization", basicAuth);
            } 

            connssl.getOutputStream().write(postDataBytes);


            if (connssl.getResponseCode() != 200) {
                        sb.append(connssl.getResponseCode()).append(":").append(connssl.getResponseMessage());  // return error resp code,messg
                        String output;
                        StringBuilder sberror = new StringBuilder();
                        sberror.append(connssl.getResponseCode()).append(": ").append(connssl.getResponseMessage());
                        BufferedReader br = new BufferedReader(new InputStreamReader((connssl.getErrorStream())));
                        while ((output = br.readLine()) != null) {
                            sberror.append(output).append("\n");
                        }
                        br.close(); 
                        bslog(sberror.toString());

            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader((connssl.getInputStream())));
                String output = "";


                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                br.close(); 
            }

            if (connssl != null) {
              connssl.disconnect();
            }
            
            
        }
            
        
        
       return sb.toString();
    }

    static HostnameVerifier allHostsValid = new HostnameVerifier() {
      public boolean verify(String hostname, SSLSession session) {
          return true;
      }  
    };
    
    public static byte[] sendServerPostByteR(ArrayList<String[]> hlist, String postData, byte[] b) throws MalformedURLException, IOException {
       
        StringBuilder sb = new StringBuilder();
        String urlString = "";
        if (! bsmf.MainFrame.rhost.isBlank()) {
            urlString = bsmf.MainFrame.protocol + "://" + bsmf.MainFrame.rhost + ":" + bsmf.MainFrame.serverport + "/bsapi/dataServ";
        } else {
            urlString = bsmf.MainFrame.protocol + "://" + bsmf.MainFrame.ip + ":" + bsmf.MainFrame.serverport + "/bsapi/dataServ";
        }
        
               
        String user = bsmf.MainFrame.user;
        String pass = bsmf.MainFrame.pass;
        
        URL url = new URL(urlString);
        
        byte[] postDataBytes;
        byte[] readDataBytes = null;
        
        if (b != null) {
            postDataBytes = b;
        } else {
            postDataBytes = postData.getBytes("UTF-8");
        }
        if (bsmf.MainFrame.protocol.equals("http")) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(300000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            // Custom Headers
            for (String[] h : hlist) {
             conn.setRequestProperty(h[0],h[1]);
            }



            // auth   
            if (! user.isBlank() && ! pass.isBlank()) {
            String userCredentials = new String(user + ":" + pass);
            String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
            conn.setRequestProperty("Authorization", basicAuth);
            } else {
                return readDataBytes;
            } 




            conn.getOutputStream().write(postDataBytes);


            if (conn.getResponseCode() != 200) {
                        sb.append(conn.getResponseCode() + ": " + conn.getResponseMessage());
                        String output = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                        while ((output = br.readLine()) != null) {
                            sb.append(output).append("\n");
                        }
                        br.close(); 
                        //throw new RuntimeException("Failed : HTTP error code : "
                        //		+ conn.getResponseCode());

            } else {
                 readDataBytes = IOUtils.toByteArray(conn.getInputStream());
            }

            if (conn != null) {
              conn.disconnect();
            }
        } else {
            HttpsURLConnection connssl = (HttpsURLConnection) url.openConnection();
            connssl.setHostnameVerifier(allHostsValid);
            connssl.setDoOutput(true);
            connssl.setConnectTimeout(10000);
            connssl.setReadTimeout(300000);
            connssl.setRequestMethod("POST");
            connssl.setRequestProperty("Content-Type", "text/plain");
            connssl.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            // Custom Headers
            for (String[] h : hlist) {
             connssl.setRequestProperty(h[0],h[1]);
            }



            // auth   
            if (! user.isBlank() && ! pass.isBlank()) {
            String userCredentials = new String(user + ":" + pass);
            String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
            connssl.setRequestProperty("Authorization", basicAuth);
            } else {
                return readDataBytes;
            } 




            connssl.getOutputStream().write(postDataBytes);


            if (connssl.getResponseCode() != 200) {
                        sb.append(connssl.getResponseCode() + ": " + connssl.getResponseMessage());
                        String output = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader((connssl.getErrorStream())));
                        while ((output = br.readLine()) != null) {
                            sb.append(output).append("\n");
                        }
                        br.close(); 
                        //throw new RuntimeException("Failed : HTTP error code : "
                        //		+ conn.getResponseCode());

            } else {
                 readDataBytes = IOUtils.toByteArray(connssl.getInputStream());
            }

            if (connssl != null) {
              connssl.disconnect();
            } 
        }
       return readDataBytes;
    }

    
    public static boolean confirmServerAuth(HttpServletRequest httpRequest) {
        final String authorization = httpRequest.getHeader("Authorization");
       // System.out.println("confirmServerAuth: enter");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            Base64 b = new Base64(); 
            String credentials = new String(b.decode(base64Credentials), Charset.forName("UTF-8"));
           // System.out.println("confirmServerAuth 1: " + credentials + "/" + bsmf.MainFrame.user + "/" + bsmf.MainFrame.pass);
            final String[] v = credentials.split(":", 2);
            if ( v != null && v.length > 1 && v[0].equals(bsmf.MainFrame.user) && v[1].equals(bsmf.MainFrame.pass)) {
                return true;
            } else {
                bslog("confirmServerAuth creds failed: " + credentials);
                System.out.println("confirmServerAuth creds failed: " + credentials );
            }            
        } else {
        bslog("confirmServerAuth creds: No basic auth ");
        System.out.println("confirmServerAuth: no basic auth");
        }
        return false;
    }
    
    public static boolean confirmServerAuthAPI(HttpServletRequest httpRequest, HashMap<String,String> hm) {
        String user = httpRequest.getHeader("user");
        String ip = httpRequest.getRemoteAddr();
        String xff = httpRequest.getHeader("X-FORWARDED-FOR");
        String sessionid = httpRequest.getHeader("sessionid");
        
        if (hm.containsKey(user) && hm.get(user).equals(sessionid + "," + ip)) {
            return true;
        } else {
           bslog("confirmServerAuthAPI creds failed: " +  user + "," + sessionid + "," + ip + ",xff:" + xff);
           System.out.println("confirmServerAuthAPI creds failed: " + user + "," + sessionid + "," + ip + ",xff:" + xff); 
           return false;
        }
    }
    
    
    public static boolean confirmServerSession(HttpServletRequest httpRequest) {
        String cookievalue = "";
        String ip = httpRequest.getRemoteAddr();
        Cookie[] cookies = httpRequest.getCookies(); 
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("bscookie")) {
                     cookievalue = c.getValue();
                     break;
                    }
                }
            }
            Base64 b = new Base64(); 
            String credentials = new String(b.decode(cookievalue), Charset.forName("UTF-8"));
            System.out.println("credentials: -- > " + credentials);
            final String[] v = credentials.split(",", 4); // user:sessionid:sessionIP:site            
            if (v != null && v.length == 4) {
                if (! ip.equals(v[2])) {  // if cookie IP does not match current Request Session ID...bail
                    return false;
                } else {
                    return bsmf.MainFrame.isValidUserSession(ip, v[0], v[1]);
                }
            }            
        
        return false;
    }
    
    public static String getSiteFromSessionCookie(HttpServletRequest httpRequest) {
        String cookievalue = "";
        String site = "";
        Cookie[] cookies = httpRequest.getCookies(); 
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("bscookie")) {
                     cookievalue = c.getValue();
                     break;
                    }
                }
            }
            Base64 b = new Base64(); 
            String credentials = new String(b.decode(cookievalue), Charset.forName("UTF-8"));
            final String[] v = credentials.split(",", 4); // user:sessionid:sessionIP:site            
            if (v != null && v.length == 4) {
               site = v[3]; 
            }            
        
        return site;
    }
    
    
    public static boolean killServerSession(HttpServletRequest httpRequest) {
        String cookievalue = "";
        String ip = httpRequest.getRemoteAddr();
        Cookie[] cookies = httpRequest.getCookies(); 
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("bscookie")) {
                     cookievalue = c.getValue();
                     break;
                    }
                }
            }
            Base64 b = new Base64(); 
            String credentials = new String(b.decode(cookievalue), Charset.forName("UTF-8"));
            final String[] v = credentials.split(",", 4); // user:sessionid:sessionIP:site            
            if (v != null && v.length == 4) {
                if (! ip.equals(v[2])) {  // if cookie IP does not match current Request Session ID...bail
                    return false;
                } else {
                    return bsmf.MainFrame.killUserSession(ip, v[0], v[1]);
                }
            }            
        
        return false;
    }
        
    
    public static String checkDigitUCC18(int serialno) throws NumberFormatException {
        int evenSum = 0;
        int oddSum = 0;

        // method returns a 20 character UCC or SSCC type serial number.
        // incoming key can be of any length and will be converted to a 17 character string padded to the right with zeros
        // a checksum will be calculated over the 17 characters and appended to the end to give an 18 character string
        // a '00' is preprending as the application identifier to give a total of 20 chars
        
        String key = String.format("%-17s", serialno ).replace(' ', '0');  // pad to the right with zeros
        	
        //Loop through all the data, summing up the evens and odds
        for(int i = 0; i < key.length(); i++) {
            //Offset since the SSCC standard starts it's index at 1
            if((i + 1) % 2 == 0){
                evenSum += Integer.parseInt(String.valueOf(key.charAt(i)));
            } else {
                oddSum += Integer.parseInt(String.valueOf(key.charAt(i)));
            }
        }

        int oddsTotal = oddSum * 3;
        int bothTotal = oddsTotal + evenSum;
        int remainder = bothTotal % 10;
        int checksum = 10 - remainder;
        
        if (checksum == 10) {
            checksum = 0;
        }
        
        return key + String.valueOf(checksum);
       
		
    }

    public static String arrayToJson(String[] arr) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(arr);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static String ArrayListStringArrayToJson(ArrayList<String[]> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static String ArrayListStringToJson(ArrayList<String> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    public static String SetStringToJson(Set<String> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    
    public static String ArrayListDoubleToJson(ArrayList<Double> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    public static String DefaultTableModelToJson(DefaultTableModel model) {
      /*
        // Create the main JSON object
        JSONObject json = new JSONObject();

        // Get column names and add them to a JSON array
        JSONArray columns = new JSONArray();
        for (int i = 0; i < model.getColumnCount(); i++) {
            columns.put(model.getColumnName(i));
        }
        json.put("columns", columns);

        // Get the data and add it to a JSON array
        JSONArray data = new JSONArray();
        for (int i = 0; i < model.getRowCount(); i++) {
            JSONObject row = new JSONObject();
            for (int j = 0; j < model.getColumnCount(); j++) {
                String columnName = model.getColumnName(j);
                Object cellValue = model.getValueAt(i, j);
                row.put(columnName, cellValue);
            }
            data.put(row);
        }
        json.put("data", data);

        return json.toString(4); // Use an indentation of 4 spaces for pretty-printing
    */
      
      JSONArray jsonArrayOfArrays = new JSONArray();

        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            JSONArray rowArray = new JSONArray();
            for (int j = 0; j < columnCount; j++) {
                rowArray.put(model.getValueAt(i, j));
            }
            jsonArrayOfArrays.put(rowArray);
        }

        return jsonArrayOfArrays.toString(2);
    
    }
    
    
    public static String HashMapStringIntegerToJson(Map<String,Integer> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static String HashMapStringStringToJson(HashMap<String,String> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static String HashMapStringStringArrToJson(HashMap<String,String[]> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    
    public static String boolToJson(boolean b) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(b);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static String intToJson(int i) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(i);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    public static String doubleToJson(double i) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(i);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static Object[][] jsonToData(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        Object[][] rawData = null;
        
        if (jsonString == null || jsonString.isBlank() || jsonString.startsWith("[]")) {
            return new Object[0][0];
        }
        try {
            rawData = objectMapper.readValue(jsonString, Object[][].class);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        
        if (rawData.length == 0) {
            return new Object[0][0];
        }
        
        Object[][] data = new Object[rawData.length][rawData[0].length];
        for (int i = 0; i < rawData.length; i++) {
            for (int j = 0; j < rawData[i].length; j++) {
                if (rawData[i][j] == null) {
                    continue;
                }
                if (rawData[i][j].equals("select")) { // Assuming the first column is for ImageIcons.
                    data[i][j] = BlueSeerUtils.clickflag;
                } else if (rawData[i][j].equals("detail")) {
                    data[i][j] = BlueSeerUtils.clickbasket;
                } else if (rawData[i][j].equals("print")) {
                    data[i][j] = BlueSeerUtils.clickprint; 
                } else if (rawData[i][j].equals("mail")) {
                    data[i][j] = BlueSeerUtils.clickmail;
                } else if (rawData[i][j].equals("void")) {
                    data[i][j] = BlueSeerUtils.clickvoid; 
                } else if (rawData[i][j].equals("clock")) {
                    data[i][j] = BlueSeerUtils.clickclock;  
                } else if (rawData[i][j].equals("chart")) {
                    data[i][j] = BlueSeerUtils.clickchart;      
                } else {
                    data[i][j] = rawData[i][j];
                }
            }
        }
        
        return data;
    }
    
    public static String[] jsonToStringArray(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        String[] x = null;
        try {
            x = objectMapper.readValue(jsonstring, String[].class);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    public static boolean jsonToBoolean(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        boolean x = false;
        try {
            x = objectMapper.readValue(jsonstring, boolean.class);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    public static ArrayList<String> jsonToArrayListString(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> x = null;
        try {
            x = objectMapper.readValue(jsonstring, ArrayList.class);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static Set<String> jsonToSetString(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> x = null;
        try {
            x = objectMapper.readValue(jsonstring, Set.class);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static ArrayList<Double> jsonToArrayListDouble(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Double> x = null;
        try {
            x = objectMapper.readValue(jsonstring, ArrayList.class);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static Map<String,Integer> jsonToHashMapStringInteger(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Integer> x = null;
        try {
            x = objectMapper.readValue(jsonstring, new TypeReference<HashMap<String,Integer>>() {});
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static HashMap<String,String> jsonToHashMapStringString(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,String> x = null;
        try {
            x = objectMapper.readValue(jsonstring, new TypeReference<HashMap<String,String>>() {});
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static HashMap<String,String[]> jsonToHashMapStringStringArr(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,String[]> x = null;
        try {
            x = objectMapper.readValue(jsonstring, new TypeReference<HashMap<String,String[]>>() {});
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static DefaultTableModel jsonToDefaultTableModel(String jsonstring) throws JsonProcessingException {
        
        System.out.println(jsonstring);
        ObjectMapper objectMapper = new ObjectMapper();
       // JsonNode rootNode = objectMapper.readTree(jsonstring);
       // JsonNode rowsNode = rootNode.get("data"); 
        
        List<Map<String, Object>> jsonData = objectMapper.readValue(jsonstring, List.class);

       // if (jsonData.isEmpty()) {
       //     return new DefaultTableModel(); // Empty model if no data
       // }

        // Extract column names from the first object
        Vector<String> columnNames = new Vector<>(jsonData.get(0).keySet());

        // Extract data for rows
        Vector<Vector<Object>> data = new Vector<>();
        for (Map<String, Object> rowMap : jsonData) {
            Vector<Object> row = new Vector<>();
            for (String columnName : columnNames) {
                row.add(rowMap.get(columnName));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
        
        
        
        /*
        DefaultTableModel x = new DefaultTableModel();
        JsonNode rootNode = objectMapper.readTree(jsonstring);
        
        JsonNode columnsNode = rootNode.get("columns");
        List<String> columnIdentifiers = new ArrayList<>();
        if (columnsNode != null && columnsNode.isArray()) {
            for (JsonNode column : columnsNode) {
                columnIdentifiers.add(column.asText());
            }
        } 
        
        JsonNode rowsNode = rootNode.get("data");
        for (JsonNode rowNode : rowsNode) {
                // Create an object array for each row
                Object[] rowData = new Object[columnIdentifiers.size()];
                for (int i = 0; i < columnIdentifiers.size(); i++) {
                    String columnName = columnIdentifiers.get(i);
                    JsonNode cellNode = rowNode.get(columnName);
                    if (cellNode != null) {
                        rowData[i] = extractValue(cellNode);
                    } else {
                        rowData[i] = null; // Handle missing cell data gracefully
                    }
                }
                x.addRow(rowData);
            }
        
        return x;
        
        */
    }

     private static Object extractValue(JsonNode node) {
        if (node.isTextual()) {
            return node.asText();
        } else if (node.isNumber()) {
            return node.numberValue();
        } else if (node.isBoolean()) {
            return node.asBoolean();
        }
        // Return a string representation for other types or null
        return node.toString();
    }
    
    public static ArrayList<String[]> jsonToArrayListStringArray(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String[]> x = null;
        try {
            x = objectMapper.readValue(jsonstring, new TypeReference<ArrayList<String[]>>() {});
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }

    public static int jsonToInt(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        int x = 0;
        try {
            x = objectMapper.readValue(jsonstring, Integer.class);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    public static double jsonToDouble(String jsonstring) {
        ObjectMapper objectMapper = new ObjectMapper();
        double x = 0.00;
        try {
            x = objectMapper.readValue(jsonstring, Double.class);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    
    public static String jrtToJson(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        String x = "";
        try {   
            x = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            bslog(ex);
        }
        return x;
    }
    
    public static Object[][] dropColumn(Object[][] originalArray, int columnIndexToDrop) {
        if (originalArray == null || originalArray.length == 0 || originalArray[0].length <= columnIndexToDrop || columnIndexToDrop < 0) {
            // Handle invalid input or empty array
            return null;
        }

        int numRows = originalArray.length;
        int numColsOriginal = originalArray[0].length;
        int numColsNew = numColsOriginal - 1;

        Object[][] newArray = new Object[numRows][numColsNew];

        for (int i = 0; i < numRows; i++) {
            int newColIndex = 0; // Index for the new array's columns
            for (int j = 0; j < numColsOriginal; j++) {
                if (j != columnIndexToDrop) {
                    newArray[i][newColIndex] = originalArray[i][j];
                    newColIndex++;
                }
            }
        }
        return newArray;
    }
    
    public static class FileDragHandler extends TransferHandler {

    private final List<File> filesToDrag;

    public FileDragHandler(List<File> files) {
        this.filesToDrag = files;
    }

    @Override
    public int getSourceActions(JComponent c) {
        // Defines the allowed actions (COPY, MOVE, or both)
        return TransferHandler.COPY; // Use COPY for dragging to external apps
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        // Wraps the list of files in a Transferable object
        return new FileListTransferable(filesToDrag);
    }

    // Custom Transferable implementation for File lists
    public static class FileListTransferable implements Transferable {
        private final List<File> files;

        public FileListTransferable(List<File> files) {
            this.files = files;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            // Only support javaFileListFlavor for native file explorer compatibility
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.javaFileListFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return files; // Return the list of files
            }
            throw new UnsupportedFlavorException(flavor);
        }
        
    }
}
    
    public void dndFile() {
    JFrame frame = new JFrame("Drag File Demo");
        JPanel panel = new JPanel();
        JLabel dragSourceLabel = new JLabel("Drag this file out");
        
        // The file to be dragged (ensure it exists for testing)
        File fileToDrag = new File("C:/temp/methods.txt"); // Replace with a valid file path
        List<File> files = Arrays.asList(fileToDrag);

        // Set the custom TransferHandler on the component
        dragSourceLabel.setTransferHandler(new BlueSeerUtils.FileDragHandler(files));
        
        // The JLabel doesn't have setDragEnabled(), so we use a MouseListener to start the drag
        dragSourceLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.COPY);
            }
        });

        panel.add(dragSourceLabel);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void dndFile2(Path filepath) {
        JDialog mydialog = new JDialog();
        JLabel dragSourceLabel;
        if (filepath == null) {
            dragSourceLabel = new JLabel("No Attachment File was generated!");
        } else {
            dragSourceLabel = new JLabel("");
            dragSourceLabel.setIcon(clickfile);
            List<File> files = Arrays.asList(filepath.toFile());
            dragSourceLabel.setTransferHandler(new BlueSeerUtils.FileDragHandler(files));
            dragSourceLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    JComponent comp = (JComponent) e.getSource();
                    TransferHandler handler = comp.getTransferHandler();
                    handler.exportAsDrag(comp, e, TransferHandler.COPY);
                }
            });
        }
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add( dragSourceLabel, gbc );
        
        mydialog.add(panel);
        
        mydialog.pack();
        mydialog.setLocationRelativeTo( null );
        mydialog.setResizable(false);
        mydialog.setVisible(true);
        mydialog.setTitle("Drag and Drop File Attachment:");
        mydialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        mydialog.setSize(new Dimension(300, 300));
    }
    
}



